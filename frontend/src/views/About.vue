<template>
  <div class="min-h-screen bg-black text-white p-4 pt-[80px] flex items-center justify-center">
    <div class="w-full max-w-2xl bg-[#161823] rounded-xl border border-white/5 p-8 shadow-2xl relative overflow-hidden">
      <!-- Background decoration -->
      <div class="absolute -top-20 -right-20 w-60 h-60 bg-[#fe2c55] rounded-full blur-[100px] opacity-10 pointer-events-none"></div>
      <div class="absolute -bottom-20 -left-20 w-60 h-60 bg-[#25f4ee] rounded-full blur-[100px] opacity-10 pointer-events-none"></div>

      <h2 class="text-3xl font-bold mb-2 flex items-center gap-3">
        <span class="bg-gradient-to-r from-[#fe2c55] to-[#f54ea2] text-transparent bg-clip-text">关于我们</span>
      </h2>
      <p class="text-gray-400 mb-8 text-lg">基于 Vue3 + Spring Boot 的现代化短视频分享平台</p>
      
      <div class="grid grid-cols-1 md:grid-cols-2 gap-8 mb-8">
        <div>
          <h3 class="text-white font-medium mb-4 flex items-center gap-2 text-lg">
            <code-outlined /> 技术栈
          </h3>
          <div class="space-y-4">
            <div class="group">
              <span class="text-gray-500 text-sm block mb-1">前端 Framework</span>
              <div class="text-gray-200 bg-white/5 p-3 rounded-lg border border-white/5 group-hover:border-[#fe2c55]/30 transition-colors">
                Vue 3 + Vite + Ant Design Vue + Pinia + UnoCSS
              </div>
            </div>
            <div class="group">
              <span class="text-gray-500 text-sm block mb-1">后端 Backend</span>
              <div class="text-gray-200 bg-white/5 p-3 rounded-lg border border-white/5 group-hover:border-[#fe2c55]/30 transition-colors">
                Spring Boot 3 + Spring Security + JWT + JPA
              </div>
            </div>
            <div class="group">
              <span class="text-gray-500 text-sm block mb-1">数据库 Database</span>
              <div class="text-gray-200 bg-white/5 p-3 rounded-lg border border-white/5 group-hover:border-[#fe2c55]/30 transition-colors">
                MySQL 8.0 + Redis
              </div>
            </div>
          </div>
        </div>

        <div>
          <h3 class="text-white font-medium mb-4 flex items-center gap-2 text-lg">
            <thunderbolt-outlined /> 功能特性
          </h3>
          <ul class="space-y-3">
            <li v-for="(feature, index) in features" :key="index" class="flex items-center gap-3 text-gray-300 p-2 rounded hover:bg-white/5 transition-colors">
              <check-circle-filled class="text-[#25f4ee]" />
              <span>{{ feature }}</span>
            </li>
          </ul>
        </div>
      </div>
      
      <div class="border-t border-white/10 pt-6 text-center text-gray-500 text-sm">
        <p>&copy; {{ new Date().getFullYear() }} BaseWeb Short Video Platform. All rights reserved.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { CheckCircleFilled, CodeOutlined, ThunderboltOutlined } from '@ant-design/icons-vue'

const features = [
  '用户注册登录 / 权限管理',
  '视频上传 / 转码 / 播放',
  '点赞 / 评论 / 收藏 / 转发',
  '用户关注 / 粉丝系统',
  '个性化视频推荐算法',
  '响应式深色主题界面'
]
</script>
