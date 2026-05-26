<template>
  <div class="min-h-screen bg-black flex items-center justify-center p-4 overflow-hidden relative">
    <!-- Background Glows -->
    <div class="absolute top-1/4 left-1/4 w-96 h-96 bg-[#fe2c55] rounded-full blur-[150px] opacity-20 pointer-events-none animate-pulse"></div>
    <div class="absolute bottom-1/4 right-1/4 w-96 h-96 bg-[#25f4ee] rounded-full blur-[150px] opacity-20 pointer-events-none animate-pulse" style="animation-delay: 1s"></div>

    <div class="text-center z-10 relative">
      <div class="relative inline-block">
        <h1 class="text-[150px] md:text-[200px] font-black leading-none tracking-tighter text-transparent bg-clip-text bg-gradient-to-br from-white to-gray-600 select-none">
          404
        </h1>
        <div class="absolute top-0 left-0 w-full h-full flex items-center justify-center opacity-50 blur-sm pointer-events-none mix-blend-overlay">
          <span class="text-[150px] md:text-[200px] font-black leading-none tracking-tighter text-[#fe2c55] translate-x-1 translate-y-1">404</span>
        </div>
        <div class="absolute top-0 left-0 w-full h-full flex items-center justify-center opacity-50 blur-sm pointer-events-none mix-blend-overlay">
          <span class="text-[150px] md:text-[200px] font-black leading-none tracking-tighter text-[#25f4ee] -translate-x-1 -translate-y-1">404</span>
        </div>
      </div>
      
      <p class="text-2xl text-gray-300 mb-2 font-light">页面好像迷路了</p>
      <p class="text-gray-500 mb-8 text-sm">The page you are looking for does not exist.</p>
      
      <button 
        class="px-8 py-3 bg-[#fe2c55] hover:bg-[#e62548] text-white rounded-full font-medium transition-all duration-300 hover:scale-105 shadow-lg shadow-red-900/30 flex items-center gap-2 mx-auto"
        @click="$router.push('/')"
      >
        <home-filled />
        返回首页
      </button>
    </div>
  </div>
</template>

<script setup>
import { HomeFilled } from '@ant-design/icons-vue'
</script>
