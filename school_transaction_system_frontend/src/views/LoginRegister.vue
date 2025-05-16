<template>
  <div class="login-container">
    <div class="login-bg" />
    <el-card class="box-card">
      <div class="title-row">
        <el-icon size="24"><User /></el-icon>
        <h2>{{ isLogin ? '用户登录' : '用户注册' }}</h2>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" show-password placeholder="请输入密码" />
        </el-form-item>

        <el-form-item v-if="!isLogin" label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" show-password placeholder="请再次输入密码" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit" style="width: 100%">
            {{ isLogin ? '登录' : '注册' }}
          </el-button>
        </el-form-item>

        <div class="switch-link">
          <span @click="isLogin = !isLogin">
            {{ isLogin ? '没有账号？点此注册' : '已有账号？点此登录' }}
          </span>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { loginApi, registerApi } from '../api/user'
import { User } from '@element-plus/icons-vue'

const router = useRouter()
const isLogin = ref(true)
const loading = ref(false)
const formRef = ref()

const form = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [
    { required: false },
    {
      validator: (_, value) => {
        if (!isLogin.value && value !== form.password) {
          return Promise.reject('两次密码不一致')
        }
        return Promise.resolve()
      },
      trigger: 'blur'
    }
  ]
}

const handleSubmit = async () => {
  await formRef.value.validate()
  const data = {
    username: form.username,
    password: form.password
  }

  try {
    loading.value = true
    if (isLogin.value) {
      const res = await loginApi(data)
      ElMessage.success('登录成功')

      const role = res.data.role
      if (role === 'admin') {
        router.push('/admin')
      } else {
        router.push('/userhome')
      }
    } else {
      await registerApi({ ...data })
      ElMessage.success('注册成功，请登录')
      isLogin.value = true
    }
  } catch (err) {
    ElMessage.error(err?.response?.data?.message || '请求失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  position: relative;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #a8edea, #fed6e3);
  z-index: -1;
}

.box-card {
  width: 420px;
  padding: 30px 20px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  border-radius: 16px;
  background-color: white;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: center;
  margin-bottom: 20px;
}

.title-row h2 {
  font-weight: 600;
  margin: 0;
}

.switch-link {
  text-align: center;
  color: #409eff;
  cursor: pointer;
  font-size: 14px;
  margin-top: 10px;
}

.switch-link span:hover {
  text-decoration: underline;
}
</style>
