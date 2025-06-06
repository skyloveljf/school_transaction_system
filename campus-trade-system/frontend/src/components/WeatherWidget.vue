<template>
    <div class="weather-widget" @click="toggleDetails">
      <div class="weather-main">
        <div class="weather-icon">
          <el-icon :size="iconSize"><component :is="weatherIcon" /></el-icon>
        </div>
        <div class="weather-info">
          <div class="weather-temp">{{ weather.temperature }}°C</div>
          <div class="weather-desc" v-if="showDesc">{{ weather.weather }}</div>
        </div>
        <div class="weather-city" v-if="showCity">{{ weather.city }}</div>
      </div>
      
      <!-- 详细信息弹出框 -->
      <el-popover
        v-model:visible="showDetails"
        :width="280"
        placement="bottom"
        trigger="manual"
      >
        <template #reference>
          <div></div>
        </template>
        
        <div class="weather-details">
          <div class="weather-header">
            <h4>{{ weather.city }}天气</h4>
            <el-button @click="refreshWeather" :loading="loading" size="small" type="primary" link>
              <el-icon><Refresh /></el-icon>
            </el-button>
          </div>
          
          <div class="weather-content">
            <div class="weather-main-info">
              <div class="weather-large-icon">
                <el-icon :size="40"><component :is="weatherIcon" /></el-icon>
              </div>
              <div class="weather-temp-large">{{ weather.temperature }}°C</div>
              <div class="weather-desc-large">{{ weather.weather }}</div>
            </div>
            
            <el-divider />
            
            <div class="weather-extra-info">
              <div class="info-item">
                <span class="info-label">湿度:</span>
                <span class="info-value">{{ weather.humidity }}%</span>
              </div>
              <div class="info-item">
                <span class="info-label">风向:</span>
                <span class="info-value">{{ weather.winddirection }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">风力:</span>
                <span class="info-value">{{ weather.windpower }}级</span>
              </div>
              <div class="info-item">
                <span class="info-label">更新时间:</span>
                <span class="info-value">{{ formatTime(weather.reporttime) }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-popover>
    </div>
  </template>
  
  <script setup>
  import { ref, computed, onMounted, defineProps } from 'vue'
  import { Sunny, Cloudy, Refresh } from '@element-plus/icons-vue'
  import { getCurrentWeatherApi, getWeatherByCityNameApi } from '@/api/weather'
  import { ElMessage } from 'element-plus'
  
  // 定义组件属性
  const props = defineProps({
    cityName: {
      type: String,
      default: ''
    },
    compact: {
      type: Boolean,
      default: false
    },
    theme: {
      type: String,
      default: 'light', // light, dark
      validator: (value) => ['light', 'dark'].includes(value)
    }
  })
  
  // 响应式数据
  const weather = ref({
    city: '北京',
    weather: '晴',
    temperature: '22',
    humidity: '65',
    winddirection: '东南',
    windpower: '3',
    reporttime: new Date().toISOString()
  })
  
  const loading = ref(false)
  const showDetails = ref(false)
  
  // 计算属性
  const iconSize = computed(() => props.compact ? 16 : 20)
  const showDesc = computed(() => !props.compact)
  const showCity = computed(() => !props.compact)
  
  // 根据天气状况选择图标
  const weatherIcon = computed(() => {
    const weatherType = weather.value.weather
    if (weatherType.includes('晴')) return Sunny
    if (weatherType.includes('云') || weatherType.includes('阴')) return Cloudy
    return Sunny // 默认图标
  })
  
  // 获取天气数据
  const fetchWeather = async () => {
    try {
      loading.value = true
      let response
      
      if (props.cityName) {
        response = await getWeatherByCityNameApi(props.cityName)
      } else {
        response = await getCurrentWeatherApi()
      }
      
      console.log('天气API响应:', response)
      
      // 处理响应数据
      if (response && response.success && response.data) {
        weather.value = response.data
      } else if (response && response.city) {
        weather.value = response
      } else {
        console.warn('天气API返回格式异常:', response)
        ElMessage.warning('天气数据获取异常，显示默认数据')
      }
      
    } catch (error) {
      console.error('获取天气数据失败:', error)
      ElMessage.error('获取天气数据失败')
    } finally {
      loading.value = false
    }
  }
  
  // 刷新天气数据
  const refreshWeather = () => {
    fetchWeather()
  }
  
  // 切换详情显示
  const toggleDetails = () => {
    showDetails.value = !showDetails.value
  }
  
  // 格式化时间
  const formatTime = (timeStr) => {
    if (!timeStr) return ''
    
    try {
      const date = new Date(timeStr)
      if (isNaN(date.getTime())) {
        return timeStr.slice(0, 16) // 如果无法解析，返回前16个字符
      }
      
      const now = new Date()
      const diff = now.getTime() - date.getTime()
      const minutes = Math.floor(diff / 60000)
      
      if (minutes < 1) return '刚刚'
      if (minutes < 60) return `${minutes}分钟前`
      
      const hours = Math.floor(minutes / 60)
      if (hours < 24) return `${hours}小时前`
      
      return date.toLocaleString('zh-CN', {
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    } catch (error) {
      return timeStr
    }
  }
  
  // 组件挂载时获取天气数据
  onMounted(() => {
    fetchWeather()
    
    // 设置定时刷新（每30分钟）
    setInterval(() => {
      fetchWeather()
    }, 30 * 60 * 1000)
  })
  </script>
  
  <style scoped>
  .weather-widget {
    cursor: pointer;
    transition: all 0.3s ease;
    user-select: none;
  }
  
  .weather-widget:hover {
    opacity: 0.8;
  }
  
  .weather-main {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  
  .weather-icon {
    color: #f39c12;
    display: flex;
    align-items: center;
  }
  
  .weather-info {
    display: flex;
    flex-direction: column;
    align-items: center;
    min-width: 50px;
  }
  
  .weather-temp {
    font-size: 14px;
    font-weight: 600;
    line-height: 1;
  }
  
  .weather-desc {
    font-size: 12px;
    color: #666;
    line-height: 1;
    margin-top: 2px;
  }
  
  .weather-city {
    font-size: 12px;
    color: #888;
  }
  
  /* 详细信息样式 */
  .weather-details {
    padding: 0;
  }
  
  .weather-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
  }
  
  .weather-header h4 {
    margin: 0;
    font-size: 16px;
    color: #303133;
  }
  
  .weather-main-info {
    text-align: center;
    margin-bottom: 16px;
  }
  
  .weather-large-icon {
    color: #f39c12;
    margin-bottom: 8px;
  }
  
  .weather-temp-large {
    font-size: 24px;
    font-weight: bold;
    color: #303133;
    margin-bottom: 4px;
  }
  
  .weather-desc-large {
    font-size: 14px;
    color: #666;
  }
  
  .weather-extra-info {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 8px;
  }
  
  .info-item {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    padding: 4px 0;
  }
  
  .info-label {
    color: #666;
  }
  
  .info-value {
    color: #303133;
    font-weight: 500;
  }
  
  /* 主题样式 */
  .weather-widget.dark .weather-temp,
  .weather-widget.dark .weather-city {
    color: #fff;
  }
  
  .weather-widget.dark .weather-desc {
    color: #ccc;
  }
  
  :deep(.el-divider) {
    margin: 12px 0;
  }
  </style> 