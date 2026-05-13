import { onMounted, onUnmounted } from 'vue'

let audioCtx = null
let isReady = false

function initAudioContext() {
  if (!audioCtx) {
    try {
      audioCtx = new (window.AudioContext || window.webkitAudioContext)()
    } catch (e) {
      console.warn('AudioContext 创建失败:', e)
      return false
    }
  }
  if (audioCtx.state === 'suspended') {
    audioCtx.resume()
  }
  isReady = true
  return true
}

function ensureReady() {
  if (!isReady || !audioCtx || audioCtx.state === 'suspended') {
    return initAudioContext()
  }
  return true
}

function playHoverSound(volume = 0.2) {
  if (!ensureReady()) return
  try {
    const osc = audioCtx.createOscillator()
    const gain = audioCtx.createGain()
    
    osc.type = 'sine'
    osc.frequency.setValueAtTime(800, audioCtx.currentTime)
    osc.frequency.exponentialRampToValueAtTime(1200, audioCtx.currentTime + 0.05)
    
    gain.gain.setValueAtTime(0, audioCtx.currentTime)
    gain.gain.linearRampToValueAtTime(volume, audioCtx.currentTime + 0.01)
    gain.gain.exponentialRampToValueAtTime(0.001, audioCtx.currentTime + 0.08)
    
    osc.connect(gain)
    gain.connect(audioCtx.destination)
    osc.start()
    osc.stop(audioCtx.currentTime + 0.1)
  } catch (e) {
    console.warn('悬停音效播放失败:', e)
  }
}

function playClickSound(volume = 0.4) {
  if (!ensureReady()) return
  try {
    const osc = audioCtx.createOscillator()
    const gain = audioCtx.createGain()
    
    osc.type = 'triangle'
    osc.frequency.setValueAtTime(600, audioCtx.currentTime)
    osc.frequency.exponentialRampToValueAtTime(100, audioCtx.currentTime + 0.1)
    
    gain.gain.setValueAtTime(0, audioCtx.currentTime)
    gain.gain.linearRampToValueAtTime(volume, audioCtx.currentTime + 0.005)
    gain.gain.exponentialRampToValueAtTime(0.001, audioCtx.currentTime + 0.15)
    
    osc.connect(gain)
    gain.connect(audioCtx.destination)
    osc.start()
    osc.stop(audioCtx.currentTime + 0.2)
  } catch (e) {
    console.warn('点击音效播放失败:', e)
  }
}

export function useGlobalSound() {
  const hoverSelectors = 'a, button, .el-menu-item, .el-dropdown-item, [role="button"]'
  const clickSelectors = 'a, button, .el-menu-item, .el-dropdown-item, [role="button"]'

  const handleFirstInteraction = () => {
    initAudioContext()
    document.removeEventListener('click', handleFirstInteraction, true)
    document.removeEventListener('touchstart', handleFirstInteraction, true)
  }

  const handleMouseEnter = (e) => {
    if (!isReady) return
    if (e.target && e.target.closest) {
      const target = e.target.closest(hoverSelectors)
      if (target) playHoverSound(0.2)
    }
  }

  const handleClick = (e) => {
    if (e.target && e.target.closest) {
      const target = e.target.closest(clickSelectors)
      if (target) playClickSound(0.4)
    }
  }

  onMounted(() => {
    document.addEventListener('click', handleFirstInteraction, true)
    document.addEventListener('touchstart', handleFirstInteraction, true)
    document.addEventListener('mouseenter', handleMouseEnter, true)
    document.addEventListener('click', handleClick, true)
  })

  onUnmounted(() => {
    document.removeEventListener('click', handleFirstInteraction, true)
    document.removeEventListener('touchstart', handleFirstInteraction, true)
    document.removeEventListener('mouseenter', handleMouseEnter, true)
    document.removeEventListener('click', handleClick, true)
  })
}
