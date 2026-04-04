<template>
  <div class="share-link-generator">
    <div class="card">
      <h3 class="card__title">Генерация ссылки для проверки</h3>
      <p class="card__description">
        Создайте временную ссылку или QR-код для работодателя
      </p>

      <form class="form" @submit.prevent="generate">
        <label class="form__field">
          <span class="form__label">Срок действия (дни)</span>
          <input
            type="number"
            class="form__input"
            v-model.number="ttlDays"
            min="1"
            max="30"
            placeholder="7"
          />
        </label>

        <label class="form__field form__field--checkbox">
          <input type="checkbox" v-model="requireAuth" />
          <span>Требовать авторизацию для проверки</span>
        </label>

        <button type="submit" class="btn btn--primary" :disabled="loading">
          {{ loading ? 'Генерация...' : 'Сгенерировать ссылку' }}
        </button>
      </form>

      <div v-if="generatedLink" class="result">
        <label class="form__label">Ваша ссылка</label>
        <div class="result__link-row">
          <input
            type="text"
            class="form__input form__input--readonly"
            :value="generatedLink"
            readonly
          />
          <button class="btn btn--secondary" @click="copyLink">Копировать</button>
        </div>
      </div>
    </div>

    <div v-if="generatedLink" class="card qr-card">
      <h4 class="card__title card__title--sm">QR-код</h4>
      <div class="qr-wrapper">
        <img :src="qrImageUrl" alt="QR-код для проверки диплома" class="qr-image" />
        <p class="qr-caption">Отсканируйте для проверки диплома</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

/**
 * ShareLinkGenerator — генерация ссылок и QR-кодов для проверки диплома
 * @prop {String} diplomaHash
 */
const props = defineProps({
  diplomaHash: { type: String, default: '' }
})

const ttlDays = ref(7)
const requireAuth = ref(false)
const loading = ref(false)
const generatedLink = ref('')

const qrImageUrl = computed(() => {
  if (!generatedLink.value) return ''
  return `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${encodeURIComponent(generatedLink.value)}`
})

function generate() {
  loading.value = true
  setTimeout(() => {
    generatedLink.value = `${window.location.origin}/verify/${props.diplomaHash}?ttl=${ttlDays.value}`
    loading.value = false
  }, 500)
}

function copyLink() {
  navigator.clipboard.writeText(generatedLink.value)
}
</script>

<style scoped>
.share-link-generator {
  display: flex;
  flex-direction: column;
  gap: var(--space-5, 20px);
}

/* ── Card ── */
.card {
  background: #ffffff;
  border-radius: var(--radius-lg, 16px);
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0, 0, 0, 0.08));
  border: 1px solid var(--color-gray-blue, #d4dbe6);
  padding: var(--space-5, 20px);
  display: flex;
  flex-direction: column;
  gap: var(--space-4, 16px);
  transition: box-shadow 0.2s ease;
}

.card:hover {
  box-shadow: var(--shadow-base, 0 2px 8px rgba(0, 0, 0, 0.1));
}

.card__title {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--color-black, #1a1a1a);
  margin: 0;
}

.card__title--sm {
  font-size: 1rem;
}

.card__description {
  font-size: 0.875rem;
  color: var(--color-pale-black, #8c8c8c);
  margin: 0;
}

/* ── Form ── */
.form {
  display: flex;
  flex-direction: column;
  gap: var(--space-4, 16px);
}

.form__field {
  display: flex;
  flex-direction: column;
  gap: var(--space-1, 4px);
}

.form__field--checkbox {
  flex-direction: row;
  align-items: center;
  gap: var(--space-2, 8px);
  font-size: 0.875rem;
  color: var(--color-black, #1a1a1a);
}

.form__field--checkbox input[type="checkbox"] {
  width: 18px;
  height: 18px;
  accent-color: var(--color-main-blue, #264b82);
}

.form__label {
  font-size: 0.8125rem;
  font-weight: 500;
  color: var(--color-pale-black, #8c8c8c);
}

.form__input {
  padding: var(--space-2, 8px) var(--space-3, 12px);
  border: 1px solid var(--color-gray-blue, #d4dbe6);
  border-radius: var(--radius-base, 8px);
  font-size: 0.9375rem;
  color: var(--color-black, #1a1a1a);
  background: #ffffff;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  outline: none;
}

.form__input:focus {
  border-color: var(--color-sea-clear, #48b8c2);
  box-shadow: 0 0 0 3px rgba(72, 184, 194, 0.15);
}

.form__input--readonly {
  background: var(--color-gray-blue, #d4dbe6);
  color: var(--color-pale-black, #8c8c8c);
  cursor: default;
}

/* ── Buttons ── */
.btn {
  padding: var(--space-2, 8px) var(--space-4, 16px);
  border-radius: var(--radius-base, 8px);
  font-size: 0.9375rem;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: background 0.2s ease, box-shadow 0.2s ease;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn--primary {
  background: var(--color-main-blue, #264b82);
  color: #ffffff;
  align-self: flex-start;
}

.btn--primary:hover:not(:disabled) {
  background: #1e3d6b;
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0, 0, 0, 0.12));
}

.btn--secondary {
  background: #ffffff;
  color: var(--color-main-blue, #264b82);
  border: 1px solid var(--color-main-blue, #264b82);
  white-space: nowrap;
}

.btn--secondary:hover {
  background: var(--color-main-blue, #264b82);
  color: #ffffff;
}

/* ── Result ── */
.result {
  display: flex;
  flex-direction: column;
  gap: var(--space-2, 8px);
}

.result__link-row {
  display: flex;
  align-items: center;
  gap: var(--space-2, 8px);
}

.result__link-row .form__input {
  flex: 1;
}

/* ── QR ── */
.qr-card {
  align-items: center;
}

.qr-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3, 12px);
}

.qr-image {
  width: 200px;
  height: 200px;
  border-radius: var(--radius-md, 12px);
  border: 1px solid var(--color-gray-blue, #d4dbe6);
  padding: var(--space-2, 8px);
  background: #ffffff;
}

.qr-caption {
  font-size: 0.8125rem;
  color: var(--color-pale-black, #8c8c8c);
  margin: 0;
}
</style>
