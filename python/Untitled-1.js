import request from '@/utils/request'  // 导入 request 工具

export default {
  // ...
  methods: {
    getUser() {
      let username = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).username : ""
      if (username) {
        // 直接使用导入的 request
        request.get("/user/username/" + username).then(res => {
          this.user = res.data
        })
      }
    }
  }
}