import api from './index'

export default {
  // Get test checklist by artifact ID
  getChecklist(artifactId) {
    return api.get(`/strategy/checklist/${artifactId}`)
  },

  // Update checklist item status
  updateItemStatus(artifactId, itemId, data) {
    return api.put(`/strategy/checklist/${artifactId}/item/${itemId}`, data)
  },

  // Batch update checklist items
  batchUpdateStatus(artifactId, items) {
    return api.put(`/strategy/checklist/${artifactId}/batch`, items)
  },

  // Generate test strategy
  generateStrategy(artifactId) {
    return api.post(`/strategy/generate/${artifactId}`)
  },

  // Get strategy detail
  getStrategy(artifactId) {
    return api.get(`/strategy/${artifactId}`)
  }
}
