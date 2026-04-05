/**
 * Публичный поиск: GET /api/public/certificates/search?diplomaNumber=…
 * В OpenAPI только номер диплома, не ФИО.
 */

/**
 * @param {string} raw
 * @returns {{ ok: true, query: string } | { ok: false, message: string }}
 */
export function validatePublicSearchDiplomaNumberInput(raw) {
  const q = String(raw || '').trim()
  if (!q) return { ok: false, message: 'Введите номер диплома.' }

  const parts = q.split(/\s+/).filter(Boolean)
  const hasDigit = /\d/.test(q)
  const hasDelim = /[-–—_/]/.test(q)

  // Несколько слов без цифр и без типичных разделителей номера — почти наверняка ФИО, а не diplomaNumber
  if (parts.length >= 2 && !hasDigit && !hasDelim) {
    return {
      ok: false,
      message:
        'Этот запрос уходит в API как diplomaNumber (номер диплома), например DIP-2026-001. Поиск по ФИО этим методом не поддерживается. Свои дипломы смотрите в разделе «Мои дипломы».',
    }
  }

  return { ok: true, query: q }
}

/**
 * Бэкенд иногда отдаёт 500 с detail "Certificate not found" вместо 404.
 * @param {Error & { status?: number, detail?: string, responseBody?: object }} err
 */
export function isCertificateNotFoundError(err) {
  if (!err || typeof err !== 'object') return false
  if (err.status === 404) return true
  const d = String(err.detail || err.responseBody?.detail || '').toLowerCase()
  if (err.status === 500 && (d.includes('not found') || d.includes('не найден'))) return true
  return false
}
