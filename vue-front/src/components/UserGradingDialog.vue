<template>
  <el-dialog
    v-model="dialogVisible"
    title="主观题自我批改"
    width="900px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="grading-container">
      <el-alert
        title="请对照标准答案，对自己的主观题进行客观评分"
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 20px;"
      />

      <!-- 快捷操作 -->
      <div class="shortcut-actions">
        <el-button @click="markAllCorrect">全部标记为正确</el-button>
        <el-button @click="markAllWrong">全部标记为错误</el-button>
        <el-button type="primary" @click="submitGrading" :loading="submitting">
          提交批改结果
        </el-button>
      </div>

      <!-- 主观题列表 -->
      <div class="question-list">
        <div
          v-for="(item, index) in subjectiveQuestions"
          :key="item.questionId"
          class="question-item"
        >
          <div class="question-header">
            <span class="question-number">第{{ item.sortOrder }}题</span>
            <span class="question-score">{{ item.totalScore }}分</span>
          </div>

          <div class="question-content">
            <div class="content-label">题目内容：</div>
            <div class="content-text" v-html="renderLatex(item.questionContent)"></div>
          </div>

          <div class="answer-section">
            <div class="answer-box">
              <div class="answer-label">📝 你的答案：</div>
              <div class="answer-content user-answer">
                {{ item.userAnswer || '未作答' }}
              </div>
            </div>

            <div class="answer-box">
              <div class="answer-label">📊 标准答案：</div>
              <div class="answer-content standard-answer" v-html="renderLatex(item.standardAnswer)"></div>
            </div>
          </div>

          <!-- 评分区域 -->
          <div class="grading-section">
            <div class="grading-item">
              <div class="grading-label">
                📝 过程分（{{ item.processScoreValue }}分，占{{ (item.processRatio * 100).toFixed(0) }}%）
              </div>
              <div class="grading-options">
                <el-radio-group v-model="item.userProcessGrading">
                  <el-radio :label="1">✓ 过程正确</el-radio>
                  <el-radio :label="0">✗ 过程错误</el-radio>
                </el-radio-group>
              </div>
            </div>

            <div class="grading-item">
              <div class="grading-label">
                ✎️ 结果分（{{ item.resultScoreValue }}分，占{{ (item.resultRatio * 100).toFixed(0) }}%）
              </div>
              <div class="grading-options">
                <el-radio-group v-model="item.userResultGrading">
                  <el-radio :label="1">✓ 答案正确</el-radio>
                  <el-radio :label="0">✗ 答案错误</el-radio>
                </el-radio-group>
              </div>
            </div>

            <div class="score-preview">
              本题得分：<strong>{{ calculateQuestionScore(item) }}</strong> / {{ item.totalScore }} 分
              <span class="score-detail">
                （过程：{{ item.userProcessGrading === 1 ? item.processScoreValue : 0 }}分 +
                结果：{{ item.userResultGrading === 1 ? item.resultScoreValue : 0 }}分）
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="submitGrading" :loading="submitting">
          提交批改结果
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { saveUserGrading, getSubjectiveQuestions } from '@/api/examRecord'
import { renderLatex } from '@/utils/latex'

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

const emit = defineEmits(['update:visible', 'submitted'])

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const subjectiveQuestions = ref([])
const submitting = ref(false)

// 监听对话框打开，加载主观题数据
watch(() => props.visible, async (newVal) => {
  if (newVal && props.sessionId) {
    await loadSubjectiveQuestions()
  }
})

// 加载主观题列表
const loadSubjectiveQuestions = async () => {
  try {
    const response = await getSubjectiveQuestions(props.sessionId)
    if (response.code === 200) {
      subjectiveQuestions.value = response.data.map(item => ({
        ...item,
        userProcessGrading: item.userProcessGrading ?? null,
        userResultGrading: item.userResultGrading ?? null
      }))
    } else {
      ElMessage.error(response.message || '加载主观题失败')
    }
  } catch (error) {
    console.error('加载主观题失败:', error)
    ElMessage.error('加载主观题失败')
  }
}

// 计算单题得分
const calculateQuestionScore = (item) => {
  let score = 0
  if (item.userProcessGrading === 1) {
    score += parseFloat(item.processScoreValue || 0)
  }
  if (item.userResultGrading === 1) {
    score += parseFloat(item.resultScoreValue || 0)
  }
  return score.toFixed(1)
}

// 全部标记为正确
const markAllCorrect = () => {
  subjectiveQuestions.value.forEach(item => {
    item.userProcessGrading = 1
    item.userResultGrading = 1
  })
  ElMessage.success('已全部标记为正确')
}

// 全部标记为错误
const markAllWrong = () => {
  subjectiveQuestions.value.forEach(item => {
    item.userProcessGrading = 0
    item.userResultGrading = 0
  })
  ElMessage.success('已全部标记为错误')
}

// 提交批改结果
const submitGrading = async () => {
  // 验证所有题目都已批改
  const ungraded = subjectiveQuestions.value.filter(
    item => item.userProcessGrading === null || item.userResultGrading === null
  )

  if (ungraded.length > 0) {
    ElMessage.warning(`还有 ${ungraded.length} 道题未批改，请全部批改后再提交`)
    return
  }

  submitting.value = true
  try {
    const gradingItems = subjectiveQuestions.value.map(item => ({
      questionId: item.questionId,
      processGrading: item.userProcessGrading,
      resultGrading: item.userResultGrading
    }))

    const response = await saveUserGrading({
      sessionId: props.sessionId,
      gradingItems
    })

    if (response.code === 200) {
      ElMessage.success('批改结果已保存')
      emit('submitted')
      handleClose()
    } else {
      ElMessage.error(response.message || '保存失败')
    }
  } catch (error) {
    console.error('保存批改结果失败:', error)
    ElMessage.error('保存批改结果失败')
  } finally {
    submitting.value = false
  }
}

// 关闭对话框
const handleClose = () => {
  dialogVisible.value = false
}
</script>

<style scoped>
.grading-container {
  max-height: 600px;
  overflow-y: auto;
  padding: 10px;
}

.shortcut-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.question-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.question-item {
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  padding: 15px;
  background-color: #fff;
  transition: box-shadow 0.3s;
}

.question-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 2px solid #409eff;
}

.question-number {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.question-score {
  font-size: 14px;
  color: #67c23a;
  font-weight: bold;
}

.question-content {
  margin-bottom: 12px;
}

.content-label {
  font-weight: bold;
  color: #606266;
  margin-bottom: 6px;
}

.content-text {
  color: #303133;
  line-height: 1.8;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.answer-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin-bottom: 15px;
}

.answer-box {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 12px;
  background-color: #fafafa;
}

.answer-label {
  font-weight: bold;
  color: #606266;
  margin-bottom: 8px;
  font-size: 14px;
}

.answer-content {
  min-height: 60px;
  padding: 10px;
  border-radius: 4px;
  line-height: 1.6;
  font-size: 14px;
}

.user-answer {
  background-color: #e1f3f8;
  color: #303133;
}

.standard-answer {
  background-color: #f0f9ff;
  color: #409eff;
}

.grading-section {
  border-top: 1px dashed #dcdfe6;
  padding-top: 15px;
}

.grading-item {
  margin-bottom: 12px;
}

.grading-label {
  font-weight: bold;
  color: #606266;
  margin-bottom: 8px;
  font-size: 14px;
}

.grading-options {
  margin-left: 10px;
}

.score-preview {
  margin-top: 15px;
  padding: 10px;
  background-color: #ecf5ff;
  border-left: 4px solid #409eff;
  border-radius: 4px;
  font-size: 14px;
  color: #303133;
}

.score-preview strong {
  color: #409eff;
  font-size: 16px;
  margin-left: 5px;
}

.score-detail {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
