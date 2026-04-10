<template>
  <div class="video-container">
    <el-page-header @back="goBack" content="糖尿病科普"></el-page-header>

    <!-- 封面区域(未播放时显示) -->
    <div v-if="!isPlaying" class="single-video">
      <el-card class="video-card" shadow="hover">
        <div class="video-cover" @click="playFullScreen">
          <img src="/111.jpg" alt="糖尿病科普视频封面">
          <div class="play-icon">
            <i class="el-icon-caret-right"></i>
          </div>
        </div>
        <div class="video-info">
          <h3 class="video-title">糖尿病科普视频</h3>
          <p class="video-desc">本视频详细讲解糖尿病的成因、症状、预防及日常管理知识,帮助您全面了解糖尿病.</p>
          <el-tag size="small">科普教育</el-tag>
        </div>
      </el-card>
    </div>

    <!-- 全屏视频区域(播放时显示) -->
    <div v-else ref="videoContainer" class="fullscreen-video">
      <video
          ref="videoPlayer"
          src="/1111.mp4"
          controls
          @ended="onVideoEnded"
      ></video>
      <!-- 退出按钮 -->
      <div class="exit-btn" @click="exitVideo">
        <i class="el-icon-close"></i>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DiabetesVideo',
  data() {
    return {
      isPlaying: false
    }
  },
  methods: {
    async playFullScreen() {
      this.isPlaying = true;
      this.$nextTick(() => {
        const video = this.$refs.videoPlayer;
        const container = this.$refs.videoContainer;

        if (!video || !container) return;

        // 尝试播放
        const playPromise = video.play();
        if (playPromise !== undefined) {
          playPromise.catch(() => {
            // 自动播放被阻止时,用户需手动点击播放(保留 controls 即可)
            console.warn('自动播放被阻止,请手动点击播放按钮');
          });
        }

        // 请求全屏
        if (container.requestFullscreen) {
          container.requestFullscreen();
        } else if (container.webkitRequestFullscreen) {
          container.webkitRequestFullscreen();
        } else if (container.mozRequestFullScreen) {
          container.mozRequestFullScreen();
        }
      });
    },

    onVideoEnded() {
      this.exitVideo();
    },

    exitVideo() {
      this.exitFullscreen();
      this.isPlaying = false;
    },

    goBack() {
      if (this.isPlaying) {
        this.exitVideo();
      } else {
        this.$router.back();
      }
    },

    exitFullscreen() {
      if (document.exitFullscreen) {
        document.exitFullscreen();
      } else if (document.webkitExitFullscreen) {
        document.webkitExitFullscreen();
      } else if (document.mozCancelFullScreen) {
        document.mozCancelFullScreen();
      }
    }
  }
}
</script>

<style scoped>
.video-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.single-video {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.video-card {
  width: 600px;
  cursor: pointer;
  transition: transform 0.3s;
}

.video-card:hover {
  transform: translateY(-5px);
}

.video-cover {
  position: relative;
  height: 340px;
  overflow: hidden;
  border-radius: 4px 4px 0 0;
}

.video-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
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
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 32px;
  transition: all 0.3s;
}

.play-icon i {
  transform: translateX(4px);
}

.video-cover:hover .play-icon {
  background-color: rgba(74, 144, 226, 0.8);
  transform: translate(-50%, -50%) scale(1.1);
}

.video-info {
  padding: 15px;
}

.video-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #333;
}

.video-desc {
  font-size: 14px;
  color: #666;
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
  transition: background-color 0.3s;
}

.exit-btn:hover {
  background-color: rgba(255, 0, 0, 0.7);
}
</style>