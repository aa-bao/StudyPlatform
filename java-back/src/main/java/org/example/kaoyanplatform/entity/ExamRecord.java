package org.example.kaoyanplatform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// 考试记录实体类
@Data
@TableName("exam_record")
public class ExamRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long sessionId;
    private Long questionId;
    private String userAnswer;
    private String userAnswerImages;  // 用户手写答案图片（JSON格式存储Base64列表）
    private Integer isCorrect;
    private BigDecimal scoreEarned;
    private Integer durationSeconds;
    private String aiFeedback;
    private Integer knowledgePointId;

    // 用户自评字段（用于主观题）
    private Integer userProcessGrading;  // 用户自评-过程：null-未批改, 0-错误, 1-正确
    private Integer userResultGrading;   // 用户自评-结果：null-未批改, 0-错误, 1-正确
    private LocalDateTime gradingTime;   // 批改时间

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
