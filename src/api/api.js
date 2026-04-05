const rawApiBase = (import.meta.env.VITE_API_BASE_URL || '').replace(/\/$/, '')
/** Явно включить прокси Vite в dev (иначе при заданном URL запросы идут в бэкенд напрямую — меньше 502/socket hang up) */
const useViteProxyInDev =
  import.meta.env.DEV &&
  import.meta.env.VITE_DEV_PROXY === 'true' &&
  !!rawApiBase

let BASE_URL = rawApiBase
if (import.meta.env.DEV) {
  BASE_URL = useViteProxyInDev ? '' : rawApiBase
}

if (!BASE_URL && import.meta.env.PROD) {
  console.warn(
    '[api] VITE_API_BASE_URL не задан — в production запросы пойдут на тот же origin (нужен прокси или env).'
  )
}

function parseBody(text) {
  if (text == null || !String(text).trim()) return {}
  const t = String(text).trim()
  if (t.startsWith('{') || t.startsWith('[')) {
    try {
      return JSON.parse(text)
    } catch {
      return { detail: t.slice(0, 600) }
    }
  }
  return { detail: t.slice(0, 600) }
}

function formatValidationErrors(errors) {
  if (!errors || typeof errors !== 'object') return ''
  return Object.entries(errors)
    .map(([k, v]) => `${k}: ${Array.isArray(v) ? v.join(', ') : v}`)
    .join('; ')
}

function pickErrorMessage(payload, status) {
  if (status === 502 || status === 504) {
    return (
      (payload && typeof payload === 'object' && (payload.detail || payload.message)) ||
      'Сервер недоступен (502). Проверьте бэкенд, VPN/Tailscale или отключите прокси: не задавайте VITE_DEV_PROXY, запросы пойдут напрямую на VITE_API_BASE_URL.'
    )
  }
  if (status >= 500) {
    const parts = [
      payload.detail,
      payload.message,
      payload.title,
      formatValidationErrors(payload.errors),
    ].filter(Boolean)
    const msg = parts.join('. ').trim()
    return (
      msg ||
      `Ошибка сервера (${status}). Для публичного поиска диплома это обычно баг на бэкенде (не пойманное исключение) — смотрите логи Spring; при отсутствии диплома ожидается 404, а не 500.`
    )
  }
  if (!payload || typeof payload !== 'object') return `HTTP ${status}`
  const val = formatValidationErrors(payload.errors)
  const base =
    payload.detail ||
    payload.message ||
    payload.title ||
    payload.error ||
    `HTTP ${status}`
  return val ? `${base} (${val})` : base
}
function splitDiplomaNumber(diplomaNumber) {
  if (!diplomaNumber || typeof diplomaNumber !== 'string') {
    return { series: null, number: null }
  }

  const parts = diplomaNumber.split('-')
  if (parts.length >= 2) {
    return {
      series: parts.slice(0, -1).join('-') || null,
      number: parts[parts.length - 1] || diplomaNumber,
    }
  }

  return { series: null, number: diplomaNumber }
}

function normalizeCertificate(item) {
  if (!item || typeof item !== 'object') return item

  const { series, number } = splitDiplomaNumber(item.diplomaNumber)

  return {
    // Оригинальные поля бэкенда сохраняем
    ...item,

    // Поля, которые уже ждёт фронт
    id: item.certificateId ?? null,
    certificateId: item.certificateId ?? null,

    number: item.number ?? number ?? item.diplomaNumber ?? null,
    series: item.series ?? series ?? null,

    issueDate: item.issueDate ?? item.issuedAt ?? null,
    hash: item.hash ?? item.payloadHash ?? item.certificateId ?? null,

    isVerified:
      typeof item.isVerified === 'boolean'
        ? item.isVerified
        : item.status === 'ACTIVE',
  }
}

function normalizeCertificates(items) {
  return Array.isArray(items) ? items.map(normalizeCertificate) : []
}

/**
 * Универсальный запрос JSON
 * — Content-Type только при теле (GET без лишнего заголовка)
 * — Сетевые сбои fetch → понятная ошибка
 */
async function request(method, path, { body, token, params, baseUrl } = {}) {
  const headers = { Accept: 'application/json, application/problem+json' }
  if (token) headers.Authorization = `Bearer ${token}`
  if (body !== undefined) headers['Content-Type'] = 'application/json'

  // Если path — уже полный URL, не добавляем BASE_URL
  const isAbsolute = path.startsWith('http://') || path.startsWith('https://')
  let url = isAbsolute ? path : `${BASE_URL}${path}`
  if (params) {
    const query = new URLSearchParams()
    Object.entries(params).forEach(([k, v]) => {
      if (v !== undefined && v !== null && v !== '') query.append(k, String(v))
    })
    const qs = query.toString()
    if (qs) url += `?${qs}`
  }

  let res
  try {
    res = await fetch(url, {
      method,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    })
  } catch (e) {
    const isFetch =
      e instanceof TypeError &&
      (String(e.message).includes('fetch') || String(e.message).includes('Failed to fetch'))
    const msg = isFetch
      ? 'Нет связи с сервером: проверьте VITE_API_BASE_URL, VPN/Tailscale и CORS на бэкенде для origin фронтенда.'
      : e.message || 'Ошибка сети'
    const err = new Error(msg)
    err.cause = e
    throw err
  }

  const text = await res.text()

  if (!res.ok) {
    const payload = parseBody(text)
    const err = new Error(pickErrorMessage(payload, res.status))
    err.status = res.status
    err.errors = payload.errors || null
    err.detail = payload.detail || payload.message || null
    err.responseBody = payload
    /** Сырой ответ (для отладки 500 в консоли / Sentry) */
    err.rawResponse = typeof text === 'string' ? text.slice(0, 4000) : ''
    if (import.meta.env.DEV && res.status >= 500) {
      console.warn('[api]', res.status, url, err.rawResponse?.slice(0, 800) || '(пустое тело)')
    }
    throw err
  }

  if (res.status === 204) return null
  if (!text || !String(text).trim()) return null

  const t = String(text).trim()
  if (t.startsWith('{') || t.startsWith('[')) {
    try {
      return JSON.parse(text)
    } catch {
      return null
    }
  }

  return null
}

async function uploadFileDisabled() {
  throw new Error(
    'Загрузка файлов на сервер не описана в OpenAPI бэкенда. Используйте CSV и выпуск через POST /api/certificates.'
  )
}

export const api = {
    // ===================== DIPLOMA CLAIMS =====================

  /** POST /api/student/diploma-claims — заявка студента на привязку диплома */
  createStudentDiplomaClaim: (data, token) =>
    request('POST', '/api/student/diploma-claims', { body: data, token }),

  /** GET /api/university/diploma-claims/pending — pending заявки для вуза */
  getUniversityPendingDiplomaClaims: (token) =>
    request('GET', '/api/university/diploma-claims/pending', { token }),

  /** POST /api/university/diploma-claims/{requestId}/review — одобрить/отклонить */
  reviewUniversityDiplomaClaim: (requestId, data, token) =>
    request(
      'POST',
      `/api/university/diploma-claims/${encodeURIComponent(requestId)}/review`,
      { body: data, token }
    ),
  // ===================== AUTH =====================

  login: (data) => request('POST', '/api/auth/login', { body: data }),

  registerUniversity: (data) =>
    request('POST', '/api/auth/register/university', { body: data }),

  registerStudent: (data) =>
    request('POST', '/api/auth/register/student', { body: data }),

  registerEmployer: (data) =>
    request('POST', '/api/auth/register/employer', { body: data }),

  // ===================== PUBLIC / EMPLOYER SEARCH =====================

  /** GET /api/public/certificates/search?diplomaNumber= (без авторизации) */
  publicCertificatesSearch: (params) =>
    request('GET', '/api/public/certificates/search', { params }),

  /** GET /api/employer/certificates/search?diplomaNumber= (Bearer EMPLOYER) */
  employerCertificatesSearch: (params, token) =>
    request('GET', '/api/employer/certificates/search', { params, token }),

  // ===================== CERTIFICATES =====================

  issueCertificate: (data, token) =>
    request('POST', '/api/certificates', { body: data, token }),

/** GET /api/certificates/my — список дипломов студента (массив) */
getMyCertificates: async (token) => {
  const data = await request('GET', '/api/certificates/my', { token })
  return normalizeCertificates(data)
},
/** GET /api/certificates/my/{certificateId} */
getMyCertificateById: async (certificateId, token) => {
  const data = await request(
    'GET',
    `/api/certificates/my/${encodeURIComponent(certificateId)}`,
    { token }
  )
  return normalizeCertificate(data)
},
  // ===================== QR =====================

  generateCertificateQr: (certificateId, token) =>
    request('POST', `/api/qr/certificates/${encodeURIComponent(certificateId)}`, { token }),

  // ===================== PUBLIC VERIFY BY QR =====================

  /** POST /api/v1/verifications/qr — верификация по QR (порт 8082, без авторизации) */
  verifyByQr: async (data) => {
    const VERIFY_BASE = (import.meta.env.VITE_VERIFY_API_BASE_URL || 'http://26.86.179.119:8082').replace(/\/$/, '')

    const headers = {
      Accept: 'application/json, application/problem+json',
      'Content-Type': 'application/json',
    }

    const url = `${VERIFY_BASE}/api/v1/verifications/qr`

    let res
    try {
      res = await fetch(url, {
        method: 'POST',
        headers,
        body: JSON.stringify(data),
      })
    } catch (e) {
      const msg = `Нет связи с сервером верификации (${VERIFY_BASE}): проверьте VPN/Tailscale и CORS на бэкенде.`
      const err = new Error(msg)
      err.cause = e
      throw err
    }

    const text = await res.text()
    if (!res.ok) {
      const payload = parseBody(text)
      const err = new Error(pickErrorMessage(payload, res.status))
      err.status = res.status
      err.detail = payload.detail || payload.message || null
      throw err
    }

    if (res.status === 204) return null
    if (!text || !String(text).trim()) return null
    const t = String(text).trim()
    if (t.startsWith('{') || t.startsWith('[')) {
      try {
        return JSON.parse(text)
      } catch {
        return null
      }
    }
    return null
  },

  // ===================== ПУБЛИЧНАЯ ВЕРИФИКАЦИЯ =====================

  /**
   * Публичная верификация — работает БЕЗ токена.
   * Параметры: certificateId, token (query).
   */
  publicVerifyQuery: (params) =>
    request('GET', '/api/public/verify', { params }),

  // ===================== Совместимость / заглушки =====================

  register: () => {
    throw new Error('Используйте registerUniversity / registerStudent / registerEmployer')
  },

  refresh: () => {
    throw new Error('Бэкенд не выдаёт refresh_token; войдите снова')
  },

  logout: async () => null,

  /** У ВУЗа в OpenAPI нет списка всех дипломов — только локальный кэш на фронте */
  getDiplomas: async () => ({ data: [], total: 0 }),

  getDiploma: (id, token) => api.getMyCertificateById(id, token),

  createDiploma: (data, token) => api.issueCertificate(data, token),

  updateDiploma: async () => {
    throw new Error('PATCH диплома не поддерживается API')
  },

  deleteDiploma: async () => {
    throw new Error('DELETE диплома не поддерживается API')
  },

  verify: async () => {
    throw new Error('Используйте поиск по номеру диплома')
  },

  verifyBulk: async () => {
    throw new Error('Массовая проверка не описана в OpenAPI')
  },

  verifyShare: (token, certificateId) =>
    api.publicVerifyQuery({ token, certificateId }),

  publicVerify: (token, certificateId) =>
    api.publicVerifyQuery({ token, certificateId }),

  uploadFile: () => uploadFileDisabled(),

  getUploads: async () => [],

  createApiKey: async () => ({}),
  deleteApiKey: async () => null,
  getApiKeyUsage: async () => ({}),

  getSettings: async () => ({}),
  updateSettings: async () => ({}),

  getStats: async () => ({}),

  createShareToken: async () => {
    throw new Error('Share-токены не описаны в OpenAPI. Используйте POST /api/qr/certificates/{id}')
  },
  getShareTokens: async () => [],
  deleteShareToken: async () => null,

  getVerificationLog: async () => ({ data: [], total: 0 }),
}
