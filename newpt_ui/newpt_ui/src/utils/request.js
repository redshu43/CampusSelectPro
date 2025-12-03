// src/utils/request.js
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 1. 创建 axios 实例
const request = axios.create({
    // 基础路径：以后你发请求直接写 '/auth/login' 就行，不用写 '/api' 了
    // (前提：这里配合 Vite 的代理使用)
    baseURL: '/api',
    timeout: 5000 // 请求超时时间：5秒
})

// 2. 【核心】请求拦截器 (Request Interceptor)
//    作用：在请求发送出去之前，自动带上 Token
request.interceptors.request.use(
    (config) => {
        // 从浏览器缓存中取出 Token
        const token = localStorage.getItem('token')

        // 如果有 Token，就加到 Header 里
        if (token) {
            // 注意：后端只要 Bearer 后面的空格，这里要拼好
            config.headers.Authorization = `Bearer ${token}`
        }

        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// 3. 【核心】响应拦截器 (Response Interceptor)
//    作用：统一处理后端返回的错误 (比如 Token 过期)
request.interceptors.response.use(
    (response) => {
        // 后端返回的 JSON 数据
        const res = response.data

        // 如果 state 不是 200，说明逻辑上有错误
        if (res.state !== 200) {
            ElMessage.error(res.message || '系统错误')

            // 特殊处理：401 说明 Token 过期或无效
            if (res.state === 401) {
                localStorage.removeItem('token') // 清除坏 Token
                window.location.href = '/login'  // 强制踢回登录页
            }

            return Promise.reject(new Error(res.message || 'Error'))
        }

        // 如果成功，直接返回 res，这样你在页面里就不用写 response.data 了
        return res
    },
    (error) => {
        // 处理网络错误 (如 404, 500)
        console.error('err' + error)
        ElMessage.error(error.message || '网络请求失败')
        return Promise.reject(error)
    }
)

// 4. 导出这个封装好的实例
export default request