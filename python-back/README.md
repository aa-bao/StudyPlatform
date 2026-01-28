# 考研平台 Python 后端 - AI 服务

> 专注于 AI 服务和数据处理工具的 Python 后端服务

## 📋 项目概述

本项目是考研平台的 Python 后端服务，专注于提供 AI 增强功能和数据处理工具，与 Java 后端（业务逻辑）配合使用。

### 核心功能

#### 🤖 AI 服务
- **图片识别**: 使用智谱 GLM-4V-Flash 模型识别图片中的题目内容
  - 支持识别题目、选项、答案、解析
  - 自动处理 LaTeX 数学公式
  - 智能清理和修复公式错误

- **智能批改**: 使用智谱 GLM-4-Plus 模型批改主观题
  - 按步骤给分
  - 提供详细的批改反馈
  - 标注优点和不足

#### 🛠️ 数据处理工具
- **PDF 题目提取**: 使用 MinerU 将 PDF 文件转换为 Markdown 格式
- **Markdown 转 JSON**: 将 Markdown 格式的题目转换为结构化 JSON 数据

---

## 🏗️ 项目结构

```
python-back/
├── .env                    # 环境变量配置
├── requirements.txt        # Python 依赖
├── run_server.py          # 服务启动脚本
├── README.md              # 项目文档
│
└── app/                   # 应用主目录
    ├── __init__.py        # 应用初始化
    ├── config.py          # 配置管理
    ├── main.py            # FastAPI 应用入口
    │
    ├── models/            # 数据模型（Pydantic）
    │   ├── __init__.py
    │   ├── ai_models.py       # AI 服务相关模型
    │   └── tool_models.py     # 工具服务相关模型
    │
    ├── routers/           # API 路由
    │   ├── __init__.py
    │   ├── ai_router.py       # AI 服务路由
    │   ├── md_tools.py        # Markdown 工具路由
    │   └── pdf_tools.py       # PDF 工具路由
    │
    ├── services/          # 业务逻辑服务
    │   ├── __init__.py
    │   ├── image_recognition_service.py  # 图片识别服务
    │   └── grading_service.py            # 智能批改服务
    │
    └── tools/             # 数据处理工具
        ├── __init__.py
        ├── MDToJson/      # Markdown 转 JSON 工具
        └── PDFExtract/    # PDF 提取工具
```

---

## 🚀 快速开始

### 环境要求

- Python 3.10+
- 智谱 AI API Key

### 安装依赖

```bash
cd python-back
pip install -r requirements.txt
```

### 配置环境变量

编辑 `.env` 文件：

```env
# GLM API 配置
GLM_API_KEY=your_zhipu_api_key_here

# 服务配置
APP_PORT=8082
APP_HOST=0.0.0.0

# CORS 配置
ALLOWED_ORIGINS=http://localhost:5173,http://localhost:8081

# 日志配置
LOG_LEVEL=INFO
```

### 启动服务

**方式一：使用 Python 直接运行**

```bash
python -m app.main
```

**方式二：使用 Uvicorn**

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8082 --reload
```

**方式三：使用启动脚本**

```bash
python run_server.py
```

### 验证服务

- 访问根路径: http://localhost:8082/
- API 文档: http://localhost:8082/docs
- 健康检查: http://localhost:8082/health

---

## 📡 API 接口

### AI 服务接口

#### 1. 图片识别

```
POST /ai/recognize
Content-Type: multipart/form-data

参数:
  file: 图片文件

响应:
{
  "code": 200,
  "message": "识别成功",
  "data": {
    "type": 1,
    "content": "题目内容（含LaTeX公式）",
    "options": [...],
    "answer": "答案",
    "analysis": "解析",
    "tags": ["标签1"],
    "difficulty": 3
  }
}
```

#### 2. 智能批改

```
POST /ai/grade
Content-Type: application/json

{
  "question_content": "题目内容",
  "user_answer": "学生答案",
  "reference_answer": "参考答案",
  "question_type": 4,
  "max_score": 100
}

响应:
{
  "code": 200,
  "message": "批改成功",
  "data": {
    "score": 85,
    "feedback": "详细反馈",
    "strengths": ["优点1"],
    "weaknesses": ["不足1"]
  }
}
```

### 数据处理工具接口

#### 3. PDF 提取

```
POST /tools/pdf/pdf-extract
Content-Type: multipart/form-data

参数:
  file: PDF 文件
  lang: ch
  parse_method: auto
  formula_enable: true
  table_enable: true
```

#### 4. Markdown 转 JSON

```
POST /tools/md/md-to-json
Content-Type: multipart/form-data

参数:
  file: Markdown 文件
  max_workers: 3
  batch_size: 10
```

---

## ⚙️ 配置说明

### GLM 模型配置

在 `app/config.py` 中配置：

```python
GLM_MODEL_IMAGE: str = "glm-4v-flash"      # 图片识别模型
GLM_MODEL_GRADING: str = "glm-4-plus"      # 智能批改模型
GLM_TEMPERATURE: float = 0.1                # 温度参数
GLM_MAX_TOKENS: int = 8000                  # 最大 token 数
GLM_TIMEOUT: int = 60                       # 超时时间（秒）
```

### CORS 配置

允许跨域访问的源列表：

```python
ALLOWED_ORIGINS: str = "http://localhost:5173,http://localhost:8081"
```

---

## 🔧 开发指南

### 添加新的 AI 服务

1. 在 `app/services/` 下创建服务文件
2. 实现服务类
3. 在 `app/routers/ai_router.py` 中添加路由
4. 在 `app/models/ai_models.py` 中定义数据模型

### 添加新的数据处理工具

1. 在 `app/tools/` 下创建工具目录
2. 实现工具逻辑
3. 在 `app/routers/` 下创建对应的路由文件
4. 在 `app/models/tool_models.py` 中定义数据模型

### 代码规范

- 使用 Python 3.10+ 类型注解
- 遵循 PEP 8 代码规范
- 使用 Pydantic 进行数据验证
- 使用 FastAPI 自动生成 API 文档

---

## 📦 依赖说明

### Web 框架
- `fastapi`: 现代化 Web 框架
- `uvicorn`: ASGI 服务器
- `python-multipart`: 文件上传支持

### AI SDK
- `openai`: 智谱 GLM API 调用（兼容 OpenAI SDK）

### 数据验证
- `pydantic`: 数据验证和序列化
- `pydantic-settings`: 配置管理

### 日志
- `loguru`: 日志记录

### PDF 处理
- `PyMuPDF`: PDF 文件读取
- `mineru`: PDF 转换为 Markdown

---

## 🔗 与 Java 后端集成

### Java 调用 Python 服务

Java 后端通过 `PythonBackendClient` 调用 Python 服务：

```java
// 图片识别
QuestionDTO result = pythonBackendClient.recognizeQuestion(imageFile);

// 智能批改
GradingResult result = pythonBackendClient.gradeAnswer(request);
```

### 服务职责划分

**Java 后端负责**:
- 用户认证和授权
- 业务逻辑处理
- 数据库操作
- 文件管理
- API 网关（统一对外接口）

**Python 后端负责**:
- AI 图片识别
- AI 智能批改
- PDF 题目提取
- Markdown 转 JSON

---

## 🐛 常见问题

### 1. ModuleNotFoundError: No module named 'xxx'

```bash
pip install -r requirements.txt
```

### 2. GLM API 调用失败

检查 `.env` 文件中的 `GLM_API_KEY` 是否正确配置。

### 3. CORS 跨域问题

检查 `.env` 中的 `ALLOWED_ORIGINS` 是否包含前端地址。

### 4. PDF 提取失败

确保已安装 MinerU 依赖：
```bash
pip install mineru
```

---

## 📝 更新日志

### v1.0.0 (2026-01-25)

**重构优化**:
- ✅ 精简项目结构，专注于 AI 服务
- ✅ 删除数据库相关模块（由 Java 后端处理）
- ✅ 优化配置管理，分离图片识别和批改模型
- ✅ 添加完整的项目文档
- ✅ 优化 API 路由结构
- ✅ 更新依赖列表

**功能特性**:
- 🤖 AI 图片识别（GLM-4V-Flash）
- 🤖 AI 智能批改（GLM-4-Plus）
- 🛠️ PDF 题目提取（MinerU）
- 🛠️ Markdown 转 JSON

---

## 📄 许可证

MIT License

---

## 👥 联系方式

- 项目地址: [GitHub](https://github.com/your-repo)
- 问题反馈: [Issues](https://github.com/your-repo/issues)
