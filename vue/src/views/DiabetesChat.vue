<template>
  <div class="chat-wrapper">
    <div class="diabetes-chat-container">
      <div class="chat-header">
        <el-icon :size="24" class="doctor-icon"><ChatDotRound /></el-icon>
        <h3>糖尿病健康咨询助手</h3>
        <div class="status">
          <el-button
            class="mute-btn"
            link
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
          </el-button>
          <span class="online-dot"></span>
          <span class="status-text">在线</span>
        </div>
      </div>

      <div class="chat-history" ref="chatHistory">
        <div v-if="messages.length === 0" class="welcome-message">
          <p>您好!我是糖尿病健康咨询助手,有任何关于糖尿病的问题都可以问我~</p>
        </div>

        <div v-if="messages.length === 0" class="quick-questions-panel">
          <div class="panel-header">💡 试试这些问题</div>
          <div v-for="group in quickQuestions" :key="group.category" class="question-group">
            <div class="group-label">{{ group.category }}</div>
            <div class="group-questions">
              <el-button
                v-for="q in group.questions"
                :key="q"
                class="question-chip"
                @click="askSample(q)"
              >{{ q }}</el-button>
            </div>
          </div>
        </div>

        <div v-for="(msg, index) in messages" :key="index" class="message-item">
          <div :class="msg.isUser ? 'user-message' : 'bot-message'">
            <span class="sender">{{ msg.isUser ? '我' : '医生助手' }}</span>
            <p class="content">
              <span v-if="msg.isUser || !msg.isTyping">{{ msg.displayContent }}</span>
              <span v-else>{{ msg.displayContent }}<span class="typing-cursor">|</span></span>
            </p>
            <el-button
              v-if="!msg.isUser"
              class="speak-btn"
              :class="{ speaking: msg.isSpeaking }"
              link
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
            </el-button>
            <span class="time">{{ formatTime(msg.timestamp) }}</span>
          </div>
        </div>

        <div v-if="isLoading && messages.length > 0" class="loading-indicator">
          <div class="dot"></div>
          <div class="dot"></div>
          <div class="dot"></div>
        </div>
      </div>

      <div class="input-area">
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

        <el-input
          v-model="question"
          placeholder="请输入您的糖尿病相关问题..."
          @keyup.enter="sendMessage"
          :disabled="isLoading"
          class="chat-input"
        />
        <el-button type="primary" @click="sendMessage" :disabled="isLoading" :loading="isLoading" class="send-btn">
          发送
        </el-button>
      </div>

      <div class="additional-features">
        <h2>📋 AI辅助工具</h2>
        <div class="feature-card card-plan">
          <div class="plan-header-row">
            <h3><span class="card-dot card-dot-green"></span>健康计划生成</h3>
            <el-button v-if="healthPlan" class="collapse-btn" link @click="planCollapsed = !planCollapsed">
              {{ planCollapsed ? '展开计划 ▾' : '收起全部 ▴' }}
            </el-button>
          </div>
          <div class="feature-content plan-feature-content" :class="{ 'generating-active': isGeneratingPlan }">
            <ProgressOverlay 
              :visible="isGeneratingPlan"
              title="正在生成健康计划"
              :steps="healthPlanSteps"
              :hints="healthPlanHints"
              color="#10b981"
            />
            <div class="plan-form">
              <div class="form-item">
                <label>风险等级</label>
                <el-select v-model="riskLevel" class="plan-select" placeholder="请选择">
                  <el-option label="低风险" value="low" />
                  <el-option label="中风险" value="medium" />
                  <el-option label="高风险" value="high" />
                </el-select>
              </div>
              <div class="form-item">
                <label>异常指标</label>
                <el-checkbox-group v-model="abnormalIndicators" class="checkbox-group">
                  <el-checkbox v-for="indicator in indicators" :key="indicator" :value="indicator">
                    {{ indicator }}
                  </el-checkbox>
                </el-checkbox-group>
              </div>
              <el-button @click="handleGenerateHealthPlan" :disabled="!riskLevel || isLoading" class="feature-btn feature-btn-green">
                📋 生成健康计划
              </el-button>
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
            <el-button v-if="healthPlan" @click="savePlanToRecord" class="feature-btn feature-btn-outline-green" style="margin-top: 12px;">
              💾 保存到诊疗档案
            </el-button>
          </div>
        </div>

        <div class="feature-card card-report">
          <h3><span class="card-dot card-dot-blue"></span>报告解读</h3>
          <div class="feature-content">
            <el-input
              v-model="reportJson"
              type="textarea"
              placeholder="请粘贴预测结果的JSON内容..."
              :rows="4"
              class="report-textarea"
            />
            <el-button @click="interpretReport" :disabled="!reportJson || isLoading" class="feature-btn feature-btn-blue">
              📋 解读报告
            </el-button>
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
          <el-button class="recipe-back-btn" link @click="recipeData = null">← 返回重新生成</el-button>
        </template>
        <template v-else>
          <h4>食谱偏好设置</h4>
          <div class="recipe-prefs">
            <div class="form-item">
              <label>饮食偏好</label>
              <el-checkbox-group v-model="selectedDietPrefs" class="checkbox-group">
                <el-checkbox v-for="pref in dietPrefs" :key="pref" :value="pref">
                  {{ pref }}
                </el-checkbox>
              </el-checkbox-group>
            </div>
            <div class="form-item">
              <label>餐次</label>
              <el-select v-model="mealCount" class="plan-select">
                <el-option label="一日三餐" value="3" />
                <el-option label="一日五餐（加餐）" value="5" />
              </el-select>
            </div>
            <div class="form-item">
              <label>口味偏好</label>
              <el-select v-model="tastePref" class="plan-select">
                <el-option label="清淡" value="清淡" />
                <el-option label="适中" value="适中" />
                <el-option label="重口" value="重口" />
              </el-select>
            </div>
          </div>
          <el-button @click="generateRecipe(true)" :disabled="isLoading || isRecipeLoading" :loading="isRecipeLoading" class="recipe-gen-btn">
            🍳 生成控糖食谱
          </el-button>
        </template>
      </div>
    </div>

    <el-dialog v-model="showRecipeDialog" title="🍎 今日控糖食谱" width="680px" :close-on-click-modal="true" class="recipe-dialog" append-to-body>
      <div class="recipe-dialog-body">
        <div class="recipe-text recipe-dialog-text" v-if="recipeData" v-html="formatRecipe(recipeData)"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { ChatDotRound } from '@element-plus/icons-vue'
import ProgressOverlay from '@/components/common/ProgressOverlay.vue'
import { quickQuestions } from '@/data/diabetesMockData'
import { useModelConfig } from '@/composables/useModelConfig'
import { useChat } from '@/composables/useChat'
import { useHealthPlan } from '@/composables/useHealthPlan'
import { useRecipe } from '@/composables/useRecipe'
import { useReport } from '@/composables/useReport'
import { useTTS } from '@/composables/useTTS'
import { useMarquee } from '@/composables/useMarquee'

const route = useRoute()

const {
  currentUser,
  defaultModel,
  selectedModel,
  tempModel,
  showModelSelector,
  isAdmin,
  loadDefaultModel,
  setDefaultModel,
  initUser
} = useModelConfig()

const {
  marqueeTrack,
  allQuestions,
  initQuestions,
  startMarquee,
  pauseMarquee,
  resumeMarquee,
  stopMarquee
} = useMarquee()

const {
  messages,
  question,
  isLoading,
  chatHistory,
  sendMessage: sendMessageOriginal,
  addUserMessage,
  addBotMessage,
  typeBotMessage,
  scrollToBottom,
  formatTime,
  askSample: askSampleOriginal,
  clearTypingTimer
} = useChat(selectedModel)

const {
  isMuted,
  speak,
  stopSpeaking,
  toggleSpeak,
  toggleMute
} = useTTS(messages)

const onBotMessageComplete = (index) => {
  if (!isMuted.value) {
    nextTick(() => speak(index))
  }
}

const {
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
} = useHealthPlan(selectedModel, { addUserMessage, typeBotMessage, isLoading })

const healthPlanSteps = [
  '分析风险指标',
  '生成饮食建议',
  '制定运动方案',
  '整理注意事项'
]

const healthPlanHints = [
  '正在根据您的风险等级和异常指标进行分析...',
  '为您量身定制低GI、高纤维的饮食方案...',
  '设计适合您身体状况的运动计划...',
  '整理关键健康提醒和注意事项...'
]

const {
  reportJson,
  reportResult,
  interpretReport
} = useReport(selectedModel, { addUserMessage, typeBotMessage, isLoading })

const {
  showRecipePanel,
  recipeData,
  isRecipeLoading,
  showRecipeDialog,
  dietPrefs,
  selectedDietPrefs,
  mealCount,
  tastePref,
  formatRecipe,
  generateRecipe,
  saveRecipeToRecord
} = useRecipe(selectedModel, isLoading)

const sendMessage = sendMessageOriginal
const askSample = askSampleOriginal

const handleGenerateHealthPlan = async () => {
  await generateHealthPlan()
  showRecipePanel.value = true
  nextTick(() => {
    if (!recipeData.value) {
      generateRecipe(true)
    }
  })
}

watch(() => messages.value.length, () => {
  if (messages.value.length > 0) {
    nextTick(() => startMarquee())
  }
})

watch(showRecipePanel, (val) => {
  if (val) {
    nextTick(() => startMarquee())
  } else {
    stopMarquee()
  }
})

onMounted(async () => {
  initUser()
  await loadDefaultModel()
  selectedModel.value = defaultModel.value
  if (isAdmin.value) {
    tempModel.value = defaultModel.value
  }

  if (recipeData.value) {
    showRecipePanel.value = true
  }

  const query = route.query
  if (query.context) {
    messages.value.push({
      isUser: false,
      content: `根据您的检测结果：${query.context}，我可以为您提供更针对性的建议。`,
      displayContent: `根据您的检测结果：${query.context}，我可以为您提供更针对性的建议。`,
      isTyping: false,
      isSpeaking: false,
      timestamp: new Date()
    })
  }

  initQuestions()
})

onBeforeUnmount(() => {
  stopSpeaking()
  stopMarquee()
  clearTypingTimer()
})
</script>

<style scoped>
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

.input-area {
  display: flex;
  padding: 18px 24px;
  background: white;
  border-top: 1px solid #eaeef5;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.chat-input {
  flex: 1;
  min-width: 200px;
}

.chat-input :deep(.el-input__wrapper) {
  border-radius: 24px;
  padding: 8px 20px;
  box-shadow: 0 0 0 1px #cbd5e1;
}

.chat-input :deep(.el-input__wrapper:focus-within) {
  box-shadow: 0 0 0 1px #4a90e2, 0 0 0 3px rgba(74, 144, 226, 0.15);
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

.plan-feature-content {
  position: relative;
  overflow: hidden;
}

.plan-feature-content.generating-active {
  min-height: 500px;
}

.report-textarea {
  width: 100%;
}

.report-textarea :deep(.el-textarea__inner) {
  border-radius: 10px;
  padding: 14px 16px;
  font-size: 14px;
  resize: vertical;
  background: #fafbfc;
  transition: border-color 0.2s, box-shadow 0.2s, background 0.2s;
}

.report-textarea :deep(.el-textarea__inner:focus) {
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
  width: 100%;
}

.checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.checkbox-group .el-checkbox {
  margin-right: 0;
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

.recipe-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #fff7ed 0%, #fffbeb 100%);
  border-radius: 12px 12px 0 0;
  padding: 18px 24px;
  margin: 0;
  border-bottom: 1px solid #fde2c8;
}

.recipe-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #e67e22;
}

.recipe-dialog :deep(.el-dialog__body) {
  padding: 20px 24px;
  max-height: 70vh;
  overflow-y: auto;
}

.recipe-dialog :deep(.el-dialog) {
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
