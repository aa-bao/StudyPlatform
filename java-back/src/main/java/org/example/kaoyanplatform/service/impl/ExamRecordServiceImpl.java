package org.example.kaoyanplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.kaoyanplatform.entity.*;
import org.example.kaoyanplatform.entity.dto.ExamRecordDetailDTO;
import org.example.kaoyanplatform.entity.dto.SubjectiveQuestionDetailDTO;
import org.example.kaoyanplatform.entity.dto.UserGradingDTO;
import org.example.kaoyanplatform.mapper.ExamRecordMapper;
import org.example.kaoyanplatform.mapper.QuestionPaperRelMapper;
import org.example.kaoyanplatform.service.ExamRecordService;
import org.example.kaoyanplatform.mapper.ExamSessionMapper;
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

    @Autowired
    private org.example.kaoyanplatform.mapper.QuestionMapper questionMapper;

    @Autowired
    private ExamSessionMapper examSessionMapper;

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

        // 查询考试会话获取paperId
        ExamSession examSession = examSessionMapper.selectById(sessionId);
        if (examSession == null || examSession.getPaperId() == null) {
            return Collections.emptyList();
        }
        Long paperId = examSession.getPaperId();

        // 查询该会话的所有答题记录
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getSessionId, sessionId);
        List<ExamRecord> records = this.list(wrapper);

        if (records.isEmpty()) {
            return Collections.emptyList();
        }

        return records.stream()
            .filter(record -> {
                // 查询题目类型，筛选出主观题（type=4）
                Question question = questionMapper.selectById(record.getQuestionId());
                return question != null && question.getType() == 4;
            })
            .map(record -> {
                SubjectiveQuestionDetailDTO dto = new SubjectiveQuestionDetailDTO();
                dto.setQuestionId(record.getQuestionId());
                dto.setUserAnswer(record.getUserAnswer());
                dto.setUserProcessGrading(record.getUserProcessGrading());
                dto.setUserResultGrading(record.getUserResultGrading());

                // 查询题目信息
                Question question = questionMapper.selectById(record.getQuestionId());
                if (question != null) {
                    dto.setQuestionContent(question.getContent());
                    dto.setStandardAnswer(question.getAnswer());
                    dto.setQuestionType(question.getType());
                }

                // 查询试卷关联信息
                LambdaQueryWrapper<QuestionPaperRel> relWrapper = new LambdaQueryWrapper<>();
                relWrapper.eq(QuestionPaperRel::getPaperId, paperId)
                          .eq(QuestionPaperRel::getQuestionId, record.getQuestionId());
                QuestionPaperRel rel = questionPaperRelMapper.selectOne(relWrapper);

                if (rel != null) {
                    dto.setSortOrder(rel.getSortOrder());
                    dto.setTotalScore(rel.getScoreValue());
                    dto.setProcessScoreValue(rel.getProcessScoreValue());
                    dto.setResultScoreValue(rel.getResultScoreValue());
                    dto.setProcessRatio(rel.getProcessRatio());
                    dto.setResultRatio(rel.getResultRatio());
                }

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

    @Override
    public List<ExamRecordDetailDTO> getSessionExamDetailList(Long sessionId) {
        if (sessionId == null) {
            return Collections.emptyList();
        }

        log.info("获取会话答题详情 - sessionId: {}", sessionId);

        // 查询考试会话获取paperId
        ExamSession examSession = examSessionMapper.selectById(sessionId);
        if (examSession == null || examSession.getPaperId() == null) {
            log.warn("考试会话不存在或paperId为空 - sessionId: {}", sessionId);
            return Collections.emptyList();
        }
        Long paperId = examSession.getPaperId();
        log.info("获取到paperId: {}", paperId);

        // 查询该会话的所有答题记录
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getSessionId, sessionId);
        List<ExamRecord> records = this.list(wrapper);

        if (records.isEmpty()) {
            log.warn("没有找到答题记录 - sessionId: {}", sessionId);
            return Collections.emptyList();
        }

        log.info("找到 {} 条答题记录", records.size());

        List<ExamRecordDetailDTO> result = records.stream().map(record -> {
            ExamRecordDetailDTO dto = new ExamRecordDetailDTO();
            dto.setId(record.getId());
            dto.setSessionId(record.getSessionId());
            dto.setQuestionId(record.getQuestionId());
            dto.setUserAnswer(record.getUserAnswer());
            dto.setIsCorrect(record.getIsCorrect());
            dto.setScoreEarned(record.getScoreEarned());
            dto.setDurationSeconds(record.getDurationSeconds());
            dto.setAiFeedback(record.getAiFeedback());
            dto.setKnowledgePointId(record.getKnowledgePointId());
            dto.setUserProcessGrading(record.getUserProcessGrading());
            dto.setUserResultGrading(record.getUserResultGrading());
            dto.setGradingTime(record.getGradingTime());
            dto.setCreateTime(record.getCreateTime());

            // 查询题目信息
            Question question = questionMapper.selectById(record.getQuestionId());
            if (question != null) {
                dto.setQuestionContent(question.getContent());
                String answer = question.getAnswer();
                dto.setStandardAnswer(answer);
                dto.setQuestionType(question.getType());
                log.debug("题目ID: {}, 标准答案: {}, 题型: {}", record.getQuestionId(), answer, question.getType());
            } else {
                log.warn("未找到题目 - questionId: {}", record.getQuestionId());
            }

            // 查询试卷关联信息
            LambdaQueryWrapper<QuestionPaperRel> relWrapper = new LambdaQueryWrapper<>();
            relWrapper.eq(QuestionPaperRel::getPaperId, paperId)
                      .eq(QuestionPaperRel::getQuestionId, record.getQuestionId());
            QuestionPaperRel rel = questionPaperRelMapper.selectOne(relWrapper);

            if (rel != null) {
                dto.setSortOrder(rel.getSortOrder());
                dto.setTotalScore(rel.getScoreValue());
                dto.setProcessScoreValue(rel.getProcessScoreValue());
                dto.setResultScoreValue(rel.getResultScoreValue());
                dto.setProcessRatio(rel.getProcessRatio());
                dto.setResultRatio(rel.getResultRatio());
            }

            return dto;
        }).collect(Collectors.toList());

        log.info("返回 {} 条答题详情", result.size());
        return result;
    }
}