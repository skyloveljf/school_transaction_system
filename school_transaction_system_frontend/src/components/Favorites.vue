<template>
  <div class="my-favorites">
    <h3>我的收藏</h3>
    <el-table
      :data="favorites"
      class="favorites-table"
      stripe
      border
      style="width: 100%"
      @row-click="goToDetail"
      empty-text="暂无收藏，快去添加喜欢的商品吧~"
    >
      <el-table-column prop="title" label="商品名称" min-width="160" />
      <el-table-column label="商品图片" width="120">
        <template #default="scope">
          <img :src="scope.row.image" alt="商品图片" class="fav-image" />
        </template>
      </el-table-column>
      <el-table-column prop="price" label="价格" width="100">
        <template #default="scope">
          <span class="price">￥{{ scope.row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button
            type="danger"
            size="small"
            @click.stop="removeFavorite(scope.row)"
            class="remove-btn"
          >
            取消收藏
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'

const favorites = ref([])

const router = useRouter()

const fetchFavorites = () => {
  favorites.value = JSON.parse(localStorage.getItem('favorites') || '[]')
}

onMounted(fetchFavorites)

const removeFavorite = (item) => {
  ElMessageBox.confirm(
    `确定取消收藏 "${item.title}" 吗？`,
    '取消收藏',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    favorites.value = favorites.value.filter(fav => fav.id !== item.id)
    localStorage.setItem('favorites', JSON.stringify(favorites.value))
    ElMessage({
      type: 'success',
      message: '已取消收藏'
    })
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: '已取消操作'
    })
  })
}

const goToDetail = (row) => {
  router.push(`/product/${row.id}`)
}
</script>

<style scoped>
.my-favorites {
  padding: 32px 0 0 0;
  min-height: 420px;
  background: linear-gradient(120deg, #f8fafc 0%, #e0e7ff 100%);
  border-radius: 18px;
  box-shadow: 0 4px 24px #c7d2fe33;
  margin-bottom: 24px;
  animation: fadeInFav 0.8s;
}
@keyframes fadeInFav {
  from { opacity: 0; transform: translateY(30px);}
  to { opacity: 1; transform: none;}
}
h3 {
  text-align: center;
  font-size: 2rem;
  font-weight: 700;
  color: #6366f1;
  margin-bottom: 32px;
  letter-spacing: 2px;
  text-shadow: 0 2px 8px #e0e7ff44;
}
.favorites-table {
  background: rgba(255,255,255,0.98);
  border-radius: 14px;
  box-shadow: 0 2px 12px #a1c4fd22;
  margin-bottom: 24px;
}
.fav-image {
  max-width: 80px;
  max-height: 60px;
  border-radius: 8px;
  object-fit: cover;
  border: 1.5px solid #e0e7ff;
  box-shadow: 0 1px 6px #e0e7ff33;
  transition: transform 0.2s;
}
.fav-image:hover {
  transform: scale(1.12);
  box-shadow: 0 4px 16px #a1c4fd55;
}
.price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 1.1rem;
  letter-spacing: 1px;
}
.remove-btn {
  border-radius: 8px;
  font-weight: 600;
  letter-spacing: 1px;
  transition: background 0.2s;
}
.remove-btn:hover {
  background: linear-gradient(90deg, #f56c6c 0%, #fbc2eb 100%);
  color: #fff;
}
</style>
