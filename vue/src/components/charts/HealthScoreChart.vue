<template>
  <div class="health-score-chart">
    <div class="chart-header">
      <h3 class="chart-title">健康评分</h3>
      <p class="chart-subtitle">综合健康状况评估</p>
    </div>
    
    <div class="chart-container" v-loading="loading">
      <div ref="chartRef" class="chart-element"></div>
      
      <!-- 评分覆盖层 -->
      <div class="score-overlay">
        <div class="score-circle" :style="{ borderColor: scoreColor }">
          <div class="score-value" :style="{ color: scoreColor }">
            {{ healthScore }}
          </div>
          <div class="score-label">分</div>
        </div>
        <div class="score-level" :style="{ color: scoreColor }">
          {{ scoreLevelText }}
        </div>
        <div class="score-description">
          {{ scoreDescription }}
        </div>
      </div>
    </div>
    
    <!-- 评分详情 -->
    <div class="score-details" v-if="showDetails">
      <div class="detail-item" v-for="item in scoreDetails" :key="item.name">
        <div class="detail-header">
          <span class="detail-name">{{ item.name }}</span>
          <span class="detail-score" :style="{ color: item.color }">
            {{ item.score }}分
          </span>
        </div>
        
        <div class="detail-bar">
          <div class="detail-progress" :style="getDetailProgressStyle(item)"></div>
        </div>
        
        <div class="detail-description">
          {{ item.description }}
        </div>
      </div>
    </div>
    
    <!-- 健康建议 -->
    <div class="health-advice" v-if="showAdvice">
      <div class="advice-header">
        <el-icon><InfoFilled /></el-icon>
        <span>健康建议</span>
      </div>
      <div class="advice-content">
        {{ healthAdvice }}
      </div>
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
  // 健康评分 (0-100)
  score: {
    type: Number,
    default: 0,
    validator: (value) => value >= 0 && value <= 100
  },
  // 是否显示详情
  showDetails: {
    type: Boolean,
    default: true
  },
  // 是否显示健康建议
  showAdvice: {
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

// 计算健康评分（优先使用props，其次使用store）
const healthScore = computed(() => {
  return props.score || chartStore.getHealthScore()
})

// 计算评分颜色
const scoreColor = computed(() => {
  if (healthScore.value >= 80) return '#52c41a'
  if (healthScore.value >= 60) return '#faad14'
  if (healthScore.value >= 40) return '#fa8c16'
  return '#f5222d'
})

// 计算评分等级文本
const scoreLevelText = computed(() => {
  if (healthScore.value >= 80) return '优秀'
  if (healthScore.value >= 60) return '良好'
  if (healthScore.value >= 40) return '一般'
  return '较差'
})

// 计算评分描述
const scoreDescription = computed(() => {
  if (healthScore.value >= 80) return '您的健康状况非常好，请继续保持！'
  if (healthScore.value >= 60) return '您的健康状况良好，但仍有改善空间。'
  if (healthScore.value >= 40) return '您的健康状况一般，建议关注生活习惯。'
  return '您的健康状况较差，建议尽快咨询医生。'
})

// 计算健康建议
const healthAdvice = computed(() => {
  const score = healthScore.value
  
  if (score >= 80) {
    return '继续保持当前的生活方式，定期进行健康检查，适当增加运动强度。'
  } else if (score >= 60) {
    return '建议增加运动频率，改善饮食结构，保持规律作息，定期监测血糖水平。'
  } else if (score >= 40) {
    return '建议减少高糖高脂食物摄入，增加有氧运动，控制体重，定期进行血糖检测。'
  } else {
    return '建议立即咨询专业医生，进行全面的健康评估，制定个性化的健康管理方案。'
  }
})

// 计算评分详情
const scoreDetails = computed(() => {
  const riskProbability = chartStore.riskProbability
  const featureValues = chartStore.featureValues
  
  const details = []
  
  // 风险评分
  const riskScore = Math.max(0, Math.min(100, Math.round(100 - riskProbability)))
  details.push({
    name: '风险评分',
    score: riskScore,
    color: theme.getRiskColorByValue(riskProbability),
    description: riskScore >= 70 ? '风险较低' : riskScore >= 40 ? '风险中等' : '风险较高'
  })
  
  // 血糖评分
  if (featureValues.Glucose) {
    const glucose = featureValues.Glucose
    let glucoseScore = 100
    if (glucose > 126) glucoseScore = 30
    else if (glucose > 100) glucoseScore = 60
    else if (glucose < 70) glucoseScore = 70
    
    details.push({
      name: '血糖评分',
      score: glucoseScore,
      color: glucoseScore >= 70 ? '#52c41a' : glucoseScore >= 40 ? '#faad14' : '#f5222d',
      description: glucose <= 100 ? '血糖水平正常' : glucose <= 126 ? '血糖偏高' : '血糖过高'
    })
  }
  
  // BMI评分
  if (featureValues.BMI) {
    const bmi = featureValues.BMI
    let bmiScore = 100
    if (bmi > 30) bmiScore = 40
    else if (bmi > 25) bmiScore = 60
    else if (bmi < 18.5) bmiScore = 70
    
    details.push({
      name: 'BMI评分',
      score: bmiScore,
      color: bmiScore >= 70 ? '#52c41a' : bmiScore >= 40 ? '#faad14' : '#f5222d',
      description: bmi >= 18.5 && bmi <= 24 ? '体重正常' : bmi < 18.5 ? '体重偏轻' : '体重偏重'
    })
  }
  
  // 年龄评分
  if (featureValues.Age) {
    const age = featureValues.Age
    let ageScore = 100
    if (age > 60) ageScore = 60
    else if (age > 45) ageScore = 80
    
    details.push({
      name: '年龄评分',
      score: ageScore,
      color: ageScore >= 70 ? '#52c41a' : ageScore >= 40 ? '#faad14' : '#f5222d',
      description: age <= 45 ? '年龄风险较低' : age <= 60 ? '年龄风险中等' : '年龄风险较高'
    })
  }
  
  return details
})

/**
 * 获取详情进度条样式
 * @param {Object} item - 详情项
 * @returns {Object} 样式对象
 */
function getDetailProgressStyle(item) {
  return {
    width: `${item.score}%`,
    backgroundColor: item.color
  }
}

/**
 * 获取图表配置
 * @returns {Object} ECharts配置对象
 */
function getOption() {
  const baseOption = theme.getBaseOption()
  const score = healthScore.value
  const color = scoreColor.value
  
  // 创建渐变色
  const gradient = theme.createGradient([color, color + '40'])
  
  return {
    ...baseOption,
    series: [
      {
        type: 'pie',
        radius: ['70%', '85%'],
        center: ['50%', '50%'],
        startAngle: 90,
        silent: true,
        label: {
          show: false
        },
        data: [
          {
            value: score,
            itemStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 1,
                y2: 0,
                colorStops: [
                  { offset: 0, color: color },
                  { offset: 1, color: color + 'cc' }
                ]
              },
              shadowBlur: 10,
              shadowColor: color + '40'
            }
          },
          {
            value: 100 - score,
            itemStyle: {
              color: '#f0f0f0'
            }
          }
        ],
        animation: true,
        animationDuration: 1500,
        animationEasing: 'cubicOut'
      },
      {
        type: 'pie',
        radius: ['60%', '70%'],
        center: ['50%', '50%'],
        startAngle: 90,
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
    score: healthScore.value,
    scoreColor: scoreColor.value
  }))
})

// 监听评分变化
watch(
  () => healthScore.value,
  () => {
    // 图表会通过useChart的watch自动更新
  }
)

/**
 * 导出图表为图片
 * @param {String} type - 图片类型
 * @param {String} filename - 文件名
 */
function exportChart(type = 'png', filename = 'health-score') {
  exportImage(type, filename)
}

// 暴露方法给父组件
defineExpose({
  exportChart
})
</script>

<style scoped>
.health-score-chart {
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

.score-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  pointer-events: none;
  z-index: 10;
}

.score-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 4px solid;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.9);
  margin: 0 auto 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.score-value {
  font-size: 42px;
  font-weight: 700;
  line-height: 1.2;
}

.score-label {
  font-size: 14px;
  color: #8c8c8c;
  margin-top: 4px;
}

.score-level {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.score-description {
  font-size: 12px;
  color: #595959;
  background: rgba(255, 255, 255, 0.9);
  padding: 8px 12px;
  border-radius: 6px;
  max-width: 200px;
  line-height: 1.4;
}

.score-details {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.detail-item {
  margin-bottom: 16px;
  padding-bottom: 16px;
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

.detail-score {
  font-size: 14px;
  font-weight: 600;
}

.detail-bar {
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 8px;
}

.detail-progress {
  height: 100%;
  border-radius: 4px;
  transition: width 0.6s ease;
}

.detail-description {
  font-size: 12px;
  color: #8c8c8c;
}

.health-advice {
  margin-top: 16px;
  padding: 16px;
  background: #f6ffed;
  border: 1px solid #b7eb8f;
  border-radius: 8px;
}

.advice-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #52c41a;
  margin-bottom: 8px;
}

.advice-content {
  font-size: 13px;
  color: #595959;
  line-height: 1.6;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .score-circle {
    width: 100px;
    height: 100px;
  }
  
  .score-value {
    font-size: 32px;
  }
  
  .score-level {
    font-size: 16px;
  }
  
  .chart-container {
    height: 250px;
    min-height: 200px;
  }
  
  .score-description {
    max-width: 160px;
  }
}

@media (max-width: 480px) {
  .score-circle {
    width: 80px;
    height: 80px;
  }
  
  .score-value {
    font-size: 28px;
  }
  
  .score-level {
    font-size: 14px;
  }
  
  .chart-container {
    height: 200px;
    min-height: 180px;
  }
  
  .health-score-chart {
    padding: 12px;
  }
  
  .chart-title {
    font-size: 16px;
  }
  
  .chart-subtitle {
    font-size: 12px;
  }
  
  .score-description {
    max-width: 140px;
    font-size: 11px;
    padding: 6px 8px;
  }
}
</style>