<template>
	<div class="landing-page">
		<!-- 1. 首屏 Hero Section -->
		<div class="hero-section">
			<div class="hero-content">
				<div class="badge">PyTorch Deep Learning Powered</div>
				<h1 class="hero-title">云医智护<br /><span class="highlight">糖尿病诊断系统</span></h1>
				<p class="hero-desc">
					融合深度学习技术与医学特征工程,仅需 8 项基础生理指标,<br />
					即可在毫秒级内完成高精度的患病风险预测.
				</p>
				<div class="hero-actions">
					<el-button type="primary" size="large" round @click="scrollTo('principle')" class="btn-primary">
						<i class="el-icon-cpu"></i> 探索 AI 原理
					</el-button>
					<el-button plain size="large" round @click="scrollTo('prevention')" class="btn-plain">
						<i class="el-icon-guide"></i> 查看健康指南
					</el-button>
				</div>
			</div>
			<div class="hero-image">
				<!-- 使用 CSS 绘制一个抽象的神经网络示意图 -->
				<div class="network-viz">
					<div class="node layer1 n1"></div>
					<div class="node layer1 n2"></div>
					<div class="node layer1 n3"></div>
					<div class="node layer2 n1"></div>
					<div class="node layer2 n2"></div>
					<div class="node layer3 n1"></div>
					<div class="node layer3 n2"></div>
					<div class="node layer3 n3"></div>
					<svg class="connections">
						<line x1="20%" y1="30%" x2="50%" y2="50%" />
						<line x1="20%" y1="50%" x2="50%" y2="50%" />
						<line x1="20%" y1="70%" x2="50%" y2="50%" />
						<line x1="50%" y1="50%" x2="80%" y2="30%" />
						<line x1="50%" y1="50%" x2="80%" y2="50%" />
						<line x1="50%" y1="50%" x2="80%" y2="70%" />
					</svg>
				</div>
			</div>
		</div>

		<!-- 2. 核心优势 (Features) -->
		<div class="section container" id="features">
			<div class="section-header">
				<h2>系统核心优势</h2>
				<p>为什么选择我们的 AI 诊断方案?</p>
			</div>
			<el-row :gutter="30">
				<el-col :xs="24" :md="8">
					<div class="feature-card">
						<div class="f-icon bg-blue"><i class="el-icon-s-platform"></i></div>
						<h3>PyTorch 驱动</h3>
						<p>基于先进的深度神经网络架构,自动提取非线性特征,比传统逻辑回归更精准.</p>
					</div>
				</el-col>
				<el-col :xs="24" :md="8">
					<div class="feature-card">
						<div class="f-icon bg-green"><i class="el-icon-time"></i></div>
						<h3>毫秒级响应</h3>
						<p>模型经过量化优化,单次推理耗时低于 10ms,实现真正的实时诊断体验.</p>
					</div>
				</el-col>
				<el-col :xs="24" :md="8">
					<div class="feature-card">
						<div class="f-icon bg-purple"><i class="el-icon-lock"></i></div>
						<h3>隐私安全</h3>
						<p>支持本地化部署方案,敏感医疗数据不出域,符合医疗数据安全规范.</p>
					</div>
				</el-col>
			</el-row>
		</div>

		<!-- 3. AI 原理可视化 (静态图表模拟) -->
		<div class="section container bg-light" id="principle">
			<div class="section-header">
				<h2>AI 诊断工作原理</h2>
				<p>从原始数据到风险预测的黑盒解密</p>
			</div>

			<div class="principle-grid">
				<!-- 左侧:流程图 -->
				<div class="process-flow">
					<div class="flow-step">
						<div class="step-num">01</div>
						<div class="step-content">
							<h4>数据输入</h4>
							<p>血糖、BMI、年龄等<br />8 维生理特征</p>
						</div>
					</div>
					<div class="flow-arrow">➜</div>
					<div class="flow-step">
						<div class="step-num">02</div>
						<div class="step-content">
							<h4>特征标准化</h4>
							<p>Z-Score 归一化<br />消除量纲影响</p>
						</div>
					</div>
					<div class="flow-arrow">➜</div>
					<div class="flow-step highlight">
						<div class="step-num">03</div>
						<div class="step-content">
							<h4>深层推理</h4>
							<p>PyTorch 全连接层<br />Sigmoid 激活输出</p>
						</div>
					</div>
					<div class="flow-arrow">➜</div>
					<div class="flow-step">
						<div class="step-num">04</div>
						<div class="step-content">
							<h4>结果输出</h4>
							<p>患病概率 (0-1)<br />风险等级判定</p>
						</div>
					</div>
				</div>

				<!-- 右侧:特征重要性条形图 (纯 CSS 实现,无 ECharts 依赖) -->
				<div class="feature-importance">
					<h3>关键特征影响力分析</h3>
					<p class="sub-text">模型认为哪些指标对结果影响最大?</p>

					<div class="bar-chart">
						<div class="bar-item">
							<span class="label">血糖 (Glucose)</span>
							<div class="bar-track">
								<div class="bar-fill" style="width: 92%; background: #F56C6C;"></div>
							</div>
							<span class="value">92%</span>
						</div>
						<div class="bar-item">
							<span class="label">BMI 指数</span>
							<div class="bar-track">
								<div class="bar-fill" style="width: 85%; background: #E6A23C;"></div>
							</div>
							<span class="value">85%</span>
						</div>
						<div class="bar-item">
							<span class="label">年龄 (Age)</span>
							<div class="bar-track">
								<div class="bar-fill" style="width: 78%; background: #409EFF;"></div>
							</div>
							<span class="value">78%</span>
						</div>
						<div class="bar-item">
							<span class="label">胰岛素 (Insulin)</span>
							<div class="bar-track">
								<div class="bar-fill" style="width: 65%; background: #909399;"></div>
							</div>
							<span class="value">65%</span>
						</div>
						<div class="bar-item">
							<span class="label">家族史 (DPF)</span>
							<div class="bar-track">
								<div class="bar-fill" style="width: 45%; background: #909399;"></div>
							</div>
							<span class="value">45%</span>
						</div>
					</div>
				</div>
			</div>
		</div>

		<!-- 4. 健康科普 (Prevention) -->
		<div class="section container" id="prevention">
			<div class="section-header">
				<h2>糖尿病预防指南</h2>
				<p>早发现、早预防、早治疗</p>
			</div>
			<el-row :gutter="20">
				<el-col :xs="24" :lg="12">
					<div class="info-card warning-card">
						<div class="card-icon"><i class="el-icon-warning-outline"></i></div>
						<h3>警惕早期信号</h3>
						<ul class="check-list">
							<li><strong>多饮多尿</strong>:频繁口渴,夜尿增多.</li>
							<li><strong>体重骤降</strong>:未刻意减肥但体重明显下降.</li>
							<li><strong>视力模糊</strong>:看东西模糊,像隔了一层雾.</li>
							<li><strong>伤口难愈</strong>:小伤口长时间不愈合,易感染.</li>
							<li><strong>极度疲劳</strong>:即使休息后仍感到浑身无力.</li>
						</ul>
					</div>
				</el-col>
				<el-col :xs="24" :lg="12">
					<div class="info-card success-card">
						<div class="card-icon"><i class="el-icon-sunny"></i></div>
						<h3>科学预防建议</h3>
						<div class="advice-grid">
							<div class="advice-item">
								<i class="el-icon-food"></i>
								<div>
									<h4>均衡饮食</h4>
									<p>低糖低脂,多吃粗粮蔬菜,控制总热量.</p>
								</div>
							</div>
							<div class="advice-item">
								<i class="el-icon-running"></i>
								<div>
									<h4>适量运动</h4>
									<p>每周至少 150 分钟中等强度有氧运动.</p>
								</div>
							</div>
							<div class="advice-item">
								<i class="el-icon-monitor"></i>
								<div>
									<h4>定期筛查</h4>
									<p>45 岁以上人群建议每年检测空腹血糖.</p>
								</div>
							</div>
							<div class="advice-item">
								<i class="el-icon-moon"></i>
								<div>
									<h4>规律作息</h4>
									<p>避免熬夜,保持良好心态,减轻压力.</p>
								</div>
							</div>
						</div>
					</div>
				</el-col>
			</el-row>
		</div>

		<!-- 5. 底部 Footer -->
		<footer class="site-footer">
			<div class="footer-content">
				<div class="footer-logo">
					<i class="el-icon-s-medal"></i>
					<span>PyTorch Diabetes Diagnosis</span>
				</div>
				<p class="disclaimer">
					免责声明:本系统仅为辅助诊断工具,基于算法模型生成预测结果,<br />
					不能替代专业医生的临床诊断.如有不适,请及时前往正规医院就诊.
				</p>
				<div class="copyright">
					 2026 智能医疗实验室 | 技术栈:Vue.js + PyTorch
				</div>
			</div>
		</footer>
		<!-- 回到顶部按钮-->
		<div class="back-top" @click="backToTop" v-show="isShow">
			<span class="icon">↑</span>
			<span class="text">回顶部</span>
		</div>
	</div>
</template>


<style scoped>
	.back-top {
		position: fixed;
		bottom: 80px;
		right: 30px;
		width: 60px;
		height: 60px;
		background: linear-gradient(135deg, #36b37e,#4af7ac );/* #67c2ff */
		border-radius: 50%;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		color: white;
		font-size: 12px;
		box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
		cursor: pointer;
		z-index: 999;
		transition: all 0.3s ease;
	}

	.back-top:hover {
		transform: scale(1.1);
		box-shadow: 0 6px 18px rgba(64, 158, 255, 0.5);
	}

	.icon {
		font-size: 18px;
		font-weight: bold;
		margin-bottom: 2px;
	}
</style>

<script>
	export default {
		name: "StaticLandingPage",
		data() {
			return {
				isShow: false, // 控制按钮显示隐藏
				user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {}
			};
		},
		mounted() {
			// 监听页面滚动
			window.addEventListener("scroll", this.handleScroll);
		},
		destroyed() {
			// 页面销毁时移除监听,防止报错
			window.removeEventListener("scroll", this.handleScroll);
		},
		methods: {
			// 滚动判断:滚动超过300px显示按钮
			handleScroll() {
				this.isShow = document.documentElement.scrollTop > 300;
			},
			// 平滑回到顶部
			backToTop() {
				window.scrollTo({
					top: 0,
					behavior: "smooth"
				});
			},
			scrollTo(id) {
				const element = document.getElementById(id);
				if (element) {
					element.scrollIntoView({
						behavior: 'smooth',
						block: 'start'
					});
				}
			}
		}
	};
	// 	export default {
	// 	  data() {
	// 	    return {
	// 	      isShow: false // 控制按钮显示隐藏
	// 	    }
	// 	  },
	// 	  mounted() {
	// 	    // 监听页面滚动
	// 	    window.addEventListener("scroll", this.handleScroll);
	// 	  },
	// 	  destroyed() {
	// 	    // 页面销毁时移除监听,防止报错
	// 	    window.removeEventListener("scroll", this.handleScroll);
	// 	  },
	// 	  methods: {
	// 	    // 滚动判断:滚动超过300px显示按钮
	// 	    handleScroll() {
	// 	      this.isShow = document.documentElement.scrollTop > 300;
	// 	    },
	// 	    // 平滑回到顶部
	// 	    backToTop() {
	// 	      window.scrollTo({
	// 	        top: 0,
	// 	        behavior: "smooth" // 平滑动画,高级感拉满
	// 	      });
	// 	    }
	// 	  }
	// 	};
	// export default {
	//   name: "StaticLandingPage",
	//   data() {
	//     return {
	//       user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {}
	//     };
	//   },
	//   methods: {
	//     scrollTo(id) {
	//       const element = document.getElementById(id);
	//       if (element) {
	//         element.scrollIntoView({ behavior: 'smooth', block: 'start' });
	//       }
	//     }
	//   }
	// };
</script>



<style scoped>
	/* 全局变量与重置 */
	.landing-page {
		font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", Arial, sans-serif;
		color: #333;
		line-height: 1.6;
		background: #fff;
	}

	.container {
		max-width: 1200px;
		margin: 0 auto;
		padding: 0 20px;
	}

	.section {
		padding: 80px 0;
	}

	.bg-light {
		background-color: #f8f9fa;
	}

	.section-header {
		text-align: center;
		margin-bottom: 60px;
	}

	.section-header h2 {
		font-size: 32px;
		color: #303133;
		margin-bottom: 10px;
		font-weight: 700;
	}

	.section-header p {
		font-size: 16px;
		color: #909399;
	}

	/* 1. Hero Section */
	.hero-section {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 100px 5%;
		background: linear-gradient(135deg, #00c6fb 0%, #005bea 100%);
		color: #fff;
		min-height: 80vh;
		position: relative;
		overflow: hidden;
	}

	.hero-content {
		flex: 1;
		max-width: 600px;
		z-index: 2;
	}

	.badge {
		display: inline-block;
		background: rgba(255, 255, 255, 0.2);
		padding: 6px 12px;
		border-radius: 20px;
		font-size: 12px;
		font-weight: 600;
		letter-spacing: 1px;
		margin-bottom: 20px;
		backdrop-filter: blur(5px);
	}

	.hero-title {
		font-size: 48px;
		line-height: 1.2;
		margin: 0 0 20px 0;
		font-weight: 800;
	}

	.hero-title .highlight {
		color: #fffbe6;
		display: block;
		margin-top: 10px;
	}

	.hero-desc {
		font-size: 18px;
		opacity: 0.9;
		margin-bottom: 40px;
		line-height: 1.8;
	}

	.hero-actions {
		display: flex;
		gap: 20px;
	}

	.btn-primary {
		background: #fff;
		color: #005bea;
		border: none;
		font-weight: bold;
		padding: 15px 35px;
		font-size: 16px;
		box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
	}

	.btn-primary:hover {
		transform: translateY(-2px);
		box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
	}

	.btn-plain {
		background: transparent;
		color: #fff;
		border: 2px solid rgba(255, 255, 255, 0.6);
		font-weight: bold;
		padding: 15px 35px;
		font-size: 16px;
	}

	.btn-plain:hover {
		background: rgba(255, 255, 255, 0.1);
		border-color: #fff;
	}

	/* 抽象神经网络动画 */
	.hero-image {
		flex: 1;
		display: flex;
		justify-content: center;
		align-items: center;
		position: relative;
		height: 400px;
	}

	.network-viz {
		position: relative;
		width: 300px;
		height: 300px;
	}

	.node {
		position: absolute;
		width: 20px;
		height: 20px;
		border-radius: 50%;
		background: #fff;
		box-shadow: 0 0 15px rgba(255, 255, 255, 0.8);
		animation: pulse 2s infinite;
	}

	.layer1 .n1 {
		top: 30%;
		left: 20%;
		animation-delay: 0s;
	}

	.layer1 .n2 {
		top: 50%;
		left: 20%;
		animation-delay: 0.2s;
	}

	.layer1 .n3 {
		top: 70%;
		left: 20%;
		animation-delay: 0.4s;
	}

	.layer2 .n1 {
		top: 40%;
		left: 50%;
		animation-delay: 0.6s;
	}

	.layer2 .n2 {
		top: 60%;
		left: 50%;
		animation-delay: 0.8s;
	}

	.layer3 .n1 {
		top: 30%;
		left: 80%;
		animation-delay: 1s;
	}

	.layer3 .n2 {
		top: 50%;
		left: 80%;
		animation-delay: 1.2s;
	}

	.layer3 .n3 {
		top: 70%;
		left: 80%;
		animation-delay: 1.4s;
	}

	.connections {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		z-index: -1;
	}

	.connections line {
		stroke: rgba(255, 255, 255, 0.4);
		stroke-width: 2;
	}

	@keyframes pulse {
		0% {
			transform: scale(1);
			opacity: 1;
		}

		50% {
			transform: scale(1.2);
			opacity: 0.7;
		}

		100% {
			transform: scale(1);
			opacity: 1;
		}
	}

	/* 2. Features */
	.feature-card {
		background: #fff;
		padding: 40px 30px;
		border-radius: 12px;
		text-align: center;
		box-shadow: 0 5px 20px rgba(0, 0, 0, 0.05);
		transition: all 0.3s;
		height: 100%;
		border: 1px solid #eee;
	}

	.feature-card:hover {
		transform: translateY(-10px);
		box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
	}

	.f-icon {
		width: 70px;
		height: 70px;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		margin: 0 auto 20px;
		font-size: 32px;
		color: #fff;
	}

	.bg-blue {
		background: linear-gradient(135deg, #409EFF, #a0cfff);
	}

	.bg-green {
		background: linear-gradient(135deg, #67C23A, #b3e19d);
	}

	.bg-purple {
		background: linear-gradient(135deg, #909399, #d3d4d6);
	}

	/* Fix purple */
	.bg-purple {
		background: linear-gradient(135deg, #8E44AD, #bb8fce);
	}

	.feature-card h3 {
		font-size: 20px;
		margin-bottom: 15px;
		color: #303133;
	}

	.feature-card p {
		color: #909399;
		font-size: 14px;
		line-height: 1.8;
	}

	/* 3. Principle */
	.principle-grid {
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: 50px;
		align-items: center;
	}

	.process-flow {
		display: flex;
		flex-direction: column;
		gap: 20px;
	}

	.flow-step {
		display: flex;
		align-items: center;
		background: #fff;
		padding: 20px;
		border-radius: 10px;
		box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
		position: relative;
	}

	.flow-step.highlight {
		border: 2px solid #409EFF;
		background: #ecf5ff;
	}

	.step-num {
		font-size: 24px;
		font-weight: 800;
		color: #ddd;
		margin-right: 20px;
		width: 40px;
		text-align: center;
	}

	.flow-step.highlight .step-num {
		color: #409EFF;
	}

	.step-content h4 {
		margin: 0 0 5px;
		font-size: 16px;
		color: #303133;
	}

	.step-content p {
		margin: 0;
		font-size: 13px;
		color: #909399;
	}

	.flow-arrow {
		text-align: center;
		color: #ccc;
		font-size: 20px;
		line-height: 0.5;
	}

	/* 特征重要性条形图 (CSS Only) */
	.feature-importance {
		background: #fff;
		padding: 30px;
		border-radius: 12px;
		box-shadow: 0 5px 20px rgba(0, 0, 0, 0.05);
	}

	.feature-importance h3 {
		margin: 0 0 10px;
		font-size: 18px;
		color: #303133;
	}

	.sub-text {
		font-size: 13px;
		color: #909399;
		margin-bottom: 25px;
	}

	.bar-item {
		display: flex;
		align-items: center;
		margin-bottom: 15px;
	}

	.bar-item .label {
		width: 100px;
		font-size: 13px;
		color: #606266;
		font-weight: 500;
	}

	.bar-track {
		flex: 1;
		height: 10px;
		background: #ebeef5;
		border-radius: 5px;
		margin: 0 15px;
		overflow: hidden;
	}

	.bar-fill {
		height: 100%;
		border-radius: 5px;
		transition: width 1s ease-out;
	}

	.bar-item .value {
		width: 40px;
		text-align: right;
		font-size: 13px;
		font-weight: bold;
		color: #606266;
	}

	/* 4. Prevention Cards */
	.info-card {
		padding: 40px;
		border-radius: 12px;
		height: 100%;
		box-shadow: 0 5px 20px rgba(0, 0, 0, 0.05);
	}

	.warning-card {
		background: #fef0f0;
		border: 1px solid #fde2e2;
	}

	.success-card {
		background: #f0f9eb;
		border: 1px solid #e1f3d8;
	}

	.card-icon {
		font-size: 40px;
		margin-bottom: 20px;
	}

	.warning-card .card-icon {
		color: #F56C6C;
	}

	.success-card .card-icon {
		color: #67C23A;
	}

	.info-card h3 {
		font-size: 22px;
		margin-bottom: 25px;
		color: #303133;
	}

	.check-list {
		list-style: none;
		padding: 0;
		margin: 0;
	}

	.check-list li {
		margin-bottom: 15px;
		font-size: 14px;
		color: #606266;
		display: flex;
		align-items: flex-start;
	}

	.check-list li::before {
		content: "•";
		color: #F56C6C;
		font-weight: bold;
		margin-right: 10px;
		font-size: 18px;
		line-height: 1;
	}

	.check-list strong {
		color: #303133;
		margin-right: 5px;
	}

	.advice-grid {
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: 20px;
	}

	.advice-item {
		display: flex;
		gap: 15px;
		align-items: flex-start;
	}

	.advice-item i {
		font-size: 24px;
		color: #67C23A;
		flex-shrink: 0;
		margin-top: 2px;
	}

	.advice-item h4 {
		margin: 0 0 5px;
		font-size: 15px;
		color: #303133;
	}

	.advice-item p {
		margin: 0;
		font-size: 13px;
		color: #606266;
		line-height: 1.5;
	}

	/* 5. Footer */
	.site-footer {
		background: #303133;
		color: #909399;
		padding: 60px 0 30px;
		text-align: center;
	}

	.footer-logo {
		font-size: 20px;
		color: #fff;
		margin-bottom: 20px;
		font-weight: bold;
	}

	.footer-logo i {
		margin-right: 10px;
		color: #409EFF;
	}

	.disclaimer {
		font-size: 13px;
		max-width: 800px;
		margin: 0 auto 30px;
		line-height: 1.8;
		opacity: 0.7;
	}

	.copyright {
		font-size: 12px;
		border-top: 1px solid #4c4d4f;
		padding-top: 20px;
		opacity: 0.5;
	}

	/* 响应式调整 */
	@media (max-width: 768px) {
		.hero-section {
			flex-direction: column;
			text-align: center;
			padding-top: 60px;
			min-height: auto;
		}

		.hero-content {
			margin-bottom: 40px;
		}

		.hero-actions {
			justify-content: center;
		}

		.hero-title {
			font-size: 32px;
		}

		.principle-grid {
			grid-template-columns: 1fr;
		}

		.flow-step {
			flex-direction: row;
		}

		.flow-arrow {
			transform: rotate(90deg);
			margin: 10px 0;
		}

		.advice-grid {
			grid-template-columns: 1fr;
		}
	}
</style>