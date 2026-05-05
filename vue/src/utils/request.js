import axios from 'axios'
import router from "@/router";
import { CacheHelper } from "./cacheHelper";

export const serverIp = window.location.hostname || 'localhost'

const request = axios.create({
    baseURL: `http://${serverIp}:9090`,
    timeout: 60000
})

// 请求拦截器
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';

    const userStr = CacheHelper.get('user')
    let user = null
    if (userStr) {
      try {
        user = JSON.parse(userStr)
      } catch (e) {
        console.error('解析用户信息失败:', e)
      }
    }

    if (user && user.token) {
        config.headers['token'] = user.token;
    }

    return config
}, error => Promise.reject(error))

// 响应拦截器
request.interceptors.response.use(
    response => {
        let res = response.data;

        // 文件下载
        if (response.config.responseType === 'blob') {
            return res
        }

        // 安全解析 JSON
        if (typeof res === 'string') {
            try {
                res = res ? JSON.parse(res) : res
            } catch (e) {
                return res
            }
        }

        // 登录失效处理
        if (res.code === '401') {
            const noAuthPaths = ['/user/register', '/user/login', '/user/checkUsername'];
            const isNoAuth = noAuthPaths.some(path =>
                response.config.url.includes(path)
            );

            if (!isNoAuth) {
                router.push("/login")
            }
        }

        return res;
    },
    error => {
        console.error('接口错误:', error)
        return Promise.reject(error)
    }
)

export default request