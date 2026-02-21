package org.example.kaoyanplatform;

import org.example.kaoyanplatform.service.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 邮件服务测试类
 */
@SpringBootTest
class MailServiceTest {

    @Autowired
    private MailService mailService;

    @Test
    void testSendCode() {
        // 测试发送验证码到指定邮箱
        String email = "test@example.com"; // 请替换为真实邮箱
        String code = "123456";

        boolean success = mailService.sendCode(email, code);

        if (success) {
            System.out.println("邮件发送成功！");
        } else {
            System.out.println("邮件发送失败！");
        }
    }
}
