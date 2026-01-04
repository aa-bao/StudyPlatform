<template>
  <div class="profile-container">
    <div class="profile-content">
      <el-row :gutter="24">
        <!-- 左侧个人信息卡片 -->
        <el-col :xs="24" :sm="6" :md="6" :lg="6" class="left-col">
          <el-card class="user-card animate-card" :body-style="{ padding: '0px' }">
            <div class="user-card-top">
              <div class="role-badge" :class="userInfo.role">
                {{ userInfo.role === 'admin' ? '管理员' : '正式学员' }}
              </div>
              <el-upload class="avatar-uploader" action="#" :show-file-list="false" :auto-upload="false"
                :on-change="handleAvatarChange">
                <div class="avatar-wrapper">
                  <el-avatar :size="100" class="user-avatar"
                    :src="userInfo.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" />
                  <div class="avatar-mask">
                    <el-icon class="camera-icon">
                      <Camera />
                    </el-icon>
                    <span class="upload-text">更换</span>
                  </div>
                </div>
              </el-upload>
              <h2 class="user-name">{{ userInfo.nickname || userInfo.username || '考研战士' }}</h2>
              <p class="user-slogan">道阻且长，行则将至</p>

              <!-- 倒计时小组件 -->
              <div class="countdown-badge" v-if="daysToExam && daysToExam > 0">
                <span class="label">距离考研</span>
                <span class="number">{{ daysToExam }}</span>
                <span class="unit">天</span>
              </div>
            </div>

            <div class="user-bio">
              <div class="bio-item">
                <span class="label">用户名</span>
                <span class="value">{{ userInfo.username || '未加载' }}</span>
              </div>
              <div class="bio-item">
                <span class="label">目标年份</span>
                <span class="value">{{ userInfo.examYear || '未设置' }}</span>
              </div>
              <div class="bio-item">
                <span class="label">公共课</span>
                <div class="subject-tags" v-if="userInfo.examSubjects">
                  <el-tag v-for="tag in userInfo.examSubjects.split(',')" :key="tag" size="small" effect="plain" round
                    class="custom-tag">
                    {{ tag }}
                  </el-tag>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 近期目标 -->
          <el-card class="memo-card animate-card">
            <div class="memo-header">
              <span class="memo-title">近期目标</span>
              <el-icon class="memo-icon">
                <EditPen />
              </el-icon>
            </div>
            <div class="memo-list">
              <div class="memo-item">
                <el-checkbox v-model="memo1">背诵英语单词 50 个</el-checkbox>
              </div>
              <div class="memo-item">
                <el-checkbox v-model="memo2">复习政治马原第一章</el-checkbox>
              </div>
              <div class="memo-item">
                <el-checkbox v-model="memo3">做一套数学模拟卷</el-checkbox>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 中间设置面板 -->
        <el-col :xs="24" :sm="12" :md="11" :lg="10" class="center-col">
          <el-card class="settings-card animate-card">
            <template #header>
              <div class="card-header">
                <div class="header-left">
                  <span class="header-title">个人设置</span>
                  <span class="header-desc">完善个人信息，定制专属复习计划</span>
                </div>
              </div>
            </template>

            <el-tabs v-model="activeTab" class="custom-tabs">
              <el-tab-pane label="基本资料" name="info">
                <div class="tab-content-wrapper">
                  <el-form :model="infoForm" :rules="infoRules" ref="infoFormRef" label-width="80px"
                    class="profile-form" label-position="top">

                    <el-form-item label="昵称" prop="nickname">
                      <el-input v-model="infoForm.nickname" placeholder="请输入昵称" size="large">
                        <template #prefix><el-icon>
                            <User />
                          </el-icon></template>
                      </el-input>
                    </el-form-item>

                    <el-form-item label="考研年份" prop="examYear">
                      <el-select v-model="infoForm.examYear" placeholder="请选择目标年份" style="width: 100%" size="large">
                        <el-option label="27考研 (2026年12月考)" value="27考研" />
                        <el-option label="28考研 (2027年12月考)" value="28考研" />
                        <el-option label="29考研 (2028年12月考)" value="29考研" />
                      </el-select>
                    </el-form-item>

                    <el-form-item label="报考科目设置" class="subject-form-item">
                      <div class="subject-selection-card">
                        <div class="subject-group">
                          <div class="group-title">公共课 · 政治</div>
                          <div class="group-content">
                            <el-checkbox v-model="infoForm.politics" disabled border
                              class="fixed-subject">思想政治理论</el-checkbox>
                          </div>
                        </div>

                        <div class="subject-divider"></div>

                        <div class="subject-group">
                          <div class="group-title">公共课 · 外语</div>
                          <div class="group-content">
                            <el-radio-group v-model="infoForm.english">
                              <el-radio label="英语一" border>英语一</el-radio>
                              <el-radio label="英语二" border>英语二</el-radio>
                            </el-radio-group>
                          </div>
                        </div>

                        <div class="subject-divider"></div>

                        <div class="subject-group">
                          <div class="group-title">公共课 · 数学</div>
                          <div class="group-content">
                            <el-radio-group v-model="infoForm.math">
                              <el-radio label="数学一" border>数学一</el-radio>
                              <el-radio label="数学二" border>数学二</el-radio>
                              <el-radio label="数学三" border>数学三</el-radio>
                              <el-radio label="无" border>不考数学</el-radio>
                            </el-radio-group>
                          </div>
                        </div>
                      </div>
                    </el-form-item>

                    <el-form-item class="form-actions">
                      <el-button type="primary" size="large" @click="handleUpdateInfo" :loading="loading"
                        class="submit-btn" round>
                        保存修改
                      </el-button>
                    </el-form-item>
                  </el-form>
                </div>
              </el-tab-pane>

              <el-tab-pane label="安全设置" name="security">
                <div class="tab-content-wrapper security-wrapper">
                  <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="100px"
                    class="profile-form security-form" label-position="top">
                    <el-form-item label="旧密码" prop="oldPassword">
                      <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入当前密码"
                        size="large" />
                    </el-form-item>
                    <el-form-item label="新密码" prop="newPassword">
                      <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码"
                        size="large" />
                    </el-form-item>
                    <el-form-item label="确认密码" prop="confirmPassword">
                      <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码"
                        size="large" />
                    </el-form-item>
                    <el-form-item class="form-actions">
                      <el-button type="primary" size="large" @click="handleChangePassword" :loading="loading"
                        class="submit-btn" round>
                        修改密码
                      </el-button>
                    </el-form-item>
                  </el-form>
                </div>
              </el-tab-pane>
            </el-tabs>
          </el-card>
        </el-col>

        <!-- 右侧统计面板 -->
        <el-col :xs="24" :sm="6" :md="6" :lg="8" class="right-col">
          <div class="stats-column">
            <div class="stat-card-vertical">
              <div class="stat-icon-v icon-blue"><el-icon>
                  <Timer />
                </el-icon></div>
              <div class="stat-info-v">
                <div class="stat-value-v">126</div>
                <div class="stat-label-v">累计学习(小时)</div>
              </div>
            </div>

            <div class="stat-card-vertical">
              <div class="stat-icon-v icon-green"><el-icon>
                  <Collection />
                </el-icon></div>
              <div class="stat-info-v">
                <div class="stat-value-v">382</div>
                <div class="stat-label-v">刷题数量(道)</div>
              </div>
            </div>

            <div class="stat-card-vertical">
              <div class="stat-icon-v icon-orange"><el-icon>
                  <Trophy />
                </el-icon></div>
              <div class="stat-info-v">
                <div class="stat-value-v">78%</div>
                <div class="stat-label-v">平均正确率</div>
              </div>
            </div>

            <!-- 目标设置卡片 -->
            <el-card class="goals-setting-card animate-card">
              <div class="goal-header">
                <span class="title">我的目标</span>
                <el-button type="primary" link @click="handleUpdateInfo">保存</el-button>
              </div>

              <div class="target-form-mini">
                <div class="form-item-mini">
                  <div class="label">目标院校</div>
                  <el-input v-model="infoForm.targetSchool" placeholder="请输入目标院校" class="school-input">
                    <template #prefix><el-icon>
                        <School />
                      </el-icon></template>
                  </el-input>
                </div>

                <div class="form-item-mini">
                  <div class="label">目标分数 (总分)</div>
                  <div class="score-input-wrapper-single">
                    <el-input-number v-model="infoForm.targetTotalScore" :min="0" :max="500" :controls="false"
                      class="mini-input-large" placeholder="0" />
                    <span class="unit">分</span>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { User, Camera, Calendar, Reading, Timer, Collection, Trophy, Flag, EditPen, School } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'
import { updateUserApi, uploadAvatarApi, updatePwdApi } from '../api/user'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()
const userInfo = ref({})
const loading = ref(false)
const activeTab = ref('info')
const infoFormRef = ref(null)
const pwdFormRef = ref(null)

// 1. 表单数据
const infoForm = reactive({
  nickname: '',
  examYear: '',
  politics: true,
  english: '',
  math: '',
  targetSchool: '',
  targetTotalScore: 0
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 2. 校验规则
const infoRules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  examYear: [{ required: true, message: '请选择年份', trigger: 'change' }]
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, min: 6, message: '新密码至少6位', trigger: 'blur' }],
  confirmPassword: [{
    validator: (rule, value, callback) => {
      if (value !== pwdForm.newPassword) callback(new Error('两次输入不一致'))
      else callback()
    },
    trigger: 'blur'
  }]
}

// 3. 计算属性
const examSubjectsStr = computed(() => {
  const res = ['政治']
  if (infoForm.english) res.push(infoForm.english)
  if (infoForm.math && infoForm.math !== '无') res.push(infoForm.math)
  return res.join(',')
})

// 倒计时计算
const daysToExam = computed(() => {
  if (!userInfo.value.examYear) return null
  const yearMap = {
    '27考研': 2026,
    '28考研': 2027,
    '29考研': 2028
  }
  const targetYear = yearMap[userInfo.value.examYear]
  if (!targetYear) return null

  // 假设考试日期为当年的12月20日左右
  const examDate = new Date(targetYear, 11, 20)
  const today = new Date()

  // 如果已经过了，就不显示或者显示0
  if (today > examDate) return 0

  const diff = examDate - today
  return Math.ceil(diff / (1000 * 60 * 60 * 24))
})

// 4. 加载数据逻辑
const loadUserData = () => {
  const userStr = localStorage.getItem('user') || localStorage.getItem('userInfo')
  if (userStr) {
    const user = JSON.parse(userStr)
    userInfo.value = user

    infoForm.nickname = user.nickname || ''
    infoForm.examYear = user.examYear || ''
    infoForm.targetSchool = user.targetSchool || ''
    infoForm.targetTotalScore = user.targetTotalScore || 0

    const subjects = user.examSubjects ? user.examSubjects.split(',') : []
    infoForm.english = subjects.find(s => s.includes('英语')) || ''
    infoForm.math = subjects.find(s => s.includes('数学')) || '无'
  }
}

onMounted(loadUserData)

// 5. 修改基本资料
const handleUpdateInfo = async () => {
  if (!infoFormRef.value) return

  await infoFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const submitData = {
          id: userInfo.value.id,
          nickname: infoForm.nickname,
          examYear: infoForm.examYear,
          examSubjects: examSubjectsStr.value,
          targetSchool: infoForm.targetSchool,
          targetTotalScore: infoForm.targetTotalScore
        }

        const res = await updateUserApi(submitData)

        if (res.code === 200) {
          ElMessage.success('信息更新成功')
          const newUser = { ...userInfo.value, ...submitData }
          localStorage.setItem('user', JSON.stringify(newUser))
          userStore.setUserInfo(newUser)
          userInfo.value = newUser
        }
      } catch (error) {
        console.error("更新失败:", error)
      } finally {
        loading.value = false
      }
    }
  })
}

// 6. 更换头像
const handleAvatarChange = async (uploadFile) => {
  const rawFile = uploadFile.raw
  if (!['image/jpeg', 'image/png'].includes(rawFile.type)) {
    return ElMessage.error('图片格式错误')
  }

  try {
    const formData = new FormData()
    formData.append('file', rawFile)

    const res = await uploadAvatarApi(formData)
    const url = res.data

    if (!url) {
      ElMessage.error('头像上传失败')
      return
    }

    await updateUserApi({ id: userInfo.value.id, avatar: url })

    const newUser = { ...userInfo.value, avatar: url }
    userInfo.value = newUser
    localStorage.setItem('user', JSON.stringify(newUser))
    userStore.setUserInfo(newUser)

    ElMessage.success('头像更换成功')
  } catch (e) {
    console.error('更换头像异常:', e)
    ElMessage.error('更换头像失败')
  }
}

// 7. 修改密码
const handleChangePassword = async () => {
  await pwdFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await updatePwdApi({
          userId: userInfo.value.id,
          oldPassword: pwdForm.oldPassword,
          newPassword: pwdForm.newPassword
        })
        if (res.code === 200) {
          ElMessage.success('密码修改成功，请重新登录')
          localStorage.clear()
          router.push('/login')
        }
      } catch (e) {
        console.error(e)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
/* 容器布局 */
.profile-container {
  min-height: 100vh;
  background-color: transparent;
  padding: 20px;
}

.profile-content {
  max-width: 1400px;
  margin: 0 auto;
}

/* 统计卡片 */
.stat-row {
  margin-bottom: 20px;
}

.stat-card-vertical {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 18px;
  padding: 22px;
  margin-bottom: 18px;
  display: flex;
  align-items: center;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
  border: 1px solid rgba(255, 255, 255, 0.7);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.03);
}

.stat-icon-v {
  width: 50px;
  height: 50px;
  border-radius: 15px;
  box-shadow: 0 5px 15px -3px currentColor;
  /* 让图标带点发光感 */
}

.stat-value-v {
  font-size: 28px;
  font-weight: 800;
  letter-spacing: -1px;
}

/* 备考便签美化：像便利贴一样 */
.memo-card {
  margin-top: 25px;
  background: linear-gradient(135deg, #fffcf0 0%, #fffef9 100%) !important;
  border: 1px solid #f9f2d0 !important;
}

.memo-header {
  padding: 15px 20px;
  color: #856404;
  font-weight: 700;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.memo-item {
  padding: 10px 20px;
}


.stat-gradient-blue {
  background: linear-gradient(135deg, #e6f7ff 0%, #ffffff 100%);
  border: 1px solid #bae7ff;
}

.stat-gradient-green {
  background: linear-gradient(135deg, #f6ffed 0%, #ffffff 100%);
  border: 1px solid #d9f7be;
}

.stat-gradient-orange {
  background: linear-gradient(135deg, #fff7e6 0%, #ffffff 100%);
  border: 1px solid #ffe7ba;
}

.stat-card-vertical:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
}

.icon-blue {
  background: #e6f7ff;
  color: #1890ff;
}

.icon-green {
  background: #f6ffed;
  color: #52c41a;
}

.icon-orange {
  background: #fff7e6;
  color: #fa8c16;
}

.stat-icon-v {
  width: 54px;
  height: 54px;
  border-radius: 14px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 26px;
  margin-right: 18px;
}

.stat-gradient-blue .stat-icon-v {
  color: #1890ff;
}

.stat-gradient-green .stat-icon-v {
  color: #52c41a;
}

.stat-gradient-orange .stat-icon-v {
  color: #fa8c16;
}

.stat-info-v {
  flex: 1;
}

.stat-value-v {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-value-v .unit {
  font-size: 14px;
  font-weight: 400;
  color: #909399;
  margin-left: 2px;
}

.stat-label-v {
  font-size: 13px;
  color: #606266;
  margin-top: 4px;
}

/* 目标设置卡片美化 */
.goals-setting-card {
  margin-top: 24px;
  border-radius: 12px;
  border: none;
}

.card-header-small {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 0;
}

.card-header-small .title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.target-form-mini {
  padding-top: 5px;
}

.form-item-mini {
  margin-bottom: 15px;
}

.form-item-mini .label {
  font-size: 13px;
  color: #606266;
  margin-bottom: 6px;
  font-weight: 500;
}

.school-input :deep(.el-input__wrapper) {
  background-color: #f5f7fa;
  box-shadow: none !important;
  border: 1px solid #e4e7ed;
}

.school-input :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  background-color: #fff;
}

.score-input-wrapper-single {
  background: #fff0f0;
  border: 1px solid #fde2e2;
  border-radius: 8px;
  padding: 10px 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mini-input-large :deep(.el-input__wrapper) {
  padding: 0;
  box-shadow: none !important;
  background: transparent;
}

.mini-input-large :deep(.el-input__inner) {
  text-align: center;
  font-weight: 700;
  color: #f56c6c;
  font-size: 24px;
  height: 32px;
  line-height: 32px;
}

.unit {
  color: #f56c6c;
  font-size: 14px;
  margin-left: 4px;
  font-weight: 500;
}

/* 页面标题 */
.page-header {
  margin-bottom: 15px;
  padding-top: 15px;
}

.page-title {
  font-size: 24px;
  color: #303133;
  font-weight: 600;
  margin: 0 0 8px;
}

.page-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

/* 卡片通用动画 */
.animate-card {
  transition: all 0.3s ease;
  border: none;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.animate-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

/* 左侧卡片 */
.user-card {
  text-align: center;
  background: #fff;
  overflow: hidden;
}

.user-card-top {
  padding: 40px 20px 30px;
  background: linear-gradient(180deg, hsl(100, 53%, 90%) 0%, #ffffff 100%);
  position: relative;
  border-bottom: 1px solid #f5f7fa;
}

.role-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.role-badge.admin {
  background: #fef0f0;
  color: #f56c6c;
}

.role-badge:not(.admin) {
  background: #ecf5ff;
  color: #409eff;
}

.avatar-wrapper {
  position: relative;
  width: 100px;
  height: 100px;
  margin: 0 auto 15px;
  cursor: pointer;
  border-radius: 50%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s;
}

.avatar-wrapper:hover {
  transform: scale(1.05);
}

.avatar-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border-radius: 50%;
  opacity: 0;
  transition: all 0.3s;
}

.avatar-wrapper:hover .avatar-mask {
  opacity: 1;
}

.camera-icon {
  font-size: 20px;
  margin-bottom: 2px;
}

.upload-text {
  font-size: 12px;
}

.user-name {
  font-size: 20px;
  color: #303133;
  margin: 10px 0 5px;
  font-weight: 600;
}

.user-slogan {
  color: #909399;
  font-size: 13px;
  margin: 0 0 15px;
}

/* 倒计时徽章 */
.countdown-badge {
  display: inline-flex;
  align-items: baseline;
  background: #fff0f0;
  color: #f56c6c;
  padding: 4px 12px;
  border-radius: 20px;
  margin-top: 5px;
  font-size: 12px;
}

.countdown-badge .number {
  font-size: 16px;
  font-weight: bold;
  margin: 0 3px;
}

.countdown-badge .unit {
  font-size: 12px;
}

/* 个人信息列表 */
.user-bio {
  padding: 20px 25px;
}

.bio-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f5f7fa;
  font-size: 14px;
}

.bio-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.bio-item .label {
  color: #909399;
}

.bio-item .value {
  color: #303133;
  font-weight: 500;
}

.subject-tags {
  margin-top: 0;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: flex-end;
  flex: 1;
}

:deep(.el-card__header) {
  border-bottom: none;
  padding-bottom: 0;
}

.card-header {
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 15px;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-desc {
  font-size: 13px;
  color: #999;
  margin-left: 10px;
}

/* 自定义Tabs */
:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: #f0f0f0;
}

:deep(.el-tabs__item) {
  font-size: 15px;
  height: 45px;
  line-height: 45px;
  color: #606266;
}

:deep(.el-tabs__item.is-active) {
  color: #409eff;
  font-weight: 600;
}

.tab-content-wrapper {
  padding: 20px 0;
}

.security-wrapper {
  max-width: 400px;
}

/* 输入框和卡片统一宽度 */
.profile-form :deep(.el-input),
.profile-form :deep(.el-select) {
  width: 100%;
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  background: linear-gradient(90deg, #409eff, #36cfc9);
  border: none;
  box-shadow: 0 4px 14px rgba(64, 158, 255, 0.4);
  transition: all 0.3s;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.6);
  background: linear-gradient(90deg, #66b1ff, #5cdbd3);
}

.subject-selection-card {
  background: #fcfcfc;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 0;
  overflow: hidden;
  transition: border-color 0.3s;
}

.subject-selection-card:hover {
  border-color: #c0c4cc;
}

/* 标签页美化 */
:deep(.el-tabs__item.is-active) {
  font-size: 16px;
  color: #409eff;
  position: relative;
}

:deep(.el-tabs__item.is-active)::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 3px;
  background: #409eff;
  border-radius: 2px;
}

:deep(.el-tabs__active-bar) {
  display: none;
}

/* 复原科目选择卡片内部样式 */
.subject-group {
  padding: 15px 20px;
  display: flex;
  flex-direction: column;
}

.group-title {
  font-size: 12px;
  color: #909399;
  margin-bottom: 10px;
  font-weight: 600;
  text-transform: uppercase;
}

.subject-divider {
  height: 1px;
  background: #ebeef5;
  margin: 0;
}

.fixed-subject {
  margin-right: 0 !important;
  width: 100%;
  background-color: #f2f6fc;
}

:deep(.el-radio),
:deep(.el-checkbox) {
  margin-right: 15px;
  margin-bottom: 10px;
  height: 36px;
}

:deep(.el-radio.is-bordered),
:deep(.el-checkbox.is-bordered) {
  padding: 0 15px;
  border-radius: 4px;
  border-color: #dcdfe6;
}

:deep(.el-radio.is-bordered.is-checked) {
  background-color: #ecf5ff;
  border-color: #409eff;
}

.form-actions {
  margin-top: 30px;
  text-align: left;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .profile-content {
    padding: 20px 15px;
  }

  .left-col {
    margin-bottom: 20px;
  }

  .submit-btn {
    width: 100%;
  }

  :deep(.el-radio.is-bordered) {
    width: 100%;
    margin-right: 0;
  }
}
</style>
