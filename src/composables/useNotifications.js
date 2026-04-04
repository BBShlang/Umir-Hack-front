/**
 * useNotifications — тосты, ошибки, уведомления системы
 */
import { ref } from 'vue'

const notifications = ref([])
let nextId = 1

export function useNotifications() {
  /**
   * Добавить уведомление
   * @param {'success'|'error'|'warning'|'info'} type
   * @param {string} message
   * @param {number} [timeout=4000]
   */
  function add(type, message, timeout = 4000) {
    const id = nextId++
    const notification = { id, type, message }
    notifications.value.push(notification)

    if (timeout > 0) {
      setTimeout(() => remove(id), timeout)
    }

    return id
  }

  function success(message, timeout) {
    return add('success', message, timeout)
  }

  function error(message, timeout) {
    return add('error', message, timeout || 0) // ошибки не исчезают автоматически
  }

  function warning(message, timeout) {
    return add('warning', message, timeout)
  }

  function info(message, timeout) {
    return add('info', message, timeout)
  }

  function remove(id) {
    const idx = notifications.value.findIndex(n => n.id === id)
    if (idx !== -1) {
      notifications.value.splice(idx, 1)
    }
  }

  function clearAll() {
    notifications.value = []
  }

  return {
    notifications,
    add,
    success,
    error,
    warning,
    info,
    remove,
    clearAll
  }
}
