/**
 * useUpload — загрузка CSV/Excel файлов с данными дипломов
 * Реальный API бэкенда ДиплоРеестр
 */
import { ref } from 'vue'
import { api } from '../api/api.js'
import { useAuth } from './useAuth.js'

const progress = ref(0)
const uploading = ref(false)
const uploadError = ref(null)
const uploadResult = ref(null)

export function useUpload() {
  const { accessToken } = useAuth()

  /**
   * Загрузить файл CSV/XLSX на сервер
   * @param {File} file
   * @returns {Promise<object>} — результат загрузки
   */
  async function uploadFile(file) {
    uploading.value = true
    uploadError.value = null
    uploadResult.value = null
    progress.value = 0

    // Имитация прогресса (пока API не даёт реального прогресса)
    const progressInterval = setInterval(() => {
      if (progress.value < 90) {
        progress.value += 10
      }
    }, 200)

    try {
      const res = await api.uploadFile(file, accessToken.value)
      progress.value = 100
      uploadResult.value = res
      return res
    } catch (err) {
      uploadError.value = err.message
      throw err
    } finally {
      clearInterval(progressInterval)
      uploading.value = false
    }
  }

  /**
   * Получить историю загрузок
   * @returns {Promise<object[]>}
   */
  async function getUploads() {
    try {
      return await api.getUploads(accessToken.value)
    } catch (err) {
      uploadError.value = err.message
      throw err
    }
  }

  function reset() {
    progress.value = 0
    uploading.value = false
    uploadError.value = null
    uploadResult.value = null
  }

  return {
    progress,
    uploading,
    uploadError,
    uploadResult,
    uploadFile,
    getUploads,
    reset,
  }
}
