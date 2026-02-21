package org.example.kaoyanplatform.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 数据库初始化器，用于在项目启动时自动创建邮件验证码表
 */
@Component
public class DatabaseInitializer implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            // 检查mail_code表是否存在
            String checkTableSql = "SHOW TABLES LIKE 'mail_code'";
            int count = jdbcTemplate.queryForList(checkTableSql).size();

            if (count == 0) {
                // 读取SQL脚本
                Resource resource = resourceLoader.getResource("classpath:sql/mail_code.sql");
                byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
                String sql = new String(bytes, StandardCharsets.UTF_8);

                // 执行SQL脚本创建表
                jdbcTemplate.execute(sql);
                System.out.println("Successfully created mail_code table");
            }
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
