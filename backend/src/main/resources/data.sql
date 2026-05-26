-- 短视频平台测试数据
-- 数据库: gp2

-- 清空现有数据（按外键依赖顺序）
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE like_records;
TRUNCATE TABLE favorites;
TRUNCATE TABLE follows;
TRUNCATE TABLE comments;
TRUNCATE TABLE videos;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- ==================== 用户数据 ====================
-- 密码都是 123456，使用BCrypt加密后的值
INSERT INTO users (username, password, name, email, role, status, created_at, updated_at) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'admin@example.com', 'ADMIN', 'ACTIVE', NOW(), NOW()),
('zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张三', 'zhangsan@example.com', 'USER', 'ACTIVE', NOW(), NOW()),
('lisi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李四', 'lisi@example.com', 'USER', 'ACTIVE', NOW(), NOW()),
('wangwu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王五', 'wangwu@example.com', 'USER', 'ACTIVE', NOW(), NOW()),
('zhaoliu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '赵六', 'zhaoliu@example.com', 'USER', 'ACTIVE', NOW(), NOW()),
('xiaoming', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '小明', 'xiaoming@example.com', 'USER', 'ACTIVE', NOW(), NOW()),
('xiaohong', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '小红', 'xiaohong@example.com', 'USER', 'ACTIVE', NOW(), NOW()),
('xiaowang', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '小王', 'xiaowang@example.com', 'USER', 'ACTIVE', NOW(), NOW());

-- ==================== 视频数据 ====================
INSERT INTO videos (user_id, title, description, cover_url, video_url, duration, file_size, status, visibility, view_count, like_count, comment_count, share_count, favorite_count, created_at, updated_at) VALUES
-- 张三的视频
(2, '春日旅行Vlog', '记录春天的美好时光，樱花盛开的季节', '/uploads/covers/cover1.jpg', '/uploads/videos/video1.mp4', 120, 15728640, 'PUBLISHED', 'PUBLIC', 12580, 856, 128, 45, 234, DATE_SUB(NOW(), INTERVAL 30 DAY), NOW()),
(2, '美食制作教程', '教你做一道美味的红烧肉', '/uploads/covers/cover2.jpg', '/uploads/videos/video2.mp4', 180, 20971520, 'PUBLISHED', 'PUBLIC', 8920, 523, 89, 32, 156, DATE_SUB(NOW(), INTERVAL 25 DAY), NOW()),
(2, '健身打卡Day30', '坚持健身一个月的变化', '/uploads/covers/cover3.jpg', '/uploads/videos/video3.mp4', 90, 10485760, 'PUBLISHED', 'PUBLIC', 5630, 412, 56, 18, 98, DATE_SUB(NOW(), INTERVAL 20 DAY), NOW()),

-- 李四的视频
(3, '编程技巧分享', '10个提高编程效率的小技巧', '/uploads/covers/cover4.jpg', '/uploads/videos/video4.mp4', 240, 26214400, 'PUBLISHED', 'PUBLIC', 15680, 1234, 256, 78, 456, DATE_SUB(NOW(), INTERVAL 28 DAY), NOW()),
(3, 'AI人工智能入门', '零基础学习AI的第一课', '/uploads/covers/cover5.jpg', '/uploads/videos/video5.mp4', 300, 31457280, 'PUBLISHED', 'PUBLIC', 23450, 1890, 312, 95, 678, DATE_SUB(NOW(), INTERVAL 22 DAY), NOW()),
(3, 'Python实战项目', '用Python做一个爬虫项目', '/uploads/covers/cover6.jpg', '/uploads/videos/video6.mp4', 360, 36700160, 'PUBLISHED', 'PUBLIC', 18920, 1456, 198, 62, 534, DATE_SUB(NOW(), INTERVAL 15 DAY), NOW()),

-- 王五的视频
(4, '音乐翻唱合集', '翻唱热门歌曲精选', '/uploads/covers/cover7.jpg', '/uploads/videos/video7.mp4', 200, 22020096, 'PUBLISHED', 'PUBLIC', 34560, 2567, 423, 156, 890, DATE_SUB(NOW(), INTERVAL 18 DAY), NOW()),
(4, '吉他教学入门', '零基础学吉他第一课', '/uploads/covers/cover8.jpg', '/uploads/videos/video8.mp4', 150, 16777216, 'PUBLISHED', 'PUBLIC', 12340, 876, 145, 48, 312, DATE_SUB(NOW(), INTERVAL 12 DAY), NOW()),

-- 赵六的视频
(5, '搞笑日常', '生活中的搞笑瞬间', '/uploads/covers/cover9.jpg', '/uploads/videos/video9.mp4', 60, 7340032, 'PUBLISHED', 'PUBLIC', 45680, 3456, 567, 234, 1234, DATE_SUB(NOW(), INTERVAL 10 DAY), NOW()),
(5, '宠物萌宠', '我家猫咪的日常', '/uploads/covers/cover10.jpg', '/uploads/videos/video10.mp4', 45, 5242880, 'PUBLISHED', 'PUBLIC', 56780, 4567, 678, 289, 1567, DATE_SUB(NOW(), INTERVAL 8 DAY), NOW()),

-- 小明的视频
(6, '游戏精彩操作', '王者荣耀五杀集锦', '/uploads/covers/cover11.jpg', '/uploads/videos/video11.mp4', 90, 10485760, 'PUBLISHED', 'PUBLIC', 28900, 2134, 345, 123, 678, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),
(6, '电竞比赛解说', 'LOL世界赛精彩回顾', '/uploads/covers/cover12.jpg', '/uploads/videos/video12.mp4', 180, 20971520, 'PUBLISHED', 'PUBLIC', 19800, 1567, 234, 89, 456, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),

-- 小红的视频
(7, '美妆教程', '日常淡妆教学', '/uploads/covers/cover13.jpg', '/uploads/videos/video13.mp4', 120, 13631488, 'PUBLISHED', 'PUBLIC', 38900, 2890, 456, 167, 890, DATE_SUB(NOW(), INTERVAL 7 DAY), NOW()),
(7, '穿搭分享', '春季穿搭推荐', '/uploads/covers/cover14.jpg', '/uploads/videos/video14.mp4', 100, 11534336, 'PUBLISHED', 'PUBLIC', 26780, 1987, 312, 98, 567, DATE_SUB(NOW(), INTERVAL 4 DAY), NOW()),

-- 小王的视频
(8, '旅行日记', '云南之旅记录', '/uploads/covers/cover15.jpg', '/uploads/videos/video15.mp4', 240, 26214400, 'PUBLISHED', 'PUBLIC', 15670, 1234, 189, 67, 345, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(8, '美食探店', '探店网红餐厅', '/uploads/covers/cover16.jpg', '/uploads/videos/video16.mp4', 80, 9437184, 'PUBLISHED', 'PUBLIC', 12340, 987, 156, 45, 234, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),

-- 草稿视频
(2, '未发布的视频', '这是一个草稿视频', '/uploads/covers/draft.jpg', '/uploads/videos/draft.mp4', 60, 7340032, 'DRAFT', 'PRIVATE', 0, 0, 0, 0, 0, NOW(), NOW());

-- ==================== 评论数据 ====================
INSERT INTO comments (video_id, user_id, parent_id, root_id, content, like_count, reply_count, status, created_at, updated_at) VALUES
-- 视频1的评论
(1, 3, NULL, NULL, '拍得真好看！风景太美了', 45, 3, 'NORMAL', DATE_SUB(NOW(), INTERVAL 29 DAY), NOW()),
(1, 4, NULL, NULL, '春天果然是最美的季节', 32, 1, 'NORMAL', DATE_SUB(NOW(), INTERVAL 28 DAY), NOW()),
(1, 5, 1, 1, '同意，我也想去这里旅游', 12, 0, 'NORMAL', DATE_SUB(NOW(), INTERVAL 28 DAY), NOW()),
(1, 6, NULL, NULL, '求分享拍摄地点！', 28, 2, 'NORMAL', DATE_SUB(NOW(), INTERVAL 27 DAY), NOW()),

-- 视频2的评论
(2, 4, NULL, NULL, '红烧肉看起来太诱人了！', 56, 2, 'NORMAL', DATE_SUB(NOW(), INTERVAL 24 DAY), NOW()),
(2, 5, NULL, NULL, '学会了，今晚就试试', 34, 0, 'NORMAL', DATE_SUB(NOW(), INTERVAL 23 DAY), NOW()),

-- 视频4的评论
(4, 2, NULL, NULL, '干货满满，收藏了！', 89, 1, 'NORMAL', DATE_SUB(NOW(), INTERVAL 27 DAY), NOW()),
(4, 5, NULL, NULL, '对新手很有帮助', 67, 0, 'NORMAL', DATE_SUB(NOW(), INTERVAL 26 DAY), NOW()),
(4, 6, NULL, NULL, '第5个技巧太实用了', 45, 0, 'NORMAL', DATE_SUB(NOW(), INTERVAL 25 DAY), NOW()),

-- 视频7的评论
(7, 2, NULL, NULL, '唱得太好了！求更新', 123, 0, 'NORMAL', DATE_SUB(NOW(), INTERVAL 17 DAY), NOW()),
(7, 3, NULL, NULL, '声音好好听', 98, 0, 'NORMAL', DATE_SUB(NOW(), INTERVAL 16 DAY), NOW()),

-- 视频9的评论
(9, 2, NULL, NULL, '笑死我了哈哈哈', 234, 0, 'NORMAL', DATE_SUB(NOW(), INTERVAL 9 DAY), NOW()),
(9, 3, NULL, NULL, '太搞笑了，转发给朋友了', 189, 0, 'NORMAL', DATE_SUB(NOW(), INTERVAL 8 DAY), NOW()),
(9, 6, NULL, NULL, '期待更多搞笑视频', 156, 0, 'NORMAL', DATE_SUB(NOW(), INTERVAL 7 DAY), NOW()),

-- 视频10的评论
(10, 2, NULL, NULL, '猫咪太可爱了！', 345, 0, 'NORMAL', DATE_SUB(NOW(), INTERVAL 7 DAY), NOW()),
(10, 3, NULL, NULL, '我想养猫了', 267, 0, 'NORMAL', DATE_SUB(NOW(), INTERVAL 6 DAY), NOW()),
(10, 7, NULL, NULL, '这是什么品种的猫？', 123, 1, 'NORMAL', DATE_SUB(NOW(), INTERVAL 5 DAY), NOW());

-- ==================== 点赞数据 ====================
INSERT INTO like_records (user_id, target_type, target_id, created_at) VALUES
-- 视频点赞
(3, 'VIDEO', 1, DATE_SUB(NOW(), INTERVAL 29 DAY)),
(4, 'VIDEO', 1, DATE_SUB(NOW(), INTERVAL 28 DAY)),
(5, 'VIDEO', 1, DATE_SUB(NOW(), INTERVAL 27 DAY)),
(6, 'VIDEO', 1, DATE_SUB(NOW(), INTERVAL 26 DAY)),
(7, 'VIDEO', 1, DATE_SUB(NOW(), INTERVAL 25 DAY)),

(2, 'VIDEO', 4, DATE_SUB(NOW(), INTERVAL 27 DAY)),
(5, 'VIDEO', 4, DATE_SUB(NOW(), INTERVAL 26 DAY)),
(6, 'VIDEO', 4, DATE_SUB(NOW(), INTERVAL 25 DAY)),

(2, 'VIDEO', 7, DATE_SUB(NOW(), INTERVAL 17 DAY)),
(3, 'VIDEO', 7, DATE_SUB(NOW(), INTERVAL 16 DAY)),
(5, 'VIDEO', 7, DATE_SUB(NOW(), INTERVAL 15 DAY)),

(2, 'VIDEO', 9, DATE_SUB(NOW(), INTERVAL 9 DAY)),
(3, 'VIDEO', 9, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(4, 'VIDEO', 9, DATE_SUB(NOW(), INTERVAL 7 DAY)),

(2, 'VIDEO', 10, DATE_SUB(NOW(), INTERVAL 7 DAY)),
(3, 'VIDEO', 10, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(4, 'VIDEO', 10, DATE_SUB(NOW(), INTERVAL 5 DAY)),

-- 评论点赞
(2, 'COMMENT', 1, DATE_SUB(NOW(), INTERVAL 28 DAY)),
(3, 'COMMENT', 1, DATE_SUB(NOW(), INTERVAL 27 DAY)),
(4, 'COMMENT', 2, DATE_SUB(NOW(), INTERVAL 27 DAY)),
(5, 'COMMENT', 4, DATE_SUB(NOW(), INTERVAL 26 DAY));

-- ==================== 关注数据 ====================
INSERT INTO follows (follower_id, following_id, created_at) VALUES
-- 张三的关注
(2, 3, DATE_SUB(NOW(), INTERVAL 25 DAY)),
(2, 4, DATE_SUB(NOW(), INTERVAL 20 DAY)),
(2, 5, DATE_SUB(NOW(), INTERVAL 15 DAY)),

-- 李四的关注
(3, 2, DATE_SUB(NOW(), INTERVAL 24 DAY)),
(3, 4, DATE_SUB(NOW(), INTERVAL 18 DAY)),

-- 王五的关注
(4, 2, DATE_SUB(NOW(), INTERVAL 19 DAY)),
(4, 3, DATE_SUB(NOW(), INTERVAL 17 DAY)),
(4, 5, DATE_SUB(NOW(), INTERVAL 10 DAY)),

-- 赵六的关注
(5, 2, DATE_SUB(NOW(), INTERVAL 14 DAY)),
(5, 4, DATE_SUB(NOW(), INTERVAL 9 DAY)),

-- 小明的关注
(6, 2, DATE_SUB(NOW(), INTERVAL 12 DAY)),
(6, 3, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(6, 5, DATE_SUB(NOW(), INTERVAL 5 DAY)),

-- 小红的关注
(7, 2, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(7, 4, DATE_SUB(NOW(), INTERVAL 6 DAY)),

-- 小王的关注
(8, 2, DATE_SUB(NOW(), INTERVAL 7 DAY)),
(8, 3, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(8, 5, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- ==================== 收藏数据 ====================
INSERT INTO favorites (user_id, video_id, created_at) VALUES
-- 张三的收藏
(2, 4, DATE_SUB(NOW(), INTERVAL 26 DAY)),
(2, 7, DATE_SUB(NOW(), INTERVAL 16 DAY)),
(2, 9, DATE_SUB(NOW(), INTERVAL 8 DAY)),

-- 李四的收藏
(3, 1, DATE_SUB(NOW(), INTERVAL 28 DAY)),
(3, 7, DATE_SUB(NOW(), INTERVAL 15 DAY)),
(3, 10, DATE_SUB(NOW(), INTERVAL 5 DAY)),

-- 王五的收藏
(4, 1, DATE_SUB(NOW(), INTERVAL 27 DAY)),
(4, 4, DATE_SUB(NOW(), INTERVAL 24 DAY)),
(4, 9, DATE_SUB(NOW(), INTERVAL 6 DAY)),

-- 赵六的收藏
(5, 1, DATE_SUB(NOW(), INTERVAL 25 DAY)),
(5, 4, DATE_SUB(NOW(), INTERVAL 23 DAY)),
(5, 7, DATE_SUB(NOW(), INTERVAL 14 DAY)),

-- 小明的收藏
(6, 1, DATE_SUB(NOW(), INTERVAL 22 DAY)),
(6, 4, DATE_SUB(NOW(), INTERVAL 21 DAY)),
(6, 9, DATE_SUB(NOW(), INTERVAL 7 DAY)),

-- 小红的收藏
(7, 1, DATE_SUB(NOW(), INTERVAL 20 DAY)),
(7, 7, DATE_SUB(NOW(), INTERVAL 13 DAY)),
(7, 10, DATE_SUB(NOW(), INTERVAL 4 DAY)),

-- 小王的收藏
(8, 4, DATE_SUB(NOW(), INTERVAL 19 DAY)),
(8, 9, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(8, 10, DATE_SUB(NOW(), INTERVAL 3 DAY));

-- ==================== 验证数据 ====================
SELECT '用户数量' as type, COUNT(*) as count FROM users
UNION ALL
SELECT '视频数量', COUNT(*) FROM videos
UNION ALL
SELECT '评论数量', COUNT(*) FROM comments
UNION ALL
SELECT '点赞数量', COUNT(*) FROM like_records
UNION ALL
SELECT '关注数量', COUNT(*) FROM follows
UNION ALL
SELECT '收藏数量', COUNT(*) FROM favorites;
