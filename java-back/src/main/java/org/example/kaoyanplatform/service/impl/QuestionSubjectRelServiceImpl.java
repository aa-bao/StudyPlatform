package org.example.kaoyanplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.kaoyanplatform.entity.QuestionSubjectRel;
import org.example.kaoyanplatform.mapper.QuestionSubjectRelMapper;
import org.example.kaoyanplatform.service.QuestionSubjectRelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 题目-科目映射表Service实现类
 */
@Service
public class QuestionSubjectRelServiceImpl extends ServiceImpl<QuestionSubjectRelMapper, QuestionSubjectRel>
        implements QuestionSubjectRelService {

    @Autowired
    private QuestionSubjectRelMapper mapQuestionSubjectMapper;

    @Override
    public List<Integer> getSubjectIdsByQuestionId(Long questionId) {
        return mapQuestionSubjectMapper.getSubjectIdsByQuestionId(questionId);
    }

    @Override
    public List<Long> getQuestionIdsBySubjectId(Integer subjectId) {
        return mapQuestionSubjectMapper.getQuestionIdsBySubjectId(subjectId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addQuestionSubjectRelation(Long questionId, Integer subjectId) {
        // 检查是否已存在关联
        QueryWrapper<QuestionSubjectRel> wrapper = new QueryWrapper<>();
        wrapper.eq("question_id", questionId);
        wrapper.eq("subject_id", subjectId);
        if (count(wrapper) > 0) {
            return true; // 已存在，直接返回成功
        }

        QuestionSubjectRel relation = new QuestionSubjectRel();
        relation.setQuestionId(questionId);
        relation.setSubjectId(subjectId);
        return save(relation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAddQuestionSubjectRelations(Long questionId, List<Integer> subjectIds) {
        if (subjectIds == null || subjectIds.isEmpty()) {
            return true;
        }

        // 先删除旧的关联
        removeAllQuestionSubjectRelations(questionId);

        // 批量添加新关联
        for (Integer subjectId : subjectIds) {
            QuestionSubjectRel relation = new QuestionSubjectRel();
            relation.setQuestionId(questionId);
            relation.setSubjectId(subjectId);
            if (!save(relation)) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeQuestionSubjectRelation(Long questionId, Integer subjectId) {
        QueryWrapper<QuestionSubjectRel> wrapper = new QueryWrapper<>();
        wrapper.eq("question_id", questionId);
        wrapper.eq("subject_id", subjectId);
        return remove(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeAllQuestionSubjectRelations(Long questionId) {
        QueryWrapper<QuestionSubjectRel> wrapper = new QueryWrapper<>();
        wrapper.eq("question_id", questionId);
        return remove(wrapper);
    }
}
