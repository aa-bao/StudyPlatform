package org.example.kaoyanplatform.entity.dto;

import lombok.Data;

/**
 * 每日测试状态DTO
 */
@Data
public class DailyTestStatusDTO {
    /**
     * 剩余题目数
     */
    private Long remainingCount;

    /**
     * 是否完成
     */
    private Boolean completed;

    /**
     * 可用题目池大小
     */
    private Integer eligiblePoolSize;

    /**
     * 今日已答题数
     */
    private Long todayAnsweredCount;
}
