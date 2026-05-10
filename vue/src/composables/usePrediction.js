import { ElMessage, ElLoading } from 'element-plus'
import request from '@/utils/request'

const predictionCache = new Map()
const CACHE_TTL = 5 * 60 * 1000

export function usePrediction() {

  function getCacheKey(features) {
    return Object.keys(features).sort().map(k => `${k}:${features[k]}`).join('|')
  }

  function getCachedResult(key) {
    const cached = predictionCache.get(key)
    if (cached && Date.now() - cached.timestamp < CACHE_TTL) {
      return cached.data
    }
    if (cached) {
      predictionCache.delete(key)
    }
    return null
  }

  function setCacheResult(key, data) {
    if (predictionCache.size > 100) {
      const oldestKey = predictionCache.keys().next().value
      predictionCache.delete(oldestKey)
    }
    predictionCache.set(key, { data, timestamp: Date.now() })
  }

  async function runPrediction(features) {
    const cacheKey = getCacheKey(features)
    const cachedResult = getCachedResult(cacheKey)
    if (cachedResult) {
      return { success: true, data: cachedResult, fromCache: true }
    }

    const loadingInstance = ElLoading.service({
      lock: true, text: '正在分析数据，请稍候...', background: 'rgba(0, 0, 0, 0.7)'
    })
    try {
      const res = await request.post('/api/predict/single', features)
      if (res.code === '200' && res.data && res.data.probability !== undefined) {
        setCacheResult(cacheKey, res.data)
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
    
    // 计算健康评分（0-100，风险概率越低评分越高）
    const healthScore = Math.max(0, Math.min(100, Math.round(100 - baseProbability)))
    
    return {
      probability: baseProbability,
      risk_level: riskLevel,
      confidence_interval: [Math.max(0, baseProbability - 10), Math.min(100, baseProbability + 10)],
      feature_importance: [
        { name: '血糖', importance: glucose > 120 ? 0.35 : 0.15 },
        { name: 'BMI', importance: bmi > 24 ? 0.25 : 0.10 },
        { name: '年龄', importance: age > 45 ? 0.20 : 0.08 },
        { name: '血压', importance: features.bloodPressure > 140 ? 0.15 : 0.05 },
        { name: '胰岛素', importance: features.insulin > 100 ? 0.10 : 0.03 },
        { name: '遗传系数', importance: features.diabetesPedigreeFunction > 0.5 ? 0.12 : 0.04 },
        { name: '皮褶厚度', importance: features.skinThickness > 30 ? 0.08 : 0.02 },
        { name: '怀孕次数', importance: features.pregnancies > 3 ? 0.06 : 0.01 }
      ],
      feature_names: ['血糖', 'BMI', '年龄', '血压', '胰岛素', '遗传系数', '皮褶厚度', '怀孕次数'],
      percentiles: {
        '血糖': Math.min(100, Math.round((glucose / 200) * 100)),
        'BMI': Math.min(100, Math.round((bmi / 40) * 100)),
        '年龄': Math.min(100, Math.round((age / 100) * 100)),
        '血压': Math.min(100, Math.round(((features.bloodPressure || 120) / 200) * 100))
      },
      similar_cases: null,
      health_score: healthScore,
      // 图表组件所需数据（使用PascalCase键名，与图表组件一致）
      features: {
        Age: age,
        BMI: bmi,
        Glucose: glucose,
        BloodPressure: features.bloodPressure || 120,
        Insulin: features.insulin || 50,
        SkinThickness: features.skinThickness || 20,
        Pregnancies: features.pregnancies || 0,
        DiabetesPedigreeFunction: features.diabetesPedigreeFunction || 0.5
      },
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

  function clearCache() {
    predictionCache.clear()
  }

  return { runPrediction, calculateLocalPrediction, getRiskText, getRiskClass, getHealthAdvice, clearCache }
}
