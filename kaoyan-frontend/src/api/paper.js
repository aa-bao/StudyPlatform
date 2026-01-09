import request from '../utils/request'

// 获取试卷详情
export function getPaperDetail(paperId) {
    return request({
        url: `/paper/${paperId}`,
        method: 'get'
    })
}

// 获取试卷列表
export function getPaperList(params) {
    return request({
        url: '/paper/list',
        method: 'get',
        params
    })
}

// 分页查询试卷
export function getPaperPage(params) {
    return request({
        url: '/paper/page',
        method: 'get',
        params
    })
}

// 获取试卷题目列表
export function getPaperQuestions(paperId) {
    return request({
        url: `/paper/${paperId}/questions`,
        method: 'get'
    })
}
