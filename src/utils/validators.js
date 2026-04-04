/**
 * validators.js — схемы валидации форм
 */

/**
 * Проверить email
 * @param {string} email
 * @returns {boolean}
 */
export function isValidEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
}

/**
 * Проверить пароль (минимум 6 символов)
 * @param {string} password
 * @returns {{ valid: boolean, error: string|null }}
 */
export function validatePassword(password) {
  if (!password) return { valid: false, error: 'Пароль обязателен' }
  if (password.length < 6) return { valid: false, error: 'Минимум 6 символов' }
  return { valid: true, error: null }
}

/**
 * Проверить серийный номер диплома
 * @param {string} serial
 * @returns {{ valid: boolean, error: string|null }}
 */
export function validateSerialNumber(serial) {
  if (!serial?.trim()) return { valid: false, error: 'Серийный номер обязателен' }
  if (!/^[A-Z0-9а-яА-ЯёЁ\-]{6,30}$/i.test(serial.trim())) {
    return { valid: false, error: 'Неверный формат (буквы, цифры, дефис; 6-30 символов)' }
  }
  return { valid: true, error: null }
}

/**
 * Проверить хеш SHA-256
 * @param {string} hash
 * @returns {{ valid: boolean, error: string|null }}
 */
export function validateHash(hash) {
  if (!hash?.trim()) return { valid: false, error: 'Хеш обязателен' }
  if (!/^[a-fA-F0-9]{64}$/.test(hash.trim())) {
    return { valid: false, error: 'Хеш должен быть 64-символьной hex-строкой' }
  }
  return { valid: true, error: null }
}

/**
 * Проверить допустимый тип файла
 * @param {File} file
 * @param {string[]} allowedExtensions - ['.csv', '.xlsx']
 * @returns {{ valid: boolean, error: string|null }}
 */
export function validateFileType(file, allowedExtensions) {
  if (!file) return { valid: false, error: 'Файл не выбран' }

  const ext = '.' + file.name.split('.').pop().toLowerCase()
  if (!allowedExtensions.includes(ext)) {
    return { valid: false, error: `Допустимые форматы: ${allowedExtensions.join(', ')}` }
  }

  return { valid: true, error: null }
}

/**
 * Проверить размер файла (максимум)
 * @param {File} file
 * @param {number} maxBytes - максимальный размер в байтах
 * @returns {{ valid: boolean, error: string|null }}
 */
export function validateFileSize(file, maxBytes) {
  if (!file) return { valid: false, error: 'Файл не выбран' }
  if (file.size > maxBytes) {
    const maxMB = (maxBytes / (1024 * 1024)).toFixed(1)
    return { valid: false, error: `Файл слишком большой. Максимум: ${maxMB} МБ` }
  }
  return { valid: true, error: null }
}

/**
 * Валидация формы регистрации
 * @param {object} data
 * @returns {{ valid: boolean, errors: object }}
 */
export function validateRegisterForm(data) {
  const errors = {}

  if (!data.name?.trim()) errors.name = 'Имя обязательно'
  if (!data.email?.trim()) {
    errors.email = 'Email обязателен'
  } else if (!isValidEmail(data.email)) {
    errors.email = 'Неверный формат email'
  }

  const pw = validatePassword(data.password)
  if (!pw.valid) errors.password = pw.error

  if (!data.role) errors.role = 'Выберите роль'

  return {
    valid: Object.keys(errors).length === 0,
    errors
  }
}
