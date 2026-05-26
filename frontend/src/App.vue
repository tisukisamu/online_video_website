<template>
  <a-config-provider :theme="themeConfig">
    <div class="app-container">
      <router-view />
    </div>
  </a-config-provider>
</template>

<script setup>
import { theme } from 'ant-design-vue'

const themeConfig = {
  token: {
    colorPrimary: '#fe2c55',
    colorBgBase: '#000000',
    colorBgContainer: '#161823',
    colorBgElevated: '#1f212e',
    colorBorder: 'rgba(255, 255, 255, 0.1)',
    colorText: '#ffffff',
    colorTextSecondary: 'rgba(255, 255, 255, 0.7)',
    borderRadius: 4,
  },
  algorithm: theme.darkAlgorithm,
}
</script>

<style>
.app-container {
  min-height: 100vh;
  background: #000000;
  color: #ffffff;
}
</style>
