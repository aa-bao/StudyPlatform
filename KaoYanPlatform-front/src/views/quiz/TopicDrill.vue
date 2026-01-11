<template>
    <div class="knowledge-practice-container">
        <!-- 选择模式 -->
        <div v-if="mode === 'selection'" class="selection-container">
            <!-- 页面标题 -->
            <div class="page-header">
                <div class="header-left">
                    <h1>
                        <el-icon :size="32" class="header-icon">
                            <Reading />
                        </el-icon>
                        专项突破
                    </h1>
                    <p>按知识点系统化刷题，精准突破薄弱环节</p>
                </div>
            </div>

            <!-- 科目列表 -->
            <div class="subjects-section" v-loading="loading">
                <div v-for="(subject, index) in subjectList" :key="subject.id"
                     class="subject-row"
                     :style="{ 'animation-delay': index * 0.08 + 's' }"
                     @click="selectRootSubject(subject)">

                    <!-- 科目图标 -->
                    <div class="subject-icon-box" :class="'bg-color-' + (index % 6)">
                        <el-icon :size="36">
                            <Notebook />
                        </el-icon>
                    </div>

                    <!-- 科目信息 -->
                    <div class="subject-info">
                        <h3>{{ subject.name }}</h3>
                        <p class="subject-desc">点击开始该科目知识点练习</p>
                    </div>

                    <!-- 统计数据 -->
                    <div class="subject-metrics">
                        <div class="metric-item">
                            <span class="metric-value">{{ subject.questionCount }}</span>
                            <span class="metric-label">题目数</span>
                        </div>
                        <div class="metric-divider"></div>
                        <div class="metric-item">
                            <span class="metric-value">{{ subject.finishedCount }}</span>
                            <span class="metric-label">已完成</span>
                        </div>
                        <div class="metric-divider"></div>
                        <div class="metric-item">
                            <span class="metric-value accent">{{ Math.round((subject.finishedCount / subject.questionCount) * 100) }}%</span>
                            <span class="metric-label">完成率</span>
                        </div>
                    </div>

                    <!-- 进度条 -->
                    <div class="progress-wrapper">
                        <div class="progress-bar">
                            <div class="progress-fill"
                                 :style="{ width: Math.round((subject.finishedCount / subject.questionCount) * 100) + '%', background: getProgressGradient(index) }">
                            </div>
                        </div>
                        <el-icon class="arrow-icon" :size="20">
                            <ArrowRight />
                        </el-icon>
                    </div>
                </div>

                <!-- 空状态 -->
                <div v-if="subjectList.length === 0 && !loading" class="empty-state">
                    <el-empty description="暂无科目数据" />
                </div>
            </div>
        </div>

        <!-- 练习模式 -->
        <el-container v-else style="height: calc(100vh - 80px);">
            <!-- 左侧：知识点树 -->
            <el-aside class="tree-aside">
                <div class="aside-header">
                    <div class="header-top">
                        <el-button link @click="goBack" class="back-btn">
                            <el-icon>
                                <ArrowLeft />
                            </el-icon> 返回
                        </el-button>
                        <h3>{{ selectedRoot?.name }}</h3>
                    </div>
                    <div class="header-subtitle"><el-icon>
                            <Files />
                        </el-icon> 知识点目录</div>
                </div>
                <el-scrollbar>
                    <el-tree :data="treeData" :props="defaultProps" @node-click="handleNodeClick" node-key="id"
                        highlight-current :expand-on-click-node="false" default-expand-all :indent="16">
                        <template #default="{ node, data }">
                            <div class="custom-tree-node">
                                <span class="node-label" :title="node.label">{{ node.label }}</span>
                                <span class="node-stat" v-if="data.questionCount > 0">
                                    <el-tag size="small" :type="data.finishedCount > 0 ? 'success' : 'info'"
                                        effect="plain" round>
                                        {{ data.finishedCount }}/{{ data.questionCount }}
                                    </el-tag>
                                </span>
                            </div>
                        </template>
                    </el-tree>
                </el-scrollbar>
            </el-aside>

            <!-- 右侧：题目列表 -->
            <el-main class="questions-main" v-loading="questionsLoading">
                <div v-if="!currentSubject" class="empty-state">
                    <el-empty description="请在左侧选择一个知识点开始刷题" image-size="200">
                        <template #extra>
                            <p class="empty-tip">支持按考点、章节筛选题目，精准突破薄弱环节</p>
                        </template>
                    </el-empty>
                </div>
                <div v-else class="content-wrapper">
                    <div class="subject-header">
                        <div class="header-left">
                            <h2>{{ currentSubject.name }}</h2>
                            <el-tag effect="dark" type="primary" size="large">{{ questions.length }} 道题</el-tag>
                        </div>
                        <div class="header-right">
                            <el-progress :percentage="calculateProgress()" :stroke-width="10" :width="150"
                                type="line" />
                            <span class="progress-text">本节进度</span>
                        </div>
                    </div>

                    <div v-if="questions.length === 0" class="empty-questions">
                        <el-empty description="该知识点下暂无题目" />
                    </div>

                    <div class="question-list">
                        <div v-for="(q, index) in questions" :key="q.id" class="question-card" :id="'q-' + q.id">
                            <div class="q-header-row">
                                <div class="q-tag-group">
                                    <span class="q-index">#{{ index + 1 }}</span>
                                    <el-tag size="small" :type="getTypeColor(q.type)">{{ getTypeName(q.type) }}</el-tag>
                                    <el-tag v-if="q.difficulty" size="small" type="warning" effect="plain">难度: {{
                                        q.difficulty
                                        }}</el-tag>
                                </div>
                                <div class="q-actions">
                                    <!-- 占位符：未来可添加收藏等操作 -->
                                </div>
                            </div>

                            <div class="q-content" v-html="renderLatex(q.content)"></div>

                            <div class="q-options">
                                <div v-for="(opt, oIndex) in parseOptions(q.options)" :key="oIndex" class="option-item"
                                    :class="getOptionClass(q, opt)" @click="selectOption(q, opt)">
                                    <span class="opt-prefix">{{ getOptionLetter(opt) }}</span>
                                    <span class="opt-text" v-html="renderLatex(getOptionContent(opt))"></span>

                                    <el-icon v-if="q.isSubmitted && getOptionLetter(opt) === q.answer"
                                        class="status-icon correct"><Select /></el-icon>
                                    <el-icon
                                        v-if="q.isSubmitted && getOptionLetter(opt) === q.userSelected && q.userSelected !== q.answer"
                                        class="status-icon wrong">
                                        <CloseBold />
                                    </el-icon>
                                </div>
                            </div>

                            <div class="q-footer">
                                <div class="footer-left">
                                    <el-button type="primary" :disabled="q.isSubmitted || !q.userSelected"
                                        @click="submitAnswer(q)" round>
                                        提交答案
                                    </el-button>
                                    <el-button v-if="q.isSubmitted" @click="q.showAnalysis = !q.showAnalysis" plain
                                        round>
                                        {{ q.showAnalysis ? '收起解析' : '查看解析' }}
                                    </el-button>
                                </div>

                                <div v-if="q.isSubmitted" class="result-box" :class="q.isCorrect ? 'correct' : 'wrong'">
                                    <el-icon>
                                        <component :is="q.isCorrect ? 'Select' : 'CloseBold'" />
                                    </el-icon>
                                    <span v-if="q.isCorrect"> 回答正确</span>
                                    <span v-else> 回答错误，正确答案: <strong>{{ q.answer }}</strong></span>
                                </div>
                            </div>

                            <el-collapse-transition>
                                <div v-if="q.isSubmitted && (q.showAnalysis || !q.isCorrect)" class="analysis-wrapper">
                                    <div class="analysis-box">
                                        <div class="analysis-title"><el-icon>
                                                <Reading />
                                            </el-icon> 解析：</div>
                                        <div class="analysis-content" v-html="renderLatex(q.analysis || '暂无解析')"></div>
                                    </div>
                                </div>
                            </el-collapse-transition>
                        </div>
                    </div>
                </div>
            </el-main>
        </el-container>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Files, Select, CloseBold, Reading, Notebook, ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import katex from 'katex'
import 'katex/dist/katex.min.css'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const mode = ref('selection') // 'selection' | 'drill'
const subjectList = ref([])
const selectedRoot = ref(null)

const treeData = ref([])
const defaultProps = {
    children: 'children',
    label: 'name'
}
const loading = ref(false)
const questionsLoading = ref(false)
const currentSubject = ref(null)
const questions = ref([])
const userInfo = JSON.parse(localStorage.getItem('user') || '{}')

const loadSubjectList = async () => {
    loading.value = true
    try {
        const res = await request.get('/subject/tree', {
            params: { userId: userInfo.id }
        })
        subjectList.value = res.data
    } catch (error) {
        console.error(error)
    } finally {
        loading.value = false
    }
}

const selectRootSubject = (subject) => {
    selectedRoot.value = subject
    mode.value = 'drill'
    loadTree(subject.id)
}

const goBack = () => {
    // 如果是从科目列表页面跳转过来的（有 rootId），直接返回到 SubjectList
    if (route.query.rootId) {
        router.push({
            path: '/user/subject',
            query: {
                backToLevel: 1,
                subjectId: route.query.rootId
            }
        })
    } else if (mode.value === 'drill') {
        // 否则返回到科目选择页面
        mode.value = 'selection'
        selectedRoot.value = null
        currentSubject.value = null
        questions.value = []
        treeData.value = []
    } else {
        // 否则返回到上一页
        router.back()
    }
}

const loadTree = async (rootId) => {
    loading.value = true
    try {
        const res = await request.get('/subject/tree', {
            params: { userId: userInfo.id, rootId: rootId }
        })
        treeData.value = res.data
    } catch (error) {
        console.error(error)
    } finally {
        loading.value = false
    }
}

const handleNodeClick = async (data) => {
    currentSubject.value = data
    questionsLoading.value = true
    try {
        const res = await request.get('/question/list-by-knowledge-point', {
            params: { subjectId: data.id }
        })
        questions.value = res.data.map(q => ({
            ...q,
            userSelected: '',
            isSubmitted: false,
            isCorrect: false,
            showAnalysis: false
        }))
    } catch (error) {
        console.error(error)
    } finally {
        questionsLoading.value = false
    }
}

const calculateProgress = () => {
    if (!questions.value.length) return 0;
    const submitted = questions.value.filter(q => q.isSubmitted).length;
    return Math.round((submitted / questions.value.length) * 100);
}

const parseOptions = (opts) => {
    if (!opts) return []
    try {
        const parsed = typeof opts === 'string' ? JSON.parse(opts) : opts
        return parsed
    } catch (e) {
        return []
    }
}

const getOptionLetter = (opt) => {
    if (/^[A-Z][.、]/.test(opt)) {
        return opt.charAt(0)
    }
    return opt.charAt(0)
}

const getOptionContent = (opt) => {
    return opt.replace(/^[A-Z][.、\s]\s*/, '')
}

const getTypeName = (t) => ({ 1: '单选', 2: '多选', 3: '填空', 4: '简答' }[t] || '题目')
const getTypeColor = (t) => ({ 1: '', 2: 'success', 3: 'warning', 4: 'info' }[t] || '')

const selectOption = (q, opt) => {
    if (q.isSubmitted) return
    const letter = getOptionLetter(opt)

    if (q.type === 2) { // 多选
        let current = q.userSelected ? q.userSelected.split('') : []
        if (current.includes(letter)) {
            current = current.filter(l => l !== letter)
        } else {
            current.push(letter)
        }
        q.userSelected = current.sort().join('')
    } else { // 单选/判断
        q.userSelected = letter
    }
}

const getOptionClass = (q, opt) => {
    const letter = getOptionLetter(opt)
    if (q.isSubmitted) {
        const isAnswer = q.answer.includes(letter)
        const isSelected = q.userSelected.includes(letter)

        if (isAnswer) return 'correct-opt'
        if (isSelected && !isAnswer) return 'wrong-opt'
    } else {
        if (q.userSelected && q.userSelected.includes(letter)) return 'selected-opt'
    }
    return ''
}

const submitAnswer = async (q) => {
    if (!q.userSelected || (Array.isArray(q.userSelected) && q.userSelected.length === 0)) return

    let answerStr = q.userSelected
    if (Array.isArray(q.userSelected)) {
        answerStr = q.userSelected.sort().join('')
    }

    const dbAns = q.answer.replaceAll(/[,\\s]/g, "").toUpperCase();
    const isRight = answerStr === dbAns

    q.isCorrect = isRight
    q.isSubmitted = true

    try {
        await request.post('/record/submit', {
            userId: userInfo.id,
            questionId: q.id,
            userAnswer: answerStr
        })

        if (currentSubject.value) {
            currentSubject.value.finishedCount = (currentSubject.value.finishedCount || 0) + 1
        }
    } catch (error) {
        ElMessage.error('提交失败')
        q.isSubmitted = false
    }
}

const renderLatex = (content) => {
    if (!content) return ''
    return content.replace(/\$([^$]+)\$/g, (match, tex) => {
        try {
            return katex.renderToString(tex, {
                throwOnError: false,
                displayMode: false
            })
        } catch (err) {
            return match
        }
    }).replace(/\$\$([^$]+)\$\$/g, (match, tex) => {
        try {
            return katex.renderToString(tex, {
                throwOnError: false,
                displayMode: true
            })
        } catch (err) {
            return match
        }
    })
}

const getProgressGradient = (index) => {
    const gradients = [
        'linear-gradient(90deg, #3b82f6 0%, #60a5fa 100%)',
        'linear-gradient(90deg, #67c23a 0%, #85ce61 100%)',
        'linear-gradient(90deg, #e6a23c 0%, #ebb563 100%)',
        'linear-gradient(90deg, #f56c6c 0%, #f78989 100%)',
        'linear-gradient(90deg, #909399 0%, #a6a9ad 100%)',
        'linear-gradient(90deg, #667eea 0%, #764ba2 100%)'
    ]
    return gradients[index % gradients.length]
}

onMounted(async () => {
    // Check for rootId in query
    const rootId = route.query.rootId
    if (rootId) {
        // 从科目列表页面跳转过来，需要先加载科目列表获取科目信息
        await loadSubjectList()
        // 找到对应的科目
        const targetSubject = subjectList.value.find(s => s.id == rootId)
        if (targetSubject) {
            selectedRoot.value = targetSubject
            mode.value = 'drill'
            loadTree(rootId)
        } else {
            // 如果找不到，仍然进入 drill 模式，但使用 ID 作为名称
            selectedRoot.value = { id: rootId, name: '科目' }
            mode.value = 'drill'
            loadTree(rootId)
        }
    } else {
        loadSubjectList()
    }
})
</script>

<style scoped>
.knowledge-practice-container {
    background: linear-gradient(135deg, #f0f9ff 0%, #e6f7ff 100%);
    min-height: 100vh;
}

/* Selection Mode Styles */
.selection-container {
    padding: 32px;
    max-width: 1400px;
    margin: 0 auto;
    animation: fadeIn 0.4s ease-out;
}

@keyframes fadeIn {
    from {
        opacity: 0;
    }
    to {
        opacity: 1;
    }
}

.page-header {
    margin-bottom: 32px;
    animation: slideDown 0.5s ease-out;
}

@keyframes slideDown {
    from {
        opacity: 0;
        transform: translateY(-20px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.header-left h1 {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 32px;
    font-weight: 700;
    color: #303133;
    margin: 0 0 8px 0;
}

.header-icon {
    color: #409eff;
}

.header-left p {
    font-size: 16px;
    color: #909399;
    margin: 0;
}

.subjects-section {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.subject-row {
    background: #fff;
    border-radius: 12px;
    padding: 24px 28px;
    display: grid;
    grid-template-columns: auto 1fr auto auto;
    gap: 24px;
    align-items: center;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
    border: 1px solid transparent;
    animation: slideUp 0.5s ease-out backwards;
}

@keyframes slideUp {
    from {
        opacity: 0;
        transform: translateY(20px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.subject-row:hover {
    transform: translateX(8px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    border-color: #409eff;
}

.subject-icon-box {
    width: 64px;
    height: 64px;
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    transition: all 0.3s ease;
}

.subject-row:hover .subject-icon-box {
    transform: scale(1.1) rotate(5deg);
}

.bg-color-0 {
    background: linear-gradient(135deg, #3b82f6 0%, #60a5fa 100%);
    animation: gradientShift1 3s ease infinite;
    background-size: 200% 200%;
}

.bg-color-1 {
    background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
    animation: gradientShift2 3s ease infinite;
    background-size: 200% 200%;
}

.bg-color-2 {
    background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
    animation: gradientShift3 3s ease infinite;
    background-size: 200% 200%;
}

.bg-color-3 {
    background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
    animation: gradientShift4 3s ease infinite;
    background-size: 200% 200%;
}

.bg-color-4 {
    background: linear-gradient(135deg, #909399 0%, #a6a9ad 100%);
    animation: gradientShift5 3s ease infinite;
    background-size: 200% 200%;
}

.bg-color-5 {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    animation: gradientShift6 3s ease infinite;
    background-size: 200% 200%;
}

@keyframes gradientShift1 {
    0%, 100% { background-position: 0% 50%; }
    50% { background-position: 100% 50%; }
}

@keyframes gradientShift2 {
    0%, 100% { background-position: 0% 50%; }
    50% { background-position: 100% 50%; }
}

@keyframes gradientShift3 {
    0%, 100% { background-position: 0% 50%; }
    50% { background-position: 100% 50%; }
}

@keyframes gradientShift4 {
    0%, 100% { background-position: 0% 50%; }
    50% { background-position: 100% 50%; }
}

@keyframes gradientShift5 {
    0%, 100% { background-position: 0% 50%; }
    50% { background-position: 100% 50%; }
}

@keyframes gradientShift6 {
    0%, 100% { background-position: 0% 50%; }
    50% { background-position: 100% 50%; }
}

.subject-info {
    display: flex;
    flex-direction: column;
    gap: 6px;
}

.subject-info h3 {
    margin: 0;
    font-size: 20px;
    font-weight: 600;
    color: #303133;
    transition: color 0.3s;
}

.subject-row:hover .subject-info h3 {
    color: #409eff;
}

.subject-desc {
    margin: 0;
    font-size: 14px;
    color: #909399;
}

.subject-metrics {
    display: flex;
    align-items: center;
    gap: 20px;
}

.metric-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
}

.metric-value {
    font-size: 24px;
    font-weight: 700;
    color: #303133;
    line-height: 1;
}

.metric-value.accent {
    color: #409eff;
}

.metric-label {
    font-size: 12px;
    color: #909399;
}

.metric-divider {
    width: 1px;
    height: 32px;
    background: #e6e6e6;
}

.progress-wrapper {
    display: flex;
    align-items: center;
    gap: 16px;
    min-width: 200px;
}

.progress-bar {
    flex: 1;
    height: 8px;
    background: #f0f2f5;
    border-radius: 4px;
    overflow: hidden;
}

.progress-fill {
    height: 100%;
    border-radius: 4px;
    transition: width 0.6s ease;
    animation: progressShine 2s ease-in-out infinite;
}

@keyframes progressShine {
    0%, 100% {
        opacity: 1;
    }
    50% {
        opacity: 0.8;
    }
}

.arrow-icon {
    color: #409eff;
    transition: all 0.3s ease;
}

.subject-row:hover .arrow-icon {
    transform: translateX(4px);
    color: #66b1ff;
}

.empty-state {
    text-align: center;
    padding: 60px 20px;
}

/* Drill Mode Styles */
.tree-aside {
    width: 320px;
    background: white;
    border-right: 1px solid #e6e6e6;
    display: flex;
    flex-direction: column;
}

.aside-header {
    padding: 16px 20px;
    border-bottom: 1px solid #eee;
    background: #fff;
}

.header-top {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 10px;
}

.back-btn {
    font-size: 14px;
    padding: 0;
}

.aside-header h3 {
    margin: 0;
    font-size: 16px;
    color: #303133;
}

.header-subtitle {
    font-size: 12px;
    color: #909399;
    display: flex;
    align-items: center;
    gap: 5px;
}

.custom-tree-node {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    padding-right: 10px;
    font-size: 14px;
}

.node-label {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    margin-right: 8px;
    flex: 1;
}

.questions-main {
    padding: 20px 30px;
    overflow-y: auto;
    background: #f5f7fa;
}

.content-wrapper {
    max-width: 1000px;
    margin: 0 auto;
}

.subject-header {
    margin-bottom: 24px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.header-left {
    display: flex;
    align-items: center;
    gap: 16px;
}

.header-left h2 {
    margin: 0;
    font-size: 20px;
    color: #303133;
}

.header-right {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 4px;
}

.progress-text {
    font-size: 12px;
    color: #909399;
}

.question-list {
    display: flex;
    flex-direction: column;
    gap: 24px;
}

.question-card {
    background: #fff;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    transition: all 0.3s;
    border: 1px solid transparent;
}

.question-card:hover {
    box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
}

.q-header-row {
    display: flex;
    justify-content: space-between;
    margin-bottom: 16px;
}

.q-tag-group {
    display: flex;
    align-items: center;
    gap: 12px;
}

.q-index {
    font-family: monospace;
    font-weight: bold;
    color: #909399;
    background: #f0f2f5;
    padding: 2px 8px;
    border-radius: 4px;
}

.q-content {
    font-size: 16px;
    margin-bottom: 24px;
    line-height: 1.8;
    color: #303133;
}

.q-options {
    display: flex;
    flex-direction: column;
    gap: 12px;
    margin-bottom: 24px;
}

.option-item {
    padding: 12px 20px;
    border: 1px solid #dcdfe6;
    border-radius: 8px;
    cursor: pointer;
    display: flex;
    align-items: center;
    transition: all 0.2s;
    background: #fff;
    position: relative;
}

.option-item:hover {
    background-color: #f5f7fa;
    border-color: #c0c4cc;
}

.selected-opt {
    border-color: #409eff;
    background-color: #ecf5ff !important;
    color: #409eff;
    font-weight: 500;
}

.correct-opt {
    border-color: #67c23a !important;
    background-color: #f0f9eb !important;
    color: #67c23a !important;
}

.wrong-opt {
    border-color: #f56c6c !important;
    background-color: #fef0f0 !important;
    color: #f56c6c !important;
}

.opt-prefix {
    font-weight: bold;
    margin-right: 12px;
    width: 24px;
}

.status-icon {
    position: absolute;
    right: 16px;
    font-size: 18px;
}

.q-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-top: 16px;
    border-top: 1px dashed #eee;
}

.footer-left {
    display: flex;
    gap: 12px;
}

.result-box {
    display: flex;
    align-items: center;
    gap: 6px;
    font-weight: 600;
    font-size: 14px;
}

.result-box.correct {
    color: #67c23a;
}

.result-box.wrong {
    color: #f56c6c;
}

.analysis-wrapper {
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #f0f2f5;
}

.analysis-box {
    background: #fdf6ec;
    padding: 20px;
    border-radius: 8px;
    border-left: 4px solid #e6a23c;
}

.analysis-title {
    font-weight: bold;
    color: #e6a23c;
    margin-bottom: 12px;
    display: flex;
    align-items: center;
    gap: 6px;
}

.analysis-content {
    font-size: 15px;
    color: #606266;
    line-height: 1.6;
}

.empty-state {
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

.empty-tip {
    color: #909399;
    font-size: 14px;
    margin-top: 10px;
}

.empty-questions {
    margin-top: 40px;
}

:deep(.katex) {
    font-size: 1.1em;
}
</style>