import { createRouter, createWebHistory } from 'vue-router'
import { useMainStore } from "@/store"

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/404',
    name: '404',
    component: () => import('../views/404.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 提供一个重置路由的方法
export const resetRouter = () => {
  const newRouter = createRouter({
    history: createWebHistory(),
    routes
  })
  router.matcher = newRouter.matcher
}

// 注意:刷新页面会导致页面路由重置
export const setRoutes = () => {
  const storeMenus = localStorage.getItem("menus");
  if (storeMenus) {
    try {
      // 获取当前的路由对象名称数组
      const hasManage = router.getRoutes().some(v => v.name === 'Manage')
      if (hasManage) {
        router.removeRoute('Manage')
      }
      // 拼装动态路由
      const manageRoute = {
        path: '/',
        name: 'Manage',
        component: () => import('../views/Manage.vue'),
        redirect: "/home",
        children: [
          { path: 'person', name: '个人信息', component: () => import('../views/Person.vue')},
          { path: 'password', name: '修改密码', component: () => import('../views/Password.vue')}
        ]
      }
      const menus = JSON.parse(storeMenus)
      menus.forEach(item => {
        if (item.path) {  // 当且仅当path不为空的时候才去设置路由
          let itemMenu = {
            path: item.path.replace("/", ""),
            name: item.name,
            component: () => import(`../views/${item.pagePath}.vue`)
          }
          manageRoute.children.push(itemMenu)
        } else if(Array.isArray(item.children) && item.children.length) {
          item.children.forEach(subItem => {
            if (subItem.path) {
              let itemMenu = {
                path: subItem.path.replace("/", ""),
                name: subItem.name,
                component: () => import(`../views/${subItem.pagePath}.vue`)
              }
              manageRoute.children.push(itemMenu)
            }
          })
        }
      })
      // 动态添加到现在的路由对象中去
      router.addRoute(manageRoute)

    } catch (error) {
      console.error('动态路由设置失败:', error)
    }
  }
}

// 重置后重新设置路由
setRoutes()

let pendingPaths = new Set()

router.beforeEach((to, from, next) => {
  console.log('路由导航:', to.path)
  
  // ⭐ 核心修复：白名单优先检查，登录、注册、404直接放行！！！
  if (to.path === '/login' || to.path === '/register' || to.path === '/404') {
    console.log('白名单页面，直接放行')
    return next()
  }

  if (to.name) {
    localStorage.setItem("currentPathName", to.name)
    const store = useMainStore()
    store.setPath()
  }

  if (!to.matched.length) {
    const storeMenus = localStorage.getItem("menus")
    if (storeMenus) {
      // 如果已经尝试过重建该路径，则直接去404，防止无限循环
      if (pendingPaths.has(to.fullPath)) {
        pendingPaths.delete(to.fullPath)
        return next("/404")
      }

      pendingPaths.add(to.fullPath)
      setRoutes()  // 重建动态路由

      // 重新尝试导航到目标路径
      console.log('重建路由，重新导航')
      return next(to.fullPath)
    } else {
      return next("/login")
    }
  } else {
    // 清理标记（可选）
    pendingPaths.delete(to.fullPath)
    console.log('正常通过')
    next()
  }
})

export default router