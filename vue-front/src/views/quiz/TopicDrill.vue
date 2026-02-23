<template>
    <div class="topic-drill-container" :style="{ background: currentTheme?.lightBg || '#f8fafc' }">
        <div class="header-glow" :style="{ background: currentTheme?.gradient }"></div>

        <div class="content-wrapper">
            <div class="navbar-container">
                <nav class="subject-navbar">
                    <div class="nav-slider" :style="navSliderStyle"></div>
                    <div v-for="(subject, index) in displaySubjects" :key="subject.id"
                        :ref="el => { if (el) navRefs[index] = el }"
                        class="subject-pill"
                        :class="{ active: activeSubject?.id === subject.id }"
                        @click="handleSubjectChange(subject, index)">
                        <span class="pill-label">{{ subject.name }}</span>
                    </div>
                </nav>
            </div>

            <div class="main-layout">
                <aside class="kb-sidebar" :style="{ borderColor: currentTheme?.borderColor }">
                    <div class="sidebar-header">
                        <h3>知识图谱</h3>
                        <div class="tree-actions">
                            <button class="text-btn" @click="expandAllNodes">全部展开</button>
                            <button class="text-btn" @click="collapseAllNodes">全部收起</button>
                        </div>
                    </div>
                    <div class="tree-viewport">
                        <ModuleNode v-for="node in moduleTree" :key="node.id" :node="node" :depth="0"
                            :selected-id="selectedNode?.id" :theme-color="currentTheme?.color"
                            @node-selected="handleNodeClick" />
                    </div>
                </aside>

                <main class="content-panel" :style="{ borderColor: currentTheme?.borderColor }">
                    <transition name="fade-slide" mode="out-in">
                        <!-- 大平面刷题模式 -->
                        <div v-if="isDrillMode" :key="`drill-${selectedNode?.id}`" class="drill-mode">
                            <div class="drill-header">
                                <div class="drill-title">
                                    <h2>{{ selectedNode?.label }}</h2>
                                    <p class="drill-subtitle">开始刷题</p>
                                </div>
                                <div class="drill-controls">
                                    <button class="control-btn" @click="returnToDetail">
                                        返回详情
                                    </button>
                                    <button class="control-btn" @click="resetCards">
                                        重置布局
                                    </button>
                                    <button class="control-btn" @click="saveSession">
                                        保存进度
                                    </button>
                                </div>
                            </div>

                            <div class="drill-canvas" ref="drillCanvas">
                                <!-- 共享草稿区域 -->
                                <div class="shared-draft-section">
                                    <div class="draft-toolbar">
                                        <div class="tool-group">
                                            <label>颜色:</label>
                                            <div class="color-picker">
                                                <button
                                                    v-for="color in draftColors"
                                                    :key="color.value"
                                                    class="color-btn"
                                                    :style="{ backgroundColor: color.value }"
                                                    :class="{ active: draftColor === color.value }"
                                                    @click="changeDraftColor(color.value)"
                                                ></button>
                                            </div>
                                        </div>
                                        <div class="tool-group">
                                            <label>粗细:</label>
                                            <select v-model="draftLineWidth" @change="changeDraftLineWidth">
                                                <option value="1">细</option>
                                                <option value="2">中</option>
                                                <option value="3">粗</option>
                                                <option value="5">很粗</option>
                                            </select>
                                        </div>
                                        <div class="tool-group">
                                            <button @click="clearDraft">清空</button>
                                            <button @click="saveDraft">保存</button>
                                            <button @click="loadDraft">加载</button>
                                        </div>
                                    </div>
                                    <canvas
                                        ref="draftCanvas"
                                        class="draft-canvas"
                                        @mousedown="startDraftDrawing"
                                        @mousemove="drawDraft"
                                        @mouseup="stopDraftDrawing"
                                        @mouseleave="stopDraftDrawing"
                                        @touchstart="handleDraftTouchStart"
                                        @touchmove="handleDraftTouchMove"
                                        @touchend="stopDraftDrawing"
                                    ></canvas>
                                </div>

                                <!-- 题目卡片容器 -->
                                <QuestionCard
                                    v-for="(question, index) in questionsData"
                                    :key="question.id"
                                    :questionData="question"
                                    :questionIndex="index"
                                    :cardId="`card_${question.id}`"
                                    :style="getCardPositionStyle(index)"
                                />
                            </div>

                            <div class="drill-footer">
                                <div class="footer-stats">
                                    <span class="footer-stat">
                                        <span class="stat-value">{{ questionsData.length }}</span>
                                        <span class="stat-label">题目总数</span>
                                    </span>
                                    <span class="footer-stat">
                                        <span class="stat-value">{{ answeredCount }}</span>
                                        <span class="stat-label">已答题目</span>
                                    </span>
                                    <span class="footer-stat">
                                        <span class="stat-value">{{ correctCount }}</span>
                                        <span class="stat-label">正确答案</span>
                                    </span>
                                    <span class="footer-stat">
                                        <span class="stat-value">{{ accuracy }}%</span>
                                        <span class="stat-label">正确率</span>
                                    </span>
                                </div>
                            </div>
                        </div>

                        <!-- 详情模式 -->
                        <div v-else-if="selectedNode" :key="`detail-${selectedNode.id}`" class="detail-card">
                            <header class="detail-header">
                                <div class="title-section">
                                    <span class="type-tag" :style="{ background: currentTheme?.color }">
                                        {{ moduleTypeMap[selectedNode.type]?.label }}
                                    </span>
                                    <h1>{{ selectedNode.label }}</h1>
                                </div>

                                <div class="quick-stats">
                                    <div class="stat-item">
                                        <span class="label">完成度</span>
                                        <div class="mini-progress-bar">
                                            <div class="fill"
                                                :style="{ width: (correctRate || 0) + '%', background: currentTheme?.gradient }">
                                            </div>
                                        </div>
                                        <span class="value">{{ correctRate || 0 }}%</span>
                                    </div>
                                    <div class="stat-item" v-if="selectedNode.examFrequency">
                                        <span class="label">考察热度</span>
                                        <span class="value">{{ selectedNode.examFrequency }}次 / 5年</span>
                                    </div>
                                </div>
                            </header>

                            <section class="info-grid">
                                <div class="info-block solution">
                                    <div v-if="selectedNode.solutionPatterns?.length" class="pattern-steps">
                                        <div v-for="(p, i) in selectedNode.solutionPatterns" :key="i" class="step-item">
                                            <span class="step-num" :style="{ borderColor: currentTheme?.color, color: currentTheme?.color }">{{ i + 1 }}</span>
                                            <p>{{ p }}</p>
                                        </div>
                                    </div>
                                </div>

                                <div class="info-block mistakes" v-if="selectedNode.commonMistakes?.length">
                                    <h3><i class="icon">⚠️</i> 高频误区</h3>
                                    <ul class="mistake-list">
                                        <li v-for="m in selectedNode.commonMistakes" :key="m">{{ m }}</li>
                                    </ul>
                                </div>
                            </section>

                            <!-- 视频讲解 -->
                            <section class="video-section">
                                <div class="video-header">
                                    <h3>视频讲解</h3>
                                    <span class="video-source">Bilibili</span>
                                </div>
                                <div class="video-container">
                                    <iframe
                                        v-if="selectedNode?.videoUrl"
                                        class="bilibili-video"
                                        :src="getBilibiliEmbedUrl(selectedNode.videoUrl)"
                                        scrolling="no"
                                        border="0"
                                        frameborder="no"
                                        framespacing="0"
                                        allowfullscreen="true"
                                    ></iframe>
                                    <div v-else class="no-video">
                                        <p>暂无视频讲解</p>
                                    </div>
                                </div>
                            </section>

                            <!-- 大平面刷题入口 -->
                            <section class="drill-entry-section">
                                <div class="entry-card" @click="enterTopicDrill">
                                    <h3>大观</h3>
                                    <p>进入大观刷题模式，支持手写笔记和草稿</p>
                                    <div class="entry-stats">
                                        <span class="stat-item">
                                            <span class="stat-value">{{ questionCount }}</span>
                                            <span class="stat-label">题目总数</span>
                                        </span>
                                        <span class="stat-item">
                                            <span class="stat-value">{{ answeredCount }}</span>
                                            <span class="stat-label">已作答</span>
                                        </span>
                                        <span class="stat-item">
                                            <span class="stat-value">{{ correctRate }}%</span>
                                            <span class="stat-label">正确率</span>
                                        </span>
                                    </div>
                                    <button class="entry-button" :style="{ background: currentTheme?.gradient }">
                                        开始刷题
                                    </button>
                                </div>
                            </section>
                        </div>

                        <!-- 空状态 -->
                        <div v-else :key="`empty`" class="empty-state">
                            <div class="floating-icons">📐 🧪 🖋️</div>
                            <h2>开始你的专项练习</h2>
                            <p>从左侧图谱中选择一个知识模块查看详情</p>
                        </div>
                    </transition>
                </main>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, defineComponent, h, computed, reactive, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getExamSpecListApi, getTreeByExamSpecApi } from '@/api/subject'
import {
  saveTopicDrillProgressApi,
  loadTopicDrillProgressApi,
  getQuestionsByKnowledgePointApi,
  getKnowledgePointStatsApi
} from '@/api/topicDrill'
import { ElMessage } from 'element-plus'
import QuestionCard from '@/components/QuestionCard.vue'

// 草稿颜色选项
const draftColors = [
  { name: '黑色', value: '#000000' },
  { name: '红色', value: '#FF0000' },
  { name: '蓝色', value: '#0000FF' },
  { name: '绿色', value: '#00FF00' },
  { name: '黄色', value: '#FFFF00' },
  { name: '橙色', value: '#FFA500' }
]

const navRefs = ref([])
const navSliderStyle = reactive({
    width: '0px',
    left: '0px',
    background: ''
})

// 主科目配置（只显示4个主科目）
const MAIN_SUBJECTS = ['政治', '英语', '数学', '408']

// 科目名称映射：将后端返回的细分科目映射到主科目
const SUBJECT_NAME_MAP = {
    '英语一': '英语',
    '英语二': '英语',
    '数学一': '数学',
    '数学二': '数学',
    '数学三': '数学'
}

// 统一颜色配置（根据主科目设置颜色）
const SUBJECT_COLORS = {
    '政治': {
        color: '#ef4444',
        gradient: 'linear-gradient(135deg, #ef4444, #f87171)',
        bgGradient: 'rgba(239, 68, 68, 0.15)',
        lightBg: '#fef2f2',
        borderColor: '#fecaca'
    },
    '408': {
        color: '#3b82f6',
        gradient: 'linear-gradient(135deg, #3b82f6, #60a5fa)',
        bgGradient: 'rgba(59, 130, 246, 0.15)',
        lightBg: '#eff6ff',
        borderColor: '#bfdbfe'
    },
    '英语': {
        color: '#8b5cf6',
        gradient: 'linear-gradient(135deg, #8b5cf6, #a78bfa)',
        bgGradient: 'rgba(139, 92, 246, 0.15)',
        lightBg: '#f5f3ff',
        borderColor: '#ddd6fe'
    },
    '数学': {
        color: '#10b981',
        gradient: 'linear-gradient(135deg, #10b981, #34d399)',
        bgGradient: 'rgba(16, 185, 129, 0.15)',
        lightBg: '#ecfdf5',
        borderColor: '#a7f3d0'
    }
}

/**
 * 局部组件：ModuleNode
 * 负责递归显示知识树条目
 */
const ModuleNode = defineComponent({
    name: 'ModuleNode',
    props: ['node', 'depth', 'selectedId', 'themeColor'],
    emits: ['node-selected'],
    setup(props, { emit }) {
        const isExpanded = ref(props.node.expanded ?? props.depth < 1)

        // 监听外部数据变化（点击“全部展开”时生效）
        watch(() => props.node.expanded, (newVal) => {
            if (newVal !== undefined) isExpanded.value = newVal
        })
        const toggle = (e) => {
            e.stopPropagation()
            isExpanded.value = !isExpanded.value
            props.node.expanded = isExpanded.value
            emit('node-selected', props.node)
        }

        return () => h('div', { class: ['node-wrapper', `depth-${props.depth}`] }, [
            h('div', {
                class: ['node-item', {
                    'is-active': props.selectedId === props.node.id,
                    'has-children': props.node.children?.length > 0
                }],
                style: props.selectedId === props.node.id ? {
                    background: 'var(--node-active-bg, #eff6ff)',
                    color: props.themeColor || '#2563eb'
                } : {},
                onClick: toggle
            }, [
                props.node.children?.length ? h('span', { class: ['arrow', isExpanded.value ? 'down' : 'right'] }, '▶') : h('span', { class: 'dot' }),
                h('span', { class: 'label' }, props.node.label),
                props.node.weight ? h('span', { class: 'weight' }, `${props.node.weight}%`) : null
            ]),
            (isExpanded.value && props.node.children) ? h('div', { class: 'children-container' },
                props.node.children.map(child => h(ModuleNode, {
                    node: child,
                    depth: props.depth + 1,
                    selectedId: props.selectedId,
                    themeColor: props.themeColor,
                    onNodeSelected: (n) => emit('node-selected', n)
                }))
            ) : null
        ])
    }
})

// --- 状态管理 ---
const subjects = ref([]) // 后端返回的原始科目列表
const activeSubject = ref(null) // 当前选中的科目（后端原始数据）
const activeExamSpec = ref(null) // 当前选中的具体考试规格（如"数学一"）
const moduleTree = ref([])
const selectedNode = ref(null)

// 大平面刷题相关状态
const questionCount = ref(0) // 题目数量
const answeredCount = ref(0) // 已答题目数量
const correctRate = ref(0) // 正确率
const isDrillMode = ref(false) // 是否进入大平面模式
const questionsData = ref([]) // 题目数据
const correctCount = ref(0) // 正确答案数量
const accuracy = ref(0) // 正确率

// 共享草稿相关状态
const draftCanvas = ref(null)
const isDraftDrawing = ref(false)
const lastDraftX = ref(0)
const lastDraftY = ref(0)
const draftColor = ref('#000000')
const draftLineWidth = ref(2)

// 卡片尺寸
const cardWidth = 300
const cardHeight = 400
const cardGap = 20

// 进度条状态
const progressPercentage = ref(0) // 进度百分比
const progressSteps = ref([]) // 进度步骤

// 计算属性：处理后的显示科目列表（只显示4个主科目）
const displaySubjects = computed(() => {
    const mainSubjectMap = {}

    subjects.value.forEach(item => {
        // 映射到主科目名称
        const mainName = SUBJECT_NAME_MAP[item.name] || item.name

        // 如果该主科目还不存在，初始化数组
        if (!mainSubjectMap[mainName]) {
            mainSubjectMap[mainName] = {
                id: item.id, // 默认使用第一个的id
                name: mainName, // 使用主科目名称作为显示名称
                examSpecs: [] // 保存该主科目下的所有考试规格
            }
        }

        // 将当前考试规格添加到数组中
        mainSubjectMap[mainName].examSpecs.push(item)
    })

    // 按照 MAIN_SUBJECTS 的顺序返回
    return MAIN_SUBJECTS
        .filter(name => mainSubjectMap[name])
        .map(name => {
            const subject = mainSubjectMap[name]
            return {
                ...subject,
                color: SUBJECT_COLORS[name]?.color,
                gradient: SUBJECT_COLORS[name]?.gradient,
                bgGradient: SUBJECT_COLORS[name]?.bgGradient,
                lightBg: SUBJECT_COLORS[name]?.lightBg,
                borderColor: SUBJECT_COLORS[name]?.borderColor
            }
        })
})

// 计算当前科目的主题色
const currentTheme = computed(() => {
    if (!activeSubject.value) return SUBJECT_COLORS['政治']

    // 获取主科目名称
    const mainName = SUBJECT_NAME_MAP[activeSubject.value.name] || activeSubject.value.name
    return SUBJECT_COLORS[mainName] || SUBJECT_COLORS['政治']
})

const moduleTypeMap = {
    primary: { label: '核心模块', color: '#3b82f6' },
    secondary: { label: '题型群组', color: '#2563eb' },
    tertiary: { label: '知识考点', color: '#1d4ed8' },
    quaternary: { label: '原子考点', color: '#1e40af' }
}

// --- 逻辑处理 ---
// 获取用户选择的考试规格
const getUserExamSpec = (mainSubject, allExamSpecs) => {
    // 从localStorage获取用户信息（注意是'user'而不是'userInfo'）
    const userStr = localStorage.getItem('user') || '{}'
    const userInfo = JSON.parse(userStr)

    console.log('getUserExamSpec - mainSubject:', mainSubject)
    console.log('getUserExamSpec - userInfo:', userInfo)

    const examSubjectsStr = userInfo.examSubjects || ''
    const userSubjects = examSubjectsStr.split(',').map(s => s.trim()).filter(s => s)

    console.log('getUserExamSpec - examSubjectsStr:', examSubjectsStr)
    console.log('getUserExamSpec - userSubjects:', userSubjects)

    // 根据主科目找到用户选择的具体科目
    const userSpecificSubject = userSubjects.find(subject => {
        return SUBJECT_NAME_MAP[subject] === mainSubject
    })

    console.log('getUserExamSpec - userSpecificSubject:', userSpecificSubject)

    if (!userSpecificSubject) {
        return null
    }

    // 从后端返回的所有考试规格中找到匹配的
    const matchedSpec = allExamSpecs.find(spec => {
        return spec.name === userSpecificSubject
    })

    console.log('getUserExamSpec - matchedSpec:', matchedSpec)

    return matchedSpec
}

const fetchExamSpecs = async () => {
    try {
        const res = await getExamSpecListApi()
        if (res.code === 200) {
            subjects.value = res.data
            console.log('subjects:', res.data)
            // 加载完成后初始化第一个科目
            await nextTick()
            if (displaySubjects.value.length > 0) {
                handleSubjectChange(displaySubjects.value[0], 0)
            }
        }
    } catch (e) { ElMessage.error('加载科目失败') }
}

const handleSubjectChange = async (subject, index) => {
    // 保存主科目信息
    activeSubject.value = subject

    let targetExamSpec = null

    if (subject.name === '政治' || subject.name === '408') {
        targetExamSpec = subject.examSpecs[0]
    } else {
        targetExamSpec = getUserExamSpec(subject.name, subjects.value)

        if (!targetExamSpec) {
            ElMessage.warning(`请先在个人信息中选择${subject.name}的考试类型`)
            return
        }
    }

    // 保存当前选中的考试规格
    activeExamSpec.value = targetExamSpec

    try {
        const res = await getTreeByExamSpecApi(targetExamSpec.id)
        if (res.code === 200) {
            moduleTree.value = convertTreeData(res.data)
            selectedNode.value = moduleTree.value[0] || null
        }
    } catch (e) { ElMessage.error('加载图谱失败') }

    // 更新滑块位置
    await nextTick()
    const el = navRefs.value[index]
    if (el) {
        navSliderStyle.width = `${el.offsetWidth}px`
        navSliderStyle.left = `${el.offsetLeft}px`
        navSliderStyle.background = subject.gradient
        navSliderStyle.opacity = '1'
    }
}

const convertTreeData = (nodes) => {
    console.log('convertTreeData - nodes:', nodes)
    return nodes.map(node => ({
        id: node.id,
        label: node.name,
        type: node.level === 1 ? 'primary' : node.level === 2 ? 'secondary' : 'tertiary',
        weight: node.dynamicWeight, // 使用动态权重
        mastery: node.mastery || Math.floor(Math.random() * 100), // 演示用，后端有则取后端
        solutionPatterns: node.solutionPatterns,
        commonMistakes: node.commonMistakes,
        examFrequency: node.examFrequency,
        videoUrl: node.videoUrl, // 新增视频链接字段
        children: node.children?.length ? convertTreeData(node.children) : []
    }))
}

const handleNodeClick = (node) => {
    selectedNode.value = node
    // 加载该知识点的题目数据
    loadKnowledgePointData(node)
}

// 加载知识点数据
const loadKnowledgePointData = async (node) => {
    try {
        const userStr = localStorage.getItem('user') || '{}'
        const userInfo = JSON.parse(userStr)

        // 调用API获取该知识点的题目数量、已作答数量、正确率等数据
        const res = await getKnowledgePointStatsApi(node.id, userInfo.id)
        if (res.code === 200) {
            questionCount.value = res.data.questionCount || 0
            answeredCount.value = res.data.answeredCount || 0
            correctRate.value = res.data.correctRate || 0
        } else {
            // 如果API调用失败，使用默认值
            questionCount.value = 0
            answeredCount.value = 0
            correctRate.value = 0
        }
    } catch (error) {
        console.error('加载知识点数据失败:', error)
        // 异常情况下使用默认值
        questionCount.value = 0
        answeredCount.value = 0
        correctRate.value = 0
    }
    // 初始化进度条
    initProgressBar()
}

// 初始化进度条
const initProgressBar = () => {
    // 计算进度百分比
    if (questionCount.value > 0) {
        progressPercentage.value = Math.round((answeredCount.value / questionCount.value) * 100)
    } else {
        progressPercentage.value = 0
    }

    // 初始化进度步骤
    progressSteps.value = [
        { name: '开始学习', completed: true, percentage: 0 },
        { name: '完成10%', completed: progressPercentage.value >= 10, percentage: 10 },
        { name: '完成25%', completed: progressPercentage.value >= 25, percentage: 25 },
        { name: '完成50%', completed: progressPercentage.value >= 50, percentage: 50 },
        { name: '完成75%', completed: progressPercentage.value >= 75, percentage: 75 },
        { name: '完成100%', completed: progressPercentage.value >= 100, percentage: 100 }
    ]
}

// 更新进度条
const updateProgressBar = () => {
    if (questionCount.value > 0) {
        progressPercentage.value = Math.round((answeredCount.value / questionCount.value) * 100)
    } else {
        progressPercentage.value = 0
    }

    // 更新进度步骤
    progressSteps.value.forEach(step => {
        step.completed = progressPercentage.value >= step.percentage
    })
}

// 解析 Bilibili 视频 URL，生成嵌入代码
const getBilibiliEmbedUrl = (url) => {
    // 匹配 Bilibili 视频 URL 中的 BV 号
    const bvMatch = url.match(/BV[a-zA-Z0-9]+/)
    if (bvMatch) {
        const bvid = bvMatch[0]
        return `https://player.bilibili.com/player.html?bvid=${bvid}&page=1&high_quality=1&danmaku=0`
    }
    return ''
}

// 进入大平面刷题模式
const router = useRouter()

const enterTopicDrill = () => {
    if (!selectedNode.value) {
        ElMessage.warning('请先选择一个知识点')
        return
    }
    // 跳转到新的大平面刷题页面
    router.push({
        path: '/user/drill-page',
        query: {
            knowledgePointId: selectedNode.value.id,
            knowledgePointName: selectedNode.value.label
        }
    })
}

// 返回详情模式
const returnToDetail = () => {
    isDrillMode.value = false
}

// 加载知识点题目数据
const loadQuestionsByKnowledgePoint = async (knowledgePointId) => {
    try {
        // 调用API获取题目数据
        const res = await getQuestionsByKnowledgePointApi(knowledgePointId)
        if (res.code === 200) {
            // 处理题目数据，添加必要字段
            questionsData.value = res.data.map(question => ({
                id: question.id,
                title: `题目 ${question.id}`,
                content: question.content || '暂无题目内容',
                answer: question.answer || '暂无答案',
                questionImage: question.contentJson?.questionImage || null,
                answerImage: question.contentJson?.answerImage || null,
                difficulty: question.difficulty || 3,
                isAnswered: false,
                isCorrect: false
            }))
            calculateStats()
            ElMessage.success('题目加载成功')
        }
    } catch (error) {
        ElMessage.error('题目加载失败')
        console.error('Error loading questions:', error)
    }
}

// 计算卡片位置样式（平铺布局）
const getCardPositionStyle = (index) => {
    const cols = 3 // 每行显示的卡片数量
    const col = index % cols
    const row = Math.floor(index / cols)

    return {
        left: `${col * (cardWidth + cardGap)}px`,
        top: `${row * (cardHeight + cardGap)}px`,
        width: `${cardWidth}px`,
        height: `${cardHeight}px`
    }
}

// 生成模拟题目数据
const generateMockQuestions = () => {
    const count = Math.floor(Math.random() * 20) + 10
    const questions = []

    for (let i = 0; i < count; i++) {
        questions.push({
            id: `q_${Date.now()}_${i}`,
            title: `题目 ${i + 1}`,
            content: `这是第${i + 1}道题的内容，包含了${selectedNode.value?.label}相关的知识点`,
            answer: `这是第${i + 1}道题的答案`,
            questionImage: `https://picsum.photos/seed/question_${i}_${Date.now()}/400/200`,
            answerImage: `https://picsum.photos/seed/answer_${i}_${Date.now()}/400/200`,
            difficulty: Math.floor(Math.random() * 3) + 1, // 1-简单, 2-中等, 3-困难
            isAnswered: false,
            isCorrect: false
        })
    }
    return questions
}

// 计算统计数据
const calculateStats = () => {
    answeredCount.value = questionsData.value.filter(q => q.isAnswered).length
    correctCount.value = questionsData.value.filter(q => q.isAnswered && q.isCorrect).length
    accuracy.value = questionsData.value.length > 0 ?
        Math.round((correctCount.value / answeredCount.value) * 100) : 0
}

// 重置卡片布局
const resetCards = () => {
    localStorage.removeItem('questionCards')
    ElMessage.success('卡片布局已重置')
}

// 保存答题进度
const saveSession = async () => {
    try {
        const userStr = localStorage.getItem('user') || '{}'
        const userInfo = JSON.parse(userStr)

        const progressData = {
            userId: userInfo.id,
            subjectId: selectedNode.value.id,
            questionCount: questionCount.value,
            masteryLevel: masteryLevel.value,
            studyTime: studyTime.value,
            answeredCount: answeredCount.value,
            correctCount: correctCount.value,
            accuracy: accuracy.value,
            questionsData: questionsData.value,
            timestamp: new Date().toISOString()
        }

        const res = await saveTopicDrillProgressApi(progressData)
        if (res.code === 200) {
            ElMessage.success('答题进度已保存')
        } else {
            ElMessage.error('保存失败: ' + res.msg)
        }
    } catch (error) {
        console.error('保存答题进度失败:', error)
        ElMessage.error('保存失败')
    }
}

// 加载答题进度
const loadSession = async () => {
    try {
        const userStr = localStorage.getItem('user') || '{}'
        const userInfo = JSON.parse(userStr)

        const res = await loadTopicDrillProgressApi(
            userInfo.id,
            selectedNode.value.id
        )

        if (res.code === 200 && res.data) {
            const progressData = res.data
            questionCount.value = progressData.questionCount || 0
            masteryLevel.value = progressData.masteryLevel || 0
            studyTime.value = progressData.studyTime || 0
            answeredCount.value = progressData.answeredCount || 0
            correctCount.value = progressData.correctCount || 0
            accuracy.value = progressData.accuracy || 0
            questionsData.value = progressData.questionsData || []
            ElMessage.success('答题进度已加载')
        } else {
            ElMessage.info('暂无答题进度')
        }
    } catch (error) {
        console.error('加载答题进度失败:', error)
        ElMessage.error('加载失败')
    }
}

// 共享草稿功能方法
const initDraftCanvas = () => {
    const canvas = draftCanvas.value
    if (canvas) {
        const rect = canvas.getBoundingClientRect()
        canvas.width = rect.width
        canvas.height = rect.height
        const ctx = canvas.getContext('2d')
        ctx.lineWidth = draftLineWidth.value
        ctx.lineCap = 'round'
        ctx.lineJoin = 'round'
        ctx.strokeStyle = draftColor.value
    }
}

const changeDraftColor = (color) => {
    draftColor.value = color
    const canvas = draftCanvas.value
    if (canvas) {
        const ctx = canvas.getContext('2d')
        ctx.strokeStyle = color
    }
}

const changeDraftLineWidth = () => {
    const canvas = draftCanvas.value
    if (canvas) {
        const ctx = canvas.getContext('2d')
        ctx.lineWidth = draftLineWidth.value
    }
}

const startDraftDrawing = (e) => {
    isDraftDrawing.value = true
    const rect = draftCanvas.value.getBoundingClientRect()
    const x = e.clientX - rect.left
    const y = e.clientY - rect.top
    lastDraftX.value = x
    lastDraftY.value = y
}

const drawDraft = (e) => {
    if (!isDraftDrawing.value) return

    const canvas = draftCanvas.value
    const rect = canvas.getBoundingClientRect()
    const x = e.clientX - rect.left
    const y = e.clientY - rect.top

    const ctx = canvas.getContext('2d')
    ctx.beginPath()
    ctx.moveTo(lastDraftX.value, lastDraftY.value)
    ctx.lineTo(x, y)
    ctx.stroke()

    lastDraftX.value = x
    lastDraftY.value = y
}

const stopDraftDrawing = () => {
    isDraftDrawing.value = false
}

const handleDraftTouchStart = (e) => {
    e.preventDefault()
    const touch = e.touches[0]
    const mouseEvent = new MouseEvent('mousedown', {
        clientX: touch.clientX,
        clientY: touch.clientY
    })
    draftCanvas.value.dispatchEvent(mouseEvent)
}

const handleDraftTouchMove = (e) => {
    e.preventDefault()
    const touch = e.touches[0]
    const mouseEvent = new MouseEvent('mousemove', {
        clientX: touch.clientX,
        clientY: touch.clientY
    })
    draftCanvas.value.dispatchEvent(mouseEvent)
}

const clearDraft = () => {
    const canvas = draftCanvas.value
    if (canvas) {
        const ctx = canvas.getContext('2d')
        ctx.clearRect(0, 0, canvas.width, canvas.height)
    }
}

const saveDraft = () => {
    const canvas = draftCanvas.value
    if (canvas) {
        const draftData = canvas.toDataURL()
        const key = `draft_${selectedNode.value?.id}`
        localStorage.setItem(key, draftData)
        ElMessage.success('草稿已保存')
    }
}

const loadDraft = () => {
    const key = `draft_${selectedNode.value?.id}`
    const draftData = localStorage.getItem(key)
    if (draftData) {
        const canvas = draftCanvas.value
        if (canvas) {
            const ctx = canvas.getContext('2d')
            const img = new Image()
            img.onload = () => {
                ctx.drawImage(img, 0, 0)
            }
            img.src = draftData
            ElMessage.success('草稿已加载')
        }
    } else {
        ElMessage.info('暂无草稿')
    }
}


// --- 逻辑处理 ---
// 递归处理节点的函数
const toggleTreeNodes = (nodes, status) => {
    nodes.forEach(node => {
        node.expanded = status
        if (node.children && node.children.length > 0) {
            toggleTreeNodes(node.children, status)
        }
    })
}

// 全部展开
const expandAllNodes = () => {
    toggleTreeNodes(moduleTree.value, true)
}

// 全部收起
const collapseAllNodes = () => {
    toggleTreeNodes(moduleTree.value, false)
}

onMounted(fetchExamSpecs)
</script>

<style scoped>
/* 容器与布局 */
.topic-drill-container {
    position: relative;
    height: 100vh;
    overflow: hidden;
    background: #f8fafc;
    padding: 24px;
    display: flex;
    overflow: hidden;
    transition: background 0.6s ease;
    box-sizing: border-box;
}

.header-glow {
    position: absolute;
    top: -100px;
    left: 0;
    width: 100%;
    height: 400px;
    opacity: 0.15;
    filter: blur(80px);
    pointer-events: none;
    transition: background 0.8s ease;
}

.content-wrapper {
    max-width: 1400px;
    width: 100%;
    height: 100%;
    min-height: 0;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    gap: 24px;
}

/* 外层居中容器 */
.navbar-container {
    display: flex;
    justify-content: center;
    width: 100%;
    flex-shrink: 0;
}

/* 胶囊导航 */
.subject-navbar {
    position: relative;
    display: flex;
    gap: 8px;
    background: rgba(255, 255, 255, 0.85);
    padding: 6px;
    border-radius: 100px;
    backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 0.5);
    box-shadow:
        0 10px 25px -5px rgba(0, 0, 0, 0.05),
        0 8px 10px -6px rgba(0, 0, 0, 0.05);
    z-index: 10;
}

/* 滑块动画效果 */
.nav-slider {
    position: absolute;
    top: 6px;
    height: calc(100% - 12px);
    border-radius: 100px;
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    opacity: 0;
    z-index: 0;
}

.subject-pill {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    padding: 0 36px;
    border-radius: 100px;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    color: #64748b;
    font-weight: 500;
    z-index: 1;
    height: 40px;
    font-size: 1rem;
}

.subject-pill .pill-label {
    line-height: 1;
}

.subject-pill.active {
    color: white;
    font-weight: 600;
}

.main-layout {
    display: grid;
    grid-template-columns: 340px 1fr;
    gap: 24px;
    flex: 1;
    min-height: 0;
}

/* 侧边栏：知识图谱条目 */
.kb-sidebar {
    background: white;
    border-radius: 24px;
    border: 1px solid #e2e8f0;
    display: flex;
    flex-direction: column;
    transition: border-color 0.6s ease;
    height: 100%;
    overflow: hidden;
    min-height: 0;
}

.sidebar-header {
    padding: 20px 24px;
    border-bottom: 1px solid #f1f5f9;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.sidebar-header h3 {
    font-size: 1.1rem;
    color: #1e293b;
    margin: 0;
}

.text-btn {
    background: none;
    border: none;
    color: #3b82f6;
    font-size: 0.85rem;
    cursor: pointer;
    padding: 4px 8px;
}

.tree-viewport {
    flex: 1;
    overflow-y: auto;
    padding: 16px;
}
/* 针对左侧知识图谱和右侧内容面板设置滚动条 */
.tree-viewport::-webkit-scrollbar,
.content-panel::-webkit-scrollbar {
    width: 6px;
    /* 纵向滚动条宽度 */
    height: 6px;
    /* 横向滚动条高度 */
}

/* 滚动条轨道 */
.tree-viewport::-webkit-scrollbar-track,
.content-panel::-webkit-scrollbar-track {
    background: transparent;
}

/* 滚动条滑块 */
.tree-viewport::-webkit-scrollbar-thumb,
.content-panel::-webkit-scrollbar-thumb {
    background-color: rgba(0, 0, 0, 0.1);
    border-radius: 10px;
    transition: background-color 0.3s;
}

/* 悬浮时滑块颜色 - 这里建议配合你的主题色 */
.tree-viewport::-webkit-scrollbar-thumb:hover,
.content-panel::-webkit-scrollbar-thumb:hover {
    background-color: rgba(37, 99, 235, 0.4);
}

/* 针对 Firefox 的兼容性写法 (可选) */
.tree-viewport,
.content-panel {
    scrollbar-width: thin;
    scrollbar-color: rgba(0, 0, 0, 0.1) transparent;
}

/* 知识树条目美化 */
:deep(.node-wrapper) {
    margin-bottom: 2px;
}

:deep(.node-item) {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 12px;
    border-radius: 10px;
    cursor: pointer;
    transition: all 0.2s;
    color: #475569;
    font-size: 0.95rem;
}

:deep(.node-item:hover) {
    background: #f1f5f9;
}

:deep(.node-item.is-active) {
    background: #eff6ff;
    color: #2563eb;
    font-weight: 600;
}

:deep(.arrow) {
    font-size: 10px;
    transition: transform 0.2s;
    width: 14px;
}

:deep(.arrow.down) {
    transform: rotate(90deg);
}

:deep(.dot) {
    width: 4px;
    height: 4px;
    background: #cbd5e1;
    border-radius: 50%;
    margin: 0 5px;
}

:deep(.children-container) {
    padding-left: 20px;
    margin-top: 2px;
    border-left: 1px dashed #e2e8f0;
    margin-left: 18px;
}

/* 右侧详情面板 */
.content-panel {
    background: white;
    border-radius: 24px;
    border: 1px solid #e2e8f0;
    overflow-y: auto;
    height: 100%;
    min-height: 0;
    display: flex;
    flex-direction: column;
    transition: border-color 0.6s ease;
}

.detail-card {
    padding: 40px;
}

.detail-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 10px;
}

.type-tag {
    font-size: 0.75rem;
    padding: 4px 12px;
    border-radius: 6px;
    color: white;
    margin-bottom: 12px;
    display: inline-block;
}

.quick-stats {
    display: flex;
    gap: 32px;
}

.stat-item {
    display: flex;
    flex-direction: column;
    gap: 6px;
}

.stat-item .label {
    font-size: 0.85rem;
    color: #94a3b8;
}

.stat-item .value {
    font-weight: 600;
    color: #1e293b;
}

.mini-progress-bar {
    width: 100px;
    height: 6px;
    background: #f1f5f9;
    border-radius: 10px;
    overflow: hidden;
}

.mini-progress-bar .fill {
    height: 100%;
    transition: width 0.6s ease;
}

/* 信息板块 */
.info-grid {
    display: grid;
    gap: 32px;
}

.info-block h3 {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 20px;
    font-size: 1.2rem;
}

.step-item {
    display: flex;
    gap: 16px;
    padding: 20px;
    background: #f8fafc;
    border-radius: 16px;
    margin-bottom: 12px;
}

.step-num {
    flex-shrink: 0;
    width: 28px;
    height: 28px;
    background: white;
    border: 2px solid #e2e8f0;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: bold;
    font-size: 0.9rem;
    transition: all 0.3s ease;
}

.mistake-list {
    padding-left: 20px;
    color: #e11d48;
}

.mistake-list li {
    margin-bottom: 10px;
    line-height: 1.6;
}

/* 状态切换动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
    transition: all 0.3s;
}

.fade-slide-enter-from {
    opacity: 0;
    transform: translateX(20px);
}

.fade-slide-leave-to {
    opacity: 0;
    transform: translateX(-20px);
}

.empty-state {
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #94a3b8;
}

.floating-icons {
    font-size: 3rem;
    margin-bottom: 20px;
    opacity: 0.3;
    animation: float 3s infinite ease-in-out;
}

@keyframes float {
    0%,
    100% {
        transform: translateY(0);
    }
    50% {
        transform: translateY(-10px);
    }
}

/* 大平面刷题模式样式 */
.drill-mode {
    height: 100%;
    display: flex;
    flex-direction: column;
    background: #fafbfc;
    border-radius: 24px;
    overflow: hidden;
}

.drill-header {
    background: white;
    border-bottom: 1px solid #e2e8f0;
    padding: 20px 30px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.drill-title h2 {
    margin: 0;
    font-size: 1.5rem;
    color: #1e293b;
}

.drill-subtitle {
    margin: 5px 0 0 0;
    color: #64748b;
    font-size: 0.9rem;
}

.drill-controls {
    display: flex;
    gap: 12px;
}

.control-btn {
    padding: 8px 16px;
    background: #f1f5f9;
    border: none;
    border-radius: 8px;
    color: #475569;
    cursor: pointer;
    font-size: 0.9rem;
    transition: all 0.3s ease;
}

.control-btn:hover {
    background: #e2e8f0;
    color: #1e293b;
}

.drill-canvas {
    flex: 1;
    position: relative;
    padding: 20px;
    overflow: auto;
    background: #fafbfc;
}

/* 共享草稿区域样式 */
.shared-draft-section {
    position: absolute;
    bottom: 20px;
    right: 20px;
    width: 400px;
    height: 300px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    z-index: 100;
}

.draft-toolbar {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 10px 16px;
    background: #f8f9fa;
    border-bottom: 1px solid #e2e8f0;
    flex-wrap: wrap;
}

.tool-group {
    display: flex;
    align-items: center;
    gap: 8px;
}

.tool-group label {
    font-size: 12px;
    color: #666;
    font-weight: 500;
}

.color-picker {
    display: flex;
    gap: 4px;
}

.color-btn {
    width: 24px;
    height: 24px;
    border: none;
    border-radius: 50%;
    cursor: pointer;
    transition: transform 0.2s;
    border: 2px solid transparent;
}

.color-btn:hover {
    transform: scale(1.2);
}

.color-btn.active {
    border-color: #333;
}

.tool-group select {
    padding: 4px 8px;
    border: 1px solid #ddd;
    border-radius: 6px;
    font-size: 12px;
    background: white;
    cursor: pointer;
}

.tool-group button {
    padding: 4px 8px;
    border: 1px solid #ddd;
    border-radius: 6px;
    font-size: 12px;
    background: white;
    cursor: pointer;
    transition: background 0.2s;
}

.tool-group button:hover {
    background: #f0f0f0;
}

.draft-canvas {
    width: 100%;
    height: calc(100% - 50px);
    cursor: crosshair;
    background: white;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .shared-draft-section {
        width: 300px;
        height: 250px;
        bottom: 10px;
        right: 10px;
    }

    .drill-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 16px;
    }

    .drill-controls {
        width: 100%;
        justify-content: space-between;
    }

    .drill-entry-section .entry-card {
        padding: 20px;
    }

    .entry-stats {
        gap: 20px;
    }

    .entry-stats .stat-value {
        font-size: 1.2rem;
    }

    .footer-stats {
        gap: 20px;
    }

    .stat-value {
        font-size: 1rem;
    }
}

.drill-footer {
    background: white;
    border-top: 1px solid #e2e8f0;
    padding: 16px 30px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.footer-stats {
    display: flex;
    gap: 30px;
}

.footer-stat {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.stat-value {
    font-size: 1.2rem;
    font-weight: 600;
    color: #1e293b;
}

.stat-label {
    font-size: 0.8rem;
    color: #64748b;
    margin-top: 4px;
}

/* 大平面入口样式 */
.drill-entry-section {
    margin-top: 20px;
    padding-top: 20px;
}

.entry-card {
    background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
    border-radius: 20px;
    padding: 30px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.entry-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.entry-icon {
    font-size: 3rem;
    margin-bottom: 16px;
}

.entry-card h3 {
    margin: 0 0 12px 0;
    font-size: 1.5rem;
    color: #1e293b;
}

.entry-card p {
    margin: 0 0 20px 0;
    color: #64748b;
    line-height: 1.6;
}

.entry-stats {
    display: flex;
    justify-content: center;
    gap: 30px;
    margin-bottom: 20px;
}

.entry-stats .stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.entry-stats .stat-value {
    font-size: 1.5rem;
    font-weight: 600;
    color: #2563eb;
}

.entry-stats .stat-label {
    font-size: 0.9rem;
    color: #64748b;
    margin-top: 4px;
}

.entry-button {
    padding: 12px 30px;
    background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
    color: white;
    border: none;
    border-radius: 12px;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
}

.entry-button:hover {
    transform: scale(1.05);
    box-shadow: 0 4px 15px rgba(59, 130, 246, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
    .drill-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 16px;
    }

    .drill-controls {
        width: 100%;
        justify-content: space-between;
    }

    .drill-entry-section .entry-card {
        padding: 20px;
    }

    .entry-stats {
        gap: 20px;
    }

    .entry-stats .stat-value {
        font-size: 1.2rem;
    }

    .footer-stats {
        gap: 20px;
    }

    .stat-value {
        font-size: 1rem;
    }
}

/* 视频预览样式 */
.video-section {
    margin-top: 5px;
    padding: 24px;
    background: #f8f9fa;
    border-radius: 16px;
}

.video-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
}

.video-header h3 {
    margin: 0;
    font-size: 1.2rem;
    color: #1e293b;
}

.video-source {
    font-size: 0.9rem;
    color: #64748b;
    background: #e2e8f0;
    padding: 4px 12px;
    border-radius: 20px;
}

.video-container {
    position: relative;
    width: 100%;
    padding-bottom: 56.25%; /* 16:9 Aspect Ratio */
    height: 0;
    overflow: hidden;
    border-radius: 12px;
    background: #000;
}

.video-container iframe {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    border: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .video-section {
        padding: 16px;
    }

    .video-header h3 {
        font-size: 1rem;
    }

    .video-source {
        font-size: 0.8rem;
        padding: 3px 10px;
    }
}
</style>

