/**
 * useAuth — JWT из бэкенда (accessToken), без refresh.
 * Профиль дополняется данными регистрации и хранится в localStorage.
 */
import { ref, computed } from 'vue'
import { api } from '../api/api.js'

const PROFILE_KEY = 'user_profile'

function loadProfile() {
  try {
    const raw = localStorage.getItem(PROFILE_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

function saveProfile(p) {
  if (p) localStorage.setItem(PROFILE_KEY, JSON.stringify(p))
  else localStorage.removeItem(PROFILE_KEY)
}

function mapBackendRole(role) {
  const r = String(role || '').toUpperCase()
  if (r === 'EMPLOYER') return 'hr'
  if (r === 'UNIVERSITY') return 'university'
  if (r === 'STUDENT') return 'student'
  return r.toLowerCase() || 'student'
}

const user = ref(loadProfile())
const accessToken = ref(localStorage.getItem('access_token') || null)

const isAuthenticated = computed(() => !!accessToken.value)
const role = computed(() => user.value?.role || null)

export function useAuth() {
  function _applyLoginResponse(res, merge = {}) {
    const token = res.accessToken
    if (!token) throw new Error('Сервер не вернул accessToken')

    accessToken.value = token
    localStorage.setItem('access_token', token)
    localStorage.removeItem('refresh_token')

    const prev = loadProfile()
    const roleLower = mapBackendRole(res.role)

    const nextProfile = {
      id: res.userId,
      email: res.email,
      role: roleLower,
      name:
        merge.name ??
        prev?.name ??
        (res.email ? String(res.email).split('@')[0] : 'Пользователь'),
      universityCode: merge.universityCode ?? prev?.universityCode ?? null,
      universityId: merge.universityId ?? prev?.universityId ?? null,
      studentId: merge.studentId ?? prev?.studentId ?? null,
      employerId: merge.employerId ?? prev?.employerId ?? null,
      companyName: merge.companyName ?? prev?.companyName ?? null,
    }

    user.value = nextProfile
    saveProfile(nextProfile)
    localStorage.setItem('user_role', roleLower)
  }

  async function login(email, password) {
    const res = await api.login({ email, password })
    _applyLoginResponse(res, {})
    return user.value
  }

  async function register(data) {
    const { email, password, name, role } = data

    let merge = {}

    if (role === 'university') {
      const r = await api.registerUniversity({
        universityName: name,
        universityCode: data.universityCode,
        email,
        password,
      })
      merge = {
        name: r.universityName || name,
        universityCode: r.universityCode,
        universityId: r.universityId,
      }
    } else if (role === 'student') {
      const r = await api.registerStudent({
        fullName: name,
        birthDate: data.birthDate,
        studentNumber: data.studentNumber,
        email,
        password,
      })
      merge = {
        name: r.fullName || name,
        studentId: r.studentId,
      }
    } else if (role === 'hr') {
      const r = await api.registerEmployer({
        companyName: name,
        email,
        password,
      })
      merge = {
        name: r.companyName || name,
        companyName: r.companyName || name,
        employerId: r.employerId,
      }
    } else {
      throw new Error('Неизвестная роль')
    }

    const loginRes = await api.login({ email, password })
    _applyLoginResponse(loginRes, merge)
    return user.value
  }

  async function refresh() {
    return false
  }

  async function logout() {
    try {
      await api.logout()
    } catch {
      // Игнорируем ошибки при выходе
    }
    user.value = null
    accessToken.value = null
    localStorage.removeItem('access_token')
    localStorage.removeItem('refresh_token')
    localStorage.removeItem('user_role')
    saveProfile(null)
  }

  async function init() {
    const token = localStorage.getItem('access_token')
    const profile = loadProfile()
    if (!token || !profile?.id) {
      await logout()
      return
    }
    accessToken.value = token
    user.value = profile
    localStorage.setItem('user_role', profile.role || '')
  }

  return {
    user,
    token: accessToken,
    accessToken,
    refreshToken: ref(null),
    role,
    isAuthenticated,
    login,
    register,
    logout,
    refresh,
    init,
  }
}
