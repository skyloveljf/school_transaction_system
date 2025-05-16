// src/main.js
import { createApp } from 'vue'
import App from './App.vue'
import router from './router' // 这里默认加载 ./router/index.js

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)

app.use(router)
console.log(router.getRoutes().map(r => r.path))

app.use(ElementPlus)

app.mount('#app')
