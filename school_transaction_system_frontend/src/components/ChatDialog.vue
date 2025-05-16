<template>
  <el-dialog v-model="visible" title="私信沟通" width="400px">
    <div class="chat-box">
      <div v-for="(msg, index) in messages" :key="index" class="message">
        <p><strong>{{ msg.from }}:</strong> {{ msg.text }}</p>
      </div>
    </div>
    <el-input
      v-model="inputMessage"
      placeholder="输入私信内容..."
      @keyup.enter="sendMessage"
    />
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
      <el-button type="primary" @click="sendMessage">发送</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'

const visible = ref(false)
const inputMessage = ref('')
const messages = ref([
  { from: '张三', text: '你好，请问这件商品在吗？' },
  { from: '我', text: '在的，你想要吗？' }
])

const sendMessage = () => {
  if (!inputMessage.value.trim()) return
  messages.value.push({ from: '我', text: inputMessage.value })
  inputMessage.value = ''
}
</script>

<style scoped>
.chat-box {
  max-height: 200px;
  overflow-y: auto;
  margin-bottom: 10px;
}
.message {
  margin: 5px 0;
}
</style>
