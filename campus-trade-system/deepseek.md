�״ε��� API
DeepSeek API ʹ���� OpenAI ���ݵ� API ��ʽ��ͨ���޸����ã�������ʹ�� OpenAI SDK ������ DeepSeek API����ʹ���� OpenAI API ���ݵ������

����	��ֵ
base_url *	https://api.deepseek.com
api_key	���� API ��Կ
* ������ OpenAI ���ݿ��ǣ���Ҳ���Խ� ����Ϊ ��ʹ�ã���ע�⣬�˴� ��ģ�Ͱ汾�޹ء�base_urlhttps://api.deepseek.com/v1v1

* deepseek-chat ģ����ȫ������Ϊ DeepSeek-V3���ӿڲ��䡣 ͨ��ָ�� ���ɵ��� DeepSeek-V3��model='deepseek-chat'

* deepseek-reasoner �� DeepSeek �����Ƴ�������ģ�� DeepSeek-R1�� ͨ��ָ�� �����ɵ��� DeepSeek-R1��model='deepseek-reasoner'

���öԻ� API
�ڴ��� API key ֮�������ʹ�����������ű��������� DeepSeek API�� ����Ϊ����ʽ����������Խ� stream ����Ϊ true ��ʹ����ʽ�����

����
��
�ڵ�
// Please install OpenAI SDK first: `npm install openai`

import OpenAI from "openai";

const openai = new OpenAI({
        baseURL: 'https://api.deepseek.com',
        apiKey: '<DeepSeek API Key>'
});

async function main() {
  const completion = await openai.chat.completions.create({
    messages: [{ role: "system", content: "You are a helpful assistant." }],
    model: "deepseek-chat",
  });

  console.log(completion.choices[0].message.content);
}

main();