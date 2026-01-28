package org.example.kaoyanplatform.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 主观题详情DTO（用于用户自评）
 */
@Data
@Schema(description = "主观题详情")
public class SubjectiveQuestionDetailDTO {

    @Schema(description = "题目ID")
    private Long questionId;

    @Schema(description = "题号")
    private Integer sortOrder;

    @Schema(description = "题目内容")
    private String questionContent;

    @Schema(description = "题目类型")
    private Integer questionType;

    @Schema(description = "用户答案")
    private String userAnswer;

    @Schema(description = "标准答案")
    private String standardAnswer;

    @Schema(description = "总分")
    private BigDecimal totalScore;

    @Schema(description = "过程分满分")
    private Integer processScore;

    @Schema(description = "过程分比例")
    private Double processRatio;

    @Schema(description = "过程分分值")
    private BigDecimal processScoreValue;

    @Schema(description = "结果分满分")
    private Integer resultScore;

    @Schema(description = "结果分比例")
    private Double resultRatio;

    @Schema(description = "结果分分值")
    private BigDecimal resultScoreValue;

    @Schema(description = "用户自评-过程：null-未批改, 0-错误, 1-正确")
    private Integer userProcessGrading;

    @Schema(description = "用户自评-结果：null-未批改, 0-错误, 1-正确")
    private Integer userResultGrading;

    @Schema(description = "当前得分")
    private BigDecimal currentScore;
}
