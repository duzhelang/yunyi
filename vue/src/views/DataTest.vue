<template>
  <el-upload
      :action="'http://' + serverIp + ':9090/DataTest/upload'"
      :on-success="onSuccess"
      class="upload-demo"
      drag
      multiple>
    <i class="el-icon-upload"></i>
    <div class="el-upload__text">将预测集拖到此处,或<em>点击上传</em></div>
    <div class="el-upload__tip" slot="tip">请上传预测集</div>
  </el-upload>
</template>

<script>
export default {
  name: "DataTest",
  data() {
    return {
      serverIp: window.config ? window.config.serverIp : 'localhost',
    }
  },
  methods: {
    onSuccess() {
      this.$router.push('/test-file');
    },
  }
}
</script>

<style scoped>
.upload-demo {
  width: 100%;
  max-width: 600px;
  margin: 40px auto;
  padding: 40px 20px;
  border: 2px dashed #c0ccda;
  border-radius: 12px;
  text-align: center;
  transition: all 0.3s ease;
  background-color: #f7fafc;
}

.upload-demo:hover {
  border-color: #409eff;
  background-color: #f0f7ff;
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(64, 158, 255, 0.1);
}

.el-icon-upload {
  font-size: 48px;
  color: #409eff;
  margin-bottom: 20px;
  transition: transform 0.3s ease;
}

.upload-demo:hover .el-icon-upload {
  transform: scale(1.1);
}

.el-upload__text {
  font-size: 16px;
  color: #4e5969;
  margin-bottom: 15px;
  line-height: 1.5;
}

.el-upload__text em {
  color: #409eff;
  font-style: normal;
  font-weight: 500;
  cursor: pointer;
  text-decoration: underline;
}

.el-upload__tip {
  color: #8392a5;
  font-size: 14px;
  margin-top: 10px;
  padding: 0 20px;
}

/* 适配小屏幕 */
@media (max-width: 768px) {
  .upload-demo {
    padding: 30px 15px;
    margin: 20px auto;
  }

  .el-icon-upload {
    font-size: 36px;
    margin-bottom: 15px;
  }

  .el-upload__text {
    font-size: 14px;
  }
}
</style>