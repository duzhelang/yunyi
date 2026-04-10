<template>
  <div class="treatment-record-container" style="padding: 20px;">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="患者姓名">
          <el-input v-model="queryForm.patientName" placeholder="请输入患者姓名" clearable></el-input>
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="queryForm.idCard" placeholder="请输入身份证号" clearable></el-input>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="queryForm.phone" placeholder="请输入联系电话" clearable></el-input>
        </el-form-item>
        <el-form-item label="负责医生">
          <el-input v-model="queryForm.doctorName" placeholder="请输入医生姓名" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮栏 -->
    <el-card class="table-card" style="margin-top: 20px;">
      <div style="margin-bottom: 15px;">
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增档案</el-button>
        <el-button type="success" icon="el-icon-download" @click="downloadTemplate">下载模板</el-button>
        <el-upload
            class="upload-inline"
            :action="`http://${serverIp}:9090/treatment-record/import`"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleImportSuccess"
            accept=".xlsx,.xls"
        >
          <el-button type="warning" icon="el-icon-upload2">批量导入</el-button>
        </el-upload>
        <el-button type="info" icon="el-icon-export" @click="handleExport">批量导出</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" border stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="patientName" label="患者姓名" width="120"></el-table-column>
        <el-table-column prop="idCard" label="身份证号" width="180"></el-table-column>
        <el-table-column prop="phone" label="联系电话" width="130"></el-table-column>
        <el-table-column prop="gender" label="性别" width="60"></el-table-column>
        <el-table-column prop="age" label="年龄" width="60"></el-table-column>
        <el-table-column prop="bloodSugar" label="血糖值(mmol/L)" width="120"></el-table-column>
        <el-table-column prop="diagnosisResult" label="诊断结果" width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="doctorName" label="负责医生" width="100"></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160"></el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件(新增完善) -->
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="queryForm.pageNum"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="queryForm.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          style="margin-top: 20px; text-align: right;">
      </el-pagination>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="700px">
      <el-form :model="formData" :rules="formRules" ref="formData" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="患者姓名" prop="patientName">
              <el-input v-model="formData.patientName" placeholder="请输入患者姓名"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="formData.idCard" placeholder="请输入身份证号"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入联系电话"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="formData.gender" placeholder="请选择性别" style="width: 100%">
                <el-option label="男" value="男"></el-option>
                <el-option label="女" value="女"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="formData.age" :min="0" :max="150" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="血糖值(mmol/L)" prop="bloodSugar">
              <el-input-number v-model="formData.bloodSugar" :min="0" :step="0.01" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="诊断结果" prop="diagnosisResult">
          <el-input type="textarea" v-model="formData.diagnosisResult" :rows="2" placeholder="请输入诊断结果"></el-input>
        </el-form-item>
        <el-form-item label="治疗方案" prop="treatmentPlan">
          <el-input type="textarea" v-model="formData.treatmentPlan" :rows="2" placeholder="请输入治疗方案"></el-input>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="负责医生" prop="doctorName">
              <el-input v-model="formData.doctorName" placeholder="请输入负责医生姓名"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="formData.remark" placeholder="请输入备注"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
// 与项目统一导入服务器IP配置
import {serverIp} from "../../public/config";

export default {
  name: 'TreatmentRecord',
  data() {
    return {
      serverIp: serverIp,
      loading: false,
      total: 0,
      // 查询表单(新增分页参数)
      queryForm: {
        patientName: '',
        idCard: '',
        phone: '',
        doctorName: '',
        pageNum: 1,
        pageSize: 10
      },
      // 表格数据
      tableData: [],
      // 弹窗相关
      dialogVisible: false,
      dialogTitle: '新增诊疗档案',
      isEdit: false,
      // 表单数据
      formData: {
        id: '',
        patientName: '',
        idCard: '',
        phone: '',
        gender: '男',
        age: null,
        bloodSugar: null,
        diagnosisResult: '',
        treatmentPlan: '',
        doctorName: '',
        remark: '',
        createTime: ''
      },
      // 表单校验规则
      formRules: {
        patientName: [{required: true, message: '患者姓名不能为空', trigger: 'blur'}],
        idCard: [{required: true, message: '身份证号不能为空', trigger: 'blur'}],
        phone: [{required: true, message: '联系电话不能为空', trigger: 'blur'}],
        doctorName: [{required: true, message: '负责医生不能为空', trigger: 'blur'}],
        diagnosisResult: [{required: true, message: '诊断结果不能为空', trigger: 'blur'}],
        treatmentPlan: [{required: true, message: '治疗方案不能为空', trigger: 'blur'}]
      },
      // 上传请求头
      uploadHeaders: {
        'Authorization': localStorage.getItem('token') || ''
      },
      // 防止重复请求的标志
      isRequesting: false,
      // 防止重复提交的标志
      isSubmitting: false
    }
  },
  // 修复调用时机,避免实例未初始化
  mounted() {
    this.getList();
  },
  methods: {
    // ===================== 核心:查询列表(修复request空指针和重复请求) =====================
    async getList() {
      // 防止重复请求
      if (this.isRequesting) {
        return;
      }
      
      // 空值兜底,彻底解决 Cannot read properties of undefined (reading 'get')
      if (!this.request) {
        this.$message.error('请求实例初始化失败,请刷新页面重试')
        return
      }
      
      this.isRequesting = true;
      this.loading = true;
      
      try {
        // 统一使用 this.request 与项目规范一致
        const res = await this.request.get('/treatment-record/list', {params: this.queryForm});
        // 后端返回的code是字符串类型,需要转换为字符串比较
        if (res.code === '200' || res.code === 200) {
          this.tableData = res.data.records;
          this.total = res.data.total;
        } else {
          // 只在非重复请求时显示错误消息
          if (!this.isRequesting) {
            this.$message.error(res.msg || '查询失败');
          }
        }
      } catch (error) {
        console.error("查询异常:", error)
        // 只在非重复请求时显示错误消息
        if (!this.isRequesting) {
          this.$message.error('查询失败');
        }
      } finally {
        this.loading = false;
        // 延迟重置请求标志,防止快速重复点击
        setTimeout(() => {
          this.isRequesting = false;
        }, 300);
      }
    },

    // 重置查询
    resetQuery() {
      this.queryForm = {
        patientName: '',
        idCard: '',
        phone: '',
        doctorName: '',
        pageNum: 1,
        pageSize: 10
      };
      this.getList();
    },

    // 分页事件
    handleSizeChange(val) {
      this.queryForm.pageSize = val;
      this.getList();
    },
    handleCurrentChange(val) {
      this.queryForm.pageNum = val;
      this.getList();
    },

    // 新增
    handleAdd() {
      this.isEdit = false;
      this.dialogTitle = '新增诊疗档案';
      this.formData = {
        id: '',
        patientName: '',
        idCard: '',
        phone: '',
        gender: '男',
        age: null,
        bloodSugar: null,
        diagnosisResult: '',
        treatmentPlan: '',
        doctorName: '',
        remark: ''
      };
      this.dialogVisible = true;
      this.$nextTick(() => {
        this.$refs.formData.resetFields();
      })
    },

    // 编辑
    handleEdit(row) {
      this.isEdit = true;
      this.dialogTitle = '编辑诊疗档案';
      this.formData = {...row};
      this.dialogVisible = true;
    },

    // 提交表单
    submitForm() {
      // 防止重复提交
      if (this.isSubmitting) {
        return;
      }
      
      this.$refs.formData.validate(async (valid) => {
        if (!valid) return;
        
        this.isSubmitting = true;
        
        try {
          let res;
          if (this.isEdit) {
            res = await this.request.post('/treatment-record/update', this.formData);
          } else {
            res = await this.request.post('/treatment-record/add', this.formData);
          }
          // 后端返回的code是字符串类型,需要转换为字符串比较
          if (res.code === '200' || res.code === 200) {
            this.$message.success('操作成功');
            this.dialogVisible = false;
            this.getList();
          } else {
            this.$message.error(res.msg || '操作失败');
          }
        } catch (error) {
          this.$message.error('操作失败');
          console.error(error)
        } finally {
          // 延迟重置提交标志
          setTimeout(() => {
            this.isSubmitting = false;
          }, 500);
        }
      });
    },

    // 删除
    handleDelete(row) {
      this.$confirm('确定要删除该诊疗档案吗?', '提示', {
        type: 'warning'
      }).then(async () => {
        try {
          const res = await this.request.delete(`/treatment-record/delete/${row.id}`);
          // 后端返回的code是字符串类型,需要转换为字符串比较
          if (res.code === '200' || res.code === 200) {
            this.$message.success('删除成功');
            this.getList();
          } else {
            this.$message.error(res.msg || '删除失败');
          }
        } catch (error) {
          this.$message.error('删除失败');
        }
      });
    },

    // 下载模板(拼接完整后端地址)
    downloadTemplate() {
      window.open(`http://${serverIp}:9090/treatment-record/download-template`);
    },

    // 导入成功回调
    handleImportSuccess(res) {
      // 后端返回的code是字符串类型,需要转换为字符串比较
      if (res.code === '200' || res.code === 200) {
        const msgList = res.data;
        this.$alert(msgList.join('\n'), '导入结果', {
          confirmButtonText: '确定',
          type: msgList.length > 1 ? 'warning' : 'success'
        });
        this.getList();
      } else {
        this.$message.error(res.msg || '导入失败');
      }
    },

    // 导出数据
    handleExport() {
      let params = new URLSearchParams(this.queryForm).toString();
      window.open(`http://${serverIp}:9090/treatment-record/export?${params}`);
    }
  }
}
</script>

<style scoped>
/* 搜索卡片样式 */
.search-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 表格卡片样式 */
.table-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 搜索表单对齐 */
.search-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

/* 上传按钮内联样式 */
.upload-inline {
  display: inline-block;
  margin-left: 10px;
}

/* 弹窗底部按钮样式 */
.dialog-footer {
  text-align: right;
}

/* 表单间距优化 */
.el-form-item {
  margin-bottom: 18px;
}
</style>