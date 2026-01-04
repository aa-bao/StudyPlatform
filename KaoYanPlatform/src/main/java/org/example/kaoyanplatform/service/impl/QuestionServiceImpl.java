package org.example.kaoyanplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.kaoyanplatform.entity.Question;
import org.example.kaoyanplatform.mapper.QuestionMapper;
import org.example.kaoyanplatform.service.QuestionService;
import org.springframework.stereotype.Service;

/**
 * 题目服务实现类
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {
}