<template>
  <el-card class="product-card" @click="handleClick">
    <img :src="getImageSrc" class="product-image" />
    <div class="product-info">
      <h4>{{ props.product.title }}</h4>
      <p class="price">￥{{ props.product.price }}</p>
    </div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue';
import { getImageUrl } from '../utils/imageHelper';

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const getImageSrc = computed(() => {
  return getImageUrl(props.product.image || props.product.imageUrl);
});

const emit = defineEmits(['view'])

const handleClick = () => {
  emit('view', props.product)
}
</script>

<style scoped>
.product-card {
  width: 240px;
  margin: 10px;
  cursor: pointer;
  transition: transform 0.3s;
}
.product-card:hover {
  transform: translateY(-5px);
}
.product-image {
  width: 100%;
  height: 160px;
  object-fit: cover;
}
.product-info {
  padding: 10px;
}
.price {
  color: #f56c6c;
  font-weight: bold;
}
</style>
