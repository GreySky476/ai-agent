import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import Chat from '../views/Chat.vue';
import Support from '../views/Support.vue';
import Knowledge from '../views/Knowledge.vue'; // 改为直接导入

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', component: Login },
    { path: '/chat', component: Chat },
    { path: '/support', component: Support },
    { path: '/knowledge', component: Knowledge }
  ]
});

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  if (to.path !== '/login' && !token) {
    next('/login');
  } else {
    next();
  }
});

export default router;
