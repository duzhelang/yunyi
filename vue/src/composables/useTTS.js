import { ref, isRef } from 'vue'
import request from '@/utils/request'

export function useTTS(messages) {
  const messagesRef = isRef(messages) ? messages : ref(messages)
  const isMuted = ref(false)
  let currentAudio = null

  function base64ToBlob(base64, mimeType) {
    const byteCharacters = atob(base64)
    const byteNumbers = new Array(byteCharacters.length)
    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i)
    }
    const byteArray = new Uint8Array(byteNumbers)
    return new Blob([byteArray], { type: mimeType })
  }

  async function speak(index) {
    stopSpeaking()
    const msg = messagesRef.value[index]
    if (!msg || msg.isUser) return
    msg.isSpeaking = true
    try {
      const response = await request.post('/api/diabetes/tts', { text: msg.content })
      if (response && response.code === '200' && response.data) {
        const audioBlob = base64ToBlob(response.data, 'audio/wav')
        const audioUrl = URL.createObjectURL(audioBlob)
        currentAudio = new Audio(audioUrl)
        currentAudio.onended = () => {
          msg.isSpeaking = false
          URL.revokeObjectURL(audioUrl)
          currentAudio = null
        }
        currentAudio.onerror = () => {
          msg.isSpeaking = false
          URL.revokeObjectURL(audioUrl)
          currentAudio = null
        }
        await currentAudio.play()
      } else {
        msg.isSpeaking = false
        console.error('TTS合成失败:', response?.msg)
      }
    } catch (error) {
      msg.isSpeaking = false
      console.error('TTS请求异常:', error)
    }
  }

  function stopSpeaking() {
    if (currentAudio) {
      currentAudio.pause()
      currentAudio.currentTime = 0
      currentAudio = null
    }
    messagesRef.value.forEach(m => {
      if (!m.isUser) m.isSpeaking = false
    })
  }

  function toggleSpeak(msg, index) {
    if (msg.isSpeaking) {
      stopSpeaking()
    } else {
      speak(index)
    }
  }

  function toggleMute() {
    isMuted.value = !isMuted.value
    if (isMuted.value) {
      stopSpeaking()
    }
  }

  return {
    isMuted,
    speak,
    stopSpeaking,
    toggleSpeak,
    toggleMute
  }
}
