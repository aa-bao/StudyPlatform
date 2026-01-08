<template>
  <div class="score-report">
    <div class="report-header">
      <h2>考试结果分析</h2>
      <div class="score-display">
        <span class="score-label">总分</span>
        <span class="score-value">{{ totalScore }}</span>
        <span class="score-total">/ {{ totalPossibleScore }}</span>
      </div>
    </div>

    <div class="report-body">
      <div class="chart-section">
        <div ref="pieChartRef" class="chart-container"></div>
      </div>
      
      <div class="ai-comment-section">
        <h3><el-icon><Cpu /></el-icon> AI 智能点评</h3>
        <div class="ai-content">
          <p v-if="totalScore > 80">根据作答情况分析，您的基础非常扎实！特别是在微积分部分表现优异。建议继续保持，并适当增加难题训练。</p>
          <p v-else-if="totalScore > 60">基础尚可，但在综合应用题上失分较多。建议重点复习线性代数部分的概念。</p>
          <p v-else>本次测试暴露出基础知识点掌握不牢固。建议回归课本，系统梳理知识体系。</p>
        </div>
      </div>
    </div>

    <div class="report-footer">
      <el-button type="primary" @click="$router.push('/user/dashboard')">返回面板</el-button>
      <el-button @click="$router.push('/user/correction-notebook')">查看错题</el-button>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useExamStore } from '@/stores/exam'
import * as echarts from 'echarts'
import { Cpu } from '@element-plus/icons-vue'

const examStore = useExamStore()
const pieChartRef = ref(null)

// Calculate score (simple mock logic)
const totalPossibleScore = computed(() => {
  return examStore.questions.reduce((sum, q) => sum + q.score, 0)
})

const totalScore = computed(() => {
  // Mock scoring: 
  // For choice: if answer == correct (need correct answers in data, assuming mock)
  // Let's just generate a random score for demo or count answered
  return Math.floor(Math.random() * (totalPossibleScore.value - 60) + 60) 
})

onMounted(() => {
  if (pieChartRef.value) {
    const chart = echarts.init(pieChartRef.value)
    chart.setOption({
      title: {
        text: '得分分布',
        left: 'center'
      },
      tooltip: {
        trigger: 'item'
      },
      series: [
        {
          name: '得分分析',
          type: 'pie',
          radius: '50%',
          data: [
            { value: totalScore.value, name: '得分' },
            { value: totalPossibleScore.value - totalScore.value, name: '失分' }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    })
  }
})
</script>

<style scoped>
.score-report {
  max-width: 800px;
  margin: 40px auto;
  background: #fff;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.report-header {
  text-align: center;
  margin-bottom: 40px;
}

.score-display {
  font-size: 24px;
  color: #333;
  margin-top: 10px;
}

.score-value {
  font-size: 48px;
  color: #f56c6c;
  font-weight: bold;
  margin: 0 10px;
}

.report-body {
  display: flex;
  gap: 40px;
  margin-bottom: 40px;
}

.chart-section {
  flex: 1;
  height: 300px;
}

.chart-container {
  width: 100%;
  height: 100%;
}

.ai-comment-section {
  flex: 1;
  background: #f0f9eb;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #e1f3d8;
}

.ai-comment-section h3 {
  color: #67c23a;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 0;
}

.ai-content {
  line-height: 1.6;
  color: #606266;
}

.report-footer {
  text-align: center;
  display: flex;
  justify-content: center;
  gap: 20px;
}
</style>
