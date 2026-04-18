<template>
  <div class="diabetes-chat-container">
    <!-- 聊天头部 -->
    <div class="chat-header">
      <img src="/1234.png" alt="助手头像" class="doctor-icon" />
      <h3>糖尿病健康咨询助手</h3>
      <div class="status">
        <span class="online-dot"></span>
        <span class="status-text">在线</span>
      </div>
    </div>

    <!-- 聊天历史区域 -->
    <div class="chat-history">
      <div v-if="messages.length === 0" class="welcome-message">
        <p>您好!我是糖尿病健康咨询助手,有任何关于糖尿病的问题都可以问我~</p>
        <p class="ai-tip">*AI生成内容仅供参考,具体请遵医嘱</p>
      </div>

      <div v-for="(msg, index) in messages" :key="index" class="message-item">
        <div :class="msg.isUser ? 'user-message' : 'bot-message'">
          <span class="sender">{{ msg.isUser ? '我' : '医生助手' }}</span>
          <p class="content">
            <span v-if="msg.isUser || !msg.isTyping">{{ msg.displayContent }}</span>
            <span v-else>{{ msg.displayContent }}<span class="typing-cursor">|</span></span>
            <span class="ai-tip" v-if="!msg.isUser">*AI生成内容仅供参考,具体请遵医嘱</span>
          </p>
          <span class="time">{{ formatTime(msg.timestamp) }}</span>
        </div>
      </div>

      <div v-if="isLoading && messages.length > 0" class="loading-indicator">
        <div class="dot"></div>
        <div class="dot"></div>
        <div class="dot"></div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="input-area">
      <input
          type="text"
          v-model="question"
          placeholder="请输入您的糖尿病相关问题..."
          @keyup.enter="sendMessage"
          @focus="inputFocused = true"
          @blur="inputFocused = false"
          :class="{ focused: inputFocused }"
      />
      <button @click="sendMessage" :disabled="isLoading" class="send-btn">
        <span v-if="!isLoading">发送</span>
        <i class="el-icon-loading" v-else></i>
      </button>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

export default {
  name: 'DiabetesChat',
  data() {
    return {
      messages: [],
      question: '',
      isLoading: false,
      inputFocused: false,
      typingTimer: null
    }
  },
  methods: {
    async sendMessage() {
      if (!this.question.trim() || this.isLoading) return

      this.messages.push({
        isUser: true,
        content: this.question.trim(),
        displayContent: this.question.trim(),
        timestamp: new Date()
      })

      const userQuestion = this.question.trim()
      this.question = ''
      this.isLoading = true

      try {
        const response = await request.post('/api/diabetes/chat', null, {
          params: { question: userQuestion }
        })

        if (response && response.code === 200) {
          const botReply = response.data || '暂无有效回答'
          const botMsgIndex = this.messages.push({
            isUser: false,
            content: botReply,
            displayContent: '',
            isTyping: true,
            timestamp: new Date()
          }) - 1

          if (this.typingTimer) clearInterval(this.typingTimer)

          let currentCharIndex = 0
          this.typingTimer = setInterval(() => {
            if (currentCharIndex < botReply.length) {
              this.messages[botMsgIndex].displayContent += botReply[currentCharIndex]
              currentCharIndex++
              this.scrollToBottom()
            } else {
              clearInterval(this.typingTimer)
              this.messages[botMsgIndex].isTyping = false
              this.typingTimer = null
            }
          }, 60) // 打字速度稍快一点
        } else {
          const errorMsg = `服务提示:${response?.msg || '未知错误'}`
          this.addBotMessage(errorMsg)
        }
      } catch (error) {
        console.error('请求异常:', error)
        const errorMsg = error.response?.data?.msg || '网络连接失败,请稍后重试'
        this.addBotMessage(errorMsg)
        ElMessage.error(errorMsg)
      } finally {
        this.isLoading = false
        this.scrollToBottom()
      }
    },
    addBotMessage(content) {
      this.messages.push({
        isUser: false,
        content,
        displayContent: content,
        isTyping: false,
        timestamp: new Date()
      })
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const chatContainer = this.$el.querySelector('.chat-history')
        if (chatContainer) {
          chatContainer.scrollTop = chatContainer.scrollHeight
        }
      })
    },
    formatTime(timestamp) {
      if (!timestamp) return ''
      const date = new Date(timestamp)
      return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    }
  },
  beforeDestroy() {
    if (this.typingTimer) {
      clearInterval(this.typingTimer)
    }
  }
}
</script>

<style scoped>
.diabetes-chat-container {
  max-width: 800px;
  width: 95%;
  margin: 30px auto;
  border-radius: 20px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  background: white;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

/* 头部 */
.chat-header {
  display: flex;
  align-items: center;
  padding: 20px 24px;
  background: white;
  border-bottom: 1px solid #eaeef5;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.03);
}

.doctor-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #e6f0ff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #4a90e2;
  margin-right: 12px;
}

.chat-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.status {
  margin-left: auto;
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #64748b;
}

.online-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #10b981;
  margin-right: 6px;
  box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.4);
  animation: onlinePulse 2s infinite;
}

@keyframes onlinePulse {
  0% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.4); }
  70% { box-shadow: 0 0 0 8px rgba(16, 185, 129, 0); }
  100% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0); }
}

/* 聊天区域 */
.chat-history {
  height: 500px;
  padding: 24px;
  overflow-y: auto;
  background-color: #f8fafc;
  display: flex;
  flex-direction: column;
}

.welcome-message {
  text-align: center;
  color: #64748b;
  font-size: 15px;
  line-height: 1.6;
  margin: 0 auto;
  max-width: 80%;
}

.message-item {
  display: flex;
  margin: 16px 0;
  animation: fadeIn 0.25s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

.user-message {
  margin-left: auto;
  max-width: 75%;
}

.bot-message {
  margin-right: auto;
  max-width: 75%;
}

.user-message .content,
.bot-message .content {
  padding: 14px 18px;
  border-radius: 18px;
  font-size: 15px;
  line-height: 1.5;
  position: relative;
}

.user-message .content {
  background: #4a90e2;
  color: white;
  border-bottom-right-radius: 6px;
}

.bot-message .content {
  background: white;
  color: #1e293b;
  border: 1px solid #e2e8f0;
  border-bottom-left-radius: 6px;
}

.sender {
  font-size: 12px;
  opacity: 0.8;
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: inherit;
}

.time {
  font-size: 11px;
  opacity: 0.7;
  display: block;
  margin-top: 6px;
  text-align: right;
  color: inherit;
}

.ai-tip {
  display: block;
  font-size: 10px;
  color: #94a3b8;
  margin-top: 8px;
  font-style: italic;
}

.typing-cursor {
  animation: blink 1s infinite;
  margin-left: 2px;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

/* 加载动画 */
.loading-indicator {
  display: flex;
  gap: 6px;
  padding: 12px 18px;
  background: white;
  border-radius: 18px;
  border-bottom-left-radius: 6px;
  border: 1px solid #e2e8f0;
  margin-left: auto;
  max-width: 75%;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #94a3b8;
  animation: loadingDots 1.4s infinite ease-in-out;
}

.dot:nth-child(1) { animation-delay: -0.32s; }
.dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes loadingDots {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

/* 输入区 */
.input-area {
  display: flex;
  padding: 18px 24px;
  background: white;
  border-top: 1px solid #eaeef5;
  gap: 12px;
}

.input-area input {
  flex: 1;
  padding: 14px 20px;
  border: 1px solid #cbd5e1;
  border-radius: 24px;
  outline: none;
  font-size: 15px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.input-area input:focus {
  border-color: #4a90e2;
  box-shadow: 0 0 0 3px rgba(74, 144, 226, 0.15);
}

.send-btn {
  padding: 0 24px;
  height: 48px;
  border-radius: 24px;
  background: #4a90e2;
  color: white;
  border: none;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s, transform 0.15s;
}

.send-btn:hover:not(:disabled) {
  background: #357abd;
  transform: translateY(-1px);
}

.send-btn:disabled {
  background: #cbd5e1;
  cursor: not-allowed;
}
</style>