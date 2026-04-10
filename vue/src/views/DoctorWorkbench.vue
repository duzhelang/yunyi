<template>
  <div class="doctor-workbench">
    <!-- 头部 -->
    <div class="header">
      <h2>👨‍⚕️ 医生诊断工作台</h2>
      <el-tag type="warning" effect="dark">待处理任务:{{ pendingList.length }}</el-tag>
    </div>

    <!-- 待诊断列表 -->
    <el-table :data="pendingList" style="width: 100%" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="createTime" label="提交时间" width="170"></el-table-column>

      <!-- 关键指标概览 -->
      <el-table-column label="患者关键指标" min-width="250">
        <template slot-scope="scope">
          <div class="patient-info">
            <!-- 兼容大小写字段 -->
            <span><b>年龄:</b> {{ scope.row.Age || scope.row.age }}</span>
            <span><b>血糖:</b> <span style="color:#f56c6c">{{ scope.row.Glucose || scope.row.glucose }}</span></span>
            <span><b>BMI:</b> {{ scope.row.BMI || scope.row.bmi }}</span>
            <span v-if="scope.row.symptoms"><b>症状:</b> {{ scope.row.symptoms }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="附件" width="100">
        <template slot-scope="scope">
          <span v-if="scope.row.fileUrl || scope.row.fileName">{{ scope.row.fileUrl || scope.row.fileName }}</span>
          <span v-else style="color:#999">无</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="280" fixed="right">
        <template slot-scope="scope">
          <!-- 1. 下载 CSV 按钮 (最终稳定版) -->
          <el-button
              size="mini"
              type="info"
              icon="el-icon-download"
              circle
              @click="handleDownload(scope.row.id)"
              title="下载 CSV 去预测">
          </el-button>

          <!-- 2. 填写诊断按钮 -->
          <el-button
              size="mini"
              type="primary"
              @click="openDiagnoseDialog(scope.row)">
            ✍️ 填写结果
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 填写诊断结果弹窗 -->
    <el-dialog title="录入预测诊断结果" :visible.sync="dialogVisible" width="500px">
      <div class="dialog-info">
        <p><strong>患者 ID:</strong> {{ currentRow.id }}</p>
        <p><strong>当前状态:</strong> 待诊断 (PENDING)</p>
        <el-alert
            title="操作提示"
            type="info"
            description="1. 点击列表中的[下载]按钮获取 CSV.<br>2. 放入你的 Python 模型运行.<br>3. 将预测结果复制到这里提交."
            show-icon
            :closable="false"
            style="margin-bottom: 15px;">
        </el-alert>
      </div>

      <el-form :model="form" label-width="100px">
        <el-form-item label="预测结论">
          <el-input
              type="textarea"
              :rows="6"
              placeholder="例如:模型预测结果为阳性 (Positive),患病概率 85%.建议立即前往内分泌科就诊..."
              v-model="form.result">
          </el-input>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="success" @click="submitDiagnosis" :loading="submitting">✅ 提交并通知用户</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'
import { Message } from 'element-ui'

export default {
  name: 'DoctorWorkbench',
  data() {
    return {
      loading: false,
      pendingList: [],
      dialogVisible: false,
      submitting: false,
      currentRow: {},
      form: {
        result: ''
      }
    }
  },
  mounted() {
    console.log('💡 医生工作台组件已挂载')
    this.loadData()
  },
  methods: {
    // 加载所有 PENDING 的数据
    async loadData() {
      this.loading = true
      try {
        console.log('🔄 正在加载待办列表...')
        // 优先尝试专用接口
        let res = await request.get('/api/health-profile/doctor/pending')

        // 兼容处理:如果后端返回结构是 { code: 200, data: [...] }
        let list = []
        if (res && res.data) {
          // 如果 res.data 是数组
          if (Array.isArray(res.data)) {
            list = res.data
          }
          // 如果 res.data 是对象且里面有 data 字段 (常见于封装后的 request)
          else if (res.data.data && Array.isArray(res.data.data)) {
            list = res.data.data
          }
        }

        // 如果专用接口没数据,尝试 fallback 到全量过滤 (防止接口未更新)
        if (list.length === 0) {
          console.warn('⚠️ 专用接口无数据,尝试从全量列表过滤...')
          const listRes = await request.get('/api/health-profile/list')
          let allList = []
          if (listRes && listRes.data) {
            allList = Array.isArray(listRes.data) ? listRes.data : (listRes.data.data || [])
          }
          list = allList.filter(item => item.status === 'PENDING')
        }

        this.pendingList = list
        console.log(`✅ 加载完成,共 ${list.length} 条待办记录`)

        if (list.length === 0) {
          Message.info('当前没有待诊断的患者')
        }

      } catch (e) {
        console.error('❌ 加载列表异常:', e)
        Message.error('加载列表失败,请检查后端服务是否启动')
        this.pendingList = [] // 清空防止旧数据残留
      } finally {
        this.loading = false
      }
    },

    // 最终稳定版:下载 CSV (解决弹窗拦截+404问题)
    async handleDownload(id) {
      console.log('🖱️ [点击下载] 接收到 ID:', id)

      if (!id) {
        Message.error('错误:患者 ID 缺失')
        return
      }

      try {
        // 方案:用axios请求二进制流,避免window.open被拦截
        const response = await request({
          url: `/api/health-profile/download-csv/${id}`,
          method: 'GET',
          responseType: 'blob', // 关键:接收二进制流
          timeout: 30000 // 延长超时时间,适配大文件
        })

        // 处理返回的blob流
        const blob = new Blob([response.data], { type: 'text/csv;charset=UTF-8' })
        const fileName = `patient_data_${id}.csv`

        // 创建下载链接
        const link = document.createElement('a')
        link.href = URL.createObjectURL(blob)
        link.download = fileName
        link.style.display = 'none'
        document.body.appendChild(link)
        link.click()

        // 释放URL对象 & 清理DOM
        URL.revokeObjectURL(link.href)
        document.body.removeChild(link)

        console.log('✅ CSV文件下载触发成功,ID:', id)
        Message.success('📥 CSV文件下载成功,请查看浏览器下载栏')

      } catch (error) {
        console.error('❌ 下载失败详情:', error)
        // 精准提示错误原因
        let errorMsg = '下载失败:'
        if (error.response) {
          // 后端返回错误状态码
          const status = error.response.status
          const data = error.response.data
          if (status === 404) {
            if (typeof data === 'string') {
              errorMsg += data // 后端返回的具体404原因
            } else {
              errorMsg += '记录不存在/未生成CSV文件/文件丢失'
            }
          } else if (status === 500) {
            errorMsg += '服务器内部错误,请联系管理员'
          } else {
            errorMsg += `HTTP错误 ${status}: ${data.message || data}`
          }
        } else if (error.request) {
          // 请求发送但无响应
          errorMsg += '请求超时/后端服务未启动,请检查服务器'
        } else {
          // 请求构建失败
          errorMsg += error.message
        }
        Message.error(errorMsg)
      }
    },

    // 打开填写弹窗
    openDiagnoseDialog(row) {
      console.log('✍️ 打开诊断弹窗,Row:', row)
      this.currentRow = row
      this.form.result = ''
      this.dialogVisible = true
    },

    // 提交结果
    async submitDiagnosis() {
      if (!this.form.result.trim()) {
        Message.warning('请填写诊断结论')
        return
      }

      this.submitting = true
      try {
        console.log('📤 正在提交诊断结果...', this.currentRow.id)

        await request.post(`/api/health-profile/doctor/submit-result`, {
          profileId: this.currentRow.id,
          result: this.form.result
        })

        Message.success('✅ 诊断提交成功!')
        this.dialogVisible = false
        this.loadData() // 刷新列表
      } catch (e) {
        console.error('❌ 提交失败:', e)
        const msg = (e.response && e.response.data && e.response.data.msg) || (e.response && e.response.data && e.response.data.message) || e.message || '未知错误'
        Message.error('提交失败:' + msg)
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.doctor-workbench { padding: 20px; background: #f5f7fa; min-height: 100%; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.patient-info { display: flex; flex-direction: column; gap: 4px; font-size: 13px; color: #606266; }
.patient-info span { margin-right: 10px; }
.dialog-info p { margin: 5px 0; font-size: 14px; }
</style>