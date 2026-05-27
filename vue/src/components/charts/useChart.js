import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'
import { useChartTheme } from './chartTheme'

/**
 * ECharts图表生命周期管理composable
 * 提供统一的图表初始化、更新、销毁逻辑
 * @param {Object} options - 配置选项
 * @param {Function} options.getOption - 获取图表配置的函数
 * @param {Object} options.props - 组件props
 * @param {Boolean} options.autoResize - 是否自动调整大小，默认true
 * @returns {Object} 图表相关的响应式数据和方法
 */
export function useChart(options = {}) {
  const { getOption, props, autoResize = true } = options

  const chartRef = ref(null)
  const chartInstance = ref(null)
  const loading = ref(false)
  const hasError = ref(false)
  const errorMessage = ref('')
  let resizeObserver = null
  let resizeTimer = null
  let updateTimer = null
  let isUnmounted = false
  
  // 获取主题配置
  const theme = useChartTheme()
  
  /**
   * 初始化图表
   */
  function initChart() {
    if (!chartRef.value) return

    try {
      if (chartInstance.value) {
        chartInstance.value.dispose()
      }

      chartInstance.value = echarts.init(chartRef.value, theme.getTheme())
      scheduleUpdate()

      if (autoResize) {
        window.addEventListener('resize', handleResize)

        let prevW = 0
        let prevH = 0
        resizeObserver = new ResizeObserver((entries) => {
          const entry = entries[0]
          if (!entry || !chartInstance.value) return
          const { width, height } = entry.contentRect
          clearTimeout(resizeTimer)
          if (width > 0 && height > 0) {
            const oldW = prevW
            const oldH = prevH
            prevW = width
            prevH = height
            resizeTimer = setTimeout(() => {
              if (isUnmounted || !chartInstance.value) return
              if ((oldW < 10 || oldH < 10) && getOption) {
                const option = getOption()
                if (option) {
                  chartInstance.value.setOption(option, { notMerge: true })
                }
              }
              chartInstance.value.resize()
            }, 80)
          }
        })
        resizeObserver.observe(chartRef.value)
      }

      hasError.value = false
      errorMessage.value = ''
    } catch (error) {
      hasError.value = true
      errorMessage.value = `图表初始化失败: ${error.message}`
      console.error('图表初始化失败:', error)
    }
  }
  
  /**
   * 防抖调度图表更新
   * 合并同一事件循环内的多次调用（watch + ResizeObserver 同时触发）
   */
  function scheduleUpdate() {
    clearTimeout(updateTimer)
    updateTimer = setTimeout(() => {
      if (isUnmounted || !chartInstance.value || !getOption) return
      nextTick(() => {
        if (isUnmounted || !chartInstance.value) return
        try {
          loading.value = true
          const option = getOption()
          if (option) {
            chartInstance.value.setOption(option, { notMerge: true })
            chartInstance.value.resize()
          }
        } catch (error) {
          hasError.value = true
          errorMessage.value = `图表更新失败: ${error.message}`
          console.error('图表更新失败:', error)
        } finally {
          loading.value = false
        }
      })
    }, 16)
  }

  function handleResize() {
    clearTimeout(resizeTimer)
    resizeTimer = setTimeout(() => {
      if (!isUnmounted && chartInstance.value) {
        chartInstance.value.resize()
      }
    }, 50)
  }
  
  /**
   * 销毁图表
   */
  function disposeChart() {
    isUnmounted = true
    clearTimeout(resizeTimer)
    clearTimeout(updateTimer)

    if (chartInstance.value) {
      chartInstance.value.dispose()
      chartInstance.value = null
    }

    if (autoResize) {
      window.removeEventListener('resize', handleResize)

      if (resizeObserver) {
        resizeObserver.disconnect()
        resizeObserver = null
      }
    }
  }
  
  /**
   * 重新加载图表
   */
  function reload() {
    disposeChart()
    isUnmounted = false
    nextTick(() => {
      initChart()
    })
  }
  
  /**
   * 导出图表为图片
   * @param {String} type - 图片类型，可选 'png' | 'jpeg' | 'svg'
   * @param {String} filename - 文件名
   */
  function exportImage(type = 'png', filename = 'chart') {
    if (!chartInstance.value) return
    
    try {
      const url = chartInstance.value.getDataURL({
        type,
        pixelRatio: 2,
        backgroundColor: theme.getTheme().backgroundColor || '#fff'
      })
      
      const link = document.createElement('a')
      link.download = `${filename}.${type}`
      link.href = url
      link.click()
    } catch (error) {
      console.error('导出图表失败:', error)
    }
  }
  
  /**
   * 获取图表实例
   * @returns {Object} ECharts实例
   */
  function getChartInstance() {
    return chartInstance.value
  }
  
  if (props) {
    watch(
      () => props,
      () => {
        scheduleUpdate()
      },
      { deep: true }
    )
  }

  onMounted(() => {
    nextTick(() => {
      initChart()
    })
  })

  onUnmounted(() => {
    disposeChart()
  })
  
  return {
    chartRef,
    chartInstance,
    loading,
    hasError,
    errorMessage,
    initChart,
    updateChart: scheduleUpdate,
    disposeChart,
    reload,
    exportImage,
    getChartInstance
  }
}