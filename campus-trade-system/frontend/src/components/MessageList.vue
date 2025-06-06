<template>
  <div class="message-list">
    <div class="header-bar">
      <el-button link icon="el-icon-arrow-left" @click="goBack" class="back-btn">返回</el-button>
      <h2>📨 私信中心</h2>
    </div>
    
    <!-- 未登录时显示提示 -->
    <el-card v-if="!isLoggedIn" class="auth-card">
      <el-empty description="请先登录后查看私信" />
      <div style="text-align: center; margin-top: 20px;">
        <el-button type="primary" @click="goToLogin">立即登录</el-button>
      </div>
    </el-card>
    
    <!-- 已登录后显示内容 -->
    <div v-else>
      <el-empty v-if="loading" description="加载中..." />
      <el-empty v-else-if="conversations.length === 0" description="暂无会话" />
      <el-card
        v-else
        v-for="conv in conversations"
        :key="conv.conversationId"
        class="message-card"
        @click="goToChat(conv)"
        :hoverable="true"
      >
        <div class="card-content">
          <el-avatar :src="formatAvatarUrl(conv.otherUserAvatar)" :size="48" class="avatar" />
          <div class="text">
            <div class="name">{{ conv.otherUsername }}</div>
            <div class="preview" :title="conv.lastMessage">{{ conv.lastMessage }}</div>
          </div>
          <div class="time">{{ formatTime(conv.lastMessageTime) }}</div>
          <el-badge
            v-if="conv.unreadCount > 0"
            :value="conv.unreadCount"
            class="unread-badge"
            effect="dark"
          />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getConversationsApi } from '@/api/conversation'
import authService from '@/services/authService'

const BACKEND_BASE_URL = 'http://localhost:8080'; // TODO: Move to .env or config
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';

const router = useRouter()
const conversations = ref([])
const loading = ref(true)

// 检查用户是否已登录
const isLoggedIn = computed(() => {
  return authService.isLoggedIn()
})

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

onMounted(async () => {
  if (!isLoggedIn.value) {
    loading.value = false;
    console.log('用户未登录');
    return;
  }
  
  try {
    loading.value = true
    console.log('开始获取会话列表...');
    const response = await getConversationsApi()
    console.log('会话列表响应:', response);
    
    // 修复：因为响应拦截器已处理，response可能直接就是会话数组
    if (Array.isArray(response)) {
      conversations.value = response;
      console.log("✅ 从直接数组响应加载会话:", conversations.value.length);
    } else if (response && Array.isArray(response.data)) {
      conversations.value = response.data;
      console.log("✅ 从response.data加载会话:", conversations.value.length);
    } else {
      console.warn("❌ 无法从响应中解析会话数组:", response);
      conversations.value = [];
    }
    
    console.log(`🔍 最终加载了${conversations.value.length}个会话`);
  } catch (error) {
    console.error('获取会话列表失败：', error)
    
    if (error.response && error.response.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
    } else {
      ElMessage.error('获取会话列表失败：' + (error.message || '未知错误'))
    }
  } finally {
    loading.value = false
  }
})

const goToChat = (conv) => {
  router.push({ 
    path: '/chat', 
    query: { 
      conversationId: conv.conversationId,
      userId: conv.otherUserId, 
      username: conv.otherUsername 
    } 
  })
}

const goBack = () => {
  router.push('/userhome')
}

const goToLogin = () => {
  router.push('/login')
}

// 格式化时间显示
const formatTime = (timestamp) => {
  if (!timestamp) return ''
  
  const date = new Date(timestamp)
  const now = new Date()
  
  // 如果是今天，只显示时间
  if (date.toDateString() === now.toDateString()) {
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  
  // 如果是昨天，显示"昨天"
  const yesterday = new Date(now)
  yesterday.setDate(now.getDate() - 1)
  if (date.toDateString() === yesterday.toDateString()) {
    return '昨天'
  }
  
  // 如果是今年，显示月日
  if (date.getFullYear() === now.getFullYear()) {
    return `${date.getMonth() + 1}月${date.getDate()}日`
  }
  
  // 其他情况显示年月日
  return `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()}`
}
</script>

<style scoped>
.message-list {
  max-width: 720px;
  margin: 40px auto;
  padding: 0 15px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #2c3e50;
}

.header-bar {
  display: flex;
  align-items: center;
  margin-bottom: 25px;
  gap: 10px;
}

.back-btn {
  font-weight: 600;
  color: #409eff;
  user-select: none;
  padding: 0;
  min-width: 60px;
}

h2 {
  font-weight: 700;
  font-size: 28px;
  user-select: none;
  color: #409eff;
  flex-grow: 1;
  text-align: center;
  margin: 0;
}

.auth-card {
  padding: 30px;
  margin: 20px 0;
  text-align: center;
}

.message-card {
  margin-bottom: 14px;
  cursor: pointer;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgb(64 158 255 / 0.15);
  transition: box-shadow 0.3s ease, transform 0.2s ease;
  background-color: white;
}

.message-card:hover {
  box-shadow: 0 6px 18px rgb(64 158 255 / 0.3);
  transform: translateY(-3px);
}

.card-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
}

.avatar {
  border: 2px solid #409eff;
  transition: box-shadow 0.3s ease;
}
.avatar:hover {
  box-shadow: 0 0 8px 2px #409eff;
}

.text {
  flex: 1;
  overflow: hidden;
}

.name {
  font-weight: 600;
  font-size: 18px;
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.preview {
  font-size: 14px;
  color: #7a8a99;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.unread-badge {
  margin-left: auto;
  font-weight: 600;
  user-select: none;
}
</style>
