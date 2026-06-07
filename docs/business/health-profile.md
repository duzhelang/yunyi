# 健康档案管理

## 功能概述
管理患者的健康档案信息，包括基本信息、体检数据、病史等。

## 数据字段
- **基本信息**: 姓名、性别、年龄、身高、体重
- **体检数据**: 血压、血糖、血脂、BMI
- **病史**: 糖尿病家族史、既往病史
- **生活习惯**: 饮食、运动、吸烟、饮酒

## 草稿保存
```javascript
const { saveDraft, loadDraft } = useDraftPersistence('health-profile')

// 自动保存草稿
watch(formData, (newVal) => {
  saveDraft(newVal)
}, { deep: true })
```

## DPF计算器
**功能**: 计算糖尿病预测因子（Diabetes Pedigree Function）
- 输入家族史信息
- 计算DPF值
- 显示风险评估

## 组件架构
HealthProfileView.vue 采用容器-子组件架构（261行），拆分为 12 个独立子组件和 5 个 composable 函数。

### 容器组件
- `HealthProfileView.vue`: 顶层容器，编排所有子组件，组合 composable

### 子组件
| 组件 | 职责 |
|------|------|
| WelcomeBanner | 欢迎横幅，渐变背景 |
| HealthFormCard | 健康指标表单（血糖/BMI/血压/家族史），保存时自动发送诊断员 |
| HealthAdviceCard | AI 个性化建议展示 |
| HealthChartCard | ECharts 趋势图表（血糖/BMI 双模式） |
| HealthHistoryCard | 历史档案列表与操作 |
| HealthToolCard | 工具入口网格（急救/碳水/足部） |
| DailyCheckinCard | 每日习惯打卡与进度条 |
| DpfInfoDialog | DPF 说明弹窗 |
| ResultDialog | 预测结果弹窗（风险仪表盘+图表轮播+指标百分位+AI 处方） |
| EmergencyToolDialog | 低血糖急救指南弹窗 |
| CarbCalculatorDialog | 碳水计算器弹窗 |
| FootCareDialog | 足部护理指南弹窗 |

### Composable 函数
| 函数 | 职责 |
|------|------|
| useHealthForm | 表单数据、验证、保存、预测逻辑 |
| useHealthChart | ECharts 图表渲染生命周期 |
| useHealthHistory | 历史档案 CRUD 操作 |
| useHealthTools | 工具弹窗状态与逻辑 |
| useDailyCheckin | 每日打卡逻辑 |

## 界面布局
```
┌──────────────────────────────────────────────────┐
│                 WelcomeBanner                     │
├────────────────────────┬─────────────────────────┤
│    HealthFormCard      │    HealthChartCard       │
│    (左侧 16/24)        │    HealthHistoryCard     │
│                        │    HealthToolCard        │
│    HealthAdviceCard    │    DailyCheckinCard      │
│                        │    (右侧 8/24)           │
├────────────────────────┴─────────────────────────┤
│  弹窗: ResultDialog / EmergencyToolDialog        │
│        CarbCalculatorDialog / FootCareDialog     │
│        DpfInfoDialog / DpfCalculator             │
└──────────────────────────────────────────────────┘
```

## 数据验证
- 血糖范围: 3.9-6.1 mmol/L
- 血压范围: 90-140 mmHg
- BMI范围: 18.5-24.9