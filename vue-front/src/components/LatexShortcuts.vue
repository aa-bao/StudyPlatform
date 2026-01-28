<template>
  <div class="latex-shortcuts">
    <div class="shortcuts-header">
      <span class="title">LaTeX 快捷输入</span>
      <el-popover placement="right" width="500" trigger="click">
        <template #reference>
          <el-button type="primary" size="small" text>
            <el-icon><QuestionFilled /></el-icon>
            帮助
          </el-button>
        </template>
        <div class="latex-help">
          <h4>LaTeX 公式快速入门</h4>
          <div class="help-section">
            <h5>基础语法</h5>
            <ul>
              <li><code>行内公式</code>：$公式$ （如：$e^{2}$）</li>
              <li><code>块级公式</code>：$$公式$$ （独立一行显示）</li>
            </ul>
          </div>
          <div class="help-section">
            <h5>常用符号</h5>
            <ul>
              <li><code>分数</code>：\frac{分子}{分母} （如：\frac{1}{2} → ½）</li>
              <li><code>上标</code>：^{指数} （如：x^{2} → x²）</li>
              <li><code>下标</code>：_{下标} （如：x_{1} → x₁）</li>
              <li><code>根号</code>：\sqrt{内容} （如：\sqrt{x} → √x）</li>
              <li><code>求和</code>：\sum_{i=1}^{n} （∑）</li>
              <li><code>积分</code>：\int_{a}^{b} （∫）</li>
              <li><code>极限</code>：\lim_{x \to \infty} （lim）</li>
            </ul>
          </div>
          <div class="help-section">
            <h5>常用符号</h5>
            <ul>
              <li><code>\pi</code> → π</li>
              <li><code>\infty</code> → ∞</li>
              <li><code>\alpha</code> → α, <code>\beta</code> → β, <code>\theta</code> → θ</li>
              <li><code>\geq</code> → ≥, <code>\leq</code> → ≤, <code>\neq</code> → ≠</li>
              <li><code>\approx</code> → ≈, <code>\equiv</code> ≡</li>
            </ul>
          </div>
          <div class="help-section">
            <h5>示例</h5>
            <ul>
              <li>二次公式：$\frac{-b \pm \sqrt{b^2-4ac}}{2a}$</li>
              <li>定积分：$\int_{0}^{\infty} e^{-x^2} dx$</li>
              <li>极限：$\lim_{n \to \infty} (1 + \frac{1}{n})^n = e$</li>
            </ul>
          </div>
        </div>
      </el-popover>
    </div>

    <div class="shortcuts-grid">
      <!-- 常用符号 -->
      <div class="shortcut-group">
        <div class="group-title">常用符号</div>
        <div class="buttons-row">
          <button @click="insert('\\pi')" class="shortcut-btn">π</button>
          <button @click="insert('\\infty')" class="shortcut-btn">∞</button>
          <button @click="insert('\\alpha')" class="shortcut-btn">α</button>
          <button @click="insert('\\beta')" class="shortcut-btn">β</button>
          <button @click="insert('\\theta')" class="shortcut-btn">θ</button>
          <button @click="insert('\\lambda')" class="shortcut-btn">λ</button>
          <button @click="insert('\\mu')" class="shortcut-btn">μ</button>
          <button @click="insert('\\sigma')" class="shortcut-btn">σ</button>
        </div>
      </div>

      <!-- 运算符 -->
      <div class="shortcut-group">
        <div class="group-title">运算符</div>
        <div class="buttons-row">
          <button @click="insert('\\frac{}{}')" class="shortcut-btn" title="分数">分</button>
          <button @click="insert('\\sqrt{}')" class="shortcut-btn" title="根号">√</button>
          <button @click="insert('^{}')" class="shortcut-btn" title="上标">xⁿ</button>
          <button @click="insert('_{}')" class="shortcut-btn" title="下标">xₙ</button>
          <button @click="insert('\\pm')" class="shortcut-btn">±</button>
          <button @click="insert('\\times')" class="shortcut-btn">×</button>
          <button @click="insert('\\div')" class="shortcut-btn">÷</button>
          <button @click="insert('\\cdot')" class="shortcut-btn">·</button>
        </div>
      </div>

      <!-- 关系符 -->
      <div class="shortcut-group">
        <div class="group-title">关系符</div>
        <div class="buttons-row">
          <button @click="insert('\\geq')" class="shortcut-btn">≥</button>
          <button @click="insert('\\leq')" class="shortcut-btn">≤</button>
          <button @click="insert('\\neq')" class="shortcut-btn">≠</button>
          <button @click="insert('\\approx')" class="shortcut-btn">≈</button>
          <button @click="insert('\\equiv')" class="shortcut-btn">≡</button>
          <button @click="insert('\\in')" class="shortcut-btn">∈</button>
          <button @click="insert('\\subset')" class="shortcut-btn">⊂</button>
          <button @click="insert('\\to')" class="shortcut-btn">→</button>
        </div>
      </div>

      <!-- 微积分 -->
      <div class="shortcut-group">
        <div class="group-title">微积分</div>
        <div class="buttons-row">
          <button @click="insert('\\int_{}^{}')" class="shortcut-btn" title="积分">∫</button>
          <button @click="insert('\\sum_{}^{}')" class="shortcut-btn" title="求和">∑</button>
          <button @click="insert('\\prod_{}^{}')" class="shortcut-btn" title="乘积">∏</button>
          <button @click="insert('\\lim_{x \\to \\infty}')" class="shortcut-btn" title="极限">lim</button>
          <button @click="insert('\\partial')" class="shortcut-btn" title="偏导数">∂</button>
          <button @click="insert('\\nabla')" class="shortcut-btn" title="梯度">∇</button>
          <button @click="insert('\\mathrm{d}x')" class="shortcut-btn">dx</button>
        </div>
      </div>

      <!-- 括号 -->
      <div class="shortcut-group">
        <div class="group-title">括号</div>
        <div class="buttons-row">
          <button @click="insert('\\left( \\right)')" class="shortcut-btn">( )</button>
          <button @click="insert('\\left[ \\right]')" class="shortcut-btn">[ ]</button>
          <button @click="insert('\\left\\{ \\right\\}')" class="shortcut-btn">{ }</button>
          <button @click="insert('\\langle \\rangle')" class="shortcut-btn">⟨ ⟩</button>
          <button @click="insert('|')" class="shortcut-btn">|</button>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="shortcut-group actions">
        <button @click="clearInput" class="action-btn clear-btn">
          <el-icon><Delete /></el-icon>
          清空
        </button>
        <button @click="wrapInline" class="action-btn">
          <el-icon><MagicStick /></el-icon>
              行内公式
          </button>
        <button @click="wrapBlock" class="action-btn">
          <el-icon><MagicStick /></el-icon>
          $$块级公式$$
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { QuestionFilled, Delete, MagicStick } from '@element-plus/icons-vue'

const props = defineProps({
  targetSelector: {
    type: String,
    default: 'textarea:focus, input:focus'
  }
})

const emit = defineEmits(['insert'])

const insert = (latex) => {
  emit('insert', latex)
}

const clearInput = () => {
  emit('clear')
}

const wrapInline = () => {
  emit('wrap', '$')
}

const wrapBlock = () => {
  emit('wrap', '$$')
}
</script>

<style scoped>
.latex-shortcuts {
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  padding: 15px;
  background-color: #fafafa;
  margin-top: 10px;
}

.shortcuts-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.title {
  font-weight: bold;
  color: #303133;
  font-size: 14px;
}

.shortcuts-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.shortcut-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.group-title {
  flex-shrink: 0;
  width: 80px;
  font-size: 12px;
  font-weight: bold;
  color: #909399;
}

.buttons-row {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  flex: 1;
}

.shortcut-btn {
  min-width: 40px;
  height: 32px;
  padding: 0 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: white;
  color: #606266;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.shortcut-btn:hover {
  border-color: #409eff;
  color: #409eff;
  background-color: #ecf5ff;
}

.action-btn {
  padding: 8px 16px;
  border: 1px solid #409eff;
  border-radius: 4px;
  background-color: #409eff;
  color: white;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 5px;
}

.action-btn:hover {
  background-color: #66b1ff;
}

.clear-btn {
  border-color: #f56c6c;
  background-color: #f56c6c;
}

.clear-btn:hover {
  background-color: #f78989;
}

.shortcut-group.actions {
  justify-content: flex-end;
  border-top: 1px dashed #e4e7ed;
  padding-top: 10px;
}

/* 帮助文档样式 */
.latex-help {
  font-size: 13px;
}

.latex-help h4 {
  margin: 0 0 15px 0;
  color: #303133;
  border-bottom: 2px solid #409eff;
  padding-bottom: 8px;
}

.help-section {
  margin-bottom: 15px;
}

.help-section h5 {
  margin: 0 0 8px 0;
  color: #606266;
  font-size: 13px;
}

.help-section ul {
  margin: 0;
  padding-left: 20px;
}

.help-section li {
  margin-bottom: 5px;
  line-height: 1.6;
  color: #909399;
}

.help-section code {
  background-color: #f5f7fa;
  padding: 2px 6px;
  border-radius: 3px;
  color: #e6a23c;
  font-family: 'Courier New', monospace;
  font-size: 12px;
}
</style>
