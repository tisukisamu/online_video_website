<template>
  <div class="min-h-screen bg-[#161823] text-white">
    <div class="sticky top-0 z-50 bg-[#161823] border-b border-white/5">
      <div class="flex items-center justify-between px-4 h-[60px]">
        <div class="w-8"></div>
        <h1 class="text-lg font-bold">消息</h1>
        <div class="w-8"></div>
      </div>
    </div>

    <div class="max-w-2xl mx-auto">
      <div v-if="loading" class="flex justify-center py-20">
        <loading-outlined class="text-3xl text-gray-500 animate-spin" />
      </div>

      <div v-else-if="followingList.length === 0" class="flex flex-col items-center justify-center py-32 text-gray-500">
        <user-outlined class="text-5xl mb-4 opacity-50" />
        <p class="text-sm">暂无关注的用户</p>
        <p class="text-xs mt-2 opacity-60">关注用户后即可发送私信</p>
        <button 
          class="mt-6 px-6 py-2 bg-[#fe2c55] text-white rounded-lg text-sm font-medium hover:bg-[#e62548] transition-colors"
          @click="$router.push('/')"
        >
          去发现
        </button>
      </div>

      <div v-else class="divide-y divide-white/5">
        <div 
          v-for="user in followingList" 
          :key="user.id" 
          class="flex items-center gap-4 px-4 py-4 cursor-pointer transition-colors hover:bg-white/5"
          @click="openChat(user)"
        >
          <a-avatar :size="50" :src="resolveAvatarUrl(user.avatar) || DEFAULT_AVATAR" class="flex-shrink-0 border border-white/10" />
          <div class="flex-1 min-w-0">
            <div class="flex items-center justify-between mb-1">
              <span class="text-white font-medium truncate">{{ user.name || user.username }}</span>
              <span class="text-gray-500 text-xs">@{{ user.username }}</span>
            </div>
            <p class="text-gray-400 text-sm truncate">{{ user.bio || '这个人很懒，什么都没写' }}</p>
          </div>
          <right-outlined class="text-gray-500 text-sm flex-shrink-0" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { followApi } from '../api'
import { useUserStore } from '../stores/user'
import { DEFAULT_AVATAR, resolveAvatarUrl } from '@/utils/constants'
import { 
  LoadingOutlined, 
  UserOutlined, 
  RightOutlined 
} from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const followingList = ref([])
const loading = ref(false)

const fetchFollowing = async () => {
  loading.value = true
  try {
    const res = await followApi.getFollowing(1, 100)
    if (res.code === 200) {
      followingList.value = res.data?.list || []
    }
  } catch (e) {
    console.error('获取关注列表失败:', e)
  } finally {
    loading.value = false
  }
}

const openChat = (user) => {
  router.push(`/messages/${user.id}`)
}

onMounted(() => {
  if (userStore.isLoggedIn) {
    fetchFollowing()
  } else {
    router.push('/login')
  }
})
</script>
