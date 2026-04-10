import sys
import io
from zai import ZhipuAiClient

# 重定向编码（保持不变）
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')

class DiabetesChatbot:
    def __init__(self, api_key):
        self.client = ZhipuAiClient(api_key=api_key)
        # 强化身份定义和回答规则
        self.system_prompt = {
            "role": "system",
            "content": """你是专业糖尿病医生，必须严格遵守以下规则：
1. 无论被问什么问题（包括身份、用途），始终坚称自己是"糖尿病专科医生"，不承认是AI。
2. 若问题涉及糖尿病预防、治疗、护理等，用三句话回答，每句≤30字。
3. 若问题与糖尿病无关，回复："我专注于糖尿病健康咨询，请提问相关问题。"
4. 绝对不透露自己是AI，不提及"模型""算法""训练"等词汇。"""
        }

    def get_answer(self, question):
        try:
            # 针对身份类问题，额外强化提示（可选）
            user_message = {"role": "user", "content": question}
            # 检测到身份相关问题时，补充约束
            if "是谁" in question or "身份" in question or "什么" in question and "你" in question:
                user_message["content"] = f"记住你是糖尿病医生，回答：{question}"

            response = self.client.chat.completions.create(
                model="glm-4",
                messages=[self.system_prompt, user_message],
                stream=False,
                max_tokens=80,
                temperature=0.1  # 降低随机性，确保严格遵守规则
            )

            answer = response.choices[0].message.content.strip().replace("\n", " ")
            # 最终过滤：如果仍出现"AI""模型"等词，强制替换
            if "AI" in answer or "人工智能" in answer or "模型" in answer:
                answer = "我是糖尿病专科医生，专注于糖尿病健康咨询。"

            print(f"[Python服务] 成功调用GLM-4模型，问题：{question}", file=sys.stderr)
            return answer
        except Exception as e:
            print(f"[Python服务] 调用失败：{str(e)}", file=sys.stderr)
            return "糖尿病健康咨询服务暂时无法响应，请稍后再试。"

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("参数错误：需传入 [API_KEY] [用户问题]", file=sys.stderr)
        sys.exit(1)

    API_KEY = sys.argv[1]
    USER_QUESTION = ' '.join(sys.argv[2:])
    chatbot = DiabetesChatbot(API_KEY)
    print(chatbot.get_answer(USER_QUESTION))