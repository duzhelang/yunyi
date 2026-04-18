<template>
  <div style="line-height: 60px; display: flex">
    <div style="flex: 1;">
      <span :class="collapseBtnClass" style="cursor: pointer; font-size: 18px" @click="collapse"></span>

      <el-breadcrumb separator="/" style="display: inline-block; margin-left: 10px">
        <el-breadcrumb-item :to="'/'">首页</el-breadcrumb-item>
        <el-breadcrumb-item>{{ currentPathName }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <el-dropdown style="width: 150px; cursor: pointer; text-align: right">
      <div style="display: inline-block">
        <img :src="user?.avatarUrl || ''" alt=""
             style="width: 30px; height: 30px; border-radius: 50%; position: relative; top: 10px; right: 5px">
        <span>{{ user?.nickname || '用户' }}</span>
        <el-icon style="margin-left: 5px"><ArrowDown /></el-icon>
      </div>
      <template #dropdown>
        <el-dropdown-menu style="width: 100px; text-align: center">
          <el-dropdown-item style="font-size: 14px; padding: 5px 0">
            <router-link to="/password">修改密码</router-link>
          </el-dropdown-item>
          <el-dropdown-item style="font-size: 14px; padding: 5px 0">
            <router-link to="/person">个人信息</router-link>
          </el-dropdown-item>
          <el-dropdown-item style="font-size: 14px; padding: 5px 0">
            <span style="text-decoration: none" @click="logout">退出</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script>
import { useMainStore } from "@/store"
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'

export default {
  name: "Header",
  components: { ArrowDown },
  props: {
    collapseBtnClass: String,
    user: Object
  },
  computed: {
    currentPathName () {
      const store = useMainStore()
      return store.getCurrentPathName
    }
  },
  data() {
    return {

    }
  },
  methods: {
    collapse() {
      this.$emit("asideCollapse")
    },
    logout() {
      const store = useMainStore()
      store.logout()
      ElMessage.success("退出成功")
    }
  }
}
</script>

<style scoped>
</style>
