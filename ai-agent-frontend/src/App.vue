<template>
  <el-config-provider>
    <el-container class="layout-container">
      <el-aside width="240px" v-if="token" class="aside-menu">
        <div class="logo-box">
          <el-icon size="24"><Robot /></el-icon>
          <span class="logo-text">AI 客服平台</span>
        </div>
        <el-menu
          :router="true"
          :default-active="$route.path"
          class="custom-menu"
          background-color="#001529"
          text-color="#fff"
          active-text-color="#409eff"
        >
          <el-menu-item index="/chat" v-if="role === 'USER'">
            <el-icon><ChatLineRound /></el-icon>
            <span>智能对话</span>
          </el-menu-item>
          <el-menu-item index="/support" v-if="role === 'SUPPORT' || role === 'ADMIN'">
            <el-icon><Service /></el-icon>
            <span>人工工作台</span>
          </el-menu-item>
          <el-menu-item index="/knowledge" v-if="role === 'ADMIN'">
            <el-icon><Management /></el-icon>
            <span>知识库管理</span>
          </el-menu-item>
          <div class="menu-footer">
            <el-menu-item @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              <span>退出登录</span>
            </el-menu-item>
          </div>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header v-if="token" class="layout-header">
          <div class="header-left">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item>首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentRouteName }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-tag type="success">{{ roleName }}</el-tag>
          </div>
        </el-header>
        <el-main class="layout-main">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </el-config-provider>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();
const token = ref(localStorage.getItem('token'));
const role = ref(localStorage.getItem('role'));

const roleName = computed(() => {
  const map = { 'ADMIN': '管理员', 'SUPPORT': '客服', 'USER': '普通用户' };
  return map[role.value] || '访客';
});

const currentRouteName = computed(() => {
  const map = { '/chat': '智能对话', '/support': '人工工作台', '/knowledge': '知识库管理', '/login': '登录' };
  return map[route.path] || '未定义';
});

const handleLogout = () => {
  localStorage.clear();
  token.value = null;
  router.push('/login');
};

watch(() => route.path, () => {
  token.value = localStorage.getItem('token');
  role.value = localStorage.getItem('role');
});
</script>

<style>
body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial;
}
.layout-container {
  height: 100vh;
  background-color: #f5f7fa;
}
.aside-menu {
  background-color: #001529;
  display: flex;
  flex-direction: column;
}
.logo-box {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  gap: 10px;
  border-bottom: 1px solid #002140;
}
.logo-text {
  font-size: 18px;
  font-weight: bold;
}
.custom-menu {
  border-right: none;
  flex: 1;
}
.menu-footer {
  margin-top: auto;
  border-top: 1px solid #002140;
}
.layout-header {
  background-color: #fff;
  height: 60px !important;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
}
.layout-main {
  padding: 20px;
  overflow-y: auto;
}
/* 路由切换动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
