import Vue from 'vue'
import VueRouter from 'vue-router'
import store from "@/store";

Vue.use(VueRouter)

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
  },

]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// 提供一个重置路由的方法
export const resetRouter = () => {
  router.matcher = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
  })
}

// 注意:刷新页面会导致页面路由重置
export const setRoutes = () => {
  const storeMenus = localStorage.getItem("menus");
  if (storeMenus) {
    try {
      // 获取当前的路由对象名称数组
      const currentRouteNames = router.getRoutes().map(v => v.name)
      if (!currentRouteNames.includes('Manage')) {
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
      }
    } catch (error) {
      console.error('动态路由设置失败:', error)
    }
  }
}

// 重置后重新设置路由
setRoutes()

router.beforeEach((to, from, next) => {
  if (to.name) {
    localStorage.setItem("currentPathName", to.name)  // 设置当前的路由名称
    store.commit("setPath")
  }

  // 未找到路由的情况
  if (!to.matched.length) {
    const storeMenus = localStorage.getItem("menus")
    if (storeMenus) {
      next("/404")
    } else {
      // 跳回登录页面
      next("/login")
    }
  } else {
    // 其他的情况都放行
    next()
  }
})

export default router