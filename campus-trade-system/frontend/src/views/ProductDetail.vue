<template>
  <div class="detail-container">
    <el-card v-loading="loadingProduct || loadingFavoriteStatus" class="detail-card">
      <!-- 返回主页按钮 -->
      <el-button type="primary" plain @click="goHome" style="margin-bottom: 20px;">
        返回主页
      </el-button>

      <!-- 商品信息 -->
      <div v-if="product" class="top-section">
        <img :src="getImageUrl(product.imageUrl)" class="product-image" alt="商品图片" />
        <div class="info-section">
          <h2>{{ product.title }}</h2>
          <p class="desc">{{ product.description }}</p>
          <p class="price">￥{{ product.price }}</p>
          <p v-if="product.sellerUsername">卖家: {{ product.sellerUsername }}</p>
          <p v-if="product.categoryName">分类: {{ product.categoryName }}</p>
          <p>浏览量: {{ product.views }}</p>

          <el-button type="success" @click="handleMessageSeller">私信卖家</el-button>

          <el-button
            :type="isFavorited ? 'warning' : 'primary'"
            :loading="togglingFavorite"
            @click="toggleFavoriteAction"
            style="margin-left: 12px;"
          >
            {{ isFavorited ? '取消收藏' : '收藏商品' }}
          </el-button>
        </div>
      </div>

      <div v-else-if="!loadingProduct && productFetchError" class="error-state">
        <el-alert title="加载商品失败" type="error" :description="productFetchError" show-icon :closable="false" />
        <el-button @click="goHome" style="margin-top: 15px;">返回首页</el-button>
      </div>

      <div v-else-if="!loadingProduct && !product" class="not-found">
        <el-empty description="未找到该商品或商品已下架" />
        <el-button @click="goHome">返回首页</el-button>
      </div>

      <!-- 评论区 -->
      <el-divider v-if="product">用户评论</el-divider>

      <div v-if="product" class="comment-section" v-loading="loadingComments">
        <el-form @submit.prevent="submitComment" class="comment-form">
          <el-form-item>
            <el-input
              v-model="commentForm.content"
              type="textarea"
              placeholder="写下你的评论..."
              :rows="3"
            />
          </el-form-item>
          <el-button type="primary" :loading="submittingComment" @click="submitComment">提交评论</el-button>
        </el-form>

        <div class="comments">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-header">
              <strong>{{ comment.username }}</strong>
              <span class="date">{{ formatDate(comment.createdTime) }}</span>
              <el-button 
                v-if="isCurrentUserComment(comment)" 
                type="danger" 
                size="small" 
                @click="deleteComment(comment.id)"
                :loading="deletingComment === comment.id"
                style="margin-left: 10px;"
              >
                删除
              </el-button>
            </div>
            <p>{{ comment.content }}</p>
            
            <!-- 回复区域 -->
            <div class="replies" v-if="comment.replies && comment.replies.length > 0">
              <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                <div class="comment-header">
                  <strong>{{ reply.username }}</strong>
                  <span class="date">{{ formatDate(reply.createdTime) }}</span>
                  <el-button 
                    v-if="isCurrentUserComment(reply)" 
                    type="danger" 
                    size="small" 
                    @click="deleteComment(reply.id)"
                    :loading="deletingComment === reply.id"
                    style="margin-left: 10px;"
                  >
                    删除
                  </el-button>
                </div>
                <p>{{ reply.content }}</p>
              </div>
            </div>
            
            <!-- 回复表单 -->
            <div v-if="replyingTo === comment.id" class="reply-form">
              <el-input
                v-model="replyForm.content"
                type="textarea"
                placeholder="回复评论..."
                :rows="2"
                size="small"
              />
              <div class="reply-actions">
                <el-button type="primary" size="small" @click="submitReply(comment.id)" :loading="submittingReply">回复</el-button>
                <el-button size="small" @click="cancelReply">取消</el-button>
              </div>
            </div>
            <el-button v-else type="text" @click="startReply(comment.id)">回复</el-button>
          </div>
          <el-empty v-if="!loadingComments && comments.length === 0" description="暂无评论" />
        </div>
      </div>
    </el-card>

    <!-- 聊天对话框 -->
    <ChatDialog
      v-if="showChatDialog"
      :userId="chatUserId"
      :username="chatUsername"
      :show="showChatDialog"
      @update:show="showChatDialog = $event"
      @close="showChatDialog = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'

import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElAlert, ElSkeleton, ElInput, ElButton, ElIcon, ElEmpty, ElDivider, ElCard } from 'element-plus'
import { getProductByIdApi } from '../api/product'
import { isProductFavoritedApi, addFavoriteApi, removeFavoriteByProductIdApi } from '../api/favorite'
import { getProductCommentsApi, addCommentApi, deleteCommentApi } from '../api/comment'
import { getImageUrl } from '../utils/imageHelper'
import ChatDialog from '../components/ChatDialog.vue'
import authService from '../services/authService'

const route = useRoute()
const router = useRouter()

const product = ref(null)
const loadingProduct = ref(false)
const productFetchError = ref(null)

const comments = ref([])
const loadingComments = ref(false)
const submittingComment = ref(false)
const submittingReply = ref(false)
const deletingComment = ref(null) // 存储正在删除的评论ID

const commentForm = ref({
  content: ''
})

const replyForm = ref({
  content: ''
})
const replyingTo = ref(null) // 存储正在回复的评论ID

const isFavorited = ref(false)
const loadingFavoriteStatus = ref(false)
const togglingFavorite = ref(false)

const productId = Number(route.params.id)

// 控制聊天对话框显示
const showChatDialog = ref(false)
const chatUserId = ref(null)
const chatUsername = ref('')

const fetchProductDetails = async () => {
  if (!productId) {
    productFetchError.value = '无效的商品ID'
    return
  }
  loadingProduct.value = true
  productFetchError.value = null
  try {
    const data = await getProductByIdApi(productId)
    product.value = data
  } catch (err) {
    console.error("Failed to fetch product details:", err)
    productFetchError.value = err.message || '加载商品信息失败'
    ElMessage.error(productFetchError.value)
  } finally {
    loadingProduct.value = false
  }
}

const checkFavoriteStatus = async () => {
  if (!product.value || !authService.isLoggedIn()) return
  loadingFavoriteStatus.value = true
  try {
    isFavorited.value = await isProductFavoritedApi(product.value.productId)
  } catch (err) {
    console.error("Failed to check favorite status:", err)
  } finally {
    loadingFavoriteStatus.value = false
  }
}

const toggleFavoriteAction = async () => {
  if (!product.value || !authService.isLoggedIn()) {
    ElMessage.warning('请先登录后再操作')
    return
  }
  togglingFavorite.value = true
  try {
    if (isFavorited.value) {
      await removeFavoriteByProductIdApi(product.value.productId)
      ElMessage.success('已取消收藏')
      isFavorited.value = false
    } else {
      await addFavoriteApi(product.value.productId)
      ElMessage.success('商品已收藏')
      isFavorited.value = true
    }
  } catch (err) {
    console.error("Toggle favorite action failed:", err)
    ElMessage.error(err.message || '操作失败，请重试')
  } finally {
    togglingFavorite.value = false
  }
}

const fetchComments = async () => {
  if (!productId) return
  
  loadingComments.value = true
  try {
    comments.value = await getProductCommentsApi(productId)
  } catch (err) {
    console.error("Failed to fetch comments:", err)
    ElMessage.error('加载评论失败: ' + (err.message || '未知错误'))
  } finally {
    loadingComments.value = false
  }
}

watch(product, (newProduct) => {
  if (newProduct) {
    checkFavoriteStatus()
    fetchComments()
  }
})

onMounted(() => {
  fetchProductDetails()
})

const submitComment = async () => {
  if (!commentForm.value.content.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  
  if (!authService.isLoggedIn()) {
    ElMessage.warning('请先登录后再评论')
    return
  }

  submittingComment.value = true
  try {
    const commentData = {
      productId: product.value.productId,
      content: commentForm.value.content
    }
    
    await addCommentApi(commentData)
    ElMessage.success('评论已提交')
    commentForm.value.content = ''
    // 重新获取评论列表
    await fetchComments()
  } catch (err) {
    ElMessage.error(err.message || '评论提交失败')
  } finally {
    submittingComment.value = false
  }
}

const startReply = (commentId) => {
  replyingTo.value = commentId
  replyForm.value.content = ''
}

const cancelReply = () => {
  replyingTo.value = null
  replyForm.value.content = ''
}

const submitReply = async (parentId) => {
  if (!replyForm.value.content.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  
  if (!authService.isLoggedIn()) {
    ElMessage.warning('请先登录后再回复')
    return
  }

  submittingReply.value = true
  try {
    const replyData = {
      productId: product.value.productId,
      content: replyForm.value.content,
      parentId: parentId
    }
    
    await addCommentApi(replyData)
    ElMessage.success('回复已提交')
    replyForm.value.content = ''
    replyingTo.value = null
    // 重新获取评论列表
    await fetchComments()
  } catch (err) {
    ElMessage.error(err.message || '回复提交失败')
  } finally {
    submittingReply.value = false
  }
}

const deleteComment = async (commentId) => {
  if (!authService.isLoggedIn()) {
    ElMessage.warning('请先登录')
    return
  }
  
  deletingComment.value = commentId
  try {
    await deleteCommentApi(commentId)
    ElMessage.success('评论已删除')
    // 重新获取评论列表
    await fetchComments()
  } catch (err) {
    ElMessage.error(err.message || '删除评论失败')
  } finally {
    deletingComment.value = null
  }
}

const isCurrentUserComment = (comment) => {
  const currentUser = JSON.parse(localStorage.getItem('currentUser'))
  return currentUser && currentUser.userId === comment.userId
}

const handleMessageSeller = () => {
  if (!product.value || !product.value.sellerId) {
    ElMessage.error('无法获取卖家信息')
    return
  }
  
  // 检查用户是否已登录
  if (!authService.isLoggedIn()) {
    ElMessage.warning('请先登录后再发送私信')
    return
  }
  
  // 检查是否是自己的商品
  const userProfileString = localStorage.getItem('userProfileDto')
  if (userProfileString) {
    try {
      const userProfile = JSON.parse(userProfileString)
      if (userProfile && userProfile.userId === product.value.sellerId) {
        ElMessage.warning('不能给自己发送私信')
        return
      }
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  }
  
  console.log('准备与卖家聊天:', {
    sellerId: product.value.sellerId,
    sellerName: product.value.sellerUsername || '卖家'
  })
  
  // 设置聊天对话框参数并显示
  chatUserId.value = product.value.sellerId
  chatUsername.value = product.value.sellerUsername || '卖家'
  showChatDialog.value = true
}

const goHome = () => {
  router.push('/userhome')
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('zh-CN', { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}
</script>

<style scoped>
.detail-container {
  max-width: 1000px;
  margin: 20px auto;
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
  border: 1px solid #eee;
}

.info-section {
  flex: 1;
}

.info-section h2 {
  margin-top: 0;
}

.desc {
  color: #606266;
  margin-bottom: 15px;
}

.price {
  color: #f56c6c;
  font-size: 24px;
  font-weight: bold;
  margin: 15px 0;
}

.info-section p {
  font-size: 14px;
  color: #303133;
  margin-bottom: 8px;
}

.not-found, .error-state {
  text-align: center;
  padding: 40px 0;
}

.comment-section {
  margin-top: 30px;
}

.comment-form {
  margin-bottom: 20px;
}

.comment-item {
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.comment-header strong {
  font-weight: 600;
}

.date {
  color: #909399;
  font-size: 12px;
  margin-left: 10px;
}

.replies {
  margin-left: 20px;
  margin-top: 10px;
  padding-left: 15px;
  border-left: 2px solid #f0f0f0;
}

.reply-item {
  padding: 10px 0;
}

.reply-form {
  margin-top: 10px;
  margin-bottom: 10px;
}

.reply-actions {
  margin-top: 8px;
  display: flex;
  gap: 10px;
}
</style>
