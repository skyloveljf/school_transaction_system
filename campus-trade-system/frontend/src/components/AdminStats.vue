<template>
    <el-dialog v-model="visible" title="数据统计" width="1000px" :close-on-click-modal="false" top="5vh">
      <div class="stats-container" v-loading="loading">
        <!-- 基础统计卡片 -->
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-icon users-icon">
                <el-icon><User /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
                <div class="stat-label">总用户数</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-icon products-icon">
                <el-icon><Goods /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ stats.totalProducts || 0 }}</div>
                <div class="stat-label">在售商品数</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-icon comments-icon">
                <el-icon><ChatDotSquare /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ stats.totalComments || 0 }}</div>
                <div class="stat-label">评论总数</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-icon chart-icon">
                <el-icon><PieChart /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ categoryCount }}</div>
                <div class="stat-label">商品分类数</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
  
        <!-- 详细统计区域 -->
        <el-divider content-position="left">详细统计</el-divider>
        
        <el-row :gutter="20" style="margin-top: 20px;">
          <!-- 商品分类统计 -->
          <el-col :span="12">
            <el-card shadow="hover">
              <template #header>
                <div class="card-header">
                  <span>商品分类统计</span>
                </div>
              </template>
              
              <!-- 分类进度条显示 -->
              <div v-if="stats.categoryStats && stats.categoryStats.length > 0">
                <div v-for="item in stats.categoryStats" :key="item.categoryName" class="progress-item">
                  <div class="progress-header">
                    <span class="category-name">{{ item.categoryName }}</span>
                    <span class="category-count">{{ item.productCount }}个 ({{ item.percentage }}%)</span>
                  </div>
                  <el-progress 
                    :percentage="item.percentage" 
                    :color="getProgressColor(item.percentage)"
                    :show-text="false"
                  />
                </div>
              </div>
              <el-empty v-else description="暂无商品分类数据" :image-size="80" />
            </el-card>
          </el-col>
          
          <!-- 评论分类统计 -->
          <el-col :span="12">
            <el-card shadow="hover">
              <template #header>
                <div class="card-header">
                  <span>评论分类统计</span>
                </div>
              </template>
              
              <!-- 评论分类进度条显示 -->
              <div v-if="stats.commentCategoryStats && stats.commentCategoryStats.length > 0">
                <div v-for="item in stats.commentCategoryStats" :key="item.categoryName" class="progress-item">
                  <div class="progress-header">
                    <span class="category-name">{{ item.categoryName }}</span>
                    <span class="category-count">{{ item.commentCount }}条 ({{ item.percentage }}%)</span>
                  </div>
                  <el-progress 
                    :percentage="item.percentage" 
                    :color="getProgressColor(item.percentage)"
                    :show-text="false"
                  />
                </div>
              </div>
              <el-empty v-else description="暂无评论分类数据" :image-size="80" />
            </el-card>
          </el-col>
        </el-row>
  
        <!-- 详细数据表格 -->
        <el-divider content-position="left">分类详情</el-divider>
        
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="12">
            <el-card shadow="hover">
              <template #header>
                <div class="card-header">
                  <span>商品分类详情</span>
                </div>
              </template>
              <el-table :data="stats.categoryStats" style="width: 100%" size="small" max-height="250">
                <el-table-column prop="categoryName" label="分类名称" />
                <el-table-column prop="productCount" label="商品数量" width="80" />
                <el-table-column prop="percentage" label="占比" width="80">
                  <template #default="scope">
                    <el-tag size="small" :type="getTagType(scope.row.percentage)">
                      {{ scope.row.percentage }}%
                    </el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-col>
          
          <el-col :span="12">
            <el-card shadow="hover">
              <template #header>
                <div class="card-header">
                  <span>评论分类详情</span>
                </div>
              </template>
              <el-table :data="stats.commentCategoryStats" style="width: 100%" size="small" max-height="250">
                <el-table-column prop="categoryName" label="分类名称" />
                <el-table-column prop="commentCount" label="评论数量" width="80" />
                <el-table-column prop="percentage" label="占比" width="80">
                  <template #default="scope">
                    <el-tag size="small" :type="getTagType(scope.row.percentage)">
                      {{ scope.row.percentage }}%
                    </el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-col>
        </el-row>
      </div>
      
      <template #footer>
        <el-button @click="refreshData" :loading="loading" type="primary">刷新数据</el-button>
        <el-button @click="visible = false">关闭</el-button>
      </template>
    </el-dialog>
  </template>
  
  <script setup>
  import { ref, computed, watch } from 'vue'
  import { User, Goods, ChatDotSquare, PieChart } from '@element-plus/icons-vue'
  import { ElMessage } from 'element-plus'
  import { getAdminStats } from '@/api/admin'
  
  const visible = ref(false)
  const loading = ref(false)
  
  // 统计数据
  const stats = ref({
    totalUsers: 0,
    totalProducts: 0,
    totalComments: 0,
    categoryStats: [],
    commentCategoryStats: []
  })
  
  // 计算分类数量
  const categoryCount = computed(() => {
    return stats.value.categoryStats ? stats.value.categoryStats.length : 0
  })
  
  // 获取进度条颜色
  const getProgressColor = (percentage) => {
    if (percentage >= 50) return '#67C23A'
    if (percentage >= 30) return '#E6A23C'
    if (percentage >= 10) return '#F56C6C'
    return '#909399'
  }
  
  // 获取标签类型
  const getTagType = (percentage) => {
    if (percentage >= 50) return 'success'
    if (percentage >= 30) return 'warning'
    if (percentage >= 10) return 'danger'
    return 'info'
  }
  
  // 获取统计数据
  const fetchData = async () => {
    loading.value = true
    try {
      const response = await getAdminStats()
      console.log('管理员统计API原始响应:', response)
      
      // 处理不同的响应格式
      let data = null
      if (response && response.success && response.data) {
        // 标准API响应格式: {success: true, data: {...}}
        data = response.data
        console.log('使用标准API响应格式')
      } else if (response && response.totalUsers !== undefined) {
        // 直接数据格式: {totalUsers: 5, totalProducts: 5, ...}
        data = response
        console.log('使用直接数据格式')
      } else if (response && response.data && response.data.totalUsers !== undefined) {
        // 嵌套数据格式: {data: {totalUsers: 5, ...}}
        data = response.data
        console.log('使用嵌套数据格式')
      }
      
      if (data) {
        stats.value = {
          totalUsers: data.totalUsers || 0,
          totalProducts: data.totalProducts || 0,
          totalComments: data.totalComments || 0,
          categoryStats: data.categoryStats || [],
          commentCategoryStats: data.commentCategoryStats || []
        }
        console.log('处理后的统计数据:', stats.value)
        
        // 检查是否有分类数据
        if (stats.value.categoryStats.length === 0 && stats.value.commentCategoryStats.length === 0) {
          ElMessage.warning('统计数据获取成功，但暂无分类数据')
        } else {
          ElMessage.success('统计数据获取成功')
        }
      } else {
        console.error('API响应格式不正确:', response)
        ElMessage.error('API响应格式不正确')
      }
    } catch (error) {
      console.error('获取统计数据失败:', error)
      ElMessage.error('获取统计数据失败: ' + (error.response?.data?.message || error.message))
    } finally {
      loading.value = false
    }
  }
  
  // 刷新数据
  const refreshData = () => {
    fetchData()
  }
  
  // 监听弹窗显示状态
  watch(visible, (newVal) => {
    if (newVal) {
      fetchData()
    }
  })
  
  // 暴露给父组件的方法和属性
  defineExpose({ 
    visible, 
    fetchData,
    refreshData
  })
  </script>
  
  <style scoped>
  .stats-container {
    padding: 0 10px;
  }
  
  .stat-card {
    display: flex;
    align-items: center;
    padding: 15px;
    margin-bottom: 20px;
    border-radius: 8px;
  }
  
  .stat-icon {
    font-size: 36px;
    padding: 15px;
    border-radius: 50%;
    margin-right: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
  }
  
  .users-icon { 
    background-color: #409EFF; 
  }
  
  .products-icon { 
    background-color: #67C23A; 
  }
  
  .comments-icon { 
    background-color: #E6A23C; 
  }
  
  .chart-icon { 
    background-color: #F56C6C; 
  }
  
  .stat-content {
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  
  .stat-value {
    font-size: 24px;
    font-weight: bold;
    color: #303133;
    line-height: 1.2;
  }
  
  .stat-label {
    font-size: 13px;
    color: #909399;
    margin-top: 4px;
  }
  
  .el-divider {
    margin-top: 30px;
    margin-bottom: 20px;
  }
  
  .card-header {
    font-weight: bold;
    color: #303133;
  }
  
  .el-card {
    margin-bottom: 20px;
  }
  
  .progress-item {
    margin-bottom: 20px;
  }
  
  .progress-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
  }
  
  .category-name {
    font-weight: 500;
    color: #303133;
  }
  
  .category-count {
    font-size: 12px;
    color: #909399;
  }
  
  :deep(.el-table) {
    font-size: 12px;
  }
  
  :deep(.el-table th) {
    background-color: #f5f7fa;
  }
  
  :deep(.el-progress-bar__outer) {
    border-radius: 10px;
  }
  
  :deep(.el-progress-bar__inner) {
    border-radius: 10px;
  }
  </style>