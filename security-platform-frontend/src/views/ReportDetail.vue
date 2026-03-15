<template>
  <div class="report-detail">
    <el-card shadow="never" class="header-card">
      <template #header>
        <div class="card-header">
          <span>{{ report.name }}</span>
          <div>
            <el-button @click="goBack">返回</el-button>
            <el-button type="success" @click="exportReport('pdf')">导出PDF</el-button>
            <el-button type="warning" @click="exportReport('html')">导出HTML</el-button>
          </div>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="制品">{{ report.artifactName }}</el-descriptions-item>
        <el-descriptions-item label="生成时间">{{ formatDate(report.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="测试总数">{{ report.totalTests || 0 }}</el-descriptions-item>
        <el-descriptions-item label="通过/失败">
          <el-tag type="success" size="small">{{ report.passedTests || 0 }}</el-tag>
          /
          <el-tag type="danger" size="small">{{ report.failedTests || 0 }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card shadow="never" style="margin-top: 20px">
      <template #header>测试摘要</template>

      <el-row :gutter="20">
        <el-col :span="6">
          <div class="summary-item">
            <div class="value" style="color: #67c23a">{{ report.passedTests || 0 }}</div>
            <div class="label">通过</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="summary-item">
            <div class="value" style="color: #f56c6c">{{ report.failedTests || 0 }}</div>
            <div class="label">失败</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="summary-item">
            <div class="value" style="color: #e6a23c">{{ report.skippedTests || 0 }}</div>
            <div class="label">跳过</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="summary-item">
            <div class="value">{{ report.passRate || 0 }}%</div>
            <div class="label">通过率</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card shadow="never" style="margin-top: 20px" v-if="report.details && report.details.length">
      <template #header>测试详情</template>

      <el-table :data="report.details" max-height="400">
        <el-table-column prop="name" label="测试项" min-width="200" />
        <el-table-column prop="category" label="类别" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <StatusTag type="test" :status="row.status" />
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="耗时" width="100">
          <template #default="{ row }">
            {{ row.duration ? `${row.duration}ms` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="message" label="消息" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import reportApi from '@/api/testReport'
import StatusTag from '@/components/common/StatusTag.vue'
import { formatDate } from '@/utils/format'

const route = useRoute()
const router = useRouter()

const reportId = route.params.id
const report = ref({})

const goBack = () => {
  router.back()
}

const loadReport = async () => {
  try {
    report.value = await reportApi.getById(reportId)
  } catch (e) {
    console.error('Failed to load report:', e)
  }
}

const exportReport = async (format) => {
  try {
    const blob = await reportApi.export(reportId, format)
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${report.value.name}.${format.toLowerCase()}`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (e) {
    console.error('Failed to export:', e)
  }
}

onMounted(() => {
  loadReport()
})
</script>

<style lang="scss" scoped>
.report-detail {
  .header-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .summary-item {
    text-align: center;
    padding: 20px;
    background: #f5f7fa;
    border-radius: 8px;

    .value {
      font-size: 32px;
      font-weight: bold;
    }

    .label {
      font-size: 14px;
      color: #909399;
      margin-top: 8px;
    }
  }
}
</style>