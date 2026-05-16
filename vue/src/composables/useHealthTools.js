import { ref, computed, reactive, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'

/**
 * 健康工具箱组合式函数
 * 包含低血糖急救、碳水计数法、足部护理、糖尿病谱系计算等工具
 */
export function useHealthTools() {
  // ==================== 工具弹窗状态 ====================

  // 低血糖急救弹窗
  const emergencyDialogVisible = ref(false)
  const emergencyTab = ref('identify')
  const emergencySeverity = ref('mild')
  const emergencyStep = ref(1)
  const emergencyTimerRunning = ref(false)
  const emergencyCountdown = ref(0)
  let emergencyTimer = null

  // 碳水计数法弹窗
  const carbDialogVisible = ref(false)
  const carbTab = ref('calculator')
  const selectedFood = ref('')
  const foodWeight = ref(100)
  const activeFoodGroup = ref([])

  // 足部护理弹窗
  const footDialogVisible = ref(false)
  const footTab = ref('checklist')
  const footChecklist = ref([
    { title: '检查足底', desc: '用镜子或让家人帮助查看足底有无伤口、水泡、红肿', done: false },
    { title: '检查趾缝', desc: '每个脚趾之间仔细检查是否有破损或真菌感染', done: false },
    { title: '检查足背', desc: '检查足背有无肿胀、皮温异常', done: false },
    { title: '检查鞋子', desc: '穿鞋前检查鞋内有无异物、鞋垫是否平整', done: false },
    { title: '洗脚测试', desc: '用温度计或手肘测试水温（不超过37℃）', done: false }
  ])

  // 糖尿病谱系函数弹窗
  const dpfDialogVisible = ref(false)
  const dpfForm = reactive({
    father: { hasDiabetes: false, ageAtDiagnosis: 50 },
    mother: { hasDiabetes: false, ageAtDiagnosis: 50 },
    sibling: { hasDiabetes: false, ageAtDiagnosis: 40 },
    grandparent: { hasDiabetes: false, ageAtDiagnosis: 60 }
  })
  const dpfResult = ref(0.08)

  // ==================== 食物数据 ====================

  const foodGroups = [
    {
      label: '主食', foods: [
        { name: '白米饭', standardGram: 100, carbPerStandard: 38, servings: 2.5, calories: 130 },
        { name: '馒头', standardGram: 80, carbPerStandard: 36, servings: 2.4, calories: 180 },
        { name: '面条（煮）', standardGram: 150, carbPerStandard: 35, servings: 2.3, calories: 165 },
        { name: '全麦面包', standardGram: 50, carbPerStandard: 24, servings: 1.6, calories: 120 },
        { name: '燕麦片', standardGram: 40, carbPerStandard: 25, servings: 1.7, calories: 150 },
        { name: '红薯', standardGram: 150, carbPerStandard: 30, servings: 2.0, calories: 135 },
        { name: '土豆', standardGram: 150, carbPerStandard: 26, servings: 1.7, calories: 110 },
        { name: '玉米', standardGram: 150, carbPerStandard: 28, servings: 1.9, calories: 130 }
      ]
    },
    {
      label: '水果', foods: [
        { name: '苹果', standardGram: 200, carbPerStandard: 22, servings: 1.5, calories: 95 },
        { name: '香蕉', standardGram: 150, carbPerStandard: 30, servings: 2.0, calories: 135 },
        { name: '橙子', standardGram: 200, carbPerStandard: 20, servings: 1.3, calories: 85 },
        { name: '葡萄', standardGram: 150, carbPerStandard: 24, servings: 1.6, calories: 100 },
        { name: '西瓜', standardGram: 300, carbPerStandard: 24, servings: 1.6, calories: 90 },
        { name: '草莓', standardGram: 200, carbPerStandard: 14, servings: 0.9, calories: 60 }
      ]
    },
    {
      label: '蔬菜', foods: [
        { name: '西兰花', standardGram: 150, carbPerStandard: 11, servings: 0.7, calories: 50 },
        { name: '胡萝卜', standardGram: 150, carbPerStandard: 15, servings: 1.0, calories: 65 },
        { name: '番茄', standardGram: 200, carbPerStandard: 10, servings: 0.7, calories: 40 },
        { name: '菠菜', standardGram: 150, carbPerStandard: 5, servings: 0.3, calories: 35 },
        { name: '黄瓜', standardGram: 200, carbPerStandard: 8, servings: 0.5, calories: 30 }
      ]
    },
    {
      label: '零食/饮品', foods: [
        { name: '含糖汽水', standardGram: 355, carbPerStandard: 39, servings: 2.6, calories: 140 },
        { name: '果汁', standardGram: 250, carbPerStandard: 30, servings: 2.0, calories: 120 },
        { name: '饼干', standardGram: 50, carbPerStandard: 32, servings: 2.1, calories: 230 },
        { name: '巧克力', standardGram: 50, carbPerStandard: 28, servings: 1.9, calories: 250 },
        { name: '蛋糕', standardGram: 80, carbPerStandard: 35, servings: 2.3, calories: 280 }
      ]
    }
  ]

  // ==================== 足部护理步骤 ====================

  const footCareSteps = [
    { title: '温水洗脚', content: '每天用温水（不超过37℃）洗脚，使用温和的肥皂。洗脚前务必用手肘或温度计测试水温。洗后用柔软毛巾轻轻擦干，特别注意脚趾缝之间。', tips: '不要用热水袋或电热毯暖脚，以免烫伤' },
    { title: '彻底检查', content: '在良好光线下仔细检查双脚各处：足底、足背、脚趾缝、指甲边缘。使用镜子检查足底，或请家人帮忙。', tips: '如果视力不好，建议让家人每周至少帮忙检查一次' },
    { title: '保湿护理', content: '在足部皮肤还微湿时涂抹保湿霜（不含香料），保持皮肤柔软防止干裂。注意：不要涂抹在脚趾缝之间。', tips: '脚趾缝之间保持干燥，可用玉米淀粉撒在脚趾间吸湿' },
    { title: '修剪指甲', content: '洗脚后指甲变软时修剪。沿指甲自然弧度修剪，不要剪太短，用指甲锉修平边缘。', tips: '视力不好或指甲增厚时，请足病医生处理，不要自己修剪' },
    { title: '选择鞋袜', content: '穿合脚的软底鞋，避免穿凉鞋、高跟鞋或尖头鞋。每天换干净、无缝的棉袜，袜子不要有松紧带。', tips: '新鞋先穿1-2小时逐渐适应，每次穿前检查鞋内有无异物' }
  ]

  // ==================== 计算属性 ====================

  // 碳水计算结果
  const calcCarbs = computed(() => {
    if (!selectedFood.value) return 0
    for (const group of foodGroups) {
      const food = group.foods.find(f => f.name === selectedFood.value)
      if (food) return Math.round((foodWeight.value / food.standardGram) * food.carbPerStandard * 10) / 10
    }
    return 0
  })

  // 份数计算
  const calcServings = computed(() => {
    return Math.round(calcCarbs.value / 15 * 10) / 10
  })

  // 热量计算
  const calcCalories = computed(() => {
    if (!selectedFood.value) return 0
    for (const group of foodGroups) {
      const food = group.foods.find(f => f.name === selectedFood.value)
      if (food) return Math.round((foodWeight.value / food.standardGram) * food.calories)
    }
    return 0
  })

  // 每日推荐碳水百分比
  const dailyCarbPercent = computed(() => {
    return Math.min(Math.round((calcCarbs.value / 130) * 100), 200)
  })

  // 足部检查完成百分比
  const footCheckPercent = computed(() => {
    const done = footChecklist.value.filter(i => i.done).length
    return Math.round((done / footChecklist.value.length) * 100)
  })

  // 糖尿病谱系风险等级
  const dpfRiskClass = computed(() => {
    if (dpfResult.value < 0.4) return 'low-risk'
    if (dpfResult.value < 0.8) return 'medium-risk'
    return 'high-risk'
  })

  // 糖尿病谱系风险文字描述
  const dpfRiskText = computed(() => {
    if (dpfResult.value < 0.4) return '低风险'
    if (dpfResult.value < 0.8) return '中等风险'
    return '高风险'
  })

  // 糖尿病谱系进度条宽度
  const dpfBarWidth = computed(() => {
    return Math.min(Math.max(((dpfResult.value - 0.08) / (2.42 - 0.08)) * 100, 0), 100)
  })

  // ==================== 工具方法 ====================

  /**
   * 打开指定工具弹窗
   * @param {string} type - 工具类型：'emergency' | 'carb-count' | 'foot-care'
   */
  function openTool(type) {
    if (type === 'emergency') {
      emergencySeverity.value = 'mild'
      emergencyStep.value = 1
      emergencyTab.value = 'identify'
      emergencyDialogVisible.value = true
    } else if (type === 'carb-count') {
      selectedFood.value = ''
      foodWeight.value = 100
      carbTab.value = 'calculator'
      carbDialogVisible.value = true
    } else if (type === 'foot-care') {
      footTab.value = 'checklist'
      footDialogVisible.value = true
    }
  }

  /**
   * 启动低血糖15分钟计时器
   */
  function startEmergencyTimer() {
    if (emergencyTimer) clearInterval(emergencyTimer)
    emergencyCountdown.value = 900
    emergencyTimerRunning.value = true
    emergencyTimer = setInterval(() => {
      emergencyCountdown.value--
      if (emergencyCountdown.value <= 0) {
        clearInterval(emergencyTimer)
        emergencyTimer = null
        emergencyTimerRunning.value = false
        emergencyStep.value = Math.max(emergencyStep.value, 4)
        ElMessage.success('⏱ 15分钟已到！请复测血糖')
      }
    }, 1000)
  }

  /**
   * 计算糖尿病谱系函数值
   */
  function calculateDPF() {
    const baseValue = 0.08
    let totalScore = 0
    const familyMembers = [
      { data: dpfForm.father, weight: 0.5 },
      { data: dpfForm.mother, weight: 0.5 },
      { data: dpfForm.sibling, weight: 0.5 },
      { data: dpfForm.grandparent, weight: 0.25 }
    ]
    let hasAnyDiabetes = false
    familyMembers.forEach(member => {
      if (member.data.hasDiabetes) {
        hasAnyDiabetes = true
        const age = member.data.ageAtDiagnosis || 50
        let ageFactor = 1.0
        if (age < 30) ageFactor = 1.8
        else if (age < 40) ageFactor = 1.5
        else if (age <= 50) ageFactor = 1.0
        else if (age <= 60) ageFactor = 0.8
        else ageFactor = 0.6
        totalScore += member.weight * ageFactor
      }
    })
    dpfResult.value = hasAnyDiabetes
      ? Math.min(Math.max(baseValue + totalScore * 0.8, 0.08), 2.42)
      : baseValue
    ElMessage.success('计算完成')
  }

  /**
   * 应用DPF计算结果到store
   * @param {object} store - 状态管理store实例
   */
  function applyDPFResult(store) {
    const val = parseFloat(dpfResult.value.toFixed(3))
    store.diabetesPedigreeFunction = val
    dpfDialogVisible.value = false
    ElMessage.success(`已应用谱系函数值：${val}`)
  }

  // ==================== 生命周期清理 ====================

  onUnmounted(() => {
    if (emergencyTimer) clearInterval(emergencyTimer)
  })

  // ==================== 导出 ====================

  return {
    // 低血糖急救弹窗状态
    emergencyDialogVisible,
    emergencyTab,
    emergencySeverity,
    emergencyStep,
    emergencyTimerRunning,
    emergencyCountdown,

    // 碳水计数法弹窗状态
    carbDialogVisible,
    carbTab,
    selectedFood,
    foodWeight,
    activeFoodGroup,

    // 足部护理弹窗状态
    footDialogVisible,
    footTab,
    footChecklist,

    // 糖尿病谱系弹窗状态
    dpfDialogVisible,
    dpfForm,
    dpfResult,

    // 数据
    foodGroups,
    footCareSteps,

    // 计算属性
    calcCarbs,
    calcServings,
    calcCalories,
    dailyCarbPercent,
    footCheckPercent,
    dpfRiskClass,
    dpfRiskText,
    dpfBarWidth,

    // 方法
    openTool,
    startEmergencyTimer,
    calculateDPF,
    applyDPFResult
  }
}
