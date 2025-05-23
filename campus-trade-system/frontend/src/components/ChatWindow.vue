<template>
  <div class="chat-window">
    <div class="chat-header">
      <el-button link @click="goBack" class="back-btn">
        <el-icon><ArrowLeft /></el-icon> 返回
      </el-button>
      <div class="target-name">与 {{ targetUsername || '对方用户' }} 私信中</div>
    </div>

    <div class="chat-messages" ref="messagesBox" @scroll="handleScroll">
      <div v-if="loading && page === 0 && messages.length === 0" class="loading-container initial-load">
        <el-icon class="loading-icon is-loading"><Loading /></el-icon> 加载消息中...
      </div>
      <div class="load-more-trigger" v-if="canLoadMore && messages.length > 0">
        <el-button 
          type="text" 
          @click="loadMoreMessages" 
          :loading="loading && page > 0"
          class="load-more-btn"
        >
          {{ loading && page > 0 ? '加载中...' : '加载更早的消息' }}
        </el-button>
      </div>
      
      <div
        v-for="msg in messages"
        :key="msg.messageId"
        :class="['chat-bubble-row', msg.senderId === userStore.userId ? 'mine' : 'theirs']"
      >
        <el-avatar 
          v-if="msg.senderId !== userStore.userId" 
          :size="36" 
          :src="formatAvatarUrl(msg.senderAvatar)" 
          class="avatar theirs-avatar"
        />
        <div class="bubble-wrapper">
          <div class="sender-name" v-if="msg.senderId !== userStore.userId && showSenderName(msg)">
            {{ msg.senderName }}
          </div>
          <div class="bubble">
            <div class="bubble-content">{{ msg.content }}</div>
          </div>
        </div>
         <div class="time-and-avatar-container" :class="msg.senderId === userStore.userId ? 'mine' : 'theirs'">
          <div class="bubble-time">{{ formatDate(msg.sendTime) }}</div>
           <el-avatar 
            v-if="msg.senderId === userStore.userId" 
            :size="36" 
            :src="formatAvatarUrl(userStore.avatar)" 
            class="avatar mine-avatar"
          />
        </div>
      </div>
      <div v-if="messages.length === 0 && !loading" class="empty-message">
        没有消息记录，发送一条消息开始对话吧
      </div>
    </div>

    <div class="input-area">
      <el-input
        v-model="newMessage"
        type="textarea"
        :autosize="{ minRows: 1, maxRows: 4 }"
        placeholder="请输入消息..."
        @keyup.enter.exact.prevent="sendMessage"
        @focus="markConversationAsReadIfNecessary"
      />
      <el-button type="primary" @click="sendMessage" :loading="sending">发送</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Loading } from '@element-plus/icons-vue'
import { getMessagesApi, sendMessageApi } from '@/api/message'
import { getConversationDetailApi, markConversationAsReadApi as markConvAsReadApi } from '@/api/conversation'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const BACKEND_BASE_URL = 'http://localhost:8080'; // TODO: Move to .env or config

// DEBUG: Log userStore data once
console.log('[ChatWindow DEBUG] Initial userStore state:', 
  {
    userId: userStore.userId,
    username: userStore.username,
    avatar: userStore.avatar,
    isLoggedIn: userStore.isLoggedIn
  }
);

const conversationId = Number(route.query.conversationId)
const initialTargetUserId = Number(route.query.userId)
const targetUsername = ref(route.query.username || '')
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const formatAvatarUrl = (avatarPath) => {
  if (!avatarPath) {
    return defaultAvatar;
  }
  if (avatarPath.startsWith('http') || avatarPath.startsWith('//')) {
    return avatarPath;
  }
  const basePath = BACKEND_BASE_URL.endsWith('/') ? BACKEND_BASE_URL.slice(0, -1) : BACKEND_BASE_URL;
  const resourcePath = avatarPath.startsWith('/') ? avatarPath : `/${avatarPath}`;
  const fullUrl = `${basePath}${resourcePath}`;
  // console.log(`[ChatWindow DEBUG] Formatting avatar URL: ${avatarPath} -> ${fullUrl}`);
  return fullUrl;
};

const messages = ref([])
const newMessage = ref('')
const messagesBox = ref(null)
const loading = ref(false)
const sending = ref(false)
const page = ref(0)
const size = ref(20)
const hasMoreMessages = ref(true)

const canLoadMore = computed(() => hasMoreMessages.value && !loading.value)

onMounted(async () => {
  if (!conversationId && !initialTargetUserId) {
    ElMessage.error('会话信息不完整，无法加载聊天。')
    router.push('/messages') 
    return
  }
  
  if (conversationId) {
    await fetchConversationDetails()
    await markConversationAsReadIfNecessary()
  } else if (initialTargetUserId) {
    ElMessage.warning('当前实现需要会话ID来加载聊天。')
    return
  }
  
  await loadInitialMessages()
})

const fetchConversationDetails = async () => {
  if (!conversationId) return
  try {
    const response = await getConversationDetailApi(conversationId)
    if (response.data) {
      targetUsername.value = response.data.otherUsername
    }
  } catch (error) {
    console.error('获取会话详情失败:', error)
  }
}

const markConversationAsReadIfNecessary = async () => {
  if (conversationId && userStore.isLoggedIn) {
    try {
      await markConvAsReadApi(conversationId)
      console.log('会话已标记为已读:', conversationId)
    } catch (error) {
      console.error('标记会话为已读失败:', error)
    }
  }
}

const loadInitialMessages = async () => {
  if (!conversationId) return
  loading.value = true
  page.value = 0
  try {
    const response = await getMessagesApi(conversationId, page.value, size.value)
    let newMessages = []
    if (Array.isArray(response)) {
      newMessages = response
    } else if (response && Array.isArray(response.data)) {
      newMessages = response.data
    } else {
      console.warn('无法解析消息响应:', response)
      newMessages = []
    }
    messages.value = newMessages.reverse()
    hasMoreMessages.value = newMessages.length === size.value
    
    // DEBUG: Log loaded initial messages
    console.log('[ChatWindow DEBUG] Loaded initial messages (first 2 if available):', messages.value.slice(0, 2));
    messages.value.slice(0, 2).forEach((msg, idx) => {
      console.log(`[ChatWindow DEBUG] Initial Msg ${idx} - senderId: ${msg.senderId}, senderAvatar: ${msg.senderAvatar}, content: ${msg.content.substring(0,20)}`);
    });

    await nextTick()
    scrollToBottom()
  } catch (error) {
    ElMessage.error('获取消息失败：' + (error.response?.data?.message || error.message || '未知错误'))
    console.error('获取消息失败：', error)
  } finally {
    loading.value = false
  }
}

const loadMoreMessages = async () => {
  if (!canLoadMore.value || loading.value || !conversationId) return

  loading.value = true
  page.value++
  try {
    const response = await getMessagesApi(conversationId, page.value, size.value)
    // 修复：响应拦截器已处理，response可能直接就是消息数组
    let olderMessages = []
    if (Array.isArray(response)) {
      olderMessages = response.reverse()
    } else if (response && Array.isArray(response.data)) {
      olderMessages = response.data.reverse()
    } else {
      console.warn('无法解析更多消息响应:', response)
      olderMessages = []
    }

    // DEBUG: Log loaded older messages
    console.log('[ChatWindow DEBUG] Loaded older messages (first 2 if available):', olderMessages.slice(0, 2));
    olderMessages.slice(0, 2).forEach((msg, idx) => {
      console.log(`[ChatWindow DEBUG] Older Msg ${idx} - senderId: ${msg.senderId}, senderAvatar: ${msg.senderAvatar}, content: ${msg.content.substring(0,20)}`);
    });

    if (olderMessages.length > 0) {
      const messagesBoxEl = messagesBox.value
      const oldScrollHeight = messagesBoxEl.scrollHeight
      
      messages.value = [...olderMessages, ...messages.value]
      
      await nextTick()
      messagesBoxEl.scrollTop += (messagesBoxEl.scrollHeight - oldScrollHeight)
    }
    hasMoreMessages.value = olderMessages.length === size.value
  } catch (error) {
    ElMessage.error('加载更多消息失败：' + (error.response?.data?.message || error.message || '未知错误'))
    console.error('加载更多消息失败：', error)
    page.value--
  } finally {
    loading.value = false
  }
}

const scrollToBottom = async (behavior = 'smooth') => {
  await nextTick()
  if (messagesBox.value) {
    messagesBox.value.scrollTop = messagesBox.value.scrollHeight
  }
}

const sendMessage = async () => {
  if (!newMessage.value.trim() || !conversationId) return;
  
  const content = newMessage.value.trim();
  const tempMessageId = `temp-${Date.now()}`;

  const optimisticMessage = {
    messageId: tempMessageId,
    conversationId: conversationId,
    senderId: userStore.userId,
    senderName: userStore.username,
    senderAvatar: userStore.avatar,
    content: content,
    sendTime: new Date().toISOString(),
    isOptimistic: true
  };
  console.log('[ChatWindow DEBUG] Optimistic message being pushed:', optimisticMessage);

  messages.value.push(optimisticMessage);
  newMessage.value = '';
  await scrollToBottom('auto');

  sending.value = true;
  try {
    // 1. 'sendMessageApi' 预计返回的是实际的消息对象本身
    const actualMessageObjectFromServer = await sendMessageApi(conversationId, content); 

    // 2. 因此，sentMessageData 就是 actualMessageObjectFromServer，不再需要 .data
    const sentMessageData = actualMessageObjectFromServer; 

    // 这条日志现在应该能正确打印出消息对象了
    console.log('[ChatWindow DEBUG] Message data from server after send:', sentMessageData);

    // 3. 后续的校验逻辑保持不变
    if (sentMessageData && typeof sentMessageData === 'object' && sentMessageData.hasOwnProperty('messageId')) {
      const index = messages.value.findIndex(m => m.messageId === tempMessageId);
      if (index !== -1) {
        messages.value.splice(index, 1, sentMessageData);
      } else {
        console.warn('[ChatWindow WARN] Optimistic message not found by tempId, but server response was valid. Pushing server message.');
        messages.value.push(sentMessageData);
      }
      
      // 只有在成功更新消息列表后才滚动
      // （保持你原来的滚动逻辑或根据需要调整）
      if (messagesBox.value.scrollHeight - (messagesBox.value.scrollTop + messagesBox.value.clientHeight) <= 100) { // 简化了条件，如果用户接近底部则滚动
         await scrollToBottom('smooth');
      }
    } else {
      // 这里的日志可以更具体一些
      console.error('[ChatWindow ERROR] Invalid or unexpected message data structure received from sendMessageApi:', sentMessageData);
      ElMessage.error('消息发送成功，但更新列表时数据格式不正确。');
      // 移除乐观消息
      const index = messages.value.findIndex(m => m.messageId === tempMessageId && m.isOptimistic);
      if (index !== -1) {
        messages.value.splice(index, 1);
      }
    }
  } catch (error) {
    console.error('发送消息详细错误:', error);
    ElMessage.error('发送消息失败：' + (error.response?.data?.message || error.message || '未知错误'));
    
    const index = messages.value.findIndex(m => m.messageId === tempMessageId);
    if (index !== -1) {
      messages.value.splice(index, 1);
    }
    newMessage.value = content;
  } finally {
    sending.value = false;
  }
}

const goBack = () => {
  router.push('/messages')
}

const formatDate = (timestamp) => {
  if (!timestamp) return ''
  
  const date = new Date(timestamp)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const showSenderName = (msg) => {
  return false
}

const handleScroll = () => {
  // Implementation of handleScroll method
}
</script>

<style scoped>
.chat-window {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 120px);
  max-width: 700px;
  margin: 20px auto;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  background-color: #f9f9f9;
}

.chat-header {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  background-color: #fff;
  border-bottom: 1px solid #e0e0e0;
  min-height: 50px;
}

.back-btn {
  font-size: 16px;
  color: #409eff;
  margin-right: 10px;
}

.target-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.chat-messages {
  flex-grow: 1;
  overflow-y: auto;
  padding: 15px;
  display: flex;
  flex-direction: column;
}

.loading-container.initial-load {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #909399;
  font-size: 14px;
}
.loading-icon {
  margin-right: 8px;
}

.load-more-trigger {
  display: flex;
  justify-content: center;
  margin-bottom: 10px;
}
.load-more-btn {
  font-size: 13px;
  color: #409eff;
}

.chat-bubble-row {
  display: flex;
  margin-bottom: 15px;
  max-width: 100%;
}

.chat-bubble-row .avatar {
  flex-shrink: 0;
}

.chat-bubble-row.theirs {
  justify-content: flex-start;
}
.theirs .theirs-avatar {
  margin-right: 10px;
}
.theirs .bubble-wrapper {
  align-items: flex-start;
}
.theirs .bubble {
  background-color: #ffffff;
  border: 1px solid #e0e0e0;
}
.theirs .time-and-avatar-container {
  align-self: flex-start;
  margin-left: 10px;
}

.chat-bubble-row.mine {
  justify-content: flex-end;
}
.mine .mine-avatar {
  margin-left: 10px;
}
.mine .bubble-wrapper {
  align-items: flex-end;
}
.mine .bubble {
  background-color: #409eff;
  color: white;
}
.mine .time-and-avatar-container {
  align-self: flex-end;
  margin-right: 10px;
}
.mine .bubble-time {
  color: #5f6368;
}

.bubble-wrapper {
  display: flex;
  flex-direction: column;
  max-width: calc(100% - 60px);
}

.sender-name {
  font-size: 12px;
  color: #888;
  margin-bottom: 3px;
}
.mine .sender-name {
  display: none;
}

.bubble {
  padding: 10px 15px;
  border-radius: 18px;
  word-wrap: break-word;
  overflow-wrap: break-word;
  white-space: pre-wrap;
  max-width: 100%;
  font-size: 14px;
  line-height: 1.5;
}

.time-and-avatar-container {
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  margin: 0 5px;
}

.time-and-avatar-container.theirs {
  order: -1;
  margin-right: 10px;
}

.time-and-avatar-container.mine {
  order: 1;
  margin-left: 10px;
}

.bubble-time {
  font-size: 11px;
  color: #909399;
  margin-top: 5px;
  text-align: inherit;
}
.mine .bubble-wrapper .bubble-time {
  color: #a0cfff;
}
.theirs .bubble-wrapper .bubble-time {
}

.empty-message {
  text-align: center;
  color: #909399;
  padding: 20px;
  font-size: 14px;
  margin-top: auto;
  margin-bottom: auto;
}

.input-area {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  background-color: #fff;
  border-top: 1px solid #e0e0e0;
}

.input-area .el-textarea {
  margin-right: 10px;
}

.avatar {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
