import request from '@/utils/request'
import type { ApiResponse, PageResponse, User } from '@/types'

export const followApi = {
  follow(userId: number): Promise<ApiResponse<void>> {
    return request.post(`/follows/${userId}`)
  },

  unfollow(userId: number): Promise<ApiResponse<void>> {
    return request.delete(`/follows/${userId}`)
  },

  getFollowing(page = 1, size = 20): Promise<ApiResponse<PageResponse<User>>> {
    return request.get('/follows/following', { params: { page, size } })
  },

  getFollowers(page = 1, size = 20): Promise<ApiResponse<PageResponse<User>>> {
    return request.get('/follows/followers', { params: { page, size } })
  },

  getUserFollowing(userId: number, page = 1, size = 20): Promise<ApiResponse<PageResponse<User>>> {
    return request.get(`/follows/${userId}/following`, { params: { page, size } })
  },

  getUserFollowers(userId: number, page = 1, size = 20): Promise<ApiResponse<PageResponse<User>>> {
    return request.get(`/follows/${userId}/followers`, { params: { page, size } })
  },

  isFollowing(userId: number): Promise<ApiResponse<boolean>> {
    return request.get(`/follows/${userId}/status`)
  }
}

export default followApi
