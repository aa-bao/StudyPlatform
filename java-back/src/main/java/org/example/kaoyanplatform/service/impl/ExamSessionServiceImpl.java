package org.example.kaoyanplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.kaoyanplatform.client.PythonBackendClient;
import org.example.kaoyanplatform.entity.*;
import org.example.kaoyanplatform.entity.dto.ExamStartDTO;
import org.example.kaoyanplatform.mapper.ExamSessionMapper;
import org.example.kaoyanplatform.service.*;
import org.example.kaoyanplatform.util.MathAnswerMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExamSessionServiceImpl extends ServiceImpl<ExamSessionMapper, ExamSession> implements ExamSessionService {

    @Autowired
    private QuestionPaperRelService mapPaperQuestionService;

    @Autowired
    private ExamPaperService paperService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ExamRecordService examAnswerDetailService;

    @Autowired
    private PythonBackendClient pythonBackendClient;

    @Autowired
    private MathAnswerMatcher mathAnswerMatcher;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public ExamStartDTO startOrResumeExam(String userId, String paperId) {
        ExamPaper paper = paperService.getById(Long.parseLong(paperId));
        if (paper == null) {
            throw new RuntimeException("试卷不存在");
        }

        // 1. 先查询是否有未完成的会话（status=0）
        LambdaQueryWrapper<ExamSession> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExamSession::getUserId, Long.parseLong(userId))
                   .eq(ExamSession::getPaperId, Long.parseLong(paperId))
                   .eq(ExamSession::getStatus, 0)  // 0-进行中
                   .orderByDesc(ExamSession::getCreateTime)
                   .last("LIMIT 1");

        System.out.println("查询未完成会话 - userId: " + userId + ", paperId: " + paperId);

        ExamSession existingSession = getOne(queryWrapper);

        System.out.println("查询结果: " + (existingSession != null ? "找到会话 " + existingSession.getId() : "未找到会话"));

        ExamSession session;
        boolean isNewSession = false;

        if (existingSession != null) {
            // 2. 如果有未完成的会话，复用该会话
            session = existingSession;
            System.out.println("复用现有会话 - startTime: " + session.getStartTime() + ", expectedEndTime: " + session.getExpectedEndTime());

            // 检查考试是否已超时
            if (session.getExpectedEndTime() != null &&
                LocalDateTime.now().isAfter(session.getExpectedEndTime())) {
                // 考试已超时，自动提交
                submitExam(session.getId().toString(), "{}");
                throw new RuntimeException("考试时间已到，本次考试已自动提交");
            }
        } else {
            // 3. 如果没有未完成的会话，创建新会话
            isNewSession = true;

            Integer timeLimit = paper.getTimeLimit() != null ? paper.getTimeLimit() : 180;
            LocalDateTime startTime = LocalDateTime.now();
            LocalDateTime expectedEndTime = startTime.plusMinutes(timeLimit);

            session = new ExamSession();
            session.setUserId(Long.parseLong(userId));
            session.setPaperId(Long.parseLong(paperId));
            session.setStatus(0);
            session.setStartTime(startTime);
            session.setExpectedEndTime(expectedEndTime);
            session.setSwitchCount(0);
            session.setTotalScore(BigDecimal.ZERO);
            session.setSnapshotAnswers("{}");

            System.out.println("创建新会话 - userId: " + userId + ", startTime: " + startTime + ", expectedEndTime: " + expectedEndTime);

            save(session);
        }

        // 获取题目列表
        List<Question> questions = mapPaperQuestionService.getQuestionsWithDetails(paperId);

        ExamStartDTO dto = new ExamStartDTO();
        dto.setSession(session);
        dto.setPaper(paper);
        dto.setQuestions(questions);

        return dto;
    }

    @Override
    public boolean saveSnapshot(String sessionId, String snapshotJson) {
        ExamSession session = getById(Long.parseLong(sessionId));
        if (session == null) {
            return false;
        }
        session.setSnapshotAnswers(snapshotJson);
        return updateById(session);
    }

    @Override
    public boolean recordSwitch(String sessionId) {
        ExamSession session = getById(Long.parseLong(sessionId));
        if (session == null) {
            return false;
        }
        session.setSwitchCount(session.getSwitchCount() + 1);
        return updateById(session);
    }

    @Override
    @Transactional
    public void submitExam(String sessionId, String imagesJson) {
        ExamSession session = getById(Long.parseLong(sessionId));
        if (session == null) {
            throw new RuntimeException("考试会话不存在");
        }

        if (session.getStatus() == 1) {
            throw new RuntimeException("考试已提交");
        }

        // 解析前端传过来的图片答案
        Map<String, Object> submittedImages = new java.util.HashMap<>();
        if (imagesJson != null && !imagesJson.isEmpty() && !imagesJson.equals("{}")) {
            try {
                submittedImages = objectMapper.readValue(imagesJson, new TypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                System.err.println("解析图片答案失败: " + e.getMessage());
            }
        }

        List<QuestionPaperRel> mappings = mapPaperQuestionService.list(
                new LambdaQueryWrapper<QuestionPaperRel>()
                        .eq(QuestionPaperRel::getPaperId, session.getPaperId().toString())
                        .orderByAsc(QuestionPaperRel::getSortOrder)
        );

        if (mappings == null || mappings.isEmpty()) {
            throw new RuntimeException("试卷题目数据异常");
        }

        // 解析快照数据（只有文本答案）
        Map<String, Object> snapshotAnswers;
        try {
            snapshotAnswers = objectMapper.readValue(session.getSnapshotAnswers(), new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("答题快照解析失败");
        }

        List<ExamRecord> details = new ArrayList<>();
        BigDecimal objectiveScore = BigDecimal.ZERO;
        BigDecimal subjectiveTotalScore = BigDecimal.ZERO;
        BigDecimal objectiveTotalScore = BigDecimal.ZERO; // 计算客观题总分（不保存到数据库）

        for (QuestionPaperRel mapping : mappings) {
            Question question = questionService.getById(mapping.getQuestionId());
            if (question == null) continue;

            String questionId = mapping.getQuestionId().toString();
            String userAnswer = (String) snapshotAnswers.get(questionId);
            // 获取图片答案（从提交时传入的图片数据）
            String userAnswerImages = null;
            if (submittedImages != null && submittedImages.containsKey(questionId)) {
                try {
                    userAnswerImages = objectMapper.writeValueAsString(submittedImages.get(questionId));
                } catch (Exception e) {
                    userAnswerImages = null;
                }
            }

            ExamRecord detail = new ExamRecord();
            detail.setSessionId(session.getId());
            detail.setQuestionId(mapping.getQuestionId());
            detail.setUserAnswer(userAnswer != null ? userAnswer : "");
            detail.setUserAnswerImages(userAnswerImages);

            if (isObjectiveQuestion(question.getType())) {
                // 客观题：立即批改
                objectiveTotalScore = objectiveTotalScore.add(mapping.getScoreValue()); // 计算客观题总分
                Integer result = gradeObjectiveQuestion(question, userAnswer);
                detail.setIsCorrect(result);
                detail.setScoreEarned(result == 1 ? mapping.getScoreValue() : BigDecimal.ZERO);
                objectiveScore = objectiveScore.add(detail.getScoreEarned());
            } else {
                // 主观题：不批改（isCorrect=3，分数为0）
                detail.setIsCorrect(3);
                detail.setScoreEarned(BigDecimal.ZERO);
                detail.setAiFeedback("主观题需自行批改");
                subjectiveTotalScore = subjectiveTotalScore.add(mapping.getScoreValue());
            }

            details.add(detail);
        }

        // 保存答题明细
        examAnswerDetailService.saveBatch(details);

        // 立即更新考试状态：只设置客观题分数，主观题不批改
        session.setStatus(1);
        session.setSubmitTime(LocalDateTime.now());
        session.setTotalScore(objectiveScore);
        session.setAiSummary("客观题已批改完成。\n\n客观题得分：" + objectiveScore + " 分\n客观题总分：" + objectiveTotalScore + " 分\n主观题总分：" + subjectiveTotalScore + " 分");
        updateById(session);

        // 异步调用主观题批改
        System.out.println("\n考试已提交，开始异步批改主观题...");
        System.out.println("客观题得分：" + objectiveScore + " 分");
        System.out.println("待批改主观题数：" + (subjectiveTotalScore.compareTo(BigDecimal.ZERO) > 0 ? subjectiveTotalScore : "0"));
        System.out.println("主观题批改将在后台异步进行，请稍候...");

        gradeSubjectiveQuestionsAsync(sessionId);
    }

    @Override
    @Async
    public void gradeSubjectiveQuestionsAsync(String sessionId) {
        System.out.println("\n==========================================");
        System.out.println("开始整卷AI分析 - sessionId: " + sessionId + " @ " + LocalDateTime.now());
        System.out.println("==========================================");

        try {
            ExamSession session = getById(Long.parseLong(sessionId));
            if (session == null || session.getStatus() != 1) {
                System.out.println("会话不存在或未提交，跳过分析 - sessionId: " + sessionId);
                System.out.println("==========================================");
                return;
            }

            System.out.println("考试会话信息:");
            System.out.println("  PaperId: " + session.getPaperId());
            System.out.println("  状态: " + session.getStatus());
            System.out.println("  创建时间: " + session.getCreateTime());
            System.out.println("  提交时间: " + session.getSubmitTime());

            // 获取试卷信息
            ExamPaper paper = paperService.getById(session.getPaperId());
            if (paper == null) {
                System.out.println("试卷不存在");
                System.out.println("==========================================");
                return;
            }

            // 获取所有题目和用户答案
            List<QuestionPaperRel> mappings = mapPaperQuestionService.list(
                    new LambdaQueryWrapper<QuestionPaperRel>()
                            .eq(QuestionPaperRel::getPaperId, session.getPaperId().toString())
                            .orderByAsc(QuestionPaperRel::getSortOrder)
            );

            if (mappings == null || mappings.isEmpty()) {
                System.out.println("试卷没有题目");
                System.out.println("==========================================");
                return;
            }

            // 获取所有答题记录
            LambdaQueryWrapper<ExamRecord> recordWrapper = new LambdaQueryWrapper<>();
            recordWrapper.eq(ExamRecord::getSessionId, session.getId());
            List<ExamRecord> allDetails = examAnswerDetailService.list(recordWrapper);

            Map<String, ExamRecord> recordMap = allDetails.stream()
                    .collect(Collectors.toMap(
                            r -> r.getQuestionId().toString(),
                            r -> r,
                            (r1, r2) -> r1
                    ));

            // 构建整卷数据
            List<Map<String, Object>> questions = new ArrayList<>();
            BigDecimal objectiveTotalScore = BigDecimal.ZERO;
            BigDecimal subjectiveTotalScore = BigDecimal.ZERO;

            for (int i = 0; i < mappings.size(); i++) {
                QuestionPaperRel mapping = mappings.get(i);
                Question question = questionService.getById(mapping.getQuestionId().toString());
                if (question == null) continue;

                ExamRecord record = recordMap.get(mapping.getQuestionId().toString());

                Map<String, Object> questionData = new HashMap<>();
                questionData.put("index", i + 1);
                questionData.put("type", question.getType());
                questionData.put("content", question.getContent());
                questionData.put("answer", question.getAnswer());
                questionData.put("score", mapping.getScoreValue() != null ? mapping.getScoreValue().doubleValue() : 10.0);

                if (record != null) {
                    questionData.put("user_answer", record.getUserAnswer() != null ? record.getUserAnswer() : "");

                    // 处理图片答案
                    String userAnswerImages = record.getUserAnswerImages();
                    if (userAnswerImages != null && !userAnswerImages.isEmpty() && userAnswerImages.startsWith("[")) {
                        try {
                            List<String> imageBases = objectMapper.readValue(userAnswerImages, new TypeReference<List<String>>() {});
                            questionData.put("user_answer_images", imageBases);
                        } catch (Exception e) {
                            questionData.put("user_answer_images", new ArrayList<>());
                        }
                    } else {
                        questionData.put("user_answer_images", new ArrayList<>());
                    }
                }

                questions.add(questionData);

                // 计算客观题/主观题总分
                if (isObjectiveQuestion(question.getType())) {
                    objectiveTotalScore = objectiveTotalScore.add(mapping.getScoreValue());
                } else {
                    subjectiveTotalScore = subjectiveTotalScore.add(mapping.getScoreValue());
                }
            }

            System.out.println("准备整卷分析数据:");
            System.out.println("  总题数: " + questions.size());
            System.out.println("  客观题总分: " + objectiveTotalScore);
            System.out.println("  主观题总分: " + subjectiveTotalScore);
            System.out.println("正在调用AI进行整卷分析...");

            // 调用Python后端整卷分析
            long apiStart = System.currentTimeMillis();

            Map<String, Object> examData = new HashMap<>();
            examData.put("total_score", paper.getTotalScore() != null ? paper.getTotalScore().doubleValue() : 150.0);
            examData.put("time_limit", paper.getTimeLimit() != null ? paper.getTimeLimit() : 180);
            examData.put("questions", questions);

            Map<String, Object> analysisResult = pythonBackendClient.analyzeExam(examData);

            long apiEnd = System.currentTimeMillis();
            System.out.println("AI整卷分析完成，耗时: " + (apiEnd - apiStart) + "ms");

            // 解析分析结果并更新答题记录
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> detailedGrading = (List<Map<String, Object>>) analysisResult.get("detailed_grading");

            // 先累加客观题原来的得分（这些已经批改过了）
            BigDecimal manualObjectiveScore = BigDecimal.ZERO;
            BigDecimal manualSubjectiveScore = BigDecimal.ZERO;

            // 先获取客观题原来的得分 - 通过record的isCorrect判断（0或1表示客观题）
            for (QuestionPaperRel mapping : mappings) {
                ExamRecord record = recordMap.get(mapping.getQuestionId().toString());
                if (record != null) {
                    // 检查是否是客观题：isCorrect为0或1表示客观题
                    boolean isObjective = record.getIsCorrect() != null &&
                                         (record.getIsCorrect() == 0 || record.getIsCorrect() == 1);
                    if (isObjective && record.getScoreEarned() != null) {
                        manualObjectiveScore = manualObjectiveScore.add(record.getScoreEarned());
                        System.out.println("客观题 - questionId: " + mapping.getQuestionId() +
                                          ", score: " + record.getScoreEarned() +
                                          ", isCorrect: " + record.getIsCorrect());
                    }
                }
            }
            System.out.println("累加后的客观题总分: " + manualObjectiveScore);

            if (detailedGrading != null) {
                for (Map<String, Object> grading : detailedGrading) {
                    Integer questionIndex = (Integer) grading.get("question_index");
                    if (questionIndex == null || questionIndex < 1 || questionIndex > mappings.size()) continue;

                    QuestionPaperRel mapping = mappings.get(questionIndex - 1);
                    ExamRecord record = recordMap.get(mapping.getQuestionId().toString());
                    if (record == null) continue;

                    // 只处理主观题，客观题保持原样 - 通过record的isCorrect判断
                    boolean isObjective = record.getIsCorrect() != null &&
                                         (record.getIsCorrect() == 0 || record.getIsCorrect() == 1);
                    if (isObjective) {
                        continue; // 跳过客观题，不修改
                    }

                    // 更新得分和反馈
                    Double score = (Double) grading.get("score");
                    String feedback = (String) grading.get("feedback");

                    boolean hasAnswer = false;
                    // 检查用户是否有答案（文本或图片）
                    if (record.getUserAnswer() != null && !record.getUserAnswer().trim().isEmpty()) {
                        hasAnswer = true;
                    }
                    if (record.getUserAnswerImages() != null && !record.getUserAnswerImages().trim().isEmpty()
                            && !record.getUserAnswerImages().equals("[]")) {
                        hasAnswer = true;
                    }

                    BigDecimal scoreDecimal = BigDecimal.ZERO;
                    if (score != null && hasAnswer) {
                        scoreDecimal = BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP);
                        // 限制得分不超过题目分值
                        if (mapping.getScoreValue() != null && scoreDecimal.compareTo(mapping.getScoreValue()) > 0) {
                            scoreDecimal = mapping.getScoreValue();
                        }
                    } else if (!hasAnswer) {
                        // 没有答案的主观题得0分
                        feedback = "未作答";
                    }

                    record.setScoreEarned(scoreDecimal);

                    // 只累加主观题得分（这里已经跳过了客观题，所以可以安全累加）
                    manualSubjectiveScore = manualSubjectiveScore.add(scoreDecimal);
                    System.out.println("主观题 - questionId: " + mapping.getQuestionId() +
                                      ", score: " + scoreDecimal +
                                      ", hasAnswer: " + hasAnswer);

                    if (feedback != null) {
                        record.setAiFeedback(feedback);
                    }
                    record.setIsCorrect(2); // 标记为已批改

                    examAnswerDetailService.updateById(record);
                }
            }

            // 构建完整的AI分析报告（Markdown格式）
            @SuppressWarnings("unchecked")
            Map<String, Object> analysis = (Map<String, Object>) analysisResult.get("analysis");

            StringBuilder aiSummary = new StringBuilder();
            aiSummary.append("# AI考试分析报告\n\n");

            // 使用手动计算的总分，不依赖AI返回的分数
            BigDecimal manualTotalScore = manualObjectiveScore.add(manualSubjectiveScore);
            System.out.println("最终计算结果 - 客观题: " + manualObjectiveScore +
                              ", 主观题: " + manualSubjectiveScore +
                              ", 总分: " + manualTotalScore);
            aiSummary.append("## 成绩概览\n\n");
            aiSummary.append("| 项目 | 得分 | 总分 |\n");
            aiSummary.append("|------|------|------|\n");
            aiSummary.append("| **总分** | **").append(manualTotalScore).append("** | ").append(objectiveTotalScore.add(subjectiveTotalScore)).append(" |\n");
            aiSummary.append("| 客观题 | ").append(manualObjectiveScore).append(" | ").append(objectiveTotalScore).append(" |\n");
            aiSummary.append("| 主观题 | ").append(manualSubjectiveScore).append(" | ").append(subjectiveTotalScore).append(" |\n\n");

            if (analysis != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> objectiveAnalysis = (Map<String, Object>) analysis.get("objective_analysis");
                if (objectiveAnalysis != null) {
                    String objErrorAnalysis = (String) objectiveAnalysis.get("error_analysis");
                    if (objErrorAnalysis != null && !objErrorAnalysis.isEmpty()) {
                        aiSummary.append("## 📊 客观题分析\n\n");
                        aiSummary.append(objErrorAnalysis).append("\n\n");
                    }
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> subjectiveAnalysis = (Map<String, Object>) analysis.get("subjective_analysis");
                if (subjectiveAnalysis != null) {
                    String subjErrorAnalysis = (String) subjectiveAnalysis.get("error_analysis");
                    if (subjErrorAnalysis != null && !subjErrorAnalysis.isEmpty()) {
                        aiSummary.append("## ✍️ 主观题分析\n\n");
                        aiSummary.append(subjErrorAnalysis).append("\n\n");
                    }
                }

                @SuppressWarnings("unchecked")
                List<String> strengths = (List<String>) analysis.get("strengths");
                if (strengths != null && !strengths.isEmpty()) {
                    aiSummary.append("## ✅ 表现优秀\n\n");
                    for (String s : strengths) {
                        aiSummary.append("- ").append(s).append("\n");
                    }
                    aiSummary.append("\n");
                }

                @SuppressWarnings("unchecked")
                List<String> weaknesses = (List<String>) analysis.get("weaknesses");
                if (weaknesses != null && !weaknesses.isEmpty()) {
                    aiSummary.append("## ⚠️ 待提升部分\n\n");
                    for (String w : weaknesses) {
                        aiSummary.append("- ").append(w).append("\n");
                    }
                    aiSummary.append("\n");
                }

                @SuppressWarnings("unchecked")
                List<String> suggestions = (List<String>) analysis.get("suggestions");
                if (suggestions != null && !suggestions.isEmpty()) {
                    aiSummary.append("## 💪 学习建议\n\n");
                    for (String s : suggestions) {
                        aiSummary.append("- ").append(s).append("\n");
                    }
                }
            }

            // 使用手动计算的总分（不依赖AI返回的结果）
            BigDecimal totalScore = manualTotalScore;

            // 更新会话
            session.setTotalScore(totalScore);
            session.setAiSummary(aiSummary.toString());
            updateById(session);

            System.out.println("整卷AI分析完成 - sessionId: " + sessionId + ", 最终总分: " + totalScore);
            System.out.println("==========================================");

        } catch (Exception e) {
            System.err.println("整卷AI分析出错 - sessionId: " + sessionId + ", 错误: " + e.getMessage());
            e.printStackTrace();
            System.out.println("==========================================");
        }
    }

    private boolean isObjectiveQuestion(Integer type) {
        return type != null && (type == 1 || type == 2 || type == 3);
    }

    private Integer gradeObjectiveQuestion(Question question, String userAnswer) {
        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            return 0;
        }

        String standardAnswer = question.getAnswer().trim();
        String submittedAnswer = userAnswer.trim();

        if (question.getType() == 1) {
            // 单选题：完全匹配
            return standardAnswer.equalsIgnoreCase(submittedAnswer) ? 1 : 0;
        } else if (question.getType() == 2) {
            // 多选题
            String[] correctOptions = standardAnswer.split("[,，]");
            String[] userOptions = submittedAnswer.split("[,，]");

            int correctCount = 0;
            for (String opt : userOptions) {
                for (String correct : correctOptions) {
                    if (correct.trim().equalsIgnoreCase(opt.trim())) {
                        correctCount++;
                        break;
                    }
                }
            }

            if (correctCount == correctOptions.length && userOptions.length == correctOptions.length) {
                return 1;
            }
        } else if (question.getType() == 3) {
            // 填空题：使用数学答案匹配工具
            boolean isCorrect = mathAnswerMatcher.matchMathAnswer(standardAnswer, submittedAnswer);
            return isCorrect ? 1 : 0;
        }

        return 0;
    }
}
