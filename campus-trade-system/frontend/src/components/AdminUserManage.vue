<template>
  <el-dialog v-model="visible" title="用户管理" width="800px" :close-on-click-modal="false">
    <div style="margin-bottom: 16px;">
      <el-button @click="loadUsers" type="primary" :loading="loading">刷新数据</el-button>
      <span style="margin-left: 16px; color: #909399;">共 {{ users.length }} 个普通用户</span>
    </div>
    
    <el-table :data="users" style="width:100%" v-loading="loading" stripe>
      <el-table-column prop="userId" label="用户ID" width="80"/>
      <el-table-column prop="username" label="用户名" min-width="120"/>
      <el-table-column prop="email" label="邮箱" min-width="200"/>
      <el-table-column prop="registrationDate" label="注册时间" width="160">
        <template #default="scope">
          <span>{{ formatDate(scope.row.registrationDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="lastLoginDate" label="最后登录" width="160">
        <template #default="scope">
          <span>{{ formatDate(scope.row.lastLoginDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button 
            size="small" 
            type="danger" 
            @click="confirmDeleteUser(scope.row)"
            :loading="scope.row.deleting"
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRegularUsersApi, deleteUserApi } from '../api/admin'

const visible = ref(false)
const loading = ref(false)
const users = ref([])

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '未知'
  try {
    const date = new Date(dateString)
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (e) {
    return '格式错误'
  }
}

// 加载用户数据
const loadUsers = async () => {
  loading.value = true
  try {
    console.log('Loading regular users...')
    const response = await getRegularUsersApi()
    console.log('Regular users response:', response)
    
    // 兼容不同的响应格式
    if (Array.isArray(response)) {
      users.value = response
    } else if (response && Array.isArray(response.data)) {
      users.value = response.data
    } else if (response && response.success && Array.isArray(response.data)) {
      users.value = response.data
    } else {
      console.error('Unexpected response format:', response)
      users.value = []
      ElMessage.warning('用户数据格式异常')
    }
    
    console.log('Loaded users:', users.value)
    ElMessage.success(`成功加载 ${users.value.length} 个用户`)
  } catch (error) {
    console.error('Failed to load users:', error)
    ElMessage.error('加载用户数据失败：' + (error.message || '未知错误'))
    users.value = []
  } finally {
    loading.value = false
  }
}

// 确认删除用户
const confirmDeleteUser = (user) => {
  ElMessageBox.confirm(
    `确定要删除用户 "${user.username}" 吗？此操作不可撤销。`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
      dangerouslyUseHTMLString: false
    }
  ).then(() => {
    deleteUser(user)
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 删除用户
const deleteUser = async (user) => {
  // 设置删除状态
  user.deleting = true
  
  try {
    console.log('Deleting user:', user.userId)
    const response = await deleteUserApi(user.userId)
    console.log('Delete response:', response)
    
    // 从列表中移除用户
    const index = users.value.findIndex(u => u.userId === user.userId)
    if (index > -1) {
      users.value.splice(index, 1)
    }
    
    ElMessage.success(`用户 "${user.username}" 删除成功`)
  } catch (error) {
    console.error('Failed to delete user:', error)
    ElMessage.error('删除用户失败：' + (error.message || '未知错误'))
  } finally {
    user.deleting = false
  }
}

// 监听弹窗显示状态，自动加载数据
watch(visible, (newVal) => {
  if (newVal) {
    loadUsers()
  }
})

// 关键：暴露 visible 给父组件
defineExpose({ visible })
</script>

<style scoped>
.el-table {
  border-radius: 8px;
  overflow: hidden;
}
</style>