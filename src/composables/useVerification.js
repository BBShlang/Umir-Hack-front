/**
 * useVerification — криптопроверка (Swagger) + локальный поиск по номеру диплома
 */
import { ref } from 'vue'
import { api } from '../api/api.js'
import { useAuth } from './useAuth.js'
import { findCertificateByNumber } from '../utils/certificatesStore.js'

const verifying = ref(false)
const verificationResult = ref(null)
const verificationError = ref(null)

export function useVerification() {
  const { accessToken } = useAuth()

  /**
   * Проверка подписи (тестовый endpoint бэкенда)
   */
  async function verifyCrypto({ universityCode, payload, signature }) {
    verifying.value = true
    verificationError.value = null
    verificationResult.value = null
    try {
      const res = await api.cryptoVerify(
        { universityCode, payload, signature },
        accessToken.value
      )
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
   * По номеру диплома — ищем в локально сохранённых выпусках (тот же браузер)
   */
  async function verifyByLocalSerial(serialNumber) {
    verifying.value = true
    verificationError.value = null
    verificationResult.value = null
    try {
      const cert = findCertificateByNumber(serialNumber)
      if (!cert) {
        const err = new Error('Диплом не найден в локальном кэше')
        verificationError.value = err.message
        throw err
      }
      verificationResult.value = {
        local: true,
        certificate: cert,
      }
      return verificationResult.value
    } finally {
      verifying.value = false
    }
  }

  async function verify(params) {
    if (params.serial) return verifyByLocalSerial(params.serial)
    throw new Error('Укажите номер диплома')
  }

  async function verifyBySerial(serialNumber) {
    return verifyByLocalSerial(serialNumber)
  }

  async function verifyByHash() {
    throw new Error('Проверка по хешу не реализована в текущем API')
  }

  async function verifyBulk() {
    throw new Error('Массовая проверка не реализована в текущем API')
  }

  async function verifyByQR(tokenOrUrl) {
    verifying.value = true
    verificationError.value = null
    verificationResult.value = null
    try {
      const t = String(tokenOrUrl || '').trim()
      let certificateId = ''
      let token = ''
      try {
        const u = t.includes('://')
          ? new URL(t)
          : new URL(t.startsWith('/') ? t : `/${t}`, window.location.origin)
        certificateId = u.searchParams.get('certificateId') || ''
        token = u.searchParams.get('token') || ''
      } catch {
        token = t
      }

      const params = {}
      if (certificateId) params.certificateId = certificateId
      if (token) params.token = token

      const res = await api.publicVerifyQuery(params)
      verificationResult.value = res
      return res
    } catch (err) {
      verificationError.value = err.message
      throw err
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
    verifyCrypto,
    verifyByLocalSerial,
    reset,
  }
}
