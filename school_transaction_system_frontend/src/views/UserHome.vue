<template>
  <div class="home-page">
    <el-card class="welcome-card">
      <h2>欢迎来到校园二手交易平台</h2>
      <p>在这里你可以浏览、发布和购买校园二手商品</p>
    </el-card>

    <div class="actions">
      <el-button type="primary" @click="goToMarket">进入商品市场</el-button>
      <el-button @click="goToPost">发布新商品</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goToMarket = () => {
  router.push('/market') // 商品市场页面（后续开发）
}

const goToPost = () => {
  router.push('/post') // 发布页面（后续开发）
}
</script>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.welcome-card {
  width: 100%;
  max-width: 600px;
  text-align: center;
  margin-bottom: 40px;
  padding: 30px;
}

.actions {
  display: flex;
  gap: 20px;
}
</style>
