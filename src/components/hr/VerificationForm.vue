<template>
  <form @submit.prevent="onSubmit" class="vf">
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
        <span>{{ loading ? 'Проверка…' : 'Проверить' }}</span>
      </button>
    </div>


    <div v-if="result" class="vf-res" :class="result.statusClass">
      <div class="vf-res-head">
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

    <div v-if="error" class="vf-err">{{ error }}</div>
  </form>
</template>

<script setup>
import { computed, ref } from 'vue'
import { api } from '../../api/api.js'
import { useAuth } from '../../composables/useAuth.js'

const emit = defineEmits(['verify', 'error'])

const { accessToken, role } = useAuth()
const isEmployer = computed(() => role.value === 'hr' && !!accessToken.value)

const serialNumber = ref('')
const loading = ref(false)
const result = ref(null)
const error = ref('')

function formatDateTime(iso) {
  if (!iso) return '—'
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return String(iso)
  return d.toLocaleString('ru-RU')
}

async function onSubmit() {
  loading.value = true
  error.value = ''
  result.value = null

  try {
    const num = serialNumber.value.trim()
    if (!num) throw new Error('Укажите номер диплома')

    const cert = isEmployer.value
      ? await api.employerCertificatesSearch({ diplomaNumber: num }, accessToken.value)
      : await api.publicCertificatesSearch({ diplomaNumber: num })

    const ok = String(cert?.status || '').toUpperCase() === 'ACTIVE'
    result.value = {
      isValid: ok,
      statusClass: ok ? 'vf-res--ok' : 'vf-res--fail',
      title: ok ? 'Диплом найден' : `Запись найдена (статус: ${cert?.status || '—'})`,
      rows: [
        { k: 'ФИО', v: cert?.fullName || cert?.maskedFullName || '—' },
        { k: 'ВУЗ', v: cert?.universityName || '—' },
        { k: 'Код ВУЗа', v: cert?.universityCode || '—' },
        { k: 'Специальность', v: cert?.specialty || '—' },
        { k: 'Номер', v: cert?.diplomaNumber || '—' },
        { k: 'Год выпуска', v: cert?.graduationYear != null ? String(cert.graduationYear) : '—' },
        { k: 'Статус', v: cert?.status || '—' },
        { k: 'Выдан', v: formatDateTime(cert?.issuedAt) },
        { k: 'Действует до', v: formatDateTime(cert?.expiresAt) },
      ],
    }
    emit('verify', { serial: num, certificate: cert })
  } catch (err) {
    error.value = err.message || 'Ошибка при проверке'
    emit('error', err)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.vf { display:flex; flex-direction:column; gap:20px; }
.vf-row { display:flex; gap:12px; align-items:flex-end; }
.vf-field { flex:1; display:flex; flex-direction:column; gap:6px; }
.vf-label { font-size:13px; font-weight:600; }
.vf-input {
  width:100%; min-height:46px; border:1px solid var(--color-gray-blue); border-radius:12px; padding:0 14px; font:inherit;
}
.vf-input--area { padding:12px 14px; min-height:90px; resize:vertical; }
.vf-btn {
  min-height:46px; border:0; border-radius:12px; background:#0d1f3c; color:#fff; padding:0 18px; font-weight:700; cursor:pointer;
}
.vf-btn:disabled { opacity:.65; cursor:not-allowed; }
.vf-btn--block { width:100%; }
.vf-hint { margin:0; font-size:12px; color:var(--color-pale-black); }
.vf-res { border-radius:16px; padding:16px; border:1px solid #dbe6f3; background:#f8fbff; }
.vf-res--fail { background:#fff7f7; border-color:#f2caca; }
.vf-res-head { margin-bottom:12px; }
.vf-res-title { margin:0; font-size:16px; }
.vf-res-grid { display:grid; grid-template-columns: 180px 1fr; gap:10px 14px; margin:0; }
.vf-res-k { color:var(--color-pale-black); }
.vf-res-v { font-weight:600; word-break:break-word; }
.vf-pre { margin:0; font-size:12px; white-space:pre-wrap; word-break:break-word; }
.vf-err { padding:12px 14px; border-radius:12px; background:#fff1f2; color:#b42318; border:1px solid #f3c6cc; }
@media (max-width: 720px) {
  .vf-row, .vf-res-grid { grid-template-columns: 1fr; display:grid; }
}
</style>
