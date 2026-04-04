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
              Мои дипломы<br />
              <span class="dashboard__title-accent">Личный кабинет студента</span>
            </h1>

            <p class="dashboard__subtitle">
              Найдите свой диплом по номеру или ФИО, поделитесь с работодателем и отслеживайте проверки.
            </p>
            <div class="diplomas__hero-actions">
              <router-link to="/student/diplomas" class="diplomas__btn diplomas__btn--outline">Мои дипломы</router-link>
              <router-link to="/student/share" class="diplomas__btn diplomas__btn--outline">Поделиться дипломом</router-link>
            </div>
              </div>
            </div>

      </section>

      <!-- ===== Основная рабочая зона ===== -->
      <section class="dashboard__workspace">
        <div class="container">
          <div class="dashboard__workspace-head">
            <p class="dashboard__workspace-eyebrow">Поиск диплома</p>
            <h2 class="dashboard__workspace-title">Найдите свой документ</h2>
          </div>

          <div class="dashboard__workspace-grid">
            <!-- Левая колонка: форма поиска -->
            <div class="dashboard__workspace-panel">
              <div class="dashboard__panel-header">
                <div class="dashboard__panel-icon">
                  <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <circle cx="8" cy="8" r="5.5" stroke="currentColor" stroke-width="1.5"/>
                    <path d="M12 12l5 5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                  </svg>
                </div>
                <div class="dashboard__panel-title-wrap">
                  <h3 class="dashboard__panel-title">Поиск</h3>
                  <p class="dashboard__panel-desc">Введите номер диплома или выберите ФИО</p>
                </div>
              </div>

              <div class="dashboard__search-card">
                <form @submit.prevent="onSearch" class="sf">
                  <div class="sf-field">
                    <label class="sf-label">Номер диплома или ФИО</label>
                    <input
                      v-model="searchQuery"
                      type="text"
                      class="sf-input"
                      placeholder="Например: 12345678901234"
                      autocomplete="off"
                      spellcheck="false"
                      @input="onSearchInput"
                      @focus="showSuggestions = true"
                    />

                    <!-- Автодополнение ФИО -->
                    <Transition name="sf-fade">
                      <ul v-if="showSuggestions && filteredNames.length" class="sf-suggestions">
                        <li
                          v-for="(name, idx) in filteredNames"
                          :key="idx"
                          class="sf-suggestion"
                          @click="selectName(name)"
                        >
                          <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
                            <circle cx="7" cy="5" r="3" stroke="currentColor" stroke-width="1.2"/>
                            <path d="M2 13c0-3 2.5-5 5-5s5 2 5 5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/>
                          </svg>
                          {{ name }}
                        </li>
                      </ul>
                    </Transition>
                  </div>

                  <button type="submit" class="sf-btn" :disabled="loading || !searchQuery.trim()">
                    <svg v-if="loading" class="sf-spin" width="18" height="18" viewBox="0 0 24 24" fill="none">
                      <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2" stroke-dasharray="31.4 31.4" stroke-linecap="round">
                        <animateTransform attributeName="transform" type="rotate" from="0 12 12" to="360 12 12" dur="0.8s" repeatCount="indefinite"/>
                      </circle>
                    </svg>
                    <span>{{ loading ? 'Поиск…' : 'Найти' }}</span>
                  </button>
                </form>

                <!-- Результат поиска -->
                <Transition name="sf-fade">
                  <div v-if="diplomaResult" class="sf-res" :class="diplomaResult.statusClass">
                    <div class="sf-res-head">
                      <span class="sf-res-icon" :class="{ 'sf-res-icon--ok': diplomaResult.isValid }">
                        <svg v-if="diplomaResult.isValid" width="20" height="20" viewBox="0 0 20 20" fill="none">
                          <circle cx="10" cy="10" r="9" stroke="currentColor" stroke-width="1.5"/>
                          <path d="M6 10l3 3 5-6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                        <svg v-else width="20" height="20" viewBox="0 0 20 20" fill="none">
                          <circle cx="10" cy="10" r="9" stroke="currentColor" stroke-width="1.5"/>
                          <path d="M7 7l6 6M13 7l-6 6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                        </svg>
                      </span>
                      <h4 class="sf-res-title">{{ diplomaResult.title }}</h4>
                    </div>
                    <dl v-if="diplomaResult.isValid" class="sf-res-grid">
                      <span class="sf-res-k">ВУЗ</span>
                      <span class="sf-res-v">{{ diplomaResult.university || '—' }}</span>
                      <span class="sf-res-k">Специальность</span>
                      <span class="sf-res-v">{{ diplomaResult.specialty || '—' }}</span>
                      <span class="sf-res-k">Дата выдачи</span>
                      <span class="sf-res-v">{{ diplomaResult.issueDate || '—' }}</span>
                      <span class="sf-res-k">Статус</span>
                      <span class="sf-res-v"><StatusBadge :status="diplomaResult.status" /></span>
                    </dl>
                  </div>
                </Transition>

                <Transition name="sf-fade">
                  <div v-if="searchError" class="sf-err">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                      <circle cx="8" cy="8" r="6.5" stroke="currentColor" stroke-width="1.3"/>
                      <path d="M8 5v3.5M8 10.5v.5" stroke="currentColor" stroke-width="1.3" stroke-linecap="round"/>
                    </svg>
                    <span>{{ searchError }}</span>
                  </div>
                </Transition>
              </div>
            </div>

            <!-- Правая колонка: быстрые действия -->
            <div class="dashboard__workspace-sidebar">
              <div class="dashboard__sidebar-card">
                <h4 class="dashboard__sidebar-title">Мои действия</h4>
                <div class="dashboard__sidebar-actions">
                  <router-link to="/student/diplomas" class="dashboard__sidebar-btn">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                      <rect x="2" y="3" width="12" height="10" rx="1.5" stroke="currentColor" stroke-width="1.4"/>
                      <path d="M2 6h12" stroke="currentColor" stroke-width="1.2"/>
                    </svg>
                    Все дипломы
                  </router-link>
                  <router-link to="/student/share" class="dashboard__sidebar-btn">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                      <circle cx="11" cy="3" r="2" stroke="currentColor" stroke-width="1.3"/>
                      <circle cx="4" cy="8" r="2" stroke="currentColor" stroke-width="1.3"/>
                      <circle cx="11" cy="13" r="2" stroke="currentColor" stroke-width="1.3"/>
                      <path d="M6 9l3 3M6 7l3-3" stroke="currentColor" stroke-width="1.2"/>
                    </svg>
                    Поделиться дипломом
                  </router-link>
                  <router-link to="/student/diplomas" class="dashboard__sidebar-btn">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                      <rect x="3" y="2" width="10" height="12" rx="1.5" stroke="currentColor" stroke-width="1.4"/>
                      <path d="M6 6h4M6 9h4M6 12h2" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/>
                    </svg>
                    История проверок
                  </router-link>
                </div>
              </div>

              <div class="dashboard__sidebar-card dashboard__sidebar-card--accent">
                <h4 class="dashboard__sidebar-title">Подсказка</h4>
                <p class="dashboard__sidebar-hint">
                  Введите серийный номер диплома или начните вводить ФИО — система подскажет совпадения из вашего профиля.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- ===== Последние верификации ===== -->
      <section class="dashboard__recent">
        <div class="container">
          <div class="dashboard__recent-head">
            <p class="dashboard__how-eyebrow">Активность</p>
            <h2 class="dashboard__how-title">Последние верификации</h2>
          </div>

          <div v-if="!recentVerifications.length" class="dashboard__empty-state">
            <svg width="40" height="40" viewBox="0 0 40 40" fill="none">
              <rect x="4" y="8" width="32" height="24" rx="4" stroke="currentColor" stroke-width="2"/>
              <path d="M13 18h14M13 23h10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            </svg>
            <p>Ваш диплом ещё не проверяли</p>
          </div>

          <div v-else class="dashboard__recent-table">
            <table>
              <thead>
                <tr>
                  <th>Дата</th>
                  <th>Кем проверен</th>
                  <th>Статус</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in recentVerifications" :key="item.id">
                  <td>{{ item.date }}</td>
                  <td>{{ item.checker }}</td>
                  <td>
                    <span class="status-badge" :class="'status-' + item.status">
                      {{ item.status === 'active' ? 'Успешно' : 'Неизвестно' }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </section>
    </main>
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import AppFooter from '../../components/common/AppFooter.vue'
import StatusBadge from '../../components/common/StatusBadge.vue'

// ========== Поиск диплома ==========
const searchQuery = ref('')
const loading = ref(false)
const diplomaResult = ref(null)
const searchError = ref('')
const showSuggestions = ref(false)

// Имена из localStorage (записанные при регистрации)
const registeredNames = ref([])

// Загрузка имён из localStorage
function loadRegisteredNames() {
  const names = []
  // Имитируем: при регистрации имя сохраняется
  const stored = localStorage.getItem('user_name')
  if (stored) names.push(stored)

  // Моковые данные для демонстрации (пока нет бэкенда)
  if (!names.length) {
    names.push('Иванов Иван Иванович')
    names.push('Петрова Мария Сергеевна')
  }

  registeredNames.value = names
}

const filteredNames = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()
  if (!q) return registeredNames.value
  return registeredNames.value.filter(n => n.toLowerCase().includes(q))
})

function onSearchInput() {
  showSuggestions.value = true
}

function selectName(name) {
  searchQuery.value = name
  showSuggestions.value = false
}

// Закрытие подсказок при клике вне
function handleClickOutside(e) {
  if (!e.target.closest('.sf-field')) {
    showSuggestions.value = false
  }
}

async function onSearch() {
  if (!searchQuery.value.trim()) return

  loading.value = true
  searchError.value = ''
  diplomaResult.value = null
  showSuggestions.value = false

  try {
    await new Promise(r => setTimeout(r, 800))

    const q = searchQuery.value.trim()
    // Мок-поиск: если это похоже на номер или ФИО
    const isNumber = /^\d/i.test(q)

    if (isNumber) {
      // Поиск по номеру — моковый результат
      diplomaResult.value = {
        isValid: true,
        status: 'active',
        statusClass: 'sf-res--ok',
        title: 'Диплом найден',
        university: 'МГТУ им. Баумана',
        specialty: 'Информатика и вычислительная техника',
        issueDate: '15.06.2025'
      }
    } else {
      // Поиск по ФИО — моковый результат
      const found = registeredNames.value.some(n =>
        n.toLowerCase().includes(q.toLowerCase())
      )

      if (found) {
        diplomaResult.value = {
          isValid: true,
          status: 'active',
          statusClass: 'sf-res--ok',
          title: 'Диплом найден',
          university: 'МГТУ им. Баумана',
          specialty: 'Информатика и вычислительная техника',
          issueDate: '15.06.2025'
        }
      } else {
        diplomaResult.value = {
          isValid: false,
          statusClass: 'sf-res--fail',
          title: 'Диплом не найден'
        }
      }
    }
  } catch {
    searchError.value = 'Ошибка при поиске. Попробуйте позже.'
  } finally {
    loading.value = false
  }
}

// ========== Последние верификации ==========
const recentVerifications = ref([])

onMounted(() => {
  loadRegisteredNames()
  document.addEventListener('click', handleClickOutside)

  setTimeout(() => {
    recentVerifications.value = [
      { id: 1, date: '28.03.2026', checker: 'ООО «ТехноКадры»', status: 'active' },
      { id: 2, date: '15.03.2026', checker: 'Анонимно', status: 'active' }
    ]
  }, 400)
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
  background-image: linear-gradient(
      rgba(255, 255, 255, 0.03) 1px,
      transparent 1px
    ),
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

/* ---- Buttons (как на diplomas) ---- */
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

.diplomas__btn--outline {
  background: transparent;
  color: #fff;
  border-color: rgba(255, 255, 255, 0.3);
}

.diplomas__btn--outline:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.5);
}

.dashboard__examples {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--space-2);
  max-width: 640px;
}

.dashboard__examples-label {
  font-size: var(--font-size-xs);
  color: rgba(255, 255, 255, 0.4);
  white-space: nowrap;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.dashboard__examples-list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-2);
}

.dashboard__example-btn {
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

.dashboard__example-btn:hover {
  background: rgba(255, 255, 255, 0.14);
  color: #fff;
}

/* ===========================
   WORKSPACE
   =========================== */
.dashboard__workspace {
  padding: 80px 0;
  background: #f1f5f9;
  overflow-x: hidden;
}

.dashboard__workspace-head {
  text-align: center;
  margin-bottom: var(--space-10);
}

.dashboard__workspace-eyebrow {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--color-main-blue);
  margin-bottom: var(--space-2);
}

.dashboard__workspace-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  line-height: 1.2;
}

.dashboard__workspace-grid {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: var(--space-6);
  align-items: start;
}

/* --- Левая панель --- */
.dashboard__workspace-panel {
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.dashboard__panel-header {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-5) var(--space-6);
  background: #0d1f3c;
  border-bottom: 3px solid var(--color-main-blue);
}

.dashboard__panel-icon {
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

.dashboard__panel-icon svg {
  width: 20px;
  height: 20px;
}

.dashboard__panel-title-wrap {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.dashboard__panel-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: #fff;
  margin: 0;
  line-height: 1.3;
}

.dashboard__panel-desc {
  font-size: var(--font-size-xs);
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

.dashboard__search-card {
  padding: var(--space-6);
}

/* --- Правая колонка (сайдбар) --- */
.dashboard__workspace-sidebar {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.dashboard__sidebar-card {
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  padding: var(--space-5);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.dashboard__sidebar-card--accent {
  border-left: 3px solid var(--color-main-blue);
}

.dashboard__sidebar-title {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  margin: 0 0 var(--space-4);
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.dashboard__sidebar-actions {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.dashboard__sidebar-btn {
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

.dashboard__sidebar-btn svg {
  flex-shrink: 0;
  color: var(--color-main-blue);
}

.dashboard__sidebar-btn:hover {
  background: #dde5f0;
  color: var(--color-main-blue);
}

.dashboard__sidebar-hint {
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
  line-height: 1.6;
  margin: 0;
}

/* ===========================
   SEARCH FORM (sf)
   =========================== */
.sf {
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
}

.sf-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
  position: relative;
}

.sf-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-black);
  letter-spacing: 0.01em;
}

.sf-input {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-base);
  font-size: var(--font-size-base);
  font-family: var(--font-family);
  background: #fff;
  color: var(--color-black);
  transition: border-color 0.15s ease, box-shadow 0.15s ease;
}

.sf-input::placeholder {
  color: var(--color-pale-black);
  opacity: 0.6;
}

.sf-input:focus {
  outline: none;
  border-color: var(--color-main-blue);
  box-shadow: 0 0 0 3px rgba(38, 75, 130, 0.1);
}

/* --- Suggestions / Autocomplete --- */
.sf-suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  margin-top: 4px;
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-base);
  box-shadow: var(--shadow-md);
  list-style: none;
  padding: 4px 0;
  z-index: 10;
  max-height: 200px;
  overflow-y: auto;
}

.sf-suggestion {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  font-size: var(--font-size-sm);
  color: var(--color-black);
  cursor: pointer;
  transition: background 0.12s ease;
}

.sf-suggestion svg {
  flex-shrink: 0;
  color: var(--color-pale-black);
}

.sf-suggestion:hover {
  background: var(--color-pale-blue);
  color: var(--color-main-blue);
}

.sf-suggestion:hover svg {
  color: var(--color-main-blue);
}

/* ---- Button ---- */
.sf-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 24px;
  font-size: 14px;
  font-weight: 600;
  font-family: var(--font-family);
  color: #fff;
  background: var(--color-main-blue);
  border: none;
  border-radius: var(--radius-base);
  cursor: pointer;
  white-space: nowrap;
  flex-shrink: 0;
  transition: background 0.15s ease, opacity 0.15s ease;
}

.sf-btn:hover:not(:disabled) {
  background: #1a5bbd;
}

.sf-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.sf-spin {
  flex-shrink: 0;
}

/* ---- Result ---- */
.sf-res {
  border: 1px solid;
  border-radius: var(--radius-lg);
  padding: var(--space-5);
}

.sf-res--ok {
  background: #f0fdf4;
  border-color: #bbf7d0;
}

.sf-res--fail {
  background: #fef2f2;
  border-color: #fecaca;
}

.sf-res-head {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin-bottom: var(--space-4);
}

.sf-res-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  flex-shrink: 0;
  color: #94a3b8;
}

.sf-res-icon--ok {
  background: #dcfce7;
  color: #16a34a;
}

.sf-res-title {
  margin: 0;
  font-size: var(--font-size-base);
  font-weight: 700;
  color: var(--color-black);
}

.sf-res-grid {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 2px 16px;
  font-size: 13px;
}

.sf-res-k {
  color: var(--color-pale-black);
  font-weight: 500;
}

.sf-res-v {
  color: var(--color-black);
}

/* ---- Error ---- */
.sf-err {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  background: #fef2f2;
  color: #991b1b;
  border-radius: var(--radius-base);
  font-size: 13px;
  border: 1px solid #fecaca;
}

.sf-err svg {
  flex-shrink: 0;
  color: #dc2626;
}

/* ---- Transition ---- */
.sf-fade-enter-active,
.sf-fade-leave-active {
  transition: opacity 0.25s ease;
}

.sf-fade-enter-from,
.sf-fade-leave-to {
  opacity: 0;
}

/* ===========================
   RECENT
   =========================== */
.dashboard__recent {
  padding: 80px 0;
  background: #fff;
  overflow-x: hidden;
}

.dashboard__recent-head { text-align: center; margin-bottom: var(--space-8); }

.dashboard__how-eyebrow {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--color-main-blue);
  margin-bottom: var(--space-2);
}

.dashboard__how-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
  line-height: 1.2;
}

.dashboard__empty-state {
  padding: 3rem;
  text-align: center;
  color: #94a3b8;
  background: #fff;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-gray-blue);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-4);
}

.dashboard__empty-state svg {
  opacity: 0.4;
}

.dashboard__empty-state p {
  margin: 0;
  font-size: var(--font-size-sm);
}

.dashboard__recent-table {
  background: #fff;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.dashboard__recent-table table {
  width: 100%;
  border-collapse: collapse;
}

.dashboard__recent-table thead {
  background: var(--color-pale-blue);
}

.dashboard__recent-table th {
  padding: var(--space-3) var(--space-4);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--color-main-blue);
  text-align: left;
}

.dashboard__recent-table td {
  padding: var(--space-3) var(--space-4);
  font-size: var(--font-size-sm);
  color: var(--color-black);
  border-top: 1px solid var(--color-gray-blue);
}

.dashboard__recent-table tbody tr:hover {
  background: #f8fafc;
}

.status-badge {
  display: inline-block;
  padding: 2px 10px;
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
}

.status-active {
  background: #e6f5ee;
  color: #16a34a;
}

/* ===========================
   АДАПТИВ
   =========================== */
@media (max-width: 1100px) {
  .dashboard__workspace-grid {
    grid-template-columns: 1fr;
  }

  .dashboard__workspace-sidebar {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: var(--space-4);
  }
}

@media (max-width: 768px) {
  .dashboard__hero { padding: 44px 0 48px; }
  .dashboard__title { font-size: clamp(1.5rem, 6vw, 2.2rem); }
  .dashboard__subtitle { font-size: var(--font-size-sm); }
  .dashboard__examples { flex-direction: column; align-items: flex-start; }

  .dashboard__workspace,
  .dashboard__recent { padding: 48px 0; }

  .dashboard__workspace-title { font-size: var(--font-size-2xl); }
  .dashboard__workspace-head { margin-bottom: var(--space-8); }

  .dashboard__search-card { padding: var(--space-4); }

  .dashboard__panel-header { padding: var(--space-4); }

  .dashboard__workspace-sidebar {
    grid-template-columns: 1fr;
  }

  .dashboard__how-title { font-size: var(--font-size-2xl); }
  .dashboard__recent-head { margin-bottom: var(--space-6); }
  .dashboard__recent-table { overflow-x: auto; }
}

@media (max-width: 540px) {
  .dashboard__hero { padding: 32px 0 40px; }
  .dashboard__title { font-size: 1.5rem; line-height: 1.2; }
  .dashboard__subtitle { font-size: var(--font-size-sm); }

  .dashboard__workspace-title { font-size: var(--font-size-xl); }
}
</style>
