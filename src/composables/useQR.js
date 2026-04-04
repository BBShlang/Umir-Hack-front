/**
 * useQR — управление share-токенами, генерация QR-ссылок
 * Реальный API бэкенда ДиплоРеестр
 */
import { ref, computed } from 'vue'
import { api } from '../api/api.js'
import { useAuth } from './useAuth.js'

const shareTokens = ref([])
const loading = ref(false)
const error = ref(null)

export function useQR() {
  const { accessToken } = useAuth()

  /**
   * Создать share-токен для диплома
   * @param {number} diplomaId
   * @param {number} ttlDays — срок жизни в днях (1-30, по умолч. 7)
   * @returns {Promise<{token, url, expiresAt}>}
   */
  async function createShareToken(diplomaId, ttlDays = 7) {
    loading.value = true
    error.value = null
    try {
      const res = await api.createShareToken({ diplomaId, ttlDays }, accessToken.value)
      // Обновляем список
      await fetchTokens()
      return res
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Получить список активныхых токенов пользователя
   */
  async function fetchTokens() {
    loading.value = true
    error.value = null
    try {
      const res = await api.getShareTokens(accessToken.value)
      shareTokens.value = res || []
      return shareTokens.value
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Отозвать share-токен
   * @param {string} token
   */
  async function revokeToken(token) {
    loading.value = true
    error.value = null
    try {
      await api.deleteShareToken(token, accessToken.value)
      await fetchTokens()
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Получить оставшееся время для токена
   * @param {string} expiresAt — ISO дата
   * @returns {string}
   */
  function getTimeLeft(expiresAt) {
    if (!expiresAt) return ''
    const left = new Date(expiresAt) - new Date()
    if (left <= 0) return 'Истекло'

    const days = Math.floor(left / (1000 * 60 * 60 * 24))
    const hours = Math.floor((left % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))

    if (days > 0) return `${days} дн. ${hours} ч.`
    return `${hours} ч.`
  }

  /**
   * Построить полную URL для share-токена
   * @param {string} token
   * @returns {string}
   */
  function buildShareUrl(token) {
    const base = window.location.origin
    return `${base}/verify/share/${token}`
  }

  function reset() {
    shareTokens.value = []
    loading.value = false
    error.value = null
  }

  return {
    shareTokens,
    loading,
    error,
    createShareToken,
    fetchTokens,
    revokeToken,
    getTimeLeft,
    buildShareUrl,
    reset,
  }
}
