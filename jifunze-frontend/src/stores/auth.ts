import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios, { AxiosError } from 'axios'

interface User {
  id: number
  fullName: string
  phoneNumber: string
  role: string
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const isAuthenticated = ref(false)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const router = useRouter()

  // Computed properties
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const isInstructor = computed(() => user.value?.role === 'INSTRUCTOR')
  const isStudent = computed(() => user.value?.role === 'STUDENT')

  // Configure axios defaults for JWT + cookie authentication
  axios.defaults.baseURL = 'http://localhost:8080' // Updated to match backend port
  axios.defaults.withCredentials = true // Important for cookie-based auth

  // Login function with role-based redirection
  const login = async (phoneNumber: string, password: string) => {
    loading.value = true
    error.value = null

    try {
      const response = await axios.post('/api/auth/login', {
        phoneNumber,
        password
      })

      if (response.data.status === 'success') {
        // Set user data from login response
        user.value = {
          id: response.data.userId,
          fullName: response.data.fullName,
          phoneNumber: response.data.phoneNumber,
          role: response.data.role
        }
        isAuthenticated.value = true

        // Role-based redirection
        await redirectBasedOnRole(response.data.role)

        return { success: true }
      } else {
        error.value = response.data.message
        return { success: false, error: response.data.message }
      }
    } catch (err: unknown) {
      const errorMessage = err instanceof AxiosError
        ? err.response?.data?.message || err.message
        : err instanceof Error
          ? err.message
          : 'Login failed'
      error.value = errorMessage
      return { success: false, error: errorMessage }
    } finally {
      loading.value = false
    }
  }

  // Role-based redirection function
  const redirectBasedOnRole = async (role: string) => {
    switch (role) {
      case 'ADMIN':
        await router.push('/admin')
        break
      case 'INSTRUCTOR':
        await router.push('/instructor')
        break
      case 'STUDENT':
        await router.push('/student')
        break
      default:
        await router.push('/')
    }
  }

  // Logout function - clears cookies and user state
  const logout = async () => {
    try {
      await axios.post('/api/auth/logout')
    } catch (err) {
      console.error('Logout error:', err)
    } finally {
      // Clear user state from memory
      user.value = null
      isAuthenticated.value = false
      await router.push('/login')
    }
  }

  // Fetch user profile from the server
  const fetchUserProfile = async () => {
    try {
      // Since we don't have a profile endpoint, we'll use the login response data
      // The user data is already available from the login response
      if (user.value) {
        isAuthenticated.value = true
        return true
      }
    } catch (err: unknown) {
      // User is not authenticated
      user.value = null
      isAuthenticated.value = false
      return false
    }
    return false
  }

  // Check authentication status using profile endpoint
  const checkAuthStatus = async () => {
    return await fetchUserProfile()
  }

  // Refresh token function
  const refreshToken = async () => {
    try {
      // Get refresh token from cookie (handled automatically by browser)
      const response = await axios.post('/api/auth/refresh', {})

      if (response.data.status === 'success') {
        // Token refreshed successfully, cookies are automatically updated
        return { success: true }
      } else {
        // Refresh failed, redirect to login
        await logout()
        return { success: false }
      }
    } catch (err) {
      // Refresh failed, redirect to login
      await logout()
      return { success: false }
    }
  }

  // Register function
  const register = async (userData: {
    fullName: string
    phoneNumber: string
    password: string
    role?: string
  }) => {
    loading.value = true
    error.value = null

    try {
      // Use student registration endpoint for student registration
      const endpoint = userData.role === 'STUDENT' ? '/api/students/register' : '/api/auth/register'
      const response = await axios.post(endpoint, userData)

      if (response.data.status === 'success') {
        return { success: true, message: 'Registration successful' }
      } else {
        error.value = response.data.message
        return { success: false, error: response.data.message }
      }
    } catch (err: unknown) {
      const errorMessage = err instanceof AxiosError
        ? err.response?.data?.message || err.message
        : err instanceof Error
          ? err.message
          : 'Registration failed'
      error.value = errorMessage
      return { success: false, error: errorMessage }
    } finally {
      loading.value = false
    }
  }

  // Initialize auth status on app start
  const initializeAuth = async () => {
    await checkAuthStatus()
  }

  return {
    user,
    isAuthenticated,
    loading,
    error,
    isAdmin,
    isInstructor,
    isStudent,
    login,
    logout,
    register,
    checkAuthStatus,
    fetchUserProfile,
    refreshToken,
    initializeAuth,
    redirectBasedOnRole
  }
})
