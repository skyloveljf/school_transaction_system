import { createRouter, createWebHistory } from 'vue-router'
import LoginRegister from '../views/LoginRegister.vue'
import HomePage from '../views/UserHome.vue'
import AdminPage from '../views/AdminPage.vue'
import UserHome from '@/views/UserHome.vue'

const routes = [
  {
    path: '/',
    name: 'LoginRegister',
    component: LoginRegister
  },
  {
    path: '/home',
    name: 'UserHome',
    component: UserHome
  },
  {
    path: '/admin',
    name: 'AdminPage',
    component: AdminPage
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
