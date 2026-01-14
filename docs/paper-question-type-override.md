# 试卷题目类型覆盖功能说明

## 功能概述

已实现试卷题目管理时可以单独设置每道题的**分值、题号和题目类型**，支持：
- 每道题单独设置分值
- 每道题单独设置题号
- 每道题单独设置题型（覆盖原题型）
- 批量设置默认值

## 数据库迁移

### 1. 执行 SQL 脚本

运行以下命令添加 `type` 字段到 `map_paper_question` 表：

```bash
mysql -u your_user -p your_database < KaoYanPlatform-back/src/main/resources/sql/add_type_to_map_paper_question.sql
```

或手动执行：

```sql
-- 添加 type 字段
ALTER TABLE map_paper_question
ADD COLUMN type INT DEFAULT NULL
COMMENT '题目类型（可选，用于覆盖原题目类型），参考QuestionType枚举: 1-单选, 2-多选, 3-填空, 4-综合应用, 5-完型填空, 6-阅读理解, 7-新题型, 8-翻译, 9-小作文, 10-大作文'
AFTER parent_section_name;

-- 创建索引
CREATE INDEX idx_map_paper_question_type ON map_paper_question(type);
```

## 后端改动

### 1. MapPaperQuestion 实体

文件：`KaoYanPlatform-back/src/main/java/org/example/kaoyanplatform/entity/MapPaperQuestion.java`

添加了 `type` 字段：

```java
/**
 * 题目类型
 * 用于覆盖题库中原题目的类型，支持同一题目在不同试卷中使用不同类型
 * 例如：某道单选题可以在政治试卷中作为多选题使用
 */
private Integer type;
```

### 2. PaperController 接口

文件：`KaoYanPlatform-back/src/main/java/org/example/kaoyanplatform/controller/PaperController.java`

更新了 `addQuestionToPaper` 接口，添加 `type` 参数：

```java
@PostMapping("/{paperId}/add-question")
public Result<String> addQuestionToPaper(
    @PathVariable String paperId,
    @RequestParam String questionId,
    @RequestParam Integer sortOrder,
    @RequestParam java.math.BigDecimal scoreValue,
    @RequestParam(required = false) String parentSectionName,
    @RequestParam(required = false) Integer type  // 新增参数
) {
    // ...
    mapping.setType(type);
    // ...
}
```

## 前端改动

### PaperManage.vue

文件：`KaoYanPlatform-front/src/views/admin/PaperManage.vue`

#### 1. 表格列（Lines 342-368）

添加了三个可编辑列：

```vue
<!-- 分值列 -->
<el-table-column label="分值" width="100" align="center">
    <template #default="scope">
        <el-input-number v-model="scope.row._scoreValue"
                        :controls="false"
                        :min="0" :max="50" size="small" />
    </template>
</el-table-column>

<!-- 题号列 -->
<el-table-column label="题号" width="100" align="center">
    <template #default="scope">
        <el-input-number v-model="scope.row._sortOrder"
                        :controls="false"
                        :min="1" size="small" />
    </template>
</el-table-column>

<!-- 题型列 -->
<el-table-column label="题型" width="120" align="center">
    <template #default="scope">
        <el-select v-model="scope.row._type" placeholder="选择" size="small">
            <el-option v-for="type in questionTypesList"
                      :key="type.code" :label="type.name" :value="type.code" />
        </el-select>
    </template>
</el-table-column>

<!-- 原题型列（显示用） -->
<el-table-column label="原类型" width="100" align="center">
    <template #default="scope">
        <el-tag size="small">{{ getQuestionTypeLabel(scope.row.type) }}</el-tag>
    </template>
</el-table-column>
```

#### 2. 默认设置区域（Lines 280-326）

```vue
<el-card class="batch-setting-card">
    <template #header>
        <span>默认设置（新添加题目的默认值）</span>
    </template>

    <!-- 默认分值 -->
    <el-input-number v-model="batchQuestionSettings.scoreValue"
                    :min="0" :max="50" />

    <!-- 所属大题名称 -->
    <el-input v-model="batchQuestionSettings.parentSectionName"
              placeholder="如：一、选择题" />

    <!-- 起始题号 -->
    <el-input-number v-model="batchQuestionSettings.startOrder"
                    :min="1" />

    <!-- 题型覆盖开关 -->
    <el-switch v-model="batchQuestionSettings.overrideType" />
    <el-select v-if="batchQuestionSettings.overrideType"
              v-model="batchQuestionSettings.overrideTypeValue">
        <el-option v-for="type in questionTypesList" />
    </el-select>
</el-card>
```

#### 3. 数据初始化（Lines 820-831）

```javascript
// 为每道题初始化临时字段
availableQuestions.value.forEach(q => {
    if (q._scoreValue === undefined) {
        q._scoreValue = batchQuestionSettings.value.scoreValue
    }
    if (q._sortOrder === undefined) {
        q._sortOrder = null
    }
    if (q._type === undefined) {
        q._type = q.type // 默认使用原题型
    }
})
```

#### 4. 添加题目逻辑（Lines 941-971）

```javascript
const addQuestionsToPaper = async () => {
    const settings = batchQuestionSettings.value

    for (let i = 0; i < selectedQuestions.value.length; i++) {
        const question = selectedQuestions.value[i]

        // 优先级：单独设置 > 批量设置
        const scoreValue = question._scoreValue !== undefined
            ? question._scoreValue
            : settings.scoreValue

        const sortOrder = question._sortOrder !== undefined
            ? question._sortOrder
            : (settings.startOrder + i)

        const type = question._type !== undefined
            ? question._type
            : (settings.overrideType && settings.overrideTypeValue
                ? settings.overrideTypeValue
                : question.type)

        await addQuestionToPaper(currentPaper.value.id, {
            questionId: question.id,
            sortOrder: sortOrder,
            scoreValue: scoreValue,
            parentSectionName: settings.parentSectionName || '',
            type: type  // 发送题型参数
        })
    }
}
```

## 使用流程

### 场景1：批量添加同类型题目

1. 在"默认设置"中设置：
   - 默认分值：5
   - 所属大题：一、单选题
   - 起始题号：1
   - 题型覆盖：关闭（使用原题型）

2. 搜索并勾选10道单选题

3. 点击"添加选中题目"

**结果**：10道题都会被设置为分值5、题号1-10、题型保持原样

### 场景2：单独设置每道题

1. 搜索题目后，在表格中直接修改：
   - 第1题：分值10、题号1、题型单选
   - 第2题：分值15、题号2、题型多选
   - 第3题：分值20、题号3、题型综合应用

2. 勾选这3道题

3. 点击"添加选中题目"

**结果**：每道题使用各自单独设置的值

### 场景3：批量覆盖题型

1. 在"默认设置"中：
   - 开启"题型覆盖"开关
   - 选择题型：多选题

2. 搜索并勾选题目（不管原题型是什么）

3. 点击"添加选中题目"

**结果**：所有题目都被设置为多选题（如果某道题单独设置了题型，则优先使用单独设置的）

## 优先级规则

```
单个题目设置 > 批量默认设置 > 题库原值
```

**分值优先级**：
1. 题目表格中单独设置的分值（`_scoreValue`）
2. 批量设置中的默认分值（`scoreValue`）

**题号优先级**：
1. 题目表格中单独设置的题号（`_sortOrder`）
2. 批量设置中的起始题号 + 索引（`startOrder + i`）

**题型优先级**：
1. 题目表格中单独设置的题型（`_type`）
2. 批量设置中题型覆盖开启时的值（`overrideTypeValue`）
3. 题库中原题目的题型（`question.type`）

## 注意事项

1. **数据库必须先迁移**：必须执行 SQL 脚本添加 `type` 字段，后端才能保存题型
2. **分值使用 BigDecimal**：后端接收的 `scoreValue` 是 `java.math.BigDecimal` 类型
3. **题型代码参考 QuestionType 枚举**：
   - 1: 单选题
   - 2: 多选题
   - 3: 填空题
   - 4: 综合应用题/大题
   - 5: 完型填空
   - 6: 阅读理解
   - 7: 新题型
   - 8: 翻译题
   - 9: 小作文
   - 10: 大作文
4. **题型覆盖是可选的**：不设置 `type` 时，使用题库中原题目的类型
5. **表格列初始化**：每次搜索后都会为题目初始化临时字段，分值默认使用批量设置的值
