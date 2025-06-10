<template>
  <div class="app-container">
    <!-- 根据路由和登录状态判断是否显示导航 -->
    <side-nav v-if="showNav" class="side-nav" :is-collapse="isCollapse" />
    
    <div class="main-container" :class="{ 'with-nav': showNav }">
      <!-- 添加顶部导航栏 -->
      <header-bar 
        v-if="showNav" 
        class="header-bar" 
        @collapse-change="handleCollapse"
      />
      
      <div class="main-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
// import { useUserStore } from '@/stores/user'
import SideNav from '@/components/SideNav.vue'
import HeaderBar from '@/components/layout/HeaderBar.vue'

const route = useRoute()
// const userStore = useUserStore()
const isCollapse = ref(false)

// 在登录页面和未登录状态下不显示导航
const showNav = computed(() => {
  const isLoginPage = route.name === 'Login'
  const isRegisterPage = route.name === 'Register'
  return !isLoginPage && !isRegisterPage
})

// 处理折叠状态变化
const handleCollapse = (collapsed) => {
  isCollapse.value = collapsed
}
</script>

<style>
.app-container {
  display: flex;
  min-height: 100vh;
}

.side-nav {
  position: fixed;
  left: 0;
  top: 0;
  height: 100vh;
  z-index: 1000;
  background-color: #fff;
  border-right: 1px solid #dcdfe6;
  transition: width 0.3s;
  width: 200px;
}

.side-nav.is-collapse {
  width: 64px;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  transition: margin-left 0.3s;
  min-height: 100vh;
}

.main-container.with-nav {
  margin-left: 200px;
}

.main-container.with-nav.is-collapse {
  margin-left: 64px;
}

.header-bar {
  position: fixed;
  top: 0;
  right: 0;
  left: 200px;
  height: 60px;
  z-index: 999;
  transition: left 0.3s;
}

.header-bar.is-collapse {
  left: 64px;
}

/* Default style for .main-content (e.g., for login page) */
.main-content {
  flex: 1;
  /* display: flex; */ /* Optional: if children need flex context from main-content */
  /* flex-direction: column; */ /* Optional */
}

/* Styles for .main-content when part of the normal navigation layout */
.main-container.with-nav .main-content {
  margin-top: 60px;
  padding: 20px;
  background-color: #f5f7fa;
}

/* 重置默认样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 
    'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background-color: #f5f7fa;
}
</style>

