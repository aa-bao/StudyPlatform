import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
    baseURL: 'http://localhost:8081',
    timeout: 5000
})

// 响应拦截器
request.interceptors.response.use(
    response => {
        const res = response.data
        if (res.code === 200) {
            return res
        } else {
            ElMessage.error(res.msg || '错误')
            return Promise.reject(new Error(res.msg || '错误'))
        }
    },
    error => {
        ElMessage.error(error.message || '网络请求失败')
        return Promise.reject(error)
    }
)

export default request