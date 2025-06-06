<template>
  <div class="admin-page-container">
    <el-header class="admin-header-bar">
      <div class="header-title">
        <el-icon :size="28" style="margin-right: 10px;"><Platform /></el-icon>
        <span>校园二手交易管理后台</span>
      </div>
      
      <div class="header-center">
        <WeatherWidget :compact="false" theme="light" />
      </div>
      
      <div class="header-actions">
        <el-dropdown @command="handleDropdownCommand">
          <span class="el-dropdown-link">
            <el-avatar :size="32" :icon="UserFilled" style="margin-right: 8px; background-color: #c0c4cc;"/>
            <span>管理员</span>
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
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
import authService from '../services/authService'
import AdminUserManage from '../components/AdminUserManage.vue'
import AdminProductManage from '../components/AdminProductManage.vue'
import AdminCommentManage from '../components/AdminCommentManage.vue'
import AdminStats from '../components/AdminStats.vue'
import AdminSettingsDialog from '../components/AdminSettingsDialog.vue'
import WeatherWidget from '../components/WeatherWidget.vue'

const router = useRouter()

const userManageRef = ref(null)
const productManageRef = ref(null)
const commentManageRef = ref(null)
const statsRef = ref(null)
const adminSettingsDialogRef = ref(null)

const openUserDialog = () => {
  if (userManageRef.value) {
    userManageRef.value.visible = true
  }
}
const openProductDialog = () => {
  if (productManageRef.value) {
    productManageRef.value.visible = true
  }
}
const openCommentDialog = () => {
  if (commentManageRef.value) {
    commentManageRef.value.visible = true
  }
}
const openStatsDialog = () => {
  if (statsRef.value) {
    statsRef.value.fetchData?.()
    statsRef.value.visible = true
  }
}

const handleDropdownCommand = (command) => {
  if (command === 'profile') {
    console.log('个人中心')
  } else if (command === 'settings') {
    if (adminSettingsDialogRef.value) {
      adminSettingsDialogRef.value.visible = true
    }
  } else if (command === 'logout') {
    authService.clearLoginInfo()
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
  animation: fadeInPage 0.7s ease-out;
}

@keyframes fadeInPage {
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
  height: 60px;
  background-color: #ffffff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 10;
}

.header-title {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.header-center {
  flex-grow: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 管理员页面天气组件样式 */
.header-center :deep(.weather-widget) {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 8px 12px;
  background-color: #f8f9fa;
  transition: all 0.3s ease;
}

.header-center :deep(.weather-widget:hover) {
  background-color: #ecf5ff;
  border-color: #b3d8ff;
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
  padding: 8px;
  border-radius: 6px;
  transition: background-color 0.2s ease;
}
.el-dropdown-link:hover {
  background-color: #f5f7fa;
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
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  background-color: #fff;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.admin-card:hover {
  transform: translateY(-6px) scale(1.015);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.card-content {
  display: flex;
  align-items: center;
  padding: 24px;
}

.card-icon {
  margin-right: 20px;
  padding: 14px;
  border-radius: 50%;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
}

.user-icon { background-color: #409EFF; }
.product-icon { background-color: #67C23A; }
.comment-icon { background-color: #E6A23C; }
.stats-icon { background-color: #F56C6C; }

.info {
  flex-grow: 1;
}

.title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.desc {
  font-size: 0.9rem;
  color: #5f6368;
  line-height: 1.4;
}
</style>
