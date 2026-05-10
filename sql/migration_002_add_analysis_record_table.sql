-- 数据库迁移脚本：新增分析记录表
-- 迁移编号：002
-- 迁移描述：新增 analysis_record 表，用于存储群体分析和个体洞察的分析结果
-- 创建时间：2026-05-10
-- 作者：文档管理代理

-- 开启外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 创建分析记录表
CREATE TABLE IF NOT EXISTS `analysis_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `analysis_type` VARCHAR(20) NOT NULL COMMENT '分析类型：group（群体分析）/individual（个体洞察）',
    `user_id` BIGINT NOT NULL COMMENT '操作用户ID',
    `profile_ids` VARCHAR(500) DEFAULT NULL COMMENT '分析的档案ID列表(JSON数组)',
    `result_data` JSON DEFAULT NULL COMMENT '分析结果数据',
    `analysis_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '分析时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_type` (`user_id`, `analysis_type`),
    INDEX `idx_analysis_time` (`analysis_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分析记录表，存储群体分析和个体洞察的分析结果';

-- 回滚语句（可选，用于撤销迁移）
-- DROP TABLE IF EXISTS `analysis_record`;