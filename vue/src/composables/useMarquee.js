import { ref, nextTick } from 'vue'
import { quickQuestions } from '@/data/diabetesMockData'

export function useMarquee() {
  const marqueePaused = ref(false)
  const marqueeAnimId = ref(null)
  const marqueeTrack = ref(null)
  const allQuestions = ref([])

  function initQuestions() {
    allQuestions.value = quickQuestions.reduce((arr, group) => {
      return arr.concat(group.questions)
    }, [])
  }

  function startMarquee() {
    if (marqueeAnimId.value) return
    const track = marqueeTrack.value
    if (!track) return
    const speed = 0.3
    const animate = () => {
      if (!marqueePaused.value) {
        const currentY = parseFloat(track.style.transform?.match(/-?[\d.]+/)?.[0] || 0)
        const halfHeight = track.scrollHeight / 2
        const newY = currentY - speed
        track.style.transform = `translateY(${Math.abs(newY) >= halfHeight ? 0 : newY}px)`
      }
      marqueeAnimId.value = requestAnimationFrame(animate)
    }
    marqueeAnimId.value = requestAnimationFrame(animate)
  }

  function pauseMarquee() {
    marqueePaused.value = true
  }

  function resumeMarquee() {
    marqueePaused.value = false
  }

  function stopMarquee() {
    if (marqueeAnimId.value) {
      cancelAnimationFrame(marqueeAnimId.value)
      marqueeAnimId.value = null
    }
  }

  return {
    marqueePaused,
    marqueeAnimId,
    marqueeTrack,
    allQuestions,
    initQuestions,
    startMarquee,
    pauseMarquee,
    resumeMarquee,
    stopMarquee
  }
}
