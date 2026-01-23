# 项目架构设计

## 项目亮点

### 核心特性

- **前后端分离架构**: Vue 3 + Spring Boot 3，独立部署，易于扩展
- **AI 智能批改**: 集成智谱 GLM-4.7，主观题自动评分与反馈
- **题目智能提取**: 基于 LLM 的 PDF/Markdown 题目结构化提取
- **多层级科目体系**: 支持具体考试科目 → 知识模块 → 知识点 → 题型的灵活分类
- **虚拟分组机制**: 自动创建英语、数学、408 分组，优化展示
- **Scope 多对多映射**: 同一知识点可属于多个具体考试科目（如高数属于数一/数二/数三）
- **套卷刷题增强**: 倒计时持久化、浏览器返回阻止、切屏检测、答题快照恢复
- **未完成考试强制提醒**: 智能检测未完成考试，强制弹窗提醒继续
- **题目批量导入/导出**: 支持 JSON 格式批量导入，PDF 格式导出

### 技术难点

1. **复杂的多对多关系**: 题目-习题册-科目三者的多对多关联
2. **树形结构处理**: 递归构建科目树、拖拽排序、scope 动态过滤
3. **考试会话管理**: 倒计时持久化、状态恢复、异常处理
4. **AI 集成**: 批改重试机制、超时处理、结果解析
5. **前端状态同步**: localStorage 与后端数据一致性

## 技术栈

### Java 后端 (java-back)

#### 核心框架

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.3.5 | 核心框架 |
| Spring MVC | - | Web 框架 |
| Spring Security | - | 安全框架 |

#### 数据持久层

| 技术 | 版本 | 用途 |
|------|------|------|
| MyBatis Plus | 3.5.5 | ORM 框架 |
| MySQL | 8.0+ | 主数据库 |
| HikariCP | - | 连接池 |

#### API 文档与工具

| 技术 | 版本 | 用途 |
|------|------|------|
| Knife4j | 4.5.0 | Swagger 增强 |
| Lombok | - | 简化代码 |
| Hutool | 5.8.25 | 工具库 |

#### PDF 生成

| 技术 | 版本 | 用途 |
|------|------|------|
| Flying Saucer | 9.1.22 | XHTML 转 PDF |
| Thymeleaf | - | 模板引擎 |

#### AI 集成

| 技术 | 版本 | 用途 |
|------|------|------|
| 智谱 AI SDK | V4-2.0.2 | GLM 系列大模型 |
| GLM-4-Flash | - | 轻量级模型 |

### 前端 (vue-front)

#### 核心框架

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue.js | 3.5.26 | 渐进式框架 |
| Vite | 7.3.0 | 构建工具 |
| Pinia | 3.0.4 | 状态管理 |
| Vue Router | 4.6.4 | 路由管理 |

#### UI 组件与样式

| 技术 | 版本 | 用途 |
|------|------|------|
| Element Plus | 2.13.0 | UI 组件库 |
| KaTeX | 0.16.27 | 数学公式渲染 |
| ECharts | 6.0.0 | 数据可视化 |

#### 专业功能库

| 技术 | 版本 | 用途 |
|------|------|------|
| XLSX | 0.18.5 | Excel 导入导出 |
| Sortable.js | 1.15.6 | 拖拽排序 |

### Python 数据处理 (python-back)

> python-back 是工具集，非 Web 服务

| 技术 | 用途 |
|------|------|
| DeepSeek-v1 | Markdown 转 JSON |
| 智谱 GLM-4 | 题目智能提取 |
| MinerU | PDF 解析 |
| PaddleOCR | OCR 文字识别 |

## 项目架构

### 模块说明

1. **java-back** - Java 后端服务 (端口: 8081)
   - 用户认证、业务逻辑、数据持久化
   - 直接集成智谱 AI SDK 实现 AI 批改

2. **vue-front** - Vue 前端应用 (端口: 5173)
   - 用户界面、刷题交互、数据可视化
   - 单页应用，响应式设计

3. **python-back** - Python 数据处理工具集
   - PDF 题目提取、Markdown 转 JSON
   - 离线工具和辅助脚本

### 架构图

```
┌────────────────────────────────────────────────────┐
│              前端层 (Vue 3)                         │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐        │
│  │ 用户端   │  │ 管理端   │  │ 刷题模块 │        │
│  └──────────┘  └──────────┘  └──────────┘        │
│     Element Plus + Pinia + Vue Router              │
└────────────────────┬───────────────────────────────┘
                     │ HTTP/REST API
                     ↓
┌────────────────────────────────────────────────────┐
│          后端服务层 (Spring Boot 3.3.5)            │
│  Controller → Service → Mapper → MySQL            │
│                      ↓                              │
│              AI 服务调用 (智谱 SDK)                │
└────────────────────┬───────────────────────────────┘
                     ↓
┌────────────────────────────────────────────────────┐
│           工具集 (Python)                          │
│  PDF Extractor  │  MD to JSON Converter           │
└────────────────────────────────────────────────────┘
```

## 数据模型设计

### 核心业务表

#### 用户表 (tb_user)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, AUTO | 主键 |
| username | varchar(50) | NOT NULL, UNIQUE | 用户名/账号 |
| password | varchar(100) | NOT NULL | 密码（加密） |
| phone | varchar(15) | | 手机号 |
| email | varchar(255) | | 邮箱 |
| nickname | varchar(10) | | 昵称 |
| role | varchar(10) | DEFAULT 'student' | 角色: admin/student |
| avatar | varchar(255) | | 头像 URL |
| target_school | varchar(10) | | 目标院校 |
| target_total_score | smallint | DEFAULT 0 | 目标总分 |
| exam_year | varchar(50) | | 考研年份（如：27考研） |
| exam_subjects | varchar(255) | | 公共课 |
| create_time | datetime | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### 题目表 (tb_question)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, AUTO | 主键 |
| type | tinyint | NOT NULL | 题目类型 (1-10) |
| content | text | NOT NULL | 题干内容（支持 LaTeX） |
| options | json | | 选项：["A.xx", "B.xx", ...] |
| answer | text | NOT NULL | 正确答案 |
| analysis | text | | 解析（支持 LaTeX） |
| difficulty | tinyint | DEFAULT 1 | 难度：1-5 |
| create_time | datetime | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| tags | json | | 题目标签：["tag1", "tag2"] |
| source | varchar(100) | | 题目来源/习题集名称 |

**题目类型**:
- 1: 单选题 | 2: 多选题 | 3: 填空题 | 4: 综合应用题（408、数学、政治的大题）
- 5: 完型填空 | 6: 阅读理解 | 7: 新题型
- 8: 翻译题 | 9: 小作文 | 10: 大作文

#### 科目分类表 (tb_subject)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | int | PK, AUTO | 主键 |
| name | varchar(50) | NOT NULL | 科目名称 |
| parent_id | int | DEFAULT 0 | 父级 ID（0=根节点） |
| icon | varchar(100) | | 图标 |
| sort | tinyint | DEFAULT 0 | 排序号（值越小越靠前） |
| level | tinyint | DEFAULT 1 | 层级：1-具体考试科目, 2-知识模块, 3-知识点, 4-题型, 5-解题技巧 |
| question_count | int | DEFAULT 0 | 题目数量 |
| scope | varchar(50) | DEFAULT '1,2,3' | 适用范围：数一/数二/数三 (逗号隔开) |
| question_types | varchar(100) | | 支持的题型列表，逗号分隔 |

**多层级结构说明**:

- **Level 1 (具体考试科目)**: 具体考试科目/根节点
  - 政治(id=1)、英语一(id=2)、英语二(id=3)、数学一(id=4)、数学二(id=5)、数学三(id=6)、408(id=7)

- **Level 2 (知识模块)**: 知识模块（可跨具体考试科目）
  - 示例：马原(parent_id=1)、毛中特(parent_id=1)、高等数学(parent_id=0)、线性代数(parent_id=0)
  - 两种归属方式：
    - **传统父子关系**: `parent_id = 具体考试科目ID`
    - **Scope 多对多关系**: `parent_id = 0`, `scope = "2,3"`（如完形填空属于英语一、二）

- **Level 3 (知识点)**: 知识点/章节
  - 示例：函数与极限、行列式、矩阵

- **Level 4 (题型)**: 题型/解题方法
  - 示例：泰勒公式、洛必达法则

**scope 字段使用示例**:
- `scope = "4,5,6"` → 属于数学一、数学二、数学三
- `scope = "4,6"` → 属于数学一、数学三（**不包括数学二**）
- `scope = "2,3"` → 属于英语一、英语二

#### 习题册表 (tb_book)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | int | PK, AUTO | 主键 |
| name | varchar(100) | NOT NULL | 习题册名称 |
| description | varchar(255) | | 简介 |
| create_time | datetime | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

#### 收藏表 (tb_collection)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, AUTO | 主键 |
| user_id | bigint | NOT NULL | 用户 ID |
| question_id | bigint | NOT NULL | 题目 ID |
| create_time | datetime | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| tags | json | | 自定义标签：["重点", "易错"] |

#### 答题记录表 (tb_exam_record)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, AUTO | 主键 |
| user_id | bigint | NOT NULL | 用户 ID |
| question_id | bigint | NOT NULL | 题目 ID |
| user_answer | text | | 用户提交的答案 |
| is_correct | tinyint(1) | | 是否正确：0-错, 1-对 |
| score | int | DEFAULT 0 | 得分 |
| create_time | datetime | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| duration | int | DEFAULT 0 | 答题用时（秒） |

#### 错题记录表 (tb_mistake_record)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | int | PK, AUTO | 主键 |
| user_id | int | NOT NULL | 用户 ID |
| question_id | int | NOT NULL | 题目 ID |
| create_time | datetime | DEFAULT CURRENT_TIMESTAMP | 首次加入错题本时间 |
| update_time | datetime | | 最近一次答错时间 |
| error_count | int | | 累计答错次数 |

#### 用户学习进度表 (tb_user_progress)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, AUTO | 主键 |
| user_id | bigint | NOT NULL | 用户 ID |
| subject_id | int | NOT NULL | 科目或考点 ID |
| finished_count | int | DEFAULT 0 | 该考点下已做题目数 |
| correct_count | int | DEFAULT 0 | 该考点下做对题目数 |
| update_time | datetime | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 套卷刷题相关表

#### 试卷主表 (tb_paper)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | varchar(36) | PK, UUID | 主键 |
| title | varchar(255) | NOT NULL | 试卷标题 |
| year | varchar(10) | | 考试年份 |
| exam_spec_id | varchar(36) | | 具体考试科目 ID |
| total_score | int | DEFAULT 150 | 总分 |
| time_limit | int | DEFAULT 180 | 时间限制（分钟） |
| paper_type | tinyint | | 试卷类型：0-真题, 1-模拟卷 |
| structure_json | text | | 试卷结构 |
| create_time | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

#### 考试会话表 (tb_exam_session)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | varchar(36) | PK, UUID | 主键 |
| user_id | varchar(36) | NOT NULL | 用户 ID |
| paper_id | varchar(36) | NOT NULL | 试卷 ID |
| status | tinyint | DEFAULT 0 | 状态：0-进行中, 1-已完成 |
| start_time | datetime | | 开始时间 |
| submit_time | timestamp | | 提交时间 |
| expected_end_time | datetime | | 预期结束时间 = create_time + paper.time_limit |
| total_score | decimal(5,2) | | 总分 |
| switch_count | int | DEFAULT 0 | 切换题目次数（切屏检测） |
| ai_summary | text | | AI 总结 |
| snapshot_answers | json | | 答题快照 JSON |
| create_time | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

#### 答题明细表 (tb_exam_answer_detail)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | varchar(36) | PK, UUID | 主键 |
| session_id | varchar(36) | FK | 考试会话 ID |
| question_id | varchar(36) | FK | 题目 ID |
| user_answer | text | | 用户答案 |
| is_correct | tinyint | | 是否正确：0-错, 1-对, 2-待定（主观题） |
| score_earned | decimal(5,2) | | 得分率 |
| duration_seconds | int | | 答题时长（秒） |
| ai_feedback | text | | AI 反馈 |
| knowledge_point_id | varchar(36) | | 知识点 ID |
| create_time | datetime | | 创建时间 |

### 映射表（多对多关系）

#### 题目-习题册关联表 (map_question_book)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, AUTO | 主键 |
| question_id | bigint | FK | 题目 ID |
| book_id | int | FK | 习题册 ID |

**索引**: `uk_question_book` (question_id, book_id) 唯一索引

#### 题目-科目关联表 (map_question_subject)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, AUTO | 主键 |
| question_id | bigint | FK | 题目 ID |
| subject_id | int | FK | 科目 ID 或知识点 ID |

**索引**: `uk_question_subject` (question_id, subject_id) 唯一索引

#### 习题册-科目关联表 (map_subject_book)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | int | PK, AUTO | 主键 |
| book_id | int | FK | 习题册 ID |
| subject_id | int | FK | 科目 ID |

**索引**: `uk_book_subject` (book_id, subject_id) 唯一索引

#### 试卷-题目关联表 (map_paper_question)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | varchar(36) | PK, UUID | 主键 |
| paper_id | varchar(36) | FK | 试卷 ID |
| question_id | varchar(36) | FK | 题目 ID |
| sort_order | smallint | | 题目顺序 |
| score_value | decimal(5,2) | | 题目分值 |
| type | varchar(50) | | 题目类型 |

#### 科目权重映射表 (map_subject_weight)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, AUTO | 主键 |
| subject_id | int | FK | 科目 ID |
| weight | decimal | | 权重值 |

### 其他表

#### 学习资料表 (tb_resource)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, AUTO | 主键 |
| title | varchar(100) | NOT NULL | 资料标题 |
| url | varchar(255) | NOT NULL | 下载/预览地址 |
| file_type | varchar(20) | DEFAULT 'pdf' | 文件类型 |
| subject_id | int | | 科目 ID |
| create_time | datetime | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

### 映射表关系链

```
题目 → map_question_book → 习题册
题目 → map_question_subject → 科目/知识点
习题册 → map_subject_book → 科目/知识点
试卷 → map_paper_question → 题目
```

## AI 服务集成

### 题目智能提取流程

```
1. Java 后端接收请求
   QuestionController.importFromMarkdown()
   ↓
2. 调用 Python AI 服务
   RestTemplate.postForObject("/api/v1/questions/extract")
   ↓
3. Python AI 服务处理
   FastAPI → PromptService → GLM-4 解析
   ↓
4. 返回结构化 JSON
   QuestionImportDTO → 批量保存到数据库
```

### AI 批改流程

```
1. 学生提交试卷
   ExamSessionController.submitExam()
   ↓
2. 客观题自动判分
   单选/多选题 → 自动比对答案
   ↓
3. 主观题 AI 批改
   GLMService.gradeAnswer()
   构建批改 Prompt → 调用 GLM-4.7
   指数退避重试 (最大3次)
   ↓
4. 生成成绩报告
   汇总得分 → 保存考试记录
```

## 业务流程

### 学生端核心流程

```
用户登录 → 学习仪表盘 → 选择科目 → 选择刷题模式
    ↓
[逐题精练/专项突破]: 答题 → 查看解析 → 收藏/标记
[套卷刷题]: 倒计时 → 答题(支持标记/跳过) → 答题卡导航
    ↓
提交后反馈: 客观题自动判分 → 主观题AI批改 → 成绩报告
    ↓
错题本复习: 按科目/标签筛选 → 重做错题 → 巩固练习
```

### 管理员核心流程

```
管理员登录 → 数据看板 → 题库管理 → 习题册管理
    ↓
题目智能提取: AI提取 → 数据清洗 → 格式化验证
    ↓
科目体系管理: 树形结构 → 拖拽排序 → 虚拟分组
    ↓
试卷管理: 组卷选题 → 设置分值 → 发布考试
    ↓
错题监控: 热力图 → 高频错题TOP20 → 标签统计
```
