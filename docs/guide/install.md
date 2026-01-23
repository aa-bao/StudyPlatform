# 安装与部署指南

## 环境要求

| 环境     | 要求版本 |
| -------- | -------- |
| JDK      | 17+      |
| Maven    | 3.6+     |
| MySQL    | 8.0+     |
| Node.js  | 16+      |
| 磁盘空间 | ≥ 1GB    |

## 数据库初始化

```sql
-- 1. 创建数据库
CREATE DATABASE kaoyan_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 2. 导入表结构和初始数据
USE kaoyan_platform;
SOURCE sql/schema.sql;
```

**数据库表结构概览**:
- `tb_user` - 用户表
- `tb_question` - 题目表
- `tb_subject` - 科目表
- `tb_book` - 习题册表
- `tb_exam_record` - 答题记录表
- `tb_mistake_record` - 错题记录表
- `tb_collection` - 收藏表
- `tb_user_progress` - 用户进度表
- `map_question_book` - 题目-习题册映射表
- `map_question_subject` - 题目-科目映射表
- `map_subject_book` - 习题册-科目映射表

## 后端启动 (java-back)

### 1. 进入后端目录

```bash
cd KaoYanPlatform
```

### 2. 修改配置文件（可选）

编辑 `src/main/resources/application.yml`，配置数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/kaoyan_platform?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### 3. 启动项目

```bash
# 使用 Maven 启动
mvn spring-boot:run

# 或在 IDE 中运行主类
# KaoYanPlatform/src/main/java/org/example/kaoyanplatform/KaoyanplatformApplication.java
```

### 4. 访问后端服务

后端启动后访问：
- API 地址: http://localhost:8081
- Swagger 文档: http://localhost:8081/doc.html

## 前端启动 (vue-front)

### 1. 进入前端目录

```bash
cd vue-front
```

### 2. 安装依赖

```bash
npm install
```

### 3. 启动开发服务器

```bash
npm run dev
```

### 4. 访问前端应用

前端启动后访问: http://localhost:5173

### 5. 构建生产版本（可选）

```bash
npm run build
```

## Python 工具使用 (python-back)

> **说明**: python-back 是数据处理工具集，不是 Web 服务，无需启动。

### 1. 进入工具目录

```bash
cd python-back
```

### 2. 安装依赖（按需）

```bash
pip install openai PyPDF2 pdfplumber paddleocr zhipuai
```

### 3. 使用工具

```bash
# PDF 提取示例
python tools/PDFExtract/PDFExtract_mineru.py

# Markdown 转 JSON 示例
python tools/MDToJson/MDToJSONConverter.py
```

**工具说明**：
- `tools/MDToJson/` - Markdown 题目转 JSON 工具（使用 DeepSeek API）
- `tools/PDFExtract/` - PDF 题目提取工具（支持 MinerU）

## 默认测试账号

| 角色     | 用户名  | 密码     |
| -------- | ------- | -------- |
| 管理员   | admin   | 123123   |
| 普通用户 | student | 123123   |

## 项目结构概览

### java-back/ 后端服务结构

```
java-back/
├── src/main/java/org/example/kaoyanplatform/
│   ├── common/                # 通用模块
│   ├── config/                # 配置类
│   ├── controller/            # API 接口层
│   ├── entity/                # 数据库实体
│   ├── service/               # 业务逻辑层
│   └── mapper/                # 数据访问层
└── src/main/resources/
    ├── application.yml         # 应用配置
    └── templates/              # Thymeleaf模板
```

### vue-front/ 前端应用结构

```
vue-front/
├── src/
│   ├── api/                   # API 调用封装
│   ├── assets/                # 静态资源
│   ├── components/            # 公共组件
│   ├── router/                # 路由配置
│   ├── stores/                # Pinia 状态管理
│   └── views/                 # 页面组件
├── package.json
└── vite.config.js
```

### python-back/ 数据处理工具结构

```
python-back/
└── tools/
    ├── MDToJson/              # Markdown 转 JSON 工具
    └── PDFExtract/            # PDF 提取工具
```

## 常见问题

### 后端启动失败

1. 检查 JDK 版本是否为 17+
2. 检查 MySQL 是否已启动
3. 检查数据库连接配置是否正确

### 前端启动失败

1. 检查 Node.js 版本是否为 16+
2. 删除 `node_modules` 后重新 `npm install`
3. 检查后端服务是否已启动（端口 8081）

### 跨域问题

如果遇到跨域问题，检查后端 `CorsConfig.java` 配置是否正确。
