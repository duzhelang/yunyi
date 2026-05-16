<template>
  <div class="data-collection-container">
    <el-row :gutter="20">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>数据上传</span>
            </div>
          </template>
          <el-upload
            class="upload-area"
            drag
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            accept=".xlsx,.xls,.csv"
          >
            <el-icon class="upload-icon"><Upload /></el-icon>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <template #tip>
              <div class="el-upload__tip">支持 Excel (.xlsx/.xls) 和 CSV 文件</div>
            </template>
          </el-upload>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" style="position: relative;">
          <ProgressOverlay 
            :visible="preprocessing"
            title="数据预处理中"
            :steps="preprocessSteps"
            :hints="preprocessHints"
            color="#409eff"
          />
          <template #header>
            <div class="card-header">
              <span>数据预处理</span>
            </div>
          </template>
          <el-form label-width="120px">
            <el-form-item label="选择文件">
              <el-select v-model="selectedFileId" placeholder="请选择要预处理的文件" style="width: 100%">
                <el-option
                  v-for="file in fileList"
                  :key="file.id"
                  :label="file.name"
                  :value="file.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="缺失值策略">
              <el-select v-model="fillStrategy" placeholder="请选择">
                <el-option label="均值填充" value="mean" />
                <el-option label="中位数填充" value="median" />
                <el-option label="删除含缺失行" value="drop" />
              </el-select>
            </el-form-item>
            <el-form-item label="标准化">
              <el-switch v-model="standardize" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="preprocessing" @click="handlePreprocess">
                开始预处理
              </el-button>
            </el-form-item>
          </el-form>
          <div v-if="preprocessResult" class="preprocess-result">
            <el-alert :title="preprocessResult" type="success" show-icon :closable="false" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="hover" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>数据列表</span>
          <el-button type="primary" size="small" @click="loadData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>
      <el-table :data="dataList" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="name" label="文件名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="90" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ row.type || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="大小" width="100" align="center">
          <template #default="{ row }">
            {{ row.size ? (row.size / 1024).toFixed(1) + ' KB' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.category === 'train' ? 'success' : 'warning'" size="small">
              {{ row.category === 'train' ? '训练集' : row.category === 'test' ? '测试集' : '未分类' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="上传时间" width="180" align="center" />
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="previewData(row)">预览</el-button>
            <el-button type="danger" size="small" link @click="deleteData(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <el-dialog v-model="previewVisible" title="数据预览" width="800px" append-to-body>
      <el-table :data="previewRows" border max-height="400">
        <el-table-column v-for="col in previewColumns" :key="col" :prop="col" :label="col" min-width="120" show-overflow-tooltip />
      </el-table>
      <div v-if="!previewRows.length" style="text-align:center;color:#999;padding:20px">暂无数据</div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Upload, Refresh } from '@element-plus/icons-vue'
import request from '@/utils/request'
import ProgressOverlay from '@/components/common/ProgressOverlay.vue'

const preprocessSteps = [
  '解析文件结构',
  '处理缺失值',
  '数据标准化',
  '生成预处理结果'
]

const preprocessHints = [
  '正在读取并解析数据文件...',
  '正在使用选定策略填充缺失值...',
  '正在进行数据标准化处理...',
  '即将完成，请稍候...'
]

const uploadUrl = '/api/dataset/upload'
const uploadHeaders = (() => {
  const userStr = localStorage.getItem('user') || sessionStorage.getItem('user')
  let token = ''
  if (userStr) {
    try { token = JSON.parse(userStr).token || '' } catch {}
  }
  return { token }
})()

const dataList = ref([])
const fileList = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const selectedFileId = ref(null)
const fillStrategy = ref('mean')
const standardize = ref(true)
const preprocessing = ref(false)
const preprocessResult = ref('')

const previewVisible = ref(false)
const previewRows = ref([])
const previewColumns = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/dataset/list', {
      params: { pageNum: pageNum.value, pageSize: pageSize.value }
    })
    if (res.code === '200') {
      dataList.value = res.data?.records || []
      total.value = res.data?.total || 0
    }
  } catch (e) {
    ElMessage.error('加载数据列表失败')
  } finally {
    loading.value = false
  }
}

const loadAllFiles = async () => {
  try {
    const res = await request.get('/api/dataset/all')
    if (res.code === '200') {
      fileList.value = res.data || []
    }
  } catch (e) {
    ElMessage.error('加载文件列表失败')
  }
}

const handleUploadSuccess = (res) => {
  if (res.code === '200') {
    ElMessage.success('上传成功')
    loadData()
    loadAllFiles()
  } else {
    ElMessage.error(res.msg || '上传失败')
  }
}

const handleUploadError = () => {
  ElMessage.error('上传失败')
}

const handlePreprocess = async () => {
  if (!selectedFileId.value) {
    ElMessage.warning('请先选择要预处理的文件')
    return
  }
  preprocessing.value = true
  preprocessResult.value = ''
  try {
    const res = await request.post('/api/data/preprocess', {
      fileId: selectedFileId.value,
      fillMethod: fillStrategy.value,
      standardize: standardize.value
    })
    if (res.code === '200') {
      preprocessResult.value = res.data?.message || '预处理完成'
      ElMessage.success('预处理完成')
      loadData()
    } else {
      ElMessage.error(res.msg || '预处理失败')
    }
  } catch {
    ElMessage.error('预处理请求失败')
  } finally {
    preprocessing.value = false
  }
}

const previewData = async (row) => {
  try {
    const res = await request.get(`/api/dataset/${row.id}`)
    if (res.code === '200' && res.data) {
      ElMessage.info(`文件: ${row.name}`)
    }
    previewColumns.value = ['id', 'name', 'type', 'category', 'createTime']
    previewRows.value = [row]
    previewVisible.value = true
  } catch {
    ElMessage.error('预览失败')
  }
}

const deleteData = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除 "${row.name}"？`, '确认', { type: 'warning' })
    const res = await request.delete(`/api/dataset/${row.id}`)
    if (res.code === '200') {
      ElMessage.success('删除成功')
      loadData()
      loadAllFiles()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch {}
}

onMounted(() => {
  loadData()
  loadAllFiles()
})
</script>

<style scoped>
.data-collection-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.upload-area {
  width: 100%;
}

.upload-icon {
  font-size: 48px;
  color: #c0c4cc;
  margin-bottom: 12px;
}

.preprocess-result {
  margin-top: 16px;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
