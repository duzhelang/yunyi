<template>
  <div class="training-trend-chart">
    <div class="chart-header">
      <h3 class="chart-title">多任务性能对比</h3>
      <p class="chart-subtitle">选择已完成任务对比各项性能指标</p>
    </div>

    <div class="chart-container" v-loading="loading">
      <div ref="chartRef" class="chart-element"></div>
    </div>

    <div class="chart-toolbar">
      <el-checkbox-group v-model="selectedTaskIds" @change="onSelectionChange">
        <el-checkbox
          v-for="task in completedTasks"
          :key="task.id"
          :value="task.id"
        >
          {{ task.taskName }}
        </el-checkbox>
      </el-checkbox-group>
      <el-select v-model="metricMode" size="small" style="width: 140px;" @change="onSelectionChange">
        <el-option label="准确率" value="accuracy" />
        <el-option label="F1 分数" value="f1Score" />
        <el-option label="精确率" value="precisionRate" />
        <el-option label="召回率" value="recallRate" />
        <el-option label="AUC" value="auc" />
        <el-option label="损失" value="loss" />
      </el-select>
    </div>

    <div class="baseline-info" v-if="baselineTask">
      <el-icon><Flag /></el-icon>
      <span>基线模型: {{ baselineTask.taskName }} ({{ metricLabel }}: {{ formatMetric(baselineTask[metricMode]) }})</span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useChart } from './useChart'
import { useChartTheme } from './chartTheme'
import { Flag } from '@element-plus/icons-vue'

// 组件属性定义
const props = defineProps({
  // 任务列表数据
  tasks: { type: Array, default: () => [] },
  // 基线任务ID（可选）
  baselineTaskId: { type: [Number, String], default: null }
})

// 定义事件
const emit = defineEmits(['task-click'])

// 获取主题配置
const theme = useChartTheme()

// 选中的任务ID列表
const selectedTaskIds = ref([])

// 当前指标模式
const metricMode = ref('accuracy')

// 是否已初始化默认选中
const defaultSelected = ref(false)

// 指标中文标签映射
const metricLabels = {
  accuracy: '准确率', f1Score: 'F1 分数', precisionRate: '精确率',
  recallRate: '召回率', auc: 'AUC', loss: '损失'
}

// 当前指标标签（计算属性）
const metricLabel = computed(() => metricLabels[metricMode.value])

// 已完成且有准确率数据的任务列表
const completedTasks = computed(() =>
  props.tasks.filter(t => t.status === 'completed' && t.accuracy != null)
)

// 基线任务（计算属性）：优先使用指定的baselineTaskId，否则取准确率最高的任务
const baselineTask = computed(() =>
  props.baselineTaskId
    ? props.tasks.find(t => t.id === props.baselineTaskId)
    : completedTasks.value.reduce((best, t) => (!best || t.accuracy > best.accuracy) ? t : best, null)
)

// 当前选中的任务列表
const selectedTasks = computed(() =>
  completedTasks.value.filter(t => selectedTaskIds.value.includes(t.id))
)

// 监听已完成任务列表变化，自动选中前3个任务
watch(completedTasks, (newTasks) => {
  if (!defaultSelected.value && newTasks.length > 0) {
    selectedTaskIds.value = newTasks.slice(0, 3).map(t => t.id)
    defaultSelected.value = true
  }
}, { immediate: true })

/**
 * 格式化指标值显示
 * @param {Number} val - 指标值
 * @returns {String} 格式化后的字符串
 */
function formatMetric(val) {
  if (val == null) return '-'
  return metricMode.value === 'loss' ? val.toFixed(4) : (val * 100).toFixed(1) + '%'
}

/**
 * 选择变更回调
 */
function onSelectionChange() {}

/**
 * 获取图表配置
 * @returns {Object} ECharts配置对象
 */
function getOption() {
  const base = theme.getBaseOption()
  const tasks = selectedTasks.value
  
  // 如果没有选中任务，显示提示信息
  if (tasks.length === 0) {
    return {
      ...base,
      series: [],
      graphic: {
        type: 'text',
        left: 'center',
        top: 'middle',
        style: { text: '请勾选任务进行对比', fontSize: 14, fill: '#8c8c8c' }
      }
    }
  }

  const isLoss = metricMode.value === 'loss'
  const data = tasks.map(t => isLoss ? t[metricMode.value] : +(t[metricMode.value] * 100).toFixed(1))
  const names = tasks.map(t => t.taskName)
  const colors = theme.getColorPalette(tasks.length)

  // 计算基线值
  const baselineVal = baselineTask.value
    ? (isLoss ? baselineTask.value[metricMode.value] : +(baselineTask.value[metricMode.value] * 100).toFixed(1))
    : null

  const labelRotate = names.length > 4 ? (names.length > 6 ? 45 : 30) : 0

  return {
    ...base,
    tooltip: {
      ...base.tooltip,
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: {
      ...base.legend,
      bottom: 0,
      type: 'scroll',
      pageTextStyle: { color: '#595959' },
      pageIconColor: '#1890ff',
      pageIconInactiveColor: '#ccc'
    },
    grid: { left: '2%', right: '6%', bottom: '20%', top: '10%', containLabel: true },
    xAxis: {
      type: 'category',
      data: names,
      axisLabel: {
        ...base.axis?.axisLabel,
        rotate: labelRotate,
        interval: 0,
        overflow: 'truncate',
        width: 80
      },
      axisLine: { ...base.axis?.axisLine }
    },
    yAxis: {
      type: 'value',
      name: isLoss ? '损失值' : '百分比(%)',
      nameTextStyle: { fontSize: 11, padding: [0, 0, 0, 0] },
      axisLabel: {
        ...base.axis?.axisLabel,
        formatter: isLoss ? '{value}' : '{value}%',
        overflow: 'truncate',
        width: 50
      },
      splitLine: { ...base.axis?.splitLine }
    },
    series: [
      {
        name: metricLabel.value,
        type: 'bar',
        data: data.map((val, i) => ({
          value: val,
          itemStyle: { color: colors[i], borderRadius: [4, 4, 0, 0] }
        })),
        barMaxWidth: 40,
        label: {
          show: data.length <= 8,
          position: 'top',
          formatter: isLoss ? '{c}' : '{c}%',
          fontSize: 11,
          color: '#595959',
          overflow: 'truncate',
          width: 60
        },
        markLine: baselineVal != null ? {
          silent: true,
          lineStyle: { type: 'dashed', color: '#f5222d', width: 2 },
          data: [{ yAxis: baselineVal, name: '基线' }],
          label: {
            formatter: '基线: {c}',
            position: 'end',
            color: '#f5222d',
            fontSize: 11
          }
        } : undefined
      }
    ]
  }
}

// 使用图表composable
const { chartRef, loading } = useChart({
  getOption,
  props: computed(() => ({
    tasks: selectedTasks.value,
    metricMode: metricMode.value,
    baselineTaskId: props.baselineTaskId
  }))
})

// 暴露内部状态给父组件
defineExpose({ selectedTaskIds, metricMode })
</script>

<style scoped>
.training-trend-chart {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: box-shadow 0.3s ease;
}

.training-trend-chart:hover {
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  flex-wrap: wrap;
  gap: 8px;
}

.chart-toolbar .el-checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.chart-container {
  height: 300px;
}

.chart-element {
  width: 100%;
  height: 100%;
}

.baseline-info {
  margin-top: 12px;
  padding: 8px 12px;
  background: #fff7e6;
  border-radius: 6px;
  font-size: 13px;
  color: #d46b08;
  display: flex;
  align-items: center;
  gap: 6px;
}
</style>
