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
  padding: 20px;
}
.product-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}
</style>