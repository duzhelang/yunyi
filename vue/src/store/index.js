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
      // 清空缓存
      localStorage.removeItem("user")
      localStorage.removeItem("menus")
      router.push("/login")

      // 重置路由
      resetRouter()
    }
  }
})
