/**
 * useVerification — проверка дипломов по серийному номеру, хешу, QR
 * Реальный API бэкенда ДиплоРеестр
 */
import { ref } from 'vue'
import { api } from '../api/api.js'
import { useAuth } from './useAuth.js'

const verifying = ref(false)
const verificationResult = ref(null)
const verificationError = ref(null)

export function useVerification() {
  const { accessToken } = useAuth()

  /**
   * Проверить диплом по серийному номеру или хешу
   * @param {object} params — { serial?: string, hash?: string }
   * @returns {Promise<object>}
   */
  async function verify(params) {
    if (!params.serial && !params.hash) {
      throw new Error('Укажите серийномер или хеш')
    }

    verifying.value = true
    verificationError.value = null
    verificationResult.value = null

    try {
      const res = await api.verify(params, accessToken.value)
      verificationResult.value = res
      return res
    } catch (err) {
      verificationError.value = err.message
      throw err
    } finally {
      verifying.value = false
    }
  }

  /**
   * Проверить диплом по серийному номеру
   */
  async function verifyBySerial(serialNumber) {
    return verify({ serial: serialNumber })
  }

  /**
   * Проверить диплом по хешу
   */
  async function verifyByHash(hash) {
    return verify({ hash })
  }

  /**
   * Массовая проверка дипломов
   * @param {string[]} serials
   * @returns {Promise<object[]>}
   */
  async function verifyBulk(serials) {
    verifying.value = true
    verificationError.value = null
    verificationResult.value = null

    try {
      const res = await api.verifyBulk(serials, accessToken.value)
      verificationResult.value = res
      return res
    } catch (err) {
      verificationError.value = err.message
      throw err
    } finally {
      verifying.value = false
    }
  }

  /**
   * Проверить по QR-токену (публичная верификация)
   * @param {string} token — share token
   * @returns {Promise<object>}
   */
  async function verifyByQR(token) {
    verifying.value = true
    verificationError.value = null
    verificationResult.value = null

    try {
      // Пробуем сначала публичный endpoint
      const res = await api.publicVerify(token)
      verificationResult.value = res
      return res
    } catch {
      // Если не вышло — через share endpoint
      try {
        const res = await api.verifyShare(token)
        verificationResult.value = res
        return res
      } catch (err2) {
        verificationError.value = err2.message
        throw err2
      }
    } finally {
      verifying.value = false
    }
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
    verify,
    verifyBySerial,
    verifyByHash,
    verifyByQR,
    verifyBulk,
    reset,
  }
}
