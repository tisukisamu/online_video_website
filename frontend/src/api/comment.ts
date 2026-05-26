import request from '@/utils/request'
import type { ApiResponse, PageResponse, Comment, CommentCreateRequest } from '@/types'

export const commentApi = {
  getList(videoId: number, page = 1, size = 20): Promise<ApiResponse<PageResponse<Comment>>> {
    return request.get('/comments', { params: { videoId, page, size } })
  },

  getReplies(commentId: number, page = 1, size = 10): Promise<ApiResponse<PageResponse<Comment>>> {
    return request.get(`/comments/${commentId}/replies`, { params: { page, size } })
  },

  create(data: CommentCreateRequest): Promise<ApiResponse<Comment>> {
    return request.post('/comments', data)
  },

  delete(id: number): Promise<ApiResponse<void>> {
    return request.delete(`/comments/${id}`)
  },

  like(id: number): Promise<ApiResponse<void>> {
    return request.post(`/comments/${id}/like`)
  },

  unlike(id: number): Promise<ApiResponse<void>> {
    return request.delete(`/comments/${id}/like`)
  }
}

export default commentApi
