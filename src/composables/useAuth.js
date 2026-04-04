/**
 * useAuth — управление аутентификацией
 * Сессия, роль, токены, logout — реальный API бэкенда ДиплоРеестр
 */
import { ref, computed } from 'vue'
import { api } from '../api/api.js'

const user = ref(null)
const accessToken = ref(localStorage.getItem('access_token') || null)
const refreshToken = ref(localStorage.getItem('refresh_token') || null)

const isAuthenticated = computed(() => !!accessToken.value)
const role = computed(() => {
  if (!user.value) return null
  // Бэкенд возвращает role как enum: UNIVERSITY, STUDENT, HR
  return user.value.role?.toLowerCase() || null
})

// Маппинг ролей бэкенда -> фронтенд
const ROLE_MAP = {
  UNIVERSITY: 'university',
  STUDENT: 'student',
  HR: 'hr',
}

export function useAuth() {
  /**
   * Войти по email/password
   */
  async function login(username, password) {
    const res = await api.login({ username, password })
    _setSession(res)
    return res.user
  }

  /**
   * Зарегистрировать пользователя
   * data: { email, password, name, role }
   * role: 'university' | 'student' | 'hr' (будет преобразовано в enum)
   */
  async function register(data) {
    const roleEnum = data.role?.toUpperCase() || 'STUDENT'
    const res = await api.register({
      email: data.email,
      password: data.password,
      name: data.name,
      role: roleEnum,
    })
    _setSession(res)
    return res.user
  }

  /**
   * Обновить access token через refresh
   */
  async function refresh() {
    if (!refreshToken.value) return false
    try {
      const res = await api.refresh(refreshToken.value)
      _setSession(res)
      return true
    } catch {
      _clearSession()
      return false
    }
  }

  /**
   * Выход
   */
  async function logout() {
    if (refreshToken.value && accessToken.value) {
      try {
        await api.logout(refreshToken.value, accessToken.value)
      } catch {
        // Игнорируем ошибку при logout
      }
    }
    _clearSession()
  }

  /**
   * Инициализация — валидация токена через stats endpoint
   */
  async function init() {
    if (!accessToken.value || !refreshToken.value) {
      _clearSession()
      return
    }
    try {
      const stats = await api.getStats(accessToken.value)
      // Stats возвращает данные в зависимости от роли
      // UNIVERSITY: universityName
      // STUDENT: studentEmail, studentName
      // HR: hrName
      const name = stats.universityName || stats.studentName || stats.hrName || ''
      const roleRaw = stats.universityName ? 'UNIVERSITY' : stats.studentEmail ? 'STUDENT' : 'HR'

      user.value = {
        id: stats.universityId || stats.userId,
        name,
        role: ROLE_MAP[roleRaw] || roleRaw.toLowerCase(),
      }
    } catch {
      // Пробуем refresh
      const ok = await refresh()
      if (!ok) _clearSession()
    }
  }

  function _setSession(res) {
    accessToken.value = res.access_token
    refreshToken.value = res.refresh_token
    localStorage.setItem('access_token', res.access_token)
    localStorage.setItem('refresh_token', res.refresh_token)

    const roleLower = res.user.role?.toLowerCase() || 'student'
    user.value = {
      id: res.user.id,
      name: res.user.name,
      role: roleLower,
    }
    localStorage.setItem('user_role', roleLower)
  }

  function _clearSession() {
    user.value = null
    accessToken.value = null
    refreshToken.value = null
    localStorage.removeItem('access_token')
    localStorage.removeItem('refresh_token')
    localStorage.removeItem('user_role')
  }

  return {
    user,
    token: accessToken,       // обратно совместимость
    accessToken,
    refreshToken,
    role,
    isAuthenticated,
    login,
    register,
    logout,
    refresh,
    init,
  }
}
