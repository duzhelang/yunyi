<template>
  <div class="risk-distribution-chart">
    <div class="chart-header">
      <h3 class="chart-title">风险分布</h3>
      <p class="chart-subtitle">风险等级评估结果</p>
    </div>
    
    <div class="chart-container" v-loading="loading">
      <div ref="chartRef" class="chart-element"></div>
      
      <!-- 中心信息覆盖层 -->
      <div class="center-overlay">
        <div class="probability-circle" :style="{ borderColor: riskColor }">
          <div class="probability-value" :style="{ color: riskColor }">
            {{ probability.toFixed(1) }}%
          </div>
          <div class="probability-label">患病概率</div>
        </div>
        <div class="risk-level" :style="{ color: riskColor, backgroundColor: riskBgColor }">
          {{ riskLevelText }}
        </div>
      </div>
    </div>
    
    <!-- 风险等级图例 -->
    <div class="risk-legend">
      <div class="legend-item" v-for="item in riskLegend" :key="item.level">
        <div class="legend-color" :style="{ backgroundColor: item.color }"></div>
        <div class="legend-info">
          <span class="legend-label">{{ item.label }}</span>
          <span class="legend-range">{{ item.range }}</span>
        </div>
        <span class="legend-percentage">{{ item.percentage }}%</span>
      </div>
    </div>
    
    <!-- 风险建议 -->
    <div class="risk-advice" :style="{ borderColor: riskColor, backgroundColor: riskBgColor }">
      <div class="advice-header">
        <el-icon><InfoFilled /></el-icon>
        <span :style="{ color: riskColor }">风险评估建议</span>
      </div>
      <div class="advice-content">
        {{ riskAdvice }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useChart } from './useChart'
import { useChartTheme } from './chartTheme'
import { chartUtils } from './index'
import { InfoFilled } from '@element-plus/icons-vue'

// 组件属性定义
const props = defineProps({
  // 预测概率
  probability: {
    type: Number,
    default: 0,
    validator: (value) => value >= 0 && value <= 100
  },
  // 风险等级
  riskLevel: {
    type: String,
    default: 'low',
    validator: (value) => ['low', 'medium', 'high'].includes(value)
  },
  // 图表高度
  height: {
    type: String,
    default: '350px'
  }
})

// 获取主题配置
const theme = useChartTheme()

// 计算风险颜色
const riskColor = computed(() => {
  return theme.getRiskColor(props.riskLevel)
})

// 计算风险背景颜色
const riskBgColor = computed(() => {
  const colorMap = {
    low: '#f6ffed',
    medium: '#fffbe6',
    high: '#fff2f0'
  }
  return colorMap[props.riskLevel] || '#f6ffed'
})

// 计算风险等级文本
const riskLevelText = computed(() => {
  return chartUtils.getRiskText(props.riskLevel)
})

// 计算风险建议
const riskAdvice = computed(() => {
  const probability = props.probability
  
  if (probability < 30) {
    return '您的患病风险较低，建议保持健康的生活方式，定期进行体检，继续维持良好的饮食和运动习惯。'
  } else if (probability < 70) {
    return '您的患病风险处于中等水平，建议加强健康监测，改善饮食结构，增加运动量，控制体重，定期进行血糖检测。'
  } else {
    return '您的患病风险较高，建议立即咨询专业医生，进行全面的健康评估，制定个性化的干预方案，加强血糖监测频率。'
  }
})

// 计算风险等级图例数据
const riskLegend = computed(() => {
  const probability = props.probability
  
  // 计算各风险等级的百分比（示例逻辑）
  let lowPercentage, mediumPercentage, highPercentage
  
  if (probability < 30) {
    lowPercentage = 70
    mediumPercentage = 20
    highPercentage = 10
  } else if (probability < 70) {
    lowPercentage = 30
    mediumPercentage = 50
    highPercentage = 20
  } else {
    lowPercentage = 10
    mediumPercentage = 30
    highPercentage = 60
  }
  
  return [
    {
      level: 'low',
      label: '低风险',
      color: '#52c41a',
      range: '0% - 30%',
      percentage: lowPercentage
    },
    {
      level: 'medium',
      label: '中风险',
      color: '#faad14',
      range: '30% - 70%',
      percentage: mediumPercentage
    },
    {
      level: 'high',
      label: '高风险',
      color: '#f5222d',
      range: '70% - 100%',
      percentage: highPercentage
    }
  ]
})

/**
 * 获取图表配置
 * @returns {Object} ECharts配置对象
 */
function getOption() {
  const baseOption = theme.getBaseOption()
  
  // 准备饼图数据
  const pieData = riskLegend.value.map(item => ({
    name: item.label,
    value: item.percentage,
    itemStyle: {
      color: item.color
    }
  }))
  
  return {
    ...baseOption,
    tooltip: {
      ...baseOption.tooltip,
      trigger: 'item',
      formatter: (params) => {
        return `
          <div style="font-weight: 600; margin-bottom: 4px;">${params.name}</div>
          <div>占比: ${params.value}%</div>
          <div>概率: ${params.percent}%</div>
        `
      }
    },
    series: [
      // 外层环形图
      {
        type: 'pie',
        radius: ['50%', '70%'],
        center: ['50%', '50%'],
        avoidLabelOverlap: false,
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: pieData,
        animation: true,
        animationDuration: 1500,
        animationEasing: 'cubicOut'
      },
      // 内层装饰环
      {
        type: 'pie',
        radius: ['40%', '50%'],
        center: ['50%', '50%'],
        silent: true,
        label: {
          show: false
        },
        data: [
          {
            value: 100,
            itemStyle: {
              color: '#fafafa'
            }
          }
        ]
      }
    ]
  }
}

// 使用图表composable
const { chartRef, loading, hasError, errorMessage, exportImage } = useChart({
  getOption,
  props: computed(() => ({
    probability: props.probability,
    riskLevel: props.riskLevel,
    riskLegend: riskLegend.value
  }))
})

/**
 * 导出图表为图片
 * @param {String} type - 图片类型
 * @param {String} filename - 文件名
 */
function exportChart(type = 'png', filename = 'risk-distribution') {
  exportImage(type, filename)
}

// 暴露方法给父组件
defineExpose({
  exportChart
})
</script>

<style scoped>
.risk-distribution-chart {
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

.center-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  pointer-events: none;
  z-index: 10;
}

.probability-circle {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  border: 3px solid;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.95);
  margin: 0 auto 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.probability-value {
  font-size: 24px;
  font-weight: 700;
  line-height: 1.2;
}

.probability-label {
  font-size: 11px;
  color: #8c8c8c;
  margin-top: 2px;
}

.risk-level {
  font-size: 14px;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 12px;
  display: inline-block;
}

.risk-legend {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.legend-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.legend-item:last-child {
  border-bottom: none;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
  margin-right: 12px;
}

.legend-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.legend-label {
  font-size: 14px;
  font-weight: 500;
  color: #262626;
}

.legend-range {
  font-size: 12px;
  color: #8c8c8c;
}

.legend-percentage {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.risk-advice {
  margin-top: 16px;
  padding: 16px;
  border: 1px solid;
  border-radius: 8px;
}

.advice-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
}

.advice-content {
  font-size: 13px;
  color: #595959;
  line-height: 1.6;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .chart-container {
    height: 280px;
    min-height: 200px;
  }
  
  .probability-circle {
    width: 80px;
    height: 80px;
  }
  
  .probability-value {
    font-size: 20px;
  }
  
  .risk-level {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .risk-distribution-chart {
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
  
  .probability-circle {
    width: 70px;
    height: 70px;
  }
  
  .probability-value {
    font-size: 18px;
  }
  
  .legend-item {
    flex-wrap: wrap;
    gap: 4px;
  }
  
  .legend-info {
    flex-direction: row;
    gap: 8px;
  }
}
</style>