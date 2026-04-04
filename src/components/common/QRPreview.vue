<template>
  <div class="qr-preview">
    <canvas ref="canvasRef" :width="size" :height="size" class="qr-canvas"></canvas>
    <div v-if="loading" class="qr-loading">
      <span class="spinner"></span>
      <span>Генерация QR...</span>
    </div>
    <div v-if="error" class="qr-error">{{ error }}</div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'

/**
 * QRPreview — отображает QR-код на canvas
 * @prop {String} data - данные для кодирования
 * @prop {Number} size - размер в пикселях
 */
const props = defineProps({
  data: { type: String, default: '' },
  size: { type: Number, default: 200 }
})

const canvasRef = ref(null)
const loading = ref(false)
const error = ref('')

function drawQR(text) {
  const canvas = canvasRef.value
  if (!canvas || !text) return

  loading.value = true
  error.value = ''

  const ctx = canvas.getContext('2d')
  const size = props.size

  // Очистка
  ctx.clearRect(0, 0, size, size)

  // Простая визуализация на основе хеша (заглушка — заменить на реальную QR генерацию)
  const cellSize = 4
  const cells = Math.floor(size / cellSize)
  const hash = simpleHash(text)

  ctx.fillStyle = '#ffffff'
  ctx.fillRect(0, 0, size, size)

  ctx.fillStyle = '#000000'

  // Позиционные маркеры (углы)
  drawFinderPattern(ctx, 0, 0, cellSize)
  drawFinderPattern(ctx, (cells - 7) * cellSize, 0, cellSize)
  drawFinderPattern(ctx, 0, (cells - 7) * cellSize, cellSize)

  // Данные (псевдо)
  for (let y = 0; y < cells; y++) {
    for (let x = 0; x < cells; x++) {
      const val = (hash * (x + 1) * (y + 1)) % 100
      if (val > 50 && !isFinderArea(x, y, cells)) {
        ctx.fillRect(x * cellSize, y * cellSize, cellSize, cellSize)
      }
    }
  }

  loading.value = false
}

function drawFinderPattern(ctx, x, y, cell) {
  // Внешний
  ctx.fillStyle = '#000000'
  ctx.fillRect(x, y, cell * 7, cell * 7)
  // Белый
  ctx.fillStyle = '#ffffff'
  ctx.fillRect(x + cell, y + cell, cell * 5, cell * 5)
  // Внутренний
  ctx.fillStyle = '#000000'
  ctx.fillRect(x + cell * 2, y + cell * 2, cell * 3, cell * 3)
}

function isFinderArea(x, y, cells) {
  const margin = 8
  if (x < margin && y < margin) return true
  if (x >= cells - margin && y < margin) return true
  if (x < margin && y >= cells - margin) return true
  return false
}

function simpleHash(str) {
  let hash = 0
  for (let i = 0; i < str.length; i++) {
    const char = str.charCodeAt(i)
    hash = ((hash << 5) - hash) + char
    hash |= 0
  }
  return Math.abs(hash)
}

watch(() => props.data, (val) => { if (val) drawQR(val) }, { immediate: true })
</script>

<style scoped>
.qr-preview {
  display: inline-block;
  position: relative;
}

.qr-canvas {
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-base);
  background: #fff;
}

.qr-loading {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.9);
  border-radius: var(--radius-md);
  gap: var(--space-2);
  font-size: var(--font-size-sm);
  color: var(--color-black);
}

.spinner {
  width: 24px;
  height: 24px;
  border: 3px solid var(--color-pale-blue);
  border-top-color: var(--color-main-blue);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.qr-error {
  margin-top: var(--space-2);
  font-size: var(--font-size-xs);
  color: var(--color-red);
}
</style>
