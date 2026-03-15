<template>
  <div class="test-checklist">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>测试清单</span>
          <div>
            <el-button @click="goBack">返回</el-button>
            <el-button type="success" @click="generateStrategy" :loading="generating">
              生成策略
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="checklist" v-loading="loading" style="width: 100%">
        <el-table-column prop="category" label="类别" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="测试项" min-width="200" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="priority" label="优先级" width="100">
          <template #default="{ row }">
            <el-tag :type="getPriorityTag(row.priority)" size="small">
              {{ row.priority }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <StatusTag type="test" :status="row.status || 'TODO'" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="success"
              link
              @click="updateStatus(row.id, 'PASS')"
              v-if="row.status !== 'PASS'"
            >
              通过
            </el-button>
            <el-button
              type="danger"
              link
              @click="updateStatus(row.id, 'FAIL')"
              v-if="row.status !== 'FAIL'"
            >
              失败
            </el-button>
            <el-button
              type="warning"
              link
              @click="updateStatus(row.id, 'TODO')"
              v-if="row.status !== 'TODO'"
            >
              重置
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card shadow="never" style="margin-top: 20px">
      <template #header>测试统计</template>
      <el-row :gutter="20">
        <el-col :span="6" v-for="stat in statistics" :key="stat.label">
          <div class="stat-box" :style="{ borderColor: stat.color }">
            <div class="stat-value" :style="{ color: stat.color }">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import strategyApi from '@/api/strategy'
import StatusTag from '@/components/common/StatusTag.vue'

const route = useRoute()
const router = useRouter()

const artifactId = route.params.artifactId
const loading = ref(false)
const generating = ref(false)
const checklist = ref([])

const statistics = computed(() => {
  const total = checklist.value.length
  const passed = checklist.value.filter(item => item.status === 'PASS').length
  const failed = checklist.value.filter(item => item.status === 'FAIL').length
  const todo = total - passed - failed
  return [
    { label: '总计', value: total, color: '#409eff' },
    { label: '通过', value: passed, color: '#67c23a' },
    { label: '失败', value: failed, color: '#f56c6c' },
    { label: '待测试', value: todo, color: '#909399' }
  ]
})

const goBack = () => {
  router.back()
}

const loadChecklist = async () => {
  loading.value = true
  try {
    checklist.value = await strategyApi.getChecklist(artifactId)
  } catch (e) {
    console.error('Failed to load checklist:', e)
  } finally {
    loading.value = false
  }
}

const updateStatus = async (itemId, status) => {
  try {
    await strategyApi.updateItemStatus(artifactId, itemId, { status })
    ElMessage.success('状态已更新')
    loadChecklist()
  } catch (e) {
    console.error('Failed to update status:', e)
  }
}

const generateStrategy = async () => {
  generating.value = true
  try {
    await strategyApi.generateStrategy(artifactId)
    ElMessage.success('策略已生成')
    loadChecklist()
  } catch (e) {
    console.error('Failed to generate strategy:', e)
  } finally {
    generating.value = false
  }
}

const getPriorityTag = (priority) => {
  const types = {
    HIGH: 'danger',
    MEDIUM: 'warning',
    LOW: 'info'
  }
  return types[priority] || 'info'
}

onMounted(() => {
  loadChecklist()
})
</script>

<style lang="scss" scoped>
.test-checklist {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .stat-box {
    text-align: center;
    padding: 20px;
    border: 2px solid #dcdfe6;
    border-radius: 8px;

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