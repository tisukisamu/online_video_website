<template>
  <div class="min-h-screen bg-black text-white pb-20">
    <div class="fixed top-0 left-0 right-0 z-50 flex items-center px-4 h-[60px] bg-black border-b border-white/10">
      <div class="w-8 h-8 flex items-center justify-center rounded-full active:bg-white/10 transition-colors cursor-pointer" @click="$router.back()">
        <arrow-left-outlined class="text-white text-lg" />
      </div>
      <span class="text-white text-base font-medium ml-4">关注 ({{ total }})</span>
    </div>

    <div class="pt-[60px]">
      <div v-if="loading" class="flex justify-center py-20">
        <loading-outlined class="text-2xl text-gray-500 animate-spin" />
      </div>

      <div v-else-if="list.length === 0" class="flex flex-col items-center justify-center py-20 text-gray-500">
        <user-outlined class="text-4xl mb-4" />
        <p class="text-sm">暂无关注</p>
      </div>

      <div v-else class="divide-y divide-white/5">
        <div 
          v-for="user in list" 
          :key="user.id" 
          class="flex items-center justify-between px-4 py-3 active:bg-white/5 transition-colors"
        >
          <div class="flex items-center gap-3 flex-1 cursor-pointer" @click="$router.push(`/user/${user.id}`)">
            <a-avatar :size="44" :src="resolveAvatarUrl(user.avatar) || DEFAULT_AVATAR" class="flex-shrink-0" />
            <div class="flex-1 min-w-0">
              <div class="text-white font-medium text-sm truncate">{{ user.name }}</div>
              <div class="text-gray-500 text-xs truncate">{{ user.bio || '这个人很懒，什么都没写~' }}</div>
            </div>
          </div>
          <div v-if="!isSelf" class="flex items-center gap-2">
            <button
              v-if="user.isFollowing"
              class="px-3 py-1.5 rounded-full text-xs font-medium transition-all duration-300 border-none cursor-pointer bg-[#252533] text-white hover:bg-[#2f2f3f]"
              @click="openChat(user)"
            >
              私聊
            </button>
            <button 
              class="px-4 py-1.5 rounded-full text-xs font-medium transition-all duration-300 border-none cursor-pointer"
              :class="user.isFollowing ? 'bg-[#3a3a44] text-white hover:bg-[#4a4a55]' : 'bg-[#fe2c55] text-white hover:bg-[#e62548]'"
              @click="toggleFollow(user)"
            >
              {{ user.isFollowing ? '已关注' : '关注' }}
            </button>
          </div>
        </div>
      </div>

      <div v-if="hasMore && !loading" class="py-4 text-center">
        <button class="text-gray-500 text-sm hover:text-white transition-colors" @click="loadMore">
          加载更多
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { followApi } from '../api'
import { useUserStore } from '../stores/user'
import { message } from 'ant-design-vue'
import { DEFAULT_AVATAR, resolveAvatarUrl } from '@/utils/constants'
import { ArrowLeftOutlined, LoadingOutlined, UserOutlined } from '@ant-design/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const list = ref([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)
const hasMore = ref(true)

const userId = computed(() => route.params.id ? Number(route.params.id) : userStore.userInfo?.id)
const isSelf = computed(() => userId.value === userStore.userInfo?.id)

const fetchData = async () => {
  if (!userId.value) return
  
  loading.value = true
  try {
    const res = await followApi.getUserFollowing(userId.value, page.value, 20)
    if (res.code === 200) {
      const newList = res.data.list.map(u => ({ ...u, isFollowing: isSelf.value ? true : u.isFollowing }))
      if (page.value === 1) {
        list.value = newList
      } else {
        list.value.push(...newList)
      }
      total.value = res.data.pagination.total
      hasMore.value = page.value < res.data.pagination.totalPages
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  if (hasMore.value && !loading.value) {
    page.value++
    fetchData()
  }
}

const toggleFollow = async (user) => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  try {
    if (user.isFollowing) {
      await followApi.unfollow(user.id)
      user.isFollowing = false
    } else {
      await followApi.follow(user.id)
      user.isFollowing = true
    }
  } catch (e) {
    message.error('操作失败')
  }
}

const openChat = (user) => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  router.push(`/messages/${user.id}`)
}

onMounted(fetchData)
</script>
