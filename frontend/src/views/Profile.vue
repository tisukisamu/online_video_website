<template>
  <div class="min-h-screen bg-[#161823] text-white pb-20 relative">
    <!-- Header (Sticky) -->
    <div 
      class="fixed top-0 left-0 right-0 z-50 flex items-center justify-between px-4 h-[60px] transition-all duration-300" 
      :class="{ 'bg-[#161823] border-b border-white/5': scrollY > 100 }"
    >
      <div class="w-8"></div>
      <span 
        class="text-white text-base font-bold transition-opacity duration-300" 
        :class="{ 'opacity-0': scrollY < 100, 'opacity-100': scrollY >= 100 }"
      >
        {{ userStore.userInfo?.name }}
      </span>
      <div class="flex items-center gap-4">
         <div class="w-8 h-8 flex items-center justify-center rounded-full bg-black/30 backdrop-blur-sm cursor-pointer hover:bg-white/10 transition-colors" @click="showDrawer = true">
            <menu-outlined class="text-white text-lg" />
         </div>
      </div>
    </div>

    <!-- Main Content -->
    <div>
      <!-- Cover Image Area -->
      <div class="h-[160px] md:h-[200px] relative w-full overflow-hidden bg-gray-900">
         <div 
          class="absolute inset-0 bg-cover bg-center"
          :style="{ 
            backgroundImage: `url(${resolveAvatarUrl(userStore.userInfo?.avatar) || 'https://picsum.photos/800/600'})`,
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
                <a-avatar :size="90" :src="resolveAvatarUrl(userStore.userInfo?.avatar) || DEFAULT_AVATAR" class="border border-white/10" />
             </div>
          </div>
          
          <!-- Action Buttons -->
          <div class="flex gap-2 mb-3">
             <button class="min-w-[136px] h-9 px-4 rounded-[4px] font-medium text-sm bg-[#3a3a44] text-white hover:bg-[#4a4a55] transition-colors border-none cursor-pointer flex items-center justify-center" @click="$router.push('/profile/edit')">
               编辑资料
             </button>
          </div>
        </div>

        <!-- Name & ID -->
        <div class="mb-4">
          <h2 class="text-2xl font-bold text-white mb-1">{{ userStore.userInfo?.name || '未登录用户' }}</h2>
          <div class="text-white/60 text-sm flex items-center gap-2">
            <span>Aouku号：{{ userStore.userInfo?.username || userStore.userInfo?.id || 'user' }}</span>
            <copy-outlined class="cursor-pointer hover:text-white transition-colors" @click="copyId" />
          </div>
        </div>

        <!-- Stats Divider Line -->
        <div class="w-full h-[1px] bg-white/5 mb-4"></div>
        
        <!-- Stats -->
        <div class="flex items-center gap-6 mb-4">
          <div class="flex items-baseline gap-1.5 cursor-pointer hover:opacity-80 transition-opacity">
            <span class="text-white font-bold text-lg">{{ formatCount(stats.likeCount || 0) }}</span>
            <span class="text-white/60 text-sm">获赞</span>
          </div>
          <div class="flex items-baseline gap-1.5 cursor-pointer hover:opacity-80 transition-opacity" @click="$router.push('/following')">
            <span class="text-white font-bold text-lg">{{ formatCount(stats.followingCount) }}</span>
            <span class="text-white/60 text-sm">关注</span>
          </div>
          <div class="flex items-baseline gap-1.5 cursor-pointer hover:opacity-80 transition-opacity" @click="$router.push('/followers')">
            <span class="text-white font-bold text-lg">{{ formatCount(stats.followerCount) }}</span>
            <span class="text-white/60 text-sm">粉丝</span>
          </div>
        </div>

        <!-- Bio -->
        <div class="relative group">
           <p class="text-white/90 text-sm leading-relaxed whitespace-pre-wrap">{{ userStore.userInfo?.bio || '点击添加个人简介，让大家更好地认识你' }}</p>
        </div>
      </div>

      <!-- Tabs (Sticky) -->
      <div class="sticky top-[60px] z-40 bg-[#161823] border-b border-white/5 h-[44px]">
        <div class="flex h-full max-w-[400px]">
          <div 
            v-for="tab in ['videos', 'favorites', 'likes']" 
            :key="tab"
            class="flex-1 flex items-center justify-center cursor-pointer relative transition-colors h-full"
            :class="activeTab === tab ? 'text-white font-medium' : 'text-white/60 hover:text-white/80'"
            @click="activeTab = tab"
          >
            <span class="text-[15px]">
              {{ tab === 'videos' ? '作品' : (tab === 'favorites' ? '收藏' : '喜欢') }}
            </span>
            <span class="text-xs ml-1 opacity-60 font-normal" v-if="tab === 'videos'">{{ stats.videoCount || 0 }}</span>
            <div 
              class="absolute bottom-0 w-8 h-[2px] bg-[#face15] transition-all duration-300 rounded-full"
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
            @click="$router.push(`/video/${video.id}`)"
          >
            <img :src="video.coverUrl" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105" />
            <div class="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
            <div class="absolute bottom-1.5 left-2 flex items-center gap-1 text-white text-xs drop-shadow-md font-medium">
              <play-circle-outlined /> {{ formatCount(video.viewCount) }}
            </div>
            
             <!-- Status Badge -->
             <div 
               v-if="video.status !== 'PUBLISHED'" 
               class="absolute top-1.5 right-1.5 px-1.5 py-0.5 rounded-[2px] text-[10px] font-medium shadow-sm backdrop-blur-sm"
               :class="{
                 'bg-yellow-500/90 text-white': video.status === 'PENDING',
                 'bg-red-500/90 text-white': ['REJECTED', 'BANNED'].includes(video.status),
                 'bg-gray-500/90 text-white': video.status === 'DRAFT'
               }"
             >
               {{ video.status === 'PENDING' ? '审核中' : (video.status === 'REJECTED' ? '未通过' : (video.status === 'DRAFT' ? '草稿' : '已封禁')) }}
             </div>
          </div>
        </div>
        
        <!-- Empty State -->
        <div v-else class="py-32 flex flex-col items-center justify-center text-white/30">
          <div class="w-20 h-20 bg-[#252533] rounded-full flex items-center justify-center mb-4">
            <video-camera-outlined class="text-3xl opacity-50" />
          </div>
          <p class="text-sm">暂无作品</p>
          <button class="mt-4 px-6 py-2 bg-[#fe2c55] text-white rounded-[4px] text-sm font-medium hover:bg-[#e62548] transition-colors border-none cursor-pointer" @click="$router.push('/upload')">
            去发布
          </button>
        </div>
      </div>

      <div v-if="activeTab === 'favorites'" class="min-h-[400px] bg-[#161823]">
        <div v-if="favorites.length > 0" class="grid grid-cols-3 gap-[1px]">
          <div 
            v-for="video in favorites" 
            :key="video.id" 
            class="relative aspect-[3/4] bg-[#252533] cursor-pointer group overflow-hidden" 
            @click="$router.push(`/video/${video.id}`)"
          >
            <img :src="video.coverUrl" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105" />
            <div class="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
            <div class="absolute bottom-1.5 left-2 flex items-center gap-1 text-white text-xs drop-shadow-md font-medium">
              <play-circle-outlined /> {{ formatCount(video.viewCount) }}
            </div>
          </div>
        </div>
        <div v-else class="py-32 flex flex-col items-center justify-center text-white/30">
          <div class="w-20 h-20 bg-[#252533] rounded-full flex items-center justify-center mb-4">
            <star-outlined class="text-3xl opacity-50" />
          </div>
          <p class="text-sm">暂无收藏内容</p>
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
    
    <!-- Side Drawer for Menu -->
    <a-drawer
      placement="right"
      :closable="false"
      :visible="showDrawer"
      @close="showDrawer = false"
      width="280"
      :bodyStyle="{ padding: 0, backgroundColor: '#161823', color: 'white' }"
    >
      <div class="flex flex-col h-full">
        <div class="p-4 border-b border-white/5">
          <h3 class="text-white text-lg font-bold">账号管理</h3>
        </div>
        <div class="flex-1 overflow-y-auto py-2">
           <div class="flex items-center gap-3 px-4 py-3 hover:bg-white/5 cursor-pointer transition-colors" @click="$router.push('/my/videos'); showDrawer = false">
             <video-camera-outlined class="text-lg text-white/80" />
             <span class="text-white/90 text-sm">作品管理</span>
           </div>
           <div class="flex items-center gap-3 px-4 py-3 hover:bg-white/5 cursor-pointer transition-colors" @click="$router.push('/profile/edit'); showDrawer = false">
             <setting-outlined class="text-lg text-white/80" />
             <span class="text-white/90 text-sm">编辑资料</span>
           </div>
           <div v-if="userStore.isAdmin" class="flex items-center gap-3 px-4 py-3 hover:bg-white/5 cursor-pointer transition-colors" @click="$router.push('/admin'); showDrawer = false">
             <safety-certificate-outlined class="text-lg text-white/80" />
             <span class="text-white/90 text-sm">管理后台</span>
           </div>
        </div>
        <div class="p-4 border-t border-white/5">
           <div class="flex items-center gap-3 px-2 py-2 hover:bg-white/5 cursor-pointer transition-colors rounded-lg text-[#fe2c55]" @click="handleLogout">
             <logout-outlined class="text-lg" />
             <span class="font-medium text-sm">退出登录</span>
           </div>
        </div>
      </div>
    </a-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { userApi, favoriteApi } from '../api'
import { useUserStore } from '../stores/user'
import { DEFAULT_AVATAR, resolveAvatarUrl } from '@/utils/constants'
import { message } from 'ant-design-vue'
import {
  StarOutlined, VideoCameraOutlined, SettingOutlined,
  LogoutOutlined, RightOutlined, PlayCircleOutlined,
  ArrowLeftOutlined, CopyOutlined, MenuOutlined,
  LockOutlined, SafetyCertificateOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const stats = ref({})
const videos = ref([])
const favorites = ref([])
const activeTab = ref('videos')
const scrollY = ref(0)
const showDrawer = ref(false)
const loading = ref(false)

const fetchProfile = async () => {
  try {
    const [userRes, videosRes] = await Promise.all([
      userApi.getCurrentUser(),
      userApi.getMyVideos(1, 100)
    ])
    
    if (userRes.code === 200) stats.value = userRes.data
    if (videosRes.code === 200) videos.value = (videosRes.data?.list || []).slice(0, 30)
  } catch (e) {
    console.error(e)
  }
}

const fetchFavorites = async () => {
  if (loading.value) return
  loading.value = true
  try {
    const res = await favoriteApi.getList(1, 100)
    if (res.code === 200) {
      favorites.value = (res.data?.list || []).slice(0, 30)
    }
  } catch (e) {
    console.error('获取收藏列表失败:', e)
  } finally {
    loading.value = false
  }
}

watch(activeTab, (newTab) => {
  if (newTab === 'favorites' && favorites.value.length === 0) {
    fetchFavorites()
  }
})

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

const formatCount = (c) => {
  if (!c) return '0'
  if (c >= 10000) return (c / 10000).toFixed(1) + 'w'
  return c.toString()
}

const copyId = () => {
  const id = userStore.userInfo?.username || userStore.userInfo?.id
  if (id) {
    navigator.clipboard.writeText(id.toString())
    message.success('复制成功')
  }
}

const handleScroll = () => {
  scrollY.value = window.scrollY
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  fetchProfile()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>