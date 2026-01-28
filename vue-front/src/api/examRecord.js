import request from '../utils/request'

// 保存用户自评结果
export function saveUserGrading(data) {
    return request({
        url: '/exam-record/save-user-grading',
        method: 'post',
        data
    })
}

// 获取主观题列表
export function getSubjectiveQuestions(sessionId) {
    return request({
        url: `/exam-record/subjective-questions/${sessionId}`,
        method: 'get'
    })
}

// 获取分数统计
export function getScoreStatistics(sessionId) {
    return request({
        url: `/exam-record/score-statistics/${sessionId}`,
        method: 'get'
    })
}

// 获取会话答题详情
export function getSessionExamRecords(sessionId) {
    return request({
        url: `/exam-record/session/${sessionId}`,
        method: 'get'
    })
}
