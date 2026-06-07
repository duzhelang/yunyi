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
              </el-radio-group>
              <el-button type="primary" link class="family-dpf-link" @click="goToDpfCalculator">
                精确计算 (DPF) <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
            <div class="family-simple-row">
              <el-radio-group v-model="familyHistorySimple">
                <el-radio value="无">无</el-radio>
                <el-radio value="有">有</el-radio>
              </el-radio-group>
              <span class="family-precision-warning">
                <el-icon><Warning /></el-icon> 选"有/无"会丢失遗传风险精度
              </span>
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

    <!-- 隐私计算声明 -->
    <div class="privacy-notice">
      <el-alert
        title="隐私计算声明"
        type="success"
        :closable="false"
        show-icon
        description="🔒 您的健康数据仅在本地浏览器处理，不上传至服务器。所有计算均在本地完成，确保您的隐私安全。"
      />
      <el-alert
        title="本地推理保障"
        type="info"
        :closable="false"
        show-icon
        style="margin-top: 12px"
        description="数据不出域，模型本地运行。您的敏感健康信息永远不会离开您的设备，我们采用端侧AI技术实现完全本地化的风险评估。"
      />
    </div>

    <!-- 结果对话框 -->
    <el-dialog v-model="resultDialogVisible" width="1200px" :close-on-click-modal="false" custom-class="result-dialog" :show-close="true" append-to-body>
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

        <!-- 图表轮播区域 -->
        <div class="charts-carousel" v-if="showECharts">
          <div class="carousel-header">
            <h4>数据可视化</h4>
            <div class="carousel-indicators">
              <span 
                v-for="(chart, index) in chartItems" 
                :key="index"
                class="indicator-dot"
                :class="{ active: activeChartIndex === index }"
                @click="goToChart(index)"
              ></span>
            </div>
          </div>
          <div class="carousel-main">
            <button class="carousel-btn" @click="prevChart" :disabled="activeChartIndex === 0">
              <el-icon><ArrowLeft /></el-icon>
            </button>
            <div class="carousel-display">
              <div 
                v-for="(chart, index) in chartItems" 
                :key="index"
                class="echart-item"
                :class="{ 'chart-active': activeChartIndex === index }"
              >
                <h4 class="chart-title">{{ chart.title }}</h4>
                <div :ref="el => setChartRef(el, index)" class="echart-box"></div>
              </div>
            </div>
            <button class="carousel-btn" @click="nextChart" :disabled="activeChartIndex === chartItems.length - 1">
              <el-icon><ArrowRight /></el-icon>
            </button>
          </div>
          <div class="carousel-thumbnails">
            <div 
              v-for="(chart, index) in chartItems" 
              :key="index"
              class="thumbnail"
              :class="{ active: activeChartIndex === index }"
              @click="goToChart(index)"
            >
              <img v-if="thumbnails[index]" :src="thumbnails[index]" :alt="chart.title" />
              <div v-else class="thumbnail-placeholder"></div>
              <span class="thumbnail-label">{{ chart.title }}</span>
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
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import * as echarts from 'echarts'
import { ArrowDown, ArrowLeft, ArrowRight, Document, Warning } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useHealthStore } from '@/store/healthStore'
import { usePrediction } from '@/composables/usePrediction'

const router = useRouter()
const store = useHealthStore()
const { runPrediction, getRiskText, getRiskClass, getHealthAdvice, clearCache } = usePrediction()

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
const familyHistoryType = ref('simple')
const familyHistorySimple = ref('无')

const showECharts = ref(false)
const activeChartIndex = ref(0)
const thumbnails = ref([])
const chartRefs = ref([])

const chartItems = [
  { title: '风险仪表盘', key: 'gauge' },
  { title: '健康雷达图', key: 'radar' },
  { title: '指标对比', key: 'comparison' },
  { title: '指标偏离分析', key: 'waterfall' }
]

const riskLevelClass = computed(() => getRiskClass(store.riskLevel))

onMounted(() => {
  store.loadFromDraft()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})

function showResult() {
  activeChartIndex.value = 0
  resultDialogVisible.value = true
  nextTick(() => initECharts())
}

function renderGaugeChart(chart) {
  const riskColor = store.riskProbability >= 60 ? '#F56C6C' : store.riskProbability >= 30 ? '#E6A23C' : '#67C23A'
  chart.setOption({
    series: [{
      type: 'gauge',
      startAngle: 210,
      endAngle: -30,
      min: 0,
      max: 100,
      splitNumber: 10,
      itemStyle: { color: riskColor },
      progress: {
        show: true,
        width: 18,
        roundCap: true,
        itemStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 1, y2: 0,
            colorStops: [
              { offset: 0, color: '#67C23A' },
              { offset: 0.5, color: '#E6A23C' },
              { offset: 1, color: '#F56C6C' }
            ]
          }
        }
      },
      pointer: {
        icon: 'path://M12.8,0.7l12,40.1H0.7L12.8,0.7z',
        length: '55%',
        width: 8,
        offsetCenter: [0, '-10%'],
        itemStyle: { color: 'auto', shadowColor: 'rgba(0,0,0,0.2)', shadowBlur: 5 }
      },
      axisLine: {
        lineStyle: {
          width: 18,
          color: [[0.3, '#67C23A'], [0.7, '#E6A23C'], [1, '#F56C6C']]
        },
        roundCap: true
      },
      axisTick: { distance: -22, length: 6, lineStyle: { color: '#fff', width: 1 } },
      splitLine: { distance: -25, length: 12, lineStyle: { color: '#fff', width: 2 } },
      axisLabel: { color: '#666', distance: 30, fontSize: 11 },
      detail: {
        valueAnimation: true,
        formatter: '{value}%',
        fontSize: 28,
        fontWeight: 'bold',
        color: riskColor,
        offsetCenter: [0, '40%'],
        textStyle: { textShadowColor: 'rgba(0,0,0,0.1)', textShadowBlur: 5 }
      },
      title: {
        offsetCenter: [0, '65%'],
        fontSize: 13,
        color: '#999'
      },
      data: [{ value: store.riskProbability, name: '患病概率' }],
      animationDuration: 1500,
      animationEasingUpdate: 'cubicOut'
    }]
  })
}

function renderRadarChart(chart) {
  const featureNames = ['血糖', 'BMI', '血压', '胰岛素', '皮褶厚度', '年龄', '遗传']
  const userValues = [
    Math.min(store.glucose / 200 * 100, 100),
    Math.min(store.bmi / 40 * 100, 100),
    Math.min(store.bloodPressure / 200 * 100, 100),
    Math.min(store.insulin / 200 * 100, 100),
    Math.min(store.skinThickness / 50 * 100, 100),
    Math.min(store.age / 100 * 100, 100),
    Math.min(store.diabetesPedigreeFunction / 2.5 * 100, 100)
  ]
  const normalValues = [45, 55, 60, 25, 40, 35, 20]
  const radarColor = store.riskProbability >= 60 ? 'rgba(245,108,108,' : store.riskProbability >= 30 ? 'rgba(230,162,60,' : 'rgba(103,194,58,'
  chart.setOption({
    radar: {
      indicator: featureNames.map(name => ({ name, max: 100 })),
      radius: '65%',
      center: ['50%', '55%'],
      nameGap: 8,
      nameTextStyle: { color: '#666', fontSize: 12, fontWeight: 500 },
      splitArea: {
        areaStyle: {
          color: ['rgba(64,128,255,0.02)', 'rgba(64,128,255,0.05)', 'rgba(64,128,255,0.02)', 'rgba(64,128,255,0.05)', 'rgba(64,128,255,0.02)']
        }
      },
      axisLine: { lineStyle: { color: 'rgba(64,128,255,0.15)' } },
      splitLine: { lineStyle: { color: 'rgba(64,128,255,0.1)' } }
    },
    series: [{
      type: 'radar',
      data: [
        {
          value: userValues,
          name: '您的指标',
          symbol: 'circle',
          symbolSize: 6,
          lineStyle: { width: 2.5, color: radarColor + '0.8)', shadowColor: radarColor + '0.3)', shadowBlur: 8 },
          areaStyle: { color: { type: 'radial', x: 0.5, y: 0.5, r: 0.5, colorStops: [{ offset: 0, color: radarColor + '0.4)' }, { offset: 1, color: radarColor + '0.05)' }] } },
          itemStyle: { color: radarColor + '1)', borderColor: '#fff', borderWidth: 2 }
        },
        {
          value: normalValues,
          name: '正常参考',
          symbol: 'diamond',
          symbolSize: 5,
          lineStyle: { width: 1.5, type: 'dashed', color: 'rgba(144,147,153,0.6)' },
          areaStyle: { color: 'rgba(144,147,153,0.05)' },
          itemStyle: { color: 'rgba(144,147,153,0.8)' }
        }
      ],
      animationDuration: 1500
    }],
    tooltip: {
      trigger: 'item',
      formatter: function(params) {
        let html = `<b>${params.name}</b><br/>`
        params.value.forEach((val, i) => {
          html += `${featureNames[i]}: ${val.toFixed(1)}%<br/>`
        })
        return html
      }
    },
    legend: {
      bottom: 0,
      itemWidth: 12,
      itemHeight: 12,
      textStyle: { color: '#666', fontSize: 11 }
    }
  })
}

function renderComparisonChart(chart) {
  const metricLabels = ['血糖', 'BMI', '血压', '胰岛素']
  const userMetricValues = [store.glucose, store.bmi, store.bloodPressure, store.insulin]
  const refValues = [90, 22, 120, 50]
  const refRanges = [[70, 100], [18.5, 24], [90, 140], [16, 166]]
  chart.setOption({
    grid: { left: '3%', right: '5%', bottom: '10%', top: '15%', containLabel: true },
    xAxis: {
      type: 'category',
      data: metricLabels,
      axisLine: { lineStyle: { color: '#E5E7EB' } },
      axisTick: { show: false },
      axisLabel: { color: '#666', fontSize: 12 }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#F0F0F0', type: 'dashed' } }
    },
    tooltip: {
      trigger: 'axis',
      formatter: function(params) {
        let html = `<b>${params[0].name}</b><br/>`
        params.forEach(p => {
          html += `${p.marker} ${p.seriesName}: <b>${p.value}</b><br/>`
        })
        const idx = params[0].dataIndex
        html += `正常范围: ${refRanges[idx][0]} ~ ${refRanges[idx][1]}`
        return html
      }
    },
    legend: {
      top: 0,
      itemWidth: 12,
      itemHeight: 12,
      textStyle: { color: '#666', fontSize: 11 }
    },
    series: [
      {
        name: '您的值',
        type: 'bar',
        data: userMetricValues.map((v, i) => ({
          value: v,
          itemStyle: {
            color: v >= refRanges[i][0] && v <= refRanges[i][1]
              ? { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: '#4080FF' }, { offset: 1, color: '#79BBFF' }] }
              : { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: '#F56C6C' }, { offset: 1, color: '#F89898' }] },
            borderRadius: [4, 4, 0, 0]
          }
        })),
        barWidth: '25%',
        animationDuration: 1000,
        animationDelay: function(idx) { return idx * 100 }
      },
      {
        name: '正常参考',
        type: 'bar',
        data: refValues,
        barWidth: '25%',
        itemStyle: { color: 'rgba(144,147,153,0.3)', borderRadius: [4, 4, 0, 0] },
        animationDuration: 1000,
        animationDelay: function(idx) { return idx * 100 + 200 }
      }
    ]
  })
}

function renderWaterfallChart(chart) {
  const featureMap = { Pregnancies: '怀孕次数', Glucose: '血糖', BloodPressure: '血压',
    SkinThickness: '皮褶厚度', Insulin: '胰岛素', BMI: 'BMI',
    DiabetesPedigreeFunction: '遗传', Age: '年龄' }

  let chartData
  if (store.featureImportance.length > 0 && store.featureNames.length > 0) {
    const labels = store.featureNames.map(n => featureMap[n] || n)
    chartData = store.featureImportance.map((v, i) => ({ label: labels[i], value: v }))
      .sort((a, b) => Math.abs(b.value) - Math.abs(a.value))
  } else if (Object.keys(store.percentiles).length > 0) {
    chartData = Object.entries(store.percentiles).map(([key, val]) => ({
      label: featureMap[key] || key,
      value: (val - 50) / 50
    })).sort((a, b) => Math.abs(b.value) - Math.abs(a.value))
  } else {
    return
  }
  chart.setOption({
    grid: { left: '3%', right: '8%', bottom: '15%', top: '10%', containLabel: true },
    xAxis: {
      type: 'category',
      data: chartData.map(d => d.label),
      axisLabel: { rotate: 35, fontSize: 11, color: '#666' },
      axisLine: { lineStyle: { color: '#E5E7EB' } },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      name: '偏离程度',
      nameTextStyle: { color: '#999', fontSize: 11 },
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#F0F0F0', type: 'dashed' } }
    },
    tooltip: {
      trigger: 'axis',
      formatter: function(params) {
        const p = params[0]
        const direction = p.value >= 0 ? '偏高 ↑' : '偏低 ↓'
        return `${p.name}<br/>偏离度: <b>${p.value >= 0 ? '+' : ''}${(p.value * 100).toFixed(1)}%</b><br/>${direction}`
      }
    },
    series: [{
      type: 'bar',
      data: chartData.map(d => ({
        value: d.value,
        itemStyle: {
          color: d.value >= 0
            ? { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: '#F56C6C' }, { offset: 1, color: '#F89898' }] }
            : { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: '#67C23A' }, { offset: 1, color: '#95D475' }] },
          borderRadius: d.value >= 0 ? [4, 4, 0, 0] : [0, 0, 4, 4]
        }
      })),
      barWidth: '50%',
      animationDuration: 1200,
      animationEasing: 'elasticOut',
      label: {
        show: true,
        position: 'top',
        formatter: function(p) { return (p.value >= 0 ? '+' : '') + (p.value * 100).toFixed(0) + '%' },
        fontSize: 10,
        color: '#999'
      }
    }]
  })
}

function initECharts() {
  showECharts.value = true
  nextTick(() => {
    requestAnimationFrame(() => {
      const renderFns = [renderGaugeChart, renderRadarChart, renderComparisonChart, renderWaterfallChart]
      chartRefs.value.forEach((el, i) => {
        if (!el) return
        let chart = echarts.getInstanceByDom(el)
        if (!chart) chart = echarts.init(el)
        if (renderFns[i]) renderFns[i](chart)
      })

      window.removeEventListener('resize', handleResize)
      window.addEventListener('resize', handleResize)

      requestAnimationFrame(() => {
        chartRefs.value.forEach((_, i) => generateThumbnail(i))
      })
    })
  })
}

function generateThumbnail(index) {
  const el = chartRefs.value[index]
  if (!el) return
  const chart = echarts.getInstanceByDom(el)
  if (!chart) return
  try {
    const url = chart.getDataURL({ type: 'png', pixelRatio: 2, backgroundColor: '#F8FAFC' })
    thumbnails.value[index] = url
  } catch (e) {
    // ignore
  }
}

function handleResize() {
  chartRefs.value.forEach(el => {
    if (el) {
      const chart = echarts.getInstanceByDom(el)
      chart?.resize()
    }
  })
}

function setChartRef(el, index) {
  if (el) {
    chartRefs.value[index] = el
  }
}

function initLazyChart(index) {
  const el = chartRefs.value[index]
  if (!el) return null
  let chart = echarts.getInstanceByDom(el)
  if (chart) {
    chart.resize()
    return chart
  }
  chart = echarts.init(el)
  const key = chartItems[index].key
  if (key === 'gauge') renderGaugeChart(chart)
  else if (key === 'radar') renderRadarChart(chart)
  else if (key === 'comparison') renderComparisonChart(chart)
  else if (key === 'waterfall') renderWaterfallChart(chart)
  requestAnimationFrame(() => chart.resize())
  return chart
}

function navigateToChart(index) {
  activeChartIndex.value = index
  nextTick(() => {
    requestAnimationFrame(() => {
      const chart = initLazyChart(index)
      chart?.resize()
    })
  })
}

function prevChart() {
  if (activeChartIndex.value > 0) {
    navigateToChart(activeChartIndex.value - 1)
  }
}

function nextChart() {
  if (activeChartIndex.value < chartItems.length - 1) {
    navigateToChart(activeChartIndex.value + 1)
  }
}

function goToChart(index) {
  navigateToChart(index)
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
  if (result.fromCache) {
    ElMessage.success('已从缓存加载结果')
  }
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
  clearCache()
}

function downloadReport() {
  const featureMap = { Pregnancies: '怀孕次数', Glucose: '血糖', BloodPressure: '血压',
    SkinThickness: '皮褶厚度', Insulin: '胰岛素', BMI: 'BMI',
    DiabetesPedigreeFunction: '遗传', Age: '年龄' }
  let featureData = ''
  if (store.featureImportance.length > 0 && store.featureNames.length > 0) {
    featureData = store.featureNames.map((name, i) => {
      const label = featureMap[name] || name
      const impact = store.featureImportance[i]
      return `<tr><td>${label}</td><td>${impact.toFixed(4)}</td><td style="color:${impact >= 0 ? '#f56c6c' : '#67c23a'}">${impact >= 0 ? '↑ 升高风险' : '↓ 降低风险'}</td></tr>`
    }).join('')
  } else if (Object.keys(store.percentiles).length > 0) {
    featureData = Object.entries(store.percentiles).map(([key, val]) => {
      const label = featureMap[key] || key
      const deviation = val - 50
      return `<tr><td>${label}</td><td>${val}%</td><td style="color:${deviation >= 0 ? '#f56c6c' : '#67c23a'}">${deviation >= 0 ? '偏高' : '偏低'}</td></tr>`
    }).join('')
  }

  const lifestyleItems = [
    { label: '运动频率', value: getExerciseText(store.exerciseFrequency) },
    { label: '饮食习惯', value: getDietText(store.dietHabit) },
    { label: '吸烟情况', value: store.smoking },
    { label: '饮酒情况', value: store.drinking },
    { label: '身高', value: store.height + ' m' },
    { label: '体重', value: store.weight + ' kg' }
  ].map(item => `<div class="info-item"><span class="info-label">${item.label}:</span> ${item.value}</div>`).join('')

  const htmlContent = `<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>糖尿病风险评估报告</title>
<style>
  body { font-family: 'Microsoft YaHei', Arial, sans-serif; max-width: 800px; margin: 0 auto; padding: 20px; color: #333; }
  h1 { color: #4080FF; text-align: center; border-bottom: 3px solid #4080FF; padding-bottom: 10px; }
  h2 { color: #333; margin-top: 24px; border-left: 4px solid #4080FF; padding-left: 10px; }
  .header { background: linear-gradient(135deg, #f5f7fa, #c3cfe2); padding: 20px; border-radius: 10px; margin-bottom: 20px; }
  .risk-level { font-size: 24px; font-weight: bold; }
  .probability { font-size: 36px; color: #4080FF; font-weight: bold; }
  .info-grid { display: grid; grid-template-template: 1fr 1fr; gap: 10px; grid-template-columns: 1fr 1fr; }
  .info-item { background: #f8f9fa; padding: 10px; border-radius: 5px; }
  .info-label { font-weight: bold; color: #666; }
  .advice { background: #ecf5ff; padding: 15px; border-radius: 8px; border-left: 4px solid #4080FF; margin: 20px 0; line-height: 1.8; }
  table { width: 100%; border-collapse: collapse; margin: 12px 0; }
  th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
  th { background: #f5f7fa; font-weight: bold; }
  .footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #ddd; color: #999; text-align: center; }
</style>
</head>
<body>
  <div class="header">
    <h1>糖尿病风险评估报告</h1>
    <p style="text-align:center; color:#666;">评估时间: ${new Date().toLocaleString()}</p>
    <div style="text-align:center; margin: 20px 0;">
      <div class="risk-level" style="color:${store.riskLevel === 'high' ? '#f56c6c' : store.riskLevel === 'medium' ? '#e6a23c' : '#67c23a'}">${riskLevelText.value}</div>
      <div class="probability">${probability.value}%</div>
      ${confidenceInterval.value[0] > 0 ? `<p style="color:#999">置信区间: ${confidenceInterval.value[0]}% - ${confidenceInterval.value[1]}%</p>` : ''}
    </div>
  </div>
  <h2>核心指标</h2>
  <div class="info-grid">
    <div class="info-item"><span class="info-label">年龄:</span> ${store.age}岁</div>
    <div class="info-item"><span class="info-label">性别:</span> ${store.gender}</div>
    <div class="info-item"><span class="info-label">BMI:</span> ${store.bmi}</div>
    <div class="info-item"><span class="info-label">空腹血糖:</span> ${store.glucose} mg/dL</div>
    <div class="info-item"><span class="info-label">血压:</span> ${store.bloodPressure} mmHg</div>
    <div class="info-item"><span class="info-label">胰岛素:</span> ${store.insulin} uIU/mL</div>
    <div class="info-item"><span class="info-label">家族史DPF:</span> ${store.diabetesPedigreeFunction}</div>
  </div>
  <h2>生活方式</h2>
  <div class="info-grid">${lifestyleItems}</div>
  <h2>指标分析</h2>
  <table>
    <thead><tr><th>特征</th><th>值</th><th>影响方向</th></tr></thead>
    <tbody>${featureData || '<tr><td colspan="3">暂无数据</td></tr>'}</tbody>
  </table>
  <h2>健康建议</h2>
  <div class="advice">${healthAdvice.value}</div>
  <div class="footer">
    <p>本评估结果仅供参考，不能替代专业医疗诊断。如有疑虑，请咨询专业医生。</p>
    <p>生成时间: ${new Date().toLocaleString()}</p>
  </div>
</body></html>`
  const blob = new Blob([htmlContent], { type: 'text/html;charset=utf-8' })
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

.privacy-notice {
  margin-top: 20px;
  padding: 0 20px;
}

.privacy-notice .el-alert {
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.privacy-notice .el-alert:first-child {
  background: linear-gradient(135deg, rgba(82, 196, 26, 0.08), rgba(64, 128, 255, 0.04));
}

.privacy-notice .el-alert:last-child {
  background: linear-gradient(135deg, rgba(64, 128, 255, 0.08), rgba(82, 196, 26, 0.04));
}

.privacy-notice .el-alert__title {
  font-weight: 600;
  font-size: 14px;
}

.privacy-notice .el-alert__description {
  font-size: 13px;
  line-height: 1.6;
  margin-top: 4px;
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
  padding: 28px 32px;
  border-radius: 20px;
  margin-bottom: 24px;
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
  font-size: 20px;
  font-weight: bold;
  padding: 10px 20px;
  border-radius: 10px;
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
  font-size: 36px;
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
  margin-top: 12px;
  font-size: 14px;
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
  margin-bottom: 16px;
  background: white;
  border-radius: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px;
  background: #F8FAFC;
  cursor: pointer;
  transition: background-color 0.2s;
}

.panel-header:hover {
  background: #F0F4F8;
}

.panel-title {
  font-size: 16px;
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
  padding: 14px 18px;
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
  gap: 12px;
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
  min-width: 90px;
  font-size: 14px;
  color: #374151;
}

.percentile-item .el-progress {
  flex: 1;
}

.percentile-desc {
  min-width: 60px;
  font-size: 13px;
  color: #6B7280;
  text-align: right;
}

.health-suggestion-card {
  border: 1px solid rgba(64,128,255,0.15);
  border-radius: 14px;
  overflow: hidden;
}

.prescription-header {
  background: linear-gradient(90deg, #e6f7ff, #f0f9ff);
  padding: 12px 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
  font-size: 15px;
  color: #4080FF;
  border-bottom: 1px dashed #4080FF;
}

.suggestion-section {
  display: flex;
  padding: 20px;
}

.section-indicator {
  width: 5px;
  border-radius: 3px;
  margin-right: 14px;
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
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
  color: #374151;
}

.section-content p {
  margin: 0;
  font-size: 14px;
  line-height: 1.7;
  color: #4B5563;
}

.section-content ul {
  margin: 0;
  padding-left: 18px;
}

.section-content li {
  margin-bottom: 8px;
  font-size: 14px;
  line-height: 1.6;
  color: #4B5563;
}

.section-content li:last-child {
  margin-bottom: 0;
}

.section-divider {
  height: 1px;
  background: rgba(64, 128, 255, 0.1);
  margin: 0 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
}

.dialog-footer .el-button {
  border-radius: 10px;
  padding: 12px 24px;
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

.charts-carousel {
  margin-bottom: 24px;
  background: #F8FAFC;
  border-radius: 16px;
  border: 1px solid rgba(64, 128, 255, 0.1);
  overflow: hidden;
}

.carousel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
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
  gap: 8px;
}

.indicator-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #D1D5DB;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.indicator-dot.active {
  background: #4080FF;
  transform: scale(1.2);
  box-shadow: 0 0 8px rgba(64, 128, 255, 0.4);
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
  width: 40px;
  height: 40px;
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
  box-shadow: 0 4px 12px rgba(64, 128, 255, 0.3);
}

.carousel-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.carousel-display {
  flex: 1;
  min-height: 400px;
  background: #f8fafc;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  border: 1px solid #E5E7EB;
}

.echart-item {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  visibility: hidden;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.25s ease;
}

.echart-item.chart-active {
  position: relative;
  visibility: visible;
  opacity: 1;
  pointer-events: auto;
}

.chart-title {
  margin: 0 0 14px 0;
  font-size: 15px;
  font-weight: 600;
  color: #374151;
  text-align: center;
  letter-spacing: 1px;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(64, 128, 255, 0.08);
}

.echart-box {
  flex: 1;
  width: 100%;
  min-height: 350px;
}

.carousel-thumbnails {
  display: flex;
  gap: 12px;
  padding: 12px 16px;
  background: #F8FAFC;
  border-top: 1px solid rgba(64, 128, 255, 0.08);
  overflow-x: auto;
}

.carousel-thumbnails::-webkit-scrollbar {
  height: 6px;
}

.carousel-thumbnails::-webkit-scrollbar-track {
  background: #F0F4F8;
}

.carousel-thumbnails::-webkit-scrollbar-thumb {
  background: linear-gradient(90deg, rgba(64, 128, 255, 0.3), rgba(64, 128, 255, 0.15));
  border-radius: 3px;
}

.carousel-thumbnails::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(90deg, rgba(64, 128, 255, 0.5), rgba(64, 128, 255, 0.3));
}

.thumbnail {
  flex-shrink: 0;
  width: 160px;
  padding: 10px;
  background: white;
  border-radius: 12px;
  cursor: pointer;
  border: 2px solid #E5E7EB;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.thumbnail:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(64,128,255,0.18);
  border-color: #79BBFF;
}

.thumbnail.active {
  border-color: #4080FF;
  box-shadow: 0 0 0 3px rgba(64,128,255,0.25), 0 4px 12px rgba(64,128,255,0.15);
  background: #F0F5FF;
}

.thumbnail img {
  width: 100%;
  height: 80px;
  object-fit: contain;
  display: block;
  border-radius: 8px;
}

.thumbnail-placeholder {
  width: 100%;
  height: 80px;
  background: linear-gradient(135deg, #E5E7EB, #F3F4F6);
  border-radius: 8px;
}

.thumbnail-label {
  display: block;
  font-size: 12px;
  font-weight: 500;
  color: #374151;
  text-align: center;
  margin-top: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.result-dialog {
  border-radius: 24px !important;
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
  padding: 20px 28px;
}

.result-dialog .el-dialog__body {
  padding: 24px 28px;
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
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 8px;
}

.family-radio-row .el-radio-group .el-radio {
  margin-right: 16px;
}

.family-dpf-link {
  font-size: 14px;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 4px;
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