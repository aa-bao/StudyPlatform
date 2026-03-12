"""
AI 服务路由
"""
import time
from fastapi import APIRouter, UploadFile, File, HTTPException
from fastapi.responses import JSONResponse
from loguru import logger
from app.services.image_recognition_service import ImageRecognitionService
from app.services.grading_service import GradingService
from app.services.longcat_grading_service import LongCatGradingService
from app.services.exam_analysis_service import ExamAnalysisService
from pydantic import BaseModel
from typing import List, Dict, Any

# 创建路由器
ai_router = APIRouter(tags=["AI服务"])

# 初始化服务
image_service = ImageRecognitionService()
grading_service = GradingService()
longcat_grading_service = LongCatGradingService()
exam_analysis_service = ExamAnalysisService()


class GradingRequest(BaseModel):
    """批改请求模型（文本答案）"""
    question_content: str
    user_answer: str
    reference_answer: str
    question_type: int = 4


class GradingWithImageRequest(BaseModel):
    """图片答案批改请求模型"""
    question_content: str
    reference_answer: str
    image_bases: List[str]  # 图片Base64列表
    question_type: int = 4
    question_score: float = 100


class ExamAnalysisRequest(BaseModel):
    """整卷分析请求模型"""
    total_score: float  # 总分
    time_limit: int  # 考试时长（分钟）
    questions: List[Dict[str, Any]]  # 题目列表，包含：
    # type: 题目类型(1-单选, 2-多选, 3-填空, 4-简答, 5-解答)
    # content: 题目内容
    # answer: 标准答案
    # user_answer: 用户答案
    # user_answer_images: 用户图片答案（base64列表）
    # score: 题目分值


@ai_router.post("/recognize")
async def recognize_question(file: UploadFile = File(...)):
    """
    图片识别题目接口

    使用 GLM-4.6V-Flash 识别图片中的题目内容
    """
    try:
        # 验证文件类型
        if not file.content_type or not file.content_type.startswith("image/"):
            raise HTTPException(status_code=400, detail="只支持图片文件")

        # 读取文件内容
        image_bytes = await file.read()

        # 验证文件大小（限制 10MB）
        if len(image_bytes) > 10 * 1024 * 1024:
            raise HTTPException(status_code=400, detail="图片大小不能超过10MB")

        logger.info(f"接收到图片识别请求，文件名: {file.filename}, 大小: {len(image_bytes)} bytes")

        # 调用识别服务
        result = await image_service.recognize_question(image_bytes)

        return JSONResponse(content=result)

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"图片识别接口错误: {str(e)}")
        raise HTTPException(status_code=500, detail=f"识别失败: {str(e)}")


@ai_router.post("/grade")
async def grade_subjective_question(request: GradingRequest):
    """
    智能批改接口（文本答案）

    使用 GLM-4.7 对主观题进行智能批改
    """
    start_time = time.time()
    try:
        logger.info(f"\n==========================================")
        logger.info(f"收到文本批改请求 @ {time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(start_time))}")
        logger.info(f"题目内容: {request.question_content[:100]}{'...' if len(request.question_content) > 100 else ''}")
        logger.info(f"参考答案: {request.reference_answer[:100]}{'...' if len(request.reference_answer) > 100 else ''}")
        logger.info(f"用户答案: {request.user_answer[:100]}{'...' if len(request.user_answer) > 100 else ''}")
        logger.info(f"题型: {request.question_type}")

        # 调用批改服务
        logger.info(f"正在调用GLM批改服务...")
        result = await grading_service.grade_answer(
            question_content=request.question_content,
            user_answer=request.user_answer,
            reference_answer=request.reference_answer,
            question_type=request.question_type
        )

        end_time = time.time()
        duration = end_time - start_time
        logger.info(f"文本批改完成，耗时: {duration:.2f} 秒")
        logger.info(f"==========================================")

        return JSONResponse(content=result)

    except Exception as e:
        end_time = time.time()
        duration = end_time - start_time
        logger.error(f"文本批改失败，耗时: {duration:.2f} 秒")
        logger.error(f"错误: {str(e)}")
        logger.error(f"==========================================")
        raise HTTPException(status_code=500, detail=f"批改失败: {str(e)}")


@ai_router.post("/grade-with-image")
async def grade_with_image(request: GradingWithImageRequest):
    """
    智能批改接口（图片答案）

    使用 LongCat 对用户上传的手写答案图片进行智能批改
    """
    start_time = time.time()
    try:
        logger.info(f"\n==========================================")
        logger.info(f"收到图片批改请求 @ {time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(start_time))}")
        logger.info(f"题目内容: {request.question_content[:100]}{'...' if len(request.question_content) > 100 else ''}")
        logger.info(f"参考答案: {request.reference_answer[:100]}{'...' if len(request.reference_answer) > 100 else ''}")
        logger.info(f"题型: {request.question_type}, 分值: {request.question_score}")
        logger.info(f"图片数量: {len(request.image_bases)}")

        # 参数验证
        if not request.image_bases:
            raise HTTPException(status_code=400, detail="请至少上传一张图片")

        # 调用 LongCat 批改服务
        logger.info(f"正在调用LongCat批改服务...")
        result = await longcat_grading_service.grade_with_image(
            question_content=request.question_content,
            reference_answer=request.reference_answer,
            image_bases=request.image_bases,
            question_type=request.question_type,
            question_score=request.question_score
        )

        end_time = time.time()
        duration = end_time - start_time
        logger.info(f"图片批改完成，耗时: {duration:.2f} 秒")
        logger.info(f"==========================================")

        return JSONResponse(content=result)

    except HTTPException:
        end_time = time.time()
        duration = end_time - start_time
        logger.info(f"图片批改失败，耗时: {duration:.2f} 秒")
        logger.info(f"==========================================")
        raise
    except Exception as e:
        end_time = time.time()
        duration = end_time - start_time
        logger.error(f"图片答案批改接口错误，耗时: {duration:.2f} 秒")
        logger.error(f"错误: {str(e)}")
        logger.error(f"==========================================")
        raise HTTPException(status_code=500, detail=f"批改失败: {str(e)}")


@ai_router.post("/analyze-exam")
async def analyze_exam(request: ExamAnalysisRequest):
    """
    整卷分析接口

    使用 LongCat 对整份试卷进行全面批改和分析，包括：
    1. 对每道主观题进行详细批改
    2. 对客观题答题情况进行统计和分析
    3. 计算总分和各部分得分率
    4. 提供全面的考试分析报告
    """
    start_time = time.time()
    try:
        logger.info("\n==========================================")
        logger.info("收到整卷分析请求 @ {}".format(time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(start_time))))
        logger.info("题目数量: {}".format(len(request.questions)))
        logger.info("总分值: {}".format(request.total_score))
        logger.info("考试时长: {}".format(request.time_limit))

        # 调用整卷分析服务
        logger.info("正在调用整卷分析服务...")
        result = await exam_analysis_service.analyze_exam({
            "total_score": request.total_score,
            "time_limit": request.time_limit,
            "questions": request.questions
        })

        end_time = time.time()
        duration = end_time - start_time
        logger.info("整卷分析完成，耗时: {:.2f} 秒".format(duration))
        logger.info("==========================================")

        return JSONResponse(content=result)

    except Exception as e:
        end_time = time.time()
        duration = end_time - start_time
        logger.error("整卷分析失败，耗时: {:.2f} 秒".format(duration))
        logger.error("错误: {}".format(str(e)))
        logger.error("==========================================")
        raise HTTPException(status_code=500, detail="分析失败: {}".format(str(e)))


@ai_router.get("/health")
async def health_check():
    """AI 服务健康检查"""
    return {
        "status": "healthy",
        "service": "ai-service",
        "models": {
            "recognition": "glm-4.6v-flash",
            "grading": "glm-4-plus",
            "grading_with_image": "longcat-long-vision",
            "exam_analysis": "longcat-long-vision"
        }
    }
