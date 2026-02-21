package org.example.kaoyanplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.kaoyanplatform.entity.ExamRecord;
import org.example.kaoyanplatform.entity.dto.ExamRecordDetailDTO;
import org.example.kaoyanplatform.entity.dto.SubjectiveQuestionDetailDTO;
import org.example.kaoyanplatform.entity.dto.UserGradingDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ExamRecordService extends IService<ExamRecord> {

    /**
     * 保存用户自评结果
     * @param dto 用户自评数据
     * @return 更新的记录数
     */
    int saveUserGrading(UserGradingDTO dto);

    /**
     * 获取指定考试会话的主观题详情
     * @param sessionId 考试会话ID
     * @return 主观题详情列表
     */
    List<SubjectiveQuestionDetailDTO> getSubjectiveQuestionsBySessionId(Long sessionId);

    /**
     * 获取指定考试会话的答题记录详情（包含题目信息）
     * @param sessionId 考试会话ID
     * @return 答题记录详情列表
     */
    List<ExamRecordDetailDTO> getSessionExamDetailList(Long sessionId);

    /**
     * 计算考试会话的分数统计
     * @param sessionId 考试会话ID
     * @return 分数统计（客观题和主观题分开）
     */
    Map<String, Object> calculateScoreStatistics(Long sessionId);
}
