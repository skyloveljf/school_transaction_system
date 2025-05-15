<template>
  <div class="login-container">
    <el-card class="box-card">
      <h2>{{ isLogin ? '登录' : '注册' }}</h2>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>

        <el-form-item v-if="!isLogin" label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit">
            {{ isLogin ? '登录' : '注册' }}
          </el-button>
          <el-button type="text" @click="isLogin = !isLogin">
            {{ isLogin ? '去注册' : '已有账号，去登录' }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { loginApi, registerApi } from '../api/user'

const isLogin = ref(true)
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
    if (isLogin.value) {
      await loginApi(data)
      ElMessage.success('登录成功')
      // TODO: 跳转主页
    } else {
      await registerApi({ ...data })
      ElMessage.success('注册成功，请登录')
      isLogin.value = true
    }
  } catch (err) {
    ElMessage.error(err?.response?.data?.message || '请求失败')
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f0f2f5;
}
.box-card {
  width: 400px;
}
</style>
