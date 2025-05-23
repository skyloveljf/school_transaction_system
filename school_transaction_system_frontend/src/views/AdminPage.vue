<template>
  <div class="admin-page-container">
    <el-header class="admin-header-bar">
      <div class="header-title">
        <el-icon :size="28" style="margin-right: 10px;"><Platform /></el-icon>
        <span>校园二手交易管理后台</span>
      </div>
      <div class="header-actions">
        <el-dropdown @command="handleDropdownCommand">
          <span class="el-dropdown-link">
            <el-avatar :size="32" :icon="UserFilled" style="margin-right: 8px; background-color: #c0c4cc;"/>
            <span>管理员</span>
            <el-icon class="el-icon--right"><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="settings">设置</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <el-main class="admin-main-content">
      <div class="admin-panels">
        <el-card shadow="hover" class="admin-card" @click="openUserDialog">
          <div class="card-content">
            <el-icon :size="40" class="card-icon user-icon"><User /></el-icon>
            <div class="info">
              <div class="title">用户管理</div>
              <div class="desc">查看、编辑、删除用户</div>
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" class="admin-card" @click="openProductDialog">
          <div class="card-content">
            <el-icon :size="40" class="card-icon product-icon"><Goods /></el-icon>
            <div class="info">
              <div class="title">商品管理</div>
              <div class="desc">审核、上下架、删除商品</div>
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" class="admin-card" @click="openCommentDialog">
          <div class="card-content">
            <el-icon :size="40" class="card-icon comment-icon"><ChatDotSquare /></el-icon>
            <div class="info">
              <div class="title">评论管理</div>
              <div class="desc">查看、删除用户评论</div>
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" class="admin-card" @click="openStatsDialog">
          <div class="card-content">
            <el-icon :size="40" class="card-icon stats-icon"><DataAnalysis /></el-icon>
            <div class="info">
              <div class="title">数据统计</div>
              <div class="desc">平台关键数据一览</div>
            </div>
          </div>
        </el-card>
      </div>
    </el-main>

    <AdminUserManage ref="userManageRef" />
    <AdminProductManage ref="productManageRef" />
    <AdminCommentManage ref="commentManageRef" />
    <AdminStats ref="statsRef" />
    <AdminSettingsDialog ref="adminSettingsDialogRef" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  User,
  Goods,
  ChatDotSquare,
  DataAnalysis,
  Platform,
  UserFilled,
  ArrowDown
} from '@element-plus/icons-vue'
import AdminUserManage from '../components/AdminUserManage.vue'
import AdminProductManage from '../components/AdminProductManage.vue'
import AdminCommentManage from '../components/AdminCommentManage.vue'
import AdminStats from '../components/AdminStats.vue'
import AdminSettingsDialog from '../components/AdminSettingsDialog.vue'

const router = useRouter()

const userManageRef = ref(null)
const productManageRef = ref(null)
const commentManageRef = ref(null)
const statsRef = ref(null)
const adminSettingsDialogRef = ref(null)

const openUserDialog = () => {
  if (userManageRef.value) userManageRef.value.visible = true
}
const openProductDialog = () => {
  if (productManageRef.value) productManageRef.value.visible = true
}
const openCommentDialog = () => {
  if (commentManageRef.value) commentManageRef.value.visible = true
}
const openStatsDialog = () => {
  if (statsRef.value) {
    statsRef.value.fetchData?.()
    statsRef.value.visible = true
  }
}

const handleDropdownCommand = (command) => {
  if (command === 'profile') {
    router.push('/admin-profile') // This will now point to UserProfile.vue
  } else if (command === 'settings') {
    if (adminSettingsDialogRef.value) {
      adminSettingsDialogRef.value.visible = true
    }
  } else if (command === 'logout') {
    // Add your actual logout logic here (e.g., clear token, reset store)
    console.log('执行退出登录')
    localStorage.removeItem('userInfo') // Example: remove user info
    router.push('/')
  }
}
</script>

<style scoped>
.admin-page-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f0f2f5;
  animation: fadeInPage 0.7s ease-out; /* 新增页面淡入动画 */
}

@keyframes fadeInPage { /* 定义页面淡入动画 */
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.admin-header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  height: 60px; /* 可以调整为 64px 或与 Element Plus 默认更一致的高度 */
  background-color: #ffffff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  /* border-bottom: 1px solid #e8e8e8; */ /* 阴影足够时，底部边框可以省略 */
  z-index: 10; /* 确保header在内容之上 */
}

.header-title {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.header-actions {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  color: #303133;
  padding: 8px; /* 增加点击区域和视觉舒适度 */
  border-radius: 6px; /* 轻微圆角 */
  transition: background-color 0.2s ease;
}
.el-dropdown-link:hover {
  background-color: #f5f7fa; /* Element Plus hover 背景色 */
  color: var(--el-color-primary);
}

.admin-main-content {
  flex-grow: 1;
  padding: 24px;
}

.admin-panels {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.admin-card {
  border-radius: 16px; /* 调整圆角与其他界面更一致 */
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1); /* 更平滑的过渡效果 */
  background-color: #fff;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06); /* 调整初始阴影，更柔和 */
}

.admin-card:hover {
  transform: translateY(-6px) scale(1.015); /* 调整悬浮效果 */
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); /* 调整悬浮阴影 */
}

.card-content {
  display: flex;
  align-items: center;
  padding: 24px; /* 稍微增加内边距 */
}

.card-icon {
  margin-right: 20px;
  padding: 14px; /* 调整图标内边距 */
  border-radius: 50%;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1); /* 给图标背景一个小阴影 */
}

/* 图标颜色保持不变，功能性区分 */
.user-icon { background-color: #409EFF; }
.product-icon { background-color: #67C23A; }
.comment-icon { background-color: #E6A23C; }
.stats-icon { background-color: #F56C6C; }

.info {
  flex-grow: 1;
}

.title {
  font-size: 1.2rem; /* 稍微增大标题字号 */
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px; /* 调整间距 */
}

.desc {
  font-size: 0.9rem; /* 稍微增大描述字号 */
  color: #5f6368; /* 调整描述文字颜色，更柔和 */
  line-height: 1.4;
}
</style>
