import api from './index'

export default {
  // Get risk exclusions by artifact ID
  getList(artifactId) {
    return api.get(`/risk-exclusion/${artifactId}`)
  },

  // Approve risk exclusion
  approve(id) {
    return api.post(`/risk-exclusion/${id}/approve`)
  },

  // Reject risk exclusion
  reject(id, reason) {
    return api.post(`/risk-exclusion/${id}/reject`, { reason })
  },

  // Create risk exclusion request
  create(data) {
    return api.post('/risk-exclusion', data)
  },

  // Update risk exclusion
  update(id, data) {
    return api.put(`/risk-exclusion/${id}`, data)
  },

  // Delete risk exclusion
  delete(id) {
    return api.delete(`/risk-exclusion/${id}`)
  },

  // Get pending approvals
  getPending(projectId) {
    return api.get(`/risk-exclusion/pending/${projectId}`)
  }
}
