package org.example.kaoyanplatform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "exam_paper", autoResultMap = true)
public class ExamPaper {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private Integer examSpecId;
    private Integer totalScore;
    private Integer timeLimit;
    private Integer paperType;
    private Integer year;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<ExamPaperSection> structureJson;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Data
    public static class ExamPaperSection {
        private String name;
        private Integer start;
        private Integer end;
    }
}
