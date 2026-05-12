<template>
  <!-- 表单卡片 -->
  <el-card class="form-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <el-icon class="header-icon"><Edit /></el-icon>
        <span>健康指标自查</span>
        <el-tag size="small" type="success" effect="plain">
          <el-icon><User /></el-icon> 诊断员在线
        </el-tag>
      </div>
    </template>

    <el-form :model="form" label-width="120px" size="default" class="health-form">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="年龄 (岁)" required>
            <el-input-number
              :model-value="form.Age"
              :min="1"
              :max="120"
              placeholder="岁"
              controls-position="right"
              @update:model-value="updateFormField('Age', $event)"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="身高 (cm)" required>
            <el-input-number
              :model-value="temp.height"
              :min="50"
              :max="250"
              placeholder="厘米"
              controls-position="right"
              @update:model-value="updateTempField('height', $event)"
              @change="$emit('calc-bmi')"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="体重 (kg)" required>
            <el-input-number
              :model-value="temp.weight"
              :min="20"
              :max="300"
              placeholder="千克"
              controls-position="right"
              @update:model-value="updateTempField('weight', $event)"
              @change="$emit('calc-bmi')"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="BMI (自动计算)" required>
        <el-input :model-value="form.BMI" disabled placeholder="输入身高体重自动计算">
          <template #append>kg/m²</template>
        </el-input>
        <div v-if="bmiFeedback.text" class="feedback-tag" :class="bmiFeedback.class">
          <el-icon><component :is="bmiFeedback.icon" /></el-icon>
          {{ bmiFeedback.text }}
        </div>
      </el-form-item>

      <el-divider content-position="left">
        <span class="section-divider">🩺 核心医疗指标</span>
      </el-divider>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item required>
            <template #label>
              <el-tooltip content="禁食8小时后的血糖水平，反映基础胰岛素分泌功能，是糖尿病筛查的核心指标。正常值：70-99 mg/dL" placement="top" :show-after="300">
                <span class="label-with-tip">空腹血糖 (mg/dL) <el-icon class="tip-icon"><InfoFilled /></el-icon></span>
              </el-tooltip>
            </template>
            <el-input-number
              :model-value="form.Glucose"
              :precision="1"
              :min="0"
              :max="500"
              placeholder="mg/dL"
              controls-position="right"
              @update:model-value="updateFormField('Glucose', $event)"
              @change="$emit('evaluate-glucose')"
            />
            <div v-if="glucoseFeedback.text" class="feedback-tag" :class="glucoseFeedback.class">
              <el-icon><component :is="glucoseFeedback.icon" /></el-icon>
              {{ glucoseFeedback.text }}
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item>
            <template #label>
              <el-tooltip content="舒张压，反映血管弹性和心脏舒张期压力。高血压是糖尿病并发症的重要危险因素。正常值：&lt;80 mmHg" placement="top" :show-after="300">
                <span class="label-with-tip">血压 (mmHg) <el-icon class="tip-icon"><InfoFilled /></el-icon></span>
              </el-tooltip>
            </template>
            <el-input-number
              :model-value="form.BloodPressure"
              :min="40"
              :max="200"
              placeholder="mmHg"
              controls-position="right"
              @update:model-value="updateFormField('BloodPressure', $event)"
              @change="$emit('evaluate-bp')"
            />
            <div v-if="bpFeedback.text" class="feedback-tag" :class="bpFeedback.class">
              <el-icon><component :is="bpFeedback.icon" /></el-icon>
              {{ bpFeedback.text }}
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item>
            <template #label>
              <el-tooltip content="血清胰岛素水平，反映胰岛β细胞的分泌能力。2型糖尿病早期常出现高胰岛素血症，后期则分泌不足。正常值：16-166 mU/L" placement="top" :show-after="300">
                <span class="label-with-tip">胰岛素 (mU/L) <el-icon class="tip-icon"><InfoFilled /></el-icon></span>
              </el-tooltip>
            </template>
            <el-input-number
              :model-value="form.Insulin"
              :precision="1"
              :min="0"
              :max="200"
              placeholder="mU/L"
              controls-position="right"
              @update:model-value="updateFormField('Insulin', $event)"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item>
            <template #label>
              <el-tooltip content="肱三头肌皮褶厚度，用于估算体脂百分比。肥胖是2型糖尿病的主要风险因素之一，皮褶厚度可间接反映肥胖程度。" placement="top" :show-after="300">
                <span class="label-with-tip">皮褶厚度 (mm) <el-icon class="tip-icon"><InfoFilled /></el-icon></span>
              </el-tooltip>
            </template>
            <el-input-number
              :model-value="form.SkinThickness"
              :min="0"
              :max="100"
              placeholder="mm"
              controls-position="right"
              @update:model-value="updateFormField('SkinThickness', $event)"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item required>
            <template #label>
              <el-tooltip content="基于家族糖尿病史的遗传风险指数，综合考虑亲属患病情况和发病年龄。取值范围：0.08-2.42，值越高遗传风险越大。" placement="top" :show-after="300">
                <span class="label-with-tip">糖尿病谱系函数 <el-icon class="tip-icon"><InfoFilled /></el-icon></span>
              </el-tooltip>
            </template>
            <div class="dpf-input-group">
              <el-input-number
                :model-value="form.DiabetesPedigreeFunction"
                :precision="3"
                :step="0.01"
                :min="0.08"
                :max="2.42"
                controls-position="right"
                @update:model-value="updateFormField('DiabetesPedigreeFunction', $event)"
              />
              <el-button type="primary" plain size="small" class="dpf-calc-btn" @click="$emit('open-dpf-calc')">
                <el-icon><Edit /></el-icon> 计算
              </el-button>
              <el-button type="info" plain size="small" class="dpf-info-btn" @click="$emit('open-dpf-info')">
                <el-icon><InfoFilled /></el-icon> 说明
              </el-button>
            </div>
            <div class="helper-text">* 家族遗传系数，点击"计算"自动评估</div>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item>
            <template #label>
              <el-tooltip content="妊娠次数（含流产），妊娠期糖尿病史是2型糖尿病的独立风险因素，多次妊娠可增加患病概率。" placement="top" :show-after="300">
                <span class="label-with-tip">怀孕次数 <el-icon class="tip-icon"><InfoFilled /></el-icon></span>
              </el-tooltip>
            </template>
            <el-input-number
              :model-value="form.Pregnancies"
              :min="0"
              :max="20"
              placeholder="次"
              controls-position="right"
              @update:model-value="updateFormField('Pregnancies', $event)"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-divider content-position="left">
        <span class="section-divider">📝 补充信息</span>
      </el-divider>

      <el-form-item label="自觉症状">
        <el-input
          :model-value="form.symptoms"
          type="textarea"
          :rows="2"
          placeholder="如有多饮、多尿等症状请描述..."
          @update:model-value="updateFormField('symptoms', $event)"
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
          accept=".jpg,.jpeg,.png,.pdf,.csv"
        >
          <el-button type="primary" plain>
            <el-icon><Upload /></el-icon> 选择文件
          </el-button>
          <template #tip>
            <div class="el-upload__tip">支持 jpg、png、pdf、csv 格式</div>
          </template>
        </el-upload>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <div class="action-bar">
      <el-button plain :disabled="saving || submitting || predicting" @click="$emit('reset')">
        <el-icon><RefreshRight /></el-icon> 重置
      </el-button>
      <el-button type="success" :loading="saving" :disabled="submitting || predicting" @click="$emit('save')">
        <el-icon><Coin /></el-icon> 保存档案
      </el-button>
      <el-button type="warning" :loading="predicting" :disabled="saving || submitting" @click="$emit('quick-predict')">
        <el-icon><DataBoard /></el-icon> 快速检测
      </el-button>
      <el-button type="danger" :loading="saving || predicting" :disabled="saving || submitting || predicting" @click="$emit('save-and-predict')">
        <el-icon><DataAnalysis /></el-icon> 保存并检测
      </el-button>
      <el-button type="primary" :loading="submitting" :disabled="saving || predicting" @click="$emit('submit-doctor')">
        <el-icon><Promotion /></el-icon> 发送诊断员
      </el-button>
    </div>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import {
  Edit, User, InfoFilled, Upload, RefreshRight, Coin,
  DataBoard, DataAnalysis, Promotion
} from '@element-plus/icons-vue'

const props = defineProps({
  form: {
    type: Object,
    required: true
  },
  temp: {
    type: Object,
    required: true
  },
  bmiFeedback: {
    type: Object,
    default: () => ({ text: '', class: '', icon: '' })
  },
  glucoseFeedback: {
    type: Object,
    default: () => ({ text: '', class: '', icon: '' })
  },
  bpFeedback: {
    type: Object,
    default: () => ({ text: '', class: '', icon: '' })
  },
  saving: {
    type: Boolean,
    default: false
  },
  predicting: {
    type: Boolean,
    default: false
  },
  submitting: {
    type: Boolean,
    default: false
  },
  fileList: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits([
  'update:form',
  'update:temp',
  'save',
  'save-and-predict',
  'quick-predict',
  'submit-doctor',
  'reset',
  'calc-bmi',
  'evaluate-glucose',
  'evaluate-bp',
  'file-change',
  'open-dpf-calc',
  'open-dpf-info'
])

const uploadRef = ref(null)

/** 更新表单字段值，向父组件发送完整表单对象 */
function updateFormField(field, value) {
  emit('update:form', { ...props.form, [field]: value })
}

/** 更新临时字段（身高/体重），向父组件发送完整 temp 对象 */
function updateTempField(field, value) {
  emit('update:temp', { ...props.temp, [field]: value })
}

/** 文件上传变化处理 */
function handleFileChange(uploadFile) {
  emit('file-change', uploadFile)
}
</script>

<style scoped>
.form-card {
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
  border: 1px solid #e8ecf1;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.04);
  border-radius: 14px;
  transition: all 0.3s ease;
}
.form-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 28px rgba(64, 128, 255, 0.08);
}
.form-card :deep(.el-card__header) {
  padding: 16px 24px;
  border-bottom: 1px solid #eef2f6;
  background: linear-gradient(135deg, rgba(64, 128, 255, 0.03), rgba(82, 196, 26, 0.02));
  border-radius: 14px 14px 0 0;
}
.form-card :deep(.el-card__body) {
  padding: 20px 28px 24px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 15px;
  color: #1D2129;
}
.header-icon {
  font-size: 18px;
  color: #409EFF;
}

.el-divider {
  margin: 16px 0 14px;
}
.health-form :deep(.el-divider__text) {
  background: transparent;
}
.health-form :deep(.el-divider__text::before),
.health-form :deep(.el-divider__text::after) {
  background: linear-gradient(90deg, rgba(64, 128, 255, 0.15), transparent);
}
.section-divider {
  font-size: 13px;
  font-weight: 600;
  color: #606266;
  letter-spacing: 0.5px;
}

.health-form {
  padding: 4px 8px;
}
.health-form :deep(.el-form-item) {
  margin-bottom: 16px;
}
.health-form :deep(.el-input-number) {
  width: 100%;
}
.health-form :deep(.el-row + .el-row) {
  margin-top: 2px;
}

/* 标签悬浮提示文字可换行 */
.health-form :deep(.el-tooltip__popper) {
  max-width: 280px;
  white-space: normal !important;
  word-break: break-word !important;
  line-height: 1.5;
  padding: 10px 14px;
  font-size: 13px;
}

.feedback-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  margin-top: 6px;
  padding: 3px 10px;
  border-radius: 14px;
  font-size: 12px;
  font-weight: 500;
  animation: feedbackIn 0.3s ease;
}
@keyframes feedbackIn {
  from { opacity: 0; transform: translateY(-4px); }
  to { opacity: 1; transform: translateY(0); }
}
.feedback-tag.success { background: #f0f9eb; color: #67C23A; }
.feedback-tag.warning { background: #fdf6ec; color: #E6A23C; }
.feedback-tag.danger { background: #fef0f0; color: #F56C6C; }

.helper-text {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.action-bar {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
  padding-top: 18px;
  border-top: 1px solid #eef2f6;
  flex-wrap: wrap;
}
.action-bar .el-button {
  min-width: 110px;
  border-radius: 8px;
  font-weight: 500;
  letter-spacing: 0.3px;
}
.action-bar .el-button--primary {
  background: linear-gradient(135deg, #4a90e2, #357abd);
  border: none;
  box-shadow: 0 2px 8px rgba(74, 144, 226, 0.25);
}
.action-bar .el-button--primary:hover {
  box-shadow: 0 4px 14px rgba(74, 144, 226, 0.35);
  transform: translateY(-1px);
}
.action-bar .el-button--danger {
  background: linear-gradient(135deg, #f56c6c, #e04b4b);
  border: none;
  box-shadow: 0 2px 8px rgba(245, 108, 108, 0.2);
}
.action-bar .el-button--warning {
  background: linear-gradient(135deg, #e6a23c, #d4902a);
  border: none;
  box-shadow: 0 2px 8px rgba(230, 162, 60, 0.2);
}
.action-bar .el-button--success {
  background: linear-gradient(135deg, #67c23a, #55a830);
  border: none;
  box-shadow: 0 2px 8px rgba(103, 194, 58, 0.2);
}

/* DPF 输入组样式 */
.dpf-input-group {
  display: flex;
  gap: 6px;
  align-items: center;
}
.dpf-input-group .el-input-number {
  flex: 1;
}
.dpf-calc-btn, .dpf-info-btn {
  flex-shrink: 0;
  border-radius: 6px;
}

/* 标签悬浮提示样式 */
.label-with-tip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  cursor: help;
}
.tip-icon {
  font-size: 14px;
  color: #909399;
  transition: color 0.2s;
}
.label-with-tip:hover .tip-icon {
  color: #409EFF;
}

@media (max-width: 768px) {
  .form-card :deep(.el-card__body) {
    padding: 16px;
  }
  .health-form {
    padding: 0;
  }
  .action-bar {
    flex-direction: column;
  }
  .action-bar .el-button {
    width: 100%;
  }
  .dpf-input-group {
    flex-wrap: wrap;
  }
}
@media (min-width: 769px) and (max-width: 1200px) {
  .form-card :deep(.el-card__body) {
    padding: 16px 20px 20px;
  }
  .health-form {
    padding: 2px 4px;
  }
}
</style>
