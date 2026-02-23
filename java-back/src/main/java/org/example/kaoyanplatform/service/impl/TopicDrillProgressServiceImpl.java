package org.example.kaoyanplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.kaoyanplatform.entity.TopicDrillProgress;
import org.example.kaoyanplatform.mapper.TopicDrillProgressMapper;
import org.example.kaoyanplatform.service.TopicDrillProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopicDrillProgressServiceImpl extends ServiceImpl<TopicDrillProgressMapper, TopicDrillProgress> implements TopicDrillProgressService {

    @Autowired
    private TopicDrillProgressMapper topicDrillProgressMapper;

    @Override
    public TopicDrillProgress saveProgress(TopicDrillProgress progress) {
        // 检查是否已存在该用户的进度记录
        LambdaQueryWrapper<TopicDrillProgress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TopicDrillProgress::getUserId, progress.getUserId())
                .eq(TopicDrillProgress::getSubjectId, progress.getSubjectId());

        TopicDrillProgress existingProgress = this.getOne(queryWrapper);

        if (existingProgress != null) {
            // 如果已存在，更新记录
            existingProgress.setQuestionCount(progress.getQuestionCount());
            existingProgress.setAnsweredCount(progress.getAnsweredCount());
            existingProgress.setCorrectCount(progress.getCorrectCount());
            existingProgress.setAccuracy(progress.getAccuracy());
            existingProgress.setQuestionsData(progress.getQuestionsData());
            existingProgress.setCardPositions(progress.getCardPositions());
            existingProgress.setTimestamp(progress.getTimestamp());
            this.updateById(existingProgress);
            return existingProgress;
        } else {
            // 如果不存在，新增记录
            this.save(progress);
            return progress;
        }
    }

    @Override
    public TopicDrillProgress loadProgress(Long userId, Integer subjectId) {
        LambdaQueryWrapper<TopicDrillProgress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TopicDrillProgress::getUserId, userId)
                .eq(TopicDrillProgress::getSubjectId, subjectId);
        return this.getOne(queryWrapper);
    }

    @Override
    public void deleteProgress(Long userId, Integer subjectId) {
        LambdaQueryWrapper<TopicDrillProgress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TopicDrillProgress::getUserId, userId)
                .eq(TopicDrillProgress::getSubjectId, subjectId);
        this.remove(queryWrapper);
    }
}
