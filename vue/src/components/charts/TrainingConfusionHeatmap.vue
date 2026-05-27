<template>
  <div class="training-confusion-heatmap">
    <div class="chart-header">
      <h3 class="chart-title">混淆矩阵</h3>
      <p class="chart-subtitle">{{ displayTaskName }}</p>
      <el-tag v-if="isSampleData" size="small" type="warning" effect="plain" class="sample-tag">示例数据</el-tag>
    </div>
    <div class="chart-container" v-loading="loading">
      <div ref="chartRef" class="chart-element"></div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useChart } from './useChart'
import { useChartTheme } from './chartTheme'

const props = defineProps({
  confusionMatrix: { type: [Array, String], default: null },
  taskName: { type: String, default: '' }
})

const theme = useChartTheme()

const isSampleData = ref(false)

const sampleMatrix = [
  [85, 15],
  [10, 90]
]

function parseMatrix(raw) {
  if (!raw) return null
  if (typeof raw === 'string') {
    try { raw = JSON.parse(raw) } catch { return null }
  }
  return Array.isArray(raw) && raw.length >= 2 ? raw : null
}

watch(() => props.confusionMatrix, (val) => {
  isSampleData.value = parseMatrix(val) === null
}, { immediate: true })

const displayTaskName = computed(() => {
  if (props.taskName) return props.taskName
  return isSampleData.value ? '二分类模型（示例）' : '请选择任务'
})

const matrixData = computed(() => {
  return parseMatrix(props.confusionMatrix) || sampleMatrix
})

function getOption() {
  const base = theme.getBaseOption()
  const matrix = matrixData.value

  if (!matrix) {
    return {
      ...base,
      series: [],
      graphic: { type: 'text', left: 'center', top: 'middle', style: { text: '暂无混淆矩阵数据', fontSize: 14, fill: '#8c8c8c' } }
    }
  }

  const labels = ['阴性(0)', '阳性(1)']
  const data = []
  let maxVal = 0

  for (let i = 0; i < matrix.length; i++) {
    for (let j = 0; j < matrix[i].length; j++) {
      const val = matrix[i][j] || 0
      data.push([j, i, val])
      if (val > maxVal) maxVal = val
    }
  }

  return {
    ...base,
    tooltip: {
      ...base.tooltip,
      formatter: (params) => {
        const row = labels[params.data[1]]
        const col = labels[params.data[0]]
        return `<div style="font-weight:600">实际: ${row} / 预测: ${col}</div><div>数量: ${params.data[2]}</div>`
      }
    },
    grid: { left: '5%', right: '18%', bottom: '8%', top: '8%', containLabel: true },
    xAxis: {
      type: 'category',
      data: labels,
      name: '预测',
      nameLocation: 'center',
      nameGap: 30,
      nameTextStyle: { fontSize: 13, color: '#595959' },
      axisLabel: {
        ...base.axis?.axisLabel,
        fontSize: 13
      },
      axisLine: { ...base.axis?.axisLine }
    },
    yAxis: {
      type: 'category',
      data: labels,
      name: '实际',
      nameLocation: 'center',
      nameGap: 40,
      nameTextStyle: { fontSize: 13, color: '#595959' },
      axisLabel: {
        ...base.axis?.axisLabel,
        fontSize: 13
      },
      axisLine: { ...base.axis?.axisLine }
    },
    visualMap: {
      min: 0,
      max: maxVal,
      calculable: false,
      orient: 'vertical',
      right: '0%',
      top: 'center',
      itemWidth: 10,
      itemHeight: 80,
      inRange: { color: ['#e6f7ff', '#1890ff', '#0050b3'] },
      textStyle: { color: '#595959', fontSize: 11 }
    },
    series: [{
      type: 'heatmap',
      data,
      label: {
        show: true,
        color: '#fff',
        fontSize: 16,
        fontWeight: 600,
        formatter: p => p.data[2] > 0 ? p.data[2] : ''
      },
      emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.5)' } },
      itemStyle: { borderColor: '#fff', borderWidth: 3, borderRadius: 4 }
    }]
  }
}

const { chartRef, loading } = useChart({
  getOption,
  props: computed(() => ({ matrix: props.confusionMatrix }))
})
</script>

<style scoped>
.training-confusion-heatmap {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: box-shadow 0.3s ease;
}

.training-confusion-heatmap:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.chart-header {
  margin-bottom: 12px;
  text-align: center;
  position: relative;
}

.sample-tag {
  position: absolute;
  top: 0;
  right: 0;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 4px;
}

.chart-subtitle {
  font-size: 13px;
  color: #8c8c8c;
  margin: 0;
}

.chart-container {
  height: 340px;
}

.chart-element {
  width: 100%;
  height: 100%;
}
</style>
