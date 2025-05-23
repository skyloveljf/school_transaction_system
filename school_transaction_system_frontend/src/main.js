// src/main.js
import { createApp } from 'vue'
import App from './App.vue'
import router from './router' // 假设你已配置 router
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue' // 导入所有图标

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component) // 全局注册图标
}

app.use(router)
app.use(ElementPlus)
app.mount('#app')
