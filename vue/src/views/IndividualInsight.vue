<template>
  <div class="insight-page">
    <h2>个体洞察</h2>

    <div style="margin: 20px 0">
      <el-card shadow="hover" style="position: relative;">
        <ProgressOverlay 
          :visible="loading"
          title="正在进行个体洞察分析"
          :steps="analysisSteps"
          :hints="analysisHints"
          color="#409eff"
        />
        <template #header>
          <div class="card-header">
            <span>患者选择</span>
            <el-select v-model="selectedHistoryId" placeholder="历史记录" clearable size="small" style="width: 200px" @change="loadHistoryRecord">
              <el-option v-for="h in historyList" :key="h.id" :label="formatHistoryLabel(h)" :value="h.id" />
            </el-select>
          </div>
        </template>
        <el-form :inline="true" label-width="100px">
          <el-form-item label="选择患者">
            <el-select
              v-model="selectedProfileId"
              placeholder="请选择患者健康档案"
              filterable
              style="width: 320px"
            >
              <el-option
                v-for="p in patientList"
                :key="p.id"
                :label="'档案#' + p.id + ' - 年龄:' + (p.age || '-') + ' 血糖:' + (p.glucose || '-')"
                :value="p.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" :loading="loading" @click="runInsight">
              开始分析
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <div v-if="insightData" class="insight-content">
      <div style="margin: 20px 0">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>患者概览</span>
            </div>
          </template>
          <div class="overview-container">
            <div class="overview-item" v-for="item in overviewData" :key="item.label">
              <div class="item-label">{{ item.label }}</div>
              <div class="item-value" :class="{ abnormal: item.abnormal }">{{ item.value }}</div>
              <div class="item-range">{{ item.range }}</div>
            </div>
          </div>
        </el-card>
      </div>

      <div style="margin: 20px 0">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>风险评估</span>
            </div>
          </template>
          <div class="risk-container">
            <div class="risk-gauge">
              <div ref="riskGaugeRef" class="gauge-chart"></div>
            </div>
            <div class="risk-info">
              <div class="risk-score" :style="{ color: riskColor }">{{ riskProbability }}%</div>
              <div class="risk-level" :class="riskLevelClass">{{ riskLevelText }}</div>
              <div v-if="insightData.confidenceInterval" class="confidence-interval">
                置信区间: {{ insightData.confidenceInterval[0] }}% - {{ insightData.confidenceInterval[1] }}%
              </div>
              <div class="risk-suggestion">{{ riskSuggestion }}</div>
            </div>
          </div>
        </el-card>
      </div>

      <div style="margin: 20px 0">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>特征贡献分析</span>
            </div>
          </template>
          <div ref="shapChartRef" class="chart-container"></div>
        </el-card>
      </div>

      <!-- 历史趋势图卡片 -->
      <div style="margin: 20px 0">
        <el-card shadow="hover" class="trend-card">
          <template #header>
            <div class="card-header">
              <span>历史趋势</span>
            </div>
          </template>
          <div v-if="hasTrendData" ref="trendChartRef" class="trend-chart-container"></div>
          <div v-else class="trend-placeholder">暂无足够历史数据展示趋势</div>
        </el-card>
      </div>

      <div style="margin: 20px 0">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>相似病例</span>
            </div>
          </template>
          <el-table :data="insightData.similarCases" border stripe empty-text="暂无相似病例数据">
            <el-table-column prop="id" label="档案ID" width="100" />
            <el-table-column prop="probability" label="风险概率" width="120">
              <template #default="scope">
                <span :style="{ color: getRiskColor(scope.row.riskLevel) }">
                  {{ scope.row.probability ? scope.row.probability.toFixed(1) + '%' : '-' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="riskLevel" label="风险等级" width="100">
              <template #default="scope">
                <el-tag :type="getRiskTagType(scope.row.riskLevel)" size="small">
                  {{ getRiskLabel(scope.row.riskLevel) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="age" label="年龄" width="80" />
            <el-table-column prop="gender" label="性别" width="80">
              <template #default="scope">
                {{ scope.row.gender === 'male' ? '男' : scope.row.gender === 'female' ? '女' : '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="predictedAt" label="预测时间" min-width="160" />
          </el-table>
        </el-card>
      </div>

      <div style="margin: 20px 0">
        <el-card shadow="hover" style="position: relative;">
          <ProgressOverlay 
            :visible="adviceLoading"
            title="正在生成AI健康建议"
            :steps="adviceSteps"
            :hints="adviceHints"
            color="#67c23a"
          />
          <template #header>
            <div class="card-header">
              <span>健康建议</span>
              <el-button type="primary" size="small" @click="generateAdvice" :loading="adviceLoading">
                生成AI建议
              </el-button>
            </div>
          </template>
          <div class="advice-content" v-if="healthAdvice">
            <div v-for="(item, index) in healthAdvice" :key="index" class="advice-item">
              <el-icon class="advice-icon"><Check /></el-icon>
              <span>{{ item }}</span>
            </div>
          </div>
          <div class="advice-placeholder" v-else>
            点击上方按钮生成个性化健康建议
          </div>
        </el-card>
      </div>

      <div style="margin: 20px 0">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>导出报告</span>
            </div>
          </template>
          <el-form label-width="120px">
            <el-form-item label="报告类型">
              <el-radio-group v-model="reportType">
                <el-radio value="diagnosis">诊断建议</el-radio>
                <el-radio value="health">健康管理</el-radio>
                <el-radio value="full">完整报告</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="exportReport">
                <el-icon><Download /></el-icon>
                导出PDF报告
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </div>

    <!-- 默认展示内容（未分析时） -->
    <div v-else-if="!loading" class="default-content">
      <!-- 功能概览卡片 -->
      <el-row :gutter="20" style="margin: 20px 0">
        <el-col :span="8">
          <el-card shadow="hover" class="overview-feature-card">
            <div class="feature-icon-wrapper" style="background: linear-gradient(135deg, #e6a23c, #f0c78a)">
              <el-icon class="feature-icon"><Odometer /></el-icon>
            </div>
            <h3>风险评估仪表盘</h3>
            <p>通过可视化仪表盘直观展示患者糖尿病风险概率，快速识别高风险患者</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="overview-feature-card">
            <div class="feature-icon-wrapper" style="background: linear-gradient(135deg, #409eff, #79bbff)">
              <el-icon class="feature-icon"><DataAnalysis /></el-icon>
            </div>
            <h3>特征贡献分析</h3>
            <p>深度解析各项健康指标对风险评估的影响程度，提供可解释的AI分析</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="overview-feature-card">
            <div class="feature-icon-wrapper" style="background: linear-gradient(135deg, #67c23a, #95d475)">
              <el-icon class="feature-icon"><Document /></el-icon>
            </div>
            <h3>AI健康建议</h3>
            <p>基于分析结果生成个性化健康建议，辅助医生制定精准治疗方案</p>
          </el-card>
        </el-col>
      </el-row>

      <!-- 示例图表展示 -->
      <el-row :gutter="20" style="margin: 20px 0">
        <el-col :span="12">
          <el-card shadow="hover" class="feature-card">
            <template #header>
              <div class="card-header">
                <span>风险仪表盘示例</span>
                <el-tag type="info" size="small">示例数据</el-tag>
              </div>
            </template>
            <div ref="demoGaugeRef" class="chart-container" style="height: 300px"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover" class="feature-card">
            <template #header>
              <div class="card-header">
                <span>特征贡献度示例</span>
                <el-tag type="info" size="small">示例数据</el-tag>
              </div>
            </template>
            <div ref="demoShapRef" class="chart-container" style="height: 300px"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 系统数据概览 -->
      <el-row :gutter="20" style="margin: 20px 0">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #409eff, #79bbff)">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ patientCount }}</div>
              <div class="stat-label">患者档案数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #67c23a, #95d475)">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ historyList.length }}</div>
              <div class="stat-label">历史分析记录</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #e6a23c, #f0c78a)">
              <el-icon><Odometer /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">85%</div>
              <div class="stat-label">模型准确率</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f56c6c, #f89898)">
              <el-icon><DataAnalysis /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">8</div>
              <div class="stat-label">分析维度</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 操作指南 -->
      <el-card shadow="hover" style="margin: 20px 0">
        <template #header>
          <div class="card-header">
            <span>快速上手指南</span>
          </div>
        </template>
        <el-steps :active="0" align-center>
          <el-step title="选择患者" description="从左侧下拉菜单选择患者健康档案" :icon="User" />
          <el-step title="开始分析" description="点击「开始分析」按钮，系统将自动进行风险评估" :icon="Search" />
          <el-step title="查看结果" description="查看风险评估仪表盘、特征贡献分析等详细结果" :icon="DataAnalysis" />
          <el-step title="生成建议" description="点击生成AI健康建议，辅助制定治疗方案" :icon="Document" />
        </el-steps>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Check, Download, Odometer, DataAnalysis, Document, User } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import request from '@/utils/request'
import { CacheHelper } from '@/utils/cacheHelper'
import ProgressOverlay from '@/components/common/ProgressOverlay.vue'

const selectedProfileId = ref(null)
const patientList = ref([])
const insightData = ref(null)
const loading = ref(false)
const adviceLoading = ref(false)
const healthAdvice = ref(null)
const reportType = ref('full')

const analysisSteps = [
  '加载患者档案',
  '计算风险指标',
  '生成特征分析',
  '渲染可视化图表'
]

const analysisHints = [
  '正在获取患者健康数据...',
  '基于机器学习模型计算风险概率...',
  '分析各特征对风险的贡献度...',
  '生成仪表盘和趋势图表...'
]

const adviceSteps = [
  '分析风险等级',
  '匹配医学知识库',
  '生成个性化建议',
  '整理输出结果'
]

const adviceHints = [
  '正在评估当前风险等级...',
  '从医学知识库中匹配相关建议...',
  '根据个人情况定制健康方案...',
  '整理并格式化建议内容...'
]

// 历史记录相关
const selectedHistoryId = ref(null)
const historyList = ref([])
const hasTrendData = ref(false)

// 图表引用
const riskGaugeRef = ref(null)
const shapChartRef = ref(null)
const trendChartRef = ref(null)
let riskGaugeChart = null
let shapChart = null
let trendChart = null

// 默认展示图表引用
const demoGaugeRef = ref(null)
const demoShapRef = ref(null)
let demoGaugeChart = null
let demoShapChart = null

const patientCount = computed(() => patientList.value.length)

const riskProbability = computed(() => {
  if (!insightData.value) return 0
  return insightData.value.probability ? insightData.value.probability.toFixed(1) : '0.0'
})

const riskLevelText = computed(() => {
  if (!insightData.value) return ''
  const level = insightData.value.riskLevel
  if (level === 'high') return '高风险'
  if (level === 'medium') return '中风险'
  return '低风险'
})

const riskColor = computed(() => {
  if (!insightData.value) return '#67c23a'
  return getRiskColor(insightData.value.riskLevel)
})

const riskLevelClass = computed(() => {
  if (!insightData.value) return ''
  const level = insightData.value.riskLevel
  if (level === 'high') return 'high-risk'
  if (level === 'medium') return 'medium-risk'
  return 'low-risk'
})

const riskSuggestion = computed(() => {
  if (!insightData.value) return ''
  const level = insightData.value.riskLevel
  if (level === 'high') return '建议立即就医，进行进一步检查和专业治疗'
  if (level === 'medium') return '建议控制饮食、增加运动，定期监测血糖水平'
  return '请继续保持健康的生活方式，定期体检'
})

const overviewData = computed(() => {
  if (!insightData.value || !insightData.value.profile) return []
  const p = insightData.value.profile
  return [
    { label: '血糖', value: (p.glucose || 0) + ' mg/dL', range: '正常: 70-110', abnormal: (p.glucose || 0) > 110 },
    { label: 'BMI', value: (p.bmi || 0).toFixed(1), range: '正常: 18.5-24.9', abnormal: (p.bmi || 0) > 24.9 },
    { label: '年龄', value: (p.age || 0) + ' 岁', range: '成年', abnormal: false },
    { label: '血压', value: (p.bloodPressure || 0) + ' mmHg', range: '正常: <120', abnormal: (p.bloodPressure || 0) > 120 },
    { label: '胰岛素', value: (p.insulin || 0) + ' μU/mL', range: '正常: 10-20', abnormal: (p.insulin || 0) > 20 },
    { label: '怀孕次数', value: String(p.pregnancies || 0), range: '0-10', abnormal: false }
  ]
})

function getRiskColor(level) {
  if (level === 'high') return '#f56c6c'
  if (level === 'medium') return '#e6a23c'
  return '#67c23a'
}

function getRiskTagType(level) {
  if (level === 'high') return 'danger'
  if (level === 'medium') return 'warning'
  return 'success'
}

function getRiskLabel(level) {
  if (level === 'high') return '高风险'
  if (level === 'medium') return '中风险'
  return '低风险'
}

async function loadPatientList() {
  try {
    const res = await request.get('/api/health-profile/list/by-doctor')
    if (res.code === '200') {
      patientList.value = res.data || []
    }
  } catch (e) {
    console.error('加载患者列表失败', e)
  }
}

async function runInsight() {
  if (!selectedProfileId.value) {
    ElMessage.warning('请先选择一个患者')
    return
  }
  loading.value = true
  insightData.value = null
  healthAdvice.value = null
  disposeDemoCharts()
  try {
    const res = await request.get(`/api/insight/${selectedProfileId.value}`)
    if (res.code === '200') {
      insightData.value = res.data
      ElMessage.success('分析完成')
      // 保存分析记录ID到本地缓存（后端返回analysisRecordId字段）
      if (res.data && res.data.analysisRecordId) {
        CacheHelper.setJson('last_individual_insight_id', res.data.analysisRecordId)
      }
      // 重新加载历史记录
      await loadHistoryList()
      await nextTick()
      renderRiskGauge()
      renderShapChart()
      // 加载趋势数据
      await loadTrendData()
    } else {
      ElMessage.error(res.msg || '分析失败')
    }
  } catch (e) {
    ElMessage.error('请求失败: ' + (e.message || '未知错误'))
  } finally {
    loading.value = false
    // 如果分析失败（insightData仍为空），重新渲染示例图表
    if (!insightData.value) {
      await nextTick()
      renderDemoCharts()
    }
  }
}

// 加载历史记录列表
async function loadHistoryList() {
  try {
    const res = await request.get('/api/insight/history?limit=20')
    if (res.code === '200') {
      historyList.value = res.data || []
    }
  } catch (e) {
    console.error('加载历史记录失败', e)
  }
}

// 加载指定历史记录
async function loadHistoryRecord(historyId) {
  if (!historyId) {
    selectedHistoryId.value = null
    return
  }
  const record = historyList.value.find(h => h.id === historyId)
  if (!record) return
  try {
    const resultData = typeof record.resultData === 'string' ? JSON.parse(record.resultData) : record.resultData
    insightData.value = resultData
    disposeDemoCharts()
    // 从结果中提取患者档案ID
    if (resultData.profile && resultData.profile.id) {
      selectedProfileId.value = resultData.profile.id
    } else if (record.profileIds) {
      const ids = typeof record.profileIds === 'string' ? JSON.parse(record.profileIds) : record.profileIds
      if (ids && ids.length > 0) {
        selectedProfileId.value = ids[0]
      }
    }
    await nextTick()
    renderRiskGauge()
    renderShapChart()
    // 加载趋势数据
    await loadTrendData()
    ElMessage.success('历史记录已加载')
  } catch (e) {
    ElMessage.error('加载历史记录失败: ' + (e.message || '未知错误'))
  }
}

// 格式化历史记录标签
function formatHistoryLabel(record) {
  const time = record.analysisTime ? new Date(record.analysisTime).toLocaleString() : '未知时间'
  const resultData = typeof record.resultData === 'string' ? JSON.parse(record.resultData) : record.resultData
  const riskLevel = resultData.riskLevel || '未知'
  const riskLabel = riskLevel === 'high' ? '高风险' : riskLevel === 'medium' ? '中风险' : '低风险'
  return `${time} - ${riskLabel}`
}

// 加载趋势数据
async function loadTrendData() {
  if (!selectedProfileId.value) return
  try {
    const res = await request.get('/api/insight/history?limit=10')
    if (res.code === '200') {
      const history = res.data || []
      // 筛选当前患者的历史记录
      const patientHistory = history.filter(record => {
        const resultData = typeof record.resultData === 'string' ? JSON.parse(record.resultData) : record.resultData
        return resultData.profile && resultData.profile.id === selectedProfileId.value
      })
      if (patientHistory.length >= 2) {
        renderTrendChart(patientHistory)
        hasTrendData.value = true
      } else {
        // 如果历史记录不足，隐藏趋势图
        if (trendChart) {
          trendChart.dispose()
          trendChart = null
        }
        hasTrendData.value = false
      }
    }
  } catch (e) {
    console.error('加载趋势数据失败', e)
  }
}

// 渲染趋势图
function renderTrendChart(historyData) {
  if (!trendChartRef.value) return
  if (trendChart) {
    trendChart.dispose()
  }
  trendChart = echarts.init(trendChartRef.value)
  
  // 如果没有传入数据，尝试从历史记录中获取
  if (!historyData) {
    // 这里可以后续扩展
    return
  }
  
  // 准备数据
  const times = []
  const probabilities = []
  
  historyData.forEach(record => {
    const resultData = typeof record.resultData === 'string' ? JSON.parse(record.resultData) : record.resultData
    const time = record.analysisTime ? new Date(record.analysisTime).toLocaleDateString() : ''
    const probability = resultData.probability || 0
    times.push(time)
    probabilities.push(probability)
  })
  
  // 反转数组使其按时间正序
  times.reverse()
  probabilities.reverse()
  
  trendChart.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: function(params) {
        const data = params[0]
        return `${data.axisValue}<br/>风险概率: ${data.value}%`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: times,
      axisLabel: { fontSize: 12 }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      axisLabel: { formatter: '{value}%', fontSize: 12 }
    },
    series: [{
      name: '风险概率',
      type: 'line',
      data: probabilities,
      smooth: true,
      lineStyle: { width: 3 },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
        ])
      },
      itemStyle: { color: '#409eff' },
      markLine: {
        silent: true,
        data: [
          { yAxis: 30, lineStyle: { color: '#67c23a', type: 'dashed' }, label: { formatter: '低风险线', position: 'insideEndTop' } },
          { yAxis: 70, lineStyle: { color: '#f56c6c', type: 'dashed' }, label: { formatter: '高风险线', position: 'insideEndTop' } }
        ]
      }
    }]
  })
}

function renderRiskGauge() {
  if (!riskGaugeRef.value || !insightData.value) return
  if (riskGaugeChart) {
    riskGaugeChart.dispose()
  }
  riskGaugeChart = echarts.init(riskGaugeRef.value)
  const probability = insightData.value.probability || 0
  riskGaugeChart.setOption({
    series: [{
      type: 'gauge',
      startAngle: 200,
      endAngle: -20,
      min: 0,
      max: 100,
      splitNumber: 10,
      axisLine: {
        lineStyle: {
          width: 20,
          color: [
            [0.3, '#67c23a'],
            [0.7, '#e6a23c'],
            [1, '#f56c6c']
          ]
        }
      },
      pointer: { itemStyle: { color: 'auto' } },
      axisTick: { distance: -20, length: 6, lineStyle: { color: '#fff', width: 1 } },
      splitLine: { distance: -24, length: 14, lineStyle: { color: '#fff', width: 2 } },
      axisLabel: { color: 'inherit', distance: 30, fontSize: 12 },
      detail: {
        valueAnimation: true,
        formatter: '{value}%',
        color: 'inherit',
        fontSize: 24,
        offsetCenter: [0, '60%']
      },
      title: { offsetCenter: [0, '85%'], fontSize: 14, color: '#666' },
      data: [{ value: probability.toFixed(1), name: '风险概率' }]
    }]
  })
}

function renderShapChart() {
  if (!shapChartRef.value || !insightData.value) return
  if (shapChart) {
    shapChart.dispose()
  }
  shapChart = echarts.init(shapChartRef.value)

  const featureNames = insightData.value.featureNames || []
  const featureImportance = insightData.value.featureImportance || []

  if (featureNames.length === 0 || featureImportance.length === 0) return

  const pairs = featureNames.map((name, i) => ({ name, value: featureImportance[i] || 0 }))
  pairs.sort((a, b) => Math.abs(a.value) - Math.abs(b.value))

  shapChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value' },
    yAxis: {
      type: 'category',
      data: pairs.map(p => p.name),
      axisLabel: { fontSize: 12 }
    },
    series: [{
      type: 'bar',
      data: pairs.map(p => ({
        value: p.value,
        itemStyle: { color: p.value > 0 ? '#f56c6c' : '#67c23a' }
      })),
      barWidth: '60%',
      label: { show: true, position: 'right', formatter: '{c}', fontSize: 11 }
    }]
  })
}

function generateAdvice() {
  if (!insightData.value) {
    ElMessage.warning('请先进行分析')
    return
  }
  adviceLoading.value = true
  setTimeout(() => {
    const level = insightData.value.riskLevel
    const advice = []
    if (level === 'high') {
      advice.push('建议立即前往内分泌科就诊，进行糖化血红蛋白(HbA1c)检测')
      advice.push('严格控制碳水化合物摄入，每餐主食不超过100g')
      advice.push('每天监测空腹和餐后2小时血糖')
      advice.push('开始规律运动，每天至少30分钟中等强度有氧运动')
      advice.push('遵医嘱用药，不可自行停药或减量')
    } else if (level === 'medium') {
      advice.push('建议进行口服葡萄糖耐量试验(OGTT)进一步确认')
      advice.push('控制饮食，减少高糖、高脂食物摄入')
      advice.push('每周至少进行150分钟中等强度运动')
      advice.push('每3个月监测一次血糖水平')
      advice.push('保持健康体重，BMI目标控制在24以下')
    } else {
      advice.push('继续保持健康的生活方式')
      advice.push('每年进行一次糖尿病筛查')
      advice.push('均衡饮食，多吃蔬菜水果和全谷物')
      advice.push('保持规律运动，每周至少3次')
      advice.push('避免吸烟和过量饮酒')
    }
    healthAdvice.value = advice
    adviceLoading.value = false
    ElMessage.success('健康建议已生成')
  }, 500)
}

function exportReport() {
  if (!insightData.value) {
    ElMessage.warning('请先进行分析')
    return
  }
  ElMessage.info('报告生成中...')
  const data = insightData.value
  const profile = data.profile || {}
  const reportHtml = `
    <html><head><meta charset="utf-8"><title>个体洞察报告</title>
    <style>body{font-family:Arial,sans-serif;padding:40px}h1{text-align:center;color:#333}table{width:100%;border-collapse:collapse;margin:20px 0}td,th{border:1px solid #ddd;padding:8px;text-align:center}th{background:#f5f5f5}.section{margin:20px 0}.high{color:#f56c6c}.medium{color:#e6a23c}.low{color:#67c23a}</style>
    </head><body>
    <h1>糖尿病风险个体洞察报告</h1>
    <div class="section"><h2>一、患者信息</h2><table>
    <tr><th>年龄</th><td>${profile.age || '-'}</td><th>性别</th><td>${profile.gender === 'male' ? '男' : profile.gender === 'female' ? '女' : '-'}</td></tr>
    <tr><th>血糖</th><td>${profile.glucose || '-'} mg/dL</td><th>BMI</th><td>${profile.bmi || '-'}</td></tr>
    <tr><th>血压</th><td>${profile.bloodPressure || '-'} mmHg</td><th>胰岛素</th><td>${profile.insulin || '-'} μU/mL</td></tr>
    </table></div>
    <div class="section"><h2>二、风险评估结果</h2>
    <p><strong>风险概率：</strong><span class="${data.riskLevel}">${data.probability}%</span></p>
    <p><strong>风险等级：</strong>${getRiskLabel(data.riskLevel)}</p>
    ${data.confidenceInterval ? '<p><strong>置信区间：</strong>' + data.confidenceInterval[0] + '% - ' + data.confidenceInterval[1] + '%</p>' : ''}
    <p><strong>临床建议：</strong>${riskSuggestion.value}</p></div>
    <div class="section"><h2>三、健康建议</h2><ul>${(healthAdvice.value || []).map(a => '<li>' + a + '</li>').join('')}</ul></div>
    <div class="section"><p style="text-align:right;color:#999">报告生成时间：${new Date().toLocaleString()}</p></div>
    </body></html>`
  const blob = new Blob([reportHtml], { type: 'text/html;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = '个体洞察报告_' + new Date().toISOString().slice(0, 10) + '.html'
  a.click()
  URL.revokeObjectURL(url)
  ElMessage.success('报告已导出（HTML格式，可用浏览器打印为PDF）')
}

onMounted(async () => {
  loadPatientList()
  window.addEventListener('resize', handleResize)
  
  // 渲染默认示例图表
  await nextTick()
  renderDemoCharts()
  
  // 尝试自动恢复上次分析结果
  try {
    const lastInsightId = CacheHelper.getJson('last_individual_insight_id')
    if (lastInsightId) {
      const res = await request.get('/api/insight/history?limit=1')
      if (res.code === '200' && res.data && res.data.length > 0) {
        const lastRecord = res.data[0]
        if (lastRecord.id === lastInsightId) {
          const resultData = typeof lastRecord.resultData === 'string' ? JSON.parse(lastRecord.resultData) : lastRecord.resultData
          insightData.value = resultData
          disposeDemoCharts()
          if (resultData.profile && resultData.profile.id) {
            selectedProfileId.value = resultData.profile.id
          } else if (lastRecord.profileIds) {
            const ids = typeof lastRecord.profileIds === 'string' ? JSON.parse(lastRecord.profileIds) : lastRecord.profileIds
            if (ids && ids.length > 0) {
              selectedProfileId.value = ids[0]
            }
          }
          await nextTick()
          renderRiskGauge()
          renderShapChart()
          await loadTrendData()
        }
      }
    }
    await loadHistoryList()
  } catch (e) {
    console.error('自动恢复历史记录失败', e)
  }
})

onBeforeUnmount(() => {
  if (riskGaugeChart) riskGaugeChart.dispose()
  if (shapChart) shapChart.dispose()
  if (trendChart) trendChart.dispose()
  disposeDemoCharts()
  window.removeEventListener('resize', handleResize)
})

function renderDemoCharts() {
  // 示例仪表盘
  if (demoGaugeRef.value) {
    if (demoGaugeChart) demoGaugeChart.dispose()
    demoGaugeChart = echarts.init(demoGaugeRef.value)
    demoGaugeChart.setOption({
      series: [{
        type: 'gauge',
        startAngle: 200,
        endAngle: -20,
        min: 0,
        max: 100,
        splitNumber: 10,
        axisLine: {
          lineStyle: {
            width: 20,
            color: [[0.3, '#67c23a'], [0.7, '#e6a23c'], [1, '#f56c6c']]
          }
        },
        pointer: { itemStyle: { color: 'auto' } },
        axisTick: { distance: -20, length: 6, lineStyle: { color: '#fff', width: 1 } },
        splitLine: { distance: -24, length: 14, lineStyle: { color: '#fff', width: 2 } },
        axisLabel: { color: 'inherit', distance: 30, fontSize: 12 },
        detail: {
          valueAnimation: true,
          formatter: '{value}%',
          color: 'inherit',
          fontSize: 24,
          offsetCenter: [0, '60%']
        },
        title: { offsetCenter: [0, '85%'], fontSize: 14, color: '#666' },
        data: [{ value: 42.5, name: '风险概率' }]
      }]
    })
  }

  // 示例特征贡献图
  if (demoShapRef.value) {
    if (demoShapChart) demoShapChart.dispose()
    demoShapChart = echarts.init(demoShapRef.value)
    const features = ['BMI', '血糖', '年龄', '血压', '胰岛素', '怀孕次数', '皮肤厚度', '家族史']
    const values = [0.32, 0.28, 0.18, 0.15, 0.12, 0.08, 0.05, 0.03]
    demoShapChart.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: '3%', right: '10%', bottom: '3%', containLabel: true },
      xAxis: { type: 'value' },
      yAxis: { type: 'category', data: features, axisLabel: { fontSize: 12 } },
      series: [{
        type: 'bar',
        data: values.map(v => ({
          value: v,
          itemStyle: { color: v > 0.2 ? '#f56c6c' : v > 0.1 ? '#e6a23c' : '#67c23a' }
        })),
        barWidth: '60%',
        label: { show: true, position: 'right', formatter: '{c}', fontSize: 11 }
      }]
    })
  }
}

function disposeDemoCharts() {
  if (demoGaugeChart) { demoGaugeChart.dispose(); demoGaugeChart = null }
  if (demoShapChart) { demoShapChart.dispose(); demoShapChart = null }
}

function handleResize() {
  if (riskGaugeChart) riskGaugeChart.resize()
  if (shapChart) shapChart.resize()
  if (trendChart) trendChart.resize()
  if (demoGaugeChart) demoGaugeChart.resize()
  if (demoShapChart) demoShapChart.resize()
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.overview-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 20px;
}

.overview-item {
  text-align: center;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.overview-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.item-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.item-value {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
}

.item-value.abnormal {
  color: #f56c6c;
}

.item-range {
  font-size: 12px;
  color: #999;
}

.risk-container {
  display: flex;
  align-items: center;
  gap: 40px;
}

.risk-gauge {
  flex: 1;
}

.gauge-chart {
  height: 250px;
  width: 100%;
}

.risk-info {
  flex: 1;
  text-align: center;
}

.risk-score {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 10px;
}

.risk-level {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
}

.risk-level.high-risk { color: #f56c6c; }
.risk-level.medium-risk { color: #e6a23c; }
.risk-level.low-risk { color: #67c23a; }

.confidence-interval {
  font-size: 13px;
  color: #909399;
  margin-bottom: 10px;
}

.risk-suggestion {
  font-size: 14px;
  color: #666;
}

.chart-container {
  height: 350px;
  width: 100%;
}

.advice-content {
  padding: 15px;
}

.advice-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  padding: 8px 12px;
  background-color: #f0f9eb;
  border-radius: 4px;
}

.advice-icon {
  color: #67c23a;
  margin-right: 10px;
  flex-shrink: 0;
}

.advice-placeholder {
  padding: 40px;
  text-align: center;
  color: #999;
  background-color: #f5f7fa;
  border-radius: 4px;
}

/* 按钮样式增强 */
.el-button--primary {
  border-radius: 20px;
  padding: 10px 24px;
}

/* feature-card 统一样式 */
.feature-card {
  border-radius: 14px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 历史趋势卡片特殊样式 */
.trend-card {
  border-left: 4px solid #409eff;
}

.trend-chart-container {
  height: 300px;
  width: 100%;
}

.trend-placeholder {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 14px;
}

/* 默认展示内容样式 */
.default-content {
  margin: 20px 0;
}

.overview-feature-card {
  text-align: center;
  padding: 20px 10px;
  border-radius: 14px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.overview-feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.overview-feature-card h3 {
  margin: 15px 0 8px;
  font-size: 16px;
  color: #303133;
}

.overview-feature-card p {
  font-size: 13px;
  color: #909399;
  line-height: 1.6;
}

.feature-icon-wrapper {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}

.feature-icon {
  font-size: 30px;
  color: #fff;
}

.stat-card {
  border-radius: 14px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon .el-icon {
  font-size: 24px;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}
</style>
