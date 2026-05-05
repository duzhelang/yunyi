import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

const DRAFT_KEY = 'health_draft'

export const useHealthStore = defineStore('health', () => {
  const age = ref(30)
  const height = ref(170)
  const weight = ref(65)
  const glucose = ref(90)
  const bloodPressure = ref(120)
  const insulin = ref(50)
  const skinThickness = ref(20)
  const pregnancies = ref(0)
  const diabetesPedigreeFunction = ref(0.5)
  const gender = ref('女')
  const symptoms = ref('')
  const exerciseFrequency = ref('1')
  const dietHabit = ref('1')
  const smoking = ref('不吸烟')
  const drinking = ref('不饮酒')

  const bmi = computed(() => {
    if (!height.value || !weight.value) return 0
    return +((weight.value) / ((height.value / 100) ** 2)).toFixed(1)
  })

  const riskLevel = ref('')
  const riskProbability = ref(0)
  const predictionId = ref(null)
  const aiAdvice = ref('')
  const chartsData = ref(null)
  const featureImportance = ref([])
  const featureNames = ref([])
  const percentiles = ref({})
  const similarCases = ref(null)
  const confidenceInterval = ref([0, 0])
  const predictionTime = ref('')
  const adviceData = ref(null)

  const hasPrediction = computed(() => riskProbability.value > 0)

  function loadFromDraft() {
    try {
      const saved = localStorage.getItem(DRAFT_KEY)
      if (saved) {
        const data = JSON.parse(saved)
        age.value = data.age ?? 30
        height.value = data.height ?? 170
        weight.value = data.weight ?? 65
        glucose.value = data.glucose ?? 90
        bloodPressure.value = data.bloodPressure ?? 120
        insulin.value = data.insulin ?? 50
        skinThickness.value = data.skinThickness ?? 20
        pregnancies.value = data.pregnancies ?? 0
        diabetesPedigreeFunction.value = data.diabetesPedigreeFunction ?? 0.5
        gender.value = data.gender ?? '女'
        symptoms.value = data.symptoms ?? ''
        exerciseFrequency.value = data.exerciseFrequency ?? '1'
        dietHabit.value = data.dietHabit ?? '1'
        smoking.value = data.smoking ?? '不吸烟'
        drinking.value = data.drinking ?? '不饮酒'
        adviceData.value = data.adviceData ?? null
      }
    } catch (e) {
      console.warn('读取草稿失败', e)
    }
  }

  function saveToDraft() {
    try {
      const data = {
        age: age.value, height: height.value, weight: weight.value,
        glucose: glucose.value, bloodPressure: bloodPressure.value,
        insulin: insulin.value, skinThickness: skinThickness.value,
        pregnancies: pregnancies.value,
        diabetesPedigreeFunction: diabetesPedigreeFunction.value,
        gender: gender.value, symptoms: symptoms.value,
        exerciseFrequency: exerciseFrequency.value,
        dietHabit: dietHabit.value, smoking: smoking.value,
        drinking: drinking.value,
        adviceData: adviceData.value
      }
      localStorage.setItem(DRAFT_KEY, JSON.stringify(data))
    } catch (e) {
      console.warn('保存草稿失败', e)
    }
  }

  function clearDraft() {
    localStorage.removeItem(DRAFT_KEY)
  }

  function setPredictionResult(result) {
    riskLevel.value = result.risk_level || ''
    riskProbability.value = result.probability || 0
    confidenceInterval.value = result.confidence_interval || [0, 0]
    featureImportance.value = result.feature_importance || []
    featureNames.value = result.feature_names || []
    percentiles.value = result.percentiles || {}
    similarCases.value = result.similar_cases || null
    chartsData.value = result.charts || null
    predictionTime.value = result.time || ''
    if (result.ai_advice) {
      aiAdvice.value = result.ai_advice
    }
  }

  function resetAll() {
    age.value = 30; height.value = 170; weight.value = 65
    glucose.value = 90; bloodPressure.value = 120
    insulin.value = 50; skinThickness.value = 20
    pregnancies.value = 0; diabetesPedigreeFunction.value = 0.5
    gender.value = '女'; symptoms.value = ''
    exerciseFrequency.value = '1'; dietHabit.value = '1'
    smoking.value = '不吸烟'; drinking.value = '不饮酒'
    riskLevel.value = ''; riskProbability.value = 0
    chartsData.value = null; aiAdvice.value = ''
    predictionId.value = null; adviceData.value = null
    clearDraft()
  }

  function toFeatures() {
    return {
      pregnancies: pregnancies.value,
      glucose: glucose.value,
      bloodPressure: bloodPressure.value,
      skinThickness: skinThickness.value,
      insulin: insulin.value,
      bmi: bmi.value,
      diabetesPedigreeFunction: diabetesPedigreeFunction.value,
      age: age.value
    }
  }

  function toSavePayload() {
    return {
      age: age.value, height: height.value, weight: weight.value,
      glucose: glucose.value, bloodPressure: bloodPressure.value,
      insulin: insulin.value, skinThickness: skinThickness.value,
      pregnancies: pregnancies.value,
      diabetesPedigreeFunction: diabetesPedigreeFunction.value,
      gender: gender.value, symptoms: symptoms.value,
      exerciseFrequency: exerciseFrequency.value,
      dietHabit: dietHabit.value, smoking: smoking.value,
      drinking: drinking.value,
      bmi: bmi.value
    }
  }

  return {
    age, height, weight, bmi, glucose, bloodPressure, insulin,
    skinThickness, pregnancies, diabetesPedigreeFunction,
    gender, symptoms, exerciseFrequency, dietHabit, smoking, drinking,
    riskLevel, riskProbability, predictionId, aiAdvice,
    chartsData, featureImportance, featureNames,
    percentiles, similarCases, confidenceInterval, predictionTime,
    adviceData,
    hasPrediction,
    loadFromDraft, saveToDraft, clearDraft,
    setPredictionResult, resetAll,
    toFeatures, toSavePayload
  }
})
