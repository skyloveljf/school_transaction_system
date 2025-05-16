<template>
  <div class="detail-container">
    <el-card v-loading="loading" class="detail-card">
      <!-- 返回主页按钮 -->
      <el-button type="primary" plain @click="goHome" style="margin-bottom: 20px;">
        返回主页
      </el-button>

      <!-- 商品信息 -->
      <div v-if="product" class="top-section">
        <img :src="product.image" class="product-image" alt="商品图片" />
        <div class="info-section">
          <h2>{{ product.title }}</h2>
          <p class="desc">{{ product.description }}</p>
          <p class="price">￥{{ product.price }}</p>

          <el-button type="success" @click="handleMessage">私信卖家</el-button>

          <el-button
            :type="isFavorited ? 'warning' : 'primary'"
            @click="toggleFavorite"
            style="margin-left: 12px;"
          >
            {{ isFavorited ? '取消收藏' : '收藏商品' }}
          </el-button>
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
            />
          </el-form-item>
          <el-button type="primary" @click="submitComment">提交评论</el-button>
        </el-form>

        <div class="comments">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-header">
              <strong>{{ comment.username }}</strong>
              <span class="date">{{ formatDate(comment.createdAt) }}</span>
            </div>
            <p>{{ comment.content }}</p>
          </div>
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
  ElMessage.info('已发起与卖家的私信功能')
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
  padding: 20px;
}

.detail-card {
  padding: 20px;
}

.top-section {
  display: flex;
  gap: 30px;
  margin-bottom: 20px;
}

.product-image {
  width: 300px;
  height: 300px;
  object-fit: cover;
  border-radius: 8px;
}

.info-section {
  flex: 1;
}

.price {
  color: #f56c6c;
  font-size: 24px;
  font-weight: bold;
  margin: 15px 0;
}

.not-found {
  text-align: center;
  padding: 40px 0;
}

.comment-section {
  margin-top: 30px;
}

.comment-form {
  margin-bottom: 30px;
}

.comment-item {
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.date {
  color: #999;
  font-size: 12px;
}
</style>
