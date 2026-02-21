package org.example.kaoyanplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.kaoyanplatform.entity.User;
import org.example.kaoyanplatform.entity.dto.HomePageDataDTO;
import org.example.kaoyanplatform.entity.dto.UserStudyStatsDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 用户服务类：继承 IService 获得 count, list, save 等内置方法
 */
@Service
public interface UserService extends IService<User> {
    User login(String username, String password);
    boolean register(User user);

    /**
     * 获取用户学习统计数据（用于管理员监控）
     */
    UserStudyStatsDTO getUserStudyStats(Long userId);

    /**
     * 获取用户首页数据
     * @param userId 用户ID
     * @return 首页数据
     */
    HomePageDataDTO getHomePageData(Long userId);

    /**
     * 获取智能学习建议
     * @param userId 用户ID
     * @return 学习建议列表
     */
    List<Map<String, Object>> getRecommendations(Long userId);
}
