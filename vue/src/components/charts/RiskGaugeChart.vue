<template>
  <div class="risk-gauge-chart">
    <div class="chart-header">
      <h3 class="chart-title">风险仪表盘</h3>
      <p class="chart-subtitle">糖尿病风险概率评估</p>
    </div>
    
    <div class="chart-container" v-loading="loading">
      <div ref="chartRef" class="chart-element"></div>
      
      <!-- 风险信息覆盖层 -->
      <div class="risk-overlay" v-if="showOverlay">
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
        radius: '90%',
        center: ['50%', '55%'],
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

.risk-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -20%);
  text-align: center;
  pointer-events: none;
  z-index: 10;
}

.risk-value {
  font-size: 48px;
  font-weight: 700;
  line-height: 1.2;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.risk-label {
  font-size: 20px;
  font-weight: 600;
  margin-top: 8px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.confidence-interval {
  font-size: 12px;
  color: #8c8c8c;
  margin-top: 8px;
  background: rgba(255, 255, 255, 0.9);
  padding: 4px 8px;
  border-radius: 4px;
}

.risk-description {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.risk-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 12px;
  color: #595959;
}

.risk-item:last-child {
  margin-bottom: 0;
}

.risk-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 8px;
  flex-shrink: 0;
}

.risk-text {
  flex: 1;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .risk-value {
    font-size: 36px;
  }
  
  .risk-label {
    font-size: 16px;
  }
  
  .chart-container {
    height: 250px;
    min-height: 200px;
  }
}

@media (max-width: 480px) {
  .risk-value {
    font-size: 28px;
  }
  
  .risk-label {
    font-size: 14px;
  }
  
  .chart-container {
    height: 200px;
    min-height: 180px;
  }
  
  .risk-gauge-chart {
    padding: 12px;
  }
}
</style>