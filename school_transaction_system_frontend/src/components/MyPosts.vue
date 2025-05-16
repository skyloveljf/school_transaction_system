<template>
  <div class="my-posts">
    <template v-if="mode === 'list'">
      <h3>我的发布</h3>
      <el-button type="primary" @click="switchToAdd" style="margin-bottom: 16px;">发布新商品</el-button>

      <el-table :data="posts" style="width: 100%">
        <el-table-column prop="title" label="商品名称" />
        <el-table-column prop="price" label="价格" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" type="primary" @click="switchToEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deletePost(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </template>

    <template v-else>
      <el-button type="text" @click="mode = 'list'">&lt; 返回我的发布</el-button>
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
          <el-button type="primary" @click="submitForm" size="medium">
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
  padding: 20px;
}
.form-container {
  max-width: 600px;
  margin-top: 20px;
}
.btn-group {
  text-align: center;
  margin-top: 20px;
}
.upload-demo {
  width: 100%;
  border: 2px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  text-align: center;
  padding: 30px 0;
  color: #909399;
  font-size: 14px;
}
.image-preview {
  margin-top: 10px;
}
.image-preview img {
  max-width: 200px;
  max-height: 150px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
}
</style>
