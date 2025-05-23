import { createRouter, createWebHistory } from 'vue-router'
import LoginRegister from '../views/LoginRegister.vue'
import UserHome from '../views/UserHome.vue'
import AdminPage from '../views/AdminPage.vue'
import ProductDetail from '../views/ProductDetail.vue'
import MyPosts from '../components/MyPosts.vue'
import MessageList from '../components/MessageList.vue'
import ChatWindow from '../components/ChatWindow.vue'
import UserProfile from '../components/UserProfile.vue' // 确保正确导入 UserProfile 组件

const routes = [
  { path: '/', component: LoginRegister, name: 'LoginRegister' },
  { path: '/userhome', component: UserHome, name: 'UserHome' },
  { path: '/product/:id', component: ProductDetail, name: 'ProductDetail' },
  { path: '/admin', component: AdminPage, name: 'AdminPage' },
  { path: '/userhome/myposts', component: MyPosts, name: 'MyPosts' },
  { path: '/messages', component: MessageList, name: 'MessageList' },
  {
    path: '/chat',
    name: 'Chat',
    component: ChatWindow,
    props: route => ({
      userId: Number(route.query.userId),
      username: route.query.username
    })
  },
  {
    path: '/admin-profile',
    name: 'AdminProfile',
    component: UserProfile
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
