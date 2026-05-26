<template>
  <div class="min-h-screen bg-black text-white pt-[60px] pb-20">
    <div class="sticky top-[60px] z-10 bg-black/90 backdrop-blur-md px-4 py-3 border-b border-white/5 flex items-center justify-between">
      <h3 class="text-lg font-bold text-white m-0">我的关注</h3>
    </div>
    
    <div v-if="!userStore.isLoggedIn" class="flex flex-col items-center justify-center py-20 text-gray-500">
      <p class="mb-4 text-sm">登录后查看关注动态</p>
      <button class="px-8 py-2 bg-[#fe2c55] text-white rounded-full font-medium hover:bg-[#e62548] transition-colors" @click="$router.push('/login')">
        去登录
      </button>
    </div>
    
    <div v-else-if="videoList.length === 0 && !loading" class="flex flex-col items-center justify-center py-20 text-gray-500">
      <p class="mb-4 text-sm">暂无关注用户的视频</p>
      <button class="px-8 py-2 bg-[#fe2c55] text-white rounded-full font-medium hover:bg-[#e62548] transition-colors" @click="$router.push('/recommend')">
        去发现
      </button>
    </div>
    
    <div v-else class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-1 p-1">
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

        <!-- Bottom Info Gradient -->
        <div class="absolute inset-x-0 bottom-0 p-3 bg-gradient-to-t from-black/80 to-transparent">
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { videoApi } from '../api'
import { useUserStore } from '../stores/user'
import { PlayCircleFilled } from '@ant-design/icons-vue'
import { DEFAULT_AVATAR, resolveAvatarUrl } from '@/utils/constants'

const router = useRouter()
const userStore = useUserStore()
const videoList = ref([])
const loading = ref(false)

const defaultCover = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="200" height="300" viewBox="0 0 200 300"%3E%3Crect fill="%23222" width="200" height="300"/%3E%3C/svg%3E'

const fetchVideos = async () => {
  if (!userStore.isLoggedIn) return
  loading.value = true
  try {
    const res = await videoApi.getFollowVideos()
    if (res.code === 200) videoList.value = res.data.list
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const goToVideo = (video) => router.push(`/video/${video.id}`)

onMounted(fetchVideos)
</script>
