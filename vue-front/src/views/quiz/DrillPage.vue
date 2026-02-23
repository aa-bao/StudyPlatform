<template>
  <div class="drill-page-container">
    <!-- 页面头部 -->
    <header class="drill-header">
      <div class="header-left">
        <button class="back-btn" @click="goBack">
          <i class="icon">←</i> 返回
        </button>
        <h1 class="page-title">{{ knowledgePointName }}</h1>
      </div>
      <div class="header-right">
        <el-tooltip content="打开草稿纸" placement="top">
          <el-button class="draw-btn-footer" @click="toggleDrawingBoard" round>
            <el-icon>
              <EditPen />
            </el-icon>
            <span style="margin-left: 4px;">草稿纸</span>
          </el-button>
        </el-tooltip>
        <button class="control-btn" @click="saveSession">
          保存进度
        </button>
        <button class="control-btn" @click="resetLayout">
          重置进度
        </button>
      </div>
    </header>

    <!-- 主内容区域 -->
    <main class="drill-content">
      <!-- 画板覆盖层 -->
      <transition name="el-fade-in">
        <div v-if="showDrawingBoard" class="drawing-board-overlay">
          <div class="drawing-toolbar">
            <el-radio-group v-model="currentTool" size="small" class="tool-group">
              <el-radio-button label="pen">
                <el-icon>
                  <EditPen />
                </el-icon> 画笔
              </el-radio-button>
              <el-radio-button label="eraser">
                <el-icon>
                  <Delete />
                </el-icon> 橡皮
              </el-radio-button>
            </el-radio-group>

            <el-divider direction="vertical" />

            <div class="color-picker" v-if="currentTool === 'pen'">
              <div v-for="color in colors" :key="color" class="color-dot"
                :style="{ backgroundColor: color, transform: currentColor === color ? 'scale(1.2)' : 'scale(1)' }"
                :class="{ active: currentColor === color }" @click="currentColor = color"></div>
            </div>

            <el-divider direction="vertical" />

            <div class="width-slider" style="width: 100px; margin-right: 10px">
              <el-slider v-model="currentWidth" :min="1" :max="20" :show-tooltip="false" size="small" />
            </div>

            <el-button-group>
              <el-button @click="undoStroke" icon="RefreshLeft" :disabled="history.length === 0">撤回</el-button>
              <el-button @click="clearCanvas" icon="Delete">清空</el-button>
              <el-button @click="showDrawingBoard = false" icon="Close">关闭</el-button>
            </el-button-group>
          </div>
          <canvas ref="drawingCanvas" class="drawing-canvas"
            @mousedown="startDrawing"
            @mousemove="draw"
            @mouseup="stopDrawing"
            @mouseleave="stopDrawing"
            @touchstart="handleTouchStart"
            @touchmove="handleTouchMove"
            @touchend="stopDrawing"
            @touchcancel="stopDrawing"></canvas>
        </div>
      </transition>

      <!-- 题目卡片容器 -->
      <div class="question-cards-container">
        <QuestionCard
          v-for="(question, index) in questionsData"
          :key="question.id"
          :questionData="question"
          :questionIndex="index"
          :cardId="`card_${question.id}`"
          :style="getCardPositionStyle(index)"
          @card-moved="handleCardMoved"
          @question-updated="calculateStats"
        />
      </div>
    </main>

    <!-- 页面底部 -->
    <footer class="drill-footer">
      <div class="footer-stats">
        <span class="stat-item">
          <span class="stat-value">{{ questionsData.length }}</span>
          <span class="stat-label">题目总数</span>
        </span>
        <span class="stat-item">
          <span class="stat-value">{{ answeredCount }}</span>
          <span class="stat-label">已答题目</span>
        </span>
        <span class="stat-item">
          <span class="stat-value">{{ correctCount }}</span>
          <span class="stat-label">正确答案</span>
        </span>
        <span class="stat-item">
          <span class="stat-value">{{ accuracy }}%</span>
          <span class="stat-label">正确率</span>
        </span>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getQuestionsByKnowledgePointApi } from '@/api/question'
import { saveTopicDrillProgressApi, loadTopicDrillProgressApi } from '@/api/topicDrill'
import { ElMessage } from 'element-plus'
import { EditPen, Delete, Close, RefreshLeft } from '@element-plus/icons-vue'
import QuestionCard from '@/components/DrillQuestionCard.vue'

const router = useRouter()
const route = useRoute()

// 获取路由参数
const knowledgePointId = parseInt(route.query.knowledgePointId) || 0
const knowledgePointName = route.query.knowledgePointName || '知识点练习'

// 题目数据状态
const questionsData = ref([]) // 题目数据
const answeredCount = ref(0) // 已答题目数量
const correctCount = ref(0) // 正确答案数量
const accuracy = ref(0) // 正确率
const cardPositions = ref({}) // 卡片位置记录

// 画板相关
const showDrawingBoard = ref(false)
const drawingCanvas = ref(null)
const isDrawing = ref(false)
const ctx = ref(null)

// 画板新状态
const currentTool = ref('pen')
const currentColor = ref('#f56c6c')
const currentWidth = ref(3)
const colors = ['#f56c6c', '#409EFF', '#67C23A', '#303133']
const history = ref([])
const currentPath = ref([])

// 卡片布局配置
const cardGap = 200 // 卡片间距
const cols = 4 // 一行4张卡

// 计算卡片位置样式（自适应布局）
const getCardPositionStyle = (index) => {
  // 如果有保存的位置，使用保存的位置
  if (cardPositions.value[index]) {
    return cardPositions.value[index]
  }

  // 否则计算默认位置
  const col = index % cols
  const row = Math.floor(index / cols)

  // 动态计算卡片尺寸，不使用固定宽高
  // 根据列数和间距计算位置，让卡片内容决定大小
  const estimatedWidth = 280 // 预估卡片宽度
  const estimatedHeight = 350 // 预估卡片高度

  return {
    left: `${col * (estimatedWidth + cardGap)}px`,
    top: `${row * (estimatedHeight + cardGap)}px`,
    position: 'absolute'
  }
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 画板功能
const toggleDrawingBoard = async () => {
  showDrawingBoard.value = true
  await nextTick()
  initCanvas()
}

// 加载知识点题目数据
const loadQuestionsByKnowledgePoint = async (knowledgePointId) => {
  try {
    console.log('开始加载题目数据，知识点ID:', knowledgePointId)
    // 调用API获取题目数据
    const res = await getQuestionsByKnowledgePointApi(knowledgePointId)
    console.log('API响应:', res)
    if (res.code === 200) {
      console.log('API返回的题目数据:', res.data)
      // 处理题目数据，添加必要字段
      questionsData.value = res.data.map(question => ({
        id: question.id,
        title: `题目 ${question.id}`,
        content: question.content || '暂无题目内容',
        answer: question.answer || '暂无答案',
        analysis: question.analysis || '暂无解析',
        questionImage: question.contentJson?.questionImage || null,
        answerImage: question.contentJson?.answerImage || null,
        difficulty: question.difficulty || 3,
        isAnswered: false,
        isCorrect: null
      }))
      console.log('处理后的题目数据:', questionsData.value)
      calculateStats()
      ElMessage.success('题目加载成功')
    }
  } catch (error) {
    ElMessage.error('题目加载失败')
    console.error('Error loading questions:', error)
  }
}

// 生成模拟题目数据
const generateMockQuestions = () => {
  const count = Math.floor(Math.random() * 20) + 10
  const questions = []

  for (let i = 0; i < count; i++) {
    // 生成随机图片尺寸，让卡片大小自适应
    const width = Math.floor(Math.random() * 200) + 300 // 300-500px
    const height = Math.floor(Math.random() * 150) + 150 // 150-300px

    questions.push({
      id: `q_${Date.now()}_${i}`,
      title: `题目 ${i + 1}`,
      content: `这是第${i + 1}道题的内容，包含了${knowledgePointName}相关的知识点`,
      answer: `这是第${i + 1}道题的答案`,
      analysis: `这是第${i + 1}道题的详细解析，帮助您理解和掌握${knowledgePointName}相关的知识点。解析内容包括：1) 题目考点分析；2) 解题思路讲解；3) 易错点提醒；4) 知识点扩展。`,
      questionImage: `https://picsum.photos/seed/question_${i}_${Date.now()}/${width}/${height}`,
      answerImage: `https://picsum.photos/seed/answer_${i}_${Date.now()}/${width}/${height}`,
      difficulty: Math.floor(Math.random() * 3) + 1, // 1-简单, 2-中等, 3-困难
      isAnswered: false,
      isCorrect: null
    })
  }
  return questions
}

// 计算统计数据
const calculateStats = () => {
  answeredCount.value = questionsData.value.filter(q => q.isAnswered).length
  correctCount.value = questionsData.value.filter(q => q.isAnswered && q.isCorrect).length
  accuracy.value = answeredCount.value > 0 ?
    Math.round((correctCount.value / answeredCount.value) * 100) : 0
}

// 画板功能方法
const initCanvas = () => {
  const canvas = drawingCanvas.value
  if (!canvas) return

  canvas.width = window.innerWidth
  canvas.height = window.innerHeight
  ctx.value = canvas.getContext('2d')
  ctx.value.lineCap = 'round'
  ctx.value.lineJoin = 'round'

  // 重绘历史
  redrawCanvas()
}

const startDrawing = (e) => {
  isDrawing.value = true
  const { clientX, clientY } = e.touches ? e.touches[0] : e

  // 记录新路径的起点
  currentPath.value = [{ x: clientX, y: clientY }]

  ctx.value.beginPath()
  ctx.value.moveTo(clientX, clientY)

  // 设置当前笔触样式
  updateContextStyle()
}

const updateContextStyle = () => {
  if (currentTool.value === 'eraser') {
    ctx.value.globalCompositeOperation = 'destination-out'
    ctx.value.lineWidth = currentWidth.value * 2 // 橡皮擦粗一点
  } else {
    ctx.value.globalCompositeOperation = 'source-over'
    ctx.value.lineWidth = currentWidth.value
    ctx.value.strokeStyle = currentColor.value
  }
}

const draw = (e) => {
  if (!isDrawing.value) return
  const { clientX, clientY } = e.touches ? e.touches[0] : e

  // 记录路径点
  currentPath.value.push({ x: clientX, y: clientY })

  ctx.value.lineTo(clientX, clientY)
  ctx.value.stroke()
}

const handleTouchStart = (e) => {
  e.preventDefault()
  const touch = e.touches[0]
  const mouseEvent = new MouseEvent('mousedown', {
    clientX: touch.clientX,
    clientY: touch.clientY
  })
  drawingCanvas.value.dispatchEvent(mouseEvent)
}

const handleTouchMove = (e) => {
  e.preventDefault()
  const touch = e.touches[0]
  const mouseEvent = new MouseEvent('mousemove', {
    clientX: touch.clientX,
    clientY: touch.clientY
  })
  drawingCanvas.value.dispatchEvent(mouseEvent)
}

const stopDrawing = () => {
  if (!isDrawing.value) return
  isDrawing.value = false

  // 保存当前路径到历史
  if (currentPath.value.length > 0) {
    history.value.push({
      tool: currentTool.value,
      color: currentColor.value,
      width: currentTool.value === 'eraser' ? currentWidth.value * 2 : currentWidth.value,
      points: [...currentPath.value]
    })
  }
  currentPath.value = []

  // 自动保存草稿
  saveDraft()
}

const redrawCanvas = () => {
  if (!ctx.value || !drawingCanvas.value) return
  const canvas = drawingCanvas.value

  // 清空画布
  ctx.value.clearRect(0, 0, canvas.width, canvas.height)

  // 遍历历史重绘
  history.value.forEach(path => {
    ctx.value.beginPath()
    const points = path.points
    if (points.length === 0) return

    ctx.value.moveTo(points[0].x, points[0].y)
    for (let i = 1; i < points.length; i++) {
      ctx.value.lineTo(points[i].x, points[i].y)
    }

    if (path.tool === 'eraser') {
      ctx.value.globalCompositeOperation = 'destination-out'
      ctx.value.lineWidth = path.width
    } else {
      ctx.value.globalCompositeOperation = 'source-over'
      ctx.value.strokeStyle = path.color
      ctx.value.lineWidth = path.width
    }
    ctx.value.stroke()
  })
}

const undoStroke = () => {
  if (history.value.length > 0) {
    history.value.pop()
    redrawCanvas()
  }
}

const clearCanvas = () => {
  const canvas = drawingCanvas.value
  if (canvas && ctx.value) {
    ctx.value.clearRect(0, 0, canvas.width, canvas.height)
    history.value = [] // 清空历史
  }
}

// 保存草稿到本地存储
const saveDraft = () => {
  const canvas = drawingCanvas.value
  if (canvas) {
    const draftData = canvas.toDataURL()
    const key = `draft_${knowledgePointId}`
    localStorage.setItem(key, draftData)
  }
}

// 加载本地存储的草稿
const loadDraft = () => {
  const key = `draft_${knowledgePointId}`
  const draftData = localStorage.getItem(key)
  if (draftData) {
    const canvas = drawingCanvas.value
    if (canvas) {
      const img = new Image()
      img.onload = () => {
        ctx.value.drawImage(img, 0, 0)
        // 由于我们使用了路径记录系统，这里需要重新构建历史记录
        // 简单处理：创建一个包含整个画布内容的单一路径
        history.value = [{
          tool: 'pen',
          color: currentColor.value,
          width: currentWidth.value,
          points: [] // 简化处理，不记录具体路径
        }]
      }
      img.src = draftData
    }
  }
}

// 保存答题进度
const saveSession = async () => {
  try {
    const userStr = localStorage.getItem('user') || '{}'
    const userInfo = JSON.parse(userStr)

    const progressData = {
      userId: userInfo.id,
      subjectId: knowledgePointId,
      questionCount: questionsData.value.length,
      answeredCount: answeredCount.value,
      correctCount: correctCount.value,
      accuracy: accuracy.value,
      questionsData: JSON.stringify(questionsData.value),
      cardPositions: JSON.stringify(cardPositions.value),
      timestamp: new Date().toISOString()
    }

    console.log('保存进度数据:', progressData)
    const res = await saveTopicDrillProgressApi(progressData)
    if (res.code === 200) {
      ElMessage.success('答题进度已保存')
    } else {
      ElMessage.error('保存失败: ' + res.msg)
    }
  } catch (error) {
    console.error('保存答题进度失败:', error)
    ElMessage.error('保存失败')
  }
}

// 加载答题进度
const loadSession = async () => {
  console.log('开始加载答题进度')
  try {
    const userStr = localStorage.getItem('user') || '{}'
    const userInfo = JSON.parse(userStr)
    console.log('用户信息:', userInfo)

    const res = await loadTopicDrillProgressApi(userInfo.id, knowledgePointId)
    console.log('加载答题进度API响应:', res)

    if (res.code === 200 && res.data) {
      const progressData = res.data
      console.log('进度数据:', progressData)
      // 加载题目数据并确保每个题目都包含analysis字段
      questionsData.value = (JSON.parse(progressData.questionsData) || []).map(question => ({
        ...question,
        analysis: question.analysis || '暂无解析' // 确保有解析字段
      }))
      console.log('从本地存储加载的题目数据:', questionsData.value)
      cardPositions.value = JSON.parse(progressData.cardPositions) || {}
      answeredCount.value = progressData.answeredCount || 0
      correctCount.value = progressData.correctCount || 0
      accuracy.value = progressData.accuracy || 0
      ElMessage.success('答题进度已加载')
    } else {
      console.log('没有找到答题进度')
      ElMessage.info('暂无答题进度')
    }
  } catch (error) {
    console.error('加载答题进度失败:', error)
  }
}

// 处理卡片移动事件
const handleCardMoved = (index, newPosition) => {
  cardPositions.value[index] = newPosition
}

// 重置布局和进度
const resetLayout = () => {
  cardPositions.value = {}
  localStorage.removeItem(`draft_${knowledgePointId}`)
  // 重置答题进度，清除本地存储的数据
  questionsData.value = []
  answeredCount.value = 0
  correctCount.value = 0
  accuracy.value = 0
  // 重新加载题目数据
  loadQuestionsByKnowledgePoint(knowledgePointId)
  clearCanvas()
  ElMessage.success('布局和进度已重置，重新加载题目数据')
}

// 页面初始化
onMounted(async () => {
  console.log('页面开始初始化')
  // 加载答题进度
  await loadSession()

  console.log('加载答题进度后，题目数据长度:', questionsData.value.length)
  // 如果没有加载到进度，加载新题目
  if (questionsData.value.length === 0) {
    console.log('没有加载到答题进度，开始加载新题目')
    await loadQuestionsByKnowledgePoint(knowledgePointId)
  }

  console.log('最终题目数据:', questionsData.value)
  // 动态调整卡片布局
  await adjustCardLayout()
  console.log('页面初始化完成')
})

// 监听画板显示，加载草稿
watch(showDrawingBoard, async (newVal) => {
  if (newVal) {
    await nextTick()
    initCanvas()
    loadDraft()
  }
})

// 动态调整卡片布局
const adjustCardLayout = async () => {
  await nextTick()

  // 遍历所有卡片，获取实际尺寸并调整位置
  const cards = document.querySelectorAll('.question-card')
  if (cards.length > 0) {
    const positions = {}
    const cardWidths = []
    const cardHeights = []

    // 先计算所有卡片的实际尺寸
    cards.forEach((card, index) => {
      const rect = card.getBoundingClientRect()
      cardWidths[index] = rect.width
      cardHeights[index] = rect.height
    })

    // 根据实际尺寸计算位置
    cards.forEach((card, index) => {
      const col = index % cols
      const row = Math.floor(index / cols)

      // 计算当前列所有卡片的最大宽度和行高
      const columnCards = []
      for (let i = row * cols; i < Math.min((row + 1) * cols, cards.length); i++) {
        columnCards.push(cardWidths[i])
      }

      const maxWidth = Math.max(...columnCards, 250) // 最小宽度250px
      const maxHeight = Math.max(...cardHeights.slice(row * cols, Math.min((row + 1) * cols, cards.length)), 300) // 最小高度300px

      // 计算位置
      const left = col * (maxWidth + cardGap)
      const top = row * (maxHeight + cardGap)

      positions[index] = {
        left: `${left}px`,
        top: `${top}px`,
        position: 'absolute'
      }
    })

    // 更新卡片位置
    cardPositions.value = positions
  }
}
</script>

<style scoped>
.drill-page-container {
  position: relative;
  height: 100vh;
  overflow: hidden;
  background: #fafbfc;
  display: flex;
  flex-direction: column;
}

/* 页面头部 */
.drill-header {
  background: white;
  border-bottom: 1px solid #e2e8f0;
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.back-btn {
  padding: 8px 16px;
  background: #f1f5f9;
  border: none;
  border-radius: 8px;
  color: #475569;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.back-btn:hover {
  background: #e2e8f0;
  color: #1e293b;
}

.page-title {
  margin: 0;
  font-size: 1.5rem;
  color: #1e293b;
  font-weight: 600;
}

.header-right {
  display: flex;
  gap: 12px;
}

.control-btn {
  padding: 8px 16px;
  background: #f1f5f9;
  border: none;
  border-radius: 8px;
  color: #475569;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.control-btn:hover {
  background: #e2e8f0;
  color: #1e293b;
}

/* 主内容区域 */
.drill-content {
  flex: 1;
  position: relative;
  padding: 20px;
  overflow: auto;
  background: #fafbfc;
}

/* 题目卡片容器 */
.question-cards-container {
  position: relative;
  width: fit-content;
  min-height: 100%;
}

/* 画板覆盖层 */
.drawing-board-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(255, 255, 255, 0.1);
  z-index: 3000;
  cursor: crosshair;
}

.drawing-toolbar {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 3001;
  background: #fff;
  padding: 8px 12px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 12px;
}

.color-picker {
  display: flex;
  gap: 8px;
}

.color-dot {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  cursor: pointer;
  border: 2px solid #fff;
  box-shadow: 0 0 0 1px #dcdfe6;
  transition: transform 0.2s;
}

.color-dot:hover {
  transform: scale(1.1);
}

.color-dot.active {
  box-shadow: 0 0 0 2px #409EFF;
}

.drawing-canvas {
  width: 100%;
  height: 100%;
  touch-action: none;
}

/* 页面底部 */
.drill-footer {
  background: white;
  border-top: 1px solid #e2e8f0;
  padding: 16px 24px;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;
}

.footer-stats {
  display: flex;
  gap: 30px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 1.2rem;
  font-weight: 600;
  color: #1e293b;
}

.stat-label {
  font-size: 0.8rem;
  color: #64748b;
  margin-top: 4px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .draft-toolbar {
    width: 90%;
    left: 5%;
    transform: none;
  }

  .drill-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
    padding: 12px 16px;
  }

  .header-right {
    width: 100%;
    justify-content: space-between;
  }

  .page-title {
    font-size: 1.2rem;
  }

  .drill-content {
    padding: 16px;
  }

  .footer-stats {
    gap: 20px;
  }

  .stat-value {
    font-size: 1rem;
  }
}
</style>