<template>
  <div class="exam-app" ref="examAppRef">
    <!-- 导航栏 -->
    <nav class="system-nav">
      <div class="nav-left">
        <div class="logo-group">
          <span class="logo-group-eng">KaoYan Platform </span>
          <span class="logo-group-ch">考研真题模考系统</span>
        </div>
        <span class="ai-tag">AI ENHANCED</span>
      </div>

      <div class="nav-right">
        <div class="top-tools">
          <button @click="openDraft" class="nav-tool-btn">📝 草稿纸</button>
          <button @click="askAIHelp" class="nav-tool-btn ai-btn">✨ AI 建议</button>
        </div>

        <div class="timer-box">
          <button @click="toggleFullScreen" class="fullscreen-btn">
            <span class="icon">全屏</span>
          </button>
          <span class="timer-label">倒计时</span>
          <span class="timer-value">{{ formatTime }}</span>
        </div>
        <button @click="handleSubmit" class="submit-btn">提交试卷</button>
      </div>
    </nav>

    <!-- 试卷 -->
    <div class="main-layout">
      <div class="paper-wrapper">
        <div class="paper-page-container" :class="{ 'submitted-mask': isSubmitted }">

          <div class="paper-sheet shadow-effect">
            <div class="sealing-line">
              <span class="seal-warning">密 封 线 内 不 要 答 题</span>
            </div>

            <header class="paper-header">
              <div class="confidential-mark">绝密 <span class="star">★</span> 启用前</div>
              <h1 class="main-title">2025年全国硕士研究生招生考试数学（一）真题</h1>
              <div class="title-divider"></div>
              <div class="exam-info">
                <span>考试时间：180分钟</span>
                <span>满分：150分</span>
              </div>
              <div class="notice-box">
                <p class="notice-title">考生注意事项：</p>
                <ol class="notice-list">
                  <li>答题前，考生务必将自己的姓名、准考证号填写在答题卡上。</li>
                  <li>选择题每小题选出答案后，请在系统中点击对应选项。</li>
                  <li>主观题请在输入框内作答，或使用草稿纸记录思路。</li>
                </ol>
              </div>
            </header>

            <!-- 选择题 -->
            <div class="section-banner">一、选择题：1～10小题，每小题5分，共50分。</div>
            <div v-for="q in selectionQuestions.slice(0, 5)" :key="q.id" :id="'q' + q.id" class="question-item">
              <p class="question-title" v-html="q.title"></p>
              <div class="options-grid">
                <label v-for="opt in ['A', 'B', 'C', 'D']" :key="opt" class="option-label">
                  <input type="radio" :name="'q' + q.id" :value="opt" v-model="answers[q.id]" @change="markDone(q.id)">
                  <span class="option-text">({{ opt }}) {{ q.options ? q.options[opt] : '[内容加载中...]' }}</span>
                </label>
              </div>
            </div>
            <footer class="paper-footer">数学（一） 第 1 页（共 3 页）</footer>
          </div>

          <div class="paper-sheet shadow-effect">
            <div v-for="q in selectionQuestions.slice(5, 10)" :key="q.id" :id="'q' + q.id" class="question-item">
              <p class="question-title" v-html="q.title"></p>
              <div class="options-grid">
                <label v-for="opt in ['A', 'B', 'C', 'D']" :key="opt" class="option-label">
                  <input type="radio" :name="'q' + q.id" :value="opt" v-model="answers[q.id]" @change="markDone(q.id)">
                  <span class="option-text">({{ opt }}) {{ q.options ? q.options[opt] : '[内容加载中...]' }}</span>
                </label>
              </div>
            </div>

            <!-- 填空题 -->
            <div class="completion-banner">二、填空题：11～16小题。</div>
            <div v-for="q in subjectiveQuestions.slice(0, 3)" :key="q.id" :id="'q' + q.id" class="question-item">
              <p class="question-title" v-html="q.title"></p>
              <textarea v-model="answers[q.id]" @input="markDone(q.id)" class="answer-area" rows="4"
                placeholder="请输入你的答案..."></textarea>
            </div>
            <footer class="paper-footer">数学（一） 第 2 页（共 3 页）</footer>
          </div>

          <!-- 解答题 -->
          <div class="paper-sheet shadow-effect">
            <div v-for="q in subjectiveQuestions.slice(3)" :key="q.id" :id="'q' + q.id" class="question-item">
              <p class="question-title" v-html="q.title"></p>
              <textarea v-model="answers[q.id]" @input="markDone(q.id)" class="answer-area" rows="4"
                placeholder="请输入你的答案..."></textarea>
            </div>
            <footer class="paper-footer">数学（一） 第 3 页（共 3 页）</footer>
          </div>
        </div>

        <div v-if="isSubmitted" class="ai-report-container">
          <h2 class="report-header">✨ AI 智能阅卷报告</h2>
          <div v-html="aiResultHtml"></div>
        </div>
      </div>

      <aside class="sidebar-container" :class="{ 'is-hidden': !showSidebar }" :style="sidebarStyle"
        @mousedown="handleDragStart">
        <div class="toggle-handler" @click.stop="toggleSidebar">
          {{ showSidebar ? '▶' : '◀' }}
          <span class="toggle-text">答题卡</span>
        </div>

        <div class="sidebar-card card-glow">
          <h3 class="card-title" style="cursor: move;">
            <span>答题状态</span>
            <span class="count-tag">{{ doneCount }} / 22</span>
          </h3>
          <div class="answer-card-grid">
            <div v-for="i in 22" :key="i" @click.stop="scrollToQuestion(i)" class="card-number"
              :class="{ 'is-done': doneSet.has(i) }">{{ i }}</div>
          </div>
        </div>
      </aside>
    </div>

    <transition name="fade">
      <div v-show="showDraft" class="full-page-draft">
        <div class="draft-toolbar">
          <div class="tool-group">
            <label>颜色：</label>
            <input type="color" v-model="brushConfig.color">
          </div>
          <div class="tool-group">
            <label>粗细：</label>
            <input type="range" min="1" max="10" v-model="brushConfig.size">
          </div>
          <div class="divider"></div>
          <button @click="undoLast" class="draft-btn">↩️ 撤销</button>
          <button @click="clearCanvas" class="draft-btn danger">🗑️ 清空</button>
          <button @click="showDraft = false" class="draft-btn primary">关闭</button>
        </div>

        <canvas ref="draftCanvas" class="draft-canvas" @mousedown="startDraw" @mousemove="draw" @mouseup="endDraw"
          @mouseleave="endDraw"></canvas>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, nextTick, watch } from 'vue';

// --- 1. 状态数据 ---
const examAppRef = ref(null);
const isFullScreen = ref(false);
const isSubmitted = ref(false);
const aiResultHtml = ref('');
const switchCount = ref(0);

// 考试时长常量 (180分钟)
const EXAM_DURATION = 180 * 60;
// 当前剩余秒数 (响应式)
const secondsLeft = ref(EXAM_DURATION);
// 计时器引用，用于销毁
let timerInterval = null;

const doneSet = reactive(new Set()); // 已答题目的 ID 集合
const answers = reactive({});        // 存储答案的键值对 { questionId: answer }

// --- 2. 逻辑：可拖动侧边栏 (答题卡) ---
const showSidebar = ref(true);
const position = reactive({ x: window.innerWidth - 300, y: 120 });
const isDragging = ref(false);
const lastPositionX = ref(0);

const toggleSidebar = () => {
  if (showSidebar.value) {
    lastPositionX.value = position.x;
    position.x = window.innerWidth; // 隐藏到边缘，只留拉手
    showSidebar.value = false;
  } else {
    position.x = lastPositionX.value; // 恢复位置
    if (position.x > window.innerWidth - 260) position.x = window.innerWidth - 260;
    showSidebar.value = true;
  }
};

const sidebarStyle = computed(() => ({
  left: `${position.x}px`,
  top: `${position.y}px`,
  // 拖动时不使用过渡动画，切换隐藏时使用
  transition: isDragging.value ? 'none' : 'left 0.4s cubic-bezier(0.4, 0, 0.2, 1), top 0.4s cubic-bezier(0.4, 0, 0.2, 1)'
}));

// 拖动逻辑
const handleDragStart = (e) => {
  if (e.target.closest('.card-number') || e.target.closest('.toggle-handler')) return;
  isDragging.value = true;
  let startX = e.clientX - position.x;
  let startY = e.clientY - position.y;

  const handleMouseMove = (e) => {
    if (!isDragging.value) return;
    position.x = e.clientX - startX;
    position.y = e.clientY - startY;
  };

  const handleMouseUp = () => {
    isDragging.value = false;
    // 边界限制
    if (position.x < 0) position.x = 0;
    if (position.x > window.innerWidth - 260) position.x = window.innerWidth - 260;
    if (position.y < 0) position.y = 0;
    if (position.y > window.innerHeight - 100) position.y = window.innerHeight - 100;
    window.removeEventListener('mousemove', handleMouseMove);
    window.removeEventListener('mouseup', handleMouseUp);
  };
  window.addEventListener('mousemove', handleMouseMove);
  window.addEventListener('mouseup', handleMouseUp);
};

// --- 3. 题目数据 ---
const selectionQuestions = ref([
  { id: 1, title: '1. 设函数 $f(x) = \\lim_{n \\to \\infty} \\frac{x^{2n-1} + ax^2 + bx}{x^{2n} + 1}$ 在 $(-\\infty, +\\infty)$ 内连续，则（ ）', options: { A: '$a=1, b=1$', B: '$a=1, b=-1$', C: '$a=-1, b=1$', D: '$a=-1, b=-1$' } },
  ...Array.from({ length: 9 }, (_, i) => ({ id: i + 2, title: `第 ${i + 2} 题：[选择题内容加载中...]` }))
]);
const subjectiveQuestions = ref(Array.from({ length: 12 }, (_, i) => ({ id: i + 11, title: `${i + 11}. 考研数学解答/填空题示例题目内容...` })));

// --- 4. 逻辑：倒计时与持久化 ---

// 格式化时间显示 (mm:ss)
const formatTime = computed(() => {
  const m = Math.floor(secondsLeft.value / 60);
  const s = secondsLeft.value % 60;
  return `${m}:${s < 10 ? '0' : ''}${s}`;
});

const initTimer = () => {
  let endTime = localStorage.getItem('exam_end_time');
  if (!endTime) {
    endTime = Date.now() + EXAM_DURATION * 1000;
    localStorage.setItem('exam_end_time', endTime);
  }

  const updateTimer = () => {
    const now = Date.now();
    const remaining = Math.max(0, Math.floor((endTime - now) / 1000));
    secondsLeft.value = remaining; // 修正变量名

    if (remaining <= 0) {
      clearInterval(timerInterval);
      handleSubmit(true); // 时间到自动提交
    }
  };

  timerInterval = setInterval(updateTimer, 1000);
  updateTimer();
};

// --- 5. 逻辑：草稿纸 (全屏画布) ---
const showDraft = ref(false);
const draftCanvas = ref(null);
const ctx = ref(null);
const isDrawing = ref(false);
const strokes = ref([]);
const currentStroke = ref(null);
const brushConfig = reactive({ color: '#333333', size: 3 });

const openDraft = () => {
  showDraft.value = true;
  nextTick(() => {
    const canvas = draftCanvas.value;
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
    ctx.value = canvas.getContext('2d');
    redrawCanvas();
  });
};

const startDraw = (e) => {
  isDrawing.value = true;
  currentStroke.value = { color: brushConfig.color, size: brushConfig.size, points: [{ x: e.clientX, y: e.clientY }] };
};

const draw = (e) => {
  if (!isDrawing.value) return;
  const point = { x: e.clientX, y: e.clientY };
  currentStroke.value.points.push(point);
  drawStroke(currentStroke.value);
};

const endDraw = () => {
  if (isDrawing.value) {
    strokes.value.push(currentStroke.value);
    currentStroke.value = null;
  }
  isDrawing.value = false;
};

const drawStroke = (stroke) => {
  if (!stroke || !stroke.points.length) return;
  const c = ctx.value;
  c.beginPath();
  c.strokeStyle = stroke.color;
  c.lineWidth = stroke.size;
  c.lineCap = 'round';
  c.lineJoin = 'round';
  c.moveTo(stroke.points[0].x, stroke.points[0].y);
  stroke.points.forEach(p => c.lineTo(p.x, p.y));
  c.stroke();
};

const redrawCanvas = () => {
  if (!ctx.value) return;
  ctx.value.clearRect(0, 0, draftCanvas.value.width, draftCanvas.value.height);
  strokes.value.forEach(drawStroke);
};

const undoLast = () => { strokes.value.pop(); redrawCanvas(); };
const clearCanvas = () => { if (confirm('确定清空草稿吗？')) { strokes.value = []; redrawCanvas(); } };

// --- 6. 交互逻辑 ---

const markDone = (id) => {
  if (answers[id] && String(answers[id]).trim() !== '') {
    doneSet.add(Number(id));
  } else {
    doneSet.delete(Number(id));
  }
};

const scrollToQuestion = (id) => {
  const el = document.getElementById('q' + id);
  if (el) el.scrollIntoView({ behavior: 'smooth', block: 'center' });
};

const toggleFullScreen = () => {
  if (!document.fullscreenElement) {
    examAppRef.value.requestFullscreen().then(() => isFullScreen.value = true);
  } else {
    document.exitFullscreen();
    isFullScreen.value = false;
  }
};

const handleSubmit = (isAuto = false) => {
  if (!isAuto && !confirm("确定提交试卷吗？")) return;

  isSubmitted.value = true;
  // 清理缓存
  localStorage.removeItem('exam_end_time');
  localStorage.removeItem('exam_answers');
  localStorage.removeItem('exam_draft_strokes');
};

const handleVisibilityChange = () => {
  if (document.hidden) switchCount.value++;
};

// --- 7. 生命周期与监听 ---

// 自动保存答案和草稿
watch(answers, (newVal) => localStorage.setItem('exam_answers', JSON.stringify(newVal)), { deep: true });
watch(strokes, (newVal) => localStorage.setItem('exam_draft_strokes', JSON.stringify(newVal)), { deep: true });

onMounted(() => {
  initTimer(); // 初始化计时器

  // 恢复答案
  const savedAnswers = localStorage.getItem('exam_answers');
  if (savedAnswers) {
    Object.assign(answers, JSON.parse(savedAnswers));
    Object.keys(answers).forEach(id => {
      if (answers[id]) doneSet.add(Number(id));
    });
  }

  // 恢复草稿
  const savedStrokes = localStorage.getItem('exam_draft_strokes');
  if (savedStrokes) strokes.value = JSON.parse(savedStrokes);

  document.addEventListener('visibilitychange', handleVisibilityChange);
});

onUnmounted(() => {
  if (timerInterval) clearInterval(timerInterval);
  document.removeEventListener('visibilitychange', handleVisibilityChange);
});

const doneCount = computed(() => doneSet.size);
const askAIHelp = () => alert("AI 提示：重点检查解答题的第二步逻辑。");
</script>


<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@400;700;900&display=swap');

/* 页面大容器 */
.exam-app {
  min-height: 100vh;
  background-color: #f0f2f5;
  width: 100%;
  overflow-y: auto;
}

/* 顶部栏 */
.system-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  height: 64px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);  /* 毛玻璃效果 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border-bottom: 1px solid #eaeaea;
  position: fixed;
  width: 100%;
  top: 0;
  z-index: 1000;
}

/* 左侧 Logo 区域 */
.nav-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.logo-group {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}
               
.logo-group-eng {
  font-size: 18px;
  font-weight: 800;
  letter-spacing: 0.5px;
  color: #1a1a1a;
  font-family: 'Inter', sans-serif;
}

.logo-group-ch {
  font-size: 12px;
  color: #666;
  letter-spacing: 2px;
}

/* AI Tag 标签 */
.ai-tag {
  background: linear-gradient(135deg, #6366f1 0%, #a855f7 100%);
  color: white;
  font-size: 10px;
  font-weight: bold;
  padding: 2px 8px;
  border-radius: 4px;
  letter-spacing: 0.5px;
  box-shadow: 0 2px 4px rgba(99, 102, 241, 0.3);
}

/* 右侧工具区域 */
.nav-right {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-right: 50px;
  font-size: 14px;
}

/* 导航栏工具组 */
.top-tools {
  display: flex;
  gap: 12px;
  margin-right: 20px;
  border-right: 1px solid #eee;
  padding-right: 20px;
}

.nav-tool-btn {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 13px;
  color: #475569;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 4px;
}

.nav-tool-btn:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
  color: #1e293b;
}

/* AI 按钮特殊处理 */
.nav-tool-btn.ai-btn {
  background: linear-gradient(135deg, #f5f3ff 0%, #ede9fe 100%);
  border-color: #ddd6fe;
  color: #6d28d9;
  font-weight: 500;
}

.nav-tool-btn.ai-btn:hover {
  box-shadow: 0 2px 4px rgba(109, 40, 217, 0.1);
  transform: translateY(-1px);
}

/* 计时器盒子 */
.timer-box {
  display: flex;
  align-items: center;
  background: #f4f4f7;
  padding: 6px 16px;
  border-radius: 20px;
  border: 1px solid #e5e7eb;
  height: 30px;
}

.timer-label {
  font-size: 13px;
  color: #6b7280;
  margin-right: 8px;
  white-space: nowrap;
}

.timer-value {
  font-size: 18px;
  font-weight: bold;
  color: #ef4444;
  text-align: center;
}

/* 按钮基础样式 */
button {
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
  outline: none;
}

/* 全屏按钮 */
.fullscreen-btn {
  background: none;
  border: none;
  color: #64748b;
  font-size: 14px;
  padding: 0 8px;
  margin-right: 8px;
  border-right: 1px solid #e2e8f0;
  cursor: pointer;
  display: flex;
  align-items: center;
  height: 100%;
}

.fullscreen-btn:hover {
  background: #e5e7eb;
  color: #111827;
}

/* 提交按钮 */
.submit-btn {
  background: #2563eb;
  color: white;
  padding: 8px 20px;
  border-radius: 6px;
  font-weight: 600;
  font-size: 14px;
  box-shadow: 0 4px 6px -1px rgba(37, 99, 235, 0.2);
}

.submit-btn:hover {
  background: #1d4ed8;
  transform: translateY(-1px);
  box-shadow: 0 6px 12px -2px rgba(37, 99, 235, 0.3);
}

.submit-btn:active {
  transform: translateY(0);
}

/* 基础样式还原 */
.main-layout {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  gap: 24px;
  padding: 24px;
}

.paper-wrapper {
  margin: 0 auto;
  display: flex;
  gap: 24px;
  padding: 24px;
}


/* 试卷分页效果 */
.paper-page-container {
  display: flex;
  flex-direction: column;
  gap: 30px;
  padding-bottom: 50px;
}


.paper-sheet {
  background: white;
  width: 900px;
  min-height: 1100px;
  margin: 0 auto;
  margin-top: 40px;
  padding: 30mm 20mm 20mm 35mm;
  position: relative;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);
  font-family: 'Noto Serif SC', serif;
}

/* 密封线 */
.sealing-line {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 5px;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  padding: 30px;
  background-color: #fff;
  border-right: 2px dashed #555;
  box-sizing: border-box;
  z-index: 10;
}

/* “密封线内不要答题” 警告文字 */
.seal-warning {
  writing-mode: vertical-rl;
  font-size: 14px;
  font-weight: bold;
  color: #666;
  letter-spacing: 20px;
  padding: 10px;
}

/* 侧边栏容器 */
.sidebar-container {
  position: fixed;
  z-index: 2000;
  width: 260px;
  transition: left 0.4s cubic-bezier(0.4, 0, 0.2, 1), top 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  user-select: none;
}

/* 切换侧边栏的拉手 */
.toggle-handler {
  position: absolute;
  left: -30px; /* 露出一截在外面 */
  top: 50%;
  transform: translateY(-50%);
  width: 30px;
  height: 100px;
  background: #2563eb;
  color: white;
  border-radius: 8px 0 0 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: -2px 0 5px rgba(0,0,0,0.1);
}

.toggle-text {
  writing-mode: vertical-lr;
  font-size: 12px;
  margin-top: 5px;
  letter-spacing: 2px;
}

.sidebar-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

/* 拖动时的临时状态 */
.is-dragging {
  transition: none !important; /* 拖动时关闭动画，防止卡顿 */
  opacity: 0.9;
}

/* 通用卡片样式 */
.sidebar-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #eef2f6;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

/* 答题卡标题 */
.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.count-tag {
  font-size: 12px;
  background: #f1f5f9;
  color: #64748b;
  padding: 2px 8px;
  border-radius: 10px;
  font-family: monospace;
}

/* 答题卡网格 - 考研答题卡风格 */
.answer-card-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
}

.card-number {
  height: 32px;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 13px;
  color: #64748b;
  transition: all 0.2s ease;
}

.card-number:hover {
  border-color: #2563eb;
  color: #2563eb;
  background: #eff6ff;
}

.card-number.is-done {
  background: #2563eb;
  border-color: #2563eb;
  color: white;
  font-weight: bold;
  box-shadow: 0 2px 4px rgba(37, 99, 235, 0.2);
}


/* 考试时间、分值 */
.exam-info {
  display: flex;
  justify-content: center;
  gap: 40px;
  font-size: 16px;
  margin-bottom: 30px;
  color:#636363;
  font-style: italic;
}

/* 考试注意事外外部容器 */
.notice-box {
  border: 2px solid black;
  margin: 20px auto;
  padding: 5px 15px;
  text-align: left;
  font-size: 14px;
  line-height: 1.6;
}

/* 考试注意事项 */
.notice-title{
  font-weight: bold;
  margin-left: 0;
  padding-left: 0;
}

.notice-list {
  /* 清除默认内边距 */
  padding-left: 1.2em;
  margin: 0;
  list-style-position: outside;
  margin-bottom: 10px;
}

/* 选择题名称栏 */
.section-banner {
  background: #eef3f8;
  padding: 8px;
  font-weight: bold;
}

/* 题目单项容器 */
.question-item {
  padding: 5px;
  transition: background-color 0.3s;
  border-radius: 10px;
}

/* 题干样式 */
.question-title {
  font-size: 16px;
  line-height: 1.6;
  color: #1a1a1a;
  margin-bottom: 16px;
  font-weight: 500;
  display: flex;
}

/* 选项网格布局 - 考研试卷通常是 1列或 2列 */
.options-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px 24px;
  padding-left: 10px;
}

/* 选项标签容器 */
.option-label {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  cursor: pointer;
  padding: 10px 14px;
  border: 1px solid transparent;
  border-radius: 6px;
  background-color: #f9fafb;
  transition: all 0.2s ease;
}

.option-label:hover {
  background-color: #f3f4f6;
  border-color: #d1d5db;
}

/* 选中状态样式 */
.option-label:has(input:checked) {
  background-color: #eff6ff;
  border-color: #3b82f6;
  color: #1d4ed8;
  font-weight: 600;
}

.option-label input[type="radio"] {
  margin-top: 4px;
  cursor: pointer;
  accent-color: #2563eb;
}

/* 选项文字 */
.option-text {
  font-size: 15px;
  line-height: 1.5;
  color: inherit;
}

/* 填空题名称栏 */
.completion-banner{
  background: #eef3f8;
  padding: 8px;
  margin-top: 20px;
  font-weight: bold;  
}

.answer-area {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
}

.full-page-draft {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 3000;
  /* 高于导航栏和答题卡 */
  background: rgba(255, 255, 255, 0.4);
  /* 半透明背景，能看到试卷 */
  backdrop-filter: blur(2px);
}

.draft-toolbar {
  position: absolute;
  top: 80px;
  /* 避开导航栏位置 */
  left: 50%;
  transform: translateX(-50%);
  background: white;
  padding: 10px 20px;
  border-radius: 30px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  z-index: 3001;
}

.draft-canvas {
  cursor: crosshair;
  /* 十字准星 */
}

.draft-btn {
  padding: 6px 12px;
  border-radius: 6px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  font-size: 13px;
}

.draft-btn:hover {
  background: #f4f4f4;
}

.draft-btn.primary {
  background: #2563eb;
  color: white;
  border: none;
}

.draft-btn.danger {
  color: #ef4444;
}

</style>


