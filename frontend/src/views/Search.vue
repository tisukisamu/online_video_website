<template>
  <div class="min-h-screen bg-black pt-[60px] pb-24">
    <!-- Search Bar -->
    <div class="fixed top-0 left-0 right-0 z-50 flex items-center gap-3 px-4 h-[60px] bg-black/90 backdrop-blur-md border-b border-white/5">
      <div class="flex-1 relative">
        <search-outlined class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500 text-lg" />
        <input
          v-model="keyword"
          type="text"
          placeholder="搜索视频、用户"
          class="w-full h-10 bg-[#161823] rounded-lg text-white pl-10 pr-4 outline-none border-none placeholder-gray-600 focus:bg-[#1f212e] transition-colors"
          @keyup.enter="handleSearch"
        />
        <close-circle-filled 
          v-if="keyword" 
          class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 cursor-pointer hover:text-gray-300"
          @click="keyword = ''; videoList = []; searched = false" 
        />
      </div>
      <button 
        class="text-[#fe2c55] font-medium text-base active:opacity-70 transition-opacity bg-transparent border-none cursor-pointer"
        @click="handleSearch"
      >
        搜索
      </button>
    </div>
    
    <!-- Search Results -->
    <div v-if="searched">
      <div v-if="videoList.length > 0">
        <p class="px-4 py-2 text-gray-500 text-xs">找到 {{ videoList.length }} 个相关视频</p>
        
        <div class="grid grid-cols-2 gap-1 p-1">
          <div 
            v-for="video in videoList" 
            :key="video.id" 
            class="relative aspect-[3/4] bg-[#161823] overflow-hidden cursor-pointer group"
            @click="goToVideo(video)"
          >
            <img :src="video.coverUrl || defaultCover" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105" />
            
            <!-- Gradient Overlay -->
            <div class="absolute inset-x-0 bottom-0 h-1/2 bg-gradient-to-t from-black/80 via-black/20 to-transparent pointer-events-none"></div>
            
            <!-- Info -->
            <div class="absolute bottom-0 left-0 right-0 p-2 pointer-events-none">
              <p class="text-white text-sm font-medium line-clamp-2 mb-1 drop-shadow-md leading-snug">{{ video.title }}</p>
              <div class="flex items-center justify-between text-xs text-gray-300">
                <div class="flex items-center gap-1">
                   <div class="w-4 h-4 rounded-full bg-gray-700 overflow-hidden">
                     <img :src="resolveAvatarUrl(video.author?.avatar) || DEFAULT_AVATAR" class="w-full h-full object-cover" />
                   </div>
                   <span class="max-w-[60px] truncate">{{ video.author?.name }}</span>
                </div>
                <div class="flex items-center gap-1">
                  <heart-outlined /> {{ formatCount(video.likeCount) }}
                </div>
              </div>
            </div>
            
            <!-- Play Icon (Hover) -->
            <div class="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity bg-black/20 backdrop-blur-[1px]">
              <play-circle-filled class="text-white text-4xl drop-shadow-lg opacity-90" />
            </div>
          </div>
        </div>
      </div>
      
      <!-- No Results -->
      <div v-else-if="!loading" class="flex flex-col items-center justify-center py-20 text-gray-500">
        <div class="w-20 h-20 bg-[#161823] rounded-full flex items-center justify-center mb-4">
           <search-outlined class="text-3xl opacity-50" />
        </div>
        <p class="text-sm">未找到相关内容</p>
        <p class="text-xs opacity-60 mt-1">换个关键词试试看</p>
      </div>
    </div>
    
    <!-- Initial State -->
    <div v-else class="flex flex-col items-center justify-center py-32 text-gray-600">
      <div class="w-24 h-24 bg-gradient-to-br from-[#161823] to-black rounded-full flex items-center justify-center mb-6 shadow-lg border border-white/5">
        <search-outlined class="text-4xl text-[#fe2c55] opacity-80" />
      </div>
      <p class="text-base font-medium text-gray-400">搜索你感兴趣的内容</p>
      <div class="mt-8 flex flex-wrap justify-center gap-2 px-8 max-w-sm">
        <span class="px-3 py-1 bg-[#161823] rounded-full text-xs text-gray-500 cursor-pointer hover:bg-[#252525] hover:text-white transition-colors">热门视频</span>
        <span class="px-3 py-1 bg-[#161823] rounded-full text-xs text-gray-500 cursor-pointer hover:bg-[#252525] hover:text-white transition-colors">最新发布</span>
        <span class="px-3 py-1 bg-[#161823] rounded-full text-xs text-gray-500 cursor-pointer hover:bg-[#252525] hover:text-white transition-colors">推荐用户</span>
      </div>
    </div>
    
    <!-- Loading -->
    <div v-if="loading" class="fixed inset-0 top-[60px] bg-black/50 flex items-center justify-center z-40">
      <div class="w-10 h-10 border-4 border-white/20 border-t-[#fe2c55] rounded-full animate-spin"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { videoApi } from '../api'
import { SearchOutlined, PlayCircleFilled, HeartOutlined, CloseCircleFilled } from '@ant-design/icons-vue'
import { DEFAULT_AVATAR, resolveAvatarUrl } from '@/utils/constants'

const router = useRouter()
const route = useRoute()
const keyword = ref('')
const videoList = ref([])
const loading = ref(false)
const searched = ref(false)

const defaultCover = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="200" height="300" viewBox="0 0 200 300"%3E%3Crect fill="%23222" width="200" height="300"/%3E%3C/svg%3E'

const handleSearch = async () => {
  if (!keyword.value.trim()) return
  loading.value = true
  searched.value = true
  router.push({ query: { keyword: keyword.value } })
  try {
    const res = await videoApi.search(keyword.value)
    if (res.code === 200) videoList.value = res.data.list
  } finally {
    loading.value = false
  }
}

const goToVideo = (video) => router.push(`/video/${video.id}`)
const formatCount = (c) => {
  if (!c) return '0'
  if (c >= 10000) return (c / 10000).toFixed(1) + 'w'
  return c.toString()
}

onMounted(() => {
  if (route.query.keyword) {
    keyword.value = route.query.keyword
    handleSearch()
  }
})

watch(() => route.query.keyword, (val) => {
  if (val && val !== keyword.value) {
    keyword.value = val
    handleSearch()
  }
})
</script>
