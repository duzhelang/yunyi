import { ref } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

export function useReport(selectedModel, { addUserMessage, typeBotMessage, isLoading }) {
  const reportJson = ref('')
  const reportResult = ref('')

  async function interpretReport() {
    const prompt = `请作为糖尿病专科医生，对以下检测报告进行专业解读分析。要求：1.指出关键异常指标及其含义；2.评估糖尿病风险等级；3.给出具体的饮食、运动、监测建议。请用简洁的中文回答，控制在200字以内。\n\n报告内容：${reportJson.value}`
    addUserMessage('请解读我的检测报告')
    isLoading.value = true
    try {
      const response = await request.post('/api/diabetes/chat', null, {
        params: { question: prompt, provider: selectedModel.value }
      })
      if (response && response.code === '200') {
        const result = response.data || '暂无有效回答'
        reportResult.value = result
        typeBotMessage(result)
        ElMessage.success('报告解读完成')
      } else {
        typeBotMessage(`服务提示:${response?.msg || '未知错误'}`)
      }
    } catch (error) {
      typeBotMessage('网络连接失败，请稍后重试')
    } finally {
      isLoading.value = false
    }
  }

  return {
    reportJson,
    reportResult,
    interpretReport
  }
}
