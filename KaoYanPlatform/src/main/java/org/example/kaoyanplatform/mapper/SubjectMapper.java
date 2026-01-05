package org.example.kaoyanplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.kaoyanplatform.entity.Subject;

import java.util.List;

@Mapper
public interface SubjectMapper extends BaseMapper<Subject> {
    @Select("SELECT subject_id FROM map_subject_book WHERE book_id = #{bookId}")
    List<Integer> getSubjectIdsByBookId(Integer bookId);
}
