package org.example.kaoyanplatform.entity.dto;

import lombok.Data;
import org.example.kaoyanplatform.entity.ExamSession;
import org.example.kaoyanplatform.entity.ExamPaper;
import org.example.kaoyanplatform.entity.Question;

import java.util.List;

@Data
public class ExamStartDTO {
    private ExamSession session;
    private ExamPaper paper;
    private List<Question> questions;
}
