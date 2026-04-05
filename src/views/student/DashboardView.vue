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
              Личный кабинет студента<br />
              <span class="dashboard__title-accent">Diasoft Verify</span>
            </h1>

            <p class="dashboard__subtitle">
              Публичный поиск по номеру диплома (GET /api/public/certificates/search). Полный список ваших дипломов — в разделе «Мои дипломы» (GET /api/certificates/my).
            </p>
            <div class="dashboard__hero-actions">
              <router-link to="/student/search" class="dashboard__btn dashboard__btn--primary">Найти диплом</router-link>
              <router-link to="/student/diplomas" class="dashboard__btn dashboard__btn--outline">Мои дипломы</router-link>
            </div>
          </div>
        </div>
      </section>

      <!-- ===== Info Banner ===== -->
      <div class="container">
        <div class="info-banner">
          <div class="banner-icon-bg">
            <Search :size="24" class="banner-icon" />
          </div>
          <div class="banner-text">
            <h4>Поиск в публичном реестре</h4>
            <p>Укажите номер диплома — запрос без авторизации, как в OpenAPI бэкенда.</p>
          </div>
        </div>
      </div>

      <!-- ===== Основная рабочая зона ===== -->
      <section class="dashboard__workspace">
        <div class="container">
          <div class="dashboard-layout">
            <!-- Левая колонка: форма поиска -->
            <section class="section-pro">
              <div class="section-header">
                <h2>Поиск по номеру</h2>
                <span class="badge">Онлайн</span>
              </div>

              <div class="dashboard__search-card">
                <form @submit.prevent="onSearch" class="sf">
                  <div class="sf-field">
                    <label class="sf-label">Номер диплома (diplomaNumber)</label>
                    <div class="sf-input-wrapper">
                      <Search :size="18" class="sf-input-icon" />
                      <input
                        v-model="searchQuery"
                        type="text"
                        class="sf-input"
                        placeholder="Например: DIP-2026-001"
                        autocomplete="off"
                        spellcheck="false"
                      />
                    </div>
                    <p class="sf-hint">
                      Только номер диплома (как в Swagger: query <code class="sf-hint-code">diplomaNumber</code>). ФИО сюда не передаётся.
                    </p>
                  </div>

                  <button type="submit" class="sf-btn" :disabled="loading || !searchQuery.trim()">
                    <Search v-if="!loading" :size="18" />
                    <Loader2 v-else :size="18" class="sf-spin" />
                    <span>{{ loading ? 'Поиск…' : 'Найти диплом' }}</span>
                  </button>
                </form>

                <!-- Результат поиска -->
                <Transition name="sf-fade">
                  <div v-if="diplomaResult" class="sf-res" :class="diplomaResult.statusClass">
                    <div class="sf-res-head">
                      <span class="sf-res-icon" :class="{ 'sf-res-icon--ok': diplomaResult.isValid }">
                        <CheckCircle v-if="diplomaResult.isValid" :size="20" />
                        <AlertCircle v-else :size="20" />
                      </span>
                      <h4 class="sf-res-title">{{ diplomaResult.title }}</h4>
                    </div>
                    <div v-if="diplomaResult.isValid" class="sf-res-details">
                      <div class="sf-detail-row">
                        <Building2 :size="16" class="sf-detail-icon" />
                        <span class="sf-detail-k">ВУЗ</span>
                        <span class="sf-detail-v">{{ diplomaResult.university || '—' }}</span>
                      </div>
                      <div class="sf-detail-row">
                        <GraduationCap :size="16" class="sf-detail-icon" />
                        <span class="sf-detail-k">Специальность</span>
                        <span class="sf-detail-v">{{ diplomaResult.specialty || '—' }}</span>
                      </div>
                      <div class="sf-detail-row">
                        <Calendar :size="16" class="sf-detail-icon" />
                        <span class="sf-detail-k">Дата выдачи</span>
                        <span class="sf-detail-v">{{ diplomaResult.issuedAt || '—' }}</span>
                      </div>
                      <div class="sf-detail-row">
                        <ShieldCheck :size="16" class="sf-detail-icon" />
                        <span class="sf-detail-k">Статус</span>
                        <span class="sf-detail-v"><StatusBadge :status="diplomaResult.status" /></span>
                      </div>
                    </div>
                  </div>
                </Transition>

                <Transition name="sf-fade">
                  <div v-if="searchError" class="sf-err">
                    <AlertCircle :size="16" />
                    <span>{{ searchError }}</span>
                  </div>
                </Transition>
              </div>
            </section>

            <!-- Правая колонка: сайдбар -->
            <aside class="info-sidebar">
              <div class="info-card">
                <div class="sidebar-icon-wrap bg-blue">
                  <ShieldCheck :size="24" />
                </div>
                <h4>Безопасность</h4>
                <p>Все дипломы проходят проверку через государственный реестр в реальном времени.</p>
              </div>

              <div class="info-card">
                <div class="sidebar-icon-wrap bg-green">
                  <CheckCircle :size="24" />
                </div>
                <h4>Мгновенная проверка</h4>
                <p>Работодатель может проверить подлинность диплома за 2 секунды без звонков в вуз.</p>
              </div>

              <div class="info-card">
                <div class="sidebar-icon-wrap bg-purple">
                  <FileSearch :size="24" />
                </div>
                <h4>История проверок</h4>
                <p>Отслеживайте кто и когда проверял ваши дипломы в режиме реального времени.</p>
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
import { ref, onMounted } from 'vue'
import {
  Search, CheckCircle, AlertCircle, Building2, GraduationCap,
  Calendar, ShieldCheck, FileSearch, Loader2
} from 'lucide-vue-next'
import AppHeader from '../../components/common/AppHeader.vue'
import AppFooter from '../../components/common/AppFooter.vue'
import StatusBadge from '../../components/common/StatusBadge.vue'
import { api } from '../../api/api.js'
import {
  validatePublicSearchDiplomaNumberInput,
  isCertificateNotFoundError,
} from '../../utils/publicDiplomaSearch.js'

function formatIssueDate(iso) {
  if (!iso) return '—'
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return String(iso)
  return d.toLocaleDateString('ru-RU')
}

// ========== Поиск диплома ==========
const searchQuery = ref('')
const loading = ref(false)
const diplomaResult = ref(null)
const searchError = ref('')

async function onSearch() {
  const v = validatePublicSearchDiplomaNumberInput(searchQuery.value)
  if (!v.ok) {
    searchError.value = v.message
    diplomaResult.value = null
    return
  }

  loading.value = true
  searchError.value = ''
  diplomaResult.value = null

  try {
    const cert = await api.publicCertificatesSearch({ diplomaNumber: v.query })
    const st = String(cert?.status || '').toUpperCase()
    const ok = st === 'ACTIVE'
    diplomaResult.value = {
      isValid: ok,
      status: String(cert?.status || '').toLowerCase() || 'unknown',
      statusClass: ok ? 'sf-res--ok' : 'sf-res--fail',
      title: ok ? 'Диплом найден' : `Запись найдена (статус: ${cert?.status || '—'})`,
      university: cert?.universityName || '—',
      specialty: cert?.specialty || '—',
      issuedAt: formatIssueDate(cert?.issuedAt),
    }
  } catch (err) {
    if (isCertificateNotFoundError(err)) {
      diplomaResult.value = {
        isValid: false,
        statusClass: 'sf-res--fail',
        title: 'Диплом не найден',
      }
    } else {
      searchError.value = err.message || 'Ошибка при поиске. Попробуйте позже.'
    }
  } finally {
    loading.value = false
  }
}

// ========== Последние верификации ==========
const recentVerifications = ref([])

onMounted(() => {
  setTimeout(() => {
    recentVerifications.value = [
      { id: 1, date: '28.03.2026', checker: 'ООО «ТехноКадры»', status: 'active' },
      { id: 2, date: '15.03.2026', checker: 'Анонимно', status: 'active' }
    ]
  }, 400)
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
.dashboard__workspace {
  padding: 40px 0 80px;
  background: #f8fafc;
  overflow-x: hidden;
}

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

/* Search Card */
.dashboard__search-card {
  /* Already inside section-pro */
}

/* ===========================
   SEARCH FORM (sf)
   =========================== */
.sf {
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
}

.sf-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
  position: relative;
}

.sf-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-black);
  letter-spacing: 0.01em;
}

.sf-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.sf-input-icon {
  position: absolute;
  left: 12px;
  color: var(--color-pale-black);
  pointer-events: none;
}

.sf-input {
  width: 100%;
  padding: 10px 14px 10px 38px;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-base);
  font-size: var(--font-size-base);
  font-family: var(--font-family);
  background: #fff;
  color: var(--color-black);
  transition: border-color 0.15s ease, box-shadow 0.15s ease;
}

.sf-input::placeholder {
  color: var(--color-pale-black);
  opacity: 0.6;
}

.sf-input:focus {
  outline: none;
  border-color: var(--color-main-blue);
  box-shadow: 0 0 0 3px rgba(38, 75, 130, 0.1);
}

.sf-hint {
  margin: 0;
  font-size: 12px;
  color: var(--color-pale-black);
  line-height: 1.45;
}

.sf-hint-code {
  font-family: ui-monospace, monospace;
  font-size: 11px;
  background: #f1f5f9;
  padding: 1px 6px;
  border-radius: 4px;
}

/* Suggestions / Autocomplete */
.sf-suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  margin-top: 4px;
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-base);
  box-shadow: var(--shadow-md);
  list-style: none;
  padding: 4px 0;
  z-index: 10;
  max-height: 200px;
  overflow-y: auto;
}

.sf-suggestion {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  font-size: var(--font-size-sm);
  color: var(--color-black);
  cursor: pointer;
  transition: background 0.12s ease;
}

.sf-suggestion svg {
  flex-shrink: 0;
  color: var(--color-pale-black);
}

.sf-suggestion:hover {
  background: var(--color-pale-blue);
  color: var(--color-main-blue);
}

.sf-suggestion:hover svg {
  color: var(--color-main-blue);
}

/* Button */
.sf-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 24px;
  font-size: 14px;
  font-weight: 600;
  font-family: var(--font-family);
  color: #fff;
  background: var(--color-main-blue);
  border: none;
  border-radius: var(--radius-base);
  cursor: pointer;
  white-space: nowrap;
  flex-shrink: 0;
  transition: background 0.15s ease, opacity 0.15s ease;
}

.sf-btn:hover:not(:disabled) {
  background: #1a5bbd;
}

.sf-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.sf-spin {
  flex-shrink: 0;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Result */
.sf-res {
  border: 1px solid;
  border-radius: var(--radius-lg);
  padding: var(--space-5);
}

.sf-res--ok {
  background: #f0fdf4;
  border-color: #bbf7d0;
}

.sf-res--fail {
  background: #fef2f2;
  border-color: #fecaca;
}

.sf-res-head {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin-bottom: var(--space-4);
}

.sf-res-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  flex-shrink: 0;
}

.sf-res-icon--ok {
  background: #dcfce7;
  color: #16a34a;
}

.sf-res-icon:not(.sf-res-icon--ok) {
  background: #fee2e2;
  color: #dc2626;
}

.sf-res-title {
  margin: 0;
  font-size: var(--font-size-base);
  font-weight: 700;
  color: var(--color-black);
}

.sf-res-details {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.sf-detail-row {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  background: #f8fafc;
  border-radius: var(--radius-base);
  font-size: var(--font-size-sm);
}

.sf-detail-icon {
  flex-shrink: 0;
  color: var(--color-main-blue);
}

.sf-detail-k {
  color: var(--color-pale-black);
  font-weight: 500;
  min-width: 100px;
}

.sf-detail-v {
  color: var(--color-black);
  font-weight: var(--font-weight-semibold);
}

/* Error */
.sf-err {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  background: #fef2f2;
  color: #991b1b;
  border-radius: var(--radius-base);
  font-size: 13px;
  border: 1px solid #fecaca;
}

.sf-err svg {
  flex-shrink: 0;
  color: #dc2626;
}

/* Transition */
.sf-fade-enter-active,
.sf-fade-leave-active {
  transition: opacity 0.25s ease;
}

.sf-fade-enter-from,
.sf-fade-leave-to {
  opacity: 0;
}

/* ===========================
   Info Sidebar
   =========================== */
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
  margin: 0;
}

.info-card p {
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
  margin: 0;
  line-height: 1.5;
}

.sidebar-icon-wrap {
  padding: 12px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.sidebar-icon-wrap svg {
  color: #fff;
}

.bg-green { background: #22c55e; }
.bg-blue { background: var(--color-main-blue); }
.bg-purple { background: #a855f7; }
.bg-orange { background: #f97316; }

/* ===========================
   Recent Verifications
   =========================== */
.dashboard__recent {
  padding: 80px 0;
  background: #f8fafc;
  overflow-x: hidden;
}

.dashboard__recent-head {
  text-align: center;
  margin-bottom: var(--space-8);
}

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

/* Empty State */
.dashboard__empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-8);
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  text-align: center;
  color: var(--color-pale-black);
  font-size: var(--font-size-base);
}

.empty-icon-bg {
  width: 64px;
  height: 64px;
  background: #e8f4fd;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-main-blue);
}

/* Recent Table Card */
.recent-table-card {
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.recent-table-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-4) var(--space-6);
  background: #e8f4fd;
  border-bottom: 1px solid #b8dff5;
}

.recent-table-head h3 {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: #1a5b8c;
  margin: 0;
}

.recent-table {
  width: 100%;
  border-collapse: collapse;
}

.recent-table thead th {
  padding: var(--space-3) var(--space-5);
  text-align: left;
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  color: var(--color-pale-black);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-bottom: 1px solid var(--color-gray-blue);
  display: flex;
  align-items: center;
  gap: 4px;
  background: #f8fafc;
}

.recent-table tbody tr {
  transition: background var(--transition-fast);
}

.recent-table tbody tr:hover {
  background: #f1f5f9;
}

.recent-table tbody td {
  padding: var(--space-3) var(--space-5);
  font-size: var(--font-size-sm);
  color: var(--color-black);
  border-bottom: 1px solid var(--color-gray-blue);
}

.recent-table tbody td.mono {
  font-family: monospace;
  font-weight: var(--font-weight-semibold);
}

.recent-table tbody tr:last-child td {
  border-bottom: none;
}

/* Status Pill */
.status-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
}

.status-pill.status-active {
  background: #dcfce7;
  color: #166534;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

.status-dot-active {
  background: #22c55e;
}

/* ===========================
   АДАПТИВ
   =========================== */
@media (max-width: 1100px) {
  .dashboard-layout { grid-template-columns: 1fr; }
}

@media (max-width: 768px) {
  .dashboard__hero { padding: 44px 0 48px; }
  .dashboard__title { font-size: clamp(1.5rem, 6vw, 2.2rem); }
  .dashboard__workspace, .dashboard__recent { padding: 48px 0; }
  .dashboard__how-title { font-size: var(--font-size-2xl); }
}

@media (max-width: 540px) {
  .dashboard__hero { padding: 32px 0 40px; }
  .dashboard__title { font-size: 1.5rem; line-height: 1.2; }
  .dashboard__how-title { font-size: var(--font-size-xl); }
  .recent-table-head { flex-direction: column; gap: var(--space-2); align-items: flex-start; }
}
</style>