package org.example.kaoyanplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.kaoyanplatform.client.PythonBackendClient;
import org.example.kaoyanplatform.entity.*;
import org.example.kaoyanplatform.entity.dto.QuestionDTO;
import org.example.kaoyanplatform.entity.dto.QuestionExportDTO;
import org.example.kaoyanplatform.entity.dto.QuestionImportDTO;
import org.example.kaoyanplatform.mapper.QuestionBookRelMapper;
import org.example.kaoyanplatform.mapper.QuestionMapper;
import org.example.kaoyanplatform.mapper.QuestionSubjectRelMapper;
import org.example.kaoyanplatform.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Autowired
    private QuestionSubjectRelMapper mapQuestionSubjectMapper;

    @Autowired
    private QuestionBookRelMapper mapQuestionBookMapper;

    @Autowired
    private QuestionSubjectRelService mapQuestionSubjectService;

    @Autowired
    private QuestionBookRelService mapQuestionBookService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ExerciseBookService bookService;

    @Autowired
    private ErrorQuestionService mistakeRecordService;

    @Autowired
    private QuestionPaperRelService mapPaperQuestionService;

    @Autowired
    private PythonBackendClient pythonBackendClient;

    @Override
    public List<Question> getQuestionsBySubjectIds(List<Integer> subjectIds) {
        if (subjectIds == null || subjectIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 使用映射表 map_question_subject
        QueryWrapper<QuestionSubjectRel> qsWrapper = new QueryWrapper<>();
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

        // 复制基本字段
        question.setId(questionDTO.getId());
        question.setType(questionDTO.getType());
        question.setDifficulty(questionDTO.getDifficulty());
        question.setCreateTime(LocalDateTime.now());

        // 合并 contentJson（兼容字段优先，然后是 contentJson 本身）
        question.setContentJson(questionDTO.getMergedContentJson());

        boolean saved = save(question);

        if (!saved) {
            return false;
        }

        Long questionId = question.getId();

        // 2. 保存题目-书本关联关系
        if (questionDTO.getBookIds() != null && !questionDTO.getBookIds().isEmpty()) {
            for (Integer bookId : questionDTO.getBookIds()) {
                QuestionBookRel relation = new QuestionBookRel();
                relation.setQuestionId(questionId);
                relation.setBookId(bookId);
                mapQuestionBookMapper.insert(relation);
            }
        }

        // 3. 保存题目-科目关联关系
        if (questionDTO.getSubjectIds() != null && !questionDTO.getSubjectIds().isEmpty()) {
            for (Integer subjectId : questionDTO.getSubjectIds()) {
                QuestionSubjectRel relation = new QuestionSubjectRel();
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

        // 复制基本字段
        question.setId(questionDTO.getId());
        question.setType(questionDTO.getType());
        question.setDifficulty(questionDTO.getDifficulty());

        // 合并 contentJson（兼容字段优先，然后是 contentJson 本身）
        question.setContentJson(questionDTO.getMergedContentJson());

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
        IPage<Question> iPage = super.page(page, queryWrapper);
        Page<Question> resultPage = new Page<>(iPage.getCurrent(), iPage.getSize(), iPage.getTotal());
        resultPage.setRecords(iPage.getRecords());

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
                        ExerciseBook book = bookService.getById(bId);
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
    public boolean isQuestionExist(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }

        // 由于 content 现在存储在 JSON 字段中，我们需要先查询所有题目然后在内存中比较
        // 或者使用原生 SQL 的 JSON_EXTRACT 函数（更高效）
        // 这里使用内存比较的方式（简单但性能稍差）
        List<Question> allQuestions = list();

        for (Question q : allQuestions) {
            String qContent = q.getContent();
            if (qContent != null && qContent.equals(content)) {
                return true;
            }
        }

        return false;

        // TODO: 如果数据量大，建议改用原生 SQL 查询:
        // SELECT COUNT(*) > 0 FROM question
        // WHERE JSON_EXTRACT(content_json, '$.content') = #{content}
    }

    @Override
    public List<Question> findSimilarQuestions(String content) {
        if (content == null || content.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // 提取关键词进行模糊匹配
        // 去除空格、换行等
        String normalizedContent = content.replaceAll("\\s+", "").trim();

        // 取前50个字符作为关键词
        String keyword = normalizedContent.length() > 50
                ? normalizedContent.substring(0, 50)
                : normalizedContent;

        // 由于 content 现在存储在 JSON 字段中，我们需要先查询所有题目然后在内存中比较
        List<Question> allQuestions = list();
        List<Question> similarQuestions = new ArrayList<>();

        for (Question q : allQuestions) {
            String qContent = q.getContent();
            if (qContent != null) {
                String normalizedQContent = qContent.replaceAll("\\s+", "").trim();
                if (normalizedQContent.contains(keyword) || keyword.contains(normalizedQContent.substring(0, Math.min(50, normalizedQContent.length())))) {
                    similarQuestions.add(q);
                    if (similarQuestions.size() >= 10) {
                        break;
                    }
                }
            }
        }

        return similarQuestions;

        // TODO: 如果数据量大，建议改用原生 SQL 查询:
        // SELECT * FROM question
        // WHERE JSON_EXTRACT(content_json, '$.content') LIKE CONCAT('%', #{keyword}, '%')
        // LIMIT 10
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteQuestionWithRelations(Long id) {
        // 删除题目相关的书本关联关系
        mapQuestionBookService.removeAllQuestionBookRelations(id);
        // 删除题目相关的科目关联关系
        mapQuestionSubjectService.removeAllQuestionSubjectRelations(id);
        // 删除题目本身
        return removeById(id);
    }

    @Override
    public Question getQuestionWithDetails(Long id) {
        Question question = getById(id);
        if (question == null) return null;

        // 填充书本信息
        List<Integer> bookIds = mapQuestionBookService.getBookIdsByQuestionId(id);
        question.setBookIds(bookIds != null ? bookIds : Collections.emptyList());

        if (bookIds != null && !bookIds.isEmpty()) {
            List<String> bookNames = new ArrayList<>();
            for (Integer bookId : bookIds) {
                ExerciseBook book = bookService.getById(bookId);
                if (book != null) {
                    bookNames.add(book.getName());
                }
            }
            question.setBookNames(bookNames);
            question.setBookName(bookNames.isEmpty() ? null : bookNames.get(0));
        } else {
            question.setBookNames(Collections.emptyList());
            question.setBookName(null);
        }

        // 填充科目信息
        List<Integer> subjectIds = mapQuestionSubjectService.getSubjectIdsByQuestionId(id);
        question.setSubjectIds(subjectIds != null ? subjectIds : Collections.emptyList());

        if (subjectIds != null && !subjectIds.isEmpty()) {
            List<String> subjectNames = new ArrayList<>();
            for (Integer subjectId : subjectIds) {
                Subject subject = subjectService.getById(subjectId);
                if (subject != null) {
                    subjectNames.add(subject.getName());
                }
            }
            question.setSubjectNames(subjectNames);
            question.setSubjectName(subjectNames.isEmpty() ? null : subjectNames.get(0));
        } else {
            question.setSubjectNames(Collections.emptyList());
            question.setSubjectName(null);
        }

        return question;
    }

    @Override
    public List<Question> getQuestionsByExportConfig(QuestionExportDTO exportDTO) {
        List<Question> questions = new ArrayList<>();

        // 优先级1: 自定义题目ID列表
        if (exportDTO.getQuestionIds() != null && !exportDTO.getQuestionIds().isEmpty()) {
            questions.addAll(listByIds(exportDTO.getQuestionIds()));
        }
        // 优先级2: 按试卷导出
        else if (exportDTO.getPaperId() != null) {
            List<Question> paperQuestions = mapPaperQuestionService.getQuestionsWithDetails(exportDTO.getPaperId());
            questions.addAll(paperQuestions);
        }
        // 优先级3: 按习题册导出
        else if (exportDTO.getBookId() != null) {
            List<Question> bookQuestions = getQuestionsByBookId(exportDTO.getBookId());
            questions.addAll(bookQuestions);
        }
        // 优先级4: 按科目导出
        else if (exportDTO.getSubjectId() != null) {
            List<Question> subjectQuestions = getQuestionsBySubjectIds(Collections.singletonList(exportDTO.getSubjectId()));
            questions.addAll(subjectQuestions);
        }

        return questions;
    }

    @Override
    public List<Question> getErrorQuestionsWithTime(Integer userId) {
        // 查询错题记录，按update_time降序排列，返回所有错题
        List<ErrorQuestion> list = mistakeRecordService.list(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ErrorQuestion>()
                        .eq(ErrorQuestion::getUserId, userId)
                        .orderByDesc(ErrorQuestion::getUpdateTime));

        if (list.isEmpty()) return new ArrayList<>();

        List<Integer> qIds = list.stream().map(ErrorQuestion::getQuestionId).collect(Collectors.toList());
        List<Question> questions = listByIds(qIds);

        // 将错题时间和详细信息合并到题目对象中
        questions.forEach(q -> {
            list.stream()
                    .filter(m -> m.getQuestionId().longValue() == q.getId())
                    .findFirst()
                    .ifPresent(m -> {
                        q.setMistakeTime(m.getUpdateTime() != null ? m.getUpdateTime() : m.getCreateTime());

                        // 填充科目信息
                        List<Integer> subjectIds = mapQuestionSubjectService.getSubjectIdsByQuestionId(q.getId());
                        q.setSubjectIds(subjectIds != null ? subjectIds : Collections.emptyList());

                        if (subjectIds != null && !subjectIds.isEmpty()) {
                            List<String> subjectNames = new ArrayList<>();
                            for (Integer subjectId : subjectIds) {
                                Subject subject = subjectService.getById(subjectId);
                                if (subject != null) {
                                    subjectNames.add(subject.getName());
                                }
                            }
                            q.setSubjectNames(subjectNames);
                            q.setSubjectName(subjectNames.isEmpty() ? null : subjectNames.get(0));
                            // 为了兼容前端，设置单个subjectId字段
                            q.setSubjectId(subjectIds.get(0));
                        } else {
                            q.setSubjectNames(Collections.emptyList());
                            q.setSubjectName(null);
                            q.setSubjectId(null);
                        }

                        // 填充习题册信息
                        List<Integer> bookIds = mapQuestionBookService.getBookIdsByQuestionId(q.getId());
                        q.setBookIds(bookIds != null ? bookIds : Collections.emptyList());

                        if (bookIds != null && !bookIds.isEmpty()) {
                            List<String> bookNames = new ArrayList<>();
                            for (Integer bookId : bookIds) {
                                ExerciseBook book = bookService.getById(bookId);
                                if (book != null) {
                                    bookNames.add(book.getName());
                                }
                            }
                            q.setBookNames(bookNames);
                            q.setBookName(bookNames.isEmpty() ? null : bookNames.get(0));
                        } else {
                            q.setBookNames(Collections.emptyList());
                            q.setBookName(null);
                        }
                    });
        });

        // 按错题时间倒序排列
        questions.sort((a, b) -> {
            if (a.getMistakeTime() == null || b.getMistakeTime() == null) return 0;
            return b.getMistakeTime().compareTo(a.getMistakeTime());
        });

        return questions;
    }

    @Override
    public List<Question> getQuestionsByKnowledgePoint(Integer subjectId) {
        List<Integer> subjectIds = subjectService.getDescendantIds(subjectId);
        return getQuestionsBySubjectIds(subjectIds);
    }

    @Override
    public List<Question> getQuestionsBySubjectOrBook(Integer subjectId, Integer bookId) {
        List<Long> questionIds = null;
        if (subjectId != null) {
            questionIds = mapQuestionSubjectService.getQuestionIdsBySubjectId(subjectId);
        } else if (bookId != null) {
            questionIds = mapQuestionBookService.getQuestionIdsByBookId(bookId);
        }

        if (questionIds == null || questionIds.isEmpty()) {
            return subjectId != null || bookId != null ? new ArrayList<>() : list();
        }

        List<Question> questions = listByIds(questionIds);
        questions.sort((a, b) -> a.getId().compareTo(b.getId()));
        return questions;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveWrongQuestion(Integer userId, Long questionId) {
        if (userId == null || questionId == null) {
            return false;
        }
        
        LambdaQueryWrapper<ErrorQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ErrorQuestion::getUserId, userId)
                .eq(ErrorQuestion::getQuestionId, questionId);

        if (mistakeRecordService.count(wrapper) == 0) {
            ErrorQuestion mistakeRecord = new ErrorQuestion();
            mistakeRecord.setUserId(userId);
            mistakeRecord.setQuestionId(Math.toIntExact(questionId));
            return mistakeRecordService.save(mistakeRecord);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importQuestions(QuestionImportDTO importDTO) {
        if (importDTO.getQuestions() == null || importDTO.getQuestions().isEmpty()) {
            throw new IllegalArgumentException("题目列表不能为空");
        }

        if (importDTO.getSubjectIds() == null || importDTO.getSubjectIds().isEmpty()) {
            throw new IllegalArgumentException("科目ID不能为空");
        }

        // 验证科目是否存在
        for (Integer subjectId : importDTO.getSubjectIds()) {
            if (subjectService.getById(subjectId) == null) {
                throw new IllegalArgumentException("科目ID: " + subjectId + " 不存在");
            }
        }

        try {
            Integer bookId = importDTO.getBookId();

            // 如果新建习题册
            if (bookId == null && importDTO.getNewBookName() != null && !importDTO.getNewBookName().trim().isEmpty()) {
                ExerciseBook newBook = new ExerciseBook();
                newBook.setName(importDTO.getNewBookName().trim());
                newBook.setDescription("通过JSON导入自动创建");
                bookService.save(newBook);
                bookId = newBook.getId();
            } else if (bookId != null) {
                // 验证书本是否存在
                if (bookService.getById(bookId) == null) {
                    throw new IllegalArgumentException("习题册不存在");
                }
            }

            // 最终使用的习题册ID
            Integer finalBookId = bookId;

            // 去重检查，默认启用
            boolean checkDuplicate = importDTO.getCheckDuplicate() != null && importDTO.getCheckDuplicate();

            // 批量保存题目
            int successCount = 0;
            int duplicateCount = 0;
            int failCount = 0;
            List<String> errorMessages = new ArrayList<>();

            for (QuestionImportDTO.QuestionImportItem item : importDTO.getQuestions()) {
                try {
                    // 去重检查
                    if (checkDuplicate && isQuestionExist(item.getContent())) {
                        duplicateCount++;
                        continue;
                    }

                    // 构造 QuestionDTO
                    QuestionDTO questionDTO = new QuestionDTO();
                    questionDTO.setType(item.getType());
                    questionDTO.setContent(item.getContent());

                    // 处理选项：支持旧格式（字符串数组）和新格式（对象数组）
                    if (item.getOptions() != null) {
                        // 如果传入的是字符串数组，转换为对象数组格式
                        if (item.getOptions() instanceof List) {
                            List<?> optionsList = (List<?>) item.getOptions();
                            if (!optionsList.isEmpty() && optionsList.get(0) instanceof String) {
                                // 旧格式：["选项1", "选项2", "选项3", "选项4"]
                                // 转换为新格式：[{"label": "A", "text": "选项1"}, ...]
                                List<Map<String, String>> formattedOptions = new ArrayList<>();
                                String[] labels = {"A", "B", "C", "D", "E", "F"};
                                for (int i = 0; i < optionsList.size() && i < labels.length; i++) {
                                    Map<String, String> option = new java.util.HashMap<>();
                                    option.put("label", labels[i]);
                                    option.put("text", (String) optionsList.get(i));
                                    formattedOptions.add(option);
                                }
                                questionDTO.setOptions(formattedOptions);
                            } else if (optionsList.get(0) instanceof Map) {
                                // 新格式：已经是对象数组，直接使用
                                questionDTO.setOptions((List<Map<String, String>>) (List<?>) optionsList);
                            }
                        }
                    }

                    questionDTO.setAnswer(item.getAnswer());
                    questionDTO.setAnalysis(item.getAnalysis());
                    questionDTO.setTags(item.getTags());
                    questionDTO.setSource(item.getSource());
                    questionDTO.setSubjectIds(importDTO.getSubjectIds());

                    // 只有在有习题册ID时才设置
                    if (finalBookId != null) {
                        questionDTO.setBookIds(Collections.singletonList(finalBookId));
                    }

                    // 保存题目
                    boolean success = saveQuestionWithRelations(questionDTO);
                    if (success) {
                        successCount++;
                    } else {
                        failCount++;
                        String content = item.getContent() != null
                            ? item.getContent().substring(0, Math.min(50, item.getContent().length()))
                            : "(无内容)";
                        errorMessages.add("题目保存失败: " + content);
                    }
                } catch (Exception e) {
                    failCount++;
                    errorMessages.add("题目导入失败: " + e.getMessage());
                }
            }

            String resultMessage = String.format("导入完成！成功: %d, 跳过重复: %d, 失败: %d", successCount, duplicateCount, failCount);
            if (!errorMessages.isEmpty()) {
                resultMessage += "\n错误信息:\n" + String.join("\n", errorMessages.subList(0, Math.min(5, errorMessages.size())));
                if (errorMessages.size() > 5) {
                    resultMessage += "\n...还有 " + (errorMessages.size() - 5) + " 条错误";
                }
            }

            return resultMessage;
        } catch (Exception e) {
            throw new RuntimeException("导入失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Question> previewExportQuestions(List<Question> questions) {
        // 加载题目详情（科目、书本等）
        for (Question question : questions) {
            List<Integer> bookIds = mapQuestionBookService.getBookIdsByQuestionId(question.getId());
            question.setBookIds(bookIds != null ? bookIds : Collections.emptyList());

            if (bookIds != null && !bookIds.isEmpty()) {
                List<String> bookNames = new ArrayList<>();
                for (Integer bookIdTemp : bookIds) {
                    ExerciseBook book = bookService.getById(bookIdTemp);
                    if (book != null) {
                        bookNames.add(book.getName());
                    }
                }
                question.setBookNames(bookNames);
            }

            List<Integer> subjectIds = mapQuestionSubjectService.getSubjectIdsByQuestionId(question.getId());
            question.setSubjectIds(subjectIds != null ? subjectIds : Collections.emptyList());

            if (subjectIds != null && !subjectIds.isEmpty()) {
                List<String> subjectNames = new ArrayList<>();
                for (Integer subjectIdTemp : subjectIds) {
                    Subject subject = subjectService.getById(subjectIdTemp);
                    if (subject != null) {
                        subjectNames.add(subject.getName());
                    }
                }
                question.setSubjectNames(subjectNames);
            }
        }
        return questions;
    }

    @Override
    public QuestionDTO recognizeQuestion(MultipartFile file) {
        try {
            return pythonBackendClient.recognizeQuestion(file);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new RuntimeException("AI识别失败: " + e.getMessage(), e);
        }
    }
}