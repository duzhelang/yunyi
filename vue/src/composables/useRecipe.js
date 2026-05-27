import { ref, nextTick } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { mockRecipe } from '@/data/diabetesMockData'

const CACHE_KEY = 'recipe_cache'
const CACHE_TTL = 2 * 60 * 1000

function escapeHtml(str) {
  if (!str) return ''
  return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#039;')
}

function saveCache(data) {
  try {
    sessionStorage.setItem(CACHE_KEY, JSON.stringify({ data, ts: Date.now() }))
  } catch (e) { /* ignore */ }
}

function loadCache() {
  try {
    const raw = sessionStorage.getItem(CACHE_KEY)
    if (!raw) return null
    const { data, ts } = JSON.parse(raw)
    if (Date.now() - ts > CACHE_TTL) {
      sessionStorage.removeItem(CACHE_KEY)
      return null
    }
    return data
  } catch (e) {
    return null
  }
}

export function useRecipe(selectedModel, isLoading) {
  const cached = loadCache()
  const showRecipePanel = ref(false)
  const recipeData = ref(cached || null)
  const isRecipeLoading = ref(false)
  const showRecipeDialog = ref(false)
  const dietPrefs = ['低GI', '高纤维', '低碳水', '无糖', '素食', '高蛋白']
  const selectedDietPrefs = ref([])
  const mealCount = ref('3')
  const tastePref = ref('清淡')

  function formatRecipe(data) {
    if (!data || !data.meals) return ''
    let html = ''
    for (const meal of data.meals) {
      html += `<div class="recipe-section">`
      html += `<div class="recipe-meal">${escapeHtml(meal.icon)} ${escapeHtml(meal.label)}</div>`
      html += `<div class="recipe-detail">`
      for (const food of meal.foods) {
        html += `<div class="recipe-food-row">`
        html += `<span class="food-name">${escapeHtml(food.name)}</span>`
        html += `<span class="food-portion">${escapeHtml(food.portion)}</span>`
        html += `<span class="food-cal">${escapeHtml(food.cal)}</span>`
        html += `</div>`
      }
      html += `</div>`
      html += `<div class="recipe-summary"><span>📊 ${escapeHtml(meal.gi)}</span><span>🔥 ${escapeHtml(meal.totalCal)}</span></div>`
      html += `</div>`
    }
    return html
  }

  function parseRecipeText(text) {
    const result = { meals: [] }
    const lines = text.split('\n')
    let curMeal = null
    for (const line of lines) {
      const t = line.trim()
      if (!t) continue
      const mealMatch = t.match(/【(.+?)】|^([早午晚]餐|加餐|上午|下午|晚间)/)
      if (mealMatch) {
        if (curMeal && curMeal.foods.length > 0) result.meals.push(curMeal)
        const label = mealMatch[1] || mealMatch[2]
        let icon = '🍽️'
        if (/早/.test(label)) icon = '🌅'
        else if (/午/.test(label)) icon = '☀️'
        else if (/晚/.test(label)) icon = '🌆'
        else if (/加餐|上午|下午|晚间/.test(label)) icon = '🍎'
        curMeal = { label, icon, gi: 'GI≈--', totalCal: '总热量≈--kcal', foods: [] }
        continue
      }
      if (!curMeal) continue
      if (/每餐小计|小计|GI|总热量|全天总/.test(t)) {
        const parts = t.split('|').map(s => s.trim()).filter(Boolean)
        if (parts.length >= 2) {
          curMeal.gi = parts[0].replace(/每餐小计[：:]?\s*/, '')
          curMeal.totalCal = parts[1]
        }
        result.meals.push(curMeal)
        curMeal = null
        continue
      }
      if (t.includes('|')) {
        const parts = t.split('|').map(s => s.trim()).filter(Boolean)
        if (parts.length >= 3) {
          curMeal.foods.push({ name: parts[0], portion: parts[1], cal: parts[2] })
        } else if (parts.length === 2) {
          curMeal.foods.push({ name: parts[0], portion: parts[1], cal: '' })
        }
      }
    }
    if (curMeal && curMeal.foods.length > 0) result.meals.push(curMeal)
    return result
  }

  async function generateRecipe(fromSidebar) {
    recipeData.value = { ...mockRecipe }
    isRecipeLoading.value = true
    const prefs = selectedDietPrefs.value.length > 0 ? selectedDietPrefs.value.join('、') : '低GI、高纤维'
    const meals = mealCount.value === '5' ? '一日五餐（含上午加餐和下午加餐）' : '一日三餐'
    const prompt = `请作为糖尿病营养师生成一天的控糖食谱。严格按以下格式返回，每行一道菜用"|"分隔菜品名、分量、热量kcal：
        【早餐】
        菜品名 | 分量 | 热量kcal
        （每道菜一行，继续列出）
        每餐小计：GI≈XX | 总热量≈XXXkcal
        【午餐】
        （同上格式）
        【晚餐】
        （同上格式）
        饮食偏好：${prefs}
        餐次安排：${meals}
        口味偏好：${tastePref.value}`
    try {
      const response = await request.post('/api/diabetes/chat', null, {
        params: { question: prompt, provider: selectedModel.value }
      })
      if (response && response.code === '200' && response.data) {
        const parsed = parseRecipeText(response.data)
        if (parsed && parsed.meals && parsed.meals.length >= 2) {
          recipeData.value = parsed
          saveCache(parsed)
          ElMessage.success('控糖食谱已生成')
        } else {
          ElMessage.info('已使用标准食谱方案')
        }
      } else {
        ElMessage.info('已使用标准食谱方案')
      }
    } catch (error) {
      ElMessage.info('已使用标准食谱方案')
    } finally {
      isRecipeLoading.value = false
    }
    nextTick(() => saveRecipeToRecord())
  }

  function formatRecipeForSave(recipe) {
    if (!recipe || !recipe.meals) return ''
    return recipe.meals.map(meal => {
      const foods = meal.foods.map(f => `${f.name} ${f.portion} ${f.cal}`).join(' · ')
      return `${meal.icon} ${meal.label}\n  ${foods}\n  📊 ${meal.gi} · 🔥 ${meal.totalCal}`
    }).join('\n\n')
  }

  async function saveRecipeToRecord() {
    if (!recipeData.value || !recipeData.value.meals) return
    try {
      const recipeText = formatRecipeForSave(recipeData.value)
      const mealCountVal = recipeData.value.meals.length
      const prefs = selectedDietPrefs.value.length > 0 ? selectedDietPrefs.value.join('、') : '低GI、高纤维'
      const payload = {
        recordType: 'ai_plan',
        recordDate: new Date().toISOString().replace('T', ' ').substring(0, 19),
        diagnosis: `控糖食谱 - ${mealCountVal}餐${tastePref.value}饮食方案（${prefs}）`,
        treatmentPlan: recipeText
      }
      await request.post('/api/patient-visit', payload)
    } catch (e) {
      console.error('自动保存食谱失败', e)
    }
  }

  return {
    showRecipePanel,
    recipeData,
    isRecipeLoading,
    showRecipeDialog,
    dietPrefs,
    selectedDietPrefs,
    mealCount,
    tastePref,
    formatRecipe,
    generateRecipe,
    saveRecipeToRecord
  }
}
