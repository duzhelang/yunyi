<template>
  <el-dialog
    :model-value="modelValue"
    title="足部护理指南"
    width="680px"
    custom-class="tool-dialog"
    :close-on-click-modal="false"
    :append-to-body="true"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <div class="tool-dialog-body">
      <el-alert
        title="糖尿病足是严重并发症，每日足部检查至关重要！"
        type="warning"
        :closable="false"
        show-icon
        class="tool-alert"
      />
      <el-tabs v-model="footTab" class="tool-tabs">
        <el-tab-pane label="每日自检" name="checklist">
          <p class="section-desc">每天完成以下检查，预防足部问题</p>
          <div class="foot-checklist">
            <div
              v-for="(item, idx) in footChecklist"
              :key="idx"
              class="checklist-item"
              :class="{ done: item.done }"
              @click="handleToggle(idx)"
            >
              <el-icon class="check-icon">
                <CircleCheck />
              </el-icon>
              <div class="checklist-content">
                <strong>{{ item.title }}</strong>
                <p>{{ item.desc }}</p>
              </div>
            </div>
          </div>
          <el-progress
            :percentage="footCheckPercent"
            :stroke-width="14"
            :text-inside="true"
            :status="footCheckPercent === 100 ? 'success' : ''"
            class="foot-progress"
          >
            {{ footCheckPercent }}% 已完成
          </el-progress>
        </el-tab-pane>
        <el-tab-pane label="护理步骤" name="care">
          <div class="care-steps">
            <div v-for="(step, idx) in footCareSteps" :key="idx" class="care-step-item">
              <div class="care-step-header">
                <span class="care-step-num">{{ idx + 1 }}</span>
                <h4>{{ step.title }}</h4>
              </div>
              <p>{{ step.content }}</p>
              <div v-if="step.tips" class="care-tips">
                <span class="tip-label">小贴士：</span>{{ step.tips }}
              </div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="警示信号" name="warning">
          <div class="warning-grid">
            <div class="warning-card">
              <span class="warning-icon">🩹</span>
              <h4>伤口不愈合</h4>
              <p>超过 3 天不愈合的小伤口应及时就医</p>
            </div>
            <div class="warning-card">
              <span class="warning-icon">🔴</span>
              <h4>红肿热痛</h4>
              <p>感染迹象，需要专业处理</p>
            </div>
            <div class="warning-card">
              <span class="warning-icon">💧</span>
              <h4>水泡/溃疡</h4>
              <p>不要自行处理，立即就医</p>
            </div>
            <div class="warning-card">
              <span class="warning-icon">🌡️</span>
              <h4>足部发热</h4>
              <p>局部温度升高可能是感染</p>
            </div>
            <div class="warning-card">
              <span class="warning-icon">🦶</span>
              <h4>足部变形</h4>
              <p>跖骨头突出、锤状趾等</p>
            </div>
            <div class="warning-card">
              <span class="warning-icon">🫨</span>
              <h4>感觉异常</h4>
              <p>麻木、刺痛、蚁走感</p>
            </div>
          </div>
          <el-alert
            title="发现以上任何症状，请立即就诊内分泌科或足病门诊"
            type="error"
            :closable="false"
            show-icon
            class="tool-alert"
          />
        </el-tab-pane>
      </el-tabs>
    </div>
    <template #footer>
      <el-button @click="$emit('update:modelValue', false)">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { CircleCheck } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  footChecklist: {
    type: Array,
    required: true
  },
  footCheckPercent: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['update:modelValue', 'update:footChecklist', 'toggle-check'])

/** 当前激活的标签页 */
const footTab = ref('checklist')

/** 护理步骤静态数据 */
const footCareSteps = [
  { title: '温水洗脚', content: '每天用温水（不超过37℃）洗脚，使用温和的肥皂。洗脚前务必用手肘或温度计测试水温。洗后用柔软毛巾轻轻擦干，特别注意脚趾缝之间。', tips: '不要用热水袋或电热毯暖脚，以免烫伤' },
  { title: '彻底检查', content: '在良好光线下仔细检查双脚各处：足底、足背、脚趾缝、指甲边缘。使用镜子检查足底，或请家人帮忙。', tips: '如果视力不好，建议让家人每周至少帮忙检查一次' },
  { title: '保湿护理', content: '在足部皮肤还微湿时涂抹保湿霜（不含香料），保持皮肤柔软防止干裂。注意：不要涂抹在脚趾缝之间。', tips: '脚趾缝之间保持干燥，可用玉米淀粉撒在脚趾间吸湿' },
  { title: '修剪指甲', content: '洗脚后指甲变软时修剪。沿指甲自然弧度修剪，不要剪太短，用指甲锉修平边缘。', tips: '视力不好或指甲增厚时，请足病医生处理，不要自己修剪' },
  { title: '选择鞋袜', content: '穿合脚的软底鞋，避免穿凉鞋、高跟鞋或尖头鞋。每天换干净、无缝的棉袜，袜子不要有松紧带。', tips: '新鞋先穿1-2小时逐渐适应，每次穿前检查鞋内有无异物' }
]

/** 切换某项检查的完成状态 */
function handleToggle(idx) {
  emit('toggle-check', idx)
}
</script>

<style scoped>
/* ===== 工具弹窗通用样式（与父页面保持一致） ===== */
.tool-dialog-body {
  max-height: 65vh;
  overflow-y: auto;
  padding: 0;
}
.tool-alert {
  margin-bottom: 16px;
}
.section-desc {
  color: #606266;
  font-size: 13px;
  margin: 0 0 14px;
}

/* 足部护理 - 每日自检 */
.foot-checklist {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 14px;
}
.checklist-item {
  display: flex;
  gap: 10px;
  align-items: flex-start;
  padding: 12px 14px;
  background: #fafafa;
  border-radius: 10px;
  border: 1px solid #ebeef5;
  cursor: pointer;
  transition: all 0.2s;
}
.checklist-item:hover { border-color: #409EFF; }
.checklist-item.done { background: #f0f9eb; border-color: #67C23A; }
.checklist-item .check-icon { margin-top: 2px; color: #c0c4cc; font-size: 18px; }
.checklist-item.done .check-icon { color: #67C23A; }
.checklist-content strong { display: block; font-size: 14px; color: #303133; }
.checklist-content p { margin: 2px 0 0; font-size: 12px; color: #909399; }
.checklist-item.done .checklist-content strong {
  color: #67C23A;
  text-decoration: line-through;
}
.foot-progress { margin-top: 4px; }

/* 足部护理 - 护理步骤 */
.care-steps { display: flex; flex-direction: column; gap: 14px; }
.care-step-item {
  padding: 16px;
  background: #fafafa;
  border-radius: 12px;
  border: 1px solid #ebeef5;
}
.care-step-header { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.care-step-num {
  flex-shrink: 0;
  width: 28px; height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF, #52C41A);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
}
.care-step-header h4 { margin: 0; font-size: 15px; color: #303133; font-weight: 600; }
.care-step-item > p { margin: 0; font-size: 13px; color: #606266; line-height: 1.7; }
.care-tips {
  margin-top: 10px;
  padding: 8px 12px;
  background: #fdf6ec;
  border-radius: 6px;
  font-size: 12px;
  color: #E6A23C;
}
.tip-label { font-weight: 600; }

/* 足部护理 - 警示信号 */
.warning-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-bottom: 14px;
}
.warning-card {
  text-align: center;
  padding: 14px;
  background: #fef0f0;
  border-radius: 10px;
  border: 1px solid #fde2e2;
}
.warning-icon { font-size: 28px; display: block; margin-bottom: 6px; }
.warning-card h4 { margin: 0 0 4px; font-size: 14px; color: #F56C6C; font-weight: 600; }
.warning-card p { margin: 0; font-size: 12px; color: #909399; }

/* 响应式 */
@media (max-width: 768px) {
  .warning-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
