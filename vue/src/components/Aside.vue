<template>
  <el-menu :openeds="opens" style="min-height: 100%; overflow-x: hidden" background-color="#1e293b"
           text-color="#fff" active-text-color="#ffd04b" :collapse-transition="false" :collapse="isCollapse" router
           class="aside-menu">
    <div class="sidebar-header" v-show="logoTextShow">
      <div class="logo-title">云医智护</div>
      <div class="logo-subtitle">糖尿病诊断系统</div>
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
            {{ item.name }}
          </template>
          <div v-for="subItem in item.children" :key="subItem.id">
            <el-menu-item v-if="subItem.path || subItem.pagePath || subItem.page_path" :index="getMenuIndex(subItem)">
              <el-icon v-if="subItem.icon">
                <component :is="getIconComponent(subItem.icon)" />
              </el-icon>
              <el-icon v-else><Menu /></el-icon>
              <template #title>{{ subItem.name }}</template>
            </el-menu-item>
          </div>
        </el-sub-menu>
      </div>
      <!-- 一级叶子菜单（有路径） -->
      <div v-else-if="item.path || item.pagePath || item.page_path">
        <el-menu-item :index="getMenuIndex(item)">
          <el-icon v-if="item.icon">
            <component :is="getIconComponent(item.icon)" />
          </el-icon>
          <el-icon v-else><Menu /></el-icon>
          <template #title>{{ item.name }}</template>
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

    // 图标映射函数，将旧的 el-icon-* 格式转换为新的组件名
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
          menus.value = typeof storedMenus === 'string' ? JSON.parse(storedMenus) : storedMenus;
          opens.value = menus.value.map(v => v.id + '');
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
  width: 200px !important;
  z-index: 999 !important;
  overflow-y: auto;
}

.el-menu--collapse span {
  visibility: hidden;
}

.el-menu-item.is-active {
  background-color: #3d5a80 !important;
}

.el-menu-item:hover {
  background-color: #3d5a80 !important;
}

.el-sub-menu__title:hover {
  background-color: #3d5a80 !important;
}

.sidebar-header {
  padding: 20px 0;
  text-align: center;
  background: linear-gradient(135deg, #1e293b, #334155);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-title {
  font-size: 18px;
  font-weight: bold;
  color: #ffffff;
  margin-bottom: 5px;
  letter-spacing: 1px;
}

.logo-subtitle {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  letter-spacing: 0.5px;
}
</style>
