<template>
  <div class="repair-container">
    <el-card class="list-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon class="header-icon"><Document /></el-icon>
            <span class="header-title">我的报修工单</span>
          </div>
          <el-button type="primary" @click="goToSubmit">
            <el-icon><Plus /></el-icon>
            新建报修
          </el-button>
        </div>
      </template>

      <div class="filter-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索工单编号或标题"
          style="width: 240px"
          clearable
          @keyup.enter="loadData"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="filterStatus" placeholder="工单状态" clearable style="width: 150px" @change="loadData">
          <el-option label="待处理" value="待处理" />
          <el-option label="处理中" value="处理中" />
          <el-option label="待确认" value="待确认" />
          <el-option label="已解决" value="已解决" />
          <el-option label="已关闭" value="已关闭" />
        </el-select>
        <el-button type="primary" @click="loadData">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
        <el-button @click="resetFilter">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </div>

      <el-table :data="tableData" stripe v-loading="loading" @row-click="showDetail" class="order-table">
        <el-table-column prop="orderNo" label="工单编号" width="160">
          <template #default="{ row }">
            <span class="order-no">{{ row.orderNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="故障标题" show-overflow-tooltip min-width="200" />
        <el-table-column prop="faultType" label="故障类型" width="120" />
        <el-table-column prop="faultSubType" label="子类型" width="130" show-overflow-tooltip />
        <el-table-column label="紧急程度" width="100">
          <template #default="{ row }">
            <el-tag :type="getUrgencyType(row.urgency)" size="small" effect="dark">
              {{ row.urgency }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small" effect="plain">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" width="170">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click.stop="showDetail(row)">
              <el-icon><View /></el-icon>
              详情
            </el-button>
            <el-button
              v-if="row.status === '已解决' || row.status === '已关闭'"
              type="danger"
              link
              size="small"
              @click.stop="handleDelete(row)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[5, 10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <el-drawer
      v-model="drawerVisible"
      title="工单详情"
      size="600px"
      direction="rtl"
      :destroy-on-close="true"
    >
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
            <el-descriptions-item label="提交时间">{{ formatTime(currentOrder.createTime) }}</el-descriptions-item>
            <el-descriptions-item label="最后更新">{{ formatTime(currentOrder.updateTime) }}</el-descriptions-item>
            <el-descriptions-item label="联系手机">{{ currentOrder.contactPhone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="联系邮箱">{{ currentOrder.contactEmail || '-' }}</el-descriptions-item>
            <el-descriptions-item label="接受远程协助" :span="2">
              <el-tag :type="currentOrder.acceptRemote ? 'success' : 'info'" size="small">
                {{ currentOrder.acceptRemote ? '是' : '否' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="问题描述" :span="2">
              <div class="desc-text">{{ currentOrder.description }}</div>
            </el-descriptions-item>
            <el-descriptions-item v-if="currentOrder.reproSteps" label="重现步骤" :span="2">
              <div class="desc-text">{{ currentOrder.reproSteps }}</div>
            </el-descriptions-item>
            <el-descriptions-item v-if="currentOrder.assignUserName" label="处理人" :span="2">
              {{ currentOrder.assignUserName }}
            </el-descriptions-item>
          </el-descriptions>

          <div v-if="currentOrder.attachments && currentOrder.attachments.length > 0" class="attachment-section">
            <h4>附件</h4>
            <div class="attachment-list">
              <div v-for="(url, idx) in currentOrder.attachments" :key="idx" class="attachment-item">
                <el-link type="primary" :href="url" target="_blank">
                  <el-icon><Paperclip /></el-icon>
                  附件 {{ idx + 1 }}
                </el-link>
              </div>
            </div>
          </div>
        </div>

        <el-divider />

        <div class="log-section">
          <h4>处理记录</h4>
          <el-timeline v-if="orderLogs.length > 0">
            <el-timeline-item
              v-for="log in orderLogs"
              :key="log.id"
              :timestamp="formatTime(log.createTime)"
              :type="getLogType(log.action)"
              placement="top"
            >
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

        <div v-if="currentOrder.status === '处理中'" class="action-section">
          <el-divider />
          <h4>追加描述</h4>
          <el-input
            v-model="appendContent"
            type="textarea"
            :rows="3"
            placeholder="请输入补充信息..."
            maxlength="300"
            show-word-limit
          />
          <div style="margin-top: 12px; text-align: right;">
            <el-button type="primary" :loading="actionLoading" @click="handleAppend">提交补充</el-button>
          </div>
        </div>

        <div v-if="currentOrder.status === '待确认'" class="action-section">
          <el-divider />
          <div class="confirm-actions">
            <el-button type="success" size="large" :loading="actionLoading" @click="handleConfirm">
              <el-icon><CircleCheck /></el-icon>
              问题已解决
            </el-button>
            <el-button type="warning" size="large" :loading="actionLoading" @click="showReopenDialog = true">
              <el-icon><RefreshRight /></el-icon>
              未解决，重新处理
            </el-button>
          </div>
        </div>
      </template>
    </el-drawer>

    <el-dialog v-model="showReopenDialog" title="申请重新处理" width="450px">
      <el-input
        v-model="reopenReason"
        type="textarea"
        :rows="4"
        placeholder="请说明问题未解决的原因..."
        maxlength="200"
        show-word-limit
      />
      <template #footer>
        <el-button @click="showReopenDialog = false">取消</el-button>
        <el-button type="warning" :loading="actionLoading" @click="handleReopen">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, Plus, Search, Refresh, View, Delete, Paperclip, CircleCheck, RefreshRight } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const actionLoading = ref(false)
const tableData = ref([])
const searchKeyword = ref('')
const filterStatus = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const drawerVisible = ref(false)
const currentOrder = ref(null)
const orderLogs = ref([])
const appendContent = ref('')
const showReopenDialog = ref(false)
const reopenReason = ref('')

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/repair/my/list', {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        status: filterStatus.value,
        keyword: searchKeyword.value
      }
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

const resetFilter = () => {
  searchKeyword.value = ''
  filterStatus.value = ''
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

const handleAppend = async () => {
  if (!appendContent.value.trim()) {
    ElMessage.warning('请输入补充信息')
    return
  }
  actionLoading.value = true
  try {
    const res = await request.put('/api/repair/append/' + currentOrder.value.id, {
      content: appendContent.value
    })
    if (res.code === '200') {
      ElMessage.success('补充信息已提交')
      appendContent.value = ''
      showDetail(currentOrder.value)
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    actionLoading.value = false
  }
}

const handleConfirm = async () => {
  await ElMessageBox.confirm('确认问题已解决？确认后将无法撤回。', '确认操作', {
    confirmButtonText: '确认解决',
    cancelButtonText: '取消',
    type: 'success'
  })
  actionLoading.value = true
  try {
    const res = await request.put('/api/repair/confirm/' + currentOrder.value.id)
    if (res.code === '200') {
      ElMessage.success('已确认问题解决')
      drawerVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  } finally {
    actionLoading.value = false
  }
}

const handleReopen = async () => {
  if (!reopenReason.value.trim()) {
    ElMessage.warning('请填写原因')
    return
  }
  actionLoading.value = true
  try {
    const res = await request.put('/api/repair/reopen/' + currentOrder.value.id, {
      reason: reopenReason.value
    })
    if (res.code === '200') {
      ElMessage.success('已申请重新处理')
      showReopenDialog.value = false
      reopenReason.value = ''
      drawerVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    actionLoading.value = false
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该工单？删除后不可恢复。', '确认删除', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  })
  try {
    const res = await request.delete('/api/repair/delete/' + row.id)
    if (res.code === '200') {
      ElMessage.success('已删除')
      loadData()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

const goToSubmit = () => {
  router.push('/send')
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
  const map = { '提交': 'primary', '认领': 'warning', '分配': 'warning', '处理': 'warning', '回复': 'success', '确认': 'success', '退回': 'danger', '追加': 'primary', '评价': 'success' }
  return map[action] || 'primary'
}

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  return d.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

watch(() => route.query.id, (newId) => {
  if (newId) {
    showDetail({ id: parseInt(newId) })
  }
}, { immediate: true })

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.repair-container {
  padding: 20px;
  min-height: calc(100vh - 120px);
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
}

.list-card {
  border-radius: 12px;
}

.list-card :deep(.el-card__header) {
  padding: 16px 24px;
  border-bottom: 2px solid #409eff;
  background: linear-gradient(90deg, #ecf5ff 0%, #fff 100%);
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

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  align-items: center;
}

.order-table {
  cursor: pointer;
}

.order-no {
  font-family: monospace;
  color: #409eff;
  font-weight: 500;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.detail-section {
  padding: 0 4px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.detail-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.detail-tags {
  display: flex;
  gap: 8px;
}

.desc-text {
  white-space: pre-wrap;
  line-height: 1.6;
  color: #606266;
}

.attachment-section {
  margin-top: 16px;
}

.attachment-section h4 {
  margin: 0 0 8px;
  font-size: 14px;
  color: #303133;
}

.attachment-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.log-section h4 {
  margin: 0 0 16px;
  font-size: 14px;
  color: #303133;
}

.log-item {
  padding: 4px 0;
}

.log-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.log-operator {
  font-size: 12px;
  color: #909399;
}

.log-content {
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
}

.action-section h4 {
  margin: 0 0 12px;
  font-size: 14px;
  color: #303133;
}

.confirm-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  padding: 16px 0;
}
</style>
