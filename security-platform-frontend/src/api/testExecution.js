import api from './index'

export default {
  // Get test executions by artifact ID
  getList(artifactId) {
    return api.get(`/test-execution/${artifactId}`)
  },

  // Get execution by ID
  getById(id) {
    return api.get(`/test-execution/detail/${id}`)
  },

  // Create test execution
  create(artifactId, data) {
    return api.post(`/test-execution/${artifactId}`, data)
  },

  // Update test execution
  update(id, data) {
    return api.put(`/test-execution/${id}`, data)
  },

  // Delete test execution
  delete(id) {
    return api.delete(`/test-execution/${id}`)
  },

  // Start test execution
  start(id) {
    return api.post(`/test-execution/${id}/start`)
  },

  // Get execution progress
  getProgress(id) {
    return api.get(`/test-execution/${id}/progress`)
  },

  // Get execution statistics
  getStatistics(artifactId) {
    return api.get(`/test-execution/${artifactId}/statistics`)
  }
}
