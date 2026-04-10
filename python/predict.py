import warnings
warnings.filterwarnings("ignore")

import pandas as pd
import numpy as np
import sys
import os
import json
import pymysql
import joblib
import torch
import torch.nn as nn
from datetime import datetime
import io
import traceback

# ==========================================
# 【配置区域】请确保此处路径与 Java 配置一致
# ==========================================
# 注意：如果你的 Java 配置改了路径，这里也需要同步修改，或者最好由 Java 传入该路径
# OUTPUT_DIRECTORY = r'D:\Software-DZL125\json'
OUTPUT_DIRECTORY = os.path.join(os.getcwd(), 'json')
# 定义和训练脚本一致的模型结构
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

def write_error_json(filepath, error_msg, debug_info=None):
    """辅助函数：当发生错误时，写入包含详细错误信息的 JSON 文件"""
    result = {
        "status": "error",
        "msg": str(error_msg),
        "time": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
        "debug_info": debug_info
    }
    try:
        # 确保目录存在
        os.makedirs(os.path.dirname(filepath), exist_ok=True)
        with open(filepath, 'w', encoding='utf-8') as f:
            json.dump(result, f, ensure_ascii=False, indent=4)
        print(f"[错误已写入文件] {filepath}")
    except Exception as e:
        print(f"[严重] 连错误文件都无法写入：{str(e)}")

def main():
    print("[开始] Python 预测脚本执行")
    print("[时间] 当前时间：", datetime.now().strftime("%Y-%m-%d %H:%M:%S"))
    print("[参数] 接收的参数：", sys.argv)

    # 解决 Windows 控制台中文乱码
    if sys.platform == 'win32':
        sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8', errors='replace')
        sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8', errors='replace')

    # 1. 参数校验
    if len(sys.argv) != 5:
        msg = f"参数数量错误！需要 5 个参数，实际收到 {len(sys.argv)} 个。用法: script.py <csv_path> <json_name> <title> <model_path>"
        print(f"[错误] {msg}")
        # 如果没有输出路径，只能打印，无法写文件
        if len(sys.argv) > 2:
            # 尝试构造一个临时路径写错误信息，或者直接退出
            pass
        sys.exit(1)

    predict_file_path = sys.argv[1]
    predict_json_name = sys.argv[2]
    prediction_title = sys.argv[3]
    model_path = sys.argv[4]

    # 2. 准备输出路径
    if not os.path.exists(OUTPUT_DIRECTORY):
        try:
            os.makedirs(OUTPUT_DIRECTORY, exist_ok=True)
            print(f"[成功] 创建输出目录：{OUTPUT_DIRECTORY}")
        except Exception as e:
            print(f"[严重错误] 无法创建输出目录：{e}")
            sys.exit(1)

    if not predict_json_name.endswith('.json'):
        predict_json_name += '.json'

    output_predict_file_name = os.path.join(OUTPUT_DIRECTORY, predict_json_name)
    print(f"[信息] 目标 JSON 文件路径：{output_predict_file_name}")

    # 3. 验证输入文件
    if not os.path.exists(predict_file_path):
        write_error_json(output_predict_file_name, f"输入文件不存在：{predict_file_path}")
        sys.exit(1)
    print(f"[成功] 输入文件存在：{predict_file_path}")

    # 4. 验证模型文件
    encoder_path = model_path.replace('.pth', '_encoder.pkl')
    scaler_path = model_path.replace('.pth', '_scaler.pkl')
    model_files = [model_path, encoder_path, scaler_path]

    for path in model_files:
        if not os.path.exists(path):
            write_error_json(output_predict_file_name, f"模型文件缺失：{path}", {"checked_paths": model_files})
            sys.exit(1)
    print("[成功] 所有模型文件均存在")

    # 5. 读取数据 (核心修复：自动识别编码)
    test = None
    read_error = None

    # 尝试的编码列表
    encodings = ['utf-8', 'gbk', 'gb2312', 'latin1', 'cp1252']

    file_ext = os.path.splitext(predict_file_path)[1].lower()

    try:
        if file_ext == '.csv':
            for enc in encodings:
                try:
                    test = pd.read_csv(predict_file_path, encoding=enc)
                    print(f"[成功] 使用 '{enc}' 编码读取 CSV 成功。行数：{len(test)}")
                    break
                except UnicodeDecodeError:
                    continue
                except Exception as e:
                    read_error = f"编码 {enc} 读取失败：{str(e)}"
                    continue

            if test is None:
                raise Exception(f"无法读取 CSV 文件。尝试了编码：{encodings}。最后错误：{read_error}")

        elif file_ext in ['.xls', '.xlsx']:
            test = pd.read_excel(predict_file_path)
            print(f"[成功] 读取 Excel 成功。行数：{len(test)}")
        else:
            raise Exception(f"不支持的文件格式：{file_ext}")

        # 【关键修复】清洗列名：去除首尾空格，防止 " Pregnancies" 这种问题
        test.columns = test.columns.str.strip()
        print(f"[调试] 清洗后的列名：{list(test.columns)}")

    except Exception as e:
        error_msg = f"数据读取失败：{str(e)}"
        print(f"[致命错误] {error_msg}")
        write_error_json(output_predict_file_name, error_msg, {"traceback": traceback.format_exc()})
        sys.exit(1)

    # 6. 检查必要列
    required_columns = ['Pregnancies', 'Glucose', 'BloodPressure', 'SkinThickness',
                        'Insulin', 'BMI', 'DiabetesPedigreeFunction', 'Age']

    missing_cols = [col for col in required_columns if col not in test.columns]

    if missing_cols:
        error_msg = f"缺少必要特征列：{missing_cols}"
        debug_info = {
            "required": required_columns,
            "found": list(test.columns),
            "suggestion": "请检查 CSV 表头是否有多余空格、拼写错误或使用了中文列名。"
        }
        print(f"[致命错误] {error_msg}")
        print(f"[调试] 实际列名：{list(test.columns)}")
        write_error_json(output_predict_file_name, error_msg, debug_info)
        sys.exit(1)

    print("[成功] 列名检查通过")

    # 7. 准备数据
    test_ids = test["id"].astype(str).tolist() if "id" in test.columns else [str(i) for i in range(len(test))]
    X_test = test.drop(columns=["id", "Outcome"], errors="ignore")

    # 确保列顺序与训练时一致
    X_test = X_test[required_columns]

    # 8. 加载模型
    try:
        scaler = joblib.load(scaler_path)
        label_encoder = joblib.load(encoder_path)

        input_dim = len(required_columns)
        model = DiabetesModel(input_dim)
        model.load_state_dict(torch.load(model_path, map_location=torch.device('cpu')))
        model.eval()
        print("[成功] 模型加载完成")
    except Exception as e:
        error_msg = f"模型加载失败：{str(e)}"
        print(f"[致命错误] {error_msg}")
        write_error_json(output_predict_file_name, error_msg, {"traceback": traceback.format_exc()})
        sys.exit(1)

    # 9. 执行预测
    try:
        X_test_scaled = scaler.transform(X_test)
        X_test_tensor = torch.tensor(X_test_scaled, dtype=torch.float32)

        with torch.no_grad():
            outputs = model(X_test_tensor)
            _, y_test_pred = torch.max(outputs, 1)

        y_pred = label_encoder.inverse_transform(y_test_pred.numpy())
        y_pred_int = [int(pred) for pred in y_pred]
        print(f"[成功] 预测完成，生成 {len(y_pred_int)} 条结果")
    except Exception as e:
        error_msg = f"预测计算失败：{str(e)}"
        print(f"[致命错误] {error_msg}")
        write_error_json(output_predict_file_name, error_msg, {"traceback": traceback.format_exc()})
        sys.exit(1)

    # 10. 构建结果对象
    result = {
        "status": "success",
        "title": prediction_title,
        "create_time": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
        "file_source": os.path.basename(predict_file_path),
        "total_records": len(y_pred_int),
        "predictions": {test_id: int(pred) for test_id, pred in zip(test_ids, y_pred_int)}
    }

    # 11. 数据库操作 (可选，失败不影响文件生成)
    db_success = False
    url = "" # 用于匹配
    try:
        # 构造 URL (需与 Java 逻辑一致)
        file_basename = os.path.basename(predict_file_path)
        url = f"http://localhost:9090/DataTest/{file_basename}"

        conn = pymysql.connect(
            host='127.0.0.1',
            port=3306,
            user='root',
            password='010125', # ⚠️ 请确认密码是否正确
            database='dongfang',
            charset='utf8'
        )
        cursor = conn.cursor()

        # 查找文件 ID
        cursor.execute("SELECT id FROM sys_testfile WHERE url=%s", (url,))
        row = cursor.fetchone()

        if row:
            testfile_id = row[0]
            # 清空旧结果
            cursor.execute("DELETE FROM sys_result WHERE testfile_id=%s", (testfile_id,))

            # 插入新结果
            insert_data = [(tid, pred, testfile_id) for tid, pred in zip(test_ids, y_pred_int)]
            cursor.executemany(
                "INSERT INTO sys_result (testid, result, create_time, testfile_id) VALUES (%s, %s, NOW(), %s)",
                insert_data
            )
            conn.commit()
            db_success = True
            print(f"[成功] 数据库更新成功，ID: {testfile_id}")
        else:
            print(f"[警告] 数据库中未找到对应文件记录 (URL: {url})，跳过数据库写入，仅保存文件。")
            # 不抛出异常，继续保存文件

        cursor.close()
        conn.close()
    except Exception as e:
        print(f"[警告] 数据库操作失败：{str(e)}")
        # 不中断流程，继续保存文件

    # 12. 保存 JSON 文件
    try:
        with open(output_predict_file_name, 'w', encoding='utf-8') as f:
            json.dump(result, f, ensure_ascii=False, indent=4)

        if os.path.exists(output_predict_file_name):
            size = os.path.getsize(output_predict_file_name)
            print(f"[完成] 文件已保存：{output_predict_file_name} (大小：{size} bytes)")
            if size < 10:
                print("[警告] 文件过小，可能内容异常，请检查！")
        else:
            print("[严重错误] 文件写入后消失！")
            sys.exit(1)

    except Exception as e:
        print(f"[严重错误] 保存 JSON 文件失败：{str(e)}")
        sys.exit(1)

    print("[结束] 脚本执行完毕")
    sys.exit(0)

if __name__ == '__main__':
    main()