<template>
  <div class="my-posts">
    <template v-if="mode === 'list'">
      <h3>我的发布</h3>
      <div class="action-buttons">
      <el-button type="primary" @click="switchToAddMode" style="margin-bottom: 16px;">发布新商品</el-button>
        <el-dropdown split-button type="warning" style="margin-bottom: 16px; margin-left: 10px;" @click="cleanupMockProducts">
          清理模拟商品
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="showRemoveByIdDialog">按ID清理商品</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      
      <!-- 按ID清理对话框 -->
      <el-dialog v-model="removeByIdDialogVisible" title="按ID清理商品" width="30%">
        <el-form :model="removeByIdForm">
          <el-form-item label="商品ID">
            <el-input v-model="removeByIdForm.productId" type="number" placeholder="请输入要清理的商品ID"></el-input>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="removeByIdDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="removeProductById" :disabled="!removeByIdForm.productId">
              确认清理
            </el-button>
          </span>
        </template>
      </el-dialog>

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
          <!-- TODO: Replace with category selection dropdown fetched from /api/categories -->
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
import { uploadImageApi } from '../api/upload' // 新增的图片上传API
import authService from '../services/authService'
import { getImageUrl } from '../utils/imageHelper' // 导入图片URL处理函数
import { generateProductDescriptionApi } from '../api/deepseek'

const posts = ref([])
const generatingDescription = ref(false)
const mode = ref('list')  // 'list' | 'add' | 'edit'
const editingProductId = ref(null)
const loadingPosts = ref(false)
const postsError = ref(null)
const formSubmitting = ref(false)
const imageUploading = ref(false)
const removeByIdDialogVisible = ref(false)
const removeByIdForm = reactive({
  productId: ''
})

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
  categoryId: [{ required: true, message: '请输入分类ID', trigger: 'blur' }, { type: 'number', message: '分类ID必须为数字' }],
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

onMounted(fetchMyPosts)
// 当组件被激活时（从其他页面导航回来时）重新加载数据
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
  // 在下次 DOM 更新循环之后重置表单校验状态
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
  editingProductId.value = post.id; // 使用 post.id
  productForm.title = post.title;
  productForm.description = post.description;
  productForm.price = post.price;
  productForm.categoryId = post.categoryId; 
  productForm.imageUrl = post.imageUrl || post.image; // 兼容旧的 image 字段
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
    PENDING: '审核中', // 假设有这个状态
    SOLD: '已售出',
    DELISTED: '已下架' // 假设有这个状态
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

const cleanupMockProducts = () => {
  ElMessageBox.confirm(
    '此操作将清除所有模拟商品数据（包括使用mock图片URL的商品）。这些数据在数据库中不存在，只在前端显示。确认继续？',
    '清理确认',
    {
      confirmButtonText: '确认清理',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 清理所有localStorage中的模拟商品数据
    localStorage.removeItem('mockProducts');
    
    // 获取有效商品ID列表用于过滤
    const validProductIdsStr = localStorage.getItem('validProductIds');
    let validProductIds = [];
    if (validProductIdsStr) {
      try {
        validProductIds = JSON.parse(validProductIdsStr);
      } catch (e) {
        console.error('解析validProductIds失败:', e);
      }
    }
    
    // 标记为模拟数据的条件：
    // 1. 图片URL包含"mock_"
    // 2. 商品ID为负数（我们约定模拟数据使用负数ID）
    // 3. 商品具有isMockData=true标记
    // 4. 商品ID不在有效ID列表中
    const mockProducts = posts.value.filter(p => 
      (p.imageUrl && p.imageUrl.includes('mock_')) || 
      (p.id < 0) || 
      p.isMockData ||
      (validProductIds.length > 0 && !validProductIds.includes(p.id))
    );
    
    if (mockProducts.length === 0) {
      ElMessage.info('没有发现模拟商品数据');
      return;
    }
    
    // 从列表中移除模拟数据，只保留真正在数据库中存在的商品
    posts.value = posts.value.filter(p => 
      !((p.imageUrl && p.imageUrl.includes('mock_')) || 
        (p.id < 0) || 
        p.isMockData ||
        (validProductIds.length > 0 && !validProductIds.includes(p.id)))
    );
    
    ElMessage.success(`成功清理了 ${mockProducts.length} 个模拟商品`);
    console.log('已清理的模拟商品:', mockProducts);
  }).catch(() => {
    ElMessage.info('已取消清理操作');
  });
}

const showRemoveByIdDialog = () => {
  removeByIdForm.productId = '';
  removeByIdDialogVisible.value = true;
}

const removeProductById = () => {
  const productId = parseInt(removeByIdForm.productId);
  
  // 允许清理任何ID的商品，包括负数ID（模拟数据）
  if (isNaN(productId)) {
    ElMessage.error('请输入有效的商品ID');
    return;
  }
  
  const productToRemove = posts.value.find(p => p.id === productId);
  
  if (!productToRemove) {
    ElMessage.error(`未找到ID为 ${productId} 的商品`);
    return;
  }
  
  // 确认提示，确保用户知道他们在做什么
  ElMessageBox.confirm(
    `确认要从界面上移除商品 "${productToRemove.title}" (ID: ${productId}) 吗？如果这是一个模拟商品，它将被完全移除。`,
    '确认清理',
    {
      confirmButtonText: '确认清理',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 从前端列表中移除
    posts.value = posts.value.filter(p => p.id !== productId);
    
    // 检查是否是模拟数据
    const isMockData = (productToRemove.imageUrl && productToRemove.imageUrl.includes('mock_')) || 
                       (productId < 0) || 
                       productToRemove.isMockData;

    if (isMockData) {
      // 从localStorage中删除模拟商品
      const mockProductsData = localStorage.getItem('mockProducts');
      if (mockProductsData) {
        try {
          let mockProducts = JSON.parse(mockProductsData);
          const initialLength = mockProducts.length;
          mockProducts = mockProducts.filter(p => p.id !== productId);
          
          localStorage.setItem('mockProducts', JSON.stringify(mockProducts));
          console.log(`已从本地存储中删除模拟商品 (ID: ${productId})`);
        } catch (e) {
          console.error('处理本地模拟商品数据失败:', e);
        }
      }
      ElMessage.success(`已成功从本地清理模拟商品 (ID: ${productId})`);
    } else {
      ElMessage.success(`已从界面移除商品 (ID: ${productId})，但数据库中的记录仍然存在`);
    }
    
    removeByIdDialogVisible.value = false;
  }).catch(() => {
    ElMessage.info('已取消清理操作');
  });
}
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
