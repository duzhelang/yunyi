<template>
  <div class="dataset-management">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <el-icon class="title-icon"><FolderOpened /></el-icon>
          训练集管理
        </h1>
        <p class="page-desc">管理所有训练用CSV文件的数据资产</p>
      </div>
      <div class="header-stats">
        <div class="stat-card">
          <span class="stat-num">{{ fileList.length }}</span>
          <span class="stat-label">数据集总数</span>
        </div>
        <div class="stat-card">
          <span class="stat-num">{{ fileList.filter(f => f.category === 'train').length }}</span>
          <span class="stat-label">训练集</span>
        </div>
        <div class="stat-card">
          <span class="stat-num">{{ fileList.filter(f => f.category === 'test').length }}</span>
          <span class="stat-label">测试集</span>
        </div>
      </div>
    </div>

    <div class="main-card">
      <div class="card-header">
        <div class="search-bar">
          <el-input v-model="keyword" placeholder="输入文件名搜索" prefix-icon="Search" clearable
                    @keyup.enter="loadData" style="width: 240px" />
          <el-select v-model="category" placeholder="分类筛选" clearable style="width: 140px" @change="loadData">
            <el-option label="训练集" value="train" />
            <el-option label="测试集" value="test" />
          </el-select>
          <el-button type="primary" :icon="Search" @click="loadData">搜索</el-button>
        </div>
        <div class="action-bar">
          <el-button type="primary" plain :icon="Refresh" @click="scanFiles" :loading="scanning">扫描目录</el-button>
          <el-button type="primary" :icon="Upload" @click="uploadDialogVisible = true">上传数据集</el-button>
        </div>
      </div>

      <el-table :data="fileList" border stripe style="width: 100%"
                :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 600 }">
        <el-table-column label="文件名" prop="name" min-width="200" show-overflow-tooltip />
        <el-table-column label="分类" prop="category" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.category === 'train' ? 'success' : 'warning'" size="small">
              {{ row.category === 'train' ? '训练集' : row.category === 'test' ? '测试集' : row.category || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="质量" prop="qualityLevel" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="qualityTagType(row.qualityLevel)" size="small" effect="plain">
              {{ qualityLabel(row.qualityLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="样本量" prop="sampleCount" width="90" align="center">
          <template #default="{ row }">
            {{ row.sampleCount ? row.sampleCount.toLocaleString() : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="文件大小" prop="fileSize" width="100" align="center">
          <template #default="{ row }">
            {{ row.fileSize || formatSize(row.size) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isDelete ? 'danger' : 'success'" size="small">
              {{ row.isDelete ? '已删除' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="入库时间" width="170" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewFile(row)">详情</el-button>
            <el-popconfirm title="确定删除该数据集？" @confirm="deleteFile(row.id)" confirm-button-text="删除"
                           cancel-button-text="取消">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination background layout="total, sizes, prev, pager, next, jumper"
                       :total="total" :page-sizes="[10, 20, 50]" v-model:current-page="currentPage"
                       v-model:page-size="pageSize" @current-change="loadData" @size-change="loadData" />
      </div>
    </div>

    <el-dialog v-model="uploadDialogVisible" title="上传数据集" width="480px" destroy-on-close>
      <div class="upload-area">
        <el-upload
          ref="uploadRef"
          :auto-upload="false"
          :on-change="handleFileChange"
          :before-upload="beforeUpload"
          :limit="1"
          accept=".csv,.xlsx,.xls"
          drag
        >
          <el-icon class="el-icon--upload"><Upload /></el-icon>
          <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          <template #tip>
            <div class="el-upload__tip">支持 CSV、XLSX、XLS 格式，单文件最大 50MB</div>
          </template>
        </el-upload>
        <el-form label-width="80px" style="margin-top: 16px">
          <el-form-item label="数据分类">
            <el-radio-group v-model="uploadCategory">
              <el-radio value="train">训练集</el-radio>
              <el-radio value="test">测试集</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUpload" :loading="uploading" :disabled="!selectedFile">
          {{ uploading ? '上传中...' : '开始上传' }}
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="数据集详情" width="600px" destroy-on-close>
      <template v-if="currentFile">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="文件名" :span="2">{{ currentFile.name }}</el-descriptions-item>
          <el-descriptions-item label="分类">
            <el-tag :type="currentFile.category === 'train' ? 'success' : 'warning'" size="small">
              {{ currentFile.category === 'train' ? '训练集' : currentFile.category === 'test' ? '测试集' : currentFile.category || '-' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="质量等级">
            <el-tag :type="qualityTagType(currentFile.qualityLevel)" size="small" effect="plain">
              {{ qualityLabel(currentFile.qualityLevel) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="样本量">{{ currentFile.sampleCount ? currentFile.sampleCount.toLocaleString() : '-' }}</el-descriptions-item>
          <el-descriptions-item label="文件大小">{{ currentFile.fileSize || formatSize(currentFile.size) }}</el-descriptions-item>
          <el-descriptions-item label="存储路径" :span="2">
            <el-text type="info" size="small" style="word-break: break-all">{{ currentFile.url }}</el-text>
          </el-descriptions-item>
          <el-descriptions-item label="Python路径" :span="2">
            <el-text type="info" size="small" style="word-break: break-all">{{ currentFile.pythonurl || '-' }}</el-text>
          </el-descriptions-item>
          <el-descriptions-item label="入库时间" :span="2">{{ formatDate(currentFile.createTime) }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="currentFile.columnInfo" style="margin-top: 16px">
          <h4 style="margin-bottom: 8px; color: #303133">特征列信息</h4>
          <div class="columns-grid">
            <el-tag v-for="(col, idx) in parseColumnInfo(currentFile.columnInfo)" :key="idx"
                    type="info" size="small" style="margin: 2px">
              {{ col.name }}
            </el-tag>
          </div>
        </div>
        <div v-if="currentFile.remark" style="margin-top: 16px">
          <h4 style="margin-bottom: 8px; color: #303133">备注</h4>
          <el-text>{{ currentFile.remark }}</el-text>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Upload, Refresh, FolderOpened } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { serverIp } from '@/utils/request'

const fileList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const category = ref('')
const scanning = ref(false)
const uploadDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentFile = ref(null)
const selectedFile = ref(null)
const uploading = ref(false)
const uploadCategory = ref('train')

const qualityTagType = (level) => {
  const map = { raw: 'info', cleaned: 'success', verified: 'primary' }
  return map[level] || 'info'
}

const qualityLabel = (level) => {
  const map = { raw: '原始', cleaned: '已清洗', verified: '已验证' }
  return map[level] || '未知'
}

const formatSize = (bytes) => {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / 1048576).toFixed(1) + ' MB'
}

const loadData = () => {
  request.get('/api/dataset/list', {
    params: {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: keyword.value || undefined,
      category: category.value || undefined
    }
  }).then(res => {
    if (res.code === '200') {
      fileList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  })
}

const viewFile = (row) => {
  request.get(`/api/dataset/${row.id}`).then(res => {
    if (res.code === '200') {
      currentFile.value = res.data
      detailDialogVisible.value = true
    }
  })
}

const deleteFile = (id) => {
  request.delete(`/api/dataset/${id}`).then(res => {
    if (res.code === '200') {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  })
}

const scanFiles = () => {
  scanning.value = true
  request.post('/api/dataset/scan').then(res => {
    if (res.code === '200') {
      const data = res.data
      ElMessage.success(`扫描完成：新增 ${data.newFiles}，更新 ${data.updatedFiles}，共 ${data.totalFiles} 个文件`)
      loadData()
    } else {
      ElMessage.error(res.msg || '扫描失败')
    }
  }).finally(() => {
    scanning.value = false
  })
}

const beforeUpload = (file) => {
  const maxSize = 50 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过 50MB')
    return false
  }
  return true
}

const handleFileChange = (uploadFile) => {
  selectedFile.value = uploadFile.raw
}

const submitUpload = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择文件')
    return
  }
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    const url = `http://${serverIp}:9090/python/upload?name=datasets&pythonurl=diabetes_1.csv`
    const response = await fetch(url, { method: 'POST', body: formData })
    const result = await response.json()
    if (result.code === '200') {
      ElMessage.success('文件上传成功！')
      uploadDialogVisible.value = false
      selectedFile.value = null
      loadData()
    } else {
      ElMessage.error(result.msg || '文件上传失败')
    }
  } catch (err) {
    ElMessage.error('上传出错: ' + err.message)
  } finally {
    uploading.value = false
  }
}

const parseColumnInfo = (info) => {
  if (!info) return []
  try {
    return typeof info === 'string' ? JSON.parse(info) : info
  } catch { return [] }
}

const formatDate = (date) => {
  if (!date) return '-'
  const d = new Date(date)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.dataset-management {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 24px;
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  border-radius: 12px;
  color: #1a1a2e;
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
  opacity: 0.75;
  margin: 0;
}

.header-stats {
  display: flex;
  gap: 16px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.25);
  border-radius: 8px;
  padding: 12px 20px;
  text-align: center;
  backdrop-filter: blur(10px);
}

.stat-num {
  display: block;
  font-size: 28px;
  font-weight: 700;
}

.stat-label {
  font-size: 12px;
  opacity: 0.75;
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
  flex-wrap: wrap;
  gap: 12px;
}

.search-bar {
  display: flex;
  gap: 10px;
}

.action-bar {
  display: flex;
  gap: 10px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.upload-area {
  text-align: center;
}

.columns-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}
</style>
