import { defineStore } from 'pinia'
import { ref } from 'vue'
import projectApi from '@/api/project'

export const useProjectStore = defineStore('project', () => {
  const currentProject = ref(null)
  const projects = ref([])
  const loading = ref(false)

  const setCurrentProject = (project) => {
    currentProject.value = project
  }

  const fetchProjects = async (params) => {
    loading.value = true
    try {
      const data = await projectApi.getList(params)
      projects.value = data.content || []
      return data
    } finally {
      loading.value = false
    }
  }

  const fetchProjectById = async (id) => {
    const project = await projectApi.getById(id)
    currentProject.value = project
    return project
  }

  return {
    currentProject,
    projects,
    loading,
    setCurrentProject,
    fetchProjects,
    fetchProjectById
  }
})