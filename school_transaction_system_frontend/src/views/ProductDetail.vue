<template>
  <div class="detail-container">
    <el-card v-loading="loading" class="detail-card">
      <!-- 返回主页按钮 -->
      <el-button type="primary" plain @click="goHome" class="back-btn">
        返回主页
      </el-button>

      <!-- 商品信息 -->
      <div v-if="product" class="top-section">
        <div class="img-wrapper">
          <img :src="product.image" class="product-image" alt="商品图片" />
        </div>
        <div class="info-section">
          <h2 class="product-title">{{ product.title }}</h2>
          <p class="desc">{{ product.description }}</p>
          <p class="price">￥{{ product.price }}</p>
          <div class="btn-group">
            <el-button type="success" @click="handleMessage">私信卖家</el-button>
            <el-button
              :type="isFavorited ? 'warning' : 'primary'"
              @click="toggleFavorite"
              class="fav-btn"
            >
              {{ isFavorited ? '取消收藏' : '收藏商品' }}
            </el-button>
          </div>
        </div>
      </div>

      <div v-else class="not-found">
        <el-empty description="未找到该商品" />
        <el-button @click="goHome">返回首页</el-button>
      </div>

      <!-- 评论区 -->
      <el-divider v-if="product">用户评论</el-divider>

      <div v-if="product" class="comment-section">
        <el-form @submit.prevent="submitComment" class="comment-form">
          <el-form-item>
            <el-input
              v-model="commentForm.content"
              type="textarea"
              placeholder="写下你的评论..."
              :rows="3"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
          <el-button type="primary" @click="submitComment" class="comment-btn">提交评论</el-button>
        </el-form>

        <div class="comments">
          <transition-group name="fade-list" tag="div">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <div class="comment-header">
                <el-avatar
                  :size="28"
                  src="https://img.icons8.com/color/48/user-male-circle--v2.png"
                  class="comment-avatar"
                />
                <strong class="comment-username">{{ comment.username }}</strong>
                <span class="date">{{ formatDate(comment.createdAt) }}</span>
              </div>
              <p class="comment-content">{{ comment.content }}</p>
            </div>
          </transition-group>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { mockProducts, mockComments } from '../api/product'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const product = ref(null)
const comments = ref([])

const commentForm = ref({
  content: ''
})

const favorites = ref([])
const isFavorited = ref(false)

const loadFavorites = () => {
  favorites.value = JSON.parse(localStorage.getItem('favorites') || '[]')
  isFavorited.value = product.value && favorites.value.some(item => item.id === product.value.id)
}

const saveFavorites = () => {
  localStorage.setItem('favorites', JSON.stringify(favorites.value))
}

const toggleFavorite = () => {
  if (!product.value) return

  if (isFavorited.value) {
    favorites.value = favorites.value.filter(item => item.id !== product.value.id)
    ElMessage.success('已取消收藏')
  } else {
    favorites.value.push(product.value)
    ElMessage.success('已收藏该商品')
  }
  isFavorited.value = !isFavorited.value
  saveFavorites()
}

const loadProduct = async () => {
  loading.value = true
  try {
    const id = parseInt(route.params.id)
    product.value = mockProducts.find(p => p.id === id) || null

    if (product.value) {
      comments.value = mockComments.filter(c => c.productId === id)
    }
  } catch (error) {
    ElMessage.error('加载商品信息失败')
  } finally {
    loading.value = false
  }
}

watch(product, (newVal) => {
  if (newVal) {
    loadFavorites()
  }
})

onMounted(() => {
  loadProduct()
})

const submitComment = () => {
  if (!commentForm.value.content.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  const newComment = {
    id: Date.now(),
    productId: product.value.id,
    userId: 1,
    username: '当前用户',
    content: commentForm.value.content,
    createdAt: new Date().toISOString()
  }

  comments.value.unshift(newComment)
  commentForm.value.content = ''
  ElMessage.success('评论已提交')
}

const handleMessage = () => {
  router.push({
    path: '/chat',
    query: {
      userId: product.value.sellerId || 2,
      username: product.value.sellerName || '卖家'
    }
  })
}

// 返回首页方法
const goHome = () => {
  router.push('/userhome')
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString()
}
</script>

<style scoped>
.detail-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 32px 0 24px 0;
  background: linear-gradient(120deg, #f8fafc 0%, #e0e7ff 100%);
  border-radius: 22px;
  box-shadow: 0 8px 32px #c7d2fe33;
  min-height: 600px;
  animation: fadeInDetail 0.8s;
}
@keyframes fadeInDetail {
  from { opacity: 0; transform: translateY(30px);}
  to { opacity: 1; transform: none;}
}
.detail-card {
  padding: 32px 32px 24px 32px;
  border-radius: 18px;
  background: rgba(255,255,255,0.98);
  box-shadow: 0 4px 24px #a1c4fd33;
  border: 1.5px solid #e0e7ff;
}

.back-btn {
  margin-bottom: 20px;
  border-radius: 18px;
  font-weight: 600;
  letter-spacing: 1px;
  background: linear-gradient(90deg, #a1c4fd 0%, #c2e9fb 100%);
  border: none;
  color: #6366f1;
  transition: background 0.2s;
}
.back-btn:hover {
  background: linear-gradient(90deg, #6366f1 0%, #a1c4fd 100%);
  color: #fff;
}

.top-section {
  display: flex;
  gap: 36px;
  margin-bottom: 28px;
  align-items: flex-start;
}

.img-wrapper {
  flex-shrink: 0;
  background: linear-gradient(135deg, #e0e7ff 0%, #f8fafc 100%);
  border-radius: 14px;
  box-shadow: 0 2px 12px #a1c4fd33;
  padding: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-image {
  width: 300px;
  height: 300px;
  object-fit: cover;
  border-radius: 10px;
  box-shadow: 0 2px 12px #e0e7ff55;
  background: #fff;
}

.info-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

.product-title {
  font-size: 2.1rem;
  font-weight: 800;
  color: #6366f1;
  margin-bottom: 12px;
  letter-spacing: 2px;
  text-shadow: 0 2px 8px #e0e7ff44;
}

.desc {
  font-size: 1.1rem;
  color: #6b7280;
  margin-bottom: 18px;
  line-height: 1.7;
}

.price {
  color: #f56c6c;
  font-size: 26px;
  font-weight: bold;
  margin: 18px 0 24px 0;
  letter-spacing: 1px;
  text-shadow: 0 2px 8px #fbc2eb33;
}

.btn-group {
  display: flex;
  gap: 16px;
}

.fav-btn {
  font-weight: 600;
  border-radius: 10px;
}

.not-found {
  text-align: center;
  padding: 40px 0;
}

.comment-section {
  margin-top: 36px;
  background: rgba(246,248,255,0.7);
  border-radius: 14px;
  padding: 24px 18px 8px 18px;
  box-shadow: 0 2px 12px #e0e7ff22;
}

.comment-form {
  margin-bottom: 30px;
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.comment-btn {
  border-radius: 8px;
  font-weight: 600;
  background: linear-gradient(90deg, #a1c4fd 0%, #c2e9fb 100%);
  border: none;
  color: #6366f1;
  transition: background 0.2s;
}
.comment-btn:hover {
  background: linear-gradient(90deg, #6366f1 0%, #a1c4fd 100%);
  color: #fff;
}

.comments {
  margin-top: 10px;
}

.fade-list-enter-active, .fade-list-leave-active {
  transition: all 0.4s cubic-bezier(.55,0,.1,1);
}
.fade-list-enter-from, .fade-list-leave-to {
  opacity: 0;
  transform: translateY(20px);
}
.fade-list-leave-from, .fade-list-enter-to {
  opacity: 1;
  transform: translateY(0);
}

.comment-item {
  padding: 18px 0 10px 0;
  border-bottom: 1px solid #e0e7ff;
  transition: background 0.2s;
}
.comment-item:last-child {
  border-bottom: none;
}
.comment-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 4px;
}
.comment-avatar {
  background: #eef2ff;
  border: 1.5px solid #e0e7ff;
}
.comment-username {
  color: #6366f1;
  font-weight: 600;
  font-size: 1rem;
}
.date {
  color: #999;
  font-size: 12px;
  margin-left: auto;
}
.comment-content {
  color: #444;
  font-size: 1.05rem;
  line-height: 1.7;
  word-break: break-all;
  margin-left: 38px;
}
</style>
