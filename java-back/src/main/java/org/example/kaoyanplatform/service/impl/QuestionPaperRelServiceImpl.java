package org.example.kaoyanplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.kaoyanplatform.entity.Question;
import org.example.kaoyanplatform.entity.QuestionPaperRel;
import org.example.kaoyanplatform.mapper.QuestionPaperRelMapper;
import org.example.kaoyanplatform.service.QuestionPaperRelService;
import org.example.kaoyanplatform.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuestionPaperRelServiceImpl extends ServiceImpl<QuestionPaperRelMapper, QuestionPaperRel> implements QuestionPaperRelService {

    @Autowired
    @Lazy
    private QuestionService questionService;

    @Override
    public List<Question> getQuestionsWithDetails(String paperId) {
        LambdaQueryWrapper<QuestionPaperRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionPaperRel::getPaperId, Long.parseLong(paperId))
               .orderByAsc(QuestionPaperRel::getSortOrder);

        List<QuestionPaperRel> mappings = list(wrapper);

        if (mappings == null || mappings.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> questionIds = mappings.stream()
                .map(QuestionPaperRel::getQuestionId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<Question> questionWrapper = new LambdaQueryWrapper<>();
        questionWrapper.in(Question::getId, questionIds);

        List<Question> questions = questionService.list(questionWrapper);

        // 将试卷关联信息（题号、分值、题型）填充到Question对象中
        // 创建questionId到Question的映射，方便快速查找
        java.util.Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(
                        Question::getId,
                        q -> q,
                        (q1, q2) -> q1 // 如果有重复，保留第一个
                ));

        // 按照mappings的顺序（即sortOrder顺序）构建结果列表
        List<Question> sortedQuestions = new java.util.ArrayList<>(mappings.size());
        for (QuestionPaperRel mapping : mappings) {
            Question question = questionMap.get(Long.valueOf(mapping.getQuestionId()));
            if (question != null) {
                // 设置试卷相关字段
                question.setSortOrder(mapping.getSortOrder());
                question.setScoreValue(mapping.getScoreValue());
                question.setPaperType(mapping.getType()); // 试卷中的题型（可能覆盖原题型）
                sortedQuestions.add(question);
            }
        }

        return sortedQuestions;
    }

    @Override
    public boolean batchUpdateQuestionOrder(String paperId, Map<String, Integer> orderMap) {
        // orderMap: {questionId: sortOrder}
        for (Map.Entry<String, Integer> entry : orderMap.entrySet()) {
            String questionId = entry.getKey();
            Integer sortOrder = entry.getValue();

            LambdaQueryWrapper<QuestionPaperRel> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(QuestionPaperRel::getPaperId, paperId)
                   .eq(QuestionPaperRel::getQuestionId, questionId);

            QuestionPaperRel mapping = this.getOne(wrapper);
            if (mapping != null) {
                mapping.setSortOrder(sortOrder);
                this.updateById(mapping);
            }
        }

        return true;
    }
}