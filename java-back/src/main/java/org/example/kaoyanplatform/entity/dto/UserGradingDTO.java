package org.example.kaoyanplatform.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户自评数据传输对象
 */
@Data
@Schema(description = "用户自评请求")
public class UserGradingDTO {

    @Schema(description = "考试会话ID", required = true)
    private Long sessionId;

    @Schema(description = "主观题自评列表", required = true)
    private List<SubjectiveGradingItem> gradingItems;

    @Data
    @Schema(description = "主观题自评项")
    public static class SubjectiveGradingItem {

        @Schema(description = "题目ID", required = true)
        private Long questionId;

        @Schema(description = "过程分评分：null-未批改, 0-错误, 1-正确")
        private Integer processGrading;

        @Schema(description = "结果分评分：null-未批改, 0-错误, 1-正确")
        private Integer resultGrading;
    }
}
