"""
路由模块

包含所有 API 路由
"""
from fastapi import APIRouter
from .ai_router import ai_router
from .md_tools import router as md_tools_router
from .pdf_tools import router as pdf_tools_router

# 创建主 API 路由器
api_router = APIRouter()

# 包含各个子路由
api_router.include_router(ai_router, prefix="/ai", tags=["AI Services"])
api_router.include_router(md_tools_router, prefix="/tools/md", tags=["Markdown Tools"])
api_router.include_router(pdf_tools_router, prefix="/tools/pdf", tags=["PDF Tools"])

__all__ = ["api_router"]
