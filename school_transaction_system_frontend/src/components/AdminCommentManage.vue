<template>
  <el-dialog v-model="visible" title="评论管理" width="900px" :close-on-click-modal="false">
    <el-table :data="commentsWithProductInfo" style="width: 100%" stripe border max-height="500px">
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
          <el-link type="primary" @click="navigateToProduct(scope.row.productId)">{{ scope.row.productTitle || '商品信息缺失' }}</el-link>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="评论时间" width="160" />
      <el-table-column label="操作" width="100" fixed="right" align="center">
        <template #default="scope">
          <el-button size="small" type="danger" @click="deleteComment(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { mockComments, mockProducts } from '../api/product' // 假设你的模拟数据在这里

const visible = ref(false)
const router = useRouter()

// 模拟评论数据
const comments = ref([...mockComments]) // 创建副本以允许修改

// 将商品信息合并到评论中，方便展示
const commentsWithProductInfo = computed(() => {
  return comments.value.map(comment => {
    const product = mockProducts.find(p => p.id === comment.productId)
    return {
      ...comment,
      productTitle: product ? product.title : '未知商品'
    }
  })
})

const truncateText = (text, maxLength) => {
  if (text.length <= maxLength) {
    return text
  }
  return text.substring(0, maxLength) + '...'
}

const navigateToProduct = (productId) => {
  if (productId) {
    router.push(`/product/${productId}`)
    visible.value = false // 关闭弹窗
  } else {
    ElMessage.warning('无法导航，商品ID缺失')
  }
}

const deleteComment = (commentToDelete) => {
  ElMessageBox.confirm(`确定要删除这条评论吗？评论内容：“${truncateText(commentToDelete.content, 20)}”`, '删除确认', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    comments.value = comments.value.filter(c => c.id !== commentToDelete.id)
    ElMessage.success('评论已删除')
  }).catch(() => {
    ElMessage.info('已取消删除操作')
  })
}

defineExpose({ visible })
</script>

<style scoped>
.comment-content-cell {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: default;
}
.el-link {
  font-size: 12px;
}
</style>