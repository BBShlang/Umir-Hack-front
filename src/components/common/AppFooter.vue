<template>
  <footer class="app-footer">
    <div class="footer-top">
      <div class="footer-content">
        <div class="footer-brand">
          <span class="footer-logo__text">ДиплоРеестр</span>
          <p class="footer-desc">
            Система верификации дипломов с&nbsp;цифровой подписью ВУЗа и&nbsp;мгновенной проверкой подлинности.
          </p>
        </div>

        <div class="footer-right">
          <div class="footer-nav">
            <h4 class="footer-nav__title">Информация</h4>
            <ul class="footer-nav__list">
              <li><router-link to="/privacy" class="footer-nav__link">Конфиденциальность</router-link></li>
              <li><router-link to="/terms" class="footer-nav__link">Условия</router-link></li>
            </ul>
          </div>

          <div class="footer-contact">
            <h4 class="footer-nav__title">Контакты</h4>
            <ul class="footer-contact__list">
              <li>
                <svg class="footer-icon" viewBox="0 0 16 16" width="14" height="14">
                  <path d="M2 3a1 1 0 011-1h2.153a1 1 0 01.986.836l.74 4.435a1 1 0 01-.54 1.06l-1.548.773a11.037 11.037 0 006.105 6.105l.774-1.548a1 1 0 011.059-.54l4.435.74a1 1 0 01.836.986V13a1 1 0 01-1 1h-2C7.82 14 2 8.18 2 2V3z" fill="currentColor" />
                </svg>
                <a href="tel:+78001234567">8 (800) 123-45-67</a>
              </li>
              <li>
                <svg class="footer-icon" viewBox="0 0 16 16" width="14" height="14">
                  <path d="M.05 3.555A2 2 0 012 2h12a2 2 0 011.95 1.555L8 8.414.05 3.555zM0 4.697v7.104l5.803-3.558L0 4.697zM6.761 8.83l-6.57 4.027A2 2 0 002 14h12a2 2 0 001.808-1.144l-6.57-4.027L8 9.586l-1.239-.757zm3.437-.758L16 11.801V4.697l-5.802 3.375z" fill="currentColor" />
                </svg>
                <a href="mailto:support@diploms.ru">support@diploms.ru</a>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <div class="footer-bottom">
      <div class="footer-bottom__content">
        <span class="copyright">© {{ currentYear }} ДиплоРеестр. Все права защищены.</span>
        <span class="footer-version">M2/CREW</span>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { computed } from 'vue'

const currentYear = computed(() => new Date().getFullYear())
</script>

<style scoped>
.app-footer {
  background: #0d1f3c;
  color: rgba(255, 255, 255, 0.6);
  font-size: var(--font-size-sm);
}

.footer-top {
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  padding: var(--space-10) var(--space-6) var(--space-8);
  max-width: var(--container-max);
  margin: 0 auto;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: start;
}

.footer-right {
  display: flex;
  gap: var(--space-10);
}

/* --- Bottom bar --- */
.footer-bottom {
  background: rgba(0, 0, 0, 0.15);
  padding: var(--space-4) var(--space-6);
}

.footer-bottom__content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: var(--container-max);
  margin: 0 auto;
}

/* --- Brand --- */
.footer-brand {
  max-width: 280px;
}

.footer-logo__text {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: #fff;
  letter-spacing: 0.02em;
}

.footer-desc {
  line-height: var(--line-height-base);
  font-size: var(--font-size-sm);
  color: rgba(255, 255, 255, 0.45);
  margin: var(--space-2) 0 0;
}

/* --- Nav columns --- */
.footer-nav__title {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: rgba(255, 255, 255, 0.35);
  margin: 0 0 var(--space-4);
}

.footer-nav__list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.footer-nav__link {
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  font-size: var(--font-size-sm);
  transition: color var(--transition-fast);
}

.footer-nav__link:hover {
  color: var(--color-sea-clear);
}

/* --- Contact --- */
.footer-contact__list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.footer-contact__list li {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.footer-icon {
  flex-shrink: 0;
  color: rgba(255, 255, 255, 0.4);
}

.footer-contact__list a {
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  transition: color var(--transition-fast);
}

.footer-contact__list a:hover {
  color: var(--color-sea-clear);
}

.copyright {
  font-size: var(--font-size-xs);
  color: rgba(255, 255, 255, 0.4);
}

.footer-version {
  font-size: var(--font-size-xs);
  color: rgba(255, 255, 255, 0.25);
  font-family: monospace;
}

/* --- Responsive --- */
@media (max-width: 900px) {
  .footer-right {
    gap: var(--space-6);
  }
}

@media (max-width: 520px) {
  .footer-top {
    padding: var(--space-8) var(--space-4) var(--space-6);
  }

  .footer-content {
    flex-direction: column;
    gap: var(--space-6);
  }

  .footer-right {
    flex-direction: column;
    gap: var(--space-4);
  }

  .footer-bottom {
    padding: var(--space-3) var(--space-4);
  }

  .footer-bottom__content {
    flex-direction: column;
    gap: var(--space-2);
    text-align: center;
  }
}
</style>
