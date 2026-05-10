import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

/**
 * 图表状态管理
 * 统一管理图表数据、配置和显示状态
 */
export const useChartStore = defineStore('chart', () => {
  // 预测结果数据
  const predictionData = ref(null)
  
  // 图表配置缓存
  const chartConfigs = ref(new Map())
  
  // 图表显示状态
  const chartVisibility = ref({
    riskGauge: true,
    healthRadar: true,
    indicatorCompare: true,
    healthScore: true
  })
  
  // 图表加载状态
  const loadingStates = ref({
    riskGauge: false,
    healthRadar: false,
    indicatorCompare: false,
    healthScore: false
  })
  
  // 图表错误状态
  const errorStates = ref({
    riskGauge: null,
    healthRadar: null,
    indicatorCompare: null,
    healthScore: null
  })
  
  // 是否有预测数据
  const hasPredictionData = computed(() => {
    return predictionData.value !== null && predictionData.value !== undefined
  })
  
  // 获取风险概率
  const riskProbability = computed(() => {
    return predictionData.value?.probability || 0
  })
  
  // 获取风险等级
  const riskLevel = computed(() => {
    return predictionData.value?.risk_level || 'low'
  })
  
  // 获取特征重要性数据
  const featureImportanceData = computed(() => {
    if (!predictionData.value) return null
    
    const { feature_names, feature_importance } = predictionData.value
    if (!feature_names || !feature_importance) return null
    
    return feature_names.map((name, index) => ({
      name,
      importance: feature_importance[index] || 0
    }))
  })
  
  // 获取百分位数数据
  const percentilesData = computed(() => {
    return predictionData.value?.percentiles || {}
  })
  
  // 获取相似病例数据
  const similarCasesData = computed(() => {
    return predictionData.value?.similar_cases || null
  })
  
  // 获取置信区间
  const confidenceInterval = computed(() => {
    return predictionData.value?.confidence_interval || [0, 0]
  })
  
  // 获取特征值
  const featureValues = computed(() => {
    return predictionData.value?.features || {}
  })
  
  // 获取健康评分
  const healthScore = computed(() => {
    return predictionData.value?.health_score || 0
  })
  
  // 获取特征值映射
  const featureValuesMap = computed(() => {
    const features = predictionData.value?.features
    if (!features) return {}
    return features
  })
  
  /**
   * 设置预测结果数据
   * @param {Object} data - 预测结果数据
   */
  function setPredictionData(data) {
    predictionData.value = data
    clearChartErrors()
  }
  
  /**
   * 清除预测数据
   */
  function clearPredictionData() {
    predictionData.value = null
    clearChartErrors()
  }
  
  /**
   * 设置图表配置
   * @param {String} chartName - 图表名称
   * @param {Object} config - 图表配置
   */
  function setChartConfig(chartName, config) {
    chartConfigs.value.set(chartName, config)
  }
  
  /**
   * 获取图表配置
   * @param {String} chartName - 图表名称
   * @returns {Object|null} 图表配置
   */
  function getChartConfig(chartName) {
    return chartConfigs.value.get(chartName) || null
  }
  
  /**
   * 设置图表可见性
   * @param {String} chartName - 图表名称
   * @param {Boolean} visible - 是否可见
   */
  function setChartVisibility(chartName, visible) {
    if (chartName in chartVisibility.value) {
      chartVisibility.value[chartName] = visible
    }
  }
  
  /**
   * 切换图表可见性
   * @param {String} chartName - 图表名称
   */
  function toggleChartVisibility(chartName) {
    if (chartName in chartVisibility.value) {
      chartVisibility.value[chartName] = !chartVisibility.value[chartName]
    }
  }
  
  /**
   * 设置图表加载状态
   * @param {String} chartName - 图表名称
   * @param {Boolean} loading - 是否加载中
   */
  function setChartLoading(chartName, loading) {
    if (chartName in loadingStates.value) {
      loadingStates.value[chartName] = loading
    }
  }
  
  /**
   * 设置图表错误状态
   * @param {String} chartName - 图表名称
   * @param {String|null} error - 错误信息，null表示无错误
   */
  function setChartError(chartName, error) {
    if (chartName in errorStates.value) {
      errorStates.value[chartName] = error
    }
  }
  
  /**
   * 清除所有图表错误
   */
  function clearChartErrors() {
    Object.keys(errorStates.value).forEach(key => {
      errorStates.value[key] = null
    })
  }
  
  /**
   * 重置所有图表状态
   */
  function resetAll() {
    predictionData.value = null
    chartConfigs.value.clear()
    
    Object.keys(chartVisibility.value).forEach(key => {
      chartVisibility.value[key] = true
    })
    
    Object.keys(loadingStates.value).forEach(key => {
      loadingStates.value[key] = false
    })
    
    Object.keys(errorStates.value).forEach(key => {
      errorStates.value[key] = null
    })
  }
  
  /**
   * 获取健康评分
   * 基于风险概率计算健康评分（0-100）
   * @returns {Number} 健康评分
   */
  function getHealthScore() {
    if (!predictionData.value) return 0
    
    const probability = predictionData.value.probability || 0
    // 风险概率越低，健康评分越高
    return Math.max(0, Math.min(100, Math.round(100 - probability)))
  }
  
  /**
   * 获取风险等级文本
   * @returns {String} 风险等级文本
   */
  function getRiskLevelText() {
    const level = riskLevel.value
    const map = {
      low: '低风险',
      medium: '中风险',
      high: '高风险'
    }
    return map[level] || '未知'
  }
  
  /**
   * 获取风险等级颜色
   * @returns {String} 颜色值
   */
  function getRiskLevelColor() {
    const level = riskLevel.value
    const map = {
      low: '#52c41a',
      medium: '#faad14',
      high: '#f5222d'
    }
    return map[level] || '#1890ff'
  }
  
  /**
   * 格式化特征重要性数据用于图表显示
   * @param {Number} limit - 显示数量限制，默认8
   * @returns {Array} 格式化后的数据
   */
  function getFormattedFeatureImportance(limit = 8) {
    const data = featureImportanceData.value
    if (!data || data.length === 0) return []
    
    // 按重要性排序
    const sorted = [...data].sort((a, b) => Math.abs(b.importance) - Math.abs(a.importance))
    
    // 取前N个
    const topFeatures = sorted.slice(0, limit)
    
    return topFeatures.map(item => ({
      name: item.name,
      value: Math.abs(item.importance),
      isPositive: item.importance >= 0
    }))
  }
  
  /**
   * 获取百分位数格式化数据
   * @returns {Array} 格式化后的数据
   */
  function getFormattedPercentiles() {
    const data = percentilesData.value
    if (!data || Object.keys(data).length === 0) return []
    
    return Object.entries(data).map(([name, value]) => ({
      name,
      value: value || 0
    }))
  }
  
  return {
    // 状态
    predictionData,
    chartConfigs,
    chartVisibility,
    loadingStates,
    errorStates,
    
    // 计算属性
    hasPredictionData,
    riskProbability,
    riskLevel,
    featureImportanceData,
    percentilesData,
    similarCasesData,
    confidenceInterval,
    featureValues,
    healthScore,
    featureValuesMap,
    
    // 方法
    setPredictionData,
    clearPredictionData,
    setChartConfig,
    getChartConfig,
    setChartVisibility,
    toggleChartVisibility,
    setChartLoading,
    setChartError,
    clearChartErrors,
    resetAll,
    getHealthScore,
    getRiskLevelText,
    getRiskLevelColor,
    getFormattedFeatureImportance,
    getFormattedPercentiles
  }
})