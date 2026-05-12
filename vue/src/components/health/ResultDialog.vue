<template>
  <!-- 风险检测结果弹窗 -->
  <el-dialog
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    width="1200px"
    :close-on-click-modal="false"
    custom-class="result-dialog"
    :show-close="true"
  >
    <div class="result-container">
      <!-- 结果头部：风险等级 + 患病概率 -->
      <div class="result-header" :class="getRiskClass(storeData.riskLevel)">
        <div class="result-header-main">
          <span class="risk-level-badge" :class="getRiskClass(storeData.riskLevel)">{{ getRiskText(storeData.riskLevel) }}</span>
          <span class="probability-main">
            <span class="probability-label">患病概率</span>
            <span class="probability-value">{{ storeData.riskProbability }}%</span>
          </span>
        </div>
        <div v-if="storeData.confidenceInterval && storeData.confidenceInterval[0] > 0" class="confidence-interval">
          置信区间: {{ storeData.confidenceInterval[0] }}% - {{ storeData.confidenceInterval[1] }}%
        </div>
      </div>

      <!-- 主内容区域：双列布局 -->
      <div class="result-body-two-col">
        <!-- 左侧：图表轮播区域 -->
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
                <!-- 使用ECharts图表组件动态渲染 -->
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

        <!-- 右侧：数据详情 + AI 健康处方 -->
        <div class="result-right-col">
          <!-- 数据详情面板 -->
          <div class="collapse-panel data-details">
            <div class="panel-header" @click="showDataDetails = !showDataDetails">
              <span class="panel-title">数据详情</span>
              <span class="panel-arrow" :class="{ expanded: showDataDetails }"><el-icon><ArrowDown /></el-icon></span>
            </div>
            <Transition name="panel-slide">
              <div class="panel-content" v-show="showDataDetails">
                <el-descriptions :column="1" size="small" border>
                  <el-descriptions-item label="年龄">{{ storeData.age }}岁</el-descriptions-item>
                  <el-descriptions-item label="BMI">{{ storeData.bmi }}</el-descriptions-item>
                  <el-descriptions-item label="空腹血糖">{{ storeData.glucose }} mg/dL</el-descriptions-item>
                  <el-descriptions-item label="血压">{{ storeData.bloodPressure }} mmHg</el-descriptions-item>
                  <el-descriptions-item label="胰岛素">{{ storeData.insulin }} mU/L</el-descriptions-item>
                  <el-descriptions-item label="遗传系数">{{ storeData.diabetesPedigreeFunction }}</el-descriptions-item>
                </el-descriptions>
              </div>
            </Transition>
          </div>

          <!-- AI健康处方 -->
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
    </div>
    <template #footer>
      <el-button @click="$emit('update:modelValue', false)">关闭</el-button>
      <el-button type="primary" @click="$emit('re-evaluate')">重新评估</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import {
  ArrowLeft,
  ArrowRight,
  ArrowDown,
  Document,
  TrendCharts
} from '@element-plus/icons-vue'
import { useChartStore } from '@/store/chartStore'
import { usePrediction } from '@/composables/usePrediction'
// 导入ECharts图表组件
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

defineEmits(['update:modelValue', 're-evaluate'])

const chartStore = useChartStore()
const { getRiskText, getRiskClass, getHealthAdvice } = usePrediction()

const currentChartIndex = ref(0)
const showDataDetails = ref(false)

// 图表组件映射
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

// 可用图表列表（使用ECharts图表组件）
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

// 获取图表组件
function getChartComponent(key) {
  return chartComponentMap[key] || null
}

// 轮播导航：上一张
function prevChart() {
  if (availableCharts.value.length <= 1) return
  currentChartIndex.value = (currentChartIndex.value - 1 + availableCharts.value.length) % availableCharts.value.length
}

// 轮播导航：下一张
function nextChart() {
  if (availableCharts.value.length <= 1) return
  currentChartIndex.value = (currentChartIndex.value + 1) % availableCharts.value.length
}

// 轮播导航：跳转到指定图表
function goToChart(index) {
  if (index >= 0 && index < availableCharts.value.length) {
    currentChartIndex.value = index
  }
}
</script>

<style scoped>
/* 结果弹窗容器 */
.result-container {
  padding: 0;
}

/* ==================== 结果头部 ==================== */
.result-header {
  padding: 24px 28px;
  border-radius: 12px;
  margin-bottom: 24px;
  text-align: center;
  transition: all 0.3s ease;
}

.result-header.low-risk {
  background: linear-gradient(135deg, #f0f9eb, #e8f8e0);
  border: 1px solid #b7eb8f;
}

.result-header.medium-risk {
  background: linear-gradient(135deg, #fff7e6, #fff1cc);
  border: 1px solid #ffd666;
}

.result-header.high-risk {
  background: linear-gradient(135deg, #fff1f0, #ffd8d2);
  border: 1px solid #ffa39e;
}

.result-header-main {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
}

/* 风险等级标签 */
.risk-level-badge {
  display: inline-flex;
  align-items: center;
  padding: 8px 22px;
  border-radius: 20px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
  transition: all 0.3s ease;
}

.risk-level-badge.low-risk {
  background: linear-gradient(135deg, #52c41a, #73d13d);
  color: white;
  box-shadow: 0 2px 8px rgba(82, 196, 26, 0.3);
}

.risk-level-badge.medium-risk {
  background: linear-gradient(135deg, #faad14, #ffc53d);
  color: white;
  box-shadow: 0 2px 8px rgba(250, 173, 20, 0.3);
}

.risk-level-badge.high-risk {
  background: linear-gradient(135deg, #f5222d, #ff4d4f);
  color: white;
  box-shadow: 0 2px 8px rgba(245, 34, 45, 0.3);
}

/* 患病概率 */
.probability-main {
  display: inline-flex;
  align-items: baseline;
  gap: 8px;
}

.probability-label {
  font-size: 14px;
  color: #606266;
}

.probability-value {
  font-size: 32px;
  font-weight: 700;
  color: #1D2129;
  line-height: 1;
}

/* 置信区间 */
.confidence-interval {
  margin-top: 10px;
  font-size: 13px;
  color: #909399;
  background: rgba(255, 255, 255, 0.6);
  display: inline-block;
  padding: 4px 16px;
  border-radius: 12px;
}

/* ==================== 双列布局 ==================== */
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

/* ==================== 图表轮播 ==================== */
.charts-carousel {
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e8e8e8;
  padding: 20px;
}

.carousel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
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
  transition: all 0.3s;
}

.indicator-dot.active {
  background: #409EFF;
  width: 20px;
  border-radius: 4px;
}

.carousel-main {
  display: flex;
  align-items: center;
  gap: 12px;
}

.carousel-btn {
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 1px solid #e8e8e8;
  background: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: #606266;
  transition: all 0.2s;
}

.carousel-btn:hover:not(:disabled) {
  border-color: #409EFF;
  color: #409EFF;
  background: #ecf5ff;
}

.carousel-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.carousel-display {
  flex: 1;
  min-height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-component {
  width: 100%;
  height: 320px;
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

/* 缩略图导航 */
.carousel-thumbnails {
  display: flex;
  gap: 8px;
  margin-top: 16px;
  flex-wrap: wrap;
}

.thumbnail {
  padding: 6px 14px;
  border-radius: 6px;
  font-size: 12px;
  color: #606266;
  background: #f5f7fa;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
}

.thumbnail:hover {
  background: #ecf5ff;
  color: #409EFF;
}

.thumbnail.active {
  background: #409EFF;
  color: white;
  border-color: #409EFF;
}

.thumbnail-label {
  white-space: nowrap;
}

/* ==================== 折叠面板 ==================== */
.collapse-panel {
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e8e8e8;
  overflow: hidden;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px;
  cursor: pointer;
  user-select: none;
  transition: background 0.2s;
}

.panel-header:hover {
  background: #f5f7fa;
}

.panel-title {
  font-size: 14px;
  font-weight: 600;
  color: #1D2129;
}

.panel-arrow {
  font-size: 14px;
  color: #909399;
  transition: transform 0.3s;
  display: inline-flex;
  align-items: center;
}

.panel-arrow.expanded {
  transform: rotate(180deg);
}

.panel-content {
  padding: 0 18px 16px;
}

/* 面板折叠动画 */
.panel-slide-enter-active {
  transition: all 0.3s ease-out;
}

.panel-slide-leave-active {
  transition: all 0.2s ease-in;
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

/* ==================== AI健康处方 ==================== */
.health-suggestion-card {
  background: linear-gradient(135deg, #f0f5ff, #e8f4fd);
  border-radius: 12px;
  border: 1px solid #bae0ff;
  overflow: hidden;
}

.prescription-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 18px;
  background: rgba(64, 158, 255, 0.08);
  font-size: 15px;
  font-weight: 600;
  color: #1D2129;
  border-bottom: 1px solid #bae0ff;
}

.prescription-header .el-icon {
  font-size: 18px;
  color: #409EFF;
}

/* 健康建议区域 */
.suggestion-section {
  padding: 16px 18px;
}

.health-advice-section {
  display: flex;
  gap: 12px;
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
  margin: 0 0 8px;
  font-size: 14px;
  color: #303133;
}

.section-content p {
  margin: 0;
  font-size: 13px;
  color: #606266;
  line-height: 1.7;
}

/* ==================== ElDescriptions 样式覆盖 ==================== */
:deep(.el-descriptions__title) {
  font-size: 13px;
}

:deep(.el-descriptions__label) {
  font-weight: 500;
  color: #606266;
  width: 100px;
}

:deep(.el-descriptions__content) {
  color: #303133;
}
</style>

<style>
/* 结果弹窗全局样式 */
.result-dialog .el-dialog__header {
  display: none;
}

.result-dialog .el-dialog__body {
  padding: 28px 28px 10px;
}

.result-dialog .el-dialog__footer {
  padding: 16px 28px 24px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.result-dialog {
  border-radius: 16px;
  overflow: hidden;
}
</style>
