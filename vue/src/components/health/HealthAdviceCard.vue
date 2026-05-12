<template>
  <!-- 日常建议卡片 -->
  <el-card class="advice-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <el-icon class="header-icon"><Document /></el-icon>
        <span>个性化健康建议</span>
      </div>
    </template>
    <div class="advice-grid" v-if="advice && advice.length > 0">
      <div class="advice-item" v-for="(item, idx) in advice" :key="idx">
        <div class="advice-number">{{ idx + 1 }}</div>
        <div class="advice-content">
          <h4>{{ item.title }}</h4>
          <p>{{ item.content }}</p>
        </div>
      </div>
    </div>
    <div v-else class="empty-advice">
      <el-icon class="empty-icon"><ChatDotRound /></el-icon>
      <p>保存档案后将获取个性化健康建议</p>
    </div>
  </el-card>
</template>

<script setup>
import { Document, ChatDotRound } from '@element-plus/icons-vue'

defineProps({
  advice: {
    type: Array,
    default: null
  }
})
</script>

<style scoped>
.advice-card {
  margin-top: 16px;
}
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
.advice-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.advice-item {
  display: flex;
  gap: 12px;
  padding: 14px;
  background: linear-gradient(135deg, #f0f9eb, #ecf5ff);
  border-radius: 10px;
  border-left: 3px solid #67C23A;
  transition: all 0.2s;
}
.advice-item:hover {
  transform: translateX(4px);
}
.advice-number {
  flex-shrink: 0;
  width: 24px;
  height: 24px;
  background: #67C23A;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
}
.advice-content h4 {
  margin: 0 0 4px;
  font-size: 14px;
  color: #303133;
}
.advice-content p {
  margin: 0;
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
}
.empty-advice {
  text-align: center;
  padding: 30px;
  color: #909399;
}
.empty-icon {
  font-size: 40px;
  color: #c0c4cc;
  margin-bottom: 10px;
}
</style>
