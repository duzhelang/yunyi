<template>
  <div class="model-management">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <el-icon class="title-icon"><Cpu /></el-icon>
          模型管理
        </h1>
        <p class="page-desc">管理所有AI模型文件的版本、部署与调用</p>
      </div>
      <div class="header-stats">
        <div class="stat-card">
          <span class="stat-num">{{ modelList.length }}</span>
          <span class="stat-label">模型总数</span>
        </div>
        <div class="stat-card active">
          <span class="stat-num">{{ activeModel ? 1 : 0 }}</span>
          <span class="stat-label">已激活</span>
        </div>
      </div>
    </div>

    <div class="main-card">
      <div class="card-header">
        <div class="search-bar">
          <el-input v-model="modelName" placeholder="输入模型名称搜索" prefix-icon="Search" clearable
                    @keyup.enter="loadModels" style="width: 240px" />
          <el-button type="primary" :icon="Search" @click="loadModels">搜索</el-button>
        </div>
        <el-button type="primary" :icon="Plus" @click="addModelDialogVisible = true">注册新模型</el-button>
      </div>

      <el-table :data="filteredModels" border stripe style="width: 100%"
                :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 600 }">
        <el-table-column label="模型名称" prop="modelName" min-width="140" show-overflow-tooltip />
        <el-table-column label="版本" prop="version" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ row.version || 'v1.0' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="来源" prop="source" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="row.source === 'online_train' ? 'success' : 'info'" size="small">
              {{ row.source === 'online_train' ? '在线训练' : '手动上传' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'" size="small" effect="dark">
              {{ row.status === 'active' ? '已激活' : '未激活' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="描述" prop="description" min-width="180" show-overflow-tooltip />
        <el-table-column label="创建时间" prop="createTime" width="170" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewModel(row)">详情</el-button>
            <el-button v-if="row.status === 'active'" size="small" type="warning"
                       @click="deactivateModel(row)">
              取消激活
            </el-button>
            <el-button v-else size="small" type="success"
                       @click="activateModel(row)">
              激活
            </el-button>
            <el-popconfirm title="确定删除该模型？" @confirm="deleteModel(row.id)" confirm-button-text="删除"
                           cancel-button-text="取消" :disabled="row.status === 'active'">
              <template #reference>
                <el-button size="small" type="danger" :disabled="row.status === 'active'">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination background layout="total, sizes, prev, pager, next, jumper"
                       :total="total" :page-sizes="[10, 20, 50]" v-model:current-page="pageNum"
                       v-model:page-size="pageSize" @current-change="loadModels" @size-change="loadModels" />
      </div>
    </div>

    <el-dialog v-model="addModelDialogVisible" title="注册新模型" width="520px" destroy-on-close>
      <el-form :model="newModel" label-width="100px">
        <el-form-item label="模型名称" required>
          <el-input v-model="newModel.modelName" placeholder="如 diabetes_model" />
        </el-form-item>
        <el-form-item label="版本号" required>
          <el-input v-model="newModel.version" placeholder="如 v2.0.0" />
        </el-form-item>
        <el-form-item label=".pth 路径" required>
          <el-input v-model="newModel.filePath" placeholder="如 data/models/pth_models/diabetes_model.pth" />
        </el-form-item>
        <el-form-item label="Scaler 路径">
          <el-input v-model="newModel.scalerPath" placeholder="如 data/models/pkl_files/diabetes_model_scaler.pkl" />
        </el-form-item>
        <el-form-item label="Encoder 路径">
          <el-input v-model="newModel.encoderPath" placeholder="如 data/models/pkl_files/diabetes_model_encoder.pkl" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="newModel.description" type="textarea" :rows="3" placeholder="模型用途、训练数据说明等" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addModelDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAddModel">确认注册</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="模型详情" width="560px" destroy-on-close>
      <template v-if="currentModel">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="模型名称">{{ currentModel.modelName }}</el-descriptions-item>
          <el-descriptions-item label="版本">{{ currentModel.version }}</el-descriptions-item>
          <el-descriptions-item label="来源">
            <el-tag :type="currentModel.source === 'online_train' ? 'success' : 'info'" size="small">
              {{ currentModel.source === 'online_train' ? '在线训练' : '手动上传' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentModel.status === 'active' ? 'success' : 'info'" size="small" effect="dark">
              {{ currentModel.status === 'active' ? '已激活' : '未激活' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label=".pth 路径" :span="2">{{ currentModel.filePath }}</el-descriptions-item>
          <el-descriptions-item label="Scaler 路径" :span="2">{{ currentModel.scalerPath || '-' }}</el-descriptions-item>
          <el-descriptions-item label="Encoder 路径" :span="2">{{ currentModel.encoderPath || '-' }}</el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">{{ currentModel.description || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">{{ formatDate(currentModel.createTime) }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="currentModel.metrics" style="margin-top: 16px">
          <h4 style="margin-bottom: 8px; color: #303133">性能指标</h4>
          <div class="metrics-grid">
            <div class="metric-item" v-for="(val, key) in parseMetrics(currentModel.metrics)" :key="key">
              <span class="metric-label">{{ key }}</span>
              <span class="metric-value">{{ typeof val === 'number' ? val.toFixed(4) : val }}</span>
            </div>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Plus, Cpu } from '@element-plus/icons-vue'
import request from '@/utils/request'

const modelList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const modelName = ref('')
const addModelDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentModel = ref(null)

const newModel = ref({
  modelName: '',
  version: '',
  filePath: '',
  scalerPath: '',
  encoderPath: '',
  description: ''
})

const activeModel = computed(() => modelList.value.find(m => m.status === 'active'))

const filteredModels = computed(() => modelList.value)

const loadModels = () => {
  request.get('/api/model/list', {
    params: { pageNum: pageNum.value, pageSize: pageSize.value, modelName: modelName.value || undefined }
  }).then(res => {
    if (res.code === '200') {
      modelList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  })
}

const submitAddModel = () => {
  if (!newModel.value.modelName || !newModel.value.version || !newModel.value.filePath) {
    ElMessage.warning('请填写模型名称、版本号和文件路径')
    return
  }
  request.post('/api/model/add', newModel.value).then(res => {
    if (res.code === '200') {
      ElMessage.success('注册成功')
      addModelDialogVisible.value = false
      newModel.value = { modelName: '', version: '', filePath: '', scalerPath: '', encoderPath: '', description: '' }
      loadModels()
    } else {
      ElMessage.error(res.msg || '注册失败')
    }
  })
}

const activateModel = (row) => {
  request.post(`/api/model/${row.id}/activate`).then(res => {
    if (res.code === '200') {
      ElMessage.success('激活成功')
      loadModels()
    } else {
      ElMessage.error(res.msg || '激活失败')
    }
  })
}

const deactivateModel = (row) => {
  request.post(`/api/model/${row.id}/deactivate`).then(res => {
    if (res.code === '200') {
      ElMessage.success('已取消激活')
      loadModels()
    } else {
      ElMessage.error(res.msg || '取消激活失败')
    }
  })
}

const deleteModel = (id) => {
  request.delete(`/api/model/${id}`).then(res => {
    if (res.code === '200') {
      ElMessage.success('删除成功')
      loadModels()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  })
}

const viewModel = (row) => {
  request.get(`/api/model/${row.id}`).then(res => {
    if (res.code === '200') {
      currentModel.value = res.data
      detailDialogVisible.value = true
    }
  })
}

const parseMetrics = (metrics) => {
  if (!metrics) return {}
  try {
    return typeof metrics === 'string' ? JSON.parse(metrics) : metrics
  } catch { return {} }
}

const formatDate = (date) => {
  if (!date) return '-'
  const d = new Date(date)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

onMounted(() => {
  loadModels()
})
</script>

<style scoped>
.model-management {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: #fff;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 6px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-icon {
  font-size: 28px;
}

.page-desc {
  font-size: 14px;
  opacity: 0.85;
  margin: 0;
}

.header-stats {
  display: flex;
  gap: 16px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 8px;
  padding: 12px 20px;
  text-align: center;
  backdrop-filter: blur(10px);
}

.stat-card.active {
  background: rgba(255, 255, 255, 0.25);
}

.stat-num {
  display: block;
  font-size: 28px;
  font-weight: 700;
}

.stat-label {
  font-size: 12px;
  opacity: 0.8;
}

.main-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-bar {
  display: flex;
  gap: 10px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 12px;
}

.metric-item {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 12px;
  text-align: center;
}

.metric-label {
  display: block;
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.metric-value {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
</style>
