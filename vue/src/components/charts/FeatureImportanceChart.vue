<template>
  <div class="feature-importance-chart">
    <div class="chart-header">
      <h3 class="chart-title">特征重要性分析</h3>
      <p class="chart-subtitle">SHAP特征贡献度排序</p>
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
    </div>
    
    <!-- 特征详情 -->
    <div class="feature-details" v-if="showDetails">
      <div class="detail-header">
        <span class="detail-title">特征贡献详情</span>
        <span class="detail-count">共 {{ sortedFeatures.length }} 个特征</span>
      </div>
      
      <div class="detail-list">
        <div class="detail-item" v-for="item in sortedFeatures" :key="item.name">
          <div class="detail-left">
            <div class="detail-rank" :class="item.importanceType">
              {{ item.rank }}
            </div>
            <div class="detail-info">
              <div class="detail-name">{{ item.displayName }}</div>
              <div class="detail-value">
                当前值: {{ item.currentValue }}
              </div>
            </div>
          </div>
          <div class="detail-right">
            <div class="detail-impact" :class="item.importanceType">
              {{ item.impactText }}
            </div>
            <div class="detail-bar-container">
              <div 
                class="detail-bar" 
                :class="item.importanceType"
                :style="{ width: item.barWidth + '%' }"
              ></div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 分析说明 -->
    <div class="analysis-note">
      <el-icon><InfoFilled /></el-icon>
      <span>SHAP值为正表示增加患病风险，为负表示降低患病风险。绝对值越大，影响越显著。</span>
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
  // 特征当前值
  features: {
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
  },
  // 显示的最大特征数量
  maxFeatures: {
    type: Number,
    default: 8
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

// 计算排序后的特征数据
const sortedFeatures = computed(() => {
  const features = props.featureNames.map((name, index) => {
    const importance = props.featureImportance[index] || 0
    const currentValue = props.features[name]
    
    return {
      name,
      displayName: featureNameMap[name] || name,
      importance,
      importanceType: importance > 0 ? 'increase' : 'decrease',
      impactText: importance > 0 
        ? `+${(importance * 100).toFixed(1)}%` 
        : `${(importance * 100).toFixed(1)}%`,
      currentValue: currentValue !== undefined ? currentValue : 'N/A',
      absImportance: Math.abs(importance)
    }
  })
  
  // 按绝对值排序
  return features
    .sort((a, b) => b.absImportance - a.absImportance)
    .slice(0, props.maxFeatures)
    .map((item, index) => ({
      ...item,
      rank: index + 1,
      barWidth: (item.absImportance / Math.max(...features.map(f => f.absImportance))) * 100
    }))
})

/**
 * 获取图表配置
 * @returns {Object} ECharts配置对象
 */
function getOption() {
  const baseOption = theme.getBaseOption()
  
  // 准备图表数据
  const categories = sortedFeatures.value.map(item => item.displayName)
  const data = sortedFeatures.value.map(item => item.importance * 100)
  
  // 计算最大绝对值
  const maxAbsValue = Math.max(...data.map(Math.abs))
  
  return {
    ...baseOption,
    tooltip: {
      ...baseOption.tooltip,
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: (params) => {
        const item = sortedFeatures.value[params[0].dataIndex]
        return `
          <div style="font-weight: 600; margin-bottom: 8px;">${item.displayName}</div>
          <div style="margin-bottom: 4px;">SHAP贡献: ${item.impactText}</div>
          <div style="margin-bottom: 4px;">当前值: ${item.currentValue}</div>
          <div>影响: ${item.importance > 0 ? '增加患病风险' : '降低患病风险'}</div>
        `
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      name: 'SHAP贡献值 (%)',
      nameTextStyle: {
        color: '#595959',
        fontSize: 12
      },
      axisLabel: {
        ...baseOption.axis?.axisLabel,
        formatter: (value) => {
          return value > 0 ? `+${value}%` : `${value}%`
        }
      },
      axisLine: { ...baseOption.axis?.axisLine },
      axisTick: { ...baseOption.axis?.axisTick },
      splitLine: { ...baseOption.axis?.splitLine }
    },
    yAxis: {
      type: 'category',
      data: categories,
      axisLabel: {
        ...baseOption.axis?.axisLabel,
        width: 100,
        overflow: 'truncate'
      },
      axisLine: { ...baseOption.axis?.axisLine },
      axisTick: { ...baseOption.axis?.axisTick }
    },
    series: [
      {
        name: '特征重要性',
        type: 'bar',
        data: data.map((value, index) => ({
          value,
          itemStyle: {
            color: value > 0 ? '#f5222d' : '#52c41a',
            borderRadius: value > 0 ? [0, 4, 4, 0] : [4, 0, 0, 4]
          }
        })),
        label: {
          show: true,
          position: 'right',
          formatter: (params) => {
            const value = params.value
            return value > 0 ? `+${value}%` : `${value}%`
          },
          color: '#595959',
          fontSize: 11
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowColor: 'rgba(0, 0, 0, 0.3)'
          }
        }
      },
      // 零线标记
      {
        type: 'line',
        markLine: {
          silent: true,
          symbol: 'none',
          label: {
            show: false
          },
          lineStyle: {
            color: '#8c8c8c',
            width: 1,
            type: 'solid'
          },
          data: [
            {
              xAxis: 0
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
    featureNames: props.featureNames,
    featureImportance: props.featureImportance,
    features: props.features,
    showDetails: props.showDetails,
    maxFeatures: props.maxFeatures,
    sortedFeatures: sortedFeatures.value
  }))
})

/**
 * 导出图表为图片
 * @param {String} type - 图片类型
 * @param {String} filename - 文件名
 */
function exportChart(type = 'png', filename = 'feature-importance') {
  exportImage(type, filename)
}

// 暴露方法给父组件
defineExpose({
  exportChart
})
</script>

<style scoped>
.feature-importance-chart {
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

.legend-text {
  font-size: 12px;
  color: #595959;
}

.feature-details {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.detail-title {
  font-size: 14px;
  font-weight: 600;
  color: #262626;
}

.detail-count {
  font-size: 12px;
  color: #8c8c8c;
}

.detail-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #fafafa;
  border-radius: 6px;
}

.detail-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.detail-rank {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
}

.detail-rank.increase {
  background-color: #f5222d;
}

.detail-rank.decrease {
  background-color: #52c41a;
}

.detail-info {
  display: flex;
  flex-direction: column;
}

.detail-name {
  font-size: 14px;
  font-weight: 500;
  color: #262626;
}

.detail-value {
  font-size: 12px;
  color: #8c8c8c;
}

.detail-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.detail-impact {
  font-size: 14px;
  font-weight: 600;
}

.detail-impact.increase {
  color: #f5222d;
}

.detail-impact.decrease {
  color: #52c41a;
}

.detail-bar-container {
  width: 100px;
  height: 4px;
  background: #f0f0f0;
  border-radius: 2px;
  overflow: hidden;
}

.detail-bar {
  height: 100%;
  border-radius: 2px;
  transition: width 0.6s ease;
}

.detail-bar.increase {
  background-color: #f5222d;
}

.detail-bar.decrease {
  background-color: #52c41a;
}

.analysis-note {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-top: 16px;
  padding: 12px;
  background: #f6f6f6;
  border-radius: 6px;
  font-size: 12px;
  color: #8c8c8c;
  line-height: 1.5;
}

.analysis-note .el-icon {
  margin-top: 2px;
  color: #1890ff;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .chart-container {
    height: 300px;
    min-height: 200px;
  }
  
  .detail-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .detail-right {
    align-items: flex-start;
    width: 100%;
  }
  
  .detail-bar-container {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .feature-importance-chart {
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