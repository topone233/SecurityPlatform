import { ref, onUnmounted } from 'vue'

export function usePolling(fetchFn, interval = 3000) {
  const data = ref(null)
  const loading = ref(false)
  const error = ref(null)
  const isPolling = ref(false)
  let timer = null

  const fetch = async () => {
    try {
      loading.value = true
      data.value = await fetchFn()
      error.value = null
    } catch (e) {
      error.value = e
    } finally {
      loading.value = false
    }
    return data.value
  }

  const start = async () => {
    if (isPolling.value) return
    isPolling.value = true
    await fetch()
    timer = setInterval(fetch, interval)
  }

  const stop = () => {
    isPolling.value = false
    if (timer) {
      clearInterval(timer)
      timer = null
    }
  }

  const toggle = () => {
    if (isPolling.value) stop()
    else start()
  }

  onUnmounted(() => {
    stop()
  })

  return {
    data,
    loading,
    error,
    isPolling,
    fetch,
    start,
    stop,
    toggle
  }
}