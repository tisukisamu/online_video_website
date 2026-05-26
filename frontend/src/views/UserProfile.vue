<template>
  <div class="min-h-screen bg-[#161823] text-white relative">
    <!-- Header with Back Button (Sticky) -->
    <div 
      class="fixed top-0 left-0 right-0 z-50 flex items-center justify-between px-4 h-[60px] transition-all duration-300" 
      :class="{ 'bg-[#161823] border-b border-white/5': scrollY > 100 }"
    >
      <div 
        class="w-8 h-8 flex items-center justify-center rounded-full transition-colors cursor-pointer" 
        :class="scrollY > 100 ? 'hover:bg-white/10' : 'bg-black/30 backdrop-blur-sm hover:bg-black/50'"
        @click="$router.back()"
      >
        <arrow-left-outlined class="text-white text-lg" />
      </div>
      <span 
        class="text-white text-base font-bold transition-opacity duration-300" 
        :class="{ 'opacity-0': scrollY < 100, 'opacity-100': scrollY >= 100 }"
      >
        {{ user?.name }}
      </span>
      <div class="w-8"></div>
    </div>

    <!-- Main Content -->
    <div>
      <!-- Cover Image Area (Fixed Height) -->
      <div class="h-[160px] md:h-[200px] relative w-full overflow-hidden bg-gray-900">
         <div 
          class="absolute inset-0 bg-cover bg-center"
          :style="{ 
            backgroundImage: `url(${resolveAvatarUrl(user?.avatar) || 'https://picsum.photos/800/600'})`,
            filter: 'blur(20px) brightness(0.8)',
            transform: 'scale(1.1)'
          }"
        ></div>
        <div class="absolute inset-0 bg-gradient-to-b from-transparent to-[#161823]"></div>
      </div>

      <!-- User Info Container -->
      <div class="px-5 relative -mt-12 z-10 pb-6">
        <div class="flex items-end justify-between mb-4">
          <!-- Avatar -->
          <div class="relative">
             <div class="w-[96px] h-[96px] rounded-full p-[3px] bg-[#161823]">
                <a-avatar :size="90" :src="resolveAvatarUrl(user?.avatar) || DEFAULT_AVATAR" class="border border-white/10" />
             </div>
          </div>
          
          <!-- Action Buttons -->
          <div class="flex gap-2 mb-3" v-if="!isCurrentUser">
            <button 
              class="min-w-[88px] h-9 px-4 rounded-[4px] font-medium text-sm transition-colors border-none cursor-pointer flex items-center justify-center"
              :class="isFollowing ? 'bg-[#3a3a44] text-white hover:bg-[#4a4a55]' : 'bg-[#fe2c55] text-white hover:bg-[#e62548]'"
              @click="toggleFollow"
            >
              {{ isFollowing ? '已关注' : '关注' }}
            </button>
            <button
              v-if="isFollowing"
              class="min-w-[88px] h-9 px-4 rounded-[4px] font-medium text-sm transition-colors border-none cursor-pointer bg-[#3a3a44] text-white hover:bg-[#4a4a55] flex items-center justify-center"
              @click="openChat"
            >
              私聊
            </button>
          </div>
          <div class="flex gap-2 mb-3" v-else>
             <button class="min-w-[136px] h-9 px-4 rounded-[4px] font-medium text-sm bg-[#3a3a44] text-white hover:bg-[#4a4a55] transition-colors border-none cursor-pointer flex items-center justify-center" @click="$router.push('/settings')">
               编辑资料
             </button>
          </div>
        </div>

        <!-- Name & ID -->
        <div class="mb-4">
          <h2 class="text-2xl font-bold text-white mb-1">{{ user?.name }}</h2>
          <div class="text-white/60 text-sm flex items-center gap-2">
            <span>Aouku号：{{ user?.username || user?.id }}</span>
            <copy-outlined class="cursor-pointer hover:text-white transition-colors" @click="copyId" />
          </div>
        </div>

        <!-- Stats Divider Line -->
        <div class="w-full h-[1px] bg-white/5 mb-4"></div>
        
        <!-- Stats -->
        <div class="flex items-center gap-6 mb-4">
          <div class="flex items-baseline gap-1.5 cursor-pointer hover:opacity-80 transition-opacity">
            <span class="text-white font-bold text-lg">{{ formatCount(user?.likeCount) }}</span>
            <span class="text-white/60 text-sm">获赞</span>
          </div>
          <div class="flex items-baseline gap-1.5 cursor-pointer hover:opacity-80 transition-opacity" @click="$router.push(`/following/${user?.id}`)">
            <span class="text-white font-bold text-lg">{{ formatCount(user?.followingCount) }}</span>
            <span class="text-white/60 text-sm">关注</span>
          </div>
          <div class="flex items-baseline gap-1.5 cursor-pointer hover:opacity-80 transition-opacity" @click="$router.push(`/followers/${user?.id}`)">
            <span class="text-white font-bold text-lg">{{ formatCount(user?.followerCount) }}</span>
            <span class="text-white/60 text-sm">粉丝</span>
          </div>
        </div>

        <!-- Bio -->
        <div class="relative group">
           <p class="text-white/90 text-sm leading-relaxed whitespace-pre-wrap">{{ user?.bio || '添加个人简介，让大家更好地认识你' }}</p>
        </div>
      </div>

      <!-- Tabs (Sticky) -->
      <div class="sticky top-[60px] z-40 bg-[#161823] border-b border-white/5 h-[44px]">
        <div class="flex h-full max-w-[400px]">
          <div 
            v-for="tab in ['videos', 'likes']" 
            :key="tab"
            class="flex-1 flex items-center justify-center cursor-pointer relative transition-colors h-full"
            :class="activeTab === tab ? 'text-white font-medium' : 'text-white/60 hover:text-white/80'"
            @click="activeTab = tab"
          >
            <span class="text-[15px]">{{ tab === 'videos' ? '作品' : '喜欢' }}</span>
            <span class="text-xs ml-1 opacity-60 font-normal" v-if="tab === 'videos'">{{ user?.videoCount }}</span>
            <div 
              class="absolute bottom-0 w-12 h-[2px] bg-[#face15] transition-all duration-300 rounded-full"
              :class="activeTab === tab ? 'opacity-100 scale-100' : 'opacity-0 scale-0'"
            ></div>
          </div>
        </div>
      </div>
      
      <!-- Content Grid -->
      <div v-if="activeTab === 'videos'" class="min-h-[400px] bg-[#161823]">
        <div v-if="videos.length > 0" class="grid grid-cols-3 gap-[1px]">
          <div 
            v-for="video in videos" 
            :key="video.id" 
            class="relative aspect-[3/4] bg-[#252533] cursor-pointer group overflow-hidden" 
            @click="goToVideo(video.id)"
          >
            <img :src="video.coverUrl || defaultCover" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105" />
            <div class="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
            <div class="absolute bottom-1.5 left-2 flex items-center gap-1 text-white text-xs drop-shadow-md font-medium">
              <play-circle-outlined /> {{ formatCount(video.viewCount) }}
            </div>
            <!-- 置顶标识示例 (如果有) -->
            <!-- <div class="absolute top-1.5 left-2 bg-[#fe2c55] text-white text-[10px] px-1.5 py-0.5 rounded-[2px] font-medium">置顶</div> -->
          </div>
        </div>
        
        <!-- Empty State -->
        <div v-else class="py-32 flex flex-col items-center justify-center text-white/30">
          <div class="w-20 h-20 bg-[#252533] rounded-full flex items-center justify-center mb-4">
            <video-camera-outlined class="text-3xl opacity-50" />
          </div>
          <p class="text-sm">暂无作品</p>
        </div>
      </div>

      <div v-if="activeTab === 'likes'" class="min-h-[400px] bg-[#161823]">
         <div class="py-32 flex flex-col items-center justify-center text-white/30">
          <div class="w-20 h-20 bg-[#252533] rounded-full flex items-center justify-center mb-4">
            <lock-outlined class="text-3xl opacity-50" />
          </div>
          <p class="text-sm">喜欢列表仅自己可见</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { userApi, videoApi, followApi } from '../api'
import { useUserStore } from '../stores/user'
import { 
  PlayCircleOutlined, 
  ArrowLeftOutlined,
  VideoCameraOutlined,
  LockOutlined,
  CopyOutlined
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { DEFAULT_AVATAR, resolveAvatarUrl } from '@/utils/constants'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const user = ref(null)
const videos = ref([])
const isFollowing = ref(false)
const activeTab = ref('videos')
const scrollY = ref(0)

const isCurrentUser = computed(() => user.value?.id === userStore.userInfo?.id)

const defaultCover = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="200" height="300" viewBox="0 0 200 300"%3E%3Crect fill="%23222" width="200" height="300"/%3E%3C/svg%3E'

const fetchUser = async () => {
  try {
    const res = await userApi.getProfile(route.params.id)
    if (res.code === 200) user.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const fetchVideos = async () => {
  try {
    const res = await videoApi.getUserVideos(route.params.id)
    if (res.code === 200) videos.value = res.data.list
  } catch (e) {
    console.error(e)
  }
}

const toggleFollow = async () => {
  if (!userStore.isLoggedIn) return router.push('/login')
  try {
    if (isFollowing.value) {
      await followApi.unfollow(user.value.id)
      isFollowing.value = false
      user.value.followerCount--
    } else {
      await followApi.follow(user.value.id)
      isFollowing.value = true
      user.value.followerCount++
    }
  } catch (e) {
    console.error(e)
  }
}

const openChat = () => {
  if (!userStore.isLoggedIn) return router.push('/login')
  router.push(`/messages/${user.value.id}`)
}

const goToVideo = (id) => router.push(`/video/${id}`)

const copyId = () => {
  const id = user.value?.username || user.value?.id
  if (id) {
    navigator.clipboard.writeText(id.toString())
    message.success('复制成功')
  }
}

const formatCount = (c) => {
  if (!c) return '0'
  if (c >= 10000) return (c/10000).toFixed(1)+'w'
  if (c >= 1000) return (c/1000).toFixed(1)+'k'
  return c.toString()
}

const handleScroll = () => {
  scrollY.value = window.scrollY
}

onMounted(async () => {
  window.addEventListener('scroll', handleScroll)
  await fetchUser()
  await fetchVideos()
  if (userStore.isLoggedIn && !isCurrentUser.value) {
    try {
      const res = await followApi.isFollowing(user.value.id)
      if (res.code === 200) isFollowing.value = res.data
    } catch (e) {
      console.error(e)
    }
  }
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>