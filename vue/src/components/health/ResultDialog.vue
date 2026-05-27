<template>
  <el-dialog
    :model-value="modelValue"
    @update:model-value="handleVisibleChange"
    width="1200px"
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

            <div class="health-suggestion-card">
              <div class="prescription-header">
                <el-icon><Document /></el-icon>
                <span>AI 健康处方</span>
              </div>
              <div class="suggestion-section health-advice-section">
                <div class="section-indicator"></div>
                <div class="section-content">
                  <h4>健康建议</h4>
                  <p>{{ storeData.aiAdvice || getHealthAdvice(storeData.riskLevel) }}</p>
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
  RefreshRight
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
const loading = ref(false)

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
  grid-template-columns: 1fr 380px;
  gap: 24px;
}

.result-left-col {
  min-width: 0;
}

.result-right-col {
  display: flex;
  flex-direction: column;
  gap: 20px;
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

.health-suggestion-card {
  background: linear-gradient(135deg, #f0f5ff, #e8f4fd);
  border-radius: 14px;
  border: 1px solid #bae0ff;
  overflow: hidden;
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
  padding: 18px 20px;
}

.health-advice-section {
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

@media (max-width: 900px) {
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
  padding: 4px 0 !important;
}

.carousel-display .chart-header {
  margin-bottom: 4px !important;
}

.carousel-display .chart-title {
  font-size: 15px !important;
  margin-bottom: 0 !important;
}

.carousel-display .chart-subtitle {
  font-size: 12px !important;
  margin: 0 !important;
}

.carousel-display .chart-container {
  min-height: unset !important;
  height: 280px !important;
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

.carousel-display .risk-overlay {
  transform: translate(-50%, -30%) !important;
}

.carousel-display .risk-value {
  font-size: 36px !important;
}

.carousel-display .risk-label {
  font-size: 16px !important;
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
  padding: 8px !important;
  box-shadow: none !important;
  border-radius: 0 !important;
  background: transparent !important;
}
</style>
