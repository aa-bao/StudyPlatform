package org.example.kaoyanplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.kaoyanplatform.entity.TopicDrillProgress;

public interface TopicDrillProgressService extends IService<TopicDrillProgress> {

    /**
     * 保存学习进度
     */
    TopicDrillProgress saveProgress(TopicDrillProgress progress);

    /**
     * 加载学习进度
     */
    TopicDrillProgress loadProgress(Long userId, Integer subjectId);

    /**
     * 删除学习进度
     */
    void deleteProgress(Long userId, Integer subjectId);
}
