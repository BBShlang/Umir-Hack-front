<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuth } from '../../composables/useAuth.js'

const router = useRouter()
const route = useRoute()
const { login } = useAuth()

const loginVal = ref('')
const pwdVal = ref('')
const showPwd = ref(false)
const loading = ref(false)
const error = ref('')

async function submit() {
  error.value = ''
  loading.value = true
  try {
    await login(loginVal.value, pwdVal.value)
    const redirect = route.query.redirect || '/university/dashboard'
    router.push(redirect)
  } catch (err) {
    if (err.status === 401) {
      error.value = 'Неверный логин или пароль'
    } else {
      error.value = err.message || 'Ошибка сервера. Попробуйте позже.'
    }
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <!-- Фоновые эффекты -->
    <div class="login-page__bg" aria-hidden="true">
      <div class="login-page__bg-grid" />
      <div class="login-page__bg-glow login-page__bg-glow--1" />
      <div class="login-page__bg-glow login-page__bg-glow--2" />
    </div>

    <div class="login-box">
      <div class="login-box__hero">
        <h1 class="login-box__title">
          Вход в систему<br />
          <span class="login-box__title-accent">ДиплоРеестр</span>
        </h1>
        <p class="login-box__subtitle">
          Войдите в свой аккаунт для доступа к платформе верификации дипломов
        </p>
      </div>

      <form @submit.prevent="submit" class="lform">
        <div class="lform__field">
          <label class="lform__label">Email</label>
          <input
            v-model="loginVal"
            type="email"
            placeholder="your@email.com"
            class="lform__input"
            autocomplete="email"
            required
          />
        </div>

        <div class="lform__field">
          <label class="lform__label">Пароль</label>
          <div class="lform__input-wrap">
            <input
              v-model="pwdVal"
              :type="showPwd ? 'text' : 'password'"
              placeholder="••••••••"
              class="lform__input"
              autocomplete="current-password"
              required
            />
            <button
              class="lform__eye"
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

        <!-- Ошибка -->
        <div v-if="error" class="lform__error">
          <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
            <circle cx="7" cy="7" r="6" stroke="currentColor" stroke-width="1.3"/>
            <path d="M7 4v4M7 9.5v.3" stroke="currentColor" stroke-width="1.3" stroke-linecap="round"/>
          </svg>
          {{ error }}
        </div>

        <div class="lform__actions">
          <router-link to="/register" class="lform__link">Нет аккаунта? Зарегистрируйтесь</router-link>
          <button class="lform__submit" type="submit" :disabled="loading">
            <span v-if="loading" class="lform__spinner" />
            {{ loading ? 'Вход...' : 'Войти' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-8) var(--space-4);
  position: relative;
  background: #0d1f3c;
}

/* ---- Фон как hero ---- */
.login-page__bg {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.login-page__bg-grid {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(
      rgba(255, 255, 255, 0.03) 1px,
      transparent 1px
    ),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.login-page__bg-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.login-page__bg-glow--1 {
  width: 500px;
  height: 500px;
  top: -150px;
  right: -100px;
  background: rgba(38, 75, 130, 0.45);
}

.login-page__bg-glow--2 {
  width: 380px;
  height: 380px;
  bottom: -120px;
  left: -60px;
  background: rgba(72, 184, 194, 0.2);
}

/* ---- Карточка ---- */
.login-box {
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

.login-box__hero {
  padding: var(--space-8) var(--space-8) var(--space-4);
  text-align: center;
}

.login-box__title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  line-height: 1.2;
  margin-bottom: var(--space-2);
}

.login-box__title-accent {
  color: var(--color-main-blue);
}

.login-box__subtitle {
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
  line-height: 1.5;
  max-width: 400px;
  margin: 0 auto;
}

/* ===== Форма ===== */
.lform {
  padding: 0 var(--space-8) var(--space-8);
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
}

.lform__field {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.lform__label {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-black);
}

.lform__input {
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

.lform__input::placeholder { color: var(--color-pale-black); }

.lform__input:focus {
  background-color: #fff;
  border-color: var(--color-main-blue);
}

.lform__input-wrap { position: relative; }
.lform__input-wrap .lform__input { padding-right: 46px; }

/* ===== Глаз ===== */
.lform__eye {
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

.lform__eye:hover { color: var(--color-main-blue); }

/* ===== Ошибка ===== */
.lform__error {
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
.lform__actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-4);
  margin-top: var(--space-2);
}

.lform__link {
  font-size: var(--font-size-sm);
  color: var(--color-main-blue);
  text-decoration: none;
  font-weight: var(--font-weight-semibold);
}

.lform__link:hover {
  text-decoration: underline;
}

.lform__submit {
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

.lform__submit:hover:not(:disabled) { background-color: #1a5bbd; }
.lform__submit:disabled { opacity: 0.7; cursor: not-allowed; }

.lform__spinner {
  display: inline-block;
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: lform-spin 0.7s linear infinite;
}

@keyframes lform-spin {
  to { transform: rotate(360deg); }
}

/* ===========================
   АДАПТИВ
   =========================== */
@media (max-width: 540px) {
  .login-page { padding: var(--space-4) var(--space-3); }

  .login-box__hero { padding: var(--space-6) var(--space-6) var(--space-4); }
  .login-box__title { font-size: var(--font-size-xl); }
  .login-box__subtitle { font-size: var(--font-size-xs); }

  .lform { padding: 0 var(--space-6) var(--space-6); gap: var(--space-4); }

  .lform__actions {
    flex-direction: column-reverse;
    align-items: stretch;
    gap: var(--space-3);
  }

  .lform__submit { width: 100%; padding: 12px; }
}
</style>
