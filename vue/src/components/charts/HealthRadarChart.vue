<template>
  <div class="health-radar-chart">
    <div class="chart-header">
      <h3 class="chart-title">健康雷达图</h3>
      <p class="chart-subtitle">关键健康指标评估</p>
    </div>
    
    <div class="chart-container" v-loading="loading">
      <div ref="chartRef" class="chart-element"></div>
    </div>
    
    <!-- 指标说明 -->
    <div class="indicators-legend" v-if="showLegend">
      <div class="legend-title">指标说明:</div>
      <div class="legend-items">
        <div class="legend-item" v-for="item in indicators" :key="item.name">
          <span class="legend-color" :style="{ backgroundColor: item.color }"></span>
          <span class="legend-name">{{ item.name }}</span>
          <span class="legend-value">{{ item.value }}{{ item.unit }}</span>
          <span class="legend-status" :class="getStatusClass(item.status)">
            {{ getStatusText(item.status) }}
          </span>
        </div>
      </div>
    </div>
    
    <!-- 数据来源提示 -->
    <div class="data-source" v-if="showDataSource">
      <el-icon><InfoFilled /></el-icon>
      <span>数据基于最近一次健康检查结果</span>
    </div>
  </div>
</template>

<script setup>
import { computed, watch } from 'vue'
import { useChart } from './useChart'
import { useChartTheme } from './chartTheme'
import { useChartStore } from '@/store/chartStore'
import { InfoFilled } from '@element-plus/icons-vue'

// 组件属性定义
const props = defineProps({
  // 指标数据
  indicators: {
    type: Array,
    default: () => [],
    validator: (value) => {
      return value.every(item => 
        item.name && 
        typeof item.value === 'number' && 
        typeof item.max === 'number'
      )
    }
  },
  // 是否显示图例
  showLegend: {
    type: Boolean,
    default: true
  },
  // 是否显示数据来源
  showDataSource: {
    type: Boolean,
    default: true
  },
  // 图表高度
  height: {
    type: String,
    default: '400px'
  },
  // 雷达图形状
  shape: {
    type: String,
    default: 'polygon',
    validator: (value) => ['polygon', 'circle'].includes(value)
  }
})

// 获取主题配置
const theme = useChartTheme()

// 获取图表状态管理
const chartStore = useChartStore()

// 默认指标数据（如果没有提供）
const defaultIndicators = [
  { name: '血糖水平', value: 85, max: 150, unit: 'mg/dL', status: 'normal' },
  { name: 'BMI指数', value: 23.5, max: 40, unit: '', status: 'normal' },
  { name: '血压', value: 120, max: 180, unit: 'mmHg', status: 'normal' },
  { name: '胰岛素', value: 60, max: 200, unit: 'uIU/mL', status: 'normal' },
  { name: '皮肤厚度', value: 25, max: 50, unit: 'mm', status: 'normal' },
  { name: '运动频率', value: 4, max: 7, unit: '次/周', status: 'good' }
]

// 计算当前指标数据
const currentIndicators = computed(() => {
  if (props.indicators.length > 0) {
    return props.indicators
  }
  
  // 如果没有提供指标数据，尝试从store获取
  const featureValues = chartStore.featureValues
  if (featureValues && Object.keys(featureValues).length > 0) {
    return transformFeatureValues(featureValues)
  }
  
  return defaultIndicators
})

/**
 * 转换特征值为雷达图指标格式
 * @param {Object} features - 特征值对象
 * @returns {Array} 指标数组
 */
function transformFeatureValues(features) {
  const indicatorMap = {
    'Glucose': { name: '血糖水平', max: 200, unit: 'mg/dL' },
    'BMI': { name: 'BMI指数', max: 50, unit: '' },
    'BloodPressure': { name: '血压', max: 150, unit: 'mmHg' },
    'Insulin': { name: '胰岛素', max: 300, unit: 'uIU/mL' },
    'SkinThickness': { name: '皮肤厚度', max: 60, unit: 'mm' },
    'Pregnancies': { name: '怀孕次数', max: 15, unit: '次' },
    'DiabetesPedigreeFunction': { name: '家族史', max: 2.5, unit: '' },
    'Age': { name: '年龄', max: 100, unit: '岁' }
  }
  
  return Object.entries(features)
    .filter(([key]) => indicatorMap[key])
    .map(([key, value]) => {
      const config = indicatorMap[key]
      const numValue = Number(value)
      const ratio = numValue / config.max
      let status = 'normal'
      
      if (ratio > 0.8) status = 'danger'
      else if (ratio > 0.6) status = 'warning'
      else if (ratio < 0.3) status = 'good'
      
      return {
        name: config.name,
        value: numValue,
        max: config.max,
        unit: config.unit,
        status
      }
    })
    .slice(0, 8) // 最多显示8个指标
}

/**
 * 获取状态样式类
 * @param {String} status - 状态
 * @returns {String} 样式类名
 */
function getStatusClass(status) {
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
 * @param {String} status - 状态
 * @returns {String} 状态文本
 */
function getStatusText(status) {
  const map = {
    good: '优秀',
    normal: '正常',
    warning: '注意',
    danger: '警告'
  }
  return map[status] || '正常'
}

/**
 * 获取图表配置
 * @returns {Object} ECharts配置对象
 */
function getOption() {
  const baseOption = theme.getBaseOption()
  const indicators = currentIndicators.value
  
  if (indicators.length === 0) {
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
  
  // 准备雷达图指标配置
  const radarIndicators = indicators.map(item => ({
    name: item.name,
    max: item.max
  }))
  
  // 准备数据系列
  const seriesData = [
    {
      value: indicators.map(item => item.value),
      name: '当前值',
      symbol: 'circle',
      symbolSize: 6,
      lineStyle: {
        width: 2,
        color: '#1890ff'
      },
      areaStyle: {
        color: {
          type: 'radial',
          x: 0.5,
          y: 0.5,
          r: 0.5,
          colorStops: [
            { offset: 0, color: 'rgba(24, 144, 255, 0.3)' },
            { offset: 1, color: 'rgba(24, 144, 255, 0.1)' }
          ]
        }
      },
      itemStyle: {
        color: '#1890ff',
        borderColor: '#fff',
        borderWidth: 2
      }
    }
  ]
  
  // 添加正常范围参考线（可选）
  if (showNormalRange.value) {
    seriesData.push({
      value: indicators.map(item => item.max * 0.6),
      name: '正常范围',
      symbol: 'none',
      lineStyle: {
        type: 'dashed',
        width: 1,
        color: '#52c41a'
      },
      areaStyle: {
        color: 'rgba(82, 196, 26, 0.05)'
      }
    })
  }
  
  return {
    ...baseOption,
    legend: {
      ...baseOption.legend,
      data: showNormalRange.value ? ['当前值', '正常范围'] : ['当前值'],
      bottom: 10
    },
    tooltip: {
      ...baseOption.tooltip,
      trigger: 'item',
      formatter: function (params) {
        if (params.componentType === 'series' && params.seriesType === 'radar') {
          const data = params.data
          let html = `<div style="font-weight: 600; margin-bottom: 8px;">${params.name}</div>`
          
          data.value.forEach((value, index) => {
            const indicator = indicators[index]
            const ratio = value / indicator.max
            let color = '#52c41a'
            if (ratio > 0.8) color = '#f5222d'
            else if (ratio > 0.6) color = '#faad14'
            
            html += `
              <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px;">
                <span>${indicator.name}:</span>
                <span style="color: ${color}; font-weight: 500;">${value}${indicator.unit}</span>
              </div>
            `
          })
          
          return html
        }
        return ''
      }
    },
    radar: {
      indicator: radarIndicators,
      shape: props.shape,
      radius: '65%',
      center: ['50%', '55%'],
      axisName: {
        color: '#595959',
        fontSize: 12
      },
      splitArea: {
        areaStyle: {
          color: ['rgba(24, 144, 255, 0.02)', 'rgba(24, 144, 255, 0.05)']
        }
      },
      axisLine: {
        lineStyle: {
          color: '#d9d9d9'
        }
      },
      splitLine: {
        lineStyle: {
          color: '#f0f0f0',
          type: 'dashed'
        }
      }
    },
    series: [
      {
        type: 'radar',
        data: seriesData
      }
    ]
  }
}

// 是否显示正常范围参考线
const showNormalRange = computed(() => {
  return currentIndicators.value.length > 0 && currentIndicators.value.every(item => item.max)
})

// 使用图表composable
const { chartRef, loading, hasError, errorMessage, exportImage } = useChart({
  getOption,
  props: computed(() => ({
    indicators: currentIndicators.value,
    shape: props.shape
  }))
})

// 监听指标数据变化
watch(
  () => currentIndicators.value,
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
function exportChart(type = 'png', filename = 'health-radar') {
  exportImage(type, filename)
}

// 暴露方法给父组件
defineExpose({
  exportChart
})
</script>

<style scoped>
.health-radar-chart {
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
  height: v-bind(height);
  min-height: 300px;
}

.chart-element {
  width: 100%;
  height: 100%;
}

.indicators-legend {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.legend-title {
  font-size: 14px;
  font-weight: 500;
  color: #262626;
  margin-bottom: 12px;
}

.legend-items {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background: #fafafa;
  border-radius: 6px;
  font-size: 12px;
}

.legend-color {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-right: 8px;
  flex-shrink: 0;
}

.legend-name {
  flex: 1;
  color: #595959;
  margin-right: 8px;
}

.legend-value {
  color: #262626;
  font-weight: 500;
  margin-right: 8px;
}

.legend-status {
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
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

.data-source {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 16px;
  font-size: 12px;
  color: #8c8c8c;
  gap: 4px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .chart-container {
    height: 300px;
    min-height: 250px;
  }
  
  .legend-items {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .chart-container {
    height: 250px;
    min-height: 200px;
  }
  
  .health-radar-chart {
    padding: 12px;
  }
  
  .chart-title {
    font-size: 16px;
  }
  
  .chart-subtitle {
    font-size: 12px;
  }
}
</style>