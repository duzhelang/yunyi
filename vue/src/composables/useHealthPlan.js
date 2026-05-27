import { ref } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { mockPlans } from '@/data/diabetesMockData'

const CACHE_KEY = 'health_plan_cache'
const CACHE_TTL = 2 * 60 * 1000

function saveCache(data) {
  try {
    sessionStorage.setItem(CACHE_KEY, JSON.stringify({ data, ts: Date.now() }))
  } catch (e) { /* ignore */ }
}

function loadCache() {
  try {
    const raw = sessionStorage.getItem(CACHE_KEY)
    if (!raw) return null
    const { data, ts } = JSON.parse(raw)
    if (Date.now() - ts > CACHE_TTL) {
      sessionStorage.removeItem(CACHE_KEY)
      return null
    }
    return data
  } catch (e) {
    return null
  }
}

export function useHealthPlan(selectedModel, { addUserMessage, typeBotMessage, isLoading }) {
  const cached = loadCache()
  const riskLevel = ref(cached?.riskLevel || '')
  const abnormalIndicators = ref(cached?.abnormalIndicators || [])
  const healthPlan = ref(cached?.healthPlan || null)
  const planCollapsed = ref(false)
  const collapsedDays = ref([])
  const isGeneratingPlan = ref(false)
  const indicators = ['血糖', 'BMI', '血压', '胰岛素']

  function toggleDay(index) {
    const i = collapsedDays.value.indexOf(index)
    if (i === -1) collapsedDays.value.push(index)
    else collapsedDays.value.splice(i, 1)
  }

  async function generateHealthPlan() {
    const levelMap = { low: '低风险', medium: '中风险', high: '高风险' }
    const abnormalText = abnormalIndicators.value.length > 0 ? abnormalIndicators.value.join('、') : '无特殊异常'
    const planPrompt = `请作为糖尿病专科医生，根据以下信息生成一周健康计划。要求严格按JSON格式返回一个数组，每个元素包含diet(饮食建议)、exercise(运动建议)、notes(注意事项)三个字段，共7天。只返回JSON数组，不要其他文字。\n\n风险等级：${levelMap[riskLevel.value]}\n异常指标：${abnormalText}`
    addUserMessage(`请问我要注意什么（${levelMap[riskLevel.value]}，异常指标：${abnormalText}）`)
    isLoading.value = true
    isGeneratingPlan.value = true
    let usedMock = false
    try {
      const response = await request.post('/api/diabetes/chat', null, {
        params: { question: planPrompt, provider: selectedModel.value }
      })
      if (response && response.code === '200') {
        const reply = response.data || ''
        let planData = null
        try {
          const jsonMatch = reply.match(/\[[\s\S]*\]/)
          if (jsonMatch) planData = JSON.parse(jsonMatch[0])
        } catch (e) { /* parse failed */ }
        if (planData && Array.isArray(planData) && planData.length >= 5) {
          healthPlan.value = planData.slice(0, 7)
        } else {
          healthPlan.value = mockPlans[riskLevel.value] || mockPlans.low
          usedMock = true
        }
      } else {
        healthPlan.value = mockPlans[riskLevel.value] || mockPlans.low
        usedMock = true
      }
    } catch (error) {
      healthPlan.value = mockPlans[riskLevel.value] || mockPlans.low
      usedMock = true
    }
    saveCache({ riskLevel: riskLevel.value, abnormalIndicators: abnormalIndicators.value, healthPlan: healthPlan.value })
    planCollapsed.value = false
    collapsedDays.value = []
    if (usedMock) {
      typeBotMessage('已为您生成标准健康计划，请查看下方卡片了解详情')
      ElMessage.info('AI生成异常，已使用标准方案')
    } else {
      try {
        const summaryPrompt = `请作为糖尿病专科医生，针对以下一周健康计划，用3-5句话总结核心要点和特别注意事项。只说重点，不要逐天罗列，用简洁中文回答。\n${JSON.stringify(healthPlan.value)}`
        const summaryRes = await request.post('/api/diabetes/chat', null, {
          params: { question: summaryPrompt, provider: selectedModel.value }
        })
        if (summaryRes && summaryRes.code === '200') {
          typeBotMessage(summaryRes.data || '健康计划已生成，请查看下方卡片')
        } else {
          typeBotMessage('健康计划已生成，请查看下方卡片了解详情')
        }
      } catch (e) {
        typeBotMessage('健康计划已生成，请查看下方卡片了解详情')
      }
      ElMessage.success('健康计划已生成')
    }
    isGeneratingPlan.value = false
    isLoading.value = false
  }

  async function savePlanToRecord() {
    if (!healthPlan.value || healthPlan.value.length === 0) {
      ElMessage.warning('暂无健康计划可保存')
      return
    }
    try {
      const planText = healthPlan.value.map((day, i) =>
        `第${i + 1}天:\n  饮食: ${day.diet}\n  运动: ${day.exercise}\n  注意: ${day.notes}`
      ).join('\n\n')
      const summary = `基于风险等级(${riskLevel.value === 'high' ? '高' : riskLevel.value === 'medium' ? '中' : '低'})生成的${healthPlan.value.length}天健康计划`
      const payload = {
        recordType: 'ai_plan',
        recordDate: new Date().toISOString().replace('T', ' ').substring(0, 19),
        diagnosis: summary,
        treatmentPlan: planText
      }
      const res = await request.post('/api/patient-visit', payload)
      if (res && res.code === '200') {
        ElMessage.success('已保存到诊疗档案')
      } else {
        ElMessage.error(res?.msg || '保存失败')
      }
    } catch (e) {
      console.error('保存计划失败', e)
      ElMessage.error('保存失败')
    }
  }

  return {
    riskLevel,
    abnormalIndicators,
    healthPlan,
    planCollapsed,
    collapsedDays,
    isGeneratingPlan,
    indicators,
    toggleDay,
    generateHealthPlan,
    savePlanToRecord
  }
}
