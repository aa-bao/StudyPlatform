package org.example.kaoyanplatform.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.kaoyanplatform.entity.AnswerRecord;
import org.example.kaoyanplatform.entity.QuestionSubjectRel;
import org.example.kaoyanplatform.entity.Subject;
import org.example.kaoyanplatform.entity.User;
import org.example.kaoyanplatform.entity.dto.UserStudyStatsDTO;
import org.example.kaoyanplatform.entity.dto.HomePageDataDTO;
import org.example.kaoyanplatform.mapper.QuestionSubjectRelMapper;
import org.example.kaoyanplatform.mapper.SubjectMapper;
import org.example.kaoyanplatform.mapper.UserMapper;
import org.example.kaoyanplatform.service.RecordService;
import org.example.kaoyanplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private QuestionSubjectRelMapper mapQuestionSubjectMapper;

    @Autowired
    private SubjectMapper subjectMapper;

    @Autowired
    private RecordService recordService;

    /**
     * 实现登录逻辑
     */
    @Override
    public User login(String username, String password) {
        // 1. 根据用户名查询用户
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));

        // 2. 校验密码 (使用 BCrypt 校验)
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            // 为了安全，返回前隐藏密码
            user.setPassword(null);
            return user;
        }
        return null;
    }

    /**
     * 实现注册逻辑
     */
    @Override
    public boolean register(User user) {
        // 1. 检查用户名是否存在
        User existUser = getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        if (existUser != null) {
            return false;
        }

        // 2. 设置默认昵称
        if (StrUtil.isBlank(user.getNickname())) {
            user.setNickname("考研人_" + RandomUtil.randomString(6));
        }

        // 3. 密码加密 (BCrypt)
        String hashedPwd = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPwd);

        // 4. 设置初始角色为学生
        user.setRole("student");

        // 5. 设置默认头像
        if (StrUtil.isBlank(user.getAvatar())) {
            user.setAvatar("http://localhost:8081/img/default-avatar.png");
        }

        // 6. 设置默认考研年份，确保 Dashboard 进度条有终点数据
        if (StrUtil.isBlank(user.getExamYear())) {
            user.setExamYear("27考研");
        }

        // create_time 将由 MyMetaObjectHandler 自动填充
        return save(user);
    }

    @Override
    public UserStudyStatsDTO getUserStudyStats(Long userId) {
        // 1. 查询用户基本信息
        User user = getById(userId);
        if (user == null) {
            return null;
        }

        UserStudyStatsDTO statsDTO = new UserStudyStatsDTO();
        statsDTO.setUserId(user.getId());
        statsDTO.setUsername(user.getUsername());
        statsDTO.setNickname(user.getNickname());
        statsDTO.setTargetSchool(user.getTargetSchool());
        statsDTO.setExamYear(user.getExamYear());

        // 2. 获取用户的所有答题记录
        LambdaQueryWrapper<AnswerRecord> answerWrapper = new LambdaQueryWrapper<>();
        answerWrapper.eq(AnswerRecord::getUserId, userId);
        List<AnswerRecord> answerRecords = recordService.list(answerWrapper);

        // 3. 构建题目ID到科目ID的映射（通过 question_subject_rel 表）
        if (!answerRecords.isEmpty()) {
            List<Long> questionIds = answerRecords.stream()
                    .map(AnswerRecord::getQuestionId)
                    .distinct()
                    .collect(Collectors.toList());

            QueryWrapper<QuestionSubjectRel> relWrapper = new QueryWrapper<>();
            relWrapper.in("question_id", questionIds);
            List<QuestionSubjectRel> subjectRels = mapQuestionSubjectMapper.selectList(relWrapper);

            // 构建 questionId -> subjectId 映射（一个题目可能对应多个科目，取第一个）
            Map<Long, Integer> questionToSubjectMap = new HashMap<>();
            for (QuestionSubjectRel rel : subjectRels) {
                if (!questionToSubjectMap.containsKey(rel.getQuestionId())) {
                    questionToSubjectMap.put(rel.getQuestionId(), rel.getSubjectId());
                }
            }

            // 构建 subjectId -> (答题记录列表) 映射
            Map<Integer, List<AnswerRecord>> subjectAnswersMap = new HashMap<>();
            for (AnswerRecord record : answerRecords) {
                Integer subjectId = questionToSubjectMap.get(record.getQuestionId());
                if (subjectId != null) {
                    subjectAnswersMap.computeIfAbsent(subjectId, k -> new ArrayList<>()).add(record);
                }
            }

            // 4. 获取所有顶级科目（level = 1）
            QueryWrapper<Subject> subjectWrapper = new QueryWrapper<>();
            subjectWrapper.eq("level", "1");
            List<Subject> topSubjects = subjectMapper.selectList(subjectWrapper);

            // 5. 构建科目统计数据
            List<UserStudyStatsDTO.SubjectStats> subjectStatsList = new ArrayList<>();

            // 获取每个科目的题目总数
            Map<Integer, Integer> subjectQuestionCount = getSubjectQuestionCount();

            for (Subject subject : topSubjects) {
                UserStudyStatsDTO.SubjectStats subjectStats = new UserStudyStatsDTO.SubjectStats();
                subjectStats.setSubjectId(subject.getId());
                subjectStats.setSubjectName(subject.getName());

                // 获取该科目的答题记录
                List<AnswerRecord> subjectRecords = subjectAnswersMap.getOrDefault(subject.getId(), new ArrayList<>());

                // 统计完成数和正确数
                int finishedCount = subjectRecords.size();
                int correctCount = (int) subjectRecords.stream()
                        .filter(r -> r.getIsCorrect() != null && r.getIsCorrect() == 1)
                        .count();

                subjectStats.setFinishedCount(finishedCount);
                subjectStats.setCorrectCount(correctCount);

                // 计算正确率
                if (finishedCount > 0) {
                    double accuracy = (correctCount * 100.0) / finishedCount;
                    subjectStats.setAccuracy(Math.round(accuracy * 10.0) / 10.0);
                } else {
                    subjectStats.setAccuracy(0.0);
                }

                // 计算覆盖度
                Integer totalCount = subjectQuestionCount.get(subject.getId());
                subjectStats.setTotalCount(totalCount != null ? totalCount : 0);

                if (totalCount != null && totalCount > 0) {
                    double coverage = (finishedCount * 100.0) / totalCount;
                    subjectStats.setCoverage(Math.round(coverage * 100.0) / 100.0);
                } else {
                    subjectStats.setCoverage(0.0);
                }

                subjectStatsList.add(subjectStats);
            }

            statsDTO.setSubjectStats(subjectStatsList);

            // 6. 计算总体统计
            UserStudyStatsDTO.OverallStats overallStats = new UserStudyStatsDTO.OverallStats();
            int totalFinished = answerRecords.size();
            int totalCorrect = (int) answerRecords.stream()
                    .filter(r -> r.getIsCorrect() != null && r.getIsCorrect() == 1)
                    .count();

            overallStats.setTotalFinished(totalFinished);
            overallStats.setTotalCorrect(totalCorrect);

            if (totalFinished > 0) {
                double overallAccuracy = (totalCorrect * 100.0) / totalFinished;
                overallStats.setOverallAccuracy(Math.round(overallAccuracy * 100.0) / 100.0);
            } else {
                overallStats.setOverallAccuracy(0.0);
            }

            // 活跃度：最近7天完成的题目数
            LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
            int activityScore = (int) answerRecords.stream()
                    .filter(r -> r.getCreateTime() != null && r.getCreateTime().isAfter(sevenDaysAgo))
                    .count();
            overallStats.setActivityScore(activityScore);

            statsDTO.setOverallStats(overallStats);

            // 计算总学习时长（基于答题记录的duration字段，单位秒）
            int totalDurationSeconds = answerRecords.stream()
                    .mapToInt(r -> r.getDuration() != null ? r.getDuration() : 0)
                    .sum();

            // 计算累计打卡天数
            int totalStudyDays = (int) answerRecords.stream()
                    .filter(r -> r.getCreateTime() != null)
                    .map(r -> r.getCreateTime().toLocalDate())
                    .distinct()
                    .count();
            statsDTO.setTotalStudyDays(totalStudyDays);

            // 设置快捷统计字段（用于前端侧边栏显示）
            statsDTO.setQuestionsDone(totalFinished);
            statsDTO.setAccuracy(overallStats.getOverallAccuracy());
            statsDTO.setStudyHours(Math.round(totalDurationSeconds / 3600.0 * 10.0) / 10.0);
        } else {
            // 没有答题记录时，返回空统计
            statsDTO.setSubjectStats(new ArrayList<>());
            UserStudyStatsDTO.OverallStats overallStats = new UserStudyStatsDTO.OverallStats();
            overallStats.setTotalFinished(0);
            overallStats.setTotalCorrect(0);
            overallStats.setOverallAccuracy(0.0);
            overallStats.setActivityScore(0);
            statsDTO.setOverallStats(overallStats);

            statsDTO.setQuestionsDone(0);
            statsDTO.setAccuracy(0.0);
            statsDTO.setStudyHours(0.0);
            statsDTO.setTotalStudyDays(0);
        }

        return statsDTO;
    }

    /**
     * 获取每个科目的题目总数
     */
    private Map<Integer, Integer> getSubjectQuestionCount() {
        QueryWrapper<QuestionSubjectRel> wrapper = new QueryWrapper<>();
        wrapper.select("subject_id", "count(*) as count");
        wrapper.groupBy("subject_id");
        List<Map<String, Object>> resultList = mapQuestionSubjectMapper.selectMaps(wrapper);

        return resultList.stream()
                .collect(Collectors.toMap(
                        map -> ((Number) map.get("subject_id")).intValue(),
                        map -> ((Number) map.get("count")).intValue()
                ));
    }


    @Override
    public HomePageDataDTO getHomePageData(Long userId) {
        HomePageDataDTO homeData = new HomePageDataDTO();

        // 1. 获取用户基本信息
        User user = getById(userId);
        if (user == null) {
            return null;
        }

        HomePageDataDTO.UserInfo userInfo = new HomePageDataDTO.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setRole(user.getRole());
        userInfo.setTargetSchool(user.getTargetSchool());
        userInfo.setTargetTotalScore(user.getTargetTotalScore());
        userInfo.setExamYear(user.getExamYear());
        userInfo.setExamDate("2025-12-20");
        userInfo.setExamSubjects(user.getExamSubjects());
        homeData.setUserInfo(userInfo);

        // 2. 获取学习统计数据
        HomePageDataDTO.StudyStats stats = new HomePageDataDTO.StudyStats();

        // 获取用户的所有答题记录
        LambdaQueryWrapper<AnswerRecord> answerWrapper = new LambdaQueryWrapper<>();
        answerWrapper.eq(AnswerRecord::getUserId, userId);
        List<AnswerRecord> answerRecords = recordService.list(answerWrapper);

        // 获取总刷题数
        int totalQuestions = answerRecords.size();
        stats.setQuestionsDone(totalQuestions);

        // 获取总正确数，计算正确率
        int totalCorrect = (int) answerRecords.stream()
                .filter(r -> r.getIsCorrect() != null && r.getIsCorrect() == 1)
                .count();
        double accuracy = totalQuestions > 0 ? (totalCorrect * 100.0) / totalQuestions : 0.0;
        stats.setAccuracy(Math.round(accuracy * 10.0) / 10.0);

        // 获取错题本数量
        int mistakesCount = (int) answerRecords.stream()
                .filter(r -> r.getIsCorrect() != null && r.getIsCorrect() == 0)
                .count();
        stats.setMistakesCount(mistakesCount);

        // 学习时长（基于答题记录的duration字段，单位秒）
        int totalDurationSeconds = answerRecords.stream()
                .mapToInt(r -> r.getDuration() != null ? r.getDuration() : 0)
                .sum();

        // 今日学习时长
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        int todayDurationSeconds = answerRecords.stream()
                .filter(r -> r.getCreateTime() != null && !r.getCreateTime().isBefore(todayStart))
                .mapToInt(r -> r.getDuration() != null ? r.getDuration() : 0)
                .sum();

        stats.setTodayStudyTime(Math.round(todayDurationSeconds / 3600.0 * 10.0) / 10.0);
        stats.setTotalStudyHours(Math.round(totalDurationSeconds / 3600.0 * 10.0) / 10.0);

        // 连续打卡天数（基于答题记录的create_time）
        int consecutiveDays = calculateConsecutiveDays(answerRecords);
        stats.setConsecutiveDays(consecutiveDays);

        homeData.setStudyStats(stats);

        // 3. 获取科目列表（Level 1 - 考试规格）
        QueryWrapper<Subject> subjectWrapper = new QueryWrapper<>();
        subjectWrapper.eq("level", "1");
        subjectWrapper.orderByAsc("sort");
        List<Subject> subjects = subjectMapper.selectList(subjectWrapper);

        List<HomePageDataDTO.Subject> subjectList = subjects.stream()
                .map(s -> {
                    HomePageDataDTO.Subject dto = new HomePageDataDTO.Subject();
                    dto.setId(s.getId());
                    dto.setName(s.getName());
                    dto.setLevel(Integer.parseInt(s.getLevel()));
                    return dto;
                })
                .collect(Collectors.toList());
        homeData.setSubjects(subjectList);

        // 4. 每日励志语录（暂时硬编码，后续可以做成随机或按日期获取）
        HomePageDataDTO.DailyQuote quote = new HomePageDataDTO.DailyQuote();
        quote.setContent("不积跬步，无以至千里；不积小流，无以成江海。");
        quote.setAuthor("荀子");
        homeData.setDailyQuote(quote);

        // 5. 个性化推荐（暂时返回空列表，后续可以根据算法实现）
        homeData.setRecommendations(new ArrayList<>());

        return homeData;
    }

    /**
     * 计算连续打卡天数
     */
    private int calculateConsecutiveDays(List<AnswerRecord> answerRecords) {
        if (answerRecords.isEmpty()) {
            return 0;
        }

        // 获取所有有答题记录的日期
        List<LocalDate> studyDates = answerRecords.stream()
                .filter(r -> r.getCreateTime() != null)
                .map(r -> r.getCreateTime().toLocalDate())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        if (studyDates.isEmpty()) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        LocalDate lastStudyDate = studyDates.get(studyDates.size() - 1);

        // 如果最后一天不是今天或昨天，连续天数清零
        if (lastStudyDate.isBefore(today.minusDays(1))) {
            return 0;
        }

        int consecutiveDays = 1;
        for (int i = studyDates.size() - 2; i >= 0; i--) {
            LocalDate current = studyDates.get(i);
            LocalDate previous = studyDates.get(i + 1);

            // 检查是否连续
            if (previous.minusDays(1).equals(current)) {
                consecutiveDays++;
            } else {
                break;
            }
        }

        return consecutiveDays;
    }

    @Override
    public List<Map<String, Object>> getRecommendations(Long userId) {
        List<Map<String, Object>> recommendations = new ArrayList<>();

        // 获取用户学习统计
        UserStudyStatsDTO stats = getUserStudyStats(userId);
        if (stats == null) {
            return recommendations;
        }

        // 规则1: 某科目正确率<70% → 生成该科目加强练习建议
        if (stats.getSubjectStats() != null) {
            for (UserStudyStatsDTO.SubjectStats subjectStats : stats.getSubjectStats()) {
                if (subjectStats.getAccuracy() < 70 && subjectStats.getFinishedCount() >= 10) {
                    Map<String, Object> recommendation = new HashMap<>();
                    recommendation.put("id", System.currentTimeMillis());
                    recommendation.put("type", "urgent");
                    recommendation.put("priority", "high");
                    recommendation.put("title", subjectStats.getSubjectName() + "基础薄弱");
                    recommendation.put("content", String.format(
                            "您在%s的正确率仅为%.1f%%，建议优先复习基础知识点，加强专项练习。",
                            subjectStats.getSubjectName(), subjectStats.getAccuracy()
                    ));
                    recommendation.put("actionType", "practice");
                    recommendation.put("subjectId", subjectStats.getSubjectId());
                    recommendation.put("icon", "Warning");
                    recommendations.add(recommendation);
                }
            }
        }

        // 规则2: 高频错题前3 → 生成针对性复习建议
        List<Map<String, Object>> hotMistakes = recordService.getHotMistakes(userId.intValue(), 3);
        if (!hotMistakes.isEmpty()) {
            Map<String, Object> topMistake = hotMistakes.get(0);
            Map<String, Object> recommendation = new HashMap<>();
            recommendation.put("id", System.currentTimeMillis() + 1);
            recommendation.put("type", "important");
            recommendation.put("priority", "medium");
            recommendation.put("title", "高频错题专项突破");
            recommendation.put("content", String.format(
                    "检测到您在\"%s\"这道题目上错误率达%d%%，已错误%d次。建议重点复习相关知识点。",
                    ((String) topMistake.get("content")).substring(0, Math.min(20, ((String) topMistake.get("content")).length())) + "...",
                    ((Number) topMistake.get("errorRate")).intValue(),
                    ((Number) topMistake.get("errorCount")).intValue()
            ));
            recommendation.put("actionType", "review");
            recommendation.put("subjectId", null);
            recommendation.put("icon", "Reading");
            recommendations.add(recommendation);
        }

        // 规则3: 连续学习天数>7 → 生成模拟考试建议
        if (stats.getOverallStats() != null && stats.getOverallStats().getActivityScore() > 70) {
            Map<String, Object> recommendation = new HashMap<>();
            recommendation.put("id", System.currentTimeMillis() + 2);
            recommendation.put("type", "normal");
            recommendation.put("priority", "low");
            recommendation.put("title", "全真模拟考试");
            recommendation.put("content", "您已保持连续学习的好习惯，建议进行一次2小时的全真模拟考试，检验整体掌握情况并适应考试节奏。");
            recommendation.put("actionType", "exam");
            recommendation.put("subjectId", null);
            recommendation.put("icon", "Document");
            recommendations.add(recommendation);
        }

        // 规则4: 学习覆盖度>80% → 生成综合测试建议
        if (stats.getSubjectStats() != null) {
            long highCoverageCount = stats.getSubjectStats().stream()
                    .filter(s -> s.getCoverage() >= 80)
                    .count();

            if (highCoverageCount >= 3) {
                Map<String, Object> recommendation = new HashMap<>();
                recommendation.put("id", System.currentTimeMillis() + 3);
                recommendation.put("type", "normal");
                recommendation.put("priority", "low");
                recommendation.put("title", "综合能力测试");
                recommendation.put("content", "您已完成大部分科目的基础学习，建议进行跨科目综合测试，提升知识综合运用能力。");
                recommendation.put("actionType", "exam");
                recommendation.put("subjectId", null);
                recommendation.put("icon", "Trophy");
                recommendations.add(recommendation);
            }
        }

        // 限制返回数量
        return recommendations.stream()
                .limit(5)
                .collect(Collectors.toList());
    }

}