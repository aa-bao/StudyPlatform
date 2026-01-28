/**
 * LaTeX 渲染工具函数
 * 用于在 Vue 组件中渲染 LaTeX 公式
 */

import katex from 'katex'
import 'katex/dist/katex.min.css'

/**
 * 渲染 LaTeX 公式
 * @param {string} text - 包含 LaTeX 的文本
 * @param {object} options - KaTeX 配置选项
 * @returns {string} 渲染后的 HTML
 */
export function renderLatex(text, options = {}) {
  if (!text || typeof text !== 'string') {
    return ''
  }

  const defaultOptions = {
    throwOnError: false,
    displayMode: false,
    ...options
  }

  try {
    // 处理行内公式 $...$
    const inlineRegex = /\$([^$]+)\$/g
    let result = text.replace(inlineRegex, (match, formula) => {
      try {
        return katex.renderToString(formula, { ...defaultOptions, displayMode: false })
      } catch (e) {
        console.warn('LaTeX 渲染失败:', formula, e)
        return match
      }
    })

    // 处理块级公式 $$...$$
    const blockRegex = /\$\$([^$]+)\$\$/g
    result = result.replace(blockRegex, (match, formula) => {
      try {
        return katex.renderToString(formula, { ...defaultOptions, displayMode: true })
      } catch (e) {
        console.warn('LaTeX 渲染失败:', formula, e)
        return match
      }
    })

    return result
  } catch (error) {
    console.error('LaTeX 渲染错误:', error)
    return text
  }
}

/**
 * 渲染单个 LaTeX 公式（不带 $ 符号）
 * @param {string} formula - LaTeX 公式
 * @param {boolean} displayMode - 是否为块级模式
 * @returns {string} 渲染后的 HTML
 */
export function renderFormula(formula, displayMode = false) {
  if (!formula || typeof formula !== 'string') {
    return ''
  }

  try {
    return katex.renderToString(formula, {
      throwOnError: false,
      displayMode
    })
  } catch (error) {
    console.warn('公式渲染失败:', formula, error)
    return formula
  }
}

/**
 * 检测文本中是否包含 LaTeX 公式
 * @param {string} text - 待检测文本
 * @returns {boolean} 是否包含 LaTeX
 */
export function hasLatex(text) {
  if (!text || typeof text !== 'string') {
    return false
  }
  return /\$[^$]+\$/.test(text)
}

export default {
  renderLatex,
  renderFormula,
  hasLatex
}
