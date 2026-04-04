<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '../../composables/useAuth.js'

const router = useRouter()
const { register } = useAuth()

const nameVal = ref('')
const emailVal = ref('')
const pwdVal = ref('')
const showPwd = ref(false)
const roleVal = ref('')
const loading = ref(false)
const error = ref('')

async function submit() {
  error.value = ''
  loading.value = true
  try {
    await register({
      name: nameVal.value,
      email: emailVal.value,
      password: pwdVal.value,
      role: roleVal.value,
    })
    // Редирект зависит от роли
    const roleDashboards = {
      university: '/university/dashboard',
      student: '/student/dashboard',
      hr: '/hr/verify',
    }
    router.push(roleDashboards[roleVal.value] || '/login')
  } catch (err) {
    error.value = err.message || 'Ошибка регистрации'
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

      <form @submit.prevent="submit" class="rform">
        <div class="rform__field">
          <label class="rform__label">Название организации / ФИО</label>
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
                <path d="M1 10s3.5-7 9-7 9 7 9 7-3.5 7-9 7-9-7-9-7z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/>
                <circle cx="10" cy="10" r="2.5" stroke="currentColor" stroke-width="1.5"/>
              </svg>
              <svg v-else width="20" height="20" viewBox="0 0 20 20" fill="none">
                <path d="M3 3l14 14M8.5 4.5A8 8 0 0110 4c5.5 0 9 6 9 6s-1.2 2.1-3 3.7M11.5 15.5A8 8 0 0110 16c-5.5 0-9-6-9-6s1.2-2.1 3-3.7" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
              </svg>
            </button>
          </div>
        </div>

        <!-- Выбор роли -->
        <div class="rform__field">
          <label class="rform__label">Тип аккаунта</label>
          <div class="rform__roles">
            <button
              type="button"
              class="rform__role"
              :class="{ 'rform__role--active': roleVal === 'university' }"
              @click="roleVal = 'university'"
            >
              <span class="rform__role-icon">
                <svg width="28" height="28" viewBox="0 0 28 28" fill="none">
                  <rect x="6" y="10" width="16" height="14" rx="1.5" stroke="currentColor" stroke-width="1.6"/>
                  <path d="M10 10V6a4 4 0 018 0v4" stroke="currentColor" stroke-width="1.6"/>
                  <circle cx="14" cy="17" r="2" stroke="currentColor" stroke-width="1.4"/>
                </svg>
              </span>
              <span class="rform__role-content">
                <span class="rform__role-title">ВУЗ</span>
                <span class="rform__role-desc">Учебное заведение</span>
              </span>
            </button>

            <button
              type="button"
              class="rform__role"
              :class="{ 'rform__role--active': roleVal === 'student' }"
              @click="roleVal = 'student'"
            >
              <span class="rform__role-icon">
                <svg width="28" height="28" viewBox="0 0 28 28" fill="none">
                  <path d="M14 4L4 9l10 5 10-5-10-5z" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round"/>
                  <path d="M8 11v6c0 2 2.5 4 6 5 3.5-1 6-3 6-5v-6" stroke="currentColor" stroke-width="1.4" stroke-linecap="round"/>
                </svg>
              </span>
              <span class="rform__role-content">
                <span class="rform__role-title">Студент</span>
                <span class="rform__role-desc">Выпускник / Учащийся</span>
              </span>
            </button>

            <button
              type="button"
              class="rform__role"
              :class="{ 'rform__role--active': roleVal === 'hr' }"
              @click="roleVal = 'hr'"
            >
              <span class="rform__role-icon">
                <svg width="28" height="28" viewBox="0 0 28 28" fill="none">
                  <rect x="4" y="6" width="20" height="16" rx="2" stroke="currentColor" stroke-width="1.6"/>
                  <path d="M9 12h2M9 16h6" stroke="currentColor" stroke-width="1.4" stroke-linecap="round"/>
                  <path d="M18 12v6M15 15h6" stroke="currentColor" stroke-width="1.4" stroke-linecap="round"/>
                </svg>
              </span>
              <span class="rform__role-content">
                <span class="rform__role-title">HR / Работодатель</span>
                <span class="rform__role-desc">Проверка дипломов</span>
              </span>
            </button>
          </div>
        </div>

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

/* ---- Фон как hero ---- */
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
  background-image: linear-gradient(
      rgba(255, 255, 255, 0.03) 1px,
      transparent 1px
    ),
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

/* ===== Форма ===== */
.rform {
  padding: 0 var(--space-8) var(--space-8);
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
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

/* ===== Роли ===== */
.rform__roles {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.rform__role {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-3) var(--space-4);
  background-color: var(--color-pale-blue);
  border: 1.5px solid transparent;
  border-radius: var(--radius-base);
  cursor: pointer;
  transition: border-color var(--transition-fast), background-color var(--transition-fast), box-shadow var(--transition-fast);
  color: var(--color-pale-black);
  font-family: var(--font-family);
  text-align: left;
  width: 100%;
}

.rform__role:hover {
  background-color: #dde5f0;
}

.rform__role--active {
  border-color: var(--color-main-blue);
  background-color: #fff;
  box-shadow: 0 0 0 3px rgba(38, 75, 130, 0.08);
  color: var(--color-main-blue);
}

.rform__role-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: color var(--transition-fast);
}

.rform__role-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.rform__role-title {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  line-height: 1.3;
}

.rform__role-desc {
  font-size: var(--font-size-xs);
  color: var(--color-pale-black);
  transition: color var(--transition-fast);
}

.rform__role--active .rform__role-desc {
  color: var(--color-gray);
}

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
@media (max-width: 540px) {
  .register-page { padding: var(--space-4) var(--space-3); }

  .register-box__hero { padding: var(--space-6) var(--space-6) var(--space-4); }
  .register-box__title { font-size: var(--font-size-xl); }
  .register-box__subtitle { font-size: var(--font-size-xs); }

  .rform { padding: 0 var(--space-6) var(--space-6); gap: var(--space-4); }

  .rform__actions {
    flex-direction: column-reverse;
    align-items: stretch;
    gap: var(--space-3);
  }

  .rform__submit { width: 100%; padding: 12px; }

  .rform__role { padding: var(--space-3); gap: var(--space-3); }
  .rform__role-icon svg { width: 24px; height: 24px; }
}
</style>
