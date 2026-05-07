-- ============================================================
-- 菜单重构迁移脚本：功能导向 → 角色导向
-- 日期：2026-05-07
-- 说明：废除"糖尿病预测中心"，将功能按角色重新分配
-- ============================================================

-- STEP 1: 移动并重命名菜单项
-- 1a. "在线预测" (id=53) → 用户服务，重命名为"家庭预测"
UPDATE `sys_menu` SET `pid` = 85, `name` = '家庭预测', `sort_num` = 3 WHERE `id` = 53;

-- 1b. "数据报表" (id=2) → 用户服务，重命名为"我的报告"
UPDATE `sys_menu` SET `pid` = 85, `name` = '我的报告', `sort_num` = 4 WHERE `id` = 2;

-- 1c. "采集日志" (id=41) → AI模型中心，重命名为"数据采集日志"
UPDATE `sys_menu` SET `pid` = 46, `name` = '数据采集日志', `sort_num` = 4 WHERE `id` = 41;

-- 1d. "预测工作台" (id=79) → AI模型中心
UPDATE `sys_menu` SET `pid` = 46, `sort_num` = 5 WHERE `id` = 79;

-- 1e. "健康管理中心" (id=73) → 重命名为"健康档案"
UPDATE `sys_menu` SET `name` = '健康档案' WHERE `id` = 73;

-- STEP 2: 重排 sort_num
-- 用户服务子菜单重排：风险快检(1) → 健康档案(2) → 家庭预测(3) → 我的报告(4) → 智能问答(5) → 诊疗档案(6) → 糖尿病科普(7)
UPDATE `sys_menu` SET `sort_num` = 5 WHERE `id` = 87;  -- 智能问答
UPDATE `sys_menu` SET `sort_num` = 6 WHERE `id` = 75;  -- 诊疗档案
UPDATE `sys_menu` SET `sort_num` = 7 WHERE `id` = 88;  -- 糖尿病科普

-- 诊断员服务子菜单重排：诊断工作台(1) → 个体洞察(2) → 组合分析(3) → 数据采集(4)
UPDATE `sys_menu` SET `sort_num` = 1 WHERE `id` = 74;  -- 诊断工作台
UPDATE `sys_menu` SET `sort_num` = 3 WHERE `id` = 91;  -- 组合分析
UPDATE `sys_menu` SET `sort_num` = 4 WHERE `id` = 78;  -- 数据采集

-- STEP 3: 删除废弃项
-- 3a. 删除"详细报表" (id=49) - 遗留组件
DELETE FROM `sys_role_menu` WHERE `menu_id` = 49;
DELETE FROM `sys_menu` WHERE `id` = 49;

-- 3b. 删除"糖尿病预测中心" (id=77) - 子项已全部迁出
DELETE FROM `sys_role_menu` WHERE `menu_id` = 77;
DELETE FROM `sys_menu` WHERE `id` = 77;

-- STEP 4: 重建角色菜单分配
DELETE FROM `sys_role_menu`;

-- ROLE_ADMIN (id=1) - 管理员：全部菜单
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(1, 10), (1, 4), (1, 5), (1, 6), (1, 7),
(1, 46), (1, 76), (1, 45), (1, 8), (1, 41), (1, 79),
(1, 85), (1, 86), (1, 73), (1, 53), (1, 2), (1, 87), (1, 75), (1, 88),
(1, 90), (1, 74), (1, 92), (1, 91), (1, 78),
(1, 54), (1, 51), (1, 52), (1, 55), (1, 56),
(1, 93);

-- ROLE_WORKER (id=2) - 普通用户：仅用户服务
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(2, 10), (2, 85), (2, 86), (2, 73), (2, 53), (2, 2), (2, 87), (2, 75), (2, 88);

-- ROLE_DOCTOR (id=18) - 医生：用户服务 + 诊断员服务
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(18, 10), (18, 85), (18, 86), (18, 73), (18, 53), (18, 2), (18, 87), (18, 75), (18, 88),
(18, 90), (18, 74), (18, 92), (18, 91), (18, 78);

-- ROLE_MAINTENANCE (id=3) - 运维：用户服务(部分) + 运维中心
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(3, 10), (3, 85), (3, 86), (3, 73), (3, 53), (3, 87), (3, 88),
(3, 54), (3, 51), (3, 52), (3, 55), (3, 56);
