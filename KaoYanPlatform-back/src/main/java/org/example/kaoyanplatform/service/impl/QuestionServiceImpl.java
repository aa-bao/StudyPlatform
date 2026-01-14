package org.example.kaoyanplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.kaoyanplatform.entity.MapQuestionBook;
import org.example.kaoyanplatform.entity.MapQuestionSubject;
import org.example.kaoyanplatform.entity.Question;
import org.example.kaoyanplatform.entity.Subject;
import org.example.kaoyanplatform.entity.Book;
import org.example.kaoyanplatform.entity.dto.QuestionDTO;
import org.example.kaoyanplatform.entity.dto.LLMQuestionOutputDTO;
import org.example.kaoyanplatform.mapper.MapQuestionBookMapper;
import org.example.kaoyanplatform.mapper.MapQuestionSubjectMapper;
import org.example.kaoyanplatform.mapper.QuestionMapper;
import org.example.kaoyanplatform.service.MapQuestionBookService;
import org.example.kaoyanplatform.service.MapQuestionSubjectService;
import org.example.kaoyanplatform.service.QuestionService;
import org.example.kaoyanplatform.service.SubjectService;
import org.example.kaoyanplatform.service.BookService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 题目Service实现类
 * 使用映射表（map_question_subject、map_question_book）管理题目与科目、书本的关系
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Autowired
    private MapQuestionSubjectMapper mapQuestionSubjectMapper;

    @Autowired
    private MapQuestionBookMapper mapQuestionBookMapper;

    @Autowired
    private MapQuestionSubjectService mapQuestionSubjectService;

    @Autowired
    private MapQuestionBookService mapQuestionBookService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private BookService bookService;

    @Value("${zhipu.api.key}")
    private String apiKey;

    @Override
    public List<Question> getQuestionsBySubjectIds(List<Integer> subjectIds) {
        if (subjectIds == null || subjectIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 使用映射表 map_question_subject
        QueryWrapper<MapQuestionSubject> qsWrapper = new QueryWrapper<>();
        qsWrapper.in("subject_id", subjectIds);
        qsWrapper.select("question_id");
        List<Object> qIds = mapQuestionSubjectMapper.selectObjs(qsWrapper);

        if (qIds == null || qIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> questionIds = qIds.stream().map(o -> Long.valueOf(o.toString())).collect(Collectors.toList());
        return listByIds(questionIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveQuestionWithRelations(QuestionDTO questionDTO) {
        // 1. 保存题目基本信息
        Question question = new Question();
        BeanUtils.copyProperties(questionDTO, question);
        question.setCreateTime(LocalDateTime.now());
        boolean saved = save(question);

        if (!saved) {
            return false;
        }

        Long questionId = question.getId();

        // 2. 保存题目-书本关联关系
        if (questionDTO.getBookIds() != null && !questionDTO.getBookIds().isEmpty()) {
            for (Integer bookId : questionDTO.getBookIds()) {
                MapQuestionBook relation = new MapQuestionBook();
                relation.setQuestionId(questionId);
                relation.setBookId(bookId);
                mapQuestionBookMapper.insert(relation);
            }
        }

        // 3. 保存题目-科目关联关系
        if (questionDTO.getSubjectIds() != null && !questionDTO.getSubjectIds().isEmpty()) {
            for (Integer subjectId : questionDTO.getSubjectIds()) {
                MapQuestionSubject relation = new MapQuestionSubject();
                relation.setQuestionId(questionId);
                relation.setSubjectId(subjectId);
                mapQuestionSubjectMapper.insert(relation);
            }
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateQuestionWithRelations(QuestionDTO questionDTO) {
        if (questionDTO.getId() == null) {
            return false;
        }

        // 1. 更新题目基本信息
        Question question = new Question();
        BeanUtils.copyProperties(questionDTO, question);
        boolean updated = updateById(question);

        if (!updated) {
            return false;
        }

        Long questionId = questionDTO.getId();

        // 2. 删除旧的题目-书本关联关系，重新建立
        mapQuestionBookService.removeAllQuestionBookRelations(questionId);
        if (questionDTO.getBookIds() != null && !questionDTO.getBookIds().isEmpty()) {
            for (Integer bookId : questionDTO.getBookIds()) {
                mapQuestionBookService.addQuestionBookRelation(questionId, bookId);
            }
        }

        // 3. 删除旧的题目-科目关联关系，重新建立
        mapQuestionSubjectService.removeAllQuestionSubjectRelations(questionId);
        if (questionDTO.getSubjectIds() != null && !questionDTO.getSubjectIds().isEmpty()) {
            for (Integer subjectId : questionDTO.getSubjectIds()) {
                mapQuestionSubjectService.addQuestionSubjectRelation(questionId, subjectId);
            }
        }

        return true;
    }

    @Override
    public List<Question> getQuestionsByBookId(Integer bookId) {
        List<Long> questionIds = mapQuestionBookMapper.getQuestionIdsByBookId(bookId);
        if (questionIds == null || questionIds.isEmpty()) {
            return Collections.emptyList();
        }
        return listByIds(questionIds);
    }

    @Override
    public Page<Question> questionPage(Page<Question> page, List<Integer> subjectIds, Integer bookId) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();

        // 如果提供了subjectIds（多个科目），通过映射表查询
        if (subjectIds != null && !subjectIds.isEmpty()) {
            // 查询与任一科目关联的题目
            Set<Long> questionIds = new HashSet<>();
            for (Integer subjectId : subjectIds) {
                List<Long> ids = mapQuestionSubjectMapper.getQuestionIdsBySubjectId(subjectId);
                if (ids != null && !ids.isEmpty()) {
                    questionIds.addAll(ids);
                }
            }
            if (!questionIds.isEmpty()) {
                queryWrapper.in("id", questionIds);
            } else {
                // 没有找到题目，返回空页
                return new Page<>(page.getCurrent(), page.getSize());
            }
        }

        // 如果提供了bookId，通过映射表查询
        if (bookId != null) {
            List<Long> questionIds = mapQuestionBookMapper.getQuestionIdsByBookId(bookId);
            if (questionIds != null && !questionIds.isEmpty()) {
                if (subjectIds != null && !subjectIds.isEmpty()) {
                    // 如果同时提供了subjectIds和bookId，取交集
                    Set<Long> finalQuestionIds = new HashSet<>(questionIds);
                    queryWrapper.in("id", finalQuestionIds);
                } else {
                    queryWrapper.in("id", questionIds);
                }
            } else {
                // 没有找到题目，返回空页
                return new Page<>(page.getCurrent(), page.getSize());
            }
        }

        queryWrapper.orderByDesc("id");
        Page<Question> resultPage = page(page, queryWrapper);

        // 填充 subjectName 和 bookName
        if (resultPage.getRecords() != null && !resultPage.getRecords().isEmpty()) {
            for (Question q : resultPage.getRecords()) {
                // 填充科目信息
                List<Integer> sIds = mapQuestionSubjectService.getSubjectIdsByQuestionId(q.getId());
                if (sIds != null && !sIds.isEmpty()) {
                    q.setSubjectIds(sIds);
                    
                    List<String> subjectNames = new ArrayList<>();
                    for (Integer sId : sIds) {
                        Subject subject = subjectService.getById(sId);
                        if (subject != null) {
                            subjectNames.add(subject.getName());
                        }
                    }
                    q.setSubjectNames(subjectNames);
                    // 兼容性处理
                    q.setSubjectIds(sIds);
                    q.setSubjectName(sIds.isEmpty() ? null : subjectNames.get(0));
                }

                // 填充书本信息
                List<Integer> bIds = mapQuestionBookService.getBookIdsByQuestionId(q.getId());
                if (bIds != null && !bIds.isEmpty()) {
                    q.setBookIds(bIds);
                    
                    List<String> bookNames = new ArrayList<>();
                    for (Integer bId : bIds) {
                        Book book = bookService.getById(bId);
                        if (book != null) {
                            bookNames.add(book.getName());
                        }
                    }
                    q.setBookNames(bookNames);
                    // 兼容性处理
                    q.setBookName(bIds.isEmpty() ? null : bookNames.get(0));
                }
            }
        }

        return resultPage;
    }

    @Override
    public LLMQuestionOutputDTO recognizeImageToText(MultipartFile file) throws Exception {
        // 1. 初始化客户端 (使用 ClientV4)
        ClientV4 client = new ClientV4.Builder(apiKey).build();

        // 2. 图片转 Base64
        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

        // 3. 构造多模态内容列表
        List<Map<String, Object>> contentList = new ArrayList<>();

        // 3.1 文本提示
        Map<String, Object> textMap = new HashMap<>();
        textMap.put("type", "text");
        textMap.put("text", "你是一个考研题目解析专家。请识别图片中的题目内容，并严格按照 JSON 格式返回。要求：\n" +
                "1. **清洗数据**：忽略水印、页码、手写笔记等无关信息。\n" +
                "2. **公式处理**：所有数学符号、公式、科学表达式必须转换为标准的 LaTeX 语法，并统一用 $ 包裹（如 $x^2 + y^2 = r^2$）。\n" +
                "3. **字段定义**：\n" +
                "   - `content`: 只要题干内容，如有题号需删除。\n" +
                "   - `options`: 选项列表，若无选项则返回空数组 []。\n" +
                "   - `answer`: 正确答案（若图片中包含）。\n" +
                "   - `analysis`: 题目解析（若图片中包含）。\n" +
                "4. **输出约束**：只输出 JSON 字符串本身，不得包含 Markdown 代码块标记（如 ```json）或任何解释性文字。");
        contentList.add(textMap);

        // 3.2 图片内容
        Map<String, Object> imageMap = new HashMap<>();
        imageMap.put("type", "image_url");
        
        Map<String, Object> imageUrl = new HashMap<>();
        imageUrl.put("url", base64Image); // V4 SDK 自动识别 Base64，无需 url 前缀，或者根据文档加前缀
        imageMap.put("image_url", imageUrl);
        
        contentList.add(imageMap);

        // 4. 构造消息列表
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), contentList);
        messages.add(chatMessage);

        // 5. 构造请求 ID
        String requestId = String.format("req-%d", System.currentTimeMillis());

        // 6. 构造请求参数
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("glm-4.6v")
                .stream(Boolean.FALSE)
                .invokeMethod("invoke")
                .messages(messages)
                .requestId(requestId)
                .build();

        // 7. 调用接口
        ModelApiResponse response = client.invokeModelApi(request);

        if (response.isSuccess()) {
            String jsonString = response.getData().getChoices().get(0).getMessage().getContent().toString().trim();
            // 解析 JSON 到 DTO
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(jsonString, LLMQuestionOutputDTO.class);
            } catch (Exception e) {
                // 如果解析失败，创建一个兜底对象
                LLMQuestionOutputDTO dto = new LLMQuestionOutputDTO();
                dto.setContent(jsonString); // 直接作为内容
                dto.setOptions(new ArrayList<>());
                dto.setAnswer("");
                dto.setAnalysis("");
                return dto;
            }
        } else {
            throw new RuntimeException("GLM 识别失败: " + response.getMsg());
        }
    }
}