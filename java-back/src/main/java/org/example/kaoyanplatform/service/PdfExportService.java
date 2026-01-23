package org.example.kaoyanplatform.service;

import org.example.kaoyanplatform.entity.dto.QuestionExportDTO;

import java.io.FileNotFoundException;

/**
 * PDF导出服务接口
 */
public interface PdfExportService {

    /**
     * 导出题目为PDF
     * @param exportDTO 导出配置
     * @return PDF文件的字节数组
     * @throws FileNotFoundException 文件未找到异常
     */
    byte[] exportQuestionsToPdf(QuestionExportDTO exportDTO) throws FileNotFoundException;
}
