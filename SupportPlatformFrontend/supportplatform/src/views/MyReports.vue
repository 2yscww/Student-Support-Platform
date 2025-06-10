<template>
  <div class="my-reports-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的举报</span>
        </div>
      </template>

      <el-table :data="reports" v-loading="loading" style="width: 100%">
        <el-table-column prop="reportId" label="举报ID" width="100"></el-table-column>
        <el-table-column prop="reportedUsername" label="被举报用户" width="150"></el-table-column>
        <el-table-column prop="reportType" label="举报类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getReportTypeTag(row.reportType)">{{ translateReportType(row.reportType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="举报原因" show-overflow-tooltip></el-table-column>
        <el-table-column prop="status" label="状态" width="120">
           <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ translateStatus(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reportedAt" label="举报时间" width="180">
           <template #default="{ row }">
            {{ formatDateTime(row.reportedAt) }}
          </template>
        </el-table-column>
      </el-table>

    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElCard, ElTable, ElTableColumn, ElTag } from 'element-plus'
import axios from 'axios'

const reports = ref([])
const loading = ref(true)

const fetchMyReports = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/reports/my')
    if (response.data && response.data.code === 200) {
      reports.value = response.data.data
    } else {
      ElMessage.error(response.data.message || '获取举报列表失败')
    }
  } catch (error) {
    ElMessage.error('获取举报列表失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString()
}

const translateReportType = (type) => {
  const types = {
    USER: '用户',
    TASK: '任务',
    REVIEW: '评价'
  }
  return types[type] || type
}

const getReportTypeTag = (type) => {
  const tags = {
    USER: 'primary',
    TASK: 'success',
    REVIEW: 'warning'
  }
  return tags[type] || 'info'
}

const translateStatus = (status) => {
  const statuses = {
    PENDING: '待处理',
    INVESTIGATING: '调查中',
    RESOLVED: '已处理',
    REJECTED: '已驳回',
    INVALID: '无效举报'
  }
  return statuses[status] || status
}

const getStatusTag = (status) => {
    const tags = {
    PENDING: 'warning',
    INVESTIGATING: 'primary',
    RESOLVED: 'success',
    REJECTED: 'info',
    INVALID: 'danger'
  }
  return tags[status] || 'info'
}

onMounted(() => {
  fetchMyReports()
})
</script>

<style scoped>
.my-reports-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 