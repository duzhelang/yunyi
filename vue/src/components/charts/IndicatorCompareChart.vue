<template>
  <div class="indicator-compare-chart">
    <div class="chart-header">
      <h3 class="chart-title">指标对比分析</h3>
      <p class="chart-subtitle">关键健康指标与正常范围对比</p>
    </div>
    
    <!-- 图表控制栏 -->
    <div class="chart-controls">
      <div class="control-group">
        <span class="control-label">显示模式:</span>
        <el-radio-group v-model="displayMode" size="small">
          <el-radio-button value="bar">柱状图</el-radio-button>
          <el-radio-button value="progress">进度条</el-radio-button>
        </el-radio-group>
      </div>
      
      <div class="control-group">
        <el-checkbox v-model="showNormalRange">显示正常范围</el-checkbox>
      </div>
    </div>
    
    <div class="chart-container" v-loading="loading">
      <div ref="chartRef" class="chart-element" v-show="displayMode === 'bar'"></div>
      
      <!-- 进度条模式 -->
      <div class="progress-mode" v-if="displayMode === 'progress'">
        <div class="progress-item" v-for="item in currentData" :key="item.name">
          <div class="progress-header">
            <span class="progress-name">{{ item.name }}</span>
            <span class="progress-value" :style="{ color: getProgressColor(item) }">
              {{ item.value }}{{ item.unit }}
            </span>
          </div>
          
          <div class="progress-bar-container">
            <div class="progress-bar" :style="getProgressStyle(item)"></div>
            <div class="normal-range" v-if="showNormalRange" :style="getNormalRangeStyle(item)">
              <span class="range-label">{{ item.normalMin }}-{{ item.normalMax }}</span>
            </div>
          </div>
          
          <div class="progress-status" :class="getStatusClass(item)">
            {{ getStatusText(item) }}
          </div>
        </div>
      </div>
    </div>
    
    <!-- 数据统计 -->
    <div class="data-summary" v-if="showSummary">
      <div class="summary-item">
        <span class="summary-label">总指标数:</span>
        <span class="summary-value">{{ currentData.length }}</span>
      </div>
      <div class="summary-item">
        <span class="summary-label">正常指标:</span>
        <span class="summary-value normal">{{ normalCount }}</span>
      </div>
      <div class="summary-item">
        <span class="summary-label">异常指标:</span>
        <span class="summary-value warning">{{ warningCount }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useChart } from './useChart'
import { useChartTheme } from './chartTheme'
import { useChartStore } from '@/store/chartStore'

// 组件属性定义
const props = defineProps({
  // 指标数据
  data: {
    type: Array,
    default: () => [],
    validator: (value) => {
      return value.every(item => 
        item.name && 
        typeof item.value === 'number'
      )
    }
  },
  // 是否显示摘要
  showSummary: {
    type: Boolean,
    default: true
  },
  // 图表高度
  height: {
    type: String,
    default: '400px'
  }
})

// 获取主题配置
const theme = useChartTheme()

// 获取图表状态管理
const chartStore = useChartStore()

// 显示模式
const displayMode = ref('bar')

// 是否显示正常范围
const showNormalRange = ref(true)

// 默认数据（如果没有提供）
const defaultData = [
  { 
    name: '血糖水平', 
    value: 85, 
    unit: 'mg/dL', 
    normalMin: 70, 
    normalMax: 100,
    warningMin: 100,
    warningMax: 126,
    dangerMin: 126,
    dangerMax: 200
  },
  { 
    name: 'BMI指数', 
    value: 23.5, 
    unit: '', 
    normalMin: 18.5, 
    normalMax: 24,
    warningMin: 24,
    warningMax: 28,
    dangerMin: 28,
    dangerMax: 40
  },
  { 
    name: '血压(收缩)', 
    value: 120, 
    unit: 'mmHg', 
    normalMin: 90, 
    normalMax: 120,
    warningMin: 120,
    warningMax: 140,
    dangerMin: 140,
    dangerMax: 180
  },
  { 
    name: '胰岛素', 
    value: 60, 
    unit: 'uIU/mL', 
    normalMin: 16, 
    normalMax: 166,
    warningMin: 166,
    warningMax: 250,
    dangerMin: 250,
    dangerMax: 300
  },
  { 
    name: '皮肤厚度', 
    value: 25, 
    unit: 'mm', 
    normalMin: 10, 
    normalMax: 40,
    warningMin: 40,
    warningMax: 50,
    dangerMin: 50,
    dangerMax: 60
  },
  { 
    name: '运动频率', 
    value: 4, 
    unit: '次/周', 
    normalMin: 3, 
    normalMax: 7,
    warningMin: 1,
    warningMax: 3,
    dangerMin: 0,
    dangerMax: 1
  }
]

// 计算当前数据
const currentData = computed(() => {
  if (props.data.length > 0) {
    return props.data
  }
  
  // 如果没有提供数据，尝试从store获取
  const percentiles = chartStore.getFormattedPercentiles()
  if (percentiles.length > 0) {
    return transformPercentilesData(percentiles)
  }
  
  return defaultData
})

/**
 * 转换百分位数数据为对比格式
 * @param {Array} percentiles - 百分位数数据
 * @returns {Array} 对比数据数组
 */
function transformPercentilesData(percentiles) {
  return percentiles.map(item => {
    const value = item.value
    let status = 'normal'
    if (value > 80) status = 'danger'
    else if (value > 60) status = 'warning'
    else if (value < 20) status = 'good'
    
    return {
      name: item.name,
      value: value,
      unit: '%',
      normalMin: 20,
      normalMax: 60,
      warningMin: 60,
      warningMax: 80,
      dangerMin: 80,
      dangerMax: 100,
      status
    }
  })
}

// 计算正常指标数量
const normalCount = computed(() => {
  return currentData.value.filter(item => {
    const status = getStatus(item)
    return status === 'normal' || status === 'good'
  }).length
})

// 计算异常指标数量
const warningCount = computed(() => {
  return currentData.value.filter(item => {
    const status = getStatus(item)
    return status === 'warning' || status === 'danger'
  }).length
})

/**
 * 获取指标状态
 * @param {Object} item - 指标数据
 * @returns {String} 状态
 */
function getStatus(item) {
  if (item.status) return item.status
  
  const { value, normalMin, normalMax, warningMin, warningMax } = item
  
  if (value >= normalMin && value <= normalMax) {
    return 'normal'
  } else if (value >= warningMin && value <= warningMax) {
    return 'warning'
  } else if (value > warningMax) {
    return 'danger'
  } else {
    return 'good'
  }
}

/**
 * 获取状态样式类
 * @param {Object} item - 指标数据
 * @returns {String} 样式类名
 */
function getStatusClass(item) {
  const status = getStatus(item)
  const map = {
    good: 'status-good',
    normal: 'status-normal',
    warning: 'status-warning',
    danger: 'status-danger'
  }
  return map[status] || 'status-normal'
}

/**
 * 获取状态文本
 * @param {Object} item - 指标数据
 * @returns {String} 状态文本
 */
function getStatusText(item) {
  const status = getStatus(item)
  const map = {
    good: '优秀',
    normal: '正常',
    warning: '偏高',
    danger: '异常'
  }
  return map[status] || '正常'
}

/**
 * 获取进度条颜色
 * @param {Object} item - 指标数据
 * @returns {String} 颜色值
 */
function getProgressColor(item) {
  const status = getStatus(item)
  const map = {
    good: '#52c41a',
    normal: '#1890ff',
    warning: '#faad14',
    danger: '#f5222d'
  }
  return map[status] || '#1890ff'
}

/**
 * 获取进度条样式
 * @param {Object} item - 指标数据
 * @returns {Object} 样式对象
 */
function getProgressStyle(item) {
  const percentage = Math.min(100, Math.max(0, (item.value / item.dangerMax) * 100))
  const color = getProgressColor(item)
  
  return {
    width: `${percentage}%`,
    backgroundColor: color
  }
}

/**
 * 获取正常范围样式
 * @param {Object} item - 指标数据
 * @returns {Object} 样式对象
 */
function getNormalRangeStyle(item) {
  const minPercentage = (item.normalMin / item.dangerMax) * 100
  const maxPercentage = (item.normalMax / item.dangerMax) * 100
  
  return {
    left: `${minPercentage}%`,
    width: `${maxPercentage - minPercentage}%`
  }
}

/**
 * 获取图表配置
 * @returns {Object} ECharts配置对象
 */
function getOption() {
  const baseOption = theme.getBaseOption()
  const data = currentData.value
  
  if (data.length === 0) {
    return {
      ...baseOption,
      graphic: {
        type: 'text',
        left: 'center',
        top: 'middle',
        style: {
          text: '暂无数据',
          fontSize: 16,
          fill: '#8c8c8c'
        }
      }
    }
  }
  
  // 准备数据
  const categories = data.map(item => item.name)
  const values = data.map(item => item.value)
  const normalMaxValues = data.map(item => item.normalMax || 100)
  const colors = data.map(item => getProgressColor(item))
  
  return {
    ...baseOption,
    tooltip: {
      ...baseOption.tooltip,
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: function (params) {
        const dataIndex = params[0].dataIndex
        const item = data[dataIndex]
        const status = getStatus(item)
        const statusText = getStatusText(item)
        const statusColor = getProgressColor(item)
        
        let html = `<div style="font-weight: 600; margin-bottom: 8px;">${item.name}</div>`
        html += `<div style="margin-bottom: 4px;">当前值: <span style="color: ${statusColor}; font-weight: 500;">${item.value}${item.unit}</span></div>`
        html += `<div style="margin-bottom: 4px;">正常范围: ${item.normalMin}${item.unit} - ${item.normalMax}${item.unit}</div>`
        html += `<div>状态: <span style="color: ${statusColor}; font-weight: 500;">${statusText}</span></div>`
        
        return html
      }
    },
    legend: {
      ...baseOption.legend,
      data: ['当前值', '正常上限'],
      bottom: 10
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: categories,
      axisLabel: {
        ...baseOption.axis.axisLabel,
        rotate: 30,
        interval: 0
      },
      axisLine: baseOption.axis.axisLine,
      axisTick: baseOption.axis.axisTick
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        ...baseOption.axis.axisLabel
      },
      axisLine: baseOption.axis.axisLine,
      axisTick: baseOption.axis.axisTick,
      splitLine: baseOption.axis.splitLine
    },
    series: [
      {
        name: '当前值',
        type: 'bar',
        data: values.map((value, index) => ({
          value,
          itemStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: colors[index] },
                { offset: 1, color: colors[index] + '80' }
              ]
            },
            borderRadius: [4, 4, 0, 0]
          }
        })),
        barWidth: '40%',
        label: {
          show: true,
          position: 'top',
          formatter: '{c}',
          fontSize: 11,
          color: '#595959'
        }
      },
      {
        name: '正常上限',
        type: 'bar',
        data: normalMaxValues,
        barWidth: '40%',
        itemStyle: {
          color: 'rgba(82, 196, 26, 0.1)',
          borderColor: '#52c41a',
          borderWidth: 1,
          borderType: 'dashed',
          borderRadius: [4, 4, 0, 0]
        },
        barGap: '-100%'
      }
    ]
  }
}

// 使用图表composable
const { chartRef, loading, hasError, errorMessage, exportImage } = useChart({
  getOption,
  props: computed(() => ({
    data: currentData.value,
    displayMode: displayMode.value,
    showNormalRange: showNormalRange.value
  }))
})

// 监听数据变化
watch(
  () => currentData.value,
  () => {
    // 图表会通过useChart的watch自动更新
  },
  { deep: true }
)

/**
 * 导出图表为图片
 * @param {String} type - 图片类型
 * @param {String} filename - 文件名
 */
function exportChart(type = 'png', filename = 'indicator-compare') {
  exportImage(type, filename)
}

// 暴露方法给父组件
defineExpose({
  exportChart
})
</script>

<style scoped>
.indicator-compare-chart {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chart-header {
  margin-bottom: 16px;
  text-align: center;
}

.chart-title {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 4px 0;
}

.chart-subtitle {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
}

.chart-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 12px;
  background: #fafafa;
  border-radius: 6px;
}

.control-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.control-label {
  font-size: 13px;
  color: #595959;
}

.chart-container {
  height: v-bind(height);
  min-height: 300px;
}

.chart-element {
  width: 100%;
  height: 100%;
}

.progress-mode {
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.progress-item {
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.progress-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.progress-name {
  font-size: 14px;
  font-weight: 500;
  color: #262626;
}

.progress-value {
  font-size: 14px;
  font-weight: 600;
}

.progress-bar-container {
  position: relative;
  height: 12px;
  background: #f0f0f0;
  border-radius: 6px;
  overflow: hidden;
  margin-bottom: 8px;
}

.progress-bar {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  border-radius: 6px;
  transition: width 0.6s ease;
}

.normal-range {
  position: absolute;
  top: 0;
  height: 100%;
  background: rgba(82, 196, 26, 0.1);
  border-left: 1px dashed #52c41a;
  border-right: 1px dashed #52c41a;
}

.range-label {
  position: absolute;
  bottom: -18px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 10px;
  color: #52c41a;
  white-space: nowrap;
}

.progress-status {
  font-size: 12px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 4px;
  display: inline-block;
}

.status-good {
  background: rgba(82, 196, 26, 0.1);
  color: #52c41a;
}

.status-normal {
  background: rgba(24, 144, 255, 0.1);
  color: #1890ff;
}

.status-warning {
  background: rgba(250, 173, 20, 0.1);
  color: #faad14;
}

.status-danger {
  background: rgba(245, 34, 45, 0.1);
  color: #f5222d;
}

.data-summary {
  display: flex;
  justify-content: center;
  gap: 24px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.summary-label {
  font-size: 13px;
  color: #8c8c8c;
}

.summary-value {
  font-size: 14px;
  font-weight: 600;
  color: #262626;
}

.summary-value.normal {
  color: #52c41a;
}

.summary-value.warning {
  color: #faad14;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .chart-controls {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .chart-container {
    height: 300px;
    min-height: 250px;
  }
  
  .data-summary {
    flex-wrap: wrap;
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .chart-container {
    height: 250px;
    min-height: 200px;
  }
  
  .indicator-compare-chart {
    padding: 12px;
  }
  
  .chart-title {
    font-size: 16px;
  }
  
  .chart-subtitle {
    font-size: 12px;
  }
  
  .progress-item {
    margin-bottom: 12px;
    padding-bottom: 12px;
  }
}
</style>