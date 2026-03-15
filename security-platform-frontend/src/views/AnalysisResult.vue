<template>
  <div class="analysis-result">
    <el-card shadow="never" class="header-card">
      <template #header>
        <div class="card-header">
          <span>分析结果</span>
          <div>
            <el-button @click="goBack">返回</el-button>
            <el-button @click="goToChecklist">测试清单</el-button>
            <el-button type="primary" @click="goToExecution">开始测试</el-button>
          </div>
        </div>
      </template>

      <ProgressPanel
        v-if="progress.status === 'IN_PROGRESS'"
        :percentage="progress.percentage"
        :status="progress.status"
        :current-step="progress.currentStep"
        title="分析进度"
      />
    </el-card>

    <el-card shadow="never" style="margin-top: 20px" v-if="progress.status === 'COMPLETED'">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="依赖分析" name="dependencies">
          <el-table :data="dependencies" v-loading="loadingDeps" max-height="400">
            <el-table-column prop="groupId" label="GroupId" min-width="150" />
            <el-table-column prop="artifactId" label="ArtifactId" min-width="150" />
            <el-table-column prop="version" label="Version" width="120" />
            <el-table-column prop="scope" label="Scope" width="100" />
            <el-table-column label="漏洞风险" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.hasVulnerability" type="danger" size="small">有风险</el-tag>
                <el-tag v-else type="success" size="small">安全</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="入口点" name="entryPoints">
          <el-table :data="entryPoints" v-loading="loadingEntries" max-height="400">
            <el-table-column prop="className" label="类名" min-width="200" />
            <el-table-column prop="methodName" label="方法名" min-width="150" />
            <el-table-column prop="type" label="类型" width="120">
              <template #default="{ row }">
                <el-tag size="small">{{ row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="signature" label="签名" show-overflow-tooltip />
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="安全关注点" name="securityConcerns">
          <el-table :data="securityConcerns" v-loading="loadingConcerns" max-height="400">
            <el-table-column prop="className" label="类名" min-width="200" />
            <el-table-column prop="methodName" label="方法名" min-width="150" />
            <el-table-column prop="concernType" label="关注类型" width="150">
              <template #default="{ row }">
                <el-tag :type="getConcernTypeTag(row.concernType)" size="small">
                  {{ row.concernType }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" show-overflow-tooltip />
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="API端点" name="apiEndpoints">
          <el-table :data="apiEndpoints" v-loading="loadingApis" max-height="400">
            <el-table-column prop="path" label="路径" min-width="200" />
            <el-table-column prop="httpMethod" label="方法" width="100">
              <template #default="{ row }">
                <el-tag :type="getMethodTag(row.httpMethod)" size="small">
                  {{ row.httpMethod }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="controllerClass" label="Controller" min-width="150" />
            <el-table-column prop="handlerMethod" label="Handler" min-width="150" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-empty v-if="progress.status !== 'COMPLETED' && progress.status !== 'IN_PROGRESS'" description="暂无分析结果" />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import analysisApi from '@/api/analysis'
import ProgressPanel from '@/components/common/ProgressPanel.vue'

const route = useRoute()
const router = useRouter()

const artifactId = route.params.artifactId
const activeTab = ref('dependencies')

const progress = ref({
  percentage: 0,
  status: 'PENDING',
  currentStep: ''
})

const dependencies = ref([])
const entryPoints = ref([])
const securityConcerns = ref([])
const apiEndpoints = ref([])

const loadingDeps = ref(false)
const loadingEntries = ref(false)
const loadingConcerns = ref(false)
const loadingApis = ref(false)

let pollTimer = null

const goBack = () => {
  router.back()
}

const goToChecklist = () => {
  router.push(`/strategy/checklist/${artifactId}`)
}

const goToExecution = () => {
  router.push(`/execution/${artifactId}`)
}

const loadProgress = async () => {
  try {
    const data = await analysisApi.getProgress(artifactId)
    progress.value = data
    if (data.status === 'COMPLETED') {
      stopPolling()
      loadAllResults()
    }
  } catch (e) {
    console.error('Failed to load progress:', e)
  }
}

const loadAllResults = async () => {
  loadDependencies()
  loadEntryPoints()
  loadSecurityConcerns()
  loadApiEndpoints()
}

const loadDependencies = async () => {
  loadingDeps.value = true
  try {
    dependencies.value = await analysisApi.getDependencies(artifactId)
  } catch (e) {
    console.error('Failed to load dependencies:', e)
  } finally {
    loadingDeps.value = false
  }
}

const loadEntryPoints = async () => {
  loadingEntries.value = true
  try {
    entryPoints.value = await analysisApi.getEntryPoints(artifactId)
  } catch (e) {
    console.error('Failed to load entry points:', e)
  } finally {
    loadingEntries.value = false
  }
}

const loadSecurityConcerns = async () => {
  loadingConcerns.value = true
  try {
    securityConcerns.value = await analysisApi.getSecurityConcerns(artifactId)
  } catch (e) {
    console.error('Failed to load security concerns:', e)
  } finally {
    loadingConcerns.value = false
  }
}

const loadApiEndpoints = async () => {
  loadingApis.value = true
  try {
    apiEndpoints.value = await analysisApi.getApiEndpoints(artifactId)
  } catch (e) {
    console.error('Failed to load API endpoints:', e)
  } finally {
    loadingApis.value = false
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

const getConcernTypeTag = (type) => {
  const types = {
    SQL_INJECTION: 'danger',
    XSS: 'danger',
    COMMAND_INJECTION: 'danger',
    PATH_TRAVERSAL: 'warning',
    SENSITIVE_DATA: 'warning'
  }
  return types[type] || 'info'
}

const getMethodTag = (method) => {
  const types = {
    GET: 'success',
    POST: 'primary',
    PUT: 'warning',
    DELETE: 'danger',
    PATCH: 'info'
  }
  return types[method] || 'info'
}

onMounted(() => {
  startPolling()
})

onUnmounted(() => {
  stopPolling()
})
</script>

<style lang="scss" scoped>
.analysis-result {
  .header-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}
</style>