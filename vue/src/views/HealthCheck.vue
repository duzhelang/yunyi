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
      
      <el-form :model="form" label-width="150px" class="health-form">
        <el-form-item label="年龄">
          <el-input v-model.number="form.age" placeholder="请输入年龄" type="number"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="身高(m)">
          <el-input v-model.number="form.height" placeholder="请输入身高" type="number" step="0.01" @input="calculateBmi"></el-input>
        </el-form-item>
        <el-form-item label="体重(kg)">
          <el-input v-model.number="form.weight" placeholder="请输入体重" type="number" step="0.1" @input="calculateBmi"></el-input>
        </el-form-item>
        <el-form-item label="空腹血糖值(mg/dL)">
          <el-input v-model.number="form.glucose" placeholder="请输入空腹血糖值" type="number"></el-input>
        </el-form-item>
        <el-form-item label="胰岛素水平(uIU/mL)">
          <el-input v-model.number="form.insulin" placeholder="请输入胰岛素水平" type="number"></el-input>
        </el-form-item>
        <el-form-item label="血压(收缩压)">
          <el-input v-model.number="form.bloodPressure" placeholder="请输入收缩压" type="number"></el-input>
        </el-form-item>
        <el-form-item label="糖尿病家族史">
          <el-radio-group v-model="form.familyHistory">
            <el-radio label="有">有</el-radio>
            <el-radio label="无">无</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="运动频率">
          <el-select v-model="form.exerciseFrequency" placeholder="请选择运动频率">
            <el-option label="每周少于1次" value="0"></el-option>
            <el-option label="每周1-2次" value="1"></el-option>
            <el-option label="每周3-4次" value="2"></el-option>
            <el-option label="每周5次以上" value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="饮食习惯">
          <el-select v-model="form.dietHabit" placeholder="请选择饮食习惯">
            <el-option label="清淡饮食" value="0"></el-option>
            <el-option label="适中" value="1"></el-option>
            <el-option label="偏咸偏油" value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="吸烟情况">
          <el-radio-group v-model="form.smoking">
            <el-radio label="不吸烟">不吸烟</el-radio>
            <el-radio label="偶尔吸烟">偶尔吸烟</el-radio>
            <el-radio label="经常吸烟">经常吸烟</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="饮酒情况">
          <el-radio-group v-model="form.drinking">
            <el-radio label="不饮酒">不饮酒</el-radio>
            <el-radio label="偶尔饮酒">偶尔饮酒</el-radio>
            <el-radio label="经常饮酒">经常饮酒</el-radio>
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

        <div class="charts-carousel" v-if="chartsData">
          <div class="carousel-header">
            <h4>可视化分析</h4>
            <span class="carousel-indicators">
              <span
                v-for="(chart, index) in availableCharts"
                :key="index"
                :class="['indicator-dot', { active: currentChartIndex === index }]"
                @click="goToChart(index)"
              ></span>
            </span>
          </div>
          <div class="carousel-main">
            <button class="carousel-btn prev" @click="prevChart" :disabled="availableCharts.length <= 1">
              <el-icon><ArrowLeft /></el-icon>
            </button>
            <div class="carousel-display" v-loading="!chartsData" element-loading-background="rgba(255,255,255,0.5)" @click="previewCurrentChart">
              <img
                v-if="availableCharts[currentChartIndex]"
                :src="'data:image/png;base64,' + availableCharts[currentChartIndex].image"
                :alt="availableCharts[currentChartIndex].label"
              />
              <div class="carousel-hint-text">点击查看大图</div>
            </div>
            <button class="carousel-btn next" @click="nextChart" :disabled="availableCharts.length <= 1">
              <el-icon><ArrowRight /></el-icon>
            </button>
          </div>
          <div class="carousel-thumbnails">
            <div
              v-for="(chart, index) in availableCharts"
              :key="index"
              :class="['thumbnail', { active: currentChartIndex === index }]"
              @click="goToChart(index)"
            >
              <img :src="'data:image/png;base64,' + chart.image" :alt="chart.label" />
              <span class="thumbnail-label">{{ chart.label }}</span>
            </div>
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
                <el-descriptions-item label="年龄">{{ form.age }}岁</el-descriptions-item>
                <el-descriptions-item label="性别">{{ form.gender }}</el-descriptions-item>
                <el-descriptions-item label="身高">{{ form.height }} m</el-descriptions-item>
                <el-descriptions-item label="体重">{{ form.weight }} kg</el-descriptions-item>
                <el-descriptions-item label="BMI">{{ form.bmi }}</el-descriptions-item>
                <el-descriptions-item label="空腹血糖">{{ form.glucose }} mg/dL</el-descriptions-item>
                <el-descriptions-item label="胰岛素水平">{{ form.insulin }} uIU/mL</el-descriptions-item>
                <el-descriptions-item label="血压">{{ form.bloodPressure }} mmHg</el-descriptions-item>
                <el-descriptions-item label="家族史">{{ form.familyHistory }}</el-descriptions-item>
                <el-descriptions-item label="运动频率">{{ getExerciseText(form.exerciseFrequency) }}</el-descriptions-item>
                <el-descriptions-item label="饮食习惯">{{ getDietText(form.dietHabit) }}</el-descriptions-item>
                <el-descriptions-item label="吸烟情况">{{ form.smoking }}</el-descriptions-item>
                <el-descriptions-item label="饮酒情况">{{ form.drinking }}</el-descriptions-item>
              </el-descriptions>
            </div>
          </Transition>
        </div>

        <div class="collapse-panel percentile-ranking" v-if="Object.keys(percentiles).length > 0">
          <div class="panel-header" @click="togglePercentileRanking">
            <span class="panel-title">指标百分位排名</span>
            <span class="panel-arrow" :class="{ expanded: showPercentileRanking }">
              <el-icon><ArrowDown /></el-icon>
            </span>
          </div>
          <Transition name="panel-slide">
            <div class="panel-content" v-show="showPercentileRanking">
              <div class="percentile-list">
                <div class="percentile-item" v-for="(value, key) in percentiles" :key="key">
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { ArrowDown, ArrowLeft, ArrowRight, Document } from '@element-plus/icons-vue'
import request from '@/utils/request'

const form = ref({
  age: 20,
  gender: '女',
  height: 1.7,
  weight: 60,
  bmi: '',
  glucose: 90,
  insulin: 50,
  bloodPressure: 120,
  familyHistory: '无',
  exerciseFrequency: '0',
  dietHabit: '1',
  smoking: '不吸烟',
  drinking: '不饮酒'
})

const resultDialogVisible = ref(false)
const probability = ref(0)
const riskLevelText = ref('')
const healthAdvice = ref('')
const confidenceInterval = ref([0, 0])
const featureImportance = ref([])
const featureNames = ref([])
const percentiles = ref({})
const similarCases = ref(null)
const chartsData = ref(null)
const showDataDetails = ref(false)
const showPercentileRanking = ref(false)
const previewDialogVisible = ref(false)
const previewImage = ref('')
const previewTitle = ref('')
const currentChartIndex = ref(0)

const availableCharts = computed(() => {
  if (!chartsData.value) return []
  const chartList = [
    { key: 'dashboard_chart', label: '风险仪表盘', image: chartsData.value.dashboard_chart },
    { key: 'radar_chart', label: '健康雷达图', image: chartsData.value.radar_chart },
    { key: 'comparison_chart', label: '指标对比', image: chartsData.value.comparison_chart },
    { key: 'scorecard_chart', label: '健康评分', image: chartsData.value.scorecard_chart },
    { key: 'heatmap_chart', label: '风险热力图', image: chartsData.value.heatmap_chart },
    { key: 'waterfall_chart', label: '因素贡献', image: chartsData.value.waterfall_chart },
    { key: 'confidence_chart', label: '置信区间', image: chartsData.value.confidence_chart },
    { key: 'pie_chart', label: '风险分布', image: chartsData.value.pie_chart },
    { key: 'importance_chart', label: '特征重要性', image: chartsData.value.importance_chart }
  ]
  return chartList.filter(chart => chart.image)
})

function toggleDataDetails() {
  showDataDetails.value = !showDataDetails.value
}

function togglePercentileRanking() {
  showPercentileRanking.value = !showPercentileRanking.value
}

function previewChart(chartKey, title) {
  if (chartsData.value && chartsData.value[chartKey]) {
    previewImage.value = chartsData.value[chartKey]
    previewTitle.value = title
    previewDialogVisible.value = true
  }
}

function previewCurrentChart() {
  if (availableCharts.value.length > 0) {
    const current = availableCharts.value[currentChartIndex.value]
    previewImage.value = current.image
    previewTitle.value = current.label
    previewDialogVisible.value = true
  }
}

function prevChart() {
  if (availableCharts.value.length > 0) {
    currentChartIndex.value = (currentChartIndex.value - 1 + availableCharts.value.length) % availableCharts.value.length
  }
}

function nextChart() {
  if (availableCharts.value.length > 0) {
    currentChartIndex.value = (currentChartIndex.value + 1) % availableCharts.value.length
  }
}

function goToChart(index) {
  currentChartIndex.value = index
}

// 获取用户信息并填充到表单
async function loadUserInfo() {
  try {
    const user = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null
    if (user && user.username) {
      const response = await request.get('/user/username/' + user.username)
      if (response.data) {
        if (response.data.age) {
          form.value.age = response.data.age
        }
        if (response.data.sex) {
          form.value.gender = response.data.sex
        }
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

onMounted(() => {
  loadUserInfo()
  // 初始化时计算 BMI
  setTimeout(() => {
    calculateBmi()
  }, 100)
})

function calculateBmi() {
  if (form.value.height && form.value.weight && form.value.height > 0) {
    const heightInMeters = form.value.height
    const bmi = form.value.weight / (heightInMeters * heightInMeters)
    form.value.bmi = Math.round(bmi * 10) / 10
  } else {
    form.value.bmi = ''
  }
}

function updateBmiValidation() {
  if (form.value.height && form.value.weight) {
    return true
  }
  ElMessage.warning('请输入身高和体重')
  return false
}

const riskLevelClass = computed(() => {
  if (probability.value < 30) return 'low-risk'
  if (probability.value < 60) return 'medium-risk'
  return 'high-risk'
})

// 按绝对值排序特征重要性
const sortedFeatureImportance = computed(() => {
  if (!featureImportance.value || featureImportance.value.length === 0) return []
  return [...featureImportance.value]
})

function submitCheck() {
  // 验证表单
  if (form.value.age === '' || form.value.height === '' || form.value.weight === '' || form.value.glucose === '' || form.value.insulin === '' || form.value.bloodPressure === '') {
    ElMessage.warning('请填写完整的评估信息')
    return
  }

  if (!updateBmiValidation()) {
    return
  }

  // 确保 BMI 已计算
  if (!form.value.bmi) {
    calculateBmi()
  }

  const loadingInstance = ElLoading.service({
    lock: true,
    text: '正在分析数据，请稍候...',
    background: 'rgba(0, 0, 0, 0.7)'
  })

  // 准备请求数据
  const requestData = {
    pregnancies: form.value.gender === '女' ? (form.value.age > 30 ? 2 : 1) : 0,
    glucose: form.value.glucose,
    bloodPressure: form.value.bloodPressure,
    skinThickness: 20,
    insulin: form.value.insulin,
    bmi: form.value.bmi,
    diabetesPedigreeFunction: 0.5,
    age: form.value.age
  }

  // 调用后端预测接口
  request.post('/api/predict/single', requestData)
  .then(res => {
    loadingInstance.close()
    console.log('后端返回数据:', res)
    if (res.code === '200' && res.data && res.data.probability !== undefined) {
      ElMessage.success('预测成功！')
      probability.value = res.data.probability
      confidenceInterval.value = res.data.confidence_interval || [0, 0]
      featureImportance.value = res.data.feature_importance || []
      featureNames.value = res.data.feature_names || []
      percentiles.value = res.data.percentiles || {}
      similarCases.value = res.data.similar_cases || null
      chartsData.value = res.data.charts || null
      if (res.data.risk_level === 'high') {
        riskLevelText.value = '高风险'
        healthAdvice.value = '您的糖尿病风险较高，建议尽快就医，进行专业检查和治疗。建议立即咨询医生，进行详细的血糖检测和相关检查，制定个性化的预防和治疗方案。'
      } else if (res.data.risk_level === 'medium') {
        riskLevelText.value = '中风险'
        healthAdvice.value = '您的糖尿病风险中等，建议控制饮食，增加运动，定期监测血糖。建议每半年进行一次血糖检测，减少高糖、高脂肪食物摄入，每周至少进行150分钟中等强度运动。'
      } else {
        riskLevelText.value = '低风险'
        healthAdvice.value = '您的糖尿病风险较低，请继续保持健康的生活方式，定期体检。建议每年进行一次血糖检测，保持均衡饮食和适量运动。'
      }
    } else {
      ElMessage.warning('后端返回格式异常，使用本地预测')
      // 后端返回格式不符合预期，使用本地计算
      calculateLocalPrediction()
    }
    resultDialogVisible.value = true
  })
  .catch(error => {
    loadingInstance.close()
    console.error('预测失败，使用本地计算:', error)
    ElMessage.error('预测接口调用失败，使用本地预测')
    // 使用本地模拟计算
    calculateLocalPrediction()
    resultDialogVisible.value = true
  })
}

function calculateLocalPrediction() {
  // 模拟评估结果，根据输入值计算更合理的概率
  let baseProbability = 10
  
  // 年龄因素
  if (form.value.age > 60) baseProbability += 20
  else if (form.value.age > 45) baseProbability += 10
  
  // BMI因素
  if (form.value.bmi > 30) baseProbability += 25
  else if (form.value.bmi > 24) baseProbability += 15
  
  // 血糖因素
  if (form.value.glucose > 120) baseProbability += 30
  else if (form.value.glucose > 100) baseProbability += 15
  
  // 家族史因素
  if (form.value.familyHistory === '有') baseProbability += 20
  
  // 运动频率因素
  if (form.value.exerciseFrequency === '0') baseProbability += 15
  else if (form.value.exerciseFrequency === '1') baseProbability += 5
  
  // 饮食习惯因素
  if (form.value.dietHabit === '2') baseProbability += 15
  
  // 吸烟因素
  if (form.value.smoking === '经常吸烟') baseProbability += 10
  
  // 饮酒因素
  if (form.value.drinking === '经常饮酒') baseProbability += 8
  
  // 确保概率在合理范围内
  probability.value = Math.min(Math.max(baseProbability, 5), 95)
  
  if (probability.value < 30) {
    riskLevelText.value = '低风险'
    healthAdvice.value = '您的糖尿病风险较低，请继续保持健康的生活方式，定期体检。建议每年进行一次血糖检测，保持均衡饮食和适量运动。'
  } else if (probability.value < 60) {
    riskLevelText.value = '中风险'
    healthAdvice.value = '您的糖尿病风险中等，建议控制饮食，增加运动，定期监测血糖。建议每半年进行一次血糖检测，减少高糖、高脂肪食物摄入，每周至少进行150分钟中等强度运动。'
  } else {
    riskLevelText.value = '高风险'
    healthAdvice.value = '您的糖尿病风险较高，建议尽快就医，进行专业检查和治疗。建议立即咨询医生，进行详细的血糖检测和相关检查，制定个性化的预防和治疗方案。'
  }
}

function resetForm() {
  form.value = {
    age: 20,
    gender: '女',
    height: 1.7,
    weight: 60,
    bmi: '',
    glucose: 90,
    insulin: 50,
    bloodPressure: 120,
    familyHistory: '无',
    exerciseFrequency: '0',
    dietHabit: '1',
    smoking: '不吸烟',
    drinking: '不饮酒'
  }
  resultDialogVisible.value = false
}

function getExerciseText(value) {
  const map = {
    '0': '每周少于1次',
    '1': '每周1-2次',
    '2': '每周3-4次',
    '3': '每周5次以上'
  }
  return map[value] || value
}

function getDietText(value) {
  const map = {
    '0': '清淡饮食',
    '1': '适中',
    '2': '偏咸偏油'
  }
  return map[value] || value
}

function getFeatureLabel(featureName) {
  const map = {
    'Pregnancies': '怀孕次数',
    'Glucose': '血糖浓度',
    'BloodPressure': '血压',
    'SkinThickness': '皮脂厚度',
    'Insulin': '胰岛素水平',
    'BMI': 'BMI指数',
    'DiabetesPedigreeFunction': '糖尿病遗传系数',
    'Age': '年龄'
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

function downloadReport() {
  const percentileText = Object.keys(percentiles.value).length > 0
    ? `<h3>指标百分位排名</h3><ul>` +
      Object.entries(percentiles.value)
        .map(([key, value]) => `<li>${getFeatureLabel(key)}: ${value}% (${getPercentileDesc(value)})</li>`)
        .join('') + '</ul>'
    : ''

  const similarCasesText = similarCases.value
    ? `<h3>相似病例分析</h3><ul>
      <li>相似病例数: ${similarCases.value.count}</li>
      <li>平均患病概率: ${similarCases.value.avg_probability}%</li>
      <li>概率范围: ${similarCases.value.min_probability}% - ${similarCases.value.max_probability}%</li>
      <li>标准差: ${similarCases.value.std_probability}%</li>
    </ul>`
    : ''

  const featureImportanceText = featureImportance.value.length > 0
    ? `<h3>特征重要性 (SHAP)</h3><ul>` +
      featureNames.value
        .map((name, i) => `<li>${getFeatureLabel(name)}: ${(featureImportance.value[i] * 100).toFixed(2)}%</li>`)
        .join('') + '</ul>'
    : ''

  const chartsHtml = chartsData.value
    ? `<div class="charts-section">
        <h3>可视化分析</h3>
        <div class="charts-grid-full">
          ${chartsData.value.dashboard_chart ? `<div class="chart-item-full"><h5>风险仪表盘</h5><img src="data:image/png;base64,${chartsData.value.dashboard_chart}" /></div>` : ''}
          ${chartsData.value.radar_chart ? `<div class="chart-item-full"><h5>健康雷达图</h5><img src="data:image/png;base64,${chartsData.value.radar_chart}" /></div>` : ''}
          ${chartsData.value.comparison_chart ? `<div class="chart-item-full"><h5>指标对比</h5><img src="data:image/png;base64,${chartsData.value.comparison_chart}" /></div>` : ''}
          ${chartsData.value.scorecard_chart ? `<div class="chart-item-full"><h5>健康评分</h5><img src="data:image/png;base64,${chartsData.value.scorecard_chart}" /></div>` : ''}
          ${chartsData.value.heatmap_chart ? `<div class="chart-item-full"><h5>风险热力图</h5><img src="data:image/png;base64,${chartsData.value.heatmap_chart}" /></div>` : ''}
          ${chartsData.value.waterfall_chart ? `<div class="chart-item-full"><h5>因素贡献</h5><img src="data:image/png;base64,${chartsData.value.waterfall_chart}" /></div>` : ''}
          ${chartsData.value.confidence_chart ? `<div class="chart-item-full"><h5>置信区间</h5><img src="data:image/png;base64,${chartsData.value.confidence_chart}" /></div>` : ''}
          ${chartsData.value.pie_chart ? `<div class="chart-item-full"><h5>风险分布</h5><img src="data:image/png;base64,${chartsData.value.pie_chart}" /></div>` : ''}
          ${chartsData.value.importance_chart ? `<div class="chart-item-full"><h5>特征重要性</h5><img src="data:image/png;base64,${chartsData.value.importance_chart}" /></div>` : ''}
        </div>
      </div>`
    : ''

  const htmlContent = `
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>糖尿病风险评估报告</title>
  <style>
    body { font-family: 'Microsoft YaHei', Arial, sans-serif; max-width: 800px; margin: 0 auto; padding: 20px; }
    h1 { color: #4080FF; text-align: center; border-bottom: 3px solid #4080FF; padding-bottom: 10px; }
    h2 { color: #333; margin-top: 30px; }
    h3 { color: #666; margin-top: 20px; }
    .header { background: linear-gradient(135deg, #f5f7fa, #c3cfe2); padding: 20px; border-radius: 10px; margin-bottom: 20px; }
    .risk-level { font-size: 24px; font-weight: bold; color: ${riskLevelText.value === '高风险' ? '#f56c6c' : riskLevelText.value === '中风险' ? '#e6a23c' : '#67c23a'}; }
    .probability { font-size: 36px; color: #4080FF; font-weight: bold; }
    .info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
    .info-item { background: #f8f9fa; padding: 10px; border-radius: 5px; }
    .info-label { font-weight: bold; color: #666; }
    .charts-section { margin: 20px 0; }
    .charts-grid { display: flex; flex-wrap: wrap; gap: 10px; }
    .charts-grid-full { display: grid; grid-template-columns: repeat(3, 1fr); gap: 15px; }
    .chart-item { flex: 1; min-width: 200px; background: white; padding: 10px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
    .chart-item img { width: 100%; height: auto; }
    .chart-item h5 { margin: 0 0 10px 0; text-align: center; color: #666; }
    .chart-item-full { background: white; padding: 12px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
    .chart-item-full img { width: 100%; height: 180px; object-fit: contain; display: block; }
    .chart-item-full h5 { margin: 8px 0 0 0; text-align: center; color: #6B7280; font-size: 13px; font-weight: 500; }
    ul { list-style-type: none; padding-left: 0; }
    li { padding: 5px 0; border-bottom: 1px solid #eee; }
    .advice { background: #ecf5ff; padding: 15px; border-radius: 8px; border-left: 4px solid #4080FF; margin: 20px 0; }
    .tips { background: #f0f9eb; padding: 15px; border-radius: 8px; border-left: 4px solid #67c23a; }
    .tips ul { padding-left: 20px; }
    .footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #ddd; color: #999; text-align: center; }
    @media print { body { padding: 0; } .no-print { display: none; } }
  </style>
</head>
<body>
  <div class="header">
    <h1>🩺 糖尿病风险评估报告</h1>
    <p style="text-align:center; color:#666;">评估时间: ${new Date().toLocaleString()}</p>
    <div style="text-align:center; margin: 20px 0;">
      <div class="risk-level">${riskLevelText.value}</div>
      <div class="probability">${probability.value}%</div>
      ${confidenceInterval.value[0] > 0 ? `<p style="color:#666;">置信区间: ${confidenceInterval.value[0]}% - ${confidenceInterval.value[1]}%</p>` : ''}
    </div>
  </div>

  <h2>📋 数据详情</h2>
  <div class="info-grid">
    <div class="info-item"><span class="info-label">年龄:</span> ${form.value.age}岁</div>
    <div class="info-item"><span class="info-label">性别:</span> ${form.value.gender}</div>
    <div class="info-item"><span class="info-label">身高:</span> ${form.value.height} m</div>
    <div class="info-item"><span class="info-label">体重:</span> ${form.value.weight} kg</div>
    <div class="info-item"><span class="info-label">BMI:</span> ${form.value.bmi}</div>
    <div class="info-item"><span class="info-label">空腹血糖:</span> ${form.value.glucose} mg/dL</div>
    <div class="info-item"><span class="info-label">胰岛素水平:</span> ${form.value.insulin} uIU/mL</div>
    <div class="info-item"><span class="info-label">血压:</span> ${form.value.bloodPressure} mmHg</div>
    <div class="info-item"><span class="info-label">家族史:</span> ${form.value.familyHistory}</div>
    <div class="info-item"><span class="info-label">运动频率:</span> ${getExerciseText(form.value.exerciseFrequency)}</div>
    <div class="info-item"><span class="info-label">饮食习惯:</span> ${getDietText(form.value.dietHabit)}</div>
    <div class="info-item"><span class="info-label">吸烟情况:</span> ${form.value.smoking}</div>
  </div>

  ${percentileText}
  ${similarCasesText}
  ${featureImportanceText}
  ${chartsHtml}

  <h2>💡 健康建议</h2>
  <div class="advice">${healthAdvice.value}</div>

  <h2>🛡️ 预防措施</h2>
  <div class="tips">
    <ul>
      <li>保持健康饮食，控制碳水化合物摄入</li>
      <li>定期进行有氧运动，每周至少150分钟</li>
      <li>保持健康体重，BMI控制在18.5-24之间</li>
      <li>定期监测血糖，特别是有家族史的人群</li>
      <li>戒烟限酒，减少心血管疾病风险</li>
      <li>保持良好的作息习惯，避免熬夜</li>
    </ul>
  </div>

  <div class="footer">
    <p>备注: 本评估结果仅供参考，不能替代专业医疗诊断。</p>
    <p>生成时间: ${new Date().toLocaleString()}</p>
  </div>
</body>
</html>
  `

  const blob = new Blob([htmlContent], { type: 'text/html' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `糖尿病风险评估报告_${new Date().toISOString().split('T')[0]}.html`
  a.click()
  URL.revokeObjectURL(url)

  ElMessage.success('报告下载成功！可使用浏览器打印功能保存为PDF')
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
  padding: 20px 24px;
  border-radius: 16px;
  margin-bottom: 16px;
  position: relative;
  overflow: hidden;
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
.result-dialog {
  border-radius: 20px !important;
  backdrop-filter: blur(20px);
  background: rgba(255,255,255,0.92) !important;
  box-shadow: 0 25px 50px -12px rgba(0,0,0,0.25);
  animation: dialog-fade-in 0.3s ease;
}

@keyframes dialog-fade-in {
  from { opacity: 0; transform: scale(0.96); }
  to { opacity: 1; transform: scale(1); }
}

.dialog-footer .el-button {
  border-radius: 8px;
  padding: 10px 24px;
  transition: all 0.25s;
}

.dialog-footer .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 15px rgba(0,0,0,0.1);
}

.el-dialog__wrapper {
  backdrop-filter: blur(4px);
  background: rgba(0,0,0,0.5) !important;
}
</style>