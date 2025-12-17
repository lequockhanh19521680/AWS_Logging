import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  server: {
    proxy: {
      '/api/auth': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
      '/api/wallets': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
      '/api/transactions': {
        target: 'http://localhost:8083',
        changeOrigin: true,
      },
    }
  }
})
