<template>
  <div class="chat-window">
    <div class="chat-header">
      <el-button link @click="goBack" class="back-btn">
        <el-icon><ArrowLeft /></el-icon> 返回
      </el-button>
      <div class="target-name">与 {{ targetUsername }} 私信中</div>
    </div>

    <div class="chat-messages" ref="messagesBox">
      <div
        v-for="msg in messages"
        :key="msg.id"
        :class="['chat-bubble', msg.fromUserId === currentUserId ? 'mine' : 'theirs']"
      >
        <div class="bubble-content">{{ msg.content }}</div>
        <div class="bubble-time">{{ formatDate(msg.timestamp) }}</div>
      </div>
    </div>

    <div class="input-area">
      <el-input
        v-model="newMessage"
        type="textarea"
        :autosize="{ minRows: 2, maxRows: 4 }"
        placeholder="请输入消息..."
        @keyup.enter="sendMessage"
        class="chat-input"
      />
      <el-button type="primary" @click="sendMessage" class="send-btn">发送</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { mockMessages } from '../api/messages'

const route = useRoute()
const router = useRouter()

const targetUserId = Number(route.query.userId)
const targetUsername = route.query.username || '对方用户'
const currentUserId = 1

const messages = ref([])
const newMessage = ref('')
const messagesBox = ref(null)

onMounted(() => {
  messages.value = mockMessages.filter(
    m =>
      (m.fromUserId === currentUserId && m.toUserId === targetUserId) ||
      (m.fromUserId === targetUserId && m.toUserId === currentUserId)
  )
  scrollToBottom()
})

const scrollToBottom = async () => {
  await nextTick()
  if (messagesBox.value) {
    messagesBox.value.scrollTop = messagesBox.value.scrollHeight
  }
}

const sendMessage = () => {
  if (!newMessage.value.trim()) return

  const message = {
    id: Date.now(),
    fromUserId: currentUserId,
    toUserId: targetUserId,
    content: newMessage.value.trim(),
    timestamp: new Date().toISOString()
  }

  messages.value.push(message)
  newMessage.value = ''
  ElMessage.success('消息已发送')
  scrollToBottom()
}

const goBack = () => {
  router.back()
}

const formatDate = (timestamp) => {
  return new Date(timestamp).toLocaleString()
}
</script>

<style scoped>
.chat-window {
  max-width: 800px;
  margin: 40px auto;
  background: linear-gradient(120deg, #f8fafc 0%, #e0e7ff 100%);
  border-radius: 18px;
  padding: 0 0 20px 0;
  box-shadow: 0 8px 32px #c7d2fe33;
  display: flex;
  flex-direction: column;
  height: 80vh;
  animation: fadeInChat 0.8s;
}
@keyframes fadeInChat {
  from { opacity: 0; transform: translateY(30px);}
  to { opacity: 1; transform: none;}
}

.chat-header {
  display: flex;
  align-items: center;
  padding: 24px 32px 12px 32px;
  font-size: 18px;
  font-weight: 600;
  color: #6366f1;
  border-bottom: 1.5px solid #e0e7ff;
  border-top-left-radius: 18px;
  border-top-right-radius: 18px;
  background: rgba(255,255,255,0.95);
  box-shadow: 0 2px 8px #e0e7ff22;
}

.back-btn {
  margin-right: auto;
  font-size: 15px;
  color: #409EFF;
  font-weight: 600;
  letter-spacing: 1px;
  transition: color 0.2s;
}
.back-btn:hover {
  color: #6366f1;
}

.target-name {
  margin-left: auto;
  font-size: 1.1rem;
  color: #6366f1;
  font-weight: 700;
  letter-spacing: 1px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 32px 36px 18px 36px;
  border-radius: 8px;
  background: linear-gradient(120deg, #f8fafc 60%, #e0e7ff 100%);
  display: flex;
  flex-direction: column;
  gap: 2px;
  scroll-behavior: smooth;
}

.chat-bubble {
  max-width: 70%;
  margin: 10px 0;
  padding: 12px 18px;
  border-radius: 18px;
  position: relative;
  word-break: break-word;
  font-size: 15px;
  box-shadow: 0 2px 12px #e0e7ff33;
  transition: background 0.2s, box-shadow 0.2s;
  animation: bubblePop 0.4s;
}
@keyframes bubblePop {
  from { opacity: 0; transform: scale(0.97);}
  to { opacity: 1; transform: scale(1);}
}
.mine {
  background: linear-gradient(90deg, #daf1ff 60%, #a1c4fd 100%);
  align-self: flex-end;
  border-top-right-radius: 4px;
  margin-left: auto;
  text-align: right;
  color: #3730a3;
}
.theirs {
  background: linear-gradient(90deg, #eeeeee 60%, #f8fafc 100%);
  align-self: flex-start;
  border-top-left-radius: 4px;
  margin-right: auto;
  text-align: left;
  color: #444;
}

.bubble-content {
  font-size: 15px;
  line-height: 1.7;
}

.bubble-time {
  font-size: 12px;
  color: #b4b8d1;
  margin-top: 4px;
  text-align: right;
}

.input-area {
  display: flex;
  gap: 12px;
  margin: 0 32px 0 32px;
  padding-top: 10px;
  border-top: 1.5px solid #e0e7ff;
  background: rgba(255,255,255,0.95);
  border-bottom-left-radius: 18px;
  border-bottom-right-radius: 18px;
  align-items: flex-end;
}
.chat-input {
  flex: 1;
  border-radius: 10px;
  background: #f3f4f6;
  border: 1.5px solid #e0e7ff;
  transition: border-color 0.2s;
}
.chat-input:focus-within {
  border-color: #6366f1;
}
.send-btn {
  font-weight: 700;
  border-radius: 10px;
  background: linear-gradient(90deg, #a1c4fd 0%, #c2e9fb 100%);
  border: none;
  color: #6366f1;
  transition: background 0.2s;
  padding: 0 22px;
  height: 40px;
}
.send-btn:hover {
  background: linear-gradient(90deg, #6366f1 0%, #a1c4fd 100%);
  color: #fff;
}
</style>
