# 科目题型配置说明

## 概述
已实现根据科目动态加载题型的功能。每个科目可以配置自己支持的题型，添加题目时会根据选择的科目显示对应的题型选项。

## 数据库配置

### 1. 执行 SQL 迁移脚本

首先运行 SQL 脚本添加 `question_types` 字段：

```bash
mysql -u your_user -p your_database < src/main/resources/sql/add_question_types_to_subject.sql
```

或手动执行：

```sql
ALTER TABLE tb_subject ADD COLUMN question_types VARCHAR(100) DEFAULT NULL COMMENT '支持的题型列表，逗号分隔，如: 1,2,4';
```

### 2. 为科目配置题型

更新科目的题型配置：

```sql
-- 政治：单选题(1)、多选题(2)、综合应用题/大题(4)
UPDATE tb_subject SET question_types = '1,2,4' WHERE name = '政治' AND level = '1';

-- 408：单选题(1)、综合应用题/大题(4)
UPDATE tb_subject SET question_types = '1,4' WHERE name = '408' AND level = '1';

-- 英语相关：完型填空(5)、阅读理解(6)、新题型(7)、翻译题(8)、小作文(9)、大作文(10)
UPDATE tb_subject SET question_types = '5,6,7,8,9,10' WHERE name LIKE '%英语%' AND level = '1';

-- 数学相关：单选题(1)、填空题(3)、综合应用题/大题(4)
UPDATE tb_subject SET question_types = '1,3,4' WHERE name LIKE '%数学%' AND level = '1';
```

### 3. 题型代码对照表

| 代码 | 题型名称 |
|------|---------|
| 1 | 单选题 |
| 2 | 多选题 |
| 3 | 填空题 |
| 4 | 综合应用题/大题 |
| 5 | 完型填空 |
| 6 | 阅读理解 |
| 7 | 新题型 |
| 8 | 翻译题 |
| 9 | 小作文 |
| 10 | 大作文 |

## 后端接口

### 获取科目支持的题型

**接口：** `GET /subject/{subjectId}/question-types`

**参数：**
- `subjectId`: 科目ID（路径参数）

**响应示例：**

选择政治（id=1）：
```json
{
  "code": 200,
  "data": [
    { "code": 1, "name": "单选题" },
    { "code": 2, "name": "多选题" },
    { "code": 4, "name": "综合应用题/大题" }
  ]
}
```

选择英语（id=2）：
```json
{
  "code": 200,
  "data": [
    { "code": 5, "name": "完型填空" },
    { "code": 6, "name": "阅读理解" },
    { "code": 7, "name": "新题型" },
    { "code": 8, "name": "翻译题" },
    { "code": 9, "name": "小作文" },
    { "code": 10, "name": "大作文" }
  ]
}
```

## 前端使用

### 在试卷管理页面

在"添加题目到试卷"对话框中：

1. **选择考试科目**后，题型下拉框会自动更新为该科目支持的题型
2. 如果科目未配置题型，则显示所有题型
3. 清空科目选择后，题型恢复为所有题型

**代码示例：**

```javascript
import { getQuestionTypesBySubject } from '@/api/subject'

// 处理科目变化
const handleSubjectChange = async (subjectId) => {
    if (!subjectId) {
        // 清空时恢复所有题型
        questionTypesList.value = await loadQuestionTypes()
        return
    }

    // 加载该科目支持的题型
    const res = await getQuestionTypesBySubject(subjectId)
    if (res.code === 200) {
        questionTypesList.value = res.data
    }
}
```

## 在科目管理页面配置题型

管理员可以在科目管理页面为每个科目配置题型：

1. 进入科目管理页面
2. 编辑某个 Level 1 科目（政治、英语、数学等）
3. 在"支持的题型"字段中输入题型代码，用逗号分隔
4. 保存后，该科目的题型配置即生效

**配置示例：**

| 科目名称 | question_types 字段值 | 说明 |
|---------|---------------------|------|
| 政治 | `1,2,4` | 单选、多选、大题 |
| 英语一 | `5,6,7,8,9,10` | 完型、阅读、新题型、翻译、小作文、大作文 |
| 数学一 | `1,3,4` | 单选、填空、大题 |
| 408 | `1,4` | 单选、大题 |

## 扩展新题型

如果需要添加新的题型：

1. 在 `QuestionType.java` 枚举中添加新题型
2. 在需要支持该题型的科目的 `question_types` 字段中添加对应代码
3. 前端会自动获取并显示新题型

无需修改前端代码。

## 注意事项

1. 只有 **Level 1 的考试科目** 需要配置题型
2. `question_types` 字段为空时，默认显示所有题型
3. 题型代码必须存在于 `QuestionType` 枚举中
4. 配置格式：使用英文逗号分隔，如 `1,2,4`
