<template>
  <el-card class="history-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <el-icon class="header-icon"><Clock /></el-icon>
        <span>自查历史</span>
        <span class="history-count">{{ historyList.length }} 条记录</span>
      </div>
    </template>
    <div class="history-list">
      <div v-if="historyList.length === 0" class="empty-state">
        <el-icon class="empty-icon"><Document /></el-icon>
        <p>暂无历史记录</p>
      </div>
      <el-scrollbar height="350px" v-else>
        <div
          v-for="item in historyList"
          :key="item.id"
          class="history-item"
          :class="{ 'is-active': selectedHistoryId === item.id }"
          @click="$emit('select', item)"
        >
          <div class="item-header">
            <el-tag :type="statusMap[item.status]?.type" size="small" effect="dark">
              {{ statusMap[item.status]?.text }}
            </el-tag>
            <span class="time">{{ formatTime(item.createTime) }}</span>
          </div>
          <div class="item-body">
            <div class="item-metrics">
              <span class="metric">
                <el-icon><Sugar /></el-icon>
                {{ item.glucose || '-' }}
              </span>
              <span class="metric">
                <el-icon><ScaleToOriginal /></el-icon>
                {{ item.bmi || '-' }}
              </span>
              <span class="metric">
                <el-icon><User /></el-icon>
                {{ item.age || '-' }}岁
              </span>
            </div>
          </div>
          <div v-if="item.status === 'DONE' && item.diagnosisResult" class="diagnosis-box">
            <el-icon><Check /></el-icon>
            {{ item.diagnosisResult }}
          </div>
          <div class="item-actions">
            <el-button link size="small" type="primary" @click.stop="$emit('load-to-form', item)">
              <el-icon><Edit /></el-icon> 载入
            </el-button>
            <div class="action-right">
              <el-button
                type="primary"
                size="small"
                round
                @click.stop="$emit('submit-doctor', item.id)"
                :loading="submittingId === item.id"
                :disabled="item.status === 'PENDING'"
              >
                {{ item.status === 'PENDING' ? '等待中' : '咨询' }}
              </el-button>
              <el-popconfirm title="确认删除此记录？" confirm-button-text="删除" @confirm="$emit('delete', item.id)">
                <template #reference>
                  <el-button type="danger" size="small" plain :loading="deletingId === item.id" @click.stop>
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </template>
              </el-popconfirm>
            </div>
          </div>
        </div>
      </el-scrollbar>
    </div>
  </el-card>
</template>

<script setup>
import {
  Clock, Document, Sugar, ScaleToOriginal, User,
  Check, Edit, Delete
} from '@element-plus/icons-vue'

const props = defineProps({
  historyList: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  selectedHistoryId: {
    type: Number,
    default: null
  },
  submitting: {
    type: Boolean,
    default: false
  },
  submittingId: {
    type: Number,
    default: null
  },
  deletingId: {
    type: Number,
    default: null
  },
  statusMap: {
    type: Object,
    default: () => ({
      DONE: { text: '已完成', type: 'success' },
      PENDING: { text: '诊断中', type: 'warning' },
      SAVED: { text: '已保存', type: 'info' }
    })
  }
})

defineEmits(['select', 'load-to-form', 'submit-doctor', 'delete'])

/**
 * 格式化时间戳为短格式
 * @param {string|number} ts - 时间戳
 * @returns {string} 格式化后的时间字符串
 */
function formatTime(ts) {
  if (!ts) return ''
  const d = new Date(ts)
  return `${d.getMonth() + 1}/${d.getDate()} ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}
</script>

<style scoped>
.history-card {
  margin-bottom: 20px;
}
.history-list {
  position: relative;
}
.history-count {
  margin-left: auto;
  font-size: 12px;
  color: #909399;
  font-weight: normal;
}
.history-item {
  padding: 12px;
  margin-bottom: 10px;
  background: #fafafa;
  border-radius: 10px;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.2s;
}
.history-item:hover {
  background: #f0f2f5;
  border-color: #d9d9d9;
}
.history-item.is-active {
  background: linear-gradient(135deg, #ecf5ff, #f0f9eb);
  border-color: #409EFF;
}
.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.time {
  font-size: 12px;
  color: #909399;
}
.item-body {
  margin-bottom: 8px;
}
.item-metrics {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.metric {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #606266;
}
.metric .el-icon {
  color: #409EFF;
}
.diagnosis-box {
  background: #f0f9eb;
  padding: 8px 10px;
  border-radius: 6px;
  font-size: 12px;
  margin-bottom: 8px;
  color: #2d6a2d;
  display: flex;
  align-items: center;
  gap: 6px;
}
.item-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  border-top: 1px dashed #eee;
  padding-top: 8px;
}
.action-right {
  display: flex;
  align-items: center;
  gap: 8px;
}
.empty-state {
  text-align: center;
  padding: 40px;
  color: #909399;
}
.empty-icon {
  font-size: 40px;
  color: #c0c4cc;
  margin-bottom: 10px;
}
</style>
