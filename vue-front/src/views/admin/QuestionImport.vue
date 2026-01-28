<template>
    <div class="admin-container">
        <!-- 导入配置卡片 -->
        <el-card shadow="never" class="table-card">
            <template #header>
                <div class="card-header">
                    <div class="text-header">
                        <span class="title-text">题目批量导入</span>
                        <div class="header-desc">上传 JSON 文件批量导入题目到题库</div>
                    </div>
                    <div class="header-btns">
                        <el-button
                            type="primary"
                            size="large"
                            :loading="importing"
                            :disabled="!canImport"
                            @click="handleImport"
                        >
                            {{ importing ? '导入中...' : '开始导入' }}
                        </el-button>
                        <el-button size="large" @click="resetForm" :disabled="importing">
                            重置
                        </el-button>
                    </div>
                </div>
            </template>

            <div v-if="dataLoading" v-loading="true" class="loading-container"></div>

            <el-form v-else :model="importForm" label-width="120px" label-position="left" class="import-form" v-loading="importing">
                <!-- 习题册选择 -->
                <el-form-item label="习题册/试卷">
                    <el-radio-group v-model="importForm.bookMode" @change="handleBookModeChange">
                        <el-radio value="existing">选择现有习题册</el-radio>
                        <el-radio value="new">新建习题册/试卷</el-radio>
                        <el-radio value="skip">暂不选择</el-radio>
                    </el-radio-group>
                </el-form-item>

                <!-- 现有习题册 -->
                <el-form-item v-if="importForm.bookMode === 'existing'" label="选择习题册">
                    <el-select
                        v-model="importForm.bookId"
                        placeholder="请选择习题册"
                        style="width: 100%"
                        filterable
                    >
                        <el-option
                            v-for="book in allBooks"
                            :key="book.id"
                            :label="book.name"
                            :value="book.id"
                        />
                    </el-select>
                </el-form-item>

                <!-- 新建习题册 -->
                <el-form-item v-if="importForm.bookMode === 'new'" label="习题册/试卷名称">
                    <el-input
                        v-model="importForm.newBookName"
                        placeholder="请输入习题册或试卷名称"
                        clearable
                    />
                </el-form-item>

                <el-form-item v-if="importForm.bookMode === 'new'" label="类型">
                    <el-radio-group v-model="importForm.newBookType">
                        <el-radio :value="1">习题册</el-radio>
                        <el-radio :value="2">试卷</el-radio>
                    </el-radio-group>
                </el-form-item>

                <!-- 科目选择 -->
                <el-form-item label="选择科目" required>
                    <el-tree-select
                        v-model="importForm.subjectIds"
                        :data="subjectTree"
                        :props="{ label: 'name', value: 'id', children: 'children' }"
                        multiple
                        collapse-tags
                        collapse-tags-tooltip
                        clearable
                        placeholder="请选择科目（至少选择一个，可多选）"
                        check-strictly
                        filterable
                        style="width: 100%"
                        :render-after-expand="false"
                    >
                        <template #default="{ node, data }">
                            <div class="custom-tree-node">
                                <div class="node-label-wrapper">
                                    <el-icon v-if="data.children && data.children.length > 0" class="folder-icon">
                                        <Collection />
                                    </el-icon>
                                    <el-icon v-else class="leaf-icon">
                                        <Document />
                                    </el-icon>
                                    <span class="node-text">{{ node.label }}</span>
                                </div>
                            </div>
                        </template>
                    </el-tree-select>
                    <span class="form-item-tip">必填项：请至少选择一个科目用于题目分类</span>
                </el-form-item>

                <!-- 去重检查 -->
                <el-form-item label="去重检查">
                    <el-switch
                        v-model="importForm.checkDuplicate"
                        active-text="启用"
                        inactive-text="禁用"
                    />
                    <span class="form-item-tip">启用后将跳过与库中重复的题目</span>
                </el-form-item>

                <!-- 文件上传 -->
                <el-form-item label="上传JSON文件" required>
                    <el-upload
                        ref="uploadRef"
                        class="upload-area"
                        drag
                        :auto-upload="false"
                        :on-change="handleFileChange"
                        :limit="1"
                        accept=".json"
                        :file-list="fileList"
                        :disabled="importing"
                    >
                        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
                        <div class="el-upload__text">
                            将 JSON 文件拖到此处，或<em>点击上传</em>
                        </div>
                        <template #tip>
                            <div class="el-upload__tip">
                                只能上传 JSON 格式文件，且不超过 10MB
                            </div>
                        </template>
                    </el-upload>
                </el-form-item>

                <!-- JSON格式说明 -->
                <el-form-item label="JSON格式">
                    <el-alert type="info" :closable="false" show-icon>
                        <template #default>
                            <div class="format-info">
                                <p>JSON文件必须包含 <code>questions</code> 数组，每个题目包含以下字段：</p>
                                <ul>
                                    <li><code>type</code> - 题目类型（1=单选, 2=多选, 3=填空, 4=简答）</li>
                                    <li><code>content</code> - 题干内容</li>
                                    <li><code>options</code> - 选项数组（选择题必填）</li>
                                    <li><code>answer</code> - 正确答案</li>
                                    <li><code>analysis</code> - 解析（可选）</li>
                                    <li><code>tags</code> - 标签数组（可选）</li>
                                </ul>
                                <el-button type="primary" link @click="downloadTemplate">下载JSON模板</el-button>
                            </div>
                        </template>
                    </el-alert>
                </el-form-item>

                <!-- 导入结果 -->
                <el-form-item v-if="importResult" label="导入结果">
                    <el-alert
                        :type="importResult.success ? 'success' : 'error'"
                        :closable="false"
                        show-icon
                    >
                        <template #default>
                            <div class="result-info">
                                <p>{{ importResult.summary }}</p>
                                <div v-if="importResult.details" class="result-details">
                                    <el-descriptions :column="1" border size="small">
                                        <el-descriptions-item label="成功导入">
                                            {{ importResult.details.success }} 题
                                        </el-descriptions-item>
                                        <el-descriptions-item label="跳过重复">
                                            {{ importResult.details.duplicate }} 题
                                        </el-descriptions-item>
                                        <el-descriptions-item label="导入失败">
                                            {{ importResult.details.failed }} 题
                                        </el-descriptions-item>
                                    </el-descriptions>
                                </div>
                            </div>
                        </template>
                    </el-alert>
                </el-form-item>
            </el-form>
        </el-card>

        <!-- 题目预览卡片 -->
        <el-card v-if="parsedQuestions.length > 0" shadow="never" class="preview-card">
            <template #header>
                <div class="preview-header">
                    <span class="preview-title">预览题目（共 {{ parsedQuestions.length }} 道）</span>
                    <el-button text type="primary" @click="toggleExpandAll">
                        {{ allExpanded ? '收起全部' : '展开全部' }}
                    </el-button>
                </div>
            </template>

            <div class="preview-container">
                <el-collapse v-model="activeQuestions" accordion>
                    <el-collapse-item
                        v-for="(question, index) in currentPageQuestions"
                        :key="index"
                        :name="index"
                    >
                        <template #title>
                            <div class="question-title">
                                <span class="question-number">第 {{ getChineseNumber((previewCurrentPage - 1) * pageSize + index + 1) }} 题</span>
                                <el-tag :type="getTypeTagType(question.type)" size="small" :style="getTypeTagStyle(question.type)">
                                    {{ getTypeName(question.type) }}
                                </el-tag>
                                <span class="question-brief" v-html="renderLatex((question.content || '').substring(0, 60)) + '...'"></span>
                            </div>
                        </template>

                        <div class="question-detail">
                            <!-- 题干 -->
                            <div class="detail-section">
                                <div class="detail-label">
                                    <el-icon><Document /></el-icon>
                                    题干
                                </div>
                                <div class="detail-content" v-html="renderLatex(question.content || '(无)')"></div>
                            </div>

                            <!-- 选项（仅选择题显示） -->
                            <div v-if="question.options && question.options.length > 0" class="detail-section">
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
                                        <span class="option-text" v-html="formatOption(option, optIdx)"></span>
                                    </div>
                                </div>
                            </div>

                            <!-- 答案 -->
                            <div class="detail-section">
                                <div class="detail-label answer-label">
                                    <el-icon><CircleCheck /></el-icon>
                                    答案
                                </div>
                                <div class="detail-content answer-content" v-html="renderLatex(question.answer || '(未填写)')"></div>
                            </div>

                            <!-- 解析 -->
                            <div v-if="question.analysis" class="detail-section">
                                <div class="detail-label analysis-label">
                                    <el-icon><ChatLineSquare /></el-icon>
                                    解析
                                </div>
                                <div class="detail-content analysis-content" v-html="renderLatex(question.analysis)"></div>
                            </div>

                            <!-- 标签 -->
                            <div v-if="question.tags && question.tags.length > 0" class="detail-section inline">
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
                            <div v-if="question.source" class="detail-section inline">
                                <span class="inline-label">来源：{{ question.source }}</span>
                            </div>
                        </div>
                    </el-collapse-item>
                </el-collapse>

                <!-- 分页 -->
                <div v-if="parsedQuestions.length > pageSize" class="preview-pagination">
                    <el-pagination
                        :current-page="previewCurrentPage"
                        :page-size="pageSize"
                        :total="parsedQuestions.length"
                        layout="prev, pager, next"
                        small
                    />
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, List, CircleCheck, ChatLineSquare } from '@element-plus/icons-vue'
import { getAllBooks, getSubjectTree, importQuestions } from '@/api/questionImportExport'
import katex from 'katex'

// 数据
const dataLoading = ref(true)
const importing = ref(false)
const allBooks = ref([])
const subjectTree = ref([])
const fileList = ref([])
const jsonFile = ref(null)
const parsedQuestions = ref([])

const importForm = ref({
    bookMode: 'existing', // existing, new, skip
    bookId: null,
    newBookName: '',
    newBookType: 1, // 1=习题册, 2=试卷
    subjectIds: [],
    checkDuplicate: true
})

const importResult = ref(null)

// 预览相关
const activeQuestions = ref([])
const previewCurrentPage = ref(1)
const pageSize = ref(5)
const allExpanded = ref(false)

// 当前页题目
const currentPageQuestions = computed(() => {
    const start = (previewCurrentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    return parsedQuestions.value.slice(start, end)
})

// 是否可以导入
const canImport = computed(() => {
    // 必须选择科目
    if (!importForm.value.subjectIds || importForm.value.subjectIds.length === 0) {
        return false
    }

    // 习题册验证
    if (importForm.value.bookMode === 'existing') {
        if (!importForm.value.bookId) return false
    } else if (importForm.value.bookMode === 'new') {
        if (!importForm.value.newBookName || !importForm.value.newBookName.trim()) return false
    }

    // 必须上传文件
    return jsonFile.value !== null && parsedQuestions.value.length > 0
})

// 加载数据
const loadData = async () => {
    dataLoading.value = true
    try {
        const [booksRes, subjectsRes] = await Promise.all([
            getAllBooks(),
            getSubjectTree()
        ])

        if (booksRes.code === 200) {
            allBooks.value = booksRes.data || []
        }

        if (subjectsRes.code === 200) {
            subjectTree.value = subjectsRes.data || []
        }
    } catch (error) {
        ElMessage.error('加载数据失败')
    } finally {
        dataLoading.value = false
    }
}

// 习题册模式变化
const handleBookModeChange = (mode) => {
    importForm.value.bookId = null
    importForm.value.newBookName = ''
    importForm.value.newBookType = 1
}

// 处理文件变化
const handleFileChange = async (file) => {
    if (!file.raw.name.endsWith('.json')) {
        ElMessage.error('只能上传 JSON 格式文件')
        fileList.value = []
        return false
    }

    if (file.size > 10 * 1024 * 1024) {
        ElMessage.error('文件大小不能超过 10MB')
        fileList.value = []
        return false
    }

    jsonFile.value = file.raw
    fileList.value = [file]

    // 解析 JSON
    await parseJSONFile(file.raw)
}

// 解析 JSON 文件
const parseJSONFile = async (file) => {
    try {
        const text = await file.text()
        const json = JSON.parse(text)

        if (!json.questions || !Array.isArray(json.questions)) {
            throw new Error('JSON 格式错误：缺少 questions 数组')
        }

        parsedQuestions.value = json.questions
        importResult.value = null
        ElMessage.success(`成功解析 ${json.questions.length} 道题目`)
    } catch (error) {
        ElMessage.error('解析 JSON 失败：' + error.message)
        parsedQuestions.value = []
        jsonFile.value = null
        fileList.value = []
    }
}

// 执行导入
const handleImport = async () => {
    if (importing.value) return

    importing.value = true
    importResult.value = null

    try {
        const importData = {
            bookId: importForm.value.bookMode === 'existing' ? importForm.value.bookId : null,
            newBookName: importForm.value.bookMode === 'new' ? importForm.value.newBookName : null,
            newBookType: importForm.value.bookMode === 'new' ? importForm.value.newBookType : null,
            subjectIds: importForm.value.subjectIds,
            checkDuplicate: importForm.value.checkDuplicate,
            questions: parsedQuestions.value
        }

        const res = await importQuestions(importData)

        if (res.code === 200) {
            importResult.value = {
                success: true,
                summary: res.data,
                details: parseImportResult(res.data)
            }
            ElMessage.success('导入成功！')
        } else {
            importResult.value = {
                success: false,
                summary: res.message || '导入失败',
                details: null
            }
            ElMessage.error(res.message || '导入失败')
        }
    } catch (error) {
        importResult.value = {
            success: false,
            summary: '导入失败：' + error.message,
            details: null
        }
        ElMessage.error('导入失败：' + error.message)
    } finally {
        importing.value = false
    }
}

// 解析导入结果
const parseImportResult = (message) => {
    const successMatch = message.match(/成功[：:]\s*(\d+)/)
    const duplicateMatch = message.match(/跳过重复[：:]\s*(\d+)/)
    const failedMatch = message.match(/失败[：:]\s*(\d+)/)

    return {
        success: successMatch ? parseInt(successMatch[1]) : 0,
        duplicate: duplicateMatch ? parseInt(duplicateMatch[1]) : 0,
        failed: failedMatch ? parseInt(failedMatch[1]) : 0
    }
}

// 重置表单
const resetForm = () => {
    importForm.value = {
        bookMode: 'existing',
        bookId: null,
        newBookName: '',
        newBookType: 1,
        subjectIds: [],
        checkDuplicate: true
    }
    fileList.value = []
    jsonFile.value = null
    parsedQuestions.value = []
    importResult.value = null
}

// 获取题目类型名称
const getTypeName = (type) => {
    const types = {
        1: '单选题',
        2: '多选题',
        3: '填空题',
        4: '简答/问答'
    }
    return types[type] || '未知'
}

// 获取题目类型标签颜色
const getTypeTagType = (type) => {
    const types = {
        1: 'success',
        2: 'warning',
        3: 'primary',  // 填空题改为蓝色
        4: ''  // 简答题不使用预设颜色
    }
    return types[type] || ''
}

// 数字转中文数字
const getChineseNumber = (num) => {
    const chineseNums = ['零', '一', '二', '三', '四', '五', '六', '七', '八', '九', '十']
    if (num <= 10) {
        return chineseNums[num]
    } else if (num < 20) {
        return '十' + (num % 10 === 0 ? '' : chineseNums[num % 10])
    } else {
        const tens = Math.floor(num / 10)
        const units = num % 10
        return chineseNums[tens] + '十' + (units === 0 ? '' : chineseNums[units])
    }
}

// 获取题型标签自定义样式（用于简答题等需要自定义颜色的题型）
function getTypeTagStyle(type) {
    if (type === 4) {
        return { backgroundColor: '#722ed1', borderColor: '#722ed1', color: 'white' }
    }
    return {}
}

// 展开/收起全部
const toggleExpandAll = () => {
    allExpanded.value = !allExpanded.value
    if (allExpanded.value) {
        // 展开当前页所有题目
        activeQuestions.value = currentPageQuestions.value.map((_, index) => index)
    } else {
        activeQuestions.value = []
    }
}

// 渲染 LaTeX 公式
const renderLatex = (text) => {
    if (!text) return ''

    // 如果 text 是对象，尝试提取实际内容
    let content = text
    let depth = 0
    const MAX_DEPTH = 3 // 防止循环引用

    while (typeof content === 'object' && content !== null && depth < MAX_DEPTH) {
        // 如果有text属性，使用它
        if (content.text !== undefined) {
            content = content.text
        }
        // 如果有content属性，使用它
        else if (content.content !== undefined) {
            content = content.content
        }
        // 如果有value属性，使用它
        else if (content.value !== undefined) {
            content = content.value
        }
        else {
            // 都没有，转换为字符串
            content = JSON.stringify(content)
            break
        }
        depth++
    }

    // 确保是字符串类型
    if (typeof content !== 'string') {
        content = String(content)
    }

    // 匹配 $$...$$ (块级公式)
    const blockRegex = /\$\$([^$]+)\$\$/g
    // 匹配 $...$ (行内公式)
    const inlineRegex = /\$([^$]+)\$/g

    let result = content

    // 替换所有 LaTeX 公式
    const replacements = []

    // 先收集所有块级公式
    result = result.replace(blockRegex, (_, tex) => {
        const placeholder = `___LATEX_BLOCK_${replacements.length}___`
        replacements.push({
            type: 'block',
            tex: tex
        })
        return placeholder
    })

    // 再收集所有行内公式
    result = result.replace(inlineRegex, (_, tex) => {
        const placeholder = `___LATEX_INLINE_${replacements.length}___`
        replacements.push({
            type: 'inline',
            tex: tex
        })
        return placeholder
    })

    // 替换占位符为实际的 KaTeX HTML
    replacements.forEach((item, idx) => {
        const placeholder = item.type === 'block' ? `___LATEX_BLOCK_${idx}___` : `___LATEX_INLINE_${idx}___`
        try {
            // 确保在渲染前，先将 \\ 替换成 \，防止 KaTeX 错误解析转义字符
            const unescapedTex = item.tex.replace(/\\\\/g, '\\')
            const html = katex.renderToString(unescapedTex, {
                throwOnError: false,
                displayMode: item.type === 'block'
            })
            result = result.replace(placeholder, html)
        } catch (e) {
            console.error('KaTeX render error:', e)
            result = result.replace(placeholder, item.tex)
        }
    })

    return result
}

// 格式化选项显示
const formatOption = (option, index) => {
    // 获取文本内容
    let text = ''
    if (typeof option === 'string') {
        text = option
    } else if (typeof option === 'object' && option !== null) {
        // 提取 text 字段
        text = option.text || ''
        // 如果 text 还是对象，继续提取
        while (typeof text === 'object' && text !== null) {
            text = text.text || text.content || ''
        }
    }

    // 确保 text 是字符串
    text = String(text || '')

    // 只返回渲染后的文本，不添加前缀（前缀由 CSS ::before 显示）
    return renderLatex(text)
}

// 下载模板
const downloadTemplate = () => {
    const template = {
        "questions": [
            {
                "type": 1,
                "content": "设函数 $f(x) = x^3 - 3x + 1$，求 $f'(x)$",
                "options": [
                    {"label": "A", "text": "$3x^2 - 3$"},
                    {"label": "B", "text": "$3x^2 + 3$"},
                    {"label": "C", "text": "$x^2 - 3$"},
                    {"label": "D", "text": "$x^2 + 3$"}
                ],
                "answer": "A",
                "analysis": "根据求导公式 $(x^n)' = nx^{n-1}$，得到 $f'(x) = 3x^2 - 3$",
                "tags": ["导数", "基础求导"],
                "source": "高等数学例题",
                "difficulty": 2
            },
            {
                "type": 2,
                "content": "下列哪些函数在区间 $(0, +\\infty)$ 上单调递增？",
                "options": [
                    {"label": "A", "text": "$f(x) = x^2$"},
                    {"label": "B", "text": "$f(x) = e^x$"},
                    {"label": "C", "text": "$f(x) = \\ln(x)$"},
                    {"label": "D", "text": "$f(x) = 1/x$"}
                ],
                "answer": "ABC",
                "analysis": "$x^2$ 在 $x>0$ 时单调递增；$e^x$ 始终单调递增；$\\ln(x)$ 在定义域内单调递增；$1/x$ 在 $x>0$ 时单调递减",
                "tags": ["单调性", "函数性质"],
                "difficulty": 3
            },
            {
                "type": 3,
                "content": "若 $\\lim_{x \\to 0} \\frac{\\sin(2x)}{x} = $ _____",
                "answer": "2",
                "analysis": "利用重要极限 $\\lim_{x \\to 0} \\frac{\\sin(x)}{x} = 1$，原式 $= 2 \\times 1 = 2$",
                "tags": ["极限", "重要极限"],
                "difficulty": 2
            },
            {
                "type": 4,
                "content": "请论述马克思主义哲学中的质变与量变的辩证关系。",
                "answer": "量变是质变的必要准备，质变是量变的必然结果，质变引起新的量变",
                "analysis": "本题考查唯物辩证法的核心原理之一，体现了质量互变规律的基本内容",
                "tags": ["马克思主义哲学", "简答题", "辩证法"],
                "source": "政治理论真题",
                "difficulty": 4
            }
        ]
    }

    const blob = new Blob([JSON.stringify(template, null, 2)], { type: 'application/json' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'questions_template.json'
    a.click()
    URL.revokeObjectURL(url)
    ElMessage.success('模板已下载')
}

onMounted(() => {
    loadData()
})
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

.import-form {
    background: #fcfcfd;
    padding: 20px;
    border-radius: 8px;
    border: 1px solid #ebeef5;

    .form-item-tip {
        margin-left: 10px;
        font-size: 12px;
        color: #909399;
    }
}

.upload-area {
    width: 100%;
    margin: 10px 0;
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
    padding: 16px 0;
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

.preview-pagination {
    display: flex;
    justify-content: center;
    margin-top: 20px;

    :deep(.el-pagination) {
        .el-pager li {
            border-radius: 6px;
            margin: 0 2px;
            transition: all 0.2s ease;
            font-weight: 500;
            background: white;
            border: 1px solid #e0e0e0;

            &.is-active {
                background: #409eff;
                color: white;
                border-color: #409eff;
            }

            &:hover:not(.is-active) {
                color: #409eff;
                border-color: #409eff;
            }
        }

        .btn-prev,
        .btn-next {
            border-radius: 6px;
            padding: 0 12px;
            transition: all 0.2s ease;
            background: white;
            border: 1px solid #e0e0e0;

            &:hover {
                color: #409eff;
                border-color: #409eff;
            }
        }
    }
}

.format-info {
    p {
        margin-bottom: 10px;
        color: #606266;
    }

    ul {
        margin: 10px 0;
        padding-left: 20px;

        li {
            margin: 5px 0;
            color: #606266;

            code {
                background: #f5f7fa;
                padding: 2px 6px;
                border-radius: 3px;
                font-family: 'Courier New', monospace;
                color: #e6a23c;
            }
        }
    }
}

.result-info {
    p {
        margin-bottom: 15px;
        font-size: 15px;
        font-weight: 500;
    }

    .result-details {
        margin-top: 10px;
    }
}

.custom-tree-node {
    display: flex;
    align-items: center;
    width: 100%;

    .node-label-wrapper {
        display: flex;
        align-items: center;
        gap: 8px;

        .folder-icon,
        .leaf-icon {
            color: #909399;
        }

        .node-text {
            flex: 1;
        }
    }
}
</style>
