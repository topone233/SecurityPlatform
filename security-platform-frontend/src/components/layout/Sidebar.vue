<template>
  <div class="sidebar-container">
    <div class="logo">
      <el-icon><Shield /></el-icon>
      <span>安全测试平台</span>
    </div>
    <el-menu
      :default-active="activeMenu"
      router
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409eff"
    >
      <el-menu-item index="/dashboard">
        <el-icon><DataBoard /></el-icon>
        <span>仪表盘</span>
      </el-menu-item>

      <el-menu-item index="/projects">
        <el-icon><Folder /></el-icon>
        <span>项目管理</span>
      </el-menu-item>

      <el-menu-item index="/reports">
        <el-icon><Document /></el-icon>
        <span>测试报告</span>
      </el-menu-item>

      <el-menu-item index="/environments">
        <el-icon><Setting /></el-icon>
        <span>环境管理</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/projects')) return '/projects'
  if (path.startsWith('/reports')) return '/reports'
  return path
})
</script>

<style lang="scss" scoped>
.sidebar-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #263445;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  gap: 8px;

  .el-icon {
    font-size: 24px;
  }
}

.el-menu {
  border-right: none;
  flex: 1;
}
</style>
