-- 清空旧数据
TRUNCATE TABLE map_subject_book;

-- 数学一：高数、线代、概率
INSERT INTO `map_subject_book` (book_id, subject_id) VALUES (4, 401), (4, 402), (4, 403);

-- 数学二：高数、线代
INSERT INTO `map_subject_book` (book_id, subject_id) VALUES (5, 401), (5, 402);

-- 数学三：高数、线代、概率
INSERT INTO `map_subject_book` (book_id, subject_id) VALUES (6, 401), (6, 402), (6, 403);