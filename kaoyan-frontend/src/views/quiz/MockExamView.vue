<template>
  <div class="mock-exam-view">
    <!-- State: Pending (Start Screen) -->
    <div v-if="examStore.examStatus === 'pending'" class="start-screen">
      <div class="paper-card intro-card">
        <h1>{{ examStore.examInfo.title }}</h1>
        <div class="exam-rules">
          <p><el-icon><Timer /></el-icon> 考试时间：{{ examStore.examInfo.totalTime / 60 }} 分钟</p>
          <p><el-icon><List /></el-icon> 总题数：{{ examStore.totalCount }} 题</p>
          <p class="warning"><el-icon><Warning /></el-icon> 注意：切屏次数将被记录，请诚信作答。</p>
        </div>
        <el-button type="primary" size="large" @click="startExam">开始考试</el-button>
      </div>
    </div>

    <!-- State: In Progress -->
    <div v-else-if="examStore.examStatus === 'in-progress'" class="exam-layout">
      <ExamHeader />
      
      <div class="main-content">
        <!-- Left: Question Stream -->
        <div class="question-stream-container">
          <div class="paper-texture">
            <div class="seal-line"><span>密封线内不要答题</span></div>
            <div class="question-stream">
              <QuestionItem 
                v-for="(q, index) in examStore.questions" 
                :key="q.id" 
                :question="q" 
                :index="index" 
              />
            </div>
          </div>
        </div>

        <!-- Right: Answer Sheet -->
        <div class="sidebar">
          <AnswerSheet />
          
          <div class="tools-panel">
            <el-button @click="showScratchPad = !showScratchPad" style="width: 100%; margin-top: 10px;">
              {{ showScratchPad ? '隐藏草稿纸' : '打开草稿纸' }}
            </el-button>
          </div>
        </div>
      </div>

      <!-- Floating Tools -->
      <ScratchPad :visible="showScratchPad" @close="showScratchPad = false" />
    </div>

    <!-- State: Grading (Loading) -->
    <div v-else-if="examStore.examStatus === 'grading'" class="loading-screen">
      <div class="loading-content">
        <div class="loader"></div>
        <h2>正在阅卷中...</h2>
        <p>AI正在分析您的作答，请稍候</p>
      </div>
    </div>

    <!-- State: Finished (Score Report) -->
    <div v-else-if="examStore.examStatus === 'finished'" class="result-screen">
      <ScoreReport />
    </div>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref, watch } from 'vue'
import { useExamStore } from '@/stores/exam'
import { Timer, List, Warning } from '@element-plus/icons-vue'
import ExamHeader from '@/components/exam/ExamHeader.vue'
import QuestionItem from '@/components/exam/QuestionItem.vue'
import AnswerSheet from '@/components/exam/AnswerSheet.vue'
import ScratchPad from '@/components/exam/ScratchPad.vue'
import ScoreReport from '@/components/exam/ScoreReport.vue'
import { ElNotification } from 'element-plus'

const examStore = useExamStore()
const showScratchPad = ref(false)
let timerInterval = null

const startExam = () => {
  examStore.initExam()
  startTimer()
}

const startTimer = () => {
  if (timerInterval) clearInterval(timerInterval)
  timerInterval = setInterval(() => {
    examStore.tickTimer()
  }, 1000)
}

// Anti-cheating: Visibility Change
const handleVisibilityChange = () => {
  if (document.hidden && examStore.examStatus === 'in-progress') {
    examStore.incrementSwitchCount()
    ElNotification({
      title: '警告',
      message: `检测到切屏行为！当前切屏次数：${examStore.switchCount}`,
      type: 'warning',
      duration: 3000
    })
  }
}

onMounted(() => {
  document.addEventListener('visibilitychange', handleVisibilityChange)
  // If we reload page and state is in-progress, resume timer
  if (examStore.examStatus === 'in-progress') {
    startTimer()
  }
})

onUnmounted(() => {
  if (timerInterval) clearInterval(timerInterval)
  document.removeEventListener('visibilitychange', handleVisibilityChange)
})
</script>

<style scoped>
.mock-exam-view {
  min-height: 100vh;
  background-color: #f0f2f5;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}

/* Start Screen */
.start-screen {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.intro-card {
  width: 500px;
  padding: 40px;
  text-align: center;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.exam-rules {
  text-align: left;
  margin: 30px 0;
  padding: 20px;
  background: #f9fafe;
  border-radius: 4px;
}

.exam-rules p {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 10px 0;
  font-size: 16px;
}

.warning {
  color: #f56c6c;
  font-weight: bold;
}

/* Main Layout */
.exam-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.main-content {
  flex: 1;
  display: flex;
  padding: 20px;
  gap: 20px;
  overflow: hidden;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.question-stream-container {
  flex: 1;
  overflow-y: auto;
  padding-right: 10px;
  /* Custom Scrollbar */
  scrollbar-width: thin;
  scrollbar-color: #c0c4cc #f0f2f5;
}

.paper-texture {
  background-color: #fdfbf3; /* Paper off-white */
  background-image: url("data:image/svg+xml,%3Csvg width='100' height='100' viewBox='0 0 100 100' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M11 18c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm48 25c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm-43-7c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm63 31c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM34 90c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm56-76c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM12 86c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm28-65c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm23-11c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-6 60c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm29 22c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zM32 63c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm57-13c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-9-21c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM60 91c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM35 41c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM12 60c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2z' fill='%239C92AC' fill-opacity='0.03' fill-rule='evenodd'/%3E%3C/svg%3E");
  padding: 40px 60px;
  min-height: 100%;
  box-shadow: 0 0 20px rgba(0,0,0,0.1);
  position: relative;
  border-left: 1px solid #e0e0e0;
}

/* Seal Line (Mifeng Xian) */
.seal-line {
  position: absolute;
  left: 30px;
  top: 0;
  bottom: 0;
  width: 0;
  border-left: 2px dashed #999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.seal-line span {
  writing-mode: vertical-rl;
  background: #fdfbf3;
  padding: 10px 0;
  color: #999;
  font-size: 14px;
  letter-spacing: 10px;
}

.question-stream {
  margin-left: 20px;
}

.sidebar {
  width: 300px;
  display: flex;
  flex-direction: column;
}

/* Loading Screen */
.loading-screen {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: rgba(255,255,255,0.9);
}

.loading-content {
  text-align: center;
}

.loader {
  border: 4px solid #f3f3f3;
  border-radius: 50%;
  border-top: 4px solid #3498db;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
