import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { title: '仪表盘' }
  },
  {
    path: '/projects',
    name: 'ProjectList',
    component: () => import('@/views/ProjectList.vue'),
    meta: { title: '项目列表' }
  },
  {
    path: '/projects/:id',
    name: 'ProjectDetail',
    component: () => import('@/views/ProjectDetail.vue'),
    meta: { title: '项目详情' }
  },
  {
    path: '/tasks/:id',
    name: 'TaskDetail',
    component: () => import('@/views/TaskDetail.vue'),
    meta: { title: '任务详情' }
  },
  {
    path: '/artifacts/:id',
    name: 'ArtifactDetail',
    component: () => import('@/views/ArtifactDetail.vue'),
    meta: { title: '制品详情' }
  },
  {
    path: '/analysis/:artifactId',
    name: 'AnalysisResult',
    component: () => import('@/views/AnalysisResult.vue'),
    meta: { title: '分析结果' }
  },
  {
    path: '/strategy/checklist/:artifactId',
    name: 'TestChecklist',
    component: () => import('@/views/TestChecklist.vue'),
    meta: { title: '测试清单' }
  },
  {
    path: '/risk-exclusion/:artifactId',
    name: 'RiskExclusion',
    component: () => import('@/views/RiskExclusion.vue'),
    meta: { title: '风险排除' }
  },
  {
    path: '/execution/:artifactId',
    name: 'TestExecution',
    component: () => import('@/views/TestExecution.vue'),
    meta: { title: '测试执行' }
  },
  {
    path: '/reports',
    name: 'ReportList',
    component: () => import('@/views/ReportList.vue'),
    meta: { title: '测试报告' }
  },
  {
    path: '/reports/:id',
    name: 'ReportDetail',
    component: () => import('@/views/ReportDetail.vue'),
    meta: { title: '报告详情' }
  },
  {
    path: '/environments',
    name: 'EnvironmentManage',
    component: () => import('@/views/EnvironmentManage.vue'),
    meta: { title: '环境管理' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || '安全测试平台'} - 安全测试辅助平台`
  next()
})

export default router
