import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(parseInt(localStorage.getItem('userId')) || null)
  const username = ref(localStorage.getItem('username') || '')
  const avatar = ref(localStorage.getItem('avatar') || '')
  
  const isLoggedIn = computed(() => !!token.value)
  
  function setUserInfo(userInfo) {
    token.value = userInfo.token || ''
    userId.value = userInfo.userId || null
    username.value = userInfo.username || ''
    avatar.value = userInfo.avatar || ''
    
    localStorage.setItem('token', token.value)
    localStorage.setItem('userId', userId.value)
    localStorage.setItem('username', username.value)
    localStorage.setItem('avatar', avatar.value)
  }
  
  function clearUserInfo() {
    token.value = ''
    userId.value = null
    username.value = ''
    avatar.value = ''
    
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    localStorage.removeItem('avatar')
  }
  
  return {
    token,
    userId,
    username,
    avatar,
    isLoggedIn,
    setUserInfo,
    clearUserInfo
  }
}) 