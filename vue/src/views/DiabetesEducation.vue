<template>
  <div class="diabetes-education-container">
    <!-- 页面头部 -->
    <div class="header">
      <h1>糖尿病全方位科普指南</h1>
      <p class="subtitle">从10个维度系统梳理糖尿病知识，涵盖基础概念、日常管理、误区澄清及前沿进展</p>
    </div>

    <!-- 视频播放窗口 -->
    <div class="video-player-section">
      <div class="video-selector">
        <button 
          v-for="(video, index) in videoList" 
          :key="index"
          :class="['video-tab', { active: currentVideoIndex === index }]"
          @click="selectVideo(index)"
        >
          {{ video.title }}
        </button>
      </div>
      <div class="video-container">
        <iframe 
          ref="bilibiliIframe"
          :src="videoList[currentVideoIndex].src" 
          scrolling="no" 
          border="0" 
          frameborder="no" 
          framespacing="0" 
          allowfullscreen="true"
          class="bilibili-player"
        ></iframe>
      </div>
    </div>

    <!-- 内容导航 -->
    <div class="navigation">
      <div class="nav-item" v-for="(section, index) in sections" :key="index" @click="scrollToSection(section.id)">
        {{ section.title }}
      </div>
    </div>

    <!-- 主要内容 -->
    <div class="main-content">
      <!-- 基础认知篇 -->
      <section id="basic" class="section section-basic">
        <div class="section-header">
          <h2 class="section-title">一、基础认知篇</h2>
          <el-button v-if="isAdmin" type="primary" size="small" @click="toggleEditMode" class="edit-btn">
            {{ editMode ? '退出编辑' : '编辑' }}
          </el-button>
        </div>
        
        <div class="section-content">
          <h3>1. 什么是糖尿病？</h3>
          <p>糖尿病是一组以<strong>高血糖</strong>为特征的代谢性疾病。当胰腺不能产生足够的胰岛素，或身体不能有效利用产生的胰岛素时，就会发生糖尿病。</p>

          <h3>2. 主要分型</h3>
          <div class="table-container">
            <table class="info-table">
              <thead>
                <tr>
                  <th>类型</th>
                  <th>占比</th>
                  <th>特点</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td><strong>1型糖尿病</strong></td>
                  <td>约5-10%</td>
                  <td>自身免疫破坏胰岛β细胞，胰岛素绝对缺乏，多见于青少年</td>
                </tr>
                <tr>
                  <td><strong>2型糖尿病</strong></td>
                  <td>约90%</td>
                  <td>胰岛素抵抗+相对分泌不足，与生活方式密切相关</td>
                </tr>
                <tr>
                  <td><strong>妊娠糖尿病</strong></td>
                  <td>约2-10%</td>
                  <td>妊娠期间首次发生或发现，多数产后恢复</td>
                </tr>
                <tr>
                  <td><strong>特殊类型</strong></td>
                  <td>&lt;1%</td>
                  <td>单基因糖尿病、胰腺疾病、内分泌疾病等所致</td>
                </tr>
              </tbody>
            </table>
          </div>

          <h3>3. 诊断标准（静脉血浆葡萄糖）</h3>
          <ul class="standard-list">
            <li><strong>空腹血糖</strong> ≥ 7.0 mmol/L</li>
            <li><strong>餐后2小时血糖</strong> ≥ 11.1 mmol/L</li>
            <li><strong>随机血糖</strong> ≥ 11.1 mmol/L 且有典型症状</li>
            <li><strong>糖化血红蛋白（HbA1c）</strong> ≥ 6.5%</li>
          </ul>
          <div class="warning-box">
            <p>⚠️ 需重复确认（无症状者需两次异常）</p>
          </div>
        </div>
      </section>

      <!-- 症状识别篇 -->
      <section id="symptoms" class="section section-symptoms">
        <div class="section-header">
          <h2 class="section-title">二、症状识别篇</h2>
          <el-button v-if="isAdmin" type="primary" size="small" @click="toggleEditMode" class="edit-btn">
            {{ editMode ? '退出编辑' : '编辑' }}
          </el-button>
        </div>
        
        <div class="section-content">
          <h3>典型"三多一少"</h3>
          <ul class="symptom-list">
            <li><strong>多尿</strong>：血糖过高导致渗透性利尿</li>
            <li><strong>多饮</strong>：脱水刺激口渴中枢</li>
            <li><strong>多食</strong>：细胞无法利用葡萄糖，能量缺乏</li>
            <li><strong>体重下降</strong>：脂肪和蛋白质分解供能</li>
          </ul>

          <h3>不典型症状（尤其2型）</h3>
          <ul class="symptom-list">
            <li>皮肤瘙痒（尤其外阴）、伤口愈合缓慢</li>
            <li>视力模糊、反复感染（泌尿道、皮肤）</li>
            <li>乏力、手脚麻木或刺痛</li>
            <li>餐前低血糖（心悸、出汗、饥饿感）</li>
          </ul>

          <h3>高危人群筛查</h3>
          <ul class="risk-list">
            <li>年龄≥40岁</li>
            <li>超重/肥胖（BMI≥24，男性腰围≥90cm，女性≥85cm）</li>
            <li>有糖尿病家族史</li>
            <li>妊娠糖尿病史或巨大儿分娩史</li>
            <li>高血压、血脂异常、脂肪肝患者</li>
            <li>长期久坐、熬夜、精神压力大者</li>
          </ul>
        </div>
      </section>

      <!-- 饮食管理篇 -->
      <section id="diet" class="section section-diet">
        <div class="section-header">
          <h2 class="section-title">三、饮食管理篇</h2>
          <el-button v-if="isAdmin" type="primary" size="small" @click="toggleEditMode" class="edit-btn">
            {{ editMode ? '退出编辑' : '编辑' }}
          </el-button>
        </div>
        
        <div class="section-content">
          <h3>核心原则</h3>
          <div class="principle-box">
            <p>控制总热量、均衡营养、定时定量、低GI优先</p>
          </div>

          <h3>1. 碳水化合物（占总热量50-60%）</h3>
          <ul class="food-list">
            <li><span class="icon good">✅</span> 优选：全谷物、杂豆、燕麦、糙米、红薯</li>
            <li><span class="icon bad">❌</span> 限制：白粥、糯米、含糖饮料、糕点</li>
            <li><span class="icon tip">💡</span> 技巧：主食放凉后抗性淀粉增加，升糖更慢</li>
          </ul>

          <h3>2. 蛋白质（15-20%）</h3>
          <p>鱼、禽、蛋、豆制品、瘦肉</p>
          <p>肾功能不全者需限制蛋白摄入量</p>

          <h3>3. 脂肪（&lt;30%）</h3>
          <ul class="food-list">
            <li><span class="icon bad">❌</span> 减少饱和脂肪（肥肉、黄油）</li>
            <li><span class="icon good">✅</span> 增加不饱和脂肪（深海鱼、坚果、橄榄油）</li>
          </ul>

          <h3>4. 膳食纤维</h3>
          <p>每日25-30g，延缓糖分吸收</p>
          <p>蔬菜不限量（淀粉类蔬菜除外），先吃菜再吃饭</p>

          <h3>5. 升糖指数（GI）知识</h3>
          <div class="table-container">
            <table class="info-table">
              <thead>
                <tr>
                  <th>低GI（&lt;55）</th>
                  <th>中GI（55-70）</th>
                  <th>高GI（&gt;70）</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>苹果、梨、牛奶、荞麦</td>
                  <td>糙米、香蕉、菠萝</td>
                  <td>白面包、西瓜、葡萄糖</td>
                </tr>
              </tbody>
            </table>
          </div>

          <h3>6. 饮食误区</h3>
          <ul class="myth-list">
            <li><span class="icon bad">❌</span> "无糖食品"随便吃 → 可能含大量淀粉和脂肪</li>
            <li><span class="icon bad">❌</span> 不吃主食 → 易引发酮症和低血糖</li>
            <li><span class="icon bad">❌</span> 只吃粗粮 → 增加胃肠负担，影响营养吸收</li>
            <li><span class="icon bad">❌</span> 水果完全禁止 → 可选择低GI水果，在两餐间吃</li>
          </ul>
        </div>
      </section>

      <!-- 运动治疗篇 -->
      <section id="exercise" class="section section-exercise">
        <div class="section-header">
          <h2 class="section-title">四、运动治疗篇</h2>
          <el-button v-if="isAdmin" type="primary" size="small" @click="toggleEditMode" class="edit-btn">
            {{ editMode ? '退出编辑' : '编辑' }}
          </el-button>
        </div>
        
        <div class="section-content">
          <h3>运动益处</h3>
          <ul class="benefit-list">
            <li>提高胰岛素敏感性</li>
            <li>降低血糖、血脂、血压</li>
            <li>减轻体重，改善心肺功能</li>
            <li>缓解焦虑抑郁</li>
          </ul>

          <h3>推荐运动</h3>
          <div class="table-container">
            <table class="info-table">
              <thead>
                <tr>
                  <th>类型</th>
                  <th>项目</th>
                  <th>频率</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>有氧运动</td>
                  <td>快走、游泳、骑车</td>
                  <td>每周≥150分钟，中等强度</td>
                </tr>
                <tr>
                  <td>抗阻训练</td>
                  <td>哑铃、弹力带、深蹲</td>
                  <td>每周2-3次，锻炼大肌群</td>
                </tr>
                <tr>
                  <td>柔韧性</td>
                  <td>瑜伽、太极、拉伸</td>
                  <td>每天可练</td>
                </tr>
              </tbody>
            </table>
          </div>

          <h3>运动注意事项</h3>
          <ul class="note-list">
            <li><span class="icon clock">⏰</span> <strong>时机</strong>：餐后1小时开始，避免空腹运动</li>
            <li><span class="icon blood">🩸</span> <strong>监测</strong>：运动前后测血糖，&lt;5.6或&gt;16.7 mmol/L不宜运动</li>
            <li><span class="icon shoe">👟</span> <strong>装备</strong>：穿舒适鞋袜，预防足部损伤</li>
            <li><span class="icon candy">🍬</span> <strong>备糖</strong>：随身携带糖果，防低血糖</li>
          </ul>
        </div>
      </section>

      <!-- 药物治疗篇 -->
      <section id="medication" class="section section-medication">
        <div class="section-header">
          <h2 class="section-title">五、药物治疗篇</h2>
          <el-button v-if="isAdmin" type="primary" size="small" @click="toggleEditMode" class="edit-btn">
            {{ editMode ? '退出编辑' : '编辑' }}
          </el-button>
        </div>
        
        <div class="section-content">
          <h3>口服降糖药主要类别</h3>
          <div class="table-container">
            <table class="info-table">
              <thead>
                <tr>
                  <th>类别</th>
                  <th>代表药</th>
                  <th>作用机制</th>
                  <th>特点</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>双胍类</td>
                  <td>二甲双胍</td>
                  <td>减少肝糖输出，改善胰岛素抵抗</td>
                  <td>一线首选，减重，心血管获益</td>
                </tr>
                <tr>
                  <td>磺脲类</td>
                  <td>格列美脲</td>
                  <td>刺激胰岛分泌胰岛素</td>
                  <td>降糖强，但易低血糖</td>
                </tr>
                <tr>
                  <td>格列奈类</td>
                  <td>瑞格列奈</td>
                  <td>快速刺激胰岛素分泌</td>
                  <td>餐时服用，灵活</td>
                </tr>
                <tr>
                  <td>α-糖苷酶抑制剂</td>
                  <td>阿卡波糖</td>
                  <td>延缓肠道碳水吸收</td>
                  <td>降餐后血糖，腹胀副作用</td>
                </tr>
                <tr>
                  <td>DPP-4抑制剂</td>
                  <td>西格列汀</td>
                  <td>促进胰岛素分泌，抑制胰高糖素</td>
                  <td>低血糖风险小</td>
                </tr>
                <tr>
                  <td>SGLT-2抑制剂</td>
                  <td>达格列净</td>
                  <td>促进尿糖排泄</td>
                  <td>减重、降压、心肾保护</td>
                </tr>
                <tr>
                  <td>GLP-1受体激动剂</td>
                  <td>司美格鲁肽</td>
                  <td>多重机制降糖</td>
                  <td>强效降糖、减重显著</td>
                </tr>
              </tbody>
            </table>
          </div>

          <h3>胰岛素治疗</h3>
          <ul class="insulin-list">
            <li><strong>适用</strong>：1型糖尿病、妊娠糖尿病、2型口服药失效、急性并发症</li>
            <li><strong>类型</strong>：速效、短效、中效、长效、预混</li>
            <li><strong>误区</strong>：胰岛素不会"成瘾"，也不是病情晚期才用</li>
          </ul>
        </div>
      </section>

      <!-- 血糖监测篇 -->
      <section id="monitoring" class="section section-monitoring">
        <div class="section-header">
          <h2 class="section-title">六、血糖监测篇</h2>
          <el-button v-if="isAdmin" type="primary" size="small" @click="toggleEditMode" class="edit-btn">
            {{ editMode ? '退出编辑' : '编辑' }}
          </el-button>
        </div>
        
        <div class="section-content">
          <h3>监测时间点（"七点法"）</h3>
          <div class="time-points">
            <span>空腹</span> → <span>早餐后2h</span> → <span>午餐前</span> → <span>午餐后2h</span> → <span>晚餐前</span> → <span>晚餐后2h</span> → <span>睡前</span>
          </div>

          <h3>控制目标（一般成人）</h3>
          <div class="table-container">
            <table class="info-table">
              <thead>
                <tr>
                  <th>指标</th>
                  <th>目标值</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>空腹血糖</td>
                  <td>4.4-7.0 mmol/L</td>
                </tr>
                <tr>
                  <td>餐后2h血糖</td>
                  <td>&lt;10.0 mmol/L</td>
                </tr>
                <tr>
                  <td>糖化血红蛋白（HbA1c）</td>
                  <td>&lt;7.0%</td>
                </tr>
                <tr>
                  <td>血压</td>
                  <td>&lt;130/80 mmHg</td>
                </tr>
                <tr>
                  <td>LDL-C</td>
                  <td>&lt;2.6 mmol/L（合并冠心病&lt;1.8）</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="note-box">
            <p>老年人、有严重并发症者目标可适当放宽</p>
          </div>

          <h3>糖化血红蛋白意义</h3>
          <p>反映过去2-3个月平均血糖水平，是评估长期控制的"金标准"，每3个月检测一次。</p>
        </div>
      </section>

      <!-- 并发症防治篇 -->
      <section id="complications" class="section section-complications">
        <div class="section-header">
          <h2 class="section-title">七、并发症防治篇</h2>
          <el-button v-if="isAdmin" type="primary" size="small" @click="toggleEditMode" class="edit-btn">
            {{ editMode ? '退出编辑' : '编辑' }}
          </el-button>
        </div>
        
        <div class="section-content">
          <h3>急性并发症</h3>
          <div class="table-container">
            <table class="info-table">
              <thead>
                <tr>
                  <th>类型</th>
                  <th>诱因</th>
                  <th>表现</th>
                  <th>处理</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td><strong>低血糖</strong></td>
                  <td>药量过大、进食少、运动过量</td>
                  <td>心慌、出汗、手抖、意识模糊</td>
                  <td>立即补糖（15g快糖），15分钟复测</td>
                </tr>
                <tr>
                  <td><strong>酮症酸中毒</strong></td>
                  <td>感染、停药、应激</td>
                  <td>恶心呕吐、腹痛、呼吸深快、烂苹果味</td>
                  <td>急诊就医，补液+胰岛素</td>
                </tr>
                <tr>
                  <td><strong>高渗高血糖状态</strong></td>
                  <td>感染、脱水、大量饮甜饮料</td>
                  <td>严重脱水、意识障碍、血糖极高</td>
                  <td>急诊，死亡率较高</td>
                </tr>
              </tbody>
            </table>
          </div>

          <h3>慢性并发症</h3>
          <ol class="complication-list">
            <li><strong>糖尿病肾病</strong>：早期微量白蛋白尿→大量蛋白尿→肾衰竭。每年查尿微量白蛋白和肾功能。</li>
            <li><strong>糖尿病视网膜病变</strong>：致盲主因。2型确诊时即应筛查眼底，每年复查。</li>
            <li><strong>糖尿病神经病变</strong>：手套袜套样感觉异常、麻木、疼痛、胃肠功能紊乱。</li>
            <li><strong>糖尿病足</strong>：溃疡、感染、坏疽。每日检查双足，保持清洁干燥，穿合适鞋袜。</li>
            <li><strong>心脑血管病变</strong>：心梗、脑卒中风险增加2-4倍。严格控制血压、血脂、抗血小板治疗。</li>
          </ol>
        </div>
      </section>

      <!-- 特殊人群篇 -->
      <section id="special" class="section section-special">
        <div class="section-header">
          <h2 class="section-title">八、特殊人群篇</h2>
          <el-button v-if="isAdmin" type="primary" size="small" @click="toggleEditMode" class="edit-btn">
            {{ editMode ? '退出编辑' : '编辑' }}
          </el-button>
        </div>
        
        <div class="section-content">
          <h3>儿童青少年</h3>
          <ul class="special-list">
            <li>1型为主，需终身胰岛素</li>
            <li>生长发育期胰岛素需求变化大</li>
            <li>心理支持至关重要</li>
          </ul>

          <h3>老年人</h3>
          <ul class="special-list">
            <li>症状不典型，易漏诊</li>
            <li>低血糖感知能力下降，风险更高</li>
            <li>控制目标个体化，避免过度治疗</li>
            <li>注意多重用药相互作用</li>
          </ul>

          <h3>妊娠期</h3>
          <ul class="special-list">
            <li>首选饮食运动控制，必要时用胰岛素</li>
            <li>血糖控制更严格：空腹&lt;5.3，餐后1h&lt;7.8，2h&lt;6.7</li>
            <li>产后6-12周复查OGTT，此后每3年筛查</li>
          </ul>
        </div>
      </section>

      <!-- 生活方式干预篇 -->
      <section id="lifestyle" class="section section-lifestyle">
        <div class="section-header">
          <h2 class="section-title">九、生活方式干预篇</h2>
          <el-button v-if="isAdmin" type="primary" size="small" @click="toggleEditMode" class="edit-btn">
            {{ editMode ? '退出编辑' : '编辑' }}
          </el-button>
        </div>
        
        <div class="section-content">
          <h3>体重管理</h3>
          <ul class="lifestyle-list">
            <li>超重2型糖尿病患者减重5-10%，可显著改善血糖</li>
            <li>重度肥胖可考虑代谢手术（BMI≥32.5或≥27.5伴控制不佳）</li>
          </ul>

          <h3>睡眠与压力</h3>
          <ul class="lifestyle-list">
            <li>睡眠不足（&lt;6小时）增加胰岛素抵抗</li>
            <li>长期压力升高皮质醇，推高血糖</li>
            <li>建议每晚7-8小时优质睡眠</li>
          </ul>

          <h3>戒烟限酒</h3>
          <ul class="lifestyle-list">
            <li>吸烟加速血管病变，增加截肢风险</li>
            <li>酒精干扰血糖，空腹饮酒易低血糖</li>
            <li>如饮酒：女性≤1份/日，男性≤2份/日（1份≈啤酒350ml/葡萄酒150ml）</li>
          </ul>
        </div>
      </section>

      <!-- 前沿进展与误区澄清 -->
      <section id="advance" class="section section-advance">
        <div class="section-header">
          <h2 class="section-title">十、前沿进展与误区澄清</h2>
          <el-button v-if="isAdmin" type="primary" size="small" @click="toggleEditMode" class="edit-btn">
            {{ editMode ? '退出编辑' : '编辑' }}
          </el-button>
        </div>
        
        <div class="section-content">
          <h3>新技术</h3>
          <ul class="tech-list">
            <li><strong>持续葡萄糖监测（CGM）</strong>：实时看血糖曲线，发现隐匿高低血糖</li>
            <li><strong>人工胰腺/闭环系统</strong>：CGM+胰岛素泵自动调节，主要用于1型</li>
            <li><strong>干细胞治疗</strong>：尚在研究阶段，未广泛应用于临床</li>
          </ul>

          <h3>常见误区纠正</h3>
          <div class="table-container">
            <table class="info-table">
              <thead>
                <tr>
                  <th>误区</th>
                  <th>真相</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>糖尿病是吃糖吃出来的</td>
                  <td>遗传+环境共同作用，不单是吃糖</td>
                </tr>
                <tr>
                  <td>得了糖尿病就不能吃水果</td>
                  <td>可选低GI水果，控制量，两餐间吃</td>
                </tr>
                <tr>
                  <td>胰岛素会上瘾</td>
                  <td>胰岛素是人体激素，需则用，不存在成瘾</td>
                </tr>
                <tr>
                  <td>血糖正常就能停药</td>
                  <td>需医生评估，擅自停药易反弹</td>
                </tr>
                <tr>
                  <td>偏方/保健品能根治</td>
                  <td>目前无法根治，警惕虚假广告</td>
                </tr>
                <tr>
                  <td>瘦子不会得2型糖尿病</td>
                  <td>约10-15%的2型患者体重正常</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </section>

      <!-- 视频科普篇 -->
<!--      <section id="video" class="section section-video">-->
<!--        <div class="section-header">-->
<!--          <h2 class="section-title">十一、视频科普篇</h2>-->
<!--          <el-button v-if="isAdmin" type="primary" size="small" @click="toggleEditMode" class="edit-btn">-->
<!--            {{ editMode ? '退出编辑' : '编辑' }}-->
<!--          </el-button>-->
<!--        </div>-->

<!--        <div class="section-content">-->
<!--          &lt;!&ndash; 封面区域(未播放时显示) &ndash;&gt;-->
<!--          <div v-if="!isPlaying" class="video-section">-->
<!--            <div class="video-cover" @click="playFullScreen">-->
<!--              <img src="/111.jpg" alt="糖尿病科普视频封面">-->
<!--              <div class="play-icon">-->
<!--                <i class="el-icon-caret-right"></i>-->
<!--              </div>-->
<!--            </div>-->
<!--            <div class="video-info">-->
<!--              <h3 class="video-title">糖尿病科普视频</h3>-->
<!--              <p class="video-desc">本视频详细讲解糖尿病的成因、症状、预防及日常管理知识,帮助您全面了解糖尿病.</p>-->
<!--              <el-tag size="small">科普教育</el-tag>-->
<!--            </div>-->
<!--          </div>-->

<!--          &lt;!&ndash; 全屏视频区域(播放时显示) &ndash;&gt;-->
<!--          <div v-else ref="videoContainer" class="fullscreen-video">-->
<!--            <video-->
<!--                ref="videoPlayer"-->
<!--                src="/1111.mp4"-->
<!--                controls-->
<!--                @ended="onVideoEnded"-->
<!--            ></video>-->
<!--            &lt;!&ndash; 退出按钮 &ndash;&gt;-->
<!--            <div class="exit-btn" @click="exitVideo">-->
<!--              <i class="el-icon-close"></i>-->
<!--            </div>-->
<!--          </div>-->
<!--        </div>-->
<!--      </section>-->

      <!-- 核心建议总结 -->
      <section id="summary" class="section section-summary">
        <div class="section-header">
          <h2 class="section-title">核心建议总结</h2>
          <el-button v-if="isAdmin" type="primary" size="small" @click="toggleEditMode" class="edit-btn">
            {{ editMode ? '退出编辑' : '编辑' }}
          </el-button>
        </div>
        
        <div class="summary-content">
          <ol class="summary-list">
            <li><strong>早筛查</strong>：40岁以上每年体检加做血糖和HbA1c</li>
            <li><strong>管住嘴</strong>：控制总量、低GI、高纤维、少油少盐</li>
            <li><strong>迈开腿</strong>：每周150分钟中等强度运动</li>
            <li><strong>勤监测</strong>：定期测血糖、HbA1c、血压、血脂、眼底、尿蛋白、足</li>
            <li><strong>遵医嘱</strong>：不擅自调药，定期复诊</li>
            <li><strong>学知识</strong>：参加糖尿病教育课程，提升自我管理能力</li>
          </ol>
        </div>
      </section>

      <!-- 留言区 -->
      <section id="comments" class="section section-comments">
        <div class="section-header">
          <h2 class="section-title">留言讨论</h2>
          <el-button v-if="isAdmin" type="primary" size="small" @click="toggleEditMode" class="edit-btn">
            {{ editMode ? '退出编辑' : '编辑' }}
          </el-button>
        </div>
        
        <div class="comments-content">
          <div class="comment-form">
            <h3>发表留言</h3>
            <textarea v-model="commentContent" placeholder="请输入您的留言..." rows="4"></textarea>
            <button @click="submitComment" class="submit-btn">提交留言</button>
          </div>

          <div class="comment-list">
            <h3>留言列表</h3>
            <div v-if="comments.length === 0" class="no-comments">
              暂无留言，快来发表第一条留言吧！
            </div>
            <div v-else v-for="(comment, index) in comments" :key="index" class="comment-item">
              <div class="comment-header">
                <span class="comment-user">{{ comment.user }}</span>
                <span class="comment-time">{{ comment.time }}</span>
              </div>
              <div class="comment-body">
                {{ comment.content }}
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>

    <!-- 右侧悬浮导航栏 -->
    <div class="floating-nav" v-show="showFloatingNav">
      <h3>目录索引</h3>
      <ul>
        <li v-for="(section, index) in sections" :key="index" @click="scrollToSection(section.id)">
          <a :class="{ active: currentSection === section.id }">{{ section.title }}</a>
        </li>
      </ul>
    </div>

    <!-- 返回顶部控件 -->
    <transition name="fade">
      <div v-show="showBackToTop" class="back-to-top" @click="scrollToTop">
        <el-icon :size="26">
          <ArrowUp />
        </el-icon>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const sections = ref([
  { id: 'basic', title: '基础认知' },
  { id: 'symptoms', title: '症状识别' },
  { id: 'diet', title: '饮食管理' },
  { id: 'exercise', title: '运动治疗' },
  { id: 'medication', title: '药物治疗' },
  { id: 'monitoring', title: '血糖监测' },
  { id: 'complications', title: '并发症防治' },
  { id: 'special', title: '特殊人群' },
  { id: 'lifestyle', title: '生活方式' },
  { id: 'advance', title: '前沿进展' },
  { id: 'video', title: '视频科普' },
  { id: 'summary', title: '核心建议' },
  { id: 'comments', title: '留言讨论' }
])

const videoList = ref([
  { 
    title: '糖尿病基础知识',
    src: '//player.bilibili.com/player.html?isOutside=true&aid=525373118&bvid=BV1iM411x7a3&cid=1033141128&p=1&danmaku=0&muted=0'
  },
  { 
    title: '糖尿病预防与控制',
    src: '//player.bilibili.com/player.html?isOutside=true&aid=961181150&bvid=BV1sH4y1S7wc&cid=1272479110&p=1&danmaku=0&muted=0'
  },
  { 
    title: '糖尿病饮食管理',
    src: '//player.bilibili.com/player.html?isOutside=true&aid=114566306464087&bvid=BV1ebjuzGEoQ&cid=30138435449&p=1&danmaku=0&muted=0'
  },
  { 
    title: '糖尿病运动治疗',
    src: '//player.bilibili.com/player.html?isOutside=true&aid=729648895&bvid=BV1ES4y1W7s9&cid=808685182&p=1&danmaku=0&muted=0'
  },
  { 
    title: '糖尿病药物治疗',
    src: '//player.bilibili.com/player.html?isOutside=true&aid=627196236&bvid=BV15t4y1q77H&cid=236854129&p=1&danmaku=0&muted=0'
  },
  { 
    title: '血糖监测指南',
    src: '//player.bilibili.com/player.html?isOutside=true&aid=287244587&bvid=BV1Ff4y1Q7tE&cid=236853503&p=1&danmaku=0&muted=0'
  },
  { 
    title: '糖尿病并发症防治',
    src: '//player.bilibili.com/player.html?isOutside=true&aid=113597556464857&bvid=BV1ddiDYPEFg&cid=27185578468&p=1&danmaku=0&muted=0'
  },
  { 
    title: '糖尿病日常护理',
    src: '//player.bilibili.com/player.html?isOutside=true&aid=112963746793555&bvid=BV1awexeTEnG&cid=500001649938450&p=1&danmaku=0&muted=0'
  }
])

const currentVideoIndex = ref(0)
const isPlaying = ref(false)
const editMode = ref(false)
const isAdmin = ref(false)
const commentContent = ref('')
const comments = ref([])
const showFloatingNav = ref(false)
const currentSection = ref('')
const showBackToTop = ref(false)
const videoContainer = ref(null)
const videoPlayer = ref(null)
const bilibiliIframe = ref(null)

// 音频上下文用于生成音效
let audioContext = null

// 播放滑动音效（侧边栏导航）
const playScrollSound = () => {
  try {
    if (!audioContext) {
      audioContext = new (window.AudioContext || window.webkitAudioContext)()
    }
    
    // 创建振荡器
    const oscillator = audioContext.createOscillator()
    const gainNode = audioContext.createGain()
    
    // 连接节点
    oscillator.connect(gainNode)
    gainNode.connect(audioContext.destination)
    
    // 设置音效参数 - 短促的滑动声
    oscillator.type = 'sine'
    oscillator.frequency.setValueAtTime(700, audioContext.currentTime)
    oscillator.frequency.exponentialRampToValueAtTime(350, audioContext.currentTime + 0.12)
    
    // 设置音量渐变
    gainNode.gain.setValueAtTime(0.12, audioContext.currentTime)
    gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.12)
    
    // 播放并在结束后清理
    oscillator.start(audioContext.currentTime)
    oscillator.stop(audioContext.currentTime + 0.12)
    
    // 确保音频上下文是激活状态
    if (audioContext.state === 'suspended') {
      audioContext.resume()
    }
  } catch (error) {
    console.log('音效播放失败（可选功能）:', error)
  }
}

// 播放返回顶部音效
const playBackToTopSound = () => {
  try {
    if (!audioContext) {
      audioContext = new (window.AudioContext || window.webkitAudioContext)()
    }
    
    // 创建两个振荡器形成和声效果
    const oscillator1 = audioContext.createOscillator()
    const oscillator2 = audioContext.createOscillator()
    const gainNode = audioContext.createGain()
    
    // 连接节点
    oscillator1.connect(gainNode)
    oscillator2.connect(gainNode)
    gainNode.connect(audioContext.destination)
    
    // 设置音效参数 - 上升的愉悦音效
    oscillator1.type = 'sine'
    oscillator2.type = 'sine'
    oscillator1.frequency.setValueAtTime(400, audioContext.currentTime)
    oscillator1.frequency.exponentialRampToValueAtTime(800, audioContext.currentTime + 0.2)
    oscillator2.frequency.setValueAtTime(600, audioContext.currentTime)
    oscillator2.frequency.exponentialRampToValueAtTime(1200, audioContext.currentTime + 0.2)
    
    // 设置音量渐变
    gainNode.gain.setValueAtTime(0.1, audioContext.currentTime)
    gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.2)
    
    // 播放并在结束后清理
    oscillator1.start(audioContext.currentTime)
    oscillator2.start(audioContext.currentTime)
    oscillator1.stop(audioContext.currentTime + 0.2)
    oscillator2.stop(audioContext.currentTime + 0.2)
    
    // 确保音频上下文是激活状态
    if (audioContext.state === 'suspended') {
      audioContext.resume()
    }
  } catch (error) {
    console.log('音效播放失败（可选功能）:', error)
  }
}

const selectVideo = (index) => {
  currentVideoIndex.value = index
  // 切换视频后再次确保不静音
  nextTick(() => {
    setBilibiliMuted(false)
  })
}

// 使用 postMessage API 设置 B 站播放器静音状态
const setBilibiliMuted = (muted) => {
  if (!bilibiliIframe.value || !bilibiliIframe.value.contentWindow) return
  
  try {
    // B 站播放器 postMessage API 消息格式
    const message = {
      type: 'cmd',
      data: {
        cmd: 'setMuted',
        value: muted
      }
    }
    bilibiliIframe.value.contentWindow.postMessage(message, 'https://player.bilibili.com')
  } catch (error) {
    console.error('设置 B 站播放器静音失败:', error)
  }
}

const loadComments = async () => {
  try {
    const res = await request.get('/education-comment/list');
    if (res.code === '200' || res.code === 200) {
      comments.value = res.data || [];
    }
  } catch (error) {
    console.error('加载留言失败:', error);
    comments.value = [];
  }
}

const scrollToTop = () => {
  playBackToTopSound();
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

const handleScroll = () => {
  const scrollY = window.scrollY;
  showFloatingNav.value = scrollY > 600;
  showBackToTop.value = scrollY > 500;
  updateCurrentSection();
}

const scrollToSection = (sectionId) => {
  const element = document.getElementById(sectionId);
  if (element) {
    playScrollSound();
    element.scrollIntoView({ behavior: 'smooth' });
  }
}

const submitComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入留言内容');
    return;
  }
  
  try {
    const res = await request.post('/education-comment/add', {
      content: commentContent.value
    });
    
    if (res.code === '200' || res.code === 200) {
      ElMessage.success('留言成功');
      commentContent.value = '';
      loadComments();
    } else {
      ElMessage.error(res.msg || '留言失败');
    }
  } catch (error) {
    ElMessage.error('留言失败');
    console.error(error);
  }
}

const playFullScreen = async () => {
  isPlaying.value = true;
  await nextTick()
  const video = videoPlayer.value;
  const container = videoContainer.value;

  if (!video || !container) return;

  const playPromise = video.play();
  if (playPromise !== undefined) {
    playPromise.catch(() => {
      console.warn('自动播放被阻止,请手动点击播放按钮');
    });
  }

  if (container.requestFullscreen) {
    container.requestFullscreen();
  } else if (container.webkitRequestFullscreen) {
    container.webkitRequestFullscreen();
  } else if (container.mozRequestFullScreen) {
    container.mozRequestFullScreen();
  }
}

const onVideoEnded = () => {
  exitVideo();
}

const exitVideo = () => {
  exitFullscreen();
  isPlaying.value = false;
}

const exitFullscreen = () => {
  if (document.exitFullscreen) {
    document.exitFullscreen();
  } else if (document.webkitExitFullscreen) {
    document.webkitExitFullscreen();
  } else if (document.mozCancelFullScreen) {
    document.mozCancelFullScreen();
  }
}

const updateCurrentSection = () => {
  const secs = sections.value;
  for (let i = secs.length - 1; i >= 0; i--) {
    const section = secs[i];
    const element = document.getElementById(section.id);
    if (element) {
      const rect = element.getBoundingClientRect();
      if (rect.top <= 100) {
        currentSection.value = section.id;
        break;
      }
    }
  }
}

const toggleEditMode = () => {
  editMode.value = !editMode.value;
}

onMounted(() => {
  isAdmin.value = true;
  loadComments();
  window.addEventListener('scroll', handleScroll);
  updateCurrentSection();
  
  // 页面加载后设置 B 站播放器不静音
  setTimeout(() => {
    setBilibiliMuted(false);
  }, 1000);
  
  // 监听 iframe 加载完成，再次设置不静音
  if (bilibiliIframe.value) {
    bilibiliIframe.value.onload = () => {
      setBilibiliMuted(false);
    };
  }
})

onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll);
})
</script>

<style scoped>
/* 全局样式 */
body, .diabetes-education-container {
  background-color: #F8FAFC;
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  color: #374151;
  line-height: 1.8;
}

.diabetes-education-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 头部样式 */
.header {
  text-align: center;
  margin-bottom: 40px;
  padding: 40px 0;
  background: linear-gradient(135deg, rgba(64, 128, 255, 0.1), rgba(82, 196, 26, 0.1));
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.header h1 {
  color: #1F2937;
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 16px;
}

.subtitle {
  color: #6B7280;
  font-size: 16px;
  max-width: 800px;
  margin: 0 auto;
}

/* 视频播放区域样式 */
.video-player-section {
  background: #FFFFFF;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.video-selector {
  display: flex;
  flex-wrap: nowrap;
  overflow-x: auto;
  gap: 12px;
  margin-bottom: 20px;
  padding: 4px 0;
  scrollbar-width: thin;
  scrollbar-color: rgba(64, 128, 255, 0.5) transparent;
}

.video-selector::-webkit-scrollbar {
  height: 6px;
}

.video-selector::-webkit-scrollbar-track {
  background: transparent;
}

.video-selector::-webkit-scrollbar-thumb {
  background: rgba(64, 128, 255, 0.4);
  border-radius: 3px;
}

.video-selector::-webkit-scrollbar-thumb:hover {
  background: rgba(64, 128, 255, 0.6);
}

.video-tab {
  padding: 8px 20px;
  border: none;
  border-radius: 24px;
  background: linear-gradient(135deg, #F3F4F6 0%, #E5E7EB 100%);
  color: #4B5563;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  white-space: nowrap;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.video-tab:hover {
  background: linear-gradient(135deg, rgba(64, 128, 255, 0.15) 0%, rgba(64, 128, 255, 0.08) 100%);
  color: #1d4ed8;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 128, 255, 0.2);
}

.video-tab.active {
  background: linear-gradient(135deg, #4080FF 0%, #2563EB 100%);
  color: white;
  box-shadow: 0 4px 16px rgba(64, 128, 255, 0.4);
  transform: translateY(-1px);
}

.video-container {
  width: 100%;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.bilibili-player {
  width: 100%;
  height: 500px;
  display: block;
}

@media (max-width: 768px) {
  .bilibili-player {
    height: 280px;
  }
}

/* 导航样式 */
.navigation {
  display: flex;
  overflow-x: auto;
  gap: 12px;
  padding: 16px;
  margin-bottom: 32px;
  background: linear-gradient(135deg, #FFFFFF 0%, #F8FAFC 100%);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  scrollbar-width: thin;
  scrollbar-color: rgba(64, 128, 255, 0.3) transparent;
}

.navigation::-webkit-scrollbar {
  height: 4px;
}

.navigation::-webkit-scrollbar-track {
  background: transparent;
}

.navigation::-webkit-scrollbar-thumb {
  background: rgba(64, 128, 255, 0.3);
  border-radius: 2px;
}

.nav-item {
  white-space: nowrap;
  padding: 10px 20px;
  background: linear-gradient(135deg, #F3F4F6 0%, #E5E7EB 100%);
  border-radius: 24px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  color: #4B5563;
  border: none;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.nav-item:hover {
  background: linear-gradient(135deg, rgba(64, 128, 255, 0.18) 0%, rgba(64, 128, 255, 0.08) 100%);
  color: #1d4ed8;
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(64, 128, 255, 0.25);
}

/* 章节样式 */
.section {
  background: linear-gradient(135deg, #FFFFFF 0%, #FDFEFE 100%);
  padding: 28px;
  margin: 18px 0;
  border-radius: 20px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(64, 128, 255, 0.05);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.section:hover {
  box-shadow: 0 8px 32px rgba(64, 128, 255, 0.12);
  transform: translateY(-4px);
  border-color: rgba(64, 128, 255, 0.15);
}

.section-title {
  color: #1F2937;
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.section-content h3 {
  color: #374151;
  font-size: 16px;
  font-weight: 600;
  margin: 20px 0 12px 0;
}

/* 表格样式 */
.table-container {
  overflow-x: auto;
  margin: 16px 0;
}

.info-table {
  width: 100%;
  border-collapse: collapse;
  margin: 16px 0;
  font-size: 14px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border-radius: 8px;
  overflow: hidden;
}

.info-table th,
.info-table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #E5E7EB;
}

.info-table th {
  background: rgba(64, 128, 255, 0.05);
  font-weight: 600;
  color: #4080FF;
}

.info-table tr:hover {
  background: rgba(64, 128, 255, 0.03);
}

/* 列表样式 */
.standard-list,
.symptom-list,
.risk-list,
.food-list,
.myth-list,
.benefit-list,
.note-list,
.insulin-list,
.complication-list,
.special-list,
.lifestyle-list,
.tech-list,
.summary-list {
  margin: 15px 0;
  padding-left: 25px;
}

.standard-list li,
.symptom-list li,
.risk-list li,
.food-list li,
.myth-list li,
.benefit-list li,
.note-list li,
.insulin-list li,
.complication-list li,
.special-list li,
.lifestyle-list li,
.tech-list li,
.summary-list li {
  margin-bottom: 8px;
  line-height: 1.5;
}

/* 图标样式 */
.icon {
  margin-right: 8px;
  font-size: 16px;
}

.icon.good {
  color: #52C41A;
}

.icon.bad {
  color: #FF7D00;
}

.icon.tip {
  color: #4080FF;
}

.icon.clock {
  color: #4080FF;
}

.icon.blood {
  color: #FF7D00;
}

.icon.shoe {
  color: #4080FF;
}

.icon.candy {
  color: #FF7D00;
}

/* 特殊盒子样式 */
.principle-box {
  background: rgba(82, 196, 26, 0.12);
  border-left: 3px solid #52C41A;
  padding: 15px;
  margin: 15px 0;
  border-radius: 8px;
}

.warning-box {
  background: rgba(255, 125, 0, 0.1);
  border-left: 3px solid #FF7D00;
  padding: 15px;
  margin: 15px 0;
  border-radius: 8px;
  color: #92400E;
}

.note-box {
  background: rgba(64, 128, 255, 0.1);
  border-left: 3px solid #4080FF;
  padding: 15px;
  margin: 15px 0;
  border-radius: 8px;
}

/* 时间点样式 */
.time-points {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin: 16px 0;
  padding: 12px;
  background: rgba(64, 128, 255, 0.05);
  border-radius: 8px;
}

.time-points span {
  padding: 4px 8px;
  background: #FFFFFF;
  border-radius: 4px;
  font-size: 13px;
  color: #4080FF;
  font-weight: 500;
  border: none;
}

/* 留言区样式 */
.comments-content {
  margin-top: 20px;
}

.comment-form {
  margin-bottom: 30px;
  padding: 20px;
  background: #FFFFFF;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.comment-form h3 {
  margin-bottom: 15px;
  color: #4B5563;
}

.comment-form textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #E5E7EB;
  border-radius: 8px;
  resize: vertical;
  font-family: inherit;
  font-size: 14px;
  transition: border-color 0.2s ease;
}

.comment-form textarea:focus {
  outline: none;
  border-color: #4080FF;
  box-shadow: 0 0 0 3px rgba(64, 128, 255, 0.1);
}

.submit-btn {
  margin-top: 12px;
  padding: 12px 28px;
  background: linear-gradient(135deg, #4080FF 0%, #2563EB 100%);
  color: white;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 14px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(64, 128, 255, 0.3);
}

.submit-btn:hover {
  background: linear-gradient(135deg, #2563EB 0%, #1d4ed8 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(64, 128, 255, 0.4);
}

.submit-btn:active {
  transform: translateY(0);
}

.comment-list h3 {
  margin-bottom: 15px;
  color: #4B5563;
}

.no-comments {
  text-align: center;
  padding: 40px;
  color: #718096;
  background: #F9FAFB;
  border-radius: 8px;
}

.comment-item {
  padding: 18px;
  margin-bottom: 14px;
  background: linear-gradient(135deg, #F9FAFB 0%, #F3F4F6 100%);
  border-radius: 12px;
  border-left: 4px solid #E5E7EB;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.comment-item:hover {
  background: linear-gradient(135deg, #F3F4F6 0%, #E5E7EB 100%);
  border-left-color: #4080FF;
  transform: translateX(4px);
  box-shadow: 0 4px 16px rgba(64, 128, 255, 0.12);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
}

.comment-user {
  font-weight: 600;
  color: #4080FF;
}

.comment-time {
  font-size: 12px;
  color: #9CA3AF;
}

.comment-body {
  color: #4B5563;
  line-height: 1.6;
}

/* 章节头部样式 */
.section-header {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(64, 128, 255, 0.2);
}

.section-title {
  color: #1F2937;
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.edit-btn {
  background: linear-gradient(135deg, #4080FF 0%, #5A8FFF 100%);
  border: none;
  font-size: 12px;
  padding: 6px 14px;
  border-radius: 8px;
  color: white;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 8px rgba(64, 128, 255, 0.3);
}

.edit-btn:hover {
  background: linear-gradient(135deg, #3366CC 0%, #4A7FEF 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 128, 255, 0.4);
}

/* 右侧悬浮导航栏 - 精修版 */
.floating-nav {
  position: fixed;
  right: 32px;
  top: 100px;
  bottom: 32px;
  width: 190px;
  padding: 20px 16px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.18), rgba(248, 250, 252, 0.15));
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(64, 128, 255, 0.12), 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(64, 128, 255, 0.08);
  overflow-y: auto;
  z-index: 1000;
  max-height: calc(100vh - 200px);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 美化导航栏滚动条 */
.floating-nav::-webkit-scrollbar {
  width: 6px;
}
.floating-nav::-webkit-scrollbar-track {
  background: transparent;
  margin: 8px 0;
}
.floating-nav::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, rgba(64, 128, 255, 0.3), rgba(64, 128, 255, 0.15));
  border-radius: 3px;
  transition: all 0.2s ease;
}
.floating-nav::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, rgba(64, 128, 255, 0.5), rgba(64, 128, 255, 0.3));
}

/* 顶部标题层级设计 */
.floating-nav h3 {
  font-size: 14px;
  font-weight: 700;
  margin: 0 0 18px 0;
  color: #1e40af;
  border-bottom: 2px solid rgba(64, 128, 255, 0.1);
  padding-bottom: 12px;
  letter-spacing: 0.5px;
  text-transform: uppercase;
  display: flex;
  align-items: center;
  gap: 8px;
}
.floating-nav h3::before {
  content: '';
  width: 4px;
  height: 16px;
  background: linear-gradient(180deg, #4080FF, #3366CC);
  border-radius: 2px;
}

.floating-nav ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.floating-nav li {
  margin-bottom: 6px;
}

/* 导航项基础状态 */
.floating-nav a {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  color: #1e293b;
  text-decoration: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: transparent;
  border: 1px solid transparent;
  position: relative;
  overflow: hidden;
  background: linear-gradient(180deg, rgba(193, 208, 241, 0.3), rgba(149, 167, 201, 0.4));
}
.floating-nav a::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%) scaleX(0);
  width: 3px;
  height: 20px;
  background: linear-gradient(180deg, #4080FF, #3366CC);
  border-radius: 0 2px 2px 0;
  transition: transform 0.3s ease;
}

/* 鼠标悬浮交互 */
.floating-nav a:hover {
  background: linear-gradient(135deg, rgba(64, 128, 255, 0.08), rgba(64, 128, 255, 0.02));
  color: #1d4ed8;
  transform: translateX(4px);
  box-shadow: 0 2px 8px rgba(64, 128, 255, 0.08);
}
.floating-nav a:hover::before {
  transform: translateY(-50%) scaleX(1);
}

/* 激活态高亮 */
.floating-nav a.active {
  background: linear-gradient(135deg, rgba(64, 128, 255, 0.1), rgba(64, 128, 255, 0.05));
  color: #1e40af;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(64, 128, 255, 0.1);
}
.floating-nav a.active::before {
  transform: translateY(-50%) scaleX(1);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .floating-nav {
    display: none;
  }
  
  .back-to-top {
    right: 16px;
    bottom: 16px;
  }
}

/* 返回顶部按钮 */
.back-to-top {
  position: fixed;
  right: 28px;
  bottom: 28px;
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.98) 0%, rgba(248, 250, 252, 0.98) 100%);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 50%;
  box-shadow: 0 8px 28px rgba(64, 128, 255, 0.35);
  border: 1px solid rgba(64, 128, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 1001;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  color: #4080FF;
}

.back-to-top .el-icon {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}

.back-to-top:hover {
  background: linear-gradient(135deg, #4080FF 0%, #2563EB 100%);
  color: white;
  transform: translateY(-4px) scale(1.08);
  box-shadow: 0 12px 36px rgba(64, 128, 255, 0.5);
}

.back-to-top:hover .el-icon {
  transform: translateY(-1px);
}

.back-to-top:active {
  transform: translateY(-2px) scale(0.98);
}

/* 返回顶部按钮动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .floating-nav {
    display: none;
  }
  
  .back-to-top {
    right: 16px;
    bottom: 16px;
  }
}

@media (max-width: 768px) {
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .edit-btn {
    align-self: flex-end;
  }
}

/* 视频科普样式 */
.video-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 20px 0;
}

.video-cover {
  position: relative;
  width: 600px;
  height: 340px;
  overflow: hidden;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.video-cover:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
}

.video-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.video-cover:hover img {
  transform: scale(1.08);
}

.play-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background-color: rgba(64, 128, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 32px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(64, 128, 255, 0.3);
}

.play-icon i {
  transform: translateX(4px);
}

.video-cover:hover .play-icon {
  background-color: #4080FF;
  transform: translate(-50%, -50%) scale(1.1);
  box-shadow: 0 6px 16px rgba(64, 128, 255, 0.4);
}

.video-info {
  width: 600px;
  padding: 20px;
  background: #FFFFFF;
  border-radius: 0 0 8px 8px;
  margin-top: -4px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.video-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 10px;
  color: #1F2937;
}

.video-desc {
  font-size: 14px;
  color: #6B7280;
  margin-bottom: 12px;
  line-height: 1.6;
}

/* 全屏视频容器 */
.fullscreen-video {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: #000;
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.fullscreen-video video {
  width: 95vw;
  height: 95vh;
  max-width: 1600px;
  max-height: 900px;
  outline: none;
}

/* 退出按钮 */
.exit-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.6);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  cursor: pointer;
  z-index: 2001;
  transition: background-color 0.3s ease;
}

.exit-btn:hover {
  background-color: rgba(229, 62, 62, 0.7);
}

/* 各篇章卡独特颜色主题 */
.section-basic {
  background: linear-gradient(135deg, #F0F7FF 0%, #E8F4FD 50%, #F5F8FF 100%);
  border: 1px solid rgba(59, 130, 246, 0.15);
  border-top: 3px solid #3B82F6;
}

.section-basic:hover {
  box-shadow: 0 8px 32px rgba(59, 130, 246, 0.15);
  border-color: rgba(59, 130, 246, 0.25);
}

.section-symptoms {
  background: linear-gradient(135deg, #FFF7ED 0%, #FFFBF5 50%, #FEF3E2 100%);
  border: 1px solid rgba(249, 115, 22, 0.15);
  border-top: 3px solid #F97316;
}

.section-symptoms:hover {
  box-shadow: 0 8px 32px rgba(249, 115, 22, 0.15);
  border-color: rgba(249, 115, 22, 0.25);
}

.section-diet {
  background: linear-gradient(135deg, #F0FDF4 0%, #F5FFF8 50%, #ECFDF5 100%);
  border: 1px solid rgba(34, 197, 94, 0.15);
  border-top: 3px solid #22C55E;
}

.section-diet:hover {
  box-shadow: 0 8px 32px rgba(34, 197, 94, 0.15);
  border-color: rgba(34, 197, 94, 0.25);
}

.section-exercise {
  background: linear-gradient(135deg, #FEF2F2 0%, #FFF5F5 50%, #FEE2E2 100%);
  border: 1px solid rgba(239, 68, 68, 0.15);
  border-top: 3px solid #EF4444;
}

.section-exercise:hover {
  box-shadow: 0 8px 32px rgba(239, 68, 68, 0.15);
  border-color: rgba(239, 68, 68, 0.25);
}

.section-medication {
  background: linear-gradient(135deg, #FDF4FF 0%, #FBF5FF 50%, #F3E8FF 100%);
  border: 1px solid rgba(168, 85, 247, 0.15);
  border-top: 3px solid #A855F7;
}

.section-medication:hover {
  box-shadow: 0 8px 32px rgba(168, 85, 247, 0.15);
  border-color: rgba(168, 85, 247, 0.25);
}

.section-monitoring {
  background: linear-gradient(135deg, #FFFBEB 0%, #FFFEF5 50%, #FEF3C7 100%);
  border: 1px solid rgba(245, 158, 11, 0.15);
  border-top: 3px solid #F59E0B;
}

.section-monitoring:hover {
  box-shadow: 0 8px 32px rgba(245, 158, 11, 0.15);
  border-color: rgba(245, 158, 11, 0.25);
}

.section-complications {
  background: linear-gradient(135deg, #F0F9FF 0%, #F5FBFF 50%, #E0F2FE 100%);
  border: 1px solid rgba(6, 182, 212, 0.15);
  border-top: 3px solid #06B6D4;
}

.section-complications:hover {
  box-shadow: 0 8px 32px rgba(6, 182, 212, 0.15);
  border-color: rgba(6, 182, 212, 0.25);
}

.section-special {
  background: linear-gradient(135deg, #FFF1F2 0%, #FFF5F5 50%, #FFE4E6 100%);
  border: 1px solid rgba(244, 63, 94, 0.15);
  border-top: 3px solid #F43F5E;
}

.section-special:hover {
  box-shadow: 0 8px 32px rgba(244, 63, 94, 0.15);
  border-color: rgba(244, 63, 94, 0.25);
}

.section-lifestyle {
  background: linear-gradient(135deg, #ECFDF5 0%, #F0FDF4 50%, #D1FAE5 100%);
  border: 1px solid rgba(16, 185, 129, 0.15);
  border-top: 3px solid #10B981;
}

.section-lifestyle:hover {
  box-shadow: 0 8px 32px rgba(16, 185, 129, 0.15);
  border-color: rgba(16, 185, 129, 0.25);
}

.section-advance {
  background: linear-gradient(135deg, #F5F3FF 0%, #FAF9FF 50%, #EDE9FE 100%);
  border: 1px solid rgba(139, 92, 246, 0.15);
  border-top: 3px solid #8B5CF6;
}

.section-advance:hover {
  box-shadow: 0 8px 32px rgba(139, 92, 246, 0.15);
  border-color: rgba(139, 92, 246, 0.25);
}

.section-video {
  background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 50%, #E2E8F0 100%);
  border: 1px solid rgba(100, 116, 139, 0.15);
  border-top: 3px solid #64748B;
}

.section-video:hover {
  box-shadow: 0 8px 32px rgba(100, 116, 139, 0.15);
  border-color: rgba(100, 116, 139, 0.25);
}

.section-summary {
  background: linear-gradient(135deg, #FEF3C7 0%, #FFFBEB 50%, #FDE68A 100%);
  border: 1px solid rgba(217, 119, 6, 0.2);
  border-top: 3px solid #D97706;
}

.section-summary:hover {
  box-shadow: 0 8px 32px rgba(217, 119, 6, 0.2);
  border-color: rgba(217, 119, 6, 0.3);
}

.section-comments {
  background: linear-gradient(135deg, #F0F9FF 0%, #E0F2FE 50%, #F0F9FF 100%);
  border: 1px solid rgba(14, 165, 233, 0.15);
  border-top: 3px solid #0EA5E9;
}

.section-comments:hover {
  box-shadow: 0 8px 32px rgba(14, 165, 233, 0.15);
  border-color: rgba(14, 165, 233, 0.25);
}

/* 章节标题颜色与主题呼应 */
.section-basic .section-title { color: #1E40AF; }
.section-symptoms .section-title { color: #9A3412; }
.section-diet .section-title { color: #166534; }
.section-exercise .section-title { color: #991B1B; }
.section-medication .section-title { color: #6B21A8; }
.section-monitoring .section-title { color: #92400E; }
.section-complications .section-title { color: #155E75; }
.section-special .section-title { color: #9F1239; }
.section-lifestyle .section-title { color: #065F46; }
.section-advance .section-title { color: #5B21B6; }
.section-video .section-title { color: #334155; }
.section-summary .section-title { color: #92400E; }
.section-comments .section-title { color: #0369A1; }

/* 章节头部边框与主题呼应 */
.section-basic .section-header { border-bottom-color: rgba(59, 130, 246, 0.2); }
.section-symptoms .section-header { border-bottom-color: rgba(249, 115, 22, 0.2); }
.section-diet .section-header { border-bottom-color: rgba(34, 197, 94, 0.2); }
.section-exercise .section-header { border-bottom-color: rgba(239, 68, 68, 0.2); }
.section-medication .section-header { border-bottom-color: rgba(168, 85, 247, 0.2); }
.section-monitoring .section-header { border-bottom-color: rgba(245, 158, 11, 0.2); }
.section-complications .section-header { border-bottom-color: rgba(6, 182, 212, 0.2); }
.section-special .section-header { border-bottom-color: rgba(244, 63, 94, 0.2); }
.section-lifestyle .section-header { border-bottom-color: rgba(16, 185, 129, 0.2); }
.section-advance .section-header { border-bottom-color: rgba(139, 92, 246, 0.2); }
.section-video .section-header { border-bottom-color: rgba(100, 116, 139, 0.2); }
.section-summary .section-header { border-bottom-color: rgba(217, 119, 6, 0.2); }
.section-comments .section-header { border-bottom-color: rgba(14, 165, 233, 0.2); }

</style>