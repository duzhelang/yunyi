<template>
  <el-dialog
    :model-value="modelValue"
    title="糖尿病谱系函数说明"
    width="620px"
    custom-class="dpf-info-dialog"
    :append-to-body="true"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <div class="dpf-info-content">
      <div class="dpf-info-header">
        <div class="dpf-info-icon">🧬</div>
        <h3>糖尿病谱系函数（DPF）</h3>
        <p class="dpf-info-subtitle">Diabetes Pedigree Function</p>
      </div>
      <div class="dpf-info-section">
        <h4><el-icon><Document /></el-icon> 什么是糖尿病谱系函数？</h4>
        <p>糖尿病谱系函数是一个用于评估个体患糖尿病遗传风险的重要指标。它主要用于量化家族糖尿病史对个人患病风险的影响程度。</p>
      </div>
      <div class="dpf-info-section">
        <h4><el-icon><DataAnalysis /></el-icon> 主要功能和意义</h4>
        <div class="dpf-info-list">
          <div class="dpf-info-item">
            <span class="dpf-info-num">1</span>
            <div>
              <strong>遗传风险评估</strong>
              <p>通过分析家族中糖尿病患者的分布情况，计算出一个综合指数，反映个体因遗传因素导致的糖尿病风险。</p>
            </div>
          </div>
          <div class="dpf-info-item">
            <span class="dpf-info-num">2</span>
            <div>
              <strong>取值范围：0.08 - 2.42</strong>
              <p>数值越高表示遗传风险越大。</p>
              <div class="dpf-risk-levels">
                <span class="risk-tag low">低风险：0.08-0.4</span>
                <span class="risk-tag medium">中等风险：0.4-0.8</span>
                <span class="risk-tag high">高风险：&gt;0.8</span>
              </div>
            </div>
          </div>
          <div class="dpf-info-item">
            <span class="dpf-info-num">3</span>
            <div>
              <strong>计算方法</strong>
              <p>基于家族成员（如父母、兄弟姐妹、祖父母等）的糖尿病发病年龄和亲属关系程度进行加权计算。</p>
            </div>
          </div>
          <div class="dpf-info-item">
            <span class="dpf-info-num">4</span>
            <div>
              <strong>临床应用</strong>
              <ul>
                <li>在糖尿病风险预测模型中作为重要特征</li>
                <li>帮助医生评估患者的遗传易感性</li>
                <li>指导高风险人群的预防措施</li>
              </ul>
            </div>
          </div>
          <div class="dpf-info-item">
            <span class="dpf-info-num">5</span>
            <div>
              <strong>与其他风险因素的关系</strong>
              <p>糖尿病谱系函数通常与年龄、BMI、血糖水平、血压等因素一起，综合评估个体的糖尿病风险。它是预测模型的重要输入特征之一，与其他生理指标一起，帮助更准确地评估您的糖尿病风险水平。</p>
            </div>
          </div>
        </div>
      </div>
      <div class="dpf-info-section dpf-info-note">
        <el-alert type="info" :closable="false" show-icon>
          <template #title>
            <span>本计算基于 WHO 和 ADA 糖尿病风险评估指南，结果仅供参考，不构成医疗诊断依据。</span>
          </template>
        </el-alert>
      </div>
    </div>
    <template #footer>
      <el-button type="primary" @click="$emit('update:modelValue', false)">我知道了</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { Document, DataAnalysis } from '@element-plus/icons-vue'

defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

defineEmits(['update:modelValue'])
</script>

<style scoped>
.dpf-info-content {
  max-height: 60vh;
  overflow-y: auto;
}

.dpf-info-header {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #e6f7ff, #f0f9ff);
  border-radius: 12px;
  margin-bottom: 20px;
}

.dpf-info-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.dpf-info-header h3 {
  margin: 0 0 4px 0;
  font-size: 20px;
  color: #303133;
}

.dpf-info-subtitle {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.dpf-info-section {
  margin-bottom: 18px;
}

.dpf-info-section h4 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 10px 0;
  font-size: 16px;
  color: #303133;
}

.dpf-info-section p {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.dpf-info-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.dpf-info-item {
  display: flex;
  gap: 12px;
  padding: 14px;
  background: #fafbfc;
  border-radius: 10px;
  border: 1px solid #ebeef5;
}

.dpf-info-num {
  flex-shrink: 0;
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #4080FF, #69b1ff);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
}

.dpf-info-item strong {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  color: #303133;
}

.dpf-info-item p {
  margin: 0;
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
}

.dpf-info-item ul {
  margin: 4px 0 0;
  padding-left: 18px;
}

.dpf-info-item li {
  font-size: 13px;
  color: #606266;
  margin-bottom: 4px;
}

.dpf-risk-levels {
  display: flex;
  gap: 8px;
  margin-top: 10px;
  flex-wrap: wrap;
}

.risk-tag {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.risk-tag.low {
  background: #e8f5e9;
  color: #2e7d32;
}

.risk-tag.medium {
  background: #fff3e0;
  color: #ef6c00;
}

.risk-tag.high {
  background: #ffebee;
  color: #c62828;
}

.dpf-info-note {
  margin-top: 16px;
}
</style>

<style>
.dpf-info-dialog .el-dialog__header {
  background: linear-gradient(135deg, #f0f9ff, #e6f7ff);
  border-radius: 12px 12px 0 0;
  padding: 18px 24px;
  border-bottom: 1px solid #d9ecff;
}

.dpf-info-dialog .el-dialog__title {
  font-weight: 600;
  color: #303133;
}

.dpf-info-dialog .el-dialog__body {
  padding: 20px 24px;
}

.dpf-info-dialog .el-dialog {
  border-radius: 12px;
  overflow: hidden;
}
</style>
