<template>
  <div class="fixed inset-0 bg-black text-white flex overflow-hidden z-[100]">
    <div class="flex-1 flex flex-col overflow-hidden">
      <div class="flex-shrink-0 flex items-center gap-3 px-4 h-[60px] bg-black/80 backdrop-blur-md border-b border-white/5 z-50">
        <div 
          class="w-8 h-8 flex items-center justify-center rounded-full active:bg-white/10 transition-colors cursor-pointer" 
          @click="$router.back()"
        >
          <arrow-left-outlined class="text-white text-lg" />
        </div>
        <div class="flex items-center gap-3 flex-1 overflow-hidden" @click="goToProfile">
          <a-avatar :size="36" :src="resolveAvatarUrl(targetUser?.avatar) || DEFAULT_AVATAR" class="flex-shrink-0 border border-white/10" />
          <div class="flex flex-col min-w-0">
            <div class="text-white text-sm font-medium truncate">{{ targetUser?.name || '用户' }}</div>
            <div class="text-xs text-gray-500 truncate" v-if="targetUser?.bio">{{ targetUser.bio }}</div>
          </div>
        </div>
        <div class="w-8"></div>
      </div>

      <div 
        ref="messageListRef"
        class="flex-1 overflow-y-auto px-4 bg-black relative w-full"
        @scroll="handleScroll"
      >
        <div v-if="loading && page === 1" class="flex justify-center py-20">
          <loading-outlined class="text-2xl text-gray-500 animate-spin" />
        </div>

        <div v-else class="space-y-6 py-4 w-full">
          <div v-if="hasMore" class="flex justify-center py-2">
            <button 
              v-if="!loadingMore" 
              class="text-xs text-blue-400 hover:text-blue-300 transition-colors"
              @click="loadMore"
            >
              查看历史消息
            </button>
            <loading-outlined v-else class="text-gray-500 animate-spin text-sm" />
          </div>

          <div v-if="messages.length === 0" class="flex flex-col items-center justify-center py-32 text-gray-500 opacity-60">
            <message-outlined class="text-5xl mb-4" />
            <p class="text-sm">暂无消息，打个招呼吧~</p>
          </div>

          <div v-for="(msg, index) in messages" :key="msg.id" class="w-full">
            <div v-if="shouldShowTime(index)" class="flex justify-center mb-4">
              <span class="text-[10px] text-gray-500 bg-white/5 px-2 py-0.5 rounded-full">{{ formatMessageTime(msg.createdAt) }}</span>
            </div>

            <div 
              class="flex gap-3 mb-1 w-full" 
              :class="msg.senderId === userStore.userInfo?.id ? 'flex-row-reverse' : 'flex-row'"
            >
              <a-avatar 
                :size="36" 
                :src="resolveAvatarUrl(msg.senderId === userStore.userInfo?.id ? userStore.userInfo?.avatar : targetUser?.avatar) || DEFAULT_AVATAR" 
                class="flex-shrink-0 mt-0.5 cursor-pointer border border-white/10"
                @click="goToProfile(msg.senderId)"
              />

              <div 
                class="max-w-[70%] px-4 py-2.5 text-sm leading-relaxed break-words shadow-sm"
                :class="[
                  msg.senderId === userStore.userInfo?.id 
                    ? 'bg-[#fe2c55] text-white rounded-2xl rounded-tr-sm' 
                    : 'bg-[#252533] text-gray-200 rounded-2xl rounded-tl-sm'
                ]"
              >
                {{ msg.content }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="flex-shrink-0 border-t border-white/10 bg-[#161823] px-4 py-3 safe-area-bottom z-50 w-full">
        <div v-if="!isAllowed" class="text-xs text-gray-500 py-3 text-center bg-white/5 rounded-lg mx-2">
          关注对方后才能发送私信哦
        </div>
        <div v-else class="flex items-end gap-3">
          <div class="flex-1 bg-[#252533] rounded-2xl min-h-[40px] flex items-center px-4 py-2 transition-colors focus-within:bg-[#2f2f3f]">
            <input
              v-model="content"
              type="text"
              placeholder="发消息..."
              class="w-full bg-transparent border-none outline-none text-white text-sm placeholder-gray-500"
              @keyup.enter="sendMessage"
            />
          </div>
          <button
            class="w-10 h-10 rounded-full flex items-center justify-center transition-all duration-200 flex-shrink-0"
            :class="content.trim() && !sending ? 'bg-[#fe2c55] text-white shadow-lg active:scale-90' : 'bg-[#252533] text-gray-600 cursor-not-allowed'"
            :disabled="!content.trim() || sending"
            @click="sendMessage"
          >
            <send-outlined v-if="!sending" class="text-lg translate-x-0.5 translate-y-0.5" />
            <loading-outlined v-else class="animate-spin text-lg" />
          </button>
        </div>
      </div>
    </div>

    <div class="hidden lg:flex w-[320px] h-full bg-[#161823] border-l border-white/5 flex-col">
      <div class="flex-shrink-0 px-4 py-4 border-b border-white/5">
        <h3 class="text-white font-bold text-lg">关注列表</h3>
        <p class="text-gray-500 text-xs mt-1">点击用户开始聊天</p>
      </div>
      
      <div class="flex-1 overflow-y-auto">
        <div v-if="loadingFollowing" class="flex justify-center py-10">
          <loading-outlined class="text-2xl text-gray-500 animate-spin" />
        </div>
        
        <div v-else-if="followingList.length === 0" class="flex flex-col items-center justify-center py-20 text-gray-500">
          <user-outlined class="text-4xl mb-3 opacity-50" />
          <p class="text-sm">暂无关注的用户</p>
          <p class="text-xs mt-1 opacity-60">关注用户后即可发送私信</p>
        </div>
        
        <div v-else class="divide-y divide-white/5">
          <div 
            v-for="user in followingList" 
            :key="user.id" 
            class="flex items-center gap-3 px-4 py-3 cursor-pointer transition-colors hover:bg-white/5"
            :class="user.id === targetId ? 'bg-white/10' : ''"
            @click="selectUser(user)"
          >
            <a-avatar :size="44" :src="resolveAvatarUrl(user.avatar) || DEFAULT_AVATAR" class="flex-shrink-0 border border-white/10" />
            <div class="flex-1 min-w-0">
              <div class="text-white text-sm font-medium truncate">{{ user.name || user.username }}</div>
              <div class="text-gray-500 text-xs truncate">{{ user.bio || '这个人很懒，什么都没写' }}</div>
            </div>
            <div 
              v-if="user.id === targetId" 
              class="w-2 h-2 rounded-full bg-[#fe2c55]"
            ></div>
          </div>
        </div>
      </div>
    </div>

    <div 
      class="lg:hidden fixed right-4 bottom-24 z-50"
      @click="showFollowingDrawer = true"
    >
      <div class="w-12 h-12 rounded-full bg-[#fe2c55] flex items-center justify-center shadow-lg cursor-pointer hover:bg-[#e62548] transition-colors">
        <user-outlined class="text-white text-xl" />
      </div>
    </div>

    <a-drawer
      placement="right"
      :closable="false"
      :visible="showFollowingDrawer"
      @close="showFollowingDrawer = false"
      width="320"
      :bodyStyle="{ padding: 0, backgroundColor: '#161823', color: 'white' }"
    >
      <div class="flex flex-col h-full">
        <div class="flex-shrink-0 px-4 py-4 border-b border-white/5">
          <h3 class="text-white font-bold text-lg">关注列表</h3>
          <p class="text-gray-500 text-xs mt-1">点击用户开始聊天</p>
        </div>
        
        <div class="flex-1 overflow-y-auto">
          <div v-if="loadingFollowing" class="flex justify-center py-10">
            <loading-outlined class="text-2xl text-gray-500 animate-spin" />
          </div>
          
          <div v-else-if="followingList.length === 0" class="flex flex-col items-center justify-center py-20 text-gray-500">
            <user-outlined class="text-4xl mb-3 opacity-50" />
            <p class="text-sm">暂无关注的用户</p>
          </div>
          
          <div v-else class="divide-y divide-white/5">
            <div 
              v-for="user in followingList" 
              :key="user.id" 
              class="flex items-center gap-3 px-4 py-3 cursor-pointer transition-colors hover:bg-white/5"
              :class="user.id === targetId ? 'bg-white/10' : ''"
              @click="selectUser(user); showFollowingDrawer = false"
            >
              <a-avatar :size="44" :src="resolveAvatarUrl(user.avatar) || DEFAULT_AVATAR" class="flex-shrink-0 border border-white/10" />
              <div class="flex-1 min-w-0">
                <div class="text-white text-sm font-medium truncate">{{ user.name || user.username }}</div>
                <div class="text-gray-500 text-xs truncate">{{ user.bio || '这个人很懒，什么都没写' }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </a-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { userApi, followApi, messageApi } from '../api'
import { useUserStore } from '../stores/user'
import { message } from 'ant-design-vue'
import { DEFAULT_AVATAR, resolveAvatarUrl } from '@/utils/constants'
import { 
  ArrowLeftOutlined, 
  LoadingOutlined, 
  MessageOutlined,
  SendOutlined,
  UserOutlined
} from '@ant-design/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const targetUser = ref(null)
const messages = ref([])
const content = ref('')
const loading = ref(false)
const loadingMore = ref(false)
const sending = ref(false)
const page = ref(1)
const hasMore = ref(true)
const isAllowed = ref(false)
const messageListRef = ref(null)

const followingList = ref([])
const loadingFollowing = ref(false)
const showFollowingDrawer = ref(false)

const targetId = computed(() => Number(route.params.id))

const fetchFollowing = async () => {
  loadingFollowing.value = true
  try {
    const res = await followApi.getFollowing(1, 100)
    if (res.code === 200) {
      followingList.value = res.data?.list || []
    }
  } catch (e) {
    console.error('获取关注列表失败:', e)
  } finally {
    loadingFollowing.value = false
  }
}

const selectUser = (user) => {
  if (user.id !== targetId.value) {
    router.push(`/messages/${user.id}`)
  }
}

const fetchTarget = async () => {
  try {
    const res = await userApi.getProfile(targetId.value)
    if (res.code === 200) targetUser.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const fetchFollowStatus = async () => {
  if (!targetId.value) return
  try {
    const res = await followApi.isFollowing(targetId.value)
    if (res.code === 200) isAllowed.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const fetchMessages = async (isLoadMore = false) => {
  if (!targetId.value) return
  if (isLoadMore) loadingMore.value = true
  else loading.value = true

  try {
    const res = await messageApi.getConversation(targetId.value, page.value, 20)
    if (res.code === 200) {
      const newMessages = [...res.data.list].reverse()
      
      if (isLoadMore) {
        const scrollContainer = messageListRef.value
        const scrollHeight = scrollContainer.scrollHeight
        const scrollTop = scrollContainer.scrollTop
        
        messages.value = [...newMessages, ...messages.value]
        
        nextTick(() => {
          const newScrollHeight = scrollContainer.scrollHeight
          scrollContainer.scrollTop = scrollTop + (newScrollHeight - scrollHeight)
        })
      } else {
        messages.value = newMessages
        scrollToBottom()
      }
      
      hasMore.value = page.value < res.data.pagination.totalPages
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

const loadMore = () => {
  if (hasMore.value && !loadingMore.value && !loading.value) {
    page.value++
    fetchMessages(true)
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
  })
}

const sendMessage = async () => {
  if (!content.value.trim() || sending.value) return
  sending.value = true
  try {
    const res = await messageApi.sendMessage({
      receiverId: targetId.value,
      content: content.value.trim()
    })
    if (res.code === 200) {
      messages.value.push(res.data)
      content.value = ''
      scrollToBottom()
    }
  } catch (e) {
    message.error('发送失败')
  } finally {
    sending.value = false
  }
}

const goToProfile = (id) => {
  const uid = id || targetId.value
  if (uid) router.push(`/user/${uid}`)
}

const formatMessageTime = (dateStr) => {
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (days === 0) {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  } else if (days === 1) {
    return `昨天 ${date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`
  } else if (days < 7) {
    const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
    return `${weekdays[date.getDay()]} ${date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`
  } else {
    return `${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`
  }
}

const shouldShowTime = (index) => {
  if (index === 0) return true
  const currentMsg = messages.value[index]
  const prevMsg = messages.value[index - 1]
  const diff = new Date(currentMsg.createdAt) - new Date(prevMsg.createdAt)
  return diff > 5 * 60 * 1000
}

watch(targetId, async () => {
  page.value = 1
  hasMore.value = true
  messages.value = []
  await fetchTarget()
  await fetchFollowStatus()
  await fetchMessages()
})

onMounted(async () => {
  await fetchFollowing()
  await fetchTarget()
  await fetchFollowStatus()
  await fetchMessages()
})
</script>

<style scoped>
.overflow-y-auto::-webkit-scrollbar {
  width: 4px;
}
.overflow-y-auto::-webkit-scrollbar-track {
  background: transparent;
}
.overflow-y-auto::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 2px;
}
.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.2);
}

.safe-area-bottom {
  padding-bottom: env(safe-area-inset-bottom, 20px);
}
</style>
