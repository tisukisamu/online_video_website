<template>
  <div class="min-h-screen bg-black text-white pb-24">
    <div class="fixed top-0 left-0 right-0 z-50 flex items-center justify-between px-4 h-[60px] bg-black/90 backdrop-blur-md border-b border-white/5">
      <div class="w-8 h-8 flex items-center justify-center rounded-full active:bg-white/10 transition-colors cursor-pointer" @click="$router.back()">
        <arrow-left-outlined class="text-white text-lg" />
      </div>
      <span class="text-white text-base font-bold">我的作品</span>
      <div class="w-8"></div>
    </div>
    
    <div class="pt-[76px] px-4 max-w-6xl mx-auto">
      <div class="flex items-center justify-between gap-3 mb-4">
        <div class="flex items-center gap-2">
          <span class="text-gray-400 text-xs">状态</span>
          <a-select
            v-model:value="statusFilter"
            size="small"
            class="w-[140px]"
            :options="statusOptions"
            :dropdownMatchSelectWidth="false"
          />
        </div>
        <button class="px-4 h-9 bg-[#fe2c55] text-white rounded-lg text-sm font-medium hover:bg-[#e62548] transition-colors border-none cursor-pointer" @click="goUpload">
          发布视频
        </button>
      </div>
      
      <div class="md:hidden space-y-3">
        <div
          v-for="video in filteredVideos"
          :key="video.id"
          class="flex gap-3 bg-[#161823] rounded-xl border border-white/5 p-3"
        >
          <div class="w-[96px] h-[128px] bg-[#222] rounded-lg overflow-hidden shrink-0 cursor-pointer" @click="goDetail(video.id)">
            <img :src="video.coverUrl" class="w-full h-full object-cover" />
          </div>
          <div class="flex-1 min-w-0">
            <div class="flex items-start justify-between gap-2">
              <div class="min-w-0">
                <div class="text-white text-sm font-medium line-clamp-2">{{ video.title }}</div>
                <div class="text-gray-500 text-xs mt-1 line-clamp-1">{{ video.description || '暂无描述' }}</div>
              </div>
              <span class="shrink-0 text-[10px] px-2 py-0.5 rounded border" :class="statusClass(video.status)">
                {{ statusText(video.status) }}
              </span>
            </div>
            <div class="flex items-center justify-between mt-3">
              <span class="text-gray-500 text-[11px]">{{ visibilityText(video.visibility) }}</span>
              <div class="flex items-center gap-2">
                <button class="text-xs text-gray-300 hover:text-white transition-colors bg-transparent border-none cursor-pointer" @click="openEdit(video)">
                  编辑
                </button>
                <button
                  class="text-xs transition-colors bg-transparent border-none cursor-pointer"
                  :class="canToggleVisibility(video) ? 'text-[#face15] hover:brightness-110' : 'text-gray-600 cursor-not-allowed'"
                  :disabled="!canToggleVisibility(video)"
                  @click="toggleVisibility(video)"
                >
                  {{ toggleVisibilityText(video) }}
                </button>
                <a-popconfirm title="确定要删除该视频吗？" ok-text="删除" cancel-text="取消" @confirm="removeVideo(video)">
                  <button class="text-xs text-red-400 hover:text-red-300 transition-colors bg-transparent border-none cursor-pointer">
                    删除
                  </button>
                </a-popconfirm>
              </div>
            </div>
          </div>
        </div>
        
        <div v-if="loading" class="flex justify-center py-6">
          <a-spin />
        </div>
        <div v-if="!loading && filteredVideos.length === 0" class="flex flex-col items-center justify-center py-16 text-gray-500">
          <video-camera-outlined class="text-4xl mb-3 opacity-60" />
          <div class="text-sm">暂无作品</div>
        </div>
      </div>
      
      <div class="hidden md:block">
        <a-table
          :dataSource="filteredVideos"
          :columns="columns"
          :loading="loading"
          rowKey="id"
          :pagination="false"
          class="dark-table"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'coverUrl'">
              <div class="w-16 h-24 rounded bg-[#222] overflow-hidden cursor-pointer" @click="goDetail(record.id)">
                <img :src="record.coverUrl" class="w-full h-full object-cover" />
              </div>
            </template>
            <template v-else-if="column.key === 'title'">
              <div class="min-w-0">
                <div class="text-white text-sm font-medium line-clamp-1">{{ record.title }}</div>
                <div class="text-gray-500 text-xs line-clamp-1 mt-1">{{ record.description || '暂无描述' }}</div>
              </div>
            </template>
            <template v-else-if="column.key === 'status'">
              <span class="text-xs px-2 py-0.5 rounded border" :class="statusClass(record.status)">
                {{ statusText(record.status) }}
              </span>
            </template>
            <template v-else-if="column.key === 'visibility'">
              <span class="text-xs text-gray-400">{{ visibilityText(record.visibility) }}</span>
            </template>
            <template v-else-if="column.key === 'createdAt'">
              <span class="text-xs text-gray-500">{{ formatDate(record.createdAt) }}</span>
            </template>
            <template v-else-if="column.key === 'action'">
              <div class="flex items-center gap-3">
                <button class="text-xs text-gray-300 hover:text-white transition-colors bg-transparent border-none cursor-pointer" @click="goDetail(record.id)">
                  查看
                </button>
                <button class="text-xs text-gray-300 hover:text-white transition-colors bg-transparent border-none cursor-pointer" @click="openEdit(record)">
                  编辑
                </button>
                <button
                  class="text-xs transition-colors bg-transparent border-none cursor-pointer"
                  :class="canToggleVisibility(record) ? 'text-[#face15] hover:brightness-110' : 'text-gray-600 cursor-not-allowed'"
                  :disabled="!canToggleVisibility(record)"
                  @click="toggleVisibility(record)"
                >
                  {{ toggleVisibilityText(record) }}
                </button>
                <a-popconfirm title="确定要删除该视频吗？" ok-text="删除" cancel-text="取消" @confirm="removeVideo(record)">
                  <button class="text-xs text-red-400 hover:text-red-300 transition-colors bg-transparent border-none cursor-pointer">
                    删除
                  </button>
                </a-popconfirm>
              </div>
            </template>
          </template>
        </a-table>

        <div v-if="loading" class="flex justify-center py-8">
          <a-spin />
        </div>
        <div v-if="!loading && filteredVideos.length === 0" class="flex flex-col items-center justify-center py-20 text-gray-500">
          <video-camera-outlined class="text-5xl mb-4 opacity-60" />
          <div class="text-sm">暂无作品</div>
        </div>
      </div>
      
      <div ref="loadMoreRef" class="h-1"></div>
      <div v-if="!loading && hasMore && videos.length > 0" class="text-center py-4 text-gray-600 text-xs">继续下拉加载更多</div>
    </div>

    <a-modal v-model:open="editOpen" title="编辑作品" :confirm-loading="saving" @ok="saveEdit" wrap-class-name="dark-modal">
      <div class="space-y-4 mt-2">
        <div>
          <div class="text-gray-400 text-xs mb-2">标题</div>
          <a-input v-model:value="editForm.title" placeholder="请输入标题" />
        </div>
        <div>
          <div class="text-gray-400 text-xs mb-2">描述</div>
          <a-textarea v-model:value="editForm.description" :rows="4" placeholder="请输入描述" />
        </div>
        <div>
          <div class="text-gray-400 text-xs mb-2">封面</div>
          <div class="flex items-center gap-3">
            <div class="w-16 h-24 rounded bg-[#222] overflow-hidden">
              <img v-if="editForm.coverUrl" :src="editForm.coverUrl" class="w-full h-full object-cover" />
            </div>
            <div class="flex flex-col gap-2">
              <input ref="coverInput" type="file" accept="image/*" hidden @change="handleCoverChange" />
              <button class="px-3 h-9 rounded bg-[#252525] text-white text-sm hover:bg-[#333] transition-colors border-none cursor-pointer" @click="triggerCoverUpload">
                上传封面
              </button>
              <div v-if="uploadingCover" class="text-gray-500 text-xs">上传中...</div>
            </div>
          </div>
        </div>
        <div>
          <div class="text-gray-400 text-xs mb-2">可见范围</div>
          <a-select v-model:value="editForm.visibility" :options="visibilityOptions" />
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { userApi, videoApi, uploadApi } from '../api'
import { ArrowLeftOutlined, VideoCameraOutlined } from '@ant-design/icons-vue'

const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const uploadingCover = ref(false)

const page = ref(1)
const size = ref(20)
const totalPages = ref(1)
const hasMore = computed(() => page.value < totalPages.value)

const videos = ref([])
const statusFilter = ref('ALL')
const statusOptions = [
  { label: '全部', value: 'ALL' },
  { label: '已上架', value: 'PUBLISHED' },
  { label: '审核中', value: 'PENDING' },
  { label: '未通过', value: 'REJECTED' },
  { label: '已封禁', value: 'BANNED' },
  { label: '已删除', value: 'DELETED' }
]

const filteredVideos = computed(() => {
  if (statusFilter.value === 'ALL') return videos.value
  return videos.value.filter(v => v.status === statusFilter.value)
})

const columns = [
  { title: '封面', key: 'coverUrl', width: 90 },
  { title: '标题', dataIndex: 'title', key: 'title' },
  { title: '状态', dataIndex: 'status', key: 'status', width: 120 },
  { title: '可见范围', dataIndex: 'visibility', key: 'visibility', width: 120 },
  { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 160 },
  { title: '操作', key: 'action', width: 240 }
]

const fetchVideos = async ({ targetPage = 1, append = false } = {}) => {
  if (loading.value) return
  loading.value = true
  try {
    const res = await userApi.getMyVideos(targetPage, size.value)
    if (res.code === 200) {
      const list = Array.isArray(res.data?.list) ? res.data.list : []
      videos.value = append ? [...videos.value, ...list] : list
      totalPages.value = res.data?.pagination?.totalPages || 1
      page.value = targetPage
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const goDetail = (id) => router.push(`/video/${id}`)
const goUpload = () => router.push('/upload')

const canToggleVisibility = (video) => {
  return video?.status === 'PUBLISHED'
}

const toggleVisibilityText = (video) => {
  if (!canToggleVisibility(video)) return '上/下架'
  return video.visibility === 'PRIVATE' ? '上架' : '下架'
}

const toggleVisibility = async (video) => {
  if (!canToggleVisibility(video)) return
  const visibility = video.visibility === 'PRIVATE' ? 'PUBLIC' : 'PRIVATE'
  try {
    const res = await videoApi.update(video.id, { visibility })
    if (res.code === 200) {
      video.visibility = visibility
      message.success('操作成功')
    }
  } catch (e) {
    message.error('操作失败')
  }
}

const removeVideo = async (video) => {
  try {
    const res = await videoApi.delete(video.id)
    if (res.code === 200) {
      videos.value = videos.value.filter(v => v.id !== video.id)
      message.success('删除成功')
    }
  } catch (e) {
    message.error('删除失败')
  }
}

const editOpen = ref(false)
const editingVideo = ref(null)
const coverInput = ref(null)
const editForm = reactive({
  title: '',
  description: '',
  coverUrl: '',
  visibility: 'PUBLIC'
})

const visibilityOptions = [
  { label: '公开', value: 'PUBLIC' },
  { label: '粉丝可见', value: 'FOLLOWERS' },
  { label: '私密', value: 'PRIVATE' }
]

const openEdit = (video) => {
  editingVideo.value = video
  editForm.title = video.title || ''
  editForm.description = video.description || ''
  editForm.coverUrl = video.coverUrl || ''
  editForm.visibility = video.visibility || 'PUBLIC'
  editOpen.value = true
}

const triggerCoverUpload = () => coverInput.value?.click()

const handleCoverChange = async (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  if (file.size > 5 * 1024 * 1024) {
    message.error('图片大小不能超过5MB')
    e.target.value = ''
    return
  }
  uploadingCover.value = true
  try {
    const res = await uploadApi.uploadImage(file)
    if (res.code === 200) {
      editForm.coverUrl = res.data.url
      message.success('封面上传成功')
    } else {
      message.error(res.message || '上传失败')
    }
  } catch (err) {
    message.error('上传失败')
  } finally {
    uploadingCover.value = false
    e.target.value = ''
  }
}

const saveEdit = async () => {
  if (!editingVideo.value) return
  if (!editForm.title?.trim()) {
    message.error('标题不能为空')
    return
  }
  saving.value = true
  try {
    const payload = {
      title: editForm.title.trim(),
      description: (editForm.description || '').trim(),
      coverUrl: editForm.coverUrl || null,
      visibility: editForm.visibility
    }
    const res = await videoApi.update(editingVideo.value.id, payload)
    if (res.code === 200) {
      Object.assign(editingVideo.value, res.data)
      message.success('保存成功')
      editOpen.value = false
    }
  } catch (e) {
    message.error('保存失败')
  } finally {
    saving.value = false
  }
}

const statusText = (status) => {
  if (status === 'PUBLISHED') return '已上架'
  if (status === 'PENDING') return '审核中'
  if (status === 'REJECTED') return '未通过'
  if (status === 'BANNED') return '已封禁'
  if (status === 'DELETED') return '已删除'
  return status || '-'
}

const statusClass = (status) => {
  if (status === 'PUBLISHED') return 'bg-green-500/10 text-green-400 border-green-500/20'
  if (status === 'PENDING') return 'bg-yellow-500/10 text-yellow-400 border-yellow-500/20'
  if (status === 'REJECTED') return 'bg-red-500/10 text-red-400 border-red-500/20'
  if (status === 'BANNED') return 'bg-red-500/10 text-red-400 border-red-500/20'
  if (status === 'DELETED') return 'bg-gray-500/10 text-gray-400 border-gray-500/20'
  return 'bg-white/5 text-gray-400 border-white/10'
}

const visibilityText = (visibility) => {
  if (visibility === 'PUBLIC') return '公开'
  if (visibility === 'FOLLOWERS') return '粉丝可见'
  if (visibility === 'PRIVATE') return '私密'
  return visibility || '-'
}

const formatDate = (d) => {
  if (!d) return '-'
  const date = new Date(d)
  if (Number.isNaN(date.getTime())) return d
  return date.toLocaleString()
}

const loadMoreRef = ref(null)
let loadMoreObserver = null

const setupLoadMoreObserver = () => {
  if (typeof window === 'undefined') return
  if (!loadMoreRef.value) return
  if (loadMoreObserver) loadMoreObserver.disconnect()

  loadMoreObserver = new IntersectionObserver(
    (entries) => {
      const entry = entries[0]
      if (!entry?.isIntersecting) return
      if (!hasMore.value) return
      fetchVideos({ targetPage: page.value + 1, append: true })
    },
    { root: null, rootMargin: '200px 0px', threshold: 0 }
  )

  loadMoreObserver.observe(loadMoreRef.value)
}

onMounted(async () => {
  await fetchVideos({ targetPage: 1, append: false })
  await nextTick()
  setupLoadMoreObserver()
})

onUnmounted(() => {
  if (loadMoreObserver) loadMoreObserver.disconnect()
})
</script>

<style>
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

.dark-modal .ant-modal-content {
  background-color: #1f212e;
  color: white;
}
.dark-modal .ant-modal-header {
  background-color: #1f212e;
  border-bottom: 1px solid #333;
}
.dark-modal .ant-modal-title {
  color: white;
}
.dark-modal .ant-modal-close {
  color: white;
}
.dark-modal .ant-form-item-label > label {
  color: #aaa;
}
.dark-modal .ant-input, .dark-modal .ant-input-number, .dark-modal .ant-select-selector, .dark-modal .ant-input-affix-wrapper, .dark-modal .ant-input-password, .dark-modal textarea {
  background-color: #161823 !important;
  color: white !important;
  border: none !important;
}
</style>
