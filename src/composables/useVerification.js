/**
 * useVerification — проверка хэша/номера, статус, rate-limit mock
 */
import { ref } from 'vue'

const lastChecks = ref([])
const RATE_LIMIT_WINDOW = 60_000 // 1 минута
const RATE_LIMIT_MAX = 10 // макс. запросов за окно

export function useVerification() {
  const verifying = ref(false)
  const verificationResult = ref(null)
  const verificationError = ref(null)

  /**
   * Проверить диплом по серийному номеру
   * @param {string} serialNumber
   * @returns {Promise<object>}
   */
  async function verifyBySerial(serialNumber) {
    if (!serialNumber?.trim()) {
      throw new Error('Введите серийный номер')
    }

    checkRateLimit()

    verifying.value = true
    verificationError.value = null
    verificationResult.value = null

    try {
      // TODO: заменить на GET /api/verify?serial=...
      await new Promise(r => setTimeout(r, 800))

      const result = mockVerify(serialNumber.trim())
      verificationResult.value = result
      recordCheck()

      return result
    } catch (err) {
      verificationError.value = err.message
      throw err
    } finally {
      verifying.value = false
    }
  }

  /**
   * Проверить диплом по хешу
   * @param {string} hash
   * @returns {Promise<object>}
   */
  async function verifyByHash(hash) {
    if (!hash?.trim()) {
      throw new Error('Введите хеш')
    }

    checkRateLimit()

    verifying.value = true
    verificationError.value = null

    try {
      // TODO: заменить на GET /api/verify?hash=...
      await new Promise(r => setTimeout(r, 800))

      const result = mockVerifyByHash(hash.trim())
      verificationResult.value = result
      recordCheck()

      return result
    } catch (err) {
      verificationError.value = err.message
      throw err
    } finally {
      verifying.value = false
    }
  }

  /**
   * Проверить по QR-токену
   * @param {string} token
   * @returns {Promise<object>}
   */
  async function verifyByQR(token) {
    checkRateLimit()

    verifying.value = true
    verificationError.value = null

    try {
      await new Promise(r => setTimeout(r, 600))

      const result = mockVerifyByQR(token)
      verificationResult.value = result
      recordCheck()

      return result
    } catch (err) {
      verificationError.value = err.message
      throw err
    } finally {
      verifying.value = false
    }
  }

  function checkRateLimit() {
    const now = Date.now()
    const recent = lastChecks.value.filter(t => now - t < RATE_LIMIT_WINDOW)

    if (recent.length >= RATE_LIMIT_MAX) {
      throw new Error('Слишком много запросов. Подождите минуту.')
    }
  }

  function recordCheck() {
    lastChecks.value.push(Date.now())
    // Очистка старых записей
    const now = Date.now()
    lastChecks.value = lastChecks.value.filter(t => now - t < RATE_LIMIT_WINDOW)
  }

  function reset() {
    verifying.value = false
    verificationResult.value = null
    verificationError.value = null
  }

  return {
    verifying,
    verificationResult,
    verificationError,
    verifyBySerial,
    verifyByHash,
    verifyByQR,
    reset
  }
}

// ---- Mock-функции ----

function mockVerify(serial) {
  // Эмуляция: каждые 5-й номер — невалиден
  const isValid = !serial.endsWith('0') && !serial.endsWith('5')

  if (isValid) {
    return {
      isValid: true,
      status: 'active',
      serialNumber: serial,
      university: 'МГТУ им. Баумана',
      specialty: 'Информатика и вычислительная техника',
      degree: 'Бакалавр',
      issueDate: '15.06.2025',
      hash: 'a1b2c3d4e5f6' + '0'.repeat(52)
    }
  }

  return {
    isValid: false,
    status: 'error',
    serialNumber: serial,
    reason: 'Диплом не найден в реестре'
  }
}

function mockVerifyByHash(hash) {
  const found = hash.length >= 64
  return {
    isValid: found,
    status: found ? 'active' : 'error',
    hash,
    university: found ? 'МГУ' : null,
    reason: found ? '' : 'Хеш не найден'
  }
}

function mockVerifyByQR(token) {
  const valid = token && token.length > 10
  return {
    isValid: valid,
    status: valid ? 'active' : 'expired',
    reason: valid ? '' : 'QR-ссылка недействительна или истекла'
  }
}
