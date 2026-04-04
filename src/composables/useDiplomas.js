/**
 * useDiplomas — CRUD дипломов, фильтрация, пагинация
 */
import { ref, computed } from 'vue'

const diplomas = ref([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const search = ref('')
const statusFilter = ref('all')

export function useDiplomas() {
  const filteredDiplomas = computed(() => {
    let items = diplomas.value

    if (search.value) {
      const q = search.value.toLowerCase()
      items = items.filter(d =>
        d.serialNumber?.toLowerCase().includes(q) ||
        d.studentName?.toLowerCase().includes(q) ||
        d.specialty?.toLowerCase().includes(q)
      )
    }

    if (statusFilter.value !== 'all') {
      items = items.filter(d => d.status === statusFilter.value)
    }

    return items
  })

  const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

  const paginatedDiplomas = computed(() => {
    const start = (page.value - 1) * pageSize.value
    return filteredDiplomas.value.slice(start, start + pageSize.value)
  })

  async function fetchDiplomas() {
    loading.value = true
    // TODO: заменить на реальный API-вызов GET /api/diplomas
    await new Promise(r => setTimeout(r, 600))

    diplomas.value = generateMockDiplomas(25)
    total.value = diplomas.value.length
    loading.value = false
  }

  async function fetchDiplomaById(id) {
    // TODO: GET /api/diplomas/:id
    return diplomas.value.find(d => d.id === Number(id)) || null
  }

  async function createDiploma(data) {
    // TODO: POST /api/diplomas
    const newDiploma = {
      id: Date.now(),
      ...data,
      status: 'active',
      createdAt: new Date().toISOString()
    }
    diplomas.value.unshift(newDiploma)
    total.value++
    return newDiploma
  }

  async function updateDiploma(id, data) {
    // TODO: PATCH /api/diplomas/:id
    const idx = diplomas.value.findIndex(d => d.id === id)
    if (idx === -1) return null
    diplomas.value[idx] = { ...diplomas.value[idx], ...data }
    return diplomas.value[idx]
  }

  async function revokeDiploma(id) {
    return updateDiploma(id, { status: 'revoked' })
  }

  function setSearch(val) {
    search.value = val
    page.value = 1
  }

  function setStatusFilter(val) {
    statusFilter.value = val
    page.value = 1
  }

  function setPage(p) {
    page.value = p
  }

  return {
    diplomas: paginatedDiplomas,
    loading,
    total,
    page,
    pageSize,
    totalPages,
    search,
    statusFilter,
    fetchDiplomas,
    fetchDiplomaById,
    createDiploma,
    updateDiploma,
    revokeDiploma,
    setSearch,
    setStatusFilter,
    setPage
  }
}

function generateMockDiplomas(count) {
  const universities = ['МГТУ им. Баумана', 'МГУ', 'СПбГУ', 'НИЯУ МИФИ', 'ВШЭ']
  const specialties = ['Информатика', 'Математика', 'Физика', 'Экономика', 'Юриспруденция']
  const statuses = ['active', 'active', 'active', 'revoked', 'pending']

  return Array.from({ length: count }, (_, i) => ({
    id: i + 1,
    serialNumber: `DIP-${(100000 + i).toString().padStart(10, '0')}`,
    studentName: `Студент ${i + 1}`,
    specialty: specialties[i % specialties.length],
    degree: 'Бакалавр',
    university: universities[i % universities.length],
    issueDate: `2025-0${(i % 9) + 1}-${(i % 28) + 1}`,
    status: statuses[i % statuses.length],
    hash: Array.from({ length: 64 }, () =>
      'abcdef0123456789'[Math.floor(Math.random() * 16)]
    ).join('')
  }))
}
