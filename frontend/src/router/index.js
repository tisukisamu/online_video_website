import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'
import MainLayout from '../components/MainLayout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/auth/Login.vue'),
    meta: { public: true, title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/auth/Register.vue'),
    meta: { public: true, title: '注册' }
  },
  {
    path: '/',
    component: MainLayout,
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('../views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'recommend',
        name: 'Recommend',
        component: () => import('../views/Recommend.vue'),
        meta: { title: '推荐' }
      },
      {
        path: 'follow',
        name: 'Follow',
        component: () => import('../views/Follow.vue'),
        meta: { requireAuth: true, title: '关注' }
      },
      {
        path: 'search',
        name: 'Search',
        component: () => import('../views/Search.vue'),
        meta: { title: '搜索' }
      },
      {
        path: 'video/:id',
        name: 'VideoDetail',
        component: () => import('../views/VideoDetail.vue'),
        meta: { title: '视频详情' }
      },
      {
        path: 'user/:id',
        name: 'UserProfile',
        component: () => import('../views/UserProfile.vue'),
        meta: { title: '用户主页' }
      },
      {
        path: 'upload',
        name: 'VideoUpload',
        component: () => import('../views/VideoUpload.vue'),
        meta: { requireAuth: true, title: '发布视频' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue'),
        meta: { requireAuth: true, title: '个人中心' }
      },
      {
        path: 'profile/edit',
        name: 'ProfileEdit',
        component: () => import('../views/ProfileEdit.vue'),
        meta: { requireAuth: true, title: '编辑资料' }
      },
      {
        path: 'favorites',
        name: 'Favorites',
        component: () => import('../views/Favorites.vue'),
        meta: { requireAuth: true, title: '我的收藏' }
      },
      {
        path: 'my/videos',
        name: 'MyVideos',
        component: () => import('../views/MyVideos.vue'),
        meta: { requireAuth: true, title: '我的作品' }
      },
      {
        path: 'following/:id?',
        name: 'FollowingList',
        component: () => import('../views/FollowingList.vue'),
        meta: { title: '关注列表' }
      },
      {
        path: 'followers/:id?',
        name: 'FollowersList',
        component: () => import('../views/FollowersList.vue'),
        meta: { title: '粉丝列表' }
      },
      {
        path: 'messages',
        name: 'Messages',
        component: () => import('../views/Messages.vue'),
        meta: { requireAuth: true, title: '消息' }
      },
      {
        path: 'messages/:id',
        name: 'DirectMessage',
        component: () => import('../views/DirectMessage.vue'),
        meta: { requireAuth: true, title: '私聊' }
      },
      {
        path: 'admin',
        name: 'Admin',
        component: () => import('../views/admin/Dashboard.vue'),
        meta: { requireAuth: true, requireAdmin: true, title: '管理后台' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(_to, _from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    }
    return { top: 0 }
  }
})

router.beforeEach(async (to, _from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 短视频平台` : '短视频平台'
  
  const userStore = useUserStore()
  
  if (!userStore.userInfo && userStore.token) {
    await userStore.init()
  }
  
  if (to.meta.public) {
    if (userStore.isLoggedIn) {
      next('/')
    } else {
      next()
    }
    return
  }
  
  if (to.meta.requireAuth) {
    if (!userStore.isLoggedIn) {
      next({ path: '/login', query: { redirect: to.fullPath } })
      return
    }
    
    if (!userStore.isActive) {
      next('/login')
      return
    }
    
    if (to.meta.requireAdmin && !userStore.isAdmin) {
      next('/')
      return
    }
  }
  
  next()
})

export default router
