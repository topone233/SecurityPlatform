import api from './index'

export default {
  // Get environment list
  getList() {
    return api.get('/environments')
  },

  // Get environment by ID
  getById(id) {
    return api.get(`/environments/${id}`)
  },

  // Create environment
  create(data) {
    return api.post('/environments', data)
  },

  // Update environment
  update(id, data) {
    return api.put(`/environments/${id}`, data)
  },

  // Delete environment
  delete(id) {
    return api.delete(`/environments/${id}`)
  },

  // Test connection
  testConnection(id) {
    return api.post(`/environments/${id}/test`)
  }
}
