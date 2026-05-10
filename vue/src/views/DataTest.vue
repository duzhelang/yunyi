<template>
  <div class="data-test-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>数据采集日志</span>
          <div class="header-actions">
            <el-input
              v-model="keyword"
              placeholder="搜索文件名..."
              clearable
              style="width: 240px; margin-right: 12px"
              @clear="loadLogs"
              @keyup.enter="loadLogs"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="loadLogs">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="logList" border stripe v-loading="loading" empty-text="暂无采集日志">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="name" label="文件名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="type" label="文件类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ row.type || '未知' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="文件大小" width="110" align="center">
          <template #default="{ row }">
            {{ row.size ? (row.size / 1024).toFixed(1) + ' KB' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.category === 'train' ? 'success' : 'warning'" size="small">
              {{ row.category === 'train' ? '训练集' : row.category === 'test' ? '测试集' : row.category || '未分类' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '已入库' : '待处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="上传时间" width="180" align="center" />
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" size="small" link @click="handleDelete(row)">
              删除
            </el-button>
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
          @size-change="loadLogs"
          @current-change="loadLogs"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import request from '@/utils/request'

const keyword = ref('')
const logList = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadLogs = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/dataset/list', {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        keyword: keyword.value || undefined
      }
    })
    if (res.code === '200') {
      logList.value = res.data?.records || []
      total.value = res.data?.total || 0
    } else {
      ElMessage.error(res.msg || '加载失败')
    }
  } catch {
    ElMessage.error('网络请求失败')
  } finally {
    loading.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除文件 "${row.name}"？`, '确认删除', { type: 'warning' })
    const res = await request.delete(`/api/dataset/${row.id}`)
    if (res.code === '200') {
      ElMessage.success('删除成功')
      loadLogs()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch {}
}

onMounted(() => {
  loadLogs()
})
</script>

<style scoped>
.data-test-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
