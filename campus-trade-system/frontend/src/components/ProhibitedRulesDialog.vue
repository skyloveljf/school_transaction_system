<template>
    <el-dialog 
      v-model="visible" 
      title="违禁情况管理" 
      width="800px" 
      :close-on-click-modal="false"
      @open="fetchRules"
    >
      <div class="rules-header-actions">
        <el-button type="primary" @click="showAddDialog" size="small">
          <el-icon><Plus /></el-icon>
          添加违禁规则
        </el-button>
        <el-button @click="fetchRules" :loading="loading" size="small">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
        <span class="rules-count">共 {{ rules.length }} 条规则</span>
      </div>
      
      <el-table 
        :data="rules" 
        style="width: 100%" 
        stripe 
        border 
        max-height="400px"
        v-loading="loading"
        element-loading-text="加载违禁规则中..."
      >
        <el-table-column prop="ruleId" label="ID" width="60" />
        <el-table-column prop="ruleName" label="规则名称" min-width="120" />
        <el-table-column prop="ruleDescription" label="规则描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template #default="scope">
            <el-switch
              v-model="scope.row.isActive"
              @change="updateRuleStatus(scope.row)"
              :loading="updatingRuleId === scope.row.ruleId"
              active-text="启用"
              inactive-text="禁用"
            />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
          <template #default="scope">
            {{ formatDate(scope.row.createdTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="scope">
            <el-button 
              size="small" 
              type="primary" 
              @click="editRule(scope.row)"
            >
              编辑
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="deleteRule(scope.row)"
              :loading="deletingRuleId === scope.row.ruleId"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <template #footer>
        <el-button @click="visible = false">关闭</el-button>
      </template>
      
      <!-- 添加/编辑规则对话框 -->
      <el-dialog
        v-model="showFormDialog"
        :title="isEditing ? '编辑违禁规则' : '添加违禁规则'"
        width="500px"
        :close-on-click-modal="false"
      >
        <el-form 
          :model="formData" 
          :rules="formRules" 
          ref="formRef" 
          label-width="100px"
        >
          <el-form-item label="规则名称" prop="ruleName">
            <el-input 
              v-model="formData.ruleName" 
              placeholder="请输入规则名称，如：真人脸部识别"
              maxlength="50"
              show-word-limit
            />
          </el-form-item>
          <el-form-item label="规则描述" prop="ruleDescription">
            <el-input 
              v-model="formData.ruleDescription" 
              type="textarea" 
              placeholder="请详细描述违禁情况，如：商品图片中不能出现真人的脸部"
              :rows="4"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
        </el-form>
        
        <template #footer>
          <el-button @click="cancelForm">取消</el-button>
          <el-button 
            type="primary" 
            @click="submitForm"
            :loading="submitting"
          >
            {{ isEditing ? '更新' : '添加' }}
          </el-button>
        </template>
      </el-dialog>
    </el-dialog>
  </template>
  
  <script setup>
  import { ref, reactive } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { Plus, Refresh } from '@element-plus/icons-vue'
  import { 
    getAllProhibitedRulesApi,
    createProhibitedRuleApi,
    updateProhibitedRuleApi,
    deleteProhibitedRuleApi,
    updateProhibitedRuleStatusApi
  } from '../api/admin'
  
  const visible = ref(false)
  const rules = ref([])
  const loading = ref(false)
  const updatingRuleId = ref(null)
  const deletingRuleId = ref(null)
  
  // 表单相关
  const showFormDialog = ref(false)
  const isEditing = ref(false)
  const submitting = ref(false)
  const formRef = ref(null)
  const formData = reactive({
    ruleId: null,
    ruleName: '',
    ruleDescription: ''
  })
  
  const formRules = {
    ruleName: [
      { required: true, message: '请输入规则名称', trigger: 'blur' },
      { min: 2, max: 50, message: '规则名称长度在 2 到 50 个字符', trigger: 'blur' }
    ],
    ruleDescription: [
      { required: true, message: '请输入规则描述', trigger: 'blur' },
      { min: 5, max: 200, message: '规则描述长度在 5 到 200 个字符', trigger: 'blur' }
    ]
  }
  
  // 获取所有违禁规则
  const fetchRules = async () => {
    loading.value = true
    try {
      const response = await getAllProhibitedRulesApi()
      
      // 处理不同的响应格式
      if (response && response.success && response.data) {
        rules.value = Array.isArray(response.data) ? response.data : []
      } else if (Array.isArray(response)) {
        rules.value = response
      } else if (response && response.data) {
        rules.value = Array.isArray(response.data) ? response.data : []
      } else {
        console.warn('意外的响应格式:', response)
        rules.value = []
      }
      
      console.log('获取到违禁规则列表:', rules.value.length, '条规则')
    } catch (error) {
      console.error('获取违禁规则列表失败:', error)
      ElMessage.error('获取违禁规则列表失败：' + (error.message || '未知错误'))
      rules.value = []
    } finally {
      loading.value = false
    }
  }
  
  // 显示添加对话框
  const showAddDialog = () => {
    isEditing.value = false
    formData.ruleId = null
    formData.ruleName = ''
    formData.ruleDescription = ''
    showFormDialog.value = true
  }
  
  // 编辑规则
  const editRule = (rule) => {
    isEditing.value = true
    formData.ruleId = rule.ruleId
    formData.ruleName = rule.ruleName
    formData.ruleDescription = rule.ruleDescription
    showFormDialog.value = true
  }
  
  // 取消表单
  const cancelForm = () => {
    showFormDialog.value = false
    if (formRef.value) {
      formRef.value.resetFields()
    }
  }
  
  // 提交表单
  const submitForm = async () => {
    if (!formRef.value) return
    
    try {
      await formRef.value.validate()
      submitting.value = true
      
      if (isEditing.value) {
        // 更新规则
        await updateProhibitedRuleApi(formData.ruleId, formData.ruleName, formData.ruleDescription)
        ElMessage.success('更新违禁规则成功')
      } else {
        // 创建规则
        await createProhibitedRuleApi(formData.ruleName, formData.ruleDescription)
        ElMessage.success('添加违禁规则成功')
      }
      
      showFormDialog.value = false
      await fetchRules() // 重新获取列表
      
    } catch (error) {
      if (error === 'cancel') {
        return // 表单验证失败
      }
      console.error('提交表单失败:', error)
      ElMessage.error('操作失败：' + (error.message || '未知错误'))
    } finally {
      submitting.value = false
    }
  }
  
  // 更新规则状态
  const updateRuleStatus = async (rule) => {
    updatingRuleId.value = rule.ruleId
    try {
      await updateProhibitedRuleStatusApi(rule.ruleId, rule.isActive)
      const status = rule.isActive ? '启用' : '禁用'
      ElMessage.success(`${status}规则成功`)
    } catch (error) {
      // 恢复原状态
      rule.isActive = !rule.isActive
      console.error('更新规则状态失败:', error)
      ElMessage.error('更新规则状态失败：' + (error.message || '未知错误'))
    } finally {
      updatingRuleId.value = null
    }
  }
  
  // 删除规则
  const deleteRule = async (rule) => {
    try {
      await ElMessageBox.confirm(
        `确定要删除规则"${rule.ruleName}"吗？删除后无法恢复。`,
        '删除确认',
        {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'error',
        }
      )
      
      deletingRuleId.value = rule.ruleId
      
      await deleteProhibitedRuleApi(rule.ruleId)
      
      ElMessage.success('删除违禁规则成功')
      
      // 重新获取规则列表
      await fetchRules()
      
    } catch (error) {
      if (error === 'cancel') {
        ElMessage.info('已取消删除')
      } else {
        console.error('删除违禁规则失败:', error)
        ElMessage.error('删除违禁规则失败：' + (error.message || '未知错误'))
      }
    } finally {
      deletingRuleId.value = null
    }
  }
  
  // 格式化日期
  const formatDate = (dateString) => {
    if (!dateString) return ''
    try {
      return new Date(dateString).toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    } catch (error) {
      return dateString
    }
  }
  
  defineExpose({ visible })
  </script>
  
  <style scoped>
  .rules-header-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding: 0 4px;
  }
  
  .rules-count {
    color: #666;
    font-size: 14px;
  }
  </style> 