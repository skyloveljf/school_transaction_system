import { createRouter, createWebHistory } from 'vue-router'
import LoginRegister from '../views/LoginRegister.vue'
import UserHome from '../views/UserHome.vue'
import AdminPage from '../views/AdminPage.vue'
import ProductDetail from '../views/ProductDetail.vue'
import MyPosts from '../components/MyPosts.vue'
import MessageList from '../components/MessageList.vue'
import ChatWindow from '../components/ChatWindow.vue' // ✅ 新增导入聊天组件

const routes = [
  { path: '/', component: LoginRegister },
  { path: '/userhome', component: UserHome },
  { path: '/product/:id', component: ProductDetail },
  { path: '/admin', component: AdminPage },
  { path: '/userhome/myposts', component: MyPosts },
   { path: '/messages', component: MessageList } ,
  
  // ✅ 新增“私信卖家”页面路由
  {
    path: '/chat',
    name: 'Chat',
    component: ChatWindow,
    props: route => ({
      userId: Number(route.query.userId),
      username: route.query.username
    })
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
