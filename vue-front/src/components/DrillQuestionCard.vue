<template>
  <div
    class="question-card"
    :style="style"
    :class="{
      'correct-card': questionData.isCorrect === true,
      'incorrect-card': questionData.isCorrect === false
    }"
    @mousedown="startDrag"
  >
    <!-- 拖动手柄 -->
    <div class="drag-handle" v-if="isDraggable">
      <span>☰</span>
    </div>

    <!-- 卡片头部 -->
    <div class="card-header">
      <span class="card-number">{{ questionIndex + 1 }}</span>
      <div class="card-actions">
        <button
          class="action-btn"
          @click.stop="toggleAnswer"
          :class="{ active: showAnswer }"
        >
          {{ showAnswer ? '隐藏答案' : '显示答案' }}
        </button>
        <div class="answer-status">
          <button
            class="status-btn correct"
            :class="{ active: questionData.isCorrect === true }"
            @click.stop="markAsCorrect"
          >
            正确
          </button>
          <button
            class="status-btn incorrect"
            :class="{ active: questionData.isCorrect === false }"
            @click.stop="markAsIncorrect"
          >
            错误
          </button>
        </div>
      </div>
    </div>

    <!-- 题目内容 -->
    <div class="card-content">
      <div class="question-section">
        <h3>题目</h3>
        <div v-if="questionData.questionImage" class="question-image">
          <img
            :src="questionData.questionImage"
            :alt="`题目${questionIndex + 1}`"
            @error="handleImageError"
          />
        </div>
        <div v-else class="question-text" v-html="renderLatex(questionData.content || '暂无题目内容')">
        </div>
      </div>

      <!-- 答案解析（默认隐藏） -->
      <div class="answer-section" v-if="showAnswer">
        <h3>答案</h3>
        <div v-if="questionData.answerImage" class="answer-image">
          <img
            :src="questionData.answerImage"
            :alt="`答案${questionIndex + 1}`"
            @error="handleImageError"
          />
        </div>
        <div v-else class="answer-text" v-html="renderLatex(questionData.answer || '暂无答案')">
        </div>

        <!-- 解析内容 -->
        <div v-if="questionData.analysis" class="analysis-section">
          <h3>解析</h3>
          <div class="analysis-text" v-html="renderLatex(questionData.analysis)">
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, defineEmits } from 'vue'
import { ElMessage } from 'element-plus'
import katex from 'katex'

// LaTeX 渲染函数
const renderLatex = (content) => {
  if (!content) return ''
  return content.replace(/\$([^$]+)\$/g, (match, tex) => {
    try {
      return katex.renderToString(tex, {
        throwOnError: false,
        displayMode: false
      })
    } catch (err) {
      return match
    }
  }).replace(/\$\$([^$]+)\$\$/g, (match, tex) => {
    try {
      return katex.renderToString(tex, {
        throwOnError: false,
        displayMode: true
      })
    } catch (err) {
      return match
    }
  })
}

// 接收的props
const props = defineProps({
  questionData: {
    type: Object,
    required: true
  },
  questionIndex: {
    type: Number,
    default: 0
  },
  cardId: {
    type: String,
    required: true
  },
  style: {
    type: Object,
    default: () => ({})
  },
  isDraggable: {
    type: Boolean,
    default: true
  }
})

// 发送事件
const emit = defineEmits(['card-moved', 'question-updated'])

// 卡片状态
const showAnswer = ref(false)
const isDragging = ref(false)
const startX = ref(0)
const startY = ref(0)
const initialLeft = ref(0)
const initialTop = ref(0)

// 切换答案显示
const toggleAnswer = () => {
  showAnswer.value = !showAnswer.value
}

// 标记为正确
const markAsCorrect = () => {
  props.questionData.isCorrect = true
  props.questionData.isAnswered = true
  ElMessage.success('已标记为正确')
  emit('question-updated')
}

// 标记为错误
const markAsIncorrect = () => {
  props.questionData.isCorrect = false
  props.questionData.isAnswered = true
  ElMessage.warning('已标记为错误')
  emit('question-updated')
}

// 处理图片加载错误
const handleImageError = (e) => {
  console.error('图片加载失败:', e.target.src)
  e.target.style.display = 'none'
  const parent = e.target.parentElement
  const fallback = document.createElement('div')
  fallback.textContent = '图片加载失败'
  fallback.className = 'image-fallback'
  parent.appendChild(fallback)
}

// 开始拖动
const startDrag = (e) => {
  if (!props.isDraggable) return

  // 只有点击拖动手柄或卡片头部才允许拖动
  if (!e.target.classList.contains('drag-handle') &&
      !e.target.closest('.card-header') &&
      !e.target.classList.contains('card-number')) {
    return
  }

  isDragging.value = true
  startX.value = e.clientX
  startY.value = e.clientY
  // 解析 left 和 top 的值（处理可能的 px 单位）
  initialLeft.value = parseInt(props.style.left) || 0
  initialTop.value = parseInt(props.style.top) || 0

  // 添加拖动样式
  e.target.closest('.question-card').classList.add('dragging')

  // 全局事件监听
  const handleMouseMove = (e) => {
    const deltaX = e.clientX - startX.value
    const deltaY = e.clientY - startY.value
    const newLeft = initialLeft.value + deltaX
    const newTop = initialTop.value + deltaY

    // 发送位置更新事件（保留单位）
    emit('card-moved', props.questionIndex, {
      left: `${newLeft}px`,
      top: `${newTop}px`,
      position: 'absolute'
    })
  }

  const handleMouseUp = () => {
    isDragging.value = false
    document.removeEventListener('mousemove', handleMouseMove)
    document.removeEventListener('mouseup', handleMouseUp)
    // 移除拖动样式
    const card = e.target.closest('.question-card')
    if (card) {
      card.classList.remove('dragging')
    }
  }

  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
}

onMounted(() => {
  // 组件初始化
})
</script>

<style scoped>
.question-card {
  position: absolute;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
  min-width: 250px;
  cursor: move;
  /* 移除固定宽高限制，让内容决定大小 */
  width: auto;
  height: auto;
}

.question-card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

/* 默认蓝色（登录页面颜色） */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #409eff;
  color: white;
}

/* 正确时绿色 */
.question-card.correct-card .card-header {
  background: #67c23a;
}

/* 错误时红色 */
.question-card.incorrect-card .card-header {
  background: #f56c6c;
}

/* 拖动手柄 */
.drag-handle {
  position: absolute;
  top: 4px;
  left: 4px;
  width: 24px;
  height: 24px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: grab;
  z-index: 10;
  font-size: 14px;
  color: white;
}

.drag-handle:hover {
  background: rgba(255, 255, 255, 0.3);
}

.question-card.dragging {
  cursor: grabbing;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
  z-index: 1000;
}

.card-number {
  font-size: 14px;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.2);
  padding: 4px 12px;
  border-radius: 12px;
}

.card-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.action-btn {
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 6px;
  color: white;
  cursor: pointer;
  font-size: 12px;
  transition: background 0.3s ease;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.action-btn.active {
  background: rgba(255, 255, 255, 0.4);
}

.answer-status {
  display: flex;
  gap: 4px;
}

.status-btn {
  padding: 4px 8px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 11px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.status-btn.correct {
  background: rgba(34, 197, 94, 0.8);
  color: white;
}

.status-btn.correct:hover {
  background: rgba(34, 197, 94, 1);
}

.status-btn.correct.active {
  background: #22c55e;
  box-shadow: 0 0 0 2px rgba(34, 197, 94, 0.3);
}

.status-btn.incorrect {
  background: rgba(239, 68, 68, 0.8);
  color: white;
}

.status-btn.incorrect:hover {
  background: rgba(239, 68, 68, 1);
}

.status-btn.incorrect.active {
  background: #ef4444;
  box-shadow: 0 0 0 2px rgba(239, 68, 68, 0.3);
}

.card-content {
  padding: 16px;
  max-height: calc(100% - 60px);
  overflow-y: auto;
}

.question-section,
.answer-section,
.analysis-section {
  margin-bottom: 16px;
}

.question-section h3,
.answer-section h3,
.analysis-section h3 {
  font-size: 14px;
  margin-bottom: 8px;
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 4px;
}

.question-image,
.answer-image {
  margin: 8px 0;
}

.question-image img,
.answer-image img {
  /* 移除宽度限制，让图片保持原始大小 */
  max-width: none;
  width: auto;
  height: auto;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  /* 确保图片加载后能正确计算尺寸 */
  display: block;
  /* 防止图片被压缩 */
  object-fit: contain;
}

.question-text,
.answer-text,
.analysis-text {
  font-size: 14px;
  line-height: 1.6;
  color: #555;
  margin: 8px 0;
}

/* KaTeX 样式 */
:deep(.katex) {
  font-size: 1.15em;
  line-height: 1.2;
}

:deep(.katex-display) {
  margin: 0.8em 0;
  overflow-x: auto;
  overflow-y: hidden;
  padding: 4px 0;
}

.image-fallback {
  text-align: center;
  padding: 20px;
  color: #999;
  background: #f9f9f9;
  border-radius: 8px;
  font-size: 12px;
}

/* 滚动条样式 */
.card-content::-webkit-scrollbar {
  width: 6px;
}

.card-content::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.card-content::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.card-content::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
