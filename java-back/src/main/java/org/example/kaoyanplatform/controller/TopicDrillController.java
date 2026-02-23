package org.example.kaoyanplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.kaoyanplatform.common.Result;
import org.example.kaoyanplatform.entity.AnswerRecord;
import org.example.kaoyanplatform.entity.Question;
import org.example.kaoyanplatform.entity.TopicDrillProgress;
import org.example.kaoyanplatform.service.QuestionService;
import org.example.kaoyanplatform.service.RecordService;
import org.example.kaoyanplatform.service.SubjectService;
import org.example.kaoyanplatform.service.TopicDrillProgressService;

import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "专项突破", description = "专项突破相关接口")
@RestController
@RequestMapping("/topic-drill")
public class TopicDrillController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private TopicDrillProgressService topicDrillProgressService;

    @Autowired
    private RecordService recordService;

    // 1. 按知识点获取题目（递归下级）
    @GetMapping("/questions-by-knowledge-point")
    @Operation(summary = "按知识点获取题目", description = "根据知识点ID及其所有子知识点递归查询题目。")
    public Result getQuestionsByKnowledgePoint(@RequestParam Integer subjectId) {
        List<Integer> subjectIds = subjectService.getDescendantIds(subjectId);
        List<Question> questions = questionService.getQuestionsBySubjectIds(subjectIds);
        return Result.success(questions);
    }

    // 2. 保存答题进度
    @PostMapping("/save-progress")
    @Operation(summary = "保存答题进度", description = "保存用户在专项突破模式中的答题进度。")
    public Result saveProgress(@RequestBody Map<String, Object> progressData) {
        try {
            System.out.println("接收到的进度数据: " + progressData);

            TopicDrillProgress progress = new TopicDrillProgress();

            // 检查 userId
            Object userIdObj = progressData.get("userId");
            if (userIdObj != null) {
                try {
                    progress.setUserId(Long.parseLong(userIdObj.toString()));
                } catch (NumberFormatException e) {
                    System.err.println("userId 格式错误: " + userIdObj);
                }
            }

            // 检查 subjectId
            Object subjectIdObj = progressData.get("subjectId");
            if (subjectIdObj != null) {
                try {
                    progress.setSubjectId(Integer.parseInt(subjectIdObj.toString()));
                } catch (NumberFormatException e) {
                    System.err.println("subjectId 格式错误: " + subjectIdObj);
                }
            }

            // 检查 questionCount
            Object questionCountObj = progressData.get("questionCount");
            if (questionCountObj != null) {
                try {
                    progress.setQuestionCount(Integer.parseInt(questionCountObj.toString()));
                } catch (NumberFormatException e) {
                    System.err.println("questionCount 格式错误: " + questionCountObj);
                }
            }

            // 检查 answeredCount
            Object answeredCountObj = progressData.get("answeredCount");
            if (answeredCountObj != null) {
                try {
                    progress.setAnsweredCount(Integer.parseInt(answeredCountObj.toString()));
                } catch (NumberFormatException e) {
                    System.err.println("answeredCount 格式错误: " + answeredCountObj);
                }
            }

            // 检查 correctCount
            Object correctCountObj = progressData.get("correctCount");
            if (correctCountObj != null) {
                try {
                    progress.setCorrectCount(Integer.parseInt(correctCountObj.toString()));
                } catch (NumberFormatException e) {
                    System.err.println("correctCount 格式错误: " + correctCountObj);
                }
            }

            // 检查 accuracy
            Object accuracyObj = progressData.get("accuracy");
            if (accuracyObj != null) {
                try {
                    progress.setAccuracy(Integer.parseInt(accuracyObj.toString()));
                } catch (NumberFormatException e) {
                    System.err.println("accuracy 格式错误: " + accuracyObj);
                }
            }

            // 检查 questionsData
            progress.setQuestionsData(progressData.get("questionsData") != null ? progressData.get("questionsData").toString() : null);

            // 检查 cardPositions
            progress.setCardPositions(progressData.get("cardPositions") != null ? progressData.get("cardPositions").toString() : null);

            // 检查 timestamp
            Object timestampObj = progressData.get("timestamp");
            if (timestampObj != null) {
                try {
                    String timestampStr = timestampObj.toString().replace("Z", "");
                    progress.setTimestamp(java.time.LocalDateTime.parse(timestampStr));
                } catch (Exception e) {
                    System.err.println("timestamp 格式错误: " + timestampObj);
                }
            }

            topicDrillProgressService.saveProgress(progress);
            return Result.success("答题进度已保存");
        } catch (Exception e) {
            System.err.println("保存进度失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("保存失败: " + e.getMessage());
        }
    }

    // 3. 加载答题进度
    @GetMapping("/load-progress")
    @Operation(summary = "加载答题进度", description = "加载用户在专项突破模式中的答题进度。")
    public Result loadProgress(@RequestParam Integer userId, @RequestParam Integer subjectId) {
        try {
            TopicDrillProgress progress = topicDrillProgressService.loadProgress(userId.longValue(), subjectId);
            if (progress != null) {
                return Result.success(Map.of(
                        "userId", progress.getUserId(),
                        "subjectId", progress.getSubjectId(),
                        "questionCount", progress.getQuestionCount(),
                        "answeredCount", progress.getAnsweredCount(),
                        "correctCount", progress.getCorrectCount(),
                        "accuracy", progress.getAccuracy(),
                        "questionsData", progress.getQuestionsData(),
                        "cardPositions", progress.getCardPositions(),
                        "timestamp", progress.getTimestamp()
                ));
            } else {
                return Result.success(null);
            }
        } catch (Exception e) {
            return Result.error("加载失败: " + e.getMessage());
        }
    }

    // 4. 保存笔记
    @PostMapping("/save-note")
    @Operation(summary = "保存笔记", description = "保存用户在题目上添加的笔记。")
    public Result saveNote(@RequestBody Map<String, Object> noteData) {
        try {
            // 这里可以实现保存笔记的逻辑
            // 目前返回成功信息
            return Result.success("笔记已保存");
        } catch (Exception e) {
            return Result.error("保存失败: " + e.getMessage());
        }
    }

    // 5. 加载笔记
    @GetMapping("/load-notes")
    @Operation(summary = "加载笔记", description = "加载用户在题目上添加的笔记。")
    public Result loadNotes(@RequestParam Integer userId, @RequestParam Integer subjectId) {
        try {
            // 这里可以实现加载笔记的逻辑
            // 目前返回空数据
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("加载失败: " + e.getMessage());
        }
    }

    // 6. 保存卡片布局
    @PostMapping("/save-layout")
    @Operation(summary = "保存卡片布局", description = "保存用户在大平面模式中对卡片的布局。")
    public Result saveLayout(@RequestBody Map<String, Object> layoutData) {
        try {
            // 这里可以实现保存卡片布局的逻辑
            // 目前返回成功信息
            return Result.success("布局已保存");
        } catch (Exception e) {
            return Result.error("保存失败: " + e.getMessage());
        }
    }

    // 7. 加载卡片布局
    @GetMapping("/load-layout")
    @Operation(summary = "加载卡片布局", description = "加载用户在大平面模式中对卡片的布局。")
    public Result loadLayout(@RequestParam Integer userId, @RequestParam Integer subjectId) {
        try {
            // 这里可以实现加载卡片布局的逻辑
            // 目前返回空数据
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("加载失败: " + e.getMessage());
        }
    }

    // 8. 获取知识点统计数据
    @GetMapping("/knowledge-point-stats")
    @Operation(summary = "获取知识点统计数据", description = "获取指定知识点的统计数据，包括题目数量、掌握度、学习时间等。")
    public Result getKnowledgePointStats(@RequestParam Integer subjectId, @RequestParam Integer userId) {
        try {
            // 获取该知识点及其所有子知识点的题目数量
            List<Integer> subjectIds = subjectService.getDescendantIds(subjectId);
            List<Question> questions = questionService.getQuestionsBySubjectIds(subjectIds);
            int questionCount = questions.size();

            // 查询用户在该知识点下的答题记录
            LambdaQueryWrapper<AnswerRecord> recordWrapper = new LambdaQueryWrapper<>();
            recordWrapper.eq(AnswerRecord::getUserId, userId);

            // 获取该知识点下所有题目的ID
            Set<Long> questionIds = questions.stream()
                    .map(Question::getId)
                    .collect(Collectors.toSet());

            if (!questionIds.isEmpty()) {
                recordWrapper.in(AnswerRecord::getQuestionId, questionIds);
            } else {
                // 如果没有题目，返回空统计
                return Result.success(Map.of(
                        "questionCount", 0,
                        "answeredCount", 0,
                        "correctRate", 0
                ));
            }

            List<AnswerRecord> records = recordService.list(recordWrapper);

            // 计算已作答数量（去重）
            Set<Long> answeredQuestionIds = records.stream()
                    .map(AnswerRecord::getQuestionId)
                    .collect(Collectors.toSet());
            int answeredCount = answeredQuestionIds.size();

            // 计算正确率：只计算每个题目第一次回答正确的情况
            int correctCount = 0;
            if (!records.isEmpty()) {
                // 创建一个集合来跟踪已计算过的题目ID
                Set<Long> processedQuestionIds = new HashSet<>();

                for (AnswerRecord record : records) {
                    // 如果该题目未被处理过，并且回答正确
                    if (!processedQuestionIds.contains(record.getQuestionId()) && record.getIsCorrect() == 1) {
                        correctCount++;
                        processedQuestionIds.add(record.getQuestionId());
                    }
                }
            }

            double correctRate = questionCount > 0 ? Math.round((double) correctCount / questionCount * 100) : 0;

            return Result.success(Map.of(
                    "questionCount", questionCount,
                    "answeredCount", answeredCount,
                    "correctRate", correctRate
            ));
        } catch (Exception e) {
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    // 9. 获取题目推荐
    @GetMapping("/question-recommendation")
    @Operation(summary = "获取题目推荐", description = "根据用户的学习情况，推荐合适的题目。")
    public Result getQuestionRecommendation(@RequestParam Integer userId, @RequestParam Integer subjectId) {
        try {
            // 这里可以实现获取题目推荐的逻辑
            // 目前返回空数据
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    // 10. 重置进度
    @PostMapping("/reset-progress")
    @Operation(summary = "重置进度", description = "重置用户在专项突破模式中的学习进度。")
    public Result resetProgress(@RequestBody Map<String, Object> data) {
        try {
            // 这里可以实现重置进度的逻辑
            // 目前返回成功信息
            return Result.success("进度已重置");
        } catch (Exception e) {
            return Result.error("重置失败: " + e.getMessage());
        }
    }
}
