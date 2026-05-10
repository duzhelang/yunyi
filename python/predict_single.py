#!/usr/bin/env python
# -*- coding: utf-8 -*-
import warnings
warnings.filterwarnings("ignore")

import sys
import os
import json
import io
import traceback
import base64
import time
import numpy as np
import joblib
import torch
import torch.nn as nn
from datetime import datetime
from concurrent.futures import ThreadPoolExecutor, as_completed

# 尝试导入 matplotlib（若未安装则跳过绘图功能）
try:
    import matplotlib
    matplotlib.use('Agg')  # 使用非交互式后端
    import matplotlib.pyplot as plt
    import matplotlib.patches as mpatches
    MATPLOTLIB_AVAILABLE = True
except ImportError:
    MATPLOTLIB_AVAILABLE = False
    print("[警告] matplotlib 未安装，将跳过图表生成。可执行: pip install matplotlib")

# 尝试导入 SHAP（若未安装则跳过增强功能）
try:
    import shap
    SHAP_AVAILABLE = True
except ImportError:
    SHAP_AVAILABLE = False
    print("[警告] SHAP 未安装，将跳过特征重要性计算。可执行: pip install shap")

# 尝试导入 scipy（用于百分位计算）
try:
    from scipy import stats
    SCIPY_AVAILABLE = True
except ImportError:
    SCIPY_AVAILABLE = False
    print("[警告] scipy 未安装，百分位计算将使用近似算法。可执行: pip install scipy")

# ==========================================
# 模型结构定义（必须与训练脚本一致）
# ==========================================
class DiabetesModel(nn.Module):
    def __init__(self, input_dim):
        super(DiabetesModel, self).__init__()
        self.fc1 = nn.Linear(input_dim, 64)
        self.fc2 = nn.Linear(64, 32)
        self.fc3 = nn.Linear(32, 2)
        self.relu = nn.ReLU()
        self.dropout = nn.Dropout(0.2)

    def forward(self, x):
        x = self.relu(self.fc1(x))
        x = self.dropout(x)
        x = self.relu(self.fc2(x))
        x = self.dropout(x)
        x = self.fc3(x)
        return x

# ==========================================
# 辅助函数：规则引擎（备用预测）
# ==========================================
def rule_based_predict(features):
    risk_score = 0
    # 怀孕次数
    if features[0] > 6: risk_score += 15
    elif features[0] > 3: risk_score += 8
    # 血糖
    if features[1] > 120: risk_score += 30
    elif features[1] > 100: risk_score += 15
    # 血压
    if features[2] > 140: risk_score += 20
    elif features[2] > 120: risk_score += 10
    # 皮肤厚度
    if features[3] > 35: risk_score += 10
    elif features[3] < 10: risk_score += 5
    # 胰岛素
    if features[4] > 150: risk_score += 25
    elif features[4] > 80: risk_score += 15
    elif features[4] < 10: risk_score += 10
    # BMI
    if features[5] > 30: risk_score += 25
    elif features[5] > 24: risk_score += 15
    # 糖尿病谱系函数
    if features[6] > 0.8: risk_score += 20
    elif features[6] > 0.4: risk_score += 10
    # 年龄
    if features[7] > 60: risk_score += 20
    elif features[7] > 45: risk_score += 10

    prob = min(max(risk_score / 200.0, 0.05), 0.95)
    pred_class = 1 if prob > 0.5 else 0
    return pred_class, prob

# ==========================================
# MC Dropout 不确定性估计
# ==========================================
def predict_with_uncertainty(model, X_tensor, n_iter=15):
    """通过多次前向传播估计预测不确定性"""
    model.train()  # 启用 Dropout
    probs = []
    with torch.no_grad():
        for _ in range(n_iter):
            outputs = model(X_tensor)
            prob = torch.softmax(outputs, dim=1)[0][1].item()
            probs.append(prob)
    model.eval()
    return np.mean(probs), np.std(probs)

# ==========================================
# SHAP 特征重要性计算
# ==========================================
def compute_shap_importance(model, X_tensor, feature_names, model_base_path):
    """计算 SHAP 特征重要性，返回每个特征的贡献值列表"""
    if not SHAP_AVAILABLE:
        return None

    # 尝试加载预存背景数据，否则生成随机背景
    background_path = model_base_path.replace('.pth', '_background.npy')
    if os.path.exists(background_path):
        background = np.load(background_path)
    else:
        # 生成标准化后的随机数据作为背景（假设特征标准化后均值为0，方差≈1）
        background = np.random.randn(100, len(feature_names)) * 2

    background_tensor = torch.tensor(background, dtype=torch.float32)
    explainer = shap.DeepExplainer(model, background_tensor)
    shap_values = explainer.shap_values(X_tensor)

    # shap_values 对于二分类返回两个数组（每个类别），取正类（索引1）的 SHAP 值
    if isinstance(shap_values, list):
        importance = shap_values[1][0].tolist()
    else:
        importance = shap_values[0].tolist()

    return importance

# ==========================================
# 辅助函数：图表转Base64
# ==========================================
def fig_to_base64(fig):
    """将matplotlib图表转换为base64编码"""
    buffer = io.BytesIO()
    fig.savefig(buffer, format='png', dpi=150, bbox_inches='tight', facecolor='white')
    buffer.seek(0)
    img_base64 = base64.b64encode(buffer.getvalue()).decode('utf-8')
    buffer.close()
    plt.close(fig)
    return img_base64

# 全局常量
CHINESE_NAMES = {
    'Pregnancies': '怀孕次数', 'Glucose': '血糖', 'BloodPressure': '血压',
    'SkinThickness': '皮肤厚度', 'Insulin': '胰岛素', 'BMI': 'BMI指数',
    'DiabetesPedigreeFunction': '遗传系数', 'Age': '年龄'
}

NORMAL_RANGES = {
    'Pregnancies': {'min': 0, 'max': 4, 'unit': '次'},
    'Glucose': {'min': 70, 'max': 100, 'unit': 'mg/dL'},
    'BloodPressure': {'min': 60, 'max': 80, 'unit': 'mmHg'},
    'SkinThickness': {'min': 10, 'max': 30, 'unit': 'mm'},
    'Insulin': {'min': 16, 'max': 166, 'unit': 'mU/L'},
    'BMI': {'min': 18.5, 'max': 24.9, 'unit': ''},
    'DiabetesPedigreeFunction': {'min': 0.078, 'max': 2.42, 'unit': ''},
    'Age': {'min': 18, 'max': 65, 'unit': '岁'}
}

# ==========================================
# 图表绘制函数（并行）
# =========================================
# 图表绘制函数（并行）
def plot_importance(result):
    """生成特征重要性图"""
    try:
        if not result.get("feature_importance") or not result.get("feature_names"):
            return None
        
        importance = result["feature_importance"]
        names = result["feature_names"]
        
        sorted_indices = np.argsort(np.abs(importance))[::-1]
        sorted_importance = [importance[i] for i in sorted_indices]
        sorted_names = [names[i] for i in sorted_indices]
        sorted_chinese = [CHINESE_NAMES.get(n, n) for n in sorted_names]
        
        fig, ax = plt.subplots(figsize=(10, 6))
        colors = ['#f56c6c' if v > 0 else '#67c23a' for v in sorted_importance]
        bars = ax.barh(range(len(sorted_importance)), sorted_importance, color=colors, alpha=0.8)
        
        ax.set_yticks(range(len(sorted_importance)))
        ax.set_yticklabels(sorted_chinese)
        ax.invert_yaxis()
        ax.set_xlabel('SHAP Value (影响力)', fontsize=12)
        ax.set_title('糖尿病风险因素重要性分析', fontsize=14, fontweight='bold')
        ax.axvline(x=0, color='gray', linestyle='--', alpha=0.5)
        
        for i, (bar, val) in enumerate(zip(bars, sorted_importance)):
            ax.text(val + 0.01 if val > 0 else val - 0.01, i, f'{val:.3f}', 
                   va='center', ha='left' if val > 0 else 'right', fontsize=10)
        
        red_patch = mpatches.Patch(color='#f56c6c', alpha=0.8, label='增加风险')
        green_patch = mpatches.Patch(color='#67c23a', alpha=0.8, label='降低风险')
        ax.legend(handles=[red_patch, green_patch], loc='lower right')
        
        plt.tight_layout()
        return fig_to_base64(fig)
    except Exception as e:
        print(f"[警告] 特征重要性图生成失败: {str(e)}", file=sys.stderr)
        return None

def plot_dashboard(result):
    """生成风险仪表盘"""
    try:
        probability = result.get("probability", 0)
        risk_level = result.get("risk_level", "unknown")
        ci = result.get("confidence_interval")
        
        fig, ax = plt.subplots(figsize=(8, 8), subplot_kw={'projection': 'polar'})
        
        angle = np.pi * probability / 100
        
        theta = np.linspace(0, np.pi, 100)
        r = np.ones(100) * 0.8
        ax.plot(theta, r, color='#e5e7eb', linewidth=20, alpha=0.5)
        
        theta_fill = np.linspace(0, angle, 100)
        color = '#67c23a' if probability < 30 else '#e6a23c' if probability < 60 else '#f56c6c'
        ax.fill_between(theta_fill, 0, 0.8, color=color, alpha=0.7)
        ax.plot(theta_fill, np.ones(100) * 0.8, color=color, linewidth=3)
        
        ax.annotate('', xy=(angle, 0.7), xytext=(0, 0),
                   arrowprops=dict(arrowstyle='->', color='black', lw=2))
        
        ax.set_ylim(0, 1)
        ax.set_yticklabels([])
        ax.set_xticks([0, np.pi/4, np.pi/2, 3*np.pi/4, np.pi])
        ax.set_xticklabels(['0%', '25%', '50%', '75%', '100%'])
        
        ax.text(0, -0.1, f'{probability:.1f}%', fontsize=36, fontweight='bold', 
               ha='center', va='center', color=color)
        
        risk_text = {'low': '低风险', 'medium': '中风险', 'high': '高风险'}
        ax.text(np.pi/2, 0.4, risk_text.get(risk_level, '未知'), fontsize=18, 
               ha='center', va='center', fontweight='bold')
        
        if ci:
            ax.text(np.pi/2, 0.25, f'置信区间: {ci[0]}% - {ci[1]}%', fontsize=11, 
                   ha='center', va='center', color='#6b7280')
        
        plt.title('糖尿病风险评估仪表盘', fontsize=16, fontweight='bold', pad=20)
        
        return fig_to_base64(fig)
    except Exception as e:
        print(f"[警告] 风险仪表盘生成失败: {str(e)}", file=sys.stderr)
        return None

def plot_radar(result):
    """生成健康指标雷达图"""
    try:
        feature_values = list(result.get("features", {}).values())
        feature_names_list = list(result.get("features", {}).keys())
        
        if len(feature_values) != 8:
            return None
        
        max_values = [10, 200, 150, 60, 400, 50, 2, 100]
        normalized = [min(v/m, 1.0) if m > 0 else 0 for v, m in zip(feature_values, max_values)]
        
        categories = [CHINESE_NAMES.get(n, n) for n in feature_names_list]
        N = len(categories)
        angles = [n / float(N) * 2 * np.pi for n in range(N)]
        angles += angles[:1]
        normalized += normalized[:1]
        
        fig, ax = plt.subplots(figsize=(10, 10), subplot_kw={'projection': 'polar'})
        ax.plot(angles, normalized, 'o-', linewidth=2, color='#4080ff')
        ax.fill(angles, normalized, alpha=0.25, color='#4080ff')
        ax.set_xticks(angles[:-1])
        ax.set_xticklabels(categories, size=11)
        ax.set_ylim(0, 1)
        ax.set_title('健康指标雷达图', fontsize=16, fontweight='bold', pad=20)
        
        return fig_to_base64(fig)
    except Exception as e:
        print(f"[警告] 雷达图生成失败: {str(e)}", file=sys.stderr)
        return None

def plot_waterfall(result):
    """生成风险因素贡献瀑布图"""
    try:
        if not result.get("feature_importance"):
            return None
        
        feature_values = list(result.get("features", {}).values())
        if len(feature_values) != 8:
            return None
        
        importance = result["feature_importance"]
        names = result["feature_names"]
        probability = result.get("probability", 0)
        
        sorted_indices = np.argsort(importance)
        sorted_importance = [importance[i] for i in sorted_indices]
        sorted_names = [names[i] for i in sorted_indices]
        sorted_chinese = [CHINESE_NAMES.get(n, n) for n in sorted_names]
        
        fig, ax = plt.subplots(figsize=(12, 7))
        
        cumulative = np.zeros(len(sorted_importance))
        base_prob = 50
        
        for i, (name, imp) in enumerate(zip(sorted_chinese, sorted_importance)):
            contribution = imp * 50
            color = '#f56c6c' if contribution > 0 else '#67c23a'
            ax.bar(i, contribution, bottom=base_prob + cumulative[i], color=color, alpha=0.8, width=0.6)
            cumulative[i] += contribution
        
        ax.axhline(y=probability, color='#4080ff', linestyle='--', linewidth=2, label=f'最终风险: {probability:.1f}%')
        ax.axhline(y=base_prob, color='gray', linestyle=':', linewidth=1, label='基准风险: 50%')
        
        ax.set_xticks(range(len(sorted_chinese)))
        ax.set_xticklabels(sorted_chinese, rotation=45, ha='right', fontsize=10)
        ax.set_ylabel('风险概率贡献 (%)', fontsize=12)
        ax.set_title('风险因素贡献瀑布图', fontsize=14, fontweight='bold')
        ax.legend(loc='upper left')
        ax.set_ylim(0, 100)
        
        plt.tight_layout()
        return fig_to_base64(fig)
    except Exception as e:
        print(f"[警告] 瀑布图生成失败: {str(e)}", file=sys.stderr)
        return None

def plot_comparison(result):
    """生成健康指标对比分析图"""
    try:
        feature_values = list(result.get("features", {}).values())
        feature_names_list = list(result.get("features", {}).keys())
        
        if len(feature_values) != 8:
            return None
        
        fig, axes = plt.subplots(2, 4, figsize=(16, 10))
        axes = axes.flatten()
        
        for i, (name, value) in enumerate(zip(feature_names_list, feature_values)):
            ax = axes[i]
            normal = NORMAL_RANGES.get(name, {'min': 0, 'max': 100})
            chinese_name = CHINESE_NAMES.get(name, name)
            
            x = np.arange(3)
            values = [normal['min'], value, normal['max']]
            colors_bar = ['#67c23a', '#4080ff', '#67c23a']
            
            is_normal = normal['min'] <= value <= normal['max']
            if not is_normal:
                colors_bar[1] = '#f56c6c' if value > normal['max'] else '#e6a23c'
            
            bars = ax.bar(x, values, color=colors_bar, alpha=0.8, width=0.6)
            ax.set_xticks(x)
            ax.set_xticklabels(['正常下限', '您的值', '正常上限'], fontsize=9)
            ax.set_title(f'{chinese_name}', fontsize=12, fontweight='bold')
            
            for bar, val in zip(bars, values):
                ax.text(bar.get_x() + bar.get_width()/2, bar.get_height() + 1, 
                       f'{val:.1f}', ha='center', va='bottom', fontsize=9)
            
            ax.set_ylim(0, max(values) * 1.2)
        
        plt.suptitle('健康指标对比分析', fontsize=16, fontweight='bold', y=1.02)
        plt.tight_layout()
        return fig_to_base64(fig)
    except Exception as e:
        print(f"[警告] 指标对比图生成失败: {str(e)}", file=sys.stderr)
        return None

def plot_pie(result):
    """生成风险等级分布饼图"""
    try:
        probability = result.get("probability", 0)
        risk_level = result.get("risk_level", "unknown")
        
        fig, ax = plt.subplots(figsize=(8, 8))
        
        if probability < 30:
            sizes = [probability, 30 - probability, 40, 30]
            colors_pie = ['#67c23a', '#c8e6c9', '#fff3e0', '#ffebee']
            labels = ['低风险', '', '', '']
            explode = (0.05, 0, 0, 0)
        elif probability < 60:
            sizes = [30, probability - 30, 60 - probability, 40]
            colors_pie = ['#c8e6c9', '#e6a23c', '#fff3e0', '#ffebee']
            labels = ['', '中风险', '', '']
            explode = (0, 0.05, 0, 0)
        else:
            sizes = [30, 30, probability - 60, 100 - probability]
            colors_pie = ['#c8e6c9', '#fff3e0', '#f56c6c', '#ffcdd2']
            labels = ['', '', '高风险', '']
            explode = (0, 0, 0.05, 0)
        
        wedges, texts = ax.pie(sizes, colors=colors_pie, explode=explode,
                               startangle=90, counterclock=False)
        
        ax.text(0, 0, f'{probability:.1f}%', fontsize=32, fontweight='bold',
               ha='center', va='center', color='#333')
        
        risk_text = {'low': '低风险', 'medium': '中风险', 'high': '高风险'}
        ax.text(0, -0.15, risk_text.get(risk_level, '未知'), fontsize=18,
               ha='center', va='center', fontweight='bold')
        
        legend_labels = ['低风险 (0-30%)', '中风险 (30-60%)', '高风险 (60-100%)', '剩余区间']
        ax.legend(wedges, legend_labels, loc='lower center', bbox_to_anchor=(0.5, -0.1), ncol=2)
        
        ax.set_title('风险等级分布', fontsize=16, fontweight='bold')
        
        plt.tight_layout()
        return fig_to_base64(fig)
    except Exception as e:
        print(f"[警告] 风险分布饼图生成失败: {str(e)}", file=sys.stderr)
        return None

def plot_confidence(result):
    """生成预测不确定性分布图"""
    try:
        ci = result.get("confidence_interval")
        if not ci:
            return None
        
        probability = result.get("probability", 0)
        
        fig, ax = plt.subplots(figsize=(10, 6))
        
        x = np.linspace(0, 100, 1000)
        mean = probability
        std = (ci[1] - ci[0]) / 3.92
        
        y = 1 / (std * np.sqrt(2 * np.pi)) * np.exp(-0.5 * ((x - mean) / std) ** 2)
        y = y / y.max()
        
        ax.fill_between(x, y, alpha=0.3, color='#4080ff')
        ax.plot(x, y, color='#4080ff', linewidth=2)
        
        ax.axvline(x=probability, color='#f56c6c', linestyle='--', linewidth=2, label=f'预测值: {probability:.1f}%')
        ax.axvline(x=ci[0], color='#e6a23c', linestyle=':', linewidth=2, label=f'下限: {ci[0]:.1f}%')
        ax.axvline(x=ci[1], color='#e6a23c', linestyle=':', linewidth=2, label=f'上限: {ci[1]:.1f}%')
        
        ax.fill_between(x[(x >= ci[0]) & (x <= ci[1])], y[(x >= ci[0]) & (x <= ci[1])], 
                       alpha=0.5, color='#67c23a', label='95%置信区间')
        
        ax.set_xlabel('患病概率 (%)', fontsize=12)
        ax.set_ylabel('概率密度', fontsize=12)
        ax.set_title('预测不确定性分布', fontsize=14, fontweight='bold')
        ax.legend(loc='upper right')
        ax.set_xlim(0, 100)
        ax.set_ylim(0, 1.1)
        
        plt.tight_layout()
        return fig_to_base64(fig)
    except Exception as e:
        print(f"[警告] 置信区间图生成失败: {str(e)}", file=sys.stderr)
        return None

def plot_scorecard(result):
    """生成健康评分卡"""
    try:
        feature_values = list(result.get("features", {}).values())
        feature_names_list = list(result.get("features", {}).keys())
        
        if len(feature_values) != 8:
            return None
        
        health_scores = []
        score_labels = []
        
        for name, value in zip(feature_names_list, feature_values):
            normal = NORMAL_RANGES.get(name, {'min': 0, 'max': 100})
            mid = (normal['min'] + normal['max']) / 2
            range_size = normal['max'] - normal['min']
            
            if range_size > 0:
                deviation = abs(value - mid) / (range_size / 2)
                score = max(0, 100 - deviation * 30)
            else:
                score = 50
            
            health_scores.append(score)
            score_labels.append(CHINESE_NAMES.get(name, name))
        
        fig, ax = plt.subplots(figsize=(12, 6))
        
        colors_score = []
        for score in health_scores:
            if score >= 80:
                colors_score.append('#67c23a')
            elif score >= 60:
                colors_score.append('#e6a23c')
            else:
                colors_score.append('#f56c6c')
        
        bars = ax.bar(range(len(score_labels)), health_scores, color=colors_score, alpha=0.8, width=0.6)
        
        ax.axhline(y=80, color='#67c23a', linestyle='--', linewidth=1, alpha=0.5, label='优秀线 (80分)')
        ax.axhline(y=60, color='#e6a23c', linestyle='--', linewidth=1, alpha=0.5, label='及格线 (60分)')
        
        for bar, score in zip(bars, health_scores):
            ax.text(bar.get_x() + bar.get_width()/2, bar.get_height() + 2,
                   f'{score:.0f}', ha='center', va='bottom', fontsize=10, fontweight='bold')
        
        ax.set_xticks(range(len(score_labels)))
        ax.set_xticklabels(score_labels, rotation=45, ha='right', fontsize=10)
        ax.set_ylabel('健康评分 (0-100)', fontsize=12)
        ax.set_title('各项指标健康评分卡', fontsize=14, fontweight='bold')
        ax.set_ylim(0, 110)
        ax.legend(loc='upper right')
        
        overall_score = np.mean(health_scores)
        ax.text(0.02, 0.98, f'综合评分: {overall_score:.0f}分', transform=ax.transAxes,
               fontsize=14, fontweight='bold', va='top',
               bbox=dict(boxstyle='round', facecolor='wheat', alpha=0.5))
        
        plt.tight_layout()
        return fig_to_base64(fig)
    except Exception as e:
        print(f"[警告] 健康评分卡生成失败: {str(e)}", file=sys.stderr)
        return None

def plot_heatmap(result):
    """生成指标风险热力图"""
    try:
        feature_values = list(result.get("features", {}).values())
        feature_names_list = list(result.get("features", {}).keys())
        
        if len(feature_values) != 8:
            return None
        
        risk_levels_matrix = []
        
        for name, value in zip(feature_names_list, feature_values):
            normal = NORMAL_RANGES.get(name, {'min': 0, 'max': 100})
            
            if normal['min'] <= value <= normal['max']:
                risk_level_val = 0
            elif value < normal['min']:
                risk_level_val = 1
            elif value <= normal['max'] * 1.2:
                risk_level_val = 2
            elif value <= normal['max'] * 1.5:
                risk_level_val = 3
            else:
                risk_level_val = 4
            
            risk_levels_matrix.append(risk_level_val)
        
        fig, ax = plt.subplots(figsize=(14, 4))
        
        risk_colors = ['#67c23a', '#95d475', '#e6a23c', '#f56c6c', '#c45656']
        risk_labels_text = ['正常', '偏低', '轻度偏高', '中度偏高', '重度偏高']
        
        for i, (name, level) in enumerate(zip(feature_names_list, risk_levels_matrix)):
            chinese_name = CHINESE_NAMES.get(name, name)
            rect = plt.Rectangle((i, 0), 1, 1, color=risk_colors[level], alpha=0.8)
            ax.add_patch(rect)
            ax.text(i + 0.5, 0.5, chinese_name, ha='center', va='center',
                   fontsize=11, fontweight='bold', color='white' if level >= 2 else 'black')
        
        ax.set_xlim(0, 8)
        ax.set_ylim(0, 1)
        ax.set_xticks([])
        ax.set_yticks([])
        ax.set_title('指标风险热力图', fontsize=14, fontweight='bold')
        
        legend_patches = [mpatches.Patch(color=c, label=l) for c, l in zip(risk_colors, risk_labels_text)]
        ax.legend(handles=legend_patches, loc='upper center', bbox_to_anchor=(0.5, -0.05), ncol=5)
        
        plt.tight_layout()
        return fig_to_base64(fig)
    except Exception as e:
        print(f"[警告] 风险热力图生成失败: {str(e)}", file=sys.stderr)
        return None

def generate_charts(result, output_dir):
    """生成预测结果可视化图表，返回图表的base64编码"""
    if not MATPLOTLIB_AVAILABLE:
        return None
    
    try:
        plt.rcParams['font.sans-serif'] = ['SimHei', 'Microsoft YaHei', 'DejaVu Sans']
        plt.rcParams['axes.unicode_minus'] = False
        
        charts = {}
        
        # 定义图表生成任务
        chart_functions = {
            'importance_chart': plot_importance,
            'dashboard_chart': plot_dashboard,
            'radar_chart': plot_radar,
            'waterfall_chart': plot_waterfall,
            'comparison_chart': plot_comparison,
            'pie_chart': plot_pie,
            'confidence_chart': plot_confidence,
            'scorecard_chart': plot_scorecard,
            'heatmap_chart': plot_heatmap
        }
        
        # 并行生成图表
        with ThreadPoolExecutor(max_workers=4) as executor:
            future_to_chart = {executor.submit(func, result): chart_name 
                              for chart_name, func in chart_functions.items()}
            
            for future in as_completed(future_to_chart):
                chart_name = future_to_chart[future]
                try:
                    chart_base64 = future.result()
                    if chart_base64:
                        charts[chart_name] = chart_base64
                        print(f"[图表] {chart_name} 已生成", file=sys.stderr)
                except Exception as e:
                    print(f"[警告] {chart_name} 生成失败: {str(e)}", file=sys.stderr)
        
        return charts
        
    except Exception as e:
        print(f"[警告] 图表生成失败: {str(e)}", file=sys.stderr)
        print(traceback.format_exc(), file=sys.stderr)
        return None

# ==========================================
# 参考数据百分位计算
# ==========================================
def compute_percentile(value, mean, std):
    """计算单个指标在正态分布中的百分位"""
    if SCIPY_AVAILABLE:
        percentile = stats.norm.cdf((value - mean) / std) * 100
    else:
        # 使用误差函数近似正态分布CDF
        import math
        z = (value - mean) / std
        # 使用近似公式：Φ(z) ≈ 0.5 * (1 + erf(z / sqrt(2)))
        percentile = 50 * (1 + math.erf(z / math.sqrt(2))) * 100
    return round(percentile, 1)

def compute_all_percentiles(features):
    """计算所有指标的百分位（基于正态分布假设，使用向量化计算）"""
    # 参考统计数据（基于糖尿病数据集的平均值和标准差）
    reference_stats = {
        'Pregnancies': {'mean': 3.8, 'std': 3.4},
        'Glucose': {'mean': 120.9, 'std': 32.0},
        'BloodPressure': {'mean': 68.2, 'std': 19.4},
        'SkinThickness': {'mean': 20.5, 'std': 16.0},
        'Insulin': {'mean': 79.8, 'std': 115.2},
        'BMI': {'mean': 31.9, 'std': 8.0},
        'DiabetesPedigreeFunction': {'mean': 0.47, 'std': 0.33},
        'Age': {'mean': 33.2, 'std': 11.8}
    }
    
    feature_names = ['Pregnancies', 'Glucose', 'BloodPressure', 'SkinThickness',
                     'Insulin', 'BMI', 'DiabetesPedigreeFunction', 'Age']
    
    # 准备向量化计算
    means = np.array([reference_stats[name]['mean'] for name in feature_names])
    stds = np.array([reference_stats[name]['std'] for name in feature_names])
    features_array = np.array(features, dtype=np.float64)
    
    # 计算z分数
    z_scores = (features_array - means) / stds
    
    # 计算百分位
    if SCIPY_AVAILABLE:
        percentiles_array = stats.norm.cdf(z_scores) * 100
    else:
        # 使用误差函数近似
        import math
        percentiles_array = np.array([50 * (1 + math.erf(z / math.sqrt(2))) * 100 for z in z_scores])
    
    # 四舍五入到一位小数
    percentiles_array = np.round(percentiles_array, 1)
    
    # 构建结果字典
    percentiles = {}
    for i, name in enumerate(feature_names):
        if features[i] is not None:
            percentiles[name] = float(percentiles_array[i])
        else:
            percentiles[name] = 50.0  # 默认中等
    
    return percentiles

# ==========================================
# 相似病例分布计算
# ==========================================
def compute_similar_cases(features, similarity_threshold=0.8):
    """计算相似病例的分布统计（使用向量化计算）"""
    script_dir = os.path.dirname(os.path.abspath(__file__))
    similar_cases_path = os.path.join(script_dir, 'similar_cases.npy')
    
    if not os.path.exists(similar_cases_path):
        return None
    
    try:
        # 加载历史相似病例数据
        historical_data = np.load(similar_cases_path, allow_pickle=True).item()
        
        # 计算特征相似度
        historical_features = historical_data.get('features', [])
        historical_probabilities = historical_data.get('probabilities', [])
        
        if len(historical_features) == 0 or len(historical_probabilities) == 0:
            return None
        
        # 使用向量化计算相似度（替代逐行循环）
        features_array = np.array(features, dtype=np.float64)
        historical_array = np.array(historical_features, dtype=np.float64)
        probabilities_array = np.array(historical_probabilities, dtype=np.float64)
        
        # 计算归一化特征差异
        diff = np.abs(historical_array - features_array)
        similarities = 1 - np.mean(diff / (features_array + 1e-6), axis=1)
        
        # 筛选相似病例
        similar_mask = similarities >= similarity_threshold
        similar_probs = probabilities_array[similar_mask]
        
        if len(similar_probs) > 0:
            return {
                'count': int(len(similar_probs)),
                'avg_probability': round(float(np.mean(similar_probs)), 2),
                'min_probability': round(float(np.min(similar_probs)), 2),
                'max_probability': round(float(np.max(similar_probs)), 2),
                'std_probability': round(float(np.std(similar_probs)), 2)
            }
        else:
            return None
            
    except Exception as e:
        print(f"[警告] 相似病例计算失败: {str(e)}", file=sys.stderr)
        return None

# ==========================================
# 保存预测记录（用于后续相似病例分析）
# ==========================================
def save_prediction_record(features, probability):
    """保存当前预测记录到历史数据"""
    script_dir = os.path.dirname(os.path.abspath(__file__))
    similar_cases_path = os.path.join(script_dir, 'similar_cases.npy')
    
    try:
        # 加载现有数据或创建新数据
        if os.path.exists(similar_cases_path):
            historical_data = np.load(similar_cases_path, allow_pickle=True).item()
            historical_features = historical_data.get('features', [])
            historical_probabilities = historical_data.get('probabilities', [])
        else:
            historical_features = []
            historical_probabilities = []
        
        # 添加新记录
        historical_features.append(features.tolist() if isinstance(features, np.ndarray) else features)
        historical_probabilities.append(probability)
        
        # 限制历史记录数量（保留最近1000条）
        if len(historical_features) > 1000:
            historical_features = historical_features[-1000:]
            historical_probabilities = historical_probabilities[-1000:]
        
        # 保存更新后的数据
        historical_data = {
            'features': historical_features,
            'probabilities': historical_probabilities
        }
        np.save(similar_cases_path, historical_data)
        print(f"[信息] 预测记录已保存", file=sys.stderr)
        
    except Exception as e:
        print(f"[警告] 保存预测记录失败: {str(e)}", file=sys.stderr)

# ==========================================
# 主函数
# ==========================================
def main():
    # 性能监控：记录开始时间
    start_time = time.time()
    timings = {}
    
    print("[开始] Python 单条预测脚本执行（优化版）", file=sys.stderr)
    print("[时间] 当前时间：", datetime.now().strftime("%Y-%m-%d %H:%M:%S"), file=sys.stderr)

    # 解决 Windows 控制台中文乱码
    if sys.platform == 'win32':
        sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8', errors='replace')
        sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8', errors='replace')

    # 1. 参数解析（支持可选的 --model、--charts、--mc-iterations 参数）
    model_base = None
    generate_charts = False  # 默认不生成图表（由前端ECharts渲染）
    mc_iterations = 10  # 默认MC Dropout迭代次数为10次
    
    # 解析命令行参数
    remaining_args = []
    i = 1
    while i < len(sys.argv):
        arg = sys.argv[i]
        if arg == '--model':
            if i + 1 < len(sys.argv):
                model_base = sys.argv[i + 1]
                i += 2
            else:
                result = {
                    "status": "error",
                    "msg": "--model 参数需要指定模型路径",
                    "time": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
                }
                print(json.dumps(result, ensure_ascii=False))
                sys.exit(1)
        elif arg == '--charts':
            generate_charts = True
            i += 1
        elif arg == '--mc-iterations':
            if i + 1 < len(sys.argv):
                try:
                    mc_iterations = int(sys.argv[i + 1])
                    if mc_iterations < 1 or mc_iterations > 100:
                        raise ValueError("MC迭代次数必须在1-100之间")
                    i += 2
                except ValueError as e:
                    result = {
                        "status": "error",
                        "msg": f"--mc-iterations 参数格式错误：{str(e)}",
                        "time": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
                    }
                    print(json.dumps(result, ensure_ascii=False))
                    sys.exit(1)
            else:
                result = {
                    "status": "error",
                    "msg": "--mc-iterations 参数需要指定迭代次数",
                    "time": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
                }
                print(json.dumps(result, ensure_ascii=False))
                sys.exit(1)
        else:
            remaining_args.append(arg)
            i += 1
    
    if len(remaining_args) != 8:
        result = {
            "status": "error",
            "msg": f"参数数量错误！需要 8 个特征参数，实际收到 {len(remaining_args)} 个。",
            "time": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        }
        print(json.dumps(result, ensure_ascii=False))
        sys.exit(1)

    # 2. 解析特征参数
    try:
        pregnancies = float(remaining_args[0])
        glucose = float(remaining_args[1])
        blood_pressure = float(remaining_args[2])
        skin_thickness = float(remaining_args[3])
        insulin = float(remaining_args[4])
        bmi = float(remaining_args[5])
        diabetes_pedigree_function = float(remaining_args[6])
        age = float(remaining_args[7])
    except ValueError as e:
        result = {
            "status": "error",
            "msg": f"参数格式错误：{str(e)}",
            "time": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        }
        print(json.dumps(result, ensure_ascii=False))
        sys.exit(1)
    
    print(f"[配置] 模型路径: {model_base or '默认'}", file=sys.stderr)
    print(f"[配置] 生成图表: {generate_charts}", file=sys.stderr)
    print(f"[配置] MC迭代次数: {mc_iterations}", file=sys.stderr)

    features = [pregnancies, glucose, blood_pressure, skin_thickness,
                insulin, bmi, diabetes_pedigree_function, age]

    feature_names = ['Pregnancies', 'Glucose', 'BloodPressure', 'SkinThickness',
                     'Insulin', 'BMI', 'DiabetesPedigreeFunction', 'Age']

    # 3. 模型文件路径
    if model_base is None:
        script_dir = os.path.dirname(os.path.abspath(__file__))
        model_base = os.path.join(script_dir, 'diabetes_model')  # 不含扩展名
    
    model_path = model_base + '.pth'
    encoder_path = model_base + '_encoder.pkl'
    scaler_path = model_base + '_scaler.pkl'

    # 4. 初始化结果容器
    result = {
        "status": "success",
        "prediction": None,
        "probability": None,
        "confidence_interval": None,
        "feature_names": feature_names,
        "feature_importance": None,
        "time": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
        "features": dict(zip(feature_names, features))
    }

    # 5. 尝试加载模型并执行增强预测
    use_enhanced = False
    try:
        if os.path.exists(model_path) and os.path.exists(scaler_path) and os.path.exists(encoder_path):
            # 模型加载计时
            model_load_start = time.time()
            scaler = joblib.load(scaler_path)
            label_encoder = joblib.load(encoder_path)

            model = DiabetesModel(input_dim=8)
            model.load_state_dict(torch.load(model_path, map_location='cpu'))
            model.eval()
            timings['model_load'] = time.time() - model_load_start
            print(f"[信息] 模型加载完成 (耗时: {timings['model_load']:.3f}秒)", file=sys.stderr)

            # 数据标准化
            features_array = np.array(features).reshape(1, -1)
            features_scaled = scaler.transform(features_array)
            X_tensor = torch.tensor(features_scaled, dtype=torch.float32)

            # ----- 5.1 不确定性估计 (MC Dropout) -----
            try:
                mc_start = time.time()
                prob_mean, prob_std = predict_with_uncertainty(model, X_tensor, n_iter=mc_iterations)
                timings['mc_dropout'] = time.time() - mc_start
                ci_lower = max(0.0, prob_mean - 1.96 * prob_std)
                ci_upper = min(1.0, prob_mean + 1.96 * prob_std)
                result["confidence_interval"] = [round(ci_lower * 100, 2), round(ci_upper * 100, 2)]
                result["probability"] = round(prob_mean * 100, 2)
                result["prediction"] = 1 if prob_mean > 0.5 else 0
                print(f"[成功] 不确定性估计完成 (耗时: {timings['mc_dropout']:.3f}秒, 迭代次数: {mc_iterations})", file=sys.stderr)
            except Exception as e:
                print(f"[警告] 不确定性估计失败，回退到常规预测: {str(e)}", file=sys.stderr)
                # 回退到单次预测
                model.eval()
                with torch.no_grad():
                    outputs = model(X_tensor)
                    prob = torch.softmax(outputs, dim=1)[0][1].item()
                result["probability"] = round(prob * 100, 2)
                result["prediction"] = 1 if prob > 0.5 else 0

            # ----- 5.2 SHAP 特征重要性 -----
            try:
                shap_start = time.time()
                model.eval()  # SHAP 计算应在 eval 模式下
                importance = compute_shap_importance(model, X_tensor, feature_names, model_base)
                timings['shap'] = time.time() - shap_start
                if importance is not None:
                    result["feature_importance"] = importance
                    print(f"[成功] SHAP 特征重要性计算完成 (耗时: {timings['shap']:.3f}秒)", file=sys.stderr)
                else:
                    print("[警告] SHAP 不可用，跳过特征重要性", file=sys.stderr)
            except Exception as e:
                print(f"[警告] SHAP 计算失败: {str(e)}", file=sys.stderr)

            use_enhanced = True
        else:
            print("[警告] 模型文件不完整，将使用规则引擎", file=sys.stderr)
    except Exception as e:
        print(f"[错误] 模型加载或增强预测失败: {str(e)}", file=sys.stderr)
        print(traceback.format_exc(), file=sys.stderr)

    # 6. 如果增强预测未执行或失败，使用规则引擎
    if not use_enhanced:
        pred_class, prob = rule_based_predict(features)
        result["prediction"] = pred_class
        result["probability"] = round(prob * 100, 2)
        result["confidence_interval"] = None
        result["feature_importance"] = None
        print("[信息] 使用规则引擎完成预测", file=sys.stderr)

    # 7. 确定风险等级（基于最终概率）
    prob_val = result["probability"] / 100.0
    if prob_val > 0.6:
        result["risk_level"] = "high"
    elif prob_val > 0.3:
        result["risk_level"] = "medium"
    else:
        result["risk_level"] = "low"

    # 8. 计算百分位排名
    percentile_start = time.time()
    percentiles = compute_all_percentiles(features)
    timings['percentiles'] = time.time() - percentile_start
    if percentiles:
        result["percentiles"] = percentiles
        print(f"[成功] 百分位计算完成 (耗时: {timings['percentiles']:.3f}秒)", file=sys.stderr)

    # 9. 计算相似病例分布
    similar_start = time.time()
    similar_cases = compute_similar_cases(features)
    timings['similar_cases'] = time.time() - similar_start
    if similar_cases:
        result["similar_cases"] = similar_cases
        print(f"[成功] 相似病例分析完成 (耗时: {timings['similar_cases']:.3f}秒)", file=sys.stderr)

    # 10. 保存当前预测记录
    save_prediction_record(features, result["probability"])

    # 11. 生成可视化图表（根据参数决定是否生成）
    if generate_charts:
        chart_start = time.time()
        script_dir = os.path.dirname(os.path.abspath(__file__))
        project_root = os.path.dirname(script_dir)
        charts_dir = os.path.join(project_root, 'files', 'charts')
        os.makedirs(charts_dir, exist_ok=True)
        charts = generate_charts(result, charts_dir)
        timings['charts'] = time.time() - chart_start
        if charts:
            result["charts"] = charts
            print(f"[成功] 所有图表生成完成 (耗时: {timings['charts']:.3f}秒)", file=sys.stderr)
        else:
            print("[信息] 图表生成跳过（matplotlib可能未安装）", file=sys.stderr)
    else:
        print("[信息] 图表生成已禁用（使用 --charts 参数启用）", file=sys.stderr)

    # 12. 计算总耗时并添加性能统计信息
    total_time = time.time() - start_time
    timings['total'] = total_time
    
    # 添加性能统计信息到结果
    result["performance"] = {
        "total_time_seconds": round(total_time, 3),
        "timings": {k: round(v, 3) for k, v in timings.items()},
        "mc_iterations": mc_iterations,
        "charts_generated": generate_charts
    }
    
    print(f"[性能] 总耗时: {total_time:.3f}秒", file=sys.stderr)
    print(f"[性能] 各步骤耗时: {json.dumps(timings, indent=2)}", file=sys.stderr)
    
    # 13. 输出 JSON 结果
    print(json.dumps(result, ensure_ascii=False))

if __name__ == '__main__':
    main()