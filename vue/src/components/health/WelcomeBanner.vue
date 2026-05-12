<template>
  <div class="welcome-banner">
    <h1>🏥 健康档案</h1>
    <p>智能自查 · 趋势追踪 · 医生协同</p>
  </div>
</template>

<script setup>
/**
 * 健康档案欢迎横幅组件
 * 带渐变色背景的横幅，显示标题和副标题
 */
</script>

<style scoped>
.welcome-banner {
  background: linear-gradient(135deg, #4080FF 0%, #52C41A 100%);
  border-radius: 16px;
  padding: 28px 32px;
  margin-bottom: 24px;
  box-shadow: 0 8px 24px rgba(64, 128, 255, 0.2);
  position: relative;
  overflow: hidden;
}

.welcome-banner::after {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 300px;
  height: 300px;
  background: rgba(255, 255, 255, 0.06);
  border-radius: 50%;
}

.welcome-banner h1 {
  margin: 0 0 6px 0;
  font-size: 24px;
  color: white;
  font-weight: 700;
  position: relative;
  z-index: 1;
}

.welcome-banner p {
  margin: 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
  letter-spacing: 2px;
  position: relative;
  z-index: 1;
}
</style>
