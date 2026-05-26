import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'
import { message } from 'ant-design-vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)
  const loading = ref(false)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')
  const isActive = computed(() => userInfo.value?.status === 'ACTIVE')
  const username = computed(() => userInfo.value?.username || userInfo.value?.name || '')
  const avatar = computed(() => userInfo.value?.avatar || '')
  const userRole = computed(() => userInfo.value?.role || '')

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const clearToken = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('user', JSON.stringify(info))
  }

  const loginAction = async (credentials) => {
    loading.value = true
    try {
      const res = await authApi.login(credentials)
      if (res.code === 200) {
        const { token: jwtToken, ...info } = res.data
        setToken(jwtToken)
        setUserInfo(info)
        message.success('登录成功')
        return true
      }
      return false
    } catch (error) {
      return false
    } finally {
      loading.value = false
    }
  }

  const registerAction = async (data) => {
    loading.value = true
    try {
      const res = await authApi.register(data)
      if (res.code === 200) {
        const { token: jwtToken, ...info } = res.data
        setToken(jwtToken)
        setUserInfo(info)
        message.success('注册成功')
        return true
      }
      return false
    } catch (error) {
      return false
    } finally {
      loading.value = false
    }
  }

  const fetchUserInfo = async () => {
    if (!token.value) return false
    try {
      const res = await authApi.getCurrentUser()
      if (res.code === 200) {
        setUserInfo(res.data)
        return true
      }
      return false
    } catch (error) {
      clearToken()
      return false
    }
  }

  const logout = () => {
    clearToken()
    message.success('已退出登录')
  }

  const checkPermission = (requiredRole) => {
    if (!isLoggedIn.value) return false
    if (!isActive.value) return false
    if (requiredRole === 'ADMIN') {
      return isAdmin.value
    }
    return true
  }

  const init = async () => {
    if (token.value) {
      const savedUser = localStorage.getItem('user')
      if (savedUser) {
        try {
          userInfo.value = JSON.parse(savedUser)
        } catch (e) {
          // ignore error
        }
      }
      // Always fetch fresh user info
      await fetchUserInfo()
    }
  }

  return {
    token,
    userInfo,
    loading,
    isLoggedIn,
    isAdmin,
    isActive,
    username,
    avatar,
    userRole,
    setToken,
    clearToken,
    setUserInfo,
    loginAction,
    registerAction,
    fetchUserInfo,
    logout,
    checkPermission,
    init
  }
})
