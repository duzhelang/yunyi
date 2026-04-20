-- 为sys_user表添加新字段
-- 请在MySQL中执行此脚本

ALTER TABLE `sys_user`
ADD COLUMN `real_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名' AFTER `nickname`,
ADD COLUMN `sex` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '性别' AFTER `real_name`,
ADD COLUMN `age` int(11) NULL DEFAULT NULL COMMENT '年龄' AFTER `sex`,
ADD COLUMN `emergency_contact` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '紧急联系人' AFTER `age`,
ADD COLUMN `emergency_relation` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '与患者关系' AFTER `emergency_contact`;

-- 添加索引以提高查询性能
ALTER TABLE `sys_user`
ADD INDEX `idx_real_name` (`real_name`),
ADD INDEX `idx_sex` (`sex`),
ADD INDEX `idx_age` (`age`);
