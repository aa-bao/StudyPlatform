package org.example.kaoyanplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.kaoyanplatform.common.Result;
import org.example.kaoyanplatform.entity.ExamRecord;
import org.example.kaoyanplatform.entity.dto.SubjectiveQuestionDetailDTO;
import org.example.kaoyanplatform.entity.dto.UserGradingDTO;
import org.example.kaoyanplatform.service.ExamRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "答题明细管理", description = "答题明细查询和管理接口")
@RestController
@RequestMapping("/exam-record")
public class ExamRecordController {

    @Autowired
    private ExamRecordService examRecordService;

    @GetMapping("/question/{questionId}")
    @Operation(summary = "根据题目ID查询答题记录", description = "查询指定题目在所有考试中的答题情况")
    public Result<List<ExamRecord>> getDetailsByQuestionId(
            @Parameter(description = "题目ID", required = true) @PathVariable String questionId) {
        try {
            LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ExamRecord::getQuestionId, Long.parseLong(questionId))
                   .orderByDesc(ExamRecord::getCreateTime);
            List<ExamRecord> details = examRecordService.list(wrapper);
            return Result.success(details);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取答题明细详情", description = "根据答题明细ID获取详细信息")
    public Result<ExamRecord> getDetail(
            @Parameter(description = "答题明细ID", required = true) @PathVariable String id) {
        try {
            ExamRecord detail = examRecordService.getById(Long.parseLong(id));
            if (detail == null) {
                return Result.error("答题明细不存在");
            }
            return Result.success(detail);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/save-user-grading")
    @Operation(summary = "保存用户自评结果", description = "用户提交主观题的自我批改结果")
    public Result<String> saveUserGrading(@RequestBody UserGradingDTO dto) {
        try {
            int updateCount = examRecordService.saveUserGrading(dto);
            return Result.success("成功保存 " + updateCount + " 道题的批改结果");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/subjective-questions/{sessionId}")
    @Operation(summary = "获取主观题列表", description = "获取指定考试会话的所有主观题及用户答案")
    public Result<List<SubjectiveQuestionDetailDTO>> getSubjectiveQuestions(
            @Parameter(description = "考试会话ID", required = true) @PathVariable Long sessionId) {
        try {
            List<SubjectiveQuestionDetailDTO> questions = examRecordService.getSubjectiveQuestionsBySessionId(sessionId);
            return Result.success(questions);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/score-statistics/{sessionId}")
    @Operation(summary = "获取分数统计", description = "计算客观题和主观题的得分统计")
    public Result<Map<String, Object>> getScoreStatistics(
            @Parameter(description = "考试会话ID", required = true) @PathVariable Long sessionId) {
        try {
            Map<String, Object> statistics = examRecordService.calculateScoreStatistics(sessionId);
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/session/{sessionId}")
    @Operation(summary = "获取会话答题详情", description = "获取指定考试会话的所有答题记录")
    public Result<List<ExamRecord>> getSessionDetails(
            @Parameter(description = "考试会话ID", required = true) @PathVariable Long sessionId) {
        try {
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExamRecord> wrapper =
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            wrapper.eq(ExamRecord::getSessionId, sessionId);
            List<ExamRecord> records = examRecordService.list(wrapper);
            return Result.success(records);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
