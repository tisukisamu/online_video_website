import request from '@/utils/request'
import type { ApiResponse, PageResponse, User, Video, UserUpdateRequest } from '@/types'

export const userApi = {
  getProfile(id: number): Promise<ApiResponse<User>> {
    return request.get(`/users/${id}`)
  },

  getCurrentUser(): Promise<ApiResponse<User>> {
    return request.get('/users/me')
  },

  updateProfile(data: UserUpdateRequest): Promise<ApiResponse<User>> {
    return request.put('/users/me', data)
  },

  getMyVideos(page = 1, size = 20): Promise<ApiResponse<PageResponse<Video>>> {
    return request.get('/users/me/videos', { params: { page, size } })
  }
}

export default userApi
