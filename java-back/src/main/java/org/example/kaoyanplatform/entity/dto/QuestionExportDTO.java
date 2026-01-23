package org.example.kaoyanplatform.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 题目导出请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionExportDTO {

    /** 导出模式：1-仅题目，2-题目+答案解析 */
    private Integer mode;

    /** 习题册ID（可选，导出整个习题册） */
    private Integer bookId;

    /** 试卷ID（可选，导出整个试卷） */
    private String paperId;

    /** 自定义题目ID列表（可选，导出指定题目） */
    private List<Long> questionIds;

    /** 科目ID（可选，按科目筛选） */
    private Integer subjectId;

    /** 是否包含难度 */
    private Boolean includeDifficulty;

    /** 是否包含标签 */
    private Boolean includeTags;

    /** 是否包含来源 */
    private Boolean includeSource;

    /** 每页题目数量（用于分页，默认10题） */
    private Integer questionsPerPage;
}
