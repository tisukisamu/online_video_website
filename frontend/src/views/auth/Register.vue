<template>
  <div class="min-h-screen bg-black flex flex-col relative overflow-hidden">
    <!-- Background Gradient Effect -->
    <div class="absolute top-[-20%] right-[-10%] w-[300px] h-[300px] bg-[#fe2c55] rounded-full blur-[120px] opacity-20 pointer-events-none"></div>
    <div class="absolute bottom-[-10%] left-[-10%] w-[250px] h-[250px] bg-[#25f4ee] rounded-full blur-[100px] opacity-10 pointer-events-none"></div>

    <div class="p-4 absolute top-0 left-0 z-10">
      <close-outlined class="text-white text-2xl cursor-pointer opacity-80 hover:opacity-100 transition-opacity" @click="$router.push('/')" />
    </div>
    
    <div class="flex-1 flex flex-col justify-center px-8 pt-20 pb-8 z-10">
      <h1 class="text-white text-3xl font-bold mb-2 tracking-wide">注册</h1>
      <p class="text-gray-400 text-sm mb-10 tracking-wide">注册账号，开启精彩之旅</p>
      
      <a-form :model="formData" :rules="rules" @finish="handleSubmit" class="w-full">
        <a-form-item name="username" class="mb-6">
          <a-input
            v-model:value="formData.username"
            placeholder="请输入用户名"
            size="large"
            class="!bg-[#161823] !border-none !rounded-lg !text-white !h-12 !pl-4 placeholder:!text-white/30 hover:!bg-[#1f212e] focus:!bg-[#1f212e] transition-colors"
          >
            <template #prefix>
              <user-outlined class="text-gray-500 mr-2" />
            </template>
          </a-input>
        </a-form-item>
        
        <a-form-item name="password" class="mb-6">
          <a-input-password
            v-model:value="formData.password"
            placeholder="请输入密码"
            size="large"
            class="!bg-[#161823] !border-none !rounded-lg !text-white !h-12 !pl-4 placeholder:!text-white/30 hover:!bg-[#1f212e] focus:!bg-[#1f212e] transition-colors"
          >
            <template #prefix>
              <lock-outlined class="text-gray-500 mr-2" />
            </template>
          </a-input-password>
        </a-form-item>
        
        <a-form-item name="confirmPassword" class="mb-6">
          <a-input-password
            v-model:value="formData.confirmPassword"
            placeholder="请确认密码"
            size="large"
            class="!bg-[#161823] !border-none !rounded-lg !text-white !h-12 !pl-4 placeholder:!text-white/30 hover:!bg-[#1f212e] focus:!bg-[#1f212e] transition-colors"
          >
            <template #prefix>
              <lock-outlined class="text-gray-500 mr-2" />
            </template>
          </a-input-password>
        </a-form-item>
        
        <a-form-item name="name" class="mb-8">
          <a-input
            v-model:value="formData.name"
            placeholder="请输入昵称"
            size="large"
            class="!bg-[#161823] !border-none !rounded-lg !text-white !h-12 !pl-4 placeholder:!text-white/30 hover:!bg-[#1f212e] focus:!bg-[#1f212e] transition-colors"
          >
            <template #prefix>
              <smile-outlined class="text-gray-500 mr-2" />
            </template>
          </a-input>
        </a-form-item>
        
        <a-form-item>
          <a-button 
            type="primary" 
            html-type="submit" 
            block 
            :loading="loading" 
            class="!bg-[#fe2c55] !border-none !rounded-lg !h-12 !text-base !font-medium hover:!bg-[#e6254c] hover:!shadow-[0_0_15px_rgba(254,44,85,0.4)] transition-all duration-300 w-full"
          >
            注册
          </a-button>
        </a-form-item>
      </a-form>
    </div>
    
    <div class="text-center p-6 text-sm text-gray-500 z-10">
      <span>已有账号？</span>
      <a class="text-[#fe2c55] ml-1 cursor-pointer hover:text-[#e6254c] hover:underline" @click="$router.push('/login')">立即登录</a>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { CloseOutlined, UserOutlined, LockOutlined, SmileOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

const formData = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  name: ''
})

const validateConfirmPassword = async (_rule, value) => {
  if (value !== formData.password) throw new Error('两次密码不一致')
}

const rules = {
  username: [
    { required: true, message: '请输入用户名' },
    { min: 3, max: 20, message: '用户名3-20个字符' }
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, message: '密码至少6个字符' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码' },
    { validator: validateConfirmPassword }
  ],
  name: [{ required: true, message: '请输入昵称' }]
}

const handleSubmit = async () => {
  loading.value = true
  try {
    const { confirmPassword, ...registerData } = formData
    const success = await userStore.registerAction(registerData)
    if (success) router.push('/')
  } finally {
    loading.value = false
  }
}
</script>
