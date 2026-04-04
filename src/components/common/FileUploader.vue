<template>
  <div class="file-uploader">
    <label
      class="upload-zone"
      :class="{ 'is-dragover': isDragOver, 'has-error': error }"
      @dragover.prevent="isDragOver = true"
      @dragleave="isDragOver = false"
      @drop.prevent="onDrop"
    >
      <input
        type="file"
        class="upload-input"
        :accept="acceptedFormats"
        :multiple="multiple"
        @change="onFileSelect"
      />
      <div class="upload-content">
        <slot name="icon">
          <svg class="upload-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" stroke-width="2"/>
            <polyline points="17 8 12 3 7 8" stroke-width="2"/>
            <line x1="12" y1="3" x2="12" y2="15" stroke-width="2"/>
          </svg>
        </slot>
        <p class="upload-text">
          <span class="upload-main">Перетащите файл сюда</span>
          <span class="upload-alt">или нажмите для выбора</span>
        </p>
        <p class="upload-hint" v-if="hint">{{ hint }}</p>
      </div>
    </label>

    <div v-if="error" class="upload-error">{{ error }}</div>

    <div v-if="progress > 0 && progress < 100" class="upload-progress">
      <div class="progress-bar" :style="{ width: progress + '%' }"></div>
      <span>{{ Math.round(progress) }}%</span>
    </div>

    <div v-if="uploadedFiles.length" class="uploaded-list">
      <div v-for="file in uploadedFiles" :key="file.name" class="uploaded-file">
        <span class="file-name">{{ file.name }}</span>
        <span class="file-size">{{ formatSize(file.size) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

/**
 * FileUploader — зона загрузки файлов с drag & drop
 * @prop {String} acceptedFormats - допустимые форматы (по умолч. '.csv,.xlsx,.xls')
 * @prop {Boolean} multiple - разрешить множественную загрузку
 * @prop {String} hint - подсказка под зоной загрузки
 * @prop {Function} onUpload - колбэк при выборе файлов
 */
const props = defineProps({
  acceptedFormats: { type: String, default: '.csv,.xlsx,.xls,.pdf,.doc,.docx' },
  multiple: { type: Boolean, default: true },
  hint: { type: String, default: '' },
  onUpload: { type: Function, default: null }
})

const emit = defineEmits(['files-selected', 'error'])

const isDragOver = ref(false)
const error = ref('')
const progress = ref(0)
const uploadedFiles = ref([])

function formatSize(bytes) {
  if (bytes < 1024) return bytes + ' Б'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' КБ'
  return (bytes / (1024 * 1024)).toFixed(1) + ' МБ'
}

function onDrop(e) {
  isDragOver.value = false
  const files = Array.from(e.dataTransfer.files)
  handleFiles(files)
}

function onFileSelect(e) {
  const files = Array.from(e.target.files)
  handleFiles(files)
}

function handleFiles(files) {
  error.value = ''
  if (!files.length) return

  // Валидация расширений
  const allowed = props.acceptedFormats.split(',').map(f => f.trim().toLowerCase())
  const invalid = files.filter(f => {
    const ext = '.' + f.name.split('.').pop().toLowerCase()
    return !allowed.includes(ext)
  })

  if (invalid.length) {
    error.value = `Недопустимый формат: ${invalid.map(f => f.name).join(', ')}`
    emit('error', error.value)
    return
  }

  uploadedFiles.value = files
  emit('files-selected', files)

  if (props.onUpload) {
    props.onUpload(files, (p) => { progress.value = p })
  }
}
</script>

<style scoped>
.upload-zone {
  border: 2px dashed var(--color-gray-blue);
  border-radius: var(--radius-lg);
  padding: var(--space-8);
  text-align: center;
  cursor: pointer;
  transition: all var(--transition-fast);
  background: #fff;
}

.upload-zone:hover,
.upload-zone.is-dragover {
  border-color: var(--color-main-blue);
  background: var(--color-pale-blue);
}

.upload-zone.has-error {
  border-color: var(--color-red);
}

.upload-input {
  display: none;
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-2);
}

.upload-icon {
  width: 48px;
  height: 48px;
  color: var(--color-pale-black);
}

.upload-text {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.upload-main {
  font-weight: var(--font-weight-semibold);
  color: var(--color-black);
}

.upload-alt {
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
}

.upload-hint {
  font-size: var(--font-size-xs);
  color: var(--color-pale-black);
}

.upload-error {
  margin-top: var(--space-3);
  padding: var(--space-3);
  background: #fde8e7;
  color: var(--color-red);
  border-radius: var(--radius-base);
  font-size: var(--font-size-sm);
}

.upload-progress {
  margin-top: var(--space-4);
  height: 6px;
  background: var(--color-pale-blue);
  border-radius: var(--radius-sm);
  position: relative;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, var(--color-main-blue), var(--color-sea-clear));
  border-radius: var(--radius-sm);
  transition: width var(--transition-base);
}

.uploaded-list {
  margin-top: var(--space-4);
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.uploaded-file {
  display: flex;
  justify-content: space-between;
  padding: var(--space-2) var(--space-3);
  background: var(--color-pale-blue);
  border-radius: var(--radius-base);
  font-size: var(--font-size-sm);
}

.file-size {
  color: var(--color-pale-black);
}
</style>
