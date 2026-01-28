"""
工具服务相关的数据模型
"""
from pydantic import BaseModel
from typing import Optional


class PDFExtractRequest(BaseModel):
    """PDF 提取请求"""
    output_dir: Optional[str] = "./output"
    lang: Optional[str] = "ch"
    parse_method: Optional[str] = "auto"
    formula_enable: Optional[bool] = True
    table_enable: Optional[bool] = True
    start_page_id: Optional[int] = 0
    end_page_id: Optional[int] = None


class PDFExtractResponse(BaseModel):
    """PDF 提取响应"""
    success: bool
    message: str
    data: Optional[dict] = None
    error: Optional[str] = None


class PDFDetectResponse(BaseModel):
    """PDF 检测响应"""
    success: bool
    data: Optional[dict] = None
    error: Optional[str] = None


class MDConvertRequest(BaseModel):
    """Markdown 转换请求"""
    max_workers: Optional[int] = 3
    batch_size: Optional[int] = 10


class MDConvertResponse(BaseModel):
    """Markdown 转换响应"""
    success: bool
    message: str
    data: Optional[dict] = None
    error: Optional[str] = None
