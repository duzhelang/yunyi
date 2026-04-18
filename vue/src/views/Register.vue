<template>
  <div class="wrapper">
    <!-- 背景图层 -->
    <div class="bg-pattern"></div>

    <!-- 注册卡片 -->
    <div class="auth-card">
      <div class="title">云医智护--糖尿病诊断系统</div>
      <div class="subtitle">账号注册</div>
      <el-form :model="user" :rules="rules" ref="userForm" class="auth-form">
        <el-form-item prop="username">
          <el-input
              placeholder="请输入账号"
              size="large"
              :prefix-icon="User"
              v-model="user.username"
              clearable
          ></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              v-model="user.password"
              clearable
          ></el-input>
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
              placeholder="请确认密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              v-model="user.confirmPassword"
              clearable
          ></el-input>
        </el-form-item>
        <el-form-item class="btn-group">
          <el-button
              type="primary"
              size="medium"
              class="auth-btn primary"
              @click="register"
          >
            注册
          </el-button>
          <el-button
              type="info"
              size="medium"
              class="auth-btn secondary"
              @click="$router.push('/login')"
          >
            返回登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import { User, Lock } from '@element-plus/icons-vue';

export default {
  name: "Register",
  components: { User, Lock },
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('请确认密码'));
      }
      if (value !== this.user.password) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };

    return {
      user: {},
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 16, message: '用户名长度需在3-16个字符之间', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度需在6-20个字符之间', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, trigger: 'blur', validator: validateConfirmPassword }
        ]
      }
    };
  },
  methods: {
    register() {
      this.$refs.userForm.validate((valid) => {
        if (valid) {
          request.post("/user/register", this.user).then(res => {
            if (res.code === '200') {
              ElMessage.success("注册成功,即将跳转到登录页");
              setTimeout(() => {
                this.$router.push('/login');
              }, 1500);
            } else {
              ElMessage.error(res.msg);
            }
          }).catch(err => {
            ElMessage.error("网络异常,请稍后重试");
            console.error("注册请求失败:", err);
          });
        } else {
          ElMessage.warning("请完善注册信息");
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

/* 背景纹理 */
.bg-pattern {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:
  url('../assets/1.jpeg'),
      radial-gradient(circle at 10% 10%, rgba(255,255,255,0.1) 0%, transparent 30%),
      radial-gradient(circle at 90% 90%, rgba(255,255,255,0.1) 0%, transparent 30%);
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-blend-mode: overlay;
  z-index: 0;
}

/* 统一的认证卡片样式 */
.auth-card {
  width: 420px;
    max-width: 90vw;
    padding: 48px 50px;
    box-sizing: border-box;
    overflow: hidden;
  
    /* 核心:蓝调渐变+低透明度,适配背景的通透磨砂感 */
    background: linear-gradient(
      135deg, 
      rgba(120, 120, 120, 0.5) 0%,   /* 浅蓝高光区,贴近背景色调 */
      rgba(255, 255, 255, 0.25) 50%,  /* 青蓝过渡区,降低透明度增强通透 */
      rgba(255, 255, 255, 0.6) 100%  /* 白色收尾,平衡整体亮度 */
    );
    /* 取消纯色背景,避免覆盖渐变,仅保留渐变层即可 */
  
    /* 磨砂核心:统一模糊值+全浏览器前缀,保证效果一致 */
    -webkit-backdrop-filter: blur(3.5px); /* Safari 兼容 */
    backdrop-filter: blur(7px);         /* 主模糊值,3px偏弱,6px磨砂感更明显 */
    -moz-backdrop-filter: blur(6px);    /* Firefox 兼容 */
    -o-backdrop-filter: blur(6px);      /* Opera 兼容 */
    -ms-backdrop-filter: blur(6px);     /* IE 兼容 */
  
    /* 边框:匹配蓝调渐变,增强磨砂边缘质感 */
    /* border: 1px solid rgba(142, 193, 240, 0.7); /* 替换纯白边框,贴合蓝调风格 */ 
    border-radius: 24px;
  
    /* 阴影:替换暖色调内层阴影,适配蓝调风格,增强磨砂层次 */
	box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
    /* box-shadow: 
      0 8px 32px rgba(0, 0, 0, 0.08),                
      inset 0 1px 0 rgba(255, 255, 255, 0.9),        
      inset 0 -1px 0 rgba(142, 193, 240, 0.5);       
  */
    /* 基础布局+交互 */
    text-align: center;
    transition: all 0.3s ease;
    z-index: 1;
  }


.auth-card:hover {
  transform: translateY(-5px);
  backdrop-filter: blur(10px);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.15);
}

.title {
  font-size: 22px;
  font-weight: 600;
  color: #1a365d;
  line-height: 1.4;
  margin-bottom: 8px;
}

.subtitle {
  font-size: 18px;
  color: #4a90e2;
  font-weight: 500;
  margin-bottom: 24px;
}

/* 表单样式 - Element Plus */
.auth-form :deep(.el-input__wrapper) {
  border-radius: 14px;
  border: 1px solid #dce6f4;
  padding-left: 44px;
  transition: all 0.2s ease;
  font-size: 15px;
  box-shadow: none;
}

.auth-form :deep(.el-input__wrapper.is-focus) {
  border-color: #4a90e2;
  box-shadow: 0 0 0 3px rgba(74, 144, 226, 0.1) !important;
}

.auth-form :deep(.el-input__prefix) {
  left: 14px;
}

.auth-form :deep(.el-input__suffix) {
  right: 14px;
}

.auth-form :deep(.el-input--large) {
  height: 52px;
}

/* 错误提示样式优化 */
.auth-form :deep(.el-form-item__error) {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
  text-align: left;
  padding-left: 14px;
}

/* 按钮组样式 */
.btn-group {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
}

/* 统一按钮样式 */
.auth-btn {
  padding: 12px 24px;
  border-radius: 12px;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.25s ease;
  width: 120px;
}

.auth-btn.primary {
  background-color: #4a90e2;
  color: white;
  border: none;
  box-shadow: 0 2px 6px rgba(74, 144, 226, 0.3);
}

.auth-btn.primary:hover {
  background-color: #357abd;
  box-shadow: 0 4px 10px rgba(74, 144, 226, 0.4);
  transform: translateY(-2px);
}

.auth-btn.secondary {
  background-color: transparent;
  color: #4a90e2;
  border: 1px solid #4a90e2;
}

.auth-btn.secondary:hover {
  background-color: rgba(74, 144, 226, 0.1);
  border-color: #4a90e2;
  color: #4a90e2;
}
</style>