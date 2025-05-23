<template>
  <div class="user-home">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="left-section">
        <div class="logo">🎓 校园二手交易平台</div>
      </div>

      <div class="right-section">
        <el-button type="text" @click="goToMessages" class="message-btn" aria-label="私信">
          <el-icon><ChatDotRound /></el-icon>
          <el-badge is-dot v-if="unreadCount > 0" class="badge"></el-badge>
        </el-button>

        <el-avatar
          :size="36"
          :src="userAvatarUrl"
          class="avatar"
        />
        <span class="welcome">{{ welcomeMessage }}</span>
        <el-button type="primary" plain @click="logout" class="logout-btn">退出登录</el-button>
      </div>
    </el-header>

    <!-- 主体内容 -->
    <el-main>
      <div class="tab-container">
        <el-tabs v-model="activeTab" class="custom-tabs" stretch>
          <el-tab-pane name="browse" label="商品浏览">
            <template #label>
              <el-icon><ShoppingCart /></el-icon> 商品浏览
            </template>
            <BrowseProducts v-if="activeTab === 'browse'" />
          </el-tab-pane>

          <el-tab-pane name="myposts" label="我的发布">
            <template #label>
              <el-icon><Edit /></el-icon> 我的发布
            </template>
            <MyPosts v-if="activeTab === 'myposts'" />
          </el-tab-pane>

          <el-tab-pane name="favorites" label="我的收藏">
            <template #label>
              <el-icon><Star /></el-icon> 我的收藏
            </template>
            <Favorites v-if="activeTab === 'favorites'" />
          </el-tab-pane>

          <el-tab-pane name="profile" label="个人信息">
            <template #label>
              <el-icon><User /></el-icon> 个人信息
            </template>
            <UserProfile v-if="activeTab === 'profile'" />
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-main>

    <!-- 页脚 -->
    <el-footer class="footer">
      © 2025 校园二手交易平台 |
      <a href="https://github.com/skyloveljf/school_transaction_system" target="_blank">关于我们</a> |
      <a href="mailto:codingsky899@gmail.com" target="_blank">联系方式</a>
    </el-footer>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import authService from '../services/authService'

import BrowseProducts from '../components/BrowseProducts.vue'
import MyPosts from '../components/MyPosts.vue'
import Favorites from '../components/Favorites.vue'
import UserProfile from '../components/UserProfile.vue'

import { ShoppingCart, Edit, Star, User, ChatDotRound } from '@element-plus/icons-vue'
import { getImageUrl } from '../utils/imageHelper'

const activeTab = ref('browse')
const unreadCount = ref(2) // 模拟未读消息数，真实应从后端接口获取
const router = useRouter()

// 监听选项卡变化，确保切换到"我的发布"选项卡时重新加载数据
watch(activeTab, (newTab) => {
  console.log('Tab changed to:', newTab);
});

// 获取用户信息并显示
let userNameDisplay = '您' // Default display name
let userAvatar = ''
const userProfileString = localStorage.getItem('userProfileDto')
if (userProfileString) {
  try {
    const userProfile = JSON.parse(userProfileString)
    if (userProfile && userProfile.username) {
      userNameDisplay = userProfile.username
    }
    if (userProfile && userProfile.avatarUrl) {
      userAvatar = userProfile.avatarUrl
    }
  } catch (e) {
    console.error('Failed to parse userProfile from localStorage:', e)
  }
}
const welcomeMessage = ref(`欢迎，${userNameDisplay}`)

// 计算用户头像URL，使用默认头像作为备选
const userAvatarUrl = computed(() => {
  return userAvatar ? getImageUrl(userAvatar) : '/default-placeholder.png'
})

const logout = () => {
  authService.clearLoginInfo()
  router.push('/')
}
const goToMessages = () => {
  // 检查用户是否已登录
  if (!authService.isLoggedIn()) {
    ElMessage.warning('请先登录后查看私信');
    return;
  }
  
  console.log('跳转到消息中心');
  router.push('/messages');
}
</script>

<style scoped>
.user-home {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #e0f7fa, #ffffff);
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue",
    Arial, sans-serif;
}

.header {
  height: 64px;
  background: linear-gradient(90deg, #3a8ee6, #5ba5f7);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px;
  font-size: 20px;
  box-shadow: 0 4px 12px rgb(58 142 230 / 0.4);
  font-weight: 600;
}

.left-section {
  display: flex;
  align-items: center;
}

.right-section {
  display: flex;
  align-items: center;
}

.logo {
  font-weight: 700;
  font-size: 22px;
  user-select: none;
}

/* 顶部按钮悬浮动画 */
.message-btn {
  position: relative;
  margin-right: 20px;
  color: white;
  font-size: 20px;
  transition: color 0.3s ease, transform 0.3s ease;
  cursor: pointer;
}
.message-btn:hover {
  color: #66b1ff;
  transform: scale(1.15);
}

/* 未读红点位置 */
.badge {
  position: absolute !important;
  top: 4px;
  right: 4px;
  pointer-events: none;
}

/* 头像悬浮动画 */
.avatar {
  margin-right: 12px;
  cursor: pointer;
  border: 2px solid white;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  will-change: transform;
}
.avatar:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.5);
}

.welcome {
  font-size: 17px;
  font-weight: 500;
  color: #f0f5ff;
  margin-right: 20px;
  user-select: none;
}

/* 退出按钮点击缩放 */
.logout-btn {
  border-radius: 20px;
  padding: 6px 20px;
  font-weight: 600;
  transition: transform 0.2s ease;
  cursor: pointer;
}
.logout-btn:active {
  transform: scale(0.9);
}

/* 主体内容 */
.el-main {
  flex: 1;
  padding: 50px 40px 60px;
  background-color: #f9fbfd;
  overflow-y: auto;
}

.tab-container {
  max-width: 1100px;
  margin: 0 auto;
}

.custom-tabs {
  margin-bottom: 25px;
  font-weight: 600;
  --el-tabs-bar-color: #409eff;
  --el-tabs-text-color: #606266;
  --el-tabs-text-color-active: #409eff;
}

/* 直接应用于el-tab-pane内部内容 */
.el-tabs__content {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 8px 20px rgb(0 0 0 / 0.06);
  padding: 30px 30px 40px;
  min-height: 400px;
  transition: box-shadow 0.3s ease, transform 0.3s ease;
  will-change: transform;
}
.el-tabs__content:hover {
  box-shadow: 0 14px 30px rgb(0 0 0 / 0.12);
  transform: translateY(-8px);
}

/* 页脚 */
.footer {
  height: 60px;
  background-color: #f0f3fa;
  color: #8c98a4;
  text-align: center;
  line-height: 60px;
  font-size: 14px;
  border-top: 1px solid #d8dde6;
  user-select: none;
}

.footer a {
  color: #409eff;
  margin: 0 12px;
  text-decoration: none;
  transition: color 0.3s ease;
}
.footer a:hover {
  color: #66b1ff;
  text-decoration: underline;
}

/* Tab 切换动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.4s ease;
}
.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(20px);
}
.fade-slide-enter-to {
  opacity: 1;
  transform: translateY(0);
}
.fade-slide-leave-from {
  opacity: 1;
  transform: translateY(0);
}
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}
</style>
