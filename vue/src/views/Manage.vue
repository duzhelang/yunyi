<template>
  <div class="manage-wrapper">
    <GlobalParticleBg :show-switcher="true" />

    <el-container class="manage-container">

      <el-aside :width="sideWidth + 'px'" class="manage-aside">
        <Aside :isCollapse="isCollapse" :logoTextShow="logoTextShow" style="padding-bottom: 20px" />
      </el-aside>

      <el-container class="manage-main-container">
        <el-header class="manage-header">
          <Header :collapseBtnClass="collapseBtnClass" @asideCollapse="collapse" :user="user" />
        </el-header>

        <el-main class="manage-main">
          <router-view :key="$route.fullPath" @refreshUser="getUser" />
        </el-main>

      </el-container>
    </el-container>
  </div>
</template>

<script>

import Aside from "@/components/Aside.vue";
import Header from "@/components/Header.vue";
import GlobalParticleBg from "@/components/GlobalParticleBg.vue";
import request from "@/utils/request";
import { setRoutes } from "@/router";

export default {
  name: 'Home',
  data() {
    return {
      collapseBtnClass: 'Fold',
      isCollapse: false,
      sideWidth: 200,
      logoTextShow: true,
      user: {}
    }
  },
  components: {
    Aside,
    Header,
    GlobalParticleBg
  },
  created() {
    this.getUser()
  },
  methods: {
    collapse() {  // 点击收缩按钮触发
      this.isCollapse = !this.isCollapse
      if (this.isCollapse) {  // 收缩
        this.sideWidth = 64
        this.collapseBtnClass = 'Expand'
        this.logoTextShow = false
      } else {   // 展开
        this.sideWidth = 200
        this.collapseBtnClass = 'Fold'
        this.logoTextShow = true
      }
    },
    getUser() {
      let username = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).username : ""
      if (username) {
        request.get("/user/username/" + username).then(res => {
          this.user = res.data
        })
      }
    }
  }
}
</script>

<style scoped>
.manage-wrapper {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
}

.manage-container {
  position: relative;
  z-index: 1;
  min-height: 100vh;
}

.manage-aside {
  box-shadow: 1px 0 6px rgba(0, 0, 0, 0.12);
  transition: width 0.3s ease;
  background: linear-gradient(to right, #6a7077, #acb0b6, #d3d8df);
}

.manage-main-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: transparent;
}

.manage-header {
  border-bottom: 1px solid rgba(228, 231, 237, 0.6);
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.manage-main {
  padding: 20px 24px;
  background: rgba(255, 255, 250, 0.99);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  min-height: calc(100vh - 60px);
}

/* 优化工作区域内文字和构件样式 */
.manage-main :deep(.el-card) {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: none;
  margin-bottom: 16px;
}

.manage-main :deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
  font-size: 14px;
}

.manage-main :deep(.el-table th) {
  background-color: #f5f7fa !important;
  color: #303133;
  font-weight: 600;
}

.manage-main :deep(.el-table td) {
  color: #606266;
}

.manage-main :deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
}

.manage-main :deep(.el-input__wrapper) {
  border-radius: 8px;
}

.manage-main :deep(.el-form-item__label) {
  font-weight: 500;
  color: #303133;
}

.manage-main :deep(.el-tag) {
  border-radius: 6px;
}

.manage-main :deep(.el-pagination) {
  margin-top: 16px;
}
</style>