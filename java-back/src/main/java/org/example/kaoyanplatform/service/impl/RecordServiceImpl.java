package org.example.kaoyanplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.kaoyanplatform.entity.AnswerRecord;
import org.example.kaoyanplatform.entity.ErrorQuestion;
import org.example.kaoyanplatform.entity.Question;
import org.example.kaoyanplatform.entity.dto.*;
import org.example.kaoyanplatform.mapper.AnswerRecordMapper;
import org.example.kaoyanplatform.service.ErrorQuestionService;
import org.example.kaoyanplatform.service.QuestionService;
import org.example.kaoyanplatform.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 答题记录服务实现类
 */
@Service
public class RecordServiceImpl extends ServiceImpl<AnswerRecordMapper, AnswerRecord> implements RecordService {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ErrorQuestionService mistakeRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AnswerSubmitResultDTO submitAnswer(AnswerRecord examRecord) {
        // 1. 查询题目
        Question question = questionService.getById(examRecord.getQuestionId());
        if (question == null) {
            throw new IllegalArgumentException("题目不存在");
        }

        // 2. 判题逻辑：去除空格、转大写
        String dbAns = question.getAnswer().replaceAll("[,\\s]", "").toUpperCase();
        String userAns = examRecord.getUserAnswer().replaceAll("[,\\s]", "").toUpperCase();
        boolean isRight = dbAns.equals(userAns);

        examRecord.setIsCorrect(isRight ? 1 : 0);
        examRecord.setScore(isRight ? 5 : 0);
        examRecord.setCreateTime(LocalDateTime.now());
        
        // 设置默认来源
        if (examRecord.getSource() == null || examRecord.getSource().isEmpty()) {
            examRecord.setSource("single_practice");
        }

        // 3. 保存答题记录
        save(examRecord);

        // 4. 错题本逻辑
        if (!isRight) {
            updateMistakeBook(examRecord);
        }

        // 5. 构造返回结果
        AnswerSubmitResultDTO result = new AnswerSubmitResultDTO();
        result.setIsCorrect(examRecord.getIsCorrect());
        result.setCorrectAnswer(question.getAnswer());
        result.setAnalysis(question.getAnalysis());

        return result;
    }

    @Override
    public UserStatsDTO getUserStats(Integer userId) {
        // 使用 Lambda 获取统计
        long total = count(new LambdaQueryWrapper<AnswerRecord>().eq(AnswerRecord::getUserId, userId));
        long correct = count(new LambdaQueryWrapper<AnswerRecord>()
                .eq(AnswerRecord::getUserId, userId)
                .eq(AnswerRecord::getIsCorrect, 1));

        double accuracy = (total == 0) ? 0 : Math.round((double) correct / total * 100);

        // 计算总时长
        QueryWrapper<AnswerRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("IFNULL(sum(duration), 0) as totalDuration").eq("user_id", userId);
        Map<String, Object> sumMap = getMap(queryWrapper);
        Object totalTime = sumMap != null ? sumMap.get("totalDuration") : 0;

        UserStatsDTO stats = new UserStatsDTO();
        stats.setTotal(total);
        stats.setCorrect(correct);
        stats.setAccuracy(accuracy);
        stats.setTotalTime(totalTime);

        return stats;
    }

    @Override
    public List<AnswerRecord> getRecentRecords(Integer userId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return list(new LambdaQueryWrapper<AnswerRecord>()
                .eq(AnswerRecord::getUserId, userId)
                .orderByDesc(AnswerRecord::getCreateTime)
                .last("limit " + limit));
    }

    @Override
    public List<Map<String, Object>> getDailyTestQuestions(Long userId) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        LambdaQueryWrapper<AnswerRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnswerRecord::getUserId, userId)
                .ge(AnswerRecord::getCreateTime, sevenDaysAgo)
                .lt(AnswerRecord::getCreateTime, todayStart)
                .orderByDesc(AnswerRecord::getCreateTime);

        List<AnswerRecord> recentRecords = list(wrapper);

        if (recentRecords.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> errorQuestionIds = getUserErrorQuestionIds(userId);
        Set<Long> todayAnsweredIds = getTodayAnsweredQuestionIds(userId.intValue());
        List<DailyTestQuestionDTO> prioritizedQuestions = prioritizeQuestions(recentRecords, errorQuestionIds, todayAnsweredIds);

        if (prioritizedQuestions.isEmpty()) {
            return new ArrayList<>();
        }

        List<DailyTestQuestionDTO> limitedQuestions = limitQuestionCount(prioritizedQuestions, 5, 10);

        return buildQuestionDetails(limitedQuestions);
    }

    @Override
    public DailyTestStatusDTO getDailyTestStatus(Long userId) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        Set<Long> todayAnsweredIds = getTodayAnsweredQuestionIds(userId.intValue());
        long todayAnsweredCount = todayAnsweredIds.size();

        LambdaQueryWrapper<AnswerRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnswerRecord::getUserId, userId)
                .ge(AnswerRecord::getCreateTime, sevenDaysAgo)
                .lt(AnswerRecord::getCreateTime, todayStart);

        List<AnswerRecord> recentRecords = list(wrapper);

        int eligiblePoolSize = 0;
        long remainingCount = 0;

        if (!recentRecords.isEmpty()) {
            Set<Long> errorQuestionIds = getUserErrorQuestionIds(userId);
            List<DailyTestQuestionDTO> prioritizedQuestions = prioritizeQuestions(recentRecords, errorQuestionIds, todayAnsweredIds);
            remainingCount = prioritizedQuestions.size();

            // 计算总池大小
            Set<Long> totalPoolIds = new HashSet<>();
            for (AnswerRecord r : recentRecords) {
                totalPoolIds.add(r.getQuestionId());
            }
            eligiblePoolSize = totalPoolIds.size();
        }

        DailyTestStatusDTO status = new DailyTestStatusDTO();
        status.setRemainingCount(remainingCount);
        status.setCompleted(remainingCount == 0);
        status.setEligiblePoolSize(eligiblePoolSize);
        status.setTodayAnsweredCount(todayAnsweredCount);

        return status;
    }

    @Override
    public List<Map<String, Object>> getDailyTestAccuracyStats(Long userId) {
        LocalDateTime sevenDaysAgo = LocalDate.now().minusDays(6).atStartOfDay();

        // 仅查询“每日测试”模式下的答题记录
        LambdaQueryWrapper<AnswerRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnswerRecord::getUserId, userId)
                .eq(AnswerRecord::getSource, "daily_test")
                .ge(AnswerRecord::getCreateTime, sevenDaysAgo)
                .orderByAsc(AnswerRecord::getCreateTime);

        List<AnswerRecord> records = list(wrapper);

        // 按日期分组
        Map<LocalDate, List<AnswerRecord>> groupedByDate = records.stream()
                .collect(Collectors.groupingBy(r -> r.getCreateTime().toLocalDate()));

        List<Map<String, Object>> statsList = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            List<AnswerRecord> dayRecords = groupedByDate.getOrDefault(date, new ArrayList<>());

            double accuracy = 0;
            if (!dayRecords.isEmpty()) {
                long correctCount = dayRecords.stream().filter(r -> r.getIsCorrect() == 1).count();
                accuracy = Math.round((double) correctCount / dayRecords.size() * 100);
            }

            Map<String, Object> dayStat = new HashMap<>();
            dayStat.put("date", date.getMonthValue() + "/" + date.getDayOfMonth());
            dayStat.put("accuracy", accuracy);
            statsList.add(dayStat);
        }

        return statsList;
    }

    @Override
    public List<StudyHeatmapDTO> getDailyStudyStats(Long userId, Integer days) {
        // 1. 计算日期范围（过去N天）
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);

        // 2. 查询该用户的答题记录
        LambdaQueryWrapper<AnswerRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnswerRecord::getUserId, userId)
                .ge(AnswerRecord::getCreateTime, startDate)
                .orderByAsc(AnswerRecord::getCreateTime);

        List<AnswerRecord> records = list(wrapper);

        // 3. 按日期分组聚合
        Map<LocalDate, List<AnswerRecord>> groupedByDate = records.stream()
                .collect(Collectors.groupingBy(r -> r.getCreateTime().toLocalDate()));

        // 4. 构建返回数据
        return groupedByDate.entrySet().stream()
                .map(entry -> {
                    StudyHeatmapDTO dto = new StudyHeatmapDTO();
                    dto.setRecordDate(entry.getKey().toString());
                    dto.setTotalDuration(entry.getValue().stream()
                            .mapToInt(AnswerRecord::getDuration).sum());
                    dto.setQuestionCount(entry.getValue().size());
                    return dto;
                })
                .sorted(Comparator.comparing(StudyHeatmapDTO::getRecordDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getHotMistakes(Integer userId, Integer limit) {
        // 1. 查询用户的所有答题记录
        LambdaQueryWrapper<AnswerRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnswerRecord::getUserId, userId);
        List<AnswerRecord> records = list(wrapper);

        if (records.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 按题目ID分组统计
        Map<Long, List<AnswerRecord>> groupedByQuestion = records.stream()
                .collect(Collectors.groupingBy(AnswerRecord::getQuestionId));

        // 3. 计算每个题目的错误率和错误次数
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Long, List<AnswerRecord>> entry : groupedByQuestion.entrySet()) {
            Long questionId = entry.getKey();
            List<AnswerRecord> questionRecords = entry.getValue();

            long totalCount = questionRecords.size();
            long errorCount = questionRecords.stream()
                    .filter(r -> r.getIsCorrect() == 0)
                    .count();

            if (errorCount > 0) { // 只统计有错误的题目
                double errorRate = (errorCount * 100.0) / totalCount;

                Map<String, Object> item = new HashMap<>();
                item.put("questionId", questionId);
                item.put("errorRate", errorRate);
                item.put("errorCount", (int) errorCount);

                // 获取题目内容和科目名称
                Question question = questionService.getById(questionId);
                if (question != null) {
                    item.put("content", question.getContent());
                    item.put("subjectName", getSubjectName(questionId));
                }

                result.add(item);
            }
        }

        // 4. 按错误率降序排序,取前N条
        return result.stream()
                .sorted((a, b) -> {
                    Number rateA = (Number) a.get("errorRate");
                    Number rateB = (Number) b.get("errorRate");
                    return Double.compare(rateB.doubleValue(), rateA.doubleValue());
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getTodayStats(Integer userId) {
        Map<String, Object> result = new HashMap<>();

        // 今日开始时间
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        // 查询今日答题记录
        LambdaQueryWrapper<AnswerRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnswerRecord::getUserId, userId)
                .ge(AnswerRecord::getCreateTime, todayStart);
        List<AnswerRecord> todayRecords = list(wrapper);

        // 今日刷题数
        int todayQuestions = todayRecords.size();

        // 今日学习时长(秒转小时)
        int totalSeconds = todayRecords.stream()
                .mapToInt(AnswerRecord::getDuration)
                .sum();
        double todayStudyHours = totalSeconds / 3600.0;

        // 今日新增错题数
        long newMistakes = todayRecords.stream()
                .filter(r -> r.getIsCorrect() == 0)
                .map(AnswerRecord::getQuestionId)
                .distinct()
                .count();

        result.put("todayQuestions", todayQuestions);
        result.put("todayStudyHours", Math.round(todayStudyHours * 10.0) / 10.0);
        result.put("newMistakes", (int) newMistakes);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMistakeBook(AnswerRecord examRecord) {
        LambdaQueryWrapper<ErrorQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ErrorQuestion::getUserId, examRecord.getUserId())
                .eq(ErrorQuestion::getQuestionId, examRecord.getQuestionId())
                .last("limit 1");

        ErrorQuestion exist = mistakeRecordService.getOne(wrapper);
        if (exist == null) {
            ErrorQuestion wb = new ErrorQuestion();
            wb.setUserId(examRecord.getUserId().intValue());
            wb.setQuestionId(examRecord.getQuestionId().intValue());
            wb.setErrorCount(1);
            wb.setUpdateTime(LocalDateTime.now());
            mistakeRecordService.save(wb);
        } else {
            exist.setErrorCount(exist.getErrorCount() + 1);
            exist.setUpdateTime(LocalDateTime.now());
            mistakeRecordService.updateById(exist);
        }
    }

    // ==================== 私有辅助方法 ====================

    private Set<Long> getUserErrorQuestionIds(Long userId) {
        LambdaQueryWrapper<ErrorQuestion> errorWrapper = new LambdaQueryWrapper<>();
        errorWrapper.eq(ErrorQuestion::getUserId, userId);
        return mistakeRecordService.list(errorWrapper).stream()
                .map(eq -> Long.valueOf(eq.getQuestionId()))
                .collect(Collectors.toSet());
    }

    private Set<Long> getTodayAnsweredQuestionIds(Integer userId) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LambdaQueryWrapper<AnswerRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnswerRecord::getUserId, userId)
                .ge(AnswerRecord::getCreateTime, todayStart);
        return list(wrapper).stream()
                .map(AnswerRecord::getQuestionId)
                .collect(Collectors.toSet());
    }

    private List<DailyTestQuestionDTO> prioritizeQuestions(List<AnswerRecord> records, Set<Long> errorQuestionIds, Set<Long> todayAnsweredIds) {
        List<DailyTestQuestionDTO> priorityHigh = new ArrayList<>();
        List<DailyTestQuestionDTO> priorityMedium = new ArrayList<>();
        List<DailyTestQuestionDTO> priorityLow = new ArrayList<>();
        Set<Long> processedIds = new HashSet<>();

        for (AnswerRecord record : records) {
            Long questionId = record.getQuestionId();
            if (processedIds.contains(questionId) || todayAnsweredIds.contains(questionId)) continue;

            int daysAgo = (int) ChronoUnit.DAYS.between(record.getCreateTime().toLocalDate(), LocalDate.now());
            boolean isError = errorQuestionIds.contains(questionId);
            DailyTestQuestionDTO dto = buildDailyTestDTO(questionId, record.getId(), daysAgo, isError);

            if (daysAgo >= 4 && daysAgo <= 7 && isError) {
                priorityHigh.add(dto);
                processedIds.add(questionId);
            } else if (daysAgo >= 1 && daysAgo <= 3 && isError) {
                priorityMedium.add(dto);
                processedIds.add(questionId);
            } else if (daysAgo >= 1 && daysAgo <= 7) {
                priorityLow.add(dto);
                processedIds.add(questionId);
            }
        }

        List<DailyTestQuestionDTO> result = new ArrayList<>();
        result.addAll(priorityHigh);
        result.addAll(priorityMedium);
        result.addAll(priorityLow);
        return result;
    }

    private DailyTestQuestionDTO buildDailyTestDTO(Long questionId, Long recordId, int daysAgo, boolean isError) {
        DailyTestQuestionDTO dto = new DailyTestQuestionDTO();
        dto.setQuestionId(questionId);
        dto.setRecordId(recordId);
        dto.setDaysAgo(daysAgo);
        dto.setIsError(isError);
        return dto;
    }

    private List<DailyTestQuestionDTO> limitQuestionCount(List<DailyTestQuestionDTO> questions, int min, int max) {
        int limit = Math.min(questions.size(), max);
        if (limit < min && !questions.isEmpty()) {
            limit = Math.min(questions.size(), min);
        }
        return questions.subList(0, limit);
    }

    private List<Map<String, Object>> buildQuestionDetails(List<DailyTestQuestionDTO> dtos) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (DailyTestQuestionDTO dto : dtos) {
            Question question = questionService.getById(dto.getQuestionId());
            if (question != null) {
                result.add(convertToMap(question, dto));
            }
        }
        return result;
    }

    private Map<String, Object> convertToMap(Question question, DailyTestQuestionDTO dto) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", question.getId());
        map.put("type", question.getType());
        map.put("difficulty", question.getDifficulty());
        map.put("content", question.getContent());
        map.put("options", question.getOptions());
        map.put("answer", question.getAnswer());
        map.put("analysis", question.getAnalysis());
        map.put("tags", question.getTags());
        map.put("source", question.getSource());
        map.put("daysAgo", dto.getDaysAgo());
        map.put("isError", dto.getIsError());
        map.put("recordId", dto.getRecordId());
        return map;
    }

    private String getSubjectName(Long questionId) {
        // 这里需要通过question_subject_rel表查询
        // 简化实现,返回"未知科目",实际应该查询数据库
        return "数学";
    }
}