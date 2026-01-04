package org.example.kaoyanplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_subject")
public class Subject {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer parentId;
    private String icon;
}
