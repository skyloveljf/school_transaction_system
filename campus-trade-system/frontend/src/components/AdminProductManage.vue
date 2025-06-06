<template>
  <el-dialog v-model="visible" title="商品管理" width="1000px" :close-on-click-modal="false" @open="fetchProducts">
    <!-- 违禁情况管理区域 -->
    <div class="prohibited-rules-section">
      <div class="section-header">
        <h4>违禁情况管理</h4>
        <el-button type="primary" @click="openProhibitedRulesDialog" size="small">
          <el-icon><Setting /></el-icon>
          管理违禁规则
        </el-button>
      </div>
      <div class="active-rules-display">
        <div v-if="activeRules.length > 0" class="rules-tags">
          <span class="rules-label">当前激活规则：</span>
          <el-tag 
            v-for="rule in activeRules.slice(0, 3)" 
            :key="rule.ruleId" 
            effect="light" 
            size="small"
            style="margin-right: 8px;"
          >
            {{ rule.ruleName }}
          </el-tag>
          <el-tag v-if="activeRules.length > 3" effect="light" size="small">
            +{{ activeRules.length - 3 }}...
          </el-tag>
        </div>
        <div v-else class="no-rules">
          <el-text type="info">暂无激活的违禁规则</el-text>
        </div>
      </div>
    </div>

    <el-divider />

    <div class="product-header-actions">
      <div class="left-actions">
        <el-button type="primary" @click="fetchProducts" :loading="loading" size="small">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
        <span class="product-count">共 {{ products.length }} 件商品</span>
      </div>
      <div class="right-actions">
        <el-button 
          type="success" 
          @click="executeAutoReview" 
          :loading="autoReviewLoading"
          :disabled="pendingReviewCount === 0"
          size="small"
        >
          <el-icon><MagicStick /></el-icon>
          一键自动审核 ({{ pendingReviewCount }})
        </el-button>
      </div>
    </div>
    
    <el-table 
      :data="products" 
      style="width: 100%" 
      stripe 
      border 
      max-height="500px"
      v-loading="loading"
      element-loading-text="加载商品数据中..."
    >
      <el-table-column prop="productId" label="ID" width="80" fixed />
      <el-table-column label="图片" width="100">
        <template #default="scope">
          <el-image
            style="width: 60px; height: 60px; border-radius: 4px;"
            :src="getImageUrl(scope.row.imageUrl)"
            :preview-src-list="[getImageUrl(scope.row.imageUrl)]"
            fit="cover"
            hide-on-click-modal
            preview-teleported
          >
            <template #error>
              <div class="image-slot">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="商品名称" min-width="180" />
      <el-table-column prop="price" label="价格(元)" width="120">
        <template #default="scope">
          <span>¥{{ scope.row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发布者" width="120">
        <template #default="scope">
          <span>{{ scope.row.seller?.username || '未知用户' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120" align="center">
        <template #default="scope">
          <el-tag :type="statusTagType(scope.row.status)" effect="light">
            {{ formatStatus(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发布时间" width="160">
        <template #default="scope">
          {{ formatDate(scope.row.postDate) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right" align="center">
        <template #default="scope">
          <div class="action-buttons">
            <template v-if="scope.row.status === 'PENDING_REVIEW'">
              <el-button 
                size="small" 
                type="success" 
                @click="reviewProduct(scope.row, true)"
                :loading="reviewingProductId === scope.row.productId"
              >
                通过
              </el-button>
              <el-button 
                size="small" 
                type="danger" 
                plain 
                @click="reviewProduct(scope.row, false)"
                :loading="reviewingProductId === scope.row.productId"
              >
                拒绝
              </el-button>
            </template>
            <template v-else-if="scope.row.status === 'AVAILABLE'">
              <el-button 
                size="small" 
                type="danger" 
                @click="deleteProduct(scope.row)"
                :loading="deletingProductId === scope.row.productId"
              >
                下架
              </el-button>
            </template>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
    
    <!-- 违禁规则管理对话框 -->
    <ProhibitedRulesDialog ref="prohibitedRulesDialogRef" />
    
    <!-- 自动审核结果对话框 -->
    <AutoReviewResultDialog 
      ref="autoReviewResultDialogRef" 
      @refresh-products="onRefreshProducts" 
    />
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Picture, Setting, MagicStick } from '@element-plus/icons-vue'
import { 
  getAllProductsApi, 
  reviewProductApi, 
  deleteProductApi,
  getActiveProhibitedRulesApi,
  executeAutoReviewApi
} from '../api/admin'
import { getImageUrl } from '../utils/imageHelper'
import ProhibitedRulesDialog from './ProhibitedRulesDialog.vue'
import AutoReviewResultDialog from './AutoReviewResultDialog.vue'

const visible = ref(false)
const products = ref([])
const loading = ref(false)
const reviewingProductId = ref(null)
const deletingProductId = ref(null)

// 违禁规则相关
const activeRules = ref([])
const prohibitedRulesDialogRef = ref(null)

// 自动审核相关
const autoReviewLoading = ref(false)
const autoReviewResultDialogRef = ref(null)

// 计算待审核商品数量
const pendingReviewCount = computed(() => {
  return products.value.filter(product => product.status === 'PENDING_REVIEW').length
})

// 获取商品列表
const fetchProducts = async () => {
  loading.value = true
  try {
    const response = await getAllProductsApi()
    
    // 处理不同的响应格式
    if (response.data && Array.isArray(response.data)) {
      products.value = response.data
    } else if (Array.isArray(response)) {
      products.value = response
    } else if (response.success && Array.isArray(response.data)) {
      products.value = response.data
    } else {
      console.warn('意外的响应格式:', response)
      products.value = []
    }
    
    console.log('获取到商品列表:', products.value.length, '件商品')
    
    // 同时获取激活的违禁规则
    await fetchActiveRules()
    
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败：' + (error.message || '未知错误'))
    products.value = []
  } finally {
    loading.value = false
  }
}

// 获取激活的违禁规则
const fetchActiveRules = async () => {
  try {
    const response = await getActiveProhibitedRulesApi()
    
    if (response && response.success && response.data) {
      activeRules.value = Array.isArray(response.data) ? response.data : []
    } else if (Array.isArray(response)) {
      activeRules.value = response
    } else if (response && response.data) {
      activeRules.value = Array.isArray(response.data) ? response.data : []
    } else {
      activeRules.value = []
    }
    
    console.log('获取到激活的违禁规则:', activeRules.value.length, '条')
  } catch (error) {
    console.error('获取激活违禁规则失败:', error)
    activeRules.value = []
  }
}

// 状态标签类型
const statusTagType = (status) => {
  switch (status) {
    case 'AVAILABLE':
      return 'success'
    case 'PENDING_REVIEW':
      return 'warning'
    default:
      return 'info'
  }
}

// 格式化状态显示
const formatStatus = (status) => {
  switch (status) {
    case 'AVAILABLE':
      return '在售'
    case 'PENDING_REVIEW':
      return '审核中'
    default:
      return status
  }
}

// 格式化日期
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

// 审核商品
const reviewProduct = async (product, approved) => {
  const action = approved ? '通过' : '拒绝'
  const actionDesc = approved ? '审核通过后商品将上架销售' : '审核拒绝后商品将被删除'
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}商品"${product.title}"吗？${actionDesc}`,
      `${action}确认`,
      {
        confirmButtonText: action,
        cancelButtonText: '取消',
        type: approved ? 'success' : 'warning',
      }
    )
    
    reviewingProductId.value = product.productId
    
    const response = await reviewProductApi(product.productId, approved)
    
    ElMessage.success(`商品${action}成功`)
    
    // 重新获取商品列表
    await fetchProducts()
    
  } catch (error) {
    if (error === 'cancel') {
      ElMessage.info('已取消操作')
    } else {
      console.error('审核商品失败:', error)
      ElMessage.error('审核商品失败：' + (error.message || '未知错误'))
    }
  } finally {
    reviewingProductId.value = null
  }
}

// 删除商品（下架）
const deleteProduct = async (product) => {
  try {
    await ElMessageBox.confirm(
      `确定要下架商品"${product.title}"吗？下架后商品将被删除且无法恢复。`,
      '下架确认',
      {
        confirmButtonText: '确定下架',
        cancelButtonText: '取消',
        type: 'error',
      }
    )
    
    deletingProductId.value = product.productId
    
    const response = await deleteProductApi(product.productId)
    
    ElMessage.success('商品下架成功')
    
    // 重新获取商品列表
    await fetchProducts()
    
  } catch (error) {
    if (error === 'cancel') {
      ElMessage.info('已取消操作')
    } else {
      console.error('下架商品失败:', error)
      ElMessage.error('下架商品失败：' + (error.message || '未知错误'))
    }
  } finally {
    deletingProductId.value = null
  }
}

// 打开违禁规则管理对话框
const openProhibitedRulesDialog = () => {
  if (prohibitedRulesDialogRef.value) {
    prohibitedRulesDialogRef.value.visible = true
  }
}

// 执行一键自动审核
const executeAutoReview = async () => {
  const pendingCount = pendingReviewCount.value
  
  if (pendingCount === 0) {
    ElMessage.warning('当前没有待审核的商品')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要执行一键自动审核吗？将自动审核 ${pendingCount} 件待审核商品。`,
      '自动审核确认',
      {
        confirmButtonText: '开始审核',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    autoReviewLoading.value = true
    ElMessage.info('正在执行自动审核，请稍候...')
    
    const response = await executeAutoReviewApi()
    
    let resultData = null
    if (response && response.success && response.data) {
      resultData = response.data
    } else if (response && response.data) {
      resultData = response.data
    } else {
      resultData = response
    }
    
    // 显示审核结果
    if (autoReviewResultDialogRef.value && resultData) {
      autoReviewResultDialogRef.value.setResult(resultData)
    }
    
    // 显示成功消息
    const { totalReviewed = 0, approved = 0, rejected = 0, failed = 0 } = resultData || {}
    ElMessage.success(`自动审核完成！总计：${totalReviewed}，通过：${approved}，拒绝：${rejected}，失败：${failed}`)
    
    // 刷新商品列表
    await fetchProducts()
    
  } catch (error) {
    if (error === 'cancel') {
      ElMessage.info('已取消自动审核')
    } else {
      console.error('自动审核失败:', error)
      ElMessage.error('自动审核失败：' + (error.message || '未知错误'))
    }
  } finally {
    autoReviewLoading.value = false
  }
}

// 刷新商品列表（来自审核结果对话框的事件）
const onRefreshProducts = async () => {
  await fetchProducts()
}

// 组件挂载时获取激活规则
onMounted(() => {
  fetchActiveRules()
})

defineExpose({ visible })
</script>

<style scoped>
/* 违禁规则区域样式 */
.prohibited-rules-section {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.section-header h4 {
  margin: 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.active-rules-display {
  min-height: 32px;
  display: flex;
  align-items: center;
}

.rules-tags {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.rules-label {
  color: #606266;
  font-size: 14px;
  margin-right: 8px;
  white-space: nowrap;
}

.no-rules {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 8px;
}

/* 产品头部操作区域样式 */
.product-header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0 4px;
}

.left-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.right-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.product-count {
  color: #666;
  font-size: 14px;
}

.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: center;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 14px;
}
</style>