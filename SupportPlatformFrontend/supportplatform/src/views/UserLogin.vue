<template>
    <div class="login-container">
      <el-card class="login-card">
        <template #header>
          <h2 class="login-title">学生互助平台</h2>
        </template>
        
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="rules"
          label-position="top"
        >
          <el-form-item label="邮箱" prop="email">
            <el-input
              v-model="loginForm.email"
              placeholder="请输入邮箱"
              type="email"
            >
              <template #prefix>
                <el-icon><Message /></el-icon>
              </template>
            </el-input>
          </el-form-item>
  
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="loginForm.password"
              placeholder="请输入密码"
              type="password"
              show-password
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
  
          <el-form-item>
            <el-button
              type="primary"
              :loading="loading"
              class="login-button"
              @click="handleLogin"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive } from 'vue'
  import { useRouter } from 'vue-router'
  import { useUserStore } from '@/stores/user'
  import { ElMessage } from 'element-plus'
  import { Message, Lock } from '@element-plus/icons-vue'
  import axios from 'axios'
  
  const router = useRouter()
  const userStore = useUserStore()
  const loading = ref(false)
  const loginFormRef = ref(null)
  
  // 登录表单数据
  const loginForm = reactive({
    email: '',
    password: ''
  })
  
  // 表单验证规则
  const rules = {
    email: [
      { required: true, message: '请输入邮箱', trigger: 'blur' },
      { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
    ]
  }
  
  // 处理登录
  const handleLogin = async () => {
    if (!loginFormRef.value) return
  
    try {
      // 表单验证
      await loginFormRef.value.validate()
      
      loading.value = true
      
      // 发送登录请求
      const response = await axios.post('/api/user/login', {
        email: loginForm.email,
        password: loginForm.password
      })
      
      if (response.data.code === 200) {
        // 存储token和用户信息
        const userData = response.data.data
        userStore.setToken(userData.token)
        userStore.setUserInfo({
          userId: userData.userId,
          username: userData.username,
          role: userData.role || 'USER'  // 存储用户角色，如果后端没返回则默认为USER
        })
        
        ElMessage.success('登录成功')
        router.push('/task-hall')
      } else {
        ElMessage.error(response.data.message || '登录失败')
      }
    } catch (error) {
      console.error('Login error:', error)
      ElMessage.error(
        error.response?.data?.message || 
        '登录失败，请检查网络连接或联系管理员'
      )
    } finally {
      loading.value = false
    }
  }
  </script>
  
  <style scoped>
  .login-container {
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #f0f2f5;
  }
  
  .login-card {
    width: 400px;
  }
  
  .login-title {
    text-align: center;
    color: #303133;
    margin: 0;
  }
  
  .login-button {
    width: 100%;
  }
  
  .el-form-item {
    margin-bottom: 25px;
  }
  </style> 