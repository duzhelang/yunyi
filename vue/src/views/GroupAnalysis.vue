<template>
  <div>
    <h2>群体分析</h2>
    
    <!-- 筛选器 -->
    <div style="margin: 20px 0">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>筛选条件</span>
          </div>
        </template>
        <el-form :inline="true" label-width="80px">
          <el-form-item label="时间范围">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="数据源">
            <el-select v-model="dataSource" placeholder="请选择">
              <el-option label="全部" value="all"></el-option>
              <el-option label="CSV文件" value="csv"></el-option>
              <el-option label="Excel文件" value="excel"></el-option>
              <el-option label="手动录入" value="manual"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="applyFilter">应用筛选</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
    
    <!-- 全局风险分布 -->
    <div style="margin: 20px 0">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>全局风险分布</span>
          </div>
        </template>
        <div class="chart-container">
          <div class="chart-item">
            <h3>饼图</h3>
            <div id="pieChart" class="chart"></div>
          </div>
          <div class="chart-item">
            <h3>柱状图</h3>
            <div id="barChart" class="chart"></div>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- 特征重要性排行 -->
    <div style="margin: 20px 0">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>特征重要性排行</span>
          </div>
        </template>
        <div id="featureChart" class="chart full-width"></div>
      </el-card>
    </div>
    
    <!-- 多文件对比 -->
    <div style="margin: 20px 0">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>多文件对比</span>
          </div>
        </template>
        <el-form label-width="120px">
          <el-form-item label="选择文件">
            <el-select 
              v-model="selectedFiles" 
              multiple 
              placeholder="请选择2-5个文件"
              :disabled="selectedFiles.length >= 5"
            >
              <el-option 
                v-for="file in fileList" 
                :key="file.id" 
                :label="file.name" 
                :value="file.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="compareFiles" :disabled="selectedFiles.length < 2">
              开始对比
            </el-button>
          </el-form-item>
        </el-form>
        <div id="compareChart" class="chart full-width" v-if="showCompareChart"></div>
      </el-card>
    </div>
    
    <!-- 异常样本标记 -->
    <div style="margin: 20px 0">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>异常样本标记</span>
            <el-button type="warning" size="small" @click="exportAnomalies">
              导出异常样本
            </el-button>
          </div>
        </template>
        <el-table :data="anomalyList" border stripe>
          <el-table-column prop="id" label="ID" width="80"></el-table-column>
          <el-table-column prop="sampleId" label="样本ID" width="120"></el-table-column>
          <el-table-column prop="confidence" label="置信度" width="100">
            <template #default="scope">
              <el-tag type="danger" v-if="scope.row.confidence < 0.6">
                {{ (scope.row.confidence * 100).toFixed(1) }}%
              </el-tag>
              <span v-else>{{ (scope.row.confidence * 100).toFixed(1) }}%</span>
            </template>
          </el-table-column>
          <el-table-column prop="prediction" label="预测结果" width="100"></el-table-column>
          <el-table-column prop="features" label="关键特征"></el-table-column>
          <el-table-column label="操作" width="100" align="center">
            <template #default="scope">
              <el-button type="primary" size="small" @click="viewSample(scope.row)">
                查看
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
    
    <!-- 导出报表 -->
    <div style="margin: 20px 0">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>导出报表</span>
          </div>
        </template>
        <el-form label-width="120px">
          <el-form-item label="导出格式">
            <el-radio-group v-model="exportFormat">
              <el-radio value="pdf">PDF</el-radio>
              <el-radio value="excel">Excel</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="包含内容">
            <el-checkbox-group v-model="exportContent">
              <el-checkbox value="风险分布">风险分布</el-checkbox>
              <el-checkbox value="特征重要性">特征重要性</el-checkbox>
              <el-checkbox value="异常样本">异常样本</el-checkbox>
              <el-checkbox value="结论摘要">结论摘要</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="exportReport">
              导出报表
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

export default {
  name: "GroupAnalysis",
  data() {
    return {
      dateRange: [],
      dataSource: 'all',
      selectedFiles: [],
      showCompareChart: false,
      exportFormat: 'pdf',
      exportContent: ['风险分布', '特征重要性', '结论摘要'],
      fileList: [
        { id: 1, name: 'diabetes_data.csv' },
        { id: 2, name: 'new_patients.xlsx' },
        { id: 3, name: 'test_data.csv' }
      ],
      anomalyList: [
        { id: 1, sampleId: 'S001', confidence: 0.55, prediction: '糖尿病', features: '血糖: 180, BMI: 35' },
        { id: 2, sampleId: 'S002', confidence: 0.58, prediction: '非糖尿病', features: '血糖: 100, BMI: 22' },
        { id: 3, sampleId: 'S003', confidence: 0.52, prediction: '糖尿病', features: '血糖: 160, BMI: 30' }
      ]
    };
  },
  mounted() {
    this.initCharts();
  },
  methods: {
    applyFilter() {
      ElMessage.info("筛选条件已应用");
    },
    compareFiles() {
      this.showCompareChart = true;
      ElMessage.info("文件对比已生成");
    },
    exportAnomalies() {
      ElMessage.info("异常样本导出中...");
    },
    exportReport() {
      ElMessage.info("报表导出中...");
    },
    viewSample(row) {
      ElMessage.info(`查看样本: ${row.sampleId}`);
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

.chart-container {
  display: flex;
  gap: 20px;
}

.chart-item {
  flex: 1;
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

h3 {
  margin-bottom: 10px;
  font-size: 14px;
  color: #333;
}
</style>