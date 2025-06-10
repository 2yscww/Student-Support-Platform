<template>
  <div class="profile-view" v-if="userProfile">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">{{ userProfile.username }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ userProfile.email }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ userProfile.userId }}</el-descriptions-item>
        <el-descriptions-item label="信誉分">{{ userProfile.creditScore }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(userProfile.status)">{{ userProfile.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag>{{ userProfile.role }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ formattedCreatedAt }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
  <div v-else>
    <el-skeleton :rows="5" animated />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useUserStore } from '@/stores/user';
import { ElMessage } from 'element-plus';
import axios from 'axios';

const userStore = useUserStore();
const userProfile = ref(null);

onMounted(async () => {
  try {
    const response = await axios.get('/api/admin/profile', {
      headers: {
        'Authorization': `Bearer ${userStore.token}`
      }
    });
    if (response.data && response.data.code === 200) {
      userProfile.value = response.data.data;
    } else {
      ElMessage.error(response.data.message || '获取管理员信息失败');
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '获取管理员信息失败');
  }
});

const formattedCreatedAt = computed(() => {
  if (userProfile.value && userProfile.value.createdAt) {
    return new Date(userProfile.value.createdAt).toLocaleString();
  }
  return '';
});

const statusType = (status) => {
  switch (status) {
    case 'ACTIVE':
      return 'success';
    case 'FROZEN':
      return 'warning';
    case 'BANNED':
      return 'danger';
    default:
      return 'info';
  }
};
</script>

<style scoped>
.profile-view {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 