<template>
  <div class="search-diploma-view">
    <AppHeader />
    <main class="main-content">
      <!-- ===== HERO ===== -->
      <section class="search-diploma__hero">
        <div class="search-diploma__hero-bg" aria-hidden="true">
          <div class="search-diploma__hero-grid-lines" />
          <div class="search-diploma__hero-glow search-diploma__hero-glow--1" />
          <div class="search-diploma__hero-glow search-diploma__hero-glow--2" />
        </div>

        <div class="container search-diploma__hero-inner">
          <div class="search-diploma__hero-content">
            <h1 class="search-diploma__title">
              Поиск диплома<br />
              <span class="search-diploma__title-accent">Привязка к профилю</span>
            </h1>

            <p class="search-diploma__subtitle">
              Найдите свой диплом по номеру и коду университета, отправьте заявку на привязку.
            </p>
          </div>
        </div>
      </section>

      <!-- ===== Форма поиска ===== -->
      <section class="search-diploma__search-section">
        <div class="container">
          <div class="search-card">
            <h2 class="search-card__title">Найти диплом</h2>
            <p class="search-card__desc">
              Поиск в реестре: GET /api/public/certificates/search — только номер диплома. Код университета сверяется с ответом (не передаётся в query).
            </p>

            <form class="search-form" @submit.prevent="onSearch">
              <div class="search-form__fields">
                <label class="search-form__field">
                  <span>Номер диплома</span>
                  <input
                    v-model="form.diplomaNumber"
                    type="text"
                    required
                    placeholder="DIP-2026-001"
                  />
                </label>
                <label class="search-form__field">
                  <span>Код университета</span>
                  <input
                    v-model="form.universityCode"
                    type="text"
                    required
                    placeholder="AITU-01"
                  />
                </label>
              </div>

              <p v-if="searchErrorMessage" class="search-form__err">{{ searchErrorMessage }}</p>
              <p v-if="searchSuccessMessage" class="search-form__ok">{{ searchSuccessMessage }}</p>

              <button
                type="submit"
                class="search-form__btn"
                :disabled="loading || !form.diplomaNumber.trim() || !form.universityCode.trim()"
              >
                {{ loading ? 'Поиск…' : 'Найти диплом' }}
              </button>
            </form>

            <!-- Результат поиска: диплом найден -->
            <Transition name="fade">
              <div v-if="foundDiploma" class="diploma-result diploma-result--found">
                <div class="diploma-result__header">
                  <CheckCircle :size="24" class="diploma-result__icon" />
                  <div>
                    <h3 class="diploma-result__title">Диплом найден</h3>
                    <p class="diploma-result__desc">
                      Данные из публичного реестра. После отправки заявки вуз сможет подтвердить привязку диплома к вашему профилю.
                    </p>
                  </div>
                </div>

                <div class="diploma-result__details">
                  <div class="detail-row">
                    <span class="detail-k">Номер диплома</span>
                    <span class="detail-v">{{ foundDiploma.diplomaNumber }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="detail-k">ФИО выпускника</span>
                    <span class="detail-v">{{ foundDiploma.fullName || foundDiploma.maskedFullName || '—' }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="detail-k">Специальность</span>
                    <span class="detail-v">{{ foundDiploma.specialty }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="detail-k">Год выпуска</span>
                    <span class="detail-v">{{ foundDiploma.graduationYear }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="detail-k">Университет</span>
                    <span class="detail-v">{{ foundDiploma.universityName }}</span>
                  </div>
                </div>
              </div>
            </Transition>

            <!-- Результат поиска: диплом не найден -->
            <Transition name="fade">
              <div v-if="notFound" class="diploma-result diploma-result--not-found">
                <AlertCircle :size="24" class="diploma-result__icon" />
                <h3 class="diploma-result__title">Диплом не найден</h3>
                <p class="diploma-result__desc">
                  Проверьте номер диплома и код университета. Если вы уверены в данных, обратитесь в деканат.
                </p>
              </div>
            </Transition>
          </div>
        </div>
      </section>

      <!-- ===== Мои заявки ===== -->
      <section class="my-claims-section">
        <div class="container">
          <div class="claims-card">
            <h2 class="claims-card__title">Привязка к профилю</h2>
            <p class="claims-card__desc">
              После поиска диплома вы можете отправить заявку в вуз на подтверждение привязки диплома к вашему профилю.
            </p>

            <div v-if="foundDiploma" class="claims-list">
              <div class="claim-item claim-item--pending">
                <div class="claim-item__header">
                  <h3 class="claim-item__number">{{ foundDiploma.diplomaNumber }}</h3>
                  <span class="claim-status claim-status--pending">
                    {{ claimSent ? 'Заявка отправлена' : 'Готово к отправке' }}
                  </span>
                </div>

                <div class="claim-item__details">
                  <div class="detail-row">
                    <span class="detail-k">Университет</span>
                    <span class="detail-v">{{ foundDiploma.universityName || '—' }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="detail-k">Код университета</span>
                    <span class="detail-v">{{ form.universityCode || '—' }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="detail-k">Специальность</span>
                    <span class="detail-v">{{ foundDiploma.specialty || '—' }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="detail-k">Год выпуска</span>
                    <span class="detail-v">{{ foundDiploma.graduationYear || '—' }}</span>
                  </div>
                </div>

                <button
                  class="claim-btn"
                  type="button"
                  :disabled="claimLoading || claimSent"
                  @click="onClaim"
                >
                  {{
                    claimLoading
                      ? 'Отправка…'
                      : claimSent
                        ? 'Заявка отправлена'
                        : 'Отправить заявку на подтверждение'
                  }}
                </button>

                <p v-if="claimError" class="claim-err">{{ claimError }}</p>
                <p v-if="claimSuccess" class="claim-ok">{{ claimSuccess }}</p>
              </div>
            </div>

            <div v-else class="claims-empty">
              <FileSearch :size="40" />
              <p>Сначала найдите диплом в реестре, затем отправьте заявку на привязку.</p>
            </div>
          </div>
        </div>
      </section>
    </main>
    <AppFooter />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { CheckCircle, AlertCircle, FileSearch } from 'lucide-vue-next'
import AppHeader from '../../components/common/AppHeader.vue'
import AppFooter from '../../components/common/AppFooter.vue'
import { useClaims } from '../../composables/useClaims.js'
import { api } from '../../api/api'
import { useAuth } from '../../composables/useAuth.js'

const { token: accessToken } = useAuth()
const {
  loading,
  searchError,
  searchUnclaimed,
} = useClaims()

const form = ref({
  diplomaNumber: '',
  universityCode: '',
})

const foundDiploma = ref(null)
const notFound = ref(false)
const searchErrorMessage = ref('')
const searchSuccessMessage = ref('')

const claimLoading = ref(false)
const claimError = ref('')
const claimSuccess = ref('')
const claimSent = ref(false)

async function onSearch() {
  searchErrorMessage.value = ''
  searchSuccessMessage.value = ''
  foundDiploma.value = null
  notFound.value = false

  claimLoading.value = false
  claimError.value = ''
  claimSuccess.value = ''
  claimSent.value = false

  const result = await searchUnclaimed(
    form.value.diplomaNumber,
    form.value.universityCode
  )

  if (result) {
    foundDiploma.value = result
    searchSuccessMessage.value = 'Диплом найден в публичном реестре.'
  } else {
    notFound.value = true
    searchErrorMessage.value =
      searchError.value || 'Диплом с таким номером не найден.'
  }
}

async function onClaim() {
  claimError.value = ''
  claimSuccess.value = ''

  if (!foundDiploma.value) {
    claimError.value = 'Сначала найдите диплом.'
    return
  }

  const token = accessToken.value
  if (!token) {
    claimError.value = 'Необходимо войти в систему как студент.'
    return
  }

  claimLoading.value = true

  try {
    const payload = {
      universityCode: String(form.value.universityCode || '').trim(),
      diplomaNumber: String(
        foundDiploma.value.diplomaNumber || form.value.diplomaNumber || ''
      ).trim(),
    }

    if (!payload.universityCode || !payload.diplomaNumber) {
      throw new Error('Не заполнены данные для отправки заявки.')
    }

    await api.createStudentDiplomaClaim(payload, token)

    claimSent.value = true
    claimSuccess.value = 'Заявка на привязку диплома успешно отправлена в вуз.'
  } catch (e) {
    claimError.value = e?.message || 'Не удалось отправить заявку.'
  } finally {
    claimLoading.value = false
  }
}
</script>

<style scoped>
.search-diploma-view { min-height: 100vh; }
.main-content { flex: 1; }

/* ===========================
   HERO
   =========================== */
.search-diploma__hero {
  position: relative;
  background: #0d1f3c;
  padding: 72px 0 80px;
}

.search-diploma__hero-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}

.search-diploma__hero-grid-lines {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(
      rgba(255, 255, 255, 0.03) 1px,
      transparent 1px
    ),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.search-diploma__hero-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.search-diploma__hero-glow--1 {
  width: 500px;
  height: 500px;
  top: -150px;
  right: -100px;
  background: rgba(38, 75, 130, 0.45);
}

.search-diploma__hero-glow--2 {
  width: 380px;
  height: 380px;
  bottom: -120px;
  left: -60px;
  background: rgba(72, 184, 194, 0.2);
}

.search-diploma__hero-inner {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.search-diploma__hero-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: var(--space-6);
}

.search-diploma__title {
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: var(--font-weight-bold);
  color: #fff;
  line-height: 1.1;
  letter-spacing: -0.02em;
}

.search-diploma__title-accent {
  color: var(--color-sea-clear);
}

.search-diploma__subtitle {
  max-width: 540px;
  font-size: var(--font-size-base);
  color: rgba(255, 255, 255, 0.65);
  line-height: 1.7;
  margin: 0;
}

/* ===========================
   SEARCH SECTION
   =========================== */
.search-diploma__search-section {
  padding: 48px 0;
  background: #f8fafc;
  overflow-x: hidden;
}

.search-card {
  max-width: 720px;
  margin: 0 auto;
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
}

.search-card__title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  margin: 0 0 var(--space-2);
}

.search-card__desc {
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
  margin: 0 0 var(--space-5);
  line-height: 1.5;
}

/* ===========================
   SEARCH FORM
   =========================== */
.search-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.search-form__fields {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-4);
}

.search-form__field {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: var(--color-black);
}

.search-form__field input {
  font-family: var(--font-family);
  font-size: var(--font-size-sm);
  padding: 10px 12px;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-base);
}

.search-form__field input:focus {
  outline: none;
  border-color: var(--color-main-blue);
  box-shadow: 0 0 0 3px rgba(38, 75, 130, 0.1);
}

.search-form__err {
  color: var(--color-red);
  font-size: var(--font-size-sm);
  margin: 0;
}

.search-form__ok {
  color: #166534;
  font-size: var(--font-size-sm);
  margin: 0;
}

.search-form__btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 12px 24px;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  font-family: var(--font-family);
  color: #fff;
  background: var(--color-main-blue);
  border: none;
  border-radius: var(--radius-base);
  cursor: pointer;
  transition: background 0.15s ease, opacity 0.15s ease;
}

.search-form__btn:hover:not(:disabled) {
  background: #1a5bbd;
}

.search-form__btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* ===========================
   DIPLOMA RESULT
   =========================== */
.diploma-result {
  margin-top: var(--space-5);
  padding: var(--space-5);
  border-radius: var(--radius-lg);
  border: 1px solid;
}

.diploma-result--found {
  background: #f0fdf4;
  border-color: #bbf7d0;
}

.diploma-result--not-found {
  background: #fef2f2;
  border-color: #fecaca;
  text-align: center;
}

.diploma-result__header {
  display: flex;
  align-items: flex-start;
  gap: var(--space-3);
  margin-bottom: var(--space-4);
}

.diploma-result__icon {
  flex-shrink: 0;
  color: #16a34a;
}

.diploma-result--not-found .diploma-result__icon {
  color: #dc2626;
}

.diploma-result__title {
  margin: 0 0 4px;
  font-size: var(--font-size-base);
  font-weight: 700;
  color: var(--color-black);
}

.diploma-result__desc {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
  line-height: 1.4;
}

.diploma-result__details {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  margin-bottom: var(--space-4);
}

.detail-row {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: var(--radius-base);
  font-size: var(--font-size-sm);
}

.detail-k {
  color: var(--color-pale-black);
  font-weight: 500;
  min-width: 140px;
}

.detail-v {
  color: var(--color-black);
  font-weight: var(--font-weight-semibold);
}

/* Claim button */
.claim-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 12px 24px;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  font-family: var(--font-family);
  color: #fff;
  background: #16a34a;
  border: none;
  border-radius: var(--radius-base);
  cursor: pointer;
  transition: background 0.15s ease, opacity 0.15s ease;
  width: 100%;
}

.claim-btn:hover:not(:disabled) {
  background: #15803d;
}

.claim-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.claim-err {
  color: #991b1b;
  font-size: var(--font-size-sm);
  margin: var(--space-2) 0 0;
  text-align: center;
}

.claim-ok {
  color: #166534;
  font-size: var(--font-size-sm);
  margin: var(--space-2) 0 0;
  text-align: center;
}

/* ===========================
   MY CLAIMS SECTION
   =========================== */
.my-claims-section {
  padding: 48px 0 80px;
  background: #fff;
  overflow-x: hidden;
}

.claims-card {
  max-width: 720px;
  margin: 0 auto;
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
}

.claims-card__title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  margin: 0 0 var(--space-2);
}

.claims-card__desc {
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
  margin: 0 0 var(--space-5);
  line-height: 1.5;
}

.claims-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.claim-item {
  padding: var(--space-4);
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  background: #fff;
}

.claim-item--pending {
  border-color: #fbbf24;
  background: #fffbeb;
}

.claim-item--approved {
  border-color: #22c55e;
  background: #f0fdf4;
}

.claim-item--rejected {
  border-color: #ef4444;
  background: #fef2f2;
}

.claim-item__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--space-3);
}

.claim-item__number {
  margin: 0;
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

.claim-item__details {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.claim-reason {
  margin: var(--space-3) 0 0;
  font-size: var(--font-size-sm);
  color: #991b1b;
  padding: 8px 12px;
  background: rgba(239, 68, 68, 0.08);
  border-radius: var(--radius-base);
}

.claims-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-8);
  text-align: center;
  color: var(--color-pale-black);
}

.claims-empty svg {
  color: var(--color-main-blue);
  opacity: 0.5;
}

.claims-empty p {
  margin: 0;
  font-size: var(--font-size-sm);
}

/* ===========================
   TRANSITIONS
   =========================== */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* ===========================
   АДАПТИВ
   =========================== */
@media (max-width: 768px) {
  .search-diploma__hero { padding: 44px 0 48px; }
  .search-diploma__title { font-size: clamp(1.5rem, 6vw, 2.2rem); }
  .search-form__fields { grid-template-columns: 1fr; }
  .detail-k { min-width: 100px; }
}

@media (max-width: 540px) {
  .search-diploma__hero { padding: 32px 0 40px; }
  .search-diploma__title { font-size: 1.5rem; line-height: 1.2; }
}
</style>