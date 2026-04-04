<template>
  <div class="audit-report">
    <div class="report-header">
      <h3>История проверок</h3>
      <div class="report-controls">
        <input type="date" v-model="dateFrom" />
        <input type="date" v-model="dateTo" />
        <button class="btn" @click="exportReport">Экспорт</button>
      </div>
    </div>

    <DataTable
      :columns="columns"
      :items="filteredReports"
      :pagination="pagination"
      @page="onPage"
    >
      <template #cell-status="{ value }">
        <StatusBadge :status="value" />
      </template>
      <template #cell-date="{ value }">
        {{ formatDateTime(value) }}
      </template>
    </DataTable>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import DataTable from '../common/DataTable.vue'
import StatusBadge from '../common/StatusBadge.vue'

/**
 * AuditReport — отчёт по истории проверок HR
 */
const dateFrom = ref('')
const dateTo = ref('')

const reports = ref([
  { id: 1, date: '2026-03-28T14:30:00Z', serial: '1234567890', status: 'active', checker: 'ООО «ТехноКадры»' },
  { id: 2, date: '2026-03-25T10:00:00Z', serial: '0987654321', status: 'revoked', checker: 'АО «КадрыПро»' },
  { id: 3, date: '2026-03-20T16:45:00Z', serial: '1122334455', status: 'active', checker: 'ИП Иванов' }
])

const columns = [
  { key: 'date', label: 'Дата проверки' },
  { key: 'serial', label: 'Серийный номер' },
  { key: 'checker', label: 'Проверяющий' },
  { key: 'status', label: 'Статус' }
]

const pagination = ref({ page: 1, pageSize: 10, total: 3 })

const filteredReports = computed(() => {
  let items = reports.value
  if (dateFrom.value) {
    const from = new Date(dateFrom.value)
    items = items.filter(r => new Date(r.date) >= from)
  }
  if (dateTo.value) {
    const to = new Date(dateTo.value)
    to.setHours(23, 59, 59)
    items = items.filter(r => new Date(r.date) <= to)
  }
  return items
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

function onPage(page) {
  pagination.value.page = page
}

function exportReport() {
  const header = 'Дата,Серийный номер,Проверяющий,Статус\n'
  const rows = filteredReports.value.map(r =>
    `${formatDateTime(r.date)},${r.serial},${r.checker},${r.status}`
  ).join('\n')

  const blob = new Blob([header + rows], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `audit_report_${Date.now()}.csv`
  a.click()
  URL.revokeObjectURL(url)
}
</script>

<style scoped>
.audit-report {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.report-header {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.report-header h3 {
  margin: 0;
  font-size: 1.25rem;
}

.report-controls {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

.report-controls input[type="date"] {
  padding: 0.375rem 0.75rem;
  border: 1px solid var(--color-border, #cbd5e1);
  border-radius: 6px;
  font-size: 0.875rem;
}

.btn {
  padding: 0.5rem 1rem;
  border: 1px solid var(--color-border, #cbd5e1);
  background: #fff;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.875rem;
}

.btn:hover { background: #f1f5f9; }
</style>
