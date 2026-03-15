<template>
  <div class="project-detail">
    <el-card shadow="never" class="info-card">
      <template #header>
        <div class="card-header">
          <span>项目信息</span>
          <div>
            <el-button @click="goBack">返回</el-button>
            <el-button type="primary" @click="showCreateDialog">创建任务</el-button>
          </div>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="项目名称">{{ project.name }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <StatusTag type="task" :status="project.status || 'CREATED'" />
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(project.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatDate(project.updatedAt) }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ project.description || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card shadow="never" style="margin-top: 20px">
      <template #header>任务列表</template>

      <el-table :data="tasks" v-loading="loadingTasks" style="width: 100%">
        <el-table-column prop="name" label="任务名称" min-width="150" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <StatusTag type="task" :status="row.status || 'CREATED'" />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="goToTask(row.id)">详情</el-button>
            <el-button type="success" link @click="startTask(row)" v-if="row.status === 'CREATED'">
              启动
            </el-button>
            <el-button type="danger" link @click="deleteTask(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Create Task Dialog -->
    <el-dialog v-model="dialogVisible" title="创建任务" width="500px">
      <el-form :model="taskForm" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="taskForm.name" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="taskForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入任务描述"
          />
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
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import projectApi from '@/api/project'
import taskApi from '@/api/task'
import StatusTag from '@/components/common/StatusTag.vue'
import { formatDate } from '@/utils/format'

const route = useRoute()
const router = useRouter()

const projectId = route.params.id
const project = ref({})
const tasks = ref([])
const loadingTasks = ref(false)

const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const taskForm = reactive({
  name: '',
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入任务名称', trigger: 'blur' }]
}

const goBack = () => {
  router.push('/projects')
}

const goToTask = (id) => {
  router.push(`/tasks/${id}`)
}

const loadProject = async () => {
  try {
    project.value = await projectApi.getById(projectId)
  } catch (e) {
    console.error('Failed to load project:', e)
  }
}

const loadTasks = async () => {
  loadingTasks.value = true
  try {
    tasks.value = await taskApi.getListByProject(projectId)
  } catch (e) {
    console.error('Failed to load tasks:', e)
  } finally {
    loadingTasks.value = false
  }
}

const showCreateDialog = () => {
  taskForm.name = ''
  taskForm.description = ''
  dialogVisible.value = true
}

const submitForm = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    await taskApi.create({
      projectId: projectId,
      name: taskForm.name,
      description: taskForm.description
    })
    ElMessage.success('创建成功')
    dialogVisible.value = false
    loadTasks()
  } catch (e) {
    console.error('Failed to create task:', e)
  } finally {
    submitting.value = false
  }
}

const startTask = async (row) => {
  await ElMessageBox.confirm(`确定启动任务 "${row.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  })
  try {
    await taskApi.start(row.id)
    ElMessage.success('任务已启动')
    loadTasks()
  } catch (e) {
    console.error('Failed to start task:', e)
  }
}

const deleteTask = async (row) => {
  await ElMessageBox.confirm(`确定删除任务 "${row.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  try {
    await taskApi.delete(row.id)
    ElMessage.success('删除成功')
    loadTasks()
  } catch (e) {
    console.error('Failed to delete task:', e)
  }
}

onMounted(() => {
  loadProject()
  loadTasks()
})
</script>

<style lang="scss" scoped>
.project-detail {
  .info-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}
</style>
