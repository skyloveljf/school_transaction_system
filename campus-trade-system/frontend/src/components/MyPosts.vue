<template>
  <div class="my-posts">
    <template v-if="mode === 'list'">
      <h3>我的发布</h3>
      <div class="action-buttons">
      <el-button type="primary" @click="switchToAddMode" style="margin-bottom: 16px;">发布新商品</el-button>
        <!-- Removed el-dropdown for cleanup mock products -->
      </div>
      
      <!-- Removed el-dialog for remove by ID -->

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

        <el-form-item label="商品分类" prop="categoryId">
          <el-select
            v-model="productForm.categoryId"
            placeholder="请选择商品分类"
            style="width: 100%;"
            clearable
            filterable
            :loading="categoriesLoading"
          >
            <el-option
              v-for="category in categories"
              :key="category.categoryId"
              :label="category.categoryName"
              :value="category.categoryId"
            />
          </el-select>
          <div v-if="categoriesError" class="el-form-item__error" style="color: var(--el-color-danger);">
            加载分类失败: {{ categoriesError }}
          </div>
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
import { ElMessageBox, ElMessage, ElAlert, ElSkeleton, ElTable, ElTableColumn, ElButton, ElForm, ElFormItem, ElInput, ElInputNumber, ElUpload, ElIcon, ElEmpty, ElTag, ElSelect, ElOption } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getProductsByUserIdApi, createProductApi, updateProductApi, deleteProductApi } from '../api/product'
import { getCategoriesApi } from '../api/category' // <--- 导入获取分类的API
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

const categories = ref([]) // <--- 新增：存储分类列表
const categoriesLoading = ref(false) // <--- 新增：分类加载状态
const categoriesError = ref(null) // <--- 新增：分类加载错误

const productFormRef = ref(null)
const productForm = reactive({
  title: '',
  description: '',
  price: null,
  categoryId: null, // 新增分类ID
  imageUrl: '' // 用于存储图片URL
})

// 从 authService 获取用户信息
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
  
  // 检查用户登录状态
  if (!authService.isLoggedIn()) {
    ElMessage.error('您尚未登录或登录已过期，请先登录')
    console.error('生成描述失败：用户未登录')
    // 保存当前URL，以便登录后返回
    localStorage.setItem('redirectAfterLogin', window.location.pathname)
    // 延迟跳转，给用户一点时间看到错误消息
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
    // 触发表单验证更新
    productFormRef.value?.validateField('description')
  } catch (error) {
    console.error('生成描述错误:', error)
    
    // 详细记录错误信息
    if (error.response) {
      console.error('错误状态码:', error.response.status)
      console.error('错误数据:', error.response.data)
      
      // 如果是401错误（未授权），引导用户登录
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
  categoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }], // <--- 修改提示信息和触发器
  imageUrl: [{ required: true, message: '请上传商品图片', trigger: 'change' }]
}

const fetchMyPosts = async () => {
  console.log('MyPosts - 正在获取我的发布，当前用户:', currentUser.value);
  if (!currentUser.value || !currentUser.value.userId) {
    postsError.value = "无法获取用户信息，请重新登录。";
    console.error('MyPosts - 无法获取用户ID，localStorage中的用户信息:', localStorage.getItem('userProfileDto'));
    // ElMessage.error(postsError.value); // 错误消息已在postsError中，让模板处理显示
    return;
  }
  loadingPosts.value = true
  postsError.value = null
  try {
    const data = await getProductsByUserIdApi(currentUser.value.userId)
    // 确保后端返回的每个商品都有 productId，并映射到 id
    posts.value = data.map(p => {
      if (!p.productId) {
        console.warn('Product received without productId:', p);
      }
      return { ...p, id: p.productId }; // 确保前端统一使用 id
    });
    
    // 保存从数据库获取的有效商品列表
    localStorage.setItem('validProductIds', JSON.stringify(posts.value.map(p => p.id)));
    
    // 从localStorage中获取临时保存的"模拟商品"
    const mockProductsData = localStorage.getItem('mockProducts');
    if (mockProductsData) {
      try {
        const mockProducts = JSON.parse(mockProductsData);
        // 合并模拟商品到列表中 (确保不重复)
        mockProducts.forEach(mockProduct => {
          if (!posts.value.some(p => p.id === mockProduct.id)) {
            posts.value.push(mockProduct);
          }
        });
      } catch (e) {
        console.error('解析本地保存的模拟商品数据失败:', e);
      }
    }
  } catch (err) {
    console.error("Failed to fetch my posts:", err)
    postsError.value = (err.message || '加载我的商品列表失败，请稍后重试');
    
    // 尝试从localStorage中恢复模拟商品
    const mockProductsData = localStorage.getItem('mockProducts');
    if (mockProductsData) {
      try {
        posts.value = JSON.parse(mockProductsData);
        postsError.value = null; // 如果成功恢复模拟数据，清除错误提示
      } catch (e) {
        console.error('解析本地保存的模拟商品数据失败:', e);
      }
    }
  } finally {
    loadingPosts.value = false
  }
}

const fetchCategories = async () => {
  categoriesLoading.value = true
  categoriesError.value = null
  try {
    const response = await getCategoriesApi() // 'response' here is the actual array of categories
    // 检查您的 request 工具是如何返回数据的
    // 如果 request 工具返回的是完整的 Axios 响应对象，那么实际数据在 response.data 中
    // categories.value = response.data // <--- 旧代码
    // 如果您的 request 工具已经处理过并直接返回数据数组，则 response 可能就是您需要的数组
    categories.value = response; // <--- 修改后的代码：直接使用 response

    console.log('API响应 (直接是数组):', response); // 调试：查看完整的API响应
    console.log('提取到的分类数据:', categories.value); // 调试：查看赋给ref的数据
    if (!Array.isArray(categories.value)) {
        console.warn('从API获取的分类数据不是一个数组!', categories.value);
        categories.value = []; // 如果不是数组，则置为空数组以避免模板错误
        categoriesError.value = '获取到的分类数据格式不正确';
    }
  } catch (error) {
    console.error('获取商品分类列表失败:', error)
    categoriesError.value = error.message || '无法加载分类数据'
    ElMessage.error('获取商品分类列表失败: ' + categoriesError.value)
    categories.value = []; // 出错时也清空
  } finally {
    categoriesLoading.value = false
  }
};

onMounted(() => {
  fetchMyPosts()
  fetchCategories() // <--- 在组件挂载时获取分类
})

onActivated(() => {
  console.log('MyPosts组件被激活，重新加载数据');
  if (mode.value === 'list') {
    fetchMyPosts();
  }
  // 考虑是否在激活时也需要刷新分类，通常分类不常变动，onMounted获取一次可能就够了
  // if (!categories.value.length && !categoriesLoading.value) {
  //   fetchCategories();
  // }
})

const resetProductForm = () => {
  productForm.title = ''
  productForm.description = ''
  productForm.price = null
  productForm.categoryId = null
  productForm.imageUrl = ''
  editingProductId.value = null
  // 在下次 DOM 更新循环之后重置表单校验状态
  productFormRef.value?.clearValidate()
}

const switchToAddMode = () => {
  resetProductForm()
  mode.value = 'add'
  // 如果分类列表为空且未在加载，则尝试获取
  if (!categories.value.length && !categoriesLoading.value && !categoriesError.value) {
    fetchCategories()
  }
}

const switchToEditMode = (post) => {
  if (!post || typeof post.id === 'undefined' || post.id === null) {
    ElMessage.error('无法编辑，商品信息不完整。');
    console.error('Edit attempt on post with invalid id:', post);
    return;
  }
  editingProductId.value = post.id; // 使用 post.id
  productForm.title = post.title;
  productForm.description = post.description;
  productForm.price = post.price;
  productForm.categoryId = post.categoryId; 
  productForm.imageUrl = post.imageUrl || post.image; // 兼容旧的 image 字段
  mode.value = 'edit';
  // 如果分类列表为空且未在加载，则尝试获取
  if (!categories.value.length && !categoriesLoading.value && !categoriesError.value) {
    fetchCategories()
  }
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
    // 手动触发imageUrl字段的校验
    productFormRef.value?.validateField('imageUrl');
  } catch (error) {
    ElMessage.error(error.message || '图片上传失败');
    productForm.imageUrl = ''; // 清空图片URL以防提交旧的或无效的URL
  } finally {
    imageUploading.value = false;
  }
}

const handleSubmitForm = async () => {
  if (!productFormRef.value) return;
  await productFormRef.value.validate(async (valid) => {
    if (valid) {
      formSubmitting.value = true;
      // 修正数据结构，确保字段名与后端接口一致
      const payload = {
        productName: productForm.title, // 后端DTO使用productName而不是title
        description: productForm.description,
        price: productForm.price,
        categoryId: productForm.categoryId,
        imageUrl: productForm.imageUrl,
      };
      // 调试信息
      console.log('Submitting product form for mode:', mode.value);
      console.log('Editing Product ID:', editingProductId.value);
      console.log('Payload:', JSON.stringify(payload));

      try {
        if (mode.value === 'add') {
          try {
            const result = await createProductApi(payload);
            console.log('商品发布成功，返回数据:', result);
          ElMessage.success('商品发布成功！');
          } catch (error) {
            // 如果API调用失败但使用了模拟图片，在本地保存模拟商品数据
            if (payload.imageUrl && payload.imageUrl.includes('mock_')) {
              console.log('API创建失败，但使用了模拟图片，在本地保存模拟商品');
              
              // 生成一个临时ID (使用负数避免与真实ID冲突)
              const tempId = -Math.floor(Math.random() * 10000) - 1;
              
              // 创建模拟商品对象
              const mockProduct = {
                id: tempId,
                title: payload.productName,
                description: payload.description,
                price: payload.price,
                categoryId: payload.categoryId,
                imageUrl: payload.imageUrl,
                status: 'AVAILABLE',
                postDate: new Date().toISOString(),
                userId: currentUser.value.userId,
                isMockData: true
              };
              
              // 获取之前保存的模拟商品
              const mockProductsData = localStorage.getItem('mockProducts');
              let mockProducts = [];
              if (mockProductsData) {
                try {
                  mockProducts = JSON.parse(mockProductsData);
                } catch (e) {
                  console.error('解析模拟商品数据失败:', e);
                }
              }
              
              // 添加新的模拟商品
              mockProducts.push(mockProduct);
              
              // 保存回localStorage
              localStorage.setItem('mockProducts', JSON.stringify(mockProducts));
              
              ElMessage.success('商品已保存在本地（模拟数据）');
              throw new Error('商品发布到后端失败，但已保存为本地模拟数据');
            } else {
              throw error; // 重新抛出错误，让外层catch处理
            }
          }
        } else if (mode.value === 'edit' && editingProductId.value) {
          const result = await updateProductApi(editingProductId.value, payload);
          console.log('商品更新成功，返回数据:', result);
          ElMessage.success('商品更新成功！');
        }
        switchToListView();
        // 短暂延迟后重新加载列表，确保数据已更新
        setTimeout(() => {
        fetchMyPosts(); // 重新加载列表
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
    formSubmitting.value = true; // 可以用一个通用的loading状态
    try {
      // 检查是否为模拟数据（通过检查图片URL是否包含mock_或者ID为负数）
      const isMockData = (post.imageUrl && post.imageUrl.includes('mock_')) || 
                         (post.id < 0) || 
                         post.isMockData;
      
      if (isMockData) {
        console.log('检测到模拟数据，本地处理删除操作:', post);
        // 对于模拟数据，直接从本地列表中移除，不调用后端API
        posts.value = posts.value.filter(p => p.id !== post.id);
        
        // 重要：更新localStorage中的模拟商品数据
        const mockProductsData = localStorage.getItem('mockProducts');
        if (mockProductsData) {
          try {
            let mockProducts = JSON.parse(mockProductsData);
            mockProducts = mockProducts.filter(p => p.id !== post.id);
            localStorage.setItem('mockProducts', JSON.stringify(mockProducts));
            console.log('已从localStorage中删除模拟商品:', post.id);
          } catch (e) {
            console.error('处理模拟商品数据出错:', e);
          }
        }
        
        ElMessage.success('商品删除成功！');
      } else {
        // 对于真实数据，调用后端API
        await deleteProductApi(post.id);
      ElMessage.success('商品删除成功！');
      fetchMyPosts(); // 重新加载列表
      }
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
    PENDING_REVIEW: '审核中',
    SOLD: '已售出',
    DELISTED: '已下架'
  };
  return statusMap[status] || status;
};

const getProductStatusType = (status) => {
  const statusTypeMap = {
    AVAILABLE: 'success',
    PENDING_REVIEW: 'warning',
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
  text-align: right; /* Aligns buttons to the right */
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
/* el-upload picture-card style adjustment */
:deep(.el-upload--picture-card) {
  width: 148px; /* Default size */
  height: 148px; /* Default size */
  display: flex;
  align-items: center;
  justify-content: center;
}
:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 148px;
  height: 148px;
}
</style>
