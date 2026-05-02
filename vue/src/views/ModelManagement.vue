<template>
  <div class="ai-model-center">
    <!-- ================= 页面头部 ================= -->
    <div class="page-header">
      <h2>🧠 AI 模型与数据管理中心</h2>
      <p>统一管理糖尿病预测模型、训练数据、性能分析与实验配置</p>
    </div>

    <!-- ================= 当前激活模型 ================= -->
    <el-card class="active-model-card">
      <template #header>
        <span>⚡ 当前激活模型</span>
      </template>
      <div v-if="activeModel" class="active-info">
        <div><strong>{{ activeModel.modelName }}</strong> v{{ activeModel.version }}</div>
        <el-tag type="success">运行中</el-tag>
        <p>{{ activeModel.description }}</p>
      </div>
      <el-empty v-else description="暂无激活模型" />
    </el-card>

    <!-- ================= 标签页切换 ================= -->
    <el-tabs v-model="activeTab" type="card" class="tabs-container">
      <!-- 模型管理 -->
      <el-tab-pane label="模型列表" name="model">
        <div class="toolbar">
          <el-input v-model="modelSearch" placeholder="搜索模型名称" clearable style="width:220px" />
          <el-select v-model="modelRemarkFilter" placeholder="按批次筛选" clearable style="width:160px">
            <el-option label="早期批次" value="早期批次" />
            <el-option label="中期批次" value="中期批次" />
            <el-option label="后期批次" value="后期批次" />
            <el-option label="最终批次" value="最终批次" />
          </el-select>
          <el-button type="primary" @click="loadModels">查询</el-button>
          <el-button @click="showAddModelDialog = true">新增模型</el-button>
        </div>

        <el-table :data="filteredModelList" border stripe v-loading="modelLoading">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="modelName" label="模型名称" />
          <el-table-column prop="version" label="版本" width="100" />
          <el-table-column prop="remark" label="批次" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'active' ? 'success' : 'info'">
                {{ row.status === 'active' ? '激活' : row.status === 'archived' ? '归档' : '离线' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="170" />
          <el-table-column label="操作" width="240">
            <template #default="{ row }">
              <el-button v-if="row.status !== 'active'" type="success" size="small" @click="activateModel(row)">激活</el-button>
              <el-button type="primary" size="small" @click="editModel(row)">编辑</el-button>
              <el-button type="danger" size="small" @click="deleteModel(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 文件管理（训练数据集） -->
      <el-tab-pane label="文件管理" name="file">
        <div class="toolbar">
          <el-input v-model="fileSearch" placeholder="搜索文件名" clearable style="width:220px" />
          <el-button type="primary" @click="loadFiles">查询</el-button>
          <el-button @click="resetFileFilter">重置</el-button>
        </div>

        <el-table :data="filteredFileList" border stripe v-loading="fileLoading">
          <el-table-column type="selection" width="50" />
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="name" label="文件名称" />
          <el-table-column prop="type" label="文件类型" width="100" />
          <el-table-column prop="size" label="大小(kb)" width="100" />
          <el-table-column label="备注" width="180">
            <template #default="{ row }">
              <el-button link @click="openRemarkDialog(row)">{{ row.remark || '点击添加' }}</el-button>
            </template>
          </el-table-column>
          <el-table-column label="训练" width="100">
            <template #default="{ row }">
              <el-button type="warning" size="small" @click="startTraining(row)">训练</el-button>
            </template>
          </el-table-column>
          <el-table-column label="下载" width="100">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="downloadFile(row)" :disabled="!row.pythonurl">下载</el-button>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-popconfirm title="确定删除吗？" @confirm="deleteFile(row.id)">
                <template #reference>
                  <el-button type="danger" size="small">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination
            v-model:current-page="filePageNum"
            v-model:page-size="filePageSize"
            :total="fileTotal"
            layout="total, sizes, prev, pager, next"
            @size-change="loadFiles"
            @current-change="loadFiles"
          />
        </div>
      </el-tab-pane>

      <!-- 分析看板 -->
      <el-tab-pane label="分析看板" name="dashboard">
        <div class="dashboard-grid">
          <div class="chart-box"><div ref="accuracyChart" style="height:220px"></div><p>准确率</p></div>
          <div class="chart-box"><div ref="precisionChart" style="height:220px"></div><p>精确率</p></div>
          <div class="chart-box"><div ref="recallChart" style="height:220px"></div><p>召回率</p></div>
          <div class="chart-box"><div ref="f1Chart" style="height:220px"></div><p>F1-Score</p></div>
        </div>
        <div class="auc-chart" ref="aucChart" style="height:300px"></div>
      </el-tab-pane>

      <!-- A/B 测试 -->
      <el-tab-pane label="A/B 测试" name="abtest">
        <el-form label-width="100px">
          <el-form-item label="模型 A">
            <el-select v-model="abTest.modelA" placeholder="选择模型">
              <el-option v-for="m in modelList" :key="m.id" :label="m.modelName + ' v' + m.version" :value="m.version" />
            </el-select>
          </el-form-item>
          <el-form-item label="模型 B">
            <el-select v-model="abTest.modelB" placeholder="选择模型">
              <el-option v-for="m in modelList" :key="m.id" :label="m.modelName + ' v' + m.version" :value="m.version" />
            </el-select>
          </el-form-item>
          <el-form-item label="流量比例">
            <el-slider v-model="abTest.trafficRatio" :min="0" :max="100" show-input />
            <span>A: {{ abTest.trafficRatio }}% / B: {{ 100 - abTest.trafficRatio }}%</span>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="startABTest">开始测试</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <!-- 特征配置 -->
      <el-tab-pane label="特征配置" name="feature">
        <el-table :data="featureList" border>
          <el-table-column prop="name" label="特征名" />
          <el-table-column prop="type" label="类型" width="100" />
          <el-table-column prop="defaultValue" label="默认值" width="100" />
          <el-table-column prop="description" label="描述" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="editFeature(row)">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- ================= 新增模型对话框 ================= -->
    <el-dialog v-model="showAddModelDialog" title="新增模型" width="500px">
      <el-form :model="addModelForm" label-width="100px">
        <el-form-item label="模型名称"><el-input v-model="addModelForm.modelName" /></el-form-item>
        <el-form-item label="版本号"><el-input v-model="addModelForm.version" /></el-form-item>
        <el-form-item label="批次"><el-select v-model="addModelForm.remark"><el-option v-for="b in batches" :key="b" :label="b" :value="b" /></el-select></el-form-item>
        <el-form-item label="文件路径"><el-input v-model="addModelForm.filePath" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="addModelForm.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddModelDialog = false">取消</el-button>
        <el-button type="primary" @click="submitAddModel" :loading="addSubmitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 编辑模型对话框 -->
    <el-dialog v-model="showEditModelDialog" title="编辑模型" width="500px">
      <el-form :model="editModelForm" label-width="100px">
        <el-form-item label="模型名称"><el-input v-model="editModelForm.modelName" /></el-form-item>
        <el-form-item label="版本"><el-input v-model="editModelForm.version" /></el-form-item>
        <el-form-item label="批次"><el-select v-model="editModelForm.remark"><el-option v-for="b in batches" :key="b" :label="b" :value="b" /></el-select></el-form-item>
        <el-form-item label="状态"><el-select v-model="editModelForm.status"><el-option label="在线" value="online"/><el-option label="离线" value="offline"/><el-option label="归档" value="archived"/></el-select></el-form-item>
        <el-form-item label="描述"><el-input v-model="editModelForm.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditModelDialog = false">取消</el-button>
        <el-button type="primary" @click="submitEditModel">保存</el-button>
      </template>
    </el-dialog>

    <!-- 备注编辑对话框 -->
    <el-dialog v-model="showRemarkDialog" title="编辑备注" width="400px">
      <el-form :model="remarkForm">
        <el-form-item label="文件名"><el-input v-model="remarkForm.name" disabled /></el-form-item>
        <el-form-item label="备注"><el-input v-model="remarkForm.remark" type="textarea" /></el-form-item>
        <el-form-item label="类型"><el-select v-model="remarkForm.remarkType"><el-option v-for="b in batches" :key="b" :label="b" :value="b" /></el-select></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRemarkDialog = false">取消</el-button>
        <el-button type="primary" @click="saveRemark">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import request from '@/utils/request'

const serverIp = window.config ? window.config.serverIp : 'localhost'

// ==================== 数据 ====================
const activeTab = ref('model')
const batches = ['早期批次', '中期批次', '后期批次', '小批量', '最终批次']

// 模型相关
const modelList = ref([])
const modelLoading = ref(false)
const modelSearch = ref('')
const modelRemarkFilter = ref('')
const filteredModelList = computed(() => {
  return modelList.value.filter(m => {
    const matchName = !modelSearch.value || m.modelName.toLowerCase().includes(modelSearch.value.toLowerCase())
    const matchRemark = !modelRemarkFilter.value || m.remark === modelRemarkFilter.value
    return matchName && matchRemark
  })
})
const activeModel = ref(null)

// 文件相关
const fileList = ref([])
const fileLoading = ref(false)
const fileSearch = ref('')
const filePageNum = ref(1)
const filePageSize = ref(10)
const fileTotal = ref(0)
const filteredFileList = computed(() => {
  return fileList.value.filter(f => !fileSearch.value || f.name.toLowerCase().includes(fileSearch.value.toLowerCase()))
})

// A/B测试
const abTest = reactive({ modelA: '', modelB: '', trafficRatio: 50 })

// 特征配置
const featureList = ref([
  { name: 'Pregnancies', type: 'numeric', defaultValue: '0', description: '怀孕次数' },
  { name: 'Glucose', type: 'numeric', defaultValue: '100', description: '葡萄糖水平' },
  { name: 'BloodPressure', type: 'numeric', defaultValue: '80', description: '血压' },
  { name: 'SkinThickness', type: 'numeric', defaultValue: '20', description: '皮肤厚度' },
  { name: 'Insulin', type: 'numeric', defaultValue: '80', description: '胰岛素' },
  { name: 'BMI', type: 'numeric', defaultValue: '25', description: '体重指数' },
  { name: 'DiabetesPedigreeFunction', type: 'numeric', defaultValue: '0.5', description: '糖尿病家族史' },
  { name: 'Age', type: 'numeric', defaultValue: '30', description: '年龄' }
])

// 对话框控制
const showAddModelDialog = ref(false)
const showEditModelDialog = ref(false)
const showRemarkDialog = ref(false)
const addSubmitting = ref(false)
const addModelForm = reactive({ modelName: '', version: '', remark: '', filePath: '', description: '' })
const editModelForm = reactive({ id: '', modelName: '', version: '', remark: '', status: '', description: '' })
const remarkForm = reactive({ id: '', name: '', remark: '', remarkType: '' })

// 图表实例
const chartInstances = {}

// ==================== 模型操作 ====================
const loadModels = async () => {
  modelLoading.value = true
  try {
    const res = await request.get('/api/model/list')
    if (res.code === 200) {
      modelList.value = res.data.records || []
    }
  } catch {
    console.warn('使用模拟数据')
    modelList.value = [
      { id:1, modelName:'糖尿病预测模型', version:'v2.1.0', remark:'早期批次', status:'active', createTime:'2026-04-15' },
      { id:2, modelName:'糖尿病预测模型', version:'v2.2.0', remark:'中期批次', status:'offline', createTime:'2026-04-18' },
      { id:3, modelName:'糖尿病预测模型', version:'v1.0.0', remark:'最终批次', status:'archived', createTime:'2026-04-01' }
    ]
  } finally {
    modelLoading.value = false
  }
}

const submitAddModel = async () => {
  if (!addModelForm.modelName || !addModelForm.version) return ElMessage.warning('请填写必要信息')
  addSubmitting.value = true
  try {
    await request.post('/api/model/add', addModelForm)
    ElMessage.success('新增成功')
    showAddModelDialog.value = false
    loadModels()
  } catch {
    // 模拟添加
    modelList.value.unshift({ ...addModelForm, id: Date.now(), status: 'offline', createTime: new Date().toLocaleString() })
    ElMessage.success('新增成功（模拟）')
    showAddModelDialog.value = false
  } finally { addSubmitting.value = false }
}

const activateModel = async (row) => {
  try {
    await ElMessageBox.confirm('确定激活此模型？', '提示', { type: 'warning' })
    await request.post(`/api/model/${row.id}/activate`)
    ElMessage.success('已激活')
    loadModels()
  } catch {
    // 模拟激活
    row.status = 'active'
    ElMessage.success('激活成功（模拟）')
  }
}

const editModel = (row) => {
  Object.assign(editModelForm, row)
  showEditModelDialog.value = true
}

const submitEditModel = async () => {
  try {
    await request.put('/api/model/' + editModelForm.id, editModelForm)
    ElMessage.success('编辑成功')
    showEditModelDialog.value = false
    loadModels()
  } catch {
    const index = modelList.value.findIndex(m => m.id === editModelForm.id)
    if (index !== -1) {
      modelList.value[index] = { ...editModelForm }
      ElMessage.success('编辑成功（模拟）')
      showEditModelDialog.value = false
    }
  }
}

const deleteModel = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除？', '警告', { type: 'warning' })
    await request.delete('/api/model/' + id)
    ElMessage.success('删除成功')
    loadModels()
  } catch {
    modelList.value = modelList.value.filter(m => m.id !== id)
    ElMessage.success('删除成功（模拟）')
  }
}

// ==================== 文件操作 ====================
const loadFiles = async () => {
  fileLoading.value = true
  try {
    const res = await request.get('/file/page', { params: { pageNum: filePageNum.value, pageSize: filePageSize.value, name: fileSearch.value } })
    if (res.code === '200') {
      fileList.value = res.data.records.map(f => ({ ...f, remark: f.remark || '点击添加备注' }))
      fileTotal.value = res.data.total
    }
  } catch {
    console.warn('文件加载失败')
  } finally { fileLoading.value = false }
}

const resetFileFilter = () => {
  fileSearch.value = ''
  loadFiles()
}

const openRemarkDialog = (row) => {
  remarkForm.id = row.id
  remarkForm.name = row.name
  remarkForm.remark = row.remark
  remarkForm.remarkType = ''
  showRemarkDialog.value = true
}

const saveRemark = async () => {
  try {
    await request.post('/file/update', { id: remarkForm.id, remark: remarkForm.remark })
    ElMessage.success('备注已保存')
    showRemarkDialog.value = false
    loadFiles()
  } catch {
    ElMessage.error('保存失败')
  }
}

const startTraining = (row) => {
  const url = row.url.slice(29)
  ElMessage.info('模型训练已启动')
  request.get('python/getUrl/' + url).then(res => {
    if (res.code === '200') ElMessage.success('训练完成，请下载模型')
  }).catch(() => ElMessage.error('训练失败'))
}

const downloadFile = (row) => {
  if (row.pythonurl) {
    window.open('http://localhost:9090/python/' + row.pythonurl)
  }
}

const deleteFile = async (id) => {
  try {
    await request.delete('/file/' + id)
    ElMessage.success('删除成功')
    loadFiles()
  } catch { ElMessage.error('删除失败') }
}

// ==================== A/B 测试 ====================
const startABTest = () => {
  ElMessage.success('A/B 测试已开始（模拟）')
}

// ==================== 特征编辑 ====================
const editFeature = (row) => {
  ElMessage.info('编辑特征: ' + row.name + ' (模拟)')
}

// ==================== 图表渲染 ====================
const initCharts = () => {
  nextTick(() => {
    const createChart = (ref, data, title) => {
      if (!chartInstances[ref]) {
        const dom = document.getElementById(ref)
        if (dom) chartInstances[ref] = echarts.init(dom)
      }
      chartInstances[ref]?.setOption({
        title: { text: title, left: 'center', textStyle: { fontSize: 12 } },
        tooltip: { trigger: 'axis' },
        xAxis: { data: ['v1.0', 'v2.0', 'v2.1', 'v2.2'] },
        yAxis: { min: 0.8, max: 1 },
        series: [{ data: data, type: 'line', smooth: true }]
      })
    }
    createChart('accuracyChart', [0.92, 0.93, 0.94, 0.94], '准确率')
    createChart('precisionChart', [0.88, 0.91, 0.92, 0.93], '精确率')
    createChart('recallChart', [0.85, 0.90, 0.91, 0.92], '召回率')
    createChart('f1Chart', [0.86, 0.90, 0.91, 0.92], 'F1')
  })
}

watch(activeTab, (val) => {
  if (val === 'dashboard') {
    nextTick(() => initCharts())
  }
})

// ==================== 生命周期 ====================
onMounted(() => {
  loadModels()
  loadFiles()
})
</script>

<style scoped>
.ai-model-center {
  padding: 20px;
  background: #f5f7fa;
}
.page-header {
  background: linear-gradient(135deg, #409EFF, #36D1DC);
  color: white;
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 20px;
}
.page-header h2 { margin: 0 0 8px; }
.active-model-card {
  margin-bottom: 20px;
  background: #f0fdf4;
}
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}
.chart-box {
  background: white;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}
.auc-chart {
  background: white;
  padding: 16px;
  border-radius: 8px;
}
</style>