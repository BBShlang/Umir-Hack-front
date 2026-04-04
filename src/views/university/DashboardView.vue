<template>
  <div class="dashboard-view">
    <AppHeader />
    <main class="main-content">
      <!-- ===== HERO ===== -->
      <section class="dashboard__hero">
        <div class="dashboard__hero-bg" aria-hidden="true">
          <div class="dashboard__hero-grid-lines" />
          <div class="dashboard__hero-glow dashboard__hero-glow--1" />
          <div class="dashboard__hero-glow dashboard__hero-glow--2" />
        </div>

        <div class="container dashboard__hero-inner">
          <div class="dashboard__hero-content">
            <h1 class="dashboard__title">
              Панель управления<br />
              <span class="dashboard__title-accent">Ваш ВУЗ</span>
            </h1>
            <p class="dashboard__subtitle">
              Управляйте реестром дипломов, загружайте новые данные и отслеживайте статус выпускников в реальном времени.
            </p>
            <div class="dashboard__hero-actions">
              <router-link to="/university/upload" class="dashboard__btn dashboard__btn--primary">Загрузить реестр</router-link>
              <router-link to="/university/diplomas" class="dashboard__btn dashboard__btn--outline">Перейти к реестру</router-link>
            </div>
          </div>
        </div>
      </section>

      <!-- ===== Возможности ===== -->
      <section class="dashboard__how">
        <div class="container">
          <div class="dashboard__how-head">
            <p class="dashboard__how-eyebrow">Быстрые действия</p>
            <h2 class="dashboard__how-title">Что вы можете сделать?</h2>
          </div>

          <div class="dashboard__steps">
            <div class="dashboard__step" @click="$router.push('/university/upload')">
              <div class="dashboard__step-icon dashboard__step-icon--blue">📤</div>
              <div class="dashboard__step-num">01</div>
              <h3 class="dashboard__step-title">Загрузить реестр</h3>
              <p class="dashboard__step-desc">
                Импортируйте CSV/Excel файл с данными выпускников. Хэши генерируются автоматически.
              </p>
            </div>

            <div class="dashboard__step" @click="$router.push('/university/diplomas')">
              <div class="dashboard__step-icon dashboard__step-icon--green">📋</div>
              <div class="dashboard__step-num">02</div>
              <h3 class="dashboard__step-title">Управление дипломами</h3>
              <p class="dashboard__step-desc">
                Просмотр, поиск и отзыв дипломов. Управление хэшами и статусами записей.
              </p>
            </div>

            <div class="dashboard__step" @click="$router.push('/university/settings')">
              <div class="dashboard__step-icon dashboard__step-icon--orange">⚙️</div>
              <div class="dashboard__step-num">03</div>
              <h3 class="dashboard__step-title">Настройки</h3>
              <p class="dashboard__step-desc">
                Конфигурация ЭЦП, API-ключей, параметров QR-кодов и безопасности.
              </p>
            </div>
          </div>
        </div>
      </section>

      <!-- ===== Последние загрузки ===== -->
      <section class="dashboard__recent">
        <div class="container">
          <div class="dashboard__recent-head">
            <p class="dashboard__how-eyebrow">Активность</p>
            <h2 class="dashboard__how-title">Последние загрузки</h2>
          </div>

          <div class="dashboard__recent-table">
            <table>
              <thead>
                <tr>
                  <th>Дата</th>
                  <th>Файл</th>
                  <th>Записей</th>
                  <th>Статус</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in recentUploads" :key="item.id">
                  <td>{{ item.date }}</td>
                  <td class="filename">{{ item.fileName }}</td>
                  <td>{{ item.count }}</td>
                  <td>
                    <span class="status-badge" :class="'status-' + item.status">
                      {{ item.status === 'active' ? 'Активен' : 'Отозван' }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </section>
    </main>
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import AppFooter from '../../components/common/AppFooter.vue'

const stats = ref({ total: 0, active: 0, revoked: 0, pending: 0 })
const recentUploads = ref([])

onMounted(() => {
  setTimeout(() => {
    stats.value = { total: 1250, active: 1180, revoked: 45, pending: 25 }
    recentUploads.value = [
      { id: 1, date: '01.04.2026', fileName: 'registry_spring_2026.csv', count: 340, status: 'active' },
      { id: 2, date: '15.03.2026', fileName: 'registry_additional.xlsx', count: 89, status: 'active' },
      { id: 3, date: '01.02.2026', fileName: 'registry_winter.csv', count: 275, status: 'revoked' }
    ]
  }, 600)
})
</script>

<style scoped>
.dashboard-view { min-height: 100vh; }

.main-content { flex: 1; }

/* ===========================
   HERO
   =========================== */
.dashboard__hero {
  position: relative;
  background: #0d1f3c;
  padding: 72px 0 80px;
}

.dashboard__hero-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}

.dashboard__hero-grid-lines {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(
      rgba(255, 255, 255, 0.03) 1px,
      transparent 1px
    ),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.dashboard__hero-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.dashboard__hero-glow--1 {
  width: 500px;
  height: 500px;
  top: -150px;
  right: -100px;
  background: rgba(38, 75, 130, 0.45);
}

.dashboard__hero-glow--2 {
  width: 380px;
  height: 380px;
  bottom: -120px;
  left: -60px;
  background: rgba(72, 184, 194, 0.2);
}

.dashboard__hero-inner {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.dashboard__hero-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: var(--space-6);
}

.dashboard__title {
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: var(--font-weight-bold);
  color: #fff;
  line-height: 1.1;
  letter-spacing: -0.02em;
}

.dashboard__title-accent {
  color: var(--color-sea-clear);
}

.dashboard__subtitle {
  max-width: 540px;
  font-size: var(--font-size-base);
  color: rgba(255, 255, 255, 0.65);
  line-height: 1.7;
  margin: 0;
}

.dashboard__hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-3);
  justify-content: center;
}

.dashboard__btn {
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

.dashboard__btn--primary {
  background: var(--color-main-blue);
  color: #fff;
  border-color: var(--color-main-blue);
}

.dashboard__btn--primary:hover {
  background: #1a5bbd;
  border-color: #1a5bbd;
}

.dashboard__btn--outline {
  background: transparent;
  color: #fff;
  border-color: rgba(255, 255, 255, 0.3);
}

.dashboard__btn--outline:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.5);
}

.dashboard__btn--lg {
  padding: 14px 32px;
  font-size: var(--font-size-lg);
}

/* ===========================
   STATS STRIP
   =========================== */
.dashboard__stats-strip {
  background: #fff;
  border-bottom: 1px solid var(--color-gray-blue);
  overflow-x: hidden;
}

.dashboard__stats-inner {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0;
}

.dashboard__stat {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-5) var(--space-5);
  border-right: 1px solid var(--color-gray-blue);
}

.dashboard__stat:last-child { border-right: none; }

.dashboard__stat-value {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-main-blue);
  white-space: nowrap;
  flex-shrink: 0;
}

.dashboard__stat-value:nth-child(2) { color: #16a34a; }

.dashboard__stat-text { display: flex; flex-direction: column; gap: 1px; }
.dashboard__stat-label { font-size: var(--font-size-sm); font-weight: var(--font-weight-semibold); color: var(--color-black); line-height: 1.3; }
.dashboard__stat-note { font-size: var(--font-size-xs); color: var(--color-pale-black); }

/* ===========================
   ШАГИ
   =========================== */
.dashboard__how {
  padding: 80px 0;
  background: #fff;
  overflow-x: hidden;
}

.dashboard__how-head { text-align: center; margin-bottom: var(--space-12); }

.dashboard__how-eyebrow {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--color-main-blue);
  margin-bottom: var(--space-2);
}

.dashboard__how-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  line-height: 1.2;
}

.dashboard__steps {
  display: flex;
  align-items: stretch;
  gap: var(--space-4);
}

.dashboard__step {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
  padding: var(--space-6);
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  background: #fff;
  position: relative;
  overflow: visible;
  transition: box-shadow var(--transition-base), border-color var(--transition-base);
  cursor: pointer;
}

.dashboard__step:hover {
  box-shadow: var(--shadow-md);
  border-color: var(--color-main-blue);
}

.dashboard__step::after {
  content: "→";
  position: absolute;
  right: calc(-1 * var(--space-4) / 2 - 7px);
  top: 50%;
  transform: translateY(-50%);
  font-size: 14px;
  color: var(--color-gray-light);
  pointer-events: none;
  z-index: 2;
}

.dashboard__step:last-child::after { display: none; }

.dashboard__step-icon {
  width: 44px; height: 44px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 1.25rem;
}

.dashboard__step-icon--blue { background: var(--color-pale-blue); color: var(--color-main-blue); }
.dashboard__step-icon--green { background: #e6f5ee; color: var(--color-green); }
.dashboard__step-icon--orange { background: #fff0e4; color: var(--color-orange); }

.dashboard__step-num {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-bold);
  letter-spacing: 0.08em;
  color: var(--color-pale-black);
  text-transform: uppercase;
}

.dashboard__step-title { font-size: var(--font-size-md); font-weight: var(--font-weight-bold); color: var(--color-black); line-height: 1.3; }
.dashboard__step-desc { font-size: var(--font-size-sm); color: var(--color-pale-black); line-height: 1.6; }

/* ===========================
   RECENT
   =========================== */
.dashboard__recent {
  padding: 80px 0;
  background: #f8fafc;
  overflow-x: hidden;
}

.dashboard__recent-head { text-align: center; margin-bottom: var(--space-8); }

.dashboard__recent-table {
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.dashboard__recent-table table {
  width: 100%;
  border-collapse: collapse;
}

.dashboard__recent-table thead {
  background: var(--color-pale-blue);
}

.dashboard__recent-table th {
  padding: var(--space-3) var(--space-4);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--color-main-blue);
  text-align: left;
}

.dashboard__recent-table td {
  padding: var(--space-3) var(--space-4);
  font-size: var(--font-size-sm);
  color: var(--color-black);
  border-top: 1px solid var(--color-gray-blue);
}

.dashboard__recent-table tbody tr:hover {
  background: #f8fafc;
}

.dashboard__recent-table .filename {
  font-family: monospace;
  color: var(--color-main-blue);
}

.status-badge {
  display: inline-block;
  padding: 2px 10px;
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
}

.status-active {
  background: #e6f5ee;
  color: #16a34a;
}

.status-revoked {
  background: #fde8e8;
  color: #dc2626;
}

/* ===========================
   CTA
   =========================== */
.dashboard__cta {
  padding: 80px 0;
  background: #0d1f3c;
  text-align: center;
}

.dashboard__cta-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* ===========================
   АДАПТИВ
   =========================== */
@media (max-width: 1100px) {
  .dashboard__stats-inner { grid-template-columns: repeat(2, 1fr); }
  .dashboard__stat:nth-child(2) { border-right: none; }
  .dashboard__stat:nth-child(1), .dashboard__stat:nth-child(2) { border-bottom: 1px solid var(--color-gray-blue); }
}

@media (max-width: 768px) {
  .dashboard__hero { padding: 44px 0 48px; }
  .dashboard__title { font-size: clamp(1.5rem, 6vw, 2.2rem); }

  .dashboard__stat {
    flex-direction: column;
    align-items: flex-start;
    gap: 2px;
    padding: var(--space-3) var(--space-4);
  }
  .dashboard__stat-value { font-size: var(--font-size-base); }
  .dashboard__stat-note { display: none; }

  .dashboard__steps { flex-direction: column; gap: var(--space-3); }
  .dashboard__step { padding: var(--space-4); }
  .dashboard__step::after { display: none; }

  .dashboard__how, .dashboard__cta { padding: 48px 0; }
  .dashboard__how-title { font-size: var(--font-size-2xl); }
  .dashboard__how-head { margin-bottom: var(--space-8); }
  .dashboard__recent { padding: 48px 0; }
  .dashboard__recent-table { overflow-x: auto; }
}

@media (max-width: 540px) {
  .dashboard__hero { padding: 32px 0 40px; }
  .dashboard__title { font-size: 1.5rem; line-height: 1.2; }

  .dashboard__stats-inner { grid-template-columns: 1fr 1fr; }
  .dashboard__stat {
    flex-direction: column;
    align-items: flex-start;
    padding: var(--space-3) var(--space-3);
    gap: 2px;
  }
  .dashboard__stat:nth-child(1), .dashboard__stat:nth-child(3) { border-right: 1px solid var(--color-gray-blue); }
  .dashboard__stat:nth-child(2), .dashboard__stat:nth-child(4) { border-right: none; }
  .dashboard__stat-value { font-size: var(--font-size-base); white-space: normal; }
  .dashboard__stat-label { font-size: 11px; }
  .dashboard__stat-note { display: none; }

  .dashboard__how-title { font-size: var(--font-size-xl); }
}
</style>
