<template>
    <div class="admin-container">
        <el-card shadow="never" class="table-card">
            <template #header>
                <div class="card-header">
                    <div class="text-header">
                        <span class="title-text">题目批量导出</span>
                        <div class="header-desc">选择条件导出题目为PDF格式</div>
                    </div>
                    <div class="header-btns">
                        <el-button type="success" :icon="Download" @click="handleExport" :loading="exportLoading">
                            导出PDF
                        </el-button>
                    </div>
                </div>
            </template>

            <div v-if="dataLoading" v-loading="true" class="loading-container"></div>

            <el-form v-else :model="exportForm" label-width="120px" label-position="left" class="export-form">
                <!-- 导出方式选择 -->
                <el-form-item label="导出方式">
                    <el-radio-group v-model="exportForm.exportType" @change="handleExportTypeChange">
                        <el-radio-button value="book">按习题册导出</el-radio-button>
                        <el-radio-button value="paper">按试卷导出</el-radio-button>
                        <el-radio-button value="custom">自定义题目</el-radio-button>
                    </el-radio-group>
                </el-form-item>

                <!-- 习题册选择 -->
                <el-form-item v-if="exportForm.exportType === 'book'" label="选择习题册">
                    <el-select
                        v-model="exportForm.bookId"
                        placeholder="请选择习题册"
                        style="width: 100%"
                        filterable
                        @change="handlePreview"
                    >
                        <el-option
                            v-for="book in books"
                            :key="book.id"
                            :label="book.name"
                            :value="book.id"
                        />
                    </el-select>
                </el-form-item>

                <!-- 试卷选择 -->
                <el-form-item v-if="exportForm.exportType === 'paper'" label="选择试卷">
                    <el-select
                        v-model="exportForm.paperId"
                        placeholder="请选择试卷"
                        style="width: 100%"
                        filterable
                        @change="handlePreview"
                    >
                        <el-option
                            v-for="paper in papers"
                            :key="paper.id"
                            :label="paper.title"
                            :value="paper.id"
                        />
                    </el-select>
                </el-form-item>

                <!-- 自定义题目 -->
                <el-form-item v-if="exportForm.exportType === 'custom'" label="选择科目">
                    <el-cascader
                        v-model="exportForm.subjectId"
                        :options="subjectTree"
                        :props="{ value: 'id', label: 'name', children: 'children', checkStrictly: true }"
                        placeholder="请选择科目（可选）"
                        clearable
                        filterable
                        style="width: 100%"
                        @change="handleSubjectChange"
                    />
                </el-form-item>

                <el-form-item v-if="exportForm.exportType === 'custom' && exportForm.subjectId" label="选择题目">
                    <el-select
                        v-model="exportForm.questionIds"
                        placeholder="请选择要导出的题目"
                        style="width: 100%"
                        multiple
                        filterable
                        collapse-tags
                        collapse-tags-tooltip
                        @change="handlePreview"
                    >
                        <el-option
                            v-for="question in subjectQuestions"
                            :key="question.id"
                            :value="question.id"
                        >
                            <template #default>
                                <span>[{{ question.id }}]</span>
                                <span v-html="renderLatex(question.content?.substring(0, 50))"></span>
                                <span>...</span>
                            </template>
                        </el-option>
                    </el-select>
                    <div class="selected-count">
                        已选择 {{ exportForm.questionIds?.length || 0 }} 道题目
                    </div>
                </el-form-item>

                <!-- PDF模式 -->
                <el-form-item label="PDF模式">
                    <el-radio-group v-model="exportForm.mode">
                        <el-radio-button :value="1">
                            <el-icon><Hide /></el-icon>
                            仅题目（刷题版）
                        </el-radio-button>
                        <el-radio-button :value="2">
                            <el-icon><View /></el-icon>
                            题目+答案解析
                        </el-radio-button>
                    </el-radio-group>
                </el-form-item>

                <!-- 附加选项 -->
                <el-form-item label="附加信息">
                    <el-checkbox-group v-model="additionalOptions">
                        <el-checkbox value="difficulty">显示难度</el-checkbox>
                        <el-checkbox value="tags">显示标签</el-checkbox>
                        <el-checkbox value="source">显示来源</el-checkbox>
                    </el-checkbox-group>
                </el-form-item>
            </el-form>
        </el-card>

        <!-- 预览区域 -->
        <el-card v-if="previewQuestions.length > 0" shadow="never" class="preview-card">
            <template #header>
                <div class="preview-header">
                    <span class="preview-title">预览题目（共 {{ previewQuestions.length }} 道）</span>
                    <el-button text type="primary" @click="toggleExpandAll">
                        {{ allExpanded ? '收起全部' : '展开全部' }}
                    </el-button>
                </div>
            </template>

            <div class="preview-container">
                <el-collapse v-model="activeQuestions" accordion>
                    <el-collapse-item v-for="(question, index) in currentPageQuestions" :key="question.id" :name="index">
                            <template #title>
                                <div class="question-title">
                                    <span class="question-number">第 {{ (currentPage - 1) * pageSize + index + 1 }} 题</span>
                                    <el-tag :type="getTypeTagColor(question.type)" size="small" :style="getTypeTagStyle(question.type)">
                                        {{ getTypeName(question.type) }}
                                    </el-tag>
                                    <span class="question-brief" v-html="renderLatex(question.content?.substring(0, 60)) + '...'"></span>
                                </div>
                            </template>

                        <div class="question-detail">
                            <!-- 题干 -->
                            <div class="detail-section">
                                <div class="detail-label">
                                    <el-icon><Document /></el-icon>
                                    题干
                                </div>
                                <div class="detail-content" v-html="renderLatex(question.content)"></div>
                            </div>

                            <!-- 选项（仅选择题显示） -->
                            <div v-if="(question.type === 1 || question.type === 2) && question.options && question.options.length > 0" class="detail-section">
                                <div class="detail-label">
                                    <el-icon><List /></el-icon>
                                    选项
                                </div>
                                <div class="options-container">
                                    <div
                                        v-for="(option, optIdx) in question.options"
                                        :key="optIdx"
                                        class="option-item"
                                        :data-index="String.fromCharCode(65 + optIdx)"
                                    >
                                        <span class="option-text" v-html="renderLatex(getOptionText(option))"></span>
                                    </div>
                                </div>
                            </div>

                            <!-- 答案 -->
                            <div v-if="exportForm.mode === 2 && question.answer" class="detail-section">
                                <div class="detail-label answer-label">
                                    <el-icon><CircleCheck /></el-icon>
                                    答案
                                </div>
                                <div class="detail-content answer-content" v-html="renderLatex(question.answer)"></div>
                            </div>

                            <!-- 解析 -->
                            <div v-if="exportForm.mode === 2 && question.analysis" class="detail-section">
                                <div class="detail-label analysis-label">
                                    <el-icon><ChatLineSquare /></el-icon>
                                    解析
                                </div>
                                <div class="detail-content analysis-content" v-html="renderLatex(question.analysis)"></div>
                            </div>

                            <!-- 难度 -->
                            <div v-if="additionalOptions.includes('difficulty') && question.difficulty" class="detail-section inline">
                                <span class="inline-label">难度：</span>
                                <el-tag :type="getDifficultyTagColor(question.difficulty)" size="small">
                                    {{ getDifficultyName(question.difficulty) }}
                                </el-tag>
                            </div>

                            <!-- 标签 -->
                            <div v-if="additionalOptions.includes('tags') && question.tags && question.tags.length > 0" class="detail-section inline">
                                <span class="inline-label">标签：</span>
                                <el-tag
                                    v-for="(tag, tagIdx) in question.tags"
                                    :key="tagIdx"
                                    size="small"
                                    style="margin-right: 5px"
                                >
                                    {{ tag }}
                                </el-tag>
                            </div>

                            <!-- 来源 -->
                            <div v-if="additionalOptions.includes('source') && question.source" class="detail-section inline">
                                <span class="inline-label">来源：{{ question.source }}</span>
                            </div>
                        </div>
                    </el-collapse-item>
                </el-collapse>

                <!-- 分页 -->
                <el-pagination
                    v-if="previewQuestions.length > pageSize"
                    :current-page="currentPage"
                    :page-size="pageSize"
                    :total="previewQuestions.length"
                    layout="prev, pager, next"
                    @current-change="handlePageChange"
                    class="pagination"
                />
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Download, View, Hide, Document, List, CircleCheck, ChatLineSquare } from '@element-plus/icons-vue'
import katex from 'katex'
import { getSubjectTree, getAllBooks, getAllPapers, getQuestionsBySubject, previewExportQuestions, exportQuestionsToPdf } from '@/api/questionImportExport'

// 表单数据
const exportForm = reactive({
    exportType: 'book', // book, paper, custom
    bookId: null,
    paperId: null,
    subjectId: null,
    questionIds: [],
    mode: 1, // 1-仅题目, 2-题目+答案
    includeDifficulty: false,
    includeTags: false,
    includeSource: false
})

// 附加选项（用于checkbox-group双向绑定）
const additionalOptions = ref([])

// 数据
const subjectTree = ref([])
const books = ref([])
const papers = ref([])
const subjectQuestions = ref([])
const previewQuestions = ref([])
const previewLoading = ref(false)
const exportLoading = ref(false)
const dataLoading = ref(true)

// 预览相关
const activeQuestions = ref([])
const allExpanded = ref(false)
const currentPage = ref(1)
const pageSize = 5

const currentPageQuestions = computed(() => {
    const start = (currentPage.value - 1) * pageSize
    const end = start + pageSize
    return previewQuestions.value.slice(start, end)
})

// 监听附加选项变化
watch(additionalOptions, (newVal) => {
    exportForm.includeDifficulty = newVal.includes('difficulty')
    exportForm.includeTags = newVal.includes('tags')
    exportForm.includeSource = newVal.includes('source')
})

// LaTeX 渲染函数
const renderLatex = (content) => {
    // 处理空值或非字符串类型
    if (!content) return ''

    // 如果是对象或数组，转换为字符串
    let strContent = content
    if (typeof content === 'object') {
        strContent = JSON.stringify(content)
    } else if (typeof content !== 'string') {
        strContent = String(content)
    }

    // 确保在渲染前，先将 \\ 替换成 \，防止 KaTeX 错误解析转义字符
    let processedContent = strContent.replace(/\\\\/g, '\\')

    return processedContent.replace(/\$([^\$]+)\$/g, (match, tex) => {
        try {
            return katex.renderToString(tex, {
                throwOnError: false,
                displayMode: false
            })
        } catch (err) {
            return match
        }
    }).replace(/\$\$([^\$]+)\$\$/g, (match, tex) => { // 块级公式
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

// 初始化
onMounted(async () => {
    await loadData()
    previewLoading.value = false
})

// 加载数据
async function loadData() {
    dataLoading.value = true
    try {
        const [subjectsRes, booksRes, papersRes] = await Promise.allSettled([
            getSubjectTree(),
            getAllBooks(),
            getAllPapers()
        ])

        if (subjectsRes.status === 'fulfilled') {
            subjectTree.value = subjectsRes.value.data || []
        } else {
            console.error('加载科目树失败:', subjectsRes.reason)
        }

        if (booksRes.status === 'fulfilled') {
            books.value = booksRes.value.data || []
        } else {
            console.error('加载习题册失败:', booksRes.reason)
        }

        if (papersRes.status === 'fulfilled') {
            papers.value = papersRes.value.data || []
        } else {
            console.error('加载试卷失败:', papersRes.reason)
        }

        console.log('数据加载完成:', { subjects: subjectTree.value.length, books: books.value.length, papers: papers.value.length })
    } catch (error) {
        ElMessage.error('加载数据失败: ' + (error.message || '未知错误'))
        console.error('加载数据失败:', error)
    } finally {
        dataLoading.value = false
    }
}

// 导出类型切换
function handleExportTypeChange() {
    // 清空选择
    exportForm.bookId = null
    exportForm.paperId = null
    exportForm.subjectId = null
    exportForm.questionIds = []
    subjectQuestions.value = []
    previewQuestions.value = []
    activeQuestions.value = []
}

// 科目切换
async function handleSubjectChange() {
    if (!exportForm.subjectId) {
        subjectQuestions.value = []
        return
    }

    try {
        const subjectId = Array.isArray(exportForm.subjectId) ? exportForm.subjectId[exportForm.subjectId.length - 1] : exportForm.subjectId
        const res = await getQuestionsBySubject(subjectId)
        subjectQuestions.value = res.data || []
        exportForm.questionIds = []
        previewQuestions.value = []
    } catch (error) {
        ElMessage.error('加载题目失败')
        console.error(error)
    }
}

// 预览
async function handlePreview() {
    // 验证
    if (exportForm.exportType === 'book' && !exportForm.bookId) {
        ElMessage.warning('请选择习题册')
        return
    }
    if (exportForm.exportType === 'paper' && !exportForm.paperId) {
        ElMessage.warning('请选择试卷')
        return
    }
    if (exportForm.exportType === 'custom' && exportForm.questionIds.length === 0) {
        ElMessage.warning('请选择要导出的题目')
        return
    }

    previewLoading.value = true
    try {
        const data = buildExportData()
        const res = await previewExportQuestions(data)
        previewQuestions.value = res.data || []
        currentPage.value = 1
        activeQuestions.value = []
        ElMessage.success(`找到 ${previewQuestions.value.length} 道题目`)
    } catch (error) {
        ElMessage.error('预览失败')
        console.error(error)
    } finally {
        previewLoading.value = false
    }
}

// 导出
async function handleExport() {
    // 验证
    if (exportForm.exportType === 'book' && !exportForm.bookId) {
        ElMessage.warning('请选择习题册')
        return
    }
    if (exportForm.exportType === 'paper' && !exportForm.paperId) {
        ElMessage.warning('请选择试卷')
        return
    }
    if (exportForm.exportType === 'custom' && exportForm.questionIds.length === 0) {
        ElMessage.warning('请选择要导出的题目')
        return
    }

    exportLoading.value = true
    try {
        const data = buildExportData()
        const res = await exportQuestionsToPdf(data)

        if (!res || res.size === 0) {
            ElMessage.error('导出失败：PDF数据为空')
            return
        }

        // 下载PDF
        const blob = new Blob([res], { type: 'application/pdf' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `题目导出_${new Date().getTime()}.pdf`
        link.click()
        window.URL.revokeObjectURL(url)

        ElMessage.success('导出成功')
    } catch (error) {
        console.error('导出错误:', error)
        let msg = '导出失败'
        if (error.message) {
            msg = error.message
        }
        ElMessage.error(msg)
    } finally {
        exportLoading.value = false
    }
}

// 构建导出数据
function buildExportData() {
    const data = {
        mode: exportForm.mode,
        includeDifficulty: exportForm.includeDifficulty,
        includeTags: exportForm.includeTags,
        includeSource: exportForm.includeSource
    }

    if (exportForm.exportType === 'book') {
        data.bookId = exportForm.bookId
    } else if (exportForm.exportType === 'paper') {
        // paperId 可能是字符串（UUID）或数字，保持原样传递
        data.paperId = exportForm.paperId
    } else if (exportForm.exportType === 'custom') {
        data.questionIds = exportForm.questionIds
        if (exportForm.subjectId) {
            const subjectId = Array.isArray(exportForm.subjectId) ? exportForm.subjectId[exportForm.subjectId.length - 1] : exportForm.subjectId
            data.subjectId = subjectId
        }
    }

    return data
}

// 展开/收起全部
function toggleExpandAll() {
    allExpanded.value = !allExpanded.value
    if (allExpanded.value) {
        activeQuestions.value = currentPageQuestions.value.map((_, i) => i)
    } else {
        activeQuestions.value = []
    }
}

// 分页切换
function handlePageChange(page) {
    activeQuestions.value = []
}

// 获取题目类型名称
function getTypeName(type) {
    const typeMap = { 1: '单选题', 2: '多选题', 3: '填空题', 4: '简答题' }
    return typeMap[type] || '未知'
}

// 获取题目类型标签颜色
function getTypeTagColor(type) {
    const colorMap = { 1: 'success', 2: 'warning', 3: 'primary', 4: 'info' }
    return colorMap[type] || ''
}

// 获取难度名称
function getDifficultyName(difficulty) {
    const difficultyMap = { 1: '简单', 2: '中等', 3: '困难' }
    return difficultyMap[difficulty] || '未知'
}

// 获取难度标签颜色
function getDifficultyTagColor(difficulty) {
    const colorMap = { 1: 'success', 2: 'warning', 3: 'danger' }
    return colorMap[difficulty] || ''
}

// 获取题型标签自定义样式（用于简答题等需要自定义颜色的题型）
function getTypeTagStyle(type) {
    if (type === 4) {
        return { backgroundColor: '#722ed1', borderColor: '#722ed1', color: 'white' }
    }
    return {}
}

// 获取选项文本内容
function getOptionText(option) {
    // 如果是字符串，直接返回
    if (typeof option === 'string') {
        return option
    }

    // 如果是对象，提取 text 字段
    if (typeof option === 'object' && option !== null) {
        // 优先使用 text 字段
        if (option.text !== undefined) {
            // 如果 text 还是对象，继续提取
            let text = option.text
            while (typeof text === 'object' && text !== null) {
                text = text.text || text.content || ''
            }
            return text
        }
        // 其次使用 content 字段
        if (option.content !== undefined) {
            return option.content
        }
    }

    // 兜底返回空字符串
    return ''
}
</script>

<style scoped lang="scss">
.admin-container {
    min-height: calc(100vh - 84px);
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.table-card {
    border-radius: 12px;
    border: none;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05) !important;

    :deep(.el-card__header) {
        padding: 16px 20px;
        background: #fff;
        border-bottom: 1px solid #ebeef5;
    }

    :deep(.el-card__body) {
        padding: 20px;
    }
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.text-header {
    display: flex;
    flex-direction: column;
    gap: 4px;
}

.title-text {
    font-size: 18px;
    font-weight: 600;
    color: #1f2f3d;
    position: relative;
    padding-left: 12px;

    &::before {
        content: "";
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 18px;
        background: #409eff;
        border-radius: 2px;
    }
}

.header-desc {
    font-size: 13px;
    color: #909399;
}

.header-btns {
    display: flex;
    gap: 10px;
}

.loading-container {
    min-height: 200px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.export-form {
    background: #fcfcfd;
    padding: 20px;
    border-radius: 8px;
    border: 1px solid #ebeef5;
}

.selected-count {
    margin-top: 8px;
    font-size: 12px;
    color: #909399;
}

.preview-card {
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05) !important;
    border: none;

    :deep(.el-card__header) {
        padding: 16px 20px;
        background: #fff;
        border-bottom: 1px solid #ebeef5;
    }

    :deep(.el-card__body) {
        padding: 20px;
        background: #fcfcfd;
    }
}

.preview-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .preview-title {
        font-size: 16px;
        font-weight: 600;
        color: #1f2f3d;
        padding-left: 12px;
        position: relative;

        &::before {
            content: "";
            position: absolute;
            left: 0;
            top: 50%;
            transform: translateY(-50%);
            width: 4px;
            height: 18px;
            background: #67c23a;
            border-radius: 2px;
        }
    }
}

.preview-container {
    background: #f8f9fa;
    border-radius: 8px;
    padding: 20px;

    :deep(.el-collapse) {
        border: none;
        background: transparent;
    }

    :deep(.el-collapse-item) {
        margin-bottom: 12px;
        border-radius: 8px;
        overflow: hidden;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        transition: all 0.3s ease;
        background: white;

        &:hover {
            box-shadow: 0 4px 16px rgba(64, 158, 255, 0.15);
            transform: translateY(-2px);
        }

        &.is-active {
            box-shadow: 0 4px 20px rgba(64, 158, 255, 0.2);
        }
    }

    :deep(.el-collapse-item__header) {
        background: white;
        border: none;
        padding: 15px 20px;
        font-size: 14px;
        height: auto;
        line-height: 1.5;
        transition: all 0.3s ease;

        &.is-active {
            border-bottom: 1px solid #ebeef5;
        }
    }

    :deep(.el-collapse-item__wrap) {
        border: none;
        background: transparent;
    }

    :deep(.el-collapse-item__content) {
        padding: 0 20px 20px;
    }
}

.question-title {
    display: flex;
    align-items: center;
    gap: 12px;
    width: 100%;

    .question-number {
        font-weight: 700;
        font-size: 14px;
        color: #409eff;
        min-width: 80px;
    }

    .question-brief {
        color: #606266;
        font-size: 14px;
        flex: 1;

        :deep(.katex) {
            font-size: 1em;
        }

        :deep(.katex-display) {
            display: inline;
            margin: 0;
        }
    }
}

.question-detail {
    padding: 20px 0;
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.detail-section {
    &.inline {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 14px;
    }
}

.detail-label {
    display: flex;
    align-items: center;
    gap: 6px;
    font-weight: 600;
    color: #409eff;
    margin-bottom: 8px;
    font-size: 14px;

    &.answer-label {
        color: #67c23a;
    }

    &.analysis-label {
        color: #e6a23c;
    }
}

.detail-content {
    padding: 12px 16px;
    background: #f8f9fa;
    border-radius: 6px;
    color: #303133;
    line-height: 1.8;
    white-space: pre-wrap;
    word-break: break-word;

    :deep(.katex) {
        font-size: 1em;
    }

    :deep(.katex-display) {
        margin: 1em 0;
        overflow-x: auto;
        overflow-y: hidden;
    }

    &.answer-content {
        background: #f0f9ff;
        border-left: 3px solid #67c23a;
        color: #67c23a;
        font-weight: 600;
    }

    &.analysis-content {
        background: #fffbf0;
        border-left: 3px solid #e6a23c;
        font-style: italic;
    }
}

.options-container {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.option-item {
    padding: 10px 16px;
    background: white;
    border: 1px solid #e0e0e0;
    border-radius: 6px;
    position: relative;
    padding-left: 45px;
    transition: all 0.3s ease;

    &:hover {
        border-color: #409eff;
        background: #ecf5ff;
        transform: translateX(4px);
        box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
    }

    &::before {
        content: attr(data-index);
        position: absolute;
        left: 12px;
        top: 50%;
        transform: translateY(-50%);
        width: 24px;
        height: 24px;
        border-radius: 50%;
        background: #409eff;
        color: white;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: 600;
        font-size: 12px;
    }

    .option-text {
        color: #303133;
        line-height: 1.6;

        :deep(.katex) {
            font-size: 1em;
        }

        :deep(.katex-display) {
            margin: 0.5em 0;
            overflow-x: auto;
            overflow-y: hidden;
        }
    }
}

.inline-label {
    color: #606266;
    font-weight: 500;
}

.pagination {
    margin-top: 20px;
    display: flex;
    justify-content: center;

    :deep(.el-pagination) {
        .btn-prev,
        .btn-next,
        .el-pager li {
            background: white;
            border-radius: 6px;
            border: 1px solid #e0e0e0;

            &:hover {
                color: #409eff;
                border-color: #409eff;
            }

            &.is-active {
                background: #409eff;
                color: white;
                border-color: #409eff;
            }
        }
    }
}
</style>
