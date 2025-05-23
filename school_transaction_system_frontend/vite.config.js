import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: { // <-- 添加 server 配置块
    proxy: {
      // 将所有 /api 开头的请求代理到后端服务
      '/api': {
        target: 'http://localhost:8080', // 后端服务地址和端口
        changeOrigin: true, // 必须设置为 true
        // 如果您的后端 API 路径中本身不包含 /api (例如直接是 /auth/login)
        // 而前端请求写的是 /api/auth/login, 您可能需要 rewrite:
        // rewrite: (path) => path.replace(/^\/api/, '') 
        // 根据您的 UserController.java (@RequestMapping("/api/users")), 您的后端路径是包含 /api 的，所以通常不需要 rewrite
      }
    }
  }
})
