<template>
  <div class="particle-demo">
    <MedicalParticles
      :show-controls="showControls"
      :initial-pattern="currentPattern"
      @pattern-change="onPatternChange"
      @performance-warning="onPerformanceWarning"
      ref="particlesRef"
    >
      <div class="demo-content">
        <div class="hero-section">
          <div class="logo-container">
            <div class="dna-icon">
              <span class="strand strand-1"></span>
              <span class="strand strand-2"></span>
              <span class="strand strand-3"></span>
            </div>
          </div>
          <h1 class="title">医学粒子动画演示</h1>
          <p class="subtitle">基于 tsparticles 的医学科学主题动态粒子背景</p>
          <div class="feature-tags">
            <span class="tag">DNA双螺旋</span>
            <span class="tag">分子结构</span>
            <span class="tag">神经网络</span>
          </div>
        </div>

        <div class="controls-section">
          <el-button-group>
            <el-button 
              :type="currentPattern === 'dna' ? 'primary' : 'default'"
              @click="switchPattern('dna')"
            >
              <el-icon><Connection /></el-icon>
              DNA双螺旋
            </el-button>
            <el-button 
              :type="currentPattern === 'molecule' ? 'primary' : 'default'"
              @click="switchPattern('molecule')"
            >
              <el-icon><Magnet /></el-icon>
              分子结构
            </el-button>
            <el-button 
              :type="currentPattern === 'neural' ? 'primary' : 'default'"
              @click="switchPattern('neural')"
            >
              <el-icon><Share /></el-icon>
              神经网络
            </el-button>
          </el-button-group>
          
          <el-button 
            type="info" 
            @click="toggleControls"
            class="toggle-controls-btn"
          >
            <el-icon><Setting /></el-icon>
            {{ showControls ? '隐藏控制面板' : '显示控制面板' }}
          </el-button>
        </div>

        <div class="info-section">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-card class="info-card" shadow="hover">
                <template #header>
                  <div class="card-header">
                    <el-icon><Connection /></el-icon>
                    <span>DNA双螺旋</span>
                  </div>
                </template>
                <div class="card-content">
                  <p>模拟DNA分子的双螺旋结构，粒子以螺旋状运动轨迹排列，形成独特的生物分子视觉效果。</p>
                  <ul>
                    <li>螺旋运动轨迹</li>
                    <li>碱基对连接效果</li>
                    <li>生物分子色彩方案</li>
                  </ul>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card class="info-card" shadow="hover">
                <template #header>
                  <div class="card-header">
                    <el-icon><Magnet /></el-icon>
                    <span>分子结构</span>
                  </div>
                </template>
                <div class="card-content">
                  <p>模拟化学分子的结构，粒子形成六边形苯环状排列，展现分子键和原子的连接关系。</p>
                  <ul>
                    <li>苯环结构排列</li>
                    <li>化学键连接</li>
                    <li>原子色彩标识</li>
                  </ul>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card class="info-card" shadow="hover">
                <template #header>
                  <div class="card-header">
                    <el-icon><Share /></el-icon>
                    <span>神经网络</span>
                  </div>
                </template>
                <div class="card-content">
                  <p>模拟神经元的树突和轴突连接，粒子形成网络状结构，展现神经信号的传递路径。</p>
                  <ul>
                    <li>神经元节点</li>
                    <li>突触连接</li>
                    <li>信号传递动画</li>
                  </ul>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <div class="performance-section">
          <el-card class="performance-card">
            <div class="performance-info">
              <span class="fps-label">FPS:</span>
              <span class="fps-value" :class="{ 'fps-low': fps < 30, 'fps-medium': fps >= 30 && fps < 50 }">
                {{ fps }}
              </span>
              <el-tag :type="fps >= 50 ? 'success' : fps >= 30 ? 'warning' : 'danger'" size="small">
                {{ fps >= 50 ? '流畅' : fps >= 30 ? '一般' : '卡顿' }}
              </el-tag>
            </div>
            <div class="performance-hint">
              <el-icon><InfoFilled /></el-icon>
              <span>如果出现卡顿，可以降低粒子密度或关闭部分动画效果</span>
            </div>
          </el-card>
        </div>
      </div>
    </MedicalParticles>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { Connection, Magnet, Share, Setting, InfoFilled } from '@element-plus/icons-vue'
import MedicalParticles from '@/components/MedicalParticles.vue'

const showControls = ref(true)
const currentPattern = ref('dna')
const fps = ref(60)
const particlesRef = ref(null)
let fpsTimer = null

const switchPattern = (pattern) => {
  currentPattern.value = pattern
}

const toggleControls = () => {
  showControls.value = !showControls.value
}

const onPatternChange = (pattern) => {
  console.log('切换到模式:', pattern)
}

const onPerformanceWarning = (currentFps) => {
  console.warn('性能警告: FPS低于30', currentFps)
}

const updateFPS = () => {
  if (particlesRef.value) {
    fps.value = particlesRef.value.getFPS()
  }
}

onMounted(() => {
  fpsTimer = setInterval(updateFPS, 1000)
})

onUnmounted(() => {
  if (fpsTimer) {
    clearInterval(fpsTimer)
  }
})
</script>

<style scoped>
.particle-demo {
  width: 100%;
  height: 100vh;
  position: relative;
  background: linear-gradient(135deg, #0a1428 0%, #1a2a4a 50%, #0d1f3c 100%);
}

.demo-content {
  position: relative;
  z-index: 2;
  padding: 40px;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.hero-section {
  text-align: center;
  margin-bottom: 60px;
}

.logo-container {
  margin-bottom: 30px;
}

.dna-icon {
  width: 80px;
  height: 80px;
  position: relative;
  margin: 0 auto;
}

.strand {
  position: absolute;
  width: 4px;
  height: 100%;
  background: linear-gradient(to bottom, #00d4ff, #00ff88, #00d4ff);
  border-radius: 2px;
  animation: dnaRotate 3s ease-in-out infinite;
}

.strand-1 {
  left: 50%;
  transform: translateX(-50%) rotate(-30deg);
}

.strand-2 {
  left: 50%;
  transform: translateX(-50%) rotate(30deg);
  animation-delay: -1s;
}

.strand-3 {
  left: 50%;
  transform: translateX(-50%) rotate(90deg);
  animation-delay: -2s;
}

@keyframes dnaRotate {
  0%, 100% {
    transform: translateX(-50%) rotate(-30deg);
    opacity: 0.8;
  }
  50% {
    transform: translateX(-50%) rotate(30deg);
    opacity: 1;
  }
}

.title {
  font-size: 48px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 20px 0;
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 0 40px rgba(0, 212, 255, 0.3);
}

.subtitle {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.7);
  margin: 0 0 30px 0;
}

.feature-tags {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
}

.tag {
  padding: 8px 20px;
  background: rgba(0, 212, 255, 0.15);
  border: 1px solid rgba(0, 212, 255, 0.3);
  border-radius: 20px;
  color: #00d4ff;
  font-size: 14px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.tag:hover {
  background: rgba(0, 212, 255, 0.25);
  border-color: #00d4ff;
  transform: translateY(-2px);
}

.controls-section {
  margin-bottom: 40px;
  display: flex;
  gap: 20px;
  align-items: center;
  flex-wrap: wrap;
  justify-content: center;
}

:deep(.el-button-group) {
  display: flex;
  gap: 4px;
}

:deep(.el-button-group .el-button) {
  margin-left: 0;
}

:deep(.el-button) {
  backdrop-filter: blur(10px);
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  box-shadow: 0 4px 15px rgba(0, 212, 255, 0.3);
}

:deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 212, 255, 0.4);
}

:deep(.el-button--default) {
  background: rgba(0, 212, 255, 0.1);
  border-color: rgba(0, 212, 255, 0.3);
  color: #00d4ff;
}

:deep(.el-button--default:hover) {
  background: rgba(0, 212, 255, 0.2);
  border-color: #00d4ff;
  color: #fff;
  transform: translateY(-2px);
}

.toggle-controls-btn {
  margin-left: 10px;
}

.info-section {
  width: 100%;
  max-width: 1200px;
  margin-bottom: 40px;
}

.info-card {
  background: rgba(10, 20, 40, 0.7);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  transition: all 0.3s ease;
  height: 100%;
}

.info-card:hover {
  transform: translateY(-5px);
  border-color: rgba(0, 212, 255, 0.5);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3), 
              0 0 20px rgba(0, 212, 255, 0.1);
}

:deep(.el-card__header) {
  background: rgba(0, 212, 255, 0.1);
  border-bottom: 1px solid rgba(0, 212, 255, 0.2);
  padding: 15px 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #00d4ff;
  font-weight: 600;
  font-size: 16px;
}

:deep(.el-card__body) {
  padding: 20px;
}

.card-content {
  color: rgba(255, 255, 255, 0.8);
}

.card-content p {
  margin: 0 0 15px 0;
  line-height: 1.6;
  font-size: 14px;
}

.card-content ul {
  margin: 0;
  padding-left: 20px;
  list-style: none;
}

.card-content li {
  position: relative;
  padding-left: 15px;
  margin-bottom: 8px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
}

.card-content li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: #00d4ff;
}

.performance-section {
  width: 100%;
  max-width: 600px;
}

.performance-card {
  background: rgba(10, 20, 40, 0.7);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
}

:deep(.el-card__body) {
  padding: 20px;
}

.performance-info {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.fps-label {
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
}

.fps-value {
  font-size: 32px;
  font-weight: 700;
  color: #00ff88;
  text-shadow: 0 0 20px rgba(0, 255, 136, 0.3);
}

.fps-low {
  color: #ff4757;
  text-shadow: 0 0 20px rgba(255, 71, 87, 0.3);
}

.fps-medium {
  color: #ffa502;
  text-shadow: 0 0 20px rgba(255, 165, 2, 0.3);
}

.performance-hint {
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.5);
  font-size: 12px;
}

.performance-hint .el-icon {
  color: #00d4ff;
}

@media (max-width: 768px) {
  .demo-content {
    padding: 20px;
  }
  
  .title {
    font-size: 32px;
  }
  
  .subtitle {
    font-size: 16px;
  }
  
  .controls-section {
    flex-direction: column;
    gap: 15px;
  }
  
  :deep(.el-button-group) {
    flex-direction: column;
  }
  
  .toggle-controls-btn {
    margin-left: 0;
  }
  
  .info-section .el-col {
    margin-bottom: 20px;
  }
}

@media (max-width: 480px) {
  .title {
    font-size: 24px;
  }
  
  .subtitle {
    font-size: 14px;
  }
  
  .feature-tags {
    gap: 8px;
  }
  
  .tag {
    padding: 6px 12px;
    font-size: 12px;
  }
  
  .fps-value {
    font-size: 24px;
  }
}

.particle-demo::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(ellipse at center, 
    rgba(0, 212, 255, 0.15) 0%, 
    rgba(0, 0, 0, 0) 70%);
  pointer-events: none;
  z-index: 0;
}

.particle-demo::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 150px;
  background: linear-gradient(to top, 
    rgba(10, 20, 40, 0.9) 0%, 
    rgba(10, 20, 40, 0) 100%);
  pointer-events: none;
  z-index: 0;
}

:deep(.particles-canvas) {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

:deep(.particles-overlay) {
  z-index: 2;
}

:deep(.controls-panel) {
  z-index: 10;
}
</style>