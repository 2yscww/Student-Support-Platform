<template>
  <div class="task-hall-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>任务大厅</span>
          <!-- Placeholder for a refresh button or other controls -->
          <el-button type="primary" @click="fetchOrders" :loading="loading">刷新任务</el-button>
        </div>
      </template>

      <div v-if="loading" class="loading-area">
        <p>正在加载任务...</p>
      </div>
      <div v-else-if="error" class="error-area">
         <el-alert
            title="加载失败"
            type="error"
            :description="error"
            show-icon
            :closable="false"
          />
      </div>
      <div v-else-if="orders.length === 0" class="empty-state">
        <el-alert
            title="当前没有可用的任务"
            type="info"
            description="请稍后再试或检查您的网络连接。"
            show-icon
            :closable="false"
          />
      </div>
      <el-table v-else :data="orders" style="width: 100%">
        <el-table-column prop="orderId" label="任务ID" width="100" />
        <el-table-column prop="title" label="任务标题" show-overflow-tooltip />
        <el-table-column prop="description" label="任务描述" show-overflow-tooltip />
        <el-table-column prop="reward" label="任务赏金" width="100">
          <template #default="scope">
            <span>¥{{ scope.row.reward.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="任务状态" width="120">
           <template #default="scope">
            <el-tag :type="getOrderStatusTagType(scope.row.orderStatus)">
              {{ getOrderStatusText(scope.row.orderStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发布时间" width="180">
          <template #default="scope">
            <span>{{ formatDate(scope.row.createdAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewTaskDetails(scope.row)">查看详情</el-button>
            <!-- Add more actions here, e.g., apply for task -->
          </template>
        </el-table-column>
      </el-table>
      
      <!-- Pagination -->
      <div v-if="totalOrders > 0" class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="totalOrders"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchOrders"
          @current-change="fetchOrders"
        />
      </div>
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
        <!-- Details will be populated here based on selectedOrderDetail -->
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
         <!-- Add more fields as needed based on your OrderDTO and database structure -->
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button type="danger" @click="reportTask">举报</el-button>
          <el-button
            v-if="canApply"
            type="primary"
            @click="applyForOrderHandler"
            :loading="applyLoading"
          >
            接单
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
            <el-radio value="TASK">举报任务</el-radio>
            <el-radio value="USER">举报发布者</el-radio>
          </el-radio-group>
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
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios' // Assuming axios is configured globally or installed
import { ElMessage, ElTable, ElTableColumn, ElCard, ElTag, ElButton, ElAlert, ElDialog, ElForm, ElFormItem, ElRadioGroup, ElRadio, ElInput } from 'element-plus'

const orders = ref([])
const loading = ref(true)
const error = ref(null)
const dialogVisible = ref(false) // For controlling the dialog visibility
const selectedOrderDetail = ref(null) // To store details of the selected order
const detailLoading = ref(false) // Loading state for fetching details
const applyLoading = ref(false) // For apply button loading state
const userInfo = ref(null) // To store user info
const reportDialogVisible = ref(false)
const reportForm = ref({
  reportType: 'TASK',
  reason: ''
})
const reportLoading = ref(false)

// Placeholder for pagination - adapt as needed
// const totalOrders = ref(0);
// const currentPage = ref(1);
// const pageSize = ref(10); // Example page size

const getOrderStatusText = (status) => {
  switch (status) {
    case 'UNASSIGNED': return '无人接单';
    case 'APPLIED': return '已申请';
    case 'ACCEPTED': return '已接受';
    case 'SUBMITTED': return '已提交';
    case 'CONFIRMED': return '已确认';
    case 'CANCELLED': return '已取消';
    default: return status; // Fallback for unknown status
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
    default: return ''; // Default tag type
  }
};

const getPaymentStatusText = (status) => {
  switch (status) {
    case 'UNPAID': return '未支付';
    case 'PAID': return '已支付';
    default: return status;
  }
};

const fetchOrders = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await axios.get('/api/orders/list')
    console.log('API Response Data for orders list:', response.data); // Log the raw response data

    if (Array.isArray(response.data)) {
      orders.value = response.data;
    } else if (response.data && Array.isArray(response.data.data)) {
      orders.value = response.data.data;
    } else if (response.data && Array.isArray(response.data.list)) {
      orders.value = response.data.list;
    } else if (response.data && Array.isArray(response.data.content)) {
      orders.value = response.data.content;
    } else if (response.data && Array.isArray(response.data.items)) {
      orders.value = response.data.items;
    } else {
      orders.value = [];
      console.error('Orders data is not in the expected array format. Received:', response.data);
      // error.value = 'Received data is not in the expected format.';
      // ElMessage.error('任务数据格式不正确，请检查API响应。');
    }
  } catch (err) {
    console.error('Failed to fetch orders:', err)
    error.value = err.message || 'An unknown error occurred while fetching tasks.'
    ElMessage.error('获取任务列表失败: ' + error.value)
    orders.value = [];
  } finally {
    loading.value = false
  }
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', { hour12: false }) // Use a more common format
}

const canApply = computed(() => {
  if (!selectedOrderDetail.value || !userInfo.value || !userInfo.value.userId) {
    return false
  }
  // Poster cannot apply for their own task
  if (selectedOrderDetail.value.publisherId === userInfo.value.userId) {
    return false
  }
  // Can only apply if the task is unassigned
  return selectedOrderDetail.value.orderStatus === 'UNASSIGNED'
})

const applyForOrderHandler = async () => {
  if (!selectedOrderDetail.value || !userInfo.value) {
    ElMessage.warning('无法获取任务信息或用户信息')
    return
  }

  if (!selectedOrderDetail.value.taskId) {
    ElMessage.error('无法获取任务ID，操作失败')
    console.error('Task ID is missing from selectedOrderDetail', selectedOrderDetail.value)
    return
  }

  applyLoading.value = true
  try {
    const payload = {
      orderId: selectedOrderDetail.value.orderId,
      taskId: selectedOrderDetail.value.taskId,
      receiverId: userInfo.value.userId
    }
    const response = await axios.post('/api/orders/apply', payload)

    if (response.data && response.data.code === 200) {
      ElMessage.success('接单成功!')
      dialogVisible.value = false
      await fetchOrders()
    } else {
      ElMessage.error(`操作失败: ${response.data.msg || '未知错误'}`)
    }
  } catch (err) {
    console.error('Failed to apply for order:', err)
    ElMessage.error('接单失败，请稍后重试。')
  } finally {
    applyLoading.value = false
  }
}

const reportTask = () => {
  reportForm.value.reason = ''
  reportForm.value.reportType = 'TASK'
  reportDialogVisible.value = true
}

const submitReportHandler = async () => {
  if (!reportForm.value.reason.trim()) {
    ElMessage.warning('举报原因不能为空')
    return
  }
  if (!selectedOrderDetail.value) {
    ElMessage.error('无法获取任务信息，举报失败')
    return
  }

  reportLoading.value = true
  try {
    const payload = {
      reportType: reportForm.value.reportType,
      reason: reportForm.value.reason.trim()
    }

    if (payload.reportType === 'TASK') {
      payload.reportedOrderId = selectedOrderDetail.value.orderId
    } else if (payload.reportType === 'USER') {
      payload.reportedUserId = selectedOrderDetail.value.publisherId
    }

    const response = await axios.post('/api/reports/submit', payload)

    if (response.data && response.data.code === 200) {
      ElMessage.success('举报已提交，我们会尽快处理！')
      reportDialogVisible.value = false
    } else {
      ElMessage.error(`举报失败: ${response.data.msg || '未知错误'}`)
    }
  } catch (err) {
    console.error('Failed to submit report:', err)
    ElMessage.error('提交举报时发生错误，请稍后重试。')
  } finally {
    reportLoading.value = false
  }
}

const fetchOrderDetails = async (orderId) => {
  if (!orderId) {
    ElMessage.error('订单ID无效');
    return;
  }

  // For debugging:
  console.log("Current token:", localStorage.getItem('token'));
  console.log("Current userInfo:", JSON.parse(localStorage.getItem('userInfo')));
  console.log("Requesting details for orderId:", orderId); 

  detailLoading.value = true;
  selectedOrderDetail.value = null;
  try {
    // 修改为POST请求，并将orderId放在请求体中
    const response = await axios.post('/api/orders/detail', { orderId: orderId });
    console.log('API Response Data for order detail:', response.data);

    // 修改响应处理逻辑以匹配 {code, data, msg} 结构
    if (response.data && response.data.code === 200 && response.data.data) {
        selectedOrderDetail.value = response.data.data;
    } else if (response.data && response.data.msg) { // 处理后端业务失败或成功但无数据（通过msg判断）的情况
        selectedOrderDetail.value = null;
        console.error('Failed to fetch order details (API error/no data):', response.data.msg);
        // 如果后端明确返回非200的code，也可以在这里进一步判断
        ElMessage.error(`获取任务详情失败: ${response.data.msg}`);
    }
    else {
        selectedOrderDetail.value = null;
        console.error('Order detail data is not in the expected format. Received:', response.data);
        ElMessage.error('获取任务详情失败：返回的数据格式不正确');
    }
  } catch (err) {
    console.error(`Failed to fetch order details for ID ${orderId}:`, err);
    selectedOrderDetail.value = null;
    // 响应拦截器应该已经处理了axios级别的网络错误或HTTP状态错误 (像之前的403)
    // ElMessage.error(`获取任务详情失败: ${err.message || '未知网络或服务器错误'}`);
  } finally {
    detailLoading.value = false;
  }
};

const viewTaskDetails = async (task) => {
  // Assuming task object has an 'orderId' property
  // If your list uses 'id' as orderId, then use task.id
  const orderIdToFetch = task.orderId || task.id; 
  if (!orderIdToFetch) {
      ElMessage.error('无法获取任务ID以查看详情');
      return;
  }
  ElMessage.info(`正在获取任务ID: ${orderIdToFetch} 的详情...`);
  await fetchOrderDetails(orderIdToFetch);
  if (selectedOrderDetail.value) {
      dialogVisible.value = true;
  }
}

onMounted(() => {
  const storedUserInfo = localStorage.getItem('userInfo');
  if (storedUserInfo) {
    userInfo.value = JSON.parse(storedUserInfo);
  }
  fetchOrders()
})
</script>

<style scoped>
.task-hall-container {
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
.loading-area {
  text-align: center;
  padding: 20px;
}
.error-area {
  text-align: center;
  padding: 20px;
}
.empty-state {
  text-align: center;
  padding: 20px;
}
.task-details-content p {
  margin-bottom: 12px;
  line-height: 1.6;
}
.task-details-content .detail-label {
  font-weight: bold;
  color: #606266;
  margin-right: 8px;
}
.detail-item {
  margin-bottom: 10px; /* Kept from previous for consistency if needed elsewhere */
  font-size: 14px; /* Consistent font size for details */
}

/* Ensure dialog content is scrollable if it gets too long */
.el-dialog__body {
  max-height: 60vh; /* Adjust as needed */
  overflow-y: auto;
}

</style> 