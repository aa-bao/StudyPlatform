package org.example.kaoyanplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

/**
 * 科目实体类
 */
@Data
@TableName("subject")
public class Subject {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;        // 科目名称
    private Integer parentId;   // 父节点 ID
    @TableField("video_url")
    private String videoUrl;     // 视频链接（原 icon 字段，现用于存储视频链接）
    private Integer sort;       // 排序号
    /**
     * 层级
     * - 1: 考试大类（CATEGORY）- 如：政治、英语一、数学一、408
     * - 2: 考试规格（EXAM_SPEC）- 如：马原、毛中特、完形填空、高等数学
     * - 3: 具体学科（SUBJECT）- 如：函数与极限、行列式
     * - 4: 知识点/章节（KNOWLEDGE_POINT）- 如：数列敛散性判定
     * - 5: 题型/解题方法（QUESTION_TYPE）- 如：泰勒公式
     */
    private String level;           // 层级
    private String scope;           // 范围
    private String questionTypes;   // 题型
    private Integer questionCount;  // 题目数量

}
