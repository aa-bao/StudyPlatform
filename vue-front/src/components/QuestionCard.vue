<template>
  <div
    class="question-card"
    :style="style"
    @mousedown="handleMouseDown"
  >
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
        <div v-else class="question-text">
          {{ questionData.content || '暂无题目内容' }}
        </div>
      </div>

      <!-- 答案解析（默认隐藏） -->
      <div class="answer-section" v-if="showAnswer">
        <h3>答案解析</h3>
        <div v-if="questionData.answerImage" class="answer-image">
          <img
            :src="questionData.answerImage"
            :alt="`答案${questionIndex + 1}`"
            @error="handleImageError"
          />
        </div>
        <div v-else class="answer-text">
          {{ questionData.answer || '暂无答案解析' }}
        </div>
      </div>
    </div>


    <!-- 卡片调整大小控制点 -->
    <div class="resize-handle" @mousedown="startResizing"></div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { saveTopicDrillNoteApi, loadTopicDrillNotesApi } from '@/api/topicDrill'
import { ElMessage } from 'element-plus'

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
  }
})

// 卡片状态
const showAnswer = ref(false)
const isDragging = ref(false)
const isResizing = ref(false)
const startX = ref(0)
const startY = ref(0)
const startWidth = ref(0)
const startHeight = ref(0)

// 笔记画布
const noteCanvas = ref(null)
const canvasWidth = ref(280)
const canvasHeight = ref(200)
const isDrawing = ref(false)
const lastX = ref(0)
const lastY = ref(0)
const noteColor = ref('#000000') // 笔记颜色
const noteLineWidth = ref(2) // 笔记线条粗细

// 笔记颜色选项
const noteColors = [
  { name: '黑色', value: '#000000' },
  { name: '红色', value: '#FF0000' },
  { name: '蓝色', value: '#0000FF' },
  { name: '绿色', value: '#00FF00' },
  { name: '黄色', value: '#FFFF00' },
  { name: '橙色', value: '#FFA500' }
]

// 切换答案显示
const toggleAnswer = () => {
  showAnswer.value = !showAnswer.value
}

// 切换笔记显示
const toggleNotes = () => {
  showNotes.value = !showNotes.value
  if (showNotes.value) {
    nextTick(() => {
      initCanvas()
    })
  }
}

// 清空笔记
const clearNotes = () => {
  const canvas = noteCanvas.value
  if (canvas) {
    const ctx = canvas.getContext('2d')
    ctx.clearRect(0, 0, canvasWidth.value, canvasHeight.value)
  }
}

// 初始化画布
const initCanvas = () => {
  const canvas = noteCanvas.value
  if (canvas) {
    const ctx = canvas.getContext('2d')
    ctx.lineWidth = noteLineWidth.value
    ctx.lineCap = 'round'
    ctx.lineJoin = 'round'
    ctx.strokeStyle = noteColor.value
  }
}

// 改变笔记颜色
const changeNoteColor = (color) => {
  noteColor.value = color
  const canvas = noteCanvas.value
  if (canvas) {
    const ctx = canvas.getContext('2d')
    ctx.strokeStyle = color
  }
}

// 改变笔记线条粗细
const changeLineWidth = () => {
  const canvas = noteCanvas.value
  if (canvas) {
    const ctx = canvas.getContext('2d')
    ctx.lineWidth = noteLineWidth.value
  }
}

// 保存笔记到本地存储
const saveNote = () => {
  const canvas = noteCanvas.value
  if (canvas) {
    const noteData = canvas.toDataURL()
    const existingNotes = JSON.parse(localStorage.getItem('questionNotes') || '{}')
    existingNotes[props.cardId] = noteData
    localStorage.setItem('questionNotes', JSON.stringify(existingNotes))
    ElMessage.success('笔记已保存')
  }
}

// 从本地存储加载笔记
const loadNote = () => {
  const existingNotes = JSON.parse(localStorage.getItem('questionNotes') || '{}')
  const noteData = existingNotes[props.cardId]
  if (noteData) {
    const canvas = noteCanvas.value
    if (canvas) {
      const ctx = canvas.getContext('2d')
      const img = new Image()
      img.onload = () => {
        ctx.drawImage(img, 0, 0)
      }
      img.src = noteData
      ElMessage.success('笔记已加载')
    }
  } else {
    ElMessage.info('暂无笔记')
  }
}

// 开始绘制
const startDrawing = (e) => {
  isDrawing.value = true
  const rect = noteCanvas.value.getBoundingClientRect()
  const x = e.clientX - rect.left
  const y = e.clientY - rect.top
  lastX.value = x
  lastY.value = y
}

// 绘制
const draw = (e) => {
  if (!isDrawing.value) return

  const canvas = noteCanvas.value
  const rect = canvas.getBoundingClientRect()
  const x = e.clientX - rect.left
  const y = e.clientY - rect.top

  const ctx = canvas.getContext('2d')
  ctx.beginPath()
  ctx.moveTo(lastX.value, lastY.value)
  ctx.lineTo(x, y)
  ctx.stroke()

  lastX.value = x
  lastY.value = y
}

// 停止绘制
const stopDrawing = () => {
  isDrawing.value = false
}

// 触摸事件处理
const handleTouchStart = (e) => {
  e.preventDefault()
  const touch = e.touches[0]
  const mouseEvent = new MouseEvent('mousedown', {
    clientX: touch.clientX,
    clientY: touch.clientY
  })
  noteCanvas.value.dispatchEvent(mouseEvent)
}

const handleTouchMove = (e) => {
  e.preventDefault()
  const touch = e.touches[0]
  const mouseEvent = new MouseEvent('mousemove', {
    clientX: touch.clientX,
    clientY: touch.clientY
  })
  noteCanvas.value.dispatchEvent(mouseEvent)
}

// 卡片拖拽
const handleMouseDown = (e) => {
  if (e.target.closest('.card-header')) {
    isDragging.value = true
    startX.value = e.clientX - parseInt(cardStyle.left)
    startY.value = e.clientY - parseInt(cardStyle.top)

    document.addEventListener('mousemove', handleMouseMove)
    document.addEventListener('mouseup', handleMouseUp)
  }
}

const handleMouseMove = (e) => {
  if (isDragging.value) {
    const newLeft = e.clientX - startX.value
    const newTop = e.clientY - startY.value

    // 限制在容器内
    const maxLeft = window.innerWidth - parseInt(cardStyle.width) - 20
    const maxTop = window.innerHeight - parseInt(cardStyle.height) - 20

    cardStyle.left = `${Math.max(0, Math.min(newLeft, maxLeft))}px`
    cardStyle.top = `${Math.max(0, Math.min(newTop, maxTop))}px`
  }
}

const handleMouseUp = () => {
  isDragging.value = false
  document.removeEventListener('mousemove', handleMouseMove)
  document.removeEventListener('mouseup', handleMouseUp)
  // 保存位置
  saveCardPosition()
}

// 卡片调整大小
const startResizing = (e) => {
  e.stopPropagation()
  isResizing.value = true
  startX.value = e.clientX
  startY.value = e.clientY
  startWidth.value = parseInt(cardStyle.width)
  startHeight.value = parseInt(cardStyle.height)

  document.addEventListener('mousemove', handleResize)
  document.addEventListener('mouseup', stopResizing)
}

const handleResize = (e) => {
  if (isResizing.value) {
    const widthChange = e.clientX - startX.value
    const heightChange = e.clientY - startY.value

    const newWidth = Math.max(200, startWidth.value + widthChange)
    const newHeight = Math.max(300, startHeight.value + heightChange)

    cardStyle.width = `${newWidth}px`
    cardStyle.height = `${newHeight}px`

    // 更新画布尺寸
    canvasWidth.value = newWidth - 40
    canvasHeight.value = Math.max(100, newHeight - 200)
  }
}

const stopResizing = () => {
  isResizing.value = false
  document.removeEventListener('mousemove', handleResize)
  document.removeEventListener('mouseup', stopResizing)
  // 保存尺寸
  saveCardPosition()
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

// 保存卡片位置和尺寸
const saveCardPosition = () => {
  const cardState = {
    id: props.cardId,
    left: cardStyle.left,
    top: cardStyle.top,
    width: cardStyle.width,
    height: cardStyle.height,
    showAnswer: showAnswer.value,
    showNotes: showNotes.value
  }

  // 保存到本地存储
  const existingCards = JSON.parse(localStorage.getItem('questionCards') || '{}')
  existingCards[props.cardId] = cardState
  localStorage.setItem('questionCards', JSON.stringify(existingCards))
}

// 加载卡片位置和尺寸
const loadCardPosition = () => {
  const existingCards = JSON.parse(localStorage.getItem('questionCards') || '{}')
  if (existingCards[props.cardId]) {
    const state = existingCards[props.cardId]
    cardStyle.left = state.left
    cardStyle.top = state.top
    if (state.width) cardStyle.width = state.width
    if (state.height) cardStyle.height = state.height
    showAnswer.value = state.showAnswer || false
    showNotes.value = state.showNotes || false

    canvasWidth.value = parseInt(cardStyle.width) - 40
    canvasHeight.value = Math.max(100, parseInt(cardStyle.height) - 200)
  }
}

// 保存笔记到后端
const saveNoteToBackend = async () => {
  const canvas = noteCanvas.value
  if (canvas) {
    const noteData = canvas.toDataURL()
    try {
      // 这里需要获取用户信息，你需要根据实际项目修改
      const userStr = localStorage.getItem('user') || '{}'
      const userInfo = JSON.parse(userStr)

      await saveTopicDrillNoteApi({
        userId: userInfo.id,
        questionId: props.questionData.id,
        noteData: noteData,
        subjectId: props.questionData.subjectId || 0
      })
    } catch (error) {
      console.error('保存笔记失败:', error)
    }
  }
}

// 从后端加载笔记
const loadNoteFromBackend = async () => {
  try {
    const userStr = localStorage.getItem('user') || '{}'
    const userInfo = JSON.parse(userStr)

    const res = await loadTopicDrillNotesApi(
      userInfo.id,
      props.questionData.subjectId || 0
    )

    if (res.code === 200 && res.data) {
      const noteData = res.data[props.questionData.id]
      if (noteData) {
        const canvas = noteCanvas.value
        if (canvas) {
          const ctx = canvas.getContext('2d')
          const img = new Image()
          img.onload = () => {
            ctx.drawImage(img, 0, 0)
          }
          img.src = noteData
        }
      }
    }
  } catch (error) {
    console.error('加载笔记失败:', error)
  }
}

// 标记为正确
const markAsCorrect = () => {
  props.questionData.isCorrect = true
  props.questionData.isAnswered = true
  saveCardPosition()
  ElMessage.success('已标记为正确')
}

// 标记为错误
const markAsIncorrect = () => {
  props.questionData.isCorrect = false
  props.questionData.isAnswered = true
  saveCardPosition()
  ElMessage.warning('已标记为错误')
}

// 暴露给父组件的方法
const resetPosition = () => {
  cardStyle.left = `${Math.random() * 100}px`
  cardStyle.top = `${Math.random() * 100}px`
  saveCardPosition()
}

const getCardState = () => {
  const canvas = noteCanvas.value
  const noteData = canvas ? canvas.toDataURL() : null

  return {
    id: props.cardId,
    position: {
      left: cardStyle.left,
      top: cardStyle.top,
      width: cardStyle.width,
      height: cardStyle.height
    },
    showAnswer: showAnswer.value,
    showNotes: showNotes.value,
    noteData: noteData,
    questionData: props.questionData
  }
}

// 定义事件
const emit = defineEmits(['delete-card'])

// 组件初始化
onMounted(() => {
  loadCardPosition()
  nextTick(() => {
    if (showNotes.value) {
      initCanvas()
    }
  })
})

// 暴露公共方法
defineExpose({
  resetPosition,
  getCardState,
  toggleAnswer,
  toggleNotes
})
</script>

<style scoped>
.question-card {
  position: absolute;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  cursor: move;
  transition: box-shadow 0.3s ease;
  overflow: hidden;
}

.question-card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  cursor: move;
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
.answer-section {
  margin-bottom: 16px;
}

.question-section h3,
.answer-section h3,
.note-section h3 {
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
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.question-text,
.answer-text {
  font-size: 14px;
  line-height: 1.6;
  color: #555;
  margin: 8px 0;
}

.image-fallback {
  text-align: center;
  padding: 20px;
  color: #999;
  background: #f9f9f9;
  border-radius: 8px;
  font-size: 12px;
}

.note-section {
  margin-top: 16px;
  border-top: 1px solid #eee;
  padding-top: 16px;
}

.note-toolbar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 8px;
  flex-wrap: wrap;
}

.tool-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tool-group label {
  font-size: 12px;
  color: #666;
  font-weight: 500;
}

.color-picker {
  display: flex;
  gap: 4px;
}

.color-btn {
  width: 24px;
  height: 24px;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: transform 0.2s;
  border: 2px solid transparent;
}

.color-btn:hover {
  transform: scale(1.2);
}

.color-btn.active {
  border-color: #333;
}

.tool-group select {
  padding: 4px 8px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 12px;
  background: white;
  cursor: pointer;
}

.tool-group button {
  padding: 4px 8px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 12px;
  background: white;
  cursor: pointer;
  transition: background 0.2s;
}

.tool-group button:hover {
  background: #f0f0f0;
}

.note-canvas-container {
  margin: 8px 0;
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  background: white;
}

.note-canvas {
  display: block;
  cursor: crosshair;
}

.note-controls {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
}

.note-controls button {
  padding: 6px 12px;
  background: #f1f5f9;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  color: #64748b;
  transition: background 0.3s ease;
}

.note-controls button:hover {
  background: #e2e8f0;
}

.add-note-btn {
  position: absolute;
  bottom: 12px;
  right: 12px;
  padding: 8px 12px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  transition: background 0.3s ease;
}

.add-note-btn:hover {
  background: #5a67d8;
}

.resize-handle {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 20px;
  height: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  cursor: nwse-resize;
  border-radius: 0 0 12px 0;
}

.resize-handle::after {
  content: '';
  position: absolute;
  bottom: 4px;
  right: 4px;
  width: 10px;
  height: 10px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 50%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .question-card {
    width: 250px !important;
    height: 350px !important;
  }

  .card-content {
    font-size: 13px;
  }

  .action-btn {
    padding: 4px 8px;
    font-size: 11px;
  }
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