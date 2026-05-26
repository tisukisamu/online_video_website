<template>
  <div class="w-full min-h-screen bg-black relative flex flex-col md:flex-row">
    <!-- Desktop Header (New) -->
    <div class="hidden md:flex fixed top-0 left-[240px] right-0 h-[60px] bg-[#161823] border-b border-white/5 z-40 items-center justify-between px-6">
      <!-- Search Bar -->
      <div class="relative w-[360px]">
        <input
          v-model="searchKeyword"
          type="text"
          placeholder="搜索视频、用户"
          class="w-full h-10 bg-[#252533] rounded-full text-white pl-10 pr-4 outline-none border-none placeholder-gray-500 focus:bg-[#2f2f3f] transition-colors"
          @keyup.enter="handleSearch"
        />
        <search-outlined class="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-500 text-lg" />
        <div 
          class="absolute right-1 top-1/2 -translate-y-1/2 w-8 h-8 flex items-center justify-center cursor-pointer hover:bg-white/10 rounded-full transition-colors"
          @click="handleSearch"
        >
          <span class="text-[#fe2c55] font-medium text-sm">搜索</span>
        </div>
      </div>

      <!-- Right Actions -->
      <div class="flex items-center gap-4">
        <div 
          v-if="userStore.isLoggedIn" 
          class="relative cursor-pointer hover:opacity-80 transition-opacity" 
          @click="$router.push('/messages')"
        >
          <message-outlined class="text-xl text-gray-300" />
        </div>
        <div class="flex items-center gap-1 cursor-pointer hover:opacity-80 transition-opacity" @click="$router.push('/upload')">
          <cloud-upload-outlined class="text-xl text-gray-300" />
          <span class="text-sm text-gray-300">上传</span>
        </div>
        <div class="w-[1px] h-4 bg-white/10"></div>
        <div v-if="userStore.isLoggedIn" class="flex items-center gap-3 cursor-pointer hover:opacity-80" @click="$router.push('/profile')">
          <span class="text-sm text-gray-300">{{ userStore.username }}</span>
          <a-avatar :size="32" :src="resolveAvatarUrl(userStore.avatar) || DEFAULT_AVATAR" />
        </div>
        <button v-else class="px-6 py-1.5 bg-[#fe2c55] text-white rounded text-sm font-medium hover:bg-[#fe2c55]/90 transition-colors" @click="$router.push('/login')">
          登录
        </button>
      </div>
    </div>

    <!-- Desktop Sidebar -->
    <div class="hidden md:flex flex-col w-[240px] h-screen fixed left-0 top-0 bg-[#161823] border-r border-white/5 z-50 p-4">
      <div class="text-2xl font-bold text-[#fe2c55] mb-8 px-4 flex items-center gap-2 cursor-pointer" @click="$router.push('/')">
        <play-circle-filled /> <span>Aouku</span>
      </div>

      <div class="flex flex-col gap-2">
        <div
          v-for="item in menuItems"
          :key="item.path"
          class="flex items-center gap-3 px-4 py-3 rounded-lg cursor-pointer transition-colors"
          :class="currentRoute === item.path ? 'bg-white/10 text-white font-medium' : 'text-gray-400 hover:bg-white/5 hover:text-white'"
          @click="handleNav(item.path)"
        >
          <component :is="item.icon" class="text-xl" />
          <span>{{ item.label }}</span>
        </div>
      </div>

      <div class="mt-auto px-4 py-4 border-t border-white/5">
        <div class="text-xs text-gray-500 leading-relaxed">
          <p class="hover:text-gray-300 cursor-pointer transition-colors">关于我们</p>
          <p class="hover:text-gray-300 cursor-pointer transition-colors">隐私政策</p>
          <p class="mt-2">© Aouku</p>
        </div>
      </div>
    </div>

    <!-- Mobile Top Navigation -->
    <div
      v-if="showTopNav"
      class="md:hidden fixed top-0 left-0 right-0 h-[50px] flex items-center justify-between z-50 px-4 transition-all duration-300"
      :class="isTransparentNav ? 'bg-gradient-to-b from-black/60 to-transparent' : 'bg-black/90 backdrop-blur-md border-b border-white/5'"
    >
      <div class="text-white/80 text-xl cursor-pointer hover:text-white transition-colors" @click="$router.push('/search')">
        <search-outlined />
      </div>

      <div class="flex gap-6 absolute left-1/2 -translate-x-1/2">
        <span
          class="text-[17px] font-medium transition-all cursor-pointer relative py-2 shadow-black drop-shadow-md"
          :class="currentRoute === '/follow' ? 'text-white font-bold scale-105 after:content-[\'\'] after:absolute after:bottom-1 after:left-1/2 after:-translate-x-1/2 after:w-6 after:h-[3px] after:bg-white after:rounded-full' : 'text-white/70 hover:text-white'"
          @click="$router.push('/follow')"
        >
          关注
        </span>
        <span
          class="text-[17px] font-medium transition-all cursor-pointer relative py-2 shadow-black drop-shadow-md"
          :class="currentRoute === '/' ? 'text-white font-bold scale-105 after:content-[\'\'] after:absolute after:bottom-1 after:left-1/2 after:-translate-x-1/2 after:w-6 after:h-[3px] after:bg-white after:rounded-full' : 'text-white/70 hover:text-white'"
          @click="$router.push('/')"
        >
          推荐
        </span>
      </div>

      <div 
        v-if="userStore.isLoggedIn"
        class="text-white/80 text-xl cursor-pointer hover:text-white transition-colors" 
        @click="$router.push('/messages')"
      >
        <message-outlined />
      </div>
      <div v-else class="w-6"></div>
    </div>

    <!-- Main Content Area -->
    <div class="flex-1 relative md:ml-[240px] md:mt-[60px]">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </div>

    <!-- Mobile Bottom Navigation -->
    <div
      v-if="!hideNav"
      class="md:hidden fixed bottom-0 left-0 right-0 h-[50px] bg-black border-t border-white/10 flex justify-around items-center z-50 pb-[env(safe-area-inset-bottom)] px-2"
    >
      <div
        class="flex flex-col items-center gap-[2px] cursor-pointer min-w-[40px] transition-colors"
        :class="currentRoute === '/' ? 'text-white' : 'text-gray-500 hover:text-gray-300'"
        @click="$router.push('/')"
      >
        <span class="text-[16px] font-bold">首页</span>
      </div>

      <div
        class="flex flex-col items-center gap-[2px] cursor-pointer min-w-[40px] transition-colors"
        :class="currentRoute === '/recommend' ? 'text-white' : 'text-gray-500 hover:text-gray-300'"
        @click="$router.push('/recommend')"
      >
        <span class="text-[16px] font-medium">发现</span>
      </div>

      <div
        class="flex items-center justify-center cursor-pointer px-2"
        @click="$router.push('/upload')"
      >
        <div class="w-[48px] h-[32px] relative flex items-center justify-center hover:opacity-90 transition-opacity">
           <div class="absolute left-0 top-0 bottom-0 w-[36px] bg-[#25f4ee] rounded-[8px]"></div>
           <div class="absolute right-0 top-0 bottom-0 w-[36px] bg-[#fe2c55] rounded-[8px]"></div>
           <div class="absolute inset-[2px] left-[2px] right-[2px] bg-white rounded-[6px] flex items-center justify-center z-10">
             <plus-outlined class="text-black text-[18px] font-extrabold" />
           </div>
        </div>
      </div>

      <div
        class="flex flex-col items-center gap-[2px] cursor-pointer min-w-[40px] transition-colors"
        :class="currentRoute === '/favorites' ? 'text-white' : 'text-gray-500 hover:text-gray-300'"
        @click="$router.push('/favorites')"
      >
        <span class="text-[16px] font-medium">收藏</span>
      </div>

      <div
        class="flex flex-col items-center gap-[2px] cursor-pointer min-w-[40px] transition-colors"
        :class="(currentRoute === '/profile' || currentRoute.startsWith('/user')) ? 'text-white' : 'text-gray-500 hover:text-gray-300'"
        @click="goToProfile"
      >
        <span class="text-[16px] font-medium">我</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  HomeFilled,
  HomeOutlined,
  CompassFilled,
  CompassOutlined,
  PlusSquareFilled,
  PlusSquareOutlined,
  MessageFilled,
  MessageOutlined,
  UserOutlined,
  SearchOutlined,
  PlusOutlined,
  HeartOutlined,
  PlayCircleFilled,
  CloudUploadOutlined
} from '@ant-design/icons-vue'
import { DEFAULT_AVATAR, resolveAvatarUrl } from '@/utils/constants'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const currentRoute = computed(() => route.path)
const searchKeyword = ref('')

const menuItems = [
  { path: '/', label: '首页', icon: HomeOutlined },
  { path: '/recommend', label: '发现', icon: CompassOutlined },
  { path: '/follow', label: '关注', icon: UserOutlined },
  { path: '/favorites', label: '收藏', icon: HeartOutlined },
]

const handleNav = (path) => {
  router.push(path)
}

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/search', query: { keyword: searchKeyword.value.trim() } })
  }
}

const hideNav = computed(() => {
  return ['/login', '/register'].some(p => route.path.startsWith(p))
})

// Transparent nav on video feeds for immersion
const isTransparentNav = computed(() => {
  return ['/', '/follow'].includes(route.path)
})

const showTopNav = computed(() => {
  if (hideNav.value) return false
  return ['/', '/follow'].includes(route.path)
})

const goToProfile = () => {
  if (userStore.isLoggedIn) {
    router.push('/profile')
  } else {
    router.push('/login')
  }
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>