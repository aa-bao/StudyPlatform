"""
智能批改服务 - 使用 GLM-4.7 进行主观题批改
"""
import json
import time
from typing import Dict, Any, Optional
from openai import OpenAI
from loguru import logger
from app.config import settings


class GradingService:
    """智能批改服务"""

    def __init__(self):
        """初始化 GLM 客户端"""
        self.client = OpenAI(
            api_key=settings.GLM_API_KEY,
            base_url=settings.GLM_BASE_URL
        )
        self.model = "glm-4-plus"  # 使用 GLM-4.7 进行批改

    def _build_prompt(self, question_content: str, user_answer: str,
                      reference_answer: str, question_type: int) -> str:
        """构建批改提示词"""
        type_names = {
            1: "单选题",
            2: "多选题",
            3: "填空题",
            4: "简答题"
        }
        type_name = type_names.get(question_type, "未知题型")

        prompt = f"""你是一位资深的考研数学阅卷组长。请按照以下标准对学生的解答进行评分。

【题型】{type_name}

【题目内容】
{question_content}

【参考答案】
{reference_answer}

【用户答案】
{user_answer}

【评分要求】
1. 本题满分：100分
2. 请按步骤给分，关注解题思路的正确性和完整性
3. 即使最终答案错误，也要考虑过程分
4. 对于计算错误、逻辑错误要明确指出
5. 给出详细的建设性反馈

【返回格式】
请以JSON格式返回评分结果：
{{
  "score": 得分(数字),
  "feedback": "详细的评分反馈",
  "strengths": ["优点1", "优点2"],
  "weaknesses": ["不足点1", "不足点2"]
}}

【注意事项】
- score: 0-100的数字
- feedback: 详细说明得分理由、错误原因等
- strengths: 列出学生答案的优点（至少1条）
- weaknesses: 列出学生答案的不足（至少1条）
- 纯粹返回JSON，不要有其他说明文字"""

        return prompt

    def _parse_response(self, response_content: str) -> Dict[str, Any]:
        """
        解析 AI 响应

        Args:
            response_content: AI 返回的内容

        Returns:
            解析后的批改结果
        """
        try:
            # 尝试直接解析 JSON
            data = json.loads(response_content)
        except json.JSONDecodeError:
            # 尝试提取 JSON 部分（可能前后有额外文字）
            logger.info("JSON解析失败，尝试提取JSON部分")
            try:
                # 查找第一个 { 和最后一个 }
                start = response_content.find('{')
                end = response_content.rfind('}') + 1
                if start >= 0 and end > start:
                    json_str = response_content[start:end]
                    data = json.loads(json_str)
                else:
                    raise ValueError("无法找到有效的JSON内容")
            except Exception as e:
                logger.error(f"JSON解析失败: {e}")
                raise ValueError(f"AI返回的内容无法解析为JSON: {response_content}")

        # 确保必填字段存在（与Java端期望的格式一致）
        if "score" not in data:
            data["score"] = 0
        if "feedback" not in data:
            data["feedback"] = "系统未能生成点评"
        if "strengths" not in data or not isinstance(data["strengths"], list):
            data["strengths"] = []
        if "weaknesses" not in data or not isinstance(data["weaknesses"], list):
            data["weaknesses"] = []

        return data

    async def grade_answer(
        self,
        question_content: str,
        user_answer: str,
        reference_answer: str,
        question_type: int = 4
    ) -> Dict[str, Any]:
        """
        批改用户答案

        Args:
            question_content: 题目内容
            user_answer: 用户答案
            reference_answer: 参考答案
            question_type: 题目类型（1-单选, 2-多选, 3-填空, 4-简答）

        Returns:
            批改结果字典
        """
        # 开始计时
        start_time = time.time()

        try:
            logger.info(f"==========================================")
            logger.info(f"开始批改题目，类型: {question_type}")
            logger.info(f"开始时间: {time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(start_time))}")

            # 构建提示词
            prompt = self._build_prompt(
                question_content, user_answer, reference_answer, question_type
            )

            # 调用 GLM API
            response = self.client.chat.completions.create(
                model=self.model,
                messages=[{
                    "role": "user",
                    "content": prompt
                }],
                temperature=0.3,
                max_tokens=2000
            )

            # API 调用完成
            api_end_time = time.time()
            api_duration = api_end_time - start_time

            # 提取响应内容
            content = response.choices[0].message.content
            logger.info(f"GLM-4.7 批改响应成功，内容长度: {len(content)} 字符")
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
                "message": "批改成功",
                "data": result
            }

        except Exception as e:
            total_duration = time.time() - start_time
            logger.error(f"批改失败，总耗时: {total_duration:.2f} 秒，错误: {str(e)}")
            return {
                "code": 500,
                "message": f"批改失败: {str(e)}",
                "data": {
                    "score": 0,
                    "feedback": f"批改系统错误: {str(e)}",
                    "strengths": [],
                    "weaknesses": []
                }
            }
