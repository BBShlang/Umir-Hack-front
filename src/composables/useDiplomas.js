/**
 * useDiplomas — CRUD дипломов, фильтрация, пагинация
 * Реальный API бэкенда ДиплоРеестр
 */
import { ref, computed } from 'vue'
import { api } from '../api/api.js'
import { useAuth } from './useAuth.js'

const diplomas = ref([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const searchQuery = ref('')
const statusFilter = ref(null) // null = all, ACTIVE, REVOKED, PENDING
const sortField = ref('createdAt')
const sortDir = ref('desc')
const error = ref(null)

export function useDiplomas() {
  const { accessToken } = useAuth()

  const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

  /**
   * Получить список дипломов с фильтрацией и пагинацией
   */
  async function fetchDiplomas() {
    loading.value = true
    error.value = null
    try {
      const params = {
        page: page.value,
        pageSize: pageSize.value,
        sort: sortField.value,
        sortDir: sortDir.value,
      }
      if (searchQuery.value) params.search = searchQuery.value
      if (statusFilter.value) params.status = statusFilter.value

      const res = await api.getDiplomas(params, accessToken.value)
      diplomas.value = res.data || []
      total.value = res.total || 0
      return res
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Получить один диплом по ID
   */
  async function fetchDiplomaById(id) {
    loading.value = true
    error.value = null
    try {
      const res = await api.getDiploma(id, accessToken.value)
      return res
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Создать диплом (только UNIVERSITY)
   */
  async function createDiploma(data) {
    loading.value = true
    error.value = null
    try {
      const res = await api.createDiploma(data, accessToken.value)
      // Обновляем список
      await fetchDiplomas()
      return res
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Обновить диплом (только UNIVERSITY)
   */
  async function updateDiploma(id, data) {
    loading.value = true
    error.value = null
    try {
      const res = await api.updateDiploma(id, data, accessToken.value)
      await fetchDiplomas()
      return res
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Отозвать диплом (только UNIVERSITY)
   */
  async function revokeDiploma(id) {
    loading.value = true
    error.value = null
    try {
      await api.deleteDiploma(id, accessToken.value)
      await fetchDiplomas()
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  function setSearch(val) {
    searchQuery.value = val
    page.value = 1
  }

  function setStatusFilter(val) {
    statusFilter.value = val || null
    page.value = 1
  }

  function setSort(field, direction) {
    sortField.value = field
    sortDir.value = direction || 'desc'
    page.value = 1
  }

  function setPage(p) {
    page.value = p
  }

  function setPageSize(s) {
    pageSize.value = s
    page.value = 1
  }

  function reset() {
    diplomas.value = []
    total.value = 0
    page.value = 1
    searchQuery.value = ''
    statusFilter.value = null
    error.value = null
  }

  return {
    diplomas,
    loading,
    total,
    page,
    pageSize,
    totalPages,
    search: searchQuery,
    statusFilter,
    sortField,
    sortDir,
    error,
    fetchDiplomas,
    fetchDiplomaById,
    createDiploma,
    updateDiploma,
    revokeDiploma,
    setSearch,
    setStatusFilter,
    setSort,
    setPage,
    setPageSize,
    reset,
  }
}
