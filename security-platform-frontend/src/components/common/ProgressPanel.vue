<template>
  <el-card class="progress-panel" shadow="never">
    <template #header>
      <div class="panel-header">
        <span>{{ title }}</span>
        <span v-if="status" class="status">
          <StatusTag :type="statusType" :status="status" />
        </span>
      </div>
    </template>

    <el-progress
      :percentage="percentage"
      :status="progressStatus"
      :stroke-width="12"
    />

    <div v-if="currentStep" class="current-step">
      <el-icon class="loading-icon"><Loading /></el-icon>
      <span>{{ currentStep }}</span>
    </div>

    <div v-if="stats" class="stats">
      <div v-for="(value, key) in stats" :key="key" class="stat-item">
        <span class="label">{{ key }}:</span>
        <span class="value">{{ value }}</span>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue'
import StatusTag from './StatusTag.vue'

const props = defineProps({
  title: {
    type: String,
    default: '处理进度'
  },
  percentage: {
    type: Number,
    default: 0
  },
  status: {
    type: String,
    default: ''
  },
  statusType: {
    type: String,
    default: 'analysis'
  },
  currentStep: {
    type: String,
    default: ''
  },
  stats: {
    type: Object,
    default: null
  }
})

const progressStatus = computed(() => {
  if (props.percentage >= 100) return 'success'
  if (props.status === 'FAILED') return 'exception'
  return null
})
</script>

<style lang="scss" scoped>
.progress-panel {
  margin-bottom: 20px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.current-step {
  margin-top: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;

  .loading-icon {
    animation: rotate 1s linear infinite;
  }
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.stats {
  margin-top: 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.stat-item {
  .label {
    color: #909399;
    margin-right: 4px;
  }
  .value {
    color: #303133;
    font-weight: 500;
  }
}
</style>
