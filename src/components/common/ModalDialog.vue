<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="isOpen" class="modal-overlay" @click.self="onBackdropClick">
        <div class="modal-dialog" :class="sizeClass" role="dialog" :aria-labelledby="titleId">
          <div class="modal-header">
            <h3 :id="titleId" class="modal-title">
              <slot name="title">Заголовок</slot>
            </h3>
            <button class="modal-close" @click="close" aria-label="Закрыть">✕</button>
          </div>
          <div class="modal-body">
            <slot />
          </div>
          <div v-if="$slots.footer" class="modal-footer">
            <slot name="footer" />
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { computed, onMounted, onUnmounted } from 'vue'

/**
 * ModalDialog — модальное окно с Teleport
 * @prop {Boolean} isOpen - состояние видимости
 * @prop {String} size - размер ('sm', 'md', 'lg', 'xl')
 * @prop {Boolean} closeOnBackdrop - закрытие по клику на оверлей
 */
const props = defineProps({
  isOpen: { type: Boolean, default: false },
  size: { type: String, default: 'md', validator: (v) => ['sm', 'md', 'lg', 'xl'].includes(v) },
  closeOnBackdrop: { type: Boolean, default: true }
})

const emit = defineEmits(['close'])

const titleId = computed(() => `modal-title-${Math.random().toString(36).slice(2, 9)}`)

const sizeClass = computed(() => `modal-${props.size}`)

function close() {
  emit('close')
}

function onBackdropClick() {
  if (props.closeOnBackdrop) close()
}

function onEscape(e) {
  if (e.key === 'Escape' && props.isOpen) close()
}

onMounted(() => document.addEventListener('keydown', onEscape))
onUnmounted(() => document.removeEventListener('keydown', onEscape))
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(13, 31, 60, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: var(--space-4);
}

.modal-dialog {
  background: #fff;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  display: flex;
  flex-direction: column;
  max-height: calc(100vh - 2rem);
}

.modal-sm { width: 100%; max-width: 400px; }
.modal-md { width: 100%; max-width: 560px; }
.modal-lg { width: 100%; max-width: 800px; }
.modal-xl { width: 100%; max-width: 1100px; }

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-5);
  border-bottom: 1px solid var(--color-gray-blue);
}

.modal-title {
  margin: 0;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-black);
}

.modal-close {
  background: none;
  border: 1px solid var(--color-gray-blue);
  font-size: var(--font-size-md);
  cursor: pointer;
  color: var(--color-pale-black);
  line-height: 1;
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
}

.modal-close:hover {
  background: var(--color-pale-blue);
  color: var(--color-main-blue);
}

.modal-body {
  padding: var(--space-6);
  overflow-y: auto;
}

.modal-footer {
  padding: var(--space-4) var(--space-5);
  border-top: 1px solid var(--color-gray-blue);
  display: flex;
  justify-content: flex-end;
  gap: var(--space-3);
}

/* Transitions */
.modal-enter-active,
.modal-leave-active {
  transition: opacity var(--transition-base);
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-active .modal-dialog,
.modal-leave-active .modal-dialog {
  transition: transform var(--transition-base);
}

.modal-enter-from .modal-dialog,
.modal-leave-to .modal-dialog {
  transform: scale(0.96) translateY(12px);
}
</style>
