<template>
  <div class="treatment-record-page">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">诊疗档案</h2>
        <span class="page-subtitle">集中管理个人健康轨迹</span>
      </div>
      <el-button type="primary" :icon="Plus" @click="openAddDialog">新增记录</el-button>
    </div>

    <div class="main-content">

      <div class="health-trend-section" v-if="healthHistoryList.length > 0">
        <div class="trend-card trend-card-profile">
          <div class="trend-card-header">
            <span class="trend-card-title">🏥 健康趋势追踪</span>
            <span class="trend-card-sub">基于自查数据</span>
          </div>
          <div class="health-charts-grid">
            <div class="health-chart-item">
              <h4 class="chart-title">血糖曲线</h4>
              <div ref="healthGlucoseRef" class="chart-box"></div>
            </div>
            <div class="health-chart-item">
              <h4 class="chart-title">BMI 趋势</h4>
              <div ref="healthBmiRef" class="chart-box"></div>
            </div>
          </div>
        </div>
      </div>

      <div class="echarts-trend-section" v-if="chartData.length >= 2">
        <div class="trend-card trend-card-echarts">
          <div class="trend-card-header">
            <span class="trend-card-title">📈 就诊健康趋势</span>
            <span class="trend-card-sub">基于就诊记录</span>
          </div>
          <div class="charts-grid">
            <div class="chart-card">
              <h4 class="chart-title">血糖趋势</h4>
              <div ref="glucoseChartRef" class="chart-box"></div>
            </div>
            <div class="chart-card" v-if="hasBPData">
              <h4 class="chart-title">血压趋势</h4>
              <div ref="bpChartRef" class="chart-box"></div>
            </div>
            <div class="chart-card" v-if="hasWeightData">
              <h4 class="chart-title">体重 / BMI 趋势</h4>
              <div ref="weightChartRef" class="chart-box"></div>
            </div>
          </div>
        </div>
      </div>

      <div class="ai-grid-row" v-if="healthPlanRecords.length > 0 || recipeRecords.length > 0">
        <div v-if="healthPlanRecords.length > 0" class="ai-section">
          <div class="section-header">
            <span class="section-title">📋 健康计划生成</span>
          </div>
          <div class="ai-cards">
            <div v-for="record in healthPlanRecords.slice(0, 2)" :key="record.id" class="ai-card plan-card" @click="openDetail(record)">
              <div class="ai-card-header">
                <span class="ai-card-icon">📋</span>
                <span class="ai-card-date">{{ formatDate(record.recordDate) }}</span>
              </div>
              <div class="ai-card-summary">{{ truncateText(record.diagnosis, 50) }}</div>
              <div class="ai-card-preview">{{ truncateText(record.treatmentPlan, 80) }}</div>
            </div>
          </div>
        </div>
        <div v-if="recipeRecords.length > 0" class="ai-section">
          <div class="section-header">
            <span class="section-title">🍎 今日控糖食谱</span>
          </div>
          <div class="ai-cards">
            <div v-for="record in recipeRecords.slice(0, 2)" :key="record.id" class="ai-card recipe-card" @click="openDetail(record)">
              <div class="ai-card-header">
                <span class="ai-card-icon">🍎</span>
                <span class="ai-card-date">{{ formatDate(record.recordDate) }}</span>
              </div>
              <div class="ai-card-summary">{{ truncateText(record.diagnosis, 50) }}</div>
              <div class="ai-card-preview">{{ truncateText(record.treatmentPlan, 80) }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="records-section">
        <div class="section-header">
          <span class="section-title">就诊与自查记录</span>
          <span class="record-total">共 {{ allRecords.length }} 条</span>
          <el-button v-if="!showAll && allRecords.length > 2" link type="primary" @click="loadAll">
            查看更多 <el-icon><ArrowDown /></el-icon>
          </el-button>
          <el-button v-if="showAll" link type="info" @click="collapseList">
            收起 <el-icon><ArrowUp /></el-icon>
          </el-button>
        </div>

        <div v-if="loading || loadingHealthHistory" class="loading-state">
          <el-skeleton :rows="2" animated />
        </div>

        <div v-else-if="allRecords.length === 0" class="empty-state">
          <el-empty description="暂无就诊或自查记录" />
        </div>

        <div v-else class="record-cards">
          <template v-for="record in displayedAllRecords" :key="record._uid">
            <div class="record-card-wrapper">
              <div class="record-card" :class="{ 'health-record': record._source === 'health' }">
                <div class="card-top">
                  <el-tag v-if="record._source === 'visit'" :type="getTypeTag(record.recordType).type" size="small" effect="dark">
                    {{ getTypeTag(record.recordType).label }}
                  </el-tag>
                  <el-tag v-else :type="healthStatusMap[record.status]?.type || 'info'" size="small" effect="dark">
                    {{ healthStatusMap[record.status]?.text || record.status }}
                  </el-tag>
                  <span class="card-date">{{ record._source === 'visit' ? formatDate(record.recordDate) : formatHistoryTime(record.createTime) }}</span>
                </div>
                <div class="card-body">
                  <div class="card-diagnosis" v-if="record._source === 'visit' && record.diagnosis">
                    <el-icon><Document /></el-icon>
                    <span>{{ truncateText(record.diagnosis, 60) }}</span>
                  </div>
                  <div class="card-complaint" v-if="record._source === 'visit' && record.chiefComplaint">
                    <el-icon><ChatLineSquare /></el-icon>
                    <span>{{ truncateText(record.chiefComplaint, 40) }}</span>
                  </div>
                  <div v-if="record._source === 'health' && record.status === 'DONE' && record.diagnosisResult" class="self-check-result">
                    <el-icon><Check /></el-icon>
                    {{ record.diagnosisResult }}
                  </div>
                  <div class="card-metrics">
                    <span v-if="record._source === 'visit' && record.glucoseFasting" class="metric-item">
                      空腹 {{ record.glucoseFasting }} mmol/L
                    </span>
                    <span v-if="record._source === 'visit' && record.glucosePostprandial" class="metric-item">
                      餐后 {{ record.glucosePostprandial }} mmol/L
                    </span>
                    <span v-if="record._source === 'visit' && record.bloodPressureSystolic" class="metric-item">
                      血压 {{ record.bloodPressureSystolic }}/{{ record.bloodPressureDiastolic }}
                    </span>
                    <span v-if="record._source === 'visit' && record.bmi" class="metric-item">
                      BMI {{ record.bmi }}
                    </span>
                    <span v-if="record._source === 'health' && record.glucose" class="metric-item">
                      <el-icon><Sugar /></el-icon> 血糖 {{ record.glucose }} mg/dL
                    </span>
                    <span v-if="record._source === 'health' && record.bmi" class="metric-item">
                      <el-icon><ScaleToOriginal /></el-icon> BMI {{ record.bmi }}
                    </span>
                    <span v-if="record._source === 'health' && record.age" class="metric-item">
                      <el-icon><User /></el-icon> {{ record.age }}岁
                    </span>
                  </div>
                </div>
                <div class="card-actions" @click.stop>
                  <div class="action-left">
                    <el-button link size="small" type="info" @click.stop="toggleDetail(record._uid)">
                      <el-icon><component :is="expandedIds.has(record._uid) ? ArrowUp : ArrowDown" /></el-icon>
                      {{ expandedIds.has(record._uid) ? '收起' : '详情' }}
                    </el-button>
                  </div>
                  <div class="action-right">
                    <el-button v-if="record._source === 'visit'" link type="primary" size="small" @click.stop="openEditDialog(record)">
                      <el-icon><Edit /></el-icon> 编辑
                    </el-button>
                    <el-button link type="danger" size="small" @click.stop="handleDelete(record)">
                      <el-icon><Delete /></el-icon> 删除
                    </el-button>
                  </div>
                </div>
              </div>

              <transition name="detail-slide">
                <div v-if="expandedIds.has(record._uid)" class="inline-detail-panel" @click.stop>
                  <template v-if="record._source === 'health'">
                    <div class="detail-section">
                      <div class="detail-section-title-inline">🩺 核心医疗指标</div>
                      <div class="detail-grid">
                        <div class="detail-cell">
                          <span class="detail-label">空腹血糖</span>
                          <span class="detail-value">{{ record.glucose ?? '-' }} mg/dL</span>
                        </div>
                        <div class="detail-cell">
                          <span class="detail-label">血压</span>
                          <span class="detail-value">{{ record.bloodPressure ?? '-' }} mmHg</span>
                        </div>
                        <div class="detail-cell">
                          <span class="detail-label">胰岛素</span>
                          <span class="detail-value">{{ record.insulin ?? '-' }} mU/L</span>
                        </div>
                        <div class="detail-cell">
                          <span class="detail-label">皮褶厚度</span>
                          <span class="detail-value">{{ record.skinThickness ?? '-' }} mm</span>
                        </div>
                        <div class="detail-cell">
                          <span class="detail-label">糖尿病谱系函数</span>
                          <span class="detail-value">{{ record.diabetesPedigreeFunction ?? '-' }}</span>
                        </div>
                        <div class="detail-cell">
                          <span class="detail-label">怀孕次数</span>
                          <span class="detail-value">{{ record.pregnancies ?? '-' }}</span>
                        </div>
                      </div>
                    </div>
                    <div class="detail-section">
                      <div class="detail-section-title-inline">📋 基本信息</div>
                      <div class="detail-grid">
                        <div class="detail-cell">
                          <span class="detail-label">性别</span>
                          <span class="detail-value">{{ record.gender || '-' }}</span>
                        </div>
                        <div class="detail-cell">
                          <span class="detail-label">年龄</span>
                          <span class="detail-value">{{ record.age ?? '-' }} 岁</span>
                        </div>
                        <div class="detail-cell">
                          <span class="detail-label">身高</span>
                          <span class="detail-value">{{ record.height ?? '-' }} cm</span>
                        </div>
                        <div class="detail-cell">
                          <span class="detail-label">体重</span>
                          <span class="detail-value">{{ record.weight ?? '-' }} kg</span>
                        </div>
                        <div class="detail-cell">
                          <span class="detail-label">BMI</span>
                          <span class="detail-value">{{ record.bmi ?? '-' }}</span>
                        </div>
                      </div>
                    </div>
                    <div class="detail-section">
                      <div class="detail-section-title-inline">🏃 生活方式</div>
                      <div class="detail-grid">
                        <div class="detail-cell">
                          <span class="detail-label">运动频率</span>
                          <span class="detail-value">{{ formatExercise(record.exerciseFrequency) }}</span>
                        </div>
                        <div class="detail-cell">
                          <span class="detail-label">饮食习惯</span>
                          <span class="detail-value">{{ formatDiet(record.dietHabit) }}</span>
                        </div>
                        <div class="detail-cell">
                          <span class="detail-label">吸烟</span>
                          <span class="detail-value">{{ record.smoking || '-' }}</span>
                        </div>
                        <div class="detail-cell">
                          <span class="detail-label">饮酒</span>
                          <span class="detail-value">{{ record.drinking || '-' }}</span>
                        </div>
                      </div>
                    </div>
                    <div v-if="record.symptoms" class="detail-section">
                      <div class="detail-section-title-inline">📝 自觉症状</div>
                      <div class="detail-symptoms-text">{{ record.symptoms }}</div>
                    </div>
                    <div v-if="record.riskLevel" class="detail-section">
                      <div class="detail-section-title-inline">📊 预测结果</div>
                      <div class="detail-grid">
                        <div class="detail-cell">
                          <span class="detail-label">风险等级</span>
                          <span class="detail-value" :class="'risk-' + record.riskLevel">{{ formatRisk(record.riskLevel) }}</span>
                        </div>
                        <div class="detail-cell">
                          <span class="detail-label">患病概率</span>
                          <span class="detail-value">{{ record.riskProbability != null ? record.riskProbability.toFixed(1) + '%' : '-' }}</span>
                        </div>
                      </div>
                    </div>
                    <div v-if="record.aiAdvice" class="detail-section">
                      <div class="detail-section-title-inline">🤖 AI 健康建议</div>
                      <div class="detail-advice-text">{{ record.aiAdvice }}</div>
                    </div>
                  </template>

                  <template v-else-if="record.recordType !== 'ai_plan'">
                    <el-descriptions :column="1" border size="small">
                      <el-descriptions-item v-if="record.hospital" label="就诊医院">{{ record.hospital }}</el-descriptions-item>
                      <el-descriptions-item v-if="record.doctorName" label="医生姓名">{{ record.doctorName }}</el-descriptions-item>
                      <el-descriptions-item v-if="record.chiefComplaint" label="主诉/描述">{{ record.chiefComplaint }}</el-descriptions-item>
                      <el-descriptions-item v-if="record.diagnosis" label="诊断/结果">{{ record.diagnosis }}</el-descriptions-item>
                      <el-descriptions-item v-if="record.treatmentPlan" label="方案/计划">
                        <div class="plan-content">{{ record.treatmentPlan }}</div>
                      </el-descriptions-item>
                      <el-descriptions-item v-if="record.glucoseFasting" label="空腹血糖">{{ record.glucoseFasting }} mmol/L</el-descriptions-item>
                      <el-descriptions-item v-if="record.glucosePostprandial" label="餐后血糖">{{ record.glucosePostprandial }} mmol/L</el-descriptions-item>
                      <el-descriptions-item v-if="record.hba1c" label="糖化血红蛋白">{{ record.hba1c }}%</el-descriptions-item>
                      <el-descriptions-item v-if="record.bloodPressureSystolic" label="血压">{{ record.bloodPressureSystolic }}/{{ record.bloodPressureDiastolic }} mmHg</el-descriptions-item>
                      <el-descriptions-item v-if="record.weight" label="体重">{{ record.weight }} kg</el-descriptions-item>
                      <el-descriptions-item v-if="record.bmi" label="BMI">{{ record.bmi }}</el-descriptions-item>
                    </el-descriptions>
                  </template>

                  <template v-else>
                    <div class="ai-plan-inline-detail">
                      <div class="plan-preview">{{ truncateText(record.treatmentPlan, 300) }}</div>
                      <el-button type="primary" size="small" @click="openDetail(record)">
                        查看完整计划
                      </el-button>
                    </div>
                  </template>
                </div>
              </transition>
            </div>
          </template>
        </div>
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑记录' : '新增记录'"
      width="650px"
      destroy-on-close
      class="record-dialog"
      align-center
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
        <el-form-item label="记录类型" prop="recordType">
          <el-select v-model="formData.recordType" placeholder="请选择记录类型" :disabled="isEdit" style="width: 100%">
            <el-option label="就诊记录" value="visit" />
            <el-option label="自查记录" value="self_check" />
            <el-option label="AI 计划" value="ai_plan" />
          </el-select>
        </el-form-item>

        <el-form-item label="记录日期" prop="recordDate">
          <el-date-picker v-model="formData.recordDate" type="datetime" placeholder="选择日期" style="width: 100%"
                          value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>

        <template v-if="formData.recordType === 'visit'">
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="就诊医院">
                <el-input v-model="formData.hospital" placeholder="请输入医院名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="医生姓名">
                <el-input v-model="formData.doctorName" placeholder="请输入医生姓名" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="主诉">
            <el-input v-model="formData.chiefComplaint" type="textarea" :rows="2" placeholder="请描述主要症状" />
          </el-form-item>
          <el-form-item label="诊断结论">
            <el-input v-model="formData.diagnosis" type="textarea" :rows="2" placeholder="请输入诊断结论" />
          </el-form-item>
          <el-form-item label="治疗方案">
            <el-input v-model="formData.treatmentPlan" type="textarea" :rows="2" placeholder="请输入治疗方案" />
          </el-form-item>
        </template>

        <template v-if="formData.recordType === 'self_check'">
          <el-form-item label="问题描述">
            <el-input v-model="formData.chiefComplaint" type="textarea" :rows="2" placeholder="请描述自查情况" />
          </el-form-item>
          <el-form-item label="自查结果">
            <el-input v-model="formData.diagnosis" type="textarea" :rows="2" placeholder="自查结果摘要" />
          </el-form-item>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="空腹血糖">
                <el-input-number v-model="formData.glucoseFasting" :min="0" :max="30" :precision="1" :step="0.1" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="餐后血糖">
                <el-input-number v-model="formData.glucosePostprandial" :min="0" :max="30" :precision="1" :step="0.1" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="收缩压">
                <el-input-number v-model="formData.bloodPressureSystolic" :min="50" :max="250" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="舒张压">
                <el-input-number v-model="formData.bloodPressureDiastolic" :min="30" :max="150" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="体重(kg)">
                <el-input-number v-model="formData.weight" :min="20" :max="200" :precision="1" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="BMI">
                <el-input-number v-model="formData.bmi" :min="10" :max="50" :precision="1" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="糖化血红蛋白">
                <el-input-number v-model="formData.hba1c" :min="3" :max="15" :precision="1" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <template v-if="formData.recordType === 'ai_plan'">
          <el-form-item label="计划内容">
            <el-input v-model="formData.treatmentPlan" type="textarea" :rows="6" placeholder="AI 生成的饮食/运动计划内容" />
          </el-form-item>
          <el-form-item label="计划摘要">
            <el-input v-model="formData.diagnosis" type="textarea" :rows="2" placeholder="计划核心要点" />
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailVisible" :size="drawerSize" destroy-on-close
      :class="{'plan-drawer': isDetailPlan, 'recipe-drawer': isDetailRecipe}"
    >
      <template #header>
        <div class="drawer-header" :class="isDetailPlan ? 'drawer-header-plan' : isDetailRecipe ? 'drawer-header-recipe' : ''">
          <div class="drawer-header-top">
            <template v-if="detailRecord?._source === 'health'">
              <el-tag :type="healthStatusMap[detailRecord.status]?.type || 'info'" effect="dark" size="small">
                {{ healthStatusMap[detailRecord.status]?.text || '自查记录' }}
              </el-tag>
              <span class="drawer-date">{{ formatHistoryTime(detailRecord?.createTime) }}</span>
            </template>
            <template v-else>
              <el-tag :type="getTypeTag(detailRecord?.recordType)?.type" effect="dark" size="small">
                {{ getTypeTag(detailRecord?.recordType)?.label }}
              </el-tag>
              <span class="drawer-date">{{ formatDate(detailRecord?.recordDate) }}</span>
            </template>
          </div>
          <div class="drawer-title-text" v-if="detailRecord?._source !== 'health' && detailRecord?.diagnosis">{{ detailRecord.diagnosis }}</div>
          <div class="drawer-title-text" v-if="detailRecord?._source === 'health' && detailRecord?.diagnosisResult">{{ detailRecord.diagnosisResult }}</div>
          <div class="drawer-subtitle" v-if="isDetailPlan">📋 详细执行计划</div>
          <div class="drawer-subtitle" v-if="isDetailRecipe">🍎 营养食谱详情</div>
        </div>
      </template>
      <template v-if="detailRecord">
        <!-- 健康档案自查记录详情 -->
        <template v-if="detailRecord._source === 'health'">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="年龄">{{ detailRecord.age ?? '-' }} 岁</el-descriptions-item>
            <el-descriptions-item label="身高">{{ detailRecord.height ?? '-' }} cm</el-descriptions-item>
            <el-descriptions-item label="体重">{{ detailRecord.weight ?? '-' }} kg</el-descriptions-item>
            <el-descriptions-item label="BMI">{{ detailRecord.bmi ?? '-' }}</el-descriptions-item>
            <el-descriptions-item label="血糖">{{ detailRecord.glucose ?? '-' }} mg/dL</el-descriptions-item>
            <el-descriptions-item label="血压">{{ detailRecord.bloodPressure ?? '-' }} mmHg</el-descriptions-item>
            <el-descriptions-item label="怀孕次数">{{ detailRecord.pregnancies ?? '-' }}</el-descriptions-item>
            <el-descriptions-item label="皮肤厚度">{{ detailRecord.skinThickness ?? '-' }} mm</el-descriptions-item>
            <el-descriptions-item label="胰岛素">{{ detailRecord.insulin ?? '-' }} μU/mL</el-descriptions-item>
            <el-descriptions-item label="糖尿病家族史">{{ detailRecord.diabetesPedigreeFunction ?? '-' }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="healthStatusMap[detailRecord.status]?.type || 'info'" size="small" effect="dark">
                {{ healthStatusMap[detailRecord.status]?.text || detailRecord.status }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatHistoryTime(detailRecord.createTime) }}</el-descriptions-item>
          </el-descriptions>
          <div v-if="detailRecord.symptoms" class="detail-section">
            <h4 class="detail-section-title">症状描述</h4>
            <div class="detail-text-block">{{ detailRecord.symptoms }}</div>
          </div>
          <div v-if="detailRecord.diagnosisResult" class="detail-section">
            <h4 class="detail-section-title">诊断结果</h4>
            <div class="detail-text-block detail-text-success">{{ detailRecord.diagnosisResult }}</div>
          </div>
          <div v-if="detailRecord.aiAdvice" class="detail-section">
            <h4 class="detail-section-title">AI 健康建议</h4>
            <div class="detail-text-block">{{ detailRecord.aiAdvice }}</div>
          </div>
        </template>

        <!-- 就诊 / 自查 普通详情 -->
        <template v-else-if="detailRecord.recordType !== 'ai_plan'">
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item v-if="detailRecord.hospital" label="就诊医院">{{ detailRecord.hospital }}</el-descriptions-item>
            <el-descriptions-item v-if="detailRecord.doctorName" label="医生姓名">{{ detailRecord.doctorName }}</el-descriptions-item>
            <el-descriptions-item v-if="detailRecord.chiefComplaint" label="主诉/描述">{{ detailRecord.chiefComplaint }}</el-descriptions-item>
            <el-descriptions-item v-if="detailRecord.diagnosis" label="诊断/结果">{{ detailRecord.diagnosis }}</el-descriptions-item>
            <el-descriptions-item v-if="detailRecord.treatmentPlan" label="方案/计划">
              <div class="plan-content">{{ detailRecord.treatmentPlan }}</div>
            </el-descriptions-item>
            <el-descriptions-item v-if="detailRecord.glucoseFasting" label="空腹血糖">{{ detailRecord.glucoseFasting }} mmol/L</el-descriptions-item>
            <el-descriptions-item v-if="detailRecord.glucosePostprandial" label="餐后血糖">{{ detailRecord.glucosePostprandial }} mmol/L</el-descriptions-item>
            <el-descriptions-item v-if="detailRecord.hba1c" label="糖化血红蛋白">{{ detailRecord.hba1c }}%</el-descriptions-item>
            <el-descriptions-item v-if="detailRecord.bloodPressureSystolic" label="血压">{{ detailRecord.bloodPressureSystolic }}/{{ detailRecord.bloodPressureDiastolic }} mmHg</el-descriptions-item>
            <el-descriptions-item v-if="detailRecord.weight" label="体重">{{ detailRecord.weight }} kg</el-descriptions-item>
            <el-descriptions-item v-if="detailRecord.bmi" label="BMI">{{ detailRecord.bmi }}</el-descriptions-item>
          </el-descriptions>
        </template>

        <!-- 健康计划逐步执行视图 -->
        <template v-else-if="isDetailPlan">
          <div class="plan-overview">
            <div class="plan-stats">
              <div class="stat-item">
                <div class="stat-number">{{ planDays.length }}</div>
                <div class="stat-label">总天数</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">{{ planCompletedCount }}</div>
                <div class="stat-label">已完成</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">{{ planProgress }}%</div>
                <div class="stat-label">完成率</div>
              </div>
            </div>
            <el-progress :percentage="planProgress" :stroke-width="12" :color="planProgressColors" />
            <div class="plan-actions plan-control-bar">
              <span class="plan-progress-text">完成率 {{ planProgress }}%</span>
              <el-button size="small" :icon="weeklyCollapsed ? ArrowDown : ArrowUp" @click="toggleWeekly" class="action-btn">
                {{ weeklyCollapsed ? '全部展开' : '全部收起' }}
              </el-button>
            </div>
          </div>
          
          <div class="plan-days-container plan-days-list">
            <el-checkbox-group v-model="completedPlanDays">
              <div v-for="(day, idx) in planDays" :key="idx" class="plan-day-card" :class="{ 'day-done': completedPlanDays.includes(idx) }">
                <div class="plan-day-header" @click="togglePlanDay(idx)">
                  <div class="day-header-left">
                    <el-checkbox :value="idx" size="large" @click.stop class="day-checkbox" />
                    <span class="day-label">{{ day.label }}</span>
                  </div>
                  <div class="day-header-right">
                    <span class="day-status" :class="completedPlanDays.includes(idx) ? 'status-done' : 'status-pending'">
                      {{ completedPlanDays.includes(idx) ? '✓ 已完成' : '进行中' }}
                    </span>
                    <span class="day-toggle">{{ collapsedPlanDays.includes(idx) ? '▾' : '▴' }}</span>
                  </div>
                </div>
                <div v-show="!collapsedPlanDays.includes(idx)" class="plan-day-body">
                  <div class="plan-item-row" v-if="day.diet">
                    <div class="item-icon-container diet-icon">
                      <span class="item-icon">🍽️</span>
                    </div>
                    <div class="item-content">
                      <span class="item-label">饮食计划</span>
                      <span class="item-value">{{ day.diet }}</span>
                    </div>
                  </div>
                  <div class="plan-item-row" v-if="day.exercise">
                    <div class="item-icon-container exercise-icon">
                      <span class="item-icon">🏃</span>
                    </div>
                    <div class="item-content">
                      <span class="item-label">运动安排</span>
                      <span class="item-value">{{ day.exercise }}</span>
                    </div>
                  </div>
                  <div class="plan-item-row" v-if="day.notes">
                    <div class="item-icon-container notes-icon">
                      <span class="item-icon">⚠️</span>
                    </div>
                    <div class="item-content">
                      <span class="item-label">注意事项</span>
                      <span class="item-value">{{ day.notes }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </el-checkbox-group>
          </div>
        </template>

        <!-- 食谱主题视图 -->
        <template v-else-if="isDetailRecipe">
          <div class="recipe-detail-body">
            <div class="recipe-overview">
              <div class="recipe-summary">
                <div class="summary-item">
                  <div class="summary-icon">🔥</div>
                  <div class="summary-content">
                    <div class="summary-value">约1430kcal</div>
                    <div class="summary-label">每日总热量</div>
                  </div>
                </div>
                <div class="summary-item">
                  <div class="summary-icon">🥗</div>
                  <div class="summary-content">
                    <div class="summary-value">GI≈48</div>
                    <div class="summary-label">升糖指数</div>
                  </div>
                </div>
              </div>
              <div class="recipe-nutrition-tags">
                <span class="nutrition-tag carbs">碳水 150g</span>
                <span class="nutrition-tag protein">蛋白 80g</span>
                <span class="nutrition-tag fat">脂肪 35g</span>
              </div>
            </div>
            
            <div class="recipe-meals-container recipe-meals">
            <div v-for="(meal, idx) in parsedRecipeMeals" :key="idx" class="recipe-meal-card recipe-meal-section">
              <div class="meal-card-header recipe-meal-header">
                <div class="meal-header-left">
                  <span class="meal-icon">{{ meal.icon }}</span>
                  <span class="meal-title">{{ meal.label }}</span>
                </div>
                <div class="meal-header-right">
                  <span class="meal-cal recipe-cal-tag">{{ meal.totalCal }}</span>
                </div>
              </div>
              
              <div class="meal-foods recipe-food-list">
                <div v-for="(food, fi) in meal.foods" :key="fi" class="food-item recipe-food-row">
                  <div class="food-info">
                    <span class="food-name rf-name">{{ food.name }}</span>
                    <span class="food-portion rf-portion">{{ food.portion }}</span>
                  </div>
                  <span class="food-cal rf-cal">{{ food.cal }}</span>
                </div>
              </div>
              
              <div class="meal-footer recipe-meal-footer">
                <div class="gi-info recipe-gi-tag">
                  <span class="gi-label">GI值</span>
                  <span class="gi-value" :class="giBadgeClass(meal.giText)">{{ meal.gi }}</span>
                  <span class="gi-badge" :class="giBadgeClass(meal.giText)">{{ meal.giText }}</span>
                </div>
              </div>
            </div>
            
            <div v-if="parsedRecipeMeals.length === 0" class="recipe-text-content">
              <div class="text-content-header">
                <span class="text-icon">📋</span>
                <span class="text-title">食谱详情</span>
              </div>
              <div class="text-content-body plan-content-lines">
                <template v-for="(line, li) in parsedPlanLines" :key="li">
                  <div v-if="line.type === 'text'" class="text-line plan-content-line">
                    {{ line.content }}
                  </div>
                  <div v-else class="text-food-group plan-content-food-group">
                    <div v-for="(food, fi) in line.items" :key="fi" class="text-food-item plan-food-item">
                      <span class="tf-name pf-name">{{ food.name }}</span>
                      <span class="tf-portion pf-portion">{{ food.portion }}</span>
                      <span class="tf-cal pf-cal">{{ food.cal }}</span>
                    </div>
                  </div>
                </template>
              </div>
            </div>
          </div>
          </div>
        </template>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, ArrowDown, ArrowUp, Edit, Delete, Document, ChatLineSquare, User, Sugar, ScaleToOriginal, Check, Loading as IconLoading, Close } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import request from '@/utils/request'
import { CacheHelper } from '@/utils/cacheHelper'
import { useHealthHistory } from '@/composables/useHealthHistory'

const CACHE_KEY_RECORDS = 'treatment_records_cache'
const CACHE_KEY_CHART = 'treatment_chart_cache'
const CACHE_TTL = 24 * 60 * 60 * 1000

const DEMO_RECORDS = [
  { id: -1, recordType: 'visit', recordDate: '2026-05-02 14:00:00', hospital: '东方医院内分泌科', doctorName: '张伟主任', chiefComplaint: '近期血糖波动较大，偶有餐后偏高', diagnosis: '2型糖尿病，需调整餐后血糖控制', treatmentPlan: '调整阿卡波糖剂量，增加餐后散步习惯，记录每日血糖日记', glucoseFasting: 6.4, glucosePostprandial: 10.1, hba1c: 6.2, bloodPressureSystolic: 130, bloodPressureDiastolic: 83, weight: 73.8, bmi: 24.6, isDeleted: 0 },
  { id: -2, recordType: 'self_check', recordDate: '2026-05-01 08:00:00', hospital: null, doctorName: null, chiefComplaint: '五一假期饮食自查', diagnosis: '假期饮食偏甜，餐后血糖略升', treatmentPlan: '减少甜食摄入，增加餐后散步', glucoseFasting: 6.3, glucosePostprandial: 10.2, hba1c: null, bloodPressureSystolic: 130, bloodPressureDiastolic: 84, weight: 75.0, bmi: 25.1, isDeleted: 0 },
  { id: -3, recordType: 'visit', recordDate: '2026-04-28 09:00:00', hospital: '东方医院眼科', doctorName: '王明主治', chiefComplaint: '视力偶有模糊，糖尿病定期眼底检查', diagnosis: '糖尿病视网膜病变早期，眼底微血管瘤', treatmentPlan: '眼底激光光凝治疗，加强血糖控制，3个月后复查眼底', glucoseFasting: 6.2, glucosePostprandial: 9.0, hba1c: 6.1, bloodPressureSystolic: 128, bloodPressureDiastolic: 82, weight: 74.0, bmi: 24.7, isDeleted: 0 },
  { id: -4, recordType: 'ai_plan', recordDate: '2026-04-20 09:00:00', hospital: null, doctorName: null, chiefComplaint: null, diagnosis: '基于当前健康数据生成的综合管理计划', treatmentPlan: '第1天: 饮食-燕麦+蓝莓+核桃早餐/糙米饭+清蒸鲈鱼+西兰花午餐/杂粮粥+凉拌木耳晚餐；运动-快走40分钟\n第2天: 饮食-全麦面包+水煮蛋+无糖豆浆早餐/红薯+鸡胸肉+生菜午餐/小米粥+清炒菠菜晚餐；运动-瑜伽60分钟\n第3天: 饮食-全麦吐司+牛油果+鸡蛋早餐/藜麦沙拉+鸡胸肉午餐/番茄蛋花汤+杂粮馒头晚餐；运动-力量训练+有氧30分钟\n第4天: 饮食-紫薯+鸡蛋+脱脂奶早餐/荞麦面+虾仁+青菜午餐/豆腐汤+蒸南瓜晚餐；运动-快走50分钟\n第5天: 饮食-希腊酸奶+坚果+奇亚籽早餐/荞麦面+虾仁+蔬菜午餐/清炒时蔬+豆腐晚餐；运动-游泳40分钟\n第6天: 饮食-黑米粥+水煮蛋+凉拌黄瓜早餐/糙米+牛肉+西兰花午餐/冬瓜排骨汤+杂粮饭晚餐；运动-骑行30分钟\n第7天: 饮食-正常均衡饮食三餐；运动-户外骑行60分钟', glucoseFasting: null, glucosePostprandial: null, hba1c: null, bloodPressureSystolic: null, bloodPressureDiastolic: null, weight: null, bmi: null, isDeleted: 0 },
  { id: -5, recordType: 'visit', recordDate: '2026-04-10 10:00:00', hospital: '东方医院内分泌科', doctorName: '张伟主任', chiefComplaint: '春季体检复查', diagnosis: '2型糖尿病，血糖控制良好', treatmentPlan: '继续维持当前方案，注意季节变化对血糖的影响，适当调整运动量', glucoseFasting: 5.8, glucosePostprandial: 8.0, hba1c: 5.8, bloodPressureSystolic: 126, bloodPressureDiastolic: 80, weight: 74.5, bmi: 24.9, isDeleted: 0 },
  { id: -6, recordType: 'ai_plan', recordDate: '2026-05-05 08:00:00', hospital: null, doctorName: null, chiefComplaint: null, diagnosis: '控糖食谱 - 一日三餐低GI均衡饮食方案', treatmentPlan: '【早餐】🌅 全麦面包 2片(约160kcal) · 水煮蛋 1个(约70kcal) · 无糖豆浆 200ml(约60kcal) · 凉拌黄瓜 100g(约30kcal)\n【午餐】☀️ 杂粮饭 100g(约130kcal) · 清蒸鲈鱼 120g(约120kcal) · 蒜蓉西兰花 150g(约55kcal) · 番茄蛋花汤 1碗(约50kcal)\n【晚餐】🌆 荞麦面 80g(约110kcal) · 鸡胸肉炒青椒 120g(约130kcal) · 清炒生菜 150g(约30kcal) · 凉拌海带丝 80g(约20kcal)\n【营养总计】GI≈48 · 总热量≈1430kcal · 碳水150g · 蛋白80g · 脂肪35g', glucoseFasting: null, glucosePostprandial: null, hba1c: null, bloodPressureSystolic: null, bloodPressureDiastolic: null, weight: null, bmi: null, isDeleted: 0 }
]

const DEMO_CHART_DATA = [
  { recordDate: '2025-11-10 09:00:00', glucoseFasting: 8.2, glucosePostprandial: 12.5, bloodPressureSystolic: 135, bloodPressureDiastolic: 85, weight: 78.0, bmi: 26.1 },
  { recordDate: '2025-12-08 10:00:00', glucoseFasting: 7.5, glucosePostprandial: 11.2, bloodPressureSystolic: 130, bloodPressureDiastolic: 82, weight: 77.0, bmi: 25.8 },
  { recordDate: '2025-12-20 08:00:00', glucoseFasting: 7.8, glucosePostprandial: null, bloodPressureSystolic: 138, bloodPressureDiastolic: 88, weight: 77.5, bmi: 25.9 },
  { recordDate: '2026-01-15 09:30:00', glucoseFasting: 6.9, glucosePostprandial: 9.8, bloodPressureSystolic: 128, bloodPressureDiastolic: 80, weight: 76.0, bmi: 25.4 },
  { recordDate: '2026-01-25 07:30:00', glucoseFasting: 7.2, glucosePostprandial: null, bloodPressureSystolic: 132, bloodPressureDiastolic: 84, weight: 76.5, bmi: 25.6 },
  { recordDate: '2026-02-15 08:00:00', glucoseFasting: 6.8, glucosePostprandial: null, bloodPressureSystolic: 135, bloodPressureDiastolic: 86, weight: 76.0, bmi: 25.4 },
  { recordDate: '2026-02-20 14:00:00', glucoseFasting: 6.5, glucosePostprandial: 9.2, bloodPressureSystolic: 145, bloodPressureDiastolic: 92, weight: 75.5, bmi: 25.2 },
  { recordDate: '2026-03-10 07:00:00', glucoseFasting: 6.3, glucosePostprandial: 9.5, bloodPressureSystolic: 128, bloodPressureDiastolic: 82, weight: 75.2, bmi: 25.1 },
  { recordDate: '2026-03-18 09:00:00', glucoseFasting: 6.1, glucosePostprandial: 8.5, bloodPressureSystolic: 132, bloodPressureDiastolic: 84, weight: 75.0, bmi: 25.1 },
  { recordDate: '2026-04-05 07:30:00', glucoseFasting: 5.9, glucosePostprandial: 8.8, bloodPressureSystolic: 124, bloodPressureDiastolic: 78, weight: 74.8, bmi: 25.0 },
  { recordDate: '2026-04-10 10:00:00', glucoseFasting: 5.8, glucosePostprandial: 8.0, bloodPressureSystolic: 126, bloodPressureDiastolic: 80, weight: 74.5, bmi: 24.9 },
  { recordDate: '2026-04-28 09:00:00', glucoseFasting: 6.2, glucosePostprandial: 9.0, bloodPressureSystolic: 128, bloodPressureDiastolic: 82, weight: 74.0, bmi: 24.7 },
  { recordDate: '2026-05-01 08:00:00', glucoseFasting: 6.3, glucosePostprandial: 10.2, bloodPressureSystolic: 130, bloodPressureDiastolic: 84, weight: 75.0, bmi: 25.1 },
  { recordDate: '2026-05-02 14:00:00', glucoseFasting: 6.4, glucosePostprandial: 10.1, bloodPressureSystolic: 130, bloodPressureDiastolic: 83, weight: 73.8, bmi: 24.6 }
]

const loading = ref(true)
const submitting = ref(false)
const showAll = ref(false)
const records = ref([])
const chartData = ref([])

const healthGlucoseRef = ref(null)
const healthBmiRef = ref(null)
let healthGlucoseChart = null
let healthBmiChart = null
const selectedHistoryId = ref(null)
const healthStatusMap = {
  DONE: { text: '已完成', type: 'success' },
  PENDING: { text: '诊断中', type: 'warning' },
  SAVED: { text: '已保存', type: 'info' }
}

const expandedIds = reactive(new Set())
function toggleDetail(uid) {
  if (expandedIds.has(uid)) {
    expandedIds.delete(uid)
  } else {
    expandedIds.add(uid)
  }
}

const EXERCISE_MAP = { '1': '很少运动', '2': '偶尔运动', '3': '经常运动', '4': '每天运动' }
const DIET_MAP = { '1': '不规律', '2': '一般', '3': '均衡饮食', '4': '健康饮食' }
const RISK_MAP = { 'low': '低风险', 'medium': '中风险', 'high': '高风险' }
function formatExercise(val) { return EXERCISE_MAP[val] || val || '-' }
function formatDiet(val) { return DIET_MAP[val] || val || '-' }
function formatRisk(val) { return RISK_MAP[val] || val || '-' }

const {
  historyList: healthHistoryList,
  loadingHistory: loadingHealthHistory,
  loadHistory: loadHealthHistory
} = useHealthHistory()

function formatHistoryTime(ts) {
  if (!ts) return ''
  const d = new Date(ts)
  return `${d.getMonth() + 1}/${d.getDate()} ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}

function renderHealthGlucoseChart() {
  if (!healthGlucoseRef.value || healthHistoryList.value.length === 0) return
  if (healthGlucoseChart) healthGlucoseChart.dispose()
  healthGlucoseChart = echarts.init(healthGlucoseRef.value)
  const sorted = [...healthHistoryList.value].sort((a, b) => new Date(a.createTime) - new Date(b.createTime))
  const times = sorted.map(item => formatHistoryTime(item.createTime))
  const glucoseData = sorted.map(item => item.glucose ?? null)
  healthGlucoseChart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#e5e7eb',
      borderWidth: 1,
      textStyle: { color: '#374151', fontSize: 12 }
    },
    grid: { top: 30, right: 20, bottom: 55, left: 55 },
    xAxis: {
      type: 'category',
      data: times,
      axisLabel: { rotate: 30, fontSize: 10, color: '#9ca3af' },
      axisLine: { lineStyle: { color: '#e5e7eb' } },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      name: 'mg/dL',
      nameTextStyle: { color: '#9ca3af', fontSize: 10 },
      axisLabel: { fontSize: 10, color: '#9ca3af' },
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f3f4f6', type: 'dashed' } }
    },
    series: [{
      data: glucoseData,
      type: 'line',
      smooth: true,
      showSymbol: true,
      symbol: 'circle',
      symbolSize: 6,
      lineStyle: { width: 2.5, color: '#E6A23C' },
      itemStyle: { color: '#E6A23C', borderColor: '#fff', borderWidth: 2 },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(230,162,60,0.25)' },
          { offset: 1, color: 'rgba(230,162,60,0.02)' }
        ])
      },
      markLine: {
        silent: true,
        symbol: 'none',
        lineStyle: { color: '#F56C6C', type: 'dashed', width: 1 },
        data: [{ yAxis: 110, label: { formatter: '上限 110', position: 'insideEndTop', fontSize: 10, color: '#F56C6C' } }]
      }
    }]
  })
}

function renderHealthBmiChart() {
  if (!healthBmiRef.value || healthHistoryList.value.length === 0) return
  if (healthBmiChart) healthBmiChart.dispose()
  healthBmiChart = echarts.init(healthBmiRef.value)
  const sorted = [...healthHistoryList.value].sort((a, b) => new Date(a.createTime) - new Date(b.createTime))
  const times = sorted.map(item => formatHistoryTime(item.createTime))
  const bmiData = sorted.map(item => item.bmi ?? null)
  healthBmiChart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#e5e7eb',
      borderWidth: 1,
      textStyle: { color: '#374151', fontSize: 12 }
    },
    grid: { top: 30, right: 20, bottom: 55, left: 55 },
    xAxis: {
      type: 'category',
      data: times,
      axisLabel: { rotate: 30, fontSize: 10, color: '#9ca3af' },
      axisLine: { lineStyle: { color: '#e5e7eb' } },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      name: 'BMI',
      nameTextStyle: { color: '#9ca3af', fontSize: 10 },
      axisLabel: { fontSize: 10, color: '#9ca3af' },
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f3f4f6', type: 'dashed' } },
      min: 18,
      max: 30
    },
    series: [{
      data: bmiData,
      type: 'line',
      smooth: true,
      showSymbol: true,
      symbol: 'diamond',
      symbolSize: 6,
      lineStyle: { width: 2.5, color: '#409EFF' },
      itemStyle: { color: '#409EFF', borderColor: '#fff', borderWidth: 2 },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64,158,255,0.25)' },
          { offset: 1, color: 'rgba(64,158,255,0.02)' }
        ])
      },
      markArea: {
        silent: true,
        data: [[
          { yAxis: 18.5, itemStyle: { color: 'rgba(16,185,129,0.06)' } },
          { yAxis: 24.9 }
        ]]
      },
      markLine: {
        silent: true,
        symbol: 'none',
        lineStyle: { color: '#F56C6C', type: 'dashed', width: 1 },
        data: [{ yAxis: 24.9, label: { formatter: '超重 24.9', position: 'insideEndTop', fontSize: 10, color: '#F56C6C' } }]
      }
    }]
  })
}

const mainRecords = computed(() => records.value.filter(r => r.recordType === 'visit' || r.recordType === 'self_check'))
const displayedRecords = computed(() => {
  const all = mainRecords.value
  return showAll.value ? all : all.slice(0, 2)
})
const allRecords = computed(() => {
  const visits = mainRecords.value.map(r => ({ ...r, _source: 'visit', _uid: `v_${r.id}`, _time: new Date(r.recordDate).getTime() || 0 }))
  const healths = healthHistoryList.value.map(r => ({ ...r, _source: 'health', _uid: `h_${r.id}`, _time: new Date(r.createTime).getTime() || 0 }))
  return [...visits, ...healths].sort((a, b) => b._time - a._time)
})
const displayedAllRecords = computed(() => {
  return showAll.value ? allRecords.value : allRecords.value.slice(0, 4)
})
const healthPlanRecords = computed(() => records.value.filter(r => r.recordType === 'ai_plan' && r.diagnosis && !r.diagnosis.includes('控糖食谱')))
const recipeRecords = computed(() => records.value.filter(r => r.recordType === 'ai_plan' && r.diagnosis && r.diagnosis.includes('控糖食谱')))

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const detailVisible = ref(false)
const detailRecord = ref(null)
const drawerTopPx = ref(0)

const isDetailPlan = computed(() => detailRecord.value?.recordType === 'ai_plan' && detailRecord.value?.diagnosis && !detailRecord.value.diagnosis.includes('控糖食谱'))
const isDetailRecipe = computed(() => detailRecord.value?.recordType === 'ai_plan' && detailRecord.value?.diagnosis && detailRecord.value.diagnosis.includes('控糖食谱'))
const drawerSize = computed(() => {
  const screenWidth = window.innerWidth
  if (isDetailPlan.value || isDetailRecipe.value) {
    return Math.min(680, screenWidth - 40) + 'px'
  }
  return Math.min(520, screenWidth - 40) + 'px'
})
const planDays = ref([])
const completedPlanDays = ref([])
const collapsedPlanDays = ref([])
const weeklyCollapsed = ref(false)
const planProgress = computed(() => planDays.value.length > 0 ? Math.round(completedPlanDays.value.length / planDays.value.length * 100) : 0)
const planCompletedCount = computed(() => completedPlanDays.value.length)
const planProgressColors = computed(() => {
  const p = planProgress.value
  if (p >= 100) return '#10b981'
  if (p >= 50) return '#e6a23c'
  return '#f59e0b'
})
const parsedRecipeMeals = computed(() => parseRecipeText(detailRecord.value?.treatmentPlan || ''))
const parsedPlanLines = computed(() => {
  const text = detailRecord.value?.treatmentPlan || ''
  return text.split('\n').filter(line => line.trim()).map(line => {
    if (line.includes('|') && /[\u4e00-\u9fa5].*\|/.test(line)) {
      const items = line.split('|').map(s => s.trim()).filter(Boolean).map(item => {
        const calMatch = item.match(/(约[\d.]+kcal)/)
        const cal = calMatch ? calMatch[1] : ''
        const rest = item.replace(/约[\d.]+kcal/, '').trim()
        const portionMatch = rest.match(/(\d+[\u4e00-\u9fa5]+)$/)
        let name = rest
        let portion = ''
        if (portionMatch) {
          portion = portionMatch[1]
          name = rest.substring(0, rest.length - portion.length).trim()
        }
        return { name, portion, cal }
      })
      return { type: 'food', items }
    }
    return { type: 'text', content: line }
  })
})

function parsePlanDays(text) {
  if (!text) return []
  const lines = text.split('\n')
  const days = []
  let currentDay = null
  for (const line of lines) {
    const dayMatch = line.match(/第(\d+)[天日]/)
    if (dayMatch) {
      if (currentDay) days.push(currentDay)
      currentDay = { label: `第${dayMatch[1]}天`, diet: '', exercise: '', notes: '' }
      const rest = line.substring(line.indexOf(dayMatch[0]) + dayMatch[0].length).trim()
      const dietMatch = rest.match(/饮食[：:\-]?\s*(.+?)(?=；|运动|$)/)
      const exerciseMatch = rest.match(/运动[：:\-]?\s*(.+?)(?=；|注意|$)/)
      const notesMatch = rest.match(/注意[：:\-]?\s*(.+?)$/)
      if (dietMatch) currentDay.diet = dietMatch[1].trim()
      if (exerciseMatch) currentDay.exercise = exerciseMatch[1].trim()
      if (notesMatch) currentDay.notes = notesMatch[1].trim()
      continue
    }
    if (!currentDay) continue
    const dietContent = line.match(/饮食[：:\-]?\s*(.+)/)
    const exerciseContent = line.match(/运动[：:\-]?\s*(.+)/)
    const notesContent = line.match(/注意[：:\-]?\s*(.+)/)
    if (dietContent) currentDay.diet = (currentDay.diet + ' ' + dietContent[1].trim()).trim()
    else if (exerciseContent) currentDay.exercise = (currentDay.exercise + ' ' + exerciseContent[1].trim()).trim()
    else if (notesContent) currentDay.notes = (currentDay.notes + ' ' + notesContent[1].trim()).trim()
  }
  if (currentDay) days.push(currentDay)
  return days
}

function parseRecipeText(text) {
  if (!text) return []
  const result = []
  const lines = text.split('\n')
  let curMeal = null
  for (const line of lines) {
    const t = line.trim()
    if (!t) continue
    const mealMatch = t.match(/【(.+?)】|^([早午晚]餐|加餐)/)
    if (mealMatch) {
      if (curMeal && curMeal.foods.length > 0) result.push(curMeal)
      const label = mealMatch[1] || mealMatch[2]
      let icon = '🍽️'
      if (/早/.test(label)) icon = '🌅'
      else if (/午/.test(label)) icon = '☀️'
      else if (/晚/.test(label)) icon = '🌆'
      else if (/加餐/.test(label)) icon = '🍪'
      curMeal = { label, icon, gi: '', giText: '', totalCal: '', totalNutri: '', foods: [] }
      continue
    }
    if (!curMeal) continue
    const nutriMatch = t.match(/GI≈|每餐小计|小计|总热量|营养总计|碳水|蛋白|脂肪/)
    if (nutriMatch) {
      const giMatch = t.match(/GI≈([\d.]+)/)
      const calMatch = t.match(/总热量≈([\d.]+)kcal/)
      if (giMatch) {
        const giVal = parseFloat(giMatch[1])
        curMeal.gi = `GI≈${giMatch[1]}`
        curMeal.giText = giVal <= 55 ? '低升糖指数' : giVal <= 69 ? '中升糖指数' : '高升糖指数'
      }
      if (calMatch) curMeal.totalCal = `🔥 ${calMatch[1]}kcal`
      continue
    }
    const items = t.split(/\s*[|·]\s*/).map(s => s.trim()).filter(Boolean)
    for (const item of items) {
      const calMatch = item.match(/\(约([\d.]+)kcal\)/)
      const cal = calMatch ? `约${calMatch[1]}kcal` : ''
      const namePart = item.replace(/\(约[\d.]+kcal\)/, '').trim()
      const portionMatch = namePart.match(/(\d+)(片|个|碗|ml|g|只|粒|杯|根|勺|盘|块)/)
      let name = namePart
      let portion = ''
      if (portionMatch) {
        const idx = namePart.indexOf(portionMatch[0])
        if (idx > 0) {
          portion = namePart.substring(idx).trim()
          name = namePart.substring(0, idx).trim()
        } else {
          name = namePart
          portion = portionMatch[0]
        }
      }
      curMeal.foods.push({ name, portion, cal })
    }
  }
  if (curMeal && curMeal.foods.length > 0) result.push(curMeal)
  return result
}

function togglePlanDay(idx) {
  const i = collapsedPlanDays.value.indexOf(idx)
  if (i === -1) collapsedPlanDays.value.push(idx)
  else collapsedPlanDays.value.splice(i, 1)
}

function giBadgeClass(text) {
  if (!text) return ''
  if (text.includes('低')) return 'gi-low'
  if (text.includes('中')) return 'gi-mid'
  if (text.includes('高')) return 'gi-high'
  return ''
}


const glucoseChartRef = ref(null)
const bpChartRef = ref(null)
const weightChartRef = ref(null)
let glucoseChart = null
let bpChart = null
let weightChart = null

const formData = reactive({
  id: null,
  recordType: 'visit',
  recordDate: '',
  hospital: '',
  doctorName: '',
  chiefComplaint: '',
  diagnosis: '',
  treatmentPlan: '',
  glucoseFasting: null,
  glucosePostprandial: null,
  hba1c: null,
  bloodPressureSystolic: null,
  bloodPressureDiastolic: null,
  weight: null,
  bmi: null
})

const formRules = {
  recordType: [{ required: true, message: '请选择记录类型', trigger: 'change' }],
  recordDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
  glucoseFasting: [{
    validator: (rule, value, callback) => {
      if (value !== null && value !== undefined && value !== '') {
        const num = Number(value)
        if (isNaN(num) || num < 1.0 || num > 35.0) {
          callback(new Error('空腹血糖范围1.0-35.0 mmol/L'))
        }
      }
      callback()
    },
    trigger: 'blur'
  }],
  glucosePostprandial: [{
    validator: (rule, value, callback) => {
      if (value !== null && value !== undefined && value !== '') {
        const num = Number(value)
        if (isNaN(num) || num < 1.0 || num > 40.0) {
          callback(new Error('餐后血糖范围1.0-40.0 mmol/L'))
        }
      }
      callback()
    },
    trigger: 'blur'
  }],
  hba1c: [{
    validator: (rule, value, callback) => {
      if (value !== null && value !== undefined && value !== '') {
        const num = Number(value)
        if (isNaN(num) || num < 3.0 || num > 18.0) {
          callback(new Error('糖化血红蛋白范围3.0-18.0%'))
        }
      }
      callback()
    },
    trigger: 'blur'
  }],
  bloodPressureSystolic: [{
    validator: (rule, value, callback) => {
      if (value !== null && value !== undefined && value !== '') {
        const num = Number(value)
        if (isNaN(num) || num < 60 || num > 260) {
          callback(new Error('收缩压范围60-260 mmHg'))
        }
      }
      callback()
    },
    trigger: 'blur'
  }],
  bloodPressureDiastolic: [{
    validator: (rule, value, callback) => {
      if (value !== null && value !== undefined && value !== '') {
        const num = Number(value)
        if (isNaN(num) || num < 30 || num > 160) {
          callback(new Error('舒张压范围30-160 mmHg'))
        }
      }
      callback()
    },
    trigger: 'blur'
  }],
  weight: [{
    validator: (rule, value, callback) => {
      if (value !== null && value !== undefined && value !== '') {
        const num = Number(value)
        if (isNaN(num) || num < 20 || num > 250) {
          callback(new Error('体重范围20-250 kg'))
        }
      }
      callback()
    },
    trigger: 'blur'
  }]
}

const hasBPData = computed(() => chartData.value.some(r => r.bloodPressureSystolic))
const hasWeightData = computed(() => chartData.value.some(r => r.weight || r.bmi))

function getTypeTag(type) {
  const map = {
    visit: { label: '就诊', type: 'primary' },
    self_check: { label: '自查', type: 'success' },
    ai_plan: { label: 'AI计划', type: 'warning' }
  }
  return map[type] || { label: type, type: 'info' }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

function truncateText(text, max) {
  if (!text) return ''
  return text.length > max ? text.substring(0, max) + '...' : text
}

function hasMetrics(record) {
  return record.glucoseFasting || record.glucosePostprandial ||
         record.bloodPressureSystolic || record.bmi || record.weight
}

function resetForm() {
  Object.assign(formData, {
    id: null, recordType: 'visit', recordDate: '', hospital: '', doctorName: '',
    chiefComplaint: '', diagnosis: '', treatmentPlan: '',
    glucoseFasting: null, glucosePostprandial: null, hba1c: null,
    bloodPressureSystolic: null, bloodPressureDiastolic: null, weight: null, bmi: null
  })
}

function openAddDialog() {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(record) {
  isEdit.value = true
  Object.assign(formData, { ...record })
  dialogVisible.value = true
}

function openDetail(record) {
  detailRecord.value = record
  detailVisible.value = true
  if (record._source === 'health') {
    selectedHistoryId.value = record.id
    return
  }
  if (record.recordType === 'ai_plan' && record.diagnosis && !record.diagnosis.includes('控糖食谱')) {
    planDays.value = parsePlanDays(record.treatmentPlan)
    completedPlanDays.value = []
    collapsedPlanDays.value = planDays.value.map((_, i) => i)
    weeklyCollapsed.value = true
  }
}

function toggleWeekly() {
  weeklyCollapsed.value = !weeklyCollapsed.value
  if (weeklyCollapsed.value) {
    collapsedPlanDays.value = planDays.value.map((_, i) => i)
  } else {
    collapsedPlanDays.value = []
  }
}

async function fetchRecords() {
  loading.value = true
  const cachedData = CacheHelper.getWithExpiry(CACHE_KEY_RECORDS)
  if (cachedData && Array.isArray(cachedData) && cachedData.length > 0) {
    records.value = cachedData
    loading.value = false
  }
  try {
    const res = await request.get('/api/patient-visit/my', { params: { limit: 50 } })
    if (res.code === '200' && Array.isArray(res.data)) {
      if (res.data.length > 0) {
        records.value = res.data
        CacheHelper.setWithExpiry(CACHE_KEY_RECORDS, res.data, CACHE_TTL)
      } else {
        CacheHelper.remove(CACHE_KEY_RECORDS)
        records.value = [...DEMO_RECORDS]
      }
    } else {
      if (!cachedData || cachedData.length === 0) {
        records.value = [...DEMO_RECORDS]
      }
    }
  } catch (e) {
    console.error('加载记录失败，尝试使用缓存数据', e)
    if (!cachedData || cachedData.length === 0) {
      records.value = [...DEMO_RECORDS]
    }
  } finally {
    loading.value = false
  }
}

async function fetchChartData() {
  const cachedChart = CacheHelper.getWithExpiry(CACHE_KEY_CHART)
  if (cachedChart && Array.isArray(cachedChart) && cachedChart.length > 0) {
    chartData.value = cachedChart
  }
  try {
    const res = await request.get('/api/patient-visit/my/chart-data')
    if (res.code === '200' && Array.isArray(res.data)) {
      if (res.data.length > 0) {
        chartData.value = res.data
        CacheHelper.setWithExpiry(CACHE_KEY_CHART, res.data, CACHE_TTL)
      } else {
        CacheHelper.remove(CACHE_KEY_CHART)
        chartData.value = [...DEMO_CHART_DATA]
      }
    } else if (chartData.value.length === 0) {
      chartData.value = [...DEMO_CHART_DATA]
    }
  } catch (e) {
    console.error('加载图表数据失败，尝试使用缓存数据', e)
    if (chartData.value.length === 0) {
      chartData.value = [...DEMO_CHART_DATA]
    }
  }
}

function loadAll() {
  showAll.value = true
}

function collapseList() {
  showAll.value = false
}

async function submitForm() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const payload = { ...formData }
    const isDemoRecord = isEdit.value && payload.id < 0
    let res
    if (isEdit.value && !isDemoRecord) {
      res = await request.put(`/api/patient-visit/${payload.id}`, payload)
    } else {
      if (isDemoRecord) delete payload.id
      res = await request.post('/api/patient-visit', payload)
    }
    if (res.code === '200') {
      ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
      dialogVisible.value = false
      CacheHelper.remove(CACHE_KEY_RECORDS)
      CacheHelper.remove(CACHE_KEY_CHART)
      fetchRecords()
      fetchChartData()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败: ' + (e.message || '网络异常'))
  } finally {
    submitting.value = false
  }
}

function handleDelete(record) {
  if (record.id < 0) {
    records.value = records.value.filter(r => r.id !== record.id)
    CacheHelper.remove(CACHE_KEY_RECORDS)
    ElMessage.success('删除成功')
    return
  }
  ElMessageBox.confirm('确定要删除该记录吗？', '提示', { type: 'warning' }).then(async () => {
    try {
      const apiUrl = record._source === 'health'
        ? `/api/health-profile/${record.id}`
        : `/api/patient-visit/${record.id}`
      const res = await request.delete(apiUrl)
      if (res.code === '200') {
        ElMessage.success('删除成功')
        CacheHelper.remove(CACHE_KEY_RECORDS)
        CacheHelper.remove(CACHE_KEY_CHART)
        fetchRecords()
        fetchChartData()
        if (record._source === 'health') {
          loadHealthHistory()
        }
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (e) {
      ElMessage.error('删除失败: ' + (e.message || '网络异常'))
    }
  }).catch(() => {})
}

function renderGlucoseChart() {
  if (!glucoseChartRef.value) return
  if (glucoseChart) glucoseChart.dispose()
  glucoseChart = echarts.init(glucoseChartRef.value)

  const dates = chartData.value.map(r => formatDate(r.recordDate))
  const fasting = chartData.value.map(r => r.glucoseFasting ?? null)
  const postprandial = chartData.value.map(r => r.glucosePostprandial ?? null)

  glucoseChart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e5e7eb',
      borderWidth: 1,
      textStyle: { color: '#374151', fontSize: 13 },
      axisPointer: { type: 'cross', lineStyle: { color: '#9ca3af', type: 'dashed' } }
    },
    legend: {
      data: ['空腹血糖', '餐后血糖'],
      top: 5,
      left: 'center',
      textStyle: { fontSize: 12, color: '#6b7280' }
    },
    grid: { top: 45, right: 25, bottom: 65, left: 60 },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: { rotate: 30, fontSize: 11, color: '#9ca3af' },
      axisLine: { lineStyle: { color: '#e5e7eb' } },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      name: 'mmol/L',
      nameTextStyle: { color: '#9ca3af', fontSize: 11 },
      axisLabel: { fontSize: 11, color: '#9ca3af' },
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f3f4f6', type: 'dashed' } }
    },
    visualMap: {
      show: false,
      pieces: [
        { lte: 6.1, color: '#10b981' },
        { gt: 6.1, lte: 7.8, color: '#f59e0b' },
        { gt: 7.8, color: '#ef4444' }
      ],
      seriesIndex: 0
    },
    series: [
      {
        name: '空腹血糖',
        type: 'line',
        data: fasting,
        smooth: true,
        connectNulls: true,
        symbolSize: 7,
        symbol: 'circle',
        lineStyle: { width: 2.5, color: '#10b981' },
        itemStyle: { color: '#10b981', borderColor: '#fff', borderWidth: 2 },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(16, 185, 129, 0.25)' },
            { offset: 1, color: 'rgba(16, 185, 129, 0.02)' }
          ])
        },
        markLine: {
          silent: true,
          symbol: 'none',
          lineStyle: { color: '#ef4444', type: 'dashed', width: 1 },
          data: [{ yAxis: 7.0, label: { formatter: '上限 7.0', position: 'insideEndTop', fontSize: 10, color: '#ef4444' } }]
        }
      },
      {
        name: '餐后血糖',
        type: 'line',
        data: postprandial,
        smooth: true,
        connectNulls: true,
        symbolSize: 7,
        symbol: 'diamond',
        lineStyle: { width: 2.5, color: '#8b5cf6', type: 'dashed' },
        itemStyle: { color: '#8b5cf6', borderColor: '#fff', borderWidth: 2 },
        markLine: {
          silent: true,
          symbol: 'none',
          lineStyle: { color: '#f59e0b', type: 'dashed', width: 1 },
          data: [{ yAxis: 10.0, label: { formatter: '上限 10.0', position: 'insideEndTop', fontSize: 10, color: '#f59e0b' } }]
        }
      }
    ]
  })
}

function renderBPChart() {
  if (!bpChartRef.value) return
  if (bpChart) bpChart.dispose()
  bpChart = echarts.init(bpChartRef.value)

  const filtered = chartData.value.filter(r => r.bloodPressureSystolic)
  const dates = filtered.map(r => formatDate(r.recordDate))
  const systolic = filtered.map(r => r.bloodPressureSystolic)
  const diastolic = filtered.map(r => r.bloodPressureDiastolic)

  bpChart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e5e7eb',
      borderWidth: 1,
      textStyle: { color: '#374151', fontSize: 13 },
      axisPointer: { type: 'cross', lineStyle: { color: '#9ca3af', type: 'dashed' } }
    },
    legend: {
      data: ['收缩压', '舒张压'],
      top: 5,
      left: 'center',
      textStyle: { fontSize: 12, color: '#6b7280' }
    },
    grid: { top: 45, right: 25, bottom: 65, left: 60 },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: { rotate: 30, fontSize: 11, color: '#9ca3af' },
      axisLine: { lineStyle: { color: '#e5e7eb' } },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      name: 'mmHg',
      nameTextStyle: { color: '#9ca3af', fontSize: 11 },
      axisLabel: { fontSize: 11, color: '#9ca3af' },
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f3f4f6', type: 'dashed' } },
      min: 50,
      max: 180
    },
    series: [
      {
        name: '收缩压',
        type: 'line',
        data: systolic,
        smooth: true,
        symbolSize: 7,
        symbol: 'circle',
        lineStyle: { width: 2.5, color: '#ef4444' },
        itemStyle: { color: '#ef4444', borderColor: '#fff', borderWidth: 2 },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(239, 68, 68, 0.15)' },
            { offset: 1, color: 'rgba(239, 68, 68, 0.02)' }
          ])
        },
        markArea: {
          silent: true,
          data: [[
            { yAxis: 90, itemStyle: { color: 'rgba(16, 185, 129, 0.08)' } },
            { yAxis: 140 }
          ]]
        },
        markLine: {
          silent: true,
          symbol: 'none',
          lineStyle: { color: '#ef4444', type: 'dashed', width: 1 },
          data: [
            { yAxis: 140, label: { formatter: '高压上限', position: 'insideEndTop', fontSize: 10, color: '#ef4444' } }
          ]
        }
      },
      {
        name: '舒张压',
        type: 'line',
        data: diastolic,
        smooth: true,
        symbolSize: 7,
        symbol: 'diamond',
        lineStyle: { width: 2.5, color: '#3b82f6', type: 'dashed' },
        itemStyle: { color: '#3b82f6', borderColor: '#fff', borderWidth: 2 },
        markLine: {
          silent: true,
          symbol: 'none',
          lineStyle: { color: '#3b82f6', type: 'dashed', width: 1 },
          data: [
            { yAxis: 90, label: { formatter: '低压上限', position: 'insideEndTop', fontSize: 10, color: '#3b82f6' } }
          ]
        }
      }
    ]
  })
}

function renderWeightChart() {
  if (!weightChartRef.value) return
  if (weightChart) weightChart.dispose()
  weightChart = echarts.init(weightChartRef.value)

  const filtered = chartData.value.filter(r => r.weight || r.bmi)
  const dates = filtered.map(r => formatDate(r.recordDate))
  const weights = filtered.map(r => r.weight ?? null)
  const bmis = filtered.map(r => r.bmi ?? null)

  weightChart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e5e7eb',
      borderWidth: 1,
      textStyle: { color: '#374151', fontSize: 13 },
      axisPointer: { type: 'cross', lineStyle: { color: '#9ca3af', type: 'dashed' } }
    },
    legend: {
      data: ['体重', 'BMI'],
      top: 5,
      left: 'center',
      textStyle: { fontSize: 12, color: '#6b7280' }
    },
    grid: { top: 45, right: 65, bottom: 65, left: 60 },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: { rotate: 30, fontSize: 11, color: '#9ca3af' },
      axisLine: { lineStyle: { color: '#e5e7eb' } },
      axisTick: { show: false }
    },
    yAxis: [
      {
        type: 'value',
        name: 'kg',
        nameTextStyle: { color: '#9ca3af', fontSize: 11 },
        axisLabel: { fontSize: 11, color: '#9ca3af' },
        axisLine: { show: false },
        splitLine: { lineStyle: { color: '#f3f4f6', type: 'dashed' } },
        position: 'left'
      },
      {
        type: 'value',
        name: 'BMI',
        nameTextStyle: { color: '#9ca3af', fontSize: 11 },
        axisLabel: { fontSize: 11, color: '#9ca3af' },
        axisLine: { show: false },
        splitLine: { show: false },
        position: 'right',
        min: 15,
        max: 35
      }
    ],
    series: [
      {
        name: '体重',
        type: 'line',
        data: weights,
        smooth: true,
        symbolSize: 7,
        symbol: 'circle',
        yAxisIndex: 0,
        lineStyle: { width: 2.5, color: '#06b6d4' },
        itemStyle: { color: '#06b6d4', borderColor: '#fff', borderWidth: 2 },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(6, 182, 212, 0.2)' },
            { offset: 1, color: 'rgba(6, 182, 212, 0.02)' }
          ])
        }
      },
      {
        name: 'BMI',
        type: 'line',
        data: bmis,
        smooth: true,
        symbolSize: 7,
        symbol: 'diamond',
        yAxisIndex: 1,
        lineStyle: { width: 2.5, color: '#f97316', type: 'dashed' },
        itemStyle: { color: '#f97316', borderColor: '#fff', borderWidth: 2 },
        markArea: {
          silent: true,
          data: [[
            { yAxis: 18.5, itemStyle: { color: 'rgba(16, 185, 129, 0.08)' } },
            { yAxis: 24.9 }
          ]]
        },
        markLine: {
          silent: true,
          symbol: 'none',
          lineStyle: { color: '#f97316', type: 'dashed', width: 1 },
          data: [
            { yAxis: 24.9, label: { formatter: '超重线', position: 'insideEndTop', fontSize: 10, color: '#f97316' } }
          ]
        }
      }
    ]
  })
}

function renderAllCharts() {
  if (chartData.value.length >= 2) {
    if (!glucoseChartRef.value) {
      setTimeout(() => renderAllCharts(), 150)
      return
    }
    renderGlucoseChart()
    if (hasBPData.value && bpChartRef.value) renderBPChart()
    if (hasWeightData.value && weightChartRef.value) renderWeightChart()
  }
  if (healthHistoryList.value.length > 0) {
    if (!healthGlucoseRef.value) {
      setTimeout(() => renderAllCharts(), 150)
      return
    }
    renderHealthGlucoseChart()
    renderHealthBmiChart()
  }
}

function handleResize() {
  glucoseChart?.resize()
  bpChart?.resize()
  weightChart?.resize()
  healthGlucoseChart?.resize()
  healthBmiChart?.resize()
}

watch(chartData, (newVal) => {
  if (newVal.length >= 2) {
    nextTick(() => renderAllCharts())
  }
}, { deep: true })

watch(healthHistoryList, (newVal) => {
  if (newVal.length > 0) {
    nextTick(() => {
      renderHealthGlucoseChart()
      renderHealthBmiChart()
    })
  }
}, { deep: true })

function handleDrawerScroll() {
  const scrollY = window.scrollY || document.documentElement.scrollTop
  const offset = Math.max(0, scrollY - 60)
  drawerTopPx.value = offset
  const drawer = document.querySelector('.el-drawer.open') || document.querySelector('.el-overlay.is-drawer .el-drawer')
  if (drawer) {
    drawer.style.setProperty('top', offset + 'px', 'important')
  }
}

watch(detailVisible, (visible) => {
  if (visible) {
    nextTick(() => {
      handleDrawerScroll()
      setTimeout(handleDrawerScroll, 300)
      window.addEventListener('scroll', handleDrawerScroll, { passive: true })
    })
  } else {
    window.removeEventListener('scroll', handleDrawerScroll)
    drawerTopPx.value = 0
  }
})

onMounted(async () => {
  await Promise.all([fetchRecords(), fetchChartData(), loadHealthHistory()])
  window.addEventListener('resize', handleResize)
  nextTick(() => renderAllCharts())
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  window.removeEventListener('scroll', handleDrawerScroll)
  glucoseChart?.dispose()
  bpChart?.dispose()
  weightChart?.dispose()
  healthGlucoseChart?.dispose()
  healthBmiChart?.dispose()
})
</script>

<style scoped>
.treatment-record-page {
  padding: 20px;
  min-height: 100%;
  background: #f5f7fa;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: #303133;
}

.page-subtitle {
  margin-left: 12px;
  font-size: 13px;
  color: #909399;
}

.main-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.records-section, .charts-section {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.ai-grid-row {
  display: flex;
  gap: 20px;
}

.ai-grid-row .ai-section {
  flex: 1;
  min-width: 0;
  border-radius: 12px;
  padding: 0;
  box-shadow: none;
}

.ai-cards {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.ai-card {
  display: flex;
  flex-direction: column;
  padding: 18px 20px;
  border-radius: 14px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  border: 1px solid #eef2f6;
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.ai-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 3px;
}

.ai-card::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(circle at 20% 80%, rgba(255, 255, 255, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.2) 0%, transparent 50%);
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.ai-card:hover::after {
  opacity: 1;
}

.ai-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.ai-card.plan-card {
  background: linear-gradient(135deg, #c3eacf 0%, #d6efe3 50%, #c4dae8 100%);
  border: 1px solid #d1fae5;
}

.ai-card.plan-card::before {
  background: linear-gradient(90deg, #10b981, #34d399);
}

.ai-card.plan-card:hover {
  box-shadow: 0 8px 24px rgba(16, 185, 129, 0.15);
}

.ai-card.recipe-card {
  background: linear-gradient(135deg, #eff6ff 0%, #e0f2fe 50%, #f5f3ff 100%);
  border: 1px solid #bfdbfe;
}

.ai-card.recipe-card::before {
  background: linear-gradient(90deg, #3b82f6, #60a5fa);
}

.ai-card.recipe-card:hover {
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.15);
}

.ai-card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  position: relative;
  z-index: 1;
}

.ai-card-icon {
  font-size: 20px;
}

.ai-card-date {
  font-size: 12px;
  color: #64748b;
}

.ai-card-summary {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 6px;
  line-height: 1.4;
  position: relative;
  z-index: 1;
}

.ai-card-preview {
  font-size: 13px;
  color: #475569;
  line-height: 1.5;
  white-space: pre-wrap;
  position: relative;
  z-index: 1;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.loading-state {
  padding: 20px 0;
}

.empty-state {
  padding: 40px 0;
}

.record-cards {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.record-card {
  display: flex;
  flex-direction: column;
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 10px;
  transition: all 0.2s ease;
}

.record-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.12);
  transform: translateY(-1px);
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.card-date {
  font-size: 13px;
  color: #909399;
}

.card-body {
  flex: 1;
}

.card-diagnosis, .card-complaint {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  margin-bottom: 6px;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}

.card-diagnosis .el-icon, .card-complaint .el-icon {
  margin-top: 3px;
  color: #909399;
  flex-shrink: 0;
}

.card-metrics {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.metric-item {
  font-size: 12px;
  padding: 2px 8px;
  background: #f0f2f5;
  border-radius: 4px;
  color: #606266;
}

.card-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #f0f2f5;
}

.action-left, .action-right {
  display: flex;
  align-items: center;
  gap: 4px;
}

.record-card-wrapper {
  display: flex;
  flex-direction: column;
}

.inline-detail-panel {
  margin: 0 0 4px;
  padding: 14px 16px;
  background: #f8fafc;
  border-radius: 0 0 10px 10px;
  border: 1px solid #e8ecf1;
  border-top: none;
}

.detail-section-title-inline {
  font-size: 12px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 6px;
  padding-bottom: 4px;
  border-bottom: 1px dashed #e0e6ed;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 4px 12px;
}

.detail-cell {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 2px 0;
}

.detail-label {
  font-size: 12px;
  color: #909399;
  flex-shrink: 0;
}

.detail-value {
  font-size: 12px;
  color: #303133;
  font-weight: 500;
  text-align: right;
}

.detail-value.risk-low {
  color: #67c23a;
}

.detail-value.risk-medium {
  color: #e6a23c;
}

.detail-value.risk-high {
  color: #f56c6c;
}

.detail-symptoms-text {
  font-size: 12px;
  color: #606266;
  line-height: 1.6;
  padding: 4px 0;
}

.detail-advice-text {
  font-size: 12px;
  color: #606266;
  line-height: 1.6;
  padding: 4px 0;
  white-space: pre-wrap;
}

.ai-plan-inline-detail {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.plan-preview {
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
  white-space: pre-wrap;
}

.detail-slide-enter-active,
.detail-slide-leave-active {
  transition: all 0.25s ease;
  overflow: hidden;
}

.detail-slide-enter-from,
.detail-slide-leave-to {
  opacity: 0;
  max-height: 0;
}

.detail-slide-enter-to,
.detail-slide-leave-from {
  opacity: 1;
  max-height: 800px;
}

.charts-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 20px;
}

.chart-card {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 16px;
  background: linear-gradient(180deg, #fafbfc 0%, #ffffff 100%);
  transition: box-shadow 0.3s ease, transform 0.2s ease;
}

.chart-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.chart-title {
  margin: 0 0 12px;
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  display: flex;
  align-items: center;
  gap: 8px;
}

.chart-title::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 16px;
  background: linear-gradient(180deg, #3b82f6, #8b5cf6);
  border-radius: 2px;
}

.chart-box {
  width: 100%;
  height: 300px;
}

.plan-content {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.6;
}

.detail-section {
  margin-top: 16px;
}

.detail-section-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px;
  padding-bottom: 6px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-text-block {
  background: #fafafa;
  border-radius: 8px;
  padding: 12px;
  font-size: 13px;
  line-height: 1.8;
  color: #606266;
  white-space: pre-wrap;
  word-break: break-word;
}

.detail-text-success {
  background: #f0f9eb;
  color: #67c23a;
  border: 1px solid #e1f3d8;
}

/* ===== 弹窗主题样式 ===== */
.drawer-header {
  padding: 4px 0;
}

.drawer-header-plan {
  background: linear-gradient(135deg, #c3eacf 0%, #d6efe3 50%, #c4dae8 100%);
  margin: -20px -45px 16px -20px;
  padding: 20px;
  border-radius: 0;
  border-bottom: 1px solid #d1fae5;
}

.drawer-header-recipe {
  background: linear-gradient(135deg, #eff6ff 0%, #e0f2fe 50%, #f5f3ff 100%);
  margin: -20px -20px 16px -20px;
  padding: 20px;
  border-radius: 0;
  border-bottom: 1px solid #bfdbfe;
}

.drawer-header-top {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.drawer-date {
  font-size: 13px;
  color: #64748b;
}

.drawer-title-text {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.5;
}

.drawer-header-plan .drawer-title-text {
  color: #065f46;
}

.drawer-header-recipe .drawer-title-text {
  color: #1e40af;
}

/* ===== 健康计划逐步执行 ===== */
.plan-control-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.plan-progress-text {
  font-size: 13px;
  color: #64748b;
  white-space: nowrap;
}

.plan-control-bar + .el-progress {
  margin-bottom: 16px;
}

/* ===== 抽屉内计划列表 ===== */
.plan-days-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.plan-day-card {
  background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 100%);
  border-radius: 12px;
  padding: 4px 16px 12px;
  border: 1px solid #d1fae5;
  border-left: 4px solid #10b981;
  transition: all 0.2s;
}

.plan-day-card.day-done {
  opacity: 0.65;
  border-left-color: #6b7280;
  background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
}

.plan-day-card.day-done .day-label {
  text-decoration: line-through;
  color: #9ca3af;
}

.plan-day-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  padding: 8px 0;
}

.plan-day-header .el-checkbox {
  margin-right: 0;
}

.day-label {
  font-weight: 600;
  font-size: 15px;
  color: #1e293b;
  margin-left: 4px;
}

.day-toggle {
  font-size: 14px;
  color: #64748b;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background 0.2s;
}

.day-toggle:hover {
  background: rgba(16, 185, 129, 0.1);
}

.plan-day-body {
  padding: 8px 0 0 32px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.plan-item-row {
  display: flex;
  gap: 10px;
  align-items: flex-start;
}

.plan-item-row .item-icon {
  font-size: 16px;
  flex-shrink: 0;
  margin-top: 1px;
}

.plan-item-row > div {
  flex: 1;
  min-width: 0;
}

.item-label {
  display: block;
  font-size: 12px;
  font-weight: 600;
  color: #10b981;
  margin-bottom: 2px;
}

.item-value {
  font-size: 13px;
  color: #475569;
  line-height: 1.5;
}

/* ===== 食谱详情 ===== */
.recipe-detail-body {
  padding: 0;
}

.recipe-meals {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.recipe-meal-section {
  background: linear-gradient(135deg, #fffbeb 0%, #fff7ed 100%);
  border: 1px solid #fde2c8;
  border-radius: 12px;
  overflow: hidden;
}

.recipe-meal-header {
  font-weight: 600;
  font-size: 15px;
  color: #e67e22;
  padding: 12px 16px;
  border-bottom: 1px solid #fde2c8;
  letter-spacing: 0.5px;
}

.recipe-food-list {
  padding: 8px 16px;
}

.recipe-food-row {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #fef3c7;
  font-size: 13px;
  gap: 8px;
}

.recipe-food-row:last-child {
  border-bottom: none;
}

.plan-content-lines {
  padding: 4px 0;
}

.plan-content-line {
  padding: 10px 16px;
  border-bottom: 1px dashed #bfdbfe;
  font-size: 13px;
  line-height: 1.6;
  color: #1e293b;
  background: #f8fafc;
  transition: background 0.2s ease;
}

.plan-content-line:first-child {
  border-radius: 8px 8px 0 0;
}

.plan-content-line:last-child {
  border-bottom: none;
  border-radius: 0 0 8px 8px;
}

.plan-content-line:hover {
  background: #f1f5f9;
}

.plan-content-food-group {
  padding: 8px 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  background: #f8fafc;
  border-bottom: 1px dashed #bfdbfe;
}

.plan-food-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  background: #fff;
  border: 1px solid #e0f2fe;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.plan-food-item:hover {
  border-color: #93c5fd;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.1);
}

.pf-name {
  font-size: 13px;
  font-weight: 600;
  color: #1e293b;
  background: #fefce8;
  border: 1px solid #fde68a;
  border-radius: 6px;
  padding: 2px 8px;
  text-decoration: underline;
  text-underline-offset: 3px;
  text-decoration-color: #f59e0b;
}

.pf-portion {
  font-size: 12px;
  color: #92400e;
  background: linear-gradient(135deg, #fffbeb, #fef3c7);
  border: 1px solid #fbbf24;
  border-radius: 6px;
  padding: 2px 8px;
  font-weight: 500;
  text-decoration: underline;
  text-underline-offset: 3px;
}

.pf-cal {
  font-size: 12px;
  color: #b45309;
  font-weight: 600;
  background: linear-gradient(135deg, #fff7ed, #ffedd5);
  border: 1px solid #fdba74;
  border-radius: 6px;
  padding: 2px 8px;
  text-decoration: underline;
  text-underline-offset: 3px;
  text-decoration-color: #f97316;
}

.rf-name {
  flex: 0 0 38%;
  color: #1e293b;
  font-weight: 600;
  font-size: 13px;
  background: #fefce8;
  border: 1px solid #fde68a;
  border-radius: 6px;
  padding: 3px 10px;
  text-decoration: underline;
  text-underline-offset: 3px;
  text-decoration-color: #f59e0b;
}

.rf-portion {
  flex: 0 0 25%;
  color: #92400e;
  text-align: center;
  font-size: 12px;
  background: linear-gradient(135deg, #fffbeb 0%, #fef3c7 100%);
  border: 1px solid #fbbf24;
  border-radius: 6px;
  padding: 3px 6px;
  font-weight: 500;
  text-decoration: underline;
  text-underline-offset: 3px;
}

.rf-cal {
  flex: 0 0 27%;
  color: #b45309;
  text-align: right;
  font-weight: 600;
  font-size: 12px;
  background: linear-gradient(135deg, #fff7ed 0%, #ffedd5 100%);
  border: 1px solid #fdba74;
  border-radius: 6px;
  padding: 3px 10px;
  text-decoration: underline;
  text-underline-offset: 3px;
  text-decoration-color: #f97316;
}

.recipe-meal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 16px;
  background: linear-gradient(135deg, #fef9e7 0%, #fdf2e9 100%);
  border-top: 1px solid #fce4c8;
  font-size: 13px;
  font-weight: 600;
  color: #1e293b;
  gap: 8px;
}

.recipe-gi-tag {
  display: flex;
  align-items: center;
  gap: 6px;
}

.gi-value {
  background: linear-gradient(135deg, #ecfdf5 0%, #d1fae5 100%);
  border: 1px solid #6ee7b7;
  border-radius: 6px;
  padding: 2px 8px;
  font-size: 12px;
  color: #065f46;
}

.gi-badge {
  font-size: 11px;
  font-weight: 600;
  padding: 2px 10px;
  border-radius: 12px;
  letter-spacing: 0.3px;
}

.gi-badge.gi-low {
  background: #d1fae5;
  color: #065f46;
  border: 1px solid #6ee7b7;
}

.gi-badge.gi-mid {
  background: #fef3c7;
  color: #92400e;
  border: 1px solid #fbbf24;
}

.gi-badge.gi-high {
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fca5a5;
}

.recipe-cal-tag {
  background: linear-gradient(135deg, #fef9c3 0%, #fde68a 100%);
  border: 1px solid #fbbf24;
  border-radius: 6px;
  padding: 2px 10px;
  font-size: 12px;
}

@media (min-width: 768px) {
  .charts-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 1200px) {
  .charts-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

.health-trend-section {
  margin-bottom: 20px;
}

.health-charts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

@media (max-width: 768px) {
  .health-charts-grid {
    grid-template-columns: 1fr;
  }
}

.health-chart-item {
  background: #fafafa;
  border-radius: 10px;
  padding: 12px;
  border: 1px solid #f0f0f0;
}

.echarts-trend-section {
  margin-bottom: 20px;
}

.trend-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.3s ease, transform 0.2s ease;
}

.trend-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.trend-card-profile {
  border-top: 3px solid #10b981;
}

.trend-card-echarts {
  border-top: 3px solid #3b82f6;
}

.trend-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.trend-card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.trend-card-sub {
  font-size: 12px;
  color: #9ca3af;
  background: #f3f4f6;
  padding: 2px 10px;
  border-radius: 10px;
}

.record-total {
  font-size: 13px;
  color: #909399;
  font-weight: normal;
}

.health-record {
  border-left: 3px solid #409EFF;
}

.self-check-result {
  background: #f0f9eb;
  padding: 8px 10px;
  border-radius: 6px;
  font-size: 12px;
  color: #2d6a2d;
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
}
</style>

<style>
/* 新增/编辑弹窗 — 屏幕正中央垂直+水平居中 */
.record-dialog {
  margin: auto !important;
}

.record-dialog .el-dialog__header {
  padding: 20px;
  border-bottom: 1px solid #e5e7eb;
}

/* 抽屉固定在右侧，满屏高度 */
.el-drawer {
  position: fixed !important;
  top: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  left: auto !important;
  transform: none !important;
  width: 680px !important;
  height: 100vh !important;
  max-height: 100vh !important;
  max-width: calc(100vw - 200px) !important;
  margin: 0 !important;
  border-radius: 0 !important;
  box-shadow: -4px 0 20px rgba(0, 0, 0, 0.1) !important;
  overflow: hidden !important;
}

.el-drawer__header {
  padding: 20px !important;
  margin-bottom: 0 !important;
  border-bottom: 1px solid #e5e7eb !important;
  flex-shrink: 0 !important;
}

.el-drawer__body {
  overflow-y: auto !important;
  height: calc(100vh - 72px) !important;
  padding: 20px !important;
}

.plan-drawer .el-drawer__body {
  background: #f0fdf4;
}

.recipe-drawer .el-drawer__body {
  background: #eff6ff;
}

.recipe-food-row {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #fef3c7;
  font-size: 13px;
  gap: 8px;
}

.recipe-food-row:last-child {
  border-bottom: none;
}

.recipe-food-row .rf-name {
  flex: 0 0 38%;
  color: #1e293b;
  font-weight: 600;
  font-size: 13px;
  background: #fefce8;
  border: 1px solid #fde68a;
  border-radius: 6px;
  padding: 3px 10px;
  text-decoration: underline;
  text-underline-offset: 3px;
  text-decoration-color: #f59e0b;
}

.recipe-food-row .rf-portion {
  flex: 0 0 25%;
  color: #92400e;
  text-align: center;
  font-size: 12px;
  background: linear-gradient(135deg, #fffbeb 0%, #fef3c7 100%);
  border: 1px solid #fbbf24;
  border-radius: 6px;
  padding: 3px 6px;
  font-weight: 500;
  text-decoration: underline;
  text-underline-offset: 3px;
}

.recipe-food-row .rf-cal {
  flex: 0 0 27%;
  color: #b45309;
  text-align: right;
  font-weight: 600;
  font-size: 12px;
  background: linear-gradient(135deg, #fff7ed 0%, #ffedd5 100%);
  border: 1px solid #fdba74;
  border-radius: 6px;
  padding: 3px 10px;
  text-decoration: underline;
  text-underline-offset: 3px;
  text-decoration-color: #f97316;
}

.recipe-drawer .el-drawer__body .recipe-gi-tag {
  display: flex;
  align-items: center;
  gap: 6px;
}

.recipe-drawer .el-drawer__body .gi-value {
  background: linear-gradient(135deg, #ecfdf5 0%, #d1fae5 100%);
  border: 1px solid #6ee7b7;
  border-radius: 6px;
  padding: 2px 8px;
  font-size: 12px;
  color: #065f46;
}

.recipe-drawer .el-drawer__body .gi-badge.gi-low {
  background: #d1fae5;
  color: #065f46;
  border: 1px solid #6ee7b7;
}

.recipe-drawer .el-drawer__body .gi-badge.gi-mid {
  background: #fef3c7;
  color: #92400e;
  border: 1px solid #fbbf24;
}

.recipe-drawer .el-drawer__body .gi-badge.gi-high {
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fca5a5;
}

.recipe-drawer .el-drawer__body .recipe-cal-tag {
  background: linear-gradient(135deg, #fef9c3 0%, #fde68a 100%);
  border: 1px solid #fbbf24;
  border-radius: 6px;
  padding: 2px 10px;
  font-size: 12px;
}

/* 新的弹窗头部样式 */
.drawer-subtitle {
  font-size: 14px;
  color: #6b7280;
  margin-top: 4px;
  font-weight: 500;
}

/* 健康计划新样式 */
.plan-overview {
  background: linear-gradient(135deg, #ecfdf5 0%, #d1fae5 100%);
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
  border: 1px solid #6ee7b7;
}

.plan-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.stat-item {
  text-align: center;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 12px;
  backdrop-filter: blur(10px);
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #065f46;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
  font-weight: 500;
}

.plan-actions {
  display: flex;
  justify-content: center;
  margin-top: 12px;
}

.action-btn {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border: none;
  color: white;
  font-weight: 600;
  border-radius: 12px;
  padding: 10px 20px;
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.plan-days-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.plan-day-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.plan-day-card:hover {
  border-color: #10b981;
  box-shadow: 0 4px 16px rgba(16, 185, 129, 0.1);
  transform: translateY(-2px);
}

.plan-day-card.day-done {
  background: linear-gradient(135deg, #ecfdf5 0%, #d1fae5 100%);
  border-color: #6ee7b7;
}

.plan-day-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  cursor: pointer;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-bottom: 1px solid #e2e8f0;
}

.day-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.day-header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.day-label {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.day-status {
  font-size: 12px;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 20px;
}

.status-done {
  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
  color: #065f46;
  border: 1px solid #6ee7b7;
}

.status-pending {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  color: #92400e;
  border: 1px solid #fbbf24;
}

.day-toggle {
  font-size: 16px;
  color: #6b7280;
  transition: transform 0.3s ease;
}

.plan-day-body {
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
}

.plan-item-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.item-icon-container {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.diet-icon {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border: 1px solid #fbbf24;
}

.exercise-icon {
  background: linear-gradient(135deg, #dbeafe 0%, #93c5fd 100%);
  border: 1px solid #60a5fa;
}

.notes-icon {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  border: 1px solid #fca5a5;
}

.item-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.item-label {
  font-size: 12px;
  color: #6b7280;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.item-value {
  font-size: 14px;
  color: #1f2937;
  line-height: 1.5;
  font-weight: 500;
}

/* 控糖食谱新样式 */
.recipe-overview {
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
  border: 1px solid #93c5fd;
}

.recipe-summary {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 12px;
  backdrop-filter: blur(10px);
}

.summary-icon {
  font-size: 24px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border-radius: 12px;
  border: 1px solid #fbbf24;
}

.summary-content {
  display: flex;
  flex-direction: column;
}

.summary-value {
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
}

.summary-label {
  font-size: 12px;
  color: #6b7280;
  font-weight: 500;
}

.recipe-nutrition-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.nutrition-tag {
  font-size: 12px;
  font-weight: 600;
  padding: 6px 12px;
  border-radius: 20px;
  backdrop-filter: blur(10px);
}

.nutrition-tag.carbs {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  color: #92400e;
  border: 1px solid #fbbf24;
}

.nutrition-tag.protein {
  background: linear-gradient(135deg, #dbeafe 0%, #93c5fd 100%);
  color: #1e40af;
  border: 1px solid #60a5fa;
}

.nutrition-tag.fat {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  color: #991b1b;
  border: 1px solid #fca5a5;
}

.recipe-meals-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.recipe-meal-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.recipe-meal-card:hover {
  border-color: #3b82f6;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.1);
  transform: translateY(-2px);
}

.meal-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-bottom: 1px solid #e2e8f0;
}

.meal-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.meal-icon {
  font-size: 24px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #dbeafe 0%, #93c5fd 100%);
  border-radius: 12px;
  border: 1px solid #60a5fa;
}

.meal-title {
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
}

.meal-header-right {
  display: flex;
  align-items: center;
}

.meal-cal {
  font-size: 14px;
  font-weight: 600;
  color: #b45309;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  padding: 6px 12px;
  border-radius: 20px;
  border: 1px solid #fbbf24;
}

.meal-foods {
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.food-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  transition: all 0.2s ease;
}

.food-item:hover {
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border-color: #93c5fd;
}

.food-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.food-name {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}

.food-portion {
  font-size: 12px;
  color: #6b7280;
  font-weight: 500;
}

.food-cal {
  font-size: 14px;
  font-weight: 600;
  color: #b45309;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  padding: 4px 8px;
  border-radius: 8px;
  border: 1px solid #fbbf24;
}

.meal-footer {
  padding: 12px 20px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-top: 1px solid #e2e8f0;
}

.gi-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.gi-label {
  font-size: 12px;
  color: #6b7280;
  font-weight: 600;
}

.gi-value {
  font-size: 14px;
  font-weight: 700;
  padding: 4px 8px;
  border-radius: 8px;
}

.gi-value.gi-low {
  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
  color: #065f46;
  border: 1px solid #6ee7b7;
}

.gi-value.gi-mid {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  color: #92400e;
  border: 1px solid #fbbf24;
}

.gi-value.gi-high {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  color: #991b1b;
  border: 1px solid #fca5a5;
}

.gi-badge {
  font-size: 12px;
  font-weight: 600;
  padding: 4px 10px;
  border-radius: 20px;
}

.gi-badge.gi-low {
  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
  color: #065f46;
  border: 1px solid #6ee7b7;
}

.gi-badge.gi-mid {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  color: #92400e;
  border: 1px solid #fbbf24;
}

.gi-badge.gi-high {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  color: #991b1b;
  border: 1px solid #fca5a5;
}

.recipe-text-content {
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.text-content-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-bottom: 1px solid #e2e8f0;
}

.text-icon {
  font-size: 24px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #dbeafe 0%, #93c5fd 100%);
  border-radius: 12px;
  border: 1px solid #60a5fa;
}

.text-title {
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
}

.text-content-body {
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.text-line {
  font-size: 14px;
  color: #374151;
  line-height: 1.6;
  padding: 8px 12px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.text-food-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.text-food-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.tf-name {
  flex: 1;
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}

.tf-portion {
  font-size: 12px;
  color: #6b7280;
  font-weight: 500;
  background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
  padding: 4px 8px;
  border-radius: 8px;
  border: 1px solid #d1d5db;
}

.tf-cal {
  font-size: 12px;
  font-weight: 600;
  color: #b45309;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  padding: 4px 8px;
  border-radius: 8px;
  border: 1px solid #fbbf24;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .plan-stats {
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
  }
  
  .recipe-summary {
    grid-template-columns: 1fr;
  }
  
  .plan-day-header,
  .meal-card-header {
    padding: 12px 16px;
  }
  
  .plan-day-body,
  .meal-foods,
  .text-content-body {
    padding: 12px 16px;
  }
}
</style>
