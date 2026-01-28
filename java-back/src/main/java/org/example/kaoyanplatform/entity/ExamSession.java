package org.example.kaoyanplatform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// 考试会话实体类
@Data
@TableName("exam_session")
public class ExamSession {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long paperId;
    private Integer status;

    /**
     * 考试开始时间 - 用户第一次真正开始考试的时间
     * 用于计算考试实际进行时长和预期结束时间
     */
    private LocalDateTime startTime;

    private LocalDateTime submitTime;
    private BigDecimal totalScore;
    private Integer switchCount;
    private String aiSummary;

    private String snapshotAnswers;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 预期结束时间 = startTime + paper.timeLimit (分钟)
     * 用于倒计时计算，用户可以提前交卷
     */
    private LocalDateTime expectedEndTime;
}
