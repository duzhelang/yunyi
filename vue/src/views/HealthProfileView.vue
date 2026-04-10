<template>
  <div class="health-profile-container">
    <el-row :gutter="24">
      <!-- 左侧:数据录入表单 -->
      <el-col :span="16">
        <div class="form-card">
          <!-- 头部 -->
          <div class="chat-header" style="border-bottom: 1px solid #eaeef5;">
            <div class="doctor-icon" style="background: #e6f0ff; color: #4a90e2;">
              <i class="el-icon-s-data"></i>
            </div>
            <h3>健康指标自查 (人工诊断)</h3>
            <div class="status">
              <span class="online-dot" style="background: #e6a23c; animation: none;"></span>
              <span class="status-text">诊断员在线 (CSV 流转模式)</span>
            </div>
          </div>

          <!-- 表单主体 -->
          <div class="form-body">
            <el-form :model="form" label-width="150px" ref="profileForm" size="default">

              <div class="section-title">👤 基础身体信息</div>

              <el-row :gutter="15">
                <el-col :span="12">
                  <el-form-item label="年龄 (Age)" required>
                    <el-input-number
                        v-model="form.Age"
                        :min="1" :max="120"
                        placeholder="岁"
                        style="width: 100%"
                        @focus="inputFocused = true"
                        @blur="inputFocused = false"
                        :class="{ 'focused-input': inputFocused }"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="怀孕次数">
                    <el-input-number
                        v-model="form.Pregnancies"
                        :min="0" :max="20"
                        placeholder="次 (无则填 0)"
                        style="width: 100%"
                        @focus="inputFocused = true"
                        @blur="inputFocused = false"
                    />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="15">
                <el-col :span="12">
                  <el-form-item label="身高 (cm)" required>
                    <el-input-number
                        v-model="temp.height"
                        :min="50" :max="250"
                        placeholder="厘米"
                        style="width: 100%"
                        @change="calculateBMI"
                        @focus="inputFocused = true"
                        @blur="inputFocused = false"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="体重 (kg)" required>
                    <el-input-number
                        v-model="temp.weight"
                        :min="20" :max="300"
                        placeholder="千克"
                        style="width: 100%"
                        @change="calculateBMI"
                        @focus="inputFocused = true"
                        @blur="inputFocused = false"
                    />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-form-item label="BMI (自动计算)" required>
                <el-input
                    v-model="form.BMI"
                    disabled
                    :class="{ 'is-error': !form.BMI, 'focused-input': inputFocused }"
                    style="width: 100%"
                >
                  <template #append>kg/m</template>
                </el-input>
                <div class="ai-tip" style="margin-top: 5px;">*由身高体重自动计算,模型必填项</div>
              </el-form-item>

              <div class="section-title">🩺 核心医疗指标</div>

              <el-row :gutter="15">
                <el-col :span="12">
                  <el-form-item label="血糖 (Glucose)" required>
                    <el-input-number
                        v-model="form.Glucose"
                        :precision="1" :step="1"
                        placeholder="mg/dL"
                        style="width: 100%"
                        @focus="inputFocused = true"
                        @blur="inputFocused = false"
                    >
                      <template #append>
                        <el-tooltip content="模型单位为 mg/dL" placement="top">
                          <i class="el-icon-question" style="cursor: pointer;"></i>
                        </el-tooltip>
                      </template>
                    </el-input-number>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="血压 (BloodPressure)">
                    <el-input-number
                        v-model="form.BloodPressure"
                        :min="40" :max="200"
                        placeholder="mmHg"
                        style="width: 100%"
                        @focus="inputFocused = true"
                        @blur="inputFocused = false"
                    />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="15">
                <el-col :span="12">
                  <el-form-item label="皮褶厚度">
                    <el-input-number
                        v-model="form.SkinThickness"
                        :min="0" :max="100"
                        placeholder="mm"
                        style="width: 100%"
                        @focus="inputFocused = true"
                        @blur="inputFocused = false"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="胰岛素 (Insulin)">
                    <el-input-number
                        v-model="form.Insulin"
                        :precision="1" :min="0"
                        placeholder="mU/L"
                        style="width: 100%"
                        @focus="inputFocused = true"
                        @blur="inputFocused = false"
                    />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-form-item label="糖尿病谱系函数" required>
                <el-input-number
                    v-model="form.DiabetesPedigreeFunction"
                    :precision="3" :step="0.01" :min="0" :max="3"
                    style="width: 100%"
                    @focus="inputFocused = true"
                    @blur="inputFocused = false"
                />
                <div class="ai-tip" style="margin-top: 5px;">*家族遗传系数,不确定填 0.5</div>
              </el-form-item>

              <!-- 单位转换工具 -->
              <div class="unit-converter">
                <el-checkbox v-model="autoConvertGlucose" @change="handleGlucoseConvert">
                  我已输入 mmol/L,自动转换为 mg/dL (18)
                </el-checkbox>
              </div>

              <div class="section-title">📝 补充信息</div>

              <el-form-item label="自觉症状">
                <el-input
                    v-model="form.symptoms"
                    type="textarea"
                    :rows="3"
                    placeholder="如有多饮、多尿等症状请描述..."
                    @focus="inputFocused = true"
                    @blur="inputFocused = false"
                    :class="{ 'focused-input': inputFocused }"
                />
              </el-form-item>

              <el-form-item label="上传化验单">
                <el-upload
                    ref="uploadRef"
                    action="#"
                    :auto-upload="false"
                    :on-change="handleFileChange"
                    :limit="1"
                    :file-list="fileList"
                >
                  <el-button type="primary" plain round icon="el-icon-upload">选择文件</el-button>
                  <div class="el-upload__tip" slot="tip">仅用于医生参考</div>
                </el-upload>
              </el-form-item>
            </el-form>
          </div>

          <!-- 底部操作区 -->
          <div class="input-area" style="border-top: 1px solid #eaeef5; background: white; padding: 18px 24px;">
            <el-button @click="resetForm" :disabled="saving || submitting" plain round>
              重置
            </el-button>
            <div style="flex: 1;"></div>
            <el-button
                @click="saveOnly"
                :loading="saving"
                :disabled="submitting"
                round
                style="margin-right: 12px;"
            >
              💾 仅保存
            </el-button>
            <el-button
                type="primary"
                @click="submitToDoctor(null)"
                :loading="submitting"
                :disabled="saving"
                round
                class="send-btn"
            >
              <span v-if="!submitting">🚀 保存并发送诊断 (生成 CSV)</span>
              <i v-else class="el-icon-loading"></i>
            </el-button>
          </div>
        </div>
      </el-col>

      <!-- 右侧:历史记录 -->
      <el-col :span="8">
        <div class="history-card">
          <div class="chat-header" style="border-bottom: 1px solid #eaeef5;">
            <h3>🕒 自查历史</h3>
          </div>

          <div class="chat-history" style="height: 600px; background: #f8fafc;">
            <!-- 空状态 -->
            <div v-if="!historyList || historyList.length === 0" class="welcome-message">
              <p>暂无历史记录</p>
              <p class="ai-tip">填写并提交后将会显示在这里</p>
            </div>

            <!-- 列表渲染 -->
            <div v-else v-for="(item, index) in historyList" :key="item.id" class="message-item" style="display: block; margin-bottom: 16px;">
              <div class="bot-message" style="max-width: 100%; margin-right: 0;">
                <div class="history-item-card">
                  <div class="history-header">
                    <el-tag
                        :type="getStatusType(item.status)"
                        size="mini"
                        round
                        effect="dark"
                    >
                      {{ getStatusText(item.status) }}
                    </el-tag>
                    <span class="time" style="color: #94a3b8; text-align: left;">{{ formatTime(item.createTime) }}</span>
                  </div>

                  <div class="history-grid">
                    <!-- ✅ 关键修改:全部改为小驼峰 (glucose, bmi, age) 以匹配后端 JSON -->
                    <span>血糖:<b>{{ item.glucose }}</b></span>
                    <span>BMI: <b>{{ item.bmi }}</b></span>
                    <span>年龄:<b>{{ item.age }}</b></span>
                  </div>

                  <!-- 诊断结果显示区 -->
                  <div v-if="item.status === 'DONE' && item.diagnosisResult" class="diagnosis-result-box">
                    <strong>👨‍⚕️ 诊断结论:</strong>
                    <div style="margin-top: 4px; color: #333; line-height: 1.5;">{{ item.diagnosisResult }}</div>
                  </div>

                  <!-- 等待中提示 -->
                  <div v-else-if="item.status === 'PENDING'" class="pending-tip">
                    <i class="el-icon-time"></i> CSV 文件已生成,诊断员正在分析中...
                  </div>

                  <!-- 其他状态提示 -->
                  <div v-else class="pending-tip" style="background: #f4f4f5; color: #909399; border-color: #e9e9eb;">
                    <i class="el-icon-document"></i> 数据已保存
                  </div>

                  <div class="history-actions">
                    <el-button type="text" size="mini" @click="loadHistoryToForm(item)">
                      载入数据
                    </el-button>
                    <el-button
                        type="primary"
                        size="mini"
                        round
                        @click="submitToDoctor(item.id)"
                        :loading="submittingId === item.id"
                        :disabled="item.status === 'PENDING'"
                    >
                      {{ item.status === 'PENDING' ? '等待诊断' : '再次咨询' }}
                    </el-button>
                  </div>
                </div>
              </div>
            </div>

            <!-- 加载指示器 -->
            <div v-if="loadingHistory" class="loading-indicator" style="justify-content: center; margin-top: 20px;">
              <div class="dot"></div>
              <div class="dot"></div>
              <div class="dot"></div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import request from '@/utils/request'
import { Message } from 'element-ui'

export default {
  name: 'HealthProfileView',
  data() {
    return {
      // 表单数据模型 (保持你的大驼峰习惯,但在赋值时要注意转换)
      form: {
        Pregnancies: 0,
        Glucose: undefined,
        BloodPressure: 0,
        SkinThickness: 0,
        Insulin: 0,
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
      saving: false,
      submitting: false,
      submittingId: null,
      loadingHistory: false,
      inputFocused: false,
      autoConvertGlucose: false,

      historyList: [], // 这里将存储后端返回的小驼峰数据
      fileList: []
    }
  },
  mounted() {
    this.loadHistory()
  },
  methods: {
    calculateBMI() {
      if (this.temp.height && this.temp.weight) {
        const h = this.temp.height / 100
        this.form.BMI = parseFloat((this.temp.weight / (h * h)).toFixed(2))
      } else {
        this.form.BMI = undefined
      }
    },

    handleGlucoseConvert(val) {
      if (!this.form.Glucose) return
      if (val && this.form.Glucose < 30) {
        this.form.Glucose = parseFloat((this.form.Glucose * 18).toFixed(1))
        Message.info(`已转换:${(this.form.Glucose/18).toFixed(1)} mmol/L → ${this.form.Glucose} mg/dL`)
      } else if (!val && this.form.Glucose > 100) {
        this.form.Glucose = parseFloat((this.form.Glucose / 18).toFixed(1))
        Message.info(`已还原单位`)
      }
    },

    handleFileChange(uploadFile) {
      this.form.file = uploadFile.raw
      this.fileList = [uploadFile]
    },

    validateForm() {
      if (!this.form.Age || !this.form.Glucose || !this.form.BMI) {
        Message.warning('请填写年龄、血糖和 BMI (身高体重) 等必填项')
        this.inputFocused = true
        return false
      }
      return true
    },

    buildPayload() {
      const payload = new FormData()
      payload.append('Pregnancies', this.form.Pregnancies || 0)
      payload.append('Glucose', this.form.Glucose)
      payload.append('BloodPressure', this.form.BloodPressure || 0)
      payload.append('SkinThickness', this.form.SkinThickness || 0)
      payload.append('Insulin', this.form.Insulin || 0)
      payload.append('BMI', this.form.BMI)
      payload.append('DiabetesPedigreeFunction', this.form.DiabetesPedigreeFunction || 0)
      payload.append('Age', this.form.Age)
      payload.append('symptoms', this.form.symptoms)
      if (this.form.file) {
        payload.append('file', this.form.file)
      }
      return payload
    },

    async saveOnly() {
      if (!this.validateForm()) return
      this.saving = true
      try {
        const response = await request.post('/api/health-profile/save', this.buildPayload(), {
          headers: { 'Content-Type': 'multipart/form-data' }
        })
        console.log('[Save] 原始响应:', response)

        let savedId = null
        if (typeof response === 'number') savedId = response
        else if (typeof response === 'string' && !isNaN(Number(response))) savedId = Number(response)
        else if (response && typeof response === 'object') {
          if (response.msg !== undefined) {
            const msgVal = Number(response.msg)
            if (!isNaN(msgVal) && msgVal > 0) savedId = msgVal
          }
          if (savedId === null) {
            const possibleFields = ['data', 'id', 'result']
            for (const field of possibleFields) {
              if (response[field] !== undefined) {
                const val = response[field]
                if (typeof val === 'number') { savedId = val; break }
                else if (typeof val === 'string' && !isNaN(Number(val))) { savedId = Number(val); break }
              }
            }
          }
        }

        if (savedId !== null && savedId !== undefined && !isNaN(savedId) && savedId > 0) {
          Message.success('档案保存成功 (ID: ' + savedId + ')')
          this.loadHistory()
          return savedId
        } else {
          Message.error('保存成功但无法获取 ID')
          return null
        }
      } catch (error) {
        console.error(error)
        Message.error('网络错误,保存失败')
        return null
      } finally {
        this.saving = false
      }
    },

    async submitToDoctor(existingId) {
      if (existingId && typeof existingId !== 'number' && typeof existingId !== 'string') existingId = null
      if (!this.validateForm()) return

      let targetId = existingId
      if (!targetId) {
        this.saving = true
        try {
          const savedId = await this.saveOnly()
          if (!savedId) return
          targetId = savedId
        } catch (e) {
          Message.error('保存过程出错')
          return
        } finally {
          this.saving = false
        }
      }

      const finalId = Number(targetId)
      if (isNaN(finalId) || finalId <= 0) {
        Message.error('系统错误:无效的档案 ID')
        return
      }

      try {
        await this.$confirm('确定要生成 CSV 并发送给诊断员吗?\n\nID: ' + finalId, '确认提交', {
          confirmButtonText: '生成并发送',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch {
        return
      }

      this.submitting = !existingId
      this.submittingId = existingId

      try {
        const response = await request.post(`/api/health-profile/send-to-doctor/${finalId}`, {})
        let isSuccess = false
        if (typeof response === 'object' && response !== null) {
          if (response.code === 200 || response.code === '200' || response.success === true) isSuccess = true
          else if (!response.code && !response.error) isSuccess = true
        } else {
          isSuccess = true
        }

        if (isSuccess) {
          Message.success('✅ CSV 文件生成成功!已通知诊断员.')
          this.loadHistory()
        } else {
          Message.warning((response && response.msg) || '提交完成')
          this.loadHistory()
        }
      } catch (error) {
        console.error('[Submit] 接口报错详情:', error)
        let errMsg = '提交过程网络出错'
        if (error.response) {
          const data = error.response.data
          errMsg = typeof data === 'string' ? data : (data?.msg || data?.message || `后端拒绝请求 (${error.response.status})`)
        } else if (error.message) {
          errMsg = error.message
        }
        Message.error('❌ 提交失败:' + errMsg)
      } finally {
        this.submitting = false
        this.submittingId = null
      }
    },

    // ✅ 修复:增加日志并正确赋值
    async loadHistory() {
      this.loadingHistory = true
      try {
        const response = await request.get('/api/health-profile/list')
        console.log('[History] 接口原始响应:', response)

        // 后端返回结构通常是 { code: 200, data: [...] }
        // axios 会自动解析 JSON,所以 response.data 就是后端的整个 JSON 对象
        if (response && response.data && Array.isArray(response.data)) {
          this.historyList = response.data
          console.log('[History] 解析后的列表长度:', this.historyList.length)
          console.log('[History] 第一条数据样例:', this.historyList[0])
        } else {
          console.warn('[History] 数据格式异常或为空', response)
          this.historyList = []
        }
      } catch (e) {
        console.error('[History] 加载失败:', e)
        Message.error('加载历史记录失败')
      } finally {
        this.loadingHistory = false
      }
    },

    // ✅ 修复:将后端的小驼峰 (item.glucose) 映射到前端的大驼峰 (this.form.Glucose)
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

      // 注意:后端列表接口通常不返回身高体重,所以这里无法自动回填 temp.height/weight
      // 需要用户手动重新输入以 recalculat BMI,或者提示用户
      Message.info('已载入历史数据 (注意:身高体重需重新输入以重新计算 BMI)')
      this.fileList = []
      this.form.file = null
      window.scrollTo({ top: 0, behavior: 'smooth' })
    },

    resetForm() {
      this.form.Pregnancies = 0
      this.form.Glucose = undefined
      this.form.BloodPressure = 0
      this.form.SkinThickness = 0
      this.form.Insulin = 0
      this.form.BMI = undefined
      this.form.DiabetesPedigreeFunction = 0.5
      this.form.Age = undefined
      this.form.symptoms = ''
      this.form.file = null
      this.temp.height = undefined
      this.temp.weight = undefined
      this.fileList = []
      this.autoConvertGlucose = false
      Message.info('表单已重置')
    },

    getStatusType(status) {
      if (status === 'DONE') return 'success'
      if (status === 'PENDING') return 'warning'
      return 'info'
    },
    getStatusText(status) {
      if (status === 'DONE') return '✅ 已完成'
      if (status === 'PENDING') return '⏳ 诊断中'
      return '📝 已保存'
    },

    formatTime(timestamp) {
      if (!timestamp) return ''
      const date = new Date(timestamp)
      return `${date.getMonth()+1}/${date.getDate()} ${date.getHours().toString().padStart(2,'0')}:${date.getMinutes().toString().padStart(2,'0')}`
    }
  }
}
</script>

<style scoped>
/* 样式部分保持不变 */
.health-profile-container { max-width: 1200px; width: 95%; margin: 30px auto; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; }
.form-card, .history-card { border-radius: 20px; box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08); overflow: hidden; background: white; margin-bottom: 20px; }
.chat-header { display: flex; align-items: center; padding: 20px 24px; background: white; }
.chat-header h3 { font-size: 18px; font-weight: 600; color: #1e293b; margin: 0 12px 0 12px; }
.doctor-icon { width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 18px; }
.status { margin-left: auto; display: flex; align-items: center; font-size: 13px; color: #64748b; }
.online-dot { width: 8px; height: 8px; border-radius: 50%; background: #10b981; margin-right: 6px; animation: onlinePulse 2s infinite; }
@keyframes onlinePulse { 0% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.4); } 70% { box-shadow: 0 0 0 8px rgba(16, 185, 129, 0); } 100% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0); } }
.form-body { padding: 24px; background-color: #f8fafc; min-height: 500px; }
.section-title { font-size: 15px; font-weight: 600; color: #4a90e2; margin: 20px 0 15px 0; padding-left: 10px; border-left: 3px solid #4a90e2; }
.ai-tip { font-size: 11px; color: #94a3b8; font-style: italic; }
.unit-converter { margin: 10px 0 20px 150px; padding: 10px; background: #ecf5ff; border-radius: 8px; font-size: 13px; color: #4a90e2; }
.focused-input >>> .el-input__inner, .focused-input >>> .el-input-number__decrease, .focused-input >>> .el-input-number__increase { border-color: #4a90e2 !important; box-shadow: 0 0 0 2px rgba(74, 144, 226, 0.15) !important; }
.is-error >>> .el-input__inner { border-color: #f56c6c !important; }
.input-area { display: flex; align-items: center; gap: 12px; }
.send-btn { background: #4a90e2; border-color: #4a90e2; transition: all 0.2s; }
.send-btn:hover:not(:disabled) { background: #357abd; transform: translateY(-1px); }
.send-btn:disabled { background: #cbd5e1; border-color: #cbd5e1; }
.chat-history { padding: 24px; overflow-y: auto; }
.welcome-message { text-align: center; color: #64748b; margin-top: 50px; }
.history-item-card { background: white; border: 1px solid #e2e8f0; border-radius: 12px; padding: 15px; transition: all 0.2s; }
.history-item-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.05); border-color: #4a90e2; }
.history-header { display: flex; justify-content: space-between; margin-bottom: 10px; }
.history-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; font-size: 13px; color: #475569; margin-bottom: 12px; }
.diagnosis-result-box { background: #f0f9eb; border: 1px solid #e1f3d8; padding: 10px; border-radius: 6px; margin-bottom: 10px; font-size: 13px; color: #67c23a; }
.pending-tip { font-size: 12px; color: #e6a23c; margin-bottom: 10px; background: #fdf6ec; padding: 8px; border-radius: 6px; border: 1px dashed #faecd8; }
.history-actions { display: flex; justify-content: flex-end; gap: 8px; border-top: 1px dashed #e2e8f0; padding-top: 10px; }
.loading-indicator { display: flex; gap: 6px; }
.dot { width: 8px; height: 8px; border-radius: 50%; background: #94a3b8; animation: loadingDots 1.4s infinite ease-in-out; }
.dot:nth-child(1) { animation-delay: -0.32s; }
.dot:nth-child(2) { animation-delay: -0.16s; }
@keyframes loadingDots { 0%, 80%, 100% { transform: scale(0); } 40% { transform: scale(1); } }
</style>