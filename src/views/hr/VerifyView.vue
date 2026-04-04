<template>
  <div class="verify-view">
    <AppHeader />

    <main class="main-content">
      <!-- ===== HERO ===== -->
      <section class="verify__hero">
        <div class="verify__hero-bg" aria-hidden="true">
          <div class="verify__hero-grid-lines" />
          <div class="verify__hero-glow verify__hero-glow--1" />
          <div class="verify__hero-glow verify__hero-glow--2" />
        </div>

        <div class="container verify__hero-inner">
          <div class="verify__hero-content">
            <h1 class="verify__hero-title">
              Проверка диплома<br />
              <span class="verify__hero-title-accent">Мгновенная верификация</span>
            </h1>

            <p class="verify__hero-subtitle">
              Введите номер диплома или отсканируйте QR-код для мгновенной проверки подлинности документа в государственном реестре.
            </p>

            <div class="verify__hero-actions">
              <router-link to="/hr/bulk" class="verify__btn verify__btn--outline">Массовая проверка</router-link>
              <router-link to="/hr/reports" class="verify__btn verify__btn--outline">Отчёты</router-link>
            </div>

            <div class="verify__examples">
              <span class="verify__examples-label">Формат номера:</span>
              <div class="verify__examples-list">
                <span class="verify__example-btn">12345678901234</span>
                <span class="verify__example-btn">АБ-2024-001234</span>
              </div>
            </div>
          </div>
        </div>
      </section>


      <!-- ===== Основная рабочая зона ===== -->
      <section class="verify__workspace">
        <div class="container">
          <div class="verify__workspace-head">
            <p class="verify__workspace-eyebrow">Проверка подлинности</p>
            <h2 class="verify__workspace-title">Введите данные диплома</h2>
          </div>

          <div class="verify__workspace-grid">
            <!-- Левая колонка: форма проверки -->
            <div class="verify__workspace-panel">
              <div class="verify__panel-header">
                <div class="verify__panel-icon">
                  <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <circle cx="8" cy="8" r="5.5" stroke="currentColor" stroke-width="1.5"/>
                    <path d="M12 12l5 5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                  </svg>
                </div>
                <div class="verify__panel-title-wrap">
                  <h3 class="verify__panel-title">Верификация</h3>
                  <p class="verify__panel-desc">Поиск по серийному номеру или хэшу</p>
                </div>
              </div>

              <div class="verify__checker-card">
                <VerificationForm @verify="onVerify" @error="onError" />
              </div>
            </div>

            <!-- Правая колонка: QR + инструкции -->
            <div class="verify__workspace-sidebar">
              <!-- QR-карточка -->
              <div class="verify__sidebar-card">
                <h4 class="verify__sidebar-title">QR-сканер</h4>
                <div class="verify__qr-scanner">
                <!-- Режим выбора: камера или файл -->
                <div v-if="!scanMode" class="verify__qr-mode-selector">
                  <button class="verify__qr-mode-btn" @click="startCameraScan">
                    <Camera size="20" />
                    <span>Камера</span>
                  </button>
                  <button class="verify__qr-mode-btn" @click="fileInputRef?.click()">
                    <Upload size="20" />
                    <span>Загрузить</span>
                  </button>
                  <input
                    ref="fileInputRef"
                    type="file"
                    accept="image/*"
                    class="verify__file-input"
                    @change="handleFileUpload"
                  />
                </div>

                <!-- Камера -->
                <div v-else-if="scanMode === 'camera'" class="verify__qr-camera">
                  <div id="qr-reader" ref="qrReaderContainerRef" class="verify__qr-reader" />
                  <button class="verify__qr-stop-btn" @click="stopCameraScan">
                    <X size="16" />
                    Остановить
                  </button>
                </div>

                <!-- Предпросмотр файла -->
                <div v-else-if="scanMode === 'file' && previewImage" class="verify__qr-preview">
                  <img :src="previewImage" alt="QR Preview" class="verify__qr-img" />
                  <span class="verify__qr-preview-text">Обработка...</span>
                </div>

                <!-- Заглушка -->
                <div v-else class="verify__qr-placeholder">
                  <Camera size="48" :stroke-width="1.5" />
                  <span>Наведите камеру на QR-код</span>
                </div>
              </div>

              <!-- Результат сканирования -->
              <div v-if="scanResult" class="verify__scan-result" :class="{ 'verify__scan-result--error': scanResult.error }">
                <div v-if="!scanResult.error" class="verify__scan-success">
                  <CheckCircle size="20" class="verify__scan-icon" />
                  <div class="verify__scan-content">
                    <p class="verify__scan-title">Диплом найден</p>
                    <p class="verify__scan-detail">{{ scanResult.studentName }}</p>
                    <p class="verify__scan-detail">{{ scanResult.degree }} • {{ scanResult.university }}</p>
                    <p class="verify__scan-detail">№ {{ scanResult.number }}</p>
                  </div>
                </div>
                <div v-else class="verify__scan-error">
                  <AlertCircle size="20" class="verify__scan-icon" />
                  <div class="verify__scan-content">
                    <p class="verify__scan-title">{{ scanResult.message }}</p>
                  </div>
                </div>
                <button class="verify__scan-close" @click="clearResult">
                  <X size="14" />
                </button>
              </div>

              <!-- История сканирований -->
              <div v-if="scanHistory.length > 0" class="verify__scan-history">
                <div class="verify__scan-history-head">
                  <History size="14" />
                  <span>Недавние сканирования</span>
                  <button class="verify__scan-clear" @click="clearHistory">
                    <X size="12" />
                  </button>
                </div>
                <div class="verify__scan-history-list">
                  <div v-for="item in scanHistory" :key="item.id" class="verify__scan-history-item">
                    <CheckCircle size="12" class="verify__scan-history-icon" />
                    <div class="verify__scan-history-content">
                      <p class="verify__scan-history-name">{{ item.studentName }}</p>
                      <p class="verify__scan-history-detail">{{ item.number }}</p>
                      <p class="verify__scan-history-time">{{ item.verifiedAt }}</p>
                    </div>
                  </div>
                </div>
              </div>
              </div>

              <!-- Быстрые действия -->
              <div class="verify__sidebar-card verify__sidebar-card--accent">
                <h4 class="verify__sidebar-title">Быстрые действия</h4>
                <div class="verify__sidebar-actions">
                  <router-link to="/hr/bulk" class="verify__sidebar-btn">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                      <rect x="2" y="2" width="5" height="5" rx="1" stroke="currentColor" stroke-width="1.3"/>
                      <rect x="9" y="2" width="5" height="5" rx="1" stroke="currentColor" stroke-width="1.3"/>
                      <rect x="2" y="9" width="5" height="5" rx="1" stroke="currentColor" stroke-width="1.3"/>
                      <rect x="9" y="9" width="5" height="5" rx="1" stroke="currentColor" stroke-width="1.3"/>
                    </svg>
                    Массовая проверка
                  </router-link>
                  <router-link to="/hr/reports" class="verify__sidebar-btn">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                      <rect x="3" y="2" width="10" height="12" rx="1.5" stroke="currentColor" stroke-width="1.4"/>
                      <path d="M6 6h4M6 9h4M6 12h2" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/>
                    </svg>
                    Отчёты
                  </router-link>
                  <router-link to="/api-docs" class="verify__sidebar-btn">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                      <path d="M8 2L2 5v6l6 3 6-3V5L8 2z" stroke="currentColor" stroke-width="1.4" stroke-linejoin="round"/>
                      <path d="M6 8l1.5 1.5L10 7" stroke="currentColor" stroke-width="1.3" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    API документация
                  </router-link>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { Html5Qrcode } from 'html5-qrcode'
import { Camera, Upload, X, CheckCircle, AlertCircle, History } from 'lucide-vue-next'
import AppHeader from '../../components/common/AppHeader.vue'
import AppFooter from '../../components/common/AppFooter.vue'
import VerificationForm from '../../components/hr/VerificationForm.vue'

// База данных дипломов (временная)
const DIPLOMAS_DB = {
  '1': { 
    id: '1', 
    studentName: 'Иванов Иван Иванович', 
    degree: 'Бакалавр',
    number: 'DIP-2023-001', 
    university: 'МГТУ им. Баумана', 
    specialty: 'Информатика и ВТ', 
    year: '2023', 
    status: 'active',
    issueDate: '20.06.2023'
  },
  '2': { 
    id: '2', 
    studentName: 'Иванов Иван Иванович', 
    degree: 'Магистр',
    number: 'DIP-2024-789', 
    university: 'Финансовый университет', 
    specialty: 'Экономика', 
    year: '2024', 
    status: 'active',
    issueDate: '15.06.2024'
  },
  '3': { 
    id: '3', 
    studentName: 'Петров Петр Петрович', 
    degree: 'Бакалавр',
    number: 'DIP-2021-555', 
    university: 'МГУ им. Ломоносова', 
    specialty: 'Физика', 
    year: '2021', 
    status: 'revoked',
    issueDate: '01.07.2021'
  }
}

defineProps({
  token: { type: String, default: null }
})

// Reactive state
const scanMode = ref(null)
const scanResult = ref(null)
const scanHistory = ref([])
const error = ref('')
const loading = ref(false)
const previewImage = ref(null)
const isCameraReady = ref(false)

// Refs for DOM elements and library instance
const html5QrCodeRef = ref(null)
const fileInputRef = ref(null)
const qrReaderContainerRef = ref(null)

let cameraTimer = null

// Initialize camera after element is mounted
const initializeCamera = async () => {
  if (isCameraReady.value) return
  
  try {
    error.value = ''
    
    if (!qrReaderContainerRef.value) {
      throw new Error('Элемент для камеры не найден')
    }

    html5QrCodeRef.value = new Html5Qrcode("qr-reader")
    
    const devices = await Html5Qrcode.getCameras()
    if (devices && devices.length === 0) {
      throw new Error('Камеры не найдены на устройстве')
    }

    await html5QrCodeRef.value.start(
      { facingMode: "environment" },
      {
        fps: 10,
        qrbox: { width: 250, height: 250 },
        aspectRatio: 1.0
      },
      onScanSuccess,
      onScanFailure
    )
    
    isCameraReady.value = true
  } catch (err) {
    console.error('Camera initialization error:', err)
    
    let errorMessage = 'Не удалось получить доступ к камере. '
    
    if (err.name === 'NotAllowedError') {
      errorMessage += 'Разрешите доступ к камере в браузере.'
    } else if (err.name === 'NotFoundError') {
      errorMessage += 'Камера не найдена на устройстве.'
    } else if (err.name === 'NotReadableError') {
      errorMessage += 'Камера занята другим приложением.'
    } else if (err.name === 'OverconstrainedError') {
      errorMessage += 'Камера не поддерживает требуемые параметры.'
    } else {
      errorMessage += err.message || 'Проверьте разрешения.'
    }
    
    error.value = errorMessage
    scanMode.value = null
    isCameraReady.value = false
  }
}

// Watch for scanMode changes to initialize camera
const startCameraScan = async () => {
  error.value = ''
  scanResult.value = null
  scanMode.value = 'camera'
  isCameraReady.value = false
  
  // Wait for DOM update before initializing camera
  await nextTick()
  await initializeCamera()
}

const stopCameraScan = async () => {
  if (html5QrCodeRef.value) {
    try {
      await html5QrCodeRef.value.stop()
      await html5QrCodeRef.value.clear()
    } catch (err) {
      console.error('Error stopping camera:', err)
    }
    html5QrCodeRef.value = null
  }
  scanMode.value = null
  isCameraReady.value = false
}

const handleFileUpload = async (e) => {
  const file = e.target.files[0]
  if (!file) return

  if (!file.type.startsWith('image/')) {
    error.value = 'Пожалуйста, загрузите изображение (PNG, JPG, JPEG)'
    return
  }

  if (file.size > 5 * 1024 * 1024) {
    error.value = 'Файл слишком большой. Максимальный размер 5MB'
    return
  }

  try {
    error.value = ''
    scanResult.value = null
    scanMode.value = 'file'
    
    const reader = new FileReader()
    reader.onload = (event) => {
      previewImage.value = event.target.result
    }
    reader.readAsDataURL(file)

    const html5QrCode = new Html5Qrcode("qr-reader-file")
    
    await html5QrCode.scanFile(file, true)
      .then(decodedText => {
        processQRCode(decodedText)
        previewImage.value = null
        scanMode.value = null
      })
      .catch(err => {
        error.value = 'Не удалось распознать QR-код на изображении. Убедитесь, что файл содержит valid QR-код.'
        scanMode.value = null
        previewImage.value = null
      })

  } catch (err) {
    error.value = 'Ошибка при обработке файла'
    scanMode.value = null
    previewImage.value = null
  }
}

const onScanSuccess = (decodedText) => {
  processQRCode(decodedText)
}

const onScanFailure = (error) => {
  // Тихо игнорируем ошибки сканирования
}

const processQRCode = (decodedText) => {
  const match = decodedText.match(/check\/(\d+)/)
  if (match) {
    const diplomaId = match[1]
    verifyDiploma(diplomaId)
  } else {
    error.value = 'Неверный формат QR-кода'
    scanMode.value = null
  }
}

const verifyDiploma = (id) => {
  loading.value = true
  
  setTimeout(() => {
    const diploma = DIPLOMAS_DB[id]
    
    if (diploma) {
      scanResult.value = {
        ...diploma,
        verified: true,
        verifiedAt: new Date().toLocaleString('ru-RU')
      }
      
      const exists = scanHistory.value.find(item => item.id === id)
      if (!exists) {
        scanHistory.value = [
          { ...diploma, verifiedAt: new Date().toLocaleString('ru-RU') }, 
          ...scanHistory.value
        ].slice(0, 10)
      }
    } else {
      scanResult.value = {
        error: true,
        message: 'Диплом не найден в системе'
      }
    }
    
    stopCameraScan()
    loading.value = false
  }, 800)
}

const clearResult = () => {
  scanResult.value = null
  error.value = ''
  scanMode.value = null
  previewImage.value = null
  isCameraReady.value = false
  if (fileInputRef.value) {
    fileInputRef.value.value = ''
  }
}

const clearHistory = () => {
  scanHistory.value = []
}

function onVerify(result) {
  console.log('Результат проверки:', result)
}

function onError(err) {
  console.error('Ошибка проверки:', err)
}

// Cleanup on unmount
onUnmounted(() => {
  if (cameraTimer) {
    clearTimeout(cameraTimer)
  }
  stopCameraScan()
})
</script>

<style scoped>
.verify-view { min-height: 100vh; }

/* ===========================
   HERO
   =========================== */
.verify__hero {
  position: relative;
  background: #0d1f3c;
  padding: 72px 0 80px;
}

.verify__hero-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}

.verify__hero-grid-lines {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(
      rgba(255, 255, 255, 0.03) 1px,
      transparent 1px
    ),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.verify__hero-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.verify__hero-glow--1 {
  width: 500px;
  height: 500px;
  top: -150px;
  right: -100px;
  background: rgba(38, 75, 130, 0.45);
}

.verify__hero-glow--2 {
  width: 380px;
  height: 380px;
  bottom: -120px;
  left: -60px;
  background: rgba(72, 184, 194, 0.2);
}

.verify__hero-inner {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.verify__hero-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: var(--space-6);
}

.verify__hero-title {
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: var(--font-weight-bold);
  color: #fff;
  line-height: 1.1;
  letter-spacing: -0.02em;
}

.verify__hero-title-accent {
  color: var(--color-sea-clear);
}

.verify__hero-subtitle {
  max-width: 540px;
  font-size: var(--font-size-base);
  color: rgba(255, 255, 255, 0.65);
  line-height: 1.7;
}

.verify__examples {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--space-2);
  max-width: 640px;
}

.verify__examples-label {
  font-size: var(--font-size-xs);
  color: rgba(255, 255, 255, 0.4);
  white-space: nowrap;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.verify__examples-list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-2);
}

.verify__example-btn {
  padding: 4px 12px;
  font-family: var(--font-family);
  font-size: var(--font-size-xs);
  color: rgba(255, 255, 255, 0.7);
  background: rgba(255, 255, 255, 0.07);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: background var(--transition-fast), color var(--transition-fast);
}

.verify__example-btn:hover {
  background: rgba(255, 255, 255, 0.14);
  color: #fff;
}

.verify__hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-3);
  justify-content: center;
}

.verify__btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px 24px;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  font-family: var(--font-family);
  border-radius: var(--radius-base);
  text-decoration: none;
  cursor: pointer;
  transition: all var(--transition-base);
  border: 2px solid transparent;
}

.verify__btn--primary {
  background: var(--color-main-blue);
  color: #fff;
  border-color: var(--color-main-blue);
}

.verify__btn--primary:hover {
  background: #1a5bbd;
  border-color: #1a5bbd;
}

.verify__btn--outline {
  background: transparent;
  color: #fff;
  border-color: rgba(255, 255, 255, 0.3);
}

.verify__btn--outline:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.5);
}

.verify__btn--lg {
  padding: 14px 32px;
  font-size: var(--font-size-lg);
}

/* ===========================
   STATS STRIP
   =========================== */
.verify__stats-strip {
  background: #fff;
  border-bottom: 1px solid var(--color-gray-blue);
  overflow-x: hidden;
}

.verify__stats-inner {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0;
}

.verify__stat {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-5) var(--space-5);
  border-right: 1px solid var(--color-gray-blue);
}

.verify__stat:last-child { border-right: none; }

.verify__stat-value {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-main-blue);
  white-space: nowrap;
  flex-shrink: 0;
}

.verify__stat-text { display: flex; flex-direction: column; gap: 1px; }
.verify__stat-label { font-size: var(--font-size-sm); font-weight: var(--font-weight-semibold); color: var(--color-black); line-height: 1.3; }
.verify__stat-note { font-size: var(--font-size-xs); color: var(--color-pale-black); }

/* ===========================
   WORKSPACE
   =========================== */
.verify__workspace {
  padding: 80px 0;
  background: #f1f5f9;
  overflow-x: hidden;
}

.verify__workspace-head {
  text-align: center;
  margin-bottom: var(--space-10);
}

.verify__workspace-eyebrow {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--color-main-blue);
  margin-bottom: var(--space-2);
}

.verify__workspace-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  line-height: 1.2;
}

.verify__workspace-grid {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: var(--space-6);
  align-items: start;
}

/* --- Левая панель --- */
.verify__workspace-panel {
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.verify__panel-header {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-5) var(--space-6);
  background: #0d1f3c;
  border-bottom: 3px solid var(--color-main-blue);
}

.verify__panel-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  background: rgba(255, 255, 255, 0.08);
  color: var(--color-sea-clear);
  flex-shrink: 0;
}

.verify__panel-icon svg {
  width: 20px;
  height: 20px;
}

.verify__panel-title-wrap {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.verify__panel-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: #fff;
  margin: 0;
  line-height: 1.3;
}

.verify__panel-desc {
  font-size: var(--font-size-xs);
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

.verify__checker-card {
  padding: var(--space-6);
}

/* --- Правая колонка (сайдбар) --- */
.verify__workspace-sidebar {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.verify__sidebar-card {
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  padding: var(--space-5);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.verify__sidebar-card--accent {
  border-left: 3px solid var(--color-main-blue);
}

.verify__sidebar-title {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  margin: 0 0 var(--space-4);
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.verify__qr-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-6) var(--space-4);
  background: var(--color-pale-blue);
  border: 2px dashed var(--color-gray-blue);
  border-radius: var(--radius-lg);
  color: var(--color-pale-black);
}

.verify__qr-placeholder svg {
  opacity: 0.5;
}

.verify__qr-placeholder span {
  font-size: var(--font-size-xs);
  text-align: center;
}

/* ===== QR SCANNER ===== */
.verify__qr-scanner {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.verify__qr-mode-selector {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.verify__qr-mode-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  padding: var(--space-3);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  font-family: var(--font-family);
  color: var(--color-pale-black);
  background: var(--color-pale-blue);
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-base);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.verify__qr-mode-btn:hover {
  background: #dde5f0;
  color: var(--color-main-blue);
  border-color: var(--color-main-blue);
}

.verify__qr-mode-btn svg {
  flex-shrink: 0;
}

.verify__file-input {
  display: none;
}

.verify__qr-camera {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.verify__qr-reader {
  width: 100%;
  border-radius: var(--radius-md);
  overflow: hidden;
  background: #000;
}

.verify__qr-reader video {
  width: 100%;
  border-radius: var(--radius-md);
}

.verify__qr-stop-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  font-family: var(--font-family);
  color: #fff;
  background: #dc2626;
  border: none;
  border-radius: var(--radius-base);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.verify__qr-stop-btn:hover {
  background: #b91c1c;
}

.verify__qr-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
}

.verify__qr-img {
  max-width: 100%;
  max-height: 200px;
  border-radius: var(--radius-md);
  object-fit: contain;
}

.verify__qr-preview-text {
  font-size: var(--font-size-xs);
  color: var(--color-pale-black);
}

/* ===== SCAN RESULT ===== */
.verify__scan-result {
  display: flex;
  align-items: flex-start;
  gap: var(--space-2);
  padding: var(--space-3);
  margin-top: var(--space-3);
  background: #e6f5ee;
  border: 1px solid #86efac;
  border-radius: var(--radius-md);
  position: relative;
}

.verify__scan-result--error {
  background: #fef2f2;
  border-color: #fca5a5;
}

.verify__scan-success,
.verify__scan-error {
  display: flex;
  align-items: flex-start;
  gap: var(--space-2);
  flex: 1;
  min-width: 0;
}

.verify__scan-icon {
  flex-shrink: 0;
  margin-top: 2px;
}

.verify__scan-success .verify__scan-icon {
  color: #16a34a;
}

.verify__scan-error .verify__scan-icon {
  color: #dc2626;
}

.verify__scan-content {
  flex: 1;
  min-width: 0;
}

.verify__scan-title {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  margin: 0 0 4px;
  line-height: 1.3;
}

.verify__scan-detail {
  font-size: var(--font-size-xs);
  color: var(--color-pale-black);
  margin: 2px 0;
  line-height: 1.4;
}

.verify__scan-close {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px;
  background: transparent;
  border: none;
  border-radius: var(--radius-sm);
  cursor: pointer;
  color: var(--color-pale-black);
  transition: all var(--transition-fast);
  flex-shrink: 0;
}

.verify__scan-close:hover {
  background: rgba(0, 0, 0, 0.08);
  color: var(--color-black);
}

/* ===== SCAN HISTORY ===== */
.verify__scan-history {
  margin-top: var(--space-3);
  padding: var(--space-3);
  background: #f8fafc;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-md);
}

.verify__scan-history-head {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: var(--space-2);
}

.verify__scan-history-head svg {
  flex-shrink: 0;
  color: var(--color-main-blue);
}

.verify__scan-clear {
  margin-left: auto;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2px;
  background: transparent;
  border: none;
  border-radius: var(--radius-sm);
  cursor: pointer;
  color: var(--color-pale-black);
  transition: all var(--transition-fast);
}

.verify__scan-clear:hover {
  background: rgba(0, 0, 0, 0.08);
  color: var(--color-black);
}

.verify__scan-history-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.verify__scan-history-item {
  display: flex;
  align-items: flex-start;
  gap: var(--space-2);
  padding: var(--space-2);
  background: #fff;
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-gray-blue);
}

.verify__scan-history-icon {
  flex-shrink: 0;
  margin-top: 2px;
  color: #16a34a;
}

.verify__scan-history-content {
  flex: 1;
  min-width: 0;
}

.verify__scan-history-name {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  color: var(--color-black);
  margin: 0 0 2px;
  line-height: 1.3;
}

.verify__scan-history-detail {
  font-size: 11px;
  color: var(--color-pale-black);
  margin: 1px 0;
  line-height: 1.3;
}

.verify__scan-history-time {
  font-size: 10px;
  color: var(--color-pale-black);
  margin: 1px 0 0;
  opacity: 0.7;
}

.verify__sidebar-actions {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.verify__sidebar-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--color-pale-black);
  background: var(--color-pale-blue);
  border-radius: var(--radius-base);
  text-decoration: none;
  transition: background var(--transition-fast), color var(--transition-fast);
}

.verify__sidebar-btn svg {
  flex-shrink: 0;
  color: var(--color-main-blue);
}

.verify__sidebar-btn:hover {
  background: #dde5f0;
  color: var(--color-main-blue);
}

/* ===========================
   FEATURES
   =========================== */
.verify__features {
  padding: 80px 0;
  background: #fff;
  overflow-x: hidden;
}

.verify__features-head { text-align: center; margin-bottom: var(--space-12); }

.verify__features-eyebrow {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--color-main-blue);
  margin-bottom: var(--space-2);
}

.verify__features-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  line-height: 1.2;
}

.verify__steps {
  display: flex;
  align-items: stretch;
  gap: var(--space-4);
}

.verify__step {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
  padding: var(--space-6);
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  background: #fff;
  position: relative;
  overflow: visible;
  transition: box-shadow var(--transition-base), border-color var(--transition-base);
  cursor: pointer;
}

.verify__step:hover {
  box-shadow: var(--shadow-md);
  border-color: var(--color-main-blue);
}

.verify__step::after {
  content: "→";
  position: absolute;
  right: calc(-1 * var(--space-4) / 2 - 7px);
  top: 50%;
  transform: translateY(-50%);
  font-size: 14px;
  color: var(--color-gray-light);
  pointer-events: none;
  z-index: 2;
}

.verify__step:last-child::after { display: none; }

.verify__step-icon {
  width: 44px; height: 44px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 1.25rem;
}

.verify__step-icon--blue { background: var(--color-pale-blue); color: var(--color-main-blue); }
.verify__step-icon--green { background: #e6f5ee; color: var(--color-green); }
.verify__step-icon--orange { background: #fff0e4; color: var(--color-orange); }

.verify__step-num {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-bold);
  letter-spacing: 0.08em;
  color: var(--color-pale-black);
  text-transform: uppercase;
}

.verify__step-title { font-size: var(--font-size-md); font-weight: var(--font-weight-bold); color: var(--color-black); line-height: 1.3; }
.verify__step-desc { font-size: var(--font-size-sm); color: var(--color-pale-black); line-height: 1.6; }

/* ===========================
   CTA
   =========================== */
.verify__cta {
  padding: 80px 0;
  background: #0d1f3c;
  text-align: center;
}

.verify__cta-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* ===========================
   АДАПТИВ
   =========================== */
@media (max-width: 1100px) {
  .verify__stats-inner { grid-template-columns: repeat(2, 1fr); }
  .verify__stat:nth-child(2) { border-right: none; }
  .verify__stat:nth-child(1), .verify__stat:nth-child(2) { border-bottom: 1px solid var(--color-gray-blue); }

  .verify__workspace-grid {
    grid-template-columns: 1fr;
  }

  .verify__workspace-sidebar {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: var(--space-4);
  }
}

@media (max-width: 768px) {
  .verify__hero { padding: 44px 0 48px; }
  .verify__hero-title { font-size: clamp(1.5rem, 6vw, 2.2rem); }
  .verify__hero-subtitle { font-size: var(--font-size-sm); }

  .verify__stat {
    flex-direction: column;
    align-items: flex-start;
    gap: 2px;
    padding: var(--space-3) var(--space-4);
  }
  .verify__stat-value { font-size: var(--font-size-base); }
  .verify__stat-note { display: none; }

  .verify__steps { flex-direction: column; gap: var(--space-3); }
  .verify__step { padding: var(--space-4); }
  .verify__step::after { display: none; }

  .verify__workspace,
  .verify__features,
  .verify__cta { padding: 48px 0; }

  .verify__workspace-title { font-size: var(--font-size-2xl); }
  .verify__workspace-head { margin-bottom: var(--space-8); }

  .verify__checker-card { padding: var(--space-4); }

  .verify__panel-header { padding: var(--space-4); }

  .verify__workspace-sidebar {
    grid-template-columns: 1fr;
  }

  .verify__features-title { font-size: var(--font-size-2xl); }
  .verify__features-head { margin-bottom: var(--space-8); }
}

@media (max-width: 540px) {
  .verify__hero { padding: 32px 0 40px; }
  .verify__hero-title { font-size: 1.5rem; line-height: 1.2; }
  .verify__hero-subtitle { font-size: var(--font-size-sm); }
  .verify__examples { flex-direction: column; align-items: flex-start; }

  .verify__stats-inner { grid-template-columns: 1fr 1fr; }
  .verify__stat {
    flex-direction: column;
    align-items: flex-start;
    padding: var(--space-3) var(--space-3);
    gap: 2px;
  }
  .verify__stat:nth-child(1), .verify__stat:nth-child(3) { border-right: 1px solid var(--color-gray-blue); }
  .verify__stat:nth-child(2), .verify__stat:nth-child(4) { border-right: none; }
  .verify__stat-value { font-size: var(--font-size-base); white-space: normal; }
  .verify__stat-label { font-size: 11px; }
  .verify__stat-note { display: none; }

  .verify__workspace-title { font-size: var(--font-size-xl); }
}
</style>
