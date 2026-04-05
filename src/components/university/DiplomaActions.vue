<template>
  <div class="diploma-actions">
    <button class="btn btn-sm btn-warning" @click="confirmRevoke">
      <slot name="revoke-icon">⚠️</slot> Отозвать
    </button>

    <ModalDialog :is-open="showRevokeConfirm" size="sm" @close="showRevokeConfirm = false">
      <template #title>Подтверждение отзыва</template>
      <p>Вы действительно хотите отозвать диплом <strong>{{ diploma.serialNumber }}</strong>?</p>
      <p class="warning-text">Это действие необратимо. QR-ссылка будет деактивирована.</p>
      <template #footer>
        <button class="btn" @click="showRevokeConfirm = false">Отмена</button>
        <button class="btn btn-danger" @click="revoke">Отозвать</button>
      </template>
    </ModalDialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import ModalDialog from '../common/ModalDialog.vue'

/**
 * DiplomaActions — действия над дипломом (отозвать)
 * @prop {Object} diploma - объект диплома
 */
const props = defineProps({
  diploma: { type: Object, default: () => ({}) }
})

const emit = defineEmits(['revoke'])

const showRevokeConfirm = ref(false)

function confirmRevoke() {
  showRevokeConfirm.value = true
}

function revoke() {
  emit('revoke', props.diploma)
  showRevokeConfirm.value = false
}
</script>

<style scoped>
.action-buttons {
  display: flex;
  gap: var(--space-2);
  flex-wrap: wrap;
}

.btn {
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-base);
  font-size: var(--font-size-xs);
  cursor: pointer;
  transition: all var(--transition-fast);
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  font-family: var(--font-family);
  font-weight: var(--font-weight-semibold);
}

.btn--outline {
  background: transparent;
  border: 1px solid var(--color-gray-blue);
  color: var(--color-black);
}

.btn--outline:hover {
  background: var(--color-pale-blue);
  border-color: var(--color-main-blue);
}

.btn--warning {
  background: #fff0e4;
  border: 1px solid var(--color-orange);
  color: var(--color-orange);
}

.btn--warning:hover {
  background: #ffe0b2;
}

.btn--info {
  background: var(--color-pale-blue);
  border: 1px solid var(--color-main-blue);
  color: var(--color-main-blue);
}

.btn--info:hover {
  background: #d0ddef;
}

.btn--danger {
  background: var(--color-red);
  color: #fff;
  border: none;
}

.btn--danger:hover {
  background: #b0231a;
}

.warning-text {
  color: var(--color-red);
  font-size: var(--font-size-sm);
  margin-top: var(--space-2);
}
</style>
