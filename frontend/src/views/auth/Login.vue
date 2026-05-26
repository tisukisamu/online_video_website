<template>
  <div class="min-h-screen bg-black flex flex-col relative overflow-hidden">
    <!-- Background Gradient Effect -->
    <div class="absolute top-[-20%] right-[-10%] w-[300px] h-[300px] bg-[#fe2c55] rounded-full blur-[120px] opacity-20 pointer-events-none"></div>
    <div class="absolute bottom-[-10%] left-[-10%] w-[250px] h-[250px] bg-[#25f4ee] rounded-full blur-[100px] opacity-10 pointer-events-none"></div>

    <div class="p-4 absolute top-0 left-0 z-10">
      <close-outlined class="text-white text-2xl cursor-pointer opacity-80 hover:opacity-100 transition-opacity" @click="$router.push('/')" />
    </div>
    
    <div class="flex-1 flex flex-col justify-center px-8 pt-20 pb-8 z-10">
      <h1 class="text-white text-3xl font-bold mb-2 tracking-wide">登录</h1>
      <p class="text-gray-400 text-sm mb-10 tracking-wide">登录后享受更多精彩内容</p>
      
      <a-form :model="formData" :rules="rules" @finish="handleSubmit" class="w-full">
        <a-form-item name="username" class="mb-6">
          <a-input
            v-model:value="formData.username"
            placeholder="请输入手机号/邮箱"
            size="large"
            class="!bg-[#161823] !border-none !rounded-lg !text-white !h-12 !pl-4 placeholder:!text-white/30 hover:!bg-[#1f212e] focus:!bg-[#1f212e] transition-colors"
          >
            <template #prefix>
              <user-outlined class="text-gray-500 mr-2" />
            </template>
          </a-input>
        </a-form-item>
        
        <a-form-item name="password" class="mb-8">
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
        
        <a-form-item>
          <a-button 
            type="primary" 
            html-type="submit" 
            block 
            :loading="loading" 
            class="!bg-[#fe2c55] !border-none !rounded-lg !h-12 !text-base !font-medium hover:!bg-[#e6254c] hover:!shadow-[0_0_15px_rgba(254,44,85,0.4)] transition-all duration-300 w-full"
          >
            登录
          </a-button>
        </a-form-item>
      </a-form>
      
      <div class="text-center mt-8">
        <div class="relative flex items-center justify-center mb-6">
          <div class="absolute w-full h-[1px] bg-white/10"></div>
          <span class="relative bg-black px-3 text-xs text-gray-500">其他登录方式</span>
        </div>
        <div class="flex justify-center gap-10">
          <div class="w-10 h-10 rounded-full bg-[#161823] flex items-center justify-center cursor-pointer hover:bg-[#1f212e] transition-colors group">
            <wechat-outlined class="text-xl text-[#07c160] group-hover:scale-110 transition-transform" />
          </div>
          <div class="w-10 h-10 rounded-full bg-[#161823] flex items-center justify-center cursor-pointer hover:bg-[#1f212e] transition-colors group">
            <qq-outlined class="text-xl text-[#12b7f5] group-hover:scale-110 transition-transform" />
          </div>
          <div class="w-10 h-10 rounded-full bg-[#161823] flex items-center justify-center cursor-pointer hover:bg-[#1f212e] transition-colors group">
            <weibo-outlined class="text-xl text-[#e6162d] group-hover:scale-110 transition-transform" />
          </div>
        </div>
      </div>
    </div>
    
    <div class="text-center p-6 text-sm text-gray-500 z-10">
      <span>还没有账号？</span>
      <a class="text-[#fe2c55] ml-1 cursor-pointer hover:text-[#e6254c] hover:underline" @click="$router.push('/register')">立即注册</a>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { CloseOutlined, WechatOutlined, QqOutlined, WeiboOutlined, UserOutlined, LockOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

const formData = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }]
}

const handleSubmit = async () => {
  loading.value = true
  try {
    const success = await userStore.loginAction(formData)
    if (success) router.push('/')
  } finally {
    loading.value = false
  }
}
</script>
