<template>
  <div class="environment-manage">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>测试环境管理</span>
          <el-button type="primary" @click="showCreateDialog">
            <el-icon><Plus /></el-icon>
            新建环境
          </el-button>
        </div>
      </template>

      <el-table :data="environments" v-loading="loading" style="width: 100%">
        <el-table-column prop="name" label="环境名称" min-width="150" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="host" label="主机地址" min-width="150" />
        <el-table-column prop="port" label="端口" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'" size="small">
              {{ row.status === 'ACTIVE' ? '可用' : '不可用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="testConnection(row)">测试连接</el-button>
            <el-button type="primary" link @click="showEditDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="deleteEnvironment(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Create/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑环境' : '新建环境'" width="500px">
      <el-form :model="envForm" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="envForm.name" placeholder="请输入环境名称" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="envForm.type" placeholder="请选择类型">
            <el-option label="开发环境" value="DEV" />
            <el-option label="测试环境" value="TEST" />
            <el-option label="预发布环境" value="STAGING" />
            <el-option label="生产环境" value="PROD" />
          </el-select>
        </el-form-item>
        <el-form-item label="主机" prop="host">
          <el-input v-model="envForm.host" placeholder="请输入主机地址" />
        </el-form-item>
        <el-form-item label="端口" prop="port">
          <el-input-number v-model="envForm.port" :min="1" :max="65535" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="envForm.description" type="textarea" :rows="2" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import envApi from '@/api/testEnvironment'
import { formatDate } from '@/utils/format'

const loading = ref(false)
const environments = ref([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const envForm = reactive({
  id: null,
  name: '',
  type: '',
  host: '',
  port: 8080,
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入环境名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  host: [{ required: true, message: '请输入主机地址', trigger: 'blur' }],
  port: [{ required: true, message: '请输入端口', trigger: 'blur' }]
}

const loadEnvironments = async () => {
  loading.value = true
  try {
    environments.value = await envApi.getList()
  } catch (e) {
    console.error('Failed to load environments:', e)
  } finally {
    loading.value = false
  }
}

const showCreateDialog = () => {
  isEdit.value = false
  envForm.id = null
  envForm.name = ''
  envForm.type = ''
  envForm.host = ''
  envForm.port = 8080
  envForm.description = ''
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  isEdit.value = true
  envForm.id = row.id
  envForm.name = row.name
  envForm.type = row.type
  envForm.host = row.host
  envForm.port = row.port
  envForm.description = row.description
  dialogVisible.value = true
}

const submitForm = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      await envApi.update(envForm.id, envForm)
      ElMessage.success('更新成功')
    } else {
      await envApi.create(envForm)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadEnvironments()
  } catch (e) {
    console.error('Failed to submit:', e)
  } finally {
    submitting.value = false
  }
}

const testConnection = async (row) => {
  try {
    const result = await envApi.testConnection(row.id)
    if (result.success) {
      ElMessage.success('连接成功')
    } else {
      ElMessage.error('连接失败: ' + result.message)
    }
  } catch (e) {
    ElMessage.error('连接测试失败')
  }
}

const deleteEnvironment = async (row) => {
  await ElMessageBox.confirm(`确定删除环境 "${row.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  try {
    await envApi.delete(row.id)
    ElMessage.success('删除成功')
    loadEnvironments()
  } catch (e) {
    console.error('Failed to delete:', e)
  }
}

onMounted(() => {
  loadEnvironments()
})
</script>

<style lang="scss" scoped>
.environment-manage {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>