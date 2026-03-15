<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6" v-for="stat in statistics" :key="stat.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: stat.color }">
              <el-icon><component :is="stat.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card shadow="never">
          <template #header>最近项目</template>
          <el-table :data="recentProjects" style="width: 100%">
            <el-table-column prop="name" label="项目名称" min-width="150" />
            <el-table-column prop="description" label="描述" show-overflow-tooltip />
            <el-table-column prop="createdAt" label="创建时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button type="primary" link @click="goToProject(row.id)">
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>待处理任务</template>
          <div class="pending-tasks">
            <div v-if="pendingTasks.length === 0" class="empty">
              暂无待处理任务
            </div>
            <div v-for="task in pendingTasks" :key="task.id" class="task-item">
              <div class="task-name">{{ task.name }}</div>
              <div class="task-project">{{ task.projectName }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import projectApi from '@/api/project'
import { formatDate } from '@/utils/format'

const router = useRouter()

const statistics = ref([
  { label: '项目总数', value: 0, icon: 'Folder', color: '#409eff' },
  { label: '进行中任务', value: 0, icon: 'Timer', color: '#e6a23c' },
  { label: '待审核风险', value: 0, icon: 'Warning', color: '#f56c6c' },
  { label: '本月报告', value: 0, icon: 'Document', color: '#67c23a' }
])

const recentProjects = ref([])
const pendingTasks = ref([])

const goToProject = (id) => {
  router.push(`/projects/${id}`)
}

onMounted(async () => {
  try {
    const projects = await projectApi.getList({ page: 0, size: 5 })
    recentProjects.value = projects.content || []

    statistics.value[0].value = projects.totalElements || 0
  } catch (e) {
    console.error('Failed to load dashboard data:', e)
  }
})
</script>

<style lang="scss" scoped>
.dashboard {
  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
      gap: 16px;
    }

    .stat-icon {
      width: 48px;
      height: 48px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-size: 24px;
    }

    .stat-info {
      .stat-value {
        font-size: 24px;
        font-weight: bold;
        color: #303133;
      }
      .stat-label {
        font-size: 14px;
        color: #909399;
      }
    }
  }

  .pending-tasks {
    .empty {
      text-align: center;
      color: #909399;
      padding: 20px;
    }

    .task-item {
      padding: 12px 0;
      border-bottom: 1px solid #ebeef5;

      &:last-child {
        border-bottom: none;
      }

      .task-name {
        font-weight: 500;
        color: #303133;
      }
      .task-project {
        font-size: 12px;
        color: #909399;
        margin-top: 4px;
      }
    }
  }
}
</style>
