/**
 * Vue Router — маршруты для всех ролей и публичные страницы
 */
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  // Публичные
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/auth/LoginView.vue')
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/auth/RegisterView.vue')
  },
  {
    path: '/verify',
    name: 'verify',
    component: () => import('../views/hr/VerifyView.vue')
  },
  {
    path: '/verify/qr',
    name: 'verify-qr',
    component: () => import('../views/public/PublicVerifyView.vue')
  },
  {
    path: '/verify/share/:token',
    name: 'verify-share',
    component: () => import('../views/hr/VerifyView.vue'),
    props: true
  },
  {
    path: '/privacy',
    name: 'privacy',
    component: () => import('../views/public/PrivacyView.vue')
  },
  {
    path: '/terms',
    name: 'terms',
    component: () => import('../views/public/TermsView.vue')
  },
  {
    path: '/forbidden',
    name: 'forbidden',
    component: () => import('../views/public/ForbiddenView.vue')
  },

  // ЛК ВУЗа
  {
    path: '/university',
    redirect: '/university/dashboard'
  },
  {
    path: '/university/dashboard',
    name: 'university-dashboard',
    component: () => import('../views/university/DashboardView.vue'),
    meta: { role: 'university' }
  },
  {
    path: '/university/upload',
    name: 'university-upload',
    component: () => import('../views/university/UploadRegistryView.vue'),
    meta: { role: 'university' }
  },
  {
    path: '/university/diplomas',
    name: 'university-diplomas',
    component: () => import('../views/university/DiplomasListView.vue'),
    meta: { role: 'university' }
  },
  {
    path: '/university/claims',
    name: 'university-claims',
    component: () => import('../views/university/ClaimsView.vue'),
    meta: { role: 'university' }
  },
  // ЛК Студента
  {
    path: '/student',
    redirect: '/student/dashboard'
  },
  {
    path: '/student/dashboard',
    name: 'student-dashboard',
    component: () => import('../views/student/DashboardView.vue'),
    meta: { role: 'student' }
  },
  {
    path: '/student/diplomas',
    name: 'student-diplomas',
    component: () => import('../views/student/MyDiplomasView.vue'),
    meta: { role: 'student' }
  },
  {
    path: '/student/search',
    name: 'student-search',
    component: () => import('../views/student/SearchDiplomaView.vue'),
    meta: { role: 'student' }
  },


  // HR-портал
  {
    path: '/hr',
    redirect: '/hr/verify'
  },
  {
    path: '/hr/verify',
    name: 'hr-verify',
    component: () => import('../views/hr/VerifyView.vue'),
    meta: { role: 'hr' }
  },

  // 404
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('../views/public/NotFoundView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Глобальный guard для проверки роли
router.beforeEach((to, from, next) => {
  // Публичные маршруты — всегда доступны
  const publicRoutes = [
    'login',
    'register',
    'verify',
    'verify-qr',
    'verify-share',
    'not-found',
    'privacy',
    'terms',
    'forbidden',
  ]
  if (publicRoutes.includes(to.name)) {
    return next()
  }

  const token = localStorage.getItem('access_token')

  // Если нет токена — на логин
  if (!token) {
    return next({ name: 'login', query: { redirect: to.fullPath } })
  }

  // Если маршрут требует роль — проверяем
  if (to.meta.role) {
    const savedRole = localStorage.getItem('user_role')
    if (savedRole && savedRole !== to.meta.role) {
      // Роль не совпадает — редирект на свой дашборд
      const dashboards = {
        university: '/university/dashboard',
        student: '/student/dashboard',
        hr: '/hr/verify'
      }
      return next(dashboards[savedRole] || '/')
    }
  }

  next()
})

export default router
