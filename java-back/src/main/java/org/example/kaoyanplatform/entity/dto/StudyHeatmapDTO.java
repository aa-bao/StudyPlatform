package org.example.kaoyanplatform.entity.dto;

import lombok.Data;

/**
 * 学习热力图数据传输对象
 * 用于展示用户每日学习时长的热力图数据
 */
@Data
public class StudyHeatmapDTO {
    /**
     * 记录日期，格式: "2026-01-29"
     */
    private String recordDate;

    /**
     * 总学习时长（秒）
     */
    private Integer totalDuration;

    /**
     * 答题数量
     */
    private Integer questionCount;
}
