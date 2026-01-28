package org.example.kaoyanplatform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("question_paper_rel")
public class QuestionPaperRel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long paperId;
    private Long questionId;
    private Integer sortOrder;
    private BigDecimal scoreValue;
    private Integer type;

    // 主观题评分配置（仅用于 type=4 简答题/解答题）
    private Integer processScore;   // 过程分满分
    private Double processRatio;   // 过程分比例 (如 0.8 表示 80%)
    private Integer resultScore;    // 结果分满分
    private Double resultRatio;    // 结果分比例 (如 0.2 表示 20%)

    /**
     * 计算过程分值
     */
    public BigDecimal getProcessScoreValue() {
        if (processRatio == null || scoreValue == null) {
            return BigDecimal.ZERO;
        }
        return scoreValue.multiply(BigDecimal.valueOf(processRatio));
    }

    /**
     * 计算结果分值
     */
    public BigDecimal getResultScoreValue() {
        if (resultRatio == null || scoreValue == null) {
            return BigDecimal.ZERO;
        }
        return scoreValue.multiply(BigDecimal.valueOf(resultRatio));
    }
}
