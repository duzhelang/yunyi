---
name: data-assets
description: Software-ODA125 项目的数据资产知识库。包含所有数据文件（CSV/NPY/PTH/PKL/H5）、数据库表数据、文件目录结构、训练数据来源、模型文件位置等完整信息。当用户询问数据文件位置、训练集内容、模型存储、数据归档、目录结构时，应调用此技能。
---

# Software-ODA125 数据资产知识库

## 1. 数据目录总览

```
Software-ODA125/
├── data/                              ← 数据根目录（application.yml: base.path=./）
│   ├── train/                         ← 训练集CSV存放目录（扫描自动入库）
│   │   └── .gitkeep                   ← 占位文件
│   ├── test/                          ← 测试集CSV存放目录（扫描自动入库）
│   │   └── .gitkeep                   ← 占位文件
│   ├── csv_for_doctor/                ← 健康档案转诊CSV（HealthProfileServiceImpl生成）
│   │   ├── patient_25_1773066956136.csv
│   │   ├── patient_26_1773067660786.csv
│   │   └── ... (共20个患者转诊CSV)
│   ├── datasets/                      ← 数据集存放目录
│   │   ├── diabetes_train_20260425_012649.csv (19KB)
│   │   ├── diabetes_train_20260425_012708.csv (10KB)
│   │   └── diabetes_train_20260425_012726.csv (16KB)
│   ├── models/                        ← 【旧目录】早期模型文件存放
│   │   ├── h5_models/                 ← Keras模型目录（16个H5模型）
│   │   │   ├── 1e53aa6902604656b03114fe9a3077ab.h5 (1.4MB)
│   │   │   ├── 2fdc1a15caad42539ee1a4968a6698a0.h5 (1.4MB)
│   │   │   ├── diabetes_1761580888744.h5 (191KB)
│   │   │   └── ... (共16个H5模型)
│   │   ├── npy_data/                  ← SHAP背景数据
│   │   │   ├── diabetes_model_20260425_background.npy (12.9KB)
│   │   │   └── similar_cases.npy (1.6KB)
│   │   ├── pkl_files/                 ← Scaler/Encoder目录（32个PKL文件）
│   │   │   ├── diabetes_1761580888744_encoder.pkl (343B)
│   │   │   ├── diabetes_1761580888744_scaler.pkl (1KB)
│   │   │   ├── diabetes_model_20260425_encoder.pkl (343B)
│   │   │   ├── diabetes_model_20260425_scaler.pkl (1.1KB)
│   │   │   ├── diabetes_model_encoder.pkl (343B)
│   │   │   ├── diabetes_model_scaler.pkl (1.2KB)
│   │   │   └── ... (共32个PKL文件)
│   │   └── pth_models/               ← PyTorch模型目录（42个文件）
│   │       ├── diabetes_model.pth (13.6KB)
│   │       ├── diabetes_model_20260425_fold1.pth (14.2KB)
│   │       ├── diabetes_model_20260425_fold2.pth (14.2KB)
│   │       ├── diabetes_model_20260425_fold3.pth (14.2KB)
│   │       ├── diabetes_model_20260425_fold4.pth (14.2KB)
│   │       ├── diabetes_model_20260425_fold5.pth (14.2KB)
│   │       ├── ceshi003.4.25_fold1.pth (13.6KB)
│   │       ├── ceshi003.4.25_fold2.pth (13.6KB)
│   │       ├── ceshi003.4.25_fold3.pth (13.6KB)
│   │       ├── ceshi003.4.25_fold4.pth (13.6KB)
│   │       ├── ceshi003.4.25_fold5.pth (13.6KB)
│   │       ├── ceshi003.4.25_encoder.pkl (343B)
│   │       ├── ceshi003.4.25_scaler.pkl (1.1KB)
│   │       ├── ceshi003.4.25_background.npy (12.9KB)
│   │       ├── ceshi003.4.25_metrics.json (1.1KB)
│   │       ├── test_model_fold1.pth (13.6KB)
│   │       ├── test_model_fold2.pth (13.6KB)
│   │       ├── test_model_fold3.pth (13.6KB)
│   │       ├── test_model_encoder.pkl (343B)
│   │       ├── test_model_scaler.pkl (1.1KB)
│   │       ├── test_model_background.npy (12.9KB)
│   │       ├── test_model_metrics.json (839B)
│   │       ├── 测试1.4.26_fold1.pth (13.6KB)
│   │       ├── 测试1.4.26_fold2.pth (13.6KB)
│   │       ├── 测试1.4.26_fold3.pth (13.6KB)
│   │       ├── 测试1.4.26_fold4.pth (13.6KB)
│   │       ├── 测试1.4.26_fold5.pth (13.6KB)
│   │       ├── 测试1.4.26_encoder.pkl (343B)
│   │       ├── 测试1.4.26_scaler.pkl (1.1KB)
│   │       ├── 测试1.4.26_background.npy (12.9KB)
│   │       └── 测试1.4.26_metrics.json (1.1KB)
│   ├── 1111.mp4 (463MB)              ← 视频文件
│   ├── 111_1761583729162.csv (486B)  ← 小数据集
│   ├── 111_1772111753123.csv (486B)
│   ├── 22222222_1773041239867.csv (118B)
│   └── diabetes_20000_samples_1772887415155.csv (584KB)  ← 20000样本大数据集
├── python/
│   ├── data/                          ← Python专用数据目录（共13个文件：4个CSV+1个标签JSON+8个预测结果JSON）
│   │   ├── diabetes.csv               ← Pima Indians原始数据集（768行×9列，8特征+Outcome二分类标签）
│   │   ├── train_10000.csv            ← 特征工程后训练集（10000行×108列，107特征+label六分类0-5）
│   │   ├── validate_1000.csv          ← 验证集（1000行×108列，同train结构，用于A10.py模型验证）
│   │   ├── validate_1000_y_true.json  ← 验证集真实标签JSON数组（1000个0-5整数）
│   │   ├── predict11.json             ← 批量预测结果（predict.py生成，title="2025年糖尿病筛查预测"）
│   │   ├── 2997371a-109a-4942-b431-e272b0d09659.json  ← UUID命名批量预测结果1
│   │   ├── 2c542afe-888b-4b1c-abcd-7ed70af05544.json  ← UUID命名批量预测结果2
│   │   ├── 40a9ce12-9251-4d51-ab1a-90512318970f.json  ← UUID命名批量预测结果3
│   │   ├── 9a6a6d4c-d32c-46e3-a114-eabe87447e23.json  ← UUID命名批量预测结果4
│   │   ├── ae6675dc-cb92-48e2-a23b-1e79c892d9eb.json  ← UUID命名批量预测结果5
│   │   ├── c202853c-2263-4876-8ba9-5fec2df18971.json  ← UUID命名批量预测结果6
│   │   ├── f35b1f7b-63cb-402f-8adb-bc2cb055b1fd.json  ← UUID命名批量预测结果7
│   │   └── f6d877fd-8462-4ccf-83e5-baf8bf79dbdd.json  ← UUID命名批量预测结果8
│   ├── similar_cases.npy              ← 相似病例历史数据（predict_single.py使用）
│   ├── train.py                       ← 训练脚本
│   ├── predict.py                     ← 批量预测脚本
│   ├── predict_single.py              ← 单条预测脚本
│   ├── diabetes_chat.py               ← AI问答脚本
│   ├── data_analysis.py               ← 数据分析脚本
│   ├── A10.py                         ← 数据处理流水线（读取train_10000.csv）
│   └── read_data.py                   ← CSV读取工具
├── logs/                              ← 训练日志目录
│   └── .gitkeep
└── sql/
    ├── Software-ODA.sql               ← 主数据库初始化脚本（含768条训练数据）
    ├── migration_001_add_health_fields.sql
    ├── migration_002_patient_visit_record.sql
    └── migration_003_model_center_upgrade.sql
```

## 2. 数据文件类型说明

### 2.1 CSV 文件

**`python/data/` 目录下的完整文件清单（共13个）：**

| 文件 | 大小 | 行×列 | 说明 | 使用者/用途 |
|------|------|-------|------|-------------|
| `diabetes.csv` | 23.8KB | 768×9 | Pima Indians原始数据集，字段：Pregnancies,Glucose,BloodPressure,SkinThickness,Insulin,BMI,DiabetesPedigreeFunction,Age,Outcome（二分类0/1） | `A10.py` 特征工程的源数据 |
| `train_10000.csv` | 17.2MB | 10000×108 | 特征工程后训练集，107个feature列+1个label列，标签0-5六分类 | `A10.py` 模型训练 |
| `validate_1000.csv` | 1.8MB | 1000×108 | 验证集，结构同train_10000.csv，用于模型性能评估 | `A10.py` 模型验证 |

**`data/` 目录下的CSV文件：**

| 文件 | 大小 | 说明 |
|------|------|------|
| `diabetes_*.csv` (共47个) | 23.8KB | 糖尿病数据集，768行×9列，与 `python/data/diabetes.csv` 同源 |
| `diabetes_20000_samples_*.csv` (共3个) | 584KB | 20000样本的大数据集 |
| `111_*.csv` (2个) | 486B | 小数据集 |
| `22222222_*.csv` | 118B | 极小数据集 |

**`data/csv_for_doctor/` 目录：**

| 文件 | 大小 | 说明 |
|------|------|------|
| `patient_*.csv` (共20个) | ~190B | 健康档案转诊CSV，由HealthProfileServiceImpl.generateCsvForDiagnostician()生成 |

**`data/datasets/` 目录：**

| 文件 | 大小 | 说明 |
|------|------|------|
| `diabetes_train_*.csv` (3个) | 10-19KB | 训练数据集 |

**`data/train/`、`data/test/` 目录：**
- 当前为空（含 `.gitkeep`），用于用户上传CSV后扫描自动入库

**数据库中的数据：**
- `diabetes_record` 表有768条糖尿病数据（与 `diabetes.csv` 同源）
- `sys_trainfile` 表预置了3条训练文件记录
- `sys_testfile` 表预置了3条测试文件记录

**CSV数据来源：**
- 上传训练集页面 → 存入 `data/` 目录 → 自动入库 `sys_trainfile`
- 扫描目录功能 → 扫描 `data/`、`data/train/`、`data/test/`、`python/data/` → 自动入库
- 健康档案转诊 → `HealthProfileServiceImpl.generateCsvForDiagnostician()` → 存入 `data/csv_for_doctor/`

**CSV数据去向：**
- 训练：`train.py <csv_path> <model_output_path>` → 读取CSV → 训练模型
- 批量预测：`predict.py <csv_path> <json_name> <title> <model_path>` → 读取CSV → 输出JSON

### 2.2 JSON 文件（预测结果）

**`python/data/` 目录下的JSON文件（predict.py 批量预测输出）：**

| 文件 | 大小 | 说明 |
|------|------|------|
| `predict11.json` | 11.5KB | 批量预测结果，title="2025年糖尿病筛查预测" |
| `{UUID}.json` × 8 | ~11.5KB/个 | 批量预测结果（UUID命名，避免冲突） |

**JSON结构：**
```json
{
  "title": "2025年糖尿病筛查预测",
  "create_time": "2025-04-27 22:47:00",
  "predictions": [
    {
      "glucose": 148, "bloodPressure": 72, "skinThickness": 35,
      "insulin": 0, "bmi": 33.6, "dpf": 0.627, "age": 50,
      "probability": 0.856, "result": "高风险"
    }
  ]
}
```

**生成方式：** `predict.py <csv_path> <json_name> <title> <model_path>` → 读取CSV → 输出JSON到 `python/data/`

### 2.3 NPY 文件（NumPy数组）

| 文件 | 路径 | 用途 | 使用者 |
|------|------|------|--------|
| `*_background.npy` | `data/models/pth_models/` | SHAP背景数据（训练时前200个样本） | `predict_single.py` 第117行：SHAP DeepExplainer |
| `similar_cases.npy` | `data/models/npy_data/` | 历史病例特征数据（用于余弦相似度匹配） | `predict_single.py` 第668行：相似病例分析 |

### 2.4 PTH 文件（PyTorch模型权重）

**`data/models/pth_models/` 目录下的PyTorch模型：**

| 文件 | 说明 |
|------|------|
| `diabetes_model.pth` | 主模型文件 |
| `*_fold1.pth` ~ `*_fold5.pth` | 5折交叉验证的模型文件 |
| `*_scaler.pkl` | StandardScaler标准化器 |
| `*_encoder.pkl` | LabelEncoder标签编码器 |
| `*_background.npy` | SHAP背景数据 |
| `*_metrics.json` | 模型评估指标 |

**模型结构：**
```python
DiabetesModel(nn.Module):
    fc1: Linear(8, 64) → ReLU → Dropout(0.2)
    fc2: Linear(64, 32) → ReLU → Dropout(0.2)
    fc3: Linear(32, 2)   # 二分类输出
```

**8个输入特征：** Pregnancies, Glucose, BloodPressure, SkinThickness, Insulin, BMI, DiabetesPedigreeFunction, Age

### 2.5 PKL 文件（Python序列化对象）

| 文件 | 路径 | 用途 | 使用者 |
|------|------|------|--------|
| `*_scaler.pkl` | `data/models/pkl_files/` | StandardScaler标准化器（特征缩放参数） | `predict_single.py`、`predict.py` |
| `*_encoder.pkl` | `data/models/pkl_files/` | LabelEncoder标签编码器（0/1映射） | `predict_single.py`、`predict.py` |

### 2.6 H5 文件（Keras模型）

**`data/models/h5_models/` 目录下的H5模型：**

| 文件 | 大小 | 说明 |
|------|------|------|
| `diabetes_*.h5` (共14个) | 191KB | Keras模型（历史遗留格式） |
| `1e53aa6902604656b03114fe9a3077ab.h5` | 1.4MB | UUID命名的Keras模型 |
| `2fdc1a15caad42539ee1a4968a6698a0.h5` | 1.4MB | UUID命名的Keras模型 |

## 3. 数据库中的数据资产

### 3.1 训练数据表

| 表名 | 数据量 | 说明 |
|------|--------|------|
| `diabetes_record` | 768条 | Pima Indians糖尿病数据集（8特征+1标签），是实际的训练数据来源 |
| `sys_trainfile` | 3条 | 训练文件注册表（CSV文件元数据） |
| `sys_testfile` | 3条 | 测试文件注册表 |

### 3.2 训练任务与模型版本表

| 表名 | 数据量 | 说明 |
|------|--------|------|
| `sys_train_task` | 3条 | 训练任务记录（任务名、参数、状态、指标） |
| `sys_model_version` | 3条 | 模型版本记录（v1.0.0手动上传、v1.1.0和v2.0.0在线训练） |

### 3.3 预置模型版本数据

```sql
-- sys_model_version 预置数据
(1, 'diabetes_model', 'v1.0.0', 'manual',      -- 手动上传
   'data/models/pth_models/diabetes_model.pth',
   'data/models/scaler/diabetes_scaler.pkl',
   'data/models/encoder/diabetes_encoder.pkl')
(2, 'diabetes_model', 'v1.1.0', 'online_train', -- 在线训练，已激活(active)
   'data/models/pth_models/diabetes_model_v1.1.pth',
   'data/models/scaler/diabetes_scaler_v1.1.pkl',
   'data/models/encoder/diabetes_encoder_v1.1.pkl')
(3, 'diabetes_model', 'v2.0.0', 'online_train', -- 在线训练
   'data/models/pth_models/diabetes_model_v2.0.pth',
   'data/models/scaler/diabetes_scaler_v2.0.pkl',
   'data/models/encoder/diabetes_encoder_v2.0.pkl')
```

**注意：** 预置的模型文件路径（`data/models/pth_models/`）指向旧目录，实际文件可能存在。新训练的模型应保存到根目录下的 `models/` 文件夹（已被用户删除）。

## 4. 文件路径配置（application.yml）

```yaml
base.path: ./

files:
  pythonUpload.path: ${base.path}/data/              # 训练集上传目录
  pythonDownload.path: ${base.path}/                  # Python输出根目录
  models.path: ${base.path}/models/                   # 模型文件目录（新，已被用户删除）
  trainLogs.path: ${base.path}/logs/                  # 训练日志目录（新）
  pythonModelPath.path: ${base.path}/models/diabetes_model.pth  # 默认模型路径
```

## 5. 数据流转关系

```
┌─────────────────────────────────────────────────────────────────┐
│                        数据流转图                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  [用户上传CSV] ──→ data/ ──→ sys_trainfile表 ──→ [训练集管理页面] │
│       │                                                         │
│       ▼                                                         │
│  [在线模型训练] ──→ train.py ──→ models/{name}.pth              │
│       │                     ──→ models/{name}_scaler.pkl        │
│       │                     ──→ models/{name}_encoder.pkl       │
│       │                     ──→ models/{name}_background.npy    │
│       ▼                                                         │
│  [模型管理页面] ──→ sys_model_version表 ──→ 激活模型             │
│       │                                                         │
│       ▼                                                         │
│  [风险快测/预测] ──→ SinglePredictController                    │
│       │              ──→ /api/model/active 获取激活模型路径      │
│       │              ──→ predict_single.py --model {base_path}  │
│       ▼                                                         │
│  [预测结果] ──→ 健康档案 / 诊疗记录 / 图表展示                  │
│                                                                 │
│  [diabetes_record表] ──→ 训练数据源（768条）                    │
│  [similar_cases.npy] ──→ 相似病例匹配                          │
│  [data/models/] ──→ 历史遗留模型（H5/PTH/PKL/NPY）             │
│  [python/data/] ──→ Python训练/验证数据 + 批量预测结果        │
└─────────────────────────────────────────────────────────────────┘
```

## 6. 当前数据问题与注意事项

### 6.1 文件缺失问题

| 问题 | 说明 | 影响 |
|------|------|------|
| 根目录 `models/` 被删除 | 用户已删除新的模型统一存放目录 | 新训练的模型无存放位置 |
| 预置路径指向旧目录 | `sys_model_version` 的文件路径指向 `data/models/pth_models/` | 需要手动迁移或更新配置 |
| `data/train/`、`data/test/` 为空 | 新建的归档目录尚无用户上传的CSV | 扫描入库功能暂无新文件可发现 |

### 6.2 目录职责划分

| 目录 | 职责 | 操作方 |
|------|------|--------|
| `data/train/` | 训练集CSV存放 | 用户上传 / 扫描入库 |
| `data/test/` | 测试集CSV存放 | 用户上传 / 扫描入库 |
| `data/csv_for_doctor/` | 转诊CSV | HealthProfileServiceImpl |
| `data/models/` | 历史遗留模型（H5/PTH/PKL/NPY） | 早期训练脚本 |
| `python/data/` | Python训练/验证数据 + 批量预测结果JSON | A10.py（读写）/ predict.py（写入） |
| `models/` | 模型文件统一存放（已删除） | train.py（写入）/ predict_single.py（读取） |
| `logs/` | 训练日志 | TrainTaskService |
| `python/similar_cases.npy` | 相似病例数据 | predict_single.py |

### 6.3 文件命名规则

**历史遗留模型（data/models/）：**
```
data/models/pth_models/
├── {modelName}.pth                 # 模型权重
├── {modelName}_fold1.pth ~ fold5.pth # 5折交叉验证模型
├── {modelName}_scaler.pkl          # 标准化器
├── {modelName}_encoder.pkl         # 标签编码器
├── {modelName}_background.npy      # SHAP背景数据
└── {modelName}_metrics.json        # 评估指标
```

**新目录规范（原models/目录，已删除）：**
```
models/
├── {modelName}.pth                 # 模型权重
├── {modelName}_scaler.pkl          # 标准化器
├── {modelName}_encoder.pkl         # 标签编码器
└── {modelName}_background.npy      # SHAP背景数据
```

## 7. 数据同步操作指南

### 7.1 扫描入库操作

```
训练集管理页面 → 点击"扫描目录" → 后端扫描以下目录：
  - data/
  - data/train/
  - data/test/
  - python/data/
→ 自动识别CSV/XLSX/XLS文件 → 写入 sys_trainfile 表
```

### 7.2 模型归档操作

```
旧目录 data/models/ 下的文件可迁移到 models/（需先恢复models/目录）：
  data/models/pth_models/*.pth     → models/
  data/models/pkl_files/*.pkl       → models/
  data/models/npy_data/*.npy        → models/
  data/models/h5_models/*.h5        → models/（如有）

迁移后需要更新 sys_model_version 表中的文件路径。
```

### 7.3 数据库路径更新SQL

```sql
-- 将旧路径更新为新路径（需先恢复models/目录）
UPDATE sys_model_version SET
    file_path = REPLACE(file_path, 'data/models/pth_models/', 'models/'),
    scaler_path = REPLACE(scaler_path, 'data/models/scaler/', 'models/'),
    encoder_path = REPLACE(encoder_path, 'data/models/encoder/', 'models/');

-- 将训练任务的输出路径更新
UPDATE sys_train_task SET
    model_output_path = REPLACE(model_output_path, 'data/models/pth_models/', 'models/');
```