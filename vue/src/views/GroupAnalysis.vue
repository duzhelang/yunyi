<template>
  <div class="group-analysis-page">
    <h2>群体分析</h2>

    <!-- 患者选择卡片 -->
    <div style="margin: 20px 0">
      <el-card shadow="hover" class="feature-card" style="position: relative;">
        <ProgressOverlay 
          :visible="loading"
          title="正在进行群体分析"
          :steps="groupAnalysisSteps"
          :hints="groupAnalysisHints"
          color="#409eff"
        />
        <template #header>
          <div class="card-header">
            <span>患者选择</span>
            <div class="header-right">
              <el-tag type="info" style="margin-right: 12px">已选 {{ selectedProfileIds.length }} 位患者</el-tag>
              <el-select
                v-model="selectedHistoryId"
                placeholder="历史记录"
                clearable
                size="small"
                style="width: 220px"
                @change="loadHistoryRecord"
              >
                <el-option
                  v-for="h in historyList"
                  :key="h.id"
                  :label="formatHistoryLabel(h)"
                  :value="h.id"
                />
              </el-select>
            </div>
          </div>
        </template>
        <el-form label-width="100px">
          <el-form-item label="选择患者">
            <el-select
              v-model="selectedProfileIds"
              multiple
              filterable
              placeholder="请选择多位患者进行群体分析（建议2-20位）"
              style="width: 100%"
            >
              <el-option
                v-for="p in patientList"
                :key="p.id"
                :label="'档案#' + p.id + ' - 年龄:' + (p.age || '-') + ' 血糖:' + (p.glucose || '-')"
                :value="p.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              :icon="DataAnalysis"
              :loading="loading"
              class="capsule-btn"
              @click="runGroupAnalysis"
            >
              开始群体分析
            </el-button>
            <el-button
              v-if="selectedHistoryId"
              type="danger"
              :icon="Delete"
              plain
              class="capsule-btn"
              @click="deleteHistoryRecord"
            >
              删除该记录
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 分析概要信息卡片 -->
    <div v-if="analysisResult" style="margin: 20px 0">
      <el-card shadow="hover" class="summary-card">
        <div class="summary-content">
          <div class="summary-item">
            <el-icon class="summary-icon"><Clock /></el-icon>
            <span class="summary-label">分析时间：</span>
            <span class="summary-value">{{ formatAnalysisTime(analysisMeta.analysisTime) }}</span>
          </div>
          <div class="summary-item">
            <el-icon class="summary-icon"><User /></el-icon>
            <span class="summary-label">分析规模：</span>
            <span class="summary-value">共分析 {{ analysisResult.totalCount }} 位患者，有效 {{ analysisResult.validCount }} 位</span>
          </div>
          <div class="summary-item">
            <el-icon class="summary-icon"><InfoFilled /></el-icon>
            <span class="summary-label">数据来源：</span>
            <el-tag :type="analysisMeta.source === 'history' ? 'warning' : 'success'" size="small">
              {{ analysisMeta.source === 'history' ? '历史记录' : '实时分析' }}
            </el-tag>
          </div>
        </div>
      </el-card>
    </div>

    <div v-if="analysisResult" class="analysis-content">
      <!-- 统计卡片区域 -->
      <div style="margin: 20px 0">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-value">{{ analysisResult.totalCount }}</div>
              <div class="stat-label">总分析人数</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-value" style="color: #67c23a">{{ analysisResult.riskDistribution.low }}</div>
              <div class="stat-label">低风险</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-value" style="color: #e6a23c">{{ analysisResult.riskDistribution.medium }}</div>
              <div class="stat-label">中风险</div>
            </el-card>
          </el-col>
        </el-row>
        <el-row :gutter="20" style="margin-top: 16px">
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-value" style="color: #f56c6c">{{ analysisResult.riskDistribution.high }}</div>
              <div class="stat-label">高风险</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-value">{{ analysisResult.avgConfidence.toFixed(1) }}%</div>
              <div class="stat-label">平均风险概率</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-value">{{ analysisResult.validCount }}</div>
              <div class="stat-label">有效分析数</div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 风险分布图表 -->
      <div style="margin: 20px 0">
        <el-card shadow="hover" class="feature-card">
          <template #header>
            <div class="card-header">
              <span>风险分布</span>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="12">
              <div ref="pieChartRef" class="chart-container"></div>
            </el-col>
            <el-col :span="12">
              <div ref="barChartRef" class="chart-container"></div>
            </el-col>
          </el-row>
        </el-card>
      </div>

      <!-- 特征重要性排行 -->
      <div style="margin: 20px 0">
        <el-card shadow="hover" class="feature-card">
          <template #header>
            <div class="card-header">
              <span>特征重要性排行</span>
            </div>
          </template>
          <div ref="featureChartRef" class="chart-container"></div>
        </el-card>
      </div>

      <!-- 患者明细表格 -->
      <div style="margin: 20px 0">
        <el-card shadow="hover" class="feature-card">
          <template #header>
            <div class="card-header">
              <span>患者明细</span>
            </div>
          </template>
          <el-table :data="analysisResult.patientResults" border stripe>
            <el-table-column prop="profileId" label="档案ID" width="100" />
            <el-table-column prop="probability" label="风险概率" width="120">
              <template #default="scope">
                <span :style="{ color: getRiskColor(scope.row.riskLevel), fontWeight: 'bold' }">
                  {{ scope.row.probability ? scope.row.probability.toFixed(1) + '%' : '-' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="riskLevel" label="风险等级" width="100">
              <template #default="scope">
                <el-tag :type="getRiskTagType(scope.row.riskLevel)" size="small">
                  {{ getRiskLabel(scope.row.riskLevel) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="age" label="年龄" width="80" />
            <el-table-column prop="gender" label="性别" width="80">
              <template #default="scope">
                {{ scope.row.gender === 'male' ? '男' : scope.row.gender === 'female' ? '女' : '-' }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>

      <!-- 导出报表 -->
      <div style="margin: 20px 0">
        <el-card shadow="hover" class="feature-card">
          <template #header>
            <div class="card-header">
              <span>导出报表</span>
            </div>
          </template>
          <el-form label-width="120px">
            <el-form-item label="报表格式">
              <el-radio-group v-model="exportFormat">
                <el-radio value="pdf">PDF</el-radio>
                <el-radio value="excel">Excel</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" class="capsule-btn" @click="exportReport">
                <el-icon><Download /></el-icon>
                导出报表
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </div>

    <!-- 默认展示内容（未分析时） -->
    <div v-else class="default-content">
      <!-- 功能概览卡片 -->
      <el-row :gutter="20" style="margin: 20px 0">
        <el-col :span="8">
          <el-card shadow="hover" class="overview-feature-card">
            <div class="feature-icon-wrapper" style="background: linear-gradient(135deg, #67c23a, #85ce61)">
              <el-icon class="feature-icon"><DataAnalysis /></el-icon>
            </div>
            <h3>群体风险评估</h3>
            <p>同时分析多位患者的糖尿病风险，快速识别高风险群体</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="overview-feature-card">
            <div class="feature-icon-wrapper" style="background: linear-gradient(135deg, #409eff, #66b1ff)">
              <el-icon class="feature-icon"><PieChart /></el-icon>
            </div>
            <h3>风险分布可视化</h3>
            <p>通过图表直观展示群体风险分布，支持多种图表类型</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="overview-feature-card">
            <div class="feature-icon-wrapper" style="background: linear-gradient(135deg, #e6a23c, #ebb563)">
              <el-icon class="feature-icon"><TrendCharts /></el-icon>
            </div>
            <h3>特征重要性分析</h3>
            <p>分析影响风险的关键因素，为临床决策提供数据支持</p>
          </el-card>
        </el-col>
      </el-row>

      <!-- 示例图表展示 -->
      <el-row :gutter="20" style="margin: 20px 0">
        <el-col :span="12">
          <el-card shadow="hover" class="feature-card">
            <template #header>
              <div class="card-header">
                <span>风险分布示例</span>
                <el-tag type="info" size="small">示例数据</el-tag>
              </div>
            </template>
            <div ref="demoPieChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover" class="feature-card">
            <template #header>
              <div class="card-header">
                <span>特征重要性示例</span>
                <el-tag type="info" size="small">示例数据</el-tag>
              </div>
            </template>
            <div ref="demoBarChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 数据统计概览 -->
      <div style="margin: 20px 0">
        <el-card shadow="hover" class="feature-card">
          <template #header>
            <div class="card-header">
              <span>系统数据概览</span>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number">{{ patientList.length }}</div>
                <div class="stat-text">已录入患者</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number">{{ historyList.length }}</div>
                <div class="stat-text">历史分析记录</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number">8</div>
                <div class="stat-text">分析指标</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number">AI</div>
                <div class="stat-text">智能分析引擎</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </div>

      <!-- 操作指南 -->
      <div style="margin: 20px 0">
        <el-card shadow="hover" class="guide-card">
          <template #header>
            <div class="card-header">
              <span>快速开始</span>
            </div>
          </template>
          <el-steps :active="0" align-center>
            <el-step title="选择患者" description="从列表中选择2-20位患者" :icon="User" />
            <el-step title="开始分析" description="点击开始群体分析按钮" :icon="DataAnalysis" />
            <el-step title="查看结果" description="查看风险分布和特征分析" :icon="PieChart" />
            <el-step title="导出报告" description="导出分析报告供临床参考" :icon="Download" />
          </el-steps>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { DataAnalysis, Download, Delete, Clock, User, InfoFilled, PieChart, TrendCharts } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import request from '@/utils/request'
import { CacheHelper } from '@/utils/cacheHelper'
import ProgressOverlay from '@/components/common/ProgressOverlay.vue'

const groupAnalysisSteps = [
  '校验患者信息',
  '提取健康特征',
  'AI模型推理',
  '汇总分析结果'
]

const groupAnalysisHints = [
  '正在验证所选患者档案的完整性...',
  '正在提取血糖、BMI、血压等关键健康指标...',
  '正在调用AI模型进行风险评估，请耐心等待...',
  '正在计算风险分布和特征重要性...'
]

// ========== 响应式数据 ==========
const selectedProfileIds = ref([])
const patientList = ref([])
const analysisResult = ref(null)
const loading = ref(false)
const exportFormat = ref('pdf')

// 历史记录相关
const historyList = ref([])
const selectedHistoryId = ref(null)

// 分析元信息（时间、来源等）
const analysisMeta = ref({
  analysisTime: null,
  source: 'realtime'
})

// 图表 DOM 引用
const pieChartRef = ref(null)
const barChartRef = ref(null)
const featureChartRef = ref(null)
// 示例图表 DOM 引用
const demoPieChartRef = ref(null)
const demoBarChartRef = ref(null)
// 图表实例
let pieChart = null
let barChart = null
let featureChart = null
let demoPieChart = null
let demoBarChart = null

// ========== 工具函数 ==========

/** 根据风险等级返回对应颜色 */
function getRiskColor(level) {
  if (level === 'high') return '#f56c6c'
  if (level === 'medium') return '#e6a23c'
  return '#67c23a'
}

/** 根据风险等级返回 Element Plus Tag 类型 */
function getRiskTagType(level) {
  if (level === 'high') return 'danger'
  if (level === 'medium') return 'warning'
  return 'success'
}

/** 根据风险等级返回中文标签 */
function getRiskLabel(level) {
  if (level === 'high') return '高风险'
  if (level === 'medium') return '中风险'
  return '低风险'
}

/** 格式化历史记录下拉选项标签 */
function formatHistoryLabel(record) {
  const time = record.analysisTime ? formatAnalysisTime(record.analysisTime) : '未知时间'
  let patientCount = 0
  try {
    const ids = JSON.parse(record.profileIds || '[]')
    patientCount = Array.isArray(ids) ? ids.length : 0
  } catch (e) {
    patientCount = 0
  }
  return `${time} - ${patientCount}位患者`
}

/** 格式化分析时间为可读字符串 */
function formatAnalysisTime(time) {
  if (!time) return '未知时间'
  // 兼容 LocalDateTime 数组格式和字符串格式
  if (Array.isArray(time)) {
    const [y, m, d, h = 0, min = 0, s = 0] = time
    return `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')} ${String(h).padStart(2, '0')}:${String(min).padStart(2, '0')}:${String(s).padStart(2, '0')}`
  }
  if (typeof time === 'string') {
    return time.replace('T', ' ').substring(0, 19)
  }
  return String(time)
}

// ========== 数据加载 ==========

/** 加载患者列表 */
async function loadPatientList() {
  try {
    const res = await request.get('/api/health-profile/list/by-doctor')
    if (res.code === '200') {
      patientList.value = res.data || []
    }
  } catch (e) {
    console.error('加载患者列表失败', e)
  }
}

/** 加载历史记录列表 */
async function loadHistoryList() {
  try {
    const res = await request.get('/api/analysis/history', { params: { limit: 20 } })
    if (res.code === '200') {
      historyList.value = res.data || []
    }
  } catch (e) {
    console.error('加载历史记录失败', e)
  }
}

/** 加载指定历史记录详情 */
async function loadHistoryRecord(recordId) {
  if (!recordId) {
    // 清除选择时重置
    selectedHistoryId.value = null
    return
  }
  loading.value = true
  try {
    const res = await request.get(`/api/analysis/record/${recordId}`)
    if (res.code === '200' && res.data) {
      const record = res.data
      // 解析 resultData（可能是字符串或已解析的对象）
      let resultData = record.resultData
      if (typeof resultData === 'string') {
        try {
          resultData = JSON.parse(resultData)
        } catch (e) {
          console.error('解析历史记录 resultData 失败', e)
          ElMessage.error('历史记录数据格式异常')
          loading.value = false
          return
        }
      }
      // 恢复分析结果
      analysisResult.value = resultData
      // 更新元信息
      analysisMeta.value = {
        analysisTime: record.analysisTime,
        source: 'history'
      }
      // 恢复选中的患者列表
      try {
        const ids = JSON.parse(record.profileIds || '[]')
        selectedProfileIds.value = ids
      } catch (e) {
        console.error('解析 profileIds 失败', e)
      }
      ElMessage.success('历史记录加载成功')
      await nextTick()
      renderAllCharts()
    } else {
      ElMessage.error(res.msg || '加载历史记录失败')
    }
  } catch (e) {
    ElMessage.error('请求失败: ' + (e.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

/** 删除当前选中的历史记录 */
async function deleteHistoryRecord() {
  if (!selectedHistoryId.value) {
    ElMessage.warning('请先选择要删除的历史记录')
    return
  }
  try {
    await ElMessageBox.confirm('确定删除该历史记录吗？删除后不可恢复。', '确认删除', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await request.delete(`/api/analysis/record/${selectedHistoryId.value}`)
    if (res.code === '200') {
      ElMessage.success('删除成功')
      selectedHistoryId.value = null
      // 刷新历史记录列表
      await loadHistoryList()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (e) {
    // 用户取消操作不做提示
    if (e !== 'cancel') {
      ElMessage.error('删除失败: ' + (e.message || '未知错误'))
    }
  }
}

// ========== 群体分析 ==========

/** 执行群体分析 */
async function runGroupAnalysis() {
  if (selectedProfileIds.value.length === 0) {
    ElMessage.warning('请至少选择一位患者')
    return
  }
  loading.value = true
  analysisResult.value = null
  selectedHistoryId.value = null
  try {
    const res = await request.post('/api/analysis/group', {
      profileIds: selectedProfileIds.value
    })
    if (res.code === '200') {
      analysisResult.value = res.data
      // 更新元信息为实时分析
      analysisMeta.value = {
        analysisTime: new Date().toISOString(),
        source: 'realtime'
      }
      ElMessage.success('群体分析完成')
      // 缓存分析记录ID到本地存储，用于页面刷新后自动恢复
      if (res.data && res.data.analysisRecordId) {
        CacheHelper.set('last_group_analysis_id', String(res.data.analysisRecordId))
      }
      // 销毁示例图表
      disposeDemoCharts()
      // 刷新历史列表
      await loadHistoryList()
      await nextTick()
      renderAllCharts()
    } else {
      ElMessage.error(res.msg || '分析失败')
    }
  } catch (e) {
    ElMessage.error('请求失败: ' + (e.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// ========== 图表渲染 ==========

/** 渲染所有图表 */
function renderAllCharts() {
  renderPieChart()
  renderBarChart()
  renderFeatureChart()
}

/** 渲染饼图（风险分布占比） */
function renderPieChart() {
  if (!pieChartRef.value || !analysisResult.value) return
  if (pieChart) pieChart.dispose()
  pieChart = echarts.init(pieChartRef.value)
  const dist = analysisResult.value.riskDistribution
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
    legend: { bottom: 10, left: 'center' },
    series: [{
      type: 'pie',
      radius: ['40%', '65%'],
      avoidLabelOverlap: true,
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { show: true, formatter: '{b}\n{c}人' },
      data: [
        { value: dist.low, name: '低风险', itemStyle: { color: '#67c23a' } },
        { value: dist.medium, name: '中风险', itemStyle: { color: '#e6a23c' } },
        { value: dist.high, name: '高风险', itemStyle: { color: '#f56c6c' } }
      ]
    }]
  })
}

/** 渲染柱状图（风险分布对比） */
function renderBarChart() {
  if (!barChartRef.value || !analysisResult.value) return
  if (barChart) barChart.dispose()
  barChart = echarts.init(barChartRef.value)
  const dist = analysisResult.value.riskDistribution
  barChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: ['低风险', '中风险', '高风险'] },
    yAxis: { type: 'value', name: '人数' },
    series: [{
      type: 'bar',
      data: [
        { value: dist.low, itemStyle: { color: '#67c23a' } },
        { value: dist.medium, itemStyle: { color: '#e6a23c' } },
        { value: dist.high, itemStyle: { color: '#f56c6c' } }
      ],
      barWidth: '50%',
      label: { show: true, position: 'top', formatter: '{c}人' }
    }]
  })
}

/** 渲染特征重要性水平柱状图 */
function renderFeatureChart() {
  if (!featureChartRef.value || !analysisResult.value) return
  if (featureChart) featureChart.dispose()
  featureChart = echarts.init(featureChartRef.value)
  const ranking = analysisResult.value.featureRanking
  if (!ranking) return

  const entries = Object.entries(ranking).sort((a, b) => b[1] - a[1])
  featureChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value', name: '平均SHAP值' },
    yAxis: { type: 'category', data: entries.map(e => e[0]).reverse() },
    series: [{
      type: 'bar',
      data: entries.map(e => e[1]).reverse(),
      barWidth: '60%',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#409eff' },
          { offset: 1, color: '#67c23a' }
        ])
      },
      label: { show: true, position: 'right', formatter: '{c}', fontSize: 11 }
    }]
  })
}

/** 渲染示例图表（默认展示） */
function renderDemoCharts() {
  // 示例饼图
  if (demoPieChartRef.value) {
    demoPieChart = echarts.init(demoPieChartRef.value)
    demoPieChart.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
      legend: { bottom: 10, left: 'center' },
      series: [{
        type: 'pie',
        radius: ['40%', '65%'],
        avoidLabelOverlap: true,
        itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
        label: { show: true, formatter: '{b}\n{c}人' },
        data: [
          { value: 45, name: '低风险', itemStyle: { color: '#67c23a' } },
          { value: 30, name: '中风险', itemStyle: { color: '#e6a23c' } },
          { value: 25, name: '高风险', itemStyle: { color: '#f56c6c' } }
        ]
      }]
    })
  }

  // 示例特征重要性柱状图
  if (demoBarChartRef.value) {
    demoBarChart = echarts.init(demoBarChartRef.value)
    demoBarChart.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'value', name: '重要性' },
      yAxis: {
        type: 'category',
        data: ['年龄', 'BMI', '血压', '胰岛素', '血糖', '怀孕次数'].reverse()
      },
      series: [{
        type: 'bar',
        data: [0.12, 0.15, 0.18, 0.22, 0.28, 0.35].reverse(),
        barWidth: '60%',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#409eff' },
            { offset: 1, color: '#67c23a' }
          ])
        },
        label: { show: true, position: 'right', formatter: '{c}', fontSize: 11 }
      }]
    })
  }
}

/** 销毁示例图表 */
function disposeDemoCharts() {
  if (demoPieChart) {
    demoPieChart.dispose()
    demoPieChart = null
  }
  if (demoBarChart) {
    demoBarChart.dispose()
    demoBarChart = null
  }
}

// ========== 导出报表 ==========

/** 导出分析报表为 HTML 文件 */
function exportReport() {
  if (!analysisResult.value) {
    ElMessage.warning('请先进行分析')
    return
  }
  const data = analysisResult.value
  const dist = data.riskDistribution
  const reportHtml = `
    <html><head><meta charset="utf-8"><title>群体分析报告</title>
    <style>body{font-family:Arial,sans-serif;padding:40px}h1{text-align:center}table{width:100%;border-collapse:collapse;margin:20px 0}td,th{border:1px solid #ddd;padding:8px;text-align:center}th{background:#f5f5f5}</style>
    </head><body>
    <h1>糖尿病风险群体分析报告</h1>
    <h2>一、概况统计</h2>
    <table><tr><th>总分析人数</th><td>${data.totalCount}</td><th>有效分析数</th><td>${data.validCount}</td></tr>
    <tr><th>低风险</th><td style="color:#67c23a">${dist.low}人</td><th>中风险</th><td style="color:#e6a23c">${dist.medium}人</td></tr>
    <tr><th>高风险</th><td style="color:#f56c6c">${dist.high}人</td><th>平均风险概率</th><td>${data.avgConfidence.toFixed(1)}%</td></tr></table>
    <h2>二、患者明细</h2>
    <table><tr><th>档案ID</th><th>风险概率</th><th>风险等级</th><th>年龄</th></tr>
    ${data.patientResults.map(p => '<tr><td>' + p.profileId + '</td><td>' + p.probability.toFixed(1) + '%</td><td>' + getRiskLabel(p.riskLevel) + '</td><td>' + (p.age || '-') + '</td></tr>').join('')}
    </table>
    <p style="text-align:right;color:#999;margin-top:40px">报告生成时间：${new Date().toLocaleString()}</p>
    </body></html>`
  const blob = new Blob([reportHtml], { type: 'text/html;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = '群体分析报告_' + new Date().toISOString().slice(0, 10) + '.html'
  a.click()
  URL.revokeObjectURL(url)
  ElMessage.success('报表已导出（HTML格式，可用浏览器打印为PDF）')
}

// ========== 窗口自适应 ==========

/** 窗口大小变化时重新调整图表尺寸 */
function handleResize() {
  if (pieChart) pieChart.resize()
  if (barChart) barChart.resize()
  if (featureChart) featureChart.resize()
  if (demoPieChart) demoPieChart.resize()
  if (demoBarChart) demoBarChart.resize()
}

// ========== 生命周期 ==========

onMounted(async () => {
  // 加载患者列表和历史记录
  await Promise.all([loadPatientList(), loadHistoryList()])
  window.addEventListener('resize', handleResize)

  // 渲染示例图表
  await nextTick()
  renderDemoCharts()

  // 自动恢复上次分析结果
  const lastAnalysisId = CacheHelper.get('last_group_analysis_id')
  if (lastAnalysisId) {
    try {
      const res = await request.get(`/api/analysis/record/${lastAnalysisId}`)
      if (res.code === '200' && res.data) {
        const record = res.data
        // 解析 resultData
        let resultData = record.resultData
        if (typeof resultData === 'string') {
          try {
            resultData = JSON.parse(resultData)
          } catch (e) {
            console.error('自动恢复：解析 resultData 失败', e)
            return
          }
        }
        // 恢复分析结果和元信息
        analysisResult.value = resultData
        analysisMeta.value = {
          analysisTime: record.analysisTime,
          source: 'history'
        }
        // 恢复选中的患者列表
        try {
          const ids = JSON.parse(record.profileIds || '[]')
          selectedProfileIds.value = ids
        } catch (e) {
          console.error('自动恢复：解析 profileIds 失败', e)
        }
        // 选中历史记录下拉
        selectedHistoryId.value = Number(lastAnalysisId)
        // 销毁示例图表
        disposeDemoCharts()
        await nextTick()
        renderAllCharts()
      }
    } catch (e) {
      console.error('自动恢复分析记录失败', e)
      // 恢复失败时清除缓存，不影响正常使用
      CacheHelper.remove('last_group_analysis_id')
    }
  }
})

onBeforeUnmount(() => {
  if (pieChart) pieChart.dispose()
  if (barChart) barChart.dispose()
  if (featureChart) featureChart.dispose()
  disposeDemoCharts()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

/* 统计卡片样式 */
.stat-card {
  text-align: center;
  padding: 20px 0;
  border-radius: 14px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

/* 图表容器 */
.chart-container {
  height: 350px;
  width: 100%;
}

/* 特征卡片统一样式 */
.feature-card {
  border-radius: 14px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 分析概要卡片 - 淡蓝色渐变背景 */
.summary-card {
  border-radius: 14px;
  background: linear-gradient(135deg, #e8f4fd 0%, #f0f7ff 50%, #eaf5ff 100%);
  border: 1px solid #d6e8f7;
}

.summary-content {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  align-items: center;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.summary-icon {
  color: #409eff;
  font-size: 16px;
}

.summary-label {
  color: #606266;
  font-size: 14px;
  white-space: nowrap;
}

.summary-value {
  color: #303133;
  font-weight: 500;
  font-size: 14px;
}

/* 胶囊形按钮 */
.capsule-btn {
  border-radius: 20px !important;
}

/* 默认展示内容样式 */
.default-content {
  margin-top: 20px;
}

/* 功能概览卡片 */
.overview-feature-card {
  text-align: center;
  padding: 30px 20px;
  border-radius: 14px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.overview-feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.feature-icon-wrapper {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}

.feature-icon {
  font-size: 28px;
  color: #fff;
}

.overview-feature-card h3 {
  font-size: 18px;
  color: #303133;
  margin: 0 0 10px;
}

.overview-feature-card p {
  font-size: 14px;
  color: #606266;
  margin: 0;
  line-height: 1.5;
}

/* 数据统计概览 */
.stat-item {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #fff 100%);
  border-radius: 12px;
  transition: transform 0.2s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
}

.stat-number {
  font-size: 36px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-text {
  font-size: 14px;
  color: #606266;
}

/* 操作指南卡片 */
.guide-card {
  border-radius: 14px;
  background: linear-gradient(135deg, #f0f9eb 0%, #fff 100%);
}

.guide-card :deep(.el-step__title) {
  font-size: 15px;
}

.guide-card :deep(.el-step__description) {
  font-size: 13px;
}
</style>
