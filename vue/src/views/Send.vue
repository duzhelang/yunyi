<template>
  <div class="repair-container">
    <div class="repair-main">
      <el-card class="repair-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <div class="header-left">
              <el-icon class="header-icon"><Warning /></el-icon>
              <span class="header-title">故障报修</span>
            </div>
            <el-tag type="info" size="small">请详细描述您遇到的问题</el-tag>
          </div>
        </template>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="110px"
          label-position="right"
          size="default"
        >
          <el-row :gutter="24">
            <el-col :span="24">
              <el-form-item label="故障标题" prop="title">
                <el-input
                  v-model="form.title"
                  placeholder="请用一句话简要描述问题（2-50字）"
                  maxlength="50"
                  show-word-limit
                  clearable
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="故障分类" prop="faultType">
                <el-cascader
                  v-model="faultTypeValue"
                  :options="faultTypeOptions"
                  :props="{ expandTrigger: 'hover' }"
                  placeholder="请选择故障分类"
                  style="width: 100%"
                  clearable
                  @change="handleFaultTypeChange"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="涉及模块" prop="pagePath">
                <el-select
                  v-model="form.pagePath"
                  placeholder="请选择出现问题的功能模块"
                  style="width: 100%"
                  clearable
                  filterable
                >
                  <el-option
                    v-for="item in moduleOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="紧急程度" prop="urgency">
                <el-radio-group v-model="form.urgency">
                  <el-radio value="一般">
                    <el-tag type="info" size="small" effect="plain">一般</el-tag>
                  </el-radio>
                  <el-radio value="紧急">
                    <el-tag type="warning" size="small" effect="plain">紧急</el-tag>
                  </el-radio>
                  <el-radio value="非常紧急">
                    <el-tag type="danger" size="small" effect="plain">非常紧急</el-tag>
                  </el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="接受远程协助">
                <el-switch
                  v-model="form.acceptRemote"
                  active-text="是"
                  inactive-text="否"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="问题描述" prop="description">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="5"
              placeholder="请详细描述您遇到的问题，包括：&#10;1. 您在做什么操作时出现的问题？&#10;2. 问题的具体表现是什么？&#10;3. 是否有错误提示信息？"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="重现步骤" prop="reproSteps">
            <el-input
              v-model="form.reproSteps"
              type="textarea"
              :rows="3"
              placeholder="请描述如何操作可以重现此问题（选填）&#10;例如：1. 打开预测工作台 → 2. 上传CSV文件 → 3. 点击开始预测 → 4. 页面卡住无响应"
              maxlength="300"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="附件上传">
            <el-upload
              ref="uploadRef"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :file-list="fileList"
              :on-success="handleUploadSuccess"
              :on-remove="handleUploadRemove"
              :on-exceed="handleExceed"
              :before-upload="beforeUpload"
              :limit="3"
              accept=".png,.jpg,.jpeg,.gif,.txt,.log,.pdf"
              list-type="picture-card"
              multiple
            >
              <el-icon><Plus /></el-icon>
              <template #tip>
                <div class="el-upload__tip">
                  支持 PNG/JPG/TXT/LOG/PDF 格式，单文件不超过 5MB，最多 3 个文件
                </div>
              </template>
            </el-upload>
          </el-form-item>

          <el-divider content-position="left">联系方式（自动填充，可修改）</el-divider>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="联系手机" prop="contactPhone">
                <el-input
                  v-model="form.contactPhone"
                  placeholder="请输入联系手机号"
                  maxlength="11"
                  clearable
                >
                  <template #prefix>
                    <el-icon><Phone /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系邮箱" prop="contactEmail">
                <el-input
                  v-model="form.contactEmail"
                  placeholder="请输入联系邮箱"
                  clearable
                >
                  <template #prefix>
                    <el-icon><Message /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item>
            <div class="form-actions">
              <el-button type="primary" size="large" :loading="submitting" @click="submitForm">
                <el-icon><Promotion /></el-icon>
                提交工单
              </el-button>
              <el-button size="large" @click="resetForm">
                <el-icon><RefreshLeft /></el-icon>
                重置表单
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card class="history-card" shadow="hover" v-if="recentOrders.length > 0">
        <template #header>
          <div class="card-header">
            <div class="header-left">
              <el-icon class="header-icon"><Clock /></el-icon>
              <span class="header-title">最近报修记录</span>
            </div>
            <el-button type="primary" link @click="goToList">查看全部</el-button>
          </div>
        </template>
        <el-table :data="recentOrders" stripe size="small" @row-click="goToDetail">
          <el-table-column prop="orderNo" label="工单编号" width="160" />
          <el-table-column prop="title" label="标题" show-overflow-tooltip />
          <el-table-column prop="faultType" label="故障类型" width="120" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small" effect="plain">
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="提交时间" width="170">
            <template #default="{ row }">
              {{ formatTime(row.createTime) }}
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Warning, Plus, Phone, Message, Promotion, RefreshLeft, Clock } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { CacheHelper } from '@/utils/cacheHelper'

const router = useRouter()
const formRef = ref(null)
const uploadRef = ref(null)
const submitting = ref(false)
const fileList = ref([])
const recentOrders = ref([])

const userStr = localStorage.getItem('user') || sessionStorage.getItem('user')
let token = ''
if (userStr) {
  try { token = JSON.parse(userStr).token || '' } catch (e) {}
}
const uploadHeaders = { token }

const uploadUrl = computed(() => {
  const host = window.location.hostname || 'localhost'
  return `http://${host}:9090/api/repair/upload`
})

const form = reactive({
  title: '',
  faultType: '',
  faultSubType: '',
  pagePath: '',
  urgency: '一般',
  description: '',
  reproSteps: '',
  attachments: [],
  contactPhone: '',
  contactEmail: '',
  acceptRemote: false
})

const faultTypeValue = ref([])

const faultTypeOptions = [
  {
    value: '功能异常',
    label: '功能异常',
    children: [
      { value: '点击无反应', label: '点击无反应' },
      { value: '页面无响应', label: '页面无响应' },
      { value: '按钮失效', label: '按钮失效' },
      { value: '表单提交失败', label: '表单提交失败' },
      { value: '登录异常', label: '登录异常' },
      { value: '其他功能异常', label: '其他功能异常' }
    ]
  },
  {
    value: '性能问题',
    label: '性能问题',
    children: [
      { value: '页面加载慢', label: '页面加载慢' },
      { value: '操作卡顿', label: '操作卡顿' },
      { value: '图表加载缓慢', label: '图表加载缓慢' },
      { value: '系统响应超时', label: '系统响应超时' },
      { value: '其他性能问题', label: '其他性能问题' }
    ]
  },
  {
    value: '数据错误',
    label: '数据错误',
    children: [
      { value: '预测结果错误', label: '预测结果错误' },
      { value: '数据显示不正确', label: '数据显示不正确' },
      { value: '报表数据异常', label: '报表数据异常' },
      { value: '文件上传失败', label: '文件上传失败' },
      { value: '其他数据问题', label: '其他数据问题' }
    ]
  },
  {
    value: '界面问题',
    label: '界面问题',
    children: [
      { value: '布局错乱', label: '布局错乱' },
      { value: '文字重叠', label: '文字重叠' },
      { value: '样式丢失', label: '样式丢失' },
      { value: '图标不显示', label: '图标不显示' },
      { value: '其他界面问题', label: '其他界面问题' }
    ]
  },
  {
    value: '其他',
    label: '其他',
    children: [
      { value: '功能建议', label: '功能建议' },
      { value: '其他问题', label: '其他问题' }
    ]
  }
]

const moduleOptions = [
  { label: '首页仪表盘', value: 'home' },
  { label: '数据采集', value: 'data-collection' },
  { label: '预测工作台', value: 'prediction-workbench' },
  { label: '诊断工作台', value: 'doctor-workbench' },
  { label: '个体洞察', value: 'individual-insight' },
  { label: '组合分析', value: 'group-analysis' },
  { label: '健康管理中心', value: 'health-profile' },
  { label: '风险快检', value: 'risk-quick' },
  { label: '智能问答', value: 'chat' },
  { label: '糖尿病科普', value: 'diabetes-education' },
  { label: '诊疗档案', value: 'treatment-record' },
  { label: '训练集管理', value: 'dataset-management' },
  { label: '模型管理', value: 'model-manager' },
  { label: '在线模型训练', value: 'online-training' },
  { label: '数据报表', value: 'dashbord' },
  { label: '详细报表', value: 'detailbord' },
  { label: '采集日志', value: 'data-test' },
  { label: '在线预测', value: 'test-file' },
  { label: '用户管理', value: 'user' },
  { label: '角色管理', value: 'role' },
  { label: '菜单管理', value: 'menu' },
  { label: '登录/注册', value: 'login' },
  { label: '其他页面', value: 'other' }
]

const rules = {
  title: [
    { required: true, message: '请输入故障标题', trigger: 'blur' },
    { min: 2, max: 50, message: '标题长度为 2-50 个字符', trigger: 'blur' }
  ],
  faultType: [
    { required: true, message: '请选择故障分类', trigger: 'change' }
  ],
  pagePath: [
    { required: true, message: '请选择涉及模块', trigger: 'change' }
  ],
  urgency: [
    { required: true, message: '请选择紧急程度', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入问题描述', trigger: 'blur' },
    { min: 10, max: 500, message: '描述长度为 10-500 个字符', trigger: 'blur' }
  ]
}

const handleFaultTypeChange = (val) => {
  if (val && val.length === 2) {
    form.faultType = val[0]
    form.faultSubType = val[1]
  } else {
    form.faultType = ''
    form.faultSubType = ''
  }
}

const beforeUpload = (file) => {
  const maxSize = 5 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过 5MB')
    return false
  }
  return true
}

const handleUploadSuccess = (response, file, files) => {
  if (response && typeof response === 'object' && response.code === '200') {
    form.attachments.push(response.data)
  } else if (typeof response === 'string') {
    form.attachments.push(response)
  }
  ElMessage.success('文件上传成功')
}

const handleUploadRemove = (file) => {
  const url = file.response?.data || file.url
  const idx = form.attachments.indexOf(url)
  if (idx > -1) {
    form.attachments.splice(idx, 1)
  }
}

const handleExceed = () => {
  ElMessage.warning('最多上传 3 个文件')
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const res = await request.post('/api/repair/submit', form)
      if (res.code === '200') {
        ElMessage.success('工单已提交，运维人员将尽快处理')
        resetForm()
        loadRecentOrders()
        router.push('/list')
      } else {
        ElMessage.error(res.msg || '提交失败')
      }
    } catch (e) {
      ElMessage.error('提交失败，请稍后重试')
    } finally {
      submitting.value = false
    }
  })
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  faultTypeValue.value = []
  form.faultType = ''
  form.faultSubType = ''
  form.attachments = []
  form.acceptRemote = false
  fileList.value = []
}

const loadRecentOrders = async () => {
  try {
    const res = await request.get('/api/repair/my/list', {
      params: { pageNum: 1, pageSize: 5, status: '', keyword: '' }
    })
    if (res.code === '200') {
      recentOrders.value = res.data.records || []
    }
  } catch (e) {}
}

const loadUserInfo = () => {
  try {
    const userStr = localStorage.getItem('user') || sessionStorage.getItem('user')
    if (userStr) {
      const user = JSON.parse(userStr)
      form.contactPhone = user.phone || ''
      form.contactEmail = user.email || ''
    }
  } catch (e) {}
}

const getStatusType = (status) => {
  const map = {
    '待处理': 'danger',
    '处理中': 'warning',
    '待确认': 'primary',
    '已解决': 'success',
    '已关闭': 'info'
  }
  return map[status] || 'info'
}

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  return d.toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

const goToList = () => {
  router.push('/list')
}

const goToDetail = (row) => {
  router.push({ path: '/list', query: { id: row.id } })
}

onMounted(() => {
  loadUserInfo()
  loadRecentOrders()
})
</script>

<style scoped>
.repair-container {
  padding: 20px;
  min-height: calc(100vh - 120px);
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
}

.repair-main {
  max-width: 960px;
  margin: 0 auto;
}

.repair-card {
  margin-bottom: 20px;
  border-radius: 12px;
}

.repair-card :deep(.el-card__header) {
  padding: 16px 24px;
  border-bottom: 2px solid #409eff;
  background: linear-gradient(90deg, #ecf5ff 0%, #fff 100%);
}

.history-card {
  border-radius: 12px;
}

.history-card :deep(.el-card__header) {
  padding: 16px 24px;
  border-bottom: 2px solid #67c23a;
  background: linear-gradient(90deg, #f0f9eb 0%, #fff 100%);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-icon {
  font-size: 20px;
  color: #409eff;
}

.history-card .header-icon {
  color: #67c23a;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.form-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  width: 100%;
  padding-top: 12px;
}

.el-upload__tip {
  color: #909399;
  font-size: 12px;
  margin-top: 8px;
}

:deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
}

:deep(.el-upload-list__item) {
  width: 100px;
  height: 100px;
}
</style>
