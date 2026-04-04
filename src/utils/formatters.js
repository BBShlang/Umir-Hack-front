/**
 * formatters.js — утилиты форматирования
 */

/**
 * Форматировать дату в читаемый вид
 * @param {string|Date} date
 * @param {object} [options]
 * @returns {string}
 */
export function formatDate(date, options = {}) {
  if (!date) return '—'
  const d = typeof date === 'string' ? new Date(date) : date
  const defaults = { year: 'numeric', month: 'long', day: 'numeric' }
  return d.toLocaleDateString('ru-RU', { ...defaults, ...options })
}

/**
 * Форматировать дату и время
 * @param {string|Date} date
 * @returns {string}
 */
export function formatDateTime(date) {
  if (!date) return '—'
  const d = typeof date === 'string' ? new Date(date) : date
  return d.toLocaleString('ru-RU', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

/**
 * Сократить хеш до читаемого вида
 * @param {string} hash
 * @param {number} [chars=12]
 * @returns {string}
 */
export function truncateHash(hash, chars = 12) {
  if (!hash) return '—'
  if (hash.length <= chars) return hash
  return hash.slice(0, chars) + '…'
}

/**
 * Форматировать число (разделитель тысяч)
 * @param {number} num
 * @returns {string}
 */
export function formatNumber(num) {
  if (num == null) return '—'
  return num.toLocaleString('ru-RU')
}

/**
 * Форматировать размер файла
 * @param {number} bytes
 * @returns {string}
 */
export function formatFileSize(bytes) {
  if (bytes < 1024) return bytes + ' Б'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' КБ'
  return (bytes / (1024 * 1024)).toFixed(1) + ' МБ'
}

/**
 * Склонять слова (день, дня, дней)
 * @param {number} n
 * @param {string[]} forms - ['день', 'дня', 'дней']
 * @returns {string}
 */
export function pluralize(n, forms) {
  const abs = Math.abs(n) % 100
  const lastDigit = abs % 10

  if (abs > 10 && abs < 20) return forms[2]
  if (lastDigit === 1) return forms[0]
  if (lastDigit >= 2 && lastDigit <= 4) return forms[1]
  return forms[2]
}
