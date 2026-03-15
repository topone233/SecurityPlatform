import api from './index'

export default {
  // Get artifact list by task ID
  getListByTask(taskId) {
    return api.get(`/artifacts/task/${taskId}`)
  },

  // Get artifact by ID
  getById(id) {
    return api.get(`/artifacts/${id}`)
  },

  // Upload artifact (multipart form)
  upload(taskId, file, onUploadProgress) {
    const formData = new FormData()
    formData.append('file', file)
    return api.post(`/artifacts/upload?taskId=${taskId}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      onUploadProgress
    })
  },

  // Delete artifact
  delete(id) {
    return api.delete(`/artifacts/${id}`)
  },

  // Trigger analysis
  triggerAnalysis(id) {
    return api.post(`/artifacts/${id}/analyze`)
  },

  // Download artifact
  download(id) {
    return api.get(`/artifacts/${id}/download`, { responseType: 'blob' })
  }
}
