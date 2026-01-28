package org.example.kaoyanplatform.entity.dto;

import lombok.Data;
import java.util.List;

/**
 * 科目数据传输对象
 */
@Data
public class SubjectDTO {
    private Integer id;
    private String name;
    private Integer parentId;
    private String icon;
    private Integer sort;
    private String level;
    private String scope;
    private String questionTypes;

    /**
     * 动态权重
     */
    private Float dynamicWeight;

    /**
     * 树形结构字段
     */
    private List<SubjectDTO> children;

    /**
     * 题目总数
     */
    private Integer questionCount;

    /**
     * 已完成题目数
     */
    private Integer finishedCount;

    /**
     * 树形ID，格式为 "父级ID-自身ID"
     */
    private String treeId;

    /**
     * 掌握度
     */
    private Integer mastery;

    /**
     * 解题通法
     */
    private List<String> solutionPatterns;

    /**
     * 高频误区
     */
    private List<String> commonMistakes;

    /**
     * 考察热度
     */
    private Integer examFrequency;

    /**
     * 展开状态
     */
    private Boolean expanded;
}