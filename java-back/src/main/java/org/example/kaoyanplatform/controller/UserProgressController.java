package org.example.kaoyanplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.kaoyanplatform.common.Result;
import org.example.kaoyanplatform.entity.AnswerRecord;
import org.example.kaoyanplatform.entity.dto.StudyHeatmapDTO;
import org.example.kaoyanplatform.service.RecordService;
import org.example.kaoyanplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "用户学习进度管理", description = "用户学习进度查询和管理接口")
@RestController
@RequestMapping("/user-progress")
public class UserProgressController {

    @Autowired
    private RecordService recordService;

    @Autowired
    private UserService userService;

    /**
     * 获取学习进度统计（管理员）
     */
    @GetMapping("/admin/stats")
    @Operation(summary = "获取学习进度统计", description = """
            获取管理员统计数据，包括：
            - totalUsers: 总用户数
            - totalFinished: 总完成题目数
            - totalCorrect: 总正确题目数
            - avgAccuracy: 平均正确率
            """)
    public Result<Map<String, Object>> getProgressStats() {
        try {
            Map<String, Object> stats = new HashMap<>();

            // 统计总用户数
            long totalUsers = userService.count();

            // 统计总答题数
            long totalFinished = recordService.count();

            // 统计总正确数
            long totalCorrect = recordService.count(
                new LambdaQueryWrapper<AnswerRecord>().eq(AnswerRecord::getIsCorrect, 1)
            );

            // 计算平均正确率
            double avgAccuracy = totalFinished > 0
                ? (double) totalCorrect / totalFinished * 100
                : 0.0;

            stats.put("totalUsers", totalUsers);
            stats.put("totalFinished", totalFinished);
            stats.put("totalCorrect", totalCorrect);
            stats.put("avgAccuracy", avgAccuracy);

            return Result.success(stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取学习排行榜（管理员）
     */
    @GetMapping("/admin/ranking")
    @Operation(summary = "获取学习排行榜", description = """
            获取学习排行榜数据。
            返回数据包含用户的学习统计信息
            """)
    public Result<List<Map<String, Object>>> getLearningRanking(
            @Parameter(description = "返回数量限制，默认10")
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            // 获取所有答题记录
            List<AnswerRecord> allRecords = recordService.list();

            // 按用户分组统计
            Map<Long, List<AnswerRecord>> userRecordsMap = allRecords.stream()
                .collect(Collectors.groupingBy(AnswerRecord::getUserId));

            // 构建排行榜数据
            List<Map<String, Object>> ranking = userRecordsMap.entrySet().stream()
                .map(entry -> {
                    List<AnswerRecord> records = entry.getValue();
                    int totalFinished = records.size();
                    int totalCorrect = (int) records.stream()
                        .filter(r -> r.getIsCorrect() != null && r.getIsCorrect() == 1)
                        .count();

                    // 计算正确率（返回Double类型）
                    double accuracy = totalFinished > 0 ? (double) totalCorrect / totalFinished * 100 : 0.0;

                    // 获取用户信息
                    var user = userService.getById(entry.getKey());

                    Map<String, Object> item = new HashMap<>();
                    item.put("user_id", entry.getKey());
                    item.put("nickname", user != null ? user.getNickname() : "未知用户");
                    item.put("avatar", user != null ? user.getAvatar() : "/img/default-avatar.png");
                    item.put("total_finished", totalFinished);
                    item.put("total_correct", totalCorrect);
                    item.put("accuracy", accuracy);
                    return item;
                })
                .sorted((a, b) -> {
                    int finishedA = (Integer) a.get("total_finished");
                    int finishedB = (Integer) b.get("total_finished");
                    // 先按完成数排序
                    return Integer.compare(finishedB, finishedA);
                })
                .limit(limit)
                .collect(Collectors.toList());

            return Result.success(ranking);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取所有用户学习进度（管理员）
     */
    @GetMapping("/admin/all")
    @Operation(summary = "获取所有用户学习进度", description = """
            分页获取所有用户的学习进度数据
            """)
    public Result<Map<String, Object>> getAllUserProgress(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "用户ID") @RequestParam(required = false) Integer userId,
            @Parameter(description = "科目ID") @RequestParam(required = false) Integer subjectId) {
        try {
            // 获取所有答题记录
            LambdaQueryWrapper<AnswerRecord> wrapper = new LambdaQueryWrapper<>();
            if (userId != null) {
                wrapper.eq(AnswerRecord::getUserId, Long.valueOf(userId));
            }
            // 注意：AnswerRecord没有subjectId字段，所以subjectId参数暂时忽略

            List<AnswerRecord> allRecords = recordService.list(wrapper);

            // 按用户分组统计
            Map<Long, List<AnswerRecord>> userRecordsMap = allRecords.stream()
                .collect(Collectors.groupingBy(AnswerRecord::getUserId));

            // 构建进度数据列表
            List<Map<String, Object>> records = userRecordsMap.entrySet().stream()
                .map(entry -> {
                    List<AnswerRecord> answerRecords = entry.getValue();
                    int finishedCount = answerRecords.size();
                    int correctCount = (int) answerRecords.stream()
                        .filter(r -> r.getIsCorrect() != null && r.getIsCorrect() == 1)
                        .count();

                    Map<String, Object> item = new HashMap<>();
                    item.put("id", entry.getKey());
                    item.put("userId", entry.getKey());
                    item.put("subjectId", 0); // AnswerRecord没有subjectId，暂时设为0
                    item.put("finishedCount", finishedCount);
                    item.put("correctCount", correctCount);
                    return item;
                })
                .sorted((a, b) -> Integer.compare((Integer) b.get("finishedCount"), (Integer) a.get("finishedCount")))
                .collect(Collectors.toList());

            // 分页处理
            int total = records.size();
            int start = (pageNum - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            List<Map<String, Object>> pagedRecords = start < total
                ? records.subList(start, end)
                : new ArrayList<>();

            Map<String, Object> result = new HashMap<>();
            result.put("records", pagedRecords);
            result.put("total", total);

            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取学习热力图数据
     */
    @GetMapping("/heatmap/{userId}")
    @Operation(summary = "获取学习热力图数据", description = """
            获取用户最近N天的学习热力图数据，包括每日学习时长和答题数量。
            参数:
            - userId: 用户ID
            - days: 查询天数（默认180，最大365）
            """)
    public Result<List<StudyHeatmapDTO>> getStudyHeatmap(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "查询天数", example = "180")
            @RequestParam(defaultValue = "180") Integer days) {

        if (days > 365) days = 365;
        if (days < 1) days = 1;

        List<StudyHeatmapDTO> data = recordService.getDailyStudyStats(userId, days);
        return Result.success(data);
    }
}
