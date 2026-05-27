/**
 * 图表组件统一导出
 * 提供所有图表组件的统一导入接口
 */

// 导入第一阶段图表组件
import RiskGaugeChart from './RiskGaugeChart.vue'
import HealthRadarChart from './HealthRadarChart.vue'
import IndicatorCompareChart from './IndicatorCompareChart.vue'
import HealthScoreChart from './HealthScoreChart.vue'

// 导入第二阶段图表组件
import RiskHeatmapChart from './RiskHeatmapChart.vue'
import FactorWaterfallChart from './FactorWaterfallChart.vue'
import ConfidenceIntervalChart from './ConfidenceIntervalChart.vue'
import RiskDistributionChart from './RiskDistributionChart.vue'
import FeatureImportanceChart from './FeatureImportanceChart.vue'

// 导入第三阶段图表组件
import TrainingTrendChart from './TrainingTrendChart.vue'
import TrainingRadarCompareChart from './TrainingRadarCompareChart.vue'
import TrainingHyperparamChart from './TrainingHyperparamChart.vue'
import TrainingConfusionHeatmap from './TrainingConfusionHeatmap.vue'
import TaskMetricsChart from './TaskMetricsChart.vue'

// 导入composable
import { useChart } from './useChart'
import { useChartTheme } from './chartTheme'

// 导出所有图表组件
export {
  // 第一阶段图表组件
  RiskGaugeChart,
  HealthRadarChart,
  IndicatorCompareChart,
  HealthScoreChart,
  
  // 第二阶段图表组件
  RiskHeatmapChart,
  FactorWaterfallChart,
  ConfidenceIntervalChart,
  RiskDistributionChart,
  FeatureImportanceChart,
  
  // 第三阶段图表组件
  TrainingTrendChart,
  TrainingRadarCompareChart,
  TrainingHyperparamChart,
  TrainingConfusionHeatmap,
  TaskMetricsChart,
  
  // Composable
  useChart,
  useChartTheme
}

// 默认导出所有图表组件
export default {
  // 第一阶段图表组件
  RiskGaugeChart,
  HealthRadarChart,
  IndicatorCompareChart,
  HealthScoreChart,
  
  // 第二阶段图表组件
  RiskHeatmapChart,
  FactorWaterfallChart,
  ConfidenceIntervalChart,
  RiskDistributionChart,
  FeatureImportanceChart,
  
  // 第三阶段图表组件
  TrainingTrendChart,
  TrainingRadarCompareChart,
  TrainingHyperparamChart,
  TrainingConfusionHeatmap,
  TaskMetricsChart,
  
  // Composable
  useChart,
  useChartTheme
}

/**
 * 图表组件注册插件
 * 可以在Vue应用中全局注册所有图表组件
 */
export const ChartPlugin = {
  install(app) {
    // 注册第一阶段图表组件
    app.component('RiskGaugeChart', RiskGaugeChart)
    app.component('HealthRadarChart', HealthRadarChart)
    app.component('IndicatorCompareChart', IndicatorCompareChart)
    app.component('HealthScoreChart', HealthScoreChart)
    
    // 注册第二阶段图表组件
    app.component('RiskHeatmapChart', RiskHeatmapChart)
    app.component('FactorWaterfallChart', FactorWaterfallChart)
    app.component('ConfidenceIntervalChart', ConfidenceIntervalChart)
    app.component('RiskDistributionChart', RiskDistributionChart)
    app.component('FeatureImportanceChart', FeatureImportanceChart)
    
    // 注册第三阶段图表组件
    app.component('TrainingTrendChart', TrainingTrendChart)
    app.component('TrainingRadarCompareChart', TrainingRadarCompareChart)
    app.component('TrainingHyperparamChart', TrainingHyperparamChart)
    app.component('TrainingConfusionHeatmap', TrainingConfusionHeatmap)
  }
}

/**
 * 图表工具函数
 * 提供图表相关的工具方法
 */
export const chartUtils = {
  /**
   * 格式化百分比
   * @param {Number} value - 数值
   * @param {Number} decimals - 小数位数
   * @returns {String} 格式化后的百分比字符串
   */
  formatPercentage(value, decimals = 1) {
    if (typeof value !== 'number' || isNaN(value)) return '0%'
    return `${value.toFixed(decimals)}%`
  },
  
  /**
   * 格式化数值
   * @param {Number} value - 数值
   * @param {Number} decimals - 小数位数
   * @returns {String} 格式化后的数值字符串
   */
  formatNumber(value, decimals = 0) {
    if (typeof value !== 'number' || isNaN(value)) return '0'
    return value.toFixed(decimals)
  },
  
  /**
   * 获取风险等级颜色
   * @param {String} level - 风险等级 'low' | 'medium' | 'high'
   * @returns {String} 颜色值
   */
  getRiskColor(level) {
    const map = {
      low: '#52c41a',
      medium: '#faad14',
      high: '#f5222d'
    }
    return map[level] || '#1890ff'
  },
  
  /**
   * 获取风险等级文本
   * @param {String} level - 风险等级
   * @returns {String} 风险等级文本
   */
  getRiskText(level) {
    const map = {
      low: '低风险',
      medium: '中风险',
      high: '高风险'
    }
    return map[level] || '未知'
  },
  
  /**
   * 计算健康评分
   * @param {Number} riskProbability - 风险概率
   * @returns {Number} 健康评分 (0-100)
   */
  calculateHealthScore(riskProbability) {
    if (typeof riskProbability !== 'number' || isNaN(riskProbability)) return 0
    return Math.max(0, Math.min(100, Math.round(100 - riskProbability)))
  },
  
  /**
   * 获取评分等级
   * @param {Number} score - 评分
   * @returns {Object} 包含等级、颜色和文本的对象
   */
  getScoreLevel(score) {
    if (score >= 80) {
      return { level: 'excellent', color: '#52c41a', text: '优秀' }
    } else if (score >= 60) {
      return { level: 'good', color: '#1890ff', text: '良好' }
    } else if (score >= 40) {
      return { level: 'fair', color: '#faad14', text: '一般' }
    } else {
      return { level: 'poor', color: '#f5222d', text: '较差' }
    }
  }
}

/**
 * 图表常量定义
 */
export const chartConstants = {
  // 风险等级
  RISK_LEVELS: {
    LOW: 'low',
    MEDIUM: 'medium',
    HIGH: 'high'
  },
  
  // 图表类型
  CHART_TYPES: {
    RISK_GAUGE: 'riskGauge',
    HEALTH_RADAR: 'healthRadar',
    INDICATOR_COMPARE: 'indicatorCompare',
    HEALTH_SCORE: 'healthScore'
  },
  
  // 颜色配置
  COLORS: {
    PRIMARY: '#1890ff',
    SUCCESS: '#52c41a',
    WARNING: '#faad14',
    DANGER: '#f5222d',
    INFO: '#13c2c2'
  },
  
  // 默认图表尺寸
  DEFAULT_SIZES: {
    SMALL: '200px',
    MEDIUM: '300px',
    LARGE: '400px',
    EXTRA_LARGE: '500px'
  }
}