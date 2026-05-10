<template>
  <div class="risk-heatmap-chart">
    <div class="chart-header">
      <h3 class="chart-title">风险热力图</h3>
      <p class="chart-subtitle">健康指标风险等级分布</p>
    </div>
    
    <div class="chart-container" v-loading="loading">
      <div ref="chartRef" class="chart-element"></div>
    </div>
    
    <!-- 风险等级图例 -->
    <div class="risk-legend">
      <div class="legend-title">风险等级</div>
      <div class="legend-items">
        <div class="legend-item" v-for="item in riskLegend" :key="item.label">
          <div class="legend-color" :style="{ backgroundColor: item.color }"></div>
          <span class="legend-label">{{ item.label }}</span>
        </div>
      </div>
    </div>
    
    <!-- 指标详情 -->
    <div class="indicator-details" v-if="showDetails">
      <div class="detail-item" v-for="item in indicatorDetails" :key="item.name">
        <div class="detail-header">
          <span class="detail-name">{{ item.name }}</span>
          <span class="detail-status" :style="{ color: item.color, backgroundColor: item.bgColor }">
            {{ item.status }}
          </span>
        </div>
        <div class="detail-info">
          <span class="detail-value">{{ item.value }}</span>
          <span class="detail-range">正常范围: {{ item.normalRange }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useChart } from './useChart'
import { useChartTheme } from './chartTheme'
import { chartUtils } from './index'

// 组件属性定义
const props = defineProps({
  // 健康指标数据
  indicators: {
    type: Object,
    default: () => ({})
  },
  // 是否显示详情
  showDetails: {
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

// 8个健康指标配置
const indicatorConfigs = [
  { key: 'Pregnancies', name: '怀孕次数', unit: '次', normalRange: '0-15' },
  { key: 'Glucose', name: '血糖', unit: 'mg/dL', normalRange: '70-100' },
  { key: 'BloodPressure', name: '血压', unit: 'mmHg', normalRange: '80-120' },
  { key: 'SkinThickness', name: '皮肤厚度', unit: 'mm', normalRange: '10-50' },
  { key: 'Insulin', name: '胰岛素', unit: 'μU/mL', normalRange: '16-166' },
  { key: 'BMI', name: 'BMI指数', unit: 'kg/m²', normalRange: '18.5-24.9' },
  { key: 'DiabetesPedigreeFunction', name: '遗传函数', unit: '', normalRange: '0-1' },
  { key: 'Age', name: '年龄', unit: '岁', normalRange: '20-80' }
]

// 风险等级图例
const riskLegend = computed(() => [
  { label: '正常', color: '#52c41a' },
  { label: '偏低', color: '#1890ff' },
  { label: '轻度偏高', color: '#faad14' },
  { label: '中度偏高', color: '#fa8c16' },
  { label: '重度偏高', color: '#f5222d' }
])

/**
 * 计算指标风险等级
 * @param {String} key - 指标键名
 * @param {Number} value - 指标值
 * @returns {Object} 风险等级信息
 */
function getIndicatorRisk(key, value) {
  if (value === undefined || value === null) {
    return { level: 'normal', color: '#52c41a', bgColor: '#f6ffed', status: '正常' }
  }
  
  // 根据不同指标计算风险等级
  switch (key) {
    case 'Glucose':
      if (value < 70) return { level: 'low', color: '#1890ff', bgColor: '#e6f7ff', status: '偏低' }
      if (value <= 100) return { level: 'normal', color: '#52c41a', bgColor: '#f6ffed', status: '正常' }
      if (value <= 126) return { level: 'mild', color: '#faad14', bgColor: '#fffbe6', status: '轻度偏高' }
      if (value <= 200) return { level: 'moderate', color: '#fa8c16', bgColor: '#fff7e6', status: '中度偏高' }
      return { level: 'severe', color: '#f5222d', bgColor: '#fff2f0', status: '重度偏高' }
    
    case 'BMI':
      if (value < 18.5) return { level: 'low', color: '#1890ff', bgColor: '#e6f7ff', status: '偏低' }
      if (value <= 24.9) return { level: 'normal', color: '#52c41a', bgColor: '#f6ffed', status: '正常' }
      if (value <= 30) return { level: 'mild', color: '#faad14', bgColor: '#fffbe6', status: '轻度偏高' }
      return { level: 'severe', color: '#f5222d', bgColor: '#fff2f0', status: '重度偏高' }
    
    case 'BloodPressure':
      if (value < 80) return { level: 'low', color: '#1890ff', bgColor: '#e6f7ff', status: '偏低' }
      if (value <= 120) return { level: 'normal', color: '#52c41a', bgColor: '#f6ffed', status: '正常' }
      if (value <= 140) return { level: 'mild', color: '#faad14', bgColor: '#fffbe6', status: '轻度偏高' }
      return { level: 'moderate', color: '#fa8c16', bgColor: '#fff7e6', status: '中度偏高' }
    
    default:
      return { level: 'normal', color: '#52c41a', bgColor: '#f6ffed', status: '正常' }
  }
}

// 计算指标详情
const indicatorDetails = computed(() => {
  return indicatorConfigs.map(config => {
    const value = props.indicators[config.key]
    const risk = getIndicatorRisk(config.key, value)
    
    return {
      name: config.name,
      value: value !== undefined ? `${value} ${config.unit}` : '未提供',
      normalRange: config.normalRange,
      status: risk.status,
      color: risk.color,
      bgColor: risk.bgColor
    }
  })
})

/**
 * 获取图表配置
 * @returns {Object} ECharts配置对象
 */
function getOption() {
  const baseOption = theme.getBaseOption()
  
  // 准备热力图数据
  const heatmapData = indicatorConfigs.map((config, index) => {
    const value = props.indicators[config.key]
    const risk = getIndicatorRisk(config.key, value)
    
    return {
      name: config.name,
      value: value !== undefined ? value : 0,
      riskLevel: risk.level,
      riskColor: risk.color,
      riskStatus: risk.status
    }
  })
  
  // 热力图数据点
  const data = heatmapData.map((item, index) => [index, 0, item.value])
  
  return {
    ...baseOption,
    tooltip: {
      ...baseOption.tooltip,
      formatter: (params) => {
        const item = heatmapData[params.data[0]]
        return `
          <div style="font-weight: 600; margin-bottom: 8px;">${item.name}</div>
          <div style="margin-bottom: 4px;">数值: ${item.value}</div>
          <div style="margin-bottom: 4px;">状态: <span style="color: ${item.riskColor}">${item.riskStatus}</span></div>
        `
      }
    },
    xAxis: {
      type: 'category',
      data: heatmapData.map(item => item.name),
      axisLabel: {
        ...baseOption.axis?.axisLabel,
        rotate: 45,
        interval: 0
      },
      axisLine: { ...baseOption.axis?.axisLine },
      axisTick: { ...baseOption.axis?.axisTick }
    },
    yAxis: {
      type: 'category',
      data: ['风险等级'],
      axisLabel: {
        ...baseOption.axis?.axisLabel,
        show: false
      },
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { show: false }
    },
    visualMap: {
      min: 0,
      max: 200,
      calculable: false,
      orient: 'horizontal',
      left: 'center',
      bottom: '0%',
      inRange: {
        color: ['#f6ffed', '#fffbe6', '#fff7e6', '#fff2f0']
      },
      textStyle: {
        color: '#595959'
      },
      show: false
    },
    series: [{
      type: 'heatmap',
      data: data,
      label: {
        show: true,
        formatter: (params) => {
          const item = heatmapData[params.data[0]]
          return item.riskStatus
        },
        color: '#262626',
        fontSize: 12
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      },
      itemStyle: {
        borderColor: '#fff',
        borderWidth: 2,
        borderRadius: 4
      }
    }]
  }
}

// 使用图表composable
const { chartRef, loading, hasError, errorMessage, exportImage } = useChart({
  getOption,
  props: computed(() => ({
    indicators: props.indicators,
    showDetails: props.showDetails
  }))
})

/**
 * 导出图表为图片
 * @param {String} type - 图片类型
 * @param {String} filename - 文件名
 */
function exportChart(type = 'png', filename = 'risk-heatmap') {
  exportImage(type, filename)
}

// 暴露方法给父组件
defineExpose({
  exportChart
})
</script>

<style scoped>
.risk-heatmap-chart {
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

.chart-container {
  position: relative;
  height: v-bind(height);
  min-height: 250px;
}

.chart-element {
  width: 100%;
  height: 100%;
}

.risk-legend {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.legend-title {
  font-size: 14px;
  font-weight: 500;
  color: #262626;
  margin-bottom: 8px;
}

.legend-items {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.legend-label {
  font-size: 12px;
  color: #595959;
}

.indicator-details {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.detail-item {
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.detail-name {
  font-size: 14px;
  font-weight: 500;
  color: #262626;
}

.detail-status {
  font-size: 12px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 4px;
}

.detail-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-value {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.detail-range {
  font-size: 12px;
  color: #8c8c8c;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .chart-container {
    height: 300px;
    min-height: 200px;
  }
  
  .legend-items {
    gap: 8px;
  }
  
  .detail-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
}

@media (max-width: 480px) {
  .risk-heatmap-chart {
    padding: 12px;
  }
  
  .chart-title {
    font-size: 16px;
  }
  
  .chart-subtitle {
    font-size: 12px;
  }
  
  .chart-container {
    height: 250px;
    min-height: 180px;
  }
  
  .legend-items {
    flex-direction: column;
    gap: 6px;
  }
}
</style>