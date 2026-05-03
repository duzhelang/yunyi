<template>
  <div class="wrapper">
    <!-- 背景图层 -->
    <div class="bg-pattern"></div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <div class="title">云医智护--糖尿病诊断系统</div>
      <el-form :model="user" ref="userForm" class="login-form">
        <el-form-item>
          <el-input
              size="large"
              :prefix-icon="User"
              v-model="user.username"
              placeholder="请输入用户名"
              clearable
          ></el-input>
        </el-form-item>

        <el-form-item>
          <el-input
              size="large"
              :prefix-icon="Lock"
              show-password
              v-model="user.password"
              placeholder="请输入密码"
              clearable
          ></el-input>
        </el-form-item>

        <el-form-item class="btn-group">
          <el-button
              type="info"
              @click.stop="goRegister"
              class="register-btn"
          >
            注册
          </el-button>
          <el-button
              type="primary"
              @click="login"
              class="login-btn"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { setRoutes } from "@/router";
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import { User, Lock } from '@element-plus/icons-vue';

export default {
  name: "Login",
  setup() {
    return { User, Lock }
  },
  data() {
    return {
      user: {}
    };
  },
  methods: {
    goRegister() {
      this.$router.push("/register");
    },
    login() {
      request.post("/user/login", this.user).then(res => {
        if (res.code === '200') {
          localStorage.setItem("user", JSON.stringify(res.data));
          localStorage.setItem("menus", JSON.stringify(res.data.menus));
          setRoutes();
          ElMessage.success("登录成功");

          // 登录成功后跳转到首页
          this.$router.push("/home");
        } else {
          ElMessage.error(res.msg);
        }
      });
    }
  }
};
</script>

<style scoped>
/* 全局重置 */
* {
	margin: 0;
	padding: 0;
	box-sizing: border-box;
}

.wrapper {
	height: 100vh;
	display: flex;
	justify-content: center;
	align-items: center;
	background: linear-gradient(135deg, #e0f2fe 0%, #b7e4ff 100%);
	position: relative;
	overflow: hidden;
}

/* 背景 */
.bg-pattern {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background-image: 
		url('../assets/3.jpeg'),
		radial-gradient(circle at 10% 10%, rgba(255,255,255,0.1) 0%, transparent 30%),
		radial-gradient(circle at 90% 90%, rgba(255,255,255,0.1) 0%, transparent 30%);
	background-size: cover;
	background-position: center;
	background-repeat: no-repeat;
	background-blend-mode: overlay;
	z-index: 0;
}

.login-card {
	width: 460px;
	max-width: 90vw;
	padding: 56px 60px;
	background-color: rgba(255, 255, 255, 0.75);
	background: linear-gradient(
		135deg,
		rgba(142,193,240, 0.85) 0%,
		rgba(61,224,237, 0.35) 50%,
		rgba(255, 255, 255, 0.7) 100%);
	-webkit-backdrop-filter: blur(6px);
	backdrop-filter: blur(4px);
	-moz-backdrop-filter: blur(6px);
	border: 1px solid rgba(255, 255, 255, 0.9);
	border-radius: 28px;
	box-shadow:
		0 12px 40px rgba(0, 0, 0, 0.1),
		inset 0 1px 0 rgba(255, 255, 255, 0.85),
		inset 0 -1px 0 rgba(200, 170, 140, 0.65);
	text-align: center;
	transition: all 0.4s ease;
	z-index: 1;
	box-sizing: border-box;
	overflow: hidden;
}

.login-card:hover {
	background-color: rgba(255, 255, 255, 0.55);
	box-shadow:
		0 16px 48px rgba(0, 0, 0, 0.15),
		inset 0 1px 0 rgba(255, 255, 255, 0.98),
		inset 0 -1px 0 rgba(255, 255, 255, 0.85);
	transform: translateY(-6px);
}

.title {
	font-size: 24px;
	font-weight: 700;
	color: #1a365d;
	margin-bottom: 40px;
	line-height: 1.4;
}

/* Element Plus 输入框样式 */
.login-form :deep(.el-input) {
  margin-bottom: 24px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 18px;
  border: 2px solid #dce6f4;
  padding-left: 52px;
  padding-right: 18px;
  transition: all 0.3s ease;
  font-size: 17px;
  box-shadow: none;
  height: 64px;
}

.login-form :deep(.el-input__wrapper:hover) {
  border-color: #b3d3f3;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: #4a90e2;
  box-shadow: 0 0 0 5px rgba(74, 144, 226, 0.18) !important;
  transform: translateY(-2px);
}

.login-form :deep(.el-input__prefix) {
  left: 18px;
  font-size: 20px;
  color: #8da4c1;
}

.login-form :deep(.el-input__suffix) {
  right: 18px;
}

.login-form :deep(.el-input__suffix-inner) {
  opacity: 0.75;
  font-size: 18px;
}

.login-form :deep(.el-input__inner) {
  font-size: 17px;
  color: #1a365d;
  height: 100%;
  line-height: 60px;
}

.btn-group {
  display: flex;
  justify-content: center;
  gap: 26px;
  margin-top: 36px;
  width: 100%;
}

.register-btn,
.login-btn {
  padding: 18px 40px;
  border-radius: 20px;
  font-weight: 700;
  font-size: 19px;
  transition: all 0.3s ease;
  border: none;
  min-width: 150px;
  text-align: center;
  flex: 1;
  max-width: 180px;
  height: 62px;
  line-height: 26px;
}

.register-btn {
  background-color: transparent;
  color: #4a90e2;
  border: 2px solid #4a90e2;
}

.register-btn:hover {
  background-color: rgba(74, 144, 226, 0.1);
  border-color: #357abd;
  color: #357abd;
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(74, 144, 226, 0.25);
}

.login-btn {
  background: linear-gradient(135deg, #4a90e2 0%, #357abd 100%);
  color: white;
  border: none;
  box-shadow: 0 4px 14px rgba(74, 144, 226, 0.35);
}

.login-btn:hover {
  background: linear-gradient(135deg, #357abd 0%, #2c68a3 100%);
  box-shadow: 0 8px 20px rgba(74, 144, 226, 0.45);
  transform: translateY(-4px);
}
</style>