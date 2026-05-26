import request from '@/utils/request'
import type { ApiResponse, PageResponse, Video } from '@/types'

export const favoriteApi = {
  getList(page = 1, size = 20): Promise<ApiResponse<PageResponse<Video>>> {
    return request.get('/favorites', { params: { page, size } })
  },

  add(videoId: number): Promise<ApiResponse<void>> {
    return request.post(`/favorites/${videoId}`)
  },

  remove(videoId: number): Promise<ApiResponse<void>> {
    return request.delete(`/favorites/${videoId}`)
  },

  isFavorited(videoId: number): Promise<ApiResponse<boolean>> {
    return request.get(`/favorites/${videoId}/status`)
  }
}

export default favoriteApi
