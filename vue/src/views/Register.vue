<template>
  <div class="wrapper">
    <!-- 背景图层 -->
    <div class="bg-pattern"></div>

    <!-- 注册卡片 -->
    <div class="auth-card">
      <div class="title">云医智护--糖尿病诊断系统</div>
      <div class="subtitle">用户注册</div>
      <el-form :model="user" :rules="rules" ref="userForm" class="auth-form">
        <!-- 基本信息 -->
        <div class="form-section">
          <div class="section-title">基本信息</div>
          <el-form-item prop="username">
            <el-input
                placeholder="请输入用户名（登录账号）"
                size="large"
                :prefix-icon="User"
                v-model="user.username"
                clearable
                @blur="checkUsername"
            >
              <template #append>
                <el-button :icon="usernameCheckIcon" @click="checkUsername" :loading="usernameChecking" />
              </template>
            </el-input>
          </el-form-item>
          <div class="username-tip" v-if="usernameStatus">{{ usernameTip }}</div>

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

          <el-form-item prop="realName">
            <el-input
                placeholder="请输入真实姓名"
                size="large"
                :prefix-icon="UserFilled"
                v-model="user.realName"
                clearable
            ></el-input>
          </el-form-item>

          <el-form-item prop="role">
            <el-select
                v-model="user.role"
                placeholder="请选择您的角色"
                size="large"
                style="width: 100%"
            >
              <el-option label="患者/普通用户" value="ROLE_WORKER"></el-option>
              <el-option label="医生/医护人员" value="ROLE_DOCTOR"></el-option>
              <el-option label="运维人员" value="ROLE_MAINTENANCE"></el-option>
            </el-select>
          </el-form-item>
        </div>

        <!-- 联系信息 -->
        <div class="form-section">
          <div class="section-title">联系信息</div>
          <div class="form-row">
            <el-form-item prop="sex" class="form-item-half">
              <el-select v-model="user.sex" placeholder="性别" size="large" style="width: 100%">
                <el-option label="男" value="男"></el-option>
                <el-option label="女" value="女"></el-option>
              </el-select>
            </el-form-item>

            <el-form-item prop="age" class="form-item-half">
              <el-input-number v-model="user.age" :min="0" :max="150" placeholder="年龄" style="width: 100%" size="large"></el-input-number>
            </el-form-item>
          </div>

          <el-form-item prop="phone">
            <el-input
                placeholder="请输入手机号（用于找回密码、接收通知）"
                size="large"
                :prefix-icon="Phone"
                v-model="user.phone"
                clearable
            ></el-input>
          </el-form-item>

          <el-form-item prop="email">
            <el-input
                placeholder="请输入邮箱（用于接收诊断报告）"
                size="large"
                :prefix-icon="Message"
                v-model="user.email"
                clearable
            ></el-input>
          </el-form-item>

          <el-form-item prop="address">
            <el-input
                placeholder="请输入地址（非必填）"
                size="large"
                :prefix-icon="Location"
                v-model="user.address"
                clearable
            ></el-input>
          </el-form-item>
        </div>

        <!-- 合规信息 -->
        <div class="form-section">
          <div class="section-title">合规与安全</div>
          <el-form-item prop="consent" class="consent-item">
            <el-checkbox v-model="user.consent">
              我同意上传个人健康数据用于AI辅助分析及科学研究（脱敏处理）
            </el-checkbox>
          </el-form-item>

          <el-form-item prop="emergencyContact">
            <el-input
                placeholder="请输入紧急联系人姓名（非必填）"
                size="large"
                :prefix-icon="Plus"
                v-model="user.emergencyContact"
                clearable
            ></el-input>
          </el-form-item>

          <el-form-item prop="emergencyRelation" v-if="user.emergencyContact">
            <el-select v-model="user.emergencyRelation" placeholder="与患者关系" size="large" style="width: 100%">
              <el-option label="父母" value="父母"></el-option>
              <el-option label="配偶" value="配偶"></el-option>
              <el-option label="子女" value="子女"></el-option>
              <el-option label="其他" value="其他"></el-option>
            </el-select>
          </el-form-item>
        </div>

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
import { User, Lock, UserFilled, Phone, Message, Location, Plus, Check, Close } from '@element-plus/icons-vue';

export default {
  name: "Register",
  components: { User, Lock, UserFilled, Phone, Message, Location, Plus, Check, Close },
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

    const validateConsent = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('请勾选知情同意书'));
      }
      callback();
    };

    return {
      user: {
        username: '',
        password: '',
        confirmPassword: '',
        realName: '',
        role: 'ROLE_WORKER',
        sex: '',
        age: null,
        phone: '',
        email: '',
        address: '',
        consent: false,
        emergencyContact: '',
        emergencyRelation: ''
      },
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
        ],
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '真实姓名长度需在2-20个字符之间', trigger: 'blur' }
        ],
        role: [
          { required: true, message: '请选择您的角色', trigger: 'change' }
        ],
        sex: [
          { required: true, message: '请选择性别', trigger: 'change' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        email: [
          { pattern: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/, message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        consent: [
          { validator: validateConsent, trigger: 'change' }
        ]
      },
      usernameChecking: false,
      usernameStatus: '',
      usernameTip: '',
      usernameCheckIcon: User
    };
  },
  methods: {
    checkUsername() {
      if (!this.user.username || this.user.username.length < 3 || this.user.username.length > 16) {
        this.usernameStatus = 'error';
        this.usernameTip = '用户名长度需在3-16个字符之间';
        this.usernameCheckIcon = Close;
        return;
      }

      this.usernameChecking = true;
      request.get("/user/checkUsername", {
        params: { username: this.user.username }
      }).then(res => {
        this.usernameChecking = false;
        if (res.data) {
          this.usernameStatus = 'error';
          this.usernameTip = '该用户名已被注册';
          this.usernameCheckIcon = Close;
        } else {
          this.usernameStatus = 'success';
          this.usernameTip = '该用户名可用';
          this.usernameCheckIcon = Check;
        }
      }).catch(() => {
        this.usernameChecking = false;
      });
    },
    register() {
      this.$refs.userForm.validate((valid) => {
        if (valid) {
          if (this.usernameStatus === 'error') {
            ElMessage.error('该用户名已被注册，请更换用户名');
            return;
          }

          const submitData = {
            username: this.user.username,
            password: this.user.password,
            nickname: this.user.realName,
            realName: this.user.realName,
            phone: this.user.phone,
            email: this.user.email,
            sex: this.user.sex,
            age: this.user.age,
            address: this.user.address,
            role: this.user.role,
            emergencyContact: this.user.emergencyContact,
            emergencyRelation: this.user.emergencyRelation
          };

          request.post("/user/register", submitData).then(res => {
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

/* 透明滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: rgba(74, 144, 226, 0.3);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(74, 144, 226, 0.5);
}

/* Firefox 透明滚动条 */
* {
  scrollbar-width: thin;
  scrollbar-color: rgba(74, 144, 226, 0.3) transparent;
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
  width: 560px;
  max-width: 92vw;
  max-height: 92vh;
  padding: 44px 52px;
  box-sizing: border-box;
  overflow-y: auto;

  background-color: rgba(255, 255, 255, 0.78);
  background: linear-gradient(
    135deg,
    rgba(142,193,240, 0.88) 0%,
    rgba(61,224,237, 0.38) 50%,
    rgba(255, 255, 255, 0.72) 100%);

  -webkit-backdrop-filter: blur(8px);
  backdrop-filter: blur(6px);
  -moz-backdrop-filter: blur(8px);

  border: 1px solid rgba(255, 255, 255, 0.92);
  border-radius: 32px;

  box-shadow:
    0 14px 44px rgba(0, 0, 0, 0.12),
    inset 0 1px 0 rgba(255, 255, 255, 0.88),
    inset 0 -1px 0 rgba(200, 170, 140, 0.6);

  text-align: center;
  transition: all 0.4s ease;
  z-index: 1;
}


.auth-card:hover {
  background-color: rgba(255, 255, 255, 0.58);
  box-shadow:
    0 18px 52px rgba(0, 0, 0, 0.16),
    inset 0 1px 0 rgba(255, 255, 255, 0.96),
    inset 0 -1px 0 rgba(255, 255, 255, 0.88);
  transform: translateY(-6px);
}

.title {
  font-size: 26px;
  font-weight: 700;
  color: #1a365d;
  line-height: 1.4;
  margin-bottom: 12px;
}

.subtitle {
  font-size: 20px;
  color: #4a90e2;
  font-weight: 600;
  margin-bottom: 36px;
}

/* 表单分区样式 */
.form-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #4a90e2;
  margin-bottom: 18px;
  padding-left: 4px;
  border-left: 4px solid #4a90e2;
  text-align: left;
}

.form-row {
  display: flex;
  gap: 18px;
}

.form-item-half {
  flex: 1;
}

/* 用户名检测提示 */
.username-tip {
  font-size: 14px;
  text-align: left;
  padding-left: 54px;
  margin-top: -18px;
  margin-bottom: 18px;
}

.username-tip.error {
  color: #f56c6c;
}

.username-tip.success {
  color: #67c23a;
}

/* Element Plus 覆盖样式 */
.auth-form :deep(.el-select) {
  width: 100%;
}

.auth-form :deep(.el-input-group__append) {
  background-color: #f5f7fa;
  border-radius: 0 20px 20px 0;
  border-left: none;
  padding: 0 18px;
}

.auth-form :deep(.el-input-group__append .el-button) {
  border: none;
  background: transparent;
}

.auth-form :deep(.el-checkbox) {
  text-align: left;
  line-height: 1.6;
  color: #606266;
  font-size: 15px;
}

.auth-form :deep(.el-checkbox__label) {
  white-space: normal;
  padding-left: 10px;
}

/* 表单样式 - Element Plus */
.auth-form :deep(.el-form-item) {
  margin-bottom: 22px;
}

.auth-form :deep(.el-input__wrapper) {
  border-radius: 20px;
  border: 2px solid #dce6f4;
  padding-left: 54px;
  padding-right: 20px;
  transition: all 0.3s ease;
  font-size: 17px;
  box-shadow: none;
  height: 60px;
}

.auth-form :deep(.el-input__wrapper:hover) {
  border-color: #b3d3f3;
}

.auth-form :deep(.el-input__wrapper.is-focus) {
  border-color: #4a90e2;
  box-shadow: 0 0 0 5px rgba(74, 144, 226, 0.18) !important;
  transform: translateY(-2px);
}

.auth-form :deep(.el-input__prefix) {
  left: 20px;
  font-size: 20px;
  color: #8da4c1;
}

.auth-form :deep(.el-input__suffix) {
  right: 20px;
}

.auth-form :deep(.el-input__suffix-inner) {
  opacity: 0.75;
  font-size: 17px;
}

.auth-form :deep(.el-input__inner) {
  font-size: 17px;
  color: #1a365d;
  height: 100%;
  line-height: 56px;
}

.auth-form :deep(.el-select .el-input__wrapper) {
  padding-left: 20px;
}

.auth-form :deep(.el-input-number) {
  width: 100%;
}

.auth-form :deep(.el-input-number .el-input__wrapper) {
  padding-left: 20px;
}

/* 错误提示样式优化 */
.auth-form :deep(.el-form-item__error) {
  color: #f56c6c;
  font-size: 13px;
  margin-top: 8px;
  text-align: left;
  padding-left: 18px;
  font-weight: 500;
}

/* 按钮组样式 */
.btn-group {
  display: flex;
  justify-content: center;
  gap: 28px;
  margin-top: 40px;
  width: 100%;
}

/* 统一按钮样式 */
.auth-btn {
  padding: 18px 44px;
  border-radius: 22px;
  font-weight: 700;
  font-size: 19px;
  transition: all 0.3s ease;
  flex: 1;
  max-width: 185px;
  text-align: center;
  height: 64px;
  line-height: 28px;
}

.auth-btn.primary {
  background: linear-gradient(135deg, #4a90e2 0%, #357abd 100%);
  color: white;
  border: none;
  box-shadow: 0 6px 18px rgba(74, 144, 226, 0.38);
}

.auth-btn.primary:hover {
  background: linear-gradient(135deg, #357abd 0%, #2c68a3 100%);
  box-shadow: 0 10px 24px rgba(74, 144, 226, 0.48);
  transform: translateY(-4px);
}

.auth-btn.secondary {
  background-color: transparent;
  color: #4a90e2;
  border: 2px solid #4a90e2;
}

.auth-btn.secondary:hover {
  background-color: rgba(74, 144, 226, 0.12);
  border-color: #357abd;
  color: #357abd;
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(74, 144, 226, 0.28);
}
</style>