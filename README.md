# 糖尿病健康管理系统

## 项目简介

这是一个基于Spring Boot + Vue的糖尿病健康管理系统，集成了机器学习模型用于糖尿病预测，提供了完整的健康档案管理、治疗记录跟踪、视频教育等功能。

## 技术栈

### 后端
- Spring Boot 3.3.5
- Java 17
- MyBatis Plus
- MySQL 8.0
- Redis
- JWT 认证
- Python 3 (数据分析与预测)
- PyTorch (机器学习模型)

### 前端
- Vue 3.4.0
- Element Plus 2.4.4
- Echarts 5.4.3
- axios
- video.js (视频播放)
- mavon-editor (富文本编辑器)
- Pinia (状态管理)
- Vite (构建工具)

## 项目结构

```
Software-ODA125/
├── python/                # Python数据分析与预测模块
│   ├── A10.ipynb          # Jupyter Notebook分析文件
│   ├── A10.py             # 数据分析脚本
│   ├── data_analysis.py   # 数据处理工具
│   ├── diabetes_chat.py   # 糖尿病聊天机器人
│   ├── predict.py         # 预测脚本
│   ├── read_data.py       # 数据读取工具
│   └── train.py           # 模型训练脚本
├── sql/                   # 数据库脚本
│   └── Software-ODA.sql  # 数据库初始化脚本
├── src/                   # 后端代码
│   ├── main/java/com/oda/springboot/
│   │   ├── Main/          # 主客户端
│   │   ├── common/        # 通用工具类
│   │   ├── config/        # 配置类
│   │   ├── controller/    # 控制器
│   │   ├── entity/        # 实体类
│   │   ├── exception/     # 异常处理
│   │   ├── mapper/        # 数据访问层
│   │   ├── service/       # 服务层
│   │   ├── test/          # 测试类
│   │   ├── utils/         # 工具类
│   │   └── SpringbootApplication.java  # 应用入口
│   └── main/resources/    # 资源文件
├── vue/                   # 前端代码
│   ├── public/            # 静态资源
│   └── src/               # 源代码
│       ├── assets/        # 静态资源
│       ├── components/    # 组件
│       ├── router/        # 路由
│       ├── store/         # 状态管理
│       ├── styles/        # 样式
│       ├── utils/         # 工具类
│       ├── views/         # 页面
│       ├── App.vue        # 根组件
│       └── main.js        # 入口文件
├── .gitignore             # Git忽略文件
├── README.md              # 项目说明
├── pom.xml                # Maven配置
└── c41dc0eb-bcba-4589-a22f-82eeb9e748ea  # 项目标识文件
```

## 核心功能

### 1. 用户管理
- 登录注册
- 个人信息管理
- 密码修改
- 基于RBAC的权限模型

### 2. 健康管理
- 健康档案管理
- 治疗记录跟踪
- 数据可视化分析

### 3. 糖尿病预测
- 基于机器学习模型的糖尿病风险预测
- 预测结果分析
- 健康建议生成

### 4. 分析记录管理
- 群体分析历史记录查询
- 个体洞察历史记录查询
- 分析记录详情查看
- 分析记录删除
- 数据持久化存储

### 5. 教育资源
- 糖尿病相关视频播放
- 健康知识文章

### 6. 系统功能
- 文件上传下载
- 在线聊天
- 留言板
- 百度地图集成
- 支付宝沙箱支付

## 机器学习模型

项目集成了基于PyTorch的糖尿病预测模型，使用8个特征进行预测：
- 怀孕次数 (Pregnancies)
- 血糖浓度 (Glucose)
- 血压 (BloodPressure)
- 皮肤厚度 (SkinThickness)
- 胰岛素水平 (Insulin)
- 体重指数 (BMI)
- 糖尿病家族史 (DiabetesPedigreeFunction)
- 年龄 (Age)

模型训练流程：
1. 数据预处理（填充缺失值、特征缩放）
2. 模型训练（3层神经网络）
3. 模型评估与保存
4. 预测推理

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- Node.js 14+
- MySQL 8.0+
- Redis (可选)
- Python 3.7+ (用于数据分析和预测)

### 后端部署
1. 克隆项目
2. 导入数据库：执行 `sql/Software-ODA.sql`
3. 修改配置：编辑 `src/main/resources/application.yml` 中的数据库连接信息
4. 构建项目：`mvn clean package`
5. 启动应用：`java -jar target/Software-ODA-0.0.1-YUNYI.jar`

### 前端部署
1. 进入前端目录：`cd vue`
2. 安装依赖：`npm install`
3. 开发环境运行：`npm run serve`
4. 生产环境构建：`npm run build`

### 模型训练
1. 准备训练数据（CSV格式）
2. 执行训练脚本：`python python/train.py <输入CSV路径> <模型输出路径>`
3. 模型将保存为 `.pth` 文件，同时生成编码器和缩放器文件

## API文档

项目集成了SpringDoc OpenAPI 3，可通过以下地址访问API文档：
- 开发环境：`http://localhost:8080/swagger-ui.html`
- 生产环境：`http://your-domain:8080/swagger-ui.html`

## 系统架构

### 后端架构
- 控制器层：处理HTTP请求和响应
- 服务层：实现业务逻辑
- 数据访问层：与数据库交互
- 工具层：提供通用功能

### 前端架构
- Vue 3 Composition API
- 组件化开发
- 路由管理 (Vue Router 4)
- 状态管理 (Pinia)
- 响应式布局
- Vite 构建工具

## 安全措施

- JWT认证
- 密码加密存储
- 权限控制
- 输入验证
- 异常处理

## 未来规划

- 移动端适配
- 实时数据同步
- 更多机器学习模型集成
- 智能健康建议系统
- 社区互动功能

## 贡献

欢迎提交Issue和Pull Request！

## 许可证

MIT License

## 联系我们

- 项目地址：***
- 邮箱：1337497448@qq.com
- 电话：13294551991

---

**项目版本：** 1.1.0
**最后更新：** 2026-05-01
