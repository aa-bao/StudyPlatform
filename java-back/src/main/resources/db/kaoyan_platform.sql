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

 Date: 04/02/2026 20:50:51
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
  `user_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '用户的答案',
  `is_correct` tinyint(1) NULL DEFAULT NULL COMMENT '是否正确: 0-错, 1-对',
  `score` smallint NULL DEFAULT 0 COMMENT '得分',
  `duration` int NULL DEFAULT 0 COMMENT '答题用时(秒)',
  `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '题目来源（每日测试、正常刷题）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_ques`(`user_id` ASC, `question_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 81 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '答题记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of answer_record
-- ----------------------------
INSERT INTO `answer_record` VALUES (1, 1, 1, 'A', 1, 5, 50000, 'daily_test', '2026-01-28 00:26:16');
INSERT INTO `answer_record` VALUES (2, 1, 2, 'B', 0, 0, 50000, NULL, '2026-01-29 00:26:20');
INSERT INTO `answer_record` VALUES (3, 1, 1, 'C', 0, 0, 104, NULL, '2026-01-29 00:29:40');
INSERT INTO `answer_record` VALUES (4, 1, 2, 'A,B,D', 0, 0, 10, NULL, '2026-01-29 00:30:04');
INSERT INTO `answer_record` VALUES (5, 1, 2, 'A,B,D', 1, 0, 10, 'daily_test', '2026-01-29 00:30:04');
INSERT INTO `answer_record` VALUES (6, 1, 2, 'A,B,D', 1, 0, 10, 'daily_test', '2026-01-29 00:30:04');
INSERT INTO `answer_record` VALUES (7, 1, 2, 'A,B,D', 1, 0, 1000, NULL, '2026-01-29 00:30:04');
INSERT INTO `answer_record` VALUES (8, 1, 2, 'A,B,D', 1, 0, 10, 'daily_test', '2026-01-29 00:30:04');
INSERT INTO `answer_record` VALUES (35, 1, 1, 'X', 0, 0, 30, NULL, '2026-01-25 23:46:19');
INSERT INTO `answer_record` VALUES (36, 1, 5, 'X', 0, 0, 30, NULL, '2026-01-25 23:46:19');
INSERT INTO `answer_record` VALUES (37, 1, 9, 'X', 0, 0, 30, NULL, '2026-01-25 23:46:19');
INSERT INTO `answer_record` VALUES (38, 1, 13, 'X', 0, 0, 30, NULL, '2026-01-25 23:46:19');
INSERT INTO `answer_record` VALUES (39, 1, 2, 'X', 0, 0, 30, NULL, '2026-01-25 23:46:19');
INSERT INTO `answer_record` VALUES (42, 1, 6, 'X', 0, 0, 30, NULL, '2026-01-28 23:46:19');
INSERT INTO `answer_record` VALUES (43, 1, 10, 'X', 0, 0, 30, NULL, '2026-01-28 23:46:19');
INSERT INTO `answer_record` VALUES (44, 1, 14, 'X', 0, 0, 30, NULL, '2026-01-28 23:46:19');
INSERT INTO `answer_record` VALUES (45, 1, 3, 'X', 0, 0, 30, NULL, '2026-01-28 23:46:19');
INSERT INTO `answer_record` VALUES (46, 1, 7, 'X', 0, 0, 30, NULL, '2026-01-28 23:46:19');
INSERT INTO `answer_record` VALUES (49, 1, 11, 'A', 1, 5, 20, NULL, '2026-01-27 23:46:19');
INSERT INTO `answer_record` VALUES (50, 1, 15, 'A', 1, 5, 20, NULL, '2026-01-27 23:46:19');
INSERT INTO `answer_record` VALUES (51, 1, 4, 'A', 1, 5, 20, NULL, '2026-01-27 23:46:19');
INSERT INTO `answer_record` VALUES (52, 1, 8, 'A', 1, 5, 20, NULL, '2026-01-27 23:46:19');
INSERT INTO `answer_record` VALUES (53, 1, 12, 'A', 1, 5, 20, NULL, '2026-01-27 23:46:19');
INSERT INTO `answer_record` VALUES (56, 1, 4, 'buyb', 0, 0, 4, NULL, '2026-01-31 00:35:04');
INSERT INTO `answer_record` VALUES (57, 1, 5, 'A', 1, 5, 3, 'daily_test', '2026-01-31 00:35:11');
INSERT INTO `answer_record` VALUES (58, 1, 9, 'B', 1, 5, 3, 'daily_test', '2026-01-31 00:35:15');
INSERT INTO `answer_record` VALUES (59, 1, 13, 'C', 0, 0, 1, NULL, '2026-01-31 00:35:17');
INSERT INTO `answer_record` VALUES (60, 1, 2, 'C', 0, 0, 1, NULL, '2026-01-31 00:35:18');
INSERT INTO `answer_record` VALUES (61, 1, 2, 'A,C,D', 1, 5, 4, 'daily_test', '2026-01-31 00:35:21');
INSERT INTO `answer_record` VALUES (62, 1, 1, 'A', 1, 5, 1, 'daily_test', '2026-01-31 00:35:24');
INSERT INTO `answer_record` VALUES (63, 1, 6, 'B,C,D', 0, 0, 9, NULL, '2026-01-31 00:35:33');
INSERT INTO `answer_record` VALUES (64, 1, 10, 'A,B', 0, 0, 11, NULL, '2026-01-31 00:35:49');
INSERT INTO `answer_record` VALUES (65, 1, 14, 'D', 0, 0, 1, NULL, '2026-01-31 00:35:51');
INSERT INTO `answer_record` VALUES (66, 1, 3, 'l', 0, 0, 3, NULL, '2026-01-31 00:35:55');
INSERT INTO `answer_record` VALUES (67, 1, 3, 'l', 0, 0, 5, NULL, '2026-01-31 00:35:57');
INSERT INTO `answer_record` VALUES (68, 1, 3, 'lub', 0, 0, 8, NULL, '2026-01-31 00:36:00');
INSERT INTO `answer_record` VALUES (69, 1, 3, '678', 0, 0, 11, NULL, '2026-01-31 00:36:03');
INSERT INTO `answer_record` VALUES (70, 1, 3, '678', 0, 0, 11, NULL, '2026-01-31 00:36:03');
INSERT INTO `answer_record` VALUES (71, 1, 3, '678', 0, 0, 11, NULL, '2026-01-31 00:36:03');
INSERT INTO `answer_record` VALUES (72, 1, 3, '678', 0, 0, 12, 'daily_test', '2026-01-31 00:36:03');
INSERT INTO `answer_record` VALUES (73, 1, 3, '678', 0, 0, 35, 'daily_test', '2026-01-31 00:36:27');
INSERT INTO `answer_record` VALUES (74, 1, 7, '435', 0, 0, 3, 'daily_test', '2026-01-31 00:51:22');
INSERT INTO `answer_record` VALUES (75, 1, 11, '34', 0, 0, 1, 'daily_test', '2026-01-31 00:51:25');
INSERT INTO `answer_record` VALUES (76, 1, 15, '4', 0, 0, 1, 'daily_test', '2026-01-31 00:51:27');
INSERT INTO `answer_record` VALUES (77, 1, 8, '234', 0, 0, 1, 'daily_test', '2026-01-31 00:51:28');
INSERT INTO `answer_record` VALUES (78, 1, 12, '天天', 0, 0, 2, NULL, '2026-01-31 00:51:33');
INSERT INTO `answer_record` VALUES (79, 1, 9, 'B', 1, 5, 23, 'single_practice', '2026-01-31 20:12:42');
INSERT INTO `answer_record` VALUES (80, 1, 10, 'A,B,C,D', 0, 0, 2, 'single_practice', '2026-01-31 20:14:11');

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
-- Records of book_subject_rel
-- ----------------------------
INSERT INTO `book_subject_rel` VALUES (12, 1, -3);
INSERT INTO `book_subject_rel` VALUES (8, 1, 4);
INSERT INTO `book_subject_rel` VALUES (9, 1, 5);
INSERT INTO `book_subject_rel` VALUES (10, 1, 6);
INSERT INTO `book_subject_rel` VALUES (11, 1, 19);
INSERT INTO `book_subject_rel` VALUES (15, 2, -3);
INSERT INTO `book_subject_rel` VALUES (13, 2, 4);
INSERT INTO `book_subject_rel` VALUES (16, 2, 5);
INSERT INTO `book_subject_rel` VALUES (17, 2, 6);
INSERT INTO `book_subject_rel` VALUES (14, 2, 20);
INSERT INTO `book_subject_rel` VALUES (18, 3, -3);
INSERT INTO `book_subject_rel` VALUES (19, 3, 4);
INSERT INTO `book_subject_rel` VALUES (23, 3, 5);
INSERT INTO `book_subject_rel` VALUES (24, 3, 6);
INSERT INTO `book_subject_rel` VALUES (20, 3, 19);
INSERT INTO `book_subject_rel` VALUES (21, 3, 20);
INSERT INTO `book_subject_rel` VALUES (22, 3, 21);
INSERT INTO `book_subject_rel` VALUES (25, 4, -3);
INSERT INTO `book_subject_rel` VALUES (26, 4, 4);
INSERT INTO `book_subject_rel` VALUES (30, 4, 5);
INSERT INTO `book_subject_rel` VALUES (31, 4, 6);
INSERT INTO `book_subject_rel` VALUES (27, 4, 19);
INSERT INTO `book_subject_rel` VALUES (28, 4, 20);
INSERT INTO `book_subject_rel` VALUES (29, 4, 21);
INSERT INTO `book_subject_rel` VALUES (32, 5, 1);
INSERT INTO `book_subject_rel` VALUES (33, 5, 8);
INSERT INTO `book_subject_rel` VALUES (34, 5, 9);
INSERT INTO `book_subject_rel` VALUES (35, 5, 10);
INSERT INTO `book_subject_rel` VALUES (36, 5, 11);
INSERT INTO `book_subject_rel` VALUES (37, 5, 12);
INSERT INTO `book_subject_rel` VALUES (38, 6, 1);
INSERT INTO `book_subject_rel` VALUES (39, 6, 8);
INSERT INTO `book_subject_rel` VALUES (40, 6, 9);
INSERT INTO `book_subject_rel` VALUES (41, 6, 10);
INSERT INTO `book_subject_rel` VALUES (42, 6, 11);
INSERT INTO `book_subject_rel` VALUES (43, 6, 12);
INSERT INTO `book_subject_rel` VALUES (44, 7, -2);
INSERT INTO `book_subject_rel` VALUES (45, 7, 2);
INSERT INTO `book_subject_rel` VALUES (52, 7, 3);
INSERT INTO `book_subject_rel` VALUES (46, 7, 13);
INSERT INTO `book_subject_rel` VALUES (47, 7, 14);
INSERT INTO `book_subject_rel` VALUES (48, 7, 15);
INSERT INTO `book_subject_rel` VALUES (49, 7, 16);
INSERT INTO `book_subject_rel` VALUES (50, 7, 17);
INSERT INTO `book_subject_rel` VALUES (51, 7, 18);
INSERT INTO `book_subject_rel` VALUES (53, 8, 7);
INSERT INTO `book_subject_rel` VALUES (54, 8, 22);
INSERT INTO `book_subject_rel` VALUES (55, 9, 24);

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
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '错题记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of error_question
-- ----------------------------
INSERT INTO `error_question` VALUES (1, 1, 2, 10, '2026-01-30 21:53:21', '2026-01-29 00:26:19');
INSERT INTO `error_question` VALUES (2, 1, 1, 3, '2026-01-30 00:33:26', '2026-01-29 00:29:40');
INSERT INTO `error_question` VALUES (3, 1, 3, 5, '2026-01-30 01:01:41', '2026-01-30 00:31:15');
INSERT INTO `error_question` VALUES (4, 1, 4, 5, '2026-01-31 00:35:04', '2026-01-30 00:31:25');
INSERT INTO `error_question` VALUES (5, 1, 1, 1, '2026-01-25 23:46:19', '2026-01-30 23:46:19');
INSERT INTO `error_question` VALUES (6, 1, 5, 1, '2026-01-25 23:46:19', '2026-01-30 23:46:19');
INSERT INTO `error_question` VALUES (7, 1, 9, 1, '2026-01-25 23:46:19', '2026-01-30 23:46:19');
INSERT INTO `error_question` VALUES (8, 1, 13, 2, '2026-01-31 00:35:17', '2026-01-30 23:46:19');
INSERT INTO `error_question` VALUES (9, 1, 2, 1, '2026-01-25 23:46:19', '2026-01-30 23:46:19');
INSERT INTO `error_question` VALUES (12, 1, 6, 2, '2026-01-31 00:35:33', '2026-01-30 23:46:19');
INSERT INTO `error_question` VALUES (13, 1, 10, 3, '2026-01-31 20:14:11', '2026-01-30 23:46:19');
INSERT INTO `error_question` VALUES (14, 1, 14, 2, '2026-01-31 00:35:51', '2026-01-30 23:46:19');
INSERT INTO `error_question` VALUES (15, 1, 3, 1, '2026-01-28 23:46:19', '2026-01-30 23:46:19');
INSERT INTO `error_question` VALUES (16, 1, 7, 2, '2026-01-31 00:51:22', '2026-01-30 23:46:19');
INSERT INTO `error_question` VALUES (19, 1, 11, 1, '2026-01-31 00:51:25', '2026-01-31 00:51:24');
INSERT INTO `error_question` VALUES (20, 1, 15, 1, '2026-01-31 00:51:27', '2026-01-31 00:51:26');
INSERT INTO `error_question` VALUES (21, 1, 8, 1, '2026-01-31 00:51:28', '2026-01-31 00:51:28');
INSERT INTO `error_question` VALUES (22, 1, 12, 1, '2026-01-31 00:51:33', '2026-01-31 00:51:32');

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
-- Records of exam_paper
-- ----------------------------
INSERT INTO `exam_paper` VALUES (1, '2025年考研数学一真题', '2025', 4, 150, 180, 0, NULL, '2026-01-26 00:43:19');
INSERT INTO `exam_paper` VALUES (2, '2025年考研数学二真题', '2025', 5, 150, 180, 0, NULL, '2026-01-26 00:43:28');
INSERT INTO `exam_paper` VALUES (3, '2025年考研数学三真题', '2025', 6, 150, 180, 0, NULL, '2026-01-26 00:43:36');
INSERT INTO `exam_paper` VALUES (4, '2025年408计算机统考真题', '2025', 7, 150, 180, 0, NULL, '2026-01-26 00:43:58');
INSERT INTO `exam_paper` VALUES (5, '2025年考研政治真题', '2025', 1, 150, 180, 0, NULL, '2026-01-26 00:44:10');
INSERT INTO `exam_paper` VALUES (6, '2025年考研政治真题', '2026', 7, 150, 180, 0, NULL, '2026-01-31 20:24:00');

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
-- Records of exam_record
-- ----------------------------
INSERT INTO `exam_record` VALUES (1, 1, 1, 'A', 1, 5.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-27 21:51:56');
INSERT INTO `exam_record` VALUES (2, 1, 2, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-27 21:51:56');
INSERT INTO `exam_record` VALUES (3, 1, 3, 'I dont know!!', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-27 21:51:56');
INSERT INTO `exam_record` VALUES (4, 1, 4, 'I dont know!!', 3, 0.00, NULL, 'AI批改失败：智能批改失败: I/O error on POST request for \"http://localhost:8082/ai/grade\": Connection refused: no further information', NULL, NULL, NULL, NULL, '2026-01-27 21:51:56');
INSERT INTO `exam_record` VALUES (5, 2, 1, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-27 21:57:01');
INSERT INTO `exam_record` VALUES (6, 2, 2, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-27 21:57:01');
INSERT INTO `exam_record` VALUES (7, 2, 3, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-27 21:57:01');
INSERT INTO `exam_record` VALUES (8, 2, 4, '', 3, 0.00, NULL, 'AI批改失败：智能批改失败: I/O error on POST request for \"http://localhost:8082/ai/grade\": Connection refused: no further information', NULL, NULL, NULL, NULL, '2026-01-27 21:57:01');
INSERT INTO `exam_record` VALUES (9, 3, 1, 'A', 1, 5.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-27 22:23:50');
INSERT INTO `exam_record` VALUES (10, 3, 2, 'A,B', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-27 22:23:50');
INSERT INTO `exam_record` VALUES (11, 3, 3, '3124', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-27 22:23:50');
INSERT INTO `exam_record` VALUES (12, 3, 4, '1', 3, 0.00, NULL, 'AI批改失败：智能批改失败: I/O error on POST request for \"http://localhost:8082/ai/grade\": Connection refused: no further information', NULL, NULL, NULL, NULL, '2026-01-27 22:23:50');
INSERT INTO `exam_record` VALUES (13, 4, 1, 'A', 1, 5.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-27 22:52:24');
INSERT INTO `exam_record` VALUES (14, 4, 2, 'B,C', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-27 22:52:24');
INSERT INTO `exam_record` VALUES (15, 4, 3, 'i', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-27 22:52:24');
INSERT INTO `exam_record` VALUES (16, 4, 4, 'iuyh', 3, 0.00, NULL, 'AI批改失败：智能批改失败: I/O error on POST request for \"http://localhost:8082/ai/grade\": Connection refused: no further information', NULL, NULL, NULL, NULL, '2026-01-27 22:52:24');
INSERT INTO `exam_record` VALUES (17, 5, 1, 'A', 1, 5.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-28 01:01:45');
INSERT INTO `exam_record` VALUES (18, 5, 2, 'A', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-28 01:01:45');
INSERT INTO `exam_record` VALUES (19, 5, 3, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-28 01:01:45');
INSERT INTO `exam_record` VALUES (20, 5, 4, '', 3, 0.00, NULL, 'AI批改失败：智能批改失败: I/O error on POST request for \"http://localhost:8082/ai/grade\": Connection refused: no further information', NULL, NULL, NULL, NULL, '2026-01-28 01:01:45');
INSERT INTO `exam_record` VALUES (21, 6, 1, 'B', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-28 01:08:37');
INSERT INTO `exam_record` VALUES (22, 6, 2, 'A', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-28 01:08:37');
INSERT INTO `exam_record` VALUES (23, 6, 3, '$15^{3}$', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-28 01:08:37');
INSERT INTO `exam_record` VALUES (24, 6, 4, '$\\alpha+\\beta$', 3, 0.00, NULL, 'AI批改失败：智能批改失败: I/O error on POST request for \"http://localhost:8082/ai/grade\": Connection refused: no further information', NULL, NULL, NULL, NULL, '2026-01-28 01:08:37');
INSERT INTO `exam_record` VALUES (25, 7, 1, 'A', 1, 5.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-28 01:14:29');
INSERT INTO `exam_record` VALUES (26, 7, 2, 'C', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-28 01:14:29');
INSERT INTO `exam_record` VALUES (27, 7, 3, '\\theta', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-28 01:14:29');
INSERT INTO `exam_record` VALUES (28, 7, 4, '\\times', 3, 0.00, NULL, 'AI批改失败：智能批改失败: I/O error on POST request for \"http://localhost:8082/ai/grade\": Connection refused: no further information', 0, 0, '2026-01-28 01:15:09', NULL, '2026-01-28 01:14:29');
INSERT INTO `exam_record` VALUES (29, 8, 1, 'A', 1, 5.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 00:28:31');
INSERT INTO `exam_record` VALUES (30, 8, 2, 'A,B,C', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 00:28:31');
INSERT INTO `exam_record` VALUES (31, 8, 3, '12341234', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 00:28:31');
INSERT INTO `exam_record` VALUES (32, 8, 4, '432143214', 3, 0.00, NULL, 'AI批改失败：智能批改失败: I/O error on POST request for \"http://localhost:8082/ai/grade\": Connection refused: no further information', NULL, NULL, NULL, NULL, '2026-01-30 00:28:31');
INSERT INTO `exam_record` VALUES (33, 9, 1, 'A', 1, 5.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 00:42:27');
INSERT INTO `exam_record` VALUES (34, 9, 2, 'A', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 00:42:27');
INSERT INTO `exam_record` VALUES (35, 9, 3, '\\beta_{}', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 00:42:27');
INSERT INTO `exam_record` VALUES (36, 9, 4, '', 3, 0.00, NULL, 'AI批改失败：智能批改失败: I/O error on POST request for \"http://localhost:8082/ai/grade\": Connection refused: no further information', NULL, NULL, NULL, NULL, '2026-01-30 00:42:27');
INSERT INTO `exam_record` VALUES (37, 10, 1, 'A', 1, 5.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 01:14:58');
INSERT INTO `exam_record` VALUES (38, 10, 2, 'D', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 01:14:58');
INSERT INTO `exam_record` VALUES (39, 10, 3, '\\sigma', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 01:14:58');
INSERT INTO `exam_record` VALUES (40, 10, 4, '\\lambda', 3, 0.00, NULL, 'AI批改失败：智能批改失败: I/O error on POST request for \"http://localhost:8082/ai/grade\": Connection refused: no further information', 1, 1, '2026-01-30 01:18:27', NULL, '2026-01-30 01:14:58');
INSERT INTO `exam_record` VALUES (41, 11, 1, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 01:30:32');
INSERT INTO `exam_record` VALUES (42, 11, 2, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 01:30:32');
INSERT INTO `exam_record` VALUES (43, 11, 3, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 01:30:32');
INSERT INTO `exam_record` VALUES (44, 11, 4, '', 3, 0.00, NULL, 'AI批改失败：智能批改失败: I/O error on POST request for \"http://localhost:8082/ai/grade\": Connection refused: no further information', NULL, NULL, NULL, NULL, '2026-01-30 01:30:32');
INSERT INTO `exam_record` VALUES (45, 12, 1, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 01:42:52');
INSERT INTO `exam_record` VALUES (46, 12, 2, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 01:42:52');
INSERT INTO `exam_record` VALUES (47, 12, 3, '\\alpha_{}\\in_{}\\times\\lim_{x \\to \\infty}\\neq\\pm_{}\\lambda\\beta\\alpha\\infty\\pi', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 01:42:52');
INSERT INTO `exam_record` VALUES (48, 12, 4, '_{}\\alpha\\theta', 3, 0.00, NULL, 'AI批改失败：智能批改失败: I/O error on POST request for \"http://localhost:8082/ai/grade\": Connection refused: no further information', NULL, NULL, NULL, NULL, '2026-01-30 01:42:52');
INSERT INTO `exam_record` VALUES (49, 13, 1, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 01:48:22');
INSERT INTO `exam_record` VALUES (50, 13, 2, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 01:48:22');
INSERT INTO `exam_record` VALUES (51, 13, 3, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 01:48:22');
INSERT INTO `exam_record` VALUES (52, 13, 4, '', 3, 0.00, NULL, 'AI批改失败：智能批改失败: I/O error on POST request for \"http://localhost:8082/ai/grade\": Connection refused: no further information', NULL, NULL, NULL, NULL, '2026-01-30 01:48:22');
INSERT INTO `exam_record` VALUES (53, 14, 1, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 01:48:29');
INSERT INTO `exam_record` VALUES (54, 14, 2, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 01:48:29');
INSERT INTO `exam_record` VALUES (55, 14, 3, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 01:48:29');
INSERT INTO `exam_record` VALUES (56, 14, 4, '', 3, 0.00, NULL, 'AI批改失败：智能批改失败: I/O error on POST request for \"http://localhost:8082/ai/grade\": Connection refused: no further information', NULL, NULL, NULL, NULL, '2026-01-30 01:48:29');
INSERT INTO `exam_record` VALUES (57, 15, 1, 'A', 1, 5.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 01:57:14');
INSERT INTO `exam_record` VALUES (58, 15, 2, 'A,B,C', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 01:57:14');
INSERT INTO `exam_record` VALUES (59, 15, 3, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 01:57:14');
INSERT INTO `exam_record` VALUES (60, 15, 4, '', 3, 0.00, NULL, 'AI批改失败：智能批改失败: I/O error on POST request for \"http://localhost:8082/ai/grade\": Connection refused: no further information', 1, 1, '2026-01-30 01:57:22', NULL, '2026-01-30 01:57:14');
INSERT INTO `exam_record` VALUES (61, 16, 1, 'C', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 02:03:01');
INSERT INTO `exam_record` VALUES (62, 16, 2, 'A,D', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 02:03:01');
INSERT INTO `exam_record` VALUES (63, 16, 3, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 02:03:01');
INSERT INTO `exam_record` VALUES (64, 16, 4, '', 3, 0.00, NULL, 'AI批改失败：智能批改失败: I/O error on POST request for \"http://localhost:8082/ai/grade\": Connection refused: no further information', 1, 1, '2026-01-30 02:03:11', NULL, '2026-01-30 02:03:01');
INSERT INTO `exam_record` VALUES (65, 17, 9, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 02:05:17');
INSERT INTO `exam_record` VALUES (66, 17, 10, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 02:05:17');
INSERT INTO `exam_record` VALUES (67, 17, 11, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 02:05:17');
INSERT INTO `exam_record` VALUES (68, 17, 12, '', 3, 0.00, NULL, 'AI批改失败：智能批改失败: I/O error on POST request for \"http://localhost:8082/ai/grade\": Connection refused: no further information', NULL, NULL, NULL, NULL, '2026-01-30 02:05:17');
INSERT INTO `exam_record` VALUES (69, 18, 9, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 02:05:36');
INSERT INTO `exam_record` VALUES (70, 18, 10, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 02:05:36');
INSERT INTO `exam_record` VALUES (71, 18, 11, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 02:05:36');
INSERT INTO `exam_record` VALUES (72, 18, 12, '', 3, 0.00, NULL, 'AI批改失败：智能批改失败: I/O error on POST request for \"http://localhost:8082/ai/grade\": Connection refused: no further information', 1, 1, '2026-01-30 02:05:40', NULL, '2026-01-30 02:05:36');
INSERT INTO `exam_record` VALUES (73, 19, 1, 'A', 1, 5.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 02:13:50');
INSERT INTO `exam_record` VALUES (74, 19, 2, 'A,B,C', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 02:13:50');
INSERT INTO `exam_record` VALUES (75, 19, 3, '$\\mu\\approx\\alpha$', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 02:13:50');
INSERT INTO `exam_record` VALUES (76, 19, 4, '$\\theta\\div\\lambda$', 3, 0.00, NULL, 'AI批改失败：智能批改失败: I/O error on POST request for \"http://localhost:8082/ai/grade\": Connection refused: no further information', 1, 1, '2026-01-30 02:13:53', NULL, '2026-01-30 02:13:50');
INSERT INTO `exam_record` VALUES (77, 20, 1, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 17:57:53');
INSERT INTO `exam_record` VALUES (78, 20, 2, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 17:57:53');
INSERT INTO `exam_record` VALUES (79, 20, 3, '', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-30 17:57:53');
INSERT INTO `exam_record` VALUES (80, 20, 4, '', 3, 0.00, NULL, 'AI批改失败：智能批改失败: I/O error on POST request for \"http://localhost:8082/ai/grade\": Connection refused: no further information', 0, 0, '2026-01-30 17:57:55', NULL, '2026-01-30 17:57:53');
INSERT INTO `exam_record` VALUES (81, 21, 1, 'A', 1, 5.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-31 20:19:08');
INSERT INTO `exam_record` VALUES (82, 21, 2, 'A,D', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-31 20:19:08');
INSERT INTO `exam_record` VALUES (83, 21, 3, '$是啊方法\\beta_{df}$', 0, 0.00, NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-31 20:19:08');
INSERT INTO `exam_record` VALUES (84, 21, 4, '$1234$', 3, 0.00, NULL, 'AI批改失败：智能批改失败: I/O error on POST request for \"http://localhost:8082/ai/grade\": Connection refused: no further information', 1, 1, '2026-01-31 20:19:37', NULL, '2026-01-31 20:19:08');

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
-- Records of exam_session
-- ----------------------------
INSERT INTO `exam_session` VALUES (1, 1, 2, 1, '2026-01-27 21:43:50', '2026-01-27 21:51:56', '2026-01-28 00:43:50', 5.00, 4, '本次考试共 4 题，其中客观题 1 题。\n客观题正确数：1/1，正确率：100.0%\n总得分：5.0 分，平均每题得分：1.25 分\n答题时长：8 分钟，切换题目次数：4 次\n\nAI 批改反馈：\n', '{\"1\": \"A\", \"2\": \"\", \"3\": \"I dont know!!\", \"4\": \"I dont know!!\"}', '2026-01-27 21:43:50');
INSERT INTO `exam_session` VALUES (2, 1, 2, 1, '2026-01-27 21:55:59', '2026-01-27 21:57:01', '2026-01-28 00:55:59', 0.00, 1, '本次考试共 4 题，其中客观题 0 题。\n客观题正确数：0/0，正确率：0.0%\n总得分：0.0 分，平均每题得分：0.00 分\n答题时长：1 分钟，切换题目次数：1 次\n\nAI 批改反馈：\n', '{}', '2026-01-27 21:55:59');
INSERT INTO `exam_session` VALUES (3, 1, 2, 1, '2026-01-27 21:57:14', '2026-01-27 22:23:50', '2026-01-28 00:57:14', 5.00, 1, '本次考试共 4 题，其中客观题 1 题。\n客观题正确数：1/1，正确率：100.0%\n总得分：5.0 分，平均每题得分：1.25 分\n答题时长：26 分钟，切换题目次数：1 次\n\nAI 批改反馈：\n', '{\"1\": \"A\", \"2\": \"A,B\", \"3\": \"3124\", \"4\": \"1\"}', '2026-01-27 21:57:14');
INSERT INTO `exam_session` VALUES (4, 1, 2, 1, '2026-01-27 22:47:15', '2026-01-27 22:52:24', '2026-01-28 01:47:15', 5.00, 1, '本次考试共 4 题，其中客观题 1 题。\n客观题正确数：1/1，正确率：100.0%\n总得分：5.0 分，平均每题得分：1.25 分\n答题时长：5 分钟，切换题目次数：1 次\n\nAI 批改反馈：\n', '{\"1\": \"A\", \"2\": \"B,C\", \"3\": \"i\", \"4\": \"iuyh\"}', '2026-01-27 22:47:15');
INSERT INTO `exam_session` VALUES (5, 1, 2, 1, '2026-01-28 00:51:51', '2026-01-28 01:01:45', '2026-01-28 03:51:51', 5.00, 2, '本次考试共 4 题，其中客观题 1 题。\n客观题正确数：1/1，正确率：100.0%\n总得分：5.0 分，平均每题得分：1.25 分\n答题时长：9 分钟，切换题目次数：2 次\n\nAI 批改反馈：\n', '{\"1\": \"A\", \"2\": \"A\"}', '2026-01-28 00:51:51');
INSERT INTO `exam_session` VALUES (6, 1, 2, 1, '2026-01-28 01:03:22', '2026-01-28 01:08:37', '2026-01-28 04:03:22', 0.00, 1, '本次考试共 4 题，其中客观题 0 题。\n客观题正确数：0/0，正确率：0.0%\n总得分：0.0 分，平均每题得分：0.00 分\n答题时长：5 分钟，切换题目次数：1 次\n\nAI 批改反馈：\n', '{\"1\": \"B\", \"2\": \"A\", \"3\": \"$15^{3}$\", \"4\": \"$\\\\alpha+\\\\beta$\"}', '2026-01-28 01:03:22');
INSERT INTO `exam_session` VALUES (7, 1, 2, 1, '2026-01-28 01:11:24', '2026-01-28 01:14:29', '2026-01-28 04:11:24', 5.00, 0, '本次考试共 4 题，其中客观题 1 题。\n客观题正确数：1/1，正确率：100.0%\n总得分：5.0 分，平均每题得分：1.25 分\n答题时长：3 分钟，切换题目次数：0 次\n\nAI 批改反馈：\n', '{\"1\": \"A\", \"2\": \"C\", \"3\": \"\\\\theta\", \"4\": \"\\\\times\"}', '2026-01-28 01:11:24');
INSERT INTO `exam_session` VALUES (8, 1, 3, 1, '2026-01-30 00:28:06', '2026-01-30 00:28:31', '2026-01-30 03:28:06', 5.00, 0, '本次考试共 4 题，其中客观题 1 题。\n客观题正确数：1/1，正确率：100.0%\n总得分：5.0 分，平均每题得分：1.25 分\n答题时长：0 分钟，切换题目次数：0 次\n\nAI 批改反馈：\n', '{\"1\": \"A\", \"2\": \"A,B,C\", \"3\": \"12341234\", \"4\": \"432143214\"}', '2026-01-30 00:28:06');
INSERT INTO `exam_session` VALUES (9, 1, 3, 1, '2026-01-30 00:42:18', '2026-01-30 00:42:27', '2026-01-30 03:42:18', 5.00, 0, '本次考试共 4 题，其中客观题 1 题。\n客观题正确数：1/1，正确率：100.0%\n总得分：5.0 分，平均每题得分：1.25 分\n答题时长：0 分钟，切换题目次数：0 次\n\nAI 批改反馈：\n', '{\"1\": \"A\", \"2\": \"A\", \"3\": \"\\\\beta_{}\"}', '2026-01-30 00:42:18');
INSERT INTO `exam_session` VALUES (10, 1, 3, 1, '2026-01-30 01:14:50', '2026-01-30 01:14:58', '2026-01-30 04:14:50', 5.00, 0, '本次考试共 4 题，其中客观题 1 题。\n客观题正确数：1/1，正确率：100.0%\n总得分：5.0 分，平均每题得分：1.25 分\n答题时长：0 分钟，切换题目次数：0 次\n\nAI 批改反馈：\n', '{\"1\": \"A\", \"2\": \"D\", \"3\": \"\\\\sigma\", \"4\": \"\\\\lambda\"}', '2026-01-30 01:14:50');
INSERT INTO `exam_session` VALUES (11, 1, 2, 1, '2026-01-30 01:30:31', '2026-01-30 01:30:32', '2026-01-30 04:30:31', 0.00, 0, '本次考试共 4 题，其中客观题 0 题。\n客观题正确数：0/0，正确率：0.0%\n总得分：0.0 分，平均每题得分：0.00 分\n答题时长：0 分钟，切换题目次数：0 次\n\nAI 批改反馈：\n', '{}', '2026-01-30 01:30:31');
INSERT INTO `exam_session` VALUES (12, 1, 2, 1, '2026-01-30 01:39:41', '2026-01-30 01:42:52', '2026-01-30 04:39:41', 0.00, 2, '本次考试共 4 题，其中客观题 0 题。\n客观题正确数：0/0，正确率：0.0%\n总得分：0.0 分，平均每题得分：0.00 分\n答题时长：3 分钟，切换题目次数：2 次\n\n建议：\n', '{\"3\": \"\\\\alpha_{}\\\\in_{}\\\\times\\\\lim_{x \\\\to \\\\infty}\\\\neq\\\\pm_{}\\\\lambda\\\\beta\\\\alpha\\\\infty\\\\pi\", \"4\": \"_{}\\\\alpha\\\\theta\"}', '2026-01-30 01:39:41');
INSERT INTO `exam_session` VALUES (13, 1, 2, 1, '2026-01-30 01:46:49', '2026-01-30 01:48:22', '2026-01-30 04:46:49', 0.00, 0, '本次考试共 4 题，其中客观题 0 题。\n客观题正确数：0/0，正确率：0.0%\n总得分：0.0 分，平均每题得分：0.00 分\n答题时长：1 分钟，切换题目次数：0 次\n\n建议：\n', '{}', '2026-01-30 01:46:49');
INSERT INTO `exam_session` VALUES (14, 1, 2, 1, '2026-01-30 01:48:27', '2026-01-30 01:48:29', '2026-01-30 04:48:27', 0.00, 0, '本次考试共 4 题，其中客观题 0 题。\n客观题正确数：0/0，正确率：0.0%\n总得分：0.0 分，平均每题得分：0.00 分\n答题时长：0 分钟，切换题目次数：0 次\n\n建议：\n', '{}', '2026-01-30 01:48:27');
INSERT INTO `exam_session` VALUES (15, 1, 2, 1, '2026-01-30 01:57:02', '2026-01-30 01:57:14', '2026-01-30 04:57:02', 5.00, 0, '本次考试共 4 题，其中客观题 1 题。\n客观题正确数：1/1，正确率：100.0%\n总得分：5.0 分，平均每题得分：1.25 分\n答题时长：0 分钟，切换题目次数：0 次\n\n建议：\n', '{\"1\": \"A\", \"2\": \"A,B,C\"}', '2026-01-30 01:57:02');
INSERT INTO `exam_session` VALUES (16, 1, 2, 1, '2026-01-30 01:57:51', '2026-01-30 02:03:01', '2026-01-30 04:57:51', 0.00, 0, '本次考试共 4 题，其中客观题 0 题。\n客观题正确数：0/0，正确率：0.0%\n总得分：0.0 分，平均每题得分：0.00 分\n答题时长：5 分钟，切换题目次数：0 次\n\n建议：\n', '{\"1\": \"C\", \"2\": \"A,D\"}', '2026-01-30 01:57:51');
INSERT INTO `exam_session` VALUES (17, 1, 5, 1, '2026-01-30 02:05:14', '2026-01-30 02:05:17', '2026-01-30 05:05:14', 0.00, 0, '本次考试共 4 题，其中客观题 0 题。\n客观题正确数：0/0，正确率：0.0%\n总得分：0.0 分，平均每题得分：0.00 分\n答题时长：0 分钟，切换题目次数：0 次\n\n建议：\n', '{}', '2026-01-30 02:05:14');
INSERT INTO `exam_session` VALUES (18, 1, 5, 1, '2026-01-30 02:05:31', '2026-01-30 02:05:36', '2026-01-30 05:05:31', 0.00, 0, '本次考试共 4 题，其中客观题 0 题。\n客观题正确数：0/0，正确率：0.0%\n总得分：0.0 分，平均每题得分：0.00 分\n答题时长：0 分钟，切换题目次数：0 次\n\n建议：\n', '{}', '2026-01-30 02:05:31');
INSERT INTO `exam_session` VALUES (19, 1, 2, 1, '2026-01-30 02:13:16', '2026-01-30 02:13:50', '2026-01-30 05:13:16', 5.00, 0, '本次考试共 4 题，其中客观题 1 题。\n客观题正确数：1/1，正确率：100.0%\n总得分：5.0 分，平均每题得分：1.25 分\n答题时长：0 分钟，切换题目次数：0 次\n\n建议：\n', '{\"1\": \"A\", \"2\": \"A,B,C\", \"3\": \"$\\\\mu\\\\approx\\\\alpha$\", \"4\": \"$\\\\theta\\\\div\\\\lambda$\"}', '2026-01-30 02:13:16');
INSERT INTO `exam_session` VALUES (20, 1, 2, 1, '2026-01-30 17:57:50', '2026-01-30 17:57:53', '2026-01-30 20:57:50', 0.00, 0, '本次考试共 4 题，其中客观题 0 题。\n客观题正确数：0/0，正确率：0.0%\n总得分：0.0 分，平均每题得分：0.00 分\n答题时长：0 分钟，切换题目次数：0 次\n\n建议：\n', '{}', '2026-01-30 17:57:50');
INSERT INTO `exam_session` VALUES (21, 1, 2, 1, '2026-01-31 20:17:59', '2026-01-31 20:19:08', '2026-01-31 23:17:59', 5.00, 0, '本次考试共 4 题，其中客观题 1 题。\n客观题正确数：1/1，正确率：100.0%\n总得分：5.0 分，平均每题得分：1.25 分\n答题时长：1 分钟，切换题目次数：0 次\n\n建议：\n', '{\"1\": \"A\", \"2\": \"A,D\", \"3\": \"$是啊方法\\\\beta_{df}$\", \"4\": \"$1234$\"}', '2026-01-31 20:17:59');

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
-- Records of exercise_book
-- ----------------------------
INSERT INTO `exercise_book` VALUES (1, '张宇高等数学18讲', '考研数学高等数学辅导书，是考研数学二教材和教辅的重要参考资料。', '2026-01-26 00:40:14');
INSERT INTO `exercise_book` VALUES (2, '张宇线性代数9讲', '考研数学线性代数辅导书，配合张宇高等数学18讲使用，是数学二复习的重要教辅材料。', '2026-01-26 00:40:55');
INSERT INTO `exercise_book` VALUES (3, '张宇考研数学题源探析经典1000题', '考研数学经典习题集，学长学姐倾情推荐的考研数学辅导书之一，是考研数学必备练习资料。', '2026-01-26 00:41:12');
INSERT INTO `exercise_book` VALUES (4, '李永乐武忠祥考研数学复习全书基础篇', '2024考研数学推荐教辅资料，包含数学知识系统复习内容，是考研数学复习的重要参考书。', '2026-01-26 00:41:23');
INSERT INTO `exercise_book` VALUES (5, '肖秀荣知识点精讲精练', '覆盖大纲所有考点，配套肖秀荣1000题使用效果最佳，是考研政治复习的经典教材。', '2026-01-26 00:41:48');
INSERT INTO `exercise_book` VALUES (6, '肖秀荣1000题', '肖秀荣老师经典教材组合的重要组成部分，是考研政治刷题的经典习题集。', '2026-01-26 00:41:59');
INSERT INTO `exercise_book` VALUES (7, '考研真相', '考研英语经典教辅资料，是考研英语辅导书中的重要参考资料。', '2026-01-26 00:42:11');
INSERT INTO `exercise_book` VALUES (8, '王道数据结构', '408考研经典教辅，王道系列是目前大家普遍使用的辅导书，数据结构科目推荐使用王道教材。', '2026-01-26 00:42:26');
INSERT INTO `exercise_book` VALUES (9, '王道操作系统', '408考研专业课教材，王道考研操作系统复习指导是考研408教材的重要组成部分。', '2026-01-26 00:42:40');

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
-- Records of favorite_folder
-- ----------------------------

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
-- Records of question
-- ----------------------------
INSERT INTO `question` VALUES (1, 1, 3, '{\"tags\": [\"导数\", \"基础求导\"], \"answer\": \"A\", \"content\": \"设函数 $f(x) = x^3 - 3x + 1$，求 $f\'(x)$\", \"options\": [{\"text\": \"$3x^2 - 3$\", \"label\": \"A\"}, {\"text\": \"$3x^2 + 3$\", \"label\": \"B\"}, {\"text\": \"$x^2 - 3$\", \"label\": \"C\"}, {\"text\": \"$x^2 + 3$\", \"label\": \"D\"}], \"analysis\": \"根据求导公式 $(x^n)\' = nx^{n-1}$，得到 $f\'(x) = 3x^2 - 3$\"}', '2026-01-27 00:35:16');
INSERT INTO `question` VALUES (2, 2, 3, '{\"tags\": [\"线性代数\", \"矩阵性质\"], \"answer\": \"ACD\", \"source\": \"线性代数经典例题\", \"content\": \"设 $A$ 为 $n$ 阶方阵，下列命题中正确的是\", \"options\": [{\"text\": \"若 $A$ 可逆，则 $A^T$ 也可逆\", \"label\": \"A\"}, {\"text\": \"若 $A$ 为对称矩阵，则 $A$ 一定可对角化\", \"label\": \"B\"}, {\"text\": \"$|A| = 0$ 是 $A$ 不可逆的充要条件\", \"label\": \"C\"}, {\"text\": \"若 $A$ 有 $n$ 个不同的特征值，则 $A$ 一定可对角化\", \"label\": \"D\"}], \"analysis\": \"A正确：可逆矩阵的转置仍可逆；B错误：对称矩阵不一定可对角化，需要满足特定条件；C正确：行列式为零是不可逆的充要条件；D正确：有n个不同特征值的矩阵一定可对角化\"}', '2026-01-27 00:35:16');
INSERT INTO `question` VALUES (3, 3, 3, '{\"tags\": [\"概率论\", \"正态分布\"], \"answer\": \"0.6826\", \"source\": \"概率论与数理统计例题\", \"content\": \"设随机变量 $X \\\\sim N(0,1)$，则 $P(|X| < 1) =$ _____\", \"analysis\": \"标准正态分布中，$P(|X| < 1) = 2\\\\Phi(1) - 1 = 2 \\\\times 0.8413 - 1 = 0.6826$，其中 $\\\\Phi(1)$ 为标准正态分布函数值\"}', '2026-01-27 00:35:16');
INSERT INTO `question` VALUES (4, 4, 3, '{\"tags\": [\"高等数学\", \"函数极值\"], \"answer\": \"最大值为2，最小值为-2\", \"source\": \"高等数学考研真题\", \"content\": \"求函数 $f(x) = x^3 - 3x$ 在区间 $[-2, 2]$ 上的最大值和最小值\", \"analysis\": \"首先求导：$f\'(x) = 3x^2 - 3 = 3(x^2 - 1)$，令 $f\'(x) = 0$，得临界点 $x = \\\\pm 1$。计算端点值：$f(-2) = -2$，$f(2) = 2$；计算临界点值：$f(-1) = 2$，$f(1) = -2$。比较得最大值为2，最小值为-2。\"}', '2026-01-27 00:35:16');
INSERT INTO `question` VALUES (5, 1, 3, '{\"tags\": [\"数据结构\", \"栈\"], \"answer\": \"A\", \"source\": \"408数据结构真题\", \"content\": \"在数据结构中，栈的插入和删除操作是在哪里进行的？\", \"options\": [{\"text\": \"栈顶\", \"label\": \"A\"}, {\"text\": \"栈底\", \"label\": \"B\"}, {\"text\": \"任意位置\", \"label\": \"C\"}, {\"text\": \"中间位置\", \"label\": \"D\"}], \"analysis\": \"栈是后进先出（LIFO）的线性表，所有插入（push）和删除（pop）操作都只能在栈顶进行\"}', '2026-01-27 00:36:17');
INSERT INTO `question` VALUES (6, 2, 3, '{\"tags\": [\"操作系统\", \"进程线程\"], \"answer\": \"ABCD\", \"source\": \"408操作系统真题\", \"content\": \"下列关于进程和线程的说法中，正确的是\", \"options\": [{\"text\": \"进程是资源分配的基本单位，线程是CPU调度的基本单位\", \"label\": \"A\"}, {\"text\": \"一个进程可以包含多个线程\", \"label\": \"B\"}, {\"text\": \"线程之间共享进程的地址空间\", \"label\": \"C\"}, {\"text\": \"进程切换的开销比线程切换的开销大\", \"label\": \"D\"}], \"analysis\": \"A正确：进程负责资源分配，线程负责CPU调度；B正确：一个进程可以创建多个线程；C正确：同一进程内的线程共享地址空间；D正确：进程切换需要切换地址空间，开销更大\"}', '2026-01-27 00:36:17');
INSERT INTO `question` VALUES (7, 3, 3, '{\"tags\": [\"计算机网络\", \"TCP/IP\"], \"answer\": \"80\", \"source\": \"408计算机网络真题\", \"content\": \"在TCP/IP协议中，HTTP协议默认使用的端口号是 _____\", \"analysis\": \"HTTP（超文本传输协议）默认使用80端口，HTTPS使用443端口，这是网络协议的基础知识\"}', '2026-01-27 00:36:17');
INSERT INTO `question` VALUES (8, 4, 3, '{\"tags\": [\"计算机组成原理\", \"体系结构\"], \"answer\": \"1. 采用二进制表示数据和指令；2. 程序存储，即指令和数据存储在同一存储器中；3. 由运算器、控制器、存储器、输入设备和输出设备五大部件组成；4. 指令由操作码和地址码组成；5. 采用存储程序控制方式\", \"source\": \"408计算机组成原理真题\", \"content\": \"简述冯·诺依曼体系结构的主要特点\", \"analysis\": \"冯·诺依曼体系结构是现代计算机的基础架构，其核心特点是程序存储和二进制表示，这使得计算机能够自动执行程序\"}', '2026-01-27 00:36:17');
INSERT INTO `question` VALUES (9, 1, 3, '{\"tags\": [\"马克思主义基本原理\", \"唯物主义\"], \"answer\": \"B\", \"source\": \"政治理论考研真题\", \"content\": \"马克思主义哲学认为，世界的本原是\", \"options\": [{\"text\": \"精神\", \"label\": \"A\"}, {\"text\": \"物质\", \"label\": \"B\"}, {\"text\": \"意识\", \"label\": \"C\"}, {\"text\": \"上帝\", \"label\": \"D\"}], \"analysis\": \"马克思主义哲学坚持唯物主义立场，认为物质是世界的本原，意识是物质世界长期发展的产物\"}', '2026-01-27 00:36:51');
INSERT INTO `question` VALUES (10, 2, 3, '{\"tags\": [\"毛泽东思想\", \"活的灵魂\"], \"answer\": \"ABC\", \"source\": \"政治理论核心考点\", \"content\": \"毛泽东思想活的灵魂包括\", \"options\": [{\"text\": \"实事求是\", \"label\": \"A\"}, {\"text\": \"群众路线\", \"label\": \"B\"}, {\"text\": \"独立自主\", \"label\": \"C\"}, {\"text\": \"武装斗争\", \"label\": \"D\"}], \"analysis\": \"毛泽东思想活的灵魂是贯穿其中的立场、观点、方法，它们有三个基本方面，即实事求是、群众路线、独立自主。武装斗争是毛泽东思想的重要内容，但不是活的灵魂\"}', '2026-01-27 00:36:51');
INSERT INTO `question` VALUES (11, 3, 3, '{\"tags\": [\"中国特色社会主义理论\", \"党的领导\"], \"answer\": \"中国共产党领导\", \"source\": \"时政热点考点\", \"content\": \"中国特色社会主义最本质的特征是 _____\", \"analysis\": \"党的十九大报告明确指出：\'中国特色社会主义最本质的特征是中国共产党领导，中国特色社会主义制度的最大优势是中国共产党领导\'\"}', '2026-01-27 00:36:51');
INSERT INTO `question` VALUES (12, 4, 3, '{\"tags\": [\"中国特色社会主义理论\", \"发展战略\"], \"answer\": \"新发展阶段明确了我国发展的历史方位，新发展理念明确了我国现代化建设的指导原则，新发展格局明确了我国经济现代化的路径选择。三者相互联系、有机统一：新发展阶段是贯彻新发展理念、构建新发展格局的现实依据；新发展理念是引领新发展阶段、构建新发展格局的思想指引；新发展格局是应对新发展阶段机遇挑战、贯彻新发展理念的战略选择\", \"source\": \"政治理论热点分析\", \"content\": \"论述新发展阶段、新发展理念、新发展格局三者之间的关系\", \"analysis\": \"本题考查对\'三新\'重大判断的理解，这是当前政治理论的重点内容，体现了对国家发展方向的深刻把握\"}', '2026-01-27 00:36:51');
INSERT INTO `question` VALUES (13, 1, 3, '{\"tags\": [\"词汇辨析\", \"完形填空\"], \"answer\": \"A\", \"source\": \"考研英语词汇真题\", \"content\": \"The ______ of the new policy was met with widespread criticism from various sectors of society.\", \"options\": [{\"text\": \"implementation\", \"label\": \"A\"}, {\"text\": \"implication\", \"label\": \"B\"}, {\"text\": \"illumination\", \"label\": \"C\"}, {\"text\": \"illustration\", \"label\": \"D\"}], \"analysis\": \"\'Implementation\' 意为\'实施、执行\'，符合句意\'新政策的实施遭到了社会各界的广泛批评\'。\'Implication\' 意为\'暗示、含义\'；\'Illumination\' 意为\'照明、阐明\'；\'Illustration\' 意为\'插图、说明\'，均不符合语境\"}', '2026-01-27 00:37:31');
INSERT INTO `question` VALUES (14, 2, 3, '{\"tags\": [\"阅读技巧\", \"学习策略\"], \"answer\": \"ACD\", \"content\": \"Which of the following are effective strategies for improving reading comprehension in English?\", \"options\": [{\"text\": \"Skimming the text first to get the main idea\", \"label\": \"A\"}, {\"text\": \"Looking up every unfamiliar word in the dictionary\", \"label\": \"B\"}, {\"text\": \"Making predictions about the content before reading\", \"label\": \"C\"}, {\"text\": \"Summarizing each paragraph after reading it\", \"label\": \"D\"}], \"analysis\": \"A正确：先略读获取主旨是有效策略；B错误：查每个生词会影响阅读流畅性，应该根据上下文猜测词义；C正确：阅读前预测内容有助于提高理解；D正确：段落后总结有助于巩固理解\"}', '2026-01-27 00:37:31');
INSERT INTO `question` VALUES (15, 7, 3, '{\"tags\": [\"词汇运用\", \"语法填空\"], \"answer\": \"development\", \"content\": \"The rapid ______ of technology has transformed the way we communicate and work.\", \"options\": [{\"text\": \"\", \"label\": \"A\"}, {\"text\": \"\", \"label\": \"B\"}, {\"text\": \"\", \"label\": \"C\"}, {\"text\": \"\", \"label\": \"D\"}], \"analysis\": \"根据句意\'技术的快速发展改变了我们沟通和工作的方式\'，此处需要名词形式，\'development\' 符合语法和语义要求。其他可能的词如\'advancement\'、\'progress\' 也可，但\'development\' 最常用\"}', '2026-01-27 00:37:31');
INSERT INTO `question` VALUES (16, 6, 3, '{\"tags\": [\"翻译技巧\", \"中译英\"], \"answer\": \"With the development of economic globalization, the interdependence among countries is deepening day by day.\", \"content\": \"Translate the following Chinese sentence into English: \'随着经济全球化的发展，各国之间的相互依存日益加深。\'\", \"options\": [{\"text\": \"\", \"label\": \"A\"}, {\"text\": \"\", \"label\": \"B\"}, {\"text\": \"\", \"label\": \"C\"}, {\"text\": \"\", \"label\": \"D\"}], \"analysis\": \"翻译要点：1. \'随着...的发展\' 译为 \'With the development of\'；2. \'经济全球化\' 译为 \'economic globalization\'；3. \'相互依存\' 译为 \'interdependence\'；4. \'日益加深\' 译为 \'is deepening day by day\' 或 \'has been deepening increasingly\'。整体结构要符合英语表达习惯\"}', '2026-01-27 00:37:31');

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
-- Records of question_book_rel
-- ----------------------------
INSERT INTO `question_book_rel` VALUES (24, 1, 1);
INSERT INTO `question_book_rel` VALUES (2, 2, 1);
INSERT INTO `question_book_rel` VALUES (3, 3, 1);
INSERT INTO `question_book_rel` VALUES (4, 4, 1);
INSERT INTO `question_book_rel` VALUES (5, 5, 8);
INSERT INTO `question_book_rel` VALUES (6, 6, 8);
INSERT INTO `question_book_rel` VALUES (7, 7, 8);
INSERT INTO `question_book_rel` VALUES (8, 8, 8);
INSERT INTO `question_book_rel` VALUES (9, 9, 6);
INSERT INTO `question_book_rel` VALUES (10, 10, 6);
INSERT INTO `question_book_rel` VALUES (11, 11, 6);
INSERT INTO `question_book_rel` VALUES (12, 12, 6);
INSERT INTO `question_book_rel` VALUES (13, 13, 7);
INSERT INTO `question_book_rel` VALUES (19, 14, 7);
INSERT INTO `question_book_rel` VALUES (23, 15, 7);
INSERT INTO `question_book_rel` VALUES (22, 16, 7);

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
-- Records of question_paper_rel
-- ----------------------------
INSERT INTO `question_paper_rel` VALUES (1, 1, 1, 1, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (2, 1, 2, 2, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (3, 1, 3, 3, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (4, 1, 4, 4, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (5, 2, 4, 4, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (6, 2, 3, 3, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (7, 2, 1, 1, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (8, 2, 2, 2, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (9, 3, 4, 4, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (10, 3, 3, 3, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (11, 3, 2, 2, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (12, 3, 1, 1, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (13, 4, 8, 4, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (14, 4, 7, 3, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (15, 4, 6, 2, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (16, 4, 5, 1, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (17, 5, 12, 4, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (18, 5, 11, 3, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (19, 5, 10, 2, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (20, 5, 9, 1, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (21, 6, 16, 1, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (22, 6, 15, 2, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (23, 6, 14, 3, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (24, 6, 13, 4, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (25, 6, 12, 16, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (26, 6, 11, 5, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (27, 6, 10, 6, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (28, 6, 9, 7, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (29, 6, 8, 15, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (30, 6, 7, 9, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (31, 6, 6, 11, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (32, 6, 5, 13, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (33, 6, 4, 14, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (34, 6, 3, 12, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (35, 6, 2, 8, 5.00, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `question_paper_rel` VALUES (36, 6, 1, 10, 5.00, NULL, NULL, NULL, NULL, NULL);

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
-- Records of question_subject_rel
-- ----------------------------
INSERT INTO `question_subject_rel` VALUES (145, 1, 26);
INSERT INTO `question_subject_rel` VALUES (6, 2, 4);
INSERT INTO `question_subject_rel` VALUES (7, 2, 5);
INSERT INTO `question_subject_rel` VALUES (8, 2, 6);
INSERT INTO `question_subject_rel` VALUES (5, 2, 19);
INSERT INTO `question_subject_rel` VALUES (10, 3, 4);
INSERT INTO `question_subject_rel` VALUES (11, 3, 5);
INSERT INTO `question_subject_rel` VALUES (12, 3, 6);
INSERT INTO `question_subject_rel` VALUES (9, 3, 19);
INSERT INTO `question_subject_rel` VALUES (14, 4, 4);
INSERT INTO `question_subject_rel` VALUES (15, 4, 5);
INSERT INTO `question_subject_rel` VALUES (16, 4, 6);
INSERT INTO `question_subject_rel` VALUES (13, 4, 19);
INSERT INTO `question_subject_rel` VALUES (17, 5, 22);
INSERT INTO `question_subject_rel` VALUES (18, 5, 23);
INSERT INTO `question_subject_rel` VALUES (19, 5, 24);
INSERT INTO `question_subject_rel` VALUES (20, 5, 25);
INSERT INTO `question_subject_rel` VALUES (21, 6, 22);
INSERT INTO `question_subject_rel` VALUES (22, 6, 23);
INSERT INTO `question_subject_rel` VALUES (23, 6, 24);
INSERT INTO `question_subject_rel` VALUES (24, 6, 25);
INSERT INTO `question_subject_rel` VALUES (25, 7, 22);
INSERT INTO `question_subject_rel` VALUES (26, 7, 23);
INSERT INTO `question_subject_rel` VALUES (27, 7, 24);
INSERT INTO `question_subject_rel` VALUES (28, 7, 25);
INSERT INTO `question_subject_rel` VALUES (29, 8, 22);
INSERT INTO `question_subject_rel` VALUES (30, 8, 23);
INSERT INTO `question_subject_rel` VALUES (31, 8, 24);
INSERT INTO `question_subject_rel` VALUES (32, 8, 25);
INSERT INTO `question_subject_rel` VALUES (38, 9, 1);
INSERT INTO `question_subject_rel` VALUES (33, 9, 8);
INSERT INTO `question_subject_rel` VALUES (34, 9, 9);
INSERT INTO `question_subject_rel` VALUES (35, 9, 10);
INSERT INTO `question_subject_rel` VALUES (36, 9, 11);
INSERT INTO `question_subject_rel` VALUES (37, 9, 12);
INSERT INTO `question_subject_rel` VALUES (44, 10, 1);
INSERT INTO `question_subject_rel` VALUES (39, 10, 8);
INSERT INTO `question_subject_rel` VALUES (40, 10, 9);
INSERT INTO `question_subject_rel` VALUES (41, 10, 10);
INSERT INTO `question_subject_rel` VALUES (42, 10, 11);
INSERT INTO `question_subject_rel` VALUES (43, 10, 12);
INSERT INTO `question_subject_rel` VALUES (50, 11, 1);
INSERT INTO `question_subject_rel` VALUES (45, 11, 8);
INSERT INTO `question_subject_rel` VALUES (46, 11, 9);
INSERT INTO `question_subject_rel` VALUES (47, 11, 10);
INSERT INTO `question_subject_rel` VALUES (48, 11, 11);
INSERT INTO `question_subject_rel` VALUES (49, 11, 12);
INSERT INTO `question_subject_rel` VALUES (56, 12, 1);
INSERT INTO `question_subject_rel` VALUES (51, 12, 8);
INSERT INTO `question_subject_rel` VALUES (52, 12, 9);
INSERT INTO `question_subject_rel` VALUES (53, 12, 10);
INSERT INTO `question_subject_rel` VALUES (54, 12, 11);
INSERT INTO `question_subject_rel` VALUES (55, 12, 12);
INSERT INTO `question_subject_rel` VALUES (57, 13, 2);
INSERT INTO `question_subject_rel` VALUES (58, 13, 3);
INSERT INTO `question_subject_rel` VALUES (59, 13, 13);
INSERT INTO `question_subject_rel` VALUES (60, 13, 14);
INSERT INTO `question_subject_rel` VALUES (61, 13, 15);
INSERT INTO `question_subject_rel` VALUES (62, 13, 16);
INSERT INTO `question_subject_rel` VALUES (63, 13, 17);
INSERT INTO `question_subject_rel` VALUES (64, 13, 18);
INSERT INTO `question_subject_rel` VALUES (105, 14, 2);
INSERT INTO `question_subject_rel` VALUES (106, 14, 3);
INSERT INTO `question_subject_rel` VALUES (107, 14, 13);
INSERT INTO `question_subject_rel` VALUES (108, 14, 14);
INSERT INTO `question_subject_rel` VALUES (109, 14, 15);
INSERT INTO `question_subject_rel` VALUES (110, 14, 16);
INSERT INTO `question_subject_rel` VALUES (111, 14, 17);
INSERT INTO `question_subject_rel` VALUES (112, 14, 18);
INSERT INTO `question_subject_rel` VALUES (137, 15, 2);
INSERT INTO `question_subject_rel` VALUES (138, 15, 3);
INSERT INTO `question_subject_rel` VALUES (139, 15, 13);
INSERT INTO `question_subject_rel` VALUES (140, 15, 14);
INSERT INTO `question_subject_rel` VALUES (141, 15, 15);
INSERT INTO `question_subject_rel` VALUES (142, 15, 16);
INSERT INTO `question_subject_rel` VALUES (143, 15, 17);
INSERT INTO `question_subject_rel` VALUES (144, 15, 18);
INSERT INTO `question_subject_rel` VALUES (129, 16, 2);
INSERT INTO `question_subject_rel` VALUES (130, 16, 3);
INSERT INTO `question_subject_rel` VALUES (131, 16, 13);
INSERT INTO `question_subject_rel` VALUES (132, 16, 14);
INSERT INTO `question_subject_rel` VALUES (133, 16, 15);
INSERT INTO `question_subject_rel` VALUES (134, 16, 16);
INSERT INTO `question_subject_rel` VALUES (135, 16, 17);
INSERT INTO `question_subject_rel` VALUES (136, 16, 18);

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
-- Records of resource
-- ----------------------------

-- ----------------------------
-- Table structure for subject
-- ----------------------------
DROP TABLE IF EXISTS `subject`;
CREATE TABLE `subject`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '科目名称',
  `parent_id` int NULL DEFAULT 0 COMMENT '父级ID（顶级为0）',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `sort` tinyint NULL DEFAULT 0 COMMENT '排序号',
  `level` tinyint NULL DEFAULT 1 COMMENT '层级: \r\nL1-考试大类（专业课/公共课）；\r\nL2-考试规格（数学一，英语一）；\r\nL3-具体学科（高数，线代）；\r\nL4-章节与知识点（函数与极限、二重积分）；\r\nL5-题型 / 解题技巧（泰勒公式）',
  `question_count` int NULL DEFAULT 0 COMMENT '该分类下的题目总数',
  `scope` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1,2,3' COMMENT '适用大纲: 1-数一, 2-数二, 3-数三 (逗号隔开)',
  `question_types` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支持的题型列表，逗号分隔，如: 1,2,4',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 62 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '科目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of subject
-- ----------------------------
INSERT INTO `subject` VALUES (1, '政治', 0, NULL, 10, 1, 0, NULL, '1,2,4');
INSERT INTO `subject` VALUES (2, '英语一', 0, NULL, 20, 1, 0, NULL, '5,6,7,8,9,10');
INSERT INTO `subject` VALUES (3, '英语二', 0, NULL, 30, 1, 0, NULL, '5,6,7,8,9,10');
INSERT INTO `subject` VALUES (4, '数学一', 0, NULL, 40, 1, 0, NULL, '1,3,4');
INSERT INTO `subject` VALUES (5, '数学二', 0, NULL, 50, 1, 0, NULL, '1,3,4');
INSERT INTO `subject` VALUES (6, '数学三', 0, NULL, 60, 1, 0, NULL, '1,3,4');
INSERT INTO `subject` VALUES (7, '408', 0, NULL, 70, 1, 0, NULL, '1,4');
INSERT INTO `subject` VALUES (8, '马原', 1, NULL, 0, 2, 0, NULL, NULL);
INSERT INTO `subject` VALUES (9, '毛中特', 1, NULL, 20, 2, 0, '', NULL);
INSERT INTO `subject` VALUES (10, '史纲', 1, NULL, 30, 2, 0, NULL, NULL);
INSERT INTO `subject` VALUES (11, '思修法基', 1, NULL, 0, 2, 0, NULL, NULL);
INSERT INTO `subject` VALUES (12, '时政', 1, NULL, 50, 2, 0, NULL, NULL);
INSERT INTO `subject` VALUES (13, '完形填空', 0, NULL, 10, 2, 0, '2,3', NULL);
INSERT INTO `subject` VALUES (14, '阅读理解', 0, NULL, 20, 2, 0, '2,3', NULL);
INSERT INTO `subject` VALUES (15, '新题型', 0, NULL, 30, 2, 0, '2,3', NULL);
INSERT INTO `subject` VALUES (16, '翻译', 0, NULL, 40, 2, 0, '2,3', NULL);
INSERT INTO `subject` VALUES (17, '小作文', 0, NULL, 50, 2, 0, '2,3', NULL);
INSERT INTO `subject` VALUES (18, '大作文', 0, NULL, 60, 2, 0, '2,3', NULL);
INSERT INTO `subject` VALUES (19, '高等数学', 0, NULL, 10, 2, 0, '4,5,6', NULL);
INSERT INTO `subject` VALUES (20, '线性代数', 0, NULL, 20, 2, 0, '4,5,6', NULL);
INSERT INTO `subject` VALUES (21, '概率论与数理统计', 0, NULL, 30, 2, 0, '4,6', NULL);
INSERT INTO `subject` VALUES (22, '数据结构', 7, NULL, 10, 2, 0, NULL, NULL);
INSERT INTO `subject` VALUES (23, '计算机组成原理', 7, NULL, 20, 2, 0, NULL, NULL);
INSERT INTO `subject` VALUES (24, '操作系统', 7, NULL, 30, 2, 0, NULL, NULL);
INSERT INTO `subject` VALUES (25, '计算机网络', 7, NULL, 40, 2, 0, NULL, NULL);
INSERT INTO `subject` VALUES (26, '函数、极限、连续', 19, NULL, 10, 3, 0, '4,5,6', NULL);
INSERT INTO `subject` VALUES (27, '一元微分学', 19, NULL, 20, 3, 0, '4,5,6', NULL);
INSERT INTO `subject` VALUES (28, '一元积分学', 19, NULL, 30, 3, 0, '4,5,6', NULL);
INSERT INTO `subject` VALUES (29, '反常积分', 19, NULL, 40, 3, 0, '4,5,6', NULL);
INSERT INTO `subject` VALUES (30, '微分方程', 19, NULL, 50, 3, 0, '4,5,6', NULL);
INSERT INTO `subject` VALUES (31, '多元微分学', 19, NULL, 60, 3, 0, '4,5,6', NULL);
INSERT INTO `subject` VALUES (32, '二重积分', 19, NULL, 70, 3, 0, '4,5,6', NULL);
INSERT INTO `subject` VALUES (33, '无穷级数', 19, NULL, 70, 3, 0, '4,6', NULL);
INSERT INTO `subject` VALUES (34, '行列式', 20, NULL, 10, 3, 0, '4,5,6', NULL);
INSERT INTO `subject` VALUES (35, '矩阵', 20, NULL, 20, 3, 0, '4,5,6', NULL);
INSERT INTO `subject` VALUES (36, '向量', 20, NULL, 30, 3, 0, '4,5,6', NULL);
INSERT INTO `subject` VALUES (37, '线性方程组', 20, NULL, 40, 3, 0, '4,5,6', NULL);
INSERT INTO `subject` VALUES (38, '矩阵的特征值和特征向量', 20, NULL, 50, 3, 0, '4,5,6', NULL);
INSERT INTO `subject` VALUES (39, '二次型', 20, NULL, 60, 3, 0, '4,5,6', NULL);
INSERT INTO `subject` VALUES (40, '随机事件和概率', 21, NULL, 10, 3, 0, '4,6', NULL);
INSERT INTO `subject` VALUES (41, '一维随机变量及其分布', 21, NULL, 20, 3, 0, '4,6', NULL);
INSERT INTO `subject` VALUES (42, '多维随机变量及其分布', 21, NULL, 30, 3, 0, '4,6', NULL);
INSERT INTO `subject` VALUES (43, '随机变量的数字特征', 21, NULL, 40, 3, 0, '4,6', NULL);
INSERT INTO `subject` VALUES (44, '大数定律和中心极限定理', 21, NULL, 50, 3, 0, '4,6', NULL);
INSERT INTO `subject` VALUES (45, '数理统计的基本概念', 21, NULL, 60, 3, 0, '4,6', NULL);
INSERT INTO `subject` VALUES (46, '参数估计与假设检验', 21, NULL, 70, 3, 0, '4', NULL);
INSERT INTO `subject` VALUES (47, '数列敛散性的判定', 26, NULL, 10, 4, 0, '4,6', NULL);
INSERT INTO `subject` VALUES (48, '函数极限的计算', 26, NULL, 20, 4, 0, '4,6', NULL);
INSERT INTO `subject` VALUES (49, '无穷小量', 26, NULL, 30, 4, 0, '4,6', NULL);
INSERT INTO `subject` VALUES (50, '确定极限中的参数', 26, NULL, 40, 4, 0, '4,6', NULL);
INSERT INTO `subject` VALUES (51, '函数的连续性与间断点的类型', 26, NULL, 50, 4, 0, '4,6', NULL);

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
-- Records of subject_weight_rel
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '$2a$10$PPkzKxxzZ0McBo/AdjozRuEcIgrvD0QCvau3kgxnw6egwTEPVZIii', '13345932175', 'admin@example.com', '管理员', 'admin', 'http://localhost:8081/uploads/87577d93-42ee-4c6f-82d6-4d1a10596459.jpg', '背景', 423, '27考研', '政治,英语一,数学一', '不要放弃自己的梦想！', '2025-12-30 17:15:11', '2026-01-31 20:08:18');
INSERT INTO `user` VALUES (2, '1123', '$2a$10$e0fDspBIiWCtLVyxwUeDL.FK2P4JKgmmZtzOAiX1pvYXab2scl30C', '13329333794', '1123@example.com', '1123', 'student', 'http://localhost:8081/uploads/d1bd0b2c-8a10-47bb-b072-a868e985138f.jpg', '山东大学', 360, '27考研', '政治,英语二,数学二', NULL, '2026-01-01 00:58:22', '2026-01-06 22:34:50');
INSERT INTO `user` VALUES (3, 'admin121e', '$2a$10$yOUbU7h.XqjLGulAZABdae.p3/KwPH5o3KKH23nbKM9BP.8LHlCsa', '13508872333', 'admin121e@example.com', '贝利亚', 'student', 'http://localhost:8081/uploads/d1bd0b2c-8a10-47bb-b072-a868e985138f.jpg', '东北大学', 360, '27考研', '政治,英语二,数学二', NULL, '2026-01-01 01:18:43', '2026-01-06 22:34:52');
INSERT INTO `user` VALUES (4, '123123', '$2a$10$Cqmo1yeF6tojwm9/ktr.KuDmgtwa6OQ/y9qw3afN5rlUCPeXp6G22', '13556361010', '123123@example.com', '123123', 'student', 'http://localhost:8081/uploads/b4cc5155-ec36-4c27-adc5-0761ba180b6e.jpg', '北京大学', 400, '27考研', '政治,英语一,数学一', NULL, '2026-01-01 02:06:02', '2026-01-06 22:34:53');
INSERT INTO `user` VALUES (5, '33223', '$2a$10$PC6I3rthE/1N6qj7y/OPGOUXqkSb2kErBKl2G8iGNlZwuK4YPlrDm', '13255187661', '33223@example.com', 'e12e', 'student', 'http://localhost:8081/uploads/b4cc5155-ec36-4c27-adc5-0761ba180b6e.jpg', '广州大学', 330, '27考研', '政治,英语二,数学二', NULL, '2026-01-01 02:06:02', '2026-01-06 22:34:55');
INSERT INTO `user` VALUES (6, '343434', '$2a$10$PC6I3rthE/1N6qj7y/OPGOUXqkSb2kErBKl2G8iGNlZwuK4YPlrDm', '13406855303', '343434@example.com', 'qeffqwe3f', 'student', 'http://localhost:8081/uploads/b4cc5155-ec36-4c27-adc5-0761ba180b6e.jpg', '上海大学', 340, '27考研', '政治,英语一,数学一', NULL, '2026-01-01 02:06:02', '2026-01-06 22:34:57');
INSERT INTO `user` VALUES (7, '个如果sfwe', '$2a$10$PC6I3rthE/1N6qj7y/OPGOUXqkSb2kErBKl2G8iGNlZwuK4YPlrDm', '13268713561', '个如果sfwe@example.com', 'wqdq', 'student', 'http://localhost:8081/uploads/b4cc5155-ec36-4c27-adc5-0761ba180b6e.jpg', '苏州大学', 350, '27考研', '政治,英语二,数学二', NULL, '2026-01-01 02:06:02', '2026-01-06 22:34:58');
INSERT INTO `user` VALUES (8, '131f', '$2a$10$PC6I3rthE/1N6qj7y/OPGOUXqkSb2kErBKl2G8iGNlZwuK4YPlrDm', '13923001923', '131f@example.com', 'sadvv', 'student', 'http://localhost:8081/uploads/b4cc5155-ec36-4c27-adc5-0761ba180b6e.jpg', '江南大学', 340, '27考研', '政治,英语二,数学二', NULL, '2026-01-01 02:06:02', '2026-01-06 22:34:59');
INSERT INTO `user` VALUES (9, '啊微腐败', '$2a$10$PC6I3rthE/1N6qj7y/OPGOUXqkSb2kErBKl2G8iGNlZwuK4YPlrDm', '13108868962', '啊微腐败@example.com', 'wFE时DF', 'student', 'http://localhost:8081/uploads/b4cc5155-ec36-4c27-adc5-0761ba180b6e.jpg', '南昌大学', 340, '27考研', '政治,英语二,数学二', NULL, '2026-01-01 02:06:02', '2026-01-06 22:35:01');
INSERT INTO `user` VALUES (10, '啊微去外地败', '$2a$10$PC6I3rthE/1N6qj7y/OPGOUXqkSb2kErBKl2G8iGNlZwuK4YPlrDm', '13375339067', '啊微去外地败@example.com', 'wFE时DF', 'student', 'http://localhost:8081/uploads/b4cc5155-ec36-4c27-adc5-0761ba180b6e.jpg', '广西大学', 330, '27考研', '政治,英语一,数学一', NULL, '2026-01-01 02:06:02', '2026-01-06 22:35:05');
INSERT INTO `user` VALUES (11, '微分方程', '$2a$10$PC6I3rthE/1N6qj7y/OPGOUXqkSb2kErBKl2G8iGNlZwuK4YPlrDm', '13550088476', '微分方程@example.com', 'wFE时DF', 'student', 'http://localhost:8081/uploads/b4cc5155-ec36-4c27-adc5-0761ba180b6e.jpg', '云南大学', 340, '27考研', '政治,英语一,数学一', NULL, '2026-01-01 02:06:02', '2026-01-06 22:35:08');
INSERT INTO `user` VALUES (12, 'usererername', '$2a$10$Zx6CQkJTGh043qd2S6uhEuwB/WzgjEOvQsaeFl6A5I6KE3ijfkw.C', '13623452353', 'usererername@example.com', '焊武弟', 'student', 'http://localhost:8081/uploads/b4cc5155-ec36-4c27-adc5-0761ba180b6e.jpg', '深圳大学', 340, '27考研', '政治,英语一,数学一', NULL, '2026-01-06 22:03:48', '2026-01-06 22:35:11');
INSERT INTO `user` VALUES (13, '升降开关', '$2a$10$Zx6CQkJTGh043qd2S6uhEuwB/WzgjEOvQsaeFl6A5I6KE3ijfkw.C', '13624425208', 'usererername@example.com', '汉武帝', 'student', 'http://localhost:8081/uploads/b4cc5155-ec36-4c27-adc5-0761ba180b6e.jpg', '深圳大学', 340, '27考研', '政治,英语一,数学一', NULL, '2026-01-06 22:03:48', '2026-01-06 22:35:11');
INSERT INTO `user` VALUES (24, 'usernameusername', '$2a$10$5V2ezlTSe2RYHGPGCvGLO.AvKAIChvdpfy.NV.4NWsJ1Cggn1PfD6', '12332323212', '12312312312@qq.com', '汉吻帝', 'student', '/img/default-avatar.png', NULL, 0, '27考研', NULL, NULL, '2026-01-30 00:04:28', '2026-01-30 00:04:28');

SET FOREIGN_KEY_CHECKS = 1;
