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
  padding: 0 15px 30px 15px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #2c3e50;
  background: linear-gradient(120deg, #f8fafc 0%, #e0e7ff 100%);
  border-radius: 18px;
  box-shadow: 0 8px 32px #c7d2fe33;
  animation: fadeInMsgList 0.8s;
}
@keyframes fadeInMsgList {
  from { opacity: 0; transform: translateY(30px);}
  to { opacity: 1; transform: none;}
}

.header-bar {
  display: flex;
  align-items: center;
  margin-bottom: 25px;
  gap: 10px;
  padding-top: 28px;
  padding-bottom: 10px;
}

.back-btn {
  font-weight: 600;
  color: #409eff;
  user-select: none;
  padding: 0;
  min-width: 60px;
  font-size: 16px;
  transition: color 0.2s;
}
.back-btn:hover {
  color: #6366f1;
}

h2 {
  font-weight: 700;
  font-size: 28px;
  user-select: none;
  color: #6366f1;
  flex-grow: 1;
  text-align: center;
  margin: 0;
  letter-spacing: 2px;
  text-shadow: 0 2px 8px #e0e7ff44;
}

.message-card {
  margin-bottom: 18px;
  cursor: pointer;
  border-radius: 14px;
  box-shadow: 0 2px 12px #a1c4fd22;
  transition: box-shadow 0.3s, transform 0.2s;
  background-color: rgba(255,255,255,0.98);
  border: 1.5px solid #e0e7ff;
  animation: cardPop 0.5s;
}
@keyframes cardPop {
  from { transform: scale(0.97);}
  to { transform: scale(1);}
}
.message-card:hover {
  box-shadow: 0 6px 18px #a1c4fd55;
  transform: translateY(-3px) scale(1.03);
  border-color: #6366f1;
}

.card-content {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 18px 24px;
}

.avatar {
  border: 2px solid #a1c4fd;
  transition: box-shadow 0.3s;
  background: #fff;
}
.avatar:hover {
  box-shadow: 0 0 12px 2px #a1c4fd;
}

.text {
  flex: 1;
  overflow: hidden;
}

.name {
  font-weight: 700;
  font-size: 18px;
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #6366f1;
  letter-spacing: 1px;
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
  --el-badge-bg-color: #f56c6c;
  --el-badge-color: #fff;
  font-size: 14px;
}
</style>
