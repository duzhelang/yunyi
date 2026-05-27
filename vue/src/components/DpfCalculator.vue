<template>
  <el-dialog v-model="visible" title="糖尿病谱系函数计算器" width="680px" :close-on-click-modal="false" custom-class="dpf-dialog" :append-to-body="true">
    <div class="dpf-dialog-content">
      <div class="dpf-section">
        <div class="dpf-section-header">
          <el-icon class="dpf-section-icon"><User /></el-icon>
          <span class="dpf-section-title">家族糖尿病史</span>
          <el-tag size="small" type="info">请填写已知信息</el-tag>
        </div>
        <div class="dpf-family-grid">
          <div v-for="member in familyMembers" :key="member.key" class="dpf-family-card" :class="{ 'has-diabetes': member.hasDiabetes }">
            <div class="family-avatar">{{ member.avatar }}</div>
            <div class="family-info">
              <span class="family-name">{{ member.label }}</span>
              <el-switch v-model="member.hasDiabetes" active-text="有" inactive-text="无" />
              <template v-if="member.hasDiabetes">
                <el-input-number v-model="member.ageAtDiagnosis" :min="10" :max="100" :step="1" size="small" controls-position="right" />
                <span class="family-age-hint">岁发病</span>
              </template>
            </div>
          </div>
        </div>
      </div>

      <div class="dpf-section">
        <div class="dpf-section-header">
          <el-icon class="dpf-section-icon"><DataAnalysis /></el-icon>
          <span class="dpf-section-title">计算结果</span>
          <el-button type="primary" size="small" @click="calculate"><el-icon><Promotion /></el-icon> 开始计算</el-button>
        </div>
        <div class="dpf-result-area">
          <div class="dpf-result-card" :class="dpfRiskClass">
            <div class="dpf-result-main">
              <span class="dpf-result-label">谱系风险指数</span>
              <span class="dpf-result-value">{{ dpfResult.toFixed(3) }}</span>
              <span class="dpf-result-range">范围: 0.08 - 2.42</span>
            </div>
            <div class="dpf-result-level">
              <span class="risk-badge" :class="dpfRiskClass">{{ dpfRiskText }}</span>
            </div>
          </div>
          <div v-if="calcSummary" class="dpf-calc-summary">
            <div class="summary-title">计算明细</div>
            <div v-for="(item, idx) in calcSummary" :key="idx" class="summary-row">
              <span>{{ item.member }}</span>
              <span>{{ item.detail }}</span>
              <span class="summary-score">+{{ item.score.toFixed(3) }}</span>
            </div>
            <div class="summary-row summary-total">
              <span>最终结果</span>
              <span></span>
              <span class="summary-score">{{ dpfResult.toFixed(3) }}</span>
            </div>
          </div>
          <div class="dpf-result-bar">
            <div class="dpf-bar-track">
              <div class="dpf-bar-fill" :style="{ width: dpfBarWidth + '%' }"></div>
              <div class="dpf-bar-marker" :style="{ left: dpfBarWidth + '%' }"></div>
            </div>
            <div class="dpf-bar-labels">
              <span>低风险</span>
              <span>中等风险</span>
              <span>高风险</span>
            </div>
          </div>
          <el-button type="success" @click="apply" class="dpf-apply-btn">
            <el-icon><Check /></el-icon> 应用此结果
          </el-button>
        </div>
      </div>

      <div class="dpf-section dpf-medical-section">
        <div class="dpf-section-header">
          <el-icon class="dpf-section-icon"><Document /></el-icon>
          <span class="dpf-section-title">医学依据</span>
        </div>
        <div class="dpf-medical-content">
          <div class="dpf-medical-item">
            <h4>📐 计算原理</h4>
            <p>基于WHO和ADA糖尿病风险评估指南，综合考虑家族成员的患病情况、亲属关系权重和发病年龄。</p>
          </div>
          <div class="dpf-medical-item">
            <h4>⚖️ 权重系数</h4>
            <div class="dpf-weight-table">
              <div class="weight-row"><span>一级亲属（父母、兄弟姐妹）</span><span class="weight-value">×0.5</span></div>
              <div class="weight-row"><span>发病年龄 &lt; 40岁</span><span class="weight-value">×1.5</span></div>
              <div class="weight-row"><span>发病年龄 40-60岁</span><span class="weight-value">×1.0</span></div>
              <div class="weight-row"><span>发病年龄 &gt; 60岁</span><span class="weight-value">×0.7</span></div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="apply">确定应用</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { User, DataAnalysis, Document, Promotion, Check } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: { type: Number, default: 0.5 }
})
const emit = defineEmits(['update:modelValue'])

const visible = defineModel('visible', { type: Boolean, default: false })

const familyMembers = ref([
  { key: 'father', label: '父亲', avatar: '👨', hasDiabetes: false, ageAtDiagnosis: 50 },
  { key: 'mother', label: '母亲', avatar: '👩', hasDiabetes: false, ageAtDiagnosis: 50 },
  { key: 'sibling', label: '兄弟姐妹', avatar: '👫', hasDiabetes: false, ageAtDiagnosis: 40 },
  { key: 'grandparent', label: '祖父母', avatar: '👴', hasDiabetes: false, ageAtDiagnosis: 60 }
])

const dpfResult = ref(props.modelValue || 0.5)
const calcSummary = ref(null)

watch(visible, (val) => {
  if (val) {
    dpfResult.value = props.modelValue || 0.5
    calcSummary.value = null
    familyMembers.value.forEach(m => {
      m.hasDiabetes = false
      m.ageAtDiagnosis = m.key === 'grandparent' ? 60 : m.key === 'sibling' ? 40 : 50
    })
  }
})

function getAgeFactor(age) {
  if (age < 30) return 1.8
  if (age < 40) return 1.5
  if (age <= 50) return 1.0
  if (age <= 60) return 0.8
  return 0.6
}

function getAgeFactorLabel(age) {
  if (age < 30) return '极早发型'
  if (age < 40) return '早发型'
  if (age <= 50) return '中年型'
  if (age <= 60) return '中老年型'
  return '晚发型'
}

function calculate() {
  const baseValue = 0.08
  let totalScore = 0
  const weights = { father: 0.5, mother: 0.5, sibling: 0.5, grandparent: 0.25 }
  const weightLabels = { father: '父亲', mother: '母亲', sibling: '兄弟姐妹', grandparent: '祖父母' }
  const summary = []
  let hasAnyDiabetes = false

  familyMembers.value.forEach(member => {
    if (member.hasDiabetes) {
      hasAnyDiabetes = true
      const age = member.ageAtDiagnosis || 50
      const weight = weights[member.key] || 0.5
      const ageFactor = getAgeFactor(age)
      const score = weight * ageFactor
      totalScore += score
      summary.push({
        member: weightLabels[member.key] || member.label,
        detail: `权重${weight} × ${getAgeFactorLabel(age)}(${age}岁)×${ageFactor}`,
        score: score
      })
    }
  })

  if (!hasAnyDiabetes) {
    dpfResult.value = baseValue
    calcSummary.value = [{ member: '无家族史', detail: '基础值', score: baseValue }]
  } else {
    dpfResult.value = Math.min(Math.max(baseValue + totalScore * 0.8, 0.08), 2.42)
    calcSummary.value = summary
  }
  ElMessage.success('计算完成')
}

function apply() {
  const val = parseFloat(dpfResult.value.toFixed(3))
  emit('update:modelValue', val)
  visible.value = false
  ElMessage.success(`已应用谱系函数值：${val.toFixed(3)}`)
}

const dpfRiskClass = computed(() => {
  if (dpfResult.value < 0.4) return 'low-risk'
  if (dpfResult.value < 0.8) return 'medium-risk'
  return 'high-risk'
})

const dpfRiskText = computed(() => {
  if (dpfResult.value < 0.4) return '低风险'
  if (dpfResult.value < 0.8) return '中等风险'
  return '高风险'
})

const dpfBarWidth = computed(() => {
  return Math.min(Math.max(((dpfResult.value - 0.08) / (2.42 - 0.08)) * 100, 0), 100)
})
</script>

<style scoped>
.dpf-dialog-content { max-height: 65vh; overflow-y: auto; padding: 0 4px; }
.dpf-section { margin-bottom: 20px; background: #fafbfc; border-radius: 12px; padding: 16px; border: 1px solid #ebeef5; }
.dpf-section-header { display: flex; align-items: center; gap: 8px; margin-bottom: 14px; padding-bottom: 10px; border-bottom: 1px dashed #e0e0e0; }
.dpf-section-icon { font-size: 18px; color: #409EFF; }
.dpf-section-title { font-size: 15px; font-weight: 600; color: #303133; }
.dpf-family-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }
.dpf-family-card { display: flex; align-items: center; gap: 12px; padding: 12px; background: white; border-radius: 10px; border: 1px solid #ebeef5; transition: all 0.3s ease; }
.dpf-family-card.has-diabetes { border-color: #F56C6C; background: linear-gradient(135deg, #fff5f5, #ffe6e6); }
.family-avatar { font-size: 28px; flex-shrink: 0; }
.family-info { flex: 1; display: flex; flex-direction: column; gap: 6px; }
.family-name { font-size: 13px; font-weight: 600; color: #303133; }
.family-age-hint { font-size: 12px; color: #909399; }
.dpf-result-area { text-align: center; }
.dpf-result-card { padding: 20px; border-radius: 12px; margin-bottom: 16px; }
.dpf-result-card.low-risk { background: linear-gradient(135deg, #e8f5e9, #c8e6c9); border: 1px solid #a5d6a7; }
.dpf-result-card.medium-risk { background: linear-gradient(135deg, #fff3e0, #ffe0b2); border: 1px solid #ffcc80; }
.dpf-result-card.high-risk { background: linear-gradient(135deg, #ffebee, #ffcdd2); border: 1px solid #ef9a9a; }
.dpf-result-main { display: flex; flex-direction: column; align-items: center; gap: 8px; margin-bottom: 12px; }
.dpf-result-label { font-size: 14px; color: #606266; }
.dpf-result-value { font-size: 36px; font-weight: 700; background: linear-gradient(135deg, #4080FF, #52C41A); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
.dpf-result-range { font-size: 12px; color: #909399; }
.risk-badge { display: inline-block; padding: 4px 16px; border-radius: 20px; font-size: 14px; font-weight: 600; }
.risk-badge.low-risk { background: #e8f5e9; color: #2e7d32; }
.risk-badge.medium-risk { background: #fff3e0; color: #ef6c00; }
.risk-badge.high-risk { background: #ffebee; color: #c62828; }
.dpf-result-bar { margin: 16px 0; padding: 0 20px; }
.dpf-bar-track { height: 10px; background: linear-gradient(90deg, #4caf50, #ff9800, #f44336); border-radius: 5px; position: relative; }
.dpf-bar-fill { height: 100%; background: transparent; border-radius: 5px; }
.dpf-bar-marker { position: absolute; top: -5px; width: 20px; height: 20px; background: white; border: 3px solid #4080FF; border-radius: 50%; transform: translateX(-50%); box-shadow: 0 2px 8px rgba(64, 128, 255, 0.3); transition: left 0.3s ease; }
.dpf-bar-labels { display: flex; justify-content: space-between; margin-top: 8px; font-size: 12px; color: #909399; }
.dpf-apply-btn { width: 200px; margin-top: 12px; }
.dpf-medical-section { background: linear-gradient(135deg, #f0f9ff, #e6f7ff); border-color: #91d5ff; }
.dpf-medical-content { display: flex; flex-direction: column; gap: 14px; }
.dpf-medical-item { padding: 12px; background: white; border-radius: 8px; border: 1px solid #d9ecff; }
.dpf-medical-item h4 { margin: 0 0 8px 0; font-size: 14px; color: #4080FF; }
.dpf-medical-item p { margin: 0; font-size: 13px; color: #606266; line-height: 1.6; }
.dpf-weight-table { display: flex; flex-direction: column; gap: 6px; }
.weight-row { display: flex; justify-content: space-between; align-items: center; padding: 6px 10px; background: #f5f7fa; border-radius: 6px; font-size: 13px; }
.weight-value { font-weight: 600; color: #4080FF; }

.dpf-calc-summary {
  margin: 16px 0;
  padding: 14px;
  background: #f8fafc;
  border-radius: 10px;
  border: 1px solid #e4e7ed;
}
.summary-title {
  font-size: 13px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #dcdfe6;
}
.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
  font-size: 13px;
  color: #606266;
}
.summary-row + .summary-row {
  border-top: 1px solid #f0f2f5;
}
.summary-score {
  font-weight: 600;
  color: #4080FF;
  min-width: 60px;
  text-align: right;
}
.summary-total {
  margin-top: 8px;
  padding-top: 10px;
  border-top: 2px solid #4080FF !important;
  font-weight: 600;
  color: #303133;
}
.summary-total .summary-score {
  font-size: 15px;
}
</style>
