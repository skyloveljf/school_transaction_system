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
  server: { // <-- ��� server ���ÿ�
    proxy: {
      // ������ /api ��ͷ�����������˷���
      '/api': {
        target: 'http://localhost:8080', // ��˷����ַ�Ͷ˿�
        changeOrigin: true, // ��������Ϊ true
        // ������ĺ�� API ·���б������� /api (����ֱ���� /auth/login)
        // ��ǰ������д���� /api/auth/login, ��������Ҫ rewrite:
        // rewrite: (path) => path.replace(/^\/api/, '') 
        // �������� UserController.java (@RequestMapping("/api/users")), ���ĺ��·���ǰ��� /api �ģ�����ͨ������Ҫ rewrite
      }
    }
  }
})
