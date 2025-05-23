<template>
  <div class="my-posts">
    <template v-if="mode === 'list'">
      <h3>我的发布</h3>
      <div class="action-buttons">
        <el-button type="primary" @click="switchToAddMode" style="margin-bottom: 16px;">发布新商品</el-button>
      </div>
      
      <div v-if="loadingPosts" class="loading-state">
        <el-skeleton :rows="3" animated />
      </div>
      <div v-else-if="postsError" class="error-state">
        <el-alert title="加载我的商品失败" type="error" :description="postsError" show-icon :closable="false" />
      </div>
      <el-empty v-else-if="posts.length === 0" description="您还没有发布任何商品" />
      <el-table v-else :data="posts" style="width: 100%">
        <el-table-column label="图片" width="100">
          <template #default="scope">
            <img :src="getImageUrl(scope.row.image || scope.row.imageUrl)" alt="商品图片" style="width: 60px; height: 60px; object-fit: cover;" />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="商品名称" />
        <el-table-column prop="price" label="价格 (元)" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getProductStatusType(scope.row.status)">
              {{ formatProductStatus(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" type="primary" @click="switchToEditMode(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="confirmDeletePost(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </template>

    <template v-else>
      <el-button type="text" @click="switchToListView" style="margin-bottom: 15px;">&lt; 返回我的发布</el-button>
      <h3 v-if="mode === 'add'">发布新商品</h3>
      <h3 v-else>编辑商品</h3>

      <el-form
        :model="productForm"
        ref="productFormRef"
        label-width="110px"
        :rules="productFormRules"
        class="form-container"
        v-loading="formSubmitting"
      >
        <el-form-item label="商品名称" prop="title">
          <el-input v-model="productForm.title" placeholder="请输入商品名称" clearable />
        </el-form-item>

        <el-form-item label="商品描述" prop="description">
          <div class="description-with-button">
            <el-input
              type="textarea"
              v-model="productForm.description"
              rows="4"
              placeholder="请输入商品描述"
              show-word-limit
              maxlength="500"
              clearable
            />
            <el-button 
              type="primary" 
              @click="generateDescription" 
              :disabled="!productForm.title || generatingDescription"
              :loading="generatingDescription" 
              size="small"
              style="margin-top: 8px;"
            >
              自动生成
            </el-button>
          </div>
        </el-form-item>

        <el-form-item label="价格 (元)" prop="price">
          <el-input-number
            v-model="productForm.price"
            :min="0.01"
            :precision="2"
            :step="1"
            controls-position="right"
            placeholder="请输入价格"
            style="width: 100%;"
          />
        </el-form-item>

        <el-form-item label="分类ID" prop="categoryId">
          <el-input-number v-model="productForm.categoryId" :min="1" placeholder="请输入分类ID" style="width: 100%;"/>
        </el-form-item>

        <el-form-item label="商品图片" prop="imageUrl">
          <el-upload
            action="#" 
            :http-request="handleImageUpload"
            :before-upload="beforeImageUpload"
            :show-file-list="false"
            list-type="picture-card"
            :limit="1"
            accept="image/*"
            v-loading="imageUploading"
          >
            <img v-if="productForm.imageUrl" :src="getImageUrl(productForm.imageUrl)" class="uploaded-image" alt="商品图片"/>
            <el-icon v-else><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">
                只能上传jpg/png文件，且不超过2MB
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item class="btn-group">
          <el-button type="primary" @click="handleSubmitForm" size="small" :loading="formSubmitting">
            {{ mode === 'add' ? '立即发布' : '保存修改' }}
          </el-button>
          <el-button @click="cancelFormSubmission" size="medium" plain>取消</el-button>
        </el-form-item>
      </el-form>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onActivated, computed } from 'vue'
import { ElMessageBox, ElMessage, ElAlert, ElSkeleton, ElTable, ElTableColumn, ElButton, ElForm, ElFormItem, ElInput, ElInputNumber, ElUpload, ElIcon, ElEmpty, ElTag } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getProductsByUserIdApi, createProductApi, updateProductApi, deleteProductApi } from '../api/product'
import { uploadImageApi } from '../api/upload'
import authService from '../services/authService'
import { getImageUrl } from '../utils/imageHelper'
import { generateProductDescriptionApi } from '../api/deepseek'

const posts = ref([])
const generatingDescription = ref(false)
const mode = ref('list')  // 'list' | 'add' | 'edit'
const editingProductId = ref(null)
const loadingPosts = ref(false)
const postsError = ref(null)
const formSubmitting = ref(false)
const imageUploading = ref(false)

const productFormRef = ref(null)
const productForm = reactive({
  title: '',
  description: '',
  price: null,
  categoryId: null,
  imageUrl: ''
})

const currentUser = computed(() => {
  const user = authService.getUserProfile();
  console.log('MyPosts - 获取当前用户信息:', user);
  return user;
});

const generateDescription = async () => {
  if (!productForm.title) {
    ElMessage.warning('请先填写商品名称，AI将根据名称生成描述')
    return
  }
  
  if (!authService.isLoggedIn()) {
    ElMessage.error('您尚未登录或登录已过期，请先登录')
    console.error('生成描述失败：用户未登录')
    localStorage.setItem('redirectAfterLogin', window.location.pathname)
    setTimeout(() => {
      window.location.href = '/'
    }, 1500)
    return
  }
  
  generatingDescription.value = true
  ElMessage.info('正在生成描述，请稍候...')
  
  try {
    console.log('调用AI生成描述API，商品名称:', productForm.title)
    const description = await generateProductDescriptionApi(productForm.title)
    
    if (!description) {
      console.warn('API返回了空描述')
      ElMessage.warning('AI未能生成有效描述，请尝试修改商品名称或手动输入')
      return
    }
    
    console.log('成功生成描述:', description.substring(0, 50) + '...')
    productForm.description = description
    ElMessage.success('AI已生成商品描述')
    productFormRef.value?.validateField('description')
  } catch (error) {
    console.error('生成描述错误:', error)
    
    if (error.response) {
      console.error('错误状态码:', error.response.status)
      console.error('错误数据:', error.response.data)
      
      if (error.response.status === 401) {
        ElMessage.error('请先登录后再使用AI生成功能')
        return
      }
    }
    
    ElMessage.error(error.message || 'AI生成描述失败，请手动输入')
  } finally {
    generatingDescription.value = false
  }
}

const productFormRules = {
  title: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入商品描述', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }, { type: 'number', message: '价格必须为数字' }],
  categoryId: [{ required: true, message: '请输入分类ID', trigger: 'blur' }, { type: 'number', message: '分类ID必须为数字' }],
  imageUrl: [{ required: true, message: '请上传商品图片', trigger: 'change' }]
}

const fetchMyPosts = async () => {
  console.log('MyPosts - 正在获取我的发布，当前用户:', currentUser.value);
  if (!currentUser.value || !currentUser.value.userId) {
    postsError.value = "无法获取用户信息，请重新登录。";
    console.error('MyPosts - 无法获取用户ID，localStorage中的用户信息:', localStorage.getItem('userProfileDto'));
    return;
  }
  
  loadingPosts.value = true
  postsError.value = null
  
  try {
    const data = await getProductsByUserIdApi(currentUser.value.userId)
    posts.value = data.map(p => ({
      ...p,
      id: p.productId // 确保使用productId作为id
    }));
  } catch (err) {
    console.error("Failed to fetch my posts:", err)
    postsError.value = (err.message || '加载我的商品列表失败，请稍后重试');
  } finally {
    loadingPosts.value = false
  }
}

onMounted(fetchMyPosts)
onActivated(() => {
  console.log('MyPosts组件被激活，重新加载数据');
  if (mode.value === 'list') {
    fetchMyPosts();
  }
})

const resetProductForm = () => {
  productForm.title = ''
  productForm.description = ''
  productForm.price = null
  productForm.categoryId = null
  productForm.imageUrl = ''
  editingProductId.value = null
  productFormRef.value?.clearValidate()
}

const switchToAddMode = () => {
  resetProductForm()
  mode.value = 'add'
}

const switchToEditMode = (post) => {
  if (!post || typeof post.id === 'undefined' || post.id === null) {
    ElMessage.error('无法编辑，商品信息不完整。');
    console.error('Edit attempt on post with invalid id:', post);
    return;
  }
  
  editingProductId.value = post.id;
  productForm.title = post.title;
  productForm.description = post.description;
  productForm.price = post.price;
  productForm.categoryId = post.categoryId;
  productForm.imageUrl = post.imageUrl || post.image;
  mode.value = 'edit';
}

const switchToListView = () => {
  mode.value = 'list'
  resetProductForm()
}

const beforeImageUpload = (rawFile) => {
  const isImage = rawFile.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件 (如 JPG, PNG)')
    return false
  }
  const isLt2M = rawFile.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

const handleImageUpload = async (options) => {
  imageUploading.value = true;
  try {
    const imageUrl = await uploadImageApi(options.file);
    productForm.imageUrl = imageUrl;
    ElMessage.success('图片上传成功！');
    productFormRef.value?.validateField('imageUrl');
  } catch (error) {
    ElMessage.error(error.message || '图片上传失败');
    productForm.imageUrl = '';
  } finally {
    imageUploading.value = false;
  }
}

const handleSubmitForm = async () => {
  if (!productFormRef.value) return;
  
  await productFormRef.value.validate(async (valid) => {
    if (valid) {
      formSubmitting.value = true;
      
      const payload = {
        productName: productForm.title,
        description: productForm.description,
        price: productForm.price,
        categoryId: productForm.categoryId,
        imageUrl: productForm.imageUrl,
      };

      console.log('Submitting product form for mode:', mode.value);
      console.log('Editing Product ID:', editingProductId.value);
      console.log('Payload:', JSON.stringify(payload));

      try {
        if (mode.value === 'add') {
          await createProductApi(payload);
          ElMessage.success('商品发布成功！');
        } else if (mode.value === 'edit' && editingProductId.value) {
          await updateProductApi(editingProductId.value, payload);
          ElMessage.success('商品更新成功！');
        }
        
        switchToListView();
        setTimeout(() => {
          fetchMyPosts();
        }, 300);
      } catch (err) {
        console.error("Submit form error:", err);
        ElMessage.error(err.message || '操作失败，请稍后重试');
      } finally {
        formSubmitting.value = false;
      }
    } else {
      ElMessage.error('请检查表单填写是否正确');
      return false;
    }
  });
}

const cancelFormSubmission = () => {
  switchToListView()
}

const confirmDeletePost = (post) => {
  if (!post || typeof post.id === 'undefined' || post.id === null) {
    ElMessage.error('商品信息不完整，无法删除。');
    console.error('Delete attempt on post with invalid id:', post);
    return;
  }

  ElMessageBox.confirm(
    `确认删除商品 "${post.title}" 吗？此操作无法撤销！`,
    '删除确认',
    {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    formSubmitting.value = true;
    try {
      await deleteProductApi(post.id);
      ElMessage.success('商品删除成功！');
      fetchMyPosts();
    } catch (err) {
      console.error("Delete post error:", err);
      ElMessage.error(err.message || '删除失败，请稍后重试');
    } finally {
      formSubmitting.value = false;
    }
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
}

const formatProductStatus = (status) => {
  const statusMap = {
    AVAILABLE: '在售',
    PENDING: '审核中',
    SOLD: '已售出',
    DELISTED: '已下架'
  };
  return statusMap[status] || status;
};

const getProductStatusType = (status) => {
  const statusTypeMap = {
    AVAILABLE: 'success',
    PENDING: 'warning',
    SOLD: 'info',
    DELISTED: 'danger'
  };
  return statusTypeMap[status] || 'primary';
};
</script>

<style scoped>
.my-posts {
  padding: 20px;
}
.form-container {
  max-width: 600px;
  margin-top: 20px;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}
.btn-group {
  text-align: right;
  margin-top: 20px;
}

.loading-state, .error-state {
  padding: 20px;
  text-align: center;
}
.uploaded-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.description-with-button {
  display: flex;
  flex-direction: column;
}

:deep(.el-upload--picture-card) {
  width: 148px;
  height: 148px;
  display: flex;
  align-items: center;
  justify-content: center;
}
:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 148px;
  height: 148px;
}
</style>