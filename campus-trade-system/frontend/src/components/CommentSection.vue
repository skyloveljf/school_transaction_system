<template>
  <div class="comments">
    <h3>商品评论</h3>
    <el-form @submit.native.prevent="submitComment">
      <el-input
        type="textarea"
        v-model="newComment"
        placeholder="写下你的评论..."
        rows="3"
      />
      <el-button type="primary" class="mt-2" @click="submitComment">发表评论</el-button>
    </el-form>

    <div class="comment-list mt-4">
      <div v-for="(comment, index) in comments" :key="index" class="comment-item">
        <p><strong>{{ comment.username }}：</strong> {{ comment.text }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const newComment = ref('')
const comments = ref([
  { username: '张三', text: '这个商品还有吗？' },
  { username: '李四', text: '价格可以便宜点吗？' }
])

const submitComment = () => {
  if (!newComment.value.trim()) return
  comments.value.push({ username: '我', text: newComment.value })
  newComment.value = ''
}
</script>

<style scoped>
.comment-item {
  border-bottom: 1px solid #ddd;
  padding: 8px 0;
}
</style>
