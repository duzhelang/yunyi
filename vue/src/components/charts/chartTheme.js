import { ref } from 'vue'

/**
 * 图表主题配置
 * 统一管理图表的颜色、字体、样式等配置
 */

// 默认主题配置
const defaultTheme = {
  // 背景色
  backgroundColor: 'transparent',
  
  // 主色调（医疗健康主题）
  color: [
    '#1890ff', // 主蓝
    '#52c41a', // 健康绿
    '#faad14', // 警告黄
    '#f5222d', // 危险红
    '#722ed1', // 紫色
    '#13c2c2', // 青色
    '#eb2f96', // 粉色
    '#fa8c16'  // 橙色
  ],
  
  // 风险等级颜色
  riskColors: {
    low: '#52c41a',      // 低风险 - 绿色
    medium: '#faad14',   // 中风险 - 黄色
    high: '#f5222d'      // 高风险 - 红色
  },
  
  // 标题样式
  title: {
    textStyle: {
      color: '#262626',
      fontSize: 16,
      fontWeight: 600,
      fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "PingFang SC", "Microsoft YaHei", sans-serif'
    },
    subtextStyle: {
      color: '#8c8c8c',
      fontSize: 12,
      fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "PingFang SC", "Microsoft YaHei", sans-serif'
    }
  },
  
  // 图例样式
  legend: {
    textStyle: {
      color: '#595959',
      fontSize: 12
    }
  },
  
  // 提示框样式
  tooltip: {
    backgroundColor: 'rgba(255, 255, 255, 0.95)',
    borderColor: '#e8e8e8',
    borderWidth: 1,
    textStyle: {
      color: '#262626',
      fontSize: 12
    },
    extraCssText: 'box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);'
  },
  
  // 坐标轴样式
  axis: {
    axisLine: {
      lineStyle: {
        color: '#d9d9d9'
      }
    },
    axisTick: {
      lineStyle: {
        color: '#d9d9d9'
      }
    },
    axisLabel: {
      color: '#8c8c8c',
      fontSize: 11
    },
    splitLine: {
      lineStyle: {
        color: '#f0f0f0',
        type: 'dashed'
      }
    }
  },
  
  // 仪表盘样式
  gauge: {
    axisLine: {
      lineStyle: {
        width: 20,
        color: [
          [0.3, '#52c41a'],
          [0.7, '#faad14'],
          [1, '#f5222d']
        ]
      }
    },
    axisTick: {
      length: 8,
      lineStyle: {
        color: 'auto'
      }
    },
    splitLine: {
      length: 20,
      lineStyle: {
        color: 'auto'
      }
    },
    pointer: {
      width: 5,
      length: '70%'
    }
  },
  
  // 雷达图样式
  radar: {
    symbol: 'circle',
    symbolSize: 6,
    lineStyle: {
      width: 2
    },
    areaStyle: {
      opacity: 0.15
    },
    splitArea: {
      areaStyle: {
        color: ['rgba(24, 144, 255, 0.02)', 'rgba(24, 144, 255, 0.05)']
      }
    }
  }
}

// 深色主题配置（备用）
const darkTheme = {
  backgroundColor: '#1f1f1f',
  color: [
    '#177ddc', // 主蓝
    '#49aa19', // 健康绿
    '#d89614', // 警告黄
    '#d32029', // 危险红
    '#9254de', // 紫色
    '#13a8a8', // 青色
    '#d84a1b', // 粉色
    '#d87a16'  // 橙色
  ],
  riskColors: {
    low: '#49aa19',
    medium: '#d89614',
    high: '#d32029'
  },
  title: {
    textStyle: {
      color: '#ffffffd9',
      fontSize: 16,
      fontWeight: 600
    },
    subtextStyle: {
      color: '#ffffff73',
      fontSize: 12
    }
  },
  legend: {
    textStyle: {
      color: '#ffffffd9',
      fontSize: 12
    }
  },
  tooltip: {
    backgroundColor: 'rgba(31, 31, 31, 0.95)',
    borderColor: '#434343',
    textStyle: {
      color: '#ffffffd9'
    }
  },
  axis: {
    axisLine: {
      lineStyle: {
        color: '#434343'
      }
    },
    axisTick: {
      lineStyle: {
        color: '#434343'
      }
    },
    axisLabel: {
      color: '#ffffff73'
    },
    splitLine: {
      lineStyle: {
        color: '#303030'
      }
    }
  }
}

/**
 * 图表主题管理composable
 * @returns {Object} 主题相关方法
 */
export function useChartTheme() {
  // 当前主题模式
  const themeMode = ref('light')
  
  /**
   * 获取当前主题配置
   * @returns {Object} 主题配置对象
   */
  function getTheme() {
    return themeMode.value === 'dark' ? darkTheme : defaultTheme
  }
  
  /**
   * 设置主题模式
   * @param {String} mode - 主题模式 'light' | 'dark'
   */
  function setThemeMode(mode) {
    if (mode === 'light' || mode === 'dark') {
      themeMode.value = mode
    }
  }
  
  /**
   * 获取风险等级颜色
   * @param {String} level - 风险等级 'low' | 'medium' | 'high'
   * @returns {String} 颜色值
   */
  function getRiskColor(level) {
    const theme = getTheme()
    return theme.riskColors[level] || theme.color[0]
  }
  
  /**
   * 根据数值获取风险颜色
   * @param {Number} value - 数值 (0-100)
   * @returns {String} 颜色值
   */
  function getRiskColorByValue(value) {
    if (value < 30) return getRiskColor('low')
    if (value < 70) return getRiskColor('medium')
    return getRiskColor('high')
  }
  
  /**
   * 获取颜色调色板
   * @param {Number} count - 需要的颜色数量
   * @returns {Array} 颜色数组
   */
  function getColorPalette(count = 8) {
    const theme = getTheme()
    const colors = theme.color
    const result = []
    
    for (let i = 0; i < count; i++) {
      result.push(colors[i % colors.length])
    }
    
    return result
  }
  
  /**
   * 创建渐变色
   * @param {Array} colors - 颜色数组，如 ['#1890ff', '#52c41a']
   * @param {String} direction - 方向 'vertical' | 'horizontal'
   * @returns {Object} ECharts渐变对象
   */
  function createGradient(colors, direction = 'vertical') {
    if (!colors || colors.length < 2) {
      return colors ? colors[0] : '#1890ff'
    }
    
    return {
      type: 'linear',
      x: direction === 'horizontal' ? 1 : 0,
      y: 0,
      x2: 0,
      y2: direction === 'vertical' ? 1 : 0,
      colorStops: colors.map((color, index) => ({
        offset: index / (colors.length - 1),
        color
      }))
    }
  }
  
  /**
   * 获取通用图表配置
   * @returns {Object} 通用配置对象
   */
  function getBaseOption() {
    const theme = getTheme()
    
    return {
      backgroundColor: theme.backgroundColor,
      color: theme.color,
      title: {
        textStyle: { ...theme.title.textStyle },
        subtextStyle: { ...theme.title.subtextStyle }
      },
      legend: {
        textStyle: { ...theme.legend.textStyle }
      },
      tooltip: {
        ...theme.tooltip,
        textStyle: { ...theme.tooltip.textStyle }
      },
      axis: {
        axisLine: { ...theme.axis.axisLine },
        axisTick: { ...theme.axis.axisTick },
        axisLabel: { ...theme.axis.axisLabel },
        splitLine: { ...theme.axis.splitLine }
      }
    }
  }
  
  return {
    themeMode,
    getTheme,
    setThemeMode,
    getRiskColor,
    getRiskColorByValue,
    getColorPalette,
    createGradient,
    getBaseOption
  }
}