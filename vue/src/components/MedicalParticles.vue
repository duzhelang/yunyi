<template>
  <div class="medical-particles-container" :style="containerStyle">
    <vue-particles
      id="medical-particles"
      :options="particlesOptions"
      :particlesInit="particlesInit"
      :particlesLoaded="particlesLoaded"
      class="particles-canvas"
    />
    <div class="particles-overlay" :style="overlayStyle"></div>
    <div v-if="showControls" class="controls-panel" :class="{ 'controls-collapsed': controlsCollapsed }">
      <div class="controls-header" @click="toggleControls">
        <el-icon><Setting /></el-icon>
        <span>粒子控制面板</span>
        <el-icon class="collapse-icon" :class="{ 'rotated': controlsCollapsed }">
          <ArrowDown />
        </el-icon>
      </div>
      <div class="controls-content" v-show="!controlsCollapsed">
        <el-scrollbar max-height="400px">
          <div class="control-section">
            <h4>粒子形态</h4>
            <el-radio-group v-model="currentPattern" @change="changePattern">
              <el-radio-button label="dna">DNA双螺旋</el-radio-button>
              <el-radio-button label="molecule">分子结构</el-radio-button>
              <el-radio-button label="neural">神经网络</el-radio-button>
            </el-radio-group>
          </div>
          <div class="control-section">
            <h4>粒子密度</h4>
            <el-slider v-model="density" :min="20" :max="200" :step="10" @change="updateParticles" />
            <span class="control-value">{{ density }}</span>
          </div>
          <div class="control-section">
            <h4>粒子大小</h4>
            <el-slider v-model="particleSize" :min="1" :max="10" :step="0.5" @change="updateParticles" />
            <span class="control-value">{{ particleSize }}</span>
          </div>
          <div class="control-section">
            <h4>运动速度</h4>
            <el-slider v-model="speed" :min="0.5" :max="5" :step="0.5" @change="updateParticles" />
            <span class="control-value">{{ speed }}</span>
          </div>
          <div class="control-section">
            <h4>连接强度</h4>
            <el-slider v-model="linkDistance" :min="50" :max="200" :step="10" @change="updateParticles" />
            <span class="control-value">{{ linkDistance }}</span>
          </div>
          <div class="control-section">
            <h4>颜色主题</h4>
            <el-color-picker v-model="primaryColor" @change="updateParticles" />
          </div>
          <div class="control-section">
            <h4>交互模式</h4>
            <el-select v-model="interactionMode" @change="updateParticles">
              <el-option label="吸引" value="attract" />
              <el-option label="排斥" value="repulse" />
              <el-option label="跟随" value="grab" />
              <el-option label="禁用" value="none" />
            </el-select>
          </div>
          <div class="control-section">
            <el-button type="primary" @click="resetDefaults">重置默认</el-button>
            <el-button @click="toggleFullscreen">全屏</el-button>
          </div>
        </el-scrollbar>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { Setting, ArrowDown } from '@element-plus/icons-vue'

const props = defineProps({
  showControls: {
    type: Boolean,
    default: true
  },
  backgroundColor: {
    type: String,
    default: 'transparent'
  },
  overlayImage: {
    type: String,
    default: ''
  },
  overlayOpacity: {
    type: Number,
    default: 0.1
  },
  initialPattern: {
    type: String,
    default: 'dna'
  }
})

const emit = defineEmits(['pattern-change', 'performance-warning'])

const currentPattern = ref(props.initialPattern)
const density = ref(80)
const particleSize = ref(3)
const speed = ref(1.5)
const linkDistance = ref(120)
const primaryColor = ref('#00d4ff')
const interactionMode = ref('attract')
const controlsCollapsed = ref(false)
const isFullscreen = ref(false)
const fps = ref(60)
let fpsInterval = null
let lastTime = 0
let frameCount = 0

const containerStyle = computed(() => ({
  backgroundColor: props.backgroundColor,
  position: 'relative',
  width: '100%',
  height: '100%',
  overflow: 'hidden'
}))

const overlayStyle = computed(() => ({
  backgroundImage: props.overlayImage ? `url(${props.overlayImage})` : 'none',
  opacity: props.overlayOpacity,
  position: 'absolute',
  top: 0,
  left: 0,
  width: '100%',
  height: '100%',
  pointerEvents: 'none',
  zIndex: 1
}))

const hexToRgb = (hex) => {
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex)
  return result ? {
    r: parseInt(result[1], 16),
    g: parseInt(result[2], 16),
    b: parseInt(result[3], 16)
  } : { r: 0, g: 212, b: 255 }
}

const getPatternConfig = (pattern) => {
  const rgb = hexToRgb(primaryColor.value)
  const baseColor = `${rgb.r},${rgb.g},${rgb.b}`
  
  const patterns = {
    dna: {
      particles: {
        number: {
          value: density.value,
          density: {
            enable: true,
            value_area: 800
          }
        },
        color: {
          value: [
            `rgba(${baseColor},1)`,
            `rgba(${Math.min(255, rgb.r + 50)},${Math.min(255, rgb.g + 50)},${Math.min(255, rgb.b + 50)},1)`,
            `rgba(${Math.max(0, rgb.r - 30)},${Math.max(0, rgb.g - 30)},${Math.max(0, rgb.b - 30)},1)`
          ]
        },
        shape: {
          type: 'circle',
          stroke: {
            width: 0.5,
            color: `rgba(${baseColor},0.8)`
          }
        },
        opacity: {
          value: 0.7,
          random: true,
          anim: {
            enable: true,
            speed: 0.5,
            opacity_min: 0.3,
            sync: false
          }
        },
        size: {
          value: particleSize.value,
          random: {
            enable: true,
            minimumValue: 1
          },
          anim: {
            enable: true,
            speed: 2,
            size_min: 0.5,
            sync: false
          }
        },
        line_linked: {
          enable: true,
          distance: linkDistance.value,
          color: `rgba(${baseColor},0.4)`,
          opacity: 0.4,
          width: 1,
          triangles: {
            enable: true,
            color: `rgba(${baseColor},0.1)`,
            opacity: 0.1
          }
        },
        move: {
          enable: true,
          speed: speed.value,
          direction: 'none',
          random: true,
          straight: false,
          out_mode: 'bounce',
          bounce: true,
          attract: {
            enable: true,
            rotateX: 600,
            rotateY: 1200
          },
          path: {
            enable: true,
            delay: {
              value: 0
            },
            options: {
              size: 20,
              draw: false,
              increment: 0.005
            }
          }
        }
      },
      interactivity: {
        detect_on: 'canvas',
        events: {
          onhover: {
            enable: interactionMode.value !== 'none',
            mode: interactionMode.value === 'attract' ? 'grab' : interactionMode.value === 'repulse' ? 'repulse' : 'grab'
          },
          onclick: {
            enable: true,
            mode: 'push'
          },
          resize: true
        },
        modes: {
          grab: {
            distance: 180,
            line_linked: {
              opacity: 0.8
            }
          },
          bubble: {
            distance: 200,
            size: 6,
            duration: 0.4,
            opacity: 0.8,
            speed: 3
          },
          repulse: {
            distance: 100,
            duration: 0.4
          },
          push: {
            particles_nb: 4
          },
          remove: {
            particles_nb: 2
          }
        }
      },
      retina_detect: true,
      fps_limit: 60
    },
    molecule: {
      particles: {
        number: {
          value: Math.floor(density.value * 0.6),
          density: {
            enable: true,
            value_area: 1000
          }
        },
        color: {
          value: [
            `rgba(0,180,255,1)`,
            `rgba(0,255,200,1)`,
            `rgba(255,100,180,1)`
          ]
        },
        shape: {
          type: ['circle', 'polygon'],
          polygon: {
            nb_sides: 6
          },
          stroke: {
            width: 1,
            color: `rgba(0,180,255,0.8)`
          }
        },
        opacity: {
          value: 0.8,
          random: true,
          anim: {
            enable: true,
            speed: 0.3,
            opacity_min: 0.4,
            sync: false
          }
        },
        size: {
          value: particleSize.value * 1.5,
          random: {
            enable: true,
            minimumValue: 2
          },
          anim: {
            enable: true,
            speed: 1,
            size_min: 1,
            sync: false
          }
        },
        line_linked: {
          enable: true,
          distance: linkDistance.value,
          color: `rgba(0,200,255,0.5)`,
          opacity: 0.6,
          width: 2,
          triangles: {
            enable: false
          }
        },
        move: {
          enable: true,
          speed: speed.value * 0.8,
          direction: 'none',
          random: false,
          straight: false,
          out_mode: 'bounce',
          bounce: true,
          attract: {
            enable: true,
            rotateX: 600,
            rotateY: 1200
          },
          path: {
            enable: true,
            delay: {
              value: 0
            },
            options: {
              size: 30,
              draw: false,
              increment: 0.01
            }
          }
        }
      },
      interactivity: {
        detect_on: 'canvas',
        events: {
          onhover: {
            enable: interactionMode.value !== 'none',
            mode: interactionMode.value === 'attract' ? 'grab' : interactionMode.value === 'repulse' ? 'repulse' : 'grab'
          },
          onclick: {
            enable: true,
            mode: 'push'
          },
          resize: true
        },
        modes: {
          grab: {
            distance: 200,
            line_linked: {
              opacity: 0.9
            }
          },
          bubble: {
            distance: 250,
            size: 8,
            duration: 0.5,
            opacity: 0.9,
            speed: 2
          },
          repulse: {
            distance: 120,
            duration: 0.5
          },
          push: {
            particles_nb: 3
          },
          remove: {
            particles_nb: 2
          }
        }
      },
      retina_detect: true,
      fps_limit: 60
    },
    neural: {
      particles: {
        number: {
          value: density.value,
          density: {
            enable: true,
            value_area: 600
          }
        },
        color: {
          value: [
            `rgba(100,200,255,1)`,
            `rgba(150,100,255,1)`,
            `rgba(255,150,200,1)`
          ]
        },
        shape: {
          type: 'circle',
          stroke: {
            width: 1,
            color: `rgba(150,100,255,0.9)`
          }
        },
        opacity: {
          value: 0.6,
          random: true,
          anim: {
            enable: true,
            speed: 0.8,
            opacity_min: 0.2,
            sync: false
          }
        },
        size: {
          value: particleSize.value,
          random: {
            enable: true,
            minimumValue: 1
          },
          anim: {
            enable: true,
            speed: 1.5,
            size_min: 0.5,
            sync: false
          }
        },
        line_linked: {
          enable: true,
          distance: linkDistance.value,
          color: `rgba(150,100,255,0.4)`,
          opacity: 0.5,
          width: 1.5,
          triangles: {
            enable: true,
            color: `rgba(150,100,255,0.15)`,
            opacity: 0.15
          }
        },
        move: {
          enable: true,
          speed: speed.value,
          direction: 'none',
          random: true,
          straight: false,
          out_mode: 'bounce',
          bounce: true,
          attract: {
            enable: true,
            rotateX: 600,
            rotateY: 1200
          },
          path: {
            enable: true,
            delay: {
              value: 0
            },
            options: {
              size: 25,
              draw: false,
              increment: 0.008
            }
          }
        }
      },
      interactivity: {
        detect_on: 'canvas',
        events: {
          onhover: {
            enable: interactionMode.value !== 'none',
            mode: interactionMode.value === 'attract' ? 'grab' : interactionMode.value === 'repulse' ? 'repulse' : 'grab'
          },
          onclick: {
            enable: true,
            mode: 'push'
          },
          resize: true
        },
        modes: {
          grab: {
            distance: 150,
            line_linked: {
              opacity: 0.7
            }
          },
          bubble: {
            distance: 180,
            size: 5,
            duration: 0.3,
            opacity: 0.7,
            speed: 2.5
          },
          repulse: {
            distance: 80,
            duration: 0.3
          },
          push: {
            particles_nb: 5
          },
          remove: {
            particles_nb: 2
          }
        }
      },
      retina_detect: true,
      fps_limit: 60
    }
  }
  
  return patterns[pattern] || patterns.dna
}

const particlesOptions = ref(getPatternConfig(currentPattern.value))

const particlesInit = async (engine) => {
  console.log('粒子引擎初始化完成')
}

const particlesLoaded = async (container) => {
  console.log('粒子容器加载完成', container)
  startFPSCounter()
}

const startFPSCounter = () => {
  const measureFPS = (timestamp) => {
    frameCount++
    const elapsed = timestamp - lastTime
    
    if (elapsed >= 1000) {
      fps.value = Math.round((frameCount * 1000) / elapsed)
      frameCount = 0
      lastTime = timestamp
      
      if (fps.value < 30) {
        emit('performance-warning', fps.value)
      }
    }
    
    requestAnimationFrame(measureFPS)
  }
  
  requestAnimationFrame(measureFPS)
}

const changePattern = (pattern) => {
  currentPattern.value = pattern
  particlesOptions.value = getPatternConfig(pattern)
  emit('pattern-change', pattern)
}

const updateParticles = () => {
  particlesOptions.value = getPatternConfig(currentPattern.value)
}

const resetDefaults = () => {
  density.value = 80
  particleSize.value = 3
  speed.value = 1.5
  linkDistance.value = 120
  primaryColor.value = '#00d4ff'
  interactionMode.value = 'attract'
  updateParticles()
}

const toggleControls = () => {
  controlsCollapsed.value = !controlsCollapsed.value
}

const toggleFullscreen = () => {
  const container = document.querySelector('.medical-particles-container')
  if (!document.fullscreenElement) {
    container.requestFullscreen()
    isFullscreen.value = true
  } else {
    document.exitFullscreen()
    isFullscreen.value = false
  }
}

const handleResize = () => {
  if (window.innerWidth < 768) {
    density.value = Math.min(density.value, 50)
    particleSize.value = Math.min(particleSize.value, 2)
    updateParticles()
  }
}

const checkPerformance = () => {
  if (navigator.hardwareConcurrency && navigator.hardwareConcurrency < 4) {
    density.value = Math.min(density.value, 60)
    particleSize.value = Math.min(particleSize.value, 2)
    updateParticles()
  }
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
  handleResize()
  checkPerformance()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (fpsInterval) {
    cancelAnimationFrame(fpsInterval)
  }
})

watch(() => props.initialPattern, (newPattern) => {
  changePattern(newPattern)
})

defineExpose({
  changePattern,
  updateParticles,
  resetDefaults,
  getFPS: () => fps.value
})
</script>

<style scoped>
.medical-particles-container {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 300px;
  overflow: hidden;
}

.particles-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.particles-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.controls-panel {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 280px;
  background: rgba(10, 20, 40, 0.85);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(0, 212, 255, 0.3);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3), 
              inset 0 1px 0 rgba(255, 255, 255, 0.1);
  z-index: 10;
  overflow: hidden;
  transition: all 0.3s ease;
}

.controls-panel:hover {
  border-color: rgba(0, 212, 255, 0.5);
  box-shadow: 0 4px 30px rgba(0, 212, 255, 0.2), 
              inset 0 1px 0 rgba(255, 255, 255, 0.1);
}

.controls-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  cursor: pointer;
  color: #00d4ff;
  font-size: 14px;
  font-weight: 500;
  border-bottom: 1px solid rgba(0, 212, 255, 0.2);
  transition: background 0.2s ease;
}

.controls-header:hover {
  background: rgba(0, 212, 255, 0.1);
}

.collapse-icon {
  margin-left: auto;
  transition: transform 0.3s ease;
}

.rotated {
  transform: rotate(180deg);
}

.controls-content {
  padding: 12px 16px;
  max-height: 400px;
  overflow-y: auto;
}

.control-section {
  margin-bottom: 16px;
}

.control-section h4 {
  color: rgba(255, 255, 255, 0.8);
  margin: 0 0 8px 0;
  font-size: 12px;
  font-weight: 400;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.control-value {
  display: inline-block;
  margin-top: 4px;
  color: #00d4ff;
  font-size: 12px;
}

:deep(.el-radio-group) {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

:deep(.el-radio-button__inner) {
  background: rgba(0, 212, 255, 0.1);
  border-color: rgba(0, 212, 255, 0.3);
  color: #00d4ff;
  font-size: 11px;
  padding: 6px 10px;
}

:deep(.el-radio-button__orig-radio:checked + .el-radio-button__inner) {
  background: rgba(0, 212, 255, 0.3);
  border-color: #00d4ff;
  color: #fff;
  box-shadow: -1px 0 0 0 #00d4ff;
}

:deep(.el-slider__runway) {
  background: rgba(0, 212, 255, 0.2);
}

:deep(.el-slider__bar) {
  background: linear-gradient(90deg, #00d4ff, #00ff88);
}

:deep(.el-slider__button) {
  border-color: #00d4ff;
  background: #00d4ff;
  box-shadow: 0 0 10px rgba(0, 212, 255, 0.5);
}

:deep(.el-color-picker__trigger) {
  border-color: rgba(0, 212, 255, 0.5);
  background: rgba(0, 212, 255, 0.1);
}

:deep(.el-select .el-input__wrapper) {
  background: rgba(0, 212, 255, 0.1);
  border-color: rgba(0, 212, 255, 0.3);
  box-shadow: none;
}

:deep(.el-select .el-input__inner) {
  color: #00d4ff;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  box-shadow: 0 2px 10px rgba(0, 212, 255, 0.3);
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #00e5ff, #00ff99);
  box-shadow: 0 2px 15px rgba(0, 212, 255, 0.5);
}

:deep(.el-button--default) {
  background: rgba(0, 212, 255, 0.1);
  border-color: rgba(0, 212, 255, 0.5);
  color: #00d4ff;
}

:deep(.el-button--default:hover) {
  background: rgba(0, 212, 255, 0.2);
  border-color: #00d4ff;
  color: #fff;
}

@media (max-width: 768px) {
  .controls-panel {
    width: 240px;
    top: 10px;
    right: 10px;
  }
  
  .controls-header {
    padding: 10px 12px;
    font-size: 13px;
  }
  
  .controls-content {
    padding: 10px 12px;
  }
  
  :deep(.el-radio-button__inner) {
    font-size: 10px;
    padding: 5px 8px;
  }
}

@media (max-width: 480px) {
  .controls-panel {
    width: 200px;
    top: 8px;
    right: 8px;
  }
  
  .control-section h4 {
    font-size: 11px;
  }
}

.controls-panel::-webkit-scrollbar {
  width: 4px;
}

.controls-panel::-webkit-scrollbar-track {
  background: rgba(0, 212, 255, 0.1);
  border-radius: 2px;
}

.controls-panel::-webkit-scrollbar-thumb {
  background: rgba(0, 212, 255, 0.5);
  border-radius: 2px;
}

.controls-panel::-webkit-scrollbar-thumb:hover {
  background: #00d4ff;
}

.medical-particles-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(ellipse at center, 
    rgba(0, 212, 255, 0.1) 0%, 
    rgba(0, 0, 0, 0) 70%);
  pointer-events: none;
  z-index: 0;
}

.medical-particles-container::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 100px;
  background: linear-gradient(to top, 
    rgba(10, 20, 40, 0.8) 0%, 
    rgba(10, 20, 40, 0) 100%);
  pointer-events: none;
  z-index: 2;
}
</style>