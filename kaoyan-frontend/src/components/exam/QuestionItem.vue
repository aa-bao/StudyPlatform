<template>
  <div class="question-item" :id="`q-${question.id}`">
    <div class="question-header">
      <span class="q-index">{{ index + 1 }}</span>
      <span class="q-type">[{{ typeLabel }}]</span>
      <span class="q-score">({{ question.score }}分)</span>
    </div>
    
    <div class="question-content" v-html="renderedContent"></div>

    <div class="answer-area">
      <!-- Single Choice -->
      <div v-if="question.type === 'single-choice'" class="options-group">
        <el-radio-group v-model="currentAnswer" @change="updateAnswer">
          <div v-for="(opt, idx) in question.options" :key="idx" class="option-item">
            <el-radio :label="optionLabels[idx]" :value="optionLabels[idx]">
              <span class="opt-label">{{ optionLabels[idx] }}.</span>
              <span class="opt-content" v-html="renderMath(opt)"></span>
            </el-radio>
          </div>
        </el-radio-group>
      </div>

      <!-- Fill Blank -->
      <div v-else-if="question.type === 'fill-blank'" class="fill-blank-area">
        <el-input 
          v-model="currentAnswer" 
          placeholder="请输入答案..." 
          @input="updateAnswer"
          type="textarea"
          :autosize="{ minRows: 1, maxRows: 3 }"
        />
      </div>

      <!-- Subjective -->
      <div v-else-if="question.type === 'subjective'" class="subjective-area">
        <el-input
          v-model="currentAnswer"
          type="textarea"
          :rows="6"
          placeholder="请输入解题过程..."
          @input="updateAnswer"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch, onMounted } from 'vue'
import katex from 'katex'
import { useExamStore } from '@/stores/exam'

const props = defineProps({
  question: {
    type: Object,
    required: true
  },
  index: {
    type: Number,
    required: true
  }
})

const examStore = useExamStore()
const currentAnswer = ref('')
const optionLabels = ['A', 'B', 'C', 'D', 'E', 'F']

// Sync local state with store
watch(() => examStore.userAnswers[props.question.id], (newVal) => {
  if (newVal !== undefined) {
    currentAnswer.value = newVal
  }
}, { immediate: true })

const typeLabel = computed(() => {
  const map = {
    'single-choice': '单选题',
    'multi-choice': '多选题',
    'fill-blank': '填空题',
    'subjective': '解答题'
  }
  return map[props.question.type] || '题目'
})

const renderMath = (text) => {
  if (!text) return ''
  // Replace $...$ with KaTeX rendered HTML
  return text.replace(/\$(.*?)\$/g, (match, formula) => {
    try {
      return katex.renderToString(formula, { throwOnError: false })
    } catch (e) {
      return match
    }
  })
}

const renderedContent = computed(() => {
  return renderMath(props.question.content)
})

const updateAnswer = (val) => {
  examStore.setAnswer(props.question.id, val)
}
</script>

<style scoped>
.question-item {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px dashed #dcdfe6;
}

.question-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  color: #606266;
  font-weight: bold;
}

.q-index {
  background: #409eff;
  color: #fff;
  width: 24px;
  height: 24px;
  border-radius: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
}

.question-content {
  font-size: 16px;
  line-height: 1.8;
  margin-bottom: 20px;
  color: #303133;
}

.options-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.option-item {
  display: flex;
  align-items: center;
}

.opt-label {
  font-weight: bold;
  margin-right: 8px;
}

/* Deep selector to handle KaTeX styles if needed */
:deep(.katex) {
  font-size: 1.1em;
}
</style>
