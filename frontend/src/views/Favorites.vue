<template>
  <div class="min-h-screen bg-black text-white pt-[60px] pb-20">
    <div class="sticky top-[60px] z-10 bg-black/90 backdrop-blur-md px-4 py-3 border-b border-white/5 flex items-center justify-between">
      <div class="w-8 h-8 flex items-center justify-center rounded-full active:bg-white/10 transition-colors" @click="$router.back()">
        <arrow-left-outlined class="text-white text-lg" />
      </div>
      <span class="text-white text-base font-bold">我的收藏</span>
      <div class="w-8"></div>
    </div>
    
    <div v-if="videoList.length === 0 && !loading" class="flex flex-col items-center justify-center py-20 text-gray-500">
      <div class="w-20 h-20 bg-[#161823] rounded-full flex items-center justify-center mb-4">
        <star-outlined class="text-4xl text-gray-600" />
      </div>
      <p class="mb-6 text-sm">暂无收藏</p>
      <button class="px-8 py-2 bg-[#fe2c55] text-white rounded-full font-medium hover:bg-[#e62548] transition-colors" @click="$router.push('/recommend')">
        去发现
      </button>
    </div>
    
    <div v-else class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-1 p-1">
      <div v-for="video in videoList" :key="video.id" class="relative aspect-[3/4] bg-[#161823] cursor-pointer group overflow-hidden rounded-sm" @click="goToVideo(video.id)">
        <img :src="video.coverUrl || defaultCover" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105" />
        
        <!-- Hover Overlay -->
        <div class="absolute inset-0 bg-black/20 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
          <play-circle-filled class="text-4xl text-white/80 drop-shadow-lg scale-0 group-hover:scale-100 transition-transform duration-300" />
        </div>

        <!-- Remove Button -->
        <button 
          class="absolute top-2 right-2 w-8 h-8 bg-black/50 backdrop-blur-sm rounded-full flex items-center justify-center text-white/80 hover:bg-black/70 hover:text-white transition-all opacity-0 group-hover:opacity-100 z-10 border-none cursor-pointer"
          @click.stop="removeFavorite(video.id)"
        >
          <close-outlined class="text-sm" />
        </button>

        <!-- Bottom Info Gradient -->
        <div class="absolute inset-x-0 bottom-0 p-3 bg-gradient-to-t from-black/80 to-transparent">
          <p class="text-white text-sm font-medium mb-1 line-clamp-2 shadow-sm">{{ video.title }}</p>
          <span class="text-gray-300 text-xs truncate block">{{ video.author?.name }}</span>
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
import { favoriteApi } from '../api'
import { message } from 'ant-design-vue'
import { ArrowLeftOutlined, StarOutlined, PlayCircleFilled, CloseOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const videoList = ref([])
const loading = ref(false)

const defaultCover = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="200" height="300" viewBox="0 0 200 300"%3E%3Crect fill="%23222" width="200" height="300"/%3E%3C/svg%3E'

const fetchFavorites = async () => {
  loading.value = true
  try {
    const res = await favoriteApi.getList()
    if (res.code === 200) videoList.value = res.data.list
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const removeFavorite = async (videoId) => {
  try {
    await favoriteApi.remove(videoId)
    videoList.value = videoList.value.filter(v => v.id !== videoId)
    message.success('已取消收藏')
  } catch (error) {
    message.error('操作失败')
  }
}

const goToVideo = (id) => router.push(`/video/${id}`)

onMounted(fetchFavorites)
</script>
