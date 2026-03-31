<template>
    <div class="mistake-monitor-container">
        <div class="page-header">
            <div class="header-left">
                <span class="title-text">错题监控中心</span>
                <div class="header-desc">查看用户错题的统计信息，包括错题数、错题题目数、涉及用户数、涉及科目数等</div>
            </div>
        </div>

        <!-- 统计卡片 -->
        <el-row :gutter="16" class="stats-row">
            <el-col :span="6">
                <div class="stat-card stat-error">
                    <div class="stat-icon">
                        <svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">{{ totalErrorCount }}</div>
                        <div class="stat-label">总错误次数</div>
                    </div>
                </div>
            </el-col>
            <el-col :span="6">
                <div class="stat-card stat-question">
                    <div class="stat-icon">
                        <svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2"/><rect x="9" y="3" width="6" height="4" rx="1"/><line x1="9" y1="12" x2="15" y2="12"/><line x1="9" y1="16" x2="13" y2="16"/></svg>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">{{ totalMistakeQuestions }}</div>
                        <div class="stat-label">错题题目数</div>
                    </div>
                </div>
            </el-col>
            <el-col :span="6">
                <div class="stat-card stat-user">
                    <div class="stat-icon">
                        <svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87"/><path d="M16 3.13a4 4 0 010 7.75"/></svg>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">{{ totalAffectedUsers }}</div>
                        <div class="stat-label">涉及用户数</div>
                    </div>
                </div>
            </el-col>
            <el-col :span="6">
                <div class="stat-card stat-subject">
                    <div class="stat-icon">
                        <svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="2"><path d="M2 3h6a4 4 0 014 4v14a3 3 0 00-3-3H2z"/><path d="M22 3h-6a4 4 0 00-4 4v14a3 3 0 013-3h7z"/></svg>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">{{ subjectCount }}</div>
                        <div class="stat-label">涉及科目数</div>
                    </div>
                </div>
            </el-col>
        </el-row>

        <!-- 科目错题分布图 -->
        <el-card shadow="never" class="section-card">
            <template #header>
                <div class="section-header">
                    <span class="section-title">科目错题分布</span>
                    <span class="section-subtitle">柱高代表错误次数，点击柱体查看详情</span>
                </div>
            </template>
            <div v-loading="loading" ref="barChartRef" class="bar-chart-container"></div>
        </el-card>

        <!-- 高频错题 TOP 20 -->
        <el-card shadow="never" class="section-card">
            <template #header>
                <div class="section-header">
                    <span class="section-title">高频错题 TOP 20</span>
                </div>
            </template>
            <el-table :data="hotMistakes" stripe v-loading="loading" max-height="500" class="modern-table">
                <el-table-column prop="questionId" label="题目ID" width="80" align="center">
                    <template #default="scope">
                        <span class="id-badge">#{{ scope.row.questionId }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="questionContent" label="题目内容" min-width="300">
                    <template #default="scope">
                        <div class="question-content" v-html="renderLatex(scope.row.questionContent)"></div>
                    </template>
                </el-table-column>
                <el-table-column prop="totalErrorCount" label="错误次数" width="110" align="center">
                    <template #default="scope">
                        <span class="error-badge">{{ scope.row.totalErrorCount }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="errorUserCount" label="错误人数" width="100" align="center" />
                <el-table-column label="操作" width="120" align="center">
                    <template #default="scope">
                        <el-button
                            size="small"
                            type="primary"
                            link
                            @click="openQuestionDetail(scope.row)"
                        >
                            查看详情
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <!-- 题目详情对话框 -->
        <el-dialog v-model="detailDialogVisible" title="题目详情" width="700px" append-to-body>
            <div v-loading="detailLoading">
                <el-descriptions :column="1" border v-if="detailQuestion">
                    <el-descriptions-item label="题干内容">
                        <div class="detail-content" v-html="renderLatex(detailQuestion.content)"></div>
                    </el-descriptions-item>
                    <el-descriptions-item v-if="detailQuestion.options && detailQuestion.options.length" label="选项">
                        <div v-for="(opt, index) in detailQuestion.options" :key="index" class="option-item">
                            <template v-if="typeof opt === 'string'">
                                {{ String.fromCharCode(65 + index) }}. <span v-html="renderLatex(opt)"></span>
                            </template>
                            <template v-else>
                                {{ opt.label }}. <span v-html="renderLatex(opt.text)"></span>
                            </template>
                        </div>
                    </el-descriptions-item>
                    <el-descriptions-item label="正确答案">
                        <span class="answer-text" v-html="renderLatex(detailQuestion.answer)"></span>
                    </el-descriptions-item>
                    <el-descriptions-item label="解析">
                        <div v-html="renderLatex(detailQuestion.analysis || '暂无解析')"></div>
                    </el-descriptions-item>
                    <el-descriptions-item label="难度" v-if="detailQuestion.difficulty">L{{ detailQuestion.difficulty }}</el-descriptions-item>
                    <el-descriptions-item label="错误次数">
                        <span class="error-badge">{{ detailQuestion.totalErrorCount }}</span>
                    </el-descriptions-item>
                    <el-descriptions-item label="错误人数">{{ detailQuestion.errorUserCount }}</el-descriptions-item>
                </el-descriptions>
            </div>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import katex from 'katex'
import 'katex/dist/katex.min.css'

const router = useRouter()
const loading = ref(false)
const heatmapData = ref([])
const hotMistakes = ref([])
const barChartRef = ref(null)
let barChart = null
let resizeObserver = null
const detailDialogVisible = ref(false)
const detailQuestion = ref(null)
const detailLoading = ref(false)

// LaTeX 渲染
const renderLatex = (content) => {
    if (!content || typeof content !== 'string') return ''
    let processed = content.replace(/\\\\/g, '\\')
    // 块级公式
    processed = processed.replace(/\$\$([^\$]+)\$\$/g, (match, tex) => {
        try {
            return katex.renderToString(tex, { throwOnError: false, displayMode: true })
        } catch { return match }
    })
    // 行内公式
    processed = processed.replace(/\$([^\$]+)\$/g, (match, tex) => {
        try {
            return katex.renderToString(tex, { throwOnError: false, displayMode: false })
        } catch { return match }
    })
    return processed
}

// 计算统计数据
const totalErrorCount = computed(() => {
    return heatmapData.value.reduce((sum, item) => sum + item.totalErrorCount, 0)
})

const totalMistakeQuestions = computed(() => {
    return heatmapData.value.reduce((sum, item) => sum + item.mistakeQuestionCount, 0)
})

const totalAffectedUsers = computed(() => {
    return heatmapData.value.reduce((sum, item) => sum + item.affectedUserCount, 0)
})

const subjectCount = computed(() => heatmapData.value.length)

// 初始化柱状图
const initBarChart = () => {
    if (!barChartRef.value || !heatmapData.value.length) return

    if (barChart) barChart.dispose()
    barChart = echarts.init(barChartRef.value)

    const sorted = [...heatmapData.value].sort((a, b) => a.totalErrorCount - b.totalErrorCount)

    const option = {
        tooltip: {
            trigger: 'axis',
            axisPointer: { type: 'shadow' },
            formatter: (params) => {
                const d = params[0]
                const item = sorted[d.dataIndex]
                return `<b>${d.name}</b><br/>错误次数：<b>${d.value}</b><br/>错题数：${item.mistakeQuestionCount}<br/>涉及用户：${item.affectedUserCount}`
            }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            top: '8%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            data: sorted.map(i => i.subjectName),
            axisLabel: {
                color: '#5c6770',
                fontSize: 12
            },
            axisLine: { lineStyle: { color: '#e9ecef' } },
            axisTick: { show: false }
        },
        yAxis: {
            type: 'value',
            axisLabel: { color: '#868e96' },
            splitLine: { lineStyle: { color: '#f1f3f5', type: 'dashed' } },
            axisLine: { show: false },
            axisTick: { show: false }
        },
        series: [{
            type: 'bar',
            data: sorted.map(i => i.totalErrorCount),
            barWidth: '50%',
            itemStyle: {
                borderRadius: [6, 6, 0, 0],
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: '#ff922b' },
                    { offset: 1, color: '#ffd8a8' }
                ])
            },
            emphasis: {
                itemStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: '#e8590c' },
                        { offset: 1, color: '#ffc078' }
                    ])
                }
            }
        }]
    }

    barChart.setOption(option)

    barChart.on('click', (params) => {
        const item = sorted[params.dataIndex]
        showSubjectDetail(item)
    })

    resizeObserver = new ResizeObserver(() => { barChart?.resize() })
    resizeObserver.observe(barChartRef.value)
}

// 加载数据
const loadData = async () => {
    loading.value = true
    try {
        const heatmapRes = await request.get('/admin/mistake-heatmap')
        heatmapData.value = heatmapRes.data || heatmapRes || []

        const hotRes = await request.get('/admin/hot-mistakes?limit=20')
        hotMistakes.value = hotRes.data || hotRes || []

        await nextTick()
        initBarChart()
    } catch (e) {
        ElMessage.error('获取数据失败')
        console.error(e)
    } finally {
        loading.value = false
    }
}

// 显示科目详情
const showSubjectDetail = (item) => {
    ElMessage.info(`${item.subjectName}: ${item.totalErrorCount}次错误，${item.mistakeQuestionCount}道错题`)
}

// 查看题目详情
const openQuestionDetail = async (row) => {
    detailDialogVisible.value = true
    detailLoading.value = true
    try {
        const res = await request.get(`/question/${row.questionId}`)
        const q = res.data || res
        // 从 contentJson 提取字段，保留统计信息
        const contentJson = q.contentJson || {}
        detailQuestion.value = {
            content: contentJson.content || row.questionContent,
            options: contentJson.options || [],
            answer: contentJson.answer || '',
            analysis: contentJson.analysis || '',
            difficulty: q.difficulty,
            totalErrorCount: row.totalErrorCount,
            errorUserCount: row.errorUserCount
        }
    } catch (e) {
        ElMessage.error('获取题目详情失败')
    } finally {
        detailLoading.value = false
    }
}

// 跳转到题目编辑页面
const goToQuestionEdit = (questionId) => {
    router.push({
        path: '/admin/questions-manage',
        query: { editId: questionId }
    })
}

onMounted(() => { loadData() })
onUnmounted(() => { resizeObserver?.disconnect(); barChart?.dispose() })
</script>

<style scoped>
/* ===== 页面头部 ===== */
.page-header {
    margin-bottom: 20px;
}

.header-left {
    display: flex;
    flex-direction: column;
    gap: 6px;
}

.title-text {
    font-size: 20px;
    font-weight: 700;
    color: #1a1a2e;
    padding-left: 14px;
    position: relative;
}

.title-text::before {
    content: "";
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 4px;
    height: 22px;
    background: #e8590c;
    border-radius: 2px;
}

.header-desc {
    font-size: 13px;
    color: #868e96;
    padding-left: 14px;
}

/* ===== 统计卡片 ===== */
.stats-row {
    margin-bottom: 20px;
}

.stat-card {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;
    border-radius: 10px;
    border: 1px solid #f1f3f5;
    transition: all 0.25s ease;
    background: #fff;
}

.stat-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.06);
}

.stat-icon {
    width: 52px;
    height: 52px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
}

.stat-error .stat-icon {
    background: #fff5f5;
    color: #e03131;
}

.stat-question .stat-icon {
    background: #fff4e6;
    color: #e8590c;
}

.stat-user .stat-icon {
    background: #e6fcf5;
    color: #0ca678;
}

.stat-subject .stat-icon {
    background: #fff9db;
    color: #e67700;
}

.stat-info {
    display: flex;
    flex-direction: column;
}

.stat-value {
    font-size: 26px;
    font-weight: 700;
    color: #212529;
    line-height: 1.2;
}

.stat-label {
    font-size: 13px;
    color: #868e96;
    margin-top: 2px;
}

/* ===== 区域卡片 ===== */
.section-card {
    border-radius: 10px;
    border: 1px solid #f1f3f5;
    margin-bottom: 20px;
}

.section-header {
    display: flex;
    align-items: center;
    gap: 12px;
}

.section-title {
    font-size: 16px;
    font-weight: 600;
    color: #343a40;
    position: relative;
    padding-left: 12px;
}

.section-title::before {
    content: "";
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 3px;
    height: 16px;
    background: #e8590c;
    border-radius: 2px;
}

.section-subtitle {
    font-size: 12px;
    color: #adb5bd;
}

/* ===== 柱状图 ===== */
.bar-chart-container {
    width: 100%;
    height: 320px;
}

/* ===== 表格 ===== */
.modern-table {
    font-size: 14px;
}

:deep(.modern-table .el-table__header th) {
    background-color: #fafafa !important;
    color: #495057;
    font-weight: 600;
}

.question-content {
    max-height: 80px;
    overflow: hidden;
    line-height: 1.6;
}

.id-badge {
    display: inline-block;
    background: #f1f3f5;
    color: #495057;
    padding: 2px 8px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 500;
    font-family: 'Monaco', 'Courier New', monospace;
}

.error-badge {
    display: inline-block;
    background: #fff5f5;
    color: #e03131;
    padding: 2px 10px;
    border-radius: 4px;
    font-size: 13px;
    font-weight: 600;
}

/* ===== 详情对话框 ===== */
.detail-content {
    line-height: 1.8;
}

.option-item {
    padding: 6px 0;
    line-height: 1.6;
}

.answer-text {
    font-weight: 600;
    color: #0ca678;
}
</style>
