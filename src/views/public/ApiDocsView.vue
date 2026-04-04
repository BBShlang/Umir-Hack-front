<template>
  <div class="api-docs">
    <AppHeader />

    <main class="main-content">
      <!-- ===== HERO ===== -->
      <section class="api-docs__hero">
        <div class="api-docs__hero-bg" aria-hidden="true">
          <div class="api-docs__hero-grid-lines" />
          <div class="api-docs__hero-glow api-docs__hero-glow--1" />
          <div class="api-docs__hero-glow api-docs__hero-glow--2" />
        </div>

        <div class="container api-docs__hero-inner">
          <div class="api-docs__hero-content">
            <h1 class="api-docs__hero-title">
              API Документация<br />
              <span class="api-docs__hero-title-accent">Интеграция с платформой</span>
            </h1>

            <p class="api-docs__hero-subtitle">
              Полное руководство по интеграции с ДиплоРеестр. REST API, аутентификация, 
              проверка подлинности и массовые запросы.
            </p>

            <div class="api-docs__hero-actions">
              <router-link to="/login" class="api-docs__btn api-docs__btn--primary">Войти</router-link>
              <a href="#authentication" class="api-docs__btn api-docs__btn--outline">Начать изучение</a>
            </div>

            <div class="api-docs__quick-access">
              <span class="api-docs__quick-access-label">Быстрый доступ:</span>
              <div class="api-docs__quick-access-list">
                <button class="api-docs__quick-btn" @click="scrollToSection('authentication')">Аутентификация</button>
                <button class="api-docs__quick-btn" @click="scrollToSection('verify')">Проверка диплома</button>
                <button class="api-docs__quick-btn" @click="scrollToSection('bulk')">Массовая проверка</button>
                <button class="api-docs__quick-btn" @click="scrollToSection('errors')">Коды ошибок</button>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- ===== Статусная полоса ===== -->
      <div class="api-docs__stats-strip">
        <div class="container api-docs__stats-inner">
          <div class="api-docs__stat">
            <span class="api-docs__stat-value">REST</span>
            <div class="api-docs__stat-text">
              <span class="api-docs__stat-label">Архитектура</span>
              <span class="api-docs__stat-note">JSON формат</span>
            </div>
          </div>
          <div class="api-docs__stat">
            <span class="api-docs__stat-value">Bearer</span>
            <div class="api-docs__stat-text">
              <span class="api-docs__stat-label">Аутентификация</span>
              <span class="api-docs__stat-note">Токен доступа</span>
            </div>
          </div>
          <div class="api-docs__stat">
            <span class="api-docs__stat-value">1000/день</span>
            <div class="api-docs__stat-text">
              <span class="api-docs__stat-label">Rate Limit</span>
              <span class="api-docs__stat-note">Стандартный план</span>
            </div>
          </div>
          <div class="api-docs__stat">
            <span class="api-docs__stat-value">SHA-256</span>
            <div class="api-docs__stat-text">
              <span class="api-docs__stat-label">Хеширование</span>
              <span class="api-docs__stat-note">Защита данных</span>
            </div>
          </div>
        </div>
      </div>

      <!-- ===== Документация ===== -->
      <section class="api-docs__content-section" id="authentication">
        <div class="container">
          <div class="api-docs__section-header">
            <p class="api-docs__section-eyebrow">Начало работы</p>
            <h2 class="api-docs__section-title">Аутентификация</h2>
          </div>

          <div class="api-docs__content-card">
            <p class="api-docs__text">
              Все запросы к API требуют авторизации через Bearer-токен. 
              Передавайте токен в заголовке каждого запроса:
            </p>
            <div class="api-docs__code-block">
              <div class="api-docs__code-header">
                <div class="api-docs__code-dots">
                  <span class="api-docs__code-dot api-docs__code-dot--red" />
                  <span class="api-docs__code-dot api-docs__code-dot--yellow" />
                  <span class="api-docs__code-dot api-docs__code-dot--green" />
                </div>
                <span class="api-docs__code-label">Заголовок запроса</span>
              </div>
              <pre class="api-docs__code-body"><code>Authorization: Bearer &lt;your-api-key&gt;</code></pre>
            </div>
          </div>
        </div>
      </section>

      <!-- ===== Проверка диплома ===== -->
      <section class="api-docs__content-section api-docs__content-section--alt" id="verify">
        <div class="container">
          <div class="api-docs__section-header">
            <p class="api-docs__section-eyebrow">Основной endpoint</p>
            <h2 class="api-docs__section-title">Проверка диплома</h2>
          </div>

          <div class="api-docs__endpoint">
            <div class="api-docs__method-badge">GET</div>
            <code class="api-docs__endpoint-path">/api/verify</code>
          </div>

          <div class="api-docs__content-card">
            <h3 class="api-docs__card-title">Параметры запроса</h3>
            <table class="api-docs__table">
              <thead>
                <tr>
                  <th>Параметр</th>
                  <th>Тип</th>
                  <th>Описание</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td><code>serial</code></td>
                  <td>string</td>
                  <td>Серийный номер диплома</td>
                </tr>
                <tr>
                  <td><code>hash</code></td>
                  <td>string</td>
                  <td>SHA-256 хеш диплома</td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="api-docs__content-card api-docs__content-card--response">
            <h3 class="api-docs__card-title">Пример ответа</h3>
            <div class="api-docs__code-block">
              <div class="api-docs__code-header">
                <div class="api-docs__code-dots">
                  <span class="api-docs__code-dot api-docs__code-dot--red" />
                  <span class="api-docs__code-dot api-docs__code-dot--yellow" />
                  <span class="api-docs__code-dot api-docs__code-dot--green" />
                </div>
                <span class="api-docs__code-label">200 OK</span>
              </div>
              <pre class="api-docs__code-body"><code>{
  "valid": true,
  "status": "active",
  "university": "МГТУ им. Баумана",
  "specialty": "Информатика",
  "issueDate": "2025-06-15",
  "hash": "a1b2c3..."
}</code></pre>
            </div>
          </div>
        </div>
      </section>

      <!-- ===== Массовая проверка ===== -->
      <section class="api-docs__content-section" id="bulk">
        <div class="container">
          <div class="api-docs__section-header">
            <p class="api-docs__section-eyebrow">Пакетная обработка</p>
            <h2 class="api-docs__section-title">Массовая проверка</h2>
          </div>

          <div class="api-docs__endpoint">
            <div class="api-docs__method-badge api-docs__method-badge--post">POST</div>
            <code class="api-docs__endpoint-path">/api/verify/bulk</code>
          </div>

          <div class="api-docs__content-card">
            <p class="api-docs__text">
              Принимает массив серийных номеров, возвращает статус каждого диплома.
            </p>
            <div class="api-docs__code-block">
              <div class="api-docs__code-header">
                <div class="api-docs__code-dots">
                  <span class="api-docs__code-dot api-docs__code-dot--red" />
                  <span class="api-docs__code-dot api-docs__code-dot--yellow" />
                  <span class="api-docs__code-dot api-docs__code-dot--green" />
                </div>
                <span class="api-docs__code-label">Request Body</span>
              </div>
              <pre class="api-docs__code-body"><code>{
  "serials": ["DIP-001", "DIP-002", "DIP-003"]
}</code></pre>
            </div>
          </div>
        </div>
      </section>

      <!-- ===== Rate Limit ===== -->
      <section class="api-docs__content-section api-docs__content-section--alt">
        <div class="container">
          <div class="api-docs__section-header">
            <p class="api-docs__section-eyebrow">Ограничения</p>
            <h2 class="api-docs__section-title">Rate Limit</h2>
          </div>

          <div class="api-docs__content-card">
            <div class="api-docs__limit-card">
              <div class="api-docs__limit-icon">⏱️</div>
              <div class="api-docs__limit-content">
                <h3 class="api-docs__limit-title">1000 запросов в день</h3>
                <p class="api-docs__limit-desc">
                  Стандартный план включает до 1000 запросов ежедневно. 
                  При превышении лимита возвращается статус <code>429 Too Many Requests</code>.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- ===== Коды ошибок ===== -->
      <section class="api-docs__content-section" id="errors">
        <div class="container">
          <div class="api-docs__section-header">
            <p class="api-docs__section-eyebrow">Справочник</p>
            <h2 class="api-docs__section-title">Коды ошибок</h2>
          </div>

          <div class="api-docs__content-card">
            <table class="api-docs__table">
              <thead>
                <tr>
                  <th>Код</th>
                  <th>Описание</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td><span class="api-docs__error-code api-docs__error-code--400">400</span></td>
                  <td>Неверные параметры запроса</td>
                </tr>
                <tr>
                  <td><span class="api-docs__error-code api-docs__error-code--401">401</span></td>
                  <td>Не авторизован</td>
                </tr>
                <tr>
                  <td><span class="api-docs__error-code api-docs__error-code--403">403</span></td>
                  <td>Доступ запрещён</td>
                </tr>
                <tr>
                  <td><span class="api-docs__error-code api-docs__error-code--404">404</span></td>
                  <td>Диплом не найден</td>
                </tr>
                <tr>
                  <td><span class="api-docs__error-code api-docs__error-code--429">429</span></td>
                  <td>Превышен лимит запросов</td>
                </tr>
                <tr>
                  <td><span class="api-docs__error-code api-docs__error-code--500">500</span></td>
                  <td>Внутренняя ошибка сервера</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </section>

      <!-- ===== CTA ===== -->
      <section class="api-docs__cta">
        <div class="container api-docs__cta-inner">
          <h2 class="api-docs__cta-title">Готовы начать интеграцию?</h2>
          <p class="api-docs__cta-desc">
            Получите API-ключ и начните проверку дипломов через нашу платформу
          </p>
          <router-link to="/register" class="api-docs__btn api-docs__btn--primary api-docs__btn--lg">
            Получить доступ
          </router-link>
        </div>
      </section>
    </main>

    <AppFooter />
  </div>
</template>

<script setup>
import AppHeader from '../../components/common/AppHeader.vue'
import AppFooter from '../../components/common/AppFooter.vue'

const scrollToSection = (id) => {
  const element = document.getElementById(id)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}
</script>

<style scoped>
.api-docs { min-height: 100vh; }

/* ===========================
   HERO
   =========================== */
.api-docs__hero {
  position: relative;
  background: #0d1f3c;
  padding: 72px 0 80px;
}

.api-docs__hero-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}

.api-docs__hero-grid-lines {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(
      rgba(255, 255, 255, 0.03) 1px,
      transparent 1px
    ),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.api-docs__hero-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.api-docs__hero-glow--1 {
  width: 500px;
  height: 500px;
  top: -150px;
  right: -100px;
  background: rgba(38, 75, 130, 0.45);
}

.api-docs__hero-glow--2 {
  width: 380px;
  height: 380px;
  bottom: -120px;
  left: -60px;
  background: rgba(72, 184, 194, 0.2);
}

.api-docs__hero-inner {
  position: relative;
  z-index: 1;
}

.api-docs__hero-content {
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
  max-width: 700px;
}

.api-docs__hero-title {
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: var(--font-weight-bold);
  color: #fff;
  line-height: 1.1;
  letter-spacing: -0.02em;
}

.api-docs__hero-title-accent {
  color: var(--color-sea-clear);
}

.api-docs__hero-subtitle {
  max-width: 540px;
  font-size: var(--font-size-base);
  color: rgba(255, 255, 255, 0.65);
  line-height: 1.7;
}

.api-docs__hero-actions {
  display: flex;
  gap: var(--space-3);
  flex-wrap: wrap;
}

.api-docs__btn {
  padding: 12px 24px;
  border-radius: var(--radius-base);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  text-decoration: none;
  transition: all var(--transition-fast);
  cursor: pointer;
  border: none;
  font-family: var(--font-family);
}

.api-docs__btn--primary {
  background: var(--color-main-blue);
  color: #fff;
}

.api-docs__btn--primary:hover {
  background: var(--color-main-blue-dark);
  transform: translateY(-1px);
}

.api-docs__btn--outline {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: #fff;
}

.api-docs__btn--outline:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.5);
}

.api-docs__btn--lg {
  padding: 14px 32px;
  font-size: var(--font-size-lg);
}

.api-docs__quick-access {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--space-2);
  max-width: 640px;
}

.api-docs__quick-access-label {
  font-size: var(--font-size-xs);
  color: rgba(255, 255, 255, 0.4);
  white-space: nowrap;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.api-docs__quick-access-list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-2);
}

.api-docs__quick-btn {
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

.api-docs__quick-btn:hover {
  background: rgba(255, 255, 255, 0.14);
  color: #fff;
}

/* ===========================
   STATS STRIP
   =========================== */
.api-docs__stats-strip {
  background: #fff;
  border-bottom: 1px solid var(--color-gray-blue);
  overflow-x: hidden;
}

.api-docs__stats-inner {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0;
}

.api-docs__stat {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-5) var(--space-5);
  border-right: 1px solid var(--color-gray-blue);
}

.api-docs__stat:last-child { border-right: none; }

.api-docs__stat-value {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-main-blue);
  white-space: nowrap;
  flex-shrink: 0;
}

.api-docs__stat-text { display: flex; flex-direction: column; gap: 1px; }
.api-docs__stat-label { font-size: var(--font-size-sm); font-weight: var(--font-weight-semibold); color: var(--color-black); line-height: 1.3; }
.api-docs__stat-note { font-size: var(--font-size-xs); color: var(--color-pale-black); }

/* ===========================
   CONTENT SECTIONS
   =========================== */
.api-docs__content-section {
  padding: 80px 0;
  background: #fff;
}

.api-docs__content-section--alt {
  background: #f8fafc;
}

.api-docs__section-header {
  text-align: center;
  margin-bottom: var(--space-10);
}

.api-docs__section-eyebrow {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--color-main-blue);
  margin-bottom: var(--space-2);
}

.api-docs__section-title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  line-height: 1.2;
}

.api-docs__content-card {
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  margin-bottom: var(--space-6);
  transition: box-shadow var(--transition-base);
}

.api-docs__content-card:hover {
  box-shadow: var(--shadow-md);
}

.api-docs__content-card--response {
  border-left: 3px solid var(--color-green);
}

.api-docs__card-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  margin: 0 0 var(--space-4);
}

.api-docs__text {
  color: var(--color-pale-black);
  line-height: 1.7;
  margin-bottom: var(--space-4);
}

/* ===========================
   CODE BLOCKS
   =========================== */
.api-docs__code-block {
  border-radius: var(--radius-lg);
  overflow: hidden;
  border: 1px solid var(--color-gray-blue);
}

.api-docs__code-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  background: #f1f5f9;
  border-bottom: 1px solid var(--color-gray-blue);
}

.api-docs__code-dots {
  display: flex;
  gap: 6px;
}

.api-docs__code-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}

.api-docs__code-dot--red { background: #ff5f57; }
.api-docs__code-dot--yellow { background: #ffbd2e; }
.api-docs__code-dot--green { background: #28c840; }

.api-docs__code-label {
  margin-left: auto;
  font-size: 11px;
  color: var(--color-pale-black);
  font-family: 'Consolas', 'Monaco', monospace;
}

.api-docs__code-body {
  background: #1e293b;
  color: #e2e8f0;
  padding: var(--space-4);
  margin: 0;
  font-size: 0.875rem;
  line-height: 1.5;
  overflow-x: auto;
  font-family: 'Consolas', 'Monaco', monospace;
}

.api-docs__code-body code {
  font-family: inherit;
}

/* ===========================
   ENDPOINT
   =========================== */
.api-docs__endpoint {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin-bottom: var(--space-6);
  padding: var(--space-4);
  background: #f8fafc;
  border-radius: var(--radius-md);
  border: 1px solid var(--color-gray-blue);
}

.api-docs__method-badge {
  padding: 4px 12px;
  background: var(--color-main-blue);
  color: #fff;
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-bold);
  border-radius: var(--radius-sm);
  letter-spacing: 0.05em;
}

.api-docs__method-badge--post {
  background: var(--color-green);
}

.api-docs__endpoint-path {
  font-size: var(--font-size-lg);
  color: var(--color-black);
  font-family: 'Consolas', 'Monaco', monospace;
}

/* ===========================
   TABLE
   =========================== */
.api-docs__table {
  width: 100%;
  border-collapse: collapse;
  margin: 0;
  font-size: var(--font-size-sm);
}

.api-docs__table th,
.api-docs__table td {
  padding: 0.75rem 1rem;
  text-align: left;
  border-bottom: 1px solid var(--color-gray-blue);
}

.api-docs__table th {
  background: #f8fafc;
  font-weight: var(--font-weight-semibold);
  color: var(--color-black);
}

.api-docs__table tr:last-child td {
  border-bottom: none;
}

.api-docs__table code {
  background: #f1f5f9;
  padding: 0.125rem 0.375rem;
  border-radius: 4px;
  font-size: 0.8125rem;
  color: #0f172a;
  font-family: 'Consolas', 'Monaco', monospace;
}

/* ===========================
   ERROR CODES
   =========================== */
.api-docs__error-code {
  display: inline-block;
  padding: 2px 8px;
  border-radius: var(--radius-sm);
  font-weight: var(--font-weight-semibold);
  font-family: 'Consolas', 'Monaco', monospace;
}

.api-docs__error-code--400 { background: #fef3c7; color: #92400e; }
.api-docs__error-code--401 { background: #fee2e2; color: #991b1b; }
.api-docs__error-code--403 { background: #fee2e2; color: #991b1b; }
.api-docs__error-code--404 { background: #fee2e2; color: #991b1b; }
.api-docs__error-code--429 { background: #ffedd5; color: #9a3412; }
.api-docs__error-code--500 { background: #fee2e2; color: #991b1b; }

/* ===========================
   LIMIT CARD
   =========================== */
.api-docs__limit-card {
  display: flex;
  align-items: flex-start;
  gap: var(--space-4);
  padding: var(--space-4);
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border-radius: var(--radius-lg);
  border: 1px solid #f59e0b;
}

.api-docs__limit-icon {
  font-size: 2rem;
  flex-shrink: 0;
}

.api-docs__limit-content {
  flex: 1;
}

.api-docs__limit-title {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: #78350f;
  margin: 0 0 var(--space-2);
}

.api-docs__limit-desc {
  font-size: var(--font-size-sm);
  color: #92400e;
  line-height: 1.6;
  margin: 0;
}

.api-docs__limit-desc code {
  background: rgba(255, 255, 255, 0.6);
  padding: 0.125rem 0.375rem;
  border-radius: 4px;
  font-size: 0.8125rem;
  font-family: 'Consolas', 'Monaco', monospace;
}

/* ===========================
   CTA
   =========================== */
.api-docs__cta {
  padding: 80px 0;
  background: #0d1f3c;
  text-align: center;
}

.api-docs__cta-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-4);
}

.api-docs__cta-title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: #fff;
  margin: 0;
}

.api-docs__cta-desc {
  font-size: var(--font-size-base);
  color: rgba(255, 255, 255, 0.65);
  margin: 0;
  max-width: 500px;
  line-height: 1.7;
}

/* ===========================
   АДАПТИВ
   =========================== */
@media (max-width: 1100px) {
  .api-docs__stats-inner { grid-template-columns: repeat(2, 1fr); }
  .api-docs__stat:nth-child(2) { border-right: none; }
  .api-docs__stat:nth-child(1), .api-docs__stat:nth-child(2) { border-bottom: 1px solid var(--color-gray-blue); }
}

@media (max-width: 768px) {
  .api-docs__hero { padding: 44px 0 48px; }
  .api-docs__hero-title { font-size: clamp(1.5rem, 6vw, 2.2rem); }
  .api-docs__hero-subtitle { font-size: var(--font-size-sm); }

  .api-docs__stat {
    flex-direction: column;
    align-items: flex-start;
    gap: 2px;
    padding: var(--space-3) var(--space-4);
  }
  .api-docs__stat-value { font-size: var(--font-size-base); }
  .api-docs__stat-note { display: none; }

  .api-docs__content-section { padding: 48px 0; }
  .api-docs__section-title { font-size: var(--font-size-xl); }
  .api-docs__section-header { margin-bottom: var(--space-8); }

  .api-docs__content-card { padding: var(--space-4); }
  .api-docs__endpoint { flex-direction: column; align-items: flex-start; gap: var(--space-2); }
  
  .api-docs__quick-access { flex-direction: column; align-items: flex-start; }
}

@media (max-width: 540px) {
  .api-docs__hero { padding: 32px 0 40px; }
  .api-docs__hero-title { font-size: 1.5rem; line-height: 1.2; }
  .api-docs__hero-subtitle { font-size: var(--font-size-sm); }
  .api-docs__hero-actions { flex-direction: column; }
  .api-docs__btn { width: 100%; text-align: center; }

  .api-docs__stats-inner { grid-template-columns: 1fr 1fr; }
  .api-docs__stat {
    flex-direction: column;
    align-items: flex-start;
    padding: var(--space-3) var(--space-3);
    gap: 2px;
  }
  .api-docs__stat:nth-child(1), .api-docs__stat:nth-child(3) { border-right: 1px solid var(--color-gray-blue); }
  .api-docs__stat:nth-child(2), .api-docs__stat:nth-child(4) { border-right: none; }
  .api-docs__stat-value { font-size: var(--font-size-base); white-space: normal; }
  .api-docs__stat-label { font-size: 11px; }
  .api-docs__stat-note { display: none; }

  .api-docs__section-title { font-size: var(--font-size-lg); }
  .api-docs__cta { padding: 48px 0; }
  .api-docs__cta-title { font-size: var(--font-size-xl); }
}
</style>
