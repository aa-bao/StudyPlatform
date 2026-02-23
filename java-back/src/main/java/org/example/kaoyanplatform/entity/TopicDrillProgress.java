package org.example.kaoyanplatform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 专项突破学习进度实体类
 */
@Data
@TableName("topic_drill_progress")
public class TopicDrillProgress {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId; // 用户ID

    private Integer subjectId; // 知识点ID

    private Integer questionCount; // 题目总数

    private Integer answeredCount; // 已答题目数量

    private Integer correctCount; // 正确答案数量

    private Integer accuracy; // 正确率

    private String questionsData; // 题目数据（JSON格式）

    private String cardPositions; // 卡片位置（JSON格式）

    private LocalDateTime timestamp; // 时间戳

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
