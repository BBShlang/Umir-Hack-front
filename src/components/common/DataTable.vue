<template>
  <div class="data-table">
    <div class="table-header" v-if="$slots.toolbar || title">
      <h3 v-if="title" class="table-title">{{ title }}</h3>
      <div class="table-toolbar">
        <slot name="toolbar" />
      </div>
    </div>

    <div class="table-wrapper">
      <table class="table">
        <thead>
          <tr>
            <th v-if="selectable" class="col-select">
              <input type="checkbox" :checked="allSelected" @change="toggleAll" />
            </th>
            <th v-for="col in columns" :key="col.key" :class="`col-${col.key}`">
              <button
                v-if="col.sortable"
                class="sort-btn"
                @click="onSort(col.key)"
              >
                {{ col.label }}
                <span class="sort-icon">{{ getSortIcon(col.key) }}</span>
              </button>
              <span v-else>{{ col.label }}</span>
            </th>
            <th v-if="$slots.actions" class="col-actions">Действия</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading" class="row-loading">
            <td :colspan="colspan">
              <div class="loading-indicator">Загрузка...</div>
            </td>
          </tr>
          <tr v-else-if="!items.length" class="row-empty">
            <td :colspan="colspan">
              <div class="empty-state">Нет данных</div>
            </td>
          </tr>
          <tr v-for="(item, idx) in items" :key="item.id || idx" class="data-row">
            <td v-if="selectable" class="col-select">
              <input type="checkbox" :checked="isSelected(item)" @change="toggleSelect(item)" />
            </td>
            <td v-for="col in columns" :key="col.key" :class="`col-${col.key}`">
              <slot :name="`cell-${col.key}`" :item="item" :value="item[col.key]">
                {{ item[col.key] }}
              </slot>
            </td>
            <td v-if="$slots.actions" class="col-actions">
              <slot name="actions" :item="item" />
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="pagination" class="table-pagination">
      <button :disabled="pagination.page <= 1" @click="onPage(pagination.page - 1)">←</button>
      <span class="page-info">
        Стр. {{ pagination.page }} из {{ totalPages }} ({{ pagination.total }})
      </span>
      <button :disabled="pagination.page >= totalPages" @click="onPage(pagination.page + 1)">→</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

/**
 * DataTable — таблица с сортировкой, выбором, пагинацией
 * @prop {String} title
 * @prop {Array} columns - [{ key, label, sortable }]
 * @prop {Array} items - данные
 * @prop {Boolean} selectable
 * @prop {Boolean} loading
 * @prop {Object} pagination - { page, pageSize, total }
 */
const props = defineProps({
  title: { type: String, default: '' },
  columns: { type: Array, default: () => [] },
  items: { type: Array, default: () => [] },
  selectable: { type: Boolean, default: false },
  loading: { type: Boolean, default: false },
  pagination: { type: Object, default: null }
})

const emit = defineEmits(['sort', 'page', 'selection-change'])

const sortKey = ref('')
const sortDir = ref('asc')
const selected = ref(new Set())

const colspan = computed(() => {
  let c = props.columns.length
  if (props.selectable) c++
  if (props.$slots?.actions) c++
  return c
})

const allSelected = computed(() => props.items.length > 0 && selected.value.size === props.items.length)
const totalPages = computed(() => props.pagination ? Math.ceil(props.pagination.total / props.pagination.pageSize) : 1)

function getSortIcon(key) {
  if (sortKey.value !== key) return ''
  return sortDir.value === 'asc' ? '↑' : '↓'
}

function onSort(key) {
  if (sortKey.value === key) {
    sortDir.value = sortDir.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortKey.value = key
    sortDir.value = 'asc'
  }
  emit('sort', { key, dir: sortDir.value })
}

function toggleAll() {
  if (allSelected.value) {
    selected.value.clear()
  } else {
    props.items.forEach(item => selected.value.add(item.id))
  }
  emit('selection-change', [...selected.value])
}

function isSelected(item) {
  return selected.value.has(item.id)
}

function toggleSelect(item) {
  if (selected.value.has(item.id)) {
    selected.value.delete(item.id)
  } else {
    selected.value.add(item.id)
  }
  emit('selection-change', [...selected.value])
}

function onPage(page) {
  emit('page', page)
}
</script>

<style scoped>
.data-table {
  border: 1px solid var(--color-gray-blue);
  border-radius: var(--radius-lg);
  overflow: hidden;
  background: #fff;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4);
  background: var(--color-pale-blue);
  border-bottom: 1px solid var(--color-gray-blue);
}

.table-title {
  margin: 0;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
}

.table-toolbar {
  display: flex;
  gap: var(--space-3);
}

.table-wrapper {
  overflow-x: auto;
}

.table {
  width: 100%;
  border-collapse: collapse;
}

.table th,
.table td {
  padding: var(--space-3) var(--space-4);
  text-align: left;
  border-bottom: 1px solid var(--color-gray-blue);
  font-size: var(--font-size-base);
}

.table th {
  background: #f5f8fc;
  font-weight: var(--font-weight-semibold);
  font-size: var(--font-size-xs);
  color: var(--color-pale-black);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.table tbody tr:hover {
  background: rgba(72, 184, 194, 0.04);
}

.sort-btn {
  background: none;
  border: none;
  cursor: pointer;
  font: inherit;
  color: inherit;
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

.col-select { width: 40px; }
.col-actions { width: 120px; text-align: right; }

.loading-indicator,
.empty-state {
  padding: var(--space-8);
  text-align: center;
  color: var(--color-pale-black);
}

.table-pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-4);
  padding: var(--space-4);
  border-top: 1px solid var(--color-gray-blue);
}

.table-pagination button {
  padding: var(--space-2) var(--space-4);
  border: 1px solid var(--color-gray-blue);
  background: #fff;
  border-radius: var(--radius-base);
  cursor: pointer;
  font-size: var(--font-size-sm);
  transition: all var(--transition-fast);
}

.table-pagination button:hover:not(:disabled) {
  background: var(--color-main-blue);
  color: #fff;
  border-color: var(--color-main-blue);
}

.table-pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: var(--font-size-sm);
  color: var(--color-pale-black);
}
</style>
