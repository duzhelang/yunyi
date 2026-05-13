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

          <transition name="detail-slide">
            <div v-if="expandedIds.has(item.id)" class="detail-panel" @click.stop>
              <div class="detail-section">
                <div class="detail-section-title">🩺 核心医疗指标</div>
                <div class="detail-grid">
                  <div class="detail-cell">
                    <span class="detail-label">空腹血糖</span>
                    <span class="detail-value">{{ item.glucose ?? '-' }} mg/dL</span>
                  </div>
                  <div class="detail-cell">
                    <span class="detail-label">血压</span>
                    <span class="detail-value">{{ item.bloodPressure ?? '-' }} mmHg</span>
                  </div>
                  <div class="detail-cell">
                    <span class="detail-label">胰岛素</span>
                    <span class="detail-value">{{ item.insulin ?? '-' }} mU/L</span>
                  </div>
                  <div class="detail-cell">
                    <span class="detail-label">皮褶厚度</span>
                    <span class="detail-value">{{ item.skinThickness ?? '-' }} mm</span>
                  </div>
                  <div class="detail-cell">
                    <span class="detail-label">糖尿病谱系函数</span>
                    <span class="detail-value">{{ item.diabetesPedigreeFunction ?? '-' }}</span>
                  </div>
                  <div class="detail-cell">
                    <span class="detail-label">怀孕次数</span>
                    <span class="detail-value">{{ item.pregnancies ?? '-' }}</span>
                  </div>
                </div>
              </div>
              <div class="detail-section">
                <div class="detail-section-title">📋 基本信息</div>
                <div class="detail-grid">
                  <div class="detail-cell">
                    <span class="detail-label">性别</span>
                    <span class="detail-value">{{ item.gender || '-' }}</span>
                  </div>
                  <div class="detail-cell">
                    <span class="detail-label">年龄</span>
                    <span class="detail-value">{{ item.age ?? '-' }} 岁</span>
                  </div>
                  <div class="detail-cell">
                    <span class="detail-label">身高</span>
                    <span class="detail-value">{{ item.height ?? '-' }} cm</span>
                  </div>
                  <div class="detail-cell">
                    <span class="detail-label">体重</span>
                    <span class="detail-value">{{ item.weight ?? '-' }} kg</span>
                  </div>
                  <div class="detail-cell">
                    <span class="detail-label">BMI</span>
                    <span class="detail-value">{{ item.bmi ?? '-' }}</span>
                  </div>
                </div>
              </div>
              <div class="detail-section">
                <div class="detail-section-title">🏃 生活方式</div>
                <div class="detail-grid">
                  <div class="detail-cell">
                    <span class="detail-label">运动频率</span>
                    <span class="detail-value">{{ formatExercise(item.exerciseFrequency) }}</span>
                  </div>
                  <div class="detail-cell">
                    <span class="detail-label">饮食习惯</span>
                    <span class="detail-value">{{ formatDiet(item.dietHabit) }}</span>
                  </div>
                  <div class="detail-cell">
                    <span class="detail-label">吸烟</span>
                    <span class="detail-value">{{ item.smoking || '-' }}</span>
                  </div>
                  <div class="detail-cell">
                    <span class="detail-label">饮酒</span>
                    <span class="detail-value">{{ item.drinking || '-' }}</span>
                  </div>
                </div>
              </div>
              <div v-if="item.symptoms" class="detail-section">
                <div class="detail-section-title">📝 自觉症状</div>
                <div class="detail-symptoms">{{ item.symptoms }}</div>
              </div>
              <div v-if="item.riskLevel" class="detail-section">
                <div class="detail-section-title">📊 预测结果</div>
                <div class="detail-grid">
                  <div class="detail-cell">
                    <span class="detail-label">风险等级</span>
                    <span class="detail-value" :class="'risk-' + item.riskLevel">{{ formatRisk(item.riskLevel) }}</span>
                  </div>
                  <div class="detail-cell">
                    <span class="detail-label">患病概率</span>
                    <span class="detail-value">{{ item.riskProbability != null ? item.riskProbability.toFixed(1) + '%' : '-' }}</span>
                  </div>
                </div>
              </div>
              <div v-if="item.aiAdvice" class="detail-section">
                <div class="detail-section-title">🤖 AI 健康建议</div>
                <div class="detail-advice">{{ item.aiAdvice }}</div>
              </div>
            </div>
          </transition>

          <div class="item-actions">
            <div class="action-left">
              <el-button link size="small" type="primary" @click.stop="$emit('load-to-form', item)">
                <el-icon><Edit /></el-icon> 载入
              </el-button>
              <el-button link size="small" type="info" @click.stop="toggleDetail(item.id)">
                <el-icon><component :is="expandedIds.has(item.id) ? ArrowUp : ArrowDown" /></el-icon>
                {{ expandedIds.has(item.id) ? '收起' : '详情' }}
              </el-button>
            </div>
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
import { reactive } from 'vue'
import {
  Clock, Document, Sugar, ScaleToOriginal, User,
  Check, Edit, Delete, ArrowDown, ArrowUp
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

const expandedIds = reactive(new Set())

function toggleDetail(id) {
  if (expandedIds.has(id)) {
    expandedIds.delete(id)
  } else {
    expandedIds.add(id)
  }
}

function formatTime(ts) {
  if (!ts) return ''
  const d = new Date(ts)
  return `${d.getMonth() + 1}/${d.getDate()} ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}

const EXERCISE_MAP = { '1': '很少运动', '2': '偶尔运动', '3': '经常运动', '4': '每天运动' }
const DIET_MAP = { '1': '不规律', '2': '一般', '3': '均衡饮食', '4': '健康饮食' }
const RISK_MAP = { 'low': '低风险', 'medium': '中风险', 'high': '高风险' }

function formatExercise(val) {
  return EXERCISE_MAP[val] || val || '-'
}

function formatDiet(val) {
  return DIET_MAP[val] || val || '-'
}

function formatRisk(val) {
  return RISK_MAP[val] || val || '-'
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
.action-left {
  display: flex;
  align-items: center;
  gap: 4px;
}

.detail-slide-enter-active,
.detail-slide-leave-active {
  transition: all 0.25s ease;
  overflow: hidden;
}
.detail-slide-enter-from,
.detail-slide-leave-to {
  opacity: 0;
  max-height: 0;
}
.detail-slide-enter-to,
.detail-slide-leave-from {
  opacity: 1;
  max-height: 600px;
}

.detail-panel {
  margin: 8px 0;
  padding: 10px 12px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e8ecf1;
}
.detail-section {
  margin-bottom: 10px;
}
.detail-section:last-child {
  margin-bottom: 0;
}
.detail-section-title {
  font-size: 12px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 6px;
  padding-bottom: 4px;
  border-bottom: 1px dashed #e0e6ed;
}
.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 4px 12px;
}
.detail-cell {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 2px 0;
}
.detail-label {
  font-size: 12px;
  color: #909399;
  flex-shrink: 0;
}
.detail-value {
  font-size: 12px;
  color: #303133;
  font-weight: 500;
  text-align: right;
}
.detail-value.risk-low {
  color: #67c23a;
}
.detail-value.risk-medium {
  color: #e6a23c;
}
.detail-value.risk-high {
  color: #f56c6c;
}
.detail-symptoms {
  font-size: 12px;
  color: #606266;
  line-height: 1.6;
  padding: 4px 0;
}
.detail-advice {
  font-size: 12px;
  color: #606266;
  line-height: 1.6;
  padding: 4px 0;
  white-space: pre-wrap;
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
