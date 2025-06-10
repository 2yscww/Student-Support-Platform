<template>
  <div class="header-bar">
    <!-- 左侧折叠按钮 -->
    <div class="left">
      <el-button
        type="text"
        @click="toggleCollapse"
        class="collapse-btn"
      >
        <el-icon>
          <Fold v-if="!isCollapse" />
          <Expand v-else />
        </el-icon>
      </el-button>
      
      <!-- 面包屑导航 -->
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>{{ currentRoute }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 右侧用户信息 -->
    <div class="right" v-if="userStore.userInfo">
      
      <!-- 用户信息下拉菜单 -->
      <el-dropdown trigger="click">
        <div class="user-info">
          <el-avatar :size="32" :src="userStore.userInfo?.avatar">
            {{ userStore.userInfo?.username?.charAt(0)?.toUpperCase() }}
          </el-avatar>
          <span class="username">{{ userStore.userInfo?.username }}</span>
        </div>
        
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item divided @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRoute, useRouter } from 'vue-router'
import { 
  // Bell, // Bell icon is no longer needed
  // UserFilled, // UserFilled icon is no longer needed
  // Medal, // Medal icon is no longer needed
  SwitchButton,
  Fold,
  Expand
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['collapse-change'])

const userStore = useUserStore()

console.log('用户信息:', userStore.userInfo);
console.log('HeaderBar 用户信息:', userStore.userInfo);

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)

const currentRoute = computed(() => {
  return route.meta.title || route.name || '首页'
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
  // 触发一个自定义事件，让父组件知道折叠状态改变
  emit('collapse-change', isCollapse.value)
}

const handleLogout = async () => {
  try {
    await userStore.logout()
    // 使用 nextTick 确保 DOM 更新和异步操作完成后再跳转
    nextTick(() => {
      router.push('/login')
      ElMessage.success('退出登录成功')
    })
  } catch (error) {
    ElMessage.error('退出登录失败')
  }
}
</script>

<style scoped>
.header-bar {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.collapse-btn {
  padding: 0;
  font-size: 20px;
  cursor: pointer;
}

.collapse-btn :deep(.el-icon) {
  font-size: 20px;
}

.right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.notice-badge {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 0 4px;
}

.username {
  font-size: 14px;
  color: #606266;
  margin-right: 4px;
}

/* 添加一些悬停效果 */
.notice-badge:hover, 
.user-info:hover {
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style> 