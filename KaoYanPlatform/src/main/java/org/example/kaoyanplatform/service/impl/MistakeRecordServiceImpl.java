package org.example.kaoyanplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.kaoyanplatform.entity.MistakeRecord;
import org.example.kaoyanplatform.mapper.MistakeRecordMapper;
import org.example.kaoyanplatform.service.MistakeRecordService;
import org.springframework.stereotype.Service;

/**
 * 错题记录服务实现类
 */
@Service
public class MistakeRecordServiceImpl extends ServiceImpl<MistakeRecordMapper, MistakeRecord> implements MistakeRecordService {
    // ServiceImpl 已经实现了基础方法
}
