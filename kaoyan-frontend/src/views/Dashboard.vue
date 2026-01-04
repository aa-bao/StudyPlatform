<template>
    <div class="dashboard-container">
        <el-row :gutter="20">
            <el-col :span="18">
                <el-card class="welcome-card" shadow="never">
                    <div class="welcome-content">
                        <h2>早安，准备冲刺 {{ displayYear }} 的研友！</h2>
                        <p>“星光不问赶路人，时光不负有心人。”</p>
                    </div>
                    <img src="@/assets/study.png" class="welcome-img" v-if="false">
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card class="countdown-card" shadow="never">
                    <div class="countdown-title">{{ userInfo.exam_year || '2027' }} 考研倒计时</div>
                    <div class="countdown-num">{{ daysRemaining }}<span>天</span></div>
                    <el-progress :percentage="progressPercentage" :stroke-width="10" status="warning" />
                </el-card>
            </el-col>
        </el-row>

        <el-row :gutter="20" style="margin-top: 20px;">
            <el-col :span="6">
                <el-card shadow="hover" class="stat-item">
                    <div class="stat-label">累计刷题</div>
                    <div class="stat-value">{{ stats.total }} <small>道</small></div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="hover" class="stat-item">
                    <div class="stat-label">综合正确率</div>
                    <div class="stat-value" :style="{ color: getAccuracyColor(stats.accuracy) }">
                        {{ stats.accuracy }}<small>%</small>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="hover" class="stat-item">
                    <div class="stat-label">累计时长</div>
                    <div class="stat-value" style="color: #67C23A">
                        {{ formatDuration(stats.totalTime).replace(/小时|分钟/g, '') }}<small>{{
                            formatDuration(stats.totalTime).match(/小时|分钟/)?.[0] || '分钟' }}</small>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="hover" class="stat-item" @click="$router.push('/user/correction-notebook')"
                    style="cursor: pointer">
                    <div class="stat-label">错题本待复习</div>
                    <div class="stat-value" style="color: #F56C6C">{{ errorCount }} <small>道</small></div>
                </el-card>
            </el-col>
        </el-row>

        <el-row :gutter="20" style="margin-top: 20px;">
            <el-col :span="12">
                <el-card header="最近刷题动态" shadow="never" style="height: 400px;">
                    <el-scrollbar height="320px">
                        <el-timeline v-if="recentList.length > 0">
                            <el-timeline-item v-for="item in recentList" :key="item.id"
                                :type="item.isCorrect === 1 ? 'success' : 'danger'" :timestamp="item.createTime">
                                答题结果：{{ item.isCorrect === 1 ? '正确' : '错误' }} (选项: {{ item.userAnswer }})
                            </el-timeline-item>
                        </el-timeline>
                        <el-empty description="暂无动态，快去刷题吧" v-else />
                    </el-scrollbar>
                </el-card>
            </el-col>

            <el-col :span="12">
                <el-card header="刷题正确率分布" shadow="never" style="height: 400px;">
                    <div id="accuracyChart" style="width: 100%; height: 320px;" v-show="stats.total > 0"></div>
                    <el-empty description="数据统计收集中..." v-if="stats.total === 0" />
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import request from '@/utils/request'
import * as echarts from 'echarts'
import { ref, onMounted, nextTick, computed } from 'vue'

// 响应式数据
const stats = ref({ total: 0, accuracy: 0 })
const errorCount = ref(0)
const recentList = ref([])
const userInfo = ref({}) // 存储用户信息



/**
 * 时间计算逻辑
 */
const parseYear = (yearStr) => {
    if (!yearStr) return null;
    // 使用正则提取所有数字
    const match = yearStr.match(/\d+/);
    if (!match) return null;
    let year = parseInt(match[0]);
    // 如果是两位数（如 27），补全为 2027
    if (year < 100) year += 2000;
    return year;
};

// 1. 修改计算剩余天数
const daysRemaining = computed(() => {
    const year = parseYear(userInfo.value.exam_year);
    if (!year) return 0;

    // 考研一般在目标年份前一年的12月下旬
    const targetDate = new Date(`${year - 1}-12-21`);
    const now = new Date();
    const diff = targetDate - now;
    return diff > 0 ? Math.ceil(diff / (1000 * 60 * 60 * 24)) : 0;
});

// 2. 修改计算进度条百分比
const progressPercentage = computed(() => {
    const year = parseYear(userInfo.value.exam_year);
    if (!userInfo.value.create_time || !year) return 0;

    const start = new Date(userInfo.value.create_time).getTime();
    const now = new Date().getTime();
    const end = new Date(`${year - 1}-12-21`).getTime();

    const total = end - start;
    const elapsed = now - start;

    if (total <= 0) return 100;
    let ratio = Math.floor((elapsed / total) * 100);

    // 边界处理，确保返回的是合法数字，避免 NaN
    const result = Math.min(Math.max(ratio, 0), 100);
    return isNaN(result) ? 0 : result;
});

const displayYear = computed(() => {
    const year = parseYear(userInfo.value.exam_year);
    // 如果获取到了年份就显示，否则显示默认值 2027
    return year ? `${year}` : '2027';
});

// 进度条文字格式化
const progressFormat = (percentage) => `备考进度: ${percentage}%`

const formatDuration = (seconds) => {
    if (!seconds) return '0分钟'
    const h = Math.floor(seconds / 3600)
    const m = Math.floor((seconds % 3600) / 60)
    if (h > 0) return `${h}小时`
    return `${m}分钟`
}

/**
 * 加载看板核心数据
 */
const loadDashboardData = async () => {
    try {
        const userStr = localStorage.getItem('user')
        console.log('从缓存获取的用户字符串:', userStr) // 加上这一行调试
        if (!userStr) {
            console.error("未找到登录信息")
            return
        }

        // 1. 获取并存入 userInfo
        const parsedUser = JSON.parse(userStr)
        // 兼容性处理：确保不管是 createTime 还是 create_time 都能被读取
        userInfo.value = {
            ...parsedUser,
            create_time: parsedUser.create_time || parsedUser.createTime,
            exam_year: parsedUser.exam_year || parsedUser.examYear
        }

        const currentUserId = userInfo.value.id

        // 2. 获取统计简报 (stats)
        const res = await request.get('/record/stats', { params: { userId: currentUserId } })
        if (res.code === 200 && res.data) { // 根据 Result.java 规范判断 code
            stats.value = res.data
            // 计算正确题目数量供图表使用
            const correctNum = Math.round(res.data.total * (res.data.accuracy / 100))
            if (res.data.total > 0) {
                // 使用 nextTick 确保 DOM 渲染后初始化图表
                nextTick(() => { initChart({ total: res.data.total, correct: correctNum }) })
            }
        }

        // 3. 获取错题总数
        // 注意：根据文档接口名可能是 getErrorBook
        const errorRes = await request.get('/question/getErrorBook', { params: { userId: currentUserId } })
        if (errorRes.code === 200 && errorRes.data) {
            errorCount.value = errorRes.data.length
        }

        // 4. 获取最近动态
        const recentRes = await request.get('/record/recent', { params: { userId: currentUserId } })
        if (recentRes.code === 200 && recentRes.data) {
            recentList.value = recentRes.data
        }
    } catch (e) {
        console.error("加载 Dashboard 数据失败:", e)
    }
}

/**
 * 初始化饼图
 */
const initChart = (data) => {
    const chartDom = document.getElementById('accuracyChart')
    if (!chartDom) return
    const myChart = echarts.init(chartDom)
    const option = {
        tooltip: { trigger: 'item', formatter: '{a} <br/>{b}: {c} ({d}%)' },
        legend: { bottom: '0', left: 'center' },
        color: ['#67C23A', '#F56C6C'],
        series: [
            {
                name: '答题占比',
                type: 'pie',
                radius: ['45%', '70%'],
                avoidLabelOverlap: false,
                itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
                label: { show: false, position: 'center' },
                emphasis: {
                    label: { show: true, fontSize: '18', fontWeight: 'bold' }
                },
                labelLine: { show: false },
                data: [
                    { value: data.correct, name: '正确' },
                    { value: data.total - data.correct, name: '错误' }
                ]
            }
        ]
    }
    myChart.setOption(option)

    // 响应式缩放
    window.addEventListener('resize', () => myChart.resize())
}


/**
 * 根据正确率返回颜色
 */
const getAccuracyColor = (acc) => {
    if (acc >= 80) return '#67C23A'
    if (acc >= 60) return '#E6A23C'
    return '#F56C6C'
}

onMounted(() => {
    loadDashboardData()
})
</script>

<style scoped>
.dashboard-container {
    padding: 10px;
}

.welcome-card {
    background: linear-gradient(135deg, #eef7ff 0%, #daedff 100%);
    border: none;
    height: 160px;
    display: flex;
    align-items: center;
}

.welcome-content h2 {
    color: #2c3e50;
    margin-bottom: 10px;
}

.welcome-content p {
    color: #5a6a7a;
    font-style: italic;
}

.countdown-card {
    text-align: center;
    height: 160px;
}

.countdown-title {
    color: #909399;
    font-size: 14px;
}

.countdown-num {
    font-size: 48px;
    font-weight: bold;
    color: #f56c6c;
    margin: 5px 0;
}

.countdown-num span {
    font-size: 16px;
    margin-left: 5px;
}

.stat-item {
    text-align: center;
    transition: transform 0.3s;
}

.stat-item:hover {
    transform: translateY(-5px);
}

.stat-label {
    color: #909399;
    font-size: 14px;
    margin-bottom: 10px;
}

.stat-value {
    font-size: 32px;
    font-weight: bold;
    color: #409EFF;
}

.stat-value small {
    font-size: 14px;
    margin-left: 4px;
    font-weight: normal;
}
</style>