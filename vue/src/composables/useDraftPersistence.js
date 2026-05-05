import { useHealthStore } from '@/store/healthStore'

const SAVE_INTERVAL = 5000
let saveTimer = null

export function useDraftPersistence() {
  const store = useHealthStore()

  function startAutoSave() {
    if (saveTimer) return
    saveTimer = setInterval(() => {
      store.saveToDraft()
    }, SAVE_INTERVAL)
  }

  function stopAutoSave() {
    if (saveTimer) {
      clearInterval(saveTimer)
      saveTimer = null
    }
  }

  function loadDraft() {
    store.loadFromDraft()
  }

  function clearDraft() {
    store.clearDraft()
  }

  return { startAutoSave, stopAutoSave, loadDraft, clearDraft }
}
