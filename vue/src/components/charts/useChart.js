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
  
  // 图表容器引用
  const chartRef = ref(null)
  // ECharts实例
  const chartInstance = ref(null)
  // 是否正在加载
  const loading = ref(false)
  // 是否有错误
  const hasError = ref(false)
  // 错误信息
  const errorMessage = ref('')
  
  // 获取主题配置
  const theme = useChartTheme()
  
  /**
   * 初始化图表
   */
  function initChart() {
    if (!chartRef.value) return
    
    try {
      // 销毁旧实例
      if (chartInstance.value) {
        chartInstance.value.dispose()
      }
      
      // 创建新实例
      chartInstance.value = echarts.init(chartRef.value, theme.getTheme())
      
      // 设置初始配置
      updateChart()
      
      // 监听窗口大小变化
      if (autoResize) {
        window.addEventListener('resize', handleResize)
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
   * 更新图表配置
   */
  function updateChart() {
    if (!chartInstance.value || !getOption) return
    
    try {
      loading.value = true
      const option = getOption()
      if (option) {
        chartInstance.value.setOption(option, true)
      }
    } catch (error) {
      hasError.value = true
      errorMessage.value = `图表更新失败: ${error.message}`
      console.error('图表更新失败:', error)
    } finally {
      loading.value = false
    }
  }
  
  /**
   * 处理窗口大小变化
   */
  function handleResize() {
    if (chartInstance.value) {
      chartInstance.value.resize()
    }
  }
  
  /**
   * 销毁图表
   */
  function disposeChart() {
    if (chartInstance.value) {
      chartInstance.value.dispose()
      chartInstance.value = null
    }
    
    if (autoResize) {
      window.removeEventListener('resize', handleResize)
    }
  }
  
  /**
   * 重新加载图表
   */
  function reload() {
    disposeChart()
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
  
  // 监听props变化，自动更新图表
  if (props) {
    watch(
      () => props,
      () => {
        nextTick(() => {
          updateChart()
        })
      },
      { deep: true }
    )
  }
  
  // 组件挂载时初始化图表
  onMounted(() => {
    nextTick(() => {
      initChart()
    })
  })
  
  // 组件卸载时销毁图表
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
    updateChart,
    disposeChart,
    reload,
    exportImage,
    getChartInstance
  }
}