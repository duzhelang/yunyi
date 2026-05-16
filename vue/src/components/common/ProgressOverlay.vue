<template>
  <transition name="fade">
    <div v-if="visible" class="progress-overlay">
      <div class="progress-overlay-content">
        <div class="pulse-ring-container">
          <div class="pulse-ring pulse-ring-1"></div>
          <div class="pulse-ring pulse-ring-2"></div>
          <div class="pulse-ring pulse-ring-3"></div>
          <div class="center-icon" :style="{ color: color }">
            <slot name="icon">
              <svg viewBox="0 0 24 24" width="48" height="48" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M9 12l2 2 4-4" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20z" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </slot>
          </div>
        </div>
        
        <h3 class="overlay-title">{{ title }}</h3>
        
        <div class="progress-steps">
          <div 
            v-for="(step, index) in steps" 
            :key="index"
            class="step-item"
            :class="{ 'step-active': currentStep >= index, 'step-current': currentStep === index }"
          >
            <div class="step-icon" :style="currentStep >= index ? { background: `linear-gradient(135deg, ${color} 0%, ${darkenColor(color, 20)} 100%)` } : {}">
              <span v-if="currentStep > index">✓</span>
              <span v-else-if="currentStep === index" class="step-dot"></span>
              <span v-else class="step-number">{{ index + 1 }}</span>
            </div>
            <span class="step-text">{{ step }}</span>
          </div>
        </div>
        
        <div class="loading-bar">
          <div class="loading-bar-progress" :style="{ width: progress + '%', background: `linear-gradient(90deg, ${color} 0%, ${lightenColor(color, 20)} 50%, ${color} 100%)` }"></div>
        </div>
        
        <p class="overlay-hint">{{ currentHint }}</p>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, computed, watch, onBeforeUnmount } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '处理中'
  },
  steps: {
    type: Array,
    default: () => ['步骤 1', '步骤 2', '步骤 3']
  },
  hints: {
    type: Array,
    default: () => ['正在处理...', '请稍候...', '即将完成...']
  },
  color: {
    type: String,
    default: '#10b981'
  }
})

const currentStep = ref(0)
const progress = ref(0)
let stepTimer = null
let progressTimer = null

const currentHint = computed(() => props.hints[currentStep.value] || props.hints[0])

function darkenColor(hex, percent) {
  const num = parseInt(hex.replace('#', ''), 16)
  const amt = Math.round(2.55 * percent)
  const R = (num >> 16) - amt
  const G = (num >> 8 & 0x00FF) - amt
  const B = (num & 0x0000FF) - amt
  return '#' + (0x1000000 + 
    (R < 255 ? R < 1 ? 0 : R : 255) * 0x10000 + 
    (G < 255 ? G < 1 ? 0 : G : 255) * 0x100 + 
    (B < 255 ? B < 1 ? 0 : B : 255)
  ).toString(16).slice(1)
}

function lightenColor(hex, percent) {
  const num = parseInt(hex.replace('#', ''), 16)
  const amt = Math.round(2.55 * percent)
  const R = (num >> 16) + amt
  const G = (num >> 8 & 0x00FF) + amt
  const B = (num & 0x0000FF) + amt
  return '#' + (0x1000000 + 
    (R < 255 ? R < 1 ? 0 : R : 255) * 0x10000 + 
    (G < 255 ? G < 1 ? 0 : G : 255) * 0x100 + 
    (B < 255 ? B < 1 ? 0 : B : 255)
  ).toString(16).slice(1)
}

watch(() => props.visible, (newVal) => {
  if (newVal) {
    startAnimation()
  } else {
    stopAnimation()
  }
})

function startAnimation() {
  stopAnimation()
  currentStep.value = 0
  progress.value = 0
  
  stepTimer = setInterval(() => {
    if (currentStep.value < props.steps.length - 1) {
      currentStep.value++
    }
  }, 1200)
  
  progressTimer = setInterval(() => {
    if (progress.value < 95) {
      progress.value += Math.random() * 8 + 2
      if (progress.value > 95) progress.value = 95
    }
  }, 200)
}

function stopAnimation() {
  if (stepTimer) {
    clearInterval(stepTimer)
    stepTimer = null
  }
  if (progressTimer) {
    clearInterval(progressTimer)
    progressTimer = null
  }
  progress.value = 100
  currentStep.value = props.steps.length - 1
}

onBeforeUnmount(() => {
  stopAnimation()
})
</script>

<style scoped>
.progress-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.97);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  border-radius: 16px;
}

.progress-overlay-content {
  text-align: center;
  padding: 40px;
  max-width: 400px;
}

.pulse-ring-container {
  position: relative;
  width: 120px;
  height: 120px;
  margin: 0 auto 30px;
}

.pulse-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border-radius: 50%;
  border: 2px solid currentColor;
  opacity: 0;
  animation: pulse-ring 2s cubic-bezier(0.4, 0, 0.2, 1) infinite;
}

.pulse-ring-1 {
  width: 60px;
  height: 60px;
  animation-delay: 0s;
}

.pulse-ring-2 {
  width: 90px;
  height: 90px;
  animation-delay: 0.5s;
}

.pulse-ring-3 {
  width: 120px;
  height: 120px;
  animation-delay: 1s;
}

.center-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: icon-pulse 2s ease-in-out infinite;
}

@keyframes pulse-ring {
  0% {
    transform: translate(-50%, -50%) scale(0.8);
    opacity: 0.8;
  }
  100% {
    transform: translate(-50%, -50%) scale(1.3);
    opacity: 0;
  }
}

@keyframes icon-pulse {
  0%, 100% {
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    transform: translate(-50%, -50%) scale(1.1);
  }
}

.overlay-title {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 24px;
  letter-spacing: 0.5px;
}

.progress-steps {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
  text-align: left;
}

.step-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
  opacity: 0.5;
}

.step-active {
  opacity: 1;
}

.step-current {
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.1);
}

.step-icon {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e2e8f0;
  color: #64748b;
  font-size: 12px;
  font-weight: 600;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.step-active .step-icon {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
}

.step-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: white;
  animation: dot-blink 1s infinite;
}

@keyframes dot-blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}

.step-text {
  font-size: 14px;
  color: #475569;
  font-weight: 500;
}

.step-active .step-text {
  color: #1e293b;
}

.loading-bar {
  height: 4px;
  background: #e2e8f0;
  border-radius: 2px;
  overflow: hidden;
  margin-bottom: 16px;
}

.loading-bar-progress {
  height: 100%;
  background: linear-gradient(90deg, #10b981 0%, #34d399 50%, #10b981 100%);
  background-size: 200% 100%;
  border-radius: 2px;
  transition: width 0.3s ease;
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.overlay-hint {
  font-size: 13px;
  color: #64748b;
  line-height: 1.5;
  min-height: 40px;
}

.fade-enter-active {
  transition: opacity 0.3s ease;
}

.fade-leave-active {
  transition: opacity 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>