package org.example.kaoyanplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_book")
public class Book {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String subjectName;
    private String description;
    private LocalDateTime createTime;
}
