import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const request = axios.create({
    baseURL: 'http://localhost:8081',
    timeout: 60000
})

// 请求拦截器
request.interceptors.request.use(
    config => {
        const userStore = useUserStore()
        const token = userStore.token
        if (token && token.trim()) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    response => {
        const contentType = response.headers['content-type']
        // 如果是 blob 响应（PDF），直接返回
        if (contentType && (contentType.includes('application/pdf') || contentType.includes('octet-stream'))) {
            return response.data
        }

        const res = response.data
        if (res && res.code === 200) {
            return res
        } else if (res) {
            const msg = res.msg || res.message || '未知错误'
            return Promise.reject(new Error(msg))
        } else {
            return Promise.reject(new Error('无效响应'))
        }
    },
    async error => {
        let msg = error.message
        if (error.response?.data instanceof Blob) {
            // 处理 blob 错误响应
            const textDecoder = new TextDecoder('utf-8')
            const errorText = textDecoder.decode(await error.response.data.arrayBuffer())
            try {
                const errorJson = JSON.parse(errorText)
                msg = errorJson.msg || errorJson.message || msg
            } catch (e) {
                msg = errorText || msg
            }
        } else if (error.response?.status === 401) {
            // 处理 token 过期或未授权
            msg = '登录已过期，请重新登录'
            const userStore = useUserStore()
            userStore.clearUserInfo()
            // 跳转到登录页面
            window.location.href = '/login'
        }
        ElMessage.error(msg || '网络请求失败')
        return Promise.reject(error)
    }
)

export default request