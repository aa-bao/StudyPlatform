package org.example.kaoyanplatform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

// 答题记录实体类
@Data
@TableName("answer_record")
public class AnswerRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long questionId;
    private Integer subjectId; // 新增科目ID字段
    private String userAnswer;
    private Integer isCorrect;
    private Integer score;
    private Integer duration;
    private String source;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}