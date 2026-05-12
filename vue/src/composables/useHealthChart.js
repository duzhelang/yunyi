import { ref } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'

/**
 * 健康趋势图表的 ECharts 渲染逻辑
 * @param {import('vue').Ref<HTMLElement|null>} chartRef - 图表容器 DOM 引用
 * @param {import('vue').Ref<Array>} historyList - 历史数据列表
 * @param {import('vue').Ref<string>} chartMode - 图表模式 ('glucose' 或 'bmi')
 * @param {import('vue').Ref<number|null>} selectedHistoryId - 选中的历史记录 ID
 */
export function useHealthChart(chartRef, historyList, chartMode, selectedHistoryId) {
  // 内部维护的 ECharts 实例
  const chartInstance = ref(null)

  // 状态映射
  const STATUS_MAP = {
    DONE: { text: '已完成', type: 'success' },
    PENDING: { text: '诊断中', type: 'warning' },
    SAVED: { text: '已保存', type: 'info' }
  }

  /**
   * 格式化时间戳为 MM/DD HH:mm 格式
   * @param {number|string} ts - 时间戳
   * @returns {string} 格式化后的时间字符串
   */
  function formatTime(ts) {
    if (!ts) return ''
    const d = new Date(ts)
    return `${d.getMonth() + 1}/${d.getDate()} ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
  }

  /**
   * 初始化 ECharts 实例
   */
  function initChart() {
    const el = chartRef.value
    if (!el) return
    // 如果已有实例，先销毁
    if (chartInstance.value) {
      chartInstance.value.dispose()
    }
    chartInstance.value = echarts.init(el)
  }

  /**
   * 根据 historyList 和 chartMode 渲染折线图
   * - 按 createTime 排序历史数据
   * - X 轴显示时间 (formatTime)，Y 轴显示血糖或 BMI
   * - 血糖模式: markLine 在 110 处画虚线
   * - tooltip 显示状态
   * - selectedHistoryId 高亮选中点
   * - areaStyle 渐变填充
   */
  function renderChart() {
    const el = chartRef.value
    if (!el) return
    if (!chartInstance.value) {
      chartInstance.value = echarts.init(el)
    }

    // 按创建时间排序
    const sorted = [...historyList.value].sort((a, b) => new Date(a.createTime) - new Date(b.createTime))
    const times = sorted.map(item => formatTime(item.createTime))
    const glucoseData = sorted.map(item => item.glucose)
    const bmiData = sorted.map(item => item.bmi)

    // 查找选中的历史记录索引
    const selectedIndex = selectedHistoryId.value
      ? sorted.findIndex(item => item.id === selectedHistoryId.value)
      : -1

    const option = {
      // 提示框
      tooltip: {
        trigger: 'axis',
        formatter: (params) => {
          const data = params[0]
          const index = data.dataIndex
          const item = sorted[index]
          return `<strong>${data.axisValue}</strong><br/>${chartMode.value === 'glucose' ? '血糖' : 'BMI'}: ${data.value}<br/>状态: ${STATUS_MAP[item.status]?.text || ''}`
        }
      },
      // 网格布局
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      // X 轴 - 时间
      xAxis: {
        type: 'category',
        data: times,
        axisLabel: { rotate: 30, fontSize: 10 }
      },
      // Y 轴 - 数值
      yAxis: {
        type: 'value',
        name: chartMode.value === 'glucose' ? '血糖 (mg/dL)' : 'BMI',
        nameTextStyle: { fontSize: 11 }
      },
      // 折线图系列
      series: [{
        data: chartMode.value === 'glucose' ? glucoseData : bmiData,
        type: 'line',
        smooth: true,
        showSymbol: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: { width: 2 },
        // 渐变填充区域
        areaStyle: {
          opacity: 0.15,
          color: chartMode.value === 'glucose'
            ? new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#E6A23C' }, { offset: 1, color: '#fff' }])
            : new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#409EFF' }, { offset: 1, color: '#fff' }])
        },
        // 数据点样式（高亮选中点）
        itemStyle: {
          color: (param) => {
            return param.dataIndex === selectedIndex ? '#F56C6C' : (chartMode.value === 'glucose' ? '#E6A23C' : '#409EFF')
          },
          borderWidth: (param) => {
            return param.dataIndex === selectedIndex ? 3 : 0
          },
          borderColor: '#fff'
        },
        // 血糖模式下的上限标记线
        markLine: {
          silent: true,
          data: chartMode.value === 'glucose'
            ? [{ yAxis: 110, label: { formatter: '上限', fontSize: 10 }, lineStyle: { type: 'dashed', color: '#F56C6C' } }]
            : []
        }
      }]
    }

    chartInstance.value.setOption(option, true)
  }

  /**
   * 清理 ECharts 实例
   */
  function destroyChart() {
    if (chartInstance.value) {
      chartInstance.value.dispose()
      chartInstance.value = null
    }
  }

  return {
    chartInstance,
    formatTime,
    initChart,
    renderChart,
    destroyChart
  }
}
