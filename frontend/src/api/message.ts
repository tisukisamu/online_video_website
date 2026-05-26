import request from '@/utils/request'
import type { ApiResponse, PageResponse, Message, MessageCreateRequest } from '@/types'

export const messageApi = {
  sendMessage(data: MessageCreateRequest): Promise<ApiResponse<Message>> {
    return request.post('/messages', data)
  },

  getConversation(userId: number, page = 1, size = 20): Promise<ApiResponse<PageResponse<Message>>> {
    return request.get('/messages', { params: { userId, page, size } })
  }
}

export default messageApi
