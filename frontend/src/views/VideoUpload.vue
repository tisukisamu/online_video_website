<template>
  <div class="min-h-screen bg-black pb-24">
    <!-- Header -->
    <div class="sticky top-0 z-50 flex justify-between items-center px-4 h-[60px] bg-black/90 backdrop-blur-md border-b border-white/5">
      <div class="w-8 h-8 flex items-center justify-center rounded-full active:bg-white/10 transition-colors" @click="$router.back()">
        <arrow-left-outlined class="text-white text-lg" />
      </div>
      <span class="text-white text-base font-bold">发布视频</span>
      <div class="w-8"></div>
    </div>
    
    <div class="p-4 max-w-5xl mx-auto">
      <div class="md:flex md:gap-8">
        <div class="md:w-[420px] md:shrink-0">
          <div 
            class="w-full aspect-[9/16] max-w-[420px] mx-auto md:mx-0 max-h-[60vh] md:max-h-[520px] min-h-[280px] bg-[#161823] rounded-xl flex flex-col items-center justify-center cursor-pointer border-2 border-dashed border-white/10 hover:border-white/30 transition-colors relative overflow-hidden mb-6 md:mb-0 group"
            @click="triggerUpload"
          >
            <input ref="videoInput" type="file" accept="video/*" hidden @change="handleVideoChange" />
            
            <template v-if="!formData.videoUrl">
              <div class="w-16 h-16 bg-[#252525] rounded-full flex items-center justify-center mb-4 group-hover:scale-110 transition-transform">
                <plus-outlined class="text-[#fe2c55] text-2xl" />
              </div>
              <p class="text-white text-base font-medium mb-1">点击上传视频</p>
              <p class="text-gray-500 text-xs px-6 text-center">支持 MP4、MOV 格式，时长不超过 15 分钟</p>
            </template>
            
            <template v-else>
              <video :src="formData.videoUrl" class="w-full h-full object-contain bg-black" controls />
              <div class="absolute top-2 right-2 bg-black/60 px-3 py-1 rounded-full text-white text-xs" @click.stop="triggerUpload">
                更换视频
              </div>
            </template>
          </div>
        </div>

        <div class="flex-1">
          <div class="space-y-6">
            <div class="space-y-4">
              <div>
                <label class="block text-gray-400 text-xs mb-2 pl-1">标题</label>
                <input 
                  v-model="formData.title" 
                  type="text"
                  placeholder="填写标题，为作品获得更多曝光" 
                  class="w-full bg-[#161823] text-white h-12 px-4 rounded-lg border-none outline-none focus:ring-1 focus:ring-white/20 transition-all placeholder-gray-600"
                  maxlength="50"
                />
              </div>
              
              <div>
                <label class="block text-gray-400 text-xs mb-2 pl-1">描述</label>
                <textarea 
                  v-model="formData.description" 
                  placeholder="添加作品描述，让更多人看到..." 
                  rows="4" 
                  class="w-full bg-[#161823] text-white p-4 rounded-lg border-none outline-none focus:ring-1 focus:ring-white/20 transition-all placeholder-gray-600 resize-none"
                  maxlength="200"
                ></textarea>
              </div>
            </div>
            
            <div>
              <label class="block text-gray-400 text-xs mb-2 pl-1">封面</label>
              <div class="flex gap-4 overflow-x-auto pb-2">
                <div 
                  class="shrink-0 w-24 h-32 bg-[#161823] rounded-lg border border-white/10 flex flex-col items-center justify-center cursor-pointer hover:bg-[#1f212e] transition-colors relative overflow-hidden"
                  @click="triggerCoverUpload"
                >
                  <input ref="coverInput" type="file" accept="image/*" hidden @change="handleCoverChange" />
                  <template v-if="formData.coverUrl">
                    <img :src="formData.coverUrl" class="w-full h-full object-cover" />
                    <div class="absolute inset-0 bg-black/40 flex items-center justify-center opacity-0 hover:opacity-100 transition-opacity">
                      <span class="text-white text-xs">更换</span>
                    </div>
                  </template>
                  <template v-else>
                    <plus-outlined class="text-gray-400 text-xl mb-2" />
                    <span class="text-gray-500 text-xs">上传封面</span>
                  </template>
                </div>
                
                <div v-if="formData.videoUrl" class="flex gap-2">
                  <div v-for="i in 3" :key="i" class="shrink-0 w-24 h-32 bg-gray-800 rounded-lg animate-pulse"></div>
                </div>
              </div>
            </div>
            
            <div>
              <label class="block text-gray-400 text-xs mb-2 pl-1">谁可以看</label>
              <div class="flex gap-2">
                 <button 
                   v-for="type in visibilityOptions" 
                   :key="type.value"
                   class="flex-1 h-10 rounded-lg text-sm font-medium transition-colors border cursor-pointer"
                   :class="formData.visibility === type.value ? 'bg-[#fe2c55] border-[#fe2c55] text-white' : 'bg-[#161823] border-transparent text-gray-400 hover:bg-[#1f212e]'"
                   @click="formData.visibility = type.value"
                 >
                   {{ type.label }}
                 </button>
              </div>
            </div>
          </div>
          
          <div class="mt-10 pb-10 md:pb-0">
            <button 
              class="w-full h-12 bg-[#fe2c55] text-white rounded-full font-bold text-lg hover:brightness-110 active:scale-[0.98] transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
              :disabled="loading || !formData.videoUrl"
              @click="handleSubmit"
            >
              <loading-outlined v-if="loading" class="animate-spin" />
              <span>{{ loading ? '发布中...' : '发布作品' }}</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { uploadApi, videoApi } from '../api'
import { message } from 'ant-design-vue'
import { ArrowLeftOutlined, PlusOutlined, LoadingOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const loading = ref(false)
const videoInput = ref(null)
const coverInput = ref(null)

const formData = reactive({
  title: '',
  description: '',
  videoUrl: '',
  coverUrl: '',
  visibility: 'PUBLIC'
})

const visibilityOptions = [
  { label: '公开', value: 'PUBLIC' },
  { label: '粉丝可见', value: 'FOLLOWERS' },
  { label: '私密', value: 'PRIVATE' }
]

const triggerUpload = () => videoInput.value?.click()
const triggerCoverUpload = () => coverInput.value?.click()

const handleVideoChange = async (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  if (file.size > 500 * 1024 * 1024) {
    message.error('视频不能超过500MB')
    return
  }
  message.loading({ content: '上传中...', key: 'upload' })
  try {
    const res = await uploadApi.uploadVideo(file)
    if (res.code === 200) {
      formData.videoUrl = res.data.url
      message.success({ content: '上传成功', key: 'upload' })
    }
  } catch {
    message.error({ content: '上传失败', key: 'upload' })
  }
}

const handleCoverChange = async (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  try {
    const res = await uploadApi.uploadImage(file)
    if (res.code === 200) formData.coverUrl = res.data.url
  } catch {
    message.error('封面上传失败')
  }
}

const handleSubmit = async () => {
  if (!formData.videoUrl) return message.error('请上传视频')
  if (!formData.title) return message.error('请填写标题')
  
  loading.value = true
  try {
    const res = await videoApi.create(formData)
    if (res.code === 200) {
      message.success('发布成功')
      router.push(`/video/${res.data.id}`)
    }
  } finally {
    loading.value = false
  }
}
</script>
