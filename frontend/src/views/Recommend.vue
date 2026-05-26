<template>
  <div class="min-h-screen bg-black text-white pb-20 group">
    <div class="sticky top-0 z-10 bg-black/90 backdrop-blur-md px-4 py-3 border-b border-white/5 flex items-center justify-between">
      <h3 class="text-lg font-bold text-white m-0">热门推荐</h3>
    </div>
    
    <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-1 p-1">
      <div 
        v-for="video in videoList" 
        :key="video.id" 
        class="relative aspect-[3/4] bg-[#161823] cursor-pointer group overflow-hidden rounded-sm" 
        @click="goToVideo(video)"
      >
        <img :src="video.coverUrl || defaultCover" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105" />
        
        <!-- Hover Overlay -->
        <div class="absolute inset-0 bg-black/20 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
          <play-circle-filled class="text-4xl text-white/80 drop-shadow-lg scale-0 group-hover:scale-100 transition-transform duration-300" />
        </div>

        <!-- Stats Overlay -->
        <div class="absolute bottom-2 left-2 flex items-center gap-1 text-white text-xs z-10 drop-shadow-md">
          <heart-filled class="text-[#fe2c55]" />
          <span>{{ formatCount(video.likeCount) }}</span>
        </div>

        <!-- Bottom Info Gradient -->
        <div class="absolute inset-x-0 bottom-0 p-3 pt-8 bg-gradient-to-t from-black/80 to-transparent pointer-events-none">
          <p class="text-white text-sm font-medium mb-1 line-clamp-2 shadow-sm">{{ video.title }}</p>
          <div class="flex items-center gap-2">
            <a-avatar :size="20" :src="resolveAvatarUrl(video.author?.avatar) || DEFAULT_AVATAR" class="border border-white/20" />
            <span class="text-gray-300 text-xs truncate">{{ video.author?.name }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <div v-if="loading" class="flex justify-center py-10">
      <a-spin />
    </div>
    
    <div v-if="!loading && videoList.length === 0" class="flex flex-col items-center justify-center py-20 text-gray-500">
      <div class="w-20 h-20 bg-[#161823] rounded-full flex items-center justify-center mb-4">
        <fire-outlined class="text-4xl text-gray-600" />
      </div>
      <p class="text-sm">暂无推荐内容</p>
    </div>

    <div ref="loadMoreRef" class="h-1"></div>

    <div class="hidden md:flex flex-col gap-3 fixed right-6 top-1/2 -translate-y-1/2 z-50 opacity-0 group-hover:opacity-100 transition-opacity">
      <button
        class="w-10 h-10 rounded-full bg-black/50 border border-white/10 text-white flex items-center justify-center cursor-pointer hover:bg-black/70 disabled:opacity-30 disabled:cursor-not-allowed"
        :disabled="page <= 1 || loading"
        @click="prevPage"
      >
        <up-outlined class="text-lg" />
      </button>
      <button
        class="w-10 h-10 rounded-full bg-black/50 border border-white/10 text-white flex items-center justify-center cursor-pointer hover:bg-black/70 disabled:opacity-30 disabled:cursor-not-allowed"
        :disabled="page >= totalPages || loading"
        @click="nextPage"
      >
        <down-outlined class="text-lg" />
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { videoApi } from '../api'
import { HeartFilled, PlayCircleFilled, FireOutlined, UpOutlined, DownOutlined } from '@ant-design/icons-vue'
import { DEFAULT_AVATAR, resolveAvatarUrl } from '@/utils/constants'

const router = useRouter()
const videoList = ref([])
const loading = ref(false)
const page = ref(1)
const totalPages = ref(1)
const size = ref(12)
const loadMoreRef = ref(null)
const isDesktop = ref(false)
let loadMoreObserver = null

const defaultCover = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="200" height="300" viewBox="0 0 200 300"%3E%3Crect fill="%23222" width="200" height="300"/%3E%3C/svg%3E'

const updateIsDesktop = () => {
  if (typeof window === 'undefined') return
  isDesktop.value = window.matchMedia('(min-width: 768px)').matches
}

const fetchVideos = async ({ targetPage = page.value, append = false } = {}) => {
  loading.value = true
  try {
    const res = await videoApi.getRecommend(targetPage, size.value)
    if (res.code === 200) {
      const list = Array.isArray(res.data?.list) ? res.data.list : []
      videoList.value = append ? [...videoList.value, ...list] : list
      totalPages.value = res.data?.pagination?.totalPages || 1
      page.value = targetPage
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const goToVideo = (video) => router.push(`/video/${video.id}`)

const prevPage = async () => {
  if (loading.value || page.value <= 1) return
  await fetchVideos({ targetPage: page.value - 1, append: false })
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const nextPage = async () => {
  if (loading.value || page.value >= totalPages.value) return
  await fetchVideos({ targetPage: page.value + 1, append: false })
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const loadNextPage = async () => {
  if (isDesktop.value) return
  if (loading.value) return
  if (page.value >= totalPages.value) return
  await fetchVideos({ targetPage: page.value + 1, append: true })
}

const setupLoadMoreObserver = () => {
  if (typeof window === 'undefined') return
  if (!loadMoreRef.value) return
  if (loadMoreObserver) loadMoreObserver.disconnect()

  loadMoreObserver = new IntersectionObserver(
    (entries) => {
      const entry = entries[0]
      if (entry?.isIntersecting) loadNextPage()
    },
    { root: null, rootMargin: '200px 0px', threshold: 0 }
  )

  loadMoreObserver.observe(loadMoreRef.value)
}

const formatCount = (c) => {
  if (!c) return '0'
  if (c >= 10000) return (c/10000).toFixed(1)+'w'
  if (c >= 1000) return (c/1000).toFixed(1)+'k'
  return c.toString()
}

onMounted(async () => {
  updateIsDesktop()
  window.addEventListener('resize', updateIsDesktop)
  await fetchVideos({ targetPage: 1, append: false })
  await nextTick()
  setupLoadMoreObserver()
})

onUnmounted(() => {
  if (typeof window !== 'undefined') window.removeEventListener('resize', updateIsDesktop)
  if (loadMoreObserver) loadMoreObserver.disconnect()
})
</script>
