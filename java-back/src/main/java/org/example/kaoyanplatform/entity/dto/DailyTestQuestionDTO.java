package org.example.kaoyanplatform.entity.dto;

import lombok.Data;

/**
 * 每日测试题目DTO
 */
@Data
public class DailyTestQuestionDTO {
    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 答题记录ID
     */
    private Long recordId;

    /**
     * 距今天数
     */
    private Integer daysAgo;

    /**
     * 是否错题
     */
    private Boolean isError;
}
