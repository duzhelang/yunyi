import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import './assets/gloable.css'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import request from "@/utils/request"
import Particles from "@tsparticles/vue3"
import { loadSlim } from "@tsparticles/slim"

const app = createApp(App)

// 全局注册 Element Plus 所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(ElementPlus, {
  locale: zhCn
})
app.use(router)
app.use(createPinia())
app.use(Particles, {
  init: async (engine) => {
    await loadSlim(engine)
  }
})

app.config.globalProperties.$request = request

app.mount('#app')
