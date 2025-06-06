<template>
  <div class="my-favorites">
    <h3>我的收藏</h3>
    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="3" animated />
    </div>
    <div v-else-if="error" class="error-state">
      <el-alert title="加载收藏列表失败" type="error" :description="error" show-icon :closable="false" />
    </div>
    <el-empty v-else-if="!favorites || favorites.length === 0" description="您还没有收藏任何商品" />
    <el-table v-else :data="favorites" style="width: 100%" @row-click="handleRowClick">
      <el-table-column label="商品图片" width="120">
        <template #default="scope">
          <img 
            :src="getImageUrl(scope.row.productImageUrl)" 
            alt="商品图片" 
            class="fav-image" 
            @error="onImageError"
          />
        </template>
      </el-table-column>
      <el-table-column label="商品名称">
        <template #default="scope">
          {{ scope.row.productName || '未知商品' }}
        </template>
      </el-table-column>
      <el-table-column label="价格 (元)" width="120">
        <template #default="scope">
          {{ scope.row.productPrice !== null ? '￥' + scope.row.productPrice.toFixed(2) : 'N/A' }}
        </template>
      </el-table-column>
      <el-table-column label="收藏时间" width="180">
        <template #default="scope">
          {{ formatDateTime(scope.row.favoriteTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="scope">
          <el-button
            type="danger"
            size="small"
            @click.stop="confirmRemoveFavorite(scope.row)"
            :loading="scope.row.removing"
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
import { ElMessageBox, ElMessage, ElTable, ElTableColumn, ElButton, ElEmpty, ElSkeleton, ElAlert } from 'element-plus'
import { getMyFavoritesApi, removeFavoriteByFavoriteIdApi } from '../api/favorite'
import authService from '../services/authService'
import { getImageUrl } from '../utils/imageHelper'

const favorites = ref([])
const loading = ref(false)
const error = ref(null)
const router = useRouter()

const fetchFavorites = async () => {
  loading.value = true
  error.value = null
  try {
    const data = await getMyFavoritesApi()
    if (data.length === 0 && !authService.isLoggedIn()) {
        error.value = '请先登录查看您的收藏。';
    } else if (data.length === 0 && authService.isLoggedIn()) {
        // User is logged in but has no favorites, ElEmpty will handle this
    }
    favorites.value = data.map(fav => ({ ...fav, removing: false }))
  } catch (err) {
    console.error("Failed to fetch favorites (this catch should ideally not be reached if API handles errors):", err)
    error.value = err.message || '无法加载收藏列表，请稍后重试。'
  } finally {
    loading.value = false
  }
}

onMounted(fetchFavorites)

const confirmRemoveFavorite = (item) => {
  if (!item || typeof item.id === 'undefined' || item.id === null) {
    console.error('Remove favorite attempt with invalid item:', item);
    ElMessage.error("收藏项信息不完整或ID无效，无法取消收藏。");
    return;
  }

  console.log('准备删除收藏项:', item);

  ElMessageBox.confirm(
    `确定取消收藏 "${item.productName || '该商品'}" 吗？`,
    '取消收藏',
    {
      confirmButtonText: '确定',
      cancelButtonText: '我再想想',
      type: 'warning'
    }
  ).then(async () => {
    item.removing = true
    try {
      await removeFavoriteByFavoriteIdApi(item.id)
      ElMessage.success('已成功取消收藏')
      favorites.value = favorites.value.filter(fav => fav.id !== item.id)
    } catch (err) {
      console.error('取消收藏失败:', err);
      ElMessage.error(err.message || '取消收藏失败')
    } finally {
      item.removing = false
    }
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}

const handleRowClick = (row) => {
  if (row.productId) {
    router.push(`/product/${row.productId}`)
  } else {
    ElMessage.warning('无法跳转，商品信息不完整。')
  }
}

const formatDateTime = (dateTimeString) => {
  if (!dateTimeString) return 'N/A'
  try {
    return new Date(dateTimeString).toLocaleString('zh-CN', {
      year: 'numeric', month: 'short', day: 'numeric', 
      hour: '2-digit', minute: '2-digit'
    })
  } catch (e) {
    return dateTimeString // fallback if parsing fails
  }
}

const onImageError = (event) => {
  event.target.src = '/default-placeholder.png' // Path to your default placeholder image
}
</script>

<style scoped>
.my-favorites {
  padding: 20px;
}

.fav-image {
  width: 80px; /* Adjusted size */
  height: 80px; /* Adjusted size */
  border-radius: 4px;
  object-fit: cover;
  border: 1px solid #eee;
}

.loading-state, .error-state {
  padding: 20px;
  text-align: center;
}
</style>
