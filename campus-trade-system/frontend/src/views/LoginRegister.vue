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
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>

        <el-form-item v-if="!isLogin" label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password placeholder="请再次输入密码" />
        </el-form-item>

        <el-form-item v-if="!isLogin" label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit" style="width: 100%">
            {{ isLogin ? '登录' : '注册' }}
          </el-button>
        </el-form-item>

        <div class="switch-link">
          <span @click="toggleFormType">
            {{ isLogin ? '没有账号？点此注册' : '已有账号？点此登录' }}
          </span>
        </div>

      
        <div class="admin-link" v-if="isLogin">
          <span @click="$router.push('/admin-register')">
            创建管理员账号
          </span>
        </div>
        
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { loginApi, registerApi } from '../api/user'
import { User } from '@element-plus/icons-vue'
import authService from '../services/authService'

const router = useRouter()
const isLogin = ref(true)
const loading = ref(false)
const formRef = ref()

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: ''
})

const rules = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  email: [
    { 
      required: computed(() => !isLogin.value),
      message: '请输入邮箱地址', 
      trigger: 'blur' 
    },
    { 
      type: 'email', 
      message: '请输入正确的邮箱地址', 
      trigger: ['blur', 'change'],
      validator: (rule, value, callback) => {
        if (!isLogin.value && value && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
          callback(new Error('请输入正确的邮箱地址'));
        } else {
          callback();
        }
      }
    }
  ],
  confirmPassword: [
    { 
      required: computed(() => !isLogin.value),
      message: '请再次输入密码', 
      trigger: 'blur' 
    },
    {
      validator: (_, value) => {
        if (!isLogin.value && value !== form.password) {
          return Promise.reject('两次密码不一致');
        }
        return Promise.resolve();
      },
      trigger: 'blur'
    }
  ]
})

const resetForm = () => {
  form.username = '';
  form.password = '';
  form.confirmPassword = '';
  form.email = '';
  if (formRef.value) {
    formRef.value.clearValidate();
    formRef.value.resetFields();
  }
}

const toggleFormType = () => {
  isLogin.value = !isLogin.value;
  resetForm();
}

const handleSubmit = async () => {
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
  } catch (error) {
    console.log('Form validation failed:', error);
    return; 
  }

  loading.value = true;
  try {
    if (isLogin.value) {
      const loginData = {
        username: form.username,
        password: form.password
      };
      
      // 登录前先清除可能存在的旧登录信息
      authService.clearLoginInfo();
      
      const res = await loginApi(loginData);
      
      // 检查返回数据完整性
      if (!res || !res.accessToken) {
        throw new Error('登录响应数据不完整，缺少token');
      }
      
      // 确保用户对象存在
      if (!res.user) {
        console.warn('登录响应中没有用户信息，将尝试从token获取');
        // 可以考虑从token中解析用户信息，或者调用其他API获取
      }
      
      // 使用authService保存登录信息
      authService.saveLoginInfo(res.accessToken, res.user);
      console.log('登录成功，token已保存:', res.accessToken.substring(0, 15) + '...');
      console.log('用户信息已保存:', res.user);
      
      ElMessage.success('登录成功');
      
      // 更新Axios拦截器
      authService.setupAxiosInterceptors();
      
      // 根据用户角色导航到相应页面
      if (res.user?.role === 'ROLE_ADMIN') {
        router.push('/admin');
      } else {
        router.push('/userhome');
      }
    } else {
      const registerData = {
        username: form.username,
        password: form.password,
        email: form.email
      };
      await registerApi(registerData);
      ElMessage.success('注册成功，请切换到登录页面进行登录。');
      isLogin.value = true;
      resetForm();
    }
  } catch (err) {
    console.error("Submit Handle Error:", err);
    
    // 提取错误信息并显示友好的错误消息
    let errorMessage = '操作失败，请检查网络或联系管理员'; // 默认错误消息
    
    // err 对象现在可能是后端返回的错误对象 { message: "..." } 或 Error 实例
    if (err && err.message) { // 如果 err 是一个对象并且有 message 属性
      errorMessage = err.message;
    } else if (typeof err === 'string') { // 如果 err 本身就是字符串
      errorMessage = err;
    }
    // 如果err.response存在，通常是axios错误，其data中可能有后端具体错误
    // 这一层已在 loginApi 中处理，这里直接用 err.message 即可
    
    ElMessage.error(errorMessage);
  } finally {
    loading.value = false;
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

.admin-link {
  text-align: center;
  color: #409eff;
  cursor: pointer;
  font-size: 14px;
  margin-top: 10px;
}

.admin-link span:hover {
  text-decoration: underline;
}
</style>
