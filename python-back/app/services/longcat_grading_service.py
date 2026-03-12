"""
智能批改服务 - 使用 LongCat 进行图片答案批改
"""
import json
import time
import base64
from typing import Dict, Any, List
from openai import OpenAI
from loguru import logger
from app.config import settings


class LongCatGradingService:
    """使用 LongCat 进行图片答案批改的服务"""

    def __init__(self):
        """初始化 LongCat 客户端"""
        self.client = OpenAI(
            api_key=settings.LONGCAT_API_KEY,
            base_url=settings.LONGCAT_BASE_URL
        )
        self.model = settings.LONGCAT_MODEL
        self.temperature = settings.LONGCAT_TEMPERATURE
        self.max_tokens = settings.LONGCAT_MAX_TOKENS

    def _encode_image(self, image_base64: str) -> str:
        """
        处理图片Base64编码

        Args:
            image_base64: 图片的Base64编码（可能已包含data:image/xxx;base64,前缀）

        Returns:
            处理后的base64字符串
        """
        # 如果已经包含前缀，直接返回
        if ',' in image_base64:
            return image_base64
        # 否则添加图片前缀（假设为jpeg格式）
        return f"data:image/jpeg;base64,{image_base64}"

    def _build_prompt(self, question_content: str, reference_answer: str,
                      question_type: int, question_score: float = 100) -> str:
        """构建批改提示词（用于图片答案）"""
        type_names = {
            1: "单选题",
            2: "多选题",
            3: "填空题",
            4: "简答题",
            5: "解答题"
        }
        type_name = type_names.get(question_type, "主观题")

        prompt = f"""你是一位资深的考研数学阅卷组长。请仔细批改学生上传的手写答案图片。

【题型】{type_name}

【题目内容】
{question_content}

【参考答案】
{reference_answer}

【本题满分】{question_score}分

【评分要求】
1. 根据参考答案和学生的手写答案进行对比评分
2. 关注解题思路的正确性和完整性
3. 即使最终答案错误，也要考虑过程分
4. 对于计算错误、逻辑错误要明确指出
5. 如果图片模糊或无法识别，请明确说明

【返回格式】
请以JSON格式返回评分结果：
{{
  "score": 得分(数字，0-{int(question_score)}之间),
  "feedback": "详细的评分反馈",
  "strengths": ["优点1", "优点2"],
  "weaknesses": ["不足点1", "不足点2"]
}}

【注意事项】
- score: 0-{int(question_score)}的数字
- feedback: 详细说明得分理由、错误原因、图片识别情况等
- strengths: 列出学生答案的优点（至少1条，如果没有则为空数组）
- weaknesses: 列出学生答案的不足（至少1条，如果没有则为空数组）
- 如果图片无法识别，score设为0，feedback说明原因
- 纯粹返回JSON，不要有其他说明文字"""

        return prompt

    def _parse_response(self, response_content: str) -> Dict[str, Any]:
        """解析 AI 响应"""
        try:
            # 尝试直接解析 JSON
            data = json.loads(response_content)
        except json.JSONDecodeError:
            logger.info("JSON解析失败，尝试提取JSON部分")
            try:
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

        # 确保必填字段存在
        if "score" not in data:
            data["score"] = 0
        if "feedback" not in data:
            data["feedback"] = "系统未能生成点评"
        if "strengths" not in data or not isinstance(data["strengths"], list):
            data["strengths"] = []
        if "weaknesses" not in data or not isinstance(data["weaknesses"], list):
            data["weaknesses"] = []

        return data

    async def grade_with_image(
        self,
        question_content: str,
        reference_answer: str,
        image_bases: List[str],
        question_type: int = 4,
        question_score: float = 100
    ) -> Dict[str, Any]:
        """
        使用图片答案进行批改

        Args:
            question_content: 题目内容
            reference_answer: 参考答案
            image_bases: 用户手写答案的图片Base64列表
            question_type: 题目类型（默认4为主观题）
            question_score: 题目满分

        Returns:
            批改结果字典
        """
        start_time = time.time()
        logger.info(f"\n==========================================")
        logger.info(f"开始LongCat图片批改任务 @ {time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(start_time))}")
        logger.info(f"题目内容: {question_content[:100]}{'...' if len(question_content) > 100 else ''}")
        logger.info(f"参考答案: {reference_answer[:100]}{'...' if len(reference_answer) > 100 else ''}")
        logger.info(f"图片数量: {len(image_bases)}")
        logger.info(f"题目分值: {question_score}")

        try:
            # 验证图片数据
            for i, img_base in enumerate(image_bases):
                if len(img_base) > 100:
                    logger.info(f"   图片{i+1}: {img_base[:100]}... (长度: {len(img_base)})")
                else:
                    logger.info(f"   图片{i+1}: {img_base} (长度: {len(img_base)})")

            # 构建提示词
            prompt = self._build_prompt(
                question_content, reference_answer, question_type, question_score
            )
            logger.info(f"提示词长度: {len(prompt)} 字符")

            # 构建消息内容（文本 + 图片）
            content = [{"type": "text", "text": prompt}]

            # 添加图片
            for i, img_base in enumerate(image_bases):
                processed_img = self._encode_image(img_base)
                content.append({
                    "type": "image_url",
                    "image_url": {
                        "url": processed_img
                    }
                })
                logger.info(f"   已添加图片{i+1}到请求")

            # 调用 LongCat API
            logger.info(f"正在调用LongCat API ({self.model})...")
            api_call_start = time.time()

            response = self.client.chat.completions.create(
                model=self.model,
                messages=[{
                    "role": "user",
                    "content": content
                }],
                temperature=self.temperature,
                max_tokens=self.max_tokens
            )

            api_call_end = time.time()
            api_duration = api_call_end - api_call_start
            logger.info(f"API调用成功，耗时: {api_duration:.2f} 秒")

            # 提取响应内容
            response_content = response.choices[0].message.content
            logger.info(f"收到响应 (长度: {len(response_content)} 字符)")
            logger.info(f"响应预览: {response_content[:200]}{'...' if len(response_content) > 200 else ''}")

            # 解析响应
            parse_start = time.time()
            result = self._parse_response(response_content)
            parse_duration = time.time() - parse_start
            logger.info(f"JSON解析完成，耗时: {parse_duration:.2f} 秒")

            # 显示解析结果
            logger.info(f"解析结果:")
            logger.info(f"   得分: {result.get('score', 'N/A')}%")
            logger.info(f"   反馈: {result.get('feedback', 'N/A')[:100]}{'...' if len(result.get('feedback', '')) > 100 else ''}")
            logger.info(f"   优点: {result.get('strengths', [])}")
            logger.info(f"   不足: {result.get('weaknesses', [])}")

            # 总耗时
            total_duration = time.time() - start_time
            logger.info(f"总耗时: {total_duration:.2f} 秒")
            logger.info(f"批改完成 @ {time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time()))}")
            logger.info(f"==========================================")

            return {
                "code": 200,
                "message": "批改成功",
                "data": result
            }

        except Exception as e:
            total_duration = time.time() - start_time
            logger.error(f"批改失败，总耗时: {total_duration:.2f} 秒")
            logger.error(f"错误类型: {type(e).__name__}")
            logger.error(f"错误信息: {str(e)}")
            if hasattr(e, 'response'):
                logger.error(f"API响应: {e.response}")
            logger.error(f"批改失败 @ {time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time()))}")
            logger.error(f"==========================================")

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