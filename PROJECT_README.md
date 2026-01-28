# KaoYanPlatform 项目技术全景文档

> **本文档用途**: 专为 LLM 对话和其他开发者提供的完整技术参考文档
> **更新原则**: 所有内容基于实际代码验证，确保准确性
> **最后更新**: 2026-01-26

---

## 目录

- [1. 项目概述](#1-项目概述)
  - [1.1 项目简介](#11-项目简介)
  - [1.2 核心特性](#12-核心特性)
  - [1.3 技术亮点](#13-技术亮点)
  - [1.4 目录结构](#14-目录结构)
  - [1.5 默认账号](#15-默认账号)
- [2. 技术架构总览](#2-技术架构总览)
  - [2.1 三层架构设计](#21-三层架构设计)
  - [2.2 技术栈清单](#22-技术栈清单)
  - [2.3 服务端口说明](#23-服务端口说明)
  - [2.4 数据流向图](#24-数据流向图)
  - [2.5 技术选型理由](#25-技术选型理由)
- [3. 前端技术栈详解](#3-前端技术栈详解)
  - [3.1 核心框架与工具](#31-核心框架与工具)
  - [3.2 Vue Router 路由管理](#32-vue-router-路由管理)
  - [3.3 Pinia 状态管理](#33-pinia-状态管理)
  - [3.4 Element Plus UI 组件库](#34-element-plus-ui-组件库)
  - [3.5 KaTeX 数学公式渲染](#35-katex-数学公式渲染)
  - [3.6 ECharts 数据可视化](#36-echarts-数据可视化)
  - [3.7 Axios API 封装](#37-axios-api-封装)
  - [3.8 前端目录结构详解](#38-前端目录结构详解)
  - [3.9 核心页面实现](#39-核心页面实现)
  - [3.10 前端开发最佳实践](#310-前端开发最佳实践)
- [4. Java 后端技术栈详解](#4-java-后端技术栈详解)
  - [4.1 核心框架与依赖](#41-核心框架与依赖)
  - [4.2 项目结构说明](#42-项目结构说明)
  - [4.3 核心配置类](#43-核心配置类)
  - [4.4 实体类设计](#44-实体类设计)
  - [4.5 MyBatis Plus 集成](#45-mybatis-plus-集成)
  - [4.6 RESTful API 设计](#46-restful-api-设计)
  - [4.7 PDF 导出功能](#47-pdf-导出功能)
  - [4.8 Python 后端客户端](#48-python-后端客户端)
- [5. Python 后端技术栈详解](#5-python-后端技术栈详解)
  - [5.1 FastAPI 框架](#51-fastapi-框架)
  - [5.2 项目结构](#52-项目结构)
  - [5.3 数据模型](#53-数据模型)
  - [5.4 核心服务](#54-核心服务)
  - [5.5 路由结构](#55-路由结构)
  - [5.6 目录结构](#56-目录结构)
- [6. 数据库设计详解](#6-数据库设计详解)
  - [6.1 用户与权限模块](#61-用户与权限模块)
  - [6.2 科目与知识体系模块](#62-科目与知识体系模块)
  - [6.3 题目管理模块](#63-题目管理模块)
  - [6.4 考试系统模块](#64-考试系统模块)
  - [6.5 刷题与错题模块](#65-刷题与错题模块)
  - [6.6 收藏夹模块](#66-收藏夹模块)
  - [6.7 索引优化策略](#67-索引优化策略)
- [7. 核心功能模块详解](#7-核心功能模块详解)
  - [7.1 用户端功能](#71-用户端功能)
  - [7.2 管理端功能](#72-管理端功能)
  - [7.3 AI 功能集成](#73-ai-功能集成)
  - [7.4 题目导入导出](#74-题目导入导出)
  - [7.5 错题本功能](#75-错题本功能)
- [8. API 接口文档](#8-api-接口文档)
  - [8.1 用户端 API](#81-用户端-api)
  - [8.2 管理端 API](#82-管理端-api)
  - [8.3 公共 API](#83-公共-api)
  - [8.4 Python 后端 API](#84-python-后端-api)
  - [8.5 API 文档访问](#85-api-文档访问)
- [9. 配置说明详解](#9-配置说明详解)
  - [9.1 Java 后端配置](#91-java-后端配置)
  - [9.2 Python 后端配置](#92-python-后端配置)
  - [9.3 Vue 前端配置](#93-vue-前端配置)
  - [9.4 环境变量配置总结](#94-环境变量配置总结)
  - [9.5 配置检查清单](#95-配置检查清单)
- [10. 环境搭建与部署指南](#10-环境搭建与部署指南)
  - [10.1 环境要求](#101-环境要求)
  - [10.2 快速启动指南](#102-快速启动指南)
  - [10.3 完整启动流程](#103-完整启动流程)
  - [10.4 生产环境部署](#104-生产环境部署)
  - [10.5 Docker 部署](#105-docker-部署)
  - [10.6 部署检查清单](#106-部署检查清单)
- [11. 开发规范与最佳实践](#11-开发规范与最佳实践)
  - [11.1 前端开发规范](#111-前端开发规范)
  - [11.2 Java 后端开发规范](#112-java-后端开发规范)
  - [11.3 Python 后端开发规范](#113-python-后端开发规范)
  - [11.4 Git 工作流规范](#114-git-工作流规范)
  - [11.5 AI 集成规范](#115-ai-集成规范)
  - [11.6 性能优化建议](#116-性能优化建议)
- [12. 常见问题与故障排查](#12-常见问题与故障排查)
  - [12.1 启动问题](#121-启动问题)
  - [12.2 连接问题](#122-连接问题)
  - [12.3 AI 服务问题](#123-ai-服务问题)
  - [12.4 性能问题](#124-性能问题)
  - [12.5 部署问题](#125-部署问题)
  - [12.6 调试技巧](#126-调试技巧)
  - [12.7 常用命令速查](#127-常用命令速查)

---

## 1. 项目概述

### 1.1 项目简介

**KaoYanPlatform** 是一款面向考研学生的在线刷题与学习管理平台，采用现代化的前后端分离架构设计。

#### 项目定位

- **目标用户**: 考研学生（数学、英语、政治、专业课等）
- **核心价值**: 智能化刷题体验 + AI 辅助学习 + 学习数据可视化
- **技术特点**: Java + Python 双后端架构，集成 GLM 大模型

#### 主要功能

| 功能模块 | 详细说明 |
|---------|---------|
| **多模式刷题** | 逐题精练、专项突破、真题模考、套卷刷题 |
| **AI 智能批改** | 集成智谱 GLM-4.7，主观题自动评分与反馈 |
| **AI 图片识别** | 使用 GLM-4.6V 自动识别图片中的题目，支持 LaTeX 数学公式 |
| **智能题库管理** | 支持 PDF/Markdown 题目智能提取、批量导入导出 |
| **学习数据分析** | ECharts 雷达图可视化展示学习进度 |
| **错题本系统** | 自动收藏错题、标签分类、重做巩固 |

### 1.2 核心特性

#### AI 能力

| 特性 | 技术实现 | 说明 |
|------|---------|------|
| **图片识别** | GLM-4.6V | 国内顶尖多模态大模型，支持数学公式识别 |
| **智能批改** | GLM-4.7 | 按步骤给分，提供详细反馈 |
| **题目提取** | MinerU + GLM | PDF → Markdown → 结构化 JSON |
| **公式支持** | LaTeX | 全链路支持数学公式（识别 → 存储 → 渲染 → 导出） |

#### 题库管理

| 特性 | 技术实现 | 说明 |
|------|---------|------|
| **JSON 字段优化** | MySQL JSON 类型 | 题目内容整合为单一 JSON 字段，减少冗余 |
| **多层级科目体系** | 树形结构 | 具体考试科目 → 知识模块 → 知识点 → 题型 |
| **Scope 多对多映射** | 逗号分隔字符串 | 知识点跨科目共享（如"高数"属于数一、数二、数三） |
| **批量导入导出** | JSON / PDF | 支持 JSON 批量导入，PDF 格式导出（含公式） |

#### 考试系统

| 特性 | 技术实现 | 说明 |
|------|---------|------|
| **倒计时持久化** | LocalStorage + 后端时间戳 | 刷新页面不丢失考试进度 |
| **答题快照恢复** | JSON 快照字段 | 异常退出可恢复答题进度 |
| **切屏检测** | Visibility API + 后端计数 | 记录切换题目次数 |
| **未完成考试提醒** | 弹窗强制确认 | 登录时自动检测未完成考试 |

### 1.3 技术亮点

#### 1. JSON 字段优化设计

**问题**: 传统设计将题目内容、选项、答案、解析分表存储，导致：
- 表结构复杂，JOIN 查询多
- 字段冗余，存储浪费
- 扩展性差

**解决方案**: 使用 MySQL JSON 字段

```sql
CREATE TABLE question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type TINYINT NOT NULL,
    content_json JSON NOT NULL,  -- 核心优化
    difficulty TINYINT DEFAULT 3,
    INDEX idx_type (type),
    INDEX idx_difficulty (difficulty)
);
```

**优势**:
- ✅ 减少表数量，简化查询
- ✅ 不同题型可有差异化结构
- ✅ MyBatis Plus 自动序列化/反序列化

#### 2. Java + Python 双后端架构

```
┌─────────────┐
│  Vue 前端   │
└──────┬──────┘
       │ HTTP API
       ↓
┌─────────────────────────────┐
│      Java 后端 (8081)        │
│  - 业务逻辑                  │
│  - 数据持久化                │
│  - API 网关                 │
└──────┬──────────────────────┘
       │ HTTP API (可选)
       ↓
┌─────────────────────────────┐
│    Python 后端 (8082)        │
│  - AI 图片识别              │
│  - AI 智能批改              │
│  - PDF 题目提取             │
└─────────────────────────────┘
```

**优势**:
- ✅ 各司其职：Java 专注业务，Python 专注 AI
- ✅ 自动降级：Python 服务失败时降级到 Java 实现
- ✅ 技术栈最优：Spring Boot 稳定，Python AI 生态丰富

#### 3. 多层级科目体系 + Scope 映射

**科目层级**:
```
L1 - 具体考试科目（政治、英语一、数学一、408）
  └─ L2 - 知识模块（马原、毛中特、高等数学）
      └─ L3 - 知识点（函数与极限、二重积分）
          └─ L4 - 题型（泰勒公式、洛必达法则）
```

**Scope 多对多映射**:
```sql
-- subject 表
scope VARCHAR(50) DEFAULT '1,2,3'  -- 逗号分隔的适用大纲
```

**示例**:
- `scope = "4,5,6"` → 属于数学一、二、三
- `scope = "2,3"` → 属于英语一、二
- `scope = "1,2,3,4,5,6"` → 属于所有数学

**优势**:
- ✅ 知识点跨科目共享，减少冗余
- ✅ 灵活适配不同考试大纲
- ✅ 按需筛选题目

#### 4. 全链路 LaTeX 公式支持

```
图片识别 (GLM-4.6V-Flash)
       ↓
    转换为 LaTeX
       ↓
  存储 (JSON 字段)
       ↓
   前端渲染 (KaTeX)
       ↓
   PDF 导出 (Flying Saucer)
```

**公式格式规范**:
- **行内公式**: `$...$` → `设函数 $f(x) = x^3 - 3x + 1$`
- **块级公式**: `$$...$$` → `答案：$$3x^2-3$$`

**常见符号**:
- 分数：`\frac{a}{b}`
- 根号：`\sqrt{x}`
- 积分：`\int_{a}^{b}`
- 求和：`\sum_{i=1}^{n}`
- 极限：`\lim_{x \to 0}`

#### 5. 考试会话增强功能

| 功能 | 实现方式 | 作用 |
|------|---------|------|
| **倒计时持久化** | 前端 LocalStorage + 后端 `expected_end_time` | 刷新页面不丢失进度 |
| **答题快照** | `exam_session.snapshot_answers` JSON 字段 | 异常退出可恢复 |
| **切屏检测** | `document.visibilityState` + `switch_count` 字段 | 防止作弊 |
| **浏览器返回阻止** | `window.history.pushState` + `beforeunload` | 误触不丢失数据 |
| **未完成考试提醒** | 登录时查询 `status=0` 的会话 | 强制继续或结束 |

### 1.4 目录结构

```
KaoYanPlatform/
├── java-back/                    # Java 后端服务 (端口: 8081)
│   ├── src/main/java/org/example/kaoyanplatform/
│   │   ├── client/               # 外部服务客户端
│   │   │   └── PythonBackendClient.java  # Python 后端客户端
│   │   ├── common/               # 通用类
│   │   │   └── Result.java       # 统一响应格式
│   │   ├── config/               # 配置类
│   │   │   ├── AsyncConfig.java
│   │   │   ├── ConditionalDataSourceConfig.java
│   │   │   ├── CorsConfig.java
│   │   │   ├── Knife4jConfig.java
│   │   │   ├── MybatisPlusConfig.java
│   │   │   ├── RestTemplateConfig.java
│   │   │   ├── SecurityConfig.java
│   │   │   └── WebConfig.java
│   │   ├── constant/             # 常量定义
│   │   │   └── SubjectLevelConstants.java
│   │   ├── controller/           # 控制器层
│   │   │   ├── AdminController.java
│   │   │   ├── CommonController.java
│   │   │   ├── ExamPaperController.java
│   │   │   ├── ExamRecordController.java
│   │   │   ├── ExamSessionController.java
│   │   │   ├── ExerciseBookController.java
│   │   │   ├── FavoriteFolderController.java
│   │   │   ├── FileController.java
│   │   │   ├── QuestionController.java
│   │   │   ├── RecordController.java
│   │   │   ├── SubjectController.java
│   │   │   ├── UserController.java
│   │   │   └── UserProgressController.java
│   │   ├── entity/               # 实体类
│   │   │   ├── AnswerRecord.java
│   │   │   ├── BookSubjectRel.java
│   │   │   ├── ErrorQuestion.java
│   │   │   ├── ExamPaper.java
│   │   │   ├── ExamRecord.java
│   │   │   ├── ExamSession.java
│   │   │   ├── ExerciseBook.java
│   │   │   ├── FavoriteFolder.java
│   │   │   ├── Question.java     
│   │   │   ├── QuestionBookRel.java
│   │   │   ├── QuestionPaperRel.java
│   │   │   ├── QuestionSubjectRel.java
│   │   │   ├── Subject.java
│   │   │   ├── SubjectWeightRel.java
│   │   │   ├── User.java
│   │   │   └── dto/              # 数据传输对象
│   │   ├── enums/                # 枚举类
│   │   │   └── QuestionType.java
│   │   ├── handler/              # 处理器
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── MyMetaObjectHandler.java
│   │   ├── mapper/               # 数据访问层
│   │   │   ├── AnswerRecordMapper.java
│   │   │   ├── BookSubjectRelMapper.java
│   │   │   ├── ErrorQuestionMapper.java
│   │   │   ├── ExamPaperMapper.java
│   │   │   ├── ExamRecordMapper.java
│   │   │   ├── ExamSessionMapper.java
│   │   │   ├── ExerciseBookMapper.java
│   │   │   ├── FavoriteFolderMapper.java
│   │   │   ├── QuestionBookRelMapper.java
│   │   │   ├── QuestionMapper.java
│   │   │   ├── QuestionPaperRelMapper.java
│   │   │   ├── QuestionSubjectRelMapper.java
│   │   │   ├── SubjectMapper.java
│   │   │   ├── SubjectWeightRelMapper.java
│   │   │   └── UserMapper.java
│   │   ├── service/              # 业务逻辑层
│   │   │   ├── AdminService.java
│   │   │   ├── QuestionService.java
│   │   │   ├── ExamSessionService.java
│   │   │   ├── PdfExportService.java
│   │   │   └── impl/             # 业务逻辑实现
│   │   ├── util/                 # 工具类
│   │   │   └── MathAnswerMatcher.java
│   │   └── KaoYanPlatformApplication.java
│   ├── src/main/resources/
│   │   └── application.yml       # 核心配置文件
│   └── pom.xml                   # Maven 依赖配置
│
├── vue-front/                    # Vue 前端应用 (端口: 5173)
│   ├── src/
│   │   ├── api/                  # API 接口封装
│   │   │   ├── common.js
│   │   │   ├── examSession.js
│   │   │   ├── paper.js
│   │   │   ├── questionImportExport.js
│   │   │   ├── subject.js
│   │   │   └── user.js
│   │   ├── assets/               # 静态资源
│   │   ├── components/           # 公共组件
│   │   ├── layout/               # 布局组件
│   │   │   ├── AdminLayout.vue   # 管理端布局
│   │   │   └── UserLayout.vue    # 用户端布局
│   │   ├── router/               # 路由配置
│   │   │   └── index.js
│   │   ├── stores/               # Pinia 状态管理
│   │   │   ├── exam.js           # 考试状态
│   │   │   └── user.js           # 用户状态
│   │   ├── utils/                # 工具函数
│   │   ├── views/                # 页面视图
│   │   │   ├── Login.vue
│   │   │   ├── Home.vue
│   │   │   ├── Dashboard.vue     # 备考面板
│   │   │   ├── SubjectList.vue
│   │   │   ├── CorrectionNotebook.vue  # 错题本
│   │   │   ├── PaperList.vue
│   │   │   ├── Exercise.vue
│   │   │   ├── quiz/             # 刷题模块
│   │   │   │   ├── SinglePractice.vue
│   │   │   │   ├── TopicDrill.vue
│   │   │   │   └── MockExam.vue
│   │   │   └── admin/            # 管理端页面
│   │   │       ├── AdminHome.vue
│   │   │       ├── UserManage.vue
│   │   │       ├── SubjectManage.vue
│   │   │       ├── QuestionManage.vue
│   │   │       ├── QuestionImport.vue
│   │   │       ├── QuestionExport.vue
│   │   │       ├── BookManage.vue
│   │   │       ├── PaperManage.vue
│   │   │       ├── ExamRecordManage.vue
│   │   │       ├── MistakeMonitor.vue
│   │   │       └── UserProgressMonitor.vue
│   │   ├── App.vue
│   │   └── main.js
│   ├── vite.config.js            # Vite 配置
│   └── package.json              # npm 依赖配置
│
├── python-back/                  # Python AI 服务 (端口: 8082)
│   ├── app/
│   │   ├── __init__.py
│   │   ├── config.py             # 配置管理
│   │   ├── main.py               # FastAPI 应用入口
│   │   ├── models/               # 数据模型
│   │   │   ├── ai_models.py      # AI 服务相关模型
│   │   │   └── tool_models.py    # 工具服务相关模型
│   │   ├── routers/              # API 路由
│   │   │   ├── ai_router.py      # AI 服务路由
│   │   │   ├── md_tools.py       # Markdown 工具路由
│   │   │   └── pdf_tools.py      # PDF 工具路由
│   │   ├── services/             # 业务逻辑服务
│   │   │   ├── image_recognition_service.py  # 图片识别服务
│   │   │   └── grading_service.py            # 智能批改服务
│   │   └── tools/                # 数据处理工具
│   │       ├── MDToJson/         # Markdown 转 JSON 工具
│   │       └── PDFExtract/       # PDF 提取工具
│   ├── .env                      # 环境变量配置
│   ├── requirements.txt          # Python 依赖
│   └── run_server.py             # 服务启动脚本
│
├── docs/                         # 项目文档
│   ├── ai/                       # AI 相关文档
│   ├── architecture/             # 架构设计文档
│   ├── database/                 # 数据库设计文档
│   ├── features/                 # 功能模块文档
│   ├── format/                   # 数据格式文档
│   └── guide/                    # 使用指南
│
├── kaoyan_platform.sql.md        # 数据库结构文档（最新）
├── README.md                     # 项目简介（对外）
└── PROJECT_README.md             # 本文档（技术全景）
```

### 1.5 默认账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | `admin` | `123123` | 拥有所有权限，可访问管理端 |
| 普通用户 | `student` | `123123` | 普通学生账号，可访问用户端 |

**访问地址**:
- 前端: http://localhost:5173
- Java 后端: http://localhost:8081
- API 文档 (Knife4j): http://localhost:8081/doc.html
- Python 后端: http://localhost:8082
- Python API 文档: http://localhost:8082/docs

---

## 2. 技术架构总览

### 2.1 三层架构设计

KaoYanPlatform 采用经典的**前后端分离** + **双后端**架构，实现了业务逻辑、AI 服务和用户界面的清晰分离。

```
┌─────────────────────────────────────────────────────────────────┐
│                      前端层 (Vue 3)                             │
│                     端口: 5173                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │   用户端     │  │   管理端     │  │  刷题模块    │          │
│  │  /user/*     │  │  /admin/*    │  │  /quiz/*     │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│                                                                 │
│  Vue 3.5.26 + Vite 7.3.0 + Element Plus + KaTeX + ECharts      │
│  Pinia 3.0.4 (状态管理) + Vue Router 4.6.4 (路由)              │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTP/REST API (Axios)
                             ↓
┌─────────────────────────────────────────────────────────────────┐
│              Java 后端服务层 (Spring Boot 3.3.5)                │
│                     端口: 8081                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  Controller 层 (接收请求，返回响应)                      │  │
│  ├──────────────────────────────────────────────────────────┤  │
│  │  Service 层 (业务逻辑，数据处理)                         │  │
│  │  ├── QuestionService (题目管理)                         │  │
│  │  ├── ExamSessionService (考试会话)                      │  │
│  │  ├── PdfExportService (PDF 导出)                        │  │
│  │  └── PythonBackendClient (调用 Python AI 服务)          │  │
│  ├──────────────────────────────────────────────────────────┤  │
│  │  Mapper 层 (数据访问，MyBatis Plus 3.5.5)              │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
│  Spring Security (安全认证) + Knife4j (API 文档)              │
│  MySQL 8.0 (数据持久化)                                        │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTP/REST API (可选调用)
                             ↓
┌─────────────────────────────────────────────────────────────────┐
│              Python AI 服务层 (FastAPI)                        │
│                     端口: 8082                                  │
│  ┌──────────────────┐  ┌──────────────────┐                    │
│  │  图片识别服务     │  │  智能批改服务    │                    │
│  │  GLM-4.6V-Flash  │  │  GLM-4.7         │                    │
│  └──────────────────┘  └──────────────────┘                    │
│  ┌──────────────────┐  ┌──────────────────┐                    │
│  │  PDF 题目提取    │  │  Markdown 转 JSON │                    │
│  │  MinerU + GLM    │  │  正则解析        │                    │
│  └──────────────────┘  └──────────────────┘                    │
│                                                                 │
│  FastAPI 0.104+ + OpenAI SDK (GLM API 兼容)                   │
│  PyMuPDF 1.23.0 + MinerU 0.5.0 (PDF 处理)                     │
└─────────────────────────────────────────────────────────────────┘
```

#### 架构优势

| 优势 | 说明 |
|------|------|
| **职责分离** | Java 专注业务逻辑和数据持久化，Python 专注 AI 服务 |
| **技术选型最优** | Spring Boot 生态成熟，Python AI 库丰富 |
| **自动降级** | Python 服务失败时，Java 后端可降级处理 |
| **独立部署** | 前端、Java 后端、Python 后端可独立开发和部署 |
| **易于扩展** | AI 服务升级不影响核心业务逻辑 |

### 2.2 技术栈清单

#### 前端技术栈 (vue-front)

| 技术 | 版本 | 用途 | 说明 |
|------|------|------|------|
| **Vue.js** | 3.5.26 | 渐进式框架 | 核心前端框架 |
| **Vite** | 7.3.0 | 构建工具 | 极速开发服务器 |
| **Pinia** | 3.0.4 | 状态管理 | 官方推荐状态管理库 |
| **Vue Router** | 4.6.4 | 路由管理 | 单页应用路由 |
| **Element Plus** | 2.13.0 | UI 组件库 | 基于 Vue 3 的组件库 |
| **KaTeX** | 0.16.27 | 数学公式渲染 | 快速渲染 LaTeX 公式 |
| **ECharts** | 6.0.0 | 数据可视化 | 图表库（雷达图、热力图） |
| **Axios** | 1.13.2 | HTTP 客户端 | API 请求封装 |
| **Sortable.js** | 1.15.6 | 拖拽排序 | 科目树拖拽排序 |
| **XLSX** | 0.18.5 | Excel 处理 | Excel 导入导出 |

**开发工具**:
- `@vitejs/plugin-vue`: 6.0.3 (Vue 3 插件)
- `vite-plugin-vue-devtools`: 8.0.5 (开发者工具)
- `sass-embedded`: 1.97.2 (CSS 预处理器)

#### Java 后端技术栈 (java-back)

| 技术 | 版本 | 用途 | 说明 |
|------|------|------|------|
| **Spring Boot** | 3.3.5 | 核心框架 | 应用基础框架 |
| **Spring MVC** | - | Web 框架 | RESTful API |
| **Spring Security** | - | 安全框架 | 认证授权 |
| **MyBatis Plus** | 3.5.5 | ORM 框架 | 数据访问层增强 |
| **MySQL** | 8.0+ | 关系数据库 | 主数据存储 |
| **H2 Database** | - | 内存数据库 | 测试/开发环境 |
| **Knife4j** | 4.5.0 | API 文档 | Swagger 增强 |
| **Hutool** | 5.8.25 | 工具库 | 简化开发 |
| **Lombok** | - | 简化代码 | 自动生成 getter/setter |
| **Flying Saucer** | 9.1.22 | PDF 生成 | XHTML 转 PDF |
| **Thymeleaf** | - | 模板引擎 | PDF 模板渲染 |
| **GLM SDK** | release-V4-2.0.2 | AI SDK | 智谱 AI 官方 SDK |

**Java 版本**: 17

#### Python 后端技术栈 (python-back)

| 技术 | 版本 | 用途 | 说明 |
|------|------|------|------|
| **FastAPI** | 0.104+ | Web 框架 | 高性能异步框架 |
| **Uvicorn** | 0.24.0+ | ASGI 服务器 | 运行 FastAPI |
| **OpenAI SDK** | 1.0.0+ | AI SDK | 兼容 GLM API |
| **Pydantic** | 2.0.0+ | 数据验证 | 请求/响应验证 |
| **Loguru** | 0.7.0+ | 日志库 | 简化日志记录 |
| **PyMuPDF** | 1.23.0+ | PDF 读取 | PDF 文件解析 |
| **MinerU** | 0.5.0+ | PDF 转 Markdown | 智能 PDF 提取 |

**Python 版本**: 3.10+

### 2.3 服务端口说明

| 服务 | 端口 | 协议 | 访问地址 | 说明 |
|------|------|------|---------|------|
| **前端应用** | 5173 | HTTP | http://localhost:5173 | Vue 3 开发服务器 |
| **Java 后端** | 8081 | HTTP | http://localhost:8081 | Spring Boot 应用 |
| **Java API 文档** | 8081 | HTTP | http://localhost:8081/doc.html | Knife4j Swagger UI |
| **Python 后端** | 8082 | HTTP | http://localhost:8082 | FastAPI 应用 |
| **Python API 文档** | 8082 | HTTP | http://localhost:8082/docs | FastAPI Swagger UI |
| **MySQL 数据库** | 3306 | TCP | localhost:3306 | 数据库服务 |

#### 服务依赖关系

```
前端 (5173)
   ↓ 依赖
Java 后端 (8081)
   ↓ 依赖
MySQL (3306)
   ↓ 可选依赖
Python 后端 (8082)
```

**说明**:
- 前端必须连接 Java 后端
- Java 后端必须连接 MySQL
- Java 后端可选连接 Python 后端（AI 功能）
- Python 后端是独立服务，不依赖其他服务

### 2.4 数据流向图

#### 题目管理流程

```
用户上传题目图片 (前端)
       ↓
   POST /api/common/recognize-image (Java)
       ↓
   调用 Python 后端 /ai/recognize (可选)
       ↓
   GLM-4.6V-Flash 识别题目
       ↓
   返回结构化 JSON (LaTeX 公式)
       ↓
   Java 保存到数据库 (question.content_json)
       ↓
   前端展示题目 (KaTeX 渲染公式)
```

#### 考试会话流程

```
用户开始考试 (前端)
       ↓
   POST /api/exam-session/start (Java)
       ↓
   创建考试会话 (exam_session 表)
       ↓
   前端倒计时 + 答题快照 (LocalStorage)
       ↓
   用户提交答案
       ↓
   POST /api/exam-session/submit (Java)
       ↓
   客观题自动判分
       ↓
   主观题调用 Python 后端 /ai/grade (可选)
       ↓
   GLM-4.7 智能批改
       ↓
   保存考试记录 (exam_record 表)
       ↓
   返回成绩和 AI 反馈 (前端展示)
```

#### 题目导出流程

```
管理员选择题目导出 (前端)
       ↓
   POST /api/question/export-pdf (Java)
       ↓
   从数据库读取题目 (question.content_json)
       ↓
   Thymeleaf 渲染 XHTML 模板
       ↓
   Flying Saucer 转 PDF (支持 LaTeX 公式)
       ↓
   返回 PDF 文件流 (前端下载)
```

### 2.5 技术选型理由

#### 为什么选择 Java + Python 双后端？

| 方案 | 优势 | 劣势 |
|------|------|------|
| **纯 Java** | 架构简单，部署方便 | AI 生态不如 Python，集成成本高 |
| **纯 Python** | AI 生态丰富，开发快速 | 企业级框架不如 Spring Boot 成熟 |
| **Java + Python** | 各司其职，技术选型最优 | 架构复杂，需要服务间通信 |

**最终选择**: Java + Python 双后端
- **Java**: 处理业务逻辑、数据持久化、用户认证（Spring Boot 生态成熟）
- **Python**: 处理 AI 服务、图片识别、PDF 处理（AI 库丰富）

#### 为什么使用 MySQL JSON 字段？

**传统方案**: 多表关联（question_content, question_option, question_answer）
- ❌ 表结构复杂，JOIN 查询多
- ❌ 字段冗余，存储浪费
- ❌ 扩展性差

**JSON 字段方案**: 单表存储（question.content_json）
- ✅ 减少表数量，简化查询
- ✅ 不同题型可有差异化结构
- ✅ MyBatis Plus 自动序列化/反序列化

#### 为什么选择 GLM 模型？

| 模型 | 用途 | 优势 |
|------|------|------|
| **GLM-4.6V** | 图片识别 | 高性价比多模态模型 |
| **GLM-4.7** | 智能批改 | 按步骤给分，提供详细反馈 |

**为什么选择 GLM**?
- ✅ 模型性价比高，价格低廉
- ✅ 国内访问稳定
- ✅ 支持 LaTeX 公式识别和输出

---

## 3. 前端技术栈详解

### 3.1 核心框架与工具

#### Vue 3 + Vite 项目配置

**Vite 配置文件**: [vue-front/vite.config.js](vue-front/vite.config.js)

```javascript
export default defineConfig({
  plugins: [
    vue(),                      // Vue 3 单文件组件支持
    vueDevTools(),              // Vue 3 开发者工具
    svgLoader()                 // SVG 文件加载器
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))  // 路径别名
    }
  },
  server: {
    port: 5173,                 // 开发服务器端口
    host: true                  // 监听所有地址
  }
})
```

**关键配置说明**:
- **路径别名**: `@` 指向 `src` 目录，简化导入路径
- **开发服务器**: 端口 5173，host 允许局域网访问
- **插件**: Vue DevTools 用于调试，svgLoader 用于 SVG 图标

#### 应用入口文件

**主入口**: [vue-front/src/main.js](vue-front/src/main.js)

```javascript
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import 'katex/dist/katex.min.css'  // KaTeX 样式

const app = createApp(App)

app.use(createPinia())      // 状态管理
app.use(router)             // 路由管理
app.use(ElementPlus)        // UI 组件库

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')
```

**初始化顺序**:
1. 创建 Vue 应用实例
2. 注册 Pinia（状态管理）
3. 注册 Vue Router（路由）
4. 注册 Element Plus（UI 组件）
5. 注册 Element Plus 图标
6. 挂载到 `#app` DOM 元素

### 3.2 Vue Router 路由管理

#### 路由配置

**路由文件**: [vue-front/src/router/index.js](vue-front/src/router/index.js)

**路由结构**:

```
/ (根路径)
├── /login                      # 登录页
│
├── /user                       # 用户端 (需要认证)
│   ├── /user/home              # 首页
│   ├── /user/dashboard         # 备考面板
│   ├── /user/correction-notebook # 错题本
│   ├── /user/profile           # 个人资料
│   ├── /user/subject           # 题库页面
│   ├── /user/single-practice/:subjectId  # 逐题精练
│   ├── /user/topic-drill       # 专项突破
│   ├── /user/paper-list        # 套卷列表
│   └── /user/mock-exam         # 沉浸式模考
│
└── /admin                      # 管理端 (需要 admin 权限)
    ├── /admin/home             # 管理首页
    ├── /admin/users-manage     # 用户管理
    ├── /admin/mistake-monitor  # 错题监控
    ├── /admin/subjects-manage  # 科目管理
    ├── /admin/books-manage     # 习题册管理
    ├── /admin/questions-manage # 题目管理
    ├── /admin/papers-manage    # 试卷管理
    ├── /admin/exam-record-manage # 考试记录管理
    ├── /admin/user-progress-monitor # 学习进度监控
    ├── /admin/question-import  # 题目导入
    └── /admin/question-export  # 题目导出
```

#### 路由守卫实现

**核心逻辑**:

```javascript
router.beforeEach((to, from, next) => {
  const role = localStorage.getItem('role')
  const userStr = localStorage.getItem('user')
  let userRole = null
  let isLogin = false

  // 解析用户信息
  if (role && userStr) {
    const userObj = JSON.parse(userStr)
    if (userObj.role === role) {
      userRole = role
      isLogin = true
    }
  }

  // 路由逻辑判断
  if (to.path === '/login') {
    // 已登录访问登录页 → 跳转到对应首页
    isLogin ? next(userRole === 'admin' ? '/admin/home' : '/user/dashboard') : next()
  } else if (to.path.startsWith('/admin')) {
    // 管理 → 检查权限
    !isLogin ? next('/login')
      : userRole !== 'admin' ? next('/user/dashboard')
      : next()
  } else {
    // 用户端 → 检查登录
    !isLogin ? next('/login') : next()
  }
})
```

**权限控制逻辑**:

| 访问路径 | 未登录 | 已登录 (student) | 已登录 (admin) |
|---------|-------|-----------------|----------------|
| `/login` | ✅ 允许 | ➡️ 重定向到首页 | ➡️ 重定向到管理首页 |
| `/user/*` | ➡️ 重定向到登录 | ✅ 允许 | ✅ 允许 |
| `/admin/*` | ➡️ 重定向到登录 | ➡️ 权限不足，重定向到用户端 | ✅ 允许 |

### 3.3 Pinia 状态管理

#### Store 模块设计

**核心 Store 文件**:
- [stores/user.js](vue-front/src/stores/user.js) - 用户状态管理
- [stores/exam.js](vue-front/src/stores/exam.js) - 考试会话状态管理

#### 考试 Store 架构

**文件**: [vue-front/src/stores/exam.js](vue-front/src/stores/exam.js)

**状态结构**:

```javascript
{
  // 考试基本信息
  paperId: null,
  sessionId: null,
  userId: '',

  // 考试信息
  examInfo: {
    title: '',              // 试卷标题
    totalTime: 180 * 60,    // 总时间（秒）
    remainingTime: 180 * 60, // 剩余时间
    startTime: null,        // 开始时间
    paperType: null,        // 试卷类型
    totalScore: null,       // 总分
    submitTime: null,       // 提交时间
    aiSummary: null,        // AI 总结
    examDetails: null       // 答题明细
  },

  // 题目与答案
  questions: [],            // 题目列表
  userAnswers: {},          // 用户答案 { questionId: answer }
  markedQuestions: {},      // 标记的题目

  // 考试状态
  examStatus: 'pending',    // pending, in-progress, grading, finished
  switchCount: 0,           // 切屏次数
  loading: false,           // 加载状态
  error: null               // 错误信息
}
```

**核心 Actions**:

| Action | 功能 | 说明 |
|--------|------|------|
| `loadPaperInfo()` | 加载试卷信息 | 获取试卷详情、时间限制 |
| `initExam()` | 初始化考试 | 调用后端 API 创建会话 |
| `setAnswer()` | 设置答案 | 保存用户答案到 store + localStorage |
| `toggleMark()` | 切换标记 | 标记/取消标记题目 |
| `tickTimer()` | 计时器 | 每秒递减剩余时间 |
| `incrementSwitchCount()` | 切屏计数 | 记录切屏次数 |
| `submitExam()` | 提交考试 | 保存快照并提交到后端 |
| `resetExam()` | 重置状态 | 清空所有考试数据 |

**持久化策略**:

```javascript
// 保存到 localStorage
function saveToLocal() {
  localStorage.setItem('currentExamState', JSON.stringify({
    sessionId,
    paperId,
    userId,
    userAnswers,
    markedQuestions,
    remainingTime,
    switchCount,
    examStatus
  }))
}

// 从 localStorage 恢复
function restoreFromLocal() {
  const savedState = localStorage.getItem('currentExamState')
  if (savedState && JSON.parse(savedState).sessionId === sessionId.value) {
    // 只恢复同一会话的数据
    userAnswers.value = parsed.userAnswers
    markedQuestions.value = parsed.markedQuestions
    examInfo.value.remainingTime = parsed.remainingTime
  }
}
```

### 3.4 Element Plus UI 组件库

#### 核心组件使用

**布局组件**:
- `<el-container>`: 页面容器
- `<el-header>`: 顶部导航
- `<el-aside>`: 侧边栏
- `<el-main>`: 主内容区

**表单组件**:
- `<el-form>` + `<el-form-item>`: 表单容器
- `<el-input>`: 文本输入
- `<el-select>` + `<el-option>`: 下拉选择
- `<el-radio>` + `<el-radio-group>`: 单选框
- `<el-checkbox>` + `<el-checkbox-group>`: 复选框
- `<el-upload>`: 文件上传

**数据展示**:
- `<el-table>`: 表格（题目列表、用户列表）
- `<el-tree>`: 树形结构（科目树）
- `<el-tag>`: 标签
- `<el-progress>`: 进度条

**反馈组件**:
- `<el-message>`: 消息提示
- `<el-message-box>`: 弹窗确认
- `<el-dialog>`: 对话框
- `<el-loading>`: 加载状态

#### 图标注册

**全局注册** (在 main.js):

```javascript
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
```

**使用方式**:

```vue
<template>
  <el-icon :size="20">
    <Edit />
  </el-icon>
</template>
```

### 3.5 KaTeX 数学公式渲染

#### KaTeX 集成

**依赖安装**:

```bash
npm install katex
```

**样式引入**: [vue-front/src/main.js](vue-front/src/main.js#L15)

```javascript
import 'katex/dist/katex.min.css'
```

#### 公式渲染组件

**核心实现**:

```vue
<template>
  <div ref="mathContent" v-html="renderedContent"></div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import katex from 'katex'

const props = defineProps({
  content: String,    // LaTeX 公式字符串
  displayMode: {      // 是否为块级公式
    type: Boolean,
    default: false
  }
})

const renderedContent = ref('')

function renderMath() {
  katex.render(props.content, renderedContent.value, {
    throwOnError: false,
    displayMode: props.displayMode
  })
}

onMounted(renderMath)
watch(() => props.content, renderMath)
</script>
```

**LaTeX 公式格式规范**:

| 格式 | 标记 | 示例 | 渲染结果 |
|------|------|------|---------|
| 行内公式 | `$...$` | `$f(x) = x^2$` | $f(x) = x^2$ |
| 块级公式 | `$$...$$` | `$$\int_0^1 x^2 dx$$` | $$\int_0^1 x^2 dx$$ |

**常见数学符号**:

```latex
分数: \frac{a}{b}
根号: \sqrt{x}
积分: \int_{a}^{b}
求和: \sum_{i=1}^{n}
极限: \lim_{x \to 0}
上下标: x^2, x_1
希腊字母: \alpha, \beta, \gamma, \theta
```

### 3.6 ECharts 数据可视化

#### 核心图表应用

**雷达图** (备考面板 - Dashboard.vue):

```javascript
import * as echarts from 'echarts'

const radarChart = echarts.init(chartRef.value)

const option = {
  radar: {
    indicator: [
      { name: '高等数学', max: 100 },
      { name: '线性代数', max: 100 },
      { name: '概率统计', max: 100 },
      { name: '英语', max: 100 },
      { name: '政治', max: 100 }
    ]
  },
  series: [{
    type: 'radar',
    data: [{
      value: [85, 72, 68, 90, 78],
      name: '我的能力'
    }]
  }]
}

radarChart.setOption(option)
```

**热力图** (错题监控 - MistakeMonitor.vue):

```javascript
const option = {
  tooltip: {
    position: 'top'
  },
  grid: {
    height: '50%',
    top: '10%'
  },
  xAxis: {
    type: 'category',
    data: ['高数', '线代', '概率', '英语', '政治'],
    splitArea: { show: true }
  },
  yAxis: {
    type: 'category',
    data: ['函数', '极限', '导数', '积分'],
    splitArea: { show: true }
  },
  visualMap: {
    min: 0,
    max: 50,
    calculable: true,
    orient: 'horizontal',
    left: 'center',
    bottom: '15%'
  },
  series: [{
    name: '错题次数',
    type: 'heatmap',
    data: heatmapData,  // [[x, y, value], ...]
    label: {
      show: true
    },
    emphasis: {
      itemStyle: {
        shadowBlur: 10,
        shadowColor: 'rgba(0, 0, 0, 0.5)'
      }
    }
  }]
}
```

### 3.7 Axios API 封装

#### API 模块结构

**目录**: [vue-front/src/api/](vue-front/src/api/)

```
api/
├── common.js                    # 公共 API
├── examSession.js               # 考试会话 API
├── paper.js                     # 试卷 API
├── questionImportExport.js      # 题目导入导出 API
├── subject.js                   # 科目 API
├── user.js                      # 用户 API
└── userProgress.js              # 用户进度 API
```

#### 请求配置

**Axios 实例配置**:

```javascript
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: 'http://localhost:8081/api',  // API 基础路径
  timeout: 30000,                        // 超时时间
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 添加 Token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  error => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
```

#### API 使用示例

**考试会话 API** ([api/examSession.js](vue-front/src/api/examSession.js)):

```javascript
import request from './request'

// 开始考试
export function startExam(userId, paperId) {
  return request({
    url: '/exam-session/start',
    method: 'post',
    data: { userId, paperId }
  })
}

// 提交答案
export function submitAnswer(sessionId, questionId, userAnswer) {
  return request({
    url: '/exam-session/submit',
    method: 'post',
    data: { sessionId, questionId, userAnswer }
  })
}

// 保存快照
export function saveSnapshot(sessionId, snapshotJson) {
  return request({
    url: '/exam-session/snapshot',
    method: 'post',
    data: { sessionId, snapshotJson }
  })
}

// 提交考试
export function submitExam(sessionId) {
  return request({
    url: '/exam-session/submit',
    method: 'post',
    params: { sessionId }
  })
}
```

### 3.8 前端目录结构详解

```
vue-front/src/
├── api/                          # API 接口封装
│   ├── common.js                 # 公共接口（登录、图片识别）
│   ├── examSession.js            # 考试会话接口
│   ├── paper.js                  # 试卷接口
│   ├── questionImportExport.js   # 题目导入导出接口
│   ├── subject.js                # 科目接口
│   ├── user.js                   # 用户接口
│   └── userProgress.js           # 用户进度接口
│
├── assets/                       # 静态资源
│   └── logo.png
│
├── components/                   # 公共组件
│   └── MathRenderer.vue          # LaTeX 公式渲染组件
│
├── layout/                       # 布局组件
│   ├── AdminLayout.vue           # 管理端布局
│   │   ├── 顶部导航栏
│   │   ├── 侧边菜单
│   │   └── 主内容区
│   └── UserLayout.vue            # 用户端布局
│       ├── 顶部导航栏
│       └── 主内容区
│
├── router/                       # 路由配置
│   └── index.js                  # 路由定义与守卫
│
├── stores/                       # Pinia 状态管理
│   ├── counter.js                # 计数器示例
│   ├── exam.js                   # 考试状态管理
│   ├── transition.js             # 过渡动画
│   └── user.js                   # 用户状态管理
│
├── utils/                        # 工具函数
│   └── katex-render.js           # KaTeX 渲染工具
│
├── views/                        # 页面视图
│   ├── Login.vue                 # 登录页
│   ├── Home.vue                  # 首页
│   ├── Dashboard.vue             # 备考面板（雷达图）
│   ├── SubjectList.vue           # 科目列表
│   ├── CorrectionNotebook.vue    # 错题本
│   ├── PaperList.vue             # 试卷列表
│   ├── UserProfile.vue           # 个人资料
│   │
│   ├── quiz/                     # 刷题模块
│   │   ├── SinglePractice.vue    # 逐题精练
│   │   ├── TopicDrill.vue        # 专项突破
│   │   └── MockExam.vue          # 沉浸式模考
│   │
│   └── admin/                    # 管理端页面
│       ├── AdminHome.vue         # 管理首页（数据看板）
│       ├── UserManage.vue        # 用户管理
│       ├── SubjectManage.vue     # 科目管理（树形结构）
│       ├── QuestionManage.vue    # 题目管理
│       ├── QuestionImport.vue    # 题目导入（JSON/PDF）
│       ├── QuestionExport.vue    # 题目导出（PDF）
│       ├── BookManage.vue        # 习题册管理
│       ├── PaperManage.vue       # 试卷管理
│       ├── ExamRecordManage.vue  # 考试记录管理
│       ├── MistakeMonitor.vue    # 错题监控（热力图）
│       └── UserProgressMonitor.vue # 学习进度监控
│
├── App.vue                       # 根组件
└── main.js                       # 应用入口
```

### 3.9 核心页面实现

#### 考试会话页面 (MockExam.vue)

**核心功能**:
1. **倒计时持久化**: localStorage + 后端时间戳
2. **答题快照**: 每 5 秒自动保存
3. **切屏检测**: Visibility API 监听
4. **浏览器返回阻止**: history.pushState

**关键代码片段**:

```javascript
import { useExamStore } from '@/stores/exam'

const examStore = useExamStore()

// 倒计时
const timer = setInterval(() => {
  examStore.tickTimer()
}, 1000)

// 切屏检测
document.addEventListener('visibilitychange', () => {
  if (document.hidden) {
    examStore.incrementSwitchCount()
  }
})

// 浏览器返回阻止
window.history.pushState(null, null, location.href)
window.addEventListener('popstate', () => {
  window.history.pushState(null, null, location.href)
  ElMessage.warning('考试进行中，请勿退出')
})

// 组件卸载前确认
onBeforeUnmount(() => {
  clearInterval(timer)
})
```

#### 科目管理页面 (SubjectManage.vue)

**核心功能**:
1. **树形结构展示**: Element Plus Tree 组件
2. **拖拽排序**: Sortable.js 集成
3. **Scope 配置**: 多选下拉框

**拖拽实现**:

```javascript
import Sortable from 'sortablejs'

const treeRef = ref(null)

onMounted(() => {
  const el = treeRef.value.$el.querySelector('.el-tree-node__content')
  Sortable.create(el, {
    animation: 150,
    onEnd({ oldIndex, newIndex }) {
      // 更新排序
      updateNodeOrder(oldIndex, newIndex)
    }
  })
})
```

### 3.10 前端开发最佳实践

#### 组件通信

**父传子** (Props):

```vue
<script setup>
defineProps({
  questionId: Number,
  content: String
})
</script>
```

**子传父** (Emits):

```vue
<script setup>
const emit = defineEmits(['answer-change'])

function submitAnswer(answer) {
  emit('answer-change', answer)
}
</script>
```

**跨组件通信** (Pinia):

```javascript
import { useExamStore } from '@/stores/exam'

const examStore = useExamStore()
examStore.setAnswer(questionId, answer)
```

#### 响应式数据

**ref vs reactive**:

```javascript
// ref - 基本类型
const count = ref(0)
count.value++  // 需要 .value

// reactive - 对象类型
const state = reactive({
  count: 0,
  name: ''
})
state.count++  // 直接访问
```

#### 生命周期

```vue
<script setup>
import { onMounted, onBeforeUnmount, watch } from 'vue'

onMounted(() => {
  // 组件挂载后
})

onBeforeUnmount(() => {
  // 组件卸载前（清理定时器、事件监听）
})

watch(() => props.questionId, (newId) => {
  // 监听 props 变化
})
```

#### 性能优化

**v-memo** (减少重复渲染):

```vue
<div v-memo="[question.id]">
  <!-- 仅当 question.id 变化时重新渲染 -->
</div>
```

**computed vs methods**:

```javascript
// computed - 有缓存，适合计算属性
const fullName = computed(() => {
  return firstName.value + ' ' + lastName.value
})

// methods - 无缓存，适合事件处理
function handleClick() {
  // 处理点击事件
}
```

---

## 4. Java 后端技术栈详解

> Java 后端基于 **Spring Boot 3.3.5**，负责核心业务逻辑处理、数据持久化和 RESTful API 提供。

### 4.1 核心框架与依赖

#### 技术栈清单

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.3.5 | 核心框架 |
| Spring Security | 6.x | 安全认证 |
| MyBatis Plus | 3.5.5 | ORM 框架 |
| MySQL Connector | 8.0.x | 数据库驱动 |
| Knife4j | 4.5.0 | API 文档（Swagger） |
| Flying Saucer | 9.1.22 | PDF 生成 |
| Thymeleaf | 3.x | PDF 模板引擎 |
| Lombok | 1.18.x | 简化 Java 代码 |
| Jackson | 2.x | JSON 处理 |
| GLM SDK | release-V4-2.0.2 | AI 集成 |

#### Maven 依赖配置 (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot 核心 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- 安全认证 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- 数据库 -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- MyBatis Plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
        <version>3.5.5</version>
    </dependency>

    <!-- API 文档 -->
    <dependency>
        <groupId>com.github.xiaoymin</groupId>
        <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
        <version>4.5.0</version>
    </dependency>

    <!-- PDF 生成 -->
    <dependency>
        <groupId>org.xhtmlrenderer</groupId>
        <artifactId>flying-saucer-pdf</artifactId>
        <version>9.1.22</version>
    </dependency>
    <dependency>
        <groupId>org.thymeleaf</groupId>
        <artifactId>thymeleaf</artifactId>
    </dependency>

    <!-- 工具类 -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

### 4.2 分层架构设计

Java 后端采用经典的三层架构，每层职责清晰：

```
┌─────────────────────────────────────────────┐
│           Controller 层（控制器）             │
│  - 接收 HTTP 请求                            │
│  - 参数校验与转换                             │
│  - 返回统一响应格式                           │
│  - 文档注解（@Operation）                     │
└─────────────────┬───────────────────────────┘
                  │ 调用
                  ↓
┌─────────────────────────────────────────────┐
│            Service 层（业务逻辑）              │
│  - 核心业务逻辑处理                           │
│  - 事务管理（@Transactional）                │
│  - 调用 Mapper 和 Python 服务                 │
│  - 数据组装与转换                             │
└─────────────────┬───────────────────────────┘
                  │ 调用
                  ↓
┌─────────────────────────────────────────────┐
│            Mapper 层（数据访问）               │
│  - 基于 MyBatis Plus                         │
│  - CRUD 基础操作                              │
│  - 自定义 SQL（XML 映射）                     │
└─────────────────┬───────────────────────────┘
                  │ 操作
                  ↓
┌─────────────────────────────────────────────┐
│           MySQL 数据库                        │
└─────────────────────────────────────────────┘
```

#### 实际代码示例：QuestionController

```java
// Controller 层：处理 HTTP 请求
@Tag(name = "题目管理", description = "题目增删改查接口")
@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private PythonBackendClient pythonBackendClient;

    @PostMapping("/recognize")
    @Operation(summary = "图片识别题目")
    public Result<QuestionDTO> recognizeQuestion(@RequestParam("file") MultipartFile file) {
        // 1. 调用 Python 服务进行 AI 识别
        QuestionDTO recognized = pythonBackendClient.recognizeQuestion(file);

        // 2. 返回统一格式响应
        return Result.success(recognized);
    }

    @GetMapping("/list-by-subject")
    @Operation(summary = "按科目获取题目")
    public Result<List<Question>> getQuestionsBySubject(
            @RequestParam(required = false) Integer subjectId) {
        // 调用 Service 层
        List<Question> questions = questionService.getQuestionsBySubject(subjectId);
        return Result.success(questions);
    }
}
```

```java
// Service 层：业务逻辑
@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public List<Question> getQuestionsBySubject(Integer subjectId) {
        // 业务逻辑：查询题目并处理关联数据
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        // 添加查询条件...
        return questionMapper.selectList(wrapper);
    }
}
```

```java
// Mapper 层：数据访问
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
    // MyBatis Plus 自动提供 CRUD 方法
    // List<Question> selectList(Wrapper<Question> wrapper);
}
```

### 4.3 统一响应格式

所有 API 接口返回统一的 `Result<T>` 格式：

```java
@Data
public class Result<T> {
    private Integer code;  // 200 成功，500 失败
    private String msg;    // 提示信息
    private T data;        // 具体数据

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }
}
```

**响应示例**：

```json
// 成功响应
{
  "code": 200,
  "msg": "成功",
  "data": {
    "id": 1,
    "type": 1,
    "content": "题目内容..."
  }
}

// 错误响应
{
  "code": 500,
  "msg": "题目不存在",
  "data": null
}
```

### 4.4 JSON 字段处理

Question 实体使用 MyBatis Plus 的 `JacksonTypeHandler` 处理 JSON 字段：

```java
@Data
@TableName(value = "question", autoResultMap = true)
public class Question {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer type;
    private Integer difficulty;

    // JSON 字段：使用 Jackson 自动序列化/反序列化
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> contentJson;

    // 便捷访问方法
    public String getContent() {
        return contentJson != null ? (String) contentJson.get("content") : null;
    }

    public void setContent(String content) {
        if (contentJson == null) {
            contentJson = new HashMap<>();
        }
        contentJson.put("content", content);
    }

    public List<Map<String, String>> getOptions() {
        if (contentJson == null) return null;
        Object options = contentJson.get("options");
        return options != null ? (List<Map<String, String>>) options : null;
    }
}
```

### 4.5 Python 服务集成

通过 `PythonBackendClient` 调用 Python 后端的 AI 服务：

```java
@Component
public class PythonBackendClient {

    @Value("${python.backend.url:http://localhost:8082}")
    private String pythonBackendUrl;

    private final RestTemplate restTemplate;

    // 图片识别
    public QuestionDTO recognizeQuestion(MultipartFile file) {
        // 1. 创建临时文件
        File tempFile = File.createTempFile("upload_", "_" + file.getOriginalFilename());
        file.transferTo(tempFile);

        // 2. 构造 multipart 请求
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(tempFile));

        // 3. 发送请求到 Python 服务
        ResponseEntity<String> response = restTemplate.exchange(
                pythonBackendUrl + "/ai/recognize",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // 4. 解析响应并返回
        return convertToQuestionDTO(data);
    }

    // 智能批改
    public Map<String, Object> gradeAnswer(...) {
        // 类似实现...
    }
}
```

### 4.6 安全配置

Spring Security 配置 ([SecurityConfig.java](java-back/src/main/java/org/example/kaoyanplatform/config/SecurityConfig.java))：

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configure(http))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/user/login", "/user/register", "/uploads/**").permitAll()
                .requestMatchers("/doc.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().permitAll()  // 开发阶段允许所有访问
            );
        return http.build();
    }
}
```

### 4.7 配置文件

[application.yml](java-back/src/main/resources/application.yml):

```yaml
server:
  port: 8081

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    jdbc-url: jdbc:mysql://localhost:3306/kaoyan_platform?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8
    username: root
    password: 123456

mybatis-plus:
  configuration:
    map-underline-to-camel-case: true
  global-config:
    db-config:
      id-type: auto
  mapper-locations: classpath*:/mapper/**/*.xml

python:
  backend:
    url: http://localhost:8082
    timeout: 30000
    enabled: true
```

### 4.8 目录结构

```
java-back/
├── src/main/java/org/example/kaoyanplatform/
│   ├── KaoYanPlatformApplication.java  # 启动类
│   ├── client/                         # 外部服务客户端
│   │   └── PythonBackendClient.java    # Python 服务调用
│   ├── common/                         # 通用类
│   │   └── Result.java                 # 统一响应格式
│   ├── config/                         # 配置类
│   │   ├── SecurityConfig.java         # 安全配置
│   │   ├── RestTemplateConfig.java     # HTTP 客户端配置
│   │   ├── Knife4jConfig.java          # API 文档配置
│   │   ├── CorsConfig.java             # 跨域配置
│   │   └── AsyncConfig.java            # 异步任务配置
│   ├── controller/                     # 控制器层
│   │   ├── QuestionController.java     # 题目管理
│   │   ├── UserController.java         # 用户管理
│   │   ├── SubjectController.java      # 科目管理
│   │   └── ...
│   ├── entity/                         # 实体类
│   │   ├── Question.java               # 题目实体
│   │   ├── Subject.java                # 科目实体
│   │   └── dto/                        # 数据传输对象
│   ├── service/                        # 业务逻辑层
│   │   ├── QuestionService.java        # 题目服务接口
│   │   └── impl/
│   │       └── QuestionServiceImpl.java
│   ├── mapper/                         # 数据访问层
│   │   ├── QuestionMapper.java
│   │   └── SubjectMapper.java
│   ├── enums/                          # 枚举类
│   ├── handler/                        # 处理器
│   │   └── GlobalExceptionHandler.java  # 全局异常处理
│   └── util/                           # 工具类
│       └── MathAnswerMatcher.java      # 数学答案匹配
└── src/main/resources/
    ├── application.yml                 # 配置文件
    └── mapper/                         # MyBatis XML 映射
```

---

## 5. Python 后端技术栈详解

> Python 后端基于 **FastAPI** 框架，专注于提供 AI 增强功能和数据处理服务。

### 5.1 核心框架与依赖

#### 技术栈清单

| 技术 | 版本 | 用途 |
|------|------|------|
| FastAPI | 0.104+ | Web 框架 |
| Uvicorn | 最新 | ASGI 服务器 |
| OpenAI SDK | 1.0+ | GLM API 调用 |
| Pydantic | 2.0+ | 数据校验 |
| PyMuPDF | 1.23+ | PDF 处理 |
| MinerU | 0.5+ | PDF 转 Markdown |
| Loguru | 最新 | 日志管理 |

#### 依赖配置 (requirements.txt)

```txt
# Web 框架
fastapi>=0.104.0
uvicorn[standard]>=0.24.0

# AI 服务
openai>=1.0.0
pydantic>=2.0.0

# PDF 处理
PyMuPDF>=1.23.0
mineru>=0.5.0

# 日志
loguru>=0.7.0

# 配置管理
pydantic-settings>=2.0.0
```

### 5.2 应用配置管理

使用 `pydantic-settings` 进行配置管理 ([config.py](python-back/app/config.py))：

```python
from pathlib import Path
from pydantic_settings import BaseSettings
from typing import List

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

    @property
    def cors_origins(self) -> List[str]:
        """获取 CORS 允许的源列表"""
        return [origin.strip() for origin in self.ALLOWED_ORIGINS.split(",")]

    class Config:
        env_file = BASE_DIR / ".env"
        env_file_encoding = "utf-8"
        case_sensitive = True

settings = Settings()
```

**环境变量配置 (.env)**：

```env
# GLM API 配置
GLM_API_KEY=your_api_key_here

# 服务端口
APP_PORT=8082

# CORS 允许的源
ALLOWED_ORIGINS=http://localhost:5173,http://localhost:8081
```

### 5.3 FastAPI 应用初始化

[main.py](python-back/app/main.py)：

```python
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager
import logging

from app.routers import api_router
from app.config import settings

@asynccontextmanager
async def lifespan(app: FastAPI):
    """应用生命周期管理"""
    logging.info("🚀 AI 服务启动中...")
    yield
    logging.info("👋 AI 服务已停止")

# 创建 FastAPI 应用
app = FastAPI(
    title=settings.APP_NAME,
    version="1.0.0",
    lifespan=lifespan,
    docs_url="/docs",
    redoc_url="/redoc"
)

# 配置 CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.cors_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 包含 API 路由
app.include_router(api_router)

@app.get("/health")
async def health_check():
    return {"status": "healthy", "service": "python-backend"}
```

### 5.4 AI 服务实现

#### 图片识别服务

[app/services/image_recognition_service.py](python-back/app/services/image_recognition_service.py)：

```python
from openai import OpenAI
from app.config import settings

class ImageRecognitionService:
    """图片识别服务 - 使用 GLM-4V-Flash"""

    def __init__(self):
        self.client = OpenAI(
            api_key=settings.GLM_API_KEY,
            base_url=settings.GLM_BASE_URL
        )
        self.model = "glm-4v-flash"  # 免费模型

    async def recognize_question(self, image_bytes: bytes) -> Dict[str, Any]:
        """识别图片中的题目"""

        # 构建 prompt
        prompt = """你是一个专业的数学题目识别助手。请识别图片中的题目内容，并以JSON格式返回。

【题目类型】1-单选, 2-多选, 3-填空, 4-简答

【LaTeX公式使用规则】
- 数学运算符号、关系符号、希腊字母、上下标、分数、根号等需要用 LaTeX
- 中文、英文、标点符号保持原样

【JSON格式要求】
{
  "type": 1,
  "content": "题目内容（用LaTeX包裹数学表达式）",
  "options": ["选项A", "选项B", "选项C", "选项D"],
  "answer": "答案",
  "analysis": "解析",
  "tags": ["标签1", "标签2"],
  "difficulty": 3
}"""

        # 调用 GLM API
        response = self.client.chat.completions.create(
            model=self.model,
            messages=[
                {
                    "role": "user",
                    "content": [
                        {"type": "text", "text": prompt},
                        {
                            "type": "image_url",
                            "image_url": {
                                "url": f"data:image/jpeg;base64,{base64.b64encode(image_bytes).decode()}"
                            }
                        }
                    ]
                }
            ],
            temperature=settings.GLM_TEMPERATURE,
            max_tokens=settings.GLM_MAX_TOKENS,
            timeout=settings.GLM_TIMEOUT
        )

        # 解析响应
        result_text = response.choices[0].message.content
        return self._parse_response(result_text)
```

#### 智能批改服务

[app/services/grading_service.py](python-back/app/services/grading_service.py)：

```python
class GradingService:
    """智能批改服务 - 使用 GLM-4-Plus"""

    def __init__(self):
        self.client = OpenAI(
            api_key=settings.GLM_API_KEY,
            base_url=settings.GLM_BASE_URL
        )
        self.model = "glm-4-plus"  # 更强大的模型

    async def grade_answer(
        self,
        question_content: str,
        user_answer: str,
        reference_answer: str,
        question_type: int
    ) -> Dict[str, Any]:
        """对主观题进行智能批改"""

        # 构建 prompt
        prompt = f"""你是一位资深的考研数学阅卷组长。

【题型】{type_name}

【题目内容】
{question_content}

【参考答案】
{reference_answer}

【用户答案】
{user_answer}

【评分要求】
1. 本题满分：100分
2. 请按步骤给分，关注解题思路的正确性和完整性
3. 即使最终答案错误，也要考虑过程分

【返回格式】
{{
  "score": 得分(0-100),
  "feedback": "详细的评分反馈",
  "strengths": ["优点1", "优点2"],
  "weaknesses": ["不足点1", "不足点2"]
}}"""

        # 调用 GLM API
        response = self.client.chat.completions.create(
            model=self.model,
            messages=[{"role": "user", "content": prompt}],
            temperature=0.1
        )

        # 解析响应
        result_text = response.choices[0].message.content
        return self._parse_response(result_text)
```

### 5.5 路由结构

[app/routers/ai_router.py](python-back/app/routers/ai_router.py)：

```python
from fastapi import APIRouter, UploadFile, File

ai_router = APIRouter(prefix="/ai", tags=["AI服务"])

@ai_router.post("/recognize")
async def recognize_question(file: UploadFile = File(...)):
    """图片识别题目接口"""
    # 验证文件类型和大小
    if not file.content_type.startswith("image/"):
        raise HTTPException(status_code=400, detail="只支持图片文件")

    image_bytes = await file.read()
    if len(image_bytes) > 10 * 1024 * 1024:
        raise HTTPException(status_code=400, detail="图片大小不能超过10MB")

    # 调用识别服务
    result = await image_service.recognize_question(image_bytes)
    return JSONResponse(content=result)

@ai_router.post("/grade")
async def grade_subjective_question(request: GradingRequest):
    """智能批改接口"""
    result = await grading_service.grade_answer(
        question_content=request.question_content,
        user_answer=request.user_answer,
        reference_answer=request.reference_answer,
        question_type=request.question_type
    )
    return JSONResponse(content=result)
```

### 5.6 目录结构

```
python-back/
├── app/
│   ├── __init__.py
│   ├── main.py                        # FastAPI 应用入口
│   ├── config.py                      # 配置管理
│   ├── routers/                       # 路由模块
│   │   ├── __init__.py
│   │   └── ai_router.py               # AI 服务路由
│   ├── services/                      # 业务逻辑层
│   │   ├── __init__.py
│   │   ├── image_recognition_service.py  # 图片识别服务
│   │   └── grading_service.py         # 智能批改服务
│   ├── models/                        # 数据模型
│   │   ├── ai_models.py               # AI 相关模型
│   │   └── tool_models.py             # 工具相关模型
│   └── tools/                         # 工具模块
│       ├── PDFExtract/                # PDF 提取工具
│       └── MDToJson/                  # Markdown 转 JSON
├── requirements.txt                   # 依赖配置
├── .env                               # 环境变量
└── run_server.py                      # 启动脚本
```

---

## 6. 数据库设计详解

> 数据库采用 **MySQL 8.0**，核心设计特点是使用 **JSON 字段** 存储题目内容，实现灵活的数据结构。
>
> **说明**: 共包含 **16 张表**，其中 `resource` 表存在于数据库但当前代码版本暂未使用。

### 6.1 用户与权限模块

#### user - 用户表

| 字段名 | 类型 | 长度 | 允许NULL | 默认值 | 键 | 说明 |
|--------|------|------|----------|--------|-----|------|
| id | bigint | - | NO | AUTO_INCREMENT | PK | 用户ID |
| username | varchar | 50 | NO | - | UK | 用户名/账号 |
| password | varchar | 100 | NO | - | - | 密码（BCrypt加密） |
| phone | varchar | 15 | YES | NULL | - | 手机号 |
| email | varchar | 255 | YES | NULL | - | 邮箱 |
| nickname | varchar | 10 | YES | NULL | - | 昵称 |
| role | varchar | 10 | YES | 'student' | - | 角色（student/admin） |
| avatar | varchar | 255 | YES | NULL | - | 头像URL |
| target_school | varchar | 10 | YES | NULL | - | 目标院校 |
| target_total_score | smallint | - | YES | 0 | - | 目标总分 |
| exam_year | varchar | 50 | YES | NULL | - | 考研年份 |
| exam_subjects | varchar | 255 | YES | NULL | - | 公共课 |
| create_time | datetime | - | YES | CURRENT_TIMESTAMP | - | 创建时间 |
| update_time | datetime | - | YES | CURRENT_TIMESTAMP ON UPDATE | - | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- UNIQUE KEY `username` (`username`)

---

### 6.2 科目与知识体系模块

#### subject - 科目表（树形结构）

| 字段名 | 类型 | 长度 | 允许NULL | 默认值 | 键 | 说明 |
|--------|------|------|----------|--------|-----|------|
| id | int | - | NO | AUTO_INCREMENT | PK | 科目ID |
| name | varchar | 50 | NO | - | - | 科目名称 |
| parent_id | int | - | YES | 0 | - | 父级ID（顶级为0） |
| icon | varchar | 100 | YES | NULL | - | 图标 |
| sort | tinyint | - | YES | 0 | - | 排序号 |
| level | tinyint | - | YES | 1 | - | 层级（1-考试大类，2-考试规格，3-具体学科，4-章节知识点，5-题型） |
| question_count | int | - | YES | 0 | - | 该分类下的题目总数 |
| scope | varchar | 50 | YES | '1,2,3' | - | 适用大纲（1-数一, 2-数二, 3-数三） |
| question_types | varchar | 100 | YES | NULL | - | 支持的题型列表（逗号分隔） |

**层级说明**：
- L1 - 考试大类（专业课/公共课）
- L2 - 考试规格（数学一、英语一）
- L3 - 具体学科（高数、线代）
- L4 - 章节与知识点（函数与极限、二重积分）
- L5 - 题型/解题技巧（泰勒公式）

**索引**：
- PRIMARY KEY (`id`)

---

#### subject_weight_rel - 科目权重映射表

| 字段名 | 类型 | 长度 | 允许NULL | 默认值 | 键 | 说明 |
|--------|------|------|----------|--------|-----|------|
| id | int | - | NO | AUTO_INCREMENT | PK | 主键 |
| subject_id | int | - | NO | - | FK | 科目ID |
| exam_spec_id | int | - | NO | - | FK | 考试规格ID（关联subject.id） |
| weight | float | - | YES | NULL | - | 该科目在该考试规格下的权重（百分比） |

**索引**：
- PRIMARY KEY (`id`)
- UNIQUE KEY `uk_subject_exam` (`subject_id`, `exam_spec_id`)
- INDEX `idx_exam_spec` (`exam_spec_id`)
- INDEX `idx_subject` (`subject_id`)

---

#### book_subject_rel - 考试科目与习题册关联表

| 字段名 | 类型 | 长度 | 允许NULL | 默认值 | 键 | 说明 |
|--------|------|------|----------|--------|-----|------|
| id | int | - | NO | AUTO_INCREMENT | PK | 主键 |
| book_id | int | - | NO | - | FK | 习题册ID |
| subject_id | int | - | NO | - | FK | 知识科目ID（高数:401；线代:402；概率403） |

**索引**：
- PRIMARY KEY (`id`)
- UNIQUE INDEX `uk_subject_book` (`book_id`, `subject_id`)

---

### 6.3 题目管理模块

#### question - 题目表（核心表，使用 JSON 字段）

| 字段名 | 类型 | 长度 | 允许NULL | 默认值 | 键 | 说明 |
|--------|------|------|----------|--------|-----|------|
| id | bigint | - | NO | AUTO_INCREMENT | PK | 题目ID |
| type | tinyint | - | NO | - | INDEX | 题目类型（1:单选, 2:多选, 3:填空, 4:简答） |
| difficulty | tinyint | - | YES | 3 | INDEX | 难度（1-5） |
| content_json | json | - | NO | - | - | 题目内容JSON（content, options, answer, analysis, tags） |
| create_time | datetime | - | YES | CURRENT_TIMESTAMP | - | 创建时间 |

**content_json 字段结构示例**：

```json
// 单选题/多选题结构
{
  "content": "设函数 $f(x) = x^3 - 3x + 1$，求 $f'(x)$",
  "options": [
    {"label": "A", "text": "$3x^2 - 3$"},
    {"label": "B", "text": "$3x^2 + 3$"},
    {"label": "C", "text": "$x^2 - 3$"},
    {"label": "D", "text": "$x^2 + 3$"}
  ],
  "answer": "A",
  "analysis": "根据求导公式，$(x^3)' = 3x^2$，$(-3x)' = -3$，$(1)' = 0$，所以 $f'(x) = 3x^2 - 3$",
  "tags": ["导数", "基本求导公式"],
  "source": "2025年考研数学一真题"
}

// 填空题/简答题结构
{
  "content": "计算极限 $\\lim_{x\\to 0} \\frac{\\sin x}{x}$",
  "answer": "1",
  "analysis": "根据重要极限，$\\lim_{x\\to 0} \\frac{\\sin x}{x} = 1$",
  "tags": ["极限", "重要极限"],
  "source": "高等数学例题"
}
```

**索引**：
- PRIMARY KEY (`id`)
- INDEX `idx_type` (`type`)
- INDEX `idx_difficulty` (`difficulty`)

---

#### question_subject_rel - 题目-科目关联表

| 字段名 | 类型 | 长度 | 允许NULL | 默认值 | 键 | 说明 |
|--------|------|------|----------|--------|-----|------|
| id | bigint | - | NO | AUTO_INCREMENT | PK | 主键 |
| question_id | bigint | - | NO | - | FK | 题目ID |
| subject_id | int | - | NO | - | FK | 科目ID或知识点ID |

**设计要点**：
- ✅ 一道题可以属于多个科目（如"线性代数的极限问题"）
- ✅ 支持科目层级（一道题可属于"高等数学"和"函数与极限"）
- ✅ 联合唯一索引防止重复关联

**索引**：
- PRIMARY KEY (`id`)
- UNIQUE INDEX `uk_question_subject` (`question_id`, `subject_id`)
- INDEX `idx_question_id` (`question_id`)
- INDEX `idx_subject_id` (`subject_id`)

---

#### question_book_rel - 题目-习题册关联表

| 字段名 | 类型 | 长度 | 允许NULL | 默认值 | 键 | 说明 |
|--------|------|------|----------|--------|-----|------|
| id | bigint | - | NO | AUTO_INCREMENT | PK | 主键 |
| question_id | bigint | - | NO | - | FK | 题目ID |
| book_id | int | - | NO | - | FK | 习题册ID |

**索引**：
- PRIMARY KEY (`id`)
- UNIQUE INDEX `uk_question_book` (`question_id`, `book_id`)
- INDEX `idx_question_id` (`question_id`)
- INDEX `idx_book_id` (`book_id`)

---

#### question_paper_rel - 题目-试卷关联表

| 字段名 | 类型 | 长度 | 允许NULL | 默认值 | 键 | 说明 |
|--------|------|------|----------|--------|-----|------|
| id | bigint | - | NO | AUTO_INCREMENT | PK | 主键 |
| paper_id | bigint | - | YES | NULL | FK | 试卷ID |
| question_id | bigint | - | YES | NULL | FK | 题目ID |
| sort_order | smallint | - | YES | NULL | - | 题号 |
| score_value | decimal | 5,2 | YES | NULL | - | 分值 |
| type | varchar | 50 | YES | NULL | - | 题目类型（1-单选, 2-多选, 3-填空, 4-综合应用等） |

**索引**：
- PRIMARY KEY (`id`)
- INDEX `paper_id` (`paper_id`)
- INDEX `idx_map_paper_question_type` (`type`)

---

### 6.4 考试系统模块

#### exam_paper - 试卷表

| 字段名 | 类型 | 长度 | 允许NULL | 默认值 | 键 | 说明 |
|--------|------|------|----------|--------|-----|------|
| id | bigint | - | NO | AUTO_INCREMENT | PK | 试卷ID |
| title | varchar | 255 | NO | - | - | 试卷标题 |
| year | varchar | 10 | YES | NULL | INDEX | 考试年份 |
| exam_spec_id | int | - | YES | NULL | INDEX | 关联科目ID |
| total_score | int | - | YES | 150 | - | 总分 |
| time_limit | int | - | YES | 180 | - | 限时（分钟） |
| paper_type | tinyint | - | YES | NULL | - | 试卷类型（0-真题, 1-模拟卷） |
| structure_json | text | - | YES | NULL | - | 试卷结构JSON |
| create_time | timestamp | - | YES | CURRENT_TIMESTAMP | - | 创建时间 |

**索引**：
- PRIMARY KEY (`id`)
- INDEX `idx_exam_spec_id` (`exam_spec_id`)
- INDEX `idx_year` (`year`)

---

#### exam_session - 考试会话表（支持快照恢复）

| 字段名 | 类型 | 长度 | 允许NULL | 默认值 | 键 | 说明 |
|--------|------|------|----------|--------|-----|------|
| id | bigint | - | NO | - | PK | 会话ID（非自增,由程序生成） |
| user_id | bigint | - | NO | - | INDEX | 用户ID |
| paper_id | bigint | - | NO | - | INDEX | 试卷ID |
| status | tinyint | - | YES | 0 | INDEX | 状态（0-进行中, 1-已完成） |
| start_time | datetime | - | YES | NULL | - | 开始时间 |
| submit_time | timestamp | - | YES | NULL | - | 提交时间 |
| expected_end_time | datetime | - | YES | NULL | - | 预期结束时间 |
| total_score | decimal | 5,2 | YES | NULL | - | 总分 |
| switch_count | int | - | YES | 0 | - | 切换题目次数（切屏检测） |
| ai_summary | text | - | YES | NULL | - | AI 总结 |
| snapshot_answers | json | - | YES | NULL | - | 答题快照 JSON |
| create_time | timestamp | - | NO | CURRENT_TIMESTAMP | - | 创建时间 |

**snapshot_answers JSON 结构**：

```json
{
  "questions": [
    {
      "questionId": 1,
      "scoreValue": 10,
      "sortOrder": 1,
      "userAnswer": "A",
      "isCorrect": true,
      "timeSpent": 120
    }
  ],
  "timeLimit": 3600,
  "mode": "PRACTICE",
  "autoSubmit": true,
  "showAnalysis": true
}
```

**快照恢复机制**：
1. 每隔 30 秒自动保存答题进度到 `snapshot_answers`
2. 用户刷新页面或异常退出后，可从 `snapshot_answers` 恢复答题状态
3. 前端 Pinia store 持久化配合后端数据库双重保障

**索引**：
- PRIMARY KEY (`id`)
- INDEX `idx_user_id` (`user_id`)
- INDEX `idx_paper_id` (`paper_id`)
- INDEX `idx_status` (`status`)

---

#### exam_record - 考试记录表

| 字段名 | 类型 | 长度 | 允许NULL | 默认值 | 键 | 说明 |
|--------|------|------|----------|--------|-----|------|
| id | bigint | - | NO | AUTO_INCREMENT | PK | 主键 |
| session_id | bigint | - | YES | NULL | INDEX | 会话ID |
| question_id | bigint | - | YES | NULL | - | 题目ID |
| user_answer | text | - | YES | NULL | - | 用户答案 |
| is_correct | tinyint | - | YES | NULL | - | 对/错/待定（主观题） |
| score_earned | decimal | 5,2 | YES | NULL | - | 得分率 |
| duration_seconds | int | - | YES | NULL | - | 用时 |
| ai_feedback | text | - | YES | NULL | - | AI返回 |
| knowledge_point_id | int | - | YES | NULL | - | 知识点ID |
| create_time | datetime | - | YES | NULL | - | 创建时间 |

**索引**：
- PRIMARY KEY (`id`)
- INDEX `session_id` (`session_id`)

---

### 6.5 刷题与错题模块

#### answer_record - 答题记录表

| 字段名 | 类型 | 长度 | 允许NULL | 默认值 | 键 | 说明 |
|--------|------|------|----------|--------|-----|------|
| id | bigint | - | NO | AUTO_INCREMENT | PK | 主键 |
| user_id | bigint | - | NO | - | INDEX | 用户ID |
| question_id | bigint | - | NO | - | INDEX | 题目ID |
| user_answer | text | - | YES | NULL | - | 用户的答案 |
| is_correct | tinyint | - | YES | NULL | - | 是否正确（0-错, 1-对, NULL-待定） |
| score | smallint | - | YES | 0 | - | 得分 |
| duration | int | - | YES | 0 | - | 答题用时（秒） |
| create_time | datetime | - | YES | CURRENT_TIMESTAMP | - | 创建时间 |

**索引**：
- PRIMARY KEY (`id`)
- INDEX `idx_user_ques` (`user_id`, `question_id`)

---

#### error_question - 错题记录表

| 字段名 | 类型 | 长度 | 允许NULL | 默认值 | 键 | 说明 |
|--------|------|------|----------|--------|-----|------|
| id | int | - | NO | AUTO_INCREMENT | PK | 主键 |
| user_id | bigint | - | NO | - | INDEX | 用户ID |
| question_id | bigint | - | NO | - | INDEX | 题目ID |
| error_count | int | - | YES | NULL | - | 错误次数 |
| update_time | datetime | - | YES | NULL | - | 更新时间 |
| create_time | datetime | - | YES | CURRENT_TIMESTAMP | - | 添加时间 |

**索引**：
- PRIMARY KEY (`id`)

---

#### favorite_folder - 收藏夹

| 字段名 | 类型 | 长度 | 允许NULL | 默认值 | 键 | 说明 |
|--------|------|------|----------|--------|-----|------|
| id | bigint | - | NO | AUTO_INCREMENT | PK | 主键 |
| user_id | bigint | - | NO | - | INDEX | 用户ID |
| question_id | bigint | - | NO | - | INDEX | 题目ID |
| tags | json | - | YES | NULL | - | 自定义标签 |
| create_time | datetime | - | YES | CURRENT_TIMESTAMP | - | 创建时间 |

**索引**：
- PRIMARY KEY (`id`)
- UNIQUE INDEX `uk_user_ques` (`user_id`, `question_id`)

---

### 6.6 习题册与资源模块

#### exercise_book - 习题册表

| 字段名 | 类型 | 长度 | 允许NULL | 默认值 | 键 | 说明 |
|--------|------|------|----------|--------|-----|------|
| id | bigint | - | NO | AUTO_INCREMENT | PK | 主键 |
| name | varchar | 100 | NO | - | - | 习题册名称 |
| description | varchar | 255 | YES | NULL | - | 简介 |
| create_time | datetime | - | YES | CURRENT_TIMESTAMP | - | 创建时间 |

**索引**：
- PRIMARY KEY (`id`)

---

#### resource - 学习资料表

> ⚠️ **注意**: 此表存在于数据库中,但当前代码版本暂未使用

| 字段名 | 类型 | 长度 | 允许NULL | 默认值 | 键 | 说明 |
|--------|------|------|----------|--------|-----|------|
| id | bigint | - | NO | AUTO_INCREMENT | PK | 主键 |
| title | varchar | 100 | NO | - | - | 资料标题 |
| url | varchar | 255 | NO | - | - | 下载/预览地址 |
| file_type | varchar | 20 | YES | 'pdf' | - | 文件类型 |
| subject_id | int | - | YES | NULL | - | 科目ID |
| create_time | datetime | - | YES | CURRENT_TIMESTAMP | - | 创建时间 |

**索引**：
- PRIMARY KEY (`id`)

---

### 6.7 表关系总结

```
用户与权限模块：
├── user (用户表)

科目与知识体系模块：
├── subject (科目表 - 树形结构)
├── subject_weight_rel (科目权重映射表)
└── book_subject_rel (习题册-科目关联表)

题目管理模块：
├── question (题目表 - JSON字段)
├── question_subject_rel (题目-科目关联表)
├── question_book_rel (题目-习题册关联表)
└── question_paper_rel (题目-试卷关联表)

考试系统模块：
├── exam_paper (试卷表)
├── exam_session (考试会话表 - 快照恢复)
└── exam_record (考试记录表)

刷题与错题模块：
├── answer_record (答题记录表)
├── error_question (错题记录表)
└── favorite_folder (收藏夹)

习题册与资源模块：
├── exercise_book (习题册表)
└── resource (学习资料表) ⚠️ 数据库中存在但代码暂未使用
```

---

### 6.8 JSON 字段设计优势

| 设计特点 | 传统方案 | 本项目方案 | 优势 |
|----------|----------|------------|------|
| 题目存储 | 多表关联（question + question_options + question_tags） | JSON 字段 | ✅ 查询简化、扩展性强 |
| 科目层级 | 递归查询或闭包表 | 简单的 parent_id | ✅ 代码处理更灵活 |
| 考试会话 | 多表关联（exam + exam_answers） | JSON 存储答题状态 | ✅ 快照恢复更容易 |
| 错题统计 | 每次错误插入新记录 | 累加 error_count | ✅ 节省空间、统计便捷 |

---

### 6.9 核心索引策略

| 表名 | 索引名 | 字段 | 类型 | 用途 |
|------|--------|------|------|------|
| question | idx_type | type | 普通索引 | 按题型查询 |
| question | idx_difficulty | difficulty | 普通索引 | 按难度查询 |
| subject | idx_parent_id | parent_id | 普通索引 | 树形结构查询 |
| question_subject_rel | uk_question_subject | question_id, subject_id | 联合唯一索引 | 防止重复关联 |
| exam_session | idx_user_id | user_id | 普通索引 | 按用户查询 |
| exam_session | idx_status | status | 普通索引 | 按状态筛选 |
| exam_session | idx_paper_id | paper_id | 普通索引 | 按试卷查询 |
| exam_paper | idx_year | year | 普通索引 | 按年份筛选 |
| answer_record | idx_user_ques | user_id, question_id | 联合索引 | 查询用户答题记录 |
| favorite_folder | uk_user_ques | user_id, question_id | 联合唯一索引 | 防止重复收藏 |

---

### 6.10 JSON 查询示例

```sql
-- 查询包含特定标签的题目
SELECT * FROM question
WHERE JSON_CONTAINS(content_json->'$.tags', '"极限"');

-- 查询题目内容中包含关键字的题目
SELECT * FROM question
WHERE content_json->'$.content' LIKE '%积分%';

-- 提取所有题目的标签
SELECT
  id,
  JSON_EXTRACT(content_json, '$.tags') AS tags
FROM question;

-- 更新题目标签
UPDATE question
SET content_json = JSON_SET(content_json, '$.tags', '["极限", "洛必达法则"]')
WHERE id = 1;
```

---

## 7. 核心功能模块详解

> 本项目提供多种刷题模式、AI 智能辅助和完整的题库管理功能。

### 7.1 刷题模式

#### 7.1.1 逐题精练模式

**功能特点**：
- 单题深度练习模式，适合针对性复习
- 即时反馈，答题后立即显示正确答案和解析
- 支持收藏、标记题目
- 自动记录错题到错题本

**页面路径**：`/user/single-practice/:subjectId`

**核心实现** ([SinglePractice.vue](vue-front/src/views/quiz/SinglePractice.vue))：

```javascript
// 题目状态管理
const currentQuestion = ref(null)
const currentIndex = ref(0)
const questions = ref([])
const userAnswer = ref(null)
const showResult = ref(false)

// 提交答案
function submitAnswer() {
  // 1. 调用后端接口记录答题
  await recordAnswer({
    questionId: currentQuestion.value.id,
    userAnswer: userAnswer.value,
    isCorrect: checkAnswer()
  })

  // 2. 如果答错，自动加入错题本
  if (!checkAnswer()) {
    await addToErrorBook(currentQuestion.value.id)
  }

  // 3. 显示答案和解析
  showResult.value = true
}

// 下一题
function nextQuestion() {
  userAnswer.value = null
  showResult.value = false
  currentIndex.value++
  currentQuestion.value = questions.value[currentIndex.value]
}
```

**交互流程**：

```
┌─────────────────────────────────────┐
│  1. 选择科目/知识点                    │
└─────────────┬───────────────────────┘
              ↓
┌─────────────────────────────────────┐
│  2. 显示题目（KaTeX 渲染公式）        │
│     - 题目内容                       │
│     - 选项列表（选择题型）            │
│     - 答案输入框（填空/简答）         │
└─────────────┬───────────────────────┘
              ↓
┌─────────────────────────────────────┐
│  3. 用户答题                         │
│     - 点击选项                       │
│     - 输入答案                       │
└─────────────┬───────────────────────┘
              ↓
┌─────────────────────────────────────┐
│  4. 提交答案                         │
│     - 即时判分（客观题自动）          │
│     - AI 批改（主观题可选）           │
└─────────────┬───────────────────────┘
              ↓
┌─────────────────────────────────────┐
│  5. 显示结果                         │
│     - ✅ / ❌ 答题正确性              │
│     - 正确答案                        │
│     - 详细解析                        │
│     - 相关知识点                      │
└─────────────┬───────────────────────┘
              ↓
┌─────────────────────────────────────┐
│  6. 操作选项                         │
│     - 下一题                          │
│     - 收藏题目                        │
│     - 查看大图                        │
└─────────────────────────────────────┘
```

---

#### 7.1.2 专项突破模式

**功能特点**：
- 按知识点/题型集中练习
- 可设置题目数量上限
- 统计正确率
- 针对性薄弱点训练

**页面路径**：`/user/topic-drill`

**配置参数**：

```javascript
// 专项练习配置
const drillConfig = {
  subjectId: 3,           // 知识点ID（函数与极限）
  questionType: 1,        // 题型筛选（1-单选, 2-多选, 3-填空, 4-简答）
  difficulty: 3,          // 难度筛选（1-5）
  questionCount: 20,      // 题目数量上限
  mode: 'sequential'      // 练习模式（sequential-顺序, random-随机）
}
```

**实现逻辑**：

```javascript
// 获取专项题目
async function loadDrillQuestions(config) {
  const response = await api.getQuestionsBySubject({
    subjectId: config.subjectId,
    type: config.questionType,
    difficulty: config.difficulty,
    limit: config.questionCount
  })

  // 随机排序（如果选择随机模式）
  if (config.mode === 'random') {
    response.data.sort(() => Math.random() - 0.5)
  }

  questions.value = response.data
}

// 统计正确率
const accuracy = computed(() => {
  const correct = questions.value.filter(q => q.isCorrect).length
  return Math.round((correct / questions.value.length) * 100)
})
```

---

#### 7.1.3 沉浸式模考模式

**功能特点**：
- 真实考试环境模拟
- 倒计时限制
- 切屏检测
- 自动保存答题进度
- 考试结束自动提交

**页面路径**：`/user/mock-exam/:paperId`

**核心功能** ([MockExam.vue](vue-front/src/views/quiz/MockExam.vue))：

```javascript
import { useExamStore } from '@/stores/exam'

const examStore = useExamStore()

// 1. 初始化考试
async function initExam() {
  await examStore.loadPaperInfo(route.params.paperId)
  await examStore.initExam(userId, route.params.paperId)

  // 启动倒计时
  startTimer()
}

// 2. 倒计时（每秒更新）
function startTimer() {
  const timer = setInterval(() => {
    examStore.tickTimer()

    // 自动保存快照（每30秒）
    if (examStore.remainingTime % 30 === 0) {
      saveSnapshot()
    }

    // 时间到自动提交
    if (examStore.remainingTime <= 0) {
      clearInterval(timer)
      handleSubmit()
    }
  }, 1000)
}

// 3. 保存答题快照
async function saveSnapshot() {
  await api.saveSnapshot(examStore.sessionId, {
    userAnswers: examStore.userAnswers,
    markedQuestions: examStore.markedQuestions,
    remainingTime: examStore.remainingTime
  })
}

// 4. 切屏检测
document.addEventListener('visibilitychange', () => {
  if (document.hidden) {
    examStore.incrementSwitchCount()

    if (examStore.switchCount > 3) {
      ElMessage.warning(`已检测到 ${examStore.switchCount} 次切屏！`)
    }
  }
})

// 5. 浏览器返回阻止
window.history.pushState(null, null, location.href)
window.addEventListener('popstate', () => {
  window.history.pushState(null, null, location.href)
  ElMessage.warning('考试进行中，请勿退出')
})

// 6. 提交试卷
async function handleSubmit() {
  const confirmed = await ElMessageBox.confirm(
    '确定要提交试卷吗？提交后无法修改答案。',
    '提交确认',
    { type: 'warning' }
  )

  if (confirmed) {
    await examStore.submitExam()
    // 跳转到成绩页
    router.push(`/user/exam-result/${examStore.sessionId}`)
  }
}
```

**界面特点**：
- ✅ 真实试卷样式（密封线、答题卡布局）
- ✅ 草稿纸功能（全屏可编辑文本区）
- ✅ 全屏模式（防干扰）
- ✅ 倒计时醒目显示
- ✅ 题目快速导航（题号列表）

---

### 7.2 考试会话管理

#### 7.2.1 会话生命周期

```
未开始 (NOT_STARTED)
    ↓
开始考试 (startExam)
    ↓
进行中 (IN_PROGRESS)
    ├─ 自动保存快照 (每30秒)
    ├─ 切屏检测记录
    └─ 倒计时更新
    ↓
提交试卷 (submitExam)
    ├─ 客观题自动判分
    ├─ 主观题 AI 批改（可选）
    └─ 生成成绩报告
    ↓
已完成 (SUBMITTED)
```

**后端实现** ([ExamSessionController.java](java-back/src/main/java/org/example/kaoyanplatform/controller/ExamSessionController.java))：

```java
@PostMapping("/start")
public Result<ExamStartDTO> startExam(@RequestParam String userId, @RequestParam String paperId) {
    // 1. 检查是否有未完成的会话
    ExamSession existingSession = examSessionService.getUnfinishedSession(userId, paperId);
    if (existingSession != null) {
        // 恢复未完成的考试
        return Result.success(examSessionService.resumeSession(existingSession));
    }

    // 2. 创建新会话
    ExamSession newSession = new ExamSession();
    newSession.setUserId(Long.parseLong(userId));
    newSession.setPaperId(Long.parseLong(paperId));
    newSession.setStatus(0); // NOT_STARTED
    newSession.setStartTime(LocalDateTime.now());

    // 3. 加载试卷题目
    List<Question> questions = examSessionService.loadPaperQuestions(paperId);

    // 4. 保存会话配置
    ExamStartDTO startDTO = new ExamStartDTO();
    startDTO.setSessionId(newSession.getId());
    startDTO.setQuestions(questions);
    startDTO.setTimeLimit(paper.getTimeLimit());

    examSessionService.save(newSession);
    return Result.success(startDTO);
}
```

---

#### 7.2.2 快照恢复机制

**前端实现**（Pinia Store - [exam.js](vue-front/src/stores/exam.js)）：

```javascript
// 自动保存快照
setInterval(() => {
  if (examStatus === 'in-progress') {
    const snapshot = {
      sessionId: sessionId.value,
      userAnswers: userAnswers.value,
      markedQuestions: markedQuestions.value,
      remainingTime: examInfo.value.remainingTime,
      timestamp: Date.now()
    }

    // 保存到 localStorage
    localStorage.setItem('exam_snapshot', JSON.stringify(snapshot))

    // 同步到后端
    api.saveSnapshot(sessionId.value, JSON.stringify(snapshot))
  }
}, 30000) // 每30秒

// 恢复快照
async function restoreSnapshot() {
  const savedSnapshot = localStorage.getItem('exam_snapshot')
  if (savedSnapshot) {
    const snapshot = JSON.parse(savedSnapshot)

    // 验证会话是否匹配
    if (snapshot.sessionId === sessionId.value) {
      userAnswers.value = snapshot.userAnswers
      markedQuestions.value = snapshot.markedQuestions
      examInfo.value.remainingTime = snapshot.remainingTime

      ElMessage.success('已恢复上次答题进度')
    }
  }
}
```

---

### 7.3 AI 功能集成

#### 7.3.1 AI 图片识别

**功能**：上传题目图片，自动识别为结构化题目数据

**流程**：

```
用户上传图片 (前端)
    ↓
POST /api/common/recognize-image (Java)
    ↓
转发到 Python 后端 /ai/recognize
    ↓
GLM-4.6V-Flash 识别
    ↓
返回结构化 JSON
{
  "type": 1,
  "content": "设函数 $f(x) = x^3 - 3x + 1$，求 $f'(x)$",
  "options": [
    {"label": "A", "text": "$3x^2 - 3$"},
    ...
  ],
  "answer": "A",
  "analysis": "根据求导公式..."
}
    ↓
Java 保存到数据库 (question.content_json)
    ↓
前端展示（KaTeX 渲染公式）
```

**前端实现**：

```javascript
async function handleImageUpload(file) {
  const formData = new FormData()
  formData.append('file', file)

  try {
    // 调用识别接口
    const response = await api.recognizeImage(formData)

    // 填充到表单
    questionForm.value = {
      type: response.data.type,
      content: response.data.content,
      options: response.data.options,
      answer: response.data.answer,
      analysis: response.data.analysis,
      tags: response.data.tags
    }

    ElMessage.success('识别成功！')
  } catch (error) {
    ElMessage.error('识别失败：' + error.message)
  }
}
```

**后端实现** ([PythonBackendClient.java](java-back/src/main/java/org/example/kaoyanplatform/client/PythonBackendClient.java))：

```java
public QuestionDTO recognizeQuestion(MultipartFile file) {
    // 1. 创建临时文件
    File tempFile = File.createTempFile("upload_", "_" + file.getOriginalFilename());
    file.transferTo(tempFile);

    try {
        // 2. 构造 multipart 请求
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(tempFile) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        // 3. 发送请求到 Python 服务
        ResponseEntity<String> response = restTemplate.exchange(
                pythonBackendUrl + "/ai/recognize",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                String.class
        );

        // 4. 解析响应
        Map<String, Object> result = objectMapper.readValue(
                response.getBody(),
                new TypeReference<Map<String, Object>>() {}
        );

        return convertToQuestionDTO(result);
    } finally {
        tempFile.delete();
    }
}
```

---

#### 7.3.2 AI 智能批改

**功能**：主观题自动评分与反馈

**流程**：

```
用户提交主观题答案
    ↓
POST /api/exam-session/submit (Java)
    ↓
客观题自动判分
    ↓
主观题调用 Python 后端 /ai/grade
    ↓
GLM-4.7 智能批改
    ↓
返回评分结果
{
  "score": 85,
  "feedback": "解题思路正确，但计算过程有误...",
  "strengths": ["步骤清晰", "公式使用正确"],
  "weaknesses": ["计算错误", "符号不规范"]
}
    ↓
保存到 exam_record.ai_feedback
    ↓
前端展示成绩和 AI 反馈
```

**前端展示**：

```vue
<template>
  <div class="ai-feedback">
    <div class="score-circle">
      <el-progress type="circle" :percentage="aiFeedback.score" :width="120" />
    </div>

    <div class="feedback-content">
      <h4>AI 评语</h4>
      <p>{{ aiFeedback.feedback }}</p>

      <h4>✅ 优点</h4>
      <ul>
        <li v-for="strength in aiFeedback.strengths" :key="strength">
          {{ strength }}
        </li>
      </ul>

      <h4>❌ 待改进</h4>
      <ul>
        <li v-for="weakness in aiFeedback.weaknesses" :key="weakness">
          {{ weakness }}
        </li>
      </ul>
    </div>
  </div>
</template>
```

---

### 7.4 题目导入导出

#### 7.4.1 批量导入

**支持格式**：
- **JSON 格式**：结构化题目数据
- **Markdown 格式**：纯文本题目（AI 提取结构）

**JSON 导入格式示例**：

```json
[
  {
    "type": 1,
    "difficulty": 3,
    "content": "设函数 $f(x) = x^3 - 3x + 1$，求 $f'(x)$",
    "options": [
      {"label": "A", "text": "$3x^2 - 3$"},
      {"label": "B", "text": "$3x^2 + 3$"},
      {"label": "C", "text": "$x^2 - 3$"},
      {"label": "D", "text": "$x^2 + 3$"}
    ],
    "answer": "A",
    "analysis": "根据求导公式...",
    "tags": ["导数", "基本求导公式"],
    "subjectIds": [3, 5],
    "bookId": 2
  }
]
```

**导入流程**：

```javascript
async function importQuestions(file) {
  const formData = new FormData()
  formData.append('file', file)

  const response = await api.importQuestions(formData)

  ElMessage.success(`成功导入 ${response.data.count} 道题目`)

  // 刷新题目列表
  await loadQuestions()
}
```

---

#### 7.4.2 PDF 导出

**功能特点**：
- 支持按科目/习题册/试卷导出
- 保留 LaTeX 公式格式
- 包含题目、选项、答案、解析
- 分页美观排版

**实现原理** ([PdfExportServiceImpl.java](java-back/src/main/java/org/example/kaoyanplatform/service/impl/PdfExportServiceImpl.java))：

```java
@Override
public byte[] exportQuestionsToPdf(QuestionExportDTO exportDTO) {
    // 1. 获取题目列表
    List<Question> questions = getQuestionsByExportConfig(exportDTO);

    // 2. 处理 LaTeX 公式 → HTML
    processQuestionContent(questions);

    // 3. 加载 Thymeleaf 模板
    Context context = new Context();
    context.setVariable("questions", questions);
    context.setVariable("title", "考研题目导出");

    String htmlContent = templateEngine.process("pdf-template", context);

    // 4. Flying Saucer 生成 PDF
    ITextRenderer renderer = new ITextRenderer();
    renderer.setDocumentFromString(htmlContent);
    renderer.layout();

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    renderer.createPDF(outputStream);

    return outputStream.toByteArray();
}
```

**PDF 模板** (Thymeleaf)：

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <style>
        .question { margin-bottom: 20px; page-break-inside: avoid; }
        .content { font-size: 14pt; margin: 10px 0; }
        .options { margin-left: 20px; }
        .answer { color: #00C853; font-weight: bold; }
        .analysis { color: #666; font-style: italic; }
    </style>
</head>
<body>
    <h1 th:text="${title}">题目导出</h1>

    <div th:each="q : ${questions}" class="question">
        <div class="content" th:utext="${q.content}">题目内容</div>

        <div class="options" th:if="${q.type == 1 or q.type == 2}">
            <div th:each="opt : ${q.options}">
                <span th:text="${opt.label}">A</span>.
                <span th:utext="${opt.text}">选项内容</span>
            </div>
        </div>

        <div class="answer">
            答案：<span th:text="${q.answer}">A</span>
        </div>

        <div class="analysis" th:utext="${q.analysis}">解析内容</div>
    </div>
</body>
</html>
```

---

### 7.5 错题本功能

**核心流程**：

```
用户答错题目
    ↓
自动添加到错题本 (error_question 表)
    ├─ user_id: 用户ID
    ├─ question_id: 题目ID
    └─ error_count: 错误次数（累加）
    ↓
错题本页面展示
    ├─ 按科目筛选
    ├─ 按错题次数排序
    └─ 显示错误次数和时间
    ↓
重新作答
    ↓
答对后标记为"已掌握"
    ↓
从错题本移除（或保留记录标记掌握）
```

**前端实现** ([CorrectionNotebook.vue](vue-front/src/views/CorrectionNotebook.vue))：

```javascript
// 加载错题
async function loadErrorQuestions() {
  const response = await api.getErrorBook(userId.value)
  errorQuestions.value = response.data
}

// 重新作答
async function reAnswer(question) {
  // 跳转到逐题练习模式
  router.push({
    path: `/user/single-practice/${question.subjectId}`,
    query: {
      questionIds: [question.id],
      mode: 'error-book'  // 标记为错题模式
    }
  })
}

// 标记为已掌握
async function markAsMastered(questionId) {
  await api.updateErrorQuestion({
    userId: userId.value,
    questionId: questionId,
    isMastered: true
  })

  ElMessage.success('已标记为掌握')
  await loadErrorQuestions()
}
```

---

## 8. API 接口文档

> 本项目提供完整的 RESTful API，访问地址：`http://localhost:8081/api`

### 8.1 用户端 API

#### 用户管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 用户登录 | POST | `/user/login` | 用户登录认证 |
| 用户注册 | POST | `/user/register` | 新用户注册 |
| 获取用户信息 | GET | `/user/info` | 获取当前登录用户信息 |
| 更新用户信息 | PUT | `/user/update` | 更新用户资料 |

**请求示例**：

```bash
# 用户登录
POST /api/user/login
Content-Type: application/json

{
  "username": "student",
  "password": "123456"
}

# 响应
{
  "code": 200,
  "msg": "成功",
  "data": {
    "id": 1,
    "username": "student",
    "role": "student",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

---

#### 题目练习

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 按科目获取题目 | GET | `/question/list-by-subject` | 根据科目ID或书本ID获取题目 |
| 按知识点获取题目 | GET | `/question/list-by-knowledge-point` | 递归查询子科目题目 |
| 获取题目详情 | GET | `/question/{id}` | 获取题目完整信息 |
| 记录答题 | POST | `/answer/record` | 记录用户答题结果 |
| 获取错题本 | GET | `/question/getErrorBook` | 获取用户错题列表 |

**请求示例**：

```bash
# 按科目获取题目
GET /api/question/list-by-subject?subjectId=3

# 响应
{
  "code": 200,
  "msg": "成功",
  "data": [
    {
      "id": 1,
      "type": 1,
      "difficulty": 3,
      "content": "设函数 $f(x) = x^3 - 3x + 1$，求 $f'(x)$",
      "contentJson": {
        "content": "设函数 $f(x) = x^3 - 3x + 1$，求 $f'(x)$",
        "options": [...],
        "answer": "A",
        "analysis": "...",
        "tags": ["导数"]
      },
      "subjectIds": [3, 5],
      "bookIds": [2]
    }
  ]
}
```

---

#### 考试会话

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 开始/恢复考试 | POST | `/exam-session/start` | 初始化考试会话或恢复未完成考试 |
| 保存答题快照 | POST | `/exam-session/snapshot` | 自动保存答题进度 |
| 记录切屏 | POST | `/exam-session/switch` | 记录用户切屏次数 |
| 提交试卷 | POST | `/exam-session/submit` | 提交考试并触发AI批改 |
| 获取会话详情 | GET | `/exam-session/{sessionId}` | 获取考试会话完整信息 |
| 获取考试历史 | GET | `/exam-session/user/{userId}` | 获取用户所有考试记录 |

**请求示例**：

```bash
# 开始考试
POST /api/exam-session/start
Content-Type: application/x-www-form-urlencoded

userId=1&paperId=5

# 响应
{
  "code": 200,
  "msg": "成功",
  "data": {
    "sessionId": "123456",
    "questions": [...],
    "timeLimit": 7200,
    "startTime": "2026-01-26T10:00:00"
  }
}

# 保存答题快照
POST /api/exam-session/snapshot
Content-Type: application/x-www-form-urlencoded

sessionId=123456&snapshotJson={"userAnswers":{"1":"A"},"remainingTime":3600}
```

---

#### 科目与习题册

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取科目树 | GET | `/subject/tree` | 获取完整的科目树形结构 |
| 获取习题册列表 | GET | `/book/list` | 获取所有习题册 |
| 获取试卷列表 | GET | `/paper/list` | 获取所有试卷 |

---

### 8.2 管理端 API

#### 题目管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 分页查询题目 | GET | `/question/page` | 分页获取题目列表 |
| 新增题目 | POST | `/question/add` | 新增题目及关联关系 |
| 更新题目 | POST | `/question/update` | 更新题目信息 |
| 删除题目 | DELETE | `/question/delete/{id}` | 级联删除题目及关联 |
| 批量导入 | POST | `/question/import` | JSON/MD文件批量导入 |
| 导出PDF | POST | `/question/export-pdf` | 导出题目为PDF文件 |

**请求示例**：

```bash
# 新增题目
POST /api/question/add
Content-Type: application/json

{
  "type": 1,
  "difficulty": 3,
  "content": "设函数 $f(x) = x^3 - 3x + 1$，求 $f'(x)$",
  "options": [
    {"label": "A", "text": "$3x^2 - 3$"},
    {"label": "B", "text": "$3x^2 + 3$"},
    {"label": "C", "text": "$x^2 - 3$"},
    {"label": "D", "text": "$x^2 + 3$"}
  ],
  "answer": "A",
  "analysis": "根据求导公式...",
  "tags": ["导数"],
  "subjectIds": [3, 5],
  "bookId": 2
}

# 响应
{
  "code": 200,
  "msg": "添加成功"
}
```

---

#### 科目管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取科目列表 | GET | `/subject/list` | 获取所有科目（平铺） |
| 获取科目树 | GET | `/subject/tree` | 获取科目树形结构 |
| 新增科目 | POST | `/subject/add` | 新增科目 |
| 更新科目 | PUT | `/subject/update` | 更新科目信息 |
| 删除科目 | DELETE | `/subject/delete/{id}` | 删除科目（级联删除子科目） |
| 拖拽排序 | POST | `/subject/sort` | 更新科目排序 |

---

#### 用户管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取用户列表 | GET | `/admin/user/list` | 分页获取用户列表 |
| 更新用户角色 | PUT | `/admin/user/role` | 更新用户角色（admin/student） |
| 删除用户 | DELETE | `/admin/user/delete/{id}` | 删除用户 |

---

#### 考试记录管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取所有考试记录 | GET | `/admin/exam-records` | 分页获取考试记录 |
| 删除考试记录 | DELETE | `/admin/exam-record/delete/{id}` | 删除考试记录 |

---

### 8.3 公共 API

#### 文件上传

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 上传图片 | POST | `/common/upload` | 上传图片文件（题目图片等） |
| AI 图片识别 | POST | `/common/recognize-image` | 识别图片中的题目 |

---

#### 收藏夹

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 添加收藏 | POST | `/favorite/add` | 添加题目到收藏夹 |
| 取消收藏 | DELETE | `/favorite/delete` | 从收藏夹移除 |
| 获取收藏列表 | GET | `/favorite/list` | 获取用户收藏的题目 |

---

### 8.4 Python 后端 API

> Python 后端独立部署于 `http://localhost:8082`

#### AI 服务

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 图片识别 | POST | `/ai/recognize` | 识别图片中的题目（支持LaTeX公式） |
| 智能批改 | POST | `/ai/grade` | 主观题智能批改 |

**请求示例**：

```bash
# 图片识别
curl -X POST "http://localhost:8082/ai/recognize" \
  -F "file=@question.jpg"

# 响应
{
  "code": 200,
  "message": "识别成功",
  "data": {
    "type": 1,
    "content": "设函数 $f(x) = x^3 - 3x + 1$，求 $f'(x)$",
    "options": [
      {"label": "A", "text": "$3x^2 - 3$"},
      {"label": "B", "text": "$3x^2 + 3$"}
    ],
    "answer": "A",
    "analysis": "根据求导公式...",
    "tags": ["导数", "基本求导公式"],
    "difficulty": 3
  }
}

# 智能批改
curl -X POST "http://localhost:8082/ai/grade" \
  -H "Content-Type: application/json" \
  -d '{
    "question_content": "计算极限...",
    "user_answer": "答案是 1",
    "reference_answer": "1",
    "question_type": 3
  }'

# 响应
{
  "code": 200,
  "message": "批改成功",
  "data": {
    "score": 85,
    "feedback": "解题思路正确，但需要写出详细步骤...",
    "strengths": ["答案正确", "理解题意"],
    "weaknesses": ["步骤不完整", "缺少中间过程"]
  }
}
```

---

#### 数据处理工具

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| PDF 提取 | POST | `/tools/pdf/extract` | 使用 MinerU 提取PDF内容为 Markdown |
| Markdown 转 JSON | POST | `/tools/md/to-json` | 将 Markdown 格式题目转换为 JSON |

---

#### 健康检查

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 健康检查 | GET | `/health` | 检查 Python 服务运行状态 |

**响应**：

```json
{
  "status": "healthy",
  "service": "python-backend",
  "version": "1.0.0"
}
```

---

### 8.5 API 文档访问

- **Java 后端 API 文档**：[http://localhost:8081/doc.html](http://localhost:8081/doc.html)
- **Python 后端 API 文档**：[http://localhost:8082/docs](http://localhost:8082/docs)

---

## 9. 配置说明详解

> 本项目涉及多个配置文件，包括 Java 后端、Python 后端和 Vue 前端的配置。

### 9.1 Java 后端配置

#### application.yml 核心配置

[application.yml](java-back/src/main/resources/application.yml):

```yaml
# ========== 服务端口 ==========
server:
  port: 8081  # Java 后端服务端口

# ========== 数据源配置 ==========
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    jdbc-url: jdbc:mysql://localhost:3306/kaoyan_platform?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8
    username: root
    password: 123456

  # ========== JPA 配置 ==========
  jpa:
    hibernate:
      ddl-auto: none  # 不自动创建表结构
    show-sql: false  # 不打印SQL语句

# ========== MyBatis Plus 配置 ==========
mybatis-plus:
  configuration:
    map-underline-to-camel-case: true  # 数据库下划线转驼峰
  global-config:
    db-config:
      id-type: auto  # 主键自增
  mapper-locations: classpath*:/mapper/**/*.xml  # Mapper XML 文件位置

# ========== Python 后端配置 ==========
python:
  backend:
    url: http://localhost:8082  # Python 后端服务地址
    timeout: 30000  # 请求超时时间（毫秒）
    enabled: true  # 是否启用 Python 后端
```

**配置说明**：

| 配置项 | 说明 | 默认值 | 修改建议 |
|--------|------|--------|----------|
| `server.port` | Java 后端端口 | 8081 | 生产环境可修改为其他端口 |
| `spring.datasource.jdbc-url` | 数据库连接地址 | - | 需根据实际环境修改 `localhost:3306` |
| `spring.datasource.username` | 数据库用户名 | root | 生产环境使用专用账号 |
| `spring.datasource.password` | 数据库密码 | 123456 | ⚠️ 生产环境必须修改 |
| `python.backend.url` | Python 后端地址 | http://localhost:8082 | Python 后端部署后修改 |
| `python.backend.enabled` | 是否启用 Python 后端 | true | AI 功能需要开启 |

---

#### Maven 依赖配置

[pom.xml](java-back/pom.xml):

```xml
<dependencies>
    <!-- Spring Boot 核心 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- 安全认证 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- 数据库 -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- MyBatis Plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
        <version>3.5.5</version>
    </dependency>

    <!-- API 文档 -->
    <dependency>
        <groupId>com.github.xiaoymin</groupId>
        <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
        <version>4.5.0</version>
    </dependency>

    <!-- PDF 生成 -->
    <dependency>
        <groupId>org.xhtmlrenderer</groupId>
        <artifactId>flying-saucer-pdf</artifactId>
        <version>9.1.22</version>
    </dependency>
    <dependency>
        <groupId>org.thymeleaf</groupId>
        <artifactId>thymeleaf</artifactId>
    </dependency>

    <!-- 工具类 -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

---

### 9.2 Python 后端配置

#### 环境变量配置 (.env)

[python-back/.env](python-back/.env):

```env
# ========== GLM API 配置 ==========
GLM_API_KEY=your_api_key_here  # ⚠️ 必须替换为实际的 API Key
GLM_MODEL_IMAGE=glm-4v-flash      # 图片识别模型（免费）
GLM_MODEL_GRADING=glm-4-plus     # 智能批改模型
GLM_BASE_URL=https://open.bigmodel.cn/api/paas/v4/

# ========== 服务配置 ==========
APP_PORT=8082                    # Python 后端端口
APP_HOST=0.0.0.0                # 监听所有地址

# ========== CORS 配置 ==========
ALLOWED_ORIGINS=http://localhost:5173,http://localhost:8081

# ========== 日志配置 ==========
LOG_LEVEL=INFO                   # 日志级别
```

**配置说明**：

| 配置项 | 说明 | 获取方式 |
|--------|------|----------|
| `GLM_API_KEY` | 智谱 AI API Key | [智谱 AI 开放平台](https://open.bigmodel.cn/) |
| `GLM_MODEL_IMAGE` | 图片识别模型 | 默认使用 `glm-4v-flash`（免费） |
| `GLM_MODEL_GRADING` | 智能批改模型 | 默认使用 `glm-4-plus` |
| `APP_PORT` | Python 后端端口 | 默认 8082，可修改 |
| `ALLOWED_ORIGINS` | CORS 允许的源 | 前端和 Java 后端地址 |

---

#### 依赖配置 (requirements.txt)

[requirements.txt](python-back/requirements.txt):

```txt
# Web 框架
fastapi>=0.104.0
uvicorn[standard]>=0.24.0

# AI 服务
openai>=1.0.0
pydantic>=2.0.0

# PDF 处理
PyMuPDF>=1.23.0
mineru>=0.5.0

# 日志
loguru>=0.7.0

# 配置管理
pydantic-settings>=2.0.0
```

---

### 9.3 Vue 前端配置

#### Vite 配置 (vite.config.js)

[vite.config.js](vue-front/vite.config.js):

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import svgLoader from 'vite-svg-loader'

export default defineConfig({
  plugins: [
    vue(),                      // Vue 3 单文件组件支持
    vueDevTools(),              // Vue 3 开发者工具
    svgLoader()                 // SVG 文件加载器
  ],

  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))  // 路径别名
    }
  },

  server: {
    port: 5173,                 // 开发服务器端口
    host: true,                 // 监听所有地址
    open: true                  // 自动打开浏览器
  }
})
```

**配置说明**：

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `plugins` | Vite 插件列表 | vue, vueDevTools, svgLoader |
| `resolve.alias` | 路径别名 | `@` → `src` 目录 |
| `server.port` | 开发服务器端口 | 5173 |
| `server.host` | 监听地址 | `true`（所有地址） |

---

#### API 基础地址配置

[api/common.js](vue-front/src/api/common.js)：

```javascript
import axios from 'axios'

const request = axios.create({
  baseURL: 'http://localhost:8081/api',  // Java 后端地址
  timeout: 30000,                        // 超时时间
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器（添加 Token）
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器（统一错误处理）
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  error => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)
```

**生产环境配置**：

```javascript
// 生产环境使用不同的 baseURL
const baseURL = process.env.NODE_ENV === 'production'
  ? 'https://api.yourdomain.com/api'  // 生产环境 API 地址
  : 'http://localhost:8081/api'         // 开发环境 API 地址
```

---

#### package.json 核心依赖

[package.json](vue-front/package.json):

```json
{
  "name": "kaoyan-frontend",
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "vue": "^3.5.26",
    "vue-router": "^4.6.4",
    "pinia": "^3.0.4",
    "element-plus": "^2.13.0",
    "katex": "^0.16.27",
    "echarts": "^6.0.0",
    "axios": "^1.13.2"
  },
  "devDependencies": {
    "vite": "^7.3.0",
    "@vitejs/plugin-vue": "^6.0.3",
    "sass-embedded": "^1.97.2"
  }
}
```

---

### 9.4 环境变量配置总结

#### Java 后端环境变量

```bash
# 可选配置（export 或 IDE 环境变量）
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/kaoyan_platform
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=123456
PYTHON_BACKEND_URL=http://localhost:8082
```

#### Python 后端环境变量

```bash
# 必须配置
GLM_API_KEY=your_api_key_here

# 可选配置
APP_PORT=8082
ALLOWED_ORIGINS=http://localhost:5173,http://localhost:8081
LOG_LEVEL=INFO
```

#### Vue 前端环境变量

```bash
# 开发环境（.env.development）
VITE_API_BASE_URL=http://localhost:8081/api

# 生产环境（.env.production）
VITE_API_BASE_URL=https://api.yourdomain.com/api
```

---

### 9.5 配置检查清单

#### 启动前检查

- [ ] **数据库**：MySQL 服务已启动，数据库 `kaoyan_platform` 已创建
- [ ] **Java 后端**：`application.yml` 中数据库连接信息正确
- [ ] **Python 后端**：`.env` 中 `GLM_API_KEY` 已配置
- [ ] **前端**：`api/common.js` 中 `baseURL` 正确

#### 生产环境检查

- [ ] 修改所有默认密码（数据库、API Key）
- [ ] 配置跨域允许的生产环境域名
- [ ] 启用 HTTPS
- [ ] 配置反向代理（Nginx）
- [ ] 设置日志级别为 `WARN` 或 `ERROR`

---

## 10. 环境搭建与部署指南

> 本章详细介绍如何从零开始搭建 KaoYanPlatform 的开发环境,以及如何部署到生产环境。

### 10.1 环境要求

#### 10.1.1 必需软件

| 软件 | 版本要求 | 用途 | 下载地址 |
|------|---------|------|---------|
| **JDK** | 17+ | Java 后端运行环境 | [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) 或 [OpenJDK](https://adoptium.net/) |
| **Maven** | 3.6+ | Java 项目构建工具 | [Maven 官网](https://maven.apache.org/download.cgi) |
| **MySQL** | 8.0+ | 数据库服务 | [MySQL 官网](https://dev.mysql.com/downloads/mysql/) |
| **Node.js** | 20.19.0+ 或 22.12.0+ | 前端运行环境 | [Node.js 官网](https://nodejs.org/) |
| **Python** | 3.10+ | Python AI 后端 | [Python 官网](https://www.python.org/downloads/) |
| **Git** | 最新版 | 版本控制 | [Git 官网](https://git-scm.com/downloads) |

#### 10.1.2 可选软件

| 软件 | 用途 | 说明 |
|------|------|------|
| **IntelliJ IDEA** | Java 开发 IDE | 推荐 Ultimate 版本 |
| **VS Code** | 前端/Python 开发 | 配合 Volar 插件 |
| **Navicat / DBeaver** | 数据库管理工具 | 可视化管理 MySQL |
| **Postman** | API 测试工具 | 测试后端接口 |

#### 10.1.3 第三方服务

| 服务 | 用途 | 获取方式 |
|------|------|---------|
| **智谱 AI API Key** | AI 图片识别和智能批改 | [智谱 AI 开放平台](https://open.bigmodel.cn/) |

---

### 10.2 快速启动指南

#### 10.2.1 数据库准备

**步骤 1**: 创建数据库

```sql
CREATE DATABASE kaoyan_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**步骤 2**: 导入数据表结构

项目提供两种方式:

**方式一: 使用 SQL 文件** (推荐)

```bash
# 找到项目根目录的 SQL 文件
cd KaoYanPlatform

# 导入数据库
mysql -u root -p kaoyan_platform < kaoyan_platform.sql.md
```

**方式二: 使用 MyBatis Plus 自动建表**

```yaml
# application.yml
spring:
  jpa:
    hibernate:
      ddl-auto: update  # 自动创建表结构
```

**步骤 3**: 验证数据库

```bash
mysql -u root -p
use kaoyan_platform;
show tables;  # 应该显示 16 张表
```

---

#### 10.2.2 Java 后端启动

**步骤 1**: 配置数据库连接

编辑 [java-back/src/main/resources/application.yml](java-back/src/main/resources/application.yml):

```yaml
spring:
  datasource:
    jdbc-url: jdbc:mysql://localhost:3306/kaoyan_platform?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8
    username: root
    password: 123456  # ⚠️ 修改为你的数据库密码

python:
  backend:
    url: http://localhost:8082  # Python 后端地址
    enabled: true  # 是否启用 AI 功能
```

**步骤 2**: 启动后端服务

**方式一: IDE 启动** (推荐用于开发)

```bash
# 在 IntelliJ IDEA 中
# 1. 打开 java-back 目录
# 2. 找到 KaoYanPlatformApplication.java
# 3. 右键 -> Run 'KaoYanPlatformApplication'
```

**方式二: Maven 命令行启动**

```bash
cd java-back

# 编译项目
mvn clean package -DskipTests

# 运行 JAR 包
java -jar target/java-back-0.0.1-SNAPSHOT.jar

# 或者直接使用 Maven 插件运行
mvn spring-boot:run
```

**步骤 3**: 验证服务

访问 [http://localhost:8081/doc.html](http://localhost:8081/doc.html) 应该看到 Knife4j API 文档界面。

---

#### 10.2.3 Python 后端启动

**步骤 1**: 创建虚拟环境

```bash
cd python-back

# Windows
python -m venv venv
venv\Scripts\activate

# macOS / Linux
python3 -m venv venv
source venv/bin/activate
```

**步骤 2**: 安装依赖

```bash
pip install -r requirements.txt

# 如果遇到 MinerU 安装问题,可以使用清华源
pip install -i https://pypi.tuna.tsinghua.edu.cn/simple -r requirements.txt
```

**步骤 3**: 配置环境变量

编辑 [python-back/.env](python-back/.env):

```env
# ⚠️ 必须配置: 替换为你的实际 API Key
GLM_API_KEY=your_api_key_here

# 可选配置
APP_PORT=8082
APP_HOST=0.0.0.0
GLM_MODEL_IMAGE=glm-4v-flash
GLM_MODEL_GRADING=glm-4-plus
ALLOWED_ORIGINS=http://localhost:5173,http://localhost:8081
LOG_LEVEL=INFO
```

**获取 API Key**:

1. 访问 [智谱 AI 开放平台](https://open.bigmodel.cn/)
2. 注册/登录账号
3. 进入"API Keys"页面
4. 创建新的 API Key

**步骤 4**: 启动服务

**方式一: 直接运行**

```bash
python app/main.py
```

**方式二: 使用启动脚本** (推荐)

```bash
python run_server.py
```

**步骤 5**: 验证服务

访问 [http://localhost:8082/docs](http://localhost:8082/docs) 应该看到 FastAPI Swagger UI。

**健康检查**:

```bash
curl http://localhost:8082/health

# 响应
{
  "status": "healthy",
  "service": "python-backend",
  "version": "1.0.0"
}
```

---

#### 10.2.4 Vue 前端启动

**步骤 1**: 安装依赖

```bash
cd vue-front

# 使用 npm
npm install

# 或使用 pnpm (更快)
pnpm install

# 或使用 yarn
yarn install
```

**步骤 2**: 配置 API 地址

编辑 [vue-front/src/api/common.js](vue-front/src/api/common.js):

```javascript
const request = axios.create({
  baseURL: 'http://localhost:8081/api',  // Java 后端地址
  timeout: 30000
})
```

**步骤 3**: 启动开发服务器

```bash
npm run dev
```

**步骤 4**: 访问应用

浏览器自动打开 [http://localhost:5173](http://localhost:5173)

**默认账号**:

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | `admin` | `123123` |
| 普通用户 | `student` | `123123` |

---

### 10.3 完整启动流程

#### 开发环境一键启动

**步骤 1**: 启动 MySQL

```bash
# Windows
net start mysql80

# macOS (Homebrew)
brew services start mysql

# Linux
sudo systemctl start mysql
```

**步骤 2**: 启动 Java 后端

```bash
cd java-back
mvn spring-boot:run
```

**步骤 3**: 启动 Python 后端

```bash
cd python-back
# 激活虚拟环境后
python run_server.py
```

**步骤 4**: 启动 Vue 前端

```bash
cd vue-front
npm run dev
```

**步骤 5**: 验证服务

```bash
# Java 后端
curl http://localhost:8081/doc.html

# Python 后端
curl http://localhost:8082/health

# 前端
# 浏览器访问 http://localhost:5173
```

---

### 10.4 生产环境部署

#### 10.4.1 数据库部署

**推荐配置**:

| 配置项 | 开发环境 | 生产环境 |
|--------|---------|---------|
| **MySQL 版本** | 8.0+ | 8.0+ (稳定版) |
| **字符集** | utf8mb4 | utf8mb4 |
| **最大连接数** | 151 | 500+ |
| **InnoDB 缓冲池** | 128M | 2G+ |
| **查询缓存** | 关闭 | 根据实际情况 |

**备份策略**:

```bash
# 每日自动备份脚本
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR=/backup/mysql
mysqldump -u root -p kaoyan_platform | gzip > ${BACKUP_DIR}/kaoyan_${DATE}.sql.gz
```

---

#### 10.4.2 Java 后端部署

**打包**:

```bash
cd java-back

# 打包为 JAR (跳过测试)
mvn clean package -DskipTests -Pprod

# 生成的文件
ls -lh target/*.jar
```

**生产配置** ([application-prod.yml](java-back/src/main/resources/application-prod.yml)):

```yaml
server:
  port: 8081

spring:
  datasource:
    jdbc-url: jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?serverTimezone=GMT%2B8
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl  # 生产环境使用 SLF4J

python:
  backend:
    url: ${PYTHON_BACKEND_URL}
    timeout: 60000  # 生产环境超时时间更长
    enabled: true

logging:
  level:
    root: WARN
    org.example.kaoyanplatform: INFO
  file:
    name: /var/log/kaoyan-platform/application.log
    max-size: 100MB
    max-history: 30
```

**部署方式一: 直接运行**

```bash
# 后台运行
nohup java -jar -Xms512m -Xmx2g \
  -Dspring.profiles.active=prod \
  target/java-back-0.0.1-SNAPSHOT.jar \
  > /dev/null 2>&1 &

# 查看日志
tail -f /var/log/kaoyan-platform/application.log
```

**部署方式二: Systemd 服务** (推荐)

创建 `/etc/systemd/system/kaoyan-java.service`:

```ini
[Unit]
Description=KaoYan Platform Java Backend
After=network.target mysql.service

[Service]
Type=simple
User=www-data
WorkingDirectory=/opt/kaoyan-platform/java-back
ExecStart=/usr/bin/java -jar -Xms512m -Xmx2g \
  -Dspring.profiles.active=prod \
  /opt/kaoyan-platform/java-back/java-back.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启动服务:

```bash
sudo systemctl daemon-reload
sudo systemctl start kaoyan-java
sudo systemctl enable kaoyan-java  # 开机自启
sudo systemctl status kaoyan-java
```

---

#### 10.4.3 Python 后端部署

**部署方式一: Uvicorn 直接运行**

```bash
cd python-back

# 后台运行
nohup uvicorn app.main:app \
  --host 0.0.0.0 \
  --port 8082 \
  --workers 4 \
  --log-level warning \
  > /var/log/kaoyan-platform/python-backend.log 2>&1 &
```

**部署方式二: Systemd 服务** (推荐)

创建 `/etc/systemd/system/kaoyan-python.service`:

```ini
[Unit]
Description=KaoYan Platform Python AI Backend
After=network.target

[Service]
Type=simple
User=www-data
WorkingDirectory=/opt/kaoyan-platform/python-back
Environment="PATH=/opt/kaoyan-platform/python-back/venv/bin"
ExecStart=/opt/kaoyan-platform/python-back/venv/bin/uvicorn app.main:app \
  --host 0.0.0.0 \
  --port 8082 \
  --workers 4 \
  --log-level warning
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启动服务:

```bash
sudo systemctl daemon-reload
sudo systemctl start kaoyan-python
sudo systemctl enable kaoyan-python
sudo systemctl status kaoyan-python
```

**部署方式三: Gunicorn + Nginx** (推荐生产环境)

```bash
# 安装 Gunicorn
pip install gunicorn

# 启动 Gunicorn
gunicorn app.main:app \
  --workers 4 \
  --worker-class uvicorn.workers.UvicornWorker \
  --bind 0.0.0.0:8082 \
  --access-logfile /var/log/kaoyan-platform/python-access.log \
  --error-logfile /var/log/kaoyan-platform/python-error.log
```

---

#### 10.4.4 Vue 前端部署

**打包**:

```bash
cd vue-front

# 生产环境打包
npm run build

# 生成的文件
ls -lh dist/
```

**部署方式一: Nginx 静态文件服务**

安装 Nginx:

```bash
# Ubuntu / Debian
sudo apt install nginx

# CentOS / RHEL
sudo yum install nginx
```

配置 Nginx (`/etc/nginx/sites-available/kaoyan-platform`):

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端静态文件
    root /opt/kaoyan-platform/vue-front/dist;
    index index.html;

    # Vue Router history 模式支持
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Java 后端代理
    location /api/ {
        proxy_pass http://localhost:8081/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Python 后端代理 (可选,如果需要直接访问)
    location /ai/ {
        proxy_pass http://localhost:8082/ai/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Gzip 压缩
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript
               application/x-javascript application/xml+rss
               application/json application/javascript;
}
```

启用配置:

```bash
sudo ln -s /etc/nginx/sites-available/kaoyan-platform /etc/nginx/sites-enabled/
sudo nginx -t  # 测试配置
sudo systemctl reload nginx
```

---

#### 10.4.5 HTTPS 配置

使用 Let's Encrypt 免费证书:

```bash
# 安装 Certbot
sudo apt install certbot python3-certbot-nginx

# 获取证书
sudo certbot --nginx -d your-domain.com

# 自动续期
sudo certbot renew --dry-run
```

---

### 10.5 Docker 部署 (可选)

#### 10.5.1 Docker Compose 配置

创建 `docker-compose.yml`:

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: kaoyan-mysql
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      MYSQL_DATABASE: kaoyan_platform
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./kaoyan_platform.sql.md:/docker-entrypoint-initdb.d/init.sql
    networks:
      - kaoyan-network

  java-backend:
    build: ./java-back
    container_name: kaoyan-java
    ports:
      - "8081:8081"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/kaoyan_platform?serverTimezone=GMT%2B8
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: 123456
      PYTHON_BACKEND_URL: http://python-backend:8082
    depends_on:
      - mysql
      - python-backend
    networks:
      - kaoyan-network

  python-backend:
    build: ./python-back
    container_name: kaoyan-python
    ports:
      - "8082:8082"
    environment:
      GLM_API_KEY: ${GLM_API_KEY}
      APP_PORT: 8082
    networks:
      - kaoyan-network

  frontend:
    build: ./vue-front
    container_name: kaoyan-frontend
    ports:
      - "80:80"
    depends_on:
      - java-backend
    networks:
      - kaoyan-network

volumes:
  mysql-data:

networks:
  kaoyan-network:
    driver: bridge
```

启动:

```bash
docker-compose up -d
```

---

### 10.6 部署检查清单

#### 启动前检查

- [ ] MySQL 服务已启动,数据库 `kaoyan_platform` 已创建
- [ ] Java 后端 `application.yml` 中数据库连接信息正确
- [ ] Python 后端 `.env` 中 `GLM_API_KEY` 已配置
- [ ] 前端 `api/common.js` 中 `baseURL` 正确

#### 生产环境检查

- [ ] 所有默认密码已修改(数据库、API Key)
- [ ] 跨域配置正确(CORS)
- [ ] HTTPS 已启用
- [ ] 日志级别设置为 `WARN` 或 `ERROR`
- [ ] 数据库备份策略已配置
- [ ] 防火墙规则已配置(仅开放必要端口)
- [ ] 监控和告警已配置

---

## 11. 开发规范与最佳实践

> 本章介绍项目的开发规范、Git 工作流以及 AI 集成规范,帮助团队保持代码质量和一致性。

### 11.1 前端开发规范

#### 11.1.1 命名规范

**文件命名**:

| 类型 | 规范 | 示例 |
|------|------|------|
| **组件文件** | PascalCase (大驼峰) | `UserProfile.vue`, `QuestionManage.vue` |
| **工具文件** | camelCase (小驼峰) | `request.js`, `katex-render.js` |
| **目录名** | kebab-case (短横线) | `user-profile/`, `quiz-module/` |

**变量命名**:

```javascript
// ✅ 好的命名
const questionList = ref([])
const isLoading = ref(false)
const handleAnswerSubmit = () => {}

// ❌ 避免的命名
const list = ref([])
const flag = ref(false)
const submit = () => {}
```

#### 11.1.2 Vue 3 组件规范

**组件结构顺序**:

```vue
<script setup>
// 1. 导入
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

// 2. Props 定义
const props = defineProps({
  questionId: Number,
  content: String
})

// 3. Emits 定义
const emit = defineEmits(['answer-change'])

// 4. 响应式数据
const loading = ref(false)
const answer = ref('')

// 5. 计算属性
const displayContent = computed(() => {
  return props.content
})

// 6. 方法
const handleSubmit = () => {
  emit('answer-change', answer.value)
}

// 7. 生命周期
onMounted(() => {
  loadData()
})
</script>

<template>
  <!-- 模板内容 -->
</template>

<style scoped>
/* 样式 */
</style>
```

**组件通信最佳实践**:

```javascript
// 1. Props 父传子 (只读数据)
const props = defineProps({
  question: Object,
  readonly: true
})

// 2. Emits 子传父 (事件触发)
const emit = defineEmits(['update', 'delete'])

// 3. Provide/Inject (跨层级)
// 父组件
provide('examConfig', { timeLimit: 7200, mode: 'exam' })

// 子组件
const config = inject('examConfig')

// 4. Pinia Store (全局状态)
import { useUserStore } from '@/stores/user'
const userStore = useUserStore()
```

#### 11.1.3 API 调用规范

**统一使用封装的 API 模块**:

```javascript
// ✅ 好的做法
import { getQuestionList, submitAnswer } from '@/api/question'

async function loadQuestions() {
  try {
    const response = await getQuestionList({ subjectId: 3 })
    questions.value = response.data
  } catch (error) {
    ElMessage.error('加载失败')
  }
}

// ❌ 避免直接使用 axios
async function loadQuestions() {
  await axios.get('/api/question/list')  // 不要这样做
}
```

**错误处理**:

```javascript
import { ElMessage } from 'element-plus'
import api from '@/api/request'

async function safeApiCall() {
  try {
    const response = await api.get('/some-endpoint')
    return response.data
  } catch (error) {
    // 统一错误处理已在拦截器中完成
    // 这里只处理特定逻辑
    if (error.response?.status === 401) {
      // 处理未授权
      router.push('/login')
    }
    return null
  }
}
```

#### 11.1.4 样式规范

**Scoped 样式**:

```vue
<style scoped>
/* 组件内样式,不会污染其他组件 */
.question-card {
  padding: 20px;
  background: #fff;
}

/* 使用 deep 修改子组件样式 */
:deep(.el-input__inner) {
  border-radius: 4px;
}
</style>
```

**CSS 变量使用**:

```vue
<script setup>
const themeColor = ref('#409EFF')
</script>

<template>
  <div :style="{ color: themeColor }">动态颜色</div>
</template>
```

---

### 11.2 Java 后端开发规范

#### 11.2.1 命名规范

**包命名**: 全小写,点分隔

```
org.example.kaoyanplatform.controller
org.example.kaoyanplatform.service.impl
org.example.kaoyanplatform.mapper
```

**类命名**:

| 类型 | 规范 | 示例 |
|------|------|------|
| **实体类** | PascalCase | `User.java`, `Question.java` |
| **Controller** | PascalCase + Controller 后缀 | `UserController.java` |
| **Service 接口** | PascalCase + Service 后缀 | `UserService.java` |
| **Service 实现** | PascalCase +ServiceImpl 后缀 | `UserServiceImpl.java` |
| **Mapper** | PascalCase + Mapper 后缀 | `UserMapper.java` |
| **DTO** | PascalCase + DTO 后缀 | `QuestionDTO.java` |

**方法命名**:

```java
// ✅ 好的命名
public List<Question> getQuestionsBySubjectId(Long subjectId) {}
public void updateUserProfile(UserProfileDTO dto) {}
public boolean checkAnswerCorrect(Long questionId, String userAnswer) {}

// ❌ 避免的命名
public List<Question> getData() {}
public void update(Object obj) {}
public boolean check(Long id, String ans) {}
```

#### 11.2.2 代码结构规范

**Controller 层规范**:

```java
@RestController
@RequestMapping("/api/question")
@Tag(name = "题目管理", description = "题目增删改查接口")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/list")
    @Operation(summary = "获取题目列表")
    public Result<List<QuestionDTO>> getQuestionList(
        @RequestParam Long subjectId
    ) {
        // 1. 参数校验
        if (subjectId == null) {
            return Result.error("科目ID不能为空");
        }

        // 2. 调用 Service
        List<QuestionDTO> questions = questionService.getListBySubject(subjectId);

        // 3. 返回结果
        return Result.success(questions);
    }

    @PostMapping("/add")
    @Operation(summary = "新增题目")
    public Result<String> addQuestion(
        @RequestBody @Valid QuestionImportDTO dto
    ) {
        // 参数校验
        questionService.addQuestion(dto);
        return Result.success("添加成功");
    }
}
```

**Service 层规范**:

```java
@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionSubjectRelService questionSubjectRelService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addQuestion(QuestionImportDTO dto) {
        // 1. 保存题目主体
        Question question = new Question();
        BeanUtils.copyProperties(dto, question);
        questionMapper.insert(question);

        // 2. 保存关联关系
        if (dto.getSubjectIds() != null) {
            questionSubjectRelService.saveRelations(
                question.getId(),
                dto.getSubjectIds()
            );
        }
    }

    @Override
    public List<QuestionDTO> getListBySubject(Long subjectId) {
        // 查询逻辑
        List<Question> questions = questionMapper.selectList(
            new LambdaQueryWrapper<Question>()
                .eq(Question::getSubjectId, subjectId)
        );

        // 转换为 DTO
        return questions.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
}
```

#### 11.2.3 异常处理规范

**统一异常处理** ([GlobalExceptionHandler.java](java-back/src/main/java/org/example/kaoyanplatform/handler/GlobalExceptionHandler.java)):

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error("系统繁忙,请稍后再试");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleValidationException(
        MethodArgumentNotValidException e
    ) {
        String message = e.getBindingResult().getAllErrors().get(0)
            .getDefaultMessage();
        return Result.error(message);
    }
}
```

**自定义业务异常**:

```java
public class BusinessException extends RuntimeException {
    private String code;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
}

// 使用
if (user == null) {
    throw new BusinessException("用户不存在");
}
```

#### 11.2.4 数据库操作规范

**使用 MyBatis Plus**:

```java
// ✅ 推荐: 使用 LambdaQueryWrapper
List<Question> questions = questionMapper.selectList(
    new LambdaQueryWrapper<Question>()
        .eq(Question::getType, 1)
        .eq(Question::getDifficulty, 3)
        .orderByDesc(Question::getCreateTime)
);

// ❌ 避免: 字符串硬编码
List<Question> questions = questionMapper.selectList(
    new QueryWrapper<Question>()
        .eq("type", 1)
        .eq("difficulty", 3)
);
```

**批量操作**:

```java
// 批量插入
@Service
public class BatchImportService {

    @Transactional(rollbackFor = Exception.class)
    public void batchInsert(List<Question> questions) {
        // 使用 MyBatis Plus saveBatch
        this.saveBatch(questions, 1000); // 每批1000条
    }
}
```

---

### 11.3 Python 后端开发规范

#### 11.3.1 代码风格

**遵循 PEP 8 规范**:

```python
# ✅ 好的代码
from fastapi import APIRouter, HTTPException
from pydantic import BaseModel

class QuestionRequest(BaseModel):
    """题目请求模型"""
    content: str
    type: int
    options: list[str] | None = None

# ✅ 类型注解
async def recognize_question(
    image_bytes: bytes,
    model: str = "glm-4v-flash"
) -> dict[str, Any]:
    """识别图片中的题目

    Args:
        image_bytes: 图片字节数据
        model: 使用的模型名称

    Returns:
        包含题目信息的字典
    """
    pass

# ❌ 避免
def recognize(q, model):
    # 缺少类型注解和文档
    pass
```

#### 11.3.2 异常处理

**统一异常格式**:

```python
from fastapi import HTTPException
from loguru import logger

@app.post("/ai/recognize")
async def recognize_question(file: UploadFile = File(...)):
    try:
        # 业务逻辑
        result = await image_service.recognize(image_bytes)
        return {"code": 200, "message": "识别成功", "data": result}

    except ValueError as e:
        logger.error(f"参数错误: {e}")
        raise HTTPException(status_code=400, detail=str(e))

    except Exception as e:
        logger.error(f"识别失败: {e}")
        raise HTTPException(status_code=500, detail="识别失败,请重试")
```

#### 11.3.3 配置管理

**使用 Pydantic Settings**:

```python
from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    """应用配置"""
    GLM_API_KEY: str
    GLM_MODEL_IMAGE: str = "glm-4v-flash"
    APP_PORT: int = 8082
    LOG_LEVEL: str = "INFO"

    class Config:
        env_file = ".env"

settings = Settings()

# 使用
api_key = settings.GLM_API_KEY
```

---

### 11.4 Git 工作流规范

#### 11.4.1 分支管理

**主要分支**:

| 分支 | 用途 | 稳定性 |
|------|------|--------|
| `main` | 生产环境代码 | 最稳定 |
| `dev` | 开发主分支 | 相对稳定 |
| `feature/*` | 功能开发分支 | 开发中 |
| `bugfix/*` | Bug 修复分支 | 开发中 |

**分支命名**:

```bash
# 功能分支
feature/question-import
feature/ai-grading
feature/user-dashboard

# 修复分支
bugfix/exam-timer-error
bugfix/pdf-export-issue

# 热修复分支 (从 main 分出)
hotfix/security-patch
```

#### 11.4.2 Commit 规范

**Commit Message 格式**:

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Type 类型**:

| Type | 说明 | 示例 |
|------|------|------|
| `feat` | 新功能 | `feat(question): 添加题目批量导入功能` |
| `fix` | Bug 修复 | `fix(exam): 修复考试计时器不准确问题` |
| `docs` | 文档更新 | `docs(readme): 更新环境搭建文档` |
| `style` | 代码格式 | `style(frontend): 统一代码缩进` |
| `refactor` | 重构 | `refactor(service): 重构题目查询逻辑` |
| `test` | 测试 | `test(ai): 添加图片识别单元测试` |
| `chore` | 构建/工具 | `chore(deps): 升级 Spring Boot 到 3.3.5` |

**示例 Commit**:

```bash
# 简单的 feat
git commit -m "feat(question): 添加题目批量导入功能"

# 带说明的 fix
git commit -m "fix(exam): 修复考试计时器不准确问题

- 修复时区转换问题
- 添加前端倒计时校验
- 优化后端时间戳处理

Closes #123"
```

#### 11.4.3 Code Review 规范

**PR 标题**:

```markdown
✅ 好的标题
feat(question): 添加题目批量导入功能支持 JSON 和 PDF 格式

❌ 不好的标题
添加功能
update
fix bug
```

**PR 描述模板**:

```markdown
## 变更说明
实现了题目批量导入功能,支持 JSON 和 PDF 两种格式。

## 变更类型
- [ ] feat 新功能
- [x] fix Bug 修复
- [ ] docs 文档更新
- [ ] refactor 重构

## 测试情况
- [x] 本地测试通过
- [ ] 单元测试通过
- [ ] 手动测试通过

## 相关 Issue
Closes #45

## 截图/演示
(如果有 UI 变更,添加截图或 GIF)

## Checklist
- [x] 遵循代码规范
- [x] 添加必要注释
- [x] 更新相关文档
```

---

### 11.5 AI 集成规范

#### 11.5.1 Prompt 编写规范

**图片识别 Prompt 模板**:

```python
IMAGE_RECOGNITION_PROMPT = """你是一个专业的考研题目识别助手。

【任务】识别图片中的题目,返回结构化 JSON

【题目类型】
1-单选, 2-多选, 3-填空, 4-简答

【LaTeX 公式规则】
- 数学符号用 LaTeX 包裹: $x^2$, $\\frac{a}{b}$
- 希腊字母: $\\alpha$, $\\beta$
- 上下标: $x_2$, $x^2$
- 积分极限: $\\int_{a}^{b}$

【返回格式】
{{
  "type": 题目类型(1-4),
  "content": "题目内容(数学公式用LaTeX)",
  "options": [
    {{"label": "A", "text": "选项内容"}},
    {{"label": "B", "text": "选项内容"}}
  ],
  "answer": "答案",
  "analysis": "解析",
  "tags": ["标签1", "标签2"],
  "difficulty": 3
}}

【注意事项】
- 公式必须使用 LaTeX 格式
- 确保选项标签为 A, B, C, D
- 解析要详细准确
"""
```

**智能批改 Prompt 模板**:

```python
GRADING_PROMPT = """你是一位资深的考研数学阅卷组长。

【题目】
{question_content}

【参考答案】
{reference_answer}

【用户答案】
{user_answer}

【评分要求】
1. 满分: 100 分
2. 按步骤给分,关注思路正确性
3. 即使最终答案错误,也要考虑过程分

【返回格式】
{{
  "score": 得分(0-100),
  "feedback": "详细评语",
  "strengths": ["优点1", "优点2"],
  "weaknesses": ["不足点1", "不足点2"],
  "suggestions": ["改进建议1", "改进建议2"]
}}
"""
```

#### 11.5.2 错误处理规范

**Python 服务降级**:

```python
@app.post("/ai/recognize")
async def recognize_question(file: UploadFile = File(...)):
    try:
        result = await image_service.recognize(image_bytes)
        return {"code": 200, "data": result}

    except TimeoutError:
        logger.error("AI 服务超时")
        raise HTTPException(
            status_code=504,
            detail="识别超时,请稍后重试"
        )

    except Exception as e:
        logger.error(f"AI 识别失败: {e}")
        raise HTTPException(
            status_code=500,
            detail="识别服务暂时不可用"
        )
```

**Java 服务调用 Python 降级**:

```java
@Service
public class QuestionService {

    @Value("${python.backend.enabled:true}")
    private boolean pythonEnabled;

    public QuestionDTO recognizeQuestion(MultipartFile file) {
        if (!pythonEnabled) {
            // Python 服务未启用,返回提示
            throw new BusinessException("AI 功能未启用");
        }

        try {
            return pythonBackendClient.recognizeQuestion(file);
        } catch (Exception e) {
            log.error("调用 Python 后端失败", e);
            throw new BusinessException("AI 识别失败,请稍后重试");
        }
    }
}
```

#### 11.5.3 成本控制

**API 调用频率限制**:

```python
from slowapi import Limiter
from slowapi.util import get_remote_address

limiter = Limiter(key_func=get_remote_address)

@app.post("/ai/recognize")
@limiter.limit("10/minute")  # 每分钟最多10次
async def recognize_question(
    request: Request,
    file: UploadFile = File(...)
):
    # 识别逻辑
    pass
```

**缓存策略**:

```python
from functools import lru_cache

@lru_cache(maxsize=100)
def get_cached_recognition(image_hash: str):
    """缓存识别结果"""
    pass
```

---

### 11.6 性能优化建议

#### 11.6.1 前端优化

**按需加载**:

```javascript
// 路由懒加载
const routes = [
  {
    path: '/admin/question-manage',
    component: () => import('@/views/admin/QuestionManage.vue')
  }
]
```

**虚拟滚动**:

```vue
<!-- 大列表使用虚拟滚动 -->
<template>
  <el-table-v2
    :columns="columns"
    :data="largeData"
    :width="700"
    :height="400"
    fixed
  />
</template>
```

#### 11.6.2 后端优化

**数据库查询优化**:

```java
// ✅ 使用分页
Page<Question> page = new Page<>(currentPage, pageSize);
questionMapper.selectPage(page, wrapper);

// ✅ 避免 N+1 查询
@TableName("question")
public class Question {
    @TableField(exist = false)  // 不查询关联字段
    private List<Subject> subjects;
}

// 使用连表查询或分别查询后组装
```

**缓存使用**:

```java
@Service
public class QuestionService {

    @Cacheable(value = "question", key = "#id")
    public Question getById(Long id) {
        return questionMapper.selectById(id);
    }

    @CacheEvict(value = "question", key = "#question.id")
    public void updateQuestion(Question question) {
        questionMapper.updateById(question);
    }
}
```

---

## 12. 常见问题与故障排查

> 本章汇总开发和使用过程中的常见问题及解决方案。

### 12.1 启动问题

#### 12.1.1 Java 后端启动失败

**问题 1: 端口被占用**

```
Error: Port 8081 was already in use.
```

**解决方案**:

```bash
# Windows
netstat -ano | findstr "8081"
taskkill /PID <进程ID> /F

# macOS / Linux
lsof -ti:8081 | xargs kill -9

# 或修改配置文件端口
# application.yml
server:
  port: 8082  # 改为其他端口
```

---

**问题 2: 数据库连接失败**

```
java.sql.SQLException: Access denied for user 'root'@'localhost'
```

**解决方案**:

1. 检查用户名密码是否正确:

```yaml
# application.yml
spring:
  datasource:
    username: root
    password: 你的实际密码
```

2. 确保 MySQL 服务已启动:

```bash
# Windows
net start mysql80

# macOS
brew services start mysql

# Linux
sudo systemctl start mysql
```

3. 测试连接:

```bash
mysql -u root -p
# 输入密码后应该能成功登录
```

---

**问题 3: Maven 依赖下载失败**

```
Failed to download artifact from repository
```

**解决方案**:

1. 配置国内镜像源 (`~/.m2/settings.xml`):

```xml
<mirrors>
  <mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <url>https://maven.aliyun.com/repository/public</url>
  </mirror>
</mirrors>
```

2. 清理缓存重新下载:

```bash
mvn clean install -U
```

---

#### 12.1.2 Python 后端启动失败

**问题 1: 虚拟环境激活失败**

```
venv\Scripts\activate : 无法加载文件
```

**解决方案 (Windows PowerShell)**:

```powershell
# 临时允许执行脚本
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope Process

# 然后再激活
venv\Scripts\activate
```

---

**问题 2: 依赖安装失败**

```
ERROR: Could not build wheels for mineru
```

**解决方案**:

1. 使用清华镜像源:

```bash
pip install -i https://pypi.tuna.tsinghua.edu.cn/simple -r requirements.txt
```

2. 某些包可能需要系统依赖:

```bash
# Ubuntu / Debian
sudo apt install python3-dev libxml2-dev libxslt-dev

# CentOS / RHEL
sudo yum install python3-devel libxml2-devel libxslt-devel
```

3. MinerU 安装问题:

```bash
# 可以先跳过 MinerU (仅用于 PDF 提取)
# 编辑 requirements.txt,注释掉 mineru 行
# 然后 pip install
```

---

**问题 3: API Key 未配置**

```
ValidationError: GLM_API_KEY is required
```

**解决方案**:

1. 创建 `.env` 文件:

```bash
cd python-back
cp .env.example .env  # 如果有示例文件
```

2. 编辑 `.env`:

```env
GLM_API_KEY=your_actual_api_key_here
```

3. 获取 API Key:
   - 访问 [智谱 AI 开放平台](https://open.bigmodel.cn/)
   - 注册/登录
   - 创建 API Key

---

#### 12.1.3 前端启动失败

**问题 1: Node.js 版本不兼容**

```
Error: Node.js version 20.19.0 or 22.12.0+ required
```

**解决方案**:

```bash
# 使用 nvm 安装正确版本
nvm install 20.19.0
nvm use 20.19.0

# 或者更新到最新版
nvm install node
```

---

**问题 2: 依赖安装失败**

```
npm ERR! code ERESOLVE
```

**解决方案**:

```bash
# 清理缓存
npm cache clean --force

# 删除 node_modules 重新安装
rm -rf node_modules package-lock.json
npm install

# 或使用 --legacy-peer-deps
npm install --legacy-peer-deps
```

---

**问题 3: Vite 启动端口被占用**

```
Error: Port 5173 is in use
```

**解决方案**:

```bash
# 方法1: 杀掉占用进程
# Windows
netstat -ano | findstr "5173"
taskkill /PID <进程ID> /F

# 方法2: 修改端口
# vite.config.js
export default defineConfig({
  server: {
    port: 3000  // 改为其他端口
  }
})
```

---

### 12.2 连接问题

#### 12.2.1 前端无法连接后端

**问题: CORS 跨域错误**

```
Access to XMLHttpRequest at 'http://localhost:8081/api'
from origin 'http://localhost:5173' has been blocked by CORS policy
```

**解决方案**:

1. 检查 Java 后端 CORS 配置 ([CorsConfig.java](java-back/src/main/java/org/example/kaoyanplatform/config/CorsConfig.java)):

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:5173")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
```

2. 检查前端 API 地址:

```javascript
// vue-front/src/api/common.js
const request = axios.create({
  baseURL: 'http://localhost:8081/api',  // 确保地址正确
  timeout: 30000
})
```

---

**问题: 网络请求超时**

```
Error: timeout of 30000ms exceeded
```

**解决方案**:

```javascript
// 增加超时时间
const request = axios.create({
  baseURL: 'http://localhost:8081/api',
  timeout: 60000  // 改为60秒
})
```

---

#### 12.2.2 Java 后端无法连接 Python 后端

**问题: 连接被拒绝**

```
Connection refused: http://localhost:8082
```

**解决方案**:

1. 检查 Python 后端是否启动:

```bash
curl http://localhost:8082/health
```

2. 检查 Java 配置:

```yaml
# application.yml
python:
  backend:
    url: http://localhost:8082  # 确保地址正确
    enabled: true
```

3. 检查防火墙设置

---

#### 12.2.3 数据库连接问题

**问题: 连接池耗尽**

```
HikariPool-1 - Connection is not available
```

**解决方案**:

```yaml
# application.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20  # 增加连接池大小
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
```

---

### 12.3 AI 服务问题

#### 12.3.1 图片识别失败

**问题 1: API 调用失败**

```
Error: GLM API request failed
```

**解决方案**:

1. 检查 API Key:

```bash
# 测试 API Key 是否有效
curl -X POST "https://open.bigmodel.cn/api/paas/v4/chat/completions" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{"model":"glm-4","messages":[{"role":"user","content":"hi"}]}'
```

2. 检查配额是否用尽:

```bash
# 登录智谱 AI 控制台查看
# https://open.bigmodel.cn/console/usage
```

---

**问题 2: 图片格式不支持**

```
Error: Unsupported image format
```

**解决方案**:

```python
# 前端限制上传格式
<el-upload
  accept="image/jpeg,image/png,image/jpg"
  :before-upload="beforeUpload"
>
</el-upload>

function beforeUpload(file) {
  const isImage = ['image/jpeg', 'image/png', 'image/jpg']
    .includes(file.type)
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传 JPG/PNG 格式!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!')
    return false
  }
  return true
}
```

---

**问题 3: LaTeX 公式渲染错误**

```
KaTeX parse error: Unexpected character
```

**解决方案**:

1. 检查公式格式:

```latex
<!-- ✅ 正确 -->
设函数 $f(x) = x^3 - 3x + 1$

<!-- ❌ 错误: 缺少闭合 $ -->
设函数 $f(x) = x^3 - 3x + 1
```

2. 转义特殊字符:

```latex
<!-- 需要转义的字符 -->
\{ \\{
\} \\}
\\ \\\\
# \#
$ \$
% \%
```

---

#### 12.3.2 智能批改问题

**问题: 批改结果不准确**

**解决方案**:

1. 优化 Prompt:

```python
# 在 prompt 中添加更多示例和约束
GRADING_PROMPT = """
【评分标准】
- 完全正确: 90-100 分
- 思路正确但有计算错误: 70-89 分
- 部分正确: 40-69 分
- 完全错误: 0-39 分

【示例】
题目: 计算 $\\lim_{x\\to 0} \\frac{\\sin x}{x}$
参考答案: 1
用户答案: 答案是1
评分: 85 分
反馈: 答案正确,但应该写出极限过程...
"""
```

2. 添加人工审核:

```java
// 对 AI 批改结果进行人工抽检
if (aiFeedback.getScore() < 60) {
    // 低分答案需要人工审核
    question.setNeedsReview(true);
}
```

---

### 12.4 性能问题

#### 12.4.1 前端页面卡顿

**问题: 大列表渲染卡顿**

**解决方案**:

```vue
<!-- 使用虚拟滚动 -->
<template>
  <el-table-v2
    :columns="columns"
    :data="questionList"
    :width="1200"
    :height="600"
    :estimated-row-height="80"
  />
</template>
```

---

**问题: 数学公式渲染慢**

**解决方案**:

```javascript
// 懒加载 KaTeX
import { defineAsyncComponent } from 'vue'

const MathRenderer = defineAsyncComponent(() =>
  import('@/components/MathRenderer.vue')
)

// 或者使用 CDN
// index.html
<script src="https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/katex.min.js"></script>
```

---

#### 12.4.2 后端响应慢

**问题: API 响应时间过长**

**解决方案**:

1. 添加数据库索引:

```sql
-- question 表
CREATE INDEX idx_type ON question(type);
CREATE INDEX idx_difficulty ON question(difficulty);

-- exam_session 表
CREATE INDEX idx_user_status ON exam_session(user_id, status);
```

2. 使用分页:

```java
// 避免 SELECT *
Page<Question> page = new Page<>(1, 20);  // 每页20条
questionMapper.selectPage(page, wrapper);
```

3. 启用缓存:

```java
@Cacheable(value = "subjects", key = "'tree'")
public List<Subject> getSubjectTree() {
    return subjectMapper.selectList(null);
}
```

---

#### 12.4.3 AI 服务响应慢

**问题: AI 识别/批改超时**

**解决方案**:

1. 增加超时时间:

```yaml
# application.yml
python:
  backend:
    timeout: 60000  # 60秒
```

2. 使用异步处理:

```java
@Async
public CompletableFuture<QuestionDTO> recognizeQuestionAsync(MultipartFile file) {
    QuestionDTO result = pythonBackendClient.recognizeQuestion(file);
    return CompletableFuture.completedFuture(result);
}

// 调用
CompletableFuture<QuestionDTO> future = questionService.recognizeQuestionAsync(file);
future.thenAccept(result -> {
    // 处理结果
});
```

---

### 12.5 部署问题

#### 12.5.1 Nginx 配置问题

**问题: 刷新页面 404**

**解决方案**:

```nginx
# Vue Router history 模式支持
location / {
    try_files $uri $uri/ /index.html;  # 添加这行
}
```

---

**问题: 静态资源 404**

**解决方案**:

```nginx
# 检查 root 路径是否正确
root /opt/kaoyan-platform/vue-front/dist;  # 绝对路径

# 或使用 alias
location /static {
    alias /opt/kaoyan-platform/vue-front/dist/static;
}
```

---

#### 12.5.2 Docker 部署问题

**问题: 容器无法连接数据库**

**解决方案**:

```yaml
# docker-compose.yml
services:
  java-backend:
    environment:
      # 使用服务名而不是 localhost
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/kaoyan_platform
    depends_on:
      - mysql  # 确保依赖关系
    networks:
      - kaoyan-network  # 确保在同一网络
```

---

### 12.6 调试技巧

#### 12.6.1 前端调试

**Vue DevTools**:

```bash
# 安装
npm install --save-dev vite-plugin-vue-devtools

# vite.config.js
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools()  // 添加插件
  ]
})
```

**Console 调试**:

```javascript
// 使用 console.table 查看对象数据
console.table(questionList)

// 使用 console.group 分组
console.group('题目加载')
console.log('总数:', total)
console.log('当前页:', currentPage)
console.groupEnd()
```

---

#### 12.6.2 后端调试

**日志输出**:

```java
@Slf4j
@Service
public class QuestionService {

    public Question getById(Long id) {
        log.debug("查询题目, ID={}", id);

        Question question = questionMapper.selectById(id);

        log.info("题目查询成功: {}", question);
        return question;
    }
}
```

**Knife4j API 文档**:

```
访问: http://localhost:8081/doc.html

功能:
- 在线测试接口
- 查看请求参数
- 查看响应格式
```

---

#### 12.6.3 Python 后端调试

**Loguru 日志**:

```python
from loguru import logger

logger.add("app.log", rotation="500 MB", compression="zip")

@app.post("/ai/recognize")
async def recognize_question(file: UploadFile = File(...)):
    logger.info(f"开始识别, 文件名: {file.filename}")
    logger.debug(f"文件大小: {file.size}")

    result = await service.recognize(image_bytes)

    logger.success(f"识别成功: {result}")
    return result
```

**FastAPI Swagger UI**:

```
访问: http://localhost:8082/docs

功能:
- 在线测试接口
- 查看请求/响应模型
- 下载 OpenAPI 规范
```

---

### 12.7 常用命令速查

#### Java 后端

```bash
# 编译打包
mvn clean package

# 跳过测试打包
mvn clean package -DskipTests

# 运行
mvn spring-boot:run

# 查看依赖树
mvn dependency:tree
```

#### Python 后端

```bash
# 激活虚拟环境
source venv/bin/activate  # Linux/macOS
venv\Scripts\activate     # Windows

# 安装依赖
pip install -r requirements.txt

# 运行
uvicorn app.main:app --reload

# 导出依赖
pip freeze > requirements.txt
```

#### Vue 前端

```bash
# 安装依赖
npm install

# 开发模式
npm run dev

# 生产打包
npm run build

# 预览打包结果
npm run preview

# 格式化代码
npm run format
```

---

**文档版本**: v1.1
**最后更新**: 2026-01-26
**最后更新**: 2026-01-26
**维护者**: KaoYanPlatform 开发团队