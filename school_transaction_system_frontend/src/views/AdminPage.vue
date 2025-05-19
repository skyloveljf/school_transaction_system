<template>
  <div class="admin-page">
    <div class="admin-header">
      <img src="https://img.icons8.com/color/96/administrator-male.png" alt="管理员头像" class="admin-avatar" />
      <div>
        <h1>管理员后台</h1>
        <p>欢迎回来，管理员！这里是你的管理面板。</p>
      </div>
    </div>
    <div class="admin-panels">
      <div class="admin-card user">
        <div class="icon"><span>👤</span></div>
        <div class="info">
          <div class="title">用户管理</div>
          <div class="desc">查看、删除用户</div>
        </div>
      </div>
      <div class="admin-card product">
        <div class="icon"><span>🛒</span></div>
        <div class="info">
          <div class="title">商品管理</div>
          <div class="desc">审核、下架、删除商品</div>
        </div>
      </div>
      <div class="admin-card comment">
        <div class="icon"><span>💬</span></div>
        <div class="info">
          <div class="title">评论管理</div>
          <div class="desc">查看、删除评论</div>
        </div>
      </div>
      <div class="admin-card stats">
        <div class="icon"><span>📊</span></div>
        <div class="info">
          <div class="title">数据统计</div>
          <div class="desc">平台数据一览</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 后续可以添加逻辑，比如获取商品列表、用户信息等
</script>

<style scoped>
.admin-page {
  padding: 40px 0;
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #e0e7ff 100%);
}
.admin-header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 40px;
  gap: 24px;
}
.admin-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: #fff;
  box-shadow: 0 2px 8px #dbeafe;
  border: 2px solid #6366f1;
}
.admin-header h1 {
  margin: 0;
  font-size: 2.2rem;
  color: #3730a3;
  font-weight: bold;
}
.admin-header p {
  margin: 8px 0 0 0;
  color: #6366f1;
  font-size: 1.1rem;
}
.admin-panels {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 32px;
}
.admin-card {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 4px 24px #c7d2fe55;
  width: 220px;
  height: 140px;
  display: flex;
  align-items: center;
  padding: 24px;
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
  border: 2px solid transparent;
}
.admin-card:hover {
  transform: translateY(-8px) scale(1.04);
  box-shadow: 0 8px 32px #6366f155;
  border-color: #6366f1;
}
.admin-card .icon {
  font-size: 2.5rem;
  margin-right: 18px;
}
.admin-card .title {
  font-size: 1.2rem;
  font-weight: bold;
  color: #3730a3;
}
.admin-card .desc {
  font-size: 0.98rem;
  color: #6366f1;
  margin-top: 6px;
}
</style>
