-- 考试会话表
CREATE TABLE IF NOT EXISTS `tb_exam_session` (
  `id` VARCHAR(36) NOT NULL COMMENT '会话ID（UUID）',
  `user_id` VARCHAR(36) NOT NULL COMMENT '用户ID',
  `paper_id` VARCHAR(36) NOT NULL COMMENT '试卷ID',
  `status` INT DEFAULT 0 COMMENT '状态：0-进行中，1-已提交',
  `start_time` DATETIME COMMENT '开始时间',
  `submit_time` DATETIME COMMENT '提交时间',
  `total_score` DECIMAL(10, 2) DEFAULT 0 COMMENT '总分',
  `switch_count` INT DEFAULT 0 COMMENT '切屏次数',
  `ai_summary` TEXT COMMENT 'AI总结',
  `snapshot_answers` TEXT COMMENT '答题快照（JSON格式）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_paper_id` (`paper_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试会话表';

-- 考试答题明细表
CREATE TABLE IF NOT EXISTS `tb_exam_answer_detail` (
  `id` VARCHAR(36) NOT NULL COMMENT '明细ID（UUID）',
  `session_id` VARCHAR(36) NOT NULL COMMENT '考试会话ID',
  `question_id` VARCHAR(36) NOT NULL COMMENT '题目ID',
  `user_answer` TEXT COMMENT '用户答案',
  `is_correct` INT DEFAULT 0 COMMENT '是否正确：0-错误，1-正确，2-待批改',
  `score_earned` DECIMAL(10, 2) DEFAULT 0 COMMENT '得分',
  `duration_seconds` INT COMMENT '用时（秒）',
  `ai_feedback` TEXT COMMENT 'AI反馈',
  `knowledge_point_id` VARCHAR(36) COMMENT '知识点ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_session_id` (`session_id`),
  INDEX `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试答题明细表';
