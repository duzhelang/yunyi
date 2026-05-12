<template>
  <div class="health-hub-pro">
    <WelcomeBanner />

    <el-row :gutter="20">
      <el-col :xs="24" :lg="16">
        <HealthFormCard
          v-model:form="form"
          v-model:temp="temp"
          :bmi-feedback="bmiFeedback"
          :glucose-feedback="glucoseFeedback"
          :bp-feedback="bpFeedback"
          :saving="saving"
          :predicting="predicting"
          :submitting="submitting"
          :file-list="fileList"
          @save="saveOnly"
          @save-and-predict="saveAndPredict"
          @quick-predict="quickPredict"
          @submit-doctor="submitToDoctor(null)"
          @reset="resetForm"
          @calc-bmi="calcBMI"
          @evaluate-glucose="evaluateGlucose"
          @evaluate-bp="evaluateBP"
          @file-change="handleFileChange"
          @open-dpf-calc="dpfDialogVisible = true"
          @open-dpf-info="dpfInfoDialogVisible = true"
        />
        <HealthAdviceCard :advice="currentAdvice" />
      </el-col>

      <el-col :xs="24" :lg="8">
        <HealthChartCard
          :history-list="historyList"
          v-model:chart-mode="chartMode"
          :selected-history-id="selectedHistoryId"
        />
        <HealthHistoryCard
          :history-list="historyList"
          :loading="loadingHistory"
          :selected-history-id="selectedHistoryId"
          :submitting="submitting"
          :submitting-id="submittingId"
          :deleting-id="deletingId"
          :status-map="STATUS_MAP"
          @select="selectHistory"
          @load-to-form="handleLoadHistoryToForm"
          @submit-doctor="submitToDoctor"
          @delete="deleteHistory"
        />
        <HealthToolCard @open-tool="handleOpenTool" />
        <DailyCheckinCard
          :check-list="checkList"
          :progress-percent="progressPercent"
          @toggle="toggleCheck"
        />
      </el-col>
    </el-row>

    <DpfCalculator v-model="store.diabetesPedigreeFunction" v-model:visible="dpfDialogVisible" />
    <DpfInfoDialog v-model="dpfInfoDialogVisible" />
    <EmergencyToolDialog
      v-model="emergencyDialogVisible"
      :emergency-severity="emergencySeverity"
      :emergency-step="emergencyStep"
      :emergency-timer-running="emergencyTimerRunning"
      :emergency-countdown="emergencyCountdown"
      @update:emergency-severity="emergencySeverity = $event"
      @update:emergency-step="emergencyStep = $event"
      @start-timer="startEmergencyTimer"
      @reset="resetEmergency"
    />
    <CarbCalculatorDialog
      v-model="carbDialogVisible"
      :food-groups="foodGroups"
      :selected-food="selectedFood"
      :food-weight="foodWeight"
      :active-food-group="activeFoodGroup"
      :calc-carbs="calcCarbs"
      :calc-servings="calcServings"
      :calc-calories="calcCalories"
      :daily-carb-percent="dailyCarbPercent"
      @update:selected-food="selectedFood = $event"
      @update:food-weight="foodWeight = $event"
      @update:active-food-group="activeFoodGroup = $event"
    />
    <FootCareDialog
      v-model="footDialogVisible"
      :foot-checklist="footChecklist"
      :foot-check-percent="footCheckPercent"
      @toggle-check="toggleFootCheck"
    />
    <ResultDialog
      v-model="resultDialogVisible"
      :store-data="resultStoreData"
      @re-evaluate="resetForm"
    />
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useHealthStore } from '@/store/healthStore'
import { useChartStore } from '@/store/chartStore'
import { useDraftPersistence } from '@/composables/useDraftPersistence'
import { useHealthForm } from '@/composables/useHealthForm'
import { useHealthHistory } from '@/composables/useHealthHistory'
import { useHealthTools } from '@/composables/useHealthTools'
import { useDailyCheckin } from '@/composables/useDailyCheckin'

import WelcomeBanner from '@/components/health/WelcomeBanner.vue'
import HealthFormCard from '@/components/health/HealthFormCard.vue'
import HealthAdviceCard from '@/components/health/HealthAdviceCard.vue'
import HealthChartCard from '@/components/health/HealthChartCard.vue'
import HealthHistoryCard from '@/components/health/HealthHistoryCard.vue'
import HealthToolCard from '@/components/health/HealthToolCard.vue'
import DailyCheckinCard from '@/components/health/DailyCheckinCard.vue'
import DpfCalculator from '@/components/DpfCalculator.vue'
import DpfInfoDialog from '@/components/health/DpfInfoDialog.vue'
import EmergencyToolDialog from '@/components/health/tools/EmergencyToolDialog.vue'
import CarbCalculatorDialog from '@/components/health/tools/CarbCalculatorDialog.vue'
import FootCareDialog from '@/components/health/tools/FootCareDialog.vue'
import ResultDialog from '@/components/health/ResultDialog.vue'

const store = useHealthStore()
const chartStore = useChartStore()
const { startAutoSave, stopAutoSave, loadDraft } = useDraftPersistence()

const resultDialogVisible = ref(false)
const dpfInfoDialogVisible = ref(false)

const chartMode = ref('glucose')

const {
  form, temp, bmiFeedback, glucoseFeedback, bpFeedback,
  saving, predicting, fileList, currentAdvice,
  calcBMI, evaluateGlucose, evaluateBP, handleFileChange,
  validate, generateAdvice, loadAdvice,
  saveOnly, saveAndPredict, quickPredict, resetForm
} = useHealthForm({
  resultDialogVisible,
  loadHistory: loadHistoryWrapper,
  chartStore
})

const {
  historyList, loadingHistory, selectedHistoryId,
  submitting, submittingId, deletingId,
  STATUS_MAP,
  loadHistory, selectHistory, loadHistoryToForm, deleteHistory
} = useHealthHistory()

const {
  emergencyDialogVisible, emergencySeverity, emergencyStep,
  emergencyTimerRunning, emergencyCountdown,
  carbDialogVisible, selectedFood, foodWeight, activeFoodGroup,
  footDialogVisible, footChecklist,
  dpfDialogVisible,
  foodGroups,
  calcCarbs, calcServings, calcCalories, dailyCarbPercent,
  footCheckPercent,
  openTool, startEmergencyTimer
} = useHealthTools()

const {
  checkList, progressPercent, toggleCheck
} = useDailyCheckin()

async function loadHistoryWrapper() {
  await loadHistory()
}

function handleLoadHistoryToForm(item) {
  loadHistoryToForm(item, store, temp, evaluateGlucose, evaluateBP, generateAdvice)
}

function handleOpenTool(type) {
  openTool(type)
}

function toggleFootCheck(idx) {
  footChecklist.value[idx].done = !footChecklist.value[idx].done
}

function resetEmergency() {
  emergencySeverity.value = 'mild'
  emergencyStep.value = 1
}

async function submitToDoctor(existingId) {
  if (!validate()) return
  let targetId = existingId
  if (!targetId) {
    targetId = await saveOnly()
    if (!targetId) return
  }
  const finalId = Number(targetId)
  try {
    await ElMessageBox.confirm(`确认生成 CSV 并发送给诊断员？\n档案 ID: ${finalId}`, '确认发送', { type: 'warning' })
  } catch { return }

  submitting.value = !existingId
  submittingId.value = existingId
  try {
    const res = await request.post(`/api/health-profile/send-to-doctor/${finalId}`, {})
    const ok = !res || res.code === 200 || res.success === true
    ElMessage[ok ? 'success' : 'warning'](ok ? 'CSV 已生成，诊断员已通知' : (res.msg || '提交完成'))
    await loadHistory()
  } catch (e) {
    ElMessage.error('提交失败：' + (e.response?.data?.msg || e.message))
  } finally {
    submitting.value = false
    submittingId.value = null
  }
}

const resultStoreData = computed(() => ({
  riskLevel: store.riskLevel,
  riskProbability: store.riskProbability,
  confidenceInterval: store.confidenceInterval,
  age: store.age,
  bmi: store.bmi,
  glucose: store.glucose,
  bloodPressure: store.bloodPressure,
  insulin: store.insulin,
  diabetesPedigreeFunction: store.diabetesPedigreeFunction,
  aiAdvice: store.aiAdvice
}))

onMounted(() => {
  loadDraft()
  loadAdvice()
  startAutoSave()
  loadHistory()
  nextTick(() => {
    calcBMI()
    evaluateGlucose(store.glucose)
    evaluateBP(store.bloodPressure)
  })
})

onUnmounted(() => {
  stopAutoSave()
})
</script>

<style scoped>
.health-hub-pro {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ed 100%);
  min-height: 100vh;
}

@media (max-width: 768px) {
  .health-hub-pro {
    padding: 12px;
  }
}
</style>
