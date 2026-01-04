package org.example.kaoyanplatform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.util.List;

@Data
@TableName(value = "tb_question", autoResultMap = true)
public class Question {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer subjectId;
    private Integer type; // 1-单选, 2-多选
    private String content;

    // 指定使用 JacksonTypeHandler 来处理 JSON 字段
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> options;
    private String answer;
    private String analysis;
    private Integer difficulty;

    // 指定使用 JacksonTypeHandler 来处理 JSON 字段 (存储标签数组)
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    @TableField(exist = false)
    private java.time.LocalDateTime createTime; // 用于前端展示错题时间

    private Integer bookId; // 习题册ID
}