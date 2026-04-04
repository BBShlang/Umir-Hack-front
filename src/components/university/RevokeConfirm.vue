<template>
  <div class="revoke-confirm">
    <div class="confirm-icon">⚠️</div>
    <h3>Отозвать диплом?</h3>
    <p class="diploma-info">
      <strong>ФИО:</strong> {{ diploma.studentName }}<br />
      <strong>Серийный номер:</strong> {{ diploma.serialNumber }}<br />
      <strong>Дата выдачи:</strong> {{ diploma.issueDate }}
    </p>
    <p class="warning">
      После отзыва диплом станет недействительным. Все QR-ссылки будут деактивированы,
      а проверки через HR-портал вернут статус «Отозван».
    </p>
    <div class="confirm-input">
      <label>
        Введите «ОТОЗВАТЬ» для подтверждения:
        <input
          type="text"
          v-model="confirmText"
          placeholder="ОТОЗВАТЬ"
          :class="{ valid: isConfirmed }"
        />
      </label>
    </div>
    <div class="actions">
      <button class="btn" @click="emit('cancel')">Отмена</button>
      <button
        class="btn btn-danger"
        :disabled="!isConfirmed"
        @click="emit('confirm')"
      >
        Подтвердить отзыв
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

/**
 * RevokeConfirm — детальный экран подтверждения отзыва диплома
 * @prop {Object} diploma - данные диплома
 */
defineProps({
  diploma: { type: Object, default: () => ({}) }
})

const emit = defineEmits(['confirm', 'cancel'])

const confirmText = ref('')
const isConfirmed = computed(() => confirmText.value.trim() === 'ОТОЗВАТЬ')
</script>

<style scoped>
.revoke-confirm {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 1rem;
}

.confirm-icon {
  font-size: 3rem;
}

.revoke-confirm h3 {
  margin: 0;
  font-size: 1.25rem;
  color: var(--color-text, #0f172a);
}

.diploma-info {
  background: #f1f5f9;
  padding: 1rem;
  border-radius: 8px;
  font-size: 0.875rem;
  text-align: left;
  width: 100%;
  max-width: 400px;
}

.warning {
  font-size: 0.875rem;
  color: var(--color-error, #dc2626);
  max-width: 400px;
}

.confirm-input label {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  font-size: 0.875rem;
  width: 100%;
  max-width: 400px;
}

.confirm-input input {
  padding: 0.625rem;
  border: 2px solid var(--color-border, #cbd5e1);
  border-radius: 6px;
  font-size: 0.875rem;
  text-align: center;
  text-transform: uppercase;
}

.confirm-input input.valid {
  border-color: #22c55e;
  background: #f0fdf4;
}

.actions {
  display: flex;
  gap: 1rem;
  margin-top: 0.5rem;
}

.btn {
  padding: 0.625rem 1.5rem;
  border: 1px solid var(--color-border, #cbd5e1);
  background: #fff;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.875rem;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-danger {
  background: #dc2626;
  color: #fff;
  border: none;
}
</style>
