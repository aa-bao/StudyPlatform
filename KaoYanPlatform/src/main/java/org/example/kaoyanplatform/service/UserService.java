package org.example.kaoyanplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.kaoyanplatform.entity.User;

/**
 * 用户服务类：继承 IService 获得 count, list, save 等内置方法
 */
public interface UserService extends IService<User> {
    User login(String username, String password);
    boolean register(User user);
}