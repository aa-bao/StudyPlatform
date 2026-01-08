<template>
  <div 
    v-show="visible" 
    class="scratch-pad" 
    ref="padRef"
    :style="{ top: `${top}px`, left: `${left}px` }"
  >
    <div class="pad-header" @mousedown="startDrag">
      <span>草稿纸 (拖拽移动)</span>
      <div class="actions">
        <el-button link size="small" @click="clearCanvas">清空</el-button>
        <el-button link size="small" @click="$emit('close')"><el-icon><Close /></el-icon></el-button>
      </div>
    </div>
    <div class="canvas-container">
      <canvas 
        ref="canvasRef" 
        width="400" 
        height="300"
        @mousedown="startDrawing"
        @mousemove="draw"
        @mouseup="stopDrawing"
        @mouseleave="stopDrawing"
      ></canvas>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, defineProps, defineEmits } from 'vue'
import { Close } from '@element-plus/icons-vue'

const props = defineProps({
  visible: Boolean
})

const emit = defineEmits(['close'])

const padRef = ref(null)
const canvasRef = ref(null)

// Dragging Logic
const top = ref(100)
const left = ref(100)
let isDragging = false
let dragOffset = { x: 0, y: 0 }

const startDrag = (e) => {
  isDragging = true
  dragOffset.x = e.clientX - left.value
  dragOffset.y = e.clientY - top.value
  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', stopDrag)
}

const onDrag = (e) => {
  if (!isDragging) return
  left.value = e.clientX - dragOffset.x
  top.value = e.clientY - dragOffset.y
}

const stopDrag = () => {
  isDragging = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
}

// Drawing Logic
let ctx = null
let isDrawing = false
let lastX = 0
let lastY = 0

onMounted(() => {
  if (canvasRef.value) {
    ctx = canvasRef.value.getContext('2d')
    ctx.strokeStyle = '#000'
    ctx.lineWidth = 2
    ctx.lineCap = 'round'
  }
})

const startDrawing = (e) => {
  isDrawing = true
  const rect = canvasRef.value.getBoundingClientRect()
  lastX = e.clientX - rect.left
  lastY = e.clientY - rect.top
}

const draw = (e) => {
  if (!isDrawing) return
  const rect = canvasRef.value.getBoundingClientRect()
  const x = e.clientX - rect.left
  const y = e.clientY - rect.top

  ctx.beginPath()
  ctx.moveTo(lastX, lastY)
  ctx.lineTo(x, y)
  ctx.stroke()
  
  lastX = x
  lastY = y
}

const stopDrawing = () => {
  isDrawing = false
}

const clearCanvas = () => {
  if (ctx && canvasRef.value) {
    ctx.clearRect(0, 0, canvasRef.value.width, canvasRef.value.height)
  }
}
</script>

<style scoped>
.scratch-pad {
  position: fixed;
  z-index: 2000;
  background: #fff8dc; /* Cornsilk color for paper feel */
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.pad-header {
  height: 36px;
  background: #fdf6ec;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 10px;
  cursor: move;
  user-select: none;
  font-size: 14px;
  color: #606266;
}

.canvas-container {
  cursor: crosshair;
  background: white;
}
</style>
