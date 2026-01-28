"""
数据模型定义

使用 Pydantic 定义请求和响应的数据模型
"""

# 重新导出常用模型
from .ai_models import *
from .tool_models import *

__all__ = [
    # AI 服务相关模型
    "ImageRecognitionRequest",
    "ImageRecognitionResponse",
    "GradingRequest",
    "GradingResponse",

    # 工具服务相关模型
    "PDFExtractRequest",
    "PDFExtractResponse",
    "MDConvertRequest",
    "MDConvertResponse",
]
