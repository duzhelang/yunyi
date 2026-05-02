<template>
  <div class="person-container">
    <el-tabs v-model="activeTab" class="person-tabs">
      <!-- 模块1：个人基础信息 -->
      <el-tab-pane label="个人基础信息" name="basic">
        <div class="module-card">
          <div class="module-header">
            <h3>个人基础信息</h3>
            <p class="module-desc">维护您的个人基础资料，部分信息将用于系统通知与身份核验，请确保信息真实有效。</p>
          </div>

          <el-form :model="form" label-width="120px" class="person-form" :rules="rules" ref="basicFormRef">
            <el-form-item label="头像" class="avatar-item">
              <el-upload
                class="avatar-uploader"
                :action="'http://' + serverIpValue + ':9090/file/upload/avatar'"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
              >
                <img v-if="form.avatarUrl" :src="form.avatarUrl" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
              </el-upload>
              <span class="avatar-tip">点击头像可上传自定义头像，支持JPG/PNG格式，大小≤2MB</span>
            </el-form-item>

            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" disabled placeholder="系统唯一标识，不可修改">
                <template #suffix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
              <span class="field-tip">系统内唯一身份标识，不允许修改</span>
            </el-form-item>

            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="form.nickname" placeholder="请设置您的系统显示昵称"></el-input>
              <span class="field-tip">可编辑，修改后将在系统内对其他用户展示</span>
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请填写常用邮箱，用于密码找回与系统通知接收"></el-input>
              <span class="field-tip">修改后需验证邮箱有效性</span>
            </el-form-item>

            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请填写常用手机号，用于验证码与紧急通知接收"></el-input>
              <span class="field-tip">修改后需验证手机号有效性</span>
            </el-form-item>

            <el-form-item label="角色身份">
              <el-input :value="userRole" disabled>
                <template #suffix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
              <span class="field-tip">明确您的权限范围，避免权限误解</span>
            </el-form-item>

            <el-form-item label="注册时间">
              <el-input :value="formatCreateTime(form.createTime) || '系统记录'" disabled></el-input>
              <span class="field-tip">账号创建时间，便于溯源</span>
            </el-form-item>

            <el-form-item class="form-buttons">
              <el-button type="primary" @click="saveBasicInfo">保存修改</el-button>
              <el-button @click="resetBasicInfo">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>

      <!-- 模块2：账号安全中心 -->
      <el-tab-pane label="账号安全中心" name="security">
        <div class="module-card">
          <div class="module-header">
            <h3>账号安全中心</h3>
            <p class="module-desc">保障您的账号安全，建议定期更新密码，开启安全保护措施，降低账号被盗风险。</p>
          </div>

          <div class="security-list">
            <!-- 修改登录密码 -->
            <div class="security-item">
              <div class="security-item-left">
                <div class="security-item-title">
                  <el-icon><Lock /></el-icon>
                  <span>修改登录密码</span>
                </div>
                <div class="security-item-desc">定期修改密码可提升账号安全性，建议使用包含大小写字母、数字、特殊符号的组合密码</div>
              </div>
              <div class="security-item-right">
                <el-button type="primary" plain @click="showPasswordDialog">修改密码</el-button>
              </div>
            </div>

            <!-- 绑定手机号 -->
            <div class="security-item">
              <div class="security-item-left">
                <div class="security-item-title">
                  <el-icon><Phone /></el-icon>
                  <span>绑定手机号</span>
                </div>
                <div class="security-item-desc">当前绑定：{{ form.phone || '未绑定' }}</div>
                <div class="security-item-tip">绑定手机号可用于密码找回、登录验证与接收紧急通知</div>
              </div>
              <div class="security-item-right">
                <el-button type="primary" plain @click="showPhoneDialog">更换手机号</el-button>
              </div>
            </div>

            <!-- 绑定邮箱 -->
            <div class="security-item">
              <div class="security-item-left">
                <div class="security-item-title">
                  <el-icon><Message /></el-icon>
                  <span>绑定邮箱</span>
                </div>
                <div class="security-item-desc">当前绑定：{{ form.email || '未绑定' }}</div>
                <div class="security-item-tip">绑定邮箱可用于密码找回、接收系统公告与医疗报告通知</div>
              </div>
              <div class="security-item-right">
                <el-button type="primary" plain @click="showEmailDialog">更换邮箱</el-button>
              </div>
            </div>

            <!-- 登录设备管理 -->
            <div class="security-item">
              <div class="security-item-left">
                <div class="security-item-title">
                  <el-icon><Monitor /></el-icon>
                  <span>登录设备管理</span>
                </div>
                <div class="security-item-desc">查看您的账号登录过的所有设备，可下线异常登录设备</div>
                <div class="security-item-tip">显示设备名称、登录时间、IP地址、地点</div>
              </div>
              <div class="security-item-right">
                <el-button type="primary" plain @click="showDeviceDialog">查看设备</el-button>
              </div>
            </div>

            <!-- 安全日志 -->
            <div class="security-item">
              <div class="security-item-left">
                <div class="security-item-title">
                  <el-icon><Document /></el-icon>
                  <span>安全日志</span>
                </div>
                <div class="security-item-desc">查看账号登录、密码修改、绑定信息变更等关键操作记录</div>
                <div class="security-item-tip">便于您排查异常操作</div>
              </div>
              <div class="security-item-right">
                <el-button type="primary" plain @click="showLogDialog">查看日志</el-button>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- 模块3：系统偏好设置 -->
      <el-tab-pane label="系统偏好设置" name="preference">
        <div class="module-card">
          <div class="module-header">
            <h3>系统偏好设置</h3>
            <p class="module-desc">自定义您的系统使用偏好，优化日常操作体验。</p>
          </div>

          <div class="preference-list">
            <!-- 消息通知设置 -->
            <div class="preference-item">
              <div class="preference-item-header">
                <div class="preference-item-title">
                  <el-icon><Bell /></el-icon>
                  <span>消息通知设置</span>
                </div>
                <div class="preference-item-desc">设置您希望接收的系统通知类型与方式</div>
              </div>
              <div class="preference-item-content">
                <div class="switch-item">
                  <span>系统通知</span>
                  <el-switch v-model="preferences.systemNotify" active-text="开启" inactive-text="关闭" />
                  <span class="switch-desc">接收系统公告、版本更新通知</span>
                </div>
                <div class="switch-item">
                  <span>业务通知</span>
                  <el-switch v-model="preferences.businessNotify" active-text="开启" inactive-text="关闭" />
                  <span class="switch-desc">接收糖尿病预测结果、任务提醒、报修进度通知</span>
                </div>
                <div class="switch-item">
                  <span>邮件通知</span>
                  <el-switch v-model="preferences.emailNotify" active-text="开启" inactive-text="关闭" />
                  <span class="switch-desc">重要通知同步发送至绑定邮箱</span>
                </div>
                <div class="switch-item">
                  <span>短信通知</span>
                  <el-switch v-model="preferences.smsNotify" active-text="开启" inactive-text="关闭" />
                  <span class="switch-desc">重要通知同步发送至绑定手机号</span>
                </div>
              </div>
            </div>

            <!-- 界面主题设置 -->
            <div class="preference-item">
              <div class="preference-item-header">
                <div class="preference-item-title">
                  <el-icon><Brush /></el-icon>
                  <span>界面主题设置</span>
                </div>
                <div class="preference-item-desc">自定义系统界面主题，适配您的使用习惯</div>
              </div>
              <div class="preference-item-content">
                <div class="select-item">
                  <span>主题模式</span>
                  <el-select v-model="preferences.theme" placeholder="请选择" style="width: 200px;">
                    <el-option label="浅色模式" value="light" />
                    <el-option label="深色模式" value="dark" />
                    <el-option label="跟随系统" value="auto" />
                  </el-select>
                </div>
                <div class="select-item">
                  <span>侧边栏模式</span>
                  <el-select v-model="preferences.sidebarMode" placeholder="请选择" style="width: 200px;">
                    <el-option label="默认展开" value="expand" />
                    <el-option label="默认收起" value="collapse" />
                  </el-select>
                </div>
                <div class="select-item">
                  <span>字体大小</span>
                  <el-select v-model="preferences.fontSize" placeholder="请选择" style="width: 200px;">
                    <el-option label="小" value="small" />
                    <el-option label="中" value="medium" />
                    <el-option label="大" value="large" />
                  </el-select>
                </div>
              </div>
            </div>

            <!-- 医疗业务偏好 -->
            <div class="preference-item">
              <div class="preference-item-header">
                <div class="preference-item-title">
                  <el-icon><DataAnalysis /></el-icon>
                  <span>医疗业务偏好</span>
                </div>
                <div class="preference-item-desc">设置您在使用糖尿病预测功能时的默认偏好</div>
              </div>
              <div class="preference-item-content">
                <div class="select-item">
                  <span>默认模型版本</span>
                  <el-select v-model="preferences.defaultModel" placeholder="请选择" style="width: 200px;">
                    <el-option label="模型 v2.1.0" value="v2.1.0" />
                    <el-option label="模型 v2.2.0" value="v2.2.0" />
                  </el-select>
                  <span class="switch-desc">选择默认使用的AI预测模型版本</span>
                </div>
                <div class="select-item">
                  <span>结果展示方式</span>
                  <el-select v-model="preferences.resultDisplay" placeholder="请选择" style="width: 200px;">
                    <el-option label="表格优先" value="table" />
                    <el-option label="图表优先" value="chart" />
                  </el-select>
                  <span class="switch-desc">设置预测结果的默认展示形式</span>
                </div>
                <div class="switch-item">
                  <span>健康建议推送</span>
                  <el-switch v-model="preferences.healthAdvice" active-text="开启" inactive-text="关闭" />
                  <span class="switch-desc">根据您的预测结果推送个性化健康管理建议</span>
                </div>
              </div>
            </div>

            <!-- 语言设置 -->
            <div class="preference-item">
              <div class="preference-item-header">
                <div class="preference-item-title">
                  <el-icon><Message /></el-icon>
                  <span>语言设置</span>
                </div>
                <div class="preference-item-desc">设置系统界面的显示语言</div>
              </div>
              <div class="preference-item-content">
                <div class="select-item">
                  <span>界面语言</span>
                  <el-select v-model="preferences.language" placeholder="请选择" style="width: 200px;">
                    <el-option label="简体中文" value="zh-CN" />
                    <el-option label="English" value="en-US" />
                  </el-select>
                </div>
              </div>
            </div>
          </div>

          <div class="preference-buttons">
            <el-button type="primary" @click="savePreferences">保存设置</el-button>
            <el-button @click="resetPreferences">重置</el-button>
          </div>
        </div>
      </el-tab-pane>

      <!-- 模块4：数据与隐私管理 -->
      <el-tab-pane label="数据与隐私管理" name="privacy">
        <div class="module-card">
          <div class="module-header">
            <h3>数据与隐私管理</h3>
            <p class="module-desc">管理您的个人数据与隐私设置，系统将严格遵守《隐私政策》，保障您的数据安全合规。</p>
          </div>

          <div class="privacy-list">
            <!-- 个人数据导出 -->
            <div class="privacy-item">
              <div class="privacy-item-left">
                <div class="privacy-item-title">
                  <el-icon><Download /></el-icon>
                  <span>个人数据导出</span>
                </div>
                <div class="privacy-item-desc">导出您在系统内的所有个人数据、健康档案、预测记录</div>
                <div class="privacy-item-tip">支持PDF/Excel格式，系统将在1-3个工作日内将数据发送至您的绑定邮箱</div>
              </div>
              <div class="privacy-item-right">
                <el-button type="primary" plain @click="applyDataExport">申请导出</el-button>
              </div>
            </div>

            <!-- 数据使用授权 -->
            <div class="privacy-item">
              <div class="privacy-item-left">
                <div class="privacy-item-title">
                  <el-icon><Key /></el-icon>
                  <span>数据使用授权</span>
                </div>
                <div class="privacy-item-desc">管理您的健康数据、预测结果的使用授权，所有数据将被脱敏处理</div>
              </div>
              <div class="privacy-item-right">
                <div class="privacy-switches">
                  <div class="privacy-switch-item">
                    <span>用于模型优化</span>
                    <el-switch v-model="privacy.modelOptimize" active-text="允许" inactive-text="拒绝" />
                    <span class="privacy-switch-desc">允许您的匿名数据用于糖尿病预测模型的优化训练</span>
                  </div>
                  <div class="privacy-switch-item">
                    <span>用于统计分析</span>
                    <el-switch v-model="privacy.statistics" active-text="允许" inactive-text="拒绝" />
                    <span class="privacy-switch-desc">允许您的匿名数据用于系统业务统计分析</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 隐私政策查看 -->
            <div class="privacy-item">
              <div class="privacy-item-left">
                <div class="privacy-item-title">
                  <el-icon><Document /></el-icon>
                  <span>隐私政策查看</span>
                </div>
                <div class="privacy-item-desc">查看系统完整的隐私政策，了解您的数据如何被收集、使用与保护</div>
              </div>
              <div class="privacy-item-right">
                <el-button type="primary" plain @click="showPrivacyPolicy">查看完整隐私政策</el-button>
              </div>
            </div>

            <!-- 账号注销 -->
            <div class="privacy-item danger">
              <div class="privacy-item-left">
                <div class="privacy-item-title">
                  <el-icon><Delete /></el-icon>
                  <span>账号注销</span>
                </div>
                <div class="privacy-item-desc">注销账号将永久删除您的所有个人数据、健康档案与预测记录</div>
                <div class="privacy-item-tip danger-tip">此操作不可恢复，请谨慎操作</div>
              </div>
              <div class="privacy-item-right">
                <el-button type="danger" plain @click="applyAccountCancellation">申请注销</el-button>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- 管理员专属模块 -->
      <el-tab-pane label="系统快捷操作" name="admin" v-if="isAdmin">
        <div class="module-card">
          <div class="module-header">
            <h3>系统快捷操作</h3>
            <p class="module-desc">快速访问管理员常用功能，便捷进行系统管理。</p>
          </div>

          <div class="admin-shortcuts">
            <div class="shortcut-item" @click="goToPage('/user')">
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </div>
            <div class="shortcut-item" @click="goToPage('/role')">
              <el-icon><Key /></el-icon>
              <span>角色管理</span>
            </div>
            <div class="shortcut-item" @click="goToPage('/menu')">
              <el-icon><Menu /></el-icon>
              <span>菜单管理</span>
            </div>
            <div class="shortcut-item" @click="goToPage('/file')">
              <el-icon><Folder /></el-icon>
              <span>模型管理</span>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改登录密码" width="450px" destroy-on-close>
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入旧密码"></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码"></el-input>
          <div class="password-strength">
            密码强度：
            <span :class="passwordStrengthClass">{{ passwordStrengthText }}</span>
          </div>
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPasswordChange">确认修改</el-button>
      </template>
    </el-dialog>

    <!-- 设备管理对话框 -->
    <el-dialog v-model="deviceDialogVisible" title="登录设备管理" width="600px" destroy-on-close>
      <el-table :data="deviceList" stripe>
        <el-table-column prop="deviceName" label="设备名称" width="150"></el-table-column>
        <el-table-column prop="loginTime" label="登录时间" width="160"></el-table-column>
        <el-table-column prop="ipAddress" label="IP地址" width="130"></el-table-column>
        <el-table-column prop="location" label="地点"></el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="scope">
            <el-button type="danger" size="small" @click="logoutDevice(scope.row)">下线</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="deviceDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 安全日志对话框 -->
    <el-dialog v-model="logDialogVisible" title="安全日志" width="700px" destroy-on-close>
      <el-table :data="logList" stripe>
        <el-table-column prop="time" label="操作时间" width="160"></el-table-column>
        <el-table-column prop="type" label="操作类型" width="120"></el-table-column>
        <el-table-column prop="ipAddress" label="IP地址" width="130"></el-table-column>
        <el-table-column prop="location" label="地点"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === '成功' ? 'success' : 'danger'" size="small">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="logDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Lock, User, Phone, Message, Monitor, Document, Bell, Brush,
  DataAnalysis, Download, Key, Delete, Menu, Folder
} from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const serverIpValue = window.config ? window.config.serverIp : 'localhost'

const activeTab = ref('basic')
const basicFormRef = ref(null)
const passwordFormRef = ref(null)

// 用户信息
const form = ref({
  avatarUrl: '',
  username: '',
  nickname: '',
  email: '',
  phone: '',
  address: '',
  role: '',
  createTime: ''
})

const user = ref(localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : {})

const userRole = computed(() => {
  const roleMap = {
    'ROLE_ADMIN': '管理员',
    'ROLE_DOCTOR': '医生',
    'ROLE_WORKER': '普通用户',
    'ROLE_MAINTENANCE': '运维人员'
  }
  return roleMap[form.value.role] || form.value.role || '未知角色'
})

const isAdmin = computed(() => form.value.role === 'ROLE_ADMIN')

// 表单验证规则
const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度为2-20个字符', trigger: 'blur' }
  ]
}

// 密码表单
const passwordDialogVisible = ref(false)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const passwordStrengthClass = computed(() => {
  const pwd = passwordForm.value.newPassword
  if (!pwd) return 'strength-none'
  let strength = 0
  if (pwd.length >= 6) strength++
  if (/[a-z]/.test(pwd) && /[A-Z]/.test(pwd)) strength++
  if (/\d/.test(pwd)) strength++
  if (/[!@#$%^&*(),.?":{}|<>]/.test(pwd)) strength++

  if (strength <= 1) return 'strength-weak'
  if (strength <= 2) return 'strength-medium'
  return 'strength-strong'
})

const passwordStrengthText = computed(() => {
  const strengthClass = passwordStrengthClass.value
  if (strengthClass === 'strength-weak') return '弱'
  if (strengthClass === 'strength-medium') return '中'
  if (strengthClass === 'strength-strong') return '强'
  return '请输入密码'
})

// 设备管理
const deviceDialogVisible = ref(false)
const deviceList = ref([
  { deviceName: 'Chrome浏览器', loginTime: '2026-04-21 10:30:25', ipAddress: '192.168.1.100', location: '北京市' },
  { deviceName: '微信小程序', loginTime: '2026-04-20 15:22:10', ipAddress: '192.168.1.101', location: '北京市' },
  { deviceName: '手机Safari', loginTime: '2026-04-19 09:15:33', ipAddress: '10.0.0.50', location: '上海市' }
])

// 安全日志
const logDialogVisible = ref(false)
const logList = ref([
  { time: '2026-04-21 10:30:25', type: '账号登录', ipAddress: '192.168.1.100', location: '北京市', status: '成功' },
  { time: '2026-04-20 14:22:10', type: '修改密码', ipAddress: '192.168.1.100', location: '北京市', status: '成功' },
  { time: '2026-04-19 09:15:33', type: '修改手机号', ipAddress: '10.0.0.50', location: '上海市', status: '成功' },
  { time: '2026-04-18 16:45:22', type: '账号登录', ipAddress: '172.16.0.88', location: '广州市', status: '失败' }
])

// 偏好设置
const preferences = reactive({
  systemNotify: true,
  businessNotify: true,
  emailNotify: false,
  smsNotify: false,
  theme: 'light',
  sidebarMode: 'expand',
  fontSize: 'medium',
  defaultModel: 'v2.2.0',
  resultDisplay: 'table',
  healthAdvice: true,
  language: 'zh-CN'
})

// 隐私设置
const privacy = reactive({
  modelOptimize: false,
  statistics: false
})

// 生命周期
onMounted(() => {
  loadUserInfo()
})

// 方法
async function loadUserInfo() {
  const response = await request.get('/user/username/' + user.value.username)
  if (response.data) {
    form.value = { ...form.value, ...response.data }
  }
}

function handleAvatarSuccess(res) {
  form.value.avatarUrl = res
  ElMessage.success('头像上传成功')
}

function saveBasicInfo() {
  basicFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await request.post('/user/saveUpdateUser', form.value)
        if (res.code === '200') {
          ElMessage.success('保存成功！')
          loadUserInfo()
        } else {
          ElMessage.error(res.msg || '保存失败')
        }
      } catch (e) {
        ElMessage.error('保存失败，请稍后重试')
      }
    }
  })
}

function resetBasicInfo() {
  ElMessageBox.confirm(
    '您确定要重置个人信息吗？所有未保存的修改将丢失。',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    loadUserInfo()
    ElMessage.success('已重置')
  }).catch(() => {})
}

function showPasswordDialog() {
  passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  passwordDialogVisible.value = true
}

async function submitPasswordChange() {
  passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await request.post('/user/password', {
          password: passwordForm.value.oldPassword,
          newPassword: passwordForm.value.newPassword
        })
        if (res.code === '200') {
          ElMessage.success('密码修改成功，请使用新密码重新登录')
          passwordDialogVisible.value = false
          setTimeout(() => {
            router.push('/login')
          }, 1500)
        } else {
          ElMessage.error(res.msg || '密码修改失败')
        }
      } catch (e) {
        ElMessage.error('密码修改失败，请检查旧密码是否正确')
      }
    }
  })
}

function showPhoneDialog() {
  ElMessage.info('手机号更换功能开发中')
}

function showEmailDialog() {
  ElMessage.info('邮箱更换功能开发中')
}

function showDeviceDialog() {
  deviceDialogVisible.value = true
}

function logoutDevice(device) {
  ElMessageBox.confirm(
    '您确定要下线该设备吗？下线后该设备将无法再登录您的账号。',
    '确认下线',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    deviceList.value = deviceList.value.filter(d => d !== device)
    ElMessage.success('设备已下线')
  }).catch(() => {})
}

function showLogDialog() {
  logDialogVisible.value = true
}

function savePreferences() {
  ElMessage.success('偏好设置已保存')
}

function resetPreferences() {
  ElMessageBox.confirm(
    '您确定要重置所有偏好设置为默认值吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    Object.assign(preferences, {
      systemNotify: true,
      businessNotify: true,
      emailNotify: false,
      smsNotify: false,
      theme: 'light',
      sidebarMode: 'expand',
      fontSize: 'medium',
      defaultModel: 'v2.2.0',
      resultDisplay: 'table',
      healthAdvice: true,
      language: 'zh-CN'
    })
    ElMessage.success('已重置为默认值')
  }).catch(() => {})
}

function applyDataExport() {
  ElMessageBox.confirm(
    '导出申请已提交，数据将发送至您的绑定邮箱，请注意查收。',
    '导出申请',
    {
      confirmButtonText: '知道了',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(() => {
    ElMessage.success('导出申请已提交')
  }).catch(() => {})
}

function showPrivacyPolicy() {
  ElMessage.info('隐私政策页面开发中')
}

function applyAccountCancellation() {
  ElMessageBox.confirm(
    '您确定要注销账号吗？注销后所有数据将无法恢复。',
    '危险操作',
    {
      confirmButtonText: '确定注销',
      cancelButtonText: '取消',
      type: 'error',
      dangerouslyUseHTMLString: true
    }
  ).then(() => {
    ElMessage.error('账号注销功能暂未开放，请联系管理员')
  }).catch(() => {})
}

function formatCreateTime(time) {
  if (!time) return ''
  const date = new Date(time)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

function goToPage(path) {
  router.push(path)
}
</script>

<style scoped>
.person-container {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}

.person-tabs :deep(.el-tabs__header) {
  background: #fff;
  padding: 15px 20px;
  border-radius: 8px 8px 0 0;
  margin-bottom: 0;
}

.person-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.person-tabs :deep(.el-tabs__item) {
  font-size: 15px;
  font-weight: 500;
}

.person-tabs :deep(.el-tabs__item.is-active) {
  color: #409EFF;
}

.person-tabs :deep(.el-tabs__active-bar) {
  background-color: #409EFF;
}

.person-tabs :deep(.el-tabs__content) {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 0 0 8px 8px;
}

.module-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.module-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.module-header h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #303133;
}

.module-desc {
  margin: 0;
  font-size: 13px;
  color: #909399;
}

.person-form {
  max-width: 600px;
}

.avatar-item {
  margin-bottom: 24px;
}

.avatar-uploader {
  display: inline-block;
}

.avatar-uploader :deep(.el-upload) {
  border: 2px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s;
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 80px;
  height: 80px;
  line-height: 80px;
  text-align: center;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: block;
}

.avatar-tip {
  display: block;
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.field-tip {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: #c0c4cc;
}

.form-buttons {
  margin-top: 24px;
}

/* 安全中心样式 */
.security-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f9fafb;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  transition: all 0.3s;
}

.security-item:hover {
  border-color: #409EFF;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.security-item-left {
  flex: 1;
}

.security-item-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 6px;
}

.security-item-title .el-icon {
  color: #409EFF;
}

.security-item-desc {
  font-size: 13px;
  color: #606266;
  margin-bottom: 4px;
}

.security-item-tip {
  font-size: 12px;
  color: #909399;
}

/* 偏好设置样式 */
.preference-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.preference-item {
  padding: 20px;
  background: #f9fafb;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.preference-item-header {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.preference-item-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.preference-item-title .el-icon {
  color: #409EFF;
}

.preference-item-desc {
  font-size: 13px;
  color: #909399;
}

.preference-item-content {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.switch-item,
.select-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #606266;
}

.switch-item span:first-child,
.select-item span:first-child {
  min-width: 100px;
}

.switch-desc {
  font-size: 12px;
  color: #909399;
  margin-left: 8px;
}

.preference-buttons {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  text-align: left;
}

/* 隐私管理样式 */
.privacy-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.privacy-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px;
  background: #f9fafb;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.privacy-item.danger {
  border-color: #f56c6c;
  background: #fef0f0;
}

.privacy-item-left {
  flex: 1;
}

.privacy-item-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 6px;
}

.privacy-item-title .el-icon {
  color: #409EFF;
}

.privacy-item.danger .privacy-item-title .el-icon {
  color: #f56c6c;
}

.privacy-item-desc {
  font-size: 13px;
  color: #606266;
  margin-bottom: 4px;
}

.privacy-item-tip {
  font-size: 12px;
  color: #909399;
}

.privacy-item-tip.danger-tip {
  color: #f56c6c;
  font-weight: 500;
}

.privacy-switches {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.privacy-switch-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #606266;
}

.privacy-switch-desc {
  font-size: 12px;
  color: #909399;
  margin-left: 8px;
}

/* 管理员快捷方式 */
.admin-shortcuts {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.shortcut-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px 16px;
  background: #f9fafb;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  cursor: pointer;
  transition: all 0.3s;
}

.shortcut-item:hover {
  border-color: #409EFF;
  background: #ecf5ff;
  transform: translateY(-2px);
}

.shortcut-item .el-icon {
  font-size: 32px;
  color: #409EFF;
  margin-bottom: 12px;
}

.shortcut-item span {
  font-size: 14px;
  color: #606266;
}

/* 密码强度 */
.password-strength {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.strength-weak {
  color: #f56c6c;
  font-weight: 500;
}

.strength-medium {
  color: #e6a23c;
  font-weight: 500;
}

.strength-strong {
  color: #67c23a;
  font-weight: 500;
}
</style>