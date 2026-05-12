<template>
  <el-card class="checkin-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <el-icon class="header-icon"><Calendar /></el-icon>
        <span>今日打卡</span>
        <el-tag type="success" size="small">{{ progressPercent }}%</el-tag>
      </div>
    </template>
    <div class="checkin-list">
      <div
        v-for="(task, idx) in checkList"
        :key="idx"
        class="checkin-item"
        :class="{ done: task.done }"
        @click="emit('toggle', idx)"
      >
        <el-icon class="check-icon" :class="task.done ? 'is-checked' : ''">
          <CircleCheck />
        </el-icon>
        <span>{{ task.text }}</span>
      </div>
    </div>
    <el-progress :percentage="progressPercent" :stroke-width="10" color="#67C23A" :show-text="false" />
  </el-card>
</template>

<script setup>
import { Calendar, CircleCheck } from '@element-plus/icons-vue'

const props = defineProps({
  checkList: {
    type: Array,
    required: true
  },
  progressPercent: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['toggle'])
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 15px;
  color: #1D2129;
}
.header-icon {
  font-size: 18px;
  color: #409EFF;
}
.checkin-list {
  margin-bottom: 12px;
}
.checkin-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
  cursor: pointer;
  transition: color 0.2s;
  color: #606266;
}
.checkin-item:hover {
  color: #409EFF;
}
.checkin-item.done {
  color: #67C23A;
}
.check-icon {
  font-size: 18px;
}
.check-icon.is-checked {
  color: #67C23A;
}
</style>
