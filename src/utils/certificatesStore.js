/**
 * Локальное хранилище выпущенных дипломов (сертификатов).
 * Локальный кэш выпусков (POST /api/certificates): у ВУЗа в спецификации нет GET списка — таблица в ЛК строится из этого кэша.
 */
const KEY = 'dipregistry_certificates_v1'

export function loadAllCertificates() {
  try {
    const raw = localStorage.getItem(KEY)
    if (!raw) return []
    const parsed = JSON.parse(raw)
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

export function saveAllCertificates(list) {
  localStorage.setItem(KEY, JSON.stringify(list))
}

export function appendCertificate(rec) {
  const all = loadAllCertificates()
  all.unshift(rec)
  saveAllCertificates(all)
}

export function appendCertificates(records) {
  if (!Array.isArray(records) || records.length === 0) return
  const all = loadAllCertificates()
  all.unshift(...records)
  saveAllCertificates(all)
}

export function patchCertificate(id, patch) {
  const all = loadAllCertificates()
  const i = all.findIndex((c) => c.id === id)
  if (i === -1) return false
  all[i] = { ...all[i], ...patch }
  saveAllCertificates(all)
  return true
}

export function findCertificateByNumber(serial) {
  const q = String(serial || '').trim().toLowerCase()
  if (!q) return null
  return loadAllCertificates().find(
    (c) => String(c.serialNumber || '').toLowerCase() === q
  )
}

export function findCertificateById(id) {
  return loadAllCertificates().find((c) => c.id === id) || null
}
