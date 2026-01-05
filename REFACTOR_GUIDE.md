# 数据库映射表重构指南

## 📋 重构概述

本次重构将题目-书本-科目之间的直接外键关系改为使用映射表（`map_`前缀）管理，实现多对多关系，提高数据灵活性和扩展性。

**重构时间**: 2026-01-06

## 🎯 重构目标

### 原有结构问题
- ❌ `tb_question.book_id`：一道题只能属于一本书
- ❌ `tb_question.subject_id`：一道题只能属于一个科目
- ❌ `tb_book.subject_id`：一本书只能属于一个科目

### 新结构优势
- ✅ 一道题可以属于多本书
- ✅ 一道题可以属于多个科目/知识点
- ✅ 一本书可以包含多个科目
- ✅ 符合数据库范式，避免数据冗余
- ✅ 扩展性强，易于维护

## 📊 新的映射表结构

### 1. map_question_book（题目-书本映射表）
```sql
CREATE TABLE `map_question_book` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `book_id` int NOT NULL COMMENT '习题册ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_question_book` (`question_id`, `book_id`),
  KEY `idx_question_id` (`question_id`),
  KEY `idx_book_id` (`book_id`)
);
```

### 2. map_question_subject（题目-科目映射表）
```sql
CREATE TABLE `map_question_subject` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `subject_id` int NOT NULL COMMENT '科目ID或知识点ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_question_subject` (`question_id`, `subject_id`),
  KEY `idx_question_id` (`question_id`),
  KEY `idx_subject_id` (`subject_id`)
);
```

### 3. map_subject_book（书本-科目映射表）
```sql
CREATE TABLE `map_subject_book` (
  `id` int NOT NULL AUTO_INCREMENT,
  `book_id` int NOT NULL COMMENT '习题册ID',
  `subject_id` int NOT NULL COMMENT '科目ID或知识点ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_book_subject` (`book_id`, `subject_id`),
  KEY `idx_book_id` (`book_id`),
  KEY `idx_subject_id` (`subject_id`)
);
```

## 🔧 代码变更清单

### ✅ 已完成的工作

#### 1. 数据库层面
- ✅ 创建数据库迁移SQL文件：`sql/migration_refactor_mapping_tables.sql`
- ✅ 定义新的映射表结构
- ✅ 数据迁移逻辑（从外键字段到映射表）
- ✅ 索引优化（确保查询性能）

#### 2. 实体类（Entity）
- ✅ `MapQuestionBook.java` - 题目-书本映射实体
- ✅ `MapQuestionSubject.java` - 题目-科目映射实体
- ✅ `MapSubjectBook.java` - 书本-科目映射实体
- ✅ `Question.java` - 移除bookId和subjectId字段，添加@TableField辅助字段
- ✅ `Book.java` - 移除subjectId字段，添加@TableField辅助字段

#### 3. 数据访问层（Mapper）
- ✅ `MapQuestionBookMapper.java` - 提供双向查询方法
- ✅ `MapQuestionSubjectMapper.java` - 提供双向查询方法
- ✅ `MapSubjectBookMapper.java` - 提供双向查询方法

#### 4. 业务逻辑层（Service）
- ✅ `MapQuestionBookService.java` 及其实现类
- ✅ `MapQuestionSubjectService.java` 及其实现类
- ✅ `MapSubjectBookService.java` 及其实现类
- ✅ 更新 `QuestionServiceImpl.java` 使用新的映射表Mapper
- ✅ 更新 `SubjectServiceImpl.java` 使用新的映射表Mapper

## 🚀 执行数据库迁移步骤

### ⚠️ 重要：执行前必须备份数据库！

```bash
# 1. 备份数据库
mysqldump -u root -p kaoyan_platform > backup_before_migration_$(date +%Y%m%d).sql

# 2. 登录MySQL
mysql -u root -p

# 3. 选择数据库
use kaoyan_platform;

# 4. 执行迁移脚本
source F:/Coding/JavaProject/KaoYanPlatform/sql/migration_refactor_mapping_tables.sql

# 5. 验证数据完整性
# 检查是否有孤立数据
SELECT COUNT(*) as orphan_questions
FROM tb_question q
LEFT JOIN map_question_subject mqs ON q.id = mqs.question_id
WHERE mqs.question_id IS NULL;

# 6. 退出MySQL
exit;
```

## 📝 迁移SQL文件说明

**文件位置**: `sql/migration_refactor_mapping_tables.sql`

**迁移内容**:
1. 创建新的映射表（如果不存在）
2. 从外键字段迁移数据到映射表
3. 删除旧的外键字段
4. 添加索引优化查询性能
5. 提供常用查询示例

**注意事项**:
- SQL脚本包含了`INSERT IGNORE`语句，避免重复数据
- 使用事务确保数据一致性
- 包含完整性检查查询

## 🧪 测试验证步骤

### 1. 单元测试

测试映射表Service的基本功能：

```java
// 测试题目-书本关联
@Autowired
private MapQuestionBookService mapQuestionBookService;

@Test
public void testQuestionBookRelation() {
    // 添加关联
    boolean result = mapQuestionBookService.addQuestionBookRelation(1000L, 1);
    assertTrue(result);

    // 查询书本ID
    List<Integer> bookIds = mapQuestionBookService.getBookIdsByQuestionId(1000L);
    assertFalse(bookIds.isEmpty());
    assertTrue(bookIds.contains(1));

    // 查询题目ID
    List<Long> questionIds = mapQuestionBookService.getQuestionIdsByBookId(1);
    assertFalse(questionIds.isEmpty());

    // 删除关联
    mapQuestionBookService.removeQuestionBookRelation(1000L, 1);
    bookIds = mapQuestionBookService.getBookIdsByQuestionId(1000L);
    assertFalse(bookIds.contains(1));
}

// 测试题目-科目关联
@Autowired
private MapQuestionSubjectService mapQuestionSubjectService;

@Test
public void testQuestionSubjectRelation() {
    // 类似上面的测试逻辑
}

// 测试书本-科目关联
@Autowired
private MapSubjectBookService mapSubjectBookService;

@Test
public void testSubjectBookRelation() {
    // 类似上面的测试逻辑
}
```

### 2. 集成测试

测试业务逻辑是否正常：

```java
// 测试QuestionService
@Autowired
private QuestionService questionService;

@Test
public void testGetQuestionsBySubjectIds() {
    List<Integer> subjectIds = Arrays.asList(401, 402);
    List<Question> questions = questionService.getQuestionsBySubjectIds(subjectIds);

    assertNotNull(questions);
    // 验证返回的题目确实属于指定的科目
}

// 测试SubjectService
@Autowired
private SubjectService subjectService;

@Test
public void testGetTree() {
    List<SubjectDTO> tree = subjectService.getTree(1L, null);
    assertNotNull(tree);
    assertFalse(tree.isEmpty());
    // 验证题目数量统计是否正确
}
```

### 3. API接口测试

使用Postman或Knife4j测试API接口：

```bash
# 1. 启动后端服务
cd KaoYanPlatform
mvn spring-boot:run

# 2. 访问Knife4j API文档
# http://localhost:8081/doc.html

# 3. 测试相关接口
# - 获取科目树（包含题目数量统计）
# - 根据科目ID获取题目列表
# - 根据书本ID获取题目列表
```

### 4. 前端功能测试

```bash
# 1. 启动前端服务
cd kaoyan-frontend
npm run dev

# 2. 测试页面功能
# - 科目列表页面（检查题目数量显示）
# - 刷题页面（检查题目加载是否正常）
# - 专项突破页面（检查知识点筛选）
# - 真题模考页面（检查题目来源筛选）
```

## 🔍 常用查询示例

### 查询某本书的所有题目
```sql
SELECT q.*
FROM tb_question q
INNER JOIN map_question_book mqb ON q.id = mqb.question_id
WHERE mqb.book_id = 1;
```

### 查询某个科目的所有题目
```sql
SELECT q.*
FROM tb_question q
INNER JOIN map_question_subject mqs ON q.id = mqs.question_id
WHERE mqs.subject_id = 401;
```

### 查询题目所属的书本和科目
```sql
SELECT
  q.id as question_id,
  q.content,
  b.name as book_name,
  s.name as subject_name
FROM tb_question q
LEFT JOIN map_question_book mqb ON q.id = mqb.question_id
LEFT JOIN tb_book b ON mqb.book_id = b.id
LEFT JOIN map_question_subject mqs ON q.id = mqs.question_id
LEFT JOIN tb_subject s ON mqs.subject_id = s.id
WHERE q.id = 1000;
```

### 统计每个科目的题目数量
```sql
SELECT
  s.id,
  s.name,
  COUNT(mqs.question_id) as question_count
FROM tb_subject s
LEFT JOIN map_question_subject mqs ON s.id = mqs.subject_id
GROUP BY s.id, s.name
ORDER BY question_count DESC;
```

## ⚠️ 可能遇到的问题

### 问题1：外键约束错误
**现象**: 执行迁移SQL时报错 "Cannot delete or update a parent row: a foreign key constraint fails"

**解决**: 确保先删除所有外键约束，再删除字段
```sql
-- 查看外键约束
SELECT CONSTRAINT_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_NAME = 'tb_question' AND COLUMN_NAME IN ('book_id', 'subject_id');

-- 删除外键约束
ALTER TABLE tb_question DROP FOREIGN KEY fk_question_book;
ALTER TABLE tb_question DROP FOREIGN KEY fk_question_subject;
```

### 问题2：数据迁移后查询变慢
**现象**: 迁移后查询性能下降

**解决**: 确保映射表上有正确的索引
```sql
-- 检查索引
SHOW INDEX FROM map_question_book;
SHOW INDEX FROM map_question_subject;
SHOW INDEX FROM map_subject_book;

-- 如果缺少索引，手动添加
CREATE INDEX idx_question_id ON map_question_book(question_id);
CREATE INDEX idx_book_id ON map_question_book(book_id);
```

### 问题3：前端API报错
**现象**: 前端调用API时返回500错误

**解决**:
1. 检查后端日志查看具体错误
2. 确认数据库迁移已执行
3. 重启后端服务
4. 检查Controller层的字段映射是否正确

### 问题4：题目数量统计不对
**现象**: 科目树中显示的题目数量与实际不符

**解决**:
1. 检查`map_question_subject`表数据是否完整
2. 检查统计查询SQL是否正确
3. 清空缓存（如果使用了Redis等缓存）

## 📞 回滚方案

如果迁移后遇到严重问题需要回滚：

```sql
-- 1. 恢复外键字段
ALTER TABLE tb_question ADD COLUMN book_id INT;
ALTER TABLE tb_question ADD COLUMN subject_id INT;
ALTER TABLE tb_book ADD COLUMN subject_id INT;

-- 2. 从映射表恢复数据
UPDATE tb_question q
SET book_id = (SELECT book_id FROM map_question_book WHERE question_id = q.id LIMIT 1);

UPDATE tb_question q
SET subject_id = (SELECT subject_id FROM map_question_subject WHERE question_id = q.id LIMIT 1);

UPDATE tb_book b
SET subject_id = (SELECT subject_id FROM map_subject_book WHERE book_id = b.id LIMIT 1);

-- 3. 删除映射表（可选）
DROP TABLE IF EXISTS map_question_book;
DROP TABLE IF EXISTS map_question_subject;
-- DROP TABLE IF EXISTS map_subject_book;

-- 4. 或者使用数据库备份恢复
mysql -u root -p kaoyan_platform < backup_before_migration_YYYYMMDD.sql
```

## ✅ 验收标准

迁移成功的标准：

- [ ] 数据库迁移SQL执行无错误
- [ ] 所有映射表数据完整，无孤立数据
- [ ] 后端服务启动无错误
- [ ] Knife4j文档可正常访问
- [ ] 科目树API返回正确的题目数量
- [ ] 按科目查询题目功能正常
- [ ] 按书本查询题目功能正常
- [ ] 前端页面数据展示正常
- [ ] 单元测试全部通过
- [ ] 集成测试全部通过

## 📚 相关文件清单

### SQL文件
- `sql/migration_refactor_mapping_tables.sql` - 数据库迁移脚本
- `sql/map_subject_book_insert.sql` - 书本-科目映射数据
- `sql/subject_insert.sql` - 科目数据
- `sql/question_testdata.sql` - 题目测试数据（需更新）

### Java文件
- 实体类：`entity/MapQuestionBook.java`, `entity/MapQuestionSubject.java`, `entity/MapSubjectBook.java`
- Mapper：`mapper/MapQuestionBookMapper.java`, `mapper/MapQuestionSubjectMapper.java`, `mapper/MapSubjectBookMapper.java`
- Service接口：`service/MapQuestionBookService.java`, `service/MapQuestionSubjectService.java`, `service/MapSubjectBookService.java`
- Service实现：`service/impl/MapQuestionBookServiceImpl.java`, `service/impl/MapQuestionSubjectServiceImpl.java`, `service/impl/MapSubjectBookServiceImpl.java`

### 修改的文件
- `entity/Question.java` - 移除外键字段
- `entity/Book.java` - 移除外键字段
- `service/impl/QuestionServiceImpl.java` - 使用新映射表
- `service/impl/SubjectServiceImpl.java` - 使用新映射表

## 🎉 总结

本次重构成功地将题目-书本-科目的直接外键关系改为映射表模式，提升了系统的灵活性和扩展性。新的数据结构支持多对多关系，更符合实际业务需求。

**重构收益**:
- ✅ 数据结构更灵活，支持多对多关系
- ✅ 符合数据库范式设计原则
- ✅ 便于后续扩展和维护
- ✅ 提高了代码的可读性和可维护性

**下一步工作**:
- 根据实际使用情况优化索引
- 考虑添加缓存提升查询性能
- 监控API性能，必要时进行SQL优化
- 更新前端代码以适配新的数据结构
