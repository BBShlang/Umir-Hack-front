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
      `Ошибка сервера (${status}). Частые причины: неверный код ВУЗа относительно JWT, студент не из вашей БД, сбой подписи на бэкенде. Смотрите логи Spring и тело ответа в Network.`
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

/**
 * Универсальный запрос JSON
 * — Content-Type только при теле (GET без лишнего заголовка)
 * — Сетевые сбои fetch → понятная ошибка
 */
async function request(method, path, { body, token, params } = {}) {
  const headers = { Accept: 'application/json, application/problem+json' }
  if (token) headers.Authorization = `Bearer ${token}`
  if (body !== undefined) headers['Content-Type'] = 'application/json'

  let url = `${BASE_URL}${path}`
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
  // ===================== AUTH =====================

  login: (data) => request('POST', '/api/auth/login', { body: data }),

  registerUniversity: (data) =>
    request('POST', '/api/auth/register/university', { body: data }),

  registerStudent: (data) =>
    request('POST', '/api/auth/register/student', { body: data }),

  registerEmployer: (data) =>
    request('POST', '/api/auth/register/employer', { body: data }),

  // ===================== CERTIFICATES =====================

  issueCertificate: (data, token) =>
    request('POST', '/api/certificates', { body: data, token }),

  /** GET /api/certificates/my — список дипломов студента (массив) */
  getMyCertificates: (token) => request('GET', '/api/certificates/my', { token }),

  /** GET /api/certificates/my/{certificateId} */
  getMyCertificateById: (certificateId, token) =>
    request('GET', `/api/certificates/my/${encodeURIComponent(certificateId)}`, { token }),

  // ===================== QR =====================

  generateCertificateQr: (certificateId, token) =>
    request('POST', `/api/qr/certificates/${encodeURIComponent(certificateId)}`, { token }),

  // ===================== CRYPTO (TEST) =====================

  cryptoVerify: (params, token) =>
    request('GET', '/api/test/crypto/verify', { params, token }),

  cryptoSign: (params, token) =>
    request('GET', '/api/test/crypto/sign', { params, token }),

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
    throw new Error('Используйте cryptoVerify или локальный поиск по номеру')
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
