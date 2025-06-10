<template>
  <div class="publish-task-container">
    <el-card class="publish-task-card">
      <template #header>
        <h2 class="publish-task-title">发布新任务</h2>
      </template>

      <el-form
        ref="publishFormRef"
        :model="taskForm"
        :rules="rules"
        label-position="top"
        label-width="100px"
      >
        <el-form-item label="任务标题" prop="title">
          <el-input
            v-model="taskForm.title"
            placeholder="请输入任务标题 (例如：帮忙代取快递)"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="任务描述" prop="description">
          <el-input
            v-model="taskForm.description"
            type="textarea"
            :rows="4"
            placeholder="请详细描述任务内容、要求等"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="悬赏金额 (元)" prop="reward">
              <el-input-number
                v-model="taskForm.reward"
                :precision="2"
                :step="1"
                :min="0.01"
                placeholder="例如：5.00"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleSubmit"
            class="submit-button"
          >
            立即发布
          </el-button>
          <el-button @click="handleResetForm" :disabled="loading">
            重置表单
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import axios from 'axios'; // Temporarily commented out to fix lint error
// import { useUserStore } from '@/stores/user'; // 如果需要获取用户信息（例如 poster_id）

const router = useRouter();
// const userStore = useUserStore(); // 如果需要
const publishFormRef = ref(null);
const loading = ref(false);

const taskForm = reactive({
  title: '',
  description: '',
  reward: null,
});

const rules = reactive({
  title: [
    { required: true, message: '请输入任务标题', trigger: 'blur' },
    { min: 5, max: 100, message: '标题长度应为 5 到 100 个字符', trigger: 'blur' },
  ],
  description: [
    { required: true, message: '请输入任务描述', trigger: 'blur' },
    { min: 10, max: 500, message: '描述长度应为 10 到 500 个字符', trigger: 'blur' },
  ],
  reward: [
    { required: true, message: '请输入悬赏金额', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '悬赏金额必须大于0', trigger: 'blur' },
  ],
});

const handleSubmit = async () => {
  if (!publishFormRef.value) return;
  await publishFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        // const posterId = userStore.userInfo?.userId; // 获取当前用户ID作为发布者ID
        // if (!posterId) {
        //   ElMessage.error('无法获取用户信息，请重新登录');
        //   loading.value = false;
        //   return;
        // }

        const taskData = {
          title: taskForm.title,
          description: taskForm.description,
          reward: taskForm.reward,
          // posterId: posterId, // 如果后端需要发布者ID，在这里添加
          // deadline: taskForm.deadline, // 根据之前的分析，后端OrderDTO不接收deadline
        };
        
        // --- 占位API，请替换为您的实际后端API ---
        console.log('Submitting task data:', taskData);
        const response = await axios.post('/api/orders/publish', taskData); // 示例API
        // 假设后端返回结构与登录/注册类似：{ code: 200, message: '...', data: ... }
        
        // 模拟API调用
        // await new Promise(resolve => setTimeout(resolve, 1000)); 
        // const mockResponse = { data: { code: 200, message: '任务发布成功！' }}; // 模拟成功响应

        if (response.data.code === 200) {
          ElMessage.success(response.data.message || '任务发布成功！');
          router.push('/task-hall'); // 或跳转到用户发布的任务列表页
        } else {
          ElMessage.error(response.data.message || '任务发布失败，请稍后再试。');
        }
      } catch (error) {
        console.error('Error publishing task:', error);
        ElMessage.error(error.response?.data?.message || '任务发布过程中发生错误，请检查网络或联系管理员。');
      } finally {
        loading.value = false;
      }
    } else {
      ElMessage.error('表单校验失败，请检查输入项。');
      return false;
    }
  });
};

const handleResetForm = () => {
  if (publishFormRef.value) {
    publishFormRef.value.resetFields();
  }
};
</script>

<style scoped>
.publish-task-container {
  padding: 20px;
  display: flex;
  justify-content: center;
}

.publish-task-card {
  width: 100%;
  max-width: 800px;
}

.publish-task-title {
  text-align: center;
  color: #303133;
  margin: 0 0 20px 0; /* 为标题下方增加一些间距 */
}

.submit-button {
  margin-right: 10px;
}
</style> 