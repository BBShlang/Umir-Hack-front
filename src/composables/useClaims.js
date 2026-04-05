/**
 * useClaims — заявки на привязку дипломов.
 * Студент: POST /api/student/diploma-claims
 * Вуз: GET /api/university/diploma-claims/pending + POST /api/university/diploma-claims/{requestId}/review
 */
import { ref } from 'vue'
import { api } from '../api/api.js'
import { useAuth } from './useAuth.js'
import {
  validatePublicSearchDiplomaNumberInput,
  isCertificateNotFoundError,
} from '../utils/publicDiplomaSearch.js'

export function useClaims() {
  const { token: accessToken } = useAuth()
  const loading = ref(false)
  const error = ref(null)

  const unclaimedDiploma = ref(null)
  const searchError = ref(null)

  /**
   * Поиск по номеру диплома; при переданном universityCode сверяем с ответом.
   */
  async function searchUnclaimed(diplomaNumber, universityCode) {
    loading.value = true
    searchError.value = null
    unclaimedDiploma.value = null
    try {
      const uc = String(universityCode || '').trim()
      const v = validatePublicSearchDiplomaNumberInput(diplomaNumber)
      if (!v.ok) {
        searchError.value = v.message
        return null
      }
      const dn = v.query

      const result = await api.publicCertificatesSearch({ diplomaNumber: dn })
      if (!result || typeof result !== 'object') return null

      if (
        uc &&
        String(result.universityCode || '').trim().toUpperCase() !== uc.toUpperCase()
      ) {
        searchError.value = 'Код университета не совпадает с записью в реестре'
        return null
      }

      unclaimedDiploma.value = result
      return result
    } catch (err) {
      unclaimedDiploma.value = null
      if (isCertificateNotFoundError(err)) return null
      searchError.value = err.message || 'Ошибка при поиске диплома'
      return null
    } finally {
      loading.value = false
    }
  }

  async function createClaim(diplomaNumber, universityCode) {
    const token = accessToken.value
    if (!token) throw new Error('Необходимо войти в систему')
    return api.createStudentDiplomaClaim(
      { diplomaNumber, universityCode },
      token
    )
  }

  const universityClaims = ref([])

  async function fetchUniversityClaims() {
    loading.value = true
    error.value = null
    try {
      const token = accessToken.value
      if (!token) {
        universityClaims.value = []
        return []
      }
      const data = await api.getUniversityPendingDiplomaClaims(token)
      universityClaims.value = Array.isArray(data) ? data : []
      return universityClaims.value
    } catch (err) {
      error.value = err.message || 'Ошибка загрузки заявок'
      universityClaims.value = []
      return []
    } finally {
      loading.value = false
    }
  }

  async function approveClaim(requestId) {
    const token = accessToken.value
    if (!token) throw new Error('Необходимо войти в систему')
    await api.reviewUniversityDiplomaClaim(requestId, { approve: true }, token)
    await fetchUniversityClaims()
  }

  async function rejectClaim(requestId, reviewComment) {
    const token = accessToken.value
    if (!token) throw new Error('Необходимо войти в систему')
    await api.reviewUniversityDiplomaClaim(requestId, { approve: false, reviewComment }, token)
    await fetchUniversityClaims()
  }

  const myClaims = ref([])

  async function fetchMyClaims() {
    myClaims.value = []
    return []
  }

  return {
    loading,
    error,
    unclaimedDiploma,
    searchError,
    universityClaims,
    myClaims,
    searchUnclaimed,
    createClaim,
    fetchUniversityClaims,
    approveClaim,
    rejectClaim,
    fetchMyClaims,
  }
}
