<template>
  <el-menu
    :default-active="activeMenu"
    class="side-menu"
    :collapse="props.isCollapse"
    @select="handleSelect"
  >
    <!-- 任务大厅 - 所有人可见 -->
    <el-menu-item index="/task-hall">
      <el-icon><List /></el-icon>
      <template #title>任务大厅</template>
    </el-menu-item>

    <!-- 已登录用户可见的内容 -->
    <template v-if="isLoggedIn">
      <!-- 管理员菜单 -->
      <template v-if="isAdmin">
        <el-menu-item index="/admin/user-management">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/reports">
          <el-icon><Warning /></el-icon>
          <template #title>举报管理</template>
        </el-menu-item>
      </template>

      <!-- 普通用户菜单 -->
      <template v-else>
        <el-sub-menu index="task-management">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>任务管理</span>
          </template>
          <el-menu-item index="/task/publish">
            <el-icon><EditPen /></el-icon>
            <template #title>发布任务</template>
          </el-menu-item>
          <el-menu-item index="/my-published-tasks">
            <el-icon><Upload /></el-icon>
            <template #title>我的发布</template>
          </el-menu-item>
          <el-menu-item index="/my-received-tasks">
            <el-icon><Download /></el-icon>
            <template #title>我的接单</template>
          </el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/my-reports">
          <el-icon><Warning /></el-icon>
          <template #title>举报管理</template>
        </el-menu-item>
      </template>

      <!-- 通用：个人中心、退出 -->
      <el-menu-item :index="isAdmin ? '/admin-profile' : '/profile'">
        <el-icon><UserFilled /></el-icon>
        <template #title>个人中心</template>
      </el-menu-item>
      <el-menu-item @click="handleLogout">
        <el-icon><SwitchButton /></el-icon>
        <template #title>退出登录</template>
      </el-menu-item>
    </template>

    <!-- 未登录用户菜单 -->
    <template v-else>
      <el-menu-item index="/login">
        <el-icon><Key /></el-icon>
        <template #title>登录</template>
      </el-menu-item>
      <el-menu-item index="/register">
        <el-icon><Edit /></el-icon>
        <template #title>注册</template>
      </el-menu-item>
    </template>
  </el-menu>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'
import {
  List,
  Document,
  User,
  Warning,
  UserFilled,
  SwitchButton,
  Upload,
  Download,
  Key,
  Edit,
  EditPen
} from '@element-plus/icons-vue'

// ✅ 正确方式：不解构 props，保持响应性
const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
})

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const isAdmin = computed(() => userStore.userInfo?.role === 'ADMIN')
const isLoggedIn = computed(() => !!userStore.token)

const handleSelect = (index) => {
  router.push(index)
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    userStore.logout()
    router.push('/login')
  } catch {
    // 用户取消
  }
}
</script>


<style scoped>
.side-menu {
  height: 100vh;
  border-right: solid 1px #e6e6e6;
}

.side-menu:not(.el-menu--collapse) {
  width: 200px;
}

.side-menu.el-menu--collapse {
  width: 64px;
}

.el-menu-item, .el-sub-menu__title {
  display: flex;
  align-items: center;
}

.el-icon {
  margin-right: 8px;
}

:deep(.el-menu-item.is-active) {
  background-color: #ecf5ff;
}
</style>
