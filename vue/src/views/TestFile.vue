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
        <template slot-scope="scope">
          <el-tag :type="scope.row.enable === 1 ? 'success' : 'info'">
            {{ scope.row.enable === 1 ? '已完成' : '未预测' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="在线预测" width="100">
        <template slot-scope="scope">
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
        <template slot-scope="scope">
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
        <template slot-scope="scope">
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
        <template slot-scope="scope">
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
    };
  },
  created() {
    this.load();
  },
  methods: {
    load() {
      this.request.get("/DataTest/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        if (res.code === '200') {
          this.tableData = res.data.records;
          this.total = res.data.total;
        } else {
          this.$message.error(res.msg || '加载失败');
        }
      }).catch(err => {
        console.error(err);
        this.$message.error('网络请求失败');
      });
    },
    del(id) {
      this.request.delete(`/DataTest/${id}`).then(res => {
        if (res.code === '200') {
          this.$message.success("删除成功");
          this.load();
        } else {
          this.$message.error(res.msg || "删除失败");
        }
      });
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    delBatch() {
      if (this.multipleSelection.length === 0) {
        this.$message.warning("请至少选择一条数据");
        return;
      }
      const ids = this.multipleSelection.map(v => v.id);
      this.request.post("/DataTest/del/batch", ids).then(res => {
        if (res.code === '200') {
          this.$message.success("批量删除成功");
          this.load();
        } else {
          this.$message.error(res.msg || "批量删除失败");
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
        this.$message.success("上传成功");
        this.load();
      } else if (res.code === '302') {
        this.$message.error("只能上传 excel/csv 文件");
      } else {
        this.$message.error(res.msg || "上传失败");
      }
    },
    show(id) {
      this.request.get(`/DataTest/members/${id}`).then(res => {
        if (res.code === '200') {
          this.$router.push({
            path: "/Dashbord",
            query: { id }
          });
        } else {
          this.$message.error(res.msg || "查看结果失败");
        }
      });
    },
    star(url) {
      // 从 URL 中提取文件名
      const fileName = decodeURIComponent(url.substring(url.lastIndexOf('/') + 1));
      this.$message.info("正在调用 Python 进行预测,请稍候...");

      this.request.get(`/DataTest/getUrl/${fileName}`).then(res => {
        if (res.code === '200') {
          this.$message.success("预测成功!");
          this.load();
        } else if (res.code === '505') {
          this.$message.success(res.msg);
          this.load();
        } else {
          this.$message.error(res.msg || "预测失败,请查看控制台日志");
          this.load();
        }
      }).catch(err => {
        this.$message.error("预测请求异常");
        console.error(err);
      });
    },

    // ================= ⬇️️⬇️️⬇️ 核心修复:下载方法 ⬇️️⬇️️⬇️ =================
    download(jsonUrl) {
      if (!jsonUrl) {
        this.$message.warning("暂无可下载的文件,请先进行预测");
        return;
      }

      // 1. 提取纯净文件名 (防止 jsonUrl 包含路径导致下载文件名错误)
      const fileName = jsonUrl.substring(jsonUrl.lastIndexOf('/') + 1);

      this.$message.info("正在下载:" + fileName);

      // 2. 构建请求 URL
      const url = `/DataTest/${jsonUrl}`;
      console.log("发起下载请求:", url);

      // 3. 发送请求 (关键:responseType 必须设为 'blob')
      this.request.get(url, {
        responseType: 'blob'
      }).then(res => {
        // [智能兼容处理]
        // 情况 A: 拦截器未干扰,Blob 在 res.data 中 (标准 Axios 行为)
        // 情况 B: 拦截器已处理,直接返回了 Blob 对象 (res 本身就是 Blob)

        let blobData = null;

        if (res.data instanceof Blob) {
          blobData = res.data;
          console.log("检测到 Blob 在 res.data 中");
        } else if (res instanceof Blob) {
          blobData = res;
          console.log("检测到 res 本身就是 Blob (拦截器已处理)");
        } else {
          // 既不是 res.data 也不是 res,说明数据格式完全不对
          console.error("接收到的数据不是 Blob 对象:", res);
          this.$message.error("服务器返回数据格式错误,无法生成文件");
          return;
        }

        // 4. 额外检查:如果 Blob 大小为 0,可能是空文件
        if (blobData.size === 0) {
          this.$message.warning("下载的文件内容为空,请检查后端生成逻辑");
          return;
        }

        // 5. 创建 Blob 对象 (指定类型为 json)
        // 注意:这里直接用 blobData 即可,不需要再 new Blob([blobData]),除非需要转换类型
        // 但为了保险起见,如果后端返回的 type 不对,我们可以重新包装
        const finalBlob = new Blob([blobData], { type: 'application/json;charset=utf-8' });

        // 6. 创建临时下载链接
        const link = document.createElement('a');
        link.href = window.URL.createObjectURL(finalBlob);
        link.download = fileName; // 使用提取出的纯净文件名

        // 7. 触发下载
        document.body.appendChild(link);
        link.click();

        // 8. 清理内存
        document.body.removeChild(link);
        window.URL.revokeObjectURL(link.href);

        this.$message.success("下载成功");
      }).catch(error => {
        console.error("下载异常:", error);

        // 针对 HTTP 错误的处理
        if (error.response) {
          // 尝试读取后端返回的错误 JSON 信息 (即使是 blob 模式,错误有时也是文本)
          const reader = new FileReader();
          reader.onload = () => {
            try {
              const errObj = JSON.parse(reader.result);
              this.$message.error(errObj.msg || "下载失败");
            } catch (e) {
              // 解析失败,直接显示状态码
              let msg = "下载失败";
              if (error.response.status === 404) msg = "文件未找到 (404)";
              if (error.response.status === 500) msg = "服务器内部错误 (500)";
              this.$message.error(msg);
            }
          };
          // 注意:error.response.data 可能已经是 Blob,也可能不是,取决于拦截器
          if (error.response.data instanceof Blob) {
            reader.readAsText(error.response.data);
          } else {
            // 如果是普通对象或字符串,尝试直接转字符串
            reader.readAsText(new Blob([error.response.data]));
          }
        } else {
          this.$message.error("网络连接中断,请检查服务器");
        }
      });
    },
    // ================= 修复结束 =================
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