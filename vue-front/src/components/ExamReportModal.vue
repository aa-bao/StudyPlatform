<template>
  <el-dialog
    v-model="dialogVisible"
    title="考试报告"
    width="800px"
    :close-on-click-modal="false"
  >
    <div class="report-container" v-loading="loading">
      <!-- 总分概览 -->
      <div class="score-overview">
        <div class="total-score-card">
          <div class="score-label">总得分</div>
          <div class="score-value">{{ reportData.totalScore || 0 }}</div>
          <div class="score-total">/ {{ reportData.fullScore || 150 }} 分</div>
        </div>

        <div class="score-breakdown">
          <div class="score-item objective">
            <div class="score-type">📊 客观题</div>
            <div class="score-number">{{ reportData.objectiveScore || 0 }}</div>
            <div class="score-detail">/ {{ reportData.objectiveFullScore || 0 }} 分</div>
            <div class="score-stats">
              答对 {{ reportData.objectiveCorrect || 0 }} / {{ reportData.objectiveCount || 0 }} 题
            </div>
          </div>

          <div class="score-item subjective">
            <div class="score-type">✍️ 主观题</div>
            <div class="score-number">{{ reportData.subjectiveScore || 0 }}</div>
            <div class="score-detail">/ {{ reportData.subjectiveFullScore || 0 }} 分</div>
            <div class="score-stats">
              已批改 {{ reportData.subjectiveGraded || 0 }} / {{ reportData.subjectiveCount || 0 }} 题
            </div>
          </div>
        </div>
      </div>

      <!-- AI 总结 -->
      <div class="ai-summary" v-if="reportData.aiSummary">
        <h4>💡 AI 总结</h4>
        <div class="summary-text markdown-content" v-html="renderMarkdown(reportData.aiSummary)"></div>
      </div>

      <!-- 详细分析 -->
      <div class="detail-analysis">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="客观题详情" name="objective">
            <div v-if="objectiveQuestions.length === 0" class="empty-state">
              <p>暂无客观题</p>
            </div>
            <div v-else class="question-list">
              <div
                v-for="q in objectiveQuestions"
                :key="q.questionId"
                class="question-detail"
                :class="{ correct: q.isCorrect === 1, wrong: q.isCorrect === 0 }"
              >
                <div class="question-header">
                  <span class="question-number">第{{ q.sortOrder }}题</span>
                  <span class="question-score">{{ q.scoreEarned || 0 }}/{{ q.scoreValue }}分</span>
                  <span class="question-status" :class="q.isCorrect === 1 ? 'correct' : 'wrong'">
                    {{ q.isCorrect === 1 ? '✓ 正确' : '✗ 错误' }}
                  </span>
                </div>
                <div class="question-answers">
                  <div class="answer-item">
                    <span class="label">你的答案：</span>
                    <span class="value">{{ q.userAnswer || '未作答' }}</span>
                  </div>
                  <div class="answer-item">
                    <span class="label">标准答案：</span>
                    <span class="value correct-answer">{{ q.standardAnswer }}</span>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="主观题详情" name="subjective">
            <div v-if="subjectiveQuestions.length === 0" class="empty-state">
              <p>暂无主观题</p>
            </div>
            <div v-else class="question-list">
              <div
                v-for="q in subjectiveQuestions"
                :key="q.questionId"
                class="question-detail subjective-detail"
              >
                <div class="question-header">
                  <span class="question-number">第{{ q.sortOrder }}题</span>
                  <span class="question-score">{{ q.scoreEarned || 0 }}/{{ q.scoreValue }}分</span>
                </div>

                <!-- 过程分和结果分显示 -->
                <div class="grading-breakdown" v-if="q.processScore || q.resultScore">
                  <div class="grading-item">
                    <span class="grading-label">📝 过程分：</span>
                    <span class="grading-score">
                      {{ q.userProcessGrading === 1 ? q.processScoreValue : 0 }}/{{ q.processScoreValue }}分
                    </span>
                    <span class="grading-status" :class="q.userProcessGrading === 1 ? 'correct' : 'wrong'">
                      {{ q.userProcessGrading === 1 ? '✓' : '✗' }}
                    </span>
                  </div>
                  <div class="grading-item">
                    <span class="grading-label">✎️ 结果分：</span>
                    <span class="grading-score">
                      {{ q.userResultGrading === 1 ? q.resultScoreValue : 0 }}/{{ q.resultScoreValue }}分
                    </span>
                    <span class="grading-status" :class="q.userResultGrading === 1 ? 'correct' : 'wrong'">
                      {{ q.userResultGrading === 1 ? '✓' : '✗' }}
                    </span>
                  </div>
                </div>

                <div class="question-answers">
                  <div class="answer-item">
                    <span class="label">📝 你的答案：</span>
                    <div class="value user-answer-content">{{ q.userAnswer || '未作答' }}</div>
                  </div>
                  <div class="answer-item">
                    <span class="label">📊 标准答案：</span>
                    <div class="value standard-answer-content" v-html="renderLatex(q.standardAnswer)"></div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 学习建议 -->
      <div class="study-suggestions" v-if="suggestions.length > 0">
        <h4>💪 学习建议</h4>
        <ul class="suggestion-list">
          <li v-for="(suggestion, index) in suggestions" :key="index">
            {{ suggestion }}
          </li>
        </ul>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="goBack">返回首页</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { renderLatex } from '@/utils/latex'
import MarkdownIt from 'markdown-it'

const props = defineProps({
  visible: {
    type: Boolean,
    required: true
  },
  sessionId: {
    type: [String, Number],
    required: true
  }
})

const emit = defineEmits(['update:visible'])

const router = useRouter()
const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

// 初始化 markdown-it
const md = new MarkdownIt({
  breaks: true,
  linkify: true,
  typographer: true
})

// 渲染 markdown 内容
const renderMarkdown = (content) => {
  if (!content) return ''
  return md.render(content)
}

const loading = ref(false)
const activeTab = ref('objective')
const reportData = ref({})
const objectiveQuestions = ref([])
const subjectiveQuestions = ref([])
const suggestions = ref([])

watch(() => props.visible, async (newVal) => {
  if (newVal && props.sessionId) {
    await loadReportData()
  }
})

const loadReportData = async () => {
  loading.value = true
  try {
    // 获取分数统计
    const statsRes = await axios.get(`/api/exam-record/score-statistics/${props.sessionId}`)
    if (statsRes.data.code === 200) {
      const stats = statsRes.data.data
      reportData.value = {
        totalScore: stats.totalScore || 0,
        objectiveScore: stats.objectiveScore || 0,
        subjectiveScore: stats.subjectiveScore || 0,
        objectiveCount: stats.objectiveCount || 0,
        subjectiveCount: stats.subjectiveCount || 0
      }
    }

    // 获取会话详情
    const sessionRes = await axios.get(`/api/exam-session/${props.sessionId}`)
    if (sessionRes.data.code === 200) {
      const session = sessionRes.data.data
      reportData.value.aiSummary = session.aiSummary

      // 解析题目详情
      // 这里需要根据实际API返回的数据结构调整
    }

    // 生成学习建议
    generateSuggestions()
  } catch (error) {
    console.error('加载报告数据失败:', error)
    ElMessage.error('加载报告失败')
  } finally {
    loading.value = false
  }
}

const generateSuggestions = () => {
  suggestions.value = []

  const objectiveRate = reportData.value.objectiveCount > 0
    ? (parseFloat(reportData.value.objectiveScore || 0) / (reportData.value.objectiveCount * 5)) * 100
    : 0

  if (objectiveRate >= 80) {
    suggestions.value.push('客观题基础扎实，继续保持！')
  } else if (objectiveRate >= 60) {
    suggestions.value.push('客观题有一定基础，还需加强练习。')
  } else {
    suggestions.value.push('客观题基础薄弱，建议多做基础题巩固。')
  }

  if (reportData.value.subjectiveCount > 0) {
    suggestions.value.push('主观题需要注重解题过程和结果的双重准确性。')
  }
}

const goBack = () => {
  router.push('/home')
}
</script>

<style scoped>
.report-container {
  padding: 10px;
}

.score-overview {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.total-score-card {
  flex: 0 0 200px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  color: white;
}

.score-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 10px;
}

.score-value {
  font-size: 36px;
  font-weight: bold;
  line-height: 1;
}

.score-total {
  font-size: 14px;
  opacity: 0.9;
  margin-top: 5px;
}

.score-breakdown {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.score-item {
  border-radius: 12px;
  padding: 20px;
  text-align: center;
}

.score-item.objective {
  background-color: #e3f2fd;
}

.score-item.subjective {
  background-color: #fff3e0;
}

.score-type {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 10px;
}

.score-number {
  font-size: 32px;
  font-weight: bold;
  line-height: 1;
}

.score-item.objective .score-number {
  color: #1976d2;
}

.score-item.subjective .score-number {
  color: #f57c00;
}

.score-detail {
  font-size: 14px;
  margin-top: 5px;
  opacity: 0.8;
}

.score-stats {
  font-size: 12px;
  margin-top: 8px;
  opacity: 0.7;
}

.ai-summary {
  background-color: #f5f7fa;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
}

.ai-summary h4 {
  margin: 0 0 10px 0;
  color: #303133;
}

.summary-text {
  margin: 0;
  color: #606266;
  line-height: 1.8;
  font-size: 14px;
}

/* Markdown 内容样式 */
.markdown-content {
  color: #303133;
  line-height: 1.8;
}

.markdown-content h1,
.markdown-content h2,
.markdown-content h3,
.markdown-content h4,
.markdown-content h5,
.markdown-content h6 {
  margin-top: 16px;
  margin-bottom: 8px;
  font-weight: 600;
  color: #303133;
}

.markdown-content h1 {
  font-size: 22px;
  border-bottom: 2px solid #e4e7ed;
  padding-bottom: 8px;
}

.markdown-content h2 {
  font-size: 18px;
  border-bottom: 1px solid #e4e7ed;
  padding-bottom: 6px;
}

.markdown-content h3 {
  font-size: 16px;
}

.markdown-content h4 {
  font-size: 15px;
}

.markdown-content p {
  margin: 8px 0;
}

.markdown-content ul,
.markdown-content ol {
  margin: 8px 0;
  padding-left: 24px;
}

.markdown-content li {
  margin: 4px 0;
}

.markdown-content code {
  background-color: #f5f7fa;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 13px;
  color: #c7254e;
}

.markdown-content pre {
  background-color: #f5f7fa;
  padding: 12px;
  border-radius: 6px;
  overflow-x: auto;
  margin: 12px 0;
}

.markdown-content pre code {
  background-color: transparent;
  padding: 0;
  color: #303133;
}

.markdown-content blockquote {
  border-left: 4px solid #409eff;
  padding-left: 12px;
  margin: 12px 0;
  color: #606266;
  background-color: #ecf5ff;
  padding: 8px 12px;
  border-radius: 0 4px 4px 0;
}

.markdown-content strong {
  color: #303133;
  font-weight: 600;
}

.markdown-content em {
  font-style: italic;
}

.markdown-content a {
  color: #409eff;
  text-decoration: none;
}

.markdown-content a:hover {
  text-decoration: underline;
}

.markdown-content table {
  border-collapse: collapse;
  width: 100%;
  margin: 12px 0;
}

.markdown-content th,
.markdown-content td {
  border: 1px solid #dcdfe6;
  padding: 8px 12px;
  text-align: left;
}

.markdown-content th {
  background-color: #f5f7fa;
  font-weight: 600;
}

.markdown-content hr {
  border: none;
  border-top: 1px solid #dcdfe6;
  margin: 16px 0;
}

.detail-analysis {
  margin-bottom: 20px;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #909399;
}

.question-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  max-height: 400px;
  overflow-y: auto;
}

.question-detail {
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  padding: 15px;
  background-color: #fff;
}

.question-detail.correct {
  border-left: 4px solid #67c23a;
  background-color: #f0f9ff;
}

.question-detail.wrong {
  border-left: 4px solid #f56c6c;
  background-color: #fef0f0;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e4e7ed;
}

.question-number {
  font-weight: bold;
  color: #303133;
}

.question-score {
  font-size: 14px;
  color: #909399;
}

.question-status {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.question-status.correct {
  background-color: #67c23a;
  color: white;
}

.question-status.wrong {
  background-color: #f56c6c;
  color: white;
}

.grading-breakdown {
  display: flex;
  gap: 20px;
  margin-bottom: 12px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.grading-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.grading-label {
  font-weight: bold;
  color: #606266;
}

.grading-score {
  color: #303133;
  font-weight: bold;
}

.grading-status {
  margin-left: auto;
  font-size: 18px;
  font-weight: bold;
}

.grading-status.correct {
  color: #67c23a;
}

.grading-status.wrong {
  color: #f56c6c;
}

.question-answers {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.answer-item {
  display: flex;
  gap: 10px;
}

.label {
  flex-shrink: 0;
  font-weight: bold;
  color: #606266;
  min-width: 80px;
}

.value {
  color: #303133;
  line-height: 1.6;
}

.user-answer-content {
  background-color: #e1f3f8;
  padding: 8px;
  border-radius: 4px;
  flex: 1;
}

.standard-answer {
  color: #409eff;
  font-weight: bold;
}

.standard-answer-content {
  background-color: #f0f9ff;
  padding: 8px;
  border-radius: 4px;
  flex: 1;
}

.study-suggestions {
  background-color: #ecf5ff;
  border-left: 4px solid #409eff;
  border-radius: 4px;
  padding: 15px;
}

.study-suggestions h4 {
  margin: 0 0 10px 0;
  color: #409eff;
}

.suggestion-list {
  margin: 0;
  padding-left: 20px;
}

.suggestion-list li {
  color: #606266;
  line-height: 1.8;
  margin-bottom: 5px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
