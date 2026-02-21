<template>
    <div class="calendar-heatmap">
        <div class="calendar-header">
            <h3 class="current-month">{{ currentYear }}年 {{ currentMonth + 1 }}月</h3>
            <div class="controls">
                <button @click="changeMonth(-1)" class="nav-btn"> &lt; 上个月 </button>
                <button @click="setToToday" class="nav-btn today-btn"> 今天 </button>
                <button @click="changeMonth(1)" class="nav-btn"> 下个月 &gt; </button>
            </div>
        </div>

        <div class="calendar-body">
            <div class="weekdays">
                <div v-for="day in ['日', '一', '二', '三', '四', '五', '六']" :key="day">{{ day }}</div>
            </div>

            <div class="days-grid">
                <div v-for="empty in firstDayOffset" :key="'empty-' + empty" class="day-cell empty"></div>

                <div v-for="date in daysInMonth" :key="date.fullDate" class="day-cell"
                    :class="{ 'is-today': date.isToday }" :style="{ backgroundColor: getColor(date.duration) }"
                    @mouseenter="showTooltip($event, date)" @mouseleave="hideTooltip">
                    <span class="day-number">{{ date.day }}</span>
                </div>
            </div>
        </div>

        <div class="calendar-footer">
            <span>学习时长越深颜色越深</span>
            <div class="legend">
                <div v-for="c in colors" :key="c" :style="{ backgroundColor: c }" class="legend-box"></div>
            </div>
        </div>

        <div v-show="tooltip.show" class="tooltip" :style="tooltip.style">
            <div class="tooltip-date">{{ tooltip.date }}</div>
            <div>学习: <strong>{{ tooltip.duration }}</strong> 分钟</div>
        </div>
    </div>
</template>

 <script setup>
 import { ref, computed, onMounted } from 'vue';
 import { getStudyHeatmap } from '@/api/userProgress';
 import { ElMessage } from 'element-plus';

 const props = defineProps({
     userId: { type: [String, Number], required: true }
 });

// 颜色配置（蓝色调）
const colors = ['#f5f7fa', '#d1e9ff', '#71bcff', '#1890ff', '#003a8c'];
const rawData = ref({}); // 格式: { '2026-01-29': 120 }

// 状态管理
const viewDate = ref(new Date());
const currentYear = computed(() => viewDate.value.getFullYear());
const currentMonth = computed(() => viewDate.value.getMonth());

const tooltip = ref({ show: false, date: '', duration: 0, style: {} });

// --- 计算属性：生成当月数据 ---
const daysInMonth = computed(() => {
    const days = [];
    const year = currentYear.value;
    const month = currentMonth.value;

    // 获取当月天数
    const totalDays = new Date(year, month + 1, 0).getDate();
    const todayStr = new Date().toISOString().split('T')[0];

    for (let i = 1; i <= totalDays; i++) {
        const fullDate = `${year}-${String(month + 1).padStart(2, '0')}-${String(i).padStart(2, '0')}`;
        days.push({
            day: i,
            fullDate,
            duration: rawData.value[fullDate] || 0,
            isToday: fullDate === todayStr
        });
    }
    return days;
});

// 计算当月 1 号是星期几 (0-6)
const firstDayOffset = computed(() => {
    return new Date(currentYear.value, currentMonth.value, 1).getDay();
});

// --- 方法 ---
const changeMonth = (delta) => {
    const newDate = new Date(viewDate.value);
    newDate.setMonth(newDate.getMonth() + delta);
    viewDate.value = newDate;
    fetchData(); // 切换月份后重新加载数据
};

const setToToday = () => {
    viewDate.value = new Date();
    fetchData();
};

const getColor = (dur) => {
    if (dur <= 0) return colors[0];
    if (dur < 30) return colors[1];
    if (dur < 60) return colors[2];
    if (dur < 120) return colors[3];
    return colors[4];
};

const showTooltip = (e, date) => {
    const rect = e.target.getBoundingClientRect();
    tooltip.value = {
        show: true,
        date: date.fullDate,
        duration: date.duration,
        style: {
            left: `${rect.left + 15}px`,
            top: `${rect.top - 50}px`
        }
    };
};

const hideTooltip = () => { tooltip.value.show = false; };

const fetchData = async () => {
    try {
        const response = await getStudyHeatmap(props.userId, 180);
        if (response.code === 200 && response.data) {
            const data = response.data;
            const formattedData = {};
            if (Array.isArray(data)) {
                data.forEach(item => {
                    if (item.recordDate && item.totalDuration) {
                        formattedData[item.recordDate] = Math.round(item.totalDuration / 60);
                    }
                });
            }
            rawData.value = formattedData;
        } else {
            rawData.value = {};
        }
    } catch (error) {
        console.error('获取热力图数据失败:', error);
        ElMessage.error('获取学习数据失败');
        rawData.value = {};
    }
};

onMounted(fetchData);
</script>

<style scoped>
.calendar-heatmap {
    width: 350px;
    background: #fff;
    border-radius: 12px;
    padding: 20px;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.calendar-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.current-month {
    margin: 0;
    font-size: 16px;
    color: #333;
}

.nav-btn {
    background: none;
    border: 1px solid #eee;
    padding: 4px 8px;
    border-radius: 4px;
    cursor: pointer;
    font-size: 12px;
    color: #666;
    transition: all 0.2s;
}

.nav-btn:hover {
    background: #f0f7ff;
    border-color: #1890ff;
    color: #1890ff;
}

.today-btn {
    margin: 0 5px;
}

.weekdays {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    text-align: center;
    font-size: 12px;
    color: #999;
    margin-bottom: 10px;
}

.days-grid {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    gap: 6px;
}

.day-cell {
    aspect-ratio: 1;
    /* 保持正方形 */
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    position: relative;
    transition: transform 0.2s;
}

.day-cell:not(.empty):hover {
    transform: translateY(-2px);
    filter: brightness(0.9);
}

.day-number {
    font-size: 10px;
    color: rgba(0, 0, 0, 0.3);
}

.is-today {
    box-shadow: inset 0 0 0 2px #1890ff;
}

.calendar-footer {
    margin-top: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 11px;
    color: #999;
}

.legend {
    display: flex;
    gap: 3px;
}

.legend-box {
    width: 10px;
    height: 10px;
    border-radius: 2px;
}

.tooltip {
    position: fixed;
    background: #333;
    color: white;
    padding: 8px 12px;
    border-radius: 6px;
    font-size: 12px;
    z-index: 100;
    pointer-events: none;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}
</style>