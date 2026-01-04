package org.example.kaoyanplatform.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.kaoyanplatform.entity.User;
import org.example.kaoyanplatform.mapper.UserMapper;
import org.example.kaoyanplatform.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 实现登录逻辑
     */
    @Override
    public User login(String username, String password) {
        // 1. 根据用户名查询用户
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));

        // 2. 校验密码 (使用 BCrypt 校验)
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            // 为了安全，返回前隐藏密码
            user.setPassword(null);
            return user;
        }
        return null;
    }

    /**
     * 实现注册逻辑
     */
    @Override
    public boolean register(User user) {
        // 1. 检查用户名是否存在
        User existUser = getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        if (existUser != null) {
            return false;
        }

        // 2. 设置默认昵称
        if (StrUtil.isBlank(user.getNickname())) {
            user.setNickname("考研人_" + RandomUtil.randomString(6));
        }

        // 3. 密码加密 (BCrypt)
        String hashedPwd = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPwd);

        // 4. 设置初始角色为学生
        user.setRole("student");

        // 5. 设置默认考研年份，确保 Dashboard 进度条有终点数据
        // 注意：请根据你 User 实体类中的实际字段名（exam_year 或 examYear）进行调整
        if (StrUtil.isBlank(user.getExamYear())) {
            user.setExamYear("2027");
        }

        // create_time 将由 MyMetaObjectHandler 自动填充
        return save(user);
    }
}