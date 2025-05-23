<template>
  <div class="browse-products">
    <h3 class="page-title">商品浏览区</h3>

    <div class="search-container">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索商品..."
        @keyup.enter="fetchProducts"
        clearable
        class="search-input"
      >
        <template #append>
          <el-button @click="fetchProducts" class="search-button">
            <el-icon><Search /></el-icon>
          </el-button>
        </template>
      </el-input>
    </div>

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="5" animated />
    </div>

    <div v-else-if="error" class="error-state">
      <el-alert 
        title="加载商品失败" 
        type="error" 
        :description="error" 
        show-icon 
        :closable="false" 
      />
    </div>

    <div v-else-if="allProducts.length === 0" class="empty-state">
      <el-empty description="暂无商品" />
    </div>

    <div v-else class="product-list">
      <ProductCard
        v-for="item in paginatedProducts"
        :key="item.id"
        :product="item"
        @view="goToDetail"
      />
    </div>

    <div class="pagination-wrapper" v-if="totalProducts > pageSize">
      <el-pagination
        background
        layout="prev, pager, next, jumper, ->, total"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="totalProducts"
        @current-change="handlePageChange"
        :pager-count="5"
        :hide-on-single-page="true"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getAllProductsApi } from '../api/product';
import ProductCard from '../components/ProductCard.vue';
import { ElMessage, ElAlert, ElSkeleton, ElInput, ElButton, ElIcon, ElEmpty, ElPagination } from 'element-plus';
import { Search } from '@element-plus/icons-vue';

const router = useRouter();

const searchKeyword = ref('');
const allProducts = ref([]);
const currentPage = ref(1);
const pageSize = ref(6); // 调整为每页6个商品更合理
const loading = ref(false);
const error = ref(null);

// 计算分页后的商品列表
const paginatedProducts = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return allProducts.value.slice(start, start + pageSize.value);
});

const totalProducts = computed(() => allProducts.value.length);

// 获取商品数据
const fetchProducts = async () => {
  loading.value = true;
  error.value = null;
  try {
    const params = {};
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value;
    }
    const data = await getAllProductsApi(params);
    allProducts.value = data.map(p => ({
      id: p.productId,
      title: p.title,
      image: p.imageUrl,
      price: p.price,
      // 可根据需要添加更多字段
    }));
    currentPage.value = 1; // 搜索后重置到第一页
  } catch (err) {
    console.error("Failed to fetch products:", err);
    error.value = err.message || '无法连接到服务器或获取数据失败';
    ElMessage.error(error.value);
  } finally {
    loading.value = false;
  }
};

// 商品详情跳转
const goToDetail = (product) => {
  router.push(`/product/${product.id}`);
};

// 分页切换事件
const handlePageChange = (page) => {
  currentPage.value = page;
  // 可选：滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

onMounted(() => {
  fetchProducts();
});
</script>

<style scoped>
.browse-products {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  text-align: center;
  margin-bottom: 24px;
  color: var(--el-text-color-primary);
  font-size: 1.5rem;
}

.search-container {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.search-input {
  max-width: 500px;
  width: 100%;
}

.search-button {
  background-color: var(--el-color-primary);
  color: white;
}

.search-button:hover {
  opacity: 0.9;
}

.product-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  padding: 10px 0;
}

.loading-state, 
.error-state, 
.empty-state {
  padding: 40px 20px;
  text-align: center;
}

.pagination-wrapper {
  margin: 40px 0;
  display: flex;
  justify-content: center;
}

/* 自定义分页样式 */
:deep(.el-pagination) {
  --el-pagination-button-width: 36px;
  --el-pagination-button-height: 36px;
  --el-pagination-font-size: 14px;
}

:deep(.el-pagination.is-background .btn-prev),
:deep(.el-pagination.is-background .btn-next),
:deep(.el-pagination.is-background .el-pager li) {
  border-radius: 6px;
  margin: 0 4px;
  min-width: 36px;
  background-color: #f5f7fa;
  transition: all 0.3s;
}

:deep(.el-pagination.is-background .btn-prev:hover),
:deep(.el-pagination.is-background .btn-next:hover),
:deep(.el-pagination.is-background .el-pager li:hover) {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

:deep(.el-pagination.is-background .el-pager li.is-active) {
  background-color: var(--el-color-primary);
  color: white;
  font-weight: normal;
}

:deep(.el-pagination__jump) {
  margin-left: 12px;
}

:deep(.el-pagination__total) {
  margin-left: 12px;
  color: var(--el-text-color-regular);
}

@media (max-width: 768px) {
  .product-list {
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
    gap: 15px;
  }
  
  :deep(.el-pagination) {
    --el-pagination-button-width: 32px;
    --el-pagination-button-height: 32px;
    flex-wrap: wrap;
  }
  
  :deep(.el-pagination__jump),
  :deep(.el-pagination__total) {
    margin-top: 10px;
    width: 100%;
    text-align: center;
    margin-left: 0;
  }
}
</style>