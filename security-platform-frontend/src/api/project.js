import api from './index'

export default {
  // Get project list
  getList(params) {
    return api.get('/projects', { params })
  },

  // Get project by ID
  getById(id) {
    return api.get(`/projects/${id}`)
  },

  // Create project
  create(data) {
    return api.post('/projects', data)
  },

  // Update project
  update(id, data) {
    return api.put(`/projects/${id}`, data)
  },

  // Delete project
  delete(id) {
    return api.delete(`/projects/${id}`)
  },

  // Get project statistics
  getStatistics(id) {
    return api.get(`/projects/${id}/statistics`)
  }
}
