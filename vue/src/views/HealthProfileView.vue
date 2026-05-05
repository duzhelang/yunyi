<template>
  <div class="health-hub-pro">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <h1>🏥 健康管理中心</h1>
      <p>智能自查 · 趋势追踪 · 医生协同</p>
    </div>
    <el-row :gutter="20">
      <!-- ================= 左侧：表单 + 建议 ================= -->
      <el-col :xs="24" :lg="16">
        <!-- 表单卡片 -->
        <el-card class="form-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon"><Edit /></el-icon>
              <span>健康指标自查</span>
              <el-tag size="small" type="success" effect="plain">
                <el-icon><User /></el-icon> 诊断员在线
              </el-tag>
            </div>
          </template>

          <el-form :model="form" label-width="140px" size="default" class="health-form">
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="年龄 (岁)" required>
                  <el-input-number v-model="form.Age" :min="1" :max="120" placeholder="岁" controls-position="right" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="身高 (cm)" required>
                  <el-input-number v-model="temp.height" :min="50" :max="250" placeholder="厘米" controls-position="right" @change="calcBMI" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="体重 (kg)" required>
                  <el-input-number v-model="temp.weight" :min="20" :max="300" placeholder="千克" controls-position="right" @change="calcBMI" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="BMI (自动计算)" required>
              <el-input v-model="form.BMI" disabled placeholder="输入身高体重自动计算">
                <template #append>kg/m²</template>
              </el-input>
              <div v-if="bmiFeedback.text" class="feedback-tag" :class="bmiFeedback.class">
                <el-icon><component :is="bmiFeedback.icon" /></el-icon>
                {{ bmiFeedback.text }}
              </div>
            </el-form-item>

            <el-divider content-position="left">
              <span class="section-divider">🩺 核心医疗指标</span>
            </el-divider>

            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item required>
                  <template #label>
                    <el-tooltip content="禁食8小时后的血糖水平，反映基础胰岛素分泌功能，是糖尿病筛查的核心指标。正常值：70-99 mg/dL" placement="top" :show-after="300">
                      <span class="label-with-tip">空腹血糖 (mg/dL) <el-icon class="tip-icon"><InfoFilled /></el-icon></span>
                    </el-tooltip>
                  </template>
                  <el-input-number v-model="form.Glucose" :precision="1" :min="0" :max="500" placeholder="mg/dL" controls-position="right" @change="evaluateGlucose" />
                  <div v-if="glucoseFeedback.text" class="feedback-tag" :class="glucoseFeedback.class">
                    <el-icon><component :is="glucoseFeedback.icon" /></el-icon>
                    {{ glucoseFeedback.text }}
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item>
                  <template #label>
                    <el-tooltip content="舒张压，反映血管弹性和心脏舒张期压力。高血压是糖尿病并发症的重要危险因素。正常值：&lt;80 mmHg" placement="top" :show-after="300">
                      <span class="label-with-tip">血压 (mmHg) <el-icon class="tip-icon"><InfoFilled /></el-icon></span>
                    </el-tooltip>
                  </template>
                  <el-input-number v-model="form.BloodPressure" :min="40" :max="200" placeholder="mmHg" controls-position="right" @change="evaluateBP" />
                  <div v-if="bpFeedback.text" class="feedback-tag" :class="bpFeedback.class">
                    <el-icon><component :is="bpFeedback.icon" /></el-icon>
                    {{ bpFeedback.text }}
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item>
                  <template #label>
                    <el-tooltip content="血清胰岛素水平，反映胰岛β细胞的分泌能力。2型糖尿病早期常出现高胰岛素血症，后期则分泌不足。正常值：16-166 mU/L" placement="top" :show-after="300">
                      <span class="label-with-tip">胰岛素 (mU/L) <el-icon class="tip-icon"><InfoFilled /></el-icon></span>
                    </el-tooltip>
                  </template>
                  <el-input-number v-model="form.Insulin" :precision="1" :min="0" :max="200" placeholder="mU/L" controls-position="right" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item>
                  <template #label>
                    <el-tooltip content="肱三头肌皮褶厚度，用于估算体脂百分比。肥胖是2型糖尿病的主要风险因素之一，皮褶厚度可间接反映肥胖程度。" placement="top" :show-after="300">
                      <span class="label-with-tip">皮褶厚度 (mm) <el-icon class="tip-icon"><InfoFilled /></el-icon></span>
                    </el-tooltip>
                  </template>
                  <el-input-number v-model="form.SkinThickness" :min="0" :max="100" placeholder="mm" controls-position="right" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item required>
                  <template #label>
                    <el-tooltip content="基于家族糖尿病史的遗传风险指数，综合考虑亲属患病情况和发病年龄。取值范围：0.08-2.42，值越高遗传风险越大。" placement="top" :show-after="300">
                      <span class="label-with-tip">糖尿病谱系函数 <el-icon class="tip-icon"><InfoFilled /></el-icon></span>
                    </el-tooltip>
                  </template>
                  <div class="dpf-input-group">
                    <el-input-number v-model="form.DiabetesPedigreeFunction" :precision="3" :step="0.01" :min="0.08" :max="2.42" controls-position="right" />
                    <el-button type="primary" plain size="small" @click="dpfDialogVisible = true" class="dpf-calc-btn">
                      <el-icon><Edit /></el-icon> 计算
                    </el-button>
                    <el-button type="info" plain size="small" @click="dpfInfoDialogVisible = true" class="dpf-info-btn">
                      <el-icon><InfoFilled /></el-icon> 说明
                    </el-button>
                  </div>
                  <div class="helper-text">* 家族遗传系数，点击"计算"自动评估</div>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item>
                  <template #label>
                    <el-tooltip content="妊娠次数（含流产），妊娠期糖尿病史是2型糖尿病的独立风险因素，多次妊娠可增加患病概率。" placement="top" :show-after="300">
                      <span class="label-with-tip">怀孕次数 <el-icon class="tip-icon"><InfoFilled /></el-icon></span>
                    </el-tooltip>
                  </template>
                  <el-input-number v-model="form.Pregnancies" :min="0" :max="20" placeholder="次" controls-position="right" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-divider content-position="left">
              <span class="section-divider">📝 补充信息</span>
            </el-divider>

            <el-form-item label="自觉症状">
              <el-input v-model="form.symptoms" type="textarea" :rows="2" placeholder="如有多饮、多尿等症状请描述..." />
            </el-form-item>
            <el-form-item label="上传化验单">
              <el-upload ref="uploadRef" action="#" :auto-upload="false" :on-change="handleFileChange" :limit="1" :file-list="fileList" accept=".jpg,.jpeg,.png,.pdf,.csv">
                <el-button type="primary" plain>
                  <el-icon><Upload /></el-icon> 选择文件
                </el-button>
                <template #tip>
                  <div class="el-upload__tip">支持 jpg、png、pdf、csv 格式</div>
                </template>
              </el-upload>
            </el-form-item>
          </el-form>

          <!-- 操作按钮 -->
          <div class="action-bar">
            <el-button @click="resetForm" plain :disabled="saving || submitting || predicting">
              <el-icon><RefreshRight /></el-icon> 重置
            </el-button>
            <el-button type="success" @click="saveOnly" :loading="saving" :disabled="submitting || predicting">
              <el-icon><Coin /></el-icon> 保存档案
            </el-button>
            <el-button type="warning" @click="quickPredict" :loading="predicting" :disabled="saving || submitting">
              <el-icon><DataBoard /></el-icon> 快速检测
            </el-button>
            <el-button type="danger" @click="saveAndPredict" :loading="saving || predicting" :disabled="saving || submitting || predicting">
              <el-icon><DataAnalysis /></el-icon> 保存并检测
            </el-button>
            <el-button type="primary" @click="submitToDoctor(null)" :loading="submitting" :disabled="saving || predicting">
              <el-icon><Promotion /></el-icon> 发送诊断员
            </el-button>
          </div>
        </el-card>

        <!-- 日常建议卡片 -->
        <el-card class="advice-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon"><Document /></el-icon>
              <span>个性化健康建议</span>
            </div>
          </template>
          <div class="advice-grid" v-if="currentAdvice">
            <div class="advice-item" v-for="(advice, idx) in currentAdvice" :key="idx">
              <div class="advice-number">{{ idx + 1 }}</div>
              <div class="advice-content">
                <h4>{{ advice.title }}</h4>
                <p>{{ advice.content }}</p>
              </div>
            </div>
          </div>
          <div v-else class="empty-advice">
            <el-icon class="empty-icon"><ChatDotRound /></el-icon>
            <p>保存档案后将获取个性化健康建议</p>
          </div>
        </el-card>
      </el-col>

      <!-- ================= 右侧：图表 + 历史 + 工具 ================= -->
      <el-col :xs="24" :lg="8">
        <!-- 趋势图表卡片 -->
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon"><DataAnalysis /></el-icon>
              <span>健康趋势追踪</span>
            </div>
          </template>
          <el-radio-group v-model="chartMode" size="small" class="chart-tabs">
            <el-radio-button value="glucose">血糖曲线</el-radio-button>
            <el-radio-button value="bmi">BMI 趋势</el-radio-button>
          </el-radio-group>
          <div ref="chartRef" class="chart-box"></div>
          <div v-if="historyList.length === 0" class="empty-chart">
            <el-icon class="empty-icon"><TrendCharts /></el-icon>
            <p>暂无数据，请先保存档案</p>
          </div>
          <div class="chart-legend" v-else>
            <span class="legend-item">
              <span class="legend-dot" :style="{ background: chartMode === 'glucose' ? '#E6A23C' : '#409EFF' }"></span>
              {{ chartMode === 'glucose' ? '空腹血糖 (mg/dL)' : 'BMI 指数' }}
            </span>
            <span class="legend-item" v-if="chartMode === 'glucose'">
              <span class="legend-line"></span>
              正常上限: 110
            </span>
          </div>
        </el-card>

        <!-- 历史记录卡片 -->
        <el-card class="history-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon"><Clock /></el-icon>
              <span>自查历史</span>
              <span class="history-count">{{ historyList.length }} 条记录</span>
            </div>
          </template>
          <div class="history-list">
            <div v-if="historyList.length === 0" class="empty-state">
              <el-icon class="empty-icon"><Document /></el-icon>
              <p>暂无历史记录</p>
            </div>
            <el-scrollbar height="350px" v-else>
              <div
                v-for="item in historyList"
                :key="item.id"
                class="history-item"
                :class="{ 'is-active': selectedHistoryId === item.id }"
                @click="selectHistory(item)"
              >
                <div class="item-header">
                  <el-tag :type="STATUS_MAP[item.status]?.type" size="small" effect="dark">
                    {{ STATUS_MAP[item.status]?.text }}
                  </el-tag>
                  <span class="time">{{ formatTime(item.createTime) }}</span>
                </div>
                <div class="item-body">
                  <div class="item-metrics">
                    <span class="metric">
                      <el-icon><Sugar /></el-icon>
                      {{ item.glucose || '-' }}
                    </span>
                    <span class="metric">
                      <el-icon><ScaleToOriginal /></el-icon>
                      {{ item.bmi || '-' }}
                    </span>
                    <span class="metric">
                      <el-icon><User /></el-icon>
                      {{ item.age || '-' }}岁
                    </span>
                  </div>
                </div>
                <div v-if="item.status === 'DONE' && item.diagnosisResult" class="diagnosis-box">
                  <el-icon><Check /></el-icon>
                  {{ item.diagnosisResult }}
                </div>
                <div class="item-actions">
                  <el-button link size="small" type="primary" @click.stop="loadHistoryToForm(item)">
                    <el-icon><Edit /></el-icon> 载入
                  </el-button>
                  <div class="action-right">
                    <el-button
                      type="primary"
                      size="small"
                      round
                      @click.stop="submitToDoctor(item.id)"
                      :loading="submittingId === item.id"
                      :disabled="item.status === 'PENDING'"
                    >
                      {{ item.status === 'PENDING' ? '等待中' : '咨询' }}
                    </el-button>
                    <el-popconfirm title="确认删除此记录？" confirm-button-text="删除" @confirm="deleteHistory(item.id)">
                      <template #reference>
                        <el-button type="danger" size="small" plain :loading="deletingId === item.id" @click.stop>
                          <el-icon><Delete /></el-icon>
                        </el-button>
                      </template>
                    </el-popconfirm>
                  </div>
                </div>
              </div>
            </el-scrollbar>
          </div>
        </el-card>

        <!-- 工具箱卡片 -->
        <el-card class="tool-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon"><Tools /></el-icon>
              <span>健康工具</span>
            </div>
          </template>
          <div class="tool-grid">
            <div class="tool-item" @click="openTool('emergency')">
              <div class="icon-box bg-red"><el-icon><Warning /></el-icon></div>
              <div class="tool-info">
                <span class="tool-name">低血糖急救</span>
                <span class="tool-desc">急救处理指南</span>
              </div>
              <el-icon class="tool-arrow"><ArrowRight /></el-icon>
            </div>
            <div class="tool-item" @click="openTool('carb-count')">
              <div class="icon-box bg-orange"><el-icon><Dish /></el-icon></div>
              <div class="tool-info">
                <span class="tool-name">碳水计数法</span>
                <span class="tool-desc">饮食控制助手</span>
              </div>
              <el-icon class="tool-arrow"><ArrowRight /></el-icon>
            </div>
            <div class="tool-item" @click="openTool('foot-care')">
              <div class="icon-box bg-green"><el-icon><Cpu /></el-icon></div>
              <div class="tool-info">
                <span class="tool-name">足部护理</span>
                <span class="tool-desc">日常护理指南</span>
              </div>
              <el-icon class="tool-arrow"><ArrowRight /></el-icon>
            </div>
          </div>
        </el-card>

        <!-- 每日打卡卡片 -->
        <el-card class="checkin-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon"><Calendar /></el-icon>
              <span>今日打卡</span>
              <el-tag type="success" size="small">{{ progressPercent }}%</el-tag>
            </div>
          </template>
          <div class="checkin-list">
            <div
              v-for="(task, idx) in checkList"
              :key="idx"
              class="checkin-item"
              :class="{ done: task.done }"
              @click="toggleCheck(idx)"
            >
              <el-icon class="check-icon" :class="task.done ? 'is-checked' : ''">
                <CircleCheck v-if="task.done" />
                <CircleCheck v-else />
              </el-icon>
              <span>{{ task.text }}</span>
            </div>
          </div>
          <el-progress :percentage="progressPercent" :stroke-width="10" color="#67C23A" :show-text="false" />
        </el-card>
      </el-col>
    </el-row>

    <!-- ================= 糖尿病谱系函数计算弹窗（组件化） ================= -->
    <DpfCalculator v-model="store.diabetesPedigreeFunction" v-model:visible="dpfDialogVisible" />

    <!-- ================= 糖尿病谱系函数说明弹窗 ================= -->
    <el-dialog v-model="dpfInfoDialogVisible" title="糖尿病谱系函数说明" width="620px" custom-class="dpf-info-dialog">
      <div class="dpf-info-content">
        <div class="dpf-info-header">
          <div class="dpf-info-icon">🧬</div>
          <h3>糖尿病谱系函数（DPF）</h3>
          <p class="dpf-info-subtitle">Diabetes Pedigree Function</p>
        </div>
        <div class="dpf-info-section">
          <h4><el-icon><Document /></el-icon> 什么是糖尿病谱系函数？</h4>
          <p>糖尿病谱系函数是一个用于评估个体患糖尿病遗传风险的重要指标。它主要用于量化家族糖尿病史对个人患病风险的影响程度。</p>
        </div>
        <div class="dpf-info-section">
          <h4><el-icon><DataAnalysis /></el-icon> 主要功能和意义</h4>
          <div class="dpf-info-list">
            <div class="dpf-info-item">
              <span class="dpf-info-num">1</span>
              <div>
                <strong>遗传风险评估</strong>
                <p>通过分析家族中糖尿病患者的分布情况，计算出一个综合指数，反映个体因遗传因素导致的糖尿病风险。</p>
              </div>
            </div>
            <div class="dpf-info-item">
              <span class="dpf-info-num">2</span>
              <div>
                <strong>取值范围：0.08 - 2.42</strong>
                <p>数值越高表示遗传风险越大。</p>
                <div class="dpf-risk-levels">
                  <span class="risk-tag low">低风险：0.08-0.4</span>
                  <span class="risk-tag medium">中等风险：0.4-0.8</span>
                  <span class="risk-tag high">高风险：&gt;0.8</span>
                </div>
              </div>
            </div>
            <div class="dpf-info-item">
              <span class="dpf-info-num">3</span>
              <div>
                <strong>计算方法</strong>
                <p>基于家族成员（如父母、兄弟姐妹、祖父母等）的糖尿病发病年龄和亲属关系程度进行加权计算。</p>
              </div>
            </div>
            <div class="dpf-info-item">
              <span class="dpf-info-num">4</span>
              <div>
                <strong>临床应用</strong>
                <ul>
                  <li>在糖尿病风险预测模型中作为重要特征</li>
                  <li>帮助医生评估患者的遗传易感性</li>
                  <li>指导高风险人群的预防措施</li>
                </ul>
              </div>
            </div>
            <div class="dpf-info-item">
              <span class="dpf-info-num">5</span>
              <div>
                <strong>与其他风险因素的关系</strong>
                <p>糖尿病谱系函数通常与年龄、BMI、血糖水平、血压等因素一起，综合评估个体的糖尿病风险。它是预测模型的重要输入特征之一，与其他生理指标一起，帮助更准确地评估您的糖尿病风险水平。</p>
              </div>
            </div>
          </div>
        </div>
        <div class="dpf-info-section dpf-info-note">
          <el-alert type="info" :closable="false" show-icon>
            <template #title>
              <span>本计算基于 WHO 和 ADA 糖尿病风险评估指南，结果仅供参考，不构成医疗诊断依据。</span>
            </template>
          </el-alert>
        </div>
      </div>
      <template #footer>
        <el-button type="primary" @click="dpfInfoDialogVisible = false">我知道了</el-button>
      </template>
    </el-dialog>

    <!-- ================= 低血糖急救弹窗 ================= -->
    <el-dialog v-model="emergencyDialogVisible" title="低血糖急救" width="680px" custom-class="tool-dialog" :close-on-click-modal="false">
      <div class="tool-dialog-body">
        <el-alert title="低血糖可能危及生命，请立即采取行动！" type="warning" :closable="false" show-icon class="tool-alert" />
        <el-tabs v-model="emergencyTab" class="tool-tabs">
          <el-tab-pane label="症状识别" name="identify">
            <div class="severity-grid">
              <div class="severity-card mild" :class="{ selected: emergencySeverity === 'mild' }" @click="emergencySeverity = 'mild'">
                <div class="severity-icon">😰</div>
                <h4>轻度</h4>
                <ul>
                  <li>饥饿感</li>
                  <li>手抖、出汗</li>
                  <li>心慌、焦虑</li>
                  <li>面色苍白</li>
                </ul>
                <span class="severity-range">血糖 &lt; 3.9 mmol/L</span>
              </div>
              <div class="severity-card moderate" :class="{ selected: emergencySeverity === 'moderate' }" @click="emergencySeverity = 'moderate'">
                <div class="severity-icon">😵</div>
                <h4>中度</h4>
                <ul>
                  <li>头晕、头痛</li>
                  <li>视物模糊</li>
                  <li>注意力不集中</li>
                  <li>言语不清</li>
                </ul>
                <span class="severity-range">血糖 &lt; 2.8 mmol/L</span>
              </div>
              <div class="severity-card severe" :class="{ selected: emergencySeverity === 'severe' }" @click="emergencySeverity = 'severe'">
                <div class="severity-icon">🚨</div>
                <h4>重度</h4>
                <ul>
                  <li>意识模糊</li>
                  <li>抽搐</li>
                  <li>昏迷</li>
                  <li>无法吞咽</li>
                </ul>
                <span class="severity-range">需要他人帮助</span>
              </div>
            </div>
            <el-alert v-if="emergencySeverity === 'severe'" title="重度低血糖：立即拨打 120！不要喂食！保持侧卧位！" type="error" :closable="false" show-icon class="tool-alert" />
          </el-tab-pane>
          <el-tab-pane label="15-15 急救步骤" name="steps">
            <div class="emergency-steps">
              <div class="step-item" :class="{ active: emergencyStep >= 1 }">
                <span class="step-num">1</span>
                <div class="step-content">
                  <h4>确认意识清醒，能够吞咽</h4>
                  <p>如果意识不清或无法吞咽，立即拨打 120，不要喂食任何东西</p>
                </div>
              </div>
              <div class="step-arrow">⬇</div>
              <div class="step-item" :class="{ active: emergencyStep >= 2 }">
                <span class="step-num">2</span>
                <div class="step-content">
                  <h4>立即摄入 15g 快速升糖食物</h4>
                  <p>选择以下任意一种：</p>
                  <div class="food-options" @click="emergencyStep = Math.max(emergencyStep, 2)">
                    <span class="food-chip">半杯果汁（120ml）</span>
                    <span class="food-chip">3-4 颗葡萄糖片</span>
                    <span class="food-chip">1 汤匙蜂蜜/糖浆</span>
                    <span class="food-chip">半罐含糖汽水</span>
                    <span class="food-chip">4-5 块方糖</span>
                    <span class="food-chip">1 管葡萄糖凝胶</span>
                  </div>
                </div>
              </div>
              <div class="step-arrow">⬇</div>
              <div class="step-item" :class="{ active: emergencyStep >= 3 }">
                <span class="step-num">3</span>
                <div class="step-content">
                  <h4>等待 15 分钟 ⏱</h4>
                  <p>设好计时器，不要急于再次进食</p>
                  <el-button type="primary" size="small" @click="startEmergencyTimer" :disabled="emergencyTimerRunning">
                    {{ emergencyTimerRunning ? `计时中 ${emergencyCountdown}s` : '开始 15 分钟计时' }}
                  </el-button>
                </div>
              </div>
              <div class="step-arrow">⬇</div>
              <div class="step-item" :class="{ active: emergencyStep >= 4 }">
                <span class="step-num">4</span>
                <div class="step-content">
                  <h4>复测血糖</h4>
                  <p>如果血糖仍 &lt; 3.9 mmol/L，重复步骤 2-3</p>
                  <p>如果恢复正常，摄入少量复合碳水（如全麦饼干）维持血糖</p>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="预防建议" name="prevent">
            <div class="prevent-list">
              <div class="prevent-item">
                <span class="prevent-icon">📋</span>
                <div><strong>规律进餐</strong><p>不要跳过正餐，每 4-5 小时进食一次</p></div>
              </div>
              <div class="prevent-item">
                <span class="prevent-icon">💊</span>
                <div><strong>药物管理</strong><p>按时按量用药，运动前咨询医生调整剂量</p></div>
              </div>
              <div class="prevent-item">
                <span class="prevent-icon">🏃</span>
                <div><strong>运动前准备</strong><p>运动前测血糖，随身携带快速升糖食品</p></div>
              </div>
              <div class="prevent-item">
                <span class="prevent-icon">🌙</span>
                <div><strong>夜间低血糖</strong><p>睡前测血糖，必要时加餐，床头备糖块</p></div>
              </div>
              <div class="prevent-item">
                <span class="prevent-icon">📱</span>
                <div><strong>医疗警示</strong><p>佩戴医疗警示手环，告知家人急救方法</p></div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
      <template #footer>
        <el-button @click="emergencyDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="emergencyStep = 1; emergencySeverity = 'mild'">重置急救步骤</el-button>
      </template>
    </el-dialog>

    <!-- ================= 碳水计数法弹窗 ================= -->
    <el-dialog v-model="carbDialogVisible" title="碳水计数法" width="680px" custom-class="tool-dialog" :close-on-click-modal="false">
      <div class="tool-dialog-body">
        <el-tabs v-model="carbTab" class="tool-tabs">
          <el-tab-pane label="碳水计算器" name="calculator">
            <div class="carb-calculator">
              <div class="calc-form">
                <el-form label-width="100px" size="small">
                  <el-form-item label="食物名称">
                    <el-select v-model="selectedFood" filterable placeholder="搜索或选择食物" style="width:100%">
                      <el-option-group v-for="group in foodGroups" :key="group.label" :label="group.label">
                        <el-option v-for="food in group.foods" :key="food.name" :label="food.name" :value="food.name" />
                      </el-option-group>
                    </el-select>
                  </el-form-item>
                  <el-form-item label="摄入重量">
                    <el-input-number v-model="foodWeight" :min="10" :max="1000" :step="10" controls-position="right" style="width:100%">
                      <template #suffix>g</template>
                    </el-input-number>
                  </el-form-item>
                  <el-form-item label="计算结果">
                    <div class="calc-result" v-if="selectedFood">
                      <div class="result-row">
                        <span class="result-label">碳水化合物</span>
                        <span class="result-value">{{ calcCarbs }} g</span>
                      </div>
                      <div class="result-row">
                        <span class="result-label">碳水份数</span>
                        <span class="result-value">{{ calcServings }} 份</span>
                      </div>
                      <div class="result-row">
                        <span class="result-label">估算热量</span>
                        <span class="result-value">{{ calcCalories }} kcal</span>
                      </div>
                      <el-progress :percentage="dailyCarbPercent" :stroke-width="12" :text-inside="true" :status="dailyCarbPercent > 100 ? 'exception' : 'success'">
                        {{ dailyCarbPercent }}% 每日推荐
                      </el-progress>
                    </div>
                    <div v-else class="calc-hint">请先选择食物</div>
                  </el-form-item>
                </el-form>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="食物参考" name="reference">
            <el-collapse v-model="activeFoodGroup" class="food-collapse">
              <el-collapse-item v-for="group in foodGroups" :key="group.label" :title="group.label + '（' + group.foods.length + '种）'" :name="group.label">
                <div class="food-table">
                  <div class="food-table-header">
                    <span>食物</span><span>份量 (g)</span><span>碳水 (g)</span><span>碳水份数</span>
                  </div>
                  <div v-for="food in group.foods" :key="food.name" class="food-table-row" @click="selectedFood = food.name; foodWeight = food.standardGram">
                    <span>{{ food.name }}</span>
                    <span>{{ food.standardGram }}g</span>
                    <span>{{ food.carbPerStandard }}g</span>
                    <span>{{ food.servings }}</span>
                  </div>
                </div>
              </el-collapse-item>
            </el-collapse>
          </el-tab-pane>
          <el-tab-pane label="每日建议" name="guide">
            <div class="carb-guide">
              <div class="guide-card">
                <h4>每日碳水推荐摄入量</h4>
                <div class="guide-grid">
                  <div class="guide-item">
                    <span class="guide-value">130g</span>
                    <span class="guide-label">基础需要</span>
                  </div>
                  <div class="guide-item highlight">
                    <span class="guide-value">45-65%</span>
                    <span class="guide-label">占总热量</span>
                  </div>
                  <div class="guide-item">
                    <span class="guide-value">30-45g</span>
                    <span class="guide-label">每餐推荐</span>
                  </div>
                  <div class="guide-item">
                    <span class="guide-value">15-30g</span>
                    <span class="guide-label">每加餐推荐</span>
                  </div>
                </div>
              </div>
              <div class="guide-tips">
                <h4>💡 控糖技巧</h4>
                <ul>
                  <li>每餐保持一致的碳水摄入量，有助于血糖稳定</li>
                  <li>优先选择低升糖指数（GI）的复合碳水</li>
                  <li>1 份碳水 = 15g 碳水化合物，方便快速估算</li>
                  <li>餐后 2 小时测血糖，评估该餐碳水量是否合适</li>
                  <li>使用"餐盘法"：1/2 蔬菜、1/4 蛋白、1/4 碳水</li>
                </ul>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-dialog>

    <!-- ================= 足部护理弹窗 ================= -->
    <el-dialog v-model="footDialogVisible" title="足部护理指南" width="680px" custom-class="tool-dialog" :close-on-click-modal="false">
      <div class="tool-dialog-body">
        <el-alert title="糖尿病足是严重并发症，每日足部检查至关重要！" type="warning" :closable="false" show-icon class="tool-alert" />
        <el-tabs v-model="footTab" class="tool-tabs">
          <el-tab-pane label="每日自检" name="checklist">
            <p class="section-desc">每天完成以下检查，预防足部问题</p>
            <div class="foot-checklist">
              <div v-for="(item, idx) in footChecklist" :key="idx" class="checklist-item" :class="{ done: item.done }" @click="item.done = !item.done">
                <el-icon class="check-icon">
                  <CircleCheck v-if="item.done" />
                  <CircleCheck v-else />
                </el-icon>
                <div class="checklist-content">
                  <strong>{{ item.title }}</strong>
                  <p>{{ item.desc }}</p>
                </div>
              </div>
            </div>
            <el-progress :percentage="footCheckPercent" :stroke-width="14" :text-inside="true" :status="footCheckPercent === 100 ? 'success' : ''" class="foot-progress">
              {{ footCheckPercent }}% 已完成
            </el-progress>
          </el-tab-pane>
          <el-tab-pane label="护理步骤" name="care">
            <div class="care-steps">
              <div v-for="(step, idx) in footCareSteps" :key="idx" class="care-step-item">
                <div class="care-step-header">
                  <span class="care-step-num">{{ idx + 1 }}</span>
                  <h4>{{ step.title }}</h4>
                </div>
                <p>{{ step.content }}</p>
                <div class="care-tips" v-if="step.tips">
                  <span class="tip-label">小贴士：</span>{{ step.tips }}
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="警示信号" name="warning">
            <div class="warning-grid">
              <div class="warning-card">
                <span class="warning-icon">🩹</span>
                <h4>伤口不愈合</h4>
                <p>超过 3 天不愈合的小伤口应及时就医</p>
              </div>
              <div class="warning-card">
                <span class="warning-icon">🔴</span>
                <h4>红肿热痛</h4>
                <p>感染迹象，需要专业处理</p>
              </div>
              <div class="warning-card">
                <span class="warning-icon">💧</span>
                <h4>水泡/溃疡</h4>
                <p>不要自行处理，立即就医</p>
              </div>
              <div class="warning-card">
                <span class="warning-icon">🌡️</span>
                <h4>足部发热</h4>
                <p>局部温度升高可能是感染</p>
              </div>
              <div class="warning-card">
                <span class="warning-icon">🦶</span>
                <h4>足部变形</h4>
                <p>跖骨头突出、锤状趾等</p>
              </div>
              <div class="warning-card">
                <span class="warning-icon">🫨</span>
                <h4>感觉异常</h4>
                <p>麻木、刺痛、蚁走感</p>
              </div>
            </div>
            <el-alert title="发现以上任何症状，请立即就诊内分泌科或足病门诊" type="error" :closable="false" show-icon class="tool-alert" />
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-dialog>

    <!-- ================= 风险检测结果弹窗 ================= -->
    <el-dialog v-model="resultDialogVisible" width="750px" :close-on-click-modal="false" custom-class="result-dialog" :show-close="true">
      <div class="result-container">
        <div class="result-header" :class="getRiskClass(store.riskLevel)">
          <div class="result-header-main">
            <span class="risk-level-badge" :class="getRiskClass(store.riskLevel)">{{ getRiskText(store.riskLevel) }}</span>
            <span class="probability-main">
              <span class="probability-label">患病概率</span>
              <span class="probability-value">{{ store.riskProbability }}%</span>
            </span>
          </div>
          <div v-if="store.confidenceInterval[0] > 0" class="confidence-interval">
            置信区间: {{ store.confidenceInterval[0] }}% - {{ store.confidenceInterval[1] }}%
          </div>
        </div>

        <div class="charts-carousel" v-if="availableCharts.length > 0">
          <div class="carousel-header">
            <h4>可视化分析</h4>
            <span class="carousel-indicators">
              <span v-for="(chart, index) in availableCharts" :key="index"
                :class="['indicator-dot', { active: currentChartIndex === index }]"
                @click="goToChart(index)"></span>
            </span>
          </div>
          <div class="carousel-main">
            <button class="carousel-btn prev" @click="prevChart" :disabled="availableCharts.length <= 1">
              <el-icon><ArrowLeft /></el-icon>
            </button>
            <div class="carousel-display" @click="previewCurrentChart">
              <img v-if="availableCharts[currentChartIndex]" :src="'data:image/png;base64,' + availableCharts[currentChartIndex].image" :alt="availableCharts[currentChartIndex].label" />
              <div class="carousel-hint-text">点击查看大图</div>
            </div>
            <button class="carousel-btn next" @click="nextChart" :disabled="availableCharts.length <= 1">
              <el-icon><ArrowRight /></el-icon>
            </button>
          </div>
          <div class="carousel-thumbnails">
            <div v-for="(chart, index) in availableCharts" :key="index"
              :class="['thumbnail', { active: currentChartIndex === index }]"
              @click="goToChart(index)">
              <img :src="'data:image/png;base64,' + chart.image" :alt="chart.label" />
              <span class="thumbnail-label">{{ chart.label }}</span>
            </div>
          </div>
        </div>

        <div class="collapse-panel data-details">
          <div class="panel-header" @click="showDataDetails = !showDataDetails">
            <span class="panel-title">数据详情</span>
            <span class="panel-arrow" :class="{ expanded: showDataDetails }"><el-icon><ArrowDown /></el-icon></span>
          </div>
          <Transition name="panel-slide">
            <div class="panel-content" v-show="showDataDetails">
              <el-descriptions :column="2" size="small" border>
                <el-descriptions-item label="年龄">{{ store.age }}岁</el-descriptions-item>
                <el-descriptions-item label="BMI">{{ store.bmi }}</el-descriptions-item>
                <el-descriptions-item label="空腹血糖">{{ store.glucose }} mg/dL</el-descriptions-item>
                <el-descriptions-item label="血压">{{ store.bloodPressure }} mmHg</el-descriptions-item>
                <el-descriptions-item label="胰岛素">{{ store.insulin }} mU/L</el-descriptions-item>
                <el-descriptions-item label="遗传系数">{{ store.diabetesPedigreeFunction }}</el-descriptions-item>
              </el-descriptions>
            </div>
          </Transition>
        </div>

        <div class="health-suggestion-card">
          <div class="prescription-header">
            <el-icon><Document /></el-icon>
            <span>AI 健康处方</span>
          </div>
          <div class="suggestion-section health-advice-section">
            <div class="section-indicator"></div>
            <div class="section-content">
              <h4>健康建议</h4>
              <p>{{ store.aiAdvice || getHealthAdvice(store.riskLevel) }}</p>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="resultDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="resetForm">重新评估</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="previewDialogVisible" :title="previewTitle" width="900px" append-to-body>
      <div class="chart-preview" v-if="previewImage">
        <img :src="'data:image/png;base64,' + previewImage" :alt="previewTitle" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import {
  Edit, User, Document, DataAnalysis, Clock, Tools, Calendar,
  Warning, ArrowRight, ArrowLeft, ArrowDown, Dish, Cpu, CircleCheck, Upload,
  RefreshRight, Coin, Promotion, InfoFilled, TrendCharts,
  Sugar, ScaleToOriginal, ChatDotRound, Check, DataBoard, Delete
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useHealthStore } from '@/store/healthStore'
import { useHealthValidator } from '@/composables/useHealthValidator'
import { usePrediction } from '@/composables/usePrediction'
import { useDraftPersistence } from '@/composables/useDraftPersistence'
import DpfCalculator from '@/components/DpfCalculator.vue'

const store = useHealthStore()
const validator = useHealthValidator()
const { runPrediction, getRiskText, getRiskClass, getHealthAdvice } = usePrediction()
const { startAutoSave, stopAutoSave, loadDraft } = useDraftPersistence()

const STATUS_MAP = {
  DONE: { text: '已完成', type: 'success' },
  PENDING: { text: '诊断中', type: 'warning' },
  SAVED: { text: '已保存', type: 'info' }
}

const form = reactive({
  Pregnancies: computed({ get: () => store.pregnancies, set: v => store.pregnancies = v }),
  Glucose: computed({ get: () => store.glucose, set: v => store.glucose = v }),
  BloodPressure: computed({ get: () => store.bloodPressure, set: v => store.bloodPressure = v }),
  SkinThickness: computed({ get: () => store.skinThickness, set: v => store.skinThickness = v }),
  Insulin: computed({ get: () => store.insulin, set: v => store.insulin = v }),
  BMI: computed({ get: () => store.bmi, set: () => {} }),
  DiabetesPedigreeFunction: computed({ get: () => store.diabetesPedigreeFunction, set: v => store.diabetesPedigreeFunction = v }),
  Age: computed({ get: () => store.age, set: v => store.age = v }),
  symptoms: computed({ get: () => store.symptoms, set: v => store.symptoms = v }),
  file: ref(null)
})

const temp = reactive({ height: 170, weight: 60 })
const bmiFeedback = ref({ text: '', class: '', icon: '' })
const glucoseFeedback = ref({ text: '', class: '', icon: '' })
const bpFeedback = ref({ text: '', class: '', icon: '' })
const saving = ref(false)
const submitting = ref(false)
const submittingId = ref(null)
const predicting = ref(false)
const deletingId = ref(null)
const fileList = ref([])
const historyList = ref([])
const loadingHistory = ref(false)
const selectedHistoryId = ref(null)
const checkList = ref([
  { text: '早餐吃了粗粮/蔬菜', done: false },
  { text: '餐后散步 20 分钟', done: false },
  { text: '喝够 8 杯水', done: false },
  { text: '今晚 23:00 前睡觉', done: false }
])
const chartMode = ref('glucose')
const chartInstance = ref(null)
const currentAdvice = ref(null)
const dpfDialogVisible = ref(false)
const dpfInfoDialogVisible = ref(false)
const resultDialogVisible = ref(false)
const showDataDetails = ref(false)
const currentChartIndex = ref(0)
const previewDialogVisible = ref(false)
const previewImage = ref('')
const previewTitle = ref('')

const emergencyDialogVisible = ref(false)
const emergencyTab = ref('identify')
const emergencySeverity = ref('mild')
const emergencyStep = ref(1)
const emergencyTimerRunning = ref(false)
const emergencyCountdown = ref(0)
let emergencyTimer = null

const carbDialogVisible = ref(false)
const carbTab = ref('calculator')
const selectedFood = ref('')
const foodWeight = ref(100)
const activeFoodGroup = ref([])

const footDialogVisible = ref(false)
const footTab = ref('checklist')
const footChecklist = ref([
  { title: '检查足底', desc: '用镜子或让家人帮助查看足底有无伤口、水泡、红肿', done: false },
  { title: '检查趾缝', desc: '每个脚趾之间仔细检查是否有破损或真菌感染', done: false },
  { title: '检查足背', desc: '检查足背有无肿胀、皮温异常', done: false },
  { title: '检查鞋子', desc: '穿鞋前检查鞋内有无异物、鞋垫是否平整', done: false },
  { title: '洗脚测试', desc: '用温度计或手肘测试水温（不超过37℃）', done: false }
])

const progressPercent = computed(() => {
  const done = checkList.value.filter(t => t.done).length
  return Math.round((done / checkList.value.length) * 100)
})

const foodGroups = [
  {
    label: '主食', foods: [
      { name: '白米饭', standardGram: 100, carbPerStandard: 38, servings: 2.5, calories: 130 },
      { name: '馒头', standardGram: 80, carbPerStandard: 36, servings: 2.4, calories: 180 },
      { name: '面条（煮）', standardGram: 150, carbPerStandard: 35, servings: 2.3, calories: 165 },
      { name: '全麦面包', standardGram: 50, carbPerStandard: 24, servings: 1.6, calories: 120 },
      { name: '燕麦片', standardGram: 40, carbPerStandard: 25, servings: 1.7, calories: 150 },
      { name: '红薯', standardGram: 150, carbPerStandard: 30, servings: 2.0, calories: 135 },
      { name: '土豆', standardGram: 150, carbPerStandard: 26, servings: 1.7, calories: 110 },
      { name: '玉米', standardGram: 150, carbPerStandard: 28, servings: 1.9, calories: 130 }
    ]
  },
  {
    label: '水果', foods: [
      { name: '苹果', standardGram: 200, carbPerStandard: 22, servings: 1.5, calories: 95 },
      { name: '香蕉', standardGram: 150, carbPerStandard: 30, servings: 2.0, calories: 135 },
      { name: '橙子', standardGram: 200, carbPerStandard: 20, servings: 1.3, calories: 85 },
      { name: '葡萄', standardGram: 150, carbPerStandard: 24, servings: 1.6, calories: 100 },
      { name: '西瓜', standardGram: 300, carbPerStandard: 24, servings: 1.6, calories: 90 },
      { name: '草莓', standardGram: 200, carbPerStandard: 14, servings: 0.9, calories: 60 }
    ]
  },
  {
    label: '蔬菜', foods: [
      { name: '西兰花', standardGram: 150, carbPerStandard: 11, servings: 0.7, calories: 50 },
      { name: '胡萝卜', standardGram: 150, carbPerStandard: 15, servings: 1.0, calories: 65 },
      { name: '番茄', standardGram: 200, carbPerStandard: 10, servings: 0.7, calories: 40 },
      { name: '菠菜', standardGram: 150, carbPerStandard: 5, servings: 0.3, calories: 35 },
      { name: '黄瓜', standardGram: 200, carbPerStandard: 8, servings: 0.5, calories: 30 }
    ]
  },
  {
    label: '零食/饮品', foods: [
      { name: '含糖汽水', standardGram: 355, carbPerStandard: 39, servings: 2.6, calories: 140 },
      { name: '果汁', standardGram: 250, carbPerStandard: 30, servings: 2.0, calories: 120 },
      { name: '饼干', standardGram: 50, carbPerStandard: 32, servings: 2.1, calories: 230 },
      { name: '巧克力', standardGram: 50, carbPerStandard: 28, servings: 1.9, calories: 250 },
      { name: '蛋糕', standardGram: 80, carbPerStandard: 35, servings: 2.3, calories: 280 }
    ]
  }
]

const footCareSteps = [
  { title: '温水洗脚', content: '每天用温水（不超过37℃）洗脚，使用温和的肥皂。洗脚前务必用手肘或温度计测试水温。洗后用柔软毛巾轻轻擦干，特别注意脚趾缝之间。', tips: '不要用热水袋或电热毯暖脚，以免烫伤' },
  { title: '彻底检查', content: '在良好光线下仔细检查双脚各处：足底、足背、脚趾缝、指甲边缘。使用镜子检查足底，或请家人帮忙。', tips: '如果视力不好，建议让家人每周至少帮忙检查一次' },
  { title: '保湿护理', content: '在足部皮肤还微湿时涂抹保湿霜（不含香料），保持皮肤柔软防止干裂。注意：不要涂抹在脚趾缝之间。', tips: '脚趾缝之间保持干燥，可用玉米淀粉撒在脚趾间吸湿' },
  { title: '修剪指甲', content: '洗脚后指甲变软时修剪。沿指甲自然弧度修剪，不要剪太短，用指甲锉修平边缘。', tips: '视力不好或指甲增厚时，请足病医生处理，不要自己修剪' },
  { title: '选择鞋袜', content: '穿合脚的软底鞋，避免穿凉鞋、高跟鞋或尖头鞋。每天换干净、无缝的棉袜，袜子不要有松紧带。', tips: '新鞋先穿1-2小时逐渐适应，每次穿前检查鞋内有无异物' }
]

const calcCarbs = computed(() => {
  if (!selectedFood.value) return 0
  for (const group of foodGroups) {
    const food = group.foods.find(f => f.name === selectedFood.value)
    if (food) return Math.round((foodWeight.value / food.standardGram) * food.carbPerStandard * 10) / 10
  }
  return 0
})

const calcServings = computed(() => {
  return Math.round(calcCarbs.value / 15 * 10) / 10
})

const calcCalories = computed(() => {
  if (!selectedFood.value) return 0
  for (const group of foodGroups) {
    const food = group.foods.find(f => f.name === selectedFood.value)
    if (food) return Math.round((foodWeight.value / food.standardGram) * food.calories)
  }
  return 0
})

const dailyCarbPercent = computed(() => {
  return Math.min(Math.round((calcCarbs.value / 130) * 100), 200)
})

const footCheckPercent = computed(() => {
  const done = footChecklist.value.filter(i => i.done).length
  return Math.round((done / footChecklist.value.length) * 100)
})

const availableCharts = computed(() => {
  if (!store.chartsData) return []
  const chartList = [
    { key: 'dashboard_chart', label: '风险仪表盘', image: store.chartsData.dashboard_chart },
    { key: 'radar_chart', label: '健康雷达图', image: store.chartsData.radar_chart },
    { key: 'comparison_chart', label: '指标对比', image: store.chartsData.comparison_chart },
    { key: 'scorecard_chart', label: '健康评分', image: store.chartsData.scorecard_chart },
    { key: 'heatmap_chart', label: '风险热力图', image: store.chartsData.heatmap_chart },
    { key: 'waterfall_chart', label: '因素贡献', image: store.chartsData.waterfall_chart },
    { key: 'confidence_chart', label: '置信区间', image: store.chartsData.confidence_chart },
    { key: 'pie_chart', label: '风险分布', image: store.chartsData.pie_chart },
    { key: 'importance_chart', label: '特征重要性', image: store.chartsData.importance_chart }
  ]
  return chartList.filter(chart => chart.image)
})

const dpfForm = reactive({
  father: { hasDiabetes: false, ageAtDiagnosis: 50 },
  mother: { hasDiabetes: false, ageAtDiagnosis: 50 },
  sibling: { hasDiabetes: false, ageAtDiagnosis: 40 },
  grandparent: { hasDiabetes: false, ageAtDiagnosis: 60 }
})
const dpfResult = ref(0.08)
const dpfRiskClass = computed(() => {
  if (dpfResult.value < 0.4) return 'low-risk'
  if (dpfResult.value < 0.8) return 'medium-risk'
  return 'high-risk'
})
const dpfRiskText = computed(() => {
  if (dpfResult.value < 0.4) return '低风险'
  if (dpfResult.value < 0.8) return '中等风险'
  return '高风险'
})
const dpfBarWidth = computed(() => {
  return Math.min(Math.max(((dpfResult.value - 0.08) / (2.42 - 0.08)) * 100, 0), 100)
})

watch(historyList, () => { nextTick(() => renderChart()) }, { deep: true })
watch(chartMode, () => { nextTick(() => renderChart()) })
watch(dpfForm, () => { calculateDPF() }, { deep: true })

const chartRef = ref(null)

onMounted(() => {
  loadDraft()
  loadAdvice()
  startAutoSave()
  loadHistory()
  nextTick(() => {
    calcBMI()
    evaluateGlucose(form.Glucose)
    evaluateBP(form.BloodPressure)
  })
})

onUnmounted(() => {
  stopAutoSave()
  if (emergencyTimer) clearInterval(emergencyTimer)
})

function calcBMI() {
  if (!temp.height || !temp.weight) {
    bmiFeedback.value = { text: '', class: '', icon: '' }
    return
  }
  store.height = temp.height
  store.weight = temp.weight
  const bmiVal = temp.weight / ((temp.height / 100) ** 2)
  const fb = validator.evaluateBMI(bmiVal)
  bmiFeedback.value = fb
}

function evaluateGlucose(val) {
  glucoseFeedback.value = validator.evaluateGlucose(val)
}

function evaluateBP(val) {
  bpFeedback.value = validator.evaluateBP(val)
}

function handleFileChange(uploadFile) {
  form.file = uploadFile.raw
  fileList.value = [uploadFile]
}

function validate() {
  if (!store.age || !store.glucose || !store.bmi) {
    ElMessage.warning('请填写年龄、血糖和 BMI（身高体重）')
    return false
  }
  return true
}

function generateAdvice() {
  const advices = []
  if (store.glucose > 126) {
    advices.push({ title: '控制血糖', content: '您的血糖偏高，建议减少高糖食物摄入，增加膳食纤维摄入。' })
  }
  if (store.bmi && store.bmi >= 24) {
    advices.push({ title: '体重管理', content: '建议通过合理饮食和适量运动将BMI控制在正常范围内。' })
  }
  if (store.bloodPressure && store.bloodPressure >= 140) {
    advices.push({ title: '血压监测', content: '您的血压偏高，建议减少盐分摄入，保持规律作息。' })
  }
  if (advices.length === 0) {
    advices.push({ title: '保持健康', content: '您的各项指标基本正常，请继续保持健康的生活方式。' })
    advices.push({ title: '定期体检', content: '建议每季度进行一次健康检查，及时了解身体状况。' })
  }
  advices.push({ title: '科学运动', content: '建议每周进行至少150分钟的中等强度有氧运动。' })
  advices.push({ title: '合理饮食', content: '保持均衡营养，增加蔬菜水果摄入，减少加工食品。' })
  currentAdvice.value = advices
  store.adviceData = advices
}

function loadAdvice() {
  if (store.adviceData && Array.isArray(store.adviceData) && store.adviceData.length > 0) {
    currentAdvice.value = store.adviceData
  } else {
    currentAdvice.value = [
      { title: '保持健康', content: '您的各项指标基本正常，请继续保持健康的生活方式。' },
      { title: '定期体检', content: '建议每季度进行一次健康检查，及时了解身体状况。' },
      { title: '科学运动', content: '建议每周进行至少150分钟的中等强度有氧运动。' },
      { title: '合理饮食', content: '保持均衡营养，增加蔬菜水果摄入，减少加工食品。' }
    ]
  }
}

async function saveOnly() {
  if (!validate()) return
  saving.value = true
  try {
    const payload = store.toSavePayload()
    const res = await request.post('/api/health-profile/save-and-predict', payload)
    if (res.code === '200' && res.data) {
      const id = res.data.id
      ElMessage.success(`档案保存成功 (ID: ${id})`)
      if (res.data.prediction) {
        store.setPredictionResult(res.data.prediction)
      }
      await loadHistory()
      generateAdvice()
      return id
    } else {
      ElMessage.error(res.msg || '保存失败')
      return null
    }
  } catch (e) {
    ElMessage.error('保存失败: ' + (e.response?.data?.msg || e.message))
    return null
  } finally { saving.value = false }
}

async function saveAndPredict() {
  if (!validate()) return
  predicting.value = true
  try {
    const payload = store.toSavePayload()
    const res = await request.post('/api/health-profile/save-and-predict', payload)
    if (res.code === '200' && res.data) {
      store.setPredictionResult(res.data.prediction)
      store.predictionId = res.data.id
      if (res.data.prediction && res.data.prediction.ai_advice) {
        store.aiAdvice = res.data.prediction.ai_advice
      }
      generateAdvice()
      await loadHistory()
      resultDialogVisible.value = true
      ElMessage.success('保存并检测完成')
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存并检测失败: ' + (e.response?.data?.msg || e.message))
  } finally {
    predicting.value = false
  }
}

async function quickPredict() {
  if (!validate()) return
  predicting.value = true
  try {
    const features = store.toFeatures()
    const result = await runPrediction(features)
    if (result.success) {
      store.setPredictionResult(result.data)
    } else {
      store.setPredictionResult(result.data)
      ElMessage.warning(result.message)
    }
    generateAdvice()
    resultDialogVisible.value = true
  } catch (e) {
    ElMessage.error('检测失败: ' + e.message)
  } finally {
    predicting.value = false
  }
}

async function submitToDoctor(existingId) {
  if (!validate()) return
  let targetId = existingId
  if (!targetId) {
    targetId = await saveOnly()
    if (!targetId) return
  }
  const finalId = Number(targetId)
  try {
    await ElMessageBox.confirm(`确认生成 CSV 并发送给诊断员？\n档案 ID: ${finalId}`, '确认发送', { type: 'warning' })
  } catch { return }

  submitting.value = !existingId
  submittingId.value = existingId
  try {
    const res = await request.post(`/api/health-profile/send-to-doctor/${finalId}`, {})
    const ok = !res || res.code === 200 || res.success === true
    ElMessage[ok ? 'success' : 'warning'](ok ? 'CSV 已生成，诊断员已通知' : (res.msg || '提交完成'))
    await loadHistory()
  } catch (e) {
    ElMessage.error('提交失败：' + (e.response?.data?.msg || e.message))
  } finally {
    submitting.value = false
    submittingId.value = null
  }
}

async function deleteHistory(id) {
  deletingId.value = id
  try {
    const res = await request.delete(`/api/health-profile/${id}`)
    if (res && res.code === '200') {
      ElMessage.success('记录已删除')
      if (selectedHistoryId.value === id) {
        selectedHistoryId.value = null
      }
      await loadHistory()
    } else {
      ElMessage.error(res?.msg || '删除失败')
    }
  } catch (e) {
    ElMessage.error('删除失败: ' + (e.response?.data?.msg || e.message))
  } finally {
    deletingId.value = null
  }
}

async function loadHistory() {
  loadingHistory.value = true
  try {
    const res = await request.get('/api/health-profile/list')
    if (res && res.code === '200') {
      historyList.value = Array.isArray(res.data) ? res.data : []
    } else if (Array.isArray(res)) {
      historyList.value = res
    } else {
      historyList.value = []
      if (res && res.msg) {
        ElMessage.warning('加载历史: ' + res.msg)
      }
    }
    nextTick(() => renderChart())
  } catch (e) {
    console.error('加载历史失败', e)
    historyList.value = []
    ElMessage.error('加载历史记录失败，请检查网络或后端服务')
  } finally { loadingHistory.value = false }
}

function selectHistory(item) {
  selectedHistoryId.value = item.id
  nextTick(() => renderChart())
}

async function loadHistoryToForm(item) {
  selectedHistoryId.value = item.id
  store.pregnancies = item.pregnancies || 0
  store.glucose = item.glucose || 90
  store.bloodPressure = item.bloodPressure || 120
  store.skinThickness = item.skinThickness || 20
  store.insulin = item.insulin || 50
  store.diabetesPedigreeFunction = item.diabetesPedigreeFunction || 0.5
  store.age = item.age || 20
  store.symptoms = item.symptoms || ''
  temp.height = item.height || null
  temp.weight = item.weight || null
  evaluateGlucose(store.glucose)
  evaluateBP(store.bloodPressure)
  fileList.value = []
  form.file = null

  if (item.aiAdvice) {
    store.aiAdvice = item.aiAdvice
    const adviceList = []
    if (store.glucose > 126) {
      adviceList.push({ title: '控制血糖', content: '您的血糖偏高，建议减少高糖食物摄入，增加膳食纤维摄入。' })
    }
    if (store.bmi && store.bmi >= 24) {
      adviceList.push({ title: '体重管理', content: '建议通过合理饮食和适量运动将BMI控制在正常范围内。' })
    }
    if (store.bloodPressure && store.bloodPressure >= 140) {
      adviceList.push({ title: '血压监测', content: '您的血压偏高，建议减少盐分摄入，保持规律作息。' })
    }
    adviceList.push({ title: 'AI 健康建议', content: item.aiAdvice })
    currentAdvice.value = adviceList
    store.adviceData = adviceList
  } else {
    generateAdvice()
  }
  ElMessage.success('已载入历史数据')
  nextTick(() => renderChart())
}

function formatTime(ts) {
  if (!ts) return ''
  const d = new Date(ts)
  return `${d.getMonth() + 1}/${d.getDate()} ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}

function renderChart() {
  const el = chartRef.value
  if (!el) return
  if (!chartInstance.value) {
    chartInstance.value = echarts.init(el)
  }

  const sorted = [...historyList.value].sort((a, b) => new Date(a.createTime) - new Date(b.createTime))
  const times = sorted.map(item => formatTime(item.createTime))
  const glucoseData = sorted.map(item => item.glucose)
  const bmiData = sorted.map(item => item.bmi)

  const selectedIndex = selectedHistoryId.value
    ? sorted.findIndex(item => item.id === selectedHistoryId.value)
    : -1

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        const data = params[0]
        const index = data.dataIndex
        const item = sorted[index]
        return `<strong>${data.axisValue}</strong><br/>${chartMode.value === 'glucose' ? '血糖' : 'BMI'}: ${data.value}<br/>状态: ${STATUS_MAP[item.status]?.text || ''}`
      }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: times,
      axisLabel: { rotate: 30, fontSize: 10 }
    },
    yAxis: {
      type: 'value',
      name: chartMode.value === 'glucose' ? '血糖 (mg/dL)' : 'BMI',
      nameTextStyle: { fontSize: 11 }
    },
    series: [{
      data: chartMode.value === 'glucose' ? glucoseData : bmiData,
      type: 'line',
      smooth: true,
      showSymbol: true,
      symbol: 'circle',
      symbolSize: 6,
      lineStyle: { width: 2 },
      areaStyle: {
        opacity: 0.15,
        color: chartMode.value === 'glucose'
          ? new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#E6A23C' }, { offset: 1, color: '#fff' }])
          : new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#409EFF' }, { offset: 1, color: '#fff' }])
      },
      itemStyle: {
        color: (param) => {
          return param.dataIndex === selectedIndex ? '#F56C6C' : (chartMode.value === 'glucose' ? '#E6A23C' : '#409EFF')
        },
        borderWidth: (param) => {
          return param.dataIndex === selectedIndex ? 3 : 0
        },
        borderColor: '#fff'
      },
      markLine: {
        silent: true,
        data: chartMode.value === 'glucose'
          ? [{ yAxis: 110, label: { formatter: '上限', fontSize: 10 }, lineStyle: { type: 'dashed', color: '#F56C6C' } }]
          : []
      }
    }]
  }

  chartInstance.value.setOption(option, true)
}

function openTool(type) {
  if (type === 'emergency') {
    emergencySeverity.value = 'mild'
    emergencyStep.value = 1
    emergencyTab.value = 'identify'
    emergencyDialogVisible.value = true
  } else if (type === 'carb-count') {
    selectedFood.value = ''
    foodWeight.value = 100
    carbTab.value = 'calculator'
    carbDialogVisible.value = true
  } else if (type === 'foot-care') {
    footTab.value = 'checklist'
    footDialogVisible.value = true
  }
}

function startEmergencyTimer() {
  if (emergencyTimer) clearInterval(emergencyTimer)
  emergencyCountdown.value = 900
  emergencyTimerRunning.value = true
  emergencyTimer = setInterval(() => {
    emergencyCountdown.value--
    if (emergencyCountdown.value <= 0) {
      clearInterval(emergencyTimer)
      emergencyTimer = null
      emergencyTimerRunning.value = false
      emergencyStep.value = Math.max(emergencyStep.value, 4)
      ElMessage.success('⏱ 15分钟已到！请复测血糖')
    }
  }, 1000)
}

function toggleCheck(idx) {
  checkList.value[idx].done = !checkList.value[idx].done
}

function resetForm() {
  store.resetAll()
  temp.height = 170
  temp.weight = 60
  fileList.value = []
  bmiFeedback.value = { text: '', class: '', icon: '' }
  glucoseFeedback.value = { text: '', class: '', icon: '' }
  bpFeedback.value = { text: '', class: '', icon: '' }
  currentAdvice.value = null
  store.adviceData = null
  nextTick(() => {
    calcBMI()
    evaluateGlucose(store.glucose)
    evaluateBP(store.bloodPressure)
  })
  ElMessage.info('表单已重置')
}

function calculateDPF() {
  const baseValue = 0.08
  let totalScore = 0
  const familyMembers = [
    { data: dpfForm.father, weight: 0.5 },
    { data: dpfForm.mother, weight: 0.5 },
    { data: dpfForm.sibling, weight: 0.5 },
    { data: dpfForm.grandparent, weight: 0.25 }
  ]
  familyMembers.forEach(member => {
    if (member.data.hasDiabetes) {
      const age = member.data.ageAtDiagnosis || 50
      let ageFactor = 1.0
      if (age < 40) ageFactor = 1.5
      else if (age > 60) ageFactor = 0.7
      totalScore += member.weight * ageFactor
    }
  })
  dpfResult.value = Math.min(Math.max(baseValue + totalScore * 0.8, 0.08), 2.42)
  ElMessage.success('计算完成')
}

function applyDPFResult() {
  const val = parseFloat(dpfResult.value.toFixed(3))
  store.diabetesPedigreeFunction = val
  dpfDialogVisible.value = false
  ElMessage.success(`已应用谱系函数值：${val}`)
}

function prevChart() {
  const len = availableCharts.value.length
  if (len > 0) currentChartIndex.value = (currentChartIndex.value - 1 + len) % len
}

function nextChart() {
  const len = availableCharts.value.length
  if (len > 0) currentChartIndex.value = (currentChartIndex.value + 1) % len
}

function goToChart(index) {
  currentChartIndex.value = index
}

function previewCurrentChart() {
  const charts = availableCharts.value
  if (charts.length > 0) {
    previewImage.value = charts[currentChartIndex.value].image
    previewTitle.value = charts[currentChartIndex.value].label
    previewDialogVisible.value = true
  }
}
</script>

<style scoped>
.health-hub-pro {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ed 100%);
  min-height: 100vh;
}

.el-card {
  border-radius: 14px;
  transition: all 0.3s ease;
  margin-bottom: 0;
}
.el-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.08);
}

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, #4080FF 0%, #52C41A 100%);
  border-radius: 16px;
  padding: 28px 32px;
  margin-bottom: 24px;
  box-shadow: 0 8px 24px rgba(64, 128, 255, 0.2);
  position: relative;
  overflow: hidden;
}

.welcome-banner::after {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 300px;
  height: 300px;
  background: rgba(255,255,255,0.06);
  border-radius: 50%;
}

.welcome-banner h1 {
  margin: 0 0 6px 0;
  font-size: 24px;
  color: white;
  font-weight: 700;
  position: relative;
  z-index: 1;
}

.welcome-banner p {
  margin: 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
  letter-spacing: 2px;
  position: relative;
  z-index: 1;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 15px;
  color: #1D2129;
}
.header-icon {
  font-size: 18px;
  color: #409EFF;
}

.el-divider {
  margin: 20px 0 16px;
}
.section-divider {
  font-size: 14px;
  font-weight: 600;
  color: #606266;
}

.health-form {
  padding: 8px 0;
}
.health-form :deep(.el-form-item) {
  margin-bottom: 18px;
}
.health-form :deep(.el-input-number) {
  width: 100%;
}

/* 标签悬浮提示文字可换行 */
.health-form :deep(.el-tooltip__popper) {
  max-width: 280px;
  white-space: normal !important;
  word-break: break-word !important;
  line-height: 1.5;
  padding: 10px 14px;
  font-size: 13px;
}

.feedback-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  margin-top: 8px;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
}
.feedback-tag.success { background: #f0f9eb; color: #67C23A; }
.feedback-tag.warning { background: #fdf6ec; color: #E6A23C; }
.feedback-tag.danger { background: #fef0f0; color: #F56C6C; }

.helper-text {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.action-bar {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #EBEEF5;
  flex-wrap: wrap;
}
.action-bar .el-button {
  min-width: 100px;
}

.advice-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.advice-item {
  display: flex;
  gap: 12px;
  padding: 14px;
  background: linear-gradient(135deg, #f0f9eb, #ecf5ff);
  border-radius: 10px;
  border-left: 3px solid #67C23A;
  transition: all 0.2s;
}
.advice-item:hover {
  transform: translateX(4px);
}
.advice-number {
  flex-shrink: 0;
  width: 24px;
  height: 24px;
  background: #67C23A;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
}
.advice-content h4 {
  margin: 0 0 4px;
  font-size: 14px;
  color: #303133;
}
.advice-content p {
  margin: 0;
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
}
.empty-advice {
  text-align: center;
  padding: 30px;
  color: #909399;
}
.empty-icon {
  font-size: 40px;
  color: #c0c4cc;
  margin-bottom: 10px;
}

.chart-card .el-radio-group {
  margin-bottom: 12px;
}
.chart-box {
  width: 100%;
  height: 220px;
  background: #fafafa;
  border-radius: 8px;
}
.empty-chart {
  text-align: center;
  padding: 30px;
  color: #909399;
}
.chart-legend {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 10px;
  font-size: 12px;
  color: #606266;
}
.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
}
.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}
.legend-line {
  width: 20px;
  height: 2px;
  background: #F56C6C;
  border-style: dashed;
}

.history-list {
  position: relative;
}
.history-count {
  margin-left: auto;
  font-size: 12px;
  color: #909399;
  font-weight: normal;
}
.history-item {
  padding: 12px;
  margin-bottom: 10px;
  background: #fafafa;
  border-radius: 10px;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.2s;
}
.history-item:hover {
  background: #f0f2f5;
  border-color: #d9d9d9;
}
.history-item.is-active {
  background: linear-gradient(135deg, #ecf5ff, #f0f9eb);
  border-color: #409EFF;
}
.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.time {
  font-size: 12px;
  color: #909399;
}
.item-body {
  margin-bottom: 8px;
}
.item-metrics {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.metric {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #606266;
}
.metric .el-icon {
  color: #409EFF;
}
.diagnosis-box {
  background: #f0f9eb;
  padding: 8px 10px;
  border-radius: 6px;
  font-size: 12px;
  margin-bottom: 8px;
  color: #2d6a2d;
  display: flex;
  align-items: center;
  gap: 6px;
}
.item-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  border-top: 1px dashed #eee;
  padding-top: 8px;
}
.action-right {
  display: flex;
  align-items: center;
  gap: 8px;
}
.empty-state {
  text-align: center;
  padding: 40px;
  color: #909399;
}

.tool-grid {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.tool-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
}
.tool-item:hover {
  background: #f5f7fa;
  border-color: #409EFF;
  transform: translateX(4px);
}
.icon-box {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}
.bg-red { background: linear-gradient(135deg, #f56c6c, #e64242); }
.bg-orange { background: linear-gradient(135deg, #e6a23c, #d19020); }
.bg-green { background: linear-gradient(135deg, #67c23a, #529b2e); }
.tool-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.tool-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}
.tool-desc {
  font-size: 12px;
  color: #909399;
}
.tool-arrow {
  color: #c0c4cc;
  font-size: 14px;
}

.checkin-list {
  margin-bottom: 12px;
}
.checkin-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
  cursor: pointer;
  transition: color 0.2s;
  color: #606266;
}
.checkin-item:hover {
  color: #409EFF;
}
.checkin-item.done {
  color: #67C23A;
}
.check-icon {
  font-size: 18px;
}
.check-icon.is-checked {
  color: #67C23A;
}

@media (max-width: 768px) {
  .health-hub-pro {
    padding: 12px;
  }
  .el-col-lg-16, .el-col-lg-8 {
    max-width: 100%;
    flex: 0 0 100%;
  }
  .action-bar {
    flex-direction: column;
  }
  .action-bar .el-button {
    width: 100%;
  }
}

/* DPF 输入组样式 */
.dpf-input-group {
  display: flex;
  gap: 8px;
  align-items: center;
}
.dpf-input-group .el-input-number {
  flex: 1;
}
.dpf-calc-btn, .dpf-info-btn {
  flex-shrink: 0;
}

/* 标签悬浮提示样式 */
.label-with-tip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  cursor: help;
}
.tip-icon {
  font-size: 14px;
  color: #909399;
  transition: color 0.2s;
}
.label-with-tip:hover .tip-icon {
  color: #409EFF;
}

/* DPF 弹窗样式 */
.dpf-dialog-content {
  max-height: 65vh;
  overflow-y: auto;
  padding: 0 4px;
}

.dpf-section {
  margin-bottom: 20px;
  background: #fafbfc;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #ebeef5;
}

.dpf-section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 14px;
  padding-bottom: 10px;
  border-bottom: 1px dashed #e0e0e0;
}

.dpf-section-icon {
  font-size: 18px;
  color: #409EFF;
}

.dpf-section-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.dpf-family-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.dpf-family-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: white;
  border-radius: 10px;
  border: 1px solid #ebeef5;
  transition: all 0.3s ease;
}

.dpf-family-card.has-diabetes {
  border-color: #F56C6C;
  background: linear-gradient(135deg, #fff5f5, #ffe6e6);
}

.family-avatar {
  font-size: 28px;
  flex-shrink: 0;
}

.family-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.family-name {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
}

.family-age-hint {
  font-size: 12px;
  color: #909399;
}

.dpf-result-area {
  text-align: center;
}

.dpf-result-card {
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 16px;
  transition: all 0.3s ease;
}

.dpf-result-card.low-risk {
  background: linear-gradient(135deg, #e8f5e9, #c8e6c9);
  border: 1px solid #a5d6a7;
}

.dpf-result-card.medium-risk {
  background: linear-gradient(135deg, #fff3e0, #ffe0b2);
  border: 1px solid #ffcc80;
}

.dpf-result-card.high-risk {
  background: linear-gradient(135deg, #ffebee, #ffcdd2);
  border: 1px solid #ef9a9a;
}

.dpf-result-main {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.dpf-result-label {
  font-size: 14px;
  color: #606266;
}

.dpf-result-value {
  font-size: 36px;
  font-weight: 700;
  background: linear-gradient(135deg, #4080FF, #52C41A);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.dpf-result-range {
  font-size: 12px;
  color: #909399;
}

.dpf-result-level {
  margin-top: 8px;
}

.risk-badge {
  display: inline-block;
  padding: 4px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
}

.risk-badge.low-risk {
  background: #e8f5e9;
  color: #2e7d32;
}

.risk-badge.medium-risk {
  background: #fff3e0;
  color: #ef6c00;
}

.risk-badge.high-risk {
  background: #ffebee;
  color: #c62828;
}

.dpf-result-bar {
  margin: 16px 0;
  padding: 0 20px;
}

.dpf-bar-track {
  height: 10px;
  background: linear-gradient(90deg, #4caf50, #ff9800, #f44336);
  border-radius: 5px;
  position: relative;
  overflow: visible;
}

.dpf-bar-fill {
  height: 100%;
  background: transparent;
  border-radius: 5px;
}

.dpf-bar-marker {
  position: absolute;
  top: -5px;
  width: 20px;
  height: 20px;
  background: white;
  border: 3px solid #4080FF;
  border-radius: 50%;
  transform: translateX(-50%);
  box-shadow: 0 2px 8px rgba(64, 128, 255, 0.3);
  transition: left 0.3s ease;
}

.dpf-bar-labels {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.dpf-apply-btn {
  width: 200px;
  margin-top: 12px;
}

.dpf-medical-section {
  background: linear-gradient(135deg, #f0f9ff, #e6f7ff);
  border-color: #91d5ff;
}

.dpf-medical-content {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.dpf-medical-item {
  padding: 12px;
  background: white;
  border-radius: 8px;
  border: 1px solid #d9ecff;
}

.dpf-medical-item h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #4080FF;
}

.dpf-medical-item p {
  margin: 0;
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
}

.dpf-weight-table {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.weight-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 10px;
  background: #f5f7fa;
  border-radius: 6px;
  font-size: 13px;
}

.weight-value {
  font-weight: 600;
  color: #4080FF;
}

.dpf-dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* DPF 说明弹窗样式 */
.dpf-info-content {
  max-height: 60vh;
  overflow-y: auto;
}

.dpf-info-header {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #e6f7ff, #f0f9ff);
  border-radius: 12px;
  margin-bottom: 20px;
}

.dpf-info-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.dpf-info-header h3 {
  margin: 0 0 4px 0;
  font-size: 20px;
  color: #303133;
}

.dpf-info-subtitle {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.dpf-info-section {
  margin-bottom: 18px;
}

.dpf-info-section h4 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 10px 0;
  font-size: 16px;
  color: #303133;
}

.dpf-info-section p {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.dpf-info-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.dpf-info-item {
  display: flex;
  gap: 12px;
  padding: 14px;
  background: #fafbfc;
  border-radius: 10px;
  border: 1px solid #ebeef5;
}

.dpf-info-num {
  flex-shrink: 0;
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #4080FF, #69b1ff);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
}

.dpf-info-item strong {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  color: #303133;
}

.dpf-info-item p {
  margin: 0;
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
}

.dpf-info-item ul {
  margin: 4px 0 0;
  padding-left: 18px;
}

.dpf-info-item li {
  font-size: 13px;
  color: #606266;
  margin-bottom: 4px;
}

.dpf-risk-levels {
  display: flex;
  gap: 8px;
  margin-top: 10px;
  flex-wrap: wrap;
}

.risk-tag {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.risk-tag.low {
  background: #e8f5e9;
  color: #2e7d32;
}

.risk-tag.medium {
  background: #fff3e0;
  color: #ef6c00;
}

.risk-tag.high {
  background: #ffebee;
  color: #c62828;
}

.dpf-info-note {
    margin-top: 16px;
  }
</style>

<style>
.dpf-dialog .el-dialog__header {
  background: linear-gradient(135deg, #e6f7ff, #f0f9ff);
  border-radius: 12px 12px 0 0;
  padding: 18px 24px;
  border-bottom: 1px solid #91d5ff;
}

.dpf-dialog .el-dialog__title {
  font-weight: 600;
  color: #303133;
}

.dpf-dialog .el-dialog__body {
  padding: 20px 24px;
}

.dpf-dialog .el-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.dpf-info-dialog .el-dialog__header {
  background: linear-gradient(135deg, #f0f9ff, #e6f7ff);
  border-radius: 12px 12px 0 0;
  padding: 18px 24px;
  border-bottom: 1px solid #d9ecff;
}

.dpf-info-dialog .el-dialog__title {
  font-weight: 600;
  color: #303133;
}

.dpf-info-dialog .el-dialog__body {
  padding: 20px 24px;
}

.dpf-info-dialog .el-dialog {
  border-radius: 12px;
  overflow: hidden;
}

/* ===== 结果弹窗样式 ===== */
.result-dialog .el-dialog__header {
  display: none;
}
.result-dialog .el-dialog {
  border-radius: 16px;
  overflow: hidden;
}
.result-dialog .el-dialog__body {
  padding: 0;
}
.result-dialog .el-dialog__footer {
  padding: 16px 24px;
  border-top: 1px solid #ebeef5;
  background: #fafbfc;
}

.result-container {
  max-height: 75vh;
  overflow-y: auto;
}

/* 结果头部 */
.result-header {
  padding: 28px 24px;
  text-align: center;
  position: relative;
  overflow: hidden;
}
.result-header::before {
  content: '';
  position: absolute;
  top: -60%;
  right: -20%;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  opacity: 0.08;
}
.result-header.low-risk {
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 50%, #a5d6a7 100%);
}
.result-header.low-risk::before { background: #4caf50; }
.result-header.medium-risk {
  background: linear-gradient(135deg, #fff3e0 0%, #ffe0b2 50%, #ffcc80 100%);
}
.result-header.medium-risk::before { background: #ff9800; }
.result-header.high-risk {
  background: linear-gradient(135deg, #ffebee 0%, #ffcdd2 50%, #ef9a9a 100%);
}
.result-header.high-risk::before { background: #f44336; }

.result-header-main {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
  position: relative;
  z-index: 1;
}

.risk-level-badge {
  display: inline-block;
  padding: 6px 20px;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 1px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
.risk-level-badge.low-risk {
  background: rgba(76, 175, 80, 0.2);
  color: #1b5e20;
  border: 2px solid rgba(76, 175, 80, 0.4);
}
.risk-level-badge.medium-risk {
  background: rgba(255, 152, 0, 0.2);
  color: #e65100;
  border: 2px solid rgba(255, 152, 0, 0.4);
}
.risk-level-badge.high-risk {
  background: rgba(244, 67, 54, 0.2);
  color: #b71c1c;
  border: 2px solid rgba(244, 67, 54, 0.4);
}

.probability-main {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}
.probability-label {
  font-size: 13px;
  color: rgba(0,0,0,0.5);
  font-weight: 500;
}
.probability-value {
  font-size: 36px;
  font-weight: 800;
  letter-spacing: -1px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
.result-header.low-risk .probability-value { color: #1b5e20; }
.result-header.medium-risk .probability-value { color: #e65100; }
.result-header.high-risk .probability-value { color: #b71c1c; }

.confidence-interval {
  margin-top: 10px;
  font-size: 12px;
  color: rgba(0,0,0,0.45);
  position: relative;
  z-index: 1;
}

/* 图表轮播区 */
.charts-carousel {
  padding: 20px 24px;
  background: #fafbfc;
  border-bottom: 1px solid #ebeef5;
}

.carousel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.carousel-header h4 {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}
.carousel-header h4::before {
  content: '';
  width: 4px;
  height: 16px;
  background: linear-gradient(180deg, #409EFF, #52C41A);
  border-radius: 2px;
  display: inline-block;
}

.carousel-indicators {
  display: flex;
  gap: 6px;
  align-items: center;
}
.indicator-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #d9d9d9;
  cursor: pointer;
  transition: all 0.3s;
}
.indicator-dot.active {
  background: #409EFF;
  width: 20px;
  border-radius: 4px;
}

.carousel-main {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.carousel-btn {
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 1px solid #dcdfe6;
  background: white;
  color: #606266;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  font-size: 16px;
}
.carousel-btn:hover:not(:disabled) {
  border-color: #409EFF;
  color: #409EFF;
  background: #ecf5ff;
}
.carousel-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.carousel-display {
  flex: 1;
  min-height: 240px;
  background: white;
  border-radius: 12px;
  border: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  overflow: hidden;
  position: relative;
  transition: all 0.2s;
}
.carousel-display:hover {
  border-color: #409EFF;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.15);
}
.carousel-display img {
  max-width: 100%;
  max-height: 260px;
  object-fit: contain;
  border-radius: 8px;
}
.carousel-hint-text {
  position: absolute;
  bottom: 8px;
  right: 12px;
  font-size: 11px;
  color: #909399;
  background: rgba(255,255,255,0.85);
  padding: 2px 8px;
  border-radius: 4px;
  opacity: 0;
  transition: opacity 0.2s;
}
.carousel-display:hover .carousel-hint-text {
  opacity: 1;
}

.carousel-thumbnails {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 4px;
}
.carousel-thumbnails::-webkit-scrollbar {
  height: 4px;
}
.carousel-thumbnails::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 2px;
}

.thumbnail {
  flex-shrink: 0;
  width: 72px;
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid transparent;
  transition: all 0.2s;
  background: white;
}
.thumbnail:hover {
  border-color: #91d5ff;
}
.thumbnail.active {
  border-color: #409EFF;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}
.thumbnail img {
  width: 100%;
  height: 48px;
  object-fit: cover;
  display: block;
}
.thumbnail-label {
  display: block;
  text-align: center;
  font-size: 10px;
  color: #606266;
  padding: 4px 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 数据详情折叠面板 */
.collapse-panel.data-details {
  margin: 0 24px;
  border: 1px solid #ebeef5;
  border-radius: 10px;
  overflow: hidden;
  margin-top: 16px;
}
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #fafbfc;
  cursor: pointer;
  transition: background 0.2s;
  user-select: none;
}
.panel-header:hover {
  background: #f0f2f5;
}
.panel-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}
.panel-arrow {
  transition: transform 0.3s;
  color: #909399;
  font-size: 14px;
}
.panel-arrow.expanded {
  transform: rotate(180deg);
}
.panel-content {
  padding: 16px;
  background: white;
}

.panel-slide-enter-active,
.panel-slide-leave-active {
  transition: all 0.3s ease;
  overflow: hidden;
}
.panel-slide-enter-from,
.panel-slide-leave-to {
  max-height: 0;
  opacity: 0;
  padding-top: 0;
  padding-bottom: 0;
}
.panel-slide-enter-to,
.panel-slide-leave-from {
  max-height: 300px;
  opacity: 1;
}

/* AI健康处方 */
.health-suggestion-card {
  margin: 16px 24px 20px;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #ebeef5;
  background: white;
}
.prescription-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 16px;
  background: linear-gradient(135deg, #f0f9eb, #ecf5ff);
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  border-bottom: 1px solid #ebeef5;
}
.prescription-header .el-icon {
  color: #67C23A;
  font-size: 18px;
}
.suggestion-section {
  padding: 16px;
}
.health-advice-section {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}
.section-indicator {
  flex-shrink: 0;
  width: 4px;
  height: 100%;
  min-height: 40px;
  background: linear-gradient(180deg, #67C23A, #409EFF);
  border-radius: 2px;
}
.section-content h4 {
  margin: 0 0 6px;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}
.section-content p {
  margin: 0;
  font-size: 13px;
  color: #606266;
  line-height: 1.7;
}

/* 图表预览 */
.chart-preview {
  text-align: center;
}
.chart-preview img {
  max-width: 100%;
  border-radius: 8px;
}

/* ===== 工具弹窗通用样式 ===== */
.tool-dialog .el-dialog {
  border-radius: 14px;
  overflow: hidden;
}
.tool-dialog .el-dialog__header {
  background: linear-gradient(135deg, #f5f7fa, #e4e8ed);
  padding: 18px 24px;
  border-bottom: 1px solid #ebeef5;
}
.tool-dialog .el-dialog__title {
  font-weight: 600;
  font-size: 17px;
  color: #303133;
}
.tool-dialog .el-dialog__body {
  padding: 0;
}
.tool-dialog .el-dialog__footer {
  padding: 14px 24px;
  border-top: 1px solid #ebeef5;
  background: #fafbfc;
}
.tool-dialog-body {
  max-height: 65vh;
  overflow-y: auto;
  padding: 20px 24px;
}
.tool-tabs .el-tabs__header {
  margin-bottom: 18px;
}
.tool-tabs .el-tabs__item {
  font-size: 14px;
  font-weight: 500;
}
.tool-alert {
  margin-bottom: 16px;
}
.section-desc {
  color: #606266;
  font-size: 13px;
  margin: 0 0 14px;
}

/* 低血糖 - 症状识别 */
.severity-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}
.severity-card {
  border: 2px solid #ebeef5;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.25s;
  background: white;
}
.severity-card:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
.severity-card.selected { border-width: 2px; }
.severity-card.mild.selected { border-color: #67C23A; background: #f0f9eb; }
.severity-card.moderate.selected { border-color: #E6A23C; background: #fdf6ec; }
.severity-card.severe.selected { border-color: #F56C6C; background: #fef0f0; }
.severity-icon { font-size: 28px; margin-bottom: 8px; }
.severity-card h4 { margin: 0 0 8px; font-size: 15px; font-weight: 600; color: #303133; }
.severity-card ul { margin: 0; padding: 0; list-style: none; }
.severity-card ul li { font-size: 13px; color: #606266; padding: 2px 0; }
.severity-card ul li::before { content: '• '; color: #909399; }
.severity-range { display: block; margin-top: 10px; font-size: 11px; color: #909399; font-weight: 500; }

/* 低血糖 - 急救步骤 */
.emergency-steps { padding: 4px 0; }
.step-item {
  display: flex;
  gap: 14px;
  padding: 14px 16px;
  background: #fafafa;
  border-radius: 10px;
  border: 1px solid #ebeef5;
  transition: all 0.3s;
}
.step-item.active {
  border-color: #409EFF;
  background: #ecf5ff;
}
.step-num {
  flex-shrink: 0;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #c0c4cc;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 15px;
}
.step-item.active .step-num { background: #409EFF; }
.step-content h4 { margin: 0 0 4px; font-size: 14px; color: #303133; font-weight: 600; }
.step-content p { margin: 4px 0; font-size: 13px; color: #606266; }
.step-arrow { text-align: center; font-size: 20px; padding: 4px 0; color: #c0c4cc; }
.food-options { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 8px; }
.food-chip {
  display: inline-block;
  padding: 5px 12px;
  background: white;
  border: 1px solid #dcdfe6;
  border-radius: 20px;
  font-size: 12px;
  color: #606266;
  cursor: default;
  transition: all 0.2s;
}
.food-chip:hover { border-color: #409EFF; color: #409EFF; background: #ecf5ff; }

/* 低血糖 - 预防 */
.prevent-list { display: flex; flex-direction: column; gap: 12px; }
.prevent-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  padding: 12px 14px;
  background: #fafafa;
  border-radius: 10px;
  border-left: 3px solid #409EFF;
}
.prevent-icon { font-size: 24px; flex-shrink: 0; }
.prevent-item strong { display: block; font-size: 14px; color: #303133; margin-bottom: 2px; }
.prevent-item p { margin: 0; font-size: 13px; color: #606266; }

/* 碳水计算器 */
.calc-form { max-width: 460px; }
.calc-result { background: #f0f9eb; border-radius: 10px; padding: 14px 16px; }
.result-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
  border-bottom: 1px dashed rgba(0,0,0,0.06);
}
.result-row:last-of-type { border-bottom: none; }
.result-label { font-size: 13px; color: #606266; }
.result-value { font-size: 16px; font-weight: 700; color: #303133; }
.calc-hint { color: #909399; font-size: 14px; padding: 20px 0; text-align: center; }

/* 食物参考表 */
.food-collapse { margin-top: 8px; }
.food-table { width: 100%; font-size: 13px; }
.food-table-header, .food-table-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 8px;
  padding: 8px 4px;
}
.food-table-header { font-weight: 600; color: #909399; border-bottom: 1px solid #ebeef5; }
.food-table-row { cursor: pointer; transition: background 0.15s; border-radius: 4px; }
.food-table-row:hover { background: #ecf5ff; }
.food-table-row:nth-child(even) { background: #fafafa; }
.food-table-row:nth-child(even):hover { background: #ecf5ff; }

/* 碳水每日建议 */
.carb-guide { display: flex; flex-direction: column; gap: 16px; }
.guide-card { background: linear-gradient(135deg, #f0f9eb, #ecf5ff); border-radius: 12px; padding: 18px; }
.guide-card h4 { margin: 0 0 12px; font-size: 15px; color: #303133; }
.guide-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; }
.guide-item { text-align: center; padding: 12px; background: white; border-radius: 10px; }
.guide-item.highlight { background: linear-gradient(135deg, #409EFF, #337ecc); color: white; }
.guide-item.highlight .guide-label { color: rgba(255,255,255,0.8); }
.guide-value { display: block; font-size: 20px; font-weight: 800; }
.guide-label { font-size: 12px; color: #909399; margin-top: 2px; display: block; }
.guide-tips { background: #fafafa; border-radius: 12px; padding: 16px; }
.guide-tips h4 { margin: 0 0 10px; font-size: 14px; color: #303133; }
.guide-tips ul { margin: 0; padding: 0 0 0 18px; }
.guide-tips li { font-size: 13px; color: #606266; padding: 3px 0; }

/* 足部护理 - 每日自检 */
.foot-checklist { display: flex; flex-direction: column; gap: 8px; margin-bottom: 14px; }
.checklist-item {
  display: flex;
  gap: 10px;
  align-items: flex-start;
  padding: 12px 14px;
  background: #fafafa;
  border-radius: 10px;
  border: 1px solid #ebeef5;
  cursor: pointer;
  transition: all 0.2s;
}
.checklist-item:hover { border-color: #409EFF; }
.checklist-item.done { background: #f0f9eb; border-color: #67C23A; }
.checklist-item .check-icon { margin-top: 2px; color: #c0c4cc; font-size: 18px; }
.checklist-item.done .check-icon { color: #67C23A; }
.checklist-content strong { display: block; font-size: 14px; color: #303133; }
.checklist-content p { margin: 2px 0 0; font-size: 12px; color: #909399; }
.checklist-item.done .checklist-content strong { color: #67C23A; text-decoration: line-through; }
.foot-progress { margin-top: 4px; }

/* 足部护理 - 护理步骤 */
.care-steps { display: flex; flex-direction: column; gap: 14px; }
.care-step-item {
  padding: 16px;
  background: #fafafa;
  border-radius: 12px;
  border: 1px solid #ebeef5;
}
.care-step-header { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.care-step-num {
  flex-shrink: 0;
  width: 28px; height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF, #52C41A);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
}
.care-step-header h4 { margin: 0; font-size: 15px; color: #303133; font-weight: 600; }
.care-step-item > p { margin: 0; font-size: 13px; color: #606266; line-height: 1.7; }
.care-tips { margin-top: 10px; padding: 8px 12px; background: #fdf6ec; border-radius: 6px; font-size: 12px; color: #E6A23C; }
.tip-label { font-weight: 600; }

/* 足部护理 - 警示信号 */
.warning-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; margin-bottom: 14px; }
.warning-card { text-align: center; padding: 14px; background: #fef0f0; border-radius: 10px; border: 1px solid #fde2e2; }
.warning-icon { font-size: 28px; display: block; margin-bottom: 6px; }
.warning-card h4 { margin: 0 0 4px; font-size: 14px; color: #F56C6C; font-weight: 600; }
.warning-card p { margin: 0; font-size: 12px; color: #909399; }

/* 响应式 */
@media (max-width: 768px) {
  .severity-grid { grid-template-columns: 1fr; }
  .guide-grid { grid-template-columns: repeat(2, 1fr); }
  .warning-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>