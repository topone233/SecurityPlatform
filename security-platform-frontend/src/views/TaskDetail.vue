<template>
  <div class="task-detail">
    <el-card shadow="never" class="info-card">
      <template #header>
        <div class="card-header">
          <span>任务信息</span>
          <div>
            <el-button @click="goBack">返回</el-button>
            <el-button type="primary" @click="triggerUpload">上传制品</el-button>
          </div>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="任务名称">{{ task.name }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <StatusTag type="task" :status="task.status || 'CREATED'" />
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(task.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatDate(task.updatedAt) }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ task.description || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card shadow="never" style="margin-top: 20px">
      <template #header>制品列表</template>

      <el-upload
        ref="uploadRef"
        :action="uploadUrl"
        :headers="uploadHeaders"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :before-upload="beforeUpload"
        :show-file-list="false"
        accept=".jar"
        v-show="false"
      />

      <el-table :data="artifacts" v-loading="loadingArtifacts" style="width: 100%">
        <el-table-column prop="fileName" label="文件名" min-width="200" />
        <el-table-column prop="fileSize" label="大小" width="120">
          <template #default="{ row }">
            {{ formatFileSize(row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <StatusTag type="artifact" :status="row.status || 'UPLOADED'" />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="上传时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="goToArtifact(row.id)">详情</el-button>
            <el-button
              type="success"
              link
              @click="analyzeArtifact(row)"
              v-if="row.status === 'UPLOADED'"
            >
              分析
            </el-button>
            <el-button
              type="warning"
              link
              @click="goToAnalysis(row.id)"
              v-if="row.status === 'ANALYZED'"
            >
              查看结果
            </el-button>
            <el-button type="danger" link @click="deleteArtifact(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import taskApi from '@/api/task'
import artifactApi from '@/api/artifact'
import StatusTag from '@/components/common/StatusTag.vue'
import { formatDate, formatFileSize } from '@/utils/format'

const route = useRoute()
const router = useRouter()

const taskId = route.params.id
const task = ref({})
const artifacts = ref([])
const loadingArtifacts = ref(false)
const uploadRef = ref(null)

const uploadUrl = `/api/artifacts/upload?taskId=${taskId}`
const uploadHeaders = {}

const goBack = () => {
  router.back()
}

const goToArtifact = (id) => {
  router.push(`/artifacts/${id}`)
}

const goToAnalysis = (artifactId) => {
  router.push(`/analysis/${artifactId}`)
}

const loadTask = async () => {
  try {
    task.value = await taskApi.getById(taskId)
  } catch (e) {
    console.error('Failed to load task:', e)
  }
}

const loadArtifacts = async () => {
  loadingArtifacts.value = true
  try {
    artifacts.value = await artifactApi.getListByTask(taskId)
  } catch (e) {
    console.error('Failed to load artifacts:', e)
  } finally {
    loadingArtifacts.value = false
  }
}

const triggerUpload = () => {
  uploadRef.value.$el.querySelector('input').click()
}

const beforeUpload = (file) => {
  if (!file.name.endsWith('.jar')) {
    ElMessage.error('只支持上传 .jar 文件')
    return false
  }
  return true
}

const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    ElMessage.success('上传成功')
    loadArtifacts()
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const handleUploadError = () => {
  ElMessage.error('上传失败')
}

const analyzeArtifact = async (row) => {
  await ElMessageBox.confirm(`确定分析制品 "${row.fileName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  })
  try {
    await artifactApi.triggerAnalysis(row.id)
    ElMessage.success('已开始分析')
    loadArtifacts()
  } catch (e) {
    console.error('Failed to trigger analysis:', e)
  }
}

const deleteArtifact = async (row) => {
  await ElMessageBox.confirm(`确定删除制品 "${row.fileName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  try {
    await artifactApi.delete(row.id)
    ElMessage.success('删除成功')
    loadArtifacts()
  } catch (e) {
    console.error('Failed to delete artifact:', e)
  }
}

onMounted(() => {
  loadTask()
  loadArtifacts()
})
</script>

<style lang="scss" scoped>
.task-detail {
  .info-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}
</style>
