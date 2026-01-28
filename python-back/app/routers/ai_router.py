"""
AI 服务路由
"""
from fastapi import APIRouter, UploadFile, File, HTTPException
from fastapi.responses import JSONResponse
from loguru import logger
from app.services.image_recognition_service import ImageRecognitionService
from app.services.grading_service import GradingService
from pydantic import BaseModel

# 创建路由器
ai_router = APIRouter(prefix="/ai", tags=["AI服务"])

# 初始化服务
image_service = ImageRecognitionService()
grading_service = GradingService()


class GradingRequest(BaseModel):
    """批改请求模型"""
    question_content: str
    user_answer: str
    reference_answer: str
    question_type: int = 4


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
    智能批改接口

    使用 GLM-4.7 对主观题进行智能批改
    """
    try:
        logger.info(f"接收到批改请求，题型: {request.question_type}")

        # 调用批改服务
        result = await grading_service.grade_answer(
            question_content=request.question_content,
            user_answer=request.user_answer,
            reference_answer=request.reference_answer,
            question_type=request.question_type
        )

        return JSONResponse(content=result)

    except Exception as e:
        logger.error(f"智能批改接口错误: {str(e)}")
        raise HTTPException(status_code=500, detail=f"批改失败: {str(e)}")


@ai_router.get("/health")
async def health_check():
    """AI 服务健康检查"""
    return {
        "status": "healthy",
        "service": "ai-service",
        "models": {
            "recognition": "glm-4.6v-flash",
            "grading": "glm-4-plus"
        }
    }
