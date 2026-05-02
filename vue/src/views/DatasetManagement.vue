<template>
  <div class="dataset-management">
    <div class="page-header">
      <div class="title-section">
        <h2 class="page-title">
          <el-icon><FolderOpened /></el-icon>
          训练集管理
        </h2>
        <p class="page-desc">数据资产中心，管理所有CSV训练数据</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" :icon="Refresh" @click="scanFiles">扫描数据目录</el-button>
        <el-button type="success" :icon="Upload" @click="uploadVisible = true">上传训练集</el-button>
      </div>
    </div>

    <!-- 上传训练集弹窗 -->
    <el-dialog v-model="uploadVisible" title="上传训练集" width="650px" destroy-on-close>
      <div class="upload-container">
        <el-upload
          :action="uploadUrl"
          :on-success="onUploadSuccess"
          :on-error="onUploadError"
          :before-upload="beforeUpload"
          drag
          multiple
          name="file"
          accept=".csv,.xlsx,.xls"
          class="upload-demo"
        >
          <el-icon class="el-icon-upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">将训练集拖到此处，或<em>点击上传</em></div>
          <div class="el-upload__tip" slot="tip">支持 CSV、XLSX、XLS 格式，单个文件不超过 50MB</div>
        </el-upload>
      </div>
    </el-dialog>

    <div class="search-section">
      <el-card>
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索训练集名称"
            clearable
            style="width: 300px"
            @keyup.enter="loadData"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="loadData">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </div>
      </el-card>
    </div>

    <div class="table-section">
      <el-card>
        <el-tabs v-model="activeCategory" @tab-change="loadData">
          <el-tab-pane label="全部" name="all" />
          <el-tab-pane label="训练集" name="train">
            <template #label>
              <span>训练集</span>
              <el-badge :value="trainCount" :hidden="trainCount === 0" type="success" style="margin-left: 4px" />
            </template>
          </el-tab-pane>
          <el-tab-pane label="测试集" name="test">
            <template #label>
              <span>测试集</span>
              <el-badge :value="testCount" :hidden="testCount === 0" type="warning" style="margin-left: 4px" />
            </template>
          </el-tab-pane>
        </el-tabs>

        <el-table :data="tableData" style="width: 100%" v-loading="loading" stripe border>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="文件名" min-width="200" />
          <el-table-column prop="category" label="分类" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.category === 'train' ? 'success' : 'warning'">
                {{ scope.row.category === 'train' ? '训练集' : '测试集' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="type" label="文件类型" width="100">
            <template #default="scope">
              <el-tag :type="getFileTagType(scope.row.type)">{{ scope.row.type.toUpperCase() }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="size" label="文件大小" width="120">
            <template #default="scope">
              {{ formatFileSize(scope.row.size) }}
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column prop="remark" label="备注" min-width="250" show-overflow-tooltip />
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="viewDetail(scope.row)">详情</el-button>
              <el-button type="danger" size="small" @click="deleteDataset(scope.row)">删除</el-button>
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

    <el-dialog v-model="detailVisible" title="训练集详情" width="500px">
      <el-descriptions v-if="currentDataset" :column="1" border>
        <el-descriptions-item label="ID">{{ currentDataset.id }}</el-descriptions-item>
        <el-descriptions-item label="文件名">{{ currentDataset.name }}</el-descriptions-item>
        <el-descriptions-item label="分类">
          <el-tag :type="currentDataset.category === 'train' ? 'success' : 'warning'">
            {{ currentDataset.category === 'train' ? '训练集' : '测试集' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="文件类型">{{ currentDataset.type }}</el-descriptions-item>
        <el-descriptions-item label="文件大小">{{ formatFileSize(currentDataset.size) }}</el-descriptions-item>
        <el-descriptions-item label="文件路径">{{ currentDataset.url }}</el-descriptions-item>
        <el-descriptions-item label="Python路径">{{ currentDataset.pythonurl }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentDataset.createTime }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentDataset.remark }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { FolderOpened, Refresh, Search, Upload, UploadFilled } from '@element-plus/icons-vue'
import request from '@/utils/request'

const serverIp = window.config ? window.config.serverIp : 'localhost'

const searchKeyword = ref('')
const tableData = ref([])
const allData = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailVisible = ref(false)
const currentDataset = ref(null)
const activeCategory = ref('all')
const uploadVisible = ref(false)

const uploadUrl = computed(() => `http://${serverIp}:9090/python/upload`)

const trainCount = computed(() => allData.value.filter(item => item.category === 'train').length)
const testCount = computed(() => allData.value.filter(item => item.category === 'test').length)

const loadData = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/dataset/list', {
      params: {
        pageNum: 1,
        pageSize: 1000,
        keyword: searchKeyword.value
      }
    })
    if (response.code === 200) {
      allData.value = response.data.records
      let filtered = allData.value
      if (activeCategory.value !== 'all') {
        filtered = allData.value.filter(item => item.category === activeCategory.value)
      }
      total.value = filtered.length
      const start = (pageNum.value - 1) * pageSize.value
      const end = start + pageSize.value
      tableData.value = filtered.slice(start, end)
    }
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const scanFiles = async () => {
  try {
    const response = await request.post('/api/dataset/scan')
    if (response.code === 200) {
      ElMessage.success('扫描成功！新增 ' + response.data.newFiles + ' 个文件')
      loadData()
    }
  } catch (error) {
    ElMessage.error('扫描失败')
  }
}

const beforeUpload = (file) => {
  const isValidType = ['text/csv',
                       'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
                       'application/vnd.ms-excel'].includes(file.type) ||
                       file.name.endsWith('.csv') ||
                       file.name.endsWith('.xlsx') ||
                       file.name.endsWith('.xls')
  if (!isValidType) {
    ElMessage.error('只能上传 CSV、XLSX 或 XLS 格式文件！')
    return false
  }
  const isValidSize = file.size / 1024 / 1024 < 50
  if (!isValidSize) {
    ElMessage.error('文件大小不能超过 50MB！')
    return false
  }
  return true
}

const onUploadSuccess = (response) => {
  if (response.code === 200) {
    ElMessage.success('上传成功')
    uploadVisible.value = false
    loadData()
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}

const onUploadError = () => {
  ElMessage.error('上传失败，请检查网络或文件格式')
}

const viewDetail = (row) => {
  currentDataset.value = row
  detailVisible.value = true
}

const deleteDataset = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该训练集吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const response = await request.delete(`/api/dataset/${row.id}`)
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

const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const getFileTagType = (type) => {
  const map = {
    'csv': 'success',
    'xlsx': 'primary',
    'xls': 'info'
  }
  return map[type] || 'default'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.dataset-management {
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

.header-actions {
  display: flex;
  gap: 12px;
}

.search-section {
  margin-bottom: 20px;
}

.search-box {
  display: flex;
  gap: 12px;
  align-items: center;
}

.table-section {
  margin-bottom: 20px;
}

.pagination-box {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 上传区域样式（融合原上传组件风格） */
.upload-container {
  padding: 10px 0;
}

.upload-demo {
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
  padding: 45px 20px;
  border: 2px dashed #c9cdd4;
  border-radius: 12px;
  text-align: center;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  background-color: #f9fafb;
}

.upload-demo:hover {
  border-color: #2ecc71;
  background-color: #f0fdf4;
  transform: translateY(-4px);
  box-shadow: 0 6px 18px rgba(46, 204, 113, 0.12);
}

.el-icon-upload {
  font-size: 52px;
  color: #2ecc71;
  margin-bottom: 22px;
  transition: transform 0.3s ease;
}

.upload-demo:hover .el-icon-upload {
  transform: scale(1.08) rotate(5deg);
}

.el-upload__text {
  font-size: 16px;
  color: #4b5563;
  margin-bottom: 18px;
  line-height: 1.6;
}

.el-upload__text em {
  color: #2ecc71;
  font-style: normal;
  font-weight: 500;
  cursor: pointer;
  text-decoration: underline;
  padding: 0 3px;
}

.el-upload__tip {
  color: #9ca3af;
  font-size: 14px;
  margin-top: 12px;
  padding: 0 25px;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .upload-demo {
    padding: 35px 15px;
    margin: 25px auto;
  }
  .el-icon-upload {
    font-size: 40px;
    margin-bottom: 18px;
  }
  .el-upload__text {
    font-size: 14px;
  }
}
</style>