<template>
  <div class="my-favorites">
    <h3>我的收藏</h3>
    <el-table :data="favorites" style="width: 100%" @row-click="goToDetail">
      <el-table-column prop="title" label="商品名称" />
      <el-table-column label="商品图片" width="120">
        <template #default="scope">
          <img :src="scope.row.image" alt="商品图片" class="fav-image" />
        </template>
      </el-table-column>
      <el-table-column prop="price" label="价格" />
      <el-table-column label="操作" width="100">
        <template #default="scope">
          <el-button
            type="danger"
            size="small"
            @click.stop="removeFavorite(scope.row)"
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
  padding: 20px;
}

.fav-image {
  max-width: 100px;
  max-height: 60px;
  border-radius: 4px;
  object-fit: cover;
}
</style>
