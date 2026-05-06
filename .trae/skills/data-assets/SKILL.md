---
name: "data-assets"
description: "Software-ODA125 项目的数据资产知识库。包含所有数据文件、数据库表数据、文件目录结构、训练数据来源、模型文件位置等完整信息。当用户询问数据文件位置、训练集内容、模型存储、数据归档、目录结构时，应调用此技能。"
---

# Software-ODA125 数据资产知识库

## 1. 数据目录总览（项目实际状态）

```
Software-ODA125/
├── python/
│   ├── train.py               # 训练脚本
│   ├── predict.py             # 批量预测脚本
│   ├── predict_single.py      # 单条预测脚本
│   ├── diabetes_chat.py       # AI聊天脚本
│   ├── data_analysis.py       # 数据分析脚本
│   ├── read_data.py           # CSV读取工具
│   ├── A10.py                 # 历史分析脚本
│   ├── similar_cases.npy      # 相似病例历史数据
│   └── Untitled-1.js          # 临时文件
├── sql/
│   ├── Software-ODA.sql       # 主数据库初始化脚本（19张表 + 预置数据）
│   └── add_python_script_field.sql  # 迁移脚本（向sys_train_task添加python_script字段）
├── data/                        # 运行时数据目录（已存在于项目根目录）
│   └── models/                  # 模型文件目录
│       ├── npy_data/            # SHAP背景数据(.npy)
│       └── pth_models/          # PyTorch模型文件(.pth)
├── src/main/resources/
│   ├── application.yml          # 主配置文件
│   └── mapper/                  # MyBatis XML映射文件（6个）
├── vue/
│   └── public/
│       └── config.js            # 运行时公共配置（导出 serverIp）
├── logs/                        # 训练日志目录（.gitkeep占位）
├── pom.xml                      # Maven依赖
├── requirements.txt             # Python依赖
└── README.md
```

### 1.1 目录实际存在状态

| 目录 | 是否存在 | 内容说明 |
|------|---------|---------|
| `./data/` | **已存在** | 含 `models/` 子目录（npy_data/ + pth_models/）、`test/`（.gitkeep）、`train/`（.gitkeep） |
| `./data/models/` | **已存在** | 存储训练产出的模型文件 |
| `./logs/` | **已存在** | 仅包含 `.gitkeep` 占位文件 |
| `./OnlinePredict/` | 不存在 | 预测数据上传目录，application.yml 已配置，需要时自动创建 |
| `./json/` | 不存在 | JSON结果下载目录，application.yml 已配置，需要时自动创建 |
| `./files/` | 不存在 | 通用文件上传目录，application.yml 已配置，需要时自动创建 |

---

## 2. Python脚本与数据文件

### 2.1 Python脚本文件

| 文件 | 作用 | 调用方式 | 相关接口 |
|------|------|---------|---------|
| **train.py** | PyTorch神经网络模型训练 | `python train.py <csv_path> <model_output_path>` | `/api/train-task/start` |
| **predict.py** | 批量预测CSV/Excel文件，输出JSON并更新数据库 | `python predict.py <csv_path> <json_name> <title> <model_path>` | `/api/test-file` |
| **predict_single.py** | 单条数据预测（功能最丰富），含MC Dropout不确定性估计、SHAP特征重要性、可视化图表（特征重要性图、风险仪表盘、雷达图、瀑布图）、百分位计算、相似病例分析 | 通过后端参数传递 | `/api/singlePredict/run` |
| **diabetes_chat.py** | AI糖尿病专业问答，支持智谱/DeepSeek/Kimi/小米MiMo | `python diabetes_chat.py <provider> <api_key> <question>` | `/api/diabetes/chat` |
| **data_analysis.py** | 数据分析可视化（含旧版TensorFlow/Keras预测逻辑，已过时） | - | - |
| **read_data.py** | 数据读取和预处理工具函数 | - | - |
| **A10.py** | 辅助脚本 | - | - |

### 2.2 模型结构 (train.py)

```python
DiabetesModel(nn.Module):
  fc1: Linear(8, 64) → ReLU → Dropout(0.2)
  fc2: Linear(64, 32) → ReLU → Dropout(0.2)
  fc3: Linear(32, 2)  # 二分类输出
```

**8个输入特征**:
1. Pregnancies (怀孕次数)
2. Glucose (血糖)
3. BloodPressure (血压)
4. SkinThickness (皮肤厚度)
5. Insulin (胰岛素)
6. BMI
7. DiabetesPedigreeFunction (遗传系数)
8. Age (年龄)

### 2.3 数据文件

| 文件 | 路径 | 格式 | 作用 |
|------|------|------|------|
| **similar_cases.npy** | `python/` | NumPy数组 | 相似病例历史预测数据，predict_single.py 用于余弦相似度匹配 |

### 2.4 训练产出文件（存储于 `data/models/`）

| 文件类型 | 格式 | 说明 |
|---------|------|------|
| 模型权重 | `.pth` | PyTorch模型文件（如 diabetes_model.pth） |
| 标准化器 | `.pkl` | StandardScaler 标准化参数 |
| 标签编码器 | `.pkl` | LabelEncoder 标签映射 |
| SHAP背景数据 | `.npy` | 背景数据集用于SHAP解释 |

---

## 3. 配置文件路径 (application.yml)

```yaml
server:
  port: 9090

spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/dongfang?serverTimezone=GMT%2b8&useSSL=false&allowPublicKeyRetrieval=true
  data.redis:
    host: 127.0.0.1
    port: 6379
    database: 0
  servlet.multipart:
    max-file-size: 1500MB
    max-request-size: 1500MB

base.path: ./

files:
  upload.path: ${base.path}/files/
  avatar.path: ${files.upload.path}avatar/
  common.path: ${files.upload.path}common/
  pythonUpload.path: ${base.path}/data/
  pythonDataTestUpload.path: ${base.path}/OnlinePredict/
  JsonDownload.path: ${base.path}/json/
  models.path: ${base.path}/data/models/
  trainLogs.path: ${base.path}/logs/
  pythonExe.path: python
  pythonTrainMain.path: ${base.path}/python/train.py
  pythonPredictMain.path: /python/predict.py
  pythonChatScript.path: ${base.path}/python/diabetes_chat.py
  pythonModelPath.path: ${base.path}/data/models/diabetes_model.pth

# 外部配置引入
spring.config.import: optional:classpath:secrets/application-secrets.yml
```

### 3.1 AI API密钥配置

通过环境变量注入（application.yml 中使用 `${KEY:}` 语法）:
- `ZHIPU_API_KEY` — 智谱 GLM
- `DEEPSEEK_API_KEY` — DeepSeek
- `KIMI_API_KEY` — Kimi (Moonshot)
- `MIMO_API_KEY` — 小米 MiMo
- `MIMO_OMNI_API_KEY` — 小米 MiMo Omni

---

## 4. 数据库数据资产

### 4.1 数据库配置
- 数据库名: `dongfang`
- 字符集: `utf8mb4`
- 初始化脚本: `sql/Software-ODA.sql`
- 迁移脚本: `sql/add_python_script_field.sql`

### 4.2 核心数据表（共19张）

**SQL初始化脚本定义的表（18张）：**

| # | 表名 | 说明 | 数据用途 |
|---|------|------|---------|
| 1 | **sys_user** | 用户表 | 系统用户信息 |
| 2 | **sys_role** | 角色表 | RBAC角色定义 |
| 3 | **sys_menu** | 菜单表 | 菜单配置（支持树形结构，通过parent_id关联） |
| 4 | **sys_role_menu** | 角色-菜单关联表 | RBAC权限控制 |
| 5 | **sys_dict** | 数据字典表 | 图标配置 |
| 6 | **sys_message** | 消息/故障报修表 | 运维消息 |
| 7 | **sys_trainfile** | 训练文件表 | 训练数据集元数据 |
| 8 | **sys_train_task** | 训练任务表 | 训练任务记录（含python_script字段，由迁移脚本添加） |
| 9 | **sys_model_version** | 模型版本表 | 模型版本管理，包含激活模型标记 |
| 10 | **sys_testfile** | 测试文件表 | 测试数据文件 |
| 11 | **sys_config** | 系统配置表 | 默认模型配置等 |
| 12 | **sys_result** | 在线数据统计表 | 在线预测结果记录（实体类名：OnlineDate） |
| 13 | **diabetes_education** | 糖尿病科普表 | 科普文章内容 |
| 14 | **diabetes_video** | 糖尿病视频表 | 视频资源 |
| 15 | **diabetes_record** | 糖尿病记录表 | 训练数据（Pima Indians数据集） |
| 16 | **education_comment** | 科普留言表 | 用户评论 |
| 17 | **user_health_profiles** | 健康档案表 | 用户健康检查记录（含预测结果JSON、AI建议） |
| 18 | **patient_visit_record** | 患者就诊记录表 | 就诊历史 |

**数据库运行时存在的表（1张，未在初始化SQL中定义）：**

| # | 表名 | 说明 | 数据用途 |
|---|------|------|---------|
| 19 | **sys_treatment_record** | 诊疗档案表 | 患者诊疗记录（实体类名：TreatmentRecord，MyBatis XML中硬编码表名） |

> **注意**：`sys_treatment_record` 表未在 `sql/Software-ODA.sql` 中定义建表语句，需要在数据库中手动创建或通过其他方式初始化。实体类 `TreatmentRecord.java` 无 `@TableName` 注解，但 `TreatmentRecordMapper.xml` 中所有 SQL 均硬编码使用 `sys_treatment_record` 表名。

### 4.3 实体类-数据表映射关系

| 实体类 | 映射表 | 映射方式 |
|--------|-------|---------|
| User.java | sys_user | `@TableName("sys_user")` |
| Role.java | sys_role | `@TableName("sys_role")` |
| Menu.java | sys_menu | `@TableName("sys_menu")` |
| RoleMenu.java | sys_role_menu | `@TableName("sys_role_menu")` |
| Dict.java | sys_dict | `@TableName("sys_dict")` |
| Message.java | sys_message | `@TableName("sys_message")` |
| Files.java | sys_trainfile | `@TableName("sys_trainfile")` |
| TrainTask.java | sys_train_task | `@TableName("sys_train_task")` |
| ModelVersion.java | sys_model_version | `@TableName("sys_model_version")` |
| TestFiles.java | sys_testfile | `@TableName("sys_testfile")` |
| SysConfig.java | sys_config | `@TableName("sys_config")` |
| OnlineDate.java | sys_result | `@TableName("sys_result")` |
| DiabetesRecord.java | diabetes_record | `@TableName("diabetes_record")` |
| DiabetesVideo.java | diabetes_video | `@TableName("diabetes_video")` |
| HealthProfile.java | user_health_profiles | `@TableName(value = "user_health_profiles", autoResultMap = true)` |
| PatientVisitRecord.java | patient_visit_record | `@TableName("patient_visit_record")` |
| DiabetesEducation.java | diabetes_education | MyBatis-Plus 默认转换 |
| EducationComment.java | education_comment | MyBatis-Plus 默认转换 |
| TreatmentRecord.java | sys_treatment_record | MyBatis XML 硬编码 |

> **特别注意**：实体类 `OnlineDate.java` 映射到 `sys_result` 表，实体类名与表名语义不匹配（疑似拼写错误，本意可能是 "OnlineData"）。

### 4.4 菜单数据结构 (sys_menu)

共 **32** 条菜单记录，分为 **8** 大模块：

```
菜单模块(8个):
1. 主页 — Home
2. 系统管理 — 用户管理 / 角色管理 / 菜单管理
3. AI模型中心 — 训练集管理 / 模型管理 / 在线模型训练
4. 用户服务 — 风险快检 / 健康管理中心 / 智能问答 / 诊疗档案 / 糖尿病科普
5. 诊断员服务 — 组合分析 / 个体洞察 / 数据采集 / 预测工作台 / 诊断工作台
6. 糖尿病预测中心 — 采集日志 / 在线预测 / 数据报表 / 详细报表
7. 运维中心 — 故障报修 / 报修详情 / 运维详情 / 信息回执
8. 关于
```

### 4.5 角色数据 (sys_role)

共 **6** 条角色记录：

| ID | 名称 | 角色编码 | 说明 |
|----|------|---------|------|
| 1 | 管理员 | ROLE_ADMIN | 系统管理员 |
| 2 | 用户 | ROLE_WORKER | 普通用户 |
| 3 | 运维 | ROLE_MAINTENANCE | 运维人员 |
| 16 | 测试1 | 0001 | 系统增删测试项 |
| 17 | 测试2 | 0002 | 系统增删测试项 |
| 18 | 医生 | ROLE_DOCTOR | 医生角色 |

### 4.6 预置数据统计

| 数据项 | 数量 | 说明 |
|--------|------|------|
| sys_menu | 32条 | 8大模块菜单配置 |
| sys_role | 6条 | 含管理员、用户、运维、医生、2个测试角色 |
| sys_dict | 17条 | Element Plus 图标配置 |
| diabetes_record | 约768条 | Pima Indians 糖尿病数据集 |
| user_health_profiles | 1条 | 预置健康档案示例 |

---

## 5. 依赖与配置文件

### 5.1 Maven依赖 (pom.xml)

核心依赖:
- Spring Boot 3.3.5
- MyBatis-Plus 3.5.9
- MySQL Connector 8.0.33
- Spring Data Redis (Lettuce)
- JWT (Auth0 java-jwt 3.10.3)
- SpringDoc OpenAPI 2.2.0 (Swagger 3)
- EasyExcel 3.3.2
- Hutool 5.7.20
- FastJSON 1.2.47
- Jython 2.7.0
- Hibernate Validator 8.0.1.Final
- Apache POI 4.1.2
- Lombok
- 阿里云 Maven 镜像仓库

### 5.2 Python依赖 (requirements.txt)

```
joblib~=1.5.0
numpy~=2.3.0
pandas~=2.3.0
scikit-learn~=1.8.0
torch~=2.11.0
matplotlib~=3.10.0
scipy~=1.15.0
shap~=0.46.0
```

### 5.3 前端依赖 (package.json)

核心依赖:
- vue ^3.4.0
- vue-router ^4.2.0
- pinia ^2.1.7
- element-plus ^2.4.4
- @element-plus/icons-vue ^2.3.2
- echarts ^5.6.0
- axios ^1.6.0
- **mavon-editor ^2.10.4** — Markdown编辑器
- **video.js ^7.18.1** — 视频播放器
- **vue-video-player ^5.0.2** — Vue视频播放器组件
- **sockjs-client ^1.6.1** — WebSocket兼容

### 5.4 MyBatis XML映射文件（6个）

> 注意：项目规则禁止引入新的 MyBatis XML 映射文件，以下为历史遗留。

| 文件 | 路径 | 用途 |
|------|------|------|
| UserMapper.xml | `src/main/resources/mapper/` | 用户查询 |
| RoleMapper.xml | `src/main/resources/mapper/` | 角色查询 |
| MenuMapper.xml | `src/main/resources/mapper/` | 菜单树形查询 |
| DiabetesEducationMapper.xml | `src/main/resources/mapper/` | 科普文章查询 |
| EducationCommentMapper.xml | `src/main/resources/mapper/` | 科普评论查询 |
| TreatmentRecordMapper.xml | `src/main/resources/mapper/` | 诊疗档案CRUD（硬编码sys_treatment_record） |

---

## 6. 数据流转说明

```
【训练流程】
用户上传训练CSV → 保存到 ./data/ 目录
    → 记录到 sys_trainfile 表
    → 启动训练任务 (sys_train_task)
    → 调用 python/train.py
    → 输出: model.pth + scaler.pkl + encoder.pkl + background.npy
    → 保存到 ./data/models/ 目录（npy_data/ + pth_models/）
    → 记录到 sys_model_version 表

【批量预测流程】
用户上传预测CSV → 保存到 ./OnlinePredict/ 目录
    → 调用 python/predict.py
    → 输出预测结果 JSON → 保存到 ./json/ 目录
    → 结果记录到 sys_result 表

【健康档案预测流程】
用户填写健康档案 → 保存到 user_health_profiles 表
    → 调用 python/predict_single.py
    → 返回: 预测结果 + SHAP解释 + 可视化图表
    → 更新 user_health_profiles 表（risk_level, risk_probability, ai_advice, prediction_json）

【AI问答流程】
用户提问 → DiabetesController 接收
    → 调用 python/diabetes_chat.py
    → 支持多AI服务商（智谱/DeepSeek/Kimi/小米MiMo）
    → 返回AI回答
```

---

## 7. 数据同步与初始化

### 7.1 数据库初始化
1. 创建数据库 `dongfang`（字符集 utf8mb4）
2. 执行 `sql/Software-ODA.sql`（18张表 + 预置数据）
3. 手动创建 `sys_treatment_record` 表（该表未在初始化脚本中定义）
4. 如有需要，执行迁移脚本 `sql/add_python_script_field.sql`

### 7.2 迁移脚本说明

`add_python_script_field.sql`：
- 向 `sys_train_task` 表添加 `python_script` 字段（VARCHAR(255)）
- 添加索引 `idx_python_script` 提高查询性能
- 使用动态SQL判断字段/索引是否存在，避免重复执行

---

## 8. 注意事项

### 8.1 目录创建
项目运行时会根据需要自动创建以下目录：
- `./data/` — 已存在
- `./data/models/` — 已存在
- `./OnlinePredict/` — 需要时自动创建
- `./json/` — 需要时自动创建
- `./logs/` — 已存在（.gitkeep）
- `./files/` — 需要时自动创建
- `./files/avatar/` — 需要时自动创建
- `./files/common/` — 需要时自动创建

### 8.2 模型文件存储
- 训练输出: PyTorch `.pth` 模型文件
- 标准化器: `.pkl` 文件 (StandardScaler, LabelEncoder)
- SHAP背景数据: `.npy` 文件
- 模型信息存储在 `sys_model_version` 表
- 默认模型路径: `./data/models/diabetes_model.pth`

### 8.3 .gitignore 排除的数据文件
以下文件类型被排除在版本控制之外：
- 模型文件: `.pth`, `.pt`, `.pkl`, `.h5`, `.joblib`, `.onnx`, `.bin`, `.ckpt`
- 数据集: `.csv`, `.xlsx`, `.xls`
- JSON输出: 全部 `.json`
- 密钥文件: `secrets/`, `application-secrets.yml`
- 数据目录: `data/`, `datasets/`, `raw_data/`
- 文件上传: `files/`, `upload/`
- 数据库备份: `*-backup.sql`, `*.sql.bak`

### 8.4 前端运行时配置
`vue/public/config.js` 导出 `serverIp` 常量，默认值 `'localhost'`。

### 8.5 文件上传限制
- 单文件最大: 1500MB
- 单请求最大: 1500MB
