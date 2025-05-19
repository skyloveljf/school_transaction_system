<template>
  <div class="my-posts">
    <template v-if="mode === 'list'">
      <h3>我的发布</h3>
      <el-button type="primary" @click="switchToAdd" class="add-btn">发布新商品</el-button>

      <el-table :data="posts" class="posts-table" stripe border>
        <el-table-column prop="image" label="图片" width="120">
          <template #default="scope">
            <img :src="scope.row.image" alt="商品图片" class="table-img" />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="商品名称" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="scope">
            <span class="price">￥{{ scope.row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" type="primary" @click="switchToEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deletePost(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </template>

    <template v-else>
      <el-button type="text" @click="mode = 'list'" class="back-link">&lt; 返回我的发布</el-button>
      <h3 v-if="mode === 'add'">发布新商品</h3>
      <h3 v-else>编辑商品</h3>

      <el-form
        :model="form"
        ref="formRef"
        label-width="110px"
        status-icon
        label-position="left"
        :rules="rules"
        class="form-container"
      >
        <el-form-item label="商品名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入商品名称" clearable />
        </el-form-item>

        <el-form-item label="商品描述" prop="description">
          <el-input
            type="textarea"
            v-model="form.description"
            rows="4"
            placeholder="请输入商品描述"
            show-word-limit
            maxlength="200"
            clearable
          />
        </el-form-item>

        <el-form-item label="价格 (元)" prop="price">
          <el-input-number
            v-model="form.price"
            :min="0"
            :step="1"
            controls-position="right"
            placeholder="请输入价格"
            style="width: 100%;"
          />
        </el-form-item>

        <el-form-item label="商品图片" prop="image">
          <el-upload
            class="upload-demo"
            drag
            :action="uploadUrl"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :limit="1"
            accept="image/*"
          >
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          </el-upload>
          <div v-if="form.image" class="image-preview">
            <img :src="form.image" alt="商品图片" />
          </div>
        </el-form-item>

        <el-form-item class="btn-group">
          <el-button type="primary" @click="submitForm" size="medium" class="submit-btn">
            {{ mode === 'add' ? '发布' : '保存' }}
          </el-button>
          <el-button @click="cancelForm" size="medium" plain>取消</el-button>
        </el-form-item>
      </el-form>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'

const posts = ref([])
const mode = ref('list')  // 'list' | 'add' | 'edit'
const editId = ref(null)

const formRef = ref(null)
const form = reactive({
  title: '',
  description: '',
  price: null,
  image: ''
})

const rules = {
  title: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入商品描述', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  image: [{ required: true, message: '请上传商品图片', trigger: 'change' }]
}

const fetchMyPosts = () => {
  posts.value = [
    { id: 101, title: '二手吉他', description: '九成新，适合初学者', price: 500, image: 'https://example.com/guitar.jpg' },
    { id: 102, title: '学习用书', description: '2020版，完好无损', price: 60, image: 'https://example.com/book.jpg' },
  ]
}

onMounted(fetchMyPosts)

const resetForm = () => {
  form.title = ''
  form.description = ''
  form.price = null
  form.image = ''
  editId.value = null
  formRef.value?.resetFields()
}

const switchToAdd = () => {
  resetForm()
  mode.value = 'add'
}

const switchToEdit = (post) => {
  form.title = post.title
  form.description = post.description
  form.price = post.price
  form.image = post.image
  editId.value = post.id
  mode.value = 'edit'
}

const submitForm = () => {
  formRef.value.validate(valid => {
    if (!valid) {
      ElMessage.error('请填写完整信息')
      return
    }
    if (mode.value === 'add') {
      const newId = posts.value.length ? Math.max(...posts.value.map(p => p.id)) + 1 : 1
      posts.value.push({
        id: newId,
        title: form.title,
        description: form.description,
        price: form.price,
        image: form.image
      })
      ElMessage.success('商品发布成功！')
    } else if (mode.value === 'edit') {
      const idx = posts.value.findIndex(p => p.id === editId.value)
      if (idx !== -1) {
        posts.value[idx] = {
          id: editId.value,
          title: form.title,
          description: form.description,
          price: form.price,
          image: form.image
        }
        ElMessage.success('商品编辑成功！')
      }
    }
    mode.value = 'list'
    resetForm()
  })
}

const cancelForm = () => {
  mode.value = 'list'
  resetForm()
}

const deletePost = (post) => {
  ElMessageBox.confirm(
    `确认删除商品 "${post.title}" 吗？此操作无法撤销！`,
    '删除确认',
    {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    posts.value = posts.value.filter(item => item.id !== post.id)
    ElMessage({ type: 'success', message: '删除成功' })
  }).catch(() => {
    ElMessage({ type: 'info', message: '已取消删除' })
  })
}

// 这里模拟上传地址，无实际上传功能
const uploadUrl = 'https://jsonplaceholder.typicode.com/posts/' // 只是示例地址

// 上传前校验（例如文件大小、类型等）
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

// 上传成功处理
const handleUploadSuccess = (response, file) => {
  // 这里模拟接口返回url，实际请替换成你的接口返回地址
  // 例如: form.image = response.url
  // 这里用本地文件预览URL替代（上传接口未实现，实际使用时替换）
  form.image = URL.createObjectURL(file.raw)
  ElMessage.success('图片上传成功')
}

const handleUploadError = () => {
  ElMessage.error('图片上传失败')
}
</script>

<style scoped>
.my-posts {
  padding: 32px 0 0 0;
  min-height: 480px;
  background: linear-gradient(120deg, #f8fafc 0%, #e0e7ff 100%);
  border-radius: 18px;
  box-shadow: 0 4px 24px #c7d2fe33;
  margin-bottom: 24px;
  animation: fadeInPosts 0.8s;
}
@keyframes fadeInPosts {
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
.add-btn {
  display: block;
  margin: 0 auto 18px auto;
  font-weight: 600;
  border-radius: 10px;
  background: linear-gradient(90deg, #a1c4fd 0%, #c2e9fb 100%);
  border: none;
  color: #6366f1;
  transition: background 0.2s;
}
.add-btn:hover {
  background: linear-gradient(90deg, #6366f1 0%, #a1c4fd 100%);
  color: #fff;
}
.posts-table {
  background: rgba(255,255,255,0.98);
  border-radius: 14px;
  box-shadow: 0 2px 12px #a1c4fd22;
  margin-bottom: 24px;
}
.table-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
  border: 1.5px solid #e0e7ff;
  box-shadow: 0 1px 6px #e0e7ff33;
}
.price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 1.1rem;
  letter-spacing: 1px;
}
.back-link {
  margin-bottom: 18px;
  color: #6366f1;
  font-size: 1rem;
  font-weight: 600;
}
.form-container {
  max-width: 600px;
  margin: 0 auto;
  background: rgba(255,255,255,0.98);
  border-radius: 14px;
  box-shadow: 0 2px 12px #a1c4fd22;
  padding: 32px 28px 18px 28px;
  margin-top: 20px;
}
.btn-group {
  text-align: center;
  margin-top: 20px;
}
.submit-btn {
  font-weight: 700;
  border-radius: 10px;
  background: linear-gradient(90deg, #a1c4fd 0%, #c2e9fb 100%);
  border: none;
  color: #6366f1;
  transition: background 0.2s;
}
.submit-btn:hover {
  background: linear-gradient(90deg, #6366f1 0%, #a1c4fd 100%);
  color: #fff;
}
.upload-demo {
  width: 100%;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  text-align: center;
  padding: 30px 0;
  color: #909399;
  font-size: 14px;
  background: #f8fafc;
  transition: border-color 0.2s;
}
.upload-demo:hover {
  border-color: #a1c4fd;
}
.image-preview {
  margin-top: 10px;
  text-align: left;
}
.image-preview img {
  max-width: 180px;
  max-height: 120px;
  border-radius: 8px;
  border: 1.5px solid #e0e7ff;
  box-shadow: 0 1px 6px #e0e7ff33;
}
</style>
