package org.example.kaoyanplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 科目权重映射表实体类
 * 用于存储不同考试规格下各科目的权重占比
 *
 * 解决问题：
 * 英语一和英语二的二级分类（如大作文）名称相同，但在真题中的分值占比不同
 * 通过此映射表可以为同一科目在不同考试规格下设置不同的权重值
 *
 * @author KaoYanPlatform
 * @since 2026-01-12
 */
@Data
@TableName("subject_weight_rel")
public class SubjectWeightRel {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 科目ID（关联subject.id）
     * 例如：大作文的ID为18
     */
    private Integer subjectId;

    /**
     * 考试规格ID（关联subject.id，level=1的记录）
     * 例如：英语一ID=2，英语二ID=3
     */
    private Integer examSpecId;

    /**
     * 该科目在该考试规格下的权重（百分比）
     * 例如：英语一的大作文为20%，英语二的大作文为15%
     */
    private Float weight;
}
