import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3001, // 确保端口一致
    proxy: {
      '/backend': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        secure: false,
        configure: (proxy, options) => {
          proxy.on('error', (err, req, res) => {
            console.log('proxy error', err);
          });
          proxy.on('proxyReq', (proxyReq, req, res) => {
            console.log('Sending Request:', req.method, req.url);
          });
          proxy.on('proxyRes', (proxyRes, req, res) => {
            console.log('Received Response from the Target:', proxyRes.statusCode, req.url);
          });
        }
      },
      '/ws-notification': {
        target: 'http://localhost:8080',
        ws: true,
        rewrite: (path) => path.replace(/^\/ws-notification/, '/ws-notification')
      }
    }
  },
  define: {
    'process.env': {},
    'global': 'window' // 关键修复：stompjs 依赖 global 变量，在某些 vite 环境中缺失会导致白屏
  }
})
