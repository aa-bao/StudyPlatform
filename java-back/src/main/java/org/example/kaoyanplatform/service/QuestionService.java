package org.example.kaoyanplatform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.kaoyanplatform.entity.ExerciseBook;
import org.example.kaoyanplatform.entity.Question;
import org.example.kaoyanplatform.entity.Subject;
import org.example.kaoyanplatform.entity.dto.QuestionDTO;
import org.example.kaoyanplatform.entity.dto.QuestionExportDTO;

import java.util.List;

public interface QuestionService extends IService<Question> {

    /**
     * 根据科目ID列表查询题目
     * @param subjectIds 科目ID列表
     * @return 题目列表
     */
    List<Question> getQuestionsBySubjectIds(List<Integer> subjectIds);

    /**
     * 新增题目（包含关联关系）
     * @param questionDTO 题目数据传输对象
     * @return 是否成功
     */
    boolean saveQuestionWithRelations(QuestionDTO questionDTO);

    /**
     * 更新题目（包含关联关系）
     * @param questionDTO 题目数据传输对象
     * @return 是否成功
     */
    boolean updateQuestionWithRelations(QuestionDTO questionDTO);

    /**
     * 根据书本ID查询题目列表
     * @param bookId 书本ID
     * @return 题目列表
     */
    List<Question> getQuestionsByBookId(Integer bookId);

    /**
     * 分页查询题目（支持关联查询）
     * @param page 分页对象
     * @param subjectIds 科目ID列表（可选）
     * @param bookId 书本ID（可选）
     * @return 分页结果
     */
    Page<Question> questionPage(Page<Question> page, List<Integer> subjectIds, Integer bookId);

    /**
     * 检查题目是否已存在（基于内容MD5去重）
     * @param content 题干内容
     * @return 是否存在
     */
    boolean isQuestionExist(String content);

    /**
     * 根据内容查找相似题目（模糊匹配）
     * @param content 题干内容
     * @return 相似题目列表
     */
    List<Question> findSimilarQuestions(String content);

    /**
     * 删除题目及其所有关联关系
     * @param id 题目ID
     * @return 删除结果
     */
    boolean deleteQuestionWithRelations(Long id);

    /**
     * 获取带有详细关联信息的题目详情
     * @param id 题目ID
     * @return 填充了关联信息的题目对象
     */
    Question getQuestionWithDetails(Long id);

    /**
     * 根据导出配置获取题目列表
     * @param exportDTO 导出配置对象
     * @return 题目列表
     */
    List<Question> getQuestionsByExportConfig(QuestionExportDTO exportDTO);

    /**
     * 获取带有错题时间的错题列表
     * @param userId 用户ID
     * @return 带有错题时间的题目列表
     */
    List<Question> getErrorQuestionsWithTime(Integer userId);

    /**
     * 按知识点获取题目（包括所有子知识点）
     * @param subjectId 科目ID
     * @return 题目列表
     */
    List<Question> getQuestionsByKnowledgePoint(Integer subjectId);

    /**
     * 按科目或书本获取题目
     * @param subjectId 科目ID（可选）
     * @param bookId 书本ID（可选）
     * @return 题目列表
     */
    List<Question> getQuestionsBySubjectOrBook(Integer subjectId, Integer bookId);

    /**
     * 保存错题记录
     * @param userId 用户ID
     * @param questionId 题目ID
     * @return 是否保存成功
     */
    boolean saveWrongQuestion(Integer userId, Long questionId);

    /**
     * 批量导入题目
     * @param importDTO 导入数据传输对象
     * @return 导入结果信息
     */
    String importQuestions(org.example.kaoyanplatform.entity.dto.QuestionImportDTO importDTO);

    /**
     * 预览导出题目（填充详细信息）
     * @param questions 题目列表
     * @return 填充了详细信息的题目列表
     */
    List<Question> previewExportQuestions(List<Question> questions);

    /**
     * AI图片识别题目
     * @param file 图片文件
     * @return 识别出的题目DTO
     */
    QuestionDTO recognizeQuestion(org.springframework.web.multipart.MultipartFile file);
}