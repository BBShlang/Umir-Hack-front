<template>
  <div class="forbidden-view">
    <div class="forbidden-container">
      <div class="forbidden-card">
        <div class="forbidden-icon">
          <svg width="64" height="64" viewBox="0 0 64 64" fill="none">
            <circle cx="32" cy="32" r="28" stroke="currentColor" stroke-width="2.5"/>
            <path d="M22 22l20 20M42 22L22 42" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
          </svg>
        </div>
        <h1 class="forbidden-title">Доступ запрещён</h1>
        <p class="forbidden-description">
          У вас нет прав для доступа к этой странице. Возможно, вы пытаетесь войти в раздел другой роли.
        </p>
        <div class="forbidden-actions">
          <router-link to="/" class="forbidden-btn">
            Вернуться на главную
          </router-link>
          <button class="forbidden-btn forbidden-btn--outline" @click="goBack">
            Назад
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function goBack() {
  router.back()
}
</script>

<style scoped>
.forbidden-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  background: #f8fafc;
}

.forbidden-container {
  width: 100%;
  max-width: 480px;
}

.forbidden-card {
  background: #fff;
  border: 1px solid var(--color-gray-blue, #e2e8f0);
  border-radius: var(--radius-lg, 16px);
  padding: 3rem 2rem;
  text-align: center;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
}

.forbidden-icon {
  color: #ef4444;
  margin-bottom: 1.5rem;
}

.forbidden-title {
  font-size: 2rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 0.75rem;
}

.forbidden-description {
  font-size: 1rem;
  color: #64748b;
  line-height: 1.6;
  margin: 0 0 2rem;
}

.forbidden-actions {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.forbidden-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.875rem 1.5rem;
  font-size: 1rem;
  font-weight: 600;
  font-family: var(--font-family, system-ui);
  border-radius: var(--radius-base, 8px);
  text-decoration: none;
  cursor: pointer;
  transition: all 0.15s ease;
  border: 2px solid transparent;
  background: var(--color-main-blue, #264b82);
  color: #fff;
}

.forbidden-btn:hover {
  background: #1a5bbd;
}

.forbidden-btn--outline {
  background: transparent;
  color: var(--color-main-blue, #264b82);
  border-color: var(--color-main-blue, #264b82);
}

.forbidden-btn--outline:hover {
  background: rgba(38, 75, 130, 0.08);
}
</style>
