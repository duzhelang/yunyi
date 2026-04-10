import warnings
warnings.filterwarnings("ignore")

import pandas as pd
import numpy as np
import sys
import os
import joblib
import torch
import torch.nn as nn
import torch.optim as optim
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler, LabelEncoder
from datetime import datetime
import io

# 解决Windows中文乱码
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')

# 定义模型结构 (必须与 predict.py 中的完全一致)
class DiabetesModel(nn.Module):
    def __init__(self, input_dim):
        super(DiabetesModel, self).__init__()
        self.fc1 = nn.Linear(input_dim, 64)
        self.fc2 = nn.Linear(64, 32)
        self.fc3 = nn.Linear(32, 2) # 二分类输出
        self.relu = nn.ReLU()
        self.dropout = nn.Dropout(0.2)

    def forward(self, x):
        x = self.relu(self.fc1(x))
        x = self.dropout(x)
        x = self.relu(self.fc2(x))
        x = self.dropout(x)
        x = self.fc3(x)
        return x

print("[开始] Python训练脚本执行")
print("[时间] 当前时间：", datetime.now().strftime("%Y-%m-%d %H:%M:%S"))
print("[参数] 接收的参数：", sys.argv)

# 检查命令行参数
# Java 调用方式: python.exe train.py <输入CSV路径> <模型输出路径(.pth)>
if len(sys.argv) != 3:
    print("[错误] 参数数量错误！需要3个参数：")
    print("  1. 训练数据CSV路径 (如 D:\\data\\train.csv)")
    print("  2. 模型保存路径 (.pth) (如 D:\\model\\diabetes_model.pth)")
    sys.exit(1)

input_csv_path = sys.argv[1]
model_output_path = sys.argv[2]

# 验证输入文件
if not os.path.exists(input_csv_path):
    print(f"[错误] 训练数据文件不存在：{input_csv_path}")
    sys.exit(1)

# 准备输出路径 (衍生出 encoder 和 scaler 的路径)
# 如果传入的是 D:\...\model.pth
# 则 encoder 为 D:\...\model_encoder.pkl
# 则 scaler 为 D:\...\model_scaler.pkl
base_name = model_output_path.rsplit('.', 1)[0] # 去掉 .pth
encoder_path = f"{base_name}_encoder.pkl"
scaler_path = f"{base_name}_scaler.pkl"

output_dir = os.path.dirname(model_output_path)
if output_dir and not os.path.exists(output_dir):
    os.makedirs(output_dir)
    print(f"[系统] 已创建模型目录：{output_dir}")

print(f"[信息] 模型将保存至：{model_output_path}")
print(f"[信息] 编码器将保存至：{encoder_path}")
print(f"[信息] 缩放器将保存至：{scaler_path}")

# 1. 读取数据
try:
    df = pd.read_csv(input_csv_path)
    print(f"[成功] 读取训练数据，共 {len(df)} 条记录")
except Exception as e:
    print(f"[错误] 读取CSV失败：{str(e)}")
    sys.exit(1)

# 2. 数据预处理
required_columns = ['Pregnancies', 'Glucose', 'BloodPressure', 'SkinThickness',
                    'Insulin', 'BMI', 'DiabetesPedigreeFunction', 'Age']
target_column = 'Outcome'

# 检查列
missing_cols = [col for col in required_columns if col not in df.columns]
if missing_cols:
    print(f"[错误] 训练数据缺少必要特征列：{missing_cols}")
    sys.exit(1)

if target_column not in df.columns:
    print(f"[错误] 训练数据缺少目标列：{target_column}")
    sys.exit(1)

# 提取特征和标签
X = df[required_columns]
y = df[target_column]

# 处理缺失值 (简单填充，可根据需求优化)
X = X.fillna(X.mean())

# 标签编码 (虽然Outcome通常是0/1，但为了通用性加上)
label_encoder = LabelEncoder()
y_encoded = label_encoder.fit_transform(y)

# 特征缩放
scaler = StandardScaler()
X_scaled = scaler.fit_transform(X)

print("[成功] 数据预处理完成 (缩放 & 编码)")

# 3. 划分数据集
X_train, X_val, y_train, y_val = train_test_split(
    X_scaled, y_encoded, test_size=0.2, random_state=42, stratify=y_encoded
)
print(f"[信息] 训练集：{len(X_train)}, 验证集：{len(X_val)}")

# 转换为 Tensor
X_train_tensor = torch.tensor(X_train, dtype=torch.float32)
y_train_tensor = torch.tensor(y_train, dtype=torch.long)
X_val_tensor = torch.tensor(X_val, dtype=torch.float32)
y_val_tensor = torch.tensor(y_val, dtype=torch.long)

# 4. 初始化模型
input_dim = len(required_columns)
model = DiabetesModel(input_dim)
criterion = nn.CrossEntropyLoss()
optimizer = optim.Adam(model.parameters(), lr=0.001)

print("[开始] 模型训练...")

# 5. 训练循环
epochs = 100
batch_size = 16
best_val_loss = float('inf')

for epoch in range(epochs):
    model.train()
    total_loss = 0

    # 简单的批处理训练
    num_batches = (len(X_train_tensor) + batch_size - 1) // batch_size
    indices = torch.randperm(len(X_train_tensor))

    for i in range(num_batches):
        start_idx = i * batch_size
        end_idx = min((i + 1) * batch_size, len(X_train_tensor))
        batch_indices = indices[start_idx:end_idx]

        batch_x = X_train_tensor[batch_indices]
        batch_y = y_train_tensor[batch_indices]

        optimizer.zero_grad()
        outputs = model(batch_x)
        loss = criterion(outputs, batch_y)
        loss.backward()
        optimizer.step()

        total_loss += loss.item()

    avg_loss = total_loss / num_batches

    # 验证
    model.eval()
    with torch.no_grad():
        val_outputs = model(X_val_tensor)
        val_loss = criterion(val_outputs, y_val_tensor)

    if val_loss < best_val_loss:
        best_val_loss = val_loss
        # 保存最佳模型
        torch.save(model.state_dict(), model_output_path)
        # 保存辅助文件
        joblib.dump(scaler, scaler_path)
        joblib.dump(label_encoder, encoder_path)

    if (epoch + 1) % 10 == 0:
        print(f"[进度] Epoch {epoch+1}/{epochs}, Loss: {avg_loss:.4f}, Val Loss: {val_loss:.4f}")

print("[成功] 训练完成！")
print(f"[结果] 最佳模型已保存至：{model_output_path}")
print(f"[结果] 缩放器已保存至：{scaler_path}")
print(f"[结果] 编码器已保存至：{encoder_path}")

# 验证文件是否存在
files_to_check = [model_output_path, scaler_path, encoder_path]
all_exist = True
for f in files_to_check:
    if not os.path.exists(f):
        print(f"[警告] 文件未生成：{f}")
        all_exist = False

if all_exist:
    print("[结束] 训练脚本成功执行")
    sys.exit(0)
else:
    print("[错误] 部分文件生成失败")
    sys.exit(1)