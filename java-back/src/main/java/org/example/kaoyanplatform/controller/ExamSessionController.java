package org.example.kaoyanplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.kaoyanplatform.common.Result;
import org.example.kaoyanplatform.entity.AnswerRecord;
import org.example.kaoyanplatform.entity.ExamSession;
import org.example.kaoyanplatform.entity.dto.ExamStartDTO;
import org.example.kaoyanplatform.service.ExamRecordService;
import org.example.kaoyanplatform.service.ExamSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "套卷刷题管理", description = "套卷刷题相关接口，包括考试会话管理、答题快照、提交批改等")
@RestController
@RequestMapping("/exam-session")
public class ExamSessionController {

    @Autowired
    private ExamSessionService examSessionService;

    @PostMapping("/start")
    @Operation(summary = "开始或恢复考试", description = "初始化考试会话或恢复未完成的考试，返回试卷完整内容和题目列表。如果用户有未完成的会话则复用，否则创建新会话")
    public Result<ExamStartDTO> startExam(
            @Parameter(description = "用户ID", required = true) @RequestParam String userId,
            @Parameter(description = "试卷ID", required = true) @RequestParam String paperId) {
        try {
            ExamStartDTO result = examSessionService.startOrResumeExam(userId, paperId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/snapshot")
    @Operation(summary = "保存答题快照", description = "自动存盘用户的答题进度，JSON格式存储")
    public Result<String> saveSnapshot(
            @Parameter(description = "考试会话ID", required = true) @RequestParam String sessionId,
            @Parameter(description = "答题快照JSON", required = true) @RequestParam String snapshotJson) {
        try {
            boolean success = examSessionService.saveSnapshot(sessionId, snapshotJson);
            return success ? Result.success("快照保存成功") : Result.error("快照保存失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/switch")
    @Operation(summary = "记录题目切换", description = "用户切换题目时调用，记录切换次数")
    public Result<String> recordSwitch(
            @Parameter(description = "考试会话ID", required = true) @RequestParam String sessionId) {
        try {
            boolean success = examSessionService.recordSwitch(sessionId);
            return success ? Result.success("切换记录成功") : Result.error("切换记录失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/submit")
    @Operation(summary = "提交考试", description = "提交考试并触发AI批改流程，客观题自动判分，主观题AI批改")
    public Result<ExamSession> submitExam(@RequestBody Map<String, Object> requestBody) {
        try {
            Object sessionIdObj = requestBody.get("sessionId");
            String sessionId = sessionIdObj != null ? sessionIdObj.toString() : null;
            String imagesJson = (String) requestBody.getOrDefault("imagesJson", "{}");
            examSessionService.submitExam(sessionId, imagesJson);
            ExamSession session = examSessionService.getById(sessionId);
            return Result.success(session);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{sessionId}")
    @Operation(summary = "放弃考试", description = "删除考试会话，放弃当前考试")
    public Result<String> abandonExam(
            @Parameter(description = "考试会话ID", required = true) @PathVariable String sessionId) {
        try {
            boolean success = examSessionService.removeById(sessionId);
            if (success) {
                return Result.success("考试已放弃");
            } else {
                return Result.error("放弃考试失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "获取考试会话详情", description = "根据会话ID获取完整的考试会话信息，包括总分、AI总结等")
    public Result<ExamSession> getSession(
            @Parameter(description = "考试会话ID", required = true) @PathVariable String sessionId) {
        try {
            ExamSession session = examSessionService.getById(sessionId);
            if (session == null) {
                return Result.error("考试会话不存在");
            }
            return Result.success(session);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户考试历史", description = "根据用户ID获取所有考试会话记录")
    public Result<List<ExamSession>> getUserSessions(
            @Parameter(description = "用户ID", required = true) @PathVariable String userId) {
        try {
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExamSession> wrapper =
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            wrapper.eq(ExamSession::getUserId, userId)
                   .orderByDesc(ExamSession::getCreateTime);
            List<ExamSession> sessions = examSessionService.list(wrapper);
            return Result.success(sessions);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/incomplete")
    @Operation(summary = "获取用户未完成考试", description = "根据用户ID获取所有进行中的考试会话（status=0）")
    public Result<List<ExamSession>> getIncompleteSessions(
            @Parameter(description = "用户ID", required = true) @PathVariable String userId) {
        try {
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExamSession> wrapper =
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            wrapper.eq(ExamSession::getUserId, userId)
                   .eq(ExamSession::getStatus, 0) // 0-进行中
                   // 排除已过期的考试（预期结束时间小于当前时间超过30分钟的，视为已超时）
                   .apply("expected_end_time IS NULL OR expected_end_time > DATE_SUB(NOW(), INTERVAL 30 MINUTE)")
                   .orderByDesc(ExamSession::getCreateTime);
            List<ExamSession> sessions = examSessionService.list(wrapper);
            return Result.success(sessions);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }


    @GetMapping("/admin/all")
    @Operation(summary = "获取所有考试记录（管理员）", description = "分页获取所有用户的考试记录，支持按用户ID、试卷ID、状态筛选")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<ExamSession>> getAllSessions(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "用户ID") @RequestParam(required = false) String userId,
            @Parameter(description = "试卷ID") @RequestParam(required = false) String paperId,
            @Parameter(description = "状态（0-进行中，1-已完成）") @RequestParam(required = false) Integer status) {
        try {
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<ExamSession> page =
                    new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExamSession> wrapper =
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();

            if (userId != null && !userId.isEmpty()) {
                wrapper.eq(ExamSession::getUserId, userId);
            }
            if (paperId != null && !paperId.isEmpty()) {
                wrapper.eq(ExamSession::getPaperId, paperId);
            }
            if (status != null) {
                wrapper.eq(ExamSession::getStatus, status);
            }

            wrapper.orderByDesc(ExamSession::getCreateTime);
            examSessionService.page(page, wrapper);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/admin/stats")
    @Operation(summary = "获取考试统计数据（管理员）", description = "获取总体考试统计：总考试次数、完成率、平均分等")
    public Result<java.util.Map<String, Object>> getExamStats() {
        try {
            java.util.Map<String, Object> stats = new java.util.HashMap<>();

            long totalSessions = examSessionService.count();
            long completedSessions = examSessionService.count(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExamSession>()
                            .eq(ExamSession::getStatus, 1)
            );
            long inProgressSessions = examSessionService.count(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExamSession>()
                            .eq(ExamSession::getStatus, 0)
            );

            stats.put("totalSessions", totalSessions);
            stats.put("completedSessions", completedSessions);
            stats.put("inProgressSessions", inProgressSessions);

            // 计算平均分（只统计已完成的）
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ExamSession> wrapper =
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            wrapper.select("IFNULL(AVG(total_score), 0) as avgScore")
                   .eq("status", 1);
            java.util.Map<String, Object> avgMap = examSessionService.getMap(wrapper);
            stats.put("avgScore", avgMap != null ? avgMap.get("avgScore") : 0);

            return Result.success(stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
