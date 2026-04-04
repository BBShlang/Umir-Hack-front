<template>
  <div class="verification-tracker">
    <div class="tracker-header">
      <h4 class="tracker-title">История проверок</h4>
      <p class="tracker-description">
        Список всех проверок вашего диплома работодателями
      </p>
    </div>

    <div v-if="loading" class="loading">Загрузка...</div>

    <div v-else-if="!verifications.length" class="empty">
      Проверок пока не было
    </div>

    <div v-else class="tracker-card">
      <table class="tracker-table">
        <thead>
          <tr>
            <th>Дата</th>
            <th>Кем проверен</th>
            <th>Статус</th>
            <th>IP</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="v in verifications" :key="v.id">
            <td>{{ formatDateTime(v.createdAt) }}</td>
            <td>{{ v.checkerName || 'Анонимно' }}</td>
            <td>
              <span class="status-dot" :class="v.statusClass"></span>
              {{ v.statusLabel }}
            </td>
            <td class="ip-cell"><code>{{ v.ip }}</code></td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="verifications.length" class="summary">
      <span>Всего проверок: <strong>{{ verifications.length }}</strong></span>
      <span>Последняя: <strong>{{ lastCheckDate }}</strong></span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

/**
 * VerificationTracker — трекер проверок диплома работодателями
 * @prop {String} diplomaId
 */
const props = defineProps({
  diplomaId: { type: String, default: '' }
})

const loading = ref(true)
const verifications = ref([])

const lastCheckDate = computed(() => {
  if (!verifications.value.length) return '—'
  return formatDateTime(verifications.value[0].createdAt)
})

onMounted(() => {
  // Эмуляция загрузки
  setTimeout(() => {
    verifications.value = [
      {
        id: 1,
        createdAt: '2026-03-28T14:30:00Z',
        checkerName: 'ООО «ТехноКадры»',
        statusClass: 'status-valid',
        statusLabel: 'Подтверждён',
        ip: '192.168.1.100'
      },
      {
        id: 2,
        createdAt: '2026-03-15T09:15:00Z',
        checkerName: null,
        statusClass: 'status-valid',
        statusLabel: 'Подтверждён',
        ip: '10.0.0.55'
      }
    ]
    loading.value = false
  }, 600)
})

function formatDateTime(dateStr) {
  return new Date(dateStr).toLocaleString('ru-RU', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
.verification-tracker {
  display: flex;
  flex-direction: column;
  gap: var(--space-4, 16px);
}

/* ── Header ── */
.tracker-header {
  display: flex;
  flex-direction: column;
  gap: var(--space-1, 4px);
}

.tracker-title {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--color-black, #1a1a1a);
}

.tracker-description {
  margin: 0;
  font-size: 0.875rem;
  color: var(--color-pale-black, #8c8c8c);
}

/* ── Loading / Empty ── */
.loading,
.empty {
  padding: var(--space-6, 24px);
  text-align: center;
  color: var(--color-pale-black, #8c8c8c);
  background: #ffffff;
  border-radius: var(--radius-lg, 16px);
  border: 1px solid var(--color-gray-blue, #d4dbe6);
  font-size: 0.9375rem;
}

/* ── Table Card ── */
.tracker-card {
  background: #ffffff;
  border-radius: var(--radius-lg, 16px);
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0, 0, 0, 0.08));
  border: 1px solid var(--color-gray-blue, #d4dbe6);
  overflow: hidden;
  transition: box-shadow 0.2s ease;
}

.tracker-card:hover {
  box-shadow: var(--shadow-base, 0 2px 8px rgba(0, 0, 0, 0.1));
}

/* ── Table ── */
.tracker-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.875rem;
}

.tracker-table thead tr {
  background: var(--color-gray-blue, #d4dbe6);
}

.tracker-table th {
  padding: var(--space-3, 12px) var(--space-4, 16px);
  text-align: left;
  font-weight: 600;
  color: var(--color-pale-black, #8c8c8c);
  font-size: 0.8125rem;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.tracker-table td {
  padding: var(--space-3, 12px) var(--space-4, 16px);
  border-top: 1px solid var(--color-gray-blue, #d4dbe6);
  color: var(--color-black, #1a1a1a);
}

.tracker-table tbody tr {
  transition: background 0.15s ease;
}

.tracker-table tbody tr:hover {
  background: rgba(72, 184, 194, 0.06);
}

/* ── Status Dot ── */
.status-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: var(--space-2, 8px);
  vertical-align: middle;
  position: relative;
  top: -1px;
}

.status-dot::after {
  content: '';
  position: absolute;
  inset: -2px;
  border-radius: 50%;
  opacity: 0.25;
}

.status-valid {
  background: var(--color-green, #0d9b68);
}

.status-valid::after {
  background: var(--color-green, #0d9b68);
}

.status-invalid {
  background: var(--color-red, #db2b21);
}

.status-invalid::after {
  background: var(--color-red, #db2b21);
}

/* ── IP Cell ── */
.ip-cell code {
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 0.8125rem;
  background: var(--color-gray-blue, #d4dbe6);
  padding: var(--space-1, 4px) var(--space-2, 8px);
  border-radius: var(--radius-sm, 4px);
  color: var(--color-pale-black, #8c8c8c);
}

/* ── Summary ── */
.summary {
  display: flex;
  justify-content: space-between;
  font-size: 0.875rem;
  color: var(--color-pale-black, #8c8c8c);
  padding-top: var(--space-2, 8px);
}

.summary strong {
  color: var(--color-black, #1a1a1a);
}
</style>
