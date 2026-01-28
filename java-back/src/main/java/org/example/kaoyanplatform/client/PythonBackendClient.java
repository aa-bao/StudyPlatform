package org.example.kaoyanplatform.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.kaoyanplatform.entity.dto.QuestionDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Python 后端客户端
 * 用于调用 Python 服务的 AI 和数据处理接口
 */
@Slf4j
@Component
public class PythonBackendClient {

    @Value("${python.backend.url:http://localhost:8082}")
    private String pythonBackendUrl;

    @Value("${python.backend.timeout:30000}")
    private int timeout;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public PythonBackendClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 图片识别题目
     * 调用 Python 服务的 /ai/recognize 接口
     *
     * @param file 图片文件
     * @return 识别后的题目数据
     */
    public QuestionDTO recognizeQuestion(MultipartFile file) {
        try {
            log.info("调用 Python 服务进行图片识别，文件名: {}", file.getOriginalFilename());

            // 创建临时文件
            File tempFile = File.createTempFile("upload_", "_" + file.getOriginalFilename());
            file.transferTo(tempFile);

            try {
                // 构造 multipart 请求
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                Resource fileResource = new FileSystemResource(tempFile) {
                    @Override
                    public String getFilename() {
                        return file.getOriginalFilename();
                    }
                };
                body.add("file", fileResource);

                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

                // 发送请求
                ResponseEntity<String> response = restTemplate.exchange(
                        pythonBackendUrl + "/ai/recognize",
                        HttpMethod.POST,
                        requestEntity,
                        String.class
                );

                // 解析响应
                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    Map<String, Object> result = objectMapper.readValue(
                            response.getBody(),
                            new TypeReference<Map<String, Object>>() {}
                    );

                    Integer code = (Integer) result.get("code");
                    if (code != null && code == 200) {
                        Map<String, Object> data = (Map<String, Object>) result.get("data");
                        return convertToQuestionDTO(data);
                    } else {
                        String message = (String) result.get("message");
                        throw new RuntimeException("Python 服务返回错误: " + message);
                    }
                } else {
                    throw new RuntimeException("Python 服务响应异常: " + response.getStatusCode());
                }

            } finally {
                // 删除临时文件
                if (tempFile.exists()) {
                    tempFile.delete();
                }
            }

        } catch (IOException e) {
            log.error("图片识别失败", e);
            throw new RuntimeException("图片识别失败: " + e.getMessage(), e);
        }
    }

    /**
     * 智能批改
     * 调用 Python 服务的 /ai/grade 接口
     *
     * @param questionContent 题目内容
     * @param userAnswer 用户答案
     * @param referenceAnswer 参考答案
     * @param questionType 题目类型
     * @return 批改结果
     */
    public Map<String, Object> gradeAnswer(
            String questionContent,
            String userAnswer,
            String referenceAnswer,
            Integer questionType) {

        try {
            log.info("调用 Python 服务进行智能批改，题型: {}", questionType);

            // 构造请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("question_content", questionContent);
            requestBody.put("user_answer", userAnswer);
            requestBody.put("reference_answer", referenceAnswer);
            requestBody.put("question_type", questionType != null ? questionType : 4);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                    pythonBackendUrl + "/ai/grade",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> result = objectMapper.readValue(
                        response.getBody(),
                        new TypeReference<Map<String, Object>>() {}
                );

                Integer code = (Integer) result.get("code");
                if (code != null && code == 200) {
                    return (Map<String, Object>) result.get("data");
                } else {
                    String message = (String) result.get("message");
                    throw new RuntimeException("Python 服务返回错误: " + message);
                }
            } else {
                throw new RuntimeException("Python 服务响应异常: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("智能批改失败", e);
            throw new RuntimeException("智能批改失败: " + e.getMessage(), e);
        }
    }

    /**
     * 健康检查
     *
     * @return Python 服务的健康状态
     */
    public boolean healthCheck() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    pythonBackendUrl + "/health",
                    String.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.warn("Python 服务健康检查失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 将 Map 转换为 QuestionDTO
     */
    private QuestionDTO convertToQuestionDTO(Map<String, Object> data) {
        QuestionDTO dto = new QuestionDTO();

        if (data.get("type") instanceof Number) {
            dto.setType(((Number) data.get("type")).intValue());
        }

        dto.setContent((String) data.get("content"));

        // 处理选项
        if (data.get("options") instanceof List) {
            List<Map<String, String>> options = (List<Map<String, String>>) data.get("options");
            dto.setOptions(options);
        }

        dto.setAnswer((String) data.get("answer"));
        dto.setAnalysis((String) data.get("analysis"));

        if (data.get("tags") instanceof List) {
            dto.setTags((List<String>) data.get("tags"));
        }

        if (data.get("difficulty") instanceof Number) {
            dto.setDifficulty(((Number) data.get("difficulty")).intValue());
        }

        return dto;
    }
}
