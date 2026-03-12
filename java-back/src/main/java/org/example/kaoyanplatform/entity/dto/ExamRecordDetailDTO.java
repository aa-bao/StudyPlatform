package org.example.kaoyanplatform.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "答题记录详情DTO（包含题目信息）")
public class ExamRecordDetailDTO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "考试会话ID")
    private Long sessionId;

    @Schema(description = "题目ID")
    private Long questionId;

    @Schema(description = "用户答案")
    private String userAnswer;

    @Schema(description = "用户手写答案图片（JSON格式Base64列表）")
    private String userAnswerImages;

    @Schema(description = "是否正确：0-错误, 1-正确, 3-待批改（主观题）")
    private Integer isCorrect;

    @Schema(description = "得分")
    private BigDecimal scoreEarned;

    @Schema(description = "答题时长（秒）")
    private Integer durationSeconds;

    @Schema(description = "AI反馈")
    private String aiFeedback;

    @Schema(description = "知识点ID")
    private Integer knowledgePointId;

    @Schema(description = "用户自评-过程：null-未批改, 0-错误, 1-正确")
    private Integer userProcessGrading;

    @Schema(description = "用户自评-结果：null-未批改, 0-错误, 1-正确")
    private Integer userResultGrading;

    @Schema(description = "批改时间")
    private LocalDateTime gradingTime;

    @Schema(description = "题目内容")
    private String questionContent;

    @Schema(description = "题目类型：1-单选, 2-多选, 3-填空, 4-主观题")
    private Integer questionType;

    @Schema(description = "标准答案")
    private String standardAnswer;

    @Schema(description = "题号")
    private Integer sortOrder;

    @Schema(description = "总分")
    private BigDecimal totalScore;

    @Schema(description = "过程分分值")
    private BigDecimal processScoreValue;

    @Schema(description = "结果分分值")
    private BigDecimal resultScoreValue;

    @Schema(description = "过程分比例")
    private Double processRatio;

    @Schema(description = "结果分比例")
    private Double resultRatio;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
