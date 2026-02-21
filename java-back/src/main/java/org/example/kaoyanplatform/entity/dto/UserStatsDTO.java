package org.example.kaoyanplatform.entity.dto;

import lombok.Data;

/**
 * 用户统计数据DTO
 */
@Data
public class UserStatsDTO {
    /**
     * 总答题数
     */
    private Long total;

    /**
     * 正确题数
     */
    private Long correct;

    /**
     * 正确率（百分比）
     */
    private Double accuracy;

    /**
     * 总时长（秒）
     */
    private Object totalTime;
}
