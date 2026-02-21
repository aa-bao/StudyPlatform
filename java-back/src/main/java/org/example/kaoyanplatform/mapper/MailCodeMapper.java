package org.example.kaoyanplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.kaoyanplatform.entity.MailCode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邮箱验证码Mapper接口
 */
@Mapper
public interface MailCodeMapper extends BaseMapper<MailCode> {
}
