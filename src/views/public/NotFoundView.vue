<template>
  <div class="not-found">
    <main class="main-content">
      <!-- ===== HERO 404 ===== -->
      <section class="not-found__hero">
        <div class="not-found__hero-bg" aria-hidden="true">
          <div class="not-found__hero-grid-lines" />
          <div class="not-found__hero-glow not-found__hero-glow--1" />
          <div class="not-found__hero-glow not-found__hero-glow--2" />
        </div>

        <div class="container not-found__hero-inner">
          <div class="not-found__content">
            <h1 class="not-found__code">404</h1>
            <h2 class="not-found__title">Страница не найдена</h2>
            <p class="not-found__description">
              Запрашиваемая страница не существует или была перемещена. 
              Проверьте адрес или вернитесь на главную.
            </p>

            <div class="not-found__actions">
              <router-link to="/login" class="not-found__btn not-found__btn--primary">
                Войти
              </router-link>
              <router-link to="/verify" class="not-found__btn not-found__btn--outline">
                Проверить диплом
              </router-link>
            </div>

            <div class="not-found__hints">
              <span class="not-found__hints-label">Возможно, вы искали:</span>
              <div class="not-found__hints-list">
                <button class="not-found__hint-btn" @click="$router.push('/login')">Войти в аккаунт</button>
                <button class="not-found__hint-btn" @click="$router.push('/register')">Создать аккаунт</button>
                <button class="not-found__hint-btn" @click="$router.push('/verify')">Проверить диплом</button>
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

// 404 — страница не найдена
</script>

<style scoped>
.not-found { min-height: 100vh; }

/* ===========================
   HERO 404
   =========================== */
.not-found__hero {
  position: relative;
  background: #0d1f3c;
  padding: 72px 0 80px;
}

.not-found__hero-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}

.not-found__hero-grid-lines {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(
      rgba(255, 255, 255, 0.03) 1px,
      transparent 1px
    ),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.not-found__hero-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.not-found__hero-glow--1 {
  width: 500px;
  height: 500px;
  top: -150px;
  right: -100px;
  background: rgba(38, 75, 130, 0.45);
}

.not-found__hero-glow--2 {
  width: 380px;
  height: 380px;
  bottom: -120px;
  left: -60px;
  background: rgba(72, 184, 194, 0.2);
}

.not-found__hero-inner {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: center;
}

.not-found__content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: var(--space-6);
  max-width: 600px;
}

.not-found__code {
  font-size: clamp(4rem, 12vw, 8rem);
  font-weight: var(--font-weight-bold);
  color: var(--color-main-blue);
  margin: 0;
  line-height: 1;
  letter-spacing: -0.04em;
  text-shadow: 0 0 40px rgba(59, 130, 246, 0.3);
}

.not-found__title {
  font-size: clamp(1.5rem, 4vw, 2rem);
  font-weight: var(--font-weight-bold);
  color: #fff;
  margin: 0;
  line-height: 1.2;
}

.not-found__description {
  font-size: var(--font-size-base);
  color: rgba(255, 255, 255, 0.65);
  line-height: 1.7;
  margin: 0;
  max-width: 480px;
}

.not-found__actions {
  display: flex;
  justify-content: center;
  gap: var(--space-3);
  flex-wrap: wrap;
  width: 100%;
}

.not-found__btn {
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

.not-found__btn--primary {
  background: var(--color-main-blue);
  color: #fff;
}

.not-found__btn--primary:hover {
  background: var(--color-main-blue-dark);
  transform: translateY(-1px);
}

.not-found__btn--outline {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: #fff;
}

.not-found__btn--outline:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.5);
}

.not-found__hints {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  justify-content: center;
  gap: var(--space-2);
  max-width: 500px;
}

.not-found__hints-label {
  font-size: var(--font-size-xs);
  color: rgba(255, 255, 255, 0.4);
  white-space: nowrap;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.not-found__hints-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: var(--space-2);
}

.not-found__hint-btn {
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

.not-found__hint-btn:hover {
  background: rgba(255, 255, 255, 0.14);
  color: #fff;
}

/* ===========================
   STATS STRIP
   =========================== */
.not-found__stats-strip {
  background: #fff;
  border-bottom: 1px solid var(--color-gray-blue);
}

.not-found__stats-inner {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0;
}

.not-found__stat {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-5) var(--space-5);
  border-right: 1px solid var(--color-gray-blue);
}

.not-found__stat:last-child { border-right: none; }

.not-found__stat-value {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-main-blue);
  white-space: nowrap;
  flex-shrink: 0;
}

.not-found__stat-text { display: flex; flex-direction: column; gap: 1px; }
.not-found__stat-label { font-size: var(--font-size-sm); font-weight: var(--font-weight-semibold); color: var(--color-black); line-height: 1.3; }
.not-found__stat-note { font-size: var(--font-size-xs); color: var(--color-pale-black); }

/* ===========================
   АДАПТИВ
   =========================== */
@media (max-width: 1100px) {
  .not-found__stats-inner { grid-template-columns: repeat(2, 1fr); }
  .not-found__stat:nth-child(2) { border-right: none; }
  .not-found__stat:nth-child(1), .not-found__stat:nth-child(2) { border-bottom: 1px solid var(--color-gray-blue); }
}

@media (max-width: 768px) {
  .not-found__hero { padding: 44px 0 48px; }
  .not-found__code { font-size: clamp(3rem, 15vw, 6rem); }
  .not-found__title { font-size: clamp(1.25rem, 5vw, 1.75rem); }
  .not-found__description { font-size: var(--font-size-sm); }
  
  .not-found__stat {
    flex-direction: column;
    align-items: flex-start;
    gap: 2px;
    padding: var(--space-3) var(--space-4);
  }
  .not-found__stat-value { font-size: var(--font-size-base); }
  .not-found__stat-note { display: none; }
  
  .not-found__hints { flex-direction: column; align-items: center; }
  
  .not-found__stats-strip { padding: 0; }
}

@media (max-width: 540px) {
  .not-found__hero { padding: 32px 0 40px; }
  .not-found__code { font-size: 4rem; }
  .not-found__title { font-size: 1.25rem; }
  .not-found__description { font-size: var(--font-size-sm); }
  .not-found__actions { flex-direction: column; align-items: center; }
  .not-found__btn { width: 100%; max-width: 280px; text-align: center; }
  
  .not-found__stats-inner { grid-template-columns: 1fr 1fr; }
  .not-found__stat {
    flex-direction: column;
    align-items: flex-start;
    padding: var(--space-3) var(--space-3);
    gap: 2px;
  }
  .not-found__stat:nth-child(1), .not-found__stat:nth-child(3) { border-right: 1px solid var(--color-gray-blue); }
  .not-found__stat:nth-child(2), .not-found__stat:nth-child(4) { border-right: none; }
  .not-found__stat-value { font-size: var(--font-size-base); white-space: normal; }
  .not-found__stat-label { font-size: 11px; }
  .not-found__stat-note { display: none; }
}
</style>