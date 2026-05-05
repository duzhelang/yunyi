---
name: "oda-project"
description: "Software-ODA125 糖尿病健康管理系统的完整项目知识库。包含后端Spring Boot、前端Vue、Python算法层的全部架构、路由、API、数据库、组件等详细信息。当用户询问项目结构、功能模块、技术细节、业务逻辑时，应调用此技能。"
---

# Software-ODA125 糖尿病健康管理系统 — 项目知识库

## 1. 项目概览

| 项目 | 信息 |
|------|------|
| 名称 | Software-ODA125 糖尿病健康管理系统 |
| 版本 | 1.0.1 |
| 最后更新 | 2026-05-05 |
| 架构 | 前后端分离（Spring Boot + Vue 3） |
| 数据库 | MySQL 8.0 (数据库名: `dongfang`, 编码: utf8mb4) |
| 缓存 | Redis (端口 6379, database 0) |

---

## 2. 技术栈

### 2.1 后端 (Java)

| 技术 | 版本/说明 |
|------|----------|
| Spring Boot | 3.3.5 |
| Java | 17 |
| MyBatis-Plus | 3.5.9 |
| MySQL Connector | 8.0.33 |
| Redis | Spring Data Redis + Lettuce |
| JWT | java-jwt 3.10.3 (Auth0) |
| Swagger | springdoc-openapi 2.2.0 (Swagger 3) |
| EasyExcel | 3.3.2 |
| Hutool | 5.7.20 |
| Lombok | 编译器注解 |
| Jython | 2.7.0 (Python 调用) |
| 构建工具 | Maven (阿里云镜像) |
| 启动类 | `com.oda.springboot.SpringbootApplication` |

### 2.2 前端 (Vue)

| 技术 | 版本/说明 |
|------|----------|
| Vue | 3.4.0 (Options API) |
| Vue Router | 4.2.0 (createWebHistory) |
| Pinia | 2.1.7 (状态管理) |
| Element Plus | 2.4.4 (UI组件库) |
| ECharts | 5.6.0 (图表) |
| Axios | 1.6.0 (HTTP) |
| Vite | 5.0.0 (构建工具) |
| video.js | 7.18.1 (视频播放) |
| mavon-editor | 2.10.4 (Markdown编辑器) |
| sockjs-client | 1.6.1 (WebSocket) |
| TypeScript | 6.0.3 (开发依赖) |

### 2.3 Python 算法层

| 技术 | 版本 |
|------|------|
| Python | 3.12.5 |
| PyTorch | 2.11.0 |
| scikit-learn | 1.8.0 |
| pandas | 2.3.0 |
| numpy | 2.3.0 |
| matplotlib | 3.10.0 |
| joblib | 1.5.0 |
| scipy | 1.15.0 |
| shap | 0.46.0 |
| openai | 2.33.0 |

---

## 3. 项目目录结构

```
Software-ODA125/
├── .gitignore                      # Git忽略规则
├── .trae/                          # Trae IDE 配置
│   └── skills/                     # 技能目录
├── pom.xml                         # Maven构建文件
├── requirements.txt                # Python依赖
├── README.md                       # 项目说明文档
├── src/                            # Java后端
│   ├── main/
│   │   ├── java/com/oda/springboot/
│   │   └── resources/
│   └── test/
├── python/                         # Python算法
├── sql/                            # SQL初始化脚本
└── vue/                            # Vue前端
```

---

## 4. 后端详细结构

### 4.1 分层架构 — 标准 MVC + Service

```
src/main/java/com/oda/springboot/
├── SpringbootApplication.java      # 启动类 (@SpringBootApplication, @EnableAsync, @MapperScan)
├── Main/
│   └── Client.java                 # 测试客户端
├── common/                         # 通用工具
│   ├── Constants.java              # 常量 (CODE_200/401/400/500/600)
│   ├── Result.java                 # 统一响应泛型类 {code, msg, data}
│   └── RoleEnum.java               # 角色枚举
├── config/                         # 配置
│   ├── interceptor/
│   │   └── JwtInterceptor.java     # JWT拦截器 (从header取token, @AuthAccess跳过)
│   ├── AsyncConfiguration.java     # 异步线程池配置
│   ├── AuthAccess.java             # 自定义注解 (跳过JWT验证)
│   ├── CorsConfig.java             # 跨域配置
│   ├── InterceptorConfig.java      # 拦截器注册 (排除 /api/diabetes/chat)
│   ├── MybatisPlusConfig.java      # MyBatis-Plus分页插件
│   ├── SwaggerConfig.java          # Swagger 3 文档配置
│   └── WebConfig.java              # 静态资源配置
├── controller/                     # 控制器 (REST API)
│   ├── dto/                        # 数据传输对象
│   │   ├── UserDTO.java            # 用户登录DTO
│   │   └── UserPasswordDTO.java    # 密码修改DTO
│   ├── UserController.java         # /api/user — 用户CRUD、登录、注册、导出
│   ├── RoleController.java         # /api/role — 角色管理
│   ├── MenuController.java         # /api/menu — 菜单管理
│   ├── DatasetController.java      # /api/dataset — 训练数据集
│   ├── DiabetesController.java     # /api/diabetes/chat — 智能对话
│   ├── DiabetesEducationController.java # /api/diabetesEducation — 科普内容
│   ├── DiabetesVideoController.java     # /api/diabetesVideo — 视频管理
│   ├── DetailbordController.java   # /api/detailbord — 详细报表
│   ├── EchartsController.java      # /api/echarts — 图表数据
│   ├── EducationCommentController.java # /api/educationComment — 科普留言
│   ├── FileController.java         # /api/file — 文件上传下载
│   ├── HealthProfileController.java # /api/healthProfile — 健康档案
│   ├── IndexController.java        # /api/index — 首页信息
│   ├── MessageController.java      # /api/message — 消息/报修
│   ├── ModelController.java        # /api/model — 模型管理
│   ├── ModelVersionController.java # /api/modelVersion — 模型版本
│   ├── SinglePredictController.java # /api/singlePredict — 单条预测
│   ├── SysConfigController.java    # /api/system — 系统配置(默认模型)
│   ├── TestFileController.java     # /api/testFile — 测试文件
│   ├── TrainTaskController.java    # /api/trainTask — 训练任务
│   ├── TreatmentRecordController.java # /api/treatmentRecord — 诊疗档案
│   └── pythonController.java       # Python脚本调用控制器
├── entity/                         # 实体类 (18个)
│   ├── User.java                   # sys_user 表
│   ├── Role.java                   # sys_role 表
│   ├── Menu.java                   # sys_menu 表
│   ├── RoleMenu.java               # sys_role_menu 表
│   ├── Files.java                  # 文件记录
│   ├── TestFiles.java              # 测试文件
│   ├── Dict.java                   # sys_dict 字典
│   ├── SysConfig.java              # sys_config 系统配置
│   ├── Message.java                # sys_message 消息
│   ├── DiabetesEducation.java      # diabetes_education 科普
│   ├── DiabetesVideo.java          # diabetes_video 视频
│   ├── DiabetesRecord.java         # diabetes_record 记录
│   ├── EducationComment.java       # education_comment 留言
│   ├── HealthProfile.java          # user_health_profiles 健康档案
│   ├── TreatmentRecord.java        # treatment_record 诊疗档案
│   ├── TrainTask.java              # sys_train_task 训练任务
│   ├── ModelVersion.java           # sys_model_version 模型版本
│   └── OnlineDate.java             # 在线日期
├── exception/                      # 异常处理
│   ├── GlobalExceptionHandler.java # 全局异常处理器 (@ControllerAdvice)
│   └── ServiceException.java       # 自定义业务异常
├── mapper/                         # MyBatis Mapper接口 (19个)
├── service/                        # 服务层
│   ├── impl/                       # 服务实现 (9个)
│   └── 接口文件                    # 接口定义 (12个)
├── test/                           # 测试控制器
│   └── TestController.java
└── utils/                          # 工具类
    ├── CodeGenerator.java          # MyBatis-Plus代码生成器
    ├── TokenUtils.java             # JWT Token工具 (获取当前用户)
    ├── PropertyUtil.java           # 属性工具
    └── UsePythonUtils.java         # Python进程调用工具
```

### 4.2 后端配置 (application.yml)

```yaml
server:
  port: 9090
  tomcat.accesslog:
    enabled: true
    directory: ./logs

spring:
  config.import: optional:classpath:secrets/application-secrets.yml
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/dongfang?serverTimezone=GMT%2b8&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: "010125"
  data.redis:
    host: 127.0.0.1
    port: 6379
    database: 0
  servlet.multipart:
    max-file-size: 1500MB
    max-request-size: 1500MB

mybatis-plus:
  configuration.log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

base.path: ./
files:
  upload.path: ${base.path}/files/
  avatar.path: ${files.upload.path}avatar/
  common.path: ${files.upload.path}common/
  pythonUpload.path: ${base.path}/data/
  pythonDownload.path: ${base.path}/
  JsonDownload.path: ${base.path}/json/
  pythonDataTestUpload.path: ${base.path}/OnlinePredict/
  pythonExe.path: python
  pythonTrainMain.path: ${base.path}/python/train.py
  pythonPredictMain.path: /python/predict.py
  pythonChatScript.path: ${base.path}/python/diabetes_chat.py
  pythonModelPath.path: ${base.path}/diabetes_model.pth

# AI API Keys (从环境变量读取)
zhipu.api.key: ${ZHIPU_API_KEY:}
deepseek.api.api-key: ${DEEPSEEK_API_KEY:}
kimi.api.api-key: ${KIMI_API_KEY:}
mimo.api.api-key: ${MIMO_API_KEY:}
```

### 4.3 核心API端点

| 模块 | 方法 | 端点 | 说明 | 认证 |
|------|------|------|------|------|
| 用户 | POST | /api/user/login | 登录 | 公开 |
| 用户 | POST | /api/user/register | 注册 | 公开 |
| 用户 | GET/PUT/DELETE | /api/user | 用户CRUD | JWT |
| 用户 | GET | /api/user/export | 用户导出Excel | JWT |
| 角色 | GET/POST/PUT/DELETE | /api/role | 角色管理CRUD | JWT |
| 菜单 | GET/POST/PUT/DELETE | /api/menu | 菜单管理CRUD | JWT |
| 系统 | GET | /api/system/defaultModel | 获取默认AI模型 | 公开 |
| 系统 | POST | /api/system/defaultModel | 设置默认AI模型(管理员) | JWT |
| 对话 | POST | /api/diabetes/chat | AI智能问答 | 公开 |
| 文件 | POST | /api/file/upload | 文件上传 | JWT |
| 文件 | GET | /api/file/{id} | 文件下载 | JWT |
| 预测 | POST | /api/singlePredict/run | 单条预测 | JWT |
| 图表 | GET | /api/echarts/* | 图表数据 | JWT |
| 科普 | GET/POST/PUT/DELETE | /api/diabetesEducation | 科普内容CRUD | JWT |
| 视频 | GET/POST/PUT/DELETE | /api/diabetesVideo | 视频CRUD | JWT |
| 档案 | GET | /api/health-profile/list | 获取用户历史记录 | JWT |
| 档案 | DELETE | /api/health-profile/{id} | 删除健康档案记录 | JWT |
| 档案 | POST | /api/health-profile/save-and-predict | 保存+AI风险预测 | JWT |
| 档案 | POST | /api/health-profile/send-to-doctor/{id} | 生成CSV发送诊断员 | JWT |
| 诊疗 | GET/POST/PUT/DELETE | /api/treatmentRecord | 诊疗档案CRUD | JWT |
| 训练 | POST | /api/trainTask/* | 训练任务管理 | JWT |

### 4.4 JWT认证机制

- **登录流程**: UserController.login() → 验证用户名密码 → 生成JWT Token（使用用户密码作为密钥）→ 返回 UserDTO(token, role, menus)
- **拦截流程**: JwtInterceptor.preHandle() → 检查 @AuthAccess 注解 → 从header取token → 解码获取userId → 查询用户 → 密码验签
- **前端注入**: request.js 请求拦截器从 localStorage 读取 `user.token`，设置 header `token`
- **响应拦截**: code='401' 时自动跳转 /login
- **白名单路径**: /api/diabetes/chat, /api/system/defaultModel (GET), /user/login, /user/register

### 4.5 统一响应格式

```java
public class Result<T> {
    private String code;   // "200"成功, "500"系统错误, "401"权限不足
    private String msg;    // 提示信息
    private T data;        // 泛型数据
}
```

---

## 5. 前端详细结构

### 5.1 入口与配置

**main.js**: 引入 Element Plus、ECharts、Pinia、全局样式、路由，挂载到 `#app`

**vite.config.ts**:
- 端口 8080
- `/api` 代理到 `http://localhost:9090`
- 别名 `@` → `./src`
- 分包策略: vendor / element-plus / echarts / axios
- preserveWhitespace: true

### 5.2 目录结构

```
vue/src/
├── App.vue                        # 根组件
├── main.js                        # 入口文件
├── vite-env.d.ts                  # Vite类型声明
├── assets/                        # 静态资源
│   ├── gloable.css                # 全局样式
│   ├── logo.png
│   └── 1.jpeg, 2.jpeg, 3.jpeg     # 背景图
├── components/                    # 公共组件
│   ├── Aside.vue                  # 侧边栏菜单
│   ├── Header.vue                 # 顶部导航栏
│   └── HelloWorld.vue
├── router/
│   └── index.js                   # 路由配置 (动态路由)
├── store/
│   └── index.js                   # Pinia状态管理 (currentPathName)
├── styles/                        # SCSS样式 (8个文件)
│   ├── index.scss                 # 主样式入口
│   ├── variables.scss             # 变量定义
│   ├── mixin.scss                 # 混入
│   ├── element-ui.scss            # Element UI覆盖
│   ├── element-variables.scss     # Element UI变量
│   ├── sidebar.scss               # 侧边栏样式
│   ├── btn.scss                   # 按钮样式
│   └── transition.scss            # 过渡动画
├── utils/                         # 工具
│   ├── request.js                 # axios封装
│   └── cacheHelper.js             # localStorage封装
└── views/                         # 31个页面组件
```

### 5.3 页面组件清单

| # | 组件名 | 文件名 | 功能说明 |
|:-:|--------|--------|---------|
| 1 | Manage | Manage.vue | 主布局 (el-container, el-aside, el-header, el-main) |
| 2 | Home | Home.vue | 首页/仪表盘 |
| 3 | Login | Login.vue | 登录页 |
| 4 | Register | Register.vue | 注册页 |
| 5 | User | User.vue | 用户管理 (el-table, CRUD) |
| 6 | Role | Role.vue | 角色管理 (el-tree角色权限分配) |
| 7 | Menu | Menu.vue | 菜单管理 (级联选择器) |
| 8 | Person | Person.vue | 个人信息 (el-form, el-switch偏好设置) |
| 9 | Password | Password.vue | 修改密码 |
| 10 | Dashbord | Dashbord.vue | 数据报表 (ECharts) |
| 11 | Detailbord | Detailbord.vue | 详细报表 (ECharts多图) |
| 12 | DiabetesChat | DiabetesChat.vue | 智能问答 (AI对话, 打字机效果, 模型切换) |
| 13 | DiabetesEducation | DiabetesEducation.vue | 糖尿病科普 (mavon-editor) |
| 14 | DoctorWorkbench | DoctorWorkbench.vue | 诊断工作台 |
| 15 | GroupAnalysis | GroupAnalysis.vue | 组合分析 |
| 16 | IndividualInsight | IndividualInsight.vue | 个体洞察 |
| 17 | DataCollection | DataCollection.vue | 数据采集 |
| 18 | DataTest | DataTest.vue | 采集日志 |
| 19 | DatasetManagement | DatasetManagement.vue | 训练集管理 |
| 20 | ModelManagement | ModelManagement.vue | 模型管理 (el-tabs) |
| 21 | OnlineTraining | OnlineTraining.vue | 在线模型训练 |
| 22 | PredictionWorkbench | PredictionWorkbench.vue | 预测工作台 |
| 23 | TestFile | TestFile.vue | 在线预测 |
| 24 | HealthProfileView | HealthProfileView.vue | 健康管理中心 |
| 25 | HealthCheck | HealthCheck.vue | 风险快检 |
| 26 | TreatmentRecord | TreatmentRecord.vue | 诊疗档案 (健康趋势图+AI计划/食谱双栏+就诊自查记录+弹窗详情) |
| 27 | Send | Send.vue | 故障报修 |
| 28 | List | List.vue | 报修详情 |
| 29 | OMlist | OMlist.vue | 运维详情 |
| 30 | OMsend | OMsend.vue | 信息回执 |
| 31 | About | About.vue | 关于页面 |
| 32 | 404 | 404.vue | 404页面 |

### 5.4 动态路由机制

```
路由模式: createWebHistory()

固定路由: /login, /register, /404 (白名单, 直接放行)

动态路由流程:
1. 用户登录成功 → 后端返回菜单树 → 存入 localStorage('menus')
2. setRoutes() 从 localStorage 读取 menus
3. 创建 Manage 路由作为根路由 (/), children 包含 3 个固定子路由:
   - person (个人信息)
   - password (修改密码)
   - treatment-record (诊疗档案) ← 始终注册，不依赖菜单缓存
4. 如果 storeMenus 存在，遍历 menus 通过 mapPagePath 映射表追加动态子路由
5. router.addRoute(manageRoute) 动态注册（放在 if 块外面，保证始终执行）
6. 路由守卫: 未匹配时自动重建动态路由，防止404

关键设计: manageRoute 创建和 router.addRoute() 均在 if(storeMenus) 块之外，
确保即使菜单缓存为空（如首次访问或缓存清除），person/password/treatment-record 仍可访问。

mapPagePath 映射表:
  'chat'/'ai-chat' → DiabetesChat
  'dataset-management'/'onlineupdate' → DatasetManagement
  'online-training'/'file' → OnlineTraining
  'model-manager' → ModelManagement
  'health-profile' → HealthProfileView
  'risk-quick' → HealthCheck
  'diabetes-education'/'diabetes-video' → DiabetesEducation
  'dashbord' → Dashbord
  'doctor-workbench' → DoctorWorkbench
  'prediction-workbench' → PredictionWorkbench
  'data-collection'/'collection-log'/'采集日志' → DataCollection
  'data-test' → DataTest
  'treatment-record' → TreatmentRecord (同时也在固定子路由中)
  'individual-insight' → IndividualInsight
  'group-analysis' → GroupAnalysis
  'detailbord'/'operation-detail'/'info-receipt' → Detailbord
  'test-file' → TestFile
  'omlist'/'report-detail'/'报修详情' → OMlist
  'omsend'/'fault-report'/'故障报修' → OMsend
  'online-predict'/'在线预测' → PredictionWorkbench
  'data-report'/'数据报表' → Dashbord
  'detail-report'/'详细报表' → Detailbord
  'menu' → Menu
  'user' → User
  'role' → Role
```

### 5.5 HTTP 请求封装 (request.js)

```javascript
baseURL: http://{host}:9090
超时: 60000ms
请求拦截器: 设置 Content-Type, 注入 token header
响应拦截器: 
  - 401 → 跳转 /login (白名单路径除外)
  - 返回 response.data 给调用方
```

### 5.6 缓存工具 (cacheHelper.js)

封装 localStorage: get/set/remove/clearAll/clearAllLoginState
缓存 keys: user, menus, currentPathName

---

## 6. Python 模块

### 6.1 文件清单

```python
python/
├── data/                  # Python专用数据目录（共13个文件）
│   ├── diabetes.csv               # Pima Indians原始数据集（768行×9列）
│   ├── train_10000.csv            # 特征工程后训练集（10000行×108列，6分类）
│   ├── validate_1000.csv          # 验证集（1000行×108列）
│   ├── validate_1000_y_true.json  # 验证集真实标签（1000个0-5整数）
│   ├── predict11.json             # 批量预测结果（title="2025年糖尿病筛查预测"）
│   ├── 2997371a-109a-4942-b431-e272b0d09659.json  # UUID命名批量预测结果1
│   ├── 2c542afe-888b-4b1c-abcd-7ed70af05544.json  # UUID命名批量预测结果2
│   ├── 40a9ce12-9251-4d51-ab1a-90512318970f.json  # UUID命名批量预测结果3
│   ├── 9a6a6d4c-d32c-46e3-a114-eabe87447e23.json  # UUID命名批量预测结果4
│   ├── ae6675dc-cb92-48e2-a23b-1e79c892d9eb.json  # UUID命名批量预测结果5
│   ├── c202853c-2263-4876-8ba9-5fec2df18971.json  # UUID命名批量预测结果6
│   ├── f35b1f7b-63cb-402f-8adb-bc2cb055b1fd.json  # UUID命名批量预测结果7
│   └── f6d877fd-8462-4ccf-83e5-baf8bf79dbdd.json  # UUID命名批量预测结果8
├── diabetes_chat.py       # AI 聊天机器人 (OpenAI兼容接口)
├── data_analysis.py       # 数据分析
├── predict.py             # 批量预测 (CSV输入)
├── predict_single.py      # 单条预测 (增强版, 含SHAP解释+图表)
├── train.py               # 模型训练 (3层神经网络)
├── read_data.py           # 数据读取
├── A10.py                 # 数据分析脚本（使用data/train_10000.csv）
├── similar_cases.npy      # 相似病例历史数据（predict_single.py使用）
└── Untitled-1.js          # (无关文件)
```

### 6.2 python/data/ 目录文件详细说明

| 文件 | 大小 | 作用 |
|------|------|------|
| `diabetes.csv` | 23.8KB | Pima Indians原始数据集，字段：Pregnancies,Glucose,BloodPressure,SkinThickness,Insulin,BMI,DiabetesPedigreeFunction,Age,Outcome（二分类0/1） |
| `train_10000.csv` | 17.2MB | 特征工程后训练集，107个feature列+1个label列，标签0-5六分类，由A10.py使用 |
| `validate_1000.csv` | 1.8MB | 验证集，结构同train_10000.csv，用于模型性能评估 |
| `validate_1000_y_true.json` | 9.8KB | 验证集真实标签，JSON数组格式，值为0-5整数，用于与预测结果对比 |
| `predict11.json` + 8个UUID.json | ~11.5KB/个 | 批量预测结果，predict.py输出，包含title, create_time, predictions数组 |

### 6.3 AI 聊天机器人 (diabetes_chat.py)

支持的模型 (通过 OpenAI 兼容 SDK 调用):

| 模型标识 | 服务商 | base_url | 模型名 |
|----------|--------|----------|--------|
| glm-4-flash | 智谱 AI | https://open.bigmodel.cn/api/paas/v4 | glm-4-flash-250414 |
| glm-4.7-flash | 智谱 AI | https://open.bigmodel.cn/api/paas/v4 | glm-4.7-flash |
| deepseek | DeepSeek | https://api.deepseek.com | deepseek-chat |
| kimi | Kimi | https://api.moonshot.cn/v1 | moonshot-v1-8k |
| mimo-v2.5-pro | 小米 MiMo | https://api.xiaomimimo.com/v1 | mimo-v2.5-pro |
| mimo-v2-flash | 小米 MiMo | https://api.xiaomimimo.com/v1 | mimo-v2-flash |
| mimo-v2-omni | 小米 MiMo | https://api.xiaomimimo.com/v1 | mimo-v2-omni |

系统提示: 强制扮演糖尿病医生，不承认是AI，用短句回答，与糖尿病无关问题则拒绝回答。

### 6.4 糖尿病预测模型

神经网络结构 (train.py / predict_single.py):
```python
DiabetesModel(nn.Module):
  fc1: Linear(8, 64) → ReLU → Dropout(0.2)
  fc2: Linear(64, 32) → ReLU → Dropout(0.2)
  fc3: Linear(32, 2)  # 二分类输出
```

8个输入特征: Pregnancies(怀孕次数), Glucose(血糖), BloodPressure(血压), SkinThickness(皮肤厚度), Insulin(胰岛素), BMI, DiabetesPedigreeFunction(遗传系数), Age(年龄)

增强预测 (predict_single.py) 额外功能:
- SHAP 特征重要性分析
- 风险仪表盘图表 (matplotlib)
- 健康指标对比分析图
- 百分位计算
- 相似病例分析
- 置信区间估算
- 规则引擎备用预测

---

## 7. 数据库

### 7.1 数据库配置
- 数据库名: `dongfang`
- 字符集: `utf8mb4`
- 排序规则: `utf8mb4_general_ci`

### 7.2 所有表清单

| # | 表名 | 说明 | 数据量 |
|:-:|------|------|:------:|
| 1 | sys_user | 用户表 (15条, 含admin/test/doctor1等) | 15 |
| 2 | sys_role | 角色表 (ROLE_ADMIN, ROLE_DOCTOR, ROLE_WORKER, ROLE_MAINTENANCE等) | 6 |
| 3 | sys_menu | 菜单表 (27个菜单, 8大模块) | 27 |
| 4 | sys_role_menu | 角色-菜单关联表 | 69 |
| 5 | sys_dict | 系统字典 (图标配置) | 16 |
| 6 | sys_message | 消息/故障报修表 | 15 |
| 7 | sys_trainfile | 训练文件表 | 3 |
| 8 | sys_train_task | 训练任务表 | 3 |
| 9 | sys_model_version | 模型版本表 | 3 |
| 10 | sys_testfile | 测试文件表 | 3 |
| 11 | sys_result | 在线数据统计 | 5 |
| 12 | diabetes_education | 糖尿病科普内容 (12篇) | 12 |
| 13 | diabetes_video | 糖尿病视频 | 0 |
| 14 | diabetes_record | 糖尿病记录 (768条训练数据) | 768 |
| 15 | education_comment | 科普留言 | 2 |
| 16 | treatment_record | 诊疗档案 | 3 |
| 17 | user_health_profiles | 健康档案 | 3 |

### 7.3 预置用户

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin | ROLE_ADMIN | 系统管理员 |
| test | 123 | ROLE_WORKER | 普通用户 |
| user | 123456 | ROLE_WORKER | 普通用户 |
| doctor1 | 123456 | ROLE_DOCTOR | 医生 |
| doctor2 | 123456 | ROLE_DOCTOR | 医生 |
| upload | (MD5) | ROLE_MAINTENANCE | 运维人员 |

### 7.4 菜单模块 (8大模块)

1. **主页** — 首页仪表盘
2. **系统管理** — 用户管理、角色管理、菜单管理
3. **AI模型中心** — 训练集管理、模型管理、在线模型训练
4. **用户服务** — 智能问答、糖尿病科普、健康管理中心、风险快检、诊疗档案
5. **诊断员服务** — 组合分析、个体洞察、数据采集、预测工作台、诊断工作台
6. **糖尿病预测中心** — 采集日志、在线预测、数据报表、详细报表
7. **运维中心** — 故障报修、报修详情、运维详情、信息回执
8. **关于**

---

## 8. 核心业务功能

### 8.1 用户系统
- 注册/登录 (JWT认证)
- 基于RBAC的权限控制 (角色+菜单)
- 个人信息管理 + 偏好设置

### 8.2 AI智能对话 (DiabetesChat.vue)
- 管理员可选择切换6种AI模型
- 打字机效果展示回答
- 上下文注入 (从URL参数读取检测结果)
- 附加功能: 报告解读 + 健康计划生成
- 控糖食谱保存格式: 使用 ` · ` 分隔食物项（非 `|`），格式为 `名称 份量 热量`
  - 示例: `全麦面包 2片(约160kcal) · 水煮蛋 1个(约70kcal)`
  - 诊断标签(diagnosis)包含"控糖食谱"关键字，用于与健康计划区分
  - 保存到 treatment_record 表，recordType='ai_plan'

### 8.3 糖尿病预测
- 单条预测: 输入8个特征 → Python神经网络 → 返回风险概率 + SHAP解释 + 图表
- 批量预测: 上传CSV文件 → Python批量处理 → 下载结果CSV
- 在线训练: 上传训练数据集 → 训练新模型 → 保存模型版本

### 8.4 科普教育
- Markdown富文本内容管理 (mavon-editor)
- 视频教程播放 (video.js)
- 评论互动

### 8.5 健康档案管理
- 用户健康档案CRUD（含生活方式字段：运动频率、饮食习惯、吸烟饮酒等）
- 健康指标自查 + 一键保存并AI风险预测（支持9张Python base64图展示）
- 风险快检（ECharts前端4图可视化：仪表盘、雷达图、因素贡献、指标对比）
- DPF 家族遗传风险计算器（公共组件，两页面共用）
- 预测结果存入数据库（风险等级、概率、JSON），支撑健康趋势追踪
- 转诊CSV附带AI预测结果
- AI健康建议（AI不可用时降级为静态模板，后端ConcurrentHashMap缓存相同riskLevel+概率的建议）
- 前端载入历史记录时直接使用数据库已缓存的AI建议，避免重复生成
- 表单草稿自动保存（localStorage，5秒间隔）
- 自查历史可删除（el-popconfirm确认后调用 DELETE /api/health-profile/{id}）
- 诊疗记录跟踪

### 8.6 诊疗档案 (TreatmentRecord.vue)
- **健康趋势图表**: ECharts 折线图展示血糖（空腹/餐后双线）、血压（收缩压/舒张压）、体重/BMI 趋势；数据不足2条时隐藏图表区
- **AI 计划/食谱双栏卡片**: 根据 `diagnosis` 字段是否含"控糖食谱"区分两类 AI 记录，左右并排展示，各最多2条，点击打开侧边弹窗详情
- **就诊/自查记录列表**: 支持就诊(visit)和自查(self_check)两种类型，默认显示2条，可展开查看全部；卡片展示诊断、主诉、血糖/血压/BMI 指标；支持编辑和删除操作
- **健康计划弹窗视图(el-drawer)**:
  - 顶部进度条 + 完成天数统计
  - "全部展开/收起" 按钮控制每周折叠
  - 每天可独立折叠/展开，使用 el-checkbox-group 标记完成状态
  - 每天显示饮食、运动、注意事项三行
  - 7天为一条计划记录
- **控糖食谱弹窗视图(el-drawer)**:
  - 解析食谱文本（`parseRecipeText`），按【早餐】【午餐】【晚餐】【营养总计】分段
  - 每餐食物项按 ` · ` 或 `|` 分隔，智能提取名称、份量、热量
  - 三列布局展示食物：名称(38%，黄底下划线)、份量(25%，琥珀色)、热量(27%，橙色)
  - GI 值说明文本框：自动分级（≤55 低/绿、56-69 中/黄、≥70 高/红）
  - 每餐底部显示 GI 值 + 热量合计标签
  - 回退方案: 无法解析为结构化数据时，逐行渲染 `parsedPlanLines`；含 `|` 的行自动拆分为独立食物卡片
- **新增/编辑记录弹窗**: 表单支持就诊、自查、AI计划三种类型，字段动态切换
- **API**: GET/POST/PUT/DELETE `/api/patient-visit/my`，图表数据 GET `/api/patient-visit/my/chart-data`
- **演示数据**: DEMO_RECORDS 包含6条记录（2就诊+1自查+1健康计划+1就诊+1控糖食谱），DEMO_CHART_DATA 包含14条历史趋势数据

### 8.7 数据分析
- ECharts可视化 (折线图、柱状图、饼图等)
- 个体洞察分析
- 群体组合分析

---

## 9. 常见问题与最佳实践

### 9.1 API Key 配置
AI模型API密钥通过环境变量注入:
```bash
set ZHIPU_API_KEY=your_key_here
set DEEPSEEK_API_KEY=your_key_here
set KIMI_API_KEY=your_key_here
set MIMO_API_KEY=your_key_here
```

### 9.2 Python 环境安装
使用 `python -m pip install` 确保安装到正确的 Python 环境:
```bash
python -m pip install -r requirements.txt
python -m pip install openai
```

### 9.3 前后端类型检查
后端 Result.code 是 String 类型 ("200")，前端判断时必须用 `=== '200'`，不能直接用 `=== 200`。

### 9.4 已知的技术债务
- 多个 Vue 文件中存在 `res.code === 200` 类型比较问题 (应该用 `=== '200'`)
- Python 脚本中的 print 调试信息有些打到 stderr 有些打到 stdout，不够一致
- 前端 `Manage.vue` 使用 `vue-resizable` 但 `package.json` 中未声明依赖
- ~~`/treatment-record` 路由404问题~~ (已修复: manageRoute 和 router.addRoute 移到 if 块外)
- Element Plus `el-checkbox` 的 `label` 属性用作值已废弃（3.0.0），项目已统一改为 `value` 属性

### 9.5 启动顺序
1. 启动 MySQL (3306) 和 Redis (6379)
2. 执行 SQL 初始化脚本
3. 启动后端: `mvn spring-boot:run` (端口 9090)
4. 安装前端依赖: `cd vue && npm install`
5. 启动前端: `npm run dev` (端口 8080, 自动代理到 9090)
