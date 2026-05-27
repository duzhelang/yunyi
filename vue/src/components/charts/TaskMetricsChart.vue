<template>
  <div class="task-metrics-chart">
    <div class="metrics-radar-wrap">
      <div ref="chartRef" class="chart-element"></div>
    </div>
    <div class="metrics-bars-wrap">
      <div class="bar-row" v-for="item in metricItems" :key="item.key">
        <span class="bar-label">{{ item.label }}</span>
        <div class="bar-track">
          <div class="bar-fill" :style="getBarStyle(item)"></div>
        </div>
        <span class="bar-value">{{ formatValue(item.value) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import * as echarts from 'echarts'
import { useChartTheme } from './chartTheme'

const props = defineProps({
  task: { type: Object, default: null }
})

const chartRef = ref(null)
let chartInstance = null
let resizeTimer = null

const theme = useChartTheme()

const metricItems = computed(() => {
  if (!props.task) return []
  return [
    { key: 'accuracy', label: '准确率', value: props.task.accuracy, max: 1, color: '#1890ff' },
    { key: 'precisionRate', label: '精确率', value: props.task.precisionRate, max: 1, color: '#52c41a' },
    { key: 'recallRate', label: '召回率', value: props.task.recallRate, max: 1, color: '#faad14' },
    { key: 'f1Score', label: 'F1 分数', value: props.task.f1Score, max: 1, color: '#722ed1' },
    { key: 'auc', label: 'AUC', value: props.task.auc, max: 1, color: '#13c2c2' }
  ]
})

function formatValue(val) {
  if (val == null) return '-'
  return (val * 100).toFixed(1) + '%'
}

function getBarStyle(item) {
  const pct = item.value != null ? Math.min(item.value / item.max * 100, 100) : 0
  return {
    width: pct + '%',
    backgroundColor: item.color
  }
}

function getOption() {
  const base = theme.getBaseOption()
  const items = metricItems.value
  if (items.length === 0) return base

  const indicator = items.map(m => ({ name: m.label, max: 100 }))
  const data = items.map(m => m.value != null ? +(m.value * 100).toFixed(1) : 0)

  return {
    ...base,
    tooltip: {
      ...base.tooltip,
      trigger: 'item'
    },
    radar: {
      indicator,
      shape: 'polygon',
      radius: '55%',
      center: ['50%', '52%'],
      axisName: {
        color: '#595959',
        fontSize: 11,
        padding: [0, 4]
      },
      splitArea: {
        areaStyle: {
          color: ['rgba(24, 144, 255, 0.03)', 'rgba(24, 144, 255, 0.06)']
        }
      },
      splitLine: {
        lineStyle: { color: '#e8e8e8' }
      },
      axisLine: {
        lineStyle: { color: '#e8e8e8' }
      }
    },
    series: [{
      type: 'radar',
      data: [{
        value: data,
        name: '性能指标',
        areaStyle: { color: 'rgba(24, 144, 255, 0.15)' },
        lineStyle: { color: '#1890ff', width: 2 },
        itemStyle: { color: '#1890ff' },
        label: { show: false }
      }]
    }]
  }
}

function initChart() {
  if (!chartRef.value) return
  if (chartInstance) {
    chartInstance.dispose()
  }
  chartInstance = echarts.init(chartRef.value, theme.getTheme())
  const option = getOption()
  if (option) {
    chartInstance.setOption(option)
  }
}

function handleResize() {
  clearTimeout(resizeTimer)
  resizeTimer = setTimeout(() => {
    if (chartInstance) {
      chartInstance.resize()
    }
  }, 100)
}

watch(() => props.task, () => {
  nextTick(() => {
    if (chartInstance) {
      const option = getOption()
      if (option) chartInstance.setOption(option, { notMerge: true })
      chartInstance.resize()
    }
  })
}, { deep: true })

onMounted(() => {
  nextTick(() => {
    setTimeout(() => {
      initChart()
      window.addEventListener('resize', handleResize)
    }, 300)
  })
})

onUnmounted(() => {
  clearTimeout(resizeTimer)
  window.removeEventListener('resize', handleResize)
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})
</script>

<style scoped>
.task-metrics-chart {
  display: flex;
  align-items: stretch;
  gap: 20px;
  padding: 8px 0;
}

.metrics-radar-wrap {
  flex: 0 0 260px;
  height: 220px;
}

.chart-element {
  width: 260px;
  height: 220px;
}

.metrics-bars-wrap {
  flex: 0 0 850px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 10px;
}

.bar-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.bar-label {
  flex: 0 0 50px;
  font-size: 13px;
  color: #595959;
  text-align: right;
  white-space: nowrap;
}

.bar-track {
  flex: 1;
  height: 14px;
  background: #f0f2f5;
  border-radius: 7px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 7px;
  transition: width 0.6s cubic-bezier(0.22, 1, 0.36, 1);
  min-width: 4px;
}

.bar-value {
  flex: 0 0 44px;
  font-size: 13px;
  font-weight: 600;
  color: #262626;
  text-align: left;
  white-space: nowrap;
}
</style>
