<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <h2 class="register-title">创建账户</h2>
      </template>
      
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="rules"
        label-position="top"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱"
            type="email"
          >
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="验证码" prop="code">
          <el-input v-model="registerForm.code" placeholder="请输入6位验证码">
            <template #append>
              <el-button @click="handleSendCode" :disabled="isSendingCode">
                {{ isSendingCode ? `${countdown}s后重发` : "获取验证码" }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            placeholder="请输入密码 (至少6位)"
            type="password"
            show-password
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            placeholder="请再次输入密码"
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
            class="register-button"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>
        
        <el-form-item>
          <el-button type="text" @click="goToLogin" class="login-link-button">
            已有账户？前往登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User, Message, Lock } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const registerFormRef = ref(null)
const countdown = ref(0);
const isSendingCode = ref(false);
let timer = null;

const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  code: ''
})

// 自定义验证规则：确认密码
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  code: [{ required: true, message: "请输入验证码", trigger: "blur" }],
}

const handleSendCode = async () => {
  if (!registerFormRef.value) return;
  // 单独验证邮箱字段
  registerFormRef.value.validateField("email", async (isValid) => {
    if (isValid) {
      isSendingCode.value = true;
      countdown.value = 60;
      try {
        // 后端需要的是 query 参数，而不是 body
        await axios.post("/api/user/send-code", null, {
          params: { email: registerForm.email },
        });
        ElMessage.success("验证码已发送，请注意查收！");
        // 开始倒计时
        timer = setInterval(() => {
          countdown.value--;
          if (countdown.value <= 0) {
            clearInterval(timer);
            isSendingCode.value = false;
          }
        }, 1000);
      } catch (error) {
        console.error("Send code error:", error);
        if (error.response && error.response.data && error.response.data.message) {
          ElMessage.error(error.response.data.message);
        } else {
          ElMessage.error("验证码发送失败，请稍后重试");
        }
        isSendingCode.value = false;
        countdown.value = 0;
        if (timer) clearInterval(timer);
      }
    } else {
      ElMessage.warning("请输入有效的邮箱地址后再获取验证码");
    }
  });
};

const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    await registerFormRef.value.validate()
    loading.value = true

    const response = await axios.post('/api/user/register', {
      username: registerForm.username,
      email: registerForm.email,
      password: registerForm.password,
      confirmPassword: registerForm.confirmPassword,
      code: registerForm.code,
    })

    if (response.data.code === 200 && response.data.data) {
      const userData = response.data.data
      userStore.setToken(userData.token)
      userStore.setUserInfo({
        userId: userData.userId,
        username: userData.username,
        role: userData.role
      })
      
      ElMessage.success(response.data.message || '注册成功，已自动登录！')
      router.push('/task-hall')
    } else {
      ElMessage.error(response.data.message || '注册失败')
    }
  } catch (error) {
    console.error('Register error:', error)
    if (error.response && error.response.data && error.response.data.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('注册失败，请检查网络或联系管理员')
    }
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  router.push('/login')
}

onUnmounted(() => {
  if (timer) {
    clearInterval(timer);
  }
});
</script>

<style scoped>
.register-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
}

.register-card {
  width: 450px; /* 稍微加宽一点以便容纳更多字段 */
}

.register-title {
  text-align: center;
  color: #303133;
  margin: 0;
}

.register-button {
  width: 100%;
}

.login-link-button {
  width: 100%;
  padding-top: 0; /* 调整与上方按钮的间距 */
}

.el-form-item {
  margin-bottom: 22px; /* 调整表单项间距 */
}
</style> 