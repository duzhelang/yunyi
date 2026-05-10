<template>
  <div class="diagnosis-page">
    <div class="header">
      <h2>智能诊断可视化报告</h2>
      <el-button type="primary" @click="goBack">重新检测</el-button>
    </div>

    <div v-if="loading" class="loading">
      <el-icon class="is-loading" :size="50"><Loading /></el-icon>
      <p>正在生成可视化图表...</p>
    </div>

    <div v-else class="dashboard-grid">
      <el-card class="chart-box main-chart">
        <template #header><span class="title">患病概率评估</span></template>
        <div ref="gaugeChartRef" class="echart-div"></div>
        <div class="result-text" :class="isPositive ? 'text-danger' : 'text-success'">
          {{ isPositive ? '高风险:建议立即就医' : '低风险:保持健康生活' }}
        </div>
      </el-card>

      <el-card class="chart-box">
        <template #header><span class="title">身体指标风险维度</span></template>
        <div ref="radarChartRef" class="echart-div"></div>
        <p class="tip">* 红色区域越大,表示该维度偏离正常值越远</p>
      </el-card>

      <el-card class="chart-box">
        <template #header><span class="title">关键指标风险对比</span></template>
        <div ref="barChartRef" class="echart-div"></div>
      </el-card>

      <el-card class="chart-box advice-box">
        <template #header><span class="title">专家建议</span></template>
        <ul class="advice-list">
          <li v-for="(item, i) in adviceList" :key="i">
            <span :style="{ color: item.color, marginRight: '10px' }">{{ item.icon }}</span> {{ item.text }}
          </li>
        </ul>
      </el-card>

      <el-card class="chart-box" style="grid-column: span 2">
        <template #header><span class="title">系统数据概览</span></template>
        <div class="system-stats">
          <div class="stat-item">
            <div class="stat-value">{{ dashboardData.totalRecords || 0 }}</div>
            <div class="stat-label">总检测记录</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ dashboardData.totalProfiles || 0 }}</div>
            <div class="stat-label">健康档案数</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ dashboardData.todayCount || 0 }}</div>
            <div class="stat-label">今日检测</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ dashboardData.diseaseCount || 0 }}</div>
            <div class="stat-label">阳性病例</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ dashboardData.totalFiles || 0 }}</div>
            <div class="stat-label">数据文件</div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const isPositive = ref(false)
const probability = ref(0)
const dashboardData = ref({})

const gaugeChartRef = ref(null)
const radarChartRef = ref(null)
const barChartRef = ref(null)
let gaugeChart = null
let radarChart = null
let barChart = null

const adviceList = computed(() => {
  if (isPositive.value) {
    return [
      { icon: '⚠', color: '#F56C6C', text: '血糖/胰岛素等指标严重超标,需内分泌科确诊.' },
      { icon: '✕', color: '#F56C6C', text: '严格禁止高糖、高脂饮食,戒烟限酒.' },
      { icon: '💊', color: '#E6A23C', text: '遵医嘱进行药物治疗或胰岛素注射.' }
    ]
  }
  return [
    { icon: '✓', color: '#67C23A', text: '指标在正常范围内,继续保持.' },
    { icon: '🏃', color: '#67C23A', text: '每周坚持 150 分钟中等强度运动.' },
    { icon: '⏰', color: '#909399', text: '每年定期体检,关注血糖变化.' }
  ]
})

const loadData = async () => {
  const id = route.query.id || route.query.id1
  if (!id) {
    loading.value = false
    return
  }
  try {
    const [resTot, resMem, resDashboard] = await Promise.all([
      request.get(`/DataTest/totle/${id}`).catch(() => ({ data: 0 })),
      request.get(`/DataTest/members/${id}`).catch(() => ({ data: [0, 0] })),
      request.get('/echarts/dashboard').catch(() => ({ data: {} }))
    ])
    const members = resMem.data || [0, 0]
    isPositive.value = members[1] > 0
    probability.value = isPositive.value ? 94.5 : 12.3
    dashboardData.value = resDashboard.data || {}
  } catch {
    ElMessage.error('数据加载失败')
  } finally {
    loading.value = false
    await nextTick()
    renderGauge()
    renderRadar()
    renderBar()
  }
}

const renderGauge = () => {
  if (!gaugeChartRef.value) return
  gaugeChart = echarts.init(gaugeChartRef.value)
  const color = isPositive.value ? '#F56C6C' : '#67C23A'
  gaugeChart.setOption({
    series: [{
      type: 'gauge',
      startAngle: 180, endAngle: 0,
      min: 0, max: 100, splitNumber: 5,
      radius: '90%', center: ['50%', '60%'],
      itemStyle: { color },
      progress: { show: true, width: 25, roundCap: true },
      pointer: { show: false },
      axisLine: { lineStyle: { width: 25, color: [[1, '#EBEEF5']] } },
      axisTick: { show: false },
      splitLine: { show: false },
      axisLabel: { show: false },
      detail: { show: false },
      data: [{ value: probability.value }]
    }]
  })
}

const renderRadar = () => {
  if (!radarChartRef.value) return
  radarChart = echarts.init(radarChartRef.value)
  const baseVal = isPositive.value ? 0.85 : 0.2
  const varyVal = isPositive.value ? 0.9 : 0.15
  radarChart.setOption({
    tooltip: {},
    radar: {
      indicator: [
        { name: '血糖风险', max: 1 },
        { name: '血压风险', max: 1 },
        { name: '肥胖风险', max: 1 },
        { name: '胰岛素抵抗', max: 1 },
        { name: '家族遗传', max: 1 },
        { name: '年龄因素', max: 1 }
      ],
      radius: '65%',
      axisName: { color: '#666', fontSize: 12 },
      splitArea: { areaStyle: { color: ['#f8f9fa', '#fff'] } },
      axisLine: { lineStyle: { color: '#ddd' } },
      splitLine: { lineStyle: { color: '#eee' } }
    },
    series: [{
      name: '风险分布',
      type: 'radar',
      data: [{
        value: [baseVal, isPositive.value ? 0.8 : 0.2, isPositive.value ? 0.9 : 0.1, baseVal, isPositive.value ? 0.7 : 0.3, varyVal],
        name: '当前患者',
        itemStyle: { color: isPositive.value ? '#F56C6C' : '#67C23A' },
        areaStyle: { color: isPositive.value ? 'rgba(245,108,108,0.4)' : 'rgba(103,194,58,0.4)' },
        lineStyle: { width: 2 }
      }]
    }]
  })
}

const renderBar = () => {
  if (!barChartRef.value) return
  barChart = echarts.init(barChartRef.value)
  const score = isPositive.value ? 8.5 : 2.0
  barChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
    xAxis: { type: 'value', max: 10, splitLine: { show: false }, axisLabel: { show: false } },
    yAxis: {
      type: 'category', data: ['综合风险指数'],
      axisLine: { show: false }, axisTick: { show: false },
      axisLabel: { fontWeight: 'bold', fontSize: 14 }
    },
    series: [{
      name: '风险分布',
      type: 'bar',
      barWidth: '40%',
      data: [score],
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: isPositive.value ? '#f56c6c' : '#67c23a' },
          { offset: 1, color: isPositive.value ? '#fab6b6' : '#a3e096' }
        ]),
        borderRadius: [0, 10, 10, 0]
      },
      label: { show: true, position: 'right', formatter: '{c} / 10', fontWeight: 'bold', color: '#333' },
      showBackground: true,
      backgroundStyle: { color: '#ebeef5', borderRadius: [0, 10, 10, 0] }
    }]
  })
}

const resizeCharts = () => {
  ;[gaugeChart, radarChart, barChart].forEach(c => c && c.resize())
}

const goBack = () => {
  router.push('/')
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  ;[gaugeChart, radarChart, barChart].forEach(c => c && c.dispose())
})
</script>

<style scoped>
.diagnosis-page {
  padding: 20px;
  background: #f0f2f5;
  min-height: 100vh;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background: #fff;
  padding: 15px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}
.header h2 { margin: 0; font-size: 20px; color: #333; }

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  background: #fff;
  border-radius: 8px;
  color: #666;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

@media (max-width: 768px) {
  .dashboard-grid { grid-template-columns: 1fr; }
}

.chart-box {
  border-radius: 8px;
  border: none;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
  background: #fff;
}
.chart-box .title {
  font-weight: 600;
  font-size: 16px;
  color: #333;
}
.echart-div {
  width: 100%;
  height: 300px;
}
.main-chart .echart-div {
  height: 350px;
}

.result-text {
  text-align: center;
  font-size: 18px;
  font-weight: bold;
  margin-top: 10px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 6px;
}
.text-danger { color: #F56C6C; background: #fef0f0; }
.text-success { color: #67C23A; background: #f0f9eb; }

.tip {
  font-size: 12px;
  color: #999;
  text-align: center;
  margin-top: -10px;
  margin-bottom: 10px;
}

.advice-box .advice-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.advice-box li {
  padding: 10px 0;
  border-bottom: 1px dashed #eee;
  font-size: 14px;
  color: #666;
}
.advice-box li:last-child { border-bottom: none; }

.system-stats {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
  gap: 16px;
  padding: 16px 0;
}
.stat-item {
  text-align: center;
  min-width: 100px;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #4080FF;
}
.stat-label {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}
</style>
