package org.example.kaoyanplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.kaoyanplatform.entity.*;
import org.example.kaoyanplatform.entity.dto.SubjectiveQuestionDetailDTO;
import org.example.kaoyanplatform.entity.dto.UserGradingDTO;
import org.example.kaoyanplatform.mapper.ExamRecordMapper;
import org.example.kaoyanplatform.mapper.QuestionPaperRelMapper;
import org.example.kaoyanplatform.service.ExamRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExamRecordServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamRecordService {

    @Autowired
    private QuestionPaperRelMapper questionPaperRelMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveUserGrading(UserGradingDTO dto) {
        if (dto == null || dto.getSessionId() == null || dto.getGradingItems() == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        int updateCount = 0;
        LocalDateTime now = LocalDateTime.now();

        for (UserGradingDTO.SubjectiveGradingItem item : dto.getGradingItems()) {
            LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ExamRecord::getSessionId, dto.getSessionId())
                   .eq(ExamRecord::getQuestionId, item.getQuestionId());

            ExamRecord record = this.getOne(wrapper);
            if (record != null) {
                record.setUserProcessGrading(item.getProcessGrading());
                record.setUserResultGrading(item.getResultGrading());
                record.setGradingTime(now);

                // 计算得分
                QuestionPaperRel rel = questionPaperRelMapper.selectOne(
                    new LambdaQueryWrapper<QuestionPaperRel>()
                        .eq(QuestionPaperRel::getPaperId, record.getSessionId()) // 这里需要优化，实际应该从session获取paperId
                        .eq(QuestionPaperRel::getQuestionId, item.getQuestionId())
                );

                if (rel != null) {
                    BigDecimal score = BigDecimal.ZERO;
                    if (item.getProcessGrading() != null && item.getProcessGrading() == 1) {
                        score = score.add(rel.getProcessScoreValue());
                    }
                    if (item.getResultGrading() != null && item.getResultGrading() == 1) {
                        score = score.add(rel.getResultScoreValue());
                    }
                    record.setScoreEarned(score);
                }

                this.updateById(record);
                updateCount++;
            }
        }

        log.info("保存用户自评结果：sessionId={}, 更新记录数={}", dto.getSessionId(), updateCount);
        return updateCount;
    }

    @Override
    public List<SubjectiveQuestionDetailDTO> getSubjectiveQuestionsBySessionId(Long sessionId) {
        if (sessionId == null) {
            return Collections.emptyList();
        }

        // 查询该会话的所有主观题记录
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getSessionId, sessionId)
               .in(ExamRecord::getIsCorrect, Arrays.asList(3, null)); // 3表示待批改（主观题）

        List<ExamRecord> records = this.list(wrapper);
        if (records.isEmpty()) {
            return Collections.emptyList();
        }

        return records.stream().map(record -> {
            SubjectiveQuestionDetailDTO dto = new SubjectiveQuestionDetailDTO();
            dto.setQuestionId(record.getQuestionId());
            dto.setUserAnswer(record.getUserAnswer());
            dto.setUserProcessGrading(record.getUserProcessGrading());
            dto.setUserResultGrading(record.getUserResultGrading());

            // 查询题目和试卷关联信息
            // 这里需要关联查询，简化处理
            // TODO: 实际实现需要从 Question 和 QuestionPaperRel 表获取完整信息

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> calculateScoreStatistics(Long sessionId) {
        Map<String, Object> result = new HashMap<>();

        // 查询所有答题记录
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getSessionId, sessionId);
        List<ExamRecord> records = this.list(wrapper);

        // 客观题统计（isCorrect = 0 或 1）
        List<ExamRecord> objectiveRecords = records.stream()
            .filter(r -> r.getIsCorrect() != null && Arrays.asList(0, 1).contains(r.getIsCorrect()))
            .collect(Collectors.toList());

        BigDecimal objectiveScore = objectiveRecords.stream()
            .map(ExamRecord::getScoreEarned)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 主观题统计（有用户自评数据）
        List<ExamRecord> subjectiveRecords = records.stream()
            .filter(r -> r.getUserProcessGrading() != null || r.getUserResultGrading() != null)
            .collect(Collectors.toList());

        BigDecimal subjectiveScore = subjectiveRecords.stream()
            .map(ExamRecord::getScoreEarned)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalScore = objectiveScore.add(subjectiveScore);

        result.put("objectiveScore", objectiveScore);
        result.put("subjectiveScore", subjectiveScore);
        result.put("totalScore", totalScore);
        result.put("objectiveCount", objectiveRecords.size());
        result.put("subjectiveCount", subjectiveRecords.size());

        return result;
    }
}