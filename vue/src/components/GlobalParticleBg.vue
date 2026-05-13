<template>
  <div class="global-particle-bg" :class="[`theme-${currentTheme}`]">
    <div class="bg-gradient-layer" :style="gradientStyle"></div>
    <vue-particles
      :id="particlesId"
      :options="particlesOptions"
      class="particles-layer"
    />
    <div class="vignette-layer"></div>
    <div class="theme-switcher" v-if="showSwitcher">
      <div class="switcher-trigger" @click="togglePanel">
        <el-icon><Brush /></el-icon>
      </div>
      <transition name="panel-fade">
        <div class="switcher-panel" v-if="panelOpen">
          <div class="panel-title">背景主题</div>
          <div class="theme-grid">
            <div
              v-for="theme in themes"
              :key="theme.id"
              class="theme-item"
              :class="{ active: currentTheme === theme.id }"
              @click="setTheme(theme.id)"
            >
              <div class="theme-preview" :style="{ background: theme.preview }"></div>
              <span class="theme-name">{{ theme.name }}</span>
            </div>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { Brush } from '@element-plus/icons-vue'

const props = defineProps({
  showSwitcher: {
    type: Boolean,
    default: true
  },
  defaultTheme: {
    type: String,
    default: 'medical-blue'
  }
})

const particlesId = 'global-particles-' + Math.random().toString(36).substr(2, 9)
const panelOpen = ref(false)
const currentTheme = ref(localStorage.getItem('particle-theme') || props.defaultTheme)

const themes = [
  {
    id: 'medical-blue',
    name: '医学蓝',
    preview: 'linear-gradient(135deg, #0a1628, #1a365d)',
    gradient: 'linear-gradient(135deg, #0a1628 0%, #1a365d 50%, #0d2137 100%)',
    particles: {
      color: '#4a9eff',
      linkColor: '#2a6cbf',
      opacity: 0.35
    }
  },
  {
    id: 'bio-green',
    name: '生物绿',
    preview: 'linear-gradient(135deg, #0a1f1a, #1a4d3e)',
    gradient: 'linear-gradient(135deg, #0a1f1a 0%, #1a4d3e 50%, #0d2b22 100%)',
    particles: {
      color: '#4ade80',
      linkColor: '#22c55e',
      opacity: 0.3
    }
  },
  {
    id: 'neural-purple',
    name: '神经紫',
    preview: 'linear-gradient(135deg, #1a0a2e, #2d1b69)',
    gradient: 'linear-gradient(135deg, #1a0a2e 0%, #2d1b69 50%, #1e1145 100%)',
    particles: {
      color: '#a78bfa',
      linkColor: '#7c3aed',
      opacity: 0.3
    }
  },
  {
    id: 'dna-cyan',
    name: '基因青',
    preview: 'linear-gradient(135deg, #0a1a1f, #1a3d4d)',
    gradient: 'linear-gradient(135deg, #0a1a1f 0%, #1a3d4d 50%, #0d2530 100%)',
    particles: {
      color: '#22d3ee',
      linkColor: '#06b6d4',
      opacity: 0.35
    }
  },
  {
    id: 'warm-medical',
    name: '暖光',
    preview: 'linear-gradient(135deg, #1a1410, #2d2418)',
    gradient: 'linear-gradient(135deg, #1a1410 0%, #2d2418 50%, #221c14 100%)',
    particles: {
      color: '#fbbf24',
      linkColor: '#d97706',
      opacity: 0.25
    }
  },
  {
    id: 'pure-dark',
    name: '纯黑',
    preview: 'linear-gradient(135deg, #0a0a0a, #1a1a1a)',
    gradient: 'linear-gradient(135deg, #0a0a0a 0%, #141414 50%, #0f0f0f 100%)',
    particles: {
      color: '#6b7280',
      linkColor: '#4b5563',
      opacity: 0.2
    }
  }
]

const currentThemeData = computed(() => {
  return themes.find(t => t.id === currentTheme.value) || themes[0]
})

const gradientStyle = computed(() => ({
  background: currentThemeData.value.gradient
}))

const particlesOptions = computed(() => ({
  particles: {
    number: {
      value: 40,
      density: {
        enable: true,
        value_area: 1200
      }
    },
    color: {
      value: currentThemeData.value.particles.color
    },
    shape: {
      type: 'circle'
    },
    opacity: {
      value: currentThemeData.value.particles.opacity,
      random: true,
      anim: {
        enable: true,
        speed: 0.3,
        opacity_min: 0.1,
        sync: false
      }
    },
    size: {
      value: 2.5,
      random: {
        enable: true,
        minimumValue: 1
      },
      anim: {
        enable: false
      }
    },
    line_linked: {
      enable: true,
      distance: 150,
      color: currentThemeData.value.particles.linkColor,
      opacity: 0.2,
      width: 1
    },
    move: {
      enable: true,
      speed: 0.8,
      direction: 'none',
      random: true,
      straight: false,
      out_mode: 'out',
      bounce: false
    }
  },
  interactivity: {
    detect_on: 'canvas',
    events: {
      onhover: {
        enable: true,
        mode: 'grab'
      },
      onclick: {
        enable: false
      },
      resize: true
    },
    modes: {
      grab: {
        distance: 140,
        line_linked: {
          opacity: 0.4
        }
      }
    }
  },
  retina_detect: true,
  fps_limit: 30
}))

const togglePanel = () => {
  panelOpen.value = !panelOpen.value
}

const setTheme = (themeId) => {
  currentTheme.value = themeId
  localStorage.setItem('particle-theme', themeId)
  panelOpen.value = false
}

const handleClickOutside = (e) => {
  if (!e.target.closest('.theme-switcher')) {
    panelOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

watch(() => currentTheme.value, () => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.global-particle-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
}

.bg-gradient-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  transition: background 0.8s ease;
}

.particles-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.vignette-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(ellipse at center, transparent 50%, rgba(0, 0, 0, 0.3) 100%);
}

.theme-switcher {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 1000;
  pointer-events: auto;
}

.switcher-trigger {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  color: rgba(255, 255, 255, 0.7);
}

.switcher-trigger:hover {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  transform: scale(1.1);
}

.switcher-panel {
  position: absolute;
  bottom: 50px;
  right: 0;
  width: 240px;
  background: rgba(10, 15, 25, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.4);
}

.panel-title {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 12px;
  font-weight: 500;
}

.theme-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

.theme-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  padding: 8px 4px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.theme-item:hover {
  background: rgba(255, 255, 255, 0.05);
}

.theme-item.active {
  background: rgba(255, 255, 255, 0.1);
}

.theme-item.active .theme-preview {
  border-color: #4a9eff;
  box-shadow: 0 0 10px rgba(74, 158, 255, 0.4);
}

.theme-preview {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  border: 2px solid transparent;
  transition: all 0.2s ease;
}

.theme-item:hover .theme-preview {
  transform: scale(1.05);
}

.theme-name {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.6);
  text-align: center;
}

.theme-item.active .theme-name {
  color: #4a9eff;
}

.panel-fade-enter-active,
.panel-fade-leave-active {
  transition: all 0.3s ease;
}

.panel-fade-enter-from,
.panel-fade-leave-to {
  opacity: 0;
  transform: translateY(10px) scale(0.95);
}

@media (max-width: 768px) {
  .theme-switcher {
    bottom: 15px;
    right: 15px;
  }

  .switcher-trigger {
    width: 36px;
    height: 36px;
  }

  .switcher-panel {
    width: 200px;
    right: -5px;
  }
}
</style>