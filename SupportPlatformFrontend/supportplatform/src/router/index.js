import { createRouter, createWebHistory } from 'vue-router'
// import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    redirect: '/task-hall'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/UserLogin.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/UserRegister.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/UserHome.vue'),
    meta: { requiresAuth: true, title: '首页' }
  },
  {
    path: '/task-hall',
    name: 'TaskHall',
    component: () => import('@/views/TaskHall.vue'),
    meta: { title: '任务大厅' }
  },
  {
    path: '/task/publish',
    name: 'PublishTask',
    component: () => import('@/views/task/PublishTask.vue'),
    meta: { requiresAuth: true, title: '发布任务' }
  },
  {
    path: '/my-published-tasks',
    name: 'MyPublishedTasks',
    component: () => import('@/views/MyPublishedTasks.vue'),
    meta: { requiresAuth: true, title: '我发布的任务' }
  },
  {
    path: '/my-received-tasks',
    name: 'MyReceivedTasks',
    component: () => import('@/views/MyReceivedTasks.vue'),
    meta: { requiresAuth: true, title: '我接受的任务' }
  },
  {
    path: '/my-reports',
    name: 'MyReports',
    component: () => import('@/views/MyReports.vue'),
    meta: { requiresAuth: true, title: '我的举报' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/ProfileView.vue'),
    meta: { requiresAuth: true, title: '个人中心' }
  },
  {
    path: '/admin-profile',
    name: 'AdminProfile',
    component: () => import('@/views/AdminProfileView.vue'),
    meta: { requiresAuth: true, title: '管理员中心' }
  },
  {
    path: '/admin/user-management',
    name: 'UserManagement',
    component: () => import('@/views/UserManagement.vue'),
    meta: { requiresAuth: true, title: '用户管理' }
  },
  {
    path: '/admin/reports',
    name: 'AdminReports',
    component: () => import('@/views/admin/AdminReports.vue'),
    meta: { requiresAuth: true, isAdmin: true, title: '举报管理' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // const userStore = useUserStore()
  const token = localStorage.getItem('token')

  // 不需要认证的路由直接通过
  if (!to.meta.requiresAuth) {
    next()
    return
  }

  // 需要认证但没有token，跳转到登录页
  if (!token) {
    next('/login')
    return
  }

  next()
})

export default router 