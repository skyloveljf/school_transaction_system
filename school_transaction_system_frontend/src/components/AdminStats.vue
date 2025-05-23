<template>
  <el-dialog v-model="visible" title="数据统计" width="900px" :close-on-click-modal="false" top="5vh">
    <div class="stats-container">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon users-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.totalUsers }}</div>
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
              <div class="stat-value">{{ stats.totalProducts }}</div>
              <div class="stat-label">商品总数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon comments-icon">
              <el-icon><ChatDotSquare /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.totalComments }}</div>
              <div class="stat-label">评论总数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon sales-icon">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">¥{{ stats.totalSales.toFixed(2) }}</div>
              <div class="stat-label">总销售额 (模拟)</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-divider content-position="left">更多统计 (占位)</el-divider>
      <el-empty description="图表和更多详细统计数据将在此展示" />
      <!-- 之后可以在这里集成 ECharts 或其他图表库 -->

    </div>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, Goods, ChatDotSquare, Money } from '@element-plus/icons-vue'
import { mockUsers } from '../api/user' // 假设用户数据
import { mockProducts, mockComments } from '../api/product' // 假设商品和评论数据

const visible = ref(false)

const stats = ref({
  totalUsers: 0,
  totalProducts: 0,
  totalComments: 0,
  totalSales: 0, // 模拟销售额
})

const fetchData = () => {
  // 实际应用中，这些数据应该从后端API获取
  stats.value.totalUsers = mockUsers.length
  stats.value.totalProducts = mockProducts.length
  stats.value.totalComments = mockComments.length

  // 模拟总销售额：假设每个“在售”商品都贡献了一定的销售额
  stats.value.totalSales = mockProducts.reduce((sum, product) => {
    // 简单模拟，可以根据实际情况调整逻辑
    if (product.status === '在售' || product.status === '已下架') { // 假设已下架的也曾贡献销售额
      return sum + (parseFloat(product.price) || 0) * (Math.floor(Math.random() * 5) + 1); // 假设每个商品卖出1-5件
    }
    return sum;
  }, 0);
}

onMounted(() => {
  // 弹窗显示时或者需要刷新数据时调用
  // 为了简单起见，这里在组件挂载时就获取一次
  // 更好的做法可能是在弹窗的 `open` 事件中获取
  fetchData()
})

defineExpose({ visible, fetchData }) // 暴露 fetchData 允许父组件刷新
</script>

<style scoped>
.stats-container {
  padding: 0 10px;
}
.stat-card {
  display: flex;
  align-items: center;
  padding: 15px; /* 调整内边距 */
  margin-bottom: 20px;
  border-radius: 8px;
}
.stat-icon {
  font-size: 36px; /* 调整图标大小 */
  padding: 15px; /* 调整图标背景区域大小 */
  border-radius: 50%;
  margin-right: 20px; /* 图标和文字间距 */
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}
.users-icon { background-color: #409EFF; }
.products-icon { background-color: #67C23A; }
.comments-icon { background-color: #E6A23C; }
.sales-icon { background-color: #F56C6C; }

.stat-content {
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.stat-value {
  font-size: 24px; /* 调整数值大小 */
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}
.stat-label {
  font-size: 13px; /* 调整标签大小 */
  color: #909399;
  margin-top: 4px;
}
.el-divider {
  margin-top: 30px;
  margin-bottom: 20px;
}
</style>