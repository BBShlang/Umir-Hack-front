/**
 * useUpload — парсинг CSV/Excel, валидация, прогресс загрузки
 */
import { ref } from 'vue'

const progress = ref(0)
const uploading = ref(false)
const uploadError = ref(null)
const uploadResult = ref(null)

export function useUpload() {
  /**
   * Распарсить CSV-текст
   * @param {string} text
   * @param {string} delimiter
   * @returns {{ headers: string[], rows: object[] }}
   */
  function parseCSV(text, delimiter = ',') {
    const lines = text.trim().split(/\r?\n/)
    if (lines.length < 2) {
      throw new Error('Файл пуст или содержит только заголовок')
    }

    const delim = lines[0].includes(';') ? ';' : delimiter
    const headers = lines[0].split(delim).map(h => h.trim().replace(/^"|"$/g, ''))
    const rows = []

    for (let i = 1; i < lines.length; i++) {
      const values = lines[i].split(delim).map(v => v.trim().replace(/^"|"$/g, ''))
      if (values.length !== headers.length) continue

      const row = {}
      headers.forEach((h, idx) => { row[h] = values[idx] })
      rows.push(row)
    }

    return { headers, rows }
  }

  /**
   * Валидация записей реестра
   * @param {object[]} rows
   * @returns {{ valid: object[], errors: string[] }}
   */
  function validateRegistry(rows) {
    const errors = []
    const valid = []

    rows.forEach((row, idx) => {
      const rowErrors = []

      if (!row['ФИО']?.trim()) rowErrors.push('Отсутствует ФИО')
      if (!row['Серийный номер']?.trim()) rowErrors.push('Отсутствует серийный номер')
      if (!row['Специальность']?.trim()) rowErrors.push('Отсутствует специальность')
      if (!row['Дата выдачи']?.trim()) rowErrors.push('Отсутствует дата выдачи')

      // Проверка формата серийного номера
      if (row['Серийный номер'] && !/^[A-Z0-9-]{6,20}$/i.test(row['Серийный номер'])) {
        rowErrors.push('Неверный формат серийного номера')
      }

      if (rowErrors.length) {
        errors.push(`Строка ${idx + 1}: ${rowErrors.join(', ')}`)
      } else {
        valid.push(row)
      }
    })

    return { valid, errors }
  }

  /**
   * Эмуляция загрузки файла на сервер
   * @param {File} file
   * @param {function} onProgress
   */
  async function uploadFile(file, onProgress) {
    uploading.value = true
    uploadError.value = null
    uploadResult.value = null
    progress.value = 0

    try {
      // Эмуляция прогресса
      for (let p = 0; p <= 100; p += 10) {
        await new Promise(r => setTimeout(r, 200))
        progress.value = p
        if (onProgress) onProgress(p)
      }

      uploadResult.value = {
        success: true,
        uploaded: 1,
        total: 1,
        fileName: file.name
      }

      return uploadResult.value
    } catch (err) {
      uploadError.value = err.message || 'Ошибка загрузки'
      throw err
    } finally {
      uploading.value = false
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
    parseCSV,
    validateRegistry,
    uploadFile,
    reset
  }
}
