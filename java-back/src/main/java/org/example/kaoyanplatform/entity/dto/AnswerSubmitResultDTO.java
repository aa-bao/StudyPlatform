package org.example.kaoyanplatform.entity.dto;

import lombok.Data;

/**
 * 提交答案返回结果DTO
 */
@Data
public class AnswerSubmitResultDTO {
    /**
     * 是否正确（1=正确，0=错误）
     */
    private Integer isCorrect;

    /**
     * 正确答案
     */
    private String correctAnswer;

    /**
     * 答案解析
     */
    private String analysis;
}
