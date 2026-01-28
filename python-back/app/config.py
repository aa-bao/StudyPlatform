"""
应用配置管理

专注于 AI 服务配置
"""
from pathlib import Path
from pydantic_settings import BaseSettings
from typing import List


# 获取项目根目录（python-back 目录）
BASE_DIR = Path(__file__).resolve().parent.parent


class Settings(BaseSettings):
    """应用配置 - AI 服务专用"""

    # ========== 服务配置 ==========
    APP_NAME: str = "考研平台 Python 后端 - AI 服务"
    APP_PORT: int = 8082
    APP_HOST: str = "0.0.0.0"

    # ========== GLM API 配置 ==========
    GLM_API_KEY: str
    GLM_MODEL_IMAGE: str = "glm-4v-flash"      # 图片识别模型
    GLM_MODEL_GRADING: str = "glm-4-plus"      # 智能批改模型
    GLM_TEMPERATURE: float = 0.1
    GLM_MAX_TOKENS: int = 8000
    GLM_TIMEOUT: int = 60
    GLM_BASE_URL: str = "https://open.bigmodel.cn/api/paas/v4/"

    # ========== CORS 配置 ==========
    ALLOWED_ORIGINS: str = "http://localhost:5173,http://localhost:8081"

    # ========== 日志配置 ==========
    LOG_LEVEL: str = "INFO"

    @property
    def cors_origins(self) -> List[str]:
        """获取 CORS 允许的源列表"""
        return [origin.strip() for origin in self.ALLOWED_ORIGINS.split(",")]

    class Config:
        """配置加载"""
        # 从项目根目录加载 .env 文件
        env_file = BASE_DIR / ".env"
        env_file_encoding = "utf-8"
        case_sensitive = True


# 全局配置实例
settings = Settings()
