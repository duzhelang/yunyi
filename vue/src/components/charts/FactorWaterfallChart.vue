<template>
  <div class="factor-waterfall-chart">
    <div class="chart-header">
      <h3 class="chart-title">因素贡献瀑布图</h3>
      <p class="chart-subtitle">各因素对患病概率的贡献分析</p>
    </div>
    
    <div class="chart-container" v-loading="loading">
      <div ref="chartRef" class="chart-element"></div>
    </div>
    
    <!-- 图例说明 -->
    <div class="chart-legend">
      <div class="legend-item">
        <div class="legend-color increase"></div>
        <span class="legend-text">增加风险</span>
      </div>
      <div class="legend-item">
        <div class="legend-color decrease"></div>
        <span class="legend-text">降低风险</span>
      </div>
      <div class="legend-item">
        <div class="legend-color base"></div>
        <span class="legend-text">基准风险</span>
      </div>
      <div class="legend-item">
        <div class="legend-color total"></div>
        <span class="legend-text">最终风险</span>
      </div>
    </div>
    
    <!-- 因素详情 -->
    <div class="factor-details" v-if="showDetails">
      <div class="detail-item" v-for="item in factorDetails" :key="item.name">
        <div class="detail-header">
          <span class="detail-name">{{ item.name }}</span>
          <span class="detail-impact" :class="item.impactType">
            {{ item.impactText }}
          </span>
        </div>
        <div class="detail-description">{{ item.description }}</div>
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
  // 特征名称
  featureNames: {
    type: Array,
    default: () => []
  },
  // 特征重要性值（SHAP值）
  featureImportance: {
    type: Array,
    default: () => []
  },
  // 最终概率
  probability: {
    type: Number,
    default: 0
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

// 特征中文名称映射
const featureNameMap = {
  'Pregnancies': '怀孕次数',
  'Glucose': '血糖水平',
  'BloodPressure': '血压',
  'SkinThickness': '皮肤厚度',
  'Insulin': '胰岛素',
  'BMI': 'BMI指数',
  'DiabetesPedigreeFunction': '遗传函数',
  'Age': '年龄'
}

// 计算因素详情
const factorDetails = computed(() => {
  return props.featureNames.map((name, index) => {
    const importance = props.featureImportance[index] || 0
    const isIncrease = importance > 0
    
    return {
      name: featureNameMap[name] || name,
      impact: importance,
      impactType: isIncrease ? 'increase' : 'decrease',
      impactText: isIncrease ? `+${(importance * 100).toFixed(1)}%` : `${(importance * 100).toFixed(1)}%`,
      description: isIncrease ? '增加患病风险' : '降低患病风险'
    }
  })
})

/**
 * 获取图表配置
 * @returns {Object} ECharts配置对象
 */
function getOption() {
  const baseOption = theme.getBaseOption()
  
  // 计算基准风险（假设基准为0）
  const baseRisk = 0
  
  // 准备瀑布图数据
  const waterfallData = []
  let currentRisk = baseRisk
  
  // 添加基准风险
  waterfallData.push({
    name: '基准风险',
    value: baseRisk,
    type: 'base'
  })
  
  // 添加各因素贡献
  props.featureNames.forEach((name, index) => {
    const importance = props.featureImportance[index] || 0
    waterfallData.push({
      name: featureNameMap[name] || name,
      value: importance * 100,
      type: importance > 0 ? 'increase' : 'decrease'
    })
  })
  
  // 添加最终风险
  const finalRisk = props.probability
  waterfallData.push({
    name: '最终风险',
    value: finalRisk,
    type: 'total'
  })
  
  // 准备图表数据
  const categories = waterfallData.map(item => item.name)
  const baseData = []
  const increaseData = []
  const decreaseData = []
  const totalData = []
  
  // 计算累计值
  let cumulative = 0
  waterfallData.forEach((item, index) => {
    if (item.type === 'base') {
      baseData.push(cumulative)
      increaseData.push('-')
      decreaseData.push('-')
      totalData.push('-')
    } else if (item.type === 'total') {
      baseData.push('-')
      increaseData.push('-')
      decreaseData.push('-')
      totalData.push(finalRisk)
    } else {
      if (item.value > 0) {
        baseData.push(cumulative)
        increaseData.push(item.value)
        decreaseData.push('-')
        totalData.push('-')
      } else {
        baseData.push(cumulative + item.value)
        increaseData.push('-')
        decreaseData.push(Math.abs(item.value))
        totalData.push('-')
      }
      cumulative += item.value
    }
  })
  
  return {
    ...baseOption,
    tooltip: {
      ...baseOption.tooltip,
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: (params) => {
        const index = params[0].dataIndex
        const item = waterfallData[index]
        
        let content = `<div style="font-weight: 600; margin-bottom: 8px;">${item.name}</div>`
        
        if (item.type === 'base') {
          content += `<div>基准风险: ${baseRisk}%</div>`
        } else if (item.type === 'total') {
          content += `<div>最终风险: ${finalRisk.toFixed(1)}%</div>`
        } else {
          const sign = item.value > 0 ? '+' : ''
          content += `<div>贡献: ${sign}${item.value.toFixed(1)}%</div>`
          content += `<div>影响: ${item.value > 0 ? '增加风险' : '降低风险'}</div>`
        }
        
        return content
      }
    },
    xAxis: {
      type: 'category',
      data: categories,
      axisLabel: {
        ...baseOption.axis?.axisLabel,
        rotate: 45,
        interval: 0
      },
      axisLine: { ...baseOption.axis?.axisLine },
      axisTick: { ...baseOption.axis?.axisTick }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        ...baseOption.axis?.axisLabel,
        formatter: '{value}%'
      },
      axisLine: { ...baseOption.axis?.axisLine },
      axisTick: { ...baseOption.axis?.axisTick },
      splitLine: { ...baseOption.axis?.splitLine }
    },
    series: [
      {
        name: '基准',
        type: 'bar',
        stack: 'waterfall',
        itemStyle: {
          color: 'transparent',
          borderColor: 'transparent'
        },
        data: baseData
      },
      {
        name: '增加风险',
        type: 'bar',
        stack: 'waterfall',
        itemStyle: {
          color: '#f5222d',
          borderRadius: [4, 4, 0, 0]
        },
        label: {
          show: true,
          position: 'top',
          formatter: (params) => {
            if (params.value === '-') return ''
            return `+${params.value}%`
          },
          color: '#f5222d',
          fontSize: 11
        },
        data: increaseData
      },
      {
        name: '降低风险',
        type: 'bar',
        stack: 'waterfall',
        itemStyle: {
          color: '#52c41a',
          borderRadius: [0, 0, 4, 4]
        },
        label: {
          show: true,
          position: 'bottom',
          formatter: (params) => {
            if (params.value === '-') return ''
            return `-${params.value}%`
          },
          color: '#52c41a',
          fontSize: 11
        },
        data: decreaseData
      },
      {
        name: '最终风险',
        type: 'bar',
        stack: 'waterfall',
        itemStyle: {
          color: '#1890ff',
          borderRadius: [4, 4, 0, 0]
        },
        label: {
          show: true,
          position: 'top',
          formatter: (params) => {
            if (params.value === '-') return ''
            return `${params.value}%`
          },
          color: '#1890ff',
          fontSize: 11,
          fontWeight: 600
        },
        data: totalData
      }
    ]
  }
}

// 使用图表composable
const { chartRef, loading, hasError, errorMessage, exportImage } = useChart({
  getOption,
  props: computed(() => ({
    featureNames: props.featureNames,
    featureImportance: props.featureImportance,
    probability: props.probability,
    showDetails: props.showDetails
  }))
})

/**
 * 导出图表为图片
 * @param {String} type - 图片类型
 * @param {String} filename - 文件名
 */
function exportChart(type = 'png', filename = 'factor-waterfall') {
  exportImage(type, filename)
}

// 暴露方法给父组件
defineExpose({
  exportChart
})
</script>

<style scoped>
.factor-waterfall-chart {
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

.chart-legend {
  display: flex;
  justify-content: center;
  gap: 24px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.legend-color.increase {
  background-color: #f5222d;
}

.legend-color.decrease {
  background-color: #52c41a;
}

.legend-color.base {
  background-color: #8c8c8c;
}

.legend-color.total {
  background-color: #1890ff;
}

.legend-text {
  font-size: 12px;
  color: #595959;
}

.factor-details {
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
  margin-bottom: 4px;
}

.detail-name {
  font-size: 14px;
  font-weight: 500;
  color: #262626;
}

.detail-impact {
  font-size: 14px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 4px;
}

.detail-impact.increase {
  color: #f5222d;
  background-color: #fff2f0;
}

.detail-impact.decrease {
  color: #52c41a;
  background-color: #f6ffed;
}

.detail-description {
  font-size: 12px;
  color: #8c8c8c;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .chart-container {
    height: 300px;
    min-height: 200px;
  }
  
  .chart-legend {
    flex-wrap: wrap;
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .factor-waterfall-chart {
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
  
  .chart-legend {
    flex-direction: column;
    align-items: center;
    gap: 8px;
  }
}
</style>