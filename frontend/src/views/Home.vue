<template>
  <div class="w-full h-screen overflow-hidden bg-black relative group" @touchstart="onTouchStart" @touchend="onTouchEnd">
    <div
      class="w-full h-full transition-transform duration-300 ease-out will-change-transform"
      :style="{ transform: `translateY(-${currentIndex * 100}%)` }"
    >
      <div
        v-for="(video, index) in videoList"
        :key="video.id"
        class="w-full h-full relative flex items-center justify-center bg-black"
        :class="{ active: index === currentIndex }"
      >
        <!-- Blurred Background for non-full-screen videos -->
        <div
          class="absolute inset-0 bg-cover bg-center blur-2xl opacity-40 scale-110"
          :style="{ backgroundImage: `url(${video.coverUrl})` }"
        ></div>

        <video
          v-if="(index === currentIndex || index === currentIndex - 1 || index === currentIndex + 1) && video.videoUrl"
          :ref="el => videoRefs[video.id] = el"
          :src="video.videoUrl"
          :poster="video.coverUrl"
          class="w-full h-full object-contain relative z-10"
          loop
          playsinline
          webkit-playsinline
          x5-video-player-type="h5-page"
          @click="togglePlay(video.id)"
        ></video>

        <!-- Play Button Overlay -->
        <div class="absolute inset-0 flex items-center justify-center z-20 pointer-events-none">
          <div v-if="!playingVideos[video.id]" class="w-20 h-20 bg-black/40 rounded-full flex items-center justify-center backdrop-blur-sm animate-pulse">
            <play-circle-filled class="text-white text-5xl opacity-80" />
          </div>
        </div>

        <!-- Video Info (Bottom Left) -->
        <div class="absolute left-4 right-20 bottom-20 z-20 pointer-events-none">
          <div class="flex items-center gap-2 mb-3 pointer-events-auto" @click="goToUser(video.userId)">
            <span class="text-white font-bold text-lg drop-shadow-md">@{{ video.author?.name || '用户' }}</span>
          </div>
          <div class="pointer-events-auto">
            <p class="text-white text-[15px] leading-relaxed mb-2 line-clamp-2 drop-shadow-md">{{ video.title }}</p>
            <p v-if="video.description" class="text-gray-300 text-sm line-clamp-1 drop-shadow-md">
              <span class="mr-1">🎵</span> {{ video.description }}
            </p>
          </div>
        </div>

        <!-- Right Action Bar -->
        <div class="absolute right-2 bottom-24 z-20 flex flex-col gap-6 items-center w-[60px]">
          <!-- Avatar -->
          <div class="relative cursor-pointer mb-2" @click="goToUser(video.userId)">
            <div class="w-[50px] h-[50px] rounded-full border-2 border-white p-[1px] overflow-hidden">
              <a-avatar :size="44" :src="resolveAvatarUrl(video.author?.avatar) || DEFAULT_AVATAR" class="w-full h-full" />
            </div>
            <div
              v-if="!video.isFollowing"
              class="absolute -bottom-2 left-1/2 -translate-x-1/2 w-5 h-5 bg-[#fe2c55] rounded-full flex items-center justify-center cursor-pointer shadow-sm"
              @click.stop="followUser(video)"
            >
              <plus-outlined class="text-white text-xs font-bold" />
            </div>
          </div>

          <!-- Like -->
          <div class="flex flex-col items-center gap-1 cursor-pointer" @click="toggleLike(video)">
            <div class="transition-transform active:scale-75">
              <heart-filled v-if="video.isLiked" class="text-[35px] text-[#fe2c55] drop-shadow-md" />
              <heart-filled v-else class="text-[35px] text-white opacity-90 drop-shadow-md" />
            </div>
            <span class="text-white text-xs font-medium shadow-black drop-shadow-md">{{ formatCount(video.likeCount) }}</span>
          </div>

          <!-- Comment -->
          <div class="flex flex-col items-center gap-1 cursor-pointer" @click="openComment(video)">
            <div class="transition-transform active:scale-75">
               <message-filled class="text-[35px] text-white opacity-90 drop-shadow-md" />
            </div>
            <span class="text-white text-xs font-medium shadow-black drop-shadow-md">{{ formatCount(video.commentCount) }}</span>
          </div>

          <!-- Favorite -->
          <div class="flex flex-col items-center gap-1 cursor-pointer" @click="toggleFavorite(video)">
            <div class="transition-transform active:scale-75">
              <star-filled v-if="video.isFavorited" class="text-[35px] text-[#face15] drop-shadow-md" />
              <star-filled v-else class="text-[35px] text-white opacity-90 drop-shadow-md" />
            </div>
            <span class="text-white text-xs font-medium shadow-black drop-shadow-md">{{ formatCount(video.favoriteCount) }}</span>
          </div>

          <!-- Share -->
          <div class="flex flex-col items-center gap-1 cursor-pointer" @click="shareVideo(video)">
             <div class="transition-transform active:scale-75">
               <share-alt-outlined class="text-[35px] text-white opacity-90 drop-shadow-md" />
             </div>
            <span class="text-white text-xs font-medium shadow-black drop-shadow-md">分享</span>
          </div>

          <!-- Rotating Music Disc (Optional visual) -->
          <div class="mt-4 w-[50px] h-[50px] bg-[#161823] rounded-full border-[6px] border-[#222] flex items-center justify-center animate-[spin_5s_linear_infinite] overflow-hidden">
             <div class="w-6 h-6 rounded-full bg-cover bg-center" :style="{ backgroundImage: `url(${resolveAvatarUrl(video.author?.avatar) || video.coverUrl})` }"></div>
          </div>
        </div>
      </div>
    </div>

    <div class="hidden md:flex flex-col gap-3 absolute right-[92px] top-1/2 -translate-y-1/2 z-30 opacity-0 group-hover:opacity-100 transition-opacity">
      <button
        class="w-10 h-10 rounded-full bg-black/50 border border-white/10 text-white flex items-center justify-center cursor-pointer hover:bg-black/70 disabled:opacity-30 disabled:cursor-not-allowed"
        :disabled="currentIndex <= 0"
        @click.stop="goPrev"
      >
        <up-outlined class="text-lg" />
      </button>
      <button
        class="w-10 h-10 rounded-full bg-black/50 border border-white/10 text-white flex items-center justify-center cursor-pointer hover:bg-black/70 disabled:opacity-30 disabled:cursor-not-allowed"
        :disabled="currentIndex >= videoList.length - 1"
        @click.stop="goNext"
      >
        <down-outlined class="text-lg" />
      </button>
    </div>

    <div v-if="loading" class="absolute inset-0 bg-black flex items-center justify-center z-50">
      <div class="w-10 h-10 border-4 border-white/20 border-t-[#fe2c55] rounded-full animate-spin"></div>
    </div>

    <!-- Comment Drawer -->
    <a-drawer
      v-model:open="commentVisible"
      placement="bottom"
      height="70vh"
      :closable="false"
      rootClassName="dark-drawer"
      :bodyStyle="{ padding: 0, display: 'flex', flexDirection: 'column' }"
    >
      <div class="flex justify-between items-center px-4 py-3 border-b border-white/10 shrink-0">
        <span class="text-sm font-bold">{{ currentVideo?.commentCount || 0 }} 条评论</span>
        <div class="w-6 h-6 bg-gray-800 rounded-full flex items-center justify-center cursor-pointer" @click="commentVisible = false">
           <close-outlined class="text-xs text-gray-400" />
        </div>
      </div>

      <div class="flex-1 overflow-y-auto p-4 custom-scrollbar">
        <div v-if="comments.length === 0" class="text-center text-gray-500 py-10 text-sm">
          暂无评论，快来抢沙发吧~
        </div>

        <div v-for="comment in comments" :key="comment.id" class="flex gap-3 mb-5">
          <a-avatar :size="32" :src="resolveAvatarUrl(comment.author?.avatar) || DEFAULT_AVATAR" class="shrink-0 border border-white/10" />
          <div class="flex-1">
            <div class="text-gray-400 text-xs mb-1">{{ comment.author?.name }}</div>
            <div class="text-white text-[14px] leading-relaxed mb-2">{{ comment.content }}</div>
            <div class="flex items-center gap-4 text-gray-500 text-xs">
              <span>{{ formatTime(comment.createdAt) }}</span>
              <span class="cursor-pointer hover:text-white transition-colors" @click="openReplyInput(comment.id)">回复</span>
              <span v-if="comment.replyCount > 0" class="text-[#fe2c55] cursor-pointer hover:underline" @click="toggleReplies(comment)">
                {{ comment.showReplies ? '收起' : `${comment.replyCount}条回复` }}
              </span>
            </div>

            <div v-if="replyState.commentId === comment.id" class="mt-2 flex items-center gap-2">
              <input 
                v-model="replyState.content"
                type="text"
                :placeholder="replyState.replyToName ? `回复 @${replyState.replyToName}` : `回复 ${comment.author?.name}`"
                class="flex-1 bg-[#252533] text-white text-sm rounded-full py-1.5 px-3 outline-none focus:bg-[#2f2f3f] transition-colors placeholder-gray-500"
                @keyup.enter="submitReply(comment)"
              />
              <button 
                class="text-[#fe2c55] font-medium text-xs disabled:opacity-50 disabled:cursor-not-allowed transition-opacity bg-transparent border-none cursor-pointer"
                :disabled="!replyState.content.trim() || replyState.submitting"
                @click="submitReply(comment)"
              >
                发送
              </button>
              <button class="text-gray-500 text-xs hover:text-white transition-colors bg-transparent border-none cursor-pointer" @click="closeReplyInput">
                取消
              </button>
            </div>

            <div v-if="comment.showReplies && comment.replies?.length > 0" class="mt-2 space-y-2 pl-2 border-l-2 border-white/10">
              <div v-for="reply in comment.replies" :key="reply.id" class="flex gap-2">
                <a-avatar :size="22" :src="resolveAvatarUrl(reply.author?.avatar) || DEFAULT_AVATAR" class="shrink-0" />
                <div class="flex-1">
                  <div class="flex items-center gap-1 mb-0.5">
                    <span class="text-gray-400 text-xs">{{ reply.author?.name }}</span>
                    <span v-if="reply.replyToName" class="text-gray-600 text-[10px]">回复 <span class="text-gray-400">@{{ reply.replyToName }}</span></span>
                  </div>
                  <p class="text-gray-200 text-xs leading-relaxed mb-0.5">{{ reply.content }}</p>
                  <div class="flex items-center gap-3 text-gray-500 text-[10px]">
                    <span>{{ formatTime(reply.createdAt) }}</span>
                    <span class="cursor-pointer hover:text-white transition-colors" @click="openReplyInput(comment.id, reply)">回复</span>
                  </div>
                </div>
              </div>
            </div>
            <div v-if="comment.loadingReplies" class="mt-2 text-center">
              <loading-outlined class="text-gray-500 animate-spin text-sm" />
            </div>
          </div>
          <div class="flex flex-col items-center gap-1 text-gray-500">
             <heart-outlined class="text-sm cursor-pointer" :class="{ 'text-[#fe2c55]': comment.isLiked }" @click="likeComment(comment)" />
             <span class="text-[10px]">{{ comment.likeCount || 0 }}</span>
          </div>
        </div>
      </div>

      <div class="p-3 border-t border-white/10 bg-[#161823] flex gap-3 items-center shrink-0 safe-area-bottom">
        <div class="flex-1 bg-[#252525] rounded-full px-4 py-2 flex items-center">
           <input
             v-model="commentContent"
             type="text"
             placeholder="留下你的精彩评论吧"
             class="bg-transparent border-none outline-none text-white text-sm w-full placeholder-gray-500"
             @keyup.enter="submitComment"
           />
        </div>
        <div
           class="w-8 h-8 flex items-center justify-center rounded-full transition-colors cursor-pointer"
           :class="commentContent.trim() ? 'bg-[#fe2c55] text-white' : 'bg-gray-800 text-gray-500'"
           @click="submitComment"
        >
           <arrow-up-outlined v-if="commentContent.trim()" />
           <message-outlined v-else />
        </div>
      </div>
    </a-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { videoApi, commentApi, followApi, favoriteApi } from '../api'
import { useUserStore } from '../stores/user'
import { message } from 'ant-design-vue'
import { DEFAULT_AVATAR, resolveAvatarUrl } from '@/utils/constants'
import {
  HeartOutlined, HeartFilled, MessageOutlined, MessageFilled,
  StarOutlined, StarFilled, ShareAltOutlined,
  PlayCircleFilled, CloseOutlined, ArrowUpOutlined,
  PlusOutlined, LoadingOutlined,
  UpOutlined,
  DownOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const videoList = ref([])
const currentIndex = ref(0)
const loading = ref(false)
const videoRefs = reactive({})
const playingVideos = reactive({})

const commentVisible = ref(false)
const currentVideo = ref(null)
const comments = ref([])
const commentContent = ref('')

const replyState = reactive({
  commentId: null,
  content: '',
  submitting: false,
  replyToId: null,
  replyToName: null
})

let startY = 0

const fetchVideos = async () => {
  loading.value = true
  try {
    const res = await videoApi.getList(1, 20)
    if (res.code === 200) {
      videoList.value = res.data.list.map(v => ({
        ...v,
        isLiked: false,
        isFavorited: false,
        isFollowing: false
      }))
      if (videoList.value.length > 0) {
        nextTick(() => playVideo(videoList.value[0].id))
      }
    }
  } finally {
    loading.value = false
  }
}

const loadMore = async () => {
  if (currentIndex.value >= videoList.value.length - 3) {
    const res = await videoApi.getList(Math.ceil(videoList.value.length / 20) + 1, 20)
    if (res.code === 200) {
      videoList.value.push(...res.data.list.map(v => ({
        ...v,
        isLiked: false,
        isFavorited: false,
        isFollowing: false
      })))
    }
  }
}

const playVideo = (id) => {
  Object.keys(videoRefs).forEach(vid => {
    const video = videoRefs[vid]
    if (video) {
      if (vid == id) {
        // 确保有有效的src才尝试播放
        if (video.src && video.src !== window.location.href) {
            const playPromise = video.play()
            if (playPromise !== undefined) {
                playPromise.catch(error => {
                    console.log('Autoplay failed:', error)
                })
            }
            playingVideos[id] = true
        }
      } else {
        video.pause()
        playingVideos[vid] = false
      }
    }
  })
}

const togglePlay = (id) => {
  const video = videoRefs[id]
  if (video && video.src && video.src !== window.location.href) {
    if (video.paused) {
      const playPromise = video.play()
      if (playPromise !== undefined) {
        playPromise.catch(error => {
          console.log('Playback failed:', error)
          // 如果是因为没有源导致的错误，可以尝试重新加载或提示
          if (error.name === 'NotSupportedError') {
             console.warn('Video format not supported or source missing')
          }
        })
      }
      playingVideos[id] = true
    } else {
      video.pause()
      playingVideos[id] = false
    }
  }
}

const onTouchStart = (e) => {
  startY = e.touches[0].clientY
}

const onTouchEnd = (e) => {
  const endY = e.changedTouches[0].clientY
  const diff = startY - endY

  if (Math.abs(diff) > 50) {
    if (diff > 0 && currentIndex.value < videoList.value.length - 1) {
      currentIndex.value++
      playVideo(videoList.value[currentIndex.value].id)
      loadMore()
    } else if (diff < 0 && currentIndex.value > 0) {
      currentIndex.value--
      playVideo(videoList.value[currentIndex.value].id)
    }
  }
}

const goPrev = () => {
  if (currentIndex.value <= 0) return
  currentIndex.value--
  playVideo(videoList.value[currentIndex.value].id)
}

const goNext = async () => {
  if (currentIndex.value >= videoList.value.length - 1) return
  currentIndex.value++
  playVideo(videoList.value[currentIndex.value].id)
  await loadMore()
}

const toggleLike = async (video) => {
  if (!userStore.isLoggedIn) return router.push('/login')
  if (video.isLiked) {
    await videoApi.unlike(video.id)
    video.likeCount--
  } else {
    await videoApi.like(video.id)
    video.likeCount++
  }
  video.isLiked = !video.isLiked
}

const toggleFavorite = async (video) => {
  if (!userStore.isLoggedIn) return router.push('/login')
  if (video.isFavorited) {
    await favoriteApi.remove(video.id)
    video.favoriteCount--
    message.success('已取消收藏')
  } else {
    await favoriteApi.add(video.id)
    video.favoriteCount++
    message.success('已收藏')
  }
  video.isFavorited = !video.isFavorited
}

const followUser = async (video) => {
  if (!userStore.isLoggedIn) return router.push('/login')
  await followApi.follow(video.userId)
  video.isFollowing = true
  message.success('关注成功')
}

const openComment = async (video) => {
  currentVideo.value = video
  commentVisible.value = true
  closeReplyInput()
  const res = await commentApi.getList(video.id)
  if (res.code === 200) {
    comments.value = res.data.list.map(c => ({
      ...c,
      showReplies: false,
      replies: [],
      loadingReplies: false
    }))
  }
}

const submitComment = async () => {
  if (!userStore.isLoggedIn) return router.push('/login')
  if (!commentContent.value.trim()) return
  const res = await commentApi.create({
    videoId: currentVideo.value.id,
    content: commentContent.value
  })
  if (res.code === 200) {
    comments.value.unshift({
      ...res.data,
      showReplies: false,
      replies: [],
      loadingReplies: false
    })
    currentVideo.value.commentCount++
    commentContent.value = ''
    message.success('评论成功')
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
      videoId: currentVideo.value.id,
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
      currentVideo.value.commentCount++
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
    comment.loadingReplies = true
    try {
      const res = await commentApi.getReplies(comment.id, 1, 10)
      if (res.code === 200) {
        comment.replies = res.data.list.map(r => ({
          ...r,
          replyToName: r.parentId !== comment.id ? findReplyAuthorName(comment.replies, r.parentId) : null
        }))
      }
    } catch (e) {
      console.error(e)
    } finally {
      comment.loadingReplies = false
    }
  }
  comment.showReplies = !comment.showReplies
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

const findReplyAuthorName = (replies, parentId) => {
  if (!replies) return null
  const reply = replies.find(r => r.id === parentId)
  return reply?.author?.name || null
}

const shareVideo = (video) => {
  navigator.clipboard.writeText(`${window.location.origin}/video/${video.id}`)
  message.success('链接已复制')
}

const goToUser = (userId) => router.push(`/user/${userId}`)

const formatCount = (c) => {
  if (!c) return '0'
  if (c >= 10000) return (c / 10000).toFixed(1) + 'w'
  if (c >= 1000) return (c / 1000).toFixed(1) + 'k'
  return c.toString()
}

const formatTime = (d) => {
  if (!d) return ''
  const now = new Date()
  const date = new Date(d)
  const diff = (now - date) / 1000
  if (diff < 60) return '刚刚'
  if (diff < 3600) return Math.floor(diff / 60) + '分钟前'
  if (diff < 86400) return Math.floor(diff / 3600) + '小时前'
  if (diff < 2592000) return Math.floor(diff / 86400) + '天前'
  return date.toLocaleDateString()
}

onMounted(fetchVideos)
onUnmounted(() => {
  Object.values(videoRefs).forEach(v => v?.pause())
})
</script>
