<template>
  <div class="test-execution">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>测试执行管理</span>
          <div>
            <el-button @click="goBack">返回</el-button>
            <el-button type="primary" @click="showCreateDialog">创建执行</el-button>
          </div>
        </div>
      </template>

      <el-table :data="executions" v-loading="loading" style="width: 100%">
        <el-table-column prop="name" label="执行名称" min-width="150" />
        <el-table-column prop="type" label="测试类型" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <StatusTag type="test" :status="row.status || 'TODO'" />
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="耗时" width="100">
          <template #default="{ row }">
            {{ formatDuration(row.duration) }}
          </template>
        </el-table-column>
        <el-table-column label="结果" width="150">
          <template #default="{ row }">
            <span v-if="row.totalTests">
              <el-tag type="success" size="small">{{ row.passedTests }}</el-tag>
              /
              <el-tag type="danger" size="small">{{ row.failedTests }}</el-tag>
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">详情</el-button>
            <el-button
              type="success"
              link
              @click="startExecution(row)"
              v-if="row.status === 'TODO'"
            >
              启动
            </el-button>
            <el-button type="danger" link @click="deleteExecution(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card shadow="never" style="margin-top: 20px">
      <template #header>执行统计</template>
      <el-row :gutter="20">
        <el-col :span="6" v-for="stat in statistics" :key="stat.label">
          <div class="stat-box">
            <div class="stat-value" :style="{ color: stat.color }">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- Create Dialog -->
    <el-dialog v-model="dialogVisible" title="创建测试执行" width="500px">
      <el-form :model="executionForm" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="executionForm.name" placeholder="请输入执行名称" />
        </el-form-item>
        <el-form-item label="测试类型" prop="type">
          <el-select v-model="executionForm.type" placeholder="请选择测试类型">
            <el-option label="单元测试" value="UNIT" />
            <el-option label="集成测试" value="INTEGRATION" />
            <el-option label="安全测试" value="SECURITY" />
            <el-option label="性能测试" value="PERFORMANCE" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="executionForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import testExecutionApi from '@/api/testExecution'
import StatusTag from '@/components/common/StatusTag.vue'
import { formatDate, formatDuration } from '@/utils/format'

const route = useRoute()
const router = useRouter()

const artifactId = route.params.artifactId
const loading = ref(false)
const executions = ref([])

const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const executionForm = reactive({
  name: '',
  type: '',
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入执行名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择测试类型', trigger: 'change' }]
}

const statistics = computed(() => {
  const total = executions.value.length
  const completed = executions.value.filter(e => e.status === 'PASS' || e.status === 'FAIL').length
  const passed = executions.value.filter(e => e.status === 'PASS').length
  const failed = executions.value.filter(e => e.status === 'FAIL').length
  return [
    { label: '总执行数', value: total, color: '#409eff' },
    { label: '已完成', value: completed, color: '#67c23a' },
    { label: '通过', value: passed, color: '#67c23a' },
    { label: '失败', value: failed, color: '#f56c6c' }
  ]
})

const goBack = () => {
  router.back()
}

const loadExecutions = async () => {
  loading.value = true
  try {
    executions.value = await testExecutionApi.getList(artifactId)
  } catch (e) {
    console.error('Failed to load executions:', e)
  } finally {
    loading.value = false
  }
}

const showCreateDialog = () => {
  executionForm.name = ''
  executionForm.type = ''
  executionForm.description = ''
  dialogVisible.value = true
}

const submitForm = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    await testExecutionApi.create(artifactId, executionForm)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    loadExecutions()
  } catch (e) {
    console.error('Failed to create execution:', e)
  } finally {
    submitting.value = false
  }
}

const startExecution = async (row) => {
  await ElMessageBox.confirm(`确定启动测试 "${row.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  })
  try {
    await testExecutionApi.start(row.id)
    ElMessage.success('已启动')
    loadExecutions()
  } catch (e) {
    console.error('Failed to start:', e)
  }
}

const viewDetail = (row) => {
  ElMessage.info('执行详情功能开发中')
}

const deleteExecution = async (row) => {
  await ElMessageBox.confirm(`确定删除执行 "${row.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  try {
    await testExecutionApi.delete(row.id)
    ElMessage.success('删除成功')
    loadExecutions()
  } catch (e) {
    console.error('Failed to delete:', e)
  }
}

onMounted(() => {
  loadExecutions()
})
</script>

<style lang="scss" scoped>
.test-execution {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .stat-box {
    text-align: center;
    padding: 20px;

    .stat-value {
      font-size: 28px;
      font-weight: bold;
    }

    .stat-label {
      font-size: 14px;
      color: #909399;
      margin-top: 8px;
    }
  }
}
</style>