<template>
  <div class="answer-sheet card-paper">
    <div class="sheet-header">
      <h3>答题卡</h3>
      <div class="legend">
        <span class="legend-item"><span class="dot done"></span>已做</span>
        <span class="legend-item"><span class="dot marked"></span>标记</span>
        <span class="legend-item"><span class="dot normal"></span>未做</span>
      </div>
    </div>

    <div class="sheet-body">
      <div class="question-grid">
        <div 
          v-for="(q, index) in examStore.questions" 
          :key="q.id"
          class="grid-item"
          :class="{
            'done': isAnswered(q.id),
            'marked': isMarked(q.id)
          }"
          @click="scrollToQuestion(q.id)"
        >
          {{ index + 1 }}
          <div v-if="isMarked(q.id)" class="mark-flag"></div>
        </div>
      </div>
    </div>
    
    <div class="sheet-footer">
       <p class="tips">点击题号可快速定位</p>
    </div>
  </div>
</template>

<script setup>
import { useExamStore } from '@/stores/exam'

const examStore = useExamStore()

const isAnswered = (id) => {
  return examStore.userAnswers[id] !== undefined && examStore.userAnswers[id] !== ''
}

const isMarked = (id) => {
  return !!examStore.markedQuestions[id]
}

const scrollToQuestion = (id) => {
  const el = document.getElementById(`q-${id}`)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'center' })
  }
}
</script>

<style scoped>
.answer-sheet {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  position: sticky;
  top: 20px;
  max-height: calc(100vh - 100px);
  overflow-y: auto;
}

.sheet-header {
  margin-bottom: 20px;
  text-align: center;
  border-bottom: 2px solid #eee;
  padding-bottom: 10px;
}

.sheet-header h3 {
  margin: 0 0 10px 0;
  color: #333;
}

.legend {
  display: flex;
  justify-content: center;
  gap: 15px;
  font-size: 12px;
  color: #666;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: inline-block;
}

.dot.normal { background: #f0f2f5; border: 1px solid #dcdfe6; }
.dot.done { background: #409eff; }
.dot.marked { background: #e6a23c; }

.question-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
}

.grid-item {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
  transition: all 0.2s;
  position: relative;
}

.grid-item:hover {
  border-color: #409eff;
  color: #409eff;
}

.grid-item.done {
  background-color: #409eff;
  color: #fff;
  border-color: #409eff;
}

.grid-item.marked {
  border-color: #e6a23c;
  background-color: #fdf6ec;
  color: #e6a23c;
}

.grid-item.marked.done {
  background-color: #e6a23c; /* Prioritize marked color or blend? Usually marked is distinct */
  color: #fff;
}

/* Optional small flag for marked */
.mark-flag {
  position: absolute;
  top: -2px;
  right: -2px;
  width: 8px;
  height: 8px;
  background: red;
  border-radius: 50%;
}

.sheet-footer {
  margin-top: 20px;
  text-align: center;
  font-size: 12px;
  color: #999;
}
</style>
