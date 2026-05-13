import { ref } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

/**
 * 健康档案历史记录管理组合式函数
 * 负责历史记录的加载、选中、删除及表单回填
 */
export function useHealthHistory() {

  // ==================== 状态定义 ====================

  /** 历史记录列表 */
  const historyList = ref([])
  /** 是否正在加载历史 */
  const loadingHistory = ref(false)
  /** 当前选中的历史记录ID */
  const selectedHistoryId = ref(null)

  /** 是否正在提交给诊断员 */
  const submitting = ref(false)
  /** 正在提交的诊断记录ID */
  const submittingId = ref(null)
  /** 正在删除的记录ID */
  const deletingId = ref(null)

  // ==================== 常量 ====================

  /** 记录状态映射 */
  const STATUS_MAP = {
    DONE: { text: '已完成', type: 'success' },
    PENDING: { text: '诊断中', type: 'warning' },
    SAVED: { text: '已保存', type: 'info' }
  }

  // ==================== 核心方法 ====================

  /**
   * 加载历史记录列表
   */
  async function loadHistory() {
    loadingHistory.value = true
    try {
      const res = await request.get('/api/health-profile/list')
      if (res && res.code === '200') {
        historyList.value = Array.isArray(res.data) ? res.data : []
      } else if (Array.isArray(res)) {
        historyList.value = res
      } else {
        historyList.value = []
        if (res && res.msg) {
          ElMessage.warning('加载历史: ' + res.msg)
        }
      }
    } catch (e) {
      console.error('加载历史失败', e)
      historyList.value = []
      ElMessage.error('加载历史记录失败，请检查网络或后端服务')
    } finally {
      loadingHistory.value = false
    }
  }

  /**
   * 选中某条历史记录
   * @param {Object} item - 历史记录对象
   */
  function selectHistory(item) {
    selectedHistoryId.value = item.id
  }

  /**
   * 将历史记录数据载入表单
   * @param {Object} item - 历史记录对象
   * @param {Object} store - 健康档案 store (useHealthStore)
   * @param {Object} temp - 临时数据对象 (含 height, weight 等)
   * @param {Function} evaluateGlucose - 血糖评估函数
   * @param {Function} evaluateBP - 血压评估函数
   * @param {Function} generateAdvice - 生成健康建议函数
   */
  async function loadHistoryToForm(item, store, temp, evaluateGlucose, evaluateBP, generateAdvice) {
    selectedHistoryId.value = item.id
    store.pregnancies = item.pregnancies || 0
    store.glucose = item.glucose || 90
    store.bloodPressure = item.bloodPressure || 120
    store.skinThickness = item.skinThickness || 20
    store.insulin = item.insulin || 50
    store.diabetesPedigreeFunction = item.diabetesPedigreeFunction || 0.5
    store.age = item.age || 20
    store.symptoms = item.symptoms || ''
    store.gender = item.gender || '女'
    store.exerciseFrequency = item.exerciseFrequency || '1'
    store.dietHabit = item.dietHabit || '1'
    store.smoking = item.smoking || '不吸烟'
    store.drinking = item.drinking || '不饮酒'
    temp.height = item.height || null
    temp.weight = item.weight || null
    evaluateGlucose(store.glucose)
    evaluateBP(store.bloodPressure)

    if (item.aiAdvice) {
      store.aiAdvice = item.aiAdvice
      const adviceList = []
      if (store.glucose > 126) {
        adviceList.push({ title: '控制血糖', content: '您的血糖偏高，建议减少高糖食物摄入，增加膳食纤维摄入。' })
      }
      if (store.bmi && store.bmi >= 24) {
        adviceList.push({ title: '体重管理', content: '建议通过合理饮食和适量运动将BMI控制在正常范围内。' })
      }
      if (store.bloodPressure && store.bloodPressure >= 140) {
        adviceList.push({ title: '血压监测', content: '您的血压偏高，建议减少盐分摄入，保持规律作息。' })
      }
      adviceList.push({ title: 'AI 健康建议', content: item.aiAdvice })
      store.adviceData = adviceList
    } else {
      generateAdvice()
    }
    ElMessage.success('已载入历史数据')
  }

  /**
   * 删除历史记录
   * @param {number|string} id - 记录ID
   */
  async function deleteHistory(id) {
    deletingId.value = id
    try {
      const res = await request.delete(`/api/health-profile/${id}`)
      if (res && res.code === '200') {
        ElMessage.success('记录已删除')
        if (selectedHistoryId.value === id) {
          selectedHistoryId.value = null
        }
        await loadHistory()
      } else {
        ElMessage.error(res?.msg || '删除失败')
      }
    } catch (e) {
      ElMessage.error('删除失败: ' + (e.response?.data?.msg || e.message))
    } finally {
      deletingId.value = null
    }
  }

  /**
   * 格式化时间戳为短格式
   * @param {number|string} ts - 时间戳
   * @returns {string} 格式化后的时间字符串，如 "5/11 14:30"
   */
  function formatTime(ts) {
    if (!ts) return ''
    const d = new Date(ts)
    return `${d.getMonth() + 1}/${d.getDate()} ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
  }

  // ==================== 导出 ====================

  return {
    // 状态
    historyList,
    loadingHistory,
    selectedHistoryId,
    submitting,
    submittingId,
    deletingId,
    // 常量
    STATUS_MAP,
    // 方法
    loadHistory,
    selectHistory,
    loadHistoryToForm,
    deleteHistory,
    formatTime
  }
}
