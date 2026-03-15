<template>
  <el-tag :type="tagType" :effect="effect" size="small">
    {{ displayText }}
  </el-tag>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  type: {
    type: String,
    required: true
  },
  status: {
    type: String,
    required: true
  }
})

const statusConfig = {
  // Task status
  task: {
    CREATED: { text: '已创建', type: 'info' },
    IN_PROGRESS: { text: '进行中', type: 'warning' },
    COMPLETED: { text: '已完成', type: 'success' }
  },
  // Artifact status
  artifact: {
    UPLOADED: { text: '已上传', type: 'info' },
    ANALYZING: { text: '分析中', type: 'warning' },
    ANALYZED: { text: '已分析', type: 'success' },
    FAILED: { text: '分析失败', type: 'danger' }
  },
  // Test status
  test: {
    TODO: { text: '待测试', type: 'info' },
    PASS: { text: '通过', type: 'success' },
    FAIL: { text: '失败', type: 'danger' },
    SKIP: { text: '跳过', type: 'warning' }
  },
  // Analysis status
  analysis: {
    PENDING: { text: '待分析', type: 'info' },
    IN_PROGRESS: { text: '分析中', type: 'warning' },
    COMPLETED: { text: '已完成', type: 'success' },
    FAILED: { text: '失败', type: 'danger' }
  },
  // Risk exclusion status
  risk: {
    PENDING: { text: '待审核', type: 'warning' },
    APPROVED: { text: '已批准', type: 'success' },
    REJECTED: { text: '已拒绝', type: 'danger' }
  }
}

const config = computed(() => {
  const typeConfig = statusConfig[props.type] || {}
  return typeConfig[props.status] || { text: props.status, type: 'info' }
})

const tagType = computed(() => config.value.type)
const displayText = computed(() => config.value.text)
const effect = computed(() => props.type === 'test' && props.status === 'PASS' ? 'light' : 'plain')
</script>
