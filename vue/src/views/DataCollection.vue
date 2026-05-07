<template>
  <div>
    <h2>数据采集</h2>
    <!-- 多格式上传 -->
    <div style="margin: 20px 0">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>多格式数据上传</span>
          </div>
        </template>
        <div class="upload-container">
          <el-upload
            class="upload-demo"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="handleFileUploadSuccess"
            :on-error="handleFileUploadError"
            :show-file-list="true"
            accept=".xlsx,.xls,.csv"
            :auto-upload="true"
          >
            <el-button type="primary">
              <el-icon><Upload /></el-icon>
              点击上传
            </el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持上传 Excel、CSV 文件
              </div>
            </template>
          </el-upload>
          
          <!-- 手动录入 -->
          <el-button type="success" class="ml-4" @click="showManualInput = true">
            <el-icon><Edit /></el-icon>
            手动录入
          </el-button>
          
          <!-- HIS系统导入 -->
          <el-button type="warning" class="ml-4">
            <el-icon><Link /></el-icon>
            HIS系统导入
          </el-button>
        </div>
      </el-card>
    </div>
    
    <!-- 数据质量检测 -->
    <div style="margin: 20px 0">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>数据质量检测</span>
          </div>
        </template>
        <el-table :data="qualityCheckData" border>
          <el-table-column prop="field" label="字段" width="120"></el-table-column>
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="scope.row.status === '正常' ? 'success' : 'danger'">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="message" label="提示信息"></el-table-column>
        </el-table>
      </el-card>
    </div>
    
    <!-- 数据预处理 -->
    <div style="margin: 20px 0">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>数据预处理</span>
          </div>
        </template>
        <el-form label-width="120px">
          <el-form-item label="缺失值填充策略">
            <el-select v-model="fillStrategy" placeholder="请选择">
              <el-option label="均值填充" value="mean"></el-option>
              <el-option label="中位数填充" value="median"></el-option>
              <el-option label="固定值填充" value="fixed"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="标准化选项">
            <el-select v-model="scaleOption" placeholder="请选择">
              <el-option label="StandardScaler" value="standard"></el-option>
              <el-option label="MinMaxScaler" value="minmax"></el-option>
              <el-option label="None" value="none"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="preprocessData">开始预处理</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
    
    <!-- 数据列表 -->
    <div style="margin: 20px 0">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>数据列表</span>
            <el-button type="primary" size="small" @click="loadData">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </template>
        <el-table :data="dataList" border stripe>
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="id" label="ID" width="80"></el-table-column>
          <el-table-column prop="name" label="文件名称"></el-table-column>
          <el-table-column prop="type" label="文件类型" width="100"></el-table-column>
          <el-table-column prop="size" label="文件大小(kb)" width="100"></el-table-column>
          <el-table-column prop="version" label="版本" width="80"></el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === '原始版' ? 'info' : 'success'">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="uploadTime" label="上传时间" width="180"></el-table-column>
          <el-table-column label="操作" width="200" align="center">
            <template #default="scope">
              <el-button type="primary" size="small" @click="previewData(scope.row)">
                预览
              </el-button>
              <el-button type="success" size="small" class="ml-2" @click="processData(scope.row)">
                处理
              </el-button>
              <el-button type="danger" size="small" class="ml-2" @click="deleteData(scope.row.id)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div style="margin-top: 10px">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
          ></el-pagination>
        </div>
      </el-card>
    </div>
    
    <!-- 手动录入对话框 -->
    <el-dialog title="手动录入数据" v-model="showManualInput" width="60%">
      <el-form :model="manualData" label-width="120px">
        <el-form-item label="怀孕次数">
          <el-input v-model.number="manualData.Pregnancies"></el-input>
        </el-form-item>
        <el-form-item label="葡萄糖">
          <el-input v-model.number="manualData.Glucose"></el-input>
        </el-form-item>
        <el-form-item label="血压">
          <el-input v-model.number="manualData.BloodPressure"></el-input>
        </el-form-item>
        <el-form-item label="皮肤厚度">
          <el-input v-model.number="manualData.SkinThickness"></el-input>
        </el-form-item>
        <el-form-item label="胰岛素">
          <el-input v-model.number="manualData.Insulin"></el-input>
        </el-form-item>
        <el-form-item label="BMI">
          <el-input v-model.number="manualData.BMI"></el-input>
        </el-form-item>
        <el-form-item label="糖尿病家族史">
          <el-input v-model.number="manualData.DiabetesPedigreeFunction"></el-input>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input v-model.number="manualData.Age"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showManualInput = false">取消</el-button>
          <el-button type="primary" @click="saveManualData">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import { Upload, Edit, Link, Refresh } from '@element-plus/icons-vue';

export default {
  name: "DataCollection",
  components: { Upload, Edit, Link, Refresh },
  data() {
    const userStr = localStorage.getItem('user') || sessionStorage.getItem('user')
    let token = ''
    if (userStr) {
      try { token = JSON.parse(userStr).token || '' } catch (e) {}
    }
    return {
      serverIp: window.config ? window.config.serverIp : 'localhost',
      uploadUrl: 'http://' + this.serverIp + ':9090/DataTest/upload',
      uploadHeaders: { token: token },
      showManualInput: false,
      fillStrategy: 'mean',
      scaleOption: 'standard',
      dataList: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      qualityCheckData: [
        { field: 'Pregnancies', status: '正常', message: '无异常' },
        { field: 'Glucose', status: '警告', message: '存在异常值: 520' },
        { field: 'BloodPressure', status: '正常', message: '无异常' },
        { field: 'SkinThickness', status: '正常', message: '无异常' },
        { field: 'Insulin', status: '正常', message: '无异常' },
        { field: 'BMI', status: '正常', message: '无异常' },
        { field: 'DiabetesPedigreeFunction', status: '正常', message: '无异常' },
        { field: 'Age', status: '正常', message: '无异常' }
      ],
      manualData: {
        Pregnancies: 0,
        Glucose: 0,
        BloodPressure: 0,
        SkinThickness: 0,
        Insulin: 0,
        BMI: 0,
        DiabetesPedigreeFunction: 0,
        Age: 0
      }
    };
  },
  created() {
    this.loadData();
  },
  methods: {
    loadData() {
      request.get("/DataTest/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
      }).then(res => {
        if (res.code === '200') {
          this.dataList = res.data?.records || [];
          this.total = res.data?.total || 0;
        } else {
          ElMessage.error(res.msg || '加载失败');
        }
      }).catch(err => {
        console.error(err);
        ElMessage.error('网络请求失败');
      });
    },
    handleFileUploadSuccess(res) {
      if (res.code === '200') {
        ElMessage.success("上传成功");
        this.loadData();
      } else {
        ElMessage.error(res.msg || "上传失败");
      }
    },
    handleFileUploadError(err) {
      ElMessage.error("上传失败");
    },
    preprocessData() {
      ElMessage.info("预处理功能开发中...");
    },
    previewData(row) {
      ElMessage.info(`预览数据: ${row.name}`);
    },
    processData(row) {
      ElMessage.info(`处理数据: ${row.name}`);
    },
    deleteData(id) {
      request.delete(`/DataTest/${id}`).then(res => {
        if (res.code === '200') {
          ElMessage.success("删除成功");
          this.loadData();
        } else {
          ElMessage.error(res.msg || "删除失败");
        }
      });
    },
    saveManualData() {
      ElMessage.success("手动录入成功");
      this.showManualInput = false;
    },
    handleSizeChange(pageSize) {
      this.pageSize = pageSize;
      this.pageNum = 1;
      this.loadData();
    },
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum;
      this.loadData();
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

.upload-container {
  display: flex;
  align-items: center;
}

.ml-4 {
  margin-left: 16px;
}

.ml-2 {
  margin-left: 8px;
}
</style>