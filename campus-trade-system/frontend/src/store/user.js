import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(parseInt(localStorage.getItem('userId')) || null)
  const username = ref(localStorage.getItem('username') || '')
  const avatar = ref(localStorage.getItem('avatar') || '')
  const role = ref(localStorage.getItem('role') || '')
  
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => role.value === 'ROLE_ADMIN')
  
  function setUserInfo(userInfo) {
    token.value = userInfo.token || ''
    userId.value = userInfo.userId || null
    username.value = userInfo.username || ''
    avatar.value = userInfo.avatar || ''
    role.value = userInfo.role || ''
    
    localStorage.setItem('token', token.value)
    localStorage.setItem('userId', userId.value)
    localStorage.setItem('username', username.value)
    localStorage.setItem('avatar', avatar.value)
    localStorage.setItem('role', role.value)
  }
  
  function clearUserInfo() {
    token.value = ''
    userId.value = null
    username.value = ''
    avatar.value = ''
    role.value = ''
    
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    localStorage.removeItem('avatar')
    localStorage.removeItem('role')
  }
  
  return {
    token,
    userId,
    username,
    avatar,
    role,
    isLoggedIn,
    isAdmin,
    setUserInfo,
    clearUserInfo
  }
}) 