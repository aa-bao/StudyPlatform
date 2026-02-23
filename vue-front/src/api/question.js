import request from '../utils/request'

// 获取知识点题目
export function getQuestionsByKnowledgePointApi(subjectId) {
  return request({
    url: '/topic-drill/questions-by-knowledge-point',
    method: 'get',
    params: { subjectId }
  })
}

// 保存题目答案
export function saveQuestionAnswerApi(data) {
  return request({
    url: '/topic-drill/save-question-answer',
    method: 'post',
    data
  })
}

// 获取题目答案
export function getQuestionAnswerApi(questionId, userId) {
  return request({
    url: '/topic-drill/get-question-answer',
    method: 'get',
    params: { questionId, userId }
  })
}

// 标记题目为已答
export function markQuestionAsAnsweredApi(data) {
  return request({
    url: '/topic-drill/mark-question-answered',
    method: 'post',
    data
  })
}

// 获取题目解析
export function getQuestionAnalysisApi(questionId) {
  return request({
    url: `/topic-drill/get-question-analysis/${questionId}`,
    method: 'get'
  })
}

// 报告题目错误
export function reportQuestionErrorApi(data) {
  return request({
    url: '/topic-drill/report-question-error',
    method: 'post',
    data
  })
}

// 获取题目难度分布
export function getQuestionDifficultyDistributionApi(subjectId) {
  return request({
    url: '/topic-drill/get-difficulty-distribution',
    method: 'get',
    params: { subjectId }
  })
}

// 获取题目类型分布
export function getQuestionTypeDistributionApi(subjectId) {
  return request({
    url: '/topic-drill/get-type-distribution',
    method: 'get',
    params: { subjectId }
  })
}
