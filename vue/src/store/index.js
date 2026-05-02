import { defineStore } from 'pinia'
import router, { resetRouter } from "@/router"
import { CacheHelper } from "@/utils/cacheHelper"

export const useMainStore = defineStore('main', {
  state: () => ({
    currentPathName: ''
  }),
  getters: {
    getCurrentPathName: (state) => state.currentPathName
  },
  actions: {
    setPath() {
      this.currentPathName = CacheHelper.get('currentPathName')
    },
    logout() {
      CacheHelper.clearAllLoginState()
      router.push("/login")
      resetRouter()
    },
    clearAllCache() {
      CacheHelper.clearAll()
      router.push("/login")
      resetRouter()
    }
  }
})
