<template>
  <div class="profile-dropdown" ref="dropdownRef">
    <!-- Кнопка-кружок профиля -->
    <button class="profile-dropdown__avatar" @click="isOpen = !isOpen" :title="userInitials">
      {{ userInitials }}
    </button>

    <!-- Выпадающее меню -->
    <Transition name="profile-dropdown__fade">
      <div v-if="isOpen" class="profile-dropdown__menu">
        <div class="profile-dropdown__header">
          <div class="profile-dropdown__info">
            <span class="profile-dropdown__name">{{ userName }}</span>
            <span class="profile-dropdown__role">{{ userRoleLabel }}</span>
          </div>
        </div>

        <!-- Дополнительные данные при регистрации -->
        <div v-if="extraFields.length" class="profile-dropdown__extra">
          <div v-for="field in extraFields" :key="field.label" class="profile-dropdown__extra-row">
            <span class="profile-dropdown__extra-label">{{ field.label }}</span>
            <span class="profile-dropdown__extra-value">{{ field.value }}</span>
          </div>
        </div>

        <div class="profile-dropdown__divider" />

        <button class="profile-dropdown__item" @click="goToDashboard">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
            <path d="M2 3h5v5H2V3zm7 0h5v5H9V3zM2 8h5v5H2V8zm7 0h5v5H9V8z" stroke="currentColor" stroke-width="1.2"/>
          </svg>
          <span>Мой кабинет</span>
        </button>

        <div class="profile-dropdown__divider" />

        <button class="profile-dropdown__item profile-dropdown__item--danger" @click="handleLogout">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
            <path d="M6 2H3v12h3M10 6l4 2-4 2M7 13h3" stroke="currentColor" stroke-width="1.3" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>Выйти</span>
        </button>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '../../composables/useAuth.js'

const router = useRouter()
const { user, logout, role } = useAuth()

const isOpen = ref(false)
const dropdownRef = ref(null)

const userInitials = computed(() => {
  const name = user.value?.name || ''
  if (!name) return '?'
  return name.split(' ').map(w => w[0]).slice(0, 2).join('').toUpperCase()
})

const userName = computed(() => user.value?.name || 'Пользователь')

const ROLE_LABELS = {
  university: 'ВУЗ',
  student: 'Студент',
  hr: 'HR',
}

const userRoleLabel = computed(() => ROLE_LABELS[role.value] || role.value || '')

// Доп. поля из localStorage (данные регистрации)
const extraFields = computed(() => {
  const fields = []
  const stored = localStorage.getItem('user_extra')
  if (stored) {
    try {
      const data = JSON.parse(stored)
      if (data.orgName) fields.push({ label: 'Организация', value: data.orgName })
      if (data.orgInn) fields.push({ label: 'ИНН/Код ВУЗа', value: data.orgInn })
      if (data.fullName) fields.push({ label: 'ФИО', value: data.fullName })
      if (data.university) fields.push({ label: 'ВУЗ', value: data.university })
      if (data.diplomaSerial) fields.push({ label: 'Серия диплома', value: data.diplomaSerial })
      if (data.companyName) fields.push({ label: 'Компания', value: data.companyName })
      if (data.companyInn) fields.push({ label: 'ИНН компании', value: data.companyInn })
    } catch {}
  }
  return fields
})

const DASHBOARDS = {
  university: '/university/dashboard',
  student: '/student/dashboard',
  hr: '/hr/verify',
}

function goToDashboard() {
  isOpen.value = false
  const dash = DASHBOARDS[role.value] || '/university/dashboard'
  router.push(dash)
}

async function handleLogout() {
  isOpen.value = false
  await logout()
  router.push('/login')
}

function handleClickOutside(e) {
  if (dropdownRef.value && !dropdownRef.value.contains(e.target)) {
    isOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.profile-dropdown {
  position: relative;
}

.profile-dropdown__avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: var(--color-main-blue);
  color: #fff;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-bold);
  font-family: var(--font-family);
  border: 2px solid rgba(255, 255, 255, 0.25);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
  flex-shrink: 0;
}

.profile-dropdown__avatar:hover {
  background: #1a5bbd;
  border-color: rgba(255, 255, 255, 0.5);
  transform: scale(1.05);
}

/* ===== Меню ===== */
.profile-dropdown__menu {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  width: 260px;
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
  z-index: 300;
  overflow: hidden;
}

.profile-dropdown__header {
  padding: var(--space-4) var(--space-4) var(--space-3);
  background: linear-gradient(135deg, var(--color-main-blue), #1a5bbd);
  color: #fff;
}

.profile-dropdown__name {
  display: block;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-bold);
  line-height: 1.3;
}

.profile-dropdown__role {
  display: inline-block;
  margin-top: 4px;
  padding: 2px 8px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-full);
  font-size: 11px;
  font-weight: var(--font-weight-semibold);
}

.profile-dropdown__extra {
  padding: var(--space-3) var(--space-4);
  background: #f8fafc;
}

.profile-dropdown__extra-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 0;
  font-size: var(--font-size-xs);
}

.profile-dropdown__extra-label {
  color: var(--color-pale-black);
}

.profile-dropdown__extra-value {
  color: var(--color-black);
  font-weight: var(--font-weight-semibold);
  max-width: 60%;
  text-align: right;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.profile-dropdown__divider {
  height: 1px;
  background: var(--color-gray-blue);
}

.profile-dropdown__item {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  width: 100%;
  padding: 10px var(--space-4);
  border: none;
  background: none;
  cursor: pointer;
  font-family: var(--font-family);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--color-black);
  transition: background var(--transition-fast);
  text-align: left;
}

.profile-dropdown__item:hover {
  background: #f1f5f9;
}

.profile-dropdown__item--danger {
  color: var(--color-red);
}

.profile-dropdown__item--danger:hover {
  background: #fdecea;
}

/* ===== Transition ===== */
.profile-dropdown__fade-enter-active,
.profile-dropdown__fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.profile-dropdown__fade-enter-from,
.profile-dropdown__fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
