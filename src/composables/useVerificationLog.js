/**
 * useVerificationLog — журнал проверок дипломов
 * Реальный API бэкенда ДиплоРеестр
 */
import { ref, computed } from 'vue'
import { api } from '../api/api.js'
import { useAuth } from './useAuth.js'

const logEntries = ref([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const error = ref(null)

export function useVerificationLog() {
  const { accessToken } = useAuth()

  const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

  /**
   * Получить журнал проверок с фильтрацией по датам
   * @param {object} filters — { dateFrom?: string, dateTo?: string }
   */
  async function fetchLog(filters = {}) {
    loading.value = true
    error.value = null
    try {
      const params = {
        page: page.value,
        pageSize: pageSize.value,
      }
      if (filters.dateFrom) params.dateFrom = filters.dateFrom
      if (filters.dateTo) params.dateTo = filters.dateTo

      const res = await api.getVerificationLog(params, accessToken.value)
      logEntries.value = res.data || []
      total.value = res.total || 0
      return res
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  function setPage(p) {
    page.value = p
  }

  function setPageSize(s) {
    pageSize.value = s
    page.value = 1
  }

  function reset() {
    logEntries.value = []
    total.value = 0
    page.value = 1
    loading.value = false
    error.value = null
  }

  return {
    logEntries,
    loading,
    total,
    page,
    pageSize,
    totalPages,
    error,
    fetchLog,
    setPage,
    setPageSize,
    reset,
  }
}
