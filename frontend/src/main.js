import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import Antd, { message, notification } from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import 'uno.css'
import './style.css'

const app = createApp(App)
const pinia = createPinia()

// Vue 全局错误处理
app.config.errorHandler = (err, vm, info) => {
  console.error('[Vue Error]', err)
  console.error('[Component]', vm?.$options?.name || 'AnonymousComponent')
  console.error('[Info]', info)
  
  if (err?.isAxiosError || err?.name === 'AxiosError' || String(err?.message || '').startsWith('Request failed with status code')) {
    return
  }

  notification.error({
    message: '应用错误',
    description: err.message || '发生了未知错误',
    duration: 5,
    placement: 'topRight'
  })
}

// 全局警告处理
app.config.warnHandler = (msg, vm, trace) => {
  console.warn('[Vue Warning]', msg)
  console.warn('[Trace]', trace)
}

// 全局未捕获的 Promise 错误
window.addEventListener('unhandledrejection', (event) => {
  event.preventDefault()
  console.error('[Unhandled Promise Rejection]', event.reason)
  
  if (event.reason?.isAxiosError || event.reason?.name === 'AxiosError' || String(event.reason?.message || '').startsWith('Request failed with status code')) {
    return
  }

  if (event.reason?.message) {
    message.error(`异步错误: ${event.reason.message}`)
  }
})

// 全局 JS 错误
window.addEventListener('error', (event) => {
  console.error('[Global Error]', event.error)
  
  // 资源加载错误
  if (event.target && (event.target.tagName === 'IMG' || event.target.tagName === 'SCRIPT' || event.target.tagName === 'LINK')) {
    message.error('资源加载失败，请刷新页面重试')
    return
  }
  
  message.error('程序发生错误，请刷新页面重试')
})

app.use(pinia)
app.use(router)
app.use(Antd)
app.mount('#app')
