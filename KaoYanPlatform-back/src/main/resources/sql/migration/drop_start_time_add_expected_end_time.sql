-- 修改考试会话表结构
-- 作者: Claude
-- 日期: 2025-01-11
-- 说明: 添加 expected_end_time 字段用于计算考试倒计时，保留 start_time 作为考试开始时间

USE kaoyan_platform;

-- 1. 添加 expected_end_time 列
ALTER TABLE tb_exam_session ADD COLUMN IF NOT EXISTS expected_end_time DATETIME COMMENT '预期结束时间 = start_time + paper.time_limit，用于倒计时计算';

-- 2. 为现有数据补充 start_time（如果为空，使用 create_time）
UPDATE tb_exam_session
SET start_time = COALESCE(start_time, create_time)
WHERE start_time IS NULL;

-- 3. 为现有数据设置 expected_end_time（基于 start_time）
UPDATE tb_exam_session
SET expected_end_time = DATE_ADD(start_time, INTERVAL 180 MINUTE)
WHERE expected_end_time IS NULL;

-- 验证修改
SELECT
    id,
    user_id,
    paper_id,
    status,
    start_time,
    create_time,
    expected_end_time,
    submit_time
FROM tb_exam_session
ORDER BY create_time DESC
LIMIT 10;
