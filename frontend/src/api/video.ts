import request from '@/utils/request'
import type { ApiResponse, PageResponse, Video, VideoCreateRequest, VideoUpdateRequest } from '@/types'

export const videoApi = {
  getList(page = 1, size = 20): Promise<ApiResponse<PageResponse<Video>>> {
    return request.get('/videos', { params: { page, size } })
  },

  getRecommend(page = 1, size = 10): Promise<ApiResponse<PageResponse<Video>>> {
    return request.get('/videos/recommend', { params: { page, size } })
  },

  getFollowVideos(page = 1, size = 10): Promise<ApiResponse<PageResponse<Video>>> {
    return request.get('/videos/follow', { params: { page, size } })
  },

  search(keyword: string, page = 1, size = 20): Promise<ApiResponse<PageResponse<Video>>> {
    return request.get('/videos/search', { params: { keyword, page, size } })
  },

  getDetail(id: number): Promise<ApiResponse<Video>> {
    return request.get(`/videos/${id}`)
  },

  create(data: VideoCreateRequest): Promise<ApiResponse<Video>> {
    return request.post('/videos', data)
  },

  update(id: number, data: VideoUpdateRequest): Promise<ApiResponse<Video>> {
    return request.put(`/videos/${id}`, data)
  },

  delete(id: number): Promise<ApiResponse<void>> {
    return request.delete(`/videos/${id}`)
  },

  recordView(id: number): Promise<ApiResponse<void>> {
    return request.post(`/videos/${id}/view`)
  },

  like(id: number): Promise<ApiResponse<void>> {
    return request.post(`/videos/${id}/like`)
  },

  unlike(id: number): Promise<ApiResponse<void>> {
    return request.delete(`/videos/${id}/like`)
  },

  getUserVideos(userId: number, page = 1, size = 20): Promise<ApiResponse<PageResponse<Video>>> {
    return request.get(`/videos/user/${userId}`, { params: { page, size } })
  }
}

export default videoApi
