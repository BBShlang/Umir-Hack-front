<template>
  <form @submit.prevent="onSubmit" class="vf">
    <div class="vf-mode">
      <label class="vf-mode__item">
        <input v-model="mode" type="radio" value="serial" />
        <span>По номеру диплома</span>
      </label>
      <label class="vf-mode__item">
        <input v-model="mode" type="radio" value="crypto" />
        <span>Криптопроверка (API)</span>
      </label>
    </div>

    <template v-if="mode === 'serial'">
      <div class="vf-row">
        <div class="vf-field">
          <label class="vf-label">Номер диплома</label>
          <input
            v-model="serialNumber"
            type="text"
            class="vf-input"
            placeholder="Например: DIP-2026-001"
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
      <p class="vf-hint">
        Ищем среди дипломов, выпущенных в этом браузере (локальный кэш). Для произвольных данных используйте криптопроверку.
      </p>
    </template>

    <template v-else>
      <div class="vf-crypto">
        <div class="vf-field">
          <label class="vf-label">Код университета</label>
          <input v-model="universityCode" type="text" class="vf-input" placeholder="AITU-01" />
        </div>
        <div class="vf-field">
          <label class="vf-label">Payload</label>
          <input v-model="payload" type="text" class="vf-input" placeholder="Исходная строка payload" />
        </div>
        <div class="vf-field">
          <label class="vf-label">Подпись (base64)</label>
          <textarea v-model="signature" class="vf-input vf-input--area" rows="3" placeholder="Base64 подписи" />
        </div>
        <button
          type="submit"
          class="vf-btn vf-btn--block"
          :disabled="loading || !universityCode.trim() || !payload.trim() || !signature.trim()"
        >
          {{ loading ? 'Проверка…' : 'Проверить подпись' }}
        </button>
      </div>
    </template>

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
          <template v-if="result.rows?.length">
            <template v-for="(row, i) in result.rows" :key="i">
              <span class="vf-res-k">{{ row.k }}</span>
              <span class="vf-res-v">{{ row.v }}</span>
            </template>
          </template>
          <template v-else>
            <span class="vf-res-k">Ответ</span>
            <span class="vf-res-v"><pre class="vf-pre">{{ result.rawJson }}</pre></span>
          </template>
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
import { api } from '../../api/api.js'
import { findCertificateByNumber } from '../../utils/certificatesStore.js'
import { useAuth } from '../../composables/useAuth.js'

const emit = defineEmits(['verify', 'error'])

const { accessToken } = useAuth()

const mode = ref('serial')
const serialNumber = ref('')
const universityCode = ref('')
const payload = ref('')
const signature = ref('')
const loading = ref(false)
const result = ref(null)
const error = ref('')

function formatDate(iso) {
  if (!iso) return '—'
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return String(iso)
  return d.toLocaleDateString('ru-RU')
}

async function onSubmit() {
  loading.value = true
  error.value = ''
  result.value = null

  try {
    if (mode.value === 'serial') {
      const cert = findCertificateByNumber(serialNumber.value)
      if (!cert) {
        throw new Error(
          'Диплом не найден в локальном кэше. Выпустите его в ЛК ВУЗа на этом устройстве или используйте криптопроверку.'
        )
      }
      const ok = cert.status === 'active'
      result.value = {
        isValid: ok,
        status: cert.status,
        statusClass: ok ? 'vf-res--ok' : 'vf-res--fail',
        title: ok ? 'Диплом найден (локальный кэш)' : 'Диплом отозван в кэше',
        rows: [
          { k: 'ФИО', v: cert.studentName || '—' },
          { k: 'Специальность', v: cert.specialty || '—' },
          { k: 'Номер', v: cert.serialNumber || '—' },
          { k: 'Дата выпуска', v: formatDate(cert.issueDate) },
          { k: 'Хеш payload', v: cert.hash ? `${cert.hash.slice(0, 16)}…` : '—' },
        ],
      }
      emit('verify', { serial: serialNumber.value, certificate: cert })
    } else {
      const res = await api.cryptoVerify(
        {
          universityCode: universityCode.value.trim(),
          payload: payload.value.trim(),
          signature: signature.value.trim(),
        },
        accessToken.value
      )
      const rawJson = JSON.stringify(res, null, 2)
      result.value = {
        isValid: true,
        statusClass: 'vf-res--ok',
        title: 'Ответ API проверки подписи',
        rows: [],
        rawJson,
      }
      emit('verify', { crypto: true, response: res })
    }
  } catch (err) {
    error.value = err.message || 'Ошибка при проверке'
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

.vf-mode {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-4);
  font-size: 13px;
}

.vf-mode__item {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: var(--color-black);
}

.vf-hint {
  margin: 0;
  font-size: 12px;
  color: var(--color-pale-black);
  line-height: 1.45;
}

.vf-crypto {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.vf-input--area {
  resize: vertical;
  min-height: 72px;
  font-family: ui-monospace, monospace;
  font-size: 12px;
}

.vf-btn--block {
  width: 100%;
  justify-content: center;
}

.vf-pre {
  margin: 0;
  font-size: 11px;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 200px;
  overflow: auto;
}

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

.vf-fade-enter-active,
.vf-fade-leave-active {
  transition: opacity 0.25s ease;
}

.vf-fade-enter-from,
.vf-fade-leave-to {
  opacity: 0;
}

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
