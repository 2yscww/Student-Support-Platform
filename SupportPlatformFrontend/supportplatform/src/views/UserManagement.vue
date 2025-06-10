<template>
  <div class="user-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
        </div>
      </template>

      <el-table :data="users" v-loading="loading" style="width: 100%">
        <el-table-column prop="userId" label="用户ID" width="100"></el-table-column>
        <el-table-column prop="username" label="用户名" width="150"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="200"></el-table-column>
        <el-table-column prop="creditScore" label="信誉分" width="100"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="statusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="role" label="角色" width="100">
          <template #default="scope">
            <el-tag>{{ scope.row.role }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="scope">
            <el-button
              size="small"
              type="warning"
              @click="handleFreeze(scope.row)"
              :disabled="scope.row.status === 'FROZEN' || scope.row.role === 'ADMIN'"
            >
              冻结
            </el-button>
            <el-button
              size="small"
              type="success"
              @click="handleUnfreeze(scope.row)"
              :disabled="scope.row.status === 'ACTIVE'"
            >
              解冻
            </el-button>
             <el-button
              size="small"
              type="danger"
              @click="handleBan(scope.row)"
              :disabled="scope.row.status === 'BANNED' || scope.row.role === 'ADMIN'"
            >
              封禁
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useUserStore } from '@/stores/user';

const users = ref([]);
const loading = ref(true);
const userStore = useUserStore();

const fetchUsers = async () => {
  loading.value = true;
  try {
    const response = await axios.get('/api/admin/users/list', {
      headers: { 'Authorization': `Bearer ${userStore.token}` }
    });
    if (response.data.code === 200) {
      users.value = response.data.data;
    } else {
      ElMessage.error(response.data.message || '获取用户列表失败');
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '获取用户列表失败');
  } finally {
    loading.value = false;
  }
};

onMounted(fetchUsers);

const updateUserStatus = async (user, status, actionText) => {
  try {
    await ElMessageBox.confirm(`确定要${actionText}用户 "${user.username}" 吗?`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    const endpoint = status === 'ACTIVE' ? '/api/admin/users/unfreeze' : '/api/admin/users/freeze';
    
    await axios.post(endpoint, 
      { userId: user.userId, status: status },
      { headers: { 'Authorization': `Bearer ${userStore.token}` } }
    );
    
    ElMessage.success(`用户 ${user.username} 已${actionText}`);
    fetchUsers(); // Refresh the list
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || `${actionText}用户失败`);
    }
  }
};

const handleFreeze = (user) => {
  updateUserStatus(user, 'FROZEN', '冻结');
};

const handleUnfreeze = (user) => {
  updateUserStatus(user, 'ACTIVE', '解冻');
};

const handleBan = (user) => {
  updateUserStatus(user, 'BANNED', '封禁');
};

const statusType = (status) => {
  switch (status) {
    case 'ACTIVE': return 'success';
    case 'FROZEN': return 'warning';
    case 'BANNED': return 'danger';
    default: return 'info';
  }
};

const formatDateTime = (dateTime) => {
  if (!dateTime) return '';
  return new Date(dateTime).toLocaleString();
};
</script>

<style scoped>
.user-management {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 