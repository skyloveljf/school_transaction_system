<template>
  <div class="message-list">
    <div class="header-bar">
      <el-button type="text" icon="el-icon-arrow-left" @click="goBack" class="back-btn">返回</el-button>
      <h2>📨 私信中心</h2>
    </div>
    <el-card
      v-for="conv in conversations"
      :key="conv.userId"
      class="message-card"
      @click="goToChat(conv)"
      :hoverable="true"
    >
      <div class="card-content">
        <el-avatar :src="conv.avatar" size="48" class="avatar" />
        <div class="text">
          <div class="name">{{ conv.username }}</div>
          <div class="preview" :title="conv.lastMessage">{{ conv.lastMessage }}</div>
        </div>
        <el-badge
          v-if="conv.unreadCount > 0"
          :value="conv.unreadCount"
          class="unread-badge"
          effect="dark"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref } from 'vue'

const router = useRouter()

const conversations = ref([
  {
    userId: 2,
    username: '张三',
    avatar: 'https://img.keaitupian.cn/uploads/upimg/1597372353577123.jpg',
    lastMessage: '你好，商品还在吗？',
    unreadCount: 2,
  },
  {
    userId: 3,
    username: '李四',
    avatar: 'https://img.keaitupian.cn/uploads/upimg/1597372353577899.jpg',
    lastMessage: '收到啦，谢谢！',
    unreadCount: 0,
  },
])

const goToChat = (conv) => {
  router.push({ path: '/chat', query: { userId: conv.userId, username: conv.username } })
}

const goBack = () => {
  router.push('/userhome')
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
