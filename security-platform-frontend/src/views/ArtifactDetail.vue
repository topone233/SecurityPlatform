<template>
  <div class="artifact-detail">
    <el-card shadow="never" class="info-card">
      <template #header>
        <div class="card-header">
          <span>制品信息</span>
          <div>
            <el-button @click="goBack">返回</el-button>
            <el-button type="primary" @click="goToAnalysis" v-if="artifact.status === 'ANALYZED'">
              查看分析结果
            </el-button>
          </div>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="文件名">{{ artifact.fileName }}</el-descriptions-item>
        <el-descriptions-item label="文件大小">{{ formatFileSize(artifact.fileSize) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <StatusTag type="artifact" :status="artifact.status || 'UPLOADED'" />
        </el-descriptions-item>
        <el-descriptions-item label="上传时间">{{ formatDate(artifact.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="存储路径" :span="2">{{ artifact.filePath || '-' }}</el-descriptions-item>
        <el-descriptions-item label="MD5">{{ artifact.md5 || '-' }}</el-descriptions-item>
        <el-descriptions-item label="SHA256">{{ artifact.sha256 || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card shadow="never" style="margin-top: 20px" v-if="artifact.status === 'ANALYZING'">
      <template #header>分析进度</template>
      <ProgressPanel
        :percentage="progress.percentage"
        :status="progress.status"
        :current-step="progress.currentStep"
        :stats="{
          '已分析类': progress.analyzedClasses,
          '总类数': progress.totalClasses
        }"
      />
    </el-card>

    <el-card shadow="never" style="margin-top: 20px" v-if="artifact.status === 'ANALYZED'">
      <template #header>快速操作</template>
      <el-space>
        <el-button type="primary" @click="goToAnalysis">查看分析结果</el-button>
        <el-button @click="goToChecklist">测试清单</el-button>
        <el-button @click="goToRiskExclusion">风险排除</el-button>
        <el-button @click="goToExecution">测试执行</el-button>
        <el-button type="success" @click="generateReport">生成报告</el-button>
      </el-space>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import artifactApi from '@/api/artifact'
import analysisApi from '@/api/analysis'
import reportApi from '@/api/testReport'
import StatusTag from '@/components/common/StatusTag.vue'
import ProgressPanel from '@/components/common/ProgressPanel.vue'
import { formatDate, formatFileSize } from '@/utils/format'

const route = useRoute()
const router = useRouter()

const artifactId = route.params.id
const artifact = ref({})
const progress = ref({
  percentage: 0,
  status: 'IN_PROGRESS',
  currentStep: '',
  analyzedClasses: 0,
  totalClasses: 0
})

let pollTimer = null

const goBack = () => {
  router.back()
}

const goToAnalysis = () => {
  router.push(`/analysis/${artifactId}`)
}

const goToChecklist = () => {
  router.push(`/strategy/checklist/${artifactId}`)
}

const goToRiskExclusion = () => {
  router.push(`/risk-exclusion/${artifactId}`)
}

const goToExecution = () => {
  router.push(`/execution/${artifactId}`)
}

const loadArtifact = async () => {
  try {
    artifact.value = await artifactApi.getById(artifactId)
  } catch (e) {
    console.error('Failed to load artifact:', e)
  }
}

const loadProgress = async () => {
  try {
    const data = await analysisApi.getProgress(artifactId)
    progress.value = data
  } catch (e) {
    console.error('Failed to load progress:', e)
  }
}

const startPolling = () => {
  loadProgress()
  pollTimer = setInterval(loadProgress, 3000)
}

const stopPolling = () => {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

const generateReport = async () => {
  try {
    await reportApi.generate(artifactId)
    ElMessage.success('报告生成中，请稍后查看')
  } catch (e) {
    console.error('Failed to generate report:', e)
  }
}

onMounted(async () => {
  await loadArtifact()
  if (artifact.value.status === 'ANALYZING') {
    startPolling()
  }
})

onUnmounted(() => {
  stopPolling()
})
</script>

<style lang="scss" scoped>
.artifact-detail {
  .info-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}
</style>