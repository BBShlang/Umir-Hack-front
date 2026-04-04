<template>
  <div class="reports-view">
    <AppHeader />

    <main class="main-content">
      <!-- ===== HERO ===== -->
      <section class="reports__hero">
        <div class="reports__hero-bg" aria-hidden="true">
          <div class="reports__hero-grid-lines" />
          <div class="reports__hero-glow reports__hero-glow--1" />
          <div class="reports__hero-glow reports__hero-glow--2" />
        </div>

        <div class="container reports__hero-inner">
          <div class="reports__hero-content">
            <h1 class="reports__hero-title">
              Отчёты и API<br />
              <span class="reports__hero-title-accent">Аналитика и интеграция</span>
            </h1>

            <p class="reports__hero-subtitle">
              Просматривайте историю проверок, экспортируйте отчёты для аудита и подключайте API для автоматизации процессов.
            </p>

            <div class="reports__hero-actions">
              <router-link to="/hr/verify" class="reports__btn reports__btn--outline">Одиночная проверка</router-link>
            </div>

            <div class="reports__examples">
              <span class="reports__examples-label">Доступные функции:</span>
              <div class="reports__examples-list">
                <span class="reports__example-btn">История проверок</span>
                <span class="reports__example-btn">Экспорт PDF</span>
                <span class="reports__example-btn">API ключи</span>
              </div>
            </div>
          </div>
        </div>
      </section>


      <!-- ===== Основная рабочая зона ===== -->
      <section class="reports__workspace">
        <div class="container">
          <div class="dashboard-layout">
            <!-- Левая колонка: отчёты -->
            <section class="section-pro">
              <div class="section-header">
                <h2>Аудит и отчёты</h2>
                <span class="badge">Доступно</span>
              </div>

              <div class="reports__checker-card">
                <AuditReport />
              </div>
            </section>

            <!-- Правая колонка: сайдбар -->
            <aside class="info-sidebar">
              <!-- API доступ -->
              <div class="info-card">
                <h4>API доступ</h4>
                <div class="reports__api-mini">
                  <div class="reports__api-mini-row">
                    <span class="reports__api-label">Endpoint</span>
                    <code class="reports__api-code">/api/test/crypto/verify</code>
                  </div>
                  <div class="reports__api-mini-row">
                    <span class="reports__api-label">Метод</span>
                    <span class="reports__api-method-badge">GET</span>
                  </div>
                  <div class="reports__api-mini-row">
                    <span class="reports__api-label">Limit</span>
                    <span class="reports__api-limit">1000/день</span>
                  </div>
                </div>
                <button class="reports__api-docs-btn" @click="onOpenDocs">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <path d="M8 2L2 5v6l6 3 6-3V5L8 2z" stroke="currentColor" stroke-width="1.4" stroke-linejoin="round"/>
                    <path d="M6 8l1.5 1.5L10 7" stroke="currentColor" stroke-width="1.3" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  Открыть документацию
                </button>
              </div>

              <!-- Требования -->
              <div class="info-card">
                <h4>Возможности API</h4>
                <ul class="reports__sidebar-list">
                  <li>
                    <span class="reports__sidebar-list-icon">
                      <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                        <circle cx="8" cy="8" r="6" stroke="currentColor" stroke-width="1.4"/>
                        <path d="M5.5 8l2 2 3-3.5" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                    </span>
                    <span>Проверка по серийному номеру</span>
                  </li>
                  <li>
                    <span class="reports__sidebar-list-icon">
                      <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                        <circle cx="8" cy="8" r="6" stroke="currentColor" stroke-width="1.4"/>
                        <path d="M5.5 8l2 2 3-3.5" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                    </span>
                    <span>Пакетная верификация</span>
                  </li>
                  <li>
                    <span class="reports__sidebar-list-icon">
                      <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                        <circle cx="8" cy="8" r="6" stroke="currentColor" stroke-width="1.4"/>
                        <path d="M5.5 8l2 2 3-3.5" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                    </span>
                    <span>Bearer-аутентификация</span>
                  </li>
                  <li>
                    <span class="reports__sidebar-list-icon">
                      <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                        <circle cx="8" cy="8" r="6" stroke="currentColor" stroke-width="1.4"/>
                        <path d="M5.5 8l2 2 3-3.5" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                    </span>
                    <span>JSON формат ответов</span>
                  </li>
                </ul>
              </div>

              <!-- Быстрые действия -->
              <div class="info-card">
                <h4>Быстрые действия</h4>
                <div class="reports__sidebar-actions">
                  <router-link to="/hr/verify" class="reports__sidebar-btn">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                      <circle cx="7" cy="7" r="5" stroke="currentColor" stroke-width="1.4"/>
                      <path d="M11 11l3.5 3.5" stroke="currentColor" stroke-width="1.4" stroke-linecap="round"/>
                    </svg>
                    Проверить диплом
                  </router-link>
                </div>
              </div>
            </aside>
          </div>
        </div>
      </section>

    </main>

    <AppFooter />
  </div>
</template>

<script setup>
import { FileText } from 'lucide-vue-next'
import AppFooter from '../../components/common/AppFooter.vue'
import AuditReport from '../../components/hr/AuditReport.vue'
import ApiAccessWidget from '../../components/hr/ApiAccessWidget.vue'

function onOpenDocs() {
  const base = (import.meta.env.VITE_API_BASE_URL || '').replace(/\/$/, '')
  if (base) window.open(`${base}/swagger-ui/index.html`, '_blank', 'noopener,noreferrer')
  else window.open('/api-docs', '_blank', 'noopener,noreferrer')
}
</script>

<style scoped>
.reports-view { min-height: 100vh; }
.main-content { flex: 1; }

/* ===========================
   Info Banner
   =========================== */
.info-banner {
  display: flex;
  gap: var(--space-4);
  padding: var(--space-5);
  background: #e8f4fd;
  border: 1px solid #b8dff5;
  border-radius: var(--radius-lg);
  margin-top: var(--space-6);
  margin-bottom: var(--space-6);
  align-items: flex-start;
}

.banner-icon-bg {
  background: #b8dff5;
  border-radius: 50%;
  padding: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-icon {
  color: #1a5b8c;
}

.banner-text h4 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-bold);
  color: #1a5b8c;
  margin: 0 0 var(--space-1);
}

.banner-text p {
  font-size: var(--font-size-sm);
  color: #2d7ab5;
  margin: 0;
  line-height: 1.5;
}

/* ===========================
   WORKSPACE / Dashboard Layout
   =========================== */
.dashboard-layout {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: var(--space-6);
  align-items: start;
}

/* Section Pro */
.section-pro {
  background: #fff;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-gray-blue);
  padding: var(--space-6);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--space-6);
}

.section-header h2 {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  margin: 0;
}

.badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  background: #dcfce7;
  color: #166534;
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
}

/* Info Sidebar */
.info-sidebar {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.info-card {
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  padding: var(--space-5);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: var(--space-3);
}

.info-card h4 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  margin: 0 0 var(--space-3);
}

.info-card p {
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
  margin: 0;
  line-height: 1.5;
}

/* Список возможностей */
.reports__sidebar-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
  width: 100%;
}

.reports__sidebar-list li {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
  text-align: left;
}

.reports__sidebar-list-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  color: var(--color-green);
  flex-shrink: 0;
}

/* Быстрые действия */
.reports__sidebar-actions {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  width: 100%;
}

.reports__sidebar-btn {
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
  width: 100%;
}

.reports__sidebar-btn svg {
  flex-shrink: 0;
  color: var(--color-main-blue);
}

.reports__sidebar-btn:hover {
  background: #dde5f0;
  color: var(--color-main-blue);
}

/* ===========================
   HERO
   =========================== */
.reports__hero {
  position: relative;
  background: #0d1f3c;
  padding: 72px 0 80px;
}

.reports__hero-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}

.reports__hero-grid-lines {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(
      rgba(255, 255, 255, 0.03) 1px,
      transparent 1px
    ),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.reports__hero-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.reports__hero-glow--1 {
  width: 500px;
  height: 500px;
  top: -150px;
  right: -100px;
  background: rgba(38, 75, 130, 0.45);
}

.reports__hero-glow--2 {
  width: 380px;
  height: 380px;
  bottom: -120px;
  left: -60px;
  background: rgba(72, 184, 194, 0.2);
}

.reports__hero-inner {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.reports__hero-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: var(--space-6);
}

.reports__hero-title {
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: var(--font-weight-bold);
  color: #fff;
  line-height: 1.1;
  letter-spacing: -0.02em;
}

.reports__hero-title-accent {
  color: var(--color-sea-clear);
}

.reports__hero-subtitle {
  max-width: 540px;
  font-size: var(--font-size-base);
  color: rgba(255, 255, 255, 0.65);
  line-height: 1.7;
}

.reports__examples {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--space-2);
  max-width: 640px;
}

.reports__examples-label {
  font-size: var(--font-size-xs);
  color: rgba(255, 255, 255, 0.4);
  white-space: nowrap;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.reports__examples-list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-2);
}

.reports__example-btn {
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

.reports__example-btn:hover {
  background: rgba(255, 255, 255, 0.14);
  color: #fff;
}

.reports__hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-3);
  justify-content: center;
}

.reports__btn {
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

.reports__btn--primary {
  background: var(--color-main-blue);
  color: #fff;
  border-color: var(--color-main-blue);
}

.reports__btn--primary:hover {
  background: #1a5bbd;
  border-color: #1a5bbd;
}

.reports__btn--outline {
  background: transparent;
  color: #fff;
  border-color: rgba(255, 255, 255, 0.3);
}

.reports__btn--outline:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.5);
}

.reports__btn--lg {
  padding: 14px 32px;
  font-size: var(--font-size-lg);
}

/* ===========================
   STATS STRIP
   =========================== */
.reports__stats-strip {
  background: #fff;
  border-bottom: 1px solid var(--color-gray-blue);
  overflow-x: hidden;
}

.reports__stats-inner {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0;
}

.reports__stat {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-5) var(--space-5);
  border-right: 1px solid var(--color-gray-blue);
}

.reports__stat:last-child { border-right: none; }

.reports__stat-value {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-main-blue);
  white-space: nowrap;
  flex-shrink: 0;
}

.reports__stat-text { display: flex; flex-direction: column; gap: 1px; }
.reports__stat-label { font-size: var(--font-size-sm); font-weight: var(--font-weight-semibold); color: var(--color-black); line-height: 1.3; }
.reports__stat-note { font-size: var(--font-size-xs); color: var(--color-pale-black); }

/* ===========================
   WORKSPACE
   =========================== */
.reports__workspace {
  padding: 80px 0;
  background: #f1f5f9;
  overflow-x: hidden;
}

.reports__workspace-head {
  text-align: center;
  margin-bottom: var(--space-10);
}

.reports__workspace-eyebrow {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--color-main-blue);
  margin-bottom: var(--space-2);
}

.reports__workspace-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  line-height: 1.2;
}

.reports__workspace-grid {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: var(--space-6);
  align-items: start;
}

/* --- Левая панель --- */
.reports__workspace-panel {
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.reports__panel-header {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-5) var(--space-6);
  background: #0d1f3c;
  border-bottom: 3px solid var(--color-main-blue);
}

.reports__panel-icon {
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

.reports__panel-icon svg {
  width: 20px;
  height: 20px;
}

.reports__panel-title-wrap {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.reports__panel-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: #fff;
  margin: 0;
  line-height: 1.3;
}

.reports__panel-desc {
  font-size: var(--font-size-xs);
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

.reports__checker-card {
  padding: var(--space-6);
}

/* --- Правая колонка (сайдбар) --- */
.reports__workspace-sidebar {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.reports__sidebar-card {
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  padding: var(--space-5);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.reports__sidebar-card--accent {
  border-left: 3px solid var(--color-main-blue);
}

.reports__sidebar-title {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  margin: 0 0 var(--space-4);
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

/* API mini card */
.reports__api-mini {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
  margin-bottom: var(--space-5);
  padding: var(--space-4);
  background: var(--color-pale-blue);
  border-radius: var(--radius-md);
}

.reports__api-mini-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.reports__api-label {
  font-size: var(--font-size-xs);
  color: var(--color-pale-black);
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.reports__api-code {
  font-size: var(--font-size-xs);
  font-family: 'Consolas', 'Monaco', monospace;
  color: var(--color-main-blue);
  background: #fff;
  padding: 2px 8px;
  border-radius: 4px;
}

.reports__api-method-badge {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-bold);
  color: #fff;
  background: var(--color-main-blue);
  padding: 2px 10px;
  border-radius: 4px;
  letter-spacing: 0.05em;
}

.reports__api-limit {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  color: var(--color-green);
}

.reports__api-docs-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  width: 100%;
  padding: 10px 20px;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: #fff;
  background: var(--color-main-blue);
  border: none;
  border-radius: var(--radius-base);
  cursor: pointer;
  font-family: var(--font-family);
  transition: background var(--transition-fast);
}

.reports__api-docs-btn:hover {
  background: #1a5bbd;
}

/* Список возможностей */
.reports__sidebar-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.reports__sidebar-list li {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
}

.reports__sidebar-list-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  color: var(--color-green);
  flex-shrink: 0;
}

/* Быстрые действия */
.reports__sidebar-actions {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.reports__sidebar-btn {
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

.reports__sidebar-btn svg {
  flex-shrink: 0;
  color: var(--color-main-blue);
}

.reports__sidebar-btn:hover {
  background: #dde5f0;
  color: var(--color-main-blue);
}

/* ===========================
   FEATURES
   =========================== */
.reports__features {
  padding: 80px 0;
  background: #fff;
  overflow-x: hidden;
}

.reports__features-head { text-align: center; margin-bottom: var(--space-12); }

.reports__features-eyebrow {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--color-main-blue);
  margin-bottom: var(--space-2);
}

.reports__features-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  line-height: 1.2;
}

.reports__steps {
  display: flex;
  align-items: stretch;
  gap: var(--space-4);
}

.reports__step {
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

.reports__step:hover {
  box-shadow: var(--shadow-md);
  border-color: var(--color-main-blue);
}

.reports__step::after {
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

.reports__step:last-child::after { display: none; }

.reports__step-icon {
  width: 44px; height: 44px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 1.25rem;
}

.reports__step-icon--blue { background: var(--color-pale-blue); color: var(--color-main-blue); }
.reports__step-icon--green { background: #e6f5ee; color: var(--color-green); }
.reports__step-icon--orange { background: #fff0e4; color: var(--color-orange); }

.reports__step-num {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-bold);
  letter-spacing: 0.08em;
  color: var(--color-pale-black);
  text-transform: uppercase;
}

.reports__step-title { font-size: var(--font-size-md); font-weight: var(--font-weight-bold); color: var(--color-black); line-height: 1.3; }
.reports__step-desc { font-size: var(--font-size-sm); color: var(--color-pale-black); line-height: 1.6; }

/* ===========================
   CTA
   =========================== */
.reports__cta {
  padding: 80px 0;
  background: #0d1f3c;
  text-align: center;
}

.reports__cta-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* ===========================
   АДАПТИВ
   =========================== */
@media (max-width: 1100px) {
  .dashboard-layout { grid-template-columns: 1fr; }
}

@media (max-width: 768px) {
  .reports__hero { padding: 44px 0 48px; }
  .reports__hero-title { font-size: clamp(1.5rem, 6vw, 2.2rem); }
  .reports__workspace { padding: 48px 0; }
  .section-header h2 { font-size: var(--font-size-xl); }
}

@media (max-width: 540px) {
  .reports__hero { padding: 32px 0 40px; }
  .reports__hero-title { font-size: 1.5rem; line-height: 1.2; }
}
</style>
