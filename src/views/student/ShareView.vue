<template>
  <div class="share-view">
    <AppHeader />
    <main class="main-content">
      <!-- ===== HERO ===== -->
      <section class="share__hero">
        <div class="share__hero-bg" aria-hidden="true">
          <div class="share__hero-grid-lines" />
          <div class="share__hero-glow share__hero-glow--1" />
          <div class="share__hero-glow share__hero-glow--2" />
        </div>

        <div class="container share__hero-inner">
          <div class="share__hero-content">
            <h1 class="share__title">
              Поделиться дипломом<br />
              <span class="share__title-accent">Защищённая ссылка</span>
            </h1>
            <p class="share__subtitle">
              Создайте временную ссылку для работодателя с ограниченным сроком действия и контролем доступа.
            </p>
            <div class="share__hero-actions">
              <router-link to="/student/diplomas" class="share__btn share__btn--outline">К моим дипломам</router-link>
              <router-link to="/student/dashboard" class="share__btn share__btn--outline">На дашборд</router-link>
            </div>
          </div>
        </div>
      </section>

      <!-- ===== Статусная полоса — преимущества ===== -->
      <div class="share__stats-strip">
        <div class="container share__stats-inner">
          <div class="share__stat">
            <div class="share__stat-icon share__stat-icon--blue">🔒</div>
            <div class="share__stat-text">
              <span class="share__stat-label">Безопасность</span>
              <span class="share__stat-note">Ограниченный доступ</span>
            </div>
          </div>
          <div class="share__stat">
            <div class="share__stat-icon share__stat-icon--green">⏱️</div>
            <div class="share__stat-text">
              <span class="share__stat-label">Срок действия</span>
              <span class="share__stat-note">Настраивается</span>
            </div>
          </div>
          <div class="share__stat">
            <div class="share__stat-icon share__stat-icon--orange">📊</div>
            <div class="share__stat-text">
              <span class="share__stat-label">Отслеживание</span>
              <span class="share__stat-note">Кто и когда проверял</span>
            </div>
          </div>
          <div class="share__stat">
            <div class="share__stat-icon share__stat-icon--sea">🚫</div>
            <div class="share__stat-text">
              <span class="share__stat-label">Отзыв</span>
              <span class="share__stat-note">В любой момент</span>
            </div>
          </div>
        </div>
      </div>

      <!-- ===== Контент ===== -->
      <section v-if="!diploma" class="share__loading-section">
        <div class="container">
          <div class="share__loading-state">
            <div class="spinner" aria-hidden="true"></div>
            <p>Загрузка данных диплома...</p>
          </div>
        </div>
      </section>

      <section v-else class="share__content-section">
        <div class="container">
          <div class="share__content-head">
            <p class="share__how-eyebrow">Документ</p>
            <h2 class="share__how-title">Информация о дипломе</h2>
          </div>

          <div class="share__diploma-brief">
            <h3>{{ diploma.specialty }}</h3>
            <p>{{ diploma.degree }} • {{ diploma.university }}</p>
            <p class="serial">Серийный номер: <code>{{ diploma.serialNumber }}</code></p>
          </div>

          <div class="share__generator-card">
            <ShareLinkGenerator :diploma="diploma" :default-ttl="7" />
          </div>

          <div class="share__active-links" v-if="activeLinks.length">
            <div class="share__links-head">
              <p class="share__how-eyebrow">Управление</p>
              <h2 class="share__how-title">Активные ссылки</h2>
            </div>
            <div class="share__links-list">
              <div v-for="link in activeLinks" :key="link.token" class="share__link-item">
                <div class="share__link-info">
                  <span class="share__link-url">{{ link.url }}</span>
                  <span class="share__link-expires">Истекает: {{ link.expiresAt }}</span>
                </div>
                <div class="share__link-actions">
                  <button class="share__btn share__btn--sm" @click="copyLink(link.url)">Копировать</button>
                  <button class="share__btn share__btn--sm share__btn--danger" @click="revokeLink(link.token)">Отозвать</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- ===== CTA ===== -->
      <section class="share__cta">
        <div class="container share__cta-inner">
          <h2 class="share__how-title" style="margin-bottom: var(--space-3); color: #fff;">Отправляете резюме?</h2>
          <p class="share__subtitle" style="margin: 0 auto var(--space-6); max-width: 600px;">
            Добавьте ссылку на диплом в ваше резюме для подтверждения квалификации
          </p>
          <router-link to="/student/diplomas" class="share__btn share__btn--primary share__btn--lg">
            Все мои дипломы
          </router-link>
        </div>
      </section>
    </main>
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import AppFooter from '../../components/common/AppFooter.vue'
import ShareLinkGenerator from '../../components/student/ShareLinkGenerator.vue'

const props = defineProps({
  id: { type: [String, Number], default: null }
})

const diploma = ref(null)
const activeLinks = ref([])

onMounted(() => {
  setTimeout(() => {
    diploma.value = {
      id: props.id || 1,
      serialNumber: 'DIP-0000001234',
      specialty: 'Информатика и вычислительная техника',
      degree: 'Бакалавр',
      university: 'МГТУ им. Баумана',
      issueDate: '2025-06-15',
      status: 'active',
      hash: 'a1b2c3d4e5f6789012345678901234567890abcdef1234567890abcdef123456'
    }

    activeLinks.value = [
      {
        token: 'abc123',
        url: `${window.location.origin}/verify/share/abc123`,
        expiresAt: '05.04.2026, 14:30'
      }
    ]
  }, 400)
})

function copyLink(url) {
  navigator.clipboard.writeText(url)
  alert('Ссылка скопирована')
}

function revokeLink(token) {
  if (confirm('Отозвать ссылку?')) {
    activeLinks.value = activeLinks.value.filter(l => l.token !== token)
  }
}
</script>

<style scoped>
.share-view { min-height: 100vh; }

.main-content { flex: 1; }

/* ===========================
   HERO
   =========================== */
.share__hero {
  position: relative;
  background: #0d1f3c;
  padding: 72px 0 80px;
}

.share__hero-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}

.share__hero-grid-lines {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(
      rgba(255, 255, 255, 0.03) 1px,
      transparent 1px
    ),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.share__hero-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.share__hero-glow--1 {
  width: 500px;
  height: 500px;
  top: -150px;
  right: -100px;
  background: rgba(38, 75, 130, 0.45);
}

.share__hero-glow--2 {
  width: 380px;
  height: 380px;
  bottom: -120px;
  left: -60px;
  background: rgba(72, 184, 194, 0.2);
}

.share__hero-inner {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.share__hero-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: var(--space-6);
}

.share__title {
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: var(--font-weight-bold);
  color: #fff;
  line-height: 1.1;
  letter-spacing: -0.02em;
}

.share__title-accent {
  color: var(--color-sea-clear);
}

.share__subtitle {
  max-width: 540px;
  font-size: var(--font-size-base);
  color: rgba(255, 255, 255, 0.65);
  line-height: 1.7;
  margin: 0;
}

.share__hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-3);
  justify-content: center;
}

.share__btn {
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

.share__btn--primary {
  background: var(--color-main-blue);
  color: #fff;
  border-color: var(--color-main-blue);
}

.share__btn--primary:hover {
  background: #1a5bbd;
  border-color: #1a5bbd;
}

.share__btn--outline {
  background: transparent;
  color: #fff;
  border-color: rgba(255, 255, 255, 0.3);
}

.share__btn--outline:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.5);
}

.share__btn--sm {
  padding: 0.375rem 0.75rem;
  font-size: var(--font-size-xs);
  border: 1px solid var(--color-gray-blue);
  background: #fff;
}

.share__btn--danger {
  background: #dc2626;
  color: #fff;
  border-color: #dc2626;
}

.share__btn--danger:hover {
  background: #b91c1c;
}

.share__btn--lg {
  padding: 14px 32px;
  font-size: var(--font-size-lg);
}

/* ===========================
   STATS STRIP
   =========================== */
.share__stats-strip {
  background: #fff;
  border-bottom: 1px solid var(--color-gray-blue);
  overflow-x: hidden;
}

.share__stats-inner {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0;
}

.share__stat {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-5) var(--space-5);
  border-right: 1px solid var(--color-gray-blue);
}

.share__stat:last-child { border-right: none; }

.share__stat-icon {
  width: 44px; height: 44px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 1.25rem;
}

.share__stat-icon--blue { background: var(--color-pale-blue); color: var(--color-main-blue); }
.share__stat-icon--green { background: #e6f5ee; color: var(--color-green); }
.share__stat-icon--orange { background: #fff0e4; color: var(--color-orange); }
.share__stat-icon--sea { background: #e4f4f5; color: var(--color-sea-dark); }

.share__stat-text { display: flex; flex-direction: column; gap: 1px; }
.share__stat-label { font-size: var(--font-size-sm); font-weight: var(--font-weight-semibold); color: var(--color-black); line-height: 1.3; }
.share__stat-note { font-size: var(--font-size-xs); color: var(--color-pale-black); }

/* ===========================
   LOADING
   =========================== */
.share__loading-section {
  padding: 80px 0;
  background: #f8fafc;
}

.share__loading-state {
  padding: 3rem;
  text-align: center;
  color: #94a3b8;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--color-gray-blue);
  border-top-color: var(--color-main-blue);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ===========================
   CONTENT SECTION
   =========================== */
.share__content-section {
  padding: 80px 0;
  background: #fff;
  overflow-x: hidden;
}

.share__content-head { text-align: center; margin-bottom: var(--space-8); }

.share__how-eyebrow {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--color-main-blue);
  margin-bottom: var(--space-2);
}

.share__how-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  line-height: 1.2;
}

.share__diploma-brief {
  padding: var(--space-5);
  background: #fff;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-gray-blue);
  margin-bottom: var(--space-6);
  max-width: 700px;
  margin-left: auto;
  margin-right: auto;
}

.share__diploma-brief h3 {
  margin: 0 0 0.25rem;
  font-size: var(--font-size-lg);
}

.share__diploma-brief p {
  margin: 0.25rem 0;
  color: #64748b;
  font-size: var(--font-size-sm);
}

.serial code {
  font-size: var(--font-size-xs);
  background: #f1f5f9;
  padding: 0.125rem 0.375rem;
  border-radius: var(--radius-sm);
}

.share__generator-card {
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  max-width: 700px;
  margin: 0 auto var(--space-8);
}

/* ===========================
   ACTIVE LINKS
   =========================== */
.share__active-links {
  max-width: 700px;
  margin: 0 auto;
}

.share__links-head { text-align: center; margin-bottom: var(--space-8); }

.share__links-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.share__link-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4);
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  flex-wrap: wrap;
  gap: var(--space-3);
  transition: border-color var(--transition-base);
}

.share__link-item:hover {
  border-color: var(--color-main-blue);
}

.share__link-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.share__link-url {
  font-size: var(--font-size-sm);
  word-break: break-all;
}

.share__link-expires {
  font-size: var(--font-size-xs);
  color: #94a3b8;
}

.share__link-actions {
  display: flex;
  gap: var(--space-2);
}

/* ===========================
   CTA
   =========================== */
.share__cta {
  padding: 80px 0;
  background: #0d1f3c;
  text-align: center;
}

.share__cta-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* ===========================
   АДАПТИВ
   =========================== */
@media (max-width: 1100px) {
  .share__stats-inner { grid-template-columns: repeat(2, 1fr); }
  .share__stat:nth-child(2) { border-right: none; }
  .share__stat:nth-child(1), .share__stat:nth-child(2) { border-bottom: 1px solid var(--color-gray-blue); }
}

@media (max-width: 768px) {
  .share__hero { padding: 44px 0 48px; }
  .share__title { font-size: clamp(1.5rem, 6vw, 2.2rem); }

  .share__stat {
    flex-direction: column;
    align-items: flex-start;
    gap: 2px;
    padding: var(--space-3) var(--space-4);
  }
  .share__stat-label { font-size: var(--font-size-xs); }
  .share__stat-note { display: none; }

  .share__content-section { padding: 48px 0; }
  .share__how-title { font-size: var(--font-size-2xl); }
  .share__content-head { margin-bottom: var(--space-8); }

  .share__link-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .share__cta { padding: 48px 0; }
}

@media (max-width: 540px) {
  .share__hero { padding: 32px 0 40px; }
  .share__title { font-size: 1.5rem; line-height: 1.2; }

  .share__stats-inner { grid-template-columns: 1fr 1fr; }
  .share__stat {
    flex-direction: column;
    align-items: flex-start;
    padding: var(--space-3) var(--space-3);
    gap: 2px;
  }
  .share__stat:nth-child(1), .share__stat:nth-child(3) { border-right: 1px solid var(--color-gray-blue); }
  .share__stat:nth-child(2), .share__stat:nth-child(4) { border-right: none; }
  .share__stat-label { font-size: 11px; }
  .share__stat-note { display: none; }

  .share__how-title { font-size: var(--font-size-xl); }
}
</style>
