package org.example.kaoyanplatform.service;

import org.example.kaoyanplatform.entity.dto.QuestionExportDTO;

import java.io.FileNotFoundException;

/**
 * PDF导出服务接口
 */
public interface PdfExportService {

    // 导出题目为PDF
    byte[] exportQuestionsToPdf(QuestionExportDTO exportDTO) throws FileNotFoundException;
}
