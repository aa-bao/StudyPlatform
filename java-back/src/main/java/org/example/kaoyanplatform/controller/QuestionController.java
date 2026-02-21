package org.example.kaoyanplatform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.kaoyanplatform.client.PythonBackendClient;
import org.example.kaoyanplatform.common.Result;
import org.example.kaoyanplatform.entity.Question;
import org.example.kaoyanplatform.entity.dto.QuestionDTO;
import org.example.kaoyanplatform.entity.dto.QuestionImportDTO;
import org.example.kaoyanplatform.entity.dto.QuestionExportDTO;
import org.example.kaoyanplatform.mapper.QuestionMapper;
import org.example.kaoyanplatform.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "题目管理", description = "题目增删改查接口")
@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SubjectService subjectService;


    @Autowired
    private QuestionSubjectRelService mapQuestionSubjectService;

    @Autowired
    private QuestionBookRelService mapQuestionBookService;

    @Autowired
    private PdfExportService pdfExportService;


    @Autowired
    private PythonBackendClient pythonBackendClient;

    // 1. 按知识点获取题目（递归下级）
    @GetMapping("/list-by-knowledge-point")
    @Operation(summary = "按知识点获取题目", description = "根据科目ID及其所有子科目递归查询题目。")
    public Result getQuestionsByKnowledgePoint(@RequestParam Integer subjectId) {
        List<Integer> subjectIds = subjectService.getDescendantIds(subjectId);
        List<Question> questions = questionService.getQuestionsBySubjectIds(subjectIds);
        return Result.success(questions);
    }

    // 2. 按科目或书本获取题目
    @GetMapping("/list-by-subject")
    @Operation(summary = "按科目或书本获取题目", description = "根据科目ID或书本ID获取题目。")
    public Result getQuestionsBySubject(
            @RequestParam(required = false) Integer subjectId,
            @RequestParam(required = false) Integer bookId) {

        List<Long> questionIds = null;
        if (subjectId != null) {
            questionIds = mapQuestionSubjectService.getQuestionIdsBySubjectId(subjectId);
        } else if (bookId != null) {
            questionIds = mapQuestionBookService.getQuestionIdsByBookId(bookId);
        }

        if (questionIds == null || questionIds.isEmpty()) {
            return (subjectId != null || bookId != null) ? Result.success(new ArrayList<>()) : Result.success(questionMapper.selectList(null));
        }

        List<Question> questions = questionService.listByIds(questionIds);
        questions.sort((a, b) -> a.getId().compareTo(b.getId()));
        return Result.success(questions);
    }

    // 3. 获取所有题目
    @GetMapping("/all")
    @Operation(summary = "获取所有题目")
    public Result getAllQuestions() {
        return Result.success(questionMapper.selectList(null));
    }

    // 4. 新增题目
    @PostMapping("/add")
    @Operation(summary = "新增题目", description = "新增题目并建立与科目、书本的关联。")
    public Result addQuestion(@RequestBody QuestionDTO questionDTO) {
        boolean success = questionService.saveQuestionWithRelations(questionDTO);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    // 5. 更新题目
    @PostMapping("/update")
    @Operation(summary = "更新题目")
    public Result updateQuestion(@RequestBody QuestionDTO questionDTO) {
        if (questionDTO.getId() == null) return Result.error("题目ID不能为空");
        boolean success = questionService.updateQuestionWithRelations(questionDTO);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    // 6. 删除题目
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除题目", description = "级联删除题目及其科目、书本关联关系。")
    public Result deleteQuestion(@PathVariable Long id) {
        boolean success = questionService.deleteQuestionWithRelations(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    // 7. 获取题目详情
    @GetMapping("/{id}")
    @Operation(summary = "获取题目详情", description = "包含题目基本信息及关联的所有书本和科目ID列表。")
    public Result getQuestionById(@PathVariable Long id) {
        Question question = questionService.getQuestionWithDetails(id);
        if (question == null) return Result.error("题目不存在");
        return Result.success(question);
    }

    // 8. 获取错题本
    @GetMapping("/getErrorBook")
    @Operation(summary = "获取错题本")
    public Result getErrorBook(@RequestParam Integer userId) {
        List<Question> questions = questionService.getErrorQuestionsWithTime(userId);
        return Result.success(questions);
    }

    // 9. 分页查询
    @GetMapping("/page")
    @Operation(summary = "分页查询题目")
    public Result findPage(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String subjectIds,
            @RequestParam(required = false) Integer bookId) {
        Page<Question> page = new Page<>(pageNum, pageSize);
        List<Integer> subjectIdList = null;
        if (subjectIds != null && !subjectIds.isEmpty()) {
            subjectIdList = Arrays.stream(subjectIds.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
        return Result.success(questionService.questionPage(page, subjectIdList, bookId));
    }

    // 10. 手动保存错题
    @PostMapping("/saveWrong")
    @Operation(summary = "保存错题")
    public Result saveWrong(@RequestParam Integer userId, @RequestParam Long questionId) {
        if (userId == null || questionId == null) {
            return Result.error("参数不完整");
        }
        
        boolean success = questionService.saveWrongQuestion(userId, questionId);
        return success ? Result.success("错题已记录") : Result.error("保存失败");
    }

    // 11. JSON批量导入题目
    @PostMapping("/import")
    @Operation(summary = "JSON批量导入题目", description = "接收JSON格式的题目数据，批量导入题库")
    public Result importQuestions(@RequestBody QuestionImportDTO importDTO) {
        try {
            String resultMessage = questionService.importQuestions(importDTO);
            return Result.success(resultMessage);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 12. 预览要导出的题目
    @PostMapping("/export/preview")
    @Operation(summary = "预览导出题目", description = "根据导出条件预览将要导出的题目列表")
    public Result previewExportQuestions(@RequestBody QuestionExportDTO exportDTO) {
        try {
            List<Question> questions = questionService.getQuestionsByExportConfig(exportDTO);
            List<Question> detailedQuestions = questionService.previewExportQuestions(questions);
            return Result.success(detailedQuestions);
        } catch (Exception e) {
            return Result.error("预览失败: " + e.getMessage());
        }
    }

    // 13. 导出题目为PDF
    @PostMapping("/export/pdf")
    @Operation(summary = "导出题目为PDF", description = "根据导出条件生成PDF文件")
    public ResponseEntity<byte[]> exportQuestionsToPdf(@RequestBody QuestionExportDTO exportDTO) {
        try {
            byte[] pdfBytes = pdfExportService.exportQuestionsToPdf(exportDTO);

            String filename = "题目导出_" + System.currentTimeMillis() + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            String errorMsg = "未找到符合条件的题目";
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(errorMsg.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = "PDF生成失败: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMsg.getBytes(StandardCharsets.UTF_8));
        }
    }

    // 14. AI 图片识别题目
    @PostMapping("/recognize")
    @Operation(summary = "AI图片识别题目", description = "使用智谱GLM-4.6V-Flash API识别图片中的题目内容")
    public Result recognizeQuestion(@RequestParam("file") MultipartFile file) {
        try {
            QuestionDTO questionDTO = questionService.recognizeQuestion(file);
            return Result.success(questionDTO);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (RuntimeException e) {
            return Result.error("AI识别失败: " + e.getMessage());
        }
    }

}