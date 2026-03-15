<template>
  <div class="risk-exclusion">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>风险排除管理</span>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>

      <el-table :data="riskExclusions" v-loading="loading" style="width: 100%">
        <el-table-column prop="className" label="类名" min-width="180" />
        <el-table-column prop="methodName" label="方法名" min-width="150" />
        <el-table-column prop="concernType" label="风险类型" width="150">
          <template #default="{ row }">
            <el-tag :type="getRiskTypeTag(row.concernType)" size="small">
              {{ row.concernType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="severity" label="严重程度" width="100">
          <template #default="{ row }">
            <el-tag :type="getSeverityTag(row.severity)" size="small">
              {{ row.severity }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="排除原因" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <StatusTag type="risk" :status="row.status || 'PENDING'" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 'PENDING'">
              <el-button type="success" link @click="approveRisk(row)">批准</el-button>
              <el-button type="danger" link @click="showRejectDialog(row)">拒绝</el-button>
            </template>
            <span v-else class="text-muted">已处理</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Reject Dialog -->
    <el-dialog v-model="rejectDialogVisible" title="拒绝风险排除" width="400px">
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="拒绝原因">
          <el-input
            v-model="rejectForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入拒绝原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="rejectRisk" :loading="rejecting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import riskExclusionApi from '@/api/riskExclusion'
import StatusTag from '@/components/common/StatusTag.vue'

const route = useRoute()
const router = useRouter()

const artifactId = route.params.artifactId
const loading = ref(false)
const riskExclusions = ref([])

const rejectDialogVisible = ref(false)
const rejecting = ref(false)
const currentRisk = ref(null)
const rejectForm = reactive({
  reason: ''
})

const goBack = () => {
  router.back()
}

const loadRiskExclusions = async () => {
  loading.value = true
  try {
    riskExclusions.value = await riskExclusionApi.getList(artifactId)
  } catch (e) {
    console.error('Failed to load risk exclusions:', e)
  } finally {
    loading.value = false
  }
}

const approveRisk = async (row) => {
  try {
    await riskExclusionApi.approve(row.id)
    ElMessage.success('已批准')
    loadRiskExclusions()
  } catch (e) {
    console.error('Failed to approve:', e)
  }
}

const showRejectDialog = (row) => {
  currentRisk.value = row
  rejectForm.reason = ''
  rejectDialogVisible.value = true
}

const rejectRisk = async () => {
  if (!rejectForm.reason) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  rejecting.value = true
  try {
    await riskExclusionApi.reject(currentRisk.value.id, rejectForm.reason)
    ElMessage.success('已拒绝')
    rejectDialogVisible.value = false
    loadRiskExclusions()
  } catch (e) {
    console.error('Failed to reject:', e)
  } finally {
    rejecting.value = false
  }
}

const getRiskTypeTag = (type) => {
  const types = {
    SQL_INJECTION: 'danger',
    XSS: 'danger',
    COMMAND_INJECTION: 'danger',
    PATH_TRAVERSAL: 'warning'
  }
  return types[type] || 'info'
}

const getSeverityTag = (severity) => {
  const types = {
    CRITICAL: 'danger',
    HIGH: 'danger',
    MEDIUM: 'warning',
    LOW: 'info'
  }
  return types[severity] || 'info'
}

onMounted(() => {
  loadRiskExclusions()
})
</script>

<style lang="scss" scoped>
.risk-exclusion {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .text-muted {
    color: #909399;
    font-size: 12px;
  }
}
</style>