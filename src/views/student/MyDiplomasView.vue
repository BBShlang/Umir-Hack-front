<template>
  <div class="my-diplomas-view">
    <AppHeader />
    <main class="main-content">
      <!-- ===== HERO ===== -->
      <section class="diplomas__hero">
        <div class="diplomas__hero-bg" aria-hidden="true">
          <div class="diplomas__hero-grid-lines" />
          <div class="diplomas__hero-glow diplomas__hero-glow--1" />
          <div class="diplomas__hero-glow diplomas__hero-glow--2" />
        </div>

        <div class="container diplomas__hero-inner">
          <div class="diplomas__hero-content">
            <h1 class="diplomas__title">
              Мои дипломы<br />
              <span class="diplomas__title-accent">Цифровые сертификаты</span>
            </h1>
            <p class="diplomas__subtitle">
              Скачивайте документы, делитесь с работодателями и отслеживайте проверки ваших дипломов.
            </p>
            <div class="diplomas__hero-actions">
              <router-link to="/student/dashboard" class="diplomas__btn diplomas__btn--outline">На дашборд</router-link>

            </div>
          </div>
        </div>
      </section>

      <!-- ===== Состояния загрузки и пустоты ===== -->
      <section v-if="loading" class="diplomas__loading-section">
        <div class="container">
          <div class="diplomas__loading-state">
            <div class="spinner" aria-hidden="true"></div>
            <p>Загрузка дипломов...</p>
          </div>
        </div>
      </section>

      <section v-else-if="!diplomas.length" class="diplomas__empty-section">
        <div class="container">
          <div class="diplomas__empty-state">
            <div class="diplomas__empty-icon">📄</div>
            <h2 class="diplomas__how-title">У вас пока нет дипломов</h2>
            <p class="diplomas__subtitle">Ваш ВУЗ ещё не загрузил ваши данные в реестр</p>
            <router-link to="/student/dashboard" class="diplomas__btn diplomas__btn--primary">На дашборд</router-link>
          </div>
        </div>
      </section>

      <!-- ===== Карточки дипломов ===== -->
      <section v-else class="dashboard-pro">
        <!-- Info Banner -->
        <div class="container">
          <div class="info-banner">
            <div class="banner-icon-bg">
              <AlertCircle :size="24" class="banner-icon" />
            </div>
            <div class="banner-text">
              <h4>Как работодатель проверит ваш диплом?</h4>
              <p>Просто отправьте ему ссылку или QR-код из карточки. Проверка займет 2 секунды — без звонков в вуз и лишних документов.</p>
            </div>
          </div>
        </div>

        <div class="container">
          <div class="dashboard-layout">
            <!-- Main Section -->
            <section class="section-pro">
              <div class="section-header">
                <h2>Мои дипломы</h2>
                <span class="badge">{{ diplomas.length }} документов</span>
              </div>
              
              <div class="diplomas-grid-v">
                <!-- Skeleton Loading -->
                <template v-if="skeletonLoading">
                  <div 
                    v-for="i in 3" 
                    :key="i" 
                    class="diploma-card-v skeleton"
                  >
                    <div class="skeleton-accent" />
                    <div class="skeleton-body">
                      <div class="skeleton-line short" />
                      <div class="skeleton-line" />
                      <div class="skeleton-line" />
                      <div class="skeleton-line long" />
                      <div class="skeleton-line" />
                    </div>
                  </div>
                </template>

                <!-- Diplomas List -->
                <template v-else>
                  <div 
                    v-for="(d, idx) in diplomas" 
                    :key="d.id" 
                    :class="[
                      'diploma-card-v',
                      { 'expanded': expandedId === d.id },
                      { 'revoked': d.status === 'revoked' }
                    ]"
                    :style="{ animationDelay: `${idx * 100}ms` }"
                  >
                    <div class="card-v-accent" />
                    
                    <!-- Status Seals -->
                    <div v-if="d.status === 'active'" class="verified-seal">
                      <CheckCircle :size="16" />
                      <span>Verified</span>
                    </div>
                    <div v-if="d.status === 'revoked'" class="revoked-seal">
                      <AlertCircle :size="16" />
                      <span>Аннулирован</span>
                    </div>

                    <!-- Card Body -->
                    <div class="card-v-body">
                      <span class="year-tag-subtle">{{ d.year }} г.</span>
                      <Building2 :size="24" class="uni-icon" />
                      <h3>{{ d.university }}</h3>
                      <p class="student-name">{{ d.fullName }}</p>
                      <span class="degree-badge">{{ d.degree }}</span>
                      <p class="specialty-v">{{ d.specialty }}</p>
                      
                      <div class="diploma-number-row">
                        <FileText :size="16" class="icon-number" />
                        <span class="label">Номер:</span>
                        <span class="value mono">{{ d.number }}</span>
                      </div>
                      
                      <div v-if="d.status === 'active'" class="stats-row">
                        <div class="stat-item-small">
                          <Eye :size="14" />
                          <span>{{ d.verifiedCount }} проверок</span>
                        </div>
                        <div class="stat-item-small">
                          <Clock :size="14" />
                          <span>{{ d.lastVerified }}</span>
                        </div>
                      </div>
                      
                      <span :class="['status-pill', d.status]">
                        <span class="status-dot" /> 
                        {{ d.status === 'active' ? 'Действителен' : 'Аннулирован' }}
                      </span>
                    </div>

                    <!-- Card Footer -->
                    <div class="card-v-footer">
                      <button 
                        :class="['btn-share-v', { 'disabled': d.status === 'revoked' }]" 
                        @click="d.status === 'active' && handleShare(d.id)" 
                        :disabled="d.status === 'revoked'"
                      >
                        <ExternalLink :size="18" />
                        {{ expandedId === d.id ? 'Свернуть' : 'Поделиться с работодателем' }}
                      </button>
                    </div>

                    <!-- QR Panel (Expanded) -->
                    <div v-if="expandedId === d.id && d.status === 'active'" class="qr-panel-v">
                      <!-- Initial State -->
                      <div v-if="!qrUrl" class="qr-init-v">
                        <Shield :size="22" class="shield-icon" />
                        <p class="qr-ref">
                          Для диплома № <span class="mono">{{ d.number }}</span>
                        </p>
                        <p class="text-muted">Ссылка активна 24 часа</p>
                        <button class="btn-generate-v" @click="generateQR">
                          <QrCode :size="18" /> Создать QR-код
                        </button>
                      </div>

                      <!-- Active QR -->
                      <div v-else-if="qrActive" class="qr-result-v">
                        <div class="qr-timer-v">
                          <Clock :size="15" />
                          <span>Осталось: <strong class="countdown">{{ timeLeft }}</strong></span>
                        </div>
                        
                        <div class="qr-frame qr-frame-print">
                          <QrcodeVue 
                            :value="qrUrl" 
                            :size="140" 
                            level="H"
                            :margin="1"
                            render-as="svg"
                          />
                          <div class="qr-frame-badge">
                            <GraduationCap :size="20" />
                            <span>Diasoft Verify</span>
                          </div>
                        </div>
                        
                        <div class="qr-actions-v">
                          <button class="btn-action" @click="downloadQR">
                            <Download :size="15" /> PNG
                          </button>
                          <button class="btn-action" @click="showFullscreen = true">
                            <Maximize2 :size="15" /> Полный экран
                          </button>
                          
                          <!-- Share Menu -->
                          <div class="share-wrapper" ref="shareMenuRef">
                            <button class="btn-action" @click="showShareMenu = !showShareMenu">
                              <Share2 :size="15" /> Поделиться
                            </button>
                            <transition name="slide-up">
                              <div v-if="showShareMenu" class="share-menu">
                                <button @click="shareToMessenger('telegram')">
                                  <MessageCircle :size="16" /> Telegram
                                </button>
                                <button @click="shareToMessenger('whatsapp')">
                                  <Send :size="16" /> WhatsApp
                                </button>
                                <button @click="shareToMessenger('email')">
                                  <Mail :size="16" /> Email
                                </button>
                              </div>
                            </transition>
                          </div>
                          
                          <button class="btn-rev-v" @click="revokeQR">
                            <AlertCircle :size="15" /> Отозвать
                          </button>
                        </div>
                      </div>

                      <!-- Revoked QR -->
                      <div v-else class="qr-revoked-v">
                        <AlertCircle :size="24" />
                        <span>Доступ отозван</span>
                        <button class="btn-regen-v" @click="generateQR">
                          <RefreshCw :size="14" /> Новый QR
                        </button>
                      </div>
                    </div>
                  </div>
                </template>
              </div>
            </section>

            <!-- Sidebar -->
            <aside class="info-sidebar">
              <div class="info-card sidebar-card-security">
                <div class="sidebar-icon-wrap bg-green">
                  <Shield :size="24" />
                </div>
                <h4>Безопасность</h4>
                <p>Все дипломы защищены криптографической подписью вуза.</p>
              </div>
              
              <div class="info-card sidebar-card-support">
                <div class="sidebar-icon-wrap bg-blue">
                  <Mail :size="24" />
                </div>
                <h4>Поддержка</h4>
                <a href="mailto:support@diasoft.ru" class="sidebar-link">support@diasoft.ru</a>
              </div>
              
              <div class="info-card sidebar-card-help">
                <div class="sidebar-icon-wrap bg-purple">
                  <HelpCircle :size="24" />
                </div>
                <h4>Как это работает?</h4>
                <p>1. Вуз загружает данные<br/>2. Вы получаете QR<br/>3. Работодатель проверяет</p>
              </div>
            </aside>
          </div>
        </div>
      </section>

    </main>
    
    <!-- Fullscreen QR Modal -->
    <Transition name="fade">
      <div 
        v-if="showFullscreen && qrUrl" 
        class="fullscreen-overlay" 
        @click="showFullscreen = false"
      >
        <div class="fullscreen-content" @click.stop>
          <button class="fullscreen-close" @click="showFullscreen = false">
            <X :size="24" />
          </button>
          
          <div class="fullscreen-header">
            <GraduationCap :size="32" />
            <h3>Проверка диплома</h3>
            <p class="fullscreen-subtitle">Отсканируйте QR-код работодателем</p>
          </div>
          
          <div class="fullscreen-qr-wrapper">
            <QrcodeVue 
              :value="qrUrl" 
              :size="280" 
              level="H"
              :margin="1"
              render-as="svg"
            />
          </div>
          
          <div class="fullscreen-info">
            <p class="fullscreen-diploma">
              Диплом № {{ currentDiploma?.number }}
            </p>
            <p class="fullscreen-timer">
              <Clock :size="16" /> Активно: <strong>{{ timeLeft }}</strong>
            </p>
          </div>
          
          <div class="fullscreen-actions">
            <button class="btn-fullscreen" @click="copyToClipboard">
              <component :is="copied ? CheckCircle : Copy" :size="18" /> 
              {{ copied ? 'Скопировано!' : 'Копировать ссылку' }}
            </button>
            <button class="btn-fullscreen-secondary" @click="downloadQR">
              <Download :size="18" /> Скачать PNG
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <!-- Toast Notification -->
    <Transition name="slide-up">
      <div v-if="showToast" class="toast-notification show">
        <CheckCircle :size="18" />
        <span>{{ toastMessage }}</span>
        <button @click="showToast = false">
          <X :size="16" />
        </button>
      </div>
    </Transition>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue'
import { useEventListener } from '@vueuse/core'
import QrcodeVue from 'qrcode.vue'
import { 
  GraduationCap, LogOut, Copy, Download, 
  Shield, Clock, CheckCircle, AlertCircle,
  Building2, FileText, QrCode, RefreshCw, 
  ExternalLink, Mail, HelpCircle,
  Share2, MessageCircle, Send, X,
  Maximize2, Eye
} from 'lucide-vue-next'
import { useRouter } from 'vue-router'
import AppFooter from '../../components/common/AppFooter.vue'
import VerificationTracker from '../../components/student/VerificationTracker.vue'

const router = useRouter()

// База данных дипломов
const DIPLOMAS_DB = [
  { id: '1', studentId: 'STU-001', fullName: 'Иванов Иван Иванович', degree: 'Бакалавр', number: 'DIP-2023-001', university: 'МГТУ им. Баумана', specialty: 'Информатика и ВТ', year: '2023', status: 'active', verifiedCount: 12, lastVerified: '2 часа назад' },
  { id: '2', studentId: 'STU-001', fullName: 'Иванов Иван Иванович', degree: 'Магистр', number: 'DIP-2024-789', university: 'Финансовый университет', specialty: 'Экономика', year: '2024', status: 'active', verifiedCount: 3, lastVerified: 'вчера' },
  { id: '3', studentId: 'STU-001', fullName: 'Иванов Иван Иванович', degree: 'Бакалавр', number: 'DIP-2021-555', university: 'МГУ им. Ломоносова', specialty: 'Физика', year: '2021', status: 'revoked', verifiedCount: 0, lastVerified: '-' }
]

const user = {
  id: 'STU-001',
  name: 'Иванов Иван Иванович'
}

// Реактивные состояния
const loading = ref(true)
const skeletonLoading = ref(true)
const diplomas = ref([])
const expandedId = ref(null)
const showFullscreen = ref(false)

const FIXED_TTL = '24h'
const qrUrl = ref('')
const qrExpiry = ref(null)
const timeLeft = ref(null)
const copied = ref(false)
const qrActive = ref(true)
const showToast = ref(false)
const toastMessage = ref('')
const showShareMenu = ref(false)

// Рефы для DOM-элементов
const shareMenuRef = ref(null)
const fileInputRef = ref(null)

// Таймеры для очистки
let loadingTimer = null
let countdownInterval = null

const activeCount = computed(() => diplomas.value.filter(d => d.status === 'active').length)
const downloadsCount = computed(() => diplomas.value.length * 3)

// Загрузка дипломов при монтировании
onMounted(() => {
  loadingTimer = setTimeout(() => {
    diplomas.value = DIPLOMAS_DB.filter(d => d.studentId === user.id)
    skeletonLoading.value = false
    loading.value = false
  }, 600)
})

// Таймер обратного отсчёта для QR-кода
const startCountdown = () => {
  if (countdownInterval) clearInterval(countdownInterval)
  
  countdownInterval = setInterval(() => {
    if (!qrExpiry.value || !qrActive.value) return
    
    const now = new Date()
    const diff = qrExpiry.value - now
    
    if (diff <= 0) {
      timeLeft.value = '00:00:00'
      qrActive.value = false
      clearInterval(countdownInterval)
    } else {
      const h = Math.floor(diff / 3600000)
      const m = Math.floor((diff % 3600000) / 60000)
      const s = Math.floor((diff % 60000) / 1000)
      timeLeft.value = `${h}ч ${m}м ${s}с`
    }
  }, 1000)
}

const stopCountdown = () => {
  if (countdownInterval) {
    clearInterval(countdownInterval)
    countdownInterval = null
  }
}

// Обработчик клика вне меню шеринга
useEventListener(document, 'mousedown', (event) => {
  if (shareMenuRef.value && !shareMenuRef.value.contains(event.target)) {
    showShareMenu.value = false
  }
})

// Обработчик клавиши Escape для выхода из полноэкранного режима
useEventListener(window, 'keydown', (e) => {
  if (e.key === 'Escape') {
    showFullscreen.value = false
  }
})

// Показ уведомления
const showNotification = (message) => {
  toastMessage.value = message
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 3000)
}

// Переключение раскрытия карточки
const handleShare = (id) => {
  if (expandedId.value === id) {
    expandedId.value = null
    resetQR()
  } else {
    expandedId.value = id
    qrUrl.value = ''
    qrActive.value = true
    qrExpiry.value = null
    timeLeft.value = null
  }
}

// Генерация QR-кода
const generateQR = () => {
  if (!expandedId.value || !qrActive.value) return
  
  const expiry = new Date(Date.now() + 24 * 3600000)
  qrExpiry.value = expiry
  qrUrl.value = `https://verify.diasoft.ru/check/${expandedId.value}?ttl=${FIXED_TTL}&exp=${expiry.getTime()}`
  qrActive.value = true
  
  startCountdown()
  showNotification('QR-код успешно сгенерирован')
}

// Копирование ссылки в буфер обмена
const copyToClipboard = async () => {
  if (!qrUrl.value) return
  
  try {
    await navigator.clipboard.writeText(qrUrl.value)
    copied.value = true
    showNotification('Ссылка скопирована в буфер обмена')
    setTimeout(() => {
      copied.value = false
    }, 2000)
  } catch (err) {
    showNotification('Не удалось скопировать ссылку')
  }
}

// Сброс QR-состояния
const resetQR = () => {
  qrUrl.value = ''
  qrExpiry.value = null
  timeLeft.value = null
  qrActive.value = true
  stopCountdown()
}

// Отзыв доступа к QR-коду
const revokeQR = () => {
  qrActive.value = false
  qrUrl.value = ''
  stopCountdown()
  showNotification('Доступ отозван')
}

// Скачивание QR-кода как PNG
const downloadQR = () => {
  const qrFrame = document.querySelector('.qr-frame-print')
  if (!qrFrame) return
  
  const svg = qrFrame.querySelector('svg')
  if (!svg) return
  
  const svgData = new XMLSerializer().serializeToString(svg)
  const canvas = document.createElement('canvas')
  const ctx = canvas.getContext('2d')
  const img = new Image()
  
  img.onload = () => {
    canvas.width = 300
    canvas.height = 300
    ctx.fillStyle = 'white'
    ctx.fillRect(0, 0, canvas.width, canvas.height)
    ctx.drawImage(img, 0, 0, 300, 300)
    
    const pngFile = canvas.toDataURL('image/png')
    const downloadLink = document.createElement('a')
    downloadLink.download = `diploma_qr_${expandedId.value}.png`
    downloadLink.href = pngFile
    downloadLink.click()
    
    showNotification('QR-код скачан')
  }
  
  img.src = 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(svgData)))
}

// Шаринг в мессенджеры
const shareToMessenger = (platform) => {
  const text = `Проверка моего диплома: ${qrUrl.value}`
  let url = ''
  
  switch(platform) {
    case 'telegram':
      url = `https://t.me/share/url?url=${encodeURIComponent(qrUrl.value)}&text=${encodeURIComponent('Мой верифицированный диплом')}`
      break
    case 'whatsapp':
      url = `https://wa.me/?text=${encodeURIComponent(text)}`
      break
    case 'email':
      url = `mailto:?subject=Верификация диплома&body=${encodeURIComponent(text)}`
      break
    default:
      return
  }
  
  window.open(url, '_blank', 'width=600,height=400')
  showShareMenu.value = false
}

// Выход из аккаунта
const handleLogout = () => {
  emit('logout')
}

// Очистка при размонтировании
onUnmounted(() => {
  if (loadingTimer) clearTimeout(loadingTimer)
  stopCountdown()
})

// Вычисляемое свойство для текущего диплома в полноэкранном режиме
const currentDiploma = computed(() => {
  return diplomas.value.find(d => d.id === expandedId.value)
})

function onDownload(diploma) {
  alert(`Скачивание сертификата для ${diploma.serialNumber}`)
}

function onShare(diploma) {
  router.push({ name: 'student-share', params: { id: diploma.id } })
}
</script>

<style scoped>
.my-diplomas-view { min-height: 100vh; }

.main-content { flex: 1; }

/* ===========================
   HERO
   =========================== */
.diplomas__hero {
  position: relative;
  background: #0d1f3c;
  padding: 72px 0 80px;
}

.diplomas__hero-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}

.diplomas__hero-grid-lines {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(
      rgba(255, 255, 255, 0.03) 1px,
      transparent 1px
    ),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.diplomas__hero-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.diplomas__hero-glow--1 {
  width: 500px;
  height: 500px;
  top: -150px;
  right: -100px;
  background: rgba(38, 75, 130, 0.45);
}

.diplomas__hero-glow--2 {
  width: 380px;
  height: 380px;
  bottom: -120px;
  left: -60px;
  background: rgba(72, 184, 194, 0.2);
}

.diplomas__hero-inner {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.diplomas__hero-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: var(--space-6);
}

.diplomas__title {
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: var(--font-weight-bold);
  color: #fff;
  line-height: 1.1;
  letter-spacing: -0.02em;
}

.diplomas__title-accent {
  color: var(--color-sea-clear);
}

.diplomas__subtitle {
  max-width: 540px;
  font-size: var(--font-size-base);
  color: rgba(255, 255, 255, 0.65);
  line-height: 1.7;
  margin: 0;
}

.diplomas__hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-3);
  justify-content: center;
}

.diplomas__btn {
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

.diplomas__btn--primary {
  background: var(--color-main-blue);
  color: #fff;
  border-color: var(--color-main-blue);
}

.diplomas__btn--primary:hover {
  background: #1a5bbd;
  border-color: #1a5bbd;
}

.diplomas__btn--outline {
  background: transparent;
  color: #fff;
  border-color: rgba(255, 255, 255, 0.3);
}

.diplomas__btn--outline:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.5);
}

.diplomas__btn--lg {
  padding: 14px 32px;
  font-size: var(--font-size-lg);
}

/* ===========================
   STATS STRIP
   =========================== */
.diplomas__stats-strip {
  background: #fff;
  border-bottom: 1px solid var(--color-gray-blue);
  overflow-x: hidden;
}

.diplomas__stats-inner {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0;
}

.diplomas__stat {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-5) var(--space-5);
  border-right: 1px solid var(--color-gray-blue);
}

.diplomas__stat:last-child { border-right: none; }

.diplomas__stat-value {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-main-blue);
  white-space: nowrap;
  flex-shrink: 0;
}

.diplomas__stat-text { display: flex; flex-direction: column; gap: 1px; }
.diplomas__stat-label { font-size: var(--font-size-sm); font-weight: var(--font-weight-semibold); color: var(--color-black); line-height: 1.3; }
.diplomas__stat-note { font-size: var(--font-size-xs); color: var(--color-pale-black); }

/* ===========================
   LOADING
   =========================== */
.diplomas__loading-section {
  padding: 80px 0;
  background: #f8fafc;
}

.diplomas__loading-state {
  padding: 3rem;
  text-align: center;
  color: #94a3b8;
  background: #fff;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-gray-blue);
}

.spinner {
  width: 40px;
  height: 40px;
  margin: 0 auto 1rem;
  border: 3px solid var(--color-gray-blue);
  border-top-color: var(--color-main-blue);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ===========================
   EMPTY
   =========================== */
.diplomas__empty-section {
  padding: 80px 0;
  background: #f8fafc;
}

.diplomas__empty-state {
  padding: 3rem;
  text-align: center;
  background: #fff;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-gray-blue);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
}

.diplomas__empty-icon {
  font-size: 3rem;
  opacity: 0.5;
}

/* ===========================
   CARDS SECTION
   =========================== */
.diplomas__cards-section {
  padding: 80px 0;
  background: #fff;
  overflow-x: hidden;
}

.diplomas__cards-head { text-align: center; margin-bottom: var(--space-12); }

.diplomas__how-eyebrow {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--color-main-blue);
  margin-bottom: var(--space-2);
}

.diplomas__how-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  line-height: 1.2;
}

.diplomas__cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: var(--space-6);
}

/* ===========================
   TRACKER
   =========================== */
.diplomas__tracker-section {
  padding: 80px 0;
  background: #f8fafc;
  overflow-x: hidden;
}

.diplomas__tracker-head { text-align: center; margin-bottom: var(--space-8); }

.diplomas__tracker-card {
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  max-width: 700px;
  margin: 0 auto;
}

/* ===========================
   DASHBOARD PRO — Новые карточки дипломов
   =========================== */
.dashboard-pro {
  padding: 40px 0 80px;
  background: #f8fafc;
  overflow-x: hidden;
}

/* Info Banner */
.info-banner {
  display: flex;
  gap: var(--space-4);
  padding: var(--space-5);
  background: #e8f4fd;
  border: 1px solid #b8dff5;
  border-radius: var(--radius-lg);
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

/* Dashboard Layout */
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
  background: #f1f5f9;
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  color: var(--color-pale-black);
}

/* Diplomas Grid V */
.diplomas-grid-v {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--space-5);
}

/* Diploma Card V */
.diploma-card-v {
  position: relative;
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  overflow: hidden;
  transition: all 0.3s ease;
  animation: fadeInUp 0.5s ease forwards;
  opacity: 0;
  min-width: 0;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.diploma-card-v.expanded {
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  border-color: var(--color-main-blue);
}

.diploma-card-v.revoked {
  opacity: 0.7;
}

.card-v-accent {
  height: 4px;
  background: linear-gradient(90deg, var(--color-main-blue), #48b8c2);
}

.diploma-card-v.revoked .card-v-accent {
  background: #94a3b8;
}

/* Verified / Revoked Seals */
.verified-seal,
.revoked-seal {
  position: absolute;
  top: var(--space-3);
  right: var(--space-3);
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: var(--radius-full);
  font-size: 11px;
  font-weight: var(--font-weight-semibold);
  z-index: 1;
}

.verified-seal {
  background: #dcfce7;
  color: #166534;
}

.revoked-seal {
  background: #fee2e2;
  color: #991b1b;
}

/* Card Body */
.card-v-body {
  padding: var(--space-5);
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.year-tag-subtle {
  display: inline-flex;
  align-items: center;
  padding: 2px 10px;
  background: #f1f5f9;
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  color: var(--color-pale-black);
  align-self: flex-start;
}

.uni-icon {
  color: var(--color-main-blue);
  margin: 0 auto;
}

.card-v-body h3 {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  margin: 0;
  text-align: center;
}

.student-name {
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
  margin: 0;
  text-align: center;
}

.degree-badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  background: #dbeafe;
  color: #1e40af;
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  align-self: center;
}

.specialty-v {
  font-size: var(--font-size-sm);
  color: var(--color-black);
  margin: 0;
  text-align: center;
}

/* Diploma Number Row */
.diploma-number-row {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  background: #f8fafc;
  border-radius: var(--radius-base);
  font-size: var(--font-size-sm);
}

.icon-number {
  color: var(--color-main-blue);
}

.label {
  color: var(--color-pale-black);
}

.value.mono {
  font-family: monospace;
  color: var(--color-black);
  font-weight: var(--font-weight-semibold);
}

/* Stats Row */
.stats-row {
  display: flex;
  gap: var(--space-3);
}

.stat-item-small {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background: #f1f5f9;
  border-radius: var(--radius-full);
  font-size: 12px;
  color: var(--color-pale-black);
}

/* Status Pill */
.status-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  align-self: center;
}

.status-pill.active {
  background: #dcfce7;
  color: #166534;
}

.status-pill.revoked {
  background: #fee2e2;
  color: #991b1b;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-pill.active .status-dot {
  background: #22c55e;
}

.status-pill.revoked .status-dot {
  background: #ef4444;
}

/* Card Footer */
.card-v-footer {
  padding: 0 var(--space-5) var(--space-5);
}

.btn-share-v {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  padding: 12px 20px;
  background: var(--color-main-blue);
  color: #fff;
  border: none;
  border-radius: var(--radius-base);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: all var(--transition-base);
}

.btn-share-v:hover:not(.disabled) {
  background: #1a5bbd;
}

.btn-share-v.disabled {
  background: #94a3b8;
  cursor: not-allowed;
  opacity: 0.6;
}

/* QR Panel */
.qr-panel-v {
  padding: var(--space-5);
  border-top: 1px solid var(--color-gray-blue);
  background: #f8fafc;
}

/* QR Init State */
.qr-init-v {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-6);
}

.shield-icon {
  color: var(--color-main-blue);
}

.qr-ref {
  font-size: var(--font-size-sm);
  color: var(--color-black);
  margin: 0;
}

.qr-ref .mono {
  font-family: monospace;
  font-weight: var(--font-weight-semibold);
}

.text-muted {
  font-size: var(--font-size-xs);
  color: var(--color-pale-black);
  margin: 0;
}

.btn-generate-v {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: 10px 24px;
  background: var(--color-main-blue);
  color: #fff;
  border: none;
  border-radius: var(--radius-base);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: all var(--transition-base);
}

.btn-generate-v:hover {
  background: #1a5bbd;
}

/* QR Result */
.qr-result-v {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-4);
}

.qr-timer-v {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: 6px 14px;
  background: #fef3c7;
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  color: #92400e;
}

.countdown {
  font-family: monospace;
  font-weight: var(--font-weight-bold);
}

.qr-frame {
  position: relative;
  padding: var(--space-4);
  background: #fff;
  border: 2px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
}

.qr-frame-badge {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: 6px 14px;
  background: #f1f5f9;
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  color: var(--color-pale-black);
}

.qr-actions-v {
  display: flex;
  gap: var(--space-2);
  flex-wrap: wrap;
  justify-content: center;
}

.btn-action {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-base);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  color: var(--color-black);
  cursor: pointer;
  transition: all var(--transition-base);
}

.btn-action:hover {
  background: #f1f5f9;
}

/* Share Menu */
.share-wrapper {
  position: relative;
}

.share-menu {
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  margin-bottom: var(--space-2);
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-base);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 10;
  min-width: 140px;
  overflow: hidden;
}

.share-menu button {
  width: 100%;
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: 10px 14px;
  background: none;
  border: none;
  font-size: var(--font-size-xs);
  color: var(--color-black);
  cursor: pointer;
  transition: background var(--transition-base);
}

.share-menu button:hover {
  background: #f1f5f9;
}

.btn-rev-v {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  background: #fee2e2;
  border: 1px solid #fca5a5;
  border-radius: var(--radius-base);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  color: #991b1b;
  cursor: pointer;
  transition: all var(--transition-base);
}

.btn-rev-v:hover {
  background: #fecaca;
}

/* QR Revoked */
.qr-revoked-v {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-4);
  background: #fee2e2;
  border-radius: var(--radius-base);
  color: #991b1b;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
}

.btn-regen-v {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 14px;
  background: #fff;
  border: 1px solid #fca5a5;
  border-radius: var(--radius-full);
  font-size: 11px;
  font-weight: var(--font-weight-semibold);
  color: #991b1b;
  cursor: pointer;
  transition: all var(--transition-base);
}

.btn-regen-v:hover {
  background: #fee2e2;
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

.sidebar-icon-wrap {
  padding: 12px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.sidebar-icon-wrap svg {
  color: #fff;
}

.bg-green { background: #22c55e; }
.bg-blue { background: var(--color-main-blue); }
.bg-purple { background: #a855f7; }

.sidebar-link {
  color: var(--color-main-blue);
  text-decoration: none;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  transition: color var(--transition-base);
}

.sidebar-link:hover {
  color: #1a5bbd;
  text-decoration: underline;
}

/* ===========================
   Skeleton Loading
   =========================== */
.diploma-card-v.skeleton {
  pointer-events: none;
  animation: none;
  opacity: 1;
}

.skeleton-accent {
  height: 4px;
  background: linear-gradient(90deg, #e2e8f0, #cbd5e1, #e2e8f0);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.skeleton-body {
  padding: var(--space-5);
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.skeleton-line {
  height: 14px;
  background: linear-gradient(90deg, #e2e8f0, #cbd5e1, #e2e8f0);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  border-radius: var(--radius-base);
}

.skeleton-line.short { width: 30%; }
.skeleton-line.long { width: 80%; }

/* ===========================
   CTA
   =========================== */
.diplomas__cta {
  padding: 80px 0;
  background: #0d1f3c;
  text-align: center;
}

.diplomas__cta-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* ===========================
   CTA
   =========================== */
.diplomas__cta {
  padding: 80px 0;
  background: #0d1f3c;
  text-align: center;
}

.diplomas__cta-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* ===========================
   АДАПТИВ
   =========================== */
@media (max-width: 1100px) {
  .diplomas__stats-inner { grid-template-columns: repeat(2, 1fr); }
  .diplomas__stat:nth-child(2) { border-right: none; }
  .diplomas__stat:nth-child(1), .diplomas__stat:nth-child(2) { border-bottom: 1px solid var(--color-gray-blue); }
}

@media (max-width: 768px) {
  .diplomas__hero { padding: 44px 0 48px; }
  .diplomas__title { font-size: clamp(1.5rem, 6vw, 2.2rem); }

  .diplomas__stat {
    flex-direction: column;
    align-items: flex-start;
    gap: 2px;
    padding: var(--space-3) var(--space-4);
  }
  .diplomas__stat-value { font-size: var(--font-size-base); }
  .diplomas__stat-note { display: none; }

  .diplomas__cards-section, .diplomas__tracker-section { padding: 48px 0; }
  .diplomas__how-title { font-size: var(--font-size-2xl); }
  .diplomas__cards-head, .diplomas__tracker-head { margin-bottom: var(--space-8); }

  .diplomas__cards-grid {
    grid-template-columns: 1fr;
  }

  .diplomas__cta { padding: 48px 0; }
}

@media (max-width: 540px) {
  .diplomas__hero { padding: 32px 0 40px; }
  .diplomas__title { font-size: 1.5rem; line-height: 1.2; }

  .diplomas__stats-inner { grid-template-columns: 1fr 1fr; }
  .diplomas__stat {
    flex-direction: column;
    align-items: flex-start;
    padding: var(--space-3) var(--space-3);
    gap: 2px;
  }
  .diplomas__stat:nth-child(1), .diplomas__stat:nth-child(3) { border-right: 1px solid var(--color-gray-blue); }
  .diplomas__stat:nth-child(2), .diplomas__stat:nth-child(4) { border-right: none; }
  .diplomas__stat-value { font-size: var(--font-size-base); white-space: normal; }
  .diplomas__stat-label { font-size: 11px; }
  .diplomas__stat-note { display: none; }

  .diplomas__how-title { font-size: var(--font-size-xl); }
}

/* ===========================
   Анимации для переходов
   =========================== */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.2s ease;
}
.slide-up-enter-from {
  opacity: 0;
  transform: translateY(10px);
}
.slide-up-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* ===========================
   Fullscreen QR Modal
   =========================== */
.fullscreen-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(8px);
}

.fullscreen-content {
  background: #fff;
  border-radius: var(--radius-xl);
  padding: var(--space-8);
  max-width: 500px;
  width: 90%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-6);
  position: relative;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.fullscreen-close {
  position: absolute;
  top: var(--space-4);
  right: var(--space-4);
  background: none;
  border: none;
  cursor: pointer;
  color: var(--color-pale-black);
  transition: color var(--transition-base);
}

.fullscreen-close:hover {
  color: var(--color-black);
}

.fullscreen-header {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-2);
}

.fullscreen-header h3 {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  margin: 0;
}

.fullscreen-subtitle {
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
  margin: 0;
}

.fullscreen-qr-wrapper {
  padding: var(--space-4);
  background: #f8fafc;
  border-radius: var(--radius-base);
}

.fullscreen-info {
  text-align: center;
}

.fullscreen-diploma {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-black);
  margin: 0 0 var(--space-2);
}

.fullscreen-timer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
  margin: 0;
}

.fullscreen-actions {
  display: flex;
  gap: var(--space-3);
}

.btn-fullscreen {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: 10px 20px;
  background: var(--color-main-blue);
  color: #fff;
  border: none;
  border-radius: var(--radius-base);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: all var(--transition-base);
}

.btn-fullscreen:hover {
  background: #1a5bbd;
}

.btn-fullscreen-secondary {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: 10px 20px;
  background: #f1f5f9;
  color: var(--color-black);
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-base);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: all var(--transition-base);
}

.btn-fullscreen-secondary:hover {
  background: #e2e8f0;
}

/* ===========================
   Toast Notification
   =========================== */
.toast-notification {
  position: fixed;
  bottom: var(--space-6);
  right: var(--space-6);
  background: #10b981;
  color: #fff;
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-base);
  display: flex;
  align-items: center;
  gap: var(--space-3);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 2000;
  opacity: 0;
  transform: translateY(10px);
  transition: all 0.3s ease;
}

.toast-notification.show {
  opacity: 1;
  transform: translateY(0);
}

.toast-notification button {
  background: none;
  border: none;
  color: #fff;
  cursor: pointer;
  opacity: 0.7;
  transition: opacity var(--transition-base);
}

.toast-notification button:hover {
  opacity: 1;
}
</style>
