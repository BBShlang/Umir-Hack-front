<template>
  <div class="upload-registry-view">
    <AppHeader />
    <main class="main-content">
      <!-- ===== HERO ===== -->
      <section class="upload__hero">
        <div class="upload__hero-bg" aria-hidden="true">
          <div class="upload__hero-grid-lines" />
          <div class="upload__hero-glow upload__hero-glow--1" />
          <div class="upload__hero-glow upload__hero-glow--2" />
        </div>

        <div class="container upload__hero-inner">
          <div class="upload__hero-content">
            <h1 class="upload__title">
              Загрузка реестра<br />
              <span class="upload__title-accent">CSV / Excel</span>
            </h1>
            <p class="upload__subtitle">
              Загрузите файл с данными о выпускниках. Система автоматически сгенерирует хэши и зарегистрирует дипломы в блокчейне.
            </p>
            <div class="upload__hero-actions">
              <router-link to="/university/diplomas" class="upload__btn upload__btn--outline">Перейти к реестру</router-link>
              <router-link to="/university/dashboard" class="upload__btn upload__btn--outline">На дашборд</router-link>
            </div>
          </div>
        </div>
      </section>

      <!-- ===== Загрузчик ===== -->
      <section class="upload__uploader-section">
        <div class="container">
          <div class="upload__uploader-head">
            <p class="upload__how-eyebrow">Импорт данных</p>
            <h2 class="upload__how-title">Загрузите файл реестра</h2>
          </div>
          <div class="upload__uploader-card">
            <RegistryUploader @import-complete="onImportComplete" @error="onError" />
          </div>
        </div>
      </section>
    </main>
    <AppFooter />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '../../components/common/AppHeader.vue'
import AppFooter from '../../components/common/AppFooter.vue'
import RegistryUploader from '../../components/university/RegistryUploader.vue'
import { useAuth } from '../../composables/useAuth.js'

const router = useRouter()
const { user } = useAuth()

const uploadLog = ref([])
let logId = 1

function addLog(message, type = 'info') {
  uploadLog.value.unshift({
    id: logId++,
    time: new Date().toLocaleTimeString('ru-RU'),
    message,
    type
  })
}

function onImportComplete(result) {
  addLog(`Импорт завершён: ${result.importedRows} из ${result.totalRows} записей`, 'success')
  
  if (result.failedRows > 0) {
    addLog(`Не удалось импортировать: ${result.failedRows} записей`, 'error')
  }

  if (result.errors?.length) {
    result.errors.forEach(err => {
      addLog(`Строка ${err.rowNumber}: ${err.message}`, 'error')
    })
  }

  // Перенаправляем на страницу реестра через 2 секунды
  setTimeout(() => {
    router.push('/university/diplomas')
  }, 2000)
}

function onError(err) {
  addLog(`Ошибка: ${err}`, 'error')
}
</script>

<style scoped>
.upload-registry-view { min-height: 100vh; }

.main-content { flex: 1; }

/* ===========================
   HERO
   =========================== */
.upload__hero {
  position: relative;
  background: #0d1f3c;
  padding: 72px 0 80px;
}

.upload__hero-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}

.upload__hero-grid-lines {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(
      rgba(255, 255, 255, 0.03) 1px,
      transparent 1px
    ),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.upload__hero-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.upload__hero-glow--1 {
  width: 500px;
  height: 500px;
  top: -150px;
  right: -100px;
  background: rgba(38, 75, 130, 0.45);
}

.upload__hero-glow--2 {
  width: 380px;
  height: 380px;
  bottom: -120px;
  left: -60px;
  background: rgba(72, 184, 194, 0.2);
}

.upload__hero-inner {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.upload__hero-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: var(--space-6);
}

.upload__title {
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: var(--font-weight-bold);
  color: #fff;
  line-height: 1.1;
  letter-spacing: -0.02em;
}

.upload__title-accent {
  color: var(--color-sea-clear);
}

.upload__subtitle {
  max-width: 540px;
  font-size: var(--font-size-base);
  color: rgba(255, 255, 255, 0.65);
  line-height: 1.7;
  margin: 0;
}

.upload__hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-3);
  justify-content: center;
}

.upload__btn {
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

.upload__btn--primary {
  background: var(--color-main-blue);
  color: #fff;
  border-color: var(--color-main-blue);
}

.upload__btn--primary:hover {
  background: #1a5bbd;
  border-color: #1a5bbd;
}

.upload__btn--outline {
  background: transparent;
  color: #fff;
  border-color: rgba(255, 255, 255, 0.3);
}

.upload__btn--outline:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.5);
}

.upload__btn--lg {
  padding: 14px 32px;
  font-size: var(--font-size-lg);
}

/* ===========================
   STATS STRIP — шаги
   =========================== */
.upload__stats-strip {
  background: #fff;
  border-bottom: 1px solid var(--color-gray-blue);
  overflow-x: hidden;
}

.upload__stats-inner {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0;
}

.upload__stat {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-5) var(--space-5);
  border-right: 1px solid var(--color-gray-blue);
}

.upload__stat:last-child { border-right: none; }

.upload__step-icon {
  width: 44px; height: 44px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 1.25rem;
}

.upload__step-icon--blue { background: var(--color-pale-blue); color: var(--color-main-blue); }
.upload__step-icon--green { background: #e6f5ee; color: var(--color-green); }
.upload__step-icon--orange { background: #fff0e4; color: var(--color-orange); }
.upload__step-icon--sea { background: #e4f4f5; color: var(--color-sea-dark); }

.upload__stat-text { display: flex; flex-direction: column; gap: 1px; }
.upload__stat-label { font-size: var(--font-size-sm); font-weight: var(--font-weight-semibold); color: var(--color-black); line-height: 1.3; }
.upload__stat-note { font-size: var(--font-size-xs); color: var(--color-pale-black); }

/* ===========================
   UPLOADER SECTION
   =========================== */
.upload__uploader-section {
  padding: 80px 0;
  background: #fff;
  overflow-x: hidden;
}

.upload__uploader-head { text-align: center; margin-bottom: var(--space-12); }

.upload__how-eyebrow {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--color-main-blue);
  margin-bottom: var(--space-2);
}

.upload__how-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  line-height: 1.2;
}

.upload__uploader-card {
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  max-width: 700px;
  margin: 0 auto;
}

/* ===========================
   LOG
   =========================== */
.upload__log-section {
  padding: 80px 0;
  background: #f8fafc;
  overflow-x: hidden;
}

.upload__log-head { text-align: center; margin-bottom: var(--space-8); }

.upload__log-list {
  background: #1e293b;
  color: #e2e8f0;
  padding: var(--space-4);
  border-radius: var(--radius-lg);
  font-family: monospace;
  font-size: var(--font-size-xs);
  max-height: 400px;
  overflow-y: auto;
}

.upload__log-entry {
  display: flex;
  gap: var(--space-3);
  padding: var(--space-1) 0;
}

.upload__log-time {
  color: #64748b;
  flex-shrink: 0;
}

.upload__log-message.success { color: #4ade80; }
.upload__log-message.error { color: #f87171; }

/* ===========================
   CTA
   =========================== */
.upload__cta {
  padding: 80px 0;
  background: #0d1f3c;
  text-align: center;
}

.upload__cta-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* ===========================
   АДАПТИВ
   =========================== */
@media (max-width: 1100px) {
  .upload__stats-inner { grid-template-columns: repeat(2, 1fr); }
  .upload__stat:nth-child(2) { border-right: none; }
  .upload__stat:nth-child(1), .upload__stat:nth-child(2) { border-bottom: 1px solid var(--color-gray-blue); }
}

@media (max-width: 768px) {
  .upload__hero { padding: 44px 0 48px; }
  .upload__title { font-size: clamp(1.5rem, 6vw, 2.2rem); }

  .upload__stat {
    flex-direction: column;
    align-items: flex-start;
    gap: 2px;
    padding: var(--space-3) var(--space-4);
  }
  .upload__stat-label { font-size: var(--font-size-xs); }
  .upload__stat-note { display: none; }

  .upload__uploader-section, .upload__log-section { padding: 48px 0; }
  .upload__how-title { font-size: var(--font-size-2xl); }
  .upload__uploader-head, .upload__log-head { margin-bottom: var(--space-8); }

  .upload__cta { padding: 48px 0; }
}

@media (max-width: 540px) {
  .upload__hero { padding: 32px 0 40px; }
  .upload__title { font-size: 1.5rem; line-height: 1.2; }

  .upload__stats-inner { grid-template-columns: 1fr 1fr; }
  .upload__stat {
    flex-direction: column;
    align-items: flex-start;
    padding: var(--space-3) var(--space-3);
    gap: 2px;
  }
  .upload__stat:nth-child(1), .upload__stat:nth-child(3) { border-right: 1px solid var(--color-gray-blue); }
  .upload__stat:nth-child(2), .upload__stat:nth-child(4) { border-right: none; }
  .upload__stat-label { font-size: 11px; }
  .upload__stat-note { display: none; }

  .upload__how-title { font-size: var(--font-size-xl); }
}
</style>
