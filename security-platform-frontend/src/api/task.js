import api from './index'

export default {
  // Get task list by project ID
  getListByProject(projectId) {
    return api.get(`/tasks/project/${projectId}`)
  },

  // Get task by ID
  getById(id) {
    return api.get(`/tasks/${id}`)
  },

  // Create task
  create(data) {
    return api.post('/tasks', data)
  },

  // Update task
  update(id, data) {
    return api.put(`/tasks/${id}`, data)
  },

  // Delete task
  delete(id) {
    return api.delete(`/tasks/${id}`)
  },

  // Start task
  start(id) {
    return api.post(`/tasks/${id}/start`)
  },

  // Complete task
  complete(id) {
    return api.post(`/tasks/${id}/complete`)
  }
}
