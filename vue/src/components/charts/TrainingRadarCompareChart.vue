<template>
  <div class="training-radar-compare">
    <div class="chart-header">
      <h3 class="chart-title">性能雷达对比</h3>
      <p class="chart-subtitle">多维度对比训练任务的综合性能</p>
    </div>
    <div class="chart-container" v-loading="loading">
      <div ref="chartRef" class="chart-element"></div>
    </div>
    <div class="chart-toolbar">
      <el-checkbox-group v-model="selectedIds" @change="refresh">
        <el-checkbox v-for="t in completedTasks" :key="t.id" :value="t.id">{{ t.taskName }}</el-checkbox>
      </el-checkbox-group>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useChart } from './useChart'
import { useChartTheme } from './chartTheme'

// 组件属性定义
const props = defineProps({
  // 任务列表数据
  tasks: { type: Array, default: () => [] },
  // 基线任务ID（可选）
  baselineTaskId: { type: [Number, String], default: null }
})

// 获取主题配置
const theme = useChartTheme()

// 选中的任务ID列表
const selectedIds = ref([])

// 是否已初始化默认选中
const defaultSelected = ref(false)

// 已完成且有准确率数据的任务列表
const completedTasks = computed(() => props.tasks.filter(t => t.status === 'completed' && t.accuracy != null))

// 监听已完成任务列表变化，自动选中前3个任务
watch(completedTasks, (newTasks) => {
  if (!defaultSelected.value && newTasks.length > 0) {
    selectedIds.value = newTasks.slice(0, 3).map(t => t.id)
    defaultSelected.value = true
  }
}, { immediate: true })

// 基线任务（计算属性）：优先使用指定的baselineTaskId，否则取准确率最高的任务
const baselineTask = computed(() =>
  props.baselineTaskId
    ? props.tasks.find(t => t.id === props.baselineTaskId)
    : completedTasks.value.reduce((best, t) => (!best || t.accuracy > best.accuracy) ? t : best, null)
)

// 雷达图维度定义
const radarDims = [
  { key: 'accuracy', name: '准确率', max: 1 },
  { key: 'precisionRate', name: '精确率', max: 1 },
  { key: 'recallRate', name: '召回率', max: 1 },
  { key: 'f1Score', name: 'F1 分数', max: 1 },
  { key: 'auc', name: 'AUC', max: 1 }
]

/**
 * 选择变更回调（预留方法）
 */
function refresh() {}

/**
 * 获取图表配置
 * @returns {Object} ECharts配置对象
 */
function getOption() {
  const base = theme.getBaseOption()
  const selected = completedTasks.value.filter(t => selectedIds.value.includes(t.id))

  // 如果没有选中任务，显示提示信息
  if (selected.length === 0) {
    return {
      ...base,
      series: [],
      radar: { indicator: [] },
      graphic: {
        type: 'text',
        left: 'center',
        top: 'middle',
        style: { text: '请勾选任务进行对比', fontSize: 14, fill: '#8c8c8c' }
      }
    }
  }

  const colors = theme.getColorPalette(selected.length + 1)
  
  // 构建雷达图数据系列
  const seriesData = selected.map((t, i) => ({
    value: radarDims.map(d => t[d.key] ?? 0),
    name: t.taskName,
    symbol: 'circle',
    symbolSize: 5,
    lineStyle: { width: 2, color: colors[i] },
    areaStyle: { color: colors[i], opacity: 0.08 },
    itemStyle: { color: colors[i], borderColor: '#fff', borderWidth: 2 }
  }))

  // 如果存在基线任务，添加基线系列（红色虚线）
  if (baselineTask.value) {
    seriesData.push({
      value: radarDims.map(d => baselineTask.value[d.key] ?? 0),
      name: '基线 (' + baselineTask.value.taskName + ')',
      symbol: 'diamond',
      symbolSize: 6,
      lineStyle: { type: 'dashed', width: 2, color: '#f5222d' },
      areaStyle: { color: '#f5222d', opacity: 0.04 },
      itemStyle: { color: '#f5222d', borderColor: '#fff', borderWidth: 2 }
    })
  }

  return {
    ...base,
    legend: {
      ...base.legend,
      bottom: 0,
      type: 'scroll',
      pageTextStyle: { color: '#595959' },
      pageIconColor: '#1890ff',
      pageIconInactiveColor: '#ccc',
      data: seriesData.map(s => s.name)
    },
    tooltip: { ...base.tooltip, trigger: 'item' },
    radar: {
      indicator: radarDims.map(d => ({ name: d.name, max: d.max })),
      shape: 'polygon',
      radius: '55%',
      center: ['50%', '48%'],
      axisName: {
        color: '#595959',
        fontSize: 12,
        overflow: 'truncate',
        width: 50,
        padding: [3, 5]
      },
      splitArea: { areaStyle: { color: ['rgba(24,144,255,0.02)', 'rgba(24,144,255,0.05)'] } },
      axisLine: { lineStyle: { color: '#d9d9d9' } },
      splitLine: { lineStyle: { color: '#f0f0f0', type: 'dashed' } }
    },
    series: [{ type: 'radar', data: seriesData }]
  }
}

// 使用图表composable
const { chartRef, loading } = useChart({
  getOption,
  props: computed(() => ({ selected: selectedIds.value, baseline: baselineTask.value }))
})

// 暴露内部状态给父组件
defineExpose({ selectedIds })
</script>

<style scoped>
.training-radar-compare {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: box-shadow 0.3s ease;
}

.training-radar-compare:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.chart-header {
  margin-bottom: 12px;
  text-align: center;
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

.chart-toolbar {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.chart-toolbar .el-checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.chart-container {
  height: 320px;
}

.chart-element {
  width: 100%;
  height: 100%;
}
</style>