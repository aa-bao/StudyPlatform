"""
AI 服务相关的数据模型
"""
from pydantic import BaseModel
from typing import Optional, List


class ImageRecognitionRequest(BaseModel):
    """图片识别请求"""
    pass  # 使用 multipart/form-data，不需要定义字段


class ImageRecognitionResponse(BaseModel):
    """图片识别响应"""
    code: int
    message: str
    data: Optional[dict] = None


class GradingRequest(BaseModel):
    """智能批改请求"""
    question_content: str
    user_answer: str
    reference_answer: str
    question_type: int
    max_score: Optional[int] = 100


class GradingResponse(BaseModel):
    """智能批改响应"""
    code: int
    message: str
    data: Optional[dict] = None
