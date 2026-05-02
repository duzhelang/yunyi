<template>
  <div class="online-training">
    <div class="page-header">
      <div class="title-section">
        <h2 class="page-title">
          <el-icon><Cpu /></el-icon>
          在线模型训练
        </h2>
        <p class="page-desc">训练任务启动器和历史记录看板</p>
      </div>
    </div>

    <div class="create-section">
      <el-card>
        <template #header>
          <div class="card-header">
            <span class="card-title">
              <el-icon><Plus /></el-icon>
              新建训练任务
            </span>
          </div>
        </template>
        
        <el-form :model="taskForm" label-width="120px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="训练文件">
                <el-select v-model="taskForm.trainFileId" placeholder="请选择训练文件" style="width: 100%">
                  <el-option
                    v-for="file in fileList"
                    :key="file.id"
                    :label="file.name + ' (' + (file.category === 'train' ? '训练集' : '测试集') + ')'"
                    :value="file.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="选择模型">
                <el-select v-model="taskForm.selectedModel" placeholder="请选择模型" style="width: 100%">
                  <el-option label="train.py (新建模型)" value="new_model" />
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

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="模型名称">
                <el-input v-model="taskForm.modelName" placeholder="例如: diabetes_model_v2" />
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Cpu, Plus, List, VideoPlay, Refresh } from '@element-plus/icons-vue'
import request from '@/utils/request'

const taskForm = ref({
  trainFileId: null,
  selectedModel: 'new_model',
  modelName: '',
  hyperParams: {
    learningRate: 0.001,
    epochs: 100,
    batchSize: 32
  }
})

const fileList = ref([])
const trainedModels = ref([])
const tableData = ref([])
const loading = ref(false)
const submitting = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailVisible = ref(false)
const currentTask = ref(null)

const loadFileList = async () => {
  try {
    const response = await request.get('/api/dataset/all')
    if (response.code === 200) {
      fileList.value = response.data
    }
  } catch (error) {
    console.error('加载训练文件列表失败', error)
  }
}

const loadTrainedModels = async () => {
  try {
    const response = await request.get('/api/model/all')
    if (response.code === 200) {
      trainedModels.value = response.data.filter(m => m.source === 'online_train').slice(0, 6)
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
    if (response.code === 200) {
      tableData.value = response.data.records
      total.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
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
    if (response.code === 200) {
      ElMessage.success('训练任务已启动')
      loadData()
      taskForm.value.modelName = ''
    }
  } catch (error) {
    ElMessage.error('启动失败')
  } finally {
    submitting.value = false
  }
}

const viewDetail = (row) => {
  currentTask.value = row
  detailVisible.value = true
}

const retryTask = async (row) => {
  try {
    const response = await request.post(`/api/train-task/${row.id}/retry`)
    if (response.code === 200) {
      ElMessage.success('任务已重新启动')
      loadData()
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
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadData()
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

onMounted(() => {
  loadFileList()
  loadTrainedModels()
  loadData()
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

.create-section {
  margin-bottom: 20px;
}

.table-section {
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
</style>
