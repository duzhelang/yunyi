<template>
  <div class="training-hyperparam-chart">
    <div class="chart-header">
      <h3 class="chart-title">超参-性能关联分析</h3>
      <p class="chart-subtitle">平行坐标系展示超参与最终性能的关系</p>
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

/**
 * 超参-性能关联分析图表组件
 * 使用平行坐标系展示学习率、批次大小、训练轮次等超参与准确率的关联
 * 线条颜色渐变：蓝→绿（高准确率）/ 蓝→红（低准确率）
 * 用于指导超参调优
 */

const props = defineProps({
  // 训练任务数据列表
  tasks: { type: Array, default: () => [] }
})

// 获取主题配置
const theme = useChartTheme()

// 是否使用示例数据
const isSampleData = ref(false)

// 生成示例超参数据
const sampleTasks = [
  { taskName: '实验A-lr0.1', learningRate: 0.1, batchSize: 32, epochs: 20, accuracy: 0.85, f1Score: 0.84, loss: 0.35, optimizer: 'SGD' },
  { taskName: '实验B-lr0.01', learningRate: 0.01, batchSize: 64, epochs: 30, accuracy: 0.92, f1Score: 0.91, loss: 0.18, optimizer: 'Adam' },
  { taskName: '实验C-lr0.001', learningRate: 0.001, batchSize: 128, epochs: 50, accuracy: 0.94, f1Score: 0.93, loss: 0.12, optimizer: 'Adam' },
  { taskName: '实验D-lr0.0001', learningRate: 0.0001, batchSize: 16, epochs: 40, accuracy: 0.88, f1Score: 0.87, loss: 0.28, optimizer: 'AdamW' },
  { taskName: '实验E-lr0.01_bs32', learningRate: 0.01, batchSize: 32, epochs: 50, accuracy: 0.93, f1Score: 0.92, loss: 0.15, optimizer: 'Adam' },
  { taskName: '实验F-lr0.1_bs128', learningRate: 0.1, batchSize: 128, epochs: 30, accuracy: 0.78, f1Score: 0.76, loss: 0.52, optimizer: 'SGD' },
  { taskName: '实验G-lr0.001_bs64', learningRate: 0.001, batchSize: 64, epochs: 60, accuracy: 0.95, f1Score: 0.94, loss: 0.10, optimizer: 'AdamW' },
  { taskName: '实验H-lr0.01_bs16', learningRate: 0.01, batchSize: 16, epochs: 20, accuracy: 0.86, f1Score: 0.85, loss: 0.32, optimizer: 'SGD' }
].map((t, i) => ({ ...t, id: i, status: 'completed' }))

function filterRealTasks() {
  return props.tasks.filter(t => t.status === 'completed' && t.accuracy != null && t.learningRate != null)
}

watch(() => props.tasks, () => {
  isSampleData.value = filterRealTasks().length === 0
}, { immediate: true, deep: true })

const displayTasks = computed(() => {
  const real = filterRealTasks()
  return real.length > 0 ? real : sampleTasks
})

// 平行坐标轴定义
const parallelAxes = [
  { dim: 0, name: '学习率' },
  { dim: 1, name: '批次大小' },
  { dim: 2, name: '训练轮次' },
  { dim: 3, name: '准确率(%)' },
  { dim: 4, name: 'F1(%)' },
  { dim: 5, name: '损失' }
]

/**
 * 获取图表配置
 * @returns {Object} ECharts配置对象
 */
function getOption() {
  const base = theme.getBaseOption()
  const tasks = displayTasks.value

  // 无数据时显示提示
  if (tasks.length === 0) {
    return {
      ...base,
      series: [],
      parallel: { show: false },
      parallelAxis: [],
      graphic: {
        type: 'text',
        left: 'center',
        top: 'middle',
        style: {
          text: '暂无已完成的训练任务数据',
          fontSize: 14,
          fill: '#8c8c8c'
        }
      }
    }
  }

  // 准备图表数据
  const data = tasks.map(t => [
    t.learningRate ?? 0,
    t.batchSize ?? 0,
    t.epochs ?? 0,
    t.accuracy != null ? +(t.accuracy * 100).toFixed(1) : 0,
    t.f1Score != null ? +(t.f1Score * 100).toFixed(1) : 0,
    t.loss ?? 0
  ])

  // 准确率索引
  const accIdx = 3
  const accValues = data.map(d => d[accIdx])
  const minAcc = Math.min(...accValues)
  const maxAcc = Math.max(...accValues)

  return {
    ...base,
    tooltip: {
      ...base.tooltip,
      trigger: 'item',
      formatter: (params) => {
        const t = tasks[params.dataIndex]
        return `<div style="font-weight:600;margin-bottom:6px">${t.taskName}</div>
          <div>学习率: ${t.learningRate}</div>
          <div>批次大小: ${t.batchSize}</div>
          <div>训练轮次: ${t.epochs}</div>
          <div>准确率: ${(t.accuracy * 100).toFixed(1)}%</div>
          <div>F1: ${t.f1Score != null ? (t.f1Score * 100).toFixed(1) + '%' : '-'}</div>
          <div>损失: ${t.loss ?? '-'}</div>
          <div>优化器: ${t.optimizer ?? '-'}</div>`
      }
    },
    parallel: {
      left: '6%',
      right: '8%',
      bottom: '15%',
      top: '12%',
      parallelAxisDefault: {
        type: 'value',
        nameLocation: 'start',
        nameGap: 25,
        nameTextStyle: { fontSize: 12, color: '#595959', padding: [0, 0, 0, 0] },
        axisLine: { ...base.axis?.axisLine },
        axisTick: { ...base.axis?.axisTick },
        axisLabel: {
          ...base.axis?.axisLabel,
          overflow: 'truncate',
          width: 60,
          fontSize: 10
        },
        splitLine: { ...base.axis?.splitLine }
      }
    },
    parallelAxis: parallelAxes.map(axis => {
      if (axis.dim === 0) {
        return {
          ...axis,
          type: 'log',
          axisLabel: {
            fontSize: 10,
            formatter: (v) => v < 0.001 ? v.toExponential(0) : v
          }
        }
      }
      if (axis.dim === accIdx) {
        return {
          ...axis,
          min: Math.max(0, minAcc - 5),
          max: Math.min(100, maxAcc + 5),
          axisLabel: {
            fontSize: 10,
            formatter: '{value}%'
          }
        }
      }
      return axis
    }),
    series: [{
      type: 'parallel',
      lineStyle: { width: 2, opacity: 0.7 },
      data: data.map((d) => ({
        value: d,
        lineStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 1, y2: 0,
            colorStops: [
              { offset: 0, color: '#1890ff' },
              { offset: 1, color: d[accIdx] >= 80 ? '#52c41a' : d[accIdx] >= 60 ? '#faad14' : '#f5222d' }
            ]
          },
          width: Math.max(1, d[accIdx] / 20)
        }
      }))
    }]
  }
}

// 使用图表composable
const { chartRef, loading } = useChart({
  getOption,
  props: computed(() => ({ tasks: displayTasks.value }))
})
</script>

<style scoped>
.training-hyperparam-chart {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: box-shadow 0.3s ease;
}

.training-hyperparam-chart:hover {
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
  height: 360px;
}

.chart-element {
  width: 100%;
  height: 100%;
}
</style>
