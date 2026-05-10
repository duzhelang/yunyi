<template>
	<div class="landing-page">
		<!-- 1. 首屏 Hero Section -->
		<div class="hero-section">
			<!-- 顶部用户导航栏 -->
			<div class="hero-top-bar">
				<div class="top-bar-inner">
					<div class="logo-area">
						<span class="logo-icon">🏥</span>
						<span class="logo-text">云医智护</span>
					</div>
					<div class="user-area">
						<template v-if="user && user.username">
							<div class="user-greeting">
								<span class="greeting-wave">👋</span>
								<span class="greeting-text">Hi, {{ user.nickname || user.username }}</span>
							</div>
							<div class="user-avatar" @click="goToPerson">
								<span class="avatar-emoji">🧑‍⚕️</span>
								<span class="avatar-ring"></span>
							</div>
						</template>
						<template v-else>
							<div class="login-prompt" @click="goToLogin">
								<span class="login-icon">✨</span>
								<span class="login-text">登录 / 注册</span>
								<span class="login-arrow">→</span>
							</div>
						</template>
					</div>
				</div>
			</div>

			<div class="hero-content">
				<div class="badge">由PyTorch 深度学习驱动</div>
				<h1 class="hero-title">云&ensp;医&ensp;智&ensp;护<br /><span class="highlight">&emsp;&emsp;糖尿病诊断系统</span></h1>
				<p class="hero-desc">
					融合深度学习技术与医学特征工程,仅需 8 项基础生理指标,<br />
					即可在毫秒级内完成高精度的患病风险预测.
				</p>

				<!-- 功能快捷入口 -->
				<div class="quick-entries">
					<div class="entry-card entry-risk" @click="goToRiskCheck">
						<div class="entry-bg-icon">🩺</div>
						<div class="entry-content">
							<div class="entry-icon-wrap">
								<span class="entry-emoji">⚡</span>
							</div>
							<div class="entry-info">
								<h4>风险快检</h4>
								<p>30秒极速评估</p>
							</div>
						</div>
						<div class="entry-shine"></div>
						<span class="entry-hot-tag">HOT</span>
					</div>
					<div class="entry-card entry-chat" @click="goToAiChat">
						<div class="entry-bg-icon">💬</div>
						<div class="entry-content">
							<div class="entry-icon-wrap">
								<span class="entry-emoji">🤖</span>
							</div>
							<div class="entry-info">
								<h4>智能问答</h4>
								<p>AI 在线答疑</p>
							</div>
						</div>
						<div class="entry-shine"></div>
						<span class="entry-new-tag">NEW</span>
					</div>
				</div>

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
					<div class="flow-step" :class="{ 'active': activeStep === 1 }" @click="toggleStep(1)">
						<div class="step-num">01</div>
						<div class="step-content">
							<h4>数据输入</h4>
							<p>血糖、BMI、年龄等<br />8 维生理特征</p>
						</div>
						<div class="step-detail" v-if="activeStep === 1">
							<div class="detail-icon">{{ stepDetails[1].icon }}</div>
							<p>{{ stepDetails[1].description }}</p>
						</div>
					</div>
					<div class="flow-arrow" :class="{ 'pulse': activeStep === 1 }">➜</div>
					<div class="flow-step" :class="{ 'active': activeStep === 2 }" @click="toggleStep(2)">
						<div class="step-num">02</div>
						<div class="step-content">
							<h4>特征标准化</h4>
							<p>Z-Score 归一化<br />消除量纲影响</p>
						</div>
						<div class="step-detail" v-if="activeStep === 2">
							<div class="detail-icon">{{ stepDetails[2].icon }}</div>
							<p>{{ stepDetails[2].description }}</p>
						</div>
					</div>
					<div class="flow-arrow" :class="{ 'pulse': activeStep === 2 }">➜</div>
					<div class="flow-step highlight" :class="{ 'active': activeStep === 3 }" @click="toggleStep(3)">
						<div class="step-num">03</div>
						<div class="step-content">
							<h4>深层推理</h4>
							<p>PyTorch 全连接层<br />Sigmoid 激活输出</p>
						</div>
						<div class="step-detail" v-if="activeStep === 3">
							<div class="detail-icon">{{ stepDetails[3].icon }}</div>
							<p>{{ stepDetails[3].description }}</p>
						</div>
					</div>
					<div class="flow-arrow" :class="{ 'pulse': activeStep === 3 }">➜</div>
					<div class="flow-step" :class="{ 'active': activeStep === 4 }" @click="toggleStep(4)">
						<div class="step-num">04</div>
						<div class="step-content">
							<h4>结果输出</h4>
							<p>患病概率 (0-1)<br />风险等级判定</p>
						</div>
						<div class="step-detail" v-if="activeStep === 4">
							<div class="detail-icon">{{ stepDetails[4].icon }}</div>
							<p>{{ stepDetails[4].description }}</p>
						</div>
					</div>
				</div>

				<!-- 右侧:特征重要性条形图 (纯 CSS 实现,无 ECharts 依赖) -->
				<div class="feature-importance">
					<h3>关键特征影响力分析</h3>
					<p class="sub-text">模型认为哪些指标对结果影响最大?</p>

					<div class="bar-chart">
						<div class="bar-item" 
							v-for="(item, index) in featureData" 
							:key="item.label"
							:class="{ 'active': activeBarIndex === index }"
							@mouseenter="setActiveBar(index)"
							@mouseleave="clearActiveBar()">
							<span class="label">{{ item.label }}</span>
							<div class="bar-track">
								<div class="bar-fill" 
									:style="{ 
										width: item.width + '%', 
										background: activeBarIndex === index ? item.color : item.color + 'CC',
										boxShadow: activeBarIndex === index ? `0 0 12px ${item.color}66` : 'none'
									}">
									<div class="bar-shine" v-if="activeBarIndex === index"></div>
								</div>
							</div>
							<span class="value" :style="{ color: activeBarIndex === index ? item.color : '#606266' }">
								{{ item.currentValue }}%
							</span>
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
          <span>云医智护——全场景糖尿病健康服务一体化系统</span>
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
				isShow: false,
				user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
				activeStep: null,
				isBarsAnimated: false,
				isPreventionAnimated: false,
				activeBarIndex: -1,
				featureData: [
					{ label: '血糖 (Glucose)', value: 92, color: '#F56C6C', currentValue: 0, width: 0 },
					{ label: 'BMI 指数', value: 85, color: '#E6A23C', currentValue: 0, width: 0 },
					{ label: '年龄 (Age)', value: 78, color: '#409EFF', currentValue: 0, width: 0 },
					{ label: '胰岛素 (Insulin)', value: 65, color: '#909399', currentValue: 0, width: 0 },
					{ label: '家族史 (DPF)', value: 45, color: '#909399', currentValue: 0, width: 0 }
				],
				stepDetails: {
					1: {
						title: '数据输入',
						description: '系统接收8维生理特征数据，包括血糖值、BMI指数、年龄、胰岛素水平、血压、怀孕次数、皮肤厚度和糖尿病家族史。这些原始数据是AI诊断的基础。',
						icon: '📊'
					},
					2: {
						title: '特征标准化',
						description: '使用Z-Score归一化方法处理原始数据，消除不同特征之间的量纲差异，确保每个特征对模型的影响权重均衡。',
						icon: '⚙️'
					},
					3: {
						title: '深层推理',
						description: 'PyTorch全连接神经网络进行深层特征提取和模式识别，通过Sigmoid激活函数输出0-1之间的概率值。',
						icon: '🧠'
					},
					4: {
						title: '结果输出',
						description: '输出患病概率和风险等级判定，为用户提供直观的健康评估结果和个性化建议。',
						icon: '📋'
					}
				}
			};
		},
		mounted() {
			window.addEventListener("scroll", this.handleScroll);
			this.initIntersectionObserver();
		},
		destroyed() {
			window.removeEventListener("scroll", this.handleScroll);
			if (this.observer) {
				this.observer.disconnect();
			}
		},
		methods: {
			handleScroll() {
				this.isShow = document.documentElement.scrollTop > 300;
			},
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
			},
			initIntersectionObserver() {
				this.observer = new IntersectionObserver((entries) => {
					entries.forEach(entry => {
						if (entry.isIntersecting) {
							const id = entry.target.id;
							if (id === 'principle' && !this.isBarsAnimated) {
								this.animateBars();
							}
							if (id === 'prevention' && !this.isPreventionAnimated) {
								this.animatePrevention();
							}
						}
					});
				}, { threshold: 0.3 });

				this.$nextTick(() => {
					const principleSection = document.getElementById('principle');
					const preventionSection = document.getElementById('prevention');
					if (principleSection) this.observer.observe(principleSection);
					if (preventionSection) this.observer.observe(preventionSection);
				});
			},
			animateBars() {
				this.isBarsAnimated = true;
				this.featureData.forEach((item, index) => {
					setTimeout(() => {
						this.animateBarItem(index, item.value);
					}, index * 300);
				});
			},
			animateBarItem(index, targetValue) {
				const duration = 1500;
				const startTime = Date.now();
				const animate = () => {
					const elapsed = Date.now() - startTime;
					const progress = Math.min(elapsed / duration, 1);
					const easeOut = 1 - Math.pow(1 - progress, 3);
					this.featureData[index].currentValue = Math.round(targetValue * easeOut);
					this.featureData[index].width = targetValue * easeOut;
					if (progress < 1) {
						requestAnimationFrame(animate);
					} else {
						this.startBreathAnimation(index, targetValue);
					}
				};
				requestAnimationFrame(animate);
			},
			startBreathAnimation(index, targetValue) {
				const breathAmount = 3;
				let growing = true;
				let currentWidth = targetValue;
				const breath = () => {
					if (this.activeBarIndex === index) {
						currentWidth = targetValue;
						this.featureData[index].width = currentWidth;
						return;
					}
					if (growing) {
						currentWidth += 0.15;
						if (currentWidth >= targetValue + breathAmount) {
							growing = false;
						}
					} else {
						currentWidth -= 0.15;
						if (currentWidth <= targetValue - breathAmount) {
							growing = true;
						}
					}
					this.featureData[index].width = currentWidth;
					requestAnimationFrame(breath);
				};
				setTimeout(() => {
					requestAnimationFrame(breath);
				}, index * 500);
			},
			setActiveBar(index) {
				this.activeBarIndex = index;
			},
			clearActiveBar() {
				this.activeBarIndex = -1;
			},
			animatePrevention() {
				this.isPreventionAnimated = true;
				const items = document.querySelectorAll('.check-list li, .advice-item');
				items.forEach((item, index) => {
					item.style.opacity = '0';
					item.style.transform = 'translateY(20px)';
					setTimeout(() => {
						item.style.transition = 'all 0.5s ease';
						item.style.opacity = '1';
						item.style.transform = 'translateY(0)';
					}, index * 150);
				});
			},
			toggleStep(step) {
				this.activeStep = this.activeStep === step ? null : step;
			},
			goToPerson() {
				this.$router.push('/person');
			},
			goToLogin() {
				this.$router.push('/login');
			},
			goToRiskCheck() {
				if (this.user && this.user.username) {
					this.$router.push('/risk-quick');
				} else {
					this.$router.push('/login');
				}
			},
			goToAiChat() {
				if (this.user && this.user.username) {
					this.$router.push('/chat');
				} else {
					this.$router.push('/login');
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
		flex-wrap: wrap;
		align-items: center;
		justify-content: space-between;
		padding: 0 5% 100px;
		background: linear-gradient(135deg, #00c6fb 0%, #005bea 100%);
		color: #fff;
		min-height: 80vh;
		position: relative;
		overflow: hidden;
	}

	/* 顶部用户导航栏 */
	.hero-top-bar {
		width: 100%;
		padding: 20px 0;
		position: relative;
		z-index: 10;
	}

	.top-bar-inner {
		display: flex;
		align-items: center;
		justify-content: space-between;
		max-width: 1400px;
		margin: 0 auto;
	}

	.logo-area {
		display: flex;
		align-items: center;
		gap: 10px;
	}

	.logo-icon {
		font-size: 28px;
		animation: logoFloat 3s ease-in-out infinite;
	}

	@keyframes logoFloat {
		0%, 100% { transform: translateY(0); }
		50% { transform: translateY(-4px); }
	}

	.logo-text {
		font-size: 20px;
		font-weight: 700;
		letter-spacing: 2px;
		text-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
	}

	.user-area {
		display: flex;
		align-items: center;
		gap: 16px;
	}

	.user-greeting {
		display: flex;
		align-items: center;
		gap: 8px;
		background: rgba(255, 255, 255, 0.15);
		backdrop-filter: blur(10px);
		-webkit-backdrop-filter: blur(10px);
		padding: 8px 18px;
		border-radius: 20px;
		border: 1px solid rgba(255, 255, 255, 0.2);
	}

	.greeting-wave {
		font-size: 18px;
		animation: waveHand 1.5s ease-in-out infinite;
		transform-origin: 70% 70%;
	}

	@keyframes waveHand {
		0%, 100% { transform: rotate(0deg); }
		25% { transform: rotate(15deg); }
		50% { transform: rotate(-5deg); }
		75% { transform: rotate(10deg); }
	}

	.greeting-text {
		font-size: 14px;
		font-weight: 500;
	}

	.user-avatar {
		position: relative;
		width: 44px;
		height: 44px;
		border-radius: 50%;
		background: rgba(255, 255, 255, 0.2);
		backdrop-filter: blur(8px);
		-webkit-backdrop-filter: blur(8px);
		display: flex;
		align-items: center;
		justify-content: center;
		cursor: pointer;
		transition: all 0.3s ease;
		border: 2px solid rgba(255, 255, 255, 0.3);
	}

	.user-avatar:hover {
		transform: scale(1.1) rotate(5deg);
		border-color: rgba(255, 255, 255, 0.6);
		box-shadow: 0 0 20px rgba(255, 255, 255, 0.3);
	}

	.avatar-emoji {
		font-size: 22px;
	}

	.avatar-ring {
		position: absolute;
		top: -3px;
		left: -3px;
		right: -3px;
		bottom: -3px;
		border-radius: 50%;
		border: 2px solid transparent;
		border-top-color: #fff;
		animation: avatarRing 2s linear infinite;
	}

	@keyframes avatarRing {
		0% { transform: rotate(0deg); }
		100% { transform: rotate(360deg); }
	}

	.login-prompt {
		display: flex;
		align-items: center;
		gap: 8px;
		background: rgba(255, 255, 255, 0.2);
		backdrop-filter: blur(10px);
		-webkit-backdrop-filter: blur(10px);
		padding: 10px 24px;
		border-radius: 25px;
		border: 1px solid rgba(255, 255, 255, 0.3);
		cursor: pointer;
		transition: all 0.3s ease;
	}

	.login-prompt:hover {
		background: rgba(255, 255, 255, 0.35);
		transform: translateY(-2px);
		box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
	}

	.login-icon {
		font-size: 16px;
		animation: sparkle 1.5s ease-in-out infinite;
	}

	@keyframes sparkle {
		0%, 100% { transform: scale(1) rotate(0deg); }
		50% { transform: scale(1.2) rotate(15deg); }
	}

	.login-text {
		font-size: 14px;
		font-weight: 600;
		letter-spacing: 1px;
	}

	.login-arrow {
		font-size: 16px;
		transition: transform 0.3s ease;
	}

	.login-prompt:hover .login-arrow {
		transform: translateX(4px);
	}

	.hero-content {
		flex: 1;
		max-width: 600px;
		z-index: 2;
		padding-top: 20px;
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

	/* 快捷入口卡片 */
	.quick-entries {
		display: flex;
		gap: 20px;
		margin-bottom: 30px;
	}

	.entry-card {
		position: relative;
		background: rgba(255, 255, 255, 0.15);
		backdrop-filter: blur(15px);
		-webkit-backdrop-filter: blur(15px);
		border: 1px solid rgba(255, 255, 255, 0.25);
		border-radius: 16px;
		padding: 18px 24px;
		cursor: pointer;
		transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
		overflow: hidden;
		min-width: 200px;
		flex: 1;
	}

	.entry-card:hover {
		transform: translateY(-5px) scale(1.02);
		background: rgba(255, 255, 255, 0.25);
		border-color: rgba(255, 255, 255, 0.5);
		box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
	}

	.entry-risk:hover {
		box-shadow: 0 15px 40px rgba(255, 107, 107, 0.2);
	}

	.entry-chat:hover {
		box-shadow: 0 15px 40px rgba(100, 200, 255, 0.2);
	}

	.entry-bg-icon {
		position: absolute;
		right: -5px;
		bottom: -10px;
		font-size: 64px;
		opacity: 0.15;
		transition: all 0.3s ease;
	}

	.entry-card:hover .entry-bg-icon {
		opacity: 0.25;
		transform: scale(1.1) rotate(-5deg);
	}

	.entry-content {
		display: flex;
		align-items: center;
		gap: 14px;
		position: relative;
		z-index: 2;
	}

	.entry-icon-wrap {
		width: 48px;
		height: 48px;
		border-radius: 14px;
		display: flex;
		align-items: center;
		justify-content: center;
		background: rgba(255, 255, 255, 0.2);
		border: 1px solid rgba(255, 255, 255, 0.3);
		transition: all 0.3s ease;
	}

	.entry-risk .entry-icon-wrap {
		background: linear-gradient(135deg, rgba(255, 107, 107, 0.3), rgba(255, 166, 77, 0.3));
	}

	.entry-chat .entry-icon-wrap {
		background: linear-gradient(135deg, rgba(100, 200, 255, 0.3), rgba(150, 100, 255, 0.3));
	}

	.entry-card:hover .entry-icon-wrap {
		transform: rotate(-8deg) scale(1.1);
	}

	.entry-emoji {
		font-size: 24px;
	}

	.entry-info h4 {
		margin: 0 0 2px;
		font-size: 17px;
		font-weight: 700;
		letter-spacing: 1px;
	}

	.entry-info p {
		margin: 0;
		font-size: 12px;
		opacity: 0.8;
		font-weight: 400;
	}

	.entry-shine {
		position: absolute;
		top: 0;
		left: -100%;
		width: 60%;
		height: 100%;
		background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.15), transparent);
		transition: left 0.6s ease;
	}

	.entry-card:hover .entry-shine {
		left: 150%;
	}

	.entry-hot-tag,
	.entry-new-tag {
		position: absolute;
		top: 10px;
		right: 10px;
		font-size: 10px;
		font-weight: 700;
		padding: 3px 8px;
		border-radius: 8px;
		letter-spacing: 0.5px;
		animation: tagBounce 2s ease-in-out infinite;
	}

	.entry-hot-tag {
		background: linear-gradient(135deg, #ff6b6b, #ffa64d);
		color: #fff;
	}

	.entry-new-tag {
		background: linear-gradient(135deg, #64c8ff, #9664ff);
		color: #fff;
	}

	@keyframes tagBounce {
		0%, 100% { transform: translateY(0) scale(1); }
		50% { transform: translateY(-3px) scale(1.05); }
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
		cursor: pointer;
		transition: all 0.3s ease;
		flex-wrap: wrap;
	}

	.flow-step:hover {
		transform: translateY(-3px);
		box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
	}

	.flow-step.active {
		border: 2px solid #409EFF;
		background: #f0f7ff;
	}

	.flow-step.highlight {
		border: 2px solid #409EFF;
		background: #ecf5ff;
	}

	.flow-step.highlight.active {
		border-color: #337ecc;
		background: #d9ecff;
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
		transition: all 0.3s ease;
	}

	.flow-arrow.pulse {
		animation: pulse 1.5s infinite;
		color: #409EFF;
	}

	@keyframes pulse {
		0% { opacity: 1; transform: scale(1); }
		50% { opacity: 0.7; transform: scale(1.2); }
		100% { opacity: 1; transform: scale(1); }
	}

	.step-detail {
		width: 100%;
		margin-top: 15px;
		padding-top: 15px;
		border-top: 1px dashed #e4e7ed;
		display: flex;
		align-items: flex-start;
		gap: 12px;
		animation: fadeIn 0.3s ease;
	}

	@keyframes fadeIn {
		from { opacity: 0; transform: translateY(-10px); }
		to { opacity: 1; transform: translateY(0); }
	}

	.detail-icon {
		font-size: 28px;
		flex-shrink: 0;
	}

	.step-detail p {
		margin: 0;
		font-size: 13px;
		color: #606266;
		line-height: 1.6;
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
		cursor: pointer;
		transition: all 0.3s ease;
		padding: 8px 12px;
		border-radius: 8px;
	}

	.bar-item:hover,
	.bar-item.active {
		background: rgba(64, 158, 255, 0.05);
		transform: translateX(5px);
	}

	.bar-item .label {
		width: 100px;
		font-size: 13px;
		color: #606266;
		font-weight: 500;
		transition: color 0.3s ease;
	}

	.bar-item.active .label {
		color: #303133;
		font-weight: 600;
	}

	.bar-track {
		flex: 1;
		height: 10px;
		background: #ebeef5;
		border-radius: 5px;
		margin: 0 15px;
		overflow: hidden;
		position: relative;
	}

	.bar-track::after {
		content: '';
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
		animation: trackShimmer 3s ease-in-out infinite;
	}

	@keyframes trackShimmer {
		0% { transform: translateX(-100%); }
		100% { transform: translateX(100%); }
	}

	.bar-fill {
		height: 100%;
		border-radius: 5px;
		transition: width 1s ease-out, background 0.3s ease, box-shadow 0.3s ease;
		position: relative;
		overflow: hidden;
	}

	.bar-shine {
		position: absolute;
		top: 0;
		left: -100%;
		width: 100%;
		height: 100%;
		background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
		animation: shine 1.5s ease-in-out infinite;
	}

	@keyframes shine {
		0% { left: -100%; }
		50% { left: 100%; }
		100% { left: 100%; }
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
		transition: all 0.3s ease;
	}

	.info-card:hover {
		transform: translateY(-5px);
		box-shadow: 0 12px 30px rgba(0, 0, 0, 0.1);
	}

	.warning-card {
		background: #fef0f0;
		border: 1px solid #fde2e2;
	}

	.warning-card:hover {
		border-color: #f56c6c;
	}

	.success-card {
		background: #f0f9eb;
		border: 1px solid #e1f3d8;
	}

	.success-card:hover {
		border-color: #67c23a;
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
		transition: all 0.3s ease;
	}

	.check-list li:hover {
		color: #303133;
		transform: translateX(5px);
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
		transition: all 0.3s ease;
		padding: 10px;
		border-radius: 8px;
	}

	.advice-item:hover {
		background: rgba(103, 194, 58, 0.1);
		transform: translateX(5px);
	}

	.advice-item i {
		font-size: 24px;
		color: #67C23A;
		flex-shrink: 0;
		margin-top: 2px;
		transition: transform 0.3s ease;
	}

	.advice-item:hover i {
		transform: scale(1.2);
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
			padding: 0 5% 60px;
			min-height: auto;
		}

		.hero-top-bar {
			padding: 15px 0;
		}

		.user-greeting {
			display: none;
		}

		.quick-entries {
			flex-direction: column;
			gap: 12px;
		}

		.entry-card {
			min-width: auto;
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
<script lang="ts">
</script>