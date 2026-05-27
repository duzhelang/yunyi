<template>
  <el-dialog
    v-model="visible"
    title="低血糖急救"
    width="680px"
    custom-class="tool-dialog"
    :close-on-click-modal="false"
    :append-to-body="true"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <div class="tool-dialog-body">
      <el-alert
        title="低血糖可能危及生命，请立即采取行动！"
        type="warning"
        :closable="false"
        show-icon
        class="tool-alert"
      />
      <el-tabs v-model="currentTab" class="tool-tabs">
        <!-- 症状识别 -->
        <el-tab-pane label="症状识别" name="identify">
          <div class="severity-grid">
            <div
              class="severity-card mild"
              :class="{ selected: severity === 'mild' }"
              @click="selectSeverity('mild')"
            >
              <div class="severity-icon">😰</div>
              <h4>轻度</h4>
              <ul>
                <li>饥饿感</li>
                <li>手抖、出汗</li>
                <li>心慌、焦虑</li>
                <li>面色苍白</li>
              </ul>
              <span class="severity-range">血糖 &lt; 3.9 mmol/L</span>
            </div>
            <div
              class="severity-card moderate"
              :class="{ selected: severity === 'moderate' }"
              @click="selectSeverity('moderate')"
            >
              <div class="severity-icon">😵</div>
              <h4>中度</h4>
              <ul>
                <li>头晕、头痛</li>
                <li>视物模糊</li>
                <li>注意力不集中</li>
                <li>言语不清</li>
              </ul>
              <span class="severity-range">血糖 &lt; 2.8 mmol/L</span>
            </div>
            <div
              class="severity-card severe"
              :class="{ selected: severity === 'severe' }"
              @click="selectSeverity('severe')"
            >
              <div class="severity-icon">🚨</div>
              <h4>重度</h4>
              <ul>
                <li>意识模糊</li>
                <li>抽搐</li>
                <li>昏迷</li>
                <li>无法吞咽</li>
              </ul>
              <span class="severity-range">需要他人帮助</span>
            </div>
          </div>
          <el-alert
            v-if="severity === 'severe'"
            title="重度低血糖：立即拨打 120！不要喂食！保持侧卧位！"
            type="error"
            :closable="false"
            show-icon
            class="tool-alert"
          />
        </el-tab-pane>

        <!-- 15-15 急救步骤 -->
        <el-tab-pane label="15-15 急救步骤" name="steps">
          <div class="emergency-steps">
            <div class="step-item" :class="{ active: step >= 1 }">
              <span class="step-num">1</span>
              <div class="step-content">
                <h4>确认意识清醒，能够吞咽</h4>
                <p>如果意识不清或无法吞咽，立即拨打 120，不要喂食任何东西</p>
              </div>
            </div>
            <div class="step-arrow">⬇</div>
            <div class="step-item" :class="{ active: step >= 2 }">
              <span class="step-num">2</span>
              <div class="step-content">
                <h4>立即摄入 15g 快速升糖食物</h4>
                <p>选择以下任意一种：</p>
                <div class="food-options" @click="advanceToStep2">
                  <span class="food-chip">半杯果汁（120ml）</span>
                  <span class="food-chip">3-4 颗葡萄糖片</span>
                  <span class="food-chip">1 汤匙蜂蜜/糖浆</span>
                  <span class="food-chip">半罐含糖汽水</span>
                  <span class="food-chip">4-5 块方糖</span>
                  <span class="food-chip">1 管葡萄糖凝胶</span>
                </div>
              </div>
            </div>
            <div class="step-arrow">⬇</div>
            <div class="step-item" :class="{ active: step >= 3 }">
              <span class="step-num">3</span>
              <div class="step-content">
                <h4>等待 15 分钟 ⏱</h4>
                <p>设好计时器，不要急于再次进食</p>
                <el-button
                  type="primary"
                  size="small"
                  :disabled="timerRunning"
                  @click="startTimer"
                >
                  {{ timerRunning ? `计时中 ${countdown}s` : '开始 15 分钟计时' }}
                </el-button>
              </div>
            </div>
            <div class="step-arrow">⬇</div>
            <div class="step-item" :class="{ active: step >= 4 }">
              <span class="step-num">4</span>
              <div class="step-content">
                <h4>复测血糖</h4>
                <p>如果血糖仍 &lt; 3.9 mmol/L，重复步骤 2-3</p>
                <p>如果恢复正常，摄入少量复合碳水（如全麦饼干）维持血糖</p>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <!-- 预防建议 -->
        <el-tab-pane label="预防建议" name="prevent">
          <div class="prevent-list">
            <div class="prevent-item">
              <span class="prevent-icon">📋</span>
              <div><strong>规律进餐</strong><p>不要跳过正餐，每 4-5 小时进食一次</p></div>
            </div>
            <div class="prevent-item">
              <span class="prevent-icon">💊</span>
              <div><strong>药物管理</strong><p>按时按量用药，运动前咨询医生调整剂量</p></div>
            </div>
            <div class="prevent-item">
              <span class="prevent-icon">🏃</span>
              <div><strong>运动前准备</strong><p>运动前测血糖，随身携带快速升糖食品</p></div>
            </div>
            <div class="prevent-item">
              <span class="prevent-icon">🌙</span>
              <div><strong>夜间低血糖</strong><p>睡前测血糖，必要时加餐，床头备糖块</p></div>
            </div>
            <div class="prevent-item">
              <span class="prevent-icon">📱</span>
              <div><strong>医疗警示</strong><p>佩戴医疗警示手环，告知家人急救方法</p></div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    <template #footer>
      <el-button @click="closeDialog">关闭</el-button>
      <el-button type="primary" @click="resetEmergency">重置急救步骤</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  emergencySeverity: { type: String, default: 'mild' },
  emergencyStep: { type: Number, default: 1 },
  emergencyTimerRunning: { type: Boolean, default: false },
  emergencyCountdown: { type: Number, default: 0 }
})

const emit = defineEmits([
  'update:modelValue',
  'update:emergencySeverity',
  'update:emergencyStep',
  'start-timer',
  'reset'
])

// 本地状态
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const currentTab = ref('identify')
const severity = ref(props.emergencySeverity)
const step = ref(props.emergencyStep)
const timerRunning = ref(props.emergencyTimerRunning)
const countdown = ref(props.emergencyCountdown)

// 同步外部 prop 变化
watch(() => props.emergencySeverity, (val) => { severity.value = val })
watch(() => props.emergencyStep, (val) => { step.value = val })
watch(() => props.emergencyTimerRunning, (val) => { timerRunning.value = val })
watch(() => props.emergencyCountdown, (val) => { countdown.value = val })

// 打开弹窗时重置标签页
watch(() => props.modelValue, (val) => {
  if (val) {
    currentTab.value = 'identify'
  }
})

function selectSeverity(val) {
  severity.value = val
  emit('update:emergencySeverity', val)
}

function advanceToStep2() {
  const newVal = Math.max(step.value, 2)
  step.value = newVal
  emit('update:emergencyStep', newVal)
}

function startTimer() {
  emit('start-timer')
}

function closeDialog() {
  visible.value = false
}

function resetEmergency() {
  step.value = 1
  severity.value = 'mild'
  emit('update:emergencyStep', 1)
  emit('update:emergencySeverity', 'mild')
  emit('reset')
}
</script>

<style scoped>
/* ===== 工具弹窗通用样式 ===== */
.tool-dialog-body {
  max-height: 65vh;
  overflow-y: auto;
  padding: 0;
}
.tool-tabs :deep(.el-tabs__header) {
  margin-bottom: 18px;
}
.tool-tabs :deep(.el-tabs__item) {
  font-size: 14px;
  font-weight: 500;
}
.tool-alert {
  margin-bottom: 16px;
}

/* 低血糖 - 症状识别 */
.severity-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}
.severity-card {
  border: 2px solid #ebeef5;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.25s;
  background: white;
}
.severity-card:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
.severity-card.selected { border-width: 2px; }
.severity-card.mild.selected { border-color: #67C23A; background: #f0f9eb; }
.severity-card.moderate.selected { border-color: #E6A23C; background: #fdf6ec; }
.severity-card.severe.selected { border-color: #F56C6C; background: #fef0f0; }
.severity-icon { font-size: 28px; margin-bottom: 8px; }
.severity-card h4 { margin: 0 0 8px; font-size: 15px; font-weight: 600; color: #303133; }
.severity-card ul { margin: 0; padding: 0; list-style: none; }
.severity-card ul li { font-size: 13px; color: #606266; padding: 2px 0; }
.severity-card ul li::before { content: '• '; color: #909399; }
.severity-range { display: block; margin-top: 10px; font-size: 11px; color: #909399; font-weight: 500; }

/* 低血糖 - 急救步骤 */
.emergency-steps { padding: 4px 0; }
.step-item {
  display: flex;
  gap: 14px;
  padding: 14px 16px;
  background: #fafafa;
  border-radius: 10px;
  border: 1px solid #ebeef5;
  transition: all 0.3s;
}
.step-item.active {
  border-color: #409EFF;
  background: #ecf5ff;
}
.step-num {
  flex-shrink: 0;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #c0c4cc;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 15px;
}
.step-item.active .step-num { background: #409EFF; }
.step-content h4 { margin: 0 0 4px; font-size: 14px; color: #303133; font-weight: 600; }
.step-content p { margin: 4px 0; font-size: 13px; color: #606266; }
.step-arrow { text-align: center; font-size: 20px; padding: 4px 0; color: #c0c4cc; }
.food-options { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 8px; }
.food-chip {
  display: inline-block;
  padding: 5px 12px;
  background: white;
  border: 1px solid #dcdfe6;
  border-radius: 20px;
  font-size: 12px;
  color: #606266;
  cursor: default;
  transition: all 0.2s;
}
.food-chip:hover { border-color: #409EFF; color: #409EFF; background: #ecf5ff; }

/* 低血糖 - 预防 */
.prevent-list { display: flex; flex-direction: column; gap: 12px; }
.prevent-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  padding: 12px 14px;
  background: #fafafa;
  border-radius: 10px;
  border-left: 3px solid #409EFF;
}
.prevent-icon { font-size: 24px; flex-shrink: 0; }
.prevent-item strong { display: block; font-size: 14px; color: #303133; margin-bottom: 2px; }
.prevent-item p { margin: 0; font-size: 13px; color: #606266; }
</style>
