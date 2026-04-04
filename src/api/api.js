const BASE_URL = import.meta.env.DEV
  ? ""
  : import.meta.env.VITE_API_BASE_URL || "";

/**
 * Универсальный запрос JSON
 */
async function request(method, path, { body, token, params } = {}) {
  const headers = { "Content-Type": "application/json" };
  if (token) headers["Authorization"] = `Bearer ${token}`;

  // Собираем query params
  let url = `${BASE_URL}${path}`;
  if (params) {
    const query = new URLSearchParams();
    Object.entries(params).forEach(([k, v]) => {
      if (v !== undefined && v !== null) query.append(k, String(v));
    });
    const qs = query.toString();
    if (qs) url += `?${qs}`;
  }

  const res = await fetch(url, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  if (!res.ok) {
    const payload = await res.json().catch(() => ({}));
    const err = new Error(payload.detail || `HTTP ${res.status}`);
    err.status = res.status;
    err.errors = payload.errors || null;
    throw err;
  }

  // 204 No Content
  if (res.status === 204) return null;

  return res.json();
}

/**
 * Загрузка файла (multipart/form-data)
 */
async function uploadFile(method, path, { file, token, params } = {}) {
  const headers = {};
  if (token) headers["Authorization"] = `Bearer ${token}`;

  const formData = new FormData();
  formData.append("file", file);

  let url = `${BASE_URL}${path}`;
  if (params) {
    const query = new URLSearchParams();
    Object.entries(params).forEach(([k, v]) => {
      if (v !== undefined && v !== null) query.append(k, String(v));
    });
    const qs = query.toString();
    if (qs) url += `?${qs}`;
  }

  const res = await fetch(url, {
    method,
    headers,
    body: formData,
  });

  if (!res.ok) {
    const payload = await res.json().catch(() => ({}));
    const err = new Error(payload.detail || `HTTP ${res.status}`);
    err.status = res.status;
    throw err;
  }

  return res.json();
}

/**
 * Запрос blob (скачивание файла)
 */
async function requestBlob(method, path, { body, token } = {}) {
  const headers = {};
  if (token) headers["Authorization"] = `Bearer ${token}`;
  if (body !== undefined) headers["Content-Type"] = "application/json";

  const res = await fetch(`${BASE_URL}${path}`, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  if (!res.ok) {
    const payload = await res.json().catch(() => ({}));
    const err = new Error(payload.detail || `HTTP ${res.status}`);
    err.status = res.status;
    throw err;
  }

  const filename = (res.headers.get("content-disposition") || "")
    .match(/filename="?([^"]+)"?/)?.[1] || "document.docx";
  const blob = await res.blob();
  return { blob, filename };
}

export const api = {
  // ===================== AUTH =====================

  /** POST /api/v1/auth/register */
  register: (data) =>
    request("POST", "/api/v1/auth/register", { body: data }),

  /** POST /api/v1/auth/login */
  login: (data) =>
    request("POST", "/api/v1/auth/login", { body: data }),

  /** POST /api/v1/auth/refresh */
  refresh: (refresh_token) =>
    request("POST", "/api/v1/auth/refresh", { body: { refresh_token } }),

  /** POST /api/v1/auth/logout */
  logout: (refresh_token, access_token) =>
    request("POST", "/api/v1/auth/logout", {
      body: { refresh_token },
      token: access_token,
    }),

  // ===================== DIPLOMAS =====================

  /** GET /api/v1/diplomas */
  getDiplomas: (params, token) =>
    request("GET", "/api/v1/diplomas", { params, token }),

  /** GET /api/v1/diplomas/:id */
  getDiploma: (id, token) =>
    request("GET", `/api/v1/diplomas/${id}`, { token }),

  /** POST /api/v1/diplomas */
  createDiploma: (data, token) =>
    request("POST", "/api/v1/diplomas", { body: data, token }),

  /** PATCH /api/v1/diplomas/:id */
  updateDiploma: (id, data, token) =>
    request("PATCH", `/api/v1/diplomas/${id}`, { body: data, token }),

  /** DELETE /api/v1/diplomas/:id */
  deleteDiploma: (id, token) =>
    request("DELETE", `/api/v1/diplomas/${id}`, { token }),

  // ===================== VERIFY =====================

  /** GET /api/v1/verify?serial=...&hash=... */
  verify: (params, token) =>
    request("GET", "/api/v1/verify", { params, token }),

  /** POST /api/v1/verify/bulk */
  verifyBulk: (serials, token) =>
    request("POST", "/api/v1/verify/bulk", { body: { serials }, token }),

  /** GET /api/v1/verify/share/:token */
  verifyShare: (token) =>
    request("GET", `/api/v1/verify/share/${token}`),

  // ===================== PUBLIC VERIFY =====================

  /** GET /api/v1/public/verify/:token */
  publicVerify: (token) =>
    request("GET", `/api/v1/public/verify/${token}`),

  // ===================== UPLOADS =====================

  /** POST /api/v1/uploads */
  uploadFile: (file, token) =>
    uploadFile("POST", "/api/v1/uploads", { file, token }),

  /** GET /api/v1/uploads */
  getUploads: (token) =>
    request("GET", "/api/v1/uploads", { token }),

  // ===================== API KEYS =====================

  /** POST /api/v1/api-keys */
  createApiKey: (token) =>
    request("POST", "/api/v1/api-keys", { token }),

  /** DELETE /api/v1/api-keys */
  deleteApiKey: (token) =>
    request("DELETE", "/api/v1/api-keys", { token }),

  /** GET /api/v1/api-keys/usage */
  getApiKeyUsage: (token) =>
    request("GET", "/api/v1/api-keys/usage", { token }),

  // ===================== SETTINGS =====================

  /** GET /api/v1/settings */
  getSettings: (token) =>
    request("GET", "/api/v1/settings", { token }),

  /** PUT /api/v1/settings */
  updateSettings: (data, token) =>
    request("PUT", "/api/v1/settings", { body: data, token }),

  // ===================== STATS =====================

  /** GET /api/v1/stats */
  getStats: (token) =>
    request("GET", "/api/v1/stats", { token }),

  // ===================== SHARE TOKENS =====================

  /** POST /api/v1/share-tokens */
  createShareToken: (data, token) =>
    request("POST", "/api/v1/share-tokens", { body: data, token }),

  /** GET /api/v1/share-tokens */
  getShareTokens: (token) =>
    request("GET", "/api/v1/share-tokens", { token }),

  /** DELETE /api/v1/share-tokens/:token */
  deleteShareToken: (shareToken, token) =>
    request("DELETE", `/api/v1/share-tokens/${shareToken}`, { token }),

  // ===================== VERIFICATION LOG =====================

  /** GET /api/v1/verification-log */
  getVerificationLog: (params, token) =>
    request("GET", "/api/v1/verification-log", { params, token }),
};
