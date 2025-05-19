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
      />
      <el-button type="primary" @click="sendMessage">发送</el-button>
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
  background-color: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  height: 80vh;
}

.chat-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  font-size: 18px;
  font-weight: 500;
  color: #333;
}

.back-btn {
  margin-right: auto;
  font-size: 14px;
  color: #409EFF;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  border-radius: 8px;
  background-color: #f9f9f9;
}

.chat-bubble {
  max-width: 70%;
  margin: 8px 0;
  padding: 10px 14px;
  border-radius: 16px;
  position: relative;
  word-break: break-word;
}

.mine {
  background-color: #daf1ff;
  align-self: flex-end;
  border-top-right-radius: 4px;
  margin-left: auto;
  text-align: right;
}

.theirs {
  background-color: #eeeeee;
  align-self: flex-start;
  border-top-left-radius: 4px;
  margin-right: auto;
  text-align: left;
}

.bubble-content {
  font-size: 14px;
}

.bubble-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.input-area {
  display: flex;
  gap: 10px;
  margin-top: 12px;
}
</style>
