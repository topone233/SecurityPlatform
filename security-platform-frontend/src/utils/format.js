export function formatFileSize(bytes) {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

export function formatDate(date) {
  if (!date) return '-'
  const d = new Date(date)
  return d.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

export function formatDuration(ms) {
  if (!ms || ms < 0) return '-'
  const seconds = Math.floor(ms / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)

  if (hours > 0) {
    return `${hours}h ${minutes % 60}m ${seconds % 60}s`
  } else if (minutes > 0) {
    return `${minutes}m ${seconds % 60}s`
  } else {
    return `${seconds}s`
  }
}

export function getRiskLevel(severity) {
  const levels = {
    CRITICAL: { text: '严重', color: '#f56c6c', level: 4 },
    HIGH: { text: '高危', color: '#e6a23c', level: 3 },
    MEDIUM: { text: '中危', color: '#409eff', level: 2 },
    LOW: { text: '低危', color: '#67c23a', level: 1 },
    INFO: { text: '信息', color: '#909399', level: 0 }
  }
  return levels[severity] || levels.INFO
}