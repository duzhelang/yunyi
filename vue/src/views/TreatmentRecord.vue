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

      <div class="charts-section" v-if="chartData.length >= 2">
        <div class="section-header">
          <span class="section-title">健康趋势</span>
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
          <el-button v-if="!showAll && mainRecords.length >= 2" link type="primary" @click="loadAll">
            查看更多 <el-icon><ArrowDown /></el-icon>
          </el-button>
          <el-button v-if="showAll" link type="info" @click="collapseList">
            收起 <el-icon><ArrowUp /></el-icon>
          </el-button>
        </div>

        <div v-if="loading" class="loading-state">
          <el-skeleton :rows="2" animated />
        </div>

        <div v-else-if="mainRecords.length === 0" class="empty-state">
          <el-empty description="暂无就诊或自查记录" />
        </div>

        <div v-else class="record-cards">
          <div v-for="record in displayedRecords" :key="record.id" class="record-card" @click="openDetail(record)">
            <div class="card-top">
              <el-tag :type="getTypeTag(record.recordType).type" size="small" effect="dark">
                {{ getTypeTag(record.recordType).label }}
              </el-tag>
              <span class="card-date">{{ formatDate(record.recordDate) }}</span>
            </div>
            <div class="card-body">
              <div class="card-diagnosis" v-if="record.diagnosis">
                <el-icon><Document /></el-icon>
                <span>{{ truncateText(record.diagnosis, 60) }}</span>
              </div>
              <div class="card-complaint" v-if="record.chiefComplaint">
                <el-icon><ChatLineSquare /></el-icon>
                <span>{{ truncateText(record.chiefComplaint, 40) }}</span>
              </div>
              <div class="card-metrics" v-if="hasMetrics(record)">
                <span v-if="record.glucoseFasting" class="metric-item">
                  空腹 {{ record.glucoseFasting }} mmol/L
                </span>
                <span v-if="record.glucosePostprandial" class="metric-item">
                  餐后 {{ record.glucosePostprandial }} mmol/L
                </span>
                <span v-if="record.bloodPressureSystolic" class="metric-item">
                  血压 {{ record.bloodPressureSystolic }}/{{ record.bloodPressureDiastolic }}
                </span>
                <span v-if="record.bmi" class="metric-item">
                  BMI {{ record.bmi }}
                </span>
              </div>
            </div>
            <div class="card-actions" @click.stop>
              <el-button link type="primary" size="small" @click="openEditDialog(record)">
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
              <el-button link type="danger" size="small" @click="handleDelete(record)">
                <el-icon><Delete /></el-icon> 删除
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑记录' : '新增记录'"
      width="650px"
      destroy-on-close
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

    <el-drawer v-model="detailVisible" size="520px" destroy-on-close
      :class="{'plan-drawer': isDetailPlan, 'recipe-drawer': isDetailRecipe}"
    >
      <template #header>
        <div class="drawer-header" :class="isDetailPlan ? 'drawer-header-plan' : isDetailRecipe ? 'drawer-header-recipe' : ''">
          <div class="drawer-header-top">
            <el-tag :type="getTypeTag(detailRecord?.recordType)?.type" effect="dark" size="small">
              {{ getTypeTag(detailRecord?.recordType)?.label }}
            </el-tag>
            <span class="drawer-date">{{ formatDate(detailRecord?.recordDate) }}</span>
          </div>
          <div class="drawer-title-text" v-if="detailRecord?.diagnosis">{{ detailRecord.diagnosis }}</div>
        </div>
      </template>
      <template v-if="detailRecord">
        <!-- 就诊 / 自查 普通详情 -->
        <template v-if="detailRecord.recordType !== 'ai_plan'">
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
          <div class="plan-control-bar">
            <el-button size="small" :icon="weeklyCollapsed ? ArrowDown : ArrowUp" @click="toggleWeekly">
              {{ weeklyCollapsed ? '全部展开' : '全部收起' }}
            </el-button>
            <span class="plan-progress-text">{{ planCompletedCount }}/{{ planDays.length }} 天已完成</span>
          </div>
          <el-progress :percentage="planProgress" :stroke-width="8" :color="planProgressColors" />
          <div class="plan-days-list">
            <el-checkbox-group v-model="completedPlanDays">
              <div v-for="(day, idx) in planDays" :key="idx" class="plan-day-card" :class="{ 'day-done': completedPlanDays.includes(idx) }">
                <div class="plan-day-header" @click="togglePlanDay(idx)">
                  <el-checkbox :value="idx" size="large" @click.stop>
                    <span class="day-label">{{ day.label }}</span>
                  </el-checkbox>
                  <span class="day-toggle">{{ collapsedPlanDays.includes(idx) ? '▾' : '▴' }}</span>
                </div>
                <div v-show="!collapsedPlanDays.includes(idx)" class="plan-day-body">
                  <div class="plan-item-row" v-if="day.diet">
                    <span class="item-icon">🍽️</span>
                    <div><span class="item-label">饮食</span><span class="item-value">{{ day.diet }}</span></div>
                  </div>
                  <div class="plan-item-row" v-if="day.exercise">
                    <span class="item-icon">🏃</span>
                    <div><span class="item-label">运动</span><span class="item-value">{{ day.exercise }}</span></div>
                  </div>
                  <div class="plan-item-row" v-if="day.notes">
                    <span class="item-icon">⚠️</span>
                    <div><span class="item-label">注意</span><span class="item-value">{{ day.notes }}</span></div>
                  </div>
                </div>
              </div>
            </el-checkbox-group>
          </div>
        </template>

        <!-- 食谱主题视图 -->
        <template v-else-if="isDetailRecipe">
          <div class="recipe-detail-body">
            <div class="recipe-meals" v-if="parsedRecipeMeals.length > 0">
              <div v-for="(meal, idx) in parsedRecipeMeals" :key="idx" class="recipe-meal-section">
                <div class="recipe-meal-header">{{ meal.label }}</div>
                <div class="recipe-food-list">
                  <div v-for="(food, fi) in meal.foods" :key="fi" class="recipe-food-row">
                    <span class="rf-name">{{ food.name }}</span>
                    <span class="rf-portion">{{ food.portion }}</span>
                    <span class="rf-cal">{{ food.cal }}</span>
                  </div>
                </div>
                <div class="recipe-meal-footer">
                  <span class="recipe-gi-tag">
                    <span class="gi-value">{{ meal.gi }}</span>
                    <span class="gi-badge" :class="giBadgeClass(meal.giText)">{{ meal.giText }}</span>
                  </span>
                  <span class="recipe-cal-tag">{{ meal.totalCal }}</span>
                </div>
              </div>
            </div>
            <div v-else class="plan-content-lines">
              <template v-for="(line, li) in parsedPlanLines" :key="li">
                <div v-if="line.type === 'text'" class="plan-content-line">
                  {{ line.content }}
                </div>
                <div v-else class="plan-content-food-group">
                  <div v-for="(food, fi) in line.items" :key="fi" class="plan-food-item">
                    <span class="pf-name">{{ food.name }}</span>
                    <span class="pf-portion">{{ food.portion }}</span>
                    <span class="pf-cal">{{ food.cal }}</span>
                  </div>
                </div>
              </template>
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
import { Plus, ArrowDown, ArrowUp, Edit, Delete, Document, ChatLineSquare } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import request from '@/utils/request'

const DEMO_RECORDS = [
  { id: 9001, recordType: 'visit', recordDate: '2026-05-02 14:00:00', hospital: '东方医院内分泌科', doctorName: '张伟主任', chiefComplaint: '近期血糖波动较大，偶有餐后偏高', diagnosis: '2型糖尿病，需调整餐后血糖控制', treatmentPlan: '调整阿卡波糖剂量，增加餐后散步习惯，记录每日血糖日记', glucoseFasting: 6.4, glucosePostprandial: 10.1, hba1c: 6.2, bloodPressureSystolic: 130, bloodPressureDiastolic: 83, weight: 73.8, bmi: 24.6, isDeleted: 0 },
  { id: 9002, recordType: 'self_check', recordDate: '2026-05-01 08:00:00', hospital: null, doctorName: null, chiefComplaint: '五一假期饮食自查', diagnosis: '假期饮食偏甜，餐后血糖略升', treatmentPlan: '减少甜食摄入，增加餐后散步', glucoseFasting: 6.3, glucosePostprandial: 10.2, hba1c: null, bloodPressureSystolic: 130, bloodPressureDiastolic: 84, weight: 75.0, bmi: 25.1, isDeleted: 0 },
  { id: 9003, recordType: 'visit', recordDate: '2026-04-28 09:00:00', hospital: '东方医院眼科', doctorName: '王明主治', chiefComplaint: '视力偶有模糊，糖尿病定期眼底检查', diagnosis: '糖尿病视网膜病变早期，眼底微血管瘤', treatmentPlan: '眼底激光光凝治疗，加强血糖控制，3个月后复查眼底', glucoseFasting: 6.2, glucosePostprandial: 9.0, hba1c: 6.1, bloodPressureSystolic: 128, bloodPressureDiastolic: 82, weight: 74.0, bmi: 24.7, isDeleted: 0 },
  { id: 9004, recordType: 'ai_plan', recordDate: '2026-04-20 09:00:00', hospital: null, doctorName: null, chiefComplaint: null, diagnosis: '基于当前健康数据生成的综合管理计划', treatmentPlan: '第1天: 饮食-燕麦+蓝莓+核桃早餐/糙米饭+清蒸鲈鱼+西兰花午餐/杂粮粥+凉拌木耳晚餐；运动-快走40分钟\n第2天: 饮食-全麦面包+水煮蛋+无糖豆浆早餐/红薯+鸡胸肉+生菜午餐/小米粥+清炒菠菜晚餐；运动-瑜伽60分钟\n第3天: 饮食-全麦吐司+牛油果+鸡蛋早餐/藜麦沙拉+鸡胸肉午餐/番茄蛋花汤+杂粮馒头晚餐；运动-力量训练+有氧30分钟\n第4天: 饮食-紫薯+鸡蛋+脱脂奶早餐/荞麦面+虾仁+青菜午餐/豆腐汤+蒸南瓜晚餐；运动-快走50分钟\n第5天: 饮食-希腊酸奶+坚果+奇亚籽早餐/荞麦面+虾仁+蔬菜午餐/清炒时蔬+豆腐晚餐；运动-游泳40分钟\n第6天: 饮食-黑米粥+水煮蛋+凉拌黄瓜早餐/糙米+牛肉+西兰花午餐/冬瓜排骨汤+杂粮饭晚餐；运动-骑行30分钟\n第7天: 饮食-正常均衡饮食三餐；运动-户外骑行60分钟', glucoseFasting: null, glucosePostprandial: null, hba1c: null, bloodPressureSystolic: null, bloodPressureDiastolic: null, weight: null, bmi: null, isDeleted: 0 },
  { id: 9005, recordType: 'visit', recordDate: '2026-04-10 10:00:00', hospital: '东方医院内分泌科', doctorName: '张伟主任', chiefComplaint: '春季体检复查', diagnosis: '2型糖尿病，血糖控制良好', treatmentPlan: '继续维持当前方案，注意季节变化对血糖的影响，适当调整运动量', glucoseFasting: 5.8, glucosePostprandial: 8.0, hba1c: 5.8, bloodPressureSystolic: 126, bloodPressureDiastolic: 80, weight: 74.5, bmi: 24.9, isDeleted: 0 },
  { id: 9006, recordType: 'ai_plan', recordDate: '2026-05-05 08:00:00', hospital: null, doctorName: null, chiefComplaint: null, diagnosis: '控糖食谱 - 一日三餐低GI均衡饮食方案', treatmentPlan: '【早餐】🌅 全麦面包 2片(约160kcal) · 水煮蛋 1个(约70kcal) · 无糖豆浆 200ml(约60kcal) · 凉拌黄瓜 100g(约30kcal)\n【午餐】☀️ 杂粮饭 100g(约130kcal) · 清蒸鲈鱼 120g(约120kcal) · 蒜蓉西兰花 150g(约55kcal) · 番茄蛋花汤 1碗(约50kcal)\n【晚餐】🌆 荞麦面 80g(约110kcal) · 鸡胸肉炒青椒 120g(约130kcal) · 清炒生菜 150g(约30kcal) · 凉拌海带丝 80g(约20kcal)\n【营养总计】GI≈48 · 总热量≈1430kcal · 碳水150g · 蛋白80g · 脂肪35g', glucoseFasting: null, glucosePostprandial: null, hba1c: null, bloodPressureSystolic: null, bloodPressureDiastolic: null, weight: null, bmi: null, isDeleted: 0 }
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

const loading = ref(false)
const submitting = ref(false)
const showAll = ref(false)
const records = ref([])
const chartData = ref([])

const mainRecords = computed(() => records.value.filter(r => r.recordType === 'visit' || r.recordType === 'self_check'))
const displayedRecords = computed(() => {
  const all = mainRecords.value
  return showAll.value ? all : all.slice(0, 2)
})
const healthPlanRecords = computed(() => records.value.filter(r => r.recordType === 'ai_plan' && r.diagnosis && !r.diagnosis.includes('控糖食谱')))
const recipeRecords = computed(() => records.value.filter(r => r.recordType === 'ai_plan' && r.diagnosis && r.diagnosis.includes('控糖食谱')))

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const detailVisible = ref(false)
const detailRecord = ref(null)

const isDetailPlan = computed(() => detailRecord.value?.recordType === 'ai_plan' && detailRecord.value?.diagnosis && !detailRecord.value.diagnosis.includes('控糖食谱'))
const isDetailRecipe = computed(() => detailRecord.value?.recordType === 'ai_plan' && detailRecord.value?.diagnosis && detailRecord.value.diagnosis.includes('控糖食谱'))
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
      curMeal = { label: `${icon} ${label}`, gi: '', giText: '', totalCal: '', totalNutri: '', foods: [] }
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
  recordDate: [{ required: true, message: '请选择记录日期', trigger: 'change' }]
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
  try {
    const res = await request.get('/api/patient-visit/my', { params: { limit: 20 } })
    if (res.code === '200' && res.data && res.data.length > 0) {
      records.value = res.data
    } else {
      records.value = DEMO_RECORDS
    }
  } catch (e) {
    console.error('加载记录失败，使用演示数据', e)
    records.value = DEMO_RECORDS
  } finally {
    loading.value = false
  }
}

async function fetchChartData() {
  try {
    const res = await request.get('/api/patient-visit/my/chart-data')
    if (res.code === '200' && res.data && res.data.length > 0) {
      chartData.value = res.data
    } else {
      chartData.value = DEMO_CHART_DATA
    }
  } catch (e) {
    console.error('加载图表数据失败，使用演示数据', e)
    chartData.value = DEMO_CHART_DATA
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
    let res
    if (isEdit.value) {
      res = await request.put(`/api/patient-visit/${payload.id}`, payload)
    } else {
      res = await request.post('/api/patient-visit', payload)
    }
    if (res.code === '200') {
      ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
      dialogVisible.value = false
      fetchRecords()
      fetchChartData()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

function handleDelete(record) {
  ElMessageBox.confirm('确定要删除该记录吗？', '提示', { type: 'warning' }).then(async () => {
    try {
      const res = await request.delete(`/api/patient-visit/${record.id}`)
      if (res.code === '200') {
        ElMessage.success('删除成功')
        fetchRecords()
        fetchChartData()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (e) {
      ElMessage.error('删除失败')
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
    tooltip: { trigger: 'axis' },
    legend: { data: ['空腹血糖', '餐后血糖'], top: 5, left: 'center' },
    grid: { top: 40, right: 20, bottom: 65, left: 55 },
    xAxis: { type: 'category', data: dates, axisLabel: { rotate: 30, fontSize: 11 } },
    yAxis: {
      type: 'value', name: 'mmol/L',
      axisLabel: { fontSize: 11 }
    },
    visualMap: {
      show: false, pieces: [
        { lte: 6.1, color: '#67c23a' },
        { gt: 6.1, lte: 7.8, color: '#e6a23c' },
        { gt: 7.8, color: '#f56c6c' }
      ],
      seriesIndex: 0
    },
    series: [
      { name: '空腹血糖', type: 'line', data: fasting, smooth: true, connectNulls: true, symbolSize: 6, lineStyle: { width: 2 } },
      { name: '餐后血糖', type: 'line', data: postprandial, smooth: true, connectNulls: true, symbolSize: 6, lineStyle: { type: 'dashed', width: 2 } }
    ]
  })
}

function renderBPChart() {
  if (!bpChartRef.value) return
  if (bpChart) bpChart.dispose()
  bpChart = echarts.init(bpChartRef.value)

  const dates = chartData.value.filter(r => r.bloodPressureSystolic).map(r => formatDate(r.recordDate))
  const systolic = chartData.value.filter(r => r.bloodPressureSystolic).map(r => r.bloodPressureSystolic)
  const diastolic = chartData.value.filter(r => r.bloodPressureSystolic).map(r => r.bloodPressureDiastolic)

  bpChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['收缩压', '舒张压'], top: 5, left: 'center' },
    grid: { top: 40, right: 20, bottom: 65, left: 55 },
    xAxis: { type: 'category', data: dates, axisLabel: { rotate: 30, fontSize: 11 } },
    yAxis: { type: 'value', name: 'mmHg' },
    series: [
      { name: '收缩压', type: 'line', data: systolic, smooth: true, symbolSize: 6, lineStyle: { width: 2 },
        markArea: { silent: true, data: [[{ yAxis: 90, itemStyle: { color: 'rgba(103,194,58,0.1)' } }, { yAxis: 140 }]] } },
      { name: '舒张压', type: 'line', data: diastolic, smooth: true, symbolSize: 6, lineStyle: { type: 'dashed', width: 2 },
        markArea: { silent: true, data: [[{ yAxis: 60, itemStyle: { color: 'rgba(103,194,58,0.1)' } }, { yAxis: 90 }]] } }
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
    tooltip: { trigger: 'axis' },
    legend: { data: ['体重', 'BMI'], top: 5, left: 'center' },
    grid: { top: 40, right: 55, bottom: 65, left: 55 },
    xAxis: { type: 'category', data: dates, axisLabel: { rotate: 30, fontSize: 11 } },
    yAxis: [
      { type: 'value', name: 'kg', position: 'left' },
      { type: 'value', name: 'BMI', position: 'right' }
    ],
    series: [
      { name: '体重', type: 'line', data: weights, smooth: true, symbolSize: 6, yAxisIndex: 0, lineStyle: { width: 2 } },
      { name: 'BMI', type: 'line', data: bmis, smooth: true, symbolSize: 6, yAxisIndex: 1, lineStyle: { type: 'dashed', width: 2 } }
    ]
  })
}

function renderAllCharts() {
  nextTick(() => {
    if (chartData.value.length >= 2) {
      renderGlucoseChart()
      if (hasBPData.value) renderBPChart()
      if (hasWeightData.value) renderWeightChart()
    }
  })
}

function handleResize() {
  glucoseChart?.resize()
  bpChart?.resize()
  weightChart?.resize()
}

watch(chartData, () => { renderAllCharts() }, { deep: true })

onMounted(async () => {
  await Promise.all([fetchRecords(), fetchChartData()])
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  glucoseChart?.dispose()
  bpChart?.dispose()
  weightChart?.dispose()
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
  cursor: pointer;
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
  justify-content: flex-end;
  gap: 8px;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #f0f2f5;
}

.charts-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
}

.chart-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 12px;
}

.chart-title {
  margin: 0 0 8px;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.chart-box {
  width: 100%;
  height: 280px;
}

.plan-content {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.6;
}

/* ===== 弹窗主题样式 ===== */
.drawer-header {
  padding: 4px 0;
}

.drawer-header-plan {
  background: linear-gradient(135deg, #c3eacf 0%, #d6efe3 50%, #c4dae8 100%);
  margin: -20px -20px 16px -20px;
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

@media (min-width: 900px) {
  .charts-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>

<style>
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

.el-drawer .recipe-drawer__body .recipe-gi-tag {
  display: flex;
  align-items: center;
  gap: 6px;
}

.el-drawer .recipe-drawer__body .gi-value {
  background: linear-gradient(135deg, #ecfdf5 0%, #d1fae5 100%);
  border: 1px solid #6ee7b7;
  border-radius: 6px;
  padding: 2px 8px;
  font-size: 12px;
  color: #065f46;
}

.el-drawer .recipe-drawer__body .gi-badge.gi-low {
  background: #d1fae5;
  color: #065f46;
  border: 1px solid #6ee7b7;
}

.el-drawer .recipe-drawer__body .gi-badge.gi-mid {
  background: #fef3c7;
  color: #92400e;
  border: 1px solid #fbbf24;
}

.el-drawer .recipe-drawer__body .gi-badge.gi-high {
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fca5a5;
}

.el-drawer .recipe-drawer__body .recipe-cal-tag {
  background: linear-gradient(135deg, #fef9c3 0%, #fde68a 100%);
  border: 1px solid #fbbf24;
  border-radius: 6px;
  padding: 2px 10px;
  font-size: 12px;
}
</style>
