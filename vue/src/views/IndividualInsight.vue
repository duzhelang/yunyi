<template>
  <div>
    <h2>个体洞察</h2>
    
    <!-- 样本选择 -->
    <div style="margin: 20px 0">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>样本选择</span>
          </div>
        </template>
        <el-form :inline="true" label-width="80px">
          <el-form-item label="文件">
            <el-select v-model="selectedFile" placeholder="请选择文件">
              <el-option 
                v-for="file in fileList" 
                :key="file.id" 
                :label="file.name" 
                :value="file.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="样本ID">
            <el-input v-model="sampleId" placeholder="请输入样本ID"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadSample">加载样本</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
    
    <!-- 患者概览卡片 -->
    <div style="margin: 20px 0">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>患者概览</span>
          </div>
        </template>
        <div class="overview-container">
          <div class="overview-item" v-for="item in overviewData" :key="item.label">
            <div class="item-label">{{ item.label }}</div>
            <div class="item-value" :class="{ 'abnormal': item.abnormal }">{{ item.value }}</div>
            <div class="item-range">{{ item.range }}</div>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- 风险仪表盘 -->
    <div style="margin: 20px 0">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>风险评估</span>
          </div>
        </template>
        <div class="risk-container">
          <div class="risk-gauge">
            <div id="riskGauge" class="gauge"></div>
          </div>
          <div class="risk-info">
            <div class="risk-score">{{ riskScore }}%</div>
            <div class="risk-level" :class="riskLevelClass">{{ riskLevel }}</div>
            <div class="risk-suggestion">{{ riskSuggestion }}</div>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- SHAP解释力图 -->
    <div style="margin: 20px 0">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>特征贡献分析</span>
          </div>
        </template>
        <div id="shapChart" class="chart full-width"></div>
      </el-card>
    </div>
    
    <!-- 相似病例检索 -->
    <div style="margin: 20px 0">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>相似病例</span>
          </div>
        </template>
        <el-table :data="similarCases" border stripe>
          <el-table-column prop="id" label="病例ID" width="100"></el-table-column>
          <el-table-column prop="similarity" label="相似度" width="100">
            <template #default="scope">
              <el-progress :percentage="scope.row.similarity" :format="formatSimilarity"></el-progress>
            </template>
          </el-table-column>
          <el-table-column prop="features" label="关键特征"></el-table-column>
          <el-table-column prop="outcome" label="真实结局" width="120"></el-table-column>
          <el-table-column label="操作" width="100" align="center">
            <template #default="scope">
              <el-button type="primary" size="small" @click="viewCase(scope.row)">
                查看
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
    
    <!-- 健康建议生成 -->
    <div style="margin: 20px 0">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>健康建议</span>
            <el-button type="primary" size="small" @click="generateAdvice">
              生成建议
            </el-button>
          </div>
        </template>
        <div class="advice-content" v-if="healthAdvice">
          <div v-for="(item, index) in healthAdvice" :key="index" class="advice-item">
            <el-icon class="advice-icon"><Check /></el-icon>
            <span>{{ item }}</span>
          </div>
        </div>
        <div class="advice-placeholder" v-else>
          点击上方按钮生成个性化健康建议
        </div>
      </el-card>
    </div>
    
    <!-- 一键导出报告 -->
    <div style="margin: 20px 0">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>导出报告</span>
          </div>
        </template>
        <el-form label-width="120px">
          <el-form-item label="报告类型">
            <el-radio-group v-model="reportType">
              <el-radio value="诊断建议">诊断建议</el-radio>
              <el-radio value="健康管理">健康管理</el-radio>
              <el-radio value="完整报告">完整报告</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="exportReport">
              导出PDF报告
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import { Check } from '@element-plus/icons-vue';

export default {
  name: "IndividualInsight",
  components: { Check },
  data() {
    return {
      selectedFile: null,
      sampleId: '',
      reportType: '完整报告',
      fileList: [
        { id: 1, name: 'diabetes_data.csv' },
        { id: 2, name: 'new_patients.xlsx' },
        { id: 3, name: 'test_data.csv' }
      ],
      overviewData: [
        { label: '血糖', value: '180 mg/dL', range: '正常: 70-110', abnormal: true },
        { label: 'BMI', value: '32', range: '正常: 18.5-24.9', abnormal: true },
        { label: '年龄', value: '45', range: '成年', abnormal: false },
        { label: '血压', value: '130/85', range: '正常: <120/80', abnormal: true },
        { label: '胰岛素', value: '120 μU/mL', range: '正常: 10-20', abnormal: true },
        { label: '怀孕次数', value: '2', range: '0-10', abnormal: false }
      ],
      riskScore: 87,
      riskLevel: '高风险',
      riskSuggestion: '建议立即就医，进行进一步检查',
      similarCases: [
        { id: 'Case001', similarity: 92, features: '血糖: 175, BMI: 31, 年龄: 43', outcome: '糖尿病' },
        { id: 'Case002', similarity: 88, features: '血糖: 185, BMI: 33, 年龄: 47', outcome: '糖尿病' },
        { id: 'Case003', similarity: 85, features: '血糖: 170, BMI: 30, 年龄: 42', outcome: '糖尿病前期' },
        { id: 'Case004', similarity: 82, features: '血糖: 190, BMI: 34, 年龄: 46', outcome: '糖尿病' },
        { id: 'Case005', similarity: 80, features: '血糖: 165, BMI: 29, 年龄: 44', outcome: '糖尿病前期' }
      ],
      healthAdvice: null
    };
  },
  computed: {
    riskLevelClass() {
      if (this.riskScore >= 80) return 'high-risk';
      if (this.riskScore >= 50) return 'medium-risk';
      return 'low-risk';
    }
  },
  mounted() {
    this.initCharts();
  },
  methods: {
    loadSample() {
      ElMessage.info("样本加载中...");
    },
    generateAdvice() {
      this.healthAdvice = [
        '控制饮食，减少糖分和碳水化合物摄入',
        '每周进行至少150分钟中等强度有氧运动',
        '定期监测血糖水平，建议每天测量',
        '保持健康体重，目标BMI控制在24以下',
        '避免吸烟和过量饮酒',
        '定期就医，遵循医生建议' 
      ];
      ElMessage.success("健康建议已生成");
    },
    exportReport() {
      ElMessage.info("报告导出中...");
    },
    viewCase(row) {
      ElMessage.info(`查看病例: ${row.id}`);
    },
    formatSimilarity(percentage) {
      return `${percentage}%`;
    },
    initCharts() {
      // 这里应该初始化ECharts图表
      // 由于环境限制，这里只做模拟
      console.log("初始化图表");
    }
  }
};
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.overview-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 20px;
}

.overview-item {
  text-align: center;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.item-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.item-value {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
}

.item-value.abnormal {
  color: #f56c6c;
}

.item-range {
  font-size: 12px;
  color: #999;
}

.risk-container {
  display: flex;
  align-items: center;
  gap: 40px;
}

.risk-gauge {
  flex: 1;
}

.gauge {
  height: 200px;
  background-color: #f5f7fa;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}

.risk-info {
  flex: 1;
  text-align: center;
}

.risk-score {
  font-size: 48px;
  font-weight: bold;
  color: #f56c6c;
  margin-bottom: 10px;
}

.risk-level {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
}

.risk-level.high-risk {
  color: #f56c6c;
}

.risk-level.medium-risk {
  color: #e6a23c;
}

.risk-level.low-risk {
  color: #67c23a;
}

.risk-suggestion {
  font-size: 14px;
  color: #666;
}

.chart {
  height: 300px;
  background-color: #f5f7fa;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}

.full-width {
  width: 100%;
}

.advice-content {
  padding: 15px;
}

.advice-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  padding: 8px 12px;
  background-color: #f0f9eb;
  border-radius: 4px;
}

.advice-icon {
  color: #67c23a;
  margin-right: 10px;
}

.advice-placeholder {
  padding: 40px;
  text-align: center;
  color: #999;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style>