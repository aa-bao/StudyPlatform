package org.example.kaoyanplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.kaoyanplatform.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 这里什么都不用写，MyBatis-Plus 已经帮你写好了增删改查
}