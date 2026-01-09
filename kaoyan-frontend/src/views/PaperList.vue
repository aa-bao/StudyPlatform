<template>
  <div class="paper-exam-container">
    <div class="page-header">
      <h1>套卷刷题</h1>
      <p>选择一份试卷开始模考</p>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-radio-group v-model="filterType" @change="loadPapers">
        <el-radio-button :label="null">全部</el-radio-button>
        <el-radio-button :label="0">真题</el-radio-button>
        <el-radio-button :label="1">模拟题</el-radio-button>
      </el-radio-group>

      <el-input
        v-model="searchKeyword"
        placeholder="搜索试卷名称"
        style="width: 300px;"
        clearable
        @clear="loadPapers"
        @keyup.enter="loadPapers"
      >
        <template #append>
          <el-button :icon="Search" @click="loadPapers" />
        </template>
      </el-input>
    </div>

    <!-- 试卷列表 -->
    <div v-loading="loading" class="paper-list">
      <el-row :gutter="20">
        <el-col
          v-for="paper in papers"
          :key="paper.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
        >
          <el-card class="paper-card" shadow="hover" @click="startExam(paper.id)">
            <div class="paper-header">
              <el-tag v-if="paper.paperType === 0" type="success" size="small">真题</el-tag>
              <el-tag v-else type="warning" size="small">模拟题</el-tag>
            </div>
            <h3 class="paper-title">{{ paper.title }}</h3>
            <div class="paper-info">
              <div class="info-item">
                <el-icon><Timer /></el-icon>
                <span>{{ paper.timeLimit || 180 }} 分钟</span>
              </div>
              <div class="info-item">
                <el-icon><Document /></el-icon>
                <span>{{ paper.totalScore || 150 }} 分</span>
              </div>
            </div>
            <div class="paper-footer">
              <el-button type="primary" size="small" @click.stop="startExam(paper.id)">
                开始考试
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-empty v-if="!loading && papers.length === 0" description="暂无试卷数据" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Timer, Document } from '@element-plus/icons-vue'
import { getPaperPage } from '@/api/paper'

const router = useRouter()

const loading = ref(false)
const papers = ref([])
const filterType = ref(null)
const searchKeyword = ref('')

// 加载试卷列表
const loadPapers = async () => {
  try {
    loading.value = true

    const params = {
      pageNum: 1,
      pageSize: 100  // 获取前100条数据
    }
    if (filterType.value !== null) {
      params.paperType = filterType.value
    }
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }

    const res = await getPaperPage(params)
    if (res.code === 200) {
      papers.value = res.data.records || []
    }
  } catch (error) {
    console.error('加载试卷列表失败:', error)
    ElMessage.error('加载试卷列表失败')
  } finally {
    loading.value = false
  }
}

// 开始考试
const startExam = (paperId) => {
  // 获取当前用户信息
  const userStr = localStorage.getItem('user')
  if (!userStr) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    const user = JSON.parse(userStr)
    // 跳转到沉浸式考试页面，传递试卷ID和用户ID
    router.push({
      path: '/user/mock-exam',
      query: {
        paperId: paperId,
        userId: user.id
      }
    })
  } catch (error) {
    console.error('解析用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

onMounted(() => {
  loadPapers()
})
</script>

<style scoped>
.paper-exam-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
  text-align: center;
}

.page-header h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.page-header p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.paper-list {
  min-height: 400px;
}

.paper-card {
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 20px;
}

.paper-card:hover {
  transform: translateY(-4px);
}

.paper-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.paper-title {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 48px;
}

.paper-info {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #606266;
}

.paper-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
