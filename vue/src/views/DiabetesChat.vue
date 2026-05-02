<template>
  <div class="chat-wrapper">
    <div class="diabetes-chat-container">
      <!-- 聊天头部 -->
      <div class="chat-header">
        <el-icon :size="24" class="doctor-icon"><ChatDotRound /></el-icon>
        <h3>糖尿病健康咨询助手</h3>
        <div class="status">
          <span class="online-dot"></span>
          <span class="status-text">在线</span>
        </div>
      </div>

      <!-- 聊天历史区域 -->
      <div class="chat-history" ref="chatHistory">
        <div v-if="messages.length === 0" class="welcome-message">
          <p>您好!我是糖尿病健康咨询助手,有任何关于糖尿病的问题都可以问我~</p>
<!--          <p class="ai-tip">*AI生成内容仅供参考,具体请遵医嘱</p>-->
        </div>

        <div v-if="messages.length === 0" class="quick-questions-panel">
          <div class="panel-header">💡 试试这些问题</div>
          <div v-for="group in quickQuestions" :key="group.category" class="question-group">
            <div class="group-label">{{ group.category }}</div>
            <div class="group-questions">
              <span
                v-for="q in group.questions"
                :key="q"
                class="question-chip"
                @click="askSample(q)"
              >{{ q }}</span>
            </div>
          </div>
        </div>

        <div v-for="(msg, index) in messages" :key="index" class="message-item">
          <div :class="msg.isUser ? 'user-message' : 'bot-message'">
            <span class="sender">{{ msg.isUser ? '我' : '医生助手' }}</span>
            <p class="content">
              <span v-if="msg.isUser || !msg.isTyping">{{ msg.displayContent }}</span>
              <span v-else>{{ msg.displayContent }}<span class="typing-cursor">|</span></span>
<!--              <span class="ai-tip" v-if="!msg.isUser">*AI生成内容仅供参考,具体请遵医嘱</span>-->
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
        <!-- 管理员设置模型区域 -->
        <div v-if="isAdmin" class="model-setting">
          <el-button link @click="showModelSelector = !showModelSelector">⚙️ 设置默认模型</el-button>
          <div v-if="showModelSelector" class="model-selector">
            <el-select v-model="tempModel" size="small" style="width: 150px">
              <el-option label="智谱GLM-4-Flash" value="glm-4-flash" />
              <el-option label="智谱GLM-4.7-Flash" value="glm-4.7-flash" />
              <el-option label="DeepSeek" value="deepseek" />
              <el-option label="Kimi" value="kimi" />
              <el-option label="MiMo-Pro" value="mimo-v2.5-pro" />
              <el-option label="MiMo-Flash" value="mimo-v2-flash" />
            </el-select>
            <el-button type="primary" size="small" @click="setDefaultModel">保存</el-button>
          </div>
          <el-tag style="margin-left:10px" size="small">当前默认：{{ defaultModel }}</el-tag>
        </div>

        <input
            link
            v-model="question"
            placeholder="请输入您的糖尿病相关问题..."
            @keyup.enter="sendMessage"
            @focus="inputFocused = true"
            @blur="inputFocused = false"
            :class="{ focused: inputFocused }"
            :disabled="isLoading"
        />
        <button @click="sendMessage" :disabled="isLoading" class="send-btn">
          <span v-if="!isLoading">发送</span>
          <i class="el-icon-loading" v-else></i>
        </button>
      </div>

      <!-- 报告解读 -->
      <div class="additional-features">
        <h2>附加功能</h2>
        <div class="feature-card">
          <h3>报告解读</h3>
          <div class="feature-content">
            <textarea
                v-model="reportJson"
                placeholder="请粘贴预测结果的JSON内容..."
                rows="4"
                class="report-textarea"
            ></textarea>
            <button @click="interpretReport" :disabled="!reportJson || isLoading" class="feature-btn">
              解读报告
            </button>
            <div class="report-interpretation" v-if="reportInterpretation">
              <h4>报告解读结果</h4>
              <div class="interpretation-content">
                {{ reportInterpretation }}
              </div>
            </div>
          </div>
        </div>

        <!-- 健康计划生成 -->
        <div class="feature-card">
          <h3>健康计划生成</h3>
          <div class="feature-content">
            <div class="plan-form">
              <div class="form-item">
                <label>风险等级</label>
                <select v-model="riskLevel" class="plan-select">
                  <option value="">请选择</option>
                  <option value="low">低风险</option>
                  <option value="medium">中风险</option>
                  <option value="high">高风险</option>
                </select>
              </div>
              <div class="form-item">
                <label>异常指标</label>
                <div class="checkbox-group">
                  <label v-for="indicator in indicators" :key="indicator">
                    <input
                        type="checkbox"
                        :value="indicator"
                        v-model="abnormalIndicators"
                    />
                    {{ indicator }}
                  </label>
                </div>
              </div>
              <button @click="generateHealthPlan" :disabled="!riskLevel || isLoading" class="feature-btn">
                生成健康计划
              </button>
            </div>
            <div class="health-plan" v-if="healthPlan">
              <h4>一周健康计划</h4>
              <div v-for="(day, index) in healthPlan" :key="index" class="plan-day">
                <div class="day-header">第{{ index + 1 }}天</div>
                <div class="plan-content">
                  <div class="plan-item">
                    <span class="plan-label">饮食建议:</span>
                    <span class="plan-value">{{ day.diet }}</span>
                  </div>
                  <div class="plan-item">
                    <span class="plan-label">运动建议:</span>
                    <span class="plan-value">{{ day.exercise }}</span>
                  </div>
                  <div class="plan-item">
                    <span class="plan-label">注意事项:</span>
                    <span class="plan-value">{{ day.notes }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧悬浮滚动问题面板 -->
    <div v-if="messages.length > 0"
         class="floating-side-panel"
         @mouseenter="pauseMarquee"
         @mouseleave="resumeMarquee">
      <div class="panel-title-wrapper">
        <div class="panel-title">💡 试试这些问题</div>
      </div>
      <div class="marquee-viewport">
        <div class="marquee-track" ref="marqueeTrack">
          <div
            v-for="(q, i) in allQuestions"
            :key="'a'+i"
            class="marquee-item"
            @click="askSample(q)"
          >{{ q }}</div>
          <div
            v-for="(q, i) in allQuestions"
            :key="'b'+i"
            class="marquee-item"
            @click="askSample(q)"
          >{{ q }}</div>
        </div>
      </div>
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
      typingTimer: null,
      reportJson: '',
      reportInterpretation: null,
      riskLevel: '',
      abnormalIndicators: [],
      healthPlan: null,
      indicators: ['血糖', 'BMI', '血压', '胰岛素'],
      quickQuestions: [
        {
          category: '基础认知',
          questions: [
            '空腹血糖正常值是多少？超过多少算糖尿病？',
            '糖尿病能根治吗？有没有最新的治疗方法？'
          ]
        },
        {
          category: '风险评估',
          questions: [
            '我妈妈是糖尿病，我会不会遗传？'
          ]
        },
        {
          category: '症状识别',
          questions: [
            '糖尿病早期有哪些症状需要注意？',
            '经常觉得口渴、尿多，是不是糖尿病前兆？',
            '最近脚有点麻，是不是糖尿病神经病变？'
          ]
        },
        {
          category: '治疗与生活管理',
          questions: [
            '我刚刚确诊2型糖尿病，应该先控制饮食还是直接吃药？',
            '晚饭没吃，为什么早上空腹血糖还是高？',
            '得了糖尿病还能吃米饭和面食吗？怎么吃比较好？',
            '糖尿病人可以吃水果吗？哪些水果比较安全？'
          ]
        }
      ],
      // 用户与模型配置
      currentUser: null,
      defaultModel: 'glm-4-flash',
      selectedModel: 'glm-4-flash',
      tempModel: 'glm-4-flash',
      showModelSelector: false,
      allQuestions: [],
      marqueePaused: false,
      marqueeAnimId: null,
    }
  },
  computed: {
    isAdmin() {
      if (!this.currentUser) return false
      const role = this.currentUser.role || ''
      return role === 'ROLE_ADMIN' || role === 'admin'
    }
  },
  watch: {
    'messages.length'(newLen) {
      if (newLen > 0) {
        this.$nextTick(() => {
          this.startMarquee();
        });
      }
    }
  },
  async mounted() {
    const storedUser = localStorage.getItem("user")
    if (storedUser) {
      try {
        this.currentUser = JSON.parse(storedUser)
      } catch (e) { /* ignore */ }
    }

    // 加载系统默认模型（管理员和普通用户都需要获取）
    await this.loadDefaultModel()

    // 所有用户都使用系统默认模型
    this.selectedModel = this.defaultModel

    // 管理员额外允许切换模型
    if (this.isAdmin) {
      this.tempModel = this.defaultModel
    }

    // 检查URL参数，获取上下文信息
    const query = this.$route.query;
    if (query.context) {
      this.messages.push({
        isUser: false,
        content: `根据您的检测结果：${query.context}，我可以为您提供更针对性的建议。`,
        displayContent: `根据您的检测结果：${query.context}，我可以为您提供更针对性的建议。`,
        isTyping: false,
        timestamp: new Date()
      });
    }

    // 初始化滚动问题列表（扁平化所有分类问题）
    this.allQuestions = this.quickQuestions.reduce((arr, group) => {
      return arr.concat(group.questions);
    }, []);
  },
  methods: {
    async loadDefaultModel() {
      try {
        const res = await request.get('/api/system/defaultModel')
        if (res.code === '200') {
          this.defaultModel = res.data
          if (!this.isAdmin) {
            this.selectedModel = this.defaultModel
          }
        }
      } catch (e) {
        console.error('获取默认模型失败', e)
        ElMessage.warning('无法获取系统默认模型，使用本地默认值')
      }
    },
    async setDefaultModel() {
      if (!this.tempModel) return
      try {
        const res = await request.post('/api/system/defaultModel', null, {
          params: { model: this.tempModel }
        })
        if (res.code === '200') {
          this.defaultModel = this.tempModel
          // 管理员自己也切换到新模型
          this.selectedModel = this.tempModel
          ElMessage.success('默认模型已更新，所有用户将使用此模型')
          this.showModelSelector = false
        } else {
          ElMessage.error(res.msg || '设置失败')
        }
      } catch (e) {
        ElMessage.error('请求异常，设置失败')
      }
    },
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
          params: {
            question: userQuestion,
            provider: this.selectedModel   // 使用管理员设定的模型
          }
        })

        if (response && response.code === '200') {
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
          }, 60)
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
        const chatContainer = this.$refs.chatHistory
        if (chatContainer) {
          chatContainer.scrollTop = chatContainer.scrollHeight
        }
      })
    },
    formatTime(timestamp) {
      if (!timestamp) return ''
      const date = new Date(timestamp)
      return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    },
    interpretReport() {
      this.isLoading = true
      setTimeout(() => {
        this.isLoading = false
        this.reportInterpretation = '根据您的报告结果，您的血糖水平偏高，属于糖尿病高风险人群。建议您调整饮食结构，增加运动量，并定期监测血糖变化。'
        ElMessage.success('报告解读完成')
      }, 1000)
    },
    generateHealthPlan() {
      this.isLoading = true
      setTimeout(() => {
        this.isLoading = false
        this.healthPlan = [
          {
            diet: '早餐：全麦面包+鸡蛋+牛奶；午餐：糙米+清蒸鱼+蔬菜；晚餐：小米粥+瘦肉+凉拌菜',
            exercise: '快走30分钟，下午做瑜伽20分钟',
            notes: '监测早餐后2小时血糖'
          },
          {
            diet: '早餐：燕麦粥+坚果；午餐：荞麦面+鸡肉+蔬菜；晚餐：红薯+豆腐+蔬菜',
            exercise: '游泳40分钟',
            notes: '监测午餐后2小时血糖'
          },
          {
            diet: '早餐：鸡蛋+蔬菜沙拉；午餐：糙米饭+牛肉+蔬菜；晚餐：南瓜粥+鱼肉+蔬菜',
            exercise: '骑自行车30分钟，晚上散步20分钟',
            notes: '监测晚餐后2小时血糖'
          },
          {
            diet: '早餐：全麦馒头+豆浆；午餐：玉米+虾+蔬菜；晚餐：小米粥+瘦肉+蔬菜',
            exercise: '慢跑20分钟，做力量训练15分钟',
            notes: '保持水分摄入'
          },
          {
            diet: '早餐：鸡蛋+水果；午餐：糙米饭+鸡肉+蔬菜；晚餐：红薯+豆腐+蔬菜',
            exercise: '快走40分钟',
            notes: '避免久坐'
          },
          {
            diet: '早餐：燕麦粥+坚果；午餐：荞麦面+鱼肉+蔬菜；晚餐：小米粥+瘦肉+蔬菜',
            exercise: '游泳30分钟，下午做瑜伽20分钟',
            notes: '监测空腹血糖'
          },
          {
            diet: '早餐：全麦面包+牛奶；午餐：糙米+牛肉+蔬菜；晚餐：南瓜粥+鸡肉+蔬菜',
            exercise: '骑自行车40分钟',
            notes: '总结一周饮食和运动情况'
          }
        ]
        ElMessage.success('健康计划已生成')
      }, 1000)
    },
    askSample(sampleQ) {
      this.question = sampleQ;
      this.sendMessage();
    },
    startMarquee() {
      if (this.marqueeAnimId) return;
      const track = this.$refs.marqueeTrack;
      if (!track) return;
      const speed = 0.3;
      const animate = () => {
        if (!this.marqueePaused) {
          const currentY = parseFloat(track.style.transform?.match(/-?[\d.]+/)?.[0] || 0);
          const halfHeight = track.scrollHeight / 2;
          const newY = currentY - speed;
          track.style.transform = `translateY(${Math.abs(newY) >= halfHeight ? 0 : newY}px)`;
        }
        this.marqueeAnimId = requestAnimationFrame(animate);
      };
      this.marqueeAnimId = requestAnimationFrame(animate);
    },
    pauseMarquee() {
      this.marqueePaused = true;
    },
    resumeMarquee() {
      this.marqueePaused = false;
    }
  },
  beforeDestroy() {
    if (this.marqueeAnimId) {
      cancelAnimationFrame(this.marqueeAnimId);
      this.marqueeAnimId = null;
    }
    if (this.typingTimer) {
      clearInterval(this.typingTimer)
    }
  }
}
</script>

<style scoped>
/* 外层 flex 布局 */
.chat-wrapper {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  gap: 0;
  max-width: 1100px;
  width: 95%;
  margin: 30px auto;
}

/* 原有样式保持完全不变 */
.diabetes-chat-container {
  flex: 1;
  max-width: 800px;
  min-width: 0;
  border-radius: 20px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  background: white;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.chat-header {
  display: flex;
  align-items: center;
  padding: 20px 24px;
  background: white;
  border-bottom: 1px solid #eaeef5;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.03);
}

.doctor-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #e6f0ff;
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

/* 输入区域 */
.input-area {
  display: flex;
  padding: 18px 24px;
  background: white;
  border-top: 1px solid #eaeef5;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.input-area input {
  flex: 1;
  min-width: 200px;
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
  white-space: nowrap;
}

.send-btn:hover:not(:disabled) {
  background: #357abd;
  transform: translateY(-1px);
}

.send-btn:disabled {
  background: #cbd5e1;
  cursor: not-allowed;
}

/* 模型设置区域新增样式 */
.model-setting {
  display: flex;
  align-items: center;
  margin-right: 8px;
}
.model-setting .el-button--text {
  padding: 0 8px;
  font-size: 14px;
}
.model-selector {
  display: flex;
  align-items: center;
  gap: 8px;
}
.model-display {
  margin-right: 8px;
}

/* 附加功能区域 */
.additional-features {
  margin-top: 40px;
  padding: 0 24px;
}

.additional-features h2 {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 24px;
  text-align: center;
}

.feature-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  padding: 24px;
  margin-bottom: 24px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.feature-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
}

.feature-card h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 16px;
  border-bottom: 1px solid #e2e8f0;
  padding-bottom: 12px;
}

.feature-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.report-textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  font-size: 14px;
  resize: vertical;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.report-textarea:focus {
  border-color: #4a90e2;
  box-shadow: 0 0 0 3px rgba(74, 144, 226, 0.15);
}

.feature-btn {
  align-self: flex-start;
  padding: 10px 20px;
  border-radius: 20px;
  background: #4a90e2;
  color: white;
  border: none;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s, transform 0.15s;
}

.feature-btn:hover:not(:disabled) {
  background: #357abd;
  transform: translateY(-1px);
}

.feature-btn:disabled {
  background: #cbd5e1;
  cursor: not-allowed;
}

.report-interpretation {
  background: #f0f9eb;
  border-radius: 8px;
  padding: 16px;
  margin-top: 8px;
}

.report-interpretation h4 {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 12px;
}

.interpretation-content {
  color: #64748b;
  line-height: 1.5;
  font-size: 14px;
}

.plan-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  font-size: 14px;
  font-weight: 500;
  color: #475569;
}

.plan-select {
  padding: 8px 12px;
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.plan-select:focus {
  border-color: #4a90e2;
  box-shadow: 0 0 0 3px rgba(74, 144, 226, 0.15);
}

.checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.checkbox-group label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #475569;
  cursor: pointer;
}

.health-plan {
  margin-top: 16px;
}

.health-plan h4 {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 16px;
}

.plan-day {
  background: #f8fafc;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  border-left: 4px solid #4a90e2;
}

.day-header {
  font-weight: 600;
  color: #4a90e2;
  margin-bottom: 12px;
  font-size: 14px;
}

.plan-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.plan-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.plan-label {
  font-weight: 500;
  color: #475569;
  font-size: 13px;
}

.plan-value {
  color: #64748b;
  font-size: 13px;
  line-height: 1.4;
}

/* 示例问题面板 */
.quick-questions-panel {
  background: #f1f5f9;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
}
.panel-header {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 12px;
}
.question-group {
  margin-bottom: 12px;
}
.group-label {
  font-size: 13px;
  font-weight: 500;
  color: #475569;
  margin-bottom: 6px;
}
.group-questions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.question-chip {
  font-size: 13px;
  padding: 6px 14px;
  background: white;
  border: 1px solid #cbd5e1;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s;
  color: #1e293b;
}
.question-chip:hover {
   border-color: #4a90e2;
   color: #4a90e2;
   box-shadow: 0 2px 6px rgba(74,144,226,0.15);
 }

 /* ========== 右侧悬浮跑马灯面板 ========== */
 .floating-side-panel {
   margin-top: 50px;
   margin-left: 40px;
   width: 250px;
   padding: 10px;
   flex-shrink: 0;
 }
 .panel-title-wrapper {
   padding: 0 0 10px 0;
 }
 .panel-title {
   font-size: 16px;
   font-weight: 600;
   color: #1e293b;
   text-align: center;
 }
 .marquee-viewport {
   height: 360px;
   overflow: hidden;
   border-radius: 16px;
   padding: 10px;
 }
 .marquee-track {
   will-change: transform;
   transition: none;
 }
 .marquee-item {
   padding: 12px 12px;
   margin-bottom: 10px;
   font-size: 16px;
   line-height: 1.6;
   color: #1e293b;
   background: linear-gradient(135deg, #e8f0fe 0%, #d0e0ff 80%);

   border: 1px solid #d0d7e2;
   border-radius: 12px;
   cursor: pointer;
   word-break: break-all;
   text-indent: 2em;
   transition: all 0.3s ease;
   box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08), 0 1px 3px rgba(0, 0, 0, 0.06);
 }
 .marquee-item:hover {
   background: linear-gradient(135deg, #e8f0fe 0%, #d4e4fc 100%);
   border-color: #7cb3f5;
   color: #1a56db;
   box-shadow:
     0 6px 16px rgba(59, 130, 246, 0.2),
     0 3px 8px rgba(0, 0, 0, 0.1);
   transform: translateX(4px);
 }

 /* 响应式设计 */
@media (max-width: 768px) {
  .diabetes-chat-container {
    width: 100%;
    margin: 0;
    border-radius: 0;
  }

  .additional-features {
    padding: 0 16px;
  }

  .feature-card {
    padding: 16px;
  }

  .checkbox-group {
    flex-direction: column;
    align-items: flex-start;
  }

  .input-area {
    flex-direction: column;
    align-items: stretch;
  }

  .model-setting,
  .model-display {
    margin-right: 0;
    margin-bottom: 8px;
    justify-content: center;
  }

  .quick-questions-panel {
    padding: 12px;
  }

  .floating-side-panel {
    display: none;
  }
}
</style>