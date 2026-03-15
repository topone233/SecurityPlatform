import api from './index'

export default {
  // Get report list
  getList(params) {
    return api.get('/reports', { params })
  },

  // Get report by ID
  getById(id) {
    return api.get(`/reports/${id}`)
  },

  // Generate report
  generate(artifactId) {
    return api.post(`/reports/generate/${artifactId}`)
  },

  // Export report
  export(id, format = 'pdf') {
    return api.get(`/reports/${id}/export`, {
      params: { format },
      responseType: 'blob'
    })
  },

  // Delete report
  delete(id) {
    return api.delete(`/reports/${id}`)
  },

  // Get report by artifact
  getByArtifact(artifactId) {
    return api.get(`/reports/artifact/${artifactId}`)
  }
}
