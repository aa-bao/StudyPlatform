package org.example.kaoyanplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.kaoyanplatform.entity.AnswerRecord;
import org.example.kaoyanplatform.entity.dto.*;

import java.util.List;
import java.util.Map;

/**
 * 答题记录服务类
 */
public interface RecordService extends IService<AnswerRecord> {
    /**
     * 提交答案并判分
     * @param examRecord 答题记录
     * @return 判分结果
     */
    AnswerSubmitResultDTO submitAnswer(AnswerRecord examRecord);

    /**
     * 获取用户统计数据
     * @param userId 用户ID
     * @return 统计数据
     */
    UserStatsDTO getUserStats(Integer userId);

    /**
     * 获取最近答题记录
     * @param userId 用户ID
     * @param limit 限制条数
     * @return 答题记录列表
     */
    List<AnswerRecord> getRecentRecords(Integer userId, Integer limit);

    /**
     * 获取每日测试题目
     * @param userId 用户ID
     * @return 题目列表
     */
    List<Map<String, Object>> getDailyTestQuestions(Long userId);

    /**
     * 获取每日测试状态
     * @param userId 用户ID
     * @return 每日测试状态
     */
    DailyTestStatusDTO getDailyTestStatus(Long userId);

    /**
     * 获取每日测试正确率统计
     * @param userId 用户ID
     * @return 每日正确率统计列表
     */
    List<Map<String, Object>> getDailyTestAccuracyStats(Long userId);

    /**
     * 获取用户每日学习统计数据（用于热力图）
     * @param userId 用户ID
     * @param days 查询天数
     * @return 每日学习数据列表
     */
    List<StudyHeatmapDTO> getDailyStudyStats(Long userId, Integer days);

    /**
     * 获取高频错题
     * @param userId 用户ID
     * @param limit 返回数量
     * @return 高频错题列表
     */
    List<Map<String, Object>> getHotMistakes(Integer userId, Integer limit);

    /**
     * 获取今日统计
     * @param userId 用户ID
     * @return 今日统计数据
     */
    Map<String, Object> getTodayStats(Integer userId);

    /**
     * 更新错题本
     * @param examRecord 答题记录
     */
    void updateMistakeBook(AnswerRecord examRecord);
}