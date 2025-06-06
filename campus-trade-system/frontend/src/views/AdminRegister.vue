<template>
    <div class="admin-register-container">
      <div class="register-bg" />
      <el-card class="box-card">
        <div class="title-row">
          <el-icon size="24"><UserFilled /></el-icon>
          <h2>管理员注册</h2>
        </div>
  
        <el-alert
          title="注意"
          type="warning"
          :closable="false"
          style="margin-bottom: 20px;"
        >
          这是临时的管理员注册界面，仅用于创建首个管理员账号。请妥善保管管理员账号信息。
        </el-alert>
  
        <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入管理员用户名" />
          </el-form-item>
  
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
          </el-form-item>
  
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" show-password placeholder="请再次输入密码" />
          </el-form-item>
  
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" placeholder="请输入邮箱" />
          </el-form-item>
  
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="handleSubmit" style="width: 100%">
              创建管理员账号
            </el-button>
          </el-form-item>
  
          <div class="switch-link">
            <span @click="$router.push('/')">
              返回登录页面
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
  import { UserFilled } from '@element-plus/icons-vue'
  import axios from 'axios'
  
  const router = useRouter()
  const loading = ref(false)
  const formRef = ref()
  
  const form = reactive({
    username: '',
    password: '',
    confirmPassword: '',
    email: ''
  })
  
  const rules = reactive({
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 3, max: 20, message: '用户名长度应为3-20个字符', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
    ],
    email: [
      { required: true, message: '请输入邮箱地址', trigger: 'blur' },
      { 
        type: 'email', 
        message: '请输入正确的邮箱地址', 
        trigger: ['blur', 'change']
      }
    ],
    confirmPassword: [
      { required: true, message: '请再次输入密码', trigger: 'blur' },
      {
        validator: (_, value) => {
          if (value !== form.password) {
            return Promise.reject('两次密码不一致');
          }
          return Promise.resolve();
        },
        trigger: 'blur'
      }
    ]
  })
  
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
      const registerData = {
        username: form.username,
        password: form.password,
        email: form.email
      };
  
      const response = await axios.post('/api/auth/register-admin', registerData);
      
      ElMessage.success(`管理员账号创建成功！用户名: ${form.username}`);
      ElMessage.info('请返回登录页面使用新创建的管理员账号登录');
      
      // 清空表单
      Object.keys(form).forEach(key => {
        form[key] = '';
      });
      if (formRef.value) {
        formRef.value.resetFields();
      }
      
    } catch (error) {
      console.error("Admin register error:", error);
      
      let errorMessage = '创建管理员账号失败';
      if (error.response?.data) {
        if (typeof error.response.data === 'string') {
          errorMessage = error.response.data;
        } else if (error.response.data.message) {
          errorMessage = error.response.data.message;
        }
      }
      
      ElMessage.error(errorMessage);
    } finally {
      loading.value = false;
    }
  }
  </script>
  
  <style scoped>
  .admin-register-container {
    position: relative;
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .register-bg {
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, #667eea, #764ba2);
    z-index: -1;
  }
  
  .box-card {
    width: 450px;
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
    color: #333;
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