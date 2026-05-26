<template>
  <div class="min-h-screen bg-black text-white pt-[60px]">
    <div class="max-w-[1400px] mx-auto flex flex-col lg:flex-row h-[calc(100vh-60px)]">
      <div class="flex-1 bg-black flex items-center justify-center relative group">
        <div class="absolute inset-0 bg-cover bg-center blur-3xl opacity-20" :style="{ backgroundImage: `url(${video?.coverUrl})` }"></div>
        <div class="relative w-full h-full flex items-center justify-center p-4">
          <video
            v-if="video?.videoUrl"
            ref="videoRef"
            :src="video.videoUrl"
            :poster="video.coverUrl"
            controls
            autoplay
            class="max-w-full max-h-full rounded-lg shadow-2xl"
            @play="recordView"
          ></video>
          <div v-else class="text-white text-opacity-50 flex flex-col items-center">
            <video-camera-outlined class="text-6xl mb-4" />
            <span>视频加载失败或不存在</span>
          </div>
        </div>
      </div>
      
      <div class="w-full lg:w-[400px] bg-[#161823] flex flex-col border-l border-white/5 h-full">
        <div class="p-4 border-b border-white/5 flex items-center justify-between">
          <div class="flex items-center gap-3 cursor-pointer" @click="$router.push(`/user/${video?.userId}`)">
            <a-avatar :size="40" :src="resolveAvatarUrl(video?.author?.avatar) || DEFAULT_AVATAR" class="border border-white/10" />
            <div class="flex flex-col">
              <span class="text-white font-medium text-sm hover:underline">{{ video?.author?.name }}</span>
              <span class="text-gray-500 text-xs">{{ formatCount(video?.author?.followerCount) }} 粉丝</span>
            </div>
          </div>
          <button 
            v-if="!isOwner"
            class="px-6 py-1.5 rounded-full text-sm font-medium transition-all duration-300"
            :class="isFollowing ? 'bg-[#3a3a44] text-white hover:bg-[#4a4a55]' : 'bg-[#fe2c55] text-white hover:bg-[#e62548]'"
            @click="toggleFollow"
          >
            {{ isFollowing ? '已关注' : '关注' }}
          </button>
        </div>

        <div class="flex-1 overflow-y-auto custom-scrollbar">
          <div class="p-4 border-b border-white/5">
            <h1 class="text-white text-lg font-medium mb-2 leading-snug">{{ video?.title }}</h1>
            <div class="text-gray-400 text-sm mb-4 whitespace-pre-wrap">{{ video?.description || '暂无描述' }}</div>
            <div class="flex items-center justify-between text-gray-500 text-xs">
              <span>{{ formatDate(video?.createdAt) }}</span>
              <div class="flex items-center gap-4">
                <span class="flex items-center gap-1"><eye-outlined /> {{ formatCount(video?.viewCount) }}</span>
              </div>
            </div>
          </div>

          <div class="p-4 flex items-center justify-around border-b border-white/5">
            <button class="flex flex-col items-center gap-1 group bg-transparent border-none p-0 cursor-pointer" @click="toggleLike">
              <div class="w-10 h-10 rounded-full bg-[#252533] flex items-center justify-center transition-all group-active:scale-90" :class="{ 'text-[#fe2c55]': video?.isLiked }">
                <like-filled v-if="video?.isLiked" class="text-xl" />
                <like-outlined v-else class="text-xl group-hover:text-white transition-colors" />
              </div>
              <span class="text-xs text-gray-400">{{ formatCount(video?.likeCount) }}</span>
            </button>
            <button class="flex flex-col items-center gap-1 group bg-transparent border-none p-0 cursor-pointer" @click="toggleFavorite">
              <div class="w-10 h-10 rounded-full bg-[#252533] flex items-center justify-center transition-all group-active:scale-90" :class="{ 'text-[#face15]': video?.isFavorited }">
                <star-filled v-if="video?.isFavorited" class="text-xl" />
                <star-outlined v-else class="text-xl group-hover:text-white transition-colors" />
              </div>
              <span class="text-xs text-gray-400">{{ formatCount(video?.favoriteCount) }}</span>
            </button>
            <button class="flex flex-col items-center gap-1 group bg-transparent border-none p-0 cursor-pointer" @click="shareVideo">
              <div class="w-10 h-10 rounded-full bg-[#252533] flex items-center justify-center transition-all group-active:scale-90">
                <share-alt-outlined class="text-xl group-hover:text-white transition-colors" />
              </div>
              <span class="text-xs text-gray-400">分享</span>
            </button>
          </div>

          <div class="p-4">
            <h3 class="text-white text-sm font-medium mb-4">评论 ({{ formatCount(video?.commentCount) }})</h3>
            <div class="space-y-4">
              <div v-for="comment in comments" :key="comment.id" class="group">
                <div class="flex gap-3">
                  <a-avatar :size="32" :src="resolveAvatarUrl(comment.author?.avatar) || DEFAULT_AVATAR" class="flex-shrink-0 cursor-pointer" @click="$router.push(`/user/${comment.userId}`)" />
                  <div class="flex-1">
                    <div class="flex items-center justify-between mb-1">
                      <span class="text-gray-400 text-xs font-medium cursor-pointer hover:text-white" @click="$router.push(`/user/${comment.userId}`)">{{ comment.author?.name }}</span>
                      <span class="text-gray-600 text-[10px]">{{ formatDate(comment.createdAt) }}</span>
                    </div>
                    <p class="text-gray-200 text-sm leading-relaxed mb-2">{{ comment.content }}</p>
                    <div class="flex items-center gap-4 text-gray-500 text-xs">
                      <button class="flex items-center gap-1 hover:text-white transition-colors bg-transparent border-none p-0 cursor-pointer" @click="likeComment(comment)">
                        <like-filled v-if="comment.isLiked" class="text-[#fe2c55]" />
                        <like-outlined v-else />
                        {{ comment.likeCount || 0 }}
                      </button>
                      <button class="hover:text-white transition-colors bg-transparent border-none p-0 cursor-pointer" @click="openReplyInput(comment.id)">
                        回复
                      </button>
                      <span v-if="comment.replyCount > 0" class="text-[#fe2c55] cursor-pointer hover:underline" @click="toggleReplies(comment)">
                        {{ comment.showReplies ? '收起' : `${comment.replyCount}条回复` }}
                      </span>
                    </div>

                    <div v-if="replyState.commentId === comment.id" class="mt-3 flex items-center gap-2">
                      <input 
                        v-model="replyState.content"
                        type="text"
                        :placeholder="replyState.replyToName ? `回复 @${replyState.replyToName}` : `回复 ${comment.author?.name}`"
                        class="flex-1 bg-[#252533] text-white text-sm rounded-full py-2 px-4 outline-none focus:bg-[#2f2f3f] transition-colors placeholder-gray-500"
                        @keyup.enter="submitReply(comment)"
                        ref="replyInputRef"
                      />
                      <button 
                        class="text-[#fe2c55] font-medium text-xs disabled:opacity-50 disabled:cursor-not-allowed transition-opacity"
                        :disabled="!replyState.content.trim() || replyState.submitting"
                        @click="submitReply(comment)"
                      >
                        发送
                      </button>
                      <button class="text-gray-500 text-xs hover:text-white transition-colors" @click="closeReplyInput">
                        取消
                      </button>
                    </div>

                    <div v-if="comment.showReplies && comment.replies?.length > 0" class="mt-3 space-y-3 pl-2 border-l-2 border-white/10">
                      <div v-for="reply in comment.replies" :key="reply.id" class="flex gap-2">
                        <a-avatar :size="24" :src="resolveAvatarUrl(reply.author?.avatar) || DEFAULT_AVATAR" class="flex-shrink-0 cursor-pointer" @click="$router.push(`/user/${reply.userId}`)" />
                        <div class="flex-1">
                          <div class="flex items-center gap-1 mb-1">
                            <span class="text-gray-400 text-xs font-medium cursor-pointer hover:text-white" @click="$router.push(`/user/${reply.userId}`)">{{ reply.author?.name }}</span>
                            <span v-if="reply.replyToName" class="text-gray-600 text-xs">回复 <span class="text-gray-400">@{{ reply.replyToName }}</span></span>
                          </div>
                          <p class="text-gray-200 text-xs leading-relaxed mb-1">{{ reply.content }}</p>
                          <div class="flex items-center gap-3 text-gray-500 text-[10px]">
                            <span>{{ formatDate(reply.createdAt) }}</span>
                            <button class="hover:text-white transition-colors bg-transparent border-none p-0 cursor-pointer" @click="openReplyInput(comment.id, reply)">
                              回复
                            </button>
                          </div>
                        </div>
                      </div>
                      <button 
                        v-if="comment.hasMoreReplies" 
                        class="text-gray-500 text-xs hover:text-white transition-colors w-full text-left"
                        @click="loadMoreReplies(comment)"
                      >
                        加载更多回复...
                      </button>
                    </div>
                    <div v-if="comment.loadingReplies" class="mt-2 text-center">
                      <loading-outlined class="text-gray-500 animate-spin" />
                    </div>
                  </div>
                </div>
              </div>
              <div v-if="comments.length === 0" class="text-center py-10 text-gray-500 text-sm">
                暂无评论，快来抢沙发吧
              </div>
            </div>
          </div>
        </div>

        <div class="p-4 border-t border-white/5 bg-[#161823]">
          <div class="flex items-center gap-3">
            <div class="flex-1 relative">
              <input 
                v-model="commentContent" 
                type="text" 
                placeholder="善语结善缘，恶语伤人心"
                class="w-full bg-[#252533] text-white text-sm rounded-full py-2.5 pl-4 pr-10 outline-none focus:bg-[#2f2f3f] transition-colors placeholder-gray-500"
                @keyup.enter="submitComment"
              />
              <div class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 cursor-pointer hover:text-white">
                <smile-outlined />
              </div>
            </div>
            <button 
              class="text-[#fe2c55] font-medium text-sm disabled:opacity-50 disabled:cursor-not-allowed transition-opacity"
              :disabled="!commentContent.trim() || submitting"
              @click="submitComment"
            >
              发送
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { videoApi, commentApi, followApi, favoriteApi } from '../api'
import { useUserStore } from '../stores/user'
import { message } from 'ant-design-vue'
import { DEFAULT_AVATAR, resolveAvatarUrl } from '@/utils/constants'
import { 
  EyeOutlined, 
  LikeOutlined, 
  LikeFilled, 
  StarOutlined, 
  StarFilled, 
  ShareAltOutlined, 
  SmileOutlined, 
  LoadingOutlined,
  VideoCameraOutlined
} from '@ant-design/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const video = ref(null)
const comments = ref([])
const commentContent = ref('')
const submitting = ref(false)
const isFollowing = ref(false)
const hasRecordedView = ref(false)

const replyState = reactive({
  commentId: null,
  content: '',
  submitting: false,
  replyToId: null,
  replyToName: null
})

const isOwner = computed(() => video.value?.userId === userStore.userInfo?.id)

const fetchVideo = async () => {
  try {
    const res = await videoApi.getDetail(route.params.id)
    if (res.code === 200) video.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const fetchComments = async () => {
  try {
    const res = await commentApi.getList(route.params.id)
    if (res.code === 200) {
      comments.value = res.data.list.map(c => ({
        ...c,
        showReplies: false,
        replies: [],
        loadingReplies: false,
        hasMoreReplies: false,
        replyPage: 1
      }))
    }
  } catch (error) {
    console.error(error)
  }
}

const recordView = async () => {
  if (hasRecordedView.value) return
  hasRecordedView.value = true
  try {
    await videoApi.recordView(route.params.id)
  } catch (error) {
    console.error(error)
  }
}

const toggleLike = async () => {
  if (!userStore.isLoggedIn) return router.push('/login')
  try {
    if (video.value.isLiked) {
      await videoApi.unlike(video.value.id)
      video.value.isLiked = false
      video.value.likeCount--
    } else {
      await videoApi.like(video.value.id)
      video.value.isLiked = true
      video.value.likeCount++
    }
  } catch (error) {
    message.error('操作失败')
  }
}

const toggleFavorite = async () => {
  if (!userStore.isLoggedIn) return router.push('/login')
  try {
    if (video.value.isFavorited) {
      await favoriteApi.remove(video.value.id)
      video.value.isFavorited = false
      video.value.favoriteCount--
      message.success('已取消收藏')
    } else {
      await favoriteApi.add(video.value.id)
      video.value.isFavorited = true
      video.value.favoriteCount++
      message.success('收藏成功')
    }
  } catch (error) {
    message.error('操作失败')
  }
}

const toggleFollow = async () => {
  if (!userStore.isLoggedIn) return router.push('/login')
  try {
    if (isFollowing.value) {
      await followApi.unfollow(video.value.userId)
      isFollowing.value = false
    } else {
      await followApi.follow(video.value.userId)
      isFollowing.value = true
    }
  } catch (error) {
    message.error('操作失败')
  }
}

const submitComment = async () => {
  if (!userStore.isLoggedIn) return router.push('/login')
  if (!commentContent.value.trim()) return
  submitting.value = true
  try {
    const res = await commentApi.create({ videoId: video.value.id, content: commentContent.value })
    if (res.code === 200) {
      comments.value.unshift({
        ...res.data,
        showReplies: false,
        replies: [],
        loadingReplies: false,
        hasMoreReplies: false,
        replyPage: 1
      })
      commentContent.value = ''
      video.value.commentCount++
      message.success('评论成功')
    }
  } catch (error) {
    message.error('评论失败')
  } finally {
    submitting.value = false
  }
}

const likeComment = async (comment) => {
  if (!userStore.isLoggedIn) return router.push('/login')
  try {
    if (comment.isLiked) {
      await commentApi.unlike(comment.id)
      comment.isLiked = false
      comment.likeCount--
    } else {
      await commentApi.like(comment.id)
      comment.isLiked = true
      comment.likeCount++
    }
  } catch (error) {
    message.error('操作失败')
  }
}

const openReplyInput = (commentId, reply = null) => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  
  replyState.commentId = commentId
  replyState.submitting = false
  
  if (reply) {
    replyState.content = `@${reply.author?.name} `
    replyState.replyToId = reply.id
    replyState.replyToName = reply.author?.name
  } else {
    replyState.content = ''
    replyState.replyToId = null
    replyState.replyToName = null
  }
  
  nextTick(() => {
    const input = document.querySelector('.reply-input-focus')
    if (input) input.focus()
  })
}

const closeReplyInput = () => {
  replyState.commentId = null
  replyState.content = ''
  replyState.replyToId = null
  replyState.replyToName = null
  replyState.submitting = false
}

const submitReply = async (comment) => {
  if (!replyState.content.trim()) return
  replyState.submitting = true
  try {
    const res = await commentApi.create({
      videoId: video.value.id,
      content: replyState.content,
      parentId: replyState.replyToId || comment.id
    })
    if (res.code === 200) {
      if (!comment.replies) comment.replies = []
      const newReply = {
        ...res.data,
        replyToName: replyState.replyToId ? replyState.replyToName : null
      }
      comment.replies.push(newReply)
      comment.replyCount++
      comment.showReplies = true
      video.value.commentCount++
      closeReplyInput()
      message.success('回复成功')
    }
  } catch (error) {
    message.error('回复失败')
  } finally {
    replyState.submitting = false
  }
}

const toggleReplies = async (comment) => {
  if (!comment.showReplies && comment.replies.length === 0) {
    await loadReplies(comment)
  }
  comment.showReplies = !comment.showReplies
}

const loadReplies = async (comment) => {
  comment.loadingReplies = true
  try {
    const res = await commentApi.getReplies(comment.id, 1, 10)
    if (res.code === 200) {
      comment.replies = res.data.list.map(r => ({
        ...r,
        replyToName: r.parentId !== comment.id ? findReplyAuthorName(comment.replies, r.parentId) : null
      }))
      comment.replyPage = 1
      comment.hasMoreReplies = res.data.pagination.page < res.data.pagination.totalPages
    }
  } catch (error) {
    console.error(error)
  } finally {
    comment.loadingReplies = false
  }
}

const loadMoreReplies = async (comment) => {
  comment.replyPage++
  comment.loadingReplies = true
  try {
    const res = await commentApi.getReplies(comment.id, comment.replyPage, 10)
    if (res.code === 200) {
      const newReplies = res.data.list.map(r => ({
        ...r,
        replyToName: r.parentId !== comment.id ? findReplyAuthorName(comment.replies, r.parentId) : null
      }))
      comment.replies.push(...newReplies)
      comment.hasMoreReplies = res.data.pagination.page < res.data.pagination.totalPages
    }
  } catch (error) {
    console.error(error)
  } finally {
    comment.loadingReplies = false
  }
}

const findReplyAuthorName = (replies, parentId) => {
  if (!replies) return null
  const reply = replies.find(r => r.id === parentId)
  return reply?.author?.name || null
}

const shareVideo = () => {
  navigator.clipboard.writeText(window.location.href)
  message.success('链接已复制')
}

const formatCount = (c) => {
  if (!c) return '0'
  if (c >= 10000) return (c/10000).toFixed(1)+'w'
  if (c >= 1000) return (c/1000).toFixed(1)+'k'
  return c.toString()
}

const formatDate = (d) => {
  if (!d) return ''
  const date = new Date(d)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff/60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff/3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff/86400000)}天前`
  
  return date.toLocaleDateString()
}

onMounted(async () => {
  await fetchVideo()
  await fetchComments()
  if (userStore.isLoggedIn && video.value) {
    try {
      const res = await followApi.isFollowing(video.value.userId)
      if (res.code === 200) isFollowing.value = res.data
    } catch (e) {
      console.error(e)
    }
  }
})
</script>
