import request from '@/utils/request'
import type { ApiResponse, User, DashboardStats } from '@/types'

export const adminApi = {
  getAllUsers(): Promise<ApiResponse<User[]>> {
    return request.get('/admin/users')
  },

  updateUserStatus(id: number, status: 'ACTIVE' | 'DISABLED'): Promise<ApiResponse<User>> {
    return request.put(`/admin/users/${id}/status`, null, { params: { status } })
  },

  updateUserRole(id: number, role: 'ADMIN' | 'USER'): Promise<ApiResponse<User>> {
    return request.put(`/admin/users/${id}/role`, null, { params: { role } })
  },

  deleteUser(id: number): Promise<ApiResponse<void>> {
    return request.delete(`/admin/users/${id}`)
  },

  getDashboardStats(): Promise<ApiResponse<DashboardStats>> {
    return request.get('/admin/dashboard')
  },

  getVideos(params: { page?: number; size?: number; status?: string }): Promise<ApiResponse<any>> {
    return request.get('/admin/videos', { params })
  },

  auditVideo(id: number, status: 'PUBLISHED' | 'REJECTED' | 'BANNED', reason?: string): Promise<ApiResponse<any>> {
    return request.put(`/admin/videos/${id}/audit`, null, { params: { status, reason } })
  },

  getComments(params: { page?: number; size?: number }): Promise<ApiResponse<any>> {
    return request.get('/admin/comments', { params })
  },

  deleteComment(id: number): Promise<ApiResponse<void>> {
    return request.delete(`/admin/comments/${id}`)
  }
}


export default adminApi
