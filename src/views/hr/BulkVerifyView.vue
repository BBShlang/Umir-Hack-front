<template>
  <div class="bulk-verify-view">
    <AppHeader />

    <main class="main-content">
      <!-- ===== HERO ===== -->
      <section class="bulk__hero">
        <div class="bulk__hero-bg" aria-hidden="true">
          <div class="bulk__hero-grid-lines" />
          <div class="bulk__hero-glow bulk__hero-glow--1" />
          <div class="bulk__hero-glow bulk__hero-glow--2" />
        </div>

        <div class="container bulk__hero-inner">
          <div class="bulk__hero-content">
            <h1 class="bulk__hero-title">
              Массовая проверка<br />
              <span class="bulk__hero-title-accent">Пакетная верификация</span>
            </h1>

            <p class="bulk__hero-subtitle">
              Загрузите список кандидатов через CSV/Excel и получите результаты проверки всех дипломов за несколько минут.
            </p>

            <div class="bulk__hero-actions">
              <router-link to="/hr/verify" class="bulk__btn bulk__btn--outline">Одиночная проверка</router-link>
              <router-link to="/hr/reports" class="bulk__btn bulk__btn--outline">Отчёты</router-link>
            </div>

            <div class="bulk__examples">
              <span class="bulk__examples-label">Поддерживаемые форматы:</span>
              <div class="bulk__examples-list">
                <span class="bulk__example-btn">.CSV</span>
                <span class="bulk__example-btn">.XLSX</span>
                <span class="bulk__example-btn">.XLS</span>
              </div>
            </div>
          </div>
        </div>
      </section>


      <!-- ===== Основная рабочая зона ===== -->
      <section class="bulk__workspace">
        <div class="container">
          <div class="bulk__workspace-head">
            <p class="bulk__workspace-eyebrow">Пакетная обработка</p>
            <h2 class="bulk__workspace-title">Загрузите список кандидатов</h2>
          </div>

          <div class="bulk__workspace-grid">
            <!-- Левая колонка: ввод данных -->
            <div class="bulk__workspace-panel">
              <div class="bulk__panel-header">
                <div class="bulk__panel-icon">
                  <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <path d="M10 3v10m0 0l-3-3m3 3l3-3M4 13v2a2 2 0 002 2h8a2 2 0 002-2v-2" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
                <div class="bulk__panel-title-wrap">
                  <h3 class="bulk__panel-title">Ввод данных</h3>
                  <p class="bulk__panel-desc">Введите серийные номера или загрузите файл</p>
                </div>
              </div>

              <div class="bulk__checker-card">
                <BulkChecker @view-detail="onViewDetail" />
              </div>
            </div>

            <!-- Правая колонка: инструкция -->
            <div class="bulk__workspace-sidebar">
              <div class="bulk__sidebar-card">
                <h4 class="bulk__sidebar-title">Требования к файлу</h4>
                <ul class="bulk__sidebar-list">
                  <li>
                    <span class="bulk__sidebar-list-icon">
                      <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                        <circle cx="8" cy="8" r="6" stroke="currentColor" stroke-width="1.4"/>
                        <path d="M5.5 8l2 2 3-3.5" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                    </span>
                    <span>Форматы: CSV, XLS, XLSX</span>
                  </li>
                  <li>
                    <span class="bulk__sidebar-list-icon">
                      <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                        <circle cx="8" cy="8" r="6" stroke="currentColor" stroke-width="1.4"/>
                        <path d="M5.5 8l2 2 3-3.5" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                    </span>
                    <span>До 1000 записей за раз</span>
                  </li>
                  <li>
                    <span class="bulk__sidebar-list-icon">
                      <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                        <circle cx="8" cy="8" r="6" stroke="currentColor" stroke-width="1.4"/>
                        <path d="M5.5 8l2 2 3-3.5" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                    </span>
                    <span>Колонки: ФИО, Номер диплома</span>
                  </li>
                  <li>
                    <span class="bulk__sidebar-list-icon">
                      <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                        <circle cx="8" cy="8" r="6" stroke="currentColor" stroke-width="1.4"/>
                        <path d="M5.5 8l2 2 3-3.5" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                    </span>
                    <span>Кодировка UTF-8</span>
                  </li>
                </ul>
              </div>

              <div class="bulk__sidebar-card bulk__sidebar-card--accent">
                <h4 class="bulk__sidebar-title">Быстрые действия</h4>
                <div class="bulk__sidebar-actions">
                  <router-link to="/hr/verify" class="bulk__sidebar-btn">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                      <circle cx="7" cy="7" r="5" stroke="currentColor" stroke-width="1.4"/>
                      <path d="M11 11l3.5 3.5" stroke="currentColor" stroke-width="1.4" stroke-linecap="round"/>
                    </svg>
                    Одиночная проверка
                  </router-link>
                  <router-link to="/hr/reports" class="bulk__sidebar-btn">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                      <rect x="3" y="2" width="10" height="12" rx="1.5" stroke="currentColor" stroke-width="1.4"/>
                      <path d="M6 6h4M6 9h4M6 12h2" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/>
                    </svg>
                    Отчёты
                  </router-link>
                  <router-link to="/api-docs" class="bulk__sidebar-btn">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                      <path d="M8 2L2 5v6l6 3 6-3V5L8 2z" stroke="currentColor" stroke-width="1.4" stroke-linejoin="round"/>
                      <path d="M6 8l1.5 1.5L10 7" stroke="currentColor" stroke-width="1.3" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    API документация
                  </router-link>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>

    <AppFooter />
  </div>
</template>

<script setup>
import AppFooter from '../../components/common/AppFooter.vue'
import BulkChecker from '../../components/hr/BulkChecker.vue'

function onViewDetail(item) {
  alert(`Детали: ${item.serial} — ${item.status}`)
}
</script>

<style scoped>
.bulk-verify-view { min-height: 100vh; }

/* ===========================
   HERO
   =========================== */
.bulk__hero {
  position: relative;
  background: #0d1f3c;
  padding: 72px 0 80px;
}

.bulk__hero-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}

.bulk__hero-grid-lines {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(
      rgba(255, 255, 255, 0.03) 1px,
      transparent 1px
    ),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.bulk__hero-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.bulk__hero-glow--1 {
  width: 500px;
  height: 500px;
  top: -150px;
  right: -100px;
  background: rgba(38, 75, 130, 0.45);
}

.bulk__hero-glow--2 {
  width: 380px;
  height: 380px;
  bottom: -120px;
  left: -60px;
  background: rgba(72, 184, 194, 0.2);
}

.bulk__hero-inner {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.bulk__hero-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: var(--space-6);
}

.bulk__hero-title {
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: var(--font-weight-bold);
  color: #fff;
  line-height: 1.1;
  letter-spacing: -0.02em;
}

.bulk__hero-title-accent {
  color: var(--color-sea-clear);
}

.bulk__hero-subtitle {
  max-width: 540px;
  font-size: var(--font-size-base);
  color: rgba(255, 255, 255, 0.65);
  line-height: 1.7;
}

.bulk__examples {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--space-2);
  max-width: 640px;
}

.bulk__examples-label {
  font-size: var(--font-size-xs);
  color: rgba(255, 255, 255, 0.4);
  white-space: nowrap;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.bulk__examples-list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-2);
}

.bulk__example-btn {
  padding: 4px 12px;
  font-family: var(--font-family);
  font-size: var(--font-size-xs);
  color: rgba(255, 255, 255, 0.7);
  background: rgba(255, 255, 255, 0.07);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: background var(--transition-fast), color var(--transition-fast);
}

.bulk__example-btn:hover {
  background: rgba(255, 255, 255, 0.14);
  color: #fff;
}

.bulk__hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-3);
  justify-content: center;
}

.bulk__btn {
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

.bulk__btn--primary {
  background: var(--color-main-blue);
  color: #fff;
  border-color: var(--color-main-blue);
}

.bulk__btn--primary:hover {
  background: #1a5bbd;
  border-color: #1a5bbd;
}

.bulk__btn--outline {
  background: transparent;
  color: #fff;
  border-color: rgba(255, 255, 255, 0.3);
}

.bulk__btn--outline:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.5);
}

.bulk__btn--lg {
  padding: 14px 32px;
  font-size: var(--font-size-lg);
}

/* ===========================
   STATS STRIP
   =========================== */
.bulk__stats-strip {
  background: #fff;
  border-bottom: 1px solid var(--color-gray-blue);
  overflow-x: hidden;
}

.bulk__stats-inner {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0;
}

.bulk__stat {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-5) var(--space-5);
  border-right: 1px solid var(--color-gray-blue);
}

.bulk__stat:last-child { border-right: none; }

.bulk__stat-value {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-main-blue);
  white-space: nowrap;
  flex-shrink: 0;
}

.bulk__stat-text { display: flex; flex-direction: column; gap: 1px; }
.bulk__stat-label { font-size: var(--font-size-sm); font-weight: var(--font-weight-semibold); color: var(--color-black); line-height: 1.3; }
.bulk__stat-note { font-size: var(--font-size-xs); color: var(--color-pale-black); }

/* ===========================
   WORKSPACE (новая основная секция)
   =========================== */
.bulk__workspace {
  padding: 80px 0;
  background: #f1f5f9;
  overflow-x: hidden;
}

.bulk__workspace-head {
  text-align: center;
  margin-bottom: var(--space-10);
}

.bulk__workspace-eyebrow {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--color-main-blue);
  margin-bottom: var(--space-2);
}

.bulk__workspace-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  line-height: 1.2;
}

.bulk__workspace-grid {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: var(--space-6);
  align-items: start;
}

/* --- Левая панель --- */
.bulk__workspace-panel {
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.bulk__panel-header {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-5) var(--space-6);
  background: #0d1f3c;
  border-bottom: 3px solid var(--color-main-blue);
}

.bulk__panel-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  background: rgba(255, 255, 255, 0.08);
  color: var(--color-sea-clear);
  flex-shrink: 0;
}

.bulk__panel-icon svg {
  width: 20px;
  height: 20px;
}

.bulk__panel-title-wrap {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.bulk__panel-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: #fff;
  margin: 0;
  line-height: 1.3;
}

.bulk__panel-desc {
  font-size: var(--font-size-xs);
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

.bulk__checker-card {
  padding: var(--space-6);
}

/* --- Правая колонка (сайдбар) --- */
.bulk__workspace-sidebar {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.bulk__sidebar-card {
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  padding: var(--space-5);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.bulk__sidebar-card--accent {
  border-left: 3px solid var(--color-main-blue);
}

.bulk__sidebar-title {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  margin: 0 0 var(--space-4);
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.bulk__sidebar-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.bulk__sidebar-list li {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
}

.bulk__sidebar-list-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  color: var(--color-green);
  flex-shrink: 0;
}

.bulk__sidebar-actions {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.bulk__sidebar-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--color-pale-black);
  background: var(--color-pale-blue);
  border-radius: var(--radius-base);
  text-decoration: none;
  transition: background var(--transition-fast), color var(--transition-fast);
}

.bulk__sidebar-btn svg {
  flex-shrink: 0;
  color: var(--color-main-blue);
}

.bulk__sidebar-btn:hover {
  background: #dde5f0;
  color: var(--color-main-blue);
}

/* ===========================
   HOW IT WORKS
   =========================== */
.bulk__how {
  padding: 80px 0;
  background: #fff;
  overflow-x: hidden;
}

.bulk__how-head { text-align: center; margin-bottom: var(--space-12); }

.bulk__how-eyebrow {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--color-main-blue);
  margin-bottom: var(--space-2);
}

.bulk__how-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  line-height: 1.2;
}

.bulk__steps {
  display: flex;
  align-items: stretch;
  gap: var(--space-4);
}

.bulk__step {
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

.bulk__step:hover {
  box-shadow: var(--shadow-md);
  border-color: var(--color-main-blue);
}

.bulk__step::after {
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

.bulk__step:last-child::after { display: none; }

.bulk__step-icon {
  width: 44px; height: 44px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 1.25rem;
}

.bulk__step-icon--blue { background: var(--color-pale-blue); color: var(--color-main-blue); }
.bulk__step-icon--green { background: #e6f5ee; color: var(--color-green); }
.bulk__step-icon--orange { background: #fff0e4; color: var(--color-orange); }

.bulk__step-num {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-bold);
  letter-spacing: 0.08em;
  color: var(--color-pale-black);
  text-transform: uppercase;
}

.bulk__step-title { font-size: var(--font-size-md); font-weight: var(--font-weight-bold); color: var(--color-black); line-height: 1.3; }
.bulk__step-desc { font-size: var(--font-size-sm); color: var(--color-pale-black); line-height: 1.6; }

/* ===========================
   CTA
   =========================== */
.bulk__cta {
  padding: 80px 0;
  background: #0d1f3c;
  text-align: center;
}

.bulk__cta-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* ===========================
   АДАПТИВ
   =========================== */
@media (max-width: 1100px) {
  .bulk__stats-inner { grid-template-columns: repeat(2, 1fr); }
  .bulk__stat:nth-child(2) { border-right: none; }
  .bulk__stat:nth-child(1), .bulk__stat:nth-child(2) { border-bottom: 1px solid var(--color-gray-blue); }

  .bulk__workspace-grid {
    grid-template-columns: 1fr;
  }

  .bulk__workspace-sidebar {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: var(--space-4);
  }
}

@media (max-width: 768px) {
  .bulk__hero { padding: 44px 0 48px; }
  .bulk__hero-title { font-size: clamp(1.5rem, 6vw, 2.2rem); }
  .bulk__hero-subtitle { font-size: var(--font-size-sm); }

  .bulk__stat {
    flex-direction: column;
    align-items: flex-start;
    gap: 2px;
    padding: var(--space-3) var(--space-4);
  }
  .bulk__stat-value { font-size: var(--font-size-base); }
  .bulk__stat-note { display: none; }

  .bulk__steps { flex-direction: column; gap: var(--space-3); }
  .bulk__step { padding: var(--space-4); }
  .bulk__step::after { display: none; }

  .bulk__workspace,
  .bulk__how,
  .bulk__cta { padding: 48px 0; }

  .bulk__workspace-title { font-size: var(--font-size-2xl); }
  .bulk__workspace-head { margin-bottom: var(--space-8); }

  .bulk__checker-card { padding: var(--space-4); }

  .bulk__panel-header { padding: var(--space-4); }

  .bulk__workspace-sidebar {
    grid-template-columns: 1fr;
  }

  .bulk__how-title { font-size: var(--font-size-2xl); }
  .bulk__how-head { margin-bottom: var(--space-8); }
}

@media (max-width: 540px) {
  .bulk__hero { padding: 32px 0 40px; }
  .bulk__hero-title { font-size: 1.5rem; line-height: 1.2; }
  .bulk__hero-subtitle { font-size: var(--font-size-sm); }
  .bulk__examples { flex-direction: column; align-items: flex-start; }

  .bulk__stats-inner { grid-template-columns: 1fr 1fr; }
  .bulk__stat {
    flex-direction: column;
    align-items: flex-start;
    padding: var(--space-3) var(--space-3);
    gap: 2px;
  }
  .bulk__stat:nth-child(1), .bulk__stat:nth-child(3) { border-right: 1px solid var(--color-gray-blue); }
  .bulk__stat:nth-child(2), .bulk__stat:nth-child(4) { border-right: none; }
  .bulk__stat-value { font-size: var(--font-size-base); white-space: normal; }
  .bulk__stat-label { font-size: 11px; }
  .bulk__stat-note { display: none; }

  .bulk__workspace-title { font-size: var(--font-size-xl); }
}
</style>
