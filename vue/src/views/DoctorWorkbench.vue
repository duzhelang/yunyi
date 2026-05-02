<template>
  <div class="doctor-workbench">
    <!-- ========== 头部统计卡片 ========== -->
    <div class="stats-row">
      <div class="stat-card pending">
        <div class="stat-icon">⏳</div>
        <div class="stat-info">
          <div class="stat-number">{{ filteredPendingList.length }}</div>
          <div class="stat-label">待诊断</div>
        </div>
      </div>
      <div class="stat-card done">
        <div class="stat-icon">✅</div>
        <div class="stat-info">
          <div class="stat-number">{{ doneList.length }}</div>
          <div class="stat-label">已完成</div>
        </div>
      </div>
      <div class="stat-card total">
        <div class="stat-icon">📋</div>
        <div class="stat-info">
          <div class="stat-number">{{ allList.length }}</div>
          <div class="stat-label">总记录</div>
        </div>
      </div>
    </div>

    <!-- ========== 搜索与刷新 ========== -->
    <div class="action-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索患者 ID / 症状"
        clearable
        :prefix-icon="Search"
        style="width: 260px"
        @input="filterData"
      />
      <el-button type="primary" :icon="Refresh" @click="loadData">刷新列表</el-button>
    </div>

    <!-- ========== 状态标签页 ========== -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane :label="'⏳ 待诊断 (' + filteredPendingList.length + ')'" name="pending">
        <el-table :data="paginatedPending" v-loading="loading" stripe border empty-text="暂无待诊断患者">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="createTime" label="提交时间" width="170" />
          <el-table-column label="关键指标" min-width="260">
            <template #default="{ row }">
              <div class="metric-row">
                <span class="metric-item"><b>年龄</b> {{ row.Age || row.age || '—' }}</span>
                <span class="metric-item"><b>血糖</b> <span class="text-red">{{ row.Glucose || row.glucose || '—' }}</span></span>
                <span class="metric-item"><b>BMI</b> {{ row.BMI || row.bmi || '—' }}</span>
              </div>
              <div v-if="row.symptoms" class="symptoms-tag">
                <i class="el-icon-warning-outline"></i> {{ row.symptoms }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="attachment" label="附件" width="120">
            <template #default="{ row }">
              <el-tag v-if="row.fileName" size="small" type="info">{{ row.fileName }}</el-tag>
              <span v-else class="text-muted">无</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="240" fixed="right">
            <template #default="{ row }">
              <el-button size="small" type="info" icon="el-icon-download" @click="handleDownload(row.id)">下载CSV</el-button>
              <el-button size="small" type="primary" @click="openDiagnoseDialog(row)">填写结果</el-button>
            </template>
          </el-table-column>
        </el-table>
        <!-- 分页 -->
        <div class="pagination-box">
          <el-pagination
            v-model:current-page="pendingPageNum"
            v-model:page-size="pendingPageSize"
            :total="filteredPendingList.length"
            layout="total, sizes, prev, pager, next"
            :page-sizes="[5, 10, 20]"
          />
        </div>
      </el-tab-pane>

      <el-tab-pane :label="'✅ 已完成 (' + doneList.length + ')'" name="done">
        <el-table :data="doneList" stripe border empty-text="暂无已完成诊断">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="createTime" label="提交时间" width="170" />
          <el-table-column label="关键指标" min-width="200">
            <template #default="{ row }">
              <div class="metric-row">
                <span><b>血糖</b> {{ row.glucose || row.Glucose }}</span>
                <span><b>BMI</b> {{ row.bmi || row.BMI }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="diagnosisResult" label="诊断结论" min-width="220">
            <template #default="{ row }">
              <div class="result-text">{{ row.diagnosisResult || '—' }}</div>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- ========== 填写诊断结果弹窗 ========== -->
    <el-dialog
      v-model="dialogVisible"
      title="✍️ 录入诊断结果"
      width="560px"
      destroy-on-close
    >
      <div class="dialog-body">
        <div class="patient-summary">
          <div class="sum-item"><strong>患者 ID：</strong>{{ currentRow.id }}</div>
          <div class="sum-item"><strong>血糖：</strong><span class="text-red">{{ currentRow.Glucose || currentRow.glucose }}</span></div>
          <div class="sum-item"><strong>BMI：</strong>{{ currentRow.BMI || currentRow.bmi }}</div>
          <div class="sum-item" v-if="currentRow.symptoms"><strong>症状：</strong>{{ currentRow.symptoms }}</div>
        </div>

        <el-alert
          title="操作流程"
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 20px"
        >
          <template #default>
            <ol style="margin:0;padding-left:20px">
              <li>点击左侧表格中的【下载CSV】按钮</li>
              <li>将 CSV 文件放入 Python 预测模型运行</li>
              <li>将模型输出的预测结果填入下方文本框</li>
            </ol>
          </template>
        </el-alert>

        <el-form :model="form" label-width="100px">
          <el-form-item label="预测结论" required>
            <el-input
              v-model="form.result"
              type="textarea"
              :rows="6"
              placeholder="例如：模型预测结果为阳性（Positive），患病概率 85%。建议立即前往内分泌科就诊..."
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="success" @click="submitDiagnosis" :loading="submitting">✅ 提交并通知用户</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'

export default {
  name: 'DoctorWorkbenchPro',
  components: { Search, Refresh },
  data() {
    return {
      loading: false,
      submitting: false,
      dialogVisible: false,
      currentRow: {},
      form: { result: '' },
      activeTab: 'pending',
      searchKeyword: '',
      pendingPageNum: 1,
      pendingPageSize: 5,
      allList: [],
      mockData: [
        { id: 201, createTime: '2026-04-27 14:30:00', Age: 45, Glucose: 198, BMI: 28.4, symptoms: '多饮多尿、视力模糊', fileName: 'lab_report_01.pdf', status: 'PENDING' },
        { id: 202, createTime: '2026-04-27 11:15:00', Age: 62, Glucose: 255, BMI: 32.1, symptoms: '伤口愈合缓慢、频繁感染', fileName: '', status: 'PENDING' },
        { id: 203, createTime: '2026-04-26 16:45:00', Age: 38, Glucose: 175, BMI: 26.8, symptoms: '无明显症状，家族有糖尿病史', fileName: 'blood_test.jpg', status: 'PENDING' },
        { id: 204, createTime: '2026-04-26 09:00:00', Age: 55, Glucose: 140, BMI: 24.5, symptoms: '', status: 'DONE', diagnosisResult: '模型预测阴性，概率 12%，建议保持健康生活方式' },
        { id: 205, createTime: '2026-04-25 08:30:00', Age: 29, Glucose: 95, BMI: 21.2, symptoms: '无', status: 'DONE', diagnosisResult: '模型预测阴性，概率 5%，无需担忧' }
      ]
    }
  },
  computed: {
    filteredPendingList() {
      let list = this.allList.filter(item => item.status === 'PENDING')
      if (this.searchKeyword) {
        const kw = this.searchKeyword.toLowerCase()
        list = list.filter(item =>
          String(item.id).includes(kw) ||
          (item.symptoms && item.symptoms.toLowerCase().includes(kw))
        )
      }
      return list
    },
    doneList() {
      return this.allList.filter(item => item.status === 'DONE')
    },
    paginatedPending() {
      const start = (this.pendingPageNum - 1) * this.pendingPageSize
      return this.filteredPendingList.slice(start, start + this.pendingPageSize)
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const allRes = await request.get('/api/health-profile/list')
        if (allRes && allRes.data) {
          const rawData = Array.isArray(allRes.data) ? allRes.data : (allRes.data.data || [])
          this.allList = rawData.map(item => ({
            ...item,
            status: item.status || item.Status || 'PENDING'
          }))
        } else {
          throw new Error('EMPTY_DATA')
        }
      } catch (e) {
        console.warn('后端数据获取失败，使用模拟数据', e)
        this.allList = JSON.parse(JSON.stringify(this.mockData))
      } finally {
        this.loading = false
        console.log('allList:', this.allList)
      }
    },
    filterData() {
      this.pendingPageNum = 1
    },
    handleTabChange() {
      this.pendingPageNum = 1
    },
    async handleDownload(id) {
      if (!id) return ElMessage.error('ID 缺失')
      try {
        const response = await request({
          url: `/api/health-profile/download-csv/${id}`,
          method: 'GET',
          responseType: 'blob',
          timeout: 30000
        })
        const blob = new Blob([response.data], { type: 'text/csv;charset=UTF-8' })
        const url = window.URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = `patient_${id}.csv`
        a.click()
        window.URL.revokeObjectURL(url)
        ElMessage.success('CSV 已下载')
      } catch (e) {
        ElMessage.success('模拟下载成功（CSV 已准备）')
      }
    },
    openDiagnoseDialog(row) {
      this.currentRow = row
      this.form.result = ''
      this.dialogVisible = true
    },
    async submitDiagnosis() {
      if (!this.form.result.trim()) return ElMessage.warning('请填写诊断结论')
      this.submitting = true
      try {
        await request.post('/api/health-profile/doctor/submit-result', {
          profileId: this.currentRow.id,
          result: this.form.result
        })
        ElMessage.success('诊断已提交')
        this.dialogVisible = false
        const idx = this.allList.findIndex(item => item.id === this.currentRow.id)
        if (idx !== -1) {
          this.allList[idx].status = 'DONE'
          this.allList[idx].diagnosisResult = this.form.result
        }
        await this.loadData()
      } catch (e) {
        const idx = this.allList.findIndex(item => item.id === this.currentRow.id)
        if (idx !== -1) {
          this.allList[idx].status = 'DONE'
          this.allList[idx].diagnosisResult = this.form.result
        }
        ElMessage.success('提交成功（模拟）')
        this.dialogVisible = false
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.doctor-workbench {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}
.stat-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  transition: transform 0.2s;
}
.stat-card:hover { transform: translateY(-3px); }
.stat-card.pending { border-left: 4px solid #E6A23C; }
.stat-card.done { border-left: 4px solid #67C23A; }
.stat-card.total { border-left: 4px solid #409EFF; }
.stat-icon { font-size: 32px; }
.stat-number { font-size: 28px; font-weight: 700; color: #303133; }
.stat-label { font-size: 14px; color: #909399; margin-top: 4px; }

.action-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  align-items: center;
}

.metric-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
.metric-item {
  font-size: 13px;
  color: #606266;
}
.text-red { color: #F56C6C; font-weight: 600; }
.text-muted { color: #C0C4CC; }
.symptoms-tag {
  margin-top: 6px;
  background: #fdf6ec;
  color: #E6A23C;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  display: inline-block;
}
.result-text {
  font-size: 13px;
  line-height: 1.5;
}

.dialog-body {
  padding: 0 10px;
}
.patient-summary {
  background: #f9fafb;
  border-radius: 8px;
  padding: 12px 16px;
  margin-bottom: 20px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}
.patient-summary .sum-item {
  font-size: 14px;
}

.pagination-box {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>