<template>
  <div class="profile-form" v-loading="loadingProfile || savingProfile || avatarUploading">
    <el-form label-width="100px" :model="userInfo" ref="profileFormRef" :rules="profileRules">
      <el-form-item label="头像" prop="avatarUrl">
        <el-upload
          class="avatar-uploader"
          action="#" 
          :http-request="handleAvatarUploadRequest" 
          :show-file-list="false"
          :before-upload="beforeAvatarUpload"
        >
          <img v-if="userInfo.avatarUrl" :src="getImageUrl(userInfo.avatarUrl)" class="avatar" alt="User Avatar" @error="onImageError"/>
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
        <div class="el-upload__tip">点击上传新头像，仅支持JPG/PNG，小于2MB</div>
      </el-form-item>

      <el-form-item label="用户名">
        <el-input :model-value="userInfo.username" disabled />
      </el-form-item>

      <el-form-item label="邮箱" prop="email">
        <el-input v-model="userInfo.email" placeholder="请输入您的邮箱"/>
      </el-form-item>

      <el-form-item label="简介" prop="bio">
        <el-input type="textarea" v-model="userInfo.bio" rows="3" placeholder="简单介绍一下自己吧"/>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submitProfileChanges" :loading="savingProfile">保存修改</el-button>
        <el-button @click="passwordDialogVisible = true">修改密码</el-button>
      </el-form-item>
    </el-form>

    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="400px" @closed="resetPasswordForm">
      <el-form :model="passwordForm" label-width="100px" :rules="passwordRules" ref="passwordFormRef">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword" :loading="changingPassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElDialog, ElForm, ElFormItem, ElInput, ElButton, ElUpload, ElIcon } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getMyProfileApi, updateMyProfileApi, changePasswordApi } from '../api/user'
import { uploadImageApi } from '../api/upload'
import authService from '../services/authService'
import { getImageUrl } from '../utils/imageHelper'

const userInfo = reactive({
  userId: null,
  username: '', // 用户名通常不允许修改，从后端获取后设为disabled
  avatarUrl: '',
  email: '',
  bio: ''
})

const profileFormRef = ref(null)
const loadingProfile = ref(false)
const savingProfile = ref(false)
const avatarUploading = ref(false)

const profileRules = {
  email: [
    { required: true, message: '邮箱不能为空', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] }
  ],
  // avatarUrl: [{ required: true, message: '头像不能为空', trigger: 'change' }] // Can be optional
}

const passwordDialogVisible = ref(false)
const passwordFormRef = ref(null)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const changingPassword = ref(false)

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致!'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const loadUserProfile = async () => {
  loadingProfile.value = true
  try {
    const data = await getMyProfileApi()
    userInfo.userId = data.userId
    userInfo.username = data.username
    userInfo.avatarUrl = data.avatarUrl || '' // Fallback to empty string if null
    userInfo.email = data.email
    userInfo.bio = data.bio || ''
  } catch (error) {
    ElMessage.error(error.message || '加载用户信息失败')
  } finally {
    loadingProfile.value = false
  }
}

onMounted(loadUserProfile)

const beforeAvatarUpload = (rawFile) => {
  const isImage = rawFile.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件 (JPG, PNG)')
    return false
  }
  const isLt2M = rawFile.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

const handleAvatarUploadRequest = async (options) => {
  avatarUploading.value = true
  try {
    const imageUrl = await uploadImageApi(options.file)
    userInfo.avatarUrl = imageUrl
    ElMessage.success('头像上传成功！新头像将在保存后生效。')
    // Optionally trigger validation for avatarUrl if it has rules
    // profileFormRef.value?.validateField('avatarUrl')
  } catch (error) {
    ElMessage.error(error.message || '头像上传失败')
  } finally {
    avatarUploading.value = false
  }
}

const submitProfileChanges = async () => {
  if (!profileFormRef.value) return
  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      savingProfile.value = true
      try {
        // Prepare payload based on UserUpdateRequest DTO for backend
        const payload = {
          email: userInfo.email,
          bio: userInfo.bio,
          avatarUrl: userInfo.avatarUrl 
        }
        const updatedProfile = await updateMyProfileApi(payload)
        ElMessage.success('个人资料更新成功！')
              // 使用authService更新本地存储的用户信息
      authService.saveLoginInfo(null, updatedProfile);
        // Optionally re-fetch or update specific fields if response differs slightly
        userInfo.avatarUrl = updatedProfile.avatarUrl;
        userInfo.bio = updatedProfile.bio || userInfo.bio; // 更新bio字段
        // etc. for other fields returned by backend

      } catch (error) {
        ElMessage.error(error.message || '保存失败，请稍后重试')
      } finally {
        savingProfile.value = false
      }
    } else {
      ElMessage.error('请检查表单信息是否填写正确。')
      return false
    }
  })
}

const handleChangePassword = async () => {
  console.log('修改密码按钮被点击');
  // 检查用户是否已登录
  console.log('登录状态:', authService.isLoggedIn());
  console.log('用户信息:', authService.getUserProfile());
  
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    console.log('表单验证结果:', valid);
    if (valid) {
      changingPassword.value = true
      try {
        const { oldPassword, newPassword, confirmPassword } = passwordForm
        // 调用实际的API，正确映射参数名
        const response = await changePasswordApi({ 
          oldPassword, 
          newPassword, 
          confirmNewPassword: confirmPassword  // 映射前端的confirmPassword到后端的confirmNewPassword
        })
        ElMessage.success(response.message || '密码修改成功！') // 后端可能直接返回消息字符串
        passwordDialogVisible.value = false
      } catch (error) {
        // error已经是处理过的，可以直接显示message
        ElMessage.error(error.message || '密码修改失败，请重试');
      } finally {
        changingPassword.value = false
      }
    }
  })
}

const resetPasswordForm = () => {
  passwordFormRef.value?.resetFields()
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}

const onImageError = (event) => {
  event.target.src = 'default-placeholder.png' // Path to your default placeholder image
}
</script>

<style scoped>
.profile-form {
  max-width: 600px;
  margin: 20px auto;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.avatar {
  width: 100px; /* Increased size */
  height: 100px; /* Increased size */
  border-radius: 50%;
  display: block;
  object-fit: cover;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px; /* Increased size */
  height: 100px; /* Increased size */
  line-height: 100px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
}

.el-upload__tip {
  font-size: 12px;
  color: #909399;
  margin-top: 7px;
}
</style>
