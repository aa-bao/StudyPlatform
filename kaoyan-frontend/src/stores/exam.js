import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useExamStore = defineStore('exam', () => {
  // --- State ---
  const examInfo = ref({
    title: '2025年考研数学一模拟卷',
    totalTime: 180 * 60, // 3 hours in seconds
    remainingTime: 180 * 60,
    startTime: null
  })

  // Mock Questions Data
  const questions = ref([
    {
      id: 1,
      type: 'single-choice',
      content: '设函数 $f(x) = \\frac{\\ln(1+x)}{x}$，则 $\\lim_{x\\to 0} f(x)$ 等于：',
      options: ['0', '1', 'e', '不存在'],
      score: 5
    },
    {
      id: 2,
      type: 'single-choice',
      content: '若矩阵 $A=\\begin{bmatrix} 1 & 2 \\\\ 3 & 4 \\end{bmatrix}$，则 $|A|$ 的值为：',
      options: ['-2', '2', '10', '-10'],
      score: 5
    },
    {
      id: 3,
      type: 'fill-blank',
      content: '曲线 $y=x^3-3x+1$ 的拐点坐标为 ______。',
      score: 5
    },
    {
      id: 4,
      type: 'subjective',
      content: '计算不定积分 $\\int x \\sin x \\, dx$。',
      score: 10
    },
    {
      id: 5,
      type: 'single-choice',
      content: '下列级数中收敛的是：',
      options: ['$\\sum_{n=1}^{\\infty} \\frac{1}{n}$', '$\\sum_{n=1}^{\\infty} \\frac{1}{\\sqrt{n}}$', '$\\sum_{n=1}^{\\infty} \\frac{(-1)^n}{n}$', '$\\sum_{n=1}^{\\infty} 2^n$'],
      score: 5
    }
  ])

  // Answers map: { questionId: value }
  const userAnswers = ref({})
  
  // Marks map: { questionId: boolean } (for "Mark for Review")
  const markedQuestions = ref({})

  const examStatus = ref('pending') // pending, in-progress, submitted, grading, finished
  const switchCount = ref(0) // Tab switching count

  // --- Getters ---
  const answeredCount = computed(() => Object.keys(userAnswers.value).length)
  const totalCount = computed(() => questions.value.length)
  const progressPercentage = computed(() => Math.round((answeredCount.value / totalCount.value) * 100))

  // --- Actions ---
  function initExam() {
    // Try to restore from localStorage
    const savedState = localStorage.getItem('currentExamState')
    if (savedState) {
      const parsed = JSON.parse(savedState)
      userAnswers.value = parsed.userAnswers || {}
      markedQuestions.value = parsed.markedQuestions || {}
      examInfo.value.remainingTime = parsed.remainingTime || examInfo.value.totalTime
      switchCount.value = parsed.switchCount || 0
      // If exam was submitted, we might want to handle that, but for now let's assume resume
    } else {
      userAnswers.value = {}
      markedQuestions.value = {}
      examInfo.value.remainingTime = examInfo.value.totalTime
      switchCount.value = 0
    }
    examStatus.value = 'in-progress'
  }

  function setAnswer(questionId, value) {
    userAnswers.value[questionId] = value
    saveState()
  }

  function toggleMark(questionId) {
    if (markedQuestions.value[questionId]) {
      delete markedQuestions.value[questionId]
    } else {
      markedQuestions.value[questionId] = true
    }
    saveState()
  }

  function tickTimer() {
    if (examStatus.value === 'in-progress' && examInfo.value.remainingTime > 0) {
      examInfo.value.remainingTime--
      if (examInfo.value.remainingTime % 5 === 0) {
        saveState() // Save every 5 seconds
      }
    } else if (examInfo.value.remainingTime <= 0 && examStatus.value === 'in-progress') {
      submitExam()
    }
  }

  function incrementSwitchCount() {
    switchCount.value++
    saveState()
  }

  function saveState() {
    localStorage.setItem('currentExamState', JSON.stringify({
      userAnswers: userAnswers.value,
      markedQuestions: markedQuestions.value,
      remainingTime: examInfo.value.remainingTime,
      switchCount: switchCount.value
    }))
  }

  function submitExam() {
    examStatus.value = 'grading'
    saveState()
    // Simulate grading delay
    setTimeout(() => {
      examStatus.value = 'finished'
      localStorage.removeItem('currentExamState') // Clear temp state after finish
    }, 2000)
  }

  return {
    examInfo,
    questions,
    userAnswers,
    markedQuestions,
    examStatus,
    switchCount,
    answeredCount,
    totalCount,
    progressPercentage,
    initExam,
    setAnswer,
    toggleMark,
    tickTimer,
    incrementSwitchCount,
    submitExam
  }
})
