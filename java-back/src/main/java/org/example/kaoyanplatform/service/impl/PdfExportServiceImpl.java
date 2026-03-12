package org.example.kaoyanplatform.service.impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.kaoyanplatform.entity.ExerciseBook;
import org.example.kaoyanplatform.entity.ExamPaper;
import org.example.kaoyanplatform.entity.Question;
import org.example.kaoyanplatform.entity.dto.QuestionExportDTO;
import org.example.kaoyanplatform.service.*;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Base64;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import org.scilab.forge.jlatexmath.TeXConstants;

/**
 * PDF导出服务实现类
 */
@Service
@RequiredArgsConstructor
public class PdfExportServiceImpl implements PdfExportService {

    private final QuestionService questionService;
    private final ExerciseBookService bookService;
    private final ExamPaperService paperService;
    private final QuestionBookRelService mapQuestionBookService;
    private final QuestionPaperRelService mapPaperQuestionService;
    private final SubjectService subjectService;
    private final TemplateEngine templateEngine;

    @Override
    public byte[] exportQuestionsToPdf(QuestionExportDTO exportDTO) throws FileNotFoundException {
        // 1. 根据导出条件获取题目列表
        List<Question> questions = getQuestionsByExportConfig(exportDTO);

        if (questions.isEmpty()) {
            throw new FileNotFoundException("没有找到符合条件的题目");
        }

        // 2. 处理题目内容：将LaTeX公式转换为HTML，同时处理特殊字符
        processQuestionContent(questions);

        // 3. 获取导出元数据
        String title = "考研题目";
        String subtitle = null;
        String bookName = null;
        String subjectName = null;
        boolean isPaperExport = exportDTO.getPaperId() != null;

        if (exportDTO.getBookId() != null) {
            ExerciseBook book = bookService.getById(exportDTO.getBookId());
            if (book != null) {
                bookName = book.getName();
                title = bookName + " - 题目导出";
            }
        } else if (isPaperExport) {
            ExamPaper paper = paperService.getById(exportDTO.getPaperId());
            if (paper != null) {
                subtitle = paper.getTitle();
                title = "试卷题目导出";
            }
        }

        // 4. 加载题目详情（科目、标签等）
        loadQuestionDetails(questions);

        // 5. 根据导出类型选择模板
        Context context = new Context();
        context.setVariable("title", title);
        context.setVariable("subtitle", subtitle);
        context.setVariable("bookName", bookName);
        context.setVariable("subjectName", subjectName);
        context.setVariable("exportTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        context.setVariable("totalQuestions", questions.size());
        context.setVariable("mode", exportDTO.getMode() != null ? exportDTO.getMode() : 1);
        context.setVariable("includeDifficulty", exportDTO.getIncludeDifficulty() != null && exportDTO.getIncludeDifficulty());
        context.setVariable("includeTags", exportDTO.getIncludeTags() != null && exportDTO.getIncludeTags());
        context.setVariable("includeSource", exportDTO.getIncludeSource() != null && exportDTO.getIncludeSource());
        context.setVariable("questions", questions);
        context.setVariable("currentPage", 1);
        context.setVariable("totalPages", 1);

        String templateName;
        if (isPaperExport) {
            // 试卷模板：按试卷结构分组
            ExamPaper paper = paperService.getById(exportDTO.getPaperId());
            List<PaperSectionData> sections = groupQuestionsBySection(questions, paper);
            context.setVariable("sections", sections);
            context.setVariable("allQuestions", questions);
            templateName = "pdf/paper_template";
        } else {
            // 题目模板：直接列出题目
            templateName = "pdf/question_template";
        }

        // 6. 渲染HTML模板
        String htmlContent = templateEngine.process(templateName, context);

        // 7. 转换为PDF
        byte[] pdfBytes = convertHtmlToPdf(htmlContent);

        return pdfBytes;
    }

    /**
     * 处理题目内容：转换LaTeX公式为HTML，同时转义HTML特殊字符
     */
    private void processQuestionContent(List<Question> questions) {
        for (Question question : questions) {
            if (question.getContent() != null) {
                question.setContent(convertLatexToHtml(question.getContent()));
            }
            if (question.getAnswer() != null) {
                question.setAnswer(convertLatexToHtml(question.getAnswer()));
            }
            if (question.getAnalysis() != null) {
                question.setAnalysis(convertLatexToHtml(question.getAnalysis()));
            }
            if (question.getOptions() != null) {
                // 处理新的选项格式：List<Map<String, String>>
                String[] labels = {"A", "B", "C", "D", "E", "F"};
                List<Map<String, String>> processedOptions = new ArrayList<>();
                for (int i = 0; i < question.getOptions().size(); i++) {
                    Map<String, String> opt = question.getOptions().get(i);
                    Map<String, String> newOpt = new java.util.HashMap<>();
                    // 确保选项有正确的标签（A、B、C、D等）
                    String label = opt.get("label");
                    if (label == null || label.isEmpty()) {
                        label = i < labels.length ? labels[i] : String.valueOf((char)('A' + i));
                    }
                    newOpt.put("label", label);
                    newOpt.put("text", convertLatexToHtml(opt.get("text")));
                    processedOptions.add(newOpt);
                }
                question.setOptions(processedOptions);
            }
        }
    }

    /**
     * 将文本转换为HTML，同时处理特殊字符
     * 使用JLaTeXMath将LaTeX公式转换为图片
     */
    private String convertLatexToHtml(String text) {
        if (text == null) return null;

        // 首先转义HTML特殊字符
        String escapedText = text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;");

        // 使用JLaTeXMath渲染LaTeX公式
        try {
            // 提取行内公式 $...$
            Pattern inlinePattern = Pattern.compile("\\$(.*?)\\$");
            Matcher inlineMatcher = inlinePattern.matcher(escapedText);
            StringBuffer sb = new StringBuffer();
            int formulaCount = 0;

            while (inlineMatcher.find()) {
                String formula = inlineMatcher.group(1);
                String imgTag = renderLatexFormula(formula, formulaCount++);
                inlineMatcher.appendReplacement(sb, Matcher.quoteReplacement(imgTag));
            }
            inlineMatcher.appendTail(sb);

            // 提取块级公式 $$...$$
            Pattern blockPattern = Pattern.compile("\\$\\$(.*?)\\$\\$", Pattern.DOTALL);
            Matcher blockMatcher = blockPattern.matcher(sb.toString());
            sb = new StringBuffer();

            while (blockMatcher.find()) {
                String formula = blockMatcher.group(1);
                String imgTag = renderLatexFormula(formula, formulaCount++);
                blockMatcher.appendReplacement(sb, Matcher.quoteReplacement(imgTag));
            }
            blockMatcher.appendTail(sb);

            return sb.toString();
        } catch (Exception e) {
            System.err.println("LaTeX formula rendering failed: " + e.getMessage());
            // 如果渲染失败，返回原始文本
            return escapedText;
        }
    }

    /**
     * 使用JLaTeXMath渲染LaTeX公式为图片
     */
    private String renderLatexFormula(String formula, int formulaCount) {
        try {
            // 渲染公式为图片
            TeXFormula teXFormula = new TeXFormula(formula);
            TeXIcon teXIcon = teXFormula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 16);

            // 设置前景色
            teXIcon.setForeground(Color.BLACK);

            // 创建图片
            BufferedImage bufferedImage = new BufferedImage(
                teXIcon.getIconWidth(),
                teXIcon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = bufferedImage.createGraphics();
            graphics2D.setColor(Color.WHITE);
            graphics2D.fillRect(0, 0, teXIcon.getIconWidth(), teXIcon.getIconHeight());
            teXIcon.paintIcon(null, graphics2D, 0, 0);
            graphics2D.dispose();

            // 转换为Base64字符串
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // 创建HTML img标签
            return String.format("<img src=\"data:image/png;base64,%s\" alt=\"%s\" style=\"vertical-align: middle;\" />",
                base64Image, formula);
        } catch (Exception e) {
            System.err.println("Error rendering LaTeX formula '" + formula + "': " + e.getMessage());
            return "$" + formula + "$";
        }
    }

    /**
     * 将题目按试卷结构分组
     */
    private List<PaperSectionData> groupQuestionsBySection(List<Question> questions, ExamPaper paper) {
        List<PaperSectionData> sections = new ArrayList<>();

        if (paper != null && paper.getStructureJson() != null && !paper.getStructureJson().isEmpty()) {
            for (ExamPaper.ExamPaperSection sectionDef : paper.getStructureJson()) {
                PaperSectionData section = new PaperSectionData();
                section.setName(sectionDef.getName() != null ? sectionDef.getName() : "第" + (sections.size() + 1) + "部分");

                List<Question> sectionQuestions = new ArrayList<>();
                for (Question q : questions) {
                    if (q.getSortOrder() != null &&
                        q.getSortOrder() >= sectionDef.getStart() &&
                        q.getSortOrder() <= sectionDef.getEnd()) {
                        sectionQuestions.add(q);
                    }
                }
                sectionQuestions.sort(Comparator.comparing(Question::getSortOrder));
                section.setQuestions(sectionQuestions);
                sections.add(section);
            }
        } else {
            PaperSectionData defaultSection = new PaperSectionData();
            defaultSection.setName("第一部分");
            List<Question> sortedQuestions = new ArrayList<>(questions);
            sortedQuestions.sort(Comparator.comparing(Question::getSortOrder));
            defaultSection.setQuestions(sortedQuestions);
            sections.add(defaultSection);
        }

        return sections;
    }

    @Data
    public static class PaperSectionData {
        private String name;
        private List<Question> questions;
    }

    /**
     * 根据导出配置获取题目列表
     */
    private List<Question> getQuestionsByExportConfig(QuestionExportDTO exportDTO) {
        List<Question> questions = new ArrayList<>();

        if (exportDTO.getQuestionIds() != null && !exportDTO.getQuestionIds().isEmpty()) {
            questions.addAll(questionService.listByIds(exportDTO.getQuestionIds()));
        } else if (exportDTO.getPaperId() != null) {
            List<Question> paperQuestions = mapPaperQuestionService.getQuestionsWithDetails(exportDTO.getPaperId());
            questions.addAll(paperQuestions);
        } else if (exportDTO.getBookId() != null) {
            List<Question> bookQuestions = questionService.getQuestionsByBookId(exportDTO.getBookId());
            questions.addAll(bookQuestions);
        } else if (exportDTO.getSubjectId() != null) {
            List<Question> subjectQuestions = questionService.getQuestionsBySubjectIds(Collections.singletonList(exportDTO.getSubjectId()));
            questions.addAll(subjectQuestions);
        }

        return questions;
    }

    /**
     * 加载题目详情
     */
    private void loadQuestionDetails(List<Question> questions) {
        for (Question question : questions) {
            if (question.getSubjectIds() == null || question.getSubjectIds().isEmpty()) {
                question.setSubjectNames(new ArrayList<>());
            }
        }
    }

    /**
     * 将HTML转换为PDF
     */
    private byte[] convertHtmlToPdf(String htmlContent) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();

            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                renderer.getFontResolver().addFont(
                    "C:\\Windows\\Fonts\\simsun.ttc",
                    "Identity-H",
                    false
                );
            } else if (os.contains("mac")) {
                renderer.getFontResolver().addFont(
                    "/System/Library/Fonts/PingFang.ttc",
                    "Identity-H",
                    false
                );
            } else {
                File fontFile = findSystemFont();
                if (fontFile != null && fontFile.exists()) {
                    renderer.getFontResolver().addFont(
                        fontFile.getAbsolutePath(),
                        "Identity-H",
                        false
                    );
                }
            }

            // 调试输出：检查HTML内容的前300行
            if (htmlContent.length() > 0) {
                String[] lines = htmlContent.split("\n");
                System.out.println("HTML Content for PDF (first " + Math.min(300, lines.length) + " lines):");
                for (int i = 0; i < Math.min(300, lines.length); i++) {
                    System.out.println(String.format("%03d: %s", i+1, lines[i]));
                    // 如果是第288行，特别输出
                    if (i == 287) {
                        System.out.println("Line 288 detail:");
                        if (lines[i].length() >= 75) {
                            System.out.println("Char 75: '" + lines[i].charAt(74) + "'");
                            System.out.println("Context around char 75: " + lines[i].substring(Math.max(0, 74 - 10), Math.min(lines[i].length(), 74 + 10)));
                        }
                    }
                }
            }

            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            System.err.println("PDF generation error details:");
            System.err.println("HTML content length: " + (htmlContent != null ? htmlContent.length() : 0));
            if (htmlContent != null && htmlContent.length() > 0) {
                String[] lines = htmlContent.split("\n");
                System.err.println("HTML lines: " + lines.length);
                if (lines.length >= 288) {
                    System.err.println("Line 288: " + lines[287]);
                    if (lines[287].length() >= 75) {
                        System.err.println("Line 288 char 75: " + lines[287].charAt(74));
                    }
                }
            }
            throw new RuntimeException("PDF生成失败: " + e.getMessage(), e);
        }
    }

    /**
     * 查找系统中的中文字体文件
     */
    private File findSystemFont() {
        String[] fontPaths = {
            "/usr/share/fonts/truetype/droid/DroidSansFallbackFull.ttf",
            "/usr/share/fonts/truetype/wqy/wqy-microhei.ttc",
            "/usr/share/fonts/truetype/wqy/wqy-zenhei.ttc",
            "/usr/share/fonts/opentype/noto/NotoSansCJK-Regular.ttc",
            "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"
        };

        for (String path : fontPaths) {
            File file = new File(path);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }
}
