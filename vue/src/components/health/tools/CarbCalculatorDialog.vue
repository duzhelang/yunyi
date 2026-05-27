<template>
  <!-- ================= 碳水计数法弹窗 ================= -->
  <el-dialog
    :model-value="modelValue"
    title="碳水计数法"
    width="680px"
    custom-class="tool-dialog"
    :close-on-click-modal="false"
    :append-to-body="true"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <div class="tool-dialog-body">
      <el-tabs v-model="carbTab" class="tool-tabs">
        <el-tab-pane label="碳水计算器" name="calculator">
          <div class="carb-calculator">
            <div class="calc-form">
              <el-form label-width="100px" size="small">
                <el-form-item label="食物名称">
                  <el-select
                    :model-value="selectedFood"
                    filterable
                    placeholder="搜索或选择食物"
                    style="width:100%"
                    @update:model-value="$emit('update:selectedFood', $event)"
                  >
                    <el-option-group v-for="group in foodGroups" :key="group.label" :label="group.label">
                      <el-option v-for="food in group.foods" :key="food.name" :label="food.name" :value="food.name" />
                    </el-option-group>
                  </el-select>
                </el-form-item>
                <el-form-item label="摄入重量">
                  <el-input-number
                    :model-value="foodWeight"
                    :min="10"
                    :max="1000"
                    :step="10"
                    controls-position="right"
                    style="width:100%"
                    @update:model-value="$emit('update:foodWeight', $event)"
                  >
                    <template #suffix>g</template>
                  </el-input-number>
                </el-form-item>
                <el-form-item label="计算结果">
                  <div class="calc-result" v-if="selectedFood">
                    <div class="result-row">
                      <span class="result-label">碳水化合物</span>
                      <span class="result-value">{{ calcCarbs }} g</span>
                    </div>
                    <div class="result-row">
                      <span class="result-label">碳水份数</span>
                      <span class="result-value">{{ calcServings }} 份</span>
                    </div>
                    <div class="result-row">
                      <span class="result-label">估算热量</span>
                      <span class="result-value">{{ calcCalories }} kcal</span>
                    </div>
                    <el-progress
                      :percentage="dailyCarbPercent"
                      :stroke-width="12"
                      :text-inside="true"
                      :status="dailyCarbPercent > 100 ? 'exception' : 'success'"
                    >
                      {{ dailyCarbPercent }}% 每日推荐
                    </el-progress>
                  </div>
                  <div v-else class="calc-hint">请先选择食物</div>
                </el-form-item>
              </el-form>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="食物参考" name="reference">
          <el-collapse
            :model-value="activeFoodGroup"
            class="food-collapse"
            @update:model-value="$emit('update:activeFoodGroup', $event)"
          >
            <el-collapse-item
              v-for="group in foodGroups"
              :key="group.label"
              :title="group.label + '（' + group.foods.length + '种）'"
              :name="group.label"
            >
              <div class="food-table">
                <div class="food-table-header">
                  <span>食物</span><span>份量 (g)</span><span>碳水 (g)</span><span>碳水份数</span>
                </div>
                <div
                  v-for="food in group.foods"
                  :key="food.name"
                  class="food-table-row"
                  @click="selectFood(food)"
                >
                  <span>{{ food.name }}</span>
                  <span>{{ food.standardGram }}g</span>
                  <span>{{ food.carbPerStandard }}g</span>
                  <span>{{ food.servings }}</span>
                </div>
              </div>
            </el-collapse-item>
          </el-collapse>
        </el-tab-pane>
        <el-tab-pane label="每日建议" name="guide">
          <div class="carb-guide">
            <div class="guide-card">
              <h4>每日碳水推荐摄入量</h4>
              <div class="guide-grid">
                <div class="guide-item">
                  <span class="guide-value">130g</span>
                  <span class="guide-label">基础需要</span>
                </div>
                <div class="guide-item highlight">
                  <span class="guide-value">45-65%</span>
                  <span class="guide-label">占总热量</span>
                </div>
                <div class="guide-item">
                  <span class="guide-value">30-45g</span>
                  <span class="guide-label">每餐推荐</span>
                </div>
                <div class="guide-item">
                  <span class="guide-value">15-30g</span>
                  <span class="guide-label">每加餐推荐</span>
                </div>
              </div>
            </div>
            <div class="guide-tips">
              <h4>💡 控糖技巧</h4>
              <ul>
                <li>每餐保持一致的碳水摄入量，有助于血糖稳定</li>
                <li>优先选择低升糖指数（GI）的复合碳水</li>
                <li>1 份碳水 = 15g 碳水化合物，方便快速估算</li>
                <li>餐后 2 小时测血糖，评估该餐碳水量是否合适</li>
                <li>使用"餐盘法"：1/2 蔬菜、1/4 蛋白、1/4 碳水</li>
              </ul>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    <template #footer>
      <el-button @click="$emit('update:modelValue', false)">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
/**
 * 碳水计数法弹窗组件
 * 提供碳水计算器、食物参考表和每日建议功能
 */

// ==================== Props 定义 ====================
const props = defineProps({
  /** 弹窗显示状态 */
  modelValue: {
    type: Boolean,
    default: false
  },
  /** 食物分组数据 */
  foodGroups: {
    type: Array,
    default: () => []
  },
  /** 当前选中的食物名称 */
  selectedFood: {
    type: String,
    default: ''
  },
  /** 食物摄入重量（克） */
  foodWeight: {
    type: Number,
    default: 100
  },
  /** 展开的食物分组名称数组 */
  activeFoodGroup: {
    type: Array,
    default: () => []
  },
  /** 计算出的碳水化合物含量 */
  calcCarbs: {
    type: Number,
    default: 0
  },
  /** 计算出的碳水份数 */
  calcServings: {
    type: Number,
    default: 0
  },
  /** 计算出的估算热量 */
  calcCalories: {
    type: Number,
    default: 0
  },
  /** 占每日推荐碳水的百分比 */
  dailyCarbPercent: {
    type: Number,
    default: 0
  }
})

// ==================== 事件定义 ====================
const emit = defineEmits([
  'update:modelValue',
  'update:selectedFood',
  'update:foodWeight',
  'update:activeFoodGroup'
])

// ==================== 本地状态 ====================
import { ref } from 'vue'

/** 当前激活的标签页 */
const carbTab = ref('calculator')

// ==================== 辅助函数 ====================

/**
 * 格式化时间
 * @param {Date|string|number} date - 日期对象或时间戳
 * @param {string} format - 格式模板，如 'YYYY-MM-DD HH:mm:ss'
 * @returns {string} 格式化后的时间字符串
 */
function formatTime(date, format = 'YYYY-MM-DD HH:mm:ss') {
  const d = new Date(date)
  if (isNaN(d.getTime())) return ''
  const map = {
    'YYYY': d.getFullYear(),
    'MM': String(d.getMonth() + 1).padStart(2, '0'),
    'DD': String(d.getDate()).padStart(2, '0'),
    'HH': String(d.getHours()).padStart(2, '0'),
    'mm': String(d.getMinutes()).padStart(2, '0'),
    'ss': String(d.getSeconds()).padStart(2, '0')
  }
  let result = format
  for (const [key, val] of Object.entries(map)) {
    result = result.replace(key, val)
  }
  return result
}

/**
 * 选择食物并更新到父组件
 * @param {Object} food - 食物对象
 */
function selectFood(food) {
  emit('update:selectedFood', food.name)
  emit('update:foodWeight', food.standardGram)
}
</script>

<style scoped>
/* 碳水计算器 */
.calc-form { max-width: 460px; }
.calc-result { background: #f0f9eb; border-radius: 10px; padding: 14px 16px; }
.result-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
  border-bottom: 1px dashed rgba(0,0,0,0.06);
}
.result-row:last-of-type { border-bottom: none; }
.result-label { font-size: 13px; color: #606266; }
.result-value { font-size: 16px; font-weight: 700; color: #303133; }
.calc-hint { color: #909399; font-size: 14px; padding: 20px 0; text-align: center; }

/* 食物参考表 */
.food-collapse { margin-top: 8px; }
.food-table { width: 100%; font-size: 13px; }
.food-table-header, .food-table-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 8px;
  padding: 8px 4px;
}
.food-table-header { font-weight: 600; color: #909399; border-bottom: 1px solid #ebeef5; }
.food-table-row { cursor: pointer; transition: background 0.15s; border-radius: 4px; }
.food-table-row:hover { background: #ecf5ff; }
.food-table-row:nth-child(even) { background: #fafafa; }
.food-table-row:nth-child(even):hover { background: #ecf5ff; }

/* 碳水每日建议 */
.carb-guide { display: flex; flex-direction: column; gap: 16px; }
.guide-card { background: linear-gradient(135deg, #f0f9eb, #ecf5ff); border-radius: 12px; padding: 18px; }
.guide-card h4 { margin: 0 0 12px; font-size: 15px; color: #303133; }
.guide-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; }
.guide-item { text-align: center; padding: 12px; background: white; border-radius: 10px; }
.guide-item.highlight { background: linear-gradient(135deg, #409EFF, #337ecc); color: white; }
.guide-item.highlight .guide-label { color: rgba(255,255,255,0.8); }
.guide-value { display: block; font-size: 20px; font-weight: 800; }
.guide-label { font-size: 12px; color: #909399; margin-top: 2px; display: block; }
.guide-tips { background: #fafafa; border-radius: 12px; padding: 16px; }
.guide-tips h4 { margin: 0 0 10px; font-size: 14px; color: #303133; }
.guide-tips ul { margin: 0; padding: 0 0 0 18px; }
.guide-tips li { font-size: 13px; color: #606266; padding: 3px 0; }

/* 弹窗通用样式（非 scoped 部分由父组件或全局样式提供） */
</style>
