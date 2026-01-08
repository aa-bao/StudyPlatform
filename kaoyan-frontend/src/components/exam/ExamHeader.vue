<template>
  <div class="exam-header">
    <div class="left-section">
      <h2 class="exam-title">{{ examStore.examInfo.title }}</h2>
    </div>

    <div class="center-section">
      <div class="timer-box" :class="{ warning: examStore.examInfo.remainingTime < 300 }">
        <el-icon><Timer /></el-icon>
        <span class="time-text">{{ formattedTime }}</span>
      </div>
      
      <div class="progress-box">
        <span class="label">进度: {{ examStore.answeredCount }} / {{ examStore.totalCount }}</span>
        <el-progress 
          :percentage="examStore.progressPercentage" 
          :show-text="false" 
          :stroke-width="8"
          class="progress-bar"
        />
      </div>
    </div>

    <div class="right-section">
      <el-button @click="toggleFullScreen" plain size="small">
        <el-icon><FullScreen /></el-icon>
        {{ isFullScreen ? '退出全屏' : '全屏模式' }}
      </el-button>
      <el-button type="primary" size="small" @click="handleHandIn">
        交卷
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useExamStore } from '@/stores/exam'
import { Timer, FullScreen } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

const examStore = useExamStore()
const isFullScreen = ref(false)

const formattedTime = computed(() => {
  const t = examStore.examInfo.remainingTime
  const h = Math.floor(t / 3600)
  const m = Math.floor((t % 3600) / 60)
  const s = t % 60
  return `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
})

const toggleFullScreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen().catch((err) => {
      console.error(`Error attempting to enable full-screen mode: ${err.message}`)
    })
    isFullScreen.value = true
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen()
      isFullScreen.value = false
    }
  }
}

const handleHandIn = () => {
  ElMessageBox.confirm(
    '确认提交试卷吗？提交后无法修改答案。',
    '交卷确认',
    {
      confirmButtonText: '确认交卷',
      cancelButtonText: '继续检查',
      type: 'warning',
    }
  ).then(() => {
    examStore.submitExam()
  }).catch(() => {})
}
</script>

<style scoped>
.exam-header {
  height: 60px;
  background-color: #fff;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  z-index: 100;
  position: relative;
}

.exam-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.center-section {
  display: flex;
  align-items: center;
  gap: 30px;
}

.timer-box {
  display: flex;
  align-items: center;
  gap: 8px;
  font-family: 'Courier New', monospace;
  font-size: 20px;
  font-weight: bold;
  color: #333;
  padding: 4px 12px;
  border-radius: 4px;
  background: #f5f7fa;
}

.timer-box.warning {
  color: #f56c6c;
  background: #fef0f0;
  animation: pulse 1s infinite;
}

.progress-box {
  display: flex;
  flex-direction: column;
  width: 200px;
}

.progress-box .label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 2px;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.6; }
  100% { opacity: 1; }
}

.right-section {
  display: flex;
  gap: 12px;
}
</style>
