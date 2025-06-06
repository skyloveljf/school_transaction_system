<template>
    <el-dialog 
      v-model="visible" 
      title="自动审核结果" 
      width="700px" 
      :close-on-click-modal="false"
    >
      <div class="result-summary" v-if="result">
        <div class="summary-cards">
          <div class="summary-card">
            <div class="card-value">{{ result.totalReviewed || 0 }}</div>
            <div class="card-label">总审核数</div>
          </div>
          <div class="summary-card success">
            <div class="card-value">{{ result.approved || 0 }}</div>
            <div class="card-label">通过</div>
          </div>
          <div class="summary-card danger">
            <div class="card-value">{{ result.rejected || 0 }}</div>
            <div class="card-label">拒绝</div>
          </div>
          <div class="summary-card warning">
            <div class="card-value">{{ result.failed || 0 }}</div>
            <div class="card-label">失败</div>
          </div>
        </div>
        
        <div class="review-time">
          <el-icon><Clock /></el-icon>
          审核时间：{{ formatDate(result.reviewTime) }}
        </div>
      </div>
      
      <div class="result-details" v-if="result && result.details && result.details.length > 0">
        <h4>审核详情</h4>
        <el-table 
          :data="result.details" 
          style="width: 100%" 
          stripe 
          border 
          max-height="300px"
        >
          <el-table-column prop="productId" label="商品ID" width="80" />
          <el-table-column prop="productTitle" label="商品名称" min-width="150" show-overflow-tooltip />
          <el-table-column label="审核结果" width="100" align="center">
            <template #default="scope">
              <el-tag 
                :type="getResultTagType(scope.row)" 
                effect="light"
              >
                {{ getResultText(scope.row) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="reason" label="原因/说明" min-width="200" show-overflow-tooltip />
        </el-table>
      </div>
      
      <div class="empty-result" v-else-if="result">
        <el-empty description="没有审核详情数据" />
      </div>
      
      <template #footer>
        <el-button @click="visible = false">关闭</el-button>
        <el-button type="primary" @click="refreshProductList">
          刷新商品列表
        </el-button>
      </template>
    </el-dialog>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  import { Clock } from '@element-plus/icons-vue'
  
  const visible = ref(false)
  const result = ref(null)
  
  // 设置审核结果数据
  const setResult = (data) => {
    result.value = data
    visible.value = true
  }
  
  // 获取结果标签类型
  const getResultTagType = (detail) => {
    if (detail.error) {
      return 'warning'
    } else if (detail.approved) {
      return 'success'
    } else {
      return 'danger'
    }
  }
  
  // 获取结果文本
  const getResultText = (detail) => {
    if (detail.error) {
      return '失败'
    } else if (detail.approved) {
      return '通过'
    } else {
      return '拒绝'
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
        minute: '2-digit',
        second: '2-digit'
      })
    } catch (error) {
      return dateString
    }
  }
  
  // 刷新商品列表事件
  const emit = defineEmits(['refresh-products'])
  
  const refreshProductList = () => {
    emit('refresh-products')
    visible.value = false
  }
  
  defineExpose({ 
    visible,
    setResult
  })
  </script>
  
  <style scoped>
  .result-summary {
    margin-bottom: 20px;
  }
  
  .summary-cards {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    margin-bottom: 16px;
  }
  
  .summary-card {
    background: #f8f9fa;
    border: 1px solid #e9ecef;
    border-radius: 8px;
    padding: 16px;
    text-align: center;
    transition: all 0.3s;
  }
  
  .summary-card.success {
    background: #f0f9ff;
    border-color: #67c23a;
  }
  
  .summary-card.danger {
    background: #fef0f0;
    border-color: #f56c6c;
  }
  
  .summary-card.warning {
    background: #fdf6ec;
    border-color: #e6a23c;
  }
  
  .card-value {
    font-size: 24px;
    font-weight: bold;
    color: #303133;
    margin-bottom: 4px;
  }
  
  .summary-card.success .card-value {
    color: #67c23a;
  }
  
  .summary-card.danger .card-value {
    color: #f56c6c;
  }
  
  .summary-card.warning .card-value {
    color: #e6a23c;
  }
  
  .card-label {
    font-size: 14px;
    color: #909399;
  }
  
  .review-time {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #666;
    font-size: 14px;
    justify-content: center;
  }
  
  .result-details h4 {
    margin: 0 0 16px 0;
    color: #303133;
    font-size: 16px;
  }
  
  .empty-result {
    padding: 20px;
  }
  </style>