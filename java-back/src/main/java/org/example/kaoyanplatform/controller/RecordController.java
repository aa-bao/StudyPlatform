package org.example.kaoyanplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.kaoyanplatform.common.Result;
import org.example.kaoyanplatform.entity.AnswerRecord;
import org.example.kaoyanplatform.entity.dto.*;
import org.example.kaoyanplatform.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 答题记录Controller
 */
@Tag(name = "答题记录", description = "答题记录相关接口")
@RestController
@RequestMapping("/record")
public class RecordController {

    @Autowired
    private RecordService recordService;

    /**
     * 1. 提交答题并判分
     */
    @PostMapping("/submit")
    @Operation(summary = "提交答题", description = "提交答案并自动判分，自动记录错题。")
    public Result submitRecord(@RequestBody AnswerRecord examRecord) {
        if (examRecord.getUserId() == null || examRecord.getQuestionId() == null || examRecord.getUserAnswer() == null) {
            return Result.error("参数不完整（userId, questionId, userAnswer 必填）");
        }

        try {
            AnswerSubmitResultDTO result = recordService.submitAnswer(examRecord);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("提交失败: " + e.getMessage());
        }
    }

    /**
     * 2. 获取个人统计数据
     */
    @GetMapping("/stats")
    @Operation(summary = "获取个人统计", description = "返回总题量、正确题量、正确率及总耗时。")
    public Result getStats(@Parameter(description = "用户ID") @RequestParam Integer userId) {
        if (userId == null) {
            return Result.error("未传入用户ID");
        }

        try {
            UserStatsDTO stats = recordService.getUserStats(userId);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 3. 获取最近动态
     */
    @GetMapping("/recent")
    @Operation(summary = "获取最近动态", description = "获取用户最近10条答题记录。")
    public Result getRecentRecords(
            @Parameter(description = "用户ID") @RequestParam Integer userId,
            @Parameter(description = "限制条数") @RequestParam(required = false, defaultValue = "10") Integer limit) {
        if (userId == null) {
            return Result.error("未传入用户ID");
        }

        try {
            List<AnswerRecord> records = recordService.getRecentRecords(userId, limit);
            return Result.success(records);
        } catch (Exception e) {
            return Result.error("获取最近动态失败: " + e.getMessage());
        }
    }

    /**
     * 4. 获取每日测试题目
     */
    @GetMapping("/daily-test/questions")
    @Operation(summary = "获取每日测试题目", description = "根据用户过去7天的答题记录，按优先级智能推荐5-10道题目")
    public Result getDailyTestQuestions(@Parameter(description = "用户ID") @RequestParam Long userId) {
        if (userId == null) {
            return Result.error("未传入用户ID");
        }

        try {
            List<Map<String, Object>> questions = recordService.getDailyTestQuestions(userId);
            return Result.success(questions);
        } catch (Exception e) {
            return Result.error("获取每日测试题目失败: " + e.getMessage());
        }
    }

    /**
     * 5. 获取每日测试状态
     */
    @GetMapping("/daily-test/status")
    @Operation(summary = "获取每日测试状态", description = "获取每日测试状态，包括剩余题目数、完成状态等")
    public Result getDailyTestStatus(@Parameter(description = "用户ID") @RequestParam Long userId) {
        if (userId == null) {
            return Result.error("未传入用户ID");
        }

        try {
            DailyTestStatusDTO status = recordService.getDailyTestStatus(userId);
            return Result.success(status);
        } catch (Exception e) {
            return Result.error("获取每日测试状态失败: " + e.getMessage());
        }
    }

    /**
     * 6. 获取高频错题
     */
    @GetMapping("/hot-mistakes")
    @Operation(summary = "获取高频错题", description = "返回错误率最高的题目列表")
    public Result getHotMistakes(
            @Parameter(description = "用户ID") @RequestParam Integer userId,
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "5") Integer limit
    ) {
        if (userId == null) {
            return Result.error("未传入用户ID");
        }

        try {
            List<Map<String, Object>> hotMistakes = recordService.getHotMistakes(userId, limit);
            return Result.success(hotMistakes);
        } catch (Exception e) {
            return Result.error("获取高频错题失败: " + e.getMessage());
        }
    }

    /**
     * 7. 获取今日统计
     */
    @GetMapping("/today-stats")
    @Operation(summary = "获取今日统计", description = "返回今日刷题数、学习时长、新增错题数")
    public Result getTodayStats(@Parameter(description = "用户ID") @RequestParam Integer userId) {
        if (userId == null) {
            return Result.error("未传入用户ID");
        }

        try {
            Map<String, Object> todayStats = recordService.getTodayStats(userId);
            return Result.success(todayStats);
        } catch (Exception e) {
            return Result.error("获取今日统计失败: " + e.getMessage());
        }
    }

    /**
     * 8. 获取每日测试正确率统计
     */
    @GetMapping("/daily-test/accuracy-stats")
    @Operation(summary = "获取每日测试正确率统计", description = "返回过去7天每天的答题正确率数据，用于折线图展示")
    public Result getDailyTestAccuracyStats(@Parameter(description = "用户ID") @RequestParam Long userId) {
        if (userId == null) {
            return Result.error("未传入用户ID");
        }

        try {
            List<Map<String, Object>> stats = recordService.getDailyTestAccuracyStats(userId);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取正确率统计失败: " + e.getMessage());
        }
    }

    /**
     * 9. 获取用户每日学习统计数据（用于热力图）
     */
    @GetMapping("/daily-study-stats")
    @Operation(summary = "获取每日学习统计", description = "获取用户每日学习统计数据，用于学习热力图展示")
    public Result getDailyStudyStats(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "查询天数") @RequestParam(defaultValue = "30") Integer days
    ) {
        if (userId == null) {
            return Result.error("未传入用户ID");
        }

        try {
            List<StudyHeatmapDTO> stats = recordService.getDailyStudyStats(userId, days);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取学习统计失败: " + e.getMessage());
        }
    }
}
