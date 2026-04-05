<template>
  <div class="public-verify-view">
    <AppHeader />
    <main class="main-content">
      <!-- ===== HERO ===== -->
      <section class="verify__hero">
        <div class="verify__hero-bg" aria-hidden="true">
          <div class="verify__hero-grid-lines" />
          <div class="verify__hero-glow verify__hero-glow--1" />
          <div class="verify__hero-glow verify__hero-glow--2" />
        </div>

        <div class="container verify__hero-inner">
          <div class="verify__hero-content">
            <h1 class="verify__title">
              Проверка диплома<br />
              <span class="verify__title-accent">Верификация по QR-коду</span>
            </h1>
            <p class="verify__subtitle">
              Результаты проверки подлинности цифрового диплома
            </p>
          </div>
        </div>
      </section>

      <!-- ===== Loading ===== -->
      <section v-if="loading" class="verify__loading-section">
        <div class="container">
          <div class="loading-state">
            <Loader2 :size="32" class="spin" />
            <p>Проверка подлинности диплома...</p>
          </div>
        </div>
      </section>

      <!-- ===== Error: invalid link ===== -->
      <section v-else-if="linkError" class="verify__error-section">
        <div class="container">
          <div class="error-card">
            <AlertCircle :size="48" class="error-icon" />
            <h2 class="error-title">Некорректная ссылка</h2>
            <p class="error-desc">
              Ссылка для проверки не содержит необходимых данных. Убедитесь, что QR-код был сгенерирован корректно.
            </p>
            <p v-if="linkError" class="error-detail">{{ linkError }}</p>
          </div>
        </div>
      </section>

      <!-- ===== Error: verification failed ===== -->
      <section v-else-if="verifyError" class="verify__error-section">
        <div class="container">
          <div class="error-card error-card--api">
            <AlertCircle :size="48" class="error-icon" />
            <h2 class="error-title">Ошибка проверки</h2>
            <p class="error-desc">{{ verifyError }}</p>
          </div>
        </div>
      </section>

      <!-- ===== Result ===== -->
      <section v-else-if="verifyResult" class="verify__result-section">
        <div class="container">
          <!-- Status Banner -->
          <div
            class="status-banner"
            :class="`status-banner--${isVerified ? 'verified' : 'not-verified'}`"
          >
            <div class="status-banner__icon">
              <CheckCircle v-if="isVerified" :size="32" />
              <AlertCircle v-else :size="32" />
            </div>
            <div class="status-banner__body">
              <h2 class="status-banner__title">
                {{ isVerified ? 'Диплом подлинный' : 'Диплом не подтверждён' }}
              </h2>
              <p class="status-banner__desc">
                {{ isVerified
                  ? 'Данные диплома успешно верифицированы через реестр.'
                  : 'Проверка не пройдена. Диплом может быть поддельным или отозванным.'
                }}
              </p>
              <span v-if="resultData.reasonCode" class="status-banner__reason">
                Код причины: <code>{{ resultData.reasonCode }}</code>
              </span>
            </div>
          </div>

          <!-- Verification Meta -->
          <div class="verify-meta">
            <div class="meta-item">
              <span class="meta-k">Verification ID</span>
              <span class="meta-v mono">{{ resultData.verificationId || '—' }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-k">Время проверки</span>
              <span class="meta-v">{{ formatDateTime(resultData.verifiedAt) }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-k">Статус</span>
              <span class="meta-v">
                <span class="status-badge" :class="`status-badge--${resultData.status?.toLowerCase()}`">
                  {{ resultData.status || '—' }}
                </span>
              </span>
            </div>
          </div>

          <!-- Diploma Data Card -->
          <div class="diploma-card">
            <div class="diploma-card__header">
              <GraduationCap :size="24" class="diploma-card__icon" />
              <h3 class="diploma-card__title">Данные диплома</h3>
            </div>

            <div class="diploma-card__body">
              <div class="detail-row">
                <span class="detail-k">Certificate ID</span>
                <span class="detail-v mono">{{ certData.certificateId || '—' }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-k">Номер диплома</span>
                <span class="detail-v">{{ certData.diplomaNumber || '—' }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-k">Код университета</span>
                <span class="detail-v mono">{{ certData.universityCode || '—' }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-k">Университет</span>
                <span class="detail-v">{{ certData.universityName || '—' }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-k">ФИО выпускника</span>
                <span class="detail-v">{{ certData.fullNameMasked || '—' }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-k">Специальность</span>
                <span class="detail-v">{{ certData.specialty || '—' }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-k">Год выпуска</span>
                <span class="detail-v">{{ certData.graduationYear || '—' }}</span>
              </div>
            </div>
          </div>

          <!-- Checks Card -->
          <div class="checks-card">
            <div class="checks-card__header">
              <Shield :size="20" />
              <h3 class="checks-card__title">Результаты проверок</h3>
            </div>

            <div class="checks-card__body">
              <div class="check-item" :class="`check-item--${checkStatus(checksData.token)}`">
                <span class="check-k">Токен</span>
                <span class="check-v">{{ checksData.token || '—' }}</span>
              </div>
              <div class="check-item" :class="`check-item--${checkStatus(checksData.ttl)}`">
                <span class="check-k">Срок действия (TTL)</span>
                <span class="check-v">{{ checksData.ttl || '—' }}</span>
              </div>
              <div class="check-item" :class="`check-item--${checkStatus(checksData.revocation)}`">
                <span class="check-k">Отзыв</span>
                <span class="check-v">{{ checksData.revocation || '—' }}</span>
              </div>
              <div class="check-item" :class="`check-item--${checkStatus(checksData.payloadHash)}`">
                <span class="check-k">Хеш полезной нагрузки</span>
                <span class="check-v mono">{{ checksData.payloadHash || '—' }}</span>
              </div>
              <div class="check-item" :class="`check-item--${checkStatus(checksData.signature)}`">
                <span class="check-k">Подпись</span>
                <span class="check-v">{{ checksData.signature || '—' }}</span>
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
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import {
  CheckCircle,
  AlertCircle,
  Loader2,
  GraduationCap,
  Shield,
} from 'lucide-vue-next'
import AppHeader from '../../components/common/AppHeader.vue'
import AppFooter from '../../components/common/AppFooter.vue'
import { api } from '../../api/api.js'

const route = useRoute()

const loading = ref(true)
const linkError = ref('')
const verifyError = ref('')
const verifyResult = ref(null)

const resultData = computed(() => verifyResult.value || {})
const certData = computed(() => verifyResult.value?.certificate || {})
const checksData = computed(() => verifyResult.value?.checks || {})

const isVerified = computed(() => {
  const s = String(resultData.value?.status || '').toLowerCase()
  return s === 'verified' || s === 'valid' || s === 'active' || s === 'success'
})

function checkStatus(val) {
  if (!val) return 'unknown'
  const s = String(val).toLowerCase()
  if (s === 'ok' || s === 'valid' || s === 'verified' || s === 'active' || s === 'pass' || s === 'true') return 'ok'
  if (s === 'failed' || s === 'invalid' || s === 'revoked' || s === 'expired' || s === 'false' || s === 'error') return 'fail'
  return 'unknown'
}

function formatDateTime(dateStr) {
  if (!dateStr) return '—'
  try {
    return new Date(dateStr).toLocaleString('ru-RU', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
    })
  } catch {
    return dateStr
  }
}

onMounted(async () => {
  const { certificateId, token } = route.query

  if (!certificateId && !token) {
    // Пробуем распарсить из pathname если ссылка вида /api/public/verify?certificateId=...&token=...
    const params = new URLSearchParams(window.location.search)
    const cid = params.get('certificateId')
    const tkn = params.get('token')
    if (!cid || !tkn) {
      loading.value = false
      linkError.value = 'В ссылке отсутствуют параметры certificateId и/или token.'
      return
    }
    certificateId = cid
    token = tkn
  }

  if (!certificateId || !token) {
    loading.value = false
    linkError.value = 'В ссылке отсутствуют параметры certificateId и/или token.'
    return
  }

  try {
    const res = await api.verifyByQr({ certificateId, token })
    verifyResult.value = res
  } catch (err) {
    verifyError.value = err.message || 'Не удалось выполнить проверку подлинности.'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.public-verify-view { min-height: 100vh; }
.main-content { flex: 1; }

/* ===========================
   HERO
   =========================== */
.verify__hero {
  position: relative;
  background: #0d1f3c;
  padding: 60px 0 64px;
}

.verify__hero-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}

.verify__hero-grid-lines {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(
      rgba(255, 255, 255, 0.03) 1px,
      transparent 1px
    ),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.verify__hero-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.verify__hero-glow--1 {
  width: 500px;
  height: 500px;
  top: -150px;
  right: -100px;
  background: rgba(38, 75, 130, 0.45);
}

.verify__hero-glow--2 {
  width: 380px;
  height: 380px;
  bottom: -120px;
  left: -60px;
  background: rgba(72, 184, 194, 0.2);
}

.verify__hero-inner {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.verify__hero-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: var(--space-4);
}

.verify__title {
  font-size: clamp(1.6rem, 3.5vw, 2.4rem);
  font-weight: var(--font-weight-bold);
  color: #fff;
  line-height: 1.15;
  letter-spacing: -0.02em;
}

.verify__title-accent {
  color: var(--color-sea-clear);
}

.verify__subtitle {
  max-width: 480px;
  font-size: var(--font-size-base);
  color: rgba(255, 255, 255, 0.6);
  line-height: 1.6;
  margin: 0;
}

/* ===========================
   LOADING
   =========================== */
.verify__loading-section {
  padding: 80px 0;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
  text-align: center;
  color: var(--color-pale-black);
}

.spin {
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ===========================
   ERROR
   =========================== */
.verify__error-section {
  padding: 60px 0 80px;
}

.error-card {
  max-width: 560px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: var(--space-3);
  padding: var(--space-8) var(--space-6);
  background: #fff;
  border: 1px solid #fecaca;
  border-radius: var(--radius-lg);
}

.error-card--api {
  border-color: var(--color-gray-blue);
}

.error-icon {
  color: #ef4444;
  flex-shrink: 0;
}

.error-title {
  margin: 0;
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
}

.error-desc {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
  line-height: 1.6;
}

.error-detail {
  margin: 0;
  font-size: 0.75rem;
  color: #991b1b;
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  word-break: break-all;
}

/* ===========================
   RESULT
   =========================== */
.verify__result-section {
  padding: 40px 0 80px;
}

/* Status Banner */
.status-banner {
  display: flex;
  align-items: flex-start;
  gap: var(--space-4);
  padding: var(--space-6);
  border-radius: var(--radius-lg);
  margin-bottom: var(--space-5);
}

.status-banner--verified {
  background: #f0fdf4;
  border: 2px solid #22c55e;
}

.status-banner--not-verified {
  background: #fef2f2;
  border: 2px solid #ef4444;
}

.status-banner__icon {
  flex-shrink: 0;
  margin-top: 2px;
}

.status-banner--verified .status-banner__icon {
  color: #16a34a;
}

.status-banner--not-verified .status-banner__icon {
  color: #dc2626;
}

.status-banner__body {
  flex: 1;
}

.status-banner__title {
  margin: 0 0 4px;
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
}

.status-banner__desc {
  margin: 0 0 8px;
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
  line-height: 1.5;
}

.status-banner__reason {
  display: inline-block;
  font-size: 0.75rem;
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  color: #6b7280;
  background: rgba(0, 0, 0, 0.05);
  padding: 3px 10px;
  border-radius: var(--radius-base);
}

.status-banner__reason code {
  color: #1a1a1a;
  font-weight: 600;
}

/* Verify Meta */
.verify-meta {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--space-3);
  margin-bottom: var(--space-5);
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: var(--space-3);
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-base);
}

.meta-k {
  font-size: 0.75rem;
  font-weight: 600;
  color: var(--color-pale-black);
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.meta-v {
  font-size: var(--font-size-sm);
  color: var(--color-black);
  font-weight: 500;
}

.meta-v.mono {
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 0.7rem;
  word-break: break-all;
}

.status-badge {
  display: inline-block;
  padding: 3px 10px;
  border-radius: var(--radius-full);
  font-size: 0.7rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.status-badge--verified,
.status-badge--valid,
.status-badge--active,
.status-badge--success {
  background: #dcfce7;
  color: #166534;
}

.status-badge--not-verified,
.status-badge--failed,
.status-badge--revoked,
.status-badge--expired,
.status-badge--rejected {
  background: #fee2e2;
  color: #991b1b;
}

/* Diploma Card */
.diploma-card {
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  margin-bottom: var(--space-5);
  overflow: hidden;
}

.diploma-card__header {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-4) var(--space-5);
  background: #f8fafc;
  border-bottom: 1px solid var(--color-gray-blue);
}

.diploma-card__icon {
  color: var(--color-main-blue);
  flex-shrink: 0;
}

.diploma-card__title {
  margin: 0;
  font-size: var(--font-size-base);
  font-weight: 700;
  color: var(--color-black);
}

.diploma-card__body {
  padding: var(--space-5);
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

/* Checks Card */
.checks-card {
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.checks-card__header {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-4) var(--space-5);
  background: #f8fafc;
  border-bottom: 1px solid var(--color-gray-blue);
  color: var(--color-black);
}

.checks-card__title {
  margin: 0;
  font-size: var(--font-size-base);
  font-weight: 700;
}

.checks-card__body {
  padding: var(--space-5);
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.check-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  border-radius: var(--radius-base);
  font-size: var(--font-size-sm);
}

.check-item--ok {
  background: #f0fdf4;
}

.check-item--fail {
  background: #fef2f2;
}

.check-item--unknown {
  background: #f8fafc;
}

.check-k {
  font-weight: 600;
  color: var(--color-pale-black);
  min-width: 160px;
}

.check-v {
  color: var(--color-black);
  font-weight: 500;
  text-align: right;
}

.check-v.mono {
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 0.7rem;
  word-break: break-all;
}

/* Detail row (shared) */
.detail-row {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: var(--space-2);
  padding: 6px 0;
}

.detail-k {
  color: var(--color-pale-black);
  font-weight: 500;
  min-width: 140px;
  font-size: var(--font-size-sm);
}

.detail-v {
  color: var(--color-black);
  font-weight: var(--font-weight-semibold);
  font-size: var(--font-size-sm);
  text-align: right;
}

.detail-v.mono {
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 0.7rem;
  word-break: break-all;
}

/* ===========================
   RESPONSIVE
   =========================== */
@media (max-width: 768px) {
  .verify__hero { padding: 40px 0 48px; }
  .verify__title { font-size: clamp(1.3rem, 6vw, 1.8rem); }
  .status-banner { flex-direction: column; }
  .verify-meta { grid-template-columns: 1fr; }
  .detail-row { flex-direction: column; gap: 2px; }
  .detail-k { min-width: auto; }
  .detail-v { text-align: left; }
  .check-item { flex-direction: column; align-items: flex-start; gap: 4px; }
  .check-v { text-align: left; }
}

@media (max-width: 540px) {
  .verify__hero { padding: 32px 0 40px; }
  .verify__title { font-size: 1.3rem; line-height: 1.2; }
}
</style>
