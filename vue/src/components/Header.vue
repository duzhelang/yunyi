<template>
  <div style="line-height: 60px; display: flex">
    <div style="flex: 1;">
      <el-icon :size="18" style="cursor: pointer" @click="collapse">
        <component :is="collapseBtnClass" />
      </el-icon>

      <el-breadcrumb separator="/" style="display: inline-block; margin-left: 10px">
        <el-breadcrumb-item :to="'/'">首页</el-breadcrumb-item>
        <el-breadcrumb-item>{{ currentPathName }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <el-dropdown class="avatar-dropdown">
      <div class="avatar-container">
        <img :src="user?.avatarUrl || ''" alt="" class="avatar-image">
        <span class="avatar-name">{{ user?.nickname || '用户' }}</span>
        <el-icon><ArrowDown /></el-icon>
      </div>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="goToPassword">
            修改密码
          </el-dropdown-item>
          <el-dropdown-item @click="goToPerson">
            个人信息
          </el-dropdown-item>
          <el-dropdown-item>
            <span @click="logout">退出</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script>
import { computed } from 'vue'
import { ArrowDown, Fold, Expand } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { resetRouter, setRoutes } from '@/router'
import { useRouter, useRoute } from 'vue-router'

export default {
  name: 'Header',
  components: { ArrowDown, Fold, Expand },
  props: {
    collapseBtnClass: String,
    user: Object
  },
  emits: ['asideCollapse'],
  setup(props, { emit }) {
    const router = useRouter()
    const route = useRoute()

    // 当前路径名称
    const currentPathName = computed(() => route.name)

    // 侧边栏折叠
    const collapse = () => {
      emit('asideCollapse')
    }

    // 跳转到个人信息页面
    const goToPerson = () => {
      console.log('点击跳转到个人信息页面')
      console.log('当前路由:', router.getRoutes().map(r => ({path: r.path, name: r.name})))
      
      router.push('/person').catch(err => {
        console.error('跳转失败:', err)
        console.log('尝试重新设置路由...')
        setRoutes()
        setTimeout(() => {
          router.push('/person').catch(err2 => {
            console.error('二次跳转失败:', err2)
          })
        }, 100)
      })
    }

    // 跳转到修改密码页面
    const goToPassword = () => {
      console.log('点击跳转到修改密码页面')
      router.push('/password').catch(err => {
        console.error('跳转失败:', err)
      })
    }

    // 退出登录
    const logout = () => {
      // 1. 清空本地缓存
      localStorage.removeItem("user")
      localStorage.removeItem("menus")
      
      // 2. 重置动态路由
      resetRouter()
      
      // 3. 跳转登录页
      window.location.href = '/login'
    }

    return {
      currentPathName,
      collapse,
      logout,
      goToPerson,
      goToPassword
    }
  }
}
</script>

<style scoped>
/* 头像下拉框样式 */
.avatar-dropdown {
  position: relative;
}

.avatar-container {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px;
  border-radius: 20px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.avatar-container:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.avatar-image {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #409EFF;
  transition: all 0.3s ease;
}

.avatar-container:hover .avatar-image {
  transform: scale(1.1);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.avatar-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-right: 4px;
}

/* 下拉菜单样式 */
:deep(.el-dropdown-menu) {
  min-width: 120px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  padding: 4px 0;
  border: none;
}

:deep(.el-dropdown-item) {
  padding: 8px 16px;
  font-size: 14px;
  transition: all 0.3s ease;
}

:deep(.el-dropdown-item:hover) {
  background-color: #ecf5ff;
  color: #409EFF;
}

:deep(.el-dropdown-item:last-child) {
  border-bottom-left-radius: 8px;
  border-bottom-right-radius: 8px;
}

:deep(.el-dropdown-item:first-child) {
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
}
</style>
