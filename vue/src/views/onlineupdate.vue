<template>
  <el-upload
      :action="'http://' + serverIp + ':9090/python/upload'"
      :on-success="onSuccess"
      class="upload-demo"
      drag
      multiple>
    <i class="el-icon-upload"></i>
    <div class="el-upload__text">将训练集拖到此处,或<em>点击上传</em></div>
    <div class="el-upload__tip" slot="tip">请上传训练集</div>
  </el-upload>
</template>

<script>
import { serverIp } from "../../public/config";
export default {
  name: "update",
  data() {
    return {
      serverIp: serverIp,
    }
  },
  methods: {
    onSuccess() {
      this.$router.push('/file');
    },
  }
}
</script>

<style scoped>
.upload-demo {
  width: 100%;
  max-width: 600px;
  margin: 40px auto;
  padding: 45px 20px;
  border: 2px dashed #c9cdD4;
  border-radius: 12px;
  text-align: center;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  background-color: #f9fafb;
}

.upload-demo:hover {
  border-color: #2ecc71;
  background-color: #f0fdf4;
  transform: translateY(-4px);
  box-shadow: 0 6px 18px rgba(46, 204, 113, 0.12);
}

.el-icon-upload {
  font-size: 52px;
  color: #2ecc71;
  margin-bottom: 22px;
  transition: transform 0.3s ease;
}

.upload-demo:hover .el-icon-upload {
  transform: scale(1.08) rotate(5deg);
}

.el-upload__text {
  font-size: 16px;
  color: #4b5563;
  margin-bottom: 18px;
  line-height: 1.6;
}

.el-upload__text em {
  color: #2ecc71;
  font-style: normal;
  font-weight: 500;
  cursor: pointer;
  text-decoration: underline;
  padding: 0 3px;
}

.el-upload__tip {
  color: #9ca3af;
  font-size: 14px;
  margin-top: 12px;
  padding: 0 25px;
  line-height: 1.5;
}

/* 适配小屏幕 */
@media (max-width: 768px) {
  .upload-demo {
    padding: 35px 15px;
    margin: 25px auto;
  }

  .el-icon-upload {
    font-size: 40px;
    margin-bottom: 18px;
  }

  .el-upload__text {
    font-size: 14px;
  }
}
</style>