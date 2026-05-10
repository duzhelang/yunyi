<template>
  <div class="confidence-interval-chart">
    <div class="chart-header">
      <h3 class="chart-title">置信区间分析</h3>
      <p class="chart-subtitle">预测概率的不确定性评估</p>
    </div>
    
    <div class="chart-container" v-loading="loading">
      <div ref="chartRef" class="chart-element"></div>
    </div>
    
    <!-- 统计信息 -->
    <div class="stats-container">
      <div class="stat-item">
        <div class="stat-label">预测概率</div>
        <div class="stat-value primary">{{ probability.toFixed(1) }}%</div>
      </div>
      <div class="stat-item">
        <div class="stat-label">95%置信下限</div>
        <div class="stat-value success">{{ lowerBound.toFixed(1) }}%</div>
      </div>
      <div class="stat-item">
        <div class="stat-label">95%置信上限</div>
        <div class="stat-value warning">{{ upperBound.toFixed(1) }}%</div>
      </div>
      <div class="stat-item">
        <div class="stat-label">置信区间宽度</div>
        <div class="stat-value info">{{ intervalWidth.toFixed(1) }}%</div>
      </div>
    </div>
    
    <!-- 解释说明 -->
    <div class="interpretation">
      <div class="interpretation-header">
        <el-icon><InfoFilled /></el-icon>
        <span>结果解释</span>
      </div>
      <div class="interpretation-content">
        <p>基于模型预测，患病概率的<strong>95%置信区间</strong>为 <strong>{{ lowerBound.toFixed(1) }}% - {{ upperBound.toFixed(1) }}%</strong>。</p>
        <p>这意味着我们有95%的把握认为，真实的患病概率落在此区间内。</p>
        <p v-if="intervalWidth > 20">置信区间较宽，表明预测存在较大不确定性，建议结合其他检查结果综合判断。</p>
        <p v-else>置信区间较窄，表明预测结果相对可靠。</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useChart } from './useChart'
import { useChartTheme } from './chartTheme'
import { InfoFilled } from '@element-plus/icons-vue'

// 组件属性定义
const props = defineProps({
  // 预测概率
  probability: {
    type: Number,
    default: 0
  },
  // 置信区间 [下限, 上限]
  confidenceInterval: {
    type: Array,
    default: () => [0, 0],
    validator: (value) => value.length === 2
  },
  // 图表高度
  height: {
    type: String,
    default: '350px'
  }
})

// 获取主题配置
const theme = useChartTheme()

// 计算置信区间边界
const lowerBound = computed(() => {
  return props.confidenceInterval[0] || Math.max(0, props.probability - 10)
})

const upperBound = computed(() => {
  return props.confidenceInterval[1] || Math.min(100, props.probability + 10)
})

// 计算置信区间宽度
const intervalWidth = computed(() => {
  return upperBound.value - lowerBound.value
})

/**
 * 生成正态分布曲线数据
 * @param {Number} mean - 均值
 * @param {Number} std - 标准差
 * @param {Number} min - 最小值
 * @param {Number} max - 最大值
 * @param {Number} points - 数据点数量
 * @returns {Array} 曲线数据点
 */
function generateNormalDistribution(mean, std, min, max, points = 100) {
  const data = []
  const step = (max - min) / points
  
  for (let x = min; x <= max; x += step) {
    const exponent = -0.5 * Math.pow((x - mean) / std, 2)
    const y = (1 / (std * Math.sqrt(2 * Math.PI))) * Math.exp(exponent)
    data.push([x, y])
  }
  
  return data
}

/**
 * 获取图表配置
 * @returns {Object} ECharts配置对象
 */
function getOption() {
  const baseOption = theme.getBaseOption()
  
  // 计算标准差（基于置信区间）
  const std = intervalWidth.value / (2 * 1.96) // 95%置信区间对应1.96个标准差
  
  // 生成正态分布曲线数据
  const curveData = generateNormalDistribution(
    props.probability,
    std,
    Math.max(0, props.probability - 4 * std),
    Math.min(100, props.probability + 4 * std)
  )
  
  // 生成置信区间内的数据
  const confidenceData = curveData.filter(
    point => point[0] >= lowerBound.value && point[0] <= upperBound.value
  )
  
  return {
    ...baseOption,
    tooltip: {
      ...baseOption.tooltip,
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      },
      formatter: (params) => {
        const x = params[0].data[0]
        const y = params[0].data[1]
        
        return `
          <div style="font-weight: 600; margin-bottom: 8px;">概率: ${x.toFixed(1)}%</div>
          <div>概率密度: ${y.toFixed(4)}</div>
          <div style="margin-top: 4px; color: ${x >= lowerBound.value && x <= upperBound.value ? '#1890ff' : '#8c8c8c'}">
            ${x >= lowerBound.value && x <= upperBound.value ? '在95%置信区间内' : '在95%置信区间外'}
          </div>
        `
      }
    },
    xAxis: {
      type: 'value',
      name: '患病概率 (%)',
      nameTextStyle: {
        color: '#595959',
        fontSize: 12
      },
      min: Math.max(0, props.probability - 4 * std),
      max: Math.min(100, props.probability + 4 * std),
      axisLabel: {
        ...baseOption.axis?.axisLabel,
        formatter: '{value}%'
      },
      axisLine: { ...baseOption.axis?.axisLine },
      axisTick: { ...baseOption.axis?.axisTick }
    },
    yAxis: {
      type: 'value',
      name: '概率密度',
      nameTextStyle: {
        color: '#595959',
        fontSize: 12
      },
      axisLabel: {
        ...baseOption.axis?.axisLabel,
        formatter: (value) => value.toFixed(3)
      },
      axisLine: { ...baseOption.axis?.axisLine },
      axisTick: { ...baseOption.axis?.axisTick },
      splitLine: { ...baseOption.axis?.splitLine }
    },
    series: [
      // 正态分布曲线
      {
        name: '概率分布',
        type: 'line',
        data: curveData,
        smooth: true,
        symbol: 'none',
        lineStyle: {
          color: '#1890ff',
          width: 2
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(24, 144, 255, 0.3)' },
              { offset: 1, color: 'rgba(24, 144, 255, 0.05)' }
            ]
          }
        }
      },
      // 置信区间填充
      {
        name: '95%置信区间',
        type: 'line',
        data: confidenceData,
        smooth: true,
        symbol: 'none',
        lineStyle: {
          color: '#1890ff',
          width: 0
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(24, 144, 255, 0.6)' },
              { offset: 1, color: 'rgba(24, 144, 255, 0.1)' }
            ]
          }
        }
      },
      // 预测值标记线
      {
        name: '预测值',
        type: 'line',
        markLine: {
          silent: true,
          symbol: 'none',
          label: {
            show: true,
            position: 'start',
            formatter: `预测值: ${props.probability.toFixed(1)}%`,
            color: '#f5222d',
            fontSize: 12,
            fontWeight: 600
          },
          lineStyle: {
            color: '#f5222d',
            width: 2,
            type: 'solid'
          },
          data: [
            {
              xAxis: props.probability
            }
          ]
        },
        data: []
      },
      // 置信区间下限标记线
      {
        name: '置信下限',
        type: 'line',
        markLine: {
          silent: true,
          symbol: 'none',
          label: {
            show: true,
            position: 'start',
            formatter: `下限: ${lowerBound.value.toFixed(1)}%`,
            color: '#52c41a',
            fontSize: 11
          },
          lineStyle: {
            color: '#52c41a',
            width: 1,
            type: 'dashed'
          },
          data: [
            {
              xAxis: lowerBound.value
            }
          ]
        },
        data: []
      },
      // 置信区间上限标记线
      {
        name: '置信上限',
        type: 'line',
        markLine: {
          silent: true,
          symbol: 'none',
          label: {
            show: true,
            position: 'start',
            formatter: `上限: ${upperBound.value.toFixed(1)}%`,
            color: '#faad14',
            fontSize: 11
          },
          lineStyle: {
            color: '#faad14',
            width: 1,
            type: 'dashed'
          },
          data: [
            {
              xAxis: upperBound.value
            }
          ]
        },
        data: []
      }
    ]
  }
}

// 使用图表composable
const { chartRef, loading, hasError, errorMessage, exportImage } = useChart({
  getOption,
  props: computed(() => ({
    probability: props.probability,
    confidenceInterval: props.confidenceInterval,
    lowerBound: lowerBound.value,
    upperBound: upperBound.value
  }))
})

/**
 * 导出图表为图片
 * @param {String} type - 图片类型
 * @param {String} filename - 文件名
 */
function exportChart(type = 'png', filename = 'confidence-interval') {
  exportImage(type, filename)
}

// 暴露方法给父组件
defineExpose({
  exportChart
})
</script>

<style scoped>
.confidence-interval-chart {
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

.stats-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.stat-item {
  text-align: center;
  padding: 12px;
  background: #fafafa;
  border-radius: 6px;
}

.stat-label {
  font-size: 12px;
  color: #8c8c8c;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
}

.stat-value.primary {
  color: #1890ff;
}

.stat-value.success {
  color: #52c41a;
}

.stat-value.warning {
  color: #faad14;
}

.stat-value.info {
  color: #13c2c2;
}

.interpretation {
  margin-top: 16px;
  padding: 16px;
  background: #e6f7ff;
  border: 1px solid #91d5ff;
  border-radius: 8px;
}

.interpretation-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #1890ff;
  margin-bottom: 12px;
}

.interpretation-content {
  font-size: 13px;
  color: #595959;
  line-height: 1.6;
}

.interpretation-content p {
  margin: 0 0 8px 0;
}

.interpretation-content p:last-child {
  margin-bottom: 0;
}

.interpretation-content strong {
  color: #262626;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .stats-container {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .chart-container {
    height: 280px;
    min-height: 200px;
  }
}

@media (max-width: 480px) {
  .confidence-interval-chart {
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
  
  .stats-container {
    grid-template-columns: 1fr;
  }
  
  .stat-value {
    font-size: 16px;
  }
}
</style>