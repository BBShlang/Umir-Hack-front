<template>
  <form @submit.prevent="onSubmit" class="vf">
    <div class="vf-row">
      <div class="vf-field">
        <label class="vf-label">Серийный номер диплома</label>
        <input
          v-model="serialNumber"
          type="text"
          class="vf-input"
          placeholder="Например: 12345678901234"
          autocomplete="off"
          spellcheck="false"
        />
      </div>
      <button type="submit" class="vf-btn" :disabled="loading || !serialNumber.trim()">
        <svg v-if="loading" class="vf-spin" width="18" height="18" viewBox="0 0 24 24" fill="none">
          <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2" stroke-dasharray="31.4 31.4" stroke-linecap="round">
            <animateTransform attributeName="transform" type="rotate" from="0 12 12" to="360 12 12" dur="0.8s" repeatCount="indefinite"/>
          </circle>
        </svg>
        <span>{{ loading ? 'Проверка…' : 'Проверить' }}</span>
      </button>
    </div>

    <Transition name="vf-fade">
      <div v-if="result" class="vf-res" :class="result.statusClass">
        <div class="vf-res-head">
          <span class="vf-res-icon" :class="{ 'vf-res-icon--ok': result.isValid }">
            <svg v-if="result.isValid" width="20" height="20" viewBox="0 0 20 20" fill="none">
              <circle cx="10" cy="10" r="9" stroke="currentColor" stroke-width="1.5"/>
              <path d="M6 10l3 3 5-6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <svg v-else width="20" height="20" viewBox="0 0 20 20" fill="none">
              <circle cx="10" cy="10" r="9" stroke="currentColor" stroke-width="1.5"/>
              <path d="M7 7l6 6M13 7l-6 6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            </svg>
          </span>
          <h4 class="vf-res-title">{{ result.title }}</h4>
        </div>
        <dl class="vf-res-grid">
          <span class="vf-res-k">ВУЗ</span>
          <span class="vf-res-v">{{ result.university || '—' }}</span>
          <span class="vf-res-k">Специальность</span>
          <span class="vf-res-v">{{ result.specialty || '—' }}</span>
          <span class="vf-res-k">Дата выдачи</span>
          <span class="vf-res-v">{{ result.issueDate || '—' }}</span>
          <span class="vf-res-k">Статус</span>
          <span class="vf-res-v"><StatusBadge :status="result.status" /></span>
        </dl>
      </div>
    </Transition>

    <Transition name="vf-fade">
      <div v-if="error" class="vf-err">
        <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
          <circle cx="8" cy="8" r="6.5" stroke="currentColor" stroke-width="1.3"/>
          <path d="M8 5v3.5M8 10.5v.5" stroke="currentColor" stroke-width="1.3" stroke-linecap="round"/>
        </svg>
        <span>{{ error }}</span>
      </div>
    </Transition>
  </form>
</template>

<script setup>
import { ref } from 'vue'
import StatusBadge from '../common/StatusBadge.vue'

const emit = defineEmits(['verify', 'error'])

const serialNumber = ref('')
const loading = ref(false)
const result = ref(null)
const error = ref('')

async function onSubmit() {
  if (!serialNumber.value.trim()) return

  loading.value = true
  error.value = ''
  result.value = null

  try {
    await new Promise(r => setTimeout(r, 800))

    result.value = {
      isValid: true,
      status: 'active',
      statusClass: 'vf-res--ok',
      title: 'Диплом действителен',
      university: 'МГТУ им. Баумана',
      specialty: 'Информатика и вычислительная техника',
      issueDate: '15.06.2025'
    }

    emit('verify', { serial: serialNumber.value, ...result.value })
  } catch (err) {
    error.value = 'Ошибка при проверке. Попробуйте позже.'
    emit('error', err)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.vf {
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
}

/* ---- Row ---- */
.vf-row {
  display: flex;
  gap: var(--space-3);
  align-items: flex-end;
}

.vf-field {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.vf-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-black);
  letter-spacing: 0.01em;
}

.vf-input {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-base);
  font-size: var(--font-size-base);
  font-family: var(--font-family);
  background: #fff;
  color: var(--color-black);
  transition: border-color 0.15s ease, box-shadow 0.15s ease;
}

.vf-input::placeholder {
  color: var(--color-pale-black);
  opacity: 0.6;
}

.vf-input:focus {
  outline: none;
  border-color: var(--color-main-blue);
  box-shadow: 0 0 0 3px rgba(38, 75, 130, 0.1);
}

/* ---- Button ---- */
.vf-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 24px;
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

.vf-btn:hover:not(:disabled) {
  background: #1a5bbd;
}

.vf-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.vf-spin {
  flex-shrink: 0;
}

/* ---- Result ---- */
.vf-res {
  border: 1px solid;
  border-radius: var(--radius-lg);
  padding: var(--space-5);
}

.vf-res--ok {
  background: #f0fdf4;
  border-color: #bbf7d0;
}

.vf-res--fail {
  background: #fef2f2;
  border-color: #fecaca;
}

.vf-res-head {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin-bottom: var(--space-4);
}

.vf-res-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  flex-shrink: 0;
  color: #94a3b8;
}

.vf-res-icon--ok {
  background: #dcfce7;
  color: #16a34a;
}

.vf-res-title {
  margin: 0;
  font-size: var(--font-size-base);
  font-weight: 700;
  color: var(--color-black);
}

.vf-res-grid {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 2px 16px;
  font-size: 13px;
}

.vf-res-k {
  color: var(--color-pale-black);
  font-weight: 500;
}

.vf-res-v {
  color: var(--color-black);
}

/* ---- Error ---- */
.vf-err {
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

.vf-err svg {
  flex-shrink: 0;
  color: #dc2626;
}

/* ---- Transition ---- */
.vf-fade-enter-active,
.vf-fade-leave-active {
  transition: opacity 0.25s ease;
}

.vf-fade-enter-from,
.vf-fade-leave-to {
  opacity: 0;
}

/* ---- Mobile ---- */
@media (max-width: 768px) {
  .vf-row {
    flex-direction: column;
    align-items: stretch;
  }

  .vf-btn {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 540px) {
  .vf-res-grid {
    grid-template-columns: 1fr;
    gap: 2px;
  }

  .vf-res-v {
    margin-bottom: var(--space-2);
  }
}
</style>
