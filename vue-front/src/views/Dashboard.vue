<template>
    <div class="dashboard-container">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
            <el-icon class="is-loading" :size="48">
                <Loading />
            </el-icon>
            <p>数据加载中...</p>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="error" class="error-state">
            <el-alert
                title="加载失败"
                type="error"
                :description="error"
                show-icon
                :closable="false"
            />
            <el-button @click="loadDashboardData" type="primary" style="margin-top: 20px;">重新加载</el-button>
        </div>

        <!-- 正常内容 -->
        <div v-else>
            <!-- 顶部筛选区域 -->
            <div class="dashboard-header">
                <div class="welcome-section">
                    <h1>数据看板</h1>
                    <span class="subtitle">学习数据全景分析</span>
                    <p class="last-update">最后更新: {{ lastUpdateTime }}</p>
                </div>

                <div class="header-actions">
                    <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
                        end-placeholder="结束日期" :shortcuts="shortcuts" @change="handleDateChange" />
                    <el-select v-model="subjectFilter" placeholder="选择科目" @change="handleSubjectChange">
                        <el-option label="全部科目" value="all" />
                        <el-option v-for="subject in allSubjects" :key="subject.id" :label="subject.name"
                            :value="subject.id" />
                    </el-select>
                </div>
            </div>

            <!-- 第一行：核心指标卡片 -->
            <div class="stats-grid">
                <div class="stat-card" @click="goToPractice()">
                    <div class="stat-header">
                        <h3>累计刷题</h3>
                        <el-icon name="Collection" class="stat-icon" />
                    </div>
                    <div class="stat-value">{{ coreStats.totalQuestions || 0 }}</div>
                    <div class="stat-trend positive">
                        <el-icon name="Top" /> +{{ coreStats.todayQuestions || 0 }} 今日
                    </div>
                </div>

                <div class="stat-card" @click="viewMistakeAnalysis()">
                    <div class="stat-header">
                        <h3>总体正确率</h3>
                        <el-icon name="Promotion" class="stat-icon" />
                    </div>
                    <div class="stat-value">{{ coreStats.accuracy || 0 }}%</div>
                    <div class="stat-trend" :class="coreStats.accuracyTrend > 0 ? 'positive' : 'negative'">
                        <el-icon :name="coreStats.accuracyTrend > 0 ? 'Top' : 'Bottom'" />
                        {{ Math.abs(coreStats.accuracyTrend) }}% {{ coreStats.accuracyTrend > 0 ? '上升' : '下降' }}
                    </div>
                </div>

                <div class="stat-card" @click="viewStudyTime()">
                    <div class="stat-header">
                        <h3>累计学习时长</h3>
                        <el-icon name="Timer" class="stat-icon" />
                    </div>
                    <div class="stat-value">{{ coreStats.totalStudyHours || 0 }}h</div>
                    <div class="stat-trend positive">
                        <el-icon name="Top" /> +{{ coreStats.todayStudyHours || 0 }}h 今日
                    </div>
                </div>

                <div class="stat-card" @click="viewMistakes()">
                    <div class="stat-header">
                        <h3>错题数量</h3>
                        <el-icon name="Failed" class="stat-icon" />
                    </div>
                    <div class="stat-value">{{ coreStats.mistakeCount || 0 }}</div>
                    <div class="stat-trend negative">
                        <el-icon name="Bottom" /> +{{ coreStats.newMistakes || 0 }} 今日新增
                    </div>
                </div>
            </div>

            <!-- 第二行：主要图表 -->
            <div class="charts-row">
                <div class="chart-container">
                    <div class="chart-header">
                        <h2>学科能力雷达图</h2>
                        <el-dropdown @command="handleRadarCommand">
                            <el-button type="primary" text>
                                操作 <el-icon name="ArrowDownBold" />
                            </el-button>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item command="change-metrics">更改指标</el-dropdown-item>
                                    <el-dropdown-item command="compare-with-avg">与平均值对比</el-dropdown-item>
                                    <el-dropdown-item command="export-data">导出数据</el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </div>
                    <div ref="radarChart" class="chart-area" style="width: 100%; height: 400px;"></div>
                </div>

                <div class="chart-container">
                    <div class="chart-header">
                        <h2>错题热力分布</h2>
                        <el-dropdown @command="handleHeatmapCommand">
                            <el-button type="primary" text>
                                操作 <el-icon name="ArrowDownBold" />
                            </el-button>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item command="filter-subject">按科目筛选</el-dropdown-item>
                                    <el-dropdown-item command="sort-by-errors">按错误率排序</el-dropdown-item>
                                    <el-dropdown-item command="export-data">导出数据</el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </div>
                    <div ref="heatMap" class="chart-area" style="width: 100%; height: 400px;"></div>
                </div>
            </div>

            <!-- 第三行：学习趋势和错题分析 -->
            <div class="analysis-row">
                <div class="chart-container">
                    <div class="chart-header">
                        <h2>学习趋势</h2>
                        <el-radio-group v-model="trendPeriod" @change="handleTrendPeriodChange">
                            <el-radio-button value="daily">日</el-radio-button>
                            <el-radio-button value="weekly">周</el-radio-button>
                            <el-radio-button value="monthly">月</el-radio-button>
                        </el-radio-group>
                    </div>
                    <div ref="trendChart" class="chart-area" style="width: 100%; height: 350px;"></div>
                </div>

                <div class="chart-container">
                    <div class="chart-header">
                        <h2>高频错题分析</h2>
                    </div>
                    <div class="hot-mistakes-list">
                        <div v-for="(mistake, index) in hotMistakes" :key="index" class="hot-mistake-item"
                             @click="viewQuestionDetail(mistake)">
                            <div class="mistake-index">{{ index + 1 }}</div>
                            <div class="mistake-content">
                                <div class="mistake-subject">
                                    <span class="subject-badge" :class="'subject-' + mistake.subjectType">
                                        {{ mistake.subjectName }}
                                    </span>
                                    <span class="knowledge-point">{{ mistake.knowledgePoint }}</span>
                                    <span class="error-count">错误 {{ mistake.errorCount }} 次</span>
                                </div>
                                <div class="mistake-question" v-html="renderLatex(mistake.question)"></div>
                            </div>
                        </div>
                        <div v-if="hotMistakes.length === 0" class="empty-state">
                            <p>🎉 暂无错题数据</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 考研目标进度 -->
            <div class="progress-section">
                <div class="chart-container">
                    <div class="chart-header">
                        <h2>考研目标进度</h2>
                        <el-button type="primary" text @click="editTarget">
                            <el-icon name="Edit" /> 编辑目标
                        </el-button>
                    </div>
                    <div class="target-progress">
                        <div class="target-info">
                            <p class="target-school">目标院校: <span>{{ userInfo.target_school || '未设置' }}</span></p>
                            <p class="target-exam">考试类型: <span>{{ examType }}</span></p>
                            <p class="target-date">考试日期: <span>{{ formattedExamDate }}</span></p>
                            <p class="days-left">距离考试还有 <span class="days-count">{{ daysToExam }}</span> 天</p>
                        </div>

                        <el-progress :text-inside="true" :stroke-width="25" :percentage="targetProgress.percentage"
                            :color="getProgressColor(targetProgress.percentage)" status="success">
                            <span class="progress-text">{{ targetProgress.percentage }}%</span>
                        </el-progress>

                        <div class="progress-details">
                            <p>已完成 <span class="completed">{{ targetProgress.completed }}</span> /
                                计划 <span class="total">{{ targetProgress.total }}</span> 题目</p>
                            <p>预计达成目标日期: <span class="estimate-date">{{ targetProgress.estimateDate }}</span></p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 错题详情对话框 -->
        <el-dialog
            v-model="questionDetailDialogVisible"
            title="错题详情"
            width="800px"
            :close-on-click-modal="false"
            class="question-detail-dialog"
        >
            <div v-if="selectedQuestion" class="question-detail-content">
                <!-- 题目基本信息 -->
                <div class="detail-section">
                    <div class="detail-header">
                        <h4>题目信息</h4>
                        <div class="question-meta">
                            <el-tag v-if="selectedQuestion.subjectNames && selectedQuestion.subjectNames.length > 0"
                                    type="primary" size="small">
                                {{ selectedQuestion.subjectNames.join('、') }}
                            </el-tag>
                            <el-tag v-if="selectedQuestion.bookNames && selectedQuestion.bookNames.length > 0"
                                    type="info" size="small" style="margin-left: 8px;">
                                {{ selectedQuestion.bookNames.join('、') }}
                            </el-tag>
                        </div>
                    </div>
                    <div class="question-body">
                        <div class="question-text" v-html="renderLatex(selectedQuestion.content)"></div>

                        <!-- 选项（如果有） -->
                        <div v-if="selectedQuestion.options && selectedQuestion.options.length > 0" class="options-list">
                            <div v-for="(option, index) in selectedQuestion.options" :key="index" class="option-item">
                                <span class="option-label">{{ option.label || ['A', 'B', 'C', 'D', 'E', 'F'][index] }}.</span>
                                <span class="option-text" v-html="renderLatex(option.text)"></span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 答案与解析 -->
                <div class="detail-section">
                    <div class="detail-header">
                        <h4>答案与解析</h4>
                    </div>
                    <div class="answer-section">
                        <div class="answer-row">
                            <span class="answer-label">正确答案：</span>
                            <span class="answer-value primary latex-answer" v-html="renderLatex(selectedQuestion.answer)"></span>
                        </div>
                        <div v-if="selectedQuestion.analysis" class="analysis-row">
                            <span class="analysis-label">题目解析：</span>
                            <div class="analysis-content latex-content" v-html="renderLatex(selectedQuestion.analysis)"></div>
                        </div>
                    </div>
                </div>

                <!-- 标签（如果有） -->
                <div v-if="selectedQuestion.tags && selectedQuestion.tags.length > 0" class="detail-section">
                    <div class="detail-header">
                        <h4>知识点标签</h4>
                    </div>
                    <div class="tags-list">
                        <el-tag v-for="(tag, index) in selectedQuestion.tags" :key="index" size="small" style="margin-right: 8px; margin-bottom: 8px;">
                            {{ tag }}
                        </el-tag>
                    </div>
                </div>
            </div>

            <template #footer>
                <el-button @click="questionDetailDialogVisible = false">关闭</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import * as echarts from 'echarts'
import { useRouter } from 'vue-router'
import { ElMessage, ElDialog } from 'element-plus'
import {
    getHomePageDataApi,
    getUserStudyStatsApi,
    getHotMistakesApi,
    getTodayStatsApi
} from '@/api/user'
import { getStudyHeatmap } from '@/api/userProgress'
import { renderLatex } from '@/utils/latex'

const router = useRouter()

// 用户信息
const userInfo = ref({
    nickname: '',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    role: 'student',
    target_school: '',
    exam_year: '',
    exam_date: '',
    target_total_score: 0,
    exam_subjects: ''
})

// 考试类型计算
const examType = computed(() => {
    return userInfo.value.exam_subjects?.replace(/、/g, ' + ') || ''
})

// 格式化考试日期
const formattedExamDate = computed(() => {
    if (userInfo.value.exam_date) {
        const date = new Date(userInfo.value.exam_date)
        return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
    }
    return '未设置'
})

// 计算距离考试天数
const daysToExam = computed(() => {
    if (userInfo.value.exam_date) {
        const examDate = new Date(userInfo.value.exam_date)
        const today = new Date()
        const diffTime = examDate - today
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
        return diffDays > 0 ? diffDays : 0
    }
    return 0
})

// 最后更新时间
const lastUpdateTime = ref('')

// 日期范围筛选
const dateRange = ref([
    new Date(new Date().setDate(new Date().getDate() - 6)),
    new Date()
])

// 快捷日期选项
const shortcuts = ref([
    {
        text: '最近7天',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setDate(start.getDate() - 6)
            return [start, end]
        }
    },
    {
        text: '最近30天',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setDate(start.getDate() - 29)
            return [start, end]
        }
    },
    {
        text: '本月',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setDate(1)
            return [start, end]
        }
    }
])

// 科目筛选
const subjectFilter = ref('all')
const allSubjects = ref([])

// 核心统计数据
const coreStats = ref({
    totalQuestions: 0,
    todayQuestions: 0,
    accuracy: 0,
    accuracyTrend: 0,
    totalStudyHours: 0,
    todayStudyHours: 0,
    mistakeCount: 0,
    newMistakes: 0
})

// 目标进度
const targetProgress = ref({
    percentage: 0,
    completed: 0,
    total: 2000,
    estimateDate: ''
})

// 高频错题
const hotMistakes = ref([])

// 错题详情对话框
const questionDetailDialogVisible = ref(false)
const selectedQuestion = ref(null)

// 学习趋势周期
const trendPeriod = ref('daily')

// 加载状态
const loading = ref(true)
const error = ref(null)

// 图表引用
const radarChart = ref(null)
const heatMap = ref(null)
const trendChart = ref(null)

// 学习统计数据(用于图表)
const studyStats = ref(null)
const studyHeatmap = ref([])

// 主数据获取函数
const loadDashboardData = async () => {
    loading.value = true
    error.value = null

    try {
        const userStr = localStorage.getItem('user')
        if (!userStr) {
            throw new Error('未获取到用户信息,请先登录')
        }

        const user = JSON.parse(userStr)
        const userId = user.id

        if (!userId) {
            throw new Error('用户ID不存在,请先登录')
        }

        // 并行请求所有API
        const [
            homeDataRes,
            studyStatsRes,
            heatmapRes,
            todayStatsRes,
            hotMistakesRes
        ] = await Promise.all([
            getHomePageDataApi(userId),
            getUserStudyStatsApi(userId),
            getStudyHeatmap(userId, 30),
            getTodayStatsApi(userId),
            getHotMistakesApi(userId, 5)
        ])

        // 数据转换和赋值
        transformAndAssignData({
            homeData: homeDataRes.data,
            studyStats: studyStatsRes.data,
            heatmap: heatmapRes.data,
            todayStats: todayStatsRes.data,
            hotMistakes: hotMistakesRes.data
        })

        // 更新最后更新时间
        lastUpdateTime.value = new Date().toLocaleString('zh-CN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        })

    } catch (err) {
        error.value = err.message || '数据加载失败'
        console.error('加载Dashboard数据失败:', err)
        ElMessage.error(error.value)
    } finally {
        loading.value = false
    }
}

// 数据转换函数
const transformAndAssignData = (data) => {
    // 1. 用户信息
    userInfo.value = data.homeData?.userInfo || {}

    // 2. 核心统计
    const stats = data.homeData?.studyStats || {}
    const todayStats = data.todayStats || {}
    coreStats.value = {
        totalQuestions: Number(stats.questionsDone || 0),
        todayQuestions: Number(todayStats.todayQuestions || 0),
        accuracy: Math.round(Number(stats.accuracy || 0)),
        accuracyTrend: 0, // 暂时设为0
        totalStudyHours: Number(stats.totalStudyHours || 0).toFixed(1),
        todayStudyHours: Number(todayStats.todayStudyHours || 0).toFixed(1),
        mistakeCount: Number(stats.mistakesCount || 0),
        newMistakes: Number(todayStats.newMistakes || 0)
    }

    // 3. 科目列表
    allSubjects.value = data.homeData?.subjects || []

    // 4. 高频错题 - 按错误次数排序
    hotMistakes.value = (data.hotMistakes || [])
        .sort((a, b) => (b.errorCount || 0) - (a.errorCount || 0))
        .map((item, index) => {
            // 获取科目类型（用于样式）
            const subjectName = item.subjectName || '未分类'
            let subjectType = 'math'
            if (subjectName.includes('代数') || subjectName.includes('线代')) {
                subjectType = 'algebra'
            } else if (subjectName.includes('概率') || subjectName.includes('统计')) {
                subjectType = 'probability'
            }

            // 限制题目文本长度
            const questionText = item.content || '题目内容加载失败'
            const truncatedQuestion = questionText.length > 100
                ? questionText.substring(0, 100) + '...'
                : questionText

            return {
                id: item.questionId,
                rank: index + 1,
                question: truncatedQuestion,
                subjectName: subjectName,
                subjectType: subjectType,
                knowledgePoint: item.knowledgePoint || '未知知识点',
                errorCount: Number(item.errorCount || 0),
                fullData: item // 保存完整数据以便点击时查看详情
            }
        })

    // 5. 目标进度 - 从用户信息获取
    const totalQuestions = Number(coreStats.value.totalQuestions || 0)
    const targetTotal = 2000 // 可从用户目标获取，暂时硬编码
    const examDate = userInfo.value.exam_date || new Date().toISOString().split('T')[0]

    targetProgress.value = {
        percentage: Math.min(100, Math.round((totalQuestions / targetTotal) * 100)),
        completed: totalQuestions,
        total: targetTotal,
        estimateDate: examDate
    }

    // 6. 学习统计数据和热力图
    studyStats.value = data.studyStats
    studyHeatmap.value = data.heatmap || []

    // 9. 更新图表
    setTimeout(() => {
        updateChartsWithData()
    }, 100)
}

// 图表数据更新
const updateChartsWithData = () => {
    if (!radarChart.value || !heatMap.value || !trendChart.value) {
        return
    }

    // 雷达图:从studyStats获取各科目正确率
    const radarData = getRadarChartData()
    updateRadarChart(radarData)

    // 热力图:基于错题分布(暂时用模拟数据)
    const heatmapData = getHeatmapChartData()
    updateHeatmapChart(heatmapData)

    // 趋势图:从studyHeatmap生成
    const trendData = getTrendChartData()
    updateTrendChart(trendData)
}

const getRadarChartData = () => {
    // 从allSubjects获取科目,从studyStats获取正确率
    return allSubjects.value.map(subject => {
        const subjectStat = studyStats.value?.subjectStats?.find(
            s => s.subjectId === subject.id
        )
        return {
            name: subject.name,
            value: Math.round(subjectStat?.accuracy || 0)
        }
    })
}

const getHeatmapChartData = () => {
    // 暂时返回模拟数据,实际应该基于错题分布
    const xAxisData = ['极限', '导数', '积分', '级数', '微分方程', '矩阵', '特征值', '概率分布', '统计推断']
    const yAxisData = ['基础', '提高', '强化', '冲刺']
    const heatMapData = []

    for (let i = 0; i < yAxisData.length; i++) {
        for (let j = 0; j < xAxisData.length; j++) {
            let val = 20 + Math.floor(Math.random() * 50)
            heatMapData.push([j, i, val])
        }
    }

    return { xAxisData, yAxisData, heatMapData }
}

const getTrendChartData = () => {
    // 从studyHeatmap聚合数据
    const dates = studyHeatmap.value.map(item => {
        const date = new Date(item.recordDate)
        return `${date.getMonth() + 1}/${date.getDate()}`
    })
    const questions = studyHeatmap.value.map(item => item.questionCount)
    const time = studyHeatmap.value.map(item => (item.totalDuration / 3600).toFixed(1))

    return { dates, questions, time }
}

// 初始化和更新图表
let radarChartInstance = null
let heatMapInstance = null
let trendChartInstance = null

const updateRadarChart = (data) => {
    if (!radarChartInstance) {
        radarChartInstance = echarts.init(radarChart.value)
    }

    const indicator = data.map(item => ({ name: item.name, max: 100 }))
    const value = data.map(item => item.value)

    const option = {
        tooltip: {
            trigger: 'axis'
        },
        radar: {
            radius: '65%',
            indicator: indicator,
            axisName: {
                color: '#666',
                fontSize: 14
            },
            splitLine: {
                lineStyle: {
                    color: 'rgba(130, 179, 255, 0.3)'
                }
            },
            axisLine: {
                lineStyle: {
                    color: 'rgba(130, 179, 255, 0.8)'
                }
            },
            splitArea: {
                areaStyle: {
                    color: ['rgba(197, 225, 255, 0.2)', 'rgba(158, 195, 255, 0.1)'],
                    shadowColor: 'rgba(0, 0, 0, 0.1)',
                    shadowBlur: 5
                }
            }
        },
        series: [{
            name: '能力分布',
            type: 'radar',
            data: [{
                value: value,
                name: '你的能力',
                symbol: 'circle',
                symbolSize: 8,
                lineStyle: {
                    width: 3,
                    color: '#409EFF'
                },
                areaStyle: {
                    color: 'rgba(64, 158, 255, 0.2)'
                },
                itemStyle: {
                    color: '#409EFF',
                    borderColor: '#fff',
                    borderWidth: 2
                }
            }]
        }]
    }
    radarChartInstance.setOption(option)
}

const updateHeatmapChart = (data) => {
    if (!heatMapInstance) {
        heatMapInstance = echarts.init(heatMap.value)
    }

    const option = {
        tooltip: {
            position: 'top',
            formatter: function (params) {
                return `${data.xAxisData[params.value[0]]} - ${data.yAxisData[params.value[1]]}<br/>错误率: ${params.value[2]}%`
            }
        },
        grid: {
            height: '70%',
            y: '15%'
        },
        xAxis: {
            type: 'category',
            data: data.xAxisData,
            axisLabel: {
                rotate: 45,
                fontSize: 12
            },
            splitArea: {
                show: true
            }
        },
        yAxis: {
            type: 'category',
            data: data.yAxisData,
            axisLabel: {
                fontSize: 12
            },
            splitArea: {
                show: true
            }
        },
        visualMap: {
            min: 0,
            max: 100,
            calculable: true,
            orient: 'horizontal',
            left: 'center',
            bottom: '0%',
            text: ['错误率低', '错误率高'],
            inRange: {
                color: ['#e6f7ff', '#bae7ff', '#69c0ff', '#3385ff', '#0b3488']
            },
            textStyle: {
                color: '#666'
            }
        },
        series: [{
            name: '错题分布',
            type: 'heatmap',
            data: data.heatMapData,
            label: {
                show: true,
                formatter: function (params) {
                    return params.value[2] > 50 ? params.value[2] + '%' : ''
                },
                color: '#333',
                fontSize: 10
            },
            emphasis: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowColor: 'rgba(0, 0, 0, 0.2)'
                }
            }
        }]
    }
    heatMapInstance.setOption(option)
}

const updateTrendChart = (data) => {
    if (!trendChartInstance) {
        trendChartInstance = echarts.init(trendChart.value)
    }

    const option = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross'
            }
        },
        legend: {
            data: ['刷题数量', '学习时长(h)'],
            bottom: 0
        },
        grid: {
            top: 40,
            left: 50,
            right: 50,
            bottom: 60
        },
        xAxis: {
            type: 'category',
            data: data.dates,
            axisLabel: {
                fontSize: 12
            }
        },
        yAxis: [{
            type: 'value',
            name: '数量',
            min: 0,
            axisLabel: {
                fontSize: 12,
                formatter: '{value}'
            },
            splitLine: {
                lineStyle: {
                    type: 'dashed',
                    color: 'rgba(0, 0, 0, 0.05)'
                }
            }
        }, {
            type: 'value',
            name: '时长(h)',
            min: 0,
            axisLabel: {
                fontSize: 12,
                formatter: '{value}h'
            }
        }],
        series: [
            {
                name: '刷题数量',
                type: 'bar',
                data: data.questions,
                itemStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: '#66b1ff' },
                        { offset: 1, color: '#409eff' }
                    ])
                },
                emphasis: {
                    itemStyle: {
                        color: '#409eff'
                    }
                }
            },
            {
                name: '学习时长(h)',
                type: 'line',
                yAxisIndex: 1,
                data: data.time,
                symbol: 'rect',
                symbolSize: 6,
                lineStyle: {
                    width: 2,
                    type: 'dashed',
                    color: '#e6a23c'
                },
                itemStyle: {
                    color: '#e6a23c',
                    borderColor: '#fff',
                    borderWidth: 1
                }
            }
        ]
    }
    trendChartInstance.setOption(option)
}

// 进度条颜色
const getProgressColor = (percentage) => {
    if (percentage < 30) return '#ff4d4f'
    if (percentage < 70) return '#faad14'
    return '#52c41a'
}

// 导航操作
const goToPractice = () => {
    router.push('/practice')
}
const viewMistakes = () => {
    router.push('/correction-notebook')
}
const startMockExam = () => {
    router.push('/mock-exam')
}
const goToQuestion = (id) => {
    router.push(`/question/${id}`)
}

// 查看题目详情
const viewQuestionDetail = (mistake) => {
    if (mistake.fullData) {
        selectedQuestion.value = mistake.fullData
        questionDetailDialogVisible.value = true
    } else {
        ElMessage.warning('无法查看题目详情')
    }
}

const viewFullRanking = () => {
    router.push('/rankings')
}
const editTarget = () => {
    router.push('/user/profile')
}

// 图表操作
const handleRadarCommand = (command) => {
    switch (command) {
        case 'change-metrics':
            ElMessage.info('切换雷达图指标')
            break
        case 'compare-with-avg':
            ElMessage.info('与平均值对比')
            break
        case 'export-data':
            ElMessage.success('雷达图数据已导出')
            break
    }
}

const handleHeatmapCommand = (command) => {
    switch (command) {
        case 'filter-subject':
            ElMessage.info('按科目筛选错题分布')
            break
        case 'sort-by-errors':
            ElMessage.info('按错误次数排序')
            hotMistakes.value.sort((a, b) => b.errorCount - a.errorCount)
            break
        case 'export-data':
            ElMessage.success('热力图数据已导出')
            break
    }
}

const handleDateChange = async () => {
    if (dateRange.value && dateRange.value.length === 2) {
        await loadDashboardData()
        ElMessage.success('已更新数据范围')
    }
}

const handleSubjectChange = () => {
    updateChartsWithData()
    ElMessage.success('已切换科目筛选')
}

const handleTrendPeriodChange = () => {
    ElMessage.info(`已切换到${trendPeriod.value === 'daily' ? '日' : trendPeriod.value === 'weekly' ? '周' : '月'}视图`)
    // 这里应该更新趋势图
}

// 视图操作
const viewMistakeAnalysis = () => {
    router.push('/analysis/mistakes')
}
const viewStudyTime = () => {
    router.push('/analysis/study-time')
}

// 组件挂载
onMounted(() => {
    loadDashboardData()

    // 响应窗口大小变化
    const resizeCharts = () => {
        if (radarChartInstance) radarChartInstance.resize()
        if (heatMapInstance) heatMapInstance.resize()
        if (trendChartInstance) trendChartInstance.resize()
    }

    window.addEventListener('resize', resizeCharts)

    // 定时刷新(5分钟)
    const refreshInterval = setInterval(() => {
        loadDashboardData()
    }, 5 * 60 * 1000)

    // 清理函数
    onUnmounted(() => {
        window.removeEventListener('resize', resizeCharts)
        if (radarChartInstance) radarChartInstance.dispose()
        if (heatMapInstance) heatMapInstance.dispose()
        if (trendChartInstance) trendChartInstance.dispose()
        clearInterval(refreshInterval)
    })
})
</script>

<style scoped>
/* 整体布局 */
.dashboard-container {
    padding: 20px;
    background-color: #f8fafc;
    min-height: 100vh;
}

/* 加载状态 */
.loading-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: 50vh;
    color: #409eff;
}

.loading-state p {
    margin-top: 20px;
    font-size: 16px;
    color: #606266;
}

/* 错误状态 */
.error-state {
    padding: 40px;
    text-align: center;
}

/* 顶部筛选区域 */
.dashboard-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 32px;
    flex-wrap: wrap;
    gap: 20px;
    background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
    border: 1px solid #ebeef5;
    position: relative;
    overflow: hidden;
}

.dashboard-header::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(64, 158, 255, 0.1) 0%, transparent 70%);
    z-index: 0;
}

.welcome-section {
    display: flex;
    flex-direction: column;
    position: relative;
    z-index: 1;
    max-width: 650px;
    gap: 8px;
}

.welcome-section h1 {
    font-size: 45px;
    font-weight: 800;
    margin: 0 0 10px;
    background: linear-gradient(to right, #3a6197, #4a92da);
    background-clip: text;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    position: relative;
    display: inline-block;
}

.subtitle {
    font-size: 18px;
    color: #4a5568;
    font-weight: 500;
}

.last-update {
    color: #718096;
    font-size: 14px;
    margin: 8px 0 0;
    display: flex;
    align-items: center;
    gap: 6px;
}

.last-update::before {
    content: '';
    display: inline-block;
    width: 8px;
    height: 8px;
    background: #4ade80;
    border-radius: 50%;
    box-shadow: 0 0 6px #4ade80;
}

.header-actions {
    display: flex;
    flex-direction: column;
    gap: 16px;
    flex-wrap: wrap;
    align-items: center;
    position: relative;
    z-index: 1;
    min-width: 420px;
    justify-content: flex-end;
}

:deep(.el-date-editor) {
    width: 320px;
    height: 44px;
}

:deep(.el-input__wrapper) {
    border-radius: 12px !important;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05) !important;
    border: 1px solid #dcdfe6 !important;
    background-color: white !important;
    transition: all 0.3s ease !important;
}

:deep(.el-input__wrapper:hover) {
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15) !important;
    border-color: #409eff !important;
}

:deep(.el-select) {
    width: 160px;
    height: 44px;
}

:deep(.el-button) {
    height: 44px;
    border-radius: 12px;
    font-weight: 500;
    padding: 0 24px;
    box-shadow: 0 3px 10px rgba(64, 158, 255, 0.25);
    transition: all 0.3s ease;
}

:deep(.el-button:hover) {
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(64, 158, 255, 0.35);
}

:deep(.el-button:active) {
    transform: translateY(0);
}

:deep(.el-icon) {
    font-size: 18px;
    margin-right: 6px;
    vertical-align: middle;
}

/* 响应式调整 */
@media (max-width: 1100px) {
  .dashboard-header {
    flex-direction: column;
    align-items: stretch;
  }

  .header-actions {
    width: 100%;
    justify-content: space-between;
    min-width: auto;
  }

  :deep(.el-date-editor) {
    width: 200px;
  }

  :deep(.el-select) {
    width: 140px;
  }
}

@media (max-width: 768px) {
  .dashboard-header {
    padding: 20px;
  }

  .welcome-section h1 {
    font-size: 28px;
  }

  .subtitle {
    font-size: 16px;
  }

  :deep(.el-date-editor) {
    width: 180px;
  }

  :deep(.el-select) {
    width: 120px;
  }

  :deep(.el-button) {
    height: 40px;
    font-size: 14px;
    padding: 0 16px;
  }

  :deep(.el-icon) {
    font-size: 16px;
  }
}

@media (max-width: 480px) {
  :deep(.el-date-editor) {
    width: 100%;
  }

  .header-actions {
    flex-direction: column;
    gap: 12px;
  }

  :deep(.el-select) {
    width: 100%;
  }

  :deep(.el-button) {
    width: 100%;
  }
}

/* 统计卡片 */
.stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 20px;
    margin-bottom: 25px;
}

.stat-card {
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    border: 1px solid #ebeef5;
    cursor: pointer;
    transition: all 0.3s ease;
}

.stat-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
    border-color: #dcdfe6;
}

.stat-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
}

.stat-header h3 {
    font-size: 16px;
    color: #606266;
    margin: 0;
}

.stat-icon {
    color: #409eff;
    font-size: 20px;
}

.stat-value {
    font-size: 32px;
    font-weight: 700;
    color: #303133;
    margin-bottom: 8px;
}

.stat-trend {
    font-size: 14px;
    display: flex;
    align-items: center;
    gap: 4px;
}

.stat-trend.positive {
    color: #67c23a;
}

.stat-trend.negative {
    color: #f56c6c;
}

/* 图表区域 */
.charts-row,
.analysis-row,
.progress-row {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(550px, 1fr));
    gap: 25px;
    margin-bottom: 30px;
}

.chart-container {
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    border: 1px solid #ebeef5;
    transition: all 0.3s ease;
}

.chart-container:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.chart-header h2 {
    font-size: 18px;
    color: #303133;
    margin: 0;
    display: flex;
    align-items: center;
    gap: 8px;
}

.chart-header h2 .el-icon {
    color: #409eff;
    font-size: 20px;
}

.chart-area {
    width: 100%;
    height: 350px;
}

/* 错题表格 */
.hot-mistakes-container {
    height: 350px;
}

.question-content {
    color: #409eff;
    cursor: pointer;
    text-decoration: underline;
}

.question-content:hover {
    color: #66b1ff;
}

/* 目标进度 */
.target-progress {
    padding: 15px 0;
}

.target-info {
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 1px solid #ebeef5;
}

.target-school,
.target-exam,
.target-date {
    font-size: 15px;
    margin: 5px 0;
    color: #606266;
}

.target-school span,
.target-exam span,
.target-date span {
    font-weight: 600;
    color: #303133;
}

.el-progress {
    margin: 15px 0;
}

.progress-text {
    font-size: 18px;
    font-weight: 600;
    color: white;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.progress-details {
    margin-top: 15px;
    font-size: 14px;
    color: #606266;
}

.completed {
    color: #67c23a;
    font-weight: 600;
}

.total {
    color: #e6a23c;
    font-weight: 600;
}

.estimate-date {
    color: #409eff;
    font-weight: 600;
}

/* 排行榜 */
.ranking-container {
    padding: 10px 0;
}

.ranking-item {
    display: flex;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f2f6fc;
}

.ranking-item:last-child {
    border-bottom: none;
}

.ranking-item.highlight-item {
    background-color: rgba(64, 158, 255, 0.05);
    border-radius: 6px;
    padding: 12px 15px;
    margin: 5px 0;
}

.self-item {
    background-color: rgba(103, 194, 58, 0.1);
    border-radius: 8px;
    padding: 15px;
    margin-bottom: 20px;
    border: 1px solid rgba(103, 194, 58, 0.2);
}

.ranking-position {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 15px;
    font-weight: 600;
    font-size: 14px;
    flex-shrink: 0;
}

.user-position {
    background: linear-gradient(45deg, #409eff, #66b1ff);
    color: white;
}

.gold {
    background: linear-gradient(45deg, #ffd700, #ffcc00);
    color: #333;
    box-shadow: 0 0 6px rgba(255, 215, 0, 0.5);
}

.silver {
    background: linear-gradient(45deg, #c0c0c0, #a9a9a9);
    color: #333;
    box-shadow: 0 0 6px rgba(192, 192, 192, 0.5);
}

.bronze {
    background: linear-gradient(45deg, #cd7f32, #b87333);
    color: #fff;
    box-shadow: 0 0 6px rgba(205, 127, 50, 0.5);
}

.ranking-avatar {
    margin-right: 15px;
}

.ranking-info {
    flex: 1;
}

.ranking-nickname {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin: 0;
}

.ranking-stats {
    font-size: 13px;
    color: #909399;
    margin: 3px 0 0;
}

.ranking-footer {
    text-align: center;
    margin-top: 20px;
    padding-top: 15px;
    border-top: 1px solid #ebeef5;
}

/* 学习建议 */
.suggestions-section {
    margin-top: 20px;
}

.suggestions-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
    gap: 20px;
}

.suggestion-card {
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    border: 1px solid #ebeef5;
    transition: all 0.3s ease;
}

.suggestion-card:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
}

.suggestion-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
}

.suggestion-icon {
    font-size: 20px;
    color: #409eff;
}

.suggestion-title {
    font-size: 18px;
    color: #303133;
    margin: 0 0 10px;
}

.suggestion-content {
    color: #606266;
    line-height: 1.6;
    margin-bottom: 15px;
    font-size: 14px;
}

.suggestion-actions {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}

/* 响应式调整 */
@media (max-width: 1200px) {

    .charts-row,
    .analysis-row,
    .progress-row {
        grid-template-columns: 1fr;
    }
}

@media (max-width: 980px) {
    .dashboard-header {
        flex-direction: column;
        align-items: stretch;
    }

    .header-actions {
        width: 100%;
        justify-content: space-between;
    }
}

@media (max-width: 768px) {
    .stats-grid {
        grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
    }

    .suggestions-container {
        grid-template-columns: 1fr;
    }

    .chart-area {
        height: 300px;
    }
}

/* 高频错题卡片样式 */
.hot-mistakes-list {
    max-height: 400px;
    overflow-y: auto;
    padding-right: 8px;
}

.hot-mistakes-list::-webkit-scrollbar {
    width: 6px;
}

.hot-mistakes-list::-webkit-scrollbar-thumb {
    background: rgba(59, 130, 246, 0.3);
    border-radius: 3px;
}

.hot-mistakes-list::-webkit-scrollbar-track {
    background: rgba(209, 213, 219, 0.2);
}

.hot-mistake-item {
    display: flex;
    gap: 12px;
    padding: 15px;
    margin-bottom: 12px;
    background: rgba(255, 255, 255, 0.6);
    border-radius: 12px;
    border: 1px solid rgba(209, 213, 219, 0.3);
    transition: all 0.3s ease;
    cursor: pointer;
}

.hot-mistake-item:hover {
    background: rgba(255, 255, 255, 0.9);
    border-color: rgba(59, 130, 246, 0.4);
    transform: translateX(5px);
}

.mistake-index {
    width: 28px;
    height: 28px;
    background: linear-gradient(135deg, #3b82f6, #60a5fa);
    color: white;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 600;
    font-size: 14px;
    flex-shrink: 0;
}

.mistake-content {
    flex: 1;
}

.mistake-subject {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
    flex-wrap: wrap;
    gap: 8px;
}

.subject-badge {
    padding: 3px 10px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 500;
}

.subject-badge.subject-math {
    background: rgba(59, 130, 246, 0.15);
    color: #3b82f6;
}

.subject-badge.subject-algebra {
    background: rgba(16, 185, 129, 0.15);
    color: #10b981;
}

.subject-badge.subject-probability {
    background: rgba(245, 158, 11, 0.15);
    color: #f59e0b;
}

.knowledge-point {
    font-size: 12px;
    color: #6b7280;
    background: rgba(107, 114, 128, 0.1);
    padding: 2px 8px;
    border-radius: 8px;
}

.error-count {
    font-size: 12px;
    color: #ef4444;
    font-weight: 600;
}

.mistake-question {
    color: #374151;
    line-height: 1.6;
    margin: 8px 0;
    font-size: 14px;
}

.empty-state {
    text-align: center;
    padding: 40px 20px;
    color: #10b981;
    font-size: 16px;
}

/* 目标进度样式 */
.progress-section {
    margin-bottom: 30px;
}

.progress-section .chart-container {
    max-width: 800px;
    margin: 0 auto;
}

.target-info .days-left {
    font-size: 15px;
    margin: 5px 0;
    color: #f56c6c;
}

.days-count {
    font-weight: 700;
    font-size: 18px;
    color: #f56c6c;
}

/* 错题详情对话框样式 */
.question-detail-dialog :deep(.el-dialog__body) {
    padding: 20px;
    max-height: 600px;
    overflow-y: auto;
}

.question-detail-content {
    color: #1f2937;
}

.detail-section {
    margin-bottom: 24px;
    padding-bottom: 20px;
    border-bottom: 1px solid #e5e7eb;
}

.detail-section:last-child {
    border-bottom: none;
}

.detail-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
}

.detail-header h4 {
    font-size: 16px;
    font-weight: 600;
    color: #3b82f6;
    margin: 0;
}

.question-meta {
    display: flex;
    align-items: center;
}

.question-body {
    line-height: 1.8;
}

.question-text {
    font-size: 15px;
    color: #374151;
    margin-bottom: 16px;
    padding: 12px;
    background: #f9fafb;
    border-radius: 8px;
}

.options-list {
    margin-top: 12px;
}

.option-item {
    display: flex;
    padding: 8px 12px;
    margin-bottom: 8px;
    background: white;
    border: 1px solid #e5e7eb;
    border-radius: 6px;
    transition: all 0.2s;
}

.option-item:hover {
    border-color: #3b82f6;
    background: #f0f9ff;
}

.option-label {
    font-weight: 600;
    color: #3b82f6;
    min-width: 24px;
}

.option-text {
    flex: 1;
    color: #4b5563;
}

.answer-section {
    background: #f0f9ff;
    padding: 16px;
    border-radius: 8px;
}

.answer-row,
.analysis-row {
    margin-bottom: 12px;
}

.answer-row:last-child,
.analysis-row:last-child {
    margin-bottom: 0;
}

.answer-label,
.analysis-label {
    font-weight: 600;
    color: #1f2937;
    margin-right: 8px;
}

.answer-value {
    font-weight: 600;
    font-size: 16px;
}

.answer-value.primary {
    color: #10b981;
}

.analysis-content {
    display: inline-block;
    color: #4b5563;
    line-height: 1.8;
    padding: 8px 0;
}

.tags-list {
    display: flex;
    flex-wrap: wrap;
}

/* KaTeX样式优化 */
.latex-answer :deep(.katex),
.latex-content :deep(.katex) {
    font-size: 1.1em;
}

.latex-content :deep(.katex-display) {
    margin: 12px 0;
}

.question-text :deep(.katex) {
    font-size: 1.05em;
}

.option-text :deep(.katex) {
    font-size: 1em;
}
</style>
