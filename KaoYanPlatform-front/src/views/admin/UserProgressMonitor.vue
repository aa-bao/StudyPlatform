<template>
    <div class="user-progress-monitor">
        <!-- 统计卡片 -->
        <el-row :gutter="20" class="stats-row">
            <el-col :span="6">
                <el-card shadow="hover" class="stats-card">
                    <div class="stats-content">
                        <div class="stats-icon" style="background: #ecf5ff">
                            <el-icon :size="24" color="#409EFF">
                                <User />
                            </el-icon>
                        </div>
                        <div class="stats-info">
                            <div class="stats-value">{{ stats.totalUsers || 0 }}</div>
                            <div class="stats-label">学习用户数</div>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="hover" class="stats-card">
                    <div class="stats-content">
                        <div class="stats-icon" style="background: #f0f9ff">
                            <el-icon :size="24" color="#67C23A">
                                <DocumentChecked />
                            </el-icon>
                        </div>
                        <div class="stats-info">
                            <div class="stats-value">{{ stats.totalFinished || 0 }}</div>
                            <div class="stats-label">总完成题目数</div>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="hover" class="stats-card">
                    <div class="stats-content">
                        <div class="stats-icon" style="background: #fdf6ec">
                            <el-icon :size="24" color="#E6A23C">
                                <CircleCheck />
                            </el-icon>
                        </div>
                        <div class="stats-info">
                            <div class="stats-value">{{ stats.totalCorrect || 0 }}</div>
                            <div class="stats-label">总正确题目数</div>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="hover" class="stats-card">
                    <div class="stats-content">
                        <div class="stats-icon" style="background: #fef0f0">
                            <el-icon :size="24" color="#F56C6C">
                                <TrendCharts />
                            </el-icon>
                        </div>
                        <div class="stats-info">
                            <div class="stats-value">{{ stats.avgAccuracy ? stats.avgAccuracy.toFixed(1) : '0.0' }}%</div>
                            <div class="stats-label">平均正确率</div>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 学习排行榜 -->
        <el-card shadow="never" class="ranking-card">
            <template #header>
                <div class="card-header">
                    <span class="card-title">🏆 学习排行榜 Top 10</span>
                    <el-button type="primary" link @click="loadRanking" :icon="Refresh">刷新</el-button>
                </div>
            </template>
            <el-table :data="rankingData" v-loading="loadingRanking" stripe>
                <el-table-column label="排名" width="80" align="center">
                    <template #default="scope">
                        <div v-if="scope.$index < 3" class="rank-badge">
                            <el-tag v-if="scope.$index === 0" type="danger" effect="dark">🥇 1</el-tag>
                            <el-tag v-else-if="scope.$index === 1" type="warning" effect="dark">🥈 2</el-tag>
                            <el-tag v-else-if="scope.$index === 2" type="success" effect="dark">🥉 3</el-tag>
                        </div>
                        <div v-else class="rank-number">{{ scope.$index + 1 }}</div>
                    </template>
                </el-table-column>
                <el-table-column prop="user_id" label="用户ID" width="120" align="center" />
                <el-table-column label="用户昵称" width="150" align="center">
                    <template #default="scope">
                        {{ getUserName(scope.row.user_id) }}
                    </template>
                </el-table-column>
                <el-table-column prop="total_finished" label="完成题目数" width="120" align="center">
                    <template #default="scope">
                        <el-statistic :value="scope.row.total_finished" />
                    </template>
                </el-table-column>
                <el-table-column prop="total_correct" label="正确题目数" width="120" align="center">
                    <template #default="scope">
                        <el-statistic :value="scope.row.total_correct" />
                    </template>
                </el-table-column>
                <el-table-column label="正确率" width="120" align="center">
                    <template #default="scope">
                        <el-progress
                            :percentage="calculateAccuracy(scope.row.total_correct, scope.row.total_finished)"
                            :color="getProgressColor(calculateAccuracy(scope.row.total_correct, scope.row.total_finished))"
                        />
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <!-- 搜索筛选 -->
        <el-card shadow="never" class="search-card">
            <el-form :inline="true" :model="searchForm" class="search-form">
                <el-form-item label="用户ID">
                    <el-input v-model="searchForm.userId" placeholder="请输入用户ID" clearable style="width: 200px" />
                </el-form-item>
                <el-form-item label="科目">
                    <el-select v-model="searchForm.subjectId" placeholder="全部科目" clearable style="width: 200px">
                        <el-option
                            v-for="subject in allSubjects"
                            :key="subject.id"
                            :label="subject.name"
                            :value="subject.id"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadProgress" :icon="Search">搜索</el-button>
                    <el-button @click="resetSearch">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <!-- 学习进度表格 -->
        <el-card shadow="never" class="table-card">
            <el-table :data="tableData" v-loading="loading" stripe>
                <el-table-column prop="id" label="记录ID" width="100" align="center" />
                <el-table-column prop="userId" label="用户ID" width="120" align="center" />
                <el-table-column label="用户昵称" width="150" align="center">
                    <template #default="scope">
                        {{ getUserName(scope.row.userId) }}
                    </template>
                </el-table-column>
                <el-table-column prop="subjectId" label="科目ID" width="100" align="center" />
                <el-table-column label="科目名称" width="200">
                    <template #default="scope">
                        {{ getSubjectName(scope.row.subjectId) }}
                    </template>
                </el-table-column>
                <el-table-column prop="finishedCount" label="完成题目数" width="120" align="center">
                    <template #default="scope">
                        <el-tag type="primary">{{ scope.row.finishedCount || 0 }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="correctCount" label="正确题目数" width="120" align="center">
                    <template #default="scope">
                        <el-tag type="success">{{ scope.row.correctCount || 0 }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="正确率" width="200" align="center">
                    <template #default="scope">
                        <div class="accuracy-wrapper">
                            <el-progress
                                :percentage="calculateAccuracy(scope.row.correctCount, scope.row.finishedCount)"
                                :color="getProgressColor(calculateAccuracy(scope.row.correctCount, scope.row.finishedCount))"
                            />
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="updateTime" label="更新时间" width="180" align="center" />
            </el-table>

            <div class="pagination-container">
                <el-pagination
                    :current-page="pageNum"
                    :page-size="pageSize"
                    :page-sizes="[10, 20, 50, 100]"
                    :total="total"
                    layout="total, sizes, prev, pager, next, jumper"
                    @size-change="loadProgress"
                    @current-change="loadProgress"
                />
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, User, DocumentChecked, CircleCheck, TrendCharts } from '@element-plus/icons-vue'
import { getAllUserProgress, getProgressStats, getLearningRanking } from '@/api/userProgress'
import request from '@/utils/request'

// 数据定义
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const loadingRanking = ref(false)
const rankingData = ref([])

const stats = ref({
    totalUsers: 0,
    totalRecords: 0,
    totalFinished: 0,
    totalCorrect: 0,
    avgAccuracy: 0
})

const searchForm = ref({
    userId: null,
    subjectId: null
})

// 用户缓存
const userCache = ref(new Map())
// 科目缓存
const subjectCache = ref(new Map())
const allSubjects = ref([])
const examSpecs = ref([])

// 加载统计数据
const loadStats = async () => {
    try {
        const res = await getProgressStats()
        if (res.code === 200) {
            stats.value = res.data || stats.value
        }
    } catch (e) {
        console.error('获取统计数据失败', e)
    }
}

// 加载排行榜
const loadRanking = async () => {
    loadingRanking.value = true
    try {
        const res = await getLearningRanking(10)
        if (res.code === 200) {
            rankingData.value = res.data || []
            // 预加载用户信息
            rankingData.value.forEach(item => {
                loadUserInfo(item.user_id)
            })
        }
    } catch (e) {
        ElMessage.error('获取排行榜失败')
        console.error(e)
    } finally {
        loadingRanking.value = false
    }
}

// 加载学习进度
const loadProgress = async () => {
    loading.value = true
    try {
        const params = {
            pageNum: pageNum.value,
            pageSize: pageSize.value,
            ...searchForm.value
        }
        // 移除空值参数
        Object.keys(params).forEach(key => {
            if (params[key] === null || params[key] === undefined || params[key] === '') {
                delete params[key]
            }
        })

        const res = await getAllUserProgress(params)
        if (res.code === 200) {
            tableData.value = res.data.records || []
            total.value = res.data.total || 0

            // 预加载用户信息
            tableData.value.forEach(item => {
                if (item.userId) {
                    loadUserInfo(item.userId)
                }
            })
        }
    } catch (e) {
        ElMessage.error('获取学习进度失败')
        console.error(e)
    } finally {
        loading.value = false
    }
}

// 加载科目树
const loadSubjects = async () => {
    try {
        const res = await request.get('/subject/manage-tree')
        const treeData = res.data || res || []
        examSpecs.value = treeData

        // 提取所有科目（扁平化）
        const flattenSubjects = (nodes) => {
            let result = []
            nodes.forEach(node => {
                result.push(node)
                if (node.children && node.children.length > 0) {
                    result = result.concat(flattenSubjects(node.children))
                }
            })
            return result
        }

        allSubjects.value = flattenSubjects(treeData)
    } catch (e) {
        console.error('获取科目列表失败', e)
    }
}

// 加载用户信息
const loadUserInfo = async (userId) => {
    if (userCache.value.has(userId)) {
        return userCache.value.get(userId)
    }

    try {
        const res = await request.get('/user/userInfo', { params: { userId } })
        if (res.code === 200 && res.data) {
            const userInfo = res.data
            userCache.value.set(userId, userInfo)
            return userInfo
        }
    } catch (e) {
        console.error(`获取用户${userId}信息失败`, e)
    }
    return null
}

// 获取用户昵称
const getUserName = (userId) => {
    const user = userCache.value.get(userId)
    return user ? user.nickname || user.username : `用户${userId}`
}

// 获取科目名称
const getSubjectName = (subjectId) => {
    if (!subjectId) return '-'

    // 先从缓存查找
    if (subjectCache.value.has(subjectId)) {
        return subjectCache.value.get(subjectId)
    }

    // 递归查找科目树
    const findSubject = (nodes, targetId) => {
        for (const node of nodes) {
            if (String(node.id) === String(targetId)) {
                return node
            }
            if (node.children && node.children.length > 0) {
                const found = findSubject(node.children, targetId)
                if (found) return found
            }
        }
        return null
    }

    const subject = findSubject(examSpecs.value, subjectId)
    if (subject) {
        subjectCache.value.set(subjectId, subject.name)
        return subject.name
    }

    return `科目${subjectId}`
}

// 计算正确率
const calculateAccuracy = (correct, finished) => {
    if (!finished || finished === 0) return 0
    return Math.round((correct / finished) * 100)
}

// 获取进度条颜色
const getProgressColor = (percentage) => {
    if (percentage >= 80) return '#67C23A'
    if (percentage >= 60) return '#E6A23C'
    return '#F56C6C'
}

// 重置搜索
const resetSearch = () => {
    searchForm.value = {
        userId: null,
        subjectId: null
    }
    pageNum.value = 1
    loadProgress()
}

// 初始化
onMounted(() => {
    loadStats()
    loadRanking()
    loadSubjects()
    loadProgress()
})
</script>

<style scoped>
.user-progress-monitor {
    padding: 0;
}

/* 统计卡片 */
.stats-row {
    margin-bottom: 20px;
}

.stats-card {
    border-radius: 8px;
    transition: transform 0.2s;
}

.stats-card:hover {
    transform: translateY(-2px);
}

.stats-content {
    display: flex;
    align-items: center;
    padding: 10px 0;
}

.stats-icon {
    width: 56px;
    height: 56px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 16px;
}

.stats-info {
    flex: 1;
}

.stats-value {
    font-size: 28px;
    font-weight: 600;
    color: #303133;
    line-height: 1.2;
}

.stats-label {
    font-size: 14px;
    color: #909399;
    margin-top: 4px;
}

/* 排行榜样式 */
.ranking-card {
    margin-bottom: 20px;
    border-radius: 8px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.card-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
}

.rank-badge {
    font-size: 18px;
    font-weight: bold;
}

.rank-number {
    font-size: 16px;
    font-weight: 600;
    color: #606266;
}

/* 搜索区域 */
.search-card {
    margin-bottom: 20px;
    border-radius: 8px;
}

.search-form {
    margin-bottom: 0;
}

/* 表格区域 */
.table-card {
    border-radius: 8px;
}

.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}

.accuracy-wrapper {
    padding: 0 10px;
}
</style>
