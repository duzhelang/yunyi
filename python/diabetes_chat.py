import sys
import io
import re
import json
from datetime import datetime

# 设置标准输出编码为UTF-8，避免中文乱码
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')

try:
    from openai import OpenAI
except ImportError:
    print("错误：请安装 openai 库: pip install openai", file=sys.stderr)
    sys.exit(1)

class DiabetesChatbot:
    def __init__(self, provider, api_key):
        """
        provider: 模型服务商，可选 'glm-4-flash', 'glm-4.7-flash', 'deepseek', 'kimi', 'mimo-v2.5-pro', 'mimo-v2-flash'
        api_key: 对应的 API 密钥
        """
        self.provider = provider
        self.api_key = api_key

        # 各服务商的配置
        config_map = {
            'glm-4-flash': {
                'base_url': 'https://open.bigmodel.cn/api/paas/v4',
                'model': 'glm-4-flash-250414'
            },
            'glm-4.7-flash': {
                'base_url': 'https://open.bigmodel.cn/api/paas/v4',
                'model': 'glm-4.7-flash'
            },
            'deepseek': {
                'base_url': 'https://api.deepseek.com',
                'model': 'deepseek-V4-flash'
            },
            'kimi': {
                'base_url': 'https://api.moonshot.cn/v1',
                'model': 'moonshot-v1-8k'
            },
            'mimo-v2.5-pro': {
                'base_url': 'https://api.xiaomimimo.com/v1',
                'model': 'mimo-v2.5-pro'
            },
            'mimo-v2-flash': {
                'base_url': 'https://api.xiaomimimo.com/v1',
                'model': 'mimo-v2-flash'
            }
        }

        if provider not in config_map:
            raise ValueError(f"不支持的服务商: {provider}，可选: glm-4-flash, glm-4.7-flash, deepseek, kimi, mimo-v2.5-pro, mimo-v2-flash")

        cfg = config_map[provider]
        # 初始化 OpenAI 兼容的客户端
        self.client = OpenAI(api_key=api_key, base_url=cfg['base_url'])
        self.model = cfg['model']

        self.system_prompt = {
            "role": "system",
            "content": (
                "你是一位专业的糖尿病专科医生，请用中文回答患者问题。"
                "不要透露你是一个AI模型，始终以医生的身份与患者交流。"
            )
        }

    def get_answer(self, question):
        try:
            user_message = {"role": "user", "content": question}

            response = self.client.chat.completions.create(
                model=self.model,
                messages=[self.system_prompt, user_message],
                stream=False,
                max_tokens=300,
                temperature=0.1
            )

            answer = response.choices[0].message.content.strip()
            answer = re.sub(r'\*{1,2}|_{1,2}|`+', '', answer)
            answer = answer.replace("\n", " ")
            if any(word in answer for word in ["AI", "人工智能", "模型", "算法", "训练"]):
                answer = "我是糖尿病专科医生，专注于糖尿病健康咨询。"

            return answer

        except Exception as e:
            print(f"[Python服务] 调用失败：{str(e)}", file=sys.stderr)
            return "糖尿病健康咨询服务暂时无法响应，请稍后再试。"


if __name__ == "__main__":
    # 新的调用格式：python diabetes_chat.py <provider> <api_key> <question...>
    if len(sys.argv) < 4:
        print("参数错误：需传入 [服务商: glm-4-flash/glm-4.7-flash/deepseek/kimi/mimo-v2.5-pro/mimo-v2-flash] [API_KEY] [用户问题]", file=sys.stderr)
        sys.exit(1)

    provider = sys.argv[1].lower()
    api_key = sys.argv[2]
    question = ' '.join(sys.argv[3:])

    try:
        chatbot = DiabetesChatbot(provider, api_key)
        answer = chatbot.get_answer(question)
        # 将最终答案打印到标准输出，供 Java 进程捕获
        print(answer)
    except Exception as e:
        print(f"初始化失败: {str(e)}", file=sys.stderr)
        sys.exit(1)