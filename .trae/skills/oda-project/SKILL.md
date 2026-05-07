---
name: "oda-project"
description: "Software-ODA125 糖尿病健康管理系统的完整项目知识库。包含后端Spring Boot、前端Vue、Python算法层的全部架构、路由、API、数据库、组件等详细信息。当用户询问项目结构、功能模块、技术细节、业务逻辑、API接口、数据库表、组件关系时，应调用此技能。"
---

# Software-ODA125 糖尿病健康管理系统 — 项目知识库

> **最后更新**：2026-05-07  
> **最新变更**：测试集路径迁移至 data/test/、JSON 输出路径迁移至 data/json/、清理过渡性 SQL 迁移文件

## 1. 项目概览

| 项目 | 信息 |
|------|------|
| 名称 | Software-ODA125 糖尿病健康管理系统 |
| 版本 | 0.0.1-YUNYI |
| 最后更新 | 2026-05-07（测试集路径迁移 + JSON路径迁移） |
| 架构 | 前后端分离（Spring Boot + Vue 3 + Python算法层） |
| 数据库 | MySQL 8.0 (数据库名: `dongfang`, 编码: utf8mb4) |
| 缓存 | Redis (端口 6379, database 0) |
| 启动类 | `com.oda.springboot.SpringbootApplication` |

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
| FastJSON | 1.2.47 |
| Jython | 2.7.0 (Python 调用) |
| Hibernate Validator | 8.0.1.Final |
| Apache POI | 4.1.2 |
| Lombok | 编译器注解 |
| 构建工具 | Maven (阿里云镜像) |

### 2.2 前端 (Vue)

| 技术 | 版本/说明 |
|------|----------|
| Vue | 3.4.0 (Composition API, `<script setup>`) |
| Vue Router | 4.2.0 (createWebHistory) |
| Pinia | 2.1.7 (状态管理) |
| Element Plus | 2.4.4 (UI组件库) |
| @element-plus/icons-vue | 2.3.2 (图标库) |
| ECharts | 5.6.0 (图表) |
| Axios | 1.6.0 (HTTP) |
| Vite | 5.0.0 (构建工具) |
| mavon-editor | 2.10.4 (Markdown编辑器) |
| video.js | 7.18.1 (视频播放) |
| vue-video-player | 5.0.2 (Vue视频播放) |
| sockjs-client | 1.6.1 (WebSocket兼容) |

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

---

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
│   └── Software-ODA.sql            # 主初始化脚本（18张表，已整合所有迁移）
├── data/                           # 运行时数据目录（已存在）
│   ├── json/                       # JSON预测结果输出
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

---

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
│   ├── impl/                       # 服务实现 (10个)
│   │   ├── DiabetesEducationServiceImpl.java
│   │   ├── DiabetesVideoServiceImpl.java
│   │   ├── EducationCommentServiceImpl.java
│   │   ├── HealthProfileServiceImpl.java
│   │   ├── MenuServiceImpl.java
│   │   ├── MessageServiceImpl.java
│   │   ├── PatientVisitServiceImpl.java
│   │   ├── RoleServiceImpl.java
│   │   ├── TreatmentRecordServiceImpl.java
│   │   └── UserServiceImpl.java
│   ├── IDiabetesEducationService.java   # 科普文章服务接口
│   ├── IDiabetesVideoService.java       # 视频服务接口
│   ├── IEducationCommentService.java    # 评论服务接口
│   ├── IHealthProfileService.java       # 健康档案服务接口
│   ├── IMenuService.java                # 菜单服务接口
│   ├── IPatientVisitService.java        # 就诊记录服务接口
│   ├── IRoleService.java                # 角色服务接口
│   ├── ITreatmentRecordService.java     # 诊疗档案服务接口
│   ├── IUserService.java                # 用户服务接口
│   ├── MessageService.java              # 消息服务接口（注意：无 I 前缀，命名不一致）
│   ├── FileScanService.java             # 直接类：扫描data/train目录CSV/XLSX文件，自动创建sys_trainfile记录
│   ├── ModelVersionService.java         # 直接类：模型版本CRUD、激活/停用（同模型名互斥激活）
│   ├── PythonScriptService.java         # 直接类：扫描python/目录训练脚本文件
│   ├── TrainTaskService.java            # 直接类：创建训练任务、@Async异步执行、训练完成后自动注册模型版本
│   └── SysConfigService.java            # 直接类：获取/设置默认AI模型配置（默认glm-4-flash）
├── test/
│   └── TestController.java
└── utils/                          # 工具类
    ├── CodeGenerator.java          # 代码生成器
    ├── TokenUtils.java             # JWT Token工具
    ├── PropertyUtil.java           # 属性读取工具（getJsonDownload()读取data/json/路径）
    └── UsePythonUtils.java         # Python进程调用
```

### 4.2 MyBatis XML映射文件（6个，历史遗留）

| 文件 | 路径 | 用途 |
|------|------|------|
| UserMapper.xml | `src/main/resources/mapper/` | 用户查询 |
| RoleMapper.xml | `src/main/resources/mapper/` | 角色查询 |
| MenuMapper.xml | `src/main/resources/mapper/` | 菜单树形查询 |
| DiabetesEducationMapper.xml | `src/main/resources/mapper/` | 科普文章查询 |
| EducationCommentMapper.xml | `src/main/resources/mapper/` | 科普评论查询 |
| TreatmentRecordMapper.xml | `src/main/resources/mapper/` | 诊疗档案CRUD（硬编码 `sys_treatment_record`） |

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
  pythonDataTestUpload.path: ${base.path}/data/test/
  JsonDownload.path: ${base.path}/data/json/
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

| 模块 | 方法 | 端点 | 说明 | 认证 |
|------|------|------|------|------|
| 用户 | POST | /api/user/login | 登录 | 公开 |
| 用户 | POST | /api/user/register | 注册 | 公开 |
| 用户 | GET/POST/PUT/DELETE | /api/user | 用户CRUD | JWT |
| 角色 | GET/POST/PUT/DELETE | /api/role | 角色管理CRUD | JWT |
| 菜单 | GET/POST/PUT/DELETE | /api/menu | 菜单管理CRUD | JWT |
| 系统 | GET/POST | /api/system/defaultModel | 默认模型配置 | JWT |
| 对话 | POST | /api/diabetes/chat | AI智能问答 | 公开 |
| 文件 | POST | /api/file/upload | 文件上传 | JWT |
| 预测 | POST | /api/singlePredict/run | 单条预测 | JWT |
| 训练集 | GET | /api/dataset/list | 分页查询训练集（支持keyword/category筛选） | JWT |
| 训练集 | GET | /api/dataset/{id} | 获取训练集详情 | JWT |
| 训练集 | POST | /api/dataset/upload | 上传训练集（FormData: file + category） | JWT |
| 训练集 | POST | /api/dataset/scan | 扫描data/train目录自动入库 | JWT |
| 训练集 | DELETE | /api/dataset/{id} | 删除训练集（逻辑删除） | JWT |
| 训练集 | GET | /api/dataset/all | 获取所有训练集（下拉选择用） | JWT |
| 训练任务 | POST | /api/train-task/start | 启动训练 | JWT |
| 模型版本 | GET/POST/PUT/DELETE | /api/model-version | 模型版本管理 | JWT |
| 健康档案 | POST | /api/health-profile/save-and-predict | 保存并预测 | JWT |
| 诊疗档案 | GET/POST/PUT/DELETE | /api/treatment-record | 诊疗档案CRUD | JWT |
| 就诊记录 | GET/POST/PUT/DELETE | /api/patient-visit | 就诊记录CRUD | JWT |

### 4.6 JWT认证机制

- **登录流程**：UserController.login() → 验证用户名密码 → 生成JWT Token（使用用户密码作为密钥，有效期7天）→ 返回 UserDTO(token, role, menus)
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
- **上传接口认证**：
  - **推荐使用 `/api/dataset/upload`**：新接口，使用 JWT 认证，支持 category 参数
  - **旧接口 `/python/upload`**：依赖 Redis userId，存在多用户冲突风险，不推荐使用
  - 后端上传接口从 JWT token 解析 userId（而非从 Redis 获取）：
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

---

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
│   ├── Aside.vue                  # 侧边栏导航（getMenuIndex路由映射、getIconComponent图标映射、菜单树渲染）
│   ├── Header.vue                 # 顶部导航栏（折叠按钮、面包屑、用户头像下拉、退出登录含resetRouter）
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

| # | 组件名 | 文件名 | 功能说明 |
|---|--------|--------|---------|
| 1 | Manage | Manage.vue | 主布局（侧边栏+顶栏+内容区） |
| 2 | Home | Home.vue | 首页/仪表盘 |
| 3 | Login | Login.vue | 登录页 |
| 4 | Register | Register.vue | 注册页 |
| 5 | User | User.vue | 用户管理 |
| 6 | Role | Role.vue | 角色管理 |
| 7 | Menu | Menu.vue | 菜单管理 |
| 8 | Person | Person.vue | 个人信息 |
| 9 | Password | Password.vue | 修改密码 |
| 10 | Dashbord | Dashbord.vue | 数据报表 |
| 11 | Detailbord | Detailbord.vue | 详细报表 |
| 12 | DiabetesChat | DiabetesChat.vue | 智能问答（含AI模型切换、打字机效果、上下文注入） |
| 13 | DiabetesEducation | DiabetesEducation.vue | 糖尿病科普（含视频播放功能） |
| 14 | DoctorWorkbench | DoctorWorkbench.vue | 诊断工作台 |
| 15 | GroupAnalysis | GroupAnalysis.vue | 组合分析 |
| 16 | IndividualInsight | IndividualInsight.vue | 个体洞察 |
| 17 | DataCollection | DataCollection.vue | 数据采集 |
| 18 | DataTest | DataTest.vue | 采集日志 |
| 19 | DatasetManagement | DatasetManagement.vue | 训练集管理 |
| 20 | ModelManagement | ModelManagement.vue | 模型管理 |
| 21 | OnlineTraining | OnlineTraining.vue | 在线模型训练 |
| 22 | PredictionWorkbench | PredictionWorkbench.vue | 预测工作台 |
| 23 | TestFile | TestFile.vue | 在线预测 |
| 24 | HealthProfileView | HealthProfileView.vue | 健康管理中心 |
| 25 | HealthCheck | HealthCheck.vue | 风险快检（ECharts可视化） |
| 26 | TreatmentRecord | TreatmentRecord.vue | 诊疗档案（含Excel导入导出） |
| 27 | Send | Send.vue | 故障报修 |
| 28 | List | List.vue | 报修详情 |
| 29 | OMlist | OMlist.vue | 运维详情 |
| 30 | OMsend | OMsend.vue | 信息回执 |
| 31 | About | About.vue | 关于页面 |
| 32 | 404 | 404.vue | 404页面 |

> **注意**：没有独立的 `DiabetesVideo.vue`，视频功能嵌入在 `DiabetesEducation.vue` 中实现。

### 5.4 动态路由机制

```
路由模式: createWebHistory()

固定路由: /login, /register, /404 (白名单, 直接放行)
```

**动态路由完整流程**:

```
用户登录成功
  ↓
后端返回菜单树 (根据角色权限筛选)
  ↓
存入 localStorage('menus')
  ↓
调用 setRoutes()
  ↓
① 检查并移除已有的 'Manage' 路由 (router.removeRoute)
  ↓
② 创建 Manage 父路由 (/), children 包含 3 个固定子路由
  ↓
③ 初始化 existingPaths = Set(['person', 'password', 'treatment-record'])
  ↓
④ 遍历菜单树，调用 buildChildRoute() 生成动态子路由
  ↓
⑤ router.addRoute(manageRoute) 注册完整路由树
```

**`buildChildRoute(menuItem)` 核心逻辑**:

```javascript
const buildChildRoute = (menuItem) => {
  if (!menuItem.path && !menuItem.pagePath) return null
  // 1. 确定组件名：优先 pagePath，否则从 path 去掉前导 /
  let componentName = menuItem.pagePath || menuItem.path.replace(/^\//, '')
  // 2. 通过 mapPagePath 查映射表
  if (mapPagePath[componentName]) {
    componentName = mapPagePath[componentName]
  } else {
    // 3. 自动转换：首字母大写 + kebab-case → camelCase
    componentName = componentName.charAt(0).toUpperCase() + componentName.slice(1)
    componentName = componentName.replace(/-([a-z])/g, (g) => g[1].toUpperCase())
  }
  // 4. 路径统一化：去掉前导 /，转小写（确保为相对路径格式）
  const routePath = menuItem.path
    ? menuItem.path.replace(/^\//, '').toLowerCase()
    : menuItem.pagePath.toLowerCase()
  // 5. 去重检查
  if (existingPaths.has(routePath)) return null
  existingPaths.add(routePath)
  return { path: routePath, name: menuItem.name, component: () => import(`../views/${componentName}.vue`) }
}
```

**`existingPaths` 去重机制**:
- 初始值：`Set(['person', 'password', 'treatment-record'])`（3 个固定子路由路径，相对路径格式）
- 每次成功生成路由后 `existingPaths.add(routePath)`
- 防止同一路径被重复注册导致 Vue Router 警告或注册失败
- **路径必须为相对路径格式**（无前导 `/`），否则与初始值格式不一致会导致去重失效

**`beforeEach` 路由守卫**:

```javascript
router.beforeEach((to, from, next) => {
  // 1. 白名单放行：/login, /register, /404
  if (to.path === '/login' || to.path === '/register' || to.path === '/404') {
    return next()
  }
  // 2. 更新当前路径名到缓存
  if (to.name) {
    CacheHelper.set('currentPathName', to.name)
    const store = useMainStore()
    store.setPath()
  }
  // 3. 未匹配到路由时（如页面刷新）
  if (!to.matched.length) {
    const storeMenus = CacheHelper.get('menus')
    if (storeMenus) {
      // 3a. pendingPaths 防无限循环：同一路径已 pending 则跳 404
      if (pendingPaths.has(to.fullPath)) {
        pendingPaths.delete(to.fullPath)
        return next("/404")
      }
      // 3b. 标记路径为 pending，重建路由后重新导航
      pendingPaths.add(to.fullPath)
      setRoutes()
      return next(to.fullPath)
    } else {
      // 3c. 无菜单数据，跳转登录
      return next("/login")
    }
  } else {
    // 4. 正常匹配，清理 pending 标记
    pendingPaths.delete(to.fullPath)
    next()
  }
})
```

**`resetRouter()` — Vue Router 4 实现**:

```javascript
export const resetRouter = () => {
  const currentRoutes = router.getRoutes()
  currentRoutes.forEach(route => {
    if (route.name && route.name !== 'Login' && route.name !== 'Register' && route.name !== '404') {
      router.removeRoute(route.name)
    }
  })
}
```

> **重要**：Vue Router 4 中 `router.matcher` 不可赋值，不能使用 Vue Router 3 的 `resetRouter` 写法。必须使用 `router.removeRoute()` 逐个移除。

**Aside.vue 菜单索引 (`getMenuIndex`)**:

```javascript
const getMenuIndex = (menuItem) => {
  if (menuItem.path) {
    // 优先使用 path，确保以 / 开头并转小写
    return menuItem.path.startsWith('/') ? menuItem.path.toLowerCase() : '/' + menuItem.path.toLowerCase()
  }
  // 否则取 pagePath（兼容 page_path 字段名），加 / 前缀并转小写
  let pagePathVal = menuItem.pagePath || menuItem.page_path
  return pagePathVal ? '/' + pagePathVal.toLowerCase() : ''
}
```

> 与 `el-menu` 的 `router` 属性配合：菜单项的 `index` 就是路由路径，点击自动导航。

**`mapPagePath` 完整映射表 (50 条)**:

| key | 映射组件 | 说明 |
|-----|---------|------|
| `dataset-management` | DatasetManagement | kebab-case 别名 |
| `DatasetManagement` | DatasetManagement | pagePath 直接映射 |
| `onlineupdate` | DatasetManagement | 旧版别名 |
| `online-training` | OnlineTraining | kebab-case 别名 |
| `OnlineTraining` | OnlineTraining | pagePath 直接映射 |
| `file` | OnlineTraining | 简短别名 |
| `model-manager` | ModelManagement | kebab-case 别名 |
| `health-profile` | HealthProfileView | kebab-case 别名 |
| `risk-quick` | HealthCheck | kebab-case 别名 |
| `chat` | DiabetesChat | 简短别名 |
| `ai-chat` | DiabetesChat | 别名 |
| `diabetes-education` | DiabetesEducation | kebab-case 别名 |
| `diabetes-video` | DiabetesEducation | 视频功能复用科普页 |
| `menu` | Menu | — |
| `user` | User | — |
| `role` | Role | — |
| `dashbord` | Dashbord | 拼写保留 |
| `doctor-workbench` | DoctorWorkbench | — |
| `prediction-workbench` | PredictionWorkbench | — |
| `data-collection` | DataCollection | — |
| `data-test` | DataTest | — |
| `treatment-record` | TreatmentRecord | — |
| `individual-insight` | IndividualInsight | — |
| `group-analysis` | GroupAnalysis | — |
| `detailbord` | Detailbord | 拼写保留 |
| `omlist` | OMlist | — |
| `omsend` | OMsend | — |
| `test-file` | TestFile | — |
| `collection-log` | DataCollection | 英文别名 |
| `采集日志` | DataCollection | 中文别名 |
| `online-predict` | PredictionWorkbench | 英文别名 |
| `在线预测` | PredictionWorkbench | 中文别名 |
| `data-report` | Dashbord | 英文别名 |
| `数据报表` | Dashbord | 中文别名 |
| `detail-report` | Detailbord | 英文别名 |
| `详细报表` | Detailbord | 中文别名 |
| `fault-report` | OMsend | 英文别名 |
| `故障报修` | OMsend | 中文别名 |
| `report-detail` | OMlist | 英文别名 |
| `报修详情` | OMlist | 中文别名 |
| `operation-detail` | Detailbord | 英文别名 |
| `运维详情` | Detailbord | 中文别名 |
| `info-receipt` | Detailbord | 英文别名 |
| `信息回执` | Detailbord | 中文别名 |

### 5.5 HTTP 请求封装 (request.js)

```javascript
baseURL: http://{host}:9090  // host = window.location.hostname || 'localhost'
超时: 60000ms
请求拦截器: 
  - 设置 Content-Type（FormData 请求除外，由浏览器自动设置 multipart/form-data）
  - 注入 token header（从 localStorage 读取 user.token）
响应拦截器:
  - blob 响应 (responseType === 'blob') 直接返回（文件下载场景）
  - JSON 字符串安全解析
  - 401 → 跳转 /login (白名单路径 /user/register, /user/login, /user/checkUsername 除外)
  - 返回 response.data 给调用方
```

**⚠️ 文件上传注意事项**：
1. **使用 request.js 封装的 axios**（推荐）：
   - 自动携带 JWT token
   - FormData 请求时，拦截器会跳过 `Content-Type` 覆盖，由浏览器自动设置 `multipart/form-data`
   - 示例：`request.post('/api/dataset/upload', formData)`
2. **el-upload 组件原生上传**：
   - `el-upload` 默认使用原生 HTTP 请求，**不经过 request.js 的 axios 拦截器**
   - 请求头中**不会自动携带 token**
   - 必须手动添加 `:headers="uploadHeaders"` 属性
3. **涉及的页面**：DatasetManagement.vue、DataCollection.vue、TestFile.vue、User.vue

### 5.6 缓存工具 (cacheHelper.js)

封装 localStorage: get/set/remove/clearAll/clearAllLoginState
缓存 keys: user, menus, currentPathName

### 5.7 状态管理 (Pinia)

**useMainStore** (`store/index.js`) — 使用 Options API 风格：
- State: `currentPathName` (当前路径名)
- Getters: `getCurrentPathName` (state => state.currentPathName)
- Actions: `setPath()` (从缓存读取), `logout()` (清除登录状态), `clearAllCache()` (清除所有缓存)

**useHealthStore** (`store/healthStore.js`) — 使用 Composition API 风格：
- 身体数据 ref: age, height, weight, glucose, bloodPressure, insulin, skinThickness, pregnancies, diabetesPedigreeFunction, gender, symptoms, exerciseFrequency, dietHabit, smoking, drinking
- 计算属性: bmi（自动计算）、hasPrediction
- 预测结果: riskLevel, riskProbability, predictionId, aiAdvice, chartsData, featureImportance, featureNames, percentiles, similarCases, confidenceInterval, predictionTime, adviceData
- 方法: loadFromDraft(), saveToDraft(), clearDraft(), setPredictionResult(), resetAll(), toFeatures(), toSavePayload()

### 5.8 Composables（组合式函数）

| 文件 | 功能 |
|------|------|
| useDraftPersistence.js | 表单草稿自动保存/恢复（基于localStorage，key='health_draft'，间隔5000ms，依赖useHealthStore） |
| useGlobalSound.js | 全局音效管理（Web Audio API，不依赖音频文件；悬停sine波800→1200Hz、点击triangle波600→100Hz；在App.vue中全局调用） |
| useHealthValidator.js | 健康数据校验（6字段三级范围：glucose/bmi/bloodPressure/age/insulin/skinThickness，返回{level,message}） |
| usePrediction.js | 预测逻辑封装（调用/api/predict/single，失败回退本地规则计算，含getRiskText/getRiskClass/getHealthAdvice） |

---

## 6. 数据库

### 6.1 数据库配置
- 数据库名: `dongfang`
- 字符集: `utf8mb4`
- 初始化脚本: `sql/Software-ODA.sql`

### 6.2 核心表清单（共19张）

**SQL初始化脚本定义（18张）：**

| # | 表名 | 说明 |
|---|------|------|
| 1 | sys_user | 用户表 |
| 2 | sys_role | 角色表 |
| 3 | sys_menu | 菜单表（树形结构，parent_id关联） |
| 4 | sys_role_menu | 角色-菜单关联表 |
| 5 | sys_dict | 数据字典（图标配置） |
| 6 | sys_message | 消息/故障报修表 |
| 7 | sys_trainfile | 训练文件表 |
| 8 | sys_train_task | 训练任务表（含python_script字段，已整合到主SQL） |
| 9 | sys_model_version | 模型版本表 |
| 10 | sys_testfile | 测试文件表 |
| 11 | sys_config | 系统配置表 |
| 12 | sys_result | 在线数据统计表（实体类：OnlineDate） |
| 13 | diabetes_education | 糖尿病科普文章 |
| 14 | diabetes_video | 糖尿病视频 |
| 15 | diabetes_record | 糖尿病记录（Pima Indians数据集） |
| 16 | education_comment | 科普留言 |
| 17 | user_health_profiles | 健康档案 |
| 18 | patient_visit_record | 患者就诊记录 |

**数据库运行时存在（1张，未在初始化SQL中定义）：**

| # | 表名 | 说明 |
|---|------|------|
| 19 | sys_treatment_record | 诊疗档案表（实体类：TreatmentRecord，XML硬编码表名） |

### 6.3 角色数据 (sys_role)

| ID | 名称 | 角色编码 | 说明 |
|----|------|---------|------|
| 1 | 管理员 | ROLE_ADMIN | 系统管理员 |
| 2 | 用户 | ROLE_WORKER | 普通用户 |
| 3 | 运维 | ROLE_MAINTENANCE | 运维人员 |
| 16 | 测试1 | 0001 | 系统增删测试项 |
| 17 | 测试2 | 0002 | 系统增删测试项 |
| 18 | 医生 | ROLE_DOCTOR | 医生角色 |

### 6.4 菜单模块 (8大模块, 32条菜单记录)

1. **主页** — 首页仪表盘
2. **系统管理** — 用户管理、角色管理、菜单管理
3. **AI模型中心** — 训练集管理、模型管理、在线模型训练
4. **用户服务** — 风险快检、健康管理中心、智能问答、诊疗档案、糖尿病科普
5. **诊断员服务** — 组合分析、个体洞察、数据采集、预测工作台、诊断工作台
6. **糖尿病预测中心** — 采集日志、在线预测、数据报表、详细报表
7. **运维中心** — 故障报修、报修详情、运维详情、信息回执
8. **关于**

---

## 7. Python 模块

### 7.1 文件清单

```python
python/
├── train.py               # PyTorch模型训练
├── predict.py             # 批量预测 (CSV/Excel → JSON，输出到 data/json/)
├── predict_single.py      # 单条预测 (功能最丰富)
├── diabetes_chat.py       # AI 聊天机器人 (OpenAI兼容)
├── data_analysis.py       # 数据分析 (含旧版TensorFlow逻辑)
├── read_data.py           # 数据读取工具函数
├── A10.py                 # 辅助脚本
├── similar_cases.npy      # 相似病例历史数据
└── Untitled-1.js          # 临时文件
```

### 7.2 模型结构 (train.py / predict_single.py)

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

### 7.3 predict_single.py 增强功能
- MC Dropout 不确定性估计
- SHAP 特征重要性解释
- 可视化图表（特征重要性图、风险仪表盘、雷达图、瀑布图）
- 百分位计算（用户指标在人群中的位置）
- 相似病例分析（基于余弦相似度匹配 `similar_cases.npy`）

### 7.4 AI 聊天机器人 (diabetes_chat.py)

支持的模型:
- 智谱 (GLM): glm-4-flash, glm-4.7-flash
- DeepSeek
- Kimi (Moonshot)
- 小米 MiMo: mimo-v2.5-pro, mimo-v2-flash, mimo-v2-omni

系统提示: 强制扮演糖尿病医生，不承认是AI，用短句回答，与糖尿病无关问题则拒绝回答。

---

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
- 批量预测: 上传CSV/Excel文件 → Python批量处理 → 下载结果JSON (输出到 data/json/)
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

### 9.2 前后端类型检查
- 后端 `Result.code` 是 **String** 类型 (`"200"`)
- 前端判断时必须用 `=== '200'`, 不能用 `=== 200`

### 9.3 项目规范
- Vue 组件必须使用 `<script setup>` Composition API
- 所有 HTTP 请求必须通过 `@/utils/request.js` 封装
- Element Plus 图标使用 `@element-plus/icons-vue` 包
- 状态管理使用 Pinia（禁止 Vuex）
- 路由使用 `createWebHistory` 模式（禁止 Hash 模式）
- **文件上传接口选择**：
  - **推荐**：使用 `/api/dataset/upload`（JWT 认证，支持 category 参数）
  - **旧接口**：`/python/upload`（依赖 Redis userId，存在多用户冲突风险，不推荐使用）
- **el-upload 组件注意事项**：
  - 使用 `request.js` 封装的 axios 发送 FormData 时，拦截器会自动跳过 `Content-Type` 覆盖
  - 使用 el-upload 原生上传时，必须手动携带 token：`:headers="uploadHeaders"`

### 9.4 启动顺序
1. 启动 MySQL (3306) 和 Redis (6379)
2. 执行 `sql/Software-ODA.sql` 初始化数据库（已整合所有迁移）
3. 手动创建 `sys_treatment_record` 表（如不存在）
4. 启动后端: `mvn spring-boot:run` (端口 9090)
5. 进入前端: `cd vue` → `npm install` → `npm run dev` (端口 8080)

### 9.5 运行时目录
以下目录在 application.yml 中已配置，运行时按需自动创建：
- `./data/` — 训练数据上传（已存在）
- `./data/test/` — 预测数据上传（测试集）
- `./data/json/` — JSON预测结果输出
- `./data/models/` — 模型文件（已存在，含 npy_data/ 和 pth_models/）
- `./logs/` — 训练日志（已存在）
- `./files/` — 通用文件上传（含 avatar/ 和 common/ 子目录）

### 9.6 .gitignore 关键排除项
- 模型文件: `.pth`, `.pkl`, `.h5`, `.onnx`, `.bin`, `.ckpt`
- 数据集: `.csv`, `.xlsx`, `.xls`
- JSON输出: 全部 `.json`（位于 `data/json/` 目录）
- 密钥文件: `secrets/`, `application-secrets.yml`
- 构建产物: `**/target/`, `**/dist/`, `node_modules/`

---

## 10. 已知技术债务与非标准模式

> 以下为项目中已知的规范违规和技术债务，修改时需注意兼容性。

### 10.1 前端 Options API 违规（4 个文件）

| 文件 | 问题 | 影响 |
|------|------|------|
| Manage.vue | 使用 `export default { data(), methods, created() }` + 组件名 `'Home'`（与功能不匹配） | 主布局组件，改动需谨慎 |
| Login.vue | 使用 `export default { setup(), data(), methods }` 混合模式 | 登录入口 |
| 404.vue | 使用 Options API + Vue2 生命周期 `beforeDestroy`（应为 `unmounted`） | 404页面 |
| Header.vue | 使用 `export default { setup() }` 而非 `<script setup>` | 退出登录含 resetRouter 逻辑 |

### 10.2 后端命名不规范

| 问题 | 位置 |
|------|------|
| `pythonController.java` 首字母小写 | `controller/` 目录 |
| `MessageService.java` 无 `I` 前缀（其他接口均有） | `service/` 目录 |
| `OnlineDate` 实体类名与 `sys_result` 表名语义不匹配 | `entity/OnlineDate.java` |
| `DiabetesEducation` 实体无 `@TableName` 注解（依赖默认转换） | `entity/DiabetesEducation.java` |

### 10.3 数据库相关

| 问题 | 说明 |
|------|------|
| `sys_treatment_record` 表未在 `Software-ODA.sql` 中定义 | 运行时存在，需手动创建 |
| `Dashbord`/`Detailbord` 拼写错误（应为 Dashboard/Dashboard） | 数据库 menu 表 + 前端组件名，已全局保留此拼写 |

### 10.4 已废弃的 MyBatis XML 映射（6 个）

项目规则禁止引入新的 MyBatis XML 映射文件，但以下 6 个为历史遗留：

| 文件 | 说明 |
|------|------|
| UserMapper.xml | 用户查询 |
| RoleMapper.xml | 角色查询 |
| MenuMapper.xml | 菜单树形查询 |
| DiabetesEducationMapper.xml | 科普文章查询 |
| EducationCommentMapper.xml | 科普评论查询 |
| TreatmentRecordMapper.xml | 诊疗档案CRUD（硬编码 `sys_treatment_record` 表名） |

> 新增功能应使用 MyBatis-Plus 注解方式（`@Select`、`@Insert` 等），禁止新增 XML 映射文件。

### 10.5 文件上传接口迁移

| 问题 | 说明 |
|------|------|
| `/python/upload` 旧接口 | 依赖 Redis userId，存在多用户冲突风险；不支持 category 参数；URL 硬编码参数 |
| `/api/dataset/upload` 新接口 | JWT 认证，支持 category 参数，自动解析 CSV 列信息 |
| `DatasetManagement.vue` 已迁移 | 使用 request.js + FormData 调用新接口 |
| 其他页面待迁移 | DataCollection.vue、TestFile.vue、User.vue 仍使用旧上传方式 |

> **迁移指南**：将 `fetch('http://${serverIp}:9090/python/upload', ...)` 替换为 `request.post('/api/dataset/upload', formData)`
