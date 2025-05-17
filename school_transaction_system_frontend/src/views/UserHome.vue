<template>
  <div class="user-home">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="left-section">
        <div class="logo">🎓 校园二手交易平台</div>
      </div>

      <div class="right-section">
        <el-avatar
          :size="32"
          src="https://img.keaitupian.cn/uploads/upimg/1597372353577123.jpg"
          style="margin-right: 10px"
        />
        <span class="welcome">欢迎，codingsky</span>
        <el-button type="danger" plain @click="logout" style="margin-left: 20px;">退出登录</el-button>
      </div>
    </el-header>

    <!-- 主体内容 -->
    <el-main>
      <div class="tab-container">
        <el-tabs v-model="activeTab" type="card" class="custom-tabs">
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

        <div class="tab-content card">
          <BrowseProducts v-if="activeTab === 'browse'" />
          <MyPosts v-if="activeTab === 'myposts'" />
          <Favorites v-if="activeTab === 'favorites'" />
          <UserProfile v-if="activeTab === 'profile'" />
        </div>
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import BrowseProducts from '../components/BrowseProducts.vue'
import MyPosts from '../components/MyPosts.vue'
import Favorites from '../components/Favorites.vue'
import UserProfile from '../components/UserProfile.vue'

import { ShoppingCart, Edit, Star, User } from '@element-plus/icons-vue'

const activeTab = ref('browse')
const router = useRouter()

const logout = () => {
  router.push('/')
}
</script>

<style scoped>
.user-home {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #e0f7fa, #ffffff);
}

.header {
  height: 64px;
  background: linear-gradient(90deg, #409EFF, #66b1ff);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  font-size: 18px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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
  font-weight: bold;
  font-size: 20px;
  font-family: 'Segoe UI', sans-serif;
}

.welcome {
  font-size: 16px;
  margin-left: 5px;
}

.el-main {
  flex: 1;
  padding: 40px 60px;
}

.tab-container {
  max-width: 1000px;
  margin: 0 auto;
}

.custom-tabs {
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
}

.card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 30px;
}

.footer {
  height: 60px;
  background-color: #f2f6fc;
  color: #666;
  text-align: center;
  line-height: 60px;
  font-size: 14px;
  border-top: 1px solid #ddd;
}

.footer a {
  color: #409EFF;
  margin: 0 10px;
  text-decoration: none;
}

.footer a:hover {
  text-decoration: underline;
}
</style>
