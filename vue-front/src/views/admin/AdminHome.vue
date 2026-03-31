<template>
    <div class="admin-home">
        <!-- 顶部统计卡片区域 -->
        <div class="stats-section">
            <el-row :gutter="20">
                <el-col :span="6" v-for="(item, index) in statsCards" :key="item.title">
                    <div
                        class="stat-card"
                        :style="{ animationDelay: `${index * 0.1}s` }" :class="`stat-card-${index}`"
                    >
                       <div class="stat-background"></div>
                        <div class="stat-content">
                            <div class="stat-icon-wrapper" :style="{ background: item.iconBg }">
                                <el-icon class="stat-icon" :style="{ color: item.color }">
                                    <component :is="item.icon" />
                                </el-icon>
                            </div>
                            <div class="stat-info">
                                <div class="stat-title">{{ item.title }}</div>
                                <div class="stat-number">
                                    <CountUp :end-val="item.value" :duration="2000" />
                                    <span v-if="item.unit" class="stat-unit">{{ item.unit }}</span>
                                </div>
                                <div class="stat-trend" v-if="item.trend">
                                    <el-icon :size="12">
                                        <component :is="item.trendIcon" />
                                    </el-icon>
                                    <span :style="{ color: item.trendColor }">{{ item.trend }}</span>
                                    <span class="trend-label">较昨日</span>
                                </div>
                            </div>
                        </div>
                        <div class="stat-decoration" :style="{ borderColor: item.color }"></div>
                    </div>
                </el-col>
            </el-row>
        </div>

        <!-- 图表区域 -->
        <div class="charts-section">
            <el-row :gutter="20">
                <el-col :span="16">
                    <div class="chart-card main-chart">
                        <div class="chart-header">
                            <div class="chart-title">
                                <div class="title-icon"><el-icon><TrendCharts /></el-icon></div>
                                <span>用户活跃趋势</span>
                            </div>
                            <div class="chart-actions">
                                <el-radio-group v-model="activePeriod" size="small" @change="handlePeriodChange">
                                    <el-radio-button value="week">周</el-radio-button>
                                    <el-radio-button value="month">月</el-radio-button>
                                    <el-radio-button value="year">年</el-radio-button>
                                </el-radio-group>
                            </div>
                        </div>
                        <div id="activityChart" class="chart-container"></div>
                    </div>
                </el-col>

                <el-col :span="8">
                    <div class="chart-card">
                        <div class="chart-header">
                            <div class="chart-title">
                                <div class="title-icon"><el-icon><DataAnalysis /></el-icon></div>
                                <span>科目错题统计</span>
                            </div>
                        </div>
                        <div id="subjectPieChart" class="chart-container"></div>
                    </div>
                </el-col>
            </el-row>
        </div>

        <!-- 统计图表区域 -->
        <div class="charts-section">
            <el-row :gutter="20">
                <el-col :span="12">
                    <div class="chart-card">
                        <div class="chart-header">
                            <div class="chart-title">
                                <div class="title-icon"><el-icon><DataAnalysis /></el-icon></div>
                                <span>刷题完成率趋势</span>
                            </div>
                        </div>
                        <div id="completionRateChart" class="chart-container"></div>
                    </div>
                </el-col>

                <el-col :span="12">
                    <div class="chart-card">
                        <div class="chart-header">
                            <div class="chart-title">
                                <div class="title-icon"><el-icon><Clock /></el-icon></div>
                                <span>答题时段热力图</span>
                            </div>
                        </div>
                        <div id="mistakeHeatmapChart" class="chart-container"></div>
                    </div>
                </el-col>
            </el-row>
        </div>

        <!-- 统计图表区域 -->
        <div class="charts-section">
            <el-row :gutter="20">
                <el-col :span="10">
                    <div class="chart-card ranking-card">
                        <div class="chart-header">
                            <div class="chart-title">
                                <div class="title-icon trophy-icon"><el-icon><Trophy /></el-icon></div>
                                <span>学霸排行榜</span>
                            </div>
                            <el-tag size="small" type="warning" effect="dark" round>Top 10</el-tag>
                        </div>
                        <div class="ranking-list">
                            <div
                                v-for="(user, index) in topUsers"
                                :key="user.id"
                                class="ranking-item"
                                :class="`rank-row-${index + 1}`"
                            >
                                <div class="rank-badge">
                                    <span v-if="index === 0" class="medal-gold">🥇</span>
                                    <span v-else-if="index === 1" class="medal-silver">🥈</span>
                                    <span v-else-if="index === 2" class="medal-bronze">🥉</span>
                                    <span v-else class="rank-num">{{ index + 1 }}</span>
                                </div>
                                <el-avatar :size="42" :src="user.avatar" class="user-avatar">
                                    {{ user.username?.charAt(0) }}
                                </el-avatar>
                                <div class="user-info-box">
                                    <div class="user-top-row">
                                        <span class="user-name">{{ user.username }}</span>
                                        <span class="user-study-time">{{ user.studyHours }}h</span>
                                    </div>
                                    <div class="accuracy-progress">
                                        <el-progress 
                                            :percentage="user.accuracy" 
                                            :stroke-width="6" 
                                            :show-text="false"
                                            :color="index < 3 ? '#E6A23C' : '#409EFF'" 
                                        />
                                        <div class="progress-labels">
                                            <span>已刷 {{ user.exerciseCount }} 题</span>
                                            <span>准确率 {{ user.accuracy }}%</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </el-col>

                <el-col :span="14">
                    <el-row :gutter="20">
                        <el-col :span="24" style="margin-bottom: 20px;">
                            <div class="chart-card activity-card">
                                <div class="chart-header">
                                    <div class="chart-title">
                                        <div class="title-icon activity-icon"><el-icon><Clock /></el-icon></div>
                                        <span>实时学习动态</span>
                                    </div>
                                    <span class="live-indicator"><span class="dot"></span>实时更新中</span>
                                </div>
                                <div class="activity-feed">
                                    <div v-for="(act, idx) in realtimeActivities" :key="idx" class="feed-item">
                                        <div class="feed-time">{{ act.time }}</div>
                                        <div class="feed-dot" :style="{ background: act.color }"></div>
                                        <div class="feed-content">
                                            <span class="feed-user">{{ act.user }}</span>
                                            <span class="feed-action">{{ act.action }}</span>
                                            <span class="feed-detail">{{ act.detail }}</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </el-col>
                        <el-col :span="24">
                            <div class="chart-card">
                                <div class="chart-header">
                                    <div class="chart-title">
                                        <div class="title-icon"><el-icon><Reading /></el-icon></div>
                                        <span>科目热门度排行</span>
                                    </div>
                                </div>
                                <div id="subjectBarChart" class="chart-container" style="height: 200px;"></div>
                            </div>
                        </el-col>
                    </el-row>
                </el-col>
            </el-row>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted, h } from 'vue'
import request from '@/utils/request'
import * as echarts from 'echarts'
import {
    Reading, TrendCharts,
    DataAnalysis, Trophy, Clock
} from '@element-plus/icons-vue'

// --- 数字滚动组件 ---
const CountUp = {
    props: {
        endVal: { type: Number, default: 0 },
        duration: { type: Number, default: 1000 }
    },
    setup(props) {
        const displayValue = ref(0)
        let animationFrame = null
        let startTime = null
        const animate = (timestamp) => {
            if (!startTime) startTime = timestamp
            const progress = timestamp - startTime
            const percent = Math.min(progress / props.duration, 1)
            displayValue.value = Math.floor(percent * props.endVal)
            if (percent < 1) animationFrame = requestAnimationFrame(animate)
        }
        onMounted(() => { animationFrame = requestAnimationFrame(animate) })
        onUnmounted(() => { if (animationFrame) cancelAnimationFrame(animationFrame) })
        return () => h('span', displayValue.value)
    }
}

// --- 数据定义 ---
const statsCards = ref([
    { title: '题目总数', value: 0, unit: '道', icon: 'Memo', color: '#409EFF', iconBg: 'rgba(64, 158, 255, 0.1)', trend: '+12', trendIcon: 'ArrowUp', trendColor: '#67C23A' },
    { title: '注册用户', value: 0, unit: '人', icon: 'User', color: '#67C23A', iconBg: 'rgba(103, 194, 58, 0.1)', trend: '+8', trendIcon: 'ArrowUp', trendColor: '#67C23A' },
    { title: '练习总量', value: 0, unit: '次', icon: 'Reading', color: '#E6A23C', iconBg: 'rgba(230, 162, 60, 0.1)', trend: '+156', trendIcon: 'ArrowUp', trendColor: '#67C23A' },
    { title: '今日活跃', value: 0, unit: '人', icon: 'TrendCharts', color: '#F56C6C', iconBg: 'rgba(245, 108, 108, 0.1)', trend: '-5', trendIcon: 'ArrowDown', trendColor: '#F56C6C' }
])

let charts = {}
const activePeriod = ref('week')
const topUsers = ref([])
const realtimeActivities = ref([
    { user: '张同学', action: '完成了', detail: '《高等数学·极限》专项', time: '1分钟前', color: '#409EFF' },
    { user: '李同学', action: '获得了', detail: '《英语阅读》模考 85分', time: '5分钟前', color: '#67C23A' },
    { user: '王同学', action: '收藏了', detail: '《线性代数》重难点', time: '10分钟前', color: '#E6A23C' },
    { user: '刘同学', action: '刷新了', detail: '错题本记录', time: '20分钟前', color: '#F56C6C' }
])

// --- 逻辑函数 ---
const loadStats = async () => {
    try {
        const res = await request.get('/admin/statistics')
        const data = res.data || res

        // 更新统计数值
        statsCards.value[0].value = data.questionCount || 0
        statsCards.value[1].value = data.userCount || 0
        statsCards.value[2].value = data.exerciseCount || 0
        statsCards.value[3].value = data.todayActive || 0

        // 更新趋势数据
        updateTrend(0, data.questionTrend)
        updateTrend(1, data.userTrend)
        updateTrend(2, data.exerciseTrend)
        updateTrend(3, data.todayActiveTrend)

        await nextTick()
        initCharts()

        // 加载动态数据
        loadTopUsers()
        loadActivityTrend('week')
        loadRecentActivities()
        loadSubjectMistakeCount()
        loadHourlyActivityHeatmap()
    } catch (e) {
        console.warn('使用模拟数据');
        await nextTick(); initCharts({}); loadMockTopUsers();
    }
}

// 更新趋势数据的辅助函数
const updateTrend = (index, trendValue) => {
    if (trendValue === undefined || trendValue === null) return

    const trend = trendValue > 0 ? `+${trendValue}` : `${trendValue}`
    const trendIcon = trendValue >= 0 ? 'ArrowUp' : 'ArrowDown'
    const trendColor = trendValue >= 0 ? '#67C23A' : '#F56C6C'

    statsCards.value[index].trend = trend
    statsCards.value[index].trendIcon = trendIcon
    statsCards.value[index].trendColor = trendColor
}

const loadTopUsers = async () => {
    try {
        const res = await request.get('/admin/top-users', { params: { limit: 10 } })
        topUsers.value = res.data || []
    } catch (e) {
        console.warn('加载学霸排行榜失败，使用模拟数据')
        loadMockTopUsers()
    }
}

const loadMockTopUsers = () => {
    topUsers.value = [
        { id: 1, username: '张同学', exerciseCount: 2580, accuracy: 92, studyHours: 128 },
        { id: 2, username: '李同学', exerciseCount: 2340, accuracy: 89, studyHours: 115 },
        { id: 3, username: '王同学', exerciseCount: 2100, accuracy: 91, studyHours: 108 },
        { id: 4, username: '赵同学', exerciseCount: 1980, accuracy: 87, studyHours: 98 },
        { id: 5, username: '刘同学', exerciseCount: 1850, accuracy: 85, studyHours: 92 }
    ]
}

// 加载用户活跃趋势
const loadActivityTrend = async (period = 'week') => {
    try {
        const res = await request.get('/admin/activity-trend', { params: { period } })
        const data = res.data || {}

        // 更新活跃趋势图表
        if (charts.activity) {
            charts.activity.setOption({
                xAxis: {
                    type: 'category',
                    boundaryGap: false,
                    data: data.dates || []
                },
                series: [{
                    name: '活跃用户',
                    type: 'line',
                    smooth: true,
                    data: data.activeUsers || [],
                    symbol: 'circle',
                    symbolSize: 8,
                    itemStyle: { color: '#409EFF' },
                    lineStyle: { width: 4, shadowBlur: 10, shadowColor: 'rgba(64,158,255,0.3)' },
                    areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(64,158,255,0.2)' }, { offset: 1, color: 'transparent' }]) }
                }]
            })
        }
    } catch (e) {
        console.warn('加载活跃趋势失败')
    }
}

// 加载实时学习动态
const loadRecentActivities = async () => {
    try {
        const res = await request.get('/admin/recent-activities', { params: { limit: 10 } })
        const activities = res.data || []

        // 转换数据格式
        realtimeActivities.value = activities.map(item => ({
            user: item.username,
            action: item.action,
            detail: item.detail,
            time: item.time,
            color: item.color
        }))
    } catch (e) {
        console.warn('加载实时动态失败，使用模拟数据')
    }
}

// 加载科目错题统计
const loadSubjectMistakeCount = async () => {
    try {
        const res = await request.get('/admin/subject-mistake-count')
        const data = res.data || {}

        // 更新科目错题统计图表
        if (charts.pie) {
            charts.pie.setOption({
                tooltip: {
                    trigger: 'axis',
                    axisPointer: { type: 'shadow' },
                    formatter: '{b}: {c}题'
                },
                grid: {
                    left: '15%',
                    right: '10%',
                    bottom: '10%',
                    top: '15%',
                    containLabel: true
                },
                xAxis: {
                    type: 'value',
                    axisLabel: {
                        formatter: '{value}题'
                    },
                    splitLine: {
                        lineStyle: {
                            type: 'dashed'
                        }
                    }
                },
                yAxis: {
                    type: 'category',
                    data: data.subjects || ['政治', '英语', '数学', '408专业课'],
                    axisLabel: {
                        fontSize: 12
                    }
                },
                series: [{
                    name: '错题数量',
                    type: 'bar',
                    data: data.counts || [0, 0, 0, 0],
                    barWidth: 18,
                    itemStyle: {
                        borderRadius: [0, 10, 10, 0],
                        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                            { offset: 0, color: '#409EFF' },
                            { offset: 1, color: '#F56C6C' }
                        ])
                    },
                    label: {
                        show: true,
                        position: 'right',
                        formatter: '{c}题',
                        fontSize: 11,
                        color: '#606266'
                    },
                    emphasis: {
                        itemStyle: {
                            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                                { offset: 0, color: '#66b1ff' },
                                { offset: 1, color: '#f78989' }
                            ])
                        }
                    }
                }]
            })
        }
    } catch (e) {
        console.warn('加载科目错题统计失败')
    }
}

// 加载答题时段热力图
const loadHourlyActivityHeatmap = async () => {
    try {
        const res = await request.get('/admin/hourly-activity-heatmap')
        const data = res.data || {}

        if (charts.heat) {
            charts.heat.setOption({
                xAxis: {
                    data: data.hours || []
                },
                yAxis: {
                    data: data.days || []
                },
                series: [{
                    data: data.data || []
                }]
            })
        }
    } catch (e) {
        console.warn('加载答题时段热力图失败')
    }
}

// --- 图表初始化 ---
const initCharts = () => {
    // 销毁已存在的图表实例，避免重复初始化
    const chartIds = ['activity', 'pie', 'rate', 'heat', 'bar']
    chartIds.forEach(id => {
        if (charts[id] && !charts[id].isDisposed()) {
            charts[id].dispose()
        }
    })

    // 1. 活跃趋势图
    charts.activity = echarts.init(document.getElementById('activityChart'))
    charts.activity.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', boundaryGap: false, data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'] },
        yAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed' } } },
        series: [
            {
                name: '活跃用户', type: 'line', smooth: true, data: [120, 132, 101, 134, 90, 230, 210],
                symbol: 'circle', symbolSize: 8,
                itemStyle: { color: '#409EFF' },
                lineStyle: { width: 4, shadowBlur: 10, shadowColor: 'rgba(64,158,255,0.3)' },
                areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(64,158,255,0.2)' }, { offset: 1, color: 'transparent' }]) }
            }
        ]
    })

    // 2. 科目错题统计图
    charts.pie = echarts.init(document.getElementById('subjectPieChart'))
    charts.pie.setOption({
        tooltip: {
            trigger: 'axis',
            axisPointer: { type: 'shadow' },
            formatter: '{b}: {c}题'
        },
        grid: {
            left: '15%',
            right: '10%',
            bottom: '10%',
            top: '15%',
            containLabel: true
        },
        xAxis: {
            type: 'value',
            axisLabel: {
                formatter: '{value}题'
            },
            splitLine: {
                lineStyle: {
                    type: 'dashed'
                }
            }
        },
        yAxis: {
            type: 'category',
            data: ['政治', '英语', '数学', '408专业课'],
            axisLabel: {
                fontSize: 12
            }
        },
        series: [{
            name: '错题数量',
            type: 'bar',
            data: [],
            barWidth: 18,
            itemStyle: {
                borderRadius: [0, 10, 10, 0],
                color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                    { offset: 0, color: '#409EFF' },
                    { offset: 1, color: '#F56C6C' }
                ])
            },
            label: {
                show: true,
                position: 'right',
                formatter: '{c}题',
                fontSize: 11,
                color: '#606266'
            }
        }]
    })

    // 3. 完成率
    charts.rate = echarts.init(document.getElementById('completionRateChart'))
    charts.rate.setOption({
        xAxis: { type: 'category', data: ['W1', 'W2', 'W3', 'W4', 'W5', 'W6'] },
        yAxis: { type: 'value', max: 100 },
        series: [{
            data: [65, 82, 75, 90, 85, 95], type: 'bar', barWidth: 20,
            itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#409EFF' }, { offset: 1, color: '#0066CC' }]), borderRadius: [10, 10, 0, 0] }
        }]
    })

    // 4. 答题时段热力图
    charts.heat = echarts.init(document.getElementById('mistakeHeatmapChart'))
    charts.heat.setOption({
        tooltip: {
            position: 'top',
            formatter: function(params) {
                return `${params.value[1]} ${params.value[0]}<br/>答题: ${params.value[2]}次`
            }
        },
        grid: {
            height: '60%',
            top: '8%',
            left: '12%',
            right: '10%',
            bottom: '25%'
        },
        xAxis: {
            type: 'category',
            data: [],
            splitArea: {
                show: true
            },
            axisLabel: {
                fontSize: 10
            }
        },
        yAxis: {
            type: 'category',
            data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
            splitArea: {
                show: true
            },
            axisLabel: {
                fontSize: 11
            }
        },
        visualMap: {
            min: 0,
            max: 20,
            calculable: true,
            orient: 'horizontal',
            left: 'center',
            bottom: '2%',
            text: ['高', '低'],
            inRange: {
                color: ['#f0f9ff', '#bae0ff', '#74c0fc', '#339af0', '#1c7ed6', '#1864ab']
            },
            textStyle: {
                color: '#666'
            }
        },
        series: [{
            type: 'heatmap',
            data: [],
            label: {
                show: true,
                fontSize: 10,
                formatter: function(params) {
                    return params.value[2] > 0 ? params.value[2] : ''
                }
            },
            emphasis: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        }]
    })

    // 5. 柱状图
    charts.bar = echarts.init(document.getElementById('subjectBarChart'))
    charts.bar.setOption({
        grid: { top: 10, bottom: 20, left: 60 },
        xAxis: { type: 'value', show: false },
        yAxis: { type: 'category', data: ['高数', '英语', '政治', '线代'], axisLine: {show:false}, axisTick: {show:false} },
        series: [{ data: [120, 200, 150, 80], type: 'bar', barWidth: 15, itemStyle: { borderRadius: 10, color: '#409EFF' }, label: { show: true, position: 'right' } }]
    })
}

const handleResize = () => Object.values(charts).forEach(c => c.resize())
const handlePeriodChange = (period) => {
    loadActivityTrend(period)
}

onMounted(() => {
    loadStats()
    window.addEventListener('resize', handleResize)
})
onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
    Object.values(charts).forEach(c => c.dispose())
})
</script>

<style scoped>
.admin-home {
    padding: 0;
    animation: fadeIn 0.6s ease-out;
}

/* --- 1. 卡片通用样式 --- */
.chart-card {
    background: #ffffff;
    border-radius: 20px;
    padding: 20px;
    border: 1px solid rgba(235, 238, 245, 0.7);
    box-shadow: 0 8px 24px rgba(149, 157, 165, 0.05);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    margin-bottom: 20px;
}

.chart-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 32px rgba(64, 158, 255, 0.1);
}

.chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.chart-title {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 16px;
    font-weight: 600;
    color: #2c3e50;
}

.title-icon {
    width: 38px;
    height: 38px;
    background: #f0f7ff;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #409EFF;
}

.chart-container {
    width: 100%;
    height: 300px;
}

/* --- 2. 统计卡片微调 --- */
.stat-card {
    position: relative;
    border-radius: 16px;
    padding: 24px;
    background: linear-gradient(135deg, #ffffff 0%, #f7f9fc 100%);
    overflow: hidden;
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    cursor: pointer;
    border: 1px solid #ebeef5;
    margin-bottom: 20px;
    animation: slideIn 0.6s ease-out forwards;
    box-shadow: 0 2px 12px rgba(64, 158, 255, 0.08);
    border: 1px solid rgba(64, 158, 255, 0.1);
}
.stat-background {
    position: absolute;
    top: -50%;
    right: -20%;
    width: 200px;
    height: 200px;
    background: radial-gradient(circle, rgba(64, 158, 255, 0.08) 0%, transparent 70%);
    border-radius: 50%;
    transition: all 0.4s ease;
}

.stat-icon-wrapper {
    width: 64px;
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 16px;
    transition: all 0.4s ease;
}

.stat-card:hover .stat-icon-wrapper {
    transform: rotate(-10deg) scale(1.1);
}

.stat-icon {
    font-size: 32px;
}
.stat-info {
    flex: 1;
}

.stat-content {
    position: relative;
    display: flex;
    align-items: flex-start;
    gap: 16px;
    z-index: 1;
}
.stat-title {
    font-size: 14px;
    color: #909399;
    margin-bottom: 8px;
    font-weight: 500;
    letter-spacing: 0.5px;
}
.stat-number {
    font-size: 32px;
    font-weight: 700;
    color: #303133;
    margin-bottom: 8px;
    display: flex;
    align-items: baseline;
    gap: 4px;
    letter-spacing: -0.5px;
}
.stat-unit { font-size: 14px; color: #999; margin-left: 4px; }
.stat-trend {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 13px;
}
.trend-label {
    color: #909399;
    margin-left: 4px;
}
.stat-decoration {
    position: absolute;
    bottom: 0;
    right: 0;
    width: 80px;
    height: 80px;
    border-right: 3px solid;
    border-bottom: 3px solid;
    border-radius: 0 0 16px 0;
    opacity: 0.3;
    transition: all 0.4s ease;
}
.stat-card:hover .stat-decoration {
    width: 100px;
    height: 100px;
    opacity: 0.5;
}

/* --- 3. 学霸排行榜美化 --- */
.ranking-list {
    margin-top: 10px;
}

.ranking-item {
    display: flex;
    align-items: center;
    gap: 15px;
    padding: 12px;
    margin-bottom: 12px;
    border-radius: 14px;
    background: #f9fbff;
    transition: all 0.2s;
}

.ranking-item:hover {
    background: #fff;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.08);
    transform: scale(1.02);
}

.rank-badge {
    width: 30px;
    text-align: center;
    font-weight: bold;
}

.medal-gold { font-size: 24px; }
.rank-num { color: #909399; font-size: 14px; }

.user-info-box { flex: 1; }
.user-top-row { display: flex; justify-content: space-between; margin-bottom: 6px; }
.user-name { font-weight: 600; color: #303133; }
.user-study-time { font-size: 13px; color: #409EFF; font-weight: bold; }

.progress-labels {
    display: flex;
    justify-content: space-between;
    font-size: 11px;
    color: #909399;
    margin-top: 4px;
}

/* --- 4. 实时动态美化 --- */
.activity-feed {
    height: 200px;
    overflow-y: auto;
    padding-left: 10px;
}

.feed-item {
    display: flex;
    align-items: center;
    padding: 12px 0;
    position: relative;
    border-left: 2px solid #f0f2f5;
    padding-left: 20px;
}

.feed-time {
    position: absolute;
    left: -70px;
    width: 60px;
    text-align: right;
    font-size: 12px;
    color: #c0c4cc;
}

.feed-dot {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    position: absolute;
    left: -6px;
    border: 2px solid #fff;
    box-shadow: 0 0 4px rgba(0,0,0,0.1);
}

.feed-content {
    background: #f8fafc;
    padding: 8px 14px;
    border-radius: 8px;
    font-size: 13px;
    width: 100%;
}

.feed-user { font-weight: 600; color: #409EFF; margin-right: 8px; }
.feed-action { color: #606266; margin-right: 8px; }
.feed-detail { color: #909399; }

.live-indicator {
    display: flex;
    align-items: center;
    font-size: 12px;
    color: #67C23A;
    gap: 6px;
}

.dot {
    width: 8px;
    height: 8px;
    background: #67C23A;
    border-radius: 50%;
    animation: blink 1.5s infinite;
}

@keyframes blink {
    0% { opacity: 1; transform: scale(1); }
    50% { opacity: 0.4; transform: scale(1.2); }
    100% { opacity: 1; transform: scale(1); }
}

/* 隐藏滚动条 */
.activity-feed::-webkit-scrollbar { width: 4px; }
.activity-feed::-webkit-scrollbar-thumb { background: #e4e7ed; border-radius: 10px; }
</style>
