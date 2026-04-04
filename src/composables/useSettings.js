/**
 * useSettings — настройки университета (ЭЦП, QR TTL)
 * Реальный API бэкенда ДиплоРеестр
 */
import { ref } from 'vue'
import { api } from '../api/api.js'
import { useAuth } from './useAuth.js'

const settings = ref(null)
const loading = ref(false)
const error = ref(null)

export function useSettings() {
  const { accessToken } = useAuth()

  /**
   * Получить настройки университета
   * @returns {Promise<{ecpEnabled: boolean, qrTtlDays: number, apiKey: string|null}>}
   */
  async function fetchSettings() {
    loading.value = true
    error.value = null
    try {
      const res = await api.getSettings(accessToken.value)
      settings.value = res
      return res
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Обновить настройки
   * @param {object} data — { ecpEnabled?: boolean, qrTtlDays?: number }
   */
  async function updateSettings(data) {
    loading.value = true
    error.value = null
    try {
      const res = await api.updateSettings(data, accessToken.value)
      settings.value = res
      return res
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  function reset() {
    settings.value = null
    loading.value = false
    error.value = null
  }

  return {
    settings,
    loading,
    error,
    fetchSettings,
    updateSettings,
    reset,
  }
}
