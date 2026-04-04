<template>
  <div class="bulk-checker">
    <h3>Массовая проверка</h3>
    <p class="description">
      Загрузите список серийных номеров для пакетной проверки
    </p>

    <div class="input-section">
      <textarea
        v-model="serialNumbers"
        class="serial-input"
        placeholder="Введите серийные номера (каждый с новой строки или через запятую)"
        rows="8"
      ></textarea>
      <div class="input-actions">
        <span class="count-info">Найдено: {{ parsedCount }} номеров</span>
        <button class="btn btn-primary" :disabled="!canCheck || loading" @click="checkAll">
          {{ loading ? 'Проверяю...' : 'Проверить все' }}
        </button>
      </div>
    </div>

    <div v-if="results.length" class="results-section">
      <div class="results-header">
        <h4>Результаты</h4>
        <div class="results-summary">
          <span class="summary-valid">✅ Подтверждено: {{ validCount }}</span>
          <span class="summary-invalid">❌ Не найдено: {{ invalidCount }}</span>
        </div>
        <button class="btn btn-sm" @click="exportResults">Экспорт CSV</button>
      </div>

      <DataTable
        :columns="resultColumns"
        :items="results"
        :loading="loading"
      >
        <template #cell-status="{ value }">
          <StatusBadge :status="value" />
        </template>
        <template #cell-actions="{ item }">
          <button class="btn-link" @click="emit('view-detail', item)">Подробнее</button>
        </template>
      </DataTable>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import DataTable from '../common/DataTable.vue'
import StatusBadge from '../common/StatusBadge.vue'

/**
 * BulkChecker — массовая проверка дипломов
 */
const emit = defineEmits(['view-detail', 'error'])

const serialNumbers = ref('')
const loading = ref(false)
const results = ref([])

const parsedList = computed(() => {
  return serialNumbers.value
    .split(/[,;\n\r]+/)
    .map(s => s.trim())
    .filter(s => s.length > 0)
})

const parsedCount = computed(() => parsedList.value.length)
const canCheck = computed(() => parsedCount.value > 0)

const validCount = computed(() => results.value.filter(r => r.status === 'active').length)
const invalidCount = computed(() => results.value.filter(r => r.status === 'error').length)

const resultColumns = [
  { key: 'serial', label: 'Серийный номер' },
  { key: 'university', label: 'ВУЗ' },
  { key: 'specialty', label: 'Специальность' },
  { key: 'status', label: 'Статус' },
  { key: 'actions', label: 'Действия' }
]

async function checkAll() {
  loading.value = true
  results.value = []

  // Эмуляция пакетной проверки
  await new Promise(r => setTimeout(r, 1500))

  results.value = parsedList.value.map((serial, idx) => {
    const isValid = idx % 5 !== 0 // Эмуляция: каждый 5-й невалиден
    return {
      id: idx,
      serial,
      university: isValid ? 'МГТУ им. Баумана' : null,
      specialty: isValid ? 'Информатика' : null,
      status: isValid ? 'active' : 'error',
      issueDate: isValid ? '15.06.2025' : null
    }
  })

  loading.value = false
}

function exportResults() {
  const header = 'Серийный номер,ВУЗ,Специальность,Статус,Дата выдачи\n'
  const rows = results.value.map(r =>
    `${r.serial},${r.university || 'Не найден'},${r.specialty || '—'},${r.status},${r.issueDate || '—'}`
  ).join('\n')

  const blob = new Blob([header + rows], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'verification_results.csv'
  a.click()
  URL.revokeObjectURL(url)
}
</script>

<style scoped>
.bulk-checker {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.bulk-checker h3 {
  margin: 0;
  font-size: 1.25rem;
}

.description {
  margin: 0;
  font-size: 0.875rem;
  color: var(--color-text-muted, #64748b);
}

.serial-input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid var(--color-border, #cbd5e1);
  border-radius: 8px;
  font-family: monospace;
  font-size: 0.875rem;
  resize: vertical;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 0.75rem;
}

.count-info {
  font-size: 0.875rem;
  color: var(--color-text-muted, #64748b);
}

.btn {
  padding: 0.625rem 1.5rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.875rem;
  font-weight: 500;
}

.btn-primary {
  background: var(--color-primary, #3b82f6);
  color: #fff;
}

.btn-primary:disabled { opacity: 0.5; cursor: not-allowed; }

.btn-sm {
  padding: 0.375rem 0.75rem;
  font-size: 0.8125rem;
  border: 1px solid var(--color-border, #cbd5e1);
  background: #fff;
  border-radius: 6px;
  cursor: pointer;
}

.results-header {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 1rem;
}

.results-header h4 {
  margin: 0;
}

.results-summary {
  display: flex;
  gap: 1rem;
  font-size: 0.875rem;
}

.btn-link {
  background: none;
  border: none;
  color: var(--color-primary, #3b82f6);
  cursor: pointer;
  text-decoration: underline;
  font-size: 0.875rem;
}
</style>
