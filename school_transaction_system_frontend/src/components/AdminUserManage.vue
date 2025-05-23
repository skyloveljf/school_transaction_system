<template>
  <el-dialog v-model="visible" title="用户管理" width="700px" :close-on-click-modal="false">
    <el-table :data="users" style="width:100%">
      <el-table-column prop="id" label="ID" width="60"/>
      <el-table-column prop="username" label="用户名"/>
      <el-table-column prop="role" label="角色" width="80"/>
      <el-table-column prop="email" label="邮箱"/>
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button size="small" type="danger" @click="deleteUser(scope.row)">删除</el-button>
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
import { ElMessage } from 'element-plus'

const visible = ref(false)

const users = ref([
  { id: 1, username: 'admin', role: 'admin', email: 'admin@example.com' },
  { id: 2, username: 'user1', role: 'user', email: 'user1@example.com' }
])

const deleteUser = (row) => {
  users.value = users.value.filter(u => u.id !== row.id)
  ElMessage.success('用户已删除')
}

// 关键：暴露 visible 给父组件
defineExpose({ visible })
</script>