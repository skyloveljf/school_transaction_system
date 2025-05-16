import { createRouter, createWebHistory } from 'vue-router'
import LoginRegister from '../views/LoginRegister.vue'
import UserHome from '../views/UserHome.vue'
import AdminPage from '../views/AdminPage.vue'
import ProductDetail from '../views/ProductDetail.vue'
import MyPosts from '../components/MyPosts.vue'
const routes = [
  { path: '/', component: LoginRegister },
  { path: '/userhome', component: UserHome },
  { path: '/product/:id', component: ProductDetail },
  { path: '/admin', component: AdminPage },
  { path: '/userhome/myposts', component: MyPosts }  // 这里新增路由
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
