/*
 Navicat Premium Data Transfer

 Source Server         : lang
 Source Server Type    : MySQL
 Source Server Version : 80041
 Source Host           : localhost:3306
 Source Schema         : dongfang

 Target Server Type    : MySQL
 Target Server Version : 80041
 File Encoding         : 65001

 Date: 14/04/2026 14:59:03
*/
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
  `value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '内容',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '类型'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES ('coffee', 'el-icon-coffee', 'icon');
INSERT INTO `sys_dict` VALUES ('document', 'el-icon-document', 'icon');
INSERT INTO `sys_dict` VALUES ('house', 'el-icon-house', 'icon');
INSERT INTO `sys_dict` VALUES ('menu', 'el-icon-menu', 'icon');
INSERT INTO `sys_dict` VALUES ('s-custom', 'el-icon-s-custom', 'icon');
INSERT INTO `sys_dict` VALUES ('s-grid', 'el-icon-s-grid', 'icon');
INSERT INTO `sys_dict` VALUES ('s-marketing', 'el-icon-s-marketing', 'icon');
INSERT INTO `sys_dict` VALUES ('user', 'el-icon-user', 'icon');
INSERT INTO `sys_dict` VALUES ('upload', 'el-icon-upload', 'icon');
INSERT INTO `sys_dict` VALUES ('dashboard', 'el-icon-dashboard', 'icon');
INSERT INTO `sys_dict` VALUES ('pie-chart', 'el-icon-pie-chart', 'icon');
INSERT INTO `sys_dict` VALUES ('data-analysis', 'el-icon-data-analysis', 'icon');
INSERT INTO `sys_dict` VALUES ('chat-dot-round', 'el-icon-chat-dot-round', 'icon');
INSERT INTO `sys_dict` VALUES ('book', 'el-icon-book', 'icon');
INSERT INTO `sys_dict` VALUES ('alert', 'el-icon-alert', 'icon');
INSERT INTO `sys_dict` VALUES ('s-data', 'el-icon-s-data', 'icon');
INSERT INTO `sys_dict` VALUES ('info', 'el-icon-info', 'icon');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '名称',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路径',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标',
  `description` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `pid` int(11) NULL DEFAULT NULL COMMENT '父级id',
  `page_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '页面路径',
  `sort_num` int(11) NULL DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (10, '主页', '/home', 'el-icon-house', '系统首页', NULL, 'Home', 0);
INSERT INTO `sys_menu` VALUES (4, '系统管理', NULL, 'el-icon-s-grid', '系统管理模块', NULL, NULL, 1);
INSERT INTO `sys_menu` VALUES (5, '用户管理', '/user', 'el-icon-user', '用户管理', 4, 'User', 1);
INSERT INTO `sys_menu` VALUES (6, '角色管理', '/role', 'el-icon-s-custom', '角色管理', 4, 'Role', 2);
INSERT INTO `sys_menu` VALUES (7, '菜单管理', '/menu', 'el-icon-menu', '菜单管理', 4, 'Menu', 3);
INSERT INTO `sys_menu` VALUES (46, 'AI模型中心', NULL, 'el-icon-s-grid', 'AI模型训练与管理中心', NULL, NULL, 2);
INSERT INTO `sys_menu` VALUES (45, '训练集管理', '/dataset-management', 'el-icon-document', '训练数据集管理中心', 46, 'DatasetManagement', 1);
INSERT INTO `sys_menu` VALUES (76, '模型管理', '/model-manager', 'el-icon-s-data', '模型资产统一管理中心', 46, 'ModelManagement', 2);
INSERT INTO `sys_menu` VALUES (8, '在线模型训练', '/online-training', 'el-icon-document', '训练任务启动器和历史记录看板', 46, 'OnlineTraining', 3);
INSERT INTO `sys_menu` VALUES (85, '用户服务', NULL, 'el-icon-user', '用户服务模块', NULL, NULL, 3);
INSERT INTO `sys_menu` VALUES (87, '智能问答', '/diabetes/chat', 'el-icon-chat-dot-round', '糖尿病健康咨询与报告解读', 85, 'DiabetesChat', 1);
INSERT INTO `sys_menu` VALUES (88, '糖尿病科普', '/diabetes-education', 'el-icon-book', '糖尿病知识科普教育', 85, 'DiabetesEducation', 2);
INSERT INTO `sys_menu` VALUES (73, '健康档案自查', '/health-profile', 'el-icon-s-custom', '个人健康档案查询', 85, 'HealthProfileView', 3);
INSERT INTO `sys_menu` VALUES (86, '风险快检', '/risk-quick', 'el-icon-alert', '风险评估快捷入口', 85, 'HealthCheck', 4);
INSERT INTO `sys_menu` VALUES (75, '诊疗档案', '/treatment-record', 'el-icon-document', '患者诊疗档案查询与批量导', 85, 'TreatmentRecord', 5);
INSERT INTO `sys_menu` VALUES (90, '诊断员服务', NULL, 'el-icon-s-custom', '诊断员服务模块', NULL, NULL, 4);
INSERT INTO `sys_menu` VALUES (91, '组合分析', '/diabetes/group', 'el-icon-pie-chart', '基于预测结果的宏观统计与对比分析', 90, 'GroupAnalysis', 1);
INSERT INTO `sys_menu` VALUES (92, '个体洞察', '/diabetes/individual', 'el-icon-user', '单一样本或单文件的深度归因分析', 90, 'IndividualInsight', 2);
INSERT INTO `sys_menu` VALUES (78, '数据采集', '/diabetes/collection', 'el-icon-upload', '原始数据资产的统一管理入口', 90, 'DataCollection', 3);
INSERT INTO `sys_menu` VALUES (79, '预测工作台', '/diabetes/workbench', 'el-icon-dashboard', '预测任务的全生命周期管理中心', 90, 'PredictionWorkbench', 4);
INSERT INTO `sys_menu` VALUES (74, '诊断工作台', '/doctor-workbench', 'el-icon-s-custom', '诊断医生工作台，处理患者', 90, 'DoctorWorkbench', 5);
INSERT INTO `sys_menu` VALUES (77, '糖尿病预测中心', NULL, 'el-icon-data-analysis', '糖尿病预测与分析中心', NULL, NULL, 5);
INSERT INTO `sys_menu` VALUES (41, '采集日志', '/data-test', 'el-icon-s-marketing', '在线测试模型数据', 77, 'DataTest', 1);
INSERT INTO `sys_menu` VALUES (53, '在线预测', '/test-file', 'el-icon-document', '在线预测功能', 77, 'TestFile', 2);
INSERT INTO `sys_menu` VALUES (2, '数据报表', '/dashbord', 'el-icon-s-marketing', '数据报表', 77, 'Dashbord', 3);
INSERT INTO `sys_menu` VALUES (49, '详细报表', '/detailbord', 'el-icon-s-marketing', '详细报表', 77, 'Detailbord', 4);
INSERT INTO `sys_menu` VALUES (54, '运维中心', NULL, 'el-icon-s-grid', '运维管理中心', NULL, NULL, 6);
INSERT INTO `sys_menu` VALUES (51, '故障报修', '/send', 'el-icon-document', '故障报修', 54, 'Send', 1);
INSERT INTO `sys_menu` VALUES (52, '报修详情', '/list', 'el-icon-s-marketing', '报修详情', 54, 'List', 2);
INSERT INTO `sys_menu` VALUES (55, '运维详情', '/omlist', 'el-icon-s-marketing', '运维详情', 54, 'OMlist', 3);
INSERT INTO `sys_menu` VALUES (56, '信息回执', '/omsend', 'el-icon-document', '信息回执', 54, 'OMsend', 4);
INSERT INTO `sys_menu` VALUES (93, '关于', '/about', 'el-icon-info', '关于系统', NULL, 'About', 7);

-- ----------------------------
-- Table structure for sys_message
-- ----------------------------
DROP TABLE IF EXISTS `sys_message`;
CREATE TABLE `sys_message`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '标题',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '故障类型',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '内容',
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `send_user_id` int(11) NULL DEFAULT NULL COMMENT '发送者用户ID',
  `send_user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发送者用户名',
  `send_real_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发送者真实姓名',
  `receive_user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '维修人员姓名',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK_message_send_user_id`(`send_user_id`) USING BTREE,
  CONSTRAINT `FK_message_send_user_id` FOREIGN KEY (`send_user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_message
-- ----------------------------
INSERT INTO `sys_message` VALUES (6, '故障1', '故障0', '87848', '2026-03-02 12:29:34', 2, 'admin', 'admin', 'admin');
INSERT INTO `sys_message` VALUES (7, 'tsts', '故障0', 'ssssss', '2026-03-02 21:34:40', 2, 'admin', 'admin', 'admin');
INSERT INTO `sys_message` VALUES (8, '1', '故障0', '11hhhj', '2026-03-02 21:35:08', 2, 'admin', 'admin', 'test');
INSERT INTO `sys_message` VALUES (9, 'test1', '故障1', 'test1', '2026-03-05 13:50:46', 2, 'admin', 'admin', 'uphold');
INSERT INTO `sys_message` VALUES (10, 'test', '故障2', '11111111ss', '2026-03-07 20:26:07', 58, 'upload', 'upload', 'admin');
INSERT INTO `sys_message` VALUES (11, 'test6', '故障2', '已查看，故障2修改完成。', '2026-03-08 12:54:23', 58, 'upload', 'upload', 'admin');
INSERT INTO `sys_message` VALUES (12, 'test7', '故障5', '故障5，上报', '2026-03-15 13:24:33', 2, 'admin', 'admin', 'upload');
INSERT INTO `sys_message` VALUES (13, 'test6', '故障5', '故障5，上报', '2026-03-15 13:28:53', 2, 'admin', 'admin', 'upload');
INSERT INTO `sys_message` VALUES (14, 'test8', '故障5', '故障5，收到', '2026-03-15 13:32:33', 58, 'upload', 'upload', 'upload');
INSERT INTO `sys_message` VALUES (15, 'test10', '故障5', '11111111', '2026-03-17 09:51:11', 58, 'upload', 'upload', 'test');
INSERT INTO `sys_message` VALUES (16, 'test4', '故障3', '故障3，上报', '2026-03-17 15:20:44', 2, 'admin', 'admin', 'upload');
INSERT INTO `sys_message` VALUES (17, 'test5', '故障4', 'test5,111', '2026-03-17 19:16:28', 58, 'upload', 'upload', 'upload');
INSERT INTO `sys_message` VALUES (18, 'test12', '故障5', 'sssssssss', '2026-03-17 21:19:44', 58, 'upload', 'upload', 'upload');
INSERT INTO `sys_message` VALUES (19, 'test555', '故障2', 'ssadaxcxssssss', '2026-03-17 21:19:44', 58, 'upload', 'upload', 'upload');
INSERT INTO `sys_message` VALUES (20, 'test', '故障2', 'sssssssss', '2026-03-17 21:59:04', 58, 'upload', 'upload', 'upload');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `avatar_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色',
  `roleid` int(11) NULL DEFAULT NULL COMMENT 'roleId编号外键',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `sex` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '性别',
  `age` int(11) NULL DEFAULT NULL COMMENT '年龄',
  `emergency_contact` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '紧急联系人',
  `emergency_relation` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '与患者关系',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user (从dongfang4.29.sql整合)
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'test', '202cb962ac59075b964b07152d234b70', 'test', 'test@qq.com', '12345678901', '南京', '2026-03-01 21:53:41', 'https://picsum.photos/80/80', 'ROLE_WORKER', 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (2, 'admin', '21232f297a57a5a743894a0e4a801fc3', 'admin', 'admin@qq.com', '13220260425', '南京', '2026-03-01 22:30:35', 'http://localhost:9090/file/avatar/1777434087158_1777358889987_1775233771405_sm.png', 'ROLE_ADMIN', 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (58, 'upload', '76ee3de97a1b8b903319b7c013d8c877', 'upload', 'upload@qq.com', '12345678901', '南京', '2026-03-06 15:48:40', 'https://picsum.photos/80/80', 'ROLE_MAINTENANCE', 3, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (81, 'zzz', 'e10adc3949ba59abbe56e057f20f883e', 'zzz', NULL, NULL, NULL, '2025-10-28 16:59:31', NULL, 'ROLE_WORKER', 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (85, 'zyl', NULL, '无忧', NULL, NULL, NULL, '2025-11-04 10:57:00', NULL, 'ROLE_WORKER', 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (86, 'zyl', 'e10adc3949ba59abbe56e057f20f883e', 'zyl', NULL, NULL, NULL, '2025-11-04 10:57:44', NULL, 'ROLE_WORKER', 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (88, 'zyl12345', 'e10adc3949ba59abbe56e057f20f883e', 'zyl12345', NULL, NULL, NULL, '2025-11-05 10:10:41', NULL, 'ROLE_MAINTENANCE', 3, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (89, 'zhouyunli', 'e10adc3949ba59abbe56e057f20f883e', 'zhouyunli', NULL, NULL, NULL, '2025-11-05 10:11:48', NULL, 'ROLE_MAINTENANCE', 3, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (90, '111', '25f9e794323b453885f5181f1b624d0b', '111', NULL, NULL, NULL, '2026-03-10 18:24:15', NULL, 'ROLE_WORKER', 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (91, 'user', 'e10adc3949ba59abbe56e057f20f883e', 'user', NULL, NULL, NULL, '2026-03-10 18:26:26', NULL, 'ROLE_WORKER', 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (92, 'test1', 'e10adc3949ba59abbe56e057f20f883e', 'test1', NULL, NULL, NULL, '2026-03-20 22:43:08', NULL, 'ROLE_MAINTENANCE', 3, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (93, '123', 'e10adc3949ba59abbe56e057f20f883e', '123', NULL, NULL, NULL, '2026-03-22 02:50:21', NULL, 'ROLE_WORKER', 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (94, '他11', 'e10adc3949ba59abbe56e057f20f883e', '他11', NULL, NULL, NULL, '2026-03-28 01:14:32', NULL, 'ROLE_WORKER', 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (95, 'doctor1', 'e10adc3949ba59abbe56e057f20f883e', '李医生', 'doctor1@hospital.com', '13800138001', '南京市第一医院', '2026-04-28 10:00:00', NULL, 'ROLE_DOCTOR', 18, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (96, 'doctor2', 'e10adc3949ba59abbe56e057f20f883e', '王医生', 'doctor2@hospital.com', '13800138002', '南京市第二医院', '2026-04-28 11:00:00', NULL, 'ROLE_DOCTOR', 18, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色名',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `flag` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role (从dongfang4.29.sql整合)
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '管理员', '管理员', 'ROLE_ADMIN');
INSERT INTO `sys_role` VALUES (2, '用户', '普通用户', 'ROLE_WORKER');
INSERT INTO `sys_role` VALUES (3, '运维', '运维', 'ROLE_MAINTENANCE');
INSERT INTO `sys_role` VALUES (16, '测试1', '系统增删测试项', '0001');
INSERT INTO `sys_role` VALUES (17, '测试2', '系统增删测试项', '0002');
INSERT INTO `sys_role` VALUES (18, '医生', '医生角色', 'ROLE_DOCTOR');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  `menu_id` int(11) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 10), (1, 4), (1, 5), (1, 6), (1, 7), (1, 46), (1, 45), (1, 76), (1, 8), (1, 85), (1, 87), (1, 88), (1, 73), (1, 86), (1, 75), (1, 90), (1, 91), (1, 92), (1, 78), (1, 79), (1, 74), (1, 77), (1, 41), (1, 53), (1, 2), (1, 49), (1, 54), (1, 51), (1, 52), (1, 55), (1, 56), (1, 93);
INSERT INTO `sys_role_menu` VALUES (2, 10), (2, 85), (2, 87), (2, 88), (2, 73), (2, 86), (2, 75), (2, 90), (2, 91), (2, 92), (2, 78), (2, 79), (2, 74), (2, 77), (2, 41), (2, 53), (2, 2), (2, 49);
INSERT INTO `sys_role_menu` VALUES (3, 10), (3, 85), (3, 87), (3, 88), (3, 73), (3, 86), (3, 77), (3, 41), (3, 53);

-- ----------------------------
-- Table structure for sys_testfile
-- ----------------------------
DROP TABLE IF EXISTS `sys_testfile`;
CREATE TABLE `sys_testfile` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件名',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件类型',
  `size` bigint(20) NULL DEFAULT NULL COMMENT '文件大小',
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件路径',
  `enable` int(11) NULL DEFAULT 1 COMMENT '是否启用',
  `md5` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件MD5',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '用户ID',
  `json_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'JSON文件路径',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '原始文件名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '测试文件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_testfile (预置数据)
-- ----------------------------
INSERT INTO `sys_testfile` (`id`, `name`, `type`, `size`, `url`, `enable`, `md5`, `is_delete`, `user_id`, `json_url`, `file_name`) VALUES 
(1, 'test_data_2024_01.csv', 'csv', 102400, 'data/test/test_data_2024_01.csv', 1, 'd41d8cd98f00b204e9800998ecf8427e', 0, 1, 'data/test/test_data_2024_01.json', 'test_data_2024_01.csv'),
(2, 'patient_records.csv', 'csv', 204800, 'data/test/patient_records.csv', 1, 'e4d909c290d0fb1ca068ffaddf22cbd0', 0, 1, 'data/test/patient_records.json', 'patient_records.csv'),
(3, 'diabetes_test_set.csv', 'csv', 153600, 'data/test/diabetes_test_set.csv', 1, 'a1b2c3d4e5f67890abcdef1234567890', 0, 2, 'data/test/diabetes_test_set.json', 'diabetes_test_set.csv');

-- ----------------------------
-- Table structure for sys_trainfile
-- ----------------------------
DROP TABLE IF EXISTS `sys_trainfile`;
CREATE TABLE `sys_trainfile` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件名',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件类型',
  `size` bigint(20) NULL DEFAULT NULL COMMENT '文件大小',
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件路径',
  `pythonurl` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Python脚本路径',
  `md5` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件MD5',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
  `enable` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '用户ID',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类',
  `file_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'csv' COMMENT '文件类型',
  `file_size` bigint(20) NULL DEFAULT NULL COMMENT '文件大小(字节)',
  `record_count` int(11) NULL DEFAULT NULL COMMENT '记录条数',
  `column_count` int(11) NULL DEFAULT NULL COMMENT '列数',
  `last_scan_time` timestamp NULL DEFAULT NULL COMMENT '最后扫描时间',
  `quality_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'normal' COMMENT '质量等级: high, normal, low',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '训练文件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_trainfile (预置数据)
-- ----------------------------
INSERT INTO `sys_trainfile` (`id`, `name`, `type`, `size`, `url`, `pythonurl`, `md5`, `is_delete`, `enable`, `user_id`, `remark`, `category`, `file_type`, `file_size`, `record_count`, `column_count`, `last_scan_time`, `quality_level`) VALUES 
(1, 'diabetes_train_dataset.csv', 'csv', 1048576, 'data/train/diabetes_train_dataset.csv', 'python/train/diabetes_train.py', 'abc123def4567890abcdef1234567890', 0, 1, 1, '糖尿病训练数据集V1.0', 'diabetes', 'csv', 1048576, 768, 9, '2026-03-01 10:00:00', 'high'),
(2, 'diabetes_features_extended.csv', 'csv', 2097152, 'data/train/diabetes_features_extended.csv', 'python/train/feature_extract.py', '123abc456def7890123abcdef4567890', 0, 1, 1, '扩展特征训练数据集', 'diabetes', 'csv', 2097152, 1536, 12, '2026-03-15 14:30:00', 'high'),
(3, 'patient_history_data.csv', 'csv', 524288, 'data/train/patient_history_data.csv', 'python/train/history_analysis.py', '9876543210fedcba09876543210fedcba', 0, 1, 2, '患者历史数据', 'history', 'csv', 524288, 512, 8, '2026-03-20 09:15:00', 'normal');

-- ----------------------------
-- Table structure for sys_train_task
-- ----------------------------
DROP TABLE IF EXISTS `sys_train_task`;
CREATE TABLE `sys_train_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '训练任务ID',
  `task_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
  `train_file_id` int(11) NULL DEFAULT NULL COMMENT '关联的训练文件ID',
  `train_file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '训练文件名称',
  `model_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模型名称',
  `hyper_params` json NULL DEFAULT NULL COMMENT '训练超参数JSON',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'pending' COMMENT '状态: pending, running, completed, failed',
  `accuracy` decimal(10,6) NULL DEFAULT NULL COMMENT '准确率',
  `loss` decimal(10,6) NULL DEFAULT NULL COMMENT '损失值',
  `recall_rate` decimal(10,6) NULL DEFAULT NULL COMMENT '召回率',
  `precision_rate` decimal(10,6) NULL DEFAULT NULL COMMENT '精确率',
  `f1_score` decimal(10,6) NULL DEFAULT NULL COMMENT 'F1分数',
  `log_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '训练日志路径',
  `model_output_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模型输出路径',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '错误信息',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '结束时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_train_file` (`train_file_id`) USING BTREE,
  INDEX `idx_status` (`status`) USING BTREE,
  INDEX `idx_create_time` (`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '训练任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_train_task (预置数据)
-- ----------------------------
INSERT INTO `sys_train_task` (`id`, `task_name`, `train_file_id`, `train_file_name`, `model_name`, `hyper_params`, `status`, `accuracy`, `loss`, `recall_rate`, `precision_rate`, `f1_score`, `log_path`, `model_output_path`, `create_time`, `start_time`, `end_time`) VALUES 
(1, '糖尿病预测模型V1训练', 1, 'diabetes_train_dataset.csv', 'diabetes_model', '{\"epochs\": 100, \"batch_size\": 32, \"learning_rate\": 0.001}', 'completed', 0.875000, 0.325000, 0.850000, 0.890000, 0.870000, 'logs/train/task_1.log', 'models/diabetes_model_v1.pth', '2026-03-02 09:00:00', '2026-03-02 09:05:00', '2026-03-02 10:30:00'),
(2, '扩展特征模型训练', 2, 'diabetes_features_extended.csv', 'diabetes_model', '{\"epochs\": 150, \"batch_size\": 64, \"learning_rate\": 0.0005}', 'completed', 0.892000, 0.298000, 0.875000, 0.908000, 0.891000, 'logs/train/task_2.log', 'models/diabetes_model_v2.pth', '2026-03-16 14:00:00', '2026-03-16 14:05:00', '2026-03-16 16:45:00'),
(3, '历史数据分析模型', 3, 'patient_history_data.csv', 'history_model', '{\"epochs\": 80, \"batch_size\": 16, \"learning_rate\": 0.002}', 'running', NULL, NULL, NULL, NULL, NULL, 'logs/train/task_3.log', NULL, '2026-03-21 10:00:00', '2026-03-21 10:05:00', NULL);

-- ----------------------------
-- Table structure for sys_model_version
-- ----------------------------
DROP TABLE IF EXISTS `sys_model_version`;
CREATE TABLE `sys_model_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '模型版本ID',
  `model_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模型名称',
  `version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '版本号',
  `source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'manual' COMMENT '来源: manual, online_train',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模型文件路径',
  `scaler_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标准化器路径',
  `encoder_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '编码器路径',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模型描述',
  `metrics` json NULL DEFAULT NULL COMMENT '模型性能指标JSON',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'inactive' COMMENT '状态: active, inactive, archived',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_model_name` (`model_name`) USING BTREE,
  INDEX `idx_status` (`status`) USING BTREE,
  INDEX `idx_create_time` (`create_time`) USING BTREE,
  UNIQUE KEY `uk_model_version` (`model_name`, `version`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '模型版本表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_model_version (预置数据)
-- ----------------------------
INSERT INTO `sys_model_version` (`id`, `model_name`, `version`, `source`, `file_path`, `scaler_path`, `encoder_path`, `description`, `metrics`, `status`, `create_time`) VALUES 
(1, 'diabetes_model', 'v1.0.0', 'manual', 'data/models/pth_models/diabetes_model.pth', 'data/models/scaler/diabetes_scaler.pkl', 'data/models/encoder/diabetes_encoder.pkl', '基础糖尿病预测模型，使用逻辑回归算法', '{\"accuracy\": 0.85, \"precision\": 0.83, \"recall\": 0.87, \"f1\": 0.85}', 'inactive', '2026-02-20 10:00:00'),
(2, 'diabetes_model', 'v1.1.0', 'online_train', 'data/models/pth_models/diabetes_model_v1.1.pth', 'data/models/scaler/diabetes_scaler_v1.1.pkl', 'data/models/encoder/diabetes_encoder_v1.1.pkl', '优化版糖尿病预测模型，使用随机森林算法', '{\"accuracy\": 0.875, \"precision\": 0.89, \"recall\": 0.85, \"f1\": 0.87}', 'active', '2026-03-02 10:30:00'),
(3, 'diabetes_model', 'v2.0.0', 'online_train', 'data/models/pth_models/diabetes_model_v2.0.pth', 'data/models/scaler/diabetes_scaler_v2.0.pkl', 'data/models/encoder/diabetes_encoder_v2.0.pkl', '扩展特征版糖尿病预测模型，使用XGBoost算法', '{\"accuracy\": 0.892, \"precision\": 0.908, \"recall\": 0.875, \"f1\": 0.891}', 'inactive', '2026-03-16 16:45:00');

-- ----------------------------
-- Table structure for diabetes_education
-- ----------------------------
DROP TABLE IF EXISTS `diabetes_education`;
CREATE TABLE `diabetes_education` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `section_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '篇章ID',
  `section_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '篇章标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '篇章内容(HTML)',
  `sort_order` int(11) NULL DEFAULT NULL COMMENT '排序顺序',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_section_id` (`section_id`) USING BTREE,
  INDEX `idx_sort_order` (`sort_order`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '糖尿病科普内容表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for education_comment
-- ----------------------------
DROP TABLE IF EXISTS `education_comment`;
CREATE TABLE `education_comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '用户ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '留言内容',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id` (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '科普留言表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of diabetes_education
-- ----------------------------
INSERT INTO `diabetes_education` (`id`, `section_id`, `section_title`, `content`, `sort_order`) VALUES (1, 'basic', '一、基础认知篇', '<h3>1. 什么是糖尿病？</h3><p>糖尿病是一组以<strong>高血糖</strong>为特征的代谢性疾病。当胰腺不能产生足够的胰岛素，或身体不能有效利用产生的胰岛素时，就会发生糖尿病。</p><h3>2. 主要分型</h3><table class="info-table"><thead><tr><th>类型</th><th>占比</th><th>特点</th></tr></thead><tbody><tr><td><strong>1型糖尿病</strong></td><td>约5-10%</td><td>自身免疫破坏胰岛β细胞，胰岛素绝对缺乏，多见于青少年</td></tr><tr><td><strong>2型糖尿病</strong></td><td>约90%</td><td>胰岛素抵抗+相对分泌不足，与生活方式密切相关</td></tr><tr><td><strong>妊娠糖尿病</strong></td><td>约2-10%</td><td>妊娠期间首次发生或发现，多数产后恢复</td></tr><tr><td><strong>特殊类型</strong></td><td>&lt;1%</td><td>单基因糖尿病、胰腺疾病、内分泌疾病等所致</td></tr></tbody></table><h3>3. 诊断标准（静脉血浆葡萄糖）</h3><ul class="standard-list"><li><strong>空腹血糖</strong> ≥ 7.0 mmol/L</li><li><strong>餐后2小时血糖</strong> ≥ 11.1 mmol/L</li><li><strong>随机血糖</strong> ≥ 11.1 mmol/L 且有典型症状</li><li><strong>糖化血红蛋白（HbA1c）</strong> ≥ 6.5%</li></ul><div class="warning-box"><p>⚠️ 需重复确认（无症状者需两次异常）</p></div>', 1);
INSERT INTO `diabetes_education` (`id`, `section_id`, `section_title`, `content`, `sort_order`) VALUES (2, 'symptoms', '二、症状识别篇', '<h3>典型"三多一少"</h3><ul class="symptom-list"><li><strong>多尿</strong>：血糖过高导致渗透性利尿</li><li><strong>多饮</strong>：脱水刺激口渴中枢</li><li><strong>多食</strong>：细胞无法利用葡萄糖，能量缺乏</li><li><strong>体重下降</strong>：脂肪和蛋白质分解供能</li></ul><h3>不典型症状（尤其2型）</h3><ul class="symptom-list"><li>皮肤瘙痒（尤其外阴）、伤口愈合缓慢</li><li>视力模糊、反复感染（泌尿道、皮肤）</li><li>乏力、手脚麻木或刺痛</li><li>餐前低血糖（心悸、出汗、饥饿感）</li></ul><h3>高危人群筛查</h3><ul class="risk-list"><li>年龄≥40岁</li><li>超重/肥胖（BMI≥24，男性腰围≥90cm，女性≥85cm）</li><li>有糖尿病家族史</li><li>妊娠糖尿病史或巨大儿分娩史</li><li>高血压、血脂异常、脂肪肝患者</li><li>长期久坐、熬夜、精神压力大者</li></ul>', 2);
INSERT INTO `diabetes_education` (`id`, `section_id`, `section_title`, `content`, `sort_order`) VALUES (3, 'diet', '三、饮食管理篇', '<h3>核心原则</h3><div class="principle-box"><p>控制总热量、均衡营养、定时定量、低GI优先</p></div><h3>1. 碳水化合物（占总热量50-60%）</h3><ul class="food-list"><li><span class="icon good">✅</span> 优选：全谷物、杂豆、燕麦、糙米、红薯</li><li><span class="icon bad">❌</span> 限制：白粥、糯米、含糖饮料、糕点</li><li><span class="icon tip">💡</span> 技巧：主食放凉后抗性淀粉增加，升糖更慢</li></ul><h3>2. 蛋白质（15-20%）</h3><p>鱼、禽、蛋、豆制品、瘦肉</p><p>肾功能不全者需限制蛋白摄入量</p><h3>3. 脂肪（&lt;30%）</h3><ul class="food-list"><li><span class="icon bad">❌</span> 减少饱和脂肪（肥肉、黄油）</li><li><span class="icon good">✅</span> 增加不饱和脂肪（深海鱼、坚果、橄榄油）</li></ul><h3>4. 膳食纤维</h3><p>每日25-30g，延缓糖分吸收</p><p>蔬菜不限量（淀粉类蔬菜除外），先吃菜再吃饭</p>', 3);
INSERT INTO `diabetes_education` (`id`, `section_id`, `section_title`, `content`, `sort_order`) VALUES (4, 'exercise', '四、运动治疗篇', '<h3>运动益处</h3><ul class="benefit-list"><li>提高胰岛素敏感性</li><li>降低血糖、血脂、血压</li><li>减轻体重，改善心肺功能</li><li>缓解焦虑抑郁</li></ul><h3>推荐运动</h3><table class="info-table"><thead><tr><th>类型</th><th>项目</th><th>频率</th></tr></thead><tbody><tr><td>有氧运动</td><td>快走、游泳、骑车</td><td>每周≥150分钟，中等强度</td></tr><tr><td>抗阻训练</td><td>哑铃、弹力带、深蹲</td><td>每周2-3次，锻炼大肌群</td></tr><tr><td>柔韧性</td><td>瑜伽、太极、拉伸</td><td>每天可练</td></tr></tbody></table><h3>运动注意事项</h3><ul class="note-list"><li><span class="icon clock">⏰</span> <strong>时机</strong>：餐后1小时开始，避免空腹运动</li><li><span class="icon blood">🩸</span> <strong>监测</strong>：运动前后测血糖，&lt;5.6或&gt;16.7 mmol/L不宜运动</li><li><span class="icon shoe">👟</span> <strong>装备</strong>：穿舒适鞋袜，预防足部损伤</li><li><span class="icon candy">🍬</span> <strong>备糖</strong>：随身携带糖果，防低血糖</li></ul>', 4);
INSERT INTO `diabetes_education` (`id`, `section_id`, `section_title`, `content`, `sort_order`) VALUES (5, 'medication', '五、药物治疗篇', '<h3>口服降糖药主要类别</h3><table class="info-table"><thead><tr><th>类别</th><th>代表药</th><th>作用机制</th><th>特点</th></tr></thead><tbody><tr><td>双胍类</td><td>二甲双胍</td><td>减少肝糖输出，改善胰岛素抵抗</td><td>一线首选，减重，心血管获益</td></tr><tr><td>磺脲类</td><td>格列美脲</td><td>刺激胰岛分泌胰岛素</td><td>降糖强，但易低血糖</td></tr><tr><td>格列奈类</td><td>瑞格列奈</td><td>快速刺激胰岛素分泌</td><td>餐时服用，灵活</td></tr><tr><td>α-糖苷酶抑制剂</td><td>阿卡波糖</td><td>延缓肠道碳水吸收</td><td>降餐后血糖，腹胀副作用</td></tr><tr><td>DPP-4抑制剂</td><td>西格列汀</td><td>促进胰岛素分泌，抑制胰高糖素</td><td>低血糖风险小</td></tr><tr><td>SGLT-2抑制剂</td><td>达格列净</td><td>促进尿糖排泄</td><td>减重、降压、心肾保护</td></tr><tr><td>GLP-1受体激动剂</td><td>司美格鲁肽</td><td>多重机制降糖</td><td>强效降糖、减重显著</td></tr></tbody></table><h3>胰岛素治疗</h3><ul class="insulin-list"><li><strong>适用</strong>：1型糖尿病、妊娠糖尿病、2型口服药失效、急性并发症</li><li><strong>类型</strong>：速效、短效、中效、长效、预混</li><li><strong>误区</strong>：胰岛素不会"成瘾"，也不是病情晚期才用</li></ul>', 5);
INSERT INTO `diabetes_education` (`id`, `section_id`, `section_title`, `content`, `sort_order`) VALUES (6, 'monitoring', '六、血糖监测篇', '<h3>监测时间点（"七点法"）</h3><div class="time-points"><span>空腹</span> → <span>早餐后2h</span> → <span>午餐前</span> → <span>午餐后2h</span> → <span>晚餐前</span> → <span>晚餐后2h</span> → <span>睡前</span></div><h3>控制目标（一般成人）</h3><table class="info-table"><thead><tr><th>指标</th><th>目标值</th></tr></thead><tbody><tr><td>空腹血糖</td><td>4.4-7.0 mmol/L</td></tr><tr><td>餐后2h血糖</td><td>&lt;10.0 mmol/L</td></tr><tr><td>糖化血红蛋白（HbA1c）</td><td>&lt;7.0%</td></tr><tr><td>血压</td><td>&lt;130/80 mmHg</td></tr><tr><td>LDL-C</td><td>&lt;2.6 mmol/L（合并冠心病&lt;1.8）</td></tr></tbody></table><div class="note-box"><p>老年人、有严重并发症者目标可适当放宽</p></div><h3>糖化血红蛋白意义</h3><p>反映过去2-3个月平均血糖水平，是评估长期控制的"金标准"，每3个月检测一次。</p>', 6);
INSERT INTO `diabetes_education` (`id`, `section_id`, `section_title`, `content`, `sort_order`) VALUES (7, 'complications', '七、并发症防治篇', '<h3>急性并发症</h3><table class="info-table"><thead><tr><th>类型</th><th>诱因</th><th>表现</th><th>处理</th></tr></thead><tbody><tr><td><strong>低血糖</strong></td><td>药量过大、进食少、运动过量</td><td>心慌、出汗、手抖、意识模糊</td><td>立即补糖（15g快糖），15分钟复测</td></tr><tr><td><strong>酮症酸中毒</strong></td><td>感染、停药、应激</td><td>恶心呕吐、腹痛、呼吸深快、烂苹果味</td><td>急诊就医，补液+胰岛素</td></tr><tr><td><strong>高渗高血糖状态</strong></td><td>感染、脱水、大量饮甜饮料</td><td>严重脱水、意识障碍、血糖极高</td><td>急诊，死亡率较高</td></tr></tbody></table><h3>慢性并发症</h3><ol class="complication-list"><li><strong>糖尿病肾病</strong>：早期微量白蛋白尿→大量蛋白尿→肾衰竭。每年查尿微量白蛋白和肾功能。</li><li><strong>糖尿病视网膜病变</strong>：致盲主因。2型确诊时即应筛查眼底，每年复查。</li><li><strong>糖尿病神经病变</strong>：手套袜套样感觉异常、麻木、疼痛、胃肠功能紊乱。</li><li><strong>糖尿病足</strong>：溃疡、感染、坏疽。每日检查双足，保持清洁干燥，穿合适鞋袜。</li><li><strong>心脑血管病变</strong>：心梗、脑卒中风险增加2-4倍。严格控制血压、血脂、抗血小板治疗。</li></ol>', 7);
INSERT INTO `diabetes_education` (`id`, `section_id`, `section_title`, `content`, `sort_order`) VALUES (8, 'special', '八、特殊人群篇', '<h3>儿童青少年</h3><ul class="special-list"><li>1型为主，需终身胰岛素</li><li>生长发育期胰岛素需求变化大</li><li>心理支持至关重要</li></ul><h3>老年人</h3><ul class="special-list"><li>症状不典型，易漏诊</li><li>低血糖感知能力下降，风险更高</li><li>控制目标个体化，避免过度治疗</li><li>注意多重用药相互作用</li></ul><h3>妊娠期</h3><ul class="special-list"><li>首选饮食运动控制，必要时用胰岛素</li><li>血糖控制更严格：空腹&lt;5.3，餐后1h&lt;7.8，2h&lt;6.7</li><li>产后6-12周复查OGTT，此后每3年筛查</li></ul>', 8);
INSERT INTO `diabetes_education` (`id`, `section_id`, `section_title`, `content`, `sort_order`) VALUES (9, 'lifestyle', '九、生活方式干预篇', '<h3>体重管理</h3><ul class="lifestyle-list"><li>超重2型糖尿病患者减重5-10%，可显著改善血糖</li><li>重度肥胖可考虑代谢手术（BMI≥32.5或≥27.5伴控制不佳）</li></ul><h3>睡眠与压力</h3><ul class="lifestyle-list"><li>睡眠不足（&lt;6小时）增加胰岛素抵抗</li><li>长期压力升高皮质醇，推高血糖</li><li>建议每晚7-8小时优质睡眠</li></ul><h3>戒烟限酒</h3><ul class="lifestyle-list"><li>吸烟加速血管病变，增加截肢风险</li><li>酒精干扰血糖，空腹饮酒易低血糖</li><li>如饮酒：女性≤1份/日，男性≤2份/日（1份≈啤酒350ml/葡萄酒150ml）</li></ul>', 9);
INSERT INTO `diabetes_education` (`id`, `section_id`, `section_title`, `content`, `sort_order`) VALUES (10, 'advance', '十、前沿进展与误区澄清', '<h3>新技术</h3><ul class="tech-list"><li><strong>持续葡萄糖监测（CGM）</strong>：实时看血糖曲线，发现隐匿高低血糖</li><li><strong>人工胰腺/闭环系统</strong>：CGM+胰岛素泵自动调节，主要用于1型</li><li><strong>干细胞治疗</strong>：尚在研究阶段，未广泛应用于临床</li></ul><h3>常见误区纠正</h3><table class="info-table"><thead><tr><th>误区</th><th>真相</th></tr></thead><tbody><tr><td>糖尿病是吃糖吃出来的</td><td>遗传+环境共同作用，不单是吃糖</td></tr><tr><td>得了糖尿病就不能吃水果</td><td>可选低GI水果，控制量，两餐间吃</td></tr><tr><td>胰岛素会上瘾</td><td>胰岛素是人体激素，需则用，不存在成瘾</td></tr><tr><td>血糖正常就能停药</td><td>需医生评估，擅自停药易反弹</td></tr><tr><td>偏方/保健品能根治</td><td>目前无法根治，警惕虚假广告</td></tr><tr><td>瘦子不会得2型糖尿病</td><td>约10-15%的2型患者体重正常</td></tr></tbody></table>', 10);
INSERT INTO `diabetes_education` (`id`, `section_id`, `section_title`, `content`, `sort_order`) VALUES (11, 'video', '十一、视频科普篇', '<p>视频科普内容可通过后台管理系统上传管理</p>', 11);
INSERT INTO `diabetes_education` (`id`, `section_id`, `section_title`, `content`, `sort_order`) VALUES (12, 'summary', '核心建议总结', '<h3>核心建议总结</h3><ol class="summary-list"><li><strong>早筛查</strong>：40岁以上每年体检加做血糖和HbA1c</li><li><strong>管住嘴</strong>：控制总量、低GI、高纤维、少油少盐</li><li><strong>迈开腿</strong>：每周150分钟中等强度运动</li><li><strong>勤监测</strong>：定期测血糖、HbA1c、血压、血脂、眼底、尿蛋白、足</li><li><strong>遵医嘱</strong>：不擅自调药，定期复诊</li><li><strong>学知识</strong>：参加糖尿病教育课程，提升自我管理能力</li></ol>', 12);

-- ----------------------------
-- Records of education_comment
-- ----------------------------
INSERT INTO `education_comment` (`id`, `user_id`, `user_name`, `content`) VALUES (1, 1, '用户1', '这篇科普文章非常详细，对我帮助很大！');
INSERT INTO `education_comment` (`id`, `user_id`, `user_name`, `content`) VALUES (2, 2, '用户2', '希望能看到更多关于糖尿病食谱的具体推荐。');

-- ----------------------------
-- Table structure for treatment_record
-- ----------------------------
DROP TABLE IF EXISTS `treatment_record`;
CREATE TABLE `treatment_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `patient_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '患者姓名',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证号',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '性别',
  `age` int(11) NULL DEFAULT NULL COMMENT '年龄',
  `blood_sugar` decimal(10,2) NULL DEFAULT NULL COMMENT '血糖值(mmol/L)',
  `diagnosis_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '诊断结果',
  `treatment_plan` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '治疗方案',
  `doctor_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '负责医生',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '诊疗档案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of treatment_record (预置数据)
-- ----------------------------
INSERT INTO `treatment_record` (`id`, `patient_name`, `id_card`, `phone`, `gender`, `age`, `blood_sugar`, `diagnosis_result`, `treatment_plan`, `doctor_name`, `remark`, `create_time`) VALUES 
(1, '张三', '110101198801011234', '13800138001', '男', 38, 8.50, '2型糖尿病，中度', '1. 二甲双胍 500mg 每日三次；2. 饮食控制，每日热量摄入控制在1500kcal以内；3. 每周运动不少于150分钟；4. 定期监测血糖', '张医生', '患者依从性较好，建议每月复查一次', '2026-03-01 10:00:00'),
(2, '李四', '120102199002022345', '13900139002', '女', 36, 10.20, '2型糖尿病，重度', '1. 胰岛素治疗，每日两次注射；2. 严格饮食控制；3. 每日监测血糖4次；4. 内分泌科定期随访', '王医生', '患者血糖控制不佳，需加强管理', '2026-03-05 14:30:00'),
(3, '王五', '310103197503033456', '13700137003', '男', 51, 7.80, '2型糖尿病，轻度', '1. 饮食调整，减少碳水化合物摄入；2. 每周运动3-4次；3. 每三个月复查糖化血红蛋白', '张医生', '早期发现，控制良好', '2026-03-10 09:15:00');

-- ----------------------------
-- Table structure for diabetes_video
-- ----------------------------
DROP TABLE IF EXISTS `diabetes_video`;
CREATE TABLE `diabetes_video` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '对应糖尿病类型',
  `video_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '视频标题',
  `video_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '视频存储路径或在线地址',
  `cover_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面图路径',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '视频描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '糖尿病视频表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for diabetes_record
-- ----------------------------
DROP TABLE IF EXISTS `diabetes_record`;
CREATE TABLE `diabetes_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `pregnancies` int(11) NULL DEFAULT NULL COMMENT '怀孕次数',
  `glucose` int(11) NULL DEFAULT NULL COMMENT '血糖浓度',
  `blood_pressure` int(11) NULL DEFAULT NULL COMMENT '血压',
  `skin_thickness` int(11) NULL DEFAULT NULL COMMENT '皮褶厚度',
  `insulin` int(11) NULL DEFAULT NULL COMMENT '胰岛素',
  `bmi` double NULL DEFAULT NULL COMMENT '身体质量指数',
  `diabetes_pedigree_function` double NULL DEFAULT NULL COMMENT '糖尿病谱系功能',
  `age` int(11) NULL DEFAULT NULL COMMENT '年龄',
  `outcome` int(11) NULL DEFAULT NULL COMMENT '结果: 0无糖尿病, 1有糖尿病',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `testfile_id` int(11) NULL DEFAULT NULL COMMENT '关联的测试文件ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '糖尿病记录表' ROW_FORMAT = Dynamic;

-- ----------------------------

-- Records of diabetes_record (整合数据，来自dongfang4.29.sql)
-- ----------------------------
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (1, 6, 148, 72, 35, 0, 33.60, 0.627000, 50, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (2, 1, 85, 66, 29, 0, 26.60, 0.351000, 31, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (3, 8, 183, 64, 0, 0, 23.30, 0.672000, 32, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (4, 1, 89, 66, 23, 94, 28.10, 0.167000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (5, 0, 137, 40, 35, 168, 43.10, 2.288000, 33, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (6, 5, 116, 74, 0, 0, 25.60, 0.201000, 30, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (7, 3, 78, 50, 32, 88, 31.00, 0.248000, 26, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (8, 10, 115, 0, 0, 0, 35.30, 0.134000, 29, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (9, 2, 197, 70, 45, 543, 30.50, 0.158000, 53, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (10, 8, 125, 96, 0, 0, 0.00, 0.232000, 54, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (11, 4, 110, 92, 0, 0, 37.60, 0.191000, 30, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (12, 10, 168, 74, 0, 0, 38.00, 0.537000, 34, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (13, 10, 139, 80, 0, 0, 27.10, 1.441000, 57, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (14, 1, 189, 60, 23, 846, 30.10, 0.398000, 59, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (15, 5, 166, 72, 19, 175, 25.80, 0.587000, 51, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (16, 7, 100, 0, 0, 0, 30.00, 0.484000, 32, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (17, 0, 118, 84, 47, 230, 45.80, 0.551000, 31, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (18, 7, 107, 74, 0, 0, 29.60, 0.254000, 31, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (19, 1, 103, 30, 38, 83, 43.30, 0.183000, 33, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (20, 1, 115, 70, 30, 96, 34.60, 0.529000, 32, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (21, 3, 126, 88, 41, 235, 39.30, 0.704000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (22, 8, 99, 84, 0, 0, 35.40, 0.388000, 50, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (23, 7, 196, 90, 0, 0, 39.80, 0.451000, 41, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (24, 9, 119, 80, 35, 0, 29.00, 0.263000, 29, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (25, 11, 143, 94, 33, 146, 36.60, 0.254000, 51, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (26, 10, 125, 70, 26, 115, 31.10, 0.205000, 41, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (27, 7, 147, 76, 0, 0, 39.40, 0.257000, 43, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (28, 1, 97, 66, 15, 140, 23.20, 0.487000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (29, 13, 145, 82, 19, 110, 22.20, 0.245000, 57, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (30, 5, 117, 92, 0, 0, 34.10, 0.337000, 38, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (31, 5, 109, 75, 26, 0, 36.00, 0.546000, 60, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (32, 3, 158, 76, 36, 245, 31.60, 0.851000, 28, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (33, 3, 88, 58, 11, 54, 24.80, 0.267000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (34, 6, 92, 92, 0, 0, 19.90, 0.188000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (35, 10, 122, 78, 31, 0, 27.60, 0.512000, 45, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (36, 4, 103, 60, 33, 192, 24.00, 0.966000, 33, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (37, 11, 138, 76, 0, 0, 33.20, 0.420000, 35, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (38, 9, 102, 76, 37, 0, 32.90, 0.665000, 46, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (39, 2, 90, 68, 42, 0, 38.20, 0.503000, 27, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (40, 4, 111, 72, 47, 207, 37.10, 1.390000, 56, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (41, 3, 180, 64, 25, 70, 34.00, 0.271000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (42, 7, 133, 84, 0, 0, 40.20, 0.696000, 37, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (43, 7, 106, 92, 18, 0, 22.70, 0.235000, 48, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (44, 9, 171, 110, 24, 240, 45.40, 0.721000, 54, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (45, 7, 159, 64, 0, 0, 27.40, 0.294000, 40, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (46, 0, 180, 66, 39, 0, 42.00, 1.893000, 25, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (47, 1, 146, 56, 0, 0, 29.70, 0.564000, 29, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (48, 2, 71, 70, 27, 0, 28.00, 0.586000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (49, 7, 103, 66, 32, 0, 39.10, 0.344000, 31, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (50, 7, 105, 0, 0, 0, 0.00, 0.305000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (51, 1, 103, 80, 11, 82, 19.40, 0.491000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (52, 1, 101, 50, 15, 36, 24.20, 0.526000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (53, 5, 88, 66, 21, 23, 24.40, 0.342000, 30, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (54, 8, 176, 90, 34, 300, 33.70, 0.467000, 58, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (55, 7, 150, 66, 42, 342, 34.70, 0.718000, 42, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (56, 1, 73, 50, 10, 0, 23.00, 0.248000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (57, 7, 187, 68, 39, 304, 37.70, 0.254000, 41, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (58, 0, 100, 88, 60, 110, 46.80, 0.962000, 31, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (59, 0, 146, 82, 0, 0, 40.50, 1.781000, 44, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (60, 0, 105, 64, 41, 142, 41.50, 0.173000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (61, 2, 84, 0, 0, 0, 0.00, 0.304000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (62, 8, 133, 72, 0, 0, 32.90, 0.270000, 39, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (63, 5, 44, 62, 0, 0, 25.00, 0.587000, 36, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (64, 2, 141, 58, 34, 128, 25.40, 0.699000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (65, 7, 114, 66, 0, 0, 32.80, 0.258000, 42, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (66, 5, 99, 74, 27, 0, 29.00, 0.203000, 32, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (67, 0, 109, 88, 30, 0, 32.50, 0.855000, 38, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (68, 2, 109, 92, 0, 0, 42.70, 0.845000, 54, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (69, 1, 95, 66, 13, 38, 19.60, 0.334000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (70, 4, 146, 85, 27, 100, 28.90, 0.189000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (71, 2, 100, 66, 20, 90, 32.90, 0.867000, 28, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (72, 5, 139, 64, 35, 140, 28.60, 0.411000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (73, 13, 126, 90, 0, 0, 43.40, 0.583000, 42, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (74, 4, 129, 86, 20, 270, 35.10, 0.231000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (75, 1, 79, 75, 30, 0, 32.00, 0.396000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (76, 1, 0, 48, 20, 0, 24.70, 0.140000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (77, 7, 62, 78, 0, 0, 32.60, 0.391000, 41, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (78, 5, 95, 72, 33, 0, 37.70, 0.370000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (79, 0, 131, 0, 0, 0, 43.20, 0.270000, 26, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (80, 2, 112, 66, 22, 0, 25.00, 0.307000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (81, 3, 113, 44, 13, 0, 22.40, 0.140000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (82, 2, 74, 0, 0, 0, 0.00, 0.102000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (83, 7, 83, 78, 26, 71, 29.30, 0.767000, 36, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (84, 0, 101, 65, 28, 0, 24.60, 0.237000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (85, 5, 137, 108, 0, 0, 48.80, 0.227000, 37, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (86, 2, 110, 74, 29, 125, 32.40, 0.698000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (87, 13, 106, 72, 54, 0, 36.60, 0.178000, 45, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (88, 2, 100, 68, 25, 71, 38.50, 0.324000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (89, 15, 136, 70, 32, 110, 37.10, 0.153000, 43, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (90, 1, 107, 68, 19, 0, 26.50, 0.165000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (91, 1, 80, 55, 0, 0, 19.10, 0.258000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (92, 4, 123, 80, 15, 176, 32.00, 0.443000, 34, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (93, 7, 81, 78, 40, 48, 46.70, 0.261000, 42, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (94, 4, 134, 72, 0, 0, 23.80, 0.277000, 60, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (95, 2, 142, 82, 18, 64, 24.70, 0.761000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (96, 6, 144, 72, 27, 228, 33.90, 0.255000, 40, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (97, 2, 92, 62, 28, 0, 31.60, 0.130000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (98, 1, 71, 48, 18, 76, 20.40, 0.323000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (99, 6, 93, 50, 30, 64, 28.70, 0.356000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (100, 1, 122, 90, 51, 220, 49.70, 0.325000, 31, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (101, 1, 163, 72, 0, 0, 39.00, 1.222000, 33, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (102, 1, 151, 60, 0, 0, 26.10, 0.179000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (103, 0, 125, 96, 0, 0, 22.50, 0.262000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (104, 1, 81, 72, 18, 40, 26.60, 0.283000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (105, 2, 85, 65, 0, 0, 39.60, 0.930000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (106, 1, 126, 56, 29, 152, 28.70, 0.801000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (107, 1, 96, 122, 0, 0, 22.40, 0.207000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (108, 4, 144, 58, 28, 140, 29.50, 0.287000, 37, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (109, 3, 83, 58, 31, 18, 34.30, 0.336000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (110, 0, 95, 85, 25, 36, 37.40, 0.247000, 24, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (111, 3, 171, 72, 33, 135, 33.30, 0.199000, 24, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (112, 8, 155, 62, 26, 495, 34.00, 0.543000, 46, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (113, 1, 89, 76, 34, 37, 31.20, 0.192000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (114, 4, 76, 62, 0, 0, 34.00, 0.391000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (115, 7, 160, 54, 32, 175, 30.50, 0.588000, 39, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (116, 4, 146, 92, 0, 0, 31.20, 0.539000, 61, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (117, 5, 124, 74, 0, 0, 34.00, 0.220000, 38, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (118, 5, 78, 48, 0, 0, 33.70, 0.654000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (119, 4, 97, 60, 23, 0, 28.20, 0.443000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (120, 4, 99, 76, 15, 51, 23.20, 0.223000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (121, 0, 162, 76, 56, 100, 53.20, 0.759000, 25, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (122, 6, 111, 64, 39, 0, 34.20, 0.260000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (123, 2, 107, 74, 30, 100, 33.60, 0.404000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (124, 5, 132, 80, 0, 0, 26.80, 0.186000, 69, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (125, 0, 113, 76, 0, 0, 33.30, 0.278000, 23, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (126, 1, 88, 30, 42, 99, 55.00, 0.496000, 26, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (127, 3, 120, 70, 30, 135, 42.90, 0.452000, 30, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (128, 1, 118, 58, 36, 94, 33.30, 0.261000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (129, 1, 117, 88, 24, 145, 34.50, 0.403000, 40, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (130, 0, 105, 84, 0, 0, 27.90, 0.741000, 62, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (131, 4, 173, 70, 14, 168, 29.70, 0.361000, 33, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (132, 9, 122, 56, 0, 0, 33.30, 1.114000, 33, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (133, 3, 170, 64, 37, 225, 34.50, 0.356000, 30, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (134, 8, 84, 74, 31, 0, 38.30, 0.457000, 39, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (135, 2, 96, 68, 13, 49, 21.10, 0.647000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (136, 2, 125, 60, 20, 140, 33.80, 0.088000, 31, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (137, 0, 100, 70, 26, 50, 30.80, 0.597000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (138, 0, 93, 60, 25, 92, 28.70, 0.532000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (139, 0, 129, 80, 0, 0, 31.20, 0.703000, 29, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (140, 5, 105, 72, 29, 325, 36.90, 0.159000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (141, 3, 128, 78, 0, 0, 21.10, 0.268000, 55, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (142, 5, 106, 82, 30, 0, 39.50, 0.286000, 38, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (143, 2, 108, 52, 26, 63, 32.50, 0.318000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (144, 10, 108, 66, 0, 0, 32.40, 0.272000, 42, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (145, 4, 154, 62, 31, 284, 32.80, 0.237000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (146, 0, 102, 75, 23, 0, 0.00, 0.572000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (147, 9, 57, 80, 37, 0, 32.80, 0.096000, 41, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (148, 2, 106, 64, 35, 119, 30.50, 1.400000, 34, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (149, 5, 147, 78, 0, 0, 33.70, 0.218000, 65, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (150, 2, 90, 70, 17, 0, 27.30, 0.085000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (151, 1, 136, 74, 50, 204, 37.40, 0.399000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (152, 4, 114, 65, 0, 0, 21.90, 0.432000, 37, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (153, 9, 156, 86, 28, 155, 34.30, 1.189000, 42, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (154, 1, 153, 82, 42, 485, 40.60, 0.687000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (155, 8, 188, 78, 0, 0, 47.90, 0.137000, 43, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (156, 7, 152, 88, 44, 0, 50.00, 0.337000, 36, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (157, 2, 99, 52, 15, 94, 24.60, 0.637000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (158, 1, 109, 56, 21, 135, 25.20, 0.833000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (159, 2, 88, 74, 19, 53, 29.00, 0.229000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (160, 17, 163, 72, 41, 114, 40.90, 0.817000, 47, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (161, 4, 151, 90, 38, 0, 29.70, 0.294000, 36, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (162, 7, 102, 74, 40, 105, 37.20, 0.204000, 45, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (163, 0, 114, 80, 34, 285, 44.20, 0.167000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (164, 2, 100, 64, 23, 0, 29.70, 0.368000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (165, 0, 131, 88, 0, 0, 31.60, 0.743000, 32, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (166, 6, 104, 74, 18, 156, 29.90, 0.722000, 41, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (167, 3, 148, 66, 25, 0, 32.50, 0.256000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (168, 4, 120, 68, 0, 0, 29.60, 0.709000, 34, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (169, 4, 110, 66, 0, 0, 31.90, 0.471000, 29, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (170, 3, 111, 90, 12, 78, 28.40, 0.495000, 29, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (171, 6, 102, 82, 0, 0, 30.80, 0.180000, 36, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (172, 6, 134, 70, 23, 130, 35.40, 0.542000, 29, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (173, 2, 87, 0, 23, 0, 28.90, 0.773000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (174, 1, 79, 60, 42, 48, 43.50, 0.678000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (175, 2, 75, 64, 24, 55, 29.70, 0.370000, 33, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (176, 8, 179, 72, 42, 130, 32.70, 0.719000, 36, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (177, 6, 85, 78, 0, 0, 31.20, 0.382000, 42, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (178, 0, 129, 110, 46, 130, 67.10, 0.319000, 26, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (179, 5, 143, 78, 0, 0, 45.00, 0.190000, 47, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (180, 5, 130, 82, 0, 0, 39.10, 0.956000, 37, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (181, 6, 87, 80, 0, 0, 23.20, 0.084000, 32, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (182, 0, 119, 64, 18, 92, 34.90, 0.725000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (183, 1, 0, 74, 20, 23, 27.70, 0.299000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (184, 5, 73, 60, 0, 0, 26.80, 0.268000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (185, 4, 141, 74, 0, 0, 27.60, 0.244000, 40, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (186, 7, 194, 68, 28, 0, 35.90, 0.745000, 41, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (187, 8, 181, 68, 36, 495, 30.10, 0.615000, 60, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (188, 1, 128, 98, 41, 58, 32.00, 1.321000, 33, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (189, 8, 109, 76, 39, 114, 27.90, 0.640000, 31, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (190, 5, 139, 80, 35, 160, 31.60, 0.361000, 25, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (191, 3, 111, 62, 0, 0, 22.60, 0.142000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (192, 9, 123, 70, 44, 94, 33.10, 0.374000, 40, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (193, 7, 159, 66, 0, 0, 30.40, 0.383000, 36, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (194, 11, 135, 0, 0, 0, 52.30, 0.578000, 40, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (195, 8, 85, 55, 20, 0, 24.40, 0.136000, 42, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (196, 5, 158, 84, 41, 210, 39.40, 0.395000, 29, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (197, 1, 105, 58, 0, 0, 24.30, 0.187000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (198, 3, 107, 62, 13, 48, 22.90, 0.678000, 23, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (199, 4, 109, 64, 44, 99, 34.80, 0.905000, 26, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (200, 4, 148, 60, 27, 318, 30.90, 0.150000, 29, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (201, 0, 113, 80, 16, 0, 31.00, 0.874000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (202, 1, 138, 82, 0, 0, 40.10, 0.236000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (203, 0, 108, 68, 20, 0, 27.30, 0.787000, 32, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (204, 2, 99, 70, 16, 44, 20.40, 0.235000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (205, 6, 103, 72, 32, 190, 37.70, 0.324000, 55, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (206, 5, 111, 72, 28, 0, 23.90, 0.407000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (207, 8, 196, 76, 29, 280, 37.50, 0.605000, 57, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (208, 5, 162, 104, 0, 0, 37.70, 0.151000, 52, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (209, 1, 96, 64, 27, 87, 33.20, 0.289000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (210, 7, 184, 84, 33, 0, 35.50, 0.355000, 41, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (211, 2, 81, 60, 22, 0, 27.70, 0.290000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (212, 0, 147, 85, 54, 0, 42.80, 0.375000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (213, 7, 179, 95, 31, 0, 34.20, 0.164000, 60, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (214, 0, 140, 65, 26, 130, 42.60, 0.431000, 24, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (215, 9, 112, 82, 32, 175, 34.20, 0.260000, 36, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (216, 12, 151, 70, 40, 271, 41.80, 0.742000, 38, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (217, 5, 109, 62, 41, 129, 35.80, 0.514000, 25, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (218, 6, 125, 68, 30, 120, 30.00, 0.464000, 32, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (219, 5, 85, 74, 22, 0, 29.00, 1.224000, 32, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (220, 5, 112, 66, 0, 0, 37.80, 0.261000, 41, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (221, 0, 177, 60, 29, 478, 34.60, 1.072000, 21, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (222, 2, 158, 90, 0, 0, 31.60, 0.805000, 66, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (223, 7, 119, 0, 0, 0, 25.20, 0.209000, 37, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (224, 7, 142, 60, 33, 190, 28.80, 0.687000, 61, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (225, 1, 100, 66, 15, 56, 23.60, 0.666000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (226, 1, 87, 78, 27, 32, 34.60, 0.101000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (227, 0, 101, 76, 0, 0, 35.70, 0.198000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (228, 3, 162, 52, 38, 0, 37.20, 0.652000, 24, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (229, 4, 197, 70, 39, 744, 36.70, 2.329000, 31, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (230, 0, 117, 80, 31, 53, 45.20, 0.089000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (231, 4, 142, 86, 0, 0, 44.00, 0.645000, 22, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (232, 6, 134, 80, 37, 370, 46.20, 0.238000, 46, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (233, 1, 79, 80, 25, 37, 25.40, 0.583000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (234, 4, 122, 68, 0, 0, 35.00, 0.394000, 29, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (235, 3, 74, 68, 28, 45, 29.70, 0.293000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (236, 4, 171, 72, 0, 0, 43.60, 0.479000, 26, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (237, 7, 181, 84, 21, 192, 35.90, 0.586000, 51, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (238, 0, 179, 90, 27, 0, 44.10, 0.686000, 23, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (239, 9, 164, 84, 21, 0, 30.80, 0.831000, 32, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (240, 0, 104, 76, 0, 0, 18.40, 0.582000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (241, 1, 91, 64, 24, 0, 29.20, 0.192000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (242, 4, 91, 70, 32, 88, 33.10, 0.446000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (243, 3, 139, 54, 0, 0, 25.60, 0.402000, 22, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (244, 6, 119, 50, 22, 176, 27.10, 1.318000, 33, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (245, 2, 146, 76, 35, 194, 38.20, 0.329000, 29, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (246, 9, 184, 85, 15, 0, 30.00, 1.213000, 49, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (247, 10, 122, 68, 0, 0, 31.20, 0.258000, 41, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (248, 0, 165, 90, 33, 680, 52.30, 0.427000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (249, 9, 124, 70, 33, 402, 35.40, 0.282000, 34, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (250, 1, 111, 86, 19, 0, 30.10, 0.143000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (251, 9, 106, 52, 0, 0, 31.20, 0.380000, 42, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (252, 2, 129, 84, 0, 0, 28.00, 0.284000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (253, 2, 90, 80, 14, 55, 24.40, 0.249000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (254, 0, 86, 68, 32, 0, 35.80, 0.238000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (255, 12, 92, 62, 7, 258, 27.60, 0.926000, 44, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (256, 1, 113, 64, 35, 0, 33.60, 0.543000, 21, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (257, 3, 111, 56, 39, 0, 30.10, 0.557000, 30, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (258, 2, 114, 68, 22, 0, 28.70, 0.092000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (259, 1, 193, 50, 16, 375, 25.90, 0.655000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (260, 11, 155, 76, 28, 150, 33.30, 1.353000, 51, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (261, 3, 191, 68, 15, 130, 30.90, 0.299000, 34, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (262, 3, 141, 0, 0, 0, 30.00, 0.761000, 27, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (263, 4, 95, 70, 32, 0, 32.10, 0.612000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (264, 3, 142, 80, 15, 0, 32.40, 0.200000, 63, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (265, 4, 123, 62, 0, 0, 32.00, 0.226000, 35, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (266, 5, 96, 74, 18, 67, 33.60, 0.997000, 43, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (267, 0, 138, 0, 0, 0, 36.30, 0.933000, 25, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (268, 2, 128, 64, 42, 0, 40.00, 1.101000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (269, 0, 102, 52, 0, 0, 25.10, 0.078000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (270, 2, 146, 0, 0, 0, 27.50, 0.240000, 28, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (271, 10, 101, 86, 37, 0, 45.60, 1.136000, 38, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (272, 2, 108, 62, 32, 56, 25.20, 0.128000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (273, 3, 122, 78, 0, 0, 23.00, 0.254000, 40, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (274, 1, 71, 78, 50, 45, 33.20, 0.422000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (275, 13, 106, 70, 0, 0, 34.20, 0.251000, 52, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (276, 2, 100, 70, 52, 57, 40.50, 0.677000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (277, 7, 106, 60, 24, 0, 26.50, 0.296000, 29, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (278, 0, 104, 64, 23, 116, 27.80, 0.454000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (279, 5, 114, 74, 0, 0, 24.90, 0.744000, 57, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (280, 2, 108, 62, 10, 278, 25.30, 0.881000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (281, 0, 146, 70, 0, 0, 37.90, 0.334000, 28, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (282, 10, 129, 76, 28, 122, 35.90, 0.280000, 39, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (283, 7, 133, 88, 15, 155, 32.40, 0.262000, 37, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (284, 7, 161, 86, 0, 0, 30.40, 0.165000, 47, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (285, 2, 108, 80, 0, 0, 27.00, 0.259000, 52, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (286, 7, 136, 74, 26, 135, 26.00, 0.647000, 51, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (287, 5, 155, 84, 44, 545, 38.70, 0.619000, 34, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (288, 1, 119, 86, 39, 220, 45.60, 0.808000, 29, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (289, 4, 96, 56, 17, 49, 20.80, 0.340000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (290, 5, 108, 72, 43, 75, 36.10, 0.263000, 33, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (291, 0, 78, 88, 29, 40, 36.90, 0.434000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (292, 0, 107, 62, 30, 74, 36.60, 0.757000, 25, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (293, 2, 128, 78, 37, 182, 43.30, 1.224000, 31, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (294, 1, 128, 48, 45, 194, 40.50, 0.613000, 24, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (295, 0, 161, 50, 0, 0, 21.90, 0.254000, 65, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (296, 6, 151, 62, 31, 120, 35.50, 0.692000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (297, 2, 146, 70, 38, 360, 28.00, 0.337000, 29, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (298, 0, 126, 84, 29, 215, 30.70, 0.520000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (299, 14, 100, 78, 25, 184, 36.60, 0.412000, 46, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (300, 8, 112, 72, 0, 0, 23.60, 0.840000, 58, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (301, 0, 167, 0, 0, 0, 32.30, 0.839000, 30, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (302, 2, 144, 58, 33, 135, 31.60, 0.422000, 25, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (303, 5, 77, 82, 41, 42, 35.80, 0.156000, 35, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (304, 5, 115, 98, 0, 0, 52.90, 0.209000, 28, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (305, 3, 150, 76, 0, 0, 21.00, 0.207000, 37, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (306, 2, 120, 76, 37, 105, 39.70, 0.215000, 29, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (307, 10, 161, 68, 23, 132, 25.50, 0.326000, 47, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (308, 0, 137, 68, 14, 148, 24.80, 0.143000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (309, 0, 128, 68, 19, 180, 30.50, 1.391000, 25, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (310, 2, 124, 68, 28, 205, 32.90, 0.875000, 30, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (311, 6, 80, 66, 30, 0, 26.20, 0.313000, 41, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (312, 0, 106, 70, 37, 148, 39.40, 0.605000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (313, 2, 155, 74, 17, 96, 26.60, 0.433000, 27, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (314, 3, 113, 50, 10, 85, 29.50, 0.626000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (315, 7, 109, 80, 31, 0, 35.90, 1.127000, 43, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (316, 2, 112, 68, 22, 94, 34.10, 0.315000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (317, 3, 99, 80, 11, 64, 19.30, 0.284000, 30, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (318, 3, 182, 74, 0, 0, 30.50, 0.345000, 29, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (319, 3, 115, 66, 39, 140, 38.10, 0.150000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (320, 6, 194, 78, 0, 0, 23.50, 0.129000, 59, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (321, 4, 129, 60, 12, 231, 27.50, 0.527000, 31, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (322, 3, 112, 74, 30, 0, 31.60, 0.197000, 25, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (323, 0, 124, 70, 20, 0, 27.40, 0.254000, 36, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (324, 13, 152, 90, 33, 29, 26.80, 0.731000, 43, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (325, 2, 112, 75, 32, 0, 35.70, 0.148000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (326, 1, 157, 72, 21, 168, 25.60, 0.123000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (327, 1, 122, 64, 32, 156, 35.10, 0.692000, 30, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (328, 10, 179, 70, 0, 0, 35.10, 0.200000, 37, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (329, 2, 102, 86, 36, 120, 45.50, 0.127000, 23, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (330, 6, 105, 70, 32, 68, 30.80, 0.122000, 37, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (331, 8, 118, 72, 19, 0, 23.10, 1.476000, 46, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (332, 2, 87, 58, 16, 52, 32.70, 0.166000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (333, 1, 180, 0, 0, 0, 43.30, 0.282000, 41, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (334, 12, 106, 80, 0, 0, 23.60, 0.137000, 44, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (335, 1, 95, 60, 18, 58, 23.90, 0.260000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (336, 0, 165, 76, 43, 255, 47.90, 0.259000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (337, 0, 117, 0, 0, 0, 33.80, 0.932000, 44, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (338, 5, 115, 76, 0, 0, 31.20, 0.343000, 44, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (339, 9, 152, 78, 34, 171, 34.20, 0.893000, 33, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (340, 7, 178, 84, 0, 0, 39.90, 0.331000, 41, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (341, 1, 130, 70, 13, 105, 25.90, 0.472000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (342, 1, 95, 74, 21, 73, 25.90, 0.673000, 36, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (343, 1, 0, 68, 35, 0, 32.00, 0.389000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (344, 5, 122, 86, 0, 0, 34.70, 0.290000, 33, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (345, 8, 95, 72, 0, 0, 36.80, 0.485000, 57, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (346, 8, 126, 88, 36, 108, 38.50, 0.349000, 49, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (347, 1, 139, 46, 19, 83, 28.70, 0.654000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (348, 3, 116, 0, 0, 0, 23.50, 0.187000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (349, 3, 99, 62, 19, 74, 21.80, 0.279000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (350, 5, 0, 80, 32, 0, 41.00, 0.346000, 37, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (351, 4, 92, 80, 0, 0, 42.20, 0.237000, 29, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (352, 4, 137, 84, 0, 0, 31.20, 0.252000, 30, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (353, 3, 61, 82, 28, 0, 34.40, 0.243000, 46, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (354, 1, 90, 62, 12, 43, 27.20, 0.580000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (355, 3, 90, 78, 0, 0, 42.70, 0.559000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (356, 9, 165, 88, 0, 0, 30.40, 0.302000, 49, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (357, 1, 125, 50, 40, 167, 33.30, 0.962000, 28, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (358, 13, 129, 0, 30, 0, 39.90, 0.569000, 44, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (359, 12, 88, 74, 40, 54, 35.30, 0.378000, 48, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (360, 1, 196, 76, 36, 249, 36.50, 0.875000, 29, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (361, 5, 189, 64, 33, 325, 31.20, 0.583000, 29, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (362, 5, 158, 70, 0, 0, 29.80, 0.207000, 63, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (363, 5, 103, 108, 37, 0, 39.20, 0.305000, 65, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (364, 4, 146, 78, 0, 0, 38.50, 0.520000, 67, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (365, 4, 147, 74, 25, 293, 34.90, 0.385000, 30, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (366, 5, 99, 54, 28, 83, 34.00, 0.499000, 30, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (367, 6, 124, 72, 0, 0, 27.60, 0.368000, 29, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (368, 0, 101, 64, 17, 0, 21.00, 0.252000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (369, 3, 81, 86, 16, 66, 27.50, 0.306000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (370, 1, 133, 102, 28, 140, 32.80, 0.234000, 45, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (371, 3, 173, 82, 48, 465, 38.40, 2.137000, 25, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (372, 0, 118, 64, 23, 89, 0.00, 1.731000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (373, 0, 84, 64, 22, 66, 35.80, 0.545000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (374, 2, 105, 58, 40, 94, 34.90, 0.225000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (375, 2, 122, 52, 43, 158, 36.20, 0.816000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (376, 12, 140, 82, 43, 325, 39.20, 0.528000, 58, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (377, 0, 98, 82, 15, 84, 25.20, 0.299000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (378, 1, 87, 60, 37, 75, 37.20, 0.509000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (379, 4, 156, 75, 0, 0, 48.30, 0.238000, 32, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (380, 0, 93, 100, 39, 72, 43.40, 1.021000, 35, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (381, 1, 107, 72, 30, 82, 30.80, 0.821000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (382, 0, 105, 68, 22, 0, 20.00, 0.236000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (383, 1, 109, 60, 8, 182, 25.40, 0.947000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (384, 1, 90, 62, 18, 59, 25.10, 1.268000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (385, 1, 125, 70, 24, 110, 24.30, 0.221000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (386, 1, 119, 54, 13, 50, 22.30, 0.205000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (387, 5, 116, 74, 29, 0, 32.30, 0.660000, 35, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (388, 8, 105, 100, 36, 0, 43.30, 0.239000, 45, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (389, 5, 144, 82, 26, 285, 32.00, 0.452000, 58, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (390, 3, 100, 68, 23, 81, 31.60, 0.949000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (391, 1, 100, 66, 29, 196, 32.00, 0.444000, 42, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (392, 5, 166, 76, 0, 0, 45.70, 0.340000, 27, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (393, 1, 131, 64, 14, 415, 23.70, 0.389000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (394, 4, 116, 72, 12, 87, 22.10, 0.463000, 37, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (395, 4, 158, 78, 0, 0, 32.90, 0.803000, 31, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (396, 2, 127, 58, 24, 275, 27.70, 1.600000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (397, 3, 96, 56, 34, 115, 24.70, 0.944000, 39, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (398, 0, 131, 66, 40, 0, 34.30, 0.196000, 22, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (399, 3, 82, 70, 0, 0, 21.10, 0.389000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (400, 3, 193, 70, 31, 0, 34.90, 0.241000, 25, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (401, 4, 95, 64, 0, 0, 32.00, 0.161000, 31, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (402, 6, 137, 61, 0, 0, 24.20, 0.151000, 55, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (403, 5, 136, 84, 41, 88, 35.00, 0.286000, 35, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (404, 9, 72, 78, 25, 0, 31.60, 0.280000, 38, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (405, 5, 168, 64, 0, 0, 32.90, 0.135000, 41, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (406, 2, 123, 48, 32, 165, 42.10, 0.520000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (407, 4, 115, 72, 0, 0, 28.90, 0.376000, 46, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (408, 0, 101, 62, 0, 0, 21.90, 0.336000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (409, 8, 197, 74, 0, 0, 25.90, 1.191000, 39, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (410, 1, 172, 68, 49, 579, 42.40, 0.702000, 28, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (411, 6, 102, 90, 39, 0, 35.70, 0.674000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (412, 1, 112, 72, 30, 176, 34.40, 0.528000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (413, 1, 143, 84, 23, 310, 42.40, 1.076000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (414, 1, 143, 74, 22, 61, 26.20, 0.256000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (415, 0, 138, 60, 35, 167, 34.60, 0.534000, 21, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (416, 3, 173, 84, 33, 474, 35.70, 0.258000, 22, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (417, 1, 97, 68, 21, 0, 27.20, 1.095000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (418, 4, 144, 82, 32, 0, 38.50, 0.554000, 37, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (419, 1, 83, 68, 0, 0, 18.20, 0.624000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (420, 3, 129, 64, 29, 115, 26.40, 0.219000, 28, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (421, 1, 119, 88, 41, 170, 45.30, 0.507000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (422, 2, 94, 68, 18, 76, 26.00, 0.561000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (423, 0, 102, 64, 46, 78, 40.60, 0.496000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (424, 2, 115, 64, 22, 0, 30.80, 0.421000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (425, 8, 151, 78, 32, 210, 42.90, 0.516000, 36, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (426, 4, 184, 78, 39, 277, 37.00, 0.264000, 31, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (427, 0, 94, 0, 0, 0, 0.00, 0.256000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (428, 1, 181, 64, 30, 180, 34.10, 0.328000, 38, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (429, 0, 135, 94, 46, 145, 40.60, 0.284000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (430, 1, 95, 82, 25, 180, 35.00, 0.233000, 43, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (431, 2, 99, 0, 0, 0, 22.20, 0.108000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (432, 3, 89, 74, 16, 85, 30.40, 0.551000, 38, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (433, 1, 80, 74, 11, 60, 30.00, 0.527000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (434, 2, 139, 75, 0, 0, 25.60, 0.167000, 29, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (435, 1, 90, 68, 8, 0, 24.50, 1.138000, 36, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (436, 0, 141, 0, 0, 0, 42.40, 0.205000, 29, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (437, 12, 140, 85, 33, 0, 37.40, 0.244000, 41, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (438, 5, 147, 75, 0, 0, 29.90, 0.434000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (439, 1, 97, 70, 15, 0, 18.20, 0.147000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (440, 6, 107, 88, 0, 0, 36.80, 0.727000, 31, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (441, 0, 189, 104, 25, 0, 34.30, 0.435000, 41, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (442, 2, 83, 66, 23, 50, 32.20, 0.497000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (443, 4, 117, 64, 27, 120, 33.20, 0.230000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (444, 8, 108, 70, 0, 0, 30.50, 0.955000, 33, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (445, 4, 117, 62, 12, 0, 29.70, 0.380000, 30, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (446, 0, 180, 78, 63, 14, 59.40, 2.420000, 25, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (447, 1, 100, 72, 12, 70, 25.30, 0.658000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (448, 0, 95, 80, 45, 92, 36.50, 0.330000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (449, 0, 104, 64, 37, 64, 33.60, 0.510000, 22, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (450, 0, 120, 74, 18, 63, 30.50, 0.285000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (451, 1, 82, 64, 13, 95, 21.20, 0.415000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (452, 2, 134, 70, 0, 0, 28.90, 0.542000, 23, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (453, 0, 91, 68, 32, 210, 39.90, 0.381000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (454, 2, 119, 0, 0, 0, 19.60, 0.832000, 72, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (455, 2, 100, 54, 28, 105, 37.80, 0.498000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (456, 14, 175, 62, 30, 0, 33.60, 0.212000, 38, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (457, 1, 135, 54, 0, 0, 26.70, 0.687000, 62, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (458, 5, 86, 68, 28, 71, 30.20, 0.364000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (459, 10, 148, 84, 48, 237, 37.60, 1.001000, 51, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (460, 9, 134, 74, 33, 60, 25.90, 0.460000, 81, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (461, 9, 120, 72, 22, 56, 20.80, 0.733000, 48, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (462, 1, 71, 62, 0, 0, 21.80, 0.416000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (463, 8, 74, 70, 40, 49, 35.30, 0.705000, 39, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (464, 5, 88, 78, 30, 0, 27.60, 0.258000, 37, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (465, 10, 115, 98, 0, 0, 24.00, 1.022000, 34, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (466, 0, 124, 56, 13, 105, 21.80, 0.452000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (467, 0, 74, 52, 10, 36, 27.80, 0.269000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (468, 0, 97, 64, 36, 100, 36.80, 0.600000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (469, 8, 120, 0, 0, 0, 30.00, 0.183000, 38, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (470, 6, 154, 78, 41, 140, 46.10, 0.571000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (471, 1, 144, 82, 40, 0, 41.30, 0.607000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (472, 0, 137, 70, 38, 0, 33.20, 0.170000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (473, 0, 119, 66, 27, 0, 38.80, 0.259000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (474, 7, 136, 90, 0, 0, 29.90, 0.210000, 50, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (475, 4, 114, 64, 0, 0, 28.90, 0.126000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (476, 0, 137, 84, 27, 0, 27.30, 0.231000, 59, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (477, 2, 105, 80, 45, 191, 33.70, 0.711000, 29, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (478, 7, 114, 76, 17, 110, 23.80, 0.466000, 31, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (479, 8, 126, 74, 38, 75, 25.90, 0.162000, 39, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (480, 4, 132, 86, 31, 0, 28.00, 0.419000, 63, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (481, 3, 158, 70, 30, 328, 35.50, 0.344000, 35, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (482, 0, 123, 88, 37, 0, 35.20, 0.197000, 29, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (483, 4, 85, 58, 22, 49, 27.80, 0.306000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (484, 0, 84, 82, 31, 125, 38.20, 0.233000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (485, 0, 145, 0, 0, 0, 44.20, 0.630000, 31, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (486, 0, 135, 68, 42, 250, 42.30, 0.365000, 24, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (487, 1, 139, 62, 41, 480, 40.70, 0.536000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (488, 0, 173, 78, 32, 265, 46.50, 1.159000, 58, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (489, 4, 99, 72, 17, 0, 25.60, 0.294000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (490, 8, 194, 80, 0, 0, 26.10, 0.551000, 67, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (491, 2, 83, 65, 28, 66, 36.80, 0.629000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (492, 2, 89, 90, 30, 0, 33.50, 0.292000, 42, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (493, 4, 99, 68, 38, 0, 32.80, 0.145000, 33, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (494, 4, 125, 70, 18, 122, 28.90, 1.144000, 45, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (495, 3, 80, 0, 0, 0, 0.00, 0.174000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (496, 6, 166, 74, 0, 0, 26.60, 0.304000, 66, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (497, 5, 110, 68, 0, 0, 26.00, 0.292000, 30, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (498, 2, 81, 72, 15, 76, 30.10, 0.547000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (499, 7, 195, 70, 33, 145, 25.10, 0.163000, 55, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (500, 6, 154, 74, 32, 193, 29.30, 0.839000, 39, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (501, 2, 117, 90, 19, 71, 25.20, 0.313000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (502, 3, 84, 72, 32, 0, 37.20, 0.267000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (503, 6, 0, 68, 41, 0, 39.00, 0.727000, 41, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (504, 7, 94, 64, 25, 79, 33.30, 0.738000, 41, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (505, 3, 96, 78, 39, 0, 37.30, 0.238000, 40, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (506, 10, 75, 82, 0, 0, 33.30, 0.263000, 38, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (507, 0, 180, 90, 26, 90, 36.50, 0.314000, 35, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (508, 1, 130, 60, 23, 170, 28.60, 0.692000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (509, 2, 84, 50, 23, 76, 30.40, 0.968000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (510, 8, 120, 78, 0, 0, 25.00, 0.409000, 64, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (511, 12, 84, 72, 31, 0, 29.70, 0.297000, 46, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (512, 0, 139, 62, 17, 210, 22.10, 0.207000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (513, 9, 91, 68, 0, 0, 24.20, 0.200000, 58, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (514, 2, 91, 62, 0, 0, 27.30, 0.525000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (515, 3, 99, 54, 19, 86, 25.60, 0.154000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (516, 3, 163, 70, 18, 105, 31.60, 0.268000, 28, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (517, 9, 145, 88, 34, 165, 30.30, 0.771000, 53, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (518, 7, 125, 86, 0, 0, 37.60, 0.304000, 51, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (519, 13, 76, 60, 0, 0, 32.80, 0.180000, 41, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (520, 6, 129, 90, 7, 326, 19.60, 0.582000, 60, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (521, 2, 68, 70, 32, 66, 25.00, 0.187000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (522, 3, 124, 80, 33, 130, 33.20, 0.305000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (523, 6, 114, 0, 0, 0, 0.00, 0.189000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (524, 9, 130, 70, 0, 0, 34.20, 0.652000, 45, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (525, 3, 125, 58, 0, 0, 31.60, 0.151000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (526, 3, 87, 60, 18, 0, 21.80, 0.444000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (527, 1, 97, 64, 19, 82, 18.20, 0.299000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (528, 3, 116, 74, 15, 105, 26.30, 0.107000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (529, 0, 117, 66, 31, 188, 30.80, 0.493000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (530, 0, 111, 65, 0, 0, 24.60, 0.660000, 31, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (531, 2, 122, 60, 18, 106, 29.80, 0.717000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (532, 0, 107, 76, 0, 0, 45.30, 0.686000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (533, 1, 86, 66, 52, 65, 41.30, 0.917000, 29, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (534, 6, 91, 0, 0, 0, 29.80, 0.501000, 31, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (535, 1, 77, 56, 30, 56, 33.30, 1.251000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (536, 4, 132, 0, 0, 0, 32.90, 0.302000, 23, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (537, 0, 105, 90, 0, 0, 29.60, 0.197000, 46, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (538, 0, 57, 60, 0, 0, 21.70, 0.735000, 67, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (539, 0, 127, 80, 37, 210, 36.30, 0.804000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (540, 3, 129, 92, 49, 155, 36.40, 0.968000, 32, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (541, 8, 100, 74, 40, 215, 39.40, 0.661000, 43, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (542, 3, 128, 72, 25, 190, 32.40, 0.549000, 27, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (543, 10, 90, 85, 32, 0, 34.90, 0.825000, 56, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (544, 4, 84, 90, 23, 56, 39.50, 0.159000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (545, 1, 88, 78, 29, 76, 32.00, 0.365000, 29, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (546, 8, 186, 90, 35, 225, 34.50, 0.423000, 37, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (547, 5, 187, 76, 27, 207, 43.60, 1.034000, 53, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (548, 4, 131, 68, 21, 166, 33.10, 0.160000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (549, 1, 164, 82, 43, 67, 32.80, 0.341000, 50, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (550, 4, 189, 110, 31, 0, 28.50, 0.680000, 37, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (551, 1, 116, 70, 28, 0, 27.40, 0.204000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (552, 3, 84, 68, 30, 106, 31.90, 0.591000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (553, 6, 114, 88, 0, 0, 27.80, 0.247000, 66, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (554, 1, 88, 62, 24, 44, 29.90, 0.422000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (555, 1, 84, 64, 23, 115, 36.90, 0.471000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (556, 7, 124, 70, 33, 215, 25.50, 0.161000, 37, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (557, 1, 97, 70, 40, 0, 38.10, 0.218000, 30, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (558, 8, 110, 76, 0, 0, 27.80, 0.237000, 58, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (559, 11, 103, 68, 40, 0, 46.20, 0.126000, 42, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (560, 11, 85, 74, 0, 0, 30.10, 0.300000, 35, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (561, 6, 125, 76, 0, 0, 33.80, 0.121000, 54, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (562, 0, 198, 66, 32, 274, 41.30, 0.502000, 28, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (563, 1, 87, 68, 34, 77, 37.60, 0.401000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (564, 6, 99, 60, 19, 54, 26.90, 0.497000, 32, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (565, 0, 91, 80, 0, 0, 32.40, 0.601000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (566, 2, 95, 54, 14, 88, 26.10, 0.748000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (567, 1, 99, 72, 30, 18, 38.60, 0.412000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (568, 6, 92, 62, 32, 126, 32.00, 0.085000, 46, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (569, 4, 154, 72, 29, 126, 31.30, 0.338000, 37, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (570, 0, 121, 66, 30, 165, 34.30, 0.203000, 33, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (571, 3, 78, 70, 0, 0, 32.50, 0.270000, 39, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (572, 2, 130, 96, 0, 0, 22.60, 0.268000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (573, 3, 111, 58, 31, 44, 29.50, 0.430000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (574, 2, 98, 60, 17, 120, 34.70, 0.198000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (575, 1, 143, 86, 30, 330, 30.10, 0.892000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (576, 1, 119, 44, 47, 63, 35.50, 0.280000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (577, 6, 108, 44, 20, 130, 24.00, 0.813000, 35, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (578, 2, 118, 80, 0, 0, 42.90, 0.693000, 21, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (579, 10, 133, 68, 0, 0, 27.00, 0.245000, 36, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (580, 2, 197, 70, 99, 0, 34.70, 0.575000, 62, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (581, 0, 151, 90, 46, 0, 42.10, 0.371000, 21, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (582, 6, 109, 60, 27, 0, 25.00, 0.206000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (583, 12, 121, 78, 17, 0, 26.50, 0.259000, 62, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (584, 8, 100, 76, 0, 0, 38.70, 0.190000, 42, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (585, 8, 124, 76, 24, 600, 28.70, 0.687000, 52, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (586, 1, 93, 56, 11, 0, 22.50, 0.417000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (587, 8, 143, 66, 0, 0, 34.90, 0.129000, 41, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (588, 6, 103, 66, 0, 0, 24.30, 0.249000, 29, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (589, 3, 176, 86, 27, 156, 33.30, 1.154000, 52, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (590, 0, 73, 0, 0, 0, 21.10, 0.342000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (591, 11, 111, 84, 40, 0, 46.80, 0.925000, 45, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (592, 2, 112, 78, 50, 140, 39.40, 0.175000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (593, 3, 132, 80, 0, 0, 34.40, 0.402000, 44, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (594, 2, 82, 52, 22, 115, 28.50, 1.699000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (595, 6, 123, 72, 45, 230, 33.60, 0.733000, 34, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (596, 0, 188, 82, 14, 185, 32.00, 0.682000, 22, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (597, 0, 67, 76, 0, 0, 45.30, 0.194000, 46, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (598, 1, 89, 24, 19, 25, 27.80, 0.559000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (599, 1, 173, 74, 0, 0, 36.80, 0.088000, 38, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (600, 1, 109, 38, 18, 120, 23.10, 0.407000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (601, 1, 108, 88, 19, 0, 27.10, 0.400000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (602, 6, 96, 0, 0, 0, 23.70, 0.190000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (603, 1, 124, 74, 36, 0, 27.80, 0.100000, 30, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (604, 7, 150, 78, 29, 126, 35.20, 0.692000, 54, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (605, 4, 183, 0, 0, 0, 28.40, 0.212000, 36, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (606, 1, 124, 60, 32, 0, 35.80, 0.514000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (607, 1, 181, 78, 42, 293, 40.00, 1.258000, 22, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (608, 1, 92, 62, 25, 41, 19.50, 0.482000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (609, 0, 152, 82, 39, 272, 41.50, 0.270000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (610, 1, 111, 62, 13, 182, 24.00, 0.138000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (611, 3, 106, 54, 21, 158, 30.90, 0.292000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (612, 3, 174, 58, 22, 194, 32.90, 0.593000, 36, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (613, 7, 168, 88, 42, 321, 38.20, 0.787000, 40, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (614, 6, 105, 80, 28, 0, 32.50, 0.878000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (615, 11, 138, 74, 26, 144, 36.10, 0.557000, 50, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (616, 3, 106, 72, 0, 0, 25.80, 0.207000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (617, 6, 117, 96, 0, 0, 28.70, 0.157000, 30, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (618, 2, 68, 62, 13, 15, 20.10, 0.257000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (619, 9, 112, 82, 24, 0, 28.20, 1.282000, 50, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (620, 0, 119, 0, 0, 0, 32.40, 0.141000, 24, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (621, 2, 112, 86, 42, 160, 38.40, 0.246000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (622, 2, 92, 76, 20, 0, 24.20, 1.698000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (623, 6, 183, 94, 0, 0, 40.80, 1.461000, 45, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (624, 0, 94, 70, 27, 115, 43.50, 0.347000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (625, 2, 108, 64, 0, 0, 30.80, 0.158000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (626, 4, 90, 88, 47, 54, 37.70, 0.362000, 29, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (627, 0, 125, 68, 0, 0, 24.70, 0.206000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (628, 0, 132, 78, 0, 0, 32.40, 0.393000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (629, 5, 128, 80, 0, 0, 34.60, 0.144000, 45, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (630, 4, 94, 65, 22, 0, 24.70, 0.148000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (631, 7, 114, 64, 0, 0, 27.40, 0.732000, 34, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (632, 0, 102, 78, 40, 90, 34.50, 0.238000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (633, 2, 111, 60, 0, 0, 26.20, 0.343000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (634, 1, 128, 82, 17, 183, 27.50, 0.115000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (635, 10, 92, 62, 0, 0, 25.90, 0.167000, 31, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (636, 13, 104, 72, 0, 0, 31.20, 0.465000, 38, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (637, 5, 104, 74, 0, 0, 28.80, 0.153000, 48, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (638, 2, 94, 76, 18, 66, 31.60, 0.649000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (639, 7, 97, 76, 32, 91, 40.90, 0.871000, 32, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (640, 1, 100, 74, 12, 46, 19.50, 0.149000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (641, 0, 102, 86, 17, 105, 29.30, 0.695000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (642, 4, 128, 70, 0, 0, 34.30, 0.303000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (643, 6, 147, 80, 0, 0, 29.50, 0.178000, 50, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (644, 4, 90, 0, 0, 0, 28.00, 0.610000, 31, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (645, 3, 103, 72, 30, 152, 27.60, 0.730000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (646, 2, 157, 74, 35, 440, 39.40, 0.134000, 30, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (647, 1, 167, 74, 17, 144, 23.40, 0.447000, 33, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (648, 0, 179, 50, 36, 159, 37.80, 0.455000, 22, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (649, 11, 136, 84, 35, 130, 28.30, 0.260000, 42, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (650, 0, 107, 60, 25, 0, 26.40, 0.133000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (651, 1, 91, 54, 25, 100, 25.20, 0.234000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (652, 1, 117, 60, 23, 106, 33.80, 0.466000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (653, 5, 123, 74, 40, 77, 34.10, 0.269000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (654, 2, 120, 54, 0, 0, 26.80, 0.455000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (655, 1, 106, 70, 28, 135, 34.20, 0.142000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (656, 2, 155, 52, 27, 540, 38.70, 0.240000, 25, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (657, 2, 101, 58, 35, 90, 21.80, 0.155000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (658, 1, 120, 80, 48, 200, 38.90, 1.162000, 41, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (659, 11, 127, 106, 0, 0, 39.00, 0.190000, 51, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (660, 3, 80, 82, 31, 70, 34.20, 1.292000, 27, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (661, 10, 162, 84, 0, 0, 27.70, 0.182000, 54, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (662, 1, 199, 76, 43, 0, 42.90, 1.394000, 22, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (663, 8, 167, 106, 46, 231, 37.60, 0.165000, 43, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (664, 9, 145, 80, 46, 130, 37.90, 0.637000, 40, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (665, 6, 115, 60, 39, 0, 33.70, 0.245000, 40, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (666, 1, 112, 80, 45, 132, 34.80, 0.217000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (667, 4, 145, 82, 18, 0, 32.50, 0.235000, 70, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (668, 10, 111, 70, 27, 0, 27.50, 0.141000, 40, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (669, 6, 98, 58, 33, 190, 34.00, 0.430000, 43, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (670, 9, 154, 78, 30, 100, 30.90, 0.164000, 45, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (671, 6, 165, 68, 26, 168, 33.60, 0.631000, 49, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (672, 1, 99, 58, 10, 0, 25.40, 0.551000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (673, 10, 68, 106, 23, 49, 35.50, 0.285000, 47, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (674, 3, 123, 100, 35, 240, 57.30, 0.880000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (675, 8, 91, 82, 0, 0, 35.60, 0.587000, 68, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (676, 6, 195, 70, 0, 0, 30.90, 0.328000, 31, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (677, 9, 156, 86, 0, 0, 24.80, 0.230000, 53, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (678, 0, 93, 60, 0, 0, 35.30, 0.263000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (679, 3, 121, 52, 0, 0, 36.00, 0.127000, 25, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (680, 2, 101, 58, 17, 265, 24.20, 0.614000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (681, 2, 56, 56, 28, 45, 24.20, 0.332000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (682, 0, 162, 76, 36, 0, 49.60, 0.364000, 26, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (683, 0, 95, 64, 39, 105, 44.60, 0.366000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (684, 4, 125, 80, 0, 0, 32.30, 0.536000, 27, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (685, 5, 136, 82, 0, 0, 0.00, 0.640000, 69, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (686, 2, 129, 74, 26, 205, 33.20, 0.591000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (687, 3, 130, 64, 0, 0, 23.10, 0.314000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (688, 1, 107, 50, 19, 0, 28.30, 0.181000, 29, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (689, 1, 140, 74, 26, 180, 24.10, 0.828000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (690, 1, 144, 82, 46, 180, 46.10, 0.335000, 46, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (691, 8, 107, 80, 0, 0, 24.60, 0.856000, 34, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (692, 13, 158, 114, 0, 0, 42.30, 0.257000, 44, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (693, 2, 121, 70, 32, 95, 39.10, 0.886000, 23, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (694, 7, 129, 68, 49, 125, 38.50, 0.439000, 43, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (695, 2, 90, 60, 0, 0, 23.50, 0.191000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (696, 7, 142, 90, 24, 480, 30.40, 0.128000, 43, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (697, 3, 169, 74, 19, 125, 29.90, 0.268000, 31, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (698, 0, 99, 0, 0, 0, 25.00, 0.253000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (699, 4, 127, 88, 11, 155, 34.50, 0.598000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (700, 4, 118, 70, 0, 0, 44.50, 0.904000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (701, 2, 122, 76, 27, 200, 35.90, 0.483000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (702, 6, 125, 78, 31, 0, 27.60, 0.565000, 49, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (703, 1, 168, 88, 29, 0, 35.00, 0.905000, 52, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (704, 2, 129, 0, 0, 0, 38.50, 0.304000, 41, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (705, 4, 110, 76, 20, 100, 28.40, 0.118000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (706, 6, 80, 80, 36, 0, 39.80, 0.177000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (707, 10, 115, 0, 0, 0, 0.00, 0.261000, 30, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (708, 2, 127, 46, 21, 335, 34.40, 0.176000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (709, 9, 164, 78, 0, 0, 32.80, 0.148000, 45, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (710, 2, 93, 64, 32, 160, 38.00, 0.674000, 23, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (711, 3, 158, 64, 13, 387, 31.20, 0.295000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (712, 5, 126, 78, 27, 22, 29.60, 0.439000, 40, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (713, 10, 129, 62, 36, 0, 41.20, 0.441000, 38, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (714, 0, 134, 58, 20, 291, 26.40, 0.352000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (715, 3, 102, 74, 0, 0, 29.50, 0.121000, 32, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (716, 7, 187, 50, 33, 392, 33.90, 0.826000, 34, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (717, 3, 173, 78, 39, 185, 33.80, 0.970000, 31, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (718, 10, 94, 72, 18, 0, 23.10, 0.595000, 56, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (719, 1, 108, 60, 46, 178, 35.50, 0.415000, 24, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (720, 5, 97, 76, 27, 0, 35.60, 0.378000, 52, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (721, 4, 83, 86, 19, 0, 29.30, 0.317000, 34, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (722, 1, 114, 66, 36, 200, 38.10, 0.289000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (723, 1, 149, 68, 29, 127, 29.30, 0.349000, 42, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (724, 5, 117, 86, 30, 105, 39.10, 0.251000, 42, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (725, 1, 111, 94, 0, 0, 32.80, 0.265000, 45, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (726, 4, 112, 78, 40, 0, 39.40, 0.236000, 38, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (727, 1, 116, 78, 29, 180, 36.10, 0.496000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (728, 0, 141, 84, 26, 0, 32.40, 0.433000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (729, 2, 175, 88, 0, 0, 22.90, 0.326000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (730, 2, 92, 52, 0, 0, 30.10, 0.141000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (731, 3, 130, 78, 23, 79, 28.40, 0.323000, 34, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (732, 8, 120, 86, 0, 0, 28.40, 0.259000, 22, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (733, 2, 174, 88, 37, 120, 44.50, 0.646000, 24, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (734, 2, 106, 56, 27, 165, 29.00, 0.426000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (735, 2, 105, 75, 0, 0, 23.30, 0.560000, 53, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (736, 4, 95, 60, 32, 0, 35.40, 0.284000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (737, 0, 126, 86, 27, 120, 27.40, 0.515000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (738, 8, 65, 72, 23, 0, 32.00, 0.600000, 42, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (739, 2, 99, 60, 17, 160, 36.60, 0.453000, 21, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (740, 1, 102, 74, 0, 0, 39.50, 0.293000, 42, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (741, 11, 120, 80, 37, 150, 42.30, 0.785000, 48, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (742, 3, 102, 44, 20, 94, 30.80, 0.400000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (743, 1, 109, 58, 18, 116, 28.50, 0.219000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (744, 9, 140, 94, 0, 0, 32.70, 0.734000, 45, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (745, 13, 153, 88, 37, 140, 40.60, 1.174000, 39, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (746, 12, 100, 84, 33, 105, 30.00, 0.488000, 46, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (747, 1, 147, 94, 41, 0, 49.30, 0.358000, 27, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (748, 1, 81, 74, 41, 57, 46.30, 1.096000, 32, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (749, 3, 187, 70, 22, 200, 36.40, 0.408000, 36, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (750, 6, 162, 62, 0, 0, 24.30, 0.178000, 50, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (751, 4, 136, 70, 0, 0, 31.20, 1.182000, 22, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (752, 1, 121, 78, 39, 74, 39.00, 0.261000, 28, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (753, 3, 108, 62, 24, 0, 26.00, 0.223000, 25, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (754, 0, 181, 88, 44, 510, 43.30, 0.222000, 26, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (755, 8, 154, 78, 32, 0, 32.40, 0.443000, 45, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (756, 1, 128, 88, 39, 110, 36.50, 1.057000, 37, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (757, 7, 137, 90, 41, 0, 32.00, 0.391000, 39, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (758, 0, 123, 72, 0, 0, 36.30, 0.258000, 52, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (759, 1, 106, 76, 0, 0, 37.50, 0.197000, 26, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (760, 6, 190, 92, 0, 0, 35.50, 0.278000, 66, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (761, 2, 88, 58, 26, 16, 28.40, 0.766000, 22, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (762, 9, 170, 74, 31, 0, 44.00, 0.403000, 43, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (763, 9, 89, 62, 0, 0, 22.50, 0.142000, 33, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (764, 10, 101, 76, 48, 180, 32.90, 0.171000, 63, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (765, 2, 122, 70, 27, 0, 36.80, 0.340000, 27, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (766, 5, 121, 72, 23, 112, 26.20, 0.245000, 30, 0, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (767, 1, 126, 60, 0, 0, 30.10, 0.349000, 47, 1, NULL);
INSERT INTO `diabetes_record` (`id`, `pregnancies`, `glucose`, `blood_pressure`, `skin_thickness`, `insulin`, `bmi`, `diabetes_pedigree_function`, `age`, `outcome`, `testfile_id`) VALUES (768, 1, 93, 70, 31, 0, 30.40, 0.315000, 23, 0, NULL);

-- ----------------------------
-- Table structure for sys_result
-- ----------------------------
DROP TABLE IF EXISTS `sys_result`;
CREATE TABLE `sys_result` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `testid` int(11) NULL DEFAULT NULL,
  `result` int(11) NULL DEFAULT NULL COMMENT '预测结果',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `testfile_id` int(11) NULL DEFAULT NULL COMMENT '关联的测试文件ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '在线数据统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_result (预置数据)
-- ----------------------------
INSERT INTO `sys_result` (`id`, `testid`, `result`, `testfile_id`) VALUES 
(1, 1, 1, 1),
(2, 2, 0, 1),
(3, 3, 1, 1),
(4, 4, 0, 2),
(5, 5, 1, 2);

-- ----------------------------
-- Table structure for user_health_profiles
-- ----------------------------
DROP TABLE IF EXISTS `user_health_profiles`;
CREATE TABLE `user_health_profiles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `Pregnancies` int(11) NULL DEFAULT NULL COMMENT '怀孕次数',
  `Glucose` double NULL DEFAULT NULL COMMENT '血糖',
  `BloodPressure` int(11) NULL DEFAULT NULL COMMENT '血压',
  `SkinThickness` int(11) NULL DEFAULT NULL COMMENT '皮褶厚度',
  `Insulin` double NULL DEFAULT NULL COMMENT '胰岛素',
  `BMI` double NULL DEFAULT NULL COMMENT 'BMI',
  `DiabetesPedigreeFunction` double NULL DEFAULT NULL COMMENT '糖尿病谱系功能',
  `Age` int(11) NULL DEFAULT NULL COMMENT '年龄',
  `symptoms` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '症状描述',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '原始上传的文件名',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `csv_file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生成的CSV文件在服务器的绝对路径',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'PENDING' COMMENT '状态:PENDING待诊断, DONE已完成',
  `diagnosis_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '诊断员填写的最终结果',
  `diagnose_time` timestamp NULL DEFAULT NULL COMMENT '诊断完成的时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '健康档案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_health_profiles (预置数据)
-- ----------------------------

-- 修复 user_health_profiles 表字段长度
ALTER TABLE user_health_profiles
  MODIFY COLUMN symptoms VARCHAR(255) COMMENT '症状描述',
  MODIFY COLUMN file_url VARCHAR(255) COMMENT '文件URL',
  MODIFY COLUMN csv_file_path VARCHAR(255) COMMENT 'CSV文件路径',
  MODIFY COLUMN diagnosis_result TEXT COMMENT '诊断结果（长文本）';
	
INSERT INTO `user_health_profiles` (`id`, `user_id`, `Pregnancies`, `Glucose`, `BloodPressure`, `SkinThickness`, `Insulin`, `BMI`, `DiabetesPedigreeFunction`, `Age`, `symptoms`, `file_url`, `csv_file_path`, `status`, `diagnosis_result`, `diagnose_time`) VALUES 
(1, 3, 2, 120.5, 75, 30, 85.5, 28.5, 0.456, 35, '偶尔口渴、疲劳', 'uploads/health/profile_1.csv', 'data/health/profile_1.csv', 'DONE', '经诊断，目前血糖控制在正常范围，建议继续保持健康的生活方式，定期监测血糖。', '2026-03-15 11:30:00'),
(2, 3, 0, 145.2, 80, 35, 0, 32.1, 0.623, 40, '多饮、多尿、体重下降', 'uploads/health/profile_2.csv', 'data/health/profile_2.csv', 'PENDING', NULL, NULL),
(3, 2, 1, 95.8, 68, 25, 72.3, 24.8, 0.289, 28, '无明显症状，常规体检', 'uploads/health/profile_3.csv', 'data/health/profile_3.csv', 'DONE', '血糖正常，身体状况良好，建议每年体检一次。', '2026-03-10 16:00:00');

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
    `config_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
    `config_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置值',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`config_key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` (`config_key`, `config_value`) VALUES ('default_ai_model', 'zhipu') ON DUPLICATE KEY UPDATE config_value = VALUES(config_value);

SET FOREIGN_KEY_CHECKS = 1;
