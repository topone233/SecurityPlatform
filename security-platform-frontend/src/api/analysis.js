import api from './index'

export default {
  // Get analysis progress
  getProgress(artifactId) {
    return api.get(`/analysis/${artifactId}/progress`)
  },

  // Get analysis result
  getResult(artifactId) {
    return api.get(`/analysis/${artifactId}/result`)
  },

  // Get dependency list
  getDependencies(artifactId) {
    return api.get(`/analysis/${artifactId}/dependencies`)
  },

  // Get entry points
  getEntryPoints(artifactId) {
    return api.get(`/analysis/${artifactId}/entry-points`)
  },

  // Get security concerns
  getSecurityConcerns(artifactId) {
    return api.get(`/analysis/${artifactId}/security-concerns`)
  },

  // Get API endpoints
  getApiEndpoints(artifactId) {
    return api.get(`/analysis/${artifactId}/api-endpoints`)
  },

  // Trigger re-analysis
  reanalyze(artifactId) {
    return api.post(`/analysis/${artifactId}/reanalyze`)
  }
}
