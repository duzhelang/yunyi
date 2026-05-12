import { ref, reactive, computed, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { useHealthStore } from '@/store/healthStore'
import { useHealthValidator } from '@/composables/useHealthValidator'
import { usePrediction } from '@/composables/usePrediction'

/**
 * 健康档案表单业务逻辑组合式函数
 * 负责表单字段绑定、校验、反馈、保存、预测、重置等全部表单逻辑
 * @param {Object} options - 外部依赖选项
 * @param {Ref} options.dpfDialogVisible - 谱系函数计算弹窗可见性
 * @param {Ref} options.resultDialogVisible - 结果弹窗可见性
 * @param {Function} options.loadHistory - 加载历史记录列表的函数
 * @param {Object} options.chartStore - 图表状态管理 store（需包含 setPredictionData 方法）
 */
export function useHealthForm({ dpfDialogVisible, resultDialogVisible, loadHistory, chartStore }) {
  const store = useHealthStore()
  const validator = useHealthValidator()
  const { runPrediction, getRiskText, getRiskClass, getHealthAdvice } = usePrediction()

  // ==================== 表单字段绑定 ====================
  const form = reactive({
    Pregnancies: computed({
      get: () => store.pregnancies,
      set: (v) => { store.pregnancies = v }
    }),
    Glucose: computed({
      get: () => store.glucose,
      set: (v) => { store.glucose = v }
    }),
    BloodPressure: computed({
      get: () => store.bloodPressure,
      set: (v) => { store.bloodPressure = v }
    }),
    SkinThickness: computed({
      get: () => store.skinThickness,
      set: (v) => { store.skinThickness = v }
    }),
    Insulin: computed({
      get: () => store.insulin,
      set: (v) => { store.insulin = v }
    }),
    BMI: computed({
      get: () => store.bmi,
      set: () => {}
    }),
    DiabetesPedigreeFunction: computed({
      get: () => store.diabetesPedigreeFunction,
      set: (v) => { store.diabetesPedigreeFunction = v }
    }),
    Age: computed({
      get: () => store.age,
      set: (v) => { store.age = v }
    }),
    symptoms: computed({
      get: () => store.symptoms,
      set: (v) => { store.symptoms = v }
    }),
    file: ref(null)
  })

  // ==================== 身高体重临时变量 ====================
  const temp = reactive({ height: 170, weight: 60 })

  // ==================== 反馈状态 ====================
  const bmiFeedback = ref({ text: '', class: '', icon: '' })
  const glucoseFeedback = ref({ text: '', class: '', icon: '' })
  const bpFeedback = ref({ text: '', class: '', icon: '' })

  // ==================== 操作状态 ====================
  const saving = ref(false)
  const predicting = ref(false)
  const fileList = ref([])

  // ==================== 健康建议 ====================
  const currentAdvice = ref(null)

  // ==================== 反馈计算 ====================
  /**
   * 根据身高体重计算 BMI 并更新反馈
   */
  function calcBMI() {
    if (!temp.height || !temp.weight) {
      bmiFeedback.value = { text: '', class: '', icon: '' }
      return
    }
    store.height = temp.height
    store.weight = temp.weight
    const bmiVal = temp.weight / ((temp.height / 100) ** 2)
    const fb = validator.evaluateBMI(bmiVal)
    bmiFeedback.value = fb
  }

  /**
   * 评估血糖水平并更新反馈
   * @param {number} val - 血糖值
   */
  function evaluateGlucose(val) {
    glucoseFeedback.value = validator.evaluateGlucose(val)
  }

  /**
   * 评估血压水平并更新反馈
   * @param {number} val - 血压值
   */
  function evaluateBP(val) {
    bpFeedback.value = validator.evaluateBP(val)
  }

  // ==================== 文件上传 ====================
  /**
   * 处理上传文件变更
   * @param {Object} uploadFile - Element Plus 上传组件文件对象
   */
  function handleFileChange(uploadFile) {
    form.file = uploadFile.raw
    fileList.value = [uploadFile]
  }

  // ==================== 表单校验 ====================
  /**
   * 校验必填字段
   * @returns {boolean} 是否通过校验
   */
  function validate() {
    if (!store.age || !store.glucose || !store.bmi) {
      ElMessage.warning('请填写年龄、血糖和 BMI（身高体重）')
      return false
    }
    return true
  }

  // ==================== 健康建议生成 ====================
  /**
   * 根据当前指标生成健康建议
   */
  function generateAdvice() {
    const advices = []
    if (store.glucose > 126) {
      advices.push({ title: '控制血糖', content: '您的血糖偏高，建议减少高糖食物摄入，增加膳食纤维摄入。' })
    }
    if (store.bmi && store.bmi >= 24) {
      advices.push({ title: '体重管理', content: '建议通过合理饮食和适量运动将BMI控制在正常范围内。' })
    }
    if (store.bloodPressure && store.bloodPressure >= 140) {
      advices.push({ title: '血压监测', content: '您的血压偏高，建议减少盐分摄入，保持规律作息。' })
    }
    if (advices.length === 0) {
      advices.push({ title: '保持健康', content: '您的各项指标基本正常，请继续保持健康的生活方式。' })
      advices.push({ title: '定期体检', content: '建议每季度进行一次健康检查，及时了解身体状况。' })
    }
    advices.push({ title: '科学运动', content: '建议每周进行至少150分钟的中等强度有氧运动。' })
    advices.push({ title: '合理饮食', content: '保持均衡营养，增加蔬菜水果摄入，减少加工食品。' })
    currentAdvice.value = advices
    store.adviceData = advices
  }

  /**
   * 加载已存储的健康建议，若无则生成默认建议
   */
  function loadAdvice() {
    if (store.adviceData && Array.isArray(store.adviceData) && store.adviceData.length > 0) {
      currentAdvice.value = store.adviceData
    } else {
      currentAdvice.value = [
        { title: '保持健康', content: '您的各项指标基本正常，请继续保持健康的生活方式。' },
        { title: '定期体检', content: '建议每季度进行一次健康检查，及时了解身体状况。' },
        { title: '科学运动', content: '建议每周进行至少150分钟的中等强度有氧运动。' },
        { title: '合理饮食', content: '保持均衡营养，增加蔬菜水果摄入，减少加工食品。' }
      ]
    }
  }

  // ==================== 保存与预测 ====================
  /**
   * 仅保存档案
   * @returns {Promise<number|null>} 保存成功返回档案ID，失败返回 null
   */
  async function saveOnly() {
    if (!validate()) return
    saving.value = true
    try {
      const payload = store.toSavePayload()
      const res = await request.post('/api/health-profile/save-and-predict', payload)
      if (res.code === '200' && res.data) {
        const id = res.data.id
        ElMessage.success(`档案保存成功 (ID: ${id})`)
        if (res.data.prediction) {
          store.setPredictionResult(res.data.prediction)
        }
        if (typeof loadHistory === 'function') {
          await loadHistory()
        }
        generateAdvice()
        return id
      } else {
        ElMessage.error(res.msg || '保存失败')
        return null
      }
    } catch (e) {
      ElMessage.error('保存失败: ' + (e.response?.data?.msg || e.message))
      return null
    } finally {
      saving.value = false
    }
  }

  /**
   * 保存档案并进行预测
   */
  async function saveAndPredict() {
    if (!validate()) return
    predicting.value = true
    try {
      const payload = store.toSavePayload()
      const res = await request.post('/api/health-profile/save-and-predict', payload)
      if (res.code === '200' && res.data) {
        store.setPredictionResult(res.data.prediction)
        store.predictionId = res.data.id
        if (res.data.prediction && res.data.prediction.ai_advice) {
          store.aiAdvice = res.data.prediction.ai_advice
        }
        // 更新 chartStore 数据
        if (chartStore && typeof chartStore.setPredictionData === 'function') {
          chartStore.setPredictionData(res.data.prediction)
        }
        generateAdvice()
        if (typeof loadHistory === 'function') {
          await loadHistory()
        }
        if (resultDialogVisible) {
          resultDialogVisible.value = true
        }
        ElMessage.success('保存并检测完成')
      } else {
        ElMessage.error(res.msg || '保存失败')
      }
    } catch (e) {
      ElMessage.error('保存并检测失败: ' + (e.response?.data?.msg || e.message))
    } finally {
      predicting.value = false
    }
  }

  /**
   * 快速预测（不保存到后端）
   */
  async function quickPredict() {
    if (!validate()) return
    predicting.value = true
    try {
      const features = store.toFeatures()
      const result = await runPrediction(features)
      if (result.success) {
        store.setPredictionResult(result.data)
      } else {
        store.setPredictionResult(result.data)
        ElMessage.warning(result.message)
      }
      // 更新 chartStore 数据
      if (chartStore && typeof chartStore.setPredictionData === 'function') {
        chartStore.setPredictionData(result.data)
      }
      generateAdvice()
      if (resultDialogVisible) {
        resultDialogVisible.value = true
      }
    } catch (e) {
      ElMessage.error('检测失败: ' + e.message)
    } finally {
      predicting.value = false
    }
  }

  // ==================== 表单重置 ====================
  /**
   * 重置表单所有字段和状态
   */
  function resetForm() {
    store.resetAll()
    temp.height = 170
    temp.weight = 60
    fileList.value = []
    bmiFeedback.value = { text: '', class: '', icon: '' }
    glucoseFeedback.value = { text: '', class: '', icon: '' }
    bpFeedback.value = { text: '', class: '', icon: '' }
    currentAdvice.value = null
    store.adviceData = null
    nextTick(() => {
      calcBMI()
      evaluateGlucose(store.glucose)
      evaluateBP(store.bloodPressure)
    })
    ElMessage.info('表单已重置')
  }

  // ==================== 历史数据加载到表单 ====================
  /**
   * 将历史记录的数据加载到当前表单
   * @param {Object} item - 历史记录对象
   */
  function loadHistoryToForm(item) {
    store.pregnancies = item.pregnancies || 0
    store.glucose = item.glucose || 90
    store.bloodPressure = item.bloodPressure || 120
    store.skinThickness = item.skinThickness || 20
    store.insulin = item.insulin || 50
    store.diabetesPedigreeFunction = item.diabetesPedigreeFunction || 0.5
    store.age = item.age || 20
    store.symptoms = item.symptoms || ''
    temp.height = item.height || null
    temp.weight = item.weight || null
    evaluateGlucose(store.glucose)
    evaluateBP(store.bloodPressure)
    fileList.value = []
    form.file = null

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
      currentAdvice.value = adviceList
      store.adviceData = adviceList
    } else {
      generateAdvice()
    }
    ElMessage.success('已载入历史数据')
  }

  // ==================== 返回 ====================
  return {
    // 状态
    form,
    temp,
    bmiFeedback,
    glucoseFeedback,
    bpFeedback,
    saving,
    predicting,
    fileList,
    currentAdvice,
    // 方法
    calcBMI,
    evaluateGlucose,
    evaluateBP,
    handleFileChange,
    validate,
    generateAdvice,
    loadAdvice,
    saveOnly,
    saveAndPredict,
    quickPredict,
    resetForm,
    loadHistoryToForm
  }
}
