/*
 Navicat Premium Dump SQL

 Source Server         : mysql8
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : kaoyan_platform

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 22/02/2026 14:22:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for answer_record
-- ----------------------------
DROP TABLE IF EXISTS `answer_record`;
CREATE TABLE `answer_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户id',
  `question_id` bigint NOT NULL COMMENT '题目id',
  `subject_id` int NULL DEFAULT NULL COMMENT '科目ID',
  `user_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '用户的答案',
  `is_correct` tinyint(1) NULL DEFAULT NULL COMMENT '是否正确: 0-错, 1-对',
  `score` smallint NULL DEFAULT 0 COMMENT '得分',
  `duration` int NULL DEFAULT 0 COMMENT '答题用时(秒)',
  `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '题目来源（每日测试、正常刷题）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_ques`(`user_id` ASC, `question_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 89 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '答题记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for book_subject_rel
-- ----------------------------
DROP TABLE IF EXISTS `book_subject_rel`;
CREATE TABLE `book_subject_rel`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `book_id` int NOT NULL COMMENT '习题册id',
  `subject_id` int NOT NULL COMMENT '知识科目ID (高数:401；线代:402；概率403)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_subject_book`(`book_id` ASC, `subject_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 56 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试科目与习题册关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for error_question
-- ----------------------------
DROP TABLE IF EXISTS `error_question`;
CREATE TABLE `error_question`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `error_count` int NULL DEFAULT NULL COMMENT '错误次数',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '错题记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for exam_paper
-- ----------------------------
DROP TABLE IF EXISTS `exam_paper`;
CREATE TABLE `exam_paper`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '试卷ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '试卷标题',
  `year` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '考试年份',
  `exam_spec_id` int NULL DEFAULT NULL COMMENT '关联科目ID',
  `total_score` int NULL DEFAULT 150 COMMENT '总分',
  `time_limit` int NULL DEFAULT 180 COMMENT '限时（分钟）',
  `paper_type` tinyint NULL DEFAULT NULL COMMENT '试卷类型：0-真题, 1-模拟卷',
  `structure_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '试卷结构JSON',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_exam_spec_id`(`exam_spec_id` ASC) USING BTREE,
  INDEX `idx_year`(`year` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '试卷表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for exam_record
-- ----------------------------
DROP TABLE IF EXISTS `exam_record`;
CREATE TABLE `exam_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `session_id` bigint NOT NULL COMMENT '会话id',
  `question_id` bigint NOT NULL COMMENT '题目id',
  `user_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '用户答案',
  `is_correct` tinyint NULL DEFAULT NULL COMMENT '对/错/待定（主观题）',
  `score_earned` decimal(5, 2) NULL DEFAULT NULL COMMENT '得分率',
  `duration_seconds` int NULL DEFAULT NULL COMMENT '用时',
  `ai_feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'ai返回',
  `user_process_grading` tinyint NULL DEFAULT NULL COMMENT '用户自评-过程：null-未批改, 0-错误, 1-正确',
  `user_result_grading` tinyint NULL DEFAULT NULL COMMENT '用户自评-结果：null-未批改, 0-错误, 1-正确',
  `grading_time` datetime NULL DEFAULT NULL COMMENT '批改时间',
  `knowledge_point_id` int NULL DEFAULT NULL COMMENT '知识点id',
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_session_question`(`session_id` ASC, `question_id` ASC) USING BTREE,
  INDEX `idx_user_grading`(`user_process_grading` ASC, `user_result_grading` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 85 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '考试记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for exam_session
-- ----------------------------
DROP TABLE IF EXISTS `exam_session`;
CREATE TABLE `exam_session`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `paper_id` bigint NOT NULL COMMENT '试卷 ID',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-进行中, 1-已完成',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `submit_time` timestamp NULL DEFAULT NULL COMMENT '提交时间',
  `expected_end_time` datetime NULL DEFAULT NULL COMMENT '预期结束时间 (create_time + paper.time_limit)',
  `total_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '总分',
  `switch_count` int NULL DEFAULT 0 COMMENT '切换题目次数（切屏检测）',
  `ai_summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'AI 总结',
  `snapshot_answers` json NULL COMMENT '答题快照 JSON',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_paper_id`(`paper_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '考试会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for exercise_book
-- ----------------------------
DROP TABLE IF EXISTS `exercise_book`;
CREATE TABLE `exercise_book`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '习题册名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '简介',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '习题册表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for favorite_folder
-- ----------------------------
DROP TABLE IF EXISTS `favorite_folder`;
CREATE TABLE `favorite_folder`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户名',
  `question_id` bigint NOT NULL COMMENT '题目id',
  `tags` json NULL COMMENT '自定义标签',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_ques`(`user_id` ASC, `question_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '收藏夹' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mail_code
-- ----------------------------
DROP TABLE IF EXISTS `mail_code`;
CREATE TABLE `mail_code`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮箱地址',
  `code` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '验证码',
  `biz_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '业务类型：register/login/reset',
  `send_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `send_count` int NULL DEFAULT 1 COMMENT '发送次数',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-未使用, 1-已使用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_email_biz`(`email` ASC, `biz_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '邮箱验证码表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` tinyint NOT NULL COMMENT '题目类型(1:单选,2:多选,3:填空,4:简答)',
  `difficulty` tinyint NULL DEFAULT 3 COMMENT '难度(1-5)',
  `content_json` json NOT NULL COMMENT '题目内容JSON: {content, options, answer, analysis, tags}',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_difficulty`(`difficulty` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question_book_rel
-- ----------------------------
DROP TABLE IF EXISTS `question_book_rel`;
CREATE TABLE `question_book_rel`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `book_id` int NOT NULL COMMENT '习题册ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_question_book`(`question_id` ASC, `book_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  INDEX `idx_book_id`(`book_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目-书本关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question_paper_rel
-- ----------------------------
DROP TABLE IF EXISTS `question_paper_rel`;
CREATE TABLE `question_paper_rel`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `paper_id` bigint NULL DEFAULT NULL COMMENT '试卷id',
  `question_id` bigint NULL DEFAULT NULL COMMENT '题目id',
  `sort_order` smallint NULL DEFAULT NULL COMMENT '题号',
  `score_value` decimal(5, 2) NULL DEFAULT NULL COMMENT '分值',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '题目类型: \r\n1-单选\r\n2-多选\r\n3-填空\r\n4-综合应用\r\n5-完型填空\r\n6-阅读理解\r\n7-新题型\r\n8-翻译\r\n9-小作文\r\n10-大作文',
  `process_score` int NULL DEFAULT NULL COMMENT '过程分满分（仅用于简答题）',
  `process_ratio` double NULL DEFAULT NULL COMMENT '过程分比例（如0.8表示80%，仅用于简答题）',
  `result_score` int NULL DEFAULT NULL COMMENT '结果分满分（仅用于简答题）',
  `result_ratio` double NULL DEFAULT NULL COMMENT '结果分比例（如0.2表示20%，仅用于简答题）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `paper_id`(`paper_id` ASC) USING BTREE,
  INDEX `idx_map_paper_question_type`(`type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '题目-试卷关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for question_subject_rel
-- ----------------------------
DROP TABLE IF EXISTS `question_subject_rel`;
CREATE TABLE `question_subject_rel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `subject_id` int NOT NULL COMMENT '科目ID或知识点ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_question_subject`(`question_id` ASC, `subject_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  INDEX `idx_subject_id`(`subject_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 146 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目-科目关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for resource
-- ----------------------------
DROP TABLE IF EXISTS `resource`;
CREATE TABLE `resource`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资料标题',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '下载/预览地址',
  `file_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'pdf' COMMENT '文件类型',
  `subject_id` int NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '学习资料表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for subject
-- ----------------------------
DROP TABLE IF EXISTS `subject`;
CREATE TABLE `subject`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '科目名称',
  `parent_id` int NULL DEFAULT 0 COMMENT '父级ID（顶级为0）',
  `video_url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '视频链接',
  `sort` tinyint NULL DEFAULT 0 COMMENT '排序号',
  `level` tinyint NULL DEFAULT 1 COMMENT '层级: \r\nL1-考试大类（专业课/公共课）；\r\nL2-考试规格（数学一，英语一）；\r\nL3-具体学科（高数，线代）；\r\nL4-章节与知识点（函数与极限、二重积分）；\r\nL5-题型 / 解题技巧（泰勒公式）',
  `question_count` int NULL DEFAULT 0 COMMENT '该分类下的题目总数',
  `scope` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1,2,3' COMMENT '适用大纲: 1-数一, 2-数二, 3-数三 (逗号隔开)',
  `question_types` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支持的题型列表，逗号分隔，如: 1,2,4',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 62 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '科目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for subject_weight_rel
-- ----------------------------
DROP TABLE IF EXISTS `subject_weight_rel`;
CREATE TABLE `subject_weight_rel`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `subject_id` int NOT NULL COMMENT '科目ID',
  `exam_spec_id` int NOT NULL COMMENT '考试规格ID（关联tb_subject.id，如：英语一=2，英语二=3）',
  `weight` float NULL DEFAULT NULL COMMENT '该科目在该考试规格下的权重（百分比）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_subject_exam`(`subject_id` ASC, `exam_spec_id` ASC) USING BTREE COMMENT '同一科目在同一考试规格下只能有一条记录',
  INDEX `idx_exam_spec`(`exam_spec_id` ASC) USING BTREE COMMENT '考试规格索引',
  INDEX `idx_subject`(`subject_id` ASC) USING BTREE COMMENT '科目索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '科目权重映射表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名/账号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `phone` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `nickname` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `role` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'student' COMMENT '角色',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像URL',
  `target_school` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '目标院校',
  `target_total_score` smallint NULL DEFAULT 0 COMMENT '目标总分',
  `exam_year` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '考研年份',
  `exam_subjects` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公共课',
  `motto` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '期语',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for topic_drill_progress
-- ----------------------------
DROP TABLE IF EXISTS `topic_drill_progress`;
CREATE TABLE `topic_drill_progress`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `subject_id` int NOT NULL COMMENT '知识点ID',
  `question_count` int NULL DEFAULT 0 COMMENT '题目总数',
  `answered_count` int NULL DEFAULT 0 COMMENT '已答题目数量',
  `correct_count` int NULL DEFAULT 0 COMMENT '正确答案数量',
  `accuracy` int NULL DEFAULT 0 COMMENT '正确率',
  `questions_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '题目数据（JSON格式）',
  `card_positions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '卡片位置（JSON格式）',
  `timestamp` datetime NULL DEFAULT NULL COMMENT '时间戳',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_subject`(`user_id` ASC, `subject_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '专项突破学习进度表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
