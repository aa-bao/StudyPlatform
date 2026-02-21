package org.example.kaoyanplatform.service.impl;

import org.example.kaoyanplatform.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.regex.Pattern;

/**
 * 邮箱服务实现类
 */
@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    @Override
    public boolean sendCode(String to, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("考研平台注册验证码");

            String content = "<!DOCTYPE html>\n" +
                    "<html lang=\"zh-CN\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>YanGOGO平台注册验证码</title>\n" +
                    "</head>\n" +
                    "<body style=\"margin: 0; padding: 0; background-color: #f5f7fa; font-family: 'Microsoft YaHei', Arial, sans-serif;\">\n" +
                    "    <div style=\"font-family: 'Microsoft YaHei', Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f8f9fa;\">\n" +
                    "        <div style=\"background-color: #2563eb; color: white; padding: 20px; text-align: center; border-radius: 8px;\">\n" +
                    "            <h1 style=\"margin: 0;\">YanGOGO</h1>\n" +
                    "            <p>欢迎注册</p>\n" +
                    "        </div>\n" +
                    "        <div style=\"background-color: white; padding: 30px; border-radius: 8px; margin-top: 20px;\">\n" +
                    "            <p style=\"font-size: 16px; line-height: 1.6;\">您好，感谢您注册YanGOGO平台！</p>\n" +
                    "            <p style=\"font-size: 16px; line-height: 1.6;\">您的注册验证码是：</p>\n" +
                    "            <div style=\"background-color: #f1f5f9; padding: 20px; text-align: center; border-radius: 4px; margin: 20px 0;\">\n" +
                    "                <span style=\"font-size: 36px; font-weight: bold; color: #2563eb;\">" + code + "</span>\n" +
                    "            </div>\n" +
                    "            <p style=\"font-size: 14px; color: #64748b; line-height: 1.6;\">\n" +
                    "                验证码将在5分钟后过期，请尽快完成注册。<br>\n" +
                    "                如果您没有请求此验证码，请忽略此邮件。\n" +
                    "            </p>\n" +
                    "        </div>\n" +
                    "        <div style=\"text-align: center; margin-top: 30px; color: #64748b;\">\n" +
                    "            <p>create by Tian</p>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";

            helper.setText(content, true);

            javaMailSender.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean validateEmailFormat(String email) {
        return email != null && Pattern.matches(EMAIL_PATTERN, email);
    }
}
