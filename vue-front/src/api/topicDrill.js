import request from '../utils/request'

// 获取知识点题目数据
export function getQuestionsByKnowledgePointApi(subjectId) {
  return request({
    url: '/topic-drill/questions-by-knowledge-point',
    method: 'get',
    params: { subjectId }
  })
}

// 保存答题进度
export function saveTopicDrillProgressApi(data) {
  return request({
    url: '/topic-drill/save-progress',
    method: 'post',
    data
  })
}

// 加载答题进度
export function loadTopicDrillProgressApi(userId, subjectId) {
  return request({
    url: '/topic-drill/load-progress',
    method: 'get',
    params: { userId, subjectId }
  })
}

// 保存笔记
export function saveTopicDrillNoteApi(data) {
  return request({
    url: '/topic-drill/save-note',
    method: 'post',
    data
  })
}

// 加载笔记
export function loadTopicDrillNotesApi(userId, subjectId) {
  return request({
    url: '/topic-drill/load-notes',
    method: 'get',
    params: { userId, subjectId }
  })
}

// 保存卡片布局
export function saveTopicDrillLayoutApi(data) {
  return request({
    url: '/topic-drill/save-layout',
    method: 'post',
    data
  })
}

// 加载卡片布局
export function loadTopicDrillLayoutApi(userId, subjectId) {
  return request({
    url: '/topic-drill/load-layout',
    method: 'get',
    params: { userId, subjectId }
  })
}

// 获取知识点统计数据
export function getKnowledgePointStatsApi(subjectId, userId) {
  return request({
    url: '/topic-drill/knowledge-point-stats',
    method: 'get',
    params: { subjectId, userId }
  })
}

// 获取题目推荐
export function getQuestionRecommendationApi(userId, subjectId) {
  return request({
    url: '/topic-drill/question-recommendation',
    method: 'get',
    params: { userId, subjectId }
  })
}

// 重置进度
export function resetTopicDrillProgressApi(data) {
  return request({
    url: '/topic-drill/reset-progress',
    method: 'post',
    data
  })
}
