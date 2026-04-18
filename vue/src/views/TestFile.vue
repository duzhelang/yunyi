<template>
  <div>
    <!-- 搜索栏 -->
    <div style="margin: 10px 0">
      <el-input style="width: 200px" placeholder="请输入名称" suffix-icon="el-icon-search" v-model="name"></el-input>
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>

    <!-- 操作栏 -->
    <div style="margin: 10px 0">
      <el-upload
          :action="'http://' + serverIp + ':9090/DataTest/upload'"
          :show-file-list="false"
          accept=".xlsx,.xls,.csv"
          :on-success="handleFileUploadSuccess"
          style="display: inline-block"
      >
        <el-button type="primary" class="ml-5">上传文件 <i class="el-icon-top"></i></el-button>
      </el-upload>

      <el-popconfirm
          class="ml-5"
          confirm-button-text='确定'
          cancel-button-text='我再想想'
          icon="el-icon-info"
          icon-color="red"
          title="您确定批量删除这些数据吗?"
          @confirm="delBatch"
      >
        <el-button type="danger" slot="reference">批量删除 <i class="el-icon-remove-outline"></i></el-button>
      </el-popconfirm>
    </div>

    <!-- 表格 -->
    <el-table
        :data="tableData"
        border
        stripe
        :header-cell-class-name="'headerBg'"
        @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="文件名称" show-overflow-tooltip></el-table-column>
      <el-table-column prop="type" label="文件类型" width="100"></el-table-column>
      <el-table-column prop="size" label="文件大小(kb)" width="100"></el-table-column>

      <!-- 预测状态列 (优化显示) -->
      <el-table-column label="预测状态" width="100">
        <template v-slot="scope">
          <el-tag :type="scope.row.enable === 1 ? 'success' : 'info'">
            {{ scope.row.enable === 1 ? '已完成' : '未预测' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="在线预测" width="100">
        <template v-slot="scope">
          <el-button
              type="primary"
              size="mini"
              @click="star(scope.row.url)"
              :disabled="scope.row.enable === 1"
          >
            {{ scope.row.enable === 1 ? '已完成' : '开始预测' }}
          </el-button>
        </template>
      </el-table-column>

      <el-table-column label="查看结果" width="100">
        <template v-slot="scope">
          <el-button
              type="success"
              size="mini"
              @click="show(scope.row.id)"
              :disabled="scope.row.enable !== 1"
          >
            查看图表
          </el-button>
        </template>
      </el-table-column>

      <el-table-column label="下载结果" width="100">
        <template v-slot="scope">
          <el-button
              type="warning"
              size="mini"
              @click="download(scope.row.jsonUrl)"
              :disabled="!scope.row.jsonUrl || scope.row.enable !== 1"
          >
            下载 JSON
          </el-button>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="150" align="center">
        <template v-slot="scope">
          <el-popconfirm
              class="ml-5"
              confirm-button-text='确定'
              cancel-button-text='我再想想'
              icon="el-icon-info"
              icon-color="red"
              title="您确定删除吗?"
              @confirm="del(scope.row.id)"
          >
            <el-button type="danger" size="mini" slot="reference">删除 <i class="el-icon-remove-outline"></i></el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div style="padding: 10px 0">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[2, 5, 10, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
      ></el-pagination>
    </div>
  </div>
</template>

<script>
import { serverIp } from "../../public/config";
import request from "@/utils/request";
import { ElMessage } from "element-plus";

export default {
  name: "TestFile",
  data() {
    return {
      serverIp: serverIp,
      tableData: [],
      name: '',
      multipleSelection: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      isMounted: false
    };
  },
  created() {
    this.isMounted = true;
    this.load();
  },
  beforeUnmount() {
    this.isMounted = false;
  },
  methods: {
    load() {
      request.get("/DataTest/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        if (!this.isMounted) return;
        if (res.code === '200') {
          this.tableData = res.data?.records || [];
          this.total = res.data?.total || 0;
        } else {
          ElMessage.error(res.msg || '加载失败');
        }
      }).catch(err => {
        if (!this.isMounted) return;
        console.error(err);
        ElMessage.error('网络请求失败');
      });
    },
    del(id) {
      request.delete(`/DataTest/${id}`).then(res => {
        if (!this.isMounted) return;
        if (res.code === '200') {
          ElMessage.success("删除成功");
          this.load();
        } else {
          ElMessage.error(res.msg || "删除失败");
        }
      });
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    delBatch() {
      if (this.multipleSelection.length === 0) {
        ElMessage.warning("请至少选择一条数据");
        return;
      }
      const ids = this.multipleSelection.map(v => v.id);
      request.post("/DataTest/del/batch", ids).then(res => {
        if (!this.isMounted) return;
        if (res.code === '200') {
          ElMessage.success("批量删除成功");
          this.load();
        } else {
          ElMessage.error(res.msg || "批量删除失败");
        }
      });
    },
    reset() {
      this.name = "";
      this.pageNum = 1;
      this.load();
    },
    handleSizeChange(pageSize) {
      this.pageSize = pageSize;
      this.pageNum = 1;
      this.load();
    },
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum;
      this.load();
    },
    handleFileUploadSuccess(res) {
      if (res.code === '200') {
        ElMessage.success("上传成功");
        this.load();
      } else if (res.code === '302') {
        ElMessage.error("只能上传 excel/csv 文件");
      } else {
        ElMessage.error(res.msg || "上传失败");
      }
    },
    show(id) {
      request.get(`/DataTest/members/${id}`).then(res => {
        if (!this.isMounted) return;
        if (res.code === '200') {
          this.$router.push({
            path: "/Dashbord",
            query: { id }
          });
        } else {
          ElMessage.error(res.msg || "查看结果失败");
        }
      });
    },
    star(url) {
      const fileName = decodeURIComponent(url.substring(url.lastIndexOf('/') + 1));
      ElMessage.info("正在调用 Python 进行预测,请稍候...");

      request.get(`/DataTest/getUrl/${fileName}`).then(res => {
        if (!this.isMounted) return;
        if (res.code === '200') {
          ElMessage.success("预测成功!");
          this.load();
        } else if (res.code === '505') {
          ElMessage.success(res.msg);
          this.load();
        } else {
          ElMessage.error(res.msg || "预测失败,请查看控制台日志");
          this.load();
        }
      }).catch(err => {
        if (!this.isMounted) return;
        ElMessage.error("预测请求异常");
        console.error(err);
      });
    },

    download(jsonUrl) {
      if (!this.isMounted) return;
      if (!jsonUrl) {
        ElMessage.warning("暂无可下载的文件,请先进行预测");
        return;
      }

      const fileName = jsonUrl.substring(jsonUrl.lastIndexOf('/') + 1);

      ElMessage.info("正在下载:" + fileName);

      const url = `/DataTest/${jsonUrl}`;
      console.log("发起下载请求:", url);

      request.get(url, {
        responseType: 'blob'
      }).then(res => {
        if (!this.isMounted) return;
        let blobData = null;

        if (res.data instanceof Blob) {
          blobData = res.data;
          console.log("检测到 Blob 在 res.data 中");
        } else if (res instanceof Blob) {
          blobData = res;
          console.log("检测到 res 本身就是 Blob (拦截器已处理)");
        } else {
          console.error("接收到的数据不是 Blob 对象:", res);
          ElMessage.error("服务器返回数据格式错误,无法生成文件");
          return;
        }

        if (blobData.size === 0) {
          ElMessage.warning("下载的文件内容为空,请检查后端生成逻辑");
          return;
        }

        const finalBlob = new Blob([blobData], { type: 'application/json;charset=utf-8' });

        const link = document.createElement('a');
        link.href = window.URL.createObjectURL(finalBlob);
        link.download = fileName;

        document.body.appendChild(link);
        link.click();

        document.body.removeChild(link);
        window.URL.revokeObjectURL(link.href);

        ElMessage.success("下载成功");
      }).catch(error => {
        if (!this.isMounted) return;
        console.error("下载异常:", error);

        if (error.response) {
          const reader = new FileReader();
          reader.onload = () => {
            try {
              const errObj = JSON.parse(reader.result);
              ElMessage.error(errObj.msg || "下载失败");
            } catch (e) {
              let msg = "下载失败";
              if (error.response.status === 404) msg = "文件未找到 (404)";
              if (error.response.status === 500) msg = "服务器内部错误 (500)";
              ElMessage.error(msg);
            }
          };
          if (error.response.data instanceof Blob) {
            reader.readAsText(error.response.data);
          } else {
            reader.readAsText(new Blob([error.response.data]));
          }
        } else {
          ElMessage.error("网络连接中断,请检查服务器");
        }
      });
    },
  }
};
</script>

<style scoped>
.headerBg {
  background-color: #f5f7fa !important;
}
.ml-5 {
  margin-left: 5px;
}
</style>