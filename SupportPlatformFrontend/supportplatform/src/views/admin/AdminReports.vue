<template>
  <div class="reports-management-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>举报管理</span>
        </div>
      </template>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="所有举报" name="all">
          <el-table :data="allReports" v-loading="loading">
            <el-table-column prop="reportId" label="ID" width="60"></el-table-column>
            <el-table-column prop="reporterUsername" label="举报人" width="120"></el-table-column>
            <el-table-column prop="reportedUsername" label="被举报人" width="120"></el-table-column>
            <el-table-column prop="reason" label="举报原因" show-overflow-tooltip></el-table-column>
            <el-table-column prop="reportType" label="类型" width="100"></el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">
                <el-tag :type="getStatusTagType(scope.row.status)">{{ translateStatus(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="reportedAt" label="举报时间" width="180"></el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <el-button size="small" @click="viewReportDetails(scope.row.reportId)">查看详情</el-button>
                <div v-if="scope.row.status === 'PENDING' || scope.row.status === 'INVESTIGATING'" style="display: inline-block; margin-left: 10px;">
                  <el-dropdown @command="command => handleUpdateReportStatus(scope.row.reportId, command)">
                    <el-button type="primary" size="small">
                      处理<el-icon class="el-icon--right"><ArrowDown /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item v-if="scope.row.status === 'PENDING'" command="INVESTIGATING">开始调查</el-dropdown-item>
                        <el-dropdown-item command="RESOLVED">处理完成</el-dropdown-item>
                        <el-dropdown-item command="REJECTED">驳回举报</el-dropdown-item>
                        <el-dropdown-item command="INVALID">无效举报</el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
                <span v-else>--</span>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="待处理" name="pending">
          <el-table :data="pendingReports" v-loading="loading">
            <el-table-column prop="reportId" label="ID" width="60"></el-table-column>
            <el-table-column prop="reporterUsername" label="举报人" width="120"></el-table-column>
            <el-table-column prop="reportedUsername" label="被举报人" width="120"></el-table-column>
            <el-table-column prop="reason" label="举报原因" show-overflow-tooltip></el-table-column>
            <el-table-column prop="reportType" label="类型" width="100"></el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">
                <el-tag :type="getStatusTagType(scope.row.status)">{{ translateStatus(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="reportedAt" label="举报时间" width="180"></el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <el-button size="small" @click="viewReportDetails(scope.row.reportId)">查看详情</el-button>
                <el-dropdown @command="command => handleUpdateReportStatus(scope.row.reportId, command)" style="margin-left: 10px;">
                  <el-button type="primary" size="small">
                    处理<el-icon class="el-icon--right"><ArrowDown /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item v-if="scope.row.status === 'PENDING'" command="INVESTIGATING">开始调查</el-dropdown-item>
                      <el-dropdown-item command="RESOLVED">处理完成</el-dropdown-item>
                      <el-dropdown-item command="REJECTED">驳回举报</el-dropdown-item>
                      <el-dropdown-item command="INVALID">无效举报</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="已处理" name="resolved">
          <el-table :data="resolvedReports" v-loading="loading">
            <el-table-column prop="reportId" label="ID" width="60"></el-table-column>
            <el-table-column prop="reporterUsername" label="举报人" width="120"></el-table-column>
            <el-table-column prop="reportedUsername" label="被举报人" width="120"></el-table-column>
            <el-table-column prop="reason" label="举报原因" show-overflow-tooltip></el-table-column>
            <el-table-column prop="reportType" label="类型" width="100"></el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">
                <el-tag :type="getStatusTagType(scope.row.status)">{{ translateStatus(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="reportedAt" label="举报时间" width="180"></el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="scope">
                <el-button size="small" @click="viewReportDetails(scope.row.reportId)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- Report Details Dialog -->
    <el-dialog v-model="detailsDialogVisible" title="举报详情" width="60%" destroy-on-close>
      <div v-if="detailsLoading" class="loading-area">加载中...</div>
      <div v-else-if="currentReportDetails">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="举报ID">{{ currentReportDetails.reportInfo.reportId }}</el-descriptions-item>
          <el-descriptions-item label="举报类型">{{ currentReportDetails.reportInfo.reportType }}</el-descriptions-item>
          <el-descriptions-item label="举报人">{{ currentReportDetails.reportInfo.reporterUsername }}</el-descriptions-item>
          <el-descriptions-item label="被举报人">{{ currentReportDetails.reportInfo.reportedUsername }}</el-descriptions-item>
          <el-descriptions-item label="举报时间">{{ currentReportDetails.reportInfo.reportedAt }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusTagType(currentReportDetails.reportInfo.status)">{{ translateStatus(currentReportDetails.reportInfo.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="举报原因" :span="2">{{ currentReportDetails.reportInfo.reason }}</el-descriptions-item>
        </el-descriptions>

        <el-divider v-if="currentReportDetails.reportedTaskDetails || currentReportDetails.reportedReviewDetails" />

        <div v-if="currentReportDetails.reportedTaskDetails">
          <h4>相关任务详情</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="任务ID">{{ currentReportDetails.reportedTaskDetails.taskId }}</el-descriptions-item>
            <el-descriptions-item label="任务标题">{{ currentReportDetails.reportedTaskDetails.title }}</el-descriptions-item>
            <el-descriptions-item label="发布者">{{ currentReportDetails.reportedTaskDetails.posterUsername }}</el-descriptions-item>
             <el-descriptions-item label="任务赏金">¥{{ currentReportDetails.reportedTaskDetails.reward ? currentReportDetails.reportedTaskDetails.reward.toFixed(2) : 'N/A' }}</el-descriptions-item>
            <el-descriptions-item label="任务描述" :span="2">{{ currentReportDetails.reportedTaskDetails.description }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div v-if="currentReportDetails.reportedReviewDetails" style="margin-top: 20px;">
          <h4>相关评价详情</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="评价ID">{{ currentReportDetails.reportedReviewDetails.reviewId }}</el-descriptions-item>
            <el-descriptions-item label="评价人">{{ currentReportDetails.reportedReviewDetails.reviewerUsername }}</el-descriptions-item>
            <el-descriptions-item label="评分">
              <el-rate :model-value="currentReportDetails.reportedReviewDetails.rating" disabled />
            </el-descriptions-item>
            <el-descriptions-item label="评价内容" :span="2">{{ currentReportDetails.reportedReviewDetails.content }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
      <div v-else>
        <el-alert title="无法加载举报详情" type="error" show-icon :closable="false" />
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button
              v-if="currentReportDetails && currentReportDetails.reportedTaskDetails"
              type="danger"
              @click="handleDeleteTask(currentReportDetails.reportedTaskDetails.taskId)"
          >删除任务</el-button>
          <el-button
              v-if="currentReportDetails && currentReportDetails.reportedReviewDetails"
              type="danger"
              @click="handleDeleteReview(currentReportDetails.reportedReviewDetails.reviewId)"
          >删除评价</el-button>
          <el-button @click="detailsDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, ElDialog, ElDescriptions, ElDescriptionsItem, ElDivider, ElAlert, ElRate } from 'element-plus'
import axios from 'axios'
import { ArrowDown } from '@element-plus/icons-vue'

const activeTab = ref('all')
const loading = ref(false)
const allReports = ref([])
const detailsDialogVisible = ref(false)
const detailsLoading = ref(false)
const currentReportDetails = ref(null)

// Use computed properties for pending and resolved reports to correctly categorize them.
const pendingReports = computed(() =>
  allReports.value.filter(report => report.status === 'PENDING' || report.status === 'INVESTIGATING')
)
const resolvedReports = computed(() =>
  allReports.value.filter(report => ['RESOLVED', 'REJECTED', 'INVALID'].includes(report.status))
)

const translateStatus = (status) => {
  const statusMap = {
    'PENDING': '待处理',
    'INVESTIGATING': '调查中',
    'RESOLVED': '已处理',
    'REJECTED': '已驳回',
    'INVALID': '无效举报'
  }
  return statusMap[status] || status
}

const getStatusTagType = (status) => {
  switch (status) {
    case 'PENDING': return 'warning'
    case 'INVESTIGATING': return 'primary'
    case 'RESOLVED': return 'success'
    case 'REJECTED': return 'info'
    case 'INVALID': return 'danger'
    default: return ''
  }
}

// Fetches all reports and lets computed properties handle filtering for tabs.
const fetchReports = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/admin/reports/list/all')
    if (response.data.code === 200) {
      allReports.value = response.data.data || []
    } else {
      ElMessage.error(response.data.msg || '获取举报列表失败')
    }
  } catch (error) {
    console.error('Failed to fetch reports:', error)
    ElMessage.error('网络错误，请稍后再试')
  } finally {
    loading.value = false
  }
}

const viewReportDetails = async (reportId) => {
  detailsLoading.value = true;
  detailsDialogVisible.value = true;
  currentReportDetails.value = null;
  try {
    const response = await axios.post('/api/admin/reports/detail', { reportId: reportId });
    if (response.data.code === 200) {
      currentReportDetails.value = response.data.data;
    } else {
      ElMessage.error(response.data.msg || '获取举报详情失败');
    }
  } catch (error) {
    console.error('Failed to fetch report details:', error);
    ElMessage.error('网络错误，无法获取举报详情');
  } finally {
    detailsLoading.value = false;
  }
};

const handleDeleteTask = async (taskId) => {
  try {
    await ElMessageBox.confirm(
        `确定要删除这个任务 (ID: ${taskId}) 吗? 这个操作不可逆.`,
        '删除确认',
        {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning',
        }
    )
    const response = await axios.delete('/api/admin/orders/delete', {
      data: { orderId: taskId }
    })

    if (response.data.code === 200) {
      ElMessage.success('任务删除成功！')
      detailsDialogVisible.value = false
      await fetchReports()
    } else {
      ElMessage.error(response.data.msg || '删除任务失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete task:', error)
      ElMessage.error('删除任务失败，请查看控制台')
    } else {
      ElMessage.info('操作已取消')
    }
  }
}

const handleDeleteReview = async (reviewId) => {
  try {
    await ElMessageBox.confirm(
        `确定要删除这条评价 (ID: ${reviewId}) 吗? 这个操作不可逆.`,
        '删除确认',
        {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning',
        }
    )

    const response = await axios.delete('/api/admin/reviews/delete', {
      data: { reviewId: reviewId }
    })

    if (response.data.code === 200) {
      ElMessage.success('评价删除成功！')
      detailsDialogVisible.value = false
      await fetchReports()
    } else {
      ElMessage.error(response.data.msg || '删除评价失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete review:', error)
      ElMessage.error('删除评价失败，请查看控制台')
    } else {
      ElMessage.info('操作已取消')
    }
  }
}

const handleUpdateReportStatus = async (reportId, newStatus) => {
  try {
    const translatedStatus = translateStatus(newStatus)
    await ElMessageBox.confirm(
      `确定要将此举报的状态更新为 "${translatedStatus}" 吗?`,
      '状态更新确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    const response = await axios.put('/api/admin/reports/update-status', {
      reportId: reportId,
      newStatus: newStatus,
    })

    if (response.data.code === 200) {
      ElMessage.success('举报状态更新成功！')
      await fetchReports()
    } else {
      ElMessage.error(response.data.msg || '更新失败')
    }
  } catch (error) {
    if (error === 'cancel') {
      ElMessage.info('操作已取消')
    } else {
      console.error('Failed to update report status:', error)
      ElMessage.error('更新举报状态失败，请查看控制台')
    }
  }
}

onMounted(() => {
  fetchReports()
})
</script>

<style scoped>
.reports-management-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.el-icon--right {
  margin-left: 4px;
}
.loading-area {
  text-align: center;
  padding: 20px;
}
</style> 