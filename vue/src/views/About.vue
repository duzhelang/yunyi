<template>
  <div class="about-page">
    <!-- 粒子背景 -->
    <div class="particles-bg" ref="particlesBg"></div>
    
    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 英雄区域 -->
      <section class="hero-section">
        <div class="hero-badge">
          <span class="badge-dot"></span>
          <span>关于系统</span>
        </div>
        <h1 class="hero-title">
          <span class="title-line">云医智护</span>
          <span class="title-highlight">糖尿病诊断系统</span>
        </h1>
        <p class="hero-subtitle">
          基于人工智能的糖尿病风险预测与健康管理平台，为医疗专业人员和患者提供智能化的诊断支持与个性化健康建议。
        </p>
        <div class="hero-stats">
          <div class="stat-item" v-for="(stat, index) in stats" :key="index">
            <div class="stat-number">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </div>
      </section>

      <!-- 功能特性 -->
      <section class="features-section">
        <h2 class="section-title">核心功能</h2>
        <div class="features-grid">
          <div 
            class="feature-card" 
            v-for="(feature, index) in features" 
            :key="index"
            @mouseenter="activeFeature = index"
            @mouseleave="activeFeature = -1"
          >
            <div class="feature-icon" :style="{ background: feature.gradient }">
              <el-icon :size="24">
                <component :is="feature.icon" />
              </el-icon>
            </div>
            <h3 class="feature-title">{{ feature.title }}</h3>
            <p class="feature-desc">{{ feature.description }}</p>
            <div class="feature-arrow" :class="{ 'active': activeFeature === index }">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </section>

      <!-- 技术栈 -->
      <section class="tech-section">
        <h2 class="section-title">技术架构</h2>
        <div class="tech-grid">
          <div class="tech-category" v-for="(category, index) in techStack" :key="index">
            <h3 class="category-title">
              <span class="category-dot" :style="{ background: category.color }"></span>
              {{ category.name }}
            </h3>
            <div class="tech-items">
              <div 
                class="tech-item" 
                v-for="(tech, techIndex) in category.items" 
                :key="techIndex"
                @mouseenter="hoveredTech = `${index}-${techIndex}`"
                @mouseleave="hoveredTech = ''"
                :class="{ 'hovered': hoveredTech === `${index}-${techIndex}` }"
              >
                <span class="tech-name">{{ tech.name }}</span>
                <span class="tech-version">{{ tech.version }}</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 团队信息 -->
<!--      <section class="team-section">-->
<!--        <h2 class="section-title">开发团队</h2>-->
<!--        <div class="team-grid">-->
<!--          <div -->
<!--            class="team-card" -->
<!--            v-for="(member, index) in teamMembers" -->
<!--            :key="index"-->
<!--            @mouseenter="activeTeamMember = index"-->
<!--            @mouseleave="activeTeamMember = -1"-->
<!--            :class="{ 'active': activeTeamMember === index }"-->
<!--          >-->
<!--            <div class="member-avatar" :style="{ background: member.gradient }">-->
<!--              <span class="avatar-text">{{ member.name.charAt(0) }}</span>-->
<!--            </div>-->
<!--            <h4 class="member-name">{{ member.name }}</h4>-->
<!--            <p class="member-role">{{ member.role }}</p>-->
<!--            <p class="member-desc">{{ member.description }}</p>-->
<!--          </div>-->
<!--        </div>-->
<!--      </section>-->

      <!-- 联系方式 -->
      <section class="contact-section">
        <div class="contact-card">
          <div class="contact-content">
            <h2 class="contact-title">联系我们</h2>
            <p class="contact-desc">
              如果您有任何问题、建议或合作意向，欢迎随时与我们联系。
            </p>
            <div class="contact-info">
              <div class="contact-item">
                <el-icon><Message /></el-icon>
                <span>support@yunyizhihu.com</span>
              </div>
              <div class="contact-item">
                <el-icon><Phone /></el-icon>
                <span>400-888-9999</span>
              </div>
              <div class="contact-item">
                <el-icon><Location /></el-icon>
                <span>北京市海淀区中关村科技园</span>
              </div>
            </div>
          </div>
          <div class="contact-decoration">
            <div class="decoration-circle"></div>
            <div class="decoration-circle"></div>
            <div class="decoration-circle"></div>
          </div>
        </div>
      </section>

      <!-- 版本信息 -->
      <footer class="footer-section">
        <div class="footer-content">
          <div class="footer-logo">
            <span class="logo-icon">☁️</span>
            <span class="logo-text">云医智护</span>
          </div>
          <div class="footer-info">
            <p>版本 v2.1.0 | 构建于 2026 年</p>
            <p>© 2026 云医智护团队 版权所有</p>
          </div>
        </div>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { 
  DataAnalysis, 
  Cpu, 
  ChatDotRound, 
  User, 
  TrendCharts, 
  Warning,
  ArrowRight,
  Message,
  Phone,
  Location
} from '@element-plus/icons-vue'

const activeFeature = ref(-1)
const activeTeamMember = ref(-1)
const hoveredTech = ref('')
const particlesBg = ref(null)

const stats = ref([
  { value: '98.7%', label: '预测准确率' },
  { value: '50,000+', label: '服务用户' },
  { value: '24/7', label: '系统可用性' },
  { value: '15+', label: '核心算法' }
])

const features = ref([
  {
    icon: 'DataAnalysis',
    title: '智能风险预测',
    description: '基于机器学习算法，综合分析多项生理指标，提供精准的糖尿病风险评估。',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    icon: 'Cpu',
    title: 'AI辅助诊断',
    description: '深度学习模型辅助医生进行早期筛查和诊断，提高诊断效率和准确性。',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    icon: 'ChatDotRound',
    title: '智能健康助手',
    description: '7×24小时AI健康顾问，解答糖尿病相关问题，提供个性化健康建议。',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    icon: 'TrendCharts',
    title: '健康数据追踪',
    description: '可视化健康数据趋势，帮助用户了解身体状况变化，及时调整管理策略。',
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  },
  {
    icon: 'User',
    title: '个性化管理',
    description: '根据用户个人情况定制专属健康管理方案，包括饮食、运动和用药建议。',
    gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'
  },
  {
    icon: 'Warning',
    title: '风险预警系统',
    description: '实时监测健康指标，当指标异常时及时发出预警，防范健康风险。',
    gradient: 'linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)'
  }
])

const techStack = ref([
  {
    name: '前端技术',
    color: '#42b883',
    items: [
      { name: 'Vue 3', version: '3.4.x' },
      { name: 'Element Plus', version: '2.5.x' },
      { name: 'Vite', version: '5.x' },
      { name: 'Pinia', version: '2.x' },
      { name: 'Vue Router', version: '4.x' },
      { name: 'ECharts', version: '5.x' }
    ]
  },
  {
    name: '后端技术',
    color: '#3498db',
    items: [
      { name: 'Spring Boot', version: '3.x' },
      { name: 'MyBatis Plus', version: '3.5.x' },
      { name: 'MySQL', version: '8.0' },
      { name: 'Redis', version: '7.x' },
      { name: 'Spring Security', version: '6.x' },
      { name: 'JWT', version: '0.12.x' }
    ]
  },
  {
    name: 'AI/ML 技术',
    color: '#e74c3c',
    items: [
      { name: 'Python', version: '3.10+' },
      { name: 'TensorFlow', version: '2.x' },
      { name: 'Scikit-learn', version: '1.x' },
      { name: 'Pandas', version: '2.x' },
      { name: 'NumPy', version: '1.x' },
      { name: 'XGBoost', version: '2.x' }
    ]
  }
])

const teamMembers = ref([
  {
    name: '张明',
    role: '项目负责人',
    description: '10年医疗信息化经验，专注于AI医疗应用研究',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    name: '李华',
    role: '前端架构师',
    description: 'Vue.js专家，负责系统前端架构设计与优化',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    name: '王芳',
    role: 'AI算法工程师',
    description: '机器学习博士，主导糖尿病预测模型研发',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    name: '赵强',
    role: '后端开发工程师',
    description: 'Java全栈开发，负责系统后端架构与API设计',
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  },
  {
    name: '刘洋',
    role: '医疗顾问',
    description: '内分泌科主任医师，提供专业医疗指导',
    gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'
  }
])

// 粒子动画
let animationId = null
let particles = []

class Particle {
  constructor(canvas) {
    this.canvas = canvas
    this.x = Math.random() * canvas.width
    this.y = Math.random() * canvas.height
    this.size = Math.random() * 2 + 1
    this.speedX = (Math.random() - 0.5) * 0.5
    this.speedY = (Math.random() - 0.5) * 0.5
    this.opacity = Math.random() * 0.5 + 0.1
  }

  update() {
    this.x += this.speedX
    this.y += this.speedY

    if (this.x > this.canvas.width) this.x = 0
    if (this.x < 0) this.x = this.canvas.width
    if (this.y > this.canvas.height) this.y = 0
    if (this.y < 0) this.y = this.canvas.height
  }

  draw(ctx) {
    ctx.fillStyle = `rgba(94, 234, 212, ${this.opacity})`
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2)
    ctx.fill()
  }
}

const initParticles = () => {
  if (!particlesBg.value) return
  
  const canvas = document.createElement('canvas')
  canvas.style.position = 'absolute'
  canvas.style.top = '0'
  canvas.style.left = '0'
  canvas.style.width = '100%'
  canvas.style.height = '100%'
  canvas.style.pointerEvents = 'none'
  particlesBg.value.appendChild(canvas)
  
  const resizeCanvas = () => {
    canvas.width = particlesBg.value.offsetWidth
    canvas.height = particlesBg.value.offsetHeight
  }
  
  resizeCanvas()
  window.addEventListener('resize', resizeCanvas)
  
  const ctx = canvas.getContext('2d')
  
  // 创建粒子
  for (let i = 0; i < 50; i++) {
    particles.push(new Particle(canvas))
  }
  
  const animate = () => {
    ctx.clearRect(0, 0, canvas.width, canvas.height)
    
    particles.forEach(particle => {
      particle.update()
      particle.draw(ctx)
    })
    
    animationId = requestAnimationFrame(animate)
  }
  
  animate()
}

onMounted(() => {
  initParticles()
})

onUnmounted(() => {
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
})
</script>

<style scoped>
.about-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #0f172a 100%);
  color: #ffffff;
  position: relative;
  overflow: hidden;
}

.particles-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
}

.main-content {
  position: relative;
  z-index: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 40px;
}

/* 英雄区域 */
.hero-section {
  text-align: center;
  padding: 80px 0 60px;
  position: relative;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(94, 234, 212, 0.1);
  border: 1px solid rgba(94, 234, 212, 0.3);
  border-radius: 20px;
  padding: 6px 16px;
  font-size: 14px;
  color: #5eead4;
  margin-bottom: 24px;
}

.badge-dot {
  width: 6px;
  height: 6px;
  background: #5eead4;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.hero-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 24px;
  line-height: 1.2;
}

.title-line {
  display: block;
  background: linear-gradient(90deg, #ffffff 0%, #94a3b8 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.title-highlight {
  display: block;
  background: linear-gradient(90deg, #5eead4 0%, #4facfe 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 18px;
  color: #94a3b8;
  max-width: 600px;
  margin: 0 auto 40px;
  line-height: 1.6;
}

.hero-stats {
  display: flex;
  justify-content: center;
  gap: 60px;
  padding: 30px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #5eead4;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #94a3b8;
}

/* 功能特性 */
.features-section {
  padding: 80px 0;
}

.section-title {
  font-size: 32px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 48px;
  background: linear-gradient(90deg, #ffffff 0%, #94a3b8 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.feature-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 32px;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.feature-card:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(94, 234, 212, 0.3);
  transform: translateY(-4px);
}

.feature-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  color: #ffffff;
}

.feature-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #ffffff;
}

.feature-desc {
  font-size: 14px;
  color: #94a3b8;
  line-height: 1.6;
}

.feature-arrow {
  position: absolute;
  bottom: 24px;
  right: 24px;
  opacity: 0;
  transform: translateX(-10px);
  transition: all 0.3s ease;
  color: #5eead4;
}

.feature-arrow.active {
  opacity: 1;
  transform: translateX(0);
}

/* 技术栈 */
.tech-section {
  padding: 80px 0;
}

.tech-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
}

.tech-category {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 24px;
}

.category-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  color: #ffffff;
}

.category-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.tech-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tech-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 8px;
  transition: all 0.2s ease;
}

.tech-item.hovered {
  background: rgba(94, 234, 212, 0.1);
  transform: translateX(4px);
}

.tech-name {
  font-size: 14px;
  color: #e2e8f0;
}

.tech-version {
  font-size: 12px;
  color: #64748b;
  background: rgba(255, 255, 255, 0.1);
  padding: 2px 8px;
  border-radius: 4px;
}

/* 团队信息 */
.team-section {
  padding: 80px 0;
}

.team-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 24px;
}

.team-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  transition: all 0.3s ease;
}

.team-card.active {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(94, 234, 212, 0.3);
  transform: translateY(-4px);
}

.member-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}

.avatar-text {
  font-size: 24px;
  font-weight: 600;
  color: #ffffff;
}

.member-name {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
  color: #ffffff;
}

.member-role {
  font-size: 13px;
  color: #5eead4;
  margin-bottom: 8px;
}

.member-desc {
  font-size: 12px;
  color: #94a3b8;
  line-height: 1.5;
}

/* 联系方式 */
.contact-section {
  padding: 80px 0;
}

.contact-card {
  background: linear-gradient(135deg, rgba(94, 234, 212, 0.1) 0%, rgba(79, 172, 254, 0.1) 100%);
  border: 1px solid rgba(94, 234, 212, 0.2);
  border-radius: 24px;
  padding: 48px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.contact-content {
  position: relative;
  z-index: 1;
}

.contact-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #ffffff;
}

.contact-desc {
  font-size: 16px;
  color: #94a3b8;
  margin-bottom: 24px;
  max-width: 400px;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #e2e8f0;
}

.contact-item .el-icon {
  color: #5eead4;
  font-size: 18px;
}

.contact-decoration {
  position: absolute;
  right: -20px;
  top: -20px;
  width: 200px;
  height: 200px;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(94, 234, 212, 0.2);
}

.decoration-circle:nth-child(1) {
  width: 200px;
  height: 200px;
  top: 0;
  left: 0;
  animation: float 6s ease-in-out infinite;
}

.decoration-circle:nth-child(2) {
  width: 150px;
  height: 150px;
  top: 25px;
  left: 25px;
  animation: float 8s ease-in-out infinite reverse;
}

.decoration-circle:nth-child(3) {
  width: 100px;
  height: 100px;
  top: 50px;
  left: 50px;
  animation: float 10s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
}

/* 页脚 */
.footer-section {
  padding: 40px 0;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  margin-top: 40px;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  font-size: 24px;
}

.logo-text {
  font-size: 20px;
  font-weight: 600;
  background: linear-gradient(90deg, #5eead4 0%, #4facfe 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.footer-info {
  text-align: right;
  font-size: 13px;
  color: #64748b;
  line-height: 1.6;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .tech-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .team-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .main-content {
    padding: 40px 20px;
  }
  
  .hero-title {
    font-size: 36px;
  }
  
  .hero-stats {
    flex-direction: column;
    gap: 20px;
  }
  
  .features-grid,
  .tech-grid,
  .team-grid {
    grid-template-columns: 1fr;
  }
  
  .contact-card {
    flex-direction: column;
    text-align: center;
  }
  
  .footer-content {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
}
</style>