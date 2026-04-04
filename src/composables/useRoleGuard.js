/**
 * useRoleGuard — проверка доступа к маршрутам по роли
 */
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuth } from './useAuth.js'

/**
 * Маппинг ролей к разрешённым префиксам маршрутов
 */
const roleRoutes = {
  university: ['/university'],
  student: ['/student'],
  hr: ['/hr']
}

export function useRoleGuard() {
  const route = useRoute()
  const router = useRouter()
  const { role, isAuthenticated } = useAuth()

  const isAllowed = computed(() => {
    if (!isAuthenticated.value) return false

    const currentPath = route.path
    const userRole = role.value

    if (!userRole) return false

    // Публичные маршруты
    const publicRoutes = ['/', '/login', '/register', '/verify', '/404', '/api-docs']
    if (publicRoutes.includes(currentPath)) return true

    // Проверка по роли
    const allowedPrefixes = roleRoutes[userRole] || []
    return allowedPrefixes.some(prefix => currentPath.startsWith(prefix))
  })

  function checkAccess(requiredRole) {
    if (!isAuthenticated.value) {
      router.push({ name: 'login', query: { redirect: route.fullPath } })
      return false
    }

    if (role.value !== requiredRole) {
      router.push({ name: 'forbidden' })
      return false
    }

    return true
  }

  function redirectToDashboard() {
    const userRole = role.value
    if (!userRole) {
      router.push('/')
      return
    }

    const dashboards = {
      university: '/university/dashboard',
      student: '/student/dashboard',
      hr: '/hr/verify'
    }

    router.push(dashboards[userRole] || '/')
  }

  return {
    isAllowed,
    checkAccess,
    redirectToDashboard
  }
}
