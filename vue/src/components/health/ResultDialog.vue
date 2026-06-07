<template>
  <el-dialog
    :model-value="modelValue"
    @update:model-value="handleVisibleChange"
    width="1280px"
    :close-on-click-modal="false"
    custom-class="result-dialog"
    :show-close="true"
    :before-close="handleBeforeClose"
    :append-to-body="true"
    destroy-on-close
  >
    <div class="result-container" v-loading="loading" element-loading-text="正在加载检测结果..." element-loading-background="rgba(255,255,255,0.9)">
      <template v-if="!loading">
        <div class="result-header" :class="getRiskClass(storeData.riskLevel)">
          <div class="result-header-main">
            <span class="risk-level-badge" :class="getRiskClass(storeData.riskLevel)">
              <el-icon class="risk-icon"><Warning v-if="storeData.riskLevel === 'high'" /><InfoFilled v-else-if="storeData.riskLevel === 'medium'" /><CircleCheck v-else /></el-icon>
              {{ getRiskText(storeData.riskLevel) }}
            </span>
            <span class="probability-main">
              <span class="probability-label">患病概率</span>
              <span class="probability-value">{{ storeData.riskProbability || 0 }}<span class="probability-unit">%</span></span>
            </span>
          </div>
          <div v-if="storeData.confidenceInterval && storeData.confidenceInterval[0] > 0" class="confidence-interval">
            置信区间: {{ storeData.confidenceInterval[0] }}% - {{ storeData.confidenceInterval[1] }}%
          </div>
        </div>

        <div class="result-body-two-col">
          <div class="result-left-col">
            <div class="charts-carousel" v-if="availableCharts.length > 0">
              <div class="carousel-header">
                <h4>可视化分析</h4>
                <span class="carousel-indicators">
                  <span v-for="(chart, index) in availableCharts" :key="index"
                    :class="['indicator-dot', { active: currentChartIndex === index }]"
                    @click="goToChart(index)"></span>
                </span>
              </div>
              <div class="carousel-main">
                <button class="carousel-btn prev" @click="prevChart" :disabled="availableCharts.length <= 1">
                  <el-icon><ArrowLeft /></el-icon>
                </button>
                <div class="carousel-display">
                  <component
                    v-if="availableCharts[currentChartIndex]"
                    :is="getChartComponent(availableCharts[currentChartIndex].key)"
                    class="chart-component"
                  />
                  <div v-else class="chart-placeholder">
                    <el-icon class="empty-icon"><TrendCharts /></el-icon>
                    <p>暂无图表数据</p>
                  </div>
                </div>
                <button class="carousel-btn next" @click="nextChart" :disabled="availableCharts.length <= 1">
                  <el-icon><ArrowRight /></el-icon>
                </button>
              </div>
              <div class="carousel-thumbnails">
                <div v-for="(chart, index) in availableCharts" :key="index"
                  :class="['thumbnail', { active: currentChartIndex === index }]"
                  @click="goToChart(index)">
                  <span class="thumbnail-label">{{ chart.label }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="result-right-col">
            <div class="collapse-panel data-details">
              <div class="panel-header" @click="showDataDetails = !showDataDetails">
                <span class="panel-title">数据详情</span>
                <span class="panel-arrow" :class="{ expanded: showDataDetails }"><el-icon><ArrowDown /></el-icon></span>
              </div>
              <Transition name="panel-slide">
                <div class="panel-content" v-show="showDataDetails">
                  <el-descriptions :column="1" size="small" border>
                    <el-descriptions-item label="年龄">{{ storeData.age || '-' }}岁</el-descriptions-item>
                    <el-descriptions-item label="BMI">{{ storeData.bmi || '-' }}</el-descriptions-item>
                    <el-descriptions-item label="空腹血糖">{{ storeData.glucose || '-' }} mg/dL</el-descriptions-item>
                    <el-descriptions-item label="血压">{{ storeData.bloodPressure || '-' }} mmHg</el-descriptions-item>
                    <el-descriptions-item label="胰岛素">{{ storeData.insulin || '-' }} mU/L</el-descriptions-item>
                    <el-descriptions-item label="遗传系数">{{ storeData.diabetesPedigreeFunction || '-' }}</el-descriptions-item>
                  </el-descriptions>
                </div>
              </Transition>
            </div>

            <div class="collapse-panel percentile-panel">
              <div class="panel-header" @click="showPercentiles = !showPercentiles">
                <span class="panel-title">指标百分位</span>
                <span class="panel-arrow" :class="{ expanded: showPercentiles }"><el-icon><ArrowDown /></el-icon></span>
              </div>
              <Transition name="panel-slide">
                <div class="panel-content" v-show="showPercentiles">
                  <el-select
                    v-model="selectedPercentileKey"
                    placeholder="选择指标"
                    size="default"
                    class="percentile-select"
                  >
                    <el-option
                      v-for="item in percentileOptions"
                      :key="item.key"
                      :label="item.label"
                      :value="item.key"
                    />
                  </el-select>
                  <div class="percentile-display" v-if="selectedPercentileValue !== null">
                    <div class="percentile-bar-wrapper">
                      <div class="percentile-bar-track">
                        <div class="percentile-bar-fill" :style="{ width: selectedPercentileValue + '%', background: percentileColor }"></div>
                        <div class="percentile-bar-marker" :style="{ left: selectedPercentileValue + '%', borderColor: percentileColor }"></div>
                      </div>
                      <div class="percentile-labels">
                        <span>0%</span>
                        <span>50%</span>
                        <span>100%</span>
                      </div>
                    </div>
                    <div class="percentile-value-text">
                      <span class="percentile-number" :style="{ color: percentileColor }">{{ selectedPercentileValue }}%</span>
                      <span class="percentile-desc">{{ percentileDescription }}</span>
                    </div>
                  </div>
                  <div v-else class="percentile-empty">暂无该指标的百分位数据</div>
                </div>
              </Transition>
            </div>

            <div class="health-suggestion-card">
              <div class="prescription-header">
                <el-icon><Document /></el-icon>
                <span>AI 健康处方</span>
              </div>
              <div class="prescription-body">
                <div class="suggestion-section diet-section">
                  <div class="section-indicator" style="background: linear-gradient(180deg, #52C41A, #95DE64);"></div>
                  <div class="section-content">
                    <h4><el-icon class="section-icon"><Food /></el-icon> 饮食建议</h4>
                    <ul class="advice-list">
                      <li v-for="(item, idx) in dietAdvice" :key="idx">{{ item }}</li>
                    </ul>
                  </div>
                </div>
                <div class="suggestion-section exercise-section">
                  <div class="section-indicator" style="background: linear-gradient(180deg, #FA8C16, #FFA940);"></div>
                  <div class="section-content">
                    <h4><el-icon class="section-icon"><Bicycle /></el-icon> 运动建议</h4>
                    <ul class="advice-list">
                      <li v-for="(item, idx) in exerciseAdvice" :key="idx">{{ item }}</li>
                    </ul>
                  </div>
                </div>
                <div class="suggestion-section checkup-section">
                  <div class="section-indicator" style="background: linear-gradient(180deg, #722ED1, #B37FEB);"></div>
                  <div class="section-content">
                    <h4><el-icon class="section-icon"><Timer /></el-icon> 定期检查</h4>
                    <ul class="advice-list">
                      <li v-for="(item, idx) in checkupAdvice" :key="idx">{{ item }}</li>
                    </ul>
                  </div>
                </div>
                <div class="suggestion-section lifestyle-section">
                  <div class="section-indicator" style="background: linear-gradient(180deg, #13C2C2, #5CDBD3);"></div>
                  <div class="section-content">
                    <h4><el-icon class="section-icon"><Sunny /></el-icon> 生活习惯</h4>
                    <ul class="advice-list">
                      <li v-for="(item, idx) in lifestyleAdvice" :key="idx">{{ item }}</li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>
    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
      <el-button type="primary" @click="handleReEvaluate">
        <el-icon><RefreshRight /></el-icon> 重新评估
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import {
  ArrowLeft,
  ArrowRight,
  ArrowDown,
  Document,
  TrendCharts,
  Warning,
  InfoFilled,
  CircleCheck,
  RefreshRight,
  Food,
  Bicycle,
  Timer,
  Sunny
} from '@element-plus/icons-vue'
import { useChartStore } from '@/store/chartStore'
import { usePrediction } from '@/composables/usePrediction'
import {
  RiskGaugeChart,
  HealthRadarChart,
  IndicatorCompareChart,
  HealthScoreChart,
  RiskHeatmapChart,
  FactorWaterfallChart,
  ConfidenceIntervalChart,
  RiskDistributionChart,
  FeatureImportanceChart
} from '@/components/charts'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  storeData: {
    type: Object,
    default: () => ({
      riskLevel: 'low',
      riskProbability: 0,
      confidenceInterval: [0, 0],
      age: 0,
      bmi: 0,
      glucose: 0,
      bloodPressure: 0,
      insulin: 0,
      diabetesPedigreeFunction: 0,
      aiAdvice: ''
    })
  }
})

const emit = defineEmits(['update:modelValue', 're-evaluate'])

const chartStore = useChartStore()
const { getRiskText, getRiskClass, getHealthAdvice } = usePrediction()

const currentChartIndex = ref(0)
const showDataDetails = ref(false)
const showPercentiles = ref(false)
const selectedPercentileKey = ref('')
const loading = ref(false)

const percentileNameMap = {
  glucose: '空腹血糖',
  bmi: 'BMI',
  blood_pressure: '血压',
  insulin: '胰岛素',
  age: '年龄',
  dpf: '遗传系数',
  pregnancies: '怀孕次数',
  skin_thickness: '皮肤厚度'
}

const percentileOptions = computed(() => {
  const data = chartStore.percentilesData
  if (!data || Object.keys(data).length === 0) return []
  return Object.keys(data).map(key => ({
    key,
    label: percentileNameMap[key] || key
  }))
})

const selectedPercentileValue = computed(() => {
  if (!selectedPercentileKey.value) return null
  const data = chartStore.percentilesData
  if (!data) return null
  return data[selectedPercentileKey.value] ?? null
})

const percentileColor = computed(() => {
  const val = selectedPercentileValue.value
  if (val === null) return '#909399'
  if (val <= 30) return '#52C41A'
  if (val <= 70) return '#FA8C16'
  return '#F5222D'
})

const percentileDescription = computed(() => {
  const val = selectedPercentileValue.value
  if (val === null) return ''
  if (val <= 25) return '处于较低水平'
  if (val <= 50) return '处于中等偏低水平'
  if (val <= 75) return '处于中等偏高水平'
  return '处于较高水平'
})

const dietAdviceMap = {
  low: [
    '保持均衡饮食，每日三餐定时定量',
    '多摄入蔬菜、全谷物和优质蛋白',
    '控制精制糖和高脂食物的摄入'
  ],
  medium: [
    '减少精制碳水化合物，选择低GI食物',
    '每餐蔬菜占一半，控制主食份量',
    '避免含糖饮料，选择白水或无糖茶'
  ],
  high: [
    '严格控制碳水化合物摄入量',
    '咨询营养师制定个性化饮食方案',
    '记录每日饮食，监控餐后血糖变化'
  ]
}

const exerciseAdviceMap = {
  low: [
    '每周至少150分钟中等强度有氧运动',
    '选择步行、游泳、骑车等适合自己的运动',
    '保持日常活动量，减少久坐时间'
  ],
  medium: [
    '每周运动不少于5天，每次30分钟以上',
    '结合有氧运动和适量力量训练',
    '运动前后监测血糖，随身携带糖果'
  ],
  high: [
    '在医生指导下制定运动计划',
    '从低强度开始，循序渐进增加运动量',
    '避免空腹运动，运动中注意身体反应'
  ]
}

const checkupAdviceMap = {
  low: [
    '每年进行一次空腹血糖和糖化血红蛋白检查',
    '定期监测血压、血脂等基础指标',
    '保持健康体检的良好习惯'
  ],
  medium: [
    '每3-6个月检查一次血糖相关指标',
    '关注眼底、肾功能等并发症筛查',
    '定期复查并记录指标变化趋势'
  ],
  high: [
    '立即就医进行全面糖尿病评估',
    '每1-3个月复查糖化血红蛋白',
    '定期进行眼底、足部、肾功能筛查'
  ]
}

const lifestyleAdviceMap = {
  low: [
    '保持规律作息，每晚7-8小时充足睡眠',
    '学会管理压力，保持积极乐观心态',
    '戒烟限酒，远离不良生活习惯'
  ],
  medium: [
    '保证充足睡眠，避免熬夜',
    '学习压力管理技巧，如冥想或深呼吸',
    '戒烟并严格限制酒精摄入'
  ],
  high: [
    '建立严格的作息规律，保证睡眠质量',
    '寻求心理支持，积极应对疾病压力',
    '彻底戒烟戒酒，配合治疗方案'
  ]
}

const dietAdvice = computed(() => dietAdviceMap[props.storeData.riskLevel] || dietAdviceMap.low)
const exerciseAdvice = computed(() => exerciseAdviceMap[props.storeData.riskLevel] || exerciseAdviceMap.low)
const checkupAdvice = computed(() => checkupAdviceMap[props.storeData.riskLevel] || checkupAdviceMap.low)
const lifestyleAdvice = computed(() => lifestyleAdviceMap[props.storeData.riskLevel] || lifestyleAdviceMap.low)

const chartComponentMap = {
  riskGauge: RiskGaugeChart,
  healthRadar: HealthRadarChart,
  indicatorCompare: IndicatorCompareChart,
  healthScore: HealthScoreChart,
  riskHeatmap: RiskHeatmapChart,
  factorWaterfall: FactorWaterfallChart,
  confidenceInterval: ConfidenceIntervalChart,
  riskDistribution: RiskDistributionChart,
  featureImportance: FeatureImportanceChart
}

const availableCharts = computed(() => {
  return [
    { key: 'riskGauge', label: '风险仪表盘' },
    { key: 'healthRadar', label: '健康雷达图' },
    { key: 'indicatorCompare', label: '指标对比' },
    { key: 'healthScore', label: '健康评分' },
    { key: 'riskHeatmap', label: '风险热力图' },
    { key: 'factorWaterfall', label: '因素贡献' },
    { key: 'confidenceInterval', label: '置信区间' },
    { key: 'riskDistribution', label: '风险分布' },
    { key: 'featureImportance', label: '特征重要性' }
  ]
})

watch(() => props.modelValue, (visible) => {
  if (visible) {
    loading.value = true
    currentChartIndex.value = 0
    showDataDetails.value = false
    showPercentiles.value = false
    selectedPercentileKey.value = ''
    setTimeout(() => {
      loading.value = false
    }, 600)
  }
})

function getChartComponent(key) {
  return chartComponentMap[key] || null
}

function prevChart() {
  if (availableCharts.value.length <= 1) return
  currentChartIndex.value = (currentChartIndex.value - 1 + availableCharts.value.length) % availableCharts.value.length
}

function nextChart() {
  if (availableCharts.value.length <= 1) return
  currentChartIndex.value = (currentChartIndex.value + 1) % availableCharts.value.length
}

function goToChart(index) {
  if (index >= 0 && index < availableCharts.value.length) {
    currentChartIndex.value = index
  }
}

function handleVisibleChange(val) {
  emit('update:modelValue', val)
}

function handleBeforeClose(done) {
  done()
}

function handleClose() {
  emit('update:modelValue', false)
}

function handleReEvaluate() {
  emit('update:modelValue', false)
  emit('re-evaluate')
}
</script>

<style scoped>
.result-container {
  padding: 0;
  min-height: 200px;
}

.result-header {
  padding: 28px 32px;
  border-radius: 14px;
  margin-bottom: 24px;
  text-align: center;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.result-header::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 200px;
  height: 200px;
  border-radius: 50%;
  opacity: 0.08;
  pointer-events: none;
}

.result-header.low-risk {
  background: linear-gradient(135deg, #f0f9eb, #e8f8e0);
  border: 1px solid #b7eb8f;
}
.result-header.low-risk::before {
  background: #52c41a;
}

.result-header.medium-risk {
  background: linear-gradient(135deg, #fff7e6, #fff1cc);
  border: 1px solid #ffd666;
}
.result-header.medium-risk::before {
  background: #faad14;
}

.result-header.high-risk {
  background: linear-gradient(135deg, #fff1f0, #ffd8d2);
  border: 1px solid #ffa39e;
}
.result-header.high-risk::before {
  background: #f5222d;
}

.result-header-main {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
  flex-wrap: wrap;
  position: relative;
  z-index: 1;
}

.risk-level-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 26px;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
}

.risk-icon {
  font-size: 18px;
}

.risk-level-badge.low-risk {
  background: linear-gradient(135deg, #52c41a, #73d13d);
  color: #fff;
  box-shadow: 0 4px 12px rgba(82, 196, 26, 0.3);
}

.risk-level-badge.medium-risk {
  background: linear-gradient(135deg, #faad14, #ffc53d);
  color: #fff;
  box-shadow: 0 4px 12px rgba(250, 173, 20, 0.3);
}

.risk-level-badge.high-risk {
  background: linear-gradient(135deg, #f5222d, #ff4d4f);
  color: #fff;
  box-shadow: 0 4px 12px rgba(245, 34, 45, 0.3);
}

.probability-main {
  display: inline-flex;
  align-items: baseline;
  gap: 10px;
}

.probability-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.probability-value {
  font-size: 36px;
  font-weight: 700;
  color: #1D2129;
  line-height: 1;
  letter-spacing: -0.5px;
}

.probability-unit {
  font-size: 18px;
  font-weight: 500;
  color: #606266;
  margin-left: 2px;
}

.confidence-interval {
  margin-top: 12px;
  font-size: 13px;
  color: #909399;
  background: rgba(255, 255, 255, 0.7);
  display: inline-block;
  padding: 5px 18px;
  border-radius: 14px;
  backdrop-filter: blur(4px);
  position: relative;
  z-index: 1;
}

.result-body-two-col {
  display: grid;
  grid-template-columns: 1fr 420px;
  gap: 24px;
  align-items: start;
}

.result-left-col {
  min-width: 0;
}

.result-right-col {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.charts-carousel {
  background: #ffffff;
  border-radius: 14px;
  border: 1px solid #e8ecf1;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.02);
}

.carousel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}

.carousel-header h4 {
  margin: 0;
  font-size: 15px;
  color: #1D2129;
  font-weight: 600;
}

.carousel-indicators {
  display: flex;
  gap: 6px;
}

.indicator-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #d9d9d9;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.indicator-dot.active {
  background: #409EFF;
  width: 22px;
  border-radius: 4px;
}

.carousel-main {
  display: flex;
  align-items: center;
  gap: 14px;
}

.carousel-btn {
  flex-shrink: 0;
  width: 38px;
  height: 38px;
  border-radius: 50%;
  border: 1px solid #e8ecf1;
  background: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: #606266;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.carousel-btn:hover:not(:disabled) {
  border-color: #409EFF;
  color: #409EFF;
  background: #ecf5ff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
  transform: scale(1.05);
}

.carousel-btn:active:not(:disabled) {
  transform: scale(0.95);
}

.carousel-btn:disabled {
  opacity: 0.35;
  cursor: not-allowed;
}

.carousel-display {
  flex: 1;
  min-width: 0;
  height: 380px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-component {
  width: 100%;
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  scrollbar-width: thin;
  scrollbar-color: #d4d7dc transparent;
}
.chart-component::-webkit-scrollbar {
  width: 5px;
}
.chart-component::-webkit-scrollbar-thumb {
  background: #d4d7dc;
  border-radius: 3px;
}
.chart-component::-webkit-scrollbar-track {
  background: transparent;
}

.chart-placeholder {
  text-align: center;
  color: #909399;
  padding: 40px;
}

.chart-placeholder .empty-icon {
  font-size: 48px;
  color: #c0c4cc;
  margin-bottom: 12px;
}

.chart-placeholder p {
  margin: 0;
  font-size: 14px;
}

.carousel-thumbnails {
  display: flex;
  gap: 8px;
  margin-top: 18px;
  flex-wrap: wrap;
}

.thumbnail {
  padding: 7px 16px;
  border-radius: 8px;
  font-size: 12px;
  color: #606266;
  background: #f5f7fa;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;
}

.thumbnail:hover {
  background: #ecf5ff;
  color: #409EFF;
  border-color: #d4e6ff;
}

.thumbnail.active {
  background: #409EFF;
  color: #fff;
  border-color: #409EFF;
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.25);
}

.thumbnail-label {
  white-space: nowrap;
}

.collapse-panel {
  background: #ffffff;
  border-radius: 14px;
  border: 1px solid #e8ecf1;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.02);
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  cursor: pointer;
  user-select: none;
  transition: background 0.2s;
}

.panel-header:hover {
  background: #f8f9fb;
}

.panel-title {
  font-size: 14px;
  font-weight: 600;
  color: #1D2129;
}

.panel-arrow {
  font-size: 14px;
  color: #909399;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: inline-flex;
  align-items: center;
}

.panel-arrow.expanded {
  transform: rotate(180deg);
}

.panel-content {
  padding: 0 20px 18px;
}

.panel-slide-enter-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.panel-slide-leave-active {
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.panel-slide-enter-from {
  opacity: 0;
  max-height: 0;
  padding-top: 0;
  padding-bottom: 0;
}

.panel-slide-enter-to {
  opacity: 1;
  max-height: 500px;
}

.panel-slide-leave-from {
  opacity: 1;
  max-height: 500px;
}

.panel-slide-leave-to {
  opacity: 0;
  max-height: 0;
  padding-top: 0;
  padding-bottom: 0;
}

.percentile-select {
  width: 100%;
  margin-bottom: 14px;
}

.percentile-bar-wrapper {
  margin-bottom: 10px;
}

.percentile-bar-track {
  position: relative;
  height: 8px;
  background: #f0f2f5;
  border-radius: 4px;
  overflow: visible;
}

.percentile-bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.percentile-bar-marker {
  position: absolute;
  top: -4px;
  width: 16px;
  height: 16px;
  background: #fff;
  border: 2px solid currentColor;
  border-radius: 50%;
  transform: translateX(-50%);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.15);
  transition: left 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.percentile-labels {
  display: flex;
  justify-content: space-between;
  margin-top: 6px;
  font-size: 11px;
  color: #909399;
}

.percentile-value-text {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.percentile-number {
  font-size: 24px;
  font-weight: 700;
  line-height: 1;
}

.percentile-desc {
  font-size: 13px;
  color: #606266;
}

.percentile-empty {
  font-size: 13px;
  color: #909399;
  text-align: center;
  padding: 12px 0;
}

.health-suggestion-card {
  background: linear-gradient(135deg, #f0f5ff, #e8f4fd);
  border-radius: 14px;
  border: 1px solid #bae0ff;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.prescription-body {
  overflow-y: auto;
  max-height: 420px;
  scrollbar-width: thin;
  scrollbar-color: #d4d7dc transparent;
}
.prescription-body::-webkit-scrollbar {
  width: 5px;
}
.prescription-body::-webkit-scrollbar-thumb {
  background: #c0d0e0;
  border-radius: 3px;
}
.prescription-body::-webkit-scrollbar-track {
  background: transparent;
}

.prescription-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 20px;
  background: rgba(64, 158, 255, 0.06);
  font-size: 15px;
  font-weight: 600;
  color: #1D2129;
  border-bottom: 1px solid #d6eaff;
}

.prescription-header .el-icon {
  font-size: 18px;
  color: #409EFF;
}

.suggestion-section {
  padding: 14px 20px;
  border-bottom: 1px solid #f0f4ff;
}

.suggestion-section:last-child {
  border-bottom: none;
}

.health-advice-section {
  display: flex;
  gap: 14px;
}

.diet-section,
.exercise-section,
.checkup-section,
.lifestyle-section {
  display: flex;
  gap: 14px;
}

.section-indicator {
  flex-shrink: 0;
  width: 4px;
  background: linear-gradient(180deg, #409EFF, #36cfc9);
  border-radius: 2px;
}

.section-content {
  flex: 1;
}

.section-content h4 {
  margin: 0 0 10px;
  font-size: 14px;
  color: #303133;
  font-weight: 600;
}

.section-content p {
  margin: 0;
  font-size: 13px;
  color: #4e5969;
  line-height: 1.8;
}

.section-icon {
  font-size: 15px;
  vertical-align: -2px;
  margin-right: 2px;
}

.advice-list {
  margin: 0;
  padding: 0 0 0 16px;
  list-style: none;
}

.advice-list li {
  position: relative;
  font-size: 13px;
  color: #4e5969;
  line-height: 1.7;
  padding-left: 12px;
  margin-bottom: 6px;
}

.advice-list li:last-child {
  margin-bottom: 0;
}

.advice-list li::before {
  content: '';
  position: absolute;
  left: 0;
  top: 8px;
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: #c0c4cc;
}

.diet-section .advice-list li::before {
  background: #52C41A;
}

.exercise-section .advice-list li::before {
  background: #FA8C16;
}

.checkup-section .advice-list li::before {
  background: #722ED1;
}

.lifestyle-section .advice-list li::before {
  background: #13C2C2;
}

:deep(.el-descriptions__title) {
  font-size: 13px;
}

:deep(.el-descriptions__label) {
  font-weight: 500;
  color: #606266;
  width: 100px;
  background: #fafbfc;
}

:deep(.el-descriptions__content) {
  color: #303133;
}

:deep(.el-loading-mask) {
  border-radius: 14px;
}

:deep(.el-loading-spinner .el-loading-text) {
  color: #4e5969;
  font-size: 14px;
  margin-top: 12px;
}

:deep(.el-loading-spinner .circular) {
  width: 36px;
  height: 36px;
}

@media (max-width: 1000px) {
  .result-body-two-col {
    grid-template-columns: 1fr;
  }
  .result-right-col {
    order: -1;
  }
  .probability-value {
    font-size: 28px;
  }
}
</style>

<style>
.result-dialog .el-dialog__header {
  display: none;
}

.result-dialog .el-dialog__body {
  padding: 28px 28px 10px;
}

.result-dialog .el-dialog__footer {
  padding: 16px 28px 24px;
  border-top: 1px solid #eef2f6;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.result-dialog {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.12);
}

.carousel-display .chart-component {
  background: transparent !important;
  box-shadow: none !important;
  border-radius: 0 !important;
  border: none !important;
  padding: 0 !important;
}

.carousel-display .chart-header {
  margin-bottom: 2px !important;
}

.carousel-display .chart-title {
  font-size: 14px !important;
  margin-bottom: 0 !important;
}

.carousel-display .chart-subtitle {
  font-size: 11px !important;
  margin: 0 !important;
}

.carousel-display .chart-container {
  min-height: unset !important;
  height: 260px !important;
}

.carousel-display .risk-description {
  display: none !important;
}

.carousel-display .chart-controls {
  display: none !important;
}

.carousel-display .indicators-legend {
  display: none !important;
}

.carousel-display .data-source {
  display: none !important;
}

.carousel-display .chart-legend {
  display: none !important;
}

.carousel-display .factor-details {
  display: none !important;
}

.carousel-display .data-summary {
  display: none !important;
}

.carousel-display .risk-info-below {
  padding: 4px 0 0 !important;
}

.carousel-display .risk-value {
  font-size: 26px !important;
  line-height: 1.1 !important;
}

.carousel-display .risk-label {
  font-size: 12px !important;
  margin-top: 1px !important;
}

.carousel-display .confidence-interval {
  font-size: 10px !important;
  margin-top: 4px !important;
  padding: 2px 10px !important;
}

.carousel-display .progress-mode {
  display: none !important;
}

.carousel-display .center-overlay {
  transform: translate(-50%, -40%) !important;
}

.carousel-display .probability-circle {
  width: 80px !important;
  height: 80px !important;
}

.carousel-display .probability-value {
  font-size: 20px !important;
}

.carousel-display .risk-legend {
  display: none !important;
}

.carousel-display .risk-advice {
  display: none !important;
}

.carousel-display .risk-gauge-chart,
.carousel-display .health-radar-chart,
.carousel-display .indicator-compare-chart,
.carousel-display .risk-distribution-chart,
.carousel-display .factor-waterfall-chart {
  padding: 8px 12px !important;
  box-shadow: none !important;
  border-radius: 8px !important;
  background: linear-gradient(180deg, #fafbfd 0%, #f5f8ff 100%) !important;
  border: 1px solid #eef2f8 !important;
}
</style>
