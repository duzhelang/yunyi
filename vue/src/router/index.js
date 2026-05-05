import { createRouter, createWebHistory } from 'vue-router'
import { useMainStore } from "@/store"
import { CacheHelper } from "@/utils/cacheHelper"

// pagePath 到组件名称的映射表
const mapPagePath = {
  'dataset-management': 'DatasetManagement',
  'DatasetManagement': 'DatasetManagement',
  'onlineupdate': 'DatasetManagement',
  'online-training': 'OnlineTraining',
  'OnlineTraining': 'OnlineTraining',
  'file': 'OnlineTraining',
  'model-manager': 'ModelManagement',
  'health-profile': 'HealthProfileView',
  'risk-quick': 'HealthCheck',
  'chat': 'DiabetesChat',
  'ai-chat': 'DiabetesChat',
  'diabetes-education': 'DiabetesEducation',
  'diabetes-video': 'DiabetesEducation',
  'menu': 'Menu',
  'user': 'User',
  'role': 'Role',
  'dashbord': 'Dashbord',
  'doctor-workbench': 'DoctorWorkbench',
  'prediction-workbench': 'PredictionWorkbench',
  'data-collection': 'DataCollection',
  'data-test': 'DataTest',
  'treatment-record': 'TreatmentRecord',
  'individual-insight': 'IndividualInsight',
  'group-analysis': 'GroupAnalysis',
  'detailbord': 'Detailbord',
  'omlist': 'OMlist',
  'omsend': 'OMsend',
  'test-file': 'TestFile',
  'collection-log': 'DataCollection',
  '采集日志': 'DataCollection',
  'online-predict': 'PredictionWorkbench',
  '在线预测': 'PredictionWorkbench',
  'data-report': 'Dashbord',
  '数据报表': 'Dashbord',
  'detail-report': 'Detailbord',
  '详细报表': 'Detailbord',
  'fault-report': 'OMsend',
  '故障报修': 'OMsend',
  'report-detail': 'OMlist',
  '报修详情': 'OMlist',
  'operation-detail': 'Detailbord',
  '运维详情': 'Detailbord',
  'info-receipt': 'Detailbord',
  '信息回执': 'Detailbord'
}

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
  const storeMenus = CacheHelper.get('menus');
  console.log('setRoutes 被调用，storeMenus:', storeMenus)

  const hasManage = router.getRoutes().some(v => v.name === 'Manage')
  console.log('hasManage:', hasManage)
  if (hasManage) {
    router.removeRoute('Manage')
  }

  const manageRoute = {
    path: '/',
    name: 'Manage',
    component: () => import('../views/Manage.vue'),
    redirect: "/home",
    children: [
      { path: 'person', name: '个人信息', component: () => import('../views/Person.vue')},
      { path: 'password', name: '修改密码', component: () => import('../views/Password.vue')},
      { path: 'treatment-record', name: '诊疗档案', component: () => import('../views/TreatmentRecord.vue')}
    ]
  }

  const existingPaths = new Set(['person', 'password', 'treatment-record'])

  if (storeMenus) {
    try {
      const menus = JSON.parse(storeMenus)
      menus.forEach(item => {
        if (item.path || item.pagePath) {
          let componentName = item.pagePath || item.path.replace(/^\//, '')
          if (mapPagePath[componentName]) {
            componentName = mapPagePath[componentName]
          } else {
            componentName = componentName.charAt(0).toUpperCase() + componentName.slice(1)
            componentName = componentName.replace(/-([a-z])/g, (g) => g[1].toUpperCase())
          }
          const routePath = item.path ? (item.path.startsWith('/') ? item.path : '/' + item.path).toLowerCase() : '/' + item.pagePath.toLowerCase()

          if (!existingPaths.has(routePath)) {
            existingPaths.add(routePath)
            manageRoute.children.push({
              path: routePath,
              name: item.name,
              component: () => import(`../views/${componentName}.vue`)
            })
          }
        }

        if (Array.isArray(item.children) && item.children.length) {
          item.children.forEach(subItem => {
            if (subItem.path || subItem.pagePath) {
              let componentName = subItem.pagePath || subItem.path.replace(/^\//, '')
              if (mapPagePath[componentName]) {
                componentName = mapPagePath[componentName]
              } else {
                componentName = componentName.charAt(0).toUpperCase() + componentName.slice(1)
                componentName = componentName.replace(/-([a-z])/g, (g) => g[1].toUpperCase())
              }
              const routePath = subItem.path ? (subItem.path.startsWith('/') ? subItem.path : '/' + subItem.path).toLowerCase() : '/' + subItem.pagePath.toLowerCase()

              if (!existingPaths.has(routePath)) {
                existingPaths.add(routePath)
                manageRoute.children.push({
                  path: routePath,
                  name: subItem.name,
                  component: () => import(`../views/${componentName}.vue`)
                })
              }
            }
          })
        }
      })
    } catch (error) {
      console.error('动态路由设置失败:', error)
    }
  } else {
    console.log('没有storeMenus，跳过动态路由')
  }

  router.addRoute(manageRoute)

  console.log('添加路由后的所有路由:', router.getRoutes().map(r => ({path: r.path, name: r.name})))
}

// 初始化时设置路由
setRoutes()

let pendingPaths = new Set()

router.beforeEach((to, from, next) => {
  console.log('路由导航:', to.path)
  console.log('当前所有路由:', router.getRoutes().map(r => ({path: r.path, name: r.name})))
  
  // ⭐ 核心修复：白名单优先检查，登录、注册、404直接放行！！！
  if (to.path === '/login' || to.path === '/register' || to.path === '/404') {
    console.log('白名单页面，直接放行')
    return next()
  }

  if (to.name) {
    CacheHelper.set('currentPathName', to.name)
    const store = useMainStore()
    store.setPath()
  }

  if (!to.matched.length) {
    const storeMenus = CacheHelper.get('menus')
    console.log('未匹配到路由，menus:', storeMenus)
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
