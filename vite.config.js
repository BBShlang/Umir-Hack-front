import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd())

  return {
    plugins: [vue()],
    server: {
      host: true,
      port: 5173,
      allowedHosts: ['buoyantly-positive-prawn.cloudpub.ru'],
      proxy: {
        '/api': {
          target: env.VITE_API_BASE_URL || 'http://localhost:8081',
          changeOrigin: true,
          timeout: 120_000,
          proxyTimeout: 120_000,
          rewriteRequestHeaders: (headers) => {
            // Удаляем origin чтобы бэкенд не блокировал CORS
            delete headers.origin;
            delete headers.referer;
            return headers;
          },
        },
        '/v3': {
          target: env.VITE_API_BASE_URL || 'http://localhost:8081',
          changeOrigin: true,
          timeout: 120_000,
          proxyTimeout: 120_000,
          rewriteRequestHeaders: (headers) => {
            delete headers.origin;
            delete headers.referer;
            return headers;
          },
        },
        '/actuator': {
          target: env.VITE_API_BASE_URL || 'http://localhost:8081',
          changeOrigin: true,
        },
      },
    },
  }
})
