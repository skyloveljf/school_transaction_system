首次调用 API
DeepSeek API 使用与 OpenAI 兼容的 API 格式，通过修改配置，您可以使用 OpenAI SDK 来访问 DeepSeek API，或使用与 OpenAI API 兼容的软件。

参数	价值
base_url *	https://api.deepseek.com
api_key	申请 API 密钥
* 出于与 OpenAI 兼容考虑，您也可以将 设置为 来使用，但注意，此处 与模型版本无关。base_urlhttps://api.deepseek.com/v1v1

* deepseek-chat 模型已全面升级为 DeepSeek-V3，接口不变。 通过指定 即可调用 DeepSeek-V3。model='deepseek-chat'

* deepseek-reasoner 是 DeepSeek 最新推出的推理模型 DeepSeek-R1。 通过指定 ，即可调用 DeepSeek-R1。model='deepseek-reasoner'

调用对话 API
在创建 API key 之后，你可以使用以下样例脚本的来访问 DeepSeek API。 样例为非流式输出，您可以将 stream 设置为 true 来使用流式输出。

卷曲
蟒
节点
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