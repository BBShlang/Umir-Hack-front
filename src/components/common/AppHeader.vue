<template>
  <header class="app-header">
    <div class="header-content">
      <div class="header-left">
        <slot name="logo">
          <span class="logo">ДиплоРеестр</span>
        </slot>
      </div>
      <div class="header-center">
        <slot name="navigation"></slot>
      </div>
      <div class="header-right">
        <slot name="user-menu"></slot>
      </div>
    </div>
  </header>
</template>

<script setup>
/**
 * AppHeader — верхняя шапка приложения
 * Использует слоты для кастомизации контента
 */
</script>

<style scoped>
.app-header {
  background: #0d1f3c;
  color: #fff;
  padding: 0 var(--space-6);
  height: 64px;
  display: flex;
  align-items: center;
  box-shadow: var(--shadow-md);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  max-width: var(--container-max);
  margin: 0 auto;
}

.logo {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  letter-spacing: 0.02em;
  color: #fff;
}
</style>
