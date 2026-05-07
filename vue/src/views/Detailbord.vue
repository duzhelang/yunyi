<template>
  <div class="result-management">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <el-icon class="title-icon"><TrendCharts /></el-icon>
          预测结果管理
        </h1>
        <p class="page-desc">查看和管理模型预测结果数据</p>
      </div>
      <div class="header-stats">
        <div class="stat-card stat-total">
          <span class="stat-num">{{ total }}</span>
          <span class="stat-label">结果总数</span>
        </div>
        <div class="stat-card stat-today">
          <span class="stat-num">{{ todayCount }}</span>
          <span class="stat-label">今日预测</span>
        </div>
      </div>
    </div>

    <div class="result-panel">
      <div class="tab-toolbar">
        <div class="search-bar">
          <el-input v-model="searchResult" placeholder="搜索故障类型" prefix-icon="Search" clearable
                    @keyup.enter="loadData" style="width: 240px" />
          <el-button type="primary" :icon="Search" @click="loadData">搜索</el-button>
          <el-button :icon="Refresh" @click="reset">重置</el-button>
        </div>
        <div class="action-bar">
          <el-button v-if="selectedIds.length > 0" type="danger" plain @click="batchDelete">
            批量删除 ({{ selectedIds.length }})
          </el-button>
        </div>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading" @selection-change="handleSelectionChange"
                :header-cell-style="{ background: '#ecf5ff', color: '#409eff', fontWeight: 600 }">
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="testid" label="测试ID" width="120" align="center" />
        <el-table-column prop="result" label="故障类型" min-width="180">
          <template #default="{ row }">
            <el-tag :type="row.result === 1 ? 'danger' : 'success'" size="small" effect="plain">
              {{ row.result === 1 ? '故障' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="生成时间" width="180" align="center" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="goSend()">故障报修</el-button>
            <el-popconfirm title="确定删除该预测结果？" @confirm="deleteSingle(row.id)"
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
                       :total="total" :page-sizes="[10, 20, 50]"
                       v-model:current-page="pageNum" v-model:page-size="pageSize"
                       @current-change="loadData" @size-change="handleSizeChange" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, TrendCharts } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()

const tableData = ref([])
const total = ref(0)
const todayCount = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const searchResult = ref('')
const loading = ref(false)
const selectedIds = ref([])
const testfileId = route.query.id1

const loadData = () => {
  loading.value = true
  const params = {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    result: searchResult.value || ''
  }
  if (testfileId) {
    params.id = testfileId
  }
  request.get('/detailbord/page', { params }).then(res => {
    if (res.code === '200') {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    }
  }).catch(() => {
    ElMessage.error('加载预测结果失败')
  }).finally(() => {
    loading.value = false
  })
}

const loadTodayCount = () => {
  request.get('/detailbord/totle').then(res => {
    if (res.code === '200') {
      todayCount.value = res.data || 0
    }
  })
}

const handleSizeChange = () => {
  pageNum.value = 1
  loadData()
}

const handleSelectionChange = (rows) => {
  selectedIds.value = rows.map(r => r.id)
}

const reset = () => {
  searchResult.value = ''
  pageNum.value = 1
  loadData()
}

const deleteSingle = (id) => {
  request.delete(`/detailbord/${id}`).then(res => {
    if (res.code === '200') {
      ElMessage.success('删除成功')
      loadData()
      loadTodayCount()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  })
}

const batchDelete = () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先选择要删除的预测结果')
    return
  }
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 条预测结果吗？`, '批量删除确认', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    request.delete('/detailbord/batch', { data: selectedIds.value }).then(res => {
      if (res.code === '200') {
        ElMessage.success(`成功删除 ${selectedIds.value.length} 条预测结果`)
        selectedIds.value = []
        loadData()
        loadTodayCount()
      } else {
        ElMessage.error(res.msg || '批量删除失败')
      }
    })
  }).catch(() => {})
}

const goSend = () => {
  router.push('/Send')
}

onMounted(() => {
  loadData()
  loadTodayCount()
})
</script>

<style scoped>
.result-management {
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
  opacity: 0.8;
  margin: 0;
}

.header-stats {
  display: flex;
  gap: 16px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.2);
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
  opacity: 0.8;
}

.result-panel {
  background: #fff;
  border-radius: 12px;
  padding: 20px 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
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
</style>
