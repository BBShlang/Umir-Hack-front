<template>
  <div class="claims-view">
    <AppHeader />
    <main class="main-content">
      <!-- ===== HERO ===== -->
      <section class="claims__hero">
        <div class="claims__hero-bg" aria-hidden="true">
          <div class="claims__hero-grid-lines" />
          <div class="claims__hero-glow claims__hero-glow--1" />
          <div class="claims__hero-glow claims__hero-glow--2" />
        </div>

        <div class="container claims__hero-inner">
          <div class="claims__hero-content">
            <h1 class="claims__title">
              Заявки на привязку дипломов<br />
              <span class="claims__title-accent">Управление заявками</span>
            </h1>
            <p class="claims__subtitle">
              Студенты отправляют заявки на привязку дипломов. Подтвердите или отклоните каждую заявку.
            </p>
            <div class="claims__hero-actions">
              <button class="claims__btn claims__btn--outline" @click="fetchClaims">
                <RefreshCw :size="16" :class="{ spin: loading }" />
                <span>Обновить</span>
              </button>
            </div>
          </div>
        </div>
      </section>

      <section class="claims__filters">
        <div class="container">
          <div class="filters-bar">
            <div class="filters-bar__tabs">
              <button
                :class="['tab-btn', { 'tab-btn--active': statusTab === 'all' }]"
                @click="statusTab = 'all'"
              >
                Все
              </button>
              <button
                :class="['tab-btn', { 'tab-btn--active': statusTab === 'PENDING' }]"
                @click="statusTab = 'PENDING'"
              >
                На рассмотрении
              </button>
              <button
                :class="['tab-btn', { 'tab-btn--active': statusTab === 'APPROVED' }]"
                @click="statusTab = 'APPROVED'"
              >
                Одобрены
              </button>
              <button
                :class="['tab-btn', { 'tab-btn--active': statusTab === 'REJECTED' }]"
                @click="statusTab = 'REJECTED'"
              >
                Отклонены
              </button>
            </div>
            <span class="filters-bar__count">
              {{ filteredClaims.length }} из {{ allClaims.length }}
            </span>
          </div>
        </div>
      </section>

      <!-- ===== Список заявок ===== -->
      <section class="claims__list-section">
        <div class="container">
          <!-- Empty state -->
          <div v-if="filteredClaims.length === 0 && !loading" class="empty-state">
            <FileSearch :size="48" />
            <h3>Заявок нет</h3>
            <p>Пока нет заявок с выбранным фильтром.</p>
          </div>

          <!-- Loading -->
          <div v-if="loading" class="loading-state">
            <Loader2 :size="32" class="spin" />
            <p>Загрузка...</p>
          </div>

          <!-- Cards -->
          <div v-if="!loading && filteredClaims.length" class="claims-grid">
            <div
              v-for="claim in filteredClaims"
              :key="claim.requestId"
              class="claim-card"
              :class="`claim-card--${claim.status?.toLowerCase()}`"
            >
              <div class="claim-card__header">
                <div class="claim-card__meta">
                  <span class="claim-card__id">ID: {{ shortId(claim.requestId) }}</span>
                  <span class="claim-status" :class="`claim-status--${claim.status?.toLowerCase()}`">
                    {{ getStatusLabel(claim.status) }}
                  </span>
                </div>
              </div>

              <div class="claim-card__body">
                <div class="claim-detail">
                  <span class="claim-detail-k">Request ID</span>
                  <span class="claim-detail-v claim-detail-v--mono">{{ claim.requestId }}</span>
                </div>
                <div class="claim-detail">
                  <span class="claim-detail-k">Certificate ID</span>
                  <span class="claim-detail-v claim-detail-v--mono">{{ claim.certificateId }}</span>
                </div>
                <div class="claim-detail">
                  <span class="claim-detail-k">Student ID</span>
                  <span class="claim-detail-v claim-detail-v--mono">{{ claim.studentId }}</span>
                </div>
                <div class="claim-detail">
                  <span class="claim-detail-k">Дата заявки</span>
                  <span class="claim-detail-v">{{ formatDate(claim.createdAt) }}</span>
                </div>
                <div v-if="claim.reviewedAt" class="claim-detail">
                  <span class="claim-detail-k">Дата рассмотрения</span>
                  <span class="claim-detail-v">{{ formatDate(claim.reviewedAt) }}</span>
                </div>
                <div v-if="claim.reviewComment" class="claim-detail">
                  <span class="claim-detail-k">Комментарий</span>
                  <span class="claim-detail-v">{{ claim.reviewComment }}</span>
                </div>
              </div>

              <div v-if="claim.status === 'PENDING'" class="claim-card__actions">
                <button
                  class="action-btn action-btn--approve"
                  :disabled="actionLoading === claim.requestId"
                  @click="onApprove(claim)"
                >
                  <Check :size="16" />
                  <span>{{ actionLoading === claim.requestId ? 'Обработка…' : 'Одобрить' }}</span>
                </button>
                <button
                  class="action-btn action-btn--reject"
                  :disabled="actionLoading === claim.requestId"
                  @click="openRejectModal(claim)"
                >
                  <X :size="16" />
                  <span>Отклонить</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>

    <!-- Модалка отклонения -->
    <ModalDialog :is-open="!!rejectingClaim" size="md" @close="closeRejectModal">
      <div v-if="rejectingClaim" class="reject-modal">
        <h3 class="reject-modal__title">Отклонить заявку</h3>
        <p class="reject-modal__desc">
          Укажите причину отклонения заявки <strong>{{ shortId(rejectingClaim.requestId) }}</strong>.
        </p>
        <label class="reject-modal__field">
          <span>Причина отклонения</span>
          <textarea
            v-model="rejectReason"
            rows="3"
            placeholder="Укажите причину..."
          />
        </label>
        <p v-if="rejectError" class="reject-modal__err">{{ rejectError }}</p>
        <div class="reject-modal__actions">
          <button class="reject-modal__cancel" @click="closeRejectModal">Отмена</button>
          <button
            class="reject-modal__submit reject-modal__submit--danger"
            :disabled="rejectLoading || !rejectReason.trim()"
            @click="onReject"
          >
            {{ rejectLoading ? 'Обработка…' : 'Отклонить' }}
          </button>
        </div>
      </div>
    </ModalDialog>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { RefreshCw, FileSearch, Loader2, Check, X } from 'lucide-vue-next'
import AppHeader from '../../components/common/AppHeader.vue'
import AppFooter from '../../components/common/AppFooter.vue'
import ModalDialog from '../../components/common/ModalDialog.vue'
import { useClaims } from '../../composables/useClaims.js'

const {
  loading,
  universityClaims,
  fetchUniversityClaims,
  approveClaim,
  rejectClaim,
} = useClaims()

const statusTab = ref('all')
const actionLoading = ref(null)
const rejectingClaim = ref(null)
const rejectReason = ref('')
const rejectError = ref('')
const rejectLoading = ref(false)

const allClaims = computed(() => universityClaims.value || [])

const filteredClaims = computed(() => {
  if (statusTab.value === 'all') return allClaims.value
  return allClaims.value.filter(c => c.status === statusTab.value)
})

function shortId(uuid) {
  if (!uuid) return '—'
  return uuid.length > 12 ? uuid.slice(0, 8) + '…' + uuid.slice(-4) : uuid
}

function getStatusLabel(status) {
  const map = {
    PENDING: 'На рассмотрении',
    APPROVED: 'Одобрена',
    REJECTED: 'Отклонена',
  }
  return map[status] || status || 'Неизвестно'
}

function formatDate(dateStr) {
  if (!dateStr) return '—'
  try {
    const d = new Date(dateStr)
    return d.toLocaleDateString('ru-RU', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    })
  } catch {
    return dateStr
  }
}

async function fetchClaims() {
  await fetchUniversityClaims()
}

async function onApprove(claim) {
  actionLoading.value = claim.requestId
  try {
    await approveClaim(claim.requestId)
  } catch (err) {
    alert('Ошибка: ' + err.message)
  } finally {
    actionLoading.value = null
  }
}

function openRejectModal(claim) {
  rejectingClaim.value = claim
  rejectReason.value = ''
  rejectError.value = ''
  rejectLoading.value = false
}

function closeRejectModal() {
  rejectingClaim.value = null
  rejectReason.value = ''
  rejectError.value = ''
  rejectLoading.value = false
}

async function onReject() {
  if (!rejectingClaim.value || !rejectReason.value.trim()) return
  rejectError.value = ''
  rejectLoading.value = true
  try {
    await rejectClaim(rejectingClaim.value.requestId, rejectReason.value.trim())
    closeRejectModal()
  } catch (err) {
    rejectError.value = err.message || 'Не удалось отклонить заявку'
  } finally {
    rejectLoading.value = false
  }
}

onMounted(() => {
  fetchClaims()
})
</script>

<style scoped>
.claims-view { min-height: 100vh; }
.main-content { flex: 1; }

/* ===========================
   HERO
   =========================== */
.claims__hero {
  position: relative;
  background: #0d1f3c;
  padding: 72px 0 80px;
}

.claims__hero-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}

.claims__hero-grid-lines {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(
      rgba(255, 255, 255, 0.03) 1px,
      transparent 1px
    ),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.claims__hero-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.claims__hero-glow--1 {
  width: 500px;
  height: 500px;
  top: -150px;
  right: -100px;
  background: rgba(38, 75, 130, 0.45);
}

.claims__hero-glow--2 {
  width: 380px;
  height: 380px;
  bottom: -120px;
  left: -60px;
  background: rgba(72, 184, 194, 0.2);
}

.claims__hero-inner {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.claims__hero-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: var(--space-6);
}

.claims__title {
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: var(--font-weight-bold);
  color: #fff;
  line-height: 1.1;
  letter-spacing: -0.02em;
}

.claims__title-accent {
  color: var(--color-sea-clear);
}

.claims__subtitle {
  max-width: 540px;
  font-size: var(--font-size-base);
  color: rgba(255, 255, 255, 0.65);
  line-height: 1.7;
  margin: 0;
}

.claims__hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-3);
  justify-content: center;
}

.claims__btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 24px;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  font-family: var(--font-family);
  border-radius: var(--radius-base);
  text-decoration: none;
  cursor: pointer;
  transition: all var(--transition-base);
  border: 2px solid transparent;
  background: transparent;
  color: #fff;
  border-color: rgba(255, 255, 255, 0.3);
}

.claims__btn:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.5);
}

/* ===========================
   FILTERS
   =========================== */
.claims__filters {
  background: #fff;
  border-bottom: 1px solid var(--color-gray-blue);
}

.filters-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-4) 0;
  flex-wrap: wrap;
  gap: var(--space-3);
}

.filters-bar__tabs {
  display: flex;
  gap: var(--space-2);
  flex-wrap: wrap;
}

.tab-btn {
  padding: 8px 16px;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  font-family: var(--font-family);
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-full);
  background: #fff;
  color: var(--color-black);
  cursor: pointer;
  transition: all 0.15s ease;
}

.tab-btn:hover {
  background: #f1f5f9;
}

.tab-btn--active {
  background: var(--color-main-blue);
  color: #fff;
  border-color: var(--color-main-blue);
}

.filters-bar__count {
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
}

/* ===========================
   LIST SECTION
   =========================== */
.claims__list-section {
  padding: 48px 0 80px;
  background: #f8fafc;
  overflow-x: hidden;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-10);
  text-align: center;
  color: var(--color-pale-black);
}

.empty-state svg {
  opacity: 0.4;
  color: var(--color-main-blue);
}

.empty-state h3 {
  margin: 0;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
}

.empty-state p {
  margin: 0;
  font-size: var(--font-size-sm);
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-10);
  text-align: center;
  color: var(--color-pale-black);
}

.spin {
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.claims-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: var(--space-5);
}

/* ===========================
   CLAIM CARD
   =========================== */
.claim-card {
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  overflow: hidden;
  transition: box-shadow 0.15s ease;
}

.claim-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.claim-card--pending {
  border-color: #fbbf24;
}

.claim-card--approved {
  border-color: #22c55e;
}

.claim-card--rejected {
  border-color: #ef4444;
}

.claim-card__header {
  padding: var(--space-4) var(--space-5);
  background: #f8fafc;
  border-bottom: 1px solid var(--color-gray-blue);
}

.claim-card__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.claim-card__number {
  font-size: var(--font-size-base);
  font-weight: 700;
  color: var(--color-black);
}

.claim-card__id {
  font-size: var(--font-size-base);
  font-weight: 700;
  color: var(--color-black);
}

.claim-status {
  padding: 4px 12px;
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
}

.claim-status--pending {
  background: #fef3c7;
  color: #92400e;
}

.claim-status--approved {
  background: #dcfce7;
  color: #166534;
}

.claim-status--rejected {
  background: #fee2e2;
  color: #991b1b;
}

.claim-card__body {
  padding: var(--space-5);
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.claim-detail {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: 6px 0;
  font-size: var(--font-size-sm);
}

.claim-detail-k {
  color: var(--color-pale-black);
  font-weight: 500;
  min-width: 120px;
}

.claim-detail-v {
  color: var(--color-black);
  font-weight: var(--font-weight-semibold);
}

.claim-detail-v--mono {
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 0.7rem;
  word-break: break-all;
}

.claim-rejection-reason {
  margin: var(--space-3) 0 0;
  font-size: var(--font-size-sm);
  color: #991b1b;
  padding: 8px 12px;
  background: rgba(239, 68, 68, 0.08);
  border-radius: var(--radius-base);
}

.claim-card__actions {
  padding: var(--space-4) var(--space-5);
  border-top: 1px solid var(--color-gray-blue);
  display: flex;
  gap: var(--space-3);
  background: #f8fafc;
}

.action-btn {
  flex: 1;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 16px;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  font-family: var(--font-family);
  border: none;
  border-radius: var(--radius-base);
  cursor: pointer;
  transition: all 0.15s ease;
}

.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.action-btn--approve {
  background: #16a34a;
  color: #fff;
}

.action-btn--approve:hover:not(:disabled) {
  background: #15803d;
}

.action-btn--reject {
  background: #ef4444;
  color: #fff;
}

.action-btn--reject:hover:not(:disabled) {
  background: #dc2626;
}

/* ===========================
   REJECT MODAL
   =========================== */
.reject-modal {
  padding: var(--space-6);
}

.reject-modal__title {
  margin: 0 0 var(--space-2);
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
}

.reject-modal__desc {
  margin: 0 0 var(--space-4);
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
  line-height: 1.5;
}

.reject-modal__field {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: var(--color-black);
}

.reject-modal__field textarea {
  font-family: var(--font-family);
  font-size: var(--font-size-sm);
  padding: 10px 12px;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-base);
  resize: vertical;
}

.reject-modal__field textarea:focus {
  outline: none;
  border-color: var(--color-main-blue);
  box-shadow: 0 0 0 3px rgba(38, 75, 130, 0.1);
}

.reject-modal__err {
  color: var(--color-red);
  font-size: var(--font-size-sm);
  margin: var(--space-2) 0 0;
}

.reject-modal__actions {
  display: flex;
  gap: var(--space-3);
  margin-top: var(--space-5);
  justify-content: flex-end;
}

.reject-modal__cancel {
  padding: 10px 20px;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  font-family: var(--font-family);
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-base);
  background: #fff;
  color: var(--color-black);
  cursor: pointer;
}

.reject-modal__submit {
  padding: 10px 20px;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  font-family: var(--font-family);
  border: none;
  border-radius: var(--radius-base);
  color: #fff;
  cursor: pointer;
}

.reject-modal__submit--danger {
  background: #ef4444;
}

.reject-modal__submit--danger:hover:not(:disabled) {
  background: #dc2626;
}

.reject-modal__submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* ===========================
   АДАПТИВ
   =========================== */
@media (max-width: 768px) {
  .claims__hero { padding: 44px 0 48px; }
  .claims__title { font-size: clamp(1.5rem, 6vw, 2.2rem); }
  .claims-grid { grid-template-columns: 1fr; }
}

@media (max-width: 540px) {
  .claims__hero { padding: 32px 0 40px; }
  .claims__title { font-size: 1.5rem; line-height: 1.2; }
  .filters-bar { flex-direction: column; align-items: flex-start; }
}
</style>
