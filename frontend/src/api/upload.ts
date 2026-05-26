import request from '@/utils/request'
import type { ApiResponse, UploadResult } from '@/types'

export const uploadApi = {
  uploadVideo(file: File): Promise<ApiResponse<UploadResult>> {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/upload/video', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  uploadImage(file: File): Promise<ApiResponse<UploadResult>> {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/upload/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  uploadAvatar(file: File): Promise<ApiResponse<UploadResult>> {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/upload/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

export default uploadApi
