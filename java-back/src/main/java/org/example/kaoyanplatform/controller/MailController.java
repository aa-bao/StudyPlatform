package org.example.kaoyanplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.kaoyanplatform.common.Result;
import org.example.kaoyanplatform.entity.MailCode;
import org.example.kaoyanplatform.mapper.MailCodeMapper;
import org.example.kaoyanplatform.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * 邮箱验证码控制器
 */
@RestController
@RequestMapping("/api/mail")
@Tag(name = "邮箱验证码", description = "提供邮箱验证码发送和验证接口")
public class MailController {

    @Autowired
    private MailService mailService;

    @Autowired
    private MailCodeMapper mailCodeMapper;

    @Value("${mail.code.expireMinutes:5}")
    private int expireMinutes;

    @Value("${mail.code.maxSendCount:5}")
    private int maxSendCount;

    @Value("${mail.code.intervalSeconds:60}")
    private int intervalSeconds;

    /**
     * 发送邮箱验证码
     */
    @PostMapping("/sendCode")
    @Operation(summary = "发送邮箱验证码", description = "发送验证码到指定邮箱")
    public Result<String> sendCode(@RequestBody MailCodeRequest request) {
        String email = request.getEmail();
        String bizType = request.getBizType();

        // 校验邮箱格式
        if (!mailService.validateEmailFormat(email)) {
            return Result.error("邮箱格式不正确");
        }

        // 校验发送频率
        MailCode lastCode = getLastCode(email, bizType);
        if (lastCode != null && isWithinInterval(lastCode.getSendTime())) {
            long remainingSeconds = getRemainingSeconds(lastCode.getSendTime());
            return Result.error("发送频率过高，请稍后再试 (" + remainingSeconds + "秒)");
        }

        // 校验发送次数
        if (lastCode != null && lastCode.getSendCount() >= maxSendCount) {
            return Result.error("今日发送次数已达上限，请明天再试");
        }

        // 生成验证码
        String code = generateCode();

        // 发送邮件
        boolean sendSuccess = mailService.sendCode(email, code);

        if (sendSuccess) {
            // 保存验证码
            saveOrUpdateCode(email, code, bizType, lastCode);
            return Result.success("验证码发送成功，有效期" + expireMinutes + "分钟");
        } else {
            return Result.error("验证码发送失败，请稍后再试");
        }
    }

    /**
     * 验证邮箱验证码
     */
    @PostMapping("/verifyCode")
    @Operation(summary = "验证邮箱验证码", description = "验证邮箱验证码是否正确")
    public Result<String> verifyCode(@RequestBody MailCodeVerifyRequest request) {
        String email = request.getEmail();
        String code = request.getCode();
        String bizType = request.getBizType();

        // 校验邮箱格式
        if (!mailService.validateEmailFormat(email)) {
            return Result.error("邮箱格式不正确");
        }

        // 校验验证码格式
        if (!isValidCode(code)) {
            return Result.error("验证码格式不正确");
        }

        // 获取最后一次发送的验证码
        MailCode lastCode = getLastCode(email, bizType);
        if (lastCode == null) {
            return Result.error("未发送验证码");
        }

        // 校验验证码是否过期
        if (lastCode.getExpireTime().isBefore(LocalDateTime.now())) {
            return Result.error("验证码已过期，请重新获取");
        }

        // 校验验证码是否已使用
        if (lastCode.getStatus() == 1) {
            return Result.error("验证码已使用");
        }

        // 校验验证码是否正确
        if (!lastCode.getCode().equals(code)) {
            return Result.error("验证码不正确");
        }

        // 标记验证码为已使用
        lastCode.setStatus(1);
        mailCodeMapper.updateById(lastCode);

        return Result.success("验证码验证成功");
    }

    private MailCode getLastCode(String email, String bizType) {
        return mailCodeMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MailCode>()
                .eq(MailCode::getEmail, email)
                .eq(MailCode::getBizType, bizType)
                .orderByDesc(MailCode::getSendTime)
                .last("LIMIT 1"));
    }

    private boolean isWithinInterval(LocalDateTime lastSendTime) {
        return lastSendTime.plusSeconds(intervalSeconds).isAfter(LocalDateTime.now());
    }

    private long getRemainingSeconds(LocalDateTime lastSendTime) {
        return intervalSeconds - (LocalDateTime.now().getSecond() - lastSendTime.getSecond());
    }

    private String generateCode() {
        Random random = new Random();
        int codeInt = random.nextInt(900000) + 100000;
        return String.valueOf(codeInt);
    }

    private void saveOrUpdateCode(String email, String code, String bizType, MailCode lastCode) {
        MailCode mailCode = new MailCode();
        mailCode.setEmail(email);
        mailCode.setCode(code);
        mailCode.setBizType(bizType);
        mailCode.setSendTime(LocalDateTime.now());
        mailCode.setExpireTime(LocalDateTime.now().plusMinutes(expireMinutes));
        mailCode.setStatus(0);

        if (lastCode != null) {
            mailCode.setSendCount(lastCode.getSendCount() + 1);
        } else {
            mailCode.setSendCount(1);
        }

        mailCodeMapper.insert(mailCode);
    }

    private boolean isValidCode(String code) {
        return code != null && code.length() == 6 && code.matches("\\d+");
    }

    public static class MailCodeRequest {
        private String email;
        private String bizType;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getBizType() {
            return bizType;
        }

        public void setBizType(String bizType) {
            this.bizType = bizType;
        }
    }

    public static class MailCodeVerifyRequest {
        private String email;
        private String code;
        private String bizType;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getBizType() {
            return bizType;
        }

        public void setBizType(String bizType) {
            this.bizType = bizType;
        }
    }
}
