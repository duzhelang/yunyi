<template>
  <div class="health-assistant">
    <!-- 1. 顶部欢迎 -->
    <div class="welcome-banner">
      <h1>👋 您好,今天来做个健康自查吧</h1>
      <p>输入血糖、身高体重,立即获取智能评估与建议</p>
    </div>

    <el-row :gutter="20">
      <!-- 左侧:动态自查 + 通用建议 -->
      <el-col :xs="24" :lg="16">

        <!-- 2. [核心功能]动态指标自测器 -->
        <el-card shadow="hover" class="section-card interactive-card">
          <div slot="header" class="card-title">
            <i class="el-icon-odometer"></i> 指标自测器
            <el-tag size="mini" type="success" style="float:right">自动计算 BMI</el-tag>
          </div>

          <div class="checker-grid">
            <!-- 循环生成输入框(修复:只循环需要展示的字段) -->
            <div v-for="(item, key) in getDisplayableFormItems" :key="key" class="checker-item">
              <label class="checker-label">
                {{ item.label }}
                <span class="unit">{{ item.unit }}</span>
              </label>

              <!-- 特殊处理:BMI输入组 -->
              <div v-if="key === 'bmiGroup'" class="bmi-input-group">
                <div class="sub-input">
                  <span class="sub-label">身高 (cm)</span>
                  <el-input-number
                      v-model="checkForm.height.value"
                      :min="50" :max="250"
                      placeholder="例: 170"
                      @change="calculateBMI"
                      style="width: 100%"
                  ></el-input-number>
                </div>
                <div class="sub-input">
                  <span class="sub-label">体重 (kg)</span>
                  <el-input-number
                      v-model="checkForm.weight.value"
                      :min="20" :max="300"
                      placeholder="例: 65"
                      @change="calculateBMI"
                      style="width: 100%"
                  ></el-input-number>
                </div>
              </div>

              <!-- 普通输入框 (血糖等) -->
              <el-input-number
                  v-else
                  v-model="item.value"
                  :min="0"
                  :max="item.max"
                  :step="item.step || 0.1"
                  :placeholder="'请输入' + item.label"
                  style="width: 100%"
                  @change="calculateResult(key)"
              ></el-input-number>

              <!-- 动态结果反馈区 -->
              <div v-if="item.status && item.status !== 'empty'" class="result-feedback" :class="item.statusClass">
                <i :class="item.statusIcon"></i>
                <span><b>{{ item.resultText }}</b>:{{ item.advice }}</span>
              </div>

              <!-- 未输入提示 -->
              <div v-if="item.status === 'empty'" class="result-feedback status-empty">
                <i class="el-icon-question"></i>
                <span>暂未检查此项,请填写后自动评估</span>
              </div>
            </div>
          </div>

          <div class="action-row">
            <el-button type="primary" @click="calculateAll" round icon="el-icon-search">一键全面评估</el-button>
            <el-button @click="resetForm" round icon="el-icon-refresh">重置所有</el-button>
          </div>

          <div class="tip-box">
            <i class="el-icon-info"></i> 注:评估标准参考中国2型糖尿病防治指南,BMI = 体重(kg) / 身高(m).
          </div>
        </el-card>

        <!-- 3. 日常健康建议 -->
        <el-card shadow="hover" class="section-card">
          <div slot="header" class="card-title">
            <i class="el-icon-s-order"></i> 日常健康管理建议
          </div>
          <div class="advice-grid">
            <div class="advice-item">
              <div class="adv-icon bg-green"><i class="el-icon-food"></i></div>
              <h3>饮食控制</h3>
              <ul>
                <li><b>粗细搭配:</b>主食增加燕麦、荞麦、糙米.</li>
                <li><b>多吃蔬菜:</b>每日500g以上绿叶菜.</li>
                <li><b>少油少盐:</b>食盐&lt;5g,避免油炸.</li>
              </ul>
            </div>
            <div class="advice-item">
              <div class="adv-icon bg-blue"><i class="el-icon-running"></i></div>
              <h3>科学运动</h3>
              <ul>
                <li><b>频率:</b>每周150分钟中等强度运动.</li>
                <li><b>时机:</b>餐后1小时运动,防低血糖.</li>
                <li><b>抗阻:</b>每周2次力量训练.</li>
              </ul>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧:新工具栏 + 打卡 -->
      <el-col :xs="24" :lg="8">

        <!-- 4. [新版]实用工具箱 -->
        <el-card shadow="hover" class="side-card">
          <div slot="header" class="card-title"><i class="el-icon-s-tools"></i> 糖友必备工具</div>
          <div class="tool-grid-vertical">
            <!-- 低血糖急救 -->
            <div class="tool-btn-large" @click="openTool('emergency')">
              <div class="icon-box-large bg-red"><i class="el-icon-warning"></i></div>
              <span class="tool-text-large">低血糖急救</span>
            </div>

            <!-- 碳水计数法 -->
            <div class="tool-btn-large" @click="openTool('carb-count')">
              <div class="icon-box-large bg-orange"><i class="el-icon-magic-stick"></i></div>
              <span class="tool-text-large">碳水计数法</span>
            </div>

            <!-- 足部护理指南 -->
            <div class="tool-btn-large" @click="openTool('foot-care')">
              <div class="icon-box-large bg-green"><i class="el-icon-cpu"></i></div>
              <span class="tool-text-large">足部护理指南</span>
            </div>
          </div>
        </el-card>

        <!-- 5. 每日打卡 -->
        <el-card shadow="hover" class="side-card checkin-card">
          <div slot="header" class="card-title"><i class="el-icon-calendar-check"></i> 今日打卡</div>
          <div class="checkin-list">
            <div v-for="(item, index) in checkList" :key="index" class="check-item" @click="toggleCheck(index)">
              <i class="icon-circle"
                 :class="item.done ? 'el-icon-check' : 'el-icon-circle-outline'"
                 :style="{ color: item.done ? '#67C23A' : '#DCDFE6', background: item.done ? '#f0f9eb' : '#f5f7fa' }"></i>
              <span :style="{ textDecoration: item.done ? 'line-through' : 'none', color: item.done ? '#909399' : '#303133' }">
                {{ item.text }}
              </span>
            </div>
          </div>
          <div class="checkin-progress">
            <div class="progress-text">
              <span>今日完成度</span>
              <span>{{ progressPercent }}%</span>
            </div>
            <el-progress :percentage="progressPercent" :stroke-width="10" :show-text="false" :color="progressPercent === 100 ? '#67C23A' : '#409EFF'"></el-progress>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: "HealthAssistantInteractive",
  data() {
    return {
      // 打卡数据
      checkList: [
        { text: "早餐吃了粗粮/蔬菜", done: false },
        { text: "餐后散步 20 分钟", done: false },
        { text: "喝够 8 杯水", done: false },
        { text: "今晚 23:00 前睡觉", done: false }
      ],
      // 自查表单数据
      checkForm: {
        fastingGlucose: {
          label: "空腹血糖",
          unit: "mmol/L",
          value: null,
          max: 30,
          status: null,
          statusClass: '',
          statusIcon: '',
          resultText: '',
          advice: ''
        },
        postprandialGlucose: {
          label: "餐后2小时血糖",
          unit: "mmol/L",
          value: null,
          max: 40,
          status: null,
          statusClass: '',
          statusIcon: '',
          resultText: '',
          advice: ''
        },
        hba1c: {
          label: "糖化血红蛋白",
          unit: "%",
          value: null,
          max: 15,
          step: 0.1,
          status: null,
          statusClass: '',
          statusIcon: '',
          resultText: '',
          advice: ''
        },
        // BMI 结果展示项
        bmiResult: {
          label: "BMI 指数评估",
          unit: "kg/m",
          value: null,
          status: null,
          statusClass: '',
          statusIcon: '',
          resultText: '',
          advice: ''
        },
        // 用于输入的中间变量(不参与循环展示)
        height: { value: null },
        weight: { value: null }
      }
    };
  },
  computed: {
    // 修复:计算完成度百分比
    progressPercent() {
      const doneCount = this.checkList.filter(i => i.done).length;
      return Math.round((doneCount / this.checkList.length) * 100);
    },
    // 修复:定义需要循环展示的表单项(排除height/weight,新增bmiGroup)
    getDisplayableFormItems() {
      return {
        fastingGlucose: this.checkForm.fastingGlucose,
        postprandialGlucose: this.checkForm.postprandialGlucose,
        hba1c: this.checkForm.hba1c,
        bmiGroup: { // 新增BMI输入组的占位项
          label: "BMI 指数",
          unit: "",
          status: this.checkForm.bmiResult.status,
          statusClass: this.checkForm.bmiResult.statusClass,
          statusIcon: this.checkForm.bmiResult.statusIcon,
          resultText: this.checkForm.bmiResult.resultText,
          advice: this.checkForm.bmiResult.advice
        },
        bmiResult: this.checkForm.bmiResult
      };
    }
  },
  methods: {
    // 打卡切换
    toggleCheck(index) {
      this.checkList[index].done = !this.checkList[index].done;
    },

    // 打开工具 (模拟)
    openTool(type) {
      const maps = {
        'emergency': '🚑 低血糖急救:立即食用 15g 快速升糖食品(如半杯果汁、3-4 颗糖),15 分钟后复测.若未缓解请立即就医.',
        'carb-count': '🍚 碳水计数法:每份碳水约含 15g 碳水化合物,帮助精准控制餐后血糖.例如:1小碗米饭≈2份碳水.',
        'foot-care': '🦶 足部护理指南:每天检查双脚有无伤口、水泡;穿宽松鞋袜;洗脚水温不超过 37℃;定期修剪指甲.'
      };
      this.$message.info(maps[type] || '功能开发中...');
    },

    // 核心算法:根据数值判断状态
    evaluate(type, val) {
      // [关键]如果值为 null、undefined 或 0,返回空状态
      if (val === null || val === undefined || val === 0) {
        return {
          status: 'empty',
          statusClass: '',
          statusIcon: '',
          resultText: '',
          advice: ''
        };
      }

      let res = { status: '', statusClass: '', statusIcon: '', resultText: '', advice: '' };

      if (type === 'fastingGlucose') {
        if (val < 3.9) {
          res = { status: 'danger', statusClass: 'status-low', statusIcon: 'el-icon-warning-outline', resultText: '偏低', advice: '可能存在低血糖风险,请立即补充糖分并监测.' };
        } else if (val <= 6.1) {
          res = { status: 'normal', statusClass: 'status-normal', statusIcon: 'el-icon-circle-check', resultText: '正常', advice: '保持良好习惯,定期复查即可.' };
        } else if (val < 7.0) {
          res = { status: 'warning', statusClass: 'status-warn', statusIcon: 'el-icon-info', resultText: '偏高 (糖尿病前期)', advice: '处于糖尿病前期,需严格控制饮食并增加运动.' };
        } else {
          res = { status: 'danger', statusClass: 'status-danger', statusIcon: 'el-icon-close', resultText: '过高', advice: '达到糖尿病诊断标准,建议尽快就医确诊.' };
        }
      }
      else if (type === 'postprandialGlucose') {
        if (val <= 7.8) {
          res = { status: 'normal', statusClass: 'status-normal', statusIcon: 'el-icon-circle-check', resultText: '正常', advice: '血糖控制良好,继续保持.' };
        } else if (val < 11.1) {
          res = { status: 'warning', statusClass: 'status-warn', statusIcon: 'el-icon-info', resultText: '偏高 (糖耐量异常)', advice: '糖耐量受损,需减少碳水摄入,加强餐后运动.' };
        } else {
          res = { status: 'danger', statusClass: 'status-danger', statusIcon: 'el-icon-close', resultText: '过高', advice: '疑似糖尿病,请务必前往医院内分泌科就诊.' };
        }
      }
      else if (type === 'hba1c') {
        if (val < 4) {
          res = { status: 'danger', statusClass: 'status-low', statusIcon: 'el-icon-warning-outline', resultText: '偏低', advice: '数值异常偏低,请确认检测是否准确.' };
        } else if (val <= 6.0) {
          res = { status: 'normal', statusClass: 'status-normal', statusIcon: 'el-icon-circle-check', resultText: '正常', advice: '近3个月血糖控制理想.' };
        } else if (val < 6.5) {
          res = { status: 'warning', statusClass: 'status-warn', statusIcon: 'el-icon-info', resultText: '临界偏高', advice: '处于糖尿病前期边缘,需引起重视.' };
        } else {
          res = { status: 'danger', statusClass: 'status-danger', statusIcon: 'el-icon-close', resultText: '过高', advice: '提示长期血糖控制不佳,是糖尿病的重要指标.' };
        }
      }
      else if (type === 'bmiResult') {
        // BMI 逻辑
        if (val < 18.5) {
          res = { status: 'warning', statusClass: 'status-warn', statusIcon: 'el-icon-info', resultText: '偏瘦', advice: '体重过轻,建议均衡营养,适当增重.' };
        } else if (val <= 23.9) {
          res = { status: 'normal', statusClass: 'status-normal', statusIcon: 'el-icon-circle-check', resultText: '正常', advice: '体重标准,请继续保持.' };
        } else if (val < 28.0) {
          res = { status: 'warning', statusClass: 'status-warn', statusIcon: 'el-icon-info', resultText: '超重', advice: '体重超标,建议控制饮食总量并增加有氧运动.' };
        } else {
          res = { status: 'danger', statusClass: 'status-danger', statusIcon: 'el-icon-close', resultText: '肥胖', advice: '属于肥胖范围,是糖尿病高危因素,建议减重.' };
        }
      }
      return res;
    },

    // 专门计算 BMI
    calculateBMI() {
      const h = this.checkForm.height.value;
      const w = this.checkForm.weight.value;
      const bmiItem = this.checkForm.bmiResult;

      // 如果身高或体重缺失或为0,则清空结果并显示未输入
      if (!h || !w || h === 0 || w === 0) {
        bmiItem.value = null;
        bmiItem.status = 'empty';
        bmiItem.statusClass = '';
        bmiItem.statusIcon = '';
        bmiItem.resultText = '';
        bmiItem.advice = '';
        return;
      }

      // 计算:体重(kg) / (身高(m) * 身高(m))
      const heightInMeters = h / 100;
      const bmiValue = w / (heightInMeters * heightInMeters);

      bmiItem.value = parseFloat(bmiValue.toFixed(1)); // 保留一位小数

      // 调用评估
      const result = this.evaluate('bmiResult', bmiItem.value);
      Object.assign(bmiItem, result);
    },

    // 单个计算 (输入时触发)
    calculateResult(key) {
      const item = this.checkForm[key];
      if (!item) return; // 防止key不存在导致报错
      const result = this.evaluate(key, item.value);
      Object.assign(item, result);
    },

    // 一键计算
    calculateAll() {
      // 先算 BMI
      this.calculateBMI();

      // 再算其他
      ['fastingGlucose', 'postprandialGlucose', 'hba1c'].forEach(key => {
        this.calculateResult(key);
      });

      // 检查是否有数据
      const hasData =
          this.checkForm.fastingGlucose.value ||
          this.checkForm.postprandialGlucose.value ||
          this.checkForm.hba1c.value ||
          (this.checkForm.height.value && this.checkForm.weight.value);

      if (!hasData) {
        this.$message.warning('请先输入至少一项指标数值');
      } else {
        this.$message.success('评估完成,请查看各项反馈');
      }
    },

    // 修复:重置表单(语法错误 + 逻辑错误)
    resetForm() {
      // 重置血糖等指标
      ['fastingGlucose', 'postprandialGlucose', 'hba1c', 'bmiResult'].forEach(key => {
        this.checkForm[key].value = null;
        this.checkForm[key].status = null;
        this.checkForm[key].statusClass = '';
        this.checkForm[key].statusIcon = '';
        this.checkForm[key].resultText = '';
        this.checkForm[key].advice = '';
      });
      // 重置身高体重
      this.checkForm.height.value = null;
      this.checkForm.weight.value = null;

      // 重置打卡状态(可选)
      // this.checkList.forEach(item => item.done = false);

      this.$message.info('表单已重置');
    }
  }
};
</script>

<style scoped>
.health-assistant { padding: 20px; background-color: #f5f7fa; min-height: 100vh; }
.welcome-banner { background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%); color: white; padding: 30px; border-radius: 12px; margin-bottom: 20px; box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3); }
.welcome-banner h1 { margin: 0 0 10px 0; font-size: 24px; }
.welcome-banner p { margin: 0; opacity: 0.9; font-size: 15px; }

.section-card, .side-card { border-radius: 12px; border: none; margin-bottom: 20px; background: #fff; }
.card-title { font-weight: 600; font-size: 16px; color: #303133; display: flex; align-items: center; gap: 8px; }
.card-title i { color: #409EFF; }

/* 交互式自查样式 */
.checker-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 20px; margin-bottom: 20px; }
.checker-item { background: #fafafa; padding: 15px; border-radius: 8px; border: 1px solid #ebeef5; transition: all 0.3s; position: relative; }
.checker-label { display: block; font-size: 14px; color: #606266; margin-bottom: 8px; font-weight: 600; }
.unit { font-weight: normal; font-size: 12px; color: #909399; margin-left: 4px; }

/* BMI 组合输入框样式 */
.bmi-input-group { display: flex; gap: 10px; }
.sub-input { flex: 1; }
.sub-label { display: block; font-size: 12px; color: #909399; margin-bottom: 4px; }

/* 结果反馈区域 */
.result-feedback { margin-top: 12px; padding: 10px; border-radius: 6px; font-size: 13px; line-height: 1.5; display: flex; align-items: flex-start; gap: 8px; animation: fadeIn 0.3s ease; }
.result-feedback i { font-size: 16px; margin-top: 2px; }
.result-feedback b { display: block; margin-bottom: 2px; }

/* 状态颜色 */
.status-normal { background: #f0f9eb; color: #67C23A; border: 1px solid #e1f3d8; }
.status-warn { background: #fdf6ec; color: #E6A23C; border: 1px solid #faecd8; }
.status-danger { background: #fef0f0; color: #F56C6C; border: 1px solid #fde2e2; }
.status-low { background: #f4f4f5; color: #909399; border: 1px solid #e9e9eb; }
/* 未输入状态 */
.status-empty { background: #f5f7fa; color: #909399; border: 1px dashed #dcdfe6; font-style: italic; }

.action-row { display: flex; gap: 10px; justify-content: center; margin-bottom: 15px; }
.tip-box { margin-top: 15px; padding: 10px; background: #ecf5ff; color: #409EFF; font-size: 13px; border-radius: 6px; display: flex; align-items: center; gap: 8px; }

/* 建议部分 */
.advice-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; }
.advice-item { background: #fafafa; padding: 20px; border-radius: 8px; border: 1px solid #ebeef5; }
.adv-icon { width: 40px; height: 40px; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: white; font-size: 20px; margin-bottom: 15px; }
.bg-green { background: #67C23A; } .bg-blue { background: #409EFF; }
.advice-item h3 { margin: 0 0 10px 0; font-size: 16px; color: #303133; }
.advice-item ul { margin: 0; padding-left: 18px; color: #606266; font-size: 14px; line-height: 1.8; }

/* 工具栏样式 - 垂直排列,大按钮 */
.tool-grid-vertical { display: flex; flex-direction: column; gap: 20px; }
.tool-btn-large { display: flex; align-items: center; padding: 20px; background: #fff; border: 1px solid #ebeef5; border-radius: 12px; cursor: pointer; transition: all 0.2s; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
.tool-btn-large:hover { background: #f5f7fa; border-color: #409EFF; transform: translateY(-2px); box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15); }
.icon-box-large { width: 50px; height: 50px; border-radius: 12px; display: flex; align-items: center; justify-content: center; color: white; font-size: 24px; margin-right: 15px; flex-shrink: 0; }
.bg-red { background: #F56C6C; }
.bg-orange { background: #E6A23C; }
/* 修复:避免样式覆盖 */
.adv-icon.bg-green { background: #67C23A !important; }
.tool-text-large { font-size: 16px; color: #303133; font-weight: 500; }

/* 打卡部分 */
.checkin-list { margin-bottom: 15px; }
.check-item { display: flex; align-items: center; padding: 10px 0; cursor: pointer; }
.icon-circle { font-size: 20px; margin-right: 10px; width: 24px; height: 24px; display: flex; align-items: center; justify-content: center; border-radius: 50%; }
.checkin-progress { border-top: 1px solid #ebeef5; padding-top: 15px; }
.progress-text { font-size: 12px; color: #909399; margin-bottom: 8px; display: flex; justify-content: space-between; }

@keyframes fadeIn { from { opacity: 0; transform: translateY(-5px); } to { opacity: 1; transform: translateY(0); } }

@media (max-width: 768px) {
  .advice-grid { grid-template-columns: 1fr; }
  .checker-grid { grid-template-columns: 1fr; }
  .tool-btn-large { flex-direction: column; text-align: center; padding: 15px; }
  .icon-box-large { margin-right: 0; margin-bottom: 10px; }
}
</style>