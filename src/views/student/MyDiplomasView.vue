<template>
  <div class="my-diplomas-view">
    <AppHeader />
    <main class="main-content">
      <!-- ===== HERO ===== -->
      <section class="diplomas__hero">
        <div class="diplomas__hero-bg" aria-hidden="true">
          <div class="diplomas__hero-grid-lines" />
          <div class="diplomas__hero-glow diplomas__hero-glow--1" />
          <div class="diplomas__hero-glow diplomas__hero-glow--2" />
        </div>

        <div class="container diplomas__hero-inner">
          <div class="diplomas__hero-content">
            <h1 class="diplomas__title">
              Мои дипломы<br />
              <span class="diplomas__title-accent">Цифровые сертификаты</span>
            </h1>
            <p class="diplomas__subtitle">
              Скачивайте документы, делитесь с работодателями и отслеживайте проверки ваших дипломов.
            </p>
            <div class="diplomas__hero-actions">
              <router-link to="/student/dashboard" class="diplomas__btn diplomas__btn--outline">На дашборд</router-link>
              <router-link to="/student/share" class="diplomas__btn diplomas__btn--outline">Поделиться дипломом</router-link>
            </div>
          </div>
        </div>
      </section>

      <!-- ===== Состояния загрузки и пустоты ===== -->
      <section v-if="loading" class="diplomas__loading-section">
        <div class="container">
          <div class="diplomas__loading-state">
            <div class="spinner" aria-hidden="true"></div>
            <p>Загрузка дипломов...</p>
          </div>
        </div>
      </section>

      <section v-else-if="!diplomas.length" class="diplomas__empty-section">
        <div class="container">
          <div class="diplomas__empty-state">
            <div class="diplomas__empty-icon">📄</div>
            <h2 class="diplomas__how-title">У вас пока нет дипломов</h2>
            <p class="diplomas__subtitle">Ваш ВУЗ ещё не загрузил ваши данные в реестр</p>
            <router-link to="/student/dashboard" class="diplomas__btn diplomas__btn--primary">На дашборд</router-link>
          </div>
        </div>
      </section>

      <!-- ===== Карточки дипломов ===== -->
      <section v-else class="diplomas__cards-section">
        <div class="container">
          <div class="diplomas__cards-head">
            <p class="diplomas__how-eyebrow">Документы</p>
            <h2 class="diplomas__how-title">Ваши дипломы</h2>
          </div>

          <div class="diplomas__cards-grid">
            <MyDiplomaCard
              v-for="diploma in diplomas"
              :key="diploma.id"
              :diploma="diploma"
              @download="onDownload(diploma)"
              @share="onShare(diploma)"
            />
          </div>
        </div>
      </section>

      <!-- ===== Трекер верификаций ===== -->
      <section v-if="diplomas.length" class="diplomas__tracker-section">
        <div class="container">
          <div class="diplomas__tracker-head">
            <p class="diplomas__how-eyebrow">Мониторинг</p>
            <h2 class="diplomas__how-title">Последние проверки</h2>
          </div>
          <div class="diplomas__tracker-card">
            <VerificationTracker :diploma-id="diplomas[0]?.id" />
          </div>
        </div>
      </section>

    </main>
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppFooter from '../../components/common/AppFooter.vue'
import MyDiplomaCard from '../../components/student/MyDiplomaCard.vue'
import VerificationTracker from '../../components/student/VerificationTracker.vue'

const router = useRouter()

const loading = ref(true)
const diplomas = ref([])

const activeCount = computed(() => diplomas.value.filter(d => d.status === 'active').length)
const downloadsCount = computed(() => diplomas.value.length * 3) // Заглушка

onMounted(() => {
  setTimeout(() => {
    diplomas.value = [
      {
        id: 1,
        serialNumber: 'DIP-0000001234',
        studentName: 'Иванов Иван Иванович',
        specialty: 'Информатика и вычислительная техника',
        degree: 'Бакалавр',
        university: 'МГТУ им. Баумана',
        issueDate: '2025-06-15',
        status: 'active',
        hash: 'a1b2c3d4e5f6789012345678901234567890abcdef1234567890abcdef123456'
      },
      {
        id: 2,
        serialNumber: 'DIP-0000005678',
        studentName: 'Иванов Иван Иванович',
        specialty: 'Прикладная математика',
        degree: 'Магистр',
        university: 'МГУ',
        issueDate: '2023-06-20',
        status: 'active',
        hash: 'fedcba0987654321fedcba0987654321fedcba0987654321fedcba0987654321'
      }
    ]
    loading.value = false
  }, 600)
})

function onDownload(diploma) {
  alert(`Скачивание сертификата для ${diploma.serialNumber}`)
}

function onShare(diploma) {
  router.push({ name: 'student-share', params: { id: diploma.id } })
}
</script>

<style scoped>
.my-diplomas-view { min-height: 100vh; }

.main-content { flex: 1; }

/* ===========================
   HERO
   =========================== */
.diplomas__hero {
  position: relative;
  background: #0d1f3c;
  padding: 72px 0 80px;
}

.diplomas__hero-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}

.diplomas__hero-grid-lines {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(
      rgba(255, 255, 255, 0.03) 1px,
      transparent 1px
    ),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.diplomas__hero-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.diplomas__hero-glow--1 {
  width: 500px;
  height: 500px;
  top: -150px;
  right: -100px;
  background: rgba(38, 75, 130, 0.45);
}

.diplomas__hero-glow--2 {
  width: 380px;
  height: 380px;
  bottom: -120px;
  left: -60px;
  background: rgba(72, 184, 194, 0.2);
}

.diplomas__hero-inner {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.diplomas__hero-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: var(--space-6);
}

.diplomas__title {
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: var(--font-weight-bold);
  color: #fff;
  line-height: 1.1;
  letter-spacing: -0.02em;
}

.diplomas__title-accent {
  color: var(--color-sea-clear);
}

.diplomas__subtitle {
  max-width: 540px;
  font-size: var(--font-size-base);
  color: rgba(255, 255, 255, 0.65);
  line-height: 1.7;
  margin: 0;
}

.diplomas__hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-3);
  justify-content: center;
}

.diplomas__btn {
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

.diplomas__btn--primary {
  background: var(--color-main-blue);
  color: #fff;
  border-color: var(--color-main-blue);
}

.diplomas__btn--primary:hover {
  background: #1a5bbd;
  border-color: #1a5bbd;
}

.diplomas__btn--outline {
  background: transparent;
  color: #fff;
  border-color: rgba(255, 255, 255, 0.3);
}

.diplomas__btn--outline:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.5);
}

.diplomas__btn--lg {
  padding: 14px 32px;
  font-size: var(--font-size-lg);
}

/* ===========================
   STATS STRIP
   =========================== */
.diplomas__stats-strip {
  background: #fff;
  border-bottom: 1px solid var(--color-gray-blue);
  overflow-x: hidden;
}

.diplomas__stats-inner {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0;
}

.diplomas__stat {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-5) var(--space-5);
  border-right: 1px solid var(--color-gray-blue);
}

.diplomas__stat:last-child { border-right: none; }

.diplomas__stat-value {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-main-blue);
  white-space: nowrap;
  flex-shrink: 0;
}

.diplomas__stat-text { display: flex; flex-direction: column; gap: 1px; }
.diplomas__stat-label { font-size: var(--font-size-sm); font-weight: var(--font-weight-semibold); color: var(--color-black); line-height: 1.3; }
.diplomas__stat-note { font-size: var(--font-size-xs); color: var(--color-pale-black); }

/* ===========================
   LOADING
   =========================== */
.diplomas__loading-section {
  padding: 80px 0;
  background: #f8fafc;
}

.diplomas__loading-state {
  padding: 3rem;
  text-align: center;
  color: #94a3b8;
  background: #fff;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-gray-blue);
}

.spinner {
  width: 40px;
  height: 40px;
  margin: 0 auto 1rem;
  border: 3px solid var(--color-gray-blue);
  border-top-color: var(--color-main-blue);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ===========================
   EMPTY
   =========================== */
.diplomas__empty-section {
  padding: 80px 0;
  background: #f8fafc;
}

.diplomas__empty-state {
  padding: 3rem;
  text-align: center;
  background: #fff;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-gray-blue);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
}

.diplomas__empty-icon {
  font-size: 3rem;
  opacity: 0.5;
}

/* ===========================
   CARDS SECTION
   =========================== */
.diplomas__cards-section {
  padding: 80px 0;
  background: #fff;
  overflow-x: hidden;
}

.diplomas__cards-head { text-align: center; margin-bottom: var(--space-12); }

.diplomas__how-eyebrow {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--color-main-blue);
  margin-bottom: var(--space-2);
}

.diplomas__how-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  line-height: 1.2;
}

.diplomas__cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: var(--space-6);
}

/* ===========================
   TRACKER
   =========================== */
.diplomas__tracker-section {
  padding: 80px 0;
  background: #f8fafc;
  overflow-x: hidden;
}

.diplomas__tracker-head { text-align: center; margin-bottom: var(--space-8); }

.diplomas__tracker-card {
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
.diplomas__cta {
  padding: 80px 0;
  background: #0d1f3c;
  text-align: center;
}

.diplomas__cta-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* ===========================
   АДАПТИВ
   =========================== */
@media (max-width: 1100px) {
  .diplomas__stats-inner { grid-template-columns: repeat(2, 1fr); }
  .diplomas__stat:nth-child(2) { border-right: none; }
  .diplomas__stat:nth-child(1), .diplomas__stat:nth-child(2) { border-bottom: 1px solid var(--color-gray-blue); }
}

@media (max-width: 768px) {
  .diplomas__hero { padding: 44px 0 48px; }
  .diplomas__title { font-size: clamp(1.5rem, 6vw, 2.2rem); }

  .diplomas__stat {
    flex-direction: column;
    align-items: flex-start;
    gap: 2px;
    padding: var(--space-3) var(--space-4);
  }
  .diplomas__stat-value { font-size: var(--font-size-base); }
  .diplomas__stat-note { display: none; }

  .diplomas__cards-section, .diplomas__tracker-section { padding: 48px 0; }
  .diplomas__how-title { font-size: var(--font-size-2xl); }
  .diplomas__cards-head, .diplomas__tracker-head { margin-bottom: var(--space-8); }

  .diplomas__cards-grid {
    grid-template-columns: 1fr;
  }

  .diplomas__cta { padding: 48px 0; }
}

@media (max-width: 540px) {
  .diplomas__hero { padding: 32px 0 40px; }
  .diplomas__title { font-size: 1.5rem; line-height: 1.2; }

  .diplomas__stats-inner { grid-template-columns: 1fr 1fr; }
  .diplomas__stat {
    flex-direction: column;
    align-items: flex-start;
    padding: var(--space-3) var(--space-3);
    gap: 2px;
  }
  .diplomas__stat:nth-child(1), .diplomas__stat:nth-child(3) { border-right: 1px solid var(--color-gray-blue); }
  .diplomas__stat:nth-child(2), .diplomas__stat:nth-child(4) { border-right: none; }
  .diplomas__stat-value { font-size: var(--font-size-base); white-space: normal; }
  .diplomas__stat-label { font-size: 11px; }
  .diplomas__stat-note { display: none; }

  .diplomas__how-title { font-size: var(--font-size-xl); }
}
</style>
