/**
 * api-mock.js — имитация REST API с задержкой для разработки
 */

const DELAY = 500 // базовая задержка в мс

function delay(ms = DELAY) {
  return new Promise(resolve => setTimeout(resolve, ms + Math.random() * 200))
}

// ---- Хранилище (in-memory) ----
const db = {
  diplomas: [],
  users: [],
  verifications: []
}

// Заполнение mock-данными
function seedData() {
  const universities = ['МГТУ им. Баумана', 'МГУ', 'СПбГУ', 'ВШЭ']
  const specialties = ['Информатика', 'Математика', 'Физика', 'Экономика']
  const statuses = ['active', 'active', 'active', 'revoked', 'pending']

  for (let i = 0; i < 50; i++) {
    db.diplomas.push({
      id: i + 1,
      serialNumber: `DIP-${(100000 + i).toString().padStart(10, '0')}`,
      studentName: `Студент ${i + 1}`,
      specialty: specialties[i % specialties.length],
      degree: i % 3 === 0 ? 'Магистр' : 'Бакалавр',
      university: universities[i % universities.length],
      issueDate: `2025-0${(i % 9) + 1}-${((i % 28) + 1).toString().padStart(2, '0')}`,
      status: statuses[i % statuses.length],
      hash: Array.from({ length: 64 }, () =>
        'abcdef0123456789'[Math.floor(Math.random() * 16)]
      ).join(''),
      createdAt: new Date().toISOString()
    })
  }
}

seedData()

// ---- API методы ----

/**
 * GET /api/diplomas
 */
export async function apiGetDiplomas({ page = 1, pageSize = 10, search = '', status = 'all' } = {}) {
  await delay()

  let items = db.diplomas

  if (search) {
    const q = search.toLowerCase()
    items = items.filter(d =>
      d.serialNumber.toLowerCase().includes(q) ||
      d.studentName.toLowerCase().includes(q) ||
      d.specialty.toLowerCase().includes(q)
    )
  }

  if (status !== 'all') {
    items = items.filter(d => d.status === status)
  }

  const total = items.length
  const start = (page - 1) * pageSize
  const data = items.slice(start, start + pageSize)

  return { data, total, page, pageSize }
}

/**
 * GET /api/diplomas/:id
 */
export async function apiGetDiplomaById(id) {
  await delay(300)
  return db.diplomas.find(d => d.id === Number(id)) || null
}

/**
 * POST /api/diplomas
 */
export async function apiCreateDiploma(data) {
  await delay(800)
  const newDiploma = {
    id: db.diplomas.length + 1,
    ...data,
    status: 'active',
    hash: Array.from({ length: 64 }, () =>
      'abcdef0123456789'[Math.floor(Math.random() * 16)]
    ).join(''),
    createdAt: new Date().toISOString()
  }
  db.diplomas.push(newDiploma)
  return newDiploma
}

/**
 * PATCH /api/diplomas/:id
 */
export async function apiUpdateDiploma(id, data) {
  await delay(400)
  const idx = db.diplomas.findIndex(d => d.id === Number(id))
  if (idx === -1) throw new Error('Диплом не найден')
  db.diplomas[idx] = { ...db.diplomas[idx], ...data }
  return db.diplomas[idx]
}

/**
 * DELETE /api/diplomas/:id (отзыв)
 */
export async function apiRevokeDiploma(id) {
  await delay(600)
  return apiUpdateDiploma(id, { status: 'revoked' })
}

/**
 * POST /api/verify
 */
export async function apiVerifyDiploma({ serial, hash } = {}) {
  await delay(600)

  let diploma = null
  if (serial) {
    diploma = db.diplomas.find(d => d.serialNumber === serial)
  } else if (hash) {
    diploma = db.diplomas.find(d => d.hash === hash)
  }

  const result = {
    checkedAt: new Date().toISOString()
  }

  if (!diploma) {
    result.valid = false
    result.status = 'not_found'
    result.reason = 'Диплом не найден в реестре'
  } else if (diploma.status === 'revoked') {
    result.valid = false
    result.status = 'revoked'
    result.reason = 'Диплом отозван ВУЗом'
  } else {
    result.valid = true
    result.status = diploma.status
    result.serialNumber = diploma.serialNumber
    result.university = diploma.university
    result.specialty = diploma.specialty
    result.issueDate = diploma.issueDate
    result.hash = diploma.hash
  }

  db.verifications.push(result)
  return result
}

/**
 * POST /api/verify/bulk
 */
export async function apiBulkVerify({ serials } = {}) {
  await delay(1000)

  const results = await Promise.all(
    serials.map(serial => apiVerifyDiploma({ serial }))
  )

  return {
    total: serials.length,
    valid: results.filter(r => r.valid).length,
    invalid: results.filter(r => !r.valid).length,
    results
  }
}

/**
 * POST /api/auth/login
 */
export async function apiLogin(email, password) {
  await delay(500)

  const mockUsers = {
    'university@test.ru': { id: 1, name: 'МГТУ', role: 'university' },
    'student@test.ru': { id: 2, name: 'Иванов И.И.', role: 'student' },
    'hr@test.ru': { id: 3, name: 'HR Отдел', role: 'hr' }
  }

  const user = mockUsers[email.toLowerCase()]
  if (!user) throw new Error('Неверный email или пароль')

  return {
    user,
    token: 'mock-token-' + Date.now()
  }
}

/**
 * POST /api/auth/register
 */
export async function apiRegister(data) {
  await delay(800)

  const user = {
    id: db.users.length + 1,
    name: data.name,
    role: data.role,
    createdAt: new Date().toISOString()
  }

  db.users.push(user)

  return {
    user,
    token: 'mock-token-' + Date.now()
  }
}
