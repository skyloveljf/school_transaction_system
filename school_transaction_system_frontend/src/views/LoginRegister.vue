<template>
  <div class="login-container">
    <div class="login-bg"></div>
    <el-card class="box-card">
      <div class="title-row">
        <el-icon size="32" class="user-icon"><User /></el-icon>
        <h2>{{ isLogin ? '用户登录' : '用户注册' }}</h2>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            clearable
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            show-password
            placeholder="请输入密码"
            clearable
            prefix-icon="Lock"
            size="large"
          />
        </el-form-item>
        <el-form-item v-if="!isLogin" label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            show-password
            placeholder="请再次输入密码"
            clearable
            prefix-icon="Lock"
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleSubmit"
            class="login-btn"
            size="large"
          >
            {{ isLogin ? '登录' : '注册' }}
          </el-button>
        </el-form-item>
        <div class="switch-link">
          <span @click="toggleFormType">
            {{ isLogin ? '没有账号？点此注册' : '已有账号？点此登录' }}
          </span>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { loginApi, registerApi } from '../api/user'
import { useRouter } from 'vue-router'
import { User } from '@element-plus/icons-vue' // User 图标用于标题行

const router = useRouter()
const isLogin = ref(true)
const loading = ref(false)
const formRef = ref()

const form = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const rules = computed(() => ({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [
    {
      required: !isLogin.value,
      message: '请再次输入密码',
      trigger: 'blur'
    },
    {
      validator: (rule, value, callback) => {
        if (!isLogin.value && value !== form.password) {
          callback(new Error('两次密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}))

const toggleFormType = () => {
  isLogin.value = !isLogin.value
  if (formRef.value) {
    formRef.value.resetFields() // 重置表单字段值和校验状态
  }
  // 通常 resetFields 会将 form 中的值重置为初始值（空字符串）
  // 如果发现 resetFields 后 form 中的值未按预期清空，可以保留下面的手动清空逻辑
  // form.username = ''
  // form.password = ''
  // form.confirmPassword = ''
}

const handleSubmit = async () => {
  if (!formRef.value) return
  formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      const submitData = {
        username: form.username,
        password: form.password
      }
      try {
        if (isLogin.value) {
          const res = await loginApi(submitData)
          ElMessage.success('登录成功')
          localStorage.setItem('userInfo', JSON.stringify(res.data)) // 存储用户信息
          const role = res.data.role
          if (role === 'admin') {
            router.push('/admin')
          } else {
            router.push('/userhome')
          }
        } else {
          // 注册逻辑
          await registerApi({ username: submitData.username, password: submitData.password })
          ElMessage.success('注册成功，请登录')
          toggleFormType() // 注册成功后切换到登录表单并重置
        }
      } catch (err) {
        // 统一处理错误提示
        const errorMessage = err?.response?.data?.message || err?.message || '操作失败，请重试'
        ElMessage.error(errorMessage)
      } finally {
        loading.value = false
      }
    } else {
      ElMessage.error('请检查输入项')
      // 不需要显式 return false，validate 的回调参数 valid 已经表明了校验结果
    }
  })
}
</script>

<style scoped>
.login-container {
  position: relative;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(120deg, #a1c4fd 0%, #c2e9fb 100%);
  overflow: hidden;
  animation: fadeIn 1.2s;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(40px);}
  to { opacity: 1; transform: none;}
}
.login-bg {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 80% 20%, #fbc2eb 0%, #a6c1ee 100%);
  opacity: 0.5;
  z-index: -1;
  filter: blur(8px);
  animation: bgMove 8s infinite alternate;
}
@keyframes bgMove {
  0% { background-position: 80% 20%; }
  100% { background-position: 20% 80%; }
}
.box-card {
  width: 420px;
  padding: 38px 32px 28px 32px;
  border-radius: 22px;
  background: rgba(255,255,255,0.98);
  box-shadow: 0 8px 32px 0 rgba(99,102,241,0.18), 0 2px 12px 0 rgba(0,0,0,0.06);
  border: 1.5px solid #e0e7ff;
  backdrop-filter: blur(2px);
  transition: box-shadow 0.2s;
  animation: cardPop 0.8s;
}
@keyframes cardPop {
  from { transform: scale(0.95);}
  to { transform: scale(1);}
}
.title-row {
  display: flex;
  align-items: center;
  gap: 12px;
  justify-content: center;
  margin-bottom: 32px;
}
.title-row h2 {
  font-weight: 700;
  margin: 0;
  font-size: 1.8rem;
  color: #6366f1;
  letter-spacing: 2px;
  text-shadow: 0 2px 8px #e0e7ff44;
}
.user-icon {
  color: #6366f1;
  background: #eef2ff;
  border-radius: 50%;
  padding: 8px;
  box-shadow: 0 2px 8px #dbeafe;
}
.el-form-item {
  margin-bottom: 24px;
}
:deep(.el-input__wrapper) {
  background: #f3f4f6 !important;
  border-radius: 10px !important;
  border: 1.5px solid #e0e7ff !important;
  transition: border-color 0.2s, box-shadow 0.2s !important;
  box-shadow: 0 1px 4px #e0e7ff33 !important;
}
:deep(.el-input__wrapper.is-focus) {
  border-color: #6366f1 !important;
  box-shadow: 0 2px 8px #6366f122 !important;
}
:deep(.el-input__inner) {
  font-size: 16px !important;
  color: #3730a3 !important;
  background: transparent !important;
}
.login-btn {
  width: 100%;
  font-weight: 700;
  letter-spacing: 2px;
  border-radius: 10px;
  background: linear-gradient(90deg, #a1c4fd 0%, #c2e9fb 100%);
  border: none;
  box-shadow: 0 2px 8px #a1c4fd44;
  transition: background 0.2s, box-shadow 0.2s;
}
.login-btn:hover {
  background: linear-gradient(90deg, #6366f1 0%, #a1c4fd 100%);
  box-shadow: 0 4px 16px #6366f144;
}
.switch-link {
  text-align: center;
  color: #6366f1;
  cursor: pointer;
  font-size: 15px;
  margin-top: 16px;
  transition: color 0.2s;
  user-select: none;
}
.switch-link span:hover {
  text-decoration: underline;
  color: #3730a3;
}
</style>
