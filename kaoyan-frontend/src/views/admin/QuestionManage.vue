<template>
    <div class="admin-container">
        <el-card shadow="never" class="table-card">
            <template #header>
                <div class="card-header">
                    <span class="title-text">题目管理中心</span>
                    <div class="header-btns">
                        <el-button type="success" icon="Download" @click="exportToExcel">导出 CSV</el-button>
                        <el-button type="primary" icon="Plus" @click="handleAdd">新增题目</el-button>
                    </div>
                </div>
            </template>

            <el-form :inline="true" :model="searchForm" class="search-form">
                <el-form-item label="所属科目">
                    <el-select v-model="searchForm.subjectId" placeholder="请选择科目" clearable style="width: 200px"
                        @change="loadData">
                        <el-option label="考研政治" :value="1" />
                        <el-option label="考研英语一" :value="2" />
                        <el-option label="考研数学一" :value="3" />
                        <el-option label="计算机 408" :value="4" />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" icon="Search" @click="loadData">查询</el-button>
                </el-form-item>
            </el-form>

            <el-table :data="tableData" border stripe v-loading="loading" class="custom-table">
                <el-table-column prop="id" label="ID" width="70" align="center" />
                <el-table-column prop="content" label="题干内容" show-overflow-tooltip />
                <el-table-column label="科目" width="120" align="center">
                    <template #default="scope">
                        <el-tag size="small">{{ formatSubject(scope.row.subjectId) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="type" label="类型" width="80" align="center">
                    <template #default="scope">
                        <el-tag :type="scope.row.type === 1 ? '' : 'success'">
                            {{ scope.row.type === 1 ? '单选' : '多选' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="answer" label="正确答案" width="100" align="center" />
                <el-table-column label="操作" width="160" align="center">
                    <template #default="scope">
                        <el-button size="small" type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
                        <el-button size="small" type="danger" link @click="handleDelete(scope.row.id)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination-container">
                <el-pagination :current-page="pageNum" :page-size="pageSize" :page-sizes="[5, 10, 20]"
                    layout="total, sizes, prev, pager, next" :total="total" @size-change="loadData"
                    @current-change="loadData" />
            </div>
        </el-card>

        <el-dialog v-model="dialogVisible" :title="form.id ? '编辑题目' : '新增题目'" width="600px" destroy-on-close>
            <el-form :model="form" label-width="100px">
                <el-form-item label="所属科目">
                    <el-select v-model="form.subjectId" style="width: 100%">
                        <el-option label="考研政治" :value="1" />
                        <el-option label="考研英语一" :value="2" />
                        <el-option label="考研数学一" :value="3" />
                        <el-option label="计算机 408" :value="4" />
                    </el-select>
                </el-form-item>
                <el-form-item label="题目类型">
                    <el-radio-group v-model="form.type">
                        <el-radio :label="1">单选</el-radio>
                        <el-radio :label="2">多选</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="题干内容">
                    <el-input v-model="form.content" type="textarea" :rows="3" />
                </el-form-item>
                <el-form-item label="题目选项">
                    <div v-for="(opt, index) in dynamicOptions" :key="index"
                        style="display: flex; margin-bottom: 10px; width: 100%">
                        <el-input v-model="dynamicOptions[index]" placeholder="请输入选项内容">
                            <template #prepend>{{ String.fromCharCode(65 + index) }}</template>
                        </el-input>
                        <el-button icon="Delete" type="danger" plain style="margin-left: 10px"
                            @click="removeOption(index)" />
                    </div>
                    <el-button type="success" size="small" icon="Plus" @click="addOption">添加选项</el-button>
                </el-form-item>
                <el-form-item label="题目标签">
                    <el-select v-model="form.tags" multiple filterable allow-create default-first-option
                        placeholder="请输入或选择标签 (如: 操作系统, 真题)">
                        <el-option v-for="tag in tagOptions" :key="tag" :label="tag" :value="tag" />
                    </el-select>
                </el-form-item>
                <el-form-item label="正确答案">
                    <el-input v-model="form.answer" placeholder="A 或 ABC" />
                </el-form-item>
                <el-form-item label="解析">
                    <el-input v-model="form.analysis" type="textarea" :rows="2" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="saveQuestion">确定保存</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Delete, Download } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const searchForm = ref({ subjectId: null }) // 默认设为 null 以便触发 placeholder

const dynamicOptions = ref(['', '', '', ''])
const form = ref({ id: null, subjectId: 1, type: 1, content: '', options: [], answer: '', analysis: '', difficulty: 3, tags: [] })
const tagOptions = ref(['考研真题', '模拟题', '易错题', '重点', '操作系统', '数据结构', '计算机网络', '计算机组成原理'])

const loadData = async () => {
    loading.value = true
    try {
        const res = await request.get('/question/page', {
            params: { pageNum: pageNum.value, pageSize: pageSize.value, subjectId: searchForm.value.subjectId }
        })
        const data = res.data || res
        tableData.value = data.records || []
        total.value = data.total || 0
    } catch (e) {
        ElMessage.error("获取数据失败")
    } finally {
        loading.value = false
    }
}

const formatSubject = (id) => {
    const map = { 1: '政治', 2: '英语一', 3: '数学一', 4: '408专业课' }
    return map[id] || '未知'
}

const addOption = () => dynamicOptions.value.push('')
const removeOption = (index) => dynamicOptions.value.splice(index, 1)

const handleAdd = () => {
    form.value = { id: null, subjectId: 1, type: 1, content: '', options: [], answer: '', analysis: '', difficulty: 3, tags: [] }
    dynamicOptions.value = ['', '', '', '']
    dialogVisible.value = true
}

const handleEdit = (row) => {
    form.value = { ...row }
    // 确保 tags 是数组
    if (!Array.isArray(form.value.tags)) {
        form.value.tags = []
    }
    dynamicOptions.value = Array.isArray(row.options) ? [...row.options] : []
    dialogVisible.value = true
}

const saveQuestion = async () => {
    form.value.options = dynamicOptions.value
    const url = form.value.id ? '/question/update' : '/question/add'
    await request.post(url, form.value)
    ElMessage.success("保存成功")
    dialogVisible.value = false
    loadData()
}

const handleDelete = (id) => {
    ElMessageBox.confirm('确认删除该题目吗？').then(async () => {
        await request.delete(`/question/delete/${id}`)
        ElMessage.success("删除成功")
        loadData()
    })
}

const exportToExcel = () => {
    let csv = "\uFEFFID,科目,类型,题干,答案\n";
    tableData.value.forEach(row => {
        csv += `${row.id},${formatSubject(row.subjectId)},${row.type == 1 ? '单选' : '多选'},${row.content.replace(/,/g, '，')},${row.answer}\n`
    });
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = '题目导出.csv';
    a.click();
}

onMounted(loadData)
</script>

<style scoped>
.admin-container {
    padding: 16px;
    background-color: #f5f7f9;
}

.table-card {
    border-radius: 8px;
    border: none;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.title-text {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
}

.search-form {
    background: #fff;
    padding: 12px 0;
    margin-bottom: 8px;
}

/* 优化点 1：消除侧边栏/菜单右侧细线，并统一间距 */
:deep(.el-menu) {
    border-right: none !important;
}

/* 强制让容器占满，不留白边 */
.custom-table {
    width: 100% !important;
    margin-top: 10px;
}

.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}

/* 调整表单项间距 */
.el-form-item {
    margin-right: 20px !important;
}
</style>