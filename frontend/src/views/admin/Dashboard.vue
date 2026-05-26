<template>
  <div class="min-h-screen bg-black text-white p-6 pt-[80px]">
    <div class="max-w-7xl mx-auto">
      <div class="flex items-center justify-between mb-8">
        <div>
          <h2 class="text-2xl font-bold text-white mb-1">管理后台</h2>
          <span class="text-gray-400 text-sm">Welcome back, {{ userStore.username }}</span>
        </div>
        <div class="flex gap-2">
           <button class="px-4 py-2 bg-[#161823] hover:bg-[#252836] text-white rounded border border-white/10 transition-colors" @click="fetchStats">
             <reload-outlined />
           </button>
        </div>
      </div>
      
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <div class="bg-[#161823] p-6 rounded-xl border border-white/5 relative overflow-hidden group">
          <div class="absolute top-0 right-0 w-24 h-24 bg-blue-500/10 rounded-bl-full -mr-4 -mt-4 transition-transform group-hover:scale-110"></div>
          <div class="relative z-10">
            <div class="w-12 h-12 rounded-lg bg-blue-500/20 flex items-center justify-center text-blue-400 text-2xl mb-4 group-hover:bg-blue-500 group-hover:text-white transition-colors">
              <user-outlined />
            </div>
            <span class="text-3xl font-bold text-white block mb-1">{{ stats.userCount || 0 }}</span>
            <span class="text-gray-500 text-sm">用户总数</span>
          </div>
        </div>

        <div class="bg-[#161823] p-6 rounded-xl border border-white/5 relative overflow-hidden group">
          <div class="absolute top-0 right-0 w-24 h-24 bg-[#fe2c55]/10 rounded-bl-full -mr-4 -mt-4 transition-transform group-hover:scale-110"></div>
          <div class="relative z-10">
            <div class="w-12 h-12 rounded-lg bg-[#fe2c55]/20 flex items-center justify-center text-[#fe2c55] text-2xl mb-4 group-hover:bg-[#fe2c55] group-hover:text-white transition-colors">
              <video-camera-outlined />
            </div>
            <span class="text-3xl font-bold text-white block mb-1">{{ stats.videoCount || 0 }}</span>
            <span class="text-gray-500 text-sm">视频总数</span>
          </div>
        </div>

        <div class="bg-[#161823] p-6 rounded-xl border border-white/5 relative overflow-hidden group">
          <div class="absolute top-0 right-0 w-24 h-24 bg-yellow-500/10 rounded-bl-full -mr-4 -mt-4 transition-transform group-hover:scale-110"></div>
          <div class="relative z-10">
            <div class="w-12 h-12 rounded-lg bg-yellow-500/20 flex items-center justify-center text-yellow-400 text-2xl mb-4 group-hover:bg-yellow-500 group-hover:text-white transition-colors">
              <message-outlined />
            </div>
            <span class="text-3xl font-bold text-white block mb-1">{{ stats.commentCount || 0 }}</span>
            <span class="text-gray-500 text-sm">评论总数</span>
          </div>
        </div>

        <div class="bg-[#161823] p-6 rounded-xl border border-white/5 relative overflow-hidden group">
          <div class="absolute top-0 right-0 w-24 h-24 bg-green-500/10 rounded-bl-full -mr-4 -mt-4 transition-transform group-hover:scale-110"></div>
          <div class="relative z-10">
            <div class="w-12 h-12 rounded-lg bg-green-500/20 flex items-center justify-center text-green-400 text-2xl mb-4 group-hover:bg-green-500 group-hover:text-white transition-colors">
              <rise-outlined />
            </div>
            <span class="text-3xl font-bold text-white block mb-1">{{ stats.todayNewUsers || 0 }}</span>
            <span class="text-gray-500 text-sm">今日新增用户</span>
          </div>
        </div>
      </div>
      
      <div class="bg-[#161823] rounded-xl border border-white/5 p-6">
        <div class="flex gap-4 mb-6 border-b border-white/10 pb-4">
          <button 
            @click="activeTab = 'users'"
            class="text-lg font-bold transition-colors relative bg-transparent border-none cursor-pointer"
            :class="activeTab === 'users' ? 'text-white' : 'text-gray-500 hover:text-white'"
          >
            用户管理
            <div v-if="activeTab === 'users'" class="absolute -bottom-[17px] left-0 w-full h-0.5 bg-[#fe2c55]"></div>
          </button>
          <button 
            @click="activeTab = 'videos'"
            class="text-lg font-bold transition-colors relative bg-transparent border-none cursor-pointer"
            :class="activeTab === 'videos' ? 'text-white' : 'text-gray-500 hover:text-white'"
          >
            视频审核
            <div v-if="activeTab === 'videos'" class="absolute -bottom-[17px] left-0 w-full h-0.5 bg-[#fe2c55]"></div>
          </button>
          <button 
            @click="activeTab = 'comments'"
            class="text-lg font-bold transition-colors relative bg-transparent border-none cursor-pointer"
            :class="activeTab === 'comments' ? 'text-white' : 'text-gray-500 hover:text-white'"
          >
            评论管理
            <div v-if="activeTab === 'comments'" class="absolute -bottom-[17px] left-0 w-full h-0.5 bg-[#fe2c55]"></div>
          </button>
        </div>

        <div v-if="activeTab === 'users'">
          <a-table 
            :dataSource="users" 
            :columns="columns" 
            :loading="loading" 
            rowKey="id" 
            :pagination="{ pageSize: 10, theme: 'dark' }" 
            class="dark-table"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'role'">
                <span 
                  class="px-2 py-0.5 rounded text-xs font-medium border"
                  :class="record.role === 'ADMIN' ? 'bg-red-500/10 text-red-400 border-red-500/20' : 'bg-blue-500/10 text-blue-400 border-blue-500/20'"
                >
                  {{ record.role }}
                </span>
              </template>
              <template v-if="column.key === 'status'">
                 <span 
                  class="px-2 py-0.5 rounded text-xs font-medium border"
                  :class="record.status === 'ACTIVE' ? 'bg-green-500/10 text-green-400 border-green-500/20' : 'bg-yellow-500/10 text-yellow-400 border-yellow-500/20'"
                >
                  {{ record.status }}
                </span>
              </template>
              <template v-if="column.key === 'action'">
                <div class="flex gap-2">
                  <button 
                    class="text-xs px-2 py-1 rounded border transition-colors bg-transparent cursor-pointer"
                    :class="record.status === 'ACTIVE' ? 'border-red-500/30 text-red-400 hover:bg-red-500/10' : 'border-green-500/30 text-green-400 hover:bg-green-500/10'"
                    @click="toggleStatus(record)"
                  >
                    {{ record.status === 'ACTIVE' ? '禁用' : '启用' }}
                  </button>
                  <button 
                    class="text-xs px-2 py-1 rounded border border-blue-500/30 text-blue-400 hover:bg-blue-500/10 transition-colors bg-transparent cursor-pointer"
                    @click="toggleRole(record)"
                  >
                    {{ record.role === 'ADMIN' ? '降为用户' : '升为管理员' }}
                  </button>
                </div>
              </template>
            </template>
          </a-table>
        </div>

        <div v-if="activeTab === 'videos'">
          <div class="flex gap-2 mb-4">
             <button 
               v-for="status in ['ALL', 'PENDING', 'PUBLISHED', 'REJECTED', 'BANNED']" 
               :key="status"
               @click="videoFilter = status; fetchVideos()"
               class="px-3 py-1 rounded text-sm border transition-colors"
               :class="videoFilter === status ? 'bg-[#fe2c55] text-white border-[#fe2c55]' : 'bg-transparent text-gray-400 border-white/10 hover:border-white/30'"
             >
               {{ status === 'ALL' ? '全部' : status }}
             </button>
          </div>
          <a-table 
            :dataSource="videos" 
            :columns="videoColumns" 
            :loading="videoLoading" 
            rowKey="id" 
            :pagination="{ 
              total: videoTotal,
              current: videoPage,
              pageSize: videoSize,
              onChange: (page) => { videoPage = page; fetchVideos() },
              theme: 'dark' 
            }" 
            class="dark-table"
          >
            <template #bodyCell="{ column, record }">
               <template v-if="column.key === 'coverUrl'">
                 <img :src="record.coverUrl" class="w-16 h-24 object-cover rounded bg-gray-800" />
               </template>
               <template v-if="column.key === 'status'">
                 <span 
                  class="px-2 py-0.5 rounded text-xs font-medium border"
                  :class="{
                    'bg-green-500/10 text-green-400 border-green-500/20': record.status === 'PUBLISHED',
                    'bg-yellow-500/10 text-yellow-400 border-yellow-500/20': record.status === 'PENDING',
                    'bg-red-500/10 text-red-400 border-red-500/20': ['REJECTED', 'BANNED'].includes(record.status)
                  }"
                >
                  {{ record.status }}
                </span>
               </template>
               <template v-if="column.key === 'action'">
                 <div class="flex flex-col gap-1">
                   <div v-if="record.status === 'PENDING'" class="flex gap-1">
                     <button @click="auditVideo(record, 'PUBLISHED')" class="text-xs px-2 py-1 rounded border border-green-500/30 text-green-400 hover:bg-green-500/10 bg-transparent cursor-pointer">通过</button>
                     <button @click="auditVideo(record, 'REJECTED')" class="text-xs px-2 py-1 rounded border border-red-500/30 text-red-400 hover:bg-red-500/10 bg-transparent cursor-pointer">拒绝</button>
                   </div>
                   <button 
                     v-if="record.status === 'PUBLISHED'" 
                     @click="auditVideo(record, 'BANNED')" 
                     class="text-xs px-2 py-1 rounded border border-red-500/30 text-red-400 hover:bg-red-500/10 bg-transparent cursor-pointer"
                   >
                     下架/封禁
                   </button>
                    <button 
                     v-if="['REJECTED', 'BANNED'].includes(record.status)" 
                     @click="auditVideo(record, 'PUBLISHED')" 
                     class="text-xs px-2 py-1 rounded border border-blue-500/30 text-blue-400 hover:bg-blue-500/10 bg-transparent cursor-pointer"
                   >
                     重新上架
                   </button>
                 </div>
               </template>
            </template>
          </a-table>
        </div>

        <div v-if="activeTab === 'comments'">
          <a-table 
            :dataSource="comments" 
            :columns="commentColumns" 
            :loading="commentLoading" 
            rowKey="id" 
            :pagination="{ 
              total: commentTotal,
              current: commentPage,
              pageSize: commentSize,
              onChange: (page) => { commentPage = page; fetchComments() },
              theme: 'dark' 
            }" 
            class="dark-table"
          >
            <template #bodyCell="{ column, record }">
               <template v-if="column.key === 'content'">
                 <div class="max-w-[300px] truncate" :title="record.content">
                   {{ record.content }}
                 </div>
               </template>
               <template v-if="column.key === 'author'">
                 <div class="flex items-center gap-2">
                   <a-avatar :size="24" :src="record.author?.avatar" />
                   <span>{{ record.author?.name || '未知用户' }}</span>
                 </div>
               </template>
               <template v-if="column.key === 'status'">
                 <span 
                  class="px-2 py-0.5 rounded text-xs font-medium border"
                  :class="{
                    'bg-green-500/10 text-green-400 border-green-500/20': record.status === 'NORMAL',
                    'bg-red-500/10 text-red-400 border-red-500/20': record.status === 'DELETED'
                  }"
                >
                  {{ record.status === 'NORMAL' ? '正常' : '已删除' }}
                </span>
               </template>
               <template v-if="column.key === 'action'">
                 <button 
                   v-if="record.status === 'NORMAL'"
                   @click="handleDeleteComment(record)" 
                   class="text-xs px-2 py-1 rounded border border-red-500/30 text-red-400 hover:bg-red-500/10 bg-transparent cursor-pointer"
                 >
                   删除
                 </button>
                 <span v-else class="text-gray-500 text-xs">已删除</span>
               </template>
            </template>
          </a-table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { adminApi } from '../../api'
import { useUserStore } from '../../stores/user'
import { message } from 'ant-design-vue'
import { 
  UserOutlined, 
  VideoCameraOutlined, 
  MessageOutlined, 
  RiseOutlined,
  ReloadOutlined
} from '@ant-design/icons-vue'

const userStore = useUserStore()
const activeTab = ref('users')
const loading = ref(false)
const stats = ref({})
const users = ref([])

// Video state
const videos = ref([])
const videoLoading = ref(false)
const videoPage = ref(1)
const videoSize = ref(10)
const videoTotal = ref(0)
const videoFilter = ref('ALL')

// Comment state
const comments = ref([])
const commentLoading = ref(false)
const commentPage = ref(1)
const commentSize = ref(10)
const commentTotal = ref(0)

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id' },
  { title: '用户名', dataIndex: 'username', key: 'username' },
  { title: '姓名', dataIndex: 'name', key: 'name' },
  { title: '角色', dataIndex: 'role', key: 'role' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '操作', key: 'action' }
]

const videoColumns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
  { title: '封面', key: 'coverUrl', width: 80 },
  { title: '标题', dataIndex: 'title', key: 'title' },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt' },
  { title: '操作', key: 'action', width: 150 }
]

const commentColumns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
  { title: '内容', dataIndex: 'content', key: 'content' },
  { title: '作者', key: 'author', width: 150 },
  { title: '视频ID', dataIndex: 'videoId', key: 'videoId', width: 80 },
  { title: '点赞', dataIndex: 'likeCount', key: 'likeCount', width: 60 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 80 },
  { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 150 },
  { title: '操作', key: 'action', width: 80 }
]

const fetchStats = async () => {
  try {
    const res = await adminApi.getDashboardStats()
    if (res.code === 200) stats.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await adminApi.getAllUsers()
    if (res.code === 200) users.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const fetchVideos = async () => {
  videoLoading.value = true
  try {
    const params = {
      page: videoPage.value,
      size: videoSize.value,
      status: videoFilter.value === 'ALL' ? undefined : videoFilter.value
    }
    const res = await adminApi.getVideos(params)
    if (res.code === 200) {
      videos.value = Array.isArray(res.data?.list) ? res.data.list : []
      videoTotal.value = res.data?.pagination?.total || 0
    }
  } catch (e) {
    console.error(e)
  } finally {
    videoLoading.value = false
  }
}

const auditVideo = async (video, status) => {
  try {
    const res = await adminApi.auditVideo(video.id, status)
    if (res.code === 200) {
      message.success('操作成功')
      fetchVideos()
    }
  } catch (e) {
    message.error('操作失败')
  }
}

const fetchComments = async () => {
  commentLoading.value = true
  try {
    const res = await adminApi.getComments({
      page: commentPage.value,
      size: commentSize.value
    })
    if (res.code === 200) {
      comments.value = Array.isArray(res.data?.list) ? res.data.list : []
      commentTotal.value = res.data?.pagination?.total || 0
    }
  } catch (e) {
    console.error(e)
  } finally {
    commentLoading.value = false
  }
}

const handleDeleteComment = async (comment) => {
  try {
    const res = await adminApi.deleteComment(comment.id)
    if (res.code === 200) {
      message.success('评论删除成功')
      fetchComments()
    }
  } catch (e) {
    message.error('删除失败')
  }
}

const toggleStatus = async (user) => {
  try {
    const newStatus = user.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE'
    await adminApi.updateUserStatus(user.id, newStatus)
    user.status = newStatus
    message.success('状态更新成功')
  } catch (e) {
    message.error('操作失败')
  }
}

const toggleRole = async (user) => {
  try {
    const newRole = user.role === 'ADMIN' ? 'USER' : 'ADMIN'
    await adminApi.updateUserRole(user.id, newRole)
    user.role = newRole
    message.success('角色更新成功')
  } catch (e) {
    message.error('操作失败')
  }
}

watch(activeTab, (val) => {
  if (val === 'videos' && (Array.isArray(videos.value) ? videos.value.length : 0) === 0) {
    fetchVideos()
  }
  if (val === 'comments' && (Array.isArray(comments.value) ? comments.value.length : 0) === 0) {
    fetchComments()
  }
})

onMounted(() => {
  fetchStats()
  fetchUsers()
})
</script>

<style>
/* Reusing the dark table styles defined in global scope or Users.vue */
.dark-table .ant-table {
  background: transparent !important;
  color: #fff !important;
}
.dark-table .ant-table-thead > tr > th {
  background: #1f212e !important;
  color: #888 !important;
  border-bottom: 1px solid #2a2c35 !important;
}
.dark-table .ant-table-tbody > tr > td {
  background: transparent !important;
  color: #fff !important;
  border-bottom: 1px solid #222 !important;
}
.dark-table .ant-table-tbody > tr:hover > td {
  background: #2a2c35 !important;
}
.dark-table .ant-pagination-item-active {
  background: #fe2c55 !important;
  border-color: #fe2c55 !important;
}
.dark-table .ant-pagination-item a {
  color: #fff !important;
}
</style>
