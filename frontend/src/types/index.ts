export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp?: string
}

export interface PageResponse<T> {
  list: T[]
  pagination: Pagination
}

export interface Pagination {
  page: number
  size: number
  total: number
  totalPages: number
}

export interface User {
  id: number
  username: string
  name: string
  email: string
  avatar: string
  bio?: string
  role: 'ADMIN' | 'USER'
  status: 'ACTIVE' | 'DISABLED'
  createdAt: string
  videoCount?: number
  followerCount?: number
  followingCount?: number
  likeCount?: number
  isFollowing?: boolean
}

export interface Video {
  id: number
  userId: number
  title: string
  description: string
  coverUrl: string
  videoUrl: string
  duration: number
  fileSize: number
  status: 'DRAFT' | 'PUBLISHED' | 'DELETED'
  visibility: 'PUBLIC' | 'FOLLOWERS' | 'PRIVATE'
  viewCount: number
  likeCount: number
  commentCount: number
  shareCount: number
  favoriteCount: number
  createdAt: string
  updatedAt: string
  author?: User
  isLiked?: boolean
  isFavorited?: boolean
}

export interface Comment {
  id: number
  videoId: number
  userId: number
  parentId?: number
  rootId?: number
  content: string
  likeCount: number
  replyCount: number
  status: 'NORMAL' | 'DELETED'
  createdAt: string
  updatedAt: string
  author?: User
  isLiked?: boolean
  replies?: Comment[]
}

export interface UploadResult {
  url: string
  fileName: string
  fileSize: number
}

export interface AuthResponse {
  token: string
  type: string
  id: number
  username: string
  email: string
  name: string
  role: 'ADMIN' | 'USER'
  status: 'ACTIVE' | 'DISABLED'
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  name: string
  email?: string
}

export interface VideoCreateRequest {
  title: string
  description?: string
  coverUrl?: string
  videoUrl: string
  duration?: number
  fileSize?: number
  visibility?: 'PUBLIC' | 'FOLLOWERS' | 'PRIVATE'
}

export interface VideoUpdateRequest {
  title?: string
  description?: string
  coverUrl?: string
  visibility?: 'PUBLIC' | 'FOLLOWERS' | 'PRIVATE'
}

export interface CommentCreateRequest {
  videoId: number
  content: string
  parentId?: number
}

export interface Message {
  id: number
  senderId: number
  receiverId: number
  content: string
  createdAt: string
}

export interface MessageCreateRequest {
  receiverId: number
  content: string
}

export interface UserUpdateRequest {
  name?: string
  email?: string
  avatar?: string
  bio?: string
}

export interface DashboardStats {
  userCount: number
  videoCount: number
  commentCount: number
  todayNewUsers: number
  todayNewVideos: number
}
