package org.example.kaoyanplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.kaoyanplatform.entity.Question;
import org.example.kaoyanplatform.entity.QuestionPaperRel;

import java.util.List;
import java.util.Map;

public interface QuestionPaperRelService extends IService<QuestionPaperRel> {

    List<Question> getQuestionsWithDetails(String paperId);
    
    /**
     * 批量更新试卷中题目的顺序
     * @param paperId 试卷ID
     * @param orderMap 题目ID到排序序号的映射
     * @return 更新结果
     */
    boolean batchUpdateQuestionOrder(String paperId, Map<String, Integer> orderMap);
}