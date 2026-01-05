package org.example.kaoyanplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.kaoyanplatform.entity.MapQuestionSubject;
import org.example.kaoyanplatform.entity.Question;
import org.example.kaoyanplatform.mapper.MapQuestionSubjectMapper;
import org.example.kaoyanplatform.mapper.QuestionMapper;
import org.example.kaoyanplatform.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目Service实现类
 * 使用映射表（map_question_subject）管理题目与科目的关系
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Autowired
    private MapQuestionSubjectMapper mapQuestionSubjectMapper;

    @Override
    public List<Question> getQuestionsBySubjectIds(List<Integer> subjectIds) {
        if (subjectIds == null || subjectIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 使用新的映射表 map_question_subject
        QueryWrapper<MapQuestionSubject> qsWrapper = new QueryWrapper<>();
        qsWrapper.in("subject_id", subjectIds);
        qsWrapper.select("question_id");
        List<Object> qIds = mapQuestionSubjectMapper.selectObjs(qsWrapper);

        if (qIds == null || qIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> questionIds = qIds.stream().map(o -> Long.valueOf(o.toString())).collect(Collectors.toList());
        return listByIds(questionIds);
    }
}