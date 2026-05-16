import { ref, nextTick } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

export function useChat(selectedModel) {
  const messages = ref([])
  const question = ref('')
  const isLoading = ref(false)
  const chatHistory = ref(null)
  let typingTimer = null

  function scrollToBottom() {
    nextTick(() => {
      const container = chatHistory.value
      if (container) {
        container.scrollTop = container.scrollHeight
      }
    })
  }

  function formatTime(timestamp) {
    if (!timestamp) return ''
    const date = new Date(timestamp)
    return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
  }

  function addUserMessage(content) {
    messages.value.push({
      isUser: true,
      content,
      displayContent: content,
      timestamp: new Date()
    })
    scrollToBottom()
  }

  function addBotMessage(content) {
    messages.value.push({
      isUser: false,
      content,
      displayContent: content,
      isTyping: false,
      isSpeaking: false,
      timestamp: new Date()
    })
  }

  function typeBotMessage(content) {
    const botMsgIndex = messages.value.push({
      isUser: false,
      content,
      displayContent: '',
      isTyping: true,
      isSpeaking: false,
      timestamp: new Date()
    }) - 1

    if (typingTimer) clearInterval(typingTimer)

    let currentCharIndex = 0
    typingTimer = setInterval(() => {
      if (currentCharIndex < content.length) {
        messages.value[botMsgIndex].displayContent += content[currentCharIndex]
        currentCharIndex++
        scrollToBottom()
      } else {
        clearInterval(typingTimer)
        typingTimer = null
        messages.value[botMsgIndex].isTyping = false
      }
    }, 30)
  }

  async function sendMessage() {
    if (!question.value.trim() || isLoading.value) return

    messages.value.push({
      isUser: true,
      content: question.value.trim(),
      displayContent: question.value.trim(),
      timestamp: new Date()
    })

    const userQuestion = question.value.trim()
    question.value = ''
    isLoading.value = true

    try {
      const response = await request.post('/api/diabetes/chat', null, {
        params: { question: userQuestion, provider: selectedModel.value }
      })

      if (response && response.code === '200') {
        const botReply = response.data || '暂无有效回答'
        const botMsgIndex = messages.value.push({
          isUser: false,
          content: botReply,
          displayContent: '',
          isTyping: true,
          isSpeaking: false,
          timestamp: new Date()
        }) - 1

        if (typingTimer) clearInterval(typingTimer)

        let currentCharIndex = 0
        typingTimer = setInterval(() => {
          if (currentCharIndex < botReply.length) {
            messages.value[botMsgIndex].displayContent += botReply[currentCharIndex]
            currentCharIndex++
            scrollToBottom()
          } else {
            clearInterval(typingTimer)
            typingTimer = null
            messages.value[botMsgIndex].isTyping = false
          }
        }, 60)
      } else {
        const errorMsg = `服务提示:${response?.msg || '未知错误'}`
        addBotMessage(errorMsg)
      }
    } catch (error) {
      console.error('请求异常:', error)
      const errorMsg = error.response?.data?.msg || '网络连接失败,请稍后重试'
      addBotMessage(errorMsg)
      ElMessage.error(errorMsg)
    } finally {
      isLoading.value = false
      scrollToBottom()
    }
  }

  function askSample(sampleQ) {
    question.value = sampleQ
    sendMessage()
  }

  function clearTypingTimer() {
    if (typingTimer) {
      clearInterval(typingTimer)
      typingTimer = null
    }
  }

  return {
    messages,
    question,
    isLoading,
    chatHistory,
    sendMessage,
    addUserMessage,
    addBotMessage,
    typeBotMessage,
    scrollToBottom,
    formatTime,
    askSample,
    clearTypingTimer
  }
}
