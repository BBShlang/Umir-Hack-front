<template>
  <div class="diploma-card" @click="$emit('click', diploma)">
    <div class="diploma-card__header">
      <div class="diploma-card__title">{{ diploma.universityName || 'Диплом' }}</div>
      <StatusBadge
        :status="isVerified ? 'verified' : 'pending'"
        :label="isVerified ? 'Подтверждён' : 'Не подтверждён'"
      />
    </div>

    <div class="diploma-card__body">
      <div class="diploma-card__row">
        <span class="diploma-card__label">Серия</span>
        <span class="diploma-card__value">{{ diploma.series ?? '—' }}</span>
      </div>
      <div class="diploma-card__row">
        <span class="diploma-card__label">Номер</span>
        <span class="diploma-card__value">{{ diploma.number ?? diploma.diplomaNumber ?? '—' }}</span>
      </div>
      <div class="diploma-card__row">
        <span class="diploma-card__label">Дата выдачи</span>
        <span class="diploma-card__value">{{ formatDate(diploma.issueDate || diploma.issuedAt) }}</span>
      </div>
      <div class="diploma-card__row">
        <span class="diploma-card__label">Специальность</span>
        <span class="diploma-card__value">{{ diploma.specialty ?? '—' }}</span>
      </div>
    </div>

    <div class="diploma-card__footer">
      <span class="diploma-card__hash">{{ shortHash }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import StatusBadge from '../common/StatusBadge.vue'

/**
 * MyDiplomaCard — карточка диплома в личном кабинете студента
 * @prop {Object} diploma — объект диплома
 */
const props = defineProps({
  diploma: { type: Object, required: true }
})

defineEmits(['click'])

const isVerified = computed(() => {
  // Поддержка и старой модели, и новой из Swagger
  if (typeof props.diploma.isVerified === 'boolean') return props.diploma.isVerified
  return props.diploma.status === 'ACTIVE'
})

const shortHash = computed(() => {
  const h =
    props.diploma.hash ||
    props.diploma.payloadHash ||
    props.diploma.certificateId ||
    ''
  return h.length > 12 ? h.slice(0, 6) + '…' + h.slice(-4) : h || '—'
})

function formatDate(dateStr) {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleDateString('ru-RU', {
    day: '2-digit',
    month: 'long',
    year: 'numeric'
  })
}
</script>
<style scoped>
.diploma-card {
  background: #ffffff;
  border-radius: var(--radius-lg, 16px);
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0, 0, 0, 0.08));
  border: 1px solid var(--color-gray-blue, #d4dbe6);
  padding: var(--space-5, 20px);
  display: flex;
  flex-direction: column;
  gap: var(--space-4, 16px);
  cursor: pointer;
  transition: box-shadow 0.2s ease, border-color 0.2s ease, transform 0.15s ease;
}

.diploma-card:hover {
  box-shadow: var(--shadow-md, 0 4px 12px rgba(0, 0, 0, 0.12));
  border-color: var(--color-sea-clear, #48b8c2);
  transform: translateY(-2px);
}

.diploma-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-2, 8px);
}

.diploma-card__title {
  font-size: 1.0625rem;
  font-weight: 600;
  color: var(--color-black, #1a1a1a);
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.diploma-card__body {
  display: flex;
  flex-direction: column;
  gap: var(--space-3, 12px);
}

.diploma-card__row {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: var(--space-2, 8px);
}

.diploma-card__label {
  font-size: 0.8125rem;
  color: var(--color-pale-black, #8c8c8c);
  white-space: nowrap;
}

.diploma-card__value {
  font-size: 0.875rem;
  color: var(--color-black, #1a1a1a);
  font-weight: 500;
  text-align: right;
}

.diploma-card__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: var(--space-3, 12px);
  border-top: 1px solid var(--color-gray-blue, #d4dbe6);
}

.diploma-card__hash {
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 0.75rem;
  color: var(--color-pale-black, #8c8c8c);
  letter-spacing: 0.02em;
}
</style>
