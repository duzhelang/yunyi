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
  '数据采集日志': 'DataTest',
  'online-predict': 'PredictionWorkbench',
  '家庭预测': 'TestFile',
  'data-report': 'Dashbord',
  '我的报告': 'Dashbord',
  'fault-report': 'Send',
  '故障报修': 'Send',
  'report-detail': 'List',
  '报修详情': 'List',
  'operation-detail': 'OMlist',
  '运维详情': 'OMlist',
  'info-receipt': 'OMsend',
  '信息回执': 'OMsend'
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

export const resetRouter = () => {
  const currentRoutes = router.getRoutes()
  currentRoutes.forEach(route => {
    if (route.name && route.name !== 'Login' && route.name !== 'Register' && route.name !== '404') {
      router.removeRoute(route.name)
    }
  })
}

// 注意:刷新页面会导致页面路由重置
export const setRoutes = () => {
  const storeMenus = CacheHelper.get('menus');

  const hasManage = router.getRoutes().some(v => v.name === 'Manage')
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

  const buildChildRoute = (menuItem) => {
    if (!menuItem.path && !menuItem.pagePath) return null
    let componentName = menuItem.pagePath || menuItem.path.replace(/^\//, '')
    if (mapPagePath[componentName]) {
      componentName = mapPagePath[componentName]
    } else {
      componentName = componentName.charAt(0).toUpperCase() + componentName.slice(1)
      componentName = componentName.replace(/-([a-z])/g, (g) => g[1].toUpperCase())
    }
    const routePath = menuItem.path
      ? menuItem.path.replace(/^\//, '').toLowerCase()
      : menuItem.pagePath.toLowerCase()

    if (existingPaths.has(routePath)) return null
    existingPaths.add(routePath)
    return {
      path: routePath,
      name: menuItem.name,
      component: () => import(`../views/${componentName}.vue`)
    }
  }

  if (storeMenus) {
    try {
      const menus = JSON.parse(storeMenus)
      menus.forEach(item => {
        const route = buildChildRoute(item)
        if (route) manageRoute.children.push(route)

        if (Array.isArray(item.children) && item.children.length) {
          item.children.forEach(subItem => {
            const subRoute = buildChildRoute(subItem)
            if (subRoute) manageRoute.children.push(subRoute)
          })
        }
      })
    } catch (error) {
      console.error('动态路由设置失败:', error)
    }
  }

  router.addRoute(manageRoute)
}

// 初始化时设置路由
setRoutes()

let pendingPaths = new Set()

router.beforeEach((to, from, next) => {
  if (to.path === '/login' || to.path === '/register' || to.path === '/404') {
    return next()
  }

  if (to.name) {
    CacheHelper.set('currentPathName', to.name)
    const store = useMainStore()
    store.setPath()
  }

  if (!to.matched.length) {
    const storeMenus = CacheHelper.get('menus')
    if (storeMenus) {
      if (pendingPaths.has(to.fullPath)) {
        pendingPaths.delete(to.fullPath)
        return next("/404")
      }
      pendingPaths.add(to.fullPath)
      setRoutes()
      return next(to.fullPath)
    } else {
      return next("/login")
    }
  } else {
    pendingPaths.delete(to.fullPath)
    next()
  }
})

export default router
