import request from '../utils/request'

// 登录
export function loginApi(data) {
    return request({
        url: '/user/login',
        method: 'post',
        data
    })
}

// 注册
export function registerApi(data) {
    return request({
        url: '/user/register',
        method: 'post',
        data
    })
}

// 更新个人信息
export function updateUserApi(data) {
    return request({
        url: '/user/update',
        method: 'post',
        data
    })
}

// 修改密码
export function updatePwdApi(data) {
    return request({
        url: '/user/updatePwd',
        method: 'post',
        data
    })
}

// 上传头像
export function uploadAvatarApi(formData) {
    return request({
        url: '/file/upload', 
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

// 获取用户信息（根据userId）
export function getUserInfoApi(userId) {
    return request({
        url: '/user/userInfo',
        method: 'get',
        params: {
            userId
        }
    })
}

// 获取用户首页数据
export function getHomePageDataApi(userId) {
    return request({
        url: `/user/homeData/${userId}`,
        method: 'get'
    })
}

// 获取用户学习统计数据
export function getUserStudyStatsApi(userId) {
    return request({
        url: `/user/studyStats/${userId}`,
        method: 'get'
    })
}

// 获取用户错题本
export function getErrorBookApi(userId) {
    return request({
        url: '/question/getErrorBook',
        method: 'get',
        params: {
            userId
        }
    })
}

// 获取每日测试题目
export function getDailyTestQuestionsApi(userId) {
    return request({
        url: '/record/daily-test/questions',
        method: 'get',
        params: {
            userId
        }
    })
}

// 获取每日测试状态（今日是否已完成）
export function getDailyTestStatusApi(userId) {
    return request({
        url: '/record/daily-test/status',
        method: 'get',
        params: {
            userId
        }
    })
}

// 获取每日测试正确率统计
export function getDailyTestAccuracyStatsApi(userId) {
    return request({
        url: '/record/daily-test/accuracy-stats',
        method: 'get',
        params: {
            userId
        }
    })
}

// ==================== Dashboard API ====================

// 获取高频错题
export function getHotMistakesApi(userId, limit = 5) {
    return request({
        url: '/record/hot-mistakes',
        method: 'get',
        params: { userId, limit }
    })
}

// 获取今日统计
export function getTodayStatsApi(userId) {
    return request({
        url: '/record/today-stats',
        method: 'get',
        params: { userId }
    })
}

// 获取智能学习建议
export function getRecommendationsApi(userId) {
    return request({
        url: `/user/recommendations/${userId}`,
        method: 'get'
    })
}

// 获取最近答题记录(用于趋势图)
export function getRecentRecordsApi(userId, days = 7) {
    return request({
        url: '/record/recent',
        method: 'get',
        params: { userId, limit: days * 20 } // 假设每天最多20条
    })
}
