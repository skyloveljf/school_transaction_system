<template>
  <div class="profile-form">
    <el-form label-width="80px" :model="userInfo" class="profile-el-form">
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
        <el-input v-model="userInfo.nickname" clearable maxlength="20" />
      </el-form-item>

      <el-form-item label="邮箱">
        <el-input v-model="userInfo.email" clearable maxlength="40" />
      </el-form-item>

      <el-form-item label="简介">
        <el-input type="textarea" v-model="userInfo.bio" maxlength="100" show-word-limit rows="3" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="saveProfile" class="save-btn">保存修改</el-button>
        <el-button @click="dialogVisible = true" class="pwd-btn">修改密码</el-button>
      </el-form-item>
    </el-form>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="dialogVisible" title="修改密码" width="400px" class="pwd-dialog">
      <el-form :model="passwordForm" label-width="100px" :rules="passwordRules" ref="passwordFormRef">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" clearable />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" clearable />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" clearable />
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
import { ElMessage } from 'element-plus'
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
  max-width: 520px;
  margin: 0 auto;
  padding: 36px 0 0 0;
  background: linear-gradient(120deg, #f8fafc 0%, #e0e7ff 100%);
  border-radius: 18px;
  box-shadow: 0 4px 24px #c7d2fe33;
  animation: fadeInProfile 0.8s;
}
@keyframes fadeInProfile {
  from { opacity: 0; transform: translateY(30px);}
  to { opacity: 1; transform: none;}
}
.profile-el-form {
  background: rgba(255,255,255,0.98);
  border-radius: 14px;
  box-shadow: 0 2px 12px #a1c4fd22;
  padding: 32px 28px 18px 28px;
}
.avatar-uploader {
  display: inline-block;
  cursor: pointer;
  transition: box-shadow 0.2s;
}
.avatar-uploader:hover {
  box-shadow: 0 2px 12px #a1c4fd55;
}
.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 2.5px solid #e0e7ff;
  box-shadow: 0 2px 8px #e0e7ff55;
  object-fit: cover;
  background: #fff;
  transition: box-shadow 0.2s, border-color 0.2s;
}
.avatar-uploader-icon {
  font-size: 32px;
  color: #b4b8d1;
  width: 80px;
  height: 80px;
  line-height: 80px;
  text-align: center;
  border: 2px dashed #e0e7ff;
  border-radius: 50%;
  background: #f8fafc;
  transition: border-color 0.2s;
}
.avatar-uploader:hover .avatar-uploader-icon {
  border-color: #6366f1;
  color: #6366f1;
}
.save-btn {
  font-weight: 700;
  border-radius: 10px;
  background: linear-gradient(90deg, #a1c4fd 0%, #c2e9fb 100%);
  border: none;
  color: #6366f1;
  transition: background 0.2s;
  margin-right: 12px;
}
.save-btn:hover {
  background: linear-gradient(90deg, #6366f1 0%, #a1c4fd 100%);
  color: #fff;
}
.pwd-btn {
  border-radius: 10px;
  font-weight: 600;
  color: #6366f1;
  background: #f3f4f6;
  border: 1.5px solid #e0e7ff;
  margin-left: 0;
  transition: background 0.2s, color 0.2s;
}
.pwd-btn:hover {
  background: #6366f1;
  color: #fff;
}
.pwd-dialog :deep(.el-dialog__body) {
  padding-top: 18px;
  padding-bottom: 0;
}
</style>
