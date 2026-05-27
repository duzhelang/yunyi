<template>
  <div class="doctor-workbench">
    <!-- ========== 头部统计卡片 ========== -->
    <div class="stats-row">
      <div class="stat-card pending">
        <div class="stat-icon">⏳</div>
        <div class="stat-info">
          <div class="stat-number">{{ filteredPendingList.length }}</div>
          <div class="stat-label">待诊断</div>
        </div>
      </div>
      <div class="stat-card done">
        <div class="stat-icon">✅</div>
        <div class="stat-info">
          <div class="stat-number">{{ doneList.length }}</div>
          <div class="stat-label">已完成</div>
        </div>
      </div>
      <div class="stat-card total">
        <div class="stat-icon">📋</div>
        <div class="stat-info">
          <div class="stat-number">{{ allList.length }}</div>
          <div class="stat-label">总记录</div>
        </div>
      </div>
      <div class="stat-card ai">
        <div class="stat-icon">🤖</div>
        <div class="stat-info">
          <div class="stat-number">{{ aiDiagnosedCount }}</div>
          <div class="stat-label">AI诊断</div>
        </div>
      </div>
    </div>

    <!-- ========== 搜索与刷新 ========== -->
    <div class="action-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索患者 ID / 症状"
        clearable
        :prefix-icon="Search"
        style="width: 260px"
        @input="filterData"
      />
      <el-button type="primary" :icon="Refresh" @click="loadData">刷新列表</el-button>
    </div>

    <!-- ========== 状态标签页 ========== -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane :label="'⏳ 待诊断 (' + filteredPendingList.length + ')'" name="pending">
        <el-table :data="paginatedPending" v-loading="loading" stripe border empty-text="暂无待诊断患者">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="createTime" label="提交时间" width="170" />
          <el-table-column label="关键指标" min-width="260">
            <template #default="{ row }">
              <div class="metric-row">
                <span class="metric-item"><b>年龄</b> {{ row.Age || row.age || '—' }}</span>
                <span class="metric-item"><b>血糖</b> <span class="text-red">{{ row.Glucose || row.glucose || '—' }}</span></span>
                <span class="metric-item"><b>BMI</b> {{ row.BMI || row.bmi || '—' }}</span>
              </div>
              <div v-if="row.symptoms" class="symptoms-tag">
                <i class="el-icon-warning-outline"></i> {{ row.symptoms }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="attachment" label="附件" width="120">
            <template #default="{ row }">
              <el-tag v-if="row.fileName" size="small" type="info">{{ row.fileName }}</el-tag>
              <span v-else class="text-muted">无</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="300" fixed="right">
            <template #default="{ row }">
              <el-button size="small" type="info" icon="el-icon-download" @click="handleDownload(row.id)">下载CSV</el-button>
              <el-button size="small" type="warning" @click="openAiDiagnoseDialog(row)">AI诊断</el-button>
              <el-button size="small" type="primary" @click="openDiagnoseDialog(row)">填写结果</el-button>
            </template>
          </el-table-column>
        </el-table>
        <!-- 分页 -->
        <div class="pagination-box">
          <el-pagination
            v-model:current-page="pendingPageNum"
            v-model:page-size="pendingPageSize"
            :total="filteredPendingList.length"
            layout="total, sizes, prev, pager, next"
            :page-sizes="[5, 10, 20]"
          />
        </div>
      </el-tab-pane>

      <el-tab-pane :label="'✅ 已完成 (' + doneList.length + ')'" name="done">
        <el-table :data="doneList" stripe border empty-text="暂无已完成诊断">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="createTime" label="提交时间" width="170" />
          <el-table-column label="关键指标" min-width="200">
            <template #default="{ row }">
              <div class="metric-row">
                <span><b>血糖</b> {{ row.glucose || row.Glucose }}</span>
                <span><b>BMI</b> {{ row.bmi || row.BMI }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="diagnosisResult" label="诊断结论" min-width="220">
            <template #default="{ row }">
              <div class="result-text">{{ row.diagnosisResult || '—' }}</div>
            </template>
          </el-table-column>
          <el-table-column label="诊断方式" width="100">
            <template #default="{ row }">
              <el-tag :type="row.diagnosisMethod === 'AI' ? 'success' : 'info'" size="small">
                {{ row.diagnosisMethod === 'AI' ? 'AI诊断' : '人工诊断' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- ========== AI诊断弹窗 ========== -->
    <el-dialog
      v-model="aiDialogVisible"
      title="🤖 AI智能诊断"
      width="800px"
      destroy-on-close
    >
      <div class="ai-dialog-body">
        <div class="patient-summary">
          <div class="sum-item"><strong>患者 ID：</strong>{{ currentRow.id }}</div>
          <div class="sum-item"><strong>年龄：</strong>{{ currentRow.Age || currentRow.age || '—' }}</div>
          <div class="sum-item"><strong>血糖：</strong><span class="text-red">{{ currentRow.Glucose || currentRow.glucose || '—' }}</span></div>
          <div class="sum-item"><strong>BMI：</strong>{{ currentRow.BMI || currentRow.bmi || '—' }}</div>
          <div class="sum-item" v-if="currentRow.symptoms"><strong>症状：</strong>{{ currentRow.symptoms }}</div>
        </div>

        <div v-if="aiPredictionResult" class="ai-result-section">
          <h4>AI预测结果</h4>
          <div class="prediction-result">
            <div class="risk-level" :class="getRiskClass(aiPredictionResult.risk_level)">
              风险等级：{{ getRiskText(aiPredictionResult.risk_level) }}
            </div>
            <div class="probability">
              患病概率：{{ (aiPredictionResult.probability * 100).toFixed(1) }}%
            </div>
            <div class="confidence">
              置信区间：[{{ (aiPredictionResult.confidence_interval[0] * 100).toFixed(1) }}%, {{ (aiPredictionResult.confidence_interval[1] * 100).toFixed(1) }}%]
            </div>
          </div>

          <div class="feature-importance">
            <h4>影响因素分析</h4>
            <div class="feature-list">
              <div v-for="(feature, index) in aiPredictionResult.feature_importance" :key="index" class="feature-item">
                <span class="feature-name">{{ feature.name }}</span>
                <el-progress 
                  :percentage="Math.min(100, feature.importance * 100)" 
                  :stroke-width="10"
                  :show-text="false"
                />
                <span class="feature-value">{{ (feature.importance * 100).toFixed(1) }}%</span>
              </div>
            </div>
          </div>

          <div class="ai-advice">
            <h4>AI健康建议</h4>
            <p>{{ getHealthAdvice(aiPredictionResult.risk_level) }}</p>
          </div>
        </div>

        <div v-else-if="aiLoading" class="ai-loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>正在分析数据，请稍候...</span>
        </div>

        <div v-else class="ai-empty">
          <p>点击下方按钮开始AI诊断分析</p>
        </div>
      </div>

      <template #footer>
        <el-button @click="aiDialogVisible = false">取消</el-button>
        <el-button type="warning" @click="runAiPrediction" :loading="aiLoading">
          {{ aiPredictionResult ? '重新分析' : '开始AI诊断' }}
        </el-button>
        <el-button type="success" @click="submitAiDiagnosis" :disabled="!aiPredictionResult">
          提交诊断结果
        </el-button>
      </template>
    </el-dialog>

    <!-- ========== 手动填写诊断结果弹窗 ========== -->
    <el-dialog
      v-model="dialogVisible"
      title="✍️ 录入诊断结果"
      width="560px"
      destroy-on-close
    >
      <div class="dialog-body">
        <div class="patient-summary">
          <div class="sum-item"><strong>患者 ID：</strong>{{ currentRow.id }}</div>
          <div class="sum-item"><strong>血糖：</strong><span class="text-red">{{ currentRow.Glucose || currentRow.glucose }}</span></div>
          <div class="sum-item"><strong>BMI：</strong>{{ currentRow.BMI || currentRow.bmi }}</div>
          <div class="sum-item" v-if="currentRow.symptoms"><strong>症状：</strong>{{ currentRow.symptoms }}</div>
        </div>

        <el-alert
          title="操作流程"
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 20px"
        >
          <template #default>
            <ol style="margin:0;padding-left:20px">
              <li>点击左侧表格中的【下载CSV】按钮</li>
              <li>将 CSV 文件放入 Python 预测模型运行</li>
              <li>将模型输出的预测结果填入下方文本框</li>
            </ol>
          </template>
        </el-alert>

        <el-form :model="form" label-width="100px">
          <el-form-item label="预测结论" required>
            <el-input
              v-model="form.result"
              type="textarea"
              :rows="6"
              placeholder="例如：模型预测结果为阳性（Positive），患病概率 85%。建议立即前往内分泌科就诊..."
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="success" @click="submitDiagnosis" :loading="submitting">✅ 提交并通知用户</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Loading } from '@element-plus/icons-vue'
import { usePrediction } from '@/composables/usePrediction'

const { runPrediction, getRiskText, getRiskClass, getHealthAdvice } = usePrediction()

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const aiDialogVisible = ref(false)
const aiLoading = ref(false)
const aiPredictionResult = ref(null)
const currentRow = ref({})
const form = ref({ result: '' })
const activeTab = ref('pending')
const searchKeyword = ref('')
const pendingPageNum = ref(1)
const pendingPageSize = ref(5)
const allList = ref([])

const mockData = [
  { id: 201, createTime: '2026-04-27 14:30:00', Age: 45, Glucose: 198, BMI: 28.4, symptoms: '多饮多尿、视力模糊', fileName: 'lab_report_01.pdf', status: 'PENDING' },
  { id: 202, createTime: '2026-04-27 11:15:00', Age: 62, Glucose: 255, BMI: 32.1, symptoms: '伤口愈合缓慢、频繁感染', fileName: '', status: 'PENDING' },
  { id: 203, createTime: '2026-04-26 16:45:00', Age: 38, Glucose: 175, BMI: 26.8, symptoms: '无明显症状，家族有糖尿病史', fileName: 'blood_test.jpg', status: 'PENDING' },
  { id: 204, createTime: '2026-04-26 09:00:00', Age: 55, Glucose: 140, BMI: 24.5, symptoms: '', status: 'DONE', diagnosisResult: '模型预测阴性，概率 12%，建议保持健康生活方式', diagnosisMethod: 'AI' },
  { id: 205, createTime: '2026-04-25 08:30:00', Age: 29, Glucose: 95, BMI: 21.2, symptoms: '无', status: 'DONE', diagnosisResult: '模型预测阴性，概率 5%，无需担忧', diagnosisMethod: '人工' }
]

const filteredPendingList = computed(() => {
  let list = allList.value.filter(item => item.status === 'PENDING')
  if (searchKeyword.value) {
    const kw = searchKeyword.value.toLowerCase()
    list = list.filter(item =>
      String(item.id).includes(kw) ||
      (item.symptoms && item.symptoms.toLowerCase().includes(kw))
    )
  }
  return list
})

const doneList = computed(() => {
  return allList.value.filter(item => item.status === 'DONE')
})

const paginatedPending = computed(() => {
  const start = (pendingPageNum.value - 1) * pendingPageSize.value
  return filteredPendingList.value.slice(start, start + pendingPageSize.value)
})

const aiDiagnosedCount = computed(() => {
  return allList.value.filter(item => item.diagnosisMethod === 'AI').length
})

onMounted(() => {
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    const allRes = await request.get('/api/health-profile/list')
    if (allRes && allRes.data) {
      const rawData = Array.isArray(allRes.data) ? allRes.data : (allRes.data.data || [])
      allList.value = rawData.map(item => ({
        ...item,
        status: item.status || item.Status || 'PENDING'
      }))
    } else {
      throw new Error('EMPTY_DATA')
    }
  } catch (e) {
    console.warn('后端数据获取失败，使用模拟数据', e)
    allList.value = JSON.parse(JSON.stringify(mockData))
  } finally {
    loading.value = false
    console.log('allList:', allList.value)
  }
}

function filterData() {
  pendingPageNum.value = 1
}

function handleTabChange() {
  pendingPageNum.value = 1
}

async function handleDownload(id) {
  if (!id) return ElMessage.error('ID 缺失')
  try {
    const response = await request({
      url: `/api/health-profile/download-csv/${id}`,
      method: 'GET',
      responseType: 'blob',
      timeout: 30000
    })
    const blob = new Blob([response.data], { type: 'text/csv;charset=UTF-8' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `patient_${id}.csv`
    a.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('CSV 已下载')
  } catch (e) {
    ElMessage.success('模拟下载成功（CSV 已准备）')
  }
}

function openDiagnoseDialog(row) {
  currentRow.value = row
  form.value.result = ''
  dialogVisible.value = true
}

function openAiDiagnoseDialog(row) {
  currentRow.value = row
  aiPredictionResult.value = null
  aiDialogVisible.value = true
}

async function runAiPrediction() {
  if (!currentRow.value) return
  
  aiLoading.value = true
  try {
    const features = {
      age: currentRow.value.Age || currentRow.value.age || 30,
      glucose: currentRow.value.Glucose || currentRow.value.glucose || 90,
      bmi: currentRow.value.BMI || currentRow.value.bmi || 22,
      bloodPressure: currentRow.value.BloodPressure || currentRow.value.bloodPressure || 120,
      insulin: currentRow.value.Insulin || currentRow.value.insulin || 50,
      skinThickness: currentRow.value.SkinThickness || currentRow.value.skinThickness || 20,
      pregnancies: currentRow.value.Pregnancies || currentRow.value.pregnancies || 0,
      diabetesPedigreeFunction: currentRow.value.DiabetesPedigreeFunction || currentRow.value.diabetesPedigreeFunction || 0.5
    }
    
    const result = await runPrediction(features)
    if (result.success) {
      aiPredictionResult.value = result.data
      ElMessage.success('AI诊断完成')
    } else {
      ElMessage.warning(result.message || 'AI诊断失败，使用本地预测')
      aiPredictionResult.value = result.data
    }
  } catch (e) {
    ElMessage.error('AI诊断失败: ' + e.message)
  } finally {
    aiLoading.value = false
  }
}

async function submitAiDiagnosis() {
  if (!aiPredictionResult.value) return
  
  const resultText = `AI诊断结果：风险等级 ${getRiskText(aiPredictionResult.value.risk_level)}，患病概率 ${(aiPredictionResult.value.probability * 100).toFixed(1)}%。${getHealthAdvice(aiPredictionResult.value.risk_level)}`
  
  try {
    await request.post('/api/health-profile/doctor/submit-result', {
      profileId: currentRow.value.id,
      result: resultText,
      diagnosisMethod: 'AI',
      riskLevel: aiPredictionResult.value.risk_level,
      probability: aiPredictionResult.value.probability
    })
    
    ElMessage.success('AI诊断结果已提交')
    aiDialogVisible.value = false
    
    const idx = allList.value.findIndex(item => item.id === currentRow.value.id)
    if (idx !== -1) {
      allList.value[idx].status = 'DONE'
      allList.value[idx].diagnosisResult = resultText
      allList.value[idx].diagnosisMethod = 'AI'
    }
    await loadData()
  } catch (e) {
    const idx = allList.value.findIndex(item => item.id === currentRow.value.id)
    if (idx !== -1) {
      allList.value[idx].status = 'DONE'
      allList.value[idx].diagnosisResult = resultText
      allList.value[idx].diagnosisMethod = 'AI'
    }
    ElMessage.success('提交成功（模拟）')
    aiDialogVisible.value = false
  }
}

async function submitDiagnosis() {
  if (!form.value.result.trim()) return ElMessage.warning('请填写诊断结论')
  submitting.value = true
  try {
    await request.post('/api/health-profile/doctor/submit-result', {
      profileId: currentRow.value.id,
      result: form.value.result,
      diagnosisMethod: '人工'
    })
    ElMessage.success('诊断已提交')
    dialogVisible.value = false
    const idx = allList.value.findIndex(item => item.id === currentRow.value.id)
    if (idx !== -1) {
      allList.value[idx].status = 'DONE'
      allList.value[idx].diagnosisResult = form.value.result
      allList.value[idx].diagnosisMethod = '人工'
    }
    await loadData()
  } catch (e) {
    const idx = allList.value.findIndex(item => item.id === currentRow.value.id)
    if (idx !== -1) {
      allList.value[idx].status = 'DONE'
      allList.value[idx].diagnosisResult = form.value.result
      allList.value[idx].diagnosisMethod = '人工'
    }
    ElMessage.success('提交成功（模拟）')
    dialogVisible.value = false
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.doctor-workbench {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}
.stat-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  transition: transform 0.2s;
}
.stat-card:hover { transform: translateY(-3px); }
.stat-card.pending { border-left: 4px solid #E6A23C; }
.stat-card.done { border-left: 4px solid #67C23A; }
.stat-card.total { border-left: 4px solid #409EFF; }
.stat-card.ai { border-left: 4px solid #9B59B6; }
.stat-icon { font-size: 32px; }
.stat-number { font-size: 28px; font-weight: 700; color: #303133; }
.stat-label { font-size: 14px; color: #909399; margin-top: 4px; }

.action-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  align-items: center;
}

.metric-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
.metric-item {
  font-size: 13px;
  color: #606266;
}
.text-red { color: #F56C6C; font-weight: 600; }
.text-muted { color: #C0C4CC; }
.symptoms-tag {
  margin-top: 6px;
  background: #fdf6ec;
  color: #E6A23C;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  display: inline-block;
}
.result-text {
  font-size: 13px;
  line-height: 1.5;
}

.dialog-body {
  padding: 0 10px;
}
.patient-summary {
  background: #f9fafb;
  border-radius: 8px;
  padding: 12px 16px;
  margin-bottom: 20px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}
.patient-summary .sum-item {
  font-size: 14px;
}

.pagination-box {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.ai-dialog-body {
  padding: 0 10px;
}

.ai-result-section {
  margin-top: 20px;
}

.ai-result-section h4 {
  margin: 16px 0 8px 0;
  color: #303133;
  font-size: 16px;
}

.prediction-result {
  background: #f9fafb;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
}

.risk-level {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
  padding: 8px 12px;
  border-radius: 6px;
  display: inline-block;
}

.risk-level.low-risk {
  background: #f0f9ff;
  color: #67C23A;
  border: 1px solid #67C23A;
}

.risk-level.medium-risk {
  background: #fdf6ec;
  color: #E6A23C;
  border: 1px solid #E6A23C;
}

.risk-level.high-risk {
  background: #fef0f0;
  color: #F56C6C;
  border: 1px solid #F56C6C;
}

.probability {
  font-size: 16px;
  margin-bottom: 4px;
}

.confidence {
  font-size: 14px;
  color: #909399;
}

.feature-importance {
  margin-bottom: 20px;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.feature-name {
  width: 80px;
  font-size: 14px;
  color: #606266;
}

.feature-value {
  width: 50px;
  font-size: 14px;
  color: #909399;
  text-align: right;
}

.ai-advice {
  background: #f0f9ff;
  border-radius: 8px;
  padding: 16px;
  border-left: 4px solid #409EFF;
}

.ai-advice h4 {
  margin: 0 0 8px 0;
  color: #409EFF;
}

.ai-advice p {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}

.ai-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #909399;
}

.ai-loading .is-loading {
  font-size: 32px;
  margin-bottom: 12px;
}

.ai-empty {
  text-align: center;
  padding: 40px;
  color: #909399;
}
</style>
