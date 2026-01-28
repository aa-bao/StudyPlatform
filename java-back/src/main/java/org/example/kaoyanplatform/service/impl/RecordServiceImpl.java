package org.example.kaoyanplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.kaoyanplatform.entity.AnswerRecord;
import org.example.kaoyanplatform.mapper.AnswerRecordMapper;
import org.example.kaoyanplatform.service.RecordService;
import org.springframework.stereotype.Service;

/**
 * 答题记录服务实现类
 */
@Service
public class RecordServiceImpl extends ServiceImpl<AnswerRecordMapper, AnswerRecord> implements RecordService {
}