package org.example.kaoyanplatform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 邮箱验证码实体类
 */
@Data
@TableName("mail_code")
public class MailCode {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String email;

    private String code;

    private String bizType;

    private LocalDateTime sendTime;

    private LocalDateTime expireTime;

    private Integer sendCount;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
