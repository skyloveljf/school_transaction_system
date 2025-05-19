<template>
  <div class="profile-container">
    <el-skeleton :rows="10" animated v-if="isLoading" class="profile-skeleton"/>
    <div v-else-if="!userDataLoaded" class="profile-empty">
      <el-empty description="未能加载用户信息或用户不存在" />
    </div>
    <div class="profile-form" v-else>
      <el-form label-width="80px" :model="editableUserInfo" class="profile-el-form">
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :on-change="handleAvatarChange"
            :auto-upload="false"
            :disabled="!isOwnProfile"
          >
            <img v-if="editableUserInfo.avatar" :src="editableUserInfo.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="昵称">
          <el-input v-model="editableUserInfo.nickname" clearable maxlength="20" :disabled="!isOwnProfile" />
        </el-form-item>

        <el-form-item label="邮箱">
          <el-input v-model="editableUserInfo.email" clearable maxlength="40" :disabled="!isOwnProfile" />
        </el-form-item>

        <el-form-item label="简介">
          <el-input type="textarea" v-model="editableUserInfo.bio" maxlength="100" show-word-limit rows="3" :disabled="!isOwnProfile" />
        </el-form-item>

        <el-form-item v-if="isOwnProfile">
          <el-button type="primary" @click="saveProfile" class="save-btn">保存修改</el-button>
          <el-button @click="passwordDialogVisible = true" class="pwd-btn">修改密码</el-button>
          <el-button v-if="route.name === 'AdminProfile'" @click="goBack" class="back-btn">返回</el-button> <!-- 新增返回按钮 -->
        </el-form-item>
         <el-form-item v-else>
          <el-tag type="info">您正在查看其他用户的资料</el-tag>
        </el-form-item>
      </el-form>

      <!-- 修改密码对话框 -->
      <el-dialog v-model="passwordDialogVisible" title="修改密码" width="400px" class="pwd-dialog" append-to-body>
        <el-form :model="passwordForm" label-width="100px" :rules="passwordRules" ref="passwordFormRef">
          <el-form-item label="原密码" prop="oldPassword">
            <el-input v-model="passwordForm.oldPassword" type="password" clearable show-password />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="passwordForm.newPassword" type="password" clearable show-password />
          </el-form-item>
          <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input v-model="passwordForm.confirmPassword" type="password" clearable show-password />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPassword">确认修改</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { mockUsers } from '../api/user'

const route = useRoute()
const router = useRouter()

const isLoading = ref(true)
const userDataLoaded = ref(false)
const isOwnProfile = ref(false)

const editableUserInfo = reactive({
  id: null,
  avatar: '',
  nickname: '',
  email: '',
  bio: '',
  role: ''
})

const getCurrentLoggedInUser = () => {
  const userInfoString = localStorage.getItem('userInfo')
  if (userInfoString) {
    try {
      return JSON.parse(userInfoString)
    } catch (e) {
      console.error("解析localStorage中的userInfo失败:", e)
      localStorage.removeItem('userInfo')
      return null
    }
  }
  return null
}

const fetchUserProfileById = async (userId) => {
  // console.log(`尝试加载用户 ID: ${userId} 的信息`)
  if (!Array.isArray(mockUsers) || mockUsers.length === 0) {
    console.warn("mockUsers 为空或未定义")
    return null
  }
  const user = mockUsers.find(u => u.id === parseInt(userId))
  if (user) {
    return { ...user }
  }
  // console.warn(`在 mockUsers 中未找到 ID 为 ${userId} 的用户`)
  return null
}

onMounted(async () => {
  // console.log('UserProfile mounted. Current route name:', route.name);
  // console.log('Current route params:', route.params);

  isLoading.value = true
  userDataLoaded.value = false
  const loggedInUser = getCurrentLoggedInUser();
  // console.log('Logged in user from localStorage:', loggedInUser);
  let targetUserId = null

  if (!loggedInUser) {
    ElMessage.error('您尚未登录，请先登录。')
    isLoading.value = false
    return;
  }

  if (route.name === 'AdminProfile') {
    // console.log('Condition: route.name is AdminProfile');
    if (loggedInUser && loggedInUser.role === 'admin') {
      // console.log('Condition: loggedInUser is admin. Admin ID:', loggedInUser.id);
      targetUserId = loggedInUser.id;
      isOwnProfile.value = true;
    } else {
      // console.warn('Condition: loggedInUser is NOT admin or loggedInUser is null. Role:', loggedInUser?.role);
      ElMessage.error('权限不足或无法识别管理员身份。');
      isLoading.value = false;
      return;
    }
  } else if (route.params.id) {
    // console.log('Condition: route.params.id exists. Param ID:', route.params.id);
    targetUserId = route.params.id
    isOwnProfile.value = loggedInUser.id === parseInt(targetUserId)
  } else {
    // console.log('Condition: Fallback - loading current loggedInUser. User ID:', loggedInUser.id);
    targetUserId = loggedInUser.id
    isOwnProfile.value = true
  }

  // console.log('Final targetUserId to load:', targetUserId);
  // console.log('Is own profile:', isOwnProfile.value);

  if (targetUserId) {
    const profileData = await fetchUserProfileById(targetUserId);
    // console.log('Fetched profile data:', profileData);
    if (profileData) {
        Object.assign(editableUserInfo, profileData)
        userDataLoaded.value = true
    } else {
        ElMessage.error('加载用户信息失败或用户不存在。');
    }
  } else {
    ElMessage.warning('无法确定要加载的用户信息（targetUserId is null）。')
  }
  isLoading.value = false
})

const saveProfile = () => {
  if (!isOwnProfile.value) return;
  const userIndex = mockUsers.findIndex(u => u.id === editableUserInfo.id)
  if (userIndex !== -1) {
    mockUsers[userIndex] = { ...mockUsers[userIndex], ...editableUserInfo }
    const loggedInUser = getCurrentLoggedInUser()
    if (loggedInUser && loggedInUser.id === editableUserInfo.id) {
      localStorage.setItem('userInfo', JSON.stringify(mockUsers[userIndex]))
    }
    ElMessage.success('信息保存成功 (模拟)')
  } else {
    ElMessage.error('保存失败：未找到用户 (模拟)')
  }
  // console.log("保存的用户信息:", editableUserInfo);
}

const handleAvatarChange = (uploadFile) => {
  if (!isOwnProfile.value) return;
  const reader = new FileReader()
  reader.onload = (e) => {
    editableUserInfo.avatar = e.target.result
  }
  reader.readAsDataURL(uploadFile.raw)
  ElMessage.info('头像已预览，点击“保存修改”以生效。')
}

const passwordDialogVisible = ref(false)
const passwordFormRef = ref()
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (passwordForm.confirmPassword !== '') {
      passwordFormRef.value?.validateField('confirmPassword')
    }
    callback()
  }
}
const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error("两次输入的密码不一致!"))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { validator: validatePass, trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur'}
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validatePass2, trigger: 'blur' }
  ]
}

const submitPassword = () => {
  if (!isOwnProfile.value) return;
  if (!passwordFormRef.value) return;
  passwordFormRef.value.validate((valid) => {
    if (valid) {
      // console.log("修改密码提交:", passwordForm, "用户ID:", editableUserInfo.id)
      ElMessage.success('密码修改成功 (模拟)')
      passwordDialogVisible.value = false
      passwordFormRef.value.resetFields()
    } else {
      ElMessage.error('请检查密码输入。')
    }
  })
}

// 新增返回方法
const goBack = () => {
  router.go(-1) // 或者 router.back()
}

watch(() => route.params.id, (newId, oldId) => {
  if (newId && newId !== oldId && route.name !== 'AdminProfile') {
    onMounted()
  }
})
watch(() => route.name, (newName, oldName) => {
  if (newName === 'AdminProfile' && newName !== oldName) {
    onMounted()
  }
})

</script>

<style scoped>
.profile-container {
  /* 整体容器样式，可以根据需要调整 */
  padding: 20px;
}
.profile-skeleton {
  max-width: 520px;
  margin: 40px auto;
  padding: 30px;
  background-color: #fff;
  border-radius: 18px;
}
.profile-empty {
  margin-top: 50px;
}
.profile-form {
  max-width: 520px;
  margin: 0 auto;
  /* padding: 36px 0 0 0; */ /* 移除顶部padding，让整体容器控制 */
  background: linear-gradient(120deg, #f8fafc 0%, #e0e7ff 100%);
  border-radius: 18px;
  box-shadow: 0 4px 24px #c7d2fe33;
  animation: fadeInProfile 0.8s;
  overflow: hidden; /* 确保内部 el-form 的背景不会溢出 */
}
@keyframes fadeInProfile {
  from { opacity: 0; transform: translateY(30px);}
  to { opacity: 1; transform: none;}
}
.profile-el-form {
  background: rgba(255,255,255,0.98);
  /* border-radius: 14px; */ /* 移除这里的圆角，让父级控制 */
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
  /* margin-left: 0; */ /* 移除，让按钮自然排列 */
  margin-right: 12px; /* 为返回按钮留出间距 */
  transition: background 0.2s, color 0.2s;
}
.pwd-btn:hover {
  background: #6366f1;
  color: #fff;
}
/* 返回按钮样式 */
.back-btn {
  border-radius: 10px;
  font-weight: 600;
  /* color: #6366f1; */ /* 可以自定义颜色或使用默认按钮颜色 */
  /* background: #f3f4f6; */
  /* border: 1.5px solid #e0e7ff; */
  transition: background 0.2s, color 0.2s;
}
/* 如果需要特定样式可以取消注释并修改 */
/* .back-btn:hover {
  background: #e0e7ff;
  color: #3730a3;
} */

.pwd-dialog :deep(.el-dialog__body) {
  padding-top: 18px;
  padding-bottom: 0;
}
.pwd-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid #e0e7ff;
  margin-right: 0;
}
.pwd-dialog :deep(.el-dialog__footer) {
  border-top: 1px solid #e0e7ff;
}
</style>
