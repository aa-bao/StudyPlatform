package org.example.kaoyanplatform.service;

/**
 * 邮箱服务接口
 */
public interface MailService {

    /**
     * 发送验证码邮件
     * @param to 收件人邮箱
     * @param code 验证码
     * @return 是否发送成功
     */
    boolean sendCode(String to, String code);

    /**
     * 验证邮箱格式
     * @param email 邮箱地址
     * @return 是否是有效的邮箱格式
     */
    boolean validateEmailFormat(String email);
}
