***

name: "oda-project"
description: "Software-ODA125 糖尿病健康管理系统的完整项目知识库。包含后端Spring Boot、前端Vue、Python算法层的全部架构、路由、API、数据库、组件等详细信息。当用户询问项目结构、功能模块、技术细节、业务逻辑时，应调用此技能。"
------------------------------------------------------------------------------------------------------------------------------------------

# Software-ODA125 糖尿病健康管理系统 — 项目知识库

## 1. 项目概览

| 项目   | 信息                                         |
| ---- | ------------------------------------------ |
| 名称   | Software-ODA125 糖尿病健康管理系统                  |
| 版本   | 0.0.1-YUNYI                                |
| 最后更新 | 2026-05-06                                 |
| 架构   | 前后端分离（Spring Boot + Vue 3 + Python算法层）     |
| 数据库  | MySQL 8.0 (数据库名: `dongfang`, 编码: utf8mb4)  |
| 缓存   | Redis (端口 6379, database 0)                |
| 启动类  | `com.oda.springboot.SpringbootApplication` |

***

## 2. 技术栈

### 2.1 后端 (Java)

| 技术                  | 版本/说明                               |
| ------------------- | ----------------------------------- |
| Spring Boot         | 3.3.5                               |
| Java                | 17                                  |
| MyBatis-Plus        | 3.5.9                               |
| MySQL Connector     | 8.0.33                              |
| Redis               | Spring Data Redis + Lettuce         |
| JWT                 | java-jwt 3.10.3 (Auth0)             |
| Swagger             | springdoc-openapi 2.2.0 (Swagger 3) |
| EasyExcel           | 3.3.2                               |
| Hutool              | 5.7.20                              |
| FastJSON            | 1.2.47                              |
| Jython              | 2.7.0 (Python 调用)                   |
| Hibernate Validator | 8.0.1.Final                         |
| Apache POI          | 4.1.2                               |
| Lombok              | 编译器注解                               |
| 构建工具                | Maven (阿里云镜像)                       |

### 2.2 前端 (Vue)

| 技术                      | 版本/说明                                     |
| ----------------------- | ----------------------------------------- |
| Vue                     | 3.4.0 (Composition API, `<script setup>`) |
| Vue Router              | 4.2.0 (createWebHistory)                  |
| Pinia                   | 2.1.7 (状态管理)                              |
| Element Plus            | 2.4.4 (UI组件库)                             |
| @element-plus/icons-vue | 2.3.2 (图标库)                               |
| ECharts                 | 5.6.0 (图表)                                |
| Axios                   | 1.6.0 (HTTP)                              |
| Vite                    | 5.0.0 (构建工具)                              |
| mavon-editor            | 2.10.4 (Markdown编辑器)                      |
| video.js                | 7.18.1 (视频播放)                             |
| vue-video-player        | 5.0.2 (Vue视频播放)                           |
| sockjs-client           | 1.6.1 (WebSocket兼容)                       |

### 2.3 Python 算法层

| 技术           | 版本     |
| ------------ | ------ |
| Python       | 3.12.5 |
| PyTorch      | 2.11.0 |
| scikit-learn | 1.8.0  |
| pandas       | 2.3.0  |
| numpy        | 2.3.0  |
| matplotlib   | 3.10.0 |
| joblib       | 1.5.0  |
| scipy        | 1.15.0 |
| shap         | 0.46.0 |

***

## 3. 项目目录结构

```
Software-ODA125/
├── .gitignore                      # Git忽略规则（约200行）
├── .trae/                          # Trae IDE 配置
│   └── skills/                     # 技能目录
├── pom.xml                         # Maven构建文件
├── requirements.txt                # Python依赖
├── README.md                       # 项目说明文档
├── src/                            # Java后端
│   ├── main/
│   │   ├── java/com/oda/springboot/
│   │   │   ├── SpringbootApplication.java
│   │   │   ├── Main/
│   │   │   ├── common/
│   │   │   ├── config/
│   │   │   ├── controller/         # 24个控制器 + 3个DTO
│   │   │   ├── entity/             # 19个实体类
│   │   │   ├── exception/
│   │   │   ├── mapper/             # 20个Mapper接口
│   │   │   ├── service/
│   │   │   ├── test/
│   │   │   └── utils/
│   │   └── resources/
│   │       ├── mapper/             # 6个MyBatis XML映射文件
│   │       ├── templates/          # 代码生成器模板
│   │       └── application.yml
│   └── test/
├── python/                         # Python算法层
│   ├── train.py
│   ├── predict.py
│   ├── predict_single.py
│   ├── diabetes_chat.py
│   ├── data_analysis.py
│   ├── read_data.py
│   ├── A10.py
│   ├── similar_cases.npy
│   └── Untitled-1.js
├── sql/                            # SQL脚本
│   ├── Software-ODA.sql            # 主初始化脚本（18张表）
│   └── add_python_script_field.sql # 迁移脚本
├── data/                           # 运行时数据目录（已存在）
│   └── models/
│       ├── npy_data/               # SHAP背景数据
│       └── pth_models/             # PyTorch模型文件
├── logs/                           # 训练日志目录（.gitkeep）
└── vue/                            # Vue前端
    ├── public/
    │   ├── config.js               # 运行时配置（导出serverIp）
    │   ├── favicon.ico
    │   └── index.html
    ├── src/
    │   ├── assets/
    │   ├── components/
    │   ├── composables/
    │   ├── router/
    │   ├── store/
    │   ├── styles/
    │   ├── utils/
    │   ├── views/
    │   ├── App.vue
    │   └── main.js
    ├── vite.config.js              # Vite主配置
    ├── vite.config.ts              # Vite TypeScript配置（并存）
    ├── babel.config.js
    ├── index.html
    └── package.json
```

***

## 4. 后端详细结构

### 4.1 分层架构 — Controller → Service (接口+impl) → Mapper

```
src/main/java/com/oda/springboot/
├── SpringbootApplication.java      # 启动类 (@SpringBootApplication, @EnableAsync, @MapperScan)
├── Main/
│   └── Client.java                 # 测试客户端
├── common/                         # 通用工具
│   ├── Constants.java              # 常量
│   ├── Result.java                 # 统一响应泛型类 {code(String), msg, data}
│   └── RoleEnum.java               # 角色枚举
├── config/                         # 配置
│   ├── interceptor/
│   │   └── JwtInterceptor.java     # JWT拦截器
│   ├── AsyncConfiguration.java     # 异步线程池配置
│   ├── AuthAccess.java             # 自定义注解 (跳过JWT验证)
│   ├── CorsConfig.java             # 跨域配置
│   ├── InterceptorConfig.java      # 拦截器注册
│   ├── MybatisPlusConfig.java      # MyBatis-Plus分页插件
│   ├── SwaggerConfig.java          # Swagger 3 文档配置
│   └── WebConfig.java              # 静态资源配置
├── controller/                     # 控制器 (24个)
│   ├── dto/                        # 数据传输对象 (3个)
│   │   ├── SaveAndPredictRequest.java
│   │   ├── UserDTO.java
│   │   └── UserPasswordDTO.java
│   ├── UserController.java
│   ├── RoleController.java
│   ├── MenuController.java
│   ├── IndexController.java
│   ├── DatasetController.java
│   ├── DiabetesController.java     # 智能对话
│   ├── DiabetesEducationController.java
│   ├── DiabetesVideoController.java
│   ├── DetailbordController.java
│   ├── EchartsController.java
│   ├── EducationCommentController.java
│   ├── FileController.java
│   ├── HealthProfileController.java
│   ├── MessageController.java
│   ├── ModelController.java
│   ├── ModelVersionController.java
│   ├── PatientVisitController.java
│   ├── PythonScriptController.java
│   ├── SinglePredictController.java
│   ├── SysConfigController.java
│   ├── TestFileController.java
│   ├── TrainTaskController.java
│   ├── TreatmentRecordController.java
│   └── pythonController.java       # 注意：命名不规范，首字母小写
├── entity/                         # 实体类 (19个)
│   ├── User.java                   # @TableName("sys_user")
│   ├── Role.java                   # @TableName("sys_role")
│   ├── Menu.java                   # @TableName("sys_menu")
│   ├── RoleMenu.java               # @TableName("sys_role_menu")
│   ├── Files.java                  # @TableName("sys_trainfile")
│   ├── TestFiles.java              # @TableName("sys_testfile")
│   ├── Dict.java                   # @TableName("sys_dict")
│   ├── SysConfig.java              # @TableName("sys_config")
│   ├── Message.java                # @TableName("sys_message")
│   ├── DiabetesEducation.java      # 默认转换 → diabetes_education
│   ├── DiabetesVideo.java          # @TableName("diabetes_video")
│   ├── DiabetesRecord.java         # @TableName("diabetes_record")
│   ├── EducationComment.java       # 默认转换 → education_comment
│   ├── HealthProfile.java          # @TableName(value = "user_health_profiles", autoResultMap = true)
│   ├── TreatmentRecord.java        # 无@TableName，XML硬编码sys_treatment_record
│   ├── PatientVisitRecord.java     # @TableName("patient_visit_record")
│   ├── TrainTask.java              # @TableName("sys_train_task")
│   ├── ModelVersion.java           # @TableName("sys_model_version")
│   └── OnlineDate.java             # @TableName("sys_result") — 类名与表名不匹配
├── exception/                      # 异常处理
│   ├── GlobalExceptionHandler.java
│   └── ServiceException.java
├── mapper/                         # MyBatis Mapper接口 (20个)
├── service/                        # 服务层
│   ├── impl/                       # 服务实现 (9个)
│   ├── FileScanService.java        # 文件扫描（直接类，无接口）
│   ├── ModelVersionService.java
│   ├── PythonScriptService.java
│   ├── TrainTaskService.java
│   ├── SysConfigService.java
│   └── 其他接口定义...
├── test/
│   └── TestController.java
└── utils/                          # 工具类
    ├── CodeGenerator.java          # 代码生成器
    ├── TokenUtils.java             # JWT Token工具
    ├── PropertyUtil.java           # 属性读取工具
    └── UsePythonUtils.java         # Python进程调用
```

### 4.2 MyBatis XML映射文件（6个，历史遗留）

| 文件                          | 路径                           | 用途                                   |
| --------------------------- | ---------------------------- | ------------------------------------ |
| UserMapper.xml              | `src/main/resources/mapper/` | 用户查询                                 |
| RoleMapper.xml              | `src/main/resources/mapper/` | 角色查询                                 |
| MenuMapper.xml              | `src/main/resources/mapper/` | 菜单树形查询                               |
| DiabetesEducationMapper.xml | `src/main/resources/mapper/` | 科普文章查询                               |
| EducationCommentMapper.xml  | `src/main/resources/mapper/` | 科普评论查询                               |
| TreatmentRecordMapper.xml   | `src/main/resources/mapper/` | 诊疗档案CRUD（硬编码 `sys_treatment_record`） |

> **注意**：项目规则禁止引入新的 MyBatis XML 映射文件。`TreatmentRecordMapper.xml` 中所有 SQL 均硬编码使用 `sys_treatment_record` 表名。

### 4.3 代码生成器模板

路径：`src/main/resources/templates/`

- controller.java.vm
- entity.java.vm
- mapper.xml.vm
- service.java.vm
- serviceImpl.java.vm

### 4.4 后端配置 (application.yml)

```yaml
server:
  ip: localhost
  port: 9090

spring:
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
  config.import: optional:classpath:secrets/application-secrets.yml

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

# AI API Keys (从环境变量读取)
zhipu.api.key: ${ZHIPU_API_KEY:}
deepseek.api.api-key: ${DEEPSEEK_API_KEY:}
kimi.api.api-key: ${KIMI_API_KEY:}
mimo.api.api-key: ${MIMO_API_KEY:}
mimo-omni.api.api-key: ${MIMO_OMNI_API_KEY:}
```

### 4.5 核心API端点

| 模块   | 方法                  | 端点                                   | 说明       | 认证  |
| ---- | ------------------- | ------------------------------------ | -------- | --- |
| 用户   | POST                | /api/user/login                      | 登录       | 公开  |
| 用户   | POST                | /api/user/register                   | 注册       | 公开  |
| 用户   | GET/POST/PUT/DELETE | /api/user                            | 用户CRUD   | JWT |
| 角色   | GET/POST/PUT/DELETE | /api/role                            | 角色管理CRUD | JWT |
| 菜单   | GET/POST/PUT/DELETE | /api/menu                            | 菜单管理CRUD | JWT |
| 系统   | GET/POST            | /api/system/defaultModel             | 默认模型配置   | JWT |
| 对话   | POST                | /api/diabetes/chat                   | AI智能问答   | 公开  |
| 文件   | POST                | /api/file/upload                     | 文件上传     | JWT |
| 预测   | POST                | /api/singlePredict/run               | 单条预测     | JWT |
| 训练集  | GET/POST/PUT/DELETE | /api/dataset                         | 训练集管理    | JWT |
| 训练任务 | POST                | /api/train-task/start                | 启动训练     | JWT |
| 模型版本 | GET/POST/PUT/DELETE | /api/model-version                   | 模型版本管理   | JWT |
| 健康档案 | POST                | /api/health-profile/save-and-predict | 保存并预测    | JWT |
| 诊疗档案 | GET/POST/PUT/DELETE | /api/treatment-record                | 诊疗档案CRUD | JWT |
| 就诊记录 | GET/POST/PUT/DELETE | /api/patient-visit                   | 就诊记录CRUD | JWT |

### 4.6 JWT认证机制

- **登录流程**：UserController.login() → 验证用户名密码 → 生成JWT Token（使用用户密码作为密钥，有效期8小时）→ 返回 UserDTO(token, role, menus)
- **拦截流程**：JwtInterceptor.preHandle() → 检查 @AuthAccess 注解 → 从header取token → 解码获取userId → 查询用户 → 密码验签
- **前端注入**：request.js 请求拦截器从 localStorage 读取 `user.token`，设置 header `token`
- **响应拦截**：code='401' 时自动跳转 /login
- **白名单路径**：/api/diabetes/chat, /api/system/defaultModel (GET), /user/login, /user/register
- **el-upload组件认证**：`el-upload` 使用原生 HTTP 请求，不经过 request.js 拦截器，必须手动携带 token：
  ```javascript
  // 在 data() 中添加 uploadHeaders
  const userStr = localStorage.getItem('user') || sessionStorage.getItem('user')
  let token = ''
  if (userStr) {
    try { token = JSON.parse(userStr).token || '' } catch (e) {}
  }
  return {
    uploadHeaders: { token: token }
  }
  // 模板中使用 :headers="uploadHeaders"
  ```
- **后端上传接口认证**：上传接口从 JWT token 解析 userId（而非从 Redis 获取）：
  ```java
  String token = request.getHeader("token");
  String userId = null;
  if (StrUtil.isNotBlank(token)) {
      try {
          userId = JWT.decode(token).getAudience().get(0);
      } catch (Exception e) {
          return Result.error("401", "身份凭证异常，请重新登录");
      }
  }
  ```

### 4.7 统一响应格式

```java
public class Result<T> {
    private String code;   // "200"成功, "500"系统错误, "401"权限不足
    private String msg;    // 提示信息
    private T data;        // 泛型数据
}
```

> **重要**：`Result.code` 是 **String** 类型，前端判断时必须用 `=== '200'`，不能用 `=== 200`。

***

## 5. 前端详细结构

### 5.1 入口与配置

**main.js**：引入 Element Plus、ECharts、Pinia、全局样式、路由，挂载到 `#app`

**vite.config.js**（主配置）：

- 开发服务器端口: `8080`
- 代理: `/api` → `http://localhost:9090`
- 路径别名: `@` → `./src`, `vue` → `vue/dist/vue.esm-bundler.js`（启用运行时编译）
- 代码分割 (manualChunks): vendor (vue+vue-router+pinia) / element-plus / echarts / axios

> 注意：项目中同时存在 `vite.config.js` 和 `vite.config.ts`，以 `.js` 为准。

### 5.2 目录结构

```
vue/src/
├── App.vue                        # 根组件
├── main.js                        # 入口文件
├── vite-env.d.ts                  # Vite类型声明
├── assets/                        # 静态资源
│   ├── gloable.css                # 全局CSS（注意拼写：gloable而非global）
│   ├── logo.png                   # 项目Logo
│   └── 1.jpeg, 2.jpeg, 3.jpeg    # 页面配图
├── components/                    # 公共组件 (4个)
│   ├── Aside.vue                  # 侧边栏导航
│   ├── Header.vue                 # 顶部导航栏
│   ├── DpfCalculator.vue          # DPF家族遗传风险计算器
│   └── HelloWorld.vue             # 占位/示例组件
├── composables/                   # 组合式函数 (4个)
│   ├── useDraftPersistence.js     # 表单草稿自动保存
│   ├── useGlobalSound.js          # 全局音效管理
│   ├── useHealthValidator.js      # 健康数据校验
│   └── usePrediction.js           # 预测逻辑封装
├── router/
│   └── index.js                   # 路由配置 (动态路由)
├── store/
│   ├── index.js                   # useMainStore (路径管理、登出)
│   └── healthStore.js             # useHealthStore (健康数据核心Store)
├── styles/                        # SCSS样式 (8个文件)
│   ├── index.scss                 # SCSS入口
│   ├── variables.scss             # SCSS变量定义
│   ├── mixin.scss                 # SCSS Mixin
│   ├── element-ui.scss            # Element Plus样式覆盖
│   ├── element-variables.scss     # Element Plus主题变量
│   ├── sidebar.scss               # 侧边栏样式
│   ├── btn.scss                   # 按钮样式
│   └── transition.scss            # 过渡动画样式
├── utils/                         # 工具
│   ├── request.js                 # Axios请求封装（自动注入token、统一错误处理）
│   └── cacheHelper.js             # localStorage封装（get/set/remove/clearAll/clearAllLoginState）
└── views/                         # 32个页面组件
```

### 5.3 页面组件清单

| #  | 组件名                 | 文件名                     | 功能说明                      |
| -- | ------------------- | ----------------------- | ------------------------- |
| 1  | Manage              | Manage.vue              | 主布局（侧边栏+顶栏+内容区）           |
| 2  | Home                | Home.vue                | 首页/仪表盘                    |
| 3  | Login               | Login.vue               | 登录页                       |
| 4  | Register            | Register.vue            | 注册页                       |
| 5  | User                | User.vue                | 用户管理                      |
| 6  | Role                | Role.vue                | 角色管理                      |
| 7  | Menu                | Menu.vue                | 菜单管理                      |
| 8  | Person              | Person.vue              | 个人信息                      |
| 9  | Password            | Password.vue            | 修改密码                      |
| 10 | Dashbord            | Dashbord.vue            | 数据报表                      |
| 11 | Detailbord          | Detailbord.vue          | 详细报表                      |
| 12 | DiabetesChat        | DiabetesChat.vue        | 智能问答（含AI模型切换、打字机效果、上下文注入） |
| 13 | DiabetesEducation   | DiabetesEducation.vue   | 糖尿病科普（含视频播放功能）            |
| 14 | DoctorWorkbench     | DoctorWorkbench.vue     | 诊断工作台                     |
| 15 | GroupAnalysis       | GroupAnalysis.vue       | 组合分析                      |
| 16 | IndividualInsight   | IndividualInsight.vue   | 个体洞察                      |
| 17 | DataCollection      | DataCollection.vue      | 数据采集                      |
| 18 | DataTest            | DataTest.vue            | 采集日志                      |
| 19 | DatasetManagement   | DatasetManagement.vue   | 训练集管理                     |
| 20 | ModelManagement     | ModelManagement.vue     | 模型管理                      |
| 21 | OnlineTraining      | OnlineTraining.vue      | 在线模型训练                    |
| 22 | PredictionWorkbench | PredictionWorkbench.vue | 预测工作台                     |
| 23 | TestFile            | TestFile.vue            | 在线预测                      |
| 24 | HealthProfileView   | HealthProfileView\.vue  | 健康管理中心                    |
| 25 | HealthCheck         | HealthCheck.vue         | 风险快检（ECharts可视化）          |
| 26 | TreatmentRecord     | TreatmentRecord.vue     | 诊疗档案（含Excel导入导出）          |
| 27 | Send                | Send.vue                | 故障报修                      |
| 28 | List                | List.vue                | 报修详情                      |
| 29 | OMlist              | OMlist.vue              | 运维详情                      |
| 30 | OMsend              | OMsend.vue              | 信息回执                      |
| 31 | About               | About.vue               | 关于页面                      |
| 32 | 404                 | 404.vue                 | 404页面                     |

> **注意**：没有独立的 `DiabetesVideo.vue`，视频功能嵌入在 `DiabetesEducation.vue` 中实现。

### 5.4 动态路由机制

```
路由模式: createWebHistory()

固定路由: /login, /register, /404 (白名单, 直接放行)

动态路由流程:
1. 用户登录成功 → 后端返回菜单树 → 存入 localStorage('menus')
2. setRoutes() 从 localStorage 读取 menus
3. 创建 Manage 路由作为根路由 (/), children 包含固定子路由
4. 根据菜单映射追加动态子路由
5. router.addRoute() 动态注册
```

**mapPagePath 映射表 (sys\_menu.page\_path → Vue组件)**:

| page\_path          | Vue组件                   |
| ------------------- | ----------------------- |
| Home                | Home.vue                |
| User                | User.vue                |
| Role                | Role.vue                |
| Menu                | Menu.vue                |
| DatasetManagement   | DatasetManagement.vue   |
| ModelManagement     | ModelManagement.vue     |
| OnlineTraining      | OnlineTraining.vue      |
| HealthProfileView   | HealthProfileView\.vue  |
| HealthCheck         | HealthCheck.vue         |
| DiabetesChat        | DiabetesChat.vue        |
| DiabetesEducation   | DiabetesEducation.vue   |
| TreatmentRecord     | TreatmentRecord.vue     |
| DoctorWorkbench     | DoctorWorkbench.vue     |
| PredictionWorkbench | PredictionWorkbench.vue |
| DataCollection      | DataCollection.vue      |
| DataTest            | DataTest.vue            |
| GroupAnalysis       | GroupAnalysis.vue       |
| IndividualInsight   | IndividualInsight.vue   |
| Dashbord            | Dashbord.vue            |
| Detailbord          | Detailbord.vue          |
| TestFile            | TestFile.vue            |
| Send                | Send.vue                |
| List                | List.vue                |
| OMlist              | OMlist.vue              |
| OMsend              | OMsend.vue              |
| About               | About.vue               |

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

### 5.7 状态管理 (Pinia)

**useMainStore** (`store/index.js`) — 使用 Options API 风格：

- State: `currentPathName` (当前路径名)
- Actions: `setPath()` (从缓存读取), `logout()` (清除登录状态), `clearAllCache()` (清除所有缓存)

**useHealthStore** (`store/healthStore.js`) — 使用 Composition API 风格：

- 身体数据 ref: age, height, weight, glucose, bloodPressure, insulin, skinThickness, pregnancies, diabetesPedigreeFunction, gender, symptoms, exerciseFrequency, dietHabit, smoking, drinking
- 计算属性: bmi（自动计算）、hasPrediction
- 预测结果: riskLevel, riskProbability, predictionId, aiAdvice, chartsData, featureImportance, featureNames, percentiles, similarCases, confidenceInterval, predictionTime, adviceData
- 方法: loadFromDraft(), saveToDraft(), clearDraft(), setPredictionResult(), resetAll(), toFeatures(), toSavePayload()

### 5.8 Composables（组合式函数）

| 文件                     | 功能                          |
| ---------------------- | --------------------------- |
| useDraftPersistence.js | 表单草稿自动保存/恢复（基于localStorage） |
| useGlobalSound.js      | 全局音效管理                      |
| useHealthValidator.js  | 健康数据校验逻辑                    |
| usePrediction.js       | 预测逻辑封装                      |

***

## 6. 数据库

### 6.1 数据库配置

- 数据库名: `dongfang`
- 字符集: `utf8mb4`
- 初始化脚本: `sql/Software-ODA.sql`

### 6.2 核心表清单（共19张）

**SQL初始化脚本定义（18张）：**

| #  | 表名                     | 说明                       |
| -- | ---------------------- | ------------------------ |
| 1  | sys\_user              | 用户表                      |
| 2  | sys\_role              | 角色表                      |
| 3  | sys\_menu              | 菜单表（树形结构，parent\_id关联）   |
| 4  | sys\_role\_menu        | 角色-菜单关联表                 |
| 5  | sys\_dict              | 数据字典（图标配置）               |
| 6  | sys\_message           | 消息/故障报修表                 |
| 7  | sys\_trainfile         | 训练文件表                    |
| 8  | sys\_train\_task       | 训练任务表（含python\_script字段） |
| 9  | sys\_model\_version    | 模型版本表                    |
| 10 | sys\_testfile          | 测试文件表                    |
| 11 | sys\_config            | 系统配置表                    |
| 12 | sys\_result            | 在线数据统计表（实体类：OnlineDate）  |
| 13 | diabetes\_education    | 糖尿病科普文章                  |
| 14 | diabetes\_video        | 糖尿病视频                    |
| 15 | diabetes\_record       | 糖尿病记录（Pima Indians数据集）   |
| 16 | education\_comment     | 科普留言                     |
| 17 | user\_health\_profiles | 健康档案                     |
| 18 | patient\_visit\_record | 患者就诊记录                   |

**数据库运行时存在（1张，未在初始化SQL中定义）：**

| #  | 表名                     | 说明                                  |
| -- | ---------------------- | ----------------------------------- |
| 19 | sys\_treatment\_record | 诊疗档案表（实体类：TreatmentRecord，XML硬编码表名） |

### 6.3 角色数据 (sys\_role)

| ID | 名称  | 角色编码              | 说明      |
| -- | --- | ----------------- | ------- |
| 1  | 管理员 | ROLE\_ADMIN       | 系统管理员   |
| 2  | 用户  | ROLE\_WORKER      | 普通用户    |
| 3  | 运维  | ROLE\_MAINTENANCE | 运维人员    |
| 16 | 测试1 | 0001              | 系统增删测试项 |
| 17 | 测试2 | 0002              | 系统增删测试项 |
| 18 | 医生  | ROLE\_DOCTOR      | 医生角色    |

### 6.4 菜单模块 (8大模块, 32条菜单记录)

1. **主页** — 首页仪表盘
2. **系统管理** — 用户管理、角色管理、菜单管理
3. **AI模型中心** — 训练集管理、模型管理、在线模型训练
4. **用户服务** — 风险快检、健康管理中心、智能问答、诊疗档案、糖尿病科普
5. **诊断员服务** — 组合分析、个体洞察、数据采集、预测工作台、诊断工作台
6. **糖尿病预测中心** — 采集日志、在线预测、数据报表、详细报表
7. **运维中心** — 故障报修、报修详情、运维详情、信息回执
8. **关于**

***

## 7. Python 模块

### 7.1 文件清单

```python
python/
├── train.py               # PyTorch模型训练
├── predict.py             # 批量预测 (CSV/Excel → JSON)
├── predict_single.py      # 单条预测 (功能最丰富)
├── diabetes_chat.py       # AI 聊天机器人 (OpenAI兼容)
├── data_analysis.py       # 数据分析 (含旧版TensorFlow逻辑)
├── read_data.py           # 数据读取工具函数
├── A10.py                 # 辅助脚本
├── similar_cases.npy      # 相似病例历史数据
└── Untitled-1.js          # 临时文件
```

### 7.2 模型结构 (train.py / predict\_single.py)

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

### 7.3 predict\_single.py 增强功能

- MC Dropout 不确定性估计
- SHAP 特征重要性解释
- 可视化图表（特征重要性图、风险仪表盘、雷达图、瀑布图）
- 百分位计算（用户指标在人群中的位置）
- 相似病例分析（基于余弦相似度匹配 `similar_cases.npy`）

### 7.4 AI 聊天机器人 (diabetes\_chat.py)

支持的模型:

- 智谱 (GLM): glm-4-flash, glm-4.7-flash
- DeepSeek
- Kimi (Moonshot)
- 小米 MiMo: mimo-v2.5-pro, mimo-v2-flash, mimo-v2-omni

系统提示: 强制扮演糖尿病医生，不承认是AI，用短句回答，与糖尿病无关问题则拒绝回答。

***

## 8. 核心业务功能

### 8.1 用户系统

- 注册/登录 (JWT认证)
- 基于RBAC的权限控制 (角色+菜单)
- 个人信息管理 + 偏好设置

### 8.2 AI智能对话 (DiabetesChat.vue)

- 管理员可选择切换多种AI模型
- 打字机效果展示回答
- 上下文注入 (从URL参数读取检测结果)
- 附加功能: 报告解读 + 健康计划生成

### 8.3 糖尿病预测

- 单条预测: 输入8个特征 → Python神经网络 → 返回风险概率 + SHAP解释
- 批量预测: 上传CSV/Excel文件 → Python批量处理 → 下载结果JSON
- 在线训练: 上传训练数据集 → 训练新模型 → 保存模型版本

### 8.4 健康档案管理

- 用户健康档案CRUD（含生活方式字段：运动频率、饮食习惯、吸烟、饮酒等）
- 健康指标自查 + 一键保存并AI风险预测
- 风险快检（ECharts可视化）
- DPF家族遗传风险计算器（DpfCalculator.vue）
- 表单草稿自动保存（useDraftPersistence.js）

### 8.5 诊疗档案 (TreatmentRecord.vue)

- 健康趋势图表 (ECharts)
- AI计划/食谱双栏卡片
- 就诊/自查记录列表
- 支持编辑和删除
- Excel批量导入导出（EasyExcel）

***

## 9. 常见问题与最佳实践

### 9.1 API Key 配置

AI模型API密钥通过环境变量注入:

```bash
set ZHIPU_API_KEY=your_key_here
set DEEPSEEK_API_KEY=your_key_here
set KIMI_API_KEY=your_key_here
set MIMO_API_KEY=your_key_here
```

### 9.2 前后端类型检查

- 后端 `Result.code` 是 **String** 类型 (`"200"`)
- 前端判断时必须用 `=== '200'`, 不能用 `=== 200`

### 9.3 项目规范

- Vue 组件必须使用 `<script setup>` Composition API
- 所有 HTTP 请求必须通过 `@/utils/request.js` 封装
- Element Plus 图标使用 `@element-plus/icons-vue` 包
- 状态管理使用 Pinia（禁止 Vuex）
- 路由使用 `createWebHistory` 模式（禁止 Hash 模式）

### 9.4 启动顺序

1. 启动 MySQL (3306) 和 Redis (6379)
2. 执行 `sql/Software-ODA.sql` 初始化数据库
3. 手动创建 `sys_treatment_record` 表（如不存在）
4. 执行 `sql/add_python_script_field.sql` 迁移脚本（如需要）
5. 启动后端: `mvn spring-boot:run` (端口 9090)
6. 进入前端: `cd vue` → `npm install` → `npm run dev` (端口 8080)

### 9.5 运行时目录

以下目录在 application.yml 中已配置，运行时按需自动创建：

- `./data/` — 训练数据上传（已存在）
- `./OnlinePredict/` — 预测数据上传
- `./json/` — JSON结果下载
- `./data/models/` — 模型文件（已存在，含 npy\_data/ 和 pth\_models/）
- `./logs/` — 训练日志（已存在）
- `./files/` — 通用文件上传（含 avatar/ 和 common/ 子目录）

### 9.6 .gitignore 关键排除项

- 模型文件: `.pth`, `.pkl`, `.h5`, `.onnx`, `.bin`, `.ckpt`
- 数据集: `.csv`, `.xlsx`, `.xls`
- JSON输出: 全部 `.json`
- 密钥文件: `secrets/`, `application-secrets.yml`
- 构建产物: `**/target/`, `**/dist/`, `node_modules/`

