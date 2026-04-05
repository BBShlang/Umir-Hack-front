/**
 * useQR — генерация QR-кодов для дипломов через реальный API
 * POST /api/qr/certificates/{certificateId}
 */
import { ref } from 'vue'
import { api } from '../api/api.js'
import { useAuth } from './useAuth.js'

export function useQR() {
  const { accessToken } = useAuth()

  const loading = ref(false)
  const error = ref(null)
  const lastQrResult = ref(null)

  /**
   * Сгенерировать QR-код для диплома
   * @param {string} certificateId — UUID диплома
   * @returns {Promise<{qrId, certificateId, token, qrContent, expiresAt}>}
   */
  async function generateCertificateQr(certificateId) {
    loading.value = true
    error.value = null
    lastQrResult.value = null
    try {
      const res = await api.generateCertificateQr(certificateId, accessToken.value)
      lastQrResult.value = res
      return res
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Построить полную URL для публичной верификации
   * @param {string} certificateId
   * @param {string} token
   * @returns {string}
   */
  function buildVerifyUrl(certificateId, token) {
    const base = window.location.origin
    return `${base}/verify?certificateId=${encodeURIComponent(certificateId)}&token=${encodeURIComponent(token)}`
  }

  /**
   * Получить оставшееся время для QR-токена
   * @param {string} expiresAt — ISO дата
   * @returns {string}
   */
  function getTimeLeft(expiresAt) {
    if (!expiresAt) return ''
    const left = new Date(expiresAt) - new Date()
    if (left <= 0) return 'Истекло'

    const days = Math.floor(left / (1000 * 60 * 60 * 24))
    const hours = Math.floor((left % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
    const minutes = Math.floor((left % (1000 * 60 * 60)) / (1000 * 60))

    if (days > 0) return `${days} дн. ${hours} ч.`
    if (hours > 0) return `${hours} ч. ${minutes} мин.`
    return `${minutes} мин.`
  }

  function reset() {
    lastQrResult.value = null
    loading.value = false
    error.value = null
  }

  return {
    loading,
    error,
    lastQrResult,
    generateCertificateQr,
    buildVerifyUrl,
    getTimeLeft,
    reset,
  }
}
