<template>
  <div class="container" @mousemove="handleMouseMove">
    <canvas ref="canvas" class="bg"></canvas>

    <div class="content">
      <div class="face" :class="{ breathe: true }">
  
		<svg viewBox="0 0 120 120">

		  <circle cx="60" cy="60" r="55" stroke="#fff" stroke-width="10" fill="transparent"/>
		
		  <circle
		    class="eye"
		    :cx="35 + eyeOffsetX"
		    :cy="45 + eyeOffsetY"
		    r="10"
		    fill="#fff"
		  />
		  <circle
		    class="eye"
		    :cx="85 + eyeOffsetX"
		    :cy="45 + eyeOffsetY"
		    r="10"
		    fill="#fff"
		  />

		  <path
		    :d="mouthPath"
		    stroke="#fff"
		    stroke-width="10"
		    fill="none"
		    stroke-linecap="round"
			style="transition: d 0.3s ease;"
		  />
		</svg>
      </div>

      <h1>404</h1>
      <p>页面未找到<br/>您访问的页面不存在或发生了其他错误</p>

      <div class="actions">
        <button @click="goBack">返回上一页</button>
        <button @click="goHome">返回首页</button>
      </div>

      <div class="note">
        注：评估标准参考中国2型糖尿病防治指南，BMI = 体重(kg) / 身高(m)²。
      </div>
    </div>
  </div>
</template>

<script>
// Vue2 选项式 API
export default {
  name: "NotFound",
  data() {
    return {
      eyeOffsetX: 0,
      eyeOffsetY: 0,
      // mouthPath: "M30 70 Q60 90 90 70",
	  mouthPath: "M35 85 Q60 65 85 85",
      idleTimer: null
    };
  },
  methods: {
    handleMouseMove(e) {
      const x = (e.clientX / window.innerWidth - 0.5) * 10;
      const y = (e.clientY / window.innerHeight - 0.5) * 10;

      this.eyeOffsetX = x;
      this.eyeOffsetY = y;

      this.resetIdle();
    },
    sigh() {
      this.mouthPath = "M38 90 Q60 55 83 90";//叹
      setTimeout(() => {
        this.mouthPath = "M35 85 Q60 65 85 85";//哭脸
      }, 1200);
    },
    resetIdle() {
      clearTimeout(this.idleTimer);
      this.idleTimer = setTimeout(this.sigh, 2000);
    },
    goBack() {
      this.mouthPath = "M35 80 Q60 100 85 80";//笑脸
      setTimeout(() => history.back(), 300);
    },
    goHome() {
      this.mouthPath = "M35 80 Q60 100 85 80";
      setTimeout(() => {
        window.location.href = "/";
      }, 300);
    },
    initParticles() {
      const c = this.$refs.canvas;
      const ctx = c.getContext("2d");

      c.width = window.innerWidth;
      c.height = window.innerHeight;

      const particles = Array.from({ length: 60 }, () => ({
        x: Math.random() * c.width,
        y: Math.random() * c.height,
        r: Math.random() * 2,
        dx: Math.random() - 0.5,
        dy: Math.random() - 0.5
      }));

      const draw = () => {
        ctx.clearRect(0, 0, c.width, c.height);

        particles.forEach(p => {
          p.x += p.dx;
          p.y += p.dy;

          if (p.x < 0 || p.x > c.width) p.dx *= -1;
          if (p.y < 0 || p.y > c.height) p.dy *= -1;

          ctx.beginPath();
          ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2);
          ctx.fillStyle = "rgba(255,255,255,0.5)";
          ctx.fill();
        });

        requestAnimationFrame(draw);
      };

      draw();
    }
  },
  mounted() {
    this.resetIdle();
    this.initParticles();
  },
  beforeDestroy() {
    // 清除定时器防止内存泄漏
    clearTimeout(this.idleTimer);
  }
};
</script>

<style scoped>
.container {
  width: 100%;
  height: 100vh;
  overflow: hidden;
  /* background: linear-gradient(135deg, #667eea, #764ba2); */
  background: linear-gradient(135deg,#2c45b5, #2cb4ce);
  position: relative;
}

.bg {
  position: absolute;
  width: 100%;
  height: 100%;
}

.content {
  position: relative;
  z-index: 2;
  text-align: center;
  color: white;
  top: 50%;
  transform: translateY(-50%);
}

.face {
  width: 120px;
  margin: 0 auto 20px;
  transition: transform 0.3s;
}

.breathe {
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%,100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

h1 {
  font-size: 72px;
  margin: 0 0 10px 0;
}

p {
  margin-bottom: 20px;
  opacity: 0.8;
  line-height: 1.5;
}

.actions button {
  margin: 0 10px;
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  background: white;
  color: #333;
  transition: 0.3s;
}

.actions button:hover {
  transform: scale(1.05);
}

.note {
  margin-top: 30px;
  font-size: 14px;
  color: rgba(255,255,255,0.7);
  line-height: 1.4;
}

@media (max-width: 600px) {
  h1 {
    font-size: 48px;
  }
}
</style>