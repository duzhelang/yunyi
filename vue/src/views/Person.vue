<template>
  <el-card style="width: 500px;">
    <el-form label-width="80px" size="small">
      <el-upload
          class="avatar-uploader"
	  :action="'http://' + serverIpValue +':9090/file/upload/avatar'"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
      >
        <img v-if="form.avatarUrl" :src="form.avatarUrl" class="avatar">
        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
      </el-upload>

      <el-form-item label="用户名">
        <el-input v-model="form.username" disabled autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="昵称">
        <el-input v-model="form.nickname" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="form.email" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="电话">
        <el-input v-model="form.phone" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="地址">
        <el-input type="textarea" v-model="form.address" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="saveUpdateUser">确 定</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { serverIp } from "../../public/config"
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

// 定义 emit 事件
const emit = defineEmits(['refreshUser'])

// 响应式数据
const serverIpValue = serverIp
const form = ref({
  avatarUrl: ""
})
const user = ref(localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {})

// 生命周期钩子
onMounted(() => {
  getUser().then(res => {
    console.log(res)
    form.value = res
  })
})

// 方法
async function getUser() {
  const response = await request.get("/user/username/" + user.value.username)
  return response.data
}

function saveUpdateUser() {
  request.post("/user/saveUpdateUser", form.value).then(res => {
    if (res.code === '200') {
      ElMessage.success("保存成功")

      // 触发父级更新User的方法
      emit("refreshUser")

      // 更新浏览器存储的用户信息
      getUser().then(res => {
        res.token = JSON.parse(localStorage.getItem("user")).token
        localStorage.setItem("user", JSON.stringify(res))
      })

    } else {
      ElMessage.error("保存失败")
    }
  })
}

function handleAvatarSuccess(res) {
  form.value.avatarUrl = res
}
</script>

<style>
.avatar-uploader {
  text-align: center;
  padding-bottom: 10px;
}
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 138px;
  height: 138px;
  line-height: 138px;
  text-align: center;
}
.avatar {
  width: 138px;
  height: 138px;
  display: block;
}
</style>
