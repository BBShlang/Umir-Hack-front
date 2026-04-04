<template>
  <div class="app-shell">
    <!-- Прогресс-бар переходов -->
    <div class="route-progress" :class="{ 'route-progress--active': isRouteLoading }" />

    <!-- Основной контент (каждый view сам рендерит Header/Footer) -->
    <main class="app-shell__main">
      <RouterView v-slot="{ Component }">
        <Transition name="page" mode="out-in">
          <component :is="Component" />
        </Transition>
      </RouterView>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from './composables/useAuth.js'

const router = useRouter()
const { init } = useAuth()

const isRouteLoading = ref(false)

router.beforeEach(() => {
  isRouteLoading.value = true
})

router.afterEach(() => {
  isRouteLoading.value = false
})

onMounted(() => {
  init()
})
</script>

<style scoped>
.app-shell {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: #f8fafc;
}

.app-shell__main {
  flex: 1;
}

/* ===== Прогресс-бар роутера ===== */
.route-progress {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  z-index: 9999;
  background: linear-gradient(90deg, #3b82f6, #6366f1);
  transform: scaleX(0);
  transform-origin: left center;
  opacity: 0;
  transition: opacity 200ms ease;
  pointer-events: none;
}

.route-progress--active {
  opacity: 1;
  animation: route-loading 0.9s ease-in-out forwards;
}

@keyframes route-loading {
  0%   { transform: scaleX(0);   }
  60%  { transform: scaleX(0.75); }
  100% { transform: scaleX(0.92); }
}

.page-enter-active {
  transition: opacity 200ms ease, transform 200ms ease;
}

.page-leave-active {
  transition: opacity 160ms ease, transform 160ms ease;
}

.page-enter-from {
  opacity: 0;
  transform: translateY(8px);
}

.page-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}
</style>
