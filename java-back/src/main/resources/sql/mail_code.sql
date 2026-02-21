-- ----------------------------
-- Table structure for mail_code
-- ----------------------------
DROP TABLE IF EXISTS `mail_code`;
CREATE TABLE `mail_code` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮箱地址',
  `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '验证码',
  `biz_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '业务类型: register-注册, reset-pwd-重置密码',
  `send_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态: 0-未使用, 1-已使用',
  `send_count` int NULL DEFAULT 1 COMMENT '发送次数',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_email_biz`(`email` ASC, `biz_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '邮箱验证码表' ROW_FORMAT = Dynamic;
