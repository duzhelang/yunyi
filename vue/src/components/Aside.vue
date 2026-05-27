<template>
  <el-menu :openeds="opens" :style="{ width: isCollapse ? '80px' : '200px' }" :class="{ 'is-collapse': isCollapse }" style="min-height: 100%; overflow-x: hidden" background-color="transparent"
           text-color="rgba(50, 50, 50, 0.95)" active-text-color="#5eead4" :collapse-transition="false" :collapse="isCollapse" router
           class="aside-menu">
    <div class="sidebar-header" :class="{ 'sidebar-header--collapse': isCollapse }">
      <div class="logo-title" v-show="!isCollapse">云医智护</div>
      <div class="logo-subtitle" v-show="!isCollapse">糖尿病诊断系统</div>
      <div class="logo-icon-only" v-show="isCollapse">
        <div class="logo-icon-circle">云</div>
        <div class="logo-icon-label">云医智护</div>
      </div>
    </div>
    <div v-for="item in menus" :key="item.id">
      <!-- 父菜单（有子菜单） -->
      <div v-if="item.children && item.children.length">
        <el-sub-menu :index="item.id + ''">
          <template #title>
            <el-icon v-if="item.icon">
              <component :is="getIconComponent(item.icon)" />
            </el-icon>
            <el-icon v-else><Menu /></el-icon>
            <span class="menu-title-text">{{ item.name }}</span>
          </template>
          <div v-for="subItem in item.children" :key="subItem.id">
            <el-menu-item v-if="subItem.path || subItem.pagePath || subItem.page_path" :index="getMenuIndex(subItem)">
              <el-icon v-if="subItem.icon">
                <component :is="getIconComponent(subItem.icon)" />
              </el-icon>
              <el-icon v-else><Menu /></el-icon>
              <template #title><span class="menu-title-text">{{ subItem.name }}</span></template>
            </el-menu-item>
          </div>
        </el-sub-menu>
      </div>
      <!-- 一级叶子菜单（有路径） -->
      <div v-else-if="item.path || item.pagePath || item.page_path" class="top-level-leaf">
        <el-menu-item :index="getMenuIndex(item)">
          <el-icon v-if="item.icon">
            <component :is="getIconComponent(item.icon)" />
          </el-icon>
          <el-icon v-else><Menu /></el-icon>
          <template #title><span class="menu-title-text">{{ item.name }}</span></template>
        </el-menu-item>
      </div>
    </div>
  </el-menu>
</template>

<script>
import { ref, onMounted } from 'vue'
import { Menu, Coffee, Document, House, User, Grid, Upload, Download, Delete, Edit, Plus, Search, Message, ArrowDown, Loading, Check, Close, Top, Bottom, Warning, Setting, DataAnalysis, PieChart, Tools, List, VideoPlay, OfficeBuilding, DocumentCopy, Reading, EditPen, Folder, FolderOpened, DataLine, Avatar, Cpu, InfoFilled, Odometer, ChatDotRound } from '@element-plus/icons-vue';
import { CacheHelper } from "@/utils/cacheHelper";

export default {
  name: "Aside",
  components: { Menu, Coffee, Document, House, User, Grid, Upload, Download, Delete, Edit, Plus, Search, Message, ArrowDown, Loading, Check, Close, Top, Bottom, Warning, Setting, DataAnalysis, PieChart, Tools, List, VideoPlay, OfficeBuilding, DocumentCopy, Reading, EditPen, Folder, FolderOpened, DataLine, Avatar, Cpu, InfoFilled, Odometer, ChatDotRound },
  props: {
    isCollapse: {
      type: Boolean,
      default: false
    },
    logoTextShow: {
      type: Boolean,
      default: true
    }
  },
  setup(props) {
    const menus = ref([])
    const opens = ref([])

    // 获取菜单索引
    const getMenuIndex = (menuItem) => {
      if (menuItem.path) {
        return menuItem.path.startsWith('/') ? menuItem.path.toLowerCase() : '/' + menuItem.path.toLowerCase();
      }
      let pagePathVal = menuItem.pagePath || menuItem.page_path;
      return pagePathVal ? '/' + pagePathVal.toLowerCase() : '';
    }

    // 图标映射函数组件名
    const getIconComponent = (iconName) => {
      const iconMap = {
        'el-icon-coffee': 'Coffee',
        'el-icon-document': 'Document',
        'el-icon-house': 'House',
        'el-icon-user': 'User',
        'el-icon-s-grid': 'Grid',
        'el-icon-upload': 'Upload',
        'el-icon-upload2': 'Upload',
        'el-icon-download': 'Download',
        'el-icon-remove-outline': 'Delete',
        'el-icon-edit': 'Edit',
        'el-icon-plus': 'Plus',
        'el-icon-circle-plus-outline': 'Plus',
        'el-icon-search': 'Search',
        'el-icon-message': 'Message',
        'el-icon-arrow-down': 'ArrowDown',
        'el-icon-loading': 'Loading',
        'el-icon-question': 'Menu',
        'el-icon-time': 'Menu',
        'el-icon-circle-check': 'Check',
        'el-icon-close': 'Close',
        'el-icon-top': 'Top',
        'el-icon-bottom': 'Bottom',
        'el-icon-warning': 'Warning',
        'el-icon-data-analysis': 'DataAnalysis',
        'el-icon-dashboard': 'Odometer',
        'el-icon-pie-chart': 'PieChart',
        'el-icon-setting': 'Setting',
        'el-icon-chat-dot-round': 'ChatDotRound',
        'el-icon-tools': 'Tools',
        'el-icon-list': 'List',
        'el-icon-video-play': 'VideoPlay',
        'el-icon-office-building': 'OfficeBuilding',
        'el-icon-document-copy': 'DocumentCopy',
        'el-icon-reading': 'Reading',
        'el-icon-edit-pen': 'EditPen',
        'el-icon-folder': 'Folder',
        'el-icon-folder-opened': 'FolderOpened',
        'el-icon-data-line': 'DataLine',
        'el-icon-s-custom': 'Avatar',
        'el-icon-cpu': 'Cpu',
        'el-icon-menu': 'Menu',
        'el-icon-s-data': 'DataLine',
        'el-icon-info': 'InfoFilled',
        'el-icon-book': 'Reading',
        'el-icon-alert': 'Warning',
        'el-icon-s-marketing': 'DataAnalysis'
      };
      return iconMap[iconName] || 'Menu';
    }

    onMounted(() => {
      try {
        const storedMenus = CacheHelper.get('menus');
        if (storedMenus) {
          let parsed = typeof storedMenus === 'string' ? JSON.parse(storedMenus) : storedMenus;
          parsed = parsed.map(item => {
            if (item.children && item.children.length) {
              return { ...item, children: item.children.filter(c => c.name !== '我的报告') };
            }
            return item;
          }).filter(item => item.name !== '我的报告');
          menus.value = parsed;
          const userSvc = parsed.find(v => v.name === '用户服务');
          opens.value = parsed.map(v => v.id + '');
          if (userSvc && !opens.value.includes(userSvc.id + '')) {
            opens.value.push(userSvc.id + '');
          }
        }
      } catch (error) {
        console.error('解析菜单数据失败:', error);
        menus.value = [];
        opens.value = [];
      }
    })

    return {
      menus,
      opens,
      getMenuIndex,
      getIconComponent
    }
  }
}
</script>

<style>
.aside-menu {
  position: fixed !important;
  top: 0;
  left: 0;
  height: 100vh !important;
  z-index: 999 !important;
  overflow-y: auto;
  background: linear-gradient(to right, #6a7077, #acb0b6, #f0f7fff7) !important;
  border-right: none !important;
  box-shadow: 1px 0 6px rgba(0, 0, 0, 0.12);
  transition: width 0.3s ease;
}

.aside-menu::-webkit-scrollbar {
  width: 4px;
}

.aside-menu::-webkit-scrollbar-track {
  background: transparent;
}

.aside-menu::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
}

.aside-menu::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.35);
}

.sidebar-header--collapse {
  padding: 16px 0 12px !important;
}

.sidebar-header--collapse .logo-icon-only {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.sidebar-header--collapse .logo-icon-circle {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: linear-gradient(135deg, #5eead4, #3b82f6);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  font-weight: 700;
  color: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.sidebar-header--collapse .logo-icon-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
  letter-spacing: 1px;
  white-space: nowrap;
}

.el-menu--collapse .el-sub-menu__title .el-sub-menu__icon-arrow {
  display: none;
}

.el-menu--collapse .el-menu-item,
.el-menu--collapse .el-sub-menu__title {
  height: auto;
  padding: 10px 0 !important;
  text-align: center;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  line-height: normal;
  margin: 4px 6px;
}

.el-menu--collapse .top-level-leaf .el-menu-item {
  padding: 14px 0 !important;
  margin: 6px 6px;
}

.el-menu--collapse .top-level-leaf .el-menu-item .el-icon {
  font-size: 24px;
  margin-bottom: 5px;
}

.el-menu--collapse .top-level-leaf .menu-title-text {
  font-size: 13px;
}

.el-menu--collapse .el-menu-item .el-icon,
.el-menu--collapse .el-sub-menu__title > .el-icon {
  margin-right: 0;
  margin-bottom: 4px;
  font-size: 22px;
}

.el-menu--collapse .menu-title-text {
  display: block;
  font-size: 12px;
  line-height: 1.3;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 70px;
}

.el-menu-item,
.el-sub-menu__title {
  margin: 2px 8px;
  border-radius: 8px;
  transition: background-color 0.2s ease, color 0.15s ease;
  height: 44px;
  line-height: 44px;
}

.el-menu-item:hover,
.el-sub-menu__title:hover {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

.el-menu-item.is-active {
  background: rgba(255, 255, 255, 0.2) !important;
  border-left: none !important;
  color: #001d3b!important;
  font-weight: 400;
  position: relative;
  backdrop-filter: blur(4px);
}

.el-menu-item.is-active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 55%;
  background: #5eead4;
  border-radius: 0 3px 3px 0;
}

.sidebar-header {
  padding: 22px 0 18px;
  text-align: center;
  background: linear-gradient(to right, #585e65, #8a8f96, #a8adb3);
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

.logo-title {
  font-size: 25px;
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 4px;
  letter-spacing: 2px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.15);
}

.logo-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.75);
  letter-spacing: 1px;
}

.el-sub-menu .el-menu {
  background: linear-gradient(to right, #5e646b, #9a9ea5, #c2c7ce) !important;
}

.el-sub-menu .el-menu .el-menu-item {
  padding-left: 28px !important;
  font-size: 17px;
  height: 40px;
  line-height: 40px;
}

.el-icon {
  margin-right: 8px;
  font-size: 16px;
}
</style>
