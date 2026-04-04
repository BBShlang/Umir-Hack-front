<template>
  <div class="search-input" :class="{ focused: isFocused }">
    <input
      ref="inputRef"
      type="search"
      class="search-field"
      :value="modelValue"
      :placeholder="placeholder"
      @input="onInput"
      @focus="isFocused = true"
      @blur="isFocused = false"
    />
    <button v-if="modelValue" class="clear-btn" @click="onClear" aria-label="Очистить">✕</button>
  </div>
</template>

<script setup>
import { ref } from 'vue'

/**
 * SearchInput — поле поиска с иконкой и кнопкой очистки
 * @prop {String} modelValue
 * @prop {String} placeholder
 */
defineProps({
  modelValue: { type: String, default: '' },
  placeholder: { type: String, default: 'Поиск...' }
})

const emit = defineEmits(['update:modelValue', 'search'])

const isFocused = ref(false)
const inputRef = ref(null)

let debounceTimer = null

function onInput(e) {
  const value = e.target.value
  emit('update:modelValue', value)

  // Debounce 300ms
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    emit('search', value)
  }, 300)
}

function onClear() {
  emit('update:modelValue', '')
  emit('search', '')
  inputRef.value?.focus()
}
</script>

<style scoped>
.search-input {
  position: relative;
  display: flex;
  align-items: center;
}

.search-field {
  width: 100%;
  padding: var(--space-3) var(--space-10) var(--space-3) var(--space-10);
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-md);
  font-size: var(--font-size-base);
  background: #fff;
  transition: border-color var(--transition-fast), box-shadow var(--transition-fast);
  font-family: var(--font-family);
}

.search-field:focus {
  outline: none;
  border-color: var(--color-main-blue);
  box-shadow: 0 0 0 3px rgba(38, 75, 130, 0.12);
}

.search-icons {
  position: absolute;
  left: var(--space-4);
  width: 18px;
  height: 18px;
  color: var(--color-pale-black);
  pointer-events: none;
}

.clear-btn {
  position: absolute;
  right: var(--space-3);
  background: none;
  border: none;
  cursor: pointer;
  color: var(--color-pale-black);
  font-size: var(--font-size-base);
  padding: var(--space-1);
  border-radius: var(--radius-sm);
  transition: background var(--transition-fast);
}

.clear-btn:hover {
  background: var(--color-pale-blue);
}
</style>
