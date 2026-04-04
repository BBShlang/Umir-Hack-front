<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '../../composables/useAuth.js'

const router = useRouter()
const { register } = useAuth()

// Выбор роли: 'university' | 'student' | 'hr'
const roleVal = ref('')

// Общие поля
const emailVal = ref('')
const pwdVal = ref('')
const nameVal = ref('')
const universityCodeVal = ref('')
const studentNumberVal = ref('')
const birthDateVal = ref('')
const showPwd = ref(false)

const loading = ref(false)
const error = ref('')

// Дашборд по роли
const roleDashboardMap = {
  university: '/university/dashboard',
  student: '/student/dashboard',
  hr: '/hr/verify'
}

// Валидация перед отправкой
function validate() {
  if (!roleVal.value) {
    error.value = 'Выберите тип аккаунта'
    return false
  }
  if (!emailVal.value || !pwdVal.value || !nameVal.value) {
    error.value = 'Заполните все обязательные поля'
    return false
  }
  if (roleVal.value === 'university' && !universityCodeVal.value.trim()) {
    error.value = 'Укажите код университета'
    return false
  }
  if (roleVal.value === 'student') {
    if (!studentNumberVal.value.trim()) {
      error.value = 'Укажите номер студенческого билета'
      return false
    }
    if (!birthDateVal.value) {
      error.value = 'Укажите дату рождения'
      return false
    }
  }
  if (pwdVal.value.length < 6) {
    error.value = 'Пароль должен содержать минимум 6 символов'
    return false
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(emailVal.value)) {
    error.value = 'Неверный формат email'
    return false
  }
  return true
}

async function submit() {
  error.value = ''
  if (!validate()) {
    loading.value = false
    return
  }

  loading.value = true
  try {
    const payload = {
      email: emailVal.value,
      password: pwdVal.value,
      name: nameVal.value,
      role: roleVal.value,
      universityCode: universityCodeVal.value.trim(),
      studentNumber: studentNumberVal.value.trim(),
      birthDate: birthDateVal.value,
    }

    await register(payload)
    // Redirect to appropriate dashboard based on role
    const redirect = roleDashboardMap[roleVal.value] || '/university/dashboard'
    router.push(redirect)
  } catch (err) {
    if (err.status === 409) {
      error.value = 'Пользователь с таким email уже существует'
    } else if (err.errors) {
      const messages = Object.values(err.errors).flatMap((v) =>
        Array.isArray(v) ? v : [v]
      )
      error.value = messages.join('. ') || 'Ошибка регистрации'
    } else {
      error.value = err.message || 'Ошибка регистрации'
    }
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="register-page">
    <!-- Фоновые эффекты -->
    <div class="register-page__bg" aria-hidden="true">
      <div class="register-page__bg-grid" />
      <div class="register-page__bg-glow register-page__bg-glow--1" />
      <div class="register-page__bg-glow register-page__bg-glow--2" />
    </div>

    <div class="register-box">
      <div class="register-box__hero">
        <h1 class="register-box__title">
          Регистрация<br />
          <span class="register-box__title-accent">Добро пожаловать</span>
        </h1>
        <p class="register-box__subtitle">
          Создайте аккаунт для доступа к платформе верификации дипломов
        </p>
      </div>

      <!-- ===== Выбор роли (вверху) ===== -->
      <div class="register-box__roles">
        <button
          type="button"
          class="role-btn"
          :class="{ 'role-btn--active': roleVal === 'university' }"
          @click="roleVal = 'university'; error = ''"
        >
          <span class="role-btn__icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <rect x="3" y="8" width="18" height="13" rx="1.5" stroke="currentColor" stroke-width="1.5"/>
              <path d="M7 8V5a5 5 0 0110 0v3" stroke="currentColor" stroke-width="1.5"/>
              <circle cx="12" cy="14.5" r="1.5" stroke="currentColor" stroke-width="1.3"/>
            </svg>
          </span>
          <span class="role-btn__text">
            <span class="role-btn__title">ВУЗ</span>
            <span class="role-btn__desc">Учебное заведение</span>
          </span>
        </button>

        <button
          type="button"
          class="role-btn"
          :class="{ 'role-btn--active': roleVal === 'student' }"
          @click="roleVal = 'student'; error = ''"
        >
          <span class="role-btn__icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <path d="M12 3L3 8l9 5 9-5-9-5z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/>
              <path d="M6 10v5c0 2 2.5 3.5 6 4.5 3.5-1 6-2.5 6-4.5v-5" stroke="currentColor" stroke-width="1.3" stroke-linecap="round"/>
            </svg>
          </span>
          <span class="role-btn__text">
            <span class="role-btn__title">Студент</span>
            <span class="role-btn__desc">Выпускник / Учащийся</span>
          </span>
        </button>

        <button
          type="button"
          class="role-btn"
          :class="{ 'role-btn--active': roleVal === 'hr' }"
          @click="roleVal = 'hr'; error = ''"
        >
          <span class="role-btn__icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <rect x="2" y="4" width="20" height="16" rx="2" stroke="currentColor" stroke-width="1.5"/>
              <path d="M6 9h3M6 13h5" stroke="currentColor" stroke-width="1.3" stroke-linecap="round"/>
              <path d="M16 9v6M14 12h4" stroke="currentColor" stroke-width="1.3" stroke-linecap="round"/>
            </svg>
          </span>
          <span class="role-btn__text">
            <span class="role-btn__title">HR</span>
            <span class="role-btn__desc">Работодатель</span>
          </span>
        </button>
      </div>

      <!-- ===== Форма (показывается после выбора роли) ===== -->
      <form v-if="roleVal" @submit.prevent="submit" class="rform">
        
        <!-- Общие поля -->
        <div class="rform__field">
          <label class="rform__label">Email</label>
          <input
            v-model="emailVal"
            type="email"
            placeholder="your@email.com"
            class="rform__input"
            autocomplete="email"
            required
          />
        </div>

        <div class="rform__field">
          <label class="rform__label">Пароль</label>
          <div class="rform__input-wrap">
            <input
              v-model="pwdVal"
              :type="showPwd ? 'text' : 'password'"
              placeholder="Минимум 6 символов"
              class="rform__input"
              autocomplete="new-password"
              required
              minlength="6"
            />
            <button
              class="rform__eye"
              type="button"
              @click="showPwd = !showPwd"
              tabindex="-1"
            >
              <svg v-if="!showPwd" width="20" height="20" viewBox="0 0 20 20" fill="none">
                <path d="M1 10s3.5-7 9-7 9 7-3.5 7-9 7-9-7-9-7z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/>
                <circle cx="10" cy="10" r="2.5" stroke="currentColor" stroke-width="1.5"/>
              </svg>
              <svg v-else width="20" height="20" viewBox="0 0 20 20" fill="none">
                <path d="M3 3l14 14M8.5 4.5A8 8 0 0110 4c5.5 0 9 6 9 6s-1.2 2.1-3 3.7M11.5 15.5A8 8 0 0110 16c-5.5 0-9-6-9-6s1.2-2.1 3-3.7" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
              </svg>
            </button>
          </div>
        </div>

        <!-- Поля для ВУЗа -->
        <template v-if="roleVal === 'university'">
          <div class="rform__field">
            <label class="rform__label">Название организации</label>
            <input
              v-model="nameVal"
              type="text"
              placeholder="МГТУ им. Баумана"
              class="rform__input"
              autocomplete="organization"
              required
            />
          </div>
          <div class="rform__field">
            <label class="rform__label">Код университета</label>
            <input
              v-model="universityCodeVal"
              type="text"
              placeholder="AITU-01"
              class="rform__input"
              autocomplete="off"
              required
            />
          </div>
        </template>

        <!-- Поля для Студента -->
        <template v-if="roleVal === 'student'">
          <div class="rform__field">
            <label class="rform__label">ФИО полностью</label>
            <input
              v-model="nameVal"
              type="text"
              placeholder="Иванов Иван Иванович"
              class="rform__input"
              autocomplete="name"
              required
            />
          </div>
          <div class="rform__field">
            <label class="rform__label">Номер студенческого билета</label>
            <input
              v-model="studentNumberVal"
              type="text"
              placeholder="ST-2026-001"
              class="rform__input"
              autocomplete="off"
              required
            />
          </div>
          <div class="rform__field">
            <label class="rform__label">Дата рождения</label>
            <input
              v-model="birthDateVal"
              type="date"
              class="rform__input"
              required
            />
          </div>
        </template>

        <!-- Поля для HR -->
        <template v-if="roleVal === 'hr'">
          <div class="rform__field">
            <label class="rform__label">Название компании</label>
            <input
              v-model="nameVal"
              type="text"
              placeholder="ООО «Технологии»"
              class="rform__input"
              autocomplete="organization"
              required
            />
          </div>
        </template>

        <!-- Ошибка -->
        <div v-if="error" class="rform__error">
          <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
            <circle cx="7" cy="7" r="6" stroke="currentColor" stroke-width="1.3"/>
            <path d="M7 4v4M7 9.5v.3" stroke="currentColor" stroke-width="1.3" stroke-linecap="round"/>
          </svg>
          {{ error }}
        </div>

        <div class="rform__actions">
          <router-link to="/login" class="rform__link">Уже есть аккаунт? Войти</router-link>
          <button class="rform__submit" type="submit" :disabled="loading">
            <span v-if="loading" class="rform__spinner" />
            {{ loading ? 'Регистрация...' : 'Зарегистрироваться' }}
          </button>
        </div>
      </form>

      <!-- Подсказка, если роль не выбрана -->
      <div v-else class="register-box__hint">
        <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
          <circle cx="8" cy="8" r="7" stroke="currentColor" stroke-width="1.2" opacity="0.5"/>
          <path d="M8 5v4M8 10.5v.3" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" opacity="0.5"/>
        </svg>
        <span>Выберите тип аккаунта выше, чтобы продолжить</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-8) var(--space-4);
  position: relative;
  background: #0d1f3c;
}

/* ---- Фон ---- */
.register-page__bg {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.register-page__bg-grid {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.register-page__bg-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.register-page__bg-glow--1 {
  width: 500px;
  height: 500px;
  top: -150px;
  right: -100px;
  background: rgba(38, 75, 130, 0.45);
}

.register-page__bg-glow--2 {
  width: 380px;
  height: 380px;
  bottom: -120px;
  left: -60px;
  background: rgba(72, 184, 194, 0.2);
}

/* ---- Карточка ---- */
.register-box {
  position: relative;
  z-index: 1;
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  width: 100%;
  max-width: 520px;
  overflow: hidden;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.25);
}

.register-box__hero {
  padding: var(--space-8) var(--space-8) var(--space-4);
  text-align: center;
  border-bottom: 1px solid var(--color-gray-blue);
}

.register-box__title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  line-height: 1.2;
  margin-bottom: var(--space-2);
}

.register-box__title-accent {
  color: var(--color-main-blue);
}

.register-box__subtitle {
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
  line-height: 1.5;
  max-width: 400px;
  margin: 0 auto;
}

/* ===== Выбор роли (вверху) ===== */
.register-box__roles {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1px;
  background: var(--color-gray-blue);
  border-bottom: 1px solid var(--color-gray-blue);
}

.role-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-4) var(--space-3);
  background: #fff;
  border: none;
  cursor: pointer;
  transition: all var(--transition-fast);
  color: var(--color-pale-black);
  font-family: var(--font-family);
  text-align: center;
}

.role-btn:hover {
  background: #f8fafc;
  color: var(--color-main-blue);
}

.role-btn--active {
  background: var(--color-pale-blue);
  color: var(--color-main-blue);
  font-weight: var(--font-weight-semibold);
}

.role-btn__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: var(--color-pale-blue);
  border-radius: var(--radius-base);
  transition: background var(--transition-fast), color var(--transition-fast);
}

.role-btn--active .role-btn__icon {
  background: var(--color-main-blue);
  color: #fff;
}

.role-btn__text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.role-btn__title {
  font-size: var(--font-size-sm);
  line-height: 1.2;
}

.role-btn__desc {
  font-size: var(--font-size-xs);
  color: var(--color-pale-black);
  line-height: 1.2;
}

.role-btn--active .role-btn__desc {
  color: var(--color-gray);
}

/* ===== Подсказка ===== */
.register-box__hint {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  padding: var(--space-4) var(--space-8);
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
  background: #f8fafc;
  border-bottom: 1px solid var(--color-gray-blue);
}

/* ===== Форма ===== */
.rform {
  padding: var(--space-6) var(--space-8) var(--space-8);
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.rform__field {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.rform__label {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-black);
}

.rform__input {
  font-family: var(--font-family);
  font-size: var(--font-size-base);
  color: var(--color-black);
  background-color: var(--color-pale-blue);
  border: 1.5px solid transparent;
  border-radius: var(--radius-base);
  padding: 11px 14px;
  outline: none;
  width: 100%;
  transition: border-color var(--transition-fast), background-color var(--transition-fast);
}

.rform__input::placeholder { color: var(--color-pale-black); }

.rform__input:focus {
  background-color: #fff;
  border-color: var(--color-main-blue);
}

.rform__input-wrap { position: relative; }
.rform__input-wrap .rform__input { padding-right: 46px; }

/* ===== Глаз ===== */
.rform__eye {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  color: var(--color-pale-black);
  display: flex;
  align-items: center;
  padding: 0;
  transition: color var(--transition-fast);
}

.rform__eye:hover { color: var(--color-main-blue); }

/* ===== Ошибка ===== */
.rform__error {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--font-size-sm);
  color: var(--color-red);
  background: #fdecea;
  border: 1px solid #f5beba;
  border-radius: var(--radius-base);
  padding: var(--space-2) var(--space-3);
}

/* ===== Actions ===== */
.rform__actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-4);
  margin-top: var(--space-2);
}

.rform__link {
  font-size: var(--font-size-sm);
  color: var(--color-main-blue);
  text-decoration: none;
  font-weight: var(--font-weight-semibold);
}

.rform__link:hover {
  text-decoration: underline;
}

.rform__submit {
  font-family: var(--font-family);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: #fff;
  background-color: var(--color-main-blue);
  border: none;
  cursor: pointer;
  padding: 10px 36px;
  border-radius: var(--radius-base);
  transition: background-color var(--transition-fast);
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.rform__submit:hover:not(:disabled) { background-color: #1a5bbd; }
.rform__submit:disabled { opacity: 0.7; cursor: not-allowed; }

.rform__spinner {
  display: inline-block;
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: rform-spin 0.7s linear infinite;
}

@keyframes rform-spin {
  to { transform: rotate(360deg); }
}

/* ===========================
   АДАПТИВ
   =========================== */
@media (max-width: 640px) {
  .register-box__roles {
    grid-template-columns: 1fr;
    gap: 0;
  }
  
  .role-btn {
    flex-direction: row;
    justify-content: flex-start;
    gap: var(--space-3);
    padding: var(--space-3) var(--space-4);
    text-align: left;
  }
  
  .role-btn__icon {
    width: 32px;
    height: 32px;
  }
  
  .role-btn__desc {
    display: none;
  }
}

@media (max-width: 540px) {
  .register-page { padding: var(--space-4) var(--space-3); }

  .register-box__hero { padding: var(--space-6) var(--space-6) var(--space-4); }
  .register-box__title { font-size: var(--font-size-xl); }
  .register-box__subtitle { font-size: var(--font-size-xs); }

  .rform { padding: 0 var(--space-6) var(--space-6); gap: var(--space-3); }

  .rform__actions {
    flex-direction: column-reverse;
    align-items: stretch;
    gap: var(--space-3);
  }

  .rform__submit { width: 100%; padding: 12px; }
}
</style>