<template>
  <el-container class="support-wrapper">
    <el-aside width="250px" class="user-list">
      <h3>在线用户</h3>
      <el-menu @select="handleSelectUser">
        <el-menu-item v-for="user in activeUsers" :key="user.id" :index="user.id">
          {{ user.name }}
          <el-tag size="small" v-if="user.hasNew" type="danger">NEW</el-tag>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-main class="chat-main">
      <div v-if="selectedUser">
        <h3>正在处理用户：{{ selectedUser }}</h3>
        <div class="message-list">
          <!-- 模拟对话列表，实际需从后端获取该用户的历史 -->
          <div class="message assistant">
            <div class="content">您好，刚才 AI 助手已经为您记录了问题，我现在来接手。</div>
          </div>
        </div>
        <div class="support-input">
          <el-input v-model="supportInput" placeholder="输入人工回复..." @keyup.enter="handleSend" />
          <el-button type="success" @click="handleSend">人工回复</el-button>
        </div>
      </div>
      <div v-else class="empty-state">
        请从左侧选择一个需要人工介入的用户
      </div>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { Client } from '@stomp/stompjs';
import { ElNotification } from 'element-plus';

const activeUsers = ref([]);
const selectedUser = ref(null);
const supportInput = ref('');
let stompClient = null;

const handleSelectUser = (id) => {
  selectedUser.value = id;
};

const handleSend = () => {
  supportInput.value = '';
};

onMounted(() => {
  // 使用更现代的 @stomp/stompjs Client
  stompClient = new Client({
    brokerURL: 'ws://localhost:8080/ws-notification/websocket', // 注意：SockJS 降级通常需要 /websocket 后缀
    onConnect: () => {
      stompClient.subscribe('/topic/support', (notification) => {
        const data = JSON.parse(notification.body);
        ElNotification({
          title: '新的人工求助',
          message: data.message,
          type: 'warning'
        });
        if (!activeUsers.value.find(u => u.id === data.sessionId)) {
          activeUsers.value.push({ id: data.sessionId, name: `用户 ${data.sessionId}`, hasNew: true });
        }
      });
    },
    onStompError: (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
    }
  });

  stompClient.activate();
});

onUnmounted(() => {
  if (stompClient) stompClient.deactivate();
});
</script>

<style scoped>
.support-wrapper { height: 100%; border: 1px solid #eee; }
.user-list { border-right: 1px solid #eee; padding: 20px; background-color: #fafafa; }
.chat-main { padding: 20px; display: flex; flex-direction: column; }
.message-list { flex: 1; min-height: 400px; padding: 10px; border: 1px solid #eee; margin-top: 10px; background-color: #fff; }
.support-input { margin-top: 20px; display: flex; gap: 10px; }
.empty-state { text-align: center; color: #999; margin-top: 200px; }
</style>
