<template>
  <div class="registry-uploader">
    <!-- Зона выбора файла -->
    <div class="file-select-zone">
      <input
        type="file"
        ref="fileInput"
        class="file-input"
        accept=".csv,.xlsx,.xls"
        @change="onFileSelected"
      />
      <button class="file-select-btn" @click="$refs.fileInput.click()">
        <svg class="file-select-btn__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" stroke-width="2"/>
          <polyline points="17 8 12 3 7 8" stroke-width="2"/>
          <line x1="12" y1="3" x2="12" y2="15" stroke-width="2"/>
        </svg>
        {{ selectedFile ? selectedFile.name : 'Выбрать файл (CSV, XLSX, XLS)' }}
      </button>
      <p class="file-hint">Поддерживаемые форматы: CSV, XLSX, XLS</p>
    </div>

    <!-- Состояние загрузки -->
    <div v-if="isLoading" class="loading-state">
      <div class="spinner"></div>
      <span>{{ loadingText }}</span>
    </div>

    <!-- Предпросмотр данных -->
    <div v-if="previewData" class="preview-section">
      <h4 class="preview-title">
        Предпросмотр импорта
        <span class="preview-badge">{{ previewData.totalRows }} записей</span>
      </h4>

      <!-- Статистика -->
      <div class="preview-stats">
        <div class="stat-card stat-card--success">
          <span class="stat-card__icon">✓</span>
          <div class="stat-card__content">
            <span class="stat-card__value">{{ previewData.validRows }}</span>
            <span class="stat-card__label">Валидных</span>
          </div>
        </div>
        <div v-if="previewData.invalidRows > 0" class="stat-card stat-card--error">
          <span class="stat-card__icon">✕</span>
          <div class="stat-card__content">
            <span class="stat-card__value">{{ previewData.invalidRows }}</span>
            <span class="stat-card__label">Ошибок</span>
          </div>
        </div>
      </div>

      <!-- Таблица валидных записей -->
      <div v-if="previewData.validItems.length" class="preview-table-wrapper">
        <h5 class="preview-table-title">Валидные записи (первые {{ limitedPreview.length }})</h5>
        <table class="preview-table">
          <thead>
            <tr>
              <th>№ строки</th>
              <th>Код вуза</th>
              <th>Номер диплома</th>
              <th>ФИО</th>
              <th>Специальность</th>
              <th>Год выпуска</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in limitedPreview" :key="item.rowNumber">
              <td>{{ item.rowNumber }}</td>
              <td>{{ item.universityCode }}</td>
              <td>{{ item.diplomaNumber }}</td>
              <td>{{ item.fullName }}</td>
              <td>{{ item.specialty }}</td>
              <td>{{ item.graduationYear }}</td>
            </tr>
          </tbody>
        </table>
        <p v-if="previewData.validItems.length > MAX_PREVIEW" class="preview-note">
          Показано {{ MAX_PREVIEW }} из {{ previewData.validItems.length }}
        </p>
      </div>

      <!-- Ошибки валидации -->
      <div v-if="previewData.errors.length" class="errors-list">
        <h5 class="errors-title">Ошибки валидации ({{ previewData.errors.length }})</h5>
        <ul class="errors-ul">
          <li v-for="err in previewData.errors" :key="err.rowNumber" class="error-item">
            <span class="error-row">Строка {{ err.rowNumber }}</span>
            <span class="error-msg">{{ err.message }}</span>
          </li>
        </ul>
      </div>
    </div>

    <!-- Кнопки действий -->
    <div v-if="previewData && previewData.validRows > 0" class="actions">
      <button
        class="btn btn-primary"
        :disabled="isCommitting"
        @click="commitImport"
      >
        {{ isCommitting ? 'Импорт...' : 'Подтвердить импорт' }}
      </button>
    </div>

    <!-- Результат импорта -->
    <div v-if="importResult" class="result-section">
      <h4 class="result-title">Результат импорта</h4>
      <div class="result-stats">
        <div class="stat-card stat-card--success">
          <span class="stat-card__icon">✓</span>
          <div class="stat-card__content">
            <span class="stat-card__value">{{ importResult.importedRows }}</span>
            <span class="stat-card__label">Импортировано</span>
          </div>
        </div>
        <div v-if="importResult.failedRows > 0" class="stat-card stat-card--error">
          <span class="stat-card__icon">✕</span>
          <div class="stat-card__content">
            <span class="stat-card__value">{{ importResult.failedRows }}</span>
            <span class="stat-card__label">Ошибок</span>
          </div>
        </div>
      </div>

      <!-- Ошибки при импорте -->
      <div v-if="importResult.errors.length" class="errors-list">
        <ul class="errors-ul">
          <li v-for="err in importResult.errors" :key="err.rowNumber" class="error-item">
            <span class="error-row">Строка {{ err.rowNumber }}</span>
            <span class="error-msg">{{ err.message }}</span>
          </li>
        </ul>
      </div>

      <button class="btn btn-secondary" @click="resetUploader">Загрузить другой файл</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useAuth } from '../../composables/useAuth.js'
import { appendCertificates } from '../../utils/certificatesStore.js'
import * as XLSX from 'xlsx'

/**
 * RegistryUploader — загрузка CSV/Excel реестра дипломов с предпросмотром и импортом
 */
const emit = defineEmits(['import-complete', 'error'])

const { token } = useAuth()

const MAX_PREVIEW = 10

const fileInput = ref(null)
const selectedFile = ref(null)
const isLoading = ref(false)
const loadingText = ref('')
const previewData = ref(null)
const isCommitting = ref(false)
const importResult = ref(null)

const limitedPreview = computed(() => 
  previewData.value?.validItems?.slice(0, MAX_PREVIEW) || []
)

function onFileSelected(e) {
  const file = e.target.files?.[0]
  if (!file) return

  // Проверка расширения
  const ext = file.name.split('.').pop().toLowerCase()
  const allowedExts = ['csv', 'xlsx', 'xls']
  if (!allowedExts.includes(ext)) {
    emit('error', 'Поддерживаемые форматы: CSV, XLSX, XLS')
    return
  }

  selectedFile.value = file
  previewData.value = null
  importResult.value = null

  // Отправляем файл на предпросмотр
  uploadPreview(file)
}

async function uploadPreview(file) {
  isLoading.value = true
  loadingText.value = 'Анализ файла...'

  try {
    const formData = new FormData()
    formData.append('file', file)

    const response = await fetch('/api/university/certificates/import/preview', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token.value}`,
      },
      body: formData,
    })

    if (!response.ok) {
      const errorData = await response.json().catch(() => null)
      throw new Error(errorData?.detail || errorData?.message || `Ошибка сервера: ${response.status}`)
    }

    const data = await response.json()
    
    // Структура ответа:
    // { totalRows, validRows, invalidRows, validItems: [...], errors: [...] }
    previewData.value = {
      totalRows: data.totalRows || 0,
      validRows: data.validRows || 0,
      invalidRows: data.invalidRows || 0,
      validItems: data.validItems || [],
      errors: data.errors || [],
    }

    if (data.validRows === 0 && data.errors.length === 0) {
      emit('error', 'Файл не содержит валидных записей')
    }
  } catch (error) {
    emit('error', error.message || 'Ошибка загрузки файла')
    previewData.value = null
  } finally {
    isLoading.value = false
  }
}

/**
 * Преобразует данные с сервера в формат локального хранилища
 */
function toLocalRecord(item) {
  return {
    id: item.certificateId || crypto.randomUUID?.() || `cert-${Date.now()}-${item.rowNumber}`,
    serialNumber: item.diplomaNumber,
    studentId: item.studentId || null,
    studentName: item.fullName,
    specialty: item.specialty,
    issueDate: new Date().toISOString().split('T')[0],
    status: 'active',
    hash: null,
    signature: null,
    universityCode: item.universityCode,
    graduationYear: item.graduationYear,
  }
}

async function commitImport() {
  if (!selectedFile.value || !previewData.value) return

  isCommitting.value = true
  loadingText.value = 'Импорт дипломов...'

  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)

    const response = await fetch('/api/university/certificates/import/commit', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token.value}`,
      },
      body: formData,
    })

    if (!response.ok) {
      const errorData = await response.json().catch(() => null)
      throw new Error(errorData?.detail || errorData?.message || `Ошибка сервера: ${response.status}`)
    }

    const data = await response.json()
    
    // Структура ответа:
    // { totalRows, importedRows, failedRows, importedDiplomaNumbers: [...], errors: [...] }
    importResult.value = {
      totalRows: data.totalRows || 0,
      importedRows: data.importedRows || 0,
      failedRows: data.failedRows || 0,
      importedDiplomaNumbers: data.importedDiplomaNumbers || [],
      errors: data.errors || [],
    }

    // Сохраняем импортированные дипломы в локальное хранилище
    if (data.importedDiplomaNumbers?.length) {
      const importedItems = previewData.value.validItems.filter(
        item => data.importedDiplomaNumbers.includes(item.diplomaNumber)
      )
      const localRecords = importedItems.map(toLocalRecord)
      appendCertificates(localRecords)
    }

    emit('import-complete', importResult.value)
  } catch (error) {
    emit('error', error.message || 'Ошибка импорта')
  } finally {
    isCommitting.value = false
    isLoading.value = false
  }
}

function resetUploader() {
  selectedFile.value = null
  previewData.value = null
  importResult.value = null
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}
</script>

<style scoped>
.registry-uploader {
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
}

/* ===== Зона выбора файла ===== */
.file-select-zone {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-8) var(--space-6);
  background: linear-gradient(135deg, #f8fafc 0%, #e8ecf4 100%);
  border-radius: var(--radius-lg);
}

.file-input {
  display: none;
}

.file-select-btn {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-4) var(--space-8);
  background: #fff;
  border: 2px solid var(--color-main-blue);
  border-radius: var(--radius-md);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-main-blue);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-family: var(--font-family);
}

.file-select-btn:hover {
  background: var(--color-main-blue);
  color: #fff;
}

.file-select-btn__icon {
  width: 24px;
  height: 24px;
}

.file-hint {
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
  margin: 0;
}

/* ===== Состояние загрузки ===== */
.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-3);
  padding: var(--space-8);
  background: #fff;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-gray-blue);
  font-size: var(--font-size-base);
  color: var(--color-pale-black);
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid var(--color-pale-blue);
  border-top-color: var(--color-main-blue);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ===== Предпросмотр ===== */
.preview-section,
.result-section {
  padding: var(--space-6);
  background: #fff;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-gray-blue);
}

.preview-title,
.result-title {
  margin: 0 0 var(--space-5);
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  display: flex;
  align-items: center;
  gap: var(--space-3);
  flex-wrap: wrap;
}

.preview-badge {
  display: inline-block;
  padding: var(--space-1) var(--space-3);
  background: var(--color-pale-blue);
  color: var(--color-main-blue);
  border-radius: var(--radius-sm);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
}

/* ===== Статистика ===== */
.preview-stats,
.result-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: var(--space-4);
  margin-bottom: var(--space-6);
}

.stat-card {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-4);
  border-radius: var(--radius-md);
  border: 1px solid transparent;
}

.stat-card--success {
  background: #e6f5ee;
  border-color: #b7e4c7;
}

.stat-card--error {
  background: #fde8e7;
  border-color: #f5b7b1;
}

.stat-card__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: var(--radius-sm);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  flex-shrink: 0;
}

.stat-card--success .stat-card__icon {
  background: #fff;
  color: #27ae60;
}

.stat-card--error .stat-card__icon {
  background: #fff;
  color: #e74c3c;
}

.stat-card__content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-card__value {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  line-height: 1;
}

.stat-card--success .stat-card__value {
  color: #27ae60;
}

.stat-card--error .stat-card__value {
  color: #e74c3c;
}

.stat-card__label {
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
}

/* ===== Таблица ===== */
.preview-table-wrapper {
  margin-top: var(--space-6);
}

.preview-table-title {
  margin: 0 0 var(--space-3);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-black);
}

.preview-table {
  overflow-x: auto;
  border-radius: var(--radius-md);
  border: 1px solid var(--color-gray-blue);
}

.preview-table table {
  width: 100%;
  border-collapse: collapse;
  font-size: var(--font-size-sm);
}

.preview-table th,
.preview-table td {
  padding: var(--space-3) var(--space-4);
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
  white-space: nowrap;
}

.preview-table tbody tr:last-child td {
  border-bottom: none;
}

.preview-table tbody tr:hover {
  background: #f8fafc;
}

.preview-note {
  font-size: var(--font-size-xs);
  color: var(--color-pale-black);
  margin: var(--space-2) 0 0;
  padding: var(--space-2);
}

/* ===== Ошибки ===== */
.errors-list {
  margin-top: var(--space-6);
}

.errors-title {
  margin: 0 0 var(--space-3);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-red);
}

.errors-ul {
  margin: 0;
  padding: 0;
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.error-item {
  display: flex;
  gap: var(--space-3);
  padding: var(--space-3);
  background: #fde8e7;
  border-radius: var(--radius-base);
  font-size: var(--font-size-sm);
  border-left: 3px solid var(--color-red);
}

.error-row {
  font-weight: var(--font-weight-semibold);
  color: var(--color-black);
  white-space: nowrap;
  flex-shrink: 0;
}

.error-msg {
  color: var(--color-red);
}

/* ===== Кнопки действий ===== */
.actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-3);
}

.btn {
  padding: var(--space-3) var(--space-6);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-family: var(--font-family);
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-primary {
  background: var(--color-main-blue);
  color: #fff;
}

.btn-primary:hover:not(:disabled) {
  background: #1a3660;
}

.btn-secondary {
  background: var(--color-pale-blue);
  color: var(--color-main-blue);
}

.btn-secondary:hover {
  background: #c8e6f5;
}

/* ===== АДАПТИВ ===== */
@media (max-width: 768px) {
  .file-select-zone {
    padding: var(--space-5) var(--space-4);
  }

  .file-select-btn {
    padding: var(--space-3) var(--space-5);
    font-size: var(--font-size-sm);
  }

  .preview-section,
  .result-section {
    padding: var(--space-4);
  }

  .preview-title,
  .result-title {
    font-size: var(--font-size-lg);
  }

  .preview-stats,
  .result-stats {
    grid-template-columns: 1fr;
    gap: var(--space-3);
  }

  .stat-card {
    padding: var(--space-3);
  }

  .stat-card__icon {
    width: 32px;
    height: 32px;
    font-size: var(--font-size-base);
  }

  .stat-card__value {
    font-size: var(--font-size-xl);
  }

  .preview-table th,
  .preview-table td {
    padding: var(--space-2) var(--space-3);
    font-size: var(--font-size-xs);
  }

  .preview-table th {
    font-size: 10px;
  }

  .error-item {
    flex-direction: column;
    gap: var(--space-1);
  }

  .actions {
    justify-content: center;
  }

  .btn {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .file-select-zone {
    padding: var(--space-4) var(--space-3);
  }

  .file-select-btn {
    padding: var(--space-3);
    font-size: var(--font-size-xs);
    width: 100%;
    justify-content: center;
  }

  .preview-section,
  .result-section {
    padding: var(--space-3);
  }

  .preview-title,
  .result-title {
    font-size: var(--font-size-base);
  }

  .preview-table-wrapper {
    margin-left: calc(var(--space-3) * -1);
    margin-right: calc(var(--space-3) * -1);
  }

  .preview-table {
    border-radius: 0;
    border-left: none;
    border-right: none;
  }

  .preview-table th,
  .preview-table td {
    padding: var(--space-2);
    font-size: 11px;
  }
}
</style>
