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

 Date: 23/01/2026 22:28:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for map_paper_question
-- ----------------------------
DROP TABLE IF EXISTS `map_paper_question`;
CREATE TABLE `map_paper_question`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `paper_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '试卷id',
  `question_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '题目id',
  `sort_order` smallint NULL DEFAULT NULL COMMENT '题号',
  `score_value` decimal(5, 2) NULL DEFAULT NULL COMMENT '分值',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '题目类型: \r\n1-单选\r\n2-多选\r\n3-填空\r\n4-综合应用\r\n5-完型填空\r\n6-阅读理解\r\n7-新题型\r\n8-翻译\r\n9-小作文\r\n10-大作文',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `paper_id`(`paper_id` ASC) USING BTREE,
  INDEX `idx_map_paper_question_type`(`type` ASC) USING BTREE,
  CONSTRAINT `map_paper_question_ibfk_1` FOREIGN KEY (`paper_id`) REFERENCES `tb_paper` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of map_paper_question
-- ----------------------------
INSERT INTO `map_paper_question` VALUES ('088df2318acf3eb1dcff8ef13d7e5ad4', 'b591348d2de9a6de94466e91e5ef60e4', '1', 1, 5.00, NULL);
INSERT INTO `map_paper_question` VALUES ('20d77c0c3b49175817b5256ffcc4bc84', 'b591348d2de9a6de94466e91e5ef60e4', '13', 4, 5.00, NULL);
INSERT INTO `map_paper_question` VALUES ('2f3382390e3db526b5734710a41062a6', 'fc732cc5a9f6a05477ef1f6b9758759c', '11', 3, 5.00, NULL);
INSERT INTO `map_paper_question` VALUES ('3c00b323d4283093c0ba25d790e18463', 'fc732cc5a9f6a05477ef1f6b9758759c', '13', 1, 5.00, NULL);
INSERT INTO `map_paper_question` VALUES ('45917ecda24eea909e0a29fd1911825e', 'b591348d2de9a6de94466e91e5ef60e4', '16', 7, 5.00, NULL);
INSERT INTO `map_paper_question` VALUES ('5378e3aede6106c9b75d605de3935ff1', 'fc732cc5a9f6a05477ef1f6b9758759c', '15', 4, 5.00, NULL);
INSERT INTO `map_paper_question` VALUES ('5d7c3b96b791fde22105266b2ea76a01', 'fc732cc5a9f6a05477ef1f6b9758759c', '12', 2, 5.00, NULL);
INSERT INTO `map_paper_question` VALUES ('79d80c0044b80619c3c476af311544eb', 'b591348d2de9a6de94466e91e5ef60e4', '15', 6, 5.00, NULL);
INSERT INTO `map_paper_question` VALUES ('85c0b40257df6ce6e9534af7ecba6f3e', 'b591348d2de9a6de94466e91e5ef60e4', '11', 2, 5.00, NULL);
INSERT INTO `map_paper_question` VALUES ('9ed2ae6dbe92fee633413e2fdada750e', 'b591348d2de9a6de94466e91e5ef60e4', '14', 5, 5.00, NULL);
INSERT INTO `map_paper_question` VALUES ('aa8556000ff30d9ad1825ca6102f8eb5', 'b591348d2de9a6de94466e91e5ef60e4', '12', 3, 5.00, NULL);
INSERT INTO `map_paper_question` VALUES ('ae8e1fc1ba6362fb052965f1d49c5e55', 'fc732cc5a9f6a05477ef1f6b9758759c', '16', 5, 5.00, NULL);

-- ----------------------------
-- Table structure for map_question_book
-- ----------------------------
DROP TABLE IF EXISTS `map_question_book`;
CREATE TABLE `map_question_book`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `book_id` int NOT NULL COMMENT '习题册ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_question_book`(`question_id` ASC, `book_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  INDEX `idx_book_id`(`book_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目-书本关联映射表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of map_question_book
-- ----------------------------
INSERT INTO `map_question_book` VALUES (19, 1, 10);
INSERT INTO `map_question_book` VALUES (3, 2, 3);
INSERT INTO `map_question_book` VALUES (5, 3, 2);
INSERT INTO `map_question_book` VALUES (4, 3, 3);
INSERT INTO `map_question_book` VALUES (6, 4, 1);
INSERT INTO `map_question_book` VALUES (7, 5, 1);
INSERT INTO `map_question_book` VALUES (8, 5, 2);
INSERT INTO `map_question_book` VALUES (9, 5, 3);
INSERT INTO `map_question_book` VALUES (10, 5, 5);
INSERT INTO `map_question_book` VALUES (18, 6, 11);
INSERT INTO `map_question_book` VALUES (17, 7, 11);
INSERT INTO `map_question_book` VALUES (13, 8, 14);
INSERT INTO `map_question_book` VALUES (14, 9, 15);
INSERT INTO `map_question_book` VALUES (15, 10, 16);
INSERT INTO `map_question_book` VALUES (20, 11, 10);
INSERT INTO `map_question_book` VALUES (24, 12, 9);
INSERT INTO `map_question_book` VALUES (22, 13, 18);
INSERT INTO `map_question_book` VALUES (27, 14, 18);
INSERT INTO `map_question_book` VALUES (25, 15, 18);
INSERT INTO `map_question_book` VALUES (26, 16, 18);
INSERT INTO `map_question_book` VALUES (28, 17, 21);
INSERT INTO `map_question_book` VALUES (29, 18, 21);
INSERT INTO `map_question_book` VALUES (30, 19, 21);
INSERT INTO `map_question_book` VALUES (31, 20, 21);
INSERT INTO `map_question_book` VALUES (32, 21, 21);
INSERT INTO `map_question_book` VALUES (33, 22, 7);
INSERT INTO `map_question_book` VALUES (34, 23, 7);

-- ----------------------------
-- Table structure for map_question_subject
-- ----------------------------
DROP TABLE IF EXISTS `map_question_subject`;
CREATE TABLE `map_question_subject`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `subject_id` int NOT NULL COMMENT '科目ID或知识点ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_question_subject`(`question_id` ASC, `subject_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  INDEX `idx_subject_id`(`subject_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 84 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目-科目关联映射表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of map_question_subject
-- ----------------------------
INSERT INTO `map_question_subject` VALUES (31, 1, 4);
INSERT INTO `map_question_subject` VALUES (32, 1, 5);
INSERT INTO `map_question_subject` VALUES (33, 1, 6);
INSERT INTO `map_question_subject` VALUES (34, 1, 19);
INSERT INTO `map_question_subject` VALUES (7, 2, 1);
INSERT INTO `map_question_subject` VALUES (6, 2, 8);
INSERT INTO `map_question_subject` VALUES (8, 3, 1);
INSERT INTO `map_question_subject` VALUES (9, 3, 8);
INSERT INTO `map_question_subject` VALUES (11, 4, 1);
INSERT INTO `map_question_subject` VALUES (10, 4, 8);
INSERT INTO `map_question_subject` VALUES (12, 5, 9);
INSERT INTO `map_question_subject` VALUES (30, 6, -2);
INSERT INTO `map_question_subject` VALUES (28, 6, 2);
INSERT INTO `map_question_subject` VALUES (29, 6, 3);
INSERT INTO `map_question_subject` VALUES (27, 7, -2);
INSERT INTO `map_question_subject` VALUES (25, 7, 2);
INSERT INTO `map_question_subject` VALUES (26, 7, 3);
INSERT INTO `map_question_subject` VALUES (17, 8, 7);
INSERT INTO `map_question_subject` VALUES (18, 8, 22);
INSERT INTO `map_question_subject` VALUES (19, 9, 7);
INSERT INTO `map_question_subject` VALUES (20, 9, 23);
INSERT INTO `map_question_subject` VALUES (22, 10, 7);
INSERT INTO `map_question_subject` VALUES (21, 10, 24);
INSERT INTO `map_question_subject` VALUES (35, 11, -3);
INSERT INTO `map_question_subject` VALUES (36, 11, 4);
INSERT INTO `map_question_subject` VALUES (37, 11, 5);
INSERT INTO `map_question_subject` VALUES (39, 11, 6);
INSERT INTO `map_question_subject` VALUES (38, 11, 19);
INSERT INTO `map_question_subject` VALUES (54, 12, 4);
INSERT INTO `map_question_subject` VALUES (55, 12, 5);
INSERT INTO `map_question_subject` VALUES (56, 12, 6);
INSERT INTO `map_question_subject` VALUES (57, 12, 19);
INSERT INTO `map_question_subject` VALUES (44, 13, -3);
INSERT INTO `map_question_subject` VALUES (45, 13, 4);
INSERT INTO `map_question_subject` VALUES (47, 13, 5);
INSERT INTO `map_question_subject` VALUES (48, 13, 6);
INSERT INTO `map_question_subject` VALUES (46, 13, 19);
INSERT INTO `map_question_subject` VALUES (68, 14, -3);
INSERT INTO `map_question_subject` VALUES (69, 14, 4);
INSERT INTO `map_question_subject` VALUES (70, 14, 5);
INSERT INTO `map_question_subject` VALUES (71, 14, 6);
INSERT INTO `map_question_subject` VALUES (72, 14, 19);
INSERT INTO `map_question_subject` VALUES (58, 15, -3);
INSERT INTO `map_question_subject` VALUES (59, 15, 4);
INSERT INTO `map_question_subject` VALUES (61, 15, 5);
INSERT INTO `map_question_subject` VALUES (62, 15, 6);
INSERT INTO `map_question_subject` VALUES (60, 15, 19);
INSERT INTO `map_question_subject` VALUES (63, 16, -3);
INSERT INTO `map_question_subject` VALUES (64, 16, 4);
INSERT INTO `map_question_subject` VALUES (66, 16, 5);
INSERT INTO `map_question_subject` VALUES (67, 16, 6);
INSERT INTO `map_question_subject` VALUES (65, 16, 19);
INSERT INTO `map_question_subject` VALUES (73, 17, 7);
INSERT INTO `map_question_subject` VALUES (74, 18, 7);
INSERT INTO `map_question_subject` VALUES (75, 19, 7);
INSERT INTO `map_question_subject` VALUES (76, 20, 7);
INSERT INTO `map_question_subject` VALUES (77, 21, 7);
INSERT INTO `map_question_subject` VALUES (78, 22, 4);
INSERT INTO `map_question_subject` VALUES (80, 22, 5);
INSERT INTO `map_question_subject` VALUES (79, 22, 6);
INSERT INTO `map_question_subject` VALUES (81, 23, 4);
INSERT INTO `map_question_subject` VALUES (83, 23, 5);
INSERT INTO `map_question_subject` VALUES (82, 23, 6);

-- ----------------------------
-- Table structure for map_subject_book
-- ----------------------------
DROP TABLE IF EXISTS `map_subject_book`;
CREATE TABLE `map_subject_book`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `book_id` int NOT NULL COMMENT '大纲ID (数一:4；数二:5；数三:6)',
  `subject_id` int NOT NULL COMMENT '知识科目ID (高数:401；线代:402；概率403)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_subject_book`(`book_id` ASC, `subject_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 125 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试科目与考纲书本映射表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of map_subject_book
-- ----------------------------
INSERT INTO `map_subject_book` VALUES (6, 1, 1);
INSERT INTO `map_subject_book` VALUES (1, 1, 8);
INSERT INTO `map_subject_book` VALUES (2, 1, 9);
INSERT INTO `map_subject_book` VALUES (3, 1, 10);
INSERT INTO `map_subject_book` VALUES (4, 1, 11);
INSERT INTO `map_subject_book` VALUES (5, 1, 12);
INSERT INTO `map_subject_book` VALUES (7, 2, 1);
INSERT INTO `map_subject_book` VALUES (8, 2, 8);
INSERT INTO `map_subject_book` VALUES (9, 2, 9);
INSERT INTO `map_subject_book` VALUES (10, 2, 10);
INSERT INTO `map_subject_book` VALUES (11, 2, 11);
INSERT INTO `map_subject_book` VALUES (12, 2, 12);
INSERT INTO `map_subject_book` VALUES (13, 3, 1);
INSERT INTO `map_subject_book` VALUES (14, 3, 8);
INSERT INTO `map_subject_book` VALUES (15, 3, 9);
INSERT INTO `map_subject_book` VALUES (16, 3, 10);
INSERT INTO `map_subject_book` VALUES (17, 3, 11);
INSERT INTO `map_subject_book` VALUES (18, 3, 12);
INSERT INTO `map_subject_book` VALUES (19, 4, 1);
INSERT INTO `map_subject_book` VALUES (20, 4, 8);
INSERT INTO `map_subject_book` VALUES (21, 4, 9);
INSERT INTO `map_subject_book` VALUES (22, 4, 10);
INSERT INTO `map_subject_book` VALUES (23, 4, 11);
INSERT INTO `map_subject_book` VALUES (24, 4, 12);
INSERT INTO `map_subject_book` VALUES (25, 5, 1);
INSERT INTO `map_subject_book` VALUES (26, 5, 8);
INSERT INTO `map_subject_book` VALUES (27, 5, 9);
INSERT INTO `map_subject_book` VALUES (28, 5, 10);
INSERT INTO `map_subject_book` VALUES (29, 5, 11);
INSERT INTO `map_subject_book` VALUES (30, 5, 12);
INSERT INTO `map_subject_book` VALUES (31, 6, -3);
INSERT INTO `map_subject_book` VALUES (32, 6, 4);
INSERT INTO `map_subject_book` VALUES (36, 6, 5);
INSERT INTO `map_subject_book` VALUES (37, 6, 6);
INSERT INTO `map_subject_book` VALUES (33, 6, 19);
INSERT INTO `map_subject_book` VALUES (34, 6, 20);
INSERT INTO `map_subject_book` VALUES (35, 6, 21);
INSERT INTO `map_subject_book` VALUES (38, 7, -3);
INSERT INTO `map_subject_book` VALUES (39, 7, 4);
INSERT INTO `map_subject_book` VALUES (43, 7, 5);
INSERT INTO `map_subject_book` VALUES (44, 7, 6);
INSERT INTO `map_subject_book` VALUES (40, 7, 19);
INSERT INTO `map_subject_book` VALUES (41, 7, 20);
INSERT INTO `map_subject_book` VALUES (42, 7, 21);
INSERT INTO `map_subject_book` VALUES (45, 8, -3);
INSERT INTO `map_subject_book` VALUES (46, 8, 4);
INSERT INTO `map_subject_book` VALUES (50, 8, 5);
INSERT INTO `map_subject_book` VALUES (51, 8, 6);
INSERT INTO `map_subject_book` VALUES (47, 8, 19);
INSERT INTO `map_subject_book` VALUES (48, 8, 20);
INSERT INTO `map_subject_book` VALUES (49, 8, 21);
INSERT INTO `map_subject_book` VALUES (52, 9, -3);
INSERT INTO `map_subject_book` VALUES (53, 9, 4);
INSERT INTO `map_subject_book` VALUES (57, 9, 5);
INSERT INTO `map_subject_book` VALUES (58, 9, 6);
INSERT INTO `map_subject_book` VALUES (54, 9, 19);
INSERT INTO `map_subject_book` VALUES (55, 9, 20);
INSERT INTO `map_subject_book` VALUES (56, 9, 21);
INSERT INTO `map_subject_book` VALUES (59, 10, -3);
INSERT INTO `map_subject_book` VALUES (60, 10, 4);
INSERT INTO `map_subject_book` VALUES (64, 10, 5);
INSERT INTO `map_subject_book` VALUES (65, 10, 6);
INSERT INTO `map_subject_book` VALUES (61, 10, 19);
INSERT INTO `map_subject_book` VALUES (62, 10, 20);
INSERT INTO `map_subject_book` VALUES (63, 10, 21);
INSERT INTO `map_subject_book` VALUES (66, 11, -2);
INSERT INTO `map_subject_book` VALUES (67, 11, 2);
INSERT INTO `map_subject_book` VALUES (74, 11, 3);
INSERT INTO `map_subject_book` VALUES (68, 11, 13);
INSERT INTO `map_subject_book` VALUES (69, 11, 14);
INSERT INTO `map_subject_book` VALUES (70, 11, 15);
INSERT INTO `map_subject_book` VALUES (71, 11, 16);
INSERT INTO `map_subject_book` VALUES (72, 11, 17);
INSERT INTO `map_subject_book` VALUES (73, 11, 18);
INSERT INTO `map_subject_book` VALUES (80, 12, -2);
INSERT INTO `map_subject_book` VALUES (83, 12, 2);
INSERT INTO `map_subject_book` VALUES (84, 12, 3);
INSERT INTO `map_subject_book` VALUES (81, 12, 17);
INSERT INTO `map_subject_book` VALUES (82, 12, 18);
INSERT INTO `map_subject_book` VALUES (85, 13, -2);
INSERT INTO `map_subject_book` VALUES (86, 13, 2);
INSERT INTO `map_subject_book` VALUES (87, 13, 3);
INSERT INTO `map_subject_book` VALUES (88, 13, 17);
INSERT INTO `map_subject_book` VALUES (89, 13, 18);
INSERT INTO `map_subject_book` VALUES (90, 14, 7);
INSERT INTO `map_subject_book` VALUES (91, 14, 22);
INSERT INTO `map_subject_book` VALUES (92, 15, 7);
INSERT INTO `map_subject_book` VALUES (93, 15, 23);
INSERT INTO `map_subject_book` VALUES (94, 16, 7);
INSERT INTO `map_subject_book` VALUES (95, 16, 24);
INSERT INTO `map_subject_book` VALUES (96, 17, 7);
INSERT INTO `map_subject_book` VALUES (97, 17, 25);
INSERT INTO `map_subject_book` VALUES (104, 18, -3);
INSERT INTO `map_subject_book` VALUES (98, 18, 4);
INSERT INTO `map_subject_book` VALUES (102, 18, 5);
INSERT INTO `map_subject_book` VALUES (103, 18, 6);
INSERT INTO `map_subject_book` VALUES (99, 18, 19);
INSERT INTO `map_subject_book` VALUES (100, 18, 20);
INSERT INTO `map_subject_book` VALUES (101, 18, 21);
INSERT INTO `map_subject_book` VALUES (105, 19, -2);
INSERT INTO `map_subject_book` VALUES (106, 19, 2);
INSERT INTO `map_subject_book` VALUES (113, 19, 3);
INSERT INTO `map_subject_book` VALUES (107, 19, 13);
INSERT INTO `map_subject_book` VALUES (108, 19, 14);
INSERT INTO `map_subject_book` VALUES (109, 19, 15);
INSERT INTO `map_subject_book` VALUES (110, 19, 16);
INSERT INTO `map_subject_book` VALUES (111, 19, 17);
INSERT INTO `map_subject_book` VALUES (112, 19, 18);
INSERT INTO `map_subject_book` VALUES (114, 20, 1);
INSERT INTO `map_subject_book` VALUES (115, 20, 8);
INSERT INTO `map_subject_book` VALUES (116, 20, 9);
INSERT INTO `map_subject_book` VALUES (117, 20, 10);
INSERT INTO `map_subject_book` VALUES (118, 20, 11);
INSERT INTO `map_subject_book` VALUES (119, 20, 12);
INSERT INTO `map_subject_book` VALUES (120, 21, 7);
INSERT INTO `map_subject_book` VALUES (121, 21, 22);
INSERT INTO `map_subject_book` VALUES (122, 21, 23);
INSERT INTO `map_subject_book` VALUES (123, 21, 24);
INSERT INTO `map_subject_book` VALUES (124, 21, 25);

-- ----------------------------
-- Table structure for map_subject_weight
-- ----------------------------
DROP TABLE IF EXISTS `map_subject_weight`;
CREATE TABLE `map_subject_weight`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subject_id` int NOT NULL COMMENT '科目ID（关联tb_subject.id）',
  `exam_spec_id` int NOT NULL COMMENT '考试规格ID（关联tb_subject.id，如：英语一=2，英语二=3）',
  `weight` float NULL DEFAULT NULL COMMENT '该科目在该考试规格下的权重（百分比）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_subject_exam`(`subject_id` ASC, `exam_spec_id` ASC) USING BTREE COMMENT '同一科目在同一考试规格下只能有一条记录',
  INDEX `idx_exam_spec`(`exam_spec_id` ASC) USING BTREE COMMENT '考试规格索引',
  INDEX `idx_subject`(`subject_id` ASC) USING BTREE COMMENT '科目索引'
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '科目权重映射表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of map_subject_weight
-- ----------------------------
INSERT INTO `map_subject_weight` VALUES (1, 13, 2, 10, '2026-01-13 00:06:03', '2026-01-13 00:06:03');
INSERT INTO `map_subject_weight` VALUES (2, 14, 2, 40, '2026-01-13 00:06:03', '2026-01-13 00:06:03');
INSERT INTO `map_subject_weight` VALUES (3, 15, 2, 10, '2026-01-13 00:06:03', '2026-01-13 00:06:03');
INSERT INTO `map_subject_weight` VALUES (4, 16, 2, 10, '2026-01-13 00:06:03', '2026-01-13 00:06:03');
INSERT INTO `map_subject_weight` VALUES (5, 17, 2, 10, '2026-01-13 00:06:03', '2026-01-13 00:06:03');
INSERT INTO `map_subject_weight` VALUES (6, 18, 2, 20, '2026-01-13 00:06:03', '2026-01-13 00:06:03');
INSERT INTO `map_subject_weight` VALUES (7, 13, 3, 10, '2026-01-13 00:06:03', '2026-01-13 00:06:03');
INSERT INTO `map_subject_weight` VALUES (8, 14, 3, 40, '2026-01-13 00:06:03', '2026-01-13 00:06:03');
INSERT INTO `map_subject_weight` VALUES (9, 15, 3, 10, '2026-01-13 00:06:03', '2026-01-13 00:06:03');
INSERT INTO `map_subject_weight` VALUES (10, 16, 3, 15, '2026-01-13 00:06:03', '2026-01-13 00:06:03');
INSERT INTO `map_subject_weight` VALUES (11, 17, 3, 10, '2026-01-13 00:06:03', '2026-01-13 00:06:03');
INSERT INTO `map_subject_weight` VALUES (12, 18, 3, 15, '2026-01-13 00:06:03', '2026-01-13 00:06:03');
INSERT INTO `map_subject_weight` VALUES (13, 19, 4, 58, '2026-01-13 00:27:35', '2026-01-13 00:27:35');
INSERT INTO `map_subject_weight` VALUES (14, 20, 4, 21, '2026-01-13 00:27:35', '2026-01-13 00:27:35');
INSERT INTO `map_subject_weight` VALUES (15, 21, 4, 21, '2026-01-13 00:27:35', '2026-01-13 00:27:35');
INSERT INTO `map_subject_weight` VALUES (16, 19, 5, 78, '2026-01-13 00:27:35', '2026-01-13 00:27:35');
INSERT INTO `map_subject_weight` VALUES (17, 20, 5, 22, '2026-01-13 00:27:35', '2026-01-13 00:27:35');
INSERT INTO `map_subject_weight` VALUES (18, 19, 6, 58, '2026-01-13 00:27:35', '2026-01-13 00:27:35');
INSERT INTO `map_subject_weight` VALUES (19, 20, 6, 21, '2026-01-13 00:27:35', '2026-01-13 00:27:35');
INSERT INTO `map_subject_weight` VALUES (20, 21, 6, 21, '2026-01-13 00:27:35', '2026-01-13 00:27:35');
INSERT INTO `map_subject_weight` VALUES (21, 22, 7, 28, '2026-01-13 00:35:34', '2026-01-13 00:35:34');
INSERT INTO `map_subject_weight` VALUES (22, 23, 7, 28, '2026-01-13 00:35:34', '2026-01-13 00:35:34');
INSERT INTO `map_subject_weight` VALUES (23, 24, 7, 27, '2026-01-13 00:35:34', '2026-01-13 00:35:34');
INSERT INTO `map_subject_weight` VALUES (24, 25, 7, 17, '2026-01-13 00:35:34', '2026-01-13 00:35:34');
INSERT INTO `map_subject_weight` VALUES (25, 8, 1, 24, '2026-01-13 00:56:49', '2026-01-13 00:56:49');
INSERT INTO `map_subject_weight` VALUES (26, 9, 1, 30, '2026-01-13 00:58:15', '2026-01-13 00:58:15');
INSERT INTO `map_subject_weight` VALUES (27, 10, 1, 14, '2026-01-13 00:58:27', '2026-01-13 00:58:27');
INSERT INTO `map_subject_weight` VALUES (28, 11, 1, 16, '2026-01-13 00:58:46', '2026-01-13 00:58:46');
INSERT INTO `map_subject_weight` VALUES (29, 12, 1, 16, '2026-01-13 00:58:54', '2026-01-13 00:58:54');

-- ----------------------------
-- Table structure for tb_book
-- ----------------------------
DROP TABLE IF EXISTS `tb_book`;
CREATE TABLE `tb_book`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '习题册名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '简介',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '习题册表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_book
-- ----------------------------
INSERT INTO `tb_book` VALUES (1, '肖秀荣知识点精讲精练', '覆盖大纲所有考点，配套肖秀荣1000题使用效果最佳', '2026-01-08 13:23:04');
INSERT INTO `tb_book` VALUES (2, '徐涛核心考案', '逻辑清晰，适合搭配徐涛强化班视频（趣味性强，适合理科生）', '2026-01-08 13:23:20');
INSERT INTO `tb_book` VALUES (3, '肖秀荣1000题', '（必刷！），基础题+真题改编，建议刷2-3遍，错题标记反复看', '2026-01-08 13:23:35');
INSERT INTO `tb_book` VALUES (4, '考研政治思维导图', '可视化梳理马原、史纲等易混知识点。', '2026-01-08 13:23:46');
INSERT INTO `tb_book` VALUES (5, '肖秀荣讲真题', '近5年真题分析，掌握命题人思路。', '2026-01-08 13:24:05');
INSERT INTO `tb_book` VALUES (6, '考研数学复习全书', '知识体系系统，覆盖考纲全面，例题很多是来自近些年的真题，比较适合第一轮打基础时做整个知识体系的构建。', '2026-01-08 13:25:11');
INSERT INTO `tb_book` VALUES (7, '数学复习全书', '这套复习全书的原型是双李版复习全书，对知识体系的讲解非常细致，基本无死角，跟的例题也比较，对于练习思维很有帮助。', '2026-01-08 13:25:30');
INSERT INTO `tb_book` VALUES (8, '数学基础过关660题', '李永乐的《数学基础过关660题》，非常注重基础，题型只有选择题和填空题，是由李永乐、王式安等几个年龄比较大的老师精心打磨的。', '2026-01-08 13:27:17');
INSERT INTO `tb_book` VALUES (9, '接力题典1800', '《接力题典1800》是汤家凤老师得了力作，分为基础篇和提高篇，基础篇强烈介意刷一下，强化可以用李永乐的《数学基础过关660题》，效果可能更好一些。', '2026-01-08 13:27:30');
INSERT INTO `tb_book` VALUES (10, '精讲精练880题', '李林老师的扛鼎之作，有人说，李林是个扫地僧，这本书总体质量还是很高。但李林更出名的是他的押题，这个相信很多人都有耳闻，在押题这块，类似数学界的“肖秀荣”。', '2026-01-08 13:27:38');
INSERT INTO `tb_book` VALUES (11, '考研真题黄皮书', '相比考研真相，黄皮书更注重解题思路和技巧，题目后面都附有解题技巧提炼，会带大家以命题人的角度去做题，更好的掌握出题规律，对于提高英语阅读的做题能力非常有帮助，更适合有基础，重效率、重学习思路逻辑的同学。', '2026-01-08 13:28:54');
INSERT INTO `tb_book` VALUES (12, '考研英语高分写作', '作文话题收集全面，有大量优质的范文，能极大提升语料库。\n\n里面除了有很多的历年满分作文外，还会教你如何搭建框架，能在短期内帮你快速提升写作能力，突破作文这关。\n\n本书的核心就是背诵是最有效的方法，多读、多背，多写，大小作文各背十多二十篇以后，再去写作毫无压力。', '2026-01-08 13:29:10');
INSERT INTO `tb_book` VALUES (13, '九步搞定考研英语高分作文', '注重写作思维的培养，运用了“九宫格”的写作结构，把一篇作文分为三大段九个部分，每个部分又具体拆开来讲，教会大家该怎么下手，让写出来的文章层次鲜明、结构清晰。', '2026-01-08 13:29:56');
INSERT INTO `tb_book` VALUES (14, '王道数据结构', '计算机专业考研的408科目涵盖了数据结构、计算机组成原理、操作系统和计算机网络四门核心课程，内容广泛且难度较大。考试特点为知识点多、难度大、题量多，考察的不仅是记忆能力，更是对知识的理解和应用能力。', '2026-01-08 13:31:13');
INSERT INTO `tb_book` VALUES (15, '王道计算机组成原理', '计算机专业考研的408科目涵盖了数据结构、计算机组成原理、操作系统和计算机网络四门核心课程，内容广泛且难度较大。考试特点为知识点多、难度大、题量多，考察的不仅是记忆能力，更是对知识的理解和应用能力。', '2026-01-08 13:31:36');
INSERT INTO `tb_book` VALUES (16, '王道操作系统', '计算机专业考研的408科目涵盖了数据结构、计算机组成原理、操作系统和计算机网络四门核心课程，内容广泛且难度较大。考试特点为知识点多、难度大、题量多，考察的不仅是记忆能力，更是对知识的理解和应用能力。', '2026-01-08 13:31:55');
INSERT INTO `tb_book` VALUES (17, '王道计算机网络', '计算机专业考研的408科目涵盖了数据结构、计算机组成原理、操作系统和计算机网络四门核心课程，内容广泛且难度较大。考试特点为知识点多、难度大、题量多，考察的不仅是记忆能力，更是对知识的理解和应用能力。', '2026-01-08 13:32:13');
INSERT INTO `tb_book` VALUES (18, '考研数学真题导入', '考研数学真题导入专用', '2026-01-14 14:08:12');
INSERT INTO `tb_book` VALUES (19, '考研英语真题导入', '考研英语真题导入专用', '2026-01-14 14:08:37');
INSERT INTO `tb_book` VALUES (20, '考研政治真题导入', '考研政治真题导入专用', '2026-01-14 14:08:59');
INSERT INTO `tb_book` VALUES (21, '考研408真题导入', '考研408真题导入专用', '2026-01-14 14:09:14');

-- ----------------------------
-- Table structure for tb_collection
-- ----------------------------
DROP TABLE IF EXISTS `tb_collection`;
CREATE TABLE `tb_collection`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `tags` json NULL COMMENT '自定义标签',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_ques`(`user_id` ASC, `question_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '收藏夹' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_collection
-- ----------------------------

-- ----------------------------
-- Table structure for tb_exam_answer_detail
-- ----------------------------
DROP TABLE IF EXISTS `tb_exam_answer_detail`;
CREATE TABLE `tb_exam_answer_detail`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `session_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会话id',
  `question_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '题目id',
  `user_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '用户答案',
  `is_correct` tinyint NULL DEFAULT NULL COMMENT '对/错/待定（主观题）',
  `score_earned` decimal(5, 2) NULL DEFAULT NULL COMMENT '得分率',
  `duration_seconds` int NULL DEFAULT NULL COMMENT '用时',
  `ai_feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'ai返回',
  `knowledge_point_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '知识点id',
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `session_id`(`session_id` ASC) USING BTREE,
  CONSTRAINT `tb_exam_answer_detail_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `tb_exam_session` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_exam_answer_detail
-- ----------------------------
INSERT INTO `tb_exam_answer_detail` VALUES ('09429aef89850edfc4598ea226a33f4d', '9ea10cba2f2afd7dd738167e85b69aff', '14', '', 0, 0.00, NULL, NULL, NULL, '2026-01-14 16:03:12');
INSERT INTO `tb_exam_answer_detail` VALUES ('0d97a17bc4817864b4f11d7bb4ed274f', '46b13d326aa0bfb09ec431e1e0c3e79c', '1', '', 0, 0.00, NULL, NULL, NULL, '2026-01-11 23:44:54');
INSERT INTO `tb_exam_answer_detail` VALUES ('0e7e06a98912343e4aacc2702413f4ad', 'f387dd3a6ce57b3794f5cf1f588ec351', '1', '', 0, 0.00, NULL, NULL, NULL, '2026-01-12 00:12:02');
INSERT INTO `tb_exam_answer_detail` VALUES ('1e6baae0b37d48ae12c8097408286b58', 'c59751250e10736b0b70c8dc255e1857', '1', '', 0, 0.00, NULL, NULL, NULL, '2026-01-11 23:44:47');
INSERT INTO `tb_exam_answer_detail` VALUES ('25e67d29e868148adb94f52f571fd7e0', 'c179a69071c02f290094fd6039ec1fb9', '12', 'A', 0, 0.00, NULL, NULL, NULL, '2026-01-14 16:00:55');
INSERT INTO `tb_exam_answer_detail` VALUES ('2b471f2924da3c7afc078d92b7bb20b4', '368fbf4dfad184c4ea5147da86ee2d13', '14', '', 0, 0.00, NULL, NULL, NULL, '2026-01-23 02:48:21');
INSERT INTO `tb_exam_answer_detail` VALUES ('2de4024de79429b5b8abe7e7852f1adf', 'c179a69071c02f290094fd6039ec1fb9', '11', 'A', 0, 0.00, NULL, NULL, NULL, '2026-01-14 16:00:55');
INSERT INTO `tb_exam_answer_detail` VALUES ('33352f82d39787967637779bd930938a', '368fbf4dfad184c4ea5147da86ee2d13', '15', '', 0, 0.00, NULL, NULL, NULL, '2026-01-23 02:48:21');
INSERT INTO `tb_exam_answer_detail` VALUES ('3e323fdaa2578908c561479b16534b7a', '9ec4ef3818110e7dc41249f62fdc6c77', '12', 'C', 1, 5.00, NULL, NULL, NULL, '2026-01-14 14:49:12');
INSERT INTO `tb_exam_answer_detail` VALUES ('450134abfe2074b56b0058fc19e3064f', 'c179a69071c02f290094fd6039ec1fb9', '15', '', 0, 0.00, NULL, NULL, NULL, '2026-01-14 16:00:55');
INSERT INTO `tb_exam_answer_detail` VALUES ('4ef9b6cb85e19437d91bb3dff09b5a1e', '9ec4ef3818110e7dc41249f62fdc6c77', '11', 'A', 0, 0.00, NULL, NULL, NULL, '2026-01-14 14:49:12');
INSERT INTO `tb_exam_answer_detail` VALUES ('5812518d20a0ac0b9b5f69e60ee1d13c', '3733fc84f24aacf3620c6d8d66989b6c', '11', '', 0, 0.00, NULL, NULL, NULL, '2026-01-14 16:05:27');
INSERT INTO `tb_exam_answer_detail` VALUES ('5ed90048683a45b5ebba92a4963c9efc', '9ea10cba2f2afd7dd738167e85b69aff', '13', '', 0, 0.00, NULL, NULL, NULL, '2026-01-14 16:03:12');
INSERT INTO `tb_exam_answer_detail` VALUES ('60b5d692945846dc97cd24f123813e9c', '368fbf4dfad184c4ea5147da86ee2d13', '13', '', 0, 0.00, NULL, NULL, NULL, '2026-01-23 02:48:21');
INSERT INTO `tb_exam_answer_detail` VALUES ('611035ae219bae4991c69eae042f1d98', '0d3f4b4c14ba2c879c287804b836ca86', '1', '', 0, 0.00, NULL, NULL, NULL, '2026-01-12 15:19:57');
INSERT INTO `tb_exam_answer_detail` VALUES ('631092eeee6bb6562702241371d0bb3d', '3733fc84f24aacf3620c6d8d66989b6c', '12', '', 0, 0.00, NULL, NULL, NULL, '2026-01-14 16:05:27');
INSERT INTO `tb_exam_answer_detail` VALUES ('63c50db8b55dcbfc4346ec7d96a1703e', '9ea10cba2f2afd7dd738167e85b69aff', '15', '', 0, 0.00, NULL, NULL, NULL, '2026-01-14 16:03:12');
INSERT INTO `tb_exam_answer_detail` VALUES ('6584598b82fa550d821770784fa1c193', '9ea10cba2f2afd7dd738167e85b69aff', '11', '', 0, 0.00, NULL, NULL, NULL, '2026-01-14 16:03:12');
INSERT INTO `tb_exam_answer_detail` VALUES ('6b6ed7d55fcf8327e1a616636804cc16', '368fbf4dfad184c4ea5147da86ee2d13', '16', '', 3, 0.00, NULL, 'AI批改失败：GLM API 调用失败，已重试 3 次', NULL, '2026-01-23 02:48:21');
INSERT INTO `tb_exam_answer_detail` VALUES ('7fcf67098c132ccf1b84ba193d5fcbc6', '9ec4ef3818110e7dc41249f62fdc6c77', '15', '', 3, 0.00, NULL, 'AI批改失败：GLM API 调用失败，已重试 3 次', NULL, '2026-01-14 14:49:12');
INSERT INTO `tb_exam_answer_detail` VALUES ('7ff3981d4d2cf4632775924d400be4fc', 'c179a69071c02f290094fd6039ec1fb9', '14', 'A', 1, 5.00, NULL, NULL, NULL, '2026-01-14 16:00:55');
INSERT INTO `tb_exam_answer_detail` VALUES ('8024e9ffc955c2e25bb7b180f9d3cda7', '9ea10cba2f2afd7dd738167e85b69aff', '16', '', 3, 0.00, NULL, 'AI批改失败：GLM API 调用失败，已重试 3 次', NULL, '2026-01-14 16:03:12');
INSERT INTO `tb_exam_answer_detail` VALUES ('85a3898d72b9aedc02d0bd3e8edde85c', 'ed829d294e7b5e7d9a86f1e071c7891e', '1', '', 0, 0.00, NULL, NULL, NULL, '2026-01-11 23:51:37');
INSERT INTO `tb_exam_answer_detail` VALUES ('8f2075de4abf5ac4076e89a12bf06e88', '3733fc84f24aacf3620c6d8d66989b6c', '1', '', 0, 0.00, NULL, NULL, NULL, '2026-01-14 16:05:27');
INSERT INTO `tb_exam_answer_detail` VALUES ('95b57748e6fbbffa2bd280710f357201', '9ea10cba2f2afd7dd738167e85b69aff', '12', '', 0, 0.00, NULL, NULL, NULL, '2026-01-14 16:03:12');
INSERT INTO `tb_exam_answer_detail` VALUES ('965102f8245b2e225d99271c10aa2774', 'c179a69071c02f290094fd6039ec1fb9', '1', 'A', 1, 5.00, NULL, NULL, NULL, '2026-01-14 16:00:55');
INSERT INTO `tb_exam_answer_detail` VALUES ('a21b0c48a9233d753e050d6814f43e39', '8aa8a904e166bf868ffe0d9ec8a73c33', '1', '', 0, 0.00, NULL, NULL, NULL, '2026-01-11 23:51:31');
INSERT INTO `tb_exam_answer_detail` VALUES ('ae28dd4f49a5b0b7a354c45ce77ae0de', '3733fc84f24aacf3620c6d8d66989b6c', '13', '', 0, 0.00, NULL, NULL, NULL, '2026-01-14 16:05:27');
INSERT INTO `tb_exam_answer_detail` VALUES ('b4cbfe3731743ba4e6ca110d01b2f3e0', '9ea10cba2f2afd7dd738167e85b69aff', '1', '', 0, 0.00, NULL, NULL, NULL, '2026-01-14 16:03:12');
INSERT INTO `tb_exam_answer_detail` VALUES ('b89c97361669b9b30bc38a8455f49bc3', '3733fc84f24aacf3620c6d8d66989b6c', '14', 'A', 1, 5.00, NULL, NULL, NULL, '2026-01-14 16:05:27');
INSERT INTO `tb_exam_answer_detail` VALUES ('bbf90a536fd122b83827da9999bbe92a', '368fbf4dfad184c4ea5147da86ee2d13', '12', '', 0, 0.00, NULL, NULL, NULL, '2026-01-23 02:48:21');
INSERT INTO `tb_exam_answer_detail` VALUES ('be1a304884cafb49f28229f19b7e83ac', '368fbf4dfad184c4ea5147da86ee2d13', '1', '', 0, 0.00, NULL, NULL, NULL, '2026-01-23 02:48:21');
INSERT INTO `tb_exam_answer_detail` VALUES ('c0927f3b960b147a70c5cab540b893fc', 'c179a69071c02f290094fd6039ec1fb9', '13', 'A', 0, 0.00, NULL, NULL, NULL, '2026-01-14 16:00:55');
INSERT INTO `tb_exam_answer_detail` VALUES ('c1dbbe95967bfe886bd06dcc1f8797ba', '9ec4ef3818110e7dc41249f62fdc6c77', '14', 'C', 0, 0.00, NULL, NULL, NULL, '2026-01-14 14:49:12');
INSERT INTO `tb_exam_answer_detail` VALUES ('c4308d0a011d1dc4f57ec123359b5a7f', '9ec4ef3818110e7dc41249f62fdc6c77', '1', 'B', 0, 0.00, NULL, NULL, NULL, '2026-01-14 14:49:12');
INSERT INTO `tb_exam_answer_detail` VALUES ('c4a6cadf26e8fea6f5c8e3694648d71c', '3733fc84f24aacf3620c6d8d66989b6c', '16', '', 3, 0.00, NULL, 'AI批改失败：GLM API 调用失败，已重试 3 次', NULL, '2026-01-14 16:05:27');
INSERT INTO `tb_exam_answer_detail` VALUES ('c76de514263f62cfe8900f42f6273dea', '3733fc84f24aacf3620c6d8d66989b6c', '15', '', 0, 0.00, NULL, NULL, NULL, '2026-01-14 16:05:27');
INSERT INTO `tb_exam_answer_detail` VALUES ('cbb1504f2a2652464e1efeb61edd143e', '30d74422590ee4964113f5763d757b59', '1', '', 0, 0.00, NULL, NULL, NULL, '2026-01-11 23:51:26');
INSERT INTO `tb_exam_answer_detail` VALUES ('cc6e401d094b8dcc1acfc9916227b7c5', '368fbf4dfad184c4ea5147da86ee2d13', '11', '', 0, 0.00, NULL, NULL, NULL, '2026-01-23 02:48:21');
INSERT INTO `tb_exam_answer_detail` VALUES ('cce333517f23d4f73a2ab1a84d9fcc3e', 'f7b3c8f7be5eff2db4a3b5e379070389', '1', '', 0, 0.00, NULL, NULL, NULL, '2026-01-12 00:12:19');
INSERT INTO `tb_exam_answer_detail` VALUES ('dc086a22a568daccb693b0c2798fc373', '4b5a623ef31b1141c59dc0679d429837', '1', '', 0, 0.00, NULL, NULL, NULL, '2026-01-11 23:49:59');
INSERT INTO `tb_exam_answer_detail` VALUES ('dcea382784f4d582a1fb3d279a0ecc1f', '9ec4ef3818110e7dc41249f62fdc6c77', '16', '', 3, 0.00, NULL, 'AI批改失败：GLM API 调用失败，已重试 3 次', NULL, '2026-01-14 14:49:12');
INSERT INTO `tb_exam_answer_detail` VALUES ('e1a3cf846becb49b69a30fc91da77dd4', '9ec4ef3818110e7dc41249f62fdc6c77', '13', 'C', 1, 5.00, NULL, NULL, NULL, '2026-01-14 14:49:12');
INSERT INTO `tb_exam_answer_detail` VALUES ('e6c350175a9e66e607d2ed263d9bfac1', '4406bf830e116a2fd0f1d8053de06e55', '1', '', 0, 0.00, NULL, NULL, NULL, '2026-01-12 00:10:56');
INSERT INTO `tb_exam_answer_detail` VALUES ('efe3f8f364eab5a94e91799a34d75f97', 'c179a69071c02f290094fd6039ec1fb9', '16', '', 3, 0.00, NULL, 'AI批改失败：GLM API 调用失败，已重试 3 次', NULL, '2026-01-14 16:00:55');
INSERT INTO `tb_exam_answer_detail` VALUES ('f8ed78dc2b6c3e47e89f6439df304ed2', '9ed7c6d665827fbd814ea089db4d50d7', '1', '', 0, 0.00, NULL, NULL, NULL, '2026-01-11 23:44:14');

-- ----------------------------
-- Table structure for tb_exam_record
-- ----------------------------
DROP TABLE IF EXISTS `tb_exam_record`;
CREATE TABLE `tb_exam_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `user_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '用户提交的答案',
  `is_correct` tinyint(1) NULL DEFAULT NULL COMMENT '是否正确: 0-错, 1-对',
  `score` int NULL DEFAULT 0 COMMENT '得分',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `duration` int NULL DEFAULT 0 COMMENT '答题用时(秒)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_ques`(`user_id` ASC, `question_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '答题记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_exam_record
-- ----------------------------
INSERT INTO `tb_exam_record` VALUES (1, 1, 4, 'A', 0, 0, '2026-01-08 14:59:51', 1);
INSERT INTO `tb_exam_record` VALUES (2, 1, 5, 'C', 0, 0, '2026-01-08 15:00:11', 1);
INSERT INTO `tb_exam_record` VALUES (3, 1, 6, 'C', 0, 0, '2026-01-08 15:00:21', 1);
INSERT INTO `tb_exam_record` VALUES (4, 1, 7, 'D', 0, 0, '2026-01-08 15:00:23', 1);
INSERT INTO `tb_exam_record` VALUES (5, 1, 1, 'B', 0, 0, '2026-01-08 15:00:53', 5);
INSERT INTO `tb_exam_record` VALUES (6, 1, 1, 'D', 0, 0, '2026-01-08 15:00:57', 1);
INSERT INTO `tb_exam_record` VALUES (7, 1, 1, 'D', 0, 0, '2026-01-08 15:01:00', 1);
INSERT INTO `tb_exam_record` VALUES (8, 1, 1, 'A', 1, 5, '2026-01-08 15:01:10', 4);
INSERT INTO `tb_exam_record` VALUES (9, 1, 8, 'B', 1, 5, '2026-01-08 15:01:33', 2);
INSERT INTO `tb_exam_record` VALUES (10, 1, 9, 'A', 0, 0, '2026-01-08 15:01:50', 5);
INSERT INTO `tb_exam_record` VALUES (11, 1, 4, '', 0, 0, '2026-01-09 21:12:38', 3);
INSERT INTO `tb_exam_record` VALUES (12, 1, 5, 'C', 0, 0, '2026-01-09 21:12:49', 11);

-- ----------------------------
-- Table structure for tb_exam_session
-- ----------------------------
DROP TABLE IF EXISTS `tb_exam_session`;
CREATE TABLE `tb_exam_session`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户id',
  `paper_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '试卷id',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态0进行中，1已完成',
  `start_time` datetime NULL DEFAULT NULL COMMENT '考试开始时间',
  `submit_time` timestamp NULL DEFAULT NULL COMMENT '提交时间',
  `expected_end_time` datetime NULL DEFAULT NULL COMMENT '预期结束时间 = create_time + paper.time_limit',
  `total_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '总分',
  `switch_count` int NULL DEFAULT 0 COMMENT '切屏次数',
  `ai_summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'ai总结',
  `snapshot_answers` json NULL COMMENT '试卷答案快照',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_exam_session
-- ----------------------------
INSERT INTO `tb_exam_session` VALUES ('0d3f4b4c14ba2c879c287804b836ca86', '1', '1', 1, '2026-01-12 15:19:50', '2026-01-12 15:19:57', '2026-01-12 18:19:50', 0.00, 0, '本次考试共 1 题，其中客观题 0 题。\n客观题正确数：0/0，正确率：0.0%\n总得分：0.0 分，平均每题得分：0.00 分\n答题时长：0 分钟，切换题目次数：0 次\n\nAI 批改反馈：\n', '{}', '2026-01-12 15:19:50');
INSERT INTO `tb_exam_session` VALUES ('30d74422590ee4964113f5763d757b59', '1', '1', 1, NULL, '2026-01-11 23:51:26', '2026-01-12 02:45:00', 0.00, 0, '本次考试共 1 题，其中客观题 0 题。\n客观题正确数：0/0，正确率：0.0%\n总得分：0.0 分，平均每题得分：0.00 分\n答题时长：0 分钟，切换题目次数：0 次\n\nAI 批改反馈：\n', '{}', '2026-01-11 23:45:00');
INSERT INTO `tb_exam_session` VALUES ('368fbf4dfad184c4ea5147da86ee2d13', '1', 'b591348d2de9a6de94466e91e5ef60e4', 1, '2026-01-23 02:40:09', '2026-01-23 02:48:21', '2026-01-23 05:40:09', 0.00, 2, '本次考试共 7 题，其中客观题 0 题。\n客观题正确数：0/0，正确率：0.0%\n总得分：0.0 分，平均每题得分：0.00 分\n答题时长：8 分钟，切换题目次数：2 次\n\nAI 批改反馈：\n', '{}', '2026-01-23 02:40:09');
INSERT INTO `tb_exam_session` VALUES ('3733fc84f24aacf3620c6d8d66989b6c', '1', 'b591348d2de9a6de94466e91e5ef60e4', 1, '2026-01-14 16:04:57', '2026-01-14 16:05:27', '2026-01-14 19:04:57', 5.00, 1, '本次考试共 7 题，其中客观题 1 题。\n客观题正确数：1/1，正确率：100.0%\n总得分：5.0 分，平均每题得分：0.71 分\n答题时长：0 分钟，切换题目次数：1 次\n\nAI 批改反馈：\n', '{\"14\": \"A\"}', '2026-01-14 16:04:57');
INSERT INTO `tb_exam_session` VALUES ('4406bf830e116a2fd0f1d8053de06e55', '1', '1', 1, '2026-01-12 00:10:49', '2026-01-12 00:10:56', '2026-01-12 03:10:49', 0.00, 0, '本次考试共 1 题，其中客观题 0 题。\n客观题正确数：0/0，正确率：0.0%\n总得分：0.0 分，平均每题得分：0.00 分\n答题时长：0 分钟，切换题目次数：0 次\n\nAI 批改反馈：\n', '{}', '2026-01-12 00:10:49');
INSERT INTO `tb_exam_session` VALUES ('46b13d326aa0bfb09ec431e1e0c3e79c', '1', '1', 1, NULL, '2026-01-11 23:44:54', '2026-01-12 02:44:51', 0.00, 0, '本次考试共 1 题，其中客观题 0 题。\n客观题正确数：0/0，正确率：0.0%\n总得分：0.0 分，平均每题得分：0.00 分\n答题时长：0 分钟，切换题目次数：0 次\n\nAI 批改反馈：\n', '{}', '2026-01-11 23:44:51');
INSERT INTO `tb_exam_session` VALUES ('4b5a623ef31b1141c59dc0679d429837', '1', '1', 1, NULL, '2026-01-11 23:49:59', '2026-01-12 02:48:08', 0.00, 0, '本次考试共 1 题，其中客观题 0 题。\n客观题正确数：0/0，正确率：0.0%\n总得分：0.0 分，平均每题得分：0.00 分\n答题时长：0 分钟，切换题目次数：0 次\n\nAI 批改反馈：\n', '{}', '2026-01-11 23:48:08');
INSERT INTO `tb_exam_session` VALUES ('8aa8a904e166bf868ffe0d9ec8a73c33', '1', '1', 1, NULL, '2026-01-11 23:51:31', '2026-01-12 02:40:13', 0.00, 0, '本次考试共 1 题，其中客观题 0 题。\n客观题正确数：0/0，正确率：0.0%\n总得分：0.0 分，平均每题得分：0.00 分\n答题时长：0 分钟，切换题目次数：0 次\n\nAI 批改反馈：\n', '{}', '2026-01-11 23:40:13');
INSERT INTO `tb_exam_session` VALUES ('9ea10cba2f2afd7dd738167e85b69aff', '1', 'b591348d2de9a6de94466e91e5ef60e4', 1, '2026-01-14 16:02:52', '2026-01-14 16:03:12', '2026-01-14 19:02:52', 0.00, 0, '本次考试共 7 题，其中客观题 0 题。\n客观题正确数：0/0，正确率：0.0%\n总得分：0.0 分，平均每题得分：0.00 分\n答题时长：0 分钟，切换题目次数：0 次\n\nAI 批改反馈：\n', '{}', '2026-01-14 16:02:52');
INSERT INTO `tb_exam_session` VALUES ('9ec4ef3818110e7dc41249f62fdc6c77', '1', 'b591348d2de9a6de94466e91e5ef60e4', 1, '2026-01-14 14:27:43', '2026-01-14 14:49:12', '2026-01-14 17:27:43', 10.00, 1, '本次考试共 7 题，其中客观题 2 题。\n客观题正确数：2/2，正确率：100.0%\n总得分：10.0 分，平均每题得分：1.43 分\n答题时长：21 分钟，切换题目次数：1 次\n\nAI 批改反馈：\n', '{\"1\": \"B\", \"11\": \"A\", \"12\": \"C\", \"13\": \"C\", \"14\": \"C\"}', '2026-01-14 14:27:43');
INSERT INTO `tb_exam_session` VALUES ('9ed7c6d665827fbd814ea089db4d50d7', '1', '1', 1, NULL, '2026-01-11 23:44:14', '2026-01-12 02:44:05', 0.00, 0, '本次考试共 1 题，其中客观题 0 题。\n客观题正确数：0/0，正确率：0.0%\n总得分：0.0 分，平均每题得分：0.00 分\n答题时长：0 分钟，切换题目次数：0 次\n\nAI 批改反馈：\n', '{}', '2026-01-11 23:44:05');
INSERT INTO `tb_exam_session` VALUES ('c179a69071c02f290094fd6039ec1fb9', '1', 'b591348d2de9a6de94466e91e5ef60e4', 1, '2026-01-14 15:50:07', '2026-01-14 16:00:55', '2026-01-14 18:50:07', 10.00, 1, '本次考试共 7 题，其中客观题 2 题。\n客观题正确数：2/2，正确率：100.0%\n总得分：10.0 分，平均每题得分：1.43 分\n答题时长：10 分钟，切换题目次数：1 次\n\nAI 批改反馈：\n', '{\"1\": \"A\", \"11\": \"A\", \"12\": \"A\", \"13\": \"A\", \"14\": \"A\"}', '2026-01-14 15:50:07');
INSERT INTO `tb_exam_session` VALUES ('c59751250e10736b0b70c8dc255e1857', '1', '1', 1, NULL, '2026-01-11 23:44:47', '2026-01-12 02:44:19', 0.00, 0, '本次考试共 1 题，其中客观题 0 题。\n客观题正确数：0/0，正确率：0.0%\n总得分：0.0 分，平均每题得分：0.00 分\n答题时长：0 分钟，切换题目次数：0 次\n\nAI 批改反馈：\n', '{}', '2026-01-11 23:44:19');
INSERT INTO `tb_exam_session` VALUES ('ed829d294e7b5e7d9a86f1e071c7891e', '1', '1', 1, NULL, '2026-01-11 23:51:37', '2026-01-12 02:39:14', 0.00, 0, '本次考试共 1 题，其中客观题 0 题。\n客观题正确数：0/0，正确率：0.0%\n总得分：0.0 分，平均每题得分：0.00 分\n答题时长：0 分钟，切换题目次数：0 次\n\nAI 批改反馈：\n', '{}', '2026-01-11 23:39:14');
INSERT INTO `tb_exam_session` VALUES ('f387dd3a6ce57b3794f5cf1f588ec351', '1', '1', 1, '2026-01-12 00:11:00', '2026-01-12 00:12:02', '2026-01-12 03:11:00', 0.00, 0, '本次考试共 1 题，其中客观题 0 题。\n客观题正确数：0/0，正确率：0.0%\n总得分：0.0 分，平均每题得分：0.00 分\n答题时长：0 分钟，切换题目次数：0 次\n\nAI 批改反馈：\n', '{}', '2026-01-12 00:11:00');
INSERT INTO `tb_exam_session` VALUES ('f7b3c8f7be5eff2db4a3b5e379070389', '1', '1', 1, '2026-01-12 00:12:13', '2026-01-12 00:12:19', '2026-01-12 03:12:13', 0.00, 0, '本次考试共 1 题，其中客观题 0 题。\n客观题正确数：0/0，正确率：0.0%\n总得分：0.0 分，平均每题得分：0.00 分\n答题时长：0 分钟，切换题目次数：0 次\n\nAI 批改反馈：\n', '{}', '2026-01-12 00:12:13');

-- ----------------------------
-- Table structure for tb_mistake_record
-- ----------------------------
DROP TABLE IF EXISTS `tb_mistake_record`;
CREATE TABLE `tb_mistake_record`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '用户ID',
  `question_id` int NOT NULL COMMENT '题目ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `error_count` int NULL DEFAULT NULL COMMENT '错误次数',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_mistake_record
-- ----------------------------
INSERT INTO `tb_mistake_record` VALUES (1, 1, 4, '2026-01-08 14:59:50', 2, '2026-01-09 21:12:38');
INSERT INTO `tb_mistake_record` VALUES (2, 1, 5, '2026-01-08 15:00:10', 2, '2026-01-09 21:12:49');
INSERT INTO `tb_mistake_record` VALUES (3, 1, 6, '2026-01-08 15:00:20', 1, '2026-01-08 15:00:21');
INSERT INTO `tb_mistake_record` VALUES (4, 1, 7, '2026-01-08 15:00:22', 1, '2026-01-08 15:00:23');
INSERT INTO `tb_mistake_record` VALUES (5, 1, 1, '2026-01-08 15:00:52', 3, '2026-01-08 15:01:00');
INSERT INTO `tb_mistake_record` VALUES (6, 1, 9, '2026-01-08 15:01:50', 1, '2026-01-08 15:01:50');

-- ----------------------------
-- Table structure for tb_paper
-- ----------------------------
DROP TABLE IF EXISTS `tb_paper`;
CREATE TABLE `tb_paper`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '试卷标题',
  `year` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '考试年份',
  `exam_spec_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联科目',
  `total_score` int NULL DEFAULT 150 COMMENT '总分',
  `time_limit` int NULL DEFAULT 180 COMMENT '限时',
  `paper_type` tinyint NULL DEFAULT NULL COMMENT '0:真题 1:模拟卷',
  `structure_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '存储试卷结构及灵活分值。\r\n[{\"name\":\"二、解答题\",\"start\":17,\"end\":22,\"default_score\":12,\"exceptions\":{\"17\":10}}]',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_paper
-- ----------------------------
INSERT INTO `tb_paper` VALUES ('4369cd0a6ab884609b17ba2f3c5558fc', '2025年全国硕士研究生招生考试数学（一）真题', '2025', '4', 150, 180, 0, NULL, '2026-01-13 02:04:37');
INSERT INTO `tb_paper` VALUES ('b591348d2de9a6de94466e91e5ef60e4', '2025年全国硕士研究生招生考试数学（二）真题', '2025', '5', 150, 180, 0, NULL, '2026-01-14 14:00:34');
INSERT INTO `tb_paper` VALUES ('fc732cc5a9f6a05477ef1f6b9758759c', '2025年全国硕士研究生招生考试数学（三）真题', '2025', '6', 150, 180, 0, NULL, '2026-01-14 14:00:48');

-- ----------------------------
-- Table structure for tb_question
-- ----------------------------
DROP TABLE IF EXISTS `tb_question`;
CREATE TABLE `tb_question`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` tinyint NOT NULL COMMENT '题目类型: \r\n 1: 单选题\r\n 2: 多选题\r\n 3: 填空题\r\n 4: 综合应用题（408、数学、政治的大题）\r\n 5：完型填空\r\n 6：阅读理解\r\n 7：新题型\r\n 8：翻译题\r\n 9：小作文\r\n 10：大作文',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '题干内容',
  `options` json NULL COMMENT '选项(JSON存储: [\"A.xx\", \"B.xx\"])',
  `answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '正确答案',
  `analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '解析',
  `difficulty` tinyint NULL DEFAULT 1 COMMENT '难度: 1-5',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `tags` json NULL COMMENT '题目标签',
  `source` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '题目来源/习题集名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '题目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_question
-- ----------------------------
INSERT INTO `tb_question` VALUES (1, 1, '设函数$z = z(x, y)$由$z + \\ln z - \\int_{y}^{x} \\mathrm{e}^{-t^2} \\mathrm{d}t = 0$确定，则$\\frac{\\partial z}{\\partial x} + \\frac{\\partial z}{\\partial y} = ( \\quad )$', '[\"$\\\\frac{z}{z + 1}(\\\\mathrm{e}^{-x^2} - \\\\mathrm{e}^{-y^2})$\", \"$\\\\frac{z}{z + 1}(\\\\mathrm{e}^{-x^2} + \\\\mathrm{e}^{-y^2})$\", \"$-\\\\frac{z}{z + 1}(\\\\mathrm{e}^{-x^2} - \\\\mathrm{e}^{-y^2})$\", \"$-\\\\frac{z}{z + 1}(\\\\mathrm{e}^{-x^2} + \\\\mathrm{e}^{-y^2})$\"]', 'A', '选A', 2, '2026-01-08 14:45:00', '[\"考研真题\", \"基础\"]', NULL);
INSERT INTO `tb_question` VALUES (2, 1, '“芳林新叶催陈叶，流水前波让后波。”这句诗蕴含的哲理是（ ）', '[\"发展的实质是新事物的产生和旧事物的灭亡\", \"事物的发展是量变和质变的统一\", \"矛盾是事物发展的动力\", \"意识具有能动反作用\"]', 'A', '“新叶”和“后波”代表新事物，“陈叶”和“前波”代表旧事物。诗句强调了新陈代谢是自然界不可抗拒的规律，体现了发展的本质是新事物代替旧事物。', 3, '2026-01-08 14:49:52', '[]', NULL);
INSERT INTO `tb_question` VALUES (3, 1, '习近平总书记多次强调，我们要坚持“两点论”和“重点论”的统一。下列做法符合“重点论”的是（ ）', '[\"既看到改革的成就，又看到存在的问题\", \"在经济建设中，既要抓物质文明，又要抓精神文明\", \"解决当前矛盾时，要善于抓“牛鼻子”，解决主要矛盾\", \"统筹兼顾，不遗漏任何细节\"]', 'C', '“重点论”要求在认识复杂事物时着重把握主要矛盾，在认识某一矛盾时着重把握矛盾的主要方面。“抓牛鼻子”正是抓主要矛盾的体现。A、B属于“两点论”。', 3, '2026-01-08 14:50:34', '[]', NULL);
INSERT INTO `tb_question` VALUES (4, 1, '随着数字经济的发展，我国法律不断完善关于网络数据安全和个人隐私保护的规定。这说明（ ）', '[\"社会意识具有相对独立性\", \"社会存在决定社会意识\", \"生产力决定生产关系\", \"上层建筑决定经济基础\"]', 'B', '数字经济的发展（社会存在）促使了法律法规的修订（社会意识）。这体现了社会存在决定社会意识。D项表述错误，应为经济基础决定上层建筑。', 3, '2026-01-08 14:51:04', '[]', NULL);
INSERT INTO `tb_question` VALUES (5, 1, '经过长期努力，中国特色社会主义进入了新时代，我国社会主要矛盾已经转化为（ ）', '[\"人民日益增长的物质文化需要同落后的社会生产之间的矛盾\", \"人民日益增长的美好生活需要和不平衡不充分的发展之间的矛盾\", \"资产阶级和无产阶级之间的矛盾\", \"生产力与生产关系之间的矛盾\"]', 'B', 'B 是党的十九大作出的重要判断，是理解新时代中国特色社会主义的关键。', 3, '2026-01-08 14:51:51', '[]', NULL);
INSERT INTO `tb_question` VALUES (6, 1, 'The manager decided to ________ the meeting until next Monday due to the heavy rain.', '[\"put off\", \"put out\", \"put up\", \"put on\"]', 'A', 'A 解析： 考查 put 短语辨析。\n\nput off：推迟（符合语境，因为大雨推迟会议）。\n\nput out：熄灭、发布。\n\nput up：张贴、举起、建造。\n\nput on：穿上、上映。', 3, '2026-01-08 14:53:08', '[]', NULL);
INSERT INTO `tb_question` VALUES (7, 1, 'By the time he arrives in Beijing tomorrow, I ________ for two days.', '[\"will leave\", \"have left\", \"will have left\", \"left\"]', 'C', 'Beijing Beijing Beijing Beijing Beijing Beijing Beijing Beijing ', 3, '2026-01-08 14:54:26', '[]', NULL);
INSERT INTO `tb_question` VALUES (8, 1, '若一个栈的输入序列为 1, 2, 3, ..., n，输出序列的第一个元素是 n，则第 i 个输出元素是（ ）。', '[\"n-i\", \"n-i+1\", \"i\", \"不确定\"]', 'B', '当第一个输出元素是 $n$ 时，说明 $1$ 到 $n$ 已全部按序入栈。根据栈的“后进先出”（LIFO）特性，出栈序列必须是输入序列的逆序，即 $n, n-1, n-2, ..., 1$。第 $i$ 个出栈的元素应该是 $n-(i-1) = n-i+1$。', 3, '2026-01-08 14:56:14', '[]', NULL);
INSERT INTO `tb_question` VALUES (9, 1, '假设某计算机的指令流水线由取指（IF）、译码（ID）、执行（EX）、写回（WB）四个过程段组成。若各段运行时间均为 $\\Delta t$，则连续执行 $n$ 条指令所需的时间是（ ）。', '[\"$n \\\\Delta t$\", \"$(n+2) \\\\Delta t$\", \"$(n+3) \\\\Delta t$\", \"$4n \\\\Delta t$\"]', 'C', '流水线执行时间公式为：$T = (k + n - 1) \\Delta t$，其中 $k$ 为流水线段数，$n$ 为指令数。此处 $k=4$，所以 $T = (4 + n - 1) \\Delta t = (n+3) \\Delta t$。', 3, '2026-01-08 14:58:06', '[]', NULL);
INSERT INTO `tb_question` VALUES (10, 1, '在进程管理中，从就绪状态到运行状态的转变是由（ ）完成的。', '[\"作业调度\", \"进程调度\", \"中断处理\", \"用户自己\"]', 'B', '作业调度（低级调度除外的高级调度）负责从后备队列选作业进内存；进程调度（短程调度）负责从就绪队列选一个进程占用CPU。从就绪到运行是典型的进程调度行为。', 3, '2026-01-08 14:59:02', '[]', NULL);
INSERT INTO `tb_question` VALUES (11, 1, '已知函数 $f(x)=\\int_{0}^{x} \\mathrm{e}^{t^{2}} \\sin t \\mathrm{d}t, g(x)=\\int_{0}^{x} \\mathrm{e}^{t^{2}} \\mathrm{d}t \\cdot \\sin^{2}x$, 则( )', '[\"$x=0$ 是 $f(x)$ 的极值点, 也是 $g(x)$ 的极值点\", \"$x=0$ 是 $f(x)$ 的极值点, $(0,0)$ 是曲线 $y=g(x)$ 的拐点\", \"$x=0$ 是 $f(x)$ 的极值点, $(0,0)$ 是曲线 $y=f(x)$ 的拐点\", \"$(0,0)$ 是曲线 $y=f(x)$ 的拐点, 也是曲线 $y=g(x)$ 的拐点\"]', 'B', 'B~', 3, '2026-01-14 14:06:21', '[\"考研真题\"]', NULL);
INSERT INTO `tb_question` VALUES (12, 1, '如果对微分方程 $y\'\' - 2a y\' + (a + 2)y = 0$ 的任一解 $y(x)$，反常积分 $\\int_0^{+\\infty} y(x) dx$ 均收敛，那么 $a$ 的取值范围是( )', '[\"$(-2, -1]$\", \"$(-\\\\infty, -1]$\", \"$(-2, 0)$\", \"$(-\\\\infty, 0)$\"]', 'C', 'CCCCCC', 3, '2026-01-14 14:07:16', '[\"考研真题\"]', NULL);
INSERT INTO `tb_question` VALUES (13, 1, '设函数$f(x),g(x)$在$x=0$的某去心邻域内有定义且恒不为零,若当$x\\to 0$时,$f(x)$是$g(x)$的高阶无穷小,则当$x\\to 0$时( )', '[\"$f(x)+g(x)=o(g(x))$\", \"$f(x)g(x)=o(f^2(x))$\", \"$f(x)=o(e^{g(x)}-1)$\", \"$f(x)=o(g^2(x))$\"]', 'C', '选Ccccc', 3, '2026-01-14 14:10:04', '[\"考研真题\"]', NULL);
INSERT INTO `tb_question` VALUES (14, 1, '设函数$f(x,y)$连续，则$\\int_{-2}^{2} \\mathrm{d}x \\int_{4 - x^2}^{4} f(x,y) \\mathrm{d}y = (  )$', '[\"$\\\\\\\\int_{0}^{4} \\\\\\\\left[ \\\\\\\\int_{-2}^{-\\\\\\\\sqrt{4 - y}} f(x,y)\\\\\\\\mathrm{d}x + \\\\\\\\int_{\\\\\\\\sqrt{4 - y}}^{2} f(x,y)\\\\\\\\mathrm{d}x \\\\\\\\right] \\\\\\\\mathrm{d}y$\", \"$\\\\\\\\int_{0}^{4} \\\\\\\\left[ \\\\\\\\int_{-2}^{\\\\\\\\sqrt{4 - y}} f(x,y)\\\\\\\\mathrm{d}x + \\\\\\\\int_{\\\\\\\\sqrt{4 - y}}^{2} f(x,y)\\\\\\\\mathrm{d}x \\\\\\\\right] \\\\\\\\mathrm{d}y$\", \"$\\\\\\\\int_{0}^{4} \\\\\\\\left[ \\\\\\\\int_{-2}^{-\\\\\\\\sqrt{4 - y}} f(x,y)\\\\\\\\mathrm{d}x + \\\\\\\\int_{2}^{\\\\\\\\sqrt{4 - y}} f(x,y)\\\\\\\\mathrm{d}x \\\\\\\\right] \\\\\\\\mathrm{d}y$\", \"$\\\\\\\\int_{0}^{4} \\\\\\\\mathrm{d}y \\\\\\\\int_{\\\\\\\\sqrt{4 - y}}^{2} f(x,y)\\\\\\\\mathrm{d}x$\"]', 'A', '', 3, '2026-01-14 14:12:01', '[\"考研真题\"]', NULL);
INSERT INTO `tb_question` VALUES (15, 3, '设$\\int_{1}^{+\\infty} \\frac{a}{x(2x+a)}\\mathrm{d}x=\\ln 2$，则$a=$____.', '[\"\", \"\", \"\", \"\"]', '2', '$\\ln 2=\\int_{1}^{+\\infty} \\frac{a}{x(2x+a)}\\mathrm{d}x=2\\int_{1}^{+\\infty} \\frac{a}{2x(2x+a)}\\mathrm{d}x=\\int_{1}^{+\\infty}\\left(\\frac{1}{2x}-\\frac{1}{2x+a}\\right)\\mathrm{d}(2x)=\\ln \\frac{2x}{2x+a}\\bigg|_{1}^{+\\infty}=-\\ln \\frac{2}{2+a}=\\ln\\left(1+\\frac{a}{2}\\right)$，从而$2=1+\\frac{a}{2}$，解得$a=2$', 3, '2026-01-14 14:24:43', '[\"考研真题\"]', NULL);
INSERT INTO `tb_question` VALUES (16, 4, '计算$\\int_{0}^{1} \\frac{1}{(x+1)(x^2 - 2x + 2)} \\mathrm{d}x$。', '[\"\", \"\", \"\", \"\"]', '$\\frac{1}{5}\\left( \\frac{3}{2}\\ln 2 + \\frac{\\pi}{2} \\right)$', '【解】$\\int_{0}^{1} \\frac{1}{(x+1)(x^2 - 2x + 2)} \\mathrm{d}x = \\int_{0}^{1} \\frac{1}{(x+1)\\left[(x-1)^2 + 1\\right]} \\mathrm{d}x \\stackrel{x-1=t}{=} \\int_{-1}^{0} \\frac{1}{(t+2)(t^2 + 1)} \\mathrm{d}t = \\frac{1}{5}\\int_{-1}^{0} \\left( \\frac{1}{t+2} + \\frac{-t + 2}{t^2 + 1} \\right) \\mathrm{d}t = \\frac{1}{5}\\left[ \\ln(t+2) - \\frac{1}{2}\\ln(t^2 + 1) + 2\\arctan t \\right]_{-1}^{0} = \\frac{1}{5}\\left[ \\ln 2 - \\left( -\\frac{1}{2}\\ln 2 - \\frac{\\pi}{2} \\right) \\right] = \\frac{1}{5}\\left( \\frac{3}{2}\\ln 2 + \\frac{\\pi}{2} \\right)$', 3, '2026-01-14 14:27:05', '[\"考研真题\"]', NULL);
INSERT INTO `tb_question` VALUES (17, 1, '为解决计算机主机与打印机之间速度不匹配问题，通常设置个打印数据缓冲区，主机将要输出的数据依次写该缓冲区，打印机则依次从该缓冲区中取出数据。该缓冲区的逻辑结构应该是。', '[\"A. 栈\", \"B. 队列\", \"C. 树\", \"D. 图\"]', 'B', '打印数据缓冲区需要按照先进先出的原则进行数据访问，因此其逻辑结构应该是队列。', 1, '2026-01-17 19:38:54', '[\"数据结构\", \"队列\"]', NULL);
INSERT INTO `tb_question` VALUES (18, 1, '设栈S和队列Q的初始状态均为空，元素a,b,c,d,e,f,g依次进入栈S。若每个元素出栈后即进入队列Q，且7个元素出队的顺序是b,d,c,f,e,a,g，则栈S的容量至少是。', '[\"A. 1\", \"B. 2\", \"C. 3\", \"D. 4\"]', 'C', '根据出队顺序，可以推断出栈的最大容量为3，因为元素c在元素d之前出栈，说明栈中至少有c,d,e三个元素。', 1, '2026-01-17 19:38:54', '[\"数据结构\", \"栈\", \"队列\"]', NULL);
INSERT INTO `tb_question` VALUES (19, 1, '给定叉树如右图所。设N代表叉树的根，L代表根结点的左树，R代表根结点的右树。若遍历后的结点序列是3,1,7,5,6,2,4，则其遍历式是。', '[\"A. LRN\", \"B. NRL\", \"C. RLN\", \"D. RNL\"]', 'A', '根据遍历序列，可以确定遍历顺序为先左子树后右子树，因此遍历式为LRN。', 1, '2026-01-17 19:38:54', '[\"数据结构\", \"树\", \"遍历\"]', NULL);
INSERT INTO `tb_question` VALUES (20, 1, '下列叉排序树中，满平衡叉树定义的是。', '[\"A.\", \"B.\", \"C.\", \"D.\"]', 'A', '满平衡叉树是指每个节点要么没有子节点，要么有两个子节点，并且左右子树的高度差不超过1。', 1, '2026-01-17 19:38:54', '[\"数据结构\", \"树\", \"平衡树\"]', NULL);
INSERT INTO `tb_question` VALUES (21, 1, '已知棵完全叉树的第6层（设根为第1层）有8个叶结点，则该完全叉树的结点个数最多是。', '[\"A. 39\", \"B. 52\", \"C. 111\", \"D. 119\"]', 'D', '完全叉树的第6层有8个叶节点，说明第5层有4个节点，以此类推，第1层有1个节点，因此总节点数为1+2+4+8+16+32+64=127。', 1, '2026-01-17 19:38:54', '[\"数据结构\", \"树\", \"完全叉树\"]', NULL);
INSERT INTO `tb_question` VALUES (22, 1, '设函数 f(x) = x³ - 3x + 1，求 f\'(x)', '[\"A. 3x² - 3\", \"B. 3x² + 3\", \"C. x² - 3\", \"D. x² + 3\"]', 'A', '根据求导法则，f\'(x) = 3x² - 3', 1, '2026-01-22 21:27:41', '[\"导数\", \"基础题\"]', '高等数学例题');
INSERT INTO `tb_question` VALUES (23, 2, '下列哪些函数在区间 (0, +∞) 上单调递增？', '[\"A. f(x) = x²\", \"B. f(x) = eˣ\", \"C. f(x) = ln(x)\", \"D. f(x) = 1/x\"]', 'ABC', 'x²在x>0时单调递增；eˣ始终单调递增；ln(x)在定义域内单调递增；1/x在x>0时单调递减', 1, '2026-01-22 21:27:41', '[\"单调性\", \"多选题\"]', NULL);

-- ----------------------------
-- Table structure for tb_resource
-- ----------------------------
DROP TABLE IF EXISTS `tb_resource`;
CREATE TABLE `tb_resource`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资料标题',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '下载/预览地址',
  `file_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'pdf' COMMENT '文件类型',
  `subject_id` int NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '学习资料表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_resource
-- ----------------------------

-- ----------------------------
-- Table structure for tb_subject
-- ----------------------------
DROP TABLE IF EXISTS `tb_subject`;
CREATE TABLE `tb_subject`  (
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
) ENGINE = InnoDB AUTO_INCREMENT = 62 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '科目分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_subject
-- ----------------------------
INSERT INTO `tb_subject` VALUES (1, '政治', 0, NULL, 10, 1, 0, NULL, '1,2,4');
INSERT INTO `tb_subject` VALUES (2, '英语一', 0, NULL, 20, 1, 0, NULL, '5,6,7,8,9,10');
INSERT INTO `tb_subject` VALUES (3, '英语二', 0, NULL, 30, 1, 0, NULL, '5,6,7,8,9,10');
INSERT INTO `tb_subject` VALUES (4, '数学一', 0, NULL, 40, 1, 0, NULL, '1,3,4');
INSERT INTO `tb_subject` VALUES (5, '数学二', 0, NULL, 50, 1, 0, NULL, '1,3,4');
INSERT INTO `tb_subject` VALUES (6, '数学三', 0, NULL, 60, 1, 0, NULL, '1,3,4');
INSERT INTO `tb_subject` VALUES (7, '408', 0, NULL, 70, 1, 0, NULL, '1,4');
INSERT INTO `tb_subject` VALUES (8, '马原', 1, NULL, 10, 2, 0, NULL, NULL);
INSERT INTO `tb_subject` VALUES (9, '毛中特', 1, NULL, 20, 2, 0, '', NULL);
INSERT INTO `tb_subject` VALUES (10, '史纲', 1, NULL, 30, 2, 0, NULL, NULL);
INSERT INTO `tb_subject` VALUES (11, '思修法基', 1, NULL, 40, 2, 0, NULL, NULL);
INSERT INTO `tb_subject` VALUES (12, '时政', 1, NULL, 50, 2, 0, NULL, NULL);
INSERT INTO `tb_subject` VALUES (13, '完形填空', 0, NULL, 10, 2, 0, '2,3', NULL);
INSERT INTO `tb_subject` VALUES (14, '阅读理解', 0, NULL, 20, 2, 0, '2,3', NULL);
INSERT INTO `tb_subject` VALUES (15, '新题型', 0, NULL, 30, 2, 0, '2,3', NULL);
INSERT INTO `tb_subject` VALUES (16, '翻译', 0, NULL, 40, 2, 0, '2,3', NULL);
INSERT INTO `tb_subject` VALUES (17, '小作文', 0, NULL, 50, 2, 0, '2,3', NULL);
INSERT INTO `tb_subject` VALUES (18, '大作文', 0, NULL, 60, 2, 0, '2,3', NULL);
INSERT INTO `tb_subject` VALUES (19, '高等数学', 0, NULL, 10, 2, 0, '4,5,6', NULL);
INSERT INTO `tb_subject` VALUES (20, '线性代数', 0, NULL, 20, 2, 0, '4,5,6', NULL);
INSERT INTO `tb_subject` VALUES (21, '概率论与数理统计', 0, NULL, 30, 2, 0, '4,6', NULL);
INSERT INTO `tb_subject` VALUES (22, '数据结构', 7, NULL, 10, 2, 0, NULL, NULL);
INSERT INTO `tb_subject` VALUES (23, '计算机组成原理', 7, NULL, 20, 2, 0, NULL, NULL);
INSERT INTO `tb_subject` VALUES (24, '操作系统', 7, NULL, 30, 2, 0, NULL, NULL);
INSERT INTO `tb_subject` VALUES (25, '计算机网络', 7, NULL, 40, 2, 0, NULL, NULL);
INSERT INTO `tb_subject` VALUES (26, '函数、极限、连续', 19, NULL, 10, 3, 0, '4,5,6', NULL);
INSERT INTO `tb_subject` VALUES (27, '一元微分学', 19, NULL, 20, 3, 0, '4,5,6', NULL);
INSERT INTO `tb_subject` VALUES (28, '一元积分学', 19, NULL, 30, 3, 0, '4,5,6', NULL);
INSERT INTO `tb_subject` VALUES (29, '反常积分', 19, NULL, 40, 3, 0, '4,5,6', NULL);
INSERT INTO `tb_subject` VALUES (30, '微分方程', 19, NULL, 50, 3, 0, '4,5,6', NULL);
INSERT INTO `tb_subject` VALUES (31, '多元微分学', 19, NULL, 60, 3, 0, '4,5,6', NULL);
INSERT INTO `tb_subject` VALUES (32, '二重积分', 19, NULL, 70, 3, 0, '4,5,6', NULL);
INSERT INTO `tb_subject` VALUES (33, '无穷级数', 19, NULL, 70, 3, 0, '4,6', NULL);
INSERT INTO `tb_subject` VALUES (34, '行列式', 20, NULL, 10, 3, 0, '4,5,6', NULL);
INSERT INTO `tb_subject` VALUES (35, '矩阵', 20, NULL, 20, 3, 0, '4,5,6', NULL);
INSERT INTO `tb_subject` VALUES (36, '向量', 20, NULL, 30, 3, 0, '4,5,6', NULL);
INSERT INTO `tb_subject` VALUES (37, '线性方程组', 20, NULL, 40, 3, 0, '4,5,6', NULL);
INSERT INTO `tb_subject` VALUES (38, '矩阵的特征值和特征向量', 20, NULL, 50, 3, 0, '4,5,6', NULL);
INSERT INTO `tb_subject` VALUES (39, '二次型', 20, NULL, 60, 3, 0, '4,5,6', NULL);
INSERT INTO `tb_subject` VALUES (40, '随机事件和概率', 21, NULL, 10, 3, 0, '4,6', NULL);
INSERT INTO `tb_subject` VALUES (41, '一维随机变量及其分布', 21, NULL, 20, 3, 0, '4,6', NULL);
INSERT INTO `tb_subject` VALUES (42, '多维随机变量及其分布', 21, NULL, 30, 3, 0, '4,6', NULL);
INSERT INTO `tb_subject` VALUES (43, '随机变量的数字特征', 21, NULL, 40, 3, 0, '4,6', NULL);
INSERT INTO `tb_subject` VALUES (44, '大数定律和中心极限定理', 21, NULL, 50, 3, 0, '4,6', NULL);
INSERT INTO `tb_subject` VALUES (45, '数理统计的基本概念', 21, NULL, 60, 3, 0, '4,6', NULL);
INSERT INTO `tb_subject` VALUES (46, '参数估计与假设检验', 21, NULL, 70, 3, 0, '4', NULL);
INSERT INTO `tb_subject` VALUES (47, '数列敛散性的判定', 26, NULL, 10, 4, 0, '4,6', NULL);
INSERT INTO `tb_subject` VALUES (48, '函数极限的计算', 26, NULL, 20, 4, 0, '4,6', NULL);
INSERT INTO `tb_subject` VALUES (49, '无穷小量', 26, NULL, 30, 4, 0, '4,6', NULL);
INSERT INTO `tb_subject` VALUES (50, '确定极限中的参数', 26, NULL, 40, 4, 0, '4,6', NULL);
INSERT INTO `tb_subject` VALUES (51, '函数的连续性与间断点的类型', 26, NULL, 50, 4, 0, '4,6', NULL);

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`  (
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
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO `tb_user` VALUES (1, 'admin', '$2a$10$YdQ2N6TgMkqfyDRgIVg8cupiVypv6anq38bSWEJ3s8xb.9BaMNFcm', '13345932175', 'admin@example.com', '最大的管理员', 'admin', 'http://localhost:8081/uploads/d1bd0b2c-8a10-47bb-b072-a868e985138f.jpg', '清华大学', 410, '27考研', '政治,英语二,数学一', '2025-12-30 17:15:11', '2026-01-06 22:45:39');
INSERT INTO `tb_user` VALUES (2, '1123', '$2a$10$e0fDspBIiWCtLVyxwUeDL.FK2P4JKgmmZtzOAiX1pvYXab2scl30C', '13329333794', '1123@example.com', '1123', 'student', 'http://localhost:8081/uploads/d1bd0b2c-8a10-47bb-b072-a868e985138f.jpg', '山东大学', 360, '27考研', '政治,英语二,数学二', '2026-01-01 00:58:22', '2026-01-06 22:34:50');
INSERT INTO `tb_user` VALUES (3, 'admin121e', '$2a$10$yOUbU7h.XqjLGulAZABdae.p3/KwPH5o3KKH23nbKM9BP.8LHlCsa', '13508872333', 'admin121e@example.com', '贝利亚', 'student', 'http://localhost:8081/uploads/d1bd0b2c-8a10-47bb-b072-a868e985138f.jpg', '东北大学', 360, '27考研', '政治,英语二,数学二', '2026-01-01 01:18:43', '2026-01-06 22:34:52');
INSERT INTO `tb_user` VALUES (4, '123123', '$2a$10$Cqmo1yeF6tojwm9/ktr.KuDmgtwa6OQ/y9qw3afN5rlUCPeXp6G22', '13556361010', '123123@example.com', '123123', 'student', 'http://localhost:8081/uploads/b4cc5155-ec36-4c27-adc5-0761ba180b6e.jpg', '北京大学', 400, '27考研', '政治,英语一,数学一', '2026-01-01 02:06:02', '2026-01-06 22:34:53');
INSERT INTO `tb_user` VALUES (5, '33223', '$2a$10$PC6I3rthE/1N6qj7y/OPGOUXqkSb2kErBKl2G8iGNlZwuK4YPlrDm', '13255187661', '33223@example.com', 'e12e', 'student', 'http://localhost:8081/uploads/b4cc5155-ec36-4c27-adc5-0761ba180b6e.jpg', '广州大学', 330, '27考研', '政治,英语二,数学二', '2026-01-01 02:06:02', '2026-01-06 22:34:55');
INSERT INTO `tb_user` VALUES (6, '343434', '$2a$10$PC6I3rthE/1N6qj7y/OPGOUXqkSb2kErBKl2G8iGNlZwuK4YPlrDm', '13406855303', '343434@example.com', 'qeffqwe3f', 'student', 'http://localhost:8081/uploads/b4cc5155-ec36-4c27-adc5-0761ba180b6e.jpg', '上海大学', 340, '27考研', '政治,英语一,数学一', '2026-01-01 02:06:02', '2026-01-06 22:34:57');
INSERT INTO `tb_user` VALUES (7, '个如果sfwe', '$2a$10$PC6I3rthE/1N6qj7y/OPGOUXqkSb2kErBKl2G8iGNlZwuK4YPlrDm', '13268713561', '个如果sfwe@example.com', 'wqdq', 'student', 'http://localhost:8081/uploads/b4cc5155-ec36-4c27-adc5-0761ba180b6e.jpg', '苏州大学', 350, '27考研', '政治,英语二,数学二', '2026-01-01 02:06:02', '2026-01-06 22:34:58');
INSERT INTO `tb_user` VALUES (8, '131f', '$2a$10$PC6I3rthE/1N6qj7y/OPGOUXqkSb2kErBKl2G8iGNlZwuK4YPlrDm', '13923001923', '131f@example.com', 'sadvv', 'student', 'http://localhost:8081/uploads/b4cc5155-ec36-4c27-adc5-0761ba180b6e.jpg', '江南大学', 340, '27考研', '政治,英语二,数学二', '2026-01-01 02:06:02', '2026-01-06 22:34:59');
INSERT INTO `tb_user` VALUES (9, '啊微腐败', '$2a$10$PC6I3rthE/1N6qj7y/OPGOUXqkSb2kErBKl2G8iGNlZwuK4YPlrDm', '13108868962', '啊微腐败@example.com', 'wFE时DF', 'student', 'http://localhost:8081/uploads/b4cc5155-ec36-4c27-adc5-0761ba180b6e.jpg', '南昌大学', 340, '27考研', '政治,英语二,数学二', '2026-01-01 02:06:02', '2026-01-06 22:35:01');
INSERT INTO `tb_user` VALUES (10, '啊微去外地败', '$2a$10$PC6I3rthE/1N6qj7y/OPGOUXqkSb2kErBKl2G8iGNlZwuK4YPlrDm', '13375339067', '啊微去外地败@example.com', 'wFE时DF', 'student', 'http://localhost:8081/uploads/b4cc5155-ec36-4c27-adc5-0761ba180b6e.jpg', '广西大学', 330, '27考研', '政治,英语一,数学一', '2026-01-01 02:06:02', '2026-01-06 22:35:05');
INSERT INTO `tb_user` VALUES (11, '微分方程', '$2a$10$PC6I3rthE/1N6qj7y/OPGOUXqkSb2kErBKl2G8iGNlZwuK4YPlrDm', '13550088476', '微分方程@example.com', 'wFE时DF', 'student', 'http://localhost:8081/uploads/b4cc5155-ec36-4c27-adc5-0761ba180b6e.jpg', '云南大学', 340, '27考研', '政治,英语一,数学一', '2026-01-01 02:06:02', '2026-01-06 22:35:08');
INSERT INTO `tb_user` VALUES (12, 'usererername', '$2a$10$Zx6CQkJTGh043qd2S6uhEuwB/WzgjEOvQsaeFl6A5I6KE3ijfkw.C', '13624425208', 'usererername@example.com', '汉武帝', 'student', 'http://localhost:8081/uploads/b4cc5155-ec36-4c27-adc5-0761ba180b6e.jpg', '深圳大学', 340, '27考研', '政治,英语一,数学一', '2026-01-06 22:03:48', '2026-01-06 22:35:11');

-- ----------------------------
-- Table structure for tb_user_progress
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_progress`;
CREATE TABLE `tb_user_progress`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `subject_id` int NOT NULL COMMENT '科目或考点ID',
  `finished_count` int NULL DEFAULT 0 COMMENT '该考点下已做题目数',
  `correct_count` int NULL DEFAULT 0 COMMENT '该考点下做对题目数',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_subject`(`user_id` ASC, `subject_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户知识点练习进度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_user_progress
-- ----------------------------
INSERT INTO `tb_user_progress` VALUES (1, 1, 1, 2, 0, '2026-01-09 21:12:38');
INSERT INTO `tb_user_progress` VALUES (2, 1, 8, 2, 0, '2026-01-09 21:12:38');
INSERT INTO `tb_user_progress` VALUES (3, 1, 9, 2, 0, '2026-01-09 21:12:49');
INSERT INTO `tb_user_progress` VALUES (4, 1, 2, 2, 0, '2026-01-08 15:00:23');
INSERT INTO `tb_user_progress` VALUES (5, 1, 3, 2, 0, '2026-01-08 15:00:23');
INSERT INTO `tb_user_progress` VALUES (6, 1, 4, 4, 1, '2026-01-08 15:01:10');
INSERT INTO `tb_user_progress` VALUES (7, 1, 5, 4, 1, '2026-01-08 15:01:10');
INSERT INTO `tb_user_progress` VALUES (8, 1, 6, 4, 1, '2026-01-08 15:01:10');
INSERT INTO `tb_user_progress` VALUES (9, 1, 19, 4, 1, '2026-01-08 15:01:10');
INSERT INTO `tb_user_progress` VALUES (10, 1, 7, 2, 1, '2026-01-08 15:01:50');
INSERT INTO `tb_user_progress` VALUES (11, 1, 22, 1, 1, '2026-01-08 15:01:33');
INSERT INTO `tb_user_progress` VALUES (12, 1, 23, 1, 0, '2026-01-08 15:01:50');

SET FOREIGN_KEY_CHECKS = 1;
