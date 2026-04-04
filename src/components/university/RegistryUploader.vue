<template>
  <div class="registry-uploader">
    <FileUploader
      :accepted-formats="'.csv,.xlsx,.xls'"
      :hint="hint"
      @files-selected="onFilesSelected"
    />
    <div v-if="parsedPreview.length" class="preview-section">
      <h4>Предпросмотр ({{ parsedPreview.length }} записей)</h4>
      <div class="preview-table">
        <table>
          <thead>
            <tr>
              <th v-for="col in previewColumns" :key="col">{{ col }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, i) in limitedPreview" :key="i">
              <td v-for="col in previewColumns" :key="col">{{ row[col] }}</td>
            </tr>
          </tbody>
        </table>
        <p v-if="parsedPreview.length > MAX_PREVIEW" class="preview-note">
          Показано {{ MAX_PREVIEW }} из {{ parsedPreview.length }}
        </p>
      </div>
    </div>
    <div v-if="uploadErrors.length" class="errors-section">
      <h4>Ошибки валидации ({{ uploadErrors.length }})</h4>
      <ul>
        <li v-for="(err, i) in uploadErrors" :key="i">{{ err }}</li>
      </ul>
    </div>
    <div class="actions">
      <button class="btn btn-primary" :disabled="!canSubmit" @click="submit">
        Загрузить в реестр
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import FileUploader from '../common/FileUploader.vue'

/**
 * RegistryUploader — загрузка и предпросмотр реестра дипломов
 */
const emit = defineEmits(['submit', 'error'])

const MAX_PREVIEW = 10
const parsedPreview = ref([])
const uploadErrors = ref([])
const previewColumns = ref([])

const limitedPreview = computed(() => parsedPreview.value.slice(0, MAX_PREVIEW))
const canSubmit = computed(() => parsedPreview.value.length > 0 && uploadErrors.value.length === 0)
const hint = computed(() =>
  parsedPreview.value.length
    ? `Загружено: ${parsedPreview.value.length} записей`
    : 'Поддерживаемые форматы: CSV, Excel. Колонки: ФИО, Серийный номер, Специальность, Дата выдачи'
)

function onFilesSelected(files) {
  uploadErrors.value = []
  parsedPreview.value = []
  previewColumns.value = []

  const file = files[0]
  if (!file) return

  const reader = new FileReader()
  const ext = file.name.split('.').pop().toLowerCase()

  if (ext === 'csv') {
    reader.onload = (e) => parseCSV(e.target.result)
    reader.readAsText(file, 'utf-8')
  } else {
    uploadErrors.value.push(`Формат .${ext} требует дополнительной обработки. Используйте CSV для быстрого предпросмотра.`)
    emit('error', 'Неподдерживаемый формат')
  }
}

function parseCSV(text) {
  const lines = text.trim().split(/\r?\n/)
  if (lines.length < 2) {
    uploadErrors.value.push('Файл пуст или содержит только заголовок')
    return
  }

  const delimiter = lines[0].includes(';') ? ';' : ','
  const headers = lines[0].split(delimiter).map(h => h.trim().replace(/^"|"$/g, ''))
  previewColumns.value = headers

  const records = []
  for (let i = 1; i < lines.length; i++) {
    const values = lines[i].split(delimiter).map(v => v.trim().replace(/^"|"$/g, ''))
    if (values.length !== headers.length) {
      uploadErrors.value.push(`Строка ${i + 1}: ожидается ${headers.length} колонок, найдено ${values.length}`)
      continue
    }
    const record = {}
    headers.forEach((h, idx) => { record[h] = values[idx] })
    records.push(record)
  }

  parsedPreview.value = records
}

function submit() {
  emit('submit', parsedPreview.value)
}
</script>

<style scoped>
.registry-uploader {
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
}

.preview-section,
.errors-section {
  padding: var(--space-5);
  background: #fff;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-gray-blue);
}

.preview-section h4,
.errors-section h4 {
  margin: 0 0 var(--space-4);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-bold);
}

.preview-table {
  overflow-x: auto;
}

.preview-table table {
  width: 100%;
  border-collapse: collapse;
  font-size: var(--font-size-sm);
}

.preview-table th,
.preview-table td {
  padding: var(--space-2) var(--space-3);
  text-align: left;
  border-bottom: 1px solid var(--color-gray-blue);
}

.preview-table th {
  background: var(--color-pale-blue);
  font-weight: var(--font-weight-semibold);
  font-size: var(--font-size-xs);
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: var(--color-pale-black);
}

.preview-note {
  font-size: var(--font-size-xs);
  color: var(--color-pale-black);
  margin-top: var(--space-2);
}

.errors-section ul {
  margin: 0;
  padding-left: var(--space-5);
  color: var(--color-red);
  font-size: var(--font-size-sm);
}

.actions {
  display: flex;
  justify-content: flex-end;
}

.btn {
  padding: var(--space-3) var(--space-6);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.btn-primary {
  background: var(--color-main-blue);
  color: #fff;
}

.btn-primary:hover:not(:disabled) {
  background: #1a3660;
}

.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
