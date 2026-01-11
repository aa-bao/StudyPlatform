<template>
    <div class="layout-wrapper">
        <el-container class="full-height">
            <!-- 侧边栏 -->
            <el-aside v-if="!route.meta.hideLayout" 
            :width="isCollapse ? '64px' : '240px'" class="aside-menu">
                <div class="logo-box">
                    <div class="sidebar-toggle" @click="isCollapse = !isCollapse">
                        <el-icon>
                            <Expand v-if="isCollapse" />
                            <Fold v-else />
                        </el-icon>
                    </div>
                    <transition name="fade">
                        <span v-show="!isCollapse" class="logo-text">考研全流程平台</span>
                    </transition>
                </div>

                <el-menu :default-active="activeMenu" router :collapse="isCollapse" class="el-menu-vertical custom-menu"
                    text-color="#606266" active-text-color="#409EFF">

                    <el-menu-item index="/user/home">
                        <el-icon>
                            <img :src="homeIcon" class="menu-icon-svg" />
                        </el-icon>
                        <template #title>主页</template>
                    </el-menu-item>

                    <el-menu-item index="/user/dashboard">
                        <el-icon>
                            <img :src="dashboardIcon" class="menu-icon-svg" />
                        </el-icon>
                        <template #title>备考看板</template>
                    </el-menu-item>

                    <el-menu-item index="/user/subject">
                        <el-icon>
                            <img :src="singlePracticeIcon" class="menu-icon-svg" />
                        </el-icon>
                        <template #title>开始学习</template>
                    </el-menu-item>

                    <el-menu-item index="/user/topic-drill">
                        <el-icon>
                            <img :src="topicDrillIcon" class="menu-icon-svg" />
                        </el-icon>
                        <template #title>知识体系树</template>
                    </el-menu-item>

                    <el-menu-item index="/user/paper-list">
                        <el-icon>
                            <img :src="mockExamIcon" class="menu-icon-svg" />
                        </el-icon>
                        <template #title>套卷模考</template>
                    </el-menu-item>

                    <el-menu-item index="/user/correction-notebook">
                        <el-icon>
                            <img :src="correctionNotebookIcon" class="menu-icon-svg" />
                        </el-icon>
                        <template #title>错题本</template>
                    </el-menu-item>

                    <div class="menu-divider" v-if="userRole === 'admin'"></div>

                    <el-menu-item v-if="userRole === 'admin'" index="/admin/home" class="admin-entry">
                        <el-icon>
                            <Tools />
                        </el-icon>
                        <template #title>进入管理后台</template>
                    </el-menu-item>
                </el-menu>
            </el-aside>

            <el-container direction="vertical" class="main-container">
                <el-header class="header-bar" height="64px" v-if="!route.meta.hideLayout">
                    <div class="header-left">
                        <span class="breadcrumb-text">不要埋没你的梦想</span>
                    </div>

                    <div class="header-right">
                        <el-dropdown trigger="click">
                            <div class="user-info">
                                <el-avatar :size="36" :src="userAvatar" />
                                <span class="user-name">{{ userName }}</span>
                                <el-icon class="el-icon--right">
                                    <ArrowDown />
                                </el-icon>
                            </div>
                            <template #dropdown>
                                <el-dropdown-menu class="user-dropdown">
                                    <el-dropdown-item @click="router.push('/user/profile')" icon="UserFilled">
                                        个人主页
                                    </el-dropdown-item>
                                    <el-dropdown-item v-if="userRole === 'ADMIN'" @click="router.push('/admin/home')"
                                        icon="Management">
                                        进入后台管理系统
                                    </el-dropdown-item>
                                    <el-dropdown-item divided @click="handleLogout" icon="SwitchButton"
                                        style="color: #F56C6C">
                                        退出登录
                                    </el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </div>
                </el-header>

              <el-main class="content-main" :class="{ 'no-padding': route.meta.hideLayout, 'from-home-enter': isTransitioningFromHome }">
                    <router-view v-slot="{ Component }">
                        <transition name="fade-transform" mode="out-in">
                            <component :is="Component" />
                        </transition>
                    </router-view>
                </el-main>
            </el-container>
        </el-container>

        <!-- 未完成考试强制弹窗 -->
        <el-dialog
            v-model="showIncompleteExamDialog"
            title="⚠️ 检测到未完成的考试"
            width="500px"
            :close-on-click-modal="false"
            :close-on-press-escape="false"
            :show-close="false"
            class="incomplete-exam-dialog"
        >
            <div class="dialog-content">
                <div class="exam-info-card">
                    <div class="exam-icon">📝</div>
                    <div class="exam-details">
                        <h3 class="exam-title">{{ incompleteExamInfo.paperTitle }}</h3>
                        <p class="exam-time">{{ incompleteExamInfo.startTime }}</p>
                    </div>
                </div>
                <div class="warning-text">
                    <p>
                        您有一场考试尚未完成，为了更好的评估您的各项能力，请
                        <strong class="highlight">认真作答</strong>完成考试。
                    </p>
                </div>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="continueExam" class="continue-btn">回到考试</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user'
import { getIncompleteSessions } from '@/api/examSession'
import { getPaperDetail } from '@/api/paper'
import { useTransitionStore } from '@/stores/transition'

// 导入自定义图标
import dashboardIcon from '@/assets/icons/dashboard.svg?url'
import singlePracticeIcon from '@/assets/icons/single-practice.svg?url'
import topicDrillIcon from '@/assets/icons/tree.svg?url'
import mockExamIcon from '@/assets/icons/mock-exam.svg?url'
import correctionNotebookIcon from '@/assets/icons/correction-notebook.svg?url'
import homeIcon from '@/assets/icons/home.svg?url'

const router = useRouter()
const route = useRoute();
const userStore = useUserStore() // 使用 store
const transitionStore = useTransitionStore()
const isCollapse = ref(true)
const activeMenu = computed(() => router.path)

// 页面进入动画状态 - 从 store 获取
const isTransitioningFromHome = computed(() => transitionStore.isEnteringLayout)

// 监听 store 中的 userInfo 变化
const userName = computed(() => {
    const user = userStore.userInfo
    if (user && (user.nickname || user.username)) {
        return user.nickname || user.username
    }
    return '研友'
})

const userAvatar = computed(() => {
    return userStore.userInfo.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
})

const userRole = computed(() => {
    return userStore.userInfo.role || localStorage.getItem('role') || 'USER'
})

// 未完成考试弹窗相关
const showIncompleteExamDialog = ref(false)
const incompleteExamInfo = ref({
    paperTitle: '',
    startTime: '',
    sessionId: '',
    paperId: '',
    userId: ''
})

const handleLogout = () => {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        // 清除所有用户信息和 Token
        localStorage.removeItem('user')
        localStorage.removeItem('userInfo')
        localStorage.removeItem('role')
        localStorage.removeItem('token')

        ElMessage.success('已安全退出')

        // 跳转到登录页
        router.push('/login')
    }).catch(() => { })
}

// 检测未完成考试
const checkIncompleteExam = async () => {
    try {
        const userId = localStorage.getItem('userId') || userStore.userInfo?.id
        if (!userId) return

        // 检查当前是否已经在考试页面，避免重复弹窗
        if (route.path === '/user/mock-exam') {
            return
        }

        const res = await getIncompleteSessions(userId)
        if (res.code === 200 && res.data && res.data.length > 0) {
            // 有未完成的考试
            const session = res.data[0] // 取最近的一个

            // 获取试卷详情
            const paperRes = await getPaperDetail(session.paperId)
            if (paperRes.code === 200 && paperRes.data) {
                incompleteExamInfo.value = {
                    paperTitle: paperRes.data.title,
                    startTime: formatTime(session.createTime),
                    sessionId: session.id,
                    paperId: session.paperId,
                    userId: userId
                }

                // 显示强制弹窗
                showIncompleteExamDialog.value = true
            }
        }
    } catch (error) {
        console.error('检测未完成考试失败:', error)
    }
}

// 格式化时间
const formatTime = (timeStr) => {
    if (!timeStr) return ''
    const date = new Date(timeStr)
    const now = new Date()
    const diff = Math.floor((now - date) / 1000 / 60) // 分钟差

    if (diff < 1) return '刚刚开始'
    if (diff < 60) return `${diff}分钟前开始`
    const hours = Math.floor(diff / 60)
    if (hours < 24) return `${hours}小时前开始`
    const days = Math.floor(hours / 24)
    return `${days}天前开始`
}

// 继续考试
const continueExam = () => {
    showIncompleteExamDialog.value = false
    router.replace({
        path: '/user/mock-exam',
        query: {
            paperId: incompleteExamInfo.value.paperId,
            userId: incompleteExamInfo.value.userId
        }
    })
}

// 放弃考试
const abandonExam = () => {
    ElMessageBox.confirm(
        '确定要放弃当前考试吗？放弃后当前答题进度将不会保存，建议您继续完成考试。',
        '警告',
        {
            confirmButtonText: '仍要放弃',
            cancelButtonText: '继续考试',
            type: 'warning',
            distinguishCancelAndClose: true
        }
    ).then(() => {
        showIncompleteExamDialog.value = false
        ElMessage.warning('您已放弃考试，如需继续请从套卷列表重新进入')
    }).catch(() => {
        // 用户选择继续考试
    })
}

// 组件挂载时检测未完成考试
onMounted(() => {
    // 延迟1秒检测，避免页面加载时立即弹窗影响体验
    setTimeout(() => {
        checkIncompleteExam()
    }, 1000)

    // 检测是否从 Home 页面进入，触发偏移补偿动画
    // 通过检查 referrer 或路由状态判断
    const fromHome = router.options.history.state?.back === '/user/home' ||
                      document.referrer.includes('/user/home')

    if (fromHome && !route.meta.hideLayout) {
        transitionStore.startEnteringLayout()
    }
})

</script>

<!-- 只需要替换 <style scoped> 部分 -->

<style scoped>
.layout-wrapper {
    height: 100vh;
    width: 100vw;
    overflow: hidden;
    background: linear-gradient(135deg, #f0f9ff 0%, #e6f7ff 100%);
}

.full-height {
    height: 100%;
}

.main-container {
    height: 100%;
    overflow: hidden;
    display: flex;
    flex-direction: column;
}

/* 侧边栏 */
.aside-menu {
    background: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(10px);
    height: 100vh;
    border-right: 1px solid rgba(229, 231, 235, 0.5) !important;
    box-shadow: 2px 0 15px rgba(0, 0, 0, 0.05);
    transition: width 0.3s cubic-bezier(0.2, 0, 0, 1);
    overflow: hidden;
    z-index: 10;
    display: flex;
    flex-direction: column;
}

.logo-box {
    height: 64px;
    display: flex;
    align-items: center;
    padding-left: 20px;
    background: rgba(255, 255, 255, 0.6);
    border-bottom: 1px solid rgba(229, 231, 235, 0.5);
    overflow: hidden;
    white-space: nowrap;
}

.sidebar-toggle {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 24px;
    height: 24px;
    cursor: pointer;
    margin-right: 12px;
    color: #6b7280;
    transition: all 0.3s;
}

.sidebar-toggle:hover {
    color: #3b82f6;
    transform: scale(1.1);
}

.logo-text {
    background: linear-gradient(to right, #1f2937, #3b82f6);
    background-clip: text;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    font-size: 18px;
    font-weight: 700;
    white-space: nowrap;
}

/* 菜单项美化 */
.custom-menu {
    border-right: none;
    background: transparent;
}

.el-menu-vertical:not(.el-menu--collapse) {
    width: 240px;
}

:deep(.el-menu-item) {
    margin: 4px 10px;
    height: 48px;
    line-height: 48px;
    border-radius: 8px;
    color: #6b7280 !important;
    transition: all 0.2s;
}

:deep(.el-menu-item:hover) {
    background: rgba(59, 130, 246, 0.1) !important;
    color: #3b82f6 !important;
}

:deep(.el-menu-item.is-active) {
    background: rgba(59, 130, 246, 0.15) !important;
    color: #3b82f6 !important;
    font-weight: 600;
    position: relative;
}

:deep(.el-menu-item.is-active)::after {
    content: '';
    position: absolute;
    right: -10px;
    top: 0;
    bottom: 0;
    width: 3px;
    background: #3b82f6;
    border-radius: 2px 0 0 2px;
}

/* 菜单图标 SVG 样式 */
.menu-icon-svg {
    width: 1em;
    height: 1em;
    filter: invert(41%) sepia(5%) saturate(542%) hue-rotate(182deg) brightness(91%) contrast(87%);
    transition: all 0.2s;
}

:deep(.el-menu-item:hover) .menu-icon-svg,
:deep(.el-menu-item.is-active) .menu-icon-svg {
    filter: invert(53%) sepia(96%) saturate(3089%) hue-rotate(196deg) brightness(100%) contrast(101%);
}

/* 折叠状态下的特殊样式 */
:deep(.el-menu--collapse .el-menu-item) {
    justify-content: center;
    margin: 4px 0 !important;
    padding: 0 !important;
    width: 100%;
}

:deep(.el-menu--collapse .el-menu-item .el-icon) {
    margin-right: 0;
    text-align: center;
    vertical-align: middle;
}

.menu-divider {
    height: 1px;
    background: rgba(229, 231, 235, 0.5);
    margin: 20px 20px;
}

.admin-entry {
    color: #f59e0b !important;
    border: 1px dashed rgba(245, 158, 11, 0.3);
}

.admin-entry:hover {
    background: rgba(245, 158, 11, 0.1) !important;
    border-color: #f59e0b;
}

/* 顶部栏美化 */
.header-bar {
    background: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(10px);
    border-bottom: 1px solid rgba(229, 231, 235, 0.5);
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 24px;
    z-index: 9;
    flex-shrink: 0;
}

.breadcrumb-text {
    font-size: 16px;
    color: #1f2937;
    font-weight: 500;
}

.user-info {
    display: flex;
    align-items: center;
    cursor: pointer;
    padding: 4px 8px;
    border-radius: 40px;
    transition: all 0.3s;
    border: 1px solid transparent;
}

.user-info:hover {
    background: rgba(255, 255, 255, 0.5);
    border-color: rgba(229, 231, 235, 0.5);
}

.user-name {
    font-size: 14px;
    color: #6b7280;
    margin: 0 10px;
    font-weight: 500;
}

/* 内容区 */
.content-main {
    background: transparent;
    padding: 0;
    overflow-y: auto;
    flex: 1;
    /* 从 Home 进入时的偏移补偿动画 */
    transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.content-main.from-home-enter {
    transform: translateX(-250px);
}

.no-padding {
    padding: 0 !important;
}

/* 路由切换动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
    transition: all 0.3s;
}

.fade-transform-enter-from {
    opacity: 0;
    transform: translateX(-20px);
}

.fade-transform-leave-to {
    opacity: 0;
    transform: translateX(20px);
}

.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}

/* 未完成考试弹窗样式 */
.incomplete-exam-dialog :deep(.el-dialog__header) {
    background: linear-gradient(135deg, #fff7ed 0%, #ffedd5 100%);
    border-bottom: 2px solid #f97316;
    padding: 20px;
}

.incomplete-exam-dialog :deep(.el-dialog__title) {
    font-size: 18px;
    font-weight: 600;
    color: #c2410c;
}

.dialog-content {
    padding: 20px;
}

.exam-info-card {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;
    background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
    border-radius: 12px;
    border: 2px solid #3b82f6;
    margin-bottom: 20px;
}

.exam-icon {
    font-size: 48px;
    line-height: 1;
}

.exam-details {
    flex: 1;
}

.exam-title {
    margin: 0 0 8px 0;
    font-size: 18px;
    font-weight: 600;
    color: #1e40af;
}

.exam-time {
    margin: 0;
    font-size: 14px;
    color: #64748b;
}

.warning-text {
    padding: 16px;
    background: #fef3c7;
    border-radius: 6px;
}

.warning-text p {
    margin: 0;
    font-size: 15px;
    color: #92400e;
    line-height: 1.6;
}

.highlight {
    color: #d32f2f;
    font-weight: bold;
    font-size: 1.1em;
    text-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
}

.continue-btn {
    background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
    border: none;
    padding: 12px 24px;
    font-weight: 600;
    width: 100%;
    height: 50px;
}

.continue-btn:hover {
    background: linear-gradient(135deg, #1d4ed8 0%, #1e40af 100%);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}
</style>
