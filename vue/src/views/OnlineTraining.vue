<template>
  <div class="online-training">
    <div class="page-header">
      <div class="title-section">
        <h2 class="page-title">
          <el-icon><Cpu /></el-icon>
          在线模型训练与预测
        </h2>
        <p class="page-desc">训练任务启动器、模型预测和结果看板</p>
      </div>
    </div>

    <!-- 功能切换标签页 -->
    <el-tabs v-model="activeTab" class="main-tabs">
      <!-- 训练任务标签页 -->
      <el-tab-pane label="模型训练" name="training">
        <div class="create-section">
          <el-card>
            <template #header>
              <div class="card-header">
                <span class="card-title">
                  <el-icon><Plus /></el-icon>
                  新建训练任务
                </span>
                <el-button type="primary" plain size="small" :icon="Refresh" @click="scanFiles" :loading="scanning">扫描文件</el-button>
              </div>
            </template>
            
            <el-form :model="taskForm" label-width="120px">
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="训练数据集">
                    <el-select v-model="taskForm.trainFileId" placeholder="请选择训练数据集" style="width: 100%">
                      <el-option
                        v-for="file in fileList"
                        :key="file.id"
                        :label="file.name + (file.sampleCount ? ' (' + file.sampleCount + ' 条)' : '')"
                        :value="file.id"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="训练脚本">
                    <el-select v-model="taskForm.pythonScript" placeholder="请选择训练脚本" style="width: 100%">
                      <el-option
                        v-for="script in pythonScripts"
                        :key="script.name"
                        :label="script.name + ' - ' + script.description"
                        :value="script.name"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="模型名称">
                    <el-input v-model="taskForm.modelName" placeholder="例如: diabetes_model_v2" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="选择模型">
                    <el-select v-model="taskForm.selectedModel" placeholder="请选择模型" style="width: 100%">
                      <el-option label="使用训练脚本新建模型" value="new_model" />
                      <el-option
                        v-for="model in trainedModels"
                        :key="model.id"
                        :label="model.modelName + ' (' + model.version + ')'"
                        :value="String(model.id)"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>

              
              <el-divider content-position="left">训练参数</el-divider>
              
              <el-row :gutter="20">
                <el-col :span="6">
                  <el-form-item label="学习率">
                    <el-input v-model="taskForm.hyperParams.learningRate" type="number" step="0.0001" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="训练轮数">
                    <el-input v-model="taskForm.hyperParams.epochs" type="number" :min="1" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="批次大小">
                    <el-input v-model="taskForm.hyperParams.batchSize" type="number" :min="1" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item>
                    <el-button type="primary" @click="startTask" :loading="submitting">
                      <el-icon><VideoPlay /></el-icon>
                      启动训练
                    </el-button>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </el-card>
        </div>

        <div class="table-section">
          <el-card>
            <template #header>
              <div class="card-header">
                <span class="card-title">
                  <el-icon><List /></el-icon>
                  训练任务历史
                </span>
                <el-button size="small" @click="loadData">刷新</el-button>
              </div>
            </template>

            <el-table 
              :data="tableData" 
              style="width: 100%"
              v-loading="loading"
              stripe
              border
            >
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="taskName" label="任务名称" min-width="180" />
              <el-table-column prop="trainFileName" label="训练文件" min-width="150" />
              <el-table-column prop="modelName" label="模型名称" min-width="150" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="getStatusTagType(scope.row.status)">
                    {{ getStatusText(scope.row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="性能指标" width="280">
                <template #default="scope">
                  <div v-if="scope.row.status === 'completed'" class="metrics">
                    <span class="metric-item">准确率: {{ scope.row.accuracy }}</span>
                    <span class="metric-item">损失: {{ scope.row.loss }}</span>
                  </div>
                  <span v-else>-</span>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="创建时间" width="180" />
              <el-table-column label="操作" width="180" fixed="right">
                <template #default="scope">
                  <el-button type="primary" size="small" @click="viewDetail(scope.row)">详情</el-button>
                  <el-button v-if="scope.row.status === 'failed'" type="warning" size="small" @click="retryTask(scope.row)">重试</el-button>
                  <el-button type="danger" size="small" @click="deleteTask(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="pagination-box">
              <el-pagination
                v-model:current-page="pageNum"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 50, 100]"
                :total="total"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="loadData"
                @current-change="loadData"
              />
            </div>
          </el-card>
        </div>
      </el-tab-pane>

      <!-- 模型预测标签页 -->
      <el-tab-pane label="模型预测" name="predict">
        <div class="predict-section">
          <el-card>
            <template #header>
              <div class="card-header">
                <span class="card-title">
                  <el-icon><DataAnalysis /></el-icon>
                  模型预测配置
                </span>
                <el-button type="primary" plain size="small" :icon="Refresh" @click="scanTestFiles" :loading="scanningTestFiles">扫描测试文件</el-button>
              </div>
            </template>
            
            <el-form :model="predictForm" label-width="120px">
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="测试数据文件">
                    <el-select v-model="predictForm.testFilePath" placeholder="请选择测试数据文件" style="width: 100%">
                      <el-option
                        v-for="file in testFileList"
                        :key="file.id"
                        :label="file.name + (file.sampleCount ? ' (' + file.sampleCount + ' 条)' : '')"
                        :value="file.url"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="预测模型">
                    <el-select v-model="predictForm.modelPath" placeholder="请选择预测模型" style="width: 100%">
                      <el-option
                        v-for="model in availableModels"
                        :key="model.id"
                        :label="model.modelName + ' (' + model.version + ')'"
                        :value="model.filePath"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="预测标题">
                    <el-input v-model="predictForm.title" placeholder="例如: 糖尿病筛查检测" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="输出文件名">
                    <el-input v-model="predictForm.jsonName" placeholder="留空自动生成" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="24">
                  <el-form-item>
                    <el-button type="primary" @click="startPredict" :loading="predicting" :disabled="!predictForm.testFilePath || !predictForm.modelPath">
                      <el-icon><VideoPlay /></el-icon>
                      开始预测
                    </el-button>
                    <el-button @click="resetPredictForm">重置</el-button>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </el-card>
        </div>

        <!-- 预测进度提示 -->
        <div v-if="predicting" class="predict-progress">
          <el-card>
            <div class="progress-content">
              <el-icon class="rotating"><Loading /></el-icon>
              <span>正在执行预测，请稍候...</span>
            </div>
          </el-card>
        </div>

        <!-- 预测结果列表 -->
        <div class="result-section">
          <el-card>
            <template #header>
              <div class="card-header">
                <span class="card-title">
                  <el-icon><Document /></el-icon>
                  预测结果文件
                </span>
                <el-button size="small" @click="loadPredictResults">刷新</el-button>
              </div>
            </template>

            <el-table 
              :data="predictResults" 
              style="width: 100%"
              v-loading="loadingResults"
              stripe
              border
            >
              <el-table-column prop="fileName" label="文件名" min-width="200" />
              <el-table-column label="文件大小" width="120">
                <template #default="scope">
                  {{ formatFileSize(scope.row.size) }}
                </template>
              </el-table-column>
              <el-table-column label="生成时间" width="180">
                <template #default="scope">
                  {{ formatTime(scope.row.lastModified) }}
                </template>
              </el-table-column>
              <el-table-column label="状态" width="100">
                <template #default="scope">
                  <el-tag type="success">已完成</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="200" fixed="right">
                <template #default="scope">
                  <el-button type="primary" size="small" @click="viewPredictResult(scope.row)">查看详情</el-button>
                  <el-button type="success" size="small" @click="downloadResult(scope.row)">下载</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 训练任务详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="训练任务详情"
      width="600px"
    >
      <el-descriptions v-if="currentTask" :column="1" border>
        <el-descriptions-item label="任务ID">{{ currentTask.id }}</el-descriptions-item>
        <el-descriptions-item label="任务名称">{{ currentTask.taskName }}</el-descriptions-item>
        <el-descriptions-item label="训练文件">{{ currentTask.trainFileName }}</el-descriptions-item>
        <el-descriptions-item label="模型名称">{{ currentTask.modelName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTagType(currentTask.status)">
            {{ getStatusText(currentTask.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="性能指标">
          <div v-if="currentTask.status === 'completed'" class="metrics">
            <div class="metric-item">准确率: {{ currentTask.accuracy }}</div>
            <div class="metric-item">损失: {{ currentTask.loss }}</div>
            <div class="metric-item">召回率: {{ currentTask.recallRate }}</div>
            <div class="metric-item">精确率: {{ currentTask.precisionRate }}</div>
            <div class="metric-item">F1分数: {{ currentTask.f1Score }}</div>
          </div>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentTask.createTime }}</el-descriptions-item>
        <el-descriptions-item v-if="currentTask.startTime" label="开始时间">{{ currentTask.startTime }}</el-descriptions-item>
        <el-descriptions-item v-if="currentTask.endTime" label="结束时间">{{ currentTask.endTime }}</el-descriptions-item>
        <el-descriptions-item v-if="currentTask.errorMessage" label="错误信息">
          <div class="error-message">{{ currentTask.errorMessage }}</div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 预测结果详情对话框 -->
    <el-dialog
      v-model="predictResultVisible"
      title="预测结果详情"
      width="800px"
    >
      <div v-if="currentPredictResult" class="predict-result-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="文件名">{{ currentPredictResult.fileName }}</el-descriptions-item>
          <el-descriptions-item label="文件大小">{{ formatFileSize(currentPredictResult.size) }}</el-descriptions-item>
        </el-descriptions>
        
        <div class="result-content">
          <h4>预测结果内容：</h4>
          <pre class="json-content">{{ currentPredictResult.content }}</pre>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Cpu, Plus, List, VideoPlay, Refresh, DataAnalysis, Document, Loading } from '@element-plus/icons-vue'
import request from '@/utils/request'

const activeTab = ref('training')

const taskForm = ref({
  trainFileId: null,
  selectedModel: 'new_model',
  modelName: '',
  pythonScript: 'train.py',
  hyperParams: {
    learningRate: 0.001,
    epochs: 100,
    batchSize: 32
  }
})

const predictForm = ref({
  testFilePath: '',
  modelPath: '',
  title: '',
  jsonName: ''
})

const fileList = ref([])
const testFileList = ref([])
const pythonScripts = ref([])
const trainedModels = ref([])
const availableModels = ref([])
const tableData = ref([])
const predictResults = ref([])
const loading = ref(false)
const loadingResults = ref(false)
const submitting = ref(false)
const scanning = ref(false)
const scanningTestFiles = ref(false)
const predicting = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailVisible = ref(false)
const predictResultVisible = ref(false)
const currentTask = ref(null)
const currentPredictResult = ref(null)

const loadFileList = async () => {
  try {
    const response = await request.get('/api/dataset/all')
    if (response.code === '200') {
      fileList.value = response.data.filter(f => f.category === 'train')
    }
  } catch (error) {
    console.error('加载训练文件列表失败', error)
  }
}

const loadTestFiles = async () => {
  try {
    const response = await request.get('/api/dataset/all', { params: { category: 'test' } })
    if (response.code === '200') {
      testFileList.value = (response.data || []).filter(f => f.category === 'test' && f.url)
      if (!predictForm.value.testFilePath && testFileList.value.length) {
        predictForm.value.testFilePath = testFileList.value[0].url
      }
    }
  } catch (error) {
    console.error('加载测试文件列表失败', error)
  }
}

const loadPythonScripts = async () => {
  try {
    const response = await request.get('/api/python-scripts/training')
    if (response.code === '200') {
      pythonScripts.value = response.data
      if (!taskForm.value.pythonScript && pythonScripts.value.length > 0) {
        const defaultScript = pythonScripts.value.find(s => s.name === 'train.py')
        taskForm.value.pythonScript = defaultScript ? defaultScript.name : pythonScripts.value[0].name
      }
    }
  } catch (error) {
    console.error('加载Python脚本列表失败', error)
  }
}

const loadTrainedModels = async () => {
  try {
    const response = await request.get('/api/model/all')
    if (response.code === '200') {
      trainedModels.value = response.data.filter(m => m.source === 'online_train').slice(0, 6)
      availableModels.value = response.data
    }
  } catch (error) {
    console.error('加载训练模型列表失败', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/train-task/list', {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value
      }
    })
    if (response.code === '200') {
      tableData.value = response.data.records
      total.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const loadPredictResults = async () => {
  loadingResults.value = true
  try {
    const response = await request.get('/api/predict/results')
    if (response.code === '200') {
      const data = response.data
      if (data.files && Array.isArray(data.files)) {
        predictResults.value = data.files.map((fileName, index) => ({
          fileName,
          size: data.sizes ? data.sizes[index] : 0,
          lastModified: data.times ? data.times[index] : 0
        }))
      }
    }
  } catch (error) {
    console.error('加载预测结果列表失败', error)
  } finally {
    loadingResults.value = false
  }
}

const scanFiles = async () => {
  scanning.value = true
  try {
    const response = await request.post('/api/dataset/scan')
    if (response.code === '200') {
      const data = response.data
      ElMessage.success(`扫描完成：新增 ${data.newFiles}，更新 ${data.updatedFiles}，共 ${data.totalFiles} 个文件`)
      loadFileList()
    } else {
      ElMessage.error(response.msg || '扫描失败')
    }
  } catch (error) {
    ElMessage.error('扫描出错')
  } finally {
    scanning.value = false
  }
}

const scanTestFiles = async () => {
  scanningTestFiles.value = true
  try {
    const response = await request.post('/api/dataset/scan-test')
    if (response.code === '200') {
      const data = response.data
      ElMessage.success(`扫描完成：新增 ${data.newFiles}，更新 ${data.updatedFiles}，共 ${data.totalFiles} 个文件`)
      loadTestFiles()
    } else {
      ElMessage.error(response.msg || '扫描失败')
    }
  } catch (error) {
    ElMessage.error('扫描出错')
  } finally {
    scanningTestFiles.value = false
  }
}

const startTask = async () => {
  if (!taskForm.value.trainFileId) {
    ElMessage.warning('请选择训练文件')
    return
  }
  if (!taskForm.value.modelName.trim()) {
    ElMessage.warning('请输入模型名称')
    return
  }

  submitting.value = true
  try {
    const response = await request.post('/api/train-task/start', taskForm.value)
    if (response.code === '200') {
      ElMessage.success('训练任务已启动')
      loadData()
      taskForm.value.modelName = ''
    } else {
      ElMessage.error(response.msg || '启动失败')
    }
  } catch (error) {
    ElMessage.error('启动失败')
  } finally {
    submitting.value = false
  }
}

const startPredict = async () => {
  if (!predictForm.value.testFilePath) {
    ElMessage.warning('请选择测试数据文件')
    return
  }
  if (!predictForm.value.modelPath) {
    ElMessage.warning('请选择预测模型')
    return
  }

  predicting.value = true
  try {
    const response = await request.post('/api/predict/batch', predictForm.value)
    if (response.code === '200') {
      ElMessage.success('预测完成')
      loadPredictResults()
      resetPredictForm()
    } else {
      ElMessage.error(response.msg || '预测失败')
    }
  } catch (error) {
    ElMessage.error('预测执行失败')
  } finally {
    predicting.value = false
  }
}

const resetPredictForm = () => {
  predictForm.value = {
    testFilePath: '',
    modelPath: '',
    title: '',
    jsonName: ''
  }
}

const viewDetail = (row) => {
  currentTask.value = row
  detailVisible.value = true
}

const viewPredictResult = async (row) => {
  try {
    const response = await request.get(`/api/predict/result/${row.fileName}`)
    if (response.code === '200') {
      currentPredictResult.value = response.data
      predictResultVisible.value = true
    } else {
      ElMessage.error(response.msg || '获取结果失败')
    }
  } catch (error) {
    ElMessage.error('获取结果失败')
  }
}

const downloadResult = (row) => {
  const url = `/api/predict/result/${row.fileName}`
  request.get(url, { responseType: 'blob' }).then(res => {
    let blobData = null
    if (res.data instanceof Blob) {
      blobData = res.data
    } else if (res instanceof Blob) {
      blobData = res
    } else {
      ElMessage.error('下载失败')
      return
    }

    const link = document.createElement('a')
    link.href = window.URL.createObjectURL(blobData)
    link.download = row.fileName
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(link.href)
    ElMessage.success('下载成功')
  }).catch(error => {
    console.error('下载异常:', error)
    ElMessage.error('下载失败')
  })
}

const retryTask = async (row) => {
  try {
    const response = await request.post(`/api/train-task/${row.id}/retry`)
    if (response.code === '200') {
      ElMessage.success('任务已重新启动')
      loadData()
    } else {
      ElMessage.error(response.msg || '重试失败')
    }
  } catch (error) {
    ElMessage.error('重试失败')
  }
}

const deleteTask = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该训练任务吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await request.delete(`/api/train-task/${row.id}`)
    if (response.code === '200') {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const getStatusTagType = (status) => {
  const map = {
    'pending': 'info',
    'running': 'primary',
    'completed': 'success',
    'failed': 'danger'
  }
  return map[status] || 'default'
}

const getStatusText = (status) => {
  const map = {
    'pending': '等待中',
    'running': '运行中',
    'completed': '已完成',
    'failed': '失败'
  }
  return map[status] || status
}

const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  let size = bytes
  let unitIndex = 0
  while (size >= 1024 && unitIndex < units.length - 1) {
    size /= 1024
    unitIndex++
  }
  return `${size.toFixed(2)} ${units[unitIndex]}`
}

const formatTime = (timestamp) => {
  if (!timestamp) return '-'
  const date = new Date(timestamp)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

onMounted(() => {
  loadFileList()
  loadTestFiles()
  loadPythonScripts()
  loadTrainedModels()
  loadData()
  loadPredictResults()
})
</script>

<style scoped>
.online-training {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.page-desc {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.main-tabs {
  margin-bottom: 20px;
}

.create-section,
.predict-section {
  margin-bottom: 20px;
}

.table-section,
.result-section {
  margin-bottom: 20px;
}

.pagination-box {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.metrics {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.metric-item {
  font-size: 12px;
  color: #606266;
}

.error-message {
  color: #f56c6c;
  max-width: 400px;
  word-break: break-all;
}

.predict-progress {
  margin-bottom: 20px;
}

.progress-content {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px;
  font-size: 16px;
  color: #409eff;
}

.rotating {
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.predict-result-detail {
  padding: 10px;
}

.result-content {
  margin-top: 20px;
}

.result-content h4 {
  margin-bottom: 10px;
  color: #303133;
}

.json-content {
  background-color: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 15px;
  font-family: 'Courier New', Courier, monospace;
  font-size: 12px;
  line-height: 1.5;
  max-height: 400px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
