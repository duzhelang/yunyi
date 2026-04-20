import { defineStore } from 'pinia'
import router, { resetRouter } from "@/router"

export const useMainStore = defineStore('main', {
  state: () => ({
    currentPathName: ''
  }),
  getters: {
    getCurrentPathName: (state) => state.currentPathName
  },
  actions: {
    setPath() {
      this.currentPathName = localStorage.getItem("currentPathName")
    },
    logout() {
      // 保持向后兼容
      localStorage.removeItem("user")
      localStorage.removeItem("menus")
      router.push("/login")
      resetRouter()
    }
  }
})
