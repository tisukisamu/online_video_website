import request from '@/utils/request'
import type { ApiResponse, AuthResponse, LoginRequest, RegisterRequest } from '@/types'

export const authApi = {
  login(data: LoginRequest): Promise<ApiResponse<AuthResponse>> {
    return request.post('/auth/login', data)
  },

  register(data: RegisterRequest): Promise<ApiResponse<AuthResponse>> {
    return request.post('/auth/register', data)
  },

  getCurrentUser(): Promise<ApiResponse<AuthResponse>> {
    return request.get('/auth/me')
  },

  logout(): void {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }
}

export default authApi
