/**
 * useStats — статистика (роль-зависимая)
 * Реальный API бэкенда ДиплоРеестр
 */
import { ref } from 'vue'
import { api } from '../api/api.js'
import { useAuth } from './useAuth.js'

const stats = ref(null)
const loading = ref(false)
const error = ref(null)

export function useStats() {
  const { accessToken } = useAuth()

  /**
   * Получить статистику для текущего пользователя
   * UNIVERSITY: { universityId, universityName, totalDiplomas, activeDiplomas, revokedDiplomas, totalUploadBatches }
   * STUDENT: { userId, studentName, studentEmail, totalVerifications, personalVerifications }
   * HR: { userId, hrName, totalVerifications, personalVerifications }
   */
  async function fetchStats() {
    loading.value = true
    error.value = null
    try {
      const res = await api.getStats(accessToken.value)
      stats.value = res
      return res
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  function reset() {
    stats.value = null
    loading.value = false
    error.value = null
  }

  return {
    stats,
    loading,
    error,
    fetchStats,
    reset,
  }
}
