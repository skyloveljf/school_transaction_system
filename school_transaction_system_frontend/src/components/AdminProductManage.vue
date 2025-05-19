<template>
  <el-dialog v-model="visible" title="商品管理" width="850px" :close-on-click-modal="false">
    <el-table :data="products" style="width: 100%" stripe border max-height="500px">
      <el-table-column prop="id" label="ID" width="60" fixed />
      <el-table-column label="图片" width="80">
        <template #default="scope">
          <el-image
            style="width: 50px; height: 50px; border-radius: 4px;"
            :src="scope.row.image"
            :preview-src-list="[scope.row.image]"
            fit="cover"
            hide-on-click-modal
            preview-teleported
          />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="商品名称" min-width="150" />
      <el-table-column prop="price" label="价格(元)" width="100">
        <template #default="scope">
          <span>¥{{ scope.row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="owner" label="发布者" width="100" />
      <el-table-column prop="status" label="状态" width="110" align="center">
        <template #default="scope">
          <el-tag :type="statusTagType(scope.row.status)" effect="light">{{ scope.row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right" align="center">
        <template #default="scope">
          <el-button-group>
            <el-button v-if="scope.row.status === '审核中'" size="small" type="success" @click="updateProductStatus(scope.row, '在售')">通过</el-button>
            <el-button v-if="scope.row.status === '在售'" size="small" type="warning" @click="updateProductStatus(scope.row, '已下架')">下架</el-button>
            <el-button v-if="scope.row.status === '已下架'" size="small" type="info" @click="updateProductStatus(scope.row, '在售')">上架</el-button>
            <el-button v-if="scope.row.status === '审核中'" size="small" type="danger" plain @click="updateProductStatus(scope.row, '审核不通过')">拒绝</el-button>
          </el-button-group>
          <el-button size="small" type="danger" @click="deleteProduct(scope.row)" style="margin-left: 8px;">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { mockProducts } from '../api/product' // 假设你的模拟数据在这里

const visible = ref(false)

// 初始化商品数据，并为每个商品添加一个 status 属性
const products = ref(
  mockProducts.map((product, index) => ({
    ...product,
    // 为了演示不同状态，这里简单分配
    status: index % 4 === 0 ? '在售' : (index % 4 === 1 ? '审核中' : (index % 4 === 2 ? '已下架' : '审核不通过')),
  }))
)

const statusTagType = (status) => {
  switch (status) {
    case '在售':
      return 'success'
    case '审核中':
      return 'warning'
    case '已下架':
      return 'info'
    case '审核不通过':
      return 'danger'
    default:
      return ''
  }
}

const updateProductStatus = (product, newStatus) => {
  const originalStatus = product.status
  ElMessageBox.confirm(`确定将商品 "${product.title}" 的状态从 "${originalStatus}" 修改为 "${newStatus}" 吗？`, '状态变更确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: newStatus === '在售' ? 'success' : (newStatus === '已下架' ? 'warning' : (newStatus === '审核不通过' ? 'error' : 'info')),
  }).then(() => {
    product.status = newStatus
    ElMessage.success(`商品 "${product.title}" 状态已更新为 "${newStatus}"`)
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}

const deleteProduct = (productToDelete) => {
  ElMessageBox.confirm(`确定要永久删除商品 "${productToDelete.title}" 吗？此操作无法撤销。`, '删除确认', {
    confirmButtonText: '永久删除',
    cancelButtonText: '取消',
    type: 'error',
  }).then(() => {
    products.value = products.value.filter(p => p.id !== productToDelete.id)
    ElMessage.success(`商品 "${productToDelete.title}" 已永久删除`)
  }).catch(() => {
    ElMessage.info('已取消删除操作')
  })
}

defineExpose({ visible })
</script>

<style scoped>
.el-button-group .el-button {
  margin: 0 2px;
}
.el-tag {
  cursor: default;
}
</style>