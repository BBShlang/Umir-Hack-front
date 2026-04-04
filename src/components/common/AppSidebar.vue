<template>
  <aside class="app-sidebar" :class="{ collapsed: isCollapsed }">
    <nav class="sidebar-nav">
      <ul class="nav-list">
        <li v-for="item in navItems" :key="item.path" class="nav-item">
          <router-link
            :to="item.path"
            class="nav-link"
            :class="{ active: $route.path === item.path }"
            :title="item.label"
          >
            <component v-if="item.icon" :is="item.icon" class="nav-icon" />
            <span v-show="!isCollapsed" class="nav-label">{{ item.label }}</span>
          </router-link>
        </li>
      </ul>
    </nav>
    <button class="collapse-btn" @click="isCollapsed = !isCollapsed">
      {{ isCollapsed ? '→' : '←' }}
    </button>
  </aside>
</template>

<script setup>
import { ref } from 'vue'

/**
 * AppSidebar — боковая панель навигации
 * @prop {Array} navItems - элементы навигации [{ path, label, icon }]
 */
const props = defineProps({
  navItems: {
    type: Array,
    default: () => []
  }
})

const isCollapsed = ref(false)
</script>

<style scoped>
.app-sidebar {
  width: 240px;
  background: #fff;
  border-right: 1px solid var(--color-gray-blue);
  padding: var(--space-4) 0;
  transition: width var(--transition-base);
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - 64px);
}

.app-sidebar.collapsed {
  width: 64px;
}

.nav-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3) var(--space-5);
  color: var(--color-black);
  text-decoration: none;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  transition: all var(--transition-fast);
  border-left: 3px solid transparent;
}

.nav-link:hover {
  background: var(--color-pale-blue);
  color: var(--color-main-blue);
}

.nav-link.active {
  background: var(--color-pale-blue);
  color: var(--color-main-blue);
  border-left-color: var(--color-main-blue);
}

.nav-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.collapse-btn {
  margin-top: auto;
  padding: var(--space-2);
  background: none;
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-base);
  cursor: pointer;
  font-size: var(--font-size-md);
  color: var(--color-pale-black);
  transition: all var(--transition-fast);
  margin: 0 var(--space-4);
}

.collapse-btn:hover {
  background: var(--color-pale-blue);
  color: var(--color-main-blue);
}
</style>
