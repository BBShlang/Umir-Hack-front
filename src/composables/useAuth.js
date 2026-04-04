/**
 * useAuth — управление аутентификацией
 * Сессия, роль, токены, logout
 */
import { ref, computed } from 'vue'

const user = ref(null)
const token = ref(localStorage.getItem('auth_token') || null)
const isAuthenticated = computed(() => !!token.value)

// Роль пользователя: 'university' | 'student' | 'hr' | null
const role = computed(() => user.value?.role || null)

export function useAuth() {
  /**
   * Войти по email/password (эмуляция)
   */
  async function login(email, password) {
    // TODO: заменить на реальный API-вызов
    await new Promise(r => setTimeout(r, 500))

    const mockUsers = {
      'university@test.ru': { id: 1, name: 'МГТУ', role: 'university' },
      'student@test.ru': { id: 2, name: 'Иванов И.И.', role: 'student' },
      'hr@test.ru': { id: 3, name: 'HR Отдел', role: 'hr' }
    }

    const found = mockUsers[email.toLowerCase()]
    if (!found) {
      throw new Error('Неверный email или пароль')
    }

    user.value = found
    token.value = 'mock-token-' + Date.now()
    localStorage.setItem('auth_token', token.value)
    localStorage.setItem('user_role', found.role)

    return found
  }

  /**
   * Зарегистрировать нового пользователя
   */
  async function register(data) {
    // TODO: заменить на реальный API-вызов
    await new Promise(r => setTimeout(r, 800))

    user.value = {
      id: Date.now(),
      name: data.name,
      role: data.role
    }
    token.value = 'mock-token-' + Date.now()
    localStorage.setItem('auth_token', token.value)
    localStorage.setItem('user_role', data.role)

    return user.value
  }

  function logout() {
    user.value = null
    token.value = null
    localStorage.removeItem('auth_token')
    localStorage.removeItem('user_role')
  }

  /**
   * Инициализация при старте — проверить токен
   */
  async function init() {
    if (!token.value) return
    // TODO: валидация токена через API
    // Пока — заглушка
  }

  return {
    user,
    token,
    role,
    isAuthenticated,
    login,
    register,
    logout,
    init
  }
}
