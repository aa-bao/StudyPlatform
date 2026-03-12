"""
整卷批改和考试分析服务
使用AI对整份试卷进行主观题批改和完整的考试情况分析
"""
import json
import time
from typing import Dict, Any, List
from openai import OpenAI
from loguru import logger
from app.config import settings


class ExamAnalysisService:
    """整卷批改和考试分析服务"""

    def __init__(self):
        """初始化 LongCat 客户端（使用LongCat进行综合分析）"""
        self.client = OpenAI(
            api_key=settings.LONGCAT_API_KEY,
            base_url=settings.LONGCAT_BASE_URL
        )
        self.model = settings.LONGCAT_MODEL
        self.temperature = settings.LONGCAT_TEMPERATURE
        self.max_tokens = 3000

    def _build_grading_prompt(self, exam_data: Dict[str, Any]) -> str:
        """构建整卷批改提示词"""
        questions_text = []
        for i, question in enumerate(exam_data["questions"], 1):
            type_names = {
                1: "单选题",
                2: "多选题",
                3: "填空题",
                4: "简答题",
                5: "解答题"
            }
            type_name = type_names.get(question["type"], "主观题")

            question_info = [
                f"【第{i}题】{type_name}",
                f"题目内容：{question['content']}"
            ]

            if question["type"] in [1, 2, 3]:
                question_info.append(f"标准答案：{question['answer']}")
                question_info.append(f"用户答案：{question['user_answer']}")
            else:
                question_info.append(f"标准答案：{question['answer']}")
                if question.get("user_answer"):
                    question_info.append(f"用户答案：{question['user_answer']}")
                if question.get("user_answer_images"):
                    question_info.append(f"用户图片答案：{len(question['user_answer_images'])} 张图片")

            question_info.append(f"分值：{question['score']} 分")

            questions_text.append("\n".join(question_info))

        prompt = f"""你是一位资深的考研数学阅卷组长和教学专家。请对学生的整张试卷进行全面批改和分析。

【试卷信息】
考试科目：考研数学
总题数：{len(exam_data['questions'])} 题
总分：{exam_data['total_score']} 分
考试时长：{exam_data['time_limit']} 分钟

【答题信息】
{chr(10).join(questions_text)}

【批改和分析要求】
1. 对每道主观题进行详细批改，包括得分、错误分析、优点和不足
2. 对客观题的答题情况进行统计和分析，识别知识点和题型的薄弱环节
3. 计算总分和各部分得分率
4. 提供全面的考试分析报告，包括：
   - 客观题扣分情况及主要错误的知识点、题型
   - 主观题扣分情况及主要错误的知识点、题型
   - 学生表现优秀的方面
   - 学生欠缺的知识点和能力（如极限计算、微分方程求解等）
5. 给出针对性的学习建议

【返回格式要求】
请以JSON格式返回，包含以下字段：
{{
    "score": 总分（数字）,
    "subjective_score": 主观题得分（数字）,
    "objective_score": 客观题得分（数字）,
    "detailed_grading": [
        {{
            "question_index": 第n题,
            "score": 得分,
            "max_score": 分值,
            "feedback": "详细批改反馈",
            "strengths": ["优点1", "优点2"],
            "weaknesses": ["不足1", "不足2"]
        }}
    ],
    "analysis": {{
        "objective_analysis": {{
            "score": 客观题得分,
            "max_score": 客观题总分,
            "error_analysis": "主要错误分析（知识点、题型）",
            "wrong_questions": ["第2题", "第5题"]
        }},
        "subjective_analysis": {{
            "score": 主观题得分,
            "max_score": 主观题总分,
            "error_analysis": "主要错误分析（知识点、题型）",
            "wrong_questions": ["第8题", "第10题"]
        }},
        "strengths": ["表现优秀的方面1", "表现优秀的方面2"],
        "weaknesses": ["欠缺的知识点/题型1", "欠缺的知识点/题型2"],
        "suggestions": ["学习建议1", "学习建议2"]
    }}
}}

【注意事项】
- 所有字段必须是有效的JSON格式
- 反馈内容要详细、专业，有建设性
- 客观题只分析答题情况，不重新批改（已经由系统批改过）
- 主观题要根据题目内容和用户答案进行评分
- 总得分要等于各题得分之和
- 纯粹返回JSON，不要有其他说明文字"""

        return prompt

    def _parse_response(self, response_content: str) -> Dict[str, Any]:
        """解析 AI 响应，处理 LaTeX 和特殊字符"""
        try:
            data = json.loads(response_content)
            return data
        except json.JSONDecodeError:
            logger.info("JSON解析失败，尝试提取JSON部分并修复转义字符")
            try:
                start = response_content.find('{')
                end = response_content.rfind('}') + 1
                if start < 0 or end <= start:
                    raise ValueError("无法找到有效的JSON内容")

                json_str = response_content[start:end]

                # 修复常见的 LaTeX 转义字符
                # 这个函数会修复 LaTeX 公式中的各种特殊符号
                # 如 \pm, \alpha, \beta, \frac 等
                def fix_latex_escapes(text):
                    # 需要修复的常见 LaTeX 转义
                    fixes = {
                        r'\pm': r'\\pm',    # 正负号
                        r'\alpha': r'\\alpha',
                        r'\beta': r'\\beta',
                        r'\gamma': r'\\gamma',
                        r'\delta': r'\\delta',
                        r'\frac': r'\\frac',
                        r'\int': r'\\int',
                        r'\sum': r'\\sum',
                        r'\lim': r'\\lim',
                        r'\sin': r'\\sin',
                        r'\cos': r'\\cos',
                        r'\tan': r'\\tan',
                        r'\log': r'\\log',
                        r'\ln': r'\\ln',
                        r'\sqrt': r'\\sqrt',
                        r'\infty': r'\\infty',
                        r'\rightarrow': r'\\rightarrow',
                        r'\leq': r'\\leq',
                        r'\geq': r'\\geq',
                        r'\neq': r'\\neq',
                        r'\cdot': r'\\cdot',
                        r'\times': r'\\times',
                        r'\div': r'\\div',
                    }

                    for original, replacement in fixes.items():
                        text = text.replace(original, replacement)
                    return text

                # 修复转义字符
                json_str = fix_latex_escapes(json_str)

                # 尝试解析
                try:
                    return json.loads(json_str)
                except json.JSONDecodeError:
                    pass

                # 如果还是失败，使用正则表达式更安全的方法来修复所有转义问题
                import re
                # 找到所有可能是 LaTeX 公式的内容，临时替换它们
                # 或者直接尝试清理所有 \ 后面跟非标准JSON字符的情况
                # 这种方法比较暴力，但可能有效
                safe_json_str = re.sub(r'\\([a-zA-Z])', r'\\\\\1', json_str)
                try:
                    return json.loads(safe_json_str)
                except json.JSONDecodeError:
                    pass

                # 作为最后手段，使用 eval 方式（不安全，但在受控环境下可以使用）
                import ast
                try:
                    data = ast.literal_eval(safe_json_str)
                    if isinstance(data, dict):
                        return data
                except Exception:
                    pass

                logger.warning("使用简易解析模式")
                return self._get_default_analysis()

            except Exception as e:
                logger.error(f"JSON解析失败: {e}")
                logger.error(f"原始响应前500字符: {response_content[:500]}")
                return self._get_default_analysis()

    def _simple_parse_response(self, response_content: str) -> Dict[str, Any]:
        """简易解析模式，当完整解析失败时使用"""
        # 这是一个后备方案，返回基本的默认结构
        return self._get_default_analysis()

    def _get_default_analysis(self) -> Dict[str, Any]:
        """返回默认的分析结果"""
        return {
            "score": 0.0,
            "subjective_score": 0.0,
            "objective_score": 0.0,
            "detailed_grading": [],
            "analysis": {
                "objective_analysis": {
                    "score": 0.0,
                    "max_score": 0.0,
                    "error_analysis": "AI分析暂时不可用",
                    "wrong_questions": []
                },
                "subjective_analysis": {
                    "score": 0.0,
                    "max_score": 0.0,
                    "error_analysis": "AI分析暂时不可用",
                    "wrong_questions": []
                },
                "strengths": [],
                "weaknesses": [],
                "suggestions": ["AI分析暂时不可用，请稍后重试"]
            }
        }

    async def analyze_exam(self, exam_data: Dict[str, Any]) -> Dict[str, Any]:
        """
        分析整份试卷

        Args:
            exam_data: 考试数据，包含题目信息和用户答案

        Returns:
            分析结果
        """
        start_time = time.time()
        logger.info("\n==========================================")
        logger.info("开始整卷分析 @ {}".format(time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(start_time))))
        logger.info("题目数量: {}".format(len(exam_data["questions"])))
        logger.info("总分值: {}".format(exam_data["total_score"]))
        logger.info("考试时长: {}".format(exam_data["time_limit"]))

        try:
            # 构建提示词
            prompt = self._build_grading_prompt(exam_data)
            logger.info("提示词长度: {} 字符".format(len(prompt)))

            # 调用 AI
            logger.info("正在调用LongCat API ({})...".format(self.model))
            api_call_start = time.time()

            response = self.client.chat.completions.create(
                model=self.model,
                messages=[{
                    "role": "user",
                    "content": prompt
                }],
                temperature=self.temperature,
                max_tokens=self.max_tokens
            )

            api_call_end = time.time()
            api_duration = api_call_end - api_call_start
            logger.info("API调用成功，耗时: {:.2f} 秒".format(api_duration))

            # 提取响应内容
            response_content = response.choices[0].message.content
            logger.info("收到响应 (长度: {} 字符)".format(len(response_content)))
            logger.info("响应预览: {}".format(response_content[:200]))

            # 解析响应
            parse_start = time.time()
            result = self._parse_response(response_content)
            parse_duration = time.time() - parse_start
            logger.info("JSON解析完成，耗时: {:.2f} 秒".format(parse_duration))

            # 验证并修正AI返回的分数，确保不超过每道题的分值
            if "detailed_grading" in result and result["detailed_grading"]:
                calculated_total = 0.0
                calculated_objective = 0.0
                calculated_subjective = 0.0

                for i, grading in enumerate(result["detailed_grading"]):
                    question_index = grading.get("question_index", i + 1)
                    if 1 <= question_index <= len(exam_data["questions"]):
                        question = exam_data["questions"][question_index - 1]
                        max_score = question.get("score", 10.0)
                        ai_score = grading.get("score", 0.0)

                        # 限制分数不超过题目分值
                        if ai_score > max_score:
                            logger.warning(f"第{question_index}题AI返回分数{ai_score}超过分值{max_score}，已修正")
                            grading["score"] = max_score

                        # 检查题目类型，分别累加
                        is_objective = question.get("type") in [1, 2, 3]
                        if is_objective:
                            calculated_objective += grading["score"]
                        else:
                            calculated_subjective += grading["score"]

                        calculated_total += grading["score"]

                # 重新计算总分
                result["score"] = calculated_total
                result["objective_score"] = calculated_objective
                result["subjective_score"] = calculated_subjective

                logger.info(f"重新计算后总分: {calculated_total}, 客观题: {calculated_objective}, 主观题: {calculated_subjective}")

            # 显示解析结果
            logger.info("解析结果:")
            logger.info("   总分: {}".format(result.get("score", "N/A")))
            logger.info("   主观题得分: {}".format(result.get("subjective_score", "N/A")))
            logger.info("   客观题得分: {}".format(result.get("objective_score", "N/A")))

            # 总耗时
            total_duration = time.time() - start_time
            logger.info("总耗时: {:.2f} 秒".format(total_duration))
            logger.info("分析完成 @ {}".format(time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time()))))
            logger.info("==========================================")

            return {
                "code": 200,
                "message": "分析成功",
                "data": result
            }

        except Exception as e:
            total_duration = time.time() - start_time
            logger.error("分析失败，总耗时: {:.2f} 秒".format(total_duration))
            logger.error("错误类型: {}".format(type(e).__name__))
            logger.error("错误信息: {}".format(str(e)))
            if hasattr(e, 'response'):
                logger.error("API响应: {}".format(e.response))
            logger.error("分析失败 @ {}".format(time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time()))))
            logger.error("==========================================")

            return {
                "code": 500,
                "message": "分析失败: {}".format(str(e)),
                "data": {
                    "score": 0,
                    "subjective_score": 0,
                    "objective_score": 0,
                    "detailed_grading": [],
                    "analysis": {
                        "objective_analysis": {},
                        "subjective_analysis": {},
                        "strengths": [],
                        "weaknesses": [],
                        "suggestions": []
                    }
                }
            }