# KaoYanPlatform 考研刷题平台

一款面向考研学生的在线刷题与学习管理平台，采用现代化的前后端分离架构设计。

## 项目简介

KaoYanPlatform 提供完整的考研学习解决方案，包括：

- **多模式刷题**: 逐题精练、专项突破、真题模考、套卷刷题
- **AI 智能批改**: 集成智谱 GLM-4.7，主观题自动评分与反馈
- **智能题库管理**: 支持 PDF/Markdown 题目智能提取、批量导入导出
- **学习数据分析**: ECharts 雷达图可视化展示学习进度
- **错题本系统**: 自动收藏错题、标签分类、重做巩固

## 核心亮点

| 特性 | 说明 |
|------|------|
| **AI 智能批改** | 主观题使用 GLM-4.7 自动批改，提供详细反馈 |
| **题目智能提取** | 基于 LLM 的 PDF 题目结构化提取，支持复杂格式 |
| **多层级科目体系** | 具体考试科目 → 知识模块 → 知识点 → 题型，支持 scope 多对多映射 |
| **考试会话增强** | 倒计时持久化、浏览器返回阻止、切屏检测、答题快照恢复 |
| **未完成考试提醒** | 智能检测未完成考试，强制弹窗提醒继续 |
| **批量导入导出** | 支持 JSON 格式批量导入，PDF 格式导出（支持 LaTeX 公式） |

## 技术栈

### 后端

- **框架**: Spring Boot 3.3.5 + Spring Security
- **数据层**: MyBatis Plus 3.5.5 + MySQL 8.0
- **AI 集成**: 智谱 AI SDK (GLM-4.7)
- **API 文档**: Knife4j 4.5.0
- **PDF 生成**: Flying Saucer + Thymeleaf

### 前端

- **框架**: Vue.js 3.5.26 + Vite 7.3.0
- **状态管理**: Pinia 3.0.4
- **路由**: Vue Router 4.6.4
- **UI 组件**: Element Plus 2.13.0
- **图表**: ECharts 6.0.0
- **数学公式**: KaTeX 0.16.27

### Python 工具集

- **AI 模型**: DeepSeek-v1, 智谱 GLM-4
- **PDF 处理**: MinerU, PaddleOCR

## 快速启动

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Node.js 16+

### 后端启动

```bash
cd KaoYanPlatform
mvn spring-boot:run
```

访问: http://localhost:8081 | Swagger: http://localhost:8081/doc.html

### 前端启动

```bash
cd vue-front
npm install
npm run dev
```

访问: http://localhost:5173

### 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123123 |
| 普通用户 | student | 123123 |

## 文档导航

### 安装与部署

- [安装与部署指南](docs/guide/install.md) - 环境配置、数据库初始化、项目启动

### 架构设计

- [项目架构设计](docs/architecture/design.md) - 项目亮点、技术栈、数据模型、AI 集成

### 功能模块

- [核心功能模块](docs/features/modules.md) - 题目管理、套卷刷题、批量导入导出、未完成考试提醒

### 实现细节

- [前端架构与核心实现](docs/features/implementation-details.md) - 路由权限、状态管理、树形结构、拖拽排序

## 项目结构

```
KaoYanPlatform/
├── java-back/          # Java 后端服务 (端口: 8081)
├── vue-front/          # Vue 前端应用 (端口: 5173)
├── python-back/        # Python 数据处理工具集
├── docs/               # 项目文档
│   ├── guide/          # 安装指南
│   ├── architecture/   # 架构设计
│   └── features/       # 功能模块
└── README.md
```
