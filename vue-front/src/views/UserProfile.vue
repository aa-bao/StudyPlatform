<template>
    <div class="dashboard-page">
        <header class="hero-header">
            <div class="noise-bg"></div>
            <div class="container hero-content">
                <div class="user-profile">
                    <div class="avatar-container">
                        <div class="avatar-box" @click="handleAvatarClick" style="cursor: pointer;">
                            <img :src="userInfo.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'"
                                alt="Avatar">
                            <div class="avatar-overlay">更换头像</div>
                        </div>
                        <input type="file" ref="fileInputRef" @change="handleAvatarChange" accept="image/*" style="display: none;">
                    </div>
                    <div class="user-text">
                        <div class="name-line">
                            <h1>{{ userInfo.nickname || '用户' }}</h1>
                            <span class="year-badge">{{ userInfo.examYear || '未设置' }}</span>
                        </div>
                        <p class="motto">{{ userInfo.motto || '不要放弃自己的梦想！' }}</p>
                        <div class="stats-row">
                            <div class="stat-cell"><span>{{ progress[0].current || 0 }}</span> 刷题</div>
                            <div class="v-line"></div>
                            <div class="stat-cell"><span>{{ progress[1].percent || 0 }}%</span> 正确率</div>
                            <div class="v-line"></div>
                            <div class="stat-cell"><span>{{ progress[2].current || 0 }}h</span> 学习时长</div>
                        </div>
                    </div>
                </div>

                <div class="header-cards">
                    <div class="glass-card">
                        <div class="icon-wrap sky">
                            <img :src="calendarIcon" alt="clock">
                        </div>
                        <div class="card-info">
                            <label>连续打卡</label>
                            <p>15天</p>
                        </div>
                    </div>
                    <div class="glass-card">
                        <div class="icon-wrap sky">
                            <img :src="trophyIcon" alt="trophy">
                        </div>
                        <div class="card-info">
                            <label>今日排名</label>
                            <p>TOP 5%</p>
                        </div>
                    </div>
                </div>
            </div>
        </header>

        <main class="container main-layout">
            <div class="col-left">
                <section class="card">
                    <div class="card-header">
                        <h2 class="card-title-with-icon">
                            <img :src="userSettingIcon" alt="setting">
                            个人资料
                        </h2>
                    </div>
                    <div class="tabs">
                        <button :class="{ active: activeTab === 'basic' }" @click="activeTab = 'basic'">基本信息</button>
                        <button :class="{ active: activeTab === 'security' }"
                            @click="activeTab = 'security'">安全设置</button>
                    </div>

                    <!-- 基本信息表单 -->
                    <form v-if="activeTab === 'basic'" class="profile-form" @submit.prevent>
                        <div class="form-row">
                            <div class="form-item">
                                <label>昵称 <span class="req">*</span></label>
                                <input type="text" v-model="userInfo.nickname" placeholder="请输入昵称">
                            </div>
                            <div class="form-item">
                                <label>考研年份 <span class="req">*</span></label>
                                <div class="custom-select">
                                    <select v-model="userInfo.examYear">
                                        <option value="">请选择</option>
                                        <option value="27考研">27考研</option>
                                        <option value="28考研">28考研</option>
                                        <option value="29考研">29考研</option>
                                        <option value="30考研">30考研</option>
                                    </select>
                                    <span class="select-arrow">▼</span>
                                </div>
                            </div>
                        </div>

                        <div class="form-item">
                            <label>期语</label>
                            <div class="textarea-box">
                                <textarea v-model="userInfo.motto" placeholder="写一句话激励自己..." maxlength="100"></textarea>
                                <span class="count">{{ userInfo.motto.length }} / 100</span>
                            </div>
                        </div>

                        <div class="form-item">
                            <label>报考公共课</label>
                            <div class="subject-panel">
                                <div class="subject-item">
                                    <div class="subject-input disabled">
                                        <span>思想政治理论</span>
                                        <span class="mini-tag">必考</span>
                                    </div>
                                </div>
                                <div class="radio-groups">
                                    <div class="radio-row">
                                        <label v-for="opt in ['英语一', '英语二']" :key="opt" class="custom-radio"
                                            :class="{ selected: userInfo.examSubjects.english === opt }">
                                            <input type="radio" :value="opt" v-model="userInfo.examSubjects.english"> {{ opt }}
                                        </label>
                                    </div>
                                    <div class="radio-row">
                                        <label v-for="opt in ['数学一', '数学二', '数学三', '不考数学']" :key="opt"
                                            class="custom-radio" :class="{ selected: userInfo.examSubjects.math === opt }">
                                            <input type="radio" :value="opt" v-model="userInfo.examSubjects.math"> {{ opt }}
                                        </label>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <button type="button" class="btn-save" @click="handleSaveBasicInfo">保存修改</button>
                    </form>

                    <!-- 安全设置表单 -->
                    <form v-else class="profile-form" @submit.prevent="handleUpdatePassword">
                        <div class="form-item">
                            <label>原密码 <span class="req">*</span></label>
                            <input
                                type="password"
                                v-model="passwordForm.oldPassword"
                                placeholder="请输入原密码"
                                @blur="validateOldPassword"
                            >
                            <span v-if="passwordError.oldPassword" class="error-text">{{ passwordError.oldPassword }}</span>
                        </div>

                        <div class="form-item">
                            <label>新密码 <span class="req">*</span></label>
                            <input
                                type="password"
                                v-model="passwordForm.newPassword"
                                placeholder="请输入新密码（6-20位）"
                                @blur="validateNewPassword"
                            >
                            <span v-if="passwordError.newPassword" class="error-text">{{ passwordError.newPassword }}</span>
                        </div>

                        <div class="form-item">
                            <label>确认密码 <span class="req">*</span></label>
                            <input
                                type="password"
                                v-model="passwordForm.confirmPassword"
                                placeholder="请再次输入新密码"
                                @blur="validateConfirmPassword"
                                @input="validateConfirmPassword"
                            >
                            <span v-if="passwordError.confirmPassword" class="error-text">{{ passwordError.confirmPassword }}</span>
                        </div>

                        <div class="form-item">
                            <label>绑定邮箱</label>
                            <div class="input-with-action">
                                <input type="email" v-model="userInfo.email" placeholder="请输入邮箱地址">
                                <button type="button" class="btn-verify" @click="handleEmailVerify">验证</button>
                            </div>
                        </div>

                        <div class="form-item">
                            <label>绑定手机</label>
                            <div class="input-with-action">
                                <input type="tel" v-model="userInfo.phone" placeholder="请输入手机号">
                                <button type="button" class="btn-verify" @click="handlePhoneModify">修改</button>
                            </div>
                        </div>

                        <div class="security-tips">
                            <div class="tip-icon">💡</div>
                            <div class="tip-content">
                                <h4>安全建议</h4>
                                <ul>
                                    <li>建议定期更换密码，保护账户安全</li>
                                    <li>密码应包含字母、数字和特殊字符</li>
                                    <li>不要在多个网站使用相同密码</li>
                                </ul>
                            </div>
                        </div>

                        <button type="submit" class="btn-save">保存修改</button>
                    </form>
                </section>

                <section class="card">
                    <div class="card-header">
                        <h2 class="card-title-with-icon">
                        <img :src="chartIcon" alt="chart">
                            学习进度
                        </h2>
                        <a href="#" class="link">查看详情</a>
                    </div>
                    <div class="progress-stack">
                        <div v-for="item in progress" :key="item.label" class="progress-group">
                            <div class="progress-labels">
                                <span>{{ item.label }} ({{ item.current }}/{{ item.total }} {{ item.unit }})</span>
                                <span class="percent" :style="{ color: item.color }">{{ item.percent }}%</span>
                            </div>
                            <div class="progress-bar">
                                <div class="fill"
                                    :style="{ width: item.percent + '%', backgroundColor: item.color, boxShadow: `0 0 10px ${item.color}4d` }">
                                </div>
                            </div>
                        </div>
                    </div>
                </section>

                <section class="card">
                    <div class="card-header">
                        <h2 class="card-title-with-icon">
                            <img :src="clockIcon" alt="notebook">
                            最近学习
                        </h2>
                        <a href="#" class="link">查看全部</a>
                    </div>
                    <div class="learning-list">
                        <div v-for="i in 3" :key="i" class="learning-item">
                            <div class="item-left">
                                <div class="item-icon" :class="['green', 'amber', 'blue'][i - 1]">
                                    <img :src="notebookIcon" alt="learning">
                                </div>
                                <div class="item-info">
                                    <h3>高等数学 - 第{{ i + 1 }}章练习</h3>
                                    <p>完成 20 题 · 正确率 85%</p>
                                </div>
                            </div>
                            <span class="time">2小时前</span>
                        </div>
                    </div>
                </section>
            </div>

            <div class="col-right">
                <section class="card target-card">
                    <div class="card-header">
                        <h2 class="card-title-with-icon">
                            <img :src="circleIcon" alt="badge">
                            考研目标
                        </h2>
                    </div>
                    <div class="target-content" title="点击可编辑">
                        <div class="target-box">
                            <p>目标院校</p>
                            <input
                                v-if="editingTarget === 'school'"
                                ref="schoolInputRef"
                                type="text"
                                v-model="tempTargetSchool"
                                @blur="handleSaveTargetSchool"
                                @keyup.enter.prevent="$event.target.blur()"
                                class="target-input"
                            />
                            <h3 v-else @click="startEditTarget('school')" class="editable">{{ userInfo.targetSchool || '点击设置' }}</h3>
                        </div>
                        <div class="h-line"></div>
                        <div class="target-box">
                            <p>目标分数</p>
                            <input
                                v-if="editingTarget === 'score'"
                                ref="scoreInputRef"
                                type="number"
                                v-model="tempTargetScore"
                                @blur="handleSaveTargetScore"
                                @keyup.enter.prevent="$event.target.blur()"
                                class="target-input score-input"
                            />
                            <h3 v-else @click="startEditTarget('score')" class="score editable">{{ userInfo.targetTotalScore || 420 }}</h3>
                        </div>
                    </div>
                </section>

                <section class="card">
                    <div class="card-header">
                        <h2 class="card-title-with-icon">
                            <img :src="medalIcon" alt="badge">
                            成就徽章
                        </h2>
                    </div>
                    <div class="badge-grid">
                        <div class="badge-item active-badge">
                            <span>🔥</span>
                            <p>连续7天</p>
                        </div>
                        <div class="badge-item active-badge">
                            <span>💯</span>
                            <p>首次满分</p>
                        </div>
                        <div class="badge-item gray-badge">
                            <img :src="chartIcon" alt="chart">
                            <p>刷题1000</p>
                        </div>
                        <div class="badge-item gray-badge">
                            <img :src="medalIcon" alt="trophy">
                            <p>模考冠军</p>
                        </div>
                    </div>
                </section>

                <section class="card">
                    <div class="card-header">
                        <h2 class="card-title-with-icon">
                            <img :src="chartIcon" alt="chart">
                            学习打卡
                        </h2>
                    </div>
                    <div v-if="!userInfo.id" style="padding: 20px; text-align: center; color: #999;">
                        正在加载用户数据...
                    </div>
                    <StudyHeatmap v-if="userInfo.id" :userId="userInfo.id" :days="180" />
                </section>
            </div>
        </main>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import chartIcon from '@/assets/icons/chart-no-axis.svg?url'
import userSettingIcon from '@/assets/icons/user-setting.svg?url'
import medalIcon from '@/assets/icons/medal.svg?url'
import calendarIcon from '@/assets/icons/calendar.svg?url'
import clockIcon from '@/assets/icons/clock.svg?url'
import notebookIcon from '@/assets/icons/correction-notebook.svg?url'
import circleIcon from '@/assets/icons/circle.svg?url'
import trophyIcon from '@/assets/icons/trophy.svg?url'
import { useUserStore } from '@/stores/user'
import { getUserInfoApi, updateUserApi, updatePwdApi, uploadAvatarApi } from '@/api/user'
import StudyHeatmap from '@/components/chart/StudyHeatmap.vue'

const userStore = useUserStore()
const activeTab = ref('basic')
const fileInputRef = ref(null)
const schoolInputRef = ref(null)
const scoreInputRef = ref(null)
const editingTarget = ref(null)
const tempTargetSchool = ref('')
const tempTargetScore = ref(null)


// 用户信息
const userInfo = reactive({
    id: null,
    nickname: '',
    examYear: '',
    motto: '',
    avatar: '',
    phone: '',
    email: '',
    targetSchool: '',
    targetTotalScore: null,
    examSubjects: {
        politics: '政治',
        english: '英语一',
        math: '数学一'
    }
})

// 密码修改表单
const passwordForm = reactive({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
})

// 密码验证错误信息
const passwordError = reactive({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
})

// 验证原密码
const validateOldPassword = () => {
    if (!passwordForm.oldPassword) {
        passwordError.oldPassword = '请输入原密码'
    } else {
        passwordError.oldPassword = ''
    }
}

// 验证新密码
const validateNewPassword = () => {
    if (!passwordForm.newPassword) {
        passwordError.newPassword = '请输入新密码'
    } else if (passwordForm.newPassword.length < 6 || passwordForm.newPassword.length > 20) {
        passwordError.newPassword = '新密码长度应为6-20位'
    } else {
        passwordError.newPassword = ''
    }
}

// 验证确认密码
const validateConfirmPassword = () => {
    if (!passwordForm.confirmPassword) {
        passwordError.confirmPassword = '请再次输入新密码'
    } else if (passwordForm.confirmPassword !== passwordForm.newPassword) {
        passwordError.confirmPassword = '两次输入的密码不一致'
    } else {
        passwordError.confirmPassword = ''
    }
}

// 考研目标表单（用于弹窗编辑）
const targetForm = reactive({
    targetSchool: '',
    targetTotalScore: null
})

// 学习进度数据
const progress = ref([
    { label: '今日目标', current: 13, total: 20, unit: '题', percent: 65, color: '#2563eb' },
    { label: '本周目标', current: 16, total: 35, unit: '小时', percent: 48, color: '#10b981' },
    { label: '总体目标', current: 320, total: 1000, unit: '题目', percent: 32, color: '#f59e0b' }
])

// 成就徽章数据
const badges = ref([
    { icon: '🔥', name: '连续7天', unlocked: true },
    { icon: '💯', name: '首次满分', unlocked: true },
    { icon: '📊', name: '刷题1000', unlocked: false },
    { icon: '🏆', name: '模考冠军', unlocked: false }
])


// 获取用户数据
const fetchUserData = async () => {
    try {
        // 从store获取userId
        const userId = userStore.userInfo?.id

        if (!userId) {
            ElMessage.error('用户未登录，请先登录')
            return
        }

        const response = await getUserInfoApi(userId)
        if (response.code === 200) {
            const data = response.data
            userInfo.id = data.id
            userInfo.nickname = data.nickname || ''
            userInfo.examYear = data.examYear || ''
            userInfo.motto = data.motto || ''
            userInfo.avatar = data.avatar || ''
            userInfo.phone = data.phone || ''
            userInfo.email = data.email || ''
            userInfo.targetSchool = data.targetSchool || ''
            userInfo.targetTotalScore = data.targetTotalScore || 420

            // 解析examSubjects JSON字符串或逗号分隔字符串
            if (data.examSubjects) {
                try {
                    let subjects
                    if (typeof data.examSubjects === 'string') {
                        // 尝试解析为JSON
                        try {
                            subjects = JSON.parse(data.examSubjects)
                        } catch (e) {
                            // 如果JSON解析失败，尝试按逗号分隔
                            const parts = data.examSubjects.split(',').map(s => s.trim())
                            subjects = {
                                politics: (parts[0] && parts[0] !== 'undefined') ? parts[0] : '政治',
                                english: parts[1] || '英语一',
                                math: parts[2] || '数学一'
                            }
                        }
                    } else {
                        subjects = data.examSubjects
                    }
                    userInfo.examSubjects = subjects
                } catch (e) {
                    console.error('解析examSubjects失败:', e)
                    // 使用默认值
                    userInfo.examSubjects = {
                        politics: '政治',
                        english: '英语一',
                        math: '数学一'
                    }
                }
            } else {
                userInfo.examSubjects = {
                    politics: '政治',
                    english: '英语一',
                    math: '数学一'
                }
            }

            // 更新store
            userStore.setUserInfo(data)
        }
    } catch (error) {
        console.error('获取用户信息失败:', error)
        ElMessage.error('获取用户信息失败')
    }
}

// 点击头像触发文件选择
const handleAvatarClick = () => {
    fileInputRef.value?.click()
}

// 处理头像上传
const handleAvatarChange = async (event) => {
    const file = event.target.files[0]
    if (!file) return

    // 验证文件类型
    if (!file.type.startsWith('image/')) {
        ElMessage.error('请选择图片文件')
        return
    }

    // 验证文件大小（限制2MB）
    if (file.size > 2 * 1024 * 1024) {
        ElMessage.error('图片大小不能超过2MB')
        return
    }

    try {
        const formData = new FormData()
        formData.append('file', file)

        const response = await uploadAvatarApi(formData)
        if (response.code === 200) {
            const avatarUrl = response.data

            // 更新用户头像
            await updateUserInfo({ avatar: avatarUrl })

            ElMessage.success('头像更新成功')
        }
    } catch (error) {
        console.error('上传头像失败:', error)
        ElMessage.error('上传头像失败')
    }
}

// 更新用户信息
const updateUserInfo = async (updateData, showMessage = true) => {
    try {
        const response = await updateUserApi({
            id: userInfo.id,
            ...updateData
        })

        if (response.code === 200) {
            // 更新本地数据（排除 examSubjects，它需要保持对象结构）
            const { examSubjects, ...otherData } = updateData
            Object.assign(userInfo, otherData)

            // 如果有motto更新，需要单独处理
            if (updateData.motto !== undefined) {
                userInfo.motto = updateData.motto
            }

            if (showMessage) {
                ElMessage.success('保存成功')
            }
            return true
        }
        return false
    } catch (error) {
        console.error('更新用户信息失败:', error)
        if (showMessage) {
            ElMessage.error('保存失败')
        }
        return false
    }
}

// 保存基本信息
const handleSaveBasicInfo = async () => {
    // 验证必填字段
    if (!userInfo.nickname || !userInfo.nickname.trim()) {
        ElMessage.warning('请输入昵称')
        return
    }

    if (!userInfo.examYear) {
        ElMessage.warning('请选择考研年份')
        return
    }

    // 准备更新数据
    const updateData = {
        id: userInfo.id,
        nickname: userInfo.nickname,
        motto: userInfo.motto,
        examYear: userInfo.examYear,
        // 确保政治课固定为"政治"
        examSubjects: `政治,${userInfo.examSubjects.english || '英语一'},${userInfo.examSubjects.math || '数学一'}`
    }

    await updateUserInfo(updateData)
}

// 修改密码
const handleUpdatePassword = async () => {
    // 清空之前的错误信息
    Object.keys(passwordError).forEach(key => passwordError[key] = '')

    // 验证输入
    if (!passwordForm.oldPassword) {
        passwordError.oldPassword = '请输入原密码'
        ElMessage.warning('请输入原密码')
        return
    }

    if (!passwordForm.newPassword) {
        passwordError.newPassword = '请输入新密码'
        ElMessage.warning('请输入新密码')
        return
    }

    if (passwordForm.newPassword.length < 6 || passwordForm.newPassword.length > 20) {
        passwordError.newPassword = '新密码长度应为6-20位'
        ElMessage.warning('新密码长度应为6-20位')
        return
    }

    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
        passwordError.confirmPassword = '两次输入的密码不一致'
        ElMessage.warning('两次输入的密码不一致')
        return
    }

    try {
        const response = await updatePwdApi({
            userId: userInfo.id.toString(),
            oldPassword: passwordForm.oldPassword,
            newPassword: passwordForm.newPassword
        })

        if (response.code === 200) {
            ElMessage.success('密码修改成功，请重新登录')

            // 清空表单和错误信息
            passwordForm.oldPassword = ''
            passwordForm.newPassword = ''
            passwordForm.confirmPassword = ''
            Object.keys(passwordError).forEach(key => passwordError[key] = '')

            // 可以在这里添加跳转到登录页的逻辑
        } else {
            // 显示后端返回的具体错误信息
            const errorMsg = response.msg || '密码修改失败'
            ElMessage.error(errorMsg)

            // 根据错误信息判断是哪个字段错了
            if (errorMsg.includes('原密码')) {
                passwordError.oldPassword = errorMsg
            }
        }
    } catch (error) {
        console.error('修改密码失败:', error)
        // 处理网络错误或其他异常
        if (error.response && error.response.data && error.response.data.msg) {
            ElMessage.error(error.response.data.msg)
        } else {
            ElMessage.error('网络错误，请稍后重试')
        }
    }
}

// 开始编辑考研目标
const startEditTarget = (field) => {
    editingTarget.value = field
    if (field === 'school') {
        tempTargetSchool.value = userInfo.targetSchool || ''
    } else if (field === 'score') {
        tempTargetScore.value = userInfo.targetTotalScore || 420
    }

    // 自动聚焦输入框
    setTimeout(() => {
        if (field === 'school') {
            schoolInputRef.value?.focus()
        } else if (field === 'score') {
            scoreInputRef.value?.focus()
        }
    }, 0)
}

// 保存目标院校
const handleSaveTargetSchool = async () => {
    if (!tempTargetSchool.value || !tempTargetSchool.value.trim()) {
        tempTargetSchool.value = userInfo.targetSchool || ''
        editingTarget.value = null
        return
    }

    editingTarget.value = null
    await updateUserInfo({
        targetSchool: tempTargetSchool.value.trim()
    }, false)
    ElMessage.success('目标院校更新成功')
}

// 保存目标分数
const handleSaveTargetScore = async () => {
    const score = parseInt(tempTargetScore.value)
    if (!score || score < 0 || score > 750) {
        ElMessage.warning('请输入有效的分数（0-750）')
        tempTargetScore.value = userInfo.targetTotalScore || 420
        editingTarget.value = null
        return
    }

    editingTarget.value = null
    await updateUserInfo({
        targetTotalScore: score
    }, false)
    ElMessage.success('目标分数更新成功')
}

// 点击邮箱验证
const handleEmailVerify = () => {
    ElMessage.info('验证码已发送至您的邮箱，请查收')
}

// 点击手机修改
const handlePhoneModify = () => {
    ElMessage.info('功能开发中')
}

// 在 onMounted 中获取用户数据
onMounted(async () => {
    await fetchUserData()
})
</script>

<style scoped>
/* 基础重置 */
.dashboard-page {
    background-color: #f9fafb;
    min-height: 100vh;
    color: #4b5563;
    font-family: 'Inter', -apple-system, sans-serif;
    padding-bottom: 3rem;
}

.container {
    max-width: 1300px;
    margin: 0 auto;
    padding: 0 1rem;
}

/* Header 样式 */
.hero-header {
    background: linear-gradient(135deg, #1d4ed8 0%, #2563eb 50%, #0ea5e9 100%);
    padding: 3rem 0 6rem;
    position: relative;
    color: white;
    overflow: hidden;
}

.noise-bg {
    position: absolute;
    inset: 0;
    opacity: 0.05;
    mix-blend-mode: soft-light;
    background: 
        repeating-linear-gradient(
            0deg,
            transparent,
            transparent 2px,
            rgba(255,255,255,0.03) 2px,
            rgba(255,255,255,0.03) 4px
        );
}

.hero-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: relative;
    flex-wrap: wrap;
    gap: 2rem;
}

.user-profile {
    display: flex;
    align-items: center;
    gap: 1.5rem;
}

.avatar-box {
    width: 96px;
    height: 96px;
    border-radius: 50%;
    border: 4px solid rgba(255, 255, 255, 0.2);
    overflow: hidden;
    box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1);
    position: relative;
}

.avatar-box img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.avatar-overlay {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: rgba(0, 0, 0, 0.5);
    color: white;
    font-size: 0.7rem;
    padding: 4px 0;
    text-align: center;
    opacity: 0;
    transition: opacity 0.3s;
}

.avatar-box:hover .avatar-overlay {
    opacity: 1;
}

.avatar-container {
    position: relative;
}

.crown-icon {
    position: absolute;
    bottom: 0;
    right: 0;
    background: #fbbf24;
    width: 28px;
    height: 28px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 2px solid #fff;
}

.crown-icon img {
    width: 16px;
    height: 16px;
    filter: brightness(0) saturate(100%) invert(9%) sepia(75%) saturate(1582%) hue-rotate(354deg) brightness(93%) contrast(93%);
}

.user-text h1 {
    font-size: 1.8rem;
    margin: 0;
}

.name-line {
    display: flex;
    align-items: center;
    gap: 0.8rem;
}

.year-badge {
    background: rgba(255, 255, 255, 0.2);
    padding: 0.2rem 0.8rem;
    border-radius: 20px;
    font-size: 0.75rem;
}

.motto {
    margin: 0.5rem 0 1rem;
    opacity: 0.9;
}

.stats-row {
    display: flex;
    align-items: center;
    gap: 1rem;
    font-size: 0.85rem;
}

.stats-row span {
    font-weight: 600;
    font-size: 1.1rem;
}

.v-line {
    width: 1px;
    height: 15px;
    background: rgba(255, 255, 255, 0.3);
}

.header-cards {
    display: flex;
    gap: 1rem;
}

.glass-card {
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.1);
    padding: 1rem 1.5rem;
    border-radius: 1rem;
    display: flex;
    align-items: center;
    gap: 1rem;
    min-width: 160px;
}

.icon-wrap {
    width: 40px;
    height: 40px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.icon-wrap img {
    width: 20px;
    height: 20px;
}

.icon-wrap.sky {
    background: rgba(56, 189, 248, 0.2);
}

.icon-wrap.sky img {
    filter: brightness(0) saturate(100%) invert(92%) sepia(39%) saturate(1754%) hue-rotate(185deg) brightness(96%) contrast(91%);
}

.icon-wrap.amber {
    background: rgba(245, 158, 11, 0.2);
}

.icon-wrap.amber img {
    filter: brightness(0) saturate(100%) invert(75%) sepia(89%) saturate(495%) hue-rotate(358deg) brightness(102%) contrast(97%);
}

.card-info label {
    font-size: 0.75rem;
    display: block;
    opacity: 0.8;
}

.card-info p {
    font-weight: 600;
    margin: 0;
    font-size: 1.1rem;
}

/* 布局 */
.main-layout {
    display: grid;
    grid-template-columns: 1fr;
    gap: 1.5rem;
    margin-top: -3rem;
    position: relative;
}

@media (min-width: 1024px) {
    .main-layout {
        grid-template-columns: 8fr 4fr;
    }
}

/* 卡片通用 */
.card {
    background: white;
    border-radius: 1rem;
    border: 1px solid #e5e7eb;
    padding: 1.5rem;
    margin-bottom: 1.5rem;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
}

.card-header h2 {
    font-size: 1.1rem;
    margin: 0;
    color: #111827;
}

.card-title-with-icon {
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

.card-title-with-icon img {
    width: 20px;
    height: 20px;
    filter: brightness(0) saturate(100%) invert(27%) sepia(96%) saturate(4257%) hue-rotate(217deg) brightness(95%) contrast(95%);
}

.link {
    color: #2563eb;
    text-decoration: none;
    font-size: 0.85rem;
    font-weight: 500;
}

/* 进度条 */
.progress-stack {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
}

.progress-labels {
    display: flex;
    justify-content: space-between;
    font-size: 0.85rem;
    margin-bottom: 0.5rem;
}

.progress-bar {
    height: 10px;
    background: #f3f4f6;
    border-radius: 10px;
    overflow: hidden;
}

.fill {
    height: 100%;
    border-radius: 10px;
    transition: width 0.5s;
}

/* 标签页 */
.tabs {
    display: flex;
    gap: 2rem;
    border-bottom: 2px solid #f3f4f6;
    margin-bottom: 2rem;
}

.tabs button {
    background: none;
    border: none;
    padding: 0.8rem 0;
    color: #6b7280;
    cursor: pointer;
    border-bottom: 2px solid transparent;
    font-weight: 500;
    font-size: 0.9rem;
    transition: all 0.3s ease;
    position: relative;
}

.tabs button:hover {
    color: #3b82f6;
}

.tabs button.active {
    color: #3b82f6;
    border-bottom-color: #3b82f6;
}

.tabs button.active::after {
    content: '';
    position: absolute;
    bottom: -2px;
    left: 0;
    right: 0;
    height: 2px;
    background: #3b82f6;
    animation: slideIn 0.3s ease;
}

@keyframes slideIn {
    from {
        transform: scaleX(0);
    }
    to {
        transform: scaleX(1);
    }
}

/* 表单 */
.profile-form {
    display: flex;
    flex-direction: column;
    gap: 1.8rem;
}

.form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1rem;
}

@media (max-width: 768px) {
    .form-row {
        grid-template-columns: 1fr;
    }

    .tabs {
        gap: 1rem;
    }

    .tabs button {
        font-size: 0.85rem;
        padding: 0.7rem 0;
    }

    .input-with-action {
        flex-direction: column;
    }

    .btn-verify {
        width: 100%;
    }

    .security-tips {
        flex-direction: column;
        gap: 0.5rem;
    }

    .tip-content ul {
        padding-left: 1rem;
    }
}

.form-item label {
    display: block;
    font-size: 0.8rem;
    font-weight: 600;
    color: #374151;
    margin-bottom: 0.5rem;
    letter-spacing: 0.02em;
}

.req {
    color: #ef4444;
    margin-left: 2px;
}

.form-item input,
.form-item select,
textarea {
    width: 100%;
    max-width: 100%;
    padding: 0.75rem 1rem;
    border: 1px solid #e5e7eb;
    border-radius: 0.6rem;
    background: #f9fafb;
    outline: none;
    transition: all 0.2s;
    box-sizing: border-box;
    font-size: 0.9rem;
    color: #1f2937;
}

.form-item input::placeholder,
textarea::placeholder {
    color: #9ca3af;
}

.form-item input:hover,
.form-item select:hover,
textarea:hover {
    border-color: #d1d5db;
    background: #f3f4f6;
}

.form-item input:focus,
textarea:focus {
    border-color: #3b82f6;
    background: white;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* 错误提示文本 */
.error-text {
    display: block;
    margin-top: 0.3rem;
    font-size: 0.75rem;
    color: #ef4444;
}

/* 自定义下拉框样式 */
.custom-select {
    position: relative;
    width: 100%;
}

.custom-select select {
    width: 100%;
    padding: 0.7rem 2.5rem 0.7rem 1rem;
    border: 1px solid #e5e7eb;
    border-radius: 0.6rem;
    background: #f9fafb;
    appearance: none;
    -webkit-appearance: none;
    -moz-appearance: none;
    cursor: pointer;
    transition: all 0.2s;
    font-size: 0.9rem;
    color: #1f2937;
}

.custom-select select:hover {
    border-color: #d1d5db;
    background: #f3f4f6;
}

.custom-select select:focus {
    border-color: #3b82f6;
    background: white;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.custom-select select option {
    padding: 0.5rem;
    background: white;
}

.custom-select .select-arrow {
    position: absolute;
    right: 1rem;
    top: 50%;
    transform: translateY(-50%);
    pointer-events: none;
    font-size: 0.7rem;
    color: #6b7280;
    transition: transform 0.2s;
}

.custom-select:hover .select-arrow {
    color: #3b82f6;
}

/* 输入框+按钮组合 */
.input-with-action {
    display: flex;
    gap: 0.5rem;
}

.input-with-action input {
    flex: 1;
    padding-right: 1rem;
}

.btn-verify {
    padding: 0.7rem 1.2rem;
    border: 1px solid #e5e7eb;
    border-radius: 0.6rem;
    background: white;
    color: #6b7280;
    font-size: 0.85rem;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;
    white-space: nowrap;
}

.btn-verify:hover {
    border-color: #3b82f6;
    color: #3b82f6;
    background: #eff6ff;
}

/* 安全提示 */
.security-tips {
    display: flex;
    gap: 1rem;
    padding: 1rem;
    background: linear-gradient(135deg, #eff6ff, #dbeafe);
    border-radius: 0.8rem;
    border: 1px solid #bfdbfe;
    margin-bottom: 1rem;
}

.tip-icon {
    font-size: 1.5rem;
    flex-shrink: 0;
}

.tip-content h4 {
    margin: 0 0 0.5rem 0;
    font-size: 0.9rem;
    font-weight: 600;
    color: #1e40af;
}

.tip-content ul {
    margin: 0;
    padding-left: 1.2rem;
    list-style-type: disc;
}

.tip-content li {
    font-size: 0.8rem;
    color: #3b82f6;
    margin-bottom: 0.3rem;
    line-height: 1.4;
}

.form-item input:focus,
textarea:focus {
    border-color: #2563eb;
    background: #fff;
    box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.1);
}

.textarea-box {
    position: relative;
}

textarea {
    height: 100px;
    resize: none;
}

.count {
    position: absolute;
    bottom: 8px;
    right: 12px;
    font-size: 0.7rem;
    color: #9ca3af;
}

.subject-panel {
    border: 1px solid #fef3c7;
    background: #fffbeb;
    border-radius: 1rem;
    padding: 1.2rem;
}

.subject-item {
    margin-bottom: 1rem;
}

.subject-input {
    width: 100%;
    padding: 0.75rem 1rem;
    border: 1px solid #e5e7eb;
    border-radius: 0.6rem;
    background: #f9fafb;
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 0.9rem;
    font-weight: 500;
    box-sizing: border-box;
    transition: all 0.2s;
}

.subject-input.disabled {
    background: linear-gradient(135deg, #f3f4f6, #e5e7eb);
    color: #6b7280;
    cursor: not-allowed;
    border-color: #d1d5db;
}

.mini-tag {
    background: #fde68a;
    color: #92400e;
    font-size: 10px;
    padding: 1px 5px;
    border-radius: 4px;
}

.radio-groups {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.radio-row {
    display: flex;
    flex-wrap: wrap;
    gap: 0.8rem;
}

.custom-radio {
    padding: 0.65rem 1.2rem;
    border: 1.5px solid #e5e7eb;
    border-radius: 0.6rem;
    background: white;
    font-size: 0.85rem;
    cursor: pointer;
    transition: all 0.2s;
    position: relative;
    overflow: hidden;
}

.custom-radio input {
    display: none;
}

.custom-radio.selected {
    background: linear-gradient(135deg, #3b82f6, #2563eb);
    color: white;
    border-color: #3b82f6;
    box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
}

.custom-radio:hover {
    border-color: #3b82f6;
    background: #eff6ff;
}

.custom-radio.selected:hover {
    background: linear-gradient(135deg, #3b82f6, #2563eb);
    color: white;
}

.btn-save {
    background: linear-gradient(135deg, #3b82f6, #2563eb);
    color: white;
    border: none;
    padding: 0.8rem 2rem;
    border-radius: 0.6rem;
    font-weight: 600;
    font-size: 0.9rem;
    cursor: pointer;
    align-self: flex-start;
    box-shadow: 0 4px 6px rgba(37, 99, 235, 0.2);
    transition: all 0.3s ease;
}

.btn-save:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 12px rgba(37, 99, 235, 0.3);
}

.btn-save:active {
    transform: translateY(0);
}

/* 最近学习 */
.learning-list {
    display: flex;
    flex-direction: column;
    gap: 0.8rem;
}

.learning-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem;
    background: #f9fafb;
    border-radius: 0.8rem;
    border: 1px solid transparent;
    transition: 0.2s;
}

.learning-item:hover {
    background: white;
    border-color: #e5e7eb;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.02);
}

.item-left {
    display: flex;
    gap: 1rem;
    align-items: center;
}

.item-icon {
    width: 40px;
    height: 40px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.item-icon img {
    width: 20px;
    height: 20px;
}

.item-icon.green {
    background: #ecfdf5;
}

.item-icon.green img {
    filter: brightness(0) saturate(100%) invert(67%) sepia(98%) saturate(444%) hue-rotate(122deg) brightness(91%) contrast(90%);
}

.item-icon.amber {
    background: #fffbeb;
}

.item-icon.amber img {
    filter: brightness(0) saturate(100%) invert(75%) sepia(89%) saturate(495%) hue-rotate(358deg) brightness(102%) contrast(97%);
}

.item-icon.blue {
    background: #eff6ff;
}

.item-icon.blue img {
    filter: brightness(0) saturate(100%) invert(27%) sepia(96%) saturate(4257%) hue-rotate(217deg) brightness(95%) contrast(95%);
}

.item-info h3 {
    font-size: 0.9rem;
    margin: 0;
    color: #111827;
}

.item-info p {
    font-size: 0.75rem;
    margin: 0.2rem 0 0;
    color: #6b7280;
}

.time {
    font-size: 0.75rem;
    color: #9ca3af;
}

/* 右侧边栏专有 */
.target-card {
    text-align: center;
}

.target-card label {
    font-size: 0.9rem;
    color: #9ca3af;
    letter-spacing: 0.1em;
    text-transform: uppercase;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.4rem;
}

.target-card label img {
    width: 18px;
    height: 18px;
    filter: brightness(0) saturate(100%) invert(72%) sepia(77%) saturate(365%) hue-rotate(212deg) brightness(93%) contrast(88%);
}

.target-content {
    margin-top: 1.5rem;
    transition: transform 0.3s ease;
}

.target-content:hover {
    transform: translateY(-2px);
}

.target-box {
    margin-bottom: 24px;
}

.target-box p {
    font-size: 0.75rem;
    color: #9ca3af;
    margin-bottom: 0.4rem;
}

.target-box h3 {
    font-size: 1.8rem;
    margin: 0;
    color: #111827;
}

.target-box h3.editable {
    cursor: pointer;
    transition: color 0.2s;
}

.target-box h3.editable:hover {
    color: #2563eb;
}

.target-input {
    font-size: 1.8rem;
    font-weight: 600;
    margin: 0;
    color: #111827;
    border: 2px solid #2563eb;
    border-radius: 0.5rem;
    padding: 0.3rem 0.5rem;
    outline: none;
    background: white;
    width: 100%;
    box-sizing: border-box;
    text-align: center;
}

.target-input.score-input {
    font-size: 2.2rem;
}

.target-input:focus {
    box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.target-box h3.score {
    font-size: 2.2rem;
}

.h-line {
    height: 1px;
    background: #f3f4f6;
    width: 60px;
    margin: 1.5rem auto;
}

.badge-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 0.8rem;
}

.badge-item {
    padding: 1rem;
    border-radius: 1rem;
    text-align: center;
    border: 1px solid #f3f4f6;
}

.active-badge {
    background: #fffbeb;
    border-color: #fde68a;
}

.badge-item img {
    width: 28px;
    height: 28px;
    display: block;
    margin: 0 auto;
}

.active-badge img {
    filter: brightness(0) saturate(100%) invert(55%) sepia(87%) saturate(468%) hue-rotate(7deg) brightness(101%) contrast(93%);
}

.gray-badge img {
    filter: grayscale(1) brightness(0) saturate(100%) invert(73%) sepia(15%) saturate(389%) hue-rotate(181deg) brightness(91%) contrast(87%);
}

.active-badge p {
    font-size: 0.7rem;
    color: #92400e;
    margin: 0.5rem 0 0;
    font-weight: 500;
}

.gray-badge {
    background: #f9fafb;
    opacity: 0.6;
}

.gray-badge p {
    font-size: 0.7rem;
    margin: 0.5rem 0 0;
}


</style>