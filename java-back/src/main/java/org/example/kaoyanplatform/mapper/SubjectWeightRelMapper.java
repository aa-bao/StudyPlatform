package org.example.kaoyanplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.kaoyanplatform.entity.SubjectWeightRel;

import java.util.List;

/**
 * 科目权重映射表 Mapper
 */
@Mapper
public interface SubjectWeightRelMapper extends BaseMapper<SubjectWeightRel> {

    /**
     * 根据考试规格ID查询该考试规格下所有科目的权重映射
     *
     * @param examSpecId 考试规格ID（如：英语一=2，英语二=3）
     * @return 权重映射列表
     */
    @Select("SELECT * FROM subject_weight_rel WHERE exam_spec_id = #{examSpecId}")
    List<SubjectWeightRel> listByExamSpecId(@Param("examSpecId") Integer examSpecId);

    /**
     * 根据科目ID和考试规格ID查询权重
     *
     * @param subjectId   科目ID
     * @param examSpecId  考试规格ID
     * @return 权重映射记录
     */
    @Select("SELECT * FROM subject_weight_rel WHERE subject_id = #{subjectId} AND exam_spec_id = #{examSpecId}")
    SubjectWeightRel getBySubjectAndExamSpec(@Param("subjectId") Integer subjectId,
                                              @Param("examSpecId") Integer examSpecId);

    /**
     * 根据科目ID查询该科目在所有考试规格下的权重
     *
     * @param subjectId 科目ID
     * @return 权重映射列表
     */
    @Select("SELECT * FROM subject_weight_rel WHERE subject_id = #{subjectId}")
    List<SubjectWeightRel> listBySubjectId(@Param("subjectId") Integer subjectId);
}
