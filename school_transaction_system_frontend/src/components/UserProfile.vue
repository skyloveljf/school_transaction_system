<template>
  <div class="profile-form">
    <el-form label-width="80px" :model="userInfo">
      <el-form-item label="头像">
        <el-upload
          class="avatar-uploader"
          action="#"
          :show-file-list="false"
          :on-change="handleAvatarChange"
        >
          <img v-if="userInfo.avatar" :src="userInfo.avatar" class="avatar" />
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
      </el-form-item>

      <el-form-item label="昵称">
        <el-input v-model="userInfo.nickname" />
      </el-form-item>

      <el-form-item label="邮箱">
        <el-input v-model="userInfo.email" />
      </el-form-item>

      <el-form-item label="简介">
        <el-input type="textarea" v-model="userInfo.bio" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="saveProfile">保存修改</el-button>
        <el-button @click="dialogVisible = true">修改密码</el-button>
      </el-form-item>
    </el-form>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="dialogVisible" title="修改密码" width="400px">
      <el-form :model="passwordForm" label-width="100px" :rules="passwordRules" ref="passwordFormRef">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" />
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" />
        </el-form-item>

        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const userInfo = ref({
  avatar: 'https://img.keaitupian.cn/uploads/upimg/1597372353577123.jpg',
  nickname: 'codingsky',
  email: 'codingsky@example.com',
  bio: '热爱技术，探索未来。'
})

// 修改信息
const saveProfile = () => {
  ElMessage.success('信息保存成功')
}

// 上传头像
const handleAvatarChange = (uploadFile) => {
  const reader = new FileReader()
  reader.onload = (e) => {
    userInfo.value.avatar = e.target.result
  }
  reader.readAsDataURL(uploadFile.raw)
}

// 修改密码弹窗控制
const dialogVisible = ref(false)
const passwordFormRef = ref()
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 校验规则
const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 提交修改密码
const submitPassword = () => {
  passwordFormRef.value.validate((valid) => {
    if (valid) {
      // 模拟修改逻辑
      ElMessage.success('密码修改成功')
      dialogVisible.value = false
      passwordForm.value.oldPassword = ''
      passwordForm.value.newPassword = ''
      passwordForm.value.confirmPassword = ''
    }
  })
}
</script>

<style scoped>
.profile-form {
  padding: 20px;
}

.avatar-uploader {
  display: inline-block;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #ccc;
  width: 80px;
  height: 80px;
  line-height: 80px;
  text-align: center;
  border: 1px dashed #ccc;
  border-radius: 50%;
}
</style>
