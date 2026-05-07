<template>
  <div class="dataset-management">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <el-icon class="title-icon"><FolderOpened /></el-icon>
          数据集管理
        </h1>
        <p class="page-desc">统一管理训练集、测试集与预测结果数据</p>
      </div>
      <div class="header-stats">
        <div class="stat-card">
          <span class="stat-num">{{ stats.dataFileCount + stats.jsonCount }}</span>
          <span class="stat-label">数据集总数</span>
        </div>
        <div class="stat-card stat-train">
          <span class="stat-num">{{ stats.trainCount }}</span>
          <span class="stat-label">训练集</span>
        </div>
        <div class="stat-card stat-test">
          <span class="stat-num">{{ stats.testCount }}</span>
          <span class="stat-label">测试集</span>
        </div>
        <div class="stat-card stat-json">
          <span class="stat-num">{{ stats.jsonCount }}</span>
          <span class="stat-label">预测结果</span>
        </div>
      </div>
    </div>

    <el-tabs v-model="activeTab" class="dataset-tabs" @tab-change="handleTabChange">
      <el-tab-pane name="train">
        <template #label>
          <span class="tab-label"><el-icon><Document /></el-icon>训练集管理</span>
        </template>

        <div class="tab-toolbar">
          <div class="search-bar">
            <el-input v-model="keyword" placeholder="搜索文件名" prefix-icon="Search" clearable
                      @keyup.enter="loadTrainData" style="width: 240px" />
            <el-button type="primary" :icon="Search" @click="loadTrainData">搜索</el-button>
          </div>
          <div class="action-bar">
            <el-button v-if="trainSelectedIds.length > 0" type="danger" plain @click="batchDeleteFiles('train')">
              批量删除 ({{ trainSelectedIds.length }})
            </el-button>
            <el-button plain :icon="Refresh" @click="scanDirectory('train')" :loading="scanning">扫描 data/train</el-button>
            <el-button type="primary" :icon="Upload" @click="openUpload('train')">上传训练集</el-button>
          </div>
        </div>

        <el-table :data="trainList" border stripe v-loading="loadingTrain" @selection-change="handleTrainSelectionChange"
                  :header-cell-style="{ background: '#f0f9eb', color: '#529b2e', fontWeight: 600 }">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="文件名" prop="name" min-width="220" show-overflow-tooltip />
          <el-table-column label="分类" width="90" align="center">
            <template #default>
              <el-tag type="success" size="small">训练集</el-tag>
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
          <el-table-column label="文件大小" width="100" align="center">
            <template #default="{ row }">
              {{ row.fileSize || formatSize(row.size) }}
            </template>
          </el-table-column>
          <el-table-column label="入库时间" prop="createTime" width="170" align="center">
            <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="180" align="center" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="viewFile(row)">详情</el-button>
              <el-popconfirm title="确定删除该训练集？" @confirm="deleteFile(row.id)"
                             confirm-button-text="删除" cancel-button-text="取消">
                <template #reference>
                  <el-button size="small" type="danger">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrap">
          <el-pagination background layout="total, sizes, prev, pager, next, jumper"
                         :total="trainTotal" :page-sizes="[10, 20, 50]"
                         v-model:current-page="trainPage" v-model:page-size="trainPageSize"
                         @current-change="loadTrainData" @size-change="loadTrainData" />
        </div>
      </el-tab-pane>

      <el-tab-pane name="test">
        <template #label>
          <span class="tab-label"><el-icon><DataBoard /></el-icon>测试集管理</span>
        </template>

        <div class="tab-toolbar">
          <div class="search-bar">
            <el-input v-model="testKeyword" placeholder="搜索文件名" prefix-icon="Search" clearable
                      @keyup.enter="loadTestData" style="width: 240px" />
            <el-button type="primary" :icon="Search" @click="loadTestData">搜索</el-button>
          </div>
          <div class="action-bar">
            <el-button v-if="testSelectedIds.length > 0" type="danger" plain @click="batchDeleteFiles('test')">
              批量删除 ({{ testSelectedIds.length }})
            </el-button>
            <el-button plain :icon="Refresh" @click="scanDirectory('test')" :loading="scanning">扫描 data/test</el-button>
            <el-button type="primary" :icon="Upload" @click="openUpload('test')">上传测试集</el-button>
          </div>
        </div>

        <el-table :data="testList" border stripe v-loading="loadingTest" @selection-change="handleTestSelectionChange"
                  :header-cell-style="{ background: '#fdf6ec', color: '#b88230', fontWeight: 600 }">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="文件名" prop="name" min-width="220" show-overflow-tooltip />
          <el-table-column label="分类" width="90" align="center">
            <template #default>
              <el-tag type="warning" size="small">测试集</el-tag>
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
          <el-table-column label="文件大小" width="100" align="center">
            <template #default="{ row }">
              {{ row.fileSize || formatSize(row.size) }}
            </template>
          </el-table-column>
          <el-table-column label="入库时间" prop="createTime" width="170" align="center">
            <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="180" align="center" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="viewFile(row)">详情</el-button>
              <el-popconfirm title="确定删除该测试集？" @confirm="deleteFile(row.id)"
                             confirm-button-text="删除" cancel-button-text="取消">
                <template #reference>
                  <el-button size="small" type="danger">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrap">
          <el-pagination background layout="total, sizes, prev, pager, next, jumper"
                         :total="testTotal" :page-sizes="[10, 20, 50]"
                         v-model:current-page="testPage" v-model:page-size="testPageSize"
                         @current-change="loadTestData" @size-change="loadTestData" />
        </div>
      </el-tab-pane>

      <el-tab-pane name="json">
        <template #label>
          <span class="tab-label"><el-icon><TrendCharts /></el-icon>预测结果</span>
        </template>

        <div class="tab-toolbar">
          <div class="search-bar">
            <el-input v-model="jsonKeyword" placeholder="搜索结果文件名" prefix-icon="Search" clearable
                      @keyup.enter="loadJsonFiles" style="width: 240px" />
            <el-button type="primary" :icon="Search" @click="loadJsonFiles">搜索</el-button>
          </div>
          <div class="action-bar">
            <el-button plain :icon="Refresh" @click="loadJsonFiles" :loading="loadingJson">刷新 data/json</el-button>
          </div>
        </div>

        <el-table :data="jsonFileList" border stripe v-loading="loadingJson"
                  :header-cell-style="{ background: '#ecf5ff', color: '#409eff', fontWeight: 600 }">
          <el-table-column label="文件名" prop="fileName" min-width="260" show-overflow-tooltip />
          <el-table-column label="文件大小" prop="fileSize" width="120" align="center" />
          <el-table-column label="生成时间" width="180" align="center">
            <template #default="{ row }">{{ formatDate(row.lastModified) }}</template>
          </el-table-column>
          <el-table-column label="状态" width="90" align="center">
            <template #default>
              <el-tag type="success" size="small">已完成</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="260" align="center" fixed="right">
            <template #default="{ row }">
              <el-button size="small" type="primary" @click="previewJson(row)">预览</el-button>
              <el-button size="small" type="success" @click="downloadJson(row)">下载</el-button>
              <el-popconfirm title="确定删除该预测结果？" @confirm="deleteJsonFile(row.fileName)"
                             confirm-button-text="删除" cancel-button-text="取消">
                <template #reference>
                  <el-button size="small" type="danger">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>

        <div v-if="jsonFileList.length === 0 && !loadingJson" class="empty-tip">
          <el-empty description="暂无预测结果，可在「在线模型训练」页面执行预测生成" />
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="uploadDialogVisible" :title="uploadDialogTitle" width="480px" destroy-on-close>
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
              {{ currentFile.category === 'train' ? '训练集' : '测试集' }}
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
      </template>
    </el-dialog>

    <el-dialog v-model="jsonPreviewVisible" title="预测结果预览" width="760px" destroy-on-close>
      <template v-if="currentJsonFile">
        <el-descriptions :column="2" border style="margin-bottom: 16px">
          <el-descriptions-item label="文件名">{{ currentJsonFile.fileName }}</el-descriptions-item>
          <el-descriptions-item label="文件大小">{{ currentJsonFile.fileSize }}</el-descriptions-item>
          <el-descriptions-item label="生成时间" :span="2">{{ formatDate(currentJsonFile.lastModified) }}</el-descriptions-item>
        </el-descriptions>
        <div class="json-preview-box">
          <pre class="json-content">{{ jsonPreviewContent }}</pre>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Upload, Refresh, FolderOpened, Document, DataBoard, TrendCharts } from '@element-plus/icons-vue'
import request from '@/utils/request'

const activeTab = ref('train')

const stats = reactive({
  dataFileCount: 0,
  trainCount: 0,
  testCount: 0,
  jsonCount: 0
})

const trainList = ref([])
const trainTotal = ref(0)
const trainPage = ref(1)
const trainPageSize = ref(10)
const keyword = ref('')
const loadingTrain = ref(false)

const testList = ref([])
const testTotal = ref(0)
const testPage = ref(1)
const testPageSize = ref(10)
const testKeyword = ref('')
const loadingTest = ref(false)

const jsonFileList = ref([])
const jsonKeyword = ref('')
const loadingJson = ref(false)

const scanning = ref(false)
const trainSelectedIds = ref([])
const testSelectedIds = ref([])
const uploadDialogVisible = ref(false)
const uploadDialogTitle = ref('上传训练集')
const detailDialogVisible = ref(false)
const currentFile = ref(null)
const selectedFile = ref(null)
const uploading = ref(false)
const uploadCategory = ref('train')

const jsonPreviewVisible = ref(false)
const currentJsonFile = ref(null)
const jsonPreviewContent = ref('')

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

const formatDate = (date) => {
  if (!date) return '-'
  const d = new Date(date)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

const parseColumnInfo = (info) => {
  if (!info) return []
  try {
    return typeof info === 'string' ? JSON.parse(info) : info
  } catch { return [] }
}

const loadStats = () => {
  request.get('/api/dataset/stats').then(res => {
    if (res.code === '200') {
      Object.assign(stats, res.data)
    }
  })
}

const handleTabChange = (name) => {
  if (name === 'train' && trainList.value.length === 0) loadTrainData()
  if (name === 'test' && testList.value.length === 0) loadTestData()
  if (name === 'json' && jsonFileList.value.length === 0) loadJsonFiles()
}

const loadTrainData = () => {
  loadingTrain.value = true
  request.get('/api/dataset/list', {
    params: {
      pageNum: trainPage.value,
      pageSize: trainPageSize.value,
      keyword: keyword.value || undefined,
      category: 'train'
    }
  }).then(res => {
    if (res.code === '200') {
      trainList.value = res.data.records || []
      trainTotal.value = res.data.total || 0
    }
  }).finally(() => { loadingTrain.value = false })
}

const loadTestData = () => {
  loadingTest.value = true
  request.get('/api/dataset/list', {
    params: {
      pageNum: testPage.value,
      pageSize: testPageSize.value,
      keyword: testKeyword.value || undefined,
      category: 'test'
    }
  }).then(res => {
    if (res.code === '200') {
      testList.value = res.data.records || []
      testTotal.value = res.data.total || 0
    }
  }).finally(() => { loadingTest.value = false })
}

const loadJsonFiles = () => {
  loadingJson.value = true
  request.get('/api/dataset/json-files', {
    params: { keyword: jsonKeyword.value || undefined }
  }).then(res => {
    if (res.code === '200') {
      jsonFileList.value = res.data.files || []
    }
  }).finally(() => { loadingJson.value = false })
}

const openUpload = (cat) => {
  uploadCategory.value = cat
  uploadDialogTitle.value = cat === 'train' ? '上传训练集' : '上传测试集'
  selectedFile.value = null
  uploadDialogVisible.value = true
}

const scanDirectory = (type) => {
  scanning.value = true
  const url = type === 'test' ? '/api/dataset/scan-test' : '/api/dataset/scan'
  request.post(url).then(res => {
    if (res.code === '200') {
      const data = res.data
      ElMessage.success(`扫描完成：新增 ${data.newFiles}，更新 ${data.updatedFiles}，共 ${data.totalFiles} 个文件`)
      if (type === 'test') loadTestData()
      else loadTrainData()
      loadStats()
    } else {
      ElMessage.error(res.msg || '扫描失败')
    }
  }).finally(() => { scanning.value = false })
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
      loadTrainData()
      loadTestData()
      loadStats()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  })
}

const handleTrainSelectionChange = (rows) => {
  trainSelectedIds.value = rows.map(r => r.id)
}
const handleTestSelectionChange = (rows) => {
  testSelectedIds.value = rows.map(r => r.id)
}

const batchDeleteFiles = (type) => {
  const ids = type === 'train' ? trainSelectedIds.value : testSelectedIds.value
  if (ids.length === 0) {
    ElMessage.warning('请先选择要删除的数据集')
    return
  }
  ElMessageBox.confirm(`确定要删除选中的 ${ids.length} 个数据集吗？`, '批量删除确认', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    request.delete('/api/dataset/batch', { data: ids }).then(res => {
      if (res.code === '200') {
        ElMessage.success(`成功删除 ${ids.length} 个数据集`)
        if (type === 'train') trainSelectedIds.value = []
        else testSelectedIds.value = []
        loadTrainData()
        loadTestData()
        loadStats()
      } else {
        ElMessage.error(res.msg || '批量删除失败')
      }
    })
  }).catch(() => {})
}

const beforeUpload = (file) => {
  if (file.size > 50 * 1024 * 1024) {
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
    formData.append('category', uploadCategory.value)
    const res = await request.post('/api/dataset/upload', formData)
    if (res.code === '200') {
      ElMessage.success('文件上传成功！')
      uploadDialogVisible.value = false
      selectedFile.value = null
      if (uploadCategory.value === 'test') loadTestData()
      else loadTrainData()
      loadStats()
    } else {
      ElMessage.error(res.msg || '文件上传失败')
    }
  } catch (err) {
    ElMessage.error('上传出错: ' + err.message)
  } finally {
    uploading.value = false
  }
}

const previewJson = (row) => {
  request.get(`/api/dataset/json-files/${row.fileName}`).then(res => {
    if (res.code === '200') {
      currentJsonFile.value = res.data
      try {
        jsonPreviewContent.value = JSON.stringify(JSON.parse(res.data.content), null, 2)
      } catch {
        jsonPreviewContent.value = res.data.content
      }
      jsonPreviewVisible.value = true
    } else {
      ElMessage.error(res.msg || '预览失败')
    }
  })
}

const downloadJson = (row) => {
  request.get(`/api/dataset/json-files/${row.fileName}`, { responseType: 'blob' }).then(res => {
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
  }).catch(() => {
    ElMessage.error('下载失败')
  })
}

const deleteJsonFile = (fileName) => {
  request.delete(`/api/dataset/json-files/${fileName}`).then(res => {
    if (res.code === '200') {
      ElMessage.success('删除成功')
      loadJsonFiles()
      loadStats()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  })
}

onMounted(async () => {
  loadStats()
  await loadTrainData()
  if (trainList.value.length === 0) {
    await request.post('/api/dataset/scan')
    await loadTrainData()
    loadStats()
  }
  await loadTestData()
  if (testList.value.length === 0) {
    await request.post('/api/dataset/scan-test')
    await loadTestData()
    loadStats()
  }
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
  min-width: 80px;
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

.stat-train .stat-num { color: #1a6b2a; }
.stat-test .stat-num  { color: #8a6d1b; }
.stat-json .stat-num  { color: #1555b0; }

.dataset-tabs {
  background: #fff;
  border-radius: 12px;
  padding: 20px 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
}

.tab-toolbar {
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

.empty-tip {
  padding: 40px 0;
}

.json-preview-box {
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  max-height: 500px;
  overflow-y: auto;
}

.json-content {
  margin: 0;
  font-family: 'Courier New', Courier, monospace;
  font-size: 12px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
