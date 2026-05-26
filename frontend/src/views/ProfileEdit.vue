<template>
  <div class="min-h-screen bg-black text-white pb-20">
    <div class="fixed top-0 left-0 right-0 z-50 flex items-center justify-between px-4 h-[60px] bg-black border-b border-white/10">
      <div class="w-8 h-8 flex items-center justify-center rounded-full active:bg-white/10 transition-colors cursor-pointer" @click="$router.back()">
        <arrow-left-outlined class="text-white text-lg" />
      </div>
      <span class="text-white text-base font-medium">编辑资料</span>
      <button 
        class="text-[#fe2c55] font-medium text-sm disabled:opacity-50 disabled:cursor-not-allowed transition-opacity bg-transparent border-none cursor-pointer"
        :disabled="saving"
        @click="saveProfile"
      >
        保存
      </button>
    </div>

    <div class="pt-[80px] px-6 max-w-lg mx-auto">
      <div class="flex flex-col items-center mb-8">
        <div class="relative group cursor-pointer" @click="triggerAvatarUpload">
          <a-avatar :size="100" :src="resolveAvatarUrl(form.avatar) || DEFAULT_AVATAR" class="border-2 border-white/20" />
          <div class="absolute inset-0 rounded-full bg-black/50 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
            <camera-outlined class="text-white text-2xl" />
          </div>
          <div v-if="uploadingAvatar" class="absolute inset-0 rounded-full bg-black/70 flex items-center justify-center">
            <loading-outlined class="text-white text-2xl animate-spin" />
          </div>
        </div>
        <p class="text-gray-500 text-xs mt-3 cursor-pointer hover:text-white transition-colors" @click="triggerAvatarUpload">点击更换头像</p>
        <input 
          ref="avatarInput" 
          type="file" 
          accept="image/*" 
          class="hidden" 
          @change="handleAvatarChange"
        />
      </div>

      <div class="space-y-6">
        <div>
          <label class="text-gray-500 text-xs block mb-2 pl-1">昵称</label>
          <a-input 
            v-model:value="form.name" 
            placeholder="请输入昵称"
            :maxLength="20"
            class="!bg-[#161823] !border-none !rounded-lg !text-white !h-12 !pl-4 placeholder:!text-white/30 hover:!bg-[#1f212e] focus:!bg-[#1f212e] transition-colors"
          >
            <template #prefix>
              <user-outlined class="text-gray-500 mr-2" />
            </template>
          </a-input>
        </div>

        <div>
          <label class="text-gray-500 text-xs block mb-2 pl-1">ID</label>
          <div class="flex items-center h-12 px-4 bg-[#161823] rounded-lg text-gray-400 text-base border border-transparent">
            @{{ userStore.userInfo?.username }}
          </div>
        </div>

        <div>
          <label class="text-gray-500 text-xs block mb-2 pl-1">邮箱</label>
          <a-input 
            v-model:value="form.email" 
            placeholder="请输入邮箱"
            class="!bg-[#161823] !border-none !rounded-lg !text-white !h-12 !pl-4 placeholder:!text-white/30 hover:!bg-[#1f212e] focus:!bg-[#1f212e] transition-colors"
          >
            <template #prefix>
              <mail-outlined class="text-gray-500 mr-2" />
            </template>
          </a-input>
        </div>

        <div>
          <label class="text-gray-500 text-xs block mb-2 pl-1">简介</label>
          <a-textarea 
            v-model:value="form.bio" 
            placeholder="介绍一下自己吧..."
            :rows="4"
            :maxlength="200"
            show-count
            class="!bg-[#161823] !border-none !rounded-lg !text-white !text-base !p-4 placeholder:!text-white/30 hover:!bg-[#1f212e] focus:!bg-[#1f212e] transition-colors resize-none"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { userApi, uploadApi } from '../api'
import { useUserStore } from '../stores/user'
import { message } from 'ant-design-vue'
import { DEFAULT_AVATAR, resolveAvatarUrl } from '@/utils/constants'
import { 
  ArrowLeftOutlined, 
  CameraOutlined, 
  LoadingOutlined,
  UserOutlined,
  MailOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const form = ref({
  name: '',
  email: '',
  avatar: '',
  bio: ''
})

const saving = ref(false)
const uploadingAvatar = ref(false)
const avatarInput = ref(null)

onMounted(async () => {
  try {
    const res = await userApi.getCurrentUser()
    if (res.code === 200) {
      form.value.name = res.data.name || ''
      form.value.email = res.data.email || ''
      form.value.avatar = res.data.avatar || ''
      form.value.bio = res.data.bio || ''
    }
  } catch (e) {
    console.error(e)
  }
})

const triggerAvatarUpload = () => {
  if (!uploadingAvatar.value) {
    avatarInput.value?.click()
  }
}

const handleAvatarChange = async (e) => {
  const file = e.target.files?.[0]
  if (!file) return

  if (file.size > 5 * 1024 * 1024) {
    message.error('图片大小不能超过5MB')
    return
  }

  uploadingAvatar.value = true
  try {
    const res = await uploadApi.uploadAvatar(file)
    if (res.code === 200) {
      form.value.avatar = res.data.url
      message.success('头像上传成功')
    } else {
      message.error(res.message || '上传失败')
    }
  } catch (e) {
    message.error('上传失败')
  } finally {
    uploadingAvatar.value = false
    e.target.value = ''
  }
}

const saveProfile = async () => {
  if (!form.value.name?.trim()) {
    message.error('昵称不能为空')
    return
  }

  saving.value = true
  try {
    const res = await userApi.updateProfile({
      name: form.value.name.trim(),
      email: form.value.email?.trim() || null,
      avatar: form.value.avatar || null,
      bio: form.value.bio?.trim() || null
    })

    if (res.code === 200) {
      userStore.setUserInfo(res.data)
      message.success('保存成功')
      router.back()
    } else {
      message.error(res.message || '保存失败')
    }
  } catch (e) {
    message.error('保存失败')
  } finally {
    saving.value = false
  }
}
</script>
