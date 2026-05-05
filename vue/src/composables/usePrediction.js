import { ElMessage, ElLoading } from 'element-plus'
import request from '@/utils/request'

export function usePrediction() {

  async function runPrediction(features) {
    const loadingInstance = ElLoading.service({
      lock: true, text: '正在分析数据，请稍候...', background: 'rgba(0, 0, 0, 0.7)'
    })
    try {
      const res = await request.post('/api/predict/single', features)
      if (res.code === '200' && res.data && res.data.probability !== undefined) {
        return { success: true, data: res.data }
      }
      return { success: false, data: calculateLocalPrediction(features), message: '后端返回格式异常，使用本地预测' }
    } catch (error) {
      return { success: false, data: calculateLocalPrediction(features), message: '预测接口调用失败，使用本地预测' }
    } finally {
      loadingInstance.close()
    }
  }

  function calculateLocalPrediction(features) {
    let baseProbability = 10
    const age = features.age || 30
    const bmi = features.bmi || 22
    const glucose = features.glucose || 90
    if (age > 60) baseProbability += 20
    else if (age > 45) baseProbability += 10
    if (bmi > 30) baseProbability += 25
    else if (bmi > 24) baseProbability += 15
    if (glucose > 120) baseProbability += 30
    else if (glucose > 100) baseProbability += 15
    baseProbability = Math.min(Math.max(baseProbability, 5), 95)
    let riskLevel = 'low'
    if (baseProbability >= 60) riskLevel = 'high'
    else if (baseProbability >= 30) riskLevel = 'medium'
    return {
      probability: baseProbability,
      risk_level: riskLevel,
      confidence_interval: [Math.max(0, baseProbability - 10), Math.min(100, baseProbability + 10)],
      feature_importance: [],
      feature_names: [],
      percentiles: {},
      similar_cases: null,
      charts: null,
      time: new Date().toLocaleString()
    }
  }

  function getRiskText(level) {
    const map = { low: '低风险', medium: '中风险', high: '高风险' }
    return map[level] || '未知'
  }

  function getRiskClass(level) {
    const map = { low: 'low-risk', medium: 'medium-risk', high: 'high-risk' }
    return map[level] || ''
  }

  function getHealthAdvice(level) {
    const map = {
      low: '您的糖尿病风险较低，请继续保持健康的生活方式，定期体检。建议每年进行一次血糖检测，保持均衡饮食和适量运动。',
      medium: '您的糖尿病风险中等，建议控制饮食，增加运动，定期监测血糖。建议每半年进行一次血糖检测，减少高糖、高脂肪食物摄入，每周至少进行150分钟中等强度运动。',
      high: '您的糖尿病风险较高，建议尽快就医，进行专业检查和治疗。建议立即咨询医生，进行详细的血糖检测和相关检查，制定个性化的预防和治疗方案。'
    }
    return map[level] || ''
  }

  return { runPrediction, calculateLocalPrediction, getRiskText, getRiskClass, getHealthAdvice }
}
