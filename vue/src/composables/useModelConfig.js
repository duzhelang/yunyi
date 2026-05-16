import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

export function useModelConfig() {
  const currentUser = ref(null)
  const defaultModel = ref('glm-4-flash')
  const selectedModel = ref('glm-4-flash')
  const tempModel = ref('glm-4-flash')
  const showModelSelector = ref(false)

  const isAdmin = computed(() => {
    if (!currentUser.value) return false
    const role = currentUser.value.role || ''
    return role === 'ROLE_ADMIN' || role === 'admin'
  })

  async function loadDefaultModel() {
    try {
      const res = await request.get('/api/system/defaultModel')
      if (res.code === '200') {
        defaultModel.value = res.data
        if (!isAdmin.value) {
          selectedModel.value = defaultModel.value
        }
      }
    } catch (e) {
      console.error('获取默认模型失败', e)
      ElMessage.warning('无法获取系统默认模型，使用本地默认值')
    }
  }

  async function setDefaultModel() {
    if (!tempModel.value) return
    try {
      const res = await request.post('/api/system/defaultModel', null, {
        params: { model: tempModel.value }
      })
      if (res.code === '200') {
        defaultModel.value = tempModel.value
        selectedModel.value = tempModel.value
        ElMessage.success('默认模型已更新，所有用户将使用此模型')
        showModelSelector.value = false
      } else {
        ElMessage.error(res.msg || '设置失败')
      }
    } catch (e) {
      ElMessage.error('请求异常，设置失败')
    }
  }

  function initUser() {
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      try {
        currentUser.value = JSON.parse(storedUser)
      } catch (e) { /* ignore */ }
    }
  }

  return {
    currentUser,
    defaultModel,
    selectedModel,
    tempModel,
    showModelSelector,
    isAdmin,
    loadDefaultModel,
    setDefaultModel,
    initUser
  }
}
