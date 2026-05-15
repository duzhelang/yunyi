<template>
  <div class="chat-wrapper">
    <div class="diabetes-chat-container">
      <!-- 聊天头部 -->
      <div class="chat-header">
        <el-icon :size="24" class="doctor-icon"><ChatDotRound /></el-icon>
        <h3>糖尿病健康咨询助手</h3>
        <div class="status">
          <button
            class="mute-btn"
            @click="toggleMute"
            :title="isMuted ? '取消静音' : '静音'"
          >
            <svg v-if="!isMuted" viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <path d="M3 9v6h4l5 5V4L7 9H3zm13.5 3c0-1.77-1.02-3.29-2.5-4.03v8.05c1.48-.73 2.5-2.25 2.5-4.02zM14 3.23v2.06c2.89.86 5 3.54 5 6.71s-2.11 5.85-5 6.71v2.06c4.01-.91 7-4.49 7-8.77s-2.99-7.86-7-8.77z"/>
            </svg>
            <svg v-else viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <path d="M3 9v6h4l5 5V4L7 9H3zm13.5 3c0-1.77-1.02-3.29-2.5-4.03v8.05c1.48-.73 2.5-2.25 2.5-4.02zM14 3.23v2.06c2.89.86 5 3.54 5 6.71s-2.11 5.85-5 6.71v2.06c4.01-.91 7-4.49 7-8.77s-2.99-7.86-7-8.77z"/>
              <line x1="17" y1="9" x2="21" y2="15" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
              <line x1="21" y1="9" x2="17" y2="15" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
            </svg>
          </button>
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
            <button
              v-if="!msg.isUser"
              class="speak-btn"
              :class="{ speaking: msg.isSpeaking }"
              @click="toggleSpeak(msg, index)"
              :title="msg.isSpeaking ? '停止朗读' : '朗读'"
            >
              <svg v-if="!msg.isSpeaking" viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                <path d="M3 9v6h4l5 5V4L7 9H3zm13.5 3A4.5 4.5 0 0 0 14 8.5v7a4.49 4.49 0 0 0 2.5-3.5zM14 3.23v2.06a7.007 7.007 0 0 1 0 13.42v2.06A9.01 9.01 0 0 0 14 3.23z"/>
              </svg>
              <svg v-else viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                <path d="M3 9v6h4l5 5V4L7 9H3zm13.5 3A4.5 4.5 0 0 0 14 8.5v7a4.49 4.49 0 0 0 2.5-3.5zM14 3.23v2.06a7.007 7.007 0 0 1 0 13.42v2.06A9.01 9.01 0 0 0 14 3.23z"/>
                <rect x="17" y="7" width="2" height="10" rx="1" fill="#ef4444"/>
                <rect x="20" y="5" width="2" height="14" rx="1" fill="#ef4444"/>
              </svg>
            </button>
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
              <el-option label="MiMo-OMNI" value="mimo-v2-omni" />
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

      <!-- AI辅助工具 -->
      <div class="additional-features">
        <h2>📋 AI辅助工具</h2>
        <div class="feature-card card-plan">
          <div class="plan-header-row">
            <h3><span class="card-dot card-dot-green"></span>健康计划生成</h3>
            <button v-if="healthPlan" class="collapse-btn" @click="planCollapsed = !planCollapsed">
              {{ planCollapsed ? '展开计划 ▾' : '收起全部 ▴' }}
            </button>
          </div>
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
              <button @click="generateHealthPlan" :disabled="!riskLevel || isLoading" class="feature-btn feature-btn-green">
                📋 生成健康计划
              </button>
            </div>
            <div class="health-plan" v-if="healthPlan" v-show="!planCollapsed">
              <div v-for="(day, index) in healthPlan" :key="index" class="plan-day" :class="'plan-day-' + (index % 3)">
                <div class="day-header" @click="toggleDay(index)">
                  <span class="day-title"><span class="day-icon">{{ ['🌅','☀️','🌙','🍃','💧','🌸','⭐'][index] }}</span> 第{{ index + 1 }}天</span>
                  <span class="day-toggle">{{ collapsedDays.includes(index) ? '▾' : '▴' }}</span>
                </div>
                <div class="plan-content" v-show="!collapsedDays.includes(index)">
                  <div class="plan-item">
                    <span class="plan-label">🍽️ 饮食建议</span>
                    <span class="plan-value">{{ day.diet }}</span>
                  </div>
                  <div class="plan-item">
                    <span class="plan-label">🏃 运动建议</span>
                    <span class="plan-value">{{ day.exercise }}</span>
                  </div>
                  <div class="plan-item">
                    <span class="plan-label">⚠️ 注意事项</span>
                    <span class="plan-value">{{ day.notes }}</span>
                  </div>
                </div>
              </div>
            </div>
            <button v-if="healthPlan" @click="savePlanToRecord" class="feature-btn feature-btn-outline-green" style="margin-top: 12px;">
              💾 保存到诊疗档案
            </button>
          </div>
        </div>

        <div class="feature-card card-report">
          <h3><span class="card-dot card-dot-blue"></span>报告解读</h3>
          <div class="feature-content">
            <textarea
                v-model="reportJson"
                placeholder="请粘贴预测结果的JSON内容..."
                rows="4"
                class="report-textarea"
            ></textarea>
            <button @click="interpretReport" :disabled="!reportJson || isLoading" class="feature-btn feature-btn-blue">
              📋 解读报告
            </button>
            <div class="report-result" v-if="reportResult">
              <div class="report-result-header">
                <span class="report-result-icon">📊</span>
                <span>解读结果</span>
              </div>
              <div class="report-result-content">{{ reportResult }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧悬浮滚动问题面板 -->
    <div v-if="messages.length > 0 || showRecipePanel"
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

    <!-- 控糖食谱侧边栏 -->
    <div class="recipe-sidebar" :class="{ expanded: showRecipePanel, 'has-side-panel': messages.length > 0 || showRecipePanel }">
      <div class="recipe-toggle" @click="showRecipePanel = !showRecipePanel">
        <span class="recipe-toggle-icon">🍎</span>
        <span class="recipe-toggle-text">控糖食谱</span>
        <span class="recipe-toggle-arrow">
          <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
            <path d="M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.41z"/>
          </svg>
        </span>
      </div>
      <div class="recipe-body" v-if="showRecipePanel">
        <template v-if="recipeData">
          <h4>🍎 今日控糖食谱</h4>
          <div class="recipe-text" v-html="formatRecipe(recipeData)" @click="showRecipeDialog = true" style="cursor:pointer" title="点击查看大图"></div>
          <button class="recipe-back-btn" @click="recipeData = null">← 返回重新生成</button>
        </template>
        <template v-else>
          <h4>食谱偏好设置</h4>
          <div class="recipe-prefs">
            <div class="form-item">
              <label>饮食偏好</label>
              <div class="checkbox-group">
                <label v-for="pref in dietPrefs" :key="pref">
                  <input type="checkbox" :value="pref" v-model="selectedDietPrefs" />
                  {{ pref }}
                </label>
              </div>
            </div>
            <div class="form-item">
              <label>餐次</label>
              <select v-model="mealCount" class="plan-select">
                <option value="3">一日三餐</option>
                <option value="5">一日五餐（加餐）</option>
              </select>
            </div>
            <div class="form-item">
              <label>口味偏好</label>
              <select v-model="tastePref" class="plan-select">
                <option value="清淡">清淡</option>
                <option value="适中">适中</option>
                <option value="重口">重口</option>
              </select>
            </div>
          </div>
          <button @click="generateRecipe(true)" :disabled="isLoading || isRecipeLoading" class="recipe-gen-btn">
            <span v-if="!isLoading && !isRecipeLoading">🍳 生成控糖食谱</span>
            <span v-else-if="isRecipeLoading && !isLoading">⏳ 生成中...</span>
            <i class="el-icon-loading" v-else></i>
          </button>
        </template>
      </div>
    </div>

    <el-dialog v-model="showRecipeDialog" title="🍎 今日控糖食谱" width="680px" :close-on-click-modal="true" class="recipe-dialog">
      <div class="recipe-dialog-body">
        <div class="recipe-text recipe-dialog-text" v-if="recipeData" v-html="formatRecipe(recipeData)"></div>
      </div>
    </el-dialog>
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
      riskLevel: '',
      abnormalIndicators: [],
      healthPlan: null,
      planCollapsed: false,
      collapsedDays: [],
      indicators: ['血糖', 'BMI', '血压', '胰岛素'],
      // 控糖食谱
      showRecipePanel: false,
      recipeData: null,
      isRecipeLoading: false,
      showRecipeDialog: false,
      reportResult: '',
      dietPrefs: ['低GI', '高纤维', '低碳水', '无糖', '素食', '高蛋白'],
      selectedDietPrefs: [],
      mealCount: '3',
      tastePref: '清淡',
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
      isMuted: false,
      _currentAudio: null,
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
    },
    showRecipePanel(val) {
      if (val) {
        this.$nextTick(() => {
          this.startMarquee();
        });
      } else if (this.marqueeAnimId) {
        cancelAnimationFrame(this.marqueeAnimId);
        this.marqueeAnimId = null;
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
              // 输出完成后自动朗读
              if (!this.isMuted) {
                this.$nextTick(() => this.speak(botMsgIndex))
              }
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
    async interpretReport() {
      const prompt = `请作为糖尿病专科医生，对以下检测报告进行专业解读分析。要求：1.指出关键异常指标及其含义；2.评估糖尿病风险等级；3.给出具体的饮食、运动、监测建议。请用简洁的中文回答，控制在200字以内。\n\n报告内容：${this.reportJson}`
      this.addUserMessage('请解读我的检测报告')
      this.isLoading = true
      try {
        const response = await request.post('/api/diabetes/chat', null, {
          params: { question: prompt, provider: this.selectedModel }
        })
        if (response && response.code === '200') {
          const result = response.data || '暂无有效回答'
          this.reportResult = result
          this.typeBotMessage(result)
          ElMessage.success('报告解读完成')
        } else {
          this.addBotMessage(`服务提示:${response?.msg || '未知错误'}`)
        }
      } catch (error) {
        this.addBotMessage('网络连接失败，请稍后重试')
      } finally {
        this.isLoading = false
      }
    },
    toggleDay(index) {
      const i = this.collapsedDays.indexOf(index)
      if (i === -1) this.collapsedDays.push(index)
      else this.collapsedDays.splice(i, 1)
    },
    formatRecipe(data) {
      if (!data || !data.meals) return ''
      let html = ''
      for (const meal of data.meals) {
        html += `<div class="recipe-section">`
        html += `<div class="recipe-meal">${meal.icon} ${meal.label}</div>`
        html += `<div class="recipe-detail">`
        for (const food of meal.foods) {
          html += `<div class="recipe-food-row">`
          html += `<span class="food-name">${food.name}</span>`
          html += `<span class="food-portion">${food.portion}</span>`
          html += `<span class="food-cal">${food.cal}</span>`
          html += `</div>`
        }
        html += `</div>`
        html += `<div class="recipe-summary"><span>📊 ${meal.gi}</span><span>🔥 ${meal.totalCal}</span></div>`
        html += `</div>`
      }
      return html
    },
    getMockRecipe() {
      return {
        meals: [
          {
            label: '早餐', icon: '🌅', gi: 'GI≈48', totalCal: '总热量≈460kcal',
            foods: [
              { name: '全麦面包', portion: '2片', cal: '约160kcal' },
              { name: '水煮鸡蛋', portion: '1个', cal: '约70kcal' },
              { name: '无糖豆浆', portion: '200ml', cal: '约60kcal' },
              { name: '凉拌黄瓜', portion: '100g', cal: '约30kcal' },
              { name: '小番茄', portion: '5颗', cal: '约40kcal' },
              { name: '脱脂牛奶', portion: '100ml', cal: '约35kcal' }
            ]
          },
          {
            label: '午餐', icon: '☀️', gi: 'GI≈52', totalCal: '总热量≈520kcal',
            foods: [
              { name: '杂粮饭', portion: '100g', cal: '约130kcal' },
              { name: '清蒸鲈鱼', portion: '120g', cal: '约120kcal' },
              { name: '蒜蓉西兰花', portion: '150g', cal: '约55kcal' },
              { name: '凉拌木耳', portion: '80g', cal: '约35kcal' },
              { name: '豆腐汤', portion: '1碗', cal: '约60kcal' },
              { name: '杂粮饭搭配', portion: '荞麦馒头半个', cal: '约50kcal' }
            ]
          },
          {
            label: '晚餐', icon: '🌆', gi: 'GI≈50', totalCal: '总热量≈420kcal',
            foods: [
              { name: '荞麦面条', portion: '80g', cal: '约110kcal' },
              { name: '鸡胸肉炒青椒', portion: '120g', cal: '约130kcal' },
              { name: '清炒生菜', portion: '150g', cal: '约30kcal' },
              { name: '凉拌海带丝', portion: '80g', cal: '约20kcal' },
              { name: '番茄蛋花汤', portion: '1碗', cal: '约50kcal' },
              { name: '蒸南瓜', portion: '100g', cal: '约22kcal' }
            ]
          }
        ]
      }
    },
    getMockPlan(level) {
      const mockData = {
        low: [
          { diet: '早餐：全麦面包2片+水煮蛋1个+无糖豆浆200ml（GI≈45，约320kcal）', exercise: '快走30分钟（约150kcal）', notes: '餐后1小时运动，保持血糖稳定' },
          { diet: '午餐：杂粮饭100g+清蒸鱼100g+蒜蓉西兰花150g（GI≈48，约420kcal）', exercise: '瑜伽20分钟', notes: '午餐七分饱，细嚼慢咽' },
          { diet: '晚餐：荞麦面80g+鸡胸肉炒青椒+凉拌黄瓜（GI≈46，约380kcal）', exercise: '散步30分钟', notes: '晚餐宜早，睡前3小时不进食' },
          { diet: '早餐：燕麦片30g+脱脂牛奶200ml+煮鸡蛋1个（GI≈42，约290kcal）', exercise: '晨间拉伸15分钟', notes: '燕麦选择整粒燕麦，避免即食型' },
          { diet: '午餐：糙米饭100g+虾仁豆腐+清炒菠菜（GI≈44，约400kcal）', exercise: '游泳30分钟', notes: '多摄入优质蛋白和膳食纤维' },
          { diet: '晚餐：玉米1根+番茄炒蛋+凉拌海带丝（GI≈50，约350kcal）', exercise: '太极拳20分钟', notes: '主食粗细搭配，每餐有蔬菜' },
          { diet: '早餐：全麦三明治+无糖酸奶150g+苹果半个（GI≈43，约310kcal）', exercise: '骑行30分钟', notes: '保持规律作息，定期监测空腹血糖' }
        ],
        medium: [
          { diet: '早餐：荞麦馒头1个+水煮蛋1个+无糖豆浆200ml（GI≈48，约300kcal）', exercise: '快走40分钟', notes: '严格控制精制碳水摄入' },
          { diet: '午餐：糙米饭80g+清蒸鲈鱼100g+炒苦瓜150g（GI≈46，约380kcal）', exercise: '力量训练20分钟', notes: '苦瓜有助于辅助控糖' },
          { diet: '晚餐：藜麦粥+凉拌鸡丝+炒生菜（GI≈42，约340kcal）', exercise: '散步40分钟', notes: '晚餐减少主食量，增加蔬菜比例' },
          { diet: '早餐：山药100g+煮鸡蛋1个+无糖豆浆（GI≈45，约270kcal）', exercise: '八段锦15分钟', notes: '山药代替部分主食，控制总量' },
          { diet: '午餐：杂粮饭80g+蒜蓉虾+清炒油麦菜（GI≈44，约370kcal）', exercise: '慢跑20分钟', notes: '每周至少运动5天，每次30分钟以上' },
          { diet: '晚餐：南瓜小米粥+凉拌豆腐+炒青菜（GI≈48，约310kcal）', exercise: '瑜伽25分钟', notes: '保持心情舒畅，避免情绪波动影响血糖' },
          { diet: '早餐：全麦吐司2片+无糖酸奶+小番茄（GI≈44，约290kcal）', exercise: '游泳25分钟', notes: '坚持记录饮食日记和血糖值' }
        ],
        high: [
          { diet: '早餐：燕麦片25g+脱脂牛奶200ml+水煮蛋蛋白2个（GI≈40，约240kcal）', exercise: '遵医嘱适度活动', notes: '严格遵医嘱用药，勿自行调整' },
          { diet: '午餐：杂粮饭60g+清蒸鱼80g+大量绿叶蔬菜（GI≈44，约330kcal）', exercise: '饭后散步15分钟', notes: '严格控制总热量摄入' },
          { diet: '晚餐：荞麦面50g+鸡胸肉50g+清炒苦瓜（GI≈43，约280kcal）', exercise: '床上伸展运动10分钟', notes: '监测餐后2小时血糖，记录异常波动' },
          { diet: '早餐：蒸南瓜150g+煮鸡蛋1个+无糖豆浆200ml（GI≈46，约260kcal）', exercise: '室内慢走15分钟', notes: '出现头晕眼花等症状立即测血糖' },
          { diet: '午餐：糙米饭60g+蒜蓉虾仁60g+炒西兰花200g（GI≈43，约310kcal）', exercise: '坐姿上肢运动15分钟', notes: '保持充足睡眠，避免熬夜' },
          { diet: '晚餐：小米粥150ml+蒸豆腐+炒青菜（GI≈47，约250kcal）', exercise: '深呼吸放松训练10分钟', notes: '每周至少复诊一次，及时调整方案' },
          { diet: '早餐：全麦面包1片+无糖酸奶150g+黄瓜半根（GI≈42，约220kcal）', exercise: '散步15分钟（有人陪同）', notes: '随身携带糖果或饼干，防止低血糖' }
        ]
      }
      return mockData[level] || mockData.low
    },
    async generateHealthPlan() {
      const levelMap = { low: '低风险', medium: '中风险', high: '高风险' }
      const indicators = this.abnormalIndicators.length > 0 ? this.abnormalIndicators.join('、') : '无特殊异常'
      const planPrompt = `请作为糖尿病专科医生，根据以下信息生成一周健康计划。要求严格按JSON格式返回一个数组，每个元素包含diet(饮食建议)、exercise(运动建议)、notes(注意事项)三个字段，共7天。只返回JSON数组，不要其他文字。\n\n风险等级：${levelMap[this.riskLevel]}\n异常指标：${indicators}`
      this.addUserMessage(`请问我要注意什么（${levelMap[this.riskLevel]}，异常指标：${indicators}）`)
      this.isLoading = true
      let usedMock = false
      try {
        const response = await request.post('/api/diabetes/chat', null, {
          params: { question: planPrompt, provider: this.selectedModel }
        })
        if (response && response.code === '200') {
          const reply = response.data || ''
          let planData = null
          try {
            const jsonMatch = reply.match(/\[[\s\S]*\]/)
            if (jsonMatch) planData = JSON.parse(jsonMatch[0])
          } catch (e) { /* parse failed */ }
          if (planData && Array.isArray(planData) && planData.length >= 5) {
            this.healthPlan = planData.slice(0, 7)
          } else {
            this.healthPlan = this.getMockPlan(this.riskLevel)
            usedMock = true
          }
        } else {
          this.healthPlan = this.getMockPlan(this.riskLevel)
          usedMock = true
        }
      } catch (error) {
        this.healthPlan = this.getMockPlan(this.riskLevel)
        usedMock = true
      }
      this.planCollapsed = false
      this.collapsedDays = []
      if (usedMock) {
        this.typeBotMessage('已为您生成标准健康计划，请查看下方卡片了解详情')
        ElMessage.info('AI生成异常，已使用标准方案')
      } else {
        try {
          const summaryPrompt = `请作为糖尿病专科医生，针对以下一周健康计划，用3-5句话总结核心要点和特别注意事项。只说重点，不要逐天罗列，用简洁中文回答。\n${JSON.stringify(this.healthPlan)}`
          const summaryRes = await request.post('/api/diabetes/chat', null, {
            params: { question: summaryPrompt, provider: this.selectedModel }
          })
          if (summaryRes && summaryRes.code === '200') {
            this.typeBotMessage(summaryRes.data || '健康计划已生成，请查看下方卡片')
          } else {
            this.typeBotMessage('健康计划已生成，请查看下方卡片了解详情')
          }
        } catch (e) {
          this.typeBotMessage('健康计划已生成，请查看下方卡片了解详情')
        }
        ElMessage.success('健康计划已生成')
      }
      this.showRecipePanel = true
      this.isLoading = false
      this.generateRecipe(true)
    },
    async generateRecipe(fromSidebar) {
      this.recipeData = this.getMockRecipe()
      this.isRecipeLoading = true
      const prefs = this.selectedDietPrefs.length > 0 ? this.selectedDietPrefs.join('、') : '低GI、高纤维'
      const meals = this.mealCount === '5' ? '一日五餐（含上午加餐和下午加餐）' : '一日三餐'
      const prompt = `请作为糖尿病营养师生成一天的控糖食谱。严格按以下格式返回，每行一道菜用"|"分隔菜品名、分量、热量：
          【早餐】
          菜品名 | 分量 | 热量kcal
          （每道菜一行，继续列出）
          每餐小计：GI≈XX | 总热量≈XXXkcal
          【午餐】
          （同上格式）
          【晚餐】
          （同上格式）
          饮食偏好：${prefs}
          餐次安排：${meals}
          口味偏好：${this.tastePref}`
      try {
        const response = await request.post('/api/diabetes/chat', null, {
          params: { question: prompt, provider: this.selectedModel }
        })
        if (response && response.code === '200' && response.data) {
          const parsed = this.parseRecipeText(response.data)
          if (parsed && parsed.meals && parsed.meals.length >= 2) {
            this.recipeData = parsed
            ElMessage.success('控糖食谱已生成')
          } else {
            ElMessage.info('已使用标准食谱方案')
          }
        } else {
          ElMessage.info('已使用标准食谱方案')
        }
      } catch (error) {
        ElMessage.info('已使用标准食谱方案')
      } finally {
        this.isRecipeLoading = false
      }
      this.$nextTick(() => this.saveRecipeToRecord())
    },
    parseRecipeText(text) {
      const result = { meals: [] }
      const lines = text.split('\n')
      let curMeal = null
      for (const line of lines) {
        const t = line.trim()
        if (!t) continue
        const mealMatch = t.match(/【(.+?)】|^([早午晚]餐|加餐|上午|下午|晚间)/)
        if (mealMatch) {
          if (curMeal && curMeal.foods.length > 0) result.meals.push(curMeal)
          const label = mealMatch[1] || mealMatch[2]
          let icon = '🍽️'
          if (/早/.test(label)) icon = '🌅'
          else if (/午/.test(label)) icon = '☀️'
          else if (/晚/.test(label)) icon = '🌆'
          else if (/加餐|上午|下午|晚间/.test(label)) icon = '🍎'
          curMeal = { label, icon, gi: 'GI≈--', totalCal: '总热量≈--kcal', foods: [] }
          continue
        }
        if (!curMeal) continue
        if (/每餐小计|小计|GI|总热量|全天总/.test(t)) {
          const parts = t.split('|').map(s => s.trim()).filter(Boolean)
          if (parts.length >= 2) {
            curMeal.gi = parts[0].replace(/每餐小计[：:]?\s*/, '')
            curMeal.totalCal = parts[1]
          }
          result.meals.push(curMeal)
          curMeal = null
          continue
        }
        if (t.includes('|')) {
          const parts = t.split('|').map(s => s.trim()).filter(Boolean)
          if (parts.length >= 3) {
            curMeal.foods.push({ name: parts[0], portion: parts[1], cal: parts[2] })
          } else if (parts.length === 2) {
            curMeal.foods.push({ name: parts[0], portion: parts[1], cal: '' })
          }
        }
      }
      if (curMeal && curMeal.foods.length > 0) result.meals.push(curMeal)
      return result
    },
    async callChatApi(prompt, successMsg) {
      this.isLoading = true
      try {
        const response = await request.post('/api/diabetes/chat', null, {
          params: { question: prompt, provider: this.selectedModel }
        })
        if (response && response.code === '200') {
          this.typeBotMessage(response.data || '暂无有效回答')
          ElMessage.success(successMsg)
        } else {
          this.addBotMessage(`服务提示:${response?.msg || '未知错误'}`)
        }
      } catch (error) {
        this.addBotMessage('网络连接失败，请稍后重试')
      } finally {
        this.isLoading = false
      }
    },
    addUserMessage(content) {
      this.messages.push({
        isUser: true,
        content,
        displayContent: content,
        timestamp: new Date()
      })
      this.scrollToBottom()
    },
    typeBotMessage(content) {
      const botMsgIndex = this.messages.push({
        isUser: false,
        content,
        displayContent: '',
        isTyping: true,
        timestamp: new Date()
      }) - 1

      if (this.typingTimer) clearInterval(this.typingTimer)

      let currentCharIndex = 0
      this.typingTimer = setInterval(() => {
        if (currentCharIndex < content.length) {
          this.messages[botMsgIndex].displayContent += content[currentCharIndex]
          currentCharIndex++
          this.scrollToBottom()
        } else {
          clearInterval(this.typingTimer)
          this.messages[botMsgIndex].isTyping = false
          this.typingTimer = null
          // 输出完成后自动朗读
          if (!this.isMuted) {
            this.$nextTick(() => this.speak(botMsgIndex))
          }
        }
      }, 30)
    },
    askSample(sampleQ) {
      this.question = sampleQ;
      this.sendMessage();
    },
    toggleSpeak(msg, index) {
      if (msg.isSpeaking) {
        this.stopSpeaking()
      } else {
        this.speak(index)
      }
    },
    async speak(index) {
      // 停止当前正在播放的音频
      this.stopSpeaking()

      const msg = this.messages[index]
      if (!msg || msg.isUser) return

      msg.isSpeaking = true

      try {
        const response = await request.post('/api/diabetes/tts', { text: msg.content })

        if (response && response.code === '200' && response.data) {
          const audioData = response.data
          const audioBlob = this.base64ToBlob(audioData, 'audio/wav')
          const audioUrl = URL.createObjectURL(audioBlob)

          this._currentAudio = new Audio(audioUrl)
          this._currentAudio.onended = () => {
            msg.isSpeaking = false
            URL.revokeObjectURL(audioUrl)
            this._currentAudio = null
          }
          this._currentAudio.onerror = () => {
            msg.isSpeaking = false
            URL.revokeObjectURL(audioUrl)
            this._currentAudio = null
          }
          await this._currentAudio.play()
        } else {
          msg.isSpeaking = false
          console.error('TTS合成失败:', response?.msg)
        }
      } catch (error) {
        msg.isSpeaking = false
        console.error('TTS请求异常:', error)
      }
    },
    base64ToBlob(base64, mimeType) {
      const byteCharacters = atob(base64)
      const byteNumbers = new Array(byteCharacters.length)
      for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i)
      }
      const byteArray = new Uint8Array(byteNumbers)
      return new Blob([byteArray], { type: mimeType })
    },
    stopSpeaking() {
      if (this._currentAudio) {
        this._currentAudio.pause()
        this._currentAudio.currentTime = 0
        this._currentAudio = null
      }
      this.messages.forEach(m => {
        if (!m.isUser) m.isSpeaking = false
      })
    },
    toggleMute() {
      this.isMuted = !this.isMuted
      if (this.isMuted) {
        this.stopSpeaking()
      }
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
    },
    async savePlanToRecord() {
      if (!this.healthPlan || this.healthPlan.length === 0) {
        ElMessage.warning('暂无健康计划可保存')
        return
      }
      try {
        const planText = this.healthPlan.map((day, i) =>
          `第${i + 1}天:\n  饮食: ${day.diet}\n  运动: ${day.exercise}\n  注意: ${day.notes}`
        ).join('\n\n')
        const summary = `基于风险等级(${this.riskLevel === 'high' ? '高' : this.riskLevel === 'medium' ? '中' : '低'})生成的${this.healthPlan.length}天健康计划`
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
    },
    formatRecipeForSave(recipe) {
      if (!recipe || !recipe.meals) return ''
      return recipe.meals.map(meal => {
        const foods = meal.foods.map(f => `${f.name} ${f.portion} ${f.cal}`).join(' · ')
        return `${meal.icon} ${meal.label}\n  ${foods}\n  📊 ${meal.gi} · 🔥 ${meal.totalCal}`
      }).join('\n\n')
    },
    async saveRecipeToRecord() {
      if (!this.recipeData || !this.recipeData.meals) return
      try {
        const recipeText = this.formatRecipeForSave(this.recipeData)
        const mealCount = this.recipeData.meals.length
        const prefs = this.selectedDietPrefs.length > 0 ? this.selectedDietPrefs.join('、') : '低GI、高纤维'
        const payload = {
          recordType: 'ai_plan',
          recordDate: new Date().toISOString().replace('T', ' ').substring(0, 19),
          diagnosis: `控糖食谱 - ${mealCount}餐${this.tastePref}饮食方案（${prefs}）`,
          treatmentPlan: recipeText
        }
        await request.post('/api/patient-visit', payload)
      } catch (e) {
        console.error('自动保存食谱失败', e)
      }
    }
  },
  beforeDestroy() {
    this.stopSpeaking()
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
  position: relative;
  min-height: 600px;
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

/* 朗读按钮 */
.speak-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border: 1px solid #d0d7e2;
  border-radius: 50%;
  background: white;
  cursor: pointer;
  color: #64748b;
  transition: all 0.2s;
  opacity: 0.6;
  margin-top: 4px;
}
.speak-btn:hover {
  opacity: 1;
  color: #4a90e2;
  border-color: #4a90e2;
  background: #f0f7ff;
}
.speak-btn.speaking {
  opacity: 1;
  color: #ef4444;
  border-color: #ef4444;
  background: #fef2f2;
  animation: speakPulse 1.2s infinite;
}

/* 静音按钮 */
.mute-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 50%;
  background: transparent;
  cursor: pointer;
  color: #64748b;
  transition: all 0.2s;
  margin-right: 8px;
}
.mute-btn:hover {
  background: #f1f5f9;
  color: #4a90e2;
}
.mute-btn svg {
  display: block;
}
@keyframes speakPulse {
  0%, 100% { box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.3); }
  50% { box-shadow: 0 0 0 6px rgba(239, 68, 68, 0); }
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
  border-radius: 14px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  padding: 24px;
  margin-bottom: 24px;
  transition: transform 0.2s, box-shadow 0.2s;
  border: 1px solid #eef2f6;
  position: relative;
  overflow: hidden;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 3px;
}

.feature-card::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 80%, rgba(255, 255, 255, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.2) 0%, transparent 50%);
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.feature-card:hover::after {
  opacity: 1;
}

.feature-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.card-plan {
  background: linear-gradient(135deg, #c3eacf 0%, #d6efe3 50%, #c4dae8 100%);
  border: 1px solid #d1fae5;
}

.card-plan::before {
  background: linear-gradient(90deg, #10b981, #34d399);
}

.card-report {
  background: linear-gradient(135deg, #eff6ff 0%, #e0f2fe 50%, #f5f3ff 100%);
  border: 1px solid #bfdbfe;
}

.card-report::before {
  background: linear-gradient(90deg, #3b82f6, #60a5fa);
}

.feature-card h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 16px;
  border-bottom: 1px solid #eef2f6;
  padding-bottom: 14px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.card-dot-blue {
  background: #4a90e2;
}

.card-dot-green {
  background: #10b981;
}

.feature-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.report-textarea {
  width: 100%;
  padding: 14px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  resize: vertical;
  outline: none;
  background: #fafbfc;
  transition: border-color 0.2s, box-shadow 0.2s, background 0.2s;
}

.report-textarea:focus {
  border-color: #4a90e2;
  box-shadow: 0 0 0 3px rgba(74, 144, 226, 0.12);
  background: white;
}

.feature-btn {
  align-self: flex-start;
  padding: 10px 24px;
  border-radius: 20px;
  background: linear-gradient(135deg, #4a90e2 0%, #357abd 100%);
  color: white;
  border: none;
  font-weight: 500;
  cursor: pointer;
  font-size: 14px;
  transition: box-shadow 0.2s, transform 0.15s, opacity 0.2s;
  box-shadow: 0 3px 10px rgba(74, 144, 226, 0.2);
}

.feature-btn:hover:not(:disabled) {
  box-shadow: 0 5px 16px rgba(74, 144, 226, 0.3);
  transform: translateY(-1px);
}

.feature-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  box-shadow: none;
}

/* 计划头部行 + 折叠按钮 */
.plan-header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e2e8f0;
}

.plan-header-row h3 {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.collapse-btn {
  font-size: 12px;
  color: #4a90e2;
  background: #f0f7ff;
  border: 1px solid #d0e3f7;
  border-radius: 14px;
  padding: 4px 12px;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.collapse-btn:hover {
  background: #dbeafe;
  border-color: #4a90e2;
}

/* 控糖食谱侧边栏 */
.recipe-sidebar {
  position: absolute;
  right: 100px;
  top: 700px;
  z-index: 100;
  width: 48px;
  overflow: visible;
  transition: right 0.35s cubic-bezier(0.4, 0, 0.2, 1),
              filter 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  will-change: right;
}

.recipe-sidebar.has-side-panel {
  right: 250px;
}

.recipe-sidebar .recipe-toggle {
  border-radius: 0 12px 12px 0;
}

.recipe-sidebar.expanded {
  filter: drop-shadow(0 4px 14px rgba(230, 126, 34, 0.15));
}

.recipe-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 48px;
  height: 180px;
  padding: 12px 0;
  background: linear-gradient(135deg, #ff9a56 0%, #e67e22 100%);
  color: white;
  border-radius: 12px 0 0 12px;
  cursor: pointer;
  box-shadow: -2px 3px 10px rgba(230, 126, 34, 0.25),
              0 1px 3px rgba(0, 0, 0, 0.08);
  transition: border-radius 0.3s cubic-bezier(0.4, 0, 0.2, 1),
              box-shadow 0.3s cubic-bezier(0.4, 0, 0.2, 1),
              transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  user-select: none;
  flex-direction: column;
}

.recipe-toggle:hover {
  box-shadow: -4px 6px 16px rgba(230, 126, 34, 0.3),
              0 2px 6px rgba(0, 0, 0, 0.1);
  transform: translateX(-2px);
}

.recipe-sidebar.expanded .recipe-toggle:hover {
  transform: none;
}

.recipe-sidebar.expanded .recipe-toggle {
  flex-direction: row;
  height: auto;
  width: 100%;
  padding: 14px 16px;
  justify-content: flex-start;
  border-radius: 12px 0 0 0;
  gap: 10px;
  box-shadow: none;
}

.recipe-toggle-icon {
  font-size: 22px;
  flex-shrink: 0;
}

.recipe-toggle-text {
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 1px;
  writing-mode: vertical-rl;
  text-orientation: mixed;
  white-space: nowrap;
  transition: writing-mode 0.3s ease;
}

.recipe-sidebar.expanded .recipe-toggle-text {
  writing-mode: horizontal-tb;
  text-orientation: initial;
}

.recipe-toggle-arrow {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 8px;
}

.recipe-sidebar.expanded .recipe-toggle-arrow {
  margin-top: 0;
  margin-left: auto;
}

.recipe-toggle-arrow svg {
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.recipe-sidebar.expanded .recipe-toggle-arrow svg {
  transform: rotate(180deg);
}

.recipe-body {
  position: absolute;
  top: 0;
  left: calc(100% + 8px);
  background: white;
  border-radius: 0 12px 12px 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08),
              0 1px 4px rgba(0, 0, 0, 0.04);
  padding: 20px;
  border: 1px solid #fde2c8;
  width: 280px;
  max-height: 520px;
  overflow-y: auto;
  animation: slideInRight 0.25s ease;
}

.recipe-body::-webkit-scrollbar,
.marquee-viewport::-webkit-scrollbar {
  width: 4px;
}
.recipe-body::-webkit-scrollbar-track,
.marquee-viewport::-webkit-scrollbar-track {
  background: transparent;
}
.recipe-body::-webkit-scrollbar-thumb,
.marquee-viewport::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 4px;
}
.recipe-body::-webkit-scrollbar-thumb:hover,
.marquee-viewport::-webkit-scrollbar-thumb:hover {
  background: #ccc;
}

@keyframes slideInRight {
  from { opacity: 0; transform: translateX(-12px); }
  to { opacity: 1; transform: translateX(0); }
}

.recipe-body h4 {
  font-size: 15px;
  font-weight: 600;
  color: #e67e22;
  margin-bottom: 14px;
  padding-bottom: 10px;
  border-bottom: 2px solid #fde2c8;
}

.recipe-back-btn {
  width: 100%;
  margin-top: 12px;
  padding: 8px 0;
  border: 1px solid #fde2c8;
  border-radius: 8px;
  background: linear-gradient(135deg, #fff7ed, #fffbeb);
  color: #e67e22;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.recipe-back-btn:hover {
  background: linear-gradient(135deg, #ffedd5, #fef3c7);
  border-color: #e67e22;
  transform: translateY(-1px);
}

.recipe-prefs {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 14px;
}

.recipe-prefs .form-item label {
  font-size: 13px;
  font-weight: 500;
  color: #475569;
}

.recipe-hint {
  font-size: 12px;
  color: #94a3b8;
  text-align: center;
  margin: 0;
  padding-top: 8px;
  border-top: 1px dashed #e2e8f0;
}

.recipe-gen-btn {
  display: block;
  width: 100%;
  padding: 10px 16px;
  background: linear-gradient(135deg, #ff9a56 0%, #e67e22 100%);
  color: white;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: box-shadow 0.25s ease, transform 0.2s ease, opacity 0.2s ease;
  box-shadow: 0 3px 10px rgba(230, 126, 34, 0.25);
  margin-top: 12px;
}

.recipe-gen-btn:hover:not(:disabled) {
  box-shadow: 0 5px 16px rgba(230, 126, 34, 0.35);
  transform: translateY(-1px);
}

.recipe-gen-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  box-shadow: none;
}

.recipe-text {
  font-size: 14px;
  line-height: 1.8;
  color: #475569;
  overflow-y: auto;
  padding-right: 4px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

:deep(.recipe-text .recipe-section) {
  background: linear-gradient(135deg, #fffbeb 0%, #fff7ed 100%);
  border: 1px solid #fde2c8;
  border-radius: 10px;
  padding: 14px;
  margin-bottom: 8px;
}

:deep(.recipe-text .recipe-meal) {
  font-weight: 600;
  color: #e67e22;
  font-size: 15px;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px solid #fde2c8;
  letter-spacing: 0.5px;
}

:deep(.recipe-text .recipe-detail) {
  font-size: 13px;
  color: #57534e;
  line-height: 1.8;
  padding-left: 0;
}

:deep(.recipe-text .recipe-food-row) {
  display: flex;
  align-items: center;
  padding: 5px 6px;
  border-bottom: 1px solid #d4c4a8;
  font-size: 13px;
  line-height: 1.6;
}

:deep(.recipe-text .recipe-food-row:last-child) {
  border-bottom: none;
}

:deep(.recipe-text .food-name) {
  flex: 2;
  color: #475569;
  font-weight: 500;
}

:deep(.recipe-text .food-portion) {
  flex: 1;
  color: #92400e;
  text-align: center;
  font-size: 12px;
  background: #fffbeb;
  border-radius: 4px;
  padding: 1px 4px;
  margin: 0 6px;
}

:deep(.recipe-text .food-cal) {
  flex: 0 0 80px;
  color: #e67e22;
  text-align: right;
  font-weight: 500;
  font-size: 12px;
}

:deep(.recipe-text .recipe-gi-line) {
  display: inline-block;
  background: #fef3c7;
  color: #92400e;
  padding: 2px 10px;
  border-radius: 4px;
  font-size: 13px;
  margin: 2px 0;
}

:deep(.recipe-text .recipe-ing-line) {
  display: inline-block;
  font-size: 13px;
  color: #57534e;
  line-height: 1.7;
}

:deep(.recipe-text .recipe-divider) {
  border: none;
  border-top: 1px dashed #f0e0d0;
  margin: 8px 0;
}

:deep(.recipe-text .recipe-summary) {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #1e293b;
  font-weight: 600;
  margin-top: 6px;
  padding: 10px 14px;
  border-radius: 8px;
  background: linear-gradient(135deg, #fef9e7 0%, #fdf2e9 100%);
  border: 1px solid #fce4c8;
}

:deep(.recipe-text .recipe-summary span) {
  display: inline-block;
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
  background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 100%);
  border-radius: 10px;
  padding: 16px;
  margin-bottom: 12px;
  border: 1px solid #d1fae5;
  border-left: 4px solid #10b981;
  transition: transform 0.2s, box-shadow 0.2s;
}

.plan-day:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.1);
}

.plan-day-0 {
  border-left-color: #10b981;
  background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 100%);
}

.plan-day-1 {
  border-left-color: #3b82f6;
  background: linear-gradient(135deg, #eff6ff 0%, #e0f2fe 100%);
  border-color: #bfdbfe;
}

.plan-day-2 {
  border-left-color: #f59e0b;
  background: linear-gradient(135deg, #fffbeb 0%, #fef3c7 100%);
  border-color: #fde68a;
}

.day-header {
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 12px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: space-between;
  user-select: none;
}

.day-title {
  display: flex;
  align-items: center;
  gap: 6px;
}

.day-icon {
  font-size: 18px;
}

.day-header:hover {
  color: #10b981;
}

.day-toggle {
  font-size: 18px;
  opacity: 0.8;
  color: #64748b;
  line-height: 1;
  transition: transform 0.2s ease;
  cursor: pointer;
  padding: 2px 6px;
  border-radius: 4px;
}

.day-toggle:hover {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.plan-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.plan-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 8px 10px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 8px;
}

.plan-label {
  font-weight: 600;
  color: #334155;
  font-size: 13px;
}

.plan-value {
  color: #475569;
  font-size: 13px;
  line-height: 1.6;
}

.feature-btn-blue {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  box-shadow: 0 3px 10px rgba(59, 130, 246, 0.2);
}

.feature-btn-blue:hover:not(:disabled) {
  box-shadow: 0 5px 16px rgba(59, 130, 246, 0.3);
}

.feature-btn-green {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  box-shadow: 0 3px 10px rgba(16, 185, 129, 0.2);
}

.feature-btn-green:hover:not(:disabled) {
  box-shadow: 0 5px 16px rgba(16, 185, 129, 0.3);
}

.feature-btn-outline-green {
  background: transparent;
  border: 1.5px solid #10b981;
  color: #059669;
  box-shadow: none;
  font-size: 13px;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  width: 100%;
}

.feature-btn-outline-green:hover {
  background: rgba(16, 185, 129, 0.08);
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.15);
}

.report-result {
  background: linear-gradient(135deg, #eff6ff 0%, #e0f2fe 100%);
  border: 1px solid #bfdbfe;
  border-radius: 10px;
  padding: 16px;
  margin-top: 4px;
}

.report-result-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #1e40af;
  font-size: 14px;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px solid #bfdbfe;
}

.report-result-icon {
  font-size: 18px;
}

.report-result-content {
  color: #334155;
  font-size: 13px;
  line-height: 1.7;
  white-space: pre-wrap;
}

.recipe-dialog .el-dialog__header {
  background: linear-gradient(135deg, #fff7ed 0%, #fffbeb 100%);
  border-radius: 12px 12px 0 0;
  padding: 18px 24px;
  margin: 0;
  border-bottom: 1px solid #fde2c8;
}

.recipe-dialog .el-dialog__title {
  font-size: 18px;
  font-weight: 600;
  color: #e67e22;
}

.recipe-dialog .el-dialog__body {
  padding: 20px 24px;
  max-height: 70vh;
  overflow-y: auto;
}

.recipe-dialog .el-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.recipe-dialog-body {
  width: 100%;
}

.recipe-dialog-text {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.recipe-dialog-text :deep(.recipe-section) {
  background: linear-gradient(135deg, #fffbeb 0%, #fff7ed 100%);
  border: 1px solid #fde2c8;
  border-radius: 10px;
  padding: 18px;
  margin-bottom: 10px;
}

.recipe-dialog-text :deep(.recipe-meal) {
  font-size: 17px;
  margin-bottom: 12px;
  padding-bottom: 10px;
}

.recipe-dialog-text :deep(.recipe-food-row) {
  padding: 8px 8px;
  font-size: 15px;
}

.recipe-dialog-text :deep(.food-portion) {
  font-size: 14px;
}

.recipe-dialog-text :deep(.food-cal) {
  font-size: 14px;
  flex: 0 0 100px;
}

.recipe-dialog-text :deep(.recipe-summary) {
  font-size: 15px;
  padding: 12px 18px;
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
   height: 480px;
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

  .plan-header-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
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

  .recipe-sidebar {
    position: fixed;
    top: auto;
    bottom: 0;
    right: 0;
    left: 0;
    width: 100% !important;
    margin-left: 0;
    border-radius: 12px 12px 0 0;
    z-index: 1000;
    overflow: visible;
  }

  .recipe-toggle {
    width: 100% !important;
    height: auto !important;
    flex-direction: row !important;
    justify-content: center !important;
    padding: 14px 16px !important;
    border-radius: 12px 12px 0 0 !important;
    gap: 10px;
  }

  .recipe-toggle-text {
    writing-mode: horizontal-tb !important;
    text-orientation: initial !important;
  }

  .recipe-toggle-arrow {
    margin-top: 0 !important;
    margin-left: 10px;
  }

  .recipe-body {
    position: relative;
    top: auto;
    left: auto;
    border-radius: 12px;
    border-left: 1px solid #fde2c8;
    max-height: 50vh;
    overflow-y: auto;
    width: 100%;
    box-shadow: 0 -4px 16px rgba(0, 0, 0, 0.08);
    animation: none;
  }

  .day-header {
    margin-bottom: 8px;
  }
}
</style>