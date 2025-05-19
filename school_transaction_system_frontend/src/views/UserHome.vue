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
          src="https://img.keaitupian.cn/uploads/upimg/1597372353577123.jpg"
          class="avatar"
        />
        <span class="welcome">欢迎，condingsky</span>
        <el-button type="primary" plain @click="logout" class="logout-btn">退出登录</el-button>
      </div>
    </el-header>

    <!-- 主体内容 -->
    <el-main>
      <div class="tab-container">
        <el-tabs v-model="activeTab" type="line" class="custom-tabs" stretch>
          <el-tab-pane name="browse">
            <template #label>
              <el-icon><ShoppingCart /></el-icon> 商品浏览
            </template>
          </el-tab-pane>

          <el-tab-pane name="myposts">
            <template #label>
              <el-icon><Edit /></el-icon> 我的发布
            </template>
          </el-tab-pane>

          <el-tab-pane name="favorites">
            <template #label>
              <el-icon><Star /></el-icon> 我的收藏
            </template>
          </el-tab-pane>

          <el-tab-pane name="profile">
            <template #label>
              <el-icon><User /></el-icon> 个人信息
            </template>
          </el-tab-pane>
        </el-tabs>

        <transition name="fade-slide" mode="out-in">
          <component :is="getCurrentTabComponent" :key="activeTab" class="tab-content card" />
        </transition>
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
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

import BrowseProducts from '../components/BrowseProducts.vue'
import MyPosts from '../components/MyPosts.vue'
import Favorites from '../components/Favorites.vue'
import UserProfile from '../components/UserProfile.vue'

import { ShoppingCart, Edit, Star, User, ChatDotRound } from '@element-plus/icons-vue'

const activeTab = ref('browse')
const unreadCount = ref(2) // 模拟未读消息数，真实应从后端接口获取
const router = useRouter()

const logout = () => {
  router.push('/')
}
const goToMessages = () => {
  router.push('/messages')
}

// 计算当前选中 tab 的组件
const getCurrentTabComponent = computed(() => {
  switch (activeTab.value) {
    case 'browse':
      return BrowseProducts
    case 'myposts':
      return MyPosts
    case 'favorites':
      return Favorites
    case 'profile':
      return UserProfile
  }
})
</script>

<style scoped>
.user-home {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #e0f7fa 0%, #fbc2eb 100%);
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue",
    Arial, sans-serif;
  animation: fadeInBg 1.2s;
}
@keyframes fadeInBg {
  from { opacity: 0; }
  to { opacity: 1; }
}

.header {
  height: 68px;
  background: linear-gradient(90deg, #3a8ee6 0%, #a1c4fd 100%);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 38px;
  font-size: 20px;
  box-shadow: 0 4px 18px rgb(58 142 230 / 0.18);
  font-weight: 600;
  border-bottom-left-radius: 18px;
  border-bottom-right-radius: 18px;
  position: sticky;
  top: 0;
  z-index: 10;
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
  font-weight: 800;
  font-size: 24px;
  user-select: none;
  letter-spacing: 2px;
  text-shadow: 0 2px 8px #a1c4fd66;
}

/* 顶部按钮悬浮动画 */
.message-btn {
  position: relative;
  margin-right: 20px;
  color: white;
  font-size: 22px;
  transition: color 0.3s, transform 0.3s;
  cursor: pointer;
  border-radius: 50%;
  background: rgba(255,255,255,0.08);
  padding: 6px 10px;
}
.message-btn:hover {
  color: #66b1ff;
  background: rgba(255,255,255,0.18);
  transform: scale(1.18);
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
  border: 2px solid #fff;
  transition: transform 0.3s, box-shadow 0.3s;
  will-change: transform;
  box-shadow: 0 2px 8px #a1c4fd55;
}
.avatar:hover {
  transform: scale(1.12);
  box-shadow: 0 6px 18px #a1c4fd99;
}

.welcome {
  font-size: 17px;
  font-weight: 500;
  color: #f0f5ff;
  margin-right: 20px;
  user-select: none;
  text-shadow: 0 1px 4px #3a8ee655;
}

/* 退出按钮点击缩放 */
.logout-btn {
  border-radius: 20px;
  padding: 7px 24px;
  font-weight: 700;
  font-size: 16px;
  transition: transform 0.2s, background 0.2s;
  cursor: pointer;
  background: linear-gradient(90deg, #a1c4fd 0%, #3a8ee6 100%);
  border: none;
  color: #fff;
  box-shadow: 0 2px 8px #a1c4fd44;
}
.logout-btn:hover {
  background: linear-gradient(90deg, #3a8ee6 0%, #a1c4fd 100%);
}
.logout-btn:active {
  transform: scale(0.93);
}

/* 主体内容 */
.el-main {
  flex: 1;
  padding: 56px 0 60px;
  background: transparent;
  overflow-y: auto;
}

.tab-container {
  max-width: 1100px;
  margin: 0 auto;
}

.custom-tabs {
  margin-bottom: 28px;
  font-weight: 700;
  --el-tabs-bar-color: #409eff;
  --el-tabs-text-color: #606266;
  --el-tabs-text-color-active: #409eff;
  --el-tabs-header-height: 48px;
  --el-tabs-active-bar-height: 4px;
}

.tab-content {
  background: rgba(255,255,255,0.98);
  border-radius: 18px;
  box-shadow: 0 8px 32px rgb(99 102 241 / 0.10);
  padding: 36px 36px 44px;
  min-height: 420px;
  transition: box-shadow 0.3s, transform 0.3s;
  will-change: transform;
  animation: cardPop 0.7s;
}
@keyframes cardPop {
  from { transform: scale(0.97);}
  to { transform: scale(1);}
}
.tab-content:hover {
  box-shadow: 0 14px 36px rgb(99 102 241 / 0.18);
  transform: translateY(-8px) scale(1.01);
}

/* 页脚 */
.footer {
  height: 60px;
  background: linear-gradient(90deg, #f0f3fa 0%, #e0e7ff 100%);
  color: #8c98a4;
  text-align: center;
  line-height: 60px;
  font-size: 15px;
  border-top: 1px solid #d8dde6;
  user-select: none;
  letter-spacing: 1px;
  border-top-left-radius: 18px;
  border-top-right-radius: 18px;
  box-shadow: 0 -2px 8px #e0e7ff44;
}

.footer a {
  color: #409eff;
  margin: 0 12px;
  text-decoration: none;
  transition: color 0.3s;
}
.footer a:hover {
  color: #66b1ff;
  text-decoration: underline;
}

/* Tab 切换动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.4s cubic-bezier(.55,0,.1,1);
}
.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(30px) scale(0.98);
}
.fade-slide-enter-to {
  opacity: 1;
  transform: translateY(0) scale(1);
}
.fade-slide-leave-from {
  opacity: 1;
  transform: translateY(0) scale(1);
}
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-30px) scale(0.98);
}
</style>
