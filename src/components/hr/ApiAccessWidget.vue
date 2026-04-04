<template>
  <div class="api-access-widget">
    <h3>API доступ</h3>
    <p class="description">Управление ключами для автоматизированной проверки дипломов</p>

    <div v-if="loading" class="loading">Загрузка...</div>

    <div v-else class="api-info">
      <div class="key-row">
        <span class="key-label">API Key:</span>
        <code>{{ apiKey }}</code>
        <button class="btn btn-sm" @click="copyKey">Копировать</button>
      </div>

      <div class="stats-row">
        <div class="stat">
          <span class="stat-label">Запросов сегодня</span>
          <span class="stat-value">{{ stats.today }}</span>
        </div>
        <div class="stat">
          <span class="stat-label">Лимит в день</span>
          <span class="stat-value">{{ stats.limit }}</span>
        </div>
        <div class="stat">
          <span class="stat-label">Осталось</span>
          <span class="stat-value" :class="{ warning: stats.remaining < 50 }">
            {{ stats.remaining }}
          </span>
        </div>
      </div>

      <div class="usage-bar">
        <div class="bar-fill" :style="{ width: usagePercent + '%' }"></div>
      </div>
      <p class="usage-text">{{ usagePercent }}% использовано</p>

      <div class="actions">
        <button class="btn btn-warning" @click="regenerateKey">Перевыпустить ключ</button>
        <button class="btn btn-outline" @click="emit('open-docs')">Документация API</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

/**
 * ApiAccessWidget — виджет управления API-доступом
 */
const emit = defineEmits(['open-docs'])

const loading = ref(true)
const apiKey = ref('')
const stats = ref({ today: 0, limit: 1000, remaining: 0 })

const usagePercent = computed(() => {
  if (!stats.value.limit) return 0
  return Math.round((stats.value.today / stats.value.limit) * 100)
})

onMounted(() => {
  // Эмуляция загрузки данных
  setTimeout(() => {
    apiKey.value = 'sk-prod-' + Math.random().toString(36).slice(2, 14)
    stats.value = { today: 142, limit: 1000, remaining: 858 }
    loading.value = false
  }, 500)
})

function copyKey() {
  navigator.clipboard.writeText(apiKey.value)
}

function regenerateKey() {
  if (confirm('Старый ключ перестанет работать. Продолжить?')) {
    loading.value = true
    setTimeout(() => {
      apiKey.value = 'sk-prod-' + Math.random().toString(36).slice(2, 14)
      stats.value.today = 0
      stats.value.remaining = stats.value.limit
      loading.value = false
    }, 800)
  }
}
</script>

<style scoped>
.api-access-widget {
  padding: 1.5rem;
  background: var(--color-surface, #f8fafc);
  border-radius: 12px;
  border: 1px solid var(--color-border, #e2e8f0);
}

.api-access-widget h3 {
  margin: 0 0 0.5rem;
  font-size: 1.125rem;
}

.description {
  margin: 0 0 1.5rem;
  font-size: 0.875rem;
  color: var(--color-text-muted, #64748b);
}

.loading {
  padding: 2rem;
  text-align: center;
  color: var(--color-text-muted, #94a3b8);
}

.key-row {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
}

.key-label {
  font-size: 0.875rem;
  color: var(--color-text-muted, #64748b);
}

.key-row code {
  font-size: 0.875rem;
  background: #e2e8f0;
  padding: 0.375rem 0.75rem;
  border-radius: 4px;
  word-break: break-all;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  margin-bottom: 1rem;
}

.stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.25rem;
}

.stat-label {
  font-size: 0.75rem;
  color: var(--color-text-muted, #94a3b8);
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--color-text, #0f172a);
}

.stat-value.warning {
  color: var(--color-warning, #f59e0b);
}

.usage-bar {
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 0.5rem;
}

.bar-fill {
  height: 100%;
  background: var(--color-primary, #3b82f6);
  transition: width 0.3s;
}

.usage-text {
  font-size: 0.75rem;
  color: var(--color-text-muted, #64748b);
  text-align: center;
  margin: 0;
}

.actions {
  display: flex;
  gap: 0.75rem;
  margin-top: 1.5rem;
}

.btn {
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.875rem;
}

.btn-sm { padding: 0.375rem 0.75rem; font-size: 0.8125rem; border: 1px solid #cbd5e1; background: #fff; }

.btn-warning {
  background: #fef3c7;
  border: 1px solid #f59e0b;
  color: #92400e;
}

.btn-outline {
  background: #fff;
  border: 1px solid var(--color-border, #cbd5e1);
}
</style>
