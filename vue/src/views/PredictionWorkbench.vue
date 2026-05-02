<template>
  <div>
    <h2>预测工作台</h2>
    
    <!-- 模型选择器 -->
    <div style="margin: 20px 0">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>模型选择</span>
          </div>
        </template>
        <el-form label-width="120px">
          <el-form-item label="模型版本">
            <el-select v-model="selectedModel" placeholder="请选择模型">
              <el-option 
                v-for="model in modelList" 
                :key="model.version"
                :label="model.name" 
                :value="model.version"
              >
                <div class="model-option">
                  <div class="model-name">{{ model.name }}</div>
                  <div class="model-desc">{{ model.description }}</div>
                </div>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="预测参数">
            <el-input-number v-model="threshold" :min="0" :max="1" :step="0.05" placeholder="阈值"></el-input-number>
          </el-form-item>
          <el-form-item label="输出格式">
            <el-radio-group v-model="outputFormat">
              <el-radio label="json">JSON</el-radio>
              <el-radio label="csv">CSV</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="生成报告">
            <el-switch v-model="generateReport"></el-switch>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
    
    <!-- 任务卡片列表 -->
    <div style="margin: 20px 0">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>任务列表</span>
            <el-button type="primary" size="small" @click="loadTasks">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </template>
        <div class="task-card-container">
          <el-card 
            v-for="task in taskList" 
            :key="task.id"
            class="task-card"
            shadow="hover"
          >
            <template #header>
              <div class="task-header">
                <span class="task-name">{{ task.name }}</span>
                <el-tag :type="task.status === '已完成' ? 'success' : task.status === '进行中' ? 'warning' : 'info'">
                  {{ task.status }}
                </el-tag>
              </div>
            </template>
            <div class="task-info">
              <div class="info-item">
                <span class="label">文件大小:</span>
                <span class="value">{{ task.size }}kb</span>
              </div>
              <div class="info-item">
                <span class="label">样本数:</span>
                <span class="value">{{ task.sampleCount }}</span>
              </div>
              <div class="info-item">
                <span class="label">上传时间:</span>
                <span class="value">{{ task.uploadTime }}</span>
              </div>
              <div class="info-item">
                <span class="label">上次预测:</span>
                <span class="value">{{ task.lastPrediction || '未预测' }}</span>
              </div>
            </div>
            <div class="task-progress" v-if="task.status === '进行中'">
              <el-progress :percentage="task.progress" :format="formatProgress"></el-progress>
              <div class="progress-text">{{ task.progressText }}</div>
            </div>
            <div class="task-actions">
              <el-button 
                type="primary" 
                size="small" 
                @click="startPrediction(task)"
                :disabled="task.status === '进行中'"
              >
                开始预测
              </el-button>
              <el-button 
                type="success" 
                size="small" 
                class="ml-2" 
                @click="viewGroupAnalysis(task)"
                :disabled="task.status !== '已完成'"
              >
                查看群体分析
              </el-button>
              <el-button 
                type="info" 
                size="small" 
                class="ml-2" 
                @click="viewIndividualInsight(task)"
                :disabled="task.status !== '已完成'"
              >
                查看个体洞察
              </el-button>
            </div>
          </el-card>
        </div>
      </el-card>
    </div>
    
    <!-- 任务历史 -->
    <div style="margin: 20px 0">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>任务历史</span>
          </div>
        </template>
        <el-table :data="historyList" border stripe>
          <el-table-column prop="id" label="ID" width="80"></el-table-column>
          <el-table-column prop="taskName" label="任务名称"></el-table-column>
          <el-table-column prop="modelVersion" label="模型版本" width="120"></el-table-column>
          <el-table-column prop="parameters" label="参数" width="150"></el-table-column>
          <el-table-column prop="predictionTime" label="预测时间" width="180"></el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === '成功' ? 'success' : 'danger'">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" align="center">
            <template #default="scope">
              <el-button type="primary" size="small" @click="viewHistoryResult(scope.row)">
                查看结果
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import { Refresh } from '@element-plus/icons-vue';

export default {
  name: "PredictionWorkbench",
  components: { Refresh },
  data() {
    return {
      selectedModel: 'v2.1.0',
      threshold: 0.5,
      outputFormat: 'json',
      generateReport: true,
      modelList: [
        {
          version: 'v2.1.0',
          name: '糖尿病预测模型 v2.1.0',
          description: '基于神经网络的二分类模型，准确率92%'
        },
        {
          version: 'v2.2.0',
          name: '糖尿病预测模型 v2.2.0',
          description: '优化版模型，添加了特征工程，准确率94%'
        }
      ],
      taskList: [
        {
          id: 1,
          name: 'diabetes_data.csv',
          size: 128,
          sampleCount: 768,
          uploadTime: '2026-04-19 14:30:00',
          lastPrediction: '2026-04-19 15:00:00',
          status: '已完成',
          progress: 100,
          progressText: '预测完成'
        },
        {
          id: 2,
          name: 'new_patients.xlsx',
          size: 256,
          sampleCount: 1200,
          uploadTime: '2026-04-20 10:00:00',
          lastPrediction: null,
          status: '待预测',
          progress: 0,
          progressText: '等待开始'
        },
        {
          id: 3,
          name: 'test_data.csv',
          size: 64,
          sampleCount: 300,
          uploadTime: '2026-04-20 11:00:00',
          lastPrediction: null,
          status: '进行中',
          progress: 65,
          progressText: '预计剩余 20 秒'
        }
      ],
      historyList: [
        {
          id: 1,
          taskName: 'diabetes_data.csv',
          modelVersion: 'v2.1.0',
          parameters: '{"threshold": 0.5}',
          predictionTime: '2026-04-19 15:00:00',
          status: '成功'
        },
        {
          id: 2,
          taskName: 'diabetes_data.csv',
          modelVersion: 'v2.2.0',
          parameters: '{"threshold": 0.5}',
          predictionTime: '2026-04-19 16:30:00',
          status: '成功'
        }
      ]
    };
  },
  methods: {
    loadTasks() {
      // 模拟加载任务
      ElMessage.info("任务列表已刷新");
    },
    startPrediction(task) {
      // 模拟开始预测
      task.status = '进行中';
      task.progress = 0;
      task.progressText = '开始预测...';
      
      // 模拟进度更新
      let interval = setInterval(() => {
        task.progress += 5;
        if (task.progress >= 100) {
          clearInterval(interval);
          task.status = '已完成';
          task.progressText = '预测完成';
          task.lastPrediction = new Date().toLocaleString();
          ElMessage.success("预测完成");
        } else {
          task.progressText = `预计剩余 ${Math.ceil((100 - task.progress) / 5) * 2} 秒`;
        }
      }, 1000);
    },
    viewGroupAnalysis(task) {
      this.$router.push({
        path: '/groupAnalysis',
        query: { taskId: task.id }
      });
    },
    viewIndividualInsight(task) {
      this.$router.push({
        path: '/individualInsight',
        query: { taskId: task.id }
      });
    },
    viewHistoryResult(row) {
      ElMessage.info(`查看历史结果: ${row.taskName}`);
    },
    formatProgress(percentage) {
      return `${percentage}%`;
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

.task-card-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.task-card {
  margin-bottom: 0;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-name {
  font-weight: bold;
}

.task-info {
  margin: 15px 0;
}

.info-item {
  margin: 5px 0;
  display: flex;
}

.label {
  width: 100px;
  color: #666;
}

.value {
  font-weight: 500;
}

.task-progress {
  margin: 15px 0;
}

.progress-text {
  text-align: center;
  margin-top: 5px;
  font-size: 12px;
  color: #666;
}

.task-actions {
  display: flex;
  margin-top: 15px;
}

.ml-2 {
  margin-left: 8px;
}

.model-option {
  padding: 5px;
}

.model-name {
  font-weight: bold;
}

.model-desc {
  font-size: 12px;
  color: #666;
  margin-top: 2px;
}
</style>