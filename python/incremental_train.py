import warnings
warnings.filterwarnings("ignore")

import pandas as pd
import numpy as np
import sys
import os
import glob
import argparse
import joblib
import json
import torch
import torch.nn as nn
import torch.optim as optim
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler, LabelEncoder
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, roc_auc_score, confusion_matrix
from datetime import datetime
import io

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')


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


def get_project_root():
    return os.path.dirname(os.path.dirname(os.path.abspath(__file__)))


def resolve_model_path(base_model, project_root):
    if os.path.isabs(base_model) and os.path.exists(base_model):
        return base_model

    candidates = [
        os.path.join(project_root, base_model),
        os.path.join(project_root, 'data', 'models', 'pth_models', base_model),
        os.path.join(project_root, 'data', 'models', base_model),
    ]
    for c in candidates:
        if os.path.exists(c):
            return c

    return base_model


def resolve_preprocessor_path(model_path, suffix):
    base = model_path.rsplit('.', 1)[0]
    name = os.path.basename(base)
    parent = os.path.dirname(base)
    pkl_dir = os.path.join(os.path.dirname(os.path.dirname(parent)), 'pkl_files')

    candidates = [
        base + suffix,
        os.path.join(parent, name + suffix),
        os.path.join(pkl_dir, name + suffix),
    ]
    for c in candidates:
        if os.path.exists(c):
            return c
    return candidates[0]


def freeze_layers(model, freeze_config):
    if freeze_config == 'none':
        return
    elif freeze_config == 'fc1':
        for param in model.fc1.parameters():
            param.requires_grad = False
        print("[冻结] 已冻结层: fc1")
    elif freeze_config == 'fc1+fc2':
        for param in model.fc1.parameters():
            param.requires_grad = False
        for param in model.fc2.parameters():
            param.requires_grad = False
        print("[冻结] 已冻结层: fc1, fc2")
    elif freeze_config == 'all-hidden':
        for param in model.fc1.parameters():
            param.requires_grad = False
        for param in model.fc2.parameters():
            param.requires_grad = False
        print("[冻结] 已冻结层: fc1, fc2 (仅训练输出层 fc3)")


def scan_csv_files(search_dirs):
    found = []
    for d in search_dirs:
        if not os.path.isdir(d):
            continue
        for pattern in ['*.csv', '*.CSV']:
            for f in glob.glob(os.path.join(d, '**', pattern), recursive=True):
                try:
                    size_mb = os.path.getsize(f) / (1024 * 1024)
                    df_head = pd.read_csv(f, nrows=0)
                    cols = list(df_head.columns)
                    sample_count = sum(1 for _ in open(f, encoding='utf-8', errors='replace')) - 1
                    found.append({
                        'path': f,
                        'name': os.path.basename(f),
                        'size_mb': round(size_mb, 2),
                        'columns': cols,
                        'column_count': len(cols),
                        'sample_count': sample_count,
                        'dir': os.path.relpath(os.path.dirname(f), get_project_root())
                    })
                except Exception:
                    pass
    return found


def select_dataset():
    project_root = get_project_root()
    search_dirs = [
        os.path.join(project_root, 'data', 'train'),
        os.path.join(project_root, 'data', 'test'),
        os.path.join(project_root, 'data'),
        os.path.join(project_root, 'python', 'data'),
        os.path.join(project_root, 'python'),
    ]
    search_dirs = list(dict.fromkeys(search_dirs))

    print("[扫描] 正在搜索可用数据集...")
    datasets = scan_csv_files(search_dirs)

    if not datasets:
        print("[错误] 未找到任何 CSV 文件，请将数据集放入以下目录之一：")
        for d in search_dirs:
            print(f"  - {os.path.relpath(d, project_root)}")
        sys.exit(1)

    print(f"\n[信息] 共找到 {len(datasets)} 个数据集：\n")
    print(f"  {'序号':<6}{'文件名':<40}{'样本数':<10}{'特征数':<8}{'大小(MB)':<10}{'所在目录'}")
    print(f"  {'─'*6}{'─'*40}{'─'*10}{'─'*8}{'─'*10}{'─'*20}")
    for i, ds in enumerate(datasets):
        print(f"  {i+1:<6}{ds['name']:<40}{ds['sample_count']:<10}{ds['column_count']:<8}{ds['size_mb']:<10}{ds['dir']}")

    print()
    while True:
        try:
            choice = input(f"[选择] 请输入数据集序号 (1-{len(datasets)})：").strip()
            if not choice:
                continue
            idx = int(choice) - 1
            if 0 <= idx < len(datasets):
                selected = datasets[idx]
                print(f"\n[确认] 已选择: {selected['name']} ({selected['sample_count']} 条记录, {selected['column_count']} 个特征)")
                print(f"[路径] {selected['path']}")
                return selected['path']
            else:
                print(f"[提示] 请输入 1 到 {len(datasets)} 之间的数字")
        except ValueError:
            print("[提示] 请输入有效的数字")
        except (EOFError, KeyboardInterrupt):
            print("\n[取消] 用户取消操作")
            sys.exit(0)


def parse_arguments():
    parser = argparse.ArgumentParser(description='糖尿病预测模型增量训练脚本')
    parser.add_argument('csv_path', nargs='?', default=None, help='新训练数据CSV路径')
    parser.add_argument('model_path', nargs='?', default=None, help='输出模型保存路径(.pth)')
    parser.add_argument('--base-model', type=str, required=False, default=None, help='已有模型文件路径(.pth)')
    parser.add_argument('--lr', '--learning-rate', type=float, default=0.0001, help='学习率 (默认: 0.0001)')
    parser.add_argument('--epochs', type=int, default=50, help='训练轮数 (默认: 50)')
    parser.add_argument('--batch-size', type=int, default=16, help='批次大小 (默认: 16)')
    parser.add_argument('--test-size', type=float, default=0.2, help='验证集比例 (默认: 0.2)')
    parser.add_argument('--model-name', type=str, default=None, help='模型名称 (默认: 自动生成)')
    parser.add_argument('--reuse-preprocessor', action='store_true', default=False, help='复用已有模型的 scaler 和 encoder')
    parser.add_argument('--freeze-layers', type=str, default='none', choices=['none', 'fc1', 'fc1+fc2', 'all-hidden'], help='冻结层配置 (默认: none)')

    args, unknown = parser.parse_known_args()

    if unknown:
        positional = [arg for arg in unknown if not arg.startswith('-')]
        if positional:
            if args.csv_path is None and len(positional) > 0:
                args.csv_path = positional[0]
            if args.model_path is None and len(positional) > 1:
                args.model_path = positional[1]

    if '--help' in sys.argv or '-h' in sys.argv:
        parser.print_help()
        print("\n使用示例：")
        print("  python incremental_train.py --base-model diabetes_model.pth")
        print("  python incremental_train.py new_data.csv output.pth --base-model model.pth --reuse-preprocessor")
        print("  python incremental_train.py data.csv out.pth --base-model model.pth --freeze-layers fc1 --lr 0.00005")
        print("  python incremental_train.py --base-model model.pth --freeze-layers all-hidden --epochs 30")
        sys.exit(0)

    return args


print("=" * 60)
print("[开始] 糖尿病预测模型 - 增量训练脚本")
print("[时间]", datetime.now().strftime("%Y-%m-%d %H:%M:%S"))
print("=" * 60)

args = parse_arguments()

project_root = get_project_root()

if args.base_model is None:
    print("[错误] 增量训练必须指定基础模型，请使用 --base-model 参数")
    print("[提示] 示例: python incremental_train.py data.csv out.pth --base-model diabetes_model.pth")
    sys.exit(1)

base_model_path = resolve_model_path(args.base_model, project_root)
if not os.path.exists(base_model_path):
    print(f"[错误] 基础模型文件不存在: {base_model_path}")
    print(f"[提示] 原始参数: {args.base_model}")
    sys.exit(1)

print(f"[信息] 基础模型: {base_model_path}")

if args.csv_path is None:
    print("[模式] 交互式数据集选择模式")
    input_csv_path = select_dataset()
else:
    input_csv_path = args.csv_path

if not os.path.isabs(input_csv_path):
    candidate = os.path.join(project_root, input_csv_path)
    if os.path.exists(candidate):
        input_csv_path = candidate

if not os.path.exists(input_csv_path):
    print(f"[错误] 训练数据文件不存在: {input_csv_path}")
    sys.exit(1)

models_dir = os.path.join(project_root, 'data', 'models')

if args.model_path:
    model_output_path = args.model_path
    if not os.path.isabs(model_output_path):
        model_output_path = os.path.join(models_dir, model_output_path)
else:
    model_name = args.model_name or f"incremental_{datetime.now().strftime('%Y%m%d_%H%M%S')}"
    model_output_path = os.path.join(models_dir, f"{model_name}.pth")

model_name = os.path.basename(model_output_path).rsplit('.', 1)[0]

pth_dir = os.path.join(models_dir, 'pth_models')
pkl_dir = os.path.join(models_dir, 'pkl_files')
npy_dir = os.path.join(models_dir, 'npy_data')

for dir_path in [pth_dir, pkl_dir, npy_dir]:
    if not os.path.exists(dir_path):
        os.makedirs(dir_path)
        print(f"[系统] 已创建目录: {dir_path}")

model_output_path = os.path.join(pth_dir, f"{model_name}.pth")
encoder_path = os.path.join(pkl_dir, f"{model_name}_encoder.pkl")
scaler_path = os.path.join(pkl_dir, f"{model_name}_scaler.pkl")
background_path = os.path.join(npy_dir, f"{model_name}_background.npy")

lr = args.lr
epochs = args.epochs
batch_size = args.batch_size
test_size = args.test_size
reuse_preprocessor = args.reuse_preprocessor
freeze_layers_config = args.freeze_layers

print(f"\n{'─' * 40}")
print(f"[配置] 训练模式: 增量训练 (Fine-tuning)")
print(f"[配置] 学习率: {lr}")
print(f"[配置] 训练轮数: {epochs}")
print(f"[配置] 批次大小: {batch_size}")
print(f"[配置] 验证集比例: {test_size}")
print(f"[配置] 复用预处理器: {'是' if reuse_preprocessor else '否'}")
print(f"[配置] 冻结层: {freeze_layers_config}")
print(f"[配置] 数据集: {input_csv_path}")
print(f"[配置] 基础模型: {base_model_path}")
print(f"{'─' * 40}")

required_columns = ['Pregnancies', 'Glucose', 'BloodPressure', 'SkinThickness',
                    'Insulin', 'BMI', 'DiabetesPedigreeFunction', 'Age']
target_column = 'Outcome'

print("\n[步骤 1/5] 加载基础模型权重...")
input_dim = len(required_columns)
model = DiabetesModel(input_dim)

try:
    state_dict = torch.load(base_model_path, map_location='cpu', weights_only=True)
    model.load_state_dict(state_dict)
    print(f"[成功] 已加载基础模型权重: {base_model_path}")
except Exception as e:
    print(f"[错误] 加载基础模型失败: {str(e)}")
    sys.exit(1)

print("\n[步骤 2/5] 配置层冻结...")
freeze_layers(model, freeze_layers_config)

trainable_params = sum(p.numel() for p in model.parameters() if p.requires_grad)
total_params = sum(p.numel() for p in model.parameters())
print(f"[信息] 可训练参数: {trainable_params}/{total_params}")

print("\n[步骤 3/5] 加载和预处理新数据...")
try:
    df = pd.read_csv(input_csv_path)
    print(f"[成功] 读取训练数据，共 {len(df)} 条记录")
except Exception as e:
    print(f"[错误] 读取CSV失败: {str(e)}")
    sys.exit(1)

missing_cols = [col for col in required_columns if col not in df.columns]
if missing_cols:
    print(f"[错误] 训练数据缺少必要特征列: {missing_cols}")
    sys.exit(1)

if target_column not in df.columns:
    print(f"[错误] 训练数据缺少目标列: {target_column}")
    sys.exit(1)

X = df[required_columns]
y = df[target_column]
X = X.fillna(X.mean())

if reuse_preprocessor:
    base_scaler_path = resolve_preprocessor_path(base_model_path, '_scaler.pkl')
    base_encoder_path = resolve_preprocessor_path(base_model_path, '_encoder.pkl')

    if os.path.exists(base_scaler_path) and os.path.exists(base_encoder_path):
        try:
            scaler = joblib.load(base_scaler_path)
            label_encoder = joblib.load(base_encoder_path)
            X_scaled = scaler.transform(X)
            y_encoded = label_encoder.transform(y)
            print(f"[成功] 已复用基础模型的 scaler 和 encoder")
            print(f"  scaler: {base_scaler_path}")
            print(f"  encoder: {base_encoder_path}")
        except Exception as e:
            print(f"[警告] 加载预处理器失败，将重新创建: {str(e)}")
            label_encoder = LabelEncoder()
            y_encoded = label_encoder.fit_transform(y)
            scaler = StandardScaler()
            X_scaled = scaler.fit_transform(X)
    else:
        print(f"[警告] 未找到基础模型的预处理器文件，将重新创建")
        label_encoder = LabelEncoder()
        y_encoded = label_encoder.fit_transform(y)
        scaler = StandardScaler()
        X_scaled = scaler.fit_transform(X)
else:
    label_encoder = LabelEncoder()
    y_encoded = label_encoder.fit_transform(y)
    scaler = StandardScaler()
    X_scaled = scaler.fit_transform(X)
    print("[信息] 使用新数据重新创建 scaler 和 encoder")

print("[成功] 数据预处理完成")

X_train, X_val, y_train, y_val = train_test_split(
    X_scaled, y_encoded, test_size=test_size, random_state=42, stratify=y_encoded
)
print(f"[信息] 训练集: {len(X_train)}, 验证集: {len(X_val)}")

X_train_tensor = torch.tensor(X_train, dtype=torch.float32)
y_train_tensor = torch.tensor(y_train, dtype=torch.long)
X_val_tensor = torch.tensor(X_val, dtype=torch.float32)
y_val_tensor = torch.tensor(y_val, dtype=torch.long)

criterion = nn.CrossEntropyLoss()
optimizer = optim.Adam(filter(lambda p: p.requires_grad, model.parameters()), lr=lr)

print(f"\n[步骤 4/5] 开始增量训练...")
print(f"[信息] 优化器仅更新可训练参数")

best_val_loss = float('inf')

for epoch in range(epochs):
    model.train()
    total_loss = 0
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

    model.eval()
    with torch.no_grad():
        val_outputs = model(X_val_tensor)
        val_loss = criterion(val_outputs, y_val_tensor)
        _, predicted = torch.max(val_outputs, 1)
        correct = (predicted == y_val_tensor).sum().item()
        val_acc = correct / len(y_val_tensor)

    if val_loss < best_val_loss:
        best_val_loss = val_loss
        torch.save(model.state_dict(), model_output_path)
        if not reuse_preprocessor:
            joblib.dump(scaler, scaler_path)
            joblib.dump(label_encoder, encoder_path)
        else:
            joblib.dump(scaler, scaler_path)
            joblib.dump(label_encoder, encoder_path)

    if (epoch + 1) % 10 == 0 or epoch == 0:
        print(f"[进度] Epoch {epoch+1}/{epochs}, Loss: {avg_loss:.4f}, Val Loss: {val_loss:.4f}, Val Acc: {val_acc:.4f}")

print(f"\n[步骤 5/5] 保存模型和辅助文件...")

print(f"[结果] 最佳模型已保存至: {model_output_path}")
print(f"[结果] 缩放器已保存至: {scaler_path}")
print(f"[结果] 编码器已保存至: {encoder_path}")

background_sample = X_train_tensor[:200].numpy()
np.save(background_path, background_sample)
print(f"[结果] 背景数据已保存至: {background_path}")

files_to_check = [model_output_path, scaler_path, encoder_path, background_path]
all_exist = True
for f in files_to_check:
    if not os.path.exists(f):
        print(f"[警告] 文件未生成: {f}")
        all_exist = False

print(f"\n{'=' * 60}")
if all_exist:
    best_model = DiabetesModel(input_dim)
    best_model.load_state_dict(torch.load(model_output_path, map_location='cpu', weights_only=True))
    best_model.eval()
    with torch.no_grad():
        final_outputs = best_model(X_val_tensor)
        final_loss = criterion(final_outputs, y_val_tensor).item()
        final_probs = torch.softmax(final_outputs, dim=1)[:, 1].numpy()
        _, final_predicted = torch.max(final_outputs, 1)
        y_pred = final_predicted.numpy()
        y_true = y_val_tensor.numpy()
        final_accuracy = accuracy_score(y_true, y_pred)
        final_precision = precision_score(y_true, y_pred, zero_division=0)
        final_recall = recall_score(y_true, y_pred, zero_division=0)
        final_f1 = f1_score(y_true, y_pred, zero_division=0)
        final_auc = roc_auc_score(y_true, final_probs)
        cm = confusion_matrix(y_true, y_pred)
        cm_list = cm.tolist()

    metrics_json = json.dumps({
        "accuracy": round(final_accuracy, 4),
        "loss": round(final_loss, 4),
        "precision": round(final_precision, 4),
        "recall": round(final_recall, 4),
        "f1": round(final_f1, 4),
        "auc": round(final_auc, 4),
        "confusionMatrix": cm_list
    })
    print(f"[METRICS]{metrics_json}")

    print("[完成] 增量训练成功！")
    print(f"[基础模型] {base_model_path}")
    print(f"[新模型] {model_output_path}")
    print(f"[训练轮数] {epochs}")
    print(f"[学习率] {lr}")
    print(f"[冻结配置] {freeze_layers_config}")
    print(f"[预处理器] {'复用已有' if reuse_preprocessor else '重新创建'}")
    print(f"[指标] 准确率: {final_accuracy:.4f}, 损失: {final_loss:.4f}, 精确率: {final_precision:.4f}, 召回率: {final_recall:.4f}, F1: {final_f1:.4f}, AUC: {final_auc:.4f}")
    print(f"[混淆矩阵] TN={cm[0][0]}, FP={cm[0][1]}, FN={cm[1][0]}, TP={cm[1][1]}")
    print("=" * 60)
    sys.exit(0)
else:
    print("[错误] 部分文件生成失败")
    print("=" * 60)
    sys.exit(1)
