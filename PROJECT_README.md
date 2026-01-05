# 项目背景与技术文档

> **注意**: 本文档旨在辅助 AI 理解 **KaoYanPlatform** (考研刷题平台) 的全栈上下文。它涵盖了前端 (`kaoyan-frontend`) 和后端 (`KaoYanPlatform`)。

## 重要更新记录

### 2026-01-06: 数据库表命名规范统一
**变更内容**: 统一数据库表命名规范，所有表使用 `tb_` 前缀

**主要变更**:
- ✅ 重命名 `sys_user` 表为 `tb_user`
- ✅ 更新 Java 实体类 `User.java` 的 `@TableName` 注解
- ✅ 更新项目文档中的表名说明

**执行指南**: 详见 `sql/RENAME_USER_TABLE_GUIDE.md`

### 2026-01-06: 数据库映射表重构
**重构内容**: 将题目-书本-科目之间的直接外键关系改为使用映射表（`map_`前缀）管理

**主要变更**:
- ✅ 从 `tb_question` 表删除 `book_id` 和 `subject_id` 外键字段
- ✅ 从 `tb_book` 表删除 `subject_id` 外键字段
- ✅ 新增 `map_question_book` 映射表（题目-书本多对多关系）
- ✅ 新增 `map_question_subject` 映射表（题目-科目多对多关系）
- ✅ 新增 `map_subject_book` 映射表（书本-科目多对多关系）
- ✅ 新增对应的 Java 实体类、Mapper、Service 层代码

**重构收益**:
- 支持多对多关系：一道题可以属于多本书、多个科目
- 符合数据库范式设计原则，避免数据冗余
- 提高数据灵活性和系统扩展性

**相关文档**: 详见 `REFACTOR_GUIDE.md` 重构指南（包含迁移步骤、测试方法、问题排查）

## 1. 项目概览

*   **项目名称**: KaoYanPlatform (考研刷题平台)
*   **架构模式**: 前后端分离。
*   **目标**: 一个供学生练习考题、管理错题（错题本）并追踪进度的平台。包含一个用于题目和用户管理的后台管理系统。
*   **核心流程**:
    *   **用户**: 登录 (支持双模切换) -> 仪表盘。
        *   **逐题精练 (Single Practice)**: 经典模式，按习题册顺序刷题，进入刷题界面左侧为题目显示区，右侧为作答区。
        *   **专项突破 (Topic Drill)**: 分题型模式，左侧树形目录选择考点，右侧针对性练习。
        *   **真题模考 (Mock Exam)**: 套卷模式，模拟真实考试环境（开发中）。
    *   **通用功能**: 刷题 (Latex 公式支持、计时、画板草稿、多选、"不会"选项需提交) -> 查看解析 (支持 Latex) -> 查看错题本 (Correction Notebook，瀑布流布局、动态面包屑导航、支持政治/英语/数学/408分类、显示最近错误时间) -> 收藏题目 (支持自定义标签、历史标签记忆) -> 下载资料。
    *   **管理员**: 登录 -> 管理后台 -> 题目管理 (支持标签管理) -> 用户管理 -> 科目管理 -> 资料管理。

随心刷 | 精准练 | 整卷测
逐题精练｜专项突破｜真题模考

## 2. 技术栈

### 后端 (`KaoYanPlatform`)
*   **框架**: Spring Boot 3.5.9
*   **ORM**: MyBatis Plus 3.5.5
*   **数据库**: MySQL 8.0
*   **API 文档**: Knife4j 4.5.0 (Swagger)
*   **安全**: Spring Security
*   **工具库**: Lombok, Hutool 5.8.25

### 前端 (`kaoyan-frontend`)
*   **框架**: Vue 3.5 (Composition API)
*   **构建工具**: Vite 7.3
*   **UI 组件库**: Element Plus 2.13
*   **数学公式渲染**: Katex (支持行内/块级公式)
*   **状态管理**: Pinia 3.0
*   **路由**: Vue Router 4.6
*   **HTTP 客户端**: Axios 1.13
*   **图表**: ECharts 6.0
*   **特色功能**: HTML5 Canvas (画板), Flex/Grid 布局 (分屏刷题、错题本瀑布流), Glassmorphism (毛玻璃特效)

## 3. 详细项目结构

### 后端结构 (`KaoYanPlatform/src/main/java/org/example/kaoyanplatform`)
```text
kaoyanplatform/
├── common/
│   └── Result.java             # 统一 API 响应封装 (code, msg, data)
├── config/
│   ├── CorsConfig.java         # CORS 跨域配置
│   ├── SecurityConfig.java     # Spring Security 配置
│   ├── WebConfig.java          # MVC 配置
│   └── Knife4jConfig.java      # API 文档配置
│   └── MybatisPlusConfig.java  # MybatisPlus 配置
├── controller/                 # API 接口层
│   ├── AdminController.java
│   ├── CollectionController.java # 收藏管理 (新增 /tags 接口)
│   ├── FileController.java     # 文件上传/下载
│   ├── QuestionController.java # 题目管理
│   ├── RecordController.java   # 考试记录
│   ├── SubjectController.java  # 科目管理
│   └── UserController.java     # 用户认证与个人信息
├── entity/                     # 数据库模型 (MyBatis Plus)
│   ├── dto/                    # 数据传输对象
│   │   └── SubjectDTO.java     # 科目树形结构传输对象
│   ├── Book.java               # tb_book 表
│   ├── ExamRecord.java         # tb_exam_record 表
│   ├── MapQuestionBook.java    # map_question_book 表 (题目-书本映射)
│   ├── MapQuestionSubject.java # map_question_subject 表 (题目-科目映射)
│   ├── MapSubjectBook.java     # map_subject_book 表 (书本-科目映射)
│   ├── Question.java           # tb_question 表
│   ├── Subject.java            # tb_subject 表
│   ├── User.java               # tb_user 表
│   └── MistakeRecord.java      # tb_mistake_record 表
├── handler/
│   ├── GlobalExceptionHandler.java # 全局异常处理
│   └── MyMetaObjectHandler.java    # MP自动填充处理
├── mapper/                     # 数据访问层 (接口)
│   ├── BookMapper.java
│   ├── CollectionMapper.java
│   ├── ExamRecordMapper.java
│   ├── MapQuestionBookMapper.java    # 题目-书本映射Mapper
│   ├── MapQuestionSubjectMapper.java # 题目-科目映射Mapper
│   ├── MapSubjectBookMapper.java     # 书本-科目映射Mapper
│   ├── QuestionMapper.java
│   ├── SubjectMapper.java
│   ├── UserMapper.java
│   ├── UserProgressMapper.java
│   └── MistakeRecordMapper.java
└── service/                    # 业务逻辑层
│   ├── BookService.java
│   ├── CollectionService.java
│   ├── MapQuestionBookService.java    # 题目-书本映射Service
│   ├── MapQuestionSubjectService.java # 题目-科目映射Service
│   ├── MapSubjectBookService.java     # 书本-科目映射Service
│   ├── QuestionService.java
│   ├── RecordService.java
│   ├── SubjectService.java
│   ├── UserService.java
│   ├── UserProgressService.java
│   └── MistakeRecordService.java
│   └── impl/
│       ├── BookServiceImpl.java
│       ├── CollectionServiceImpl.java
│       ├── MapQuestionBookServiceImpl.java
│       ├── MapQuestionSubjectServiceImpl.java
│       ├── MapSubjectBookServiceImpl.java
│       ├── QuestionServiceImpl.java
│       ├── RecordServiceImpl.java
│       ├── SubjectServiceImpl.java
│       ├── UserServiceImpl.java
│       ├── UserProgressServiceImpl.java
│       └── MistakeRecordServiceImpl.java
└── KaoYanPlatformApplication
```

### 前端结构 (`kaoyan-frontend/src`)
```text
src/
├── api/
│   └── user.js                 # 用户相关 API 调用
├── assets/                     # 静态资源 (图片)
├── router/
│   └── index.js                # 路由定义与导航守卫
├── stores/                     # Pinia 状态仓库
│   ├── user.js                 # 用户会话状态
│   └── counter.js
├── utils/
│   └── request.js              # 带有拦截器的 Axios 实例
├── views/                      # 页面组件
│   ├── admin/                  # 管理员专用视图
│   │   ├── AdminHome.vue
│   │   └── QuestionManage.vue
│   ├── quiz/                   # 刷题模式视图
│   │   ├── SinglePractice.vue  # 逐题精练
│   │   ├── TopicDrill.vue      # 专项突破
│   │   └── MockExam.vue        # 真题模考
│   ├── layout/                 # 布局容器
│   │   ├── AdminLayout.vue     # 管理员侧边栏 + 顶栏
│   │   └── UserLayout.vue      # 用户侧边栏 (Light Theme) + 顶栏
├── Dashboard.vue           # 用户首页
├── Login.vue               # 登录页
├── CorrectionNotebook.vue  # 错题本 (瀑布流、分类导航)
├── SubjectList.vue         # 科目选择列表
└── UserProfile.vue
├── App.vue                     # 根组件
└── main.js                     # 入口文件 (插件配置)
```

## 4. 数据模型与接口

### 数据库设计 (完整表结构)

#### 用户表 (`tb_user`)
| 字段 | 类型 | 描述 |
| :--- | :--- | :--- |
| `id` | bigint | 主键, 自增 |
| `username` | varchar(50) | 用户名/手机号 (Unique) |
| `password` | varchar(100) | 密码 |
| `nickname` | varchar(50) | 昵称 |
| `role` | varchar(20) | 角色: `admin` 或 `student` |
| `avatar` | varchar(255) | 头像 URL |
| `target_school` | varchar(100) | 目标院校 |
| `target_total_score` | int | 目标总分 |
| `exam_year` | varchar(50) | 考研年份 |
| `exam_subjects` | varchar(255) | 公共课 |
| `create_time` | datetime | 创建时间 |
| `update_time` | datetime | 更新时间 |

#### 题目表 (`tb_question`)
| 字段 | 类型 | 描述 |
| :--- | :--- | :--- |
| `id` | bigint | 主键, 自增 |
| `type` | tinyint | 题目类型: 1-单选, 2-多选, 3-填空, 4-简答 |
| `content` | text | 题干内容 |
| `options` | json | 选项(JSON存储: ["A.xx", "B.xx"]) |
| `answer` | text | 正确答案 |
| `analysis` | text | 解析 |
| `difficulty` | tinyint | 难度: 1-5 |
| `tags` | json | 题目标签 |
| `create_time` | datetime | 创建时间 |
| `source` | varchar(100) | 题目来源 |
**注**: 题目与科目、书本的关系通过映射表管理，不再直接存储外键字段。通过 `map_question_subject` 关联科目，通过 `map_question_book` 关联习题册。

#### 答题记录表 (`tb_exam_record`)
| 字段 | 类型 | 描述 |
| :--- | :--- | :--- |
| `id` | bigint | 主键, 自增 |
| `user_id` | bigint | 用户ID |
| `question_id` | bigint | 题目ID |
| `user_answer` | text | 用户提交的答案 |
| `is_correct` | tinyint(1) | 是否正确: 0-错, 1-对 |
| `score` | int | 得分 |
| `duration` | int | 答题耗时(秒) |
| `create_time` | datetime | 创建时间 |

#### 错题本 (`tb_mistake_record`)
| 字段 | 类型 | 描述 |
| :--- | :--- | :--- |
| `id` | int | 主键, 自增 |
| `user_id` | int | 用户ID |
| `question_id` | int | 题目ID |
| `create_time` | datetime | 首次加入错题本时间 |
| `update_time` | datetime | 最近一次答错时间 |
| `error_count` | int | 累计答错次数 |

#### 科目分类表 (`tb_subject`)
| 字段 | 类型 | 描述 |
| :--- | :--- | :--- |
| `id` | int | 主键, 自增 |
| `name` | varchar(50) | 科目名称 |
| `parent_id` | int | 父级ID |
| `icon` | varchar(100) | 图标 |
| `sort` | int | 排序号 |
| `level` | varchar(100) | 层级:1-专业课/公共课；2-章;3-节/知识点 |
| `question_count` | int | 题目数量 |
| `scope` | varchar(50) | 适用范围: "4,5,6" (对应数一、数二、数三) |
注：id范围1-100为顶级科目，例如：1-政治，2-英语一，3-英语二；id范围100以上为二级科目，例如：401-高数，402-线代，403-概率，其中，前缀40为高数下的详细知识点，例如id4011为函数、极限、连续,4012为一元微分学；

#### 习题册表 (`tb_book`)
| 字段 | 类型 | 描述 |
| :--- | :--- | :--- |
| `id` | bigint | 主键, 自增 |
| `name` | varchar(50) | 习题册名称 |
| `description` | varchar(255) | 习题册描述 |
| `create_time` | datetime | 创建时间 |
**注**: 习题册与科目的关系通过 `map_subject_book` 映射表管理，支持一本书包含多个科目。

#### 收藏夹表 (`tb_collection`)
| 字段 | 类型 | 描述 |
| :--- | :--- | :--- |
| `id` | bigint | 主键, 自增 |
| `user_id` | bigint | 用户ID |
| `question_id` | bigint | 题目ID |
| `tag` | json | 自定义标签 |
| `create_time` | datetime | 创建时间 |

#### 学习资源表 (`tb_resource`)
| 字段 | 类型 | 描述 |
| :--- | :--- | :--- |
| `id` | bigint | 主键, 自增 |
| `title` | varchar(100) | 资料标题 |
| `url` | varchar(255) | 下载/预览地址 |
| `file_type` | varchar(20) | 文件类型 |
| `subject_id` | int | 所属科目ID |
| `create_time` | datetime | 创建时间 |

#### 用户学习进度表 (`tb_user_progress`)
| 字段 | 类型 | 描述 |
| :--- | :--- | :--- |
| `id` | bigint | 主键, 自增 |
| `user_id` | bigint | 用户ID |
| `subject_id` | int | 科目或考点ID |
| `finished_count` | int | 该考点下已做题目数 |
| `correct_count` | int | 该考点下做对题目数 |
| `update_time` | datetime | 更新时间 |

#### 映射表设计（多对多关系）

**设计说明**: 使用映射表（`map_`前缀）管理题目、书本、科目之间的多对多关系，提高数据灵活性和扩展性。

##### 题目-书本关联表 (`map_question_book`)
| 字段 | 类型 | 描述 |
| :--- | :--- | :--- |
| `id` | bigint | 主键, 自增 |
| `question_id` | bigint | 题目ID |
| `book_id` | int | 习题册ID |
**索引**: `uk_question_book` (question_id, book_id) 唯一索引, `idx_question_id`, `idx_book_id`

##### 题目-科目关联表 (`map_question_subject`)
| 字段 | 类型 | 描述 |
| :--- | :--- | :--- |
| `id` | bigint | 主键, 自增 |
| `question_id` | bigint | 题目ID |
| `subject_id` | int | 科目ID或知识点ID |
**索引**: `uk_question_subject` (question_id, subject_id) 唯一索引, `idx_question_id`, `idx_subject_id`

##### 书本-科目关联表 (`map_subject_book`)
| 字段 | 类型 | 描述 |
| :--- | :--- | :--- |
| `id` | int | 主键, 自增 |
| `book_id` | int | 习题册ID (如: 数一:4; 数二:5; 数三:6) |
| `subject_id` | int | 科目ID或知识点ID (如: 高数:401; 线代:402; 概率:403) |
**索引**: `uk_book_subject` (book_id, subject_id) 唯一索引, `idx_book_id`, `idx_subject_id`

**映射表关系链**:
```
题目 → map_question_book → 习题册
题目 → map_question_subject → 科目/知识点
习题册 → map_subject_book → 科目/知识点
```

**查询示例**:
```sql
-- 查询某本书的所有题目
SELECT q.* FROM tb_question q
INNER JOIN map_question_book mqb ON q.id = mqb.question_id
WHERE mqb.book_id = 1;

-- 查询某个科目的所有题目
SELECT q.* FROM tb_question q
INNER JOIN map_question_subject mqs ON q.id = mqs.question_id
WHERE mqs.subject_id = 401;

-- 查询题目所属的书本和科目
SELECT q.id, q.content, b.name as book_name, s.name as subject_name
FROM tb_question q
LEFT JOIN map_question_book mqb ON q.id = mqb.question_id
LEFT JOIN tb_book b ON mqb.book_id = b.id
LEFT JOIN map_question_subject mqs ON q.id = mqs.question_id
LEFT JOIN tb_subject s ON mqs.subject_id = s.id
WHERE q.id = 1000;
```




### API 规范
*   **基础 URL**: `http://localhost:8081`
*   **result.java**:

    ```json
    public class Result<T> {
        private Integer code; // 200 是成功，500 是失败
        private String msg;   // 提示信息
        private T data;       // 具体数据
    
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
        
        public static <T> Result<T> success() {
            Result<T> result = new Result<>();
            result.setCode(200);
            result.setMsg("成功");
            result.setData(null);
            return result;
        }
    }
    ```
*   **拦截器** (`request.js`):
    *   **响应**: 自动解包 `data`。如果 `code !== 200`，触发 `ElMessage.error` 并拒绝 Promise。

## 5. 前端核心架构

### 路由与权限 (`router/index.js`)
*   **路由**:
    *   `/login`: 公开。
    *   `/admin/*`: 受保护。需要 `role === 'admin'`。
    *   `/user/*`: 受保护。需要登录。
*   **守卫**:
    *   检查 `localStorage` 中的 `role` 和 `userInfo`。
    *   将未授权访问重定向到 `/login`。
    *   将已登录用户从 `/login` 重定向到其对应的仪表盘。

### 布局策略
*   **AdminLayout**: 用于 `/admin` 路由。包含管理员侧边栏。
*   **UserLayout**: 用于 `/user` 路由。专为学生设计。采用浅色主题侧边栏，优化视觉体验。

## 6. 开发规范

*   **认证**:
    *   前端依赖 `localStorage` (`role`, `userInfo`) 进行路由权限检查。
    *   后端使用 Spring Security 进行安全控制。
*   **环境**:
    *   开发 API 代理: `request.js` 中硬编码 `baseURL` 为 `http://localhost:8081`。
*   **JSON 处理**:
    *   后端使用 `JacksonTypeHandler` 处理复杂字段（如 `Question` 实体中的 `options`, `tags`），自动将 JSON 字符串映射为 Java List。
*   **图标系统**:
    *   使用 SVG 图标 (`assets/icons/`)。
    *   通过 `vite-svg-loader` 加载，需在导入时添加 `?url` 后缀（如 `import icon from '@/assets/icons/icon.svg?url'`）以作为 URL 字符串使用。
    *   利用 CSS `filter` 属性实现图标颜色动态切换。
