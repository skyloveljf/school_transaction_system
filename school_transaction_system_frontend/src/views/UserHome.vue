<template>
  <div class="user-home">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="logo">校园二手交易平台</div>
      <el-button type="danger" @click="logout">退出登录</el-button>
    </el-header>

    <!-- 主体内容 -->
    <el-main>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="商品浏览" name="browse" />
        <el-tab-pane label="我的发布" name="myposts" />
        <el-tab-pane label="我的收藏" name="favorites" />
      </el-tabs>

      <div class="tab-content">
        <BrowseProducts v-if="activeTab === 'browse'" />
        <MyPosts v-if="activeTab === 'myposts'" />
        <Favorites v-if="activeTab === 'favorites'" />
      </div>
    </el-main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import BrowseProducts from '../components/BrowseProducts.vue'
import MyPosts from '../components/MyPosts.vue'
import Favorites from '../components/Favorites.vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeTab = ref('browse')

const logout = () => {
  // 清理登录状态
  router.push('/')
}
</script>

<style scoped>
.user-home {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  height: 60px;
  background-color: #409eff;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  font-size: 18px;
}

.el-main {
  flex: 1;
  padding: 20px;
  background-color: #f5f7fa;
}

.tab-content {
  margin-top: 20px;
}
</style>
