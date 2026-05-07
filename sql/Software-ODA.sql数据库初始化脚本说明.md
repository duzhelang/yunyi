# Software-ODA.sql 数据库初始化脚本说明

## 一、文件概述

`Software-ODA.sql` 是**糖尿病健康管理系统 (Software-ODA125)** 的完整数据库初始化脚本，包含所有表结构定义和预置数据，用于快速搭建系统运行所需的 MySQL 数据库环境。

**技术参数：**
| 参数 | 值 |
|------|-----|
| 数据库类型 | MySQL 8.0+ |
| 目标数据库名 | `dongfang` |
| 字符集 | UTF-8 Unicode (`utf8mb4`) |
| 排序规则 | `utf8mb4_unicode_ci` |
| 总行数 | 1450+ 行 |
| 表数量 | 22 张 |

## 二、表结构总览

| 序号 | 表名 | 说明 | 预置数据 |
|:---:|------|------|:-------:|
| **系统基础表** | | | |
| 1 | `sys_dict` | 系统字典表（图标配置） | ✅ 16条 |
| 2 | `sys_menu` | 菜单表（树形结构） | ✅ 32个 |
| 3 | `sys_user` | 用户表 | ✅ 15个 |
| 4 | `sys_role` | 角色表 | ✅ 6个 |
| 5 | `sys_role_menu` | 角色菜单关联表 | ✅ 权限配置 |
| 6 | `sys_message` | 消息/故障报修表 | ✅ 15条 |
| 7 | `sys_config` | 系统配置表 | ✅ 4条 |
| **AI模型相关表** | | | |
| 8 | `sys_trainfile` | 训练文件表 | ✅ 3条 |
| 9 | `sys_train_task` | 训练任务表 | ✅ 3条 |
| 10 | `sys_model_version` | 模型版本表 | ✅ 3条 |
| 11 | `sys_testfile` | 测试文件表 | ✅ 3条 |
| **业务数据表** | | | |
| 12 | `diabetes_education` | 糖尿病科普内容表 | ✅ 12篇 |
| 13 | `education_comment` | 科普留言表 | ✅ 2条 |
| 14 | `diabetes_video` | 糖尿病视频表 | ❌ |
| 15 | `diabetes_record` | 糖尿病记录表（Pima数据集） | ✅ 768条 |
| 16 | `sys_result` | 在线数据统计表 | ✅ 5条 |
| **健康档案与诊疗表** | | | |
| 17 | `user_health_profiles` | 健康档案表 | ✅ 3条 |
| 18 | `patient_visit_record` | 患者就诊记录表 | ✅ 5条 |
| 19 | `sys_treatment_record` | 诊疗档案表 | ❌ |
| **报修工单表** | | | |
| 20 | `sys_repair_order` | 报修工单表 | ✅ 3条 |
| 21 | `sys_repair_log` | 工单处理日志表 | ✅ 3条 |
| 22 | `sys_repair_evaluation` | 工单满意度评价表 | ❌ |

## 三、核心表结构详解

### 1. 用户与权限管理

**sys_user（用户表）**
| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | int | 主键，自增 |
| `username` | varchar(255) | 用户名（唯一） |
| `password` | varchar(255) | 密码 |
| `nickname` | varchar(255) | 昵称 |
| `real_name` | varchar(50) | 真实姓名 |
| `roleid` | int | 角色ID（关联sys_role） |
| `avatar` | varchar(255) | 头像路径 |
| `email` | varchar(100) | 邮箱 |

**预置用户：**
| 用户名 | 密码 | 角色标识 | 角色名 |
|--------|------|----------|--------|
| admin | admin | ROLE_ADMIN | 管理员 |
| test | 123 | ROLE_WORKER | 普通用户 |
| upload | (MD5加密) | ROLE_MAINTENANCE | 运维 |
| doctor1 | 123456 | ROLE_DOCTOR | 医生 |
| doctor2 | 123456 | ROLE_DOCTOR | 医生 |
| user | 123456 | ROLE_WORKER | 普通用户 |

**sys_role（角色表）**
| ID | 角色名 | 标识 | 说明 |
|----|--------|------|------|
| 1 | 管理员 | ROLE_ADMIN | 最高权限 |
| 2 | 用户 | ROLE_WORKER | 普通用户权限 |
| 3 | 运维 | ROLE_MAINTENANCE | 运维权限 |
| 16 | 测试1 | 0001 | 系统增删测试项 |
| 17 | 测试2 | 0002 | 系统增删测试项 |
| 18 | 医生 | ROLE_DOCTOR | 医生角色 |

### 2. 菜单系统

**sys_menu（菜单表）** - 共 32 个菜单，8 个模块

```
主页
└── 首页

系统管理(4)
├── 用户管理
├── 角色管理
└── 菜单管理

AI模型中心(46)
├── 训练集管理
├── 模型管理
└── 在线模型训练

用户服务(85)
├── 风险快检
├── 健康管理中心
├── 智能问答
├── 诊疗档案
└── 糖尿病科普

诊断员服务(90)
├── 组合分析
├── 个体洞察
├── 数据采集
├── 预测工作台
└── 诊断工作台

糖尿病预测中心(77)
├── 采集日志
├── 在线预测
├── 数据报表
└── 详细报表

运维中心(54)
├── 故障报修
├── 报修详情
├── 运维详情
└── 信息回执

关于
└── 关于
```

### 3. AI模型训练管理

**sys_train_task（训练任务表）**
| 字段 | 说明 |
|------|------|
| `task_name` | 任务名称 |
| `train_file_id` | 关联训练文件 |
| `model_name` | 模型名称 |
| `hyper_params` | 训练超参数(JSON) |
| `status` | 状态(pending/running/completed/failed) |
| `accuracy/loss/recall_rate/precision_rate/f1_score` | 评估指标 |
| `python_script` | 训练脚本路径 |

**预置训练任务：**
| 任务名 | 状态 | 准确率 |
|--------|------|--------|
| 糖尿病预测模型V1训练 | completed | 87.5% |
| 扩展特征模型训练 | completed | 89.2% |
| 历史数据分析模型 | running | - |

**sys_model_version（模型版本表）**
| 版本 | 来源 | 状态 | 准确率 |
|------|------|------|--------|
| v1.0.0 | manual | inactive | 85% |
| v1.1.0 | online_train | **active** | 87.5% |
| v2.0.0 | online_train | inactive | 89.2% |

### 4. 训练文件表

**sys_trainfile（训练文件表）** - 支持按 `category` 分类（train/test）

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | int | 主键，自增 |
| `name` | varchar(255) | 文件名 |
| `type` | varchar(20) | 文件扩展名 |
| `size` | bigint | 文件大小（字节） |
| `url` | varchar(255) | 存储路径（相对路径，正斜杠） |
| `md5` | varchar(32) | MD5哈希值 |
| `is_delete` | tinyint(1) | 逻辑删除标记 |
| `enable` | tinyint(1) | 启用状态 |
| `category` | varchar(50) | 分类标识（train/test） |
| `file_size` | varchar(50) | 格式化文件大小（如 "1.50 MB"） |
| `row_count` | int | 数据行数 |
| `col_count` | int | 数据列数 |
| `status` | varchar(20) | 处理状态 |

### 5. 糖尿病科普内容

**diabetes_education（糖尿病科普内容表）** - 12篇完整科普文章：
1. 基础认知篇 - 糖尿病定义、分型、诊断标准
2. 症状识别篇 - 三多一少症状、高危人群
3. 饮食管理篇 - 碳水化合物、蛋白质、脂肪摄入建议
4. 运动治疗篇 - 运动益处、推荐项目、注意事项
5. 药物治疗篇 - 口服药类别、胰岛素治疗
6. 血糖监测篇 - 七点法、控制目标
7. 并发症防治篇 - 急性/慢性并发症
8. 特殊人群篇 - 儿童、老年人、妊娠期
9. 生活方式干预篇 - 体重、睡眠、戒烟限酒
10. 前沿进展与误区澄清 - 新技术、常见误区
11. 视频科普篇
12. 核心建议总结

### 6. 健康档案与诊疗

**user_health_profiles（健康档案表）** - 包含用户健康数据、症状描述、诊断状态

**patient_visit_record（患者就诊记录表）** - 包含患者就诊记录、诊断结果、治疗方案

**sys_treatment_record（诊疗档案表）**
| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | bigint | 主键，自增 |
| `patient_name` | varchar(50) | 患者姓名 |
| `id_card` | varchar(18) | 身份证号 |
| `phone` | varchar(20) | 联系电话 |
| `gender` | varchar(10) | 性别 |
| `age` | int | 年龄 |
| `blood_sugar` | decimal(5,2) | 血糖值(mmol/L) |
| `diagnosis_result` | varchar(200) | 诊断结果 |
| `treatment_plan` | text | 治疗方案 |
| `doctor_name` | varchar(50) | 负责医生 |
| `remark` | text | 备注 |
| `create_time` | datetime | 创建时间 |
| `update_time` | datetime | 更新时间 |

### 7. 报修工单系统

**sys_repair_order（报修工单表）**
| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | int | 主键，自增 |
| `order_no` | varchar(20) | 工单编号（唯一） |
| `title` | varchar(100) | 工单标题 |
| `fault_type` | varchar(50) | 故障类型 |
| `fault_sub_type` | varchar(50) | 故障子类型 |
| `urgency` | varchar(20) | 紧急程度 |
| `description` | text | 故障描述 |
| `repro_steps` | text | 重现步骤 |
| `page_path` | varchar(200) | 发生页面/模块 |
| `attachments` | json | 附件路径数组 |
| `contact_phone` | varchar(20) | 联系手机 |
| `contact_email` | varchar(100) | 联系邮箱 |
| `accept_remote` | tinyint(1) | 是否接受远程协助 |
| `status` | varchar(20) | 工单状态 |
| `submit_user_id` | int | 提交人ID |
| `assign_user_id` | int | 指派运维人员ID |
| `create_time` | datetime | 创建时间 |
| `update_time` | datetime | 最后更新时间 |

**sys_repair_log（工单处理日志表）**
| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | int | 主键，自增 |
| `order_id` | int | 工单ID |
| `operator_id` | int | 操作人ID |
| `action` | varchar(50) | 操作类型 |
| `content` | text | 操作内容 |
| `attachments` | json | 附件路径数组 |
| `is_visible_to_user` | tinyint(1) | 是否对用户可见 |
| `create_time` | datetime | 操作时间 |

**sys_repair_evaluation（工单满意度评价表）**
| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | int | 主键，自增 |
| `order_id` | int | 工单ID（唯一） |
| `user_id` | int | 评价人ID |
| `rating` | int | 星级评分(1-5) |
| `comment` | text | 评价内容 |
| `create_time` | datetime | 评价时间 |

### 8. 糖尿病记录数据

**diabetes_record（糖尿病记录表）** - 包含 Pima Indians 糖尿病数据集（768条）：
- Pregnancies（怀孕次数）
- Glucose（血糖浓度）
- BloodPressure（血压）
- SkinThickness（皮褶厚度）
- Insulin（胰岛素）
- BMI（身体质量指数）
- DiabetesPedigreeFunction（糖尿病谱系功能）
- Age（年龄）
- Outcome（结果：0=无糖尿病，1=有糖尿病）

## 四、使用方法

### 方式一：命令行执行
```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS dongfang DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;"

# 导入数据
mysql -u root -p dongfang < Software-ODA.sql
```

### 方式二：Navicat导入
1. 打开 Navicat，连接到 MySQL 服务器
2. 创建数据库 `dongfang`（字符集 utf8mb4）
3. 右键数据库 → 运行 SQL 文件 → 选择 Software-ODA.sql

### 方式三：SQL客户端执行
```sql
-- 先创建数据库
CREATE DATABASE IF NOT EXISTS dongfang 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

USE dongfang;

-- 然后执行 Software-ODA.sql 内容
```

## 五、注意事项

⚠️ **重要提醒：**
1. 脚本开头包含 `SET FOREIGN_KEY_CHECKS = 0`，会暂时禁用外键约束
2. 脚本会**删除并重建所有表**，执行前请备份现有数据
3. 建议在空数据库上执行，避免数据丢失
4. 执行完成后会自动恢复外键约束（`SET FOREIGN_KEY_CHECKS = 1`）
5. `sys_trainfile.file_size` 字段为 `varchar(50)` 类型，存储格式化文件大小（如 "1.50 MB"）
6. 路径字段统一使用正斜杠 `/` 格式（如 `data/train/xxx.csv`）

## 六、数据库关系图

```
用户管理          权限管理          菜单管理
┌─────────┐      ┌─────────┐      ┌─────────┐
│sys_user │◄────►│sys_role │◄────►│sys_menu │
└────┬────┘      └────┬────┘      └────┬────┘
    │                │                │
    │                ▼                │
    │        ┌─────────────┐         │
    │        │sys_role_menu│◄────────┘
    │        └─────────────┘
    ▼
训练管理          模型管理          业务数据
┌──────────┐     ┌─────────────┐    ┌──────────────────┐
│sys_train │     │sys_model_   │    │diabetes_education│
│_file     │────►│version      │    └────────┬─────────┘
└────┬─────┘     └─────────────┘             │
    │                       │               ▼
    ▼                       │       ┌──────────────┐
┌──────────┐                 │       │education_    │
│sys_train │                 │       │comment       │
│_task     │─────────────────┘       └──────────────┘
└──────────┘

诊疗数据          健康档案          预测记录
┌─────────────────┐ ┌──────────────┐ ┌──────────┐
│sys_treatment_   │ │user_health_ │ │sys_result│
│record           │ │profiles     │ └────┬─────┘
└────────┬────────┘ └──────┬───────┘      │
         │                │              │
         ▼                ▼              ▼
┌─────────────────┐ ┌──────────┐ ┌─────────────┐
│patient_visit_   │ │diabetes_ │ │sys_testfile │
│record           │ │record    │ └─────────────┘
└─────────────────┘ └──────────┘

报修工单系统
┌─────────────────┐
│sys_repair_order │
└────┬────────────┘
     │
     ├──►┌─────────────────┐
     │   │sys_repair_log   │
     │   └─────────────────┘
     │
     └──►┌─────────────────────┐
         │sys_repair_evaluation│
         └─────────────────────┘
```

## 七、预置数据统计

| 分类 | 表名 | 记录数 |
|------|------|--------|
| 系统配置 | sys_dict | 16 |
| 菜单 | sys_menu | 32 |
| 用户 | sys_user | 15 |
| 角色 | sys_role | 6 |
| 权限 | sys_role_menu | 69 |
| 消息 | sys_message | 15 |
| 系统配置 | sys_config | 4 |
| 训练文件 | sys_trainfile | 3 |
| 训练任务 | sys_train_task | 3 |
| 模型版本 | sys_model_version | 3 |
| 测试文件 | sys_testfile | 3 |
| 科普文章 | diabetes_education | 12 |
| 留言 | education_comment | 2 |
| 糖尿病记录 | diabetes_record | 768 |
| 预测结果 | sys_result | 5 |
| 健康档案 | user_health_profiles | 3 |
| 就诊记录 | patient_visit_record | 5 |
| 报修工单 | sys_repair_order | 3 |
| 工单日志 | sys_repair_log | 3 |
| **总计** | **22个表** | **970条** |

## 八、系统默认账号

| 用户名 | 密码 | 角色 | 权限范围 |
|--------|------|------|----------|
| **admin** | admin | 管理员 | 所有功能 |
| **test** | 123 | 普通用户 | 基础功能 |
| **user** | 123456 | 普通用户 | 用户服务、糖尿病预测中心（部分） |
| **doctor1** | 123456 | 医生 | 用户服务、诊断员服务、糖尿病预测中心 |
| **doctor2** | 123456 | 医生 | 用户服务、诊断员服务、糖尿病预测中心 |
| **upload** | (MD5加密) | 运维 | 运维相关功能 |

---

**文件路径：** `sql/Software-ODA.sql`  
**最后更新：** 2026年5月7日  
**适用系统：** 糖尿病健康管理系统 (Software-ODA125) v0.0.1-YUNYI  
**数据来源：** 整合自 dongfang4.29.sql + 报修工单表 + 诊疗档案表  
**变更记录：**
- 2026-05-07: 合并 `sys_repair_order`、`sys_repair_log`、`sys_repair_evaluation` 三张报修工单表
- 2026-05-07: 合并 `sys_treatment_record` 诊疗档案表（原仅运行时存在）
- 2026-05-07: 修复 `sys_trainfile.file_size` 列类型（bigint → varchar(50)）
- 2026-05-07: 删除临时文件 `create_repair_tables.sql`，统一到主脚本
