/**
 * useApiKeys — управление API ключами (HR роль)
 * Реальный API бэкенда ДиплоРеестр
 */
import { ref } from 'vue'
import { api } from '../api/api.js'
import { useAuth } from './useAuth.js'

const apiKeyData = ref(null)
const loading = ref(false)
const error = ref(null)

export function useApiKeys() {
  const { accessToken } = useAuth()

  /**
   * Создать новый API ключ (старый автоматически отзывается)
   * @returns {Promise<{key, prefix, createdAt, warning}>}
   */
  async function createApiKey() {
    loading.value = true
    error.value = null
    try {
      const res = await api.createApiKey(accessToken.value)
      apiKeyData.value = res
      return res
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Отозвать API ключ
   */
  async function deleteApiKey() {
    loading.value = true
    error.value = null
    try {
      await api.deleteApiKey(accessToken.value)
      apiKeyData.value = null
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Получить статистику использования API ключа
   * @returns {Promise<object>}
   */
  async function getUsage() {
    loading.value = true
    error.value = null
    try {
      const res = await api.getApiKeyUsage(accessToken.value)
      apiKeyData.value = res
      return res
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  function reset() {
    apiKeyData.value = null
    loading.value = false
    error.value = null
  }

  return {
    apiKeyData,
    loading,
    error,
    createApiKey,
    deleteApiKey,
    getUsage,
    reset,
  }
}
