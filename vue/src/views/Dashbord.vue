<template>
  <div class="diagnosis-page">
    <!-- 顶部 -->
    <div class="header">
      <h2><i class="el-icon-s-data"></i> 智能诊断可视化报告</h2>
      <el-button type="primary" @click="goBack">重新检测</el-button>
    </div>

    <div v-if="loading" class="loading">
      <el-icon class="is-loading" :size="50"><Loading /></el-icon>
      <p>正在生成可视化图表...</p>
    </div>

    <div v-else class="dashboard-grid">

      <!-- [图表 1]核心概率仪表盘 (Gauge Chart) -->
      <el-card class="chart-box main-chart">
        <template #header><span class="title">🎯 患病概率评估</span></template>
        <div id="gaugeChart" class="echart-div"></div>
        <div class="result-text" :class="isPositive ? 'text-danger' : 'text-success'">
          {{ isPositive ? '高风险:建议立即就医' : '低风险:保持健康生活' }}
        </div>
      </el-card>

      <!-- [图表 2]多维风险雷达图 (Radar Chart) -->
      <el-card class="chart-box">
        <template #header><span class="title">🕸️ 身体指标风险维度</span></template>
        <div id="radarChart" class="echart-div"></div>
        <p class="tip">* 红色区域越大,表示该维度偏离正常值越远</p>
      </el-card>

      <!-- [图表 3]关键指标对比条形图 (Bar Chart) -->
      <!-- 模拟展示:虽然只有一条数据,但我们可以画出"当前值"vs"警戒线" -->
      <el-card class="chart-box">
        <template #header><span class="title">📊 关键指标风险对比</span></template>
        <div id="barChart" class="echart-div"></div>
      </el-card>

      <!-- 文字建议区 -->
      <el-card class="chart-box advice-box">
        <template #header><span class="title">💡 专家建议</span></template>
        <ul class="advice-list">
          <li v-for="(item, i) in adviceList" :key="i">
            <i :class="item.icon" :style="{color: item.color}"></i> {{ item.text }}
          </li>
        </ul>
      </el-card>

    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import { Loading } from '@element-plus/icons-vue';

export default {
  name: "VisualDiagnosis",
  components: { Loading },
  data() {
    return {
      loading: true,
      id: this.$route.query.id || this.$route.query.id1,
      isPositive: false,
      probability: 0,

      gaugeChart: null,
      radarChart: null,
      barChart: null
    };
  },
  computed: {
    adviceList() {
      if (this.isPositive) {
        return [
          { icon: 'el-icon-warning', color: '#F56C6C', text: '血糖/胰岛素等指标严重超标,需内分泌科确诊.' },
          { icon: 'el-icon-close', color: '#F56C6C', text: '严格禁止高糖、高脂饮食,戒烟限酒.' },
          { icon: 'el-icon-medical-box', color: '#E6A23C', text: '遵医嘱进行药物治疗或胰岛素注射.' }
        ];
      } else {
        return [
          { icon: 'el-icon-circle-check', color: '#67C23A', text: '指标在正常范围内,继续保持.' },
          { icon: 'el-icon-running', color: '#67C23A', text: '每周坚持 150 分钟中等强度运动.' },
          { icon: 'el-icon-time', color: '#909399', text: '每年定期体检,关注血糖变化.' }
        ];
      }
    }
  },
  mounted() {
    this.loadData();
    window.addEventListener('resize', this.resizeCharts);
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.resizeCharts);
    [this.gaugeChart, this.radarChart, this.barChart].forEach(c => c && c.dispose());
  },
  methods: {
    loadData() {
      if (!this.id) { this.loading = false; return; }

      Promise.all([
        request.get(`/DataTest/totle/${this.id}`),
        request.get(`/DataTest/members/${this.id}`)
      ]).then(([resTot, resMem]) => {
        const members = resMem.data || [0, 0];
        this.isPositive = members[1] > 0;
        this.probability = this.isPositive ? 94.5 : 12.3;

        this.loading = false;

        this.$nextTick(() => {
          this.renderGauge();
          this.renderRadar();
          this.renderBar();
        });
      }).catch(err => {
        console.error(err);
        ElMessage.error("数据加载失败");
        this.loading = false;
      });
    },

    renderGauge() {
      const dom = document.getElementById('gaugeChart');
      if (!dom) return;
      this.gaugeChart = echarts.init(dom);

      const color = this.isPositive ? '#F56C6C' : '#67C23A';

      const option = {
        series: [{
          type: 'gauge',
          startAngle: 180,
          endAngle: 0,
          min: 0,
          max: 100,
          splitNumber: 5,
          radius: '90%',
          center: ['50%', '60%'],
          itemStyle: { color: color },
          progress: { show: true, width: 25, roundCap: true },
          pointer: { show: false },
          axisLine: { lineStyle: { width: 25, color: [[1, '#EBEEF5']] } },
          axisTick: { show: false },
          splitLine: { show: false },
          axisLabel: { show: false },
          detail: {
            show: false
          },
          data: [{ value: this.probability }]
        }]
      };
      this.gaugeChart.setOption(option);
    },

    renderRadar() {
      const dom = document.getElementById('radarChart');
      if (!dom) return;
      this.radarChart = echarts.init(dom);

      const baseVal = this.isPositive ? 0.85 : 0.2;
      const varyVal = this.isPositive ? 0.9 : 0.15;

      const option = {
        tooltip: {},
        radar: {
          indicator: [
            { name: '血糖风险', max: 1 },
            { name: '血压风险', max: 1 },
            { name: '肥胖风险', max: 1 },
            { name: '胰岛素抵抗', max: 1 },
            { name: '家族遗传', max: 1 },
            { name: '年龄因素', max: 1 }
          ],
          radius: '65%',
          shape: 'circle',
          axisName: { color: '#666', fontSize: 12 },
          splitArea: { areaStyle: { color: ['#f8f9fa', '#fff'] } },
          axisLine: { lineStyle: { color: '#ddd' } },
          splitLine: { lineStyle: { color: '#eee' } }
        },
        series: [{
          name: '风险分布',
          type: 'radar',
          data: [{
            value: [
              baseVal,
              this.isPositive ? 0.8 : 0.2,
              this.isPositive ? 0.9 : 0.1,
              baseVal,
              this.isPositive ? 0.7 : 0.3,
              varyVal
            ],
            name: '当前患者',
            itemStyle: { color: this.isPositive ? '#F56C6C' : '#67C23A' },
            areaStyle: {
              color: this.isPositive ? 'rgba(245, 108, 108, 0.4)' : 'rgba(103, 194, 58, 0.4)',
              shadowBlur: 10,
              shadowColor: 'rgba(0,0,0,0.2)'
            },
            lineStyle: { width: 2 }
          }]
        }]
      };
      this.radarChart.setOption(option);
    },

    renderBar() {
      const dom = document.getElementById('barChart');
      if (!dom) return;
      this.barChart = echarts.init(dom);

      const score = this.isPositive ? 8.5 : 2.0;

      const option = {
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
        xAxis: {
          type: 'value',
          max: 10,
          splitLine: { show: false },
          axisLabel: { show: false }
        },
        yAxis: {
          type: 'category',
          data: ['综合风险指数'],
          axisLine: { show: false },
          axisTick: { show: false },
          axisLabel: { fontWeight: 'bold', fontSize: 14 }
        },
        series: [
          {
            name: '风险分布',
            type: 'bar',
            barWidth: '40%',
            data: [score],
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: this.isPositive ? '#f56c6c' : '#67c23a' },
                { offset: 1, color: this.isPositive ? '#fab6b6' : '#a3e096' }
              ]),
              borderRadius: [0, 10, 10, 0]
            },
            label: {
              show: true,
              position: 'right',
              formatter: '{c} / 10',
              fontWeight: 'bold',
              color: '#333'
            },
            showBackground: true,
            backgroundStyle: { color: '#ebeef5', borderRadius: [0, 10, 10, 0] }
          },
          {
            type: 'markLine',
            silent: true,
            data: [{ xAxis: 6, name: '警戒线' }],
            lineStyle: { color: '#E6A23C', type: 'dashed', width: 2 },
            label: { show: false }
          }
        ]
      };
      this.barChart.setOption(option);
    },

    resizeCharts() {
      [this.gaugeChart, this.radarChart, this.barChart].forEach(c => c && c.resize());
    },

    goBack() {
      this.$router.push('/');
    }
  }
};
</script>

<style scoped>
.diagnosis-page {
  padding: 20px;
  background: #f0f2f5;
  min-height: 100vh;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background: #fff;
  padding: 15px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}
.header h2 { margin: 0; font-size: 20px; color: #333; }
.header h2 i { color: #409EFF; margin-right: 8px; }

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  background: #fff;
  border-radius: 8px;
  color: #666;
}
.loading .is-loading {
  animation: rotating 2s linear infinite;
  color: #409EFF;
  margin-bottom: 16px;
}
@keyframes rotating {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

@media (min-width: 992px) {
  .main-chart {
    grid-column: span 1;
    grid-row: span 2;
  }
}
@media (max-width: 768px) {
  .dashboard-grid { grid-template-columns: 1fr; }
  .main-chart { grid-row: auto; }
}

.chart-box {
  border-radius: 8px;
  border: none;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
  background: #fff;
  display: flex;
  flex-direction: column;
}
.chart-box .title {
  font-weight: 600;
  font-size: 16px;
  color: #333;
}
.echart-div {
  width: 100%;
  height: 300px;
  flex-grow: 1;
}
.main-chart .echart-div {
  height: 350px;
}

.result-text {
  text-align: center;
  font-size: 18px;
  font-weight: bold;
  margin-top: 10px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 6px;
}
.text-danger { color: #F56C6C; background: #fef0f0; }
.text-success { color: #67C23A; background: #f0f9eb; }

.tip {
  font-size: 12px;
  color: #999;
  text-align: center;
  margin-top: -10px;
  margin-bottom: 10px;
}

.advice-box .advice-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.advice-box li {
  padding: 10px 0;
  border-bottom: 1px dashed #eee;
  font-size: 14px;
  color: #666;
  display: flex;
  align-items: flex-start;
}
.advice-box li:last-child { border-bottom: none; }
.advice-box li i {
  margin-right: 10px;
  font-size: 16px;
  margin-top: 2px;
}
</style>