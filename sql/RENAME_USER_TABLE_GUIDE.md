# 数据库表重命名指南：sys_user -> tb_user

## 📋 重命名概述

将数据库表 `sys_user` 重命名为 `tb_user`，统一命名规范，所有表使用 `tb_` 前缀。

**变更时间**: 2026-01-06

## ✅ 已完成的代码修改

### 1. Java后端
- ✅ `entity/User.java` - 更新 `@TableName` 注解：`sys_user` → `tb_user`

### 2. 项目文档
- ✅ `PROJECT_README.md` - 更新表名说明：`sys_user` → `tb_user`

### 3. SQL脚本
- ✅ 创建 `sql/rename_sys_user_to_tb_user.sql` - 表重命名脚本

## 🚀 执行数据库重命名

### 方法一：使用Navicat（推荐）

1. **备份数据库**
   ```
   右键点击 kaoyan_platform 数据库 → 转储SQL文件 → 保存备份
   ```

2. **打开SQL文件**
   ```
   文件位置: F:\Coding\JavaProject\KaoYanPlatform\sql\rename_sys_user_to_tb_user.sql
   ```

3. **执行SQL脚本**
   ```
   点击"运行"按钮或按F5
   ```

4. **验证结果**
   执行以下SQL检查：
   ```sql
   -- 查看 tb_user 表是否存在
   SHOW TABLES LIKE 'tb_user';

   -- 查看 sys_user 表是否已删除
   SHOW TABLES LIKE 'sys_user';

   -- 查看 tb_user 表结构
   DESC tb_user;

   -- 查看数据行数
   SELECT COUNT(*) FROM tb_user;
   ```

### 方法二：使用MySQL命令行

```bash
# 1. 备份数据库
mysqldump -u root -p kaoyan_platform > backup_before_rename_user_$(date +%Y%m%d).sql

# 2. 登录MySQL
mysql -u root -p

# 3. 执行重命名
USE kaoyan_platform;
RENAME TABLE sys_user TO tb_user;

# 4. 验证
SHOW TABLES LIKE 'tb_user';
SELECT COUNT(*) FROM tb_user;
```

### 方法三：使用完整路径执行

```bash
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p kaoyan_platform < F:\Coding\JavaProject\KaoYanPlatform\sql\rename_sys_user_to_tb_user.sql
```

## ⚠️ 注意事项

### 1. 外键约束
如果有其他表引用 `sys_user` 表的外键，可能需要额外处理：

```sql
-- 查看引用 sys_user 的外键
SELECT
    CONSTRAINT_NAME,
    TABLE_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'kaoyan_platform'
AND REFERENCED_TABLE_NAME = 'sys_user';

-- 如果有外键，需要先删除，重命名后再重建
-- ALTER TABLE xxx DROP FOREIGN KEY fk_xxx;
-- RENAME TABLE sys_user TO tb_user;
-- ALTER TABLE xxx ADD CONSTRAINT fk_xxx FOREIGN KEY (user_id) REFERENCES tb_user(id);
```

### 2. 应用程序影响
- ✅ 代码已同步更新，无需修改应用层
- ⚠️ 重命名表后需要重启后端服务
- ⚠️ 确保没有直接在SQL中硬编码 `sys_user` 表名的查询

### 3. 存储过程和视图
检查是否有存储过程或视图引用了 `sys_user` 表：

```sql
-- 查看引用 sys_user 的视图
SELECT VIEW_NAME, VIEW_DEFINITION
FROM information_schema.VIEWS
WHERE TABLE_SCHEMA = 'kaoyan_platform'
AND VIEW_DEFINITION LIKE '%sys_user%';

-- 查看引用 sys_user 的存储过程/函数
SELECT ROUTINE_NAME, ROUTINE_TYPE
FROM information_schema.ROUTINES
WHERE ROUTINE_SCHEMA = 'kaoyan_platform'
AND ROUTINE_DEFINITION LIKE '%sys_user%';
```

## ✅ 验证清单

重命名完成后，请验证以下项目：

- [ ] `sys_user` 表已不存在
- [ ] `tb_user` 表存在，数据完整
- [ ] `tb_user` 表结构与原 `sys_user` 相同
- [ ] 数据行数一致
- [ ] 重启后端服务，启动无错误
- [ ] 登录功能正常
- [ ] 用户注册功能正常
- [ ] 用户信息查询正常
- [ ] 前端页面数据加载正常

## 🔧 重启后端服务

```bash
cd F:\Coding\JavaProject\KaoYanPlatform\KaoYanPlatform
mvn clean install
mvn spring-boot:run
```

检查启动日志：
- ✅ MyBatis Plus 扫描到 `tb_user` 表
- ✅ 没有表不存在的错误
- ✅ 可以正常访问 http://localhost:8081/doc.html

## 🔄 回滚方案

如果重命名后遇到问题需要回滚：

```sql
-- 方式1：直接重命名回去
RENAME TABLE tb_user TO sys_user;

-- 方式2：使用备份恢复
mysql -u root -p kaoyan_platform < backup_before_rename_user_YYYYMMDD.sql
```

同时需要回滚代码修改：
1. 将 `User.java` 中的 `@TableName("tb_user")` 改回 `@TableName("sys_user")`
2. 更新 `PROJECT_README.md` 中的表名说明

## 📞 相关文档

- 主项目文档：`PROJECT_README.md`
- 重构指南：`REFACTOR_GUIDE.md`
- 映射表迁移：`sql/migration_refactor_mapping_tables.sql`

## 🎉 总结

本次重命名统一了数据库表命名规范，所有业务表都使用 `tb_` 前缀：

- ✅ `tb_user` - 用户表（原 `sys_user`）
- ✅ `tb_question` - 题目表
- ✅ `tb_book` - 习题册表
- ✅ `tb_subject` - 科目表
- ✅ `tb_exam_record` - 答题记录表
- ✅ `tb_mistake_record` - 错题本表
- ✅ `tb_collection` - 收藏夹表
- ✅ `tb_resource` - 学习资源表
- ✅ `tb_user_progress` - 用户学习进度表

映射表使用 `map_` 前缀：
- ✅ `map_question_book` - 题目-书本映射
- ✅ `map_question_subject` - 题目-科目映射
- ✅ `map_subject_book` - 书本-科目映射
