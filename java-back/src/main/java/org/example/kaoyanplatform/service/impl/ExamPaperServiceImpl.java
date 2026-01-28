package org.example.kaoyanplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.kaoyanplatform.entity.ExamPaper;
import org.example.kaoyanplatform.mapper.ExamPaperMapper;
import org.example.kaoyanplatform.service.ExamPaperService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.kaoyanplatform.entity.QuestionPaperRel;
import org.example.kaoyanplatform.service.QuestionPaperRelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ExamPaperServiceImpl extends ServiceImpl<ExamPaperMapper, ExamPaper> implements ExamPaperService {
    
    @Autowired
    private QuestionPaperRelService mapPaperQuestionService;

    @Override
    public boolean deletePaperWithMappings(String id) {
        // 先删除试卷关联的所有题目关系
        List<QuestionPaperRel> mappings = mapPaperQuestionService.list(
                new LambdaQueryWrapper<QuestionPaperRel>().eq(QuestionPaperRel::getPaperId, id)
        );
        
        if (!mappings.isEmpty()) {
            mapPaperQuestionService.removeByIds(
                    mappings.stream().map(QuestionPaperRel::getId).toList()
            );
        }
        
        // 再删除试卷本身
        return this.removeById(id);
    }
}