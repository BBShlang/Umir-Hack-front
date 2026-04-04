/**
 * useQR — генерация QR, TTL-таймер, отзыв ссылки
 */
import { ref, computed, onUnmounted } from 'vue'

const qrLinks = ref(new Map())
let timerInterval = null

export function useQR() {
  /**
   * Создать временную ссылку для проверки
   * @param {string} diplomaHash
   * @param {number} ttlDays - срок жизни в днях
   * @returns {string} URL
   */
  function createShareLink(diplomaHash, ttlDays = 7) {
    const token = generateToken()
    const expiresAt = new Date()
    expiresAt.setDate(expiresAt.getDate() + ttlDays)

    const linkData = {
      token,
      hash: diplomaHash,
      expiresAt: expiresAt.toISOString(),
      isActive: true
    }

    qrLinks.value.set(token, linkData)

    startTimer()

    return `${window.location.origin}/verify/share/${token}`
  }

  /**
   * Проверить валидность ссылки
   * @param {string} token
   * @returns {{ valid: boolean, data: object|null, reason: string }}
   */
  function verifyShareToken(token) {
    const data = qrLinks.value.get(token)
    if (!data) {
      return { valid: false, data: null, reason: 'Ссылка не найдена' }
    }

    if (!data.isActive) {
      return { valid: false, data: null, reason: 'Ссылка отозвана' }
    }

    if (new Date() > new Date(data.expiresAt)) {
      return { valid: false, data: null, reason: 'Срок действия истёк' }
    }

    return { valid: true, data, reason: '' }
  }

  /**
   * Отозвать ссылку
   * @param {string} token
   */
  function revokeLink(token) {
    const data = qrLinks.value.get(token)
    if (data) {
      data.isActive = false
      qrLinks.value.set(token, data)
    }
  }

  /**
   * Получить оставшееся время
   * @param {string} token
   * @returns {string|null}
   */
  function getTimeLeft(token) {
    const data = qrLinks.value.get(token)
    if (!data) return null

    const left = new Date(data.expiresAt) - new Date()
    if (left <= 0) return 'Истекло'

    const days = Math.floor(left / (1000 * 60 * 60 * 24))
    const hours = Math.floor((left % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))

    if (days > 0) return `${days} дн. ${hours} ч.`
    return `${hours} ч.`
  }

  function startTimer() {
    if (timerInterval) return
    timerInterval = setInterval(() => {
      // Таймер тикает — UI может подписаться на getTimeLeft
    }, 60000)
  }

  function generateToken() {
    return btoa(Date.now().toString() + Math.random().toString()).replace(/[^a-zA-Z0-9]/g, '').slice(0, 24)
  }

  onUnmounted(() => {
    if (timerInterval) clearInterval(timerInterval)
  })

  return {
    qrLinks,
    createShareLink,
    verifyShareToken,
    revokeLink,
    getTimeLeft
  }
}
