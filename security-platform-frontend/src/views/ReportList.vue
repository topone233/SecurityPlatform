<template>
  <div class="report-list">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>测试报告</span>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="报告名称">
          <el-input v-model="searchForm.name" placeholder="请输入报告名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadReports">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="reports" v-loading="loading" style="width: 100%">
        <el-table-column prop="name" label="报告名称" min-width="200" />
        <el-table-column prop="artifactName" label="制品" min-width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'COMPLETED' ? 'success' : 'warning'" size="small">
              {{ row.status === 'COMPLETED' ? '已完成' : '生成中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="生成时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewReport(row)" v-if="row.status === 'COMPLETED'">
              查看
            </el-button>
            <el-button type="success" link @click="exportReport(row, 'pdf')" v-if="row.status === 'COMPLETED'">
              PDF
            </el-button>
            <el-button type="warning" link @click="exportReport(row, 'html')" v-if="row.status === 'COMPLETED'">
              HTML
            </el-button>
            <el-button type="danger" link @click="deleteReport(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadReports"
        @current-change="loadReports"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import reportApi from '@/api/testReport'
import { formatDate } from '@/utils/format'

const router = useRouter()

const loading = ref(false)
const reports = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  name: ''
})

const loadReports = async () => {
  loading.value = true
  try {
    const data = await reportApi.getList({
      name: searchForm.name || undefined,
      page: currentPage.value - 1,
      size: pageSize.value
    })
    reports.value = data.content || []
    total.value = data.totalElements || 0
  } catch (e) {
    console.error('Failed to load reports:', e)
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.name = ''
  currentPage.value = 1
  loadReports()
}

const viewReport = (row) => {
  router.push(`/reports/${row.id}`)
}

const exportReport = async (row, format) => {
  try {
    const blob = await reportApi.export(row.id, format)
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${row.name}.${format.toLowerCase()}`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (e) {
    console.error('Failed to export:', e)
  }
}

const deleteReport = async (row) => {
  await ElMessageBox.confirm(`确定删除报告 "${row.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  try {
    await reportApi.delete(row.id)
    ElMessage.success('删除成功')
    loadReports()
  } catch (e) {
    console.error('Failed to delete:', e)
  }
}

onMounted(() => {
  loadReports()
})
</script>

<style lang="scss" scoped>
.report-list {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .search-form {
    margin-bottom: 20px;
  }
}
</style>