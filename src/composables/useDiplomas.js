/**
 * useDiplomas — выпуск через POST /api/certificates + локальный список
 */
import { ref, computed } from 'vue'
import { api } from '../api/api.js'
import { useAuth } from './useAuth.js'
import {
  loadAllCertificates,
  appendCertificate,
  patchCertificate,
} from '../utils/certificatesStore.js'

const loading = ref(false)
const error = ref(null)
const page = ref(1)
const pageSize = ref(10)
const searchQuery = ref('')
const statusFilter = ref(null)
const allRows = ref([])
const sortField = ref('createdAt')
const sortDir = ref('desc')

function trim(s) {
  return String(s ?? '').trim()
}

const UUID_RE =
  /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i

function normalizeIssueRequest(data) {
  const universityCode = trim(data.universityCode)
  const studentId = trim(data.studentId)
  const diplomaNumber = trim(data.diplomaNumber)
  const fullName = trim(data.fullName)
  const specialty = trim(data.specialty)
  const graduationYear = Number(data.graduationYear)

  if (!universityCode) throw new Error('Укажите код университета')
  if (!studentId) throw new Error('Укажите UUID студента')
  if (!UUID_RE.test(studentId)) {
    throw new Error('Поле «UUID студента» должно быть в формате UUID (как в ответе регистрации студента)')
  }
  if (!diplomaNumber) throw new Error('Укажите номер диплома')
  if (!fullName) throw new Error('Укажите ФИО')
  if (!specialty) throw new Error('Укажите специальность')
  if (!Number.isFinite(graduationYear) || graduationYear < 1950 || graduationYear > 2100) {
    throw new Error('Укажите корректный год выпуска (1950–2100)')
  }

  return {
    universityCode,
    studentId,
    diplomaNumber,
    fullName,
    specialty,
    graduationYear,
  }
}

function recordFromIssue(req, res) {
  return {
    id: res.certificateId,
    serialNumber: res.diplomaNumber,
    studentName: req.fullName,
    specialty: req.specialty,
    issueDate: res.issuedAt,
    status: 'active',
    hash: res.payloadHash,
    signature: res.signature,
    studentId: req.studentId,
    universityCode: req.universityCode,
    graduationYear: req.graduationYear,
  }
}

export function useDiplomas() {
  const { accessToken } = useAuth()

  function reloadFromStorage() {
    allRows.value = loadAllCertificates()
  }

  const filtered = computed(() => {
    let rows = [...allRows.value]
    const q = searchQuery.value.trim().toLowerCase()
    if (q) {
      rows = rows.filter(
        (r) =>
          String(r.studentName || '')
            .toLowerCase()
            .includes(q) ||
          String(r.serialNumber || '')
            .toLowerCase()
            .includes(q) ||
          String(r.specialty || '')
            .toLowerCase()
            .includes(q)
      )
    }
    if (statusFilter.value) {
      rows = rows.filter((r) => r.status === statusFilter.value)
    }
    return rows
  })

  const total = computed(() => filtered.value.length)

  const diplomas = computed(() => {
    const start = (page.value - 1) * pageSize.value
    return filtered.value.slice(start, start + pageSize.value)
  })

  const totalPages = computed(() =>
    Math.max(1, Math.ceil(total.value / pageSize.value))
  )

  async function fetchDiplomas() {
    loading.value = true
    error.value = null
    try {
      reloadFromStorage()
      return { data: diplomas.value, total: total.value }
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function fetchDiplomaById(id) {
    reloadFromStorage()
    return allRows.value.find((r) => r.id === id) || null
  }

  async function createDiploma(data) {
    loading.value = true
    error.value = null
    try {
      const req = normalizeIssueRequest(data)
      const res = await api.issueCertificate(req, accessToken.value)
      const rec = recordFromIssue(req, res)
      appendCertificate(rec)
      reloadFromStorage()
      return res
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function updateDiploma() {
    throw new Error('Редактирование не поддерживается API')
  }

  async function revokeDiploma(id) {
    patchCertificate(id, { status: 'revoked' })
    reloadFromStorage()
  }

  function setSearch(val) {
    searchQuery.value = val
    page.value = 1
  }

  function setStatusFilter(val) {
    statusFilter.value = val || null
    page.value = 1
  }

  function setSort() {}

  function setPage(p) {
    page.value = p
  }

  function setPageSize(s) {
    pageSize.value = s
    page.value = 1
  }

  function reset() {
    allRows.value = []
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
    reloadFromStorage,
  }
}
