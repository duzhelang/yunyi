<template>
  <!-- 工具箱卡片 -->
  <el-card class="tool-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <el-icon class="header-icon"><Tools /></el-icon>
        <span>健康工具</span>
      </div>
    </template>
    <div class="tool-grid">
      <div class="tool-item" @click="$emit('open-tool', 'emergency')">
        <div class="icon-box bg-red"><el-icon><Warning /></el-icon></div>
        <div class="tool-info">
          <span class="tool-name">低血糖急救</span>
          <span class="tool-desc">急救处理指南</span>
        </div>
        <el-icon class="tool-arrow"><ArrowRight /></el-icon>
      </div>
      <div class="tool-item" @click="$emit('open-tool', 'carb-count')">
        <div class="icon-box bg-orange"><el-icon><Dish /></el-icon></div>
        <div class="tool-info">
          <span class="tool-name">碳水计数法</span>
          <span class="tool-desc">饮食控制助手</span>
        </div>
        <el-icon class="tool-arrow"><ArrowRight /></el-icon>
      </div>
      <div class="tool-item" @click="$emit('open-tool', 'foot-care')">
        <div class="icon-box bg-green"><el-icon><Cpu /></el-icon></div>
        <div class="tool-info">
          <span class="tool-name">足部护理</span>
          <span class="tool-desc">日常护理指南</span>
        </div>
        <el-icon class="tool-arrow"><ArrowRight /></el-icon>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { Tools, Warning, Dish, Cpu, ArrowRight } from '@element-plus/icons-vue'

defineEmits(['open-tool'])
</script>

<style scoped>
.tool-grid {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.tool-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
}
.tool-item:hover {
  background: #f5f7fa;
  border-color: #409EFF;
  transform: translateX(4px);
}
.icon-box {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}
.bg-red { background: linear-gradient(135deg, #f56c6c, #e64242); }
.bg-orange { background: linear-gradient(135deg, #e6a23c, #d19020); }
.bg-green { background: linear-gradient(135deg, #67c23a, #529b2e); }
.tool-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.tool-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}
.tool-desc {
  font-size: 12px;
  color: #909399;
}
.tool-arrow {
  color: #c0c4cc;
  font-size: 14px;
}
</style>
