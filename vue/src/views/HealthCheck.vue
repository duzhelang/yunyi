<template>
  <div class="health-check-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>风险快检</span>
          <p class="card-desc">快速评估您的健康风险，预防糖尿病</p>
        </div>
      </template>
      
      <!-- 健康风险评估说明 -->
      <div class="risk-intro">
        <el-alert
          title="评估说明"
          type="info"
          :closable="false"
          show-icon
        >
          <template #default>
            <p>本评估工具基于临床数据，可快速评估您的糖尿病风险。请如实填写以下信息，评估结果仅供参考，不能替代专业医疗诊断。</p>
          </template>
        </el-alert>
      </div>
      
      <el-form label-width="150px" class="health-form">
        <el-form-item label="年龄">
          <el-input v-model.number="store.age" placeholder="请输入年龄" type="number"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="store.gender">
            <el-radio value="男">男</el-radio>
            <el-radio value="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="身高(m)">
          <el-input v-model.number="store.height" placeholder="请输入身高" type="number" step="0.01" @input="calculateBmi"></el-input>
        </el-form-item>
        <el-form-item label="体重(kg)">
          <el-input v-model.number="store.weight" placeholder="请输入体重" type="number" step="0.1" @input="calculateBmi"></el-input>
        </el-form-item>
        <el-form-item label="BMI">
          <el-input :model-value="store.bmi" disabled placeholder="自动计算"></el-input>
        </el-form-item>
        <el-form-item label="空腹血糖值(mg/dL)">
          <el-input v-model.number="store.glucose" placeholder="请输入空腹血糖值" type="number"></el-input>
        </el-form-item>
        <el-form-item label="胰岛素水平(uIU/mL)">
          <el-input v-model.number="store.insulin" placeholder="请输入胰岛素水平" type="number"></el-input>
        </el-form-item>
        <el-form-item label="血压(收缩压)">
          <el-input v-model.number="store.bloodPressure" placeholder="请输入收缩压" type="number"></el-input>
        </el-form-item>
        <el-form-item label="糖尿病家族史">
          <div class="family-history-wrapper">
            <div class="family-radio-row">
              <el-radio-group v-model="familyHistoryType">
                <el-radio value="simple">快速选择</el-radio>
                <el-radio value="precise">精确计算 (DPF)</el-radio>
              </el-radio-group>
            </div>
            <div v-if="familyHistoryType === 'simple'" class="family-simple-row">
              <el-radio-group v-model="familyHistorySimple">
                <el-radio value="无">无</el-radio>
                <el-radio value="有">有</el-radio>
              </el-radio-group>
              <span class="family-precision-warning">
                <el-icon><Warning /></el-icon> 选"有/无"会丢失遗传风险精度
              </span>
            </div>
            <div v-else class="family-precise-row">
              <div class="family-dpf-group">
                <el-input-number v-model="store.diabetesPedigreeFunction" :precision="3" :step="0.01" :min="0.08" :max="2.42" controls-position="right" style="width: 180px" />
                <el-button type="primary" plain size="small" @click="goToDpfCalculator">
                  精确计算
                </el-button>
              </div>
              <span class="family-dpf-value">当前 DPF: {{ store.diabetesPedigreeFunction.toFixed(3) }}</span>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="运动频率">
          <el-select v-model="store.exerciseFrequency" placeholder="请选择运动频率">
            <el-option label="每周少于1次" value="0"></el-option>
            <el-option label="每周1-2次" value="1"></el-option>
            <el-option label="每周3-4次" value="2"></el-option>
            <el-option label="每周5次以上" value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="饮食习惯">
          <el-select v-model="store.dietHabit" placeholder="请选择饮食习惯">
            <el-option label="清淡饮食" value="0"></el-option>
            <el-option label="适中" value="1"></el-option>
            <el-option label="偏咸偏油" value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="吸烟情况">
          <el-radio-group v-model="store.smoking">
            <el-radio value="不吸烟">不吸烟</el-radio>
            <el-radio value="偶尔吸烟">偶尔吸烟</el-radio>
            <el-radio value="经常吸烟">经常吸烟</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="饮酒情况">
          <el-radio-group v-model="store.drinking">
            <el-radio value="不饮酒">不饮酒</el-radio>
            <el-radio value="偶尔饮酒">偶尔饮酒</el-radio>
            <el-radio value="经常饮酒">经常饮酒</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitCheck" style="width: 100%">开始评估</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 结果对话框 -->
    <el-dialog v-model="resultDialogVisible" width="750px" :close-on-click-modal="false" custom-class="result-dialog" :show-close="true">
      <div class="result-container">
        <div class="result-header" :class="riskLevelClass">
          <div class="result-header-main">
            <span class="risk-level-badge" :class="riskLevelClass">{{ riskLevelText }}</span>
            <span class="probability-main">
              <span class="probability-label">患病概率</span>
              <span class="probability-value">{{ probability }}%</span>
            </span>
          </div>
          <div v-if="confidenceInterval[0] > 0" class="confidence-interval">
            置信区间: {{ confidenceInterval[0] }}% - {{ confidenceInterval[1] }}%
          </div>
        </div>

        <div class="echarts-grid" v-if="showECharts">
          <div class="echart-item">
            <h4 class="chart-title">风险仪表盘</h4>
            <div ref="gaugeChartRef" class="echart-box"></div>
          </div>
          <div class="echart-item">
            <h4 class="chart-title">健康雷达图</h4>
            <div ref="radarChartRef" class="echart-box"></div>
          </div>
          <div class="echart-item">
            <h4 class="chart-title">因素贡献</h4>
            <div ref="waterfallChartRef" class="echart-box"></div>
          </div>
          <div class="echart-item">
            <h4 class="chart-title">指标对比</h4>
            <div ref="comparisonChartRef" class="echart-box"></div>
          </div>
        </div>

        <div class="collapse-panel data-details">
          <div class="panel-header" @click="toggleDataDetails">
            <span class="panel-title">数据详情</span>
            <span class="panel-arrow" :class="{ expanded: showDataDetails }">
              <el-icon><ArrowDown /></el-icon>
            </span>
          </div>
          <Transition name="panel-slide">
            <div class="panel-content" v-show="showDataDetails">
              <el-descriptions :column="2" size="small" border>
                <el-descriptions-item label="年龄">{{ store.age }}岁</el-descriptions-item>
                <el-descriptions-item label="性别">{{ store.gender }}</el-descriptions-item>
                <el-descriptions-item label="身高">{{ store.height }} m</el-descriptions-item>
                <el-descriptions-item label="体重">{{ store.weight }} kg</el-descriptions-item>
                <el-descriptions-item label="BMI">{{ store.bmi }}</el-descriptions-item>
                <el-descriptions-item label="空腹血糖">{{ store.glucose }} mg/dL</el-descriptions-item>
                <el-descriptions-item label="胰岛素水平">{{ store.insulin }} uIU/mL</el-descriptions-item>
                <el-descriptions-item label="血压">{{ store.bloodPressure }} mmHg</el-descriptions-item>
                <el-descriptions-item label="家族史DPF">{{ store.diabetesPedigreeFunction }}</el-descriptions-item>
                <el-descriptions-item label="运动频率">{{ getExerciseText(store.exerciseFrequency) }}</el-descriptions-item>
                <el-descriptions-item label="饮食习惯">{{ getDietText(store.dietHabit) }}</el-descriptions-item>
                <el-descriptions-item label="吸烟情况">{{ store.smoking }}</el-descriptions-item>
                <el-descriptions-item label="饮酒情况">{{ store.drinking }}</el-descriptions-item>
              </el-descriptions>
            </div>
          </Transition>
        </div>

        <div class="collapse-panel percentile-ranking" v-if="Object.keys(store.percentiles).length > 0">
          <div class="panel-header" @click="togglePercentileRanking">
            <span class="panel-title">指标百分位排名</span>
            <span class="panel-arrow" :class="{ expanded: showPercentileRanking }">
              <el-icon><ArrowDown /></el-icon>
            </span>
          </div>
          <Transition name="panel-slide">
            <div class="panel-content" v-show="showPercentileRanking">
              <div class="percentile-list">
                <div class="percentile-item" v-for="(value, key) in store.percentiles" :key="key">
                  <span class="percentile-label">{{ getFeatureLabel(key) }}</span>
                  <el-progress
                    :percentage="value"
                    :stroke-width="8"
                    :color="getPercentileColor(value)"
                    :format="val => val + '%'"
                  />
                  <span class="percentile-desc">{{ getPercentileDesc(value) }}</span>
                </div>
              </div>
            </div>
          </Transition>
        </div>

        <div class="health-suggestion-card">
          <div class="prescription-header">
            <el-icon><Document /></el-icon>
            <span>AI 健康处方</span>
          </div>
          <div class="suggestion-section health-advice-section">
            <div class="section-indicator"></div>
            <div class="section-content">
              <h4>健康建议</h4>
              <p>{{ healthAdvice }}</p>
            </div>
          </div>
          <div class="section-divider"></div>
          <div class="suggestion-section prevention-section">
            <div class="section-indicator"></div>
            <div class="section-content">
              <h4>预防措施</h4>
              <ul>
                <li>保持健康饮食，控制碳水化合物摄入</li>
                <li>定期进行有氧运动，每周至少150分钟</li>
                <li>保持健康体重，BMI控制在18.5-24之间</li>
                <li>定期监测血糖，特别是有家族史的人群</li>
                <li>戒烟限酒，减少心血管疾病风险</li>
                <li>保持良好的作息习惯，避免熬夜</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="resultDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="resetForm">重新评估</el-button>
          <el-button type="info" @click="downloadReport">下载报告</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="previewDialogVisible" :title="previewTitle" width="900px" append-to-body>
      <div class="chart-preview" v-if="previewImage">
        <img :src="'data:image/png;base64,' + previewImage" :alt="previewTitle" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import * as echarts from 'echarts'
import { ArrowDown, ArrowLeft, ArrowRight, Document, Warning } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useHealthStore } from '@/store/healthStore'
import { usePrediction } from '@/composables/usePrediction'

const router = useRouter()
const store = useHealthStore()
const { runPrediction, getRiskText, getRiskClass, getHealthAdvice } = usePrediction()

function goToDpfCalculator() {
  router.push('/health-profile')
}

const resultDialogVisible = ref(false)
const probability = computed(() => store.riskProbability)
const riskLevelText = computed(() => getRiskText(store.riskLevel))
const healthAdvice = computed(() => getHealthAdvice(store.riskLevel))
const confidenceInterval = computed(() => store.confidenceInterval)
const showDataDetails = ref(false)
const showPercentileRanking = ref(false)
const previewDialogVisible = ref(false)
const previewImage = ref('')
const previewTitle = ref('')
const familyHistoryType = ref('precise')
const familyHistorySimple = ref('无')

const showECharts = ref(false)
const gaugeChartRef = ref(null)
const radarChartRef = ref(null)
const waterfallChartRef = ref(null)
const comparisonChartRef = ref(null)

const riskLevelClass = computed(() => getRiskClass(store.riskLevel))

onMounted(() => {
  store.loadFromDraft()
})

function showResult() {
  resultDialogVisible.value = true
  nextTick(() => initECharts())
}

function initECharts() {
  showECharts.value = true
  nextTick(() => {
    // 1. 风险仪表盘 (gauge)
    if (gaugeChartRef.value) {
      const gaugeChart = echarts.init(gaugeChartRef.value)
      gaugeChart.setOption({
        series: [{
          type: 'gauge',
          startAngle: 200, endAngle: -20,
          min: 0, max: 100,
          pointer: { length: '60%' },
          progress: { show: true, width: 15 },
          axisLine: { lineStyle: { width: 15, color: [
            [0.3, '#67C23A'], [0.6, '#E6A23C'], [1, '#F56C6C']
          ]}},
          data: [{ value: store.riskProbability, name: '患病概率' }],
          detail: { formatter: '{value}%', fontSize: 20, fontWeight: 'bold' }
        }]
      })
    }

    // 2. 健康雷达图 (radar)
    if (radarChartRef.value) {
      const radarChart = echarts.init(radarChartRef.value)
      const featureNames = ['血糖', 'BMI', '血压', '胰岛素', '皮褶厚度', '年龄', '怀孕次数', '遗传']
      const userValues = [
        store.glucose / 200 * 100,
        store.bmi / 40 * 100,
        store.bloodPressure / 200 * 100,
        store.insulin / 200 * 100,
        store.skinThickness / 50 * 100,
        store.age / 100 * 100,
        store.pregnancies / 10 * 100,
        store.diabetesPedigreeFunction / 2.5 * 100
      ]
      radarChart.setOption({
        radar: {
          indicator: featureNames.map((name, i) => ({ name, max: 100 })),
          radius: '65%'
        },
        series: [{
          type: 'radar',
          data: [{ value: userValues, name: '您的指标', areaStyle: { opacity: 0.2 } }],
          lineStyle: { width: 2 }
        }]
      })
    }

    // 3. 因素贡献 (bar)
    if (waterfallChartRef.value && store.featureImportance.length > 0 && store.featureNames.length > 0) {
      const waterfallChart = echarts.init(waterfallChartRef.value)
      const importanceLabels = store.featureNames.map(n => {
        const map = { Pregnancies: '怀孕次数', Glucose: '血糖', BloodPressure: '血压',
          SkinThickness: '皮褶厚度', Insulin: '胰岛素', BMI: 'BMI',
          DiabetesPedigreeFunction: '遗传', Age: '年龄' }
        return map[n] || n
      })
      waterfallChart.setOption({
        xAxis: { type: 'category', data: importanceLabels, axisLabel: { rotate: 30 } },
        yAxis: { type: 'value', name: 'SHAP 值' },
        series: [{
          type: 'bar',
          data: store.featureImportance.map(v => ({
            value: v,
            itemStyle: { color: v >= 0 ? '#F56C6C' : '#67C23A' }
          }))
        }]
      })
    }

    // 4. 指标对比 (bar)
    if (comparisonChartRef.value) {
      const comparisonChart = echarts.init(comparisonChartRef.value)
      const metricLabels = ['血糖', 'BMI', '血压', '胰岛素']
      const userMetricValues = [store.glucose, store.bmi, store.bloodPressure, store.insulin]
      comparisonChart.setOption({
        xAxis: { type: 'category', data: metricLabels },
        yAxis: { type: 'value' },
        tooltip: { trigger: 'axis' },
        series: [
          { name: '您的值', type: 'bar', data: userMetricValues, barWidth: '30%' },
          { name: '参考值', type: 'bar', data: [90, 22, 120, 50], barWidth: '30%', itemStyle: { color: '#909399' } }
        ]
      })
    }
  })
}

function calculateBmi() {
  if (store.height && store.weight) {
    const h = store.height
    const w = store.weight
    const calcBmi = w / (h * h)
    store.height = h
    store.weight = w
  }
}

async function submitCheck() {
  if (!store.age || !store.height || !store.weight || !store.glucose || !store.insulin || !store.bloodPressure) {
    ElMessage.warning('请填写完整的评估信息')
    return
  }
  if (!store.height || !store.weight) {
    ElMessage.warning('请输入身高和体重')
    return
  }

  if (familyHistoryType.value === 'simple') {
    store.diabetesPedigreeFunction = familyHistorySimple.value === '有' ? 0.8 : 0.2
  }

  const features = store.toFeatures()
  const result = await runPrediction(features)
  if (result.success) {
    store.setPredictionResult(result.data)
  } else {
    store.setPredictionResult(result.data)
    ElMessage.warning(result.message)
  }
  showResult()
  saveSelfCheckToRecord()
}

async function saveSelfCheckToRecord() {
  try {
    const glucoseValue = store.glucose ? (store.glucose / 18).toFixed(1) : null
    const riskText = store.riskLevel === 'high' ? '高风险' : store.riskLevel === 'medium' ? '中风险' : '低风险'
    const payload = {
      recordType: 'self_check',
      recordDate: new Date().toISOString().replace('T', ' ').substring(0, 19),
      chiefComplaint: '风险快检自查',
      diagnosis: `风险等级: ${riskText}，患病概率: ${store.riskProbability}%`,
      treatmentPlan: healthAdvice.value || `${riskText}，建议定期监测并咨询医生`,
      glucoseFasting: glucoseValue ? Number(glucoseValue) : null,
      bloodPressureSystolic: store.bloodPressure || null,
      bloodPressureDiastolic: null,
      bmi: store.bmi || null,
      weight: store.weight || null
    }
    await request.post('/api/patient-visit', payload)
  } catch (e) {
    console.error('自动保存自查记录失败', e)
  }
}

function getFeatureLabel(featureName) {
  const map = {
    Pregnancies: '怀孕次数', Glucose: '血糖浓度', BloodPressure: '血压',
    SkinThickness: '皮脂厚度', Insulin: '胰岛素水平', BMI: 'BMI指数',
    DiabetesPedigreeFunction: '糖尿病遗传系数', Age: '年龄'
  }
  return map[featureName] || featureName
}

function getPercentileColor(value) {
  if (value < 25) return '#bbdefb'
  if (value < 50) return '#c8e6c9'
  if (value < 75) return '#ffe0b2'
  return '#ffcdd2'
}

function getPercentileDesc(value) {
  if (value < 25) return '偏低'
  if (value < 50) return '正常偏低'
  if (value < 75) return '正常偏高'
  return '偏高'
}

function getExerciseText(value) {
  const map = { '0': '每周少于1次', '1': '每周1-2次', '2': '每周3-4次', '3': '每周5次以上' }
  return map[value] || value
}

function getDietText(value) {
  const map = { '0': '清淡饮食', '1': '适中', '2': '偏咸偏油' }
  return map[value] || value
}

function toggleDataDetails() {
  showDataDetails.value = !showDataDetails.value
}

function togglePercentileRanking() {
  showPercentileRanking.value = !showPercentileRanking.value
}

function resetForm() {
  store.resetAll()
  resultDialogVisible.value = false
}

function downloadReport() {
  const htmlContent = `
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>糖尿病风险评估报告</title>
<style>
  body { font-family: 'Microsoft YaHei', Arial, sans-serif; max-width: 800px; margin: 0 auto; padding: 20px; }
  h1 { color: #4080FF; text-align: center; border-bottom: 3px solid #4080FF; padding-bottom: 10px; }
  .header { background: linear-gradient(135deg, #f5f7fa, #c3cfe2); padding: 20px; border-radius: 10px; margin-bottom: 20px; }
  .risk-level { font-size: 24px; font-weight: bold; }
  .probability { font-size: 36px; color: #4080FF; font-weight: bold; }
  .info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
  .info-item { background: #f8f9fa; padding: 10px; border-radius: 5px; }
  .advice { background: #ecf5ff; padding: 15px; border-radius: 8px; border-left: 4px solid #4080FF; margin: 20px 0; }
  .footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #ddd; color: #999; text-align: center; }
</style>
</head>
<body>
  <div class="header">
    <h1>🩺 糖尿病风险评估报告</h1>
    <p style="text-align:center; color:#666;">评估时间: ${new Date().toLocaleString()}</p>
    <div style="text-align:center; margin: 20px 0;">
      <div class="risk-level" style="color:${store.riskLevel === 'high' ? '#f56c6c' : store.riskLevel === 'medium' ? '#e6a23c' : '#67c23a'}">${riskLevelText.value}</div>
      <div class="probability">${store.riskProbability}%</div>
    </div>
  </div>
  <h2>📋 数据详情</h2>
  <div class="info-grid">
    <div class="info-item"><span class="info-label">年龄:</span> ${store.age}岁</div>
    <div class="info-item"><span class="info-label">BMI:</span> ${store.bmi}</div>
    <div class="info-item"><span class="info-label">空腹血糖:</span> ${store.glucose} mg/dL</div>
    <div class="info-item"><span class="info-label">血压:</span> ${store.bloodPressure} mmHg</div>
  </div>
  <h2>💡 健康建议</h2>
  <div class="advice">${healthAdvice.value}</div>
  <div class="footer">
    <p>本评估结果仅供参考，不能替代专业医疗诊断。</p>
    <p>生成时间: ${new Date().toLocaleString()}</p>
  </div>
</body></html>`
  const blob = new Blob([htmlContent], { type: 'text/html' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `糖尿病风险评估报告_${new Date().toISOString().split('T')[0]}.html`
  a.click()
  URL.revokeObjectURL(url)
  ElMessage.success('报告下载成功！')
}
</script>

<style scoped>
.health-check-container {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

.box-card {
  border-radius: 16px !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1) !important;
  background: rgba(255, 255, 255, 0.95) !important;
  backdrop-filter: blur(10px) !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
  overflow: hidden;
}

.card-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px 0;
  background: linear-gradient(135deg, rgba(64, 128, 255, 0.1), rgba(82, 196, 26, 0.1));
  border-radius: 16px 16px 0 0;
}

.card-header span {
  font-size: 24px;
  font-weight: bold;
  color: #4080FF;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.card-desc {
  font-size: 16px;
  color: #6B7280;
  margin-top: 10px;
}

.risk-intro {
  margin-bottom: 30px;
}

.form-tip {
  font-size: 12px;
  color: #9CA3AF;
  margin-top: 5px;
  font-style: italic;
}

.health-form {
  max-width: 600px;
  margin: 0 auto;
}

.health-form .el-form-item {
  margin-bottom: 20px;
}

.health-form .el-input, .health-form .el-select {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.health-form .el-input:focus-within, .health-form .el-select:focus-within {
  box-shadow: 0 0 0 3px rgba(64, 128, 255, 0.1);
}

.health-form .el-button {
  border-radius: 8px;
  padding: 12px 0;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.health-form .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 128, 255, 0.3);
}

.result-container {
  padding: 0;
}

.result-header {
  padding: 24px 28px;
  border-radius: 16px;
  margin-bottom: 20px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0,0,0,0.06);
}

.result-header::before {
  content: '';
  position: absolute;
  top: -30%;
  left: -30%;
  width: 160%;
  height: 160%;
  background: radial-gradient(circle at center, rgba(255,255,255,0.2) 0%, transparent 70%);
  animation: glow-pulse 3s infinite alternate;
}

.result-header::before {
  content: '';
  position: absolute;
  top: -30%;
  left: -30%;
  width: 160%;
  height: 160%;
  background: radial-gradient(circle at center, rgba(255,255,255,0.2) 0%, transparent 70%);
  animation: glow-pulse 3s infinite alternate;
}

@keyframes glow-pulse {
  from { opacity: 0.4; transform: scale(1); }
  to { opacity: 0.8; transform: scale(1.1); }
}

.result-header.low-risk {
  background: linear-gradient(135deg, rgba(82, 196, 26, 0.1), rgba(64, 128, 255, 0.05));
}

.result-header.medium-risk {
  background: linear-gradient(135deg, rgba(230, 162, 60, 0.15), rgba(64, 128, 255, 0.05));
}

.result-header.high-risk {
  background: linear-gradient(135deg, rgba(245, 108, 108, 0.15), rgba(64, 128, 255, 0.05));
}

.result-header-main {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
}

.risk-level-badge {
  font-size: 18px;
  font-weight: bold;
  padding: 8px 16px;
  border-radius: 8px;
}

.risk-level-badge.low-risk {
  background-color: #f0f9eb;
  color: #52C41A;
}

.risk-level-badge.medium-risk {
  background-color: #fff7e6;
  color: #FAAD14;
}

.risk-level-badge.high-risk {
  background-color: #fff1f0;
  color: #F56C6C;
}

.probability-main {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.probability-label {
  font-size: 13px;
  color: #6B7280;
}

.probability-value {
  font-size: 32px;
  font-weight: bold;
  background: linear-gradient(135deg, #4080FF, #52C41A);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 2px 8px rgba(64, 128, 255, 0.3);
  animation: number-breathe 2s ease-in-out infinite;
}

@keyframes number-breathe {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.06); }
}

.confidence-interval {
  margin-top: 10px;
  font-size: 13px;
  color: #6B7280;
  text-align: center;
}

.charts-carousel {
  margin-bottom: 12px;
  background: #F8FAFC;
  border-radius: 12px;
  border: 1px solid rgba(64, 128, 255, 0.1);
  overflow: hidden;
}

.carousel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 16px;
  border-bottom: 1px solid rgba(64, 128, 255, 0.08);
}

.carousel-header h4 {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  color: #374151;
}

.carousel-indicators {
  display: flex;
  gap: 6px;
}

.indicator-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #D1D5DB;
  cursor: pointer;
  transition: all 0.2s;
}

.indicator-dot.active {
  background: #4080FF;
  transform: scale(1.2);
}

.indicator-dot:hover {
  background: #4080FF;
  opacity: 0.7;
}

.carousel-main {
  display: flex;
  align-items: center;
  padding: 16px;
  gap: 12px;
  background: white;
}

.carousel-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 1px solid #E5E7EB;
  background: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  flex-shrink: 0;
}

.carousel-btn:hover:not(:disabled) {
  background: #4080FF;
  border-color: #4080FF;
  color: white;
}

.carousel-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.carousel-display {
  flex: 1;
  height: 280px;
  background: #f8fafc;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  border: 1px solid #E5E7EB;
  transition: all 0.3s;
}

.carousel-display:hover {
  border-color: #4080FF;
}

.carousel-display img {
  max-width: 100%;
  max-height: 250px;
  object-fit: contain;
}

.carousel-hint-text {
  position: absolute;
  bottom: 8px;
  font-size: 11px;
  color: #9CA3AF;
  background: rgba(255, 255, 255, 0.9);
  padding: 2px 8px;
  border-radius: 4px;
}

.carousel-thumbnails {
  display: flex;
  gap: 10px;
  padding: 12px 16px;
  background: #F8FAFC;
  border-top: 1px solid rgba(64, 128, 255, 0.08);
  overflow-x: auto;
}

.carousel-thumbnails::-webkit-scrollbar {
  height: 4px;
}

.carousel-thumbnails::-webkit-scrollbar-track {
  background: #F0F4F8;
}

.carousel-thumbnails::-webkit-scrollbar-thumb {
  background: #D1D5DB;
  border-radius: 2px;
}

.thumbnail {
  flex-shrink: 0;
  width: 80px;
  padding: 6px;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.thumbnail:hover {
  transform: scale(1.08);
  box-shadow: 0 6px 16px rgba(64,128,255,0.15);
}

.thumbnail.active {
  border-color: #4080FF;
  box-shadow: 0 0 0 3px rgba(64,128,255,0.2);
}

.thumbnail img {
  width: 100%;
  height: 45px;
  object-fit: contain;
  display: block;
}

.thumbnail-label {
  display: block;
  font-size: 10px;
  color: #6B7280;
  text-align: center;
  margin-top: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.carousel-hint {
  font-size: 12px;
  color: #9CA3AF;
}

.chart-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F8FAFC;
  border-radius: 8px;
  padding: 20px;
}

.chart-preview img {
  max-width: 100%;
  max-height: 70vh;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.collapse-panel {
  margin-bottom: 12px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #F8FAFC;
  cursor: pointer;
  transition: background-color 0.2s;
}

.panel-header:hover {
  background: #F0F4F8;
}

.panel-title {
  font-size: 15px;
  font-weight: 600;
  color: #374151;
}

.panel-arrow {
  display: flex;
  align-items: center;
  color: #4080FF;
  transition: transform 0.3s ease;
}

.panel-arrow.expanded {
  transform: rotate(180deg);
}

.panel-content {
  padding: 12px 16px;
}

.el-descriptions {
  --el-descriptions-table-border: none;
}

.el-descriptions__body .el-descriptions__table.is-bordered {
  border-collapse: separate;
  border-spacing: 0 4px;
}

.el-descriptions__body .el-descriptions__table tr {
  background: #f9fbfd;
  border-radius: 6px;
}

.el-descriptions__body .el-descriptions__table td {
  border: none;
  padding: 8px 12px;
}

.panel-slide-enter-active,
.panel-slide-leave-active {
  transition: max-height 0.4s ease, opacity 0.3s ease;
  max-height: 600px;
  overflow: hidden;
}

.panel-slide-enter-from,
.panel-slide-leave-to {
  max-height: 0;
  opacity: 0;
}

.percentile-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.percentile-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.percentile-item .el-progress-bar__outer {
  height: 6px;
  border-radius: 3px;
  background: #f0f0f0;
}

.percentile-item .el-progress-bar__inner {
  border-radius: 3px;
  background: linear-gradient(90deg, #67c23a, #e6a23c, #f56c6c);
}

.percentile-label {
  min-width: 80px;
  font-size: 13px;
  color: #374151;
}

.percentile-item .el-progress {
  flex: 1;
}

.percentile-desc {
  min-width: 50px;
  font-size: 12px;
  color: #6B7280;
  text-align: right;
}

.health-suggestion-card {
  border: 1px solid rgba(64,128,255,0.15);
  border-radius: 12px;
  overflow: hidden;
}

.prescription-header {
  background: linear-gradient(90deg, #e6f7ff, #f0f9ff);
  padding: 10px 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #4080FF;
  border-bottom: 1px dashed #4080FF;
}

.suggestion-section {
  display: flex;
  padding: 16px;
}

.section-indicator {
  width: 4px;
  border-radius: 2px;
  margin-right: 12px;
  flex-shrink: 0;
}

.health-advice-section .section-indicator {
  background: #4080FF;
}

.prevention-section .section-indicator {
  background: #52C41A;
}

.section-content {
  flex: 1;
}

.section-content h4 {
  margin: 0 0 8px 0;
  font-size: 15px;
  font-weight: 600;
  color: #374151;
}

.section-content p {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
  color: #4B5563;
}

.section-content ul {
  margin: 0;
  padding-left: 18px;
}

.section-content li {
  margin-bottom: 6px;
  font-size: 14px;
  line-height: 1.5;
  color: #4B5563;
}

.section-content li:last-child {
  margin-bottom: 0;
}

.section-divider {
  height: 1px;
  background: rgba(64, 128, 255, 0.1);
  margin: 0 16px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.dialog-footer .el-button {
  border-radius: 8px;
  padding: 10px 20px;
  transition: all 0.2s;
}

.dialog-footer .el-button:hover {
  transform: translateY(-1px);
}

.dialog-footer .el-button--primary {
  background: #4080FF;
  border-color: #4080FF;
}

.dialog-footer .el-button--primary:hover {
  background: #3370FF;
  border-color: #3370FF;
}

.dialog-footer .el-button--info {
  background: #F8FAFC;
  border-color: #E5E7EB;
  color: #374151;
}

.dialog-footer .el-button--info:hover {
  background: #F0F4F8;
  border-color: #D1D5DB;
}

@media (max-width: 768px) {
  .health-check-container {
    padding: 10px;
  }

  .health-form {
    max-width: 100%;
  }

  .card-header span {
    font-size: 20px;
  }

  .result-header-main {
    flex-direction: column;
    gap: 12px;
  }

  .charts-grid {
    flex-direction: column;
  }

  .dialog-footer {
    flex-direction: column;
  }

  .dialog-footer .el-button {
    width: 100%;
  }
}
</style>

<style>
.el-dialog__wrapper {
  backdrop-filter: blur(4px);
  background: rgba(0,0,0,0.5) !important;
}

.echarts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 20px;
}

.echart-item {
  background: #F8FAFC;
  border-radius: 14px;
  border: 1px solid rgba(64, 128, 255, 0.1);
  padding: 14px;
  transition: box-shadow 0.3s;
}

.echart-item:hover {
  box-shadow: 0 4px 16px rgba(64, 128, 255, 0.1);
}

.chart-title {
  margin: 0 0 10px 0;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  text-align: center;
  letter-spacing: 1px;
}

.echart-box {
  width: 100%;
  height: 200px;
}

.result-dialog {
  border-radius: 20px !important;
  backdrop-filter: blur(20px);
  background: rgba(255,255,255,0.96) !important;
  box-shadow: 0 25px 50px -12px rgba(0,0,0,0.25) !important;
  animation: dialog-fade-in 0.3s ease;
}

@keyframes dialog-fade-in {
  from { opacity: 0; transform: scale(0.96); }
  to { opacity: 1; transform: scale(1); }
}

.result-dialog .el-dialog__header {
  border-bottom: 1px solid rgba(64, 128, 255, 0.08);
  padding: 18px 24px;
}

.result-dialog .el-dialog__body {
  padding: 20px 24px;
}

.family-history-group {
  display: flex;
  gap: 8px;
  align-items: center;
}

.family-hint {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 6px;
  font-size: 12px;
  color: #E6A23C;
}

.family-hint .el-icon {
  font-size: 14px;
}

.family-history-wrapper {
  width: 100%;
}

.family-radio-row {
  margin-bottom: 8px;
}

.family-radio-row .el-radio-group .el-radio {
  margin-right: 16px;
}

.family-simple-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.family-precision-warning {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #E6A23C;
}

.family-precision-warning .el-icon {
  font-size: 14px;
}

.family-precise-row {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.family-dpf-group {
  display: flex;
  gap: 8px;
  align-items: center;
}

.family-dpf-value {
  font-size: 12px;
  color: #909399;
}
</style>