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
              prefix-icon="el-icon-user"
              v-model="user.username"
              placeholder="请输入用户名"
              clearable
          ></el-input>
        </el-form-item>

        <el-form-item>
          <el-input
              size="large"
              prefix-icon="el-icon-lock"
              show-password
              v-model="user.password"
              placeholder="请输入密码"
              clearable
          ></el-input>
        </el-form-item>

        <el-form-item class="btn-group">
          <el-button
              type="info"
              size="medium"
              @click="$router.push('/register')"
              class="register-btn"
          >
            注册
          </el-button>
          <el-button
              type="primary"
              size="medium"
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

export default {
  name: "Login",
  data() {
    return {
      user: {}
      // 注意:已完全移除 rules 校验规则
    };
  },
  methods: {
    login() {
      // 不再校验,直接提交
      this.request.post("/user/login", this.user).then(res => {
        if (res.code === '200') {
          localStorage.setItem("user", JSON.stringify(res.data));
          localStorage.setItem("menus", JSON.stringify(res.data.menus));
          setRoutes();
          this.$message.success("登录成功");

          if (res.data.role === 'ROLE_WORKER') {
            this.$router.push("/home");
          } else {
            this.$router.push("/");
          }
        } else {
          this.$message.error(res.msg);
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
  width: 420px;
  max-width: 90vw;
  padding: 48px 50px;
  /* 核心1:更低的透明度 + 轻微渐变,增强通透感 */
  background-color: rgba(255, 255, 255, 0.7);
  background: linear-gradient(
    135deg, 
    rgba(142,193,240, 0.8) 0%, 
	rgba(61,224,237, 0.3) 50%,
    rgba(255, 255, 255, 0.65) 100%
  );
  /* 核心2:增强模糊 + 浏览器前缀全覆盖 */
  
  -webkit-backdrop-filter: blur(8px); /* Safari */
  backdrop-filter: blur(3.4px);
  -moz-backdrop-filter: blur(8px); /* Firefox 兼容 */
  /* 核心3:细边框 + 低透明度,模拟磨砂边缘 */
  border: 1px solid rgba(255, 255, 255, 0.85);
  border-radius: 24px;
  /* 核心4:多层阴影,模拟磨砂的光影层次 */
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.08), /* 外层柔阴影 */
    inset 0 1px 0 rgba(255, 255, 255, 0.8), /* 内层高光 */
    inset 0 -1px 0 rgba(200, 170, 140, 0.6); /* 内层暗边 */
  text-align: center;
  transition: all 0.3s ease;
  z-index: 1;
  /* 可选:轻微内边距阴影,增强磨砂立体感 */
  box-sizing: border-box;
  overflow: hidden;
}

.login-card:hover {
	background-color: rgba(255, 255, 255, 0.5);
	  box-shadow: 
	    0 12px 40px rgba(0, 0, 0, 0.12),
	    inset 0 1px 0 rgba(255, 255, 255, 0.95),
	    inset 0 -1px 0 rgba(255, 255, 255, 0.8);
  transform: translateY(-8px);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.15);
}

.title {
  font-size: 22px;
  font-weight: 600;
  color: #1a365d;
  margin-bottom: 32px;
  line-height: 1.4;
}

.login-form ::v-deep .el-input__inner {
  border-radius: 14px;
  border: 1px solid #dce6f4;
  padding-left: 44px;
  transition: all 0.2s ease;
  font-size: 15px;
}

.login-form ::v-deep .el-input__inner:focus {
  border-color: #4a90e2;
  box-shadow: 0 0 0 3px rgba(74, 144, 226, 0.1);
  outline: none;
}

.login-form ::v-deep .el-input__prefix {
  left: 14px;
}

.login-form ::v-deep .el-input__suffix {
  right: 14px;
}

.login-form ::v-deep .el-input__suffix-inner {
  opacity: 0.6;
}

.login-form ::v-deep .el-input--large .el-input__inner {
  height: 52px;
}

.btn-group {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
}

.register-btn,
.login-btn {
  padding: 12px 24px;
  border-radius: 12px;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.25s ease;
  border: none;
}

.register-btn {
  background-color: transparent;
  color: #4a90e2;
  border: 1px solid #4a90e2;
}

.register-btn:hover {
  background-color: rgba(74, 144, 226, 0.1);
  border-color: #4a90e2;
  color: #4a90e2;
}

.login-btn {
  background-color: #4a90e2;
  color: white;
  border: none;
  box-shadow: 0 2px 6px rgba(74, 144, 226, 0.3);
}

.login-btn:hover {
  background-color: #357abd;
  box-shadow: 0 4px 10px rgba(74, 144, 226, 0.4);
  transform: translateY(-2px);
}
</style>