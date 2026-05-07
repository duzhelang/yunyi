<template>
  <div class="repair-container">
    <el-card class="receipt-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon class="header-icon"><Checked /></el-icon>
            <span class="header-title">信息回执</span>
          </div>
          <el-tag type="info" size="small">请确认运维人员的处理结果</el-tag>
        </div>
      </template>

      <div v-if="pendingOrders.length > 0">
        <div class="section-title">
          <el-icon><Bell /></el-icon>
          <span>待确认工单 ({{ pendingOrders.length }})</span>
        </div>
        <div class="pending-list">
          <el-card v-for="order in pendingOrders" :key="order.id" class="pending-item" shadow="hover">
            <div class="pending-header">
              <div class="pending-info">
                <span class="order-no">{{ order.orderNo }}</span>
                <span class="order-title">{{ order.title }}</span>
              </div>
              <el-tag type="primary" effect="dark" size="small">待确认</el-tag>
            </div>
            <div class="pending-meta">
              <span><el-icon><Warning /></el-icon> {{ order.faultType }} / {{ order.faultSubType }}</span>
              <span><el-icon><Clock /></el-icon> 提交于 {{ formatTime(order.createTime) }}</span>
              <span v-if="order.assignUserName"><el-icon><User /></el-icon> 处理人：{{ order.assignUserName }}</span>
            </div>
            <div class="pending-desc">{{ order.description }}</div>
            <div class="pending-actions">
              <el-button type="success" @click="handleConfirm(order)">
                <el-icon><CircleCheck /></el-icon>
                问题已解决
              </el-button>
              <el-button type="warning" @click="openReopenDialog(order)">
                <el-icon><RefreshRight /></el-icon>
                未解决，重新处理
              </el-button>
              <el-button type="primary" link @click="showDetail(order)">查看详情</el-button>
            </div>
          </el-card>
        </div>
      </div>
      <el-empty v-else description="暂无待确认的工单" :image-size="100">
        <template #image>
          <el-icon style="font-size: 60px; color: #67c23a;"><CircleCheck /></el-icon>
        </template>
      </el-empty>
    </el-card>

    <el-card class="history-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon class="header-icon"><Clock /></el-icon>
            <span class="header-title">回执历史</span>
          </div>
        </div>
      </template>
      <el-table :data="historyOrders" stripe v-loading="historyLoading">
        <el-table-column prop="orderNo" label="工单编号" width="160" />
        <el-table-column prop="title" label="标题" show-overflow-tooltip min-width="180" />
        <el-table-column prop="faultType" label="故障类型" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small" effect="plain">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="评价" width="140">
          <template #default="{ row }">
            <template v-if="row._evaluation">
              <el-rate :model-value="row._evaluation.rating" disabled size="small" />
            </template>
            <span v-else class="no-eval">未评价</span>
          </template>
        </el-table-column>
        <el-table-column label="最后更新" width="170">
          <template #default="{ row }">{{ formatTime(row.updateTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="showDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="historyPageNum" v-model:page-size="historyPageSize" :page-sizes="[5, 10, 20]" :total="historyTotal" layout="total, sizes, prev, pager, next" @size-change="loadHistory" @current-change="loadHistory" />
      </div>
    </el-card>

    <el-dialog v-model="showReopen" title="申请重新处理" width="450px">
      <p class="reopen-tip">请说明问题未解决的原因，运维人员将重新处理：</p>
      <el-input v-model="reopenReason" type="textarea" :rows="4" placeholder="请详细描述问题仍然存在的表现..." maxlength="200" show-word-limit />
      <template #footer>
        <el-button @click="showReopen = false">取消</el-button>
        <el-button type="warning" :loading="actionLoading" @click="handleReopen">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showEvaluation" title="满意度评价" width="480px">
      <div class="eval-section">
        <p class="eval-tip">请对本次运维服务进行评价：</p>
        <div class="eval-stars">
          <el-rate v-model="evalRating" :colors="['#99A9BF', '#F7BA2A', '#FF9900']" show-text :texts="['很差', '较差', '一般', '满意', '非常满意']" size="large" />
        </div>
        <el-input v-model="evalComment" type="textarea" :rows="3" placeholder="请输入评价内容（选填）..." maxlength="200" show-word-limit style="margin-top: 16px;" />
      </div>
      <template #footer>
        <el-button @click="showEvaluation = false">稍后评价</el-button>
        <el-button type="primary" :loading="actionLoading" @click="submitEvaluation">提交评价</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="drawerVisible" title="工单详情" size="600px" direction="rtl" :destroy-on-close="true">
      <template v-if="currentOrder">
        <div class="detail-section">
          <div class="detail-header">
            <h3>{{ currentOrder.title }}</h3>
            <el-tag :type="getStatusType(currentOrder.status)" effect="dark">{{ currentOrder.status }}</el-tag>
          </div>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="工单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="故障类型">{{ currentOrder.faultType }} / {{ currentOrder.faultSubType }}</el-descriptions-item>
            <el-descriptions-item label="涉及模块">{{ currentOrder.pagePath }}</el-descriptions-item>
            <el-descriptions-item label="紧急程度">{{ currentOrder.urgency }}</el-descriptions-item>
            <el-descriptions-item label="提交时间">{{ formatTime(currentOrder.createTime) }}</el-descriptions-item>
            <el-descriptions-item label="最后更新">{{ formatTime(currentOrder.updateTime) }}</el-descriptions-item>
            <el-descriptions-item label="问题描述" :span="2"><div class="desc-text">{{ currentOrder.description }}</div></el-descriptions-item>
            <el-descriptions-item v-if="currentOrder.assignUserName" label="处理人" :span="2">{{ currentOrder.assignUserName }}</el-descriptions-item>
          </el-descriptions>
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
                </div>
                <div class="log-content">{{ log.content }}</div>
              </div>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无处理记录" :image-size="80" />
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Checked, Bell, Warning, Clock, User, CircleCheck, RefreshRight } from '@element-plus/icons-vue'
import request from '@/utils/request'

const actionLoading = ref(false)
const pendingOrders = ref([])
const historyOrders = ref([])
const historyLoading = ref(false)
const historyPageNum = ref(1)
const historyPageSize = ref(10)
const historyTotal = ref(0)

const showReopen = ref(false)
const reopenReason = ref('')
const reopenOrderId = ref(null)

const showEvaluation = ref(false)
const evalOrderId = ref(null)
const evalRating = ref(5)
const evalComment = ref('')

const drawerVisible = ref(false)
const currentOrder = ref(null)
const orderLogs = ref([])

const loadPending = async () => {
  try {
    const res = await request.get('/api/repair/pending/confirm', { params: { pageNum: 1, pageSize: 50 } })
    if (res.code === '200') pendingOrders.value = res.data.records || []
  } catch (e) {}
}

const loadHistory = async () => {
  historyLoading.value = true
  try {
    const res = await request.get('/api/repair/my/list', {
      params: { pageNum: historyPageNum.value, pageSize: historyPageSize.value, status: '', keyword: '' }
    })
    if (res.code === '200') {
      const records = res.data.records || []
      for (const order of records) {
        if (order.status === '已解决' || order.status === '已关闭') {
          try {
            const evalRes = await request.get('/api/repair/evaluation/' + order.id)
            if (evalRes.code === '200' && evalRes.data) {
              order._evaluation = evalRes.data
            }
          } catch (e) {}
        }
      }
      historyOrders.value = records
      historyTotal.value = res.data.total || 0
    }
  } catch (e) {
    ElMessage.error('加载数据失败')
  } finally {
    historyLoading.value = false
  }
}

const handleConfirm = async (order) => {
  await ElMessageBox.confirm('确认问题已解决？确认后工单将标记为已解决。', '确认操作', { confirmButtonText: '确认解决', cancelButtonText: '取消', type: 'success' })
  actionLoading.value = true
  try {
    const res = await request.put('/api/repair/confirm/' + order.id)
    if (res.code === '200') {
      ElMessage.success('已确认问题解决')
      evalOrderId.value = order.id
      evalRating.value = 5
      evalComment.value = ''
      showEvaluation.value = true
      loadPending()
      loadHistory()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  } finally {
    actionLoading.value = false
  }
}

const openReopenDialog = (order) => {
  reopenOrderId.value = order.id
  reopenReason.value = ''
  showReopen.value = true
}

const handleReopen = async () => {
  if (!reopenReason.value.trim()) {
    ElMessage.warning('请填写原因')
    return
  }
  actionLoading.value = true
  try {
    const res = await request.put('/api/repair/reopen/' + reopenOrderId.value, { reason: reopenReason.value })
    if (res.code === '200') {
      ElMessage.success('已申请重新处理')
      showReopen.value = false
      loadPending()
      loadHistory()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    actionLoading.value = false
  }
}

const submitEvaluation = async () => {
  actionLoading.value = true
  try {
    const res = await request.put('/api/repair/evaluate/' + evalOrderId.value, {
      rating: evalRating.value,
      comment: evalComment.value
    })
    if (res.code === '200') {
      ElMessage.success('评价提交成功，感谢您的反馈')
      showEvaluation.value = false
      loadHistory()
    } else {
      ElMessage.error(res.msg || '评价失败')
    }
  } catch (e) {
    ElMessage.error('评价失败')
  } finally {
    actionLoading.value = false
  }
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

const getStatusType = (status) => {
  const map = { '待处理': 'danger', '处理中': 'warning', '待确认': 'primary', '已解决': 'success', '已关闭': 'info' }
  return map[status] || 'info'
}

const getLogType = (action) => {
  const map = { '提交': 'primary', '认领': 'warning', '分配': 'warning', '处理': 'warning', '回复': 'success', '确认': 'success', '退回': 'danger', '追加': 'primary', '评价': 'success' }
  return map[action] || 'primary'
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

onMounted(() => {
  loadPending()
  loadHistory()
})
</script>

<style scoped>
.repair-container { padding: 20px; min-height: calc(100vh - 120px); background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%); }
.receipt-card { border-radius: 12px; margin-bottom: 20px; }
.receipt-card :deep(.el-card__header) { padding: 16px 24px; border-bottom: 2px solid #67c23a; background: linear-gradient(90deg, #f0f9eb 0%, #fff 100%); }
.history-card { border-radius: 12px; }
.history-card :deep(.el-card__header) { padding: 16px 24px; border-bottom: 2px solid #409eff; background: linear-gradient(90deg, #ecf5ff 0%, #fff 100%); }
.card-header { display: flex; align-items: center; justify-content: space-between; }
.header-left { display: flex; align-items: center; gap: 8px; }
.header-icon { font-size: 20px; color: #67c23a; }
.history-card .header-icon { color: #409eff; }
.header-title { font-size: 16px; font-weight: 600; color: #303133; }
.section-title { display: flex; align-items: center; gap: 6px; font-size: 15px; font-weight: 600; color: #303133; margin-bottom: 16px; }
.section-title .el-icon { color: #e6a23c; }
.pending-list { display: flex; flex-direction: column; gap: 12px; }
.pending-item { border-left: 4px solid #409eff; }
.pending-item :deep(.el-card__body) { padding: 16px 20px; }
.pending-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.pending-info { display: flex; align-items: center; gap: 12px; }
.order-no { font-family: monospace; color: #409eff; font-weight: 600; font-size: 13px; }
.order-title { font-size: 15px; font-weight: 500; color: #303133; }
.pending-meta { display: flex; gap: 20px; font-size: 12px; color: #909399; margin-bottom: 8px; }
.pending-meta .el-icon { margin-right: 2px; }
.pending-desc { font-size: 13px; color: #606266; line-height: 1.5; margin-bottom: 12px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.pending-actions { display: flex; gap: 12px; align-items: center; }
.no-eval { font-size: 12px; color: #c0c4cc; }
.pagination-wrapper { display: flex; justify-content: flex-end; margin-top: 16px; }
.reopen-tip { color: #606266; margin-bottom: 12px; }
.eval-section { text-align: center; }
.eval-tip { color: #606266; margin-bottom: 16px; font-size: 15px; }
.eval-stars { display: flex; justify-content: center; }
.detail-section { padding: 0 4px; }
.detail-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; }
.detail-header h3 { margin: 0; font-size: 18px; color: #303133; }
.desc-text { white-space: pre-wrap; line-height: 1.6; color: #606266; }
.log-section h4 { margin: 0 0 12px; font-size: 14px; color: #303133; }
.log-item { padding: 4px 0; }
.log-header { display: flex; align-items: center; gap: 8px; margin-bottom: 4px; }
.log-operator { font-size: 12px; color: #909399; }
.log-content { font-size: 13px; color: #606266; line-height: 1.5; }
</style>
