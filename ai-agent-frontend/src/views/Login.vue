<template>
  <div class="login-wrapper">
    <el-card class="login-card">
      <h2>AI 客服系统登录</h2>
      <el-form :model="form" ref="loginForm" label-width="0">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" style="width: 100%">登录</el-button>
        </el-form-item>
        <el-form-item>
          <el-link @click="handleRegister">新用户注册</el-link>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue';
import { useRouter } from 'vue-router';
import request from '../api/request';
import { ElMessage } from 'element-plus';

const router = useRouter();
const form = reactive({
  username: '',
  password: ''
});

const handleLogin = async () => {
  try {
    const res = await request.post('/auth/login', { // 配合 baseURL: '/backend'
      username: form.username,
      password: form.password
    });
    if (res.data.token) {
      localStorage.setItem('token', res.data.token);
      // 解析 Token 中的角色（简单处理）
      try {
        const payload = JSON.parse(atob(res.data.token.split('.')[1]));
        const rawRole = payload.roles && payload.roles.length > 0 ? payload.roles[0] : 'USER';
        localStorage.setItem('role', rawRole.replace('ROLE_', ''));
      } catch (e) {
        localStorage.setItem('role', 'USER');
      }
      ElMessage.success('登录成功');
      router.push('/chat');
    }
  } catch (err) {
    ElMessage.error('登录失败: ' + (err.response?.data?.message || '请检查账号密码'));
  }
};

const handleRegister = async () => {
  try {
    await request.post('/auth/register', form); // 配合 baseURL: '/backend'
    ElMessage.success('注册成功，请重新登录');
  } catch (err) {
    ElMessage.error('注册失败');
  }
};
</script>

<style scoped>
.login-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}
.login-card {
  width: 400px;
  text-align: center;
}
</style>
