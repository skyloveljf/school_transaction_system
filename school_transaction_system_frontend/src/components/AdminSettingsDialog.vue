<template>
  <el-dialog v-model="visible" title="平台设置" width="650px" :close-on-click-modal="false" class="admin-settings-dialog">
    <div class="dialog-content-wrapper">
      <el-form :model="settings" label-width="120px" class="settings-form">
        <el-tabs v-model="activeTab" class="settings-tabs">
          <el-tab-pane label="基础设置" name="basic" class="settings-tab-pane">
            <el-form-item label="平台名称">
              <el-input v-model="settings.platformName" placeholder="例如：校园二手市场" clearable />
            </el-form-item>
            <el-form-item label="维护模式">
              <el-switch v-model="settings.maintenanceMode" />
              <span class="setting-tip">开启后，普通用户将无法访问主要功能。</span>
            </el-form-item>
            <el-form-item label="新用户注册">
              <el-radio-group v-model="settings.registration">
                <el-radio label="open">开放注册</el-radio>
                <el-radio label="invite">仅邀请码注册</el-radio>
                <el-radio label="closed">关闭注册</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-tab-pane>

          <el-tab-pane label="内容审核" name="moderation" class="settings-tab-pane">
            <el-form-item label="敏感词过滤">
              <el-switch v-model="settings.keywordFilter" />
            </el-form-item>
            <el-form-item label="敏感词列表">
              <el-input
                v-model="settings.keywords"
                type="textarea"
                :rows="3"
                placeholder="每行一个敏感词"
                :disabled="!settings.keywordFilter"
                clearable
              />
            </el-form-item>
            <el-form-item label="自动审核商品">
              <el-switch v-model="settings.autoApproveProducts" />
              <span class="setting-tip">关闭后，新发布的商品需要手动审核。</span>
            </el-form-item>
          </el-tab-pane>

          <el-tab-pane label="通知设置" name="notifications" class="settings-tab-pane">
              <el-form-item label="管理员邮箱">
                  <el-input v-model="settings.adminEmail" placeholder="接收重要通知的邮箱" clearable />
              </el-form-item>
              <el-form-item label="新用户注册通知">
                  <el-switch v-model="settings.notifyNewUser" />
              </el-form-item>
              <el-form-item label="商品举报通知">
                  <el-switch v-model="settings.notifyReport" />
              </el-form-item>
          </el-tab-pane>
        </el-tabs>
      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false" class="footer-btn">取消</el-button>
        <el-button type="primary" @click="saveSettings" class="footer-btn save-btn">保存设置</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const visible = ref(false)
const activeTab = ref('basic')

// 模拟设置数据，实际应用中应从后端加载和保存
const settings = reactive({
  platformName: '校园二手交易平台',
  maintenanceMode: false,
  registration: 'open', // open, invite, closed
  keywordFilter: true,
  keywords: '违禁词1\n敏感内容\n广告词',
  autoApproveProducts: false,
  adminEmail: 'admin@example.com',
  notifyNewUser: true,
  notifyReport: true,
})

const saveSettings = () => {
  // 在这里添加保存设置到后端的逻辑
  console.log('保存设置:', settings)
  ElMessage.success('设置已保存！')
  visible.value = false
}

defineExpose({ visible })
</script>

<style scoped>
/* 对话框全局样式调整 */
.admin-settings-dialog :deep(.el-dialog__body) {
  padding: 0; /* 移除默认的 body padding，由 .dialog-content-wrapper 控制 */
}
.admin-settings-dialog :deep(.el-dialog__header) {
  padding: 20px 24px 10px;
  margin-right: 0; /* 覆盖 Element Plus 可能的 margin */
  border-bottom: 1px solid #e0e7ff; /* 统一的分割线颜色 */
  background-color: #f8fafc; /* 轻微的头部背景色 */
  border-top-left-radius: 14px; /* 配合 dialog 圆角 */
  border-top-right-radius: 14px;
}
.admin-settings-dialog :deep(.el-dialog__title) {
  color: #6366f1; /* 统一的标题颜色 */
  font-weight: 600;
}

.dialog-content-wrapper {
  padding: 20px 24px; /* 内容区域的内边距 */
  background-color: #fdfdff; /* 比页面主背景稍亮一点的背景 */
}

.settings-form {
  /* 可以给表单整体一个背景和圆角，类似 UserProfile.vue 中的 .profile-el-form */
  background: rgba(255,255,255,0.98);
  border-radius: 14px;
  box-shadow: 0 2px 12px #a1c4fd18; /* 更柔和的阴影 */
  padding: 24px 20px 10px 20px;
  border: 1px solid #e0e7ff;
}

.settings-tabs {
  /* 可以调整标签页样式以匹配 UserHome.vue 中的 .custom-tabs */
  --el-tabs-header-height: 42px;
}

.settings-tabs :deep(.el-tabs__item) {
  font-weight: 500;
  color: #606266;
}
.settings-tabs :deep(.el-tabs__item.is-active) {
  color: #6366f1; /* 激活标签颜色 */
}
.settings-tabs :deep(.el-tabs__active-bar) {
  background-color: #6366f1; /* 激活指示条颜色 */
}
.settings-tabs :deep(.el-tabs__nav-wrap::after) {
  background-color: #e0e7ff; /* 底部线条颜色 */
}


.settings-tab-pane {
  padding-top: 15px; /* 标签内容区域的上边距 */
  padding-bottom: 5px;
}

/* 表单项样式 */
.settings-form :deep(.el-form-item__label) {
  color: #3730a3; /* 标签文字颜色 */
  font-weight: 500;
}

.settings-form :deep(.el-input__wrapper),
.settings-form :deep(.el-textarea__inner) {
  background: #f8fafc; /* 输入框背景 */
  border-radius: 8px; /* 输入框圆角 */
  border: 1px solid #e0e7ff;
  box-shadow: 0 1px 4px #e0e7ff22; /* 轻微内阴影 */
}
.settings-form :deep(.el-input__wrapper:focus-within),
.settings-form :deep(.el-textarea__inner:focus) {
  border-color: #6366f1;
  box-shadow: 0 0 0 2px #6366f133; /* 模拟 focus 效果 */
}

.setting-tip {
  font-size: 12px;
  color: #909399;
  margin-left: 10px;
  line-height: normal; /* 确保与 switch 对齐 */
}

/* 底部按钮区域 */
.dialog-footer {
  padding: 15px 24px;
  border-top: 1px solid #e0e7ff;
  text-align: right;
  background-color: #f8fafc;
  border-bottom-left-radius: 14px; /* 配合 dialog 圆角 */
  border-bottom-right-radius: 14px;
}
.footer-btn {
  border-radius: 8px; /* 按钮圆角 */
  font-weight: 500;
}
.save-btn {
  background: linear-gradient(90deg, #a1c4fd 0%, #6366f1 100%);
  border: none;
  color: white;
}
.save-btn:hover {
  background: linear-gradient(90deg, #8aaefb 0%, #5254e0 100%);
}

</style>