<template>
  <span class="status-badge" :class="statusClass">
    {{ statusLabel }}
  </span>
</template>

<script setup>
import { computed } from 'vue'

/**
 * StatusBadge — бейдж для отображения статуса
 * @prop {String} status - ключ статуса (active, revoked, pending, expired, error)
 */
const props = defineProps({
  status: {
    type: String,
    default: 'pending'
  }
})

const statusMap = {
  active: { label: 'Активен', class: 'status-active' },
  revoked: { label: 'Отозван', class: 'status-revoked' },
  pending: { label: 'Ожидает', class: 'status-pending' },
  expired: { label: 'Истёк', class: 'status-expired' },
  error: { label: 'Ошибка', class: 'status-error' }
}

const statusClass = computed(() => statusMap[props.status]?.class || 'status-pending')
const statusLabel = computed(() => statusMap[props.status]?.label || 'Неизвестно')
</script>

<style scoped>
.status-badge {
  display: inline-block;
  padding: 3px var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  line-height: 1.4;
}

.status-active {
  background: #e6f5ee;
  color: var(--color-green);
}

.status-revoked {
  background: #fde8e7;
  color: var(--color-red);
}

.status-pending {
  background: #fff0e4;
  color: var(--color-orange);
}

.status-expired {
  background: var(--color-pale-blue);
  color: var(--color-gray);
}

.status-error {
  background: #fde8e7;
  color: var(--color-red);
}
</style>
