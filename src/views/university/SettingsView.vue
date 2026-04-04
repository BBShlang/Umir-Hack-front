<template>
  <div class="settings-view">
    <AppHeader />
    <main class="main-content">
      <!-- ===== HERO ===== -->
      <section class="settings__hero">
        <div class="settings__hero-bg" aria-hidden="true">
          <div class="settings__hero-grid-lines" />
          <div class="settings__hero-glow settings__hero-glow--1" />
          <div class="settings__hero-glow settings__hero-glow--2" />
        </div>

        <div class="container settings__hero-inner">
          <div class="settings__hero-content">
            <h1 class="settings__title">
              Настройки ВУЗа<br />
              <span class="settings__title-accent">Конфигурация</span>
            </h1>
            <p class="settings__subtitle">
              Управляйте параметрами ЭЦП, API-ключами, сроками действия QR-кодов и безопасностью вашего аккаунта.
            </p>
            <div class="settings__hero-actions">
              <router-link to="/university/dashboard" class="settings__btn settings__btn--outline">На дашборд</router-link>
              <router-link to="/university/diplomas" class="settings__btn settings__btn--outline">К реестру</router-link>
            </div>
          </div>
        </div>
      </section>
      <!-- ===== Основные настройки ===== -->
      <section class="settings__main-section">
        <div class="container">
          <div class="settings__main-head">
            <p class="settings__how-eyebrow">Конфигурация</p>
            <h2 class="settings__how-title">Параметры системы</h2>
          </div>
          <div class="settings__main-card">
            <SignatureSettings :settings="settings" @save="onSave" />
          </div>
        </div>
      </section>
    </main>
    <AppFooter />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import AppFooter from '../../components/common/AppFooter.vue'
import SignatureSettings from '../../components/university/SignatureSettings.vue'

const settings = ref({
  ecpEnabled: true,
  qrTtlDays: 7,
  apiKey: 'sk-prod-' + Math.random().toString(36).slice(2, 14)
})

function onSave(newSettings) {
  settings.value = newSettings
  alert('Настройки сохранены')
}
</script>

<style scoped>
.settings-view { min-height: 100vh; }

.main-content { flex: 1; }

/* ===========================
   HERO
   =========================== */
.settings__hero {
  position: relative;
  background: #0d1f3c;
  padding: 72px 0 80px;
}

.settings__hero-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}

.settings__hero-grid-lines {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(
      rgba(255, 255, 255, 0.03) 1px,
      transparent 1px
    ),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.settings__hero-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.settings__hero-glow--1 {
  width: 500px;
  height: 500px;
  top: -150px;
  right: -100px;
  background: rgba(38, 75, 130, 0.45);
}

.settings__hero-glow--2 {
  width: 380px;
  height: 380px;
  bottom: -120px;
  left: -60px;
  background: rgba(72, 184, 194, 0.2);
}

.settings__hero-inner {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.settings__hero-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: var(--space-6);
}

.settings__title {
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: var(--font-weight-bold);
  color: #fff;
  line-height: 1.1;
  letter-spacing: -0.02em;
}

.settings__title-accent {
  color: var(--color-sea-clear);
}

.settings__subtitle {
  max-width: 540px;
  font-size: var(--font-size-base);
  color: rgba(255, 255, 255, 0.65);
  line-height: 1.7;
  margin: 0;
}

.settings__hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-3);
  justify-content: center;
}

.settings__btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px 24px;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  font-family: var(--font-family);
  border-radius: var(--radius-base);
  text-decoration: none;
  cursor: pointer;
  transition: all var(--transition-base);
  border: 2px solid transparent;
}

.settings__btn--primary {
  background: var(--color-main-blue);
  color: #fff;
  border-color: var(--color-main-blue);
}

.settings__btn--primary:hover {
  background: #1a5bbd;
  border-color: #1a5bbd;
}

.settings__btn--outline {
  background: transparent;
  color: #fff;
  border-color: rgba(255, 255, 255, 0.3);
}

.settings__btn--outline:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.5);
}

.settings__btn--lg {
  padding: 14px 32px;
  font-size: var(--font-size-lg);
}

/* ===========================
   STATS STRIP — параметры
   =========================== */
.settings__stats-strip {
  background: #fff;
  border-bottom: 1px solid var(--color-gray-blue);
  overflow-x: hidden;
}

.settings__stats-inner {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0;
}

.settings__stat {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-5) var(--space-5);
  border-right: 1px solid var(--color-gray-blue);
}

.settings__stat:last-child { border-right: none; }

.settings__stat-icon {
  width: 44px; height: 44px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 1.25rem;
}

.settings__stat-icon--blue { background: var(--color-pale-blue); color: var(--color-main-blue); }
.settings__stat-icon--green { background: #e6f5ee; color: var(--color-green); }
.settings__stat-icon--orange { background: #fff0e4; color: var(--color-orange); }
.settings__stat-icon--sea { background: #e4f4f5; color: var(--color-sea-dark); }

.settings__stat-text { display: flex; flex-direction: column; gap: 1px; }
.settings__stat-label { font-size: var(--font-size-sm); font-weight: var(--font-weight-semibold); color: var(--color-black); line-height: 1.3; }
.settings__stat-note { font-size: var(--font-size-xs); color: var(--color-pale-black); }

/* ===========================
   MAIN SECTION
   =========================== */
.settings__main-section {
  padding: 80px 0;
  background: #fff;
  overflow-x: hidden;
}

.settings__main-head { text-align: center; margin-bottom: var(--space-12); }

.settings__how-eyebrow {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--color-main-blue);
  margin-bottom: var(--space-2);
}

.settings__how-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  line-height: 1.2;
}

.settings__main-card {
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  max-width: 700px;
  margin: 0 auto;
}

/* ===========================
   CTA
   =========================== */
.settings__cta {
  padding: 80px 0;
  background: #0d1f3c;
  text-align: center;
}

.settings__cta-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* ===========================
   АДАПТИВ
   =========================== */
@media (max-width: 1100px) {
  .settings__stats-inner { grid-template-columns: repeat(2, 1fr); }
  .settings__stat:nth-child(2) { border-right: none; }
  .settings__stat:nth-child(1), .settings__stat:nth-child(2) { border-bottom: 1px solid var(--color-gray-blue); }
}

@media (max-width: 768px) {
  .settings__hero { padding: 44px 0 48px; }
  .settings__title { font-size: clamp(1.5rem, 6vw, 2.2rem); }

  .settings__stat {
    flex-direction: column;
    align-items: flex-start;
    gap: 2px;
    padding: var(--space-3) var(--space-4);
  }
  .settings__stat-label { font-size: var(--font-size-xs); }
  .settings__stat-note { display: none; }

  .settings__main-section { padding: 48px 0; }
  .settings__how-title { font-size: var(--font-size-2xl); }
  .settings__main-head { margin-bottom: var(--space-8); }

  .settings__cta { padding: 48px 0; }
}

@media (max-width: 540px) {
  .settings__hero { padding: 32px 0 40px; }
  .settings__title { font-size: 1.5rem; line-height: 1.2; }

  .settings__stats-inner { grid-template-columns: 1fr 1fr; }
  .settings__stat {
    flex-direction: column;
    align-items: flex-start;
    padding: var(--space-3) var(--space-3);
    gap: 2px;
  }
  .settings__stat:nth-child(1), .settings__stat:nth-child(3) { border-right: 1px solid var(--color-gray-blue); }
  .settings__stat:nth-child(2), .settings__stat:nth-child(4) { border-right: none; }
  .settings__stat-label { font-size: 11px; }
  .settings__stat-note { display: none; }

  .settings__how-title { font-size: var(--font-size-xl); }
}
</style>
