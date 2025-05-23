<template>
    <el-dialog v-model="visible" :title="`与 ${otherUsername} 私信沟通`" width="400px">
      <div class="chat-box">
        <div v-if="loading" class="loading">
          <el-icon class="loading-icon"><Loading /></el-icon> 加载中...
        </div>
        <div v-else-if="messages.length === 0" class="no-messages">暂无消息记录</div>
        <template v-else>
          <div 
            v-for="msg in sortedMessages" 
            :key="msg.messageId" 
            :class="['message-row', msg.senderId === userStore.userId ? 'mine' : 'theirs']"
          >
            <el-avatar 
              v-if="msg.senderId !== userStore.userId" 
              :size="32" 
              :src="formatAvatarUrl(msg.senderAvatar)" 
              class="avatar theirs-avatar"
            />
            <div class="message-bubble-wrapper">
              <div class="message-bubble">
                <p class="message-text">{{ msg.content }}</p>
              </div>
              <span class="message-time">{{ formatTime(msg.sendTime) }}</span>
            </div>
            <el-avatar 
              v-if="msg.senderId === userStore.userId" 
              :size="32" 
              :src="formatAvatarUrl(userStore.avatar)" 
              class="avatar mine-avatar"
            />
          </div>
        </template>
      </div>
      
      <div class="input-wrapper">
        <el-input
          v-model="inputMessage"
          placeholder="输入私信内容..."
          @keyup.enter.exact.prevent="sendMessage"
          class="message-input"
          ref="inputEl"
        />
      </div>
      
      <template #footer>
        <el-button @click="close" :disabled="sending">关闭</el-button>
        <el-button 
          type="primary" 
          @click.prevent="sendMessage" 
          :loading="sending"
          :disabled="!inputMessage.trim()"
        >
          {{ sending ? '发送中...' : '发送' }}
        </el-button>
      </template>
    </el-dialog>
  </template>
  
  <script setup>
  import { ref, computed, defineProps, defineEmits, watch, onMounted } from 'vue'
  import { ElMessage } from 'element-plus'
  import { Loading } from '@element-plus/icons-vue'
  import { getMessagesApi, sendDirectMessageApi } from '@/api/message'
  import { createConversationApi } from '@/api/conversation'
  import { useUserStore } from '@/store/user'
  import axios from 'axios'
  import authService from '@/services/authService'
  
  const props = defineProps({
    show: Boolean,
    userId: Number,
    username: String
  })
  
  const emit = defineEmits(['update:show', 'close', 'sent-message'])
  const userStore = useUserStore()
  
  const BACKEND_BASE_URL = 'http://localhost:8080'; // TODO: Move to .env or config
  
  const visible = computed({
    get: () => props.show !== undefined ? props.show : true,
    set: (value) => {
      emit('update:show', value)
      if (!value) {
        emit('close')
      }
    }
  })
  
  const conversationId = ref(null)
  const otherUsername = computed(() => props.username || '对方用户')
  const inputMessage = ref('')
  const messages = ref([])
  const loading = ref(false)
  const sending = ref(false)
  const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
  
  // 对消息进行排序，确保按时间顺序显示（旧消息在上，新消息在下）
  const sortedMessages = computed(() => {
    // 为保持稳定性，先对messages进行浅拷贝，避免直接修改原数组
    return [...messages.value].sort((a, b) => {
      const timeA = new Date(a.sendTime).getTime();
      const timeB = new Date(b.sendTime).getTime();
      return timeA - timeB; // 升序排列
    });
  });
  
  // 格式化头像URL
  const formatAvatarUrl = (avatarPath) => {
    if (!avatarPath) {
      return defaultAvatar;
    }
    if (avatarPath.startsWith('http') || avatarPath.startsWith('//')) {
      return avatarPath;
    }
    const basePath = BACKEND_BASE_URL.endsWith('/') ? BACKEND_BASE_URL.slice(0, -1) : BACKEND_BASE_URL;
    const resourcePath = avatarPath.startsWith('/') ? avatarPath : `/${avatarPath}`;
    return `${basePath}${resourcePath}`;
  };
  
  // 监听对话框显示状态
  watch(() => props.show, async (newVal) => {
    if (newVal && props.userId) {
      console.log("对话框显示，正在准备创建会话...");
      // 先尝试创建/获取会话
      await createOrGetConversation();
      // 然后加载消息
      if (conversationId.value) {
        await loadMessages();
      }
    }
  })
  
  // 检查用户登录状态
  const checkUserLoginStatus = () => {
    if (!authService.isLoggedIn()) {
      ElMessage.error('请先登录后再发送私信');
      return false;
    }
    return true;
  }
  
  const createOrGetConversation = async () => {
    if (!props.userId) return;
    if (!checkUserLoginStatus()) return;
    
    try {
      loading.value = true;
      console.log(`尝试创建/获取与用户${props.userId}的会话`);
      const response = await createConversationApi(props.userId);
      console.log("创建会话响应:", response);
      
      // 修复：因为响应拦截器已处理，response直接就是会话对象
      if (response && response.conversationId) {
        conversationId.value = response.conversationId;
        console.log("✅ 从直接响应获取会话ID:", conversationId.value);
      } 
      // 兼容旧格式，以防响应拦截器行为改变  
      else if (response && response.data) {
        // 情况1: {data: {conversationId: xxx}}
        if (response.data.conversationId) {
          conversationId.value = response.data.conversationId;
          console.log("✅ 从data.conversationId获取会话ID:", conversationId.value);
        } 
        // 情况2: {data: {data: {conversationId: xxx}}}
        else if (response.data.data && response.data.data.conversationId) {
          conversationId.value = response.data.data.conversationId;
          console.log("✅ 从data.data.conversationId获取会话ID:", conversationId.value);
        }
        // 情况3: {data: {id: xxx}}
        else if (response.data.id) {
          conversationId.value = response.data.id;
          console.log("✅ 从data.id获取会话ID:", conversationId.value);
        }
      } 
      // 其他可能的格式
      else if (response && response.id) {
        conversationId.value = response.id;
        console.log("✅ 从response.id获取会话ID:", conversationId.value);
      } else {
        console.warn("❌ 无法从响应中解析会话ID:", response);
      }
      
      console.log("🔍 最终获取会话ID:", conversationId.value);
    } catch (error) {
      console.error('创建/获取会话失败:', error);
      if (error.response && error.response.status === 401) {
        ElMessage.error('未登录或登录已过期，请重新登录');
      } else {
        ElMessage.error('无法建立对话连接: ' + (error.message || '未知错误'));
      }
    } finally {
      loading.value = false;
    }
  }
  
  const loadMessages = async () => {
    if (!conversationId.value) {
      console.log("没有会话ID，无法加载消息");
      return;
    }
    
    try {
      loading.value = true;
      console.log(`加载会话${conversationId.value}的消息`);
      const response = await getMessagesApi(conversationId.value, 0, 10);
      console.log("加载消息响应:", response);
      
      // 修复：因为响应拦截器已处理，response可能直接就是消息数组
      if (Array.isArray(response)) {
        messages.value = response;
        console.log("✅ 从直接数组响应加载消息:", messages.value.length);
      } else if (response && Array.isArray(response.data)) {
        messages.value = response.data;
        console.log("✅ 从response.data加载消息:", messages.value.length);
      } else if (response && response.data && Array.isArray(response.data.data)) {
        messages.value = response.data.data;
        console.log("✅ 从response.data.data加载消息:", messages.value.length);
      } else {
        console.warn("❌ 无法从响应中解析消息数组:", response);
        messages.value = [];
      }
      
      console.log(`🔍 最终加载了${messages.value.length}条消息`);
    } catch (error) {
      console.error('加载消息失败:', error);
      if (error.response && error.response.status === 401) {
        ElMessage.error('未登录或登录已过期，请重新登录');
      }
    } finally {
      loading.value = false;
    }
  }
  
  const sendMessage = async () => {
    if (!inputMessage.value.trim() || !props.userId) {
      console.log('消息为空或没有用户ID，不发送消息');
      return;
    }
    
    if (!checkUserLoginStatus()) return;
    
    const content = inputMessage.value.trim();
    console.log('准备发送消息:', content, '给用户:', props.userId);
    
    // 先检查是否有会话ID
    if (!conversationId.value) {
      console.log('没有会话ID，先创建会话');
      await createOrGetConversation();
      if (!conversationId.value) {
        ElMessage.error('无法创建会话，请稍后再试');
        return;
      }
    }
    
    // 直接在界面上显示消息，不等待API响应
    const tempMsg = {
      messageId: 'temp-' + Date.now(),
      content: content,
      senderId: userStore.userId,
      senderName: userStore.username,
      senderAvatar: userStore.avatar || defaultAvatar,
      sendTime: new Date(),
      isRead: false
    };
    
    // 把消息添加到本地显示
    messages.value.push(tempMsg);
    
    // 先清空输入框，给用户即时反馈
    const originalMessage = inputMessage.value;
    inputMessage.value = '';
    
    try {
      sending.value = true;
      console.log(`发送消息到会话: ${conversationId.value}`);
      
      // 使用封装的API函数发送消息
      const response = await sendDirectMessageApi(conversationId.value, content);
      console.log('发送消息响应:', response);
      
      ElMessage.success('消息发送成功!');
      
      // 刷新消息列表
      await loadMessages();
      
      // 通知父组件
      emit('sent-message', { receiverId: props.userId, content });
      
    } catch (error) {
      console.error('发送消息失败:', error);
      
      // 显示详细错误
      if (error.response) {
        console.error('错误状态:', error.response.status);
        console.error('错误数据:', error.response.data);
        
        if (error.response.status === 401) {
          ElMessage.error('未登录或登录已过期，请重新登录');
        } else {
          ElMessage.error('发送消息失败: ' + (error.response.data?.message || '服务器错误'));
        }
      } else {
        ElMessage.error('发送消息失败：' + (error.message || '网络错误'));
      }
      
      // 移除临时消息
      messages.value = messages.value.filter(msg => msg.messageId !== tempMsg.messageId);
      
      // 恢复消息内容到输入框
      inputMessage.value = originalMessage;
      
    } finally {
      sending.value = false;
    }
  };
  
  const close = () => {
    visible.value = false;
    inputMessage.value = '';
  }
  
  const formatTime = (timestamp) => {
    if (!timestamp) return '';
    const date = new Date(timestamp);
    return date.toLocaleString('zh-CN', { hour: '2-digit', minute: '2-digit' });
  }
  
  // 加载时打印组件信息，帮助调试
  onMounted(() => {
    console.log('ChatDialog组件已加载, props:', props);
    console.log('[ChatDialog DEBUG] userStore状态:', {
      userId: userStore.userId,
      username: userStore.username,
      avatar: userStore.avatar,
      isLoggedIn: userStore.isLoggedIn
    });
    
    // 如果组件加载时show为true，则自动初始化对话
    if (props.show && props.userId) {
      console.log('组件加载时show为true，自动初始化对话');
      createOrGetConversation().then(() => {
        if (conversationId.value) {
          loadMessages();
        }
      });
    }
  })
  </script>
  
  <style scoped>
  .chat-box {
    max-height: 300px;
    overflow-y: auto;
    margin-bottom: 15px;
    padding: 10px;
    background-color: #f9f9f9;
    border-radius: 4px;
  }
  
  .message-row {
    margin: 12px 0;
    display: flex;
    align-items: flex-start;
  }
  
  .message-row.mine {
    flex-direction: row-reverse;
  }
  
  .avatar {
    flex-shrink: 0;
  }
  
  .theirs-avatar {
    margin-right: 8px;
  }
  
  .mine-avatar {
    margin-left: 8px;
  }
  
  .message-bubble-wrapper {
    display: flex;
    flex-direction: column;
    max-width: 70%;
  }
  
  .mine .message-bubble-wrapper {
    align-items: flex-end;
  }
  
  .theirs .message-bubble-wrapper {
    align-items: flex-start;
  }
  
  .message-bubble {
    padding: 8px 12px;
    border-radius: 18px;
    max-width: 100%;
  }
  
  .theirs .message-bubble {
    background-color: #ffffff;
    border: 1px solid #e0e0e0;
    text-align: left;
  }
  
  .mine .message-bubble {
    background-color: #409eff;
    color: white;
    text-align: right;
  }
  
  .message-text {
    margin: 0;
    word-break: break-word;
    line-height: 1.4;
  }
  
  .message-time {
    font-size: 11px;
    color: #999;
    margin-top: 4px;
  }
  
  .mine .message-time {
    color: #a0cfff;
  }
  
  .loading, .no-messages {
    text-align: center;
    padding: 20px;
    color: #909399;
    font-style: italic;
  }
  
  .loading-icon {
    animation: rotating 2s linear infinite;
  }
  
  @keyframes rotating {
    from {
      transform: rotate(0deg);
    }
    to {
      transform: rotate(360deg);
    }
  }
  
  .input-wrapper {
    margin-top: 15px;
    position: relative;
  }
  
  .message-input {
    width: 100%;
  }
  </style>