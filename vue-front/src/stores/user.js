import {
    defineStore
} from 'pinia'

export const useUserStore = defineStore('user', {
    // 定义状态：存储用户信息和 token
    state: () => ({
        userInfo: JSON.parse(localStorage.getItem('user') || '{}'),
        token: localStorage.getItem('token') || ''
    }),

    actions: {
        // 设置用户信息并持久化到本地
        setUserInfo(userInfo) {
            this.userInfo = userInfo
            localStorage.setItem('user', JSON.stringify(userInfo))
        },

        // 设置 token 并持久化到本地
        setToken(token) {
            this.token = token
            localStorage.setItem('token', token)
        },

        // 退出登录时清除信息
        clearUserInfo() {
            this.userInfo = {}
            this.token = ''
            localStorage.removeItem('user')
            localStorage.removeItem('role')
            localStorage.removeItem('token')
        }
    },

    getters: {
        // 快速获取用户角色
        role: (state) => state.userInfo.role || localStorage.getItem('role') || '',
        // 检查是否已登录
        isLoggedIn: (state) => !!state.token && Object.keys(state.userInfo).length > 0
    }
})
