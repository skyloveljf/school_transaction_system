<template>
  <el-dialog v-model="visible" title="评论管理" width="900px" :close-on-click-modal="false" @open="fetchComments">
    <div class="comment-header-actions">
      <el-button type="primary" @click="fetchComments" :loading="loading" size="small">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
      <span class="comment-count">共 {{ comments.length }} 条评论</span>
    </div>
    
    <el-table 
      :data="comments" 
      style="width: 100%" 
      stripe 
      border 
      max-height="500px"
      v-loading="loading"
      element-loading-text="加载评论数据中..."
    >
      <el-table-column prop="id" label="评论ID" width="80" fixed />
      <el-table-column prop="content" label="评论内容" min-width="250">
        <template #default="scope">
          <el-tooltip :content="scope.row.content" placement="top" :disabled="scope.row.content.length <= 50">
            <div class="comment-content-cell">{{ truncateText(scope.row.content, 50) }}</div>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column prop="username" label="评论者" width="120" />
      <el-table-column prop="productTitle" label="所属商品" min-width="180">
        <template #default="scope">
          <el-link 
            type="primary" 
            @click="navigateToProduct(scope.row.productId)"
            :disabled="!scope.row.productId"
          >
            {{ scope.row.productTitle || '商品信息缺失' }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column prop="createdTime" label="评论时间" width="160">
        <template #default="scope">
          {{ formatDate(scope.row.createdTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" fixed="right" align="center">
        <template #default="scope">
          <el-button 
            size="small" 
            type="danger" 
            @click="deleteComment(scope.row)"
            :loading="deletingCommentId === scope.row.id"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { getAllCommentsApi, deleteCommentApi } from '../api/admin'

const visible = ref(false)
const router = useRouter()
const loading = ref(false)
const comments = ref([])
const deletingCommentId = ref(null)

// 获取所有评论
const fetchComments = async () => {
  loading.value = true
  try {
    const response = await getAllCommentsApi()
    
    // 处理不同的响应格式
    let commentsData = []
    if (Array.isArray(response)) {
      commentsData = response
    } else if (response && response.data) {
      commentsData = Array.isArray(response.data) ? response.data : []
    } else if (response && response.success && response.data) {
      commentsData = Array.isArray(response.data) ? response.data : []
    }
    
    comments.value = commentsData
    console.log('获取到评论数据:', commentsData)
  } catch (error) {
    console.error('获取评论失败:', error)
    ElMessage.error('获取评论数据失败：' + (error.message || '未知错误'))
    comments.value = []
  } finally {
    loading.value = false
  }
}

// 监听弹窗显示状态，自动加载数据
watch(visible, (newVal) => {
  if (newVal) {
    fetchComments()
  }
})

const truncateText = (text, maxLength) => {
  if (!text) return ''
  if (text.length <= maxLength) {
    return text
  }
  return text.substring(0, maxLength) + '...'
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  try {
    return new Date(dateString).toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (error) {
    return dateString
  }
}

const navigateToProduct = (productId) => {
  if (productId) {
    router.push(`/product/${productId}`)
    visible.value = false // 关闭弹窗
  } else {
    ElMessage.warning('无法导航，商品ID缺失')
  }
}

const deleteComment = async (commentToDelete) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除这条评论吗？评论内容："${truncateText(commentToDelete.content, 20)}"`, 
      '删除确认', 
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    deletingCommentId.value = commentToDelete.id
    
    const response = await deleteCommentApi(commentToDelete.id)
    
    ElMessage.success('评论已删除')
    
    // 重新获取评论列表
    await fetchComments()
    
  } catch (error) {
    if (error === 'cancel') {
      ElMessage.info('已取消删除操作')
    } else {
      console.error('删除评论失败:', error)
      ElMessage.error('删除评论失败：' + (error.message || '未知错误'))
    }
  } finally {
    deletingCommentId.value = null
  }
}

defineExpose({ visible })
</script>

<style scoped>
.comment-header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0 4px;
}

.comment-count {
  color: #666;
  font-size: 14px;
}

.comment-content-cell {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: default;
}

.el-link {
  font-size: 12px;
}

.el-table {
  border-radius: 8px;
}

.el-dialog__body {
  padding: 20px;
}
</style>