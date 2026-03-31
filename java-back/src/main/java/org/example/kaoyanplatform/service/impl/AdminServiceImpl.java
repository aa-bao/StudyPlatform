package org.example.kaoyanplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.kaoyanplatform.entity.*;
import org.example.kaoyanplatform.entity.dto.MistakeHeatmapDTO;
import org.example.kaoyanplatform.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements org.example.kaoyanplatform.service.AdminService {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private ErrorQuestionService mistakeRecordService;

    @Autowired
    private QuestionSubjectRelService mapQuestionSubjectService;

    @Autowired
    private RecordService recordService;

    @Override
    public Map<String, Object> getStatistics(Integer userId) {
        Map<String, Object> map = new HashMap<>();

        // 1. 处理卡片统计数据
        if (userId != null) {
            // 普通用户模式
            map.put("questionCount", questionService.count());
            map.put("userCount", 1);
            map.put("exerciseCount", mistakeRecordService.count(new QueryWrapper<ErrorQuestion>().eq("user_id", userId)));
            map.put("todayActive", "个人模式");
        } else {
            // 管理员模式：显示全局数据

            // 获取时间范围
            LocalDateTime todayStart = LocalDate.now().atStartOfDay();
            LocalDateTime todayEnd = LocalDate.now().plusDays(1).atStartOfDay();
            LocalDateTime yesterdayStart = LocalDate.now().minusDays(1).atStartOfDay();
            LocalDateTime yesterdayEnd = LocalDate.now().atStartOfDay();

            // 1. 题目总数及变化
            long currentQuestionCount = questionService.count();
            map.put("questionCount", currentQuestionCount);

            // 查询昨日的题目总数（使用创建时间小于昨天的记录）
            long yesterdayQuestionCount = questionService.count(
                new QueryWrapper<Question>()
                    .lt("create_time", yesterdayEnd)
            );
            map.put("questionTrend", currentQuestionCount - yesterdayQuestionCount);

            // 2. 用户总数及变化
            long currentUserCount = userService.count();
            map.put("userCount", currentUserCount);

            // 查询昨日的用户总数
            long yesterdayUserCount = userService.count(
                new QueryWrapper<User>()
                    .lt("create_time", yesterdayEnd)
            );
            map.put("userTrend", currentUserCount - yesterdayUserCount);

            // 3. 练习总数及变化
            long totalExercises = recordService.count();
            map.put("exerciseCount", totalExercises);

            // 昨日练习总数
            long yesterdayExerciseCount = recordService.count(
                new QueryWrapper<AnswerRecord>()
                    .lt("create_time", yesterdayEnd)
            );
            map.put("exerciseTrend", totalExercises - yesterdayExerciseCount);

            // 4. 今日活跃用户数及变化
            List<AnswerRecord> todayRecords = recordService.list(
                new QueryWrapper<AnswerRecord>()
                    .ge("create_time", todayStart)
                    .lt("create_time", todayEnd)
            );
            Set<Long> todayActiveUsers = new HashSet<>();
            for (AnswerRecord record : todayRecords) {
                todayActiveUsers.add(record.getUserId());
            }
            int todayActive = todayActiveUsers.size();
            map.put("todayActive", todayActive);

            // 昨日活跃用户数
            List<AnswerRecord> yesterdayRecords = recordService.list(
                new QueryWrapper<AnswerRecord>()
                    .ge("create_time", yesterdayStart)
                    .lt("create_time", yesterdayEnd)
            );
            Set<Long> yesterdayActiveUsers = new HashSet<>();
            for (AnswerRecord record : yesterdayRecords) {
                yesterdayActiveUsers.add(record.getUserId());
            }
            int yesterdayActive = yesterdayActiveUsers.size();
            map.put("todayActiveTrend", todayActive - yesterdayActive);
        }

        // 2. 饼图数据（通过映射表查询各科目题目数量）
        List<Map<String, Object>> seriesData = new ArrayList<>();

        // 通过映射表查询各科目的题目数量
        // 政治科ID=1
        long politicsCount = getCountBySubjectId(1);
        seriesData.add(Map.of("name", "政治", "value", politicsCount));

        // 英语科ID=2
        long englishCount = getCountBySubjectId(2);
        seriesData.add(Map.of("name", "英语", "value", englishCount));

        // 数学科ID=3
        long mathCount = getCountBySubjectId(3);
        seriesData.add(Map.of("name", "数学", "value", mathCount));

        // 408专业课科ID=4
        long cs408Count = getCountBySubjectId(4);
        seriesData.add(Map.of("name", "408专业课", "value", cs408Count));

        map.put("subjectData", seriesData);

        return map;
    }

    @Override
    public List<MistakeHeatmapDTO> getMistakeHeatmap() {
        return mistakeRecordService.getMistakeHeatmap();
    }

    @Override
    public List<MistakeHeatmapDTO.HotMistakeQuestion> getHotMistakes(Integer limit) {
        return mistakeRecordService.getHotMistakeQuestions(limit);
    }

    @Override
    public Map<String, Object> getActivityTrend(String period) {
        Map<String, Object> result = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Integer> activeUsers = new ArrayList<>();

        if (period.equals("year")) {
            // 按月聚合：遍历过去12个月
            for (int i = 11; i >= 0; i--) {
                LocalDate monthStart = LocalDate.now().minusMonths(i).withDayOfMonth(1);
                LocalDate monthEnd = monthStart.plusMonths(1);
                LocalDateTime start = monthStart.atStartOfDay();
                LocalDateTime end = monthEnd.atStartOfDay();

                List<AnswerRecord> monthRecords = recordService.list(
                    new QueryWrapper<AnswerRecord>()
                        .ge("create_time", start)
                        .lt("create_time", end)
                );

                Set<Long> uniqueUsers = new HashSet<>();
                for (AnswerRecord record : monthRecords) {
                    uniqueUsers.add(record.getUserId());
                }

                dates.add(monthStart.getMonthValue() + "月");
                activeUsers.add(uniqueUsers.size());
            }
        } else {
            int days = period.equals("month") ? 30 : 7;

            for (int i = days - 1; i >= 0; i--) {
                LocalDateTime dayStart = LocalDate.now().minusDays(i).atStartOfDay();
                LocalDateTime dayEnd = LocalDate.now().minusDays(i - 1).atStartOfDay();

                List<AnswerRecord> dayRecords = recordService.list(
                    new QueryWrapper<AnswerRecord>()
                        .ge("create_time", dayStart)
                        .lt("create_time", dayEnd)
                );

                Set<Long> uniqueUsers = new HashSet<>();
                for (AnswerRecord record : dayRecords) {
                    uniqueUsers.add(record.getUserId());
                }

                dates.add(LocalDate.now().minusDays(i).getMonthValue() + "/" + LocalDate.now().minusDays(i).getDayOfMonth());
                activeUsers.add(uniqueUsers.size());
            }
        }

        result.put("dates", dates);
        result.put("activeUsers", activeUsers);
        return result;
    }

    @Override
    public Map<String, Object> getAccuracyTrend(String period) {
        Map<String, Object> result = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Double> accuracyList = new ArrayList<>();
        List<Integer> totalQuestionsList = new ArrayList<>();

        int days = period.equals("month") ? 30 : 7;

        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime dayStart = LocalDate.now().minusDays(i).atStartOfDay();
            LocalDateTime dayEnd = LocalDate.now().minusDays(i - 1).atStartOfDay();

            // 查询当天的答题记录
            List<AnswerRecord> dayRecords = recordService.list(
                new QueryWrapper<AnswerRecord>()
                    .ge("create_time", dayStart)
                    .lt("create_time", dayEnd)
            );

            int totalQuestions = dayRecords.size();
            int correctCount = 0;

            for (AnswerRecord record : dayRecords) {
                if (record.getIsCorrect() != null && record.getIsCorrect() == 1) {
                    correctCount++;
                }
            }

            double accuracy = totalQuestions > 0 ? (correctCount * 100.0 / totalQuestions) : 0;

            String dateStr = LocalDate.now().minusDays(i).getMonthValue() + "/" + LocalDate.now().minusDays(i).getDayOfMonth();
            dates.add(dateStr);
            accuracyList.add(Math.round(accuracy * 10.0) / 10.0); // 保留一位小数
            totalQuestionsList.add(totalQuestions);
        }

        result.put("dates", dates);
        result.put("accuracy", accuracyList);
        result.put("totalQuestions", totalQuestionsList);
        return result;
    }

    @Override
    public Map<String, Object> getDifficultyDistribution() {
        Map<String, Object> result = new HashMap<>();

        // 统计各难度的题目数量
        Map<Integer, Integer> difficultyMap = new HashMap<>();
        difficultyMap.put(1, 0);
        difficultyMap.put(2, 0);
        difficultyMap.put(3, 0);
        difficultyMap.put(4, 0);
        difficultyMap.put(5, 0);

        List<Question> allQuestions = questionService.list();
        for (Question question : allQuestions) {
            Integer difficulty = question.getDifficulty();
            if (difficulty != null && difficulty >= 1 && difficulty <= 5) {
                difficultyMap.put(difficulty, difficultyMap.get(difficulty) + 1);
            }
        }

        List<String> difficulties = Arrays.asList("难度1", "难度2", "难度3", "难度4", "难度5");
        List<Integer> counts = Arrays.asList(
            difficultyMap.get(1),
            difficultyMap.get(2),
            difficultyMap.get(3),
            difficultyMap.get(4),
            difficultyMap.get(5)
        );

        result.put("difficulties", difficulties);
        result.put("counts", counts);
        return result;
    }

    @Override
    public Map<String, Object> getHourlyActivityHeatmap() {
        Map<String, Object> result = new HashMap<>();

        // 初始化24小时 x 7天的数据
        int[][] heatmapData = new int[7][24];

        // 获取最近7天的答题记录
        for (int dayOffset = 6; dayOffset >= 0; dayOffset--) {
            LocalDateTime dayStart = LocalDate.now().minusDays(dayOffset).atStartOfDay();
            LocalDateTime dayEnd = dayStart.plusDays(1);

            // 查询当天的答题记录
            List<AnswerRecord> dayRecords = recordService.list(
                new QueryWrapper<AnswerRecord>()
                    .ge("create_time", dayStart)
                    .lt("create_time", dayEnd)
            );

            // 统计每小时的答题次数
            for (AnswerRecord record : dayRecords) {
                int hour = record.getCreateTime().getHour();
                heatmapData[6 - dayOffset][hour]++;
            }
        }

        // 转换为前端需要的格式
        List<String> hours = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hours.add(i + ":00");
        }

        List<String> days = Arrays.asList("周一", "周二", "周三", "周四", "周五", "周六", "周日");

        List<List<Integer>> data = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            List<Integer> rowData = new ArrayList<>();
            for (int j = 0; j < 24; j++) {
                rowData.add(heatmapData[i][j]);
            }
            data.add(rowData);
        }

        result.put("hours", hours);
        result.put("days", days);
        result.put("data", data);

        return result;
    }

    @Override
    public Map<String, Object> getSubjectMistakeCount() {
        Map<String, Object> result = new HashMap<>();

        // 统计各科目的错题数量（科目ID -> 错题数量）
        Map<Integer, Integer> subjectMistakeMap = new HashMap<>();
        subjectMistakeMap.put(1, 0); // 政治
        subjectMistakeMap.put(2, 0); // 英语
        subjectMistakeMap.put(3, 0); // 数学
        subjectMistakeMap.put(4, 0); // 408专业课

        // 获取所有错题记录
        List<ErrorQuestion> allMistakes = mistakeRecordService.list();

        for (ErrorQuestion mistake : allMistakes) {
            // 通过题目ID查询所属科目ID
            List<Integer> subjectIds = mapQuestionSubjectService.getSubjectIdsByQuestionId(Long.valueOf(mistake.getQuestionId()));
            if (subjectIds != null && !subjectIds.isEmpty()) {
                Integer subjectId = subjectIds.get(0);
                // 只统计我们关心的四个科目
                if (subjectMistakeMap.containsKey(subjectId)) {
                    subjectMistakeMap.put(subjectId, subjectMistakeMap.get(subjectId) + 1);
                }
            }
        }

        List<String> subjects = Arrays.asList("政治", "英语", "数学", "408专业课");
        List<Integer> counts = Arrays.asList(
            subjectMistakeMap.get(1),
            subjectMistakeMap.get(2),
            subjectMistakeMap.get(3),
            subjectMistakeMap.get(4)
        );

        result.put("subjects", subjects);
        result.put("counts", counts);

        return result;
    }

    @Override
    public List<Map<String, Object>> getRecentActivities(Integer limit) {
        // 获取最近的答题记录
        List<AnswerRecord> recentRecords = recordService.list(
            new QueryWrapper<AnswerRecord>()
                .orderByDesc("create_time")
                .last("LIMIT " + limit)
        );

        List<Map<String, Object>> activities = new ArrayList<>();
        for (AnswerRecord record : recentRecords) {
            Map<String, Object> activity = new HashMap<>();

            // 获取用户信息
            User user = userService.getById(record.getUserId());
            String username = user != null ? user.getUsername() : "未知用户";

            // 获取题目信息
            Question question = questionService.getById(record.getQuestionId());
            String subjectName = "未知科目";
            if (question != null && question.getSubjectNames() != null && !question.getSubjectNames().isEmpty()) {
                subjectName = question.getSubjectNames().get(0);
            }

            // 计算时间差
            long minutesDiff = Duration.between(record.getCreateTime(), LocalDateTime.now()).toMinutes();
            String timeDesc;
            if (minutesDiff < 60) {
                timeDesc = minutesDiff + "分钟前";
            } else if (minutesDiff < 1440) {
                timeDesc = (minutesDiff / 60) + "小时前";
            } else {
                timeDesc = (minutesDiff / 1440) + "天前";
            }

            // 根据是否正确设置颜色
            String color = record.getIsCorrect() == 1 ? "#67C23A" : "#F56C6C";

            activity.put("username", username);
            activity.put("action", record.getIsCorrect() == 1 ? "答对了" : "答错了");
            activity.put("detail", subjectName + "题目");
            activity.put("time", timeDesc);
            activity.put("color", color);

            activities.add(activity);
        }

        return activities;
    }

    @Override
    public List<Map<String, Object>> getTopUsers(Integer limit) {
        // 查询所有用户的答题统计
        List<AnswerRecord> allRecords = recordService.list();

        // 按用户统计
        Map<Long, List<AnswerRecord>> userRecords = new HashMap<>();
        for (AnswerRecord record : allRecords) {
            userRecords.computeIfAbsent(record.getUserId(), k -> new ArrayList<>()).add(record);
        }

        // 计算每个用户的统计数据
        List<Map<String, Object>> rankings = new ArrayList<>();
        for (Map.Entry<Long, List<AnswerRecord>> entry : userRecords.entrySet()) {
            Long userId = entry.getKey();
            List<AnswerRecord> records = entry.getValue();

            int totalExercises = records.size();
            int correctCount = 0;
            int totalDuration = 0;

            for (AnswerRecord record : records) {
                if (record.getIsCorrect() != null && record.getIsCorrect() == 1) {
                    correctCount++;
                }
                if (record.getDuration() != null) {
                    totalDuration += record.getDuration();
                }
            }

            double accuracy = totalExercises > 0 ? (correctCount * 100.0 / totalExercises) : 0;
            int studyHours = totalDuration / 3600; // 转换为小时

            User user = userService.getById(userId);
            if (user != null) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", userId);
                userInfo.put("username", user.getUsername());
                userInfo.put("avatar", user.getAvatar());
                userInfo.put("exerciseCount", totalExercises);
                userInfo.put("accuracy", Math.round(accuracy));
                userInfo.put("studyHours", studyHours);
                rankings.add(userInfo);
            }
        }

        // 按刷题数排序，取前N名
        rankings.sort((a, b) -> {
            int exerciseA = (Integer) a.get("exerciseCount");
            int exerciseB = (Integer) b.get("exerciseCount");
            return exerciseB - exerciseA;
        });

        List<Map<String, Object>> topRankings = rankings.stream()
            .limit(limit)
            .collect(Collectors.toList());

        return topRankings;
    }

    /**
     * 根据科目ID获取题目数量（通过映射表）
     * @param subjectId 科目ID
     * @return 题目数量
     */
    private long getCountBySubjectId(int subjectId) {
        try {
            List<Long> questionIds = mapQuestionSubjectService.getQuestionIdsBySubjectId(subjectId);
            return questionIds != null ? questionIds.size() : 0;
        } catch (Exception e) {
            // 如果查询失败，返回0
            return 0;
        }
    }
}