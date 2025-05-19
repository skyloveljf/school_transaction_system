<template>
  <div class="browse-products">
    <h3>商品浏览区</h3>
    <div class="product-list">
      <ProductCard
        v-for="item in products"
        :key="item.id"
        :product="item"
        @view="goToDetail"
      />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { mockProducts } from '../api/product'
import ProductCard from '../components/ProductCard.vue'

const router = useRouter()
const products = ref(mockProducts)

const goToDetail = (product) => {
  router.push(`/product/${product.id}`)
}
</script>

<style scoped>
.browse-products {
  padding: 32px 0 0 0;
  min-height: 480px;
  background: linear-gradient(120deg, #f8fafc 0%, #e0e7ff 100%);
  border-radius: 18px;
  box-shadow: 0 4px 24px #c7d2fe33;
  margin-bottom: 24px;
  animation: fadeInBrowse 0.8s;
}
@keyframes fadeInBrowse {
  from { opacity: 0; transform: translateY(30px);}
  to { opacity: 1; transform: none;}
}
.browse-products h3 {
  text-align: center;
  font-size: 2rem;
  font-weight: 700;
  color: #6366f1;
  margin-bottom: 32px;
  letter-spacing: 2px;
  text-shadow: 0 2px 8px #e0e7ff44;
}

.product-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 32px 24px;
  padding: 0 12px 32px 12px;
  min-height: 320px;
  transition: box-shadow 0.2s;
}

/* 可选：为空时的提示 */
.product-list:empty::after {
  content: "暂无商品，敬请期待~";
  color: #b4b8d1;
  font-size: 1.2rem;
  margin: 40px auto;
  display: block;
  text-align: center;
  letter-spacing: 1px;
}
</style>