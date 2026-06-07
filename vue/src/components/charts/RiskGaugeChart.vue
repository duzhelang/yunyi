<template>
  <div class="risk-gauge-chart">
    <div class="chart-header">
      <h3 class="chart-title">风险仪表盘</h3>
      <p class="chart-subtitle">糖尿病风险概率评估</p>
    </div>
    
    <div class="chart-container" v-loading="loading">
      <div ref="chartRef" class="chart-element"></div>
    </div>
    
    <!-- 风险数值展示（仪表盘下方） -->
    <div class="risk-info-below" v-if="showOverlay">
      <div class="risk-value" :style="{ color: riskColor }">
        {{ riskProbability }}%
      </div>
      <div class="risk-label" :style="{ color: riskColor }">
        {{ riskLevelText }}
      </div>
      <div class="confidence-interval" v-if="showConfidence">
        置信区间: {{ confidenceMin }}% - {{ confidenceMax }}%
      </div>
    </div>
    
    <!-- 风险说明 -->
    <div class="risk-description" v-if="showDescription">
      <div class="risk-item" v-for="item in riskDescriptions" :key="item.level">
        <span class="risk-dot" :style="{ backgroundColor: item.color }"></span>
        <span class="risk-text">{{ item.text }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, watch } from 'vue'
import { useChart } from './useChart'
import { useChartTheme } from './chartTheme'
import { useChartStore } from '@/store/chartStore'

// 组件属性定义
const props = defineProps({
  // 风险概率
  probability: {
    type: Number,
    default: 0
  },
  // 风险等级
  riskLevel: {
    type: String,
    default: 'low',
    validator: (value) => ['low', 'medium', 'high'].includes(value)
  },
  // 置信区间
  confidenceInterval: {
    type: Array,
    default: () => [0, 0]
  },
  // 是否显示置信区间
  showConfidence: {
    type: Boolean,
    default: true
  },
  // 是否显示覆盖层
  showOverlay: {
    type: Boolean,
    default: true
  },
  // 是否显示风险说明
  showDescription: {
    type: Boolean,
    default: true
  },
  // 图表高度
  height: {
    type: String,
    default: '300px'
  }
})

// 获取主题配置
const theme = useChartTheme()

// 获取图表状态管理
const chartStore = useChartStore()

// 计算风险概率（优先使用props，其次使用store）
const riskProbability = computed(() => {
  return props.probability || chartStore.riskProbability
})

// 计算风险等级（优先使用props，其次使用store）
const currentRiskLevel = computed(() => {
  return props.riskLevel || chartStore.riskLevel
})

// 计算置信区间（优先使用props，其次使用store）
const confidenceIntervalData = computed(() => {
  if (props.confidenceInterval[0] !== 0 || props.confidenceInterval[1] !== 0) {
    return props.confidenceInterval
  }
  return chartStore.confidenceInterval
})

// 置信区间最小值
const confidenceMin = computed(() => {
  return Math.round(confidenceIntervalData.value[0] || 0)
})

// 置信区间最大值
const confidenceMax = computed(() => {
  return Math.round(confidenceIntervalData.value[1] || 0)
})

// 风险颜色
const riskColor = computed(() => {
  return theme.getRiskColor(currentRiskLevel.value)
})

// 风险等级文本
const riskLevelText = computed(() => {
  const map = {
    low: '低风险',
    medium: '中风险',
    high: '高风险'
  }
  return map[currentRiskLevel.value] || '未知'
})

// 风险描述
const riskDescriptions = computed(() => [
  {
    level: 'low',
    color: theme.getRiskColor('low'),
    text: '低风险 (0-30%): 继续保持健康生活方式'
  },
  {
    level: 'medium',
    color: theme.getRiskColor('medium'),
    text: '中风险 (30-70%): 建议调整生活习惯并定期检查'
  },
  {
    level: 'high',
    color: theme.getRiskColor('high'),
    text: '高风险 (70-100%): 建议立即咨询医生进行专业评估'
  }
])

/**
 * 获取图表配置
 * @returns {Object} ECharts配置对象
 */
function getOption() {
  const baseOption = theme.getBaseOption()
  
  return {
    ...baseOption,
    series: [
      {
        name: '风险概率',
        type: 'gauge',
        startAngle: 200,
        endAngle: -20,
        min: 0,
        max: 100,
        splitNumber: 10,
        radius: '85%',
        center: ['50%', '50%'],
        itemStyle: {
          color: riskColor.value
        },
        progress: {
          show: true,
          roundCap: true,
          width: 18,
          itemStyle: {
            color: riskColor.value
          }
        },
        pointer: {
          icon: 'path://M2090.36389,615.30999 L2## 090.36389,615.30999 C2## 091.48372,627.15996 2## 096.01466,634.86993 2## 103.45312,639.87989 C2## 107.42471,642.38987 2## 112.01432,643.63986 2## 117.16599,643.63986 C2## 122.31766,643.63986 2## 126.90727,642.38987 2## 130.87886,639.87989 C2## 138.31732,634.86993 2## 142.84826,627.15996 2## 143.96809,615.30999 L2## 144.02389,612.23999 L2## 134.49669,612.23999 Z',
          length: '75%',
          width: 16,
          offsetCenter: [0, '-10%'],
          itemStyle: {
            color: 'auto',
            shadowColor: 'rgba(0, 0, 0, 0.3)',
            shadowBlur: 5,
            shadowOffsetY: 2
          }
        },
        axisLine: {
          roundCap: true,
          lineStyle: {
            width: 18,
            color: [
              [0.3, '#91d5ff'],
              [0.7, '#ffe58f'],
              [1, '#ffccc7']
            ]
          }
        },
        axisTick: {
          length: 8,
          lineStyle: {
            color: 'auto',
            width: 2
          }
        },
        splitLine: {
          length: 16,
          lineStyle: {
            color: 'auto',
            width: 3
          }
        },
        axisLabel: {
          distance: 25,
          color: '#999',
          fontSize: 12,
          formatter: function (value) {
            if (value === 0 || value === 50 || value === 100) {
              return value + '%'
            }
            return ''
          }
        },
        anchor: {
          show: true,
          showAbove: true,
          size: 20,
          itemStyle: {
            borderWidth: 8,
            borderColor: riskColor.value,
            color: '#fff'
          }
        },
        title: {
          show: false
        },
        detail: {
          show: false
        },
        data: [
          {
            value: riskProbability.value
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
    probability: riskProbability.value,
    riskLevel: currentRiskLevel.value,
    confidenceInterval: confidenceIntervalData.value
  }))
})

// 监听风险数据变化
watch(
  () => [riskProbability.value, currentRiskLevel.value],
  () => {
    // 图表会通过useChart的watch自动更新
  }
)

/**
 * 导出图表为图片
 * @param {String} type - 图片类型
 * @param {String} filename - 文件名
 */
function exportChart(type = 'png', filename = 'risk-gauge') {
  exportImage(type, filename)
}

// 暴露方法给父组件
defineExpose({
  exportChart
})
</script>

<style scoped>
.risk-gauge-chart {
  background: linear-gradient(180deg, #ffffff 0%, #f8faff 100%);
  border-radius: 14px;
  padding: 20px 20px 16px;
  box-shadow: 0 2px 12px rgba(64, 128, 255, 0.06);
  border: 1px solid #e8ecf1;
}

.chart-header {
  margin-bottom: 8px;
  text-align: center;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #1D2129;
  margin: 0 0 2px 0;
  letter-spacing: 0.5px;
}

.chart-subtitle {
  font-size: 12px;
  color: #909399;
  margin: 0;
}

.chart-container {
  position: relative;
  height: v-bind(height);
  min-height: 220px;
}

.chart-element {
  width: 100%;
  height: 100%;
}

.risk-info-below {
  text-align: center;
  padding: 8px 0 4px;
}

.risk-value {
  font-size: 40px;
  font-weight: 700;
  line-height: 1.1;
  letter-spacing: -1px;
}

.risk-label {
  font-size: 16px;
  font-weight: 600;
  margin-top: 2px;
  letter-spacing: 0.5px;
}

.confidence-interval {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  background: rgba(64, 128, 255, 0.04);
  display: inline-block;
  padding: 4px 12px;
  border-radius: 10px;
  border: 1px solid rgba(64, 128, 255, 0.08);
}

.risk-description {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f2f5;
}

.risk-item {
  display: flex;
  align-items: center;
  margin-bottom: 6px;
  font-size: 12px;
  color: #606266;
  line-height: 1.5;
}

.risk-item:last-child {
  margin-bottom: 0;
}

.risk-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  margin-right: 8px;
  flex-shrink: 0;
  box-shadow: 0 0 4px rgba(0, 0, 0, 0.1);
}

.risk-text {
  flex: 1;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .risk-value {
    font-size: 32px;
  }
  
  .risk-label {
    font-size: 14px;
  }
  
  .chart-container {
    height: 220px;
    min-height: 180px;
  }
}

@media (max-width: 480px) {
  .risk-value {
    font-size: 26px;
  }
  
  .risk-label {
    font-size: 13px;
  }
  
  .chart-container {
    height: 180px;
    min-height: 160px;
  }
  
  .risk-gauge-chart {
    padding: 14px;
  }
}
</style>