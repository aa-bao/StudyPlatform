package org.example.kaoyanplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.kaoyanplatform.entity.ExamPaper;

public interface ExamPaperService extends IService<ExamPaper> {
    
    /**
     * 删除试卷及其所有关联关系
     * @param id 试卷ID
     * @return 删除结果
     */
    boolean deletePaperWithMappings(String id);
}