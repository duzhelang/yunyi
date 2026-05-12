<template>
  <el-card shadow="never">
    <template #header>
      <div class="card-header">
        <el-icon class="header-icon"><DataAnalysis /></el-icon>
        <span>健康趋势追踪</span>
      </div>
    </template>
    <el-radio-group v-model="localChartMode" size="small" class="chart-tabs">
      <el-radio-button value="glucose">血糖曲线</el-radio-button>
      <el-radio-button value="bmi">BMI 趋势</el-radio-button>
    </el-radio-group>
    <!-- ECharts 渲染容器 -->
    <div ref="chartRef" class="chart-box"></div>
    <!-- 无数据时的空状态 -->
    <div v-if="historyList.length === 0" class="empty-chart">
      <el-icon class="empty-icon"><TrendCharts /></el-icon>
      <p>暂无数据，请先保存档案</p>
    </div>
    <!-- 有数据时显示图例 -->
    <div v-if="historyList.length > 0" class="chart-legend">
      <span class="legend-item">
        <span class="legend-dot" :style="{ background: chartMode === 'glucose' ? '#E6A23C' : '#409EFF' }"></span>
        {{ chartMode === 'glucose' ? '空腹血糖 (mg/dL)' : 'BMI 指数' }}
      </span>
      <span class="legend-item" v-if="chartMode === 'glucose'">
        <span class="legend-line"></span>
        正常上限: 110
      </span>
    </div>
  </el-card>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { DataAnalysis, TrendCharts } from '@element-plus/icons-vue'
import { useHealthChart } from '@/composables/useHealthChart'

/**
 * 健康趋势追踪图表卡片
 * - 支持血糖曲线 / BMI 趋势两种模式切换
 * - 集成 ECharts 折线图渲染
 * - 无数据时显示空状态指引
 */
const props = defineProps({
  /** 历史记录列表 */
  historyList: {
    type: Array,
    default: () => []
  },
  /** 图表模式：'glucose' 血糖曲线 / 'bmi' BMI 趋势，支持 v-model */
  chartMode: {
    type: String,
    default: 'glucose'
  },
  /** 选中的历史记录 ID，用于高亮对应数据点 */
  selectedHistoryId: {
    type: Number,
    default: null
  }
})

const emit = defineEmits(['update:chartMode'])

// 图表模式本地响应式，通过 computed 实现 v-model 双向绑定
const localChartMode = computed({
  get: () => props.chartMode,
  set: (val) => emit('update:chartMode', val)
})

// ECharts 渲染容器 DOM 引用
const chartRef = ref(null)

// 使用健康图表组合式函数处理 ECharts 生命周期
const { initChart, renderChart, destroyChart } = useHealthChart(
  chartRef,
  computed(() => props.historyList),
  computed(() => props.chartMode),
  computed(() => props.selectedHistoryId)
)

onMounted(() => {
  initChart()
  if (props.historyList.length > 0) {
    renderChart()
  }
})

// 监听数据变化，重新渲染图表
watch(
  () => [props.historyList, props.chartMode, props.selectedHistoryId],
  () => {
    nextTick(() => renderChart())
  },
  { deep: true }
)

onUnmounted(() => {
  destroyChart()
})
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 15px;
  color: #1D2129;
}

.header-icon {
  font-size: 18px;
  color: #409EFF;
}

.chart-tabs {
  margin-bottom: 12px;
}

.chart-box {
  width: 100%;
  height: 220px;
  background: #fafafa;
  border-radius: 8px;
}

.empty-chart {
  text-align: center;
  padding: 30px;
  color: #909399;
}

.chart-legend {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 10px;
  font-size: 12px;
  color: #606266;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.legend-line {
  width: 20px;
  height: 2px;
  background: #F56C6C;
  border-style: dashed;
}
</style>
