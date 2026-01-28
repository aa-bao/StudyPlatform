package org.example.kaoyanplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.kaoyanplatform.common.Result;
import org.example.kaoyanplatform.entity.dto.MistakeHeatmapDTO;
import org.example.kaoyanplatform.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 管理员Controller
 */
@Tag(name = "管理员管理", description = "管理员数据统计与监控接口")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 获取全局统计数据
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取统计数据", description = """
            获取管理员统计数据，包括：
            - questionCount: 题目总数
            - userCount: 用户总数（个人模式固定为1）
            - exerciseCount: 练习次数
            - todayActive: 今日活跃用户数（个人模式显示"个人模式"）
            - subjectData: 各科目题目数量分布（政治、英语、数学、408专业课）
            """)
    public Result<Map<String, Object>> getStatistics(
            @Parameter(description = "用户ID，可选参数。传入则返回个人模式数据，不传则返回全局管理员数据")
            @RequestParam(required = false) Integer userId) {
        Map<String, Object> statistics = adminService.getStatistics(userId);
        return Result.success(statistics);
    }

    /**
     * 获取错题热力统计
     */
    @GetMapping("/mistake-heatmap")
    @Operation(summary = "获取错题热力统计", description = """
            获取全局错题热力统计数据，用于展示用户错题时间分布。
            返回数据包含：
            - date: 日期
            - count: 错题数量
            """)
    public Result<List<MistakeHeatmapDTO>> getMistakeHeatmap() {
        List<MistakeHeatmapDTO> heatmap = adminService.getMistakeHeatmap();
        return Result.success(heatmap);
    }

    /**
     * 获取全局错题 TOP N
     */
    @GetMapping("/hot-mistakes")
    @Operation(summary = "获取热门错题", description = """
            获取全局错题TOP N排名，返回错误次数最多的题目。
            返回数据包含：
            - questionId: 题目ID
            - questionTitle: 题目标题
            - errorCount: 错误次数
            """)
    public Result<List<MistakeHeatmapDTO.HotMistakeQuestion>> getHotMistakes(
            @Parameter(description = "返回数量限制，默认20，可根据需要调整")
            @RequestParam(defaultValue = "20") Integer limit) {
        List<MistakeHeatmapDTO.HotMistakeQuestion> hotMistakes = adminService.getHotMistakes(limit);
        return Result.success(hotMistakes);
    }

    /**
     * 获取用户活跃趋势数据
     */
    @GetMapping("/activity-trend")
    @Operation(summary = "获取用户活跃趋势", description = """
            获取用户活跃趋势数据，支持按周/月/年查询。
            返回数据包含：
            - dates: 日期数组
            - activeUsers: 每日活跃用户数数组
            """)
    public Result<Map<String, Object>> getActivityTrend(
            @Parameter(description = "时间周期：week-最近7天，month-最近30天，year-最近12个月")
            @RequestParam(defaultValue = "week") String period) {
        Map<String, Object> result = adminService.getActivityTrend(period);
        return Result.success(result);
    }

    /**
     * 获取答题正确率趋势
     */
    @GetMapping("/accuracy-trend")
    @Operation(summary = "获取答题正确率趋势", description = """
            获取答题正确率趋势数据，支持按周/月查询。
            返回数据包含：
            - dates: 日期数组
            - accuracy: 正确率数组（百分比）
            - totalQuestions: 总题数数组
            """)
    public Result<Map<String, Object>> getAccuracyTrend(
            @Parameter(description = "时间周期：week-最近7天，month-最近30天")
            @RequestParam(defaultValue = "week") String period) {
        Map<String, Object> result = adminService.getAccuracyTrend(period);
        return Result.success(result);
    }

    /**
     * 获取题目难度分布
     */
    @GetMapping("/difficulty-distribution")
    @Operation(summary = "获取题目难度分布", description = """
            获取题目难度分布数据。
            返回数据包含：
            - difficulty: 难度等级数组
            - count: 题目数量数组
            """)
    public Result<Map<String, Object>> getDifficultyDistribution() {
        Map<String, Object> result = adminService.getDifficultyDistribution();
        return Result.success(result);
    }

    /**
     * 获取用户答题时段热力图数据
     */
    @GetMapping("/hourly-activity-heatmap")
    @Operation(summary = "获取用户答题时段热力图", description = """
            获取最近7天24小时的答题活跃度数据。
            返回数据包含：
            - hours: 小时数组(0-23)
            - days: 星期数组(周一到周日)
            - data: 二维数据[count]
            """)
    public Result<Map<String, Object>> getHourlyActivityHeatmap() {
        Map<String, Object> result = adminService.getHourlyActivityHeatmap();
        return Result.success(result);
    }

    /**
     * 获取科目错题数量统计
     */
    @GetMapping("/subject-mistake-count")
    @Operation(summary = "获取科目错题数量统计", description = """
            获取各科目的错题数量统计。
            返回数据包含：
            - subjects: 科目名称数组
            - counts: 错题数量数组
            """)
    public Result<Map<String, Object>> getSubjectMistakeCount() {
        Map<String, Object> result = adminService.getSubjectMistakeCount();
        return Result.success(result);
    }

    /**
     * 获取实时学习动态
     */
    @GetMapping("/recent-activities")
    @Operation(summary = "获取实时学习动态", description = """
            获取最近的学习动态记录。
            返回数据包含：
            - username: 用户名
            - action: 动作描述
            - detail: 详细信息
            - time: 时间描述
            - color: 颜色标识
            """)
    public Result<List<Map<String, Object>>> getRecentActivities(
            @Parameter(description = "返回数量限制，默认10条")
            @RequestParam(defaultValue = "10") Integer limit) {
        List<Map<String, Object>> activities = adminService.getRecentActivities(limit);
        return Result.success(activities);
    }

    /**
     * 获取学霸排行榜
     */
    @GetMapping("/top-users")
    @Operation(summary = "获取学霸排行榜", description = """
            获取学习排名前列的用户。
            返回数据包含：
            - id: 用户ID
            - username: 用户名
            - avatar: 头像
            - exerciseCount: 刷题数
            - accuracy: 正确率
            - studyHours: 学习时长(小时)
            """)
    public Result<List<Map<String, Object>>> getTopUsers(
            @Parameter(description = "返回数量限制，默认10")
            @RequestParam(defaultValue = "10") Integer limit) {
        List<Map<String, Object>> topRankings = adminService.getTopUsers(limit);
        return Result.success(topRankings);
    }
}

