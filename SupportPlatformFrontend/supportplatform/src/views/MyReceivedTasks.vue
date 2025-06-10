<template>
  <div class="my-received-tasks-container" v-loading="loading">
    <el-card v-if="error" class="error-card">
      <template #header>
        <div class="card-header">
          <span>Error</span>
        </div>
      </template>
      <el-alert
        title="Failed to load your received tasks"
        type="error"
        :description="error"
        show-icon
        :closable="false"
      />
      <el-button @click="fetchMyReceivedTasks" type="primary" style="margin-top: 20px;">Retry</el-button>
    </el-card>

    <el-card v-else>
      <template #header>
        <div class="card-header">
          <span>我的接单</span>
        </div>
      </template>
      <el-table :data="tasks" style="width: 100%" empty-text="您还没有接受任何任务">
        <el-table-column prop="orderId" label="任务ID" width="100" />
        <el-table-column prop="title" label="任务标题" width="200" />
        <el-table-column prop="description" label="任务描述" show-overflow-tooltip/>
        <el-table-column prop="posterUsername" label="发布者" width="150" />
        <el-table-column prop="reward" label="任务酬金" width="120">
          <template #default="scope">
            <span>¥ {{ scope.row.reward ? scope.row.reward.toFixed(2) : 'N/A' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getOrderStatusTagType(scope.row.orderStatus)">{{ getOrderStatusText(scope.row.orderStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewTaskDetails(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Task Details Dialog -->
    <el-dialog
      v-model="dialogVisible"
      title="任务详情"
      width="60%"
      :before-close="() => dialogVisible = false"
      draggable
      destroy-on-close
    >
      <div v-if="detailLoading" class="loading-area">
        <p>正在加载详情...</p>
      </div>
      <div v-else-if="!selectedOrderDetail" class="error-area">
        <el-alert
            title="无法加载任务详情"
            type="warning"
            description="未能获取到选定任务的详细信息，请重试。"
            show-icon
            :closable="false"
          />
      </div>
      <div v-else class="task-details-content">
        <p class="detail-item"><span class="detail-label">任务ID:</span> {{ selectedOrderDetail.orderId }}</p>
        <p class="detail-item"><span class="detail-label">任务标题:</span> {{ selectedOrderDetail.title }}</p>
        <p class="detail-item"><span class="detail-label">任务描述:</span> <span v-html="selectedOrderDetail.description?.replace(/\n/g, '<br/>')"></span></p>
        <p class="detail-item"><span class="detail-label">任务赏金:</span> ¥{{ selectedOrderDetail.reward ? selectedOrderDetail.reward.toFixed(2) : 'N/A' }}</p>
        <p class="detail-item"><span class="detail-label">任务状态:</span> <el-tag :type="getOrderStatusTagType(selectedOrderDetail.orderStatus)">{{ getOrderStatusText(selectedOrderDetail.orderStatus) }}</el-tag></p>
        <p class="detail-item"><span class="detail-label">发布者ID:</span> {{ selectedOrderDetail.publisherId }}</p>
        <p class="detail-item"><span class="detail-label">接单者ID:</span> {{ selectedOrderDetail.receiverId || '暂无' }}</p>
        <p class="detail-item"><span class="detail-label">发布时间:</span> {{ formatDate(selectedOrderDetail.createdAt) }}</p>
        <p class="detail-item"><span class="detail-label">任务确认时间:</span> {{ selectedOrderDetail.confirmedAt ? formatDate(selectedOrderDetail.confirmedAt) : '未确认' }}</p>
        <p class="detail-item"><span class="detail-label">支付状态:</span> {{ selectedOrderDetail.paymentStatus ? getPaymentStatusText(selectedOrderDetail.paymentStatus) : '未知' }}</p>
      
        <el-divider />
        
        <h4>评价信息</h4>
        <div v-if="reviewsLoading" class="loading-area">加载评价中...</div>
        <div v-else-if="taskReviews.length === 0" class="empty-state">暂无评价</div>
        <div v-else class="reviews-list">
            <div v-for="review in taskReviews" :key="review.reviewId" class="review-item">
                <p><strong>{{ review.reviewerUsername }}</strong> <el-rate v-model="review.rating" disabled show-score text-color="#ff9900" score-template="{value} 星" /> </p>
                <p>{{ review.content }}</p>
                <small>{{ formatDate(review.createdAt) }}</small>
            </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button v-if="canReport" type="danger" @click="openReportDialog">举报</el-button>
          <el-button
            v-if="canSubmitWork"
            type="primary"
            @click="openSubmitWorkDialog"
            :loading="actionLoading"
          >
            提交成果
          </el-button>
          <el-button
            v-if="canReview"
            type="primary"
            @click="openReviewDialog"
            :loading="actionLoading"
          >
            评价
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- Submit Work Dialog -->
    <el-dialog
      v-model="submitWorkDialogVisible"
      title="提交任务成果"
      width="50%"
      :before-close="() => submitWorkDialogVisible = false"
      destroy-on-close
    >
        <el-form :model="submitWorkForm" label-width="120px">
            <el-form-item label="成果描述">
                <el-input
                    v-model="submitWorkForm.description"
                    type="textarea"
                    :rows="5"
                    placeholder="请详细描述您的工作成果"
                />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="submitWorkDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitWorkHandler" :loading="actionLoading">
                    确认提交
                </el-button>
            </span>
        </template>
    </el-dialog>

    <!-- Report Dialog -->
    <el-dialog
      v-model="reportDialogVisible"
      title="提交举报"
      width="40%"
      :before-close="() => reportDialogVisible = false"
      destroy-on-close
    >
      <el-form :model="reportForm" label-width="120px">
        <el-form-item label="举报类型">
          <el-radio-group v-model="reportForm.reportType">
            <el-radio label="USER">举报发布者</el-radio>
            <el-radio label="TASK">举报任务</el-radio>
            <el-radio label="REVIEW" :disabled="reportableReviews.length === 0">举报评价</el-radio>
          </el-radio-group>
          <div v-if="reportableReviews.length === 0" style="font-size: 12px; color: #999; margin-top: 5px;">暂无可举报的评价</div>
        </el-form-item>
        
        <el-form-item v-if="reportForm.reportType === 'USER'" label="举报对象">
          <p>发布者: {{ selectedOrderDetail.posterUsername }} (ID: {{ selectedOrderDetail.publisherId }})</p>
        </el-form-item>
        <el-form-item v-if="reportForm.reportType === 'TASK'" label="举报对象">
          <p>任务: {{ selectedOrderDetail.title }} (ID: {{ selectedOrderDetail.orderId }})</p>
        </el-form-item>

        <el-form-item v-if="reportForm.reportType === 'REVIEW'" label="选择评价">
          <el-select v-model="reportForm.reportedReviewId" placeholder="请选择要举报的评价" style="width: 100%;">
            <el-option
              v-for="review in reportableReviews"
              :key="review.reviewId"
              :label="review.content"
              :value="review.reviewId">
              <span style="float: left; max-width: 70%; overflow: hidden; text-overflow: ellipsis;">{{ review.content }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">by {{ review.reviewerUsername }}</span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="举报原因">
          <el-input
            v-model="reportForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请详细描述举报原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="reportDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReportHandler" :loading="reportLoading">
            提交
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- Review Dialog -->
    <el-dialog
      v-model="reviewDialogVisible"
      title="提交评价"
      width="40%"
      :before-close="() => reviewDialogVisible = false"
      destroy-on-close
    >
      <el-form :model="reviewForm" label-width="120px">
        <el-form-item label="评价发布者">
            <p>{{ selectedOrderDetail.posterUsername }} (ID: {{ selectedOrderDetail.publisherId }})</p>
        </el-form-item>
        <el-form-item label="评分">
            <el-rate v-model="reviewForm.rating" :max="5" show-score text-color="#ff9900" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input
            v-model="reviewForm.content"
            type="textarea"
            :rows="4"
            placeholder="请填写您的评价"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="reviewDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReviewHandler" :loading="reviewLoading">
            提交评价
          </el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { ElMessage, ElTable, ElTableColumn, ElCard, ElTag, ElButton, ElAlert, ElDialog, ElForm, ElInput, ElDivider, ElRate } from 'element-plus';

const tasks = ref([]);
const loading = ref(true);
const error = ref(null);
const dialogVisible = ref(false);
const selectedOrderDetail = ref(null);
const detailLoading = ref(false);
const actionLoading = ref(false);
const submitWorkDialogVisible = ref(false);
const submitWorkForm = ref({ description: '' });
const reportDialogVisible = ref(false);
const reportForm = ref({ reportType: 'USER', reason: '', reportedReviewId: null });
const reportLoading = ref(false);
const reviewDialogVisible = ref(false);
const reviewForm = ref({ rating: 5, content: '' });
const reviewLoading = ref(false);
const reviewsLoading = ref(false);
const taskReviews = ref([]);
const userInfo = ref(null);

const getOrderStatusText = (status) => {
  switch (status) {
    case 'UNASSIGNED': return '无人接单';
    case 'APPLIED': return '已申请';
    case 'ACCEPTED': return '已接受';
    case 'SUBMITTED': return '已提交';
    case 'CONFIRMED': return '已确认';
    case 'CANCELLED': return '已取消';
    default: return status;
  }
};

const getOrderStatusTagType = (status) => {
  switch (status) {
    case 'UNASSIGNED': return 'info';
    case 'APPLIED': return 'warning';
    case 'ACCEPTED': return 'primary';
    case 'SUBMITTED': return 'warning';
    case 'CONFIRMED': return 'success';
    case 'CANCELLED': return 'info';
    default: return '';
  }
};

const getPaymentStatusText = (status) => {
  switch (status) {
    case 'UNPAID': return '未支付';
    case 'PAID': return '已支付';
    default: return status;
  }
};

const fetchMyReceivedTasks = async () => {
  loading.value = true;
  error.value = null;
  try {
    const response = await axios.get('/api/orders/my-received');
    if (response.data && Array.isArray(response.data.data)) {
      tasks.value = response.data.data;
    } else {
      tasks.value = [];
    }
  } catch (err) {
    error.value = err.message || 'An unknown error occurred while fetching your received tasks.';
    ElMessage.error('获取我接受的任务列表失败: ' + error.value);
    tasks.value = [];
  } finally {
    loading.value = false;
  }
};

const formatDate = (dateString) => {
  if (!dateString) return 'N/A';
  // Replace space with 'T' for better browser compatibility
  const date = new Date(dateString.replace(' ', 'T'));
  if (isNaN(date.getTime())) {
    return dateString;
  }
  return date.toLocaleString('zh-CN', { hour12: false });
};

const fetchOrderDetails = async (orderId) => {
  detailLoading.value = true;
  taskReviews.value = []; // Reset
  try {
    const response = await axios.post('/api/orders/detail', { orderId: orderId });
    if (response.data && response.data.code === 200) {
        selectedOrderDetail.value = response.data.data;
        await fetchReviewsForOrder(selectedOrderDetail.value.orderId);
    } else {
        selectedOrderDetail.value = null;
        ElMessage.error(`获取任务详情失败: ${response.data.msg}`);
    }
  } catch (err) {
    selectedOrderDetail.value = null;
    ElMessage.error('获取任务详情网络请求失败');
  } finally {
    detailLoading.value = false;
  }
};

const viewTaskDetails = async (task) => {
  const orderIdToFetch = task.orderId || task.id;
  if (!orderIdToFetch) {
      ElMessage.error('无法获取任务ID以查看详情');
      return;
  }
  await fetchOrderDetails(orderIdToFetch);
  if (selectedOrderDetail.value) {
      dialogVisible.value = true;
  }
};

const canSubmitWork = computed(() => {
    return selectedOrderDetail.value && selectedOrderDetail.value.orderStatus === 'ACCEPTED';
});

const canReport = computed(() => {
    if (!selectedOrderDetail.value || !userInfo.value || !selectedOrderDetail.value.publisherId) {
        return false;
    }
    // Receiver (current user) cannot report themselves (if they are also the publisher somehow)
    return selectedOrderDetail.value.publisherId !== userInfo.value.userId;
});

const canReview = computed(() => {
    if (!selectedOrderDetail.value || !userInfo.value) return false;
    if (selectedOrderDetail.value.orderStatus !== 'CONFIRMED') return false;
    return !taskReviews.value.some(r => r.reviewerId === userInfo.value.userId);
});

const reportableReviews = computed(() => {
    if (!selectedOrderDetail.value || !userInfo.value) return [];
    // Receiver can report reviews written about them.
    return taskReviews.value.filter(r => r.revieweeId === userInfo.value.userId);
});

const openSubmitWorkDialog = () => {
    submitWorkForm.value = { description: '' };
    submitWorkDialogVisible.value = true;
};

const submitWorkHandler = async () => {
    if (!submitWorkForm.value.description.trim()) {
        ElMessage.warning('成果描述不能为空');
        return;
    }
    actionLoading.value = true;
    try {
        const payload = {
            orderId: selectedOrderDetail.value.orderId,
            description: submitWorkForm.value.description,
        };
        const response = await axios.post('/api/orders/submit', payload);
        if (response.data.code === 200) {
            ElMessage.success('成果提交成功！');
            submitWorkDialogVisible.value = false;
            dialogVisible.value = false;
            fetchMyReceivedTasks();
        } else {
            ElMessage.error(`提交失败: ${response.data.msg}`);
        }
    } catch (err) {
        ElMessage.error('提交成果失败，请稍后重试。');
    } finally {
        actionLoading.value = false;
    }
};

const openReportDialog = () => {
  reportForm.value = { reportType: 'USER', reason: '', reportedReviewId: null };
    if (reportableReviews.value.length === 0) {
        reportForm.value.reportType = 'USER';
    }
  reportDialogVisible.value = true;
};

const submitReportHandler = async () => {
  if (!reportForm.value.reason.trim()) {
    ElMessage.warning('举报原因不能为空');
    return;
  }

  reportLoading.value = true;
  try {
    const payload = {
      reportType: reportForm.value.reportType,
      reason: reportForm.value.reason.trim(),
    };

    switch (payload.reportType) {
        case 'USER':
            if (!selectedOrderDetail.value || !selectedOrderDetail.value.publisherId) {
                ElMessage.error('无法获取发布者信息，举报失败');
                reportLoading.value = false;
                return;
            }
            payload.reportedUserId = selectedOrderDetail.value.publisherId;
            break;
        case 'TASK':
            payload.reportedOrderId = selectedOrderDetail.value.orderId;
            break;
        case 'REVIEW':
            if (!reportForm.value.reportedReviewId) {
                ElMessage.warning('请选择要举报的评价');
                reportLoading.value = false;
                return;
            }
            payload.reportedReviewId = reportForm.value.reportedReviewId;
            break;
    }

    const response = await axios.post('/api/reports/submit', payload);
    if (response.data && response.data.code === 200) {
      ElMessage.success('举报已提交，我们会尽快处理！');
      reportDialogVisible.value = false;
    } else {
      ElMessage.error(`举报失败: ${response.data.msg || '未知错误'}`);
    }
  } catch (err) {
    ElMessage.error('提交举报时发生错误，请稍后重试。');
  } finally {
    reportLoading.value = false;
  }
};

const openReviewDialog = () => {
    reviewForm.value = { rating: 5, content: '' };
    reviewDialogVisible.value = true;
};

const submitReviewHandler = async () => {
    if (reviewForm.value.rating === 0) {
        ElMessage.warning('评分不能为空');
        return;
    }
    if (!reviewForm.value.content.trim()) {
        ElMessage.warning('评价内容不能为空');
        return;
    }
    reviewLoading.value = true;
    try {
        const payload = {
            orderId: selectedOrderDetail.value.orderId, 
            revieweeId: selectedOrderDetail.value.publisherId, // Reviewing the publisher
            rating: reviewForm.value.rating,
            content: reviewForm.value.content,
        };
        const response = await axios.post('/api/reviews/submit', payload);
        if (response.data.code === 200) {
            ElMessage.success('评价提交成功！');
            reviewDialogVisible.value = false;
            fetchReviewsForOrder(selectedOrderDetail.value.orderId);
        } else {
            ElMessage.error(`评价失败: ${response.data.msg}`);
        }
    } catch (err) {
        ElMessage.error('提交评价时发生错误，请稍后重试。');
    } finally {
        reviewLoading.value = false;
    }
};

const fetchReviewsForOrder = async (orderId) => {
    if (!orderId) return;
    reviewsLoading.value = true;
    try {
        const response = await axios.post('/api/reviews/order', { orderId: orderId });
        if (response.data.code === 200 && Array.isArray(response.data.data)) {
            taskReviews.value = response.data.data;
        } else {
            taskReviews.value = [];
        }
    } catch (err) {
        taskReviews.value = [];
        ElMessage.error('获取评价列表失败');
    } finally {
        reviewsLoading.value = false;
    }
};

onMounted(() => {
    const storedUserInfo = localStorage.getItem('userInfo');
    if (storedUserInfo) {
        userInfo.value = JSON.parse(storedUserInfo);
    }
    fetchMyReceivedTasks();
});
</script>

<style scoped>
.my-received-tasks-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.error-card {
  border-color: var(--el-color-error);
}
.task-details-content .detail-label {
  font-weight: bold;
  margin-right: 8px;
}
.detail-item {
  margin-bottom: 12px;
}
.reviews-list {
    margin-top: 10px;
}
.review-item {
    border-top: 1px solid #ebeef5;
    padding: 10px 0;
}
.review-item p {
    margin: 5px 0;
}
</style> 