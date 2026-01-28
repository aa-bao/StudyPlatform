package org.example.kaoyanplatform.service;

import org.example.kaoyanplatform.entity.dto.MistakeHeatmapDTO;
import java.util.List;
import java.util.Map;

public interface AdminService {
    
    /**
     * 获取管理员统计数据
     * @param userId 用户ID，传入则返回个人模式数据，不传则返回全局管理员数据
     * @return 统计数据Map
     */
    Map<String, Object> getStatistics(Integer userId);
    
    /**
     * 获取错题热力统计数据
     * @return 错题热力图数据列表
     */
    List<MistakeHeatmapDTO> getMistakeHeatmap();
    
    /**
     * 获取热门错题TOP N
     * @param limit 返回数量限制
     * @return 热门错题列表
     */
    List<MistakeHeatmapDTO.HotMistakeQuestion> getHotMistakes(Integer limit);
    
    /**
     * 获取用户活跃趋势数据
     * @param period 时间周期：week-最近7天，month-最近30天，year-最近12个月
     * @return 趋势数据Map
     */
    Map<String, Object> getActivityTrend(String period);
    
    /**
     * 获取答题正确率趋势数据
     * @param period 时间周期：week-最近7天，month-最近30天
     * @return 正确率趋势数据Map
     */
    Map<String, Object> getAccuracyTrend(String period);
    
    /**
     * 获取题目难度分布数据
     * @return 难度分布数据Map
     */
    Map<String, Object> getDifficultyDistribution();
    
    /**
     * 获取用户答题时段热力图数据
     * @return 时段热力图数据Map
     */
    Map<String, Object> getHourlyActivityHeatmap();
    
    /**
     * 获取科目错题数量统计数据
     * @return 科目错题数量统计Map
     */
    Map<String, Object> getSubjectMistakeCount();
    
    /**
     * 获取实时学习动态
     * @param limit 返回数量限制
     * @return 实时学习动态列表
     */
    List<Map<String, Object>> getRecentActivities(Integer limit);
    
    /**
     * 获取学霸排行榜
     * @param limit 返回数量限制
     * @return 排行榜数据列表
     */
    List<Map<String, Object>> getTopUsers(Integer limit);
}