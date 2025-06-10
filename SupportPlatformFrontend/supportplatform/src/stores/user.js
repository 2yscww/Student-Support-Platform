import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || null)
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo')) || null)
  const userProfile = ref(null);
  
  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')
  const isUser = computed(() => userInfo.value?.role === 'USER')
  
  // 方法
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }
  
  const setUserInfo = (info) => {
    userInfo.value = info
    if (info) {
      localStorage.setItem('userInfo', JSON.stringify(info))
    } else {
      localStorage.removeItem('userInfo')
    }
  }
  
  const fetchUserProfile = async () => {
    try {
      const response = await axios.get('/api/user/profile', {
        headers: {
          'Authorization': `Bearer ${token.value}`
        }
      });
      if (response.data && response.data.code === 200) {
        userProfile.value = response.data.data;
      } else {
        console.error('Failed to fetch user profile:', response.data.message);
      }
    } catch (error) {
      console.error('Failed to fetch user profile:', error);
      throw error;
    }
  };
  
  const login = async (credentials) => {
    try {
      const response = await axios.post('/api/user/login', credentials)
      if (response.data && response.data.code === 200) {
        const { token: newToken, ...userInfoPayload } = response.data.data
        setToken(newToken)
        setUserInfo(userInfoPayload)
        return true
      } else {
        console.error('Login failed:', response.data.message)
        return false
      }
    } catch (error) {
      console.error('Login failed:', error)
      return false
    }
  }
  
  const logout = async () => {
    try {
      await axios.post('/api/auth/logout')
    } catch (error) {
      console.error('Logout failed:', error)
    } finally {
      token.value = null
      userInfo.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
  
  // const fetchUserInfo = async () => { // Removing this function
  //   try {
  //     const response = await axios.get('/api/users/me')
  //     setUserInfo(response.data)
  //   } catch (error) {
  //     console.error('Failed to fetch user info:', error)
  //   }
  // }
  
  // If a token exists in localStorage, try to fetch user info
  // if (token.value) { // Removing this block
  //   fetchUserInfo()
  // }

  return {
    // 状态
    token,
    userInfo,
    userProfile,
    // 计算属性
    isLoggedIn,
    isAdmin,
    isUser,
    // 方法
    login,
    logout,
    setToken,
    setUserInfo,
    fetchUserProfile
    // fetchUserInfo // Removing this from returned methods
  }
}) 

// Immediately fetch user info if a token exists
// const userStore = useUserStore() // This line and below will be removed
// if (userStore.token) {
// userStore.fetchUserInfo()
// } 