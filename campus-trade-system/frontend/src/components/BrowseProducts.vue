<template>
  <div class="browse-products">
    <h3>商品浏览区</h3>
    <el-input
      v-model="searchKeyword"
      placeholder="搜索商品..."
      @keyup.enter="fetchProducts"
      clearable
      style="margin-bottom: 20px; max-width: 400px;"
    >
      <template #append>
        <el-button @click="fetchProducts"><el-icon><Search /></el-icon></el-button>
      </template>
    </el-input>
    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="5" animated />
    </div>
    <div v-else-if="error" class="error-state">
      <el-alert title="加载商品失败" type="error" :description="error" show-icon :closable="false" />
    </div>
    <div v-else-if="products.length === 0" class="empty-state">
      <el-empty description="暂无商品" />
    </div>
    <div v-else class="product-list">
      <ProductCard
        v-for="item in products"
        :key="item.productId" 
        :product="item"
        @view="goToDetail"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getAllProductsApi } from '../api/product';
import ProductCard from '../components/ProductCard.vue';
import { ElMessage, ElAlert, ElSkeleton, ElInput, ElButton, ElIcon, ElEmpty } from 'element-plus';
import { Search } from '@element-plus/icons-vue';

const router = useRouter();
const products = ref([]);
const loading = ref(false);
const error = ref(null);
const searchKeyword = ref('');

const fetchProducts = async () => {
  loading.value = true;
  error.value = null;
  try {
    const params = {
      status: 'AVAILABLE' // 只获取在售状态的商品
    };
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value;
    }
    const data = await getAllProductsApi(params);
    products.value = data.map(p => ({
      id: p.productId,
      productId: p.productId, // 保持原有字段
      title: p.title,
      image: p.imageUrl,
      imageUrl: p.imageUrl, // 保持原有字段
      price: p.price,
    }));
  } catch (err) {
    console.error("Failed to fetch products:", err);
    error.value = err.message || '无法连接到服务器或获取数据失败';
    ElMessage.error(error.value);
  } finally {
    loading.value = false;
  }
};

const goToDetail = (product) => {
  router.push(`/product/${product.id}`);
};

onMounted(() => {
  fetchProducts();
});
</script>

<style scoped>
.browse-products {
  padding: 20px;
}
.product-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 20px;
}
.loading-state, .error-state, .empty-state {
  padding: 20px;
  text-align: center;
}
</style>