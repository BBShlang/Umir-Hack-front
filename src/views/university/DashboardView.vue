<template>
  <div class="dashboard-view">
    <AppHeader />
    <main class="main-content">
      <!-- ===== HERO ===== -->
      <section class="dashboard__hero">
        <div class="dashboard__hero-bg" aria-hidden="true">
          <div class="dashboard__hero-grid-lines" />
          <div class="dashboard__hero-glow dashboard__hero-glow--1" />
          <div class="dashboard__hero-glow dashboard__hero-glow--2" />
        </div>

        <div class="container dashboard__hero-inner">
          <div class="dashboard__hero-content">
            <h1 class="dashboard__title">
              Панель управления<br />
              <span class="dashboard__title-accent">Ваш ВУЗ</span>
            </h1>
            <p class="dashboard__subtitle">
              Управляйте реестром дипломов, загружайте новые данные и отслеживайте статус выпускников в реальном времени.
            </p>
          </div>
        </div>
      </section>

      <!-- ===== Основная рабочая зона ===== -->
      <section class="dashboard__workspace">
        <div class="container">
          <div class="dashboard-layout">
            <!-- Левая колонка: быстрые действия -->
            <section class="section-pro">
              <div class="section-header">
                <h2>Панель управления</h2>
                <span class="badge">Активен</span>
              </div>

              <div class="dashboard__steps">
                <!-- Card 1: Загрузить реестр -->
                <div class="dashboard__step" @click="$router.push('/university/upload')">
                  <div class="card-v-accent" />
                  <div class="verified-seal">
                    <Upload :size="14" />
                    <span>Импорт</span>
                  </div>
                  <div class="card-v-body">
                    <span class="year-tag-subtle">Шаг 01</span>
                    <Upload :size="40" class="card-icon" />
                    <h3>Загрузить реестр</h3>
                    <p class="card-desc">
                      Импортируйте CSV/Excel файл с данными выпускников. Хэши генерируются автоматически.
                    </p>
                    <div class="stats-row"></div>
                  </div>
                  <div class="card-v-footer">
                    <button class="btn-share-v">
                      <span>Перейти к загрузке</span>
                      <span class="arrow">→</span>
                    </button>
                  </div>
                </div>

                <!-- Card 2: Управление дипломами -->
                <div class="dashboard__step" @click="$router.push('/university/diplomas')">
                  <div class="card-v-accent card-v-accent--green" />
                  <div class="verified-seal verified-seal--green">
                    <FileText :size="14" />
                    <span>Реестр</span>
                  </div>
                  <div class="card-v-body">
                    <span class="year-tag-subtle">Шаг 02</span>
                    <FileText :size="40" class="card-icon" />
                    <h3>Управление дипломами</h3>
                    <p class="card-desc">
                      Просмотр, поиск и отзыв дипломов. Управление хэшами и статусами записей.
                    </p>
                    <div class="stats-row"></div>
                  </div>
                  <div class="card-v-footer">
                    <button class="btn-share-v">
                      <span>Открыть реестр</span>
                      <span class="arrow">→</span>
                    </button>
                  </div>
                </div>

                <!-- Card 3: Заявки на привязку -->
                <div class="dashboard__step" @click="$router.push('/university/claims')">
                  <div class="card-v-accent card-v-accent--purple" />
                  <div class="verified-seal verified-seal--purple">
                    <FileCheck :size="14" />
                    <span>Заявки</span>
                  </div>
                  <div class="card-v-body">
                    <span class="year-tag-subtle">Шаг 03</span>
                    <FileCheck :size="40" class="card-icon" />
                    <h3>Заявки на привязку</h3>
                    <p class="card-desc">
                      Студенты отправляют заявки на привязку дипломов. Подтвердите или отклоните.
                    </p>
                    <div class="stats-row"></div>
                  </div>
                  <div class="card-v-footer">
                    <button class="btn-share-v btn-share-v--purple">
                      <span>Открыть заявки</span>
                      <span class="arrow">→</span>
                    </button>
                  </div>
                </div>
              </div>
            </section>
          </div>
        </div>
      </section>
    </main>
    <AppFooter />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { Upload, FileText, FileCheck } from 'lucide-vue-next'
import AppHeader from '../../components/common/AppHeader.vue'
import AppFooter from '../../components/common/AppFooter.vue'

const stats = ref({ total: 0, active: 0, revoked: 0, pending: 0 })
const recentUploads = ref([])

onMounted(() => {
  setTimeout(() => {
    stats.value = { total: 1250, active: 1180, revoked: 45, pending: 25 }
    recentUploads.value = [
      { id: 1, date: '01.04.2026', fileName: 'registry_spring_2026.csv', count: 340, status: 'active' },
      { id: 2, date: '15.03.2026', fileName: 'registry_additional.xlsx', count: 89, status: 'active' },
      { id: 3, date: '01.02.2026', fileName: 'registry_winter.csv', count: 275, status: 'revoked' }
    ]
  }, 600)
})
</script>

<style scoped>
.dashboard-view { min-height: 100vh; }
.main-content { flex: 1; }

/* ===========================
   HERO
   =========================== */
.dashboard__hero {
  position: relative;
  background: #0d1f3c;
  padding: 72px 0 80px;
}

.dashboard__hero-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}

.dashboard__hero-grid-lines {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
                    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.dashboard__hero-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.dashboard__hero-glow--1 {
  width: 500px;
  height: 500px;
  top: -150px;
  right: -100px;
  background: rgba(38, 75, 130, 0.45);
}

.dashboard__hero-glow--2 {
  width: 380px;
  height: 380px;
  bottom: -120px;
  left: -60px;
  background: rgba(72, 184, 194, 0.2);
}

.dashboard__hero-inner {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.dashboard__hero-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: var(--space-6);
}

.dashboard__title {
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: var(--font-weight-bold);
  color: #fff;
  line-height: 1.1;
  letter-spacing: -0.02em;
}

.dashboard__title-accent {
  color: var(--color-sea-clear);
}

.dashboard__subtitle {
  max-width: 540px;
  font-size: var(--font-size-base);
  color: rgba(255, 255, 255, 0.65);
  line-height: 1.7;
  margin: 0;
}

.dashboard__hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-3);
  justify-content: center;
}

.dashboard__btn {
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

.dashboard__btn--primary {
  background: var(--color-main-blue);
  color: #fff;
  border-color: var(--color-main-blue);
}

.dashboard__btn--primary:hover {
  background: #1a5bbd;
  border-color: #1a5bbd;
}

.dashboard__btn--outline {
  background: transparent;
  color: #fff;
  border-color: rgba(255, 255, 255, 0.3);
}

.dashboard__btn--outline:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.5);
}

/* ===========================
   WORKSPACE / Dashboard Layout
   =========================== */
.dashboard__workspace {
  padding: 40px 0 80px;
  background: #f8fafc;
  overflow-x: hidden;
}

.dashboard-layout {
  display: flex;
  justify-content: center;
  align-items: center;
}

.section-pro {
  max-width: 1100px;
  width: 100%;
  background: transparent;
  border-radius: var(--radius-lg);
  padding: 0;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--space-6);
  padding: 0 var(--space-2);
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

/* ===========================
   КАРТОЧКИ — СИНЯЯ ОБВОДКА, БЕЛЫЙ ФОН, ПО ЦЕНТРУ
   =========================== */
.dashboard__steps {
  display: flex;
  justify-content: center;
  align-items: stretch;
  gap: var(--space-6);
  flex-wrap: wrap;
}

.dashboard__step {
  position: relative;
  flex: 1;
  min-width: 320px;
  max-width: 480px;
  background: #ffffff;
  border: 2px solid var(--color-main-blue);
  border-radius: var(--radius-lg);
  overflow: hidden;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.dashboard__step:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 40px rgba(38, 75, 130, 0.2);
  border-color: #1d4ed8;
}

/* Скрываем верхнюю полоску для чистого вида */
.card-v-accent { display: none; }

/* Бейдж-печать */
.verified-seal {
  position: absolute;
  top: 16px;
  right: 16px;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: var(--radius-full);
  font-size: 12px;
  font-weight: var(--font-weight-semibold);
  background: rgba(38, 75, 130, 0.1);
  color: var(--color-main-blue);
  backdrop-filter: blur(6px);
  border: 1px solid rgba(38, 75, 130, 0.2);
  z-index: 1;
}

.verified-seal svg { flex-shrink: 0; color: var(--color-main-blue); }
.verified-seal--green {
  background: rgba(34, 197, 94, 0.1);
  color: #16a34a;
  border-color: rgba(34, 197, 94, 0.2);
}
.verified-seal--green svg { color: #16a34a; }

.verified-seal--purple {
  background: rgba(168, 85, 247, 0.1);
  color: #a855f7;
  border-color: rgba(168, 85, 247, 0.2);
}
.verified-seal--purple svg { color: #a855f7; }

/* Тело карточки */
.card-v-body {
  padding: 36px 32px 24px;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 18px;
}

.year-tag-subtle {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  background: rgba(38, 75, 130, 0.08);
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  color: var(--color-main-blue);
  border: 1px solid rgba(38, 75, 130, 0.15);
  align-self: flex-start;
}

.card-icon {
  color: var(--color-main-blue);
  margin: 0 auto 4px;
  width: 40px;
  height: 40px;
  transition: transform 0.2s ease;
}

.dashboard__step:hover .card-icon {
  transform: scale(1.05);
}

.card-v-body h3 {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  margin: 0;
  text-align: center;
}

.card-desc {
  font-size: var(--font-size-sm);
  color: #4a5568;
  margin: 0;
  text-align: center;
  line-height: 1.6;
  flex-grow: 1;
}

.stats-row { display: none; }

/* Футер карточки */
.card-v-footer {
  padding: 0 32px 32px;
}

.btn-share-v {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px 20px;
  background: var(--color-main-blue);
  color: #fff;
  border: none;
  border-radius: var(--radius-base);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-bold);
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-share-v .arrow { transition: transform 0.2s ease; }

.btn-share-v:hover {
  background: #1d4ed8;
  transform: translateY(-2px);
}

.btn-share-v:hover .arrow { transform: translateX(4px); }

.btn-share-v--purple {
  background: #a855f7;
}

.btn-share-v--purple:hover {
  background: #9333ea;
}

/* ===========================
   АДАПТИВ
   =========================== */
@media (max-width: 768px) {
  .dashboard__hero { padding: 44px 0 48px; }
  .dashboard__title { font-size: clamp(1.5rem, 6vw, 2.2rem); }

  .dashboard__steps {
    flex-direction: column;
    align-items: center;
    gap: var(--space-4);
  }
  
  .dashboard__step {
    min-width: auto;
    width: 100%;
    max-width: 100%;
  }
  
  .card-v-body { padding: 24px; }
  .card-v-footer { padding: 0 24px 24px; }
}

@media (max-width: 540px) {
  .dashboard__hero { padding: 32px 0 40px; }
  .dashboard__title { font-size: 1.5rem; line-height: 1.2; }
  .section-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}
</style>