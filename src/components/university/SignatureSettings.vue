<template>
  <div class="signature-settings">
    <h3>Настройки подписи и безопасности</h3>

    <div class="settings-section">
      <h4>Эмуляция ЭЦП</h4>
      <div class="setting-row">
        <label class="toggle">
          <input type="checkbox" v-model="localSettings.ecpEnabled" />
          <span>Использовать электронную подпись</span>
        </label>
        <p class="setting-desc">
          При загрузке реестра каждый диплом будет подписан виртуальной ЭЦП ВУЗа
        </p>
      </div>
      <div class="ecp-info">
        <span class="ecp-hash">Хеш ЭЦП: <code>{{ ecpHash }}</code></span>
        <button class="btn btn-sm" @click="regenerateEcp">Перегенерировать</button>
      </div>
    </div>

    <div class="settings-section">
      <h4>Срок жизни QR-ссылок (TTL)</h4>
      <div class="ttl-control">
        <input
          type="range"
          min="1"
          max="30"
          v-model.number="localSettings.qrTtlDays"
        />
        <span>{{ localSettings.qrTtlDays }} {{ daysWord(localSettings.qrTtlDays) }}</span>
      </div>
      <p class="setting-desc">Временные ссылки для проверки дипломов истекают через указанный срок</p>
    </div>

    <div class="settings-section">
      <h4>API-ключ для HR-портала</h4>
      <div class="api-key-row">
        <input
          type="password"
          :value="localSettings.apiKey"
          readonly
          class="api-key-input"
        />
        <button class="btn btn-sm" @click="copyApiKey">Копировать</button>
        <button class="btn btn-sm btn-warning" @click="regenerateApiKey">Сбросить</button>
      </div>
    </div>

    <div class="actions">
      <button class="btn btn-primary" @click="save">Сохранить настройки</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

/**
 * SignatureSettings — настройки ЭЦП, TTL QR и API-ключей
 */
const props = defineProps({
  settings: {
    type: Object,
    default: () => ({
      ecpEnabled: true,
      qrTtlDays: 7,
      apiKey: ''
    })
  }
})

const emit = defineEmits(['save'])

const localSettings = ref({ ...props.settings })

watch(() => props.settings, (val) => {
  localSettings.value = { ...val }
}, { deep: true })

const ecpHash = computed(() => {
  return localSettings.value.ecpEnabled
    ? generateMockHash()
    : '—'
})

function generateMockHash() {
  const chars = 'abcdef0123456789'
  return Array.from({ length: 64 }, () =>
    chars[Math.floor(Math.random() * chars.length)]
  ).join('')
}

function regenerateEcp() {
  // Эмуляция
  console.log('ЭЦП перегенерирована')
}

function daysWord(n) {
  if (n === 1) return 'день'
  if (n >= 2 && n <= 4) return 'дня'
  return 'дней'
}

function copyApiKey() {
  navigator.clipboard.writeText(localSettings.value.apiKey || 'sk-test-1234567890abcdef')
  alert('API-ключ скопирован')
}

function regenerateApiKey() {
  if (confirm('Текущий ключ будет аннулирован. Продолжить?')) {
    localSettings.value.apiKey = 'sk-new-' + Date.now().toString(36)
  }
}

function save() {
  emit('save', { ...localSettings.value })
}
</script>

<style scoped>
.signature-settings {
  display: flex;
  flex-direction: column;
  gap: var(--space-8);
}

.signature-settings h3 {
  margin: 0;
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
}

.settings-section {
  padding: var(--space-5);
  background: #fff;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-gray-blue);
}

.settings-section h4 {
  margin: 0 0 var(--space-4);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-bold);
}

.setting-row {
  margin-bottom: var(--space-2);
}

.setting-desc {
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
  margin: var(--space-1) 0 0;
}

.toggle {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  cursor: pointer;
}

.toggle input {
  width: 18px;
  height: 18px;
  accent-color: var(--color-main-blue);
}

.ecp-info,
.api-key-row {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin-top: var(--space-4);
  flex-wrap: wrap;
}

.ecp-hash code {
  font-size: var(--font-size-xs);
  background: var(--color-pale-blue);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  word-break: break-all;
}

.ttl-control {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.ttl-control input[type="range"] {
  flex: 1;
  max-width: 200px;
  accent-color: var(--color-main-blue);
}

.api-key-input {
  font-family: monospace;
  font-size: var(--font-size-sm);
  padding: var(--space-2) var(--space-3);
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-base);
  background: var(--color-pale-blue);
}

.actions {
  display: flex;
  justify-content: flex-end;
}

.btn {
  padding: var(--space-3) var(--space-5);
  border: 1px solid var(--color-gray-blue);
  background: #fff;
  border-radius: var(--radius-md);
  cursor: pointer;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  transition: all var(--transition-fast);
}

.btn:hover { background: var(--color-pale-blue); }

.btn--sm { padding: var(--space-2) var(--space-4); font-size: var(--font-size-xs); }

.btn--primary {
  background: var(--color-main-blue);
  color: #fff;
  border: none;
}

.btn--primary:hover {
  background: #1a3660;
}

.btn--warning {
  background: #fff0e4;
  border-color: var(--color-orange);
  color: var(--color-orange);
}
</style>
