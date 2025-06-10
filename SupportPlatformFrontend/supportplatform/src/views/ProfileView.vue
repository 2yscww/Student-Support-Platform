<template>
  <div class="profile-view" v-if="userProfile">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
          <div>
            <el-button type="primary" @click="showUpdateInfoDialog = true">修改个人信息</el-button>
            <el-button type="warning" @click="showUpdatePasswordDialog = true">修改密码</el-button>
          </div>
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

    <!-- 修改个人信息对话框 -->
    <el-dialog v-model="showUpdateInfoDialog" title="修改个人信息">
      <el-form :model="updateInfoForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="updateInfoForm.username"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showUpdateInfoDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdateInfo">确认</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="showUpdatePasswordDialog" title="修改密码">
      <el-form :model="updatePasswordForm" label-width="100px">
        <el-form-item label="旧密码">
          <el-input type="password" v-model="updatePasswordForm.oldPassword"></el-input>
        </el-form-item>
        <el-form-item label="新密码">
          <el-input type="password" v-model="updatePasswordForm.newPassword"></el-input>
        </el-form-item>
        <el-form-item label="确认新密码">
          <el-input type="password" v-model="updatePasswordForm.confirmPassword"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showUpdatePasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdatePassword">确认</el-button>
      </template>
    </el-dialog>
  </div>
  <div v-else>
    <el-skeleton :rows="5" animated />
  </div>
</template>

<script setup>
import { ref, onMounted, computed, reactive } from 'vue';
import { useUserStore } from '@/stores/user';
import { ElMessage } from 'element-plus';
import axios from 'axios'; // 确保你有一个配置好的 axios 实例

const userStore = useUserStore();
const userProfile = ref(null);
const showUpdateInfoDialog = ref(false);
const showUpdatePasswordDialog = ref(false);

const updateInfoForm = reactive({
  username: '',
});

const updatePasswordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
});

onMounted(async () => {
  try {
    await userStore.fetchUserProfile();
    userProfile.value = userStore.userProfile;
    // 初始化表单数据
    updateInfoForm.username = userProfile.value.username;
  } catch (error) {
    ElMessage.error('获取用户信息失败');
  }
});

const handleUpdateInfo = async () => {
  try {
    await axios.post('/api/user/update', updateInfoForm);
    ElMessage.success('信息更新成功');
    showUpdateInfoDialog.value = false;
    // 重新获取用户信息
    await userStore.fetchUserProfile();
    userProfile.value = userStore.userProfile;
    // 更新表单数据
    updateInfoForm.username = userProfile.value.username;
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '信息更新失败');
  }
};

const handleUpdatePassword = async () => {
  if (updatePasswordForm.newPassword !== updatePasswordForm.confirmPassword) {
    ElMessage.error('两次输入的新密码不一致');
    return;
  }
  try {
    await axios.post('/api/user/password/update', updatePasswordForm);
    ElMessage.success('密码修改成功');
    showUpdatePasswordDialog.value = false;
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '密码修改失败');
  }
};

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