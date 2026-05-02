<template>
  <div class="health-hub-pro">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <h1>🏥 AI 健康管理中心 Pro</h1>
      <p>智能自查 · 趋势追踪 · 医生协同</p>
    </div>

    <el-row :gutter="24">
      <!-- ================= 左侧：表单 + 建议 ================= -->
      <el-col :xs="24" :lg="16">
        <!-- 表单卡片 -->
        <el-card class="form-card">
          <template #header>
            <div class="card-header">
              <i class="el-icon-edit-outline"></i>
              <span>健康指标自查</span>
              <el-tag size="small" type="warning" effect="plain">诊断员在线</el-tag>
            </div>
          </template>

          <el-form :model="form" label-width="160px" size="default" class="health-form">
            <!-- 基础信息 -->
            <div class="section-title">👤 基础信息</div>
            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="年龄 (岁)" required>
                  <el-input-number v-model="form.Age" :min="1" :max="120" placeholder="岁" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="身高 (cm)" required>
                  <el-input-number v-model="temp.height" :min="50" :max="250" placeholder="厘米" @change="calcBMI" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="体重 (kg)" required>
                  <el-input-number v-model="temp.weight" :min="20" :max="300" placeholder="千克" @change="calcBMI" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="BMI (自动计算)" required>
              <el-input v-model="form.BMI" disabled>
                <template #append>kg/m²</template>
              </el-input>
              <div v-if="bmiFeedback.text" class="feedback-tag" :class="bmiFeedback.class">
                {{ bmiFeedback.text }}
              </div>
            </el-form-item>

            <!-- 医疗指标 -->
            <div class="section-title">🩺 核心医疗指标</div>
            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="空腹血糖 (mg/dL)" required>
                  <el-input-number v-model="form.Glucose" :precision="1" placeholder="mg/dL" @change="evaluateGlucose" />
                  <div v-if="glucoseFeedback.text" class="feedback-tag" :class="glucoseFeedback.class">
                    {{ glucoseFeedback.text }}
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="血压 (mmHg)">
                  <el-input-number v-model="form.BloodPressure" :min="40" :max="200" placeholder="mmHg" @change="evaluateBP" />
                  <div v-if="bpFeedback.text" class="feedback-tag" :class="bpFeedback.class">
                    {{ bpFeedback.text }}
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="胰岛素 (mU/L)">
                  <el-input-number v-model="form.Insulin" :precision="1" placeholder="mU/L" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="皮褶厚度 (mm)">
                  <el-input-number v-model="form.SkinThickness" placeholder="mm" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="糖尿病谱系函数" required>
                  <el-input-number v-model="form.DiabetesPedigreeFunction" :precision="3" :step="0.01" :min="0" :max="3" />
                  <div class="helper-text">* 家族遗传系数，不确定填 0.5</div>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="怀孕次数">
                  <el-input-number v-model="form.Pregnancies" :min="0" :max="20" placeholder="次" />
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 单位转换 -->
            <div class="converter-bar">
              <el-checkbox v-model="autoConvertGlucose" @change="handleGlucoseConvert">
                我已输入 mmol/L，自动转换为 mg/dL (×18)
              </el-checkbox>
            </div>

            <div class="section-title">📝 补充信息</div>
            <el-form-item label="自觉症状">
              <el-input v-model="form.symptoms" type="textarea" :rows="2" placeholder="如有多饮、多尿等症状请描述..." />
            </el-form-item>
            <el-form-item label="上传化验单">
              <el-upload ref="uploadRef" action="#" :auto-upload="false" :on-change="handleFileChange" :limit="1" :file-list="fileList">
                <el-button type="primary" plain round icon="el-icon-upload">选择文件</el-button>
              </el-upload>
            </el-form-item>
          </el-form>

          <!-- 操作按钮 -->
          <div class="action-bar">
            <el-button @click="resetForm" plain round :disabled="saving || submitting">重置</el-button>
            <el-button type="success" @click="saveOnly" :loading="saving" :disabled="submitting" round>💾 保存档案</el-button>
            <el-button type="primary" @click="submitToDoctor(null)" :loading="submitting" :disabled="saving" round>
              🚀 发送诊断员
            </el-button>
          </div>
        </el-card>

        <!-- 日常建议卡片 -->
        <el-card class="advice-card">
          <template #header>
            <div class="card-header">
              <i class="el-icon-s-order"></i>
              <span>日常健康管理建议</span>
            </div>
          </template>
          <div class="advice-grid">
            <div class="advice-item">
              <div class="adv-icon bg-green"><i class="el-icon-food"></i></div>
              <h3>饮食控制</h3>
              <ul>
                <li><b>粗细搭配</b>：主食增加燕麦、荞麦、糙米</li>
                <li><b>多吃蔬菜</b>：每日500g以上绿叶菜</li>
                <li><b>少油少盐</b>：食盐＜5g，避免油炸</li>
              </ul>
            </div>
            <div class="advice-item">
              <div class="adv-icon bg-blue"><i class="el-icon-running"></i></div>
              <h3>科学运动</h3>
              <ul>
                <li><b>频率</b>：每周150分钟中等强度运动</li>
                <li><b>时机</b>：餐后1小时运动，防低血糖</li>
                <li><b>抗阻</b>：每周2次力量训练</li>
              </ul>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- ================= 右侧：图表 + 历史 + 工具 ================= -->
      <el-col :xs="24" :lg="8">
        <!-- 趋势图表卡片（新增） -->
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <i class="el-icon-data-analysis"></i>
              <span>健康趋势追踪</span>
            </div>
          </template>
          <!-- 标签切换 -->
          <el-radio-group v-model="chartMode" size="small" class="chart-tabs">
            <el-radio-button label="glucose">血糖曲线</el-radio-button>
            <el-radio-button label="bmi">BMI 趋势</el-radio-button>
          </el-radio-group>
          <div ref="chartRef" class="chart-box"></div>
          <div v-if="historyList.length === 0" class="empty-chart">暂无数据，请先保存档案</div>
        </el-card>

        <!-- 历史记录卡片 -->
        <el-card class="history-card">
          <template #header>
            <div class="card-header">
              <i class="el-icon-time"></i>
              <span>自查历史</span>
            </div>
          </template>
          <div class="history-list">
            <div v-if="historyList.length === 0" class="empty-state">暂无历史记录</div>
            <div v-for="item in historyList" :key="item.id" class="history-item">
              <div class="item-header">
                <el-tag :type="STATUS_MAP[item.status]?.type" size="small" effect="dark">
                  {{ STATUS_MAP[item.status]?.text }}
                </el-tag>
                <span class="time">{{ formatTime(item.createTime) }}</span>
              </div>
              <div class="item-body">
                <span>血糖: <b>{{ item.glucose }}</b></span>
                <span>BMI: <b>{{ item.bmi }}</b></span>
                <span>年龄: <b>{{ item.age }}</b></span>
              </div>
              <div v-if="item.status === 'DONE' && item.diagnosisResult" class="diagnosis-box">
                👨‍⚕️ {{ item.diagnosisResult }}
              </div>
              <div class="item-actions">
                <el-button link size="small" @click="loadHistoryToForm(item)">载入</el-button>
                <el-button type="primary" size="small" round @click="submitToDoctor(item.id)" :loading="submittingId === item.id" :disabled="item.status === 'PENDING'">
                  {{ item.status === 'PENDING' ? '等待中' : '咨询' }}
                </el-button>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 工具箱卡片 -->
        <el-card class="tool-card">
          <template #header>
            <div class="card-header">
              <i class="el-icon-s-tools"></i>
              <span>糖友必备工具</span>
            </div>
          </template>
          <div class="tool-grid">
            <div class="tool-item" @click="openTool('emergency')">
              <div class="icon-box bg-red"><i class="el-icon-warning"></i></div>
              <span>低血糖急救</span>
            </div>
            <div class="tool-item" @click="openTool('carb-count')">
              <div class="icon-box bg-orange"><i class="el-icon-magic-stick"></i></div>
              <span>碳水计数法</span>
            </div>
            <div class="tool-item" @click="openTool('foot-care')">
              <div class="icon-box bg-green"><i class="el-icon-cpu"></i></div>
              <span>足部护理</span>
            </div>
          </div>
        </el-card>

        <!-- 每日打卡卡片 -->
        <el-card class="checkin-card">
          <template #header>
            <div class="card-header">
              <i class="el-icon-calendar-check"></i>
              <span>今日打卡</span>
            </div>
          </template>
          <div class="checkin-list">
            <div v-for="(task, idx) in checkList" :key="idx" class="checkin-item" @click="toggleCheck(idx)">
              <i :class="task.done ? 'el-icon-check' : 'el-icon-circle-outline'" class="check-icon" :style="{color: task.done ? '#67C23A' : '#DCDFE6'}"></i>
              <span :class="{ done: task.done }">{{ task.text }}</span>
            </div>
          </div>
          <div class="progress-section">
            <div class="progress-text">
              <span>完成度</span><span>{{ progressPercent }}%</span>
            </div>
            <el-progress :percentage="progressPercent" :stroke-width="8" color="#67C23A" :show-text="false" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'

export default {
  name: 'HealthHubPro',
  data() {
    return {
      // ================= 状态常量 =================
      STATUS_MAP: {
        DONE: { text: '✅ 已完成', type: 'success' },
        PENDING: { text: '⏳ 诊断中', type: 'warning' },
        SAVED: { text: '📝 已保存', type: 'info' }
      },

      // 表单主模型
      form: {
        Pregnancies: 0,
        Glucose: undefined,
        BloodPressure: undefined,
        SkinThickness: undefined,
        Insulin: undefined,
        BMI: undefined,
        DiabetesPedigreeFunction: 0.5,
        Age: undefined,
        symptoms: '',
        file: null
      },
      temp: {
        height: undefined,
        weight: undefined
      },

      // 即时反馈
      bmiFeedback: { text: '', class: '' },
      glucoseFeedback: { text: '', class: '' },
      bpFeedback: { text: '', class: '' },

      // 交互状态
      saving: false,
      submitting: false,
      submittingId: null,
      autoConvertGlucose: false,
      fileList: [],

      // 历史列表
      historyList: [],
      loadingHistory: false,

      // 打卡
      checkList: [
        { text: '早餐吃了粗粮/蔬菜', done: false },
        { text: '餐后散步 20 分钟', done: false },
        { text: '喝够 8 杯水', done: false },
        { text: '今晚 23:00 前睡觉', done: false }
      ],

      // 图表
      chartMode: 'glucose',
      chartInstance: null
    }
  },
  computed: {
    progressPercent() {
      const done = this.checkList.filter(t => t.done).length
      return Math.round((done / this.checkList.length) * 100)
    }
  },
  watch: {
    // 监听历史数据与模式变化，重新渲染图表
    historyList: { deep: true, handler() { this.$nextTick(() => this.renderChart()) } },
    chartMode() { this.$nextTick(() => this.renderChart()) }
  },
  mounted() {
    this.loadHistory()
  },
  methods: {
    // ================= BMI 计算（唯一入口） =================
    calcBMI() {
      if (!this.temp.height || !this.temp.weight) {
        this.form.BMI = undefined
        this.bmiFeedback = { text: '', class: '' }
        return
      }
      const h = this.temp.height / 100
      const bmi = this.temp.weight / (h * h)
      this.form.BMI = parseFloat(bmi.toFixed(2))

      // 评估状态（唯一 BMI 评估逻辑）
      let text = '', cls = ''
      if (bmi < 18.5) { text = '偏瘦'; cls = 'warning' }
      else if (bmi <= 23.9) { text = '正常'; cls = 'success' }
      else if (bmi < 28) { text = '超重'; cls = 'warning' }
      else { text = '肥胖'; cls = 'danger' }
      this.bmiFeedback = { text, class: cls }
    },

    // ================= 血糖评估（医学逻辑修正，单位 mg/dL） =================
    evaluateGlucose(val) {
      if (!val && val !== 0) {
        this.glucoseFeedback = { text: '', class: '' }
        return
      }
      let text = '', cls = ''
      if (val < 70) { text = '⚠️ 偏低（低血糖风险）'; cls = 'danger' }
      else if (val <= 110) { text = '✅ 正常'; cls = 'success' }
      else if (val < 126) { text = '⚠️ 糖尿病前期'; cls = 'warning' }
      else { text = '❗ 过高（疑似糖尿病）'; cls = 'danger' }
      this.glucoseFeedback = { text, class: cls }
    },

    // ================= 血压评估 =================
    evaluateBP(val) {
      if (!val) {
        this.bpFeedback = { text: '', class: '' }
        return
      }
      let text = '', cls = ''
      if (val < 90) { text = '偏低'; cls = 'warning' }
      else if (val <= 120) { text = '理想'; cls = 'success' }
      else if (val < 140) { text = '正常偏高'; cls = 'warning' }
      else { text = '高血压'; cls = 'danger' }
      this.bpFeedback = { text, class: cls }
    },

    // 单位转换
    handleGlucoseConvert(val) {
      if (!this.form.Glucose) return
      if (val && this.form.Glucose < 30) {
        this.form.Glucose = parseFloat((this.form.Glucose * 18).toFixed(1))
        ElMessage.info(`已转换为 ${this.form.Glucose} mg/dL`)
        this.evaluateGlucose(this.form.Glucose)
      } else if (!val && this.form.Glucose > 100) {
        this.form.Glucose = parseFloat((this.form.Glucose / 18).toFixed(1))
        ElMessage.info('已还原为 mmol/L')
        this.evaluateGlucose(this.form.Glucose)
      }
    },

    handleFileChange(uploadFile) {
      this.form.file = uploadFile.raw
      this.fileList = [uploadFile]
    },

    // ================= 保存 / 发送 =================
    validate() {
      if (!this.form.Age || !this.form.Glucose || !this.form.BMI) {
        ElMessage.warning('请填写年龄、血糖和 BMI（身高体重）')
        return false
      }
      return true
    },
    buildPayload() {
      const fd = new FormData()
      fd.append('Pregnancies', this.form.Pregnancies || 0)
      fd.append('Glucose', this.form.Glucose)
      fd.append('BloodPressure', this.form.BloodPressure || 0)
      fd.append('SkinThickness', this.form.SkinThickness || 0)
      fd.append('Insulin', this.form.Insulin || 0)
      fd.append('BMI', this.form.BMI)
      fd.append('DiabetesPedigreeFunction', this.form.DiabetesPedigreeFunction || 0)
      fd.append('Age', this.form.Age)
      fd.append('symptoms', this.form.symptoms)
      if (this.form.file) fd.append('file', this.form.file)
      return fd
    },
    extractId(res) {
      if (typeof res === 'number') return res
      if (typeof res === 'string' && !isNaN(Number(res))) return Number(res)
      if (typeof res === 'object') {
        if (res.id) return res.id
        if (res.data) return typeof res.data === 'number' ? res.data : res.data.id
        if (res.result) return res.result
      }
      return null
    },
    async saveOnly() {
      if (!this.validate()) return
      this.saving = true
      try {
        const res = await request.post('/api/health-profile/save', this.buildPayload(), {
          headers: { 'Content-Type': 'multipart/form-data' }
        })
        const id = this.extractId(res)
        if (id) {
          ElMessage.success(`档案保存成功 (ID: ${id})`)
          await this.loadHistory() // 刷新历史 + 图表
          return id
        } else {
          ElMessage.error('保存成功但无法获取 ID')
          return null
        }
      } catch (e) {
        ElMessage.error('保存失败')
        return null
      } finally { this.saving = false }
    },
    async submitToDoctor(existingId) {
      if (!this.validate()) return
      let targetId = existingId
      if (!targetId) {
        targetId = await this.saveOnly()
        if (!targetId) return
      }
      const finalId = Number(targetId)
      try {
        await ElMessageBox.confirm(`确认生成 CSV 并发送给诊断员？\n档案 ID: ${finalId}`, '确认发送', { type: 'warning' })
      } catch { return }

      this.submitting = !existingId
      this.submittingId = existingId
      try {
        const res = await request.post(`/api/health-profile/send-to-doctor/${finalId}`, {})
        const ok = !res || res.code === 200 || res.success === true
        ElMessage[ok ? 'success' : 'warning'](ok ? 'CSV 已生成，诊断员已通知' : (res.msg || '提交完成'))
        await this.loadHistory()
      } catch (e) {
        ElMessage.error('提交失败：' + (e.response?.data?.msg || e.message))
      } finally {
        this.submitting = false
        this.submittingId = null
      }
    },

    // ================= 历史记录 =================
    async loadHistory() {
      this.loadingHistory = true
      try {
        const res = await request.get('/api/health-profile/list')
        this.historyList = Array.isArray(res) ? res : (res.data || [])
        this.$nextTick(() => this.renderChart())
      } catch (e) {
        console.error('加载历史失败', e)
      } finally { this.loadingHistory = false }
    },
    loadHistoryToForm(item) {
      this.form.Pregnancies = item.pregnancies
      this.form.Glucose = item.glucose
      this.form.BloodPressure = item.bloodPressure
      this.form.SkinThickness = item.skinThickness
      this.form.Insulin = item.insulin
      this.form.BMI = item.bmi
      this.form.DiabetesPedigreeFunction = item.diabetesPedigreeFunction
      this.form.Age = item.age
      this.form.symptoms = item.symptoms || ''
      this.temp.height = null
      this.temp.weight = null
      this.calcBMI() // 重置 BMI 反馈
      this.evaluateGlucose(this.form.Glucose)
      this.evaluateBP(this.form.BloodPressure)
      this.fileList = []
      this.form.file = null
      ElMessage.info('已载入历史数据，需重新输入身高体重')
    },
    formatTime(ts) {
      if (!ts) return ''
      const d = new Date(ts)
      return `${d.getMonth() + 1}/${d.getDate()} ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
    },

    // ================= 图表渲染 =================
    renderChart() {
      if (!this.$refs.chartRef) return
      if (!this.chartInstance) {
        this.chartInstance = echarts.init(this.$refs.chartRef)
      }

      // 从 historyList 提取数据
      const sorted = [...this.historyList].sort((a, b) => new Date(a.createTime) - new Date(b.createTime))
      const times = sorted.map(item => this.formatTime(item.createTime))
      const glucoseData = sorted.map(item => item.glucose)
      const bmiData = sorted.map(item => item.bmi)

      const option = {
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: {
          type: 'category',
          data: times,
          axisLabel: { rotate: 30, fontSize: 10 }
        },
        yAxis: {
          type: 'value',
          name: this.chartMode === 'glucose' ? '血糖 (mg/dL)' : 'BMI',
          nameTextStyle: { fontSize: 11 }
        },
        series: [{
          data: this.chartMode === 'glucose' ? glucoseData : bmiData,
          type: 'line',
          smooth: true,
          showSymbol: true,
          symbol: 'circle',
          symbolSize: 6,
          lineStyle: { width: 2 },
          areaStyle: {
            opacity: 0.1,
            color: this.chartMode === 'glucose'
              ? new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#E6A23C' }, { offset: 1, color: '#fff' }])
              : new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#409EFF' }, { offset: 1, color: '#fff' }])
          },
          itemStyle: { color: this.chartMode === 'glucose' ? '#E6A23C' : '#409EFF' },
          markLine: {
            silent: true,
            data: this.chartMode === 'glucose'
              ? [{ yAxis: 110, label: { formatter: '上限', fontSize: 10 }, lineStyle: { type: 'dashed', color: '#F56C6C' } }]
              : []
          }
        }]
      }

      this.chartInstance.setOption(option, true)
    },

    // ================= 工具 & 打卡 =================
    openTool(type) {
      const tips = {
        emergency: '🚑 低血糖急救：立即食用 15g 快速升糖食品（如半杯果汁、3-4 颗糖），15 分钟后复测。若未缓解请立即就医。',
        'carb-count': '🍚 碳水计数法：每份碳水约含 15g 碳水化合物。例如：1小碗米饭≈2份碳水。',
        'foot-care': '🦶 足部护理指南：每天检查双脚有无伤口、水泡；穿宽松鞋袜；洗脚水温不超过 37℃；定期修剪指甲。'
      }
      ElMessage.info(tips[type] || '功能开发中...')
    },
    toggleCheck(idx) {
      this.checkList[idx].done = !this.checkList[idx].done
    },

    // 重置表单
    resetForm() {
      this.form = {
        Pregnancies: 0, Glucose: undefined, BloodPressure: undefined,
        SkinThickness: undefined, Insulin: undefined, BMI: undefined,
        DiabetesPedigreeFunction: 0.5, Age: undefined,
        symptoms: '', file: null
      }
      this.temp.height = undefined
      this.temp.weight = undefined
      this.fileList = []
      this.autoConvertGlucose = false
      this.bmiFeedback = { text: '', class: '' }
      this.glucoseFeedback = { text: '', class: '' }
      this.bpFeedback = { text: '', class: '' }
      ElMessage.info('表单已重置')
    }
  }
}
</script>

<style scoped>
.health-hub-pro {
  padding: 20px;
  background: #f0f2f5;
  min-height: 100vh;
}

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, #409EFF 0%, #66B1FF 100%);
  color: white;
  padding: 28px 32px;
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 6px 20px rgba(64,158,255,0.25);
}
.welcome-banner h1 { margin: 0 0 8px; font-size: 26px; }
.welcome-banner p { margin: 0; opacity: 0.9; font-size: 15px; }

/* 卡片通用 */
.form-card, .advice-card, .chart-card, .history-card, .tool-card, .checkin-card {
  border: none;
  border-radius: 16px;
  margin-bottom: 20px;
  background: #fff;
  box-shadow: 0 4px 14px rgba(0,0,0,0.06);
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 16px;
  color: #1D2129;
}
.card-header i { color: #409EFF; font-size: 18px; }

/* 表单 */
.health-form {
  padding: 8px 0;
}
.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1F2937;
  margin: 24px 0 16px 0;
  padding-left: 12px;
  border-left: 4px solid #409EFF;
}
.feedback-tag {
  display: inline-block;
  margin-top: 6px;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}
.feedback-tag.success { background: #f0f9eb; color: #67C23A; }
.feedback-tag.warning { background: #fdf6ec; color: #E6A23C; }
.feedback-tag.danger { background: #fef0f0; color: #F56C6C; }

.converter-bar {
  margin: 20px 0;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.action-bar {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #EBEEF5;
}

/* 建议卡片 */
.advice-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
.advice-item {
  background: #fafafa;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #ebeef5;
}
.adv-icon {
  width: 44px; height: 44px;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  color: white; font-size: 20px; margin-bottom: 12px;
}
.bg-green { background: #67C23A; }
.bg-blue { background: #409EFF; }
.advice-item h3 { margin: 0 0 8px; color: #303133; }
.advice-item ul { margin: 0; padding-left: 18px; color: #606266; font-size: 14px; line-height: 1.8; }

/* 图表卡片 */
.chart-card {
  position: relative;
}
.chart-tabs {
  margin-bottom: 12px;
}
.chart-box {
  width: 100%;
  height: 220px;
}
.empty-chart {
  text-align: center;
  color: #999;
  padding: 30px 0;
  font-size: 14px;
}

/* 历史记录 */
.history-list {
  max-height: 400px;
  overflow-y: auto;
}
.empty-state { text-align: center; color: #999; padding: 40px 0; }
.history-item {
  padding: 12px;
  margin-bottom: 12px;
  background: #fafafa;
  border-radius: 8px;
  border: 1px solid #eee;
}
.item-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.item-body { display: flex; gap: 12px; font-size: 13px; color: #555; margin-bottom: 6px; }
.item-body b { color: #222; }
.diagnosis-box {
  background: #f0f9eb;
  padding: 8px 10px;
  border-radius: 6px;
  font-size: 13px;
  margin-bottom: 8px;
  color: #2d6a2d;
}
.item-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  border-top: 1px dashed #eee;
  padding-top: 8px;
}

/* 工具箱 */
.tool-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.tool-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border-radius: 10px;
  background: #fff;
  border: 1px solid #ebeef5;
  cursor: pointer;
  transition: all 0.2s;
}
.tool-item:hover {
  background: #f5f7fa;
  border-color: #409EFF;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64,158,255,0.12);
}
.icon-box {
  width: 44px; height: 44px;
  border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  color: white; font-size: 20px;
}
.bg-red { background: #F56C6C; }
.bg-orange { background: #E6A23C; }

/* 打卡 */
.checkin-list {
  margin-bottom: 16px;
}
.checkin-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  cursor: pointer;
}
.check-icon {
  font-size: 20px;
  margin-right: 10px;
  transition: color 0.2s;
}
.checkin-item span { font-size: 14px; color: #303133; }
.checkin-item span.done { text-decoration: line-through; color: #909399; }
.progress-section {
  border-top: 1px solid #ebeef5;
  padding-top: 14px;
}
.progress-text {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
  margin-bottom: 6px;
}

@media (max-width: 768px) {
  .advice-grid { grid-template-columns: 1fr; }
  .tool-grid { flex-direction: row; flex-wrap: wrap; }
  .tool-item { flex: 1; min-width: 120px; }
}
</style>