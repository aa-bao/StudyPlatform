# 在线学习平台

> 一款面向考研学生的智能刷题与学习管理平台

[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-green)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.5.26-brightgreen)](https://vuejs.org/)
[![Python](https://img.shields.io/badge/Python-3.10-blue)](https://www.python.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## ✨ 项目简介

KaoYanPlatform 是一款功能完整的考研刷题平台，集成 **AI 智能批改**、**图片识别**、**学习数据分析** 等先进功能，提供完整的考研学习解决方案。

### 核心功能

- 🤖 **AI 智能批改** - GLM-4.7 主观题自动评分与反馈
- 📸 **AI 图片识别** - GLM-4.6V 自动识别题目，支持 LaTeX 数学公式
- 📚 **智能题库管理** - 支持批量导入导出、题目分类管理
- 📊 **学习数据分析** - ECharts 可视化展示学习进度
- 🎯 **多模式刷题** - 逐题精练、专项突破、真题模考、套卷刷题
- 📝 **考试系统** - 倒计时持久化、答题快照恢复、切屏检测

---

## 🛠 技术栈

### 前端
- **Vue 3.5** + **Vite 7.3** + **Pinia** + **Vue Router**
- **Element Plus 2.13** - UI 组件库
- **ECharts 6.0** - 数据可视化
- **KaTeX 0.16** - 数学公式渲染

### 后端
- **Java**
  - Spring Boot 3.3.5 + Spring Security
  - MyBatis Plus 3.5.5 + MySQL 8.0
  - Knife4j 4.5.0 (API 文档)
  - Flying Saucer (PDF 导出)

- **Python** (可选)
  - FastAPI 0.115.0
  - 智谱 GLM-4 / DeepSeek-v1
  - MinerU (PDF 解析) + PaddleOCR

---

## 🚀 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Node.js 16+
- Python 3.10+ (可选)

### 安装步骤

```bash
# 1. 克隆项目
git clone https://github.com/your-username/KaoYanPlatform.git
cd KaoYanPlatform

# 2. 初始化数据库
mysql -u root -p
CREATE DATABASE kaoyan_platform CHARACTER SET utf8mb4;
mysql -u root -p kaoyan_platform < kaoyan_platform.sql

# 3. 启动 Java 后端
cd java-back
# 修改 application.yml 中的数据库配置
mvn spring-boot:run

# 4. 启动 Vue 前端
cd vue-front
npm install
npm run dev

# 5. (可选) 启动 Python 后端
cd python-back
pip install -r requirements.txt
python run_server.py
```

### 访问地址

- 前端：http://localhost:5173
- 后端：http://localhost:8081
- API 文档：http://localhost:8081/doc.html
- Python 服务：http://localhost:8082

### 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123123 |
| 普通用户 | student | 123123 |

---

## 📂 项目结构

```
KaoYanPlatform/
├── java-back/          # Java 后端 (8081)
├── vue-front/          # Vue 前端 (5173)
├── python-back/        # Python 服务 (8082)
├── docs/               # 项目文档
├── PROJECT_README.md   # 完整技术文档
└── README.md           # 本文件
```

---

## 🎯 核心特性

### AI 图片识别
- 完全免费的 GLM-4.6V-Flash 模型
- 自动识别题目类型、选项、答案、解析
- 支持 LaTeX 数学公式识别
- 自动生成难度等级

### LaTeX 全链路支持
从图片识别 → 存储 → 前端渲染 → PDF 导出，全链路支持数学公式

```latex
# 行内公式
设函数 $f(x) = x^3 - 3x + 1$，求 $f'(x)$

# 块级公式
答案：$$3x^2-3$$
```

### 数据库优化设计
采用 **JSON 字段** 设计，将题目内容、选项、答案、解析整合为单一 JSON 字段，减少冗余，提高灵活性。

### 多层级科目体系
支持 4 层级树形结构：考试科目 → 知识模块 → 知识点 → 题型

---

## 📸 功能截图

> （此处可添加项目截图）

---

## 📖 文档

完整技术文档请查看：[PROJECT_README.md](PROJECT_README.md)

包含：
- 详细技术架构说明
- API 接口文档
- 数据库设计详解
- 配置说明
- 开发规范
- 常见问题排查

---

## 🔄 更新日志

### v2.0.0 (2025-01-26)

- ✨ 数据库结构优化（JSON 字段）
- ✨ AI 识别升级（GLM-4.6V-Flash 完全免费）
- ✨ LaTeX 全链路支持
- ✨ 收藏夹系统
- ✨ 习题册与试卷双模式管理

### v1.0.0 (2024-12-01)

- 🎉 基础题库管理功能
- 📦 JSON 批量导入导出
- 📄 PDF 导出
- 🤖 AI 智能批改
- 📊 学习数据分析

---

## 📄 许可证

[MIT License](LICENSE)

---

## ⭐ Star History

如果这个项目对你有帮助，请给一个 Star！

---

## 📮 联系方式

- 问题反馈：[Issues](https://github.com/your-username/KaoYanPlatform/issues)
- 技术交流：欢迎提交 Pull Request
