"""
图片识别服务 - 使用 GLM-4.6V-Flash 识别题目图片
"""
import base64
import json
import re
import time
from typing import Dict, Any, Optional
from openai import OpenAI
from loguru import logger
from app.config import settings


class ImageRecognitionService:
    """图片识别服务"""

    def __init__(self):
        """初始化 GLM 客户端"""
        self.client = OpenAI(
            api_key=settings.GLM_API_KEY,
            base_url=settings.GLM_BASE_URL
        )
        self.model = "glm-4.6v-flash"

    def _build_prompt(self) -> str:
        """构建识别提示词"""
        return """你是一个专业的数学题目识别助手。请识别图片中的题目内容，并以JSON格式返回。

【题目类型】1-单选, 2-多选, 3-填空, 4-简答

【LaTeX公式使用规则 - 务必遵守】
✅ 需要用LaTeX的情况（必须用$或$$包裹）：
- 数学运算符号：+, -, ×, ÷, ±, ∓
- 关系符号：=, ≠, ≈, <, >, ≤, ≥
- 希腊字母：α, β, γ, θ, π, Δ 等
- 上下标：x², xₙ, A^T, A^{-1}
- 分数：\frac{a}{b}
- 根号：\sqrt{x}
- 求和/积分：\sum, \int, \prod
- 极限：\lim_{x\to 0}
- 矩阵表示（如 $A$, $B$，而不是内容）
- 行列式：\begin{vmatrix}...\end{vmatrix}

❌ 不需要用LaTeX的情况（保持普通文本）：
- 普通数字：1, 2, 3, 2025
- 中文标点：逗号、句号、顿号、冒号
- 英文标点：, . : ; () []
- 中文文字：题目内容、解析说明
- 变量名：函数f(x)中的f和x可以不用LaTeX（除非是数学上下标）
- 普通括号：() []

【示例对比】
正确：设函数 $f(x) = x^3 - 3x + 1$，求 $f'(x)$
错误：设$函数$$f$($x$) = $x^3$ - $3x$ + $1$，求$f'($x$)$

正确：$A$, $B$ 为3阶矩阵，满足 $r(AB) = r(BA) + 1$
错误：$，$ $A$，$ $B$ 为$3$ $阶$ $矩$阵$

正确：方程组 $(A+B)x=0$ 只有零解
错误：$方$ $程$ $组$ $( $A$ + $B$ ) $x$ = $0$

【矩阵LaTeX格式】
- 矩阵：$A = \begin{pmatrix} a & b \\ c & d \end{pmatrix}$
- 行列式：$\begin{vmatrix} a & b \\ c & d \end{vmatrix}$
- 转置：$A^T$，逆矩阵：$A^{-1}$

【JSON格式要求】
{
  "type": 1,
  "content": "题目内容",
  "options": ["选项A", "选项B", "选项C", "选项D"],
  "answer": "答案",
  "analysis": "解析",
  "tags": ["标签1", "标签2"],
  "difficulty": 3
}

【重要】
1. 只给真正的数学表达式添加LaTeX标记
2. 中文、英文、标点符号保持原样，不要加LaTeX
3. 变量名（如f, g, A, B）可以加$表示，但不要过度使用
4. 简单的数字运算保持原样：2+3=5（不要改成$2+3=5$）
5. 纯粹返回JSON，不要有其他说明文字"""

    def _fix_latex_escaping(self, json_str: str) -> str:
        """
        修复 LaTeX 反斜杠转义问题

        AI 返回的 JSON 可能包含未正确转义的反斜杠
        """
        result = []
        in_string = False
        escape_next = False

        for i, char in enumerate(json_str):
            if not in_string:
                if char == '"':
                    in_string = True
                    result.append(char)
                else:
                    result.append(char)
            else:
                if escape_next:
                    # 检查是否是有效的 JSON 转义序列
                    if char in '"\\/bfnrtu':
                        result.append('\\')
                        result.append(char)
                    else:
                        # LaTeX 反斜杠，需要转义
                        result.append('\\\\')
                        result.append(char)
                    escape_next = False
                elif char == '\\':
                    escape_next = True
                elif char == '"':
                    in_string = False
                    result.append(char)
                else:
                    # 处理控制字符
                    if char == '\n':
                        result.append('\\n')
                    elif char == '\r':
                        result.append('\\r')
                    elif char == '\t':
                        result.append('\\t')
                    elif ord(char) < 32:
                        result.append(f'\\u{ord(char):04x}')
                    else:
                        result.append(char)

        return ''.join(result)

    def _clean_latex(self, text: str) -> str:
        """
        清理和修复 LaTeX 代码中的常见错误

        Args:
            text: 包含 LaTeX 的文本

        Returns:
            清理后的文本
        """
        if not text:
            return text

        # 1. 修复断裂的反斜杠
        # \b -> \\b
        text = re.sub(r'\\b(?=[a-z])', r'\\\\b', text)
        # \p -> \\p
        text = re.sub(r'\\p(?=[a-z])', r'\\\\p', text)
        # \v -> \\v
        text = re.sub(r'\\v(?=[a-z])', r'\\\\v', text)
        # \f -> \\f (修复 frac 的 f)
        text = re.sub(r'(?<!\\)\\f(?=[a-z]{2})', r'\\\\f', text)
        # \r -> \\r
        text = re.sub(r'\\r(?=[a-z])', r'\\\\r', text)

        # 2. 修复常见的错误格式
        # rac{} -> frac{}
        text = re.sub(r'\\rac\{', r'\\frac{', text)
        # eqnoarray -> equation (虽然不是直接等价，但修复明显错误)
        text = re.sub(r'\\begin{eqnoarray}', r'\\begin{equation}', text)
        text = re.sub(r'\\end{eqnoarray}', r'\\end{equation}', text)

        # 3. 修复矩阵括号格式（考研习惯用圆括号）
        # bmatrix -> pmatrix
        text = re.sub(r'\\bmatrix', r'\\pmatrix', text)
        # Bmatrix -> pmatrix
        text = re.sub(r'\\Bmatrix', r'\\pmatrix', text)
        # vmatrix -> pmatrix (行列式也可以用圆括号，但用行列式更准确)
        # 这里保持 vmatrix 不变，因为行列式应该用竖线

        # 4. 清理多余的空格
        text = re.sub(r'\$+\s+', '$', text)
        text = re.sub(r'\s+\$', '$', text)
        text = re.sub(r'\$\s+\$', '$$', text)

        return text

    def _parse_response(self, response_content: str) -> Dict[str, Any]:
        """
        解析 AI 响应

        Args:
            response_content: AI 返回的内容

        Returns:
            解析后的题目数据
        """
        # 先移除可能的 markdown 代码块标记
        content = response_content.strip()
        # 移除 ```json 开头
        if content.startswith("```json"):
            content = content[7:]
        elif content.startswith("```"):
            content = content[3:]
        # 移除 ``` 结尾
        if content.endswith("```"):
            content = content[:-3]
        content = content.strip()

        try:
            # 尝试直接解析 JSON
            data = json.loads(content)
        except json.JSONDecodeError:
            # 解析失败，尝试修复 LaTeX 转义问题
            logger.info("JSON解析失败，尝试修复LaTeX转义问题")
            try:
                fixed_json = self._fix_latex_escaping(content)
                data = json.loads(fixed_json)
            except json.JSONDecodeError as e:
                logger.error(f"JSON解析失败: {e}")
                logger.error(f"尝试解析的内容: {content[:200]}...")
                raise ValueError(f"AI返回的内容无法解析为JSON: {response_content}")

        # 后处理：清理 LaTeX 错误
        if "content" in data and data["content"]:
            data["content"] = self._clean_latex(data["content"])

        if "options" in data and isinstance(data["options"], list):
            for option in data["options"]:
                if isinstance(option, dict) and "text" in option:
                    option["text"] = self._clean_latex(option["text"])
                elif isinstance(option, str):
                    # 如果是旧格式字符串，转换为对象格式
                    pass

        if "answer" in data and data["answer"]:
            data["answer"] = self._clean_latex(data["answer"])

        if "analysis" in data and data["analysis"]:
            data["analysis"] = self._clean_latex(data["analysis"])

        # 格式化选项
        if "options" in data and isinstance(data["options"], list) and data["options"]:
            formatted_options = []
            labels = ["A", "B", "C", "D"]
            for i, option in enumerate(data["options"]):
                if i < len(labels):
                    if isinstance(option, dict):
                        formatted_options.append(option)
                    elif isinstance(option, str):
                        formatted_options.append({
                            "label": labels[i],
                            "text": option
                        })
            data["options"] = formatted_options
        else:
            # 如果没有选项或选项为空，确保设置为空列表
            data["options"] = []

        return data

    async def recognize_question(self, image_bytes: bytes) -> Dict[str, Any]:
        """
        识别图片中的题目

        Args:
            image_bytes: 图片数据的字节流

        Returns:
            识别后的题目数据字典
        """
        # 开始计时
        start_time = time.time()

        try:
            # 转换为 base64
            base64_image = base64.b64encode(image_bytes).decode('utf-8')

            logger.info(f"==========================================")
            logger.info(f"开始调用 GLM-4.6V 识别图片，大小: {len(image_bytes)} bytes")
            logger.info(f"开始时间: {time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(start_time))}")

            # 调用 GLM API
            response = self.client.chat.completions.create(
                model=self.model,
                messages=[{
                    "role": "user",
                    "content": [
                        {
                            "type": "text",
                            "text": self._build_prompt()
                        },
                        {
                            "type": "image_url",
                            "image_url": {
                                "url": base64_image
                            }
                        }
                    ]
                }],
                temperature=0.3,
                max_tokens=4000
            )

            # API 调用完成
            api_end_time = time.time()
            api_duration = api_end_time - start_time

            # 提取响应内容
            content = response.choices[0].message.content
            logger.info(f"GLM-4.6V API 响应成功，内容长度: {len(content)} 字符")
            logger.info(f"API 调用耗时: {api_duration:.2f} 秒")

            # 解析响应
            parse_start = time.time()
            result = self._parse_response(content)
            parse_duration = time.time() - parse_start
            logger.info(f"JSON 解析耗时: {parse_duration:.2f} 秒")

            # 总耗时
            total_duration = time.time() - start_time
            logger.info(f"总耗时: {total_duration:.2f} 秒")
            logger.info(f"结束时间: {time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time()))}")
            logger.info(f"==========================================")

            return {
                "code": 200,
                "message": "识别成功",
                "data": result
            }

        except Exception as e:
            total_duration = time.time() - start_time
            logger.error(f"图片识别失败，总耗时: {total_duration:.2f} 秒，错误: {str(e)}")
            return {
                "code": 500,
                "message": f"识别失败: {str(e)}",
                "data": None
            }
