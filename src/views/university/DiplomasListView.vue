<template>
  <div class="diplomas-list-view">
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
              Реестр дипломов<br />
              <span class="diplomas__title-accent">Управление записями</span>
            </h1>
            <p class="diplomas__subtitle">
              Просмотр, поиск и управление дипломами. Отзыв поддельных документов и контроль хэшей.
            </p>
            <div class="diplomas__hero-actions">
              <router-link to="/university/upload" class="diplomas__btn diplomas__btn--primary">Загрузить новый реестр</router-link>
              <router-link to="/university/dashboard" class="diplomas__btn diplomas__btn--outline">Вернуться на дашборд</router-link>
            </div>

            <!-- Поиск и фильтры внутри hero -->
            <div class="diplomas__search-row">
              <SearchInput v-model="searchQuery" @search="onSearch" placeholder="Поиск по ФИО, номеру, специальности" />
              <div class="diplomas__filters">
                <select v-model="statusFilter" @change="onStatusFilter">
                  <option value="all">Все статусы</option>
                  <option value="active">Активные</option>
                  <option value="revoked">Отозванные</option>
                  <option value="pending">Ожидают</option>
                </select>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- ===== Выпуск диплома (API бэкенда) ===== -->
      <section class="diplomas__issue-section">
        <div class="container">
          <div class="diplomas__table-head">
            <p class="diplomas__how-eyebrow">Выпуск</p>
            <h2 class="diplomas__how-title">Новый диплом</h2>
            <p class="diplomas__how-desc">

            </p>
          </div>
          <form class="issue-form" @submit.prevent="onIssueSubmit">
            <div class="issue-form__grid">
              <label class="issue-form__field">
                <span>Код университета</span>
                <input v-model="issueForm.universityCode" type="text" required placeholder="AITU-01" />
              </label>
              <label class="issue-form__field">
                <span>Номер диплома</span>
                <input v-model="issueForm.diplomaNumber" type="text" required placeholder="DIP-2026-001" />
              </label>
              <label class="issue-form__field">
                <span>ФИО выпускника</span>
                <input v-model="issueForm.fullName" type="text" required placeholder="Иванов Иван Иванович" />
              </label>
              <label class="issue-form__field">
                <span>Специальность</span>
                <input v-model="issueForm.specialty" type="text" required placeholder="Информатика" />
              </label>
              <label class="issue-form__field">
                <span>Год выпуска</span>
                <input v-model.number="issueForm.graduationYear" type="number" required min="1950" max="2100" />
              </label>
            </div>
            <p v-if="issueError" class="issue-form__err">{{ issueError }}</p>
            <p v-if="issueOk" class="issue-form__ok">{{ issueOk }}</p>
            <button type="submit" class="diplomas__btn diplomas__btn--primary" :disabled="issueLoading">
              {{ issueLoading ? 'Выпуск…' : 'Выпустить диплом' }}
            </button>
          </form>
        </div>
      </section>

      <!-- ===== Таблица дипломов ===== -->
      <section class="diplomas__table-section">
        <div class="container">
          <div class="diplomas__table-head">
            <p class="diplomas__how-eyebrow">Реестр</p>
            <h2 class="diplomas__how-title">Все дипломы</h2>
          </div>

          <div class="diplomas__table-wrapper">
            <DataTable
              :columns="columns"
              :items="diplomas"
              :loading="loading"
              :pagination="pagination"
              @sort="onSort"
              @page="onPage"
            >
              <template #cell-status="{ value }">
                <StatusBadge :status="value" />
              </template>
              <template #cell-hash="{ value }">
                <code class="hash-code">{{ value?.slice(0, 12) }}…</code>
              </template>
              <template #cell-actions="{ item }">
                <DiplomaActions :diploma="item" @revoke="onRevoke(item)" />
              </template>
            </DataTable>
          </div>
        </div>
      </section>

    </main>

    <!-- Модалка отзыва -->
    <ModalDialog :is-open="!!revokingDiploma" size="md" @close="revokingDiploma = null">
      <RevokeConfirm v-if="revokingDiploma" :diploma="revokingDiploma" @confirm="confirmRevoke" @cancel="revokingDiploma = null" />
    </ModalDialog>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { onBeforeRouteUpdate } from 'vue-router'
import AppHeader from '../../components/common/AppHeader.vue'
import AppFooter from '../../components/common/AppFooter.vue'
import DataTable from '../../components/common/DataTable.vue'
import SearchInput from '../../components/common/SearchInput.vue'
import StatusBadge from '../../components/common/StatusBadge.vue'
import ModalDialog from '../../components/common/ModalDialog.vue'
import DiplomaActions from '../../components/university/DiplomaActions.vue'
import RevokeConfirm from '../../components/university/RevokeConfirm.vue'
import { useDiplomas } from '../../composables/useDiplomas.js'
import { useAuth } from '../../composables/useAuth.js'

const { user } = useAuth()
const {
  diplomas,
  loading,
  total,
  page,
  pageSize,
  fetchDiplomas,
  createDiploma,
  setSearch,
  setStatusFilter,
  revokeDiploma,
  setPage,
} = useDiplomas()

const searchQuery = ref('')
const statusFilter = ref('all')
const revokingDiploma = ref(null)

const pagination = computed(() => ({
  page: page.value,
  pageSize: pageSize.value,
  total: total.value,
}))

const issueForm = ref({
  universityCode: user.value?.universityCode || '',
  diplomaNumber: '',
  fullName: '',
  specialty: '',
  graduationYear: new Date().getFullYear(),
})
const issueLoading = ref(false)
const issueError = ref('')
const issueOk = ref('')

watch(
  () => user.value?.universityCode,
  (code) => {
    if (code && !issueForm.value.universityCode) issueForm.value.universityCode = code
  },
  { immediate: true }
)

const columns = [
  { key: 'serialNumber', label: 'Серийный номер', sortable: true },
  { key: 'studentName', label: 'ФИО', sortable: true },
  { key: 'specialty', label: 'Специальность' },
  { key: 'issueDate', label: 'Дата выдачи', sortable: true },
  { key: 'status', label: 'Статус' },
  { key: 'hash', label: 'Хеш' },
  { key: 'actions', label: 'Действия' }
]

onMounted(() => {
  fetchDiplomas()
})

onBeforeRouteUpdate(() => {
  fetchDiplomas()
})

async function onIssueSubmit() {
  issueError.value = ''
  issueOk.value = ''
  issueLoading.value = true
  try {
    await createDiploma({ ...issueForm.value })
    issueOk.value = 'Диплом выпущен и добавлен в локальный реестр.'
    await fetchDiplomas()
  } catch (e) {
    issueError.value = e.message || 'Не удалось выпустить диплом'
  } finally {
    issueLoading.value = false
  }
}

function onSearch(q) {
  setSearch(q)
  fetchDiplomas()
}

function onStatusFilter() {
  const v = statusFilter.value === 'all' ? null : statusFilter.value
  setStatusFilter(v)
  fetchDiplomas()
}

function onSort({ key, dir }) {
  // TODO: сортировка на сервере
}

function onPage(p) {
  setPage(p)
  fetchDiplomas()
}

function onRevoke(diploma) {
  revokingDiploma.value = diploma
}

async function confirmRevoke() {
  if (!revokingDiploma.value) return
  await revokeDiploma(revokingDiploma.value.id)
  revokingDiploma.value = null
  await fetchDiplomas()
}
</script>

<style scoped>
.diplomas-list-view { min-height: 100vh; }

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
  width: 100%;
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

.diplomas__search-row {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-3);
  align-items: center;
  justify-content: center;
  width: 100%;
  max-width: 700px;
}

.diplomas__search-row .search-input {
  flex: 1;
  min-width: 260px;
}

.diplomas__filters select {
  padding: 10px 16px;
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-base);
  font-size: var(--font-size-base);
  font-family: var(--font-family);
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
  cursor: pointer;
  transition: border-color var(--transition-base);
}

.diplomas__filters select option {
  color: var(--color-black);
  background: #fff;
}

.diplomas__filters select:hover {
  border-color: rgba(255, 255, 255, 0.4);
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
   ISSUE FORM
   =========================== */
.diplomas__issue-section {
  padding: 48px 0;
  background: #f8fafc;
  border-top: 1px solid var(--color-gray-blue);
}

.issue-form {
  max-width: 960px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.issue-form__grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: var(--space-4);
}

.issue-form__field {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: var(--color-black);
}

.issue-form__field input {
  font-family: var(--font-family);
  font-size: var(--font-size-sm);
  padding: 10px 12px;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-base);
}

.issue-form__err {
  color: var(--color-red);
  font-size: var(--font-size-sm);
  margin: 0;
}

.issue-form__ok {
  color: #166534;
  font-size: var(--font-size-sm);
  margin: 0;
}

/* ===========================
   TABLE SECTION
   =========================== */
.diplomas__table-section {
  padding: 80px 0;
  background: #fff;
  overflow-x: hidden;
}

.diplomas__table-head { text-align: center; margin-bottom: var(--space-8); }

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

.diplomas__table-wrapper {
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  overflow-x: auto;
}

.hash-code {
  font-family: monospace;
  font-size: var(--font-size-xs);
  color: var(--color-main-blue);
  background: var(--color-pale-blue);
  padding: 2px 6px;
  border-radius: var(--radius-sm);
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

  .diplomas__table-section { padding: 48px 0; }
  .diplomas__how-title { font-size: var(--font-size-2xl); }
  .diplomas__table-head { margin-bottom: var(--space-8); }

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
