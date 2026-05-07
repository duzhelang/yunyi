<template>
  <div class="repair-container">
    <el-card class="om-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon class="header-icon"><Setting /></el-icon>
            <span class="header-title">运维工单管理</span>
          </div>
          <div class="header-stats">
            <el-tag type="danger" effect="dark" size="small">待处理 {{ stats.pending || 0 }}</el-tag>
            <el-tag type="warning" effect="dark" size="small">处理中 {{ stats.processing || 0 }}</el-tag>
            <el-tag type="primary" effect="dark" size="small">待确认 {{ stats.pendingConfirm || 0 }}</el-tag>
          </div>
        </div>
      </template>

      <div class="filter-bar">
        <el-input v-model="searchKeyword" placeholder="搜索工单编号/标题/提交人" style="width: 260px" clearable @keyup.enter="loadData">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="filterStatus" placeholder="工单状态" clearable style="width: 130px" @change="loadData">
          <el-option label="待处理" value="待处理" />
          <el-option label="处理中" value="处理中" />
          <el-option label="待确认" value="待确认" />
          <el-option label="已解决" value="已解决" />
          <el-option label="已关闭" value="已关闭" />
        </el-select>
        <el-select v-model="filterUrgency" placeholder="紧急程度" clearable style="width: 130px" @change="loadData">
          <el-option label="一般" value="一般" />
          <el-option label="紧急" value="紧急" />
          <el-option label="非常紧急" value="非常紧急" />
        </el-select>
        <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon>查询</el-button>
        <el-button @click="resetFilter"><el-icon><Refresh /></el-icon>重置</el-button>
      </div>

      <el-table :data="tableData" stripe v-loading="loading" @row-click="showDetail" class="order-table">
        <el-table-column prop="orderNo" label="工单编号" width="160">
          <template #default="{ row }"><span class="order-no">{{ row.orderNo }}</span></template>
        </el-table-column>
        <el-table-column prop="title" label="故障标题" show-overflow-tooltip min-width="180" />
        <el-table-column prop="faultType" label="故障类型" width="100" />
        <el-table-column prop="submitRealName" label="提交人" width="90" />
        <el-table-column label="紧急程度" width="100">
          <template #default="{ row }">
            <el-tag :type="getUrgencyType(row.urgency)" size="small" effect="dark">{{ row.urgency }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small" effect="plain">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="处理人" width="90">
          <template #default="{ row }">{{ row.assignUserName || '-' }}</template>
        </el-table-column>
        <el-table-column label="提交时间" width="170">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click.stop="showDetail(row)">
              <el-icon><View /></el-icon>详情
            </el-button>
            <el-button v-if="row.status === '待处理' && !row.assignUserId" type="warning" link size="small" @click.stop="handleClaim(row)">
              <el-icon><Pointer /></el-icon>认领
            </el-button>
            <el-button v-if="row.status === '待处理'" type="success" link size="small" @click.stop="showAssignDialog(row)">
              <el-icon><UserFilled /></el-icon>分配
            </el-button>
            <el-button v-if="row.status === '处理中' || row.status === '待确认'" type="primary" link size="small" @click.stop="showProcessDialog(row)">
              <el-icon><Edit /></el-icon>处理
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[5, 10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>

    <el-drawer v-model="drawerVisible" title="工单详情" size="650px" direction="rtl" :destroy-on-close="true">
      <template v-if="currentOrder">
        <div class="detail-section">
          <div class="detail-header">
            <h3>{{ currentOrder.title }}</h3>
            <div class="detail-tags">
              <el-tag :type="getStatusType(currentOrder.status)" effect="dark">{{ currentOrder.status }}</el-tag>
              <el-tag :type="getUrgencyType(currentOrder.urgency)" effect="plain" size="small">{{ currentOrder.urgency }}</el-tag>
            </div>
          </div>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="工单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="故障类型">{{ currentOrder.faultType }} / {{ currentOrder.faultSubType }}</el-descriptions-item>
            <el-descriptions-item label="涉及模块">{{ currentOrder.pagePath }}</el-descriptions-item>
            <el-descriptions-item label="紧急程度">{{ currentOrder.urgency }}</el-descriptions-item>
            <el-descriptions-item label="提交人">{{ currentOrder.submitRealName }} ({{ currentOrder.submitUserName }})</el-descriptions-item>
            <el-descriptions-item label="处理人">{{ currentOrder.assignUserName || '未分配' }}</el-descriptions-item>
            <el-descriptions-item label="提交时间">{{ formatTime(currentOrder.createTime) }}</el-descriptions-item>
            <el-descriptions-item label="最后更新">{{ formatTime(currentOrder.updateTime) }}</el-descriptions-item>
            <el-descriptions-item label="联系手机">{{ currentOrder.contactPhone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="联系邮箱">{{ currentOrder.contactEmail || '-' }}</el-descriptions-item>
            <el-descriptions-item label="接受远程协助">
              <el-tag :type="currentOrder.acceptRemote ? 'success' : 'info'" size="small">{{ currentOrder.acceptRemote ? '是' : '否' }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="页面路径"><code>{{ currentOrder.pagePath }}</code></el-descriptions-item>
            <el-descriptions-item label="问题描述" :span="2"><div class="desc-text">{{ currentOrder.description }}</div></el-descriptions-item>
            <el-descriptions-item v-if="currentOrder.reproSteps" label="重现步骤" :span="2"><div class="desc-text">{{ currentOrder.reproSteps }}</div></el-descriptions-item>
          </el-descriptions>

          <div v-if="currentOrder.attachments && currentOrder.attachments.length > 0" class="attachment-section">
            <h4>用户附件</h4>
            <div class="attachment-list">
              <div v-for="(url, idx) in currentOrder.attachments" :key="idx" class="attachment-item">
                <el-link type="primary" :href="url" target="_blank"><el-icon><Paperclip /></el-icon>附件 {{ idx + 1 }}</el-link>
              </div>
            </div>
          </div>
        </div>

        <el-divider />
        <div class="log-section">
          <h4>处理记录</h4>
          <el-timeline v-if="orderLogs.length > 0">
            <el-timeline-item v-for="log in orderLogs" :key="log.id" :timestamp="formatTime(log.createTime)" :type="getLogType(log.action)" placement="top">
              <div class="log-item">
                <div class="log-header">
                  <el-tag size="small" :type="getLogType(log.action)" effect="plain">{{ log.action }}</el-tag>
                  <span class="log-operator">{{ log.operatorName }}</span>
                  <el-tag v-if="!log.isVisibleToUser" size="small" type="info" effect="plain">仅内部可见</el-tag>
                </div>
                <div class="log-content">{{ log.content }}</div>
              </div>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无处理记录" :image-size="80" />
        </div>
      </template>
    </el-drawer>

    <el-dialog v-model="showAssign" title="分配工单" width="400px">
      <el-form label-width="80px">
        <el-form-item label="运维人员">
          <el-select v-model="assignUserId" placeholder="选择运维人员" style="width: 100%" filterable>
            <el-option v-for="u in userList" :key="u.id" :label="u.nickname + ' (' + u.username + ')'" :value="u.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAssign = false">取消</el-button>
        <el-button type="primary" :loading="actionLoading" @click="handleAssign">确认分配</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showProcess" title="处理工单" width="550px">
      <el-form label-width="90px">
        <el-form-item label="变更状态">
          <el-radio-group v-model="processForm.status">
            <el-radio-button v-for="s in availableStatuses" :key="s" :value="s">{{ s }}</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="回复用户">
          <el-input v-model="processForm.reply" type="textarea" :rows="3" placeholder="输入回复内容，用户可见..." maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="内部备注">
          <el-input v-model="processForm.internalNote" type="textarea" :rows="2" placeholder="仅运维人员可见的备注..." maxlength="300" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showProcess = false">取消</el-button>
        <el-button type="primary" :loading="actionLoading" @click="handleProcess">提交处理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Setting, Search, Refresh, View, Pointer, UserFilled, Edit, Paperclip } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const actionLoading = ref(false)
const tableData = ref([])
const searchKeyword = ref('')
const filterStatus = ref('')
const filterUrgency = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const stats = ref({})
const userList = ref([])

const drawerVisible = ref(false)
const currentOrder = ref(null)
const orderLogs = ref([])

const showAssign = ref(false)
const assignUserId = ref(null)
const assignOrderId = ref(null)

const showProcess = ref(false)
const processOrderId = ref(null)
const processForm = reactive({ status: '', reply: '', internalNote: '' })

const availableStatuses = computed(() => {
  if (!currentOrder.value) return []
  const s = currentOrder.value.status
  if (s === '处理中') return ['待确认', '已关闭']
  if (s === '待确认') return ['已关闭']
  return []
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/repair/all/list', {
      params: { pageNum: pageNum.value, pageSize: pageSize.value, status: filterStatus.value, urgency: filterUrgency.value, keyword: searchKeyword.value }
    })
    if (res.code === '200') {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (e) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try {
    const res = await request.get('/api/repair/stats')
    if (res.code === '200') stats.value = res.data || {}
  } catch (e) {}
}

const loadUsers = async () => {
  try {
    const res = await request.get('/api/repair/users')
    if (res.code === '200') userList.value = res.data || []
  } catch (e) {}
}

const resetFilter = () => {
  searchKeyword.value = ''
  filterStatus.value = ''
  filterUrgency.value = ''
  pageNum.value = 1
  loadData()
}

const showDetail = async (row) => {
  try {
    const res = await request.get('/api/repair/detail/' + row.id)
    if (res.code === '200') {
      currentOrder.value = res.data.order
      orderLogs.value = res.data.logs || []
      drawerVisible.value = true
    }
  } catch (e) {
    ElMessage.error('获取详情失败')
  }
}

const handleClaim = async (row) => {
  await ElMessageBox.confirm('确定认领该工单？', '确认认领', { confirmButtonText: '认领', cancelButtonText: '取消', type: 'info' })
  try {
    const res = await request.put('/api/repair/claim/' + row.id)
    if (res.code === '200') {
      ElMessage.success('已认领')
      loadData()
      loadStats()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

const showAssignDialog = (row) => {
  assignOrderId.value = row.id
  assignUserId.value = null
  showAssign.value = true
}

const handleAssign = async () => {
  if (!assignUserId.value) {
    ElMessage.warning('请选择运维人员')
    return
  }
  actionLoading.value = true
  try {
    const user = userList.value.find(u => u.id === assignUserId.value)
    const res = await request.put('/api/repair/assign/' + assignOrderId.value, {
      assignUserId: assignUserId.value,
      assignUserName: user ? user.nickname : ''
    })
    if (res.code === '200') {
      ElMessage.success('分配成功')
      showAssign.value = false
      loadData()
      loadStats()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    actionLoading.value = false
  }
}

const showProcessDialog = async (row) => {
  processOrderId.value = row.id
  processForm.status = ''
  processForm.reply = ''
  processForm.internalNote = ''
  try {
    const res = await request.get('/api/repair/detail/' + row.id)
    if (res.code === '200') {
      currentOrder.value = res.data.order
      orderLogs.value = res.data.logs || []
    }
  } catch (e) {}
  showProcess.value = true
}

const handleProcess = async () => {
  if (!processForm.status) {
    ElMessage.warning('请选择变更状态')
    return
  }
  actionLoading.value = true
  try {
    const res = await request.put('/api/repair/process/' + processOrderId.value, {
      status: processForm.status,
      reply: processForm.reply,
      internalNote: processForm.internalNote
    })
    if (res.code === '200') {
      ElMessage.success('处理成功')
      showProcess.value = false
      drawerVisible.value = false
      loadData()
      loadStats()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    actionLoading.value = false
  }
}

const getStatusType = (status) => {
  const map = { '待处理': 'danger', '处理中': 'warning', '待确认': 'primary', '已解决': 'success', '已关闭': 'info' }
  return map[status] || 'info'
}

const getUrgencyType = (urgency) => {
  const map = { '一般': 'info', '紧急': 'warning', '非常紧急': 'danger' }
  return map[urgency] || 'info'
}

const getLogType = (action) => {
  const map = { '提交': 'primary', '认领': 'warning', '分配': 'warning', '处理': 'warning', '回复': 'success', '确认': 'success', '退回': 'danger', '追加': 'primary', '内部备注': 'info', '评价': 'success' }
  return map[action] || 'primary'
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

onMounted(() => {
  loadData()
  loadStats()
  loadUsers()
})
</script>

<style scoped>
.repair-container { padding: 20px; min-height: calc(100vh - 120px); background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%); }
.om-card { border-radius: 12px; }
.om-card :deep(.el-card__header) { padding: 16px 24px; border-bottom: 2px solid #e6a23c; background: linear-gradient(90deg, #fdf6ec 0%, #fff 100%); }
.card-header { display: flex; align-items: center; justify-content: space-between; }
.header-left { display: flex; align-items: center; gap: 8px; }
.header-icon { font-size: 20px; color: #e6a23c; }
.header-title { font-size: 16px; font-weight: 600; color: #303133; }
.header-stats { display: flex; gap: 8px; }
.filter-bar { display: flex; gap: 12px; margin-bottom: 16px; align-items: center; flex-wrap: wrap; }
.order-table { cursor: pointer; }
.order-no { font-family: monospace; color: #409eff; font-weight: 500; }
.pagination-wrapper { display: flex; justify-content: flex-end; margin-top: 16px; }
.detail-section { padding: 0 4px; }
.detail-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; }
.detail-header h3 { margin: 0; font-size: 18px; color: #303133; }
.detail-tags { display: flex; gap: 8px; }
.desc-text { white-space: pre-wrap; line-height: 1.6; color: #606266; }
.attachment-section { margin-top: 16px; }
.attachment-section h4, .log-section h4 { margin: 0 0 12px; font-size: 14px; color: #303133; }
.attachment-list { display: flex; flex-wrap: wrap; gap: 12px; }
.log-item { padding: 4px 0; }
.log-header { display: flex; align-items: center; gap: 8px; margin-bottom: 4px; }
.log-operator { font-size: 12px; color: #909399; }
.log-content { font-size: 13px; color: #606266; line-height: 1.5; }
</style>
