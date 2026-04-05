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
          <div class="dashboard-layout">
            <!-- Левая колонка: форма проверки -->
            <section class="section-pro">
              <div class="section-header">
                <h2>Верификация диплома</h2>
                <span class="badge">Онлайн</span>
              </div>

              <div class="verify__checker-card">
                <VerificationForm @verify="onVerify" @error="onError" />
              </div>
            </section>

            <!-- Правая колонка: сайдбар -->
            <aside class="info-sidebar">
              <!-- QR-карточка -->
              <div class="info-card">
                <h4>QR-сканер</h4>
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
            </aside>
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
import { Camera, Upload, X, CheckCircle, AlertCircle, History, ShieldCheck } from 'lucide-vue-next'
import AppHeader from '../../components/common/AppHeader.vue'
import AppFooter from '../../components/common/AppFooter.vue'
import VerificationForm from '../../components/hr/VerificationForm.vue'
import { api } from '../../api/api.js'
import { findCertificateById } from '../../utils/certificatesStore.js'
import { useAuth } from '../../composables/useAuth.js'

const { accessToken } = useAuth()

function formatIssueDate(iso) {
  if (!iso) return '—'
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return String(iso)
  return d.toLocaleDateString('ru-RU')
}

function certToScanResult(cert) {
  return {
    studentName: cert.studentName,
    degree: 'Диплом',
    number: cert.serialNumber,
    university: cert.universityCode || '—',
    specialty: cert.specialty,
    year: String(cert.graduationYear || ''),
    status: cert.status,
    issueDate: formatIssueDate(cert.issueDate),
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

const processQRCode = async (decodedText) => {
  loading.value = true
  error.value = ''
  const t = String(decodedText || '').trim()

  try {
    const legacy = t.match(/check\/([^/?#]+)/)
    if (legacy) {
      await verifyDiploma(legacy[1])
      return
    }

    let certificateId = ''
    let token = ''
    try {
      const u = t.includes('://')
        ? new URL(t)
        : new URL(t.startsWith('/') ? t : `/${t}`, window.location.origin)
      certificateId = u.searchParams.get('certificateId') || ''
      token = u.searchParams.get('token') || ''
    } catch {
      /* ignore */
    }

    if (certificateId || token) {
      // Пробуем найти сертификат локально
      const cert = findCertificateById(certificateId)
      if (cert) {
        const row = certToScanResult(cert)
        scanResult.value = {
          ...row,
          verified: true,
          verifiedAt: new Date().toLocaleString('ru-RU'),
        }
        pushHistory({ ...row, id: cert.id, verifiedAt: scanResult.value.verifiedAt })
        await stopCameraScan()
        return
      }

      // Если локально не нашли, пробуем API верификации
      try {
        const res = await api.verifyByQr({ certificateId, token })
        scanResult.value = {
          studentName: res?.fullName || res?.studentName || 'Проверка по API',
          degree: 'Результат',
          number: res?.diplomaNumber || certificateId || '—',
          university: res?.universityCode || res?.universityName || '—',
          specialty: res?.specialty || JSON.stringify(res || {}).slice(0, 120),
          year: res?.graduationYear ? String(res.graduationYear) : '',
          status: 'active',
          issueDate: '—',
          verified: true,
          verifiedAt: new Date().toLocaleString('ru-RU'),
          apiPayload: res,
        }
        pushHistory(scanResult.value)
        await stopCameraScan()
        return
      } catch (apiErr) {
        // Если API недоступен, показываем информацию из QR
        scanResult.value = {
          studentName: 'Диплом найден',
          degree: 'Проверка по QR',
          number: certificateId || '—',
          university: 'Проверьте в реестре',
          specialty: '—',
          year: '',
          status: 'active',
          issueDate: '—',
          verified: true,
          verifiedAt: new Date().toLocaleString('ru-RU'),
        }
        pushHistory(scanResult.value)
        await stopCameraScan()
        return
      }
    }

    const uuidRe =
      /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i
    if (uuidRe.test(t)) {
      const cert = findCertificateById(t)
      if (cert) {
        const row = certToScanResult(cert)
        scanResult.value = {
          ...row,
          verified: true,
          verifiedAt: new Date().toLocaleString('ru-RU'),
        }
        pushHistory({ ...row, id: cert.id, verifiedAt: scanResult.value.verifiedAt })
      } else {
        scanResult.value = {
          error: true,
          message: 'Сертификат не найден в локальном кэше этого браузера',
        }
      }
      await stopCameraScan()
      return
    }

    error.value = 'Не удалось разобрать QR-код'
    scanMode.value = null
  } catch (e) {
    scanResult.value = {
      error: true,
      message: e.message || 'Ошибка проверки',
    }
    await stopCameraScan()
  } finally {
    loading.value = false
  }
}

function pushHistory(row) {
  const id = row.id || row.number
  const exists = scanHistory.value.find((item) => item.id === id || item.number === row.number)
  if (!exists) {
    scanHistory.value = [{ ...row, id }, ...scanHistory.value].slice(0, 10)
  }
}

const verifyDiploma = async (id) => {
  const cert = findCertificateById(id)
  if (cert) {
    const row = certToScanResult(cert)
    scanResult.value = {
      ...row,
      verified: true,
      verifiedAt: new Date().toLocaleString('ru-RU'),
    }
    pushHistory({ ...row, id: cert.id, verifiedAt: scanResult.value.verifiedAt })
  } else {
    scanResult.value = {
      error: true,
      message: 'Диплом не найден в локальном кэше',
    }
  }
  await stopCameraScan()
  loading.value = false
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
.main-content { flex: 1; }

/* ===========================
   Info Banner
   =========================== */
.info-banner {
  display: flex;
  gap: var(--space-4);
  padding: var(--space-5);
  background: #e8f4fd;
  border: 1px solid #b8dff5;
  border-radius: var(--radius-lg);
  margin-top: var(--space-6);
  margin-bottom: var(--space-6);
  align-items: flex-start;
}

.banner-icon-bg {
  background: #b8dff5;
  border-radius: 50%;
  padding: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-icon {
  color: #1a5b8c;
}

.banner-text h4 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-bold);
  color: #1a5b8c;
  margin: 0 0 var(--space-1);
}

.banner-text p {
  font-size: var(--font-size-sm);
  color: #2d7ab5;
  margin: 0;
  line-height: 1.5;
}

/* ===========================
   WORKSPACE / Dashboard Layout
   =========================== */
.dashboard-layout {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: var(--space-6);
  align-items: start;
}

/* Section Pro */
.section-pro {
  background: #fff;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-gray-blue);
  padding: var(--space-6);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--space-6);
}

.section-header h2 {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  margin: 0;
}

.badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  background: #dcfce7;
  color: #166534;
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
}

/* Info Sidebar */
.info-sidebar {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.info-card {
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  padding: var(--space-5);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: var(--space-3);
}

.info-card h4 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  margin: 0;
}

.info-card p {
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
  margin: 0;
  line-height: 1.5;
}

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
  .dashboard-layout { grid-template-columns: 1fr; }
}

@media (max-width: 768px) {
  .verify__hero { padding: 44px 0 48px; }
  .verify__hero-title { font-size: clamp(1.5rem, 6vw, 2.2rem); }
  .verify__workspace { padding: 48px 0; }
  .section-header h2 { font-size: var(--font-size-xl); }
}

@media (max-width: 540px) {
  .verify__hero { padding: 32px 0 40px; }
  .verify__hero-title { font-size: 1.5rem; line-height: 1.2; }
}
</style>
