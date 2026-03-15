# 安全测试辅助平台 - 前端

Vue 3 + Element Plus 管理后台界面

## 技术栈

- Vue 3.4 + Vite 5
- Element Plus 2.6
- Pinia 2.1 (状态管理)
- Vue Router 4.3
- Axios 1.6
- SCSS

## 快速开始

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:3000

### 构建生产版本

```bash
npm run build
```

## 项目结构

```
src/
├── api/                    # API 接口封装
│   ├── index.js            # Axios 实例配置
│   ├── project.js          # 项目管理
│   ├── task.js             # 任务管理
│   ├── artifact.js         # 制品管理
│   ├── analysis.js         # 分析引擎
│   ├── strategy.js         # 测试策略
│   ├── riskExclusion.js    # 风险排除
│   ├── testExecution.js    # 测试执行
│   ├── testReport.js       # 测试报告
│   └── testEnvironment.js  # 测试环境
│
├── components/             # 公共组件
│   ├── layout/             # 布局组件
│   ├── common/             # 通用组件
│   └── business/           # 业务组件
│
├── router/                 # 路由配置
├── stores/                 # Pinia 状态管理
├── views/                  # 页面视图
├── composables/            # 组合式函数
└── utils/                  # 工具函数
```

## 页面路由

| 页面 | 路径 | 说明 |
|------|------|------|
| 仪表盘 | /dashboard | 数据概览 |
| 项目列表 | /projects | 项目管理 |
| 项目详情 | /projects/:id | 任务列表 |
| 任务详情 | /tasks/:id | 制品上传 |
| 制品详情 | /artifacts/:id | 分析进度 |
| 分析结果 | /analysis/:artifactId | 依赖/入口点/安全关注点 |
| 测试清单 | /strategy/checklist/:artifactId | 测试项管理 |
| 风险排除 | /risk-exclusion/:artifactId | 风险审核 |
| 测试执行 | /execution/:artifactId | 执行管理 |
| 测试报告 | /reports | 报告列表 |
| 环境管理 | /environments | 测试环境配置 |

## 后端配置

后端服务需要运行在 http://localhost:8001

Vite 代理配置已将 `/api` 请求代理到后端服务。

## 开发说明

### API 响应格式

后端使用统一的 `Result<T>` 响应格式:

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

Axios 拦截器已自动处理响应格式，直接返回 `data` 字段。

### 状态标签

使用 `StatusTag` 组件显示状态:

```vue
<StatusTag type="task" status="IN_PROGRESS" />
```

支持的类型: `task`, `artifact`, `test`, `analysis`, `risk`

### 进度轮询

使用 `usePolling` composable 进行进度轮询:

```js
const { data, start, stop } = usePolling(() => api.getProgress(id), 3000)
```
